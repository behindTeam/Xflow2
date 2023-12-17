package com.front.node;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

/**
 * {@code MqttInNode} 클래스는 MQTT 브로커에서 메시지를 수신하고 처리하는 노드를 나타냅니다.
 * 이 노드는 입력과 출력을 처리하며, MQTT 프로토콜을 사용하여 메시지를 수신합니다.
 * 
 * <p>
 * MQTTInNode는 입력 Wire로부터 메시지를 수신하고, MQTT 브로커에서 특정 토픽으로부터 메시지를 받아
 * 출력 Wire로 전송합니다.
 * </p>
 */
public class MqttInNode extends InputOutputNode {
    Wire outputWire;
    IMqttClient client;
    String topicFilter;

    /**
     * {@code MqttInNode} 클래스의 기본 생성자입니다.
     * 입출력의 크기를 각각 1개씩으로 초기화
     */
    public MqttInNode() {
        this(1, 1);
    }

    /**
     * {@code MqttInNode} 클래스의 생성자입니다.
     * 입력과 출력의 크기를 지정하여 초기화합니다.
     *
     * @param inCount  입력의 크기
     * @param outCount 출력의 크기
     */
    public MqttInNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    public void setTopic(String topicFilter) {
        this.topicFilter = topicFilter;
    }

    @Override
    void preprocess() {
        outputWire = getOutputWire(0);
    }

    /**
     * MQTT 브로커에서 메시지를 수신하고 처리하는 메서드입니다.
     * MQTT 프로토콜을 사용하여 특정 토픽으로부터 메시지를 받아 출력 Wire로 전송합니다.
     */
    @Override
    void process() {
        UUID cunnetId = UUID.randomUUID();
        try (IMqttClient serverClient = client;) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);

            serverClient.connect(options);

            serverClient.subscribe(topicFilter, (topic, msg) -> {
                MyMqttMessage mqttmessage = new MyMqttMessage(cunnetId, topic, msg.getPayload());
                output(mqttmessage);
            });

            while (!Thread.currentThread().interrupted()) {
                Thread.sleep(100);
            }

            serverClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 후처리 메서드 (구현 x)
     */
    @Override
    void postprocess() {
        //
    }

    /**
     * 스레드에서 실행되는 메서드로, 전처리, 주기적 작업 수행, 후처리를 각각 1회씩 실행
     */
    @Override
    public void run() {
        preprocess();
        process();
        postprocess();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
