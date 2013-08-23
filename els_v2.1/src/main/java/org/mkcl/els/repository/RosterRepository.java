package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class RosterRepository extends BaseRepository<Roster, Serializable>{

	public Roster findLastCreated(final Session session,final String locale) throws ELSException {
		String strQuery="SELECT r FROM Roster r" +
				" WHERE r.session.id=:sessionId"+
				" AND r.locale=:locale ORDER BY r.id DESC";
		try{
			TypedQuery<Roster> query=this.em().createQuery(strQuery,Roster.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("locale",locale);
			List<Roster> rosters=query.getResultList();
			if(rosters!=null&&!rosters.isEmpty()){
				return rosters.get(0);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("RosterRepository_Roster_findLastCreated", "Last created Roster  Not found");
			throw elsException;
		}
		
	}
}
