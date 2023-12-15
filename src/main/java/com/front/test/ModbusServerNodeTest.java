package com.front.test;

import org.json.simple.JSONObject;

import com.front.message.JsonMessage;
import com.front.node.ModbusServerNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class ModbusServerNodeTest {
    public static void main(String[] args) {
        Wire wire = new BufferedWire();
        ModbusServerNode node = new ModbusServerNode();

        JSONObject modbusPayload = new JSONObject();
        modbusPayload.put("unitId", 2);
        modbusPayload.put("address", 101);
        modbusPayload.put("value", 35);

        JsonMessage modbusMessage = new JsonMessage(modbusPayload);
        wire.put(modbusMessage);

        node.connectInputWire(0, wire);

        node.start();
    }
}
