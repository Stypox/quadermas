package com.stypox.mastercom_workbook;

import android.app.Application;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MastercomWorkbookApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // do not let unhandled ReactiveX errors crash the app
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
    }
}
