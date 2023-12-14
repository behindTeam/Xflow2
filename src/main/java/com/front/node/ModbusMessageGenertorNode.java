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
        byte unitId = ((Number) in.get("unitId")).byteValue();

        JSONObject modbusPayload = new JSONObject();
        modbusPayload.put("unitId", in.get("unitId"));
        modbusPayload.put("address", in.get("address"));
        modbusPayload.put("value", data.get("value"));

        ModbusMessage modbusMessage =
                new ModbusMessage(unitId, modbusPayload.toJSONString().getBytes());
        System.out.println(modbusPayload.toJSONString());
        output(modbusMessage);
    }

    public static void main(String[] args) {
        ModbusMessageGenertorNode node = new ModbusMessageGenertorNode();
        JSONObject payload = new JSONObject();
        JSONObject inOut = new JSONObject();
        JSONObject in = new JSONObject();
        JSONObject out = new JSONObject();
        in.put("unitId", 1);
        in.put("register", 100);
        in.put("address", 1);
        out.put("site", "nhnacademy");
        out.put("branch", "gyeongnam");
        out.put("place", "class_a");
        inOut.put("in", in);
        inOut.put("out", out);
        inOut.put("value", 26);
        payload.put("24e124128c067999-temperature", inOut);
        node.toModbusMsg(new JsonMessage(payload));
    }

}
