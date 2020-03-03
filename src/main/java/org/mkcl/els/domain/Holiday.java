package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "holidays")
public class Holiday extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------//
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	@Transient
	private Integer year;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(length=150)
	private String type;

	@Column(length=600)
	private String name;
	
	@Autowired
	private transient HolidayRepository holidayRepository;


	// ---------------------------------Constructors-----------------------------------------------
	public Holiday() {
		super();
	}	

	public Holiday(Date date, String name, Integer year) {
		super();
		this.date = date;
		this.name = name;
		this.year = year;
	}


	// --------------------------------Getters & Setters--------------------------------------------

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		if(this.date != null) {
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(this.date);  
			return calendar.get(Calendar.YEAR);
		}
		else {
			return null;
		}		
	}

	/**
	 * Gets the holiday repository.
	 * 
	 * @return the holiday repository
	 */
	public static HolidayRepository getHolidayRepository() {
		final HolidayRepository repository = new Holiday().holidayRepository;
		if (repository == null) {
			throw new IllegalStateException(
			"HolidayRepository has not been injected");
		}
		return repository;
	}


	// ---------------------------------------Domain_Methods---------------------------------------

	public static List<Holiday> findAllByYear(final Integer year, String locale) {    	
		return getHolidayRepository().findAllByYear(year, locale);
	}

	/*public static List<Date> findAllSecondAndForthSaturdayHolidaysInYear(final Integer year) {    
		List<Date> dates = new ArrayList<Date>();
		try {
			return getHolidayRepository().findAllSecondAndForthSaturdaysInYear(year);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dates;
	}*/

	public static List<Date> findAllSundayHolidaysInYear(final Integer year) {    	
		return FormaterUtil.findAllSundaysInYear(year);
	}
	
	public static List<Date> findAllSaturdayHolidaysInYear(final Integer year) {    	
		return FormaterUtil.findAllSaturdayHolidaysInYear(year);
	}
	
	public static List<Date> findAllHolidayDatesByYear(final Integer year, String locale) {  
		return findAllHolidayDatesByYear(year, ApplicationConstants.DAY_WORKING_SCOPE_COMMON, locale); //consider common exceptional holidays only
	}
	
	public static List<Date> findAllHolidayDatesByYear(final Integer year, String dayWorkingScopeType, String locale) {  
		List<Date> holidayDates = new ArrayList<Date>();

		/*List<Date> saturdays = Holiday.findAllSecondAndForthSaturdayHolidaysInYear(year);
		holidayDates.addAll(saturdays);*/
		
		List<Date> sundays = Holiday.findAllSundayHolidaysInYear(year);
		holidayDates.addAll(sundays);

		List<Date> saturdays = Holiday.findAllSaturdayHolidaysInYear(year);
		holidayDates.addAll(saturdays);

		List<Holiday> masterHolidays = Holiday.findAllByYear(year, locale);
		for(Holiday i: masterHolidays) {
			holidayDates.add(i.getDate());
		}
		
		List<ExceptionalHoliday> masterExceptionalHolidays = ExceptionalHoliday.findAllByYear(year, dayWorkingScopeType, locale);
		for(ExceptionalHoliday i: masterExceptionalHolidays) {
			holidayDates.add(i.getDate());
		}

		Collections.sort(holidayDates);
		return holidayDates;
	}
	
	public static Boolean isHolidayOnDate(Date date, String locale) {
		return isHolidayOnDate(date, ApplicationConstants.DAY_WORKING_SCOPE_HOUSE_PROCEEDING, locale); //checking if there is holiday applicable for default 'house proceeding' day working scope
	}

	public static Boolean isHolidayOnDate(Date date, String dayWorkingScopeType, String locale) {
		if(date != null) {	
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",new Locale(locale));
			String dateStr = dateFormat.format(date);	
			try {
				date = dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(date);  
			Integer year = calendar.get(Calendar.YEAR);
			List<Date> allHolidayDates = Holiday.findAllHolidayDatesByYear(year, dayWorkingScopeType, locale);
			List<ExceptionalWorkingDay> masterExceptionalWorkingDays = ExceptionalWorkingDay.findAllByYear(year, dayWorkingScopeType, locale);
			for(Date i: allHolidayDates) {
				if(date.compareTo(i)==0) { //check if given date matches present holiday date
					Boolean isExceptionalWorkingDayOnHolidayDate = false;
					for(ExceptionalWorkingDay wd: masterExceptionalWorkingDays) {
						if(date.compareTo(wd.getDate())==0) { //check if given date matches present exceptional working day date
							isExceptionalWorkingDayOnHolidayDate = true;
							break;
						}
					}
					if(!isExceptionalWorkingDayOnHolidayDate) {
						return true;
					}					
				}
			}
			return false;
		}
		else {
			return null;
		}		
	}
	
	public static Date getLastWorkingDateFrom(Date fromDate, int difference, String locale) {
		return getLastWorkingDateFrom(fromDate, difference, ApplicationConstants.DAY_WORKING_SCOPE_HOUSE_PROCEEDING, locale);
	}    
	
	public static Date getLastWorkingDateFrom(Date fromDate, int difference, String dayWorkingScopeType, String locale) {
		if(difference>0) {
			difference = -difference;
		}
		if(fromDate != null) {
			Calendar dateField = Calendar.getInstance();    		
			dateField.setTime(fromDate); 
			if(difference != 0){     			
				dateField.add(Calendar.DATE, difference);
			}			
			Date checkDate = dateField.getTime();    		
			while(Holiday.isHolidayOnDate(checkDate, dayWorkingScopeType, locale)){
				dateField.setTime(checkDate);
				dateField.add(Calendar.DATE, -1);
				checkDate=dateField.getTime();    			
			}
			return checkDate;
		} 
		else {
			return null;
		}
	}

	public static Date getNextWorkingDateFrom(Date fromDate, int difference, String locale) {
		return getNextWorkingDateFrom(fromDate, difference, ApplicationConstants.DAY_WORKING_SCOPE_HOUSE_PROCEEDING, locale);
	}
	
	public static Date getNextWorkingDateFrom(Date fromDate, int difference, String dayWorkingScopeType, String locale) {
		if(fromDate != null) {
			Calendar dateField = Calendar.getInstance();    		
			dateField.setTime(fromDate); 
			if(difference != 0){     			
				dateField.add(Calendar.DATE, difference);
			}			
			Date checkDate = dateField.getTime();
			while(Holiday.isHolidayOnDate(checkDate, dayWorkingScopeType, locale)){
				dateField.setTime(checkDate);
				dateField.add(Calendar.DATE, 1);
				checkDate=dateField.getTime();    			
			}
			return checkDate;
		} 
		else {
			return null;
		}
	}
}
