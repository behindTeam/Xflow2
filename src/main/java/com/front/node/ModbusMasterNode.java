package com.front.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.front.SimpleMB;
import com.front.message.ModbusMessage;
import com.front.wire.Wire;

public class ModbusMasterNode extends InputOutputNode {
    int port;
    JSONParser parser = new JSONParser();
    Socket socket = null;

    public ModbusMasterNode() {
        this(1, 1);
    }

    public ModbusMasterNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    void preprocess() {
        //
        setInterval(1000 * 100);
    }

    @Override
    void process() {
        try (Socket socket = new Socket("127.0.0.1", 502);
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
            FileReader reader = new FileReader(
                    "src/main/java/com/front/resources/pdu.json");

            JSONObject pduObject = (JSONObject) parser.parse(reader);
            JSONObject unitId = (JSONObject) pduObject.get("unitId");

            int transactionId = 0;

            for (Object key : unitId.keySet()) {
                JSONObject keyObject = (JSONObject) unitId.get(key);
                int address = Integer.parseInt(keyObject.get("address").toString());
                int unit = Integer.parseInt(key.toString());

                Thread.sleep(20);
                byte[] request = SimpleMB.addMBAP(++transactionId, unit,
                        SimpleMB.makeReadHoldingRegistersRequest(address, 1));

                // System.out.println("request[]: " + Arrays.toString(request));

                outputStream.write(request);
                outputStream.flush();
                byte[] response = new byte[512];
                int receivedLength = inputStream.read(response, 0, response.length);

                output(new ModbusMessage(response[6], response));
                // System.out.println("response byte[]: " +
                // Arrays.toString(Arrays.copyOfRange(response, 0, receivedLength)) + "\n");
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host!!");
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    void postprocess() {
        //
    }

}