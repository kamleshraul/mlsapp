package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.batik.svggen.font.table.Device;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.ResolutionSearchVO;
import org.mkcl.els.domain.Act;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class ReferencedEntityRepository extends BaseRepository<ReferencedEntity, Serializable>{
	
	public Boolean referencing(final Long primaryId,final Long referencingId,final String locale) {
		try {
			Question primaryQuestion=Question.findById(Question.class,primaryId);
			Question referencedQuestion=Question.findById(Question.class,referencingId);
			List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
			referencedEntities=primaryQuestion.getReferencedEntities();		
			boolean alreadyRefered=false;
			int position=0;
			for(ReferencedEntity i:referencedEntities){
				
				if(i.getDeviceType() != null){
					if(i.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
						if(((Question)i.getDevice()).getId()==referencedQuestion.getId()){
							alreadyRefered=true;
						}
						position++;
						
						if(!alreadyRefered){
							ReferencedEntity referencedEntity=new ReferencedEntity();
							referencedEntity.setLocale(referencedQuestion.getLocale());
							referencedEntity.setDevice(referencedQuestion);
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
					}else if(i.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						
					}
				}
			}		
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public Boolean deReferencing(final Long primaryId,final Long referencingId,final String locale) {
		try {
			Question primaryQuestion=Question.findById(Question.class,primaryId);
			Question referencedQuestion=Question.findById(Question.class,referencingId);
			List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
			List<ReferencedEntity> newReferencedEntities=new ArrayList<ReferencedEntity>();
			referencedEntities=primaryQuestion.getReferencedEntities();
			ReferencedEntity referencedEntityToRemove=null;
			for(ReferencedEntity i:referencedEntities){
				
				if(i.getDeviceType() != null){
					if(i.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
						if(((Question)i.getDevice()).getId()==referencedQuestion.getId()){
							referencedEntityToRemove=i;				
						}else{
							newReferencedEntities.add(i);
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
					}else if(i.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						if(i.getDeviceType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
							Resolution primaryResolution=Resolution.findById(Resolution.class,primaryId);
							//Resolution referencedResolution=Resolution.findById(Resolution.class,referencingId);
							
							ReferencedEntity referencedEntityToBeRemoved=null;
							
							referencedEntityToBeRemoved=primaryResolution.getReferencedResolution();
									
							primaryResolution.simpleMerge();
							referencedEntityToBeRemoved.remove();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public List<QuestionSearchVO> fullTextSearchReferencing(final String param,
			final Question question,final int start,final int noOfRecords,final String locale) {
		return null;
	}
	
	public List<MotionSearchVO> fullTextSearchReferencing(final String param,
			final Motion motion,final int start,final int noOfRecords,final String locale) {
		
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		try{
			String houseType = motion.getHouseType().getType();
			Status statusAdmitted = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, motion.getLocale());
			Status statusRejected = Status.findByType(ApplicationConstants.MOTION_FINAL_REJECTION, motion.getLocale());
			String admittedStatusId = null;
			String rejectedStatusId = null;
			
			CustomParameter sessionsToBeSearched_CP = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.MOTION_SESSIONS_TOBE_SEARCHED_COUNT, "");
			int sessionsToBeSearched = 0;
			
			if(sessionsToBeSearched_CP != null){
				sessionsToBeSearched = Integer.parseInt(sessionsToBeSearched_CP.getValue());
			}		
			
			// to find the session to be searched
			List<Session> totalSessions = new ArrayList<Session>();		
			List<Session> sessionListCurrent = Session.findSessionsByHouseAndYear(motion.getSession().getHouse(), motion.getSession().getYear());		
			sessionListCurrent.remove(motion.getSession());
			totalSessions.addAll(sessionListCurrent);
			
			List<Session> sessionListLast = Session.findSessionsByHouseAndYear(motion.getSession().getHouse(), motion.getSession().getYear()-1);
					
			if(sessionListLast.size() > 0){
				for(Session s : sessionListLast){
					totalSessions.add(s);
				}
			}
			
			StringBuffer sb = new StringBuffer();		
			int index = 1;
			for(Session ss: totalSessions){
				sb.append(ss.getId());
				if(index < sessionsToBeSearched){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}
			
			if (statusAdmitted != null && statusRejected != null) {
				admittedStatusId = statusAdmitted.getId().toString();
				rejectedStatusId = statusRejected.getId().toString();
			}
	
			String selectQuery = "SELECT m.id as id,m.number as number,"
					+ "m.subject as subject,m.revised_subject as revisedSubject,"
					+ "m.details as noticeContent,r.revised_details as revisedNoticeContent,"
					+ "st.name as status,dt.name as deviceType,"
					+ "mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype,s.sessiontype_id as sessionType,s.session_year as year "
					+ "FROM motions as m "
					+ "LEFT JOIN housetypes as ht ON(m.housetype_id=ht.id) "
					+ "LEFT JOIN sessions as s ON(m.session_id=s.id) "
					+ "LEFT JOIN status as st ON(m.internalstatus_id=st.id) "
					+ "LEFT JOIN devicetypes as dt ON(m.devicetype_id=dt.id) "
					+ "LEFT JOIN ministries as mi ON(m.ministry_id=mi.id) "
					+ "LEFT JOIN departments as d ON(m.department_id=d.id) "
					+ "LEFT JOIN subdepartments as sd ON(m.subdepartment_id=sd.id) "
					+ "WHERE m.id<>" + motion.getId() 
					+ " AND (m.internalstatus_id=" + admittedStatusId
					+ " OR m.internalstatus_id=" + rejectedStatusId + ")"
					+ " AND ht.type='" + houseType +"'"
					+ "AND s.id IN (" + sb + ")";
			
			String searchQuery=null;
			if(!param.contains("+")&&!param.contains("-")){
				searchQuery=" AND (( match(m.subject,m.details,m.revised_subject,m.revised_details) "+
				"against('"+param+"' in natural language mode)"+
				")||m.subject LIKE '"+param+"%'||m.details LIKE '"+param+"%'|| m.revised_subject LIKE '"+param+"%'|| m.revised_details LIKE '"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters=param.split("\\+");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append("+"+i+" ");
				}
				searchQuery=" AND match(m.subject,m.details,r.revised_subject,m.revised_details) "+
				"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(m.subject,m.details,m.revised_subject,m.revised_details) "+
				"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(m.subject,m.details,m.revised_subject,m.revised_details) "+
				"against('"+param+"' in boolean  mode)";
			}
			
			String orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+
					" ,m.number "+ApplicationConstants.DESC + ", s.session_year "+ApplicationConstants.DESC;
			/**** Final Query ****/
			String query = selectQuery+searchQuery+orderByQuery;
			
			String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.noticeContent, "+
			" rs.revisedNoticeContent,rs.status,rs.deviceType,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.sessionType,rs.year FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;
			
			List result = this.em().createNativeQuery(finalQuery).getResultList();
			List<ResolutionSearchVO> resolutionSearchVOs = new ArrayList<ResolutionSearchVO>();
			
			if(result != null){
				for(Object i : result){
					Object[] o = (Object[]) i;
					
					
					MotionSearchVO motionSearchVO = new MotionSearchVO();
					
					if(o[0] != null){
						motionSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					
					if(o[1] != null){
						motionSearchVO.setNumber(o[1].toString());
					}
					
					if(o[3] != null){
						if(!o[3].toString().isEmpty()){
							motionSearchVO.setTitle(higlightText(o[3].toString(), param));
						}else if(o[2] != null){
							motionSearchVO.setTitle(higlightText(o[2].toString(), param));
						}
					}else if(o[2] != null){
						if(!o[2].toString().isEmpty()){
							motionSearchVO.setTitle(higlightText(o[2].toString(), param));
						}
					}
					
					if(o[5] != null){
						if(!o[5].toString().isEmpty()){
							motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
						}else if(o[4] != null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}else if(o[4] != null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
					
					
					if(o[6] != null){
						motionSearchVO.setStatus(o[6].toString());
					}
					if(o[7] != null){
						motionSearchVO.setDeviceType(o[7].toString());
					}
					
					if(o[8] != null){
						motionSearchVO.setMinistry(o[8].toString());
					}
					
					if(o[9] != null){
						motionSearchVO.setDepartment(o[9].toString());
					}
					
					if(o[10] != null){
						motionSearchVO.setSubDepartment(o[10].toString());
					}
					
					if(o[11] != null){
						motionSearchVO.setStatusType(o[11].toString());
					}
					
					if(o[12] != null){
						Long sessionTypeId = new Long(o[12].toString());
						SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
						if(sessionType != null){
							motionSearchVO.setSessionType(sessionType.getSessionType());
						}
					}
					
					if(o[13] != null){					
						motionSearchVO.setSessionYear(o[13].toString());
					}
					
					motionSearchVOs.add(motionSearchVO);			
				}
			}
		}catch(Exception e){
			logger.error("ReferencedEntityRepository", e);
		}
		
		return motionSearchVOs;
	}

	public List<ResolutionSearchVO> fullTextSearchReferencingResolution(String param,
			Resolution resolution, boolean isAutomatic, int start, int noOfRecords, String locale) throws ELSException {

		String houseType = resolution.getHouseType().getType();
		Status statusAdmitted = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION,resolution.getLocale());
		Status statusRejected = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION,resolution.getLocale());
		String admittedStatusId = null;
		String rejectedStatusId = null;
		
		CustomParameter sessionsToBeSearched_CP = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.RESOLUTION_NONOFFICIAL_SESSIONS_TOBE_SEARCHED_COUNT, "");
		int sessionsToBeSearched = 0;
		
		if(sessionsToBeSearched_CP != null){
			sessionsToBeSearched = Integer.parseInt(sessionsToBeSearched_CP.getValue());
		}		
		
		// to find the session to be searched
		List<Session> totalSessions = new ArrayList<Session>();		
		List<Session> sessionListCurrent = Session.findSessionsByHouseAndYear(resolution.getSession().getHouse(), resolution.getSession().getYear());		
		sessionListCurrent.remove(resolution.getSession());
		totalSessions.addAll(sessionListCurrent);
		
		List<Session> sessionListLast = Session.findSessionsByHouseAndYear(resolution.getSession().getHouse(), resolution.getSession().getYear()-1);
				
		if(sessionListLast.size() > 0){
			for(Session s : sessionListLast){
				totalSessions.add(s);
			}
		}
		
		StringBuffer sb = new StringBuffer();		
		int index = 1;
		for(Session ss: totalSessions){
			sb.append(ss.getId());
			if(index < sessionsToBeSearched){
				sb.append(",");
			}else{
				break;
			}
			index++;
		}
		
		if (statusAdmitted != null && statusRejected != null) {
			admittedStatusId = statusAdmitted.getId().toString();
			rejectedStatusId = statusRejected.getId().toString();
		}

		String selectQuery = "SELECT r.id as id,r.number as number,"
				+ "r.subject as subject,r.revised_subject as revisedSubject,"
				+ "r.notice_content as noticeContent,r.revised_notice_content as revisedNoticeContent,"
				+ "st.name as status,dt.name as deviceType,"
				+ "mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype,s.sessiontype_id as sessionType,s.session_year as year "
				+ "FROM resolutions as r "
				+ "LEFT JOIN housetypes as ht ON(r.housetype_id=ht.id) "
				+ "LEFT JOIN sessions as s ON(r.session_id=s.id) "
				+ "LEFT JOIN status as st ON(r." + houseType + "_internalstatus_id=st.id) "
				+ "LEFT JOIN devicetypes as dt ON(r.devicetype_id=dt.id) "
				+ "LEFT JOIN ministries as mi ON(r.ministry_id=mi.id) "
				+ "LEFT JOIN departments as d ON(r.department_id=d.id) "
				+ "LEFT JOIN subdepartments as sd ON(r.subdepartment_id=sd.id) "
				+ "WHERE r.id<>" + resolution.getId() 
				+ " AND (r." + houseType + "_internalstatus_id=" + admittedStatusId
				+ " OR r." + houseType + "_internalstatus_id=" + rejectedStatusId + ")"
				+ " AND ht.type='" + houseType +"'"
				+ "AND s.id IN (" + sb + ")";
		
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+param+"' in natural language mode)"+
			")||r.subject LIKE '"+param+"%'||r.notice_content LIKE '"+param+"%'|| r.revised_subject LIKE '"+param+"%'|| r.revised_notice_content LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+param+"' in boolean  mode)";
		}
		
		String orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+
				" ,r.number "+ApplicationConstants.DESC + ", s.session_year "+ApplicationConstants.DESC;
		/**** Final Query ****/
		String query=null;
		if(isAutomatic == true){
			StringBuffer automaticSearching = new StringBuffer(
					" AND (r.subject NOT LIKE '%" + param + "'" +
					" OR r.subject NOT LIKE '%" + param + "%'" +
					" OR r.subject NOT LIKE '" + param + "%')");
			
			query = selectQuery + searchQuery + automaticSearching + orderByQuery;
			
		}else{
			query = selectQuery+searchQuery+orderByQuery;
		}
		
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.noticeContent, "+
		" rs.revisedNoticeContent,rs.status,rs.deviceType,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.sessionType,rs.year FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;
		
		List result = this.em().createNativeQuery(finalQuery).getResultList();
		List<ResolutionSearchVO> resolutionSearchVOs = new ArrayList<ResolutionSearchVO>();
		
		if(result != null){
			for(Object i : result){
				Object[] o = (Object[]) i;
				
				
				ResolutionSearchVO resolutionSearchVO = new ResolutionSearchVO();
				
				if(o[0] != null){
					resolutionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				
				if(o[1] != null){
					resolutionSearchVO.setNumber(o[1].toString());
				}
				
				if(o[3] != null){
					if(!o[3].toString().isEmpty()){
						resolutionSearchVO.setSubject(higlightText(o[3].toString(), param));
					}else if(o[2] != null){
						resolutionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}else if(o[2] != null){
					if(!o[2].toString().isEmpty()){
						resolutionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}
				
				if(o[5] != null){
					if(!o[5].toString().isEmpty()){
						resolutionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else if(o[4] != null){
						resolutionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}else if(o[4] != null){
					resolutionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
				}
				
				
				if(o[6] != null){
					resolutionSearchVO.setStatus(o[6].toString());
				}
				if(o[7] != null){
					resolutionSearchVO.setDeviceType(o[7].toString());
				}
				
				if(o[8] != null){
					resolutionSearchVO.setMinistry(o[8].toString());
				}
				
				if(o[9] != null){
					resolutionSearchVO.setDepartment(o[9].toString());
				}
				
				if(o[10] != null){
					resolutionSearchVO.setSubDepartment(o[10].toString());
				}
				
				if(o[11] != null){
					resolutionSearchVO.setStatusType(o[11].toString());
				}
				
				if(o[12] != null){
					Long sessionTypeId = new Long(o[12].toString());
					SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
					if(sessionType != null){
						resolutionSearchVO.setSessionType(sessionType.getSessionType());
					}
				}
				
				if(o[13] != null){					
					resolutionSearchVO.setSessionYear(o[13].toString());
				}
				
				resolutionSearchVOs.add(resolutionSearchVO);			
			}
		}

		return resolutionSearchVOs;
	}

	public List<ResolutionSearchVO> fullTextSearchReferencingResolution(final String param,
			final Resolution resolution,final Session session,final int start,final int noOfRecords,final String locale) {

		String houseType = resolution.getHouseType().getType();
		Status statusAdmitted = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION,resolution.getLocale());
		Status statusRejected = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION,resolution.getLocale());
		String admittedStatusId = null;
		String rejectedStatusId = null;

		if (statusAdmitted != null && statusRejected != null) {
			admittedStatusId = statusAdmitted.getId().toString();
			rejectedStatusId = statusRejected.getId().toString();
		}

		String selectQuery = "SELECT r.id as id,r.number as number,"
				+ "r.subject as subject,r.revised_subject as revisedSubject,"
				+ "r.notice_content as noticeContent,r.revised_notice_content as revisedNoticeContent,"
				+ "st.name as status,dt.name as deviceType,"
				+ "mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype,s.sessiontype_id as sessionType,s.session_year as year "
				+ "FROM resolutions as r "
				+ "LEFT JOIN housetypes as ht ON(r.housetype_id=ht.id) "
				+ "LEFT JOIN status as st ON(r." + houseType + "_internalstatus_id=st.id) "
				+ "LEFT JOIN sessions as s ON(r.session_id=s.id) "
				+ "LEFT JOIN devicetypes as dt ON(r.devicetype_id=dt.id) "
				+ "LEFT JOIN ministries as mi ON(r.ministry_id=mi.id) "
				+ "LEFT JOIN departments as d ON(r.department_id=d.id) "
				+ "LEFT JOIN subdepartments as sd ON(r.subdepartment_id=sd.id) "
				+ "WHERE r.id<>" + resolution.getId() + " AND (r." + houseType 
				+ "_internalstatus_id=" + admittedStatusId + " OR r."
				+ houseType+"_internalstatus_id="+rejectedStatusId+")" 
				+ " AND ht.type='" + houseType +"' AND r.session_id="+session.getId() +" AND r.referenced_resolution is NULL";

		
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+param+"' in natural language mode)"+
			")||r.subject LIKE '"+param+"%'||r.notice_content LIKE '"+param+"%'|| r.revised_subject LIKE '"+param+"%'|| r.revised_subject LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(r.subject,r.notice_content,r.revised_subject,r.revised_notice_content) "+
			"against('"+param+"' in boolean  mode)";
		}		
		
		String orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+
				" ,r.number "+ApplicationConstants.DESC + ",s.session_year "+ApplicationConstants.DESC;
		/**** Final Query ****/
		String query=selectQuery+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.noticeContent, "+
		" rs.revisedNoticeContent,rs.status,rs.deviceType,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.sessionType,rs.year FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;
		
		List result = this.em().createNativeQuery(finalQuery).getResultList();
		List<ResolutionSearchVO> resolutionSearchVOs = new ArrayList<ResolutionSearchVO>();
		
		if(result != null){
			for(Object i : result){
				Object[] o = (Object[]) i;
				ResolutionSearchVO resolutionSearchVO = new ResolutionSearchVO();
				
				if(o[0] != null){
					resolutionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				
				if(o[1] != null){
					resolutionSearchVO.setNumber(o[1].toString());
				}
				
				if(o[3] != null){
					if(!o[3].toString().isEmpty()){
						resolutionSearchVO.setSubject(higlightText(o[3].toString(), param));
					}else if(o[2] != null){
						resolutionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}else if(o[2] != null){
					if(!o[2].toString().isEmpty()){
						resolutionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}
				
				if(o[5] != null){
					if(!o[5].toString().isEmpty()){
						resolutionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else if(o[4] != null){
						resolutionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}else if(o[4] != null){
					resolutionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
				}
				
				
				if(o[6] != null){
					resolutionSearchVO.setStatus(o[6].toString());
				}
				if(o[7] != null){
					resolutionSearchVO.setDeviceType(o[7].toString());
				}
				
				if(o[8] != null){
					resolutionSearchVO.setMinistry(o[8].toString());
				}
				
				if(o[9] != null){
					resolutionSearchVO.setDepartment(o[9].toString());
				}
				
				if(o[10] != null){
					resolutionSearchVO.setSubDepartment(o[10].toString());
				}
				
				if(o[11] != null){
					resolutionSearchVO.setStatusType(o[11].toString());
				}
				
				if(o[12] != null){
					Long sessionTypeId = new Long(o[12].toString());
					SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
					if(sessionType != null){
						resolutionSearchVO.setSessionType(sessionType.getSessionType());
					}
				}
				
				if(o[13] != null){					
					resolutionSearchVO.setSessionYear(o[13].toString());
				}
				
				resolutionSearchVOs.add(resolutionSearchVO);
			}
		}

		return resolutionSearchVOs;
	}
	
	public Integer fullTextSearchReferencingId(final String param,
			final Resolution resolution,final int start,final int noOfRecords,final String locale) {

		String houseType = resolution.getHouseType().getType();
		Status statusAdmitted = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION,resolution.getLocale());
		Status statusRejected = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION,resolution.getLocale());
		String admittedStatusId = null;
		String rejectedStatusId = null;

		if (statusAdmitted != null && statusRejected != null) {
			admittedStatusId = statusAdmitted.getId().toString();
			rejectedStatusId = statusRejected.getId().toString();
		}

		String selectQuery = "SELECT r.id as id "
				+ "FROM resolutions as r "
				+ "LEFT JOIN housetypes as ht ON(r.housetype_id=ht.id) "
				+ "LEFT JOIN status as st ON(r." + houseType + "_internalstatus_id=st.id) "
				+ "LEFT JOIN sessions as s ON(r.session_id=s.id) "
				+ "LEFT JOIN devicetypes as dt ON(r.devicetype_id=dt.id) "
				+ "LEFT JOIN ministries as mi ON(r.ministry_id=mi.id) "
				+ "LEFT JOIN departments as d ON(r.department_id=d.id) "
				+ "LEFT JOIN subdepartments as sd ON(r.subdepartment_id=sd.id) "
				+ "WHERE (r." + houseType + "_internalstatus_id=" + admittedStatusId
				+ " OR r."+ houseType + "_internalstatus_id=" + rejectedStatusId + ")"
				+ " AND ht.type='" + houseType + "'" 
				+ " AND r.session_id<>" + resolution.getSession().getId();

		
		String searchQuery=" AND (r.notice_content='"+param+"' OR r.revised_notice_content='"+param + "')";
		
		String orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+
				" ,r.number "+ApplicationConstants.DESC + ",s.session_year "+ApplicationConstants.DESC;
		/**** Final Query ****/
		String query=selectQuery+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;
		
		List result = this.em().createNativeQuery(finalQuery).getResultList();
		Integer resolutionId  = null;
		
		if(result != null){
			int i = 0;
			for(Object ro: result){
				if(i==0){
					Object o = (Object)ro.toString();
					if(o != null){
						resolutionId = new Integer(o.toString());
					}
				}else{
					break;
				}
				i++;
			}
		}

		return resolutionId;
	}
	
	public List<QuestionSearchVO> fullTextSearchReferencingQuestionHDS(final String param,
			final Question question,final Session session,final int start,final int noOfRecords,final String locale) {

		String houseType = question.getHouseType().getType();
		Status statusAdmitted = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION,question.getLocale());
		Status statusRejected = Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION,question.getLocale());
		String admittedStatusId = null;
		String rejectedStatusId = null;

		if (statusAdmitted != null && statusRejected != null) {
			admittedStatusId = statusAdmitted.getId().toString();
			rejectedStatusId = statusRejected.getId().toString();
		}

		String selectQuery = "SELECT q.id as id,q.number as number,"
				+ "q.subject as subject,q.revised_subject as revisedSubject,"
				+ "q.question_text as questionText,q.revised_question_text as revisedQuestionText,"
				+ "st.name as status,dt.name as deviceType,"
				+ "mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype,"
				+ "s.sessiontype_id as sessionType,s.session_year as year "
				+ "FROM questions as q "
				+ "LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "
				+ "LEFT JOIN status as st ON(q.internalstatus_id=st.id) "
				+ "LEFT JOIN sessions as s ON(q.session_id=s.id) "
				+ "LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "
				+ "LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "
				+ "LEFT JOIN departments as d ON(q.department_id=d.id) "
				+ "LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "
				+ "WHERE q.id<>" + question.getId() + " AND (q.internalstatus_id=" + admittedStatusId + " OR q.internalstatus_id="+rejectedStatusId+")" 
				+ " AND ht.type='" + houseType +"' AND q.session_id="+session.getId() +" AND q.referencedhds is NULL";

		
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in natural language mode)"+
			")||q.subject LIKE '"+param+"%'||q.question_text LIKE '"+param+"%' || q.revised_question_text LIKE '"+param+"%'|| q.revised_subject LIKE '"+param+"%')";
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
		
		String orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+
				" ,q.number "+ApplicationConstants.DESC + ",s.session_year "+ApplicationConstants.DESC;
		/**** Final Query ****/
		String query=selectQuery+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
		" rs.revisedQuestionTextt,rs.status,rs.deviceType,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.sessionType,rs.year FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;
		
		List result = this.em().createNativeQuery(finalQuery).getResultList();
		List<QuestionSearchVO> resolutionSearchVOs = new ArrayList<QuestionSearchVO>();
		
		if(result != null){
			for(Object i : result){
				Object[] o = (Object[]) i;
				QuestionSearchVO resolutionSearchVO = new QuestionSearchVO();
				
				if(o[0] != null){
					resolutionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				
				if(o[1] != null){
					resolutionSearchVO.setNumber(o[1].toString());
				}
				
				if(o[3] != null){
					if(!o[3].toString().isEmpty()){
						resolutionSearchVO.setSubject(higlightText(o[3].toString(), param));
					}else if(o[2] != null){
						resolutionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}else if(o[2] != null){
					if(!o[2].toString().isEmpty()){
						resolutionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}
				
				if(o[5] != null){
					if(!o[5].toString().isEmpty()){
						resolutionSearchVO.setQuestionText(higlightText(o[5].toString(),param));
					}else if(o[4] != null){
						resolutionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
					}
				}else if(o[4] != null){
					resolutionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
				}
				
				
				if(o[6] != null){
					resolutionSearchVO.setStatus(o[6].toString());
				}
				if(o[7] != null){
					resolutionSearchVO.setDeviceType(o[7].toString());
				}
				
				if(o[8] != null){
					resolutionSearchVO.setMinistry(o[8].toString());
				}
				
				if(o[9] != null){
					resolutionSearchVO.setDepartment(o[9].toString());
				}
				
				if(o[10] != null){
					resolutionSearchVO.setSubDepartment(o[10].toString());
				}
				
				if(o[11] != null){
					resolutionSearchVO.setStatusType(o[11].toString());
				}
				
				if(o[12] != null){
					Long sessionTypeId = new Long(o[12].toString());
					SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
					if(sessionType != null){
						resolutionSearchVO.setSessionType(sessionType.getSessionType());
					}
				}
				
				if(o[13] != null){					
					resolutionSearchVO.setSessionYear(o[13].toString());
				}
				
				resolutionSearchVOs.add(resolutionSearchVO);
			}
		}

		return resolutionSearchVOs;
	}
	
	public List<QuestionSearchVO> fullTextSearchReferencingQuestionHDS(final String param,
			final Question question,final boolean isAutomatic,final int start,final int noOfRecords,final String locale) throws ELSException {

		String houseType = question.getHouseType().getType();
		Status statusAdmitted = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION,question.getLocale());
		Status statusRejected = Status.findByType(ApplicationConstants.QUESTION_FINAL_REJECTION,question.getLocale());
		String admittedStatusId = null;
		String rejectedStatusId = null;
		
		
		// to find the session to be searched
		List<Session> totalSessions = new ArrayList<Session>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(question.getSession().getHouse().getFirstDate());
		
		Integer currentHouseFormationYear = calendar.get(Calendar.YEAR);
		
		for(int i = currentHouseFormationYear.intValue(); i <= question.getSession().getYear().intValue(); i++){
			List<Session> sessionList = Session.findSessionsByHouseAndYear(question.getSession().getHouse(), i);
			if(sessionList != null){
				if(sessionList.size() > 0){
					totalSessions.addAll(sessionList);
				}
			}
		}
		
		StringBuffer sb = new StringBuffer();		
		int index = 1;
		for(Session ss: totalSessions){
			sb.append(ss.getId());
			if(index < totalSessions.size()){
				sb.append(",");
			}else{
				break;
			}
			index++;
		}
		
		totalSessions = null;
		
		if (statusAdmitted != null && statusRejected != null) {
			admittedStatusId = statusAdmitted.getId().toString();
			rejectedStatusId = statusRejected.getId().toString();
		}

		String selectQuery = "SELECT q.id as id,q.number as number,"
				+ "q.subject as subject,q.revised_subject as revisedSubject,"
				+ "q.question_text as questionText,q.revised_question_text as revisedQuestionText,"
				+ "st.name as status,dt.name as deviceType,"
				+ "mi.name as ministry,sd.name as subdepartment,st.type as statustype,s.sessiontype_id as sessionType,s.session_year as year "
				+ "FROM questions as q "
				+ "LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "
				+ "LEFT JOIN sessions as s ON(q.session_id=s.id) "
				+ "LEFT JOIN status as st ON(q.internalstatus_id=st.id) "
				+ "LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "
				+ "LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "
				+ "LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "
				+ "WHERE q.id<>" + question.getId() 
				+ " AND (q.internalstatus_id=" + admittedStatusId
				+ " OR q.internalstatus_id=" + rejectedStatusId + ")"
				+ " AND ht.type='" + houseType +"'"
				+ "AND s.id IN (" + sb + ")";
		
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in natural language mode)"+
			")||q.subject LIKE '"+param+"%'||q.question_text LIKE '"+param+"%'|| q.revised_subject LIKE '"+param+"%'|| q.revised_question_text LIKE '"+param+"%')";
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
		
		String orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+
				" ,q.number "+ApplicationConstants.DESC + ", s.session_year "+ApplicationConstants.DESC;
		/**** Final Query ****/
		String query=null;
		if(isAutomatic == true){
			StringBuffer automaticSearching = new StringBuffer(
					" AND (q.subject NOT LIKE '%" + param + "'" +
					" OR q.subject NOT LIKE '%" + param + "%'" +
					" OR q.subject NOT LIKE '" + param + "%')");
			
			query = selectQuery + searchQuery + automaticSearching + orderByQuery;
			
		}else{
			query = selectQuery+searchQuery+orderByQuery;
		}
		
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
		" rs.revisedQuestionText,rs.status,rs.deviceType,rs.ministry,rs.subdepartment,rs.statustype,rs.sessionType,rs.year FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;
		
		List result = this.em().createNativeQuery(finalQuery).getResultList();
		List<QuestionSearchVO> questionSearchVOs = new ArrayList<QuestionSearchVO>();
		
		if(result != null){
			for(Object i : result){
				Object[] o = (Object[]) i;
				
				
				QuestionSearchVO questionSearchVO = new QuestionSearchVO();
				
				if(o[0] != null){
					questionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				
				if(o[1] != null){
					questionSearchVO.setNumber(o[1].toString());
				}
				
				if(o[3] != null){
					if(!o[3].toString().isEmpty()){
						questionSearchVO.setSubject(higlightText(o[3].toString(), param));
					}else if(o[2] != null){
						questionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}else if(o[2] != null){
					if(!o[2].toString().isEmpty()){
						questionSearchVO.setSubject(higlightText(o[2].toString(), param));
					}
				}
				
				if(o[5] != null){
					if(!o[5].toString().isEmpty()){
						questionSearchVO.setQuestionText(higlightText(o[5].toString(),param));
					}else if(o[4] != null){
						questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
					}
				}else if(o[4] != null){
					questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
				}
				
				
				if(o[6] != null){
					questionSearchVO.setStatus(o[6].toString());
				}
				if(o[7] != null){
					questionSearchVO.setDeviceType(o[7].toString());
				}
				
				if(o[8] != null){
					questionSearchVO.setMinistry(o[8].toString());
				}
				
				if(o[9] != null){
					questionSearchVO.setSubDepartment(o[9].toString());
				}
				
				if(o[10] != null){
					questionSearchVO.setStatusType(o[10].toString());
				}
				
				if(o[11] != null){
					Long sessionTypeId = new Long(o[11].toString());
					SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
					if(sessionType != null){
						questionSearchVO.setSessionType(sessionType.getSessionType());
					}
				}
				
				if(o[12] != null){					
					questionSearchVO.setSessionYear(o[12].toString());
				}
				
				questionSearchVOs.add(questionSearchVO);			
			}
		}

		return questionSearchVOs;
	}
	
	public List<BillSearchVO> fullTextSearchReferencingBill(final String param, final Bill bill, 
			final String language, final int start,final int noOfRecords,final String locale) {
		
		List<BillSearchVO> billSearchVOs = new ArrayList<BillSearchVO>();
		
		String actSelectQuery = "SELECT a.id as actId, a.number as actNumber," +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS actTitle," +
				" lang.type AS languageType,a.year" +			
				" FROM acts as a " +	
				" LEFT JOIN `acts_titles` AS at ON (at.act_id = a.id)" +
				" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = at.title_id)" +
				" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id)" +
				" WHERE lang.type = '" + language + "'";
		
		/**** fulltext query ****/
		String searchQuery = null;	
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND ((match(titleDraft.text) against('"+param+"' in natural language mode))" +
					" || titleDraft.text LIKE '"+param+"%')";					
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" WHERE ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode)))";				
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" WHERE ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode)))";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" WHERE ((match(titleDraft.text) against('"+param+"' in boolean mode)))";
		}
		
		/**** Order By Query ****/
		String orderByQuery=" ORDER BY a.year "+ApplicationConstants.ASC + ", a.number "+ApplicationConstants.ASC + ", lang.id "+ApplicationConstants.ASC;
		
		/**** Final Query ****/
		String finalQuery="SELECT rs.actId,rs.actNumber,rs.actTitle,rs.year" +
				" FROM ("+actSelectQuery+searchQuery+orderByQuery+") as rs";// LIMIT "+start+","+noOfRecords;
		
		List resultList=this.em().createNativeQuery(finalQuery).getResultList();
		
		if(resultList!=null && !resultList.isEmpty()) {
			for(Object i : resultList){
				Object[] o = (Object[]) i;
				BillSearchVO billSearchVO = new BillSearchVO();
				if(o[0] != null){
					billSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1] != null){
					billSearchVO.setNumber(o[1].toString());
				}
				if(o[2] != null){
					billSearchVO.setTitle(o[2].toString());
				}
				if(o[3] != null){					
					billSearchVO.setSessionYear(o[3].toString());
				}
				billSearchVO.setDeviceType("act");
				billSearchVOs.add(billSearchVO);
			}
		}
		
		String houseType = bill.getHouseType().getType();	
		
		Status statusRejected = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION,bill.getLocale());	
		
		StringBuffer sb = new StringBuffer();
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(bill.getSession().getHouse(), bill.getSession().getHouse().getFirstDate(), bill.getSession().getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(bill.getSession().getHouse(), bill.getLocale());
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(bill.getSubmissionDate());
		
		String selectQuery = "SELECT b.id as billId, b.number as billNumber," +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS billTitle," +
				" (CASE WHEN (lang.id=revisedTitleDraft.language_id) THEN revisedTitleDraft.text ELSE NULL END) AS billRevisedTitle," +
				" (CASE WHEN (lang.id=contentDraft.language_id) THEN contentDraft.text ELSE NULL END) AS billContent, " +
				" (CASE WHEN (lang.id=revisedContentDraft.language_id) THEN revisedContentDraft.text ELSE NULL END) AS billRevisedContent," +
				" lang.type AS languageType," +			
				" st.name as billStatus, dt.name as billDeviceType," +	
				" mi.name as billMinistry, sd.name as billSubDepartment, st.type as billStatusType, s.sessiontype_id as billSessionType,s.session_year as billSessionYear," +
				" CASE " +
				"		WHEN b.status_id="+statusRejected.getId().toString() + " THEN b.rejection_date " +
				"		ELSE b.admission_date " +
				" END as billDate " +
				" FROM bills as b " +	
				" LEFT JOIN housetypes as ht ON(b.housetype_id=ht.id)" +
				" LEFT JOIN sessions as s ON(b.session_id=s.id)" +
				" LEFT JOIN members as m ON(b.member_id=m.id)" +
				" LEFT JOIN status as st ON(b.recommendationstatus_id=st.id)" +
				" LEFT JOIN status as ist ON(b.internalstatus_id=ist.id)" +
				" LEFT JOIN devicetypes as dt ON(b.devicetype_id=dt.id)" +
				" LEFT JOIN ministries as mi ON(b.ministry_id=mi.id)" +				
				" LEFT JOIN subdepartments as sd ON(b.subdepartment_id=sd.id)" +
				" LEFT JOIN `bills_titles` AS bt ON (bt.bill_id = b.id)" +
				" LEFT JOIN `bills_revisedtitles` AS brt ON (brt.bill_id = b.id)" +
				" LEFT JOIN `bills_contentdrafts` AS bc ON (bc.bill_id = b.id)" +
				" LEFT JOIN `bills_revisedcontentdrafts` AS brc ON (brc.bill_id = b.id)" +
				" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = bt.title_id)" +
				" LEFT JOIN `text_drafts` AS revisedTitleDraft ON (revisedTitleDraft.id = brt.revised_title_id)" +
				" LEFT JOIN `text_drafts` AS contentDraft ON (contentDraft.id = bc.content_draft_id)" +
				" LEFT JOIN `text_drafts` AS revisedContentDraft ON (revisedContentDraft.id = brc.revised_content_draft_id)" +
				" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id OR lang.id = revisedTitleDraft.language_id" +
				" OR lang.id = contentDraft.language_id OR lang.id = revisedContentDraft.language_id)" + 
				" WHERE" +						
				" b.id <> " + bill.getId() + " AND b.parent is NULL" + 
				" AND b.housetype_id="+bill.getHouseType().getId().toString() + 
				" AND (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_PROCESSED_INTRODUCED+"')" + 
				")" +
				" AND st.type<>'"+ApplicationConstants.BILL_FINAL_NEGATIVED + "'" +
				" AND CASE " +
				" WHEN (dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + 
				"' 	AND ht.type='"+ApplicationConstants.LOWER_HOUSE +
				"' ) THEN s.id IN (" + sb + ") " +					
				" WHEN (dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + 
				"' 	AND ht.type='"+ApplicationConstants.UPPER_HOUSE +
				"' ) THEN m.id IN (" + sb + ") " +
				" WHEN dt.type='"+ApplicationConstants.GOVERNMENT_BILL + 					
				"' 	THEN year(b.submission_date)="+calendar.get(Calendar.YEAR) + 
				" END " +
				" AND lang.type = '" + language + "'";
		
		/**** fulltext query ****/
		searchQuery = "";	
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND ((match(titleDraft.text) against('"+param+"' in natural language mode))" +
					" || (match(revisedTitleDraft.text) against('"+param+"' in natural language mode))" + 
					" || (match(contentDraft.text) against('"+param+"' in natural language mode))" + 
					" || (match(revisedContentDraft.text) against('"+param+"' in natural language mode))" + 
					" || titleDraft.text LIKE '"+param+"%' || revisedTitleDraft.text LIKE '"+param+"%'" +
					" || contentDraft.text LIKE '"+param+"%' || revisedContentDraft.text LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))" +
					" || (match(revisedTitleDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(contentDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(revisedContentDraft.text) against('"+buffer.toString()+"' in boolean mode))";				
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND ((match(titleDraft.text) against('"+buffer.toString()+"' in boolean mode))" +
					" || (match(revisedTitleDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(contentDraft.text) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(revisedContentDraft.text) against('"+buffer.toString()+"' in boolean mode))";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND ((match(titleDraft.text) against('"+param+"' in boolean mode))" +
					" || (match(revisedTitleDraft.text) against('"+param+"' in boolean mode))" + 
					" || (match(contentDraft.text) against('"+param+"' in boolean mode))" + 
					" || (match(revisedContentDraft.text) against('"+param+"' in boolean mode))";
		}
		
		/**** Order By Query ****/
		orderByQuery=" ORDER BY b.submission_date "+ApplicationConstants.ASC + ", lang.id "+ApplicationConstants.ASC;
		
		/**** Final Query ****/
		finalQuery="SELECT rs.billId,rs.billNumber,rs.billTitle,rs.billRevisedTitle,rs.billContent, "+
				" rs.billRevisedContent,rs.languageType,rs.billStatus,rs.billDeviceType," + 
				" rs.billMinistry,rs.billSubDepartment,rs.billStatusType, rs.billSessionType, rs.billSessionYear," + 
				" rs.billDate FROM ("+selectQuery+searchQuery+orderByQuery+") as rs";// LIMIT "+start+","+noOfRecords;
				
		resultList=this.em().createNativeQuery(finalQuery).getResultList();
		String billId = "";
		BillSearchVO billSearchVO = new BillSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				if(!billId.equals(o[0].toString())) {
					if(!billSearchVOs.isEmpty()) {
						billSearchVO = new BillSearchVO();
					}
					billSearchVOs.add(billSearchVO);
					if(o[0] != null){
						billSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1] != null){
						billSearchVO.setNumber(o[1].toString());
					}
					if(o[7] != null){
						billSearchVO.setStatus(o[7].toString());
					}
					if(o[8] != null){
						billSearchVO.setDeviceType(o[8].toString());
					}					
					if(o[9] != null){
						billSearchVO.setMinistry(o[9].toString());
					}					
					if(o[10] != null){
						billSearchVO.setSubDepartment(o[10].toString());
					}					
					if(o[11] != null){
						billSearchVO.setStatusType(o[11].toString());
					}					
					if(o[12] != null){
						Long sessionTypeId = new Long(o[12].toString());
						SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
						if(sessionType != null){
							billSearchVO.setSessionType(sessionType.getSessionType());
						}
					}					
					if(o[13] != null){					
						billSearchVO.setSessionYear(o[13].toString());
					}					
					if(o[14] != null){	
						Date dateOfBill = FormaterUtil.formatStringToDate(o[14].toString(), "yyyy-MM-dd HH:mm:ss");
						String dateOfBillStr = FormaterUtil.formatDateToString(dateOfBill, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
						billSearchVO.setDateOfBill(dateOfBillStr);
					}
//					if(o[15] != null){	
//						Date admissionDate = FormaterUtil.formatStringToDate(o[15].toString(), "yyyy-MM-dd HH:mm:ss");
//						String admissionDateStr = FormaterUtil.formatDateToString(admissionDate, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
//						billSearchVO.setAdmissionDate(admissionDateStr);
//					}					
					if(o[2] != null){
						billSearchVO.setTitle(higlightText(o[2].toString(), param));
					}
					if(o[3] != null){
						billSearchVO.setRevisedTitle(higlightText(o[3].toString(), param));
					}
					if(o[4] != null){
						billSearchVO.setContent(higlightText(o[4].toString(), param));
					}
					if(o[5] != null){
						billSearchVO.setRevisedContent(higlightText(o[5].toString(), param));
					}					
					billId = o[0].toString();
				} else {
					if(o[2] != null){
						billSearchVO.setTitle(higlightText(o[2].toString(), param));
					}
					if(o[3] != null){
						billSearchVO.setRevisedTitle(higlightText(o[3].toString(), param));
					}
					if(o[4] != null){
						billSearchVO.setContent(higlightText(o[4].toString(), param));
					}
					if(o[5] != null){
						billSearchVO.setRevisedContent(higlightText(o[5].toString(), param));
					}
				}											
			}
		}
		//return only required number of records
		List<BillSearchVO> resultBillSearchVOs = new ArrayList<BillSearchVO>();
		for(int i=start; i<billSearchVOs.size(); i++) {
			if(i<noOfRecords) {
				resultBillSearchVOs.add(billSearchVOs.get(i));
			} else {
				break;
			}
		}
		return resultBillSearchVOs;		
	}
	
	public List<BillSearchVO> exactSearchReferencingBill(final Bill bill, 
			final String language, final int start,final int noOfRecords,final String locale) {
		
		List<BillSearchVO> billSearchVOs = new ArrayList<BillSearchVO>();
		
		String actSelectQuery = "SELECT a.id as actId, a.number as actNumber," +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS actTitle," +
				" lang.type AS languageType,a.year" +			
				" FROM acts as a " +	
				" LEFT JOIN `acts_titles` AS at ON (at.act_id = a.id)" +
				" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = at.title_id)" +
				" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id)" + 
				" WHERE lang.type = '" + language + "'";	
		
		/**** exact search query ****/		
		String exactSearchQuery="";
		
		String languagesAllowedInSession = bill.getSession().getParameter(bill.getType().getType() + "_languagesAllowed");
		exactSearchQuery += " AND (";
		int cnt = 1;
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			exactSearchQuery += "(titleDraft.text IS NOT NULL AND titleDraft.text<>'' AND titleDraft.text='"+bill.findTextOfGivenDraftTypeInGivenLanguage("title", languageAllowedInSession)+"')";
			
			if(cnt!=languagesAllowedInSession.split("#").length) {
				exactSearchQuery += " || ";
				cnt++;
			}				
		}
		exactSearchQuery += ")";
		
		/**** Order By Query ****/
		String orderByQuery=" ORDER BY a.year "+ApplicationConstants.ASC + ", a.number "+ApplicationConstants.ASC + ", lang.id "+ApplicationConstants.ASC;
		
		/**** Final Query ****/
		String finalQuery="SELECT rs.actId,rs.actNumber,rs.actTitle,rs.year" +
				" FROM ("+actSelectQuery+exactSearchQuery+orderByQuery+") as rs";// LIMIT "+start+","+noOfRecords;
				
		List resultList=this.em().createNativeQuery(finalQuery).getResultList();
		
		if(resultList!=null && !resultList.isEmpty()) {
			for(Object i : resultList){
				Object[] o = (Object[]) i;
				BillSearchVO billSearchVO = new BillSearchVO();
				if(o[0] != null){
					billSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1] != null){
					billSearchVO.setNumber(o[1].toString());
				}
				if(o[2] != null){
					billSearchVO.setTitle(o[2].toString());
				}
				if(o[3] != null){					
					billSearchVO.setSessionYear(o[3].toString());
				}
				billSearchVO.setDeviceType("act");
				billSearchVOs.add(billSearchVO);
			}
		}
		
		String houseType = bill.getHouseType().getType();	
		
		Status statusRejected = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION,bill.getLocale());	
		
		StringBuffer sb = new StringBuffer();
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			List<Session> checkSessions = Session.findSessionsByHouseAndDateLimits(bill.getSession().getHouse(), bill.getSession().getHouse().getFirstDate(), bill.getSession().getStartDate());
			System.out.println(checkSessions.size());					
			int index = 1;
			for(Session ss: checkSessions){
				sb.append(ss.getId());
				if(index < checkSessions.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkSessions = null;
		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			List<Member> checkMembers = HouseMemberRoleAssociation.findAllActiveMembersInHouse(bill.getSession().getHouse(), bill.getLocale());
			System.out.println(checkMembers.size());
			int index = 1;
			for(Member m: checkMembers){
				sb.append(m.getId());
				if(index < checkMembers.size()){
					sb.append(",");
				}else{
					break;
				}
				index++;
			}			
			checkMembers = null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(bill.getSubmissionDate());
		
		String selectQuery = "SELECT b.id as billId, b.number as billNumber," +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS billTitle," +
				" (CASE WHEN (lang.id=revisedTitleDraft.language_id) THEN revisedTitleDraft.text ELSE NULL END) AS billRevisedTitle," +
				" (CASE WHEN (lang.id=contentDraft.language_id) THEN contentDraft.text ELSE NULL END) AS billContent, " +
				" (CASE WHEN (lang.id=revisedContentDraft.language_id) THEN revisedContentDraft.text ELSE NULL END) AS billRevisedContent," +
				" lang.type AS languageType," +			
				" st.name as billStatus, dt.name as billDeviceType," +	
				" mi.name as billMinistry, sd.name as billSubDepartment, st.type as billStatusType, s.sessiontype_id as billSessionType,s.session_year as billSessionYear," +
				" CASE " +
				"		WHEN b.status_id="+statusRejected.getId().toString() + " THEN b.rejection_date " +
				"		ELSE b.admission_date " +
				" END as billDate " +
				" FROM bills as b " +	
				" LEFT JOIN housetypes as ht ON(b.housetype_id=ht.id)" +
				" LEFT JOIN sessions as s ON(b.session_id=s.id)" +
				" LEFT JOIN members as m ON(b.member_id=m.id)" +
				" LEFT JOIN status as st ON(b.recommendationstatus_id=st.id)" +
				" LEFT JOIN status as ist ON(b.internalstatus_id=ist.id)" +
				" LEFT JOIN devicetypes as dt ON(b.devicetype_id=dt.id)" +
				" LEFT JOIN ministries as mi ON(b.ministry_id=mi.id)" +				
				" LEFT JOIN subdepartments as sd ON(b.subdepartment_id=sd.id)" +
				" LEFT JOIN `bills_titles` AS bt ON (bt.bill_id = b.id)" +
				" LEFT JOIN `bills_revisedtitles` AS brt ON (brt.bill_id = b.id)" +
				" LEFT JOIN `bills_contentdrafts` AS bc ON (bc.bill_id = b.id)" +
				" LEFT JOIN `bills_revisedcontentdrafts` AS brc ON (brc.bill_id = b.id)" +
				" LEFT JOIN `text_drafts` AS titleDraft ON (titleDraft.id = bt.title_id)" +
				" LEFT JOIN `text_drafts` AS revisedTitleDraft ON (revisedTitleDraft.id = brt.revised_title_id)" +
				" LEFT JOIN `text_drafts` AS contentDraft ON (contentDraft.id = bc.content_draft_id)" +
				" LEFT JOIN `text_drafts` AS revisedContentDraft ON (revisedContentDraft.id = brc.revised_content_draft_id)" +
				" LEFT JOIN languages AS lang ON (lang.id = titleDraft.language_id OR lang.id = revisedTitleDraft.language_id" +
				" OR lang.id = contentDraft.language_id OR lang.id = revisedContentDraft.language_id)" + 
				" WHERE" +						
				" b.id <> " + bill.getId() + " AND b.parent is NULL" + 
				" AND b.housetype_id="+bill.getHouseType().getId().toString() + 
				" AND (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_PROCESSED_INTRODUCED+"')" + 
				")" +
				" AND st.type<>'"+ApplicationConstants.BILL_FINAL_NEGATIVED + "'" +
				" AND CASE " +
				" WHEN (dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + 
				"' 	AND ht.type='"+ApplicationConstants.LOWER_HOUSE +
				"' ) THEN s.id IN (" + sb + ") " +					
				" WHEN (dt.type='"+ApplicationConstants.NONOFFICIAL_BILL + 
				"' 	AND ht.type='"+ApplicationConstants.UPPER_HOUSE +
				"' ) THEN m.id IN (" + sb + ") " +
				" WHEN dt.type='"+ApplicationConstants.GOVERNMENT_BILL + 					
				"' 	THEN year(b.submission_date)="+calendar.get(Calendar.YEAR) + 
				" END " +
				" AND lang.type = '" + language + "'";
		
		/**** exact search query ****/		
		exactSearchQuery="";
		
		exactSearchQuery += " AND (";
		cnt = 1;
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			exactSearchQuery += "(titleDraft.text IS NOT NULL AND titleDraft.text<>'' AND titleDraft.text='"+bill.findTextOfGivenDraftTypeInGivenLanguage("title", languageAllowedInSession)+"')" +
					" || (revisedTitleDraft.text IS NOT NULL AND revisedTitleDraft.text<>'' AND revisedTitleDraft.text='"+bill.findTextOfGivenDraftTypeInGivenLanguage("revised_title", languageAllowedInSession)+"')" +
					" || (contentDraft.text IS NOT NULL AND contentDraft.text<>'' AND contentDraft.text='"+bill.findTextOfGivenDraftTypeInGivenLanguage("contentDraft", languageAllowedInSession)+"')" +
					" || (revisedContentDraft.text IS NOT NULL AND revisedContentDraft.text<>'' AND revisedContentDraft.text='"+bill.findTextOfGivenDraftTypeInGivenLanguage("revised_contentDraft", languageAllowedInSession)+"')";
			
			if(cnt!=languagesAllowedInSession.split("#").length) {
				exactSearchQuery += " || ";
				cnt++;
			}				
		}
		exactSearchQuery += ")";	
		
		/**** Order By Query ****/
		orderByQuery=" ORDER BY b.submission_date "+ApplicationConstants.ASC + ", lang.id "+ApplicationConstants.ASC;
		
		/**** Final Query ****/
		finalQuery="SELECT rs.billId,rs.billNumber,rs.billTitle,rs.billRevisedTitle,rs.billContent, "+
				" rs.billRevisedContent,rs.languageType,rs.billStatus,rs.billDeviceType," + 
				" rs.billMinistry,rs.billSubDepartment,rs.billStatusType, rs.billSessionType, rs.billSessionYear," + 
				" rs.billDate FROM ("+selectQuery+exactSearchQuery+orderByQuery+") as rs";// LIMIT "+start+","+noOfRecords;
				
		resultList=this.em().createNativeQuery(finalQuery).getResultList();
		String billId = "";
		BillSearchVO billSearchVO = new BillSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				if(!billId.equals(o[0].toString())) {
					if(!billSearchVOs.isEmpty()) {
						billSearchVO = new BillSearchVO();
					}
					billSearchVOs.add(billSearchVO);
					if(o[0] != null){
						billSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1] != null){
						billSearchVO.setNumber(o[1].toString());
					}
					if(o[7] != null){
						billSearchVO.setStatus(o[7].toString());
					}
					if(o[8] != null){
						billSearchVO.setDeviceType(o[8].toString());
					}					
					if(o[9] != null){
						billSearchVO.setMinistry(o[9].toString());
					}					
					if(o[10] != null){
						billSearchVO.setSubDepartment(o[10].toString());
					}					
					if(o[11] != null){
						billSearchVO.setStatusType(o[11].toString());
					}					
					if(o[12] != null){
						Long sessionTypeId = new Long(o[12].toString());
						SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
						if(sessionType != null){
							billSearchVO.setSessionType(sessionType.getSessionType());
						}
					}					
					if(o[13] != null){					
						billSearchVO.setSessionYear(o[13].toString());
					}					
					if(o[14] != null){	
						Date dateOfBill = FormaterUtil.formatStringToDate(o[14].toString(), "yyyy-MM-dd HH:mm:ss");
						String dateOfBillStr = FormaterUtil.formatDateToString(dateOfBill, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
						billSearchVO.setDateOfBill(dateOfBillStr);
					}
//					if(o[15] != null){	
//						Date admissionDate = FormaterUtil.formatStringToDate(o[15].toString(), "yyyy-MM-dd HH:mm:ss");
//						String admissionDateStr = FormaterUtil.formatDateToString(admissionDate, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
//						billSearchVO.setAdmissionDate(admissionDateStr);
//					}					
					if(o[2] != null){
						billSearchVO.setTitle(o[2].toString());
					}
					if(o[3] != null){
						billSearchVO.setRevisedTitle(o[3].toString());
					}
					if(o[4] != null){
						billSearchVO.setContent(o[4].toString());
					}
					if(o[5] != null){
						billSearchVO.setRevisedContent(o[5].toString());
					}					
					billId = o[0].toString();
				} else {
					if(o[2] != null){
						billSearchVO.setTitle(o[2].toString());
					}
					if(o[3] != null){
						billSearchVO.setRevisedTitle(o[3].toString());
					}
					if(o[4] != null){
						billSearchVO.setContent(o[4].toString());
					}
					if(o[5] != null){
						billSearchVO.setRevisedContent(o[5].toString());
					}
				}											
			}
		}
		
		//return only required number of records
		List<BillSearchVO> resultBillSearchVOs = new ArrayList<BillSearchVO>();
		for(int i=start; i<billSearchVOs.size(); i++) {
			if(i<noOfRecords) {
				resultBillSearchVOs.add(billSearchVOs.get(i));
			} else {
				break;
			}
		}
		return resultBillSearchVOs;		
	}
	
	//=========================
	
	public Boolean referencing(final String device,final Long primaryId,final Long referencingId,final String locale) {
		try {
			if(device.startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
				Question primaryQuestion=Question.findById(Question.class,primaryId);
				Question referencedQuestion=Question.findById(Question.class,referencingId);
				
				if(device.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					ReferencedEntity refEntity=new ReferencedEntity();
										
					refEntity = new ReferencedEntity();
					refEntity.setDevice(referencedQuestion);
					refEntity.setLocale(referencedQuestion.getLocale());
					refEntity.setDeviceType(referencedQuestion.getType());
					refEntity.persist();
					
					primaryQuestion.setReferencedHDS(refEntity);
					primaryQuestion.simpleMerge();
					
				}else{
					List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
					referencedEntities=primaryQuestion.getReferencedEntities();		
					boolean alreadyRefered=false;
					int position=0;
					for(ReferencedEntity i:referencedEntities){
						
						if(i.getDeviceType() != null){
							if(i.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
								if(((Question)i.getDevice()).getId()==referencedQuestion.getId()){
									alreadyRefered=true;
								}
								position++;
								
								if(!alreadyRefered){
									ReferencedEntity referencedEntity=new ReferencedEntity();
									referencedEntity.setLocale(referencedQuestion.getLocale());
									referencedEntity.setDevice(referencedQuestion);
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
							}
						}
					}
				}
			}else if(device.startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
				Resolution primaryResolution=Resolution.findById(Resolution.class,primaryId);
				Resolution referencedResolution=Resolution.findById(Resolution.class,referencingId);
				ReferencedEntity refEntity=new ReferencedEntity();
				
				
				refEntity = new ReferencedEntity();
				refEntity.setDevice(referencedResolution);
				refEntity.setLocale(referencedResolution.getLocale());
				refEntity.setDevice(referencedResolution);
				refEntity.setDeviceType(referencedResolution.getType());
				refEntity.persist();
				
				primaryResolution.setReferencedResolution(refEntity);
				
				/*Status status=Status.findByFieldName(Status.class,"type","resolution_contains_reference", locale);
				if(primaryResolution.getHouseType() != null){
					if(primaryResolution.getHouseType().getType() != null){
						if(!primaryResolution.getHouseType().getType().isEmpty()){
							if(primaryResolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								primaryResolution.setInternalStatusLowerHouse(status);
								primaryResolution.setRecommendationStatusLowerHouse(status);
							}else if(primaryResolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								primaryResolution.setInternalStatusUpperHouse(status);
								primaryResolution.setRecommendationStatusUpperHouse(status);
							}
						}
					}
				}*/
				primaryResolution.simpleMerge();
				
			}else if(device.startsWith(ApplicationConstants.DEVICE_BILLS)){
				Bill primaryBill=Bill.findById(Bill.class,primaryId);
				Bill referencedBill=Bill.findById(Bill.class,referencingId);
				
				ReferencedEntity refEntity = new ReferencedEntity();
				refEntity.setDevice(referencedBill);
				refEntity.setLocale(referencedBill.getLocale());				
				refEntity.setDeviceType(referencedBill.getType());
				refEntity.persist();
				
				primaryBill.setReferencedBill(refEntity);				
				primaryBill.simpleMerge();	
				
			}else if(device.startsWith(ApplicationConstants.DEVICE_ACTS)){
				Bill primaryBill=Bill.findById(Bill.class,primaryId);
				Act referencedAct=Act.findById(Act.class,referencingId);
				
				ReferencedEntity refEntity = new ReferencedEntity();
				refEntity.setDevice(referencedAct);
				refEntity.setLocale(referencedAct.getLocale());							
				refEntity.persist();
				
				primaryBill.setReferencedBill(refEntity);				
				primaryBill.simpleMerge();				
			}else if(device.startsWith(ApplicationConstants.DEVICE_MOTIONS)){
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	@Transactional
	public Boolean deReferencing(final String device,final Long primaryId,final Long referencingId,final String locale) {
		try {
			if(device.startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
				Question primaryQuestion=Question.findById(Question.class,primaryId);
				Question referencedQuestion=Question.findById(Question.class,referencingId);
				
				if(device.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					if(primaryQuestion.getReferencedHDS() != null){
						ReferencedEntity.getReferencedEntityRepository().removeById(primaryQuestion.getReferencedHDS().getId());
						primaryQuestion.setReferencedHDS(null);
						primaryQuestion.simpleMerge();
					}
					
				}else{
					List<ReferencedEntity> referencedEntities=new ArrayList<ReferencedEntity>();
					List<ReferencedEntity> newReferencedEntities=new ArrayList<ReferencedEntity>();
					referencedEntities=primaryQuestion.getReferencedEntities();
					ReferencedEntity referencedEntityToRemove=null;
					for(ReferencedEntity i:referencedEntities){
						
						if(i.getDeviceType() != null){
							if(i.getDeviceType().getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
								if(((Question)i.getDevice()).getId()==referencedQuestion.getId()){
									referencedEntityToRemove=i;				
								}else{
									newReferencedEntities.add(i);
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
							}
						}
					}
				}
			}else if(device.startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
				
				if(device.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					Resolution primaryResolution=Resolution.findById(Resolution.class,primaryId);					
					if(primaryResolution.getReferencedResolution() != null){
						ReferencedEntity refEntityToBeRemoved = primaryResolution.getReferencedResolution();
						primaryResolution.setReferencedResolution(null);						
						primaryResolution.simpleMerge();
						//refEntityToBeRemoved.remove();
					}
				}
			}else if(device.startsWith(ApplicationConstants.DEVICE_BILLS)){				
				if(device.equals(ApplicationConstants.NONOFFICIAL_BILL)){
					Bill primaryBill=Bill.findById(Bill.class,primaryId);					
					if(primaryBill.getReferencedBill() != null){
						ReferencedEntity refEntityToBeRemoved = primaryBill.getReferencedBill();
						primaryBill.setReferencedBill(null);						
						primaryBill.simpleMerge();
						//refEntityToBeRemoved.remove();
					}
				}
			}else if(device.startsWith(ApplicationConstants.DEVICE_ACTS)){
				Bill primaryBill=Bill.findById(Bill.class,primaryId);					
				if(primaryBill.getReferencedBill() != null){
					ReferencedEntity refEntityToBeRemoved = primaryBill.getReferencedBill();
					primaryBill.setReferencedBill(null);						
					primaryBill.simpleMerge();
					//refEntityToBeRemoved.remove();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
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
}
