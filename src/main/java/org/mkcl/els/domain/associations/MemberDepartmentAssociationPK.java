/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.associations.MemberDepartmentAssociationPK.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain.associations;

import java.io.Serializable;

import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Department;

/**
 * The Class MemberDepartmentAssociationPK.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class MemberDepartmentAssociationPK implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private Member member;

    private Department department;


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (member.hashCode() + department.hashCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof MemberDepartmentAssociationPK) {
            MemberDepartmentAssociationPK memberDepartmentAssociationPK = (MemberDepartmentAssociationPK) object;
            return (memberDepartmentAssociationPK.member == this.member)
                    && (memberDepartmentAssociationPK.department == this.department);
        }
        return false;
    }
}
