package sp.phone.ui.fragment;

import android.Manifest;
import android.os.Bundle;

import androidx.preference.SwitchPreference;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.base.util.PermissionUtils;
import gov.anzong.androidnga.ui.fragment.BasePreferenceFragment;

public class SettingsLabFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_lab);
        mapping(getPreferenceScreen());
        initDebugPreference();
    }

    private void initDebugPreference() {
        SwitchPreference preference = findPreference(getString(R.string.pref_local_debug_switch));
        if (preference == null) {
            return;
        }
        preference.setChecked(Logger.getInstance().isLocalDebug());
        preference.setOnPreferenceChangeListener((preference1, newValue1) -> {
            if (newValue1.equals(Boolean.TRUE)) {
                if (!PermissionUtils.hasPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    PermissionUtils.request(SettingsLabFragment.this, null, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return false;
                }
            }
            Logger.getInstance().setLocalDebug(Boolean.TRUE.equals(newValue1));
            Logger.getInstance().updateLogger();
            return true;
        });


    }

    @Override
    public void onResume() {
        setTitle("实验室");
        super.onResume();
    }
}
