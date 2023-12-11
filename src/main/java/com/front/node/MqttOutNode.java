package com.front.node;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.front.message.Message;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

public class MqttOutNode extends InputOutputNode {
    Wire inputWire;
    UUID cunnetId;
    IMqttClient client;

    public MqttOutNode() {
        this(1, 1);
    }

    public MqttOutNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    @Override
    void preprocess() {
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message myMqttMessage = getInputWire(0).get();
            if (myMqttMessage instanceof MyMqttMessage) {
                if (Objects.nonNull(((MyMqttMessage) myMqttMessage).getPayload())) {
                    publish((MyMqttMessage) myMqttMessage);
                }
            }
        }
    }

    @Override
    void postprocess() {
    }

    public void publish(MyMqttMessage inMessage) {
        cunnetId = UUID.randomUUID();
        // 객체를 재사용하기 위해 try with resources탈출
        try {
            IMqttClient localClient = client;
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            localClient.connect(options);
            localClient.publish(inMessage.getTopic(), new MqttMessage(inMessage.getPayload()));
            localClient.disconnect();
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
