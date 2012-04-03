/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberPartyAssociationPK.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

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

    private Date fromDate;

    private Date toDate;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (member.hashCode() + party.hashCode() + fromDate.hashCode() + toDate
                .hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof MemberPartyAssociationPK) {
            MemberPartyAssociationPK memberPartyAssociationPK = (MemberPartyAssociationPK) object;
            return (memberPartyAssociationPK.member == this.member)
                    && (memberPartyAssociationPK.party == this.party)
                    && (memberPartyAssociationPK.fromDate == this.fromDate)
                    && (memberPartyAssociationPK.toDate == this.toDate);
            // && (memberPartyAssociationPK.id == this.id);
        }
        return false;
    }
}
