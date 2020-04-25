package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.stypox.mastercom_workbook.R;

abstract public class ThemedActivity extends AppCompatActivity {

    ///////////////
    // BEHAVIOUR //
    ///////////////

    private int currentTheme;


    private int preferenceToTheme(@Nullable String preference) {
        if (preference == null) {
            return R.style.LightAppTheme;
        }

        if (preference.equals(getString(R.string.value_theme_dark))) {
            return R.style.DarkAppTheme;
        } else /*if (preference.equals(getString(R.string.settings_value_theme_light)))*/ {
            return R.style.LightAppTheme;
        }
    }

    private int getThemeFromPreferences() {
        String preference = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_theme), null);
        return preferenceToTheme(preference);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        currentTheme = getThemeFromPreferences();
        setTheme(currentTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentTheme != getThemeFromPreferences()) {
            recreate();
        }
    }


    ///////////////
    // UTILITIES //
    ///////////////

    // taken from NewPipe, file util/ThemeHelper.java, created by @mauriciocolli
    public static int resolveColor(Context context, @AttrRes int attrColor) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrColor, value, true);

        if (value.resourceId != 0) {
            return ContextCompat.getColor(context, value.resourceId);
        }

        return value.data;
    }
}
