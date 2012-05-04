/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberPartyDistrictWiseVO.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.common.vo;

import java.util.List;

/**
 * The Class MemberPartyDistrictWiseVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberPartyDistrictWiseVO {

    /** The party. */
    private String party;

    /** The total member. */
    private String totalMember;

    /** The total male. */
    private String totalMale;

    /** The total female. */
    private String totalFemale;

    /** The districts wise count. */
    private List<Reference> districtsWiseCount;

    /**
     * Gets the party.
     *
     * @return the party
     */
    public String getParty() {
        return party;
    }

    /**
     * Sets the party.
     *
     * @param party the new party
     */
    public void setParty(final String party) {
        this.party = party;
    }

    /**
     * Gets the total member.
     *
     * @return the total member
     */
    public String getTotalMember() {
        return totalMember;
    }


    /**
     * Sets the total member.
     *
     * @param totalMember the new total member
     */
    public void setTotalMember(final String totalMember) {
        this.totalMember = totalMember;
    }


    /**
     * Gets the total male.
     *
     * @return the total male
     */
    public String getTotalMale() {
        return totalMale;
    }


    /**
     * Sets the total male.
     *
     * @param totalMale the new total male
     */
    public void setTotalMale(final String totalMale) {
        this.totalMale = totalMale;
    }


    /**
     * Gets the total female.
     *
     * @return the total female
     */
    public String getTotalFemale() {
        return totalFemale;
    }


    /**
     * Sets the total female.
     *
     * @param totalFemale the new total female
     */
    public void setTotalFemale(final String totalFemale) {
        this.totalFemale = totalFemale;
    }


    /**
     * Gets the districts wise count.
     *
     * @return the districts wise count
     */
    public List<Reference> getDistrictsWiseCount() {
        return districtsWiseCount;
    }


    /**
     * Sets the districts wise count.
     *
     * @param districtsWiseCount the new districts wise count
     */
    public void setDistrictsWiseCount(final List<Reference> districtsWiseCount) {
        this.districtsWiseCount = districtsWiseCount;
    }
}
