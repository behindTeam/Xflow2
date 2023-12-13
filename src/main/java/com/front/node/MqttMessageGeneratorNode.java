package com.front.node;

import java.util.Date;
import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.MyMqttMessage;

public class MqttMessageGeneratorNode extends InputOutputNode {

    public MqttMessageGeneratorNode() {
        this(1, 1);
    }

    public MqttMessageGeneratorNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message ruleMessage = getInputWire(0).get();
            if (ruleMessage instanceof JsonMessage
                    && (Objects.nonNull(((JsonMessage) ruleMessage).getPayload()))) {
                toMqttMsg((JsonMessage) ruleMessage);
            }
        }
    }

    @Override
    void postprocess() {
        //
    }

    public void toMqttMsg(JsonMessage ruleMessage) {

        JSONObject payload = ruleMessage.getPayload();
        String key = (String) payload.keySet().toArray()[0];
        JSONObject data = (JSONObject) payload.get(key);

        String[] keyParts = key.split("-");
        String sensorType = keyParts[1];
        JSONObject out = (JSONObject) data.get("out");

        String topic = "data/" + "s/" + out.get("site") + "/b/" + out.get("branch") + "/p/"
                + out.get("place") + "/" + sensorType;

        JSONObject mqttPayload = new JSONObject();
        mqttPayload.put("time", new Date().getTime());
        mqttPayload.put("value", data.get("value"));

        MyMqttMessage mqttMessage =
                new MyMqttMessage(id, topic, mqttPayload.toJSONString().getBytes());
        output(mqttMessage);
    }

    public static void main(String[] args) {
        MqttMessageGeneratorNode node = new MqttMessageGeneratorNode();
        JSONObject payload = new JSONObject();
        JSONObject inOut = new JSONObject();
        JSONObject in = new JSONObject();
        JSONObject out = new JSONObject();
        in.put("unitId", 1);
        in.put("register", 100);
        in.put("address", 1);
        out.put("site", "nhnacademy");
        out.put("branch", "gyeongnam");
        out.put("place", "class_a");
        inOut.put("in", in);
        inOut.put("out", out);
        inOut.put("value", 26);
        payload.put("24e124128c067999-temperature", inOut);
        node.toMqttMsg(new JsonMessage(payload));
    }

}

