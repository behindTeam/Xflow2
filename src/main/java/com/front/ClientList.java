package com.front;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttClient;

// client들의 목록을 관리하는 singleton 클래스
public class ClientList {

    // map<String id, IMqttClient client>를 관리한다.
    private Map<String, IMqttClient> map;

    private static final ClientList clientList = new ClientList();

    private ClientList() {
        map = new HashMap<>();
    }

    public static ClientList getClientList() {
        return clientList;
    }

    public IMqttClient getClient(String id) {
        return map.get(id);
    }

    public void addClient(String id, IMqttClient client) {
        map.put(id, client);
    }
}
