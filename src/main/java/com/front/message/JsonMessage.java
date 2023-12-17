
package com.front.message;

import org.json.simple.JSONObject;

/**
 * {@code JsonMessage} 클래스는 Message 클래스를 상속받아 JSON 객체를 payload로 갖는 메시지를 나타냅니다.
 */
public class JsonMessage extends Message {
    JSONObject payload;

    /**
     * JsonMessage 클래스 생성자
     *
     * @param payload JSON 메시지 payload
     */
    public JsonMessage(JSONObject payload) {
        this.payload = payload;
    }

    /**
     * 메시지의 payload에 포함된 JSON 객체를 반환하는 메서드
     *
     * @return JSON payload
     */
    public JSONObject getPayload() {
        return payload;
    }

    /**
     * 객체를 문자열로 표현하는 메서드
     *
     * @return JSON 객체를 문자열로 변환한 값
     */
    @Override
    public String toString() {
        return payload.toString();
    }
}