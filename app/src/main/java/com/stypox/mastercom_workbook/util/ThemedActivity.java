package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import com.stypox.mastercom_workbook.MainActivity;
import com.stypox.mastercom_workbook.R;

import java.util.Objects;
import java.util.function.BiFunction;

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
        } else if (preference.equals(getString(R.string.value_theme_black))) {
            return R.style.BlackAppTheme;
        } else /*if (preference.equals(getString(R.string.settings_value_theme_light)))*/ {
            return R.style.LightAppTheme;
        }
    }

    private int getThemeFromPreferences() {
        String preference = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_theme), null);
        return preferenceToTheme(preference);
    }

    public static void setDecorFitsSystemWindows(Window window,
                                                 final boolean decorFitsSystemWindows) {
        if (Build.VERSION.SDK_INT >= 35) {
            window.setDecorFitsSystemWindows(decorFitsSystemWindows);
        } else if (Build.VERSION.SDK_INT >= 30) {
            final int stableFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            final View decorView = window.getDecorView();
            final int sysUiVis = decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(decorFitsSystemWindows
                    ? sysUiVis & ~stableFlag
                    : sysUiVis | stableFlag);
            window.setDecorFitsSystemWindows(decorFitsSystemWindows);
        } else if (Build.VERSION.SDK_INT >= 16) {
            final int decorFitsFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            final View decorView = window.getDecorView();
            final int sysUiVis = decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(decorFitsSystemWindows
                    ? sysUiVis & ~decorFitsFlags
                    : sysUiVis | decorFitsFlags);
        }
    }

    public static void enableEdgeToEdge(Window window) {
        Objects.requireNonNull(window);
        setDecorFitsSystemWindows(window, false);
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= 28) {
            final int newMode = Build.VERSION.SDK_INT >= 30
                    ? WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                    : WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            final WindowManager.LayoutParams attrs = window.getAttributes();
            if (attrs.layoutInDisplayCutoutMode != newMode) {
                attrs.layoutInDisplayCutoutMode = newMode;
                window.setAttributes(attrs);
            }
        }
        if (Build.VERSION.SDK_INT >= 29) {
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        currentTheme = getThemeFromPreferences();
        setTheme(currentTheme);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            enableEdgeToEdge(getWindow());
        }
    }

    @RequiresApi(21)
    static void callCompatInsetAnimationCallback(final WindowInsets insets,
                                                 final View v) {
        // In case a WindowInsetsAnimationCompat.Callback is set, make sure to
        // call its compat listener.
        View.OnApplyWindowInsetsListener insetsAnimationCallback =
                (View.OnApplyWindowInsetsListener) v.getTag(
                        R.id.tag_window_insets_animation_callback);
        if (insetsAnimationCallback != null) {
            insetsAnimationCallback.onApplyWindowInsets(v, insets);
        }
    }

    public interface OnApplyWindowInsetsListener {
        WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets);
    }

    public void fixInsets() {
        View v = findViewById(android.R.id.content);
        TypedValue typedValue = new TypedValue();
        try(TypedArray a = v.getContext().obtainStyledAttributes(typedValue.data,
                new int[]{android.R.attr.colorBackground})) {
            v.setBackgroundColor(a.getColor(0, 0));
        }


        if (Build.VERSION.SDK_INT < 21 || this instanceof MainActivity) {
            return;
        }

        final OnApplyWindowInsetsListener listener = (vv, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        | WindowInsetsCompat.Type.displayCutout()
                        | WindowInsetsCompat.Type.ime()
            );
            v.setPadding(insets.left, 0, insets.right, insets.bottom);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) vv.getLayoutParams();
            params.leftMargin = 0;
            params.topMargin = insets.top;
            params.rightMargin = 0;
            params.bottomMargin = 0;
            vv.setLayoutParams(params);
            return WindowInsetsCompat.CONSUMED;
        };

        // We got the same insets we just return the previously computed
        // insets.
        // On API < 30, the visibleInsets, used to built WindowInsetsCompat, are
        // updated after the insets dispatch so we don't have the updated
        // visible insets at that point. As a workaround, we re-apply the insets
        // so we know that we'll have the right value the next time it's called.
        // Keep a copy in case the insets haven't changed on the next call so we
        // don't need to call the listener again.
        final View.OnApplyWindowInsetsListener wrappedUserListener = new View.OnApplyWindowInsetsListener() {
            WindowInsetsCompat mLastInsets = null;

            @Override
            public WindowInsets onApplyWindowInsets(final View view,
                                                    final WindowInsets insets) {
                WindowInsetsCompat compatInsets =
                        WindowInsetsCompat.toWindowInsetsCompat(insets, view);
                if (Build.VERSION.SDK_INT < 30) {
                    callCompatInsetAnimationCallback(insets, v);

                    if (compatInsets.equals(mLastInsets)) {
                        // We got the same insets we just return the previously computed
                        // insets.
                        return listener.onApplyWindowInsets(view, compatInsets)
                                .toWindowInsets();
                    }
                }
                mLastInsets = compatInsets;
                compatInsets = listener.onApplyWindowInsets(view, compatInsets);

                if (Build.VERSION.SDK_INT >= 30) {
                    return compatInsets.toWindowInsets();
                }

                // On API < 30, the visibleInsets, used to built WindowInsetsCompat, are
                // updated after the insets dispatch so we don't have the updated
                // visible insets at that point. As a workaround, we re-apply the insets
                // so we know that we'll have the right value the next time it's called.
                view.requestApplyInsets();
                // Keep a copy in case the insets haven't changed on the next call so we
                // don't need to call the listener again.

                return compatInsets.toWindowInsets();
            }
        };

        // For backward compatibility of WindowInsetsAnimation, we use an
        // OnApplyWindowInsetsListener. We use the view tags to keep track of both listeners
        if (Build.VERSION.SDK_INT < 30) {
            v.setTag(R.id.tag_on_apply_window_listener, wrappedUserListener);
        }

        // final Object compatInsetsDispatch = v.getTag(R.id.tag_compat_insets_dispatch);
        // if (compatInsetsDispatch != null) {
        //     // Don't call `v.setOnApplyWindowInsetsListener`. Otherwise, it will overwrite the
        //     // compat-dispatch listener. The compat-dispatch listener will make sure listeners
        //     // stored with `tag_on_apply_window_listener` and
        //     // `tag_window_insets_animation_callback` are get called.
        //     return;
        // }

        if (wrappedUserListener != null) {
            v.setOnApplyWindowInsetsListener(wrappedUserListener);
        } else {
            // If the listener is null, we need to make sure our compat listener, if any, is
            // set in-lieu of the listener being removed.
            final View.OnApplyWindowInsetsListener compatInsetsAnimationCallback =
                    (View.OnApplyWindowInsetsListener) v.getTag(
                            R.id.tag_window_insets_animation_callback);
            v.setOnApplyWindowInsetsListener(compatInsetsAnimationCallback);
        }
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
