package com.stypox.mastercom_workbook.settings;

import android.os.Bundle;

import androidx.annotation.StringRes;
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
        assert theme != null;
        theme.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        final Preference secondTermStart = findPreference(getString(R.string.key_second_term_start));
        assert secondTermStart != null;
        secondTermStart.setOnPreferenceClickListener((preference) -> {
            SecondTermStart.openPickerDialog(requireContext(),
                    () -> updateSecondTermStartSummary(secondTermStart));
            return true;
        });
        updateSecondTermStartSummary(secondTermStart);

        openUrlOnPreferenceClick(R.string.key_source_code, R.string.settings_source_code_url);
        openUrlOnPreferenceClick(R.string.key_report_bug, R.string.settings_report_bug_url);
        openUrlOnPreferenceClick(R.string.key_changelog, R.string.settings_changelog_url);

        final Preference changelog = findPreference(getString(R.string.key_changelog));
        assert changelog != null;
        changelog.setTitle(getString(R.string.settings_changelog,
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    void openUrlOnPreferenceClick(@StringRes final int preferenceKey, @StringRes final int url) {
        final Preference preference = findPreference(getString(preferenceKey));
        assert preference != null;
        preference.setOnPreferenceClickListener(p -> {
            openUrlInBrowser(requireContext(), getString(url));
            return true;
        });
    }

    void updateSecondTermStartSummary(final Preference preference) {
        final SecondTermStart secondTermStart = SecondTermStart.fromPreferences(requireContext());
        preference.setSummary(requireContext().getString(
                R.string.settings_second_term_start_summary,
                secondTermStart.getDay(),
                secondTermStart.getMonth()));
    }
}
