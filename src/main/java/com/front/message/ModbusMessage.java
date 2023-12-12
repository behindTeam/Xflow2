package com.front.message;

public class ModbusMessage extends Message {
    byte address;
    byte functionCode;

    public ModbusMessage(byte address, byte functionCode) {
        this.address = address;
        this.functionCode = functionCode;
    }

    public byte getAddress() {
        return address;
    }

    public byte getFunctionCode() {
        return functionCode;
    }

    @Override
    public String toString() {
        return "ModbusMessage [address=" + address + ", functionCode=" + functionCode + "]";
    }

}
