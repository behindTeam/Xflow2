package com.front.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.front.SimpleMB;
import com.front.message.ModbusMessage;

public class ModbusMasterNode extends InputOutputNode {
    int port;

    /** JSON 파서 객체 */
    JSONParser parser = new JSONParser();
    Socket socket = null;

    public ModbusMasterNode() {
        this(1, 1);
    }

    public ModbusMasterNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    /**
     * Modbus 통신에 사용될 포트 번호를 설정합니다.
     *
     * @param port 설정할 포트 번호.
     */
    public void setPort(int port) {
        this.port = port;
    }

    public void setClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    void preprocess() {
        //
        setInterval(1000 * 10);
    }

    /**
     * Modbus 프로토콜을 사용하여 데이터를 읽어옵니다.
     * 주어진 JSON 파일에서 읽은 설정을 기반으로 Modbus 통신을 수행하고, 결과를 ModbusMessage로 변환하여 출력 와이어에
     * 전송합니다.
     */
    @Override
    void process() {
        try {
            Socket socket = this.socket;
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

            // JSON 파일에서 설정 읽기
            FileReader reader = new FileReader("src/main/java/com/front/resources/pdu.json");
            JSONObject pduObject = (JSONObject) parser.parse(reader);
            JSONObject unitId = (JSONObject) pduObject.get("unitId");
            int transactionId = 0;

            // 각 유닛에 대해 Modbus 통신 수행
            for (Object key : unitId.keySet()) {
                JSONObject keyObject = (JSONObject) unitId.get(key);
                int address = Integer.parseInt(keyObject.get("address").toString());
                int unit = Integer.parseInt(key.toString());

                Thread.sleep(20);
                byte[] request = SimpleMB.addMBAP(++transactionId, unit,
                        SimpleMB.makeReadHoldingRegistersRequest(address, 1));

                outputStream.write(request);
                outputStream.flush();

                byte[] response = new byte[512];
                int receivedLength = inputStream.read(response, 0, response.length);

                // 응답을 ModbusMessage로 변환하여 출력 와이어에 전송
                output(new ModbusMessage(response[6], response));
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host!!");
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    void postprocess() {
        // 후처리 작업
    }
}
