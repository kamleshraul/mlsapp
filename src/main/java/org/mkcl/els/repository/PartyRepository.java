package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Party;
import org.springframework.stereotype.Repository;

@Repository
public class PartyRepository extends BaseRepository<Party, Serializable> {
	
	public List<Party> findActiveParties(final House house, final String locale){
		try{
			StringBuffer strQuery = new StringBuffer("SELECT DISTINCT p FROM MemberPartyAssociation mpa" +
					" JOIN mpa.party p" +
					" WHERE p.isDissolved=:isDissolved" + 
					" AND mpa.house.id=:house" +
					" AND p.locale=:locale");
			
			Query query = this.em().createQuery(strQuery.toString(), Party.class);
			query.setParameter("isDissolved", false);
			query.setParameter("house", house.getId());
			query.setParameter("locale", locale);
			
			return query.getResultList();
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return null; 		
	}

}
