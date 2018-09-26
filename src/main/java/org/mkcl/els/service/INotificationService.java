package org.mkcl.els.service;

import java.util.Map;

public interface INotificationService {
	
	public boolean sendNotification(String message, String receivers, String locale);
	
	public boolean sendNotification(String sender, String message, String receivers, String locale);
	
	public boolean sendNotificationToAllActiveUsers(String message, String locale);	
	
	public boolean sendNotificationToAllActiveUsers(String sender, String message, String locale);
	
	
	public boolean sendVolatileNotification(String message, String receivers, String locale);
	
	public boolean sendVolatileNotification(String sender, String message, String receivers, String locale);
	
	public boolean sendVolatileNotificationToAllActiveUsers(String message, String locale);
	
	public boolean sendVolatileNotificationToAllActiveUsers(String sender, String message, String locale);
	
	
	public boolean sendNotificationWithTitle(String title, String message, String receivers, String locale);
	
	public boolean sendNotificationWithTitle(String sender, String title, String message, String receivers, String locale);
	
	public boolean sendNotificationWithTitleToAllActiveUsers(String title, String message, String locale);
	
	public boolean sendNotificationWithTitleToAllActiveUsers(String sender, String title, String message, String locale);
	
	
	public boolean sendVolatileNotificationWithTitle(String title, String message, String receivers, String locale);
	
	public boolean sendVolatileNotificationWithTitle(String sender, String title, String message, String receivers, String locale);
	
	public boolean sendVolatileNotificationWithTitleToAllActiveUsers(String title, String message, String locale);
	
	public boolean sendVolatileNotificationWithTitleToAllActiveUsers(String sender, String title, String message, String locale);
	
	
	public boolean sendNotificationWithTitleUsingTemplate(String templateKey, Map<String, String[]> templateParameters, String locale);

}
