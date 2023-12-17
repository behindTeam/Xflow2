package com.front.node;

import java.util.Objects;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.front.message.Message;
import com.front.message.MyMqttMessage;
import com.front.wire.Wire;

/**
 * {@code MqttOutNode} 클래스는 메시지를 MQTT 브로커로 전송하는 노드를 나타냅니다.
 * 이 노드는 입력과 출력을 처리하며, MQTT 프로토콜을 사용하여 메시지를 브로커에 전송합니다.
 * 
 * <p>
 * MqttOutNode는 입력 Wire로부터 MyMqttMessage를 받아 해당 메시지의 토픽과 payload를 사용하여
 * MQTT 브로커에 메시지를 발행합니다.
 * </p>
 */

public class MqttOutNode extends InputOutputNode {
    Wire inputWire;
    UUID cunnetId;
    IMqttClient client;

    /**
     * {@code MqttOutNode} 클래스의 기본 생성자입니다.
     * 입력과 출력의 크기를 각각 1로 초기화합니다.
     */
    public MqttOutNode() {
        this(1, 1);
    }

    /**
     * {@code MqttOutNode} 클래스의 생성자입니다.
     * 입력과 출력의 크기를 지정하여 초기화합니다.
     *
     * @param inCount  입력의 크기
     * @param outCount 출력의 크기
     */
    public MqttOutNode(int inCount, int outCount) {
        super(inCount, outCount);
    }

    /**
     * 전처리 메서드 (구현 x)
     */
    public void setClient(IMqttClient client) {
        this.client = client;
    }

    @Override
    void preprocess() {
    }

    /**
     * 입력 Wire로부터 MyMqttMessage를 받아 MQTT 브로커에 메시지를 발행하는 메서드
     */
    @Override
    void process() {
        for (int index = 0; index < getInputWireCount(); index++) {
            if ((getInputWire(index) != null) && (getInputWire(index).hasMessage())) {
                Message myMqttMessage = getInputWire(index).get();
                if (myMqttMessage instanceof MyMqttMessage) {
                    if (Objects.nonNull(((MyMqttMessage) myMqttMessage).getPayload())) {
                        publish((MyMqttMessage) myMqttMessage);
                    }
                }
            }
        }
    }

    /**
     * 후처리 메서드 (구현 x)
     */
    @Override
    void postprocess() {
    }

    public void publish(MyMqttMessage inMessage) {
        cunnetId = UUID.randomUUID();

        try {
            IMqttClient localClient = client;
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            localClient.connect(options);
            localClient.publish(inMessage.getTopic(), new MqttMessage(inMessage.getPayload()));
            localClient.disconnect();
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
