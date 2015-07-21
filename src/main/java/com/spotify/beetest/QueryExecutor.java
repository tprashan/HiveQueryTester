package com.spotify.beetest;

import java.io.IOException;
import java.io.FileInputStream;
import java.lang.IllegalArgumentException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class QueryExecutor {

    private static final Logger LOGGER =
            Logger.getLogger(QueryExecutor.class.getName());

    static String getTestCaseCommand(String config, String outputQueryFilename, Properties variables) {
        List<String> args = new ArrayList<String>(Arrays.asList("hive ","--config", config, "-f", outputQueryFilename));
        for (String key :  variables.stringPropertyNames()) {
            args.add("--hivevar");
            args.add(key + "=" + variables.get(key));
        }
        return StringUtils.join(args, " ");
    }

    public static void run(HiveTestCase htc, String config) throws Exception {
        String outputQueryFilename = htc.generateTestCaseQueryFile();
        Utils utils = new Utils();
        Properties variables = new Properties();
        try {
            variables.load(new FileInputStream(htc.getVariablesFilename()));
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Missing variables file");
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Invalid variables file");
        }

        String finalQuery = Utils.readFile(outputQueryFilename);
        String[] executions = finalQuery.split(";\n");
        for (String ex : executions) {
            utils.runIt(ex, variables);
        }

//        String testCaseCommand = getTestCaseCommand(config, outputQueryFilename, variables);
//
//        LOGGER.log(Level.INFO, "Running: {0}", testCaseCommand);
//        Utils.runCommand(testCaseCommand, LOGGER);

        LOGGER.log(Level.INFO, "Asserting: {0} and {1}",
                new Object[]{htc.getExpectedFilename(), htc.getOutputFilename()});
    }

}
