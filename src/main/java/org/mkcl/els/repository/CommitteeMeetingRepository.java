package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CommitteeName;
import org.springframework.stereotype.Repository;

@Repository
public class CommitteeMeetingRepository extends BaseRepository<CommitteeMeeting, Serializable>{

	public List<CommitteeMeeting> find(final CommitteeName committeeName,
			String locale) {
		String strQuery = "SELECT cm FROM CommitteeMeeting cm"
				+ " JOIN cm.committee c"
				+ " JOIN c.committeeName cn"
				+ " WHERE cn.id=:committeeNameId"
				+ " AND cn.locale=:locale";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("committeeNameId", committeeName.getId());
		query.setParameter("locale", locale);
		return query.getResultList();
	}

}
