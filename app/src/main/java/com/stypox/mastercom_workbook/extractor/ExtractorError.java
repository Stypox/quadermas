package com.stypox.mastercom_workbook.extractor;

import android.content.Context;

import androidx.annotation.Nullable;

import com.stypox.mastercom_workbook.R;

import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketException;

public class ExtractorError extends Exception {
    public enum Type {
        malformed_url,
        network,
        not_json,
        unsuitable_json,
        unsuitable_date,
        invalid_credentials,
        unknown;

        public String toString(Context context) {
            switch (this) {
                case malformed_url:
                    return context.getString(R.string.error_malformed_url);
                case network:
                    return context.getString(R.string.error_network);
                case not_json:
                    return context.getString(R.string.error_not_json);
                case unsuitable_json:
                    return context.getString(R.string.error_unsuitable_json);
                case unsuitable_date:
                    return context.getString(R.string.error_unsuitable_date);
                case invalid_credentials:
                    return context.getString(R.string.error_invalid_credentials);
                case unknown: default: // default is useless
                    return context.getString(R.string.error_unknown);
            }
        }
    }

    private final Type type;


    public ExtractorError(Type type, Throwable e) {
        super(e);
        this.type = type;
    }

    public ExtractorError(Type type) {
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
        if (hasAssignableCause(throwable, ExtractorError.class)) {
            return (ExtractorError) throwable;
        } else if (hasAssignableCause(throwable, MalformedURLException.class)) {
            return new ExtractorError(Type.malformed_url, throwable);
        } else if (hasAssignableCause(throwable, JSONException.class)) {
            if (jsonAlreadyParsed) {
                return new ExtractorError(Type.unsuitable_json, throwable);
            } else {
                return new ExtractorError(Type.not_json, throwable);
            }
        } else if (hasAssignableCause(throwable,
                // network api cancellation
                IOException.class, SocketException.class,
                // blocking code disposed
                InterruptedException.class, InterruptedIOException.class)) {
            return new ExtractorError(Type.network, throwable);
        } else {
            return new ExtractorError(Type.unknown, throwable);
        }
    }

    // taken from NewPipe, file util/ExceptionUtils.kt, created by @mauriciocolli
    private static boolean hasAssignableCause(Throwable throwable, Class<?>... causesToCheck) {
        if (throwable == null) {
            return false;
        }

        // Check if throwable is a subtype of any of the causes to check
        for (Class<?> causeClass : causesToCheck) {
            if (causeClass.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }

        @Nullable Throwable currentCause = throwable.getCause();
        // Check if cause is not pointing to the same instance, to avoid infinite loops.
        if (throwable != currentCause) {
            return hasAssignableCause(currentCause, causesToCheck);
        } else {
            return false;
        }
    }
}
