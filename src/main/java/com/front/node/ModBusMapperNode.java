package com.front.node;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.ModbusMessage;
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
        try {

        } catch (Exception e) {
            // TODO: handle exception
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

    // 우리가 가져와야할 값 unitId , data // 기온 벨류값

    public void modbusMapper(ModbusMessage modbusMessage) {

        try {
            unitId = modbusMessage.getAdu()[7];
            value = modbusMessage.getAdu()[11];

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("unitId", unitId);
            jsonObject.put("value", value);



            output(new JsonMessage(jsonObject));

        } catch (Exception e) {
            // TODO: handle exception
        }


    }

}
