package com.edu.fpt.medtest.utils;

public class SendMessageResponse extends ApiResponse {

    private boolean IsMessageSent;

    public SendMessageResponse(Boolean success, String message, boolean isMessageSent) {
        super(success, message);
        IsMessageSent = isMessageSent;
    }

    public boolean isMessageSent() {
        return IsMessageSent;
    }

    public void setMessageSent(boolean messageSent) {
        IsMessageSent = messageSent;
    }
}
