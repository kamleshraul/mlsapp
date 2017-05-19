package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "exceptional_holidays")
public class ExceptionalHoliday extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------//
	/** The Constant serialVersionUID. */
	private static final transient long serialVersionUID = 1L;

	@Transient
	private Integer year;

	@Temporal(TemporalType.DATE)
	private Date date;
	
	/** The comma separated day working scope types (from DayWorkingScope Master). */
    @Column(length=1000)
    private String dayWorkingScopes;

    // ---------------------------------Constructors-----------------------------------------------
	public ExceptionalHoliday() {
		super();		
	}
	
	// ---------------------------------Domain Methods-----------------------------------------------
	public static List<ExceptionalHoliday> findAllByYear(final Integer year, final String dayWorkingScopeType, final String locale) {
		List<ExceptionalHoliday> holidaysFromMasterInYear = new ArrayList<ExceptionalHoliday>();
		List<ExceptionalHoliday> allHolidaysFromMaster = ExceptionalHoliday.findAll(ExceptionalHoliday.class, "date", "asc", locale);
		if(allHolidaysFromMaster != null) {
			for(ExceptionalHoliday i: allHolidaysFromMaster) {
				if(i.getYear().equals(year)) { //match year			
					if(dayWorkingScopeType!=null && !dayWorkingScopeType.isEmpty()
							&& i.isApplicableInDayWorkingScope(dayWorkingScopeType)) { //check if given day working scope is applicable for the exceptional holiday
						holidaysFromMasterInYear.add(i);
					}					
				}
			}
		}
		return holidaysFromMasterInYear;
	}
	
	public Boolean isApplicableInDayWorkingScope(final String dayWorkingScopeType) {
		Boolean isApplicableInDayWorkingScope = false;
		
		for(String applicableScopeType: this.getDayWorkingScopes().split(",")) {
			if(dayWorkingScopeType.equals(applicableScopeType)) {
				isApplicableInDayWorkingScope = true;
				break;
			}
		}
		
		return isApplicableInDayWorkingScope;
	}

	// --------------------------------Getters & Setters--------------------------------------------
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
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDayWorkingScopes() {
		return dayWorkingScopes;
	}

	public void setDayWorkingScopes(String dayWorkingScopes) {
		this.dayWorkingScopes = dayWorkingScopes;
	}

}