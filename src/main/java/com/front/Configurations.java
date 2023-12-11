package com.front;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.front.node.InputOutputNode;
import com.front.node.MqttInNode;
import com.front.node.MqttOutNode;
import com.front.node.Node;
import com.front.wire.Wire;

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
public class Configurations {
    static int count = 0;
    private static Map<Node, JSONArray> map = new HashMap<>();
    private static Map<String, Node> nodeMap = new HashMap<>();
    private static Map<Node, String> brokerMap = new HashMap<>();
    private static JSONArray jsonArray;
    private static String[] configurationArgs;

    public static void main(String[] args) throws FileNotFoundException, MqttException {
        JSONParser parser = new JSONParser(); // JSON 파일 읽기
        Reader reader = new FileReader("src/main/java/com/front/settings.json");
        configurationArgs = args;

        try {
            jsonArray = (JSONArray) parser.parse(reader);

            JSONArray nodeList = (JSONArray) jsonArray.get(0);
            // JSONArray로부터 원하는 데이터 추출

            // 노드 동적 생성
            for (Object obj : nodeList) {
                JSONObject jsonObject = (JSONObject) obj;
                createNodeInstance(jsonObject, jsonObject.get("type").toString());
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
                settingClient(node, brokerId);
            }

            // 세팅 완료 후 쓰레드 시작
            for (String id : nodeMap.keySet()) {
                ((InputOutputNode) nodeMap.get(id)).start();
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
            } else if (nodeType.equals("mqtt-broker")) {
                createClient((String) jsonObject.get("broker"), (String) jsonObject.get("id"));
            }
            if (Objects.isNull(nodeName)) {
                return;
            }
            Class<?> clazz = Class.forName(nodeName);
            Node node = (Node) clazz.getDeclaredConstructor().newInstance(); // 노드 생성

            Method setNameMethod = clazz.getMethod("setName", String.class);

            setNameMethod.invoke(node, jsonObject.get("id"));

            if (!((JSONArray) jsonObject.get("wires")).isEmpty()) {
                map.put(node, (JSONArray) ((JSONArray) jsonObject.get("wires")).get(0));
            }
            if (jsonObject.containsKey("broker")) {
                brokerMap.put(node, (String) jsonObject.get("broker"));
            }

            nodeMap.put((jsonObject.get("id")).toString(), node);

            // 노드 타입에 따른 설정 분배
            switch (nodeName) {
                case "com.front.node.MessageParsingNode":
                    Method configureSettingsMethod = clazz.getMethod("configureSettings", JSONObject.class);
                    JSONObject settings = (JSONObject) ((JSONArray) (jsonArray.get(1))).get(0);
                    settings.putAll(processCommandLine(configurationArgs));
                    configureSettingsMethod.invoke(node, settings);
                    break;
                default:
                    break;
            }

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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

            connectOutputWireMethod.invoke(before, 0, wire);
            connectInputWireMethod.invoke(after, 0, wire);

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e);
        }

    }

    // 클라이언트를 생성해주는 메서드
    // Todo: port번호도 가져와야 함
    private static void createClient(String uri, String id) throws MqttException {
        if (uri.equals("mosquitto")) {
            uri = "localhost";
        }
        IMqttClient serverClient = new MqttClient("tcp://" + uri, id);
        ClientList.getClientList().addClient(id, serverClient);
    }

    private static void settingClient(Node node, String id) {
        if (node instanceof MqttInNode) {
            ((MqttInNode) node).setClient(ClientList.getClientList().getClient(id));
        } else if (node instanceof MqttOutNode) {
            ((MqttOutNode) node).setClient(ClientList.getClientList().getClient(id));
        }
    }

    // string ars[]의 내용을 적용시켜주는 메서드
    public static JSONObject processCommandLine(String[] args) throws org.json.simple.parser.ParseException {
        String usage = "scurl [option] url";
        String path = "src/main/java/com/front/resources/settings.json";

        Options cliOptions = new Options();
        cliOptions.addOption(new Option("applicationName", "an", true,
                "프로그램 옵션으로 Application Name을 줄 수 있으며, application name이 주어질 경우 해당 메시지만 수신하도록 한다."));
        cliOptions.addOption(new Option("s", true, "허용 가능한 센서 종류를 지정할 수 있다."));
        cliOptions.addOption(
                new Option("c", false, "설정 파일과 command line argument라 함께 주어질 경우 command line argument가 우선된다."));
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

// BiConsumer<Node, Node> setConnect = {(input, output) -> new Wire wire = new
// Wire()

// Method connectOutputWireMethod =
// clazz.getDeclaredMethod("connectOutputWire"); // 메소드 호출

// if (outputWire() != null) {
// output.connectInputWire();
// }
// input.connectOutputWire();
// }

// Class<?> clazz1 = Class.forName(wireName);
// Object wire = clazz1.getDeclaredConstructor().newInstance();

// connectOutputWireMethod.invoke(mqttInNode, 0, wire);