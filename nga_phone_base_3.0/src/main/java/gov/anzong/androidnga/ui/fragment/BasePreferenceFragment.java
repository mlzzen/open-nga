package gov.anzong.androidnga.ui.fragment;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    protected void mapping(PreferenceGroup group) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference preference = group.getPreference(i);
            preference.setIconSpaceReserved(false);
            if (preference instanceof PreferenceGroup) {
                mapping((PreferenceGroup) preference);
            } else {
                preference.setOnPreferenceChangeListener(this);
            }
        }
    }

    protected void configPreference(String key, Preference.OnPreferenceChangeListener listener) {
        Preference preference = findPreference(key);
        if (preference != null) {
            preference.setOnPreferenceChangeListener(listener);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    protected void setTitle(CharSequence title) {
        if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }
}
