package com.front.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.Message;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

public class RuleEngineNode extends InputOutputNode {
    Wire outputWire;
    Wire inputWire;
    Map<Integer, JSONObject> database = new HashMap<>();
    Message message;

    public RuleEngineNode() {
        this(2, 2);
    }

    public RuleEngineNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message myMqttMessage = getInputWire(0).get();
            if (myMqttMessage instanceof MyMqttMessage
                    && Objects.nonNull(((MyMqttMessage) myMqttMessage).getPayload())) {

            }
        }
    }

    @Override
    synchronized void postprocess() {
        //
    }

    @Override
    public void run() {
        preprocess();
        process();
        postprocess();
    }

}
