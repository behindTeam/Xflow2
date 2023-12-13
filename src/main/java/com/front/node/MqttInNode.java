package com.front.node;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

public class MqttInNode extends InputOutputNode {
    Wire outputWire;
    IMqttClient client;
    String topicFilter;

    public MqttInNode() {
        this(1, 1);
    }

    public MqttInNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    public void setTopic(String topicFilter) {
        this.topicFilter = topicFilter;
    }

    @Override
    void preprocess() {
        outputWire = getOutputWire(0);
    }

    @Override
    void process() {
        UUID cunnetId = UUID.randomUUID();
        try (IMqttClient serverClient = client;) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);

            serverClient.connect(options);

            serverClient.subscribe(topicFilter, (topic, msg) -> {
                MyMqttMessage mqttmessage = new MyMqttMessage(cunnetId, topic, msg.getPayload());
                output(mqttmessage);
            });

            while (!Thread.currentThread().interrupted()) {
                Thread.sleep(100);
            }

            serverClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
