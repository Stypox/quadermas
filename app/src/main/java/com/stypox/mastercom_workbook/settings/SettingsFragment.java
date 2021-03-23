package com.stypox.mastercom_workbook.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.stypox.mastercom_workbook.BuildConfig;
import com.stypox.mastercom_workbook.R;

import static com.stypox.mastercom_workbook.util.ShareUtils.openUrlInBrowser;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        final Preference theme = findPreference(getString(R.string.key_theme));
        final Preference sourceCode = findPreference(getString(R.string.key_source_code));
        final Preference reportBug = findPreference(getString(R.string.key_report_bug));
        final Preference changelog = findPreference(getString(R.string.key_changelog));

        assert theme != null;
        assert sourceCode != null;
        assert reportBug != null;
        assert changelog != null;

        changelog.setTitle(getString(R.string.settings_changelog,
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

        theme.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });
        sourceCode.setOnPreferenceClickListener(preference -> {
            openUrlInBrowser(requireContext(), getString(R.string.settings_source_code_url));
            return true;
        });
        reportBug.setOnPreferenceClickListener(preference -> {
            openUrlInBrowser(requireContext(), getString(R.string.settings_report_bug_url));
            return true;
        });
        changelog.setOnPreferenceClickListener(preference -> {
            openUrlInBrowser(requireContext(), getString(R.string.settings_changelog_url));
            return true;
        });
    }
}
