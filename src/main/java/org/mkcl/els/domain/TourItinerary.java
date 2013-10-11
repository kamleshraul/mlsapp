package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="tour_itineraries")
public class TourItinerary extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -7503179180628328699L;

	//=============== ATTRIBUTES ===============
	@Temporal(TemporalType.DATE)
	private Date date;
	
	private String fromTime;
	
	private String toTime;
	
	@Column(length=30000)
	private String details;
	
	@Column(length=30000)
	private String stayOver;
	
	//=============== CONSTRUCTORS =============
	public TourItinerary() {
		super();
	}
	
	public TourItinerary(final Date date,
			final String fromTime,
			final String toTime,
			final String locale) {
		super(locale);
		this.setDate(date);
		this.setFromTime(fromTime);
		this.setToTime(toTime);
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	public String formatDate(){
		String retVal = "";
		if(this.getDate() != null) {
			String locale = this.getLocale();
			SimpleDateFormat sdf = FormaterUtil.getDateFormatter(locale); 
			retVal = sdf.format(this.getDate());
		}
		return retVal;
	}
	
	//=============== INTERNAL METHODS =========
	
	//=============== GETTERS/SETTERS ==========
	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(final String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(final String toTime) {
		this.toTime = toTime;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(final String details) {
		this.details = details;
	}

	public String getStayOver() {
		return stayOver;
	}

	public void setStayOver(final String stayOver) {
		this.stayOver = stayOver;
	}
	
}