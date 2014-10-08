package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class EventMotionRepository extends BaseRepository<EventMotion, Serializable>{

	public List<ClubbedEntity> findClubbedEntitiesByPosition(final EventMotion motion) {
		return null;
	}

	public Integer assignEventMotionNo(final HouseType houseType,final Session session,
			final DeviceType type,final String locale) {
		String strCutMotionType = type.getType();
		String strQuery = "SELECT m FROM EventMotion m"
				+ " JOIN m.session s"
				+ " JOIN m.deviceType dt"
				+ " WHERE dt.type =:eventMotionType"
				+ " AND s.id=:sessionId"
				+ " ORDER BY m.number " + ApplicationConstants.DESC;	
		try {
			TypedQuery<EventMotion> query=this.em().createQuery(strQuery, EventMotion.class);
			query.setParameter("eventMotionType",strCutMotionType);
			query.setParameter("sessionId",session.getId());
			List<EventMotion> eventMotions = query.setFirstResult(0).setMaxResults(1).getResultList();
			
			if(eventMotions == null) {
				return 0;
			}else if(eventMotions.isEmpty()) {
				return 0;
			}else {
				if(eventMotions.get(0).getNumber() == null) {
					return 0;
				}else{
					return eventMotions.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long eventMotionId,final String locale) {
		org.mkcl.els.domain.Query revisionQuery = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.EVENTMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("eventMotionId", eventMotionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> eventMotionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d = new diff_match_patch();
		for(int i = 0; i < results.size(); i++) {
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if((i+1) < results.size()){
				o1 = (Object[])results.get(i + 1);
			}
			RevisionHistoryVO eventMotionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				eventMotionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
					UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				eventMotionRevisionVO.setEditedAs(userGroupType.getName());
			}
			eventMotionRevisionVO.setEditedBY(o[1].toString());
			eventMotionRevisionVO.setEditedOn(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[2].toString(), ApplicationConstants.DB_DATETIME_FORMAT), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
			eventMotionRevisionVO.setStatus(o[3].toString());
			/**** Revision Control(Details and Subject) ****/
			if(o1 != null){
				if(!o[4].toString().isEmpty()){
					LinkedList<Diff> diff = d.diff_main(o1[4].toString(), o[4].toString());
					String eventMotionDetails = d.diff_prettyHtml(diff);
					if(eventMotionDetails.contains("&lt;")){
						eventMotionDetails = eventMotionDetails.replaceAll("&lt;", "<");
					}
					if(eventMotionDetails.contains("&gt;")){
						eventMotionDetails = eventMotionDetails.replaceAll("&gt;", ">");
					}
					if(eventMotionDetails.contains("&amp;nbsp;")){
						eventMotionDetails = eventMotionDetails.replaceAll("&amp;nbsp;"," ");
					}
					eventMotionRevisionVO.setDetails(eventMotionDetails);
				}else{
					eventMotionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				eventMotionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty()){
					LinkedList<Diff> diff = d.diff_main(o1[5].toString(), o[5].toString());
					String eventMotionTitle = d.diff_prettyHtml(diff);
					if(eventMotionTitle.contains("&lt;")){
						eventMotionTitle = eventMotionTitle.replaceAll("&lt;", "<");
					}
					if(eventMotionTitle.contains("&gt;")){
						eventMotionTitle = eventMotionTitle.replaceAll("&gt;", ">");
					}
					if(eventMotionTitle.contains("&amp;nbsp;")){
						eventMotionTitle = eventMotionTitle.replaceAll("&amp;nbsp;"," ");
					}
					eventMotionRevisionVO.setSubject(eventMotionTitle);
				}else{
					eventMotionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				eventMotionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				eventMotionRevisionVO.setRemarks(o[6].toString());
			}	
			eventMotionRevisionVOs.add(eventMotionRevisionVO);
		}
		return eventMotionRevisionVOs;
	}

	public List<EventMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType eventMotionType,
			final Integer itemsCount,
			final String locale) {
		
		List<EventMotion> motions = new ArrayList<EventMotion>();
		
		try {
			Status status = Status.findByFieldName(Status.class,"type",ApplicationConstants.CUTMOTION_COMPLETE, locale);
			String strQuery = "SELECT cm FROM EventMotion cm"
					+ " WHERE cm.session=:session"
					+ " AND cm.member=:primaryMember" 
					+ " AND cm.deviceType=:eventMotionType"
					+ " AND cm.locale=:locale"
					+ " AND cm.status=:status ORDER BY cm.id "+ ApplicationConstants.DESC;
			TypedQuery<EventMotion> query = this.em().createQuery(strQuery, EventMotion.class);
			query.setMaxResults(itemsCount);
			query.setParameter("session", session);
			query.setParameter("primaryMember", primaryMember);
			query.setParameter("eventMotionType", eventMotionType);
			query.setParameter("locale",locale);
			query.setParameter("status",status);
			motions = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		
		return motions;
	}	

	public List<EventMotion> findAllByStatus(final Session session,
			final DeviceType eventMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		String strQuery="SELECT cm FROM EventMotion cm"
				+ " WHERE cm.session.id=:sessionId" 
				+ " AND cm.deviceType.id=:eventMotionTypeId"
				+ " AND cm.locale=:locale"
				+ " AND cm.internalStatus.id=:internalStatusId" 
				+ " AND cm.workflowStarted=:workflowStarted"
				+ " AND cm.parent IS NULL ORDER BY cm.number";
		List<EventMotion> motions = new ArrayList<EventMotion>();
		
		try{
			TypedQuery<EventMotion> query = this.em().createQuery(strQuery, EventMotion.class);
			query.setParameter("sessionId",session.getId());
			query.setParameter("eventMotionTypeId", eventMotionType.getId());
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

	public Reference findCurrentFile(final EventMotion domain) {
		String strQuery="SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.deviceType.id=:eventMotionTypeId"
				+ " AND m.locale=:locale"
				+ " AND m.fileSent=:fileSent"
				+ " AND m.file IS NOT NULL ORDER BY m.file DESC, m.fileSent DESC";
		Reference reference = null;
		try{
			TypedQuery<EventMotion> query = this.em().createQuery(strQuery, EventMotion.class);
			query.setParameter("sessionId",domain.getSession().getId());
			query.setParameter("eventMotionTypeId",domain.getDeviceType().getId());
			query.setParameter("locale",domain.getLocale());
			query.setParameter("fileSent",false);
			List<EventMotion> motions = query.getResultList();
			EventMotion motion = null;
			if(motions != null && !motions.isEmpty()){
				motion = motions.get(0);
			}
			
			if(motion == null){
				reference = new Reference(String.valueOf(1),String.valueOf(1));
			}else if(motion.getFile() == null){
				reference = new Reference(String.valueOf(1),String.valueOf(1));
			}else if(motion.getFile() != null && motion.getFileIndex() == null){
				reference = new Reference(String.valueOf(motion.getFile()),String.valueOf(1));
			}else{
				CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"FILE_" + domain.getDeviceType().getType().toUpperCase(), "");
				int fileSize = Integer.parseInt(customParameter.getValue());
				if(motion.getFileIndex() == fileSize){
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

	public List<EventMotion> findAllByFile(final Session session,
			final DeviceType eventMotionType,
			final Integer file,
			final String locale) {
		String strQuery="SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.deviceType.id=:eventMotionTypeId"
				+ " AND m.locale=:locale"
				+ " AND m.file=:file" 
				+ " ORDER BY m.fileIndex";
		List<EventMotion> motions = new ArrayList<EventMotion>();
		try{
			TypedQuery<EventMotion> query=this.em().createQuery(strQuery, EventMotion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("eventMotionTypeId", eventMotionType.getId());
			query.setParameter("locale",locale);
			query.setParameter("file",file);
			motions = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return motions;
	}	
	
	public List<EventMotion> findBySessionDeviceTypeSubdepartment(final Session session,
			final DeviceType eventMotionType,
			final SubDepartment subDepartment,
			final String locale) {
		String strQuery="SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.deviceType.id=:eventMotionTypeId"
				+ " AND m.locale=:locale"
				+ " AND m.subDepartment.id=:subDepartmentId" 
				+ " ORDER BY m.submissionDate";
		List<EventMotion> motions = new ArrayList<EventMotion>();
		try{
			TypedQuery<EventMotion> query = this.em().createQuery(strQuery, EventMotion.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("eventMotionTypeId",eventMotionType.getId());
			query.setParameter("locale",locale);
			query.setParameter("subDepartmentId", subDepartment.getId());
			motions = query.getResultList();
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		return motions;
	}

	// change to singleResult if possible
	public int findHighestFileNo(final Session session,
			final DeviceType eventMotionType,
			final String locale) {
		String strQuery="SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId"
				+ " AND m.deviceType.id=:eventMotionTypeId"
				+ " AND m.locale=:locale AND m.file IS NOT NULL" 
				+ " ORDER BY m.file";
		
		TypedQuery<EventMotion> query = this.em().createQuery(strQuery, EventMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("eventMotionTypeId", eventMotionType.getId());
		query.setParameter("locale",locale);
		
		List<EventMotion> motions= query.getResultList();
		if(motions == null){
			return 0;
		}else if(motions.isEmpty()){
			return 0;
		}else{
			 return motions.get(0).getFile();
		}
	}

	public EventMotion getMotion(final Long sessionId, 
			final Long eventMotionTypeId, 
			final Integer dNumber,
			final String locale) {
		String strQuery = "SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId" 
				+ " AND m.deviceType.id=:eventMotionTypeId" 
				+ " AND m.number=:dNumber" 
				+ " AND m.locale=:locale";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("eventMotionTypeId", eventMotionTypeId);
		query.setParameter("dNumber", dNumber);
		query.setParameter("locale", locale);
		EventMotion motion = (EventMotion) query.getSingleResult();
		return motion;
	}

	public Integer findMaxNumberBySubdepartment(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment, 
			final String locale) {
		StringBuffer strQuery = new StringBuffer("SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId" 
				+ " AND m.deviceType.id=:eventMotionTypeId" 
				+ " AND m.subDepartment.id=:subDepartmentId" 
				+ " AND m.locale=:locale");
		
		TypedQuery<EventMotion> tQuery = this.em().createNamedQuery(strQuery.toString(), EventMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("eventMotionTypeId", deviceType.getId());
		tQuery.setParameter("subDepartmentId", subDepartment.getId());
		tQuery.setParameter("locale", locale);
		
		List<EventMotion> motions = tQuery.setFirstResult(0).setMaxResults(1).getResultList();
		
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
	
	public Integer findHighestNumberByStatusDepartment(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status,
			final String locale) {
		StringBuffer strQuery = new StringBuffer("SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId" 
				+ " AND m.deviceType.id=:eventMotionTypeId" 
				+ " AND m.internalStatus.id=:internalStatusId"
				+ " AND m.status.id=:statusId"
				+ " AND m.subDepartment.id=:subDepartmentId"
				+ " AND m.locale=:locale"
				+ " ORDER BY m.internalNumber DESC");
		
		TypedQuery<CutMotion> tQuery = this.em().createQuery(strQuery.toString(), CutMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("eventMotionTypeId", deviceType.getId());
		tQuery.setParameter("subDepartmentId", subDepartment.getId());
		tQuery.setParameter("internalStatusId", status.getId());
		tQuery.setParameter("statusId", status.getId());
		tQuery.setParameter("locale", locale);
		
		List<CutMotion> motions = tQuery.setFirstResult(0).setMaxResults(1).getResultList();
		
		if (motions == null) {
			return 0;
		} else if (motions.isEmpty()) {
			return 0;
		} else {
			if (motions.get(0).getInternalNumber() == null) {
				return 0;
			} else {
				return motions.get(0).getInternalNumber();
			}
		}
	}

	public List<EventMotion> findFinalizedCutMotionsByDepartment(final Session session,
			final DeviceType deviceType,
			final SubDepartment subDepartment,
			final Status status, 
			final String sortOrder, 
			final String locale) {
		
		StringBuffer strQuery = new StringBuffer("SELECT m FROM EventMotion m"
				+ " WHERE m.session.id=:sessionId" 
				+ " AND m.deviceType.id=:eventMotionTypeId" 
				+ " AND m.locale=:locale"
				+ " AND m.internalStatus.id=:internalStatusId"
				+ " AND m.status.id=:statusId"
				+ " AND m.subDepartment.id=:subDepartmentId"
				+ " ORDER BY m.submissionDate " 
				+ sortOrder + " m.demandNumber " 
				+ sortOrder + " m.amountToBeDeducted " 
				+ sortOrder);
		
		TypedQuery<EventMotion> tQuery = this.em().createQuery(strQuery.toString(), EventMotion.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("eventMotionTypeId", deviceType.getId());
		tQuery.setParameter("subDepartmentId", subDepartment.getId());
		tQuery.setParameter("internalStatusId", status.getId());
		tQuery.setParameter("statusId", status.getId());
		tQuery.setParameter("locale", locale);
		
		return tQuery.getResultList();
	}

	public Boolean isExist(final Integer number, final DeviceType deviceType,
			final Session session, final String locale) {
		try{
			StringBuffer strQuery=new StringBuffer();
			strQuery.append("SELECT cm FROM EventMotion cm " +
					" WHERE cm.session.id=:sessionId" +
					" AND cm.number=:number" +
					" AND cm.deviceType.id=:deviceTypeId" +
					" AND cm.locale=:locale");
			Query query = this.em().createQuery(strQuery.toString());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			
			EventMotion motion = (EventMotion) query.getSingleResult();
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
}