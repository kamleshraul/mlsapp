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

import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.YaadiDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
	public synchronized Integer assignQuestionNo(final HouseType houseType, final Session session,
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
					" FROM Question q JOIN q.session s JOIN q.type dt" +
					" WHERE s.id=" + session.getId() + 
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
	public List<RevisionHistoryVO> getRevisions(final Long questionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUESTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("questionId",questionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> questionRevisionVOs = new ArrayList<RevisionHistoryVO>();
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
			if(o[1]!=null) {
				questionRevisionVO.setEditedBY(o[1].toString());
			} else {
				questionRevisionVO.setEditedBY("");
			}
			if(o[2]!=null) {
				questionRevisionVO.setEditedOn(o[2].toString());
			} else {
				questionRevisionVO.setEditedOn("");
			}
			if(o[3]!=null) {
				questionRevisionVO.setStatus(o[3].toString());
			} else {
				questionRevisionVO.setStatus("");
			}			
			if(o1!=null){
				if(o[4]!=null && !o[4].toString().isEmpty() && o1[4]!=null && !o1[4].toString().isEmpty()){
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
					if(o[4]!=null) {
						questionRevisionVO.setDetails(o[4].toString());
					} else {
						questionRevisionVO.setDetails("");
					}					
				}

			}else{
				if(o[4]!=null) {
					questionRevisionVO.setDetails(o[4].toString());
				} else {
					questionRevisionVO.setDetails("");
				}
			}
			if(o1!=null){
				if(o[5]!=null && !o[5].toString().isEmpty() && o1[5]!=null && !o1[5].toString().isEmpty()){
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
					if(o[5]!=null) {
						questionRevisionVO.setSubject(o[5].toString());
					} else {
						questionRevisionVO.setSubject("");
					}					
				}

			}else{
				if(o[5]!=null) {
					questionRevisionVO.setSubject(o[5].toString());
				} else {
					questionRevisionVO.setSubject("");
				}
			}
			if(o[6]!=null) {
				questionRevisionVO.setRemarks(o[6].toString());
			} else {
				questionRevisionVO.setRemarks("");
			}

			if(o1!=null){
				if(o[7]!=null && o1[7]!=null){
					if(!o[7].toString().isEmpty() && !o1[7].toString().isEmpty()){
						LinkedList<Diff> diff=d.diff_main(o1[7].toString(), o[7].toString());
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
						questionRevisionVO.setReason(question);
					}else{
						if(o[7] != null){
							questionRevisionVO.setReason(o[7].toString());
						}
					}

				}else{
					if(o[7] != null){
						questionRevisionVO.setReason(o[7].toString());
					}
				}
			}
				
			
			if(o1!=null){
				if(o[8]!=null && o1[8]!=null){
					if(!o[8].toString().isEmpty() && !o1[8].toString().isEmpty()){
						LinkedList<Diff> diff=d.diff_main(o1[8].toString(), o[8].toString());
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
						questionRevisionVO.setBriefExplanation(question);
					}else{
						if(o[8] != null){
							questionRevisionVO.setBriefExplanation(o[8].toString());	
						}
					}

				}else{
					if(o[8] != null){
						questionRevisionVO.setBriefExplanation(o[8].toString());
					}
				}
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

		query.append(" AND q.group.id=:groupId AND q.answeringDate IS NULL");
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
		tQuery.setParameter("groupId", group.getId());
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
		StringBuffer query = new StringBuffer(
				" SELECT q FROM Question q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId "+
				" AND q.type.id=:deviceTypeId");

		query.append(" AND q.group.id=:groupId AND q.answeringDate.answeringDate<=:strAnsweringDate");
		query.append(" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");


		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("groupId", group.getId());
		tQuery.setParameter("strAnsweringDate", answeringDate);
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

		query.append(" AND q.group.id=:groupId");
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
		tQuery.setParameter("groupId", group.getId());
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
		query.append(" AND q.group.id=:groupId AND q.answeringDate.answeringDate<:strAnsweringDate");
		query.append(" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getQuestionFilters(excludeQuestions));

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("groupId", group.getId());
		tQuery.setParameter("strAnsweringDate", answeringDate);
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
		query.append(" AND q.group.id =:groupId AND q.answeringDate = null");
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
		tQuery.setParameter("groupId", group.getId());
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
		query.append(" AND q.group.id=:groupId AND q.answeringDate.answeringDate <=:strAnsweringDate" );
		query.append(
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime" +
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		tQuery.setParameter("groupId", group.getId());
		tQuery.setParameter("strAnsweringDate", answeringDate);
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
		query.append(" AND q.group.id=:groupId AND q.answeringDate IS NULL");
		query.append(" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime AND q.locale=:locale");
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
		tQuery.setParameter("groupId", group.getId());
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
		query.append(" AND q.group.id=:groupId");
		query.append(" AND q.answeringDate.answeringDate<=:strAnsweringDate" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.answeringDate.answeringDate DESC, q.number ASC");

		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		tQuery.setParameter("groupId", group.getId());
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
		query.append(" AND q.group.id =:groupId ");
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
		tQuery.setParameter("groupId", group.getId());
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
		if(!hasParent) {
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
	@SuppressWarnings({"rawtypes" })
	public List<Question> findByBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
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
		
		StringBuffer query = null;
		
		List<Question> questions = null;
		if(isPreBallot.booleanValue()){
			query = new StringBuffer(" SELECT q.* FROM questions q" +
					" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId " +
					" AND ( q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
					" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime" +
					" AND q.locale=:locale" +
					" AND q.number IS NOT NULL");
		}else if(!isPreBallot.booleanValue()){
			//" SELECT GROUP_CONCAT(q.id),q.revised_subject FROM questions q" +
			query = new StringBuffer(
					" SELECT q.id,q.revised_subject FROM questions q" +
					" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId " +
					" AND ( q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
					" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime" +
					" AND q.locale=:locale" +
					" AND q.number IS NOT NULL");
		}
		
		query.append(this.getStatusFiltersNative(internalStatuses));
		Status balloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		if(isBalloted.booleanValue()){
			query.append(" AND (q.ballotstatus_id=:ballotStatus OR q.ballotstatus_id IS NULL)");
		}else{
			query.append(" AND q.ballotstatus_id IS NULL");
		}
		
		if(!hasParent) {
				query.append(" AND q.parent IS NULL");
		}
		
		
		if(isPreBallot.booleanValue()){
			query.append(" ORDER BY q.submission_date ASC, q.number ASC");
			
			Query tQuery = this.em().createNativeQuery(query.toString(), Question.class);
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strDiscussionDate", answeringDate);
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("locale", locale);
			
			if(isBalloted.booleanValue()){
				tQuery.setParameter("ballotStatus", balloted);
			}
			
			List data = tQuery.getResultList();
			
			if(data != null && !data.isEmpty()){
				questions = new ArrayList<Question>();
				for(Object o : data){
					Question q = (Question) o;						
					questions.add(q);
				}
			}
		}else if(!isPreBallot.booleanValue()){
			if(isMandatoryUnique.booleanValue()){
				query.append(" AND q.member_id NOT IN(SELECT" +
					" qqq.member_id FROM questions qqq" + 
					" WHERE qqq.id IN(SELECT ds.device_id" +
					" FROM ballots b" +
					" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
					" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" +
					" INNER JOIN ballot_entries_device_sequences beds ON(beds.ballot_entry_id=bbe.ballot_entry_id)" +
					" INNER JOIN device_sequences ds ON(ds.id=beds.device_sequence_id)" +
					" WHERE b.session_id=:sessionId" +
					" AND b.devicetype_id=:deviceTypeId))");
				
				query.append(" AND q.revised_subject NOT IN (SELECT" +
						" qqq.revised_subject FROM questions qqq" + 
						" WHERE qqq.id IN(SELECT ds.device_id" +
						" FROM ballots b" +
						" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
						" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" +
						" INNER JOIN ballot_entries_device_sequences beds ON(beds.ballot_entry_id=bbe.ballot_entry_id)" +
						" INNER JOIN device_sequences ds ON(ds.id=beds.device_sequence_id)" +
						" WHERE b.session_id=:sessionId" +
						" AND b.devicetype_id=:deviceTypeId))");
			}
			//query.append(" GROUP BY q.revised_subject");
			query.append(" ORDER BY q.submission_date DESC, q.number DESC");
						
			Query tQuery = this.em().createNativeQuery(query.toString());
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strDiscussionDate", answeringDate);
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("locale", locale);
			
			if(isBalloted.booleanValue()){
				tQuery.setParameter("ballotStatus", balloted);
			}
			
			List data = tQuery.getResultList();
			
			if(data != null && !data.isEmpty()){
				questions = new ArrayList<Question>();
				for(Object o : data){
					Object[] obj = ((Object[]) o);
					if(obj[0] != null){
						String[] ids = obj[0].toString().split(",");
						
						Question q = Question.findById(Question.class, new Long(ids[0]));
						
						questions.add(q);
					}
				}
			}
		}
		//query.append(" ORDER BY q.revised_subject ASC,q.member_id ASC,q.submission_date DESC");

		
		return questions;
	}
	
	@SuppressWarnings("rawtypes")
	public String findBallotedMembers(final Session session, final String memberNotice, final DeviceType deviceType){
		StringBuffer retVal = null;	
		StringBuffer query = null;
		
		if(memberNotice.equals("notice")){
			query = new StringBuffer("SELECT GROUP_CONCAT(qqq.member_id),qqq.revised_subject" +
									" FROM questions qqq" +
									" WHERE qqq.id IN(SELECT ds.device_id" + 
									" FROM ballots b" + 
									" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
									" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" + 
									" INNER JOIN ballot_entries_device_sequences beds ON(beds.ballot_entry_id=bbe.ballot_entry_id)" + 
									" INNER JOIN device_sequences ds ON(ds.id=beds.device_sequence_id)" + 
									" WHERE b.session_id=:sessionId" + 
									" AND b.devicetype_id=:deviceTypeId)");
		}else if(memberNotice.equals("member")){
			query = new StringBuffer("SELECT DISTINCT qqq.member_id,'0' AS randomData" +
										" FROM questions qqq" +
										" WHERE qqq.member_id IN(SELECT be.member_id" +
										" FROM ballots b" +
										" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
										" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" +
										" WHERE b.session_id=:sessionId" +
										" AND b.devicetype_id=:deviceTypeId)");
		}
		
		Query tQuery = this.em().createNativeQuery(query.toString());
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
				
		List data = tQuery.getResultList();
		if(memberNotice.equals("member")){
			if(data != null && !data.isEmpty()){
				retVal = new StringBuffer();
				for(int i = 0; i < data.size(); i++){
					Object[] obj = (Object[])data.get(i);
					if(obj[0] != null){
						retVal.append(obj[0].toString());
						if(i < (data.size() - 1)){
							retVal.append(",");
						}
					}
				}
			}
		}else if(memberNotice.equals("notice")){
			if(data != null && !data.isEmpty()){
				retVal = new StringBuffer();
				for(Object o : data){
					Object[] obj = (Object[])o;
					if(obj[0] != null){
						retVal.append(obj[0].toString());
						break;
					}
				}
			}
		}
		return ((retVal==null)? "":retVal.toString());
	}
	
	@SuppressWarnings("rawtypes")
	public String findBallotedSubjects(final Session session, final DeviceType deviceType){
		String retVal = null;	
		StringBuffer query = new StringBuffer("SELECT GROUP_CONCAT(qqq.revised_subject),qqq.revised_subject" +
									" FROM questions qqq" +
									" WHERE qqq.id IN(SELECT ds.device_id" + 
									" FROM ballots b" + 
									" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
									" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" + 
									" INNER JOIN ballot_entries_device_sequences beds ON(beds.ballot_entry_id=bbe.ballot_entry_id)" + 
									" INNER JOIN device_sequences ds ON(ds.id=beds.device_sequence_id)" + 
									" WHERE b.session_id=:sessionId" + 
									" AND b.devicetype_id=:deviceTypeId)");
		
		Query tQuery = this.em().createNativeQuery(query.toString());
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
				
		List data = tQuery.getResultList();
		if(data != null && !data.isEmpty()){
			for(Object o : data){
				Object[] obj = (Object[])o;
				if(obj[0] != null){
					retVal = obj[0].toString();
					break;
				}
			}
		}
		return ((retVal==null)? "":retVal.toString());
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
	@SuppressWarnings({ "rawtypes" })
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
		CustomParameter csptUseForLottery = CustomParameter.findByName(CustomParameter.class, "USE_FOR_LOTTERY", "");
		
		StringBuffer query = null;
		if(csptUseForLottery != null && csptUseForLottery.getValue() != null && !csptUseForLottery.getValue().isEmpty()){
			if(csptUseForLottery.getValue().equals("yes")){
				query = new StringBuffer("SELECT m.* FROM members m WHERE m.id IN(SELECT DISTINCT" +
						" q.member_id" +
						" FROM questions q" +
						" WHERE q.session_id=:sessionId" + 
						" AND q.devicetype_id=:deviceTypeId" +
						" AND (q.discussion_date IS NULL OR q.discussion_date=:strDiscussionDate)" +
						" AND q.submission_date>=:strStartTime" + 
						" AND q.submission_date<=:strEndTime" +
						" AND q.locale=:locale" +
						" AND q.number IS NOT NULL");
			}else if(csptUseForLottery.getValue().equals("no")){
				query = new StringBuffer("SELECT m.* FROM members m WHERE m.id IN(SELECT DISTINCT" +
						" q.member_id" +
						" FROM questions q" +
						" WHERE q.session_id=:sessionId" + 
						" AND q.devicetype_id=:deviceTypeId" +
						" AND (q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
						" AND q.submission_date>=:strStartTime" + 
						" AND q.submission_date<=:strEndTime" +
						" AND q.locale=:locale" +
						" AND q.number IS NOT NULL");
			}
		}
		
		
		query.append(this.getStatusFiltersNative(internalStatuses));
		
		if(isBalloted.booleanValue()){
			query.append(" AND q.ballotstatus_id=:ballotStatus");
		}else{
			query.append(" AND q.ballotstatus_id IS NULL");
		}
		
		if(!hasParent) {
			query.append(" AND q.parent IS NULL");
		}
		
		query.append(" AND q.member_id NOT IN(SELECT DISTINCT" +
					" be.member_id" +
					" FROM ballots b" +
					" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
					" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" +
					" WHERE b.session_id=:sessionId" +
					" AND b.devicetype_id=:deviceTypeId)" + 
					" AND q.revised_subject NOT IN(SELECT DISTINCT" +
					" qq.revised_subject" +
					" FROM questions qq" +
					" WHERE qq.session_id=:sessionId" +
					" AND qq.devicetype_id=:deviceTypeId" +
					" AND qq.ballotstatus_id IS NOT NULL)");
		
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}
		query.append(")");
		
		Query tQuery = this.em().createNativeQuery(query.toString(), Member.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strDiscussionDate", answeringDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		if(isBalloted.booleanValue()){
			Status balloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);			
			tQuery.setParameter("ballotStatus", balloted.getId());
		}
		
		List genMembers = tQuery.getResultList();
		List<Member> members = new ArrayList<Member>();
		if(genMembers != null && !genMembers.isEmpty()){
			for(Object o : genMembers){
				Member m = (Member) o;
				members.add(m);
			}
		}
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
	@Transactional
	@SuppressWarnings("unchecked")
	public List<Member> findPrimaryMembersForBallot(final Session session,
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
		CustomParameter csptUseForLottery = CustomParameter.findByName(CustomParameter.class, "USE_FOR_LOTTERY", "");
		StringBuffer query = null;
		if(csptUseForLottery != null && csptUseForLottery.getValue() != null && !csptUseForLottery.getValue().isEmpty()){
			if(csptUseForLottery.getValue().equalsIgnoreCase("yes")){
				query = new StringBuffer(
						"SELECT m.* FROM members m WHERE m.id IN (SELECT DISTINCT(q.member_id) FROM questions q" +
								" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId"+
								" AND (q.discussion_date IS NULL OR q.discussion_date=:strDiscussionDate)" +
								" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime"+
								" AND q.locale=:locale" +
								" AND q.ballotstatus_id IS NULL");
			}else if(csptUseForLottery.getValue().equalsIgnoreCase("no")){
				query = new StringBuffer(
						"SELECT m.* FROM members m WHERE m.id IN (SELECT DISTINCT(q.member_id) FROM questions q" +
								" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId"+
								" AND (q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
								" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime"+
								" AND q.locale=:locale" +
								" AND q.ballotstatus_id IS NULL");
			}
		}
		 
		query.append(this.getStatusFiltersNative(internalStatuses));
		
		if(!hasParent) {
			query.append(" AND q.parent IS NULL");
		}		
		
		query.append(" AND q.subject NOT IN(SELECT DISTINCT qqq.revised_subject FROM questions qqq WHERE qqq.session_id=:sessionId" +
				" AND qqq.devicetype_id=:deviceTypeId" +
				" AND qqq.ballotstatus_id IS NOT NULL)" +
				" AND q.revised_subject NOT IN(SELECT DISTINCT qqq.revised_subject FROM questions qqq WHERE qqq.session_id=:sessionId" +
				" AND qqq.devicetype_id=:deviceTypeId" +
				" AND qqq.ballotstatus_id IS NOT NULL)");
		
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		query.append(")");
		
		Query tQuery = this.em().createNativeQuery(query.toString(), Member.class);
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
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final Question question, final String sortOrder) {
		String strQuery = "SELECT ce FROM Question m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:questionId ORDER BY ce.position " + sortOrder;
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
	
	public List<ClubbedEntity> findClubbedEntitiesByChartAnsweringDateQuestionNumber(final Question question, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM Question q JOIN q.clubbedEntities m" +
				" WHERE q.id=:questionId ORDER BY m.question.chartAnsweringDate,m.question.number " + sortOrder;
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
		String strAnsweringDate=FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
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
		query.append(" FROM Question q LEFT JOIN q.answeringDate qd");
		query.append(" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId");
		query.append(" AND q.group.id=:groupId AND " +
			" (qd.answeringDate<=:strAnsweringDate OR qd IS NULL)");
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
		tQuery.setParameter("groupId", group.getId());
		tQuery.setParameter("strAnsweringDate", answeringDate);
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
		String strAnsweringDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
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
		query.append(" FROM Question q LEFT JOIN q.answeringDate qd");
		query.append(" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId ");
		query.append(" AND q.group.id=:groupId AND " +
				"(qd.answeringDate<=:strAnsweringDate OR qd IS NULL)");
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
		tQuery.setParameter("groupId", group.getId());
		tQuery.setParameter("strAnsweringDate", answeringDate);
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
		
		Status yaadiLaid = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_YAADILAID, locale);
		Status houseDiscussed = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_DISCUSSED, locale);
		Status balloted = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		String strQuery = "SELECT q FROM Question q" +
						" WHERE q.session.id=:sessionId" +
						" AND q.number=:number" +
						" AND q.type.id NOT IN (:deviceTypeIds)" +
						" AND q.status.id=:status" +
						" AND q.ballotStatus.id=:ballotStatus" +
						" AND (q.internalStatus.id=:yaadiLaidId OR q.recommendationStatus.id=:houseDiscussed)";
		
		Status admitted = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		TypedQuery<Question> jpQuery = this.em().createQuery(strQuery, Question.class);
		jpQuery.setParameter("sessionId", session.getId());
		jpQuery.setParameter("number", number);
		jpQuery.setParameter("deviceTypeIds", exclusiveDeviceTypeIds);
		jpQuery.setParameter("status", admitted.getId());
		jpQuery.setParameter("ballotStatus", balloted.getId());
		jpQuery.setParameter("yaadiLaidId", yaadiLaid.getId());
		jpQuery.setParameter("houseDiscussed", houseDiscussed.getId());
		
		Question question = null;
		try{
			List<Question> tempList = jpQuery.getResultList();
			if(tempList.size() > 0){
				question = tempList.get(0);
				
				if(question.getStatus().getType().equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
					return question;
				}else{
					return null;
				}
			}else{
				
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
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
		String endDate = session.getParameter(
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
		
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
				ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
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
		
		int n = internalStatuses.length;
		if(n > 0) {
			sb.append(" AND(");
		}
		
		for(int i = 0; i < n; i++) {
			sb.append(" q.internalStatus.id = " + internalStatuses[i].getId());
			if(i < n - 1) {
				sb.append(" OR");
			}
		}
		
		if(n > 0) {
			sb.append(")");
		}
		
		return sb.toString();
	}
	
	private String getStatusFiltersNative(final Status[] internalStatuses) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			sb.append(" q.internalstatus_id = " + internalStatuses[i].getId());
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
		
		int n = excludeQuestions.length;
		if(n > 0) {
			sb.append(" AND(");
		}
		
		for(int i = 0; i < n; i++) {
			sb.append(" q.id != " + excludeQuestions[i].getId());
			if(i < n - 1) {
				sb.append(" AND");
			}
		}
		
		if(n > 0) {
			sb.append(")");
		}
		
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
	
	public int findReadyToSubmitCount(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {
		Integer draftsCount = 0;
		
		String queryString = "SELECT COUNT(DISTINCT q.id) FROM questions q" +
				" INNER JOIN status sta ON (sta.id=q.status_id)" +
				" WHERE q.session_id=:sessionId" +
				" AND q.member_id=:memberId" +
				" AND q.devicetype_id=:deviceTypeId" +
				" AND sta.type LIKE '%\\_complete'" +
				" AND q.locale=:locale";
		Query query = this.em().createNativeQuery(queryString);
		query.setParameter("sessionId", session.getId());
		query.setParameter("memberId", primaryMember.getId());
		query.setParameter("deviceTypeId", deviceType.getId());		
		query.setParameter("locale", locale);
		
		@SuppressWarnings("rawtypes")
		List draftsList = query.getResultList();
		if(draftsList!=null) {
			draftsCount = ((BigInteger) draftsList.get(0)).intValue();
		}
		
		return draftsCount;
	}
	
	public List<Question> findReadyToSubmitQuestions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {		
		return this.findReadyToSubmitQuestions(session, primaryMember, deviceType, -1, locale);
	}
	
	@SuppressWarnings("unchecked")
	public List<Question> findReadyToSubmitQuestions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final Integer itemsCount,
			final String locale) {		
		String queryString = "SELECT DISTINCT q.* FROM questions q" +
				" INNER JOIN status sta ON (sta.id=q.status_id)" +
				" WHERE q.session_id=:sessionId" +
				" AND q.member_id=:memberId" +
				" AND q.devicetype_id=:deviceTypeId" +
				" AND sta.type LIKE '%\\_complete'" +
				" AND q.locale=:locale" +
				" ORDER BY q.submission_priority";
		if(itemsCount!=null && itemsCount.intValue() != -1) {
			queryString += " LIMIT :itemsCount";
		}
		Query query = this.em().createNativeQuery(queryString, Question.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("memberId", primaryMember.getId());
		query.setParameter("deviceTypeId", deviceType.getId());		
		query.setParameter("locale", locale);
		if(itemsCount!=null && itemsCount.intValue() != -1) {
			query.setParameter("itemsCount", itemsCount.intValue());
		}
		return query.getResultList();
	}
	
	public List<Question> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType questionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status = null;
			if(questionType != null){
				if(questionType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
					status = Status.findByType(ApplicationConstants.QUESTION_COMPLETE, locale);
				}
				else if(questionType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
					status = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_COMPLETE, locale);
				}
				else if(questionType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
					status = Status.findByType(ApplicationConstants.QUESTION_SHORTNOTICE_COMPLETE, locale);
				}
				else if(questionType.getType().
						equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
					status = Status.
							findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_COMPLETE, locale);
				}
			}
			
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
		strquery.append(" AND q.group.id=:groupId");
		strquery.append(" AND q.locale=:locale AND q.file=:file"+
				" ORDER BY q.fileIndex");
		try{
		TypedQuery<Question> q=this.em().createQuery(strquery.toString(), Question.class);
		q.setParameter("sessionId", session.getId());
		q.setParameter("deviceTypeId", deviceType.getId());
		q.setParameter("locale", locale);
		q.setParameter("groupId", group.getId());
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
			final SubDepartment subdepartment,
			final Integer itemsCount,
			final String locale) throws ELSException {
		
		StringBuffer strQuery = new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale"+
				" AND q.group.id=:groupId  AND q.internalStatus.id=:internalStatusId"+
				" AND (q.workflowStarted=:workflowStarted OR q.workflowStarted IS NULL)"+
				" AND ((q.internalStatus IS NOT NULL AND q.internalStatus.type LIKE '%clubbing%' AND q.parent IS NOT NULL) OR (q.parent IS  NULL))");
		/*String query = "SELECT q FROM Question q WHERE q.session.id=:sessionId" +
				" AND q.type.id=:deviceTypeId AND q.locale=:locale" +
				" AND q.group.id=:groupId  AND q.internalStatus.id=:internalStatusId" +
				" AND (q.workflowStarted=:workflowStarted OR q.workflowStarted IS NULL)" +
				" AND ((q.internalStatus IS NOT NULL AND q.internalStatus.type LIKE '%clubbing%' AND q.parent IS NOT NULL) OR (q.parent IS  NULL)))";*/
		if(subdepartment != null){
			strQuery.append(" AND q.subDepartment.id=:subdepartmentId");
		}
		strQuery.append(" ORDER BY q.number");
		try{
			TypedQuery<Question> q=this.em().createQuery(strQuery.toString(), Question.class);
			q.setMaxResults(itemsCount);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("locale", locale);
			q.setParameter("groupId", group.getId());
			q.setParameter("internalStatusId", internalStatus.getId());
			if(subdepartment != null){
				q.setParameter("subdepartmentId", subdepartment.getId());
			}
			q.setParameter("workflowStarted", "NO");
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAllByStatus",
					"Cannot get the Question");
			throw elsException;
		}
	}
	
	public List<Question> findAllByRecommendationStatus(final Session session,
			final DeviceType deviceType, 
			final Status recommendationStatus, 
			final Group group,
			final String locale) throws ELSException {
		StringBuffer query=new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale");
		query.append(" AND q.group.id=:groupId");
		query.append(" AND q.parent IS NULL");
		query.append(" AND q.recommendationStatus.id=:recommendationStatusId ORDER BY q.number");
		
		try{
			TypedQuery<Question> q=this.em().createQuery(query.toString(), Question.class);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("locale", locale);
			q.setParameter("groupId", group.getId());
			q.setParameter("recommendationStatusId", recommendationStatus.getId());
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAllByStatus", "Cannot get the Question");
			throw elsException;
		}
	}

	public List<Question> findAllForTimeoutByStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus, 
			final Group group,
			final SubDepartment subdepartment,
			final Integer itemsCount,
			final String locale) throws ELSException {
		
		StringBuffer strQuery = new StringBuffer("SELECT q FROM Question q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale"+
				" AND q.group.id=:groupId  AND q.internalStatus.id=:internalStatusId"+
				" AND q.parent IS NULL"+
				" AND q.recommendationStatus IS NOT NULL "+
				" AND (q.recommendationStatus.type LIKE '%_sendToDepartment' OR q.recommendationStatus.type LIKE '%_sendToDeskOfficer')");
		if(subdepartment != null){
			strQuery.append(" AND q.subDepartment.id=:subdepartmentId");
		}
		strQuery.append(" ORDER BY q.number");
		try{
			TypedQuery<Question> q=this.em().createQuery(strQuery.toString(), Question.class);
			q.setMaxResults(itemsCount);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("locale", locale);
			q.setParameter("groupId", group.getId());
			q.setParameter("internalStatusId", internalStatus.getId());
			if(subdepartment != null){
				q.setParameter("subdepartmentId", subdepartment.getId());
			}
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("QuestionRepository_List<Question>_findAllByStatus",
					"Cannot get the Question");
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
	
	public Question getQuestion(final Long sessionId, final Integer number,final String locale){
		String strQuery = "SELECT q FROM Question q WHERE q.session.id=:sessionId AND q.number=:number AND q.locale=:locale";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		Question question = (Question) query.getSingleResult();
		
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
		try{
		TypedQuery<QuestionDraft> tQuery = 
			this.em().createQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", id);
		tQuery.setMaxResults(1);
		QuestionDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}

	public QuestionDraft findSecondPreviousDraft(final Long id) {
		QuestionDraft draft = null;
		String query = "SELECT qd" +
					" FROM Question q join q.drafts qd" +
					" WHERE q.id=:qid" +
					" ORDER BY qd.id DESC";
		TypedQuery<QuestionDraft> tQuery = 
			this.em().createQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", id);
		List<QuestionDraft> drafts = tQuery.getResultList();
		if(drafts.size()>1){
			draft = drafts.get(1);
		}
		return draft;
	}
	
	public QuestionDraft findLatestPreviousGroupDraft(final Question question) {
		QuestionDraft latestPreviousGroupDraft = null;
		String query = "SELECT qd" +
					" FROM Question q join q.drafts qd" +
					" WHERE q.id=:qid" +
					" AND qd.recommendationStatus.id=:groupChangedStatusId" +
					" ORDER BY qd.id DESC";
		TypedQuery<QuestionDraft> tQuery = 
			this.em().createQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", question.getId());
		Status groupChangedStatus = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED, question.getLocale());
		tQuery.setParameter("groupChangedStatusId", groupChangedStatus.getId());
		List<QuestionDraft> drafts = tQuery.getResultList();
		if(!drafts.isEmpty()){
			QuestionDraft latestGroupChangedDraft = drafts.get(0);
			query = "SELECT qd" +
					" FROM Question q join q.drafts qd" +
					" WHERE q.id=:qid" +
					" AND qd.id<:latestGroupChangedDraftId" +
					" ORDER BY qd.id DESC";
			tQuery = this.em().createQuery(query, QuestionDraft.class);
			tQuery.setParameter("qid", question.getId());
			tQuery.setParameter("latestGroupChangedDraftId", latestGroupChangedDraft.getId());
			drafts = tQuery.getResultList();
		}
		if(!drafts.isEmpty()){
			latestPreviousGroupDraft = drafts.get(1); //as group change event creates 2 drafts..so we need 2nd previous draft
		}
		return latestPreviousGroupDraft;
	}
	
	public QuestionDraft findPutupDraft(final Long id, final String putupStatus, final String putupActorUsergroupName) {
		String query = "SELECT qd" +
				" FROM Question q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" AND qd.internalStatus.type LIKE :putupStatus" +
				" AND qd.editedAs=:usergroupName" +
				" ORDER BY qd.id DESC";
		TypedQuery<QuestionDraft> tQuery = 
			this.em().createQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", id);
		tQuery.setParameter("putupStatus", "question_recommend%");
		tQuery.setParameter("usergroupName", putupActorUsergroupName);
		tQuery.setMaxResults(1);
		QuestionDraft draft = tQuery.getSingleResult();
		return draft;
	}
	
//	public boolean isSubmissionDraftAbsentForQuestion(final Question question) throws ELSException {
//		boolean isSubmissionDraftAbsentForQuestion = true;
//		
//		QuestionDraft submissionDraft = null;
//		String query = "SELECT qd" +
//					" FROM Question q join q.drafts qd" +
//					" WHERE q.id=:qid" +
//					" AND qd.internalStatus.id=:submissionStatusId" +
//					" ORDER BY qd.editedOn ASC";
//		TypedQuery<QuestionDraft> tQuery = 
//			this.em().createQuery(query, QuestionDraft.class);
//		tQuery.setParameter("qid", question.getId());
//		Status submissionStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, question.getLocale());
//		submissionStatus = Question.findCorrespondingStatusForGivenQuestionType(submissionStatus, question.getOriginalType());
//		tQuery.setParameter("submissionStatusId", submissionStatus.getId());
//		List<QuestionDraft> drafts = tQuery.getResultList();
//		if(drafts!=null && !drafts.isEmpty()){
//			submissionDraft = drafts.get(0);
//		}
//		if(submissionDraft!=null && submissionDraft.getId()!=null) {
//			isSubmissionDraftAbsentForQuestion = false;
//		}
//		
//		return isSubmissionDraftAbsentForQuestion;
//	}
	
	public boolean isSubmissionDraftAbsentForQuestion(final Question question) throws ELSException {
		boolean isSubmissionDraftAbsentForQuestion = true;
		
		QuestionDraft submissionDraft = null;
		String query = "SELECT qd.id" +
					" FROM question_drafts qd" +
					" WHERE qd.question_id=:qid" +
					" AND qd.internalstatus_id=:submissionStatusId" +
					" ORDER BY qd.edited_on ASC";
		Query tQuery = 
			this.em().createNativeQuery(query, QuestionDraft.class);
		tQuery.setParameter("qid", question.getId());
		Status submissionStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, question.getLocale());
		submissionStatus = Question.findCorrespondingStatusForGivenQuestionType(submissionStatus, question.getOriginalType());
		tQuery.setParameter("submissionStatusId", submissionStatus.getId());
		@SuppressWarnings("unchecked")
		List<QuestionDraft> drafts = tQuery.getResultList();
		if(drafts!=null && !drafts.isEmpty()){
			submissionDraft = drafts.get(0);
		}
		if(submissionDraft!=null && submissionDraft.getId()!=null) {
			UserGroupType ugt = UserGroupType.findByName(UserGroupType.class, submissionDraft.getEditedAs(), question.getLocale());
			if(ugt!=null && (ugt.getType().equals(ApplicationConstants.MEMBER) || ugt.getType().equals(ApplicationConstants.TYPIST))) {
				isSubmissionDraftAbsentForQuestion = false;
			}
		}
		
		return isSubmissionDraftAbsentForQuestion;
	}
	
	public boolean isAdmittedThroughClubbing(final Question question) {
		boolean isAdmittedThroughClubbing = false;
		org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(Query.class, "keyField", "IS_QUESTION_ADMITTED_THROUGH_CLUBBING", question.getLocale());
		Query tQuery = this.em().createNativeQuery(query.getQuery(), QuestionDraft.class);
		tQuery.setParameter("questionId", question.getId());
		@SuppressWarnings("unchecked")
		List<QuestionDraft> drafts = tQuery.getResultList();
		if(drafts!=null && !drafts.isEmpty()) {
			isAdmittedThroughClubbing = true;
		}		
		return isAdmittedThroughClubbing;
	}
	
	public MemberMinister findMemberMinisterIfExists(final Question question) throws ELSException {
		MemberMinister  memberMinister = null;
		Session session = question.getSession();
		if(session==null) {
			logger.error("This question has no session.");
			throw new ELSException("question_session_null", "This question has no session.");
		}
		try{			
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					" AND mi.id=:ministryId " +
					" AND mm.ministryFromDate<=:questionDate AND (mm.ministryToDate>=:questionDate  OR mm.ministryToDate IS NULL) " +
//					" AND mm.ministryFromDate<=:questionSubmissionDate AND (mm.ministryToDate>=:questionSubmissionDate  OR mm.ministryToDate IS NULL) " +
					" AND mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", question.getLocale());
			//query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("ministryId", question.getMinistry().getId());
			if(question.getChartAnsweringDate()!=null) {
				query.setParameter("questionDate", question.getChartAnsweringDate().getAnsweringDate());
			} else {
				query.setParameter("questionDate", question.getSubmissionDate());
			}			
			//query.setParameter("questionSubmissionDate", question.getSubmissionDate());
			memberMinister = query.getSingleResult();
		}catch (NoResultException  e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		return memberMinister;
	}
	
	public MemberMinister findMemberMinisterIfExists(final Question question, final Ministry ministry) throws ELSException {
		MemberMinister  memberMinister = null;
		Session session = question.getSession();
		if(session==null) {
			logger.error("This question has no session.");
			throw new ELSException("question_session_null", "This question has no session.");
		}
		try{			
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					" AND mi.id=:ministryId AND " +
					"(mm.ministryFromDate <=:questionSubmissionDate AND (mm.ministryToDate >:questionSubmissionDate  OR mm.ministryToDate IS NULL)) AND " +
					"mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", question.getLocale());
			//query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("ministryId", ministry.getId());
			query.setParameter("questionSubmissionDate", question.getSubmissionDate());
			memberMinister = query.getSingleResult();
		}catch (NoResultException  e) {
			//As this is normal case because question may not be submitted by minister
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		return memberMinister;
	}

	public Boolean isExist(Integer number, DeviceType deviceType, Session session, String locale) {
		try{
			StringBuffer strQuery=new StringBuffer();
			strQuery.append("SELECT q FROM Question q " +
					" WHERE q.session.id=:sessionId" +
					" AND q.number=:number" +
					" AND q.locale=:locale");
			Query query=null;
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)||
					deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)||
					deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
				strQuery.append(" AND q.type.type<>'questions_halfhourdiscussion_from_question'");
				 query=this.em().createQuery(strQuery.toString());
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				strQuery.append(" AND q.type.id=:deviceTypeId"); 
				query=this.em().createQuery(strQuery.toString());
				query.setParameter("deviceTypeId", deviceType.getId());
			}
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			Question question=(Question) query.getSingleResult();
			if(question!=null){
				return true;
			}else{
				return false;
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByChartAnsDateNumber(
			final Question question,final String locale) {
		String strQuery="Select ce from Question q JOIN q.clubbedEntities ce JOIN ce.question ceq "
				+ "WHERE q.id=:question and q.locale=:locale "
				+ "ORDER BY ceq.chartAnsweringDate.answeringDate "+ApplicationConstants.ASC+","
						+ "ceq.number "+ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("question",question.getId());
		query.setParameter("locale",locale);				
		return query.getResultList();
	}

	@SuppressWarnings("rawtypes")
	public boolean containsClubbingFromSecondBatch(final Session session,
			final Member member,final Question question,final String locale) throws ELSException{
		try {
			String startTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_SECONDBATCH_SUBMISSION_STARTTIME);
			String endTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_SECONDBATCH_SUBMISSION_ENDTIME);
			Date startDate=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT,"en_US").parse(startTime);
			Date endDate=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATETIME_FORMAT,"en_US").parse(endTime);
			String strQuery="SELECT qce FROM Question q LEFT JOIN q.clubbedEntities qce "
					+ "LEFT JOIN qce.question qceq LEFT JOIN qceq.supportingMembers qceqs "
					+ "WHERE qceq.submissionDate >=:startDate AND qceq.submissionDate <=:endDate "
					+ " AND (qceq.primaryMember.id=:member OR qceqs.member.id=:member )"
					+ " AND q.id=:question";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("member",member.getId());
			query.setParameter("startDate",startDate);
			query.setParameter("endDate",endDate);
			query.setParameter("question",question.getId());
			List result=query.getResultList();
			if(result!=null && !result.isEmpty()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("Contact_Support", "Please contact support");
			throw elsException;
		}
		return false;
	}
	
	public Integer findHighestYaadiNumber(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		Integer highestYaadiNumber = null;
		if(session!=null) {			
			String deviceTypeString = null;
			if(deviceType==null) {
				deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
			} else {
				deviceTypeString = deviceType.getType();
			}
			String queryString = "SELECT MAX(q.yaadiNumber) FROM Question q"
						+ " WHERE q.type.type=:deviceTypeString"
						+ " AND q.yaadiNumber IS NOT NULL"
						+ " AND q.session.house.id=:houseId";
			String yaadiNumberingParameter = session.getParameter(deviceTypeString + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND q.yaadiLayingDate>=:sessionStartDate";
					queryString += " AND q.yaadiLayingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "question.yaadiNumberingParameterNotSet");
			}
			queryString += " AND q.locale=:locale";
			TypedQuery<Integer> query = this.em().createQuery(queryString, Integer.class);
			query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("deviceTypeString", deviceTypeString);
			if(yaadiNumberingParameter.equals("session")) {
				query.setParameter("sessionStartDate", session.getStartDate());
				query.setParameter("sessionEndDate", session.getEndDate());
			}				
			query.setParameter("locale", locale);
			try {				
				highestYaadiNumber = query.getSingleResult();
			} catch(Exception e) {
				highestYaadiNumber = 0;
			}	
			if(highestYaadiNumber==null) {
				highestYaadiNumber = 0;
			}
		}
		return highestYaadiNumber;
	}
	
	public List<Question> findQuestionsInNumberedYaadi(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final Date yaadiLayingDate, final String locale) {
		List<Question> questions = null;
		if(session!=null) {
			String deviceTypeString = null;
			if(deviceType==null) {
				deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
			} else {
				deviceTypeString = deviceType.getType();
			}
			String queryString = "SELECT q FROM Question q"
					+ " WHERE q.type.type=:deviceTypeString"
					+ " AND q.session.house.id=:houseId"
					+ " AND q.yaadiNumber=:yaadiNumber"
					+ " AND q.yaadiLayingDate=:yaadiLayingDate"
					+ " AND q.locale=:locale"
					+ " ORDER BY q.number";		
			TypedQuery<Question> query = this.em().createQuery(queryString, Question.class);
			query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("deviceTypeString", deviceTypeString);				
			query.setParameter("yaadiNumber", yaadiNumber);
			query.setParameter("yaadiLayingDate",yaadiLayingDate);
			query.setParameter("locale", locale);
			questions = query.getResultList();
		}
		return questions;
	}
	
	public Date findYaadiLayingDateForYaadi(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		Date yaadiLayingDate = null;
		if(session!=null) {
			String deviceTypeString = null;
			if(deviceType==null) {
				deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
			} else {
				deviceTypeString = deviceType.getType();
			}
			String queryString = "SELECT q.yaadiLayingDate FROM Question q"
					+ " WHERE q.type.type=:deviceTypeString"
					+ " AND q.session.house.id=:houseId"
					+ " AND q.yaadiNumber=:yaadiNumber";
			String yaadiNumberingParameter = session.getParameter(deviceTypeString + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND q.yaadiLayingDate>=:sessionStartDate";
					queryString += " AND q.yaadiLayingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "question.yaadiNumberingParameterNotSet");
			}
			queryString += " AND q.locale=:locale";
			queryString += " ORDER BY q.number";
			try {
				TypedQuery<Date> query = this.em().createQuery(queryString, Date.class);
				query.setParameter("houseId", session.getHouse().getId());
				query.setParameter("deviceTypeString", deviceTypeString);				
				if(yaadiNumberingParameter.equals("session")) {
					query.setParameter("sessionStartDate", session.getStartDate());
					query.setParameter("sessionEndDate", session.getEndDate());
				}
				query.setParameter("yaadiNumber", yaadiNumber);
				query.setParameter("locale", locale);
				yaadiLayingDate = query.getSingleResult();
			} catch(Exception e) {
				yaadiLayingDate = null;
			}
		}
		return yaadiLayingDate;
	}
	
	public List<Question> findQuestionsEligibleForNumberedYaadi(final DeviceType deviceType, final Session session, final Integer numberOfQuestionsSetInYaadi, final String locale) throws ELSException {
		List<Question> questions = null;
		if(session!=null) {
			String deviceTypeString = null;
			if(deviceType==null) {
				deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
			} else {
				deviceTypeString = deviceType.getType();
			}
			List<Long> removedDevicesNotEligibleForNumberedYaadi = YaadiDetails.findRemovedDevicesNotEligibleForNumberedYaadi(deviceType, session, locale);
			String queryString = "SELECT q FROM Question q"
					+ " LEFT JOIN q.primaryMember.houseMemberRoleAssociations mhr"
					+ " WHERE q.type.type=:deviceTypeString"
					+ " AND q.session.house.id=:houseId"
					+ " AND q.parent IS NULL"
					+ " AND q.yaadiNumber IS NULL"
					+ " AND q.yaadiLayingDate IS NULL"
					+ " AND q.status.type=:admissionStatusType"
					+ " AND q.answer IS NOT NULL AND q.answer <> '' AND q.answer <> '<p></p>' AND q.answer <> '<br><p></p>' "
					+ " AND q.locale=:locale"
					+ " AND mhr.fromDate<=:curDate"
					+ " AND mhr.toDate>=:curDate";
			if(removedDevicesNotEligibleForNumberedYaadi!=null && !removedDevicesNotEligibleForNumberedYaadi.isEmpty()) {
				queryString += " AND q.id NOT IN :removedDevicesNotEligibleForNumberedYaadi";
			}
			queryString += " ORDER BY q.number";
			String numberOfQuestionsInYaadiParameter = session.getParameter(deviceTypeString + "_" + "numberOfQuestionsInYaadi");
			if(numberOfQuestionsInYaadiParameter!=null) {
				Integer numberOfQuestionsInYaadi = Integer.parseInt(numberOfQuestionsInYaadiParameter);
				TypedQuery<Question> query = this.em().createQuery(queryString, Question.class);
				query.setParameter("houseId", session.getHouse().getId());
				query.setParameter("deviceTypeString", deviceTypeString);	
				String admissionStatus = "";
				if(deviceTypeString.equals(ApplicationConstants.STARRED_QUESTION)) {
					admissionStatus = ApplicationConstants.QUESTION_FINAL_ADMISSION;
				} else if(deviceTypeString.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					admissionStatus = ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION;
				} else if(deviceTypeString.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					admissionStatus = ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION;
				} else if(deviceTypeString.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					admissionStatus = ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION;
				}
				query.setParameter("admissionStatusType", admissionStatus);
				query.setParameter("curDate", new Date());
				query.setParameter("locale", locale);
				if(removedDevicesNotEligibleForNumberedYaadi!=null && !removedDevicesNotEligibleForNumberedYaadi.isEmpty()) {
					query.setParameter("removedDevicesNotEligibleForNumberedYaadi", removedDevicesNotEligibleForNumberedYaadi);
				}				
				questions = query.setMaxResults(numberOfQuestionsInYaadi-numberOfQuestionsSetInYaadi)
									.getResultList();
				if(questions==null) {
					questions = new ArrayList<Question>();
				}
			} else {
				logger.error("**** Session parameter 'numberOfQuestionsInYaadi' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "question.numberOfQuestionsInYaadiParameterNotSet");
			}			
		}
		return questions;
	}
	
	public boolean isYaadiOfGivenNumberExistingInSession(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		boolean isYaadiOfGivenNumberExistingInSession = false;
		if(this.findYaadiLayingDateForYaadi(deviceType, session, yaadiNumber, locale)!=null) {
			isYaadiOfGivenNumberExistingInSession = true;
		}
		return isYaadiOfGivenNumberExistingInSession;
	}
	
	public boolean isNumberedYaadiFilled(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final String locale) throws ELSException {
		boolean isNumberedYaadiFilled = false;		
		if(session!=null) {
			Long questionsFilledInYaadiCount = null;
			String deviceTypeString = null;
			if(deviceType==null) {
				deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
			} else {
				deviceTypeString = deviceType.getType();
			}
			String queryString = "SELECT COUNT(q.id) FROM Question q"
					+ " WHERE q.type.type=:deviceTypeString"
					+ " AND q.session.house.id=:houseId"
					+ " AND q.yaadiNumber=:yaadiNumber";
			String yaadiNumberingParameter = session.getParameter(deviceTypeString + "_" + "yaadiNumberingParameter");
			if(yaadiNumberingParameter!=null) {
				if(yaadiNumberingParameter.equals("session")) {
					queryString += " AND q.yaadiLayingDate>=:sessionStartDate";
					queryString += " AND q.yaadiLayingDate<=:sessionEndDate";
				}				
			} else {
				logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
				throw new ELSException("error", "question.yaadiNumberingParameterNotSet");
			}
			queryString += " AND q.locale=:locale";
			queryString += " ORDER BY q.number";
			try {
				TypedQuery<Long> query = this.em().createQuery(queryString, Long.class);
				query.setParameter("houseId", session.getHouse().getId());
				query.setParameter("deviceTypeString", deviceTypeString);				
				if(yaadiNumberingParameter.equals("session")) {
					query.setParameter("sessionStartDate", session.getStartDate());
					query.setParameter("sessionEndDate", session.getEndDate());
				}
				query.setParameter("yaadiNumber", yaadiNumber);
				query.setParameter("locale", locale);
				questionsFilledInYaadiCount = query.getSingleResult();
			} catch(Exception e) {
				throw new ELSException();
			}
			if(questionsFilledInYaadiCount!=null && questionsFilledInYaadiCount>0) {
				String numberOfQuestionsInYaadiParameter = session.getParameter(deviceTypeString + "_" + "numberOfQuestionsInYaadi");
				if(numberOfQuestionsInYaadiParameter!=null) {
					Integer numberOfQuestionsInYaadi = Integer.parseInt(numberOfQuestionsInYaadiParameter);
					if(questionsFilledInYaadiCount.intValue()==numberOfQuestionsInYaadi.intValue()) {
						isNumberedYaadiFilled = true;
					}
				} else {
					logger.error("**** Session parameter 'numberOfQuestionsInYaadi' is not set for session with ID = " + session.getId() +". ****");
					throw new ELSException("error", "question.numberOfQuestionsInYaadiParameterNotSet");
				}				
			}
		}		
		return isNumberedYaadiFilled;
	}
	
	public List<Question> findBySessionNumber(final Session session, final Integer number, final String locale){
		StringBuffer query = new StringBuffer("SELECT q FROM Question q WHERE q.session=:session AND q.number=:number AND q.locale=:locale");
		
		TypedQuery<Question> tQuery = this.em().createQuery(query.toString(), Question.class);
		tQuery.setParameter("session", session);
		tQuery.setParameter("number", number);
		tQuery.setParameter("locale", locale);
		return tQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public Question find(final Session session,
			final DeviceType deviceType,
			final Integer qNumber, 
			final String locale) {
	
		String strQuery="SELECT q FROM Question q " +
				"WHERE q.session.id=:sessionId " +
				"AND q.number=:number " +
				"AND q.type.id=:deviceTypeId " +
				"AND q.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", session.getId());
		query.setParameter("number", qNumber);
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		
		List<Question> questions = query.getResultList();		
		if(questions != null && !questions.isEmpty()){
			return questions.get(0);
		}
		
		return null;  
	}
	
	public List<Question> find(final Group group, 
			final DeviceType deviceType, 
			final Status GTEQinternalStatus,
			final Status LTrecommendationStatus,
			final String locale) {
		String strQuery = "SELECT q" +
				" FROM Question q" +
				" WHERE q.group.id=:groupId" +
				" AND q.type.id=:deviceTypeId" +
				" AND q.internalStatus.priority>=:internalStatusPriority" +
				" AND q.recommendationStatus.priority<:recommendationStatusPriority" +
				" AND q.locale=:locale";
		
		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		query.setParameter("groupId", group.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("internalStatusPriority", GTEQinternalStatus.getPriority());
		query.setParameter("recommendationStatusPriority", LTrecommendationStatus.getPriority());
		query.setParameter("locale", locale);
		
		List<Question> questions = query.getResultList();
		return questions;
	}
	
	public List<Question> find(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final Status GTEQinternalStatus,
			final Status LTrecommendationStatus,
			final String locale) {
		String strQuery = "SELECT q" +
				" FROM Question q" +
				" WHERE q.primaryMember.id=:primaryMemberId" +
				" AND q.session.id=:sessionId" +
				" AND q.type.id=:deviceTypeId" +
				" AND q.internalStatus.priority>=:internalStatusPriority" +
				" AND q.recommendationStatus.priority<:recommendationStatusPriority" +
				" AND q.locale=:locale";
		
		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		query.setParameter("primaryMemberId", member.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("internalStatusPriority", GTEQinternalStatus.getPriority());
		query.setParameter("recommendationStatusPriority", LTrecommendationStatus.getPriority());
		query.setParameter("locale", locale);
		
		List<Question> questions = query.getResultList();
		return questions;
	}

	public List<Question> findWhereMemberIsSupportingMember(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final Status GTEQinternalStatus,
			final Status LTrecommendationStatus, 
			final String locale) {
		String strQuery = "SELECT q" +
				" FROM Question q JOIN q.supportingMembers sm JOIN sm.member m" +
				" WHERE m.id=:memberId" +
				" AND q.session.id=:sessionId" +
				" AND q.type.id=:deviceTypeId" +
				" AND q.internalStatus.priority>=:internalStatusPriority" +
				" AND q.recommendationStatus.priority<:recommendationStatusPriority" +
				" AND q.locale=:locale";
		
		TypedQuery<Question> query = this.em().createQuery(strQuery, Question.class);
		query.setParameter("memberId", member.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("internalStatusPriority", GTEQinternalStatus.getPriority());
		query.setParameter("recommendationStatusPriority", LTrecommendationStatus.getPriority());
		query.setParameter("locale", locale);
		
		List<Question> questions = query.getResultList();
		return questions;
	}
	
	public QuestionDates findNextAnsweringDate(final Question question) {
		QuestionDates nextQuestionDates = null;
		String strQuery = "SELECT qd" +
				" FROM Question q JOIN q.group g JOIN g.questionDates qd" +
				" WHERE q.id=:qid" +
				" AND qd.answeringDate>:currentAnsweringDate" +
				" ORDER BY qd.answeringDate ASC";
		TypedQuery<QuestionDates> query = this.em().createQuery(strQuery, QuestionDates.class);
		query.setParameter("qid", question.getId());
		QuestionDates currentQuestionDates = Question.findQuestionDatesForStarredQuestion(question);
		query.setParameter("currentAnsweringDate", currentQuestionDates.getAnsweringDate());
		List<QuestionDates> nextQuestionDatesList = query.getResultList();
		if(!nextQuestionDatesList.isEmpty()) {
			nextQuestionDates = nextQuestionDatesList.get(0);
		}
		return nextQuestionDates;
	}

	public QuestionDraft findLatestGroupChangedDraft(final Question question) {
		String strQuery = "SELECT qd FROM Question q JOIN q.drafts qd" +
				" WHERE qd.ministry != q.ministry " +
				" AND qd.subDepartment != q.subDepartment" +
				" AND q.id=:questionId" +
				" ORDER BY qd.id desc";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("questionId", question.getId());
		List<QuestionDraft> questionDrafts = query.getResultList();
		if(questionDrafts != null && !questionDrafts.isEmpty()){
			return questionDrafts.get(0);
		}else{
			return null;
		}
	}

	public QuestionDraft findGroupChangedDraft(Question question) {
		String strQuery = "SELECT qd FROM Question q JOIN q.drafts qd" +
				" WHERE qd.ministry = q.ministry " +
				" AND qd.subDepartment = q.subDepartment" +
				" AND q.id=:questionId" +
				" ORDER BY qd.id";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("questionId", question.getId());
		List<QuestionDraft> questionDrafts = query.getResultList();
		if(questionDrafts != null && !questionDrafts.isEmpty()){
			return questionDrafts.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Member> findMembersHavingQuestionSubmittedInFirstBatch(final Session session, final DeviceType deviceType, final String locale) throws ELSException {
		try {
			/**** Here we are assuming that submission time,start time,end time are all stored in same format in db ****/
			String startTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_STARTTIME);
			String endTime=session.getParameter(ApplicationConstants.QUESTION_STARRED_FIRSTBATCH_SUBMISSION_ENDTIME);
			
			List<Member> members=new ArrayList<Member>();
			
			if(startTime!=null && !startTime.isEmpty() && endTime!=null && !endTime.isEmpty()) {
				
				org.mkcl.els.domain.Query query = 
						org.mkcl.els.domain.Query.findByFieldName(Query.class, "keyField", "ACTIVE_MEMBERS_HAVING_QUESTIONS_SUBMITTED_IN_FIRST_BATCH_FOR_GIVEN_SESSION", locale);
				
				Query persistenceQuery = this.em().createNativeQuery(query.getQuery(), Member.class);
				persistenceQuery.setParameter("sessionId", session.getId());
				persistenceQuery.setParameter("houseId", session.getHouse().getId());
				/** parameter for date on which to check whether member is active or not **/
				Date activeOnCheckDate = null;
				if(new Date().compareTo(session.getStartDate())<=0) {
					activeOnCheckDate = session.getStartDate();
				} else if(new Date().compareTo(session.getStartDate())>0
						&& new Date().compareTo(session.getEndDate())<0) {
					activeOnCheckDate = new Date();
				} else {
					activeOnCheckDate = session.getEndDate();
				}	
				persistenceQuery.setParameter("activeOnCheckDate", FormaterUtil.formatDateToString(activeOnCheckDate, ApplicationConstants.DB_DATEFORMAT));
				persistenceQuery.setParameter("deviceTypeId", deviceType.getId());
				/** parameter for status priority of questions **/
				Status submitStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale);
				persistenceQuery.setParameter("submitStatusPriority", submitStatus.getPriority());
				persistenceQuery.setParameter("firstBatchStartTime", startTime);
				persistenceQuery.setParameter("firstBatchEndTime", endTime);
				
				members = persistenceQuery.getResultList();				
				
			}
			
			return members;
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("QuestionRepository_List<Member>findMembersHavingQuestionSubmittedInFirstBatch", "No member found.");
			throw elsException;
		}		
	}

	public List<Object[]> findUnstarredAcrossSessionDepartmentwiseQuestions(final String sessionIds, final String locale) {
		List<Object[]> unstarredAcrossSessionDepartmentwiseQuestions = new ArrayList<Object[]>();
		if(sessionIds!=null && !sessionIds.isEmpty()) {
			List<String> sessionIdList = new ArrayList<String>();	
			for(String val : sessionIds.split(",")){
				sessionIdList.add(val);
			}
			org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "UNSTARRED_QUESTIONS_ACROSS_SESSION_DEPARTMENTWISE", locale);
			if(query!=null) {
				Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
				persistenceQuery.setParameter("sessionIds", sessionIdList);
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = persistenceQuery.getResultList();
				if(resultList!=null && !resultList.isEmpty()) {
					Object[] departmentwiseQuestionDetails = new Object[2];
					List<Object[]> questionsInDepartment = new ArrayList<Object[]>();
					String departmentName = resultList.get(0)[2].toString();
					long serialCount = 1;
					for(Object[] result: resultList) {
						if(result[2].toString().equals(departmentName)) {
							result[0] = FormaterUtil.formatNumberNoGrouping(serialCount, locale);
							questionsInDepartment.add(result);
							serialCount++;
						} else {
							departmentwiseQuestionDetails[0] = departmentName;
							departmentwiseQuestionDetails[1] = questionsInDepartment;
							unstarredAcrossSessionDepartmentwiseQuestions.add(departmentwiseQuestionDetails);
							departmentwiseQuestionDetails = new Object[2];
							departmentName = result[2].toString();
							questionsInDepartment = new ArrayList<Object[]>();
							serialCount = 1;
							result[0] = FormaterUtil.formatNumberNoGrouping(serialCount, locale);
							questionsInDepartment.add(result);		
							serialCount++;
						}
					}
				}
			}
		}
		return unstarredAcrossSessionDepartmentwiseQuestions;
	}
	
	public int updateUnBallot(final Member member, final Session session,
			final DeviceType deviceType, final Status internalStatus,
			final Date discussionDate) {
		StringBuffer strQuery = new StringBuffer("UPDATE questions SET ballotstatus_id=NULL,discussion_date=NULL WHERE " + 
								" member_id=:memberId " +
								" AND devicetype_id=:deviceTypeId " +
								" AND session_id=:sessionId " + 
								" AND internalstatus_id=:statusId " +
								" AND discussion_date=:discussionDate");
		Query query = this.em().createNativeQuery(strQuery.toString(), Question.class);
		query.setParameter("memberId", member.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("statusId", internalStatus.getId());
		query.setParameter("discussionDate", discussionDate);		
		return query.executeUpdate();
	}

	public List<Date> findAvailableYaadiLayingDatesForSession(final DeviceType deviceType, final Session session, final String locale) throws ELSException {
		List<Date> availableYaadiLayingDates = new ArrayList<Date>();
		if(session==null || session.getId()==null) {
			logger.error("**** Session is null ****");
			throw new ELSException("error", "session_null");
		}
		String deviceTypeString = null;
		if(deviceType==null) {
			deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
		} else {
			deviceTypeString = deviceType.getType();
		}
		String yaadiNumberingParameter = session.getParameter(deviceTypeString + "_" + "yaadiNumberingParameter");
		if(yaadiNumberingParameter!=null) {				
			if(yaadiNumberingParameter.equals("session")) {
				Calendar start = Calendar.getInstance();
				start.setTime(session.getStartDate());
				Calendar end = Calendar.getInstance();
				end.setTime(session.getEndDate());
				for (Calendar current=start; !current.after(end); current.add(Calendar.DATE, 1)) {
					availableYaadiLayingDates.add(current.getTime());
				}
			} else if(yaadiNumberingParameter.equals("house")) {
				List<Session> sessionsInHouse = Session.findAllByFieldName(Session.class, "house", session.getHouse(), "startDate", ApplicationConstants.DESC, locale);
				if(sessionsInHouse!=null && !sessionsInHouse.isEmpty()) {
					Calendar start = null;
					Calendar end = null;
					Calendar current = null;
					for(Session s: sessionsInHouse) {
						start = Calendar.getInstance();
						start.setTime(s.getStartDate());
						end = Calendar.getInstance();
						end.setTime(s.getEndDate());
						for (current=start; !current.after(end); current.add(Calendar.DATE, 1)) {
							availableYaadiLayingDates.add(current.getTime());
						}
					}
					sessionsInHouse = null;
				}
			}
		} else {
			logger.error("**** Session parameter 'yaadiNumberingParameter' is not set for session with ID = " + session.getId() +". ****");
			throw new ELSException("error", "question.yaadiNumberingParameterNotSet");
		}
		return availableYaadiLayingDates;
	}

	public Boolean isNumberedYaadiFinalized(final DeviceType deviceType, final Session session, final Integer yaadiNumber, final Date yaadiLayingDate, final String locale) {
		Boolean isNumberedYaadiFinalized = false;
		String deviceTypeString = null;
		if(deviceType==null) {
			deviceTypeString = ApplicationConstants.UNSTARRED_QUESTION;
		} else {
			deviceTypeString = deviceType.getType();
		}
		String queryString = "SELECT COUNT(q) FROM Question q"
				+ " WHERE q.type.type=:deviceTypeString"				
				+ " AND q.yaadiNumber=:yaadiNumber"
				+ " AND q.yaadiLayingDate=:yaadiLayingDate"
				+ " AND q.recommendationStatus IS NOT NULL"
				+ " AND q.recommendationStatus.type LIKE :yaadiLaidStatusType"
				+ " AND q.locale=:locale";
		Query query = this.em().createQuery(queryString);
		query.setParameter("deviceTypeString", deviceTypeString);
		query.setParameter("yaadiNumber", yaadiNumber);
		query.setParameter("yaadiLayingDate", yaadiLayingDate);
		query.setParameter("yaadiLaidStatusType", "%processed_yaadilaid");
		query.setParameter("locale", locale);
		try {
			Long finalizedQuestionsCount = (Long) query.getSingleResult();
			if(finalizedQuestionsCount!=null && finalizedQuestionsCount.longValue()>0) {
				isNumberedYaadiFinalized = true;
			}
		} catch (NoResultException nre) {
			isNumberedYaadiFinalized = false;
		}		
		return isNumberedYaadiFinalized;
	}
	
	public List<Question> findAll(final Session session, final DeviceType deviceType, final Integer number, final String locale) throws ELSException {
		
		try{
			StringBuilder strQuery = new StringBuilder("SELECT q FROM Questions q" +
					" WHERE q.session.id=:sessionId" +
					" AND q.type.id=:deviceTypeId" +
					" AND q.number=:number");
			TypedQuery<Question> query = em().createQuery(strQuery.toString(), Question.class);
			return query.getResultList();
		}catch(Exception e){
			throw new ELSException(e.getMessage(), e.toString());
		}
	}
	
	public boolean getState(Question q){
		return this.em().contains(q);
	}
	
	public String restoreQuestionTextBeforeClubbing(final Question question) {
		String restoredQuestiontext = question.getRevisedQuestionText(); //initiate with latest revised text
		String strQuery = "SELECT qd.question_text FROM questions q " 
						+ "INNER JOIN questions_drafts_association qda ON (qda.question_id=q.id) "
						+ "INNER JOIN question_drafts qd ON (qd.id=qda.question_draft_id) "
						+ "WHERE q.id=:questionId "
						+ "AND (qd.question_text IS NOT NULL AND qd.question_text<>'') "
						+ "AND qd.parent IS NULL "
						+ "ORDER BY qd.edited_on DESC LIMIT 1";
		
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("questionId", question.getId());
		//query.setParameter("parentId", question.getParent().getId());
		try {
			restoredQuestiontext = (String) query.getSingleResult();
		} catch(NoResultException e) {
			List<QuestionDraft> drafts = QuestionDraft.findAllByFieldName(QuestionDraft.class, "questionId", question.getId(), "id", ApplicationConstants.ASC, question.getLocale());
			if(drafts!=null && !drafts.isEmpty()) {
				question.setDrafts(new LinkedHashSet<QuestionDraft>(drafts));
				question.simpleMerge();
//				for(int i=drafts.size()-1; i>=0; i--) {
//					if(drafts.get(i).getParent()==null || !drafts.get(i).getParent().getId().equals(question.getParent().getId())) {
//						restoredQuestiontext = drafts.get(i).getQuestionText();
//						break;
//					}
//				}
//				restoredQuestiontext = (String) query.getSingleResult();
			}
			return restoredQuestiontext; //later try restoring drafts and query again
		}
		return restoredQuestiontext;
	}
	
	public boolean isQuestionAllowedInFirstBatchForMaxCountPerMember(final Question question) {
		boolean allowedInFirstBatchForMaxCountPerMember = true;
		
		String strSubmissionFirstBatchStartDate =  question.getSession().getParameter(question.getType().getType() + "_" + "submissionFirstBatchStartDate");
		String strSubmissionFirstBatchEndDate =  question.getSession().getParameter(question.getType().getType() + "_" + "submissionFirstBatchEndDate");
		String strSubmissionFirstBatchMaxCountPerMember = question.getSession().getParameter(question.getType().getType() + "_" + "submissionFirstBatchMaxCountPerMember");
		
		if(strSubmissionFirstBatchStartDate!=null && !strSubmissionFirstBatchStartDate.isEmpty() 
				&& strSubmissionFirstBatchEndDate!=null && !strSubmissionFirstBatchEndDate.isEmpty()) {
			
			Date submissionFirstBatchStartDate = FormaterUtil.formatStringToDate(strSubmissionFirstBatchStartDate, ApplicationConstants.DB_DATETIME_FORMAT);
			Date submissionFirstBatchEndDate = FormaterUtil.formatStringToDate(strSubmissionFirstBatchEndDate, ApplicationConstants.DB_DATETIME_FORMAT);
			
			if(strSubmissionFirstBatchMaxCountPerMember==null || strSubmissionFirstBatchMaxCountPerMember.isEmpty()) {
				strSubmissionFirstBatchMaxCountPerMember = "31";
			}
			Long submissionFirstBatchMaxCountPerMember = Long.parseLong(strSubmissionFirstBatchMaxCountPerMember);
			
			Status submitStatus = Status.findByType(ApplicationConstants.QUESTION_SUBMIT, question.getLocale());
			
			String queryString = "SELECT COUNT(q) FROM Question q "
					+ "WHERE q.originalType.id=:deviceTypeId AND q.session.id=:sessionId AND q.primaryMember.id=:memberId AND q.status.priority>=:submitPriority "
					+ "AND q.submissionDate>=:submissionFirstBatchStartDate AND q.submissionDate<=:submissionFirstBatchEndDate";
			
			Query query = this.em().createQuery(queryString);
			query.setParameter("deviceTypeId", question.getOriginalType().getId());
			query.setParameter("sessionId", question.getSession().getId());
			query.setParameter("memberId", question.getPrimaryMember().getId());
			query.setParameter("submitPriority", submitStatus.getPriority());
			query.setParameter("submissionFirstBatchStartDate", submissionFirstBatchStartDate);
			query.setParameter("submissionFirstBatchEndDate", submissionFirstBatchEndDate);
			
			Long submissionCountOfMemberInFirstBatch = (Long) query.getSingleResult();
			
			if(submissionCountOfMemberInFirstBatch!=null && submissionCountOfMemberInFirstBatch.equals(submissionFirstBatchMaxCountPerMember)) {
				allowedInFirstBatchForMaxCountPerMember = false;
			}
		}				
		
    	return allowedInFirstBatchForMaxCountPerMember;
	}
	
	public int updateTimeoutSupportingMemberTasksForDevice(final Long deviceId, final Date submissionDate) {
		if(deviceId!=null && submissionDate!=null) {
			StringBuffer strQuery = new StringBuffer("UPDATE workflow_details wd " + 
					" SET wd.completion_time=:completionTime, wd.status='TIMEOUT' " +
					" WHERE wd.device_id=:deviceId " + 
					" AND wd.workflow_sub_type = 'request_to_supporting_member' " +
					" AND wd.status='PENDING'");
			Query query = this.em().createNativeQuery(strQuery.toString());
			query.setParameter("deviceId", deviceId);	
			String submissionDateStr = FormaterUtil.formatDateToString(submissionDate, ApplicationConstants.DB_DATETIME__24HOURS_FORMAT);
			Date completionTime = FormaterUtil.formatStringToDate(submissionDateStr, ApplicationConstants.DB_DATETIME__24HOURS_FORMAT);
			query.setParameter("completionTime", completionTime);
			return query.executeUpdate();
		} else {
			return 0;
		}		
	}
	
	public List<Long> findQuestionIDsHavingPendingAnswersPostLastDateOfAnswerReceiving(final HouseType houseType, final DeviceType deviceType, final SubDepartment subDepartment, final String locale) throws ELSException {
		List<Long> questionIds = new ArrayList<Long>();
		org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUERYNAME_QIS_PENDING_FOR_ANSWER_POST_LAST_ANSWERING_DATE, "");
		String strquery = nativeQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("houseTypeId",houseType.getId());
		query.setParameter("deviceTypeId",deviceType.getId());
		Status admittedStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status admittedStatusForDeviceType = null;
		try {
			admittedStatusForDeviceType = Question.findCorrespondingStatusForGivenQuestionType(admittedStatus, deviceType);
		} catch (ELSException e) {
			e.printStackTrace();
			e.setParameter("QuestionRepository_Question_findQuestionIDsHavingPendingAnswersPostLastDateOfAnswerReceiving", "Cannot get the Admitted Status ");
			throw e;
		}
		query.setParameter("admittedStatusIdForDeviceType",admittedStatusForDeviceType.getId());
		query.setParameter("subDepartmentId",subDepartment.getId());
		List result =query.getResultList();
		for(Object i : result) {
			Long questionId = Long.parseLong(i.toString());
			questionIds.add(questionId);
		}
		return questionIds;
	}
}
