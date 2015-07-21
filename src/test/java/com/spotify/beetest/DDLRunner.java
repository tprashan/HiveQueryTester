package com.spotify.beetest;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DDLRunner {
    private String LOCAL_CONFIG = "./src/examples/local-config";
    private String HIVE_QUERY_DIRECTORY = "./src/examples/hive";
    private String VARIABLE_FILE_NAME = "./src/examples/variables.properties";

    private static final Logger LOGGER =
            Logger.getLogger(QueryExecutor.class.getName());

    @Test
    public void runDDL() throws Exception {
        FileUtils.deleteDirectory(new File("/tmp/rawBasePath"));
        FileUtils.deleteDirectory(new File("/tmp/tableBasePath"));
        SchemaGenerator createSchemaFile = new SchemaGenerator(HIVE_QUERY_DIRECTORY);
        String queryFile = createSchemaFile.getCreateQuerySetup();
        Utils utils = new Utils();
        Properties variables = new Properties();
        try {
            variables.load(new FileInputStream(VARIABLE_FILE_NAME));
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Missing variables file");
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Invalid variables file");
        }
        String finalQuery = Utils.readFile(queryFile);
        String[] executions = finalQuery.split(";\n");
        for (String ex : executions) {
            utils.runIt(ex, variables);
        }
//        String testCaseCommand = QueryExecutor.getTestCaseCommand(LOCAL_CONFIG,queryFile,variables);
//        LOGGER.log(Level.INFO, "Running: {0}", testCaseCommand);
//        Utils.runCommand(testCaseCommand, LOGGER);
        Utils.deletePath(queryFile);
    }
}
