package com.front.node;

import java.util.Date;
import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.MyMqttMessage;
import netscape.javascript.JSObject;

public class ModbusOutMapperNode extends InputOutputNode{

    ModbusOutMapperNode(){
        this(1, 1);
    }

    ModbusOutMapperNode(int inCount, int outCount) {
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
            if (ruleMessage instanceof JsonMessage &&  (Objects.nonNull(((JsonMessage) ruleMessage).getPayload()))) {
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
        JSONObject key = (JSONObject)payload.get(payload.keySet().stream().toString());
        JSONObject in = (JSONObject)key.get("in");
        


    }
    
}
