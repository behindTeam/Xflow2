package com.front.message;

import java.util.Arrays;

public class ModbusMessage extends Message {
    byte[] adu;
    byte unitId;

    public ModbusMessage(byte unitId, byte[] adu) {
        this.adu = Arrays.copyOf(adu, adu.length);
        this.unitId = unitId;
    }

    public byte getUnitId() {
        return unitId;
    }

    public byte[] getAdu() {
        return adu;
    }

    @Override
    public String toString() {
        return new String(adu) + Arrays.toString(adu);
    }
}
