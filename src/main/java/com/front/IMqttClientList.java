package com.front;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttClient;

// client들의 목록을 관리하는 singleton 클래스
public class IMqttClientList {

    /**
     * map<String id, IMqttClient client>를 관리한다.
     */
    private Map<String, IMqttClient> map;

    private static final IMqttClientList clientList = new IMqttClientList();

    private IMqttClientList() {
        map = new HashMap<>();
    }

    public static IMqttClientList getClientList() {
        return clientList;
    }

    /**
     * 주어진 ID에 해당하는 클라이언트를 반환합니다.
     *
     * @param id 클라이언트의 ID
     * @return 주어진 ID에 해당하는 클라이언트, 없으면 null
     */
    public IMqttClient getClient(String id) {
        return map.get(id);
    }

    /**
     * 클라이언트를 목록에 추가합니다.
     *
     * @param id     클라이언트의 ID
     * @param client 추가할 클라이언트
     */
    public void addClient(String id, IMqttClient client) {
        map.put(id, client);
    }
}
