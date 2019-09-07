/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Reference.java
 * Created On: Sept 16, 2017
 */
package org.mkcl.els.common.vo;

/**
 * The Class NotificationVO.
 * 
 * @author dhananjayb
 * @version v1.0.0
 */
public class NotificationVO {

	// ---------------------------------Attributes-------------------------------------------------
	private long id;
	
	private long pushMessageId;

	private String title;

	private String message; //can be fetched at client side if not shown initially
	
	private String sender;
	
	private String senderName;
	
	private String receiver;
	
	private String receivers; //in case of many receivers present

	private String sentOn;
	
	private String formattedSentOn;

	private String receivedOn; //can be set at client side
	
	private String formattedReceivedOn;
	
	private boolean markedAsReadByReceiver; //can be set at client side
	
	private boolean clearedByReceiver; //to be set at client side

	// ---------------------------------Constructors-----------------------------------------------
	/**
	 * Instantiates a new notification VO.
	 */
	public NotificationVO() {
		super();
	}

	public NotificationVO(long id, 
						long pushMessageId, 
						String title,
						String message, 
						String sender, 
						String senderName,
						String receiver, 
						String receivers,
						String sentOn, 
						String formattedSentOn, 
						String receivedOn,
						String formattedReceivedOn, 
						boolean markedAsReadByReceiver,
						boolean clearedByReceiver) {
		//super();
		this.id = id;
		this.pushMessageId = pushMessageId;
		this.title = title;
		this.message = message;
		this.sender = sender;
		this.senderName = senderName;
		this.receiver = receiver;
		this.receivers = receivers;
		this.sentOn = sentOn;
		this.formattedSentOn = formattedSentOn;
		this.receivedOn = receivedOn;
		this.formattedReceivedOn = formattedReceivedOn;
		this.markedAsReadByReceiver = markedAsReadByReceiver;
		this.clearedByReceiver = clearedByReceiver;
	}

	// ------------------------------------------Getters/Setters-----------------------------------
	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public long getPushMessageId() {
		return pushMessageId;
	}

	public void setPushMessageId(long pushMessageId) {
		this.pushMessageId = pushMessageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	public String getSentOn() {
		return sentOn;
	}

	public void setSentOn(String sentOn) {
		this.sentOn = sentOn;
	}

	public String getFormattedSentOn() {
		return formattedSentOn;
	}

	public void setFormattedSentOn(String formattedSentOn) {
		this.formattedSentOn = formattedSentOn;
	}

	public String getReceivedOn() {
		return receivedOn;
	}

	public void setReceivedOn(String receivedOn) {
		this.receivedOn = receivedOn;
	}

	public String getFormattedReceivedOn() {
		return formattedReceivedOn;
	}

	public void setFormattedReceivedOn(String formattedReceivedOn) {
		this.formattedReceivedOn = formattedReceivedOn;
	}

	/*public boolean isMarkedAsReadByReceiver() {
		return markedAsReadByReceiver;
	}*/
	
	public boolean getMarkedAsReadByReceiver() {
		return markedAsReadByReceiver;
	}

	public void setMarkedAsReadByReceiver(boolean markedAsReadByReceiver) {
		this.markedAsReadByReceiver = markedAsReadByReceiver;
	}

	/*public boolean isClearedByReceiver() {
		return clearedByReceiver;
	}*/
	
	public boolean getClearedByReceiver() {
		return clearedByReceiver;
	}

	public void setClearedByReceiver(boolean clearedByReceiver) {
		this.clearedByReceiver = clearedByReceiver;
	}
	
}
