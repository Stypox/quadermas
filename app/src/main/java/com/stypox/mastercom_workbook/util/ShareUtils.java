package com.stypox.mastercom_workbook.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareUtils {
    public static void openUrlInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
