package com.front.test;

import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.StringArrayMessage;
import com.front.node.MessageParsingNode;
import com.front.node.ProcessCommandLineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Message argMessage = new StringArrayMessage(args);
        Wire wire1 = new BufferedWire();
        wire1.put(argMessage);
        Wire wire2 = new BufferedWire();

        ProcessCommandLineNode node = new ProcessCommandLineNode();
        MessageParsingNode msgNode = new MessageParsingNode();
        node.connectInputWire(0, wire1);
        node.connectOutputWire(0, wire2);
        node.start();
        node.join();
        JsonMessage message = (JsonMessage) wire2.get();
        msgNode.connectInputWire(0, wire2);

    }
}
