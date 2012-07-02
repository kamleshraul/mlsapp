/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberHouseRoleRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;

import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MemberHouseRoleRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberHouseRoleRepository extends
        BaseRepository<HouseMemberRoleAssociation, Serializable> {

    /**
     * Find by member id and id.
     *
     * @param memberId the member id
     * @param recordIndex the record index
     * @return the house member role association
     */
    public HouseMemberRoleAssociation findByMemberIdAndId(final Long memberId,
            final int recordIndex) {
        String query = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id="
                + memberId + " AND m.recordIndex=" + recordIndex;

        try {
            return (HouseMemberRoleAssociation) this.em().createQuery(query)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            e.printStackTrace();
            return new HouseMemberRoleAssociation();
        }
    }

    /**
     * Find highest record index.
     *
     * @param member the member
     * @return the int
     */
    @SuppressWarnings("unchecked")
    public int findHighestRecordIndex(final Long member) {
        String query = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id="
                + member + " ORDER BY m.recordIndex desc LIMIT 1";
        List<HouseMemberRoleAssociation> associations = this.em()
                .createQuery(query).getResultList();
        if(associations.isEmpty()){
        	return 0;
        }else{
        return associations.get(0).getRecordIndex();
        }
    }

    /**
     * Find by pk.
     *
     * @param association the association
     * @return the house member role association
     */
    public HouseMemberRoleAssociation findByPK(
            final HouseMemberRoleAssociation association) {
        Search search = new Search();
        search.addFilterEqual("member", association.getMember());
        search.addFilterEqual("role", association.getRole());
        search.addFilterEqual("house", association.getHouse());
        search.addFilterEqual("recordIndex", association.getRecordIndex());
        if(association.getFromDate()==null){
            search.addFilterNull("fromDate");
        }else{
            search.addFilterEqual("fromDate", association.getFromDate());
        }
        if(association.getToDate()==null){
            search.addFilterNull("toDate");
        }else{
            search.addFilterEqual("toDate", association.getToDate());
        }
        return (HouseMemberRoleAssociation) this.searchUnique(search);
    }

	@SuppressWarnings("unchecked")
	public List<HouseMemberRoleAssociation> findByMemberIdRolePriorityHouseId(
			final Long member, final int rolepriority, final Long house, final String locale) {
		String query="SELECT hmra FROM HouseMemberRoleAssociation hmra JOIN hmra.role r JOIN hmra.member m JOIN hmra.house h" +
				" WHERE hmra.locale='"+locale+"' AND m.id="+member+" AND r.priority="+rolepriority+" AND h.id="+house;
		List<HouseMemberRoleAssociation> houseMemberRoleAssociations=this.em().createQuery(query).getResultList();
		return houseMemberRoleAssociations;
	}
}
