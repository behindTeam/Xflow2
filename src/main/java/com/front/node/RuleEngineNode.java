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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngineNode extends InputOutputNode {

    public RuleEngineNode() {
        this(1, 1);
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
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message myMessage = getInputWire(index).get();
                if (myMessage instanceof JsonMessage && (Objects.nonNull(((JsonMessage) myMessage).getPayload()))) {
                    msgParser((JsonMessage) myMessage);
                }
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
        String key = (String) payload.keySet().toArray()[0];

        try {
            JSONObject database = (JSONObject) parser.parse(new FileReader(
                    "src/main/java/com/front/resources/database.json"));
            for (Object fromdatabaseskey : database.keySet()) {
                if (fromdatabaseskey.toString().equals(key)) {
                    Map<String, Object> data = new HashMap<>();
                    JSONObject target = (JSONObject) database.get(key);
                    Object value = ((HashMap<?, ?>) (payload.get(key))).get("value");
                    target.replace("value", value);
                    data.put(key, target);
                    output(new JsonMessage(new JSONObject(data)));
                } else if (key.equals("payload")) {
                    JSONObject targetIn = (JSONObject) database.get(fromdatabaseskey.toString());
                    JSONObject target = (JSONObject) targetIn.get("in");
                    JSONObject recievePayload = (JSONObject) payload.get("payload");
                    if (recievePayload.get("unitId").toString().equals(target.get("unitId").toString())) {
                        Map<String, Object> data = new HashMap<>();
                        JSONObject outTarget = (JSONObject) database.get(fromdatabaseskey.toString());
                        Object ratio = target.get("ratio");
                        log.info("ratio : {}", ratio);
                        float value = ((Number) recievePayload.get("value")).floatValue();
                        log.info("value : {}", value);
                        value = (float) ((Math.round((value * ((Number) ratio).floatValue()) * 100)) / 100.0);
                        log.info("value : {}", value);

                        outTarget.replace("value", value);
                        data.put(fromdatabaseskey.toString(), outTarget);
                        output(new JsonMessage(new JSONObject(data)));
                    }
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}