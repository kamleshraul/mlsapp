package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.hibernate.mapping.Array;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class MotionRepository extends BaseRepository<Motion, Serializable>{

	public List<ClubbedEntity> findClubbedEntitiesByPosition(final Motion motion) {
		return null;
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
					" AND m.type=:motionType AND m.locale=:locale AND m.status=:status" + 
					" AND m.submissionDate" +
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
			query.setParameter("status",status);
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
			String strQuery="SELECT m FROM Motion m WHERE m.session=:session AND m.primaryMember=:primaryMember" +
					" AND m.type=:motionType AND m.locale=:locale AND m.status=:status" + 
					" AND m.submissionDate" +
					" AND m.submissionDate>=:startTime" +
					" AND m.submissionDate<=:endTime" +
					" ORDER BY m.submissionDate "+ ApplicationConstants.ASC;
			TypedQuery<Motion> query=this.em().createQuery(strQuery, Motion.class);
			query.setParameter("session", session);
			query.setParameter("motionType", motionType);
			query.setParameter("locale",locale);
			query.setParameter("startTime",startTime);			
			query.setParameter("endTime", endTime);
			query.setParameter("status",status);
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
}
