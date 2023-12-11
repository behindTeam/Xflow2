package com.front.message;

public class StringArrayMessage extends Message {
    String[] payload;

    public StringArrayMessage(String[] payload) {
        this.payload = payload;
    }

    public String[] getPayload() {
        return payload;
    }
}
