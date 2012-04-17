/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.HouseMemberRoleAssociationPK.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;

/**
 * The Class HouseMemberRoleAssociationPK.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class HouseMemberRoleAssociationPK implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

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
        return ( member.hashCode()
                + role.hashCode() + house.hashCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof HouseMemberRoleAssociationPK) {
            HouseMemberRoleAssociationPK houseMemberRoleAssociationPK = (HouseMemberRoleAssociationPK) object;
            return (houseMemberRoleAssociationPK.member == this.member)
                    && (houseMemberRoleAssociationPK.role == this.role)
                    && (houseMemberRoleAssociationPK.house == this.house);
        }
        return false;
    }
}
