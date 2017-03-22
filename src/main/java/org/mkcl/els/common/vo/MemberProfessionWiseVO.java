/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberProfessionWiseVO.java
 * Created On: Apr 19, 2012
 */
package org.mkcl.els.common.vo;


/**
 * The Class MemberProfessionWiseVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberProfessionWiseVO {

    /** The profession. */
    private String profession;

    /** The total member. */
    private String totalMember;

    /** The total male. */
    private String totalMale;

    /** The total female. */
    private String totalFemale;


    /**
     * Gets the profession.
     *
     * @return the profession
     */
    public String getProfession() {
        return profession;
    }


    /**
     * Sets the profession.
     *
     * @param profession the new profession
     */
    public void setProfession(final String profession) {
        this.profession = profession;
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


}