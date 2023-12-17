package com.front.test;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.front.node.ModBusMapperNode;
import com.front.node.ModbusMasterNode;
import com.front.node.ModbusServerNode;
import com.front.node.MqttMessageGeneratorNode;
import com.front.node.MqttOutNode;
import com.front.node.RuleEngineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class ModbusToRuleTest {
    public static void main(String[] args) {
        ModbusMasterNode reader = new ModbusMasterNode();
        ModBusMapperNode mapperNode = new ModBusMapperNode();
        RuleEngineNode ruleEngine = new RuleEngineNode();

        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();
        Wire wire3 = new BufferedWire();
        Wire wire5 = new BufferedWire();

        reader.connectOutputWire(0, wire1);
        mapperNode.connectInputWire(0, wire1);
        mapperNode.connectOutputWire(0, wire2);
        ruleEngine.connectInputWire(0, wire2);
        ruleEngine.connectInputWire(1, wire5);
        ruleEngine.connectOutputWire(0, wire3);

        reader.start();
        mapperNode.start();
        ruleEngine.start();

    }

}
