package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.AdjournmentMotionDraft;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class AdjournmentMotionRepository extends BaseRepository<AdjournmentMotion, Serializable> {

	public Integer assignMotionNo(final HouseType houseType, final Date adjourningDate, final String locale) {
		String strQuery = "SELECT m FROM AdjournmentMotion m " +
				"WHERE m.adjourningDate=:adjourningDate " +
				"AND m.houseType.id=:houseTypeId " +
				"AND m.locale=:locale " +
				"ORDER BY m.number DESC";
		TypedQuery<AdjournmentMotion> query = this.em().createQuery(strQuery, AdjournmentMotion.class);
		query.setParameter("adjourningDate", adjourningDate);
		query.setParameter("houseTypeId", houseType.getId());
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<AdjournmentMotion> adjournmentMotions = query.getResultList();
		if(adjournmentMotions==null || adjournmentMotions.isEmpty()) {
			return 0;
		} else {
			return adjournmentMotions.get(0).getNumber()==null? 0 : adjournmentMotions.get(0).getNumber();
		}		
	}
	
	public Integer assignAdmissionNumber(final Session session, final String locale) {
		String strQuery = "SELECT m FROM AdjournmentMotion m" +
				" JOIN m.status sta" +
				" WHERE m.session.id=:sessionId" +
				" AND sta.type=:admissionStatusType" +
				" AND m.admissionNumber IS NOT NULL" +
				" AND m.locale=:locale " +
				" ORDER BY m.admissionNumber DESC";
		TypedQuery<AdjournmentMotion> query = this.em().createQuery(strQuery, AdjournmentMotion.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("admissionStatusType", ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION);
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<AdjournmentMotion> adjournmentMotions = query.getResultList();
		if(adjournmentMotions==null || adjournmentMotions.isEmpty()) {
			return 1;
		} else {
			return adjournmentMotions.get(0).getAdmissionNumber()==null? 1 : adjournmentMotions.get(0).getAdmissionNumber()+1;
		}		
	}
	
	public Integer findContinuationNumber(final AdjournmentMotion adjournmentMotion) {
		Integer continuationCount = null;
		
		String queryString = "SELECT COUNT(DISTINCT am.id) FROM adjournmentmotions am" +
				" INNER JOIN status sta ON (sta.id=am.status_id)" +
				" WHERE am.session_id=:sessionId" +
				" AND am.number IS NOT NULL" +
				" AND sta.type LIKE '%\\final_admission'" +
				" AND am.adjourning_date<:adjourningDate" +
				" AND am.locale=:locale";
		Query query = this.em().createNativeQuery(queryString);
		query.setParameter("sessionId", adjournmentMotion.getSession().getId());
		query.setParameter("adjourningDate", adjournmentMotion.getAdjourningDate());
		query.setParameter("locale", adjournmentMotion.getLocale());
		try {
			//Integer countBeforeAdjourningDate = (Integer) query.getSingleResult();
			Integer countBeforeAdjourningDate = ((BigInteger) query.getSingleResult()).intValue();
			if(countBeforeAdjourningDate!=null && adjournmentMotion.getNumber()!=null) {
				continuationCount = countBeforeAdjourningDate.intValue() + adjournmentMotion.getNumber().intValue();
			} else {
				if(adjournmentMotion.getNumber()!=null) {
					continuationCount = adjournmentMotion.getNumber();
				}			
			}
		} catch(NoResultException nre) {
			if(adjournmentMotion.getNumber()!=null) {
				continuationCount = adjournmentMotion.getNumber();
			}
		} catch(Exception e) {
			if(adjournmentMotion.getNumber()!=null) {
				continuationCount = adjournmentMotion.getNumber();
			}
		}
		
		return continuationCount;
	}
	
	public List<AdjournmentMotion> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.ADJOURNMENTMOTION_COMPLETE, locale);
			String query="SELECT m FROM AdjournmentMotion m WHERE m.session.id=:sessionId"+
					" AND m.type.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<AdjournmentMotion> m=this.em().createQuery(query, AdjournmentMotion.class);
			m.setParameter("sessionId", session.getId());
			m.setParameter("deviceTypeId", motionType.getId());
			m.setParameter("memberId", primaryMember.getId());
			m.setParameter("locale", locale);
			m.setParameter("statusId", status.getId());
			m.setMaxResults(itemsCount);
			return m.getResultList();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("AdjournmentMotionRepository_List<AdjournmentMotion>_findAllByMember", "Cannot get the AdjournmentMotions ");
			throw elsException;
		}
	}

	public Boolean isDuplicateNumberExist(final Date adjourningDate, 
			final Integer number,
			final Long id,
			final String locale) {
		// TODO Auto-generated method stub
		String strQuery = "SELECT m FROM AdjournmentMotion m" +
				" WHERE" +
				" m.adjourningDate=:adjourningDate" +
				" AND m.number=:number" +
				" AND m.id<>:motionId" +
				" AND m.locale=:locale";
		TypedQuery<AdjournmentMotion> query = this.em().createQuery(strQuery, AdjournmentMotion.class);
		query.setParameter("adjourningDate", adjourningDate);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("motionId", id);
		} else {
			query.setParameter("motionId", new Long("0"));
		}
		List<AdjournmentMotion> adjournmentMotions = query.getResultList();
		if(adjournmentMotions!=null && !adjournmentMotions.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	public AdjournmentMotionDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM AdjournmentMotion m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<AdjournmentMotionDraft> tQuery = 
			this.em().createQuery(query, AdjournmentMotionDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		AdjournmentMotionDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final AdjournmentMotion adjournmentMotion) {
		String strQuery = "SELECT ce FROM AdjournmentMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:adjournmentMotionId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("adjournmentMotionId", adjournmentMotion.getId());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final AdjournmentMotion adjournmentMotion, final String sortOrder) {
		String strQuery = "SELECT ce FROM AdjournmentMotion m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:adjournmentMotionId ORDER BY ce.adjournmentMotion.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("adjournmentMotionId", adjournmentMotion.getId());
		return query.getResultList();
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param adjournmentMotionId the adjournmentMotion id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long adjournmentMotionId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.ADJOURNMENTMOTION_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("adjournmentmotionId",adjournmentMotionId);
		List results = query.getResultList();
		List<RevisionHistoryVO> adjournmentMotionRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO adjournmentMotionRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				adjournmentMotionRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				adjournmentMotionRevisionVO.setEditedAs(userGroupType.getName());
			}
			adjournmentMotionRevisionVO.setEditedBY(o[1].toString());
			adjournmentMotionRevisionVO.setEditedOn(o[2].toString());
			adjournmentMotionRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String noticeContent=d.diff_prettyHtml(diff);
					if(noticeContent.contains("&lt;")){
						noticeContent=noticeContent.replaceAll("&lt;", "<");
					}
					if(noticeContent.contains("&gt;")){
						noticeContent=noticeContent.replaceAll("&gt;", ">");
					}
					if(noticeContent.contains("&amp;nbsp;")){
						noticeContent=noticeContent.replaceAll("&amp;nbsp;"," ");
					}
					adjournmentMotionRevisionVO.setDetails(noticeContent);
				}else{
					adjournmentMotionRevisionVO.setDetails(o[4].toString());
				}

			}else{
				adjournmentMotionRevisionVO.setDetails(o[4].toString());
			}
			if(o1!=null){
				if(!o[5].toString().isEmpty() && !o1[5].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[5].toString(), o[5].toString());
					String subject=d.diff_prettyHtml(diff);
					if(subject.contains("&lt;")){
						subject=subject.replaceAll("&lt;", "<");
					}
					if(subject.contains("&gt;")){
						subject=subject.replaceAll("&gt;", ">");
					}
					if(subject.contains("&amp;nbsp;")){
						subject=subject.replaceAll("&amp;nbsp;"," ");
					}
					adjournmentMotionRevisionVO.setSubject(subject);
				}else{
					adjournmentMotionRevisionVO.setSubject(o[5].toString());
				}

			}else{
				adjournmentMotionRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				adjournmentMotionRevisionVO.setRemarks(o[6].toString());
			}			
			adjournmentMotionRevisionVOs.add(adjournmentMotionRevisionVO);
		}
		return adjournmentMotionRevisionVOs;
	}
	
	public List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY am.number ASC, s.start_date DESC, dt.id ASC";
		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT am.id as id,am.number as number,"
				+"  am.subject as subject,"
				+"  am.revised_subject as revisedSubject,"
				+"  am.notice_content as noticeContent,"
				+"  am.revised_notice_content as revisedNoticeContent,"
				+"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"
				+"  sety.session_type as sessionType,"
				+"  mi.name as ministry,"
				+"  sd.name as subdepartment,st.type as statustype," 
				+"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"
				+"  am.adjourning_date as adjourningDate,"
				+"  am.localized_actor_name as actor" 
				+"  FROM adjournmentmotions as am "
				+"  LEFT JOIN housetypes as ht ON(am.housetype_id=ht.id) "
				+"  LEFT JOIN sessions as s ON(am.session_id=s.id) "
				+"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "
				+"  LEFT JOIN status as st ON(am.recommendationstatus_id=st.id) "
				+"  LEFT JOIN devicetypes as dt ON(am.devicetype_id=dt.id) "
				+"  LEFT JOIN members as m ON(am.member_id=m.id) "
				+"  LEFT JOIN titles as t ON(m.title_id=t.id) "
				+"  LEFT JOIN ministries as mi ON(am.ministry_id=mi.id) "
				+"  LEFT JOIN subdepartments as sd ON(am.subdepartment_id=sd.id) "
				+"  WHERE am.locale='"+locale+"'"
				+"  AND st.type NOT IN('adjournmentmotion_incomplete','adjournmentmotion_complete')"
				+"  AND am.number iS NOT NULL";
		
		StringBuffer filter = new StringBuffer("");
		filter.append(addSpecialMentionNoticeFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "ADJOURNMENTMOTION_SEARCH_USE_CURRENT_SESSION", "");
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
				"rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.adjourningDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> adjournmentMotionSearchVOs=new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO adjournmentMotionSearchVO=new SearchVO();
				if(o[0]!=null){
					adjournmentMotionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					adjournmentMotionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						adjournmentMotionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							adjournmentMotionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						adjournmentMotionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						adjournmentMotionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							adjournmentMotionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						adjournmentMotionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					adjournmentMotionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					adjournmentMotionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					adjournmentMotionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					adjournmentMotionSearchVO.setSessionType(o[9].toString());
				}
				
				if(o[10]!=null){
					adjournmentMotionSearchVO.setMinistry(o[10].toString());
				}
				
				if(o[11]!=null){
					adjournmentMotionSearchVO.setSubDepartment(o[11].toString());
				}
				if(o[12]!=null){
					adjournmentMotionSearchVO.setStatusType(o[12].toString());
				}
				if(o[13]!=null){
					adjournmentMotionSearchVO.setFormattedPrimaryMember(o[13].toString());
				}
				if(o[14]!=null){
					adjournmentMotionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[14].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[15]!=null){
					adjournmentMotionSearchVO.setActor(o[15].toString());
				}
				adjournmentMotionSearchVOs.add(adjournmentMotionSearchVO);
			}
		}
		return adjournmentMotionSearchVOs;
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION+"')");
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

}
