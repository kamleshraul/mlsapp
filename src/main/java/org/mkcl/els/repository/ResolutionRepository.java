package org.mkcl.els.repository;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.ResolutionRevisionVO;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.ResolutionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.Question.CLUBBING_STATE;
import org.mkcl.els.domain.Question.STARRED_STATE;
import org.springframework.stereotype.Repository;

import com.trg.search.Filter;
import com.trg.search.Search;

@Repository
public class ResolutionRepository extends BaseRepository<Resolution, Long>{

	public Integer assignResolutionNo(final HouseType houseType, final Session session,
			final DeviceType resolutionType, final String locale) throws ELSException {
		String strHouseType = houseType.getType();
		String strResolutionType = resolutionType.getType();
		String strQuery = null;
		String lowerHouseFormationDate=null;
		House lowerHouse=null;
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)) {
			strQuery = "SELECT r" +
					" FROM Resolution r JOIN r.session s JOIN r.type rt WHERE s.id =:sessionId " +
					" AND rt.type =:resolutionType ORDER BY r.number " +ApplicationConstants.DESC;
		}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
//			Session lowerHouseSession = Session.find(session.getYear(),
//					session.getType().getType(), ApplicationConstants.LOWER_HOUSE);
			lowerHouse = Session.findCorrespondingAssemblyHouseForCouncilSession(session);

			CustomParameter dbDateFormat =
					CustomParameter.findByName(CustomParameter.class,"DB_DATETIMEFORMAT", "");
			SimpleDateFormat simpleDateFormat =
					FormaterUtil.getDateFormatter(dbDateFormat.getValue(),"en_US");
			lowerHouseFormationDate = simpleDateFormat.format(lowerHouse.getFormationDate());
			strQuery = "SELECT r FROM Resolution r JOIN r.session s JOIN r.type dt JOIN r.houseType ht" +
					" WHERE ht.type =:houseType AND s.id=:sessionId"+
					" AND r.submissionDate >= :lowerHouseFormationDate" +
					" AND dt.type=:resolutionType ORDER BY r.number " + ApplicationConstants.DESC;
		}
		try {
			Query query=this.em().createQuery(strQuery);
			if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
				query.setParameter("sessionId", session.getId());
				query.setParameter("resolutionType",resolutionType.getType());
			}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
				query.setParameter("sessionId", session.getId());
				query.setParameter("resolutionType",strResolutionType);
				query.setParameter("lowerHouseFormationDate", lowerHouse.getFormationDate());
				query.setParameter("houseType", strHouseType);
			}
			@SuppressWarnings("unchecked")
			List<Resolution> resolutions = query.setFirstResult(0).setMaxResults(1).getResultList();
			if(resolutions == null) {
				return 0;
			}
			else if(resolutions.isEmpty()) {
				return 0;
			}
			else {
				if(resolutions.get(0).getNumber() == null) {
					return 0;
				}
				else {
					return resolutions.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Integer_assignResolutionNo", "Cannot assigne the number to resolution");
			throw elsException;
		}
	}

	public List<RevisionHistoryVO> getRevisions(final Long resolutionId,
			final String locale) throws ELSException {
		try{
		org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.RESOLUTION_GET_REVISION, "");
		String strquery = nativeQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("resolutionId",resolutionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> resolutionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO resolutionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				resolutionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				resolutionRevisionVO.setEditedAs(userGroupType.getName());
			}
			resolutionRevisionVO.setEditedBY(o[1].toString());
			resolutionRevisionVO.setEditedOn(o[2].toString());
			resolutionRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String resolution=d.diff_prettyHtml(diff);
					if(resolution.contains("&lt;")){
						resolution=resolution.replaceAll("&lt;", "<");
					}
					if(resolution.contains("&gt;")){
						resolution=resolution.replaceAll("&gt;", ">");
					}
					if(resolution.contains("&amp;nbsp;")){
						resolution=resolution.replaceAll("&amp;nbsp;"," ");
					}
					resolutionRevisionVO.setDetails(resolution);
				}else{
					resolutionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				resolutionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty() && o1[5].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[5].toString(), o[5].toString());
					String resolution=d.diff_prettyHtml(diff);
					if(resolution.contains("&lt;")){
						resolution=resolution.replaceAll("&lt;", "<");
					}
					if(resolution.contains("&gt;")){
						resolution=resolution.replaceAll("&gt;", ">");
					}
					if(resolution.contains("&amp;nbsp;")){
						resolution=resolution.replaceAll("&amp;nbsp;"," ");
					}
					resolutionRevisionVO.setSubject(resolution);
				}else{
					resolutionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				resolutionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				resolutionRevisionVO.setRemarks(o[6].toString());
			}

			resolutionRevisionVOs.add(resolutionRevisionVO);
		}
		return resolutionRevisionVOs;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<RevisionHistoryVO>_getRevisions", "Cannot get the Resolution revisions");
			throw elsException;
	    }
	}

	public List<RevisionHistoryVO> getRevisions(final Long resolutionId,final Long workflowHouseTypeId,final String locale) throws ELSException {
		try{
		org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.RESOLUTION_GET_REVISION_WITH_WORKFLOWHOUSETYPE, "");
		String strquery = nativeQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("resolutionId",resolutionId);
		query.setParameter("workflowHouseTypeId",workflowHouseTypeId);
		List results = query.getResultList();
		List<RevisionHistoryVO> resolutionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO resolutionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				resolutionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				resolutionRevisionVO.setEditedAs(userGroupType.getName());
			}
			resolutionRevisionVO.setEditedBY(o[1].toString());
			resolutionRevisionVO.setEditedOn(o[2].toString());
			resolutionRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(!o[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String resolution=d.diff_prettyHtml(diff);
					if(resolution.contains("&lt;")){
						resolution=resolution.replaceAll("&lt;", "<");
					}
					if(resolution.contains("&gt;")){
						resolution=resolution.replaceAll("&gt;", ">");
					}
					if(resolution.contains("&amp;nbsp;")){
						resolution=resolution.replaceAll("&amp;nbsp;"," ");
					}
					resolutionRevisionVO.setDetails(resolution);
				}else{
					resolutionRevisionVO.setDetails(o1[4].toString());
				}

			}else{
				resolutionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[5].toString(), o[5].toString());
					String resolution=d.diff_prettyHtml(diff);
					if(resolution.contains("&lt;")){
						resolution=resolution.replaceAll("&lt;", "<");
					}
					if(resolution.contains("&gt;")){
						resolution=resolution.replaceAll("&gt;", ">");
					}
					if(resolution.contains("&amp;nbsp;")){
						resolution=resolution.replaceAll("&amp;nbsp;"," ");
					}
					resolutionRevisionVO.setSubject(resolution);
				}else{
					resolutionRevisionVO.setSubject(o1[5].toString());
				}
			}else{
				resolutionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				resolutionRevisionVO.setRemarks(o[6].toString());
			}
			if(o[7] != null){
				Date discussionDate = (Date) o[7];
				resolutionRevisionVO.setDiscussionDate(FormaterUtil.formatDateToString(discussionDate, ApplicationConstants.DB_DATEFORMAT));
			}						
			resolutionRevisionVOs.add(resolutionRevisionVO);
		}
		return resolutionRevisionVOs;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<RevisionHistoryVO>_getRevisions", "Cannot get the Resolution revisions");
			throw elsException;
	    }
	}

	//Applicable for non rejected resolutions
	public Integer findMemberResolutionCountByNumber(final Long memberId,final Long sessionId,final String locale) throws ELSException {
		String strQuery="SELECT r FROM Resolution r" +
				" WHERE r.session.id=:sessionId"+
				" AND r.member.id=:memberId" +
				" AND r.locale=:locale"+
				" AND (r.statusLowerHouse.id!=:statusLowerHouseId OR r.statusUpperHouse.id!=:statusUpperHouseId)"+
				" AND r.number IS NOT NULL";
		try{
			Status resolutionFinalRejection = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale);
			Query query=this.em().createQuery(strQuery);
			query.setParameter("sessionId", sessionId);
			query.setParameter("memberId", memberId);
			query.setParameter("locale", locale);
			query.setParameter("statusLowerHouseId", resolutionFinalRejection.getId());
			query.setParameter("statusUpperHouseId", resolutionFinalRejection.getId());
			List<Resolution> resolutions = query.getResultList();
			if(resolutions.isEmpty()){
				return 0;
			}else{
				return resolutions.size();
			}
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Integer_findMemberResolutionCountByNumber", "Cannot get the Resolution Count");
			throw elsException;
	    }	
	}

	public ResolutionDraft findLatestResolutionDraftOfUser(final Long resolutionId,final String username) throws ELSException {
		ResolutionDraft resolutionDraft = null;
		try{
			org.mkcl.els.domain.Query nativeQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.RESOLUTION_GET_LATEST_RESOLUTIONDRAFT_OF_USER, "");
			String strquery = nativeQuery.getQuery();
			Query query=this.em().createNativeQuery(strquery);
			query.setParameter("resolutionId",resolutionId);
			query.setParameter("username",username);
			List result = query.getResultList();
			for(Object i : result) {
				Long draftId = Long.parseLong(i.toString());
				resolutionDraft = ResolutionDraft.findById(ResolutionDraft.class, draftId);			
				break;
			}
			return resolutionDraft;	
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_ResolutionDraft_findLatestResolutionDraftOfUser", "Cannot get the ResolutionDraft");
			throw elsException;
	    }	
	}

	public Long findResolutionForUniqueMemberSubject(final Long memberId, final Long sessionId, final String subject,final String locale) throws ELSException {
		String strQuery="SELECT r FROM Resolution r WHERE r.member.id=:memberId AND r.subject=:subject" +
				" AND r.session.id=:sessionId" +
				" AND r.locale=:locale AND r.number IS NOT NULL" +
				"(r.statusLowerHouse.type=:statusLowerHouseType OR r.statusUpperHouse.type=statusUpperHouseType)";
		try{
			Query query=this.em().createQuery(strQuery);
			query.setParameter("memberId", memberId);
			query.setParameter("sessionId", sessionId);
			query.setParameter("subject", subject);
			query.setParameter("locale", locale);
			query.setParameter("statusUpperHouseType", ApplicationConstants.RESOLUTION_FINAL_ADMISSION);
			query.setParameter("statusLowerHouseType", ApplicationConstants.RESOLUTION_FINAL_ADMISSION);
			Resolution resolution=(Resolution) query.getSingleResult();
			if(resolution!=null){
				return resolution.getId();
			}else{
				return null;
			}
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Long_findResolutionForUniqueMemberSubject", "Cannot get the Resolution");
			throw elsException;
	    }

	}

	public List<Resolution> findNonAnsweringDate(final Session session,
			final Member member,final DeviceType deviceType,final Date startTime,final Date endTime,
			final Status[] internalStatuses,final int maxD,final String sortOrder,final String locale) throws ELSException {
		
		StringBuffer query = new StringBuffer(
				" SELECT r FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.member.id=:memberId"+ 
						" AND r.type.id=:deviceTypeId AND r.submissionDate>=:strStartTime" +
						" AND r.submissionDate<=:strEndTime AND r.number IS NOT NULL"+
						" AND r.locale=:locale "
				);
		try{
		query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY r.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY r.number DESC");
		}
		TypedQuery<Resolution> tQuery = this.em().createQuery(query.toString(), Resolution.class);
		tQuery.setMaxResults(maxD);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		List<Resolution> resolutions = tQuery.getResultList();
		return resolutions;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findNonAnsweringDate", "Cannot get the Resolution");
			throw elsException;
	    }
	}

	public List<Resolution> findNonAnsweringDate(final Session session,
			final Member member,final DeviceType deviceType,final Date finalSubmissionDate,
			final Status[] internalStatuses,final int maxD,final String sortOrder,final String locale) throws ELSException {
		StringBuffer query = new StringBuffer(
				" SELECT r FROM Resolution r" +
				" WHERE r.session.id=:sessionId" +
				" AND r.member.id=:memberId "+
				" AND r.type.id=:deviceTypeId" +
				" AND r.submissionDate<=:strFinalSubmissionDate"+
				" AND r.locale=:locale" +
				" AND r.number IS NOT NULL"
			);
		try{
		query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY r.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY r.number DESC");
		}

		Query tQuery = this.em().createQuery(query.toString());
		tQuery.setMaxResults(maxD);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("memberId", member.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strFinalSubmissionDate", finalSubmissionDate);
		tQuery.setParameter("locale", locale);
		List<Resolution> resolutions = (List<Resolution>)tQuery.getResultList();
		return resolutions;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findNonAnsweringDate", "Cannot get the Resolution");
			throw elsException;
	    }
	}

	public List<MasterVO> findUniqueMembersAndSubjects(final String house, final String sessionID) throws ELSException{
		StringBuffer query = new StringBuffer("SELECT DISTINCT r.member_id,r.subject FROM resolutions r "+ 
				"LEFT JOIN members m ON(r.member_id=m.id) "+
				"LEFT JOIN `status` s ON(r."+house+"_status_id=s.id) "+
				"WHERE s.type='"+ApplicationConstants.RESOLUTION_FINAL_ADMISSION+"' AND r.session_id="+sessionID);
		try{
		@SuppressWarnings("rawtypes")
		List uniqueMemberSubjectResolutions = this.em().createNativeQuery(query.toString()).getResultList();
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		for(Object i: uniqueMemberSubjectResolutions){
			Object[] o = (Object[])i;

			MasterVO masterVO = new MasterVO();
			if(o[0] != null){
				masterVO.setId(Long.parseLong(o[0].toString()));
			}
			if(o[1] != null){
				masterVO.setValue(o[1].toString());
			}
			masterVOs.add(masterVO);
		}
		return masterVOs;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<MasterVO>_findUniqueMembersAndSubjects", "Cannot get the Resolution");
			throw elsException;
	    }
	}

	public List<Member> findActiveMembersWithResolutions(final Session session,
			final MemberRole role,final Date activeOn,final DeviceType deviceType,final Status[] internalStatuses,
			final Date startTime,final Date endTime,final String sortOrder,final String locale) throws ELSException {
		StringBuffer query = new StringBuffer();
		query.append(
				"SELECT DISTINCT(m) FROM Resolution r" +
				" JOIN r.member m" +
				" JOIN m.houseMemberRoleAssociations hmra"+
				" WHERE hmra.fromDate<=:strActiveOn" +
				" AND hmra.toDate>=:strActiveOn"+
				" AND hmra.role=:role" +
				" AND hmra.house=:house" +
				" AND hmra.locale=:locale"+
				" AND r.session.id=:sessionId" +
				" AND r.type.id=:deviceTypeId"+
				" AND r.submissionDate>=:strStartTime" +
				" AND r.submissionDate<=:strEndTime"+ 
				" AND r.locale=:locale");
		try{
		query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
		query.append(" ORDER BY m.lastName " + sortOrder + ", m.firstName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("strActiveOn", activeOn);
		tQuery.setParameter("role", role);
		tQuery.setParameter("house", session.getHouse());
		tQuery.setParameter("locale", locale);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		List<Member> members = tQuery.getResultList();
		return members;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Member>_findActiveMembersWithResolutions", "Cannot get Members");
			throw elsException;
	    }
	}

	public List<Member> findActiveMembersWithResolutions(final Session session,
			final MemberRole role,final Date activeOn,final DeviceType deviceType,final Status[] internalStatuses,
			final Date startTime,final Date endTime,final String sortBy, final String sortOrder,final String locale) throws ELSException {
		StringBuffer query = new StringBuffer();
		query.append(
				"SELECT DISTINCT(m) FROM Resolution r" +
				" JOIN r.member m" +
				" JOIN m.houseMemberRoleAssociations hmra"+
				" WHERE hmra.fromDate<=:strActiveOn" +
				" AND hmra.toDate>=:strActiveOn"+
				" AND hmra.role=:role" +
				" AND hmra.house=:house" +
				" AND hmra.locale=:locale"+
				" AND r.session.id=:sessionId" +
				" AND r.type.id=:deviceTypeId"+
				" AND r.submissionDate>=:strStartTime" +
				" AND r.submissionDate<=:strEndTime"+ 
				" AND r.locale=:locale");
		try{
		query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
		query.append(" ORDER BY "+sortBy+" " + sortOrder + ", m.firstName " + sortOrder);

		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("strActiveOn", activeOn);
		tQuery.setParameter("role", role);
		tQuery.setParameter("house", session.getHouse());
		tQuery.setParameter("locale", locale);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		List<Member> members = tQuery.getResultList();
		return members;
		}catch(Exception e){
	    	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Member>_findActiveMembersWithResolutions", "Cannot get Members");
			throw elsException;
	    }
	}
	
	public List<Member> findActiveMembersWithoutResolutions(final Session session,
			final MemberRole role,final Date activeOn,final DeviceType deviceType,final Status[] internalStatuses,
			final Date startTime,final Date endTime,final String sortOrder,final String locale) throws ELSException {
		StringBuffer query = new StringBuffer();
		query.append("SELECT m FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
				" WHERE hmra.fromDate<=:strActiveOn AND hmra.toDate>=:strActiveOn" +
				" AND hmra.role.id=:roleId AND hmra.house.id=:houseId AND hmra.locale=:locale"+
				" AND m.id NOT IN");
		query.append(" (");
		query.append(" SELECT DISTINCT(r.member.id) FROM Resolution r " +
				" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId "+
				" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime" +
				" AND r.locale=:locale");
		try{
		query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
		query.append(" )");
		query.append(" ORDER BY m.lastName " + sortOrder + ", m.firstName " + sortOrder);
		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("strActiveOn", activeOn);
		tQuery.setParameter("roleId", role.getId());
		tQuery.setParameter("houseId", session.getHouse().getId());
		tQuery.setParameter("locale", locale);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		List<Member> members = tQuery.getResultList();
		return members;
		}catch(Exception e){
    	e.printStackTrace();
		logger.error(e.getMessage());
		ELSException elsException=new ELSException();
		elsException.setParameter("ResolutionRepository_List<Member>_findActiveMembersWithoutResolutions", "Cannot get Members");
		throw elsException;
		}

	}	

	public List<Resolution> findChosenResolutionsForGivenDate(final Session session, 
			final DeviceType deviceType,
			final Status ballotStatus,
			final Status discussionStatus,
			final Date discussionDate,
			final String locale){
		
		StringBuffer query = new StringBuffer("SELECT r FROM Resolution r"
				+ " WHERE r.session.id=:sessionID"
				+ " AND r.type.id=:deviceTypeID"
				+ " AND r.ballotStatus.id=:ballotStatusID"
				+ " AND r.discussionStatus.id=:discussionStatusID"
				+ " AND r.discussionDate=:discussionDate"
				+ " AND r.locale=:locale");
		
		TypedQuery<Resolution> tQuery = this.em().createQuery(query.toString(), Resolution.class);
		tQuery.setParameter("sessionID", session.getId());
		tQuery.setParameter("deviceTypeID", deviceType.getId());
		tQuery.setParameter("ballotStatusID",  ballotStatus.getId());
		tQuery.setParameter("discussionStatusID", discussionStatus.getId());
		tQuery.setParameter("discussionDate", discussionDate);
		tQuery.setParameter("locale", locale);
		
		List<Resolution> resos = tQuery.getResultList();
		
		return resos;
	}
	
	
	/**
	 * @param session
	 * @param deviceType
	 * @param answeringDate
	 * @param internalStatuses
	 * @param startTime
	 * @param endTime
	 * @param sortOrder
	 * @param locale
	 * @return
	 * @throws ELSException
	 */
	public List<Member> findMembersEligibleForTheBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException{
		
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT(r.member) FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId " +
						" AND r.discussionStatus IS NULL" +
						" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime" +
						" AND r.locale=:locale "
				);
		try{
			query.append(this.getStatusFilters(internalStatuses, session.getHouse().getType().getType()));
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY r.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY r.number DESC");
			}
			TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("locale", locale);
			List<Member> members = tQuery.getResultList();
			return members;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Member>_findMembersAll", "Cannot get Members");
			throw elsException;
        }
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
	 * @throws ELSException 
	 */
	public List<Member> findMembers(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
		// Removed for performance reason. Uncomment when Caching mechanism is added

		// CustomParameter dbDateFormat =
		//    	CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		// String strAnsweringDate = 
		//		FormaterUtil.formatDateToString(answeringDate, dbDateFormat.getValue());

		// CustomParameter parameter = 
		//		CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT(r.member) FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId " +
						" AND ( r.discussionDate IS NULL OR r.discussionDate<=:strDiscussionDate)" +
						" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime" +
						" AND r.locale=:locale "
				);
		try{
			query.append(this.getStatusFilters(internalStatuses, session.getHouse().getType().getType()));
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY r.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY r.number DESC");
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
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Member>_findMembersAll", "Cannot get Members");
			throw elsException;
        }
	}

	public List<Member> findMembersAll(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean isPreBallot,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT(r.member) FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId "+
						" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime"+
						" AND r.locale=:locale"
				);
		try{
		if(!isPreBallot.booleanValue()){
			query.append(" AND r.discussionDate="+null);
		}
		query.append(this.getStatusFilters(internalStatuses, session.getHouse().getType().getType()));
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" ORDER BY r.number ASC");
		}
		else if(sortOrder.equals(ApplicationConstants.DESC)) {
			query.append(" ORDER BY r.number DESC");
		}
		TypedQuery<Member> tQuery = this.em().createQuery(query.toString(), Member.class);
		tQuery.setParameter("sessionId", session.getId());
		tQuery.setParameter("deviceTypeId", deviceType.getId());
		tQuery.setParameter("strStartTime", startTime);
		tQuery.setParameter("strEndTime", endTime);
		tQuery.setParameter("locale", locale);
		List<Member> members = tQuery.getResultList();
		return members;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Member>_findMembersAll", "Cannot get Members");
			throw elsException;
        }
		
	}

	/**
	 * Gets the status filters.
	 *
	 * @param internalStatuses the internal statuses
	 * @return the status filters
	 */
	private String getStatusFilters(final Status[] internalStatuses, final String houseType) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
				sb.append(" r.internalStatusLowerHouse.id = " + internalStatuses[i].getId());
				if(i < n - 1) {
					sb.append(" OR");
				}
			}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
				sb.append(" r.internalStatusUpperHouse.id = " + internalStatuses[i].getId());
				if(i < n - 1) {
					sb.append(" OR");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

	private String getStatusFilters(final Status[] internalStatuses,final HouseType houseType) {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND(");
		int n = internalStatuses.length;
		for(int i = 0; i < n; i++) {
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				sb.append(" r.internalStatusLowerHouse.id = " + internalStatuses[i].getId());
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				sb.append(" r.internalStatusUpperHouse.id = " + internalStatuses[i].getId());
			}

			if(i < n - 1) {
				sb.append(" OR ");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public Resolution findResolutionForMemberOfUniqueSubject(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Long memberID, 
			final List<String> subjects, 
			final String locale) throws ELSException{

		Resolution resolution = null;
		try{
			//String discussionDate = FormaterUtil.formatDateToString(answeringDate, "yyyy-MM-dd");
			Status internalStatus = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale);
			Status repeatAdmittedInternalStatus = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale);
			Status ballotStatus = Status.findByType(ApplicationConstants.RESOLUTION_PROCESSED_BALLOTED, locale);
	
			StringBuffer strQuery = new StringBuffer("SELECT r" +
					" FROM Resolution r JOIN r.session s JOIN r.houseType ht" +
					" WHERE r.session.id=:sessionId AND r.discussionDate IS NULL"+
					" AND r.member.id=:memberId AND r.number IS NOT NULL");
			if(!subjects.isEmpty()){
				strQuery.append(" AND r.subject NOT IN :subjectList");
			}
			if(session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				strQuery.append(" AND (r.internalStatusLowerHouse.id=:internalStatusId OR r.internalStatusLowerHouse.id=:repeatAdmitted)");
			}else if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				strQuery.append(" AND (r.internalStatusUpperHouse.id=:internalStatusId OR r.internalStatusUpperHouse.id=:repeatAdmitted)");
			}
	
			strQuery.append(" AND (r.ballotStatus.id!=:ballotStatusId OR r.ballotStatus.id IS NULL) ORDER BY r.discussionDate ASC");
	
			TypedQuery<Resolution> query = this.em().createQuery(strQuery.toString(), Resolution.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("memberId", memberID);
			query.setParameter("internalStatusId", internalStatus.getId());
			query.setParameter("repeatAdmitted", repeatAdmittedInternalStatus.getId());
			query.setParameter("ballotStatusId", ballotStatus.getId());
			if(!subjects.isEmpty()){
				query.setParameter("subjectList", subjects);
			}
			List<Resolution> resolutions = query.getResultList();
			resolution = randomResolution(resolutions);
			return resolution;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Resolution_findResolutionForMemberOfUniqueSubject", "Cannot get Resolution");
			throw elsException;
        }
	}

	private Resolution randomResolution(final List<Resolution> resolutions){
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		List<Resolution> randomResolutionList = randomizeResolutions(resolutions);
		Resolution resolution = null;

		if(randomResolutionList.size() > 0){
			resolution = randomResolutionList.get(Math.abs(rnd.nextInt() % resolutions.size())); 
		}
		return resolution;		
	}

	private List<Resolution> randomizeResolutions(final List<Resolution> resolutions) {
		List<Resolution> newResolutions = new ArrayList<Resolution>();
		newResolutions.addAll(resolutions);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newResolutions, rnd);
		return newResolutions;
	}

	private String submissionDateAsString(final Date date) {
		// Removed for performance reason. Uncomment when Caching mechanism is added
		// CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
		//		"DB_TIMESTAMP", "");
		// String strDate = new DateFormater().formatDateToString(date, parameter.getValue());
		String strDate = FormaterUtil.formatDateToString(date, "yyyy-MM-dd HH:mm:ss");
		String str = strDate.replaceFirst("00:00:00", "23:59:59");
		return str;
	}


	public Resolution find(final Member member,final Session session,final String locale) throws ELSException {
		Status internalStatus= Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, locale);
		String strQuery=null;
		if(session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			strQuery="SELECT MIN(r) FROM Resolution r" +
					" WHERE r.member.id=:memberId AND r.session.id=:sessionId"+
					" AND r.internalStatusLowerHouse.id=:internalStatusId AND r.locale=:locale";
		}else if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
			strQuery="SELECT MIN(r) FROM Resolution r" +
					" WHERE r.member.id=:memberId AND r.session.id=:sessionId AND r.number IS NULL"+
					" AND r.internalStatusUpperHouse.id=:internalStatusId AND r.locale=:locale";
		}
		try{
			Query query=this.em().createQuery(strQuery);
			query.setParameter("memberId", member.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("internalStatusId", internalStatus.getId());
			query.setParameter("locale", locale);
			Resolution resolutions=  (Resolution) query.getSingleResult();
			return resolutions;	
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_find", "Cannot get Resolution");
			throw elsException;
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
	 * @throws ELSException 
	 */
	public List<Resolution> find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean forBalloting,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT r FROM Resolution r" +
				" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId AND r.discussionDate IS NULL"+
				" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime");

		try{
			if(forBalloting.booleanValue()){
				query.append(" AND r.ballotStatus IS NULL");
			}
	
			query.append(" AND r.locale=:locale");
	
			query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
	
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY r.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY r.number DESC");
			}
	
			TypedQuery<Resolution> tQuery = this.em().createQuery(query.toString(), Resolution.class);
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("locale", locale);
			List<Resolution> resolutions = tQuery.getResultList();
			return resolutions;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_find", "Cannot get Resolution");
			throw elsException;
        }
	}

	public List<Resolution> find(final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT r FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId"+
						" AND (r.discussionDate IS NULL OR r.discussionDate<=:strDiscussionDate)" +
						" AND r.discussionStatus IS NULL"+
						" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime"+
						" AND r.member.id=:memberId AND r.locale=:locale"
				);
		try{
			query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
	
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY r.number ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY r.number DESC");
			}
	
			TypedQuery<Resolution> tQuery = this.em().createQuery(query.toString(), Resolution.class);
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strDiscussionDate",answeringDate);
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("memberId", memberId);
			tQuery.setParameter("locale", locale);
			List<Resolution> resolutions = tQuery.getResultList();
			return resolutions;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_find", "Cannot get Resolution");
			throw elsException;
        }
	}

	public List<Resolution> findResolutionsByDiscussionDateAndMember(final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
		StringBuffer query = new StringBuffer(
				" SELECT DISTINCT r FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId" +
						" AND r.discussionDate=:strDiscussionDate AND r.discussionStatus IS NOT NULL"+
						" AND r.ballotStatus.id IS NOT NULL"+
						" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime" +
				" AND r.member.id=:memberId AND r.locale=:locale");
		try{
			query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
			if(sortOrder.equals(ApplicationConstants.ASC)) {
				query.append(" ORDER BY r.discussionDate ASC");
			}
			else if(sortOrder.equals(ApplicationConstants.DESC)) {
				query.append(" ORDER BY r.discussionDate DESC");
			}
			TypedQuery<Resolution> tQuery = this.em().createQuery(query.toString(), Resolution.class);
			tQuery.setParameter("sessionId", session.getId());
			tQuery.setParameter("deviceTypeId", deviceType.getId());
			tQuery.setParameter("strDiscussionDate", answeringDate);
			/*tQuery.setParameter("discussionStatus", null);
			tQuery.setParameter("ballotStatus", null);*/
			tQuery.setParameter("strStartTime", startTime);
			tQuery.setParameter("strEndTime", endTime);
			tQuery.setParameter("memberId", memberId);
			tQuery.setParameter("locale", locale);
	
			List<Resolution> resolutions = tQuery.getResultList();
			return resolutions;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findResolutionsByDiscussionDateAndMember", "Cannot get Resolution");
			throw elsException;
        }
	}


	/**
	 * To find the count of the resolutions selected for discussion by the member
	 * @param memberId
	 * @param session
	 * @param deviceType
	 * @param locale
	 * @return
	 * @throws ELSException 
	 */
	public Integer findMemberChoiceCount(
			final Session session,
			final DeviceType deviceType,
			final Long memberId,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String locale) throws ELSException{
		StringBuffer query = new StringBuffer(
				" SELECT COUNT(DISTINCT r.id) FROM Resolution r" +
						" WHERE r.session.id=:sessionId AND r.type.id=:deviceTypeId"+
						" AND r.discussionDate=:strDiscussionDate AND r.discussionStatus=:discussionStatus"+
						" AND r.ballotStatus IS NOT NULL AND r.number IS NOT NULL"+
						" AND r.submissionDate>=:strStartTime AND r.submissionDate<=:strEndTime" +
				" AND r.member.id=:memberId AND r.locale=:locale");
		try{
			query.append(this.getStatusFilters(internalStatuses,session.getHouse().getType()));
			Query cQuery = this.em().createQuery(query.toString());
			cQuery.setParameter("sessionId", session.getId());
			cQuery.setParameter("deviceTypeId", deviceType.getId());
			cQuery.setParameter("strDiscussionDate", answeringDate);
			cQuery.setParameter("discussionStatus", null);
			cQuery.setParameter("strStartTime", startTime);
			cQuery.setParameter("strEndTime", endTime);
			cQuery.setParameter("memberId", memberId);
			cQuery.setParameter("locale", locale);
			Integer count = new Integer(((Number)cQuery.getSingleResult()).intValue());
			return count;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Integer_findMemberChoiceCount", "Cannot get Member choice count");
			throw elsException;
        }
	}

	public List<Resolution> findAllByMember(final Session session,final  Member member,
			final DeviceType deviceType,final Integer itemsCount,final String locale) throws ELSException {
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.RESOLUTION_COMPLETE, locale);
			StringBuffer buffer=new StringBuffer();
			buffer.append("SELECT r FROM Resolution r Where r.session=:session"+
					" AND r.type=:deviceType AND r.member=:member AND r.locale=:locale");
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				if(session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					buffer.append(" AND r.statusLowerHouse=:status");
				}else if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
					buffer.append(" AND r.statusUpperHouse=:status");
				}
			}else if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
				buffer.append(" AND r.statusLowerHouse=:status");
				buffer.append(" AND r.statusUpperHouse=:status");
			}
	
			buffer.append(" ORDER BY r.id DESC");
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("session", session);
			query.setParameter("deviceType", deviceType);
			query.setParameter("locale", locale);
			query.setParameter("member", member);
			query.setParameter("status", status);
			query.setMaxResults(itemsCount);
			return query.getResultList();
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findAllByMember", "Cannot get the Resolution");
			throw elsException;
        }
	}

	public List<Resolution> findAllByStatus(final Session session,final DeviceType deviceType,
			final Status internalStatus,final Integer itemsCount,final String locale) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		try{
			buffer.append("SELECT r FROM Resolution r Where r.session.id=:sessionId"+
					" AND r.type.id=:deviceTypeId AND r.locale=:locale");
			if(session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				buffer.append(" AND r.internalStatusLowerHouse.id=:internalStatusId"+
						" AND r.workflowStartedLowerHouse=:workflowStarted");
			}else if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				buffer.append(" AND r.internalStatusUpperHouse.id=:internalStatusId"+
						" AND r.workflowStartedUpperHouse=:workflowStarted");
			}
			buffer.append(" ORDER BY r.number");
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			query.setParameter("internalStatusId", internalStatus.getId());
			query.setParameter("workflowStarted", "NO");
			query.setMaxResults(itemsCount);
			return query.getResultList();
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findAllByStatus", "Cannot get the Resolution");
			throw elsException;
        }
	}

	public Integer findResolutionWithoutNumberCount(final Member member,final DeviceType deviceType,final Session session,
			final String locale) throws ELSException {
		String strQquery="SELECT COUNT(r) FROM Resolution r Where r.session.id=:sessionId"+
				" AND r.type.id=:deviceTypeId AND r.member.id=:memberId AND r.locale=:locale"+
				" AND (r.internalStatusLowerHouse.id<>:incompleteStatusId OR r.internalStatusUpperHouse.id<>:incompleteStatusId)" +
				" AND (r.internalStatusLowerHouse.id<>:completeStatusId OR r.internalStatusUpperHouse.id<>:completeStatusId)"+
				" AND r.number IS  NULL";
		Status incompleteStatus=Status.findByType(ApplicationConstants.RESOLUTION_INCOMPLETE, locale);
		Status completeStatus=Status.findByType(ApplicationConstants.RESOLUTION_COMPLETE, locale);
		try{
			Query query=this.em().createQuery(strQquery);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			query.setParameter("memberId", member.getId());
			query.setParameter("incompleteStatusId", incompleteStatus.getId());
			query.setParameter("completeStatusId", completeStatus.getId());
			Integer nonNumberResolutions = Integer.valueOf(query.getSingleResult().toString());
			
			return nonNumberResolutions;
			
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Integer_findResolutionWithoutNumberCount", "Cannot get the Resolution count");
			throw elsException;
        }
	}

	public List<Resolution> findRejectedResolution(final Member member,final DeviceType deviceType,
			final Session session,final String locale) throws ELSException {
		try{
			Status rejectStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale);
			Status repeatRejectStatus=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION, locale);
			StringBuffer buffer=new StringBuffer();
			buffer.append("SELECT r FROM Resolution r Where r.session.id=:sessionId"+
					" AND r.type.id=:deviceTypeId AND r.member.id=:memberId AND r.locale=:locale");
			if(session.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				buffer.append(" AND (r.internalStatusLowerHouse.id=:rejectStatusId OR " +
						" r.internalStatusLowerHouse.id=:repeatRejectStatusId)");
			}else if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				buffer.append(" AND (r.internalStatusUpperHouse.id=:rejectStatusId OR " +
						" r.internalStatusUpperHouse.id=:repeatRejectStatusId)");
			}
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			query.setParameter("memberId", member.getId());
			query.setParameter("rejectStatusId", rejectStatus.getId());
			query.setParameter("repeatRejectStatusId", repeatRejectStatus.getId());
			return query.getResultList();
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findRejectedResolution", "Cannot get the Rejected Resolution ");
			throw elsException;
        }
	}
	
	public String findRejectedResolutionAsString(List<Resolution> resolutions) throws ELSException {
		try{	
			String rejectedNotices="";
			
			for(Resolution r: resolutions){
				if(resolutions.get(0).equals(r)){
					rejectedNotices=r.getNumber().toString();
				}else{
					rejectedNotices=rejectedNotices+","+r.getNumber().toString();
				}
			}
			
			return rejectedNotices;
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findRejectedResolutionAsString", "Cannot get the Rejected Resolution ");
			throw elsException;
        }
	}

	public Integer findResolutionCount(final Member member,final Session selectedSession,
			final DeviceType resolutionType,final String locale) throws ELSException {
		try{
			Status status=Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale);
			StringBuffer buffer=new StringBuffer();
			buffer.append("SELECT r FROM Resolution r Where r.session.id=:sessionId"+
					" AND r.type.id=:deviceTypeId AND r.member.id=:memberId AND r.locale=:locale"+
					" AND r.number IS NOT NULL");
			if(selectedSession.getHouse().getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				buffer.append(" AND r.internalStatusLowerHouse.id=:internalStatusId");
			}else if(selectedSession.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				buffer.append(" AND r.internalStatusUpperHouse.id=:internalStatusId");
			}
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("sessionId", selectedSession.getId());
			query.setParameter("deviceTypeId", resolutionType.getId());
			query.setParameter("locale", locale);
			query.setParameter("memberId", member.getId());
			query.setParameter("internalStatusId", status.getId());
			List<Resolution> resolutions=query.getResultList();
			if(resolutions.isEmpty()){
				return 0;
			}else{
				return resolutions.size();
			}
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Integer_findResolutionCount", "Cannot get the Resolution Font");
			throw elsException;
        }
		
	}

	public int findHighestFileNo(final Session session,final DeviceType deviceType,final HouseType houseType,
			final String locale) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		try{
		buffer.append("SELECT r FROM Resolution r Where r.session.id=:sessionId"+
				" AND r.type.id=:deviceTypeId AND r.locale=:locale");
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			buffer.append(" AND r.fileLowerHouse IS NOT NULL");
			buffer.append(" ORDER BY r.fileLowerHouse DESC");
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			buffer.append(" AND r.fileUpperHouse IS NOT NULL");
			buffer.append(" ORDER BY r.fileUpperHouse DESC");
		}
		Query query=this.em().createQuery(buffer.toString());
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		List<Resolution> resolutions=query.getResultList();
		if(resolutions==null){
			return 0;
		}else if(resolutions.isEmpty()){
			return 0;
		}else{
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				return resolutions.get(0).getFileLowerHouse();
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				return resolutions.get(0).getFileUpperHouse();
			}else{
				return 0;
			}

		}
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_int_findHighestFileNo", "Resolution File Not found");
			throw elsException;
        }
	}

	public Reference findCurrentFile(final Resolution domain,final HouseType houseType) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		try{
		buffer.append("SELECT r FROM Resolution r Where r.session.id=:sessionId"+
				" AND r.type.id=:deviceTypeId AND r.locale=:locale");
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			buffer.append(" AND r.fileLowerHouse IS NOT NULL");
			buffer.append(" ORDER BY r.fileLowerHouse DESC,r.fileIndexLowerHouse DESC");
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("sessionId", domain.getSession().getId());
			query.setParameter("deviceTypeId", domain.getType().getId());
			query.setParameter("locale", domain.getLocale());
			List<Resolution> resolutions= query.getResultList();//this.search(search);
			Resolution resolution=null;
			if(resolutions!=null&&!resolutions.isEmpty()){
				resolution=resolutions.get(0);
			}

			if(resolution==null){
				return new Reference(String.valueOf(1),String.valueOf(1));
			}else if(resolution.getFileLowerHouse()==null){
				return new Reference(String.valueOf(1),String.valueOf(1));
			}else if(resolution.getFileLowerHouse()!=null&&resolution.getFileIndexLowerHouse()==null){
				return new Reference(String.valueOf(resolution.getFileLowerHouse()),String.valueOf(1));
			}else{
				CustomParameter customParameter=CustomParameter.
						findByName(CustomParameter.class,"FILE_"+domain.getType().getType().toUpperCase(), "");
				int fileSize=Integer.parseInt(customParameter.getValue());
				if(resolution.getFileIndexLowerHouse()==fileSize){
					return new Reference(String.valueOf(resolution.getFileLowerHouse()+1),String.valueOf(1));
				}else{
					return new Reference(String.valueOf(resolution.getFileLowerHouse()),String.valueOf(resolution.getFileIndexLowerHouse()+1));
				}
			}
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			buffer.append(" AND r.fileUpperHouse IS NOT NULL");
			buffer.append(" ORDER BY r.fileUpperHouse DESC,r.fileIndexUpperHouse DESC");
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("sessionId", domain.getSession().getId());
			query.setParameter("deviceTypeId", domain.getType().getId());
			query.setParameter("locale", domain.getLocale());
			List<Resolution> resolutions= query.getResultList();
			Resolution resolution=null;
			if(resolutions!=null&&!resolutions.isEmpty()){
				resolution=resolutions.get(0);
			}

			if(resolution==null){
				return new Reference(String.valueOf(1),String.valueOf(1));
			}else if(resolution.getFileUpperHouse()==null){
				return new Reference(String.valueOf(1),String.valueOf(1));
			}else if(resolution.getFileUpperHouse()!=null&&resolution.getFileIndexUpperHouse()==null){
				return new Reference(String.valueOf(resolution.getFileUpperHouse()),String.valueOf(1));
			}else{
				CustomParameter customParameter=CustomParameter.
						findByName(CustomParameter.class,"FILE_"+domain.getType().getType().toUpperCase(), "");
				int fileSize=Integer.parseInt(customParameter.getValue());
				if(resolution.getFileIndexUpperHouse()==fileSize){
					return new Reference(String.valueOf(resolution.getFileUpperHouse()+1),String.valueOf(1));
				}else{
					return new Reference(String.valueOf(resolution.getFileUpperHouse()),String.valueOf(resolution.getFileIndexUpperHouse()+1));
				}
			}
		}else{
			return null;
		}
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_Reference_findCurrentFile", "Resolution File Not found");
			throw elsException;
        }

	}

	public List<Resolution> findAllByFile(final Session session,
			final DeviceType deviceType,final Integer file,final String locale,final HouseType houseType) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		try{
			
		buffer.append("SELECT r FROM Resolution r Where r.session.id=:sessionId"+
				" AND r.type.id=:deviceTypeId AND r.locale=:locale");
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			buffer.append(" AND (r.fileLowerHouse=:file OR r.file=:file)");
			buffer.append(" ORDER BY r.fileIndexLowerHouse");
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			buffer.append(" AND (r.fileUpperHouse=:file OR r.file=:file)");
			buffer.append(" ORDER BY r.fileIndexUpperHouse");
		}
			TypedQuery<Resolution> query=this.em().createQuery(buffer.toString(), Resolution.class);
			query.setParameter("sessionId", session.getId());
			query.setParameter("deviceTypeId", deviceType.getId());
			query.setParameter("locale", locale);
			query.setParameter("file", file);
			return query.getResultList();
		}catch(Exception e){
        	e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("ResolutionRepository_List<Resolution>_findAllByFile", "Resolution Not found");
			throw elsException;
        }
	}

	public Integer findChoiceCountForGivenDiscussionDateOfMember(final Session session, 
						final DeviceType deviceType, 
						final Member member, 
						final Date discussionDate, 
						final String locale){
				
		StringBuffer query = new StringBuffer("SELECT COUNT(r.id) FROM Resolution r"
				+ " WHERE r.session.id=:sessionID"
				+ " AND r.type.id=:deviceTypeID"
				+ " AND r.member.id=:memberID"
				+ " AND r.discussionDate=:discussionDate"
				+ " AND r.ballotStatus IS NOT NULL"
				+ " AND r.discussionStatus IS NOT NULL"
				+ " AND r.locale=:locale");
		
		TypedQuery<Long> tQuery = this.em().createQuery(query.toString(), Long.class);
		tQuery.setParameter("sessionID", session.getId());
		tQuery.setParameter("deviceTypeID", deviceType.getId());
		tQuery.setParameter("discussionDate", discussionDate);
		tQuery.setParameter("memberID", member.getId());
		tQuery.setParameter("locale", locale);
		
		Integer result = tQuery.getSingleResult().intValue();
		
		return result;
	}
	
	public Resolution findChoResolutionOfMember(final Session session, 
			final DeviceType deviceType, 
			final Member member, 
			final Date discussionDate, 
			final String locale){
	
		StringBuffer query = new StringBuffer("SELECT r FROM Resolution r"
			+ " WHERE r.session.id=:sessionID"
			+ " AND r.type.id=:deviceTypeID"
			+ " AND r.member.id=:memberID"
			+ " AND r.discussionDate=:discussionDate"
			+ " AND r.ballotStatus IS NOT NULL"
			+ " AND r.discussionStatus IS NOT NULL"
			+ " AND r.locale=:locale");
		
		TypedQuery<Resolution> tQuery = this.em().createQuery(query.toString(), Resolution.class);
		tQuery.setParameter("sessionID", session.getId());
		tQuery.setParameter("deviceTypeID", deviceType.getId());
		tQuery.setParameter("discussionDate", discussionDate);
		tQuery.setParameter("memberID", member.getId());
		tQuery.setParameter("locale", locale);
		
		List<Resolution> result = tQuery.getResultList();
		Resolution res = (result != null && !result.isEmpty())? result.get(0):null;
		return res;
	}

	public Resolution getResolution(Long sessionId, Long deviceTypeId,
			Integer dNumber, String locale) {
		String strQuery="SELECT r FROM Resolution r WHERE " +
				" r.session.id=:sessionId" +
				" AND r.type.id=:deviceTypeId" +
				" AND r.number=:dNumber" +
				" AND r.locale=:locale";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("sessionId", sessionId);
		query.setParameter("deviceTypeId", deviceTypeId);
		query.setParameter("dNumber", dNumber);
		query.setParameter("locale", locale);
		Resolution resolution=(Resolution) query.getSingleResult();
		return resolution;
	}

	public Boolean isExist(Integer number, DeviceType type, Session session,
			String locale) {
		try{
			StringBuffer strQuery=new StringBuffer();
			strQuery.append("SELECT r FROM Resolution  r " +
					" WHERE r.session.id=:sessionId" +
					" AND r.number=:number" +
					" AND r.locale=:locale"+
					" AND r.type.id=:deviceTypeId");
			Query query=this.em().createQuery(strQuery.toString());
			query.setParameter("deviceTypeId", type.getId());
			query.setParameter("sessionId", session.getId());
			query.setParameter("number", number);
			query.setParameter("locale", locale);
			Resolution resolution=(Resolution) query.getSingleResult();
			if(resolution != null){
				return true;
			}else{
				return false;
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public MemberMinister findMemberMinisterIfExists(Resolution resolution) throws ELSException {
		MemberMinister  memberMinister = null;
		Session session = resolution.getSession();
		if(session==null) {
			logger.error("This resolution has no session.");
			throw new ELSException("resolution_session_null", "This resolution has no session.");
		}
		try{			
			String queryString = "SELECT mm FROM MemberMinister mm JOIN mm.ministry mi JOIN mm.member m " +
					"WHERE mi.id IN " +
					"(SELECT gm.id FROM Group g join g.ministries gm " +
					"WHERE g.houseType.id=:houseTypeId AND g.sessionType.id=:sessionTypeId"+
					" AND g.year=:sessionYear AND g.locale=:locale) " +
					" AND mi.id=:ministryId AND " +
					"(mm.ministryFromDate <=:resolutionSubmissionDate AND (mm.ministryToDate >:resolutionSubmissionDate  OR mm.ministryToDate IS NULL)) AND " +
					"mm.locale=:locale";
			
			TypedQuery<MemberMinister> query = this.em().createQuery(queryString, MemberMinister.class);
			query.setParameter("houseTypeId", session.getHouse().getType().getId());
			query.setParameter("sessionTypeId", session.getType().getId());
			query.setParameter("sessionYear", session.getYear());
			query.setParameter("locale", resolution.getLocale());
			//query.setParameter("houseId", session.getHouse().getId());
			query.setParameter("ministryId", resolution.getMinistry().getId());
			query.setParameter("resolutionSubmissionDate", resolution.getSubmissionDate());
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

	public List<ChartVO> getAdmittedResolutions(Session session,
			DeviceType deviceType, String locale) throws ELSException {
		List<ChartVO> admittedResolutionVO = new ArrayList<ChartVO>();
		String houseType = session.getHouse().getType().getType();
		Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale);
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale);
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter("resolutions_nonofficial_submissionStartDate"),ApplicationConstants.DB_DATEFORMAT);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter("resolutions_nonofficial_submissionEndDate"),ApplicationConstants.DB_DATEFORMAT);
		Status[] statuses = {ADMITTED,REPEATADMITTED};
		List<Member> members = Resolution.
				findActiveMembersWithResolutions(session, new Date(), deviceType, statuses, startTime, endTime, "number", "ASC", locale);
		for(Member m : members){
			ChartVO chart = new ChartVO();
			chart.setMemberId(m.getId());
			chart.setMemberName(m.findFirstLastName());
			List<Resolution> resolutions = Resolution.findAdmittedResolutionByMember(m,deviceType,session, locale);
			List<DeviceVO> deviceVos = new ArrayList<DeviceVO>();
			for(Resolution r:resolutions){
				Integer number = r.getNumber();
				String formatNumber = r.formatNumber();
				String revisedNoticeContent = null;
				if(r.getRevisedNoticeContent() != null && !r.getRevisedNoticeContent().isEmpty()){
					revisedNoticeContent = r.getRevisedNoticeContent();
				}else if(r.getNoticeContent() != null && !r.getNoticeContent().isEmpty()){
					revisedNoticeContent = r.getNoticeContent();
				}
				DeviceVO deviceVO = new DeviceVO(formatNumber, revisedNoticeContent);
				deviceVO.setNumber(number);
				deviceVos.add(deviceVO);
			}
			chart.setDeviceVOs(deviceVos);
			admittedResolutionVO.add(chart);
		}
		return admittedResolutionVO;
	}

	
	@SuppressWarnings("unchecked")
	public List<Resolution> getAdmittedResolutionsByMember(Member member,
			Session session, DeviceType deviceType, String locale) {
		List<Resolution> resolutions = new ArrayList<Resolution>();
		String houseType = session.getHouse().getType().getType();
		StringBuffer buffer = new StringBuffer("SELECT r "
		+ " FROM Resolution r "
		+ " WHERE r.type.id=:deviceTypeId"
		+ " AND r.locale=:locale"
		+ " AND r.member.id=:memberId"
		+ " AND r.session.id=:sessionId");
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
			buffer.append(" AND (r.internalStatusLowerHouse.type='resolution_final_admission'"
					+ "		OR r.internalStatusLowerHouse.type='resolution_final_repeatadmission')");
		
		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
			buffer.append(" AND (r.internalStatusUpperHouse.type='resolution_final_admission'"
					+ "		OR r.internalStatusUpperHouse.type='resolution_final_repeatadmission')");
		}
		buffer.append("ORDER BY number");
		Query query = this.em().createQuery(buffer.toString());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		query.setParameter("memberId", member.getId());
		query.setParameter("sessionId", session.getId());
		resolutions = query.getResultList();
		return resolutions;
	}

	public Integer findHighestKaryavaliNumber(final DeviceType deviceType,
			final Session session, final String locale) {
		Integer highestKaryavaliNumber = null;
		if(session!=null) {			
			String resolutionSubmissionStartDate = session.getParameter("resolutions_nonofficial_submissionStartDate");
			String queryString = "SELECT MAX(r.karyavaliNumber) FROM Resolution r"
						+ " WHERE r.type.type=:strDeviceType"
						+ " AND r.karyavaliNumber IS NOT NULL"
						+ " AND r.session.house.id=:houseId"
						+ " AND r.KaryavaliGenerationDate>=:resolutionSubmissionStartDate"
					    + " AND r.KaryavaliGenerationDate<=:sessionEndDate"
						+ " AND r.locale=:locale";
			try {
				TypedQuery<Integer> query = this.em().createQuery(queryString, Integer.class);
				query.setParameter("houseId", session.getHouse().getId());
				query.setParameter("strDeviceType", deviceType.getType());
				query.setParameter("resolutionSubmissionStartDate",FormaterUtil.formatStringToDate(resolutionSubmissionStartDate,ApplicationConstants.DB_DATEFORMAT));
				query.setParameter("sessionEndDate", session.getEndDate());
				query.setParameter("locale", locale);
				highestKaryavaliNumber = query.getSingleResult();
			} catch(Exception e) {
				highestKaryavaliNumber = 0;
			}	
			if(highestKaryavaliNumber==null) {
				highestKaryavaliNumber = 0;
			}
		}
		return highestKaryavaliNumber;
	}

	public boolean isNumberedKaryavaliFilled(DeviceType deviceType,
			Session session, Integer highestKaryavaliNumber, String locale) {
		boolean isNumberedKaryavliFilled = false;		
		if(session!=null) {
			Long resolutionsFilledWithKaryavaliCount = null;
			String resolutionSubmissionStartDate = session.getParameter("resolutions_nonofficial_submissionStartDate");
			String queryString = "SELECT COUNT(r.id) FROM Resolution r"
					+ " WHERE r.type.type=:strDeviceType"
					+ " AND r.session.house.id=:houseId"
					+ " AND r.karyavaliNumber=:karyavaliNumber"
					+ " AND r.KaryavaliGenerationDate>=:resolutionSubmissionStartDate"
					+ " AND r.KaryavaliGenerationDate<=:sessionEndDate"
					+ " AND r.locale=:locale "
					+ " ORDER BY r.number";
			
			try {
				TypedQuery<Long> query = this.em().createQuery(queryString, Long.class);
				query.setParameter("houseId", session.getHouse().getId());
				query.setParameter("strDeviceType", deviceType.getType());		
				Date submissionDate = FormaterUtil.formatStringToDate(resolutionSubmissionStartDate,ApplicationConstants.DB_DATETIME_FORMAT);
				query.setParameter("resolutionSubmissionStartDate", submissionDate);
				query.setParameter("sessionEndDate", session.getEndDate());
				query.setParameter("karyavaliNumber", highestKaryavaliNumber);
				query.setParameter("locale", locale);
				if(query.getSingleResult()>0){
					isNumberedKaryavliFilled = true;
				}
			} catch(Exception e) {
				e.printStackTrace();
				isNumberedKaryavliFilled = false;
			}
		}		
		return isNumberedKaryavliFilled;
	}

	public Date findResolutionKaryavaliDate(DeviceType deviceType,
			Integer highestKaryavaliNumber, Session session, String locale) {
		Date karyavaliDate = null;
		if(session!=null) {
			String resolutionSubmissionStartDate = session.getParameter("resolutions_nonofficial_submissionStartDate");
			String queryString = "SELECT r.KaryavaliGenerationDate FROM Resolution r"
					+ " WHERE r.type.type=:deviceType"
					+ " AND r.session.house.id=:houseId"
					+ " AND r.karyavaliNumber=:karyavaliNumber"
					+ " AND r.KaryavaliGenerationDate>=:resolutionSubmissionStartDate"
					+ " AND r.KaryavaliGenerationDate<=:sessionEndDate"
					+ " AND r.locale=:locale"
					+ " ORDER BY r.number";
			try {
				TypedQuery<Date> query = this.em().createQuery(queryString, Date.class);
				query.setParameter("houseId", session.getHouse().getId());
				query.setParameter("deviceType", deviceType.getType());				
				query.setParameter("resolutionSubmissionStartDate",FormaterUtil.formatStringToDate(resolutionSubmissionStartDate,ApplicationConstants.DB_DATEFORMAT));
				query.setParameter("sessionEndDate", session.getEndDate());
				query.setParameter("karyavaliNumber", highestKaryavaliNumber);
				query.setParameter("locale", locale);
				karyavaliDate = query.getSingleResult();
			} catch(Exception e) {
				karyavaliDate = null;
			}
		}
		return karyavaliDate;
	}

	public List<ChartVO> findEligibleAdmittedNonOfficialResolution(
			Session session, Integer karyavaliNumber, Date karyavaliDate, Locale locale) throws ELSException {
		List<ChartVO> admittedResolutionVO = new ArrayList<ChartVO>();
		String houseType = session.getHouse().getType().getType();
		Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale.toString());
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale.toString());
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter("resolutions_nonofficial_submissionStartDate"),ApplicationConstants.DB_DATEFORMAT);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter("resolutions_nonofficial_submissionEndDate"),ApplicationConstants.DB_DATEFORMAT);
		Status[] statuses = {ADMITTED,REPEATADMITTED};
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.NONOFFICIAL_RESOLUTION, locale.toString());
		List<Member> members = Resolution.
				findActiveMembersWithResolutions(session, new Date(), deviceType, statuses, startTime, endTime, "number", "ASC", locale.toString());
		for(Member m : members){
			List<Resolution> resolutions = Resolution.findEligibleAdmittedResolutionByMember(m,deviceType,session, locale.toString());
			if(!resolutions.isEmpty()){
				ChartVO chart = new ChartVO();
				chart.setMemberId(m.getId());
				chart.setMemberName(m.getFullname());
				
				List<DeviceVO> deviceVos = new ArrayList<DeviceVO>();
				for(Resolution r:resolutions){
					Integer number = r.getNumber();
					String formatNumber = r.formatNumber();
					String revisedNoticeContent = null;
					if(r.getNoticeContent() != null){
						revisedNoticeContent = r.getNoticeContent();
					}else if(r.getRevisedNoticeContent() != null){
						revisedNoticeContent = r.getRevisedNoticeContent();
					}
					r.setKaryavaliGenerationDate(karyavaliDate);
					r.setKaryavaliNumber(karyavaliNumber);
					r.simpleMerge();
					DeviceVO deviceVO = new DeviceVO(formatNumber, revisedNoticeContent);
					deviceVO.setNumber(number);
					deviceVos.add(deviceVO);
				}
				chart.setDeviceVOs(deviceVos);
				admittedResolutionVO.add(chart);
			}
		}
		return admittedResolutionVO;
	}

	public List<Resolution> getEligibleAdmittedResolutionsByMember(Member member,
			Session session, DeviceType deviceType, String locale) {
		List<Resolution> resolutions = new ArrayList<Resolution>();
		String houseType = session.getHouse().getType().getType();
		StringBuffer buffer = new StringBuffer("SELECT r "
		+ " FROM Resolution r "
		+ " WHERE r.type.id=:deviceTypeId"
		+ " AND r.locale=:locale"
		+ " AND r.member.id=:memberId");
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
			buffer.append(" AND (r.internalStatusLowerHouse.type='resolution_final_admission'"
					+ "		OR r.internalStatusLowerHouse.type='resolution_final_repeatadmission')");
		
		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
			buffer.append(" AND (r.internalStatusUpperHouse.type='resolution_final_admission'"
					+ "		OR r.internalStatusUpperHouse.type='resolution_final_repeatadmission')");
		}
		buffer.append(" AND r.karyavaliNumber IS NULL"
					+" AND r.KaryavaliGenerationDate IS NULL");
		buffer.append(" ORDER BY number");
		Query query = this.em().createQuery(buffer.toString());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		query.setParameter("memberId", member.getId());
		resolutions = query.getResultList();
		return resolutions;
	}

	public List<ChartVO> findNonOfficialResolutionsByKaryavaliNumber(
			Session session, Integer karyavaliNumber, String locale) throws ELSException {
		List<ChartVO> admittedResolutionVO = new ArrayList<ChartVO>();
		String houseType = session.getHouse().getType().getType();
		Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale);
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale);
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter("resolutions_nonofficial_submissionStartDate"),ApplicationConstants.DB_DATEFORMAT);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter("resolutions_nonofficial_submissionEndDate"),ApplicationConstants.DB_DATEFORMAT);
		Status[] statuses = {ADMITTED,REPEATADMITTED};
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.NONOFFICIAL_RESOLUTION, locale.toString());
		List<Member> members = Resolution.
				findActiveMembersWithResolutions(session, new Date(), deviceType, statuses, startTime, endTime, "number", "ASC", locale);
		for(Member m : members){
			List<Resolution> resolutions = Resolution.findAdmittedNonOfficialResolutionByMemberAndKaryavaliNumber(m,deviceType,session,karyavaliNumber, locale);
			if(!resolutions.isEmpty()){
				ChartVO chart = new ChartVO();
				chart.setMemberId(m.getId());
				chart.setMemberName(m.getFullname());
				
				List<DeviceVO> deviceVos = new ArrayList<DeviceVO>();
				for(Resolution r:resolutions){
					Integer number = r.getNumber();
					String formatNumber = r.formatNumber();
					String revisedNoticeContent = null;
					if(r.getRevisedNoticeContent() != null && !r.getRevisedNoticeContent().isEmpty()){
						revisedNoticeContent = r.getRevisedNoticeContent();
					}else if(r.getNoticeContent() != null && !r.getNoticeContent().isEmpty()){
						revisedNoticeContent = r.getNoticeContent();
					}
					DeviceVO deviceVO = new DeviceVO(formatNumber, revisedNoticeContent);
					deviceVO.setNumber(number);
					deviceVos.add(deviceVO);
				}
				chart.setDeviceVOs(deviceVos);
				admittedResolutionVO.add(chart);
			}
		}
		return admittedResolutionVO;
	}

	public List<Resolution> findAdmittedNonOfficialResolutionByMemberAndKaryavaliNumber(
			Member member, DeviceType deviceType, Session session,
			Integer karyavaliNumber, String locale) {
		List<Resolution> resolutions = new ArrayList<Resolution>();
		String houseType = session.getHouse().getType().getType();
		StringBuffer buffer = new StringBuffer("SELECT r "
		+ " FROM Resolution r "
		+ " WHERE r.type.id=:deviceTypeId"
		+ " AND r.locale=:locale"
		+ " AND r.session.id=:sessionId"
		+ " AND r.member.id=:memberId");
		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
			buffer.append(" AND (r.internalStatusLowerHouse.type='resolution_final_admission'"
					+ "		OR r.internalStatusLowerHouse.type='resolution_final_repeatadmission')");
		
		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
			buffer.append(" AND (r.internalStatusUpperHouse.type='resolution_final_admission'"
					+ "		OR r.internalStatusUpperHouse.type='resolution_final_repeatadmission')");
		}
		buffer.append(" AND r.karyavaliNumber=:karyavaliNumber");
		buffer.append(" ORDER BY number");
		Query query = this.em().createQuery(buffer.toString());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale", locale);
		query.setParameter("memberId", member.getId());
		query.setParameter("sessionId", session.getId());
		query.setParameter("karyavaliNumber", karyavaliNumber);
		resolutions = query.getResultList();
		return resolutions;
	}

	public List<Resolution> findDiscussedResolution(final Session session,
			final DeviceType deviceType, final String locale) {
		String strQuery = "SELECT r "
				+ " FROM Resolution r"
				+ " WHERE r.discussionStatus IS NOT NULL"
				+ " AND r.session.id=:sessionId"
				+ " AND r.type.id=:deviceTypeId"
				+ " AND r.locale=:locale";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("sessionId", session.getId());
		query.setParameter("deviceTypeId", deviceType.getId());
		query.setParameter("locale",locale);
		return query.getResultList();
	}

	/**
	 * Motion Search Based on parameters passed
	 * **/
	public List<SearchVO> fullTextSearchForSearching(String param, int start,
			int noOfRecords, String locale, Map<String, String[]> requestMap) {
			String orderByQuery=" ORDER BY ro.number ASC, s.start_date DESC, dt.id ASC";
			/**** Condition 1 :must not contain processed question ****/
			/**** Condition 2 :parent must be null ****/
			String selectQuery="SELECT ro.id as id,ro.number as number,"
					+"  ro.subject as subject,"
					+"  ro.revised_subject as revisedSubject,"
					+"  ro.notice_content as noticeContent,"
					+"  ro.revised_notice_content as revisedNoticeContent,"
					+"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"
					+"  sety.session_type as sessionType,"
					+"  mi.name as ministry,"
					+"  sd.name as subdepartment,st.type as statustype," 
					+"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"
					+"  ro.discussion_date as discussionDate,"
					+"  CASE WHEN ro.localized_actor_name_lower_house<>NULL && ro.localized_actor_name_lower_house<>'' THEN ro.localized_actor_name_lower_house"
					+" 		 WHEN ro.localized_actor_name_upper_house<>NULL && ro.localized_actor_name_upper_house<>'' THEN ro.localized_actor_name_upper_house "
					+" 		 ELSE '-' "
					+"  END as actor" 
					+"  FROM resolutions as ro "
					+"  LEFT JOIN housetypes as ht ON(ro.housetype_id=ht.id) "
					+"  LEFT JOIN sessions as s ON(ro.session_id=s.id) "
					+"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "
					+"  LEFT JOIN status as st ON(ro.lowerhouse_recommendationstatus_id=st.id OR ro.upperhouse_recommendationstatus_id=st.id) "
					+"  LEFT JOIN devicetypes as dt ON(ro.devicetype_id=dt.id) "
					+"  LEFT JOIN members as m ON(ro.member_id=m.id) "
					+"  LEFT JOIN titles as t ON(m.title_id=t.id) "
					+"  LEFT JOIN ministries as mi ON(ro.ministry_id=mi.id) "
					+"  LEFT JOIN subdepartments as sd ON(ro.subdepartment_id=sd.id) "
					+"  WHERE ro.locale='"+locale+"'"
					+"  AND st.type NOT IN('resolution_incomplete','resolution_complete')";
			
			StringBuffer filter = new StringBuffer("");
			filter.append(addResolutionFilter(requestMap));
			
			String[] strSessionType = requestMap.get("sessionYear");
			String[] strSessionYear = requestMap.get("sessionType");
			
			if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
					|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
					|| (strSessionType == null && strSessionYear == null)){
				CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "RESOLUTION_SEARCH_USE_CURRENT_SESSION", "");
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
					searchQuery=" AND (( match(ro.subject,ro.notice_content,ro.revised_subject,ro.revised_notice_content) "+
							"against('"+param+"' in natural language mode)"+
							")||ro.subject LIKE '%"+param+"%'||ro.notice_content LIKE '%"+param+"%'||ro.revised_subject LIKE '%"+param+"%'||ro.revised_notice_content LIKE '%"+param+"%')";
				}else if(param.contains("+")&&!param.contains("-")){
					String[] parameters = param.split("\\+");
					StringBuffer buffer = new StringBuffer();
					for(String i : parameters){
						buffer.append("+"+i+" ");
					}
					
					searchQuery =" AND match(ro.subject,ro.notice_content,ro.revised_subject,ro.revised_notice_content) "+
							"against('"+buffer.toString()+"' in boolean  mode)";
				}else if(!param.contains("+")&&param.contains("-")){
					String[] parameters=param.split("-");
					StringBuffer buffer=new StringBuffer();
					for(String i:parameters){
						buffer.append(i+" "+"-");
					}
					buffer.deleteCharAt(buffer.length()-1);
					searchQuery=" AND match(ro.subject,ro.notice_content,ro.revised_subject,ro.revised_notice_content) "+
							"against('"+buffer.toString()+"' in boolean  mode)";
				}else if(param.contains("+")||param.contains("-")){
					searchQuery=" AND match(ro.subject,ro.notice_content,ro.revised_subject,ro.revised_notice_content) "+
							"against('"+param+"' in boolean  mode)";
				}	
				
				query = selectQuery + filter + searchQuery + orderByQuery;
			}
			/**** Final Query ****/
			String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.noticeContent, "+
					" rs.revisedNoticeContent,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.discussionDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

			List results=this.em().createNativeQuery(finalQuery).getResultList();
			List<SearchVO> resolutionSearchVOs=new ArrayList<SearchVO>();
			if(results!=null){
				for(Object i:results){
					Object[] o=(Object[]) i;
					SearchVO resolutionSearchVO=new SearchVO();
					if(o[0]!=null){
						resolutionSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1]!=null){
						resolutionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
					}
					if(o[3]!=null){
						if(!o[3].toString().isEmpty()){
							resolutionSearchVO.setSubject(higlightText(o[3].toString(),param));
						}else{
							if(o[2]!=null){
								resolutionSearchVO.setSubject(higlightText(o[2].toString(),param));
							}
						}
					}else{
						if(o[2]!=null){
							resolutionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}				
					if(o[5]!=null){
						if(!o[5].toString().isEmpty()){
							resolutionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
						}else{
							if(o[4]!=null){
								resolutionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
							}
						}
					}else{
						if(o[4]!=null){
							resolutionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
					if(o[6]!=null){
						resolutionSearchVO.setStatus(o[6].toString());
					}
					if(o[7]!=null){
						resolutionSearchVO.setDeviceType(o[7].toString());
					}
					if(o[8]!=null){
						resolutionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
					}
					if(o[9]!=null){
						resolutionSearchVO.setSessionType(o[9].toString());
					}
					
					if(o[10]!=null){
						resolutionSearchVO.setMinistry(o[10].toString());
					}
					
					if(o[11]!=null){
						resolutionSearchVO.setSubDepartment(o[11].toString());
					}
					if(o[12]!=null){
						resolutionSearchVO.setStatusType(o[12].toString());
					}
					if(o[13]!=null){
						resolutionSearchVO.setFormattedPrimaryMember(o[13].toString());
					}
					if(o[14]!=null){
						resolutionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[14].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
					}
					if(o[15]!=null){
						resolutionSearchVO.setActor(o[15].toString());
					}
					resolutionSearchVOs.add(resolutionSearchVO);
				}
			}
			return resolutionSearchVOs;
	}
	
	
	private String addResolutionFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND ro.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND ro.member_id=" + member);
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_PROCESSED_PASSED+"')");
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

	public Long findReferencedEntity(Resolution domain) {
		String strQuery = "SELECT re.id FROM referenced_entities re "
				+ "			JOIN resolutions ro ON (re.id=ro.referenced_resolution)"
				+ "         WHERE ro.id=:resolutionId";
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("resolutionId", domain.getId());
		try{
		BigInteger referencedEntityId = (BigInteger) query.getSingleResult();
		//ReferencedEntity referencedEntity = ReferencedEntity.findById(ReferencedEntity.class, Long.parseLong(referencedEntityId.toString()));
		return Long.parseLong(referencedEntityId.toString());
		}catch(Exception e){
			return null;
		}
	}

	public Resolution findReferencedResolution(Resolution referencedResolution) {
		String strQuery = "SELECT device FROM referenced_entities re"
				+ " JOIN resolutions ro ON (re.id=ro.referenced_resolution)"
				+ " WHERE ro.id=:resolutionId";
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("resolutionId", referencedResolution.getId());
		try{
		BigInteger resolutionId = (BigInteger) query.getSingleResult();
		Resolution resolution = Resolution.findById(Resolution.class, Long.parseLong(resolutionId.toString()));
		return resolution;
		}catch(Exception e){
			return null;
		}
		
	}

}
