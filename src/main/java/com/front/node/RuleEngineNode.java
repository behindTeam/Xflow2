package com.front.node;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.front.message.JsonMessage;
import com.front.message.Message;

public class RuleEngineNode extends InputOutputNode {

    public RuleEngineNode() {
        this(1, 1);
    }

    public RuleEngineNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message myMessage = getInputWire(0).get();
            if (myMessage instanceof JsonMessage && (Objects.nonNull(((JsonMessage) myMessage).getPayload()))) {
                msgParser((JsonMessage) myMessage);

            }
        } else if ((getInputWire(1) != null) && (getInputWire(1).hasMessage())) {
            Message myMessage = getInputWire(1).get();
            if (myMessage instanceof JsonMessage &&  (Objects.nonNull(((JsonMessage) myMessage).getPayload()))) {
                    msgParser((JsonMessage) myMessage);
                
            }
        }
    }

    @Override
    void postprocess() {
        //
    }

    private void msgParser(JsonMessage myMessage) {
        JSONObject payload = myMessage.getPayload();
        JSONParser parser = new JSONParser();
        String key = (String) payload.keySet().toArray()[0];

        try {
            JSONObject database = (JSONObject) parser.parse(new FileReader("src/main/java/com/front/database.json"));
            for (Object fromdatabaseskey : database.keySet()) {
                if (fromdatabaseskey.toString().equals(key)) {
                    Map<String, Object> data = new HashMap<>();
                    JSONObject target = (JSONObject) database.get(key);
                    Object value = payload.get(key);
                    target.replace("value", value);
                    data.put(key, target);
                    System.out.println("--------->  Mqtt 메시지 입니다.");
                    System.out.println(new JSONObject(data));
                    output(new JsonMessage(new JSONObject(data)));
                } else if (key.equals("payload")) {
                    JSONObject targetIn = (JSONObject) database.get(fromdatabaseskey.toString());
                    JSONObject target = (JSONObject) targetIn.get("in");
                    JSONObject recievePayload = (JSONObject) payload.get("payload");
                    if (recievePayload.get("unitId").toString().equals(target.get("unitId").toString())) {
                        Map<String, Object> data = new HashMap<>();
                        JSONObject outTarget = (JSONObject) database.get(fromdatabaseskey.toString());
                        Object value = recievePayload.get("value");
                        outTarget.replace("value", value);
                        data.put(fromdatabaseskey.toString(), outTarget);
                        System.out.println("--------->  Modbus 메시지 입니다.");
                        System.out.println(new JSONObject(data));
                        output(new JsonMessage(new JSONObject(data)));
                    }
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        RuleEngineNode node = new RuleEngineNode();
        // --> MqttMessageParsing
        Map<String, Object> data1 = new HashMap<>();
        data1.put("24e124785c389010-temperature", 26);
        JSONObject data2 = new JSONObject(data1);
        JsonMessage messageMqtt = new JsonMessage(data2);
        node.msgParser(messageMqtt);

        // Map<String,Object> data = new HashMap<>();
        // Map<String,Object> payload = new HashMap<>();
        JSONObject data = new JSONObject();
        JSONObject payload = new JSONObject();
        data.put("unitId", 1);
        data.put("value", 66);
        payload.put("payload", data);
        JsonMessage message = new JsonMessage(new JSONObject(payload));
        node.msgParser(message);
    }

}
