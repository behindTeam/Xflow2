package com.front.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MQTT(MQ Telemetry Transport) 메시지를 나타내는 클래스입니다. {@code Message} 클래스를 상속합니다.
 */
public class MyMqttMessage extends Message {
    /** 메시지의 페이로드 */
    byte[] payload;

    /** MQTT 토픽 */
    String topic;

    /** 메시지를 전송한 Sender의 UUID */
    UUID senderId;

    /** 측정값을 저장하는 HashMap */
    Map<String, Double> measurement = new HashMap<>();

    /** 태그 정보를 저장하는 HashMap */
    Map<String, String> tags = new HashMap<>();

    /** 디바이스 ID (기본값은 null) */
    String deviceId = null;

    /**
     * 주어진 Sender의 UUID, 토픽, 페이로드로 MyMqttMessage를 생성합니다.
     *
     * @param senderId 메시지를 전송한 Sender의 UUID.
     * @param topic    MQTT 토픽.
     * @param payload  메시지의 페이로드.
     */
    public MyMqttMessage(UUID senderId, String topic, byte[] payload) {
        this.payload = Arrays.copyOf(payload, payload.length);
        this.topic = topic;
        this.senderId = senderId;
    }

    /**
     * 메시지를 전송한 Sender의 UUID를 반환합니다.
     *
     * @return 메시지를 전송한 Sender의 UUID.
     */
    public UUID getSenderId() {
        return senderId;
    }

    /**
     * 메시지의 페이로드를 반환합니다.
     *
     * @return 메시지의 페이로드.
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * MQTT 토픽을 반환합니다.
     *
     * @return MQTT 토픽.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 태그 정보를 포함한 맵을 반환합니다.
     *
     * @return 태그 정보를 포함한 맵.
     */
    public Map<String, String> getTags() {
        return this.tags;
    }

    /**
     * 주어진 타이틀과 값으로 태그 정보를 추가합니다.
     *
     * @param title 태그의 타이틀.
     * @param value 태그의 값.
     */
    public void putTags(String title, String value) {
        tags.put(title, value);
    }

    /**
     * 측정값을 포함한 맵을 반환합니다.
     *
     * @return 측정값을 포함한 맵.
     */
    public Map<String, Double> getMeasurement() {
        return this.measurement;
    }

    /**
     * 주어진 센서와 값으로 측정값을 추가합니다.
     *
     * @param sensor 센서의 이름.
     * @param value  센서의 값.
     */
    public void putMeasurement(String sensor, Double value) {
        measurement.put(sensor, value);
    }

    /**
     * 디바이스 ID를 반환합니다.
     *
     * @return 디바이스 ID.
     */
    public String getDeviceId() {
        return this.deviceId;
    }

    /**
     * 주어진 디바이스 ID로 설정합니다.
     *
     * @param deviceId 설정할 디바이스 ID.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 메시지의 문자열 표현을 반환합니다.
     *
     * @return 메시지의 문자열 표현.
     */
    @Override
    public String toString() {
        return new String(payload) + Arrays.toString(payload);
    }
}
