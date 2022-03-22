/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Session.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.SessionVO;
import org.mkcl.els.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Session.
 *
 * @author anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "sessions")
@JsonIgnoreProperties({"parameters", "drafts"})
public class Session extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The house. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    /** The year. */
    @Column(name="session_year")
    private Integer year;

    /** The type. */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sessiontype_id")
    private SessionType type;

    /** The place. */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sessionplace_id")
    private SessionPlace place;

    /** The number. */
    private Integer number;

    /** The start date. */
    @Temporal(TemporalType.DATE)
    private Date startDate;

    /** The end date. */
    @Temporal(TemporalType.DATE)
    private Date endDate;

    /** The tentative start date. */
    @Temporal(TemporalType.DATE)
    private Date tentativeStartDate;

    /** The tentative end date. */
    @Temporal(TemporalType.DATE)
    private Date tentativeEndDate;

    /** The actual start date. */
    @Temporal(TemporalType.DATE)
    private Date actualStartDate;

    /** The actual end date. */
    @Temporal(TemporalType.DATE)
    private Date actualEndDate;

    /*
     * devices enabled for a session.This will be a list of enabled device type separated
     * by comma
     */
    /** The device types enabled. */
    @Column(length=1000)
    private String deviceTypesEnabled;
    /*
     * devices which require balloting.This will be a lsit of enabled device type separated
     * by comma.
     */

    /** The duration in days. */
    private Integer durationInDays;

    /** The duration in hrs. */
    private Integer durationInHrs;

    /** The duration in mins. */
    private Integer durationInMins;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;
   
    //---------------------Added by anand, vikas & dhananjay--------------------------------
    /** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="session_devicetype_config")
	private Map<String,String> parameters;
	
	/** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
    /** The drafts. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="session_id", referencedColumnName="id")
    private List<SessionDraft> drafts;
 
    /** The session repository. */
    @Autowired
    private transient SessionRepository sessionRepository;

    // -------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new session.
     */
    public Session() {
        super();
    }


    // -------------------------------Domain_Methods----------------------------------------------

    /**
     * Gets the session repository.
     *
     * @return the session repository
     */
    public static SessionRepository getSessionRepository() {
        SessionRepository sessionRepository = new Session().sessionRepository;
        if (sessionRepository == null) {
            throw new IllegalStateException(
                    "SessionRepository has not been injected in Session Domain");
        }
        return sessionRepository;
    }

    /**
     * Find latest session.
     *
     * @param houseType the house type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @throws ELSException 
     * @since v1.0.0
     */
    public static Session findLatestSession(final HouseType houseType,final Integer sessionYear) throws ELSException {
        return getSessionRepository().findLatestSession(houseType,sessionYear);
    }

    /**
     * Find latest session.
     *
     * @param houseType the house type
     * @return the session
     * @author compaq
     * @throws ELSException 
     * @since v1.0.0
     */
    public static Session findLatestSession(final HouseType houseType) throws ELSException {
        return getSessionRepository().findLatestSession(houseType);
    }
    
    public static Session findLatestSessionHavingGivenDeviceTypeEnabled(final HouseType houseType, final DeviceType deviceType) throws ELSException {
        return getSessionRepository().findLatestSessionHavingGivenDeviceTypeEnabled(houseType, deviceType);
    }

    /**
     * Find sessions by house and year.
     *
     * @param house the house
     * @param year the year
     * @return the list< session>
     * @author compaq
     * @throws ELSException 
     * @since v1.0.0
     */
    public static List<Session> findSessionsByHouseTypeAndYear(final House house,final Integer year) throws ELSException{
        return getSessionRepository().findSessionsByHouseTypeAndYear(house, year);
    }
    
    public static List<Session> findSessionsByHouseAndDateLimits(final House house,final Date lowerLimit,final Date upperLimit){
        return getSessionRepository().findSessionsByHouseAndDateLimits(house, lowerLimit, upperLimit);
    }

    /**
     * Find session by house session type year.
     *
     * @param house the house
     * @param sessionType the session type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @throws ELSException 
     * @since v1.0.0
     */
    public static Session findSessionByHouseSessionTypeYear(final House house,
			final SessionType sessionType, final Integer sessionYear) throws ELSException {
        return getSessionRepository().findSessionByHouseSessionTypeYear(house, sessionType, sessionYear);
    }

    /**
     * Find session by house type session type year.
     *
     * @param houseType the house type
     * @param sessionType the session type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @throws ELSException 
     * @since v1.0.0
     */
    public static Session findSessionByHouseTypeSessionTypeYear(final HouseType houseType,
            final SessionType sessionType, final Integer sessionYear) throws ELSException {
        return getSessionRepository().findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
    }

    public static List<Session> findSessionsByHouseTypeAndYear(
			final HouseType houseType, final Integer sessionYear) throws ELSException {
		return getSessionRepository().findSessionsByHouseTypeAndYear(houseType,sessionYear);
	}
    
	public static List<Session> findSessionsByHouseAndYearForGivenDeviceTypeEnabled(
			final House house, final Integer sessionYear, final DeviceType deviceType) throws ELSException {
		return getSessionRepository().findSessionsByHouseAndYearForGivenDeviceTypeEnabled(house,sessionYear,deviceType);
	}
	
	public static House findCorrespondingAssemblyHouseForCouncilSession(final Session councilSession) {
		House correspondingAssemblyHouseForCouncilSession = null;
		String locale = councilSession.getLocale();		
		HouseType assemblyHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
		List<House> assemblyHouses = House.findAllByFieldName(House.class, "type", assemblyHouseType, "firstDate", ApplicationConstants.DESC, locale);
		for(House h: assemblyHouses) {
			if(h.getFirstDate().before(councilSession.getStartDate())
					&& h.getLastDate().after(councilSession.getStartDate())) {
				correspondingAssemblyHouseForCouncilSession = h;
				break;
			}
		}		
		return correspondingAssemblyHouseForCouncilSession;
	}
	
	//------------------dhananjayb_23012013----------------------------
	public static List<String> getParametersSetForDeviceType(final Long sessionId, final String deviceType) throws ELSException {
		return getSessionRepository().getParametersSetForDeviceType(sessionId,deviceType);
    }

	public static Session find(final Integer sessionyear, final String sessiontype, final String housetype) throws ELSException {
		return getSessionRepository().find(sessionyear,sessiontype,housetype);
    }
	
	//---------------------Added by vikas & dhananjay--------------------------------
	/**
	 * to find the previous session of the given session 
	 * @param session given session
	 * @return previous session 
	 * @throws ELSException 
	 */
	public static Session findPreviousSession(final Session session) throws ELSException{
		
		List<Session> totalSessions = new ArrayList<Session>();
		
		List<Session> sessionListCurrent = Session.findSessionsByHouseTypeAndYear(session.getHouse().getType(), session.getYear());		
		sessionListCurrent.remove(session);
		totalSessions.addAll(sessionListCurrent);
		
		List<Session> sessionListLast = Session.findSessionsByHouseTypeAndYear(session.getHouse().getType(), session.getYear()-1);
				
		if(sessionListLast.size() > 0){
			Session lastSessionPrevYear = sessionListLast.get(0);
			totalSessions.add(lastSessionPrevYear);
		}
				
		return compareSession(totalSessions, session);
	}
	
	public static Session findPreviousSessionInSameHouseForGivenDeviceTypeEnabled(final Session session, final DeviceType deviceType) throws ELSException{
		
		List<Session> totalSessions = new ArrayList<Session>();
		
		List<Session> sessionListCurrent = Session.findSessionsByHouseAndYearForGivenDeviceTypeEnabled(session.getHouse(), session.getYear(), deviceType);
		for(Session s: sessionListCurrent) {
			if(s.getStartDate().compareTo(session.getStartDate())<0) {
				totalSessions.add(s);
			}
		}
		
		List<Session> sessionListLast = Session.findSessionsByHouseAndYearForGivenDeviceTypeEnabled(session.getHouse(), session.getYear()-1, deviceType);
		
		if(sessionListLast!=null && sessionListLast.size() > 0){
			Session lastSessionPrevYear = sessionListLast.get(0);
			totalSessions.add(lastSessionPrevYear);
		}
		
		return compareSession(totalSessions, session);		
	}
	
	/**
	 * helper method to find the previous session 
	 * @param sessionList 
	 * @param session
	 * @return
	 */
	private static Session compareSession(List<Session> sessionList, final Session session){
		
		Object[] sessions = sessionList.toArray();
		long minDiff;
		int indexer=-1;
		
		if(sessionList.size() > 0){
			
			minDiff = session.getStartDate().getTime() - ((Session)sessions[0]).getStartDate().getTime();
		
			for(int i = 0; i < sessions.length; i++){
				
				long tempTimeDifference = (session.getStartDate().getTime() - ((Session)sessions[i]).getStartDate().getTime()); 
				
				if(tempTimeDifference <= minDiff){
					minDiff = tempTimeDifference;
					indexer = i;
				}
			}
		}
		if(indexer >= 0)
			return ((Session)sessions[indexer]);
		else
			return null;
	}
	
	public String findHouseType() {
		String sessionHouseType = null;
		if(this.getHouse() != null) {
			if(this.getHouse().getType() != null) {
				if(this.getHouse().getType().getType() != null) {					
					sessionHouseType = this.getHouse().getType().getType();					
				}
			}
		}
		return sessionHouseType;
	}
	
	public List<Date> findAllSessionDates() {
		return findAllSessionDates(true, null); //finding all the session dates including holidays and hence no day working scope needed to match
	}
	
	public List<Date> findAllSessionDatesHavingNoHoliday() {	
		return findAllSessionDates(false, ApplicationConstants.DAY_WORKING_SCOPE_HOUSE_PROCEEDING); //finding all the session dates discluding holidays applicable for default 'house proceeding' day working scope
	}
	
	public List<Date> findAllSessionDatesHavingNoHoliday(String dayWorkingScope) {
		return findAllSessionDates(false, dayWorkingScope); //finding all the session dates discluding holidays applicable for given day working scope
	}
	
	public List<Date> findAllSessionDates(Boolean includedHolidays, String dayWorkingScope) {
		
		List<Date> sessionDates = new ArrayList<Date>();
		
		/** set calendar from session start date for processing all session dates **/
		Calendar sessionCalender = Calendar.getInstance();
		sessionCalender.setTime(this.getStartDate());
		
		/** loop through all session dates & collect non-holiday dates in the output list **/
		for(sessionCalender.getTime(); sessionCalender.getTime().compareTo(this.getEndDate())<=0; sessionCalender.add(Calendar.DATE, 1)) {
			
			Date sessionDate = sessionCalender.getTime();
			
			if(!includedHolidays) {
				//skip the date if it's holiday for given day working scope
				if(Holiday.isHolidayOnDate(sessionDate, dayWorkingScope, this.getLocale())) {
					continue;
				}
			}			
			
			sessionDates.add(sessionDate);
		}
		
		return sessionDates;
	}
	
	/** check if current date is in given session 
	 * @throws ELSException **/
	public static Boolean isCurrentDateInSession(final Session session) throws ELSException {	
		Date currentDate = new Date();		
		return isGivenDateInSession(currentDate, session);
	}
	
	public static Boolean isGivenDateInSession(final Date date, final Session session) throws ELSException {
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		Boolean isGivenDateInSession = null;
		if(DateUtil.compareDatePartOnly(date, session.getStartDate())>=0 && DateUtil.compareDatePartOnly(date, session.getEndDate())<=0) {
			isGivenDateInSession = true;
		} else {
			isGivenDateInSession = false;
		}
		return isGivenDateInSession;
	}
	
	public static List<SessionVO> findAllSessionDetailsForGivenHouseType(final HouseType houseType, final Date fromDate, final String locale) {
		return getSessionRepository().findAllSessionDetailsForGivenHouseType(houseType, fromDate, locale);
	}
	
	public static boolean loadSubmissionDatesForDeviceTypeInSession(final Session session, final DeviceType deviceType, final Date fromDate, final Date toDate) {
		return getSessionRepository().loadSubmissionDatesForDeviceTypeInSession(session, deviceType, fromDate, toDate);
	}
	
	public Date getNextSessionDate(final Date currentDate, final int difference, final String locale) {
		if(currentDate != null) {
			/**** Find Next Date (add difference to current date and obtain next working date) ****/
			Date nextDate = Holiday.getNextWorkingDateFrom(currentDate, difference, locale);
			/**** Return if the Next Date is a session date in this session ****/
			if(this.getStartDate()!=null
					&& this.getEndDate()!=null
					&& (nextDate.after(this.getStartDate())
							||nextDate.equals(this.getStartDate()))
							&&(nextDate.before(this.getEndDate())
									||nextDate.equals(this.getEndDate()))){
				
				return nextDate;
			}		
		} 
		return null;
	}
	
    // ------------------------------Getters/Setters-----------------------
	/**
     * Gets the house.
     *
     * @return the house
     */
    public House getHouse() {
		return house;
	}


	/**
	 * Sets the house.
	 *
	 * @param house the new house
	 */
	public void setHouse(final House house) {
		this.house = house;
	}


	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}


	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(final Integer year) {
		this.year = year;
	}


	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public SessionType getType() {
		return type;
	}


	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(final SessionType type) {
		this.type = type;
	}


	/**
	 * Gets the place.
	 *
	 * @return the place
	 */
	public SessionPlace getPlace() {
		return place;
	}


	/**
	 * Sets the place.
	 *
	 * @param place the new place
	 */
	public void setPlace(final SessionPlace place) {
		this.place = place;
	}


	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}


	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(final Integer number) {
		this.number = number;
	}


	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}


	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}


	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}


	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}


	/**
	 * Gets the tentative start date.
	 *
	 * @return the tentative start date
	 */
	public Date getTentativeStartDate() {
		return tentativeStartDate;
	}


	/**
	 * Sets the tentative start date.
	 *
	 * @param tentativeStartDate the new tentative start date
	 */
	public void setTentativeStartDate(final Date tentativeStartDate) {
		this.tentativeStartDate = tentativeStartDate;
	}


	/**
	 * Gets the tentative end date.
	 *
	 * @return the tentative end date
	 */
	public Date getTentativeEndDate() {
		return tentativeEndDate;
	}


	/**
	 * Sets the tentative end date.
	 *
	 * @param tentativeEndDate the new tentative end date
	 */
	public void setTentativeEndDate(final Date tentativeEndDate) {
		this.tentativeEndDate = tentativeEndDate;
	}


	/**
	 * Gets the actual start date.
	 *
	 * @return the actual start date
	 */
	public Date getActualStartDate() {
		return actualStartDate;
	}


	/**
	 * Sets the actual start date.
	 *
	 * @param actualStartDate the new actual start date
	 */
	public void setActualStartDate(final Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}


	/**
	 * Gets the actual end date.
	 *
	 * @return the actual end date
	 */
	public Date getActualEndDate() {
		return actualEndDate;
	}


	/**
	 * Sets the actual end date.
	 *
	 * @param actualEndDate the new actual end date
	 */
	public void setActualEndDate(final Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}
	

	/**
	 * Gets the device types enabled.
	 *
	 * @return the device types enabled
	 */
	public String getDeviceTypesEnabled() {
		return deviceTypesEnabled;
	}


	/**
	 * Sets the device types enabled.
	 *
	 * @param deviceTypesEnabled the new device types enabled
	 */
	public void setDeviceTypesEnabled(final String deviceTypesEnabled) {
		this.deviceTypesEnabled = deviceTypesEnabled;
	}

	/**
	 * Gets the duration in days.
	 *
	 * @return the duration in days
	 */
	public Integer getDurationInDays() {
		return durationInDays;
	}


	/**
	 * Sets the duration in days.
	 *
	 * @param durationInDays the new duration in days
	 */
	public void setDurationInDays(final Integer durationInDays) {
		this.durationInDays = durationInDays;
	}


	/**
	 * Gets the duration in hrs.
	 *
	 * @return the duration in hrs
	 */
	public Integer getDurationInHrs() {
		return durationInHrs;
	}


	/**
	 * Sets the duration in hrs.
	 *
	 * @param durationInHrs the new duration in hrs
	 */
	public void setDurationInHrs(final Integer durationInHrs) {
		this.durationInHrs = durationInHrs;
	}


	/**
	 * Gets the duration in mins.
	 *
	 * @return the duration in mins
	 */
	public Integer getDurationInMins() {
		return durationInMins;
	}


	/**
	 * Sets the duration in mins.
	 *
	 * @param durationInMins the new duration in mins
	 */
	public void setDurationInMins(final Integer durationInMins) {
		this.durationInMins = durationInMins;
	}


	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}


	/**
	 * Sets the remarks.
	 *
	 * @param remarks the new remarks
	 */
	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}


	/**
	 * @return the editedOn
	 */
	public Date getEditedOn() {
		return editedOn;
	}


	/**
	 * @param editedOn the editedOn to set
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}


	/**
	 * @return the editedBy
	 */
	public String getEditedBy() {
		return editedBy;
	}


	/**
	 * @param editedBy the editedBy to set
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}


	/**
	 * @return the editedAs
	 */
	public String getEditedAs() {
		return editedAs;
	}


	/**
	 * @param editedAs the editedAs to set
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}


	/**
	 * @return the drafts
	 */
	public List<SessionDraft> getDrafts() {
		return drafts;
	}


	/**
	 * @param drafts the drafts to set
	 */
	public void setDrafts(List<SessionDraft> drafts) {
		this.drafts = drafts;
	}


	/**
	 * Sets the session repository.
	 *
	 * @param sessionRepository the new session repository
	 */
	public void setSessionRepository(final SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}
	
	public Map<String, String> getParametersWithoutFormatting(){ //return parameters as they are already saved in the database
		return this.parameters;
	}
	
	//---------------------Added by vikas & dhananjay--------------------------------
	public Map<String, String> getParameters(){
						
		//to get the date parameters formatted in current locale
				if(!this.getLocale().equalsIgnoreCase(ApplicationConstants.STANDARD_LOCALE)){
					
					Map<String, String> localParameters = this.parameters;
					
					if((localParameters != null) & (!(localParameters.isEmpty()))){
						
						for (Map.Entry<String, String> entry : localParameters.entrySet()){
							System.out.println("key: " + entry.getKey()+"; value: " + entry.getValue());
							if((entry.getKey().endsWith(("Date")))){
								if(!entry.getValue().contains("/")){
									CustomParameter parameter;
									CustomParameter dbParameter;
									if(entry.getValue().length()>10){
										 parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
										 dbParameter = CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
									}
									else{
										 parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
										 dbParameter = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
									}									
									
									Date date = FormaterUtil.formatStringToDate(entry.getValue(), dbParameter.getValue(), this.getLocale());
									
									entry.setValue(FormaterUtil.formatDateToString(date, parameter.getValue(), this.getLocale()));
								}
								
							}else if((entry.getKey().endsWith(("Dates")))){
								
								String[] dates = entry.getValue().split("#");
								
								for(int i = 0; i < dates.length; i++){
								
									if(!entry.getValue().contains("/")){
										CustomParameter parameter;
										CustomParameter dbParameter;
										
										if(dates[i].length()>10){
											 parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
											 dbParameter = CustomParameter.findByName(CustomParameter.class, "DB_DATETIMEFORMAT", "");
										}
										else{
											 parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
											 dbParameter = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
										}
										
										Date date = FormaterUtil.formatStringToDate(dates[i], dbParameter.getValue(), this.getLocale());
										
										dates[i] = FormaterUtil.formatDateToString(date, parameter.getValue(), this.getLocale());
									}
								}
								
								String value= ""; 
								for(int i = 0; i < dates.length; i++){
									
									if((i == (dates.length - 1))){
										value += dates[i];
									}else{
										value += dates[i] + "#";
									}
								}
								entry.setValue(value);
								
							}else{
								
								try {									
									Integer i = Integer.parseInt(entry.getValue());
							        
									entry.setValue(FormaterUtil.formatDecimalNumber(i, this.getLocale()));
									
								} catch (NumberFormatException nfe) {
									
								}
							}
						}
						
						return localParameters;
					}
				}
				return parameters;
	}

	//---------------------Added by vikas & dhananjay--------------------------------
	public void setParameters(Map<String, String> parameters) throws ELSException {
		CustomParameter SERVER_TIMESTAMP = CustomParameter.findByName(CustomParameter.class, 
				"SERVER_TIMESTAMP", "");
		CustomParameter SERVER_DATEFORMAT = CustomParameter.findByName(CustomParameter.class, 
				"SERVER_DATEFORMAT", "");
		CustomParameter DB_DATEFORMAT = CustomParameter.findByName(CustomParameter.class, 
				"DB_DATEFORMAT", "");
		CustomParameter DB_TIMESTAMP = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		
		//to save the date parameters in en_US locale
		if(!this.getLocale().equalsIgnoreCase("en_US")) {	
			Map<String, String> localParameters = parameters;
			if((localParameters != null) & (!(localParameters.isEmpty()))) {
				for (Map.Entry<String, String> entry : localParameters.entrySet()) {
					System.out.println("key: " + entry.getKey()+"; value: " + entry.getValue());
					CustomParameter serverFormat = null;
					if((entry.getKey().endsWith(("Date")))) {
						if(entry.getValue().length()>10) {
							serverFormat = SERVER_TIMESTAMP;
						}
						else {
							serverFormat = SERVER_DATEFORMAT;
						}		
						SimpleDateFormat dateFormat = new SimpleDateFormat(serverFormat.getValue(),
								new Locale("en", "US"));
						Date date;
						try {
							dateFormat.setLenient(true);
							date = dateFormat.parse(entry.getValue());
							
							CustomParameter dbFormat = null;
							if(serverFormat.getName().equalsIgnoreCase("SERVER_TIMESTAMP")) {
								dbFormat = DB_TIMESTAMP;
							}
							else {
								dbFormat = DB_DATEFORMAT;
							}
							entry.setValue(FormaterUtil.formatDateToString(date, 
									dbFormat.getValue()));
							
						} catch (ParseException e) {}		
						
					}
					else if((entry.getKey().endsWith(("Dates")))) {
						String[] dates = entry.getValue().split("#");
						for(int i = 0; i < dates.length; i++){
							if(dates[i].length()>10) {
								serverFormat = SERVER_TIMESTAMP;
							}
							else {
								serverFormat = SERVER_DATEFORMAT;
							}
							SimpleDateFormat dateFormat = new SimpleDateFormat(serverFormat.getValue(),
									new Locale("en", "US"));
							Date date;
							try {
								dateFormat.setLenient(true);
								date = dateFormat.parse(dates[i]);
								
								CustomParameter dbFormat = null;
								if(serverFormat.getName().equalsIgnoreCase("SERVER_TIMESTAMP")) {
									dbFormat = DB_TIMESTAMP;
								}
								else {
									dbFormat = DB_DATEFORMAT;
								}
								dates[i] = FormaterUtil.formatDateToString(date, 
										dbFormat.getValue());
								
							} catch (ParseException e) {}		
						}
						
						String value= ""; 
						for(int i = 0; i < dates.length; i++) {
							if((i == (dates.length - 1))) {
								value += dates[i];
							}
							else {
								value += dates[i] + "#";
							}
						}
						entry.setValue(value);
					}
					else{
						try {
							Integer i = Integer.parseInt(entry.getValue());
							
							NumberFormat nf = NumberFormat.getInstance();
					        DecimalFormat df = (DecimalFormat) nf;
					        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
					        
					        dfs.setZeroDigit('\u0030');
					        
					        df.setDecimalFormatSymbols(dfs);
							entry.setValue(df.format(i));
							
						} catch (NumberFormatException nfe) {}
					}
				}
				this.parameters = localParameters;
			}
		}
		else {
			this.parameters = parameters;
		}
	}

	//---------------------Added by vikas & dhananjay--------------------------------
	public String getParameter(String key){
		
		if((key != null) & (!key.isEmpty())){
			if(parameters.containsKey(key)){
				return parameters.get(key);						
			}
		}
		return null;
	}	
	
	@Override
    public Session persist() {
		this.addSessionDraft();
		Session session = (Session) super.persist();
        return session;
	}
	
	public Session simpleMerge() {
		if(this.getDrafts()==null || this.getDrafts().isEmpty()) {
			Session dbSession = Session.findById(Session.class, this.getId());
			this.setDrafts(dbSession.getDrafts());
		}
        Session q = (Session) super.merge();
        return q;
    }
	
	@Override
    public Session merge() {		
		if(this.getId()!=null && (this.getDrafts()==null || this.getDrafts().isEmpty())) {
			Session dbSession = Session.findById(Session.class, this.getId());
			this.setDrafts(dbSession.getDrafts());
		}
		this.addSessionDraft();
		Session session = (Session) super.merge();
        return session;
	}
	
	/**
     * Adds the session draft.
     */
    private void addSessionDraft() {
    	SessionDraft draft = new SessionDraft();
    	draft.setLocale(this.getLocale());
    	
    	draft.setEditedAs(this.getEditedAs());
        draft.setEditedBy(this.getEditedBy());
        draft.setEditedOn(this.getEditedOn());
        
        draft.setHouse(this.getHouse());
        draft.setYear(this.getYear());
        draft.setType(this.getType());
        draft.setPlace(this.getPlace());
        draft.setNumber(this.getNumber());
        
        draft.setTentativeStartDate(this.getTentativeStartDate());
        draft.setTentativeEndDate(this.getTentativeEndDate());
        draft.setStartDate(this.getStartDate());
        draft.setEndDate(this.getEndDate());
        
        draft.setDeviceTypesEnabled(this.getDeviceTypesEnabled());
        draft.setParameters(this.getParametersWithoutFormatting());
        
        if(this.getId() != null) {
            Session session = Session.findById(Session.class, this.getId());
            List<SessionDraft> originalDrafts = session.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<SessionDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<SessionDraft> originalDrafts = new ArrayList<SessionDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
    
}