package sp.phone.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.activity.LauncherSubActivity;
import gov.anzong.androidnga.activity.SettingsActivity;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.ui.fragment.BasePreferenceFragment;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.presenter.BoardPresenter;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.AlertDialogFragment;

public class SettingsFragment extends BasePreferenceFragment implements Preference.OnPreferenceChangeListener {
    private BoardPresenter boardPresenter;

    private static final int REQUEST_CODE_IMPORT_JSON = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("boardPresenter", "onCreate: ");
        boardPresenter = new BoardPresenter();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        mapping(getPreferenceScreen());
        configPreference();

        // setPreferencesFromResource(R.xml.settings, rootKey);
    }

    private void configPreference() {
        findPreference(PreferenceKey.NIGHT_MODE).setEnabled(!ThemeManager.getInstance().isNightModeFollowSystem());
        findPreference(PreferenceKey.MATERIAL_THEME).setEnabled(!ThemeManager.getInstance().isNightMode());

        findPreference(PreferenceKey.KEY_CLEAR_CACHE).setOnPreferenceClickListener(preference -> {
            showClearCacheDialog();
            return true;
        });

        findPreference(PreferenceKey.KEY_CLEAR_DRAFT).setOnPreferenceClickListener(preference -> {
            showClearDraftDialog();
            return true;
        });

        findPreference(PreferenceKey.KEY_CLEAR_FAVORITE).setOnPreferenceClickListener(preference -> {
            showClearFavoriteDialog();
            return true;
        });

        findPreference(PreferenceKey.KEY_EXPORT_SETTINGS).setOnPreferenceClickListener(preference -> {
            exportSharedPreferences();
            return true;
        });

        findPreference(PreferenceKey.KEY_IMPORT_SETTINGS).setOnPreferenceClickListener(preference -> {
            // 启动文件选择器
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, REQUEST_CODE_IMPORT_JSON);
            return true;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMPORT_JSON && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData(); // 获取用户选择的文件 URI

                // 获取持久化权限
                requireContext().getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                // 导入 SharedPreferences
                importSharedPreferencesFromUri(uri);
            }
        }
    }

    private void showClearCacheDialog() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.create("确认要清除缓存吗？");
        dialogFragment.setPositiveClickListener((dialog, which) -> clearCache());
        dialogFragment.show(((BaseActivity)getActivity()).getSupportFragmentManager(),"clear_cache");
    }

    private void showClearDraftDialog() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.create("确认要清除草稿吗？");
        dialogFragment.setPositiveClickListener((dialog, which) -> clearDraft());
        dialogFragment.show(((BaseActivity)getActivity()).getSupportFragmentManager(),"clear_draft");
    }

    private void showClearFavoriteDialog() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.create("确认要清除我的收藏吗？");
        dialogFragment.setPositiveClickListener((dialog, which) -> {
            boardPresenter.clearRecentBoards();
            ToastUtils.success("收藏清除成功");
        });
        dialogFragment.show(((BaseActivity)getActivity()).getSupportFragmentManager(),"clear_favorite");
    }

    private void clearCache() {
        ThreadUtils.postOnSubThread(() -> {
            // 清除glide缓存
            Glide.get(ContextUtils.getContext()).clearDiskCache();
            // 清除avatar数据
            UserManagerImpl.getInstance().clearAvatarUrl();
            // 清除之前的使用过的awp缓存数据
            try {
                FileUtils.deleteDirectory(ContextUtils.getContext().getDir("awp", Context.MODE_PRIVATE));
                FileUtils.deleteDirectory(ContextUtils.getContext().getDir("sogou_webview", Context.MODE_PRIVATE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ToastUtils.success("缓存清除成功");
    }

    private void clearDraft() {
        // 清除草稿
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().remove(PreferenceKey.PREF_DRAFT_TOPIC).apply();
        prefs.edit().remove(PreferenceKey.PREF_DRAFT_REPLY).apply();
        ToastUtils.success("草稿清除成功");
    }

    private void exportSharedPreferences() {
        // 创建一个 JSON 对象
        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences mPrefs = android.preference.PreferenceManager.getDefaultSharedPreferences(ContextUtils.getContext());
            // 获取 BLACK_LIST 和 BOOKMARK_BOARD 的值
            String blackList = mPrefs.getString(PreferenceKey.BLACK_LIST, null);

            // 将值放入 JSON 对象
            if (blackList != null) {
                jsonObject.put(PreferenceKey.BLACK_LIST, blackList);
            }

            // 检查外部存储是否可用
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 获取下载目录路径
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File exportFile = new File(downloadDir, "open-nga-settings.json");

                if (exportFile.exists()) {
                    if (exportFile.delete()) {
                        Log.d("File", "Existing file deleted.");
                    } else {
                        ToastUtils.error("导入失败");
                        Log.e("File", "Failed to delete existing file.");
                        return; // 如果删除失败，直接返回
                    }
                }

                // 将 JSON 对象写入下载目录
                FileOutputStream fos = new FileOutputStream(exportFile);
                fos.write(jsonObject.toString().getBytes());
                fos.close();
                ToastUtils.success("导出成功");
            }
        } catch (IOException | JSONException e) {
            ToastUtils.error("导入失败");
            e.printStackTrace();
        }
    }

    private void importSharedPreferencesFromUri(Uri uri) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getContext());
        SharedPreferences.Editor editor = prefs.edit();

        try {
            // 从 URI 读取文件内容
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();

            // 解析 JSON 数据
            JSONObject jsonObject = new JSONObject(jsonString);

            // 导入 BLACK_LIST
            if (jsonObject.has(PreferenceKey.BLACK_LIST)) {
                editor.putString(PreferenceKey.BLACK_LIST, jsonObject.getString(PreferenceKey.BLACK_LIST));
            }

            // 提交更改
            editor.apply();
            ToastUtils.success("导入成功");
            Log.d("Import", "SharedPreferences imported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Import", "Failed to import SharedPreferences: " + e.getMessage());
        }
    }



    @Override
    public void onResume() {
        getActivity().setTitle(R.string.menu_setting);
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference instanceof ListPreference) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        String key = preference.getKey();
        switch (key) {
            case PreferenceKey.NIGHT_MODE:
                SettingsActivity.sRecreated = true;
                break;
            case PreferenceKey.KEY_NIGHT_MODE_FOLLOW_SYSTEM:
                findPreference(PreferenceKey.NIGHT_MODE).setEnabled(Boolean.FALSE.equals(newValue));
                SettingsActivity.sRecreated = true;
                break;
            case PreferenceKey.MATERIAL_THEME:
                SettingsActivity.sRecreated = true;
                ThreadUtils.postOnMainThreadDelay(() -> {
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                }, 200);
                break;
            default:
                break;

        }
        return true;
    }

    private void setFullScreen(boolean fullScreen) {
        int flag;
        if (fullScreen) {
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getActivity().getWindow().addFlags(flag);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            Intent intent = new Intent(getActivity(), LauncherSubActivity.class);
            intent.putExtra("fragment", preference.getFragment());
            startActivity(intent);
            return true;
        }
        switch (preference.getKey()) {
            case PreferenceKey.ADJUST_SIZE:
            case PreferenceKey.PREF_USER:
            case PreferenceKey.PREF_BLACK_LIST:
            case PreferenceKey.PREF_NOTES_LIST:
            case "pref_keyword":
                Intent intent = new Intent(getActivity(), LauncherSubActivity.class);
                intent.putExtra("fragment", preference.getFragment());
                startActivity(intent);
                break;
            default:
                return super.onPreferenceTreeClick(preference);

        }
        return true;
    }

}
