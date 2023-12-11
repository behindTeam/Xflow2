package com.front.node;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.front.message.JsonMessage;
import com.front.message.Message;
import com.front.message.StringArrayMessage;

import com.front.wire.Wire;

public class ProcessCommandLineNode extends InputOutputNode {
    private String[] args;

    public ProcessCommandLineNode() {
        this(1, 1);
    }

    public ProcessCommandLineNode(int inputCount, int outputCount) {
        super(inputCount, outputCount);
    }

    public void join() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    void preprocess() {
        Wire inputWire = getInputWire(0);
        Message message = inputWire.get();
        args = ((StringArrayMessage) message).getPayload();

    }

    @Override
    void process() {
        JsonMessage jsonMessage = null;

        jsonMessage = new JsonMessage(processCommandLine(args));

        output(jsonMessage);
    }

    @Override
    void postprocess() {
        //
    }

    @Override
    public void run() {
        preprocess();
        process();
    }

    public JSONObject processCommandLine(String[] args) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        return object;
    }
}
