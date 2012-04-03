package org.mkcl.els.domain.associations;

import java.io.Serializable;
import java.util.Date;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;

public class HouseMemberRoleAssociationPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date fromDate;

    private Date toDate;

    private Member member;

    private MemberRole role;

    private House house;

    @Override
    public int hashCode() {
        return (fromDate.hashCode() + toDate.hashCode() + member.hashCode()
                + role.hashCode() + house.hashCode());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof HouseMemberRoleAssociationPK) {
            HouseMemberRoleAssociationPK houseMemberRoleAssociationPK = (HouseMemberRoleAssociationPK) object;
            return (houseMemberRoleAssociationPK.fromDate == this.fromDate)
                    && (houseMemberRoleAssociationPK.toDate == this.toDate)
                    && (houseMemberRoleAssociationPK.member == this.member)
                    && (houseMemberRoleAssociationPK.role == this.role)
                    && (houseMemberRoleAssociationPK.house == this.house);
        }
        return false;
    }
}
