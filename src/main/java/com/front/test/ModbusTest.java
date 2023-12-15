package com.front.test;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.front.node.ModBusMapperNode;
import com.front.node.ModbusReadNode;
import com.front.node.MqttMessageGeneratorNode;
import com.front.node.MqttOutNode;
import com.front.node.RuleEngineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class ModbusTest {
    public static void main(String[] args) {
        // ModbusServerNode server = new ModbusServerNode();
        // server.start();

        ModbusReadNode reader = new ModbusReadNode();
        ModBusMapperNode mapperNode = new ModBusMapperNode();
        RuleEngineNode ruleEngine = new RuleEngineNode();
        MqttMessageGeneratorNode mqttmessage = new MqttMessageGeneratorNode();
        MqttOutNode mqttOutNode = new MqttOutNode();
        IMqttClient hostClient = null;

        // try {
        // hostClient = new MqttClient("tcp://localhost", "hello");
        // } catch (MqttException e) {
        // e.printStackTrace();
        // }
        // mqttOutNode.setClient(hostClient);

        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();
        Wire wire3 = new BufferedWire();
        Wire wire4 = new BufferedWire();

        reader.connectOutputWire(0, wire1);
        // mapperNode.connectInputWire(0, wire1);
        // mapperNode.connectOutputWire(0, wire2);
        // ruleEngine.connectInputWire(0, wire2);
        // ruleEngine.connectOutputWire(0, wire3);
        // mqttmessage.connectInputWire(0, wire3);
        // mqttmessage.connectOutputWire(0, wire4);
        // mqttOutNode.connectInputWire(0, wire4);

        reader.start();
        // mapperNode.start();
        // ruleEngine.start();
        // mqttmessage.start();
        // mqttOutNode.start();
    }

}
