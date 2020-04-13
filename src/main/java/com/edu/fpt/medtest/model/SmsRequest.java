package com.edu.fpt.medtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class SmsRequest {
    @NotNull
    private final String phoneNumber;

   /* @NotNull
    private final String message;*/

    public SmsRequest(@JsonProperty("phoneNumber") String phoneNumber){
                      //@JsonProperty("message") String message) {
        this.phoneNumber = phoneNumber;
        //this.message = message;
    }

   /* public String getMessage() {
        return message;
    }*/

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
