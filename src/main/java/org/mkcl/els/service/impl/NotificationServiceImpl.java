package org.mkcl.els.service.impl;

import java.util.Date;
import java.util.Map;

import org.mkcl.els.atmosphere.NotificationHandler;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.notification.Notification;
import org.mkcl.els.domain.notification.NotificationTemplate;
import org.mkcl.els.domain.notification.PushMessage;
import org.mkcl.els.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("notificationService")
public class NotificationServiceImpl implements INotificationService {
	
	/** The logger. */
    protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean sendNotification(String message, String receivers, String locale) {
		return this.sendNotification("", "", message, receivers, false, locale);		
	}

	@Override
	public boolean sendNotification(String sender, String message, String receivers, String locale) {
		return this.sendNotification(sender, "", message, receivers, false, locale);
	}

	@Override
	public boolean sendNotificationToAllActiveUsers(String message, String locale) {
		return this.sendNotification("", "", message, "all", false, locale);
	}
	
	@Override
	public boolean sendNotificationToAllActiveUsers(String sender, String message, String locale) {
		return this.sendNotification(sender, "", message, "all", false, locale);
	}

	@Override
	public boolean sendVolatileNotification(String message, String receivers, String locale) {
		return this.sendNotification("", "", message, receivers, true, locale);
	}

	@Override
	public boolean sendVolatileNotification(String sender, String message, String receivers, String locale) {
		return this.sendNotification(sender, "", message, receivers, true, locale);
	}

	@Override
	public boolean sendVolatileNotificationToAllActiveUsers(String message, String locale) {
		return this.sendNotification("", "", message, "all", true, locale);
	}
	
	@Override
	public boolean sendVolatileNotificationToAllActiveUsers(String sender, String message, String locale) {
		return this.sendNotification(sender, "", message, "all", true, locale);
	}

	@Override
	public boolean sendNotificationWithTitle(String title, String message, String receivers, String locale) {
		return this.sendNotification("", title, message, receivers, false, locale);
	}

	@Override
	public boolean sendNotificationWithTitle(String sender, String title, String message, String receivers, String locale) {
		return this.sendNotification(sender, title, message, receivers, false, locale);
	}

	@Override
	public boolean sendNotificationWithTitleToAllActiveUsers(String title, String message, String locale) {
		return this.sendNotification("", title, message, "all", false, locale);
	}
	
	@Override
	public boolean sendNotificationWithTitleToAllActiveUsers(String sender, String title, String message, String locale) {
		return this.sendNotification(sender, title, message, "all", false, locale);
	}

	@Override
	public boolean sendVolatileNotificationWithTitle(String title, String message, String receivers, String locale) {
		return this.sendNotification("", title, message, receivers, true, locale);
	}

	@Override
	public boolean sendVolatileNotificationWithTitle(String sender, String title, String message, String receivers, String locale) {
		return this.sendNotification(sender, title, message, receivers, true, locale);
	}

	@Override
	public boolean sendVolatileNotificationWithTitleToAllActiveUsers(String title, String message, String locale) {
		return this.sendNotification("", title, message, "all", true, locale);
	}
	
	@Override
	public boolean sendVolatileNotificationWithTitleToAllActiveUsers(String sender, String title, String message, String locale) {
		return this.sendNotification(sender, title, message, "all", true, locale);
	}
	
	@Override
	public boolean sendNotificationWithTitleUsingTemplate(String templateKey, Map<String, String[]> templateParameters, String locale) {
		NotificationTemplate notificationTemplate = NotificationTemplate.findByFieldName(NotificationTemplate.class, "templateKey", templateKey, locale);
		if(notificationTemplate!=null) {			
			String title = NotificationTemplate.generateNotificationTitle(notificationTemplate, templateParameters);
			String message = NotificationTemplate.generateNotificationMessage(notificationTemplate, templateParameters);
			String receivers = NotificationTemplate.generateNotificationReceivers(notificationTemplate, templateParameters);
			return this.sendNotificationWithTitle(title, message, receivers, locale);
		}
		return false;
	}
	
	@Transactional
	private boolean sendNotification(String sender, String title, String message, String receivers, boolean isVolatile, String locale) {
		try {
			/**** create and save pushmessage ****/
			PushMessage pushMessage = new PushMessage();
			if(sender!=null && !sender.isEmpty()) {				
				pushMessage.setSender(sender);
				User senderUser = User.findByUserName(sender, locale);
				if(senderUser!=null && senderUser.getId()!=null) {
					StringBuffer senderName = new StringBuffer("");
					if(senderUser.getTitle()!=null && !senderUser.getTitle().isEmpty()) {
						senderName.append(senderUser.getTitle());
						senderName.append(" ");
					}
					if(senderUser.getFirstName()!=null && !senderUser.getFirstName().isEmpty()) {
						senderName.append(senderUser.getFirstName());
						senderName.append(" ");
					}
					if(senderUser.getLastName()!=null && !senderUser.getLastName().isEmpty()) {
						senderName.append(senderUser.getLastName());
					}
					
				} else {
					pushMessage.setSenderName(sender);
				}
			} else {
				pushMessage.setSender("admin");
				MessageResource notificationSystemUserName = MessageResource.findByFieldName(MessageResource.class, "code", "notification.system_username", locale);
				if(notificationSystemUserName!=null) {
					pushMessage.setSenderName(notificationSystemUserName.getValue());
				} else {
					pushMessage.setSenderName("system_notifier");
				}				
			}			
			pushMessage.setMessage(message);			
			if(title!=null && !title.isEmpty()) {
				pushMessage.setTitle(title);
			} else {
				pushMessage.setTitle(message);
			}
			pushMessage.setTime(new Date().getTime());
			pushMessage.setSentOn(new Date());
			if(receivers!=null && receivers.endsWith(",")) { //in case of receivers added through for loop
				StringBuffer receiversBuffer = new StringBuffer(receivers);
				receiversBuffer.deleteCharAt(receiversBuffer.length()-1);
				receivers = receiversBuffer.toString();
			} else if(receivers==null || receivers=="") { //receivers either not set or not found
				receivers = "admin";
				pushMessage.setTitle("Not Sent: " + pushMessage.getTitle());
			}
			pushMessage.setReceivers(receivers);
			pushMessage.setIsVolatile(isVolatile);	
			pushMessage.setLocale(locale);
			pushMessage.persist();
			Long pushMessageId = pushMessage.getId(); //for fetching fresh copy later
			/**** create and save notifications for non volatile pushmessages ****/
			if(!isVolatile) {
				if(receivers==null || receivers.isEmpty()) { //receivers either not set or not found
					receivers = "admin";					
				} else if(receivers.equals("all")) {
					receivers = Credential.findAllActiveUsernamesAsCommaSeparatedString(locale);
				}				
				Notification notification = null;
				for(String receiver: receivers.split(",")) {
					if(receiver!=null && !receiver.isEmpty()) {
						notification = new Notification();
						if(pushMessage.isVersionMismatch()) {
							pushMessage = PushMessage.findById(PushMessage.class, pushMessageId); //for avoiding version mismatch exception due to stale state
						}						
						notification.setPushMessage(pushMessage);
						notification.setReceiver(receiver);		
						notification.setMarkedAsReadByReceiver(false);
						notification.setClearedByReceiver(false);
						notification.setLocale(locale);
						notification.persist();
					}					
				}
			}
			/**** broadcast pushmessage to eligible receivers if push notifications enabled ****/
			CustomParameter csptPushNotificationsEnabled = CustomParameter.findByName(CustomParameter.class, "PUSH_NOTIFICATIONS_ENABLED", "");
	        if(csptPushNotificationsEnabled!=null && csptPushNotificationsEnabled.getValue().equals("YES")) {
	        	NotificationHandler.broadcastMessage(pushMessage);
	        }			
		} catch(Exception e) {
			logger.error("Exception occured in sending notification: " + e.getMessage());
			return false;
		}
		
		return true;
	}

}