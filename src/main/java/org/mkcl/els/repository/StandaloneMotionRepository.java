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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.StandaloneMotionDraft;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
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
public class StandaloneMotionRepository extends BaseRepository<StandaloneMotion, Long> {

	/**
	 * Assign question no.
	 *
	 * @param houseType the house type
	 * @param session the session
	 * @param deviceType the question type
	 * @param locale the locale
	 * @return the integer
	 * @throws ELSException 
	 */
	@SuppressWarnings("unchecked")
	public Integer assignStandaloneMotionNo(final HouseType houseType, 
			final Session session,
			final DeviceType deviceType, 
			final String locale) throws ELSException {
		String strHouseType = houseType.getType();
		String strDeviceType = deviceType.getType();
		Long house = session.getHouse().getId();
		
		String query = null;
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			if(strDeviceType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				query = "SELECT q" +
					" FROM StandaloneMotion q JOIN q.session s JOIN s.house h JOIN q.type dt" +
					" WHERE s.id = " + session.getId() + 
					" AND (dt.type = '" + 
					ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE + "') " +
					" ORDER BY q.number " + ApplicationConstants.DESC;
			}
		}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {			
			if(strDeviceType.equals(
					ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				query = "SELECT q" +
					" FROM StandaloneMotion q JOIN q.session s JOIN s.house h JOIN q.type dt" +
					" WHERE h.id = " + house + 
					" AND (dt.type = '" + 
						ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE + "') " +
					" ORDER BY q.number " + ApplicationConstants.DESC;
			}
		}
		try {
			List<StandaloneMotion> motions = this.em().createQuery(query).setFirstResult(0).setMaxResults(1).getResultList();
			if(motions == null) {
				return 0;
			}
			else if(motions.isEmpty()) {
				return 0;
			}
			else {
				if(motions.get(0).getNumber() == null) {
					return 0;
				}
				else {
					return motions.get(0).getNumber();
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
	 * @param StandaloneMotionId the standalone id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long motionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.STANDALONE_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query = this.em().createNativeQuery(strquery);
		query.setParameter("motionId", motionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> motionRevisionVOs = new ArrayList<RevisionHistoryVO>();

		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1 = null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO motionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				motionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				motionRevisionVO.setEditedAs(userGroupType.getName());
			}
			motionRevisionVO.setEditedBY(o[1].toString());
			motionRevisionVO.setEditedOn(o[2].toString());
			motionRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(o[4] != null && !o[4].toString().isEmpty() && o1[4] != null && !o1[4].toString().isEmpty()){
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
					motionRevisionVO.setDetails(question);
				}else{
					if(o[4] != null){
						motionRevisionVO.setDetails(o[4].toString());
					}
				}

			}else{
				if(o[4] != null){
					motionRevisionVO.setDetails(o[4].toString());
				}
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty() && !o1[5].toString().isEmpty()){
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
					motionRevisionVO.setSubject(question);
				}else{
					if(o[5] != null){
						motionRevisionVO.setSubject(o[5].toString());
					}
				}

			}else{
				if(o[5] != null){
					motionRevisionVO.setSubject(o[5].toString());
				}
			}
			if(o[6] != null){
				motionRevisionVO.setRemarks(o[6].toString());
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
						motionRevisionVO.setReason(question);
					}else{
						if(o[7] != null){
							motionRevisionVO.setReason(o[7].toString());
						}
					}

				}else{
					if(o[7] != null){
						motionRevisionVO.setReason(o[7].toString());
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
						motionRevisionVO.setBriefExplanation(question);
					}else{
						if(o[8] != null){
							motionRevisionVO.setBriefExplanation(o[8].toString());	
						}
					}

				}else{
					if(o[8] != null){
						motionRevisionVO.setBriefExplanation(o[8].toString());
					}
				}
			}
			motionRevisionVOs.add(motionRevisionVO);
		}
		return motionRevisionVOs;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of StandaloneMotion.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfStandaloneMotions the max no of standaloneMotions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<StandaloneMotion> findNonDiscussionDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Integer maxNoOfStandaloneMotions,
			final String sortOrder,
			final String locale) {
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId AND q.type.id=:deviceTypeId");
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.number IS NOT NULL");
		}
		
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
		
			query.append(" AND q.group.id=:groupId AND q.discussionDate IS NULL AND q.number IS NULL");
		}
		query.append(" AND q.locale=:locale");
		query.append(getStatusFilters(internalStatuses));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setMaxResults(maxNoOfStandaloneMotions);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> motions = tQuery.getResultList();
		return motions;
	}

	/**
	 * Find dated StandaloneMotion.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfStandaloneMotions the max no of standaloneMotions
	 * @param locale the locale
	 * @return the list
	 */
	public List<StandaloneMotion> findDatedStandalones(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date discussionDate,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final Integer maxNoOfStandaloneMotions,
			final String locale) {
		
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId "+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(
				" AND q.group.id=:groupId AND q.discussionDate<=:strDiscussionDate");
		}
		
		query.append(" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		
		query.append(this.getStatusFilters(internalStatuses));
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.number IS NOT NULL ORDER BY q.number ASC");
		}else{
			query.append(" ORDER BY q.discussionDate DESC, q.number ASC");
		}

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strDiscussionDate", discussionDate);
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
		tQuery.setMaxResults(maxNoOfStandaloneMotions);
		List<StandaloneMotion> motions = tQuery.getResultList();
		return motions;
	}

	/**
	 * Find.
	 *
	 * @param session the session
	 * @param number the number
	 * @return the question
	 */
	public StandaloneMotion find(final Session session, final Integer number) {
		String strQuery="SELECT q FROM StandaloneMotion q WHERE q.session=:session AND q.number=:number";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("session", session);
		query.setParameter("number", number);
		return (StandaloneMotion) query.getSingleResult();
	}
	
	public StandaloneMotion find(final Member member, 
			final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException{
		StandaloneMotion motion = null;
		try{
			Status internalStatus= Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			String strQuery = "SELECT MIN(q) FROM StandaloneMotion q" +
						" WHERE q.primaryMember.id=:memberId AND q.session.id=:sessionId"+
						" AND q.internalStatus.id=:internalStatusId AND q.type.id=:deviceTypeId"+
						" AND q.locale=:locale";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("memberId", member.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("internalStatusId", internalStatus.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			motion =  (StandaloneMotion) query.getSingleResult();
		}catch (Exception e) {
			ELSException elsException = new ELSException();
			elsException.setParameter("StandaloneMotionRepository_Motion_find(Member...)", "No data found.");
			throw elsException;			
		}
		return motion;		
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
	public List<StandaloneMotion> findAll(final Member currentMember, 
			final Session session,
			final DeviceType deviceType, 
			final Status internalStatus) {
		List<StandaloneMotion> motions = new ArrayList<StandaloneMotion>();
		String strQuery = "SELECT q" +
				" FROM StandaloneMotion q " +
				" WHERE q.primaryMember.id=:memberId AND q.session.id=:sessionId "+ 
				" AND q.type.id=:deviceTypeId AND q.internalStatus.id=:internalStatusId"+ 
				" ORDER BY q.number " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("memberId", currentMember.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("internalStatusId", internalStatus.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		motions = query.getResultList();
		return motions;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of StandaloneMotion.
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
	public List<StandaloneMotion> find(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date discussionDate,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final StandaloneMotion[] excludeMotions,
			final Integer maxNoOfMotions,
			final String sortOrder,
			final String locale) {
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id=:groupId");
		}
		query.append(" AND q.discussionDate=:strDiscussionDate"+
				" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getStandaloneMotionFilters(excludeMotions));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setMaxResults(maxNoOfMotions);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strDiscussionDate", discussionDate);
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> motions = tQuery.getResultList();
		return motions;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of StandaloneMotion.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param excludeStandaloneMotions the exclude standaloneMotions
	 * @param maxNoOfStandaloneMotions the max no of standaloneMotions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<StandaloneMotion> findBeforeDiscussionDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date discussionDate,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final StandaloneMotion[] excludeStandaloneMotions,
			final Integer maxNoOfStandaloneMotions,
			final String sortOrder,
			final String locale) {
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId AND q.discussionDate<:strDiscussionDate");
		}
		query.append(" AND q.submissionDate<=:strFinalSubmissionDate AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getStandaloneMotionFilters(excludeStandaloneMotions));

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		
		tQuery.setParameter("strDiscussionDate", discussionDate);		
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> motions = tQuery.getResultList();
		if(motions != null) {
			motions = StandaloneMotion.sortByDiscussionDate(motions, sortOrder);
			if(motions.size() >= maxNoOfStandaloneMotions) {
				return motions.subList(0, maxNoOfStandaloneMotions);
			}
		}
		return motions;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of StandaloneMotion.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param finalSubmissionDate the final submission date
	 * @param internalStatuses the internal statuses
	 * @param excludeStandaloneMotions the exclude standaloneMotions
	 * @param maxNoOfStandaloneMotions the max no of standaloneMotions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<StandaloneMotion> findNonDiscussionDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date finalSubmissionDate,
			final Status[] internalStatuses,
			final StandaloneMotion[] excludeStandaloneMotions,
			final Integer maxNoOfStandaloneMotions,
			final String sortOrder,
			final String locale) {
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id =:sessionId AND q.primaryMember.id =:memberId"+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id =:groupId AND q.discussionDate = null");
		}else{
			query.append(" AND q.number IS NOT NULL");
		}
		query.append(" AND q.submissionDate <=:strFinalSubmissionDate AND q.locale =:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(this.getStandaloneMotionFilters(excludeStandaloneMotions));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY q.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY q.number DESC");
		}

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
		tQuery.setMaxResults(maxNoOfStandaloneMotions);
		List<StandaloneMotion> motions = tQuery.getResultList();
		return motions;
	}

	
	/**
	 * Find dated StandaloneMotion.
	 *
	 * @param session the session
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param discussionDate the answering date
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfStandaloneMotions the max no of StandaloneMotions
	 * @param locale the locale
	 * @return the list
	 */
	public List<StandaloneMotion> findDatedStandaloneMotions(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date discussionDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final Integer maxNoOfStandaloneMotions,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id =:sessionId AND q.primaryMember.id =:memberId"+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId AND q.discussionDate <=:strDiscussionDate" );
		}
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" AND q.number IS NOT NULL" );
		}
		query.append(
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime" +
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			query.append(" ORDER BY q.number ASC");
		}else{
			query.append(" ORDER BY q.discussionDate DESC, q.number ASC");
		}

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strDiscussionDate", discussionDate);
		}
		
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("locale", locale);
		tQuery.setMaxResults(maxNoOfStandaloneMotions);
		List<StandaloneMotion> mos = tQuery.getResultList();
		return mos;
	}

	/**
	 * Returns null if there is no result, else returns a List
	 * of StandaloneMotion.
	 *
	 * @param session the sessionStandaloneMotion
	 * @param member the member
	 * @param deviceType the device type
	 * @param group the group
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param internalStatuses the internal statuses
	 * @param maxNoOfStandaloneMotions the max no of StandaloneMotions
	 * @param sortOrder the sort order
	 * @param locale the locale
	 * @return the list
	 */
	public List<StandaloneMotion> findNonDiscussionDate(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final Integer maxNoOfStandaloneMotions,
			final String sortOrder,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id=:deviceTypeId");
		
				if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
						&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
					query.append(" AND q.group.id=:groupId AND q.discussionDate IS NULL");
				}
				
				if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
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

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strEndTime",endTime);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("locale", locale);
		tQuery.setMaxResults(maxNoOfStandaloneMotions);
		List<StandaloneMotion> mos = tQuery.getResultList();
		return mos;
	}

	/**
	 * Find dated StandaloneMotion.
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
	public List<StandaloneMotion> findDatedStandaloneMotions(final Session session,
			final Member member,
			final DeviceType deviceType,
			final Group group,
			final Date discussionDate,
			final Date startTime,
			final Date endTime,
			final Status[] internalStatuses,
			final String locale) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//      "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		StringBuffer query = new StringBuffer(
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.primaryMember.id=:memberId"+
				" AND q.type.id=:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id=:groupId");
		}
		query.append(" AND q.discussionDate<=:strDiscussionDate" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		query.append(" ORDER BY q.discussionDate DESC, q.number ASC");

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strDiscussionDate", discussionDate);
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> mos = tQuery.getResultList();
		return mos;
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
	public List<StandaloneMotion> findNonDiscussionDate(final Session session,
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
				" SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id =:sessionId AND q.primaryMember.id =:memberId "+
				" AND q.type.id =:deviceTypeId");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id =:groupId ");
		}
		query.append(" AND q.discussionDate IS NULL"+
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

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId",  deviceType.getId());
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
		}
		
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> mos = tQuery.getResultList();
		return mos;
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
	public List<StandaloneMotion> find(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		
		StringBuffer query = new StringBuffer(
				" SELECT q  FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId"+
				" AND (q.discussionDate IS NULL OR q.discussionDate<=:strDiscussionDate)" +
				" AND q.submissionDate>=:strStartTime AND q.submissionDate<=:strEndTime"+
				" AND q.locale=:locale");
		query.append(this.getStatusFilters(internalStatuses));
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
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

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strDiscussionDate", discussionDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> mos = tQuery.getResultList();
		return mos;
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
	@Transactional
	@SuppressWarnings({"rawtypes" })
	public List<StandaloneMotion> findByBallot(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {		
		
		StringBuffer query = null;
		
		List<StandaloneMotion> mos = null;
		CustomParameter csptUseForLottery = CustomParameter.findByName(CustomParameter.class, "USE_FOR_LOTTERY", "");
		if(csptUseForLottery != null){
			if(csptUseForLottery.getValue().equals("yes")){
				if(isPreBallot.booleanValue()){
					query = new StringBuffer(" SELECT q.id,q.revised_subject FROM standalone_motions q" +
							" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId " +
							" AND ( q.discussion_date IS NULL OR q.discussion_date=:strDiscussionDate)" +
							" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime" +
							" AND q.locale=:locale" +
							" AND q.number IS NOT NULL");
				}else if(!isPreBallot.booleanValue()){
					query = new StringBuffer(
							" SELECT q.id,q.revised_subject FROM standalone_motions q" +
							" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId " +
							" AND ( q.discussion_date IS NULL OR q.discussion_date=:strDiscussionDate)" +
							" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime" +
							" AND q.locale=:locale" +
							" AND q.number IS NOT NULL");
				}
			}else if(csptUseForLottery.getValue().equals("no")){
				if(isPreBallot.booleanValue()){
					query = new StringBuffer(" SELECT q.id,q.revised_subject FROM standalone_motions q" +
							" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId " +
							" AND ( q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
							" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime" +
							" AND q.locale=:locale" +
							" AND q.number IS NOT NULL");
				}else if(!isPreBallot.booleanValue()){
					query = new StringBuffer(
							" SELECT q.id,q.revised_subject FROM standalone_motions q" +
							" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId " +
							" AND ( q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
							" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime" +
							" AND q.locale=:locale" +
							" AND q.number IS NOT NULL");
				}
			}
		}
		
		
		query.append(this.getStatusFiltersNative(internalStatuses));
		Status balloted = Status.findByFieldName(Status.class, "type", ApplicationConstants.STANDALONE_PROCESSED_BALLOTED, locale);
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
			
			Query tQuery = this.em().createNativeQuery(query.toString());
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strDiscussionDate", discussionDate);
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("locale", locale);
			
			if(isBalloted.booleanValue()){
				tQuery.setParameter("ballotStatus", balloted);
			}
			
			List data = tQuery.getResultList();
			
			if(data != null && !data.isEmpty()){
				mos = new ArrayList<StandaloneMotion>();
				for(Object o : data){
					Object[] objArr = (Object[]) o;
					if(objArr != null){
						if(objArr[0] != null){
							StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, new Long(objArr[0].toString()));					
							mos.add(q);
						}
					}
				}
			}
		}else if(!isPreBallot.booleanValue()){
			if(isMandatoryUnique.booleanValue()){
				query.append(" AND q.member_id NOT IN(SELECT" +
					" qqq.member_id FROM standalone_motions qqq" + 
					" WHERE qqq.id IN(SELECT ds.device_id" +
					" FROM ballots b" +
					" INNER JOIN ballots_ballot_entries bbe ON(bbe.ballot_id=b.id)" +
					" INNER JOIN ballot_entries be ON(be.id=bbe.ballot_entry_id)" +
					" INNER JOIN ballot_entries_device_sequences beds ON(beds.ballot_entry_id=bbe.ballot_entry_id)" +
					" INNER JOIN device_sequences ds ON(ds.id=beds.device_sequence_id)" +
					" WHERE b.session_id=:sessionId" +
					" AND b.devicetype_id=:deviceTypeId))");
				
				query.append(" AND q.revised_subject NOT IN (SELECT" +
						" qqq.revised_subject FROM standalone_motions qqq" + 
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
			tQuery.setParameter("strDiscussionDate", discussionDate);
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("locale", locale);
			
			if(isBalloted.booleanValue()){
				tQuery.setParameter("ballotStatus", balloted);
			}
			
			List data = tQuery.getResultList();
			
			if(data != null && !data.isEmpty()){
				mos = new ArrayList<StandaloneMotion>();
				for(Object o : data){
					Object[] obj = ((Object[]) o);
					if(obj[0] != null){
						String[] ids = obj[0].toString().split(",");
						
						StandaloneMotion q = StandaloneMotion.findById(StandaloneMotion.class, new Long(ids[0]));
						
						mos.add(q);
					}
				}
			}
		}
		//query.append(" ORDER BY q.revised_subject ASC,q.member_id ASC,q.submission_date DESC");

		
		return mos;
	}
	
	@SuppressWarnings("rawtypes")
	public String findBallotedMembers(final Session session, 
			final String memberNotice, 
			final DeviceType deviceType){
		StringBuffer retVal = null;	
		StringBuffer query = null;
		
		if(memberNotice.equals("notice")){
			query = new StringBuffer("SELECT GROUP_CONCAT(qqq.member_id),qqq.revised_subject" +
									" FROM standalone_motions qqq" +
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
										" FROM standalone_motion qqq" +
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
									" FROM standalone_motions qqq" +
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
	
	public StandaloneMotion findStandaloneMotionForMemberOfUniqueSubject(final Session session, 
			final DeviceType deviceType, 
			final Date discussionDate,  
			final Long memberID, 
			final List<String> subjects, 
			final String locale){
		StandaloneMotion mo = null;
		Status internalStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
		Status ballotStatus = Status.findByType(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED, locale);
		StringBuffer strQuery = new StringBuffer("SELECT q" +
			" FROM StandaloneMotion q JOIN q.session s JOIN q.houseType ht" +
			" WHERE q.session.id=:sessionId AND q.discussionDate IS NULL"+
			" AND q.primaryMember.id =:memberId AND q.number IS NOT NULL"+
			" AND q.internalStatus.id =:internalStatusId " +
			" AND ( q.ballotStatus.id !=:ballotStatusId OR q.ballotStatus.id IS NULL)" +
			" ORDER BY q.discussionDate ASC");
		
		TypedQuery<StandaloneMotion> query = this.em().createQuery(strQuery.toString(), StandaloneMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("memberId", memberID);
		query.setParameter("internalStatusId", internalStatus.getId());
		query.setParameter("ballotStatusId",  ballotStatus.getId());
		List<StandaloneMotion> mos = query.getResultList();
		mo = randomMotion(mos);
		return mo;
	}
	
	private StandaloneMotion randomMotion(List<StandaloneMotion> mos){
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		List<StandaloneMotion> randomMotionList = randomizeMotions(mos);
		StandaloneMotion mo = null;
		if(randomMotionList.size() > 0){
			mo = randomMotionList.get(Math.abs(rnd.nextInt() % mos.size())); 
		}
		return mo;		
	}
	
	private List<StandaloneMotion> randomizeMotions(final List<StandaloneMotion> motions) {
		List<StandaloneMotion> newMotions = new ArrayList<StandaloneMotion>();
		newMotions.addAll(motions);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newMotions, rnd);
		return newMotions;
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
	@SuppressWarnings({"rawtypes" })
	public List<Member> findPrimaryMembersByBallot(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
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
						" FROM standalone_motions q" +
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
						" FROM standalone_motions q" +
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
					" FROM standalone_motions qq" +
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
		tQuery.setParameter("strDiscussionDate", discussionDate);
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
	
	
	public List<StandaloneMotion> findStandaloneMotionsByDiscussionDateAndMember(final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {


		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT q FROM StandaloneMotion q" +
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

		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("discussionDate", discussionDate);
		tQuery.setParameter("startTime", startTime);
		tQuery.setParameter("endTime", endTime);
		tQuery.setParameter("memberId", memberId);
		tQuery.setParameter("locale", locale);
		List<StandaloneMotion> mos = tQuery.getResultList();
		return mos;
	}
	
	private String getStatusFilters(final Status[] internalStatuses,
			HouseType houseType) {
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
			final Date discussionDate,
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
				" SELECT DISTINCT(q.primaryMember) FROM StandaloneMotion q" +
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
		tQuery.setParameter("strDiscussionDate", discussionDate);
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
	@SuppressWarnings("unchecked")
	public List<Member> findPrimaryMembersForBallot(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
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
				"SELECT m.* FROM members m WHERE m.id IN (SELECT DISTINCT(q.member_id) FROM standalone_motions q" +
				" WHERE q.session_id=:sessionId AND q.devicetype_id=:deviceTypeId"+
				" AND (q.discussion_date IS NULL OR q.discussion_date<=:strDiscussionDate)" +
				" AND q.submission_date>=:strStartTime AND q.submission_date<=:strEndTime"+
				" AND q.locale=:locale" +
				" AND q.ballotstatus_id IS NULL");
		query.append(this.getStatusFiltersNative(internalStatuses));
		
		if(!hasParent) {
			query.append(" AND q.parent IS NULL");
		}		
		
		query.append(" AND q.subject NOT IN(SELECT DISTINCT qqq.revised_subject FROM standalone_motions qqq WHERE qqq.session_id=:sessionId" +
				" AND qqq.devicetype_id=:deviceTypeId" +
				" AND qqq.ballotstatus_id IS NOT NULL)" +
				" AND q.revised_subject NOT IN(SELECT DISTINCT qqq.revised_subject FROM standalone_motions qqq WHERE qqq.session_id=:sessionId" +
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
		tQuery.setParameter("strDiscussionDate", discussionDate);
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		List<Member> members = tQuery.getResultList();
		return members;
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final StandaloneMotion mo) {
		String strQuery = "SELECT ce FROM StandaloneMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:motionId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", mo.getId());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final StandaloneMotion mo, final String sortOrder) {
		String strQuery = "SELECT ce FROM StandaloneMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:motionId ORDER BY ce.position " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", mo.getId());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final StandaloneMotion mo, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m FROM StandaloneMotion q JOIN q.clubbedEntities m" +
				" WHERE q.id=:motionId ORDER BY m.standaloneMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", mo.getId());
		return query.getResultList();
	}
	
	public List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final StandaloneMotion mo, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM StandaloneMotion q JOIN q.clubbedEntities m" +
				" WHERE q.id=:motionId ORDER BY m.standaloneMotion.discussionDate,m.standaloneMotion.number " + sortOrder;
		TypedQuery<ClubbedEntity> query = this.em().createQuery(strQuery, ClubbedEntity.class);
		query.setParameter("motionId", mo.getId());
		return query.getResultList();
	}

	@SuppressWarnings("unused")
	public List<Member> findActiveMembersWithStandaloneMotions(final Session session,
			final MemberRole role,
			final Date activeOn,
			final DeviceType deviceType,
			final Group group,
			final Status[] internalStatuses,
			final Date discussionDate,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {

		String strActiveOn = FormaterUtil.formatDateToString(activeOn, "yyyy-MM-dd");
		String strDiscussionDate = null;
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			strDiscussionDate = FormaterUtil.formatDateToString(discussionDate, "yyyy-MM-dd");
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
		query.append(" FROM StandaloneMotion q");
		query.append(" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId");
				
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(
			" AND q.group.id=:groupId AND " +
			" (q.discussionDate<=:strDiscussionDate OR q.discussionDate IS NULL)");
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
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strDiscussionDate", discussionDate);
		}
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		List<Member> members = tQuery.getResultList();
		return members;
	}

	@SuppressWarnings("unused")
	public List<Member> findActiveMembersWithoutStandaloneMotions(final Session session,
			final MemberRole role,
			final Date activeOn,
			final DeviceType deviceType,
			final Group group,
			final Status[] internalStatuses,
			final Date discussionDate,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
		
		String strActiveOn = FormaterUtil.formatDateToString(activeOn, "yyyy-MM-dd");
		String strDiscussionDate = null;
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			strDiscussionDate = FormaterUtil.formatDateToString(discussionDate, "yyyy-MM-dd");
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
		query.append("FROM StandaloneMotion q");		
		query.append(" WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId ");
		
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			query.append(" AND q.group.id=:groupId AND " +
					"(q.discussionDate<=:strDiscussionDate OR q.discussionDate IS NULL)");
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
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
			tQuery.setParameter("groupId", group.getId());
			tQuery.setParameter("strDiscussionDate", discussionDate);
		}
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		List<Member> members = tQuery.getResultList();
		return members;
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
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	private String discussionDateAsString(final Date date) {
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
	private String getStandaloneMotionFilters(final StandaloneMotion[] excludeStandaloneMotions) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = excludeStandaloneMotions.length;
		for(int i = 0; i < n; i++) {
			sb.append(" q.id != " + excludeStandaloneMotions[i].getId());
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
		String strQuery="SELECT q FROM StandaloneMotion q" +
				" WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId" +
				" AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale" +
				" AND q.number IS NOT NULL"+
				" AND q.internalStatus.id!=:rejectStatus" +
				" AND q.internalStatus.id!=:repeatRejectStatus"+
				" ORDER BY q.id DESC";
		try{
			Status rejectedStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REJECTION, locale);
			Status repeatRejectedStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION, locale);
			
			TypedQuery<StandaloneMotion> query = this.em().createQuery(strQuery, StandaloneMotion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("memberId", member.getId());
			query.setParameter("locale", locale);		
			query.setParameter("rejectStatus",  rejectedStatus.getId());
			query.setParameter("repeatRejectStatus",  repeatRejectedStatus.getId());
			List<StandaloneMotion> mos = query.getResultList();
			return mos.size();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_Integer_getMemberPutupCount", "Cannot get the StandaloneMotion ");
			throw elsException;
		}
	}
	
	
	public List<StandaloneMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.STANDALONE_COMPLETE, locale);
			String query="SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
					" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
					" AND q.locale=:locale AND q.status.id=:statusId"+
					" ORDER BY q.id DESC";
			TypedQuery<StandaloneMotion> q=this.em().createQuery(query, StandaloneMotion.class);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("memberId", primaryMember.getId());
			q.setParameter("locale", locale);
			q.setParameter("statusId", status.getId());
			q.setMaxResults(itemsCount);
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_List<StandaloneMotion>_findAllByMember", "Cannot get the StandaloneMotions ");
			throw elsException;
		}
	}
	
	public Integer findStandaloneMotionWithoutNumber(final Member member, 
			final DeviceType deviceType, 
			final Session session,
			final String locale) throws ELSException{
		String strQuery="SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale AND q.number IS NULL";
		try{
		TypedQuery<StandaloneMotion> query=this.em().createQuery(strQuery, StandaloneMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("memberId", member.getId());
		query.setParameter("locale", locale);
		List<StandaloneMotion> questions = query.getResultList();
		if(questions.isEmpty()){
			return 0;
		}else{
			return questions.size();
		}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_Integer_getStandaloneMotionWithoutNumber", "Cannot get the StandaloneMotion Count  ");
			throw elsException;
		}
				
	}
	
	public Integer findStandaloneMotionWithNumber(final Member member, 
			final DeviceType deviceType, 
			final Session session,
			final String locale) throws ELSException{
		String strQuery="SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale AND q.number IS NOT NULL";
		try{
		TypedQuery<StandaloneMotion> query=this.em().createQuery(strQuery, StandaloneMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("memberId", member.getId());
		query.setParameter("locale", locale);
		List<StandaloneMotion> questions = query.getResultList();
		if(questions.isEmpty()){
			return 0;
		}else{
			return questions.size();
		}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_Integer_getStandaloneMotionWithNumber", "Cannot get the StandaloneMotion Count  ");
			throw elsException;
		}
				
	}
	
	public Integer findStandaloneMotionWithNumberExcludingRejected(final Member member, 
			final DeviceType deviceType, 
			final Session session,
			final String locale) throws ELSException{
		String strQuery="SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale AND q.number IS NOT NULL AND (q.internalStatus.id!=:internalStatusId OR q.internalStatus.id=:repInternalStatusId)";
		try{
			Status rejection = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REJECTION, locale);
			Status repRejection = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION, locale);
			
			TypedQuery<StandaloneMotion> query=this.em().createQuery(strQuery, StandaloneMotion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("memberId", member.getId());
			query.setParameter("locale", locale);
			query.setParameter("internalStatusId", rejection.getId());
			query.setParameter("repInternalStatusId", repRejection.getId());
			List<StandaloneMotion> questions = query.getResultList();
			if(questions.isEmpty()){
				return 0;
			}else{
				return questions.size();
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_Integer_getStandaloneMotionWithNumber", "Cannot get the StandaloneMotion Count  ");
			throw elsException;
		}
				
	}
	
	public List<StandaloneMotion> findRejectedStandaloneMotions(final Member member,
			final Session session, 
			final DeviceType deviceType, 
			final String locale) throws ELSException{
		try{
		Status rejectionStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REJECTION, locale);
		Status repeatRejectionStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REPEATREJECTION, locale);
		String strQuery="SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.primaryMember.id=:memberId"+
				" AND q.locale=:locale AND (q.internalStatus.id=:rejectionStatusId"+
				" OR q.internalStatus.id=:repeatRejectionStatusId)";
		TypedQuery<StandaloneMotion> query=this.em().createQuery(strQuery, StandaloneMotion.class);
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
			elsException.setParameter("StandaloneMotionRepository_List<StandaloneMotion>_getRejectedStandaloneMotions", "Cannot get the StandaloneMotion ");
			throw elsException;
		}
	}
	
	
	public String findRejectedStandaloneMotionsAsString(List<StandaloneMotion> mos, 
			final String locale) throws ELSException{
		try{	
			String rejectedStandaloneMotions="";
			
			for(StandaloneMotion q: mos){
				if(mos.get(0).equals(q)){
					rejectedStandaloneMotions=q.getNumber().toString();
				}else{
					rejectedStandaloneMotions = rejectedStandaloneMotions+","+q.getNumber().toString();
				}
			}
			
			return rejectedStandaloneMotions;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_String_findRejectedStandaloneMotionAsString", "Cannot get the Rejected StandaloneMotions.");
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
	@SuppressWarnings("rawtypes")
	public StandaloneMotionDraft findLatestStandaloneMotionDraftOfUser(final Long motionId, 
			final String username) throws ELSException {
		StandaloneMotionDraft standaloneMotionDraft = null;
		try{
			org.mkcl.els.domain.Query nativeQuery = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.STANDALONE_GET_LATEST_STANDALONEMOTIONDRAFT_OF_USER, "");
			String strquery = nativeQuery.getQuery();
			Query query = this.em().createNativeQuery(strquery);
			query.setParameter("motionId", motionId);
			query.setParameter("username",username);  
			List result = query.getResultList();
			for(Object i : result) {
				Long draftId = Long.parseLong(i.toString());
				standaloneMotionDraft = StandaloneMotionDraft.findById(StandaloneMotionDraft.class, draftId);			
				break;
			}
			return standaloneMotionDraft;	
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_StandaloneMotiontionDraft_getLatestMotionDraftOfUser", "Cannot get the StandaloneMotion Draft");
			throw elsException;
		}
	}
	
	public List<StandaloneMotion> findAllByStatus(final Session session,
			final DeviceType deviceType, 
			final Status internalStatus, 
			final Group group,
			final Integer itemsCount,
			final Integer file,
			final String locale) throws ELSException {
		StringBuffer query = new StringBuffer("SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId");
		}
		if(file.compareTo(0) > 0){
			query.append(" AND q.file=:fileNum");
		}
		query.append(" AND q.internalStatus.id=:internalStatusId"+
				" AND q.workflowStarted=:workflowStarted AND q.parent IS  NULL"+
				" ORDER BY q.number");
		try{
			TypedQuery<StandaloneMotion> q=this.em().createQuery(query.toString(), StandaloneMotion.class);
			q.setMaxResults(itemsCount);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("locale", locale);
			if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
					&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				q.setParameter("groupId", group.getId());
			}
			if(file.compareTo(0) > 0){
				q.setParameter("fileNum", file);
			}
			q.setParameter("internalStatusId", internalStatus.getId());
			q.setParameter("workflowStarted", "NO");
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_List<StandaloneMotion>_findAllByStatus", "Cannot get the StandaloneMotion");
			throw elsException;
		}
	}
	
	public List<StandaloneMotion> findAllByRecommendationStatus(final Session session,
			final DeviceType deviceType, 
			final Status recommendationStatus, 
			final Group group,
			final String locale) throws ELSException {
		StringBuffer query=new StringBuffer("SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId"+
				" AND q.type.id=:deviceTypeId AND q.locale=:locale");
		if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
				&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				query.append(" AND q.group.id=:groupId");
		}
		query.append(" AND q.recommendationStatus.id=:recommendationStatusId ORDER BY q.number");
		try{
			TypedQuery<StandaloneMotion> q=this.em().createQuery(query.toString(), StandaloneMotion.class);
			q.setParameter("sessionId", session.getId());
			q.setParameter("deviceTypeId", deviceType.getId());
			q.setParameter("locale", locale);
			if(!(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
					&& session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE))){
				q.setParameter("groupId", group.getId());
			}
			q.setParameter("recommendationStatusId", recommendationStatus.getId());
			return q.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_List<StandaloneMotion>_findAllByStatus", "Cannot get the StandaloneMotion");
			throw elsException;
		}
	}

	public StandaloneMotion getStandaloneMotion(final Long sessionId,final Long deviceTypeId, final Integer number,final String locale){
		String strQuery="SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId AND q.type.id=:deviceTypeId AND q.number=:number AND q.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("deviceTypeId", deviceTypeId);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		StandaloneMotion mo = (StandaloneMotion) query.getSingleResult();
		
		return mo;		
	}
	
	public StandaloneMotion getStandaloneMotion(final Long sessionId, final Integer number,final String locale){
		String strQuery = "SELECT q FROM StandaloneMotion q WHERE q.session.id=:sessionId AND q.number=:number AND q.locale=:locale";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		StandaloneMotion mo = (StandaloneMotion) query.getSingleResult();
		
		return mo;
		
	}
	//==================portlet proceedings webservice method===============
	public List<StandaloneMotion> findByDeviceAndStatus(final DeviceType deviceType, final Status status){
				
		String  strQuery = "SELECT q FROM StandaloneMotion q" +
					" WHERE q.type.id=:deviceTypeId" +
					" AND q.status.id=:statusId";
		TypedQuery<StandaloneMotion> jpQuery = this.em().createQuery(strQuery, StandaloneMotion.class);
		jpQuery.setParameter("deviceTypeId", deviceType.getId());
		jpQuery.setParameter("statusId", status.getId());
		List<StandaloneMotion> qL = jpQuery.getResultList();
		
		return ((qL == null)? new ArrayList<StandaloneMotion>(): qL);
	}
	//======================================================================

	public StandaloneMotionDraft findPreviousDraft(final Long id) {
		String query = "SELECT qd" +
				" FROM StandaloneMotion q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" ORDER BY qd.id DESC";
		TypedQuery<StandaloneMotionDraft> tQuery = this.em().createQuery(query, StandaloneMotionDraft.class);
		tQuery.setParameter("qid", id);
		tQuery.setMaxResults(1);
		StandaloneMotionDraft draft = tQuery.getSingleResult();
		return draft;
	}

	public StandaloneMotionDraft findSecondPreviousDraft(final Long id) {
		String query = "SELECT qd" +
				" FROM StandaloneMotion q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" ORDER BY qd.id DESC";
		try{
			TypedQuery<StandaloneMotionDraft> tQuery = this.em().createQuery(query, StandaloneMotionDraft.class);
			tQuery.setParameter("qid", id);
			tQuery.setFirstResult(1);
			tQuery.setMaxResults(1);
			StandaloneMotionDraft draft = tQuery.getSingleResult();
			return draft;
		}catch(Exception e){
			return null;
		}
		
		
	}
	
	public StandaloneMotionDraft findPutupDraft(final Long id, final String putupStatus, final String putupActorUsergroupName) {
		String query = "SELECT qd" +
				" FROM StandaloneMotion q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" AND qd.internalStatus.type LIKE :putupStatus" +
				" AND qd.editedAs=:usergroupName" +
				" ORDER BY qd.id DESC";
		TypedQuery<StandaloneMotionDraft> tQuery = this.em().createQuery(query, StandaloneMotionDraft.class);
		tQuery.setParameter("qid", id);
		tQuery.setParameter("putupStatus", "standalonemotion_recommend%");
		tQuery.setParameter("usergroupName", putupActorUsergroupName);
		tQuery.setMaxResults(1);
		StandaloneMotionDraft draft = tQuery.getSingleResult();
		return draft;
	}
	
	public boolean isAdmittedThroughNameClubbing(final StandaloneMotion mo) {
		boolean isAdmittedThroughNameClubbing = false;
		String query = "SELECT qd" +
				" FROM StandaloneMotion q join q.drafts qd" +
				" WHERE q.id=:qid" +
				" AND q.internalStatus.type='" + ApplicationConstants.STANDALONE_FINAL_ADMISSION + "'" +
				" AND qd.internalStatus.type='" + ApplicationConstants.STANDALONE_RECOMMEND_NAMECLUBBING + "'" +
				" ORDER BY qd.id DESC";
		TypedQuery<StandaloneMotionDraft> tQuery = this.em().createQuery(query, StandaloneMotionDraft.class);
		tQuery.setParameter("qid", mo.getId());		
		List<StandaloneMotionDraft> drafts = tQuery.getResultList();
		if(drafts!=null && !drafts.isEmpty()) {
			isAdmittedThroughNameClubbing = true;
		}		
		return isAdmittedThroughNameClubbing;
	}
	
	public MemberMinister findMemberMinisterIfExists(StandaloneMotion mo) throws ELSException {
		MemberMinister  memberMinister = null;
		Session session = mo.getSession();
		if(session==null) {
			logger.error("This motion has no session.");
			throw new ELSException("motion_session_null", "This StandaloneMotion has no session.");
		}
		try{			
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.house h JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					" AND mi.id=:ministryId AND " +
					"(mm.ministryFromDate <=:motionSubmissionDate AND (mm.ministryToDate >:motionSubmissionDate  OR mm.ministryToDate IS NULL)) AND " +
					"mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", mo.getLocale());
			//query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("ministryId", mo.getMinistry().getId());
			query.setParameter("motionSubmissionDate", mo.getSubmissionDate());
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
			StringBuffer strQuery = new StringBuffer();
			strQuery.append("SELECT m FROM StandaloneMotion m " +
					" WHERE m.session.id=:sessionId" +
					" AND m.number=:number" +
					" AND m.locale=:locale" +
					" AND m.type.id=:deviceTypeId"); 
			
			TypedQuery<StandaloneMotion> query = this.em().createQuery(strQuery.toString(), StandaloneMotion.class);	
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			query.setParameter("deviceTypeId", deviceType.getId());
			
			StandaloneMotion mo = query.getSingleResult();
			if(mo != null){
				return true;
			}else{
				return false;
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
	public StandaloneMotion findExisting(Integer number, DeviceType deviceType, Session session, String locale) {
		try{
			StringBuffer strQuery = new StringBuffer();
			strQuery.append("SELECT m FROM StandaloneMotion m " +
					" WHERE m.session.id=:sessionId" +
					" AND m.number=:number" +
					" AND m.locale=:locale" +
					" AND m.type.id=:deviceTypeId"); 
			
			TypedQuery<StandaloneMotion> query = this.em().createQuery(strQuery.toString(), StandaloneMotion.class);	
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			query.setParameter("deviceTypeId", deviceType.getId());
			
			StandaloneMotion mo = query.getSingleResult();
			if(mo != null){
				return mo;
			}else{
				return null;
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
				
	public List<StandaloneMotion> findBySessionNumber(final Session session, final Integer number, final String locale){
		StringBuffer query = new StringBuffer("SELECT q FROM StandaloneMotion q WHERE q.session=:session AND q.number=:number AND q.locale=:locale");
		
		TypedQuery<StandaloneMotion> tQuery = this.em().createQuery(query.toString(), StandaloneMotion.class);
		tQuery.setParameter("session", session);
		tQuery.setParameter("number", number);
		tQuery.setParameter("locale", locale);
		return tQuery.getResultList();
	}
	
	
	@SuppressWarnings("rawtypes")
	public StandaloneMotionDraft getLatestStandaloneMotionDraftOfUser(final Long questionId, 
			final String username) throws ELSException {
		StandaloneMotionDraft questionDraft = null;
		try{
			org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.STANDALONE_GET_LATEST_STANDALONEMOTIONDRAFT_OF_USER, "");
			String strquery = nativeQuery.getQuery();
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("questionId",questionId);
			query.setParameter("username",username);  
			List result =query.getResultList();
			for(Object i : result) {
				Long draftId = Long.parseLong(i.toString());
				questionDraft = StandaloneMotionDraft.findById(StandaloneMotionDraft.class, draftId);			
				break;
			}
			return questionDraft;	
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("StandaloneMotionRepository_StandaloneMotionnDraft_getLatestStandaloneMotionnDraftOfUser", "Cannot get the StandaloneMotionnDraft");
			throw elsException;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByStandaloneMotionNumber(final StandaloneMotion motion, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM StandaloneMotion q JOIN q.clubbedEntities m" +
				" WHERE q.id=:motionId ORDER BY m.question.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}
	
	public int updateUnBallot(final Member member, final Session session,
			final DeviceType deviceType, final Status internalStatus,
			final Date discussionDate) {
		StringBuffer strQuery = new StringBuffer("UPDATE standalone_motions SET ballotstatus_id=NULL,discussion_date=NULL WHERE " + 
								" member_id=:memberId " +
								" AND devicetype_id=:deviceTypeId " +
								" AND session_id=:sessionId " + 
								" AND internalstatus_id=:statusId " +
								" AND discussion_date=:discussionDate");
		Query query = this.em().createNativeQuery(strQuery.toString(), StandaloneMotion.class);
		query.setParameter("memberId", member.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("statusId", internalStatus.getId());
		query.setParameter("discussionDate", discussionDate);		
		return query.executeUpdate();
	}
	
	public int findHighestFileNo(final Session session,final DeviceType motionType,
			final String locale) {
		String strQuery="SELECT m FROM StandaloneMotion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:motionTypeId AND m.locale=:locale AND m.file IS NOT NULL" +
				" ORDER BY m.file";
		
		TypedQuery<StandaloneMotion> query=this.em().createQuery(strQuery, StandaloneMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("motionTypeId",motionType.getId());
		query.setParameter("locale",locale);
		List<StandaloneMotion> motions= query.getResultList();
		if(motions==null){
			return 0;
		}else if(motions.isEmpty()){
			return 0;
		}else{
			 return motions.get(0).getFile();
		}
	}

	public List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY sm.number ASC, s.start_date DESC, dt.id ASC";
		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT sm.id as id,sm.number as number,"
				+"  sm.subject as subject,"
				+"  sm.revised_subject as revisedSubject,"
				+"  sm.brief_explanation as briefExplanation,"
				+"  sm.revised_brief_explanation as revisedBriefExplanation,"
				+"  sm.reason as reason,"
				+"  sm.revised_reason as revisedReason,"
				+"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"
				+"  sety.session_type as sessionType,"
				+"  mi.name as ministry,"
				+"  sd.name as subdepartment,st.type as statustype," 
				+"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"
				+"  sm.discussion_date as discussionDate,"
				+"  sm.localized_actor_name as actor" 
				+"  FROM standalone_motions as sm "
				+"  LEFT JOIN housetypes as ht ON(sm.housetype_id=ht.id) "
				+"  LEFT JOIN sessions as s ON(sm.session_id=s.id) "
				+"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "
				+"  LEFT JOIN status as st ON(sm.recommendationstatus_id=st.id) "
				+"  LEFT JOIN devicetypes as dt ON(sm.devicetype_id=dt.id) "
				+"  LEFT JOIN members as m ON(sm.member_id=m.id) "
				+"  LEFT JOIN titles as t ON(m.title_id=t.id) "
				+"  LEFT JOIN ministries as mi ON(sm.ministry_id=mi.id) "
				+"  LEFT JOIN subdepartments as sd ON(sm.subdepartment_id=sd.id) "
				+"  WHERE sm.locale='"+locale+"'"
				+"  AND st.type NOT IN('standalonemotion_incomplete','standalonemotion_complete')"
				+"  AND sm.number iS NOT NULL";
		
		StringBuffer filter = new StringBuffer("");
		filter.append(addStandaloneMotionFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "STANDALONEMOTION_SEARCH_USE_CURRENT_SESSION", "");
			if(csptUseCurrentSession != null && csptUseCurrentSession.getValue() != null 
					&& !csptUseCurrentSession.getValue().isEmpty() && csptUseCurrentSession.getValue().equalsIgnoreCase("yes")){
				String[] strSession = requestMap.get("session");
				if(strSession != null && strSession[0] != null && !strSession[0].isEmpty()){
					filter.append(" AND s.id=" + strSession[0]);
				}
			}
		}
		/**** full text query ****/
		String searchQuery=null;
		String query = null;
		if(requestMap.get("number") != null){
			if(!filter.toString().isEmpty()){
				query = selectQuery+filter+orderByQuery;
			}
		}else{
			if(!param.contains("+")&&!param.contains("-")){
				searchQuery=" AND (( match(sm.subject,sm.brief_explanation,sm.reason,sm.revised_subject,sm.revised_brief_explanation,sm.revised_reason) "+
						"against('"+param+"' in natural language mode)"+
						")||sm.subject LIKE '%"+param+"%'||sm.brief_explanation LIKE '%"+param+"%'||sm.reason LIKE '%"+param+
						"%'||sm.revised_subject LIKE '%"+param+"%'||sm.revised_brief_explanation LIKE '%"+param+"%'||sm.revised_reason LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(sm.subject,sm.brief_explanation,sm.reason,sm.revised_subject,sm.revised_brief_explanation,sm.revised_reason) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(sm.subject,sm.brief_explanation,sm.reason,sm.revised_subject,sm.revised_brief_explanation,sm.revised_reason) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(sm.subject,sm.brief_explanation,sm.reason,sm.revised_subject,sm.revised_brief_explanation,sm.revised_reason) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.briefExplanation,rs.revisedBriefExplanation, "+
				"rs.reason,rs.revisedReason,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.discussionDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> standaloneMotionSearchVOs=new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO standaloneMotionSearchVO=new SearchVO();
				if(o[0]!=null){
					standaloneMotionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					standaloneMotionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						standaloneMotionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							standaloneMotionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						standaloneMotionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						standaloneMotionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							standaloneMotionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						standaloneMotionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[7]!=null){
					if(!o[7].toString().isEmpty()){
						standaloneMotionSearchVO.setRevisedContent(higlightText(o[7].toString(),param));
					}else{
						if(o[6]!=null){
							standaloneMotionSearchVO.setRevisedContent(higlightText(o[6].toString(),param));
						}
					}
				}else{
					if(o[6]!=null){
						standaloneMotionSearchVO.setRevisedContent(higlightText(o[6].toString(),param));
					}
				}
				if(o[8]!=null){
					standaloneMotionSearchVO.setStatus(o[8].toString());
				}
				if(o[9]!=null){
					standaloneMotionSearchVO.setDeviceType(o[9].toString());
				}
				if(o[10]!=null){
					standaloneMotionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
				}
				if(o[11]!=null){
					standaloneMotionSearchVO.setSessionType(o[11].toString());
				}
				
				if(o[12]!=null){
					standaloneMotionSearchVO.setMinistry(o[12].toString());
				}
				
				if(o[13]!=null){
					standaloneMotionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					standaloneMotionSearchVO.setStatusType(o[14].toString());
				}
				if(o[15]!=null){
					standaloneMotionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					standaloneMotionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[16].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[17]!=null){
					standaloneMotionSearchVO.setActor(o[17].toString());
				}
				standaloneMotionSearchVOs.add(standaloneMotionSearchVO);
			}
		}
		return standaloneMotionSearchVOs;
	}
	
	private String addStandaloneMotionFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND sm.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND sm.member_id=" + member);
			}
		}
		if(requestMap.get("deviceType")!=null){
			String deviceType=requestMap.get("deviceType")[0];
			if((!deviceType.isEmpty())&&(!deviceType.equals("-"))){
				buffer.append(" AND dt.id="+deviceType);
			}
		}
		if(requestMap.get("houseType")!=null){
			String houseType=requestMap.get("houseType")[0];
			if((!houseType.isEmpty())&&(!houseType.equals("-"))){
				buffer.append(" AND ht.type='"+houseType+"'");
			}
		}
		if(requestMap.get("sessionYear")!=null){
			String sessionYear=requestMap.get("sessionYear")[0];
			if((!sessionYear.isEmpty())&&(!sessionYear.equals("-"))){
				buffer.append(" AND s.session_year="+sessionYear);
			}
		}
		if(requestMap.get("sessionType")!=null){
			String sessionType=requestMap.get("sessionType")[0];
			if((!sessionType.isEmpty())&&(!sessionType.equals("-"))){
				buffer.append(" AND sety.id="+sessionType);
			}
		}
		if(requestMap.get("ministry")!=null){
			String ministry=requestMap.get("ministry")[0];
			if((!ministry.isEmpty())&&(!ministry.equals("-"))){
				buffer.append(" AND mi.id="+ministry);
			}
		}
		if(requestMap.get("subDepartment")!=null){
			String subDepartment=requestMap.get("subDepartment")[0];
			if((!subDepartment.isEmpty())&&(!subDepartment.equals("-"))){
				buffer.append(" AND sd.id="+subDepartment);
			}
		}	
		if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_PROCESSED_BALLOTED+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String higlightText(final String textToHiglight,final String pattern) {

		String highlightedText=textToHiglight;
		String replaceMentText="<span class='highlightedSearchPattern'>";
		String replaceMentTextEnd="</span>";
		if((!pattern.contains("+"))&&(!pattern.contains("-"))){
			String[] temp=pattern.trim().split(" ");
			for(String j:temp){
				if(!j.isEmpty()){
					if(!highlightedText.contains(replaceMentText+j.trim()+replaceMentTextEnd)){
						highlightedText=highlightedText.replaceAll(j.trim(),replaceMentText+j.trim()+replaceMentTextEnd);
					}
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
	
	

	public List<StandaloneMotion> findAllAdmittedUndisccussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale) {
		
		List<StandaloneMotion> motions = new ArrayList<StandaloneMotion>();
		
		try {
			Status recommendDiscussed = Status.findByType(ApplicationConstants.STANDALONEED, locale);
			
			
			String strQuery="SELECT m FROM StandaloneMotion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.internalStatus=:internalStatus " + 
					"  AND m.ballotStatus.type =:type " + 
					" AND m.parent is NULL "+
					/*" AND (m.discussionStatus!=:recommendationStatusDiscussed AND m.discussionStatus!=:recommendationStatusUndiscussed)" +*/
					" ORDER BY m.number "+ ApplicationConstants.ASC;
			TypedQuery<StandaloneMotion> query = this.em().createQuery(strQuery, StandaloneMotion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("internalStatus", status);
			query.setParameter("type", "standalonemotion_processed_balloted");
			/*query.setParameter("recommendationStatusDiscussed", recommendDiscussed);
			query.setParameter("recommendationStatusUndiscussed", recommendUndiscussed);*/
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
}
