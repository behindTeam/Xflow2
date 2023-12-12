package com.front.message;

import java.util.Arrays;
import java.util.UUID;

public class ModbusMessage extends Message {
    byte[] adu;
    UUID senderId;

    public ModbusMessage(UUID senderId, byte[] adu) {
        this.adu = Arrays.copyOf(adu, adu.length);
        this.senderId = senderId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public byte[] getAdu() {
        return adu;
    }

    @Override
    public String toString() {
        return new String(adu) + Arrays.toString(adu);
    }
}
