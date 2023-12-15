package com.front.node;

import java.util.Date;
import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.ModbusMessage;
import com.front.message.MyMqttMessage;

public class ModbusMessageGenertorNode extends InputOutputNode {

    public ModbusMessageGenertorNode() {
        this(1, 1);
    }

    public ModbusMessageGenertorNode(int inCount, int outCount) {
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
                toModbusMsg((JsonMessage) ruleMessage);
            }
        }
    }

    @Override
    void postprocess() {
        //
    }

    public void toModbusMsg(JsonMessage ruleMessage) {

        JSONObject payload = ruleMessage.getPayload();
        String key = (String) payload.keySet().toArray()[0];
        JSONObject data = (JSONObject) payload.get(key);

        JSONObject in = (JSONObject) data.get("in");

        JSONObject modbusPayload = new JSONObject();
        modbusPayload.put("unitId", in.get("unitId"));
        modbusPayload.put("address", in.get("address"));
        modbusPayload.put("value", data.get("value"));

        JsonMessage modbusMessage = new JsonMessage(modbusPayload);
        System.out.println(modbusPayload.toJSONString());
        output(modbusMessage);
    }
}
