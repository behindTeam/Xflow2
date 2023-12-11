package com.front.message;

import java.util.Arrays;
import java.util.UUID;

public class MyMqttMessage extends Message {
    byte[] payload;
    String topic;
    UUID senderId;

    public MyMqttMessage(UUID senderId, String topic, byte[] payload) {
        this.payload = Arrays.copyOf(payload, payload.length);
        this.topic = topic;
        this.senderId = senderId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return new String(payload) + Arrays.toString(payload);
    }
}
