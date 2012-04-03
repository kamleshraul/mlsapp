/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberMinistryAssociationPk.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Ministry;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberMinistryAssociationPk.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */

public class MemberMinistryAssociationPk implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    private transient static final long serialVersionUID = 1L;

    private Date fromDate;

    private Date toDate;

    private Member member;

    private Ministry ministry;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (fromDate.hashCode() + toDate.hashCode() + member.hashCode() + ministry
                .hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof MemberMinistryAssociationPk) {
            MemberMinistryAssociationPk memberMinistryAssociationPk = (MemberMinistryAssociationPk) object;
            return (memberMinistryAssociationPk.fromDate == this.fromDate)
                    && (memberMinistryAssociationPk.toDate == this.toDate)
                    && (memberMinistryAssociationPk.member == this.member)
                    && (memberMinistryAssociationPk.ministry == this.ministry);
            // && (memberMinistryAssociationPk.id == this.id);
        }
        return false;
    }
}
