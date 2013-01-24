/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BallotRepository.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.BallotEntry;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class BallotRepository
 * 
 * @author amitd
 * @since v1.0.0
 */
@Repository
public class BallotRepository extends BaseRepository<Ballot, Long> {

	public Ballot find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("session", session);
		search.addFilterEqual("deviceType", deviceType);
		search.addFilterEqual("answeringDate", answeringDate);
		search.addFilterEqual("locale", locale);
		Ballot ballot = this.searchUnique(search);
		return ballot;
	}
	
	public List<Question> findBallotedQuestions(final Member member,
			final Session session, 
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		/**
		 * Commented for performance reason. Uncomment when Caching mechanism is added
		 */
		// CustomParameter parameter = 
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		
		String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strQuery = "SELECT q" +
				" FROM Ballot b JOIN b.ballotEntries be JOIN be.questionSequences qs" +
				" JOIN qs.question q" +
				" WHERE b.session.id = " + session.getId() +
				" AND b.deviceType.id = " + deviceType.getId() +
				" AND b.answeringDate = '" + date + "'" +
				" AND b.locale = '" + locale + "'" +
				" AND be.member.id = " + member.getId();
		
		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		List<Question> questions = query.getResultList();
		return questions;
	}
	
	public BallotEntry find(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = 
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		
		String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strQuery = "SELECT be" +
			" FROM HHDBallot b JOIN b.ballotEntries be" +
			" WHERE b.session.id = " + session.getId() +
			" AND b.deviceType.id = " + deviceType.getId() +
			" AND b.answeringDate = '" + date + "'" +
			" AND b.locale = '" + locale + "'" +
			" AND be.member.id = " + member.getId();
		
		TypedQuery<BallotEntry> query = this.em().createQuery(strQuery, BallotEntry.class);
		BallotEntry ballotEntry = query.getSingleResult();
		return ballotEntry;
	}
}
