//package org.mkcl.els.test.jpa;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "state_masters")
//public class StateMaster {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    /*
//     * Now when we are dealing with manytomany relationships having extra
//     * attributes then we need to name the id fields as some meaningful names as
//     * this will be referenced in the association id class
//     */
//    private Long id;
//
//    private String name;
//
//    @OneToMany(mappedBy = "stateMaster", cascade = { CascadeType.MERGE,
//            CascadeType.PERSIST, CascadeType.REMOVE })
//    /*
//     * Here if we don't mention the inverse relationship then new tables are
//     * created for this attribute.The inverse relationship is denoted by
//     * mappedBy attribute.
//     */
//    private List<DistrictStateAssociation> districtStateAssociations;
//
//    public StateMaster() {
//        super();
//    }
//
//    public StateMaster(String name) {
//        super();
//        this.name = name;
//        this.districtStateAssociations = new ArrayList<DistrictStateAssociation>();
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public List<DistrictStateAssociation> getDistrictStateAssociations() {
//        return districtStateAssociations;
//    }
//
//    public void setDistrictStateAssociations(
//            List<DistrictStateAssociation> districtStateAssociations) {
//        this.districtStateAssociations = districtStateAssociations;
//    }
//
//    /*
//     * Here adding district must be a batch/bulk operation.For this to work the
//     * auto generation of ids needs to be stopped i.e while doing batch/bulk
//     * insert the target objects must have their ids set. Here add and update
//     * districts method must be same as it doesnot make any difference what so
//     * ever.
//     */
//    public StateMaster addDistricts(List<DistrictMaster> districtMasters,
//            String region) {
//        for (DistrictMaster i : districtMasters) {
//            DistrictStateAssociation districtStateAssociation = new DistrictStateAssociation();
//            districtStateAssociation.setDistrictId(i.getId());
//            districtStateAssociation.setDistrictMaster(i);
//            districtStateAssociation.setRegion(region);
//            districtStateAssociation.setStateId(this.getId());
//            districtStateAssociation.setStateMaster(this);
//            this.getDistrictStateAssociations().add(districtStateAssociation);
//        }
//        return this;
//    }
//
//    /*
//     * Here the best approach will be to have only those items passed which have
//     * changed.
//     */
//
//    public StateMaster deleteDistricts(List<DistrictMaster> districtMasters,
//            String region) {
//
//        for (DistrictMaster i : districtMasters) {
//            DistrictStateAssociation districtStateAssociation = new DistrictStateAssociation();
//            districtStateAssociation.setDistrictId(i.getId());
//            districtStateAssociation.setDistrictMaster(i);
//            districtStateAssociation.setRegion(region);
//            districtStateAssociation.setStateId(this.getId());
//            districtStateAssociation.setStateMaster(this);
//            this.getDistrictStateAssociations().add(districtStateAssociation);
//        }
//        return this;
//    }
//
// }
