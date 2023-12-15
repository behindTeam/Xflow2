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
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;

import com.front.SimpleMB;
import com.front.message.JsonMessage;
import com.front.message.Message;

public class ModbusServerNode extends InputOutputNode {
    Map<Integer, JSONObject> map = new HashMap<>();
    ServerSocket serverSocket;
    Consumer<ServerSocket> consumer;
    ThreadPoolExecutor threadPoolExecutor;

    public ModbusServerNode() {
        this(1, 1);
    }

    public ModbusServerNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        try {
            serverSocket = new ServerSocket(11502);
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPoolExecutor = new ThreadPoolExecutor(10, 30, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20));
        consumer = socket -> {
            try {
                generateResponse(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
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
    }

    @Override
    void postprocess() {
        //
    }

    @Override
    public void run() {
        preprocess();

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;
        threadPoolExecutor.execute(() -> consumer.accept(serverSocket));

        while (isAlive()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;

            if (elapsedTime < interval) {
                try {
                    process();
                    Thread.sleep(interval - elapsedTime);
                } catch (InterruptedException e) {
                    stop();
                }
            }

            previousTime = startTime + (System.currentTimeMillis() - startTime) / interval * interval;
        }

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
                            int unitAddress = (int) map.get(unitId).get("address");
                            int unitValue = (int) map.get(unitId).get("value");

                            holdingregisters[unitAddress] = unitValue;
                            if (address + quantity < holdingregisters.length) {
                                outputStream.write(SimpleMB.addMBAP(transactionId, unitId,
                                        SimpleMB.makeReadHoldingRegisterResponse(address,
                                                Arrays.copyOfRange(holdingregisters, address, address + quantity))));
                                outputStream.flush();
                            }
                            break;
                    }
                }
            } else if (receiveLength < 0) {
                break;
            }

            // socket.close();

        }
    }
}