package com.stypox.mastercom_workbook.extractor;

import android.content.Context;

import com.stypox.mastercom_workbook.R;

import org.json.JSONException;

import java.net.MalformedURLException;

public class ExtractorError extends Exception {
    public enum Type {
        malformed_url,
        network,
        not_json,
        unsuitable_json,
        invalid_credentials;

        public String toString(Context context) {
            switch (this) {
                case malformed_url:
                    return context.getResources().getString(R.string.error_malformed_url);
                case network:
                    return context.getResources().getString(R.string.error_network);
                case not_json:
                    return context.getResources().getString(R.string.error_not_json);
                case unsuitable_json:
                    return context.getResources().getString(R.string.error_unsuitable_json);
                case invalid_credentials: default: // default is useless
                    return context.getResources().getString(R.string.error_invalid_credentials);
            }
        }
    }

    private Type type;

    ExtractorError(Type type, Throwable e) {
        super(e);
        this.type = type;
    }
    ExtractorError(Type type) {
        super();
        this.type = type;
    }

    public String getMessage(Context context) {
        return type.toString(context);
    }

    public boolean isType(Type type) {
        return this.type == type;
    }


    public static ExtractorError asExtractorError(Throwable throwable, boolean jsonAlreadyParsed) {
        if (throwable instanceof ExtractorError) {
            return (ExtractorError) throwable;
        } else if (throwable instanceof MalformedURLException) {
            return new ExtractorError(Type.malformed_url, throwable);
        } else if (throwable instanceof JSONException) {
            if (jsonAlreadyParsed) {
                return new ExtractorError(Type.unsuitable_json, throwable);
            } else {
                return new ExtractorError(Type.not_json, throwable);
            }
        } else { // throwable instanceof IOException
            return new ExtractorError(Type.network, throwable);
        }
    }
}
