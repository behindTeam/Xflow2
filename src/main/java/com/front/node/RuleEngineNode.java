package com.front.node;

import java.util.Objects;
import org.json.simple.JSONObject;
import com.front.message.JsonMessage;
import com.front.message.Message;

public class RuleEngineNode extends InputOutputNode {

    RuleEngineNode() {
        this(1, 1);
    }

    RuleEngineNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message myMessage = getInputWire(0).get();
            if (myMessage instanceof JsonMessage
                    && (Objects.nonNull(((JsonMessage) myMessage).getPayload()))) {
                msgParser((JsonMessage) myMessage);

            }
        }
    }

    @Override
    void postprocess() {
        //
    }

    private void msgParser(JsonMessage myMessage) {
        JSONObject payload = myMessage.getPayload();
    }

}
