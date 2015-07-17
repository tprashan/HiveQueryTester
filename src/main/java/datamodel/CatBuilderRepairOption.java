package datamodel;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CatBuilderRepairOption {
    Integer ROID ;
    Integer EQUIPMENTID ;
    Integer REPAIROPTIONTYPEID ;
    Integer SMCSCOMPID ;
    Integer SMCSJOBID ;
    Integer SMCSMODIFIERID  ;
    Integer SMCSQUANTITYID  ;
    Integer SMCSJOBLOCATIONID  ;
    Integer SMCSWORKAPPLICATIONID  ;
    Integer SMCSJOBCONDITIONID  ;
    Integer SMCSCABTYPEID  ;
    String BUSINESSGCODE  ;
    String SHOPFIELDINDICATOR  ;
    Integer FIRSTINTERVAL  ;
    Integer NEXTINTERVAL  ;
    Integer COMPONENTQUANTITY ;
    String DESCRIPTION  ;
    Integer INTERNALNOTEID  ;
    Integer EXTERNALNOTEID  ;
    String ADDITIONALNOTE  ;
    Double DOWNTIMEHOURS ;
    Double STANDARDHOURS ;
    Double TARGETHOURS ;
    String STATUS  ;
    Integer SEQUENCE ;
    String HASPARTS  ;
    String CALCULATEDOWNTIME  ;
    String ARRANGEMENTNO  ;
    String GROUPNO ;

    public String getStringRepresentation(String separator) {
        return "";
    }
}
