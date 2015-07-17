package com.spotify.beetest;

import java.io.*;

import org.apache.commons.lang3.StringUtils;



public final class HiveTestCase {

    private String selectQueryFilename;
    private String expectedFilename;
    private String variablesFilename;
    private int TEST_ID = Utils.getRandomPositiveNumber();
    private String DATABASE_NAME = "beetest";
    private String BEETEST_TEST_DIR = StringUtils.join(
            "/tmp/beetest-test-", TEST_ID);
    private String BEETEST_TEST_QUERY = StringUtils.join(
            BEETEST_TEST_DIR, "-query.hql");
    private String BEETEST_TEST_OUTPUT_TABLE = "output_" + TEST_ID;
    private String BEETEST_TEST_OUTPUT_DIRECTORY = StringUtils.join(
            BEETEST_TEST_DIR, "-", BEETEST_TEST_OUTPUT_TABLE);
    private static String NL = "\n";



    public HiveTestCase(String selectQueryFilename, String expectedFilename, String variablesFilename) {
        this.selectQueryFilename=selectQueryFilename;
        this.expectedFilename=expectedFilename;
        this.variablesFilename=variablesFilename;
    }

    public HiveTestCase() {

    }


    public String getExpectedFilename() {
        return expectedFilename;
    }

    public String getVariablesFilename() {
        return variablesFilename;
    }


    public String getOutputQuery(String selectFilename) throws IOException {
        File f = new File(selectFilename);
        String select;
        String ctas = StringUtils.join("DROP TABLE IF EXISTS ", BEETEST_TEST_OUTPUT_TABLE, ";", NL,
                "CREATE TABLE ", BEETEST_TEST_OUTPUT_TABLE, NL,
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' ", NL,
                "LOCATION '", BEETEST_TEST_OUTPUT_DIRECTORY, "' AS ", NL);
        if(f.exists() && !f.isDirectory()) {
            select = Utils.readFile(selectFilename);
        }else
            select = selectFilename;
        return ctas + select;
    }


    public String getBeeTestQuery() throws IOException {
        String databaseQuery = StringUtils.join("USE ", DATABASE_NAME, ";", NL);
        String queryContent = Utils.readFile(selectQueryFilename);
        Utils.deletePath(selectQueryFilename);
//         final query
        return StringUtils.join(databaseQuery,queryContent);
    }

    public String getOutputFilename() {
        return BEETEST_TEST_OUTPUT_DIRECTORY + "/000000_0";
    }

    public String getTestCaseQueryFilename() {
        return BEETEST_TEST_QUERY;
    }

    public String generateTestCaseQueryFile()
            throws FileNotFoundException, UnsupportedEncodingException,
            IOException {
        generateTextFile(BEETEST_TEST_QUERY, getBeeTestQuery());
        return BEETEST_TEST_QUERY;
    }

    private String generateTextFile(String filename, String content)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.println(content);
        writer.close();
        return filename;
    }


}
