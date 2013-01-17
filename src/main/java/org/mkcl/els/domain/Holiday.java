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
    
    public static List<Date> findAllSecondAndForthSaturdayHolidaysInYear(final Integer year) {    
    	List<Date> dates = new ArrayList<Date>();
		try {
			return getHolidayRepository().findAllSecondAndForthSaturdaysInYear(year);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dates;
	}
    
    public static List<Date> findAllSundayHolidaysInYear(final Integer year) {    	
		return FormaterUtil.findAllSundaysInYear(year);
	}
    
    public static List<Date> findAllHolidayDatesByYear(final Integer year, String locale) {  
    	List<Date> holidayDates = new ArrayList<Date>();
    	
    	List<Date> saturdays = Holiday.findAllSecondAndForthSaturdayHolidaysInYear(year);
    	holidayDates.addAll(saturdays);
        
    	List<Date> sundays = Holiday.findAllSundayHolidaysInYear(year);
    	holidayDates.addAll(sundays);
    	
    	List<Holiday> masterHolidays = Holiday.findAllByYear(year, locale);
    	
    	for(Holiday i: masterHolidays) {
    		holidayDates.add(i.getDate());
    	}
    	
    	Collections.sort(holidayDates);
		return holidayDates;
	}

    public static Boolean isHolidayOnDate(Date date, String locale) {		
		if(date != null) {	
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",new Locale(locale));
			String dateStr = dateFormat.format(date);	
			try {
				date = dateFormat.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(date);  
	        Integer year = calendar.get(Calendar.YEAR);
	        List<Date> allHolidayDates = Holiday.findAllHolidayDatesByYear(year, locale);
	    	for(Date i: allHolidayDates) {
	    		if(date.compareTo(i)==0) {
	    			return true;
	    		}
	    	}
	    	return false;
		}
		else {
			return null;
		}		
	}
    
    public static Boolean isHolidayOnSecondOrForthSaturday(Date date, String locale) {		
		if(date != null) {	
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",new Locale(locale));
			String dateStr = dateFormat.format(date);	
			try {
				date = dateFormat.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(date);  
	        Integer year = calendar.get(Calendar.YEAR);
	        List<Date> allHolidayDates = Holiday.findAllSecondAndForthSaturdayHolidaysInYear(year);
	        for(Date i: allHolidayDates) {
	    		if(date.compareTo(i)==0) {
	    			return true;
	    		}
	    	}
	    	return false;
		}
		else {
			return null;
		}		
	} 
    
    public static Date getLastWorkingDateFrom(Calendar dateField, Date fromDate, int difference, String locale) {
    	if(fromDate != null) { 
    		dateField.setTime(fromDate); 
    		if(difference != 0){     			
    			dateField.add(Calendar.DATE, difference);
    		}
			
    		Date checkDate = dateField.getTime();
    		dateField.setTime(checkDate); 
	        SimpleDateFormat sf=new SimpleDateFormat("EEEE");
			while(Holiday.isHolidayOnDate(checkDate, locale)){
    			if(sf.format(checkDate).equals("Monday")){
    				dateField.setTime(checkDate);
    				dateField.add(Calendar.DATE, -2);
    				checkDate=dateField.getTime();
    				if(Holiday.isHolidayOnSecondOrForthSaturday(checkDate, locale)){
    					dateField.setTime(checkDate);
        				dateField.add(Calendar.DATE, -1);
        				checkDate=dateField.getTime();
    				}
    			} else if(sf.format(checkDate).equals("Sunday")){
    				dateField.setTime(checkDate);
    				dateField.add(Calendar.DATE, -1);
    				checkDate=dateField.getTime();
    				if(Holiday.isHolidayOnSecondOrForthSaturday(checkDate, locale)){
    					dateField.setTime(checkDate);
        				dateField.add(Calendar.DATE, -1);
        				checkDate=dateField.getTime();
    				}
    			} else {
    				dateField.setTime(checkDate);
    				dateField.add(Calendar.DATE, -1);
    				checkDate=dateField.getTime();
    			}    			
    		}
			return checkDate;
    	} 
    	else {
    		return null;
    	}
    }
}
