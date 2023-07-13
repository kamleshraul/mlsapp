package org.mkcl.els.repository;

import java.io.Serializable;

import javax.persistence.TypedQuery;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Feedback;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRepository extends BaseRepository<Feedback, Serializable> {

	public Boolean findFeedbackSubmitted (final Credential credentialId, final Session sessionId) {
		try {
		StringBuffer strQuery = new StringBuffer("SELECT f FROM Feedback f" +
				" WHERE f.credential.id=:credentialId AND f.session.id=:sessionId ");
			
		TypedQuery<Feedback> query = this.em().createQuery(strQuery.toString(), Feedback.class);
		
		query.setParameter("credentialId", credentialId.getId());
		query.setParameter("sessionId", sessionId.getId());
//		List<Feedback> feedback = query.getResultList().is;
		if(query.getResultList() != null && !query.getResultList().isEmpty()) {
			return true;
		}
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
	  return false;	
	}
	
}
