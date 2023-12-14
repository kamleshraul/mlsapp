package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
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
import org.mkcl.els.domain.Motion;
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
	
	public List<Object> getDiscussionMotionDetailsMemberStatsReport(final Session session,final DeviceType deviceType,final Member member ){
		List<Object> Qdetails = new ArrayList<Object>();
		String queryString = "SELECT q.number, q.subject ,q.`notice_content`,"
				+ "				  CASE"
				+ "				   WHEN q.parent IS NOT NULL THEN qq.`discussion_date` "
				+ "				   WHEN q.parent IS NULL THEN q.discussion_date"
				+ "				  END AS 'DD',"
				+ "				  q.rejection_reason ,s.name ,dt.type,sd.name AS 'departmentName' ,q.parent "
				+ "				  FROM `discussionmotion` q "
				+ "				  LEFT JOIN `discussionmotion` qq ON (qq.id = q.parent)"
				+ "				  INNER JOIN STATUS s ON (s.id = q.status_id)"
				+ "				  INNER JOIN `discussionmotion_subdepartments` dsd ON (dsd.`discussionmotion_id` = q.`id`)"
				+ "				  INNER JOIN `subdepartments` sd ON (sd.`id`=dsd.`subdepartment_id`)"
				+ "				  INNER JOIN `devicetypes` dt ON (q.`devicetype_id` = dt.`id`) "
				+ "				  WHERE q.session_id =:sessionId "
				+ "				  AND q.devicetype_id =:deviceType "
				+ "				  AND q.member_id =:memberId";
		Query query = this.em().createNativeQuery(queryString);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceType", deviceType.getId());
		query.setParameter("memberId", member.getId());
		
		try {
			Qdetails =  query.getResultList();
			
			}catch(Exception e) {
				e.printStackTrace();
			}
		
		return Qdetails;
	}
	
	public List<DiscussionMotion> findAllAdmittedUndisccussed(final Session session,
			final DeviceType motionType, 
			final Status status,
			final String locale) {
		
		List<DiscussionMotion> motions = new ArrayList<DiscussionMotion>();
		
		try {
			/*Status recommendDiscussed = Status.findByType(ApplicationConstants.MOTION_PROCESSED_DISCUSSED, locale);
			Status recommendUndiscussed = Status.findByType(ApplicationConstants.MOTION_PROCESSED_UNDISCUSSED, locale);*/
			
			String strQuery="SELECT m FROM DiscussionMotion m" +
					" WHERE m.session=:session" +
					" AND m.type=:motionType" +
					" AND m.locale=:locale" +
					" AND m.internalStatus=:internalStatus" + 
					" AND m.parent is NULL "+
					/*" AND (m.discussionStatus!=:recommendationStatusDiscussed AND m.discussionStatus!=:recommendationStatusUndiscussed)" +*/
					" ORDER BY m.number "+ ApplicationConstants.ASC;
			TypedQuery<DiscussionMotion> query = this.em().createQuery(strQuery, DiscussionMotion.class);
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
	public List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY am.number ASC, s.start_date DESC, dt.id ASC";
		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT "
				+ "  am.id AS id,"
				+ "  am.number AS number,"
				+ "  am.subject AS SUBJECT,"
				+ "  am.revised_subject AS revisedSubject,"
				+ "  am.notice_content AS noticeContent,"
				+ "  am.revised_notice_content AS revisedNoticeContent,"
				+ "  st.name AS STATUS,"
				+ "  dt.name AS deviceType,"
				+ "  s.session_year AS sessionYear,"
				+ "  sety.session_type AS sessionType,"
				+ "  mi.name AS ministry,"
				+ "  sd.name AS subdepartment,"
				+ "  st.type AS statustype,"
				+ "  CONCAT(  t.name, ' ', m.first_name, ' ', m.last_name ) AS memberName,  "
				+ "  am.discussion_date AS discussionDate,"
				+ "  am.localized_actor_name AS actor, "
				+ "  ist.name as internalStatus "
				+ " FROM"
				+ "  discussionmotion AS am "
				+ "  LEFT JOIN discussionmotion_ministries dmm ON (dmm.discussionmotion_id = am.id)"
				+ "  LEFT JOIN ministries mi ON (mi.id = dmm.ministry_id)"
				+ "  LEFT JOIN discussionmotion_subdepartments dmsd ON (dmsd.discussionmotion_id = am.id)"
				+ "  LEFT JOIN subdepartments sd ON (sd.id = dmsd.subdepartment_id)"
				+ "  LEFT JOIN housetypes AS ht   ON (am.housetype_id = ht.id)  "		
				+ "  LEFT JOIN sessions AS s ON (am.session_id = s.id) "		
				+ "  LEFT JOIN sessiontypes AS sety  ON (s.sessiontype_id = sety.id) "
				+ "  LEFT JOIN STATUS AS ist ON ( am.internalstatus_id = ist.id ) "				
				+ "  LEFT JOIN STATUS AS st ON ( am.recommendationstatus_id = st.id ) "			
				+ "  LEFT JOIN devicetypes AS dt ON (am.devicetype_id = dt.id) "			
				+ "  LEFT JOIN members AS m  ON (am.member_id = m.id) "			
				+ "  LEFT JOIN titles AS t ON (m.title_id = t.id)  "
				+ " WHERE am.locale = 'mr_IN' "
				+ "  AND st.type NOT IN ( 'discussionmotion_incomplete',  'discussionmotion_complete' )"
				+ "  AND am.number IS NOT NULL ";
		
		StringBuffer filter = new StringBuffer("");
		filter.append(addSpecialMentionNoticeFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "DISCUSSIONMOTION_SEARCH_USE_CURRENT_SESSION", "");
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
				searchQuery=" AND (( match(am.subject,am.notice_content,am.revised_subject,am.revised_notice_content) "+
						"against('"+param+"' in natural language mode)"+
						")||am.subject LIKE '%"+param+"%'||am.notice_content LIKE '%"+param+
						"%'||am.revised_subject LIKE '%"+param+"%'||am.revised_notice_content LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(am.subject,am.notice_content,am.revised_subject,am.revised_notice_content) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(am.subject,am.notice_content,am.revised_subject,am.revised_notice_content) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(am.subject,am.notice_content,am.reason,am.revised_subject,am.revised_notice_content) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.noticeContent,rs.revisedNoticeContent, "+
				"rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.discussionDate,rs.actor,rs.internalStatus FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> discussionMotionSearchVOs=new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO discussionMotionSearchVO=new SearchVO();
				if(o[0]!=null){
					discussionMotionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					discussionMotionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						discussionMotionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							discussionMotionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						discussionMotionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						discussionMotionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							discussionMotionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						discussionMotionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					discussionMotionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					discussionMotionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					discussionMotionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					discussionMotionSearchVO.setSessionType(o[9].toString());
				}
				
				if(o[10]!=null){
					discussionMotionSearchVO.setMinistry(o[10].toString());
				}
				
				if(o[11]!=null){
					discussionMotionSearchVO.setSubDepartment(o[11].toString());
				}
				if(o[12]!=null){
					discussionMotionSearchVO.setStatusType(o[12].toString());
				}
				if(o[13]!=null){
					discussionMotionSearchVO.setFormattedPrimaryMember(o[13].toString());
				}
				if(o[14]!=null){
					discussionMotionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[14].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}else {
					discussionMotionSearchVO.setChartAnsweringDate("-");
				}
				if(o[15]!=null){
					discussionMotionSearchVO.setActor(o[15].toString());
				}
				if(o[16] != null) {
					discussionMotionSearchVO.setInternalStatus(o[16].toString());
				}
				discussionMotionSearchVOs.add(discussionMotionSearchVO);
			}
		}
		return discussionMotionSearchVOs;
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
	
	private String addSpecialMentionNoticeFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND am.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND am.member_id=" + member);
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
}
