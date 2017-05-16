package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.DocumentLink;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentLinkRepository extends BaseRepository<DocumentLink, Serializable>{

	public DocumentLink findRotationOrderLinkBySession(Session session2) throws ELSException {
		try{
			String title = "Rotation Order";
			String strQuery = "SELECT m FROM DocumentLink m  WHERE "
					+ "m.session.id=:sessionId"
					+ " AND m.title=:title";
			Query query = this.em().createQuery(strQuery);
			query.setParameter("sessionId", session2.getId());
			query.setParameter("title", title);
			List<DocumentLink> documentLinks = query.getResultList();
			if(documentLinks != null && !documentLinks.isEmpty()){
				return documentLinks.get(0);
			}else{
				return null;
			}
		}catch(Exception ex){
			ELSException elsEx = new ELSException("error", "Problem is fetching Rotation order by Session");
			throw elsEx;
		}
	}

}
