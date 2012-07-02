/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberPartyAssociationPK.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;

import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;

/**
 * The Class MemberPartyAssociationPK.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberPartyAssociationPK implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private Member member;

    private Party party;

    private Integer recordIndex;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (member.hashCode() + party.hashCode()+recordIndex.hashCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof MemberPartyAssociationPK) {
            MemberPartyAssociationPK memberPartyAssociationPK = (MemberPartyAssociationPK) object;
            return (memberPartyAssociationPK.member == this.member)
                    && (memberPartyAssociationPK.party == this.party)
                    && (memberPartyAssociationPK.recordIndex == this.recordIndex);
        }
        return false;
    }
}
