package com.spotify.beetest;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class RunAllTest {
    private static final Logger LOGGER =
            Logger.getLogger(QueryExecutor.class.getName());
    private String testCaseCommandFile = "./src/examples/testCaseCommand.txt";

    @Test
    public void RunTestCaseCommand() throws IOException, InterruptedException {
        File file = new File(testCaseCommandFile);
        String command = FileUtils.readFileToString(file);
        Utils.runCommand("hive "+command,LOGGER);
        file.delete();
    }
}
