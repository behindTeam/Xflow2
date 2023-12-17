package com.front;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.front.node.InputOutputNode;
import com.front.node.ModbusMasterNode;
import com.front.node.MqttInNode;
import com.front.node.MqttOutNode;
import com.front.node.Node;
import com.front.wire.Wire;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;

//세팅을 적용시키는 클래스, main문을 포함한다.

@Slf4j
public class Configurations {
    static int count = 0;
    private static Map<Node, JSONArray> map = new HashMap<>();
    private static Map<String, Node> nodeMap = new HashMap<>();
    private static Map<Node, String> brokerMap = new HashMap<>();
    private static Map<Node, String> socketMap = new HashMap<>();
    private static JSONArray jsonArray;
    private static String[] configurationArgs;

    public static void main(String[] args) throws FileNotFoundException, MqttException {
        JSONParser parser = new JSONParser(); // JSON 파일 읽기
        Reader reader = new FileReader("src/main/java/com/front/resources/modbusToModbusSettings/settings.json");
        configurationArgs = args;

        try {
            jsonArray = (JSONArray) parser.parse(reader);

            JSONArray nodeList = (JSONArray) jsonArray.get(0);
            // JSONArray로부터 원하는 데이터 추출

            // 노드 동적 생성
            for (Object obj : nodeList) {
                JSONObject jsonObject = (JSONObject) obj;
                createNodeInstance(jsonObject, jsonObject.get("type").toString());
                log.info("create node: {}", jsonObject.get("type"));
            }

            // 노드 동적 연결
            for (Node before : map.keySet()) {
                JSONArray idList = map.get(before);
                for (Object id : idList) {
                    if (nodeMap.containsKey(id)) {
                        Node after = nodeMap.get(id);
                        connect(before, after);
                    }
                }
            }

            // 클라이언트 동적 세팅
            for (Node node : brokerMap.keySet()) {
                String brokerId = brokerMap.get(node);
                settingIMqttClient(node, brokerId);
            }

            for (Node node : socketMap.keySet()) {
                String socketId = socketMap.get(node);
                settingSocket(node, socketId);
            }

            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 세팅 완료 후 쓰레드 시작
            for (String id : nodeMap.keySet()) {
                ((InputOutputNode) nodeMap.get(id)).start();
                log.info("node {} started", id);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // 노드 객체를 동적으로 생성하는 메서드
    private static void createNodeInstance(JSONObject jsonObject, String nodeType) throws MqttException {
        try {
            String nodeName = null;
            if (nodeType.equals("mqtt in")) {
                nodeName = "com.front.node.MqttInNode";
            } else if (nodeType.equals("mqtt out")) {
                nodeName = "com.front.node.MqttOutNode";
            } else if (nodeType.equals("messageParsing")) {
                nodeName = "com.front.node.MessageParsingNode";
            } else if (nodeType.equals("ModBusMapperNode")) {
                nodeName = "com.front.node.ModBusMapperNode";
            } else if (nodeType.equals("modbus-master")) {
                nodeName = "com.front.node.ModbusMasterNode";
            } else if (nodeType.equals("ModbusMessageGenertorNode")) {
                nodeName = "com.front.node.ModbusMessageGenertorNode";
            } else if (nodeType.equals("ModbusServerNode")) {
                nodeName = "com.front.node.ModbusServerNode";
            } else if (nodeType.equals("MqttMessageGeneratorNode")) {
                nodeName = "com.front.node.MqttMessageGeneratorNode";
            } else if (nodeType.equals("RuleEngineNode")) {
                nodeName = "com.front.node.RuleEngineNode";
            } else if (nodeType.equals("mqtt-broker")) {
                createIMqttClient((String) jsonObject.get("broker"), (String) jsonObject.get("port"),
                        (String) jsonObject.get("id"));
            } else if (nodeType.equals("modbus-client")) {
                createSocket((String) jsonObject.get("tcpHost"), (String) jsonObject.get("tcpPort"),
                        (String) jsonObject.get("id"));
            }
            if (Objects.isNull(nodeName)) {
                return;
            }
            Class<?> clazz = Class.forName(nodeName);
            Node node = (Node) clazz.getDeclaredConstructor(int.class, int.class).newInstance(3, 3); // 노드 생성

            Method setNameMethod = clazz.getMethod("setName", String.class);

            setNameMethod.invoke(node, jsonObject.get("id"));

            if (!((JSONArray) jsonObject.get("wires")).isEmpty()) {
                map.put(node, (JSONArray) ((JSONArray) jsonObject.get("wires")).get(0));
            }
            if (jsonObject.containsKey("broker")) {
                brokerMap.put(node, (String) jsonObject.get("broker"));
            }
            if (jsonObject.containsKey("server")) {
                socketMap.put(node, (String) jsonObject.get("server"));
            }

            nodeMap.put((jsonObject.get("id")).toString(), node);

            // 노드 타입에 따른 설정 분배

            switch (nodeName) {
                case "com.front.node.MessageParsingNode":
                    Method configureSettingsMethod = clazz.getMethod("configureSettings",
                            JSONObject.class);
                    JSONObject settings = (JSONObject) ((JSONArray) (jsonArray.get(1))).get(0);
                    settings.putAll(processCommandLine(configurationArgs));
                    configureSettingsMethod.invoke(node, settings);
                    break;
                case "com.front.node.MqttInNode":
                    Method setTopicMethod = clazz.getMethod("setTopic", String.class);
                    setTopicMethod.invoke(node, jsonObject.get("topic"));
                    break;
                default:
                    break;
            }

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 와이어를 동적으로 생성하여 노드 객체 두개를 이어주는 메서드
    private static void connect(Node before, Node after) {
        String wireName = "com.front.wire.BufferedWire";
        String nodeName = "com.front.node.InputOutputNode";

        try {
            Class<?> clazz = Class.forName(wireName);
            Object wire = clazz.getDeclaredConstructor().newInstance();

            Class<?> nodeClazz = Class.forName(nodeName);
            Method connectOutputWireMethod = nodeClazz.getMethod("connectOutputWire", int.class, Wire.class); // 메소드
                                                                                                              // 호출
            Method connectInputWireMethod = nodeClazz.getMethod("connectInputWire", int.class, Wire.class); // 메소드
                                                                                                            // 호출

            connectOutputWireMethod.invoke(before, ((InputOutputNode) before).getOutputWireCount(), wire);
            connectInputWireMethod.invoke(after, ((InputOutputNode) after).getInputWireCount(), wire);

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e);
            e.printStackTrace();
        }

    }

    // 클라이언트를 생성해주는 메서드
    // Todo: port번호도 가져와야 함
    private static void createIMqttClient(String uri, String port, String id) throws MqttException {
        if (uri.equals("mosquitto")) {
            uri = "localhost";
        }
        IMqttClient serverClient = new MqttClient("tcp://" + uri + ":" + port, id);
        IMqttClientList.getClientList().addClient(id, serverClient);
    }

    private static void createSocket(String uri, String port, String id)
            throws MqttException, NumberFormatException, UnknownHostException, IOException {
        Socket socket = new Socket(uri, Integer.valueOf(port));
        SocketList.getClientList().addClient(id, socket);
    }

    private static void settingIMqttClient(Node node, String id) {
        if (node instanceof MqttInNode) {
            ((MqttInNode) node).setClient(IMqttClientList.getClientList().getClient(id));
        } else if (node instanceof MqttOutNode) {
            ((MqttOutNode) node).setClient(IMqttClientList.getClientList().getClient(id));
        }
    }

    private static void settingSocket(Node node, String id) {
        if (node instanceof ModbusMasterNode) {
            ((ModbusMasterNode) node).setClient(SocketList.getClientList().getClient(id));
        }
    }

    // string ars[]의 내용을 적용시켜주는 메서드
    public static JSONObject processCommandLine(String[] args) throws org.json.simple.parser.ParseException {
        String usage = "";
        String path = "src/main/java/com/front/resources/mqttToMqttSettings.json";

        Options cliOptions = new Options();
        cliOptions.addOption(new Option("applicationName", "an", true,
                "프로그램 옵션으로 Application Name을 줄 수 있으며, application name이 주어질 경우 해당 메시지만 수신하도록 한다."));
        cliOptions.addOption(new Option("s", true, "허용 가능한 센서 종류를 지정할 수 있다."));
        cliOptions.addOption(
                new Option("c", true, "설정 파일과 command line argument라 함께 주어질 경우 command line argument가 우선된다."));
        cliOptions.addOption(new Option("h", "help", false, "사용법, 옵션을 보여줍니다."));

        HelpFormatter helpFormatter = new HelpFormatter();
        JSONObject object = new JSONObject();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine c = parser.parse(cliOptions, args);

            if (c.hasOption("h"))
                helpFormatter.printHelp(usage, cliOptions);

            if (c.hasOption("c")) {
                JSONParser jsonParser = new JSONParser();
                Reader reader;

                if (c.getOptionValue("c") == null) {
                    reader = new FileReader(path);
                } else {
                    reader = new FileReader(c.getOptionValue("c"));
                }
                object = (JSONObject) jsonParser.parse(reader);

            }

            if (c.hasOption("s")) {
                if (c.getOptionValue("s") != null) {
                    String[] arr = c.getOptionValue("s").split(",");
                    object.put("sensor", Arrays.toString(arr));
                }
            }

            if (c.hasOption("applicationName")) {
                if (c.getOptionValue("applicationName") != null) {
                    object.put("applicationName", c.getOptionValue("applicationName"));
                }
            }
        } catch (ParseException e) {
            helpFormatter.printHelp(usage, cliOptions);
        } catch (IOException | org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
        }

        return object;
    }
}