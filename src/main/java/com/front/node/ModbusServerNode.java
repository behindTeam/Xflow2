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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusServerNode extends InputOutputNode {
    Map<Integer, JSONObject> map = new HashMap<>();
    ServerSocket modbusServerSocket;
    Consumer<Socket> clientConsumer;
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
            modbusServerSocket = new ServerSocket(11502);
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPoolExecutor = new ThreadPoolExecutor(10, 30, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20));
        clientConsumer = socket -> {
            try {
                generateResponse(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        consumer = serverSocket -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPoolExecutor.execute(() -> clientConsumer.accept(clientSocket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    void process() {
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message unitDataMessage = getInputWire(index).get();
                if (unitDataMessage instanceof JsonMessage
                        && (Objects.nonNull(((JsonMessage) unitDataMessage).getPayload()))) {
                    JSONObject unitData = ((JsonMessage) unitDataMessage).getPayload();
                    JSONObject object = new JSONObject();
                    object.put("address", unitData.get("address"));
                    object.put("value", unitData.get("value"));

                    map.put(((Long) unitData.get("unitId")).intValue(), object);
                    // log.info("mapdata : {}", map.toString());
                }
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
        threadPoolExecutor.execute(() -> consumer.accept(modbusServerSocket));
        // consumer.accept(modbusServerSocket);

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

    public void generateResponse(Socket socket) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
        while (socket.isConnected()) {
            byte[] inputBuffer = new byte[1024];

            int receiveLength = inputStream.read(inputBuffer, 0, inputBuffer.length);

            if (receiveLength > 0) {
                log.info("modbusRequestToSlave: {}",
                        Arrays.toString(Arrays.copyOfRange(inputBuffer, 0, receiveLength)));

                if ((receiveLength > 7) && (6 + inputBuffer[5] == receiveLength)) {
                    int unitId = inputBuffer[6];
                    int transactionId = (inputBuffer[0] << 8) | inputBuffer[1];
                    int functionCode = inputBuffer[7];
                    int address = (inputBuffer[8] << 8) | inputBuffer[9];
                    int quantity = (inputBuffer[10] << 8) | inputBuffer[11];

                    switch (functionCode) {
                        case 3:
                            int[] holdingregisters = new int[address + 100];
                            int unitAddress = ((Long) map.get(unitId).get("address")).intValue();
                            int unitValue = ((Float) map.get(unitId).get("value")).intValue();

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