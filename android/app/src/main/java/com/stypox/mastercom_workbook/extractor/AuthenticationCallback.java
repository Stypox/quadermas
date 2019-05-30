package com.stypox.mastercom_workbook.extractor;

public interface AuthenticationCallback extends BaseCallback {
    void onAuthenticationCompleted(String fullName);
}
