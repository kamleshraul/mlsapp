/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberDepartmentRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;

import org.mkcl.els.domain.associations.MemberDepartmentAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MemberDepartmentRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberDepartmentRepository extends
        BaseRepository<MemberDepartmentAssociation, Serializable> {

    /**
     * Find by member id and id.
     *
     * @param memberId the member id
     * @param recordIndex the record index
     * @return the member department association
     */
    public MemberDepartmentAssociation findByMemberIdAndId(final Long memberId,
            final int recordIndex) {
        String query = "SELECT m FROM MemberDepartmentAssociation m WHERE m.member.id="
                + memberId + " AND m.recordIndex=" + recordIndex;

        try {
            return (MemberDepartmentAssociation) this.em().createQuery(query)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            e.printStackTrace();
            return new MemberDepartmentAssociation();
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
        String query = "SELECT m FROM MemberDepartmentAssociation m WHERE m.member.id="
                + member + " ORDER BY m.recordIndex desc LIMIT 1";
        List<MemberDepartmentAssociation> associations = this.em()
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
     * @return the member department association
     */
    public MemberDepartmentAssociation findByPK(final MemberDepartmentAssociation association) {
        Search search = new Search();
        search.addFilterEqual("member", association.getMember());
        search.addFilterEqual("department", association.getDepartment());
        search.addFilterEqual("fromDate", association.getFromDate());
        search.addFilterEqual("toDate", association.getToDate());
        return (MemberDepartmentAssociation) this.searchUnique(search);
    }
}
