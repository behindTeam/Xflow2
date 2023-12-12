package com.front.node;


import java.util.Objects;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.ModbusMessage;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

public class PrintTestNode extends InputOutputNode {
    Wire outputWire;
    Wire inputWire;
    IMqttClient client;
    byte value;
    byte unitId;
    int[] holdingregisters = new int[100];

    public PrintTestNode() {
        this(1, 1);
    }

    public PrintTestNode(int inCount, int outCount) {
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
            System.out.println((JsonMessage) modbusMessage);
        }
    }

    @Override
    void postprocess() {
        //
    }

    @Override
    public void run() {
        preprocess();
        process();
        postprocess();
    }

    // 우리가 가져와야할 값 unitId , data //

    public void modbusMapper(ModbusMessage modbusMessage) {

    }

}
