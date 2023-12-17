package com.front.node;

import java.util.UUID;

import org.json.simple.JSONObject;

import com.front.exception.AlreadyStartedException;

/**
 * {@code ActiveNode}은 {@code Node}를 상속하며, {@code Runnable} 인터페이스를 구현한 추상
 * 클래스입니다.
 * 이 클래스는 활성 노드의 기본 동작을 정의하고, thread를 사용하여 주기적으로 작업을 수행합니다.
 * 
 * <p>
 * {@code ActiveNode}의 인스턴스는 시작, 중지 및 실행 여부 확인이 가능하며,
 * 일정한 간격(interval 변수)으로 실행되는 작업을 설정할 수 있습니다.
 * </p>
 * 
 */
public abstract class ActiveNode extends Node implements Runnable {

    public static final long DEFAULT_INTERVAL = 1;
    Thread thread;
    long interval = DEFAULT_INTERVAL;

    ActiveNode() {
        super();
    }

    /**
     * JSON 형식의 데이터로 초기화하는 생성자로, 상위 클래스의 JSON 초기화 생성자를 호출합니다.
     * JSON에 "interval" 키가 포함되어 있다면 해당 값을 사용하여 간격을 설정합니다.
     *
     * @param json JSON 형식의 데이터
     */
    ActiveNode(JSONObject json) {
        super(json);

        if (json.containsKey("interval")) {
            interval = (long) json.get("interval");
        }
    }

    ActiveNode(String name) {
        super(name);
    }

    ActiveNode(String name, UUID id) {
        super(name, id);
    }

    /**
     * 현재 설정된 주기적 실행 간격을 반환합니다.
     *
     * @return 실행 간격
     */
    public long getInterval() {
        return interval;
    }

    /**
     * 주기적 실행 간격을 설정합니다.
     *
     * @param interval 설정할 주기적 실행 간격
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * 노드를 시작합니다. 이미 시작된 경우 {@code AlreadyStartedException}을 발생시킵니다.
     */
    public synchronized void start() {
        if (thread != null) {
            throw new AlreadyStartedException();
        }

        thread = new Thread(this, getName());
        thread.start();
    }

    /**
     * 노드를 중지합니다.
     */
    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * 노드가 현재 실행 중인지 여부를 반환합니다.
     *
     * @return 노드가 실행 중이면 {@code true}, 그렇지 않으면 {@code false}
     */
    public synchronized boolean isAlive() {
        return (thread != null) && thread.isAlive();
    }

    /**
     * 노드의 전처리 작업을 수행합니다.
     */
    void preprocess() {
    }

    /**
     * 노드의 주기적 작업을 수행합니다.
     */
    void process() {
    }

    /**
     * 노드의 후처리 작업을 수행합니다.
     */
    synchronized void postprocess() {
        thread = null;
    }

    /**
     * 스레드에서 실행되는 메서드로, 노드의 전처리, 주기적 작업 수행 및 후처리를 담당
     */
    @Override
    public void run() {
        preprocess();

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;

        while (isAlive()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;

            if (elapsedTime < interval) {
                try {
                    process();
                    Thread.sleep(interval - elapsedTime);
                } catch (InterruptedException e) {
                    stop();
                }
            }

            previousTime = startTime + (System.currentTimeMillis() - startTime) / interval * interval;
        }

        postprocess();
    }

    /**
     * JSON 형식으로 노드 정보를 반환
     *
     * @return JSON 형식의 노드 정보
     */
    @Override
    public JSONObject getJson() {
        JSONObject object = super.getJson();

        object.put("interval", interval);

        return object;
    }
}
