/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.NotificationController.java
 * Created On: Sept 16, 2017
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.NotificationVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.notification.PushMessage;
import org.mkcl.els.domain.notification.Notification;
import org.mkcl.els.service.INotificationService;
import org.mkcl.els.service.impl.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class NotificationController.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
@Controller
@RequestMapping("/notification")
public class NotificationController extends GenericController<Notification> {
	
	@Autowired 
	private INotificationService notificationService;
	
	public static INotificationService getNotificationService() {
    	INotificationService notificationService = new NotificationController().notificationService;
        if (notificationService == null) {
        	System.out.println("INotificationService has not been injected in QuestionController Singleton Instance");
        	notificationService = new NotificationServiceImpl();
        }
        return notificationService;
    }
	
	@RequestMapping("/testpage")
	public String renderTestPage(final HttpServletRequest request, ModelMap model, Locale locale) {
		return "title/testpage";
	}
	
	@Override
	protected String modifyEditUrlPattern(final String editUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		String editServletPath = editUrlPattern;
		//Edit Parameter
		String edit = request.getParameter("edit");
		if(edit != null){
			if(!Boolean.parseBoolean(edit)){
				editServletPath = editUrlPattern.replace("edit", "editreadonly");
			}
		}
		return editServletPath;
	}
	
	@Override
    protected void populateEdit(final ModelMap model, 
    		final Notification domain,
            final HttpServletRequest request) {
		
		NotificationVO notificationVO = Notification.populateNotificationVO(domain);
		if(notificationVO!=null) {
			model.addAttribute("notificationVO", notificationVO);
		} else {
			model.addAttribute("errorcode", "NOTIFICATION_DOMAIN_NULL");
			
		}
		
	}
	
	@RequestMapping("/active_count_for_user")
	public @ResponseBody int findActiveNotificationsCountForUser(final HttpServletRequest request, ModelMap model, Locale locale) {
		int activeNotificationsCount = Notification.findActiveNotificationsCountForCurrentUser(this.getCurrentUser().getActualUsername(), locale.toString());
		return activeNotificationsCount;
	}
	
	@RequestMapping(value = "/visible_list_for_user", method = RequestMethod.GET)
	public @ResponseBody List<NotificationVO> findVisibleNotificationsListForUser(final HttpServletRequest request, ModelMap model, Locale locale) {
		int visibleMaxCount = ApplicationConstants.NOTIFICATIONS_VISIBLE_MAXIMUM_COUNT;
		
		String activeCountStr = request.getParameter("active_count");
		if(activeCountStr!=null && !activeCountStr.isEmpty()) {
			try {
				int activeCount = Integer.parseInt(activeCountStr);
				if(activeCount > visibleMaxCount) { //atleast all the unread messages be shown
					visibleMaxCount = activeCount;
				}
			} catch(NumberFormatException nfe) {
				logger.warn("activeCount is not a number");
			}
			
		}
		
		List<NotificationVO> notificationsVOForCurrentUser = Notification.fetchNotificationsVOForCurrentUserInRange(this.getCurrentUser().getActualUsername(), locale.toString(), 0, visibleMaxCount);
		if(notificationsVOForCurrentUser!=null) {
			return notificationsVOForCurrentUser;
		} else {
			return new ArrayList<NotificationVO>();
		}		
	}
	
	@RequestMapping(value = "/find_id_by_pushmessage_receiver", method = RequestMethod.GET)
	public @ResponseBody long findByPushMessageAtReceiver(final HttpServletRequest request, Locale locale) {
		String pushMessageId = request.getParameter("pushmessage_id");
		String receiver = request.getParameter("receiver");
		long notificationId = new Long(0);
		try {
			PushMessage pushMessage = PushMessage.findById(PushMessage.class, Long.parseLong(pushMessageId));
			Notification notification = Notification.findByPushMessageAtReceiver(pushMessage, receiver, locale.toString());
			if(notification!=null) {
				notificationId = notification.getId();
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return notificationId;		
	}
	
	@Transactional
	@RequestMapping(value="/{notificationId}/markAsRead", method=RequestMethod.POST)
	public @ResponseBody boolean markNotificationAsRead(@PathVariable("notificationId") final Long notificationId, final HttpServletRequest request, ModelMap model, Locale locale) {
		boolean markedAsRead = false;
		Notification notification = Notification.findById(Notification.class, notificationId);
		if(notification!=null) {
			notification.setMarkedAsReadByReceiver(true);
			notification.merge();
			markedAsRead = true;
		}
		return markedAsRead;
	}
	
	@Transactional
	@RequestMapping(value="/{notificationId}/clear", method=RequestMethod.POST)
	public @ResponseBody boolean clearNotification(@PathVariable("notificationId") final Long notificationId, final HttpServletRequest request, ModelMap model, Locale locale) {
		boolean isCleared = false;
		Notification notification = Notification.findById(Notification.class, notificationId);
		if(notification!=null) {
			notification.setClearedByReceiver(true);
			notification.merge();
			isCleared = true;
		}
		return isCleared;
	}
	
	@Transactional
	@RequestMapping(value="/{receiver}/markAllAsRead", method=RequestMethod.POST)
	public @ResponseBody boolean markAllNotificationsAsRead(@PathVariable("receiver") final String receiver, final HttpServletRequest request, ModelMap model, Locale locale) {
		boolean marked = false;
		List<Notification> notifications = Notification.fetchAllNotificationsForCurrentUser(receiver, locale.toString()); //later beginCount will be dynamically taken
		if(notifications!=null && !notifications.isEmpty()) {
			for(Notification notification: notifications) {
				if(notification!=null) {
					if(notification.getMarkedAsReadByReceiver()==null || notification.getMarkedAsReadByReceiver().booleanValue()!=true) {
						notification.setMarkedAsReadByReceiver(true);
						notification.merge();
					}										
				}
			}
			marked = true;
		}				
		return marked;
	}
	
	@Transactional
	@RequestMapping(value="/{receiver}/clearAllRead", method=RequestMethod.POST)
	public @ResponseBody boolean clearAllReadNotifications(@PathVariable("receiver") final String receiver, final HttpServletRequest request, ModelMap model, Locale locale) {
		boolean cleared = false;
		List<Notification> notifications = Notification.fetchAllNotificationsForCurrentUser(receiver, locale.toString()); //later beginCount will be dynamically taken
		if(notifications!=null && !notifications.isEmpty()) {
			for(Notification notification: notifications) {
				if(notification!=null) {
					if(notification.getMarkedAsReadByReceiver()!=null && notification.getMarkedAsReadByReceiver().booleanValue()==true) {
						notification.setClearedByReceiver(true);
						notification.merge();
					}									
				}
			}
			cleared = true;
		}				
		return cleared;
	}
	
	@Transactional
	@RequestMapping(value="/{receiver}/viewAll", method=RequestMethod.POST)
	public @ResponseBody List<Notification> viewAllNotifications(@PathVariable("receiver") final String receiver, final HttpServletRequest request, ModelMap model, Locale locale) {
		List<Notification> notifications = Notification.fetchAllNotificationsForCurrentUser(receiver, locale.toString()); //later limit will be taken as total count
		if(notifications!=null && !notifications.isEmpty()) {
			return notifications;
		} else {		
			return new ArrayList<Notification>();
		}
	}
	
	public static void sendSupportingMemberApprovalNotification(final String deviceSubject, final DeviceType deviceType, final Member primaryMember, final String supportingMembersUserNames, final String locale) {
		Map<String, String[]> templateParameters = new HashMap<String, String[]>();
		templateParameters.put("locale", new String[]{locale});
		templateParameters.put("deviceSubject", new String[]{deviceSubject});
		templateParameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		templateParameters.put("primaryMemberId", new String[]{primaryMember.getId().toString()});		
		templateParameters.put("supportingMembersUserNames", new String[]{supportingMembersUserNames});		
		getNotificationService().sendNotificationWithTitleUsingTemplate("REQUEST_FOR_SUPPORTING_MEMBER_APPROVAL", templateParameters, locale);
	}
	
	public static void sendDepartmentChangeNotification(final String deviceNumber, 
								final DeviceType deviceType,
								final HouseType houseType,
								final String prevSubDepartment,
								final String currentSubDepartment,
								final String usergroupTypes,
								final String locale) {
		Map<String, String[]> templateParameters = new HashMap<String, String[]>();
		templateParameters.put("locale", new String[]{locale});
		templateParameters.put("deviceNumber", new String[]{deviceNumber});
		templateParameters.put("deviceTypeType", new String[]{deviceType.getType()});
		templateParameters.put("deviceTypeName", new String[]{deviceType.getName()});
		templateParameters.put("deviceTypeNameLike", new String[]{"%"+deviceType.getName()+"%"});
		templateParameters.put("houseTypeType", new String[]{houseType.getType()});
		templateParameters.put("houseTypeName", new String[]{houseType.getName()});
		templateParameters.put("houseTypeNameLike", new String[]{"%"+houseType.getName()+"%"});
		templateParameters.put("prevSubDepartment", new String[]{prevSubDepartment});
		templateParameters.put("currentSubDepartment", new String[]{currentSubDepartment});
		templateParameters.put("currentSubDepartmentLike", new String[]{"%"+currentSubDepartment+"%"});
		templateParameters.put("usergroupTypes", new String[]{usergroupTypes});		
		getNotificationService().sendNotificationWithTitleUsingTemplate("DEPARTMENT_CHANGE_INTIMATION", templateParameters, locale);
	}
	
	public static void sendDepartmentProcessNotificationForUnstarredQuestion(final Question question, final String departmentUserName, final String locale) {
		Map<String, String[]> templateParameters = new HashMap<String, String[]>();
		templateParameters.put("locale", new String[]{locale});
		templateParameters.put("deviceNumber", new String[]{FormaterUtil.formatNumberNoGrouping(question.getNumber(), locale)});
		templateParameters.put("deviceTypeId", new String[]{question.getType().getId().toString()});
		templateParameters.put("sessionId", new String[]{question.getSession().getId().toString()});
		String requestedField = "";
		if(question.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_FINAL_ADMISSION)) {
			requestedField = "answer";
		} else {
			requestedField = "factual_position";
		}
		templateParameters.put("requestedField", new String[]{requestedField});
		templateParameters.put("departmentUserName", new String[]{departmentUserName});
		getNotificationService().sendNotificationWithTitleUsingTemplate(question.getType().getType().toUpperCase() + "_REQUEST_FOR_DEPARTMENT_PROCESSING", templateParameters, locale);
	}
	
	public static void sendBallotCreationNotification(final DeviceType deviceType,
								final HouseType houseType,
								final Date ballotDate,
								final String groupNumber,
								final String ballotUserName,
								final String locale) {
		Map<String, String[]> templateParameters = new HashMap<String, String[]>();
		templateParameters.put("locale", new String[]{locale});
		templateParameters.put("deviceTypeType", new String[]{deviceType.getType()});
		templateParameters.put("deviceTypeName", new String[]{deviceType.getName()});
		templateParameters.put("deviceTypeNameLike", new String[]{"%"+deviceType.getName()+"%"});
		templateParameters.put("houseTypeType", new String[]{houseType.getType()});
		templateParameters.put("houseTypeName", new String[]{houseType.getName()});
		templateParameters.put("houseTypeNameLike", new String[]{"%"+houseType.getName()+"%"});
		templateParameters.put("ballotDate", new String[]{FormaterUtil.formatDateToString(ballotDate, ApplicationConstants.DB_DATEFORMAT)});
		templateParameters.put("groupNumber", new String[]{groupNumber});
		templateParameters.put("ballotUserName", new String[]{ballotUserName});
		String usergroupTypes = "";
		CustomParameter csptUserGroupTypesForBallotCreationNotification = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase()+"_USERGROUPTYPES_FOR_BALLOT_CREATION_NOTIFICATION", "");
		if(csptUserGroupTypesForBallotCreationNotification!=null 
				&& csptUserGroupTypesForBallotCreationNotification.getValue()!=null) {
			usergroupTypes = csptUserGroupTypesForBallotCreationNotification.getValue();
		} else {
			usergroupTypes = "deputy_secretary";
		}
		templateParameters.put("usergroupTypes", new String[]{usergroupTypes});		
		getNotificationService().sendNotificationWithTitleUsingTemplate("BALLOT_CREATION_NOTIFICATION", templateParameters, locale);
	}
	
	public static void sendNotificationFromAdminPage(final String notificationTitle, final String notificationMessage, final boolean isVolatile, final String receivers, final String locale) {
		if(isVolatile) {
			if(notificationTitle.equals(notificationMessage)) {
				if(receivers!=null && !receivers.isEmpty()) {
					getNotificationService().sendVolatileNotification(notificationMessage, receivers, locale);
				} else {
					getNotificationService().sendVolatileNotificationToAllActiveUsers(notificationMessage, locale);
				}
			} else {
				if(receivers!=null && !receivers.isEmpty()) {
					getNotificationService().sendVolatileNotificationWithTitle(notificationTitle, notificationMessage, receivers, locale);
				} else {
					getNotificationService().sendVolatileNotificationWithTitleToAllActiveUsers(notificationTitle, notificationMessage, locale);
				}
			}
		} else {
			if(notificationTitle.equals(notificationMessage)) {
				if(receivers!=null && !receivers.isEmpty()) {
					getNotificationService().sendNotification(notificationMessage, receivers, locale);
				} else {
					getNotificationService().sendNotificationToAllActiveUsers(notificationMessage, locale);
				}
			} else {
				if(receivers!=null && !receivers.isEmpty()) {
					getNotificationService().sendNotificationWithTitle(notificationTitle, notificationMessage, receivers, locale);
				} else {
					getNotificationService().sendNotificationWithTitleToAllActiveUsers(notificationTitle, notificationMessage, locale);
				}
			}
		}
	}
	
}
