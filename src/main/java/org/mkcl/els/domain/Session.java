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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.util.FormaterUtil;
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
   
    /** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="session_devicetype_config")
	private Map<String,String> parameters;
 

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
     * @since v1.0.0
     */
    public static Session findLatestSession(final HouseType houseType,final Integer sessionYear) {
        return getSessionRepository().findLatestSession(houseType,sessionYear);
    }

    /**
     * Find latest session.
     *
     * @param houseType the house type
     * @return the session
     * @author compaq
     * @since v1.0.0
     */
    public static Session findLatestSession(final HouseType houseType) {
        return getSessionRepository().findLatestSession(houseType);
    }

    /**
     * Find sessions by house and year.
     *
     * @param house the house
     * @param year the year
     * @return the list< session>
     * @author compaq
     * @since v1.0.0
     */
    public static List<Session> findSessionsByHouseAndYear(final House house,final Integer year){
        return getSessionRepository().findSessionsByHouseAndYear(house, year);
    }

    /**
     * Find session by house session type year.
     *
     * @param house the house
     * @param sessionType the session type
     * @param sessionYear the session year
     * @return the session
     * @author compaq
     * @since v1.0.0
     */
    public static Session findSessionByHouseSessionTypeYear(final House house,
			final SessionType sessionType, final Integer sessionYear) {
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
     * @since v1.0.0
     */
    public static Session findSessionByHouseTypeSessionTypeYear(final HouseType houseType,
            final SessionType sessionType, final Integer sessionYear) {
        return getSessionRepository().findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
    }

    public static List<Session> findSessionsByHouseTypeAndYear(
			final HouseType houseType, final Integer sessionYear) {
		return getSessionRepository().findSessionsByHouseTypeAndYear(houseType,sessionYear);
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
	 * Sets the session repository.
	 *
	 * @param sessionRepository the new session repository
	 */
	public void setSessionRepository(final SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}
	
	public Map<String, String> getParameters() {
		CustomParameter SERVER_TIMESTAMP = CustomParameter.findByName(CustomParameter.class, 
				"SERVER_TIMESTAMP", "");
		CustomParameter SERVER_DATEFORMAT = CustomParameter.findByName(CustomParameter.class, 
				"SERVER_DATEFORMAT", "");
		CustomParameter DB_DATEFORMAT = CustomParameter.findByName(CustomParameter.class, 
				"DB_DATEFORMAT", "");
		CustomParameter DB_TIMESTAMP = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		
		//to get the date parameters formatted in current locale
		if(! this.getLocale().equalsIgnoreCase("en_US")) {	
			Map<String, String> localParameters = this.parameters;
			if((localParameters != null) & (!(localParameters.isEmpty()))) {
				for (Map.Entry<String, String> entry : localParameters.entrySet()) {
					CustomParameter dbFormat = null;
					if((entry.getKey().endsWith(("Date")))) {
						if(entry.getValue().length() > 10) {
							dbFormat = DB_TIMESTAMP;
						}
						else {
							dbFormat = DB_DATEFORMAT;
						}
						
						SimpleDateFormat dateFormat;
						Date date;
						try {
							if (this.getLocale().equalsIgnoreCase("mr_IN")) {
								dateFormat = new SimpleDateFormat(dbFormat.getValue(), 
										new Locale("hi", "IN"));
							} 
							else {
								dateFormat = new SimpleDateFormat(dbFormat.getValue(), 
										new Locale(this.getLocale()));
							}
							dateFormat.setLenient(true);
							date = dateFormat.parse(entry.getValue());
							
							CustomParameter serverFormat = null;
							if(dbFormat.getName().equalsIgnoreCase("DB_TIMESTAMP")) {
								serverFormat = SERVER_TIMESTAMP;
							}
							else {
								serverFormat = SERVER_DATEFORMAT;
							}
							entry.setValue(FormaterUtil.formatDateToString(date, 
									serverFormat.getValue()));
						} catch (ParseException e) {}
						
					}
					else if((entry.getKey().endsWith(("Dates")))) {
						String[] dates = entry.getValue().split("#");
						for(int i = 0; i < dates.length; i++) {
							if(dates[i].length() > 10){
								dbFormat = DB_TIMESTAMP;
							}
							else{
								dbFormat = DB_DATEFORMAT;
							}		
							SimpleDateFormat dateFormat;
							Date date;
							try {
								if (this.getLocale().equalsIgnoreCase("mr_IN")) {
									dateFormat = new SimpleDateFormat(dbFormat.getValue(), 
											new Locale("hi", "IN"));
								} else {
									dateFormat = new SimpleDateFormat(dbFormat.getValue(), 
											new Locale(this.getLocale()));
								}
								dateFormat.setLenient(true);
								date = dateFormat.parse(dates[i]);
								
								CustomParameter serverFormat = null;
								if(dbFormat.getName().equalsIgnoreCase("DB_TIMESTAMP")) {
									serverFormat = SERVER_TIMESTAMP;
								}
								else {
									serverFormat = SERVER_DATEFORMAT;
								}
								dates[i] = FormaterUtil.formatDateToString(date, 
										serverFormat.getValue());
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
					else {
						try {
							Integer i = Integer.parseInt(entry.getValue());
							
							NumberFormat nf = NumberFormat.getInstance();
					        DecimalFormat df = (DecimalFormat) nf;
					        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
					        
					        if(this.getLocale().toString().equals("en_US")) {
					            dfs.setZeroDigit('\u0030');
					        }
					        else if(this.getLocale().toString().equals("mr_IN")) {
					            dfs.setZeroDigit('\u0966');
					        }
					        df.setDecimalFormatSymbols(dfs);
							entry.setValue(df.format(i));
							
						} catch (NumberFormatException nfe) {}
					}
				}
				return localParameters;
			}
		}
		return parameters;
	}


	public void setParameters(Map<String, String> parameters) {
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

	public String getParamater(String key){
		
		if((key != null) & (!key.isEmpty())){
			if(parameters.containsKey(key)){
				return parameters.get(key);						
			}
		}
		return null;
	}

	public static Session find(final Integer sessionyear, final String sessiontype, final String housetype) {
		return getSessionRepository().find(sessionyear,sessiontype,housetype);
    }
 }