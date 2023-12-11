package com.front.test;

import com.front.node.ModbusReadNode;
import com.front.node.ModbusServerNode;

public class ModbusTest {
    public static void main(String[] args) {
        ModbusServerNode server = new ModbusServerNode();
        server.start();

        ModbusReadNode reader = new ModbusReadNode();
        reader.start();
    }

}
