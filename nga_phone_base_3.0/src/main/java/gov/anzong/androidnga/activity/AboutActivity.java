package gov.anzong.androidnga.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.debug.Debugger;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.VersionUpgradeDialogFragment;
import sp.phone.util.FunctionUtils;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance().applyAboutTheme(this);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        return new MaterialAboutList(buildAppCard(), buildDevelopCard());
    }

    private MaterialAboutCard buildAppCard() {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("NGA客户端-liang魔改版")
                .icon(R.mipmap.ic_launcher)
                .setOnClickAction(() -> new VersionUpgradeDialogFragment().show(getSupportFragmentManager(), null))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("版本")
                .subText(BuildConfig.VERSION_NAME)
                .icon(R.drawable.ic_about)
                .setOnClickAction(() -> {
                    try {
                        String url = "market://details?id=" + getPackageName();
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://www.coolapk.com/apk/gov.anzong.androidnga");
                    }
                })
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("代码")
                .subText("[@mlzzen]")
                .setOnLongClickAction(Debugger::toggleDebugMode)
                .icon(R.drawable.ic_code)
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("License")
                .subText("GNU GPL v2,开放源代码许可")
                .setOnClickAction(() -> {
                    Intent intent = new Intent(AboutActivity.this, WebViewerActivity.class);
                    intent.putExtra("path", "file:///android_asset/OSLICENSE.TXT");
                    startActivity(intent);

                })
                .icon(R.drawable.ic_update_24dp)
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Github检测更新")
                .setOnClickAction(() -> FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://github.com/mlzzen/open-nga/releases"))
                .icon(R.drawable.ic_github)
                .build());
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Gitee检测更新")
                .setOnClickAction(() -> FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://gitee.com/mlzzen/open-nga/releases"))
                .icon(R.drawable.ic_gitee)
                .build());
        
                builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Github")
                .subText("bug & 建议")
                .setOnClickAction(() -> FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://github.com/mlzzen/open-nga/issues"))
                .icon(R.drawable.ic_github)
                .build());
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Gitee")
                .subText("bug & 建议")
                .setOnClickAction(() -> FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://gitee.com/mlzzen/open-nga/issues"))
                .icon(R.drawable.ic_gitee)
                .build());

        return builder.build();
    }

    private MaterialAboutCard buildDevelopCard() {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("原来的NGA开源版");

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Github")
                .setOnClickAction(() -> FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://github.com/Justwen/NGA-CLIENT-VER-OPEN-SOURCE"))
                .icon(R.drawable.ic_github)
                .build());

        return builder.build();
    }

    @Nullable
    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.title_about);
    }
}
