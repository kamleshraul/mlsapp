//package org.mkcl.els.test.jpa;
//
//import java.io.Serializable;
//
//public class DistrictStateAssociationID implements Serializable {
//
//    private Long stateId;
//
//    private Long districtId;
//
//    private String region;
//
//    @Override
//    public int hashCode() {
//        return (int) (stateId + districtId + region.hashCode());
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        if (object instanceof DistrictStateAssociationID) {
//            DistrictStateAssociationID districtStateAssociationID = (DistrictStateAssociationID) object;
//            return (districtStateAssociationID.stateId == this.stateId)
//                    && (districtStateAssociationID.districtId == this.districtId)
//                    && (districtStateAssociationID.region == this.region);
//        }
//        return false;
//    }
// }
