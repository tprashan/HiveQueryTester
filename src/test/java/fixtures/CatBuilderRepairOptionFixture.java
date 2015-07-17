package fixtures;


import datamodel.CatBuilderRepairOption;

public class CatBuilderRepairOptionFixture {
    public static CatBuilderRepairOption.CatBuilderRepairOptionBuilder defaultCatBuilderRepairOption(){
        return CatBuilderRepairOption.
                builder().
                ROID(10).
                EQUIPMENTID(10);
    }
}
