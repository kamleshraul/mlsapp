/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberPartyRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.stereotype.Repository;

/**
 * The Class MemberPartyRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberPartyRepository extends
        BaseRepository<MemberPartyAssociation, Serializable> {

    /**
     * Find by member id and id.
     *
     * @param memberId the member id
     * @param recordIndex the record index
     * @return the member party association
     */
    public MemberPartyAssociation findByMemberIdAndId(final Long memberId,
            final int recordIndex) {
        String strQuery = "SELECT m FROM MemberPartyAssociation m WHERE m.member.id=:memberId " +
        		"AND m.recordIndex=:recordIndex";

        try {
        	Query query=this.em().createQuery(strQuery);
        	query.setParameter("memberId", memberId);
        	query.setParameter("recordIndex", recordIndex);
            return (MemberPartyAssociation) query.getSingleResult();
        }catch (NoResultException e) {
            e.printStackTrace();
            return new MemberPartyAssociation();
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
        String strQuery = "SELECT m FROM MemberPartyAssociation m WHERE m.member.id=:memberId"
               + " ORDER BY m.recordIndex desc LIMIT 1";
        
        List<MemberPartyAssociation> associations = new ArrayList<MemberPartyAssociation>();
        
        try{
	        Query query=this.em().createQuery(strQuery);
	        query.setParameter("memberId", member);
	        associations = query.getResultList();
        }catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        
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
     * @return the member party association
     */
    public MemberPartyAssociation findByPK(final MemberPartyAssociation association) {
        //lesson learnt:if a field is null and is added as a filter then to enable them to form part
        //of where clause make sure null check is done otherwise null fields are not send as a part of where
        //clause.
    	StringBuffer buffer=new StringBuffer();
    	buffer.append("SELECT mpa FROM MemberPartyAssociation mpa WHERE mpa.member=:member"+
    			" AND mpa.party=:party AND mpa.recordIndex=:recordIndex" +
    			" AND mpa.fromDate=:fromDate AND mpa.toDate=:toDate");
    	MemberPartyAssociation mPartyAssociation = new MemberPartyAssociation();
    	try{
	    	Query query=this.em().createQuery(buffer.toString());
	    	query.setParameter("member", association.getMember());
	    	query.setParameter("party", association.getParty());
	    	query.setParameter("recordIndex", association.getRecordIndex());
	    	query.setParameter("fromDate", association.getFromDate());
	    	query.setParameter("toDate", association.getToDate());
	    	
	    	mPartyAssociation = (MemberPartyAssociation) query.getSingleResult();
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return mPartyAssociation;//(MemberPartyAssociation) this.searchUnique(search);
    }

	public List<Party> findActivePartiesHavingMemberInHouse(House house,
			String locale) {
		String strQuery="SELECT DISTINCT p FROM MemberPartyAssociation mpa LEFT JOIN mpa.party p " +
				"WHERE mpa.house.id=:houseId  AND p.isDissolved=false AND p.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("houseId", house.getId());
		query.setParameter("locale", locale);
		List<Party> parties=query.getResultList();
		return parties;
	}
}
