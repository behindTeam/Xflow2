package com.front.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttClient;

import com.front.SimpleMB;
import com.front.message.ModbusMessage;
import com.front.wire.Wire;

public class ModbusReadNode extends InputOutputNode {
    Wire outputWire;
    String URI;
    byte unitId = 1;
    int port;
    int[] holdingregisters = new int[100];

    public ModbusReadNode() {
        this(1, 1);
    }

    public ModbusReadNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        try (Socket socket = new Socket("127.0.0.1", 502);
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
            // byte[] request = { 0, 1, 0, 0, 0, 6, 1, 3, 0, 0, 0, 5 };

            int unitId = 1;
            int transactionId = 0;
            byte[] request = SimpleMB.addMBAP(transactionId, unitId,
                    SimpleMB.makeReadHoldingRegistersRequest(201, 1));
            outputStream.write(request);
            outputStream.flush();

            System.out.println("request byte[]: " + Arrays.toString(request));
            byte[] response = new byte[512];
            int receivedLength = inputStream.read(response, 0, response.length);

            output(new ModbusMessage(response[7], response));
            System.out.println("response byte[]: " + Arrays.toString(Arrays.copyOfRange(response, 0,
                    receivedLength)) + "\n");

            // let frame = [];
            // let length = 0;

            // frame[length++] = ((msg.transactionId >> 8) & 0xFF);
            // frame[length++] = (msg.transactionId & 0xFF);
            // frame[length++] =0;
            // frame[length++] =0;
            // frame[length++] =0;
            // frame[length++] =6;
            // frame[length++] =msg.unitId;
            // frame[length++] =msg.functionCode;
            // frame[length++] =((msg.address >> 8) & 0xFF);
            // frame[length++] =(msg.address & 0xFF);
            // frame[length++] =((msg.payload >>8) & 0xFF);
            // frame[length++] =(msg.payload & 0xFF);

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