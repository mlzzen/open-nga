package sp.phone.ui.fragment;

import android.Manifest;
import android.os.Bundle;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.logger.DebugLogger;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.base.logger.ReleaseLogger;
import gov.anzong.androidnga.base.logger.RemoteDebugLogger;
import gov.anzong.androidnga.base.util.PermissionUtils;
import gov.anzong.androidnga.ui.fragment.BasePreferenceFragment;

public class SettingsLabFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_lab);
        mapping(getPreferenceScreen());
        configPreference(getString(R.string.pref_debug_switch), (preference, newValue) -> {
            if (newValue.equals(Boolean.TRUE)) {
                if (!PermissionUtils.hasPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    PermissionUtils.request(SettingsLabFragment.this, null, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return false;
                }
            }
            updateDebugSwitch(Boolean.parseBoolean(newValue.toString()));
            return true;
        });
    }

    private void updateDebugSwitch(boolean newValue) {
        if (newValue) {
            Logger.getInstance().setLogger(new RemoteDebugLogger());
        } else if (BuildConfig.DEBUG) {
            Logger.getInstance().setLogger(new DebugLogger());
        } else {
            Logger.getInstance().setLogger(new ReleaseLogger());
        }
    }

    @Override
    public void onResume() {
        setTitle("实验室");
        super.onResume();
    }
}
