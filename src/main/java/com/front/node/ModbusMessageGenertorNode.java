package com.front.node;

import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;

import lombok.extern.slf4j.Slf4j;

/**
 * Json 형식의 메시지를 받아와 Modbus 메시지로 변환하는 노드 클래스입니다. {@code InputOutputNode}를 상속합니다.
 */
@Slf4j
public class ModbusMessageGenertorNode extends InputOutputNode {

    /**
     * 기본 생성자. 입력 및 출력 와이어 수가 1로 설정됩니다.
     */
    public ModbusMessageGenertorNode() {
        this(1, 1);
    }

    /**
     * 입력 및 출력 와이어 수를 지정하여 노드를 생성합니다.
     *
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    public ModbusMessageGenertorNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        // 전처리 작업
    }

    /**
     * 입력 와이어에서 Json 형식의 메시지를 받아와 Modbus 메시지로 변환합니다.
     * 각 입력 와이어에서 새로운 JsonMessage가 도착하면 해당 메시지를 처리하고, ModbusMessage로 변환하여 출력 와이어에
     * 전송합니다.
     */
    @Override
    void process() {
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message ruleMessage = getInputWire(index).get();
                if (ruleMessage instanceof JsonMessage
                        && (Objects.nonNull(((JsonMessage) ruleMessage).getPayload()))) {
                    toModbusMsg((JsonMessage) ruleMessage);
                }
            }
        }
    }

    @Override
    void postprocess() {
        // 후처리 작업
    }

    /**
     * JsonMessage를 ModbusMessage로 변환하여 출력 와이어에 전송합니다.
     *
     * @param ruleMessage 처리할 Json 형식의 메시지 객체.
     */
    public void toModbusMsg(JsonMessage ruleMessage) {
        JSONObject payload = ruleMessage.getPayload();
        String key = (String) payload.keySet().toArray()[0];
        JSONObject data = (JSONObject) payload.get(key);

        // Json 형식의 데이터에서 필요한 정보 추출
        JSONObject in = (JSONObject) data.get("in");

        // Modbus 메시지의 페이로드 생성
        JSONObject modbusPayload = new JSONObject();
        modbusPayload.put("unitId", in.get("unitId"));
        modbusPayload.put("address", in.get("address"));
        modbusPayload.put("value",
                (((Number) data.get("value")).floatValue() / ((Number) in.get("ratio")).floatValue()));

        // ModbusMessage로 변환
        JsonMessage modbusMessage = new JsonMessage(modbusPayload);
        log.info("modbusPayload: {}", modbusPayload.toJSONString());

        // 변환된 ModbusMessage를 출력 와이어에 전송
        output(modbusMessage);
    }
}
