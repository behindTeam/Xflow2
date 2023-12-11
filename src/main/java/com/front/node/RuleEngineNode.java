package com.front.node;

import com.front.message.Message;
import com.front.message.MyMqttMessage;

public class RuleEngineNode extends InputOutputNode{

    RuleEngineNode(){
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
            Message mytMessage = getInputWire(0).get();
            msgParser(mytMessage);
        }
    }

    @Override
    void postprocess() {
        //
    }

    private void msgParser(Message mytMessage) {
        if (mytMessage instanceof MyMqttMessage) {
             
        }
    }
}
