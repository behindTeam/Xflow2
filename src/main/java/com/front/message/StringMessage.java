
package com.front.message;

/**
 * {@code StringMessage} 클래스는 Message 클래스를 상속받아 String 타입의 payload를 갖는 메시지를
 * 나타냅니다.
 */
public class StringMessage extends Message {
    String payload;

    /**
     * StringMessage 클래스의 생성자
     *
     * @param payload String 타입의 메시지 payload
     */
    public StringMessage(String payload) {
        this.payload = payload;
    }

    /**
     * 메시지의 payload 값을 반환하는 getter 메서드
     *
     * @return String 타입의 payload 값
     */
    public String getPayload() {
        return payload;
    }
}