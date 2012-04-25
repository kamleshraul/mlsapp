/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberMinisterRepository.java
 * Created On: Apr 23, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;


import org.mkcl.els.domain.associations.MemberMinisterAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberMinisterRepository.
 *
 * @author Anand
 * @since v1.0.0
 */
@Repository
public class MemberMinisterRepository extends BaseRepository<MemberMinisterAssociation, Serializable>{
	
	/**
	 * Find by member id and id.
	 *
	 * @param memberId the member id
	 * @param recordIndex the record index
	 * @return the member minister associations
	 * @author Anand
	 * @since v1.0.0
	 */
	public MemberMinisterAssociation findByMemberIdAndId(final Long memberId,
            final int recordIndex) {
        String query = "SELECT m FROM MemberMinisterAssociation m WHERE m.member.id="
                + memberId + " AND m.recordIndex=" + recordIndex;

        try {
            return (MemberMinisterAssociation) this.em().createQuery(query)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            e.printStackTrace();
            return new MemberMinisterAssociation();
        }
    }
	
	/**
	 * Find by pk.
	 *
	 * @param association the association
	 * @return the member minister associations
	 * @author Anand
	 * @since v1.0.0
	 */
	public MemberMinisterAssociation findByPK(
            final MemberMinisterAssociation association) {
        Search search = new Search();
        search.addFilterEqual("member", association.getMember());
        search.addFilterEqual("minister", association.getMinister());
        search.addFilterEqual("fromDate", association.getFromDate());
        search.addFilterEqual("toDate", association.getToDate());
        search.addFilterEqual("remarks",association.getRemarks());
        return (MemberMinisterAssociation) this.searchUnique(search);
    }
	
	 /**
 	 * Find highest record index.
 	 *
 	 * @param member the member
 	 * @return the int
 	 * @author Anand
 	 * @since v1.0.0
 	 */
 	@SuppressWarnings("unchecked")
	    public int findHighestRecordIndex(final Long member) {
	        String query = "SELECT m FROM MemberMinisterAssociation m WHERE m.member.id="
	                + member + " ORDER BY m.recordIndex desc LIMIT 1";
	        List<MemberMinisterAssociation> associations = this.em()
	                .createQuery(query).getResultList();
	        if(associations.isEmpty()){
	        	return 0;
	        }else{
	        return associations.get(0).getRecordIndex();
	        }
	    }
}
