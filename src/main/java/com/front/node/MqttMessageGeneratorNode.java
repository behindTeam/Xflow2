package com.front.node;

import java.util.Date;
import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.MyMqttMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * MQTT 메시지를 생성하는 노드 클래스입니다. {@code InputOutputNode}를 상속합니다.
 */
@Slf4j
public class MqttMessageGeneratorNode extends InputOutputNode {

    /**
     * 기본 생성자. 입력 및 출력 와이어 수가 1로 설정됩니다.
     */
    public MqttMessageGeneratorNode() {
        this(1, 1);
    }

    /**
     * 입력 및 출력 와이어 수를 지정하여 노드를 생성합니다.
     *
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    public MqttMessageGeneratorNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        // 전처리 작업
    }

    @Override
    void process() {
        // 입력 와이어에서 ruleMessage를 받아와 MQTT 메시지로 변환
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message ruleMessage = getInputWire(index).get();
                if (ruleMessage instanceof JsonMessage
                        && (Objects.nonNull(((JsonMessage) ruleMessage).getPayload()))) {
                    toMqttMsg((JsonMessage) ruleMessage);
                }
            }
        }
    }

    @Override
    void postprocess() {
        // 후처리 작업
    }

    /**
     * JSON 형식의 ruleMessage를 받아 MQTT 메시지로 변환하여 출력합니다.
     *
     * @param ruleMessage JSON 형식의 ruleMessage
     */
    public void toMqttMsg(JsonMessage ruleMessage) {
        JSONObject payload = ruleMessage.getPayload();
        String key = (String) payload.keySet().toArray()[0];
        JSONObject data = (JSONObject) payload.get(key);

        // key에서 sensor 타입 추출
        String[] keyParts = key.split("-");
        String sensorType = keyParts[1];
        JSONObject out = (JSONObject) data.get("out");

        // MQTT topic 생성
        String topic = "data/" + "s/" + out.get("site") + "/b/" + out.get("branch") + "/p/"
                + out.get("place") + "/" + sensorType;

        // MQTT payload 생성
        JSONObject mqttPayload = new JSONObject();
        mqttPayload.put("time", new Date().getTime());
        mqttPayload.put("value", data.get("value"));

        log.info("MqttOutMessage: {}\n", mqttPayload.toJSONString());

        // MQTT 메시지 생성 및 출력
        MyMqttMessage mqttMessage = new MyMqttMessage(id, topic, mqttPayload.toJSONString().getBytes());
        output(mqttMessage);
    }
}
