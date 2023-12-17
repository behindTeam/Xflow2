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

    String applicationName;

    String[] sensor;
    JSONParser parser;
    JSONObject settings;

    /**
     * 기본 생성자로, 입력 및 출력 와이어 개수를 기본값으로 설정
     */
    public MessageParsingNode() {
        this(1, 1);
    }

    /**
     * 입력 및 출력 와이어 개수를 지정하여 생성하는 생성자
     * 
     * @param inCount  입력 와이어 개수
     * @param outCount 출력 와이어 개수
     */
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

    /**
     * 메시지 처리 메서드로, MQTT 메시지를 파싱하여 필요한 정보를 추출하고 출력 메시지를 생성
     */
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

    /**
     * 후처리 메서드 (구현 x)
     */
    @Override
    void postprocess() {
        //
    }

    /**
     * MQTT 메시지를 파싱하여 필요한 정보를 추출하고, 설정된 조건에 따라 출력 메시지를 생성하여 전송
     * 
     * @param myMqttMessage 파싱할 MQTT 메시지 객체
     */
    public void messageParsing(MyMqttMessage myMqttMessage) {
        try {
            JSONObject payload = (JSONObject) parser.parse(new String(myMqttMessage.getPayload()));

            JSONObject deviceInfo = (JSONObject) payload.get("deviceInfo");
            JSONObject object = (JSONObject) payload.get("object");

            String commonTopic = "data";

            if (deviceInfo != null) {
                Object tag = deviceInfo.get("tags");
                if (tag instanceof JSONObject) {
                    for (Object key : ((JSONObject) tag).keySet()) {
                        switch (key.toString()) {
                            case "site":
                                commonTopic += "/s/" + ((JSONObject) tag).get("site");
                                break;
                            case "name":
                                commonTopic += "/n/" + ((JSONObject) tag).get("name");
                                break;
                            case "branch":
                                commonTopic += "/b/" + ((JSONObject) tag).get("branch");
                                break;
                            case "place":
                                commonTopic += "/p/" + ((JSONObject) tag).get("place");
                                break;
                            default:
                        }
                    }
                }

            }

            long currentTime = new Date().getTime();

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
