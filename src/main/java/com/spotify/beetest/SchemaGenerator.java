package com.spotify.beetest;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SchemaGenerator {

    private static String NL = "\n";
    private String hiveQueryDirectory;
    private String DATABASE_NAME = "beetest";
    private String generatedDataFilesDir = "/tmp/";

    public SchemaGenerator(String hiveDirectoryPath) {
        this.hiveQueryDirectory = hiveDirectoryPath;
    }

    public SchemaGenerator() {
    }

    private String addBraceWithVariable(String content) {
        String lineArray[] = content.split("\n");
        String tableSchema = "";
        for (String line : lineArray) {

            if (!line.contains("'$") && !line.contains("MSCK")){
                tableSchema+=line+"\n";
            }
            if (line.contains("'$")){
                line = line.replace("$", "${");
                line = line.replaceFirst("/", "}/");
                tableSchema+=line+"\n";
            }
        }

        return tableSchema;
    }


    private String readSetupFiles(String pathname) throws IOException {
        List<String> list = new ArrayList<String>();
        File directory = new File(pathname);
        String createContent = "";
        for (File file : directory.listFiles())
        {
            String extension = FilenameUtils.getExtension(String.valueOf(file));
            if (!file.isDirectory() && extension.equals("hql"))
            {
                String content = FileUtils.readFileToString(file, "utf-8");
                String schema = addBraceWithVariable(content);
                list.add(schema);
            }
        }
        for (String createSchema : list) createContent += createSchema;
        return createContent;
    }

    public String getCreateQuerySetup() throws IOException {
        // own database
        String databaseQuery = StringUtils.join("CREATE DATABASE IF NOT EXISTS ",
                DATABASE_NAME, ";", NL, "USE ", DATABASE_NAME, ";", NL);

        String query = (hiveQueryDirectory != null
                ? readSetupFiles(hiveQueryDirectory) : "");

        // include external files in setup.hql
        query = ExternalFilesSubstitutor.replace(query);
        String SETUP_QUERY_FILE_NAME = Utils.getSetUpFileName();
        generateTextFile(SETUP_QUERY_FILE_NAME, StringUtils.join(databaseQuery, query));

        return SETUP_QUERY_FILE_NAME;
    }

    private String loadDataIntoTableSetUp(String textFilesPath, List<String> tableNames) throws IOException {
        StringBuilder loadDataQuery = new StringBuilder();
        for (String table : tableNames) {
            generateDataFile(textFilesPath, table);
            loadDataQuery.append("\nLOAD DATA LOCAL INPATH '" + generatedDataFilesDir + table + ".txt' INTO TABLE " + table + ";\n");
        }

        return loadDataQuery.toString();
    }

    private void generateDataFile(String textFilesPath, String table) throws IOException {
        File file = new File(textFilesPath+table+".txt");
        String content = FileUtils.readFileToString(file, "utf-8").replace("\t", "\u0001");
        String fileName = generatedDataFilesDir + table + ".txt";
        File f = new File(fileName);
        if(f.exists() && !f.isDirectory())
            f.delete();
        f.createNewFile();
        generateTextFile(fileName, content);
    }

    private String generateTextFile(String filename, String content)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.println(content);
        writer.close();
        return filename;
    }

    private String getGeneratedQueryFileName(String generatedQueryFilename,HiveTestCase htc,
                                                String loadDataQuerySetUp, String queryFile) throws IOException {

        String testedOutputQuery = htc.getOutputQuery(queryFile);
        generateTextFile(generatedQueryFilename, StringUtils.join(loadDataQuerySetUp, testedOutputQuery));
        return generatedQueryFilename;
    }

    private String getGeneratedQueryFileName(String generatedQueryFilename,HiveTestCase htc, String loadDataSetUp,
                                                String queryFile, String selectAllQuery) throws IOException {

        String queryFileContent = Utils.readFile(queryFile);
        String testedOutputQuery = htc.getOutputQuery(selectAllQuery);
        String nonStrictModeOfHive = getNonStrictModeOfHive();
        generateTextFile(generatedQueryFilename, StringUtils.join(loadDataSetUp, nonStrictModeOfHive,queryFileContent, testedOutputQuery));
        return generatedQueryFilename;
    }

    private String getNonStrictModeOfHive() {
        return "set hive.exec.dynamic.partition.mode=nonstrict;\nset hive.mapred.mode=nonstrict;\n";
    }

    private String getSelectAllQuery(List<String> insertedTableName) {
        Iterator iter = insertedTableName.iterator();
        return "Select * from "+iter.next()+";";
    }

    public String generateFinalQuery(List<String> tableNames, String queryFile, String textFilesDirectory,
                                        HiveTestCase htc, String generatedQueryFilename) throws IOException {

        String loadDataSetUp = loadDataIntoTableSetUp(textFilesDirectory, tableNames);
        List<String> insertedTableName = Utils.getInsertedTableName(queryFile);
        String queryFileName;
        if(insertedTableName.size()>0){
            String selectAllQuery = getSelectAllQuery(insertedTableName);
            queryFileName = getGeneratedQueryFileName(generatedQueryFilename,htc, loadDataSetUp, queryFile, selectAllQuery);
        }else{
            queryFileName = getGeneratedQueryFileName(generatedQueryFilename,htc, loadDataSetUp, queryFile);
        }
        return queryFileName ;
    }
}
