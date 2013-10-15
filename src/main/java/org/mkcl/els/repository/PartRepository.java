package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.springframework.stereotype.Repository;

@Repository
public class PartRepository extends BaseRepository<Part, Serializable> {
		
	public List<PartDraft> findRevision(final Long partId, final String locale){
		String strQuery = "SELECT pd"
							+ " FROM Part p" 
							+ " JOIN p.partDrafts pd"
							+ " WHERE p.id=:partId"
							+ " AND p.locale=:locale" 
							+ " ORDER BY pd.editedOn DESC";
		
		TypedQuery<PartDraft> jpQuery = this.em().createQuery(strQuery, PartDraft.class);
		jpQuery.setParameter("partId", partId);
		jpQuery.setParameter("locale", locale);
		
		List<PartDraft> drafts = jpQuery.getResultList();
		if(drafts != null){
			return drafts;
		}
		
		return (new ArrayList<PartDraft>());
	}	
}
