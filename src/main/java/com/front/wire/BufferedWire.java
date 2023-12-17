
package com.front.wire;

import java.util.LinkedList;
import java.util.Queue;

import com.front.message.Message;

/**
 * {@code BufferedWire} 클래스는 Wire 인터페이스를 구현하여 메시지를 버퍼에 저장하고 관리하는 클래스입니다.
 * 이 클래스는 메시지를 큐에 저장하고, 필요에 따라 메시지를 추출하거나 유무를 확인할 수 있습니다.
 */
public class BufferedWire implements Wire {
    Queue<Message> messageQueue;
    int wireCount;

    /**
     * BufferedWire 클래스의 기본 생성자입니다.
     * 큐를 초기화하여 메시지를 저장할 수 있도록 합니다.
     */
    public BufferedWire() {
        super();
        messageQueue = new LinkedList<>();
    }

    /**
     * 메시지를 버퍼에 추가하는 메서드입니다.
     *
     * @param message 버퍼에 추가할 메시지
     */
    public void put(Message message) {
        messageQueue.add(message);
    }

    /**
     * 현재 버퍼에 메시지가 있는지 여부를 확인하는 메서드입니다.
     *
     * @return 메시지가 존재하면 true, 그렇지 않으면 false
     */
    public boolean hasMessage() {
        return !messageQueue.isEmpty();
    }

    /**
     * 버퍼에서 메시지를 추출하는 메서드입니다.
     *
     * @return 추출된 메시지, 버퍼가 비어있으면 null
     */
    public Message get() {
        return messageQueue.poll();
    }
}
