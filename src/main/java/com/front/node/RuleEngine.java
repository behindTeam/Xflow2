package com.front.node;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class RuleEngine extends InputOutputNode {
    Map<Integer, JSONObject> database = new HashMap<>();

    public RuleEngine() {
        this(1, 1);
    }

    public RuleEngine(int inCount, int outCount) {
        super(inCount, outCount);
    }

    @Override
    void preprocess() {
        //
    }

    @Override
    void process() {

    }

    @Override
    void postprocess() {
        //
    }

    @Override
    public void run() {
        preprocess();
        process();
        postprocess();
    }

}
