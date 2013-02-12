/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.GroupRepository.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.repository;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class GroupRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class GroupRepository extends BaseRepository<Group, Long> {

	/**
	 * Find by house type session type year.
	 *
	 * @param houseType the house type
	 * @param sessionType the session type
	 * @param year the year
	 * @return the list
	 */
	public List<Group> findByHouseTypeSessionTypeYear(final HouseType houseType,final SessionType sessionType,final Integer year){
		Search search=new Search();
		search.addFilterEqual("houseType.id",houseType.getId());
		search.addFilterEqual("sessionType.id",sessionType.getId());
		search.addFilterEqual("year",year);
		search.addSort("number",false);
		return this.search(search);
	}

	/**
	 * Find answering dates.
	 *
	 * @param id the id
	 * @return the list
	 */
	public List<String> findAnsweringDates(final Long id) {
		String query="SELECT answering_date,locale FROM question_dates WHERE id="+id+" ORDER BY answering_date asc";
		List dates=this.em().createNativeQuery(query).getResultList();
		List<String> answeringDates=new ArrayList<String>();
		for(Object i:dates){
			Object[] o=(Object[]) i;
			if(o[0]!=null){
				try {
					Date dateDBFormat=FormaterUtil.getDateFormatter(ApplicationConstants.DB_DATEFORMAT,o[1].toString()).parse(o[0].toString());
					String answeringDate=FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT,o[1].toString()).format(dateDBFormat);
					answeringDates.add(answeringDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return answeringDates;
	}

	/**
	 * Find by number house type session type year.
	 *
	 * @param groupNumber the group number
	 * @param houseType the house type
	 * @param sessionType the session type
	 * @param year the year
	 * @return the group
	 */
	public Group findByNumberHouseTypeSessionTypeYear(final Integer groupNumber,
			final HouseType houseType, final SessionType sessionType, final Integer year) {
		Search search=new Search();
		search.addFilterEqual("houseType",houseType);
		search.addFilterEqual("sessionType",sessionType);
		search.addFilterEqual("year",year);
		search.addFilterEqual("number",groupNumber);
		return this.searchUnique(search);
	}

	/**
	 * Find.
	 *
	 * @param ministry the ministry
	 * @param houseType the house type
	 * @param sessionYear the session year
	 * @param sessionType the session type
	 * @param locale the locale
	 * @return the group
	 */
	public Group find(final Ministry ministry, final HouseType houseType,
			final Integer sessionYear, final SessionType sessionType, final String locale) {
		String query="SELECT g FROM Group g JOIN g.ministries m WHERE g.locale='"+locale+"' AND g.houseType.id="+houseType.getId()+" AND "+
		" g.year="+sessionYear+" AND g.sessionType.id="+sessionType.getId()+" AND "+
		" m.id="+ministry.getId();
		return (Group) this.em().createQuery(query).getSingleResult();
	}

	/**
	 * Find all group dates formatted.
	 *
	 * @param houseType the house type
	 * @param sessionType the session type
	 * @param sessionYear the session year
	 * @param locale the locale
	 * @return the list
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<QuestionDatesVO> findAllGroupDatesFormatted(final HouseType houseType,
			final SessionType sessionType, final Integer sessionYear, final String locale) {
		String query="SELECT  g.id,g.number,qd.answering_date,qd.final_submission_date,"+
		" qd.last_receiving_date_from_department,qd.last_sending_date_to_department,"+
		" qd.suchhi_distribution_date,qd.suchhi_printing_date,suchhi_receiving_date,"+
		" yaadi_printing_date,yaadi_receiving_date,speaker_sending_date FROM question_dates AS qd JOIN groups AS g"+
		" WHERE g.id=qd.group_id AND g.sessiontype_id="+sessionType.getId()+" AND g.group_year="+sessionYear+
		" AND g.housetype_id="+houseType.getId()+" AND g.locale='"+locale+"' ORDER BY qd.answering_date ASC";
		List results=this.em().createNativeQuery(query).getResultList();
		List<QuestionDatesVO> questionDatesVOs=new ArrayList<QuestionDatesVO>();
		CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT", "");
		CustomParameter dayOfWeekDateFormat=CustomParameter.findByName(CustomParameter.class,"WEEKDAY_FORMAT", "");
		try {
			if(dbDateFormat!=null&&dayOfWeekDateFormat!=null){
				SimpleDateFormat dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale);
				SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);
				SimpleDateFormat dayOfWeekFormat=FormaterUtil.getDateFormatter(dayOfWeekDateFormat.getValue(), locale);
				NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
				for(Object i:results){
					Object[] entry=(Object[]) i;
					QuestionDatesVO questionDatesVO=new QuestionDatesVO();
					if(entry[0]!=null){
						questionDatesVO.setGroupId(Long.parseLong(entry[0].toString()));
					}
					if(entry[1]!=null){
						int number=Integer.parseInt(entry[1].toString());
						questionDatesVO.setGroup(numberFormat.format(number));
						questionDatesVO.setRowId(number);
					}if(entry[2]!=null){
						Date date=dbFormat.parse(entry[2].toString());
						questionDatesVO.setAnsweringDate(format.format(date));
						questionDatesVO.setDayOfWeek(FormaterUtil.getDayInMarathi(dayOfWeekFormat.format(date), locale));
					}if(entry[3]!=null){
						Date date=dbFormat.parse(entry[3].toString());
						questionDatesVO.setFinalSubmissionDate(format.format(date));
					}if(entry[4]!=null){
						Date date=dbFormat.parse(entry[4].toString());
						questionDatesVO.setLastReceivingDateFromDepartment(format.format(date));
					}if(entry[5]!=null){
						Date date=dbFormat.parse(entry[5].toString());
						questionDatesVO.setLastSendingDateToDepartment(format.format(date));
					}if(entry[6]!=null){
						Date date=dbFormat.parse(entry[6].toString());
						questionDatesVO.setSuchhiDistributionDate(format.format(date));
					}if(entry[7]!=null){
						Date date=dbFormat.parse(entry[7].toString());
						questionDatesVO.setSuchhiPrintingDate(format.format(date));
					}if(entry[8]!=null){
						Date date=dbFormat.parse(entry[8].toString());
						questionDatesVO.setSuchhiReceivingDate(format.format(date));
					}if(entry[9]!=null){
						Date date=dbFormat.parse(entry[9].toString());
						questionDatesVO.setYaadiPrintingDate(format.format(date));
					}if(entry[10]!=null){
						Date date=dbFormat.parse(entry[10].toString());
						questionDatesVO.setYaadiReceivingDate(format.format(date));
					}if(entry[11]!=null){
						Date date=dbFormat.parse(entry[11].toString());
						questionDatesVO.setSpeakerSendingDate(format.format(date));
					}
					questionDatesVOs.add(questionDatesVO);
				}
			}else{
				logger.error("**** Custom Parameter 'DB_DATEFORMAT and WEEKDAY_FORMAT' not set ****");
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return questionDatesVOs;
		}
		catch (ParseException e) {
			e.printStackTrace();
			return questionDatesVOs;
		}
		return questionDatesVOs;

	}

	@SuppressWarnings("unchecked")
	public List<MasterVO> findQuestionDateByGroup(final HouseType houseType,
			final SessionType sessionType, final Integer sessionYear, final int groupNumber,
			final String locale) {
		String query="SELECT qd FROM Group g JOIN g.questionDates qd "+
		" WHERE g.houseType.id="+houseType.getId()+" AND g.sessionType.id="+sessionType.getId()+
		" AND g.year="+sessionYear+" AND g.number="+groupNumber+" AND g.locale='"+locale+"'";
		List<QuestionDates> questionDates=this.em().createQuery(query).getResultList();
		List<MasterVO> dates=new ArrayList<MasterVO>();
		SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);
		for(QuestionDates i:questionDates){
			if(i.getAnsweringDate()!=null){
				MasterVO masterVO=new MasterVO(i.getId(),format.format(i.getAnsweringDate()));
				dates.add(masterVO);
			}
		}
		return dates;
	}    

	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesByName(final Long groupid) {
		String query="SELECT m FROM Group g JOIN g.ministries m WHERE "
			+" g.id="+groupid+" ORDER BY m.name "+ApplicationConstants.ASC;
		return this.em().createQuery(query).getResultList();
	}

	public Group find(final Session session, 
			final Date answeringDate,
			final String  locale) {
		Search search = new Search();
		search.addFilterEqual("houseType", session.getHouse().getType());
		search.addFilterEqual("sessionType", session.getType());
		search.addFilterEqual("year", session.getYear());
		search.addFilterEqual("questionDates.answeringDate", answeringDate);
		search.addFilterEqual("locale", locale);
		return this.searchUnique(search);
	}

	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesByPriority(Long groupid) {
		String query="SELECT m FROM MemberMinister mm RIGHT JOIN mm.ministry m"+
		" WHERE m.id IN (SELECT mi.id FROM Group g JOIN g.ministries mi WHERE g.id="+groupid+") AND m.isExpired=false ORDER BY mm.priority";
		return this.em().createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesByPriority(Group group) {
		String query="SELECT m FROM MemberMinister mm RIGHT JOIN mm.ministry m"+
		" WHERE m.id IN (SELECT mi.id FROM Group g JOIN g.ministries mi WHERE g.id="+group.getId()+") AND m.isExpired=false ORDER BY mm.priority";
		return this.em().createQuery(query).getResultList();
	}
	
	public List<Ministry> findMinistriesInGroupsForSessionExcludingGivenGroup(HouseType houseType, SessionType sessionType, Integer sessionYear, Integer groupNumber, String locale) {
		List<Ministry> ministries = new ArrayList<Ministry>();
		
		if(houseType != null && sessionType != null && sessionYear != null && groupNumber != null && locale != null) {
			if(!locale.isEmpty()) {
				String query="SELECT g.ministries FROM Group g "+
						" WHERE g.houseType.id="+houseType.getId()+" AND g.sessionType.id="+sessionType.getId()+
						" AND g.year="+sessionYear+" AND g.number!="+groupNumber+" AND g.locale='"+locale+"'";
				
				ministries = this.em().createQuery(query).getResultList();
			}
		}
		
		return ministries;
	}
	
	public List<Ministry> findMinistriesInGroupsForSession(HouseType houseType, SessionType sessionType, Integer sessionYear, String locale) {
		List<Ministry> ministries = new ArrayList<Ministry>();
		
		if(houseType != null && sessionType != null && sessionYear != null && locale != null) {
			if(!locale.isEmpty()) {
				String query="SELECT g.ministries FROM Group g "+
						" WHERE g.houseType.id="+houseType.getId()+" AND g.sessionType.id="+sessionType.getId()+
						" AND g.year="+sessionYear+" AND g.locale='"+locale+"'";
				
				ministries = this.em().createQuery(query).getResultList();
			}
		}
		
		return ministries;
	}
	
	public List<Integer> findGroupNumbersForSessionExcludingGivenGroup(HouseType houseType, SessionType sessionType, Integer sessionYear, Integer groupNumber, String locale) {
		List<Integer> groupNumbers = new ArrayList<Integer>();
		
		if(houseType != null && sessionType != null && sessionYear != null && groupNumber != null && locale != null) {
			if(!locale.isEmpty()) {
				String query="SELECT g.number FROM Group g "+
						" WHERE g.houseType.id="+houseType.getId()+" AND g.sessionType.id="+sessionType.getId()+
						" AND g.year="+sessionYear+" AND g.number!="+groupNumber+" AND g.locale='"+locale+"'";
				
				groupNumbers = this.em().createQuery(query).getResultList();
			}
		}
		
		return groupNumbers;
	}
	
	public List<Integer> findGroupNumbersForSession(HouseType houseType, SessionType sessionType, Integer sessionYear, String locale) {
		List<Integer> groupNumbers = new ArrayList<Integer>();
		
		if(houseType != null && sessionType != null && sessionYear != null && locale != null) {
			if(!locale.isEmpty()) {
				String query="SELECT g.number FROM Group g "+
						" WHERE g.houseType.id="+houseType.getId()+" AND g.sessionType.id="+sessionType.getId()+
						" AND g.year="+sessionYear+" AND g.locale='"+locale+"'";
				
				groupNumbers = this.em().createQuery(query).getResultList();
			}
		}
		
		return groupNumbers;
	}
}
