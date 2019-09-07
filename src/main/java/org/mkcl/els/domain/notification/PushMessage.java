package org.mkcl.els.domain.notification;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.domain.BaseDomain;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class PushMessage.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity(name = "org.mkcl.els.domain.notification.pushmessage")
@Table(name = "push_messages")
public class PushMessage extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The notification title. 
     *  optional to be set for notifications having long messages.
     */
    @Column(length=10000)
    private String title;

    /** The notification message. */
    @Column(length=30000)
    private String message;
    
    /** The sender of notification. */
    @Column(length=100)
    private String sender;
    
    /** The sender's name. */
    @Column(length=300)
    private String senderName;
    
    /** The receivers of notification. (decide later if multiple receivers can be required) 
     *  Comma separated list of usernames of the receivers
     *  If not set, the notification will be sent to all the active users
     */
    @Column(length=10000)
    private String receivers;
    
    /** The sent time of notification. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date sentOn;
	
	@Column
	private Long time;
	
	/** The 'volatile' flag for push notification which need not be saved for receivers. */
	@Column
	private Boolean isVolatile;
	
	// ----------------------------Getters/Setters------------------------//
	/**
	 * @return the title
	 */
	public String getTitle() {
		if(title!=null && !title.isEmpty()) { //check as it's optional to be set for long messages
			return title;
		} else {
			return message;
		}
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * @return the receivers
	 */
	public String getReceivers() {
		return receivers;
	}

	/**
	 * @param receivers the receivers to set
	 */
	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	/**
	 * @return the sentOn
	 */
	public Date getSentOn() {
		return sentOn;
	}

	/**
	 * @param sentOn the sentOn to set
	 */
	public void setSentOn(Date sentOn) {
		this.sentOn = sentOn;
	}

	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * @param l the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	/**
	 * @return the isVolatile
	 */
	public Boolean getIsVolatile() {
		return isVolatile;
	}

	/**
	 * @param isVolatile the isVolatile to set
	 */
	public void setIsVolatile(Boolean isVolatile) {
		this.isVolatile = isVolatile;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isVersionMismatch() {
		return false;
	}

}