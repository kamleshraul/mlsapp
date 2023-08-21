package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "calendar_events_per_devicetype")
//@JsonIgnoreProperties(value={"houseType","houseType","referenceType"},ignoreUnknown=true)
public class CalendarEventsPerDeviceType extends BaseDomain implements Serializable{

	
	/** Device Type */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;
	
	
	private String parameterkey;


	public DeviceType getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}


	public String getParameterkey() {
		return parameterkey;
	}


	public void setParameterkey(String parameterkey) {
		this.parameterkey = parameterkey;
	}
	
	
}
