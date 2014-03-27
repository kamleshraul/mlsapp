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
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberBallotMemberWiseCountVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseQuestionVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

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
	 * @throws ELSException 
	 */
	@SuppressWarnings("unchecked")
	public Integer assignQuestionNo(final HouseType houseType, final Session session,
			final DeviceType questionType, final String locale) throws ELSException {
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
			else if(strQuestionType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
				query = "SELECT q" +
					" FROM Question q JOIN q.session s JOIN s.house h JOIN q.type dt" +
					" WHERE h.id = " + house + 
					" AND (dt.type = '" + 
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE + "') " +
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
			else if(strQuestionType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
				query = "SELECT q" +
					" FROM Question q JOIN q.session s JOIN s.house h JOIN q.type dt" +
					" WHERE h.id = " + house + 
					" AND (dt.type = '" + 
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE + "') " +
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
	public List<RevisionHistoryVO> getRevisions(final Long questionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUESTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("questionId",questionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> questionRevisionVOs = new ArrayList<RevisionHistoryVO>();
//		for(Object i:results) {
//			Object[] o = (Object[]) i;
//			RevisionHistoryVO questionRevisionVO = new RevisionHistoryVO();
//			if(o[0] != null) {
//				questionRevisionVO.setEditedAs(o[0].toString());
//			}
//			else {
//				UserGroupType userGroupType = 
//					UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
//				questionRevisionVO.setEditedAs(userGroupType.getName());
//			}
//			questionRevisionVO.setEditedBY(o[1].toString());
//			questionRevisionVO.setEditedOn(o[2].toString());
//			questionRevisionVO.setStatus(o[3].toString());
//			questionRevisionVO.setDetails(o[4].toString());
//			questionRevisionVO.setSubject(o[5].toString());
//			if(o[6] != null){
//				questionRevisionVO.setRemarks(o[6].toString());
//			}
//			if(o[7] != null){
//				questionRevisionVO.setReason(o[7].toString());
//			}
//			if(o[8] != null){
//				questionRevisionVO.setBriefExplanation(o[8].toString());
//			}
//			
//			questionRevisionVOs.add(questionRevisionVO);
//		}
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO questionRevisionVO = new RevisionHistoryVO();
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
			if(o1!=null){
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String question=d.diff_prettyHtml(diff);
					if(question.contains("&lt;")){
						question=question.replaceAll("&lt;", "<");
					}
					if(question.contains("&gt;")){
						question=question.replaceAll("&gt;", ">");
					}
					if(question.contains("&amp;nbsp;")){
						question=question.replaceAll("&amp;nbsp;"," ");
					}
					questionRevisionVO.setDetails(question);
				}else{
					questionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				questionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty() && o1[5].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[5].toString(), o[5].toString());
					String question=d.diff_prettyHtml(diff);
					if(question.contains("&lt;")){
						question=question.replaceAll("&lt;", "<");
					}
					if(question.contains("&gt;")){
						question=question.replaceAll("&gt;", ">");
					}
					if(question.contains("&amp;nbsp;")){
						question=question.replaceAll("&amp;nbsp;"," ");
					}
					questionRevisionVO.setSubject(question);
				}else{
					questionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				questionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				questionRevisionVO.setRemarks(o[6].toString());
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId AND q.type.id=:deviceTypeId");
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.number IS NOT NULL");
		}
		
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
		
			query.append(" AND q.group.id=:groupId AND q.answeringDate IS NULL");
		}
		
		query.append(" AND q.submissionDate <=:strFinalSubmissionDate AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setMaxResults(maxNoOfQuestions);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
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
		/*String strAnsweringDate = null;
		if(!deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
			strAnsweringDate = this.answeringDateAsString(answeringDate);
		}
		String strFinalSubmissionDate = this.submissionDateAsString(finalSubmissionDate);*/

		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId "+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(
				" AND q.group.id=:groupId AND q.answeringDate.answeringDate<=:strAnsweringDate");
		}
		
		query.append(" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		
		query.append(this.getStatusFilters(internalStatuses));
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.number IS NOT NULL ORDER BY q.number ASC");
		}else{
			query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strAnsweringDate", answeringDate);
		}
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
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
		String strQuery="SELECT q FROM Question q WHERE q.session=:session AND q.number=:number";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session", session);
		query.setParameter("number", number);
		return (Question) query.getSingleResult();
	}
	
	public Question find(final Member member, final Session session, final DeviceType deviceType, final String locale) throws ELSException{
		Question question = null;
		try{
			Status internalStatus= Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			String strQuery = "SELECT MIN(q) FROM Question q" +
						" WHERE q.primaryMember.id=:memberId AND q.session.id=:sessionId"+
						" AND q.internalStatus.id=:internalStatusId AND q.type.id=:deviceTypeId"+
						" AND q.locale=:locale";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("memberId", member.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("internalStatusId", internalStatus.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			question=  (Question) query.getSingleResult();
		}catch (Exception e) {
			ELSException elsException = new ELSException();
			elsException.setParameter("QuestionRepository_Question_find(Member...)", "No data found.");
			throw elsException;			
		}
		return question;		
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
		String strQuery = "SELECT q" +
				" FROM Question q " +
				" WHERE q.primaryMember.id=:memberId AND q.session.id=:sessionId "+ 
				" AND q.type.id=:deviceTypeId AND q.internalStatus.id=:internalStatusId"+ 
				" ORDER BY q.number " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("memberId", currentMember.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("internalStatusId", internalStatus.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		questions = query.getResultList();
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id=:groupId");
		}
		query.append(" AND q.answeringDate.answeringDate=:strAnsweringDate"+
				" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
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
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strAnsweringDate", answeringDate);
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId AND q.answeringDate.answeringDate<:strAnsweringDate");
		}
		query.append(" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getQuestionFilters(excludeQuestions));

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strAnsweringDate", answeringDate);
		}
		
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id =:sessionId AND q.primaryMember.id =:memberId"+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id =:groupId AND q.answeringDate = null");
		}else{
			query.append(" AND q.number IS NOT NULL");
		}
		query.append(" AND q.submissionDate <=:strFinalSubmissionDate AND q.locale =:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getQuestionFilters(excludeQuestions));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
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
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id =:sessionId AND q.primaryMember.id =:memberId"+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId AND q.answeringDate.answeringDate <=:strAnsweringDate" );
		}
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.number IS NOT NULL" );
		}
		query.append(
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime" +
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" ORDER BY q.number ASC");
		}else{
			query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strAnsweringDate", answeringDate);
		}
		
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("locale", locale);
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id=:deviceTypeId");
		
				if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
						&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
					query.append(" AND q.group.id=:groupId AND q.answeringDate IS NULL");
				}
				
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
						&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					query.append(" AND q.number IS NOT NULL");
				}
				
				query.append(" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime" +
							" AND q.locale=:locale");
				
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strEndTime",endTime);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("locale", locale);
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
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id=:groupId");
		}
		query.append(" AND q.answeringDate.answeringDate<=:strAnsweringDate" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strAnsweringDate", answeringDate);
		tQuery.setParameter("locale", locale);
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id =:sessionId AND q.primaryMember.id =:memberId "+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id =:groupId ");
		}
		query.append(" AND q.answeringDate IS NULL"+
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale"
		);
		query.append(this.getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
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
		StringBuffer query = new StringBuffer(
				" SELECT q  FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId"+
				" AND (q.discussionDate IS NULL OR q.discussionDate<=:strDiscussionDate)" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.ballotStatus IS NULL");
		}else if(!hasParent) {
				query.append(" AND q.parent IS NULL");
		}
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.submission_date DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strDiscussionDate", answeringDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}

	//=================Added by vikas
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
	public List<Question> findByBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId "+
				" AND ( q.discussionDate IS NULL OR q.discussionDate<=:strDiscussionDate)" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime" +
				" AND q.locale=:locale" +
				" AND q.number IS NOT NULL");
		query.append(this.getStatusFilters(internalStatuses));
		Status balloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		if(isBalloted.booleanValue()){
			query.append(" AND (q.ballotStatus=:ballotStatus OR q.ballotStatus IS NULL)");
		}else{
			query.append(" AND q.ballotStatus IS NULL");
		}
		
		if(!hasParent) {
				query.append(" AND q.parent IS NULL");
		}
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strDiscussionDate", answeringDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		if(isBalloted.booleanValue()){
			tQuery.setParameter("ballotStatus", balloted);
		}
		List<Question> questions = tQuery.getResultList();
		return questions;
	}
	
	public Question getQuestionForMemberOfUniqueSubject(final Session session, final DeviceType deviceType, final Date answeringDate,  final Long memberID, final List<String> subjects, final String locale){
		Question question = null;
		Status internalStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status ballotStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		StringBuffer strQuery = new StringBuffer("SELECT q" +
			" FROM Question q JOIN q.session s JOIN q.houseType ht" +
			" WHERE q.session.id=:sessionId AND q.discussionDate IS NULL"+
			" AND q.primaryMember.id =:memberId AND q.number IS NOT NULL"+
			" AND q.internalStatus.id =:internalStatusId " +
			" AND ( q.ballotStatus.id !=:ballotStatusId OR q.ballotStatus.id IS NULL)" +
			" ORDER BY q.discussionDate ASC");
		
		TypedQuery<Question> query = this.em().createQuery(strQuery.toString(), Question.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("memberId", memberID);
		query.setParameter("internalStatusId", internalStatus.getId());
		query.setParameter("ballotStatusId",  ballotStatus.getId());
		List<Question> questions = query.getResultList();
		question = randomQuestion(questions);
		return question;
	}
	
	private Question randomQuestion(List<Question> questions){
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		List<Question> randomQuestionList = randomizeQuestions(questions);
		Question question = null;
		if(randomQuestionList.size() > 0){
			question = randomQuestionList.get(Math.abs(rnd.nextInt() % questions.size())); 
		}
		return question;		
	}
	
	private List<Question> randomizeQuestions(final List<Question> questions) {
		List<Question> newQuestions = new ArrayList<Question>();
		newQuestions.addAll(questions);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newQuestions, rnd);
		return newQuestions;
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
	public List<Member> findPrimaryMembersByBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
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
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT(q.primaryMember) FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId "+
				" AND ( q.discussionDate IS NULL OR q.discussionDate<=:strDiscussionDate)" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		
		if(isBalloted.booleanValue()){
			query.append(" AND q.ballotStatus =:ballotStatus");
		}else{
			query.append(" AND q.ballotStatus IS NULL");
		}
		
		if(!hasParent) {
			query.append(" AND q.parent IS NULL");
		}
		
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strDiscussionDate", answeringDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		if(isBalloted.booleanValue()){
			Status balloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);			
			tQuery.setParameter("ballotStatus", balloted);
		}
		
		List<Member> members = tQuery.getResultList();
		return members;
	}
	
	
	public List<Question> findQuestionsByDiscussionDateAndMember(final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {


		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.type.id =:deviceTypeId"+
				" AND q.discussionDate=:discussionDate"+
				" AND q.submissionDate>=:startTime AND q.submissionDate<=:endTime"+
				" AND q.primaryMember.id=:memberId AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
		
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.discussionDate ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.discussionDate DESC");
		}

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("discussionDate", answeringDate);
		tQuery.setParameter("startTime", startTime);
		tQuery.setParameter("endTime", endTime);
		tQuery.setParameter("memberId", memberId);
		tQuery.setParameter("locale", locale);
		List<Question> questions = tQuery.getResultList();
		return questions;
	}
	
	private String getStatusFilters(final Status[] internalStatuses,HouseType houseType) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			sb.append(" q.internalStatus.id = " + internalStatuses[i].getId());
			
			if(i < n - 1) {
				sb.append(" OR ");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	//===============
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
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT(q.primaryMember) FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId"+
				" AND (q.discussionDate IS NULL OR q.discussionDate<=:strDiscussionDate)" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		
		if(!hasParent) {
			query.append(" AND q.parent IS NULL");
		}
		
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strDiscussionDate", answeringDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
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
					String strQuery = "SELECT q " +
						" FROM Question q JOIN q.primaryMember m JOIN q.session s JOIN q.type qt"+
						" WHERE m.id=:memberId AND s.id=:sessionId AND qt.id=:deviceTypeId "+
						" AND q.locale=:locale AND q.internalStatus.type=:internalStatusType" + 
						" AND q.submissionDate>=:startTime AND q.submissionDate<=:endTime"+
						" ORDER BY q.number " + ApplicationConstants.ASC;
					Query query=this.em().createQuery(strQuery);
					query.setParameter("memberId", member.getId());
					query.setParameter("sessionId", session.getId());
					query.setParameter("deviceTypeId",questionType.getId());
					query.setParameter("locale", locale);
					query.setParameter("internalStatusType", ApplicationConstants.QUESTION_FINAL_ADMISSION);
					query.setParameter("startTime", startTime);
					query.setParameter("endTime", endTime);
					questions = query.getResultList();
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
		String strQuery = "SELECT ce FROM Question m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:questionId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("questionId", question.getId());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByQuestionNumber(final Question question, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM Question q JOIN q.clubbedEntities m" +
				" WHERE q.id=:questionId ORDER BY m.question.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("questionId", question.getId());
		return query.getResultList();
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
		String strAnsweringDate = null;
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			strAnsweringDate=FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		}
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer();
		query.append("SELECT m" +
				" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
				" WHERE hmra.fromDate<=:strActiveOn AND hmra.toDate>=:strActiveOn"+
				" AND hmra.role.id=:roleId AND hmra.house.id=:houseId "+
				" AND hmra.locale=:locale AND m.id IN");

		query.append(" (");
		query.append(" SELECT DISTINCT(q.primaryMember.id)");
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" FROM Question q");
		}else{
				query.append(" FROM Question q LEFT JOIN q.answeringDate qd");
		}
		
		query.append(" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId");
				
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(
			" AND q.group.id=:groupId AND " +
			" (qd.answeringDate<=:strAnsweringDate OR qd IS NULL)");
		}
		query.append(
			" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
			" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" )");

		query.append(" ORDER BY m.lastName " + sortOrder + ", m.firstName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("strActiveOn", activeOn);
		tQuery.setParameter("roleId", role.getId());
		tQuery.setParameter("houseId", session.getHouse().getId());
		tQuery.setParameter("locale", locale);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strAnsweringDate", answeringDate);
		}
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
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
		String strAnsweringDate = null;
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			strAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
		}
		String strStartTime = FormaterUtil.formatDateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		String strEndTime = FormaterUtil.formatDateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		StringBuffer query = new StringBuffer();
		query.append("SELECT m" +
				" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
				" WHERE hmra.fromDate<=:strActiveOn AND hmra.toDate>=:strActiveOn"+
				" AND hmra.role.id=:roleId AND hmra.house.id=:houseId "+
				" AND hmra.locale=:locale AND m.id NOT IN");

		query.append(" (");
		query.append(" SELECT DISTINCT(q.primaryMember.id)");
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append("FROM Question q");
		}else{
			query.append(" FROM Question q LEFT JOIN q.answeringDate qd");
		}
		query.append(" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId ");
		
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id=:groupId AND " +
					"(qd.answeringDate<=:strAnsweringDate OR qd IS NULL)");
		}
		query.append(
		" AND q.submissionDate >=:strStartTime AND q.submissionDate <=:strEndTime" +
		" AND q.locale =:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" )");
		query.append(" ORDER BY m.lastName " + sortOrder + ", m.firstName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("strActiveOn", activeOn);
		tQuery.setParameter("roleId", role.getId());
		tQuery.setParameter("houseId", session.getHouse().getId());
		tQuery.setParameter("locale", locale);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strAnsweringDate", answeringDate);
		}
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		List<Member> members = tQuery.getResultList();
		return members;
	}

	/**
	 * Finds a Question excluding the current deviceType.
	 *
	 * @param session the session
	 * @param number the number
	 * @return the question
	 */
	public Question findQuestionExcludingGivenDeviceTypes(final Session session, final Integer number, final String locale, Long[] deviceTypeIds) {
		List<Long> exclusiveDeviceTypeIds = new ArrayList<Long>();
		
		for(int i = 0; i < deviceTypeIds.length; i++){
			exclusiveDeviceTypeIds.add(deviceTypeIds[i]);
		}
		String strQuery = "SELECT q FROM Question q" +
						" WHERE q.session.id=:sessionId" +
						" AND q.number=:number" +
						" AND q.type.id NOT IN (:deviceTypeIds)" +
						" AND q.status.id=:status";
		
		Status admitted = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		TypedQuery<Question> jpQuery = this.em().createQuery(strQuery, Question.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("number", number);
		jpQuery.setParameter("deviceTypeIds", exclusiveDeviceTypeIds);
		jpQuery.setParameter("status", admitted.getId());
		Question question = null;
		try{
			List<Question> tempList = jpQuery.getResultList();
			if(tempList.size() > 0){
				question = tempList.get(0);
			}
			if(question.getStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
				return question;
			}else{
				return null;
			}
		}catch(Exception e){
			question = null;
		}
		return question;
	}
	
	public Question findQuestionExcludingSameDeviceType(final Session session,
			final Integer number, 
			final Long deviceTypeId) throws ELSException {
		
		Question question;
		String strQuery="SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				"q.number=:number AND q.type.id != :exclusiveDevice";
		try{
			TypedQuery<Question> query=this.em().createQuery(strQuery, Question.class);
			query.setParameter("session", session);
			query.setParameter("number", number);
			query.setParameter("exclusiveDevice", deviceTypeId);
			question = query.getSingleResult();
			if(question.getStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
				return question;
			}else{
				return null;
			}
		}catch(EntityNotFoundException e){
			question = null;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_Question_findQuestionExcludingSameDeviceType", "Cannot get the Question ");
			throw elsException;
		}
		return question;
	}

	@SuppressWarnings({ "rawtypes"})
	public MemberBallotMemberWiseReportVO findMemberWiseReportVO(final Session session, 
			final DeviceType questionType,
			final Member member,
			final String locale) throws ELSException {
		try{
		String startDate = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endDate = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME_UH);
		
		MemberBallotMemberWiseReportVO memberBallotMemberWiseReportVO =
			new MemberBallotMemberWiseReportVO();
		
		if(startDate != null && endDate != null) {
			if((!startDate.isEmpty()) && (!endDate.isEmpty())) {
				/**** Count of questions ****/
				org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUESTION_FIND_MEMBERWISE_REPORTVO_COUNTQUERY, "");
				String strquery = nativeQuery.getQuery();
				Query countQuery=this.em().createNativeQuery(strquery);
				countQuery.setParameter("sessionId",session.getId());
				countQuery.setParameter("memberId",member.getId());
				countQuery.setParameter("locale",locale);
				countQuery.setParameter("startDate",startDate);
				countQuery.setParameter("endDate",endDate);
				countQuery.setParameter("deviceTypeId",questionType.getId());
				List countResults = countQuery.getResultList();
				
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
				org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUESTION_FIND_MEMBERWISE_REPORTVO_QUESTIONQUERY, "");
				String strqQuery = qQuery.getQuery();
				Query questionQuery=this.em().createNativeQuery(strqQuery);
				questionQuery.setParameter("sessionId",session.getId());
				questionQuery.setParameter("memberId",member.getId());
				questionQuery.setParameter("locale",locale);
				questionQuery.setParameter("startDate",startDate);
				questionQuery.setParameter("endDate",endDate);
				questionQuery.setParameter("deviceTypeId",questionType.getId());
							
				List questionResults =questionQuery.getResultList();
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
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_MemberBallotMemberWiseReportVO_findMemberWiseReportVO", "Cannot get the Member ballot ");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Question> findAdmittedStarredQuestionsUHByChartDate(final Session session, 
			final DeviceType questionType,
			final Member member,
			final String locale) throws ELSException {
		try{
		CustomParameter customParameter = 
			CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
		String startTime = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME_UH);
		String endTime = session.getParameter("questions_starred_submissionFirstBatchEndDate");
		
		List<Question> questions = new ArrayList<Question>();
		if(startTime != null && endTime != null) {
			if((!startTime.isEmpty()) && (!endTime.isEmpty())) {
				if(customParameter != null) {
					String strQuery = "SELECT q" +
						" FROM Question q JOIN q.primaryMember m JOIN q.session s JOIN q.type qt" +
						" WHERE m.id=:memberId AND s.id=:sessionId AND qt.id=:deviceTypeId"+
					    " AND q.locale=:locale AND q.internalStatus.type =:internalStatusType"+ 
					    " AND q.submissionDate>=:startTime AND q.submissionDate<=:endTime"+
					    " ORDER BY q.chartAnsweringDate.answeringDate " + ApplicationConstants.ASC;
					Query query=this.em().createQuery(strQuery);
					query.setParameter("memberId", member.getId());
					query.setParameter("sessionId", session.getId());
					query.setParameter("deviceTypeId", questionType.getId());
					query.setParameter("locale", locale);
					query.setParameter("internalStatusType", ApplicationConstants.QUESTION_FINAL_ADMISSION);
					query.setParameter("startTime", startTime);
					query.setParameter("endTime", endTime);
					questions = query.getResultList();
				}
				else {
					logger.error("Custom Parameter 'DB_DATETIMEFORMAT' not set");
				}
			}
		}
		return questions;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAdmittedStarredQuestionsUHByChartDate", "Cannot get the Question ");
			throw elsException;
		}
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

	/*
	 * To find the number of halfhour discussions standalone putup by the member for assembly
	 * 
	 */
	public Integer getMemberPutupCount(final Member member, 
			final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException{
		String strQuery="SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId" +
				" AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale" +
				" AND q.number IS NOT NULL"+
				" AND q.internalStatus.id!=:rejectStatus" +
				" AND q.internalStatus.id!=:repeatRejectStatus"+
				" ORDER BY q.id DESC";
		try{
			Status rejectedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION, locale);
			Status repeatRejectedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_REPEATREJECTION, locale);
			
			TypedQuery<Question> query=this.em().createQuery(strQuery, Question.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("memberId", member.getId());
			query.setParameter("locale", locale);		
			query.setParameter("rejectStatus",  rejectedStatus.getId());
			query.setParameter("repeatRejectStatus",  repeatRejectedStatus.getId());
			List<Question> questions = query.getResultList();
			return questions.size();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_Integer_getMemberPutupCount", "Cannot get the Question ");
			throw elsException;
		}
	}
	
	
	public List<Question> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType questionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_COMPLETE, locale);
			String query="SELECT q FROM Question q WHERE q.session.id=:sessionId"+
					" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
					" AND q.locale=:locale AND q.status.id=:statusId"+
					" ORDER BY q.id DESC";
			TypedQuery<Question> q=this.em().createQuery(query, Question.class);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", questionType.getId());
			q.setParameter("memberId", primaryMember.getId());
			q.setParameter("locale", locale);
			q.setParameter("statusId", status.getId());
			q.setMaxResults(itemsCount);
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAllByMember", "Cannot get the Questions ");
			throw elsException;
		}
	}
	
	public Integer getQuestionWithoutNumber(final Member member, 
			final DeviceType deviceType, 
			final Session session,
			final String locale) throws ELSException{
		String strQuery="SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale AND q.number IS NULL";
		try{
		TypedQuery<Question> query=this.em().createQuery(strQuery, Question.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("memberId", member.getId());
		query.setParameter("locale", locale);
		List<Question> questions=query.getResultList();
		if(questions.isEmpty()){
			return 0;
		}else{
			return questions.size();
		}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_Integer_getQuestionWithoutNumber", "Cannot get the Question Count  ");
			throw elsException;
		}
				
	}
	
	public List<Question> findRejectedQuestions(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException{
		try{
		Status rejectionStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION, locale);
		Status repeatRejectionStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_REPEATREJECTION, locale);
		String strQuery="SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale AND (q.internalStatus.id=:rejectionStatusId"+
				" OR q.internalStatus.id=:repeatRejectionStatusId)";
		TypedQuery<Question> query=this.em().createQuery(strQuery, Question.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("memberId", member.getId());
		query.setParameter("locale", locale);
		query.setParameter("rejectionStatusId", rejectionStatus.getId());
		query.setParameter("repeatRejectionStatusId", repeatRejectionStatus.getId());
		return query.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_getRejectedQuestions", "Cannot get the Question ");
			throw elsException;
		}
	}
	
	
	public String findRejectedQuestionsAsString(List<Question> questions, 
			final String locale) throws ELSException{
		try{	
			String rejectedQuestions="";
			
			for(Question q: questions){
				if(questions.get(0).equals(q)){
					rejectedQuestions=q.getNumber().toString();
				}else{
					rejectedQuestions=rejectedQuestions+","+q.getNumber().toString();
				}
			}
			
			return rejectedQuestions;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_String_findRejectedQuestionAsString", "Cannot get the Rejected Questions.");
			throw elsException;
        }
	}
	
	/**
	 * Added for hds 
	 * @param questionId
	 * @param username
	 * @return
	 * @throws ELSException 
	 */
	public QuestionDraft getLatestQuestionDraftOfUser(final Long questionId, 
			final String username) throws ELSException {
		QuestionDraft questionDraft = null;
		try{
			org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUESTION_GET_LATEST_QUESTIONDRAFT_OF_USER, "");
			String strquery = nativeQuery.getQuery();
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("questionId",questionId);
			query.setParameter("username",username);  
			List result =query.getResultList();
			for(Object i : result) {
				Long draftId = Long.parseLong(i.toString());
				questionDraft = QuestionDraft.findById(QuestionDraft.class, draftId);			
				break;
			}
			return questionDraft;	
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_QuestionDraft_getLatestQuestionDraftOfUser", "Cannot get the Question Draft");
			throw elsException;
		}
	}
	
	public List<Question> findAllByFile(final Session session, 
			final DeviceType deviceType,
			final Group group,
			final Integer file, 
			final String locale) throws ELSException {
		StringBuffer strquery=new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			strquery.append(" AND q.group.id=:groupId");
		}
		strquery.append(" AND q.locale=:locale AND q.file=:file"+
				" ORDER BY q.fileIndex");
		try{
		TypedQuery<Question> q=this.em().createQuery(strquery.toString(), Question.class);
		q.setParameter("sessionId", session.getId());
		q.setParameter("deviceTypeId", deviceType.getId());
		q.setParameter("locale", locale);
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			q.setParameter("groupId", group.getId());
		}
		q.setParameter("file", file);
		return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAllByFile", "Cannot get the Question");
			throw elsException;
		}
	}

	public List<Question> findAllByStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus, 
			final Group group,
			final Integer itemsCount,
			final String locale) throws ELSException {
		StringBuffer query=new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId");
		}
		query.append(" AND q.internalStatus.id=:internalStatusId"+
				" AND q.workflowStarted=:workflowStarted AND q.parent IS  NULL"+
				" ORDER BY q.number");
		try{
			TypedQuery<Question> q=this.em().createQuery(query.toString(), Question.class);
			q.setMaxResults(itemsCount);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("locale", locale);
			if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
					&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				q.setParameter("groupId", group.getId());
			}
			q.setParameter("internalStatusId", internalStatus.getId());
			q.setParameter("workflowStarted", "NO");
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAllByStatus", "Cannot get the Question");
			throw elsException;
		}
	}

	public int findHighestFileNo(final Session session,final DeviceType deviceType,
			final String locale) throws ELSException {
			String strQuery="SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale"+
				" AND q.file IS NOT NULL"+
				" ORDER BY q.file DESC";
			try{
			TypedQuery<Question> query=this.em().createQuery(strQuery, Question.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			List<Question> questions= query.getResultList();
			if(questions==null){
				return 0;
			}else if(questions.isEmpty()){
				return 0;
			}else{
				 return questions.get(0).getFile();
			}
			}catch(Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				ELSException elsException=new ELSException();
				elsException.setParameter("QuestionRepository_int_findHighestFileNo", "Cannot get the file No.");
				throw elsException;
			}
	}

	public Reference findCurrentFile(final Question domain) throws ELSException {
		StringBuffer strQuery=new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale");
		if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				strQuery.append(" AND q.group.id=:groupId");
		}
		strQuery.append(" AND q.file IS NOT NULL"+
				" ORDER BY q.file DESC,q.fileIndex DESC");
		try{
		TypedQuery<Question> query=this.em().createQuery(strQuery.toString(), Question.class);
		query.setParameter("sessionId", domain.getSession().getId());
		query.setParameter("deviceTypeId", domain.getType().getId());
		query.setParameter("locale", domain.getLocale());
		if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				&& domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.setParameter("groupId", domain.getGroup().getId());
		}
		List<Question> questions=query.getResultList();
		Question question=null;
		if(questions!=null&&!questions.isEmpty()){
			question=questions.get(0);
		}
		if(question==null){
			return new Reference(String.valueOf(1),String.valueOf(1));
		}else if(question.getFile()==null){
			return new Reference(String.valueOf(1),String.valueOf(1));
		}else if(question.getFile()!=null&&question.getFileIndex()==null){
			return new Reference(String.valueOf(question.getFile()),String.valueOf(1));
		}else{
			CustomParameter customParameter=CustomParameter.
			findByName(CustomParameter.class,"FILE_"+domain.getType().getType().toUpperCase(), "");
			int fileSize=Integer.parseInt(customParameter.getValue());
			StringBuffer query1=new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
					" AND q.type.id=:deviceTypeId AND q.locale=:locale");
			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
					query1.append(" AND q.group.id=:groupId");
			}
			query1.append(" AND q.file=:file AND q.file IS NOT NULL"+
					" ORDER BY q.fileIndex DESC");
			Query q=this.em().createQuery(query1.toString());
			q.setParameter("sessionId", question.getSession().getId());
			q.setParameter("deviceTypeId", question.getType().getId());
			q.setParameter("locale", question.getLocale());
			if(!(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
					&& domain.getSession().getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				q.setParameter("groupId", question.getGroup().getId());
			}
			
			q.setParameter("file", question.getFile());
			
			List<Question> qL = q.getResultList();
			Integer count = ((qL != null)? qL.size(): -1);
			
			if(count==fileSize){
				return new Reference(String.valueOf(question.getFile()+1),String.valueOf(1));
			}else{
				return new Reference(String.valueOf(question.getFile()),String.valueOf(question.getFileIndex()+1));
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_Reference_findCurrentFile", "Cannot get the current file");
			throw elsException;
		}
	}
	
	//todos 1
		@SuppressWarnings("unchecked")
		public List<Question> findAdmittedQuestionsOfGivenTypeWithoutListNumberInSession(final Long sessionId, final Long deviceTypeId) {		
			List<Question> result = new ArrayList<Question>();
			String queryString = "SELECT q FROM Question q WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId AND q.status.type=:statusType ORDER BY q.number ASC";
			Query query = this.em().createQuery(queryString);
			query.setParameter("sessionId", sessionId);
			query.setParameter("deviceTypeId", deviceTypeId);		
			query.setParameter("statusType", ApplicationConstants.QUESTION_FINAL_ADMISSION);
			CustomParameter numberOfUnstarredQuestionsInListParameter = CustomParameter.findByName(CustomParameter.class, "NUMBER_OF_UNSTARRED_QUESTIONS_IN_LIST", "");
			int numberOfUnstarredQuestionsInList = Integer.parseInt(numberOfUnstarredQuestionsInListParameter.getValue());
			List<Question> questionsToCheckForListNumber = query.getResultList();	
			int countForNumberOfUnstarredQuestionsInList = 0;
			if(questionsToCheckForListNumber!=null && !questionsToCheckForListNumber.isEmpty()) {
				for(Question q : questionsToCheckForListNumber) {
					if(countForNumberOfUnstarredQuestionsInList>=numberOfUnstarredQuestionsInList) {
						break;
					}
					if(q.getNumber()==null) {
						System.out.println("question number = " + q.getNumber());
						result.add(q);
						countForNumberOfUnstarredQuestionsInList++;
					}
				}
			} else {
				return null;
			}
			return result;
		}
		
		public Integer findHighestListNumberForAdmittedQuestionsOfGivenTypeInSession(final Long sessionId, final Long deviceTypeId) {		
			String queryString = "SELECT MAX(q.listNumber) FROM Question q WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId AND q.listNumber IS NOT NULL AND q.status.type=:statusType";
			Query query = this.em().createQuery(queryString);
			query.setParameter("sessionId", sessionId);
			query.setParameter("deviceTypeId", deviceTypeId);		
			query.setParameter("statusType", ApplicationConstants.QUESTION_FINAL_ADMISSION);
			return (Integer) query.getSingleResult();
		}
		
		public Boolean isAdmittedQuestionOfGivenTypeWithListNumberInNextSessions(final Long sessionId, final String houseType, final Long deviceTypeId) {
			boolean result=false;
			String queryString = "SELECT q.id FROM Question q WHERE q.session.id>:sessionId AND q.houseType.type=:houseType AND q.type.id=:deviceTypeId AND q.listNumber IS NOT NULL AND q.status.type=:statusType";
			Query query = this.em().createQuery(queryString);
			query.setParameter("sessionId", sessionId);
			query.setParameter("houseType", houseType);
			query.setParameter("deviceTypeId", deviceTypeId);		
			query.setParameter("statusType", ApplicationConstants.QUESTION_FINAL_ADMISSION);
			@SuppressWarnings("rawtypes")
			List questionEntries = query.getResultList();
			if(questionEntries!=null && !questionEntries.isEmpty()) {
				result=true;
			} 
			return result;
		}
		
	public Question getQuestion(final Long sessionId,final Long deviceTypeId, final Integer number,final String locale){
		String strQuery="SELECT q FROM Question q WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId AND q.number=:number AND q.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("deviceTypeId", deviceTypeId);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		Question question=(Question) query.getSingleResult();
		
		return question;
		
	}
	//==================portlet proceedings webservice method===============
	@SuppressWarnings("unchecked")
	public List<Question> findByDeviceAndStatus(final DeviceType deviceType, final Status status){
				
		String  strQuery = "SELECT q FROM Question q" +
					" WHERE q.type.id=:deviceTypeId" +
					" AND q.status.id=:statusId";
		TypedQuery<Question> jpQuery = this.em().createQuery(strQuery, Question.class);
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("statusId", status.getId());
		List<Question> qL = jpQuery.getResultList();
		
		return ((qL == null)? new ArrayList<Question>(): qL);
	}
	//======================================================================

	public QuestionDraft findPreviousDraft(final Long id) {
		String query = "SELECT qd" +
				" FROM Question q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" ORDER BY qd.id DESC";
		TypedQuery<QuestionDraft> tQuery = 
			this.em().createQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", id);
		tQuery.setMaxResults(1);
		QuestionDraft draft = tQuery.getSingleResult();
		return draft;
	}

	public QuestionDraft findSecondPreviousDraft(final Long id) {
		String query = "SELECT qd" +
				" FROM Question q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" ORDER BY qd.id DESC";
		TypedQuery<QuestionDraft> tQuery = 
			this.em().createQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", id);
		tQuery.setFirstResult(1);
		tQuery.setMaxResults(1);
		QuestionDraft draft = tQuery.getSingleResult();
		return draft;
	}
}
