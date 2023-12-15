package com.front.test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.front.node.MessageParsingNode;
import com.front.node.ModbusMessageGenertorNode;
import com.front.node.ModbusServerNode;
import com.front.node.MqttInNode;
import com.front.node.RuleEngineNode;
import com.front.wire.BufferedWire;
import com.front.wire.Wire;

public class MqtttoModbusFlowTest {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray;
        JSONObject settings = null;
        try (Reader reader = new FileReader("src/main/java/com/front/settings.json")) {
            jsonArray = (JSONArray) parser.parse(reader);
            settings = (JSONObject) ((JSONArray) (jsonArray.get(1))).get(0);
        } catch (IOException | ParseException e1) {
            e1.printStackTrace();
        }

        Wire wire1 = new BufferedWire();
        Wire wire2 = new BufferedWire();
        Wire wire3 = new BufferedWire();
        Wire wire4 = new BufferedWire();
        Wire wire5 = new BufferedWire();

        MqttInNode mqttInNode = new MqttInNode();
        MessageParsingNode messageParsingNode = new MessageParsingNode();
        RuleEngineNode ruleEngineNode = new RuleEngineNode();
        ModbusMessageGenertorNode modbusMessageGenertorNode = new ModbusMessageGenertorNode();
        ModbusServerNode modbusServerNode= new ModbusServerNode();
        IMqttClient serverClient = null;
        IMqttClient hostClient = null;

        try {
            serverClient = new MqttClient("tcp://ems.nhnacademy.com", "hi");
            hostClient = new MqttClient("tcp://localhost", "hello");
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mqttInNode.setClient(serverClient);
        mqttInNode.setTopic("application/#");
        messageParsingNode.configureSettings(settings);

        mqttInNode.connectOutputWire(0, wire1);
        messageParsingNode.connectInputWire(0, wire1);
        messageParsingNode.connectOutputWire(0, wire2);
        ruleEngineNode.connectInputWire(0, wire2);
        ruleEngineNode.connectOutputWire(0, wire3);
        modbusMessageGenertorNode.connectInputWire(0, wire3);
        modbusMessageGenertorNode.connectOutputWire(0, wire4);
        modbusServerNode.connectInputWire(0, wire4);

        mqttInNode.start();
        messageParsingNode.start();
        ruleEngineNode.start();
        modbusMessageGenertorNode.start();
        modbusServerNode.start();

    }
}

