package com.stypox.mastercom_workbook.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.annotation.Nullable;

import java.util.Date;

import static org.jsoup.internal.StringUtil.isBlank;

public class ShareUtils {
    public static void openUrlInBrowser(final Context context, final String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            context.startActivity(intent);
        } catch (final ActivityNotFoundException e) {
            // ignore
        }
    }

    public static void addEventToCalendar(final Context context,
                                          final String title,
                                          @Nullable final String description,
                                          @Nullable final String organizer,
                                          final Date begin,
                                          final Date end) {

        final Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin.getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTime());

        if (!isBlank(description)) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        }
        if (!isBlank(organizer)) {
            intent.putExtra(CalendarContract.Events.ORGANIZER, organizer);
        }

        try {
            context.startActivity(intent);
        } catch (final ActivityNotFoundException e) {
            // ignore
        }
    }
}
