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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SubDepartment;
import org.springframework.stereotype.Repository;


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
	 * @throws ELSException 
	 */
	public List<Group> findByHouseTypeSessionTypeYear(final HouseType houseType,final SessionType sessionType,final Integer year) throws ELSException{
		
		String strQuery = "SELECT g FROM Group g" +
							" WHERE g.houseType.id=:houseTypeId" +
							" AND g.sessionType.id=:sessionTypeId" +
							" AND g.year=:year" +
							" ORDER BY g.number ASC";
		List<Group> groups = new ArrayList<Group>();
		
		try{
			TypedQuery<Group> jpQuery = this.em().createQuery(strQuery, Group.class);
			jpQuery.setParameter("houseTypeId", houseType.getId());
			jpQuery.setParameter("sessionTypeId", sessionType.getId());
			jpQuery.setParameter("year", year);
			List<Group> gX = jpQuery.getResultList();
			if(gX != null){
				groups = gX;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Group>_findByHouseTypeSessionTypeYear", "Group is unavailable.");
			throw elsException;
		}
		return groups;
	}

	public Group findByNumbersBySessionId(final Integer groupNumber, final String sessionId) throws ELSException {
		
		String strQuery = "SELECT g FROM Group g" +
				" WHERE g.session.id=:sessionId" +
				" AND g.number=:groupNumber";
		
		Group group = null;
		
		try{
			TypedQuery<Group> jpQuery = this.em().createQuery(strQuery, Group.class);
			jpQuery.setParameter("sessionId", Long.parseLong(sessionId));
			jpQuery.setParameter("groupNumber", groupNumber);
			group = jpQuery.getSingleResult();
		}catch(NoResultException nre) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_Group>findByNumberHouseTypeSessionTypeYear", "Group is unavailable.");
			throw elsException;
		}
		
		return group;		 		
	}
	
	/**
	 * Find answering dates.
	 *
	 * @param id the id
	 * @return the list
	 * @throws ELSException 
	 */
	public List<String> findAnsweringDates(final Long id, final String locale) throws ELSException {
		
		String strQuery = "SELECT NEW QuestionDates(qd.answeringDate) FROM Group g" +
				" JOIN g.questionDates qd" +
				" WHERE qd.id=:qdId ORDER BY qd.answeringDate ASC";
	
		List<String> answeringDates=new ArrayList<String>();
		try{
			TypedQuery<QuestionDates> jpQuery = this.em().createQuery(strQuery, QuestionDates.class);
			jpQuery.setParameter("qdId", id);
			List<QuestionDates> dates = jpQuery.getResultList();
	
			for(QuestionDates qd : dates){
				String answeringDate=FormaterUtil.formatDateToString(qd.getAnsweringDate(), ApplicationConstants.SERVER_DATEFORMAT, locale);
				answeringDates.add(answeringDate);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<String>_findAnsweringDates", "Answering date is unavailable.");
			throw elsException;
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
	 * @throws ELSException 
	 */
	public Group findByNumberHouseTypeSessionTypeYear(final Integer groupNumber,
			final HouseType houseType, final SessionType sessionType, final Integer year) throws ELSException {
			
		String strQuery = "SELECT g FROM Group g" +
							" WHERE g.houseType.id=:houseTypeId" +
							" AND g.sessionType.id=:sessionTypeId" +
							" AND g.year=:year" +
							" AND g.number=:groupNumber";
		Group group = null;
		try{
			TypedQuery<Group> jpQuery = this.em().createQuery(strQuery, Group.class);
			jpQuery.setParameter("houseTypeId", houseType.getId());
			jpQuery.setParameter("sessionTypeId", sessionType.getId());
			jpQuery.setParameter("year", year);
			jpQuery.setParameter("groupNumber", groupNumber);
			
			group = jpQuery.getSingleResult();
			
		}catch(NoResultException nre) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_Group>findByNumberHouseTypeSessionTypeYear", "Group is unavailable.");
			throw elsException;
		}
		
		return group;		 
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
	 * @throws ELSException 
	 */
	public Group find(final Ministry ministry, final HouseType houseType,
			final Integer sessionYear, final SessionType sessionType, final String locale) throws ELSException {
		String query = "SELECT g FROM Group g" 
						+ " JOIN g.ministries m"
						+ " WHERE g.locale=:locale"
						+ " AND g.houseType.id=:houseTypeId"
						+ " AND g.year=:sessionYear"
						+ " AND g.sessionType.id=:sessionTypeId"
						+ " AND m.id=:ministryId";
		Group group = null;
		try{
			TypedQuery<Group> jpQuery = this.em().createQuery(query, Group.class);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("houseTypeId", houseType.getId());
			jpQuery.setParameter("sessionYear", sessionYear);
			jpQuery.setParameter("sessionTypeId", sessionType.getId());
			jpQuery.setParameter("ministryId", ministry.getId());
			
			group = jpQuery.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_Group_find", "Group is unavailable.");
			throw elsException;
		}
				
		return group;
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
		
		org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "GROUP_ALL_GROUP_DATES_FORMATTED", "");
		List<QuestionDatesVO> questionDatesVOs=new ArrayList<QuestionDatesVO>();
		
		try{
			if(query != null){
				Query jpQuery = this.em().createNativeQuery(query.getQuery());
				jpQuery.setParameter("sessionTypeId", sessionType.getId());
				jpQuery.setParameter("sessionYear", sessionYear);
				jpQuery.setParameter("houseTypeId", houseType.getId());
				jpQuery.setParameter("locale", locale);
				
				List results = jpQuery.getResultList();
				
				CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT", "");
				CustomParameter dayOfWeekDateFormat=CustomParameter.findByName(CustomParameter.class,"DAY_OF_WEEK_FORMAT", "");
				
					if(dbDateFormat!=null&&dayOfWeekDateFormat!=null){
						SimpleDateFormat dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale);
						SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);
						SimpleDateFormat dayOfWeekFormat=FormaterUtil.getDateFormatter(dayOfWeekDateFormat.getValue(), locale);
						// NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale);
						for(Object i:results){
							Object[] entry=(Object[]) i;
							QuestionDatesVO questionDatesVO=new QuestionDatesVO();
							if(entry[0]!=null){
								questionDatesVO.setGroupId(Long.parseLong(entry[0].toString()));
							}
							if(entry[1]!=null){
								int number=Integer.parseInt(entry[1].toString());
								//questionDatesVO.setGroup(numberFormat.format(number));
								questionDatesVO.setRowId(number);
							}if(entry[2]!=null){
								Date date=dbFormat.parse(entry[2].toString());
								questionDatesVO.setAnsweringDate(format.format(date));
								questionDatesVO.setDayOfWeek(FormaterUtil.getDayInLocaleLanguage(dayOfWeekFormat.format(date), locale));
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
							}if(entry[12] != null){
								questionDatesVO.setHouseType(entry[12].toString());
							}if(entry[13] != null){
								questionDatesVO.setGroup(entry[13].toString());
							}
							
							questionDatesVOs.add(questionDatesVO);
						}
					}else{
						logger.error("**** Custom Parameter 'DB_DATEFORMAT and WEEKDAY_FORMAT' not set ****");
					}
				}
		}catch (NumberFormatException e) {
			e.printStackTrace();
			return questionDatesVOs;
		}
		catch (ParseException e) {
			e.printStackTrace();
			return questionDatesVOs;
		}
		return questionDatesVOs;
	}
	
	private List<QuestionDates> findQuestionDatesByGroup(final HouseType houseType,
			final SessionType sessionType, final Integer sessionYear, final Integer groupNumber,
			final String locale) throws ELSException {
		
		List<QuestionDates> questionDates = null;		
		
		String query="SELECT qd FROM Group g" +
				" JOIN g.questionDates qd " +
				" WHERE g.houseType.id=:houseTypeId" +
				" AND g.sessionType.id=:sessionTypeId" +
				" AND g.year=:sessionYear" +
				" AND g.number=:groupNumber" + 
				" AND g.locale=:locale";
		
		try{
			TypedQuery<QuestionDates> jpQuery = this.em().createQuery(query, QuestionDates.class);
			jpQuery.setParameter("houseTypeId", houseType.getId());
			jpQuery.setParameter("sessionTypeId", sessionType.getId());
			jpQuery.setParameter("sessionYear", sessionYear);
			jpQuery.setParameter("groupNumber", groupNumber);
			jpQuery.setParameter("locale", locale);
			
			questionDates = jpQuery.getResultList();			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<MasterVO>_findQuestionDateByGroup", "No date found.");
			throw elsException;
		}
		
		return questionDates;
	}

	public List<MasterVO> findQuestionDateByGroup(final HouseType houseType,
			final SessionType sessionType, final Integer sessionYear, final Integer groupNumber,
			final String locale) throws ELSException {
		
		List<MasterVO> dates=new ArrayList<MasterVO>();
		
		List<QuestionDates> questionDates = findQuestionDatesByGroup(houseType, sessionType, sessionYear, groupNumber, locale);
		if(questionDates!=null) {
			SimpleDateFormat format=FormaterUtil.getDateFormatter(locale);
			for(QuestionDates i:questionDates){
				if(i.getAnsweringDate()!=null){
					MasterVO masterVO=new MasterVO(i.getId(),format.format(i.getAnsweringDate()));
					dates.add(masterVO);
				}
			}
		}			
		
		return dates;
	}    
	
	public List<Reference> findQuestionDateReferenceVOByGroup(final HouseType houseType,
			final SessionType sessionType, final Integer sessionYear, final Integer groupNumber,
			final String locale) throws ELSException {
		
		List<Reference> dates=new ArrayList<Reference>();
		
		List<QuestionDates> questionDates = findQuestionDatesByGroup(houseType, sessionType, sessionYear, groupNumber, locale);
		if(questionDates!=null) {
			CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
			SimpleDateFormat dbFormat = FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
			MessageResource mrDate = MessageResource.findByFieldName(MessageResource.class, "code", "generic.date", locale.toString());
			for(QuestionDates i:questionDates){
				if(i.getAnsweringDate()!=null){
					//Added the following code to solve the marathi month and day issue
					if(dbDateFormat!=null && dbFormat!=null) {
		            	String[] strAnsweringDates=dbFormat.format(i.getAnsweringDate()).split(",");
	            		String answeringDay=FormaterUtil.getDayInLocaleLanguage(strAnsweringDates[0],locale.toString());
	            		String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
	            		String answeringMonth=FormaterUtil.getMonthInLocaleLanguage(strAnsweringMonth[1], locale.toString());
	            		String genericDateLabel  = (mrDate!=null)? mrDate.getValue():"Date";
	            		String formattedAnsweringDate = answeringDay+", "+ genericDateLabel + " " +strAnsweringMonth[0]+" "+ answeringMonth +","+strAnsweringDates[2];
	            		Reference referenceVO=new Reference(i.getId().toString(),formattedAnsweringDate);
						dates.add(referenceVO);
		            } else {
		            	Reference referenceVO=new Reference(i.getId().toString(),FormaterUtil.formatDateToString(i.getAnsweringDate(), ApplicationConstants.REPORT_DATEFORMAT, locale.toString()));
						dates.add(referenceVO);
		            }
				}
			}
		}			
		
		return dates;
	}

	public List<Ministry> findMinistriesByName(final Long groupid) throws ELSException {
		String query="SELECT m FROM Group g" +
				" JOIN g.ministries m" +
				" WHERE g.id=:groupid ORDER BY m.name " + ApplicationConstants.ASC;
		
		List<Ministry> ministries = new ArrayList<Ministry>();
		try{
			TypedQuery<Ministry> jpQuery = this.em().createQuery(query, Ministry.class);
			jpQuery.setParameter("groupid", groupid);
			
			List<Ministry> mX = jpQuery.getResultList();
			if(mX != null){
				ministries = null;
				ministries = mX;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Ministry>_findMinistriesByName", "No ministry found.");
			throw elsException;
		}
		
		return ministries;
	}

	public Group find(final Session session, 
			final Date answeringDate,
			final String  locale) throws ELSException {
		
		String strQuery = "SELECT g FROM Group g" +
							" JOIN g.questionDates qd" +
							" WHERE g.houseType.type=:houseType_Type" +
							" AND g.sessionType.type=:sessionType_Type" +
							" AND g.year=:year" +
							" AND qd.answeringDate=:answeringDate" +
							" AND g.locale=:locale";
		
		Group group = null;
		try{
			Date date = FormaterUtil.formatStringToDate(FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.DB_DATEFORMAT, "en_US");
			TypedQuery<Group> jpQuery = this.em().createQuery(strQuery, Group.class);
			jpQuery.setParameter("houseType_Type", session.getHouse().getType().getType());
			jpQuery.setParameter("sessionType_Type", session.getType().getType());
			jpQuery.setParameter("year", session.getYear());
			jpQuery.setParameter("answeringDate", date);
			jpQuery.setParameter("locale", locale);
			
			group = jpQuery.getSingleResult();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_Group_find", "No group found.");
			throw elsException;
		}
		
		return group;
	}

	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesByPriority(final Long groupid) throws ELSException {
		String query="SELECT m FROM MemberMinister mm" +
						" RIGHT JOIN mm.ministry m" +
						" WHERE m.id IN (" +
						" SELECT mi.id FROM Group g " +
						" JOIN g.ministries mi" +
						" WHERE g.id=:groupId)" + 
						" AND mm.ministryFromDate<=:sessionStartDate " +
						" AND (mm.ministryToDate>=:sessionStartDate OR mm.ministryToDate IS NULL)" +
						" ORDER BY mm.priority";
		List<Ministry> ministries = new ArrayList<Ministry>();
		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("groupId", groupid);
			List<Ministry> mX = jpQuery.getResultList();
			if(mX != null){
				ministries = null;
				ministries = mX;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Ministry>_findMinistriesByPriority", "No ministry found.");
			throw elsException;
		}
		
		return ministries;
	}

	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesByPriority(final Group group) throws ELSException {
		String query="SELECT m FROM MemberMinister mm" +
					" RIGHT JOIN mm.ministry m"+
					" WHERE m.id IN" +
					" (SELECT mi.id FROM Group g" +
					" JOIN g.ministries mi" +
					" WHERE g.id=:groupId)" +
					" AND mm.ministryFromDate<=:sessionStartDate " +
					" AND (mm.ministryToDate>=:sessionStartDate OR mm.ministryToDate IS NULL)" +
					" ORDER BY mm.priority";
		
		
		List<Ministry> ministries = new ArrayList<Ministry>();
		try{
			Query jpQuery = this.em().createQuery(query);
			jpQuery.setParameter("groupId", group.getId());
			jpQuery.setParameter("sessionStartDate", group.getSession().getStartDate());
			List<Ministry> mX = jpQuery.getResultList();
			if(mX != null){
				ministries = null;
				ministries = mX;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Ministry>_findMinistriesByPriority", "No ministry found.");
			throw elsException;
		}
		
		return ministries;	
	}
	
	@SuppressWarnings("unchecked")
	public List<MinistryVO> findMinistriesByMinisterView(final Group group, final String locale) throws ELSException {
		List<MinistryVO> ministriesByMinisterView = new ArrayList<MinistryVO>();
		Map<String, String[]> queryParameters = new HashMap<String, String[]>();
		queryParameters.put("locale", new String[]{locale});
		queryParameters.put("groupId", new String[]{group.getId().toString()});
		String sessionStartDate = FormaterUtil.formatDateToString(group.getSession().getStartDate(), ApplicationConstants.DB_DATEFORMAT);
		queryParameters.put("sessionStartDate", new String[]{sessionStartDate});
		List resultList = org.mkcl.els.domain.Query.findReport("LOAD_GROUP_MINISTRIES_BY_MINISTER_VIEW", queryParameters);
		if(resultList!=null && !resultList.isEmpty()) {
			for(Object result: resultList) {
				Object[] r = (Object[]) result;
				if(r!=null && r.length>0) {
					MinistryVO ministryVO = new MinistryVO();
					ministryVO.setNumber(r[0].toString());
					ministryVO.setName(r[1].toString());
					ministriesByMinisterView.add(ministryVO);
				}
			}
		}
		return ministriesByMinisterView;		
	}
	
	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesInGroupsForSessionExcludingGivenGroup(final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final Integer groupNumber, 
			final String locale) throws ELSException {
		List<Ministry> ministries = new ArrayList<Ministry>();
		
		try{
			if(houseType != null && sessionType != null && sessionYear != null && groupNumber != null && locale != null) {
				if(!locale.isEmpty()) {
					String query="SELECT g.ministries FROM Group g " +
							" WHERE g.houseType.id=:houseTypeId" +
							" AND g.sessionType.id=:sessionTypeId" +
							" AND g.year=:sessionYear" +
							" AND g.number!=:groupNumber" +
							" AND g.locale=:locale";
					
					Query jpQuery = this.em().createQuery(query);
					jpQuery.setParameter("houseTypeId", houseType.getId());
					jpQuery.setParameter("sessionTypeId", sessionType.getId());
					jpQuery.setParameter("sessionYear", sessionYear);
					jpQuery.setParameter("groupNumber", groupNumber);
					jpQuery.setParameter("locale", locale);
					
					ministries = jpQuery.getResultList();	
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Ministry>_findMinistriesInGroupsForSessionExcludingGivenGroup", "No ministry found.");
			throw elsException;
		}
		
		return ministries;
	}
	
	@SuppressWarnings("unchecked")
	public List<Ministry> findMinistriesInGroupsForSession(final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final String locale) throws ELSException {
		List<Ministry> ministries = new ArrayList<Ministry>();
		
		try{
			if(houseType != null && sessionType != null && sessionYear != null && locale != null) {
				if(!locale.isEmpty()) {
					String query="SELECT g.ministries FROM Group g " +
							" WHERE g.houseType.id=:houseTypeId" +
							" AND g.sessionType.id=:sessionTypeId" +
							" AND g.year=:sessionYear" +
							" AND g.locale=:locale";
					
					Query jpQuery = this.em().createQuery(query);
					jpQuery.setParameter("houseTypeId", houseType.getId());
					jpQuery.setParameter("sessionTypeId", sessionType.getId());
					jpQuery.setParameter("sessionYear", sessionYear);
					jpQuery.setParameter("locale", locale);
					
					ministries = jpQuery.getResultList();	
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Ministry>_findMinistriesInGroupsForSession", "No ministry found.");
			throw elsException;
		}
		
		return ministries;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findGroupNumbersForSessionExcludingGivenGroup(final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final Integer groupNumber, 
			final String locale) throws ELSException {
		List<Integer> groupNumbers = new ArrayList<Integer>();
		
		try{
			if(houseType != null && sessionType != null && sessionYear != null && groupNumber != null && locale != null) {
				if(!locale.isEmpty()) {
					String query="SELECT g.number FROM Group g " +
							" WHERE g.houseType.id=:houseTypeId" +
							" AND g.sessionType.id=:sessionTypeId" +
							" AND g.year=:sessionYear" +
							" AND g.number!=:groupNumber" +
							" AND g.locale=:locale";
					
					Query jpQuery = this.em().createQuery(query);
					jpQuery.setParameter("houseTypeId", houseType.getId());
					jpQuery.setParameter("sessionTypeId", sessionType.getId());
					jpQuery.setParameter("sessionYear", sessionYear);
					jpQuery.setParameter("groupNumber", groupNumber);
					jpQuery.setParameter("locale", locale);
					
					groupNumbers = jpQuery.getResultList();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Integer>findGroupNumbersForSessionExcludingGivenGroup", "No ministry found.");
			throw elsException;
		}
		
		return groupNumbers;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findGroupNumbersForSession(final HouseType houseType, 
			final SessionType sessionType, 
			final Integer sessionYear, 
			final String locale) throws ELSException {
		List<Integer> groupNumbers = new ArrayList<Integer>();
		
		try{
			if(houseType != null && sessionType != null && sessionYear != null && locale != null) {
				if(!locale.isEmpty()) {
					String query="SELECT g.number FROM Group g " +
							" WHERE g.houseType.id=:houseTypeId" +
							" AND g.sessionType.id=:sessionTypeId" +
							" AND g.year=:sessionYear" +
							" AND g.locale=:locale";
					
					Query jpQuery = this.em().createQuery(query);
					jpQuery.setParameter("houseTypeId", houseType.getId());
					jpQuery.setParameter("sessionTypeId", sessionType.getId());
					jpQuery.setParameter("sessionYear", sessionYear);
					jpQuery.setParameter("locale", locale);
					
					groupNumbers = jpQuery.getResultList();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_List<Integer>_findGroupNumbersForSession", "No group found.");
			throw elsException;
		}
		
		return groupNumbers;
	}

	public Group find(final SubDepartment subdepartment, final Session session,
			final String locale) throws ELSException {
		String query = "SELECT g FROM Group g" 
				+ " JOIN g.subdepartments m"
				+ " WHERE g.locale=:locale"
				+ " AND m.id=:subdepartmentId"
				+ " AND g.session.id =:sessionId";
		Group group = null;
		try{
			TypedQuery<Group> jpQuery = this.em().createQuery(query, Group.class);
			jpQuery.setParameter("locale", locale);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("subdepartmentId", subdepartment.getId());
			
			group = jpQuery.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_Group_find", "Group is unavailable.");
			throw elsException;
		}
				
		return group;
	}
	
	public Group findByAnsweringDateInHouseType(final Date answeringDate, final HouseType houseType) throws ELSException {
		Group group = null;
		
		String strQuery = "SELECT * FROM groups g" +
							" INNER JOIN question_dates qd ON (qd.group_id=g.id)" +
							" WHERE (qd.answering_date=:answeringDate OR qd.display_answering_date=:answeringDate)" +
							" AND g.housetype_id=:houseTypeId";
		
		try{
			Query jpQuery = this.em().createNativeQuery(strQuery, Group.class);
			jpQuery.setParameter("houseTypeId", houseType.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			@SuppressWarnings("unchecked")
			List<Group> gX = jpQuery.getResultList();
			if(gX != null && gX.size()>0){
				group = gX.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("GroupRepository_Group_findByHouseTypeAnsweringDate", "Group is unavailable.");
			throw elsException;
		}
		return group;
	}
}
