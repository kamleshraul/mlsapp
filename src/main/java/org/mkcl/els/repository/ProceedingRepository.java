package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

@Repository
public class ProceedingRepository extends BaseRepository<Proceeding, Serializable>{

	public Boolean removePart(Proceeding proceeding, Long partId) {
		try{
			String query2="DELETE from bookmarks where master_part="+partId+" OR slave_part="+partId;
			this.em().createNativeQuery(query2).executeUpdate();
			String query = "DELETE FROM parts_drafts_association WHERE part_id ="+partId;
			this.em().createNativeQuery(query).executeUpdate();
			String query3="DELETE from parts WHERE id="+partId;
			this.em().createNativeQuery(query3).executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public List<Proceeding> findAllFilledProceedingBySlot(Slot s) {
		String strQuery="SELECT DISTINCT proc FROM Proceeding proc JOIN proc.parts p WHERE proc.slot=:slot";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("slot", s);
		List<Proceeding> proceedings=query.getResultList();
		return proceedings;
	}

	public List<RevisionHistoryVO> getRevisions(Long partId, String locale) {
		org.mkcl.els.domain.Query revisionQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.PART_GET_REVISION, "");
		String strquery = revisionQuery.getQuery();
		Query query=this.em().createNativeQuery(strquery);
		query.setParameter("partId",partId);
		query.setParameter("locale",locale);
		List results = query.getResultList();
		List<RevisionHistoryVO> partRevisionVOs = new ArrayList<RevisionHistoryVO>();
		diff_match_patch d=new diff_match_patch();
		for(int i=0;i<results.size();i++){
			Object[] o = (Object[]) results.get(i);
			Object[] o1=null;
			if(i+1<results.size()){
				o1=(Object[])results.get(i+1);
			}
			RevisionHistoryVO partRevisionVO = new RevisionHistoryVO();
			if(o[0]!=null) {
				partRevisionVO.setEditedBY(o[0].toString());
			} else {
				partRevisionVO.setEditedBY("");
			}
			if(o[1]!=null) {
				partRevisionVO.setEditedOn(o[1].toString());
			} else {
				partRevisionVO.setEditedOn("");
			}
			if(o[2]!=null && !o[2].toString().isEmpty() && o1 != null && o1[2]!=null && !o1[2].toString().isEmpty()){
				LinkedList<Diff> diff=d.diff_main(o1[2].toString(), o[2].toString());
				String partContent = d.diff_prettyHtml(diff);
				if(partContent.contains("&lt;")){
					partContent = partContent.replaceAll("&lt;", "<");
				}
				if(partContent.contains("&gt;")){
					partContent = partContent.replaceAll("&gt;", ">");
				}
				if(partContent.contains("&amp;nbsp;")){
					partContent = partContent.replaceAll("&amp;nbsp;"," ");
				}
				partRevisionVO.setDetails(partContent);
			}else{
				if(o[2]!=null) {
					partRevisionVO.setDetails(o[2].toString());
				} else {
					partRevisionVO.setDetails("");
				}					
			}
			partRevisionVOs.add(partRevisionVO);
		}
		return partRevisionVOs;
	}

	public List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		String orderByQuery=" GROUP By p.id ORDER BY ro.id,s.start_time ASC";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT p.id as id,ro.register_no as registerNumber,"
				+"  p.revised_content as content,"
				+"  ht.name as houseType,"
				+"  se.session_year as sessionYear,"
				+"  sety.session_type as sessionType,"
				+"  cn.name as committeeName,"
				+"  ro.start_time as rosterStartTime,"
				+"  ro.end_time as rosterEndTime,"
				+"  s.start_time as slotStartTime,"
				+"  s.end_time as slotEndTime,"
				+"  s.name as slotName,"
				+"  u.first_name as reporterName"
				+"  FROM parts as p "
				+"  JOIN proceedings as proc ON (p.proceeding=proc.id) "
				+"  JOIN slots as s ON (s.id=proc.slot)"
				+"	JOIN rosters as ro ON (s.roster=ro.id)"
				+"	JOIN rosters_reporters rr ON (rr.roster_id=ro.id)"
				+"	JOIN reporters r ON (rr.reporter_id=r.id)"
				+"	JOIN users u ON (r.user=u.id)"
				+"  LEFT JOIN sessions as se ON(ro.session=se.id) "
				+"  LEFT JOIN committee_meetings as cm ON(ro.committee_meeting=cm.id)"
				+"  LEFT JOIN committees as c ON(cm.committee_id=c.id)"
				+"  LEFT JOIN committee_names as cn ON(c.committee_name_id=cn.id)"
				+"  LEFT JOIN houses as h ON(se.house_id=h.id) "
				+"  LEFT JOIN housetypes as ht ON(h.housetype_id=ht.id) "
				+"  LEFT JOIN sessiontypes as sety ON(se.sessiontype_id=sety.id) "
				+"  WHERE p.locale='"+locale+"'";

		
		StringBuffer filter = new StringBuffer("");
		filter.append(addSearchFilter(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		String strModule = requestMap.get("module")[0];
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null) && strModule.isEmpty()){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, "PROCEEDING_SEARCH_USE_CURRENT_SESSION", "");
			if(csptUseCurrentSession != null && csptUseCurrentSession.getValue() != null 
					&& !csptUseCurrentSession.getValue().isEmpty() && csptUseCurrentSession.getValue().equalsIgnoreCase("yes")){
				HouseType assembly = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
				HouseType council = HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale.toString());
				Session assemblySession = null;
				Session councilSession = null;
				try {
					assemblySession = Session.findLatestSession(assembly);
					councilSession = Session.findLatestSession(council);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(assemblySession != null && councilSession != null){
					filter.append(" AND se.id IN (" + assemblySession.getId()+","+councilSession.getId()+")");
				}
			}
		}
		/**** full text query ****/
		String searchQuery=null;
		String query = null;

		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(p.revised_content) "+
					"against('"+param+"' in natural language mode)"+
					")||revised_content LIKE '%"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer = new StringBuffer();
			for(String i : parameters){
				buffer.append("+"+i+" ");
			}
			
			searchQuery =" AND match(p.revised_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(p.revised_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(p.revised_content) "+
					"against('"+param+"' in boolean  mode)";
		}	
		
		query = selectQuery + filter + searchQuery + orderByQuery;
		
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.registerNumber,rs.content,rs.houseType,rs.sessionYear, "+
				" rs.sessionType,rs.committeeName,rs.rosterStartTime,rs.rosterEndTime,rs.slotStartTime,rs.slotEndTime,rs.slotName,rs.reporterName FROM (" + query + ") as rs LIMIT " + start + "," + noOfRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<SearchVO> proceedingSearchVOs = new ArrayList<SearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				SearchVO proceedingSearchVO=new SearchVO();
				//Id
				if(o[0]!=null){
					proceedingSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				//Registry Number
				if(o[1]!=null){
					proceedingSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				//Content
				if(o[2]!=null){
					proceedingSearchVO.setNoticeContent(higlightText(o[2].toString(),param));
				}
				//HouseType
				if(o[3]!=null && !o[3].toString().isEmpty()){
					proceedingSearchVO.setSubject(higlightText(o[3].toString(),param));
				}
				//Session Year	
				if(o[4]!=null){
					proceedingSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[4].toString())));
				}
				//Session Type
				if(o[5]!=null){
					proceedingSearchVO.setSessionType(o[5].toString());
				}
				//Committee Name
				if(o[6]!=null){
					proceedingSearchVO.setMinistry(o[6].toString());
				}
				//Roster Start Time
				if(o[7]!=null){
					proceedingSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[7].toString(), ApplicationConstants.DB_DATETIME__24HOURS_FORMAT), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
				}
				//Roster End Time
				if(o[8]!=null){
					proceedingSearchVO.setSubmissionDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[8].toString(), ApplicationConstants.DB_DATETIME__24HOURS_FORMAT), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
				}
				//Slot Start Time
				if(o[9]!=null){
					proceedingSearchVO.setSubDepartment(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[9].toString(), ApplicationConstants.DB_DATETIME__24HOURS_FORMAT), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
				}
				//Slot End Time
				if(o[10]!=null){
					proceedingSearchVO.setStatusType(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[10].toString(), ApplicationConstants.DB_DATETIME__24HOURS_FORMAT), ApplicationConstants.SERVER_DATETIMEFORMAT, locale));
				}
				//Slot Name
				if(o[11]!=null){
					proceedingSearchVO.setFormattedPrimaryMember(o[11].toString());
				}
				//Reporter
				if(o[12]!=null){
					proceedingSearchVO.setActor(o[12].toString());
				}
				proceedingSearchVOs.add(proceedingSearchVO);
			}
		}
		return proceedingSearchVOs;
	}
	
	
	private String addSearchFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("houseType")!=null){
			String houseType=requestMap.get("houseType")[0];
			if((!houseType.isEmpty())&&(!houseType.equals("-"))){
				buffer.append(" AND ht.id='"+houseType+"'");
			}
		}
		if(requestMap.get("sessionYear")!=null){
			String sessionYear=requestMap.get("sessionYear")[0];
			if((!sessionYear.isEmpty())&&(!sessionYear.equals("-"))){
				buffer.append(" AND se.session_year="+sessionYear);
			}
		}
		if(requestMap.get("sessionType")!=null){
			String sessionType=requestMap.get("sessionType")[0];
			if((!sessionType.isEmpty())&&(!sessionType.equals("-"))){
				buffer.append(" AND sety.id="+sessionType);
			}
		}
		if(requestMap.get("committeeName")!=null){
			String committeeName=requestMap.get("committeeName")[0];
			if((!committeeName.isEmpty())&&(!committeeName.equals("-"))){
				buffer.append(" AND cn.id="+committeeName);
			}
		}
		if(requestMap.get("committeeMeeting")!=null){
			String committeeMeeting=requestMap.get("committeeMeeting")[0];
			if((!committeeMeeting.isEmpty())&&(!committeeMeeting.equals("-"))){
				buffer.append(" AND cm.id="+committeeMeeting);
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
