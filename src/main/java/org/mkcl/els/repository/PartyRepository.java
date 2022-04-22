package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Party;
import org.springframework.stereotype.Repository;

@Repository
public class PartyRepository extends BaseRepository<Party, Serializable> {
	
	@SuppressWarnings("unchecked")
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
	
	public String findCurrentPartySymbolPhoto(final Party party) {
		try{
			StringBuffer strQuery = new StringBuffer("SELECT DISTINCT ps.symbol FROM Party p" +
					" JOIN p.partySymbols ps" +
					" WHERE p.id=:partyId" + 
					" ORDER BY ps.changeDate DESC LIMIT 1");
			
			Query query = this.em().createQuery(strQuery.toString(), String.class);
			query.setParameter("partyId", party.getId());
			
			String currentPartySymbolPhoto = query.getSingleResult().toString();
			return currentPartySymbolPhoto;
			
		}catch(NoResultException e){
			return "";
		}catch(Exception e){
			logger.error("error", e);
			return "";
		}
	}

}
