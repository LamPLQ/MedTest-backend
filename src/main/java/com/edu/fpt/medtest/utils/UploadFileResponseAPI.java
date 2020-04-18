package com.edu.fpt.medtest.utils;

public class UploadFileResponseAPI {
    private boolean isSuccess;

    private String uri;


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
