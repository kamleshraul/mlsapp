//package org.mkcl.els.test.jpa;
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.ManyToOne;
//import javax.persistence.PrimaryKeyJoinColumn;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "districts_states")
//@IdClass(value = org.mkcl.els.test.jpa.DistrictStateAssociationID.class)
//public class DistrictStateAssociation {
//
//    @Id
//    private Long stateId;
//
//    @Id
//    private Long districtId;
//
//    @ManyToOne
//    @PrimaryKeyJoinColumn(name = "stateId", referencedColumnName = "id")
//    private StateMaster stateMaster;
//
//    @ManyToOne
//    @PrimaryKeyJoinColumn(name = "districtId", referencedColumnName = "id")
//    private DistrictMaster districtMaster;
//
//    private String region;
//
//    public DistrictStateAssociation() {
//        super();
//    }
//
//    public Long getStateId() {
//        return stateId;
//    }
//
//    public void setStateId(Long stateId) {
//        this.stateId = stateId;
//    }
//
//    public Long getDistrictId() {
//        return districtId;
//    }
//
//    public void setDistrictId(Long districtId) {
//        this.districtId = districtId;
//    }
//
//    public StateMaster getStateMaster() {
//        return stateMaster;
//    }
//
//    public void setStateMaster(StateMaster stateMaster) {
//        this.stateMaster = stateMaster;
//    }
//
//    public DistrictMaster getDistrictMaster() {
//        return districtMaster;
//    }
//
//    public void setDistrictMaster(DistrictMaster districtMaster) {
//        this.districtMaster = districtMaster;
//    }
//
//    public String getRegion() {
//        return region;
//    }
//
//    public void setRegion(String region) {
//        this.region = region;
//    }
// }
