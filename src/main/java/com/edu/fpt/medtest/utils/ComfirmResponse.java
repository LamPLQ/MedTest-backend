package com.edu.fpt.medtest.utils;

public class ComfirmResponse extends ApiResponse {

    private Boolean changedSuccess;

    public ComfirmResponse(Boolean success, String message, Boolean changedSuccess) {
        super(success, message);
        this.changedSuccess = changedSuccess;
    }

    public Boolean getChangedSuccess() {
        return changedSuccess;
    }

    public void setChangedSuccess(Boolean changedSuccess) {
        this.changedSuccess = changedSuccess;
    }
}
