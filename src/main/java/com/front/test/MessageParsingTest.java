package com.front.test;

import com.front.message.Message;
import com.front.message.StringArrayMessage;
import com.front.node.MessageParsingNode;
import com.front.node.MqttInNode;
import com.front.node.MqttOutNode;
import com.front.node.ProcessCommandLineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class MessageParsingTest {

    public static void main(String[] args) throws InterruptedException {

        Message argMessage = new StringArrayMessage(args);
        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();
        Wire wire3 = new BufferedWire();
        Wire wire4 = new BufferedWire();

        ProcessCommandLineNode node = new ProcessCommandLineNode();
        MqttInNode mqttInNode = new MqttInNode();
        MessageParsingNode msgParsingNode = new MessageParsingNode();
        MqttOutNode mqttOutNode = new MqttOutNode();

        wire1.put(argMessage);

        node.connectInputWire(0, wire1);
        node.connectOutputWire(0, wire2);
        mqttInNode.connectOutputWire(0, wire3);
        msgParsingNode.connectInputWire(0, wire2);
        msgParsingNode.connectInputWire(1, wire3);
        msgParsingNode.connectOutputWire(0, wire4);
        mqttOutNode.connectInputWire(0, wire4);

        node.start();
        node.join();
        mqttInNode.start();
        msgParsingNode.start();
        mqttOutNode.start();
    }

}
