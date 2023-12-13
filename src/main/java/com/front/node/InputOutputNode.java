package com.front.node;

import java.util.ArrayList;
import java.util.List;

import com.front.exception.OutOfBoundsException;
import com.front.message.Message;
import com.front.wire.Wire;

public abstract class InputOutputNode extends ActiveNode {
    // Wire[] inputWires;
    // Wire[] outputWires;
    List<Wire> inputWires;
    List<Wire> outputWires;

    InputOutputNode(String name, int inCount, int outCount) {
        super(name);

        // inputWires = new Wire[inCount];
        // outputWires = new Wire[outCount];
        inputWires = new ArrayList<>(inCount);
        outputWires = new ArrayList<>(outCount);
    }

    InputOutputNode(int inCount, int outCount) {
        super();

        // inputWires = new Wire[inCount];
        // outputWires = new Wire[outCount];
        inputWires = new ArrayList<>(inCount);
        outputWires = new ArrayList<>(outCount);
    }

    public void connectOutputWire(int index, Wire wire) {
        // if (index < 0 || outputWires.length <= index) {
        // throw new OutOfBoundsException();
        // }
        if (index < 0) {
            throw new OutOfBoundsException();
        }

        // outputWires[index] = wire;
        if (index < getOutputWireCount()) {
            outputWires.remove(index);
        }
        outputWires.add(index, wire);
    }

    public int getOutputWireCount() {
        // int count = 0;
        // for (Wire wire : outputWires) {
        // if (wire != null) {
        // count++;
        // }
        // }
        // return count;
        return outputWires.size();
    }

    public Wire getOutputWire(int index) {
        if (index < 0 || outputWires.size() <= index) {
            throw new OutOfBoundsException();
        }

        // return outputWires[index];
        return outputWires.get(index);
    }

    public void connectInputWire(int index, Wire wire) {
        // if (index < 0 || inputWires.length <= index) {
        // throw new OutOfBoundsException();
        // }
        if (index < 0) {
            throw new OutOfBoundsException();
        }

        // inputWires[index] = wire;
        if (index < getInputWireCount()) {
            inputWires.remove(index);
        }
        inputWires.add(index, wire);
    }

    public int getInputWireCount() {
        // int count = 0;
        // for (Wire wire : inputWires) {
        // if (wire != null) {
        // count++;
        // }
        // }
        // return count;
        return inputWires.size();
    }

    public Wire getInputWire(int index) {
        // if (index < 0 || inputWires.length <= index) {
        // throw new OutOfBoundsException();
        // }
        if (index < 0 || inputWires.size() <= index) {
            throw new OutOfBoundsException();
        }

        // return inputWires[index];
        return inputWires.get(index);
    }

    void output(Message message) {
        log.trace("Message Out");
        for (Wire port : outputWires) {
            if (port != null) {
                port.put(message);
            }
        }
    }
}
