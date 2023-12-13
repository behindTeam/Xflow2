package com.front.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.ModbusMessage;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

public class ModBusMapperNode extends InputOutputNode {
    Wire outputWire;
    Wire inputWire;
    IMqttClient client;
    byte value;
    byte unitId;
    int[] holdingregisters = new int[100];

    public ModBusMapperNode() {
        this(1, 1);
    }

    public ModBusMapperNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message modbusMessage = getInputWire(0).get();
            if (modbusMessage instanceof ModbusMessage) {
                if (Objects.nonNull(((ModbusMessage) modbusMessage).getAdu())) {
                    modbusMapper((ModbusMessage) modbusMessage);
                }
            }
        }
    }

    @Override
    void postprocess() {
        //
    }

    // @Override
    // public void run() {
    // preprocess();
    // process();
    // postprocess();
    // }

    public void modbusMapper(ModbusMessage modbusMessage) {

        try {
            unitId = modbusMessage.getAdu()[7];
            value = modbusMessage.getAdu()[11];

            Map<String, Object> data = new HashMap<>();
            Map<String, Object> sendMessage = new HashMap<>();

            data.put("unitId", unitId);
            data.put("value", value);
            sendMessage.put("payload", data);

            JSONObject jsonMessage = new JSONObject(sendMessage);

            output(new JsonMessage(jsonMessage));
            // System.out.println(jsonMessage.toString());
        } catch (Exception e) {
        }

    }
}
