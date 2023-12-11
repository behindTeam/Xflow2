package com.front.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttClient;

import com.front.SimpleMB;
import com.front.wire.Wire;

public class ModbusReadNode extends InputOutputNode {
    Wire outputWire;
    IMqttClient client;
    byte unitId = 1;
    int[] holdingregisters = new int[100];

    public ModbusReadNode() {
        this(1, 1);
    }

    public ModbusReadNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        try (Socket socket = new Socket("172.19.0.1", 11502);
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
            // byte[] request = { 0, 1, 0, 0, 0, 6, 1, 3, 0, 0, 0, 5 };
            int unitId = 1;
            int transactionId = 0;
            for (int i = 0; i < 10; i++) {
                byte[] request = SimpleMB.addMBAP(++transactionId, unitId,
                        SimpleMB.makeReadHoldingRegistersRequest(0, 5));
                outputStream.write(request);
                outputStream.flush();

                byte[] response = new byte[512];
                int receivedLength = inputStream.read(response, 0, response.length);
                System.out.println(Arrays.toString(Arrays.copyOfRange(response, 0, receivedLength)));
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host!!");
        } catch (IOException e) {
            e.printStackTrace();
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
