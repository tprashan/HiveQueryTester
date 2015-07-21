package com.spotify.beetest;


import com.google.common.io.Files;
import datamodel.CatBuilderRepairOption;
import junitx.framework.FileAssert;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fixtures.CatBuilderRepairOptionFixture.defaultCatBuilderRepairOption;

public class ImportBuilderRepairOptionTest {
    String localConfig = "./src/examples/local-config";
    String textFilesDirectory = "./src/examples/data-files/";
    static String queryFilesDirectory = "./src/examples/queries-files/";
    String dataFilesDirectory = "./src/examples/generaedDataFiles/";
    String generatedQueryFilename = Files.createTempDir()+"/hiveQuery.hql";
    String expectedFilename = "./src/examples/expected-txt-files/expected.txt";
    String variablesFilename = "./src/examples/variables.properties";
    HiveTestCase htc = null;
    static SchemaGenerator schemaGenerator = null;
    private static String queryFile;

    @BeforeClass
    public static void setupOnce(){
        schemaGenerator = new SchemaGenerator();
        queryFile = queryFilesDirectory+"select.hql";
    }

    @Before
    public void initialize() throws Exception {
        htc = new HiveTestCase(generatedQueryFilename,expectedFilename,variablesFilename);
    }

    @Test
    public void shouldReturnNoRecordsWhenThereAreNoJobPartsOrOperationsForARepairOption() throws IOException {
        CatBuilderRepairOption record = defaultCatBuilderRepairOption().build();

        Utils.writeOnFile(dataFilesDirectory + "cat_builder_repair_option",record.getStringRepresentation(",") );
        //
        //

        // make null expected.
    }

    @Test
    public void shouldReturnRepairOptionsWhenThereIsNoJobOperationButDirectJobPartListHeaders() throws IOException {
        CatBuilderRepairOption record = defaultCatBuilderRepairOption().build();

        Utils.writeOnFile(dataFilesDirectory + "cat_builder_repair_option", record.getStringRepresentation(","));
        //
        //

        // make 1 record expected.
    }


    @Test
    public void testHiveQuery() throws Exception {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("cat_builder_jobpartslistheader");
        tableNames.add("CAT_BUILDER_REPAIROPTION");
        tableNames.add("cat_builder_joboperation");


        schemaGenerator.generateFinalQuery(tableNames, queryFile, textFilesDirectory, htc, generatedQueryFilename);
        QueryExecutor.run(htc, localConfig);
        FileUtils.deleteDirectory(new File("/tmp/rawBasePath"));
        FileUtils.deleteDirectory(new File("/tmp/tableBasePath"));

        FileAssert.assertEquals("Output does not match",
                new File(htc.getExpectedFilename()),
                new File(htc.getOutputFilename()));
    }
}