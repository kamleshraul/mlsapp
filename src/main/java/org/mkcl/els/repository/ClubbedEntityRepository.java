package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.springframework.stereotype.Repository;

@Repository
public class ClubbedEntityRepository extends BaseRepository<ClubbedEntity, Serializable>{

	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchClubbing(final String param, final Question question,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		/**** Select Clause ****/
		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
		"  q.subject as subject,q.revised_subject as revisedSubject,"+
		"  q.question_text as questionText,q.revised_question_text as revisedQuestionText,"+
		"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
		"  sety.session_type as sessionType ,g.number as groupnumber,"+
		"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype"+
		"  FROM questions as q "+
		"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
		"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
		"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
		"  LEFT JOIN status as st ON(q.recommendationstatus_id=st.id) "+
		"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
		"  LEFT JOIN members as m ON(q.member_id=m.id) "+
		"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
		"  LEFT JOIN groups as g ON(q.group_id=g.id) "+
		"  LEFT JOIN question_dates as qd ON(q.answering_date=qd.id) "+
		"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
		"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
		"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
		"  WHERE q.id<>"+question.getId()+" AND q.parent is NULL ";

		DeviceType deviceType=question.getType();
		StringBuffer deviceTypeQuery=new StringBuffer();
		String orderByQuery="";
		HouseType housetype=question.getHouseType();
		if(deviceType!=null){
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
				/**** Starred Questions :starred questions:recommendation status >=to_be_put_up,<=yaadi_laid,same session
				 **** unstarred questions:recommendation status >=assistant_processed,<=yaadi_laid,same house type
				 ****/
				deviceTypeQuery.append(" AND (");
				deviceTypeQuery.append(" (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP+"')");
				deviceTypeQuery.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.STARRED_QUESTION +"')");
				deviceTypeQuery.append(" OR ");
				deviceTypeQuery.append(" (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND q.housetype_id="+housetype.getId() +" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION +"')");
				deviceTypeQuery.append(")");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
				/**** unstarred questions:recommendation status >=assistant_processed,<=yaadi_laid,same house type ****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND q.housetype_id="+housetype.getId() +" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
				/**** short notice questions:recommendation status >=assistant_processed,<=yaadi_laid,same session ****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.SHORT_NOTICE_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				/**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION +"'");
				deviceTypeQuery.append(" AND m.id = " + question.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				/**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE +"'");
				deviceTypeQuery.append(" AND m.id = " + question.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
				" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
		}

		String filter=addFilter(requestMap);

		/**** fulltext query ****/
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
			"against('"+param+"' in natural language mode)"+
			")||q.subject LIKE '"+param+"%'||q.question_text LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%')";
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
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
		" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				QuestionSearchVO questionSearchVO=new QuestionSearchVO();
				if(o[0]!=null){
					questionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					questionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						questionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							questionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						questionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						questionSearchVO.setQuestionText(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						questionSearchVO.setQuestionText(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					questionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					questionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					questionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					questionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					questionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					questionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					questionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					questionSearchVO.setDepartment(o[12].toString());
				}
				if(o[13]!=null){
					questionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					questionSearchVO.setStatusType(o[14].toString());
				}
				addClasification(questionSearchVO,question);
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}


	private void addClasification(QuestionSearchVO questionSearchVO,Question question) {
		if(questionSearchVO.getStatusType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			questionSearchVO.setClassification("Referencing");
		}else if(!questionSearchVO.getGroup().equals(String.valueOf(question.getGroup().getNumber()))){
			/**** Candidate For Group Change ****/
			questionSearchVO.setClassification("Group Change");
		}else if(questionSearchVO.getGroup().equals(String.valueOf(question.getGroup().getNumber()))){
			if(question.getMinistry()!=null){
				if(!question.getMinistry().getName().equals(questionSearchVO.getMinistry())){
					/**** Candidate For Ministry Change ****/
					questionSearchVO.setClassification("Ministry Change");
				}else{
//					if(question.getDepartment()!=null){
//						if(!question.getDepartment().getName().equals(questionSearchVO.getDepartment())){
//							/**** Candidate For Department Change ****/
//							questionSearchVO.setClassification("Department Change");
//						}else{
							if(question.getSubDepartment()!=null){
								if(!question.getSubDepartment().getName().equals(questionSearchVO.getSubDepartment())){
									/**** Candidate For Sub Department Change ****/
									questionSearchVO.setClassification("Sub Department Change");	
								}else if(question.getSubDepartment().getName().isEmpty()&&questionSearchVO.getSubDepartment().isEmpty()){
									/**** Candidate For Clubbing ****/
									questionSearchVO.setClassification("Clubbing");
								}else if(question.getSubDepartment().getName().equals(questionSearchVO.getSubDepartment())){
									/**** Candidate For Clubbing ****/
									questionSearchVO.setClassification("Clubbing");
								}
							}else if(question.getSubDepartment()==null&&questionSearchVO.getSubDepartment()==null){
								/**** Candidate For Clubbing ****/
								questionSearchVO.setClassification("Clubbing");
							}
//						}
//					}else if(question.getDepartment()==null&&questionSearchVO.getDepartment()!=null){
//						questionSearchVO.setClassification("Department Change");
//					}
				}
			}
		}
	}
	
	private void addClasification(BillSearchVO billSearchVO,Bill bill) {
		if(billSearchVO.getStatusType().equals(ApplicationConstants.BILL_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			billSearchVO.setClassification("Referencing");
		} 
		else {
			if(bill.getMinistry()!=null){
				if(!bill.getMinistry().getName().equals(billSearchVO.getMinistry())){
					/**** Candidate For Ministry Change ****/
					billSearchVO.setClassification("Ministry Change");
				}else{
//						if(bill.getDepartment()!=null){
//							if(!bill.getDepartment().getName().equals(billSearchVO.getDepartment())){
//								/**** Candidate For Department Change ****/
//								billSearchVO.setClassification("Department Change");
//							}else{
							if(bill.getSubDepartment()!=null){
								if(!bill.getSubDepartment().getName().equals(billSearchVO.getSubDepartment())){
									/**** Candidate For Sub Department Change ****/
									billSearchVO.setClassification("Sub Department Change");	
								}else if(bill.getSubDepartment().getName().isEmpty()&&billSearchVO.getSubDepartment().isEmpty()){
									/**** Candidate For Clubbing ****/
									billSearchVO.setClassification("Clubbing");
								}else if(bill.getSubDepartment().getName().equals(billSearchVO.getSubDepartment())){
									/**** Candidate For Clubbing ****/
									billSearchVO.setClassification("Clubbing");
								}
							}else if(bill.getSubDepartment()==null&&billSearchVO.getSubDepartment()==null){
								/**** Candidate For Clubbing ****/
								billSearchVO.setClassification("Clubbing");
							}
//							}
//						}else if(question.getDepartment()==null&&questionSearchVO.getDepartment()!=null){
//							questionSearchVO.setClassification("Department Change");
//						}
				}
			}
		}				
	}


	private String addFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
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
		if(requestMap.get("group")!=null){
			String group=requestMap.get("group")[0];
			if((!group.isEmpty())&&(!group.equals("-"))){
				buffer.append(" AND g.id="+group);
			}
		}
		if(requestMap.get("answeringDate")!=null){
			String answeringDate=requestMap.get("answeringDate")[0];
			if((!answeringDate.isEmpty())&&(!answeringDate.equals("-"))){
				buffer.append(" AND qd.id="+answeringDate);
			}
		}	
		if(requestMap.get("ministry")!=null){
			String ministry=requestMap.get("ministry")[0];
			if((!ministry.isEmpty())&&(!ministry.equals("-"))){
				buffer.append(" AND mi.id="+ministry);
			}
		}
		if(requestMap.get("department")!=null){
			String department=requestMap.get("department")[0];
			if((!department.isEmpty())&&(!department.equals("-"))){
				buffer.append(" AND d.id="+department);
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterForBill(final Bill bill, Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.priority=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED+"')");					
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_PROCESSED_INTRODUCED+"')");
				} 
			}
		}
		String language = "";
		if(requestMap.get("language")!=null){			
			if((!requestMap.get("language")[0].isEmpty())&&(!requestMap.get("language")[0].equals("-"))){
				language=requestMap.get("language")[0];				
			} else {
				int firstChar=requestMap.get("param")[0].charAt(0); //param already checked for null & empty
				if(firstChar>=2308 && firstChar <= 2418){
					language="marathi";
				}else if((firstChar>=65 && firstChar <= 90) || (firstChar>=97 && firstChar <= 122)
						||(firstChar>=48 && firstChar <= 57)){
					language="english";
				} else {
					//default language for bill
					language=bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
				}
			}
		} else {
			int firstChar=requestMap.get("param")[0].charAt(0); //param already checked for null & empty
			if(firstChar>=2308 && firstChar <= 2418){
				language="marathi";
			}else if((firstChar>=65 && firstChar <= 90) || (firstChar>=97 && firstChar <= 122)
					||(firstChar>=48 && firstChar <= 57)){
				language="english";
			} else {
				//default language for bill
				language=bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
			}
		}
		buffer.append(" AND lang.type = '" + language + "'");
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

	public String club(final Long questionBeingProcessed,
			final Long questionBeingClubbed,final String locale) {
		String clubbingStatus=null;
		try{
			/**** Question which is being processed ****/
			Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
			/**** Question that showed in clubbing search result and whose clubbing link was clicked ****/
			Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
			if(beingProcessedQuestion!=null&&beingClubbedQuestion!=null){
				/**** if any of the two question has its internal status as clubbed
				 *  then clubbing process will not continue****/
				String alreadyClubbedStatus=alreadyClubbed(beingProcessedQuestion,beingClubbedQuestion,locale);
				if(alreadyClubbedStatus.equals("NO")){
					/**** Noone of the question is already clubbed ****/
					clubbingStatus=clubbingRules(beingProcessedQuestion,beingClubbedQuestion,locale);
				}else{
					/**** Atleast one of the question is already clubbed.so further clubbing stops ****/
					clubbingStatus=alreadyClubbedStatus;
				}
			}else{
				/**** Atleast one of the question could not be found ****/
				if(beingClubbedQuestion==null){
					clubbingStatus="BEINGSEARCHED_DOESNOT_EXIST";
				}else if(beingProcessedQuestion==null){
					clubbingStatus="BEINGPROCESSED_DOESNOT_EXIST";
				}			
			}
		}catch(Exception ex){
			/**** An exception has occurred ****/
			logger.error("CLUBBING_FAILED",ex);
			clubbingStatus="CLUBBING_FAILED";
			return clubbingStatus;
		}
		return clubbingStatus;
	}	
	
	public String clubBill(final Long billBeingProcessed,
			final Long billBeingClubbed,final String locale) {
		String clubbingStatus=null;
		try{
			/**** Bill which is being processed ****/
			Bill beingProcessedBill=Bill.findById(Bill.class,billBeingProcessed);
			/**** Bill that showed in clubbing search result and whose clubbing link was clicked ****/
			Bill beingClubbedBill=Bill.findById(Bill.class,billBeingClubbed);
			if(beingProcessedBill!=null&&beingClubbedBill!=null){
				/**** if any of the two bill has its internal status as clubbed
				 *  then clubbing process will not continue****/
				String alreadyClubbedStatus=alreadyClubbed(beingProcessedBill,beingClubbedBill,locale);
				if(alreadyClubbedStatus.equals("NO")){
					/**** Noone of the bill is already clubbed ****/
					clubbingStatus=clubbingRules(beingProcessedBill,beingClubbedBill,locale);
				}else{
					/**** Atleast one of the bill is already clubbed.so further clubbing stops ****/
					clubbingStatus=alreadyClubbedStatus;
				}
			}else{
				/**** Atleast one of the bill could not be found ****/
				if(beingClubbedBill==null){
					clubbingStatus="BEINGSEARCHED_DOESNOT_EXIST";
				}else if(beingProcessedBill==null){
					clubbingStatus="BEINGPROCESSED_DOESNOT_EXIST";
				}			
			}
		}catch(Exception ex){
			/**** An exception has occurred ****/
			logger.error("CLUBBING_FAILED",ex);
			clubbingStatus="CLUBBING_FAILED";
			return clubbingStatus;
		}
		return clubbingStatus;
	}

	private String alreadyClubbed(final Question beingProcessedQuestion,
			final Question beingClubbedQuestion,final String locale) {
		/**** If either of the two question has an entry in clubbed entity it means the question is already clubbed ****/
		ClubbedEntity clubbedEntity1=ClubbedEntity.findByFieldName(ClubbedEntity.class,"question",
				beingClubbedQuestion, locale);
		if(clubbedEntity1!=null){
			/**** Clubbed question has an entry in clubbed entities ****/
			return "BEINGSEARCHED_QUESTION_ALREADY_CLUBBED";
		}else{
			return "NO";
		}
	}
	
	private String alreadyClubbed(final Bill beingProcessedBill,
			final Bill beingClubbedBill,final String locale) {
		/**** If either of the two bill has an entry in clubbed entity it means the bill is already clubbed ****/
		ClubbedEntity clubbedEntity1=ClubbedEntity.findByFieldName(ClubbedEntity.class,"bill",
				beingClubbedBill, locale);
		if(clubbedEntity1!=null){
			/**** Clubbed bill has an entry in clubbed entities ****/
			return "BEINGSEARCHED_BILL_ALREADY_CLUBBED";
		}else{
			return "NO";
		}
	}

	private String clubbingRules(Question beingProcessedQuestion,
			Question beingClubbedQuestion, String locale) {
		String beingProcessedQuestionType=beingProcessedQuestion.getType().getType();
		String beingClubbedQuestionType=beingClubbedQuestion.getType().getType();
		Status beingProcessedQuestionStatus=beingProcessedQuestion.getInternalStatus();
		Status beingClubbedQuestionStatus=beingClubbedQuestion.getInternalStatus();

		/**** Clubbing of starred with starred,unstarred with unstarred,short notice with short notice 
		 * and half hour discussion with half hour discussion 
		 * and half hour standalone with half hour stand alone****/
		if(beingProcessedQuestionType.equals(beingClubbedQuestionType)&&
				beingProcessedQuestion.getMinistry().getName().equals(beingClubbedQuestion.getMinistry().getName())
				/*&&beingProcessedQuestion.getDepartment().getName().equals(beingClubbedQuestion.getDepartment().getName())*/){
			if(beingProcessedQuestion.getSubDepartment()!=null&&beingClubbedQuestion.getSubDepartment()!=null){
				if(beingProcessedQuestion.getSubDepartment().getName().equals(beingClubbedQuestion.getSubDepartment().getName())){
					/**** Clubbing will take place only if both question belong to the same group,ministry
					 * ,department and sub department ****/
					/**** processed number < clubbed number ****/
					if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
						return beingProcessedIsPrimary(beingProcessedQuestion,beingClubbedQuestion
								,beingProcessedQuestionStatus,beingClubbedQuestionStatus
								,beingProcessedQuestionType,beingClubbedQuestionType,locale);
					}
					/**** processed number > clubbed number(discussed in length) ****/
					else{
						return beingClubbedIsPrimary(beingProcessedQuestion,beingClubbedQuestion
								,beingProcessedQuestionStatus,beingClubbedQuestionStatus
								,beingProcessedQuestionType,beingClubbedQuestionType,locale);
					}	
				}else{
					return "QUESTIONS_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
				}
			}else if(beingProcessedQuestion.getSubDepartment()==null&&beingClubbedQuestion.getSubDepartment()==null){
				/**** Clubbing will take place only if both question belong to the same group,ministry
				 * ,department and sub department ****/
				/**** processed number < clubbed number ****/
				if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
					return beingProcessedIsPrimary(beingProcessedQuestion,beingClubbedQuestion
							,beingProcessedQuestionStatus,beingClubbedQuestionStatus
							,beingProcessedQuestionType,beingClubbedQuestionType,locale);
				}
				/**** processed number > clubbed number(discussed in length) ****/
				else{
					return beingClubbedIsPrimary(beingProcessedQuestion,beingClubbedQuestion
							,beingProcessedQuestionStatus,beingClubbedQuestionStatus
							,beingProcessedQuestionType,beingClubbedQuestionType,locale);
				}	
			}else{
				return "QUESTIONS_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
			}

		}
		return "CLUBBING_FAILED";
	}
	
	private String clubbingRules(Bill beingProcessedBill,
			Bill beingClubbedBill, String locale) {
		String beingProcessedBillType=beingProcessedBill.getType().getType();
		String beingClubbedBillType=beingClubbedBill.getType().getType();
		Status beingProcessedBillStatus=beingProcessedBill.getInternalStatus();
		Status beingClubbedBillStatus=beingClubbedBill.getInternalStatus();

		/**** Clubbing of starred with starred,unstarred with unstarred,short notice with short notice 
		 * and half hour discussion with half hour discussion ****/
		if(beingProcessedBillType.equals(beingClubbedBillType)&&
				beingProcessedBill.getMinistry().getName().equals(beingClubbedBill.getMinistry().getName())
				/*&&beingProcessedBill.getDepartment().getName().equals(beingClubbedBill.getDepartment().getName())*/){
			if(beingProcessedBill.getSubDepartment()!=null&&beingClubbedBill.getSubDepartment()!=null){
				if(beingProcessedBill.getSubDepartment().getName().equals(beingClubbedBill.getSubDepartment().getName())){
					/**** Clubbing will take place only if both bill belong to the same group,ministry
					 * ,department and sub department ****/
					/**** processed submission date is before clubbed submission date ****/
					if(beingProcessedBill.getSubmissionDate().before(beingClubbedBill.getSubmissionDate())){
						return beingProcessedIsPrimary(beingProcessedBill,beingClubbedBill
								,beingProcessedBillStatus,beingClubbedBillStatus
								,beingProcessedBillType,beingClubbedBillType,locale);
					}
					/**** processed submission date is after clubbed submission date(discussed in length) ****/
					else{
						return beingClubbedIsPrimary(beingProcessedBill,beingClubbedBill
								,beingProcessedBillStatus,beingClubbedBillStatus
								,beingProcessedBillType,beingClubbedBillType,locale);
					}	
				}else{
					return "BILLS_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
				}
			}else if(beingProcessedBill.getSubDepartment()==null&&beingClubbedBill.getSubDepartment()==null){
				/**** Clubbing will take place only if both bill belong to the same group,ministry
				 * ,department and sub department ****/
				/**** processed submission date is before clubbed submission date ****/
				if(beingProcessedBill.getSubmissionDate().before(beingClubbedBill.getSubmissionDate())){
					return beingProcessedIsPrimary(beingProcessedBill,beingClubbedBill
							,beingProcessedBillStatus,beingClubbedBillStatus
							,beingProcessedBillType,beingClubbedBillType,locale);
				}
				/**** processed submission date after clubbed submission date(discussed in length) ****/
				else{
					return beingClubbedIsPrimary(beingProcessedBill,beingClubbedBill
							,beingProcessedBillStatus,beingClubbedBillStatus
							,beingProcessedBillType,beingClubbedBillType,locale);
				}	
			}else{
				return "BILLS_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
			}

		}
		return "CLUBBING_FAILED";
	}

	private String beingClubbedIsPrimary(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			Status beingProcessedQuestionStatus,Status beingClubbedQuestionStatus,
			String beingProcessedQuestionType,String beingClubbedQuestionType
			,String locale) {
		String beingProcessedQuestionStatusType=beingProcessedQuestionStatus.getType();
		String beingClubbedQuestionStatusType=beingClubbedQuestionStatus.getType();
		String beingProcessedRecommendationStatus=beingProcessedQuestion.getRecommendationStatus().getType();
		/**** Setting parent and child ****/
		/**** Parent Rule:if primary question(beingClubbedQuestion) has a parent then its parent 
		 * will become the parent of the whole bunch.If not then beingClubbedQuestion will become 
		 * the parent of beingProcessedQuestion.****/
		Question beingClubbedParent=beingClubbedQuestion.getParent();
		Question parent=null;
		if(beingClubbedParent!=null){
			parent=beingClubbedParent;
		}else{
			parent=beingClubbedQuestion;
		}	
		Question child=beingProcessedQuestion;		
		Status unProcessedStatus=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status approvalStatus=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);		
		if(	(beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP) 
				&& beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))||
				(beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))){
			Status clubbed=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, locale);
			actualClubbing(parent, child,clubbed,clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}else if((beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))
				&&beingClubbedQuestionStatus.getPriority()>=unProcessedStatus.getPriority()
				&&beingClubbedQuestionStatus.getPriority()<approvalStatus.getPriority()){
			Status clubbedWithPending=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(parent, child,clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}else if((beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))
				&&beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
			Status nameClubbing=Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(parent, child,nameClubbing,nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}else if((beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))
				&&beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION)){
			Status putUpForRejection=Status.findByType(ApplicationConstants.QUESTION_PUTUP_REJECTION, locale);
			actualClubbing(parent, child,putUpForRejection,putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}else if((beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))
				&&beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CONVERT_TO_UNSTARRED)){
			Status convertedToUnstarred=Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED, locale);
			actualClubbing(parent, child,convertedToUnstarred,convertedToUnstarred, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED";
		}else if((beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))
				&&beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_FINAL_CONVERT_TO_UNSTARRED_AND_ADMIT)){
			Status nameClubbing=Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			Status convertedToUnstarredAndAdmit=Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(parent, child,nameClubbing,convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}else if((beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))
				&&(beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
						||beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
						||beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
						||beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_RECOMMEND_CLARIFICATION_FROM_GOVT))){
			Status putOnHold=Status.findByType(ApplicationConstants.QUESTION_PUTUP_ONHOLD, locale);
			actualClubbing(parent, child,putOnHold,putOnHold, locale);
			return "PROCESSED_TO_BE_PUT_ON_HOLD";
		}else if(beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)
				&&beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)){
			Status putOnHold=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			actualClubbing(parent, child,putOnHold,putOnHold, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}
	
	private String beingClubbedIsPrimary(Bill beingProcessedBill,
			Bill beingClubbedBill,
			Status beingProcessedBillStatus,Status beingClubbedBillStatus,
			String beingProcessedBillType,String beingClubbedBillType
			,String locale) {
		String beingProcessedBillStatusType=beingProcessedBillStatus.getType();
		String beingClubbedBillStatusType=beingClubbedBillStatus.getType();
		String beingProcessedRecommendationStatus=beingProcessedBill.getRecommendationStatus().getType();
		/**** Setting parent and child ****/
		/**** Parent Rule:if primary bill(beingClubbedBill) has a parent then its parent 
		 * will become the parent of the whole bunch.If not then beingClubbedBill will become 
		 * the parent of beingProcessedBill.****/
		Bill beingClubbedParent=beingClubbedBill.getParent();
		Bill parent=null;
		if(beingClubbedParent!=null){
			parent=beingClubbedParent;
		}else{
			parent=beingClubbedBill;
		}	
		Bill child=beingProcessedBill;		
		Status unProcessedStatus=Status.findByType(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED, locale);
		Status approvalStatus=Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, locale);	
		if(	(beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_TO_BE_PUTUP) 
				&& beingClubbedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_TO_BE_PUTUP))||
				(beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED))){
			Status clubbed=Status.findByType(ApplicationConstants.BILL_SYSTEM_CLUBBED, locale);
			actualClubbing(parent, child,clubbed,clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}else if((beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.BILL_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.BILL_RECOMMEND_DISCUSS))
				&&beingClubbedBillStatus.getPriority()>=unProcessedStatus.getPriority()
				&&beingClubbedBillStatus.getPriority()<approvalStatus.getPriority()){
			Status clubbedWithPending=Status.findByType(ApplicationConstants.BILL_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(parent, child,clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}else if((beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)
				||beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_TO_BE_PUTUP)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.BILL_RECOMMEND_SENDBACK)
				||beingProcessedRecommendationStatus.equals(ApplicationConstants.BILL_RECOMMEND_DISCUSS))
				&&(
				beingClubbedBillStatusType.equals(ApplicationConstants.BILL_FINAL_ADMISSION)
				||beingClubbedBillStatusType.equals(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED))){
			Status nameClubbing=Status.findByType(ApplicationConstants.BILL_PUTUP_NAMECLUBBING, locale);
			actualClubbing(parent, child,nameClubbing,nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}else if(beingProcessedBillStatusType.equals(ApplicationConstants.BILL_FINAL_ADMISSION)
				&&(
				beingClubbedBillStatusType.equals(ApplicationConstants.BILL_FINAL_ADMISSION)
				||beingClubbedBillStatusType.equals(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED))){
			Status putOnHold=Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, locale);
			actualClubbing(parent, child,putOnHold,putOnHold, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}

	private String beingProcessedIsPrimary(Question beingProcessedQuestion,
			Question beingClubbedQuestion,
			Status beingProcessedQuestionStatus,Status beingClubbedQuestionStatus,
			String beingProcessedQuestionType,String beingClubbedQuestionType
			,String locale) {
		String beingProcessedQuestionStatusType=beingClubbedQuestionStatus.getType();
		String beingClubbedQuestionStatusType=beingClubbedQuestionStatus.getType();
		/***Here we will first check if beingClubbed question is itself a clubbed question ****/
		Question beingProcessedParent=beingProcessedQuestion.getParent();
		Question parent=null;
		if(beingProcessedParent!=null){
			parent=beingProcessedParent;
		}else{
			parent=beingProcessedQuestion;
		}	
		Question child=beingClubbedQuestion;
		if(	(beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP) 
				&& beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))||
				(beingProcessedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedQuestionStatusType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))){
			Status clubbed=Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, locale);
			actualClubbing(parent, child,clubbed,clubbed, locale);
			return "SEARCHED_CLUBBED_TO_PROCESSED";
		}
		return "CLUBBING_FAILED";
	}
	
	private String beingProcessedIsPrimary(Bill beingProcessedBill,
			Bill beingClubbedBill,
			Status beingProcessedBillStatus,Status beingClubbedBillStatus,
			String beingProcessedBillType,String beingClubbedBillType
			,String locale) {
		String beingProcessedBillStatusType=beingProcessedBillStatus.getType();
		String beingClubbedBillStatusType=beingClubbedBillStatus.getType();
		/***Here we will first check if beingClubbed bill is itself a clubbed bill ****/
		Bill beingProcessedParent=beingProcessedBill.getParent();
		Bill parent=null;
		if(beingProcessedParent!=null){
			parent=beingProcessedParent;
		}else{
			parent=beingProcessedBill;
		}	
		Bill child=beingClubbedBill;
		if(beingProcessedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedBillStatusType.equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)){
			Status clubbed=Status.findByType(ApplicationConstants.BILL_SYSTEM_CLUBBED, locale);
			actualClubbing(parent, child,clubbed,clubbed, locale);
			return "SEARCHED_CLUBBED_TO_PROCESSED";
		}
		return "CLUBBING_FAILED";
	}


	private void actualClubbing(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale){
		/**** Here we will obtain all clubbed entities of parent ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null){
			if(!parent.getClubbedEntities().isEmpty()){
				for(ClubbedEntity i:parent.getClubbedEntities()){
					parentClubbedEntities.add(i);
				}
			}
		}
		/****  Here we will obtain all clubbed entities of child ***/
		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null){
			if(!child.getClubbedEntities().isEmpty()){
				for(ClubbedEntity i:child.getClubbedEntities()){
					childClubbedEntities.add(i);
				}
			}
		}
		/**** set the child's parent,its internal and recommendation status to clubbed 
		 * and its clubbing to null****/
		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		if(child.getFile()!=null){
			child.setFile(null);
			child.setFileIndex(null);
			child.setFileSent(false);
		}
		child.simpleMerge();                
		/**** Here we are making new clubbed entity entry for being clubbed question only
		 * as being processed question will at this time have an entry in clubbed entities
		 * if its a clubbed question.
		 */
		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();                
		/**** add this as clubbed entity in parent question clubbed entity(either beingProcessed question)
		 * or parent of being processed question ****/
		parentClubbedEntities.add(clubbedEntity);                
		/*** add clubbed entities of clubbed question in parent clubbed entities ****/
		if(childClubbedEntities!=null){
			if(!childClubbedEntities.isEmpty()){
				for(ClubbedEntity k:childClubbedEntities){
					Question question=k.getQuestion();
					question.setParent(parent);
					question.setInternalStatus(newInternalStatus);
					question.setRecommendationStatus(newRecommendationStatus);
					question.simpleMerge();
					k.setQuestion(question);
					k.merge();
					parentClubbedEntities.add(k);
				}                        
			}
		}
		/**** Setting parent's clubbed entities to parentClubbed entities ****/
		parent.setClubbedEntities(parentClubbedEntities);
		parent.simpleMerge();
		/**** update position of parent's clubbed entities ****/
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
	
	private void actualClubbing(Bill parent,Bill child,
			Status newInternalStatus,Status newRecommendationStatus,String locale){
		/**** Here we will obtain all clubbed entities of parent ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null){
			if(!parent.getClubbedEntities().isEmpty()){
				for(ClubbedEntity i:parent.getClubbedEntities()){
					parentClubbedEntities.add(i);
				}
			}
		}
		/****  Here we will obtain all clubbed entities of child ***/
		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null){
			if(!child.getClubbedEntities().isEmpty()){
				for(ClubbedEntity i:child.getClubbedEntities()){
					childClubbedEntities.add(i);
				}
			}
		}
		/**** set the child's parent,its internal and recommendation status to clubbed 
		 * and its clubbing to null****/
		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		if(child.getFile()!=null){
			child.setFile(null);
			child.setFileIndex(null);
			child.setFileSent(false);
		}
		child.simpleMerge();                
		/**** Here we are making new clubbed entity entry for being clubbed bill only
		 * as being processed bill will at this time have an entry in clubbed entities
		 * if its a clubbed bill.
		 */
		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setBill(child);
		clubbedEntity.persist();                
		/**** add this as clubbed entity in parent bill clubbed entity(either beingProcessed bill)
		 * or parent of being processed bill ****/
		parentClubbedEntities.add(clubbedEntity);                
		/*** add clubbed entities of clubbed bill in parent clubbed entities ****/
		if(childClubbedEntities!=null){
			if(!childClubbedEntities.isEmpty()){
				for(ClubbedEntity k:childClubbedEntities){
					Bill bill=k.getBill();
					bill.setParent(parent);
					bill.setInternalStatus(newInternalStatus);
					bill.setRecommendationStatus(newRecommendationStatus);
					bill.simpleMerge();
					k.setBill(bill);
					k.merge();
					parentClubbedEntities.add(k);
				}                        
			}
		}
		/**** Setting parent's clubbed entities to parentClubbed entities ****/
		parent.setClubbedEntities(parentClubbedEntities);
		parent.simpleMerge();
		/**** update position of parent's clubbed entities ****/
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByBillSubmissionDate(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}	
	
	/**
	 * Unclub.
	 *
	 * @param questionBeingProcessed the question being processed
	 * @param questionBeingClubbed the question being clubbed
	 * @param locale the locale
	 * @return the boolean
	 */
	public String unclub(final Long questionBeingProcessed,
			final Long questionBeingClubbed, final String locale) {
		try {
			Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
			Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
			ClubbedEntity clubbedEntityToRemove=null;

			/**** If processed question's number is less than clubbed question's number
			 * then clubbed question is removed from the clubbing of processed question
			 * ,clubbed question's parent is set to null ,new clubbing of processed 
			 * question is set,their position is updated****/
			if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
				List<ClubbedEntity> oldClubbedQuestions=beingProcessedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(i.getQuestion().getId()!=beingClubbedQuestion.getId()){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedQuestions.add(i);
						}else{
							newClubbedQuestions.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
						clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedQuestions.isEmpty()){
					beingProcessedQuestion.setClubbedEntities(newClubbedQuestions);
				}else{
					beingProcessedQuestion.setClubbedEntities(null);
				}            
				beingProcessedQuestion.simpleMerge();
				clubbedEntityToRemove.remove();
				beingClubbedQuestion.setParent(null);
				String clubbedDeviceType=beingClubbedQuestion.getType().getType();
				Status newstatus=null;
				if(clubbedDeviceType.equals("questions_unstarred")
						||clubbedDeviceType.equals("questions_halfhourdiscussion_from_question")
						||clubbedDeviceType.equals("questions_shortnotice")){
					newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				}else{
					newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
				}
				beingClubbedQuestion.setInternalStatus(newstatus);
				beingClubbedQuestion.setRecommendationStatus(newstatus);
				if(beingClubbedQuestion.getFile()==null){
					/**** Add Question to file ****/
					Reference reference=Question.findCurrentFile(beingClubbedQuestion);
					beingClubbedQuestion.setFile(Integer.parseInt(reference.getId()));
					beingClubbedQuestion.setFileIndex(Integer.parseInt(reference.getName()));
					beingClubbedQuestion.setFileSent(false);
				}
				beingClubbedQuestion.simpleMerge();
			}else if(beingProcessedQuestion.getNumber()>beingClubbedQuestion.getNumber()){
				List<ClubbedEntity> oldClubbedQuestions=beingClubbedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(i.getQuestion().getId()!=beingProcessedQuestion.getId()){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedQuestions.add(i);
						}else{
							newClubbedQuestions.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
						clubbedEntityToRemove=i;
					}
				}
				beingClubbedQuestion.setClubbedEntities(newClubbedQuestions);
				beingClubbedQuestion.simpleMerge();
				clubbedEntityToRemove.remove();
				beingProcessedQuestion.setParent(null);
				String clubbedDeviceType=beingClubbedQuestion.getType().getType();
				Status newstatus=null;
				if(clubbedDeviceType.equals("questions_unstarred")
						||clubbedDeviceType.equals("questions_halfhourdiscussion_from_question")
						||clubbedDeviceType.equals("questions_shortnotice")){
					newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				}else{
					newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
				}
				beingProcessedQuestion.setInternalStatus(newstatus);
				beingProcessedQuestion.setRecommendationStatus(newstatus);
				if(beingClubbedQuestion.getFile()==null){
					/**** Add Question to file ****/
					Reference reference=Question.findCurrentFile(beingClubbedQuestion);
					beingClubbedQuestion.setFile(Integer.parseInt(reference.getId()));
					beingClubbedQuestion.setFileIndex(Integer.parseInt(reference.getName()));
					beingClubbedQuestion.setFileSent(false);
				}
				beingProcessedQuestion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
		}		
		return "SUCCESS";
	}
	
	/**
	 * Unclub.
	 *
	 * @param billBeingProcessed the bill being processed
	 * @param billBeingClubbed the bill being clubbed
	 * @param locale the locale
	 * @return the boolean
	 */
	public String unclubBill(final Long billBeingProcessed,
			final Long billBeingClubbed, final String locale) {
		try {
			Bill beingProcessedBill=Bill.findById(Bill.class,billBeingProcessed);
			Bill beingClubbedBill=Bill.findById(Bill.class,billBeingClubbed);
			ClubbedEntity clubbedEntityToRemove=null;

			/**** If processed bill's submission date is before clubbed bill's submission date
			 * then clubbed bill is removed from the clubbing of processed bill
			 * ,clubbed bill's parent is set to null ,new clubbing of processed 
			 * bill is set,their position is updated****/
			if(beingProcessedBill.getSubmissionDate().before(beingClubbedBill.getSubmissionDate())){
				List<ClubbedEntity> oldClubbedBills=beingProcessedBill.getClubbedEntities();
				List<ClubbedEntity> newClubbedBills=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedBills){
					if(i.getBill().getId()!=beingClubbedBill.getId()){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedBills.add(i);
						}else{
							newClubbedBills.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
						clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedBills.isEmpty()){
					beingProcessedBill.setClubbedEntities(newClubbedBills);
				}else{
					beingProcessedBill.setClubbedEntities(null);
				}            
				beingProcessedBill.simpleMerge();
				clubbedEntityToRemove.remove();
				beingClubbedBill.setParent(null);
				Status newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED, locale);
				beingClubbedBill.setInternalStatus(newstatus);
				beingClubbedBill.setRecommendationStatus(newstatus);
				if(beingClubbedBill.getFile()==null){
					/**** Add Bill to file ****/
					Reference reference=Bill.findCurrentFile(beingClubbedBill);
					beingClubbedBill.setFile(Integer.parseInt(reference.getId()));
					beingClubbedBill.setFileIndex(Integer.parseInt(reference.getName()));
					beingClubbedBill.setFileSent(false);
				}
				beingClubbedBill.simpleMerge();
			}else {//if(beingProcessedBill.getSubmissionDate().after(beingClubbedBill.getSubmissionDate())){
				List<ClubbedEntity> oldClubbedBills=beingClubbedBill.getClubbedEntities();
				List<ClubbedEntity> newClubbedBills=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedBills){
					if(i.getBill().getId()!=beingProcessedBill.getId()){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedBills.add(i);
						}else{
							newClubbedBills.add(i);                		
						}
					}else{
						found=true;
						position=i.getPosition();
						clubbedEntityToRemove=i;
					}
				}
				beingClubbedBill.setClubbedEntities(newClubbedBills);
				beingClubbedBill.simpleMerge();
				clubbedEntityToRemove.remove();
				beingProcessedBill.setParent(null);
				Status newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED, locale);
				beingProcessedBill.setInternalStatus(newstatus);
				beingProcessedBill.setRecommendationStatus(newstatus);
				if(beingClubbedBill.getFile()==null){
					/**** Add Bill to file ****/
					Reference reference=Bill.findCurrentFile(beingClubbedBill);
					beingClubbedBill.setFile(Integer.parseInt(reference.getId()));
					beingClubbedBill.setFileIndex(Integer.parseInt(reference.getName()));
					beingClubbedBill.setFileSent(false);
				}
				beingProcessedBill.simpleMerge();
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
		}		
		return "SUCCESS";
	}
	
	@SuppressWarnings("rawtypes")
	public List<BillSearchVO> fullTextSearchClubbing(final String param, final Bill bill,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		/**** Select Clause ****/
		/**** Condition 1 :must not contain processed bill ****/
		/**** Condition 2 :parent must be null ****/
		/**** Condition 3 :parent must be from same session & of same devicetype, billtype & billkind ****/
		/**** Condition 4 :parent should be atleast assistant processed & should not be introduced ****/
		/**** Condition 5 :parent should not be rejected or in flow of put up for rejection ****/		
		String selectQuery = "SELECT b.id as billId, b.number as billNumber," +						
				" (CASE WHEN (lang.id=titleDraft.language_id) THEN titleDraft.text ELSE NULL END) AS billTitle," +
				" (CASE WHEN (lang.id=revisedTitleDraft.language_id) THEN revisedTitleDraft.text ELSE NULL END) AS billRevisedTitle," +
				" (CASE WHEN (lang.id=contentDraft.language_id) THEN contentDraft.text ELSE NULL END) AS billContent, " +
				" (CASE WHEN (lang.id=revisedContentDraft.language_id) THEN revisedContentDraft.text ELSE NULL END) AS billRevisedContent," +
				" lang.type AS languageType," +			
				" st.name as billStatus, dt.name as billDeviceType," +	
				" mi.name as billMinistry, sd.name as billSubDepartment, st.type as billStatusType, s.sessiontype_id as billSessionType,s.session_year as billSessionYear," +				
				" b.submission_date as billSubmissionDate," +
				" b.admission_date as billAdmissionDate" +				
				" FROM bills as b" +	
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
				" AND s.id="+bill.getSession().getId() +
				" AND b.devicetype_id="+bill.getType().getId().toString() +
				" AND b.billtype_id="+bill.getBillType().getId().toString() + 
				" AND b.billkind_id="+bill.getBillKind().getId().toString() + 				
				" AND (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED+"')" + 
				" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILL_PROCESSED_INTRODUCED+"'))" + 
				" AND (ist.type<>'"+ApplicationConstants.BILL_RECOMMEND_REJECTION + "'" +
				" AND ist.type<>'"+ApplicationConstants.BILL_FINAL_REJECTION+"')";
		
		/**** filter query ****/
		String filterQuery=addFilterForBill(bill, requestMap);

		/**** fulltext query ****/
		String searchQuery=null;
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
		String orderByQuery=" ORDER BY st.priority "+ApplicationConstants.ASC+" ,b.number "+ApplicationConstants.ASC+
				", b.submission_date "+ApplicationConstants.ASC+", b.id "+ApplicationConstants.ASC;
		
		/**** Final Query ****/
		String query=selectQuery+filterQuery+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.billId,rs.billNumber,rs.billTitle,rs.billRevisedTitle,rs.billContent, "+
				" rs.billRevisedContent,rs.languageType,rs.billStatus,rs.billDeviceType," + 
				" rs.billMinistry,rs.billSubDepartment,rs.billStatusType, rs.billSessionType, rs.billSessionYear," + 
				" rs.billSubmissionDate, rs.billAdmissionDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List resultList=this.em().createNativeQuery(finalQuery).getResultList();
		List<BillSearchVO> billSearchVOs=new ArrayList<BillSearchVO>();
		String billId = "";
		BillSearchVO billSearchVO = new BillSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				if(!billId.equals(o[0].toString())) {
					if(!billSearchVOs.isEmpty()) {
						addClasification(billSearchVO,bill);
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
					if(o[15] != null){	
						Date admissionDate = FormaterUtil.formatStringToDate(o[15].toString(), "yyyy-MM-dd HH:mm:ss");
						String admissionDateStr = FormaterUtil.formatDateToString(admissionDate, "dd-MM-yyyy HH:mm:ss", bill.getLocale());
						billSearchVO.setAdmissionDate(admissionDateStr);
					}					
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
		if(!billSearchVOs.isEmpty()) {
			addClasification(billSearchVOs.get(billSearchVOs.size()-1),bill);
		}		
		return billSearchVOs;
	}
}
