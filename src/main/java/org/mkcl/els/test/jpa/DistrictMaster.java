//package org.mkcl.els.test.jpa;
//
//import java.util.List;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "district_masters")
//public class DistrictMaster {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    private String name;
//
//    @OneToMany(mappedBy = "districtMaster")
//    /*
//     * Here if we don't mention the inverse relationship then new tables are
//     * created for this attribute.The inverse relationship is denoted by
//     * mappedBy attribute.
//     */
//    private List<DistrictStateAssociation> districtStateAssociations;
//
//    public DistrictMaster() {
//        super();
//    }
//
//    public DistrictMaster(String name) {
//        super();
//        this.name = name;
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
// }
