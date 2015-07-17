/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spotify.beetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;

public class Utils {

    private static Random random = new Random();
    private Connection connection;

    public static int getRandomPositiveNumber() {
        return (random.nextInt() & Integer.MAX_VALUE);
    }

    public static int runCommand(String command, Logger LOGGER)
            throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String line = reader.readLine();

        while (line != null) {
            LOGGER.info(line);
            line = reader.readLine();
        }
        return p.exitValue();
    }

    public String getDriver() {
        return "org.apache.hive.jdbc.HiveDriver";
    }

    public void runIt(String command, Properties variables) throws Exception {
        try {
            Class.forName(getDriver());
            connection = DriverManager.getConnection("jdbc:hive2://0.0.0.0:10000", "beetest", null);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found " + e.getMessage());
        } catch (SQLException e2){
            throw new IllegalArgumentException("Sql exeception " + e2.getMessage());
        }
        // parse command... {query...}
        String var = " ";
        for (String key :  variables.stringPropertyNames()) {
            var += "set hivevar:" + key + "=" + variables.get(key)+" ";
        }
        System.out.println("-- "+command +var);
        Statement stmt = connection.createStatement();
        stmt.execute(command);
        stmt.execute(var);

    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to close the database connection");
        }
    }

//    private static Properties getHiveConfiguration(final String basePath) {
//        return new Properties(){{
//            setProperty("hiveconf:fs.default.name", "file://" + basePath);
//            setProperty("hiveconf:mapred.job.tracker", "local");
//            setProperty("hiveconf:mapreduce.framework.name", "local");
//
//            setProperty("hiveconf:hive.exec.scratchdir", basePath + "/scratchdir");
//            setProperty("hiveconf:hive.querylog.location", basePath + "/querylog");
//            setProperty("hiveconf:hive.metastore.warehouse.dir", basePath + "/warehouse");
//            setProperty("hiveconf:hive.metastore.local", "true");
//
//            setProperty("hiveconf:javax.jdo.option.ConnectionURL", "jdbc:derby:;databaseName=" + basePath + "/metastore_db;create=true");
//            setProperty("hiveconf:javax.jdo.option.ConnectionDriverName", "org.apache.derby.jdbc.EmbeddedDriver");
//
//            setProperty("hiveconf:hive.stats.dbclass", "jdbc:derby");
//            setProperty("hiveconf:hive.stats.dbconnectionstring", "jdbc:derby:;databaseName=" + basePath + "/TempStatsStore;create=true");
//            setProperty("hiveconf:hive.stats.jdbcdriver", "org.apache.derby.jdbc.EmbeddedDriver");
//
//            setProperty("hiveconf:hive.cli.print.header", "false");
//            setProperty("hiveconf:hive.metastore.execute.setugi", "true");
//            setProperty("hiveconf:hive.exec.dynamic.partition.mode", "nonstrict");
//        }};
//    }

    public static String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine());
                fileContents.append(lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    public static List<String> fileToList(String pathname) throws IOException {
        File file = new File(pathname);
        Scanner scanner = new Scanner(file);
        List<String> list = new ArrayList<String>();
        try {
            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
            return list;
        } finally {
            scanner.close();
        }
    }

    public static void deletePath(String filename) throws IOException {
        String directory = filename.substring(0,filename.lastIndexOf(File.separator));
        FileUtils.deleteDirectory(new File(directory));
    }

    public static List<String> getInsertedTableName(String queryFile) throws IOException {
        List<String> allInsertedTableName = new ArrayList<String>();
        File file = new File(queryFile);
        Scanner scanner = new Scanner(file);

        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("INSERT OVERWRITE TABLE") || line.contains("insert overwrite table")) {
                    int index = Arrays.asList(line.split(" ")).indexOf("TABLE");
                    allInsertedTableName.add(Arrays.asList(line.split(" ")).get(index+1));
                }
            }
            return allInsertedTableName;
        } finally {
            scanner.close();
        }
    }

    public static void writeOnFile(String testCaseCommandFile, String testCaseCommand) throws IOException {
        File file = new File(testCaseCommandFile);
        FileUtils.writeStringToFile(file,testCaseCommand, true);
    }

    public static String getSetUpFileName() {
        String SETUP_QUERY_FILE_NAME = Files.createTempDir()+"/setupQuery.hql";
        return SETUP_QUERY_FILE_NAME;
    }

    public static void writeToFile(String record, String cat_builder_repair_option) {

    }

}

// --hiveconf fs.default.name=file:///tmp/beetest --hiveconf mapred.job.tracker=local --hiveconf mapreduce.framework.name=local --hiveconf hive.exec.scratchdir=/tmp/beetest/scratchdir --hiveconf hive.querylog.location=/tmp/beetest/querylog --hiveconf hive.metastore.warehouse.dir=/tmp/beetest/warehouse --hiveconf hive.metastore.local=true --hiveconf javax.jdo.option.ConnectionURL='jdbc:derby:;databaseName=/tmp/beetest/metastore_db;create'=true --hiveconf javax.jdo.option.ConnectionDriverName=org.apache.derby.jdbc.EmbeddedDriver --hiveconf hive.stats.dbclass=jdbc:derby --hiveconf hive.stats.dbconnectionstring='jdbc:derby:;databaseName=/tmp/beetest/TempStatsStore;create'=true --hiveconf hive.stats.jdbcdriver=org.apache.derby.jdbc.EmbeddedDriver --hiveconf hive.cli.print.header=false --hiveconf hive.metastore.execute.setugi=true --hiveconf hive.exec.dynamic.partition.mode=nonstrict