/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberProfessionWiseReportVO.java
 * Created On: Apr 19, 2012
 */
package org.mkcl.els.common.vo;

import java.util.List;


/**
 * The Class MemberProfessionWiseReportVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberProfessionWiseReportVO {

    /** The total male member count. */
    private String totalMaleMemberCount;

    /** The total female member count. */
    private String totalFemaleMemberCount;

    /** The gross total. */
    private String grossTotal;

    /** The member profession wise v os. */
    private List<MemberProfessionWiseVO> memberProfessionWiseVOs;


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
     * Gets the gross total.
     *
     * @return the gross total
     */
    public String getGrossTotal() {
        return grossTotal;
    }

    /**
     * Sets the gross total.
     *
     * @param grossTotal the new gross total
     */
    public void setGrossTotal(final String grossTotal) {
        this.grossTotal = grossTotal;
    }


    /**
     * Gets the member profession wise v os.
     *
     * @return the member profession wise v os
     */
    public List<MemberProfessionWiseVO> getMemberProfessionWiseVOs() {
        return memberProfessionWiseVOs;
    }


    /**
     * Sets the member profession wise v os.
     *
     * @param memberProfessionWiseVOs the new member profession wise v os
     */
    public void setMemberProfessionWiseVOs(
            final List<MemberProfessionWiseVO> memberProfessionWiseVOs) {
        this.memberProfessionWiseVOs = memberProfessionWiseVOs;
    }


}
