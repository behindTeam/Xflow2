package com.front.node;

import java.util.Objects;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.ModbusMessage;
import com.front.wire.Wire;

/**
 * ModBus 메시지를 처리하고 MQTT 메시지로 매핑하는 노드 클래스입니다. {@code InputOutputNode}를 상속합니다.
 */
public class ModBusMapperNode extends InputOutputNode {
    /** 출력 와이어 객체 */
    Wire outputWire;

    /** 입력 와이어 객체 */
    Wire inputWire;

    /** MQTT 클라이언트 */
    IMqttClient client;

    /** ModBus 메시지의 값 */
    int value;

    /** ModBus 메시지의 유닛 ID */
    byte unitId;

    /** ModBus 레지스터를 저장하는 배열 */
    int[] holdingregisters = new int[100];

    /**
     * 기본 생성자. 입력 및 출력 와이어 수가 1로 설정됩니다.
     */
    public ModBusMapperNode() {
        this(1, 1);
    }

    /**
     * 입력 및 출력 와이어 수를 지정하여 노드를 생성합니다.
     *
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    public ModBusMapperNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    /**
     * MQTT 클라이언트를 설정합니다.
     *
     * @param client 설정할 MQTT 클라이언트 객체.
     */
    public void setClient(IMqttClient client) {
        this.client = client;
    }

    @Override
    void preprocess() {
        // 전처리 작업
    }

    /**
     * 입력 와이어에서 ModBus 메시지를 받아와서 처리합니다.
     * 각 입력 와이어에서 새로운 ModBus 메시지가 도착하면 해당 메시지를 처리하고, MQTT 메시지로 변환하여 출력 와이어에 전송합니다.
     * ModBus 메시지에서 유닛 ID와 값(10번째 및 11번째 바이트)을 추출하여 MQTT 메시지의 payload에 저장하고,
     * JsonMessage로 변환합니다. 변환된 JsonMessage는 출력 와이어에 전송되어 다른 노드로 전달됩니다.
     */
    @Override
    void process() {
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message modbusMessage = getInputWire(index).get();
                if (modbusMessage instanceof ModbusMessage) {
                    if (Objects.nonNull(((ModbusMessage) modbusMessage).getAdu())) {
                        modbusMapper((ModbusMessage) modbusMessage);
                    }
                }
            }
        }
    }

    @Override
    void postprocess() {
        // 후처리 작업
    }

    /**
     * ModBus 메시지를 처리하고 MQTT 메시지로 매핑합니다.
     *
     * @param modbusMessage 처리할 ModBus 메시지 객체.
     */
    public void modbusMapper(ModbusMessage modbusMessage) {
        try {
            unitId = modbusMessage.getUnitId();
            value = (modbusMessage.getAdu()[9] & 0xFF) * 256 + (modbusMessage.getAdu()[10] & 0xFF);

            JSONObject data = new JSONObject();
            JSONObject sendMessage = new JSONObject();

            data.put("unitId", unitId);
            data.put("value", value);
            sendMessage.put("payload", data);

            JSONObject jsonMessage = new JSONObject(sendMessage);

            output(new JsonMessage(jsonMessage));
            System.out.println("받은 데이터: " + jsonMessage.toString());
        } catch (Exception e) {
            // 예외 처리
        }
    }
}
