package com.front.node;

import org.json.simple.JSONObject;

import com.front.exception.AlreadyExistsException;
import com.front.exception.InvalidArgumentException;
import com.front.exception.OutOfBoundsException;
import com.front.message.Message;
import com.front.wire.Wire;

/**
 * {@code InputNode}는 {@code ActiveNode}를 상속하며, 입력을 받는 노드의 추상 클래스입니다.
 * 출력 와이어들을 관리하며, 출력 와이어를 통해 메시지를 전달할 수 있습니다.
 * 
 * <p>
 * 생성자를 통해 출력 와이어의 개수를 설정할 수 있습니다.
 * </p>
 */
public abstract class InputNode extends ActiveNode {

    // 출력 와이어 배열
    Wire[] outputWires;

    /**
     * JSON 형식의 데이터로 초기화하는 생성자로, 상위 클래스의 JSON 초기화 생성자를 호출합니다.
     *
     * @param json JSON 형식의 데이터
     */
    InputNode(JSONObject json) {
        super(json);
    }

    /**
     * 이름과 출력 와이어의 개수를 지정하여 초기화하는 생성자입니다.
     *
     * @param name  노드의 이름
     * @param count 출력 와이어의 개수
     */
    InputNode(String name, int count) {
        super(name);

        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        outputWires = new Wire[count];
    }

    /**
     * 출력 와이어의 개수를 지정하여 초기화하는 생성자입니다.
     *
     * @param count 출력 와이어의 개수
     */
    InputNode(int count) {
        super();

        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        outputWires = new Wire[count];
    }

    /**
     * 주어진 인덱스에 출력 와이어를 연결합니다.
     *
     * @param index 연결할 출력 와이어의 인덱스
     * @param wire  연결할 와이어
     * @throws OutOfBoundsException   인덱스가 유효 범위를 벗어날 때 발생
     * @throws AlreadyExistsException 이미 와이어가 연결되어 있는 경우 발생
     */
    public void connectOutputWire(int index, Wire wire) {
        if (outputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        if (outputWires[index] != null) {
            throw new AlreadyExistsException();
        }

        outputWires[index] = wire;
    }

    /**
     * 출력 와이어의 개수를 반환합니다.
     *
     * @return 출력 와이어의 개수
     */
    public int getOutputWireCount() {
        return outputWires.length;
    }

    /**
     * 주어진 인덱스의 출력 와이어를 반환합니다.
     *
     * @param index 가져올 출력 와이어의 인덱스
     * @return 출력 와이어
     * @throws OutOfBoundsException 인덱스가 유효 범위를 벗어날 때 발생
     */
    public Wire getoutputWire(int index) {
        if (index < 0 || outputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        return outputWires[index];
    }

    /**
     * 주어진 메시지를 모든 출력 와이어로 전송합니다.
     *
     * @param message 전송할 메시지
     */
    void output(Message message) {
        log.trace("Message Out");
        for (Wire wire : outputWires) {
            if (wire != null) {
                wire.put(message);
            }
        }
    }
}
