package com.front.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttClient;

import com.front.wire.Wire;
import com.front.SimpleMB;

public class ModbusServerNode extends InputOutputNode {
    Wire outputWire;
    IMqttClient client;
    byte unitId = 1;
    int[] holdingregisters = new int[100];

    public ModbusServerNode() {
        this(1, 1);
    }

    public ModbusServerNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    @Override
    void preprocess() {
        outputWire = getOutputWire(0);
        for (int i = 0; i < holdingregisters.length; i++) {
            holdingregisters[i] = i;
        }
    }

    @Override
    void process() {
        try (ServerSocket serverSocket = new ServerSocket(11502)) {
            Socket socket = serverSocket.accept();
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());

            while (socket.isConnected()) {
                byte[] inputBuffer = new byte[1024];

                int receiveLength = inputStream.read(inputBuffer, 0, inputBuffer.length);

                if (receiveLength > 0) {
                    System.out.println(Arrays.toString(Arrays.copyOfRange(inputBuffer, 0, receiveLength)));

                    if ((receiveLength > 7) && (6 + inputBuffer[5] == receiveLength)) {
                        if (unitId == inputBuffer[6]) {
                            int transactionId = inputBuffer[0] << 8 | inputBuffer[1];
                            int functionCode = inputBuffer[7];

                            switch (functionCode) {
                                case 3:
                                    int address = (inputBuffer[8] << 8) | inputBuffer[9];
                                    int quantity = (inputBuffer[10] << 8) | inputBuffer[11];

                                    if (address + quantity < holdingregisters.length) {
                                        System.out.println("address : " + address + ", quantitiy : " + quantity);

                                        outputStream.write(SimpleMB.addMBAP(transactionId, unitId,
                                                SimpleMB.makeReadHoldingRegisterResponse(address,
                                                        Arrays.copyOfRange(holdingregisters, address, quantity))));
                                        outputStream.flush();
                                    }
                                    break;

                                case 4:

                                default:
                            }
                        }

                    }
                } else if (receiveLength < 0) {
                    break;
                }

            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    void postprocess() {
        //
    }

    @Override
    public void run() {
        preprocess();
        process();
        postprocess();
    }
}
