/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: els
 * File: org.mkcl.els.domain.associations.HouseMemberRoleAssociationPK
 * Created On: Apr 5, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;

/**
 * The Class HouseMemberRoleAssociationPK.
 *
 * @author vishals
 * @version 1.0.0
 */
public class HouseMemberRoleAssociationPK implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The from date. */
    private Date fromDate;

    /** The to date. */
    private Date toDate;

    /** The member. */
    private Member member;

    /** The role. */
    private MemberRole role;

    /** The house. */
    private House house;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (fromDate.hashCode() + toDate.hashCode() + member.hashCode()
                + role.hashCode() + house.hashCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof HouseMemberRoleAssociationPK) {
            HouseMemberRoleAssociationPK houseMemberRoleAssociationPK =
                    (HouseMemberRoleAssociationPK) object;
            return (houseMemberRoleAssociationPK.fromDate == this.fromDate)
                    && (houseMemberRoleAssociationPK.toDate == this.toDate)
                    && (houseMemberRoleAssociationPK.member == this.member)
                    && (houseMemberRoleAssociationPK.role == this.role)
                    && (houseMemberRoleAssociationPK.house == this.house);
        }
        return false;
    }
}
