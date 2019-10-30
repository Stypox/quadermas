package com.stypox.mastercom_workbook.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class FileDownloader {
    public static void download(String url, String cookie,
                                String title, String description,
                                String directory, String filename,
                                Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        if (title != null       && !title.isEmpty())       request.setTitle(title);
        if (description != null && !description.isEmpty()) request.setDescription(title);
        if (cookie != null      && !cookie.isEmpty())      request.addRequestHeader("Cookie", cookie);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(directory, filename);
        request.setVisibleInDownloadsUi(true);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
