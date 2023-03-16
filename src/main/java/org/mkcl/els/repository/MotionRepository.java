package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.hibernate.mapping.Array;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class MotionRepository extends BaseRepository<Motion, Serializable>{

	public List<ClubbedEntity> findClubbedEntitiesByPosition(final Motion motion) {
		String strQuery = "SELECT ce FROM Motion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:motionId ORDER BY ce.position " + ApplicationConstants.ASC;
		TypedQuery<ClubbedEntity> query=this.em().createQuery(strQuery, ClubbedEntity.class);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}

	
	public Integer assignMotionNo(final HouseType houseType,final Session session,
			final DeviceType type,final String locale) {
		String strMotionType = type.getType();
		String strQuery = "SELECT m FROM Motion m JOIN m.session s JOIN m.type dt" +
		" WHERE dt.type =:motionType AND s.id=:sessionId"+
		" ORDER BY m.number " +ApplicationConstants.DESC;	
		try {
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("motionType",strMotionType);
			query.setParameter("sessionId",session.getId());
			List<Motion> motions = query.setFirstResult(0).
			setMaxResults(1).getResultList();
			if(motions == null) {
				return 0;
			}
			else if(motions.isEmpty()) {
				return 0;
			}
			else {
				if(motions.get(0).getNumber() == null) {
					return 0;
				}else{
					return motions.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int findReadyToSubmitCount(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {
		Integer draftsCount = 0;
		
		String queryString = "SELECT COUNT(DISTINCT q.id) FROM motions q" +
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
	
	public List<Motion> findReadyToSubmitMotions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final String locale) {		
		return this.findReadyToSubmitMotions(session, primaryMember, deviceType, -1, locale);
	}
	
	@SuppressWarnings("unchecked")
	public List<Motion> findReadyToSubmitMotions(final Session session,
			final Member primaryMember,
			final DeviceType deviceType,
			final Integer itemsCount,
			final String locale) {		
		String queryString = "SELECT DISTINCT q.* FROM motions q" +
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
		Query query = this.em().createNativeQuery(queryString, Motion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("memberId", primaryMember.getId());
		query.setParameter("deviceTypeId", deviceType.getId());		
		query.setParameter("locale", locale);
		if(itemsCount!=null && itemsCount.intValue() != -1) {
			query.setParameter("itemsCount", itemsCount.intValue());
		}
		return query.getResultList();
	}

	public Integer findMaxPostBallotNo(final HouseType houseType,
			final Session session,
			final DeviceType type,
			final String locale) {
		String strMotionType = type.getType();
		String strQuery = "SELECT m FROM Motion m JOIN m.session s JOIN m.type dt" +
		" WHERE dt.type =:motionType AND s.id=:sessionId"+
		" ORDER BY m.postBallotNumber " +ApplicationConstants.DESC;	
		try {
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("motionType",strMotionType);
			query.setParameter("sessionId",session.getId());
			List<Motion> motions = query.setFirstResult(0).
			setMaxResults(1).getResultList();
			if(motions == null) {
				return 0;
			}
			else if(motions.isEmpty()) {
				return 0;
			}
			else {
				if(motions.get(0).getPostBallotNumber() == null) {
					return 0;
				}else{
					return motions.get(0).getPostBallotNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long motionId,final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("motionId",motionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> motionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++) {
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
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
			/**** Revision Control(Details and Subject) ****/
			if(o1!=null){
				if(!o[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String motionDetails=d.diff_prettyHtml(diff);
					if(motionDetails.contains("&lt;")){
						motionDetails=motionDetails.replaceAll("&lt;", "<");
					}
					if(motionDetails.contains("&gt;")){
						motionDetails=motionDetails.replaceAll("&gt;", ">");
					}
					if(motionDetails.contains("&amp;nbsp;")){
						motionDetails=motionDetails.replaceAll("&amp;nbsp;"," ");
					}
					motionRevisionVO.setDetails(motionDetails);
				}else{
					motionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				motionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[5].toString(), o[5].toString());
					String motionSubject=d.diff_prettyHtml(diff);
					if(motionSubject.contains("&lt;")){
						motionSubject=motionSubject.replaceAll("&lt;", "<");
					}
					if(motionSubject.contains("&gt;")){
						motionSubject=motionSubject.replaceAll("&gt;", ">");
					}
					if(motionSubject.contains("&amp;nbsp;")){
						motionSubject=motionSubject.replaceAll("&amp;nbsp;"," ");
					}
					motionRevisionVO.setSubject(motionSubject);
				}else{
					motionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				motionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				motionRevisionVO.setRemarks(o[6].toString());
			}	
			motionRevisionVOs.add(motionRevisionVO);
		}
		return motionRevisionVOs;
	}

	public List<Motion> findAllByMember(final Session session,final Member primaryMember,
			final DeviceType motionType,final Integer itemsCount,final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.MOTION_COMPLETE, locale);
			String strQuery="SELECT m FROM Motion m WHERE m.session=:session AND m.primaryMember=:primaryMember" +
					" AND m.type=:motionType AND m.locale=:locale AND m.status=:status ORDER BY m.id "+ ApplicationConstants.DESC;
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setMaxResults(itemsCount);
			query.setParameter("session", session);
			query.setParameter("primaryMember", primaryMember);
			query.setParameter("motionType", motionType);
			query.setParameter("locale",locale);
			query.setParameter("status",status);
			motions = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		
		return motions;
	}	
	
	public List<Motion> findAllByMemberBatchWise(final Session session,final Member primaryMember,
			final DeviceType motionType, 
			final Date startTime,
			final Date endTime,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			//Date startTime = session.getParameter("");
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.MOTION_COMPLETE, locale);
			String strQuery="SELECT m FROM Motion m WHERE m.session=:session AND m.primaryMember=:primaryMember" +
					" AND m.type=:motionType AND m.locale=:locale AND m.status.priority>=:priority" +
					" AND m.submissionDate>=:startTime" +
					" AND m.submissionDate<=:endTime" +
					" ORDER BY m.submissionDate "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("primaryMember", primaryMember);
			query.setParameter("motionType", motionType);
			query.setParameter("locale",locale);
			query.setParameter("startTime",startTime);			
			query.setParameter("endTime", endTime);
			query.setParameter("priority", status.getPriority());
			motions = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		
		return motions;
	}
	
	public List<Motion> findAllByBatch(final Session session,
			final DeviceType motionType, 
			final Date startTime,
			final Date endTime,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			//Date startTime = session.getParameter("");
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.MOTION_COMPLETE, locale);
			String strQuery="SELECT m FROM Motion m WHERE m.session=:session" +
					" AND m.type=:motionType AND m.locale=:locale AND m.status.priority>=:priority" +
					" AND m.submissionDate>=:startTime" +
					" AND m.submissionDate<=:endTime" +
					" ORDER BY m.submissionDate "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale",locale);
			query.setParameter("startTime",startTime);			
			query.setParameter("endTime", endTime);
			query.setParameter("priority",status.getPriority());
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllUndiscussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			Status undiscussed = Status.findByType(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED, locale);
			String strQuery="SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.status=:status" + 
					" AND m.discussionStatus=:undiscussed" +
					" ORDER BY m.submissionDate "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("undiscussed", undiscussed);
			query.setParameter("status", status);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllUndiscussedByMember(final Session session,
			final DeviceType motionType, 
			final Status status,
			final Member primaryMember,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			Status undiscussed = Status.findByType(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED, locale);
			String strQuery = "SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.status=:status" + 
					" AND m.discussionStatus=:undiscussed" +
					" AND m.primaryMember=:member" +
					" ORDER BY m.submissionDate "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("status", status);
			query.setParameter("undiscussed", undiscussed.getId());
			query.setParameter("member", primaryMember);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllAdmittedUndisccussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			Status recommendDiscussed = Status.findByType(ApplicationConstants.MOTION_PROCESSED_DISCUSSED, locale);
			Status recommendUndiscussed = Status.findByType(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED, locale);
			
			String strQuery="SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.internalStatus=:internalStatus" + 
					" AND m.parent is NULL "+
					/*" AND (m.discussionStatus!=:recommendationStatusDiscussed AND m.discussionStatus!=:recommendationStatusUndiscussed)" +*/
					" ORDER BY m.number "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("internalStatus", status);
			/*query.setParameter("recommendationStatusDiscussed", recommendDiscussed);
			query.setParameter("recommendationStatusUndiscussed", recommendUndiscussed);*/
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllByStatus(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			String strQuery="SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.status=:status" + 
					" ORDER BY m.number "+ ApplicationConstants.ASC + 
					", m.postBallotNumber " + ApplicationConstants.ASC +
					", m.submissionDate " + ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("status", status);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllByNumbersInSession(final Session session,
			final DeviceType motionType, 
			final String numbers,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		List<Integer> numbersList = new ArrayList<Integer>();
		if(numbers!=null && !numbers.isEmpty()) {
			for(String num : numbers.split(",")){
				numbersList.add(Integer.parseInt(num));
			}
		}
		
		try {
			String strQuery="SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.number IN (:numbersList)" +
					" AND m.locale=:locale" +
					" ORDER BY m.number "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("numbersList", numbersList);
			query.setParameter("locale", locale);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllForDiscussion(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			CustomParameter csptSelectableFor = CustomParameter.findByName(CustomParameter.class, "SELECTABLE_MOTION_FOR_DISCUSSION", "");
			List<String> statuses = new ArrayList<String>();
			if(csptSelectableFor != null && csptSelectableFor.getValue() != null && !csptSelectableFor.getValue().isEmpty()){
				for(String s : csptSelectableFor.getValue().split(",")){
					statuses.add(s);
				}
			}
			
			if(statuses.isEmpty()){
				statuses.add(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED);
			}
			String strQuery="SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.status=:status" + 
					" AND (m.discussionDate IS NULL OR (m.discussionDate IS NOT NULL AND m.discussionStatus.type IN (:recommendationStatus)))"+					
					" ORDER BY m.number "+ ApplicationConstants.ASC + 
					", m.postBallotNumber " + ApplicationConstants.ASC +
					", m.submissionDate " + ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("status", status);
			query.setParameter("recommendationStatus", statuses);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}
	
	public List<Motion> findAllByMember(final Session session,
			final DeviceType motionType, 
			final Status status,
			final Member primaryMember,
			final String locale) {
		
		List<Motion> motions = new ArrayList<Motion>();
		
		try {
			String strQuery = "SELECT m FROM Motion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.status=:status" + 
					" AND m.primaryMember=:member" +
					" ORDER BY m.submissionDate "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale", locale);
			query.setParameter("status", status);
			query.setParameter("member", primaryMember);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}

	
	public List<Motion> findAllByStatus(final Session session,final DeviceType motionType,
			final Status internalStatus,final Integer itemsCount,final String locale) {
		String strQuery="SELECT m FROM Motion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:motionTypeId AND m.locale=:locale AND m.internalStatus.id=:internalStatusId" +
				" AND m.workflowStarted=:workflowStarted AND m.parent IS NULL ORDER BY m.number";
		List<Motion> motions = new ArrayList<Motion>();
		
		try{
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("sessionId",session.getId());
			query.setParameter("motionTypeId",motionType.getId());
			query.setParameter("locale",locale);
			query.setParameter("internalStatusId",internalStatus.getId());
			query.setParameter("workflowStarted","NO");
			query.setMaxResults(itemsCount);
			
			motions = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return motions;
	}

	public Reference findCurrentFile(final Motion domain) {
		String strQuery="SELECT m FROM Motion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:motionTypeId AND m.locale=:locale AND m.fileSent=:fileSent" +
				" AND m.file IS NOT NULL ORDER BY m.file DESC,m.fileSent DESC";
		Reference reference = null;
		try{
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId",domain.getSession().getId());
		query.setParameter("motionTypeId",domain.getType().getId());
		query.setParameter("locale",domain.getLocale());
		query.setParameter("fileSent",false);
		List<Motion> motions=query.getResultList();
		Motion motion=null;
		if(motions!=null&&!motions.isEmpty()){
			motion=motions.get(0);
		}
		if(motion==null){
			reference = new Reference(String.valueOf(1),String.valueOf(1));
		}else if(motion.getFile()==null){
			reference = new Reference(String.valueOf(1),String.valueOf(1));
		}else if(motion.getFile()!=null&&motion.getFileIndex()==null){
			reference = new Reference(String.valueOf(motion.getFile()),String.valueOf(1));
		}else{
			CustomParameter customParameter=CustomParameter.
			findByName(CustomParameter.class,"FILE_"+domain.getType().getType().toUpperCase(), "");
			int fileSize=Integer.parseInt(customParameter.getValue());
			if(motion.getFileIndex()==fileSize){
				reference = new Reference(String.valueOf(motion.getFile()+1),String.valueOf(1));
			}else{
				reference = new Reference(String.valueOf(motion.getFile()),String.valueOf(motion.getFileIndex()+1));
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return reference;
	}

	public List<Motion> findAllByFile(final Session session,final DeviceType motionType,
			final Integer file,final String locale) {
		String strQuery="SELECT m FROM Motion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:motionTypeId AND m.locale=:locale AND m.file=:file" +
				" ORDER BY m.fileIndex";
		List<Motion> motions = new ArrayList<Motion>();
		try{
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("motionTypeId",motionType.getId());
			query.setParameter("locale",locale);
			query.setParameter("file",file);
			motions = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return motions;
	}

	// change to singleResult if possible
	public int findHighestFileNo(final Session session,final DeviceType motionType,
			final String locale) {
		String strQuery="SELECT m FROM Motion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:motionTypeId AND m.locale=:locale AND m.file IS NOT NULL" +
				" ORDER BY m.file";
		
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", session.getId());
		query.setParameter("motionTypeId",motionType.getId());
		query.setParameter("locale",locale);
		List<Motion> motions= query.getResultList();
		if(motions==null){
			return 0;
		}else if(motions.isEmpty()){
			return 0;
		}else{
			 return motions.get(0).getFile();
		}
	}

	public Motion getMotion(Long sessionId, Long deviceTypeId, Integer dNumber,
			String locale) {
		String strQuery="SELECT m FROM Motion m WHERE" +
				" m.session.id=:sessionId" +
				" AND m.type.id=:deviceTypeId" +
				" AND m.number=:dNumber" +
				" AND m.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("deviceTypeId", deviceTypeId);
		query.setParameter("dNumber", dNumber);
		query.setParameter("locale", locale);
		Motion motion=(Motion) query.getSingleResult();
		return motion;
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final Motion motion, final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM Motion mo JOIN mo.clubbedEntities m" +
				" WHERE mo.id=:motionId ORDER BY m.motion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}
	
	public Boolean isExist(Integer number, DeviceType deviceType, Session session, String locale) {
		try{
			StringBuffer strQuery=new StringBuffer();
			strQuery.append("SELECT m FROM Motion m " +
					" WHERE m.session.id=:sessionId" +
					" AND m.number=:number" +
					" AND m.locale=:locale");
			
			Query query=this.em().createQuery(strQuery.toString());
			query=this.em().createQuery(strQuery.toString());
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			
			
			return ((query.getSingleResult() != null)? new Boolean(true) : new Boolean(false)) ;
			
			
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
	public List<ClubbedEntity> findClubbedEntitiesByAnsweringDateMotionNumber(final Motion motion, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM Motion q JOIN q.clubbedEntities m" +
				" WHERE q.id=:motionId ORDER BY m.motion.answeringDate,m.motion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}


	public List<Motion> findAllCompleteByCreator(final Session session,
			final String username, final DeviceType motionType, 
			final Integer itemsCount, final String strLocale) {
		List<Motion> motions = new ArrayList<Motion>();
		Status status = Status.findByType(ApplicationConstants.MOTION_COMPLETE, strLocale);
		try {
			String strQuery = "SELECT m FROM Motion m" +
					" WHERE m.session.id=:sessionId" +
					" AND m.type.id=:motionTypeId" +
					" AND m.locale=:locale" +
					" AND m.status.id=:statusId" +
					" AND m.createdBy=:createdBy" +
					" ORDER BY m.submissionPriority "+ ApplicationConstants.ASC +
					" , m.id "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query = this.em().createQuery(strQuery, Motion.class);
			query.setMaxResults(itemsCount);
			query.setParameter("sessionId", session.getId());
			query.setParameter("motionTypeId", motionType.getId());
			query.setParameter("locale", strLocale);
			query.setParameter("statusId", status.getId());
			query.setParameter("createdBy", username);
			motions = query.getResultList();
		} catch (Exception e) {
			logger.error("error", e);
		} 
		
		return motions;
	}


	/**
	 * Motion Search Based on parameters passed
	 * **/
	public List<SearchVO> fullTextSearchForSearching(final String param,
			final int start,final int noOfRecords, 
			final String locale,final Map<String, String[]> requestMap) {
		
		String orderByQuery=" ORDER BY mo.number ASC, s.start_date DESC, dt.id ASC";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT mo.id as id,mo.number as number,"
				+"  mo.subject as subject,"
				+"  mo.revised_subject as revisedSubject,"
				+"  mo.details as motionDetails,"
				+"  mo.revised_details as revisedMotionDetails,"
				+"  ist.name as internalStatus,st.name as status,dt.name as deviceType,s.session_year as sessionYear,"
				+"  sety.session_type as sessionType,"
				+"  mi.name as ministry,"
				+"  sd.name as subdepartment,st.type as statustype," 
				+"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"
				+"  (CASE WHEN mo.discussionstatus_id IS NOT NULL AND dst.type='"+ApplicationConstants.MOTION_PROCESSED_DISCUSSED+"' AND mo.discussion_date IS NOT NULL THEN mo.discussion_date ELSE '' END) as discussionDate,"
				+"  mo.localized_actor_name as actor" 
				+"  FROM motions as mo "
				+"  LEFT JOIN housetypes as ht ON(mo.housetype_id=ht.id) "
				+"  LEFT JOIN sessions as s ON(mo.session_id=s.id) "
				+"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "
				+"  LEFT JOIN status as st ON(mo.recommendationstatus_id=st.id) "
				+"  LEFT JOIN status as ist ON(mo.internalstatus_id=ist.id) " 
				+"  LEFT JOIN devicetypes as dt ON(mo.devicetype_id=dt.id) "
				+"  LEFT JOIN members as m ON(mo.member_id=m.id) "
				+"  LEFT JOIN titles as t ON(m.title_id=t.id) "
				+"  LEFT JOIN ministries as mi ON(mo.ministry_id=mi.id) "
				+"  LEFT JOIN subdepartments as sd ON(mo.subdepartment_id=sd.id) "
				+"  LEFT JOIN status as dst ON(mo.discussionstatus_id=dst.id) "
				+"  WHERE mo.locale='"+locale+"'"
				+"  AND st.type NOT IN('motion_incomplete','motion_complete')";
		
		StringBuffer filter = new StringBuffer("");
		filter.append(addMotionFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "MOTION_SEARCH_USE_CURRENT_SESSION", "");
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
				searchQuery=" AND (( match(mo.subject,mo.details,mo.revised_subject,mo.revised_details) "+
						"against('"+param+"' in natural language mode)"+
						")||mo.subject LIKE '%"+param+"%'||mo.details LIKE '%"+param+"%'||mo.revised_subject LIKE '%"+param+"%'||mo.revised_details LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(mo.subject,mo.details,mo.revised_subject,mo.revised_details) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(mo.subject,mo.details,mo.revised_subject,mo.revised_details) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(mo.subject,mo.details,mo.revised_subject,mo.revised_details) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.motionDetails, "+
				" rs.revisedMotionDetails,rs.internalStatus,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.discussionDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> motionSearchVOs=new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO motionSearchVO=new SearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setInternalStatus(o[6].toString());
				}  				
				if(o[7]!=null){
					motionSearchVO.setStatus(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setDeviceType(o[8].toString());
				}
				if(o[9]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[9].toString())));
				}
				if(o[10]!=null){
					motionSearchVO.setSessionType(o[10].toString());
				}
				
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				
				if(o[12]!=null){
					motionSearchVO.setSubDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setStatusType(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[14].toString());
				}
				if(o[15]!=null && !o[14].toString().isEmpty()){
					motionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[15].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[16]!=null){
					motionSearchVO.setActor(o[16].toString());
				}
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;

	}
	
	private String addMotionFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND mo.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND mo.member_id=" + member);
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_PROCESSED_DISCUSSED+"')");
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
	
	public int updateTimeoutSupportingMemberTasksForDevice(final Long deviceId, final Date submissionDate) {
		if(deviceId!=null && submissionDate!=null) {
			StringBuffer strQuery = new StringBuffer("UPDATE workflow_details wd " + 
					" SET wd.completion_time=:completionTime, wd.status='TIMEOUT' " +
					" WHERE wd.device_id=:deviceId " + 
					" AND wd.workflow_sub_type = 'request_to_supporting_member' " +
					" AND wd.status='PENDING'");
			Query query = this.em().createNativeQuery(strQuery.toString());
			query.setParameter("deviceId", "'"+ deviceId +"'");	
			String submissionDateStr = FormaterUtil.formatDateToString(submissionDate, ApplicationConstants.DB_DATETIME__24HOURS_FORMAT);
			Date completionTime = FormaterUtil.formatStringToDate(submissionDateStr, ApplicationConstants.DB_DATETIME__24HOURS_FORMAT);
			query.setParameter("completionTime", completionTime);
			return query.executeUpdate();
		} else {
			return 0;
		}		
	}
}
