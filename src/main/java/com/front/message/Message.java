
package com.front.message;

/**
 * {@code Message} 추상 클래스는 메시지의 기본 속성 및 행동을 정의합니다.
 */

public abstract class Message {
    static int count;
    final String id;
    long creationTime;

    /**
     * Message 클래스의 기본 생성자
     * 클래스 이름과 일련번호를 결합하여 메시지의 고유한 ID를 생성하고, 생성 시간을 기록
     */
    Message() {
        count++;
        id = getClass().getSimpleName() + count;
        creationTime = System.currentTimeMillis();
    }

    /**
     * 메시지의 고유한 ID를 반환하는 getter 메서드
     *
     * @return 메시지의 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 메시지의 생성 시간을 반환하는 getter 메서드
     *
     * @return 메시지가 생성된 시간 (타임스탬프)
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 생성된 메시지의 총 수를 반환하는 static 메서드 (공유자원)
     *
     * @return 생성된 메시지의 총 수
     */
    public static int getCount() {
        return count;
    }
}