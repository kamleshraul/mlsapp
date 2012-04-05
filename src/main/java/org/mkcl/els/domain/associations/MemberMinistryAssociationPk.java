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

/**
 * The Class MemberMinistryAssociationPk.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */

public class MemberMinistryAssociationPk implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The from date. */
    private Date fromDate;

    /** The to date. */
    private Date toDate;

    /** The member. */
    private Member member;

    /** The ministry. */
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
    public boolean equals(final Object object) {
        if (object instanceof MemberMinistryAssociationPk) {
            MemberMinistryAssociationPk memberMinistryAssociationPk =
                    (MemberMinistryAssociationPk) object;
            return (memberMinistryAssociationPk.fromDate == this.fromDate)
                    && (memberMinistryAssociationPk.toDate == this.toDate)
                    && (memberMinistryAssociationPk.member == this.member)
                    && (memberMinistryAssociationPk.ministry == this.ministry);
        }
        return false;
    }
}
