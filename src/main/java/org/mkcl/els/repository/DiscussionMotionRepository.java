package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.DiscussionMotionDraft;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class DiscussionMotionRepository extends BaseRepository<DiscussionMotion, Serializable>{

	public Boolean isExist(final Integer number, final DeviceType deviceType,
			final Session session, final String locale) {
		try{
			StringBuffer strQuery=new StringBuffer();
			strQuery.append("SELECT cm FROM DiscussionMotion cm " +
					" WHERE cm.session.id=:sessionId" +
					" AND cm.number=:number" +
					" AND cm.type.id=:deviceTypeId" +
					" AND cm.locale=:locale");
			Query query = this.em().createQuery(strQuery.toString());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			
			DiscussionMotion motion = (DiscussionMotion) query.getSingleResult();
			if(motion!=null){
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
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final DiscussionMotion motion, final String sortOrder) {
		String strQuery = "SELECT ce FROM DiscussionMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:motionId ORDER BY ce.position " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}
	
	// change to singleResult if possible
	public int findHighestFileNo(final Session session,
			final DeviceType discussionMotionType,
			final String locale) {
		String strQuery="SELECT m FROM DiscussionMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.type.id=:discussionMotionTypeId"
				+ " AND m.locale=:locale AND m.file IS NOT NULL" 
				+ " ORDER BY m.file";
		
		TypedQuery<DiscussionMotion> query = this.em().createQuery(strQuery, DiscussionMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("discussionMotionTypeId",discussionMotionType.getId());
		query.setParameter("locale",locale);
		
		List<DiscussionMotion> motions= query.getResultList();
		if(motions==null){
			return 0;
		}else if(motions.isEmpty()){
			return 0;
		}else{
			 return motions.get(0).getFile();
		}
	}
	
	
	public boolean isAdmittedThroughClubbing(final DiscussionMotion discussionmotion) {
		boolean isAdmittedThroughClubbing = false;
		org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(Query.class, "keyField", "IS_DISCUSSIONMOTION_ADMITTED_THROUGH_CLUBBING", discussionmotion.getLocale());
		Query tQuery = this.em().createNativeQuery(query.getQuery(), DiscussionMotionDraft.class);
		tQuery.setParameter("discussionmotionId", discussionmotion.getId());
		@SuppressWarnings("unchecked")
		List<QuestionDraft> drafts = tQuery.getResultList();
		if(drafts!=null && !drafts.isEmpty()) {
			isAdmittedThroughClubbing = true;
		}		
		return isAdmittedThroughClubbing;
	}

	public Integer assignDiscussionMotionNo(HouseType houseType,
			Session session, DeviceType type, String locale) {
		String strDiscussionMotionType = type.getType();
		String strQuery = "SELECT m FROM DiscussionMotion m JOIN m.session s JOIN m.type dt" +
		" WHERE dt.type =:discussionMotionType AND s.id=:sessionId"+
		" ORDER BY m.number " +ApplicationConstants.DESC;	
		try {
			TypedQuery<DiscussionMotion> query=this.em().createQuery(strQuery, DiscussionMotion.class);
			query.setParameter("discussionMotionType",strDiscussionMotionType);
			query.setParameter("sessionId",session.getId());
			List<DiscussionMotion> motions = query.setFirstResult(0).
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

	public Reference findCurrentFile(DiscussionMotion domain) {
		String strQuery="SELECT m FROM DiscussionMotion m WHERE m.session.id=:sessionId" +
				" AND m.type.id=:discussionMotionTypeId AND m.locale=:locale AND m.fileSent=:fileSent" +
				" AND m.file IS NOT NULL ORDER BY m.file DESC,m.fileSent DESC";
		Reference reference = null;
		try{
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId",domain.getSession().getId());
		query.setParameter("discussionMotionTypeId",domain.getType().getId());
		query.setParameter("locale",domain.getLocale());
		query.setParameter("fileSent",false);
		List<DiscussionMotion> motions=query.getResultList();
		DiscussionMotion motion=null;
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

	public List<RevisionHistoryVO> getRevisions(Long discussionMotionId,
			String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.DISCUSSIONMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("discussionMotionId",discussionMotionId);
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
		
	public List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final DiscussionMotion motion, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM DiscussionMotion q JOIN q.clubbedEntities m" +
				" WHERE q.id=:motionId ORDER BY m.discussionMotion.discussionDate,m.discussionMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}
	
	public List<DiscussionMotion> findAllByFile(final Session session,
			final DeviceType discussionMotionType,
			final Integer file,
			final String locale) {
		String strQuery="SELECT m FROM DiscussionMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.type.id=:discussionMotionTypeId"
				+ " AND m.locale=:locale"
				+ " AND m.file=:file" 
				+ " ORDER BY m.fileIndex";
		List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
		try{
			TypedQuery<DiscussionMotion> query=this.em().createQuery(strQuery, DiscussionMotion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("discussionMotionTypeId", discussionMotionType.getId());
			query.setParameter("locale",locale);
			query.setParameter("file",file);
			motions = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return motions;
	}	
	
	public List<DiscussionMotion> findAllByStatus(final Session session,
			final DeviceType discussionMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		String strQuery="SELECT cm FROM DiscussionMotion cm"
				+ " WHERE cm.session.id=:sessionId" 
				+ " AND cm.type.id=:discussionMotionTypeId"
				+ " AND cm.locale=:locale"
				+ " AND cm.internalStatus.id=:internalStatusId" 
				+ " AND cm.workflowStarted=:workflowStarted"
				+ " AND cm.parent IS NULL ORDER BY cm.number";
		List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
		
		try{
			TypedQuery<DiscussionMotion> query=this.em().createQuery(strQuery, DiscussionMotion.class);
			query.setParameter("sessionId",session.getId());
			query.setParameter("discussionMotionTypeId",discussionMotionType.getId());
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
	
	public List<DiscussionMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType discussionMotionType,
			final Integer itemsCount,
			final String locale) {
		
		List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
		
		try {
			Status status = Status.findByFieldName(Status.class,"type",ApplicationConstants.DISCUSSIONMOTION_COMPLETE, locale);
			String strQuery = "SELECT cm FROM DiscussionMotion cm"
					+ " WHERE cm.session=:session"
					+ " AND cm.primaryMember=:primaryMember" 
					+ " AND cm.type=:discussionMotionType"
					+ " AND cm.locale=:locale"
					+ " AND cm.status=:status ORDER BY cm.id "+ ApplicationConstants.DESC;
			TypedQuery<DiscussionMotion> query = this.em().createQuery(strQuery, DiscussionMotion.class);
			query.setMaxResults(itemsCount);
			query.setParameter("session", session);
			query.setParameter("primaryMember", primaryMember);
			query.setParameter("discussionMotionType", discussionMotionType);
			query.setParameter("locale",locale);
			query.setParameter("status",status);
			motions = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		
		return motions;
	}	
	
	public List<DiscussionMotion> findAllEnteredBy(final Session session,
			final String user,
			final DeviceType discussionMotionType,
			final Integer itemsCount,
			final String locale) {
		
		List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
		
		try {
			Status status = Status.findByFieldName(Status.class,"type",ApplicationConstants.DISCUSSIONMOTION_COMPLETE, locale);
			String strQuery = "SELECT cm FROM DiscussionMotion cm"
					+ " WHERE cm.session=:session"
					+ " AND cm.dataEnteredBy=:enteredBy" 
					+ " AND cm.type=:discussionMotionType"
					+ " AND cm.locale=:locale"
					+ " AND cm.status=:status ORDER BY cm.id "+ ApplicationConstants.DESC;
			TypedQuery<DiscussionMotion> query = this.em().createQuery(strQuery, DiscussionMotion.class);
			query.setMaxResults(itemsCount);
			query.setParameter("session", session);
			query.setParameter("enteredBy", user);
			query.setParameter("discussionMotionType", discussionMotionType);
			query.setParameter("locale",locale);
			query.setParameter("status",status);
			motions = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		
		return motions;
	}
	public MemberMinister findMemberMinisterIfExists(final DiscussionMotion discussionmotion) throws ELSException {
		MemberMinister  memberMinister = null;
		Session session = discussionmotion.getSession();
		if(session==null) {
			logger.error("This discussionmotion has no session.");
			throw new ELSException("discussionmotion_session_null", "This discussionmotion has no session.");
		}
		try{			
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					//" AND mi.id=:ministryId " +
					" AND mm.ministryFromDate<=:discussionmotionDate AND (mm.ministryToDate>=:discussionmotionDate  OR mm.ministryToDate IS NULL) " +
//					" AND mm.ministryFromDate<=:discussionmotionSubmissionDate AND (mm.ministryToDate>=:discussionmotionSubmissionDate  OR mm.ministryToDate IS NULL) " +
					" AND mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", discussionmotion.getLocale());
			//query.setParameter("houseId", session.getHouse().getId());
			//query.setParameter("ministryId", discussionmotion.getMinistry().getId());
		
				query.setParameter("discussionmotion", discussionmotion.getSubmissionDate());
						
			//query.setParameter("discussionmotionSubmissionDate", discussionmotion.getSubmissionDate());
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
	
	public MemberMinister findMemberMinisterIfExists(final DiscussionMotion discussionmotion, final Ministry ministry) throws ELSException {
		MemberMinister  memberMinister = null;
		Session session = discussionmotion.getSession();
		if(session==null) {
			logger.error("This discussionmotion has no session.");
			throw new ELSException("discussionmotion_session_null", "This discussionmotion has no session.");
		}
		try{			
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					" AND mi.id=:ministryId AND " +
					"(mm.ministryFromDate <=:discussionmotionSubmissionDate AND (mm.ministryToDate >:discussionmotionSubmissionDate  OR mm.ministryToDate IS NULL)) AND " +
					"mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", discussionmotion.getLocale());
			//query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("ministryId", ministry.getId());
			query.setParameter("discussionmotionSubmissionDate", discussionmotion.getSubmissionDate());
			memberMinister = query.getSingleResult();
		}catch (NoResultException  e) {
			//As this is normal case because discussionmotion may not be submitted by minister
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		return memberMinister;
	}
	
}
