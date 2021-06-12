package com.stypox.mastercom_workbook.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareUtils {
    public static void openUrlInBrowser(final Context context, final String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(intent);
        } catch (final ActivityNotFoundException e) {
            // ignore
        }
    }
}
