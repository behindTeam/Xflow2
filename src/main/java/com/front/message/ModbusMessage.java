package com.front.message;

import java.util.Arrays;

/**
 * Modbus 통신을 위한 메시지를 나타내는 클래스입니다. {@code Message} 클래스를 상속합니다.
 */
public class ModbusMessage extends Message {
    /** Modbus 메시지의 응용 데이터 유닛 (ADU) */
    byte[] adu;

    /** Modbus 메시지의 유닛 ID */
    byte unitId;

    /**
     * 주어진 유닛 ID와 응용 데이터 유닛 (ADU)로 ModbusMessage를 생성합니다.
     *
     * @param unitId Modbus 메시지의 유닛 ID.
     * @param adu    Modbus 메시지의 응용 데이터 유닛 (ADU).
     */
    public ModbusMessage(byte unitId, byte[] adu) {
        this.adu = Arrays.copyOf(adu, adu.length);
        this.unitId = unitId;
    }

    /**
     * Modbus 메시지의 유닛 ID를 반환합니다.
     *
     * @return Modbus 메시지의 유닛 ID.
     */
    public byte getUnitId() {
        return unitId;
    }

    /**
     * Modbus 메시지의 응용 데이터 유닛 (ADU)를 반환합니다.
     *
     * @return Modbus 메시지의 응용 데이터 유닛 (ADU).
     */
    public byte[] getAdu() {
        return adu;
    }

    /**
     * Modbus 메시지의 문자열 표현을 반환합니다.
     *
     * @return Modbus 메시지의 문자열 표현.
     */
    @Override
    public String toString() {
        return new String(adu) + Arrays.toString(adu);
    }
}
