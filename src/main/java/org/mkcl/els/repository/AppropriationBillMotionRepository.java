package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.AppropriationBillMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.AppropriationBillMotion;
import org.mkcl.els.domain.AppropriationBillMotionDraft;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class AppropriationBillMotionRepository extends BaseRepository<AppropriationBillMotion, Serializable>{

	public Integer assignAppropriationBillMotionNo(final HouseType houseType,final Session session,
			final DeviceType type,final String locale) {
		String strMotionType = type.getType();
		String strQuery = "SELECT m FROM AppropriationBillMotion m JOIN m.session s JOIN m.deviceType dt" +
		" WHERE dt.type =:motionType AND s.id=:sessionId"+
		" ORDER BY m.number " +ApplicationConstants.DESC;	
		try {
			TypedQuery<AppropriationBillMotion> query=this.em().createQuery(strQuery, AppropriationBillMotion.class);
			query.setParameter("motionType",strMotionType);
			query.setParameter("sessionId",session.getId());
			List<AppropriationBillMotion> motions = query.setFirstResult(0).
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

	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long appropriationBillMotionId,final String locale) {
		org.mkcl.els.domain.Query revisionQuery = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.APPROPRIATIONBILLMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("appropriationBillMotionId",appropriationBillMotionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> appropriationBillMotionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d = new diff_match_patch();
		for(int i = 0; i < results.size(); i++) {
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if((i+1) < results.size()){
				o1 = (Object[])results.get(i + 1);
			}
			RevisionHistoryVO appropriationBillMotionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				appropriationBillMotionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
					UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				appropriationBillMotionRevisionVO.setEditedAs(userGroupType.getName());
			}
			appropriationBillMotionRevisionVO.setEditedBY(o[1].toString());
			appropriationBillMotionRevisionVO.setEditedOn(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[2].toString(), ApplicationConstants.DB_DATETIME_FORMAT), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
			appropriationBillMotionRevisionVO.setStatus(o[3].toString());
			/**** Revision Control(Details and Subject) ****/
			if(o1 != null){
				if(!o[4].toString().isEmpty()){
					LinkedList<Diff> diff = d.diff_main(o1[4].toString(), o[4].toString());
					String appropriationBillMotionDetails = d.diff_prettyHtml(diff);
					if(appropriationBillMotionDetails.contains("&lt;")){
						appropriationBillMotionDetails = appropriationBillMotionDetails.replaceAll("&lt;", "<");
					}
					if(appropriationBillMotionDetails.contains("&gt;")){
						appropriationBillMotionDetails = appropriationBillMotionDetails.replaceAll("&gt;", ">");
					}
					if(appropriationBillMotionDetails.contains("&amp;nbsp;")){
						appropriationBillMotionDetails = appropriationBillMotionDetails.replaceAll("&amp;nbsp;"," ");
					}
					appropriationBillMotionRevisionVO.setDetails(appropriationBillMotionDetails);
				}else{
					appropriationBillMotionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				appropriationBillMotionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty()){
					LinkedList<Diff> diff = d.diff_main(o1[5].toString(), o[5].toString());
					String appropriationBillMotionTitle = d.diff_prettyHtml(diff);
					if(appropriationBillMotionTitle.contains("&lt;")){
						appropriationBillMotionTitle = appropriationBillMotionTitle.replaceAll("&lt;", "<");
					}
					if(appropriationBillMotionTitle.contains("&gt;")){
						appropriationBillMotionTitle = appropriationBillMotionTitle.replaceAll("&gt;", ">");
					}
					if(appropriationBillMotionTitle.contains("&amp;nbsp;")){
						appropriationBillMotionTitle = appropriationBillMotionTitle.replaceAll("&amp;nbsp;"," ");
					}
					appropriationBillMotionRevisionVO.setSubject(appropriationBillMotionTitle);
				}else{
					appropriationBillMotionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				appropriationBillMotionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				appropriationBillMotionRevisionVO.setRemarks(o[6].toString());
			}	
			appropriationBillMotionRevisionVOs.add(appropriationBillMotionRevisionVO);
		}
		return appropriationBillMotionRevisionVOs;
	}

	public List<AppropriationBillMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType appropriationBillMotionType,
			final Integer itemsCount,
			final String locale) {
		
		List<AppropriationBillMotion> motions = new ArrayList<AppropriationBillMotion>();
		
		try {
			Status status = Status.findByFieldName(Status.class,"type",ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE, locale);
			String strQuery = "SELECT cm FROM AppropriationBillMotion cm"
					+ " WHERE cm.session=:session"
					+ " AND cm.primaryMember=:primaryMember" 
					+ " AND cm.deviceType=:appropriationBillMotionType"
					+ " AND cm.locale=:locale"
					+ " AND cm.status=:status ORDER BY cm.id "+ ApplicationConstants.DESC;
			TypedQuery<AppropriationBillMotion> query = this.em().createQuery(strQuery, AppropriationBillMotion.class);
			query.setMaxResults(itemsCount);
			query.setParameter("session", session);
			query.setParameter("primaryMember", primaryMember);
			query.setParameter("appropriationBillMotionType", appropriationBillMotionType);
			query.setParameter("locale",locale);
			query.setParameter("status",status);
			motions = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		
		return motions;
	}	

	public List<AppropriationBillMotion> findAllByStatus(final Session session,
			final DeviceType appropriationBillMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		String strQuery="SELECT cm FROM AppropriationBillMotion cm"
				+ " WHERE cm.session.id=:sessionId" 
				+ " AND cm.deviceType.id=:appropriationBillMotionTypeId"
				+ " AND cm.locale=:locale"
				+ " AND cm.internalStatus.id=:internalStatusId" 
				+ " AND cm.workflowStarted=:workflowStarted"
				+ " AND cm.parent IS NULL ORDER BY cm.number";
		List<AppropriationBillMotion> motions = new ArrayList<AppropriationBillMotion>();
		
		try{
			TypedQuery<AppropriationBillMotion> query=this.em().createQuery(strQuery, AppropriationBillMotion.class);
			query.setParameter("sessionId",session.getId());
			query.setParameter("appropriationBillMotionTypeId",appropriationBillMotionType.getId());
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
	
	public List<AppropriationBillMotion> findBySessionDeviceTypeSubdepartment(final Session session,
			final DeviceType appropriationBillMotionType,
			final SubDepartment subDepartment,
			final String locale) {
		String strQuery="SELECT m FROM AppropriationBillMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.deviceType.id=:appropriationBillMotionTypeId"
				+ " AND m.locale=:locale"
				+ " AND m.subDepartment.id=:subDepartmentId" 
				+ " ORDER BY m.submissionDate";
		List<AppropriationBillMotion> motions = new ArrayList<AppropriationBillMotion>();
		try{
			TypedQuery<AppropriationBillMotion> query=this.em().createQuery(strQuery, AppropriationBillMotion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("appropriationBillMotionTypeId",appropriationBillMotionType.getId());
			query.setParameter("locale",locale);
			query.setParameter("subDepartmentId", subDepartment.getId());
			motions = query.getResultList();
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		return motions;
	}

	public AppropriationBillMotion getMotion(final Long sessionId, 
			final Long appropriationBillMotionTypeId, 
			final Integer dNumber,
			final String locale) {
		String strQuery="SELECT m FROM AppropriationBillMotion m"
				+ " WHERE m.session.id=:sessionId" 
				+ " AND m.deviceType.id=:appropriationBillMotionTypeId" 
				+ " AND m.number=:dNumber" 
				+ " AND m.locale=:locale";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("appropriationBillMotionTypeId", appropriationBillMotionTypeId);
		query.setParameter("dNumber", dNumber);
		query.setParameter("locale", locale);
		AppropriationBillMotion motion=(AppropriationBillMotion) query.getSingleResult();
		return motion;
	}

	public Integer findMaxNumberBySubdepartment(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment, 
			final String locale) {
		StringBuffer strQuery = new StringBuffer("SELECT m FROM AppropriationBillMotion m"
				+ " WHERE m.session.id=:sessionId" 
				+ " AND m.deviceType.id=:appropriationBillMotionTypeId" 
				+ " AND m.subDepartment.id=:subDepartmentId" 
				+ " AND m.locale=:locale");
		
		TypedQuery<AppropriationBillMotion> tQuery = this.em().createNamedQuery(strQuery.toString(), AppropriationBillMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("appropriationBillMotionTypeId", deviceType.getId());
		tQuery.setParameter("subDepartmentId", subDepartment.getId());
		tQuery.setParameter("locale", locale);
		
		List<AppropriationBillMotion> motions = tQuery.setFirstResult(0).setMaxResults(1).getResultList();
		
		if (motions == null) {
			return 0;
		} else if (motions.isEmpty()) {
			return 0;
		} else {
			if (motions.get(0).getNumber() == null) {
				return 0;
			} else {
				return motions.get(0).getNumber();
			}
		}
	}

	public List<AppropriationBillMotion> findFinalizedAppropriationBillMotionsByDepartment(final Session session,
			final DeviceType deviceType,
			final SubDepartment subDepartment,
			final Status status, 
			final String sortOrder, 
			final String locale) {
		
//		StringBuffer strQuery = new StringBuffer("SELECT m FROM AppropriationBillMotion m"
//				+ " WHERE m.session.id=:sessionId" 
//				+ " AND m.deviceType.id=:appropriationBillMotionTypeId" 
//				+ " AND m.locale=:locale"
//				//+ " AND m.internalStatus.id=:internalStatusId"
//				+ " AND m.status.id=:statusId"
//				+ " AND m.subDepartment.id=:subDepartmentId"
//				+ " ORDER BY m.demandNumber " + sortOrder
//				+ ", m.amountToBeDeducted " + sortOrder 
//				+ ", m.primaryMember.lastName " + sortOrder
//				//+ ", m.submissionDate " + sortOrder
//				);
				
		String queryString = "";
		org.mkcl.els.domain.Query queryDB = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CMOIS_FINALIZED_APPROPRIATIONBILLMOTIONS_BY_DEPARTMENT_QUERY", locale);
		if(queryDB!=null)	{
			queryString = queryDB.getQuery();
		} else {
			StringBuffer strQuery = new StringBuffer("SELECT cm.* FROM appropriation_bill_motions cm"
					+ " INNER JOIN members m ON (m.id=cm.member_id)" 
					+ " WHERE cm.session_id=:sessionId" 
					+ " AND cm.devicetype_id=:appropriationBillMotionTypeId"
					+ " AND cm.status_id=:statusId"
					+ " AND cm.department_id=(SELECT department_id FROM subdepartments WHERE id=:subDepartmentId)"
					+ " AND cm.locale=:locale"
					+ " ORDER BY cm.demand_number " + sortOrder
					+ ", cm.amount_to_be_deducted " + sortOrder 
					+ ", m.last_name " + sortOrder
					//+ ", m.submissionDate " + sortOrder
					);
			
			queryString = strQuery.toString();
		}	
		@SuppressWarnings("unchecked")
		TypedQuery<AppropriationBillMotion> tQuery = (TypedQuery<AppropriationBillMotion>) this.em().createNativeQuery(queryString, AppropriationBillMotion.class);		
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("appropriationBillMotionTypeId", deviceType.getId());
		tQuery.setParameter("subDepartmentId", subDepartment.getId());
		//tQuery.setParameter("internalStatusId", status.getId());
		tQuery.setParameter("statusId", status.getId());
		tQuery.setParameter("locale", locale);
		
		return tQuery.getResultList();
	}

	public Boolean isExist(final Integer number, final Long id, final DeviceType deviceType,
			final Session session, final String locale) {
		try{
			StringBuffer strQuery=new StringBuffer();
			strQuery.append("SELECT cm FROM AppropriationBillMotion cm " +
					" WHERE cm.session.id=:sessionId" +
					" AND cm.number=:number" +
					" AND cm.deviceType.id=:deviceTypeId" +
					" AND cm.id<>:appropriationBillMotionId" +
					" AND cm.locale=:locale");
			Query query = this.em().createQuery(strQuery.toString());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			if(id!=null) {
				query.setParameter("appropriationBillMotionId", id);
			} else {
				query.setParameter("appropriationBillMotionId", new Long("0"));
			}
			query.setParameter("locale", locale);
			
			AppropriationBillMotion motion = (AppropriationBillMotion) query.getSingleResult();
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
	
	public List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final AppropriationBillMotion motion, 
			final String sortOrder, final String locale) {
		String strQuery = "SELECT m  FROM AppropriationBillMotion q JOIN q.clubbedEntities m" +
				" WHERE q.id=:motionId ORDER BY m.appropriationBillMotion.discussionDate,m.appropriationBillMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("motionId", motion.getId());
		return query.getResultList();
	}
	
	public List<AppropriationBillMotionDraft> findDraftsForGivenDevice(final Long deviceId) {
		if(deviceId==null) {
			return null;
		}
		List<AppropriationBillMotionDraft> drafts = null;
		
		String queryString = "SELECT qd FROM AppropriationBillMotionDraft qd WHERE deviceId=:deviceId";
		TypedQuery<AppropriationBillMotionDraft> query = this.em().createQuery(queryString, AppropriationBillMotionDraft.class);
		query.setParameter("deviceId", deviceId.toString());
		drafts = query.getResultList();
		return drafts;
	}
	
	public List<MasterVO> findAllYaadiDepartmentDetails(final Session session, final DeviceType appropriationBillMotionType, final String locale) {
		List<MasterVO> allYaadiDepartments = new ArrayList<MasterVO>();
		
		org.mkcl.els.domain.Query queryDB = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CMOIS_ALL_YAADI_DEPARTMENTS_QUERY", locale);
		if(queryDB!=null)	{
			String queryString = queryDB.getQuery();
			Query query=this.em().createNativeQuery(queryString);
			query.setParameter("sessionId", session.getId());
			query.setParameter("appropriationBillMotionType", appropriationBillMotionType.getId());
			query.setParameter("locale", locale);
			List results = query.getResultList();
			for(int i = 0; i < results.size(); i++) {
				Object[] o = (Object[]) results.get(i);
				MasterVO yaadiDepartment = new MasterVO();
				yaadiDepartment.setValue(o[0].toString());
				yaadiDepartment.setName(o[1].toString());
				allYaadiDepartments.add(yaadiDepartment);
			}
		}
		
		return allYaadiDepartments;
	}
	
	public List<MasterVO> findInternalMinistriesForDepartment(final Session session,
			final DeviceType appropriationBillMotionType,
			final SubDepartment subDepartment,
			Date onDate,
			final String locale) {
		
		List<MasterVO> internalMinistries = new ArrayList<MasterVO>();
		
		org.mkcl.els.domain.Query queryDB = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "APPROPRIATIONBILLMOTION_INTERNAL_MINISTRIES_FOR_DEPARTMENT_QUERY", locale);
		if(queryDB!=null)	{
			String queryString = queryDB.getQuery();
			Query query=this.em().createNativeQuery(queryString);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", appropriationBillMotionType.getId());
			query.setParameter("subDepartmentId", subDepartment.getId());
			String onDateStr = FormaterUtil.formatDateToString(onDate, ApplicationConstants.DB_DATEFORMAT);
			onDate = FormaterUtil.formatStringToDate(onDateStr, ApplicationConstants.DB_DATEFORMAT);
			query.setParameter("onDate", onDate);
			//query.setParameter("locale", locale);
			List results = query.getResultList();
			for(int i = 0; i < results.size(); i++) {
				Object[] o = (Object[]) results.get(i);
				MasterVO internalMinistry = new MasterVO();
				internalMinistry.setId(Long.parseLong(o[0].toString()));
				internalMinistry.setName(o[1].toString());
				internalMinistry.setDisplayName(o[2].toString());
				internalMinistries.add(internalMinistry);
			}
		}
		
		return internalMinistries;
	}
	

	
	public List<Long> findAppropriationBillMotionIDsHavingPendingReplyPostLastDateOfReplyReceiving(final HouseType houseType, final DeviceType deviceType, final SubDepartment subDepartment, final String locale) throws ELSException {
		List<Long> appropriationbillmotionIds = new ArrayList<Long>();
		org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.QUERYNAME_CMOIS_PENDING_FOR_REPLY_POST_LAST_ANSWERING_DATE, "");
		String strquery = nativeQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("houseTypeId",houseType.getId());
		query.setParameter("deviceTypeId",deviceType.getId());
		Status admittedStatus = Status.findByType(ApplicationConstants.APPROPRIATIONBILLMOTION_FINAL_ADMISSION, locale);
		query.setParameter("admittedStatusIdForDeviceType",admittedStatus.getId());
		query.setParameter("subDepartmentId",subDepartment.getId());
		List result =query.getResultList();
		for(Object i : result) {
			Long appropriationbillmotionId = Long.parseLong(i.toString());
			appropriationbillmotionIds.add(appropriationbillmotionId);
		}
		return appropriationbillmotionIds;
	}
	
}