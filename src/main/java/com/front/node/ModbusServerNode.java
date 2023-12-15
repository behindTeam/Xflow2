package com.front.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;

import com.front.wire.Wire;
import com.front.SimpleMB;
import com.front.message.JsonMessage;
import com.front.message.Message;

public class ModbusServerNode extends InputOutputNode {
    Wire outputWire;
    IMqttClient client;
    Map<Integer, JSONObject> map = new HashMap<>();

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
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message unitDataMessage = getInputWire(0).get();
            if (unitDataMessage instanceof JsonMessage
                    && (Objects.nonNull(((JsonMessage) unitDataMessage).getPayload()))) {
                JSONObject unitData = ((JsonMessage) unitDataMessage).getPayload();
                JSONObject object = new JSONObject();
                object.put("address", unitData.get("address"));
                object.put("value", unitData.get("value"));

                map.put((Integer) unitData.get("unitId"), object);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(11502)) {
            generateResponse(serverSocket);

        } catch (IOException e) {
            System.err.println(e.getMessage());
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

    public void generateResponse(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        while (socket.isConnected()) {
            byte[] inputBuffer = new byte[1024];

            int receiveLength = inputStream.read(inputBuffer, 0, inputBuffer.length);

            if (receiveLength > 0) {
                System.out.println(Arrays.toString(Arrays.copyOfRange(inputBuffer, 0, receiveLength)));

                if ((receiveLength > 7) && (6 + inputBuffer[5] == receiveLength)) {
                    int unitId = inputBuffer[6];
                    int transactionId = (inputBuffer[0] << 8) | inputBuffer[1];
                    int functionCode = inputBuffer[7];
                    int address = (inputBuffer[8] << 8) | inputBuffer[9];
                    int quantity = (inputBuffer[10] << 8) | inputBuffer[11];

                    switch (functionCode) {
                        case 3:
                            int[] holdingregisters = new int[address + 100];
                            int unitAddress = (int) map.get("unitId").get("address");
                            int unitValue = (int) map.get("value").get("value");

                            holdingregisters[unitAddress] = unitValue; // ?? 얘가 Int가 되는게 맞을까? (나중에 확인)
                            if (address + quantity < holdingregisters.length) {
                                outputStream.write(SimpleMB.addMBAP(transactionId, unitId,
                                        SimpleMB.makeReadHoldingRegisterResponse(address,
                                                Arrays.copyOfRange(holdingregisters, address, quantity))));
                                outputStream.flush();
                            }
                            break;

                        case 4:
                            if (address + quantity < holdingregisters.length) {
                                outputStream.write(SimpleMB.addMBAP(transactionId, unitId,
                                        SimpleMB.makeReadInputRegistersRequest(address, quantity)));
                                outputStream.flush();
                            }
                            break;
                        case 6:
                            if (address + quantity < holdingregisters.length) {
                                outputStream.write(SimpleMB.addMBAP(transactionId, unitId,
                                        SimpleMB.makeWriteSingleRegistersRequest(address, quantity)));
                                outputStream.flush();
                            }
                            break;
                        case 16:
                            if (address + quantity < holdingregisters.length) {

                                // outputStream.write(SimpleMB.addMBAP(transactionId, unitId,
                                // SimpleMB.makeWriteSingleRegistersRequest(address,)));
                                // outputStream.flush();
                            }
                            break;
                    }
                }
            } else if (receiveLength < 0) {
                break;
            }

        }
    }
}