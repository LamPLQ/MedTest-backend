package com.edu.fpt.medtest.utils;

public class CreatedSuccessApi extends ApiResponse {
    private boolean createdSuccess;

    public CreatedSuccessApi(Boolean success, String message, boolean createdSuccess) {
        super(success, message);
        this.createdSuccess = createdSuccess;
    }

    public boolean isCreatedSuccess() {
        return createdSuccess;
    }

    public void setCreatedSuccess(boolean createdSuccess) {
        this.createdSuccess = createdSuccess;
    }
}
