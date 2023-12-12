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

    // 여기서 해야할것은 jsonObject를 가져와서 mqtt로 오면 address파일 채워주고 나머지 반대로 하면 된다.
    // 앞에 있는 인풋와이어에있는 모든 것을 체크해서 해보자.
    // 메세지 프로세스를 만들었따


    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message jsonMessage = getInputWire(0).get();
            // if( jsonMessage )
            // if (myMqttMessage instanceof MyMqttMessage
            //         && Objects.nonNull(((MyMqttMessage) myMqttMessage).getPayload())) {
                        
            // }
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
