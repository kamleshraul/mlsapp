package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="referenced_units")
public class ReferenceUnit extends BaseDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long device;
	
	private Integer number;
		
	private String deviceType;
	
	private String deviceTypeId;
		
	private String deviceName;
	
	private Long sessionId;
	
	private String sessionTypeName;
	
	private Integer sessionYear;
	
	private String houseType;
	
	private String houseTypeName;
	
	private String yaadiDate;
	
	private String yaadiNumber;
	
	private Integer position;
	
	private String internalStatus;
	
	private String internalStatusName;
	
	private String recommendationStatus;
	
	private String recommendationStatusName;
	
	private String status;
	
	private String statusName;
	

	public ReferenceUnit() {
		super();
	}

	public Long getDevice() {
		return device;
	}

	public void setDevice(Long device) {
		this.device = device;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public String getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionTypeName() {
		return sessionTypeName;
	}

	public void setSessionTypeName(String sessionTypeName) {
		this.sessionTypeName = sessionTypeName;
	}

	public Integer getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(Integer sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getHouseTypeName() {
		return houseTypeName;
	}

	public void setHouseTypeName(String houseTypeName) {
		this.houseTypeName = houseTypeName;
	}

	public String getYaadiDate() {
		return yaadiDate;
	}

	public void setYaadiDate(String yaadiDate) {
		this.yaadiDate = yaadiDate;
	}

	public String getYaadiNumber() {
		return yaadiNumber;
	}

	public void setYaadiNumber(String yaadiNumber) {
		this.yaadiNumber = yaadiNumber;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	
	public String getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(String internalStatus) {
		this.internalStatus = internalStatus;
	}

	public String getInternalStatusName() {
		return internalStatusName;
	}

	public void setInternalStatusName(String internalStatusName) {
		this.internalStatusName = internalStatusName;
	}

	public String getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(String recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRecommendationStatusName() {
		return recommendationStatusName;
	}

	public void setRecommendationStatusName(String recommendationStatusName) {
		this.recommendationStatusName = recommendationStatusName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public static String getReferences(List<ReferenceUnit> refs){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < refs.size(); i++){
			sb.append(refs.get(i).getId());
			if(i < (refs.size() - 1)){
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
	
	public String formatNumber(Integer number){
		return FormaterUtil.formatNumberNoGrouping(number, super.getLocale());
	}
}
