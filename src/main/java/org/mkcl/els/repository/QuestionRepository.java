/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.QuestionRepository.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.repository;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberBallotMemberWiseCountVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseQuestionVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class QuestionRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class QuestionRepository extends BaseRepository<Question, Long> {

	/**
	 * Assign question no.
	 *
	 * @param houseType the house type
	 * @param session the session
	 * @param questionType the question type
	 * @param locale the locale
	 * @return the integer
	 */
	@SuppressWarnings("unchecked")
	public Integer assignQuestionNo(final HouseType houseType, final Session session,
			final DeviceType questionType, final String locale) {
		String strHouseType = houseType.getType();
		String strQuestionType = questionType.getType();
		Long house = session.getHouse().getId();
		
		String query = null;
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			if(strQuestionType.equals(ApplicationConstants.STARRED_QUESTION) ||
					strQuestionType.equals(ApplicationConstants.UNSTARRED_QUESTION) ||
					strQuestionType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				query = "SELECT q" +
					" FROM Question q JOIN q.session s JOIN s.house h JOIN q.type dt" +
					" WHERE h.id = " + house + "" +
					" AND (dt.type = '" + ApplicationConstants.SHORT_NOTICE_QUESTION + "'" +
						" OR dt.type = '" + ApplicationConstants.STARRED_QUESTION + "'" +
						" OR dt.type = '" + ApplicationConstants.UNSTARRED_QUESTION + "')" +
					" ORDER BY q.number " +ApplicationConstants.DESC;
			}
			else if(strQuestionType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				query = "SELECT q" +
					" FROM Question q JOIN q.session s JOIN s.house h JOIN q.type dt" +
					" WHERE h.id = " + house + 
					" AND (dt.type = '" + 
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION + "') " +
					" ORDER BY q.number " + ApplicationConstants.DESC;
			}
		}
		else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			Session lowerHouseSession = Session.find(session.getYear(),
					session.getType().getType(), ApplicationConstants.LOWER_HOUSE);
			House lowerHouse = lowerHouseSession.getHouse();
			
			CustomParameter dbDateFormat =
				CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
			SimpleDateFormat simpleDateFormat =
				FormaterUtil.getDateFormatter(dbDateFormat.getValue(),"en_US");
			String lowerHouseFormationDate = simpleDateFormat.format(lowerHouse.getFormationDate());
			
			if(strQuestionType.equals(ApplicationConstants.STARRED_QUESTION) ||
					strQuestionType.equals(ApplicationConstants.UNSTARRED_QUESTION) ||
					strQuestionType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				query = "SELECT q" +
					" FROM Question q JOIN q.type dt JOIN q.houseType ht" +
					" WHERE ht.type = '" + ApplicationConstants.UPPER_HOUSE + "'" +
					" AND q.submissionDate >= '" + lowerHouseFormationDate + "'" +
					" AND (dt.type = '" + ApplicationConstants.SHORT_NOTICE_QUESTION + "'" +
						" OR dt.type = '" + ApplicationConstants.STARRED_QUESTION + "'" +
						" OR dt.type = '" + ApplicationConstants.UNSTARRED_QUESTION + "')" +
					" ORDER BY q.number " + ApplicationConstants.DESC;
			}
			else if(strQuestionType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				query= "SELECT q" +
					" FROM Question q JOIN q.type dt JOIN q.houseType ht " +
					" WHERE ht.type = '" + ApplicationConstants.UPPER_HOUSE + "'" +
					" AND q.submissionDate >= '" + lowerHouseFormationDate + "'" +
					" AND (dt.type = '" + 
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION + "')" +
					" ORDER BY q.number " + ApplicationConstants.DESC;
			}
		}
		try {
			List<Question> questions = this.em().createQuery(query).setFirstResult(0).
				setMaxResults(1).getResultList();
			if(questions == null) {
				return 0;
			}
			else if(questions.isEmpty()) {
				return 0;
			}
			else {
				if(questions.get(0).getNumber() == null) {
					return 0;
				}
				else {
					return questions.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Gets the revisions.
	 *
	 * @param questionId the question id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<QuestionRevisionVO> getRevisions(final Long questionId, final String locale) {
		String query = "SELECT rs.usergroup, rs.fullname, rs.editedon, rs.status," +
				" rs.question, rs.subject, rs.remark, rs.reason, rs.brief_explanation" +
				" FROM ("+
					" SELECT qd.edited_as as usergroup," +
					" concat(u.title, ' ', u.first_name, ' ', u.middle_name, ' ', u.last_name)" +
					" as fullname, qd.edited_on as editedon, s.name as status," +
					" qd.question_text as question, qd.subject as subject, qd.remarks as remark," +
					" qd.reason as reason, qd. brief_explanation as brief_explanation" +
					" FROM questions as q JOIN questions_drafts_association as qda "+
					" JOIN question_drafts as qd JOIN users as u JOIN credentials as c JOIN"+
					" status as s" +
					" WHERE q.id = qda.question_id " +
					" AND qda.question_draft_id = qd.id" +
					" AND qd.recommendationstatus_id = s.id" +
					" AND u.credential_id = c.id " +
					" AND c.username = qd.edited_by" +
					" AND q.id = " + questionId + 
					" ORDER BY qd.edited_on desc ) as rs";
		
		List results = this.em().createNativeQuery(query).getResultList();
		List<QuestionRevisionVO> questionRevisionVOs = new ArrayList<QuestionRevisionVO>();
		for(Object i:results) {
			Object[] o = (Object[]) i;
			QuestionRevisionVO questionRevisionVO = new QuestionRevisionVO();
			if(o[0] != null) {
				questionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
					UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				questionRevisionVO.setEditedAs(userGroupType.getName());
			}
			questionRevisionVO.setEditedBY(o[1].toString());
			questionRevisionVO.setEditedOn(o[2].toString());
			questionRevisionVO.setStatus(o[3].toString());
			questionRevisionVO.setQuestion(o[4].toString());
			questionRevisionVO.setSubject(o[5].toString());
			if(o[6] != null){
				questionRevisionVO.setRemarks(o[6].toString());
			}
			if(o[7] != null){
				questionRevisionVO.setReason(o[7].toString());
			}
			if(o[8] != null){
				questionRevisionVO.setBriefExplanation(o[8].toString());
			}
			
			questionRevisionVOs.add(questionRevisionVO);
		}
		return questionRevisionVOs;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of Questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfQuestions the max no of questions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findNonAnsweringDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Integer maxNoOfQuestions,
			final String sortOrder,
			final String locale) {
		String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate = " + null +
				" AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Find dated questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfQuestions the max no of questions
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findDatedQuestions(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Integer maxNoOfQuestions,
			final String locale) {
		String strAnsweringDate = this.answeringDateAsString(answeringDate);
		String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate.answeringDate <= '" + strAnsweringDate + "'" +
				" AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Find.
	 *
	 * @param session the session
	 * @param number the number
	 * @return the question
	 */
	public Question find(final Session session, final Integer number) {
		Search search = new Search();
		search.addFilterEqual("session", session);
		search.addFilterEqual("number", number);
		return this.searchUnique(search);
	}

	/**
	 * Find all.
	 *
	 * @param currentMember the current member
	 * @param session the session
	 * @param deviceType the device type
	 * @param internalStatus the internal status
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Question> findAll(final Member currentMember, final Session session,
			final DeviceType deviceType, final Status internalStatus) {
		List<Question> questions = new ArrayList<Question>();
		String query = "SELECT q" +
				" FROM Question q " +
				" WHERE q.primaryMember.id = " + currentMember.getId() +
				" AND q.session.id = " + session.getId() + 
				" AND q.type.id = " + deviceType.getId() +
				" AND q.internalStatus.id = " + internalStatus.getId() + 
				" ORDER BY q.number " + ApplicationConstants.ASC;
		questions = this.em().createQuery(query).getResultList();
		return questions;
	}

	/**
	 * Find all first batch.
	 *
	 * @param currentMember the current member
	 * @param session the session
	 * @param deviceType the device type
	 * @param internalStatus the internal status
	 * @return the list
	 */
//	public List<Question> findAllFirstBatch(final Member currentMember, final Session session,
//			final DeviceType deviceType, final Status internalStatus) {
//		List<Question> questions=new ArrayList<Question>();
//		//        Date firstBatchDate=session.getQuestionSubmissionFirstBatchDate();
//		//        Date firstBatchStartTime=session.getQuestionSubmissionFirstBatchStartTimeUH();
//		//        Date firstBatchEndTime=session.getQuestionSubmissionFirstBatchEndTimeUH();
//		//        if(firstBatchDate!=null&&firstBatchStartTime!=null&&firstBatchEndTime!=null){
//		//            Calendar calendar1=new GregorianCalendar();
//		//            calendar1.setTime(firstBatchDate);
//		//
//		//            Calendar startTime=new GregorianCalendar();
//		//            startTime.setTime(firstBatchStartTime);
//		//            startTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
//		//            startTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
//		//            startTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
//		//
//		//            Calendar endTime=new GregorianCalendar();
//		//            endTime.setTime(firstBatchEndTime);
//		//            endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
//		//            endTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
//		//            endTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
//		//
//		//            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
//		//            if(customParameter!=null){
//		//                SimpleDateFormat format= FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
//		//                String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
//		//                " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
//		//                " AND q.internalStatus.id="+internalStatus.getId()+" AND q.submissionDate>='"+format.format(startTime.getTime())+"' AND q.submissionDate>='"+format.format(endTime.getTime())+"' ORDER BY q.number "+ApplicationConstants.ASC;
//		//                questions=this.em().createQuery(query).getResultList();
//		//            }
//		//        }
//		return questions;
//	}

	/**
	 * Find all second batch.
	 *
	 * @param currentMember the current member
	 * @param session the session
	 * @param deviceType the device type
	 * @param internalStatus the internal status
	 * @return the list
	 */
//	public List<Question> findAllSecondBatch(final Member currentMember, final Session session,
//			final DeviceType deviceType, final Status internalStatus) {
//		List<Question> questions=new ArrayList<Question>();
//		//        Date secondBatchDate=session.getQuestionSubmissionSecondBatchDateUH();
//		//        Date secondBatchStartTime=session.getQuestionSubmissionSecondBatchStartTimeUH();
//		//        Date secondBatchEndTime=session.getQuestionSubmissionSecondBatchEndTimeUH();
//		//        if(secondBatchDate!=null&&secondBatchStartTime!=null&&secondBatchEndTime!=null){
//		//            Calendar calendar1=new GregorianCalendar();
//		//            calendar1.setTime(secondBatchDate);
//		//
//		//            Calendar startTime=new GregorianCalendar();
//		//            startTime.setTime(secondBatchStartTime);
//		//            startTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
//		//            startTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
//		//            startTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
//		//
//		//            Calendar endTime=new GregorianCalendar();
//		//            endTime.setTime(secondBatchEndTime);
//		//            endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
//		//            endTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
//		//            endTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
//		//
//		//            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
//		//            if(customParameter!=null){
//		//                SimpleDateFormat format= FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
//		//                String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
//		//                " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
//		//                " AND q.internalStatus.id="+internalStatus.getId()+" AND q.submissionDate>='"+format.format(startTime.getTime())+"' AND q.submissionDate>='"+format.format(endTime.getTime())+"' ORDER BY q.number "+ApplicationConstants.ASC;
//		//                questions=this.em().createQuery(query).getResultList();
//		//            }
//		//        }
//		return questions;
//	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of Questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param excludeQuestions the exclude questions
	 * @param maxNoOfQuestions the max no of questions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> find(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Question[] excludeQuestions,
			final Integer maxNoOfQuestions,
			final String sortOrder,
			final String locale) {
		String strAnsweringDate = this.answeringDateAsString(answeringDate);
		String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate.answeringDate = '" + strAnsweringDate + "'" +
				" AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getQuestionFilters(excludeQuestions));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of Questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param excludeQuestions the exclude questions
	 * @param maxNoOfQuestions the max no of questions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findBeforeAnsweringDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Question[] excludeQuestions,
			final Integer maxNoOfQuestions,
			final String sortOrder,
			final String locale) {
		String strAnsweringDate = this.answeringDateAsString(answeringDate);
		String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate.answeringDate < '" + strAnsweringDate + "'" +
				" AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getQuestionFilters(excludeQuestions));

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		List<Question> questions = tQuery.getResultList();
		if(questions != null) {
			questions = Question.sortByAnsweringDate(questions, sortOrder);
			if(questions.size() >= maxNoOfQuestions) {
				return questions.subList(0, maxNoOfQuestions);
			}
		}
		return questions;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of Questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param excludeQuestions the exclude questions
	 * @param maxNoOfQuestions the max no of questions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findNonAnsweringDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Question[] excludeQuestions,
			final Integer maxNoOfQuestions,
			final String sortOrder,
			final String locale) {
		String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate = " + null +
				" AND q.submissionDate <= '" + strFinalSubmissionDate + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getQuestionFilters(excludeQuestions));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	
	/**
	 * Find dated questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfQuestions the max no of questions
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findDatedQuestions(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final Integer maxNoOfQuestions,
			final String locale) {
		String strAnsweringDate = this.answeringDateAsString(answeringDate);

		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate.answeringDate <= '" + strAnsweringDate + "'" +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of Questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfQuestions the max no of questions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findNonAnsweringDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final Integer maxNoOfQuestions,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate = " + null +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Find dated questions.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findDatedQuestions(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String locale) {
		String strAnsweringDate = this.answeringDateAsString(answeringDate);

		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate.answeringDate <= '" + strAnsweringDate + "'" +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Find non answering date.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> findNonAnsweringDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.primaryMember.id = " + member.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND q.answeringDate = " + null +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Question> find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added

		// CustomParameter dbDateFormat =
		//    	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String strAnsweringDate = 
		//		FormaterUtil.formatDateToString(answeringDate, dbDateFormat.getValue());

		// CustomParameter parameter = 
		//		CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());

		String strDiscussionDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer(
				" SELECT q" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND" +
				" ( q.discussionDate = " + null +
				" OR" +
				" q.discussionDate <= '" + strDiscussionDate  + "' )" +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		if(! hasParent) {
			query.append(" AND q.parent = " + null);
		}
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	/**
	 * Find primary members.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<Member> findPrimaryMembers(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added

		// CustomParameter dbDateFormat =
		//    	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String strAnsweringDate = 
		//		FormaterUtil.formatDateToString(answeringDate, dbDateFormat.getValue());

		// CustomParameter parameter = 
		//		CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());

		String strDiscussionDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT(q.primaryMember)" +
				" FROM Question q" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND" +
				" ( q.discussionDate = " + null +
				" OR" +
				" q.discussionDate <= '" + strDiscussionDate  + "' )" +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'"
		);
		query.append(this.getStatusFilters(internalStatuses));
		if(! hasParent) {
			query.append(" AND q.parent = " + null);
		}
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		List<Member> members = tQuery.getResultList();
		return members;
	}
	
	/**
	 * Find admitted starred questions uh.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param member the member
	 * @param locale the locale
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Question> findAdmittedStarredQuestionsUH(final Session session,
			final DeviceType questionType, final Member member, final String locale) {
		CustomParameter customParameter = 
			CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
		String startTime = session.getParameter("questions_starred_submissionFirstBatchStartDate");
		String endTime = session.getParameter("questions_starred_submissionFirstBatchEndDate");
		List<Question> questions = new ArrayList<Question>();
		if(startTime != null && endTime != null){
			if((!startTime.isEmpty()) && (!endTime.isEmpty())){
				if(customParameter != null){
					String query = "SELECT q " +
						" FROM Question q JOIN q.primaryMember m JOIN q.session s JOIN q.type qt"+
						" WHERE m.id = " + member.getId() + 
						" AND s.id = " + session.getId() + 
						" AND qt.id = " +questionType.getId() +
						" AND q.locale = '" + locale + "'" +
						" AND q.internalStatus.type = '" + 
							ApplicationConstants.QUESTION_FINAL_ADMISSION + "'" +
						" AND q.submissionDate >= '" + startTime + "'" +
						" AND q.submissionDate <= '" + endTime +"'" +
						" ORDER BY q.number " + ApplicationConstants.ASC;
					questions = this.em().createQuery(query).getResultList();
				}
				else {
					logger.error("Custom Parameter 'DB_DATETIMEFORMAT' not set");
				}
			}
		}
		return questions;
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final Question question) {
		String query = "SELECT ce " +
				" FROM Question m JOIN m.clubbedEntities ce" +
				" WHERE m.id = " + question.getId() +
				" ORDER BY ce.position " + ApplicationConstants.ASC;
		return this.em().createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByQuestionNumber(final Question question, 
			final String sortOrder, final String locale) {
		String query = "SELECT m " +
				" FROM Question q JOIN q.clubbedEntities m" +
				" WHERE q.id = " + question.getId() + 
				" ORDER BY m.question.number " + sortOrder;
		return this.em().createQuery(query).getResultList();
	}

	public List<Member> findActiveMembersWithQuestions(final Session session,
			final MemberRole role,
			final Date activeOn,
			final DeviceType deviceType,
			final Group group,
			final Status[] internalStatuses,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added

		// CustomParameter dbDateFormat =
		//    	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String strActiveOn = FormaterUtil.formatDateToString(activeOn, dbDateFormat.getValue());
		// String strAnsweringDate = 
		//		FormaterUtil.formatDateToString(answeringDate, dbDateFormat.getValue());

		// CustomParameter dbTimestamp = 
		//		CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		// String strStartTime = 
		//		new DateFormater().formatDateToString(startTime, dbTimestamp.getValue());
		// String strEndTime = 
		//		new DateFormater().formatDateToString(endTime, dbTimestamp.getValue());

		String strActiveOn = FormaterUtil.formatDateToString(activeOn, "yyyy-MM-dd");
		String strAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer();
		query.append("SELECT m" +
				" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
				" WHERE hmra.fromDate <= '" + strActiveOn + "'" +
				" AND hmra.toDate >= '" + strActiveOn + "'" +
				" AND hmra.role.id = " + role.getId() +
				" AND hmra.house.id = " + session.getHouse().getId() +
				" AND hmra.locale = '" + locale + "'" +
				" AND m.id IN");

		query.append(" (");
		query.append(" SELECT DISTINCT(q.primaryMember.id)" +
				" FROM Question q LEFT JOIN q.answeringDate qd" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND " +
				" (qd.answeringDate <= '" + strAnsweringDate + "'" +
				" OR" +
				" qd = " + null + ")" +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" )");

		query.append(" ORDER BY m.lastName " + sortOrder + ", m.firstName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		List<Member> members = tQuery.getResultList();
		return members;
	}

	public List<Member> findActiveMembersWithoutQuestions(final Session session,
			final MemberRole role,
			final Date activeOn,
			final DeviceType deviceType,
			final Group group,
			final Status[] internalStatuses,
			final Date answeringDate,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added

		// CustomParameter dbDateFormat =
		//    	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String strActiveOn = FormaterUtil.formatDateToString(activeOn, dbDateFormat.getValue());
		// String strAnsweringDate = 
		//		FormaterUtil.formatDateToString(answeringDate, dbDateFormat.getValue());

		// CustomParameter dbTimestamp = 
		//		CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		// String strStartTime = 
		//		new DateFormater().formatDateToString(startTime, dbTimestamp.getValue());
		// String strEndTime = 
		//		new DateFormater().formatDateToString(endTime, dbTimestamp.getValue());

		String strActiveOn = FormaterUtil.formatDateToString(activeOn, "yyyy-MM-dd");
		String strAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer();
		query.append("SELECT m" +
				" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
				" WHERE hmra.fromDate <= '" + strActiveOn + "'" +
				" AND hmra.toDate >= '" + strActiveOn + "'" +
				" AND hmra.role.id = " + role.getId() +
				" AND hmra.house.id = " + session.getHouse().getId() +
				" AND hmra.locale = '" + locale + "'" +
				" AND m.id NOT IN");

		query.append(" (");
		query.append(" SELECT DISTINCT(q.primaryMember.id)" +
				" FROM Question q LEFT JOIN q.answeringDate qd" +
				" WHERE q.session.id = " + session.getId() +
				" AND q.type.id = " + deviceType.getId() +
				" AND q.group.id = " + group.getId() +
				" AND " +
				" (qd.answeringDate <= '" + strAnsweringDate + "'" +
				" OR" +
				" qd = " + null + ")" +
				" AND q.submissionDate >= '" + strStartTime + "'" +
				" AND q.submissionDate <= '" + strEndTime + "'" +
				" AND q.locale = '" + locale + "'");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" )");

		query.append(" ORDER BY m.lastName " + sortOrder + ", m.firstName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		List<Member> members = tQuery.getResultList();
		return members;
	}

	/**
	 * Find.
	 *
	 * @param session the session
	 * @param number the number
	 * @return the question
	 */
	public Question find(final Session session, final Integer number, Long deviceTypeId) {
		DeviceType deviceType = DeviceType.findById(DeviceType.class, deviceTypeId);
		Search search = new Search();
		search.addFilterEqual("session", session);
		search.addFilterEqual("number", number);
		search.addFilterNotEqual("type", deviceType);
		return this.searchUnique(search);
	}
	
	@SuppressWarnings({ "rawtypes"})
	public MemberBallotMemberWiseReportVO findMemberWiseReportVO(Session session, 
			DeviceType questionType, Member member, String locale) {
		String startDate = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endDate = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
		
		MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO =
			new MemberBallotMemberWiseReportVO();
		
		if(startDate != null && endDate != null) {
			if((!startDate.isEmpty()) && (!endDate.isEmpty())) {
				/**** Count of questions ****/
				String countQuery = "SELECT COUNT(q.id), s.name" +
						" FROM questions as q"+
						" JOIN devicetypes as dt JOIN status as s " +
						" WHERE q.session_id = " + session.getId() +
						" AND q.member_id = " + member.getId() + 
						" AND q.locale = '" + locale + "'" +
						" AND q.submission_date >= '" + startDate + "'" +
						" AND q.submission_date <= '" + endDate +"'" +
						" AND q.originaldevicetype_id = " + questionType.getId() +
						" AND q.devicetype_id = dt.id " +
						" AND q.internalstatus_id = s.id" +
						" GROUP BY q.internalstatus_id" +
						" ORDER BY s.type";
				List countResults = this.em().createNativeQuery(countQuery).getResultList();
				
				List<MemberBallotMemberWiseCountVO> countVOs = 
					new ArrayList<MemberBallotMemberWiseCountVO>();
				NumberFormat numberFormat = FormaterUtil.getNumberFormatterNoGrouping(locale);
				for(Object i : countResults) {
					Object[] o = (Object[]) i;
					MemberBallotMemberWiseCountVO memberBallotMemberWiseCountVO = 
						new MemberBallotMemberWiseCountVO();
					if(o[0] != null && o[1] != null) {
						if((!o[0].toString().isEmpty()) && (!o[1].toString().isEmpty())) {
							memberBallotMemberWiseCountVO.setCount(numberFormat.format(
									Integer.parseInt(o[0].toString())));
							memberBallotMemberWiseCountVO.setStatusType(o[1].toString());
							countVOs.add(memberBallotMemberWiseCountVO);
						}					
					}
				}
				memberBallotMemberWiseReportVO.setMemberBallotMemberWiseCountVOs(countVOs);
				/**** Member Full Name ****/
				memberBallotMemberWiseReportVO.setMember(member.getFullname());				
				/**** Questions ****/
				List<MemberBallotMemberWiseQuestionVO> questionVOs =
					new ArrayList<MemberBallotMemberWiseQuestionVO>();
				String questionQuery="SELECT q.number as questionnumber, q.subject as subject," +
					" q.rejection_reason as reason, s.name as status, g.number as groupnumber" +
					" FROM questions as q JOIN devicetypes as dt JOIN status as s" +
					" JOIN groups as g" +
					" WHERE q.session_id = " + session.getId() +
					" AND q.member_id = " + member.getId() + 
					" AND q.locale = '" + locale + "'" +
					" AND q.submission_date >= '" + startDate + "'" +
					" AND q.submission_date <= '" + endDate + "'" +
					" AND q.originaldevicetype_id = " + questionType.getId() +
					" AND q.devicetype_id = dt.id" +
					" AND q.internalstatus_id = s.id" +
					" AND q.group_id = g.id" +
					" ORDER BY g.number, s.type, q.number";
				
				String query = "SELECT rs.questionnumber, rs.subject, rs.reason, rs.status," +
					" rs.groupnumber" +
					" FROM (" + questionQuery + ") as rs";
				
				List questionResults = this.em().createNativeQuery(query).getResultList();
				int position = 1;
				for(Object i : questionResults) {
					Object[] o = (Object[]) i;
					MemberBallotMemberWiseQuestionVO questionVO = 
						new MemberBallotMemberWiseQuestionVO();
					questionVO.setSno(numberFormat.format(position));
					if(o[0] != null) {
						if(!o[0].toString().isEmpty()) {
							questionVO.setQuestionNumber(
									numberFormat.format(Integer.parseInt(o[0].toString())));							
						}
					}
					if(o[1] != null) {
						questionVO.setQuestionSubject(o[1].toString());
					}
					if(o[2] != null) {
						questionVO.setQuestionReason(o[2].toString());
					}
					if(o[3] != null) {
						questionVO.setStatusType(o[3].toString());
					}
					if(o[4] != null) {
						questionVO.setGroupNumber(o[4].toString());
						if(!o[4].toString().isEmpty()) {
							questionVO.setGroupFormattedNumber(
									numberFormat.format(Integer.parseInt(o[4].toString())));
						}
					}
					questionVOs.add(questionVO);
					position++;
				}
				memberBallotMemberWiseReportVO.setMemberBallotMemberWiseQuestionVOs(questionVOs);
			}
		}		
		return memberBallotMemberWiseReportVO;
	}

	@SuppressWarnings("unchecked")
	public List<Question> findAdmittedStarredQuestionsUHByChartDate(Session session, 
			DeviceType questionType, Member member, String locale) {
		CustomParameter customParameter = 
			CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
		String startTime = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endTime = session.getParameter("questions_starred_submissionFirstBatchEndDate");
		
		List<Question> questions = new ArrayList<Question>();
		if(startTime != null && endTime != null) {
			if((!startTime.isEmpty()) && (!endTime.isEmpty())) {
				if(customParameter != null) {
					String query = "SELECT q" +
						" FROM Question q JOIN q.primaryMember m JOIN q.session s JOIN q.type qt" +
						" WHERE m.id = " + member.getId() + 
						" AND s.id = " + session.getId() +
						" AND qt.id = " + questionType.getId() +
					    " AND q.locale = '" + locale + "'" +
					    " AND q.internalStatus.type = '" 
					    	+ ApplicationConstants.QUESTION_FINAL_ADMISSION + "'" +
					    " AND q.submissionDate >= '" + startTime + "'" +
					    " AND q.submissionDate <= '" + endTime + "'" +
					    " ORDER BY q.chartAnsweringDate.answeringDate " + ApplicationConstants.ASC;
					questions = this.em().createQuery(query).getResultList();
				}
				else {
					logger.error("Custom Parameter 'DB_DATETIMEFORMAT' not set");
				}
			}
		}
		return questions;
	}
	
	/**
	 * Gets the status filters.
	 *
	 * @param internalStatuses the internal statuses
	 * @return the status filters
	 */
	private String getStatusFilters(final Status[] internalStatuses) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			sb.append(" q.internalStatus.id = " + internalStatuses[i].getId());
			if(i < n - 1) {
				sb.append(" OR");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Submission date as string.
	 *
	 * @param date the date
	 * @return the string
	 */
	private String submissionDateAsString(final Date date) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//		"DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strDate = FormaterUtil.formatDateToString(date, "yyyy-MM-dd HH:mm:ss");
		String str = strDate.replaceFirst("00:00:00", "23:59:59");
		return str;
	}

	/**
	 * Answering date as string.
	 *
	 * @param date the date
	 * @return the string
	 */
	private String answeringDateAsString(final Date date) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//		"DB_DATEFORMAT", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strDate = FormaterUtil.formatDateToString(date, "yyyy-MM-dd");
		String str = strDate.replaceFirst("00:00:00", "23:59:59");
		return str;
	}

	/**
	 * Gets the question filters.
	 *
	 * @param excludeQuestions the exclude questions
	 * @return the question filters
	 */
	private String getQuestionFilters(final Question[] excludeQuestions) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = excludeQuestions.length;
		for(int i = 0; i < n; i++) {
			sb.append(" q.id != " + excludeQuestions[i].getId());
			if(i < n - 1) {
				sb.append(" AND");
			}
		}
		sb.append(")");
		return sb.toString();
	}

}
