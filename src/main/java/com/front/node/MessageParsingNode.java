package com.front.node;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageParsingNode extends InputOutputNode {
    Wire settingWire;
    Wire mqttWire;
    Message message;
    JSONParser parser;
    JSONObject settings;

    public MessageParsingNode() {
        this(1, 1);
    }

    public MessageParsingNode(int inCount, int outCount) {
        super(inCount, outCount);
        parser = new JSONParser();
    }

    public void configureSettings(JSONObject settings) {
        this.settings = settings;
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message myMqttMessage = getInputWire(index).get();
                if (myMqttMessage instanceof MyMqttMessage
                        && (Objects.nonNull(((MyMqttMessage) myMqttMessage).getPayload()))) {
                    messageParsing((MyMqttMessage) myMqttMessage);

                }
            }
        }
    }

    @Override
    void postprocess() {
        //
    }

    // Todo: messageParsing메소드가 똑같은 결과를 세번 output하는 현상 수정할 것
    public void messageParsing(MyMqttMessage myMqttMessage) {
        try {
            JSONObject payload = (JSONObject) parser.parse(new String(myMqttMessage.getPayload()));
            JSONObject deviceInfo = (JSONObject) payload.get("deviceInfo");
            JSONObject object = (JSONObject) payload.get("object");

            if (object != null) {
                for (Object sensorType : object.keySet()) {
                    if (deviceInfo.get("applicationName").equals(settings.get("applicationName"))) {
                        String sensor = (String) settings.get("sensor");
                        if (settings.get("sensor") != null) {
                            if (sensor.contains(sensorType.toString())) {
                                Map<String, Object> data = new HashMap<>();
                                Map<String, Object> outMessage = new HashMap<>();
                                data.put("value", object.get(sensorType));
                                outMessage.put(deviceInfo.get("devEui") + "-" + sensorType.toString(), data);
                                output(new JsonMessage(new JSONObject(outMessage)));
                                log.info(outMessage.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
