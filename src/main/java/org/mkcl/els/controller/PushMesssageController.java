package org.mkcl.els.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.PushMessage;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/pushmessage")
public class PushMesssageController extends GenericController<PushMessage> {

	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		/**** Populating filters on module page(above grid) ****/			
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Question Types Filter Starts ****/
			List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class, "priority", ApplicationConstants.ASC, locale);
					
			model.addAttribute("deviceTypes", deviceTypes);
			model.addAttribute("deviceType",deviceType.getId());
			model.addAttribute("deviceTypeType",deviceType.getType());
			/**** Question Types Filter Ends ****/

			/**** House Types Filter Starts ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType=this.getCurrentUser().getHouseType();
			if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
				houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
				houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if(houseType.equals("bothhouse")){
				houseType="lowerhouse";
			}
			model.addAttribute("houseType",houseType);
			/**** House Types Filter Ends ****/

			/**** Session Types Filter Starts ****/
			List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			model.addAttribute("sessionTypes",sessionTypes);		
			HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
			Session lastSessionCreated = null;
			Integer year=new GregorianCalendar().get(Calendar.YEAR);
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
				if(lastSessionCreated.getId()!=null){
					year=lastSessionCreated.getYear();
					model.addAttribute("sessionType",lastSessionCreated.getType().getId());
				}else{
					model.addAttribute("errorcode","nosessionentriesfound");
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
			/**** Session Types Filter Ends ****/

			/*** Session Year Filter Starts  ****/
			CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Integer> years=new ArrayList<Integer>();
			if(houseFormationYear!=null){
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i=year;i>=formationYear;i--){
					years.add(i);
				}
			}else{
				model.addAttribute("errorcode", "houseformationyearnotset");
			}
			model.addAttribute("years",years);
			model.addAttribute("sessionYear",year);	
			
			List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			String userGroupType=null;
			if(userGroups!=null){
				if(!userGroups.isEmpty()){
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEV_ALLOWED_USERGROUPTYPES", "");
					if(customParameter!=null){
						String allowedUserGroups=customParameter.getValue(); 
						for(UserGroup i:userGroups){
							if(allowedUserGroups.contains(i.getUserGroupType().getType())){
								model.addAttribute("usergroup",i.getId());
								userGroupType=i.getUserGroupType().getType();
								model.addAttribute("usergroupType",userGroupType);
								break;
							}
						}
					}else{
						model.addAttribute("errorcode","dev_allowed_usergroups_notset");
					}
				}else{
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
			}else{
				model.addAttribute("errorcode","current_user_has_no_usergroups");
			}
			/**** User group Filter Ends ****/
			
			/**** Roles Filter Starts ****/			
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				if(i.getType().startsWith("DEV_")){
					model.addAttribute("role",i.getType());
					break;
				}
			}
			/**** Roles Filter Ends ****/		
		}
	}
	
	
	@Override
	protected void populateNew(final ModelMap model, final PushMessage domain, final String locale,
			final HttpServletRequest request) {
		/**** Locale Starts ****/
		domain.setLocale(locale);	
		/**** Locale Ends ****/

		/**** Message Starts ****/
		String message = request.getParameter("message");
		if(message != null){
			domain.setMessage(message);
		}
		
		/**** Load userName and name ****/
		model.addAttribute("senderUserName", this.getCurrentUser().getActualUsername());
		model.addAttribute("senderName", this.getCurrentUser().getTitle() + " " + this.getCurrentUser().getFirstName()+ " " + this.getCurrentUser().getLastName());
		/**** Load userName and name ****/
		
		/**** House Type Starts ****/
		String selectedHouseType = request.getParameter("houseType");
		HouseType houseType = null;
		if(selectedHouseType != null){
			if(!selectedHouseType.isEmpty()){				
				try {
					Long houseTypeId = Long.parseLong(selectedHouseType);
					houseType = HouseType.findById(HouseType.class,houseTypeId);
				} catch (NumberFormatException e) {
					houseType = HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
				}
				model.addAttribute("formattedHouseType",houseType.getName());
				model.addAttribute("houseTypeType", houseType.getType());
				model.addAttribute("houseType",houseType.getId());
			}else{
				logger.error("**** Check request parameter 'houseType' for no value ****");
				model.addAttribute("errorcode","houseType_isempty");	
			}
		}else{
			logger.error("**** Check request parameter 'houseType' for null value ****");
			model.addAttribute("errorcode","houseType_isnull");
		}
		/**** House Type Ends ****/

		/**** Session Year Starts ****/
		String selectedYear=request.getParameter("sessionYear");
		Integer sessionYear=0;
		if(selectedYear!=null){
			if(!selectedYear.isEmpty()){
				sessionYear=Integer.parseInt(selectedYear);
				model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
				model.addAttribute("sessionYear",sessionYear);
			}else{
				logger.error("**** Check request parameter 'sessionYear' for no value ****");
				model.addAttribute("errorcode","sessionYear_isempty");
			}
		}else{
			logger.error("**** Check request parameter 'sessionYear' for null value ****");
			model.addAttribute("errorcode","sessionyear_isnull");
		}   
		/**** Session Year Ends ****/

		/**** Session Type Starts ****/
		String selectedSessionType=request.getParameter("sessionType");
		SessionType sessionType=null;
		if(selectedSessionType!=null){
			if(!selectedSessionType.isEmpty()){
				sessionType=SessionType.findById(SessionType.class,Long.parseLong(selectedSessionType));
				model.addAttribute("formattedSessionType",sessionType.getSessionType());
				model.addAttribute("sessionType",sessionType.getId());
			}else{
				logger.error("**** Check request parameter 'sessionType' for no value ****");
				model.addAttribute("errorcode","sessionType_isempty");	
			}
		}else{
			logger.error("**** Check request parameter 'sessionType' for null value ****");
			model.addAttribute("errorcode","sessionType_isnull");
		}
		/**** Session Type Ends ****/
		
		/**** Device Type Starts ****/
		String selectedDeviceType=request.getParameter("deviceType");
		if(selectedDeviceType==null){
			selectedDeviceType=request.getParameter("type");
		}
		DeviceType deviceType=null;
		if(selectedDeviceType!=null){
			if(!selectedDeviceType.isEmpty()){
				deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedDeviceType));
				model.addAttribute("formattedDeviceType", deviceType.getName());
				model.addAttribute("deviceType", deviceType.getId());
				model.addAttribute("selectedDeviceType", deviceType.getType());

				String device = request.getParameter("device");//deviceType.getType().split("_")[0];
				model.addAttribute("device", device);
			}else{
				logger.error("**** Check request parameter 'deviceType' for no value ****");
				model.addAttribute("errorcode","deviceType_isempty");		
			}
		}else{
			logger.error("**** Check request parameter 'deviceType' for null value ****");
			model.addAttribute("errorcode","deviceType_isempty");
		}
		/**** Device Type Ends ****/

		/**** Role Starts ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		/**** Role Ends ****/

		/**** User Group Starts ****/
		String usergroupType=request.getParameter("usergroupType");
		if(usergroupType!=null){
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType=(String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String usergroup=request.getParameter("usergroup");
		if(usergroup!=null){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",usergroup);
			request.getSession().removeAttribute("usergroup");
		}
		/**** User Group Ends ****/		

		/**** Session Starts ****/
		Session selectedSession=null;
		if(houseType!=null&&selectedYear!=null&&sessionType!=null){
			try {
				selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			} catch (ELSException e1) {
				model.addAttribute("error", e1.getParameter());
				e1.printStackTrace();
			}
			if(selectedSession!=null){
				model.addAttribute("session",selectedSession.getId());
			}else{
				logger.error("**** Session doesnot exists ****");
				model.addAttribute("errorcode","session_isnull");	
			}
		}else{
			logger.error("**** Check request parameters 'houseType,sessionYear and sessionType for null values' ****");
			model.addAttribute("errorcode","requestparams_isnull");
		}  
		/**** Session Ends ****/
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final PushMessage domain, final HttpServletRequest request) {
	
		domain.setSendDate(new Date());
		domain.setIsRead(false);
		if(domain.getDevice() != null && domain.getDeviceNumber() != null
				&& !domain.getDevice().isEmpty() && !domain.getDeviceNumber().isEmpty()){
			if(domain.getDevice().equals("Question")){
				
				Question q = Question.getQuestion(getSessionFromDomain(domain).getId(), new Integer(domain.getDeviceNumber()), domain.getLocale());
				
				domain.setDeviceType(q.getType().getId().toString());
				
			}else if(domain.getDevice().equals("Motion")){
				
			}
		}
		
		String recepientDetail = request.getParameter("selectRecepientName");		
		if(recepientDetail != null && !recepientDetail.isEmpty()){
			String[] recDetails = recepientDetail.split(",");
			domain.setRecepientName(recDetails[2]);
			domain.setRecepientUserGroupType(recDetails[1]);
			domain.setRecepientUserName(recDetails[0]);
		}
	}
	
	@Override
	protected void populateAfterCreate(ModelMap model, PushMessage domain,
			HttpServletRequest request) {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Parameters which will be read from request in populate new Ends ****/
	}


	@Override
	protected void populateEdit(final ModelMap model, final PushMessage domain,
			final HttpServletRequest request) {
		/**** Locale Starts ****/
		String locale=domain.getLocale();
		/**** Locale Ends ****/

		/**** House Type Starts ****/
		HouseType houseType = HouseType.findByType(domain.getHouseType(), locale);
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseTypeType", houseType.getType());
		model.addAttribute("houseType",houseType.getId());
		/**** House Type Ends ****/

		
		/**** Session Year Starts ****/
		Integer sessionYear = 0;
		sessionYear = domain.getSessionYear();
		model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear",sessionYear);
		/**** Session Year Ends ****/

		/**** Session Type Starts ****/
		SessionType  sessionType = SessionType.findById(SessionType.class, new Long(domain.getSessionType()));
		model.addAttribute("formattedSessionType",sessionType.getSessionType());
		model.addAttribute("sessionType",sessionType.getId());  
		/**** Session Type Ends ****/

		/**** Device Type Starts ****/
		DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(domain.getDeviceType()));
		model.addAttribute("formattedDeviceType",deviceType.getName());
		model.addAttribute("deviceType",deviceType.getId());
		model.addAttribute("device", domain.getDevice());
		model.addAttribute("selectedDeviceType",deviceType.getType());
		/**** Device Type Ends ****/		


		/**** Number Starts ****/
		if(domain.getDeviceNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.formatNumberNoGrouping(new Integer(domain.getDeviceNumber()), locale));
		}
		/**** Number Ends ****/
		
		/**** Role Starts ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		/**** Role Ends ****/
		
		/**** User Group Starts ****/
		String usergroupType = request.getParameter("usergroupType");
		if(usergroupType!=null){
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType=(String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		String strUsergroup = request.getParameter("usergroup");
		if(strUsergroup!=null){
			model.addAttribute("usergroup",strUsergroup);
		}else{
			strUsergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",strUsergroup);
			request.getSession().removeAttribute("userGroup");
		}
		/**** User Group Ends ****/
		
		model.addAttribute("senderUserName", domain.getSenderUserName());
		model.addAttribute("senderName", domain.getSenderName());
		model.addAttribute("senderUserGroup", domain.getSenderUserGroup());
		model.addAttribute("senderUserGroupType", domain.getSenderUserGroupType());
		
		model.addAttribute("recepientName", domain.getRecepientName());
		model.addAttribute("recepientUserName", domain.getRecepientUserName());
		model.addAttribute("recepientUserGroup", domain.getRecepientUserGroup());
		model.addAttribute("recepientUserGroupType", domain.getRecepientUserGroupType());
	}


	@Override
	protected void populateAfterUpdate(ModelMap model, PushMessage domain,
			HttpServletRequest request) {
		/**** Parameters which will be read from request in populate new Starts ****/
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Parameters which will be read from request in populate new Ends ****/
	}
	
	
	private Session getSessionFromDomain(PushMessage domain){
		SessionType sT = SessionType.findById(SessionType.class, new Long(domain.getSessionType()));
		Integer year = new Integer(domain.getSessionYear());
		HouseType hT = HouseType.findByType(domain.getHouseType(), domain.getLocale());
		Session session = null;
		try {
			session = Session.findSessionByHouseTypeSessionTypeYear(hT, sT, year);
		} catch (ELSException e) {			
			e.printStackTrace();
		}
		
		return session;
	}
	
	@Transactional
	@RequestMapping(value="/{id}/updateasread", method=RequestMethod.POST)
	public @ResponseBody String updateMessageAsRead(@PathVariable("id") Long id, HttpServletRequest request, Locale locale){
		
		PushMessage message = PushMessage.findById(PushMessage.class, id);
		if(message != null){			
			message.setIsRead(request.getParameter("read").equals("yes")? true: false);
		}
		return (message.merge() != null)? "SUCCESS": "FAILURE"; 
	}
	
}
