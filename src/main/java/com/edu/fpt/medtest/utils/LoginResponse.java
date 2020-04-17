package com.edu.fpt.medtest.utils;

public class LoginResponse extends ApiResponse {
    private boolean isLogin;

    public LoginResponse(Boolean success, String message, boolean isLogin) {
        super(success, message);
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
