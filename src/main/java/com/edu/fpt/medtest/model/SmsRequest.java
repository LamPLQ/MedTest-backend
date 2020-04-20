package com.edu.fpt.medtest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class SmsRequest {
    @NotNull
    private final String phoneNumber;

    @NotNull
    private final String role;

   public SmsRequest(@JsonProperty("phoneNumber") String phoneNumber,
                      @JsonProperty("role") String role) {
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

  /* public SmsRequest(@JsonProperty("phoneNumber") String phoneNumber) {
       this.phoneNumber = phoneNumber;
   }*/

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
