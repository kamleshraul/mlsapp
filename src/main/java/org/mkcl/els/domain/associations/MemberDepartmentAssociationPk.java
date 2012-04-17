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

import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.Member;

/**
 * The Class MemberMinistryAssociationPk.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */

public class MemberDepartmentAssociationPk implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------

    private transient static final long serialVersionUID = 1L;

    private Member member;

    private Department department;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (member.hashCode() + department
                .hashCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof MemberDepartmentAssociationPk) {
            MemberDepartmentAssociationPk memberMinistryAssociationPk = (MemberDepartmentAssociationPk) object;
            return (memberMinistryAssociationPk.member == this.member)
                    && (memberMinistryAssociationPk.department == this.department);
        }
        return false;
    }
}
