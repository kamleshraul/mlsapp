package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.SpecialMentionNoticeDraft;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

@Repository
public class SpecialMentionNoticeRepository  extends BaseRepository<SpecialMentionNotice, Serializable> {
	public Integer assignMotionNo(final HouseType houseType, final Date specialMentionNoticeDate, final String locale) {
		String strQuery = "SELECT m FROM SpecialMentionNotice m " +
				"WHERE m.specialMentionNoticeDate=:specialMentionNoticeDate " +
				"AND m.houseType.id=:houseTypeId " +
				"AND m.locale=:locale " +
				"ORDER BY m.number DESC";
		TypedQuery<SpecialMentionNotice> query = this.em().createQuery(strQuery, SpecialMentionNotice.class);
		query.setParameter("specialMentionNoticeDate", specialMentionNoticeDate);
		query.setParameter("houseTypeId", houseType.getId());
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<SpecialMentionNotice> specialMentionNotices = query.getResultList();
		if(specialMentionNotices==null || specialMentionNotices.isEmpty()) {
			return 0;
		} else {
			return specialMentionNotices.get(0).getNumber()==null? 0 : specialMentionNotices.get(0).getNumber();
		}		
	}
	
	public Integer assignAdmissionNumber(final Session session, final String locale) {
		String strQuery = "SELECT m FROM SpecialMentionNotice m" +
				" JOIN m.status sta" +
				" WHERE m.session.id=:sessionId" +
				" AND sta.type=:admissionStatusType" +
				" AND m.admissionNumber IS NOT NULL" +
				" AND m.locale=:locale " +
				" ORDER BY m.admissionNumber DESC";
		TypedQuery<SpecialMentionNotice> query = this.em().createQuery(strQuery, SpecialMentionNotice.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("admissionStatusType", ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION);
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<SpecialMentionNotice>  specialMentions = query.getResultList();
		if(specialMentions==null || specialMentions.isEmpty()) {
			return 1;
		} else {
			return specialMentions.get(0).getAdmissionNumber()==null? 1 : specialMentions.get(0).getAdmissionNumber()+1;
		}		
	}
	
	public List<SpecialMentionNotice> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SPECIALMENTIONNOTICE_COMPLETE, locale);
			String query="SELECT m FROM SpecialMentionNotice m WHERE m.session.id=:sessionId"+
					" AND m.type.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<SpecialMentionNotice> m=this.em().createQuery(query, SpecialMentionNotice.class);
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
			elsException.setParameter("SpecialMentionNoticeRepository_List<SpecialMentionNotice>_findAllByMember", "Cannot get the SpecialMentionNotices ");
			throw elsException;
		}
	}
	
	public Boolean isDuplicateNumberExist(final Date specialMentionNoticeDate, 
			final Integer number,
			final Long id,
			final String locale) {
		// TODO Auto-generated method stub
		String strQuery = "SELECT smn FROM SpecialMentionNotice smn" +
				" WHERE" +
				" smn.specialMentionNoticeDate=:specialMentionNoticeDate" +
				" AND smn.number=:number" +
				" AND smn.id<>:motionId" +
				" AND smn.locale=:locale";
		TypedQuery<SpecialMentionNotice> query = this.em().createQuery(strQuery, SpecialMentionNotice.class);
		query.setParameter("specialMentionNoticeDate", specialMentionNoticeDate);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("motionId", id);
		} else {
			query.setParameter("motionId", new Long("0"));
		}
		List<SpecialMentionNotice> specialMentionNotices = query.getResultList();
		if(specialMentionNotices!=null && !specialMentionNotices.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final SpecialMentionNotice specialMentionNotice) {
		String strQuery = "SELECT ce FROM SpecialMentionNotice m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:specialMentionNoticeId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("specialMentionNoticeId", specialMentionNotice.getId());
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final SpecialMentionNotice specialMentionNotice, final String sortOrder) {
		String strQuery = "SELECT ce FROM SpecialMentionNotice m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:specialMentionNoticeId ORDER BY ce.specialMentionNotice.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("specialMentionNoticeId", specialMentionNotice.getId());
		return query.getResultList();
	}
	
	public SpecialMentionNoticeDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM SpecialMentionNotice m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<SpecialMentionNoticeDraft> tQuery = 
			this.em().createQuery(query, SpecialMentionNoticeDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		SpecialMentionNoticeDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param specialMentionNoticeId the specialMentionNotice id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long specialMentionNoticeId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.SPECIALMENTIONNOTICEN_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("specialMentionNoticeId",specialMentionNoticeId);
		List results = query.getResultList();
		List<RevisionHistoryVO> specialMentionNoticeRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO specialMentionNoticeRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				specialMentionNoticeRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				specialMentionNoticeRevisionVO.setEditedAs(userGroupType.getName());
			}
			specialMentionNoticeRevisionVO.setEditedBY(o[1].toString());
			specialMentionNoticeRevisionVO.setEditedOn(o[2].toString());
			specialMentionNoticeRevisionVO.setStatus(o[3].toString());
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
					specialMentionNoticeRevisionVO.setDetails(noticeContent);
				}else{
					specialMentionNoticeRevisionVO.setDetails(o[4].toString());
				}

			}else{
				specialMentionNoticeRevisionVO.setDetails(o[4].toString());
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
					specialMentionNoticeRevisionVO.setSubject(subject);
				}else{
					specialMentionNoticeRevisionVO.setSubject(o[5].toString());
				}

			}else{
				specialMentionNoticeRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				specialMentionNoticeRevisionVO.setRemarks(o[6].toString());
			}			
			specialMentionNoticeRevisionVOs.add(specialMentionNoticeRevisionVO);
		}
		return specialMentionNoticeRevisionVOs;
	}
	
	public List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY sm.number ASC, s.start_date DESC, dt.id ASC";
		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT sm.id as id,sm.number as number,"
				+"  sm.subject as subject,"
				+"  sm.revised_subject as revisedSubject,"
				+"  sm.notice_content as noticeContent,"
				+"  sm.revised_notice_content as revisedNoticeContent,"
				+"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"
				+"  sety.session_type as sessionType,"
				+"  mi.name as ministry,"
				+"  sd.name as subdepartment,st.type as statustype," 
				+"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"
				+"  sm.specialmentionnotice_date as specialMentionNoticeDate,"
				+"  sm.localized_actor_name as actor" 
				+"  FROM specialmentionnotice as sm "
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
				+"  AND st.type NOT IN('specialmentionnotice_incomplete','specialmentionnotice_complete')"
				+"  AND sm.number iS NOT NULL";
		
		StringBuffer filter = new StringBuffer("");
		filter.append(addSpecialMentionNoticeFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "SPECIALMENTIONNOTICE_SEARCH_USE_CURRENT_SESSION", "");
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
				searchQuery=" AND (( match(sm.subject,sm.notice_content,sm.revised_subject,sm.revised_notice_content) "+
						"against('"+param+"' in natural language mode)"+
						")||sm.subject LIKE '%"+param+"%'||sm.notice_content LIKE '%"+param+
						"%'||sm.revised_subject LIKE '%"+param+"%'||sm.revised_notice_content LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(sm.subject,sm.notice_content,sm.revised_subject,sm.revised_notice_content) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(sm.subject,sm.notice_content,sm.revised_subject,sm.revised_notice_content) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(sm.subject,sm.notice_content,sm.reason,sm.revised_subject,sm.revised_notice_content) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.noticeContent,rs.revisedNoticeContent, "+
				"rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.specialMentionNoticeDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> specialMentionNoticeSearchVOs=new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO specialMentionNoticeSearchVO=new SearchVO();
				if(o[0]!=null){
					specialMentionNoticeSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					specialMentionNoticeSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						specialMentionNoticeSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							specialMentionNoticeSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						specialMentionNoticeSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						specialMentionNoticeSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							specialMentionNoticeSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						specialMentionNoticeSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					specialMentionNoticeSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					specialMentionNoticeSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					specialMentionNoticeSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					specialMentionNoticeSearchVO.setSessionType(o[9].toString());
				}
				
				if(o[10]!=null){
					specialMentionNoticeSearchVO.setMinistry(o[10].toString());
				}
				
				if(o[11]!=null){
					specialMentionNoticeSearchVO.setSubDepartment(o[11].toString());
				}
				if(o[12]!=null){
					specialMentionNoticeSearchVO.setStatusType(o[12].toString());
				}
				if(o[13]!=null){
					specialMentionNoticeSearchVO.setFormattedPrimaryMember(o[13].toString());
				}
				if(o[14]!=null){
					specialMentionNoticeSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[14].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[15]!=null){
					specialMentionNoticeSearchVO.setActor(o[15].toString());
				}
				specialMentionNoticeSearchVOs.add(specialMentionNoticeSearchVO);
			}
		}
		return specialMentionNoticeSearchVOs;
	}
	
	private String addSpecialMentionNoticeFilter(Map<String, String[]> requestMap) {
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION+"')");
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
