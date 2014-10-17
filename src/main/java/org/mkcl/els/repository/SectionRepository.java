package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Section;
import org.mkcl.els.domain.SectionDraft;
import org.springframework.stereotype.Repository;

@Repository
public class SectionRepository extends BaseRepository<Section, Serializable>{

	public SectionDraft findLatestDraftOnOrBeforeGivenTime(final Section section, final Date givenTime) {
		String strQuery="SELECT DISTINCT bd FROM Section m JOIN m.drafts bd WHERE bd.editedOn<=:givenTime"+
				" AND m.id=:sectionId ORDER BY bd.id "+ApplicationConstants.DESC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("givenTime",givenTime);
		query.setParameter("sectionId",section.getId());
		@SuppressWarnings("unchecked")
		List<SectionDraft> drafts=query.getResultList();
		if(drafts!=null&&!drafts.isEmpty()){
			return drafts.get(0);
		}
		return null;
	}
	
}
