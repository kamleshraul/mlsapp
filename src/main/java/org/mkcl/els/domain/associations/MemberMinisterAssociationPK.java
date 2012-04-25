/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberMinisterAssociationPK.java
 * Created On: Apr 23, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;

import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Minister;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberMinisterAssociationPK.
 *
 * @author Anand
 * @since v1.0.0
 */
public class MemberMinisterAssociationPK implements Serializable{
	 /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The member. */
    private Member member;
    
    /** The minister. */
    private Minister minister;
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ( member.hashCode()
                + minister.hashCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof MemberMinisterAssociationPK) {
        	MemberMinisterAssociationPK memberMinisterAssociationPK = (MemberMinisterAssociationPK) object;
            return (memberMinisterAssociationPK.member == this.member)
                    && (memberMinisterAssociationPK.minister == this.minister);
                    
        }
        return false;
    }
}
