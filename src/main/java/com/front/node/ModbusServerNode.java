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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.json.simple.JSONObject;

import com.front.SimpleMB;
import com.front.message.JsonMessage;
import com.front.message.Message;

import lombok.extern.slf4j.Slf4j;

/**
 * Modbus 프로토콜을 사용하여 데이터를 읽거나 쓰는 서버 노드 클래스입니다. {@code InputOutputNode}를 상속합니다.
 */
@Slf4j
public class ModbusServerNode extends InputOutputNode {
    /** 각 유닛에 대한 데이터를 저장하는 맵 */
    Map<Integer, JSONObject> map = new HashMap<>();

    /** Modbus 서버 소켓 */
    ServerSocket modbusServerSocket;

    /** 클라이언트 소켓을 처리할 Consumer 객체 */
    Consumer<Socket> clientConsumer;

    /** 서버 소켓을 처리할 Consumer 객체 */
    Consumer<ServerSocket> consumer;

    /** 스레드 풀 실행자 */
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * 기본 생성자. 입력 및 출력 와이어 수가 1로 설정됩니다.
     */
    public ModbusServerNode() {
        this(1, 1);
    }

    /**
     * 입력 및 출력 와이어 수를 지정하여 노드를 생성합니다.
     *
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    public ModbusServerNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        try {
            // Modbus 서버 소켓 생성
            modbusServerSocket = new ServerSocket(11502);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 스레드 풀 실행자 및 Consumer 객체 초기화
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
                    // 클라이언트 소켓을 스레드 풀에 넣어 비동기적으로 처리
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
        // 입력 와이어에서 유닛 데이터를 받아와 맵에 저장
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
                }
            }
        }
    }

    @Override
    void postprocess() {
        // 후처리 작업
    }

    @Override
    public void run() {
        preprocess();

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;
        threadPoolExecutor.execute(() -> consumer.accept(modbusServerSocket));

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

    /**
     * 클라이언트 소켓으로부터 요청을 받아 응답을 생성합니다.
     *
     * @param socket 클라이언트 소켓
     * @throws IOException 입출력 예외
     */
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
                                // Modbus 응답 생성 및 클라이언트에게 전송
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
        }
    }
}
