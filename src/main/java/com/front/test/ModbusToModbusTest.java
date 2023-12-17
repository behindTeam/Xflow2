package com.front.test;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.front.node.ModBusMapperNode;
import com.front.node.ModbusMessageGenertorNode;
import com.front.node.ModbusMasterNode;
import com.front.node.ModbusServerNode;
import com.front.node.MqttMessageGeneratorNode;
import com.front.node.MqttOutNode;
import com.front.node.RuleEngineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class ModbusToModbusTest {
    public static void main(String[] args) {

        ModbusMasterNode reader = new ModbusMasterNode();
        ModBusMapperNode mapperNode = new ModBusMapperNode();
        RuleEngineNode ruleEngine = new RuleEngineNode();
        ModbusMessageGenertorNode modbusMessageGenerator = new ModbusMessageGenertorNode();
        ModbusServerNode server = new ModbusServerNode();
        // IMqttClient hostClient = null;

        // try {
        // hostClient = new MqttClient("tcp://localhost", "hello");
        // } catch (MqttException e) {
        // e.printStackTrace();
        // }
        // mqttOutNode.setClient(hostClient);
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 502);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.setClient(socket);
        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();
        Wire wire3 = new BufferedWire();
        Wire wire4 = new BufferedWire();
        Wire wire5 = new BufferedWire();

        reader.connectOutputWire(0, wire1);
        mapperNode.connectInputWire(0, wire1);
        mapperNode.connectOutputWire(0, wire2);
        ruleEngine.connectInputWire(0, wire2);
        ruleEngine.connectInputWire(1, wire5);
        ruleEngine.connectOutputWire(0, wire3);
        modbusMessageGenerator.connectInputWire(0, wire3);
        modbusMessageGenerator.connectOutputWire(0, wire4);
        server.connectInputWire(0, wire4);

        reader.start();
        mapperNode.start();
        ruleEngine.start();
        modbusMessageGenerator.start();
        server.start();
    }

}
