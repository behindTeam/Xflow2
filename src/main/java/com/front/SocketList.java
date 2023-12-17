package com.front;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

// client들의 목록을 관리하는 singleton 클래스
public class SocketList {

    // map<String id, IMqttClient client>를 관리한다.
    private Map<String, Socket> map;

    private static final SocketList clientList = new SocketList();

    private SocketList() {
        map = new HashMap<>();
    }

    public static SocketList getClientList() {
        return clientList;
    }

    public Socket getClient(String id) {
        return map.get(id);
    }

    public void addClient(String id, Socket client) {
        map.put(id, client);
    }
}
