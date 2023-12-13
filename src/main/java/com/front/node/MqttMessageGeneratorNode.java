package com.front.node;

import java.util.Date;
import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.MyMqttMessage;

public class MqttMessageGeneratorNode extends InputOutputNode {

    MqttMessageGeneratorNode() {
        this(1, 1);
    }

    MqttMessageGeneratorNode(int inCount, int outCount) {
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

        String topic = "data/" + data.get("site") + "/" + data.get("branch") + "/"
                + data.get("place") + "/" + key;

        JSONObject mqttPayload = new JSONObject();
        mqttPayload.put("time", new Date().getTime());
        mqttPayload.put("value", data.get("value"));

        MyMqttMessage mqttMessage = new MyMqttMessage(id, null, null);

        mqttMessage.setTopic(topic);
        mqttMessage.setPayload(mqttPayload.toJSONString().getBytes());

        System.out.println(mqttMessage.toString());
        output(mqttMessage);
    }

}

