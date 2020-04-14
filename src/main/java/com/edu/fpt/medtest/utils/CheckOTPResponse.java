package com.edu.fpt.medtest.utils;

public class CheckOTPResponse extends ApiResponse {
    private boolean isSuccessfulRegistered;
    private boolean isValid;

    public CheckOTPResponse(Boolean success, String message, boolean isSuccessfulRegistered, boolean isValid) {
        super(success, message);
        this.isSuccessfulRegistered = isSuccessfulRegistered;
        this.isValid = isValid;
    }

    public boolean isSuccessfulRegistered() {
        return isSuccessfulRegistered;
    }

    public void setSuccessfulRegistered(boolean successfulRegistered) {
        isSuccessfulRegistered = successfulRegistered;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
