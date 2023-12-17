package com.front.node;

import java.util.ArrayList;
import java.util.List;

import com.front.exception.OutOfBoundsException;
import com.front.message.Message;
import com.front.wire.Wire;

/**
 * 입력 및 출력을 처리하는 노드를 나타내는 추상 클래스입니다. {@code ActiveNode}를 상속합니다.
 */
public abstract class InputOutputNode extends ActiveNode {
    /** 입력에 연결된 와이어 목록 */
    List<Wire> inputWires;

    /** 출력에 연결된 와이어 목록 */
    List<Wire> outputWires;

    /**
     * 이름을 지정하여 입력 및 출력 수를 가진 노드를 생성합니다.
     *
     * @param name     노드의 이름.
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    InputOutputNode(String name, int inCount, int outCount) {
        super(name);
        inputWires = new ArrayList<>(inCount);
        outputWires = new ArrayList<>(outCount);
    }

    /**
     * 입력 및 출력 수를 가진 노드를 생성합니다.
     *
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    InputOutputNode(int inCount, int outCount) {
        super();
        inputWires = new ArrayList<>(inCount);
        outputWires = new ArrayList<>(outCount);
    }

    /**
     * 지정된 인덱스에 출력 와이어를 연결합니다.
     *
     * @param index 연결할 출력 와이어의 인덱스.
     * @param wire  연결할 와이어 객체.
     * @throws OutOfBoundsException 인덱스가 범위를 벗어날 때 발생합니다.
     */
    public void connectOutputWire(int index, Wire wire) throws OutOfBoundsException {
        if (index < 0) {
            throw new OutOfBoundsException();
        }
        if (index < getOutputWireCount()) {
            outputWires.remove(index);
        }
        outputWires.add(index, wire);
    }

    /**
     * 출력 와이어의 수를 반환합니다.
     *
     * @return 출력 와이어의 수.
     */
    public int getOutputWireCount() {
        return outputWires.size();
    }

    /**
     * 지정된 인덱스의 출력 와이어를 반환합니다.
     *
     * @param index 가져올 출력 와이어의 인덱스.
     * @return 출력 와이어 객체.
     * @throws OutOfBoundsException 인덱스가 범위를 벗어날 때 발생합니다.
     */
    public Wire getOutputWire(int index) throws OutOfBoundsException {
        if (index < 0 || outputWires.size() <= index) {
            throw new OutOfBoundsException();
        }
        return outputWires.get(index);
    }

    /**
     * 지정된 인덱스에 입력 와이어를 연결합니다.
     *
     * @param index 연결할 입력 와이어의 인덱스.
     * @param wire  연결할 와이어 객체.
     * @throws OutOfBoundsException 인덱스가 범위를 벗어날 때 발생합니다.
     */
    public void connectInputWire(int index, Wire wire) throws OutOfBoundsException {
        if (index < 0) {
            throw new OutOfBoundsException();
        }
        if (index < getInputWireCount()) {
            inputWires.remove(index);
        }
        inputWires.add(index, wire);
    }

    /**
     * 입력 와이어의 수를 반환합니다.
     *
     * @return 입력 와이어의 수.
     */
    public int getInputWireCount() {
        return inputWires.size();
    }

    /**
     * 지정된 인덱스의 입력 와이어를 반환합니다.
     *
     * @param index 가져올 입력 와이어의 인덱스.
     * @return 입력 와이어 객체.
     * @throws OutOfBoundsException 인덱스가 범위를 벗어날 때 발생합니다.
     */
    public Wire getInputWire(int index) throws OutOfBoundsException {
        if (index < 0 || inputWires.size() <= index) {
            throw new OutOfBoundsException();
        }
        return inputWires.get(index);
    }

    /**
     * 메시지를 출력 와이어로 전송합니다.
     *
     * @param message 출력할 메시지 객체.
     */
    void output(Message message) {
        log.trace("Message Out");
        for (Wire port : outputWires) {
            if (port != null) {
                port.put(message);
            }
        }
    }
}
