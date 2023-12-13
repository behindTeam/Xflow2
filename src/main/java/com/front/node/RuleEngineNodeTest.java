package com.front.node;

import com.front.message.JsonMessage;
import org.json.simple.JSONObject;

public class RuleEngineNodeTest {

    public static void main(String[] args) {
        RuleEngineNode node = new RuleEngineNode();

        JSONObject payload = new JSONObject();
        payload.put("testKey", "testValue");

        JsonMessage message = new JsonMessage(payload);

        node.msgParser(message);
    }
}
