/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberAgeWiseReportVO.java
 * Created On: Apr 19, 2012
 */
package org.mkcl.els.common.vo;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberAgeWiseReportVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberAgeWiseReportVO {

    /** The total male member count. */
    private String totalMaleMemberCount;

    /** The total female member count. */
    private String totalFemaleMemberCount;

    /** The total av male member count. */
    private String totalAvMaleMemberCount;

    /** The total av female member count. */
    private String totalAvFemaleMemberCount;

    /** The male rec not found. */
    private String maleRecNotFound;

    /** The female rec not found. */
    private String femaleRecNotFound;

    private String infoFoundFor;

    private String infoNotFoundFor;

    private String grossTotal;

    /** The member age wise v os. */
    private List<MemberAgeWiseVO> memberAgeWiseVOs;


    /**
     * Gets the total male member count.
     *
     * @return the total male member count
     */
    public String getTotalMaleMemberCount() {
        return totalMaleMemberCount;
    }


    /**
     * Sets the total male member count.
     *
     * @param totalMaleMemberCount the new total male member count
     */
    public void setTotalMaleMemberCount(final String totalMaleMemberCount) {
        this.totalMaleMemberCount = totalMaleMemberCount;
    }


    /**
     * Gets the total female member count.
     *
     * @return the total female member count
     */
    public String getTotalFemaleMemberCount() {
        return totalFemaleMemberCount;
    }


    /**
     * Sets the total female member count.
     *
     * @param totalFemaleMemberCount the new total female member count
     */
    public void setTotalFemaleMemberCount(final String totalFemaleMemberCount) {
        this.totalFemaleMemberCount = totalFemaleMemberCount;
    }


    /**
     * Gets the total av male member count.
     *
     * @return the total av male member count
     */
    public String getTotalAvMaleMemberCount() {
        return totalAvMaleMemberCount;
    }


    /**
     * Sets the total av male member count.
     *
     * @param totalAvMaleMemberCount the new total av male member count
     */
    public void setTotalAvMaleMemberCount(final String totalAvMaleMemberCount) {
        this.totalAvMaleMemberCount = totalAvMaleMemberCount;
    }


    /**
     * Gets the total av female member count.
     *
     * @return the total av female member count
     */
    public String getTotalAvFemaleMemberCount() {
        return totalAvFemaleMemberCount;
    }


    /**
     * Sets the total av female member count.
     *
     * @param totalAvFemaleMemberCount the new total av female member count
     */
    public void setTotalAvFemaleMemberCount(final String totalAvFemaleMemberCount) {
        this.totalAvFemaleMemberCount = totalAvFemaleMemberCount;
    }


    /**
     * Gets the member age wise v os.
     *
     * @return the member age wise v os
     */
    public List<MemberAgeWiseVO> getMemberAgeWiseVOs() {
        return memberAgeWiseVOs;
    }


    /**
     * Sets the member age wise v os.
     *
     * @param memberAgeWiseVOs the new member age wise v os
     */
    public void setMemberAgeWiseVOs(final List<MemberAgeWiseVO> memberAgeWiseVOs) {
        this.memberAgeWiseVOs = memberAgeWiseVOs;
    }

    /**
     * Gets the male rec not found.
     *
     * @return the male rec not found
     */
    public String getMaleRecNotFound() {
        return maleRecNotFound;
    }



    /**
     * Sets the male rec not found.
     *
     * @param maleRecNotFound the new male rec not found
     */
    public void setMaleRecNotFound(final String maleRecNotFound) {
        this.maleRecNotFound = maleRecNotFound;
    }

    /**
     * Gets the female rec not found.
     *
     * @return the female rec not found
     */
    public String getFemaleRecNotFound() {
        return femaleRecNotFound;
    }

    /**
     * Sets the female rec not found.
     *
     * @param femaleRecNotFound the new female rec not found
     */
    public void setFemaleRecNotFound(final String femaleRecNotFound) {
        this.femaleRecNotFound = femaleRecNotFound;
    }

    public String getInfoFoundFor() {
        return infoFoundFor;
    }

    public void setInfoFoundFor(final String infoFoundFor) {
        this.infoFoundFor = infoFoundFor;
    }

    public String getInfoNotFoundFor() {
        return infoNotFoundFor;
    }

    public void setInfoNotFoundFor(final String infoNotFoundFor) {
        this.infoNotFoundFor = infoNotFoundFor;
    }

    public String getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(final String grossTotal) {
        this.grossTotal = grossTotal;
    }
}
