
package com.front.wire;

import com.front.message.Message;

/**
 * {@code Wire} 인터페이스는 메시지를 관리하는 Wire 구현 클래스가 메서드를 정의하도록 돕는 추상 클래스입니다.
 */
public interface Wire {

    /**
     * 메시지를 Wire에 추가하는 메서드입니다.
     *
     * @param message Wire에 추가할 메시지
     */
    public void put(Message message);

    /**
     * Wire에 메시지가 있는지 여부를 확인하는 메서드입니다.
     *
     * @return 메시지가 존재하면 true, 그렇지 않으면 false
     */
    public boolean hasMessage();

    /**
     * Wire에서 메시지를 가져오는 메서드입니다.
     *
     * @return 가져온 메시지, Wire에 메시지가 없으면 null
     */
    public Message get();
}
