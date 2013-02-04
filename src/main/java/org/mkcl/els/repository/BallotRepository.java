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
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
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
			" FROM Ballot b JOIN b.ballotEntries be" +
			" WHERE b.session.id = " + session.getId() +
			" AND b.deviceType.id = " + deviceType.getId() +
			" AND b.answeringDate = '" + date + "'" +
			" AND b.locale = '" + locale + "'" +
			" AND be.member.id = " + member.getId();
		
		TypedQuery<BallotEntry> query = this.em().createQuery(strQuery, BallotEntry.class);
		BallotEntry ballotEntry = query.getSingleResult();
		return ballotEntry;
	}
	
	public List<Member> findMembersEligibleForBallot(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Status internalStatus,
			final Status ballotStatus,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = 
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		
		String strQuery = "SELECT m" +
				" FROM Member m" +
				" WHERE m.id IN (" +
					" SELECT DISTINCT(q.primaryMember.id)" +
					" FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
					" WHERE c.session.id = " + session.getId() +
					" AND c.group.id = " + group.getId() +
					" AND c.answeringDate <= '" + date + "'" +
					" AND c.locale = '" + locale + "'" +
					" AND q.parent = " + null +
					" AND q.internalStatus.id = " + internalStatus.getId() +
					" AND (" +
						" q.ballotStatus.id != " + ballotStatus.getId() +
						" OR" +
						" q.ballotStatus.id = " + null +
						" )" +
				" )" +
				" ORDER BY m.lastName ASC, m.firstName ASC";
		
		TypedQuery<Member> query = this.em().createQuery(strQuery, Member.class);
		List<Member> members = query.getResultList();
		return members;
	}
	
	public List<Question> findQuestionsEligibleForBallot(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Status internalStatus,
			final Status ballotStatus,
			final Integer noOfRounds,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = 
		//	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String date = new DateFormater().formatDateToString(answeringDate, parameter.getValue());
		String date = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		
		String strQuery = "SELECT q" +
			" FROM Chart c JOIN c.chartEntries ce JOIN ce.questions q" +
			" WHERE c.session.id = " + session.getId() +
			" AND c.group.id = " + group.getId() +
			" AND c.answeringDate <= '" + date + "'" +
			" AND c.locale = '" + locale + "'" +
			" AND ce.member.id = " + member.getId() +
			" AND q.parent = " + null +
			" AND q.internalStatus.id = " + internalStatus.getId() +
			" AND (" +
				" q.ballotStatus.id != " + ballotStatus.getId() +
				" OR" +
				" q.ballotStatus.id = " + null +
				" )" +
			" ORDER BY q.answeringDate ASC, q.priority ASC";
		
		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		query.setMaxResults(noOfRounds);
		
		List<Question> questions = query.getResultList();
		return questions;
	}
	
	public void updateBallotQuestions(final Ballot ballot,
			final Status ballotStatus) {
		Session session = ballot.getSession();
		DeviceType deviceType = ballot.getDeviceType();
		Date answeringDate = ballot.getAnsweringDate();
		String locale = ballot.getLocale();
		
		CustomParameter parameter =
            CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        String date = FormaterUtil.formatDateToString(answeringDate, parameter.getValue());
			
		String query = "UPDATE questions" +
			" SET ballotstatus_id = " + ballotStatus.getId() +
			" WHERE id IN (" +
				" SELECT qid FROM (" +
					" SELECT q.id AS qid" +
					" FROM ballots AS b JOIN ballots_ballot_entries AS bbe" +
					" JOIN ballot_entries_question_sequences AS beqs" +
					" JOIN question_sequences AS qs JOIN questions AS q" +
					" WHERE b.id = bbe.ballot_id" +
					" AND bbe.ballot_entry_id = beqs.ballot_entry_id" +
					" AND beqs.question_sequence_id = qs.id" +
					" AND qs.question_id = q.id" +
					" AND b.session_id = " + session.getId() +
					" AND b.devicetype_id = " + deviceType.getId() +
					" AND b.answering_date = '" + date + "'" +
					" AND b.locale = '" + locale + "'" +
					 " ) AS rs" +
			" )";
		this.em().createNativeQuery(query).executeUpdate();
	}
}
