package com.stypox.mastercom_workbook;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class QuadermasApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // do not let unhandled ReactiveX errors crash the app
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
