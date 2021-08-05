package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.DeviceSearchVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClubbedEntityRepository extends BaseRepository<ClubbedEntity, Serializable>{

	@Autowired
	IProcessService processService;
	
	/**** Free Text Search Begins 
	 * @throws ELSException ****/
	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchClubbing(final String param, final Question question,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) throws ELSException {
		DeviceType deviceType=question.getType();
		HouseType housetype=question.getHouseType();
		StringBuffer deviceTypeQuery=new StringBuffer();
		String orderByQuery="";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.question_text as questionText,q.revised_question_text as revisedQuestionText,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,g.number as groupnumber,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName," +
				"  qd1.answering_date as answeringDate,"+				
				"  q.session_id as sessionId,"+
				"  dt.type as deviceTypeType"+
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
				"  LEFT JOIN question_dates as qd1 ON(q.chart_answering_date=qd1.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.id<>"+question.getId()+" AND q.parent is NULL ";	
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
			}
			else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
				/**** unstarred questions:recommendation status >=assistant_processed,<=yaadi_laid,same house type ****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND q.housetype_id="+housetype.getId() +" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}
			else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
				/**** short notice questions:recommendation status >=assistant_processed,<=yaadi_laid,same session ****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.SHORT_NOTICE_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				/**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION +"'");
				deviceTypeQuery.append(" AND m.id = " + question.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				/**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+question.getSession().getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE +"'");
				deviceTypeQuery.append(" AND m.id = " + question.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
		}

		String filter="";
		if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			filter=addFilterForStarredQuestion(requestMap);
		} else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
			filter=addFilterForUnStarredQuestion(requestMap);
		} else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
			filter=addFilterForShortNoticeQuestion(requestMap);
		} else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			filter=addFilterForHDQ(requestMap);
		}

		/**** full text query ****/
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '%"+param+"%'||q.question_text LIKE '%"+param+"%'||q.revised_subject LIKE '%"+param+"%'||q.revised_question_text LIKE '%"+param+"%' ||q.answer LIKE '%"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
				" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate,rs.sessionId,rs.deviceTypeType FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

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
				if(o[15]!=null){
					questionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					questionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[16].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[17]!=null){
					questionSearchVO.setSessionId(Long.parseLong(o[17].toString()));
				}
				if(o[18]!=null){
					questionSearchVO.setDeviceTypeType(o[18].toString());
				}
				addClasification(questionSearchVO,question);
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchForSearchFacility(final String param,
			final DeviceType deviceType,
			final Session session,
			final Integer start,
			final Integer noofRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY q.number";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.question_text as questionText,q.revised_question_text as revisedQuestionText,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,g.number as groupnumber,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, qd1.answering_date as answeringDate,"+
				"  q.localized_actor_name as actor" +
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
				"  LEFT JOIN question_dates as qd1 ON(q.chart_answering_date=qd1.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.locale='"+locale+"'" +
				"  AND s.id=" + session.getId().toString();	
		/*if(deviceType!=null){
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)){
				*//**** Starred Questions :starred questions:recommendation status >=to_be_put_up,<=yaadi_laid,same session
				 **** unstarred questions:recommendation status >=assistant_processed,<=yaadi_laid,same house type
				 ****//*
				deviceTypeQuery.append(" AND (");
				deviceTypeQuery.append(" (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP+"')");
				deviceTypeQuery.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+ session.getId() +" AND dt.type='"+ApplicationConstants.STARRED_QUESTION +"')");
				deviceTypeQuery.append(" OR ");
				deviceTypeQuery.append(" (st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND q.housetype_id="+ housetype.getId() +" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION +"')");
				deviceTypeQuery.append(")");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}
			else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
				*//**** unstarred questions:recommendation status >=assistant_processed,<=yaadi_laid,same house type ****//*
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND q.housetype_id="+housetype.getId() +" AND dt.type='"+ApplicationConstants.UNSTARRED_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}
			else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)){
				*//**** short notice questions:recommendation status >=assistant_processed,<=yaadi_laid,same session ****//*
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+ session.getId() +" AND dt.type='"+ApplicationConstants.SHORT_NOTICE_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				*//**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****//*
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+session.getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
			else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				*//**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****//*
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+session.getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE +"'");
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
		}*/

		String filter="";
		if(deviceType != null){
			if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				filter=addFilterForStarredQuestion(requestMap);
			} else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				filter=addFilterForUnStarredQuestion(requestMap);
			} else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				filter=addFilterForShortNoticeQuestion(requestMap);
			} else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				filter=addFilterForHDQ(requestMap);
			}
		}

		/**** full text query ****/
		String searchQuery=null;
		String query = null;
		if(requestMap.get("number") != null){
			query = selectQuery+filter+orderByQuery;
		}else{
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
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
				" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate,rs.actor FROM (" + query + ") as rs LIMIT " + start + "," + noofRecords;

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
				if(o[15]!=null){
					questionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					questionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[16].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[17]!=null){
					questionSearchVO.setActor(o[17].toString());
				}
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}

	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final Motion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.details as details,q.revised_details as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM motions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.recommendationstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.id<>"+motion.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED+"')" +
				"  AND s.id=" + motion.getSession().getId();

		
		String filter = addFilterMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '%"+param+"%'||q.details LIKE '%"+param+"%'||q.revised_details LIKE '%"+param+"%'||q.revised_subject LIKE '%"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setTitle(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setTitle(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setTitle(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setStatusType(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					motionSearchVO.setChartAnsweringDate(o[16].toString());
				}
				addClasification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchFiling(final String param, final Motion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id," +
				" CASE" +
				"	WHEN q.post_ballot_number IS NOT NULL THEN q.post_ballot_number" +
				"   ELSE q.number " +
				"  END as number," +
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.details as details,q.revised_details as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM motions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.recommendationstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED+"')";
				

		
		String filter = addFilterMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '%"+param+"%'||q.details LIKE '%"+param+"%'||q.revised_details LIKE '%"+param+"%'||q.revised_subject LIKE '%"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.details,q.revised_subject,q.revised_details) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setTitle(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setTitle(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setTitle(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setStatusType(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					motionSearchVO.setChartAnsweringDate(o[16].toString());
				}
				addClasification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchClubbing(final String param, final StandaloneMotion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		
		DeviceType deviceType = motion.getType();
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery = "";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  CASE " +
				"      WHEN ht.type='lowerhouse' THEN q.question_text " +
				"      ELSE q.reason" +
				"  END AS questionText," +
				"  CASE " +
				"      WHEN ht.type='lowerhouse' THEN q.revised_question_text " +
				"      ELSE q.revised_reason" +
				"  END AS revisedQuestionText,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,g.number as groupnumber,"+
				"  mi.name as ministry,'0' as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM standalone_motions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.recommendationstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN groups as g ON(q.group_id=g.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.id<>"+ motion.getId()+" AND q.parent is NULL ";	
		if(deviceType!=null){
			if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				/**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_PROCESSED_YAADILAID+"')");
				deviceTypeQuery.append(" AND s.id="+ motion.getSession().getId() +" AND dt.type='"+ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE +"'");
				deviceTypeQuery.append(" AND m.id = " + motion.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
		}

		String filter = addFilterStandaloneMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+") && !param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
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
		String query = selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
				" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

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
				if(o[15]!=null){
					questionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					questionSearchVO.setChartAnsweringDate("0");
				}
				addClasification(questionSearchVO, motion);
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchFiling(final String param, final StandaloneMotion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		
		DeviceType deviceType = motion.getType();
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery = "";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  CASE " +
				"      WHEN ht.type='lowerhouse' THEN q.question_text " +
				"      ELSE q.reason" +
				"  END AS questionText," +
				"  CASE " +
				"      WHEN ht.type='lowerhouse' THEN q.revised_question_text " +
				"      ELSE q.revised_reason" +
				"  END AS revisedQuestionText,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,g.number as groupnumber,"+
				"  mi.name as ministry,'0' as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM standalone_motions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.recommendationstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN groups as g ON(q.group_id=g.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.parent is NULL ";	
		if(deviceType!=null){
			if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				/**** Half hour discussion from questions Questions :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_PROCESSED_YAADILAID+"')");
				//deviceTypeQuery.append(" AND s.id="+ motion.getSession().getId() +" AND dt.type='"+ motion.getType().getType() +"'");
				//deviceTypeQuery.append(" AND m.id = " + motion.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			}
		}

		String filter = addFilterStandaloneMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+") && !param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text) "+
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
		String query = selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
				" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

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
				if(o[15]!=null){
					questionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					questionSearchVO.setChartAnsweringDate("0");
				}
				//addClasification(questionSearchVO, motion);
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public List<QuestionSearchVO> fullTextSearchFiling(final String param, final Resolution motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery = "";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.notice_content AS questionText," +
				"  q.revised_notice_content AS revisedQuestionText,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  mi.name as ministry,'0' as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM resolutions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				((motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE))? 
						"  LEFT JOIN status as st ON(q.lowerhouse_recommendationstatus_id=st.id) ":
							"  LEFT JOIN status as st ON(q.upperhouse_recommendationstatus_id=st.id) ") +
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.locale='" + locale+"'";	
		
				/**** Resolution :recommendation status >=assistant_processed,<=yaadi_laid,same session****/
				deviceTypeQuery.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP+"')");
				deviceTypeQuery.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RESOLUTION_PROCESSED_UNDERCONSIDERATION+"')");
				//deviceTypeQuery.append(" AND s.id="+ motion.getSession().getId() +" AND dt.type='"+ motion.getType().getType() +"'");
				//deviceTypeQuery.append(" AND m.id = " + motion.getPrimaryMember().getId());
				orderByQuery=" ORDER BY dt.type "+ApplicationConstants.ASC+" ,s.start_date "+ApplicationConstants.DESC+
						" ,q.number "+ApplicationConstants.ASC+" ,st.priority "+ApplicationConstants.ASC;				
			

		String filter = addFilterResolution(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+") && !param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '"+param+"%'||q.notice_content LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query = selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
				" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

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
				if(o[15]!=null){
					questionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					questionSearchVO.setChartAnsweringDate("0");
				}
				//addClasification(questionSearchVO, motion);
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;
	}
	private void addClasification(QuestionSearchVO questionSearchVO,Question question) throws ELSException {
		boolean isSearchedQuestionUnstarredFromPreviousSessionInSameHouse = false;
		boolean question_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = false;
    	String questionTypesAllowedForClubbingWithPreviousSessionUnstarred = "";
    	CustomParameter cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred = CustomParameter.findByName(CustomParameter.class, "QUESTIONTYPES_ALLOWED_FOR_CLUBBING_WITH_PREVIOUSSESSION_UNSTARRED", "");
    	if(cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred!=null
    			&& cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred.getValue()!=null) {
    		questionTypesAllowedForClubbingWithPreviousSessionUnstarred = cp_questionTypesAllowedForClubbingWithPreviousSessionUnstarred.getValue();
    	} else {
    		questionTypesAllowedForClubbingWithPreviousSessionUnstarred = ApplicationConstants.STARRED_QUESTION + "," + ApplicationConstants.UNSTARRED_QUESTION;
    	}
    	for(String eligibleQuestionType: questionTypesAllowedForClubbingWithPreviousSessionUnstarred.split(",")) {
    		if(question.getType().getType().equals(eligibleQuestionType)) {
    			question_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred = true;  
    			break;
    		}	
    	}
		if(question_isQuestionTypeAllowedForClubbingWithPreviousSessionUnstarred
				&& (questionSearchVO.getDeviceTypeType()!=null && questionSearchVO.getDeviceTypeType().equals(ApplicationConstants.UNSTARRED_QUESTION))) {
			Session searchedQuestionSession = null;
			if(questionSearchVO.getSessionId()!=null) {
				searchedQuestionSession = Session.findById(Session.class, questionSearchVO.getSessionId());
			}
			if(question.getSession()!=null && searchedQuestionSession!=null) {
				if(question.getSession().getHouse().equals(searchedQuestionSession.getHouse())) {
					if(searchedQuestionSession.getStartDate().before(question.getSession().getStartDate())) {
						isSearchedQuestionUnstarredFromPreviousSessionInSameHouse = true;
					}
				}
			}
		}		
		if(questionSearchVO.getStatusType().equals(ApplicationConstants.QUESTION_FINAL_REJECTION)
				|| questionSearchVO.getStatusType().equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_REJECTION)
				|| questionSearchVO.getStatusType().equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_REJECTION)
				|| questionSearchVO.getStatusType().equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			questionSearchVO.setClassification("Referencing");
		}else if(!questionSearchVO.getGroup().equals(String.valueOf(question.getGroup().getNumber()))){			
			if(isSearchedQuestionUnstarredFromPreviousSessionInSameHouse) {
				/**** Candidate For Clubbing ****/
				questionSearchVO.setClassification("Clubbing");
			} else {
				/**** Candidate For Group Change ****/
				questionSearchVO.setClassification("Group Change");
			}			
		}else if(questionSearchVO.getGroup().equals(String.valueOf(question.getGroup().getNumber()))){
			if(question.getMinistry()!=null){
				if(!question.getMinistry().getName().equals(questionSearchVO.getMinistry())){							
					if(isSearchedQuestionUnstarredFromPreviousSessionInSameHouse) {
						/**** Candidate For Clubbing ****/
						questionSearchVO.setClassification("Clubbing");
					} else {
						/**** Candidate For Ministry Change ****/
						questionSearchVO.setClassification("Ministry Change");
					}
				}else{					
					if(question.getSubDepartment()!=null){
						if(!question.getSubDepartment().getName().equals(questionSearchVO.getSubDepartment())){
							if(isSearchedQuestionUnstarredFromPreviousSessionInSameHouse) {
								/**** Candidate For Clubbing ****/
								questionSearchVO.setClassification("Clubbing");
							} else {
								/**** Candidate For Sub Department Change ****/
								questionSearchVO.setClassification("Sub Department Change");
							}
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
				}
			}
		}
	}	
	
	private void addClasification(MotionSearchVO motionSearchVO,Motion motion) {
		if(motionSearchVO.getStatusType().equals(ApplicationConstants.MOTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			motionSearchVO.setClassification("Referencing");
		}else{// if(motion.getMinistry() != null && motion.getDepartment() != null){
			motionSearchVO.setClassification("Clubbing");
//			if(!motion.getMinistry().getName().equals(motionSearchVO.getMinistry())){
//				/**** Candidate For Ministry Change ****/
//				motionSearchVO.setClassification("Ministry Change");
//			}else{					
//				if(motion.getSubDepartment()!=null){
//					if(!motion.getSubDepartment().getName().equals(motionSearchVO.getSubDepartment())){
//						/**** Candidate For Sub Department Change ****/
//						motionSearchVO.setClassification("Sub Department Change");	
//					}else if(motion.getSubDepartment().getName().isEmpty() && motionSearchVO.getSubDepartment().isEmpty()){
//						/**** Candidate For Clubbing ****/
//						motionSearchVO.setClassification("Clubbing");
//					}else if(motion.getSubDepartment().getName().equals(motionSearchVO.getSubDepartment())){
//						/**** Candidate For Clubbing ****/
//						motionSearchVO.setClassification("Clubbing");
//					}
//				}else if(motion.getSubDepartment()==null && motionSearchVO.getSubDepartment()==null){
//					/**** Candidate For Clubbing ****/
//					motionSearchVO.setClassification("Clubbing");
//				}					
//			}
		}
	}
	
	//special mention notice
	private void addClasification(MotionSearchVO motionSearchVO,SpecialMentionNotice notice) {
		if(motionSearchVO.getStatusType().equals(ApplicationConstants.MOTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			motionSearchVO.setClassification("Referencing");
		}else{
			motionSearchVO.setClassification("Clubbing");
		}
	}
	
	private void addClasification(MotionSearchVO motionSearchVO,CutMotion motion) {
		if(motionSearchVO.getStatusType().equals(ApplicationConstants.CUTMOTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			motionSearchVO.setClassification("Referencing");
		}else if(motion.getMinistry()!=null){
			if(!motion.getMinistry().getName().equals(motionSearchVO.getMinistry())){
				/**** Candidate For Ministry Change ****/
				motionSearchVO.setClassification("Ministry Change");
			}else{					
				if(motion.getSubDepartment()!=null){
					if(!motion.getSubDepartment().getName().equals(motionSearchVO.getSubDepartment())){
						/**** Candidate For Sub Department Change ****/
						motionSearchVO.setClassification("Sub Department Change");	
					}else if(motion.getSubDepartment().getName().isEmpty() && motionSearchVO.getSubDepartment().isEmpty()){
						/**** Candidate For Clubbing ****/
						motionSearchVO.setClassification("Clubbing");
					}else if(motion.getSubDepartment().getName().equals(motionSearchVO.getSubDepartment())){
						/**** Candidate For Clubbing ****/
						motionSearchVO.setClassification("Clubbing");
					}
				}else if(motion.getSubDepartment()==null && motionSearchVO.getSubDepartment()==null){
					/**** Candidate For Clubbing ****/
					motionSearchVO.setClassification("Clubbing");
				}					
			}
		}
	}
	
	private void addClasification(MotionSearchVO motionSearchVO,EventMotion motion) {
		if(motionSearchVO.getStatusType().equals(ApplicationConstants.EVENTMOTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			motionSearchVO.setClassification("Referencing");
		}else{
			motionSearchVO.setClassification("Clubbing");
		}
	}
	
	private void addClasification(QuestionSearchVO questionSearchVO,StandaloneMotion motion) {
		if(questionSearchVO.getStatusType().equals(ApplicationConstants.STANDALONE_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			questionSearchVO.setClassification("Referencing");
		}else if(!questionSearchVO.getGroup().equals(String.valueOf(motion.getGroup().getNumber()))){
			/**** Candidate For Group Change ****/
			questionSearchVO.setClassification("Group Change");
		}else if(questionSearchVO.getGroup().equals(String.valueOf(motion.getGroup().getNumber()))){
			if(motion.getMinistry()!=null){
				if(!motion.getMinistry().getName().equals(questionSearchVO.getMinistry())){
					/**** Candidate For Ministry Change ****/
					questionSearchVO.setClassification("Ministry Change");
				}else{					
					if(motion.getSubDepartment()!=null){
						if(!motion.getSubDepartment().getName().equals(questionSearchVO.getSubDepartment())){
							/**** Candidate For Sub Department Change ****/
							questionSearchVO.setClassification("Sub Department Change");	
						}else if(motion.getSubDepartment().getName().isEmpty()&&questionSearchVO.getSubDepartment().isEmpty()){
							/**** Candidate For Clubbing ****/
							questionSearchVO.setClassification("Clubbing");
						}else if(motion.getSubDepartment().getName().equals(questionSearchVO.getSubDepartment())){
							/**** Candidate For Clubbing ****/
							questionSearchVO.setClassification("Clubbing");
						}
					}else if(motion.getSubDepartment()==null&&questionSearchVO.getSubDepartment()==null){
						/**** Candidate For Clubbing ****/
						questionSearchVO.setClassification("Clubbing");
					}					
				}
			}
		}
	}	
	
	private void addClasification(MotionSearchVO motionSearchVO,DiscussionMotion motion) {
		if(motionSearchVO.getStatusType().equals(ApplicationConstants.DISCUSSIONMOTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			motionSearchVO.setClassification("Referencing");
//		}else if(motion.getMinistries()!=null){
//			if(!isMinistyrSubDepartmentContained(motion.getMinistries(), motionSearchVO.getMinistry())){
//				/**** Candidate For Ministry Change ****/
//				motionSearchVO.setClassification("Ministry Change");
//			}else{					
//				if(motion.getSubDepartments()!=null){
//					if(!isMinistyrSubDepartmentContained(motion.getSubDepartments(), motionSearchVO.getSubDepartment())){
//						/**** Candidate For Sub Department Change ****/
//						motionSearchVO.setClassification("Sub Department Change");	
//					}else if(motion.getSubDepartments().isEmpty() && motionSearchVO.getSubDepartment().isEmpty()){
//						/**** Candidate For Clubbing ****/
//						motionSearchVO.setClassification("Clubbing");
//					}else if(isMinistyrSubDepartmentContained(motion.getSubDepartments(), motionSearchVO.getSubDepartment()) ){
//						/**** Candidate For Clubbing ****/
//						motionSearchVO.setClassification("Clubbing");
//					}
//				}else if(motion.getSubDepartments()==null && motionSearchVO.getSubDepartment()==null){
//					/**** Candidate For Clubbing ****/
//					motionSearchVO.setClassification("Clubbing");
//				}					
//			}
//		}
		}else{
			//Uncomment the above code if ministry and subdepartment criteria is required for clubbing
			motionSearchVO.setClassification("Clubbing");
		}
	}
	
	private void addClasification(MotionSearchVO motionSearchVO,AdjournmentMotion motion) {
//		if(motionSearchVO.getStatusType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECTION)){
//			/**** Candidate For Referencing ****/
//			motionSearchVO.setClassification("Referencing");
//			
//		}
//		else 
		if(motion.getMinistry()!=null && motionSearchVO.getMinistry()!=null 
				&& motion.getMinistry().getId()!=null && !motionSearchVO.getMinistry().isEmpty()
				&& !motion.getMinistry().getName().equals(motionSearchVO.getMinistry())){
			/**** Candidate For Ministry Change ****/
			motionSearchVO.setClassification("Ministry Change");
			
		}else if(motion.getSubDepartment()!=null && motionSearchVO.getSubDepartment()!=null 
				&& motion.getSubDepartment().getId()!=null && !motionSearchVO.getSubDepartment().isEmpty()
				&& !motion.getSubDepartment().getName().equals(motionSearchVO.getSubDepartment())){
			/**** Candidate For Sub Department Change ****/
			motionSearchVO.setClassification("Sub Department Change");
			
		}else{
			motionSearchVO.setClassification("Clubbing");
		}
	}
	
	private void addClasification(MotionSearchVO motionSearchVO,BillAmendmentMotion motion) {
		if(motionSearchVO.getStatusType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECTION)){
			/**** Candidate For Referencing ****/
			motionSearchVO.setClassification("Referencing");
		}else {
			/**** Candidate For Clubbing ****/
			motionSearchVO.setClassification("Clubbing");
		}
	}
	
	private void addClassification(MotionSearchVO motionSearchVO,RulesSuspensionMotion motion) {
		motionSearchVO.setClassification("Clubbing");
	}
	
	@SuppressWarnings("unused")
	private boolean isMinistyrSubDepartmentContained(List<? extends BaseDomain> data, String value){
		boolean retVal = false;
		for(BaseDomain d : data){
			if(d instanceof Ministry){
				if(value.contains(((Ministry)d).getName())){
					retVal = true;
					break;
				}
			}else if(d instanceof SubDepartment){
				if(value.contains(((SubDepartment)d).getName())){
					retVal = true;
					break;
				}
			}
			
		}
		return retVal;
	}
	
	private String addFilterStandaloneMotion(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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
		if(requestMap.get("group")!=null){
			String group=requestMap.get("group")[0];
			if((!group.isEmpty())&&(!group.equals("-"))){
				buffer.append(" AND g.id="+group);
			}
		}
		if(requestMap.get("discussionDate")!=null){
			String answeringDate=requestMap.get("discussionDate")[0];
			if((!answeringDate.isEmpty())&&(!answeringDate.equals("-"))){
				buffer.append(" AND q.discussionDate="+FormaterUtil.formatStringToDate(answeringDate, ApplicationConstants.DB_DATEFORMAT, "en_US"));
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	
	private String addFilterResolution(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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
		if(requestMap.get("group")!=null){
			String group=requestMap.get("group")[0];
			if((!group.isEmpty())&&(!group.equals("-"))){
				buffer.append(" AND g.id="+group);
			}
		}
		if(requestMap.get("discussionDate")!=null){
			String answeringDate=requestMap.get("discussionDate")[0];
			if((!answeringDate.isEmpty())&&(!answeringDate.equals("-"))){
				buffer.append(" AND q.discussionDate="+FormaterUtil.formatStringToDate(answeringDate, ApplicationConstants.DB_DATEFORMAT, "en_US"));
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
		/*if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.STANDALONE_PROCESSED_YAADILAID+"')");
				} 
			}
		}	*/		
		return buffer.toString();
	}
	
	private String addFilter(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer("");
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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

	private String addFilterForStarredQuestion(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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
	
	private String addFilterForUnStarredQuestion(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterForShortNoticeQuestion(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterForHDQ(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND q.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND q.member_id=" + member);
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
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterMotion(Map<String, String[]> requestMap) {
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
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.MOTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.QUESTION_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterCutMotion(Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		if(requestMap.get("number") != null){
			String deviceNumber = requestMap.get("number")[0];
			if((!deviceNumber.isEmpty()) && (!deviceNumber.equals("-"))){
				buffer.append(" AND cm.number=" + deviceNumber);
			}
		}
		if(requestMap.get("primaryMember") != null){
			String member = requestMap.get("primaryMember")[0];
			if((!member.isEmpty()) && (!member.equals("-"))){
				buffer.append(" AND cm.member_id=" + member);
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
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.CUTMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.CUTMOTION_FINAL_ADMISSION+"')");
					//buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.CUTMOTION_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}

	private String addFilterEventMotion(Map<String, String[]> requestMap) {
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
		
		if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_SYSTEM_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_SYSTEM_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterDiscussionMotion(Map<String, String[]> requestMap) {
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
		
		if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_SYSTEM_PUTUP+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_SYSTEM_PUTUP+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_FINAL_ADMISSION+"')");
					buffer.append(" AND st.priority<=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_PROCESSED_YAADILAID+"')");
				} 
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterAdjournmentMotion(Map<String, String[]> requestMap) {
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
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION+"')");
				}
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterSpecialMentionNotice(Map<String, String[]> requestMap) {
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
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_FINAL_ADMISSION+"')");
				}
			}
		}			
		return buffer.toString();
	}
	
	private String addFilterForBillAmendmentMotion(final BillAmendmentMotion billAmendmentMotion, Map<String, String[]> requestMap) {
		StringBuffer buffer=new StringBuffer();
		if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.priority=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");					
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION+"')");
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
					//default language for amended bill
					language=billAmendmentMotion.getAmendedBill().getSession().getParameter(billAmendmentMotion.getAmendedBill().getType().getType()+"_defaultTitleLanguage");
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
				//default language for amended bill
				language=billAmendmentMotion.getAmendedBill().getSession().getParameter(billAmendmentMotion.getAmendedBill().getType().getType()+"_defaultTitleLanguage");
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
	/**** Free Text Search Begins ****/

	/**** Question Clubbing Begins ****/
	public String club(final Long questionBeingProcessed,
			final Long questionBeingClubbed,final String locale) {
		String clubbingStatus=null;
		try{
			Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
			Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
			if(beingProcessedQuestion!=null&&beingClubbedQuestion!=null){				
				String alreadyClubbedStatus=alreadyClubbed(beingProcessedQuestion,beingClubbedQuestion,locale);
				if(alreadyClubbedStatus.equals("NO")){
					clubbingStatus=clubbingRules(beingProcessedQuestion,beingClubbedQuestion,locale);
				}else{
					clubbingStatus=alreadyClubbedStatus;
				}
			}else{
				if(beingClubbedQuestion==null){
					clubbingStatus="BEINGSEARCHED_DOESNOT_EXIST";
				}else if(beingProcessedQuestion==null){
					clubbingStatus="BEINGPROCESSED_DOESNOT_EXIST";
				}			
			}
		}catch(Exception ex){
			logger.error("CLUBBING_FAILED",ex);
			clubbingStatus="CLUBBING_FAILED";
			return clubbingStatus;
		}
		return clubbingStatus;
	}

	@SuppressWarnings("unchecked")
	private String alreadyClubbed(final Question beingProcessedQuestion,
			final Question beingClubbedQuestion,final String locale) {
		/**** If any of the two questions have entries in clubbed entities then it means they are already clubbed
		 * and hence clubbing cannot proceed ****/
		String strQuery="SELECT ce FROM Question q JOIN q.clubbedEntities ce " +
				"WHERE ce.question.id=:clubbedQuestionId " +
				" OR ce.question.id=:processedQuestionId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("clubbedQuestionId", beingClubbedQuestion.getId());
		query.setParameter("processedQuestionId",beingProcessedQuestion.getId());
		List<ClubbedEntity> clubEntities=query.getResultList();
		if(clubEntities!=null && clubEntities.size()>0){
			return "BEINGSEARCHED_QUESTION_ALREADY_CLUBBED";
		}else{
			return "NO";
		}
	}

	private String clubbingRules(Question beingProcessedQuestion,
			Question beingClubbedQuestion, String locale) {			
		if(	beingProcessedQuestion.getType()!=null
				&&beingClubbedQuestion.getType()!=null
				&&(beingProcessedQuestion.getType().getType().equals(beingClubbedQuestion.getType().getType())
				||(beingProcessedQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
				&&beingClubbedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)))
				&&beingProcessedQuestion.getMinistry()!=null
				&&beingClubbedQuestion.getMinistry()!=null
				&& beingProcessedQuestion.getMinistry().getName().equals(beingClubbedQuestion.getMinistry().getName())
				&&beingProcessedQuestion.getSubDepartment()!=null
				&&beingClubbedQuestion.getSubDepartment()!=null){
			if(beingProcessedQuestion.getSubDepartment().getName().equals(beingClubbedQuestion.getSubDepartment().getName())){
				/**** same chart ****/
				if(beingProcessedQuestion.getChartAnsweringDate()!=null 
						&& beingClubbedQuestion.getChartAnsweringDate()!=null
						&& beingProcessedQuestion.getChartAnsweringDate().getId().equals(beingClubbedQuestion.getChartAnsweringDate().getId())){
					if(beingProcessedQuestion.getNumber().compareTo(beingClubbedQuestion.getNumber()) < 0){
						return beingProcessedIsPrimary(beingProcessedQuestion,beingClubbedQuestion);
					}else{
						return beingClubbedIsPrimary(beingProcessedQuestion,beingClubbedQuestion);
					}	
				}
				/**** different chart ****/
				else if(beingProcessedQuestion.getChartAnsweringDate()!=null 
						&& beingClubbedQuestion.getChartAnsweringDate()!=null
						&& beingProcessedQuestion.getChartAnsweringDate().getAnsweringDate().before(beingClubbedQuestion.getChartAnsweringDate().getAnsweringDate())){
					return beingProcessedIsPrimary(beingProcessedQuestion,beingClubbedQuestion);
				}
				else if(beingProcessedQuestion.getChartAnsweringDate()!=null 
						&& beingClubbedQuestion.getChartAnsweringDate()!=null
						&& beingProcessedQuestion.getChartAnsweringDate().getAnsweringDate().after(beingClubbedQuestion.getChartAnsweringDate().getAnsweringDate())){
					return beingClubbedIsPrimary(beingProcessedQuestion,beingClubbedQuestion);
				}
				/**** no chart ****/
				else{
					if(beingProcessedQuestion.getNumber().compareTo(beingClubbedQuestion.getNumber()) < 0){
						return beingProcessedIsPrimary(beingProcessedQuestion,beingClubbedQuestion);
					}else{
						return beingClubbedIsPrimary(beingProcessedQuestion,beingClubbedQuestion);
					}	
				}
			}else{
				return "QUESTIONS_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
			}			
		}
		return "CLUBBING_FAILED";
	}
	
	private String beingProcessedIsPrimary(Question beingProcessedQuestion, 
			Question beingClubbedQuestion) {
		String locale = beingClubbedQuestion.getLocale();				
		
		String beingProcessedQnISType = 
			beingProcessedQuestion.getInternalStatus().getType();
		String beingProcessedQnRSType = 
			beingProcessedQuestion.getRecommendationStatus().getType();
		
		Status beingClubbedQnIS = 
			beingClubbedQuestion.getInternalStatus();		
		String beingClubbedQnISType =
			beingClubbedQuestion.getInternalStatus().getType();
		String beingClubbedQnRSType =
			beingClubbedQuestion.getRecommendationStatus().getType();
		
		Status unProcessedStatus = 
			Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
					locale);
		Status TO_BE_PUT_UP =
			Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = 
			Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);

		// CASE A: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "TO_BE_PUT_UP"
		if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				|| (beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))) {
			Status clubbed = 
				Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, clubbed, clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}
		// CASE B: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				&& ((beingClubbedQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION) 
						&& beingClubbedQnIS.getPriority().compareTo(TO_BE_PUT_UP.getPriority()) > 0
						&& beingClubbedQnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0)
						|| (! beingClubbedQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
								&& beingClubbedQnIS.getPriority().compareTo(unProcessedStatus.getPriority()) > 0
								&& beingClubbedQnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE C: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "FINAL"
		// CASE C1
		else if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& beingClubbedQnIS.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE C2
		else if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}		
		// Case for CONVERT_TO_UNSTARRED will come here.
		// CASE D: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "TO_BE_PUT_UP"
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE E: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						|| beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))) {
			// End beingClubbedQuestion's workflow, club it with beingProcessedQuestion
			// and set its status to "TO_BE_CLUBBED_WITH_PENDING"
			endProcess(beingClubbedQuestion);
			removeExistingWorkflowAttributes(beingClubbedQuestion);
			
			Status clubbedWithPending = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "FINAL"
		// CASE F1
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK) 
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingProcessedQuestion);
			removeExistingWorkflowAttributes(beingProcessedQuestion);
			
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE F2
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK) 
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingProcessedQuestion);
			removeExistingWorkflowAttributes(beingProcessedQuestion);
			
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";			
		}
		// CASE G: beingProcessedQn = "FINAL", beingClubbedQn = "TO_BE_PUT_UP"
		// CASE G1
		else if((beingProcessedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) 
				&& beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE G2
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}
		// CASE G3
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status putUpForRejection = Status.findByType(ApplicationConstants.QUESTION_PUTUP_REJECTION, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, putUpForRejection, putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}
		// CASE H: beingProcessedQn = "FINAL", beingClubbedQn = "IN_WORKFLOW"
		// CASE H1
		else if((beingProcessedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) 
				&& (beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						|| beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingClubbedQuestion);
			removeExistingWorkflowAttributes(beingClubbedQuestion);
			
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE H2
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) 
				&& (beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						|| beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingClubbedQuestion);
			removeExistingWorkflowAttributes(beingClubbedQuestion);
			
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}
		// CASE I: beingProcessedQn = "FINAL", beingClubbedQn = "FINAL"
		// CASE I1
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) 
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			Status admitted=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, admitted, admitted, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}

	private String beingClubbedIsPrimary(Question beingProcessedQuestion,
			Question beingClubbedQuestion) {
		String locale = beingClubbedQuestion.getLocale();				
		
		String beingProcessedQnISType = 
			beingProcessedQuestion.getInternalStatus().getType();
		String beingProcessedQnRSType = 
			beingProcessedQuestion.getRecommendationStatus().getType();
		
		Status beingClubbedQnIS = 
			beingClubbedQuestion.getInternalStatus();		
		String beingClubbedQnISType =
			beingClubbedQuestion.getInternalStatus().getType();
		String beingClubbedQnRSType =
			beingClubbedQuestion.getRecommendationStatus().getType();
		
		Status unProcessedStatus = 
			Status.findByType(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, 
					locale);
		Status TO_BE_PUT_UP =
			Status.findByType(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = 
			Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		
		// CASE A: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "TO_BE_PUT_UP"
		if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				|| (beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED))) {
			Status clubbed = 
				Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, clubbed, clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}
		// CASE B: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				&& ((beingClubbedQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION) 
						&& beingClubbedQnIS.getPriority().compareTo(TO_BE_PUT_UP.getPriority()) > 0 
						&& beingClubbedQnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0)
						|| (! beingClubbedQuestion.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)
								&& beingClubbedQnIS.getPriority().compareTo(unProcessedStatus.getPriority()) > 0
								&& beingClubbedQnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE C: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "FINAL"
		// CASE C1
		else if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& beingClubbedQnIS.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE C2
		else if((beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) 
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}
		// Case for CONVERT_TO_UNSTARRED will come here.
		// CASE D: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "TO_BE_PUT_UP"
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE E: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						|| beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "TO_BE_CLUBBE_WITH_PENDING"
			endProcess(beingProcessedQuestion);
			removeExistingWorkflowAttributes(beingProcessedQuestion);
			
			Status clubbedWithPending = Status.findByType(ApplicationConstants.QUESTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "FINAL"
		// CASE F1
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK) 
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
						&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION))) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingProcessedQuestion);
			removeExistingWorkflowAttributes(beingProcessedQuestion);
			
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE F2
		else if((beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK) 
				|| beingProcessedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)) 
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingProcessedQuestion);
			removeExistingWorkflowAttributes(beingProcessedQuestion);
			
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}
		// CASE G: beingProcessedQn = "FINAL", beingClubbedQn = "TO_BE_PUT_UP"
		// CASE G1
		else if((beingProcessedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) 
				&& beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE G2
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}
		// CASE G3
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_REJECTION) 
				&& (beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedQnISType.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP))) {
			Status putUpForRejection = Status.findByType(ApplicationConstants.QUESTION_PUTUP_REJECTION, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, putUpForRejection, putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}
		// CASE H: beingProcessedQn = "FINAL", beingClubbedQn = "IN_WORKFLOW"
		// CASE H1
		else if((beingProcessedQuestion.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)
				&& beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) 
				&& (beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						|| beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingClubbedQuestion);
			removeExistingWorkflowAttributes(beingClubbedQuestion);
			
			Status convertedToUnstarredAndAdmit = Status.findByType(ApplicationConstants.QUESTION_PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, convertedToUnstarredAndAdmit, convertedToUnstarredAndAdmit, locale);
			return "PROCESSED_TO_BE_CONVERTED_TO_UNSTARRED_AND_ADMIT";
		}
		// CASE H2
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) 
				&& (beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
						|| beingClubbedQnRSType.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingClubbedQuestion);
			removeExistingWorkflowAttributes(beingClubbedQuestion);			
							
			Status nameClubbing = Status.findByType(ApplicationConstants.QUESTION_PUTUP_NAMECLUBBING, locale);
			actualClubbing(beingProcessedQuestion, beingClubbedQuestion, nameClubbing, nameClubbing, locale);
			return "PROCESSED_TO_BE_NAMED_CLUBBED_WITH_PENDING";
		}
		// CASE I: beingProcessedQn = "FINAL", beingClubbedQn = "FINAL"
		// CASE I1
		else if(beingProcessedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION) 
				&& beingClubbedQnISType.equals(ApplicationConstants.QUESTION_FINAL_ADMISSION)) {
			Status admitted=Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
			actualClubbing(beingClubbedQuestion, beingProcessedQuestion, admitted, admitted, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}		

	private void actualClubbing(Question parent,Question child,
			Status newInternalStatus,Status newRecommendationStatus,String locale){
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child question are obtained
		 * c.Child question is updated(parent,internal status,recommendation status) 
		 * d.Child Question entry is made in Clubbed Entity and child question clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child questions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child question clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent question is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentQnId = parent.getId();
				Question clubbedQn = i.getQuestion();
				Long clubbedQnId = clubbedQn.getId();
				if(! parentQnId.equals(clubbedQnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setQuestion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Question question=k.getQuestion();
				question.setParent(parent);
				
				Status internalStatus = question.getInternalStatus();
				if(internalStatus != null 
						&& internalStatus.getType().equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)) {
					question.setInternalStatus(newInternalStatus);
					question.setRecommendationStatus(newRecommendationStatus);
				}
				
				question.merge();
				k.setQuestion(question);
				k.merge();
				parentClubbedEntities.add(k);
			}			
		}
		parent.setParent(null);
		parent.setClubbedEntities(parentClubbedEntities);
		parent.merge();

		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
	
	private void endProcess(Question question) {
		try {
			// Complete task & end process
			WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(question);				
			String taskId = wfDetails.getTaskId();
			Task task = processService.findTaskById(taskId);
			
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("pv_endflag", "end");
			processService.completeTask(task, properties);
			
			// Update WorkflowDetails
			wfDetails.setStatus("COMPLETED");
			wfDetails.setCompletionTime(new Date());
			wfDetails.merge();
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	private void removeExistingWorkflowAttributes(Question question) {
		// Update question so as to remove existing workflow
		// based attributes
		question.setEndFlag(null);
		question.setLevel("0");
		question.setTaskReceivedOn(null);
		question.setWorkflowDetailsId(null);
		question.setWorkflowStarted("NO");
		question.setWorkflowStartedOn(null);
		question.setActor(null);
		question.setLocalizedActorName("");
		question.simpleMerge();
	}

	public Question updateClubbing(final Question domain) {
		/**** On same chart ****/
		if(domain.getChartAnsweringDate().getId().equals(domain.getParent().getChartAnsweringDate().getId())){
			// if(domain.getNumber() > domain.getParent().getNumber()){
			if(domain.getNumber().compareTo(domain.getParent().getNumber()) > 0){
				actualClubbing(domain.getParent(),domain,domain.getInternalStatus(), domain.getRecommendationStatus(),domain.getLocale());
			}else{
				actualClubbing(domain,domain.getParent(),domain.getInternalStatus(), domain.getRecommendationStatus(),domain.getLocale());
			}
		}
		/**** on different charts (parent is on a previous chart)****/
		else if(domain.getChartAnsweringDate().getAnsweringDate().after(domain.getParent().getChartAnsweringDate().getAnsweringDate())){
			actualClubbing(domain.getParent(),domain,domain.getInternalStatus(), domain.getRecommendationStatus(),domain.getLocale());
		}
		/**** on different charts (clubbed question is on a previous chart)****/
		else if(domain.getChartAnsweringDate().getAnsweringDate().before(domain.getParent().getChartAnsweringDate().getAnsweringDate())){
			actualClubbing(domain,domain.getParent(),domain.getInternalStatus(), domain.getRecommendationStatus(),domain.getLocale());
		}
		/**** No charts ****/
		else{
			if(domain.getNumber().compareTo(domain.getParent().getNumber()) > 0){
				actualClubbing(domain.getParent(),domain,domain.getInternalStatus(), domain.getRecommendationStatus(),domain.getLocale());
			}else{
				actualClubbing(domain,domain.getParent(),domain.getInternalStatus(), domain.getRecommendationStatus(),domain.getLocale());
			}	
		}	
		return domain;
	}
	/**** Question Clubbing Ends ****/

	/**** Motion Unclubbing Begins ****/
	public String unclubMotion(final Long motionBeingProcessed,
			final Long motionBeingClubbed, final String locale) {
		try {
			Motion beingProcessedMotion = Motion.findById(Motion.class, motionBeingProcessed);
			Motion beingClubbedMotion = Motion.findById(Motion.class, motionBeingClubbed);
			// ClubbedEntity clubbedEntityToRemove=null;

			/**** If processed question's number is less than clubbed question's number
			 * then clubbed question is removed from the clubbing of processed question
			 * ,clubbed question's parent is set to null ,new clubbing of processed 
			 * question is set,their position is updated****/
			if(beingProcessedMotion.getNumber().compareTo(beingClubbedMotion.getNumber()) < 0){
				List<ClubbedEntity> oldClubbedMotions = beingProcessedMotion.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
				Integer position = 0;
				boolean found = false;
				for(ClubbedEntity i : oldClubbedMotions){
					if(! i.getMotion().getId().equals(beingClubbedMotion.getId())){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedMotions.add(i);
						}else{
							newClubbedMotions.add(i);                		
						}
					}else{
						found = true;
						position = i.getPosition();
						// clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedMotions.isEmpty()){
					beingProcessedMotion.setClubbedEntities(newClubbedMotions);
				}else{
					beingProcessedMotion.setClubbedEntities(null);
				}            
				beingProcessedMotion.simpleMerge();
				beingClubbedMotion.setParent(null);
				Status newstatus = Status.findByFieldName(Status.class,"type",ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				
				if(!beingClubbedMotion.getInternalStatus().getType().startsWith("motion_final")){
					Status submitStatus=Status.findByType(ApplicationConstants.MOTION_SUBMIT, locale);
					beingClubbedMotion.setInternalStatus(newstatus);
					beingClubbedMotion.setRecommendationStatus(newstatus);
					beingClubbedMotion.setStatus(submitStatus);
				}
				beingClubbedMotion.simpleMerge();
			}else if(beingProcessedMotion.getNumber().compareTo(beingClubbedMotion.getNumber()) > 0){
				
				List<ClubbedEntity> oldClubbedMotions = beingClubbedMotion.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
				
				Integer position = 0;
				boolean found = false;
				
				for(ClubbedEntity i : oldClubbedMotions){
					if(! i.getMotion().getId().equals(beingProcessedMotion.getId())){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedMotions.add(i);
						}else{
							newClubbedMotions.add(i);                		
						}
					}else{
						found = true;
						position = i.getPosition();
					}
				}
				beingClubbedMotion.setClubbedEntities(newClubbedMotions);
				beingClubbedMotion.simpleMerge();

				beingProcessedMotion.setParent(null);
				Status newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				
				if(!beingProcessedMotion.getInternalStatus().getType().startsWith("motion_final")){
					Status submitStatus=Status.findByType(ApplicationConstants.MOTION_SUBMIT, locale);
					beingProcessedMotion.setInternalStatus(newstatus);
					beingProcessedMotion.setRecommendationStatus(newstatus);
					beingProcessedMotion.setStatus(submitStatus);
				}
				beingProcessedMotion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
		}		
		return "SUCCESS";
	}

	public Question unclub(final Question domain){
		try {			
			/**** Update Parent(clubbed entities-number of clubbed entities and each clubbed entities position) ****/
			List<ClubbedEntity> parentClubbedEntities =domain.getParent().getClubbedEntities();
			List<ClubbedEntity> parentNewClubbedEntities=new ArrayList<ClubbedEntity>();
			for(ClubbedEntity i:parentClubbedEntities){
				if(! i.getQuestion().getId().equals(domain.getId())){
					parentNewClubbedEntities.add(i);
				}
			}
			if(!parentNewClubbedEntities.isEmpty()){
				domain.getParent().setClubbedEntities(parentNewClubbedEntities);
			}else{
				domain.getParent().setClubbedEntities(null);
			} 
			domain.getParent().simpleMerge();
			List<ClubbedEntity> clubbedEntities=domain.getParent().findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,domain.getLocale());
			Integer position=1;
			for(ClubbedEntity i:clubbedEntities){
				i.setPosition(position);
				position++;
				i.merge();
			}

			/**** Update Domain(Parent,internal status,recommendation status,file,fileIndex,fileSent) ****/
			domain.setParent(null);
			Status newStatus=null;
			if(domain.getType().equals("questions_unstarred")
					||domain.getType().equals("questions_halfhourdiscussion_from_question")
					||domain.getType().equals("questions_shortnotice")){
				newStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
			}else{
				newStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
			}
			domain.setInternalStatus(newStatus);
			domain.setRecommendationStatus(newStatus);
			domain.simpleMerge();			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return domain;
	}

	public Question unclubWithoutMerge(final Question beingProcessedQuestion,
			final Question beingClubbedQuestion, final String locale) {
		try {
			/**** If processed question's number is less than clubbed question's number
			 * then clubbed question is removed from the clubbing of processed question
			 * ,clubbed question's parent is set to null ,new clubbing of processed 
			 * question is set,their position is updated ****/
			if(beingProcessedQuestion.getNumber().compareTo(beingClubbedQuestion.getNumber()) < 0){
				List<ClubbedEntity> oldClubbedQuestions=beingProcessedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(! (i.getQuestion().getId().equals(beingClubbedQuestion.getId())) ){
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
						//clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedQuestions.isEmpty()){
					beingProcessedQuestion.setClubbedEntities(newClubbedQuestions);
				}else{
					beingProcessedQuestion.setClubbedEntities(null);
				}            
				beingProcessedQuestion.simpleMerge();
				//clubbedEntityToRemove.remove();
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
			// }else if(beingProcessedQuestion.getNumber() > beingClubbedQuestion.getNumber()){
			}else if(beingProcessedQuestion.getNumber().compareTo(beingClubbedQuestion.getNumber()) > 0){
				List<ClubbedEntity> oldClubbedQuestions=beingClubbedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(! i.getQuestion().getId().equals(beingProcessedQuestion.getId())){
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
						//clubbedEntityToRemove=i;
					}
				}
				beingClubbedQuestion.setClubbedEntities(newClubbedQuestions);				
				//clubbedEntityToRemove.remove();
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
				beingProcessedQuestion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
		}		
		return beingClubbedQuestion;
	}

	public Question unclubChildrenWithStatus(final Question domain,final Status status) {
		List<ClubbedEntity> domainClubbedEntities =domain.getClubbedEntities();
		List<ClubbedEntity> domainNewClubbedEntities=new ArrayList<ClubbedEntity>();
		for(ClubbedEntity i:domainClubbedEntities){
			if(!i.getQuestion().getInternalStatus().getType().equals(status.getType())){
				domainNewClubbedEntities.add(i);
			}else{
				i.getQuestion().setParent(null);				
				Status newStatus=null;
				if(domain.getType().getType().equals("questions_unstarred")
						||domain.getType().getType().equals("questions_halfhourdiscussion_from_question")
						||domain.getType().getType().equals("questions_shortnotice")){
					newStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
				}else{
					newStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
				}
				i.getQuestion().setInternalStatus(newStatus);
				i.getQuestion().setRecommendationStatus(newStatus);
				i.getQuestion().simpleMerge();
			}
		}
		if(!domainNewClubbedEntities.isEmpty()){
			domain.setClubbedEntities(domainNewClubbedEntities);
		}else{
			domain.getParent().setClubbedEntities(null);
		} 
		domain.simpleMerge();
		List<ClubbedEntity> clubbedEntities=domain.findClubbedEntitiesByChartAnsweringDateQuestionNumber(ApplicationConstants.ASC,domain.getLocale());
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
		return domain;
	}

	/**** Question Unclubbing Ends ****/

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

	@SuppressWarnings("unchecked")
	private String alreadyClubbed(final Bill beingProcessedBill,
			final Bill beingClubbedBill,final String locale) {
		/**** If either of the two bill has an entry in clubbed entity it means the bill is already clubbed ****/
		String strQuery="SELECT ce FROM Bill b JOIN b.clubbedEntities ce " +
				"WHERE ce.bill.id=:clubbedBillId " +
				" OR ce.bill.id=:processedBillId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("clubbedBillId", beingClubbedBill.getId());
		query.setParameter("processedBillId",beingProcessedBill.getId());
		List<ClubbedEntity> clubEntities=query.getResultList();
		if(clubEntities!=null && clubEntities.size()>0){
			/**** Clubbed bill has an entry in clubbed entities ****/
			return "BEINGSEARCHED_BILL_ALREADY_CLUBBED";
		}else{
			return "NO";
		}
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

	// TODO: Method need to be revised keeping in mind the beingClubbedIsPrimary()
	// written for Question.
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
				&&beingClubbedBillStatus.getPriority().compareTo(unProcessedStatus.getPriority()) >= 0
				&&beingClubbedBillStatus.getPriority().compareTo(approvalStatus.getPriority()) < 0){
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
			// ClubbedEntity clubbedEntityToRemove=null;

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
					if(! i.getBill().getId().equals(beingClubbedBill.getId())){
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
						// clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedBills.isEmpty()){
					beingProcessedBill.setClubbedEntities(newClubbedBills);
				}else{
					beingProcessedBill.setClubbedEntities(null);
				}            
				beingProcessedBill.simpleMerge();
				//clubbedEntityToRemove.remove();
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
					if(! i.getBill().getId().equals(beingProcessedBill.getId())){
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
						//clubbedEntityToRemove=i;
					}
				}
				beingClubbedBill.setClubbedEntities(newClubbedBills);
				beingClubbedBill.simpleMerge();
				//clubbedEntityToRemove.remove();
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
		//return only required number of records
		List<BillSearchVO> resultBillSearchVOs = new ArrayList<BillSearchVO>();
		for(int i=start; i<billSearchVOs.size(); i++) {
			if(i<noofRecords) {
				resultBillSearchVOs.add(billSearchVOs.get(i));
			} else {
				break;
			}
		}
		return resultBillSearchVOs;
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

	@SuppressWarnings("rawtypes")
	public ClubbedEntity findByQuestion(final Question question,
			final String locale) {
		String strQuery="SELECT ce FROM ClubbedEntity ce JOIN ce.question q "
				+ " WHERE q.id=:question AND q.locale=:locale ORDER BY ce.id DESC";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("question",question.getId());
		query.setParameter("locale",locale);
		List result=query.getResultList();
		if(result!=null && !result.isEmpty()){
			return (ClubbedEntity) result.get(0);
		}
		return null;
	}
	
	/**** Motion Clubbing Begins ****/
	public String clubMotion(final Long motionBeingProcessed,
			final Long motionBeingClubbed,final String locale) {
		String clubbingStatus=null;
		try{
			Motion beingProcessedMotion = Motion.findById(Motion.class, motionBeingProcessed);
			Motion beingClubbedMotion = Motion.findById(Motion.class, motionBeingClubbed);
			if(beingProcessedMotion != null && beingClubbedMotion != null){				
				String alreadyClubbedStatus = alreadyClubbedMotion(beingProcessedMotion, beingClubbedMotion, locale);
				if(alreadyClubbedStatus.equals("NO")){
					clubbingStatus = clubbingRules(beingProcessedMotion, beingClubbedMotion, locale);
				}else{
					clubbingStatus = alreadyClubbedStatus;
				}
			}else{
				if(beingClubbedMotion == null){
					clubbingStatus = "BEINGSEARCHED_DOESNOT_EXIST";
				}else if(beingProcessedMotion == null){
					clubbingStatus="BEINGPROCESSED_DOESNOT_EXIST";
				}			
			}
		}catch(Exception ex){
			logger.error("CLUBBING_FAILED",ex);
			clubbingStatus="CLUBBING_FAILED";
			return clubbingStatus;
		}
		return clubbingStatus;
	}	
	
	@SuppressWarnings("unchecked")
	private String alreadyClubbedMotion(final Motion beingProcessedMotion,
			final Motion beingClubbedMotion,final String locale) {
		/**** If any of the two motions have entries in clubbed entities then it means they are already clubbed
		 * and hence clubbing cannot proceed ****/
		String strQuery="SELECT ce FROM Motion q JOIN q.clubbedEntities ce " +
				"WHERE ce.motion.id=:clubbedMotionId " +
				" OR ce.motion.id=:processedMotionId";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("clubbedMotionId", beingClubbedMotion.getId());
		query.setParameter("processedMotionId",beingProcessedMotion.getId());
		List<ClubbedEntity> clubEntities = query.getResultList();
		if(clubEntities!=null && clubEntities.size()>0){
			return "BEINGSEARCHED_MOTION_ALREADY_CLUBBED";
		}else{
			return "NO";
		}
	}

	private String clubbingRules(Motion beingProcessedMotion,
			Motion beingClubbedMotion, String locale) {			
		if(beingProcessedMotion.getType() != null
				&& beingClubbedMotion.getType() != null
				&& (beingProcessedMotion.getType().getType().equals(beingClubbedMotion.getType().getType())
				|| (beingProcessedMotion.getType().getType().equals(ApplicationConstants.MOTION_CALLING_ATTENTION)))
				&& beingProcessedMotion.getMinistry() !=null
				&& beingClubbedMotion.getMinistry() !=null
				&& beingProcessedMotion.getMinistry().getName().equals(beingClubbedMotion.getMinistry().getName())
				&& beingProcessedMotion.getSubDepartment() !=null
				&& beingClubbedMotion.getSubDepartment() !=null){
			if(beingProcessedMotion.getSubDepartment().getName().equals(beingClubbedMotion.getSubDepartment().getName())){
				
				if(beingProcessedMotion.getNumber().compareTo(beingClubbedMotion.getNumber()) < 0){
						return beingProcessedIsPrimary(beingProcessedMotion, beingClubbedMotion);
				}else{
					return beingClubbedIsPrimary(beingProcessedMotion, beingClubbedMotion);
				}
			}else{
				return "MOTIONS_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
			}			
		}
		return "CLUBBING_FAILED";
	}
	
	private String beingProcessedIsPrimary(Motion beingProcessedMotion, Motion beingClubbedMotion) {
		String locale = beingClubbedMotion.getLocale();				
		
		String beingProcessedMnISType = beingProcessedMotion.getInternalStatus().getType();
		String beingProcessedMnRSType = beingProcessedMotion.getRecommendationStatus().getType();
		
		Status beingClubbedMnIS = beingClubbedMotion.getInternalStatus();		
		String beingClubbedMnISType = beingClubbedMotion.getInternalStatus().getType();
		String beingClubbedMnRSType = beingClubbedMotion.getRecommendationStatus().getType();
		
		Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);

		// CASE A: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "TO_BE_PUT_UP"
		if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				|| (beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED))) {
			Status clubbed = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbed, clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}
		// CASE B: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedMnIS.getPriority().compareTo(TO_BE_PUT_UP.getPriority()) > 0
						&& beingClubbedMnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0)) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE C: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "FINAL"
		// CASE C1
		else if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))
						&& beingClubbedMnIS.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE C2
		else if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}		
		// Case for CONVERT_TO_UNSTARRED will come here.
		// CASE D: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "TO_BE_PUT_UP"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE E: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))) {
			// End beingClubbedMotion's workflow, club it with beingProcessedMotion
			// and set its status to "TO_BE_CLUBBED_WITH_PENDING"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status clubbedWithPending = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "FINAL"
		// CASE F1
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F2
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";			
		}
		// CASE G: beingProcessedQn = "FINAL", beingClubbedQn = "TO_BE_PUT_UP"
		// CASE G1
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G2
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G3
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_REJECTION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status putUpForRejection = Status.findByType(ApplicationConstants.MOTION_PUTUP_REJECTION, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, putUpForRejection, putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}
		// CASE H: beingProcessedQn = "FINAL", beingClubbedQn = "IN_WORKFLOW"
		// CASE H1
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE H2
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE I: beingProcessedQn = "FINAL", beingClubbedQn = "FINAL"
		// CASE I1
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			Status admitted=Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, admitted, admitted, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}

	private String beingClubbedIsPrimary(Motion beingProcessedMotion,
			Motion beingClubbedMotion) {
		String locale = beingClubbedMotion.getLocale();				
		
		String beingProcessedMnISType = beingProcessedMotion.getInternalStatus().getType();
		String beingProcessedMnRSType = beingProcessedMotion.getRecommendationStatus().getType();
		
		Status beingClubbedMnIS = beingClubbedMotion.getInternalStatus();		
		String beingClubbedMnISType = beingClubbedMotion.getInternalStatus().getType();
		String beingClubbedMnRSType = beingClubbedMotion.getRecommendationStatus().getType();
		
		Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);
		
		// CASE A: beingProcessedMn = "TO_BE_PUT_UP", beingClubbedQn = "TO_BE_PUT_UP"
		if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				|| (beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED))) {
			Status clubbed = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbed, clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}
		// CASE B: beingProcessedMn = "TO_BE_PUT_UP", beingClubbedMn = "IN_WORKFLOW"
		else if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedMnIS.getPriority().compareTo(TO_BE_PUT_UP.getPriority()) > 0 
						&& beingClubbedMnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0)) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE C: beingProcessedMn = "TO_BE_PUT_UP", beingClubbedMn = "FINAL"
		// CASE C1
		else if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedMnIS.equals(ApplicationConstants.MOTION_FINAL_ADMISSION))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE C2
		else if((beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// Case for CONVERT_TO_UNSTARRED will come here.
		// CASE D: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "TO_BE_PUT_UP"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE E: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "TO_BE_CLUBBE_WITH_PENDING"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status clubbedWithPending = Status.findByType(ApplicationConstants.MOTION_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "FINAL"
		// CASE F1
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE F2
		else if((beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedMotion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G: beingProcessedQn = "FINAL", beingClubbedQn = "TO_BE_PUT_UP"
		// CASE G1
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G2
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G3
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_REJECTION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			Status putUpForRejection = Status.findByType(ApplicationConstants.MOTION_PUTUP_REJECTION, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, putUpForRejection, putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}
		// CASE H: beingProcessedQn = "FINAL", beingClubbedQn = "IN_WORKFLOW"
		// CASE H1
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedMotion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE H2
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedMotion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);			
							
			Status toBeClubbed = Status.findByType(ApplicationConstants.MOTION_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE I: beingProcessedMn = "FINAL", beingClubbedMn = "FINAL"
		// CASE I1
		else if(beingProcessedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION) 
				&& beingClubbedMnISType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			Status admitted=Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, admitted, admitted, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}	
	
	private void actualClubbing(Motion parent,Motion child,
			Status newInternalStatus,Status newRecommendationStatus,String locale){
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				Motion clubbedQn = i.getMotion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMnId = parent.getId();
				Motion clubbedMn = i.getMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(! parentMnId.equals(clubbedMnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		if(child.getFile()!=null){
			child.setFile(null);
			child.setFileIndex(null);
			child.setFileSent(false);
		}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				Motion motion=k.getMotion();
				motion.setParent(parent);
				
				Status internalStatus = motion.getInternalStatus();
				if(internalStatus != null 
						&& internalStatus.getType().equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP)) {
					motion.setInternalStatus(newInternalStatus);
					motion.setRecommendationStatus(newRecommendationStatus);
				}
				
				motion.merge();
				k.setMotion(motion);
				k.merge();
				parentClubbedEntities.add(k);
			}			
		}
		parent.setParent(null);
		parent.setClubbedEntities(parentClubbedEntities);
		parent.merge();

		List<ClubbedEntity> clubbedEntities = parent.findClubbedEntitiesByMotionNumber(ApplicationConstants.ASC, locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
	
	private void endProcess(Motion motion) {
		try {
			// Complete task & end process
			WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);				
			String taskId = wfDetails.getTaskId();
			Task task = processService.findTaskById(taskId);
			
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("pv_endflag", "end");
			processService.completeTask(task, properties);
			
			// Update WorkflowDetails
			wfDetails.setStatus("COMPLETED");
			wfDetails.setCompletionTime(new Date());
			wfDetails.merge();
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	private void removeExistingWorkflowAttributes(Motion motion) {
		// Update motion so as to remove existing workflow
		// based attributes
		motion.setEndFlag(null);
		motion.setLevel("0");
		motion.setTaskReceivedOn(null);
		motion.setWorkflowDetailsId(null);
		motion.setWorkflowStarted("NO");
		motion.setWorkflowStartedOn(null);
		motion.setActor(null);
		motion.setLocalizedActorName("");
		motion.simpleMerge();
	}
	
	/**** Question Unclubbing Begins ****/
	public String unclub(final Long questionBeingProcessed,
			final Long questionBeingClubbed, final String locale) {
		try {
			Question beingProcessedQuestion=Question.findById(Question.class,questionBeingProcessed);
			Question beingClubbedQuestion=Question.findById(Question.class,questionBeingClubbed);
			// ClubbedEntity clubbedEntityToRemove=null;

			/**** If processed question's number is less than clubbed question's number
			 * then clubbed question is removed from the clubbing of processed question
			 * ,clubbed question's parent is set to null ,new clubbing of processed 
			 * question is set,their position is updated****/
			// if(beingProcessedQuestion.getNumber()<beingClubbedQuestion.getNumber()){
			if(beingProcessedQuestion.getNumber().compareTo(beingClubbedQuestion.getNumber()) < 0){
				List<ClubbedEntity> oldClubbedQuestions=beingProcessedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(! i.getQuestion().getId().equals(beingClubbedQuestion.getId())){
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
						// clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedQuestions.isEmpty()){
					beingProcessedQuestion.setClubbedEntities(newClubbedQuestions);
				}else{
					beingProcessedQuestion.setClubbedEntities(null);
				}            
				beingProcessedQuestion.simpleMerge();
				//clubbedEntityToRemove.remove();
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
				if(!beingClubbedQuestion.getInternalStatus().getType().startsWith(ApplicationConstants.QUESTION_FINAL)){
					Status submitStatus=Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale);
					beingClubbedQuestion.setInternalStatus(newstatus);
					beingClubbedQuestion.setRecommendationStatus(newstatus);
					beingClubbedQuestion.setStatus(submitStatus);
				}
				beingClubbedQuestion.simpleMerge();
			}else if(beingProcessedQuestion.getNumber().compareTo(beingClubbedQuestion.getNumber()) > 0){
				List<ClubbedEntity> oldClubbedQuestions=beingClubbedQuestion.getClubbedEntities();
				List<ClubbedEntity> newClubbedQuestions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedQuestions){
					if(! i.getQuestion().getId().equals(beingProcessedQuestion.getId())){
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
						// clubbedEntityToRemove=i;
					}
				}
				beingClubbedQuestion.setClubbedEntities(newClubbedQuestions);
				beingClubbedQuestion.simpleMerge();
				//clubbedEntityToRemove.remove();
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
				if(!beingProcessedQuestion.getInternalStatus().getType().startsWith(ApplicationConstants.QUESTION_FINAL)){
					Status submitStatus=Status.findByType(ApplicationConstants.QUESTION_SUBMIT, locale);
					beingProcessedQuestion.setInternalStatus(newstatus);
					beingProcessedQuestion.setRecommendationStatus(newstatus);
					beingProcessedQuestion.setStatus(submitStatus);
				}
				beingProcessedQuestion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
		}		
		return "SUCCESS";
	}
	
	
	/**** Motion Clubbing Begins ****/
	public String clubStandalone(final Long motionBeingProcessed,
			final Long motionBeingClubbed,final String locale) {
		String clubbingStatus=null;
		try{
			StandaloneMotion beingProcessedMotion = StandaloneMotion.findById(StandaloneMotion.class, motionBeingProcessed);
			StandaloneMotion beingClubbedMotion = StandaloneMotion.findById(StandaloneMotion.class, motionBeingClubbed);
			if(beingProcessedMotion != null && beingClubbedMotion != null){				
				String alreadyClubbedStatus = alreadyClubbedStandaloneMotion(beingProcessedMotion, beingClubbedMotion, locale);
				if(alreadyClubbedStatus.equals("NO")){
					clubbingStatus = clubbingRules(beingProcessedMotion, beingClubbedMotion, locale);
				}else{
					clubbingStatus = alreadyClubbedStatus;
				}
			}else{
				if(beingClubbedMotion == null){
					clubbingStatus = "BEINGSEARCHED_DOESNOT_EXIST";
				}else if(beingProcessedMotion == null){
					clubbingStatus="BEINGPROCESSED_DOESNOT_EXIST";
				}			
			}
		}catch(Exception ex){
			logger.error("CLUBBING_FAILED",ex);
			clubbingStatus="CLUBBING_FAILED";
			return clubbingStatus;
		}
		return clubbingStatus;
	}
	
	@SuppressWarnings("unchecked")
	private String alreadyClubbedStandaloneMotion(final StandaloneMotion beingProcessedMotion,
			final StandaloneMotion beingClubbedMotion,final String locale) {
		/**** If any of the two motions have entries in clubbed entities then it means they are already clubbed
		 * and hence clubbing cannot proceed ****/
		String strQuery="SELECT ce FROM StandaloneMotion q JOIN q.clubbedEntities ce " +
				"WHERE ce.motion.id=:clubbedMotionId " +
				" OR ce.motion.id=:processedMotionId";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("clubbedMotionId", beingClubbedMotion.getId());
		query.setParameter("processedMotionId",beingProcessedMotion.getId());
		List<ClubbedEntity> clubEntities = query.getResultList();
		if(clubEntities!=null && clubEntities.size()>0){
			return "BEINGSEARCHED_MOTION_ALREADY_CLUBBED";
		}else{
			return "NO";
		}
	}
	
	private String beingProcessedIsPrimary(StandaloneMotion beingProcessedMotion, StandaloneMotion beingClubbedMotion) {
		String locale = beingClubbedMotion.getLocale();				
		
		String beingProcessedMnISType = beingProcessedMotion.getInternalStatus().getType();
		String beingProcessedMnRSType = beingProcessedMotion.getRecommendationStatus().getType();
		
		Status beingClubbedMnIS = beingClubbedMotion.getInternalStatus();		
		String beingClubbedMnISType = beingClubbedMotion.getInternalStatus().getType();
		String beingClubbedMnRSType = beingClubbedMotion.getRecommendationStatus().getType();
		
		Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);

		// CASE A: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "TO_BE_PUT_UP"
		if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				|| (beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED))) {
			Status clubbed = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbed, clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}
		// CASE B: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedMnIS.getPriority().compareTo(TO_BE_PUT_UP.getPriority()) > 0
						&& beingClubbedMnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0)) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE C: beingProcessedQn = "TO_BE_PUT_UP", beingClubbedQn = "FINAL"
		// CASE C1
		else if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))
						&& beingClubbedMnIS.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE C2
		else if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}		
		// Case for CONVERT_TO_UNSTARRED will come here.
		// CASE D: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "TO_BE_PUT_UP"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbedWithPending,clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE E: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))) {
			// End beingClubbedMotion's workflow, club it with beingProcessedMotion
			// and set its status to "TO_BE_CLUBBED_WITH_PENDING"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status clubbedWithPending = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "FINAL"
		// CASE F1
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F2
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";			
		}
		// CASE G: beingProcessedQn = "FINAL", beingClubbedQn = "TO_BE_PUT_UP"
		// CASE G1
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP))) {
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G2
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G3
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status putUpForRejection = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_REJECTION, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, putUpForRejection, putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}
		// CASE H: beingProcessedQn = "FINAL", beingClubbedQn = "IN_WORKFLOW"
		// CASE H1
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE H2
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedQuestion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE I: beingProcessedQn = "FINAL", beingClubbedQn = "FINAL"
		// CASE I1
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			Status admitted=Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, admitted, admitted, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}

	private String beingClubbedIsPrimary(StandaloneMotion beingProcessedMotion,
			StandaloneMotion beingClubbedMotion) {
		String locale = beingClubbedMotion.getLocale();				
		
		String beingProcessedMnISType = beingProcessedMotion.getInternalStatus().getType();
		String beingProcessedMnRSType = beingProcessedMotion.getRecommendationStatus().getType();
		
		Status beingClubbedMnIS = beingClubbedMotion.getInternalStatus();		
		String beingClubbedMnISType = beingClubbedMotion.getInternalStatus().getType();
		String beingClubbedMnRSType = beingClubbedMotion.getRecommendationStatus().getType();
		
		Status TO_BE_PUT_UP = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP, locale);
		Status approvalStatus = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
		
		// CASE A: beingProcessedMn = "TO_BE_PUT_UP", beingClubbedQn = "TO_BE_PUT_UP"
		if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				|| (beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED))) {
			Status clubbed = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbed, clubbed, locale);
			return "PROCESSED_CLUBBED_TO_SEARCHED";
		}
		// CASE B: beingProcessedMn = "TO_BE_PUT_UP", beingClubbedMn = "IN_WORKFLOW"
		else if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedMnIS.getPriority().compareTo(TO_BE_PUT_UP.getPriority()) > 0 
						&& beingClubbedMnIS.getPriority().compareTo(approvalStatus.getPriority()) < 0)) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE C: beingProcessedMn = "TO_BE_PUT_UP", beingClubbedMn = "FINAL"
		// CASE C1
		else if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				&& (beingClubbedMnIS.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE C2
		else if((beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED) 
				|| beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// Case for CONVERT_TO_UNSTARRED will come here.
		// CASE D: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "TO_BE_PUT_UP"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status clubbedWithPending = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE E: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "IN_WORKFLOW"
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "TO_BE_CLUBBE_WITH_PENDING"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status clubbedWithPending = Status.findByType(ApplicationConstants.STANDALONE_SYSTEM_CLUBBED_WITH_PENDING, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, clubbedWithPending, clubbedWithPending, locale);
			return "PROCESSED_TO_BE_CLUBBED_WITH_PENDING";
		}
		// CASE F: beingProcessedQn = "IN_WORKFLOW", beingClubbedQn = "FINAL"
		// CASE F1
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedQuestion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE F2
		else if((beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK) 
				|| beingProcessedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			// End beingProcessed Qns workflow, club it with beingClubbedMotion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingProcessedMotion);
			removeExistingWorkflowAttributes(beingProcessedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G: beingProcessedQn = "FINAL", beingClubbedQn = "TO_BE_PUT_UP"
		// CASE G1
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G2
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE G3
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_REJECTION) 
				&& (beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED)
						|| beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP))) {
			Status putUpForRejection = Status.findByType(ApplicationConstants.STANDALONE_PUTUP_REJECTION, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, putUpForRejection, putUpForRejection, locale);
			return "PROCESSED_TO_BE_PUT_UP_FOR_REJECTION";
		}
		// CASE H: beingProcessedQn = "FINAL", beingClubbedQn = "IN_WORKFLOW"
		// CASE H1
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedMotion
			// and set its status to "PUTUP_CONVERT_TO_UNSTARRED_AND_ADMIT"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);
			
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE H2
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& (beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
						|| beingClubbedMnRSType.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS))) {
			// End beingClubbed Qns workflow, club it with beingProcessedMotion
			// and set its status to "PUT_UP_FOR_NAME_CLUBBING"
			endProcess(beingClubbedMotion);
			removeExistingWorkflowAttributes(beingClubbedMotion);			
							
			Status toBeClubbed = Status.findByType(ApplicationConstants.STANDALONE_TO_BE_CLUBBED, locale);
			actualClubbing(beingProcessedMotion, beingClubbedMotion, toBeClubbed, toBeClubbed, locale);
			return "PROCESSED_TO_BE_CLUBBED";
		}
		// CASE I: beingProcessedMn = "FINAL", beingClubbedMn = "FINAL"
		// CASE I1
		else if(beingProcessedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION) 
				&& beingClubbedMnISType.equals(ApplicationConstants.STANDALONE_FINAL_ADMISSION)) {
			Status admitted=Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
			actualClubbing(beingClubbedMotion, beingProcessedMotion, admitted, admitted, locale);
			return "PROCESSED_CLUBBED_WITH_SEARCHED_AND_ADMITTED";
		}
		return "CLUBBING_FAILED";
	}	
	
	private void actualClubbing(StandaloneMotion parent, StandaloneMotion child,
			Status newInternalStatus,Status newRecommendationStatus,String locale){
		/**** a.Clubbed entities of parent question are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their chart answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childQnId = child.getId();
				StandaloneMotion clubbedQn = i.getStandaloneMotion();
				Long clubbedQnId = clubbedQn.getId();
				if(! childQnId.equals(clubbedQnId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMnId = parent.getId();
				StandaloneMotion clubbedMn = i.getStandaloneMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(! parentMnId.equals(clubbedMnId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);		
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setStandaloneMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				StandaloneMotion motion=k.getStandaloneMotion();
				motion.setParent(parent);
				
				Status internalStatus = motion.getInternalStatus();
				if(internalStatus != null 
						&& internalStatus.getType().equals(ApplicationConstants.STANDALONE_SYSTEM_TO_BE_PUTUP)) {
					motion.setInternalStatus(newInternalStatus);
					motion.setRecommendationStatus(newRecommendationStatus);
				}
				
				motion.merge();
				k.setStandaloneMotion(motion);
				k.merge();
				parentClubbedEntities.add(k);
			}			
		}
		parent.setParent(null);
		parent.setClubbedEntities(parentClubbedEntities);
		parent.merge();

		List<ClubbedEntity> clubbedEntities = StandaloneMotion.findClubbedEntitiesByMotionNumber(parent, ApplicationConstants.ASC, locale);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
	}
	
	private String clubbingRules(StandaloneMotion beingProcessedMotion,
			StandaloneMotion beingClubbedMotion, String locale) {			
		if(beingProcessedMotion.getType() != null
				&& beingClubbedMotion.getType() != null
				&& (beingProcessedMotion.getType().getType().equals(beingClubbedMotion.getType().getType())
				|| (beingProcessedMotion.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)))
				&& beingProcessedMotion.getMinistry() !=null
				&& beingClubbedMotion.getMinistry() !=null
				&& beingProcessedMotion.getMinistry().getName().equals(beingClubbedMotion.getMinistry().getName())
				&& beingProcessedMotion.getSubDepartment() !=null
				&& beingClubbedMotion.getSubDepartment() !=null){
			if(beingProcessedMotion.getSubDepartment().getName().equals(beingClubbedMotion.getSubDepartment().getName())){
				
				if(beingProcessedMotion.getNumber().compareTo(beingClubbedMotion.getNumber()) < 0){
						return beingProcessedIsPrimary(beingProcessedMotion, beingClubbedMotion);
				}else{
					return beingClubbedIsPrimary(beingProcessedMotion, beingClubbedMotion);
				}
			}else{
				return "STANDALONES_FROM_DIFFERENT_MINISTRY_DEPARTMENT_SUBDEPARTMENT";
			}			
		}
		return "CLUBBING_FAILED";
	}
	
	private void endProcess(StandaloneMotion motion) {
		try {
			// Complete task & end process
			WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);				
			String taskId = wfDetails.getTaskId();
			Task task = processService.findTaskById(taskId);
			
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("pv_endflag", "end");
			processService.completeTask(task, properties);
			
			// Update WorkflowDetails
			wfDetails.setStatus("COMPLETED");
			wfDetails.setCompletionTime(new Date());
			wfDetails.merge();
		} 
		catch (ELSException e) {
			e.printStackTrace();
		}
	}
	
	private void removeExistingWorkflowAttributes(StandaloneMotion motion) {
		// Update motion so as to remove existing workflow
		// based attributes
		motion.setEndFlag(null);
		motion.setLevel("0");
		motion.setTaskReceivedOn(null);
		motion.setWorkflowDetailsId(null);
		motion.setWorkflowStarted("NO");
		motion.setWorkflowStartedOn(null);
		motion.setActor(null);
		motion.setLocalizedActorName("");
		motion.simpleMerge();
	}
	
	public String unclubStandalone(final Long motionBeingProcessed,
			final Long motionBeingClubbed, final String locale) {
		try {
			Motion beingProcessedMotion = Motion.findById(Motion.class, motionBeingProcessed);
			Motion beingClubbedMotion = Motion.findById(Motion.class, motionBeingClubbed);
			// ClubbedEntity clubbedEntityToRemove=null;

			/**** If processed question's number is less than clubbed question's number
			 * then clubbed question is removed from the clubbing of processed question
			 * ,clubbed question's parent is set to null ,new clubbing of processed 
			 * question is set,their position is updated****/
			if(beingProcessedMotion.getNumber().compareTo(beingClubbedMotion.getNumber()) < 0){
				List<ClubbedEntity> oldClubbedMotions = beingProcessedMotion.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
				Integer position = 0;
				boolean found = false;
				for(ClubbedEntity i : oldClubbedMotions){
					if(! i.getMotion().getId().equals(beingClubbedMotion.getId())){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedMotions.add(i);
						}else{
							newClubbedMotions.add(i);                		
						}
					}else{
						found = true;
						position = i.getPosition();
						// clubbedEntityToRemove=i;
					}
				}
				if(!newClubbedMotions.isEmpty()){
					beingProcessedMotion.setClubbedEntities(newClubbedMotions);
				}else{
					beingProcessedMotion.setClubbedEntities(null);
				}            
				beingProcessedMotion.simpleMerge();
				beingClubbedMotion.setParent(null);
				Status newstatus = Status.findByFieldName(Status.class,"type",ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, locale);
				
				if(!beingClubbedMotion.getInternalStatus().getType().startsWith("motion_final")){
					Status submitStatus=Status.findByType(ApplicationConstants.STANDALONE_SUBMIT, locale);
					beingClubbedMotion.setInternalStatus(newstatus);
					beingClubbedMotion.setRecommendationStatus(newstatus);
					beingClubbedMotion.setStatus(submitStatus);
				}
				beingClubbedMotion.simpleMerge();
			}else if(beingProcessedMotion.getNumber().compareTo(beingClubbedMotion.getNumber()) > 0){
				
				List<ClubbedEntity> oldClubbedMotions = beingClubbedMotion.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions = new ArrayList<ClubbedEntity>();
				
				Integer position = 0;
				boolean found = false;
				
				for(ClubbedEntity i : oldClubbedMotions){
					if(! i.getMotion().getId().equals(beingProcessedMotion.getId())){
						if(found){
							i.setPosition(position);
							position++;
							i.merge();
							newClubbedMotions.add(i);
						}else{
							newClubbedMotions.add(i);                		
						}
					}else{
						found = true;
						position = i.getPosition();
					}
				}
				beingClubbedMotion.setClubbedEntities(newClubbedMotions);
				beingClubbedMotion.simpleMerge();

				beingProcessedMotion.setParent(null);
				Status newstatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.STANDALONE_SYSTEM_ASSISTANT_PROCESSED, locale);
				
				if(!beingProcessedMotion.getInternalStatus().getType().startsWith("motion_final")){
					Status submitStatus=Status.findByType(ApplicationConstants.MOTION_SUBMIT, locale);
					beingProcessedMotion.setInternalStatus(newstatus);
					beingProcessedMotion.setRecommendationStatus(newstatus);
					beingProcessedMotion.setStatus(submitStatus);
				}
				beingProcessedMotion.simpleMerge();
			}
		} catch (Exception e) {
			logger.error("FAILED",e);
		}		
		return "SUCCESS";
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final CutMotion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.main_title as subject,q.revised_main_title as revisedSubject,"+
				"  q.notice_content as details,q.revised_notice_content as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM cutmotions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.internalstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.id<>"+motion.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')" +
				"  AND s.id=" + motion.getSession().getId();

		
		String filter = addFilterCutMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.main_title,q.notice_content,q.revised_main_title,q.revised_notice_content) "+
					"against('"+param+"' in natural language mode)"+
					")||q.main_title LIKE '"+param+"%'||q.notice_content LIKE '"+param+"%'||q.revised_notice_content LIKE '"+param+"%'||q.revised_main_title LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.main_title,q.notice_content,q.revised_main_title,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.main_title,q.notice_content,q.revised_main_title,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.main_title,q.notice_content,q.revised_main_title,q.revised_notice_content) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setTitle(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setTitle(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setTitle(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setStatusType(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					motionSearchVO.setChartAnsweringDate(o[16].toString());
				}
				addClasification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final EventMotion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.event_title as subject,q.revised_event_title as revisedSubject,"+
				"  q.description as details,q.revised_description as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  '-' as ministry,'-' as department,'-' as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM eventmotions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.internalstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  WHERE q.id<>"+motion.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.EVENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')" +
				"  AND s.id=" + motion.getSession().getId();

		
		String filter = addFilterEventMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.event_title,q.description,q.revised_event_title,q.revised_description) "+
					"against('"+param+"' in natural language mode)"+
					")||q.event_title LIKE '"+param+"%'||q.description LIKE '"+param+"%'||q.revised_description LIKE '"+param+"%'||q.revised_event_title LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.event_title,q.description,q.revised_event_title,q.revised_description) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND  match(q.event_title,q.description,q.revised_event_title,q.revised_description) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND  match(q.event_title,q.description,q.revised_event_title,q.revised_description) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setTitle(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setTitle(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setTitle(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setStatusType(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					motionSearchVO.setChartAnsweringDate(o[16].toString());
				}
				addClasification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final DiscussionMotion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.notice_content as details,q.revised_notice_content as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  (SELECT " +
				" 		GROUP_CONCAT(mn.name) "+
				"   FROM ministries mn " + 
				"   INNER JOIN discussionmotion_ministries dm ON(dm.ministry_id=mn.id)" +
				"   WHERE dm.discussionmotion_id=q.id) as ministry,'-' as department," +
				"   (SELECT " +
				"     GROUP_CONCAT(sd.name)" +
				"    FROM subdepartments sd" + 
				"    INNER JOIN discussionmotion_subdepartments dm ON(dm.subdepartment_id=sd.id)" +
				"    WHERE dm.discussionmotion_id=q.id) as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM discussionmotion as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.internalstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  WHERE q.id<>"+motion.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.DISCUSSIONMOTION_SYSTEM_ASSISTANT_PROCESSED+"')" +
				"  AND s.id=" + motion.getSession().getId();

		
		String filter = addFilterDiscussionMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '"+param+"%'||q.notice_content LIKE '"+param+"%'||q.revised_notice_content LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND  match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND  match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setTitle(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setTitle(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setTitle(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setSubDepartment(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setStatusType(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					motionSearchVO.setChartAnsweringDate(o[16].toString());
				}
				addClasification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final AdjournmentMotion motion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		
		String adjourningDate = FormaterUtil.formatDateToString(motion.getAdjourningDate(), ApplicationConstants.DB_DATEFORMAT);
		
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";		

		/**** Condition 1 :must not contain processed motion ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.notice_content as details,q.revised_notice_content as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  mi.name as ministry,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM adjournmentmotions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.internalstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.id<>"+motion.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.ADJOURNMENTMOTION_SUBMIT+"')" +
				"  AND s.id=" + motion.getSession().getId() +
				"  AND q.adjourning_date='" + adjourningDate + "'"; 

		
		String filter = addFilterAdjournmentMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '"+param+"%'||q.notice_content LIKE '"+param+"%'||q.revised_notice_content LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setSubDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setStatusType(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setChartAnsweringDate(o[15].toString());
				}
				addClasification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	/**** Free Text Search Begins ****/
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final SpecialMentionNotice notice,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		
		String specialMentionNoticeDate = FormaterUtil.formatDateToString(notice.getSpecialMentionNoticeDate(), ApplicationConstants.DB_DATEFORMAT);
		
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";		

		/**** Condition 1 :must not contain processed motion ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.notice_content as details,q.revised_notice_content as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,'0' as groupnumber,"+
				"  mi.name as ministry,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM specialmentionnotice as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.internalstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.id<>"+notice.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.SPECIALMENTIONNOTICE_SUBMIT+"')" +
				"  AND s.id=" + notice.getSession().getId() +
				"  AND q.specialMentionNotice_date='" +specialMentionNoticeDate + "'"; 

		
		String filter = addFilterSpecialMentionNotice(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '"+param+"%'||q.notice_content LIKE '"+param+"%'||q.revised_notice_content LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setFormattedGroup(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[10].toString())));
					motionSearchVO.setGroup(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setMinistry(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setSubDepartment(o[12].toString());
				}
				if(o[13]!=null){
					motionSearchVO.setStatusType(o[13].toString());
				}
				if(o[14]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[14].toString());
				}
				if(o[15]!=null){
					motionSearchVO.setChartAnsweringDate(o[15].toString());
				}
				addClasification(motionSearchVO, notice);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(final String param, final BillAmendmentMotion billAmendmentMotion,
			final Integer start,final Integer noofRecords,
			final String locale,final Map<String, String[]> requestMap) {
		
		String defaultAmendedSectionLanguage = billAmendmentMotion.getSession().getParameter(billAmendmentMotion.getAmendedBill().getType().getType()+"_defaultTitleLanguage");
		String defaultAmendedSectionNumber = billAmendmentMotion.findDefaultAmendedSectionNumber();
		
		/**** Select Clause ****/
		/**** Condition 1 : must not contain processed motion ****/
		/**** Condition 2 : parent must be null ****/
		/**** Condition 3 : must be from same session ****/
		/**** Condition 4 : should be atleast assistant processed ****/
		/**** Condition 5 : should not be rejected or in flow of put up for rejection ****/			
		String selectQuery = "SELECT bam.id as id, bam.number as number," +						
				" (CASE WHEN (lang.id=sectionAmendment.language_id) THEN sectionAmendment.amending_content ELSE NULL END) AS amendingContent, " +
				" (CASE WHEN (lang.id=revisedSectionAmendment.language_id) THEN revisedSectionAmendment.amending_content ELSE NULL END) AS amendingRevisedContent," +
				" lang.type AS languageType," +			
				" st.name as status, dt.name as deviceType," +	
				" st.type as statusType, s.sessiontype_id as sessionType,s.session_year as sessionYear," +	
				" CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName," +
				" bam.submission_date as submissionDate" +
				" FROM billamendmentmotions as bam" +	
				" LEFT JOIN bills as b ON(bam.amendedbill_id=b.id)" +
				" LEFT JOIN housetypes as ht ON(bam.housetype_id=ht.id)" +
				" LEFT JOIN sessions as s ON(bam.session_id=s.id)" +
				" LEFT JOIN members as m ON(bam.member_id=m.id)" +
				" LEFT JOIN titles as t ON(m.title_id=t.id)" +
				" LEFT JOIN status as st ON(bam.internalstatus_id=st.id)" +
				" LEFT JOIN devicetypes as dt ON(bam.devicetype_id=dt.id)" +
				" LEFT JOIN `billamendmentmotions_sectionamendments` AS bs ON (bs.billamendmentmotion_id = bam.id)" +
				" LEFT JOIN `billamendmentmotions_revisedsectionamendments` AS brs ON (brs.billamendmentmotion_id = bam.id)" +
				" LEFT JOIN `sectionamendments` AS sectionAmendment ON (sectionAmendment.id = bs.sectionamendment_id)" +
				" LEFT JOIN `sectionamendments` AS revisedSectionAmendment ON (revisedSectionAmendment.id = brs.revised_sectionamendment_id)" +
				" LEFT JOIN `sectionamendments` AS def_sectionAmendment ON (def_sectionAmendment.id = bs.sectionamendment_id AND def_sectionAmendment.language_id=(SELECT id FROM languages WHERE type='"+defaultAmendedSectionLanguage+"'))" +
				" LEFT JOIN languages AS lang ON (lang.id = sectionAmendment.language_id OR lang.id = revisedSectionAmendment.language_id)" + 
				" WHERE" +						
				" bam.id <> " + billAmendmentMotion.getId() + " AND bam.parent is NULL" +
				" AND b.id="+billAmendmentMotion.getAmendedBill().getId().toString() +
				" AND def_sectionAmendment.section_number='"+defaultAmendedSectionNumber+"'" +
				" AND s.id="+billAmendmentMotion.getSession().getId().toString() +
				" AND bam.devicetype_id="+billAmendmentMotion.getType().getId().toString() +
				" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED+"')" + 
				" AND st.type<>'"+ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_REJECTION + "'" +
				" AND st.type<>'"+ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECTION + "'";

		/**** filter query ****/
		String filterQuery=addFilterForBillAmendmentMotion(billAmendmentMotion, requestMap);

		/**** fulltext query ****/
		String searchQuery=null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery=" AND ((match(sectionAmendment.amending_content) against('"+param+"' in natural language mode))" + 
					" || (match(revisedSectionAmendment.amending_content) against('"+param+"' in natural language mode))" + 
					" || sectionAmendment.amending_content LIKE '"+param+"%' || revisedSectionAmendment.amending_content LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters=param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND ((match(sectionAmendment.amending_content) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(revisedSectionAmendment.amending_content) against('"+buffer.toString()+"' in boolean mode))";				
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters=param.split("-");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND ((match(sectionAmendment.amending_content) against('"+buffer.toString()+"' in boolean mode))" + 
					" || (match(revisedSectionAmendment.amending_content) against('"+buffer.toString()+"' in boolean mode))";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND ((match(sectionAmendment.amending_content) against('"+param+"' in boolean mode))" + 
					" || (match(revisedSectionAmendment.amending_content) against('"+param+"' in boolean mode))";
		}	

		/**** Order By Query ****/
		String orderByQuery=" ORDER BY st.priority "+ApplicationConstants.ASC+" ,bam.number "+ApplicationConstants.ASC+
				", bam.submission_date "+ApplicationConstants.ASC+", bam.id "+ApplicationConstants.ASC;

		/**** Final Query ****/
		String query=selectQuery+filterQuery+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.amendingContent, "+
				" rs.amendingRevisedContent,rs.languageType,rs.status,rs.deviceType," + 
				" rs.statusType, rs.sessionType, rs.sessionYear, rs.memberName," + 
				" rs.submissionDate FROM ("+query+") as rs LIMIT "+start+","+noofRecords;

		List resultList=this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs=new ArrayList<MotionSearchVO>();
		String motionId = "";
		MotionSearchVO motionSearchVO = new MotionSearchVO();
		if(resultList != null){
			for(Object i : resultList){
				Object[] o = (Object[]) i;		
				if(!motionId.equals(o[0].toString())) {
					if(!motionSearchVOs.isEmpty()) {
						addClasification(motionSearchVO,billAmendmentMotion);
						motionSearchVO = new MotionSearchVO();
					}
					motionSearchVOs.add(motionSearchVO);
					if(o[0] != null){
						motionSearchVO.setId(Long.parseLong(o[0].toString()));
					}
					if(o[1] != null){
						motionSearchVO.setNumber(o[1].toString());
					}
					if(o[5] != null){
						motionSearchVO.setStatus(o[5].toString());
					}
					if(o[6] != null){
						motionSearchVO.setDeviceType(o[6].toString());
					}					
					if(o[7] != null){
						motionSearchVO.setStatusType(o[7].toString());
					}					
					if(o[8] != null){
						Long sessionTypeId = new Long(o[8].toString());
						SessionType sessionType = SessionType.findById(SessionType.class, sessionTypeId);
						if(sessionType != null){
							motionSearchVO.setSessionType(sessionType.getSessionType());
						}
					}					
					if(o[9] != null){
						motionSearchVO.setSessionYear(o[9].toString());
					}	
					if(o[10] != null){
						motionSearchVO.setFormattedPrimaryMember(o[10].toString());
					}
					if(o[11] != null){	
						Date submissionDate = FormaterUtil.formatStringToDate(o[11].toString(), "yyyy-MM-dd HH:mm:ss");
						String formattedSubmissionDate = FormaterUtil.formatDateToString(submissionDate, "dd-MM-yyyy HH:mm:ss", billAmendmentMotion.getLocale());
						motionSearchVO.setSubmissionDate(formattedSubmissionDate);
					}
					if(o[2] != null){
						motionSearchVO.setContent(higlightText(o[2].toString(), param));
					}
					if(o[3] != null){
						motionSearchVO.setRevisedContent(higlightText(o[3].toString(), param));
					}					
					motionId = o[0].toString();
				} else {
					if(o[2] != null){
						motionSearchVO.setContent(higlightText(o[2].toString(), param));
					}
					if(o[3] != null){
						motionSearchVO.setRevisedContent(higlightText(o[3].toString(), param));
					}
				}											
			}
		}		
		if(!motionSearchVOs.isEmpty()) {
			addClasification(motionSearchVOs.get(motionSearchVOs.size()-1),billAmendmentMotion);
		}			
		//return only required number of records
		List<MotionSearchVO> resultMotionSearchVOs = new ArrayList<MotionSearchVO>();
		for(int i=start; i<motionSearchVOs.size(); i++) {
			if(i<noofRecords) {
				resultMotionSearchVOs.add(motionSearchVOs.get(i));
			} else {
				break;
			}
		}
		return resultMotionSearchVOs;
	}
	
	private String addQuestionFilter(Map<String, String[]> requestMap){
		
		String[] strDeviceTypes = requestMap.get("deviceType");
		StringBuffer filter = new StringBuffer("");
		
		if(strDeviceTypes != null && strDeviceTypes.length > 0){
			if(strDeviceTypes[0] != null && !strDeviceTypes[0].isEmpty()){
				DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceTypes[0]));
				
				if(deviceType != null){
					if(deviceType.getType().equals(ApplicationConstants.STARRED_QUESTION)) {
						filter.append(addFilterForStarredQuestion(requestMap));
					} else if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
						filter.append(addFilterForUnStarredQuestion(requestMap));
					} else if(deviceType.getType().equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
						filter.append(addFilterForShortNoticeQuestion(requestMap));
					} else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
						filter.append(addFilterForHDQ(requestMap));
					}
				}
			}
		}
		return filter.toString();
	}


	@SuppressWarnings("rawtypes")
	public List<MotionSearchVO> fullTextSearchClubbing(String param, RulesSuspensionMotion motion, int start,
			int noOfRecords, String locale, Map<String, String[]> requestMap) {
		String ruleSuspensionDate = FormaterUtil.formatDateToString(motion.getRuleSuspensionDate(), ApplicationConstants.DB_DATEFORMAT);
		
		StringBuffer deviceTypeQuery = new StringBuffer();
		String orderByQuery="";		

		/**** Condition 1 :must not contain processed motion ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.notice_content as details,q.revised_notice_content as revisedDetails,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,"+
				"  st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, '0' as answeringDate"+
				"  FROM rules_suspension_motions as q "+
				"  LEFT JOIN housetypes as ht ON(q.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(q.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(q.internalstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(q.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(q.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  WHERE q.id<>"+motion.getId()+" AND q.parent is NULL " +
				"  AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT+"')" +
				"  AND s.id=" + motion.getSession().getId() +
				"  AND q.rule_suspension_date='" + ruleSuspensionDate + "'"; 

		
		String filter = addFilterRulesSuspensionMotion(requestMap);

		/**** full text query ****/
		String searchQuery = null;
		if(!param.contains("+")&&!param.contains("-")){
			searchQuery = " AND (( match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in natural language mode)"+
					")||q.subject LIKE '"+param+"%'||q.notice_content LIKE '"+param+"%'||q.revised_notice_content LIKE '"+param+"%'||q.revised_subject LIKE '"+param+"%')";
		}else if(param.contains("+")&&!param.contains("-")){
			String[] parameters = param.split("\\+");
			StringBuffer buffer=new StringBuffer();
			for(String i:parameters){
				buffer.append("+"+i+" ");
			}
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(!param.contains("+")&&param.contains("-")){
			String[] parameters = param.split("-");
			StringBuffer buffer = new StringBuffer();
			for(String i:parameters){
				buffer.append(i+" "+"-");
			}
			buffer.deleteCharAt(buffer.length()-1);
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+buffer.toString()+"' in boolean  mode)";
		}else if(param.contains("+")||param.contains("-")){
			searchQuery=" AND match(q.subject,q.notice_content,q.revised_subject,q.revised_notice_content) "+
					"against('"+param+"' in boolean  mode)";
		}		
		/**** Final Query ****/
		String query=selectQuery+deviceTypeQuery.toString()+filter+searchQuery+orderByQuery;
		String finalQuery="SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.details, "+
				" rs.revisedDetails,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.statustype,rs.memberName,rs.answeringDate FROM ("+query+") as rs LIMIT "+start+","+noOfRecords;

		List results = this.em().createNativeQuery(finalQuery).getResultList();
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				MotionSearchVO motionSearchVO = new MotionSearchVO();
				if(o[0]!=null){
					motionSearchVO.setId(Long.parseLong(o[0].toString()));
				}
				if(o[1]!=null){
					motionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						motionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							motionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						motionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						motionSearchVO.setNoticeContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						motionSearchVO.setNoticeContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					motionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					motionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					motionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					motionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					motionSearchVO.setStatusType(o[10].toString());
				}
				if(o[11]!=null){
					motionSearchVO.setFormattedPrimaryMember(o[11].toString());
				}
				if(o[12]!=null){
					motionSearchVO.setChartAnsweringDate(o[12].toString());
				}
				addClassification(motionSearchVO, motion);
				motionSearchVOs.add(motionSearchVO);
			}
		}
		return motionSearchVOs;
	}


	private String addFilterRulesSuspensionMotion(Map<String, String[]> requestMap) {
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
		if(requestMap.get("status")!=null){
			String status=requestMap.get("status")[0];
			if((!status.isEmpty())&&(!status.equals("-"))){
				if(status.equals(ApplicationConstants.UNPROCESSED_FILTER)){
					buffer.append(" AND st.type=(SELECT type FROM status as sst WHERE sst.type='"+ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
				}else if(status.equals(ApplicationConstants.PENDING_FILTER)){
					buffer.append(" AND st.priority>(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED+"')");
					buffer.append(" AND st.priority<(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION+"')");
				}else if(status.equals(ApplicationConstants.APPROVED_FILTER)){
					buffer.append(" AND st.priority>=(SELECT priority FROM status as sst WHERE sst.type='"+ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION+"')");
				}
			}
		}			
		return buffer.toString();
	}
	
	public List<DeviceSearchVO> fullTextSearchForSearchFacility(final String whichDevice,
			final String param,
			final Integer start,
			final Integer noofRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		if(whichDevice!=null) {
			if(whichDevice.equals(ApplicationConstants.DEVICE_QUESTIONS)) {
				return this.fullTextSearchQuestionForSearchFacility(param, start, noofRecords, locale, requestMap);
				
			} else if(whichDevice.equals(ApplicationConstants.DEVICE_CUTMOTIONS)) {
				return this.fullTextSearchCutMotionForSearchFacility(param, start, noofRecords, locale, requestMap);
				
			} else {
				return new ArrayList<DeviceSearchVO>();
			}			
		} else {
			return new ArrayList<DeviceSearchVO>();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<DeviceSearchVO> fullTextSearchQuestionForSearchFacility(final String param,
			final Integer start,
			final Integer noofRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY q.number ASC, s.start_date DESC, dt.id ASC";

		/**** Condition 1 :must not contain processed question ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT q.id as id,q.number as number,"+
				"  q.subject as subject,q.revised_subject as revisedSubject,"+
				"  q.question_text as questionText,q.revised_question_text as revisedQuestionText,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType ,g.number as groupnumber,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName, qd1.answering_date as answeringDate,"+
				"  q.localized_actor_name as actor," +
				"  q.ballotstatus_id as ballotStatus," +
				"  q.parent as parent," +
				"  CASE WHEN q.created_by LIKE '%typist%' THEN ''"+
				" 		ELSE '*' "+
				"  END as onlineStatus" +
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
				"  LEFT JOIN question_dates as qd1 ON(q.chart_answering_date=qd1.id) "+
				"  LEFT JOIN ministries as mi ON(q.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(q.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(q.subdepartment_id=sd.id) "+
				"  WHERE q.locale='"+locale+"'"+
				"  AND st.type NOT IN('question_incomplete','question_complete')";
		
		StringBuffer strDefaultFilter = new StringBuffer();

		StringBuffer filter = new StringBuffer("");
		String[] strDevices = requestMap.get("deviceType");
		
		if(strDevices != null && strDevices.length > 0){
			filter.append(addQuestionFilter(requestMap));
		}else{
			CustomParameter csptSearchDefault = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.QUESTION_SEARCH_DEFAULT, "");
			if(csptSearchDefault != null && csptSearchDefault.getValue() != null 
					&& !csptSearchDefault.getValue().isEmpty() && csptSearchDefault.getValue().equalsIgnoreCase("yes")){
				
				CustomParameter csptSearchDefaultDevices = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.QUESTION_SEARCH_DEFAULT_DEVICES, "");
				if(csptSearchDefaultDevices != null && csptSearchDefaultDevices.getValue() != null
						&& !csptSearchDefaultDevices.getValue().isEmpty()){
					List<DeviceType> devices = DeviceType.findDevicesContainedIn(csptSearchDefaultDevices.getValue(), locale);
					for(int i = 0; i < devices.size(); i++){
						if(i == 0){
							strDefaultFilter.append(" AND (dt.id=" + devices.get(i).getId());
						}else{
							strDefaultFilter.append(" OR dt.id=" + devices.get(i).getId());
						}
					}
					strDefaultFilter.append(") "); 
				}
			}
			filter.append(strDefaultFilter.toString());
			filter.append(addFilter(requestMap));
		}
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.QUESTION_SEARCH_USE_CURRENT_SESSION, "");
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
				searchQuery=" AND (( match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
						"against('"+param+"' in natural language mode)"+
						")||q.subject LIKE '%"+param+"%'||q.question_text LIKE '%"+param+"%'||q.revised_subject LIKE '%"+param+"%'||q.revised_subject LIKE '%"+param+"%' ||q.answer LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(q.subject,q.question_text,q.revised_subject,q.revised_question_text,q.answer) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.subject,rs.revisedSubject,rs.questionText, "+
				" rs.revisedQuestionText,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.groupnumber,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.answeringDate,rs.actor,rs.ballotStatus,rs.parent,rs.onlineStatus FROM (" + query + ") as rs LIMIT " + start + "," + noofRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<DeviceSearchVO> questionSearchVOs=new ArrayList<DeviceSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				DeviceSearchVO questionSearchVO=new DeviceSearchVO();
				if(o[0]!=null){
					questionSearchVO.setId(Long.parseLong(o[0].toString()));
					if(requestMap.get("number") != null){
						Map<String, String[]> parameters = new HashMap<String, String[]>();
						parameters.put("locale", new String[]{locale.toString()});
						parameters.put("questionId", new String[]{o[0].toString()});
						List questionRevisions = org.mkcl.els.domain.Query.findReport("QIS_LATEST_REVISIONS", parameters);
						List<MasterVO> revisions = new ArrayList<MasterVO>();
						for(Object j : questionRevisions){
							Object[] obj=(Object[]) j;
							MasterVO masterVO = new MasterVO();
							//UsergroupType
							masterVO.setName(obj[0].toString());
							//Users Name
							masterVO.setValue(obj[1].toString());
							//Internal Status
							masterVO.setDisplayName(obj[2].toString());
							//Remarks
							if(obj[4] != null){
								masterVO.setType(obj[4].toString());
							}
							revisions.add(masterVO);
						}
						questionSearchVO.setRevisions(revisions);
					}
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
						questionSearchVO.setDeviceContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							questionSearchVO.setDeviceContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						questionSearchVO.setDeviceContent(higlightText(o[4].toString(),param));
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
				if(o[15]!=null){
					questionSearchVO.setFormattedPrimaryMember(o[15].toString());
				}
				if(o[16]!=null){
					questionSearchVO.setChartAnsweringDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(o[16].toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
				}
				if(o[17]!=null){
					questionSearchVO.setActor(o[17].toString());
				}
				if(o[18]!=null){
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("questionId", new String[]{o[0].toString()});
					List ballotDate = org.mkcl.els.domain.Query.findReport("QIS_GET_BALLOTDATE", parameters);
					if(ballotDate != null && !ballotDate.isEmpty()){
						questionSearchVO.setDiscussionDate(FormaterUtil.formatDateToString(FormaterUtil.formatStringToDate(ballotDate.get(0).toString(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.SERVER_DATEFORMAT, locale));
					}
				}
				if(o[19]!= null){
					Question question = Question.findById(Question.class, Long.parseLong(o[19].toString()));
					questionSearchVO.setFormattedParentNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(question.getNumber()));
					if(o[2]!=null){
						questionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
					if(o[4]!=null){
						questionSearchVO.setDeviceContent(higlightText(o[4].toString(),param));
					}
				}else{
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("questionId", new String[]{o[0].toString()});
					List clubbedNumbers = org.mkcl.els.domain.Query.findReport("QIS_GET_CLUBBEDNUMBERS", parameters);
					if(clubbedNumbers != null && !clubbedNumbers.isEmpty() && clubbedNumbers.get(0) != null){
						questionSearchVO.setFormattedClubbedNumbers(clubbedNumbers.get(0).toString());
					}
				}
				if(o[20]!= null){
					questionSearchVO.setOnlineStatus(o[20].toString());
				}
				
				questionSearchVOs.add(questionSearchVO);
			}
		}
		return questionSearchVOs;		
	}
	
	@SuppressWarnings("rawtypes")
	public List<DeviceSearchVO> fullTextSearchCutMotionForSearchFacility(final String param,
			final Integer start,
			final Integer noofRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		String orderByQuery=" ORDER BY cm.number ASC, s.start_date DESC, dt.id ASC";

		/**** Condition 1 :must not contain processed cutmotion ****/
		/**** Condition 2 :parent must be null ****/
		String selectQuery="SELECT cm.id as id,cm.number as number,"+
				"  cm.main_title as mainTitle,cm.revised_main_title as revisedMainTitle,"+
				"  cm.notice_content as noticeContent,cm.revised_notice_content as revisedNoticeContent,"+
				"  st.name as status,dt.name as deviceType,s.session_year as sessionYear,"+
				"  sety.session_type as sessionType,"+
				"  mi.name as ministry,d.name as department,sd.name as subdepartment,st.type as statustype," +
				"  CONCAT(t.name,' ',m.first_name,' ',m.last_name) as memberName,"+
				"  cm.localized_actor_name as actor," +
				"  cm.parent as parent," +
				"  CASE WHEN cm.created_by LIKE '%typist%' THEN ''"+
				" 		ELSE '*' "+
				"  END as onlineStatus" +
				"  FROM cutmotions as cm "+
				"  LEFT JOIN housetypes as ht ON(cm.housetype_id=ht.id) "+
				"  LEFT JOIN sessions as s ON(cm.session_id=s.id) "+
				"  LEFT JOIN sessiontypes as sety ON(s.sessiontype_id=sety.id) "+
				"  LEFT JOIN status as st ON(cm.recommendationstatus_id=st.id) "+
				"  LEFT JOIN devicetypes as dt ON(cm.devicetype_id=dt.id) "+
				"  LEFT JOIN members as m ON(cm.member_id=m.id) "+
				"  LEFT JOIN titles as t ON(m.title_id=t.id) "+
				"  LEFT JOIN ministries as mi ON(cm.ministry_id=mi.id) "+
				"  LEFT JOIN departments as d ON(cm.department_id=d.id) "+
				"  LEFT JOIN subdepartments as sd ON(cm.subdepartment_id=sd.id) "+
				"  WHERE cm.locale='"+locale+"'"+
				"  AND st.type NOT IN('cutmotion_incomplete','cutmotion_complete')";
		
		StringBuffer filter = new StringBuffer("");		
		filter.append(addFilterCutMotion(requestMap));
		
		String[] strSessionType = requestMap.get("sessionYear");
		String[] strSessionYear = requestMap.get("sessionType");
		
		if(strSessionType == null || (strSessionType != null && strSessionType[0].equals("-")) 
				|| strSessionYear == null || (strSessionYear != null && strSessionYear[0].equals("-"))
				|| (strSessionType == null && strSessionYear == null)){
			CustomParameter csptUseCurrentSession = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_SEARCH_USE_CURRENT_SESSION, "");
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
				searchQuery=" AND (( match(cm.main_title,cm.notice_content,cm.revised_main_title,cm.revised_notice_content,cm.reply) "+
						"against('"+param+"' in natural language mode)"+
						")||cm.main_title LIKE '%"+param+"%'||cm.notice_content LIKE '%"+param+"%'||cm.revised_main_title LIKE '%"+param+"%'||cm.revised_main_title LIKE '%"+param+"%' ||cm.reply LIKE '%"+param+"%')";
			}else if(param.contains("+")&&!param.contains("-")){
				String[] parameters = param.split("\\+");
				StringBuffer buffer = new StringBuffer();
				for(String i : parameters){
					buffer.append("+"+i+" ");
				}
				
				searchQuery =" AND match(cm.main_title,cm.notice_content,cm.revised_main_title,cm.revised_notice_content,cm.reply) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(!param.contains("+")&&param.contains("-")){
				String[] parameters=param.split("-");
				StringBuffer buffer=new StringBuffer();
				for(String i:parameters){
					buffer.append(i+" "+"-");
				}
				buffer.deleteCharAt(buffer.length()-1);
				searchQuery=" AND match(cm.main_title,cm.notice_content,cm.revised_main_title,cm.revised_notice_content,cm.reply) "+
						"against('"+buffer.toString()+"' in boolean  mode)";
			}else if(param.contains("+")||param.contains("-")){
				searchQuery=" AND match(cm.main_title,cm.notice_content,cm.revised_main_title,cm.revised_notice_content,cm.reply) "+
						"against('"+param+"' in boolean  mode)";
			}	
			
			query = selectQuery + filter + searchQuery + orderByQuery;
		}
		/**** Final Query ****/
		String finalQuery = "SELECT rs.id,rs.number,rs.mainTitle,rs.revisedMainTitle,rs.noticeContent, "+
				" rs.revisedNoticeContent,rs.status,rs.deviceType,rs.sessionYear,rs.sessionType,rs.ministry,rs.department,rs.subdepartment,rs.statustype,rs.memberName,rs.actor,rs.parent,rs.onlineStatus FROM (" + query + ") as rs LIMIT " + start + "," + noofRecords;

		List results=this.em().createNativeQuery(finalQuery).getResultList();
		List<DeviceSearchVO> cutmotionSearchVOs=new ArrayList<DeviceSearchVO>();
		if(results!=null){
			for(Object i:results){
				Object[] o=(Object[]) i;
				DeviceSearchVO cutmotionSearchVO=new DeviceSearchVO();
				if(o[0]!=null){
					cutmotionSearchVO.setId(Long.parseLong(o[0].toString()));
					if(requestMap.get("number") != null){
						Map<String, String[]> parameters = new HashMap<String, String[]>();
						parameters.put("locale", new String[]{locale.toString()});
						parameters.put("cutMotionId", new String[]{o[0].toString()});
						List cutmotionRevisions = org.mkcl.els.domain.Query.findReport("CMOIS_LATEST_REVISIONS", parameters);
						List<MasterVO> revisions = new ArrayList<MasterVO>();
						for(Object j : cutmotionRevisions){
							Object[] obj=(Object[]) j;
							MasterVO masterVO = new MasterVO();
							//UsergroupType
							masterVO.setName(obj[0].toString());
							//Users Name
							masterVO.setValue(obj[1].toString());
							//Internal Status
							masterVO.setDisplayName(obj[2].toString());
							//Remarks
							if(obj[4] != null){
								masterVO.setType(obj[4].toString());
							}
							revisions.add(masterVO);
						}
						cutmotionSearchVO.setRevisions(revisions);
					}
				}
				if(o[1]!=null){
					cutmotionSearchVO.setNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[1].toString())));
				}
				if(o[3]!=null){
					if(!o[3].toString().isEmpty()){
						cutmotionSearchVO.setSubject(higlightText(o[3].toString(),param));
					}else{
						if(o[2]!=null){
							cutmotionSearchVO.setSubject(higlightText(o[2].toString(),param));
						}
					}
				}else{
					if(o[2]!=null){
						cutmotionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
				}				
				if(o[5]!=null){
					if(!o[5].toString().isEmpty()){
						cutmotionSearchVO.setDeviceContent(higlightText(o[5].toString(),param));
					}else{
						if(o[4]!=null){
							cutmotionSearchVO.setDeviceContent(higlightText(o[4].toString(),param));
						}
					}
				}else{
					if(o[4]!=null){
						cutmotionSearchVO.setDeviceContent(higlightText(o[4].toString(),param));
					}
				}
				if(o[6]!=null){
					cutmotionSearchVO.setStatus(o[6].toString());
				}
				if(o[7]!=null){
					cutmotionSearchVO.setDeviceType(o[7].toString());
				}
				if(o[8]!=null){
					cutmotionSearchVO.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(Integer.parseInt(o[8].toString())));
				}
				if(o[9]!=null){
					cutmotionSearchVO.setSessionType(o[9].toString());
				}
				if(o[10]!=null){
					cutmotionSearchVO.setMinistry(o[10].toString());
				}
				if(o[11]!=null){
					cutmotionSearchVO.setDepartment(o[11].toString());
				}
				if(o[12]!=null){
					cutmotionSearchVO.setSubDepartment(o[12].toString());
				}
				if(o[13]!=null){
					cutmotionSearchVO.setStatusType(o[13].toString());
				}
				if(o[14]!=null){
					cutmotionSearchVO.setFormattedPrimaryMember(o[14].toString());
				}
				if(o[15]!=null){
					cutmotionSearchVO.setActor(o[15].toString());
				}
				if(o[16]!= null){
					Question cutmotion = Question.findById(Question.class, Long.parseLong(o[16].toString()));
					cutmotionSearchVO.setFormattedParentNumber(FormaterUtil.getNumberFormatterNoGrouping(locale).format(cutmotion.getNumber()));
					if(o[2]!=null){
						cutmotionSearchVO.setSubject(higlightText(o[2].toString(),param));
					}
					if(o[4]!=null){
						cutmotionSearchVO.setDeviceContent(higlightText(o[4].toString(),param));
					}
				}else{
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("cutmotionId", new String[]{o[0].toString()});
					List clubbedNumbers = org.mkcl.els.domain.Query.findReport("CMOIS_GET_CLUBBEDNUMBERS", parameters);
					if(clubbedNumbers != null && !clubbedNumbers.isEmpty() && clubbedNumbers.get(0) != null){
						cutmotionSearchVO.setFormattedClubbedNumbers(clubbedNumbers.get(0).toString());
					}
				}
				if(o[17]!= null){
					cutmotionSearchVO.setOnlineStatus(o[17].toString());
				}
				
				cutmotionSearchVOs.add(cutmotionSearchVO);
			}
		}
		return cutmotionSearchVOs;		
	}
	
}