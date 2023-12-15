package com.front.node;

import java.util.Objects;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
// import com.front.message.ModbusMessage;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

public class RuleEngine extends InputOutputNode {
    IMqttClient client;

    public RuleEngine() {
        this(1, 1);
    }

    public RuleEngine(int inCount, int outCount) {
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
            Message jsonMessage = getInputWire(0).get();
            if (jsonMessage instanceof JsonMessage) {
                if (Objects.nonNull(((JsonMessage) jsonMessage).getPayload())) {
                    System.out.println(jsonMessage.toString());
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

    public void inputMqttMessage(JsonMessage jsonMessage) {

    }

    public void inputModbusMessage(JsonMessage jsonMessage) {

    }
}