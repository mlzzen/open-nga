package sp.phone.common;

import android.content.SharedPreferences;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;

/**
 * @author yangyihang
 */
public class VersionUpgradeHelper {

    /**
     * 标记「NGA图片域名」选项顺序是否已完成调整
     */
    private static final String KEY_IMAGE_DOMAIN_REORDERED = "nga_image_domain_reordered";

    public static void upgrade() {
        upgradeBookmarkBoards();
        upgradeSettings();
        upgradeImageDomain();
    }

    private static void upgradeBookmarkBoards() {
        SharedPreferences sp = ContextUtils.getSharedPreferences(PreferenceKey.PERFERENCE);
        if (sp.contains(PreferenceKey.BOOKMARK_BOARD)) {
            String data = sp.getString(PreferenceKey.BOOKMARK_BOARD, "");
            sp.edit().remove(PreferenceKey.BOOKMARK_BOARD).apply();
            PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, data);
        }
    }

    private static void upgradeSettings() {
        SharedPreferences sp = ContextUtils.getSharedPreferences(PreferenceKey.PERFERENCE);
        SharedPreferences.Editor editor = sp.edit();
        if (sp.contains(PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI)) {
            boolean value = sp.getBoolean(PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI, true);
            String newValue = value ? "0" : "2";
            editor.putString(ContextUtils.getString(R.string.pref_load_avatar_strategy), newValue)
                    .remove(PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI)
                    .apply();
        }

        if (sp.contains(PreferenceKey.DOWNLOAD_IMG_NO_WIFI)) {
            boolean value = sp.getBoolean(PreferenceKey.DOWNLOAD_IMG_NO_WIFI, true);
            String newValue = value ? "0" : "2";
            editor.putString(ContextUtils.getString(R.string.pref_load_pic_strategy), newValue)
                    .remove(PreferenceKey.DOWNLOAD_IMG_NO_WIFI)
                    .apply();
        }
    }

    /**
     * 「NGA图片域名」中 img.nga.cn 与 img.nga.178.com 调换了位置（img.nga.cn 变为默认项，即新值 0）。
     * 这里把旧值 1（原本表示 img.nga.cn）改写为新值 0，避免老用户原本的选择被换成 img.nga.178.com。
     */
    private static void upgradeImageDomain() {
        SharedPreferences sp = ContextUtils.getSharedPreferences(PreferenceKey.PERFERENCE);
        if (sp.contains(KEY_IMAGE_DOMAIN_REORDERED)) {
            return;
        }
        SharedPreferences.Editor editor = sp.edit().putBoolean(KEY_IMAGE_DOMAIN_REORDERED, true);
        if ("1".equals(sp.getString(PreferenceKey.KEY_NGA_IMAGE_DOMAIN, null))) {
            editor.putString(PreferenceKey.KEY_NGA_IMAGE_DOMAIN, "0");
        }
        editor.apply();
    }

}
