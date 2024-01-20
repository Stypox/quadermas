package com.stypox.mastercom_workbook.util;

import static com.stypox.mastercom_workbook.util.StringUtils.isBlank;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class FileDownloader {
    public static void download(String url, String cookie,
                                String title, String description,
                                String directory, String filename,
                                Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        if (!isBlank(title)) request.setTitle(title);
        if (!isBlank(description)) request.setDescription(title);
        if (!isBlank(cookie)) request.addRequestHeader("Cookie", cookie);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(directory, filename);
        request.setVisibleInDownloadsUi(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE |
                DownloadManager.Request.NETWORK_WIFI);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
