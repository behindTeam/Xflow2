package com.front.message;

import org.json.simple.JSONObject;

public class JsonMessage extends Message {
    JSONObject payload;

    public JsonMessage(JSONObject payload) {
        this.payload = payload;
    }

    public JSONObject getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return payload.toString();
    }
}
