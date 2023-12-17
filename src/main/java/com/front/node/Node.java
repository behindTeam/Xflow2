package com.front.node;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import com.github.f4b6a3.uuid.UuidCreator;

/**
 * {@code Node}는 노드의 기본 기능을 정의하는 추상 클래스입니다.
 * 모든 노드는 이 클래스를 상속하여 생성됩니다.
 */
public abstract class Node {
    private static int count;
    UUID id;
    String name;
    Logger log;

    /**
     * 기본 생성자로, 노드의 ID를 시간 기반으로 생성하여 고유한 ID를 갖게 합니다.
     */
    Node() {
        this(UuidCreator.getTimeBased());
    }

    /**
     * JSON 형식의 데이터로 초기화하는 생성자로, ID가 포함되어 있다면 해당 ID를 사용하고,
     * 그렇지 않은 경우 시간 기반으로 ID를 생성합니다.
     *
     * @param json JSON 형식의 데이터
     */
    Node(JSONObject json) {
        if (json.containsKey("id")) {
            id = UuidCreator.fromString((String) json.get("id"));
        } else {
            id = UuidCreator.getTimeBased();
        }
    }

    /**
     * 주어진 ID로 초기화하는 생성자로, 이름은 생성되는 순서대로 부여됩니다.
     *
     * @param id 초기화할 ID
     */
    Node(UUID id) {
        this(id.toString(), id);
    }

    /**
     * 주어진 이름과 시간 기반으로 생성된 ID로 초기화하는 생성자로, 이름은 생성되는 순서대로 부여됩니다.
     *
     * @param name 초기화할 이름
     */
    Node(String name) {
        this(name, UuidCreator.getTimeBased());
    }

    /**
     * 주어진 이름과 ID로 초기화하는 생성자로, 이름은 생성되는 순서대로 부여됩니다.
     *
     * @param name 초기화할 이름
     * @param id   초기화할 ID
     */
    Node(String name, UUID id) {
        count++;
        this.id = id;
        this.name = name;
        log = LogManager.getLogger(name);

        log.trace("create node : {}", id);
    }

    /**
     * 노드의 ID를 반환합니다.
     *
     * @return 노드의 ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 노드의 이름을 반환합니다.
     *
     * @return 노드의 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 노드의 이름을 설정합니다.
     *
     * @param name 설정할 이름
     */
    public void setName(String name) {
        this.name = name;
        log = LogManager.getLogger(name);
    }

    /**
     * 생성된 노드의 총 개수를 반환합니다.
     *
     * @return 노드의 총 개수
     */
    public static int getCount() {
        return count;
    }

    /**
     * JSON 형식으로 노드 정보를 반환합니다.
     *
     * @return JSON 형식의 노드 정보
     */
    public JSONObject getJson() {
        JSONObject object = new JSONObject();

        object.put("id", id);
        object.put("name", name);

        return object;
    }
}
