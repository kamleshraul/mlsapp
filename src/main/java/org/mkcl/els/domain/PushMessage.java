/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.PushMessage.java
 * Created On: May 14, 2014
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class PushMessage.
 *
 * @author Vikas
 * @since v1.0.0
 */

@Configurable
@Entity(name = "org.mkcl.els.domain.pushmessage")
@Table(name="pushmessages")
@JsonIgnoreProperties({"senderUserGroupType","senderUserGroup",
	"recepientUserGroupType","recepientUserGroup","sessionType","houseType","sessionYear","device","deviceType",
	"isRead","priority"})
public class PushMessage extends BaseDomain implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	
	@Column(length=3000)
	private String senderName;
	
	private String senderUserName;
	
	private String senderUserGroup;
	
	private String senderUserGroupType;
	
	@Column(length=3000)
	private String recepientName;
	
	private String recepientUserName;
	
	private String recepientUserGroup;
	
	private String recepientUserGroupType;
	
	@Column(length=8000)
	private String message;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date readDate;
	
	private String sessionType;
	
	private String houseType;
	
	private Integer sessionYear;
		
	private String device;
	
	private String deviceType;
	
	private String deviceNumber;
	
	private Boolean isRead;
	
	private Integer priority;

	public PushMessage() {
		super();
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderUserGroupType() {
		return senderUserGroupType;
	}

	public void setSenderUserGroupType(String senderUserGroupType) {
		this.senderUserGroupType = senderUserGroupType;
	}

	public String getRecepientName() {
		return recepientName;
	}

	public void setRecepientName(String recepientName) {
		this.recepientName = recepientName;
	}

	public String getRecepientUserGroupType() {
		return recepientUserGroupType;
	}

	public void setRecepientUserGroupType(String recepientUserGroupType) {
		this.recepientUserGroupType = recepientUserGroupType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	
	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public Integer getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(Integer sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSenderUserName() {
		return senderUserName;
	}

	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}

	public String getSenderUserGroup() {
		return senderUserGroup;
	}

	public void setSenderUserGroup(String senderUserGroup) {
		this.senderUserGroup = senderUserGroup;
	}

	public String getRecepientUserName() {
		return recepientUserName;
	}

	public void setRecepientUserName(String recepientUserName) {
		this.recepientUserName = recepientUserName;
	}

	public String getRecepientUserGroup() {
		return recepientUserGroup;
	}

	public void setRecepientUserGroup(String recepientUserGroup) {
		this.recepientUserGroup = recepientUserGroup;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
