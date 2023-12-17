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

/**
 * RuleEngineNode 클래스입니다. {@code InputOutputNode}를 상속합니다.
 */
public class RuleEngineNode extends InputOutputNode {

    /**
     * 기본 생성자. 입력 및 출력 와이어 수가 1로 설정됩니다.
     */
    public RuleEngineNode() {
        this(1, 1);
    }

    /**
     * 입력 및 출력 와이어 수를 지정하여 노드를 생성합니다.
     *
     * @param inCount  입력 와이어 수.
     * @param outCount 출력 와이어 수.
     */
    public RuleEngineNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        // 전처리 작업
    }

    @Override
    void process() {
        // 입력 와이어에서 메시지를 받아와 RuleEngine 처리
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
        // 후처리 작업
    }

    /**
     * JSON 형식의 메시지를 받아와 RuleEngine 처리를 수행하고 결과를 출력합니다.
     *
     * @param myMessage JSON 형식의 메시지
     */
    private void msgParser(JsonMessage myMessage) {
        JSONObject payload = myMessage.getPayload();
        JSONParser parser = new JSONParser();
        String key = (String) payload.keySet().toArray()[0];

        try {
            JSONObject database = (JSONObject) parser.parse(new FileReader(
                    "src/main/java/com/front/resources/database.json"));

            for (Object fromDatabasesKey : database.keySet()) {
                if (fromDatabasesKey.toString().equals(key)) {
                    Map<String, Object> data = new HashMap<>();
                    JSONObject target = (JSONObject) database.get(key);
                    Object value = ((HashMap<?, ?>) (payload.get(key))).get("value");
                    target.replace("value", value);
                    data.put(key, target);
                    output(new JsonMessage(new JSONObject(data)));
                } else if (key.equals("payload")) {
                    JSONObject targetIn = (JSONObject) database.get(fromDatabasesKey.toString());
                    JSONObject target = (JSONObject) targetIn.get("in");
                    JSONObject receivePayload = (JSONObject) payload.get("payload");

                    if (receivePayload.get("unitId").toString().equals(target.get("unitId").toString())) {
                        Map<String, Object> data = new HashMap<>();
                        JSONObject outTarget = (JSONObject) database.get(fromDatabasesKey.toString());
                        Object ratio = target.get("ratio");
                        float value = ((Number) receivePayload.get("value")).floatValue();
                        value = (float) ((Math.round((value * ((Number) ratio).floatValue()) * 100)) / 100.0);
                        outTarget.replace("value", value);
                        data.put(fromDatabasesKey.toString(), outTarget);
                        output(new JsonMessage(new JSONObject(data)));
                    }
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
