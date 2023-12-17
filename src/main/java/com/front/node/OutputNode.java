package com.front.node;

import com.front.exception.AlreadyExistsException;
import com.front.exception.InvalidArgumentException;
import com.front.exception.OutOfBoundsException;
import com.front.wire.Wire;

/**
 * {@code OutputNode}는 노드의 출력 기능을 정의하는 추상 클래스입니다.
 * 이 클래스를 상속하여 생성된 노드는 다수의 입력 와이어에 연결될 수 있습니다.
 */
public abstract class OutputNode extends ActiveNode {
    Wire[] inputWires;

    /**
     * 주어진 이름과 입력 와이어 개수로 초기화하는 생성자입니다.
     *
     * @param name  초기화할 이름
     * @param count 초기화할 입력 와이어 개수
     */
    OutputNode(String name, int count) {
        super(name);
        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        inputWires = new Wire[count];
    }

    /**
     * 주어진 입력 와이어 개수로 초기화하는 생성자입니다.
     * 이름은 생성되는 순서대로 부여됩니다.
     *
     * @param count 초기화할 입력 와이어 개수
     */
    OutputNode(int count) {
        super();
        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        inputWires = new Wire[count];
    }

    /**
     * 주어진 인덱스와 와이어를 사용하여 입력 와이어를 연결합니다.
     * 이미 연결된 경우 {@code AlreadyExistsException}을 발생시킵니다.
     *
     * @param index 연결할 입력 와이어의 인덱스
     * @param wire  연결할 입력 와이어
     */
    public void connectInputWire(int index, Wire wire) {
        if (inputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        if (inputWires[index] != null) {
            throw new AlreadyExistsException();
        }

        inputWires[index] = wire;
    }

    /**
     * 입력 와이어의 개수를 반환합니다.
     *
     * @return 입력 와이어의 개수
     */
    public int getInputWireCount() {
        return inputWires.length;
    }

    /**
     * 주어진 인덱스에 해당하는 입력 와이어를 반환합니다.
     * 인덱스가 범위를 벗어난 경우 {@code OutOfBoundsException}을 발생시킵니다.
     *
     * @param index 반환할 입력 와이어의 인덱스
     * @return 입력 와이어
     */
    public Wire getInputWire(int index) {
        if (index < 0 || inputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        return inputWires[index];
    }
}
