package org.mkcl.els.common.vo;

import java.util.List;



public class MemberPartyDistrictWiseVO {

    private String party;

    private String totalMember;

    private String totalMale;

    private String totalFemale;

    private List<Reference> districtsWiseCount;

    public String getParty() {
        return party;
    }

    public void setParty(final String party) {
        this.party = party;
    }

    public String getTotalMember() {
        return totalMember;
    }


    public void setTotalMember(final String totalMember) {
        this.totalMember = totalMember;
    }


    public String getTotalMale() {
        return totalMale;
    }


    public void setTotalMale(final String totalMale) {
        this.totalMale = totalMale;
    }


    public String getTotalFemale() {
        return totalFemale;
    }


    public void setTotalFemale(final String totalFemale) {
        this.totalFemale = totalFemale;
    }


    public List<Reference> getDistrictsWiseCount() {
        return districtsWiseCount;
    }


    public void setDistrictsWiseCount(final List<Reference> districtsWiseCount) {
        this.districtsWiseCount = districtsWiseCount;
    }
}
