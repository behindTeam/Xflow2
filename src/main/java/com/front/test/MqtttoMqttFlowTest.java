package com.front.test;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.front.node.MessageParsingNode;
import com.front.node.MqttInNode;
import com.front.node.MqttMessageGeneratorNode;
import com.front.node.MqttOutNode;
import com.front.node.RuleEngineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class MqtttoMqttFlowTest {
    public static void main(String[] args) {
        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();
        Wire wire3 = new BufferedWire();
        Wire wire4 = new BufferedWire();

        MqttInNode mqttInNode = new MqttInNode();
        MessageParsingNode messageParsingNode = new MessageParsingNode();
        RuleEngineNode ruleEngineNode = new RuleEngineNode();
        MqttMessageGeneratorNode mqttMessageGeneratorNode = new MqttMessageGeneratorNode();
        MqttOutNode mqttOutNode = new MqttOutNode();
        IMqttClient serverClient = null;
        IMqttClient hostClient = null;

        try {
            serverClient = new MqttClient("tcp://ems.nhnacademy.com", "hi");
            hostClient = new MqttClient("tcp://localhost", "hello");
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mqttInNode.setClient(serverClient);
        mqttOutNode.setClient(hostClient);

        mqttInNode.connectOutputWire(0, wire1);
        messageParsingNode.connectInputWire(0, wire1);
        messageParsingNode.connectOutputWire(0, wire2);
        ruleEngineNode.connectInputWire(0, wire2);
        ruleEngineNode.connectOutputWire(0, wire3);
        mqttMessageGeneratorNode.connectInputWire(0, wire3);
        mqttMessageGeneratorNode.connectOutputWire(0, wire4);
        mqttOutNode.connectInputWire(0, wire4);

        mqttInNode.start();
        messageParsingNode.start();
        ruleEngineNode.start();
        mqttMessageGeneratorNode.start();
        mqttOutNode.start();

    }
}
