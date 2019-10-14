package com.stypox.mastercom_workbook.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class UrlConnectionUtils {
    public static String readAll(HttpURLConnection urlConnection) throws IOException {
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } finally {
            urlConnection.disconnect();
        }
    }
}
