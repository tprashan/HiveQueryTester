package com.spotify.beetest;

import com.google.common.io.Files;
import junitx.framework.FileAssert;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QueryTest3
{
    String localConfig = "./src/examples/local-config";
    String textFilesDirectory = "./src/examples/data-files/";
    String queryFilesDirectory = "./src/examples/queries-files/";
    String generatedQueryFilename = Files.createTempDir()+"/hiveQuery.hql";
    String expectedFilename = "./src/examples/expected-txt-files/expected2.txt";
    String variablesFilename = "./src/examples/variables.properties";
    HiveTestCase htc = null;
    SchemaGenerator schemaGenerator = null;

    @Before
    public void initialize() throws Exception {
        htc = new HiveTestCase(generatedQueryFilename,expectedFilename,variablesFilename);
        schemaGenerator = new SchemaGenerator();
    }

    @Test
    public void testHiveQuery() throws Exception {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("cat_builder_jobpartslistheader");
        tableNames.add("CAT_BUILDER_REPAIROPTION");
        tableNames.add("cat_builder_joboperation");

        String queryFile = queryFilesDirectory+"insert.hql";
        schemaGenerator.generateFinalQuery(tableNames, queryFile, textFilesDirectory, htc,generatedQueryFilename);

        QueryExecutor.run(htc, localConfig);
        FileUtils.deleteDirectory(new File("/tmp/rawBasePath"));
        FileUtils.deleteDirectory(new File("/tmp/tableBasePath"));

        FileAssert.assertEquals("Output does not match",
                new File(htc.getExpectedFilename()),
                new File(htc.getOutputFilename()));
    }

}
