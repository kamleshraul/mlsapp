package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.ProprietyPointDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class ProprietyPointRepository extends BaseRepository<ProprietyPoint, Serializable> {

	public Integer assignNumber(final HouseType houseType,final Session session,
			final DeviceType type,final String locale) {
		String strProprietyPointType = type.getType();
		String strQuery = "SELECT m FROM ProprietyPoint m"
				+ " JOIN m.session s"
				+ " JOIN m.deviceType dt"
				+ " WHERE dt.type =:proprietyPointType"
				+ " AND s.id=:sessionId"
				+ " ORDER BY m.number " + ApplicationConstants.DESC;	
		try {
			TypedQuery<ProprietyPoint> query=this.em().createQuery(strQuery, ProprietyPoint.class);
			query.setParameter("proprietyPointType",strProprietyPointType);
			query.setParameter("sessionId",session.getId());
			List<ProprietyPoint> proprietyPoints = query.setFirstResult(0).setMaxResults(1).getResultList();
			
			if(proprietyPoints == null) {
				return 0;
			}else if(proprietyPoints.isEmpty()) {
				return 0;
			}else {
				if(proprietyPoints.get(0).getNumber() == null) {
					return 0;
				}else{
					return proprietyPoints.get(0).getNumber();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public Integer assignNumber(final HouseType houseType,
								final Session session,
								final Date proprietyPointDate,
								final String locale) {
		boolean isTodaySessionEndDate = false;
		if(DateUtil.compareDatePartOnly(new Date(), session.getEndDate())==0) {
			isTodaySessionEndDate = true;
		}
		String strQuery = "SELECT MAX(number) FROM propriety_points"
				+ " WHERE session_id=:sessionId"
				+ " AND propriety_point_date=:proprietyPointDate"
				+ " AND (CASE WHEN :isTodaySessionEndDate IS TRUE AND number IS NOT NULL THEN DATE(submission_date)=:sessionEndDate ELSE TRUE END)";
		try {
			Query query=this.em().createNativeQuery(strQuery);
			query.setParameter("sessionId",session.getId());
			query.setParameter("proprietyPointDate", proprietyPointDate);
			query.setParameter("isTodaySessionEndDate", isTodaySessionEndDate);
			query.setParameter("sessionEndDate", session.getEndDate());
			
			Integer maxNumber = (Integer) query.getSingleResult();
			if(maxNumber == null) {
				return 0;
			} else {
				return maxNumber;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public Integer assignAdmissionNumber(final Session session, final String locale) {
		String strQuery = "SELECT m FROM ProprietyPoint m" +
				" JOIN m.status sta" +
				" WHERE m.session.id=:sessionId" +
				" AND sta.type=:admissionStatusType" +
				" AND m.admissionNumber IS NOT NULL" +
				" AND m.locale=:locale " +
				" ORDER BY m.admissionNumber DESC";
		TypedQuery<ProprietyPoint> query = this.em().createQuery(strQuery, ProprietyPoint.class);
		query.setParameter("sessionId", session.getId());
		query.setParameter("admissionStatusType", ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION);
		query.setParameter("locale", locale);
		query.setMaxResults(1);
		List<ProprietyPoint> proprietyPoints = query.getResultList();
		if(proprietyPoints==null || proprietyPoints.isEmpty()) {
			return 1;
		} else {
			return proprietyPoints.get(0).getAdmissionNumber()==null? 1 : proprietyPoints.get(0).getAdmissionNumber()+1;
		}		
	}
	
	public List<ProprietyPoint> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		try{
			Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.PROPRIETYPOINT_COMPLETE, locale);
			String query="SELECT m FROM ProprietyPoint m WHERE m.session.id=:sessionId"+
					" AND m.deviceType.id=:deviceTypeId AND m.primaryMember.id=:memberId"+
					" AND m.locale=:locale AND m.status.id=:statusId"+
					" ORDER BY m.id DESC";
			TypedQuery<ProprietyPoint> m=this.em().createQuery(query, ProprietyPoint.class);
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
			elsException.setParameter("ProprietyPointRepository_List<ProprietyPoint>_findAllByMember", "Cannot get the ProprietyPoints ");
			throw elsException;
		}
	}

	public Boolean isDuplicateNumberExist(final Integer number,	final Long id, final String locale) {
		String strQuery = "SELECT m FROM ProprietyPoint m" +
				" WHERE m.number=:number" +
				" AND m.id<>:proprietyPointId" +
				" AND m.locale=:locale";
		TypedQuery<ProprietyPoint> query = this.em().createQuery(strQuery, ProprietyPoint.class);
		query.setParameter("number", number);
		query.setParameter("locale", locale);
		if(id!=null) {
			query.setParameter("proprietyPointId", id);
		} else {
			query.setParameter("proprietyPointId", new Long("0"));
		}
		List<ProprietyPoint> proprietyPoints = query.getResultList();
		if(proprietyPoints!=null && !proprietyPoints.isEmpty()) {
			return true;
		} else {
			return false;
		}		
	}
	
	public ProprietyPointDraft findPreviousDraft(final Long id) {
		String query = "SELECT md" +
				" FROM ProprietyPoint m join m.drafts md" +
				" WHERE m.id=:mid" +
				" ORDER BY md.id DESC";
		try{
		TypedQuery<ProprietyPointDraft> tQuery = 
			this.em().createQuery(query, ProprietyPointDraft.class);
		tQuery.setParameter("mid", id);
		tQuery.setMaxResults(1);
		ProprietyPointDraft draft = tQuery.getSingleResult();
		return draft;
		}catch(Exception e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByPosition(final ProprietyPoint proprietyPoint) {
		String strQuery = "SELECT ce FROM ProprietyPoint m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:proprietyPointId ORDER BY ce.position " + ApplicationConstants.ASC;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("proprietyPointId", proprietyPoint.getId());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ClubbedEntity> findClubbedEntitiesByDeviceNumber(final ProprietyPoint proprietyPoint, final String sortOrder) {
		String strQuery = "SELECT ce FROM ProprietyPoint m JOIN m.clubbedEntities ce" +
				" WHERE m.id =:proprietyPointId ORDER BY ce.proprietyPoint.number " + sortOrder;
		Query query=this.em().createQuery(strQuery);
		query.setParameter("proprietyPointId", proprietyPoint.getId());
		return query.getResultList();
	}
	
	/**
	 * Gets the revisions.
	 *
	 * @param proprietyPointId the proprietyPoint id
	 * @param locale the locale
	 * @return the revisions
	 */
	@SuppressWarnings("rawtypes")
	public List<RevisionHistoryVO> getRevisions(final Long proprietyPointId, final String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.PROPRIETYPOINT_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("proprietypointId",proprietyPointId);
		List results = query.getResultList();
		List<RevisionHistoryVO> proprietyPointRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO proprietyPointRevisionVO = new RevisionHistoryVO();
			if(o[0] != null) {
				proprietyPointRevisionVO.setEditedAs(o[0].toString());
			}
			else {
				UserGroupType userGroupType = 
						UserGroupType.findByFieldName(UserGroupType.class, "type", "member", locale);
				proprietyPointRevisionVO.setEditedAs(userGroupType.getName());
			}
			proprietyPointRevisionVO.setEditedBY(o[1].toString());
			proprietyPointRevisionVO.setEditedOn(o[2].toString());
			proprietyPointRevisionVO.setStatus(o[3].toString());
			if(o1!=null){
				if(!o[4].toString().isEmpty() && !o1[4].toString().isEmpty()){
					LinkedList<Diff> diff=d.diff_main(o1[4].toString(), o[4].toString());
					String pointsOfPropriety=d.diff_prettyHtml(diff);
					if(pointsOfPropriety.contains("&lt;")){
						pointsOfPropriety=pointsOfPropriety.replaceAll("&lt;", "<");
					}
					if(pointsOfPropriety.contains("&gt;")){
						pointsOfPropriety=pointsOfPropriety.replaceAll("&gt;", ">");
					}
					if(pointsOfPropriety.contains("&amp;nbsp;")){
						pointsOfPropriety=pointsOfPropriety.replaceAll("&amp;nbsp;"," ");
					}
					proprietyPointRevisionVO.setDetails(pointsOfPropriety);
				}else{
					proprietyPointRevisionVO.setDetails(o[4].toString());
				}

			}else{
				proprietyPointRevisionVO.setDetails(o[4].toString());
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
					proprietyPointRevisionVO.setSubject(subject);
				}else{
					proprietyPointRevisionVO.setSubject(o[5].toString());
				}

			}else{
				proprietyPointRevisionVO.setSubject(o[5].toString());
			}
			if(o[6] != null){
				proprietyPointRevisionVO.setRemarks(o[6].toString());
			}			
			proprietyPointRevisionVOs.add(proprietyPointRevisionVO);
		}
		return proprietyPointRevisionVOs;
	}
	
	public List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY pp.number ASC, s.start_date DESC, dt.id ASC";
		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT pp.id as id,pp.number as number,"
				+"  pp.subject as subject,"
				+"  pp.revised_subject as revisedSubject,"
				+"  pp.points_of_propriety as pointsOfPropriety,"
				+"  pp.revised_points_of_propriety as revisedPointsOfPropriety,"
				+"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"
				+"  sety.session_type as sessionType,"
				+"  mi.name as ministry,"
				+"  sd.name as subdepartment,st.type as statustype," 
				+"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"
				+"  pp.propriety_point_date as proprietyPointDate,"
				+"  pp.localized_actor_name as actor" 
				+"  FROM propriety_points as pp "
				+"  LEFT JOIN housetypes as ht ON(pp.housetype_id=ht.id) "
				+"  LEFT JOIN sessions as s ON(pp.session_id=s.id) "
				+"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "
				+"  LEFT JOIN status as st ON(pp.recommendationstatus_id=st.id) "
				+"  LEFT JOIN devicetypes as dt ON(pp.devicetype_id=dt.id) "
				+"  LEFT JOIN members as m ON(pp.member_id=m.id) "
				+"  LEFT JOIN titles as t ON(m.title_id=t.id) "
				+"  LEFT JOIN ministries as mi ON(pp.ministry_id=mi.id) "
				+"  LEFT JOIN subdepartments as sd ON(pp.subdepartment_id=sd.id) "
				+"  WHERE pp.locale='"+locale+"'"
				+"  AND st.type NOT IN('proprietypoint_incomplete','proprietypoint_complete')"
				+"  AND pp.number iS NOT NULL";
		
		StringBuffer filter = new StringBuffer("");
		filter.append(addProprietyPointFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "PROPRIETYPOINT_SEARCH_USE_CURRENT_SESSION", "");
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
				searchQuery=" AND (( match(pp.subject,pp.points_of_propriety,pp.revised_subject,pp.revised_points_of_propriety) "+
						"against('"+param+"' in natural language mode)"+
						")||pp.subject LIKE '%"+param+"%'||pp.points_of_propriety LIKE '%"+param+
						"%'||pp.revised_subject LIKE '%"+param+"%'||pp.revised_points_of_propriety LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(pp.subject,pp.points_of_propriety,pp.revised_subject,pp.revised_points_of_propriety) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(pp.subject,pp.points_of_propriety,pp.revised_subject,pp.revised_points_of_propriety) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(pp.subject,pp.points_of_propriety,pp.reason,pp.revised_subject,pp.revised_points_of_propriety) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.pointsOfPropriety,rs.revisedPointsOfPropriety, "+
				"rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.proprietyPointDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> proprietyPointSearchVOs=new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO proprietyPointSearchVO=new SearchVO();
				if(o[0]!=null){
					proprietyPointSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					proprietyPointSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						proprietyPointSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							proprietyPointSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						proprietyPointSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						proprietyPointSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							proprietyPointSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						proprietyPointSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					proprietyPointSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					proprietyPointSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					proprietyPointSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					proprietyPointSearchVO.setSessionType(o[9].toString());
				}
				
				if(o[10]!=null){
					proprietyPointSearchVO.setMinistry(o[10].toString());
				}
				
				if(o[11]!=null){
					proprietyPointSearchVO.setSubDepartment(o[11].toString());
				}
				if(o[12]!=null){
					proprietyPointSearchVO.setStatusType(o[12].toString());
				}
				if(o[13]!=null){
					proprietyPointSearchVO.setFormattedPrimaryMember(o[13].toString());
				}
				if(o[14]!=null){
					proprietyPointSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[14].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[15]!=null){
					proprietyPointSearchVO.setActor(o[15].toString());
				}
				proprietyPointSearchVOs.add(proprietyPointSearchVO);
			}
		}
		return proprietyPointSearchVOs;
	}
	
	private String addProprietyPointFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND pp.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND pp.member_id=" + member);
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.PROPRIETYPOINT_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION+"')");
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
