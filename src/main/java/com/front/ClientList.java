package com.front;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttClient;

/**
 * 클라이언트들의 목록을 관리하는 싱글톤 클래스입니다.
 */
public class ClientList {

    /**
     * map<String id, IMqttClient client>를 관리한다.
     */
    private Map<String, IMqttClient> map;

    /**
     * 싱글톤 인스턴스
     */
    private static final ClientList clientList = new ClientList();

    // private 생성자를 통해 외부에서의 직접적인 인스턴스 생성을 막는다.
    private ClientList() {
        map = new HashMap<>();
    }

    /**
     * 싱글톤 인스턴스를 반환합니다.
     *
     * @return ClientList의 싱글톤 인스턴스
     */
    public static ClientList getClientList() {
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
