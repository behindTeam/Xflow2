package com.front;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleMB {
    public static byte[] makeReadHoldingRegisterResponse(int address, int[] registers) {
        byte[] frame = new byte[1 + 1 + registers.length * 2];

        frame[0] = 0x03;

        frame[1] = (byte) (registers.length * 2);

        for (int i = 0; i < registers.length; i++) {
            frame[2 + i * 2] = (byte) ((registers[i] >> 8) & 0xFF);
            frame[2 + i * 2 + 1] = (byte) ((registers[i]) & 0xFF);
        }

        return frame;
    }

    public static byte[] makeReadInputRegistersResponse(int startingAddress, int quantityOfRegisters) {
        byte[] frame = new byte[6];

        frame[0] = 0x04;

        frame[1] = (byte) ((startingAddress >> 8) & 0xFF);
        frame[2] = (byte) (startingAddress & 0xFF);

        frame[3] = (byte) ((quantityOfRegisters >> 8) & 0xFF);
        frame[4] = (byte) (quantityOfRegisters & 0xFF);

        return frame;
    }
    // public static byte[] makeWriteSingleRegistersResponse(int address, int[]
    // registers) {
    // byte[] frame = new byte[1 + 1 + registers.length * 2];

    // frame[0] = 0x06;

    // frame[1] = (byte) (registers.length * 2);

    // for (int i = 0; i < registers.length; i++) {
    // frame[2 + i * 2] = (byte) ((registers[i] >> 8) & 0xFF);
    // frame[2 + i * 2 + 1] = (byte) ((registers[i]) & 0xFF);
    // }

    // return frame;
    // }

    // public static byte[] makeMaskWriteRegistersResponse(int address, int[]
    // registers) {
    // byte[] frame = new byte[1 + 1 + registers.length * 2];

    // frame[0] = 0x16;

    // frame[1] = (byte) (registers.length * 2);

    // for (int i = 0; i < registers.length; i++) {
    // frame[2 + i * 2] = (byte) ((registers[i] >> 8) & 0xFF);
    // frame[2 + i * 2 + 1] = (byte) ((registers[i]) & 0xFF);
    // }

    // return frame;
    // }

    // response반응
    public static byte[] makeReadHoldingRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        // PDU의 function code
        frame[0] = 0x03;

        // PDU의 data
        b.putInt(address);
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();
        b.putInt(quantity);
        frame[3] = b.get(2);
        frame[4] = b.get(3);
        return frame;

    }

    public static byte[] makeReadInputRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        // PDU의 function code
        frame[0] = 0x04;

        // PDU의 data
        b.putInt(address);
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();
        b.putInt(quantity);
        frame[3] = b.get(2);
        frame[4] = b.get(3);

        return frame;

    }

    public static byte[] makeWriteSingleRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        // PDU의 function code
        frame[0] = 0x06;

        // PDU의 data
        b.putInt(address);
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();
        b.putInt(quantity);
        frame[3] = b.get(2);
        frame[4] = b.get(3);

        return frame;

    }

    public static byte[] makeMaskWriteRegistersRequest(int address, int and_Mask, int or_Mask) {
        byte[] frame = new byte[5];
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        // PDU의 function code
        frame[0] = 0x16;

        // PDU의 data
        b.putInt(address);
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();
        b.putInt(and_Mask);
        frame[3] = b.get(2);
        frame[4] = b.get(3);

        b.clear();
        b.putInt(or_Mask);
        frame[5] = b.get(2);
        frame[6] = b.get(3);

        return frame;

    }

    public static byte[] addMBAP(int transactionId, int unitId, byte[] pdu) {
        byte[] adu = new byte[7 + pdu.length];
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        b.putInt(transactionId);

        adu[0] = b.get(2);
        adu[1] = b.get(3);
        adu[2] = 0;
        adu[3] = 0;
        adu[4] = 0;
        adu[5] = (byte) (pdu.length + 1);
        adu[6] = (byte) unitId;
        System.arraycopy(pdu, 0, adu, 7, pdu.length);
        log.info("mbap: {}", Arrays.toString(adu));
        return adu;
    }
}