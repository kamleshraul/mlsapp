/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.SessionRepository.java
 * Created On: 26 June, 2012
 */
package org.mkcl.els.repository;

import javax.persistence.Query;

import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionRepository.
 *
 * @author Anand
 * @author Dhananjay
 * @since v1.1.0
 */
@Repository
public class SessionRepository extends BaseRepository<Session,Long>{
	
	/**
	 * Find session by year and session type.
	 *
	 * @param year the year
	 * @param sessionType the session type
	 * @param locale the locale
	 * @return the session
	 * @author Anand
	 * @author Dhananjay
	 * @since v1.1.0
	 */
	public Session findSessionByYearAndSessionType(final Integer year, final SessionType sessionType,
            final String locale){
		String query="SELECT s from Session s where s.locale='"+locale+"' AND s.year="+year+" AND s.type.sessionType='"+sessionType.getSessionType()+"'";
		Query q=this.em().createQuery(query);
		Session session=(Session) q.getSingleResult();
		return session;
	}

}
