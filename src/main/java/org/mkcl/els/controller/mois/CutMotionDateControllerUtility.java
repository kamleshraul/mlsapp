package org.mkcl.els.controller.mois;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vikasg
 *
 * Helps to create the workflow by assistant for the first time
 */
public class CutMotionDateControllerUtility{
	private static Logger logger = LoggerFactory.getLogger(CutMotionDateControllerUtility.class);
	
	public static WorkflowDetails create(final CutMotionDate domain,
			final AuthUser auser, 
			final DeviceType deviceType, 
			final Session session,
			final Status status,
			final Task task,
			final String workflowType,
			final String devicesEnabled,
			final String assigneeLevel,
			final String locale) throws ELSException{
		
		WorkflowDetails workflowDetails = new WorkflowDetails();
		String userGroupId = null;
		String userGroupType = null;
		String userGroupName = null;				
		try {
			String username = task.getAssignee();
			if(username != null){
				if(!username.isEmpty()){
					Credential credential = Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup = UserGroup.findActive(credential, domain.getSession().getEndDate(), locale);
					userGroupId = String.valueOf(userGroup.getId());
					userGroupType = userGroup.getUserGroupType().getType();
					userGroupName = userGroup.getUserGroupType().getName();
					
					workflowDetails.setLocale(locale);
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					
					workflowDetails.setAssigner(auser.getActualUsername());
					UserGroup auserGroup = null;
					for(UserGroup ug : auser.getUserGroups()){
						if(ug != null){
							auserGroup = ug;
							break;
						}
					}
					workflowDetails.setAssignerUserGroupId(auserGroup.getId().toString());
					workflowDetails.setAssignerUserGroupType(auserGroup.getUserGroupType().getType());
					
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter != null){
						SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime() != null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					
					workflowDetails.setSessionType(session.getType().getSessionType());
					workflowDetails.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale));
					workflowDetails.setHouseType(session.getHouse().getType().getName());
					
					workflowDetails.setUrlPattern("workflow/cutmotiondate");
					workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
					workflowDetails.setModule("CUTMOTION");
					workflowDetails.setDeviceType(deviceType.getName());
					workflowDetails.setDeviceId(domain.getId().toString());
					
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());
					
					workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
					workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
					
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_question", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;		
	}
	
	public static List<WorkflowDetails> create(final DeviceType deviceType,
			final Session session,
			final Status status,
			final List<Task> tasks,
			final String workflowType,
			final String assigneeLevel,
			final String locale) throws ELSException {
		List<WorkflowDetails> workflowDetailsList = new ArrayList<WorkflowDetails>();
		try {
			
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter != null){
				SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails = new WorkflowDetails();
					String userGroupId = null;
					String userGroupType = null;
					String userGroupName = null;				
					String username = i.getAssignee();
					if(username != null){
						if(!username.isEmpty()){
							Credential credential = Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroup userGroup = UserGroup.findActive(credential, session.getEndDate(), locale);
							userGroupId = String.valueOf(userGroup.getId());
							userGroupType = userGroup.getUserGroupType().getType();
							userGroupName = userGroup.getUserGroupType().getName();
							workflowDetails.setAssignee(i.getAssignee());
							workflowDetails.setAssigneeUserGroupId(userGroupId);
							workflowDetails.setAssigneeUserGroupType(userGroupType);
							workflowDetails.setAssigneeUserGroupName(userGroupName);
							workflowDetails.setAssigneeLevel(assigneeLevel);
							if(i.getCreateTime() != null){
								if(!i.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(i.getCreateTime()));
								}
							}
							
							workflowDetails.setDeviceType(deviceType.getType());
							workflowDetails.setDeviceId(deviceType.getId().toString());
							
							workflowDetails.setHouseType(session.getHouse().getType().getName());								
							workflowDetails.setSessionType(session.getType().getSessionType());								
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(session.getYear()));
								
								
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							
							workflowDetails.setUrlPattern("workflow/cutmotiondate");
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							workflowDetails.setModule("CUTMOTION");
							workflowDetails.setDeviceType(deviceType.getType());
							workflowDetails.setDeviceId(deviceType.getId().toString());
							
							workflowDetailsList.add((WorkflowDetails) workflowDetails.persist());
						}				
					}
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return workflowDetailsList;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("CutMotionDate create workflowdetails", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}	
	
	public static List<Status> getStatuses(String strStatuses, String locale){
		List<Status> statuses = new ArrayList<Status>();
		for(String s: strStatuses.split(",")){
			if(!s.isEmpty()){
				Status st = Status.findByType(s, locale);
				statuses.add(st);
			}
		}
		return statuses;
	}
	
	public static String[] getDecodedString(String[] values){
		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer != null && deploymentServer.getValue() != null && !deploymentServer.getValue().isEmpty()){
			if(deploymentServer.getValue().equals("TOMCAT")){

				for(int i = 0; i < values.length; i++){
					try {
						if(values[i] != null){
							values[i] = new String(values[i].getBytes("ISO-8859-1"), "UTF-8");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return values;
	}
	
	public static String concat(String[] value, String seperator){
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < value.length; i++){
			buff.append(value[i]);
			if(i < (value.length -1)){
				buff.append(seperator);
			}
		}
		
		return buff.toString();
	}

	public static  User getUser(final WorkflowActor wfActor, final HouseType houseType, DeviceType deviceType, final String locale) {
		UserGroup userGroup = getUserGroup(wfActor, houseType, deviceType, locale);
		if(userGroup != null) {
			User user = getUser(userGroup, locale);
			return user;
		}
		
		return null;
	}
	
	public static User getUser(final UserGroup userGroup,
			final String locale) {
		Credential credential = userGroup.getCredential();
		User user = User.findByFieldName(User.class,"credential", credential, locale);
		return user;
	}
	
	public static UserGroup getUserGroup(final WorkflowActor wfActor, 
			final HouseType houseType,
			final DeviceType deviceType,
			final String locale) {
		List<UserGroup> userGroups = getUserGroups(wfActor, locale);		
		UserGroup userGroup = getEligibleUserGroup(userGroups, houseType, deviceType, true, locale);
		if(userGroup != null) {
			return userGroup;
		}
		
		return null;
	}
	public static List<UserGroup> getUserGroups(
			final WorkflowActor workflowActor,
			final String locale) {
		UserGroupType userGroupType = workflowActor.getUserGroupType();
		
		List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class, "userGroupType", userGroupType, 
											"activeFrom", ApplicationConstants.DESC, locale);
		return userGroups;
	}
	
	public static HouseType getHouseType(final UserGroup userGroup, 
			final String locale) {
		String strHouseType = 
			userGroup.getParameterValue("HOUSETYPE_" + locale);
		if(strHouseType != null && ! strHouseType.trim().isEmpty()) {
			HouseType houseType = HouseType.findByName(strHouseType, locale);
			return houseType;
		}
		
		return null;
	}
	
	public static String getDeviceType(final UserGroup userGroup, 
			final String locale) {
		String strDeviceType = userGroup.getParameterValue("DEVICETYPE_" + locale);
		if(strDeviceType != null && ! strDeviceType.trim().isEmpty()) {
			return strDeviceType;
		}
		
		return null;
	}
	
	public static UserGroup getEligibleUserGroup(List<UserGroup> userGroups,
			final HouseType houseType,
			final DeviceType deviceType,
			final Boolean isIncludeBothHouseType,
			final String locale) {
		for(UserGroup ug : userGroups) {
			// ug's houseType should be same as @param houseType
			boolean flag1 = false;
			String houseTypeType = houseType.getType();
			HouseType usersHouseType = getHouseType(ug, locale);
			if(isIncludeBothHouseType) {
				if(usersHouseType != null &&
						(usersHouseType.getType().equals(houseTypeType)
						|| usersHouseType.getType().equals(ApplicationConstants.BOTH_HOUSE))) {
					flag1 = true;
				}
			}else {
				if(usersHouseType != null && usersHouseType.getType().equals(houseTypeType)) {
					flag1 = true;
				}
			}
			
			boolean flag2 = false;
			String usersDeviceType = getDeviceType(ug, locale);
			if(usersDeviceType != null && usersDeviceType.contains(deviceType.getName())){
				flag2 = true;
			}
			
			// ug must be active
			boolean flag3 = false;
			Date fromDate = ug.getActiveFrom();
			Date toDate = ug.getActiveTo();
			Date currentDate = new Date();
			if((fromDate == null || currentDate.after(fromDate) ||currentDate.equals(fromDate))
					&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate))) {
				flag3 = true;
			}
			
			// if all cases are met then return user
			if(flag1 && flag2 && flag3) {
				return ug;
			}
		}
		
		return null;
	}

	public static UserGroup getUserGroup(final WorkflowDetails workflowDetails) {
		String strUserGroupId = workflowDetails.getAssigneeUserGroupId();
		Long userGroupId = Long.valueOf(strUserGroupId);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, userGroupId);
		return userGroup;
	}
	
	
	public static WorkflowDetails createInitWorkflowDetails(
			final HttpServletRequest request,
			final AuthUser authUser,
			final UserGroup userGroup,
			final HouseType houseType,
			final DeviceType deviceType,
			final Status status,
			final Integer assigneeLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = getFullWorkflowName(status);
		String wfSubType =  getWorkflowSubType(status);
		Date assignmentTime = new Date();
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
				
		WorkflowActor wfActor = getNextActor(request, userGroup, houseType, deviceType, assigneeLevel, locale);
		if(wfActor != null) {
		// User parameters
		
			String assignee = authUser.getUsername();
			UserGroupType ugt = userGroup.getUserGroupType();
			wfDetails.setAssignee(assignee);
			wfDetails.setAssigneeUserGroupType(ugt.getType());
			wfDetails.setAssigneeUserGroupId(String.valueOf(userGroup.getId()));
			wfDetails.setAssigneeUserGroupName(ugt.getName());
			wfDetails.setAssigneeLevel(String.valueOf(assigneeLevel));
			wfDetails.setNextWorkflowActorId(String.valueOf(wfActor.getId()));
			
		}
		wfDetails.setHouseType(houseType.getName());
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule("");
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	
	public static WorkflowActor getNextActor(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final DeviceType deviceType,
			final Integer assigneeLevel,
			final String locale) {
		WorkflowActor wfActor = null;
		
		String strWFActorId = request.getParameter("actor");
		
		Status status = getStatus(request);
		String wfName = getFullWorkflowName(status);
			
		wfActor = WorkflowConfig.findNextCutMotionDateActor(deviceType, houseType,userGroup, status, wfName, assigneeLevel, locale);
		
		
		return wfActor;
	}
	
	public static Status getStatus(final HttpServletRequest request) {
		String strStatusId = request.getParameter("status");
		Long statusId = Long.valueOf(strStatusId);
		Status status = Status.findById(Status.class, statusId); 
		return status;
	}
	
	public static String getFullWorkflowName(final Status status) {
		String wfName = getWorkflowName(status);
		String fullWfName = wfName + "_workflow";
		return fullWfName;
	}
	
	
	
	public static String getWorkflowName(final Status status) {
		String statusType = status.getType();
		String[] tokens = splitter(statusType, "_");
		int length = tokens.length;
		return tokens[length - 1];
	}
	
	public static String[] splitter(String value, String splitterCharacter){
		String[] vals = value.split(splitterCharacter);
		
		int length = vals.length;
		for(int i = 0; i < length; i++) {
			vals[i] = vals[i].trim();
		}

		return vals;
	}
	public static String getWorkflowSubType(final Status status) {
		return status.getType();
	}
	public static WorkflowDetails createNextActorWorkflowDetails(
			final HttpServletRequest request,
			final Task task,
			final UserGroup currentActorUserGroup,
			final DeviceType deviceType,
			final HouseType houseType,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = getWorkflowName(status);
		String wfSubType =  getWorkflowSubType(status);
		Date assignmentTime = new Date();
		wfDetails.setProcessId(task.getProcessInstanceId());
		wfDetails.setTaskId(task.getId());
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
		
		// User parameters
		// Not applicable parameters: nextWorkflowActorId
		WorkflowActor nextActor = getNextActor(request, currentActorUserGroup, houseType, deviceType, currentActorLevel, locale);
		UserGroup nextUserGroup = getUserGroup(nextActor, houseType, deviceType, locale);
		UserGroupType nextUGT = nextUserGroup.getUserGroupType();
		wfDetails.setAssignee(task.getAssignee());
		wfDetails.setAssigneeUserGroupType(nextUGT.getType());
		wfDetails.setAssigneeUserGroupId(String.valueOf(nextUserGroup.getId()));
		wfDetails.setAssigneeUserGroupName(nextUGT.getName());
		wfDetails.setAssigneeLevel(String.valueOf(nextActor.getLevel()));
		
		wfDetails.setHouseType(houseType.getName());
		
		wfDetails.setUrlPattern(urlPattern);
		wfDetails.setModule(ApplicationConstants.EDITING);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	/**
	 * @param nextWorkflowActor could be null
	 */
	public static void updateWorkflowDetails(final WorkflowDetails workflowDetails, final WorkflowActor nextWorkflowActor) {
		Date completionTime = new Date();
		workflowDetails.setCompletionTime(completionTime);
		workflowDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
		
		if(nextWorkflowActor != null) {
			String wfActorId = String.valueOf(nextWorkflowActor.getId());
			workflowDetails.setNextWorkflowActorId(wfActorId);
		}
		workflowDetails.merge();
	}
	
	
	public static List<MasterVO> getActors(final HouseType houseType,
			final DeviceType deviceType,
			final UserGroup userGroup, 
			final Status status,
			final WorkflowDetails workflowDetails, final String locale) {
		List<WorkflowActor> actors = WorkflowConfig.findCutMotionDateActors(houseType, userGroup, status, getFullWorkflowName(status), Integer.parseInt(workflowDetails.getAssigneeLevel()), locale);
		List<MasterVO> actorsVO = new ArrayList<MasterVO>();
		for (WorkflowActor wfa : actors) {
			MasterVO mvo = new MasterVO();
			User user = getUser(wfa, houseType, deviceType, locale);
			String value = wfa.getId()+ ";" + wfa.getLevel() + ";" + concat(new String[] { user.getTitle(), user.getFirstName(), user.getMiddleName(), user.getLastName() }, " ");
			mvo.setValue(value);
			mvo.setName(getUserGroup(wfa, houseType, deviceType, locale).getUserGroupType().getName());
			actorsVO.add(mvo);
		}

		return actorsVO;
	}
	
	public static List<MasterVO> getActors(final HouseType houseType,
			final DeviceType deviceType,
			final UserGroup userGroup, 
			final Status internalStatus,
			final Status status,
			final WorkflowDetails workflowDetails, final String locale) {
		List<WorkflowActor> actors = WorkflowConfig.findCutMotionDateActors(houseType, userGroup, status, getFullWorkflowName(internalStatus), Integer.parseInt(workflowDetails.getAssigneeLevel()), locale);
		List<MasterVO> actorsVO = new ArrayList<MasterVO>();
		for (WorkflowActor wfa : actors) {
			MasterVO mvo = new MasterVO();
			User user = getUser(wfa, houseType, deviceType, locale);
			String value = wfa.getId()+ ";" + wfa.getLevel() + ";" + concat(new String[] { user.getTitle(), user.getFirstName(), user.getMiddleName(), user.getLastName() }, " ");
			mvo.setValue(value);
			mvo.setName(getUserGroup(wfa, houseType, deviceType, locale).getUserGroupType().getName());
			actorsVO.add(mvo);
		}

		return actorsVO;
	}
	
	public static List<MasterVO> getActors(final HouseType houseType,
			final DeviceType deviceType,
			final UserGroup userGroup, 
			final Status status,
			final Integer level, 
			final String locale) {
		List<WorkflowActor> actors = WorkflowConfig.findCutMotionDateActors(houseType, userGroup, status, getFullWorkflowName(status), level, locale);
		List<MasterVO> actorsVO = new ArrayList<MasterVO>();
		for (WorkflowActor wfa : actors) {
			MasterVO mvo = new MasterVO();
			User user = getUser(wfa, houseType, deviceType, locale);
			String value = wfa.getId()+ ";" + wfa.getLevel() + ";" + concat(new String[] { user.getTitle(), user.getFirstName(), user.getMiddleName(), user.getLastName() }, " ");
			mvo.setValue(value);
			mvo.setName(getUserGroup(wfa, houseType, deviceType, locale).getUserGroupType().getName());
			actorsVO.add(mvo);
		}

		return actorsVO;
	}
	
	 public static List<MasterVO> getActors(HttpServletRequest request, String locale){
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strUserGroup = request.getParameter("userGroup");
		String strWFDetailsId =request.getParameter("workflowDetailsId");
		WorkflowDetails workflowDetails = null;
		String strStatus = request.getParameter("selectedSubWorkflow");
		
		String[] values = getDecodedString(new String[]{strHouseType});
		strHouseType = values[0];
		
		if(strWFDetailsId != null && !strWFDetailsId.isEmpty()){
			workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWFDetailsId));
		}
		
		if(strStatus == null){
			strStatus = request.getParameter("status");
		}
		
		HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
		if(houseType == null){
			houseType = HouseType.findByType(strHouseType, locale.toString());
		}
		
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
		UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
		Status status = null;
		try{
			status = Status.findById(Status.class, new Long(strStatus));
		}catch(Exception e){
			status = Status.findByType(strStatus, locale.toString());
		}
		String strWF = request.getParameter("isWF"); 
		return (strWF != null && strWF.equals("YES"))? getActors(houseType, deviceType, userGroup, status, workflowDetails, locale.toString()): getActors(houseType, deviceType, userGroup, status, new Integer(request.getParameter("level")), locale);
	}
	 
	 public static List<MasterVO> getActors(HttpServletRequest request, CutMotionDate cutMotionDate, String locale){
			String strHouseType = request.getParameter("houseType");
			String strDeviceType = request.getParameter("deviceType");
			String strUserGroup = request.getParameter("userGroup");
			String strWFDetailsId =request.getParameter("workflowDetailsId");
			WorkflowDetails workflowDetails = null;
			String strStatus = request.getParameter("selectedSubWorkflow");
			
			String[] values = getDecodedString(new String[]{strHouseType});
			strHouseType = values[0];
			
			if(strWFDetailsId != null && !strWFDetailsId.isEmpty()){
				workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, new Long(strWFDetailsId));
			}
			
			if(strStatus == null){
				strStatus = request.getParameter("status");
			}
			
			HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
			if(houseType == null){
				houseType = HouseType.findByType(strHouseType, locale.toString());
			}
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
			Status status = null;
			try{
				status = Status.findById(Status.class, new Long(strStatus));
			}catch(Exception e){
				status = Status.findByType(strStatus, locale.toString());
			}
			String strWF = request.getParameter("isWF"); 
			return (strWF != null && strWF.equals("YES"))? getActors(houseType, deviceType, userGroup, cutMotionDate.getInternalStatus(), status, workflowDetails, locale.toString()): getActors(houseType, deviceType, userGroup, status, new Integer(request.getParameter("level")), locale);
		}
	
	public static List<MasterVO> getStatusesForActor(HttpServletRequest request, Status status, Locale locale){
		List<MasterVO> statuses = new ArrayList<MasterVO>();
		
		try{
			String strDeviceType = request.getParameter("deviceType");
			String strUserGroup = request.getParameter("usergroup");
			String strUserGroupType = request.getParameter("usergroupType");
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			
			CustomParameter actorsAllowedStatusesUserGroup = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUTUP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+status.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
			CustomParameter actorsAllowedStatusesDefualt = null;
			CustomParameter actorsStatuses = null;
			
			if(actorsAllowedStatusesUserGroup != null){
				actorsStatuses = actorsAllowedStatusesUserGroup;
			}else{
				actorsAllowedStatusesDefualt = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUTUP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+status.getType().toUpperCase()+"_BY_DEFAULT", "");
				actorsStatuses = actorsAllowedStatusesDefualt;
			}
			
			if(actorsStatuses != null){
				if(actorsStatuses.getValue() != null && !actorsStatuses.getValue().isEmpty()){
					for(Status s : getStatuses(actorsStatuses.getValue(), locale.toString())){
						MasterVO vo = new MasterVO();
						vo.setId(s.getId());
						vo.setValue(s.getType());
						vo.setName(s.getName());
						statuses.add(vo);
						vo = null;
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return statuses;
	}
	
	public static List<MasterVO> getStatusesForActor(HttpServletRequest request, CutMotionDate domain, Locale locale){
		List<MasterVO> statuses = new ArrayList<MasterVO>();
		
		try{
			String strUserGroup = request.getParameter("usergroup");
			String strUserGroupType = request.getParameter("usergroupType");
			DeviceType deviceType = domain.getDeviceType();
			
			Status status = domain.getInternalStatus();
			
			CustomParameter actorsAllowedStatusesUserGroup = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUTUP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+status.getType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
			CustomParameter actorsAllowedStatusesDefualt = null;
			CustomParameter actorsStatuses = null;
			
			if(actorsAllowedStatusesUserGroup != null){
				actorsStatuses = actorsAllowedStatusesUserGroup;
			}else{
				actorsAllowedStatusesDefualt = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUTUP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+status.getType().toUpperCase()+"_BY_DEFAULT", "");
				actorsStatuses = actorsAllowedStatusesDefualt;
			}
			
			if(actorsStatuses != null){
				if(actorsStatuses.getValue() != null && !actorsStatuses.getValue().isEmpty()){
					for(Status s : getStatuses(actorsStatuses.getValue(), locale.toString())){
						MasterVO vo = new MasterVO();
						vo.setId(s.getId());
						vo.setValue(s.getType());
						vo.setName(s.getName());
						statuses.add(vo);
						vo = null;
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return statuses;
	}
	
	public static List<MasterVO> getStatusesForActor(HttpServletRequest request, WorkflowDetails currentWorkflow, Locale locale){
		List<MasterVO> statuses = new ArrayList<MasterVO>();
		
		try{
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			
			CustomParameter actorsAllowedStatusesUserGroup = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUTUP_OPTIONS_"+currentWorkflow.getWorkflowSubType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
			CustomParameter actorsAllowedStatusesDefualt = null;
			CustomParameter actorsStatuses = null;
			
			if(actorsAllowedStatusesUserGroup != null){
				actorsStatuses = actorsAllowedStatusesUserGroup;
			}else{
				actorsAllowedStatusesDefualt = CustomParameter.findByName(CustomParameter.class, "CUTMOTIONDATE_PUTUP_OPTIONS_"+currentWorkflow.getWorkflowSubType().toUpperCase()+"_BY_DEFAULT", "");
				actorsStatuses = actorsAllowedStatusesDefualt;
			}
			
			if(actorsStatuses != null){
				if(actorsStatuses.getValue() != null && !actorsStatuses.getValue().isEmpty()){
					for(Status s : getStatuses(actorsStatuses.getValue(), locale.toString())){
						MasterVO vo = new MasterVO();
						vo.setId(s.getId());
						vo.setValue(s.getType());
						vo.setName(s.getName());
						statuses.add(vo);
						vo = null;
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return statuses;
	}
}