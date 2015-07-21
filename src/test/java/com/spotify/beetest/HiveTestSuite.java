package com.spotify.beetest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        DDLRunner.class,
    ImportBuilderRepairOptionTest.class,
    QueryTest3.class,
    PartitionTest.class
})

public class HiveTestSuite{

}

