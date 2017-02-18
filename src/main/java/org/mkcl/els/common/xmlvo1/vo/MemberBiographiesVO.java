/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.MemberBiographiesVO.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class MemberBiographiesVO.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberBiographiesVO {

    /** The member biography v os. */
    private List<MemberBiographyVO> memberBiographyVOs;

    /**
     * Instantiates a new member biographies vo.
     */
    public MemberBiographiesVO() {
        super();
        this.memberBiographyVOs = new ArrayList<MemberBiographyVO>();
    }

    /**
     * Gets the member biography v os.
     *
     * @return the member biography v os
     */
    public List<MemberBiographyVO> getMemberBiographyVOs() {
        return memberBiographyVOs;
    }

    /**
     * Sets the member biography v os.
     *
     * @param memberBiographyVOs the new member biography v os
     */
    public void setMemberBiographyVOs(final List<MemberBiographyVO> memberBiographyVOs) {
        this.memberBiographyVOs = memberBiographyVOs;
    }


}
