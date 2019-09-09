/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Notification.java
 * Created On: Sept 16, 2017
 */
package org.mkcl.els.domain.notification;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.NotificationVO;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Notification.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "notifications")
public class Notification extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** Push Message of notification */
	@ManyToOne
	@JoinColumn(name="pushmessage_id")
	private PushMessage pushMessage;
    
    /** The receiver of notification. */
    @Column(length=100)
    private String receiver;
    
    /** The receiver's name. */
    @Column(length=300)
    private String receiverName;
	
	/** The received time of notification. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date receivedOn;
	
	/** The 'marked_as_read' flag for notification. */
	@Column
	private Boolean markedAsReadByReceiver;
	
	/** The 'cleared' flag for notification as it cannot be deleted. */
	@Column
	private Boolean clearedByReceiver;
	
	@Autowired
	private transient NotificationRepository notificationRepository;
	

    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new notification.
     */
    public Notification() {
        super();
    }
    

	// ----------------------------Domain Methods-------------------------//
    public static NotificationRepository getNotificationRepository() {
        NotificationRepository notificationRepository = new Notification().notificationRepository;
        if (notificationRepository == null) {
            throw new IllegalStateException(
                    "NotificationRepository has not been injected in Notification Domain");
        }
        return notificationRepository;
    }
    
    public static int findActiveNotificationsCountForCurrentUser(final String receiver, final String locale) {
    	return getNotificationRepository().findActiveNotificationsCountForCurrentUser(receiver, locale);
    }
    
    public static List<Notification> fetchNotificationsForCurrentUserInRange(final String receiver, final String locale, final int beginCount, final int limit) {
    	return getNotificationRepository().findNotificationsForCurrentUserInRange(receiver, locale, beginCount, limit);
    }
    
    public static List<NotificationVO> fetchNotificationsVOForCurrentUserInRange(final String receiver, final String locale, final int beginCount, final int limit) {
    	return getNotificationRepository().findNotificationsVOForCurrentUserInRange(receiver, locale, beginCount, limit);
    }
    
    public static List<Notification> fetchAllNotificationsForCurrentUser(final String receiver, final String locale) {
    	return getNotificationRepository().findAllNotificationsForCurrentUser(receiver, locale);
    }
    
    public static List<NotificationVO> fetchAllNotificationsVOForCurrentUser(final String receiver, final String locale) {
    	return getNotificationRepository().findAllNotificationsVOForCurrentUser(receiver, locale);
    }
    
    public static Notification findByPushMessageAtReceiver(final PushMessage pushMessage, final String receiver, final String locale) {
    	if(pushMessage!=null && pushMessage.getId()!=null && receiver!=null && !receiver.isEmpty()) {
    		List<Notification> notificationsOfPushMessage = Notification.findAllByFieldName(Notification.class, "pushMessage", pushMessage, "id", ApplicationConstants.ASC, locale);
        	if(notificationsOfPushMessage!=null && !notificationsOfPushMessage.isEmpty()) {
        		for(Notification notification: notificationsOfPushMessage) {
        			if(notification.getReceiver().equals(receiver)) {
        				return notification;
        			}
        		}
        	}
    	}    	
    	return null;
    }
    
    public static NotificationVO populateNotificationVO(final Notification notification) {
    	return getNotificationRepository().populateNotificationVO(notification);
    }

    // ----------------------------Getters/Setters------------------------//
	/**
	 * @return the pushMessage
	 */
	public PushMessage getPushMessage() {
		return pushMessage;
	}

	/**
	 * @param pushMessage the pushMessage to set
	 */
	public void setPushMessage(PushMessage pushMessage) {
		this.pushMessage = pushMessage;
	}

	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiverName() {
		return receiverName;
	}


	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}


	/**
	 * @return the receivedOn
	 */
	public Date getReceivedOn() {
		return receivedOn;
	}

	/**
	 * @param receivedOn the receivedOn to set
	 */
	public void setReceivedOn(Date receivedOn) {
		this.receivedOn = receivedOn;
	}

	/**
	 * @return the markedAsReadByReceiver
	 */
	public Boolean getMarkedAsReadByReceiver() {
		return markedAsReadByReceiver;
	}

	/**
	 * @param markedAsReadByReceiver the markedAsReadByReceiver to set
	 */
	public void setMarkedAsReadByReceiver(Boolean markedAsReadByReceiver) {
		this.markedAsReadByReceiver = markedAsReadByReceiver;
		
		/** set received on to the timestamp when user first time marks the message as read **/
		if(markedAsReadByReceiver!=null && markedAsReadByReceiver.booleanValue()==true) {
			if(this.getReceivedOn()==null) {
				this.setReceivedOn(new Date());
			}
		}
	}

	/**
	 * @return the clearedByReceiver
	 */
	public Boolean getClearedByReceiver() {
		return clearedByReceiver;
	}

	/**
	 * @param clearedByReceiver the clearedByReceiver to set
	 */
	public void setClearedByReceiver(Boolean clearedByReceiver) {
		this.clearedByReceiver = clearedByReceiver;
	}
    
}