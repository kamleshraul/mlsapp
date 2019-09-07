package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.NotificationVO;
import org.mkcl.els.domain.notification.Notification;
import org.mkcl.els.domain.notification.PushMessage;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository extends BaseRepository<Notification, Serializable> {
	
	public int findActiveNotificationsCountForCurrentUser(final String receiver, final String locale) {
		String queryString = "SELECT COUNT(n.id) FROM Notification n "
				+ "WHERE n.receiver=:receiver "
				+ "AND (n.markedAsReadByReceiver IS NULL OR n.markedAsReadByReceiver IS FALSE) "
				+ "AND (n.clearedByReceiver IS NULL OR n.clearedByReceiver IS FALSE) "
				+ "AND n.locale=:locale";
		
		Query query = this.em().createQuery(queryString);	
		query.setParameter("receiver", receiver);
    	query.setParameter("locale", locale);
    	
    	Long activeNotificationsCount = (Long) query.getSingleResult();
    	
    	return activeNotificationsCount.intValue();
	}

    public List<Notification> findNotificationsForCurrentUserInRange(final String receiver, final String locale, final int beginCount, final int limit) {
    	String queryString = "SELECT n FROM Notification n JOIN FETCH n.pushMessage msg "
    					+ "WHERE n.receiver=:receiver "
    					+ "AND (n.clearedByReceiver IS NULL OR n.clearedByReceiver IS FALSE) "
    					+ "AND n.locale=:locale "
    					+ "ORDER BY msg.sentOn DESC";
    	
    	TypedQuery<Notification> query = this.em().createQuery(queryString, Notification.class);
    	query.setParameter("receiver", receiver);
    	query.setParameter("locale", locale);
    	query.setFirstResult(beginCount);
    	query.setMaxResults(limit);
    	
    	List<Notification> activeNotificationsForCurrentUserInRange = query.getResultList();
    	
    	return activeNotificationsForCurrentUserInRange;
    }
    
    public List<NotificationVO> findNotificationsVOForCurrentUserInRange(final String receiver, final String locale, final int beginCount, final int limit) {
    	
    	List<Notification> activeNotificationsForCurrentUserInRange = this.findNotificationsForCurrentUserInRange(receiver, locale, beginCount, limit);
    	
    	return populateNotificationListVO(activeNotificationsForCurrentUserInRange);
    }
    
    public List<Notification> findAllNotificationsForCurrentUser(final String receiver, final String locale) {
    	String queryString = "SELECT n FROM Notification n JOIN FETCH n.pushMessage msg "
    					+ "WHERE n.receiver=:receiver "
    					+ "AND (n.clearedByReceiver IS NULL OR n.clearedByReceiver IS FALSE) "
    					+ "AND n.locale=:locale "
    					+ "ORDER BY msg.sentOn DESC";
    	
    	TypedQuery<Notification> query = this.em().createQuery(queryString, Notification.class);
    	query.setParameter("receiver", receiver);
    	query.setParameter("locale", locale);
    	
    	List<Notification> activeNotificationsForCurrentUserInRange = query.getResultList();
    	
    	return activeNotificationsForCurrentUserInRange;
    }
    
    public List<NotificationVO> findAllNotificationsVOForCurrentUser(final String receiver, final String locale) {
    	
    	List<Notification> activeNotificationsForCurrentUserInRange = this.findAllNotificationsForCurrentUser(receiver, locale);
    	
    	return populateNotificationListVO(activeNotificationsForCurrentUserInRange);
    }
    
    public List<NotificationVO> populateNotificationListVO(final List<Notification> notifications) {
    	
		List<NotificationVO> notificationVOs = new ArrayList<NotificationVO>();
    	
    	if(notifications!=null && !notifications.isEmpty()) {    		
    		
    		for(Notification n: notifications) {
    			
    			if(n!=null) {
    				notificationVOs.add(this.populateNotificationVO(n));
    			}
    			
    		}
    		
    	}
    	
    	return notificationVOs;
    }
    
    public NotificationVO populateNotificationVO(final Notification notification) {
    	
    	NotificationVO notificationVO = null;
    	
    	if(notification!=null) {
    		notificationVO = new NotificationVO();
    		PushMessage pm = notification.getPushMessage();
    		notificationVO.setId(notification.getId());
			notificationVO.setPushMessageId(pm.getId());
			
			notificationVO.setTitle(pm.getTitle());
			notificationVO.setMessage(pm.getMessage());
			
			notificationVO.setSender(pm.getSender());
			notificationVO.setSenderName(pm.getSenderName());
			notificationVO.setReceiver(notification.getReceiver());
			notificationVO.setReceivers(pm.getReceivers());
			
			if(pm.getSentOn()!=null) {
				String formattedSentOn = FormaterUtil.formatDateToString(pm.getSentOn(), "yyyy-MM-dd HH:mm:ss");
				notificationVO.setSentOn(formattedSentOn);
			} else {
				notificationVO.setSentOn("");
			}
			notificationVO.setFormattedSentOn("");
			
			if(notification.getReceivedOn()!=null) {
				String formattedReceivedOn = FormaterUtil.formatDateToString(notification.getReceivedOn(), "yyyy-MM-dd HH:mm:ss");
				notificationVO.setReceivedOn(formattedReceivedOn);
			} else {
				notificationVO.setReceivedOn("");
			}
			notificationVO.setFormattedReceivedOn("");
			
			if(notification.getMarkedAsReadByReceiver()!=null) {
				notificationVO.setMarkedAsReadByReceiver(notification.getMarkedAsReadByReceiver());
			} else {
				notificationVO.setMarkedAsReadByReceiver(false);
			}
			
			if(notification.getClearedByReceiver()!=null) {
				notificationVO.setClearedByReceiver(notification.getClearedByReceiver());
			} else {
				notificationVO.setClearedByReceiver(false);
			}
    	}
    	
    	return notificationVO;
    }
	
}
