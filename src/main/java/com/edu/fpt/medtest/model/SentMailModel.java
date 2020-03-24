package com.edu.fpt.medtest.model;

import org.springframework.stereotype.Component;

@Component
public class SentMailModel {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
