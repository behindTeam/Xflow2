package com.front.node;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    void msgParser(JsonMessage myMessage) {
        JSONObject payload = myMessage.getPayload();
        JSONParser parser = new JSONParser();

        try {
            JSONObject database = (JSONObject) parser
                    .parse(new FileReader("src/main/java/com/front/database.json"));

            for (Object fromdatabaseskey : database.keySet()) {

                // System.out.println("Key : " + key.toString());
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}

