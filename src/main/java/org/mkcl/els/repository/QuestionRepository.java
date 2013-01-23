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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionRevisionVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberBallotAttendance;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
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
public class QuestionRepository extends BaseRepository<Question, Long>{


	/**
	 * Find last starred unstarred short notice question no.
	 *
	 * @param house the house
	 * @param currentSession the current session
	 * @return the integer
	 */
	public Integer findLastStarredUnstarredShortNoticeQuestionNo(final House house,final Session currentSession){
		String query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
		"JOIN devicetypes AS dt WHERE q.session_id=s.id AND s.house_id=h.id "+
		"AND h.id="+house.getId()+" AND s.id="+currentSession.getId()+" AND dt.id=q.devicetype_id AND dt.type!='halfhourdiscussion' ORDER BY q.id DESC LIMIT 0,1";
		List result=this.em().createNativeQuery(query).getResultList();
		Integer lastNumber=0;
		if(!result.isEmpty()){
			Object i=result.get(0);
			lastNumber=Integer.parseInt(i.toString());
		}
		return lastNumber;
	}

	/**
	 * Find last half hour discussion question no.
	 *
	 * @param house the house
	 * @param currentSession the current session
	 * @return the integer
	 */
	public Integer findLastHalfHourDiscussionQuestionNo(final House house,final Session currentSession){
		String query="SELECT q.number FROM questions AS q JOIN sessions AS s JOIN houses AS h "+
		"JOIN devicetypes AS dt WHERE q.session_id=s.id AND s.house_id=h.id "+
		"AND h.id="+house.getId()+" AND s.id="+currentSession.getId()+" AND dt.id=q.devicetype_id AND dt.type=='halfhourdiscussion' ORDER BY q.id DESC LIMIT 0,1";
		List result=this.em().createNativeQuery(query).getResultList();
		Integer lastNumber=0;
		if(!result.isEmpty()){
			Object i=result.get(0);
			lastNumber=Integer.parseInt(i.toString());
		}
		return lastNumber;
	}

	/**
	 * Assign question no.
	 *
	 * @param houseType the house type
	 * @param session the session
	 * @param questionType the question type
	 * @param locale the locale
	 * @return the integer
	 */
	public Integer assignQuestionNo(final HouseType houseType, final Session session,
			final DeviceType questionType,final String locale) {
		String strHouseType=houseType.getType();
		String strQuestionType=questionType.getType();
		Long house=session.getHouse().getId();
		String query=null;
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
			if(strQuestionType.equals("questions_starred")||strQuestionType.equals("questions_unstarred")||strQuestionType.equals("questions_shortnotice")){
				query="SELECT q FROM Question q JOIN q.session s JOIN s.house h JOIN q.type dt WHERE "+
				" h.id="+house+"  AND (dt.type='questions_shortnotice' OR dt.type='questions_starred' OR dt.type='questions_unstarred') ORDER BY q.number "+ApplicationConstants.DESC;
			}else if(strQuestionType.equals("questions_halfhourdiscussion_from_question")){
				query="SELECT q FROM Question q JOIN q.session s JOIN s.house h JOIN q.type dt WHERE "+
				" h.id="+house+"  AND (dt.type='questions_halfhourdiscussion_from_question') ORDER BY q.number "+ApplicationConstants.DESC;
			}
		}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
			Session lowerHouseSession=Session.find(session.getYear(),session.getType().getType(),ApplicationConstants.LOWER_HOUSE);
			House lowerHouse=lowerHouseSession.getHouse();
			CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
			SimpleDateFormat simpleDateFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(),"en_US");
			String lowerHouseFormationDate=simpleDateFormat.format(lowerHouse.getFormationDate());
			if(strQuestionType.equals("questions_starred")||strQuestionType.equals("questions_unstarred")||strQuestionType.equals("questions_shortnotice")){
				query="SELECT q FROM Question q JOIN q.type dt JOIN q.houseType ht WHERE "+
				" ht.type='"+ApplicationConstants.UPPER_HOUSE+"' AND q.submissionDate>='"+lowerHouseFormationDate+"' "+
				" AND (dt.type='questions_shortnotice' OR dt.type='questions_starred' OR dt.type='questions_unstarred') ORDER BY q.number "+ApplicationConstants.DESC;
			}else if(strQuestionType.equals("questions_halfhourdiscussion_from_question")){
				query="SELECT q FROM Question q JOIN q.type dt JOIN q.houseType ht WHERE "+
				" ht.type='"+ApplicationConstants.UPPER_HOUSE+"' AND q.submissionDate>='"+lowerHouseFormationDate+"' "+
				" AND (dt.type='questions_halfhourdiscussion_from_question') ORDER BY q.number "+ApplicationConstants.DESC;
			}
		}
		try{
			List<Question> questions=this.em().createQuery(query).setFirstResult(0).setMaxResults(1).getResultList();
			if(questions==null){
				return 0;
			}else if(questions.isEmpty()){
				return 0;
			}else{
				if(questions.get(0).getNumber()==null){
					return 0;
				}else{
					return questions.get(0).getNumber();
				}
			}
		}catch(Exception e){
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
	public List<QuestionRevisionVO> getRevisions(final Long questionId,final String locale) {
		String query="SELECT rs.usergroup,rs.fullname,rs.editedon,rs.status,rs.question,rs.subject,rs.remark FROM ("+
		"SELECT qd.edited_as as usergroup,concat(u.title,' ',u.first_name,' ',u.middle_name,' ',u.last_name) as fullname,qd.edited_on as editedon,"+
		"s.name as status,qd.question_text as question,qd.subject as subject,qd.remarks as remark FROM questions as q JOIN questions_drafts_association as qda "+
		" JOIN question_drafts as qd JOIN users as u JOIN credentials as c JOIN "+
		" status as s WHERE q.id=qda.question_id AND qda.question_draft_id=qd.id  "+
		"  AND qd.recommendationstatus_id=s.id and "+
		" u.credential_id=c.id and c.username=qd.edited_by  "+
		" AND q.id="+questionId +" ORDER BY qd.edited_on desc ) as rs";
		List results=this.em().createNativeQuery(query).getResultList();
		List<QuestionRevisionVO> questionRevisionVOs=new ArrayList<QuestionRevisionVO>();
		for(Object i:results){
			Object[] o=(Object[]) i;
			QuestionRevisionVO questionRevisionVO=new QuestionRevisionVO();
			if(o[0]!=null){
				questionRevisionVO.setEditedAs(o[0].toString());
			}else{
				UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type","member", locale);
				questionRevisionVO.setEditedAs(userGroupType.getName());
			}
			questionRevisionVO.setEditedBY(o[1].toString());
			questionRevisionVO.setEditedOn(o[2].toString());
			questionRevisionVO.setStatus(o[3].toString());
			questionRevisionVO.setQuestion(o[4].toString());
			questionRevisionVO.setSubject(o[5].toString());
			if(o[6]!=null){
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
	 * @param answeringDate the answering date
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
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
	 * Full text search clubbing.
	 *
	 * @param param the param
	 * @param sessionId the session id
	 * @param groupId the group id
	 * @param currentChartId the current chart id
	 * @param questionId the question id
	 * @param locale the locale
	 * @return the list
	 */
	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchClubbing(final String param, final Question question,
			final Integer start,final Integer noofRecords,
			final String locale) {
		/**** Data to fetch and from where ****/
		String selectQuery="SELECT q.id as id,q.number as number, "+
		" q.subject as subject,q.revised_subject as revisedSubject, "+
		" q.question_text as questionText,q.revised_question_text as revisedQuestionText, "+
		" st.name as status,dt.name as deviceType,s.session_year as sessionYear,sety.session_type as sessionType "+
		" FROM questions as q "+
		" JOIN sessions as s "+
		" JOIN sessiontypes as sety"+
		" JOIN status as st "+
		" JOIN devicetypes as dt "+
		" JOIN members as m"+
		" JOIN titles as t"+
		" LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
		" LEFT JOIN departments as d ON(q.department_id=d.id) "+
		" LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
		" WHERE q.session_id=s.id AND s.sessiontype_id=sety.id AND q.recommendationstatus_id=st.id AND "+
		" q.devicetype_id=dt.id AND q.member_id=m.id AND"+
		" m.title_id=t.id AND q.id<>"+question.getId()+" ";

		/**** Candidate questions must be from same ministry,department and subdepartment ****/
		StringBuffer minDepSubDepQuery=new StringBuffer();
		Ministry ministry=question.getMinistry();
		if(question.getMinistry()!=null){
			minDepSubDepQuery.append(" AND mi.id="+ministry.getId());
		}else{
			minDepSubDepQuery.append(" AND mi.id IS NULL");
		}
		Department department=question.getDepartment();
		if(question.getDepartment()!=null){
			minDepSubDepQuery.append(" AND d.id="+department.getId());
		}else{
			minDepSubDepQuery.append(" AND d.id IS NULL");
		}
		SubDepartment subDepartment=question.getSubDepartment();
		if(question.getSubDepartment()!=null){
			minDepSubDepQuery.append(" AND sd.id="+subDepartment.getId());
		}else{
			minDepSubDepQuery.append(" AND sd.id IS NULL");
		}

		DeviceType deviceType=question.getType();
		StringBuffer deviceTypeQuery=new StringBuffer();
		String orderByQuery="";
		HouseType housetype=question.getHouseType();
		if(deviceType!=null){
			/**** Starred Questions :only primary questions,recommendation status >=assistant_processed,
			 * starred question from same session or unstarred question of same housetype but across any session
			 ****/
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
				deviceTypeQuery.append(" AND q.parent IS NULL ");	
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND (" +
						" (s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.STARRED_QUESTION +"')"+
						" OR (dt.type='"+ApplicationConstants.UNSTARRED_QUESTION+"' AND q.housetype_id="+housetype.getId()+")"+
				" )");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
				/**** Starred Questions :only primary questions,recommendation status >=assistant_processed,
				 * unstarred question of same housetype but across any session
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION+"'AND q.housetype_id="+housetype.getId()+" ");
				orderByQuery=" ORDER BY s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
				/**** Short Notice Questions :
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND s.id=" +question.getSession().getId()+" AND dt.type='"+ApplicationConstants.SHORT_NOTICE_QUESTION +"'");			
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				/**** Half hour discussion standalone Questions :
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				/**** Half hour discussion from questions Questions :
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND m.id = " + question.getPrimaryMember().getId());
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND s.id=" +question.getSession().getId()+" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION +"'");			
			}
		}		

		/**** fulltext query ****/
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in natural language mode)";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+minDepSubDepQuery.toString()+deviceTypeQuery.toString()+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
		" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				QuestionSearchVO questionSearchVO=new QuestionSearchVO();
				if(o[0]!=null){
					questionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					questionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						questionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						questionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}else{
					questionSearchVO.setSubject(higlightText(o[2].toString(),param));
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						questionSearchVO.setQuestionText(higlightText(o[5].toString(),param));
					}else{
						questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
					}
				}else{
					questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
				}
				if(o[6]!=null){
					questionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					questionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					questionSearchVO.setSessionYear(o[8].toString());
				}
				if(o[9]!=null){
					questionSearchVO.setSessionType(o[9].toString());
				}
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}


	private String higlightText(final String textToHiglight,final String pattern) {

		String highlightedText=textToHiglight;
		String replaceMentText="<span class='bold'>";
		String replaceMentTextEnd="</span>";
		if((!pattern.contains("+"))&&(!pattern.contains("-"))){
			String[] temp=pattern.trim().split(" ");
			for(String j:temp){
				if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
					highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
				}
			}			
		}else if((pattern.contains("+"))&&(!pattern.contains("-"))){
			String[] temp=pattern.trim().split("\\+");
			for(String j:temp){
				if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
					highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
				}
			}			
		}else if((!pattern.contains("+"))&&(pattern.contains("-"))){
			String[] temp=pattern.trim().split("\\-");
			String[] temp1=temp[0].trim().split(" ");
			for(String j:temp1){
				if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
					highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
				}
			}		
		}else if(pattern.contains("+")&& pattern.contains("-")){
			String[] temp=pattern.trim().split("\\-");
			String[] temp1=temp[0].trim().split("\\+");
			for(String j:temp1){
				String[] temp2=j.trim().split(" ");
				for(String k:temp2){
					if(!highlightedText.contains(replaceMentText+k.trim()+replaceMentTextEnd)){
						highlightedText=highlightedText.replaceAll(k.trim(),replaceMentText+k.trim()+replaceMentTextEnd);
					}
				}
			}		
		}
		return highlightedText;
	}

	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchReferencing( String param, final Question question,
			final Integer start,final Integer noofRecords,
			final String locale) {
		/**** Data to fetch and from where ****/
		String selectQuery="SELECT q.id as id,q.number as number, "+
		" q.subject as subject,q.revised_subject as revisedSubject, "+
		" q.question_text as questionText,q.revised_question_text as revisedQuestionText, "+
		" st.name as status,dt.name as deviceType,s.session_year as sessionYear,sety.session_type as sessionType "+
		" FROM questions as q "+
		" JOIN sessions as s "+
		" JOIN sessiontypes as sety"+
		" JOIN status as st "+
		" JOIN devicetypes as dt "+
		" JOIN members as m"+
		" JOIN titles as t"+
		" LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
		" LEFT JOIN departments as d ON(q.department_id=d.id) "+
		" LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
		" WHERE q.session_id=s.id AND s.sessiontype_id=sety.id AND q.recommendationstatus_id=st.id AND "+
		" q.devicetype_id=dt.id AND q.member_id=m.id AND"+
		" m.title_id=t.id AND q.id<>"+question.getId()+" ";

		/**** Candidate questions must be from same ministry,department and subdepartment ****/
		StringBuffer minDepSubDepQuery=new StringBuffer();
		Ministry ministry=question.getMinistry();
		if(question.getMinistry()!=null){
			minDepSubDepQuery.append(" AND mi.id="+ministry.getId());
		}else{
			minDepSubDepQuery.append(" AND mi.id IS NULL");
		}
		Department department=question.getDepartment();
		if(question.getDepartment()!=null){
			minDepSubDepQuery.append(" AND d.id="+department.getId());
		}else{
			minDepSubDepQuery.append(" AND d.id IS NULL");
		}
		SubDepartment subDepartment=question.getSubDepartment();
		if(question.getSubDepartment()!=null){
			minDepSubDepQuery.append(" AND sd.id="+subDepartment.getId());
		}else{
			minDepSubDepQuery.append(" AND sd.id IS NULL");
		}

		DeviceType deviceType=question.getType();
		StringBuffer deviceTypeQuery=new StringBuffer();
		String orderByQuery="";
		HouseType housetype=question.getHouseType();
		if(deviceType!=null){
			/**** Starred Questions :only primary questions,recommendation status >=assistant_processed,
			 * starred question from same session or unstarred question of same housetype but across any session
			 ****/
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
				deviceTypeQuery.append(" AND q.parent IS NULL ");	
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND (" +
						" (s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.STARRED_QUESTION +"')"+
						" OR (dt.type='"+ApplicationConstants.UNSTARRED_QUESTION+"' AND q.housetype_id="+housetype.getId()+")"+
				" )");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
				/**** Starred Questions :only primary questions,recommendation status >=assistant_processed,
				 * unstarred question of same housetype but across any session
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION+"'AND q.housetype_id="+housetype.getId()+" ");
				orderByQuery=" ORDER BY s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
				/**** Short Notice Questions :
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				/**** Half hour discussion standalone Questions :
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				/**** Half hour discussion from questions Questions :
				 ****/
				deviceTypeQuery.append(" AND q.parent IS NULL");
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_ASSISTANT_PROCESSED+"')");
			}
		}		

		/**** fulltext query ****/
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in natural language mode)";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+minDepSubDepQuery.toString()+deviceTypeQuery.toString()+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
		" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				QuestionSearchVO questionSearchVO=new QuestionSearchVO();
				if(o[0]!=null){
					questionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					questionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						questionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						questionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}else{
					questionSearchVO.setSubject(higlightText(o[2].toString(),param));
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						questionSearchVO.setQuestionText(higlightText(o[5].toString(),param));
					}else{
						questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
					}
				}else{
					questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
				}
				if(o[6]!=null){
					questionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					questionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					questionSearchVO.setSessionYear(o[8].toString());
				}
				if(o[9]!=null){
					questionSearchVO.setSessionType(o[9].toString());
				}
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
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
		List<Question> questions=new ArrayList<Question>();
		String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
		" AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
		" AND q.internalStatus.id="+internalStatus.getId()+" ORDER BY q.number "+ApplicationConstants.ASC;
		questions=this.em().createQuery(query).getResultList();
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
	@SuppressWarnings("unchecked")
	public List<Question> findAllFirstBatch(final Member currentMember, final Session session,
			final DeviceType deviceType, final Status internalStatus) {
		List<Question> questions=new ArrayList<Question>();
		//        Date firstBatchDate=session.getQuestionSubmissionFirstBatchDate();
		//        Date firstBatchStartTime=session.getQuestionSubmissionFirstBatchStartTimeUH();
		//        Date firstBatchEndTime=session.getQuestionSubmissionFirstBatchEndTimeUH();
		//        if(firstBatchDate!=null&&firstBatchStartTime!=null&&firstBatchEndTime!=null){
		//            Calendar calendar1=new GregorianCalendar();
		//            calendar1.setTime(firstBatchDate);
		//
		//            Calendar startTime=new GregorianCalendar();
		//            startTime.setTime(firstBatchStartTime);
		//            startTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
		//            startTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
		//            startTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
		//
		//            Calendar endTime=new GregorianCalendar();
		//            endTime.setTime(firstBatchEndTime);
		//            endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
		//            endTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
		//            endTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
		//
		//            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
		//            if(customParameter!=null){
		//                SimpleDateFormat format= FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
		//                String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
		//                " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
		//                " AND q.internalStatus.id="+internalStatus.getId()+" AND q.submissionDate>='"+format.format(startTime.getTime())+"' AND q.submissionDate>='"+format.format(endTime.getTime())+"' ORDER BY q.number "+ApplicationConstants.ASC;
		//                questions=this.em().createQuery(query).getResultList();
		//            }
		//        }
		return questions;
	}

	/**
	 * Find all second batch.
	 *
	 * @param currentMember the current member
	 * @param session the session
	 * @param deviceType the device type
	 * @param internalStatus the internal status
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Question> findAllSecondBatch(final Member currentMember, final Session session,
			final DeviceType deviceType, final Status internalStatus) {
		List<Question> questions=new ArrayList<Question>();
		//        Date secondBatchDate=session.getQuestionSubmissionSecondBatchDateUH();
		//        Date secondBatchStartTime=session.getQuestionSubmissionSecondBatchStartTimeUH();
		//        Date secondBatchEndTime=session.getQuestionSubmissionSecondBatchEndTimeUH();
		//        if(secondBatchDate!=null&&secondBatchStartTime!=null&&secondBatchEndTime!=null){
		//            Calendar calendar1=new GregorianCalendar();
		//            calendar1.setTime(secondBatchDate);
		//
		//            Calendar startTime=new GregorianCalendar();
		//            startTime.setTime(secondBatchStartTime);
		//            startTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
		//            startTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
		//            startTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
		//
		//            Calendar endTime=new GregorianCalendar();
		//            endTime.setTime(secondBatchEndTime);
		//            endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
		//            endTime.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));
		//            endTime.set(Calendar.DATE,calendar1.get(Calendar.DATE));
		//
		//            CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
		//            if(customParameter!=null){
		//                SimpleDateFormat format= FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
		//                String query="SELECT q FROM Question q WHERE q.primaryMember.id="+currentMember.getId()+" "+
		//                " AND q.session.id="+session.getId()+" AND q.type.id="+deviceType.getId()+" "+
		//                " AND q.internalStatus.id="+internalStatus.getId()+" AND q.submissionDate>='"+format.format(startTime.getTime())+"' AND q.submissionDate>='"+format.format(endTime.getTime())+"' ORDER BY q.number "+ApplicationConstants.ASC;
		//                questions=this.em().createQuery(query).getResultList();
		//            }
		//        }
		return questions;
	}

	/**
	 * Club.
	 *
	 * @param questionBeingProcessed the question being processed
	 * @param questionBeingClubbed the question being clubbed
	 * @param locale the locale
	 * @return the boolean
	 */
	public Boolean club(final Long questionBeingProcessed,
			final Long questionBeingClubbed,final String locale) {
		Boolean status=true;		
		/**** Processed Question ****/
		Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
		/**** To be Clubbed Question ****/
		Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
		if(beingProcessedQuestion!=null&&beingClubbedQuestion!=null){
			if(!alreadyClubbed(beingProcessedQuestion,beingClubbedQuestion,locale)){
				/**** Processed Question's Clubbing ****/
				List<ClubbedEntity> beingProcessedClubbing=new ArrayList<ClubbedEntity>();
				if(beingProcessedQuestion.getClubbedEntities()!=null){
					if(!beingProcessedQuestion.getClubbedEntities().isEmpty()){
						for(ClubbedEntity i:beingProcessedQuestion.getClubbedEntities()){
							beingProcessedClubbing.add(i);
						}
					}
				}    
				/**** To be Clubbed Question's clubbing ****/
				List<ClubbedEntity> beingClubbedClubbing=new ArrayList<ClubbedEntity>();
				if(beingClubbedQuestion.getClubbedEntities()!=null){
					if(!beingClubbedQuestion.getClubbedEntities().isEmpty()){
						for(ClubbedEntity i:beingClubbedQuestion.getClubbedEntities()){
							beingClubbedClubbing.add(i);
						}
					}
				}  
				/**** Current status of processed and clubbed question ****/
				Status oldPQStatus=beingProcessedQuestion.getInternalStatus();
				Status oldCQStatus=beingClubbedQuestion.getInternalStatus();
				Status newPQStatus=beingProcessedQuestion.getInternalStatus();
				Status newCQStatus=beingClubbedQuestion.getInternalStatus();				
				/**** Device Type of processed and clubbed question ****/
				DeviceType beingProcessedQuestionType=beingProcessedQuestion.getType();
				DeviceType beingClubbedQuestionType=beingClubbedQuestion.getType();
				if(beingProcessedQuestionType.getType().equals(ApplicationConstants.STARRED_QUESTION)&&
						beingClubbedQuestionType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
					/**** Starred Clubbed With Starred ****/
					return starredClubbedWithStarred(beingProcessedQuestion,beingClubbedQuestion,beingProcessedClubbing
							,beingClubbedClubbing,oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
				}else if(beingProcessedQuestionType.getType().equals(ApplicationConstants.STARRED_QUESTION)&&
						beingClubbedQuestionType.getType().endsWith(ApplicationConstants.UNSTARRED_QUESTION)){
					/**** Starred Clubbed With Unstarred ****/
					return starredClubbedWithUnstarred(beingProcessedQuestion,beingClubbedQuestion,beingProcessedClubbing
							,beingClubbedClubbing,oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
				}else if(beingProcessedQuestionType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)&&
						beingClubbedQuestionType.getType().endsWith(ApplicationConstants.UNSTARRED_QUESTION)){
					/**** UnStarred Clubbed With Unstarred ****/
					return unstarredClubbedWithUnstarred(beingProcessedQuestion,beingClubbedQuestion,beingProcessedClubbing
							,beingClubbedClubbing,oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
				}else if(beingProcessedQuestionType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)&&
						beingClubbedQuestionType.getType().endsWith(ApplicationConstants.SHORT_NOTICE_QUESTION)){
					/**** Short Notice Clubbed With Short Notice ****/
					return shortNoticeClubbedWithShortNotice(beingProcessedQuestion,beingClubbedQuestion,beingProcessedClubbing
							,beingClubbedClubbing,oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
				}else if(beingProcessedQuestionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)&&
						beingClubbedQuestionType.getType().endsWith(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					/**** Half Hour Discussion Standalone Clubbed With Half Hour Discussion Standalone****/
					return HalfHourDiscussionSAClubbedWithHalfHourDiscussionSA(beingProcessedQuestion,beingClubbedQuestion,beingProcessedClubbing
							,beingClubbedClubbing,oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
				}else if(beingProcessedQuestionType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)&&
						beingClubbedQuestionType.getType().endsWith(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
					/**** Half Hour Discussion From Question Clubbed With Half Hour Discussion From Question ****/
					return HalfHourDiscussionFQClubbedWithHalfHourDiscussionFQ(beingProcessedQuestion,beingClubbedQuestion,beingProcessedClubbing
							,beingClubbedClubbing,oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
				}
			}
		}
		return status;
	}

	private Boolean HalfHourDiscussionFQClubbedWithHalfHourDiscussionFQ(
			Question beingProcessedQuestion, Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
			return beingProcessedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}else{
			return beingClubbedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}
	}

	private Boolean HalfHourDiscussionSAClubbedWithHalfHourDiscussionSA(
			Question beingProcessedQuestion, Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
			return beingProcessedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}else{
			return beingClubbedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}
	}

	private Boolean shortNoticeClubbedWithShortNotice(
			Question beingProcessedQuestion, Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
			return beingProcessedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}else{
			return beingClubbedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}
	}

	private Boolean unstarredClubbedWithUnstarred(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
			return beingProcessedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}else{
			return beingClubbedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}
	}

	private Boolean starredClubbedWithUnstarred(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		/**** Clubbed question is primary and unstarred ****/
		return beingClubbedIsPrimaryUnstarred(beingProcessedQuestion,
				beingClubbedQuestion,
				beingProcessedClubbing,
				beingClubbedClubbing,
				oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
	}

	private Boolean starredClubbedWithStarred(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		/**** Processed question is primary ****/
		if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
			return beingProcessedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}
		/**** Clubbed question is primary ****/
		else{
			return beingClubbedIsPrimary(beingProcessedQuestion,
					beingClubbedQuestion,
					beingProcessedClubbing,
					beingClubbedClubbing,
					oldPQStatus,oldCQStatus,newPQStatus,newCQStatus);
		}
	}

	private Boolean beingClubbedIsPrimaryUnstarred(
			Question beingProcessedQuestion, Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		try {
			String locale=beingProcessedQuestion.getLocale();
			String processedType=oldPQStatus.getType();
			String clubbedType=oldCQStatus.getType();
			if(processedType.equals("question_before_workflow_tobeputup")
					&&clubbedType.equals("question_before_workflow_tobeputup")){
				/**** Processed Question 
				 * a.parent=clubbed question
				 * b.clubbedentities=null
				 * c.internalstatus=recommednationstatus=clubbed
				 * d.merge****/
				beingProcessedQuestion.setParent(beingClubbedQuestion);
				beingProcessedQuestion.setClubbedEntities(null);
				newPQStatus=Status.findByType("question_before_workflow_clubbed", locale);
				beingProcessedQuestion.setInternalStatus(newCQStatus);
				beingProcessedQuestion.setRecommendationStatus(newCQStatus);
				beingProcessedQuestion.simpleMerge();                
				/**** create new clubbed entity****/
				ClubbedEntity clubbedEntity=new ClubbedEntity();
				clubbedEntity.setDeviceType(beingProcessedQuestion.getType());
				clubbedEntity.setLocale(beingProcessedQuestion.getLocale());
				clubbedEntity.setQuestion(beingProcessedQuestion);
				clubbedEntity.persist();                
				/**** add this as clubbed entity in clubbed clubbed entity ****/
				beingClubbedClubbing.add(clubbedEntity);                
				/*** add clubbed entities of processed question in clubbed question ****/
				if(beingProcessedClubbing!=null){
					if(!beingProcessedClubbing.isEmpty()){
						for(ClubbedEntity k:beingProcessedClubbing){
							Question question=k.getQuestion();
							question.setParent(beingClubbedQuestion);
							question.setInternalStatus(newPQStatus);
							question.setRecommendationStatus(newPQStatus);
							question.simpleMerge();
							k.setQuestion(question);
							k.merge();
							beingClubbedClubbing.add(k);
						}                        
					}
				}
				/**** update being clubbed question ****/
				beingClubbedQuestion.setParent(null);
				//newCQStatus=Status.findByType("question_before_workflow_tobeputup", locale);
				//beingClubbedQuestion.setInternalStatus(newCQStatus);
				//beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setClubbedEntities(beingClubbedClubbing);
				beingClubbedQuestion.simpleMerge();
				List<ClubbedEntity> clubbedEntities=beingClubbedQuestion.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
				Integer position=1;
				for(ClubbedEntity i:clubbedEntities){
					i.setPosition(position);
					position++;
					i.merge();
				}
			}else if(processedType.equals("question_before_workflow_tobeputup")
					&&(clubbedType.equals("question_workflow_approving_admission")
							||clubbedType.equals("question_workflow_approving_rejection")
							||clubbedType.equals("question_workflow_approving_converttounstarred"))){
				newPQStatus=Status.findByType("question_before_workflow_tobenameclubbed", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if(processedType.equals("question_before_workflow_tobeputup")
					&&clubbedType.equals("question_workflow_approving_clarificationneeded")){
				newPQStatus=Status.findByType("question_before_workflow_putonhold", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if(processedType.equals("question_before_workflow_tobeputup")
					&&(clubbedType.equals("question_workflow_decisionstatus_admission")
							||clubbedType.equals("question_workflow_decisionstatus_rejection")
							||clubbedType.equals("question_workflow_decisionstatus_converttounstarred")
							||clubbedType.equals("question_workflow_decisionstatus_onhold")
							||clubbedType.equals("question_workflow_decisionstatus_discuss")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationneeded")
							||clubbedType.equals("question_workflow_decisionstatus_nameclubbing")
							||clubbedType.equals("question_workflow_decisionstatus_sendback")
							||clubbedType.equals("question_workflow_decisionstatus_groupchanged")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationreceived")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
					)){
				newPQStatus=Status.findByType("question_before_workflow_tobeclubbedwithpending", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if(clubbedType.equals("questi" +
			"on_before_workflow_tobeputup")
			&&(processedType.equals("question_workflow_approving_admission")
					||processedType.equals("question_workflow_approving_rejection")
					||processedType.equals("question_workflow_approving_converttounstarred"))){
				newCQStatus=Status.findByType("question_before_workflow_nameclub", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}else if(clubbedType.equals("question_before_workflow_tobeputup")
					&&processedType.equals("question_workflow_approving_clarificationneeded")){
				newCQStatus=Status.findByType("question_before_workflow_putonhold", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}else if(clubbedType.equals("question_before_workflow_tobeputup")
					&&(processedType.equals("question_workflow_decisionstatus_admission")
							||processedType.equals("question_workflow_decisionstatus_rejection")
							||processedType.equals("question_workflow_decisionstatus_converttounstarred")
							||processedType.equals("question_workflow_decisionstatus_onhold")
							||processedType.equals("question_workflow_decisionstatus_discuss")
							||processedType.equals("question_workflow_decisionstatus_clarificationneeded")
							||processedType.equals("question_workflow_decisionstatus_nameclubbing")
							||processedType.equals("question_workflow_decisionstatus_sendback")
							||processedType.equals("question_workflow_decisionstatus_groupchanged")
							||processedType.equals("question_workflow_decisionstatus_clarificationreceived")
							||processedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
					)){
				newCQStatus=Status.findByType("question_before_workflow_tobeclubbedwithpending", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	private Boolean beingClubbedIsPrimary(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {

		try {
			String locale=beingProcessedQuestion.getLocale();
			String processedType=oldPQStatus.getType();
			String clubbedType=oldCQStatus.getType();
			if(	(processedType.equals("question_before_workflow_tobeputup") 
					&& clubbedType.equals("question_before_workflow_tobeputup"))||
					(processedType.equals("question_assistantprocessed") && clubbedType.equals("question_assistantprocessed"))){
				/**** Processed Question 
				 * a.parent=clubbed question
				 * b.clubbedentities=null
				 * c.internalstatus=recommednationstatus=clubbed
				 * d.merge****/
				beingProcessedQuestion.setParent(beingClubbedQuestion);
				beingProcessedQuestion.setClubbedEntities(null);
				newPQStatus=Status.findByType("question_before_workflow_clubbed", locale);
				beingProcessedQuestion.setInternalStatus(newCQStatus);
				beingProcessedQuestion.setRecommendationStatus(newCQStatus);
				beingProcessedQuestion.simpleMerge();                
				/**** create new clubbed entity****/
				ClubbedEntity clubbedEntity=new ClubbedEntity();
				clubbedEntity.setDeviceType(beingProcessedQuestion.getType());
				clubbedEntity.setLocale(beingProcessedQuestion.getLocale());
				clubbedEntity.setQuestion(beingProcessedQuestion);
				clubbedEntity.persist();                
				/**** add this as clubbed entity in clubbed clubbed entity ****/
				beingClubbedClubbing.add(clubbedEntity);                
				/*** add clubbed entities of processed question in clubbed question ****/
				if(beingProcessedClubbing!=null){
					if(!beingProcessedClubbing.isEmpty()){
						for(ClubbedEntity k:beingProcessedClubbing){
							Question question=k.getQuestion();
							question.setParent(beingClubbedQuestion);
							question.setInternalStatus(newPQStatus);
							question.setRecommendationStatus(newPQStatus);
							question.simpleMerge();
							k.setQuestion(question);
							k.merge();
							beingClubbedClubbing.add(k);
						}                        
					}
				}
				/**** update being clubbed question ****/
				beingClubbedQuestion.setParent(null);
				//newCQStatus=Status.findByType("question_before_workflow_tobeputup", locale);
				//beingClubbedQuestion.setInternalStatus(newCQStatus);
				//beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setClubbedEntities(beingClubbedClubbing);
				beingClubbedQuestion.simpleMerge();
				List<ClubbedEntity> clubbedEntities=beingClubbedQuestion.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
				Integer position=1;
				for(ClubbedEntity i:clubbedEntities){
					i.setPosition(position);
					position++;
					i.merge();
				}
				/**** Name Clubbing with Admitted Question ****/
			}else if((processedType.equals("question_assistantprocessed")||
					processedType.equals("question_before_workflow_tobeputup"))
					&&(clubbedType.equals("question_workflow_approving_admission"))){
				newPQStatus=Status.findByType("question_contains_name_clubbings", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if((processedType.equals("question_assistantprocessed")||
					processedType.equals("question_before_workflow_tobeputup"))
					&&clubbedType.equals("question_workflow_approving_clarificationneeded")){
				newPQStatus=Status.findByType("question_before_workflow_putonhold", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if((processedType.equals("question_assistantprocessed")||
					processedType.equals("question_before_workflow_tobeputup"))
					&&(clubbedType.equals("question_workflow_decisionstatus_admission")
							||clubbedType.equals("question_workflow_decisionstatus_rejection")
							||clubbedType.equals("question_workflow_decisionstatus_converttounstarred")
							||clubbedType.equals("question_workflow_decisionstatus_onhold")
							||clubbedType.equals("question_workflow_decisionstatus_discuss")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationneeded")
							||clubbedType.equals("question_workflow_decisionstatus_nameclubbing")
							||clubbedType.equals("question_workflow_decisionstatus_sendback")
							||clubbedType.equals("question_workflow_decisionstatus_groupchanged")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationreceived")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
					)){
				newPQStatus=Status.findByType("question_before_workflow_tobeclubbedwithpending", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if((clubbedType.equals("question_before_workflow_tobeputup")
					||clubbedType.equals("question_assistantprocessed"))
			&&(processedType.equals("question_workflow_approving_admission")
					||processedType.equals("question_workflow_approving_rejection")
					||processedType.equals("question_workflow_approving_converttounstarred"))){
				newCQStatus=Status.findByType("question_before_workflow_nameclub", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}else if((clubbedType.equals("question_before_workflow_tobeputup")
					||clubbedType.equals("question_assistantprocessed"))
					&&processedType.equals("question_workflow_approving_clarificationneeded")){
				newCQStatus=Status.findByType("question_before_workflow_putonhold", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}else if((clubbedType.equals("question_before_workflow_tobeputup")
					||clubbedType.equals("question_assistantprocessed"))
					&&(processedType.equals("question_workflow_decisionstatus_admission")
							||processedType.equals("question_workflow_decisionstatus_rejection")
							||processedType.equals("question_workflow_decisionstatus_converttounstarred")
							||processedType.equals("question_workflow_decisionstatus_onhold")
							||processedType.equals("question_workflow_decisionstatus_discuss")
							||processedType.equals("question_workflow_decisionstatus_clarificationneeded")
							||processedType.equals("question_workflow_decisionstatus_nameclubbing")
							||processedType.equals("question_workflow_decisionstatus_sendback")
							||processedType.equals("question_workflow_decisionstatus_groupchanged")
							||processedType.equals("question_workflow_decisionstatus_clarificationreceived")
							||processedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
					)){
				newCQStatus=Status.findByType("question_before_workflow_tobeclubbedwithpending", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;		
	}

	private Boolean beingProcessedIsPrimary(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			List<ClubbedEntity> beingProcessedClubbing,
			List<ClubbedEntity> beingClubbedClubbing, Status oldPQStatus,
			Status oldCQStatus, Status newPQStatus, Status newCQStatus) {
		try {
			String locale=beingProcessedQuestion.getLocale();
			String processedType=oldPQStatus.getType();
			String clubbedType=oldCQStatus.getType();
			/**** both questions are still to be sent for approval 
			 * a.processed question parent=null(primary question)
			 * b.clubbed question parent=processed question
			 * c.clubbed question clubbing=null
			 * d.save current clubbed question entity
			 * e.*****/
			if(	(processedType.equals("question_before_workflow_tobeputup") && clubbedType.equals("question_before_workflow_tobeputup"))||
					(processedType.equals("question_assistantprocessed") && clubbedType.equals("question_assistantprocessed"))){
				/**** Clubbed Question 
				 * a.parent=processed question
				 * b.clubbedentities=null
				 * c.internalstatus=recommednationstatus=clubbed
				 * d.merge****/
				beingClubbedQuestion.setParent(beingProcessedQuestion);
				beingClubbedQuestion.setClubbedEntities(null);
				newCQStatus=Status.findByType("question_before_workflow_clubbed", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.simpleMerge();                
				/**** create new clubbed entity****/
				ClubbedEntity clubbedEntity=new ClubbedEntity();
				clubbedEntity.setDeviceType(beingClubbedQuestion.getType());
				clubbedEntity.setLocale(beingClubbedQuestion.getLocale());
				clubbedEntity.setQuestion(beingClubbedQuestion);
				clubbedEntity.persist();                
				/**** add this as clubbed entity in processed clubbed entity ****/
				beingProcessedClubbing.add(clubbedEntity);                
				/*** add clubbed entities of clubbed question in processed question ****/
				if(beingClubbedClubbing!=null){
					if(!beingClubbedClubbing.isEmpty()){
						for(ClubbedEntity k:beingClubbedClubbing){
							Question question=k.getQuestion();
							question.setParent(beingProcessedQuestion);
							question.setInternalStatus(newCQStatus);
							question.setRecommendationStatus(newCQStatus);
							question.simpleMerge();
							k.setQuestion(question);
							k.merge();
							beingProcessedClubbing.add(k);
						}                        
					}
				}
				/**** update being processed question ****/
				beingProcessedQuestion.setParent(null);
				beingProcessedQuestion.setClubbedEntities(beingProcessedClubbing);
				beingProcessedQuestion.simpleMerge();
				List<ClubbedEntity> clubbedEntities=beingProcessedQuestion.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
				Integer position=1;
				for(ClubbedEntity i:clubbedEntities){
					i.setPosition(position);
					position++;
					i.merge();
				}
			}else if((processedType.equals("question_before_workflow_tobeputup")||
					processedType.equals("question_assistantprocessed"))
					&&(clubbedType.equals("question_workflow_approving_admission")
							||clubbedType.equals("question_workflow_approving_rejection")
							||clubbedType.equals("question_workflow_approving_converttounstarred"))){
				newPQStatus=Status.findByType("question_before_workflow_tobenameclubbed", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if((processedType.equals("question_before_workflow_tobeputup")||
					processedType.equals("question_assistantprocessed"))
					&&clubbedType.equals("question_workflow_approving_clarificationneeded")){
				newPQStatus=Status.findByType("question_before_workflow_putonhold", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if((processedType.equals("question_before_workflow_tobeputup")||
					processedType.equals("question_assistantprocessed"))
					&&(clubbedType.equals("question_workflow_decisionstatus_admission")
							||clubbedType.equals("question_workflow_decisionstatus_rejection")
							||clubbedType.equals("question_workflow_decisionstatus_converttounstarred")
							||clubbedType.equals("question_workflow_decisionstatus_onhold")
							||clubbedType.equals("question_workflow_decisionstatus_discuss")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationneeded")
							||clubbedType.equals("question_workflow_decisionstatus_nameclubbing")
							||clubbedType.equals("question_workflow_decisionstatus_sendback")
							||clubbedType.equals("question_workflow_decisionstatus_groupchanged")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationreceived")
							||clubbedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
					)){
				newPQStatus=Status.findByType("question_before_workflow_tobeclubbedwithpending", locale);
				beingProcessedQuestion.setInternalStatus(newPQStatus);
				beingProcessedQuestion.setRecommendationStatus(newPQStatus);
				beingProcessedQuestion.setProspectiveClubbings(beingProcessedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingClubbedQuestion.getId()+"##");
				beingProcessedQuestion.simpleMerge();
			}else if((clubbedType.equals("question_before_workflow_tobeputup")||
					clubbedType.equals("question_assistantprocessed"))
					&&(processedType.equals("question_workflow_approving_admission")
							||processedType.equals("question_workflow_approving_rejection")
							||processedType.equals("question_workflow_approving_converttounstarred"))){
				newCQStatus=Status.findByType("question_before_workflow_tobenameclubbed", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##NAMECLUB~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}else if((clubbedType.equals("question_before_workflow_tobeputup")||
					clubbedType.equals("question_assistantprocessed"))
					&&processedType.equals("question_workflow_approving_clarificationneeded")){
				newCQStatus=Status.findByType("question_before_workflow_putonhold", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##PUTONHOLD~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}else if((clubbedType.equals("question_before_workflow_tobeputup")||
					clubbedType.equals("question_assistantprocessed"))
					&&(processedType.equals("question_workflow_decisionstatus_admission")
							||processedType.equals("question_workflow_decisionstatus_rejection")
							||processedType.equals("question_workflow_decisionstatus_converttounstarred")
							||processedType.equals("question_workflow_decisionstatus_onhold")
							||processedType.equals("question_workflow_decisionstatus_discuss")
							||processedType.equals("question_workflow_decisionstatus_clarificationneeded")
							||processedType.equals("question_workflow_decisionstatus_nameclubbing")
							||processedType.equals("question_workflow_decisionstatus_sendback")
							||processedType.equals("question_workflow_decisionstatus_groupchanged")
							||processedType.equals("question_workflow_decisionstatus_clarificationreceived")
							||processedType.equals("question_workflow_decisionstatus_clarificationnotreceived")
					)){
				newCQStatus=Status.findByType("question_before_workflow_tobeclubbedwithpending", locale);
				beingClubbedQuestion.setInternalStatus(newCQStatus);
				beingClubbedQuestion.setRecommendationStatus(newCQStatus);
				beingClubbedQuestion.setProspectiveClubbings(beingClubbedQuestion.getProspectiveClubbings()+"##CLUBWITHPENDING~"+beingProcessedQuestion.getId()+"##");
				beingClubbedQuestion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	private Boolean alreadyClubbed(final Question beingProcessedQuestion,
			final Question beingClubbedQuestion,final String locale) {
		ClubbedEntity clubbedEntity1=ClubbedEntity.findByFieldName(ClubbedEntity.class,"question",
				beingClubbedQuestion, locale);
		if(clubbedEntity1!=null){
			return true;
		}else{
			ClubbedEntity clubbedEntity2=ClubbedEntity.findByFieldName(ClubbedEntity.class,"question",
					beingProcessedQuestion, locale);
			if(clubbedEntity2!=null){
				return true;
			}else{
				return false;
			}
		}
	}

	/**
	 * Unclub.
	 *
	 * @param questionBeingProcessed the question being processed
	 * @param questionBeingClubbed the question being clubbed
	 * @param locale the locale
	 * @return the boolean
	 */
	public Boolean unclub(final Long questionBeingProcessed,
			final Long questionBeingClubbed, final String locale) {
		try {
			Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
			Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
			ClubbedEntity clubbedEntityToRemove=null;

			/**** If processed question's number is less than clubbed question's number
			 * then clubbed question is removed from the clubbing of processed question
			 * ,clubbed question's parent is set to null ,new clubbing of processed 
			 * question is set,their position is updated****/
			if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
				List<ClubbedEntity> oldClubbedQuestions=beingProcessedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(i.getQuestion().getId()!=beingClubbedQuestion.getId()){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedQuestions.add(i);
						}else{
							newClubbedQuestions.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
						clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedQuestions.isEmpty()){
					beingProcessedQuestion.setClubbedEntities(newClubbedQuestions);
				}else{
					beingProcessedQuestion.setClubbedEntities(null);
				}            
				beingProcessedQuestion.simpleMerge();
				clubbedEntityToRemove.remove();
				beingClubbedQuestion.setParent(null);
				String clubbedDeviceType=beingClubbedQuestion.getType().getType();
				Status newstatus=null;
				if(clubbedDeviceType.equals("questions_unstarred")
						||clubbedDeviceType.equals("questions_halfhourdiscussion_from_question")
						||clubbedDeviceType.equals("questions_shortnotice")){
					newstatus=Status.findByFieldName(Status.class,"type","question_assistantprocessed", locale);
				}else{
					newstatus=Status.findByFieldName(Status.class,"type","question_before_workflow_tobeputup", locale);
				}
				beingClubbedQuestion.setInternalStatus(newstatus);
				beingClubbedQuestion.setRecommendationStatus(newstatus);
				beingClubbedQuestion.simpleMerge();
			}else if(beingProcessedQuestion.getNumber()>beingClubbedQuestion.getNumber()){
				List<ClubbedEntity> oldClubbedQuestions=beingClubbedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(i.getQuestion().getId()!=beingProcessedQuestion.getId()){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedQuestions.add(i);
						}else{
							newClubbedQuestions.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
						clubbedEntityToRemove=i;
					}
				}
				beingClubbedQuestion.setClubbedEntities(newClubbedQuestions);
				beingClubbedQuestion.simpleMerge();
				clubbedEntityToRemove.remove();
				beingProcessedQuestion.setParent(null);
				String clubbedDeviceType=beingClubbedQuestion.getType().getType();
				Status newstatus=null;
				if(clubbedDeviceType.equals("questions_unstarred")
						||clubbedDeviceType.equals("questions_halfhourdiscussion_from_question")
						||clubbedDeviceType.equals("questions_shortnotice")){
					newstatus=Status.findByFieldName(Status.class,"type","question_assistantprocessed", locale);
				}else{
					newstatus=Status.findByFieldName(Status.class,"type","question_before_workflow_tobeputup", locale);
				}
				beingProcessedQuestion.setInternalStatus(newstatus);
				beingProcessedQuestion.setRecommendationStatus(newstatus);
				beingProcessedQuestion.simpleMerge();
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
		return true;
	}


	//=========== ADD FOLLOWING METHODS ==========================
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
	 * Creates the member ballot attendance.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param locale the locale
	 * @return the boolean
	 */
	@SuppressWarnings("unchecked")
	public Boolean createMemberBallotAttendance(
			final Session session, final DeviceType questionType, final String locale) {
		/*
		 * first we will check if attendance has already been created for particular
		 * session ,device type and locale
		 */
		Boolean status=memberBallotCreated(session,questionType,locale);
		Boolean operationStatus=false;
		List<MemberBallotAttendance> memberBallotAttendances=new ArrayList<MemberBallotAttendance>();
		if(!status){
			List<Member> members=new ArrayList<Member>();
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP", "");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				Date startTime = FormaterUtil.formatStringToDate(session.getParameter(questionType.getType() +"_submissionFirstBatchStartDate"), customParameter.getValue(), session.getLocale());
				Date endTime = FormaterUtil.formatStringToDate(session.getParameter(questionType.getType() +"_submissionFirstBatchEndDate"), customParameter.getValue(), session.getLocale());
				if(startTime!=null && endTime!=null){
					String startTimeStr=format.format(startTime);
					String endTimeStr=format.format(endTime);
					String query="SELECT DISTINCT m FROM Question q JOIN q.primaryMember m JOIN m.title t WHERE q.session.id="+session.getId()+
					" AND q.type.id="+questionType.getId()+" AND q.submissionDate>='"+startTimeStr+"' AND q.submissionDate<='"+endTimeStr+"'"+
					" ORDER BY m.lastName "+ApplicationConstants.ASC;
					members=this.em().createQuery(query).getResultList();
					for(Member i:members){
						MemberBallotAttendance memberBallotAttendance=new MemberBallotAttendance(session,questionType,i,false,locale);
						memberBallotAttendance.persist();
						memberBallotAttendances.add(memberBallotAttendance);
					}
					operationStatus=true;
				}else if(startTime==null){
					logger.error("**** First Batch Submission Start Date not set ****");
				}else if(endTime==null){
					logger.error("**** First Batch Submission End Date not set ****");
				}
			}else{
				logger.error("**** Custom Parameter 'DB_TIMESTAMP(yyyy-MM-dd HH:mm:ss)' not set ****");
			}
		}
		return operationStatus;
	}

	/**
	 * Member ballot created.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param locale the locale
	 * @return the boolean
	 */
	private Boolean memberBallotCreated(final Session session, final DeviceType questionType,
			final String locale) {
		String query="SELECT m FROM MemberBallotAttendance m WHERE m.session.id="+session.getId()+
		" AND m.deviceType="+questionType.getId()+" AND m.locale='"+locale+"'";
		Integer count=this.em().createQuery(query).getResultList().size();
		if(count>0){
			return true;
		}else{
			return false;
		}
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
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
		List<Question> questions=new ArrayList<Question>();
		if(customParameter!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
			String query="SELECT q FROM Question q JOIN q.primaryMember m JOIN q.session s JOIN q.type qt "+
			" WHERE m.id="+member.getId()+" AND s.id="+session.getId()+" AND qt.id="+questionType.getId()+
			" AND q.locale='"+locale+"' AND q.internalStatus.type='question_workflow_approving_admission'  "+
			" AND q.submissionDate>='"+format.format(session.getParameter("questions_starred_submissionFirstBatchStartDate"))+"' "+
			" AND q.submissionDate<='"+format.format(session.getParameter("questions_starred_submissionFirstBatchEndDate"))+"' ORDER BY q.number "+ApplicationConstants.ASC;
			questions=this.em().createQuery(query).getResultList();
		}else{
			logger.error("Custom Parameter 'DB_DATETIMEFORMAT' not set");
		}
		return questions;
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final Question question) {
		String query="SELECT ce FROM Question m JOIN m.clubbedEntities ce WHERE m.id="+question.getId()+
		" ORDER BY ce.position "+ApplicationConstants.ASC;
		return this.em().createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByQuestionNumber(
			final Question question,final String sortOrder,final String locale) {
		String query="SELECT m FROM Question q JOIN q.clubbedEntities m WHERE q.id="+question.getId()+" ORDER BY "+
		" m.question.number "+sortOrder;
		return this.em().createQuery(query).getResultList();
	}

	public Boolean referencing(Long primaryId, Long referencingId, String locale) {
		try {
			Question primaryQuestion=Question.findById(Question.class,primaryId);
			Question referencedQuestion=Question.findById(Question.class,referencingId);
			List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
			referencedEntities=primaryQuestion.getReferencedEntities();		
			boolean alreadyRefered=false;
			int position=0;
			for(ReferencedEntity i:referencedEntities){
				if(i.getQuestion().getId()==referencedQuestion.getId()){
					alreadyRefered=true;
				}
				position++;
			}
			if(!alreadyRefered){
				ReferencedEntity referencedEntity=new ReferencedEntity();
				referencedEntity.setLocale(referencedQuestion.getLocale());
				referencedEntity.setQuestion(referencedQuestion);
				referencedEntity.setPosition(position+1);
				referencedEntity.setDeviceType(referencedQuestion.getType());
				referencedEntity.persist();
				referencedEntities.add(referencedEntity);
				if(!referencedEntities.isEmpty()){
					primaryQuestion.setReferencedEntities(referencedEntities);
				}else{
					primaryQuestion.setReferencedEntities(null);
				}
				Status status=Status.findByFieldName(Status.class,"type","question_contains_references", locale);
				primaryQuestion.setInternalStatus(status);
				primaryQuestion.setRecommendationStatus(status);
				primaryQuestion.simpleMerge();
			}else{
				return false;
			}			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public Boolean deReferencing(Long primaryId, Long referencingId, String locale) {
		try {
			Question primaryQuestion=Question.findById(Question.class,primaryId);
			Question referencedQuestion=Question.findById(Question.class,referencingId);
			List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
			List<ReferencedEntity> newReferencedEntities=new ArrayList<ReferencedEntity>();
			referencedEntities=primaryQuestion.getReferencedEntities();
			ReferencedEntity referencedEntityToRemove=null;
			for(ReferencedEntity i:referencedEntities){
				if(i.getQuestion().getId()==referencedQuestion.getId()){
					referencedEntityToRemove=i;				
				}else{
					newReferencedEntities.add(i);
				}
			}
			if(!newReferencedEntities.isEmpty()){
				primaryQuestion.setReferencedEntities(newReferencedEntities);
			}else{
				primaryQuestion.setReferencedEntities(null);
				Status status=Status.findByFieldName(Status.class,"type","question_before_workflow_tobeputup", locale);
				primaryQuestion.setInternalStatus(status);
				primaryQuestion.setRecommendationStatus(status);
			}		
			primaryQuestion.simpleMerge();
			referencedEntityToRemove.remove();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
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

		query.append(" ORDER BY m.lastName " + sortOrder);

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

		query.append(" ORDER BY m.lastName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		List<Member> members = tQuery.getResultList();
		return members;
	}
	
	//------------------------------added by vikas & dhananjay 21012013------------------------------------
	/**
	 * Find.
	 *
	 * @param session the session
	 * @param number the number
	 * @return the question
	 */
	public Question find(final Session session, final Integer number, Long deviceTypeId) {
		if(session != null && number != null && deviceTypeId != null){
			DeviceType deviceType = DeviceType.findById(DeviceType.class, deviceTypeId);
			Search search = new Search();
			search.addFilterEqual("session", session);
			search.addFilterEqual("number", number);
			search.addFilterNotEqual("type", deviceType);
			return this.searchUnique(search);
		}else{
			return null;
		}
	}
	

}
