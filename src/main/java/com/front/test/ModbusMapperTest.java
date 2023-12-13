package com.front.test;

import com.front.node.ModBusMapperNode;
import com.front.node.ModbusReadNode;
import com.front.node.ModbusServerNode;
import com.front.node.RuleEngine;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class ModbusMapperTest {
    public static void main(String[] args) {
        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();

        ModbusServerNode server = new ModbusServerNode();
        ModbusReadNode reader = new ModbusReadNode();
        ModBusMapperNode mapper = new ModBusMapperNode();
        RuleEngine ruleEngine = new RuleEngine();

        reader.connectOutputWire(0, wire1);
        mapper.connectInputWire(0, wire1);

        mapper.connectOutputWire(0, wire2);
        ruleEngine.connectInputWire(0, wire2);

        server.start();
        reader.start();

        mapper.start();

        ruleEngine.start();
    }

}