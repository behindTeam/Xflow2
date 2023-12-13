package com.front.node;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.front.message.JsonMessage;
import com.front.message.Message;

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
            Message myMessage = getInputWire(0).get();
            if (myMessage instanceof JsonMessage &&  (Objects.nonNull(((JsonMessage) myMessage).getPayload()))) {
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
        JSONParser parser = new JSONParser();
        String key = (String)payload.keySet().toArray()[0];

        try {
            JSONObject database = (JSONObject) parser.parse(new FileReader("src/main/java/com/front/database.json"));
            for (Object fromdatabaseskey : database.keySet()) {
                if (fromdatabaseskey.toString().equals(key)) {
                    Map<String,Object> data = new HashMap<>();
                    data.put(key, database.get(key));
                    System.out.println(new JSONObject(data));
                    output(new JsonMessage(new JSONObject(data)));
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        RuleEngineNode node = new RuleEngineNode();
        Map<String,Object> data1 = new HashMap<>();
        data1.put("24e124785c389010-temperature", 13);
        JSONObject data2 = new JSONObject(data1);
        JsonMessage message = new JsonMessage(data2);
        node.msgParser(message);
    }
        
}
