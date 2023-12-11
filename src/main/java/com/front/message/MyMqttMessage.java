package com.front.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyMqttMessage extends Message {
    byte[] payload;
    String topic;
    UUID senderId;
    Map<String, Double> measurement = new HashMap<>();
    Map<String, String> tags = new HashMap<>();
    String deviceId = null;

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

    public Map<String, String> getTags() {
        return this.tags;
    }

    public void putTags(String title, String value) {
        tags.put(title, value);
    }

    public Map<String, Double> getMeasurement() {
        return this.measurement;
    }

    public void putMeasurement(String sensor, Double value) {
        measurement.put(sensor, value);
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return new String(payload) + Arrays.toString(payload);
    }
}
