package com.stypox.mastercom_workbook.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.stypox.mastercom_workbook.R;

import static com.stypox.mastercom_workbook.util.ShareUtils.openUrlInBrowser;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        Preference sourceCode = findPreference(getString(R.string.key_source_code));
        Preference reportBug = findPreference(getString(R.string.key_report_bug));
        assert sourceCode != null;
        assert reportBug != null;

        sourceCode.setOnPreferenceClickListener(preference -> {
            openUrlInBrowser(requireContext(), getString(R.string.settings_source_code_url));
            return true;
        });
        reportBug.setOnPreferenceClickListener(preference -> {
            openUrlInBrowser(requireContext(), getString(R.string.settings_report_bug_url));
            return true;
        });
    }
}
