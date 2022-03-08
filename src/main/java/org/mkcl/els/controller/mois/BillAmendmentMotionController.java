package org.mkcl.els.controller.mois;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Section;
import org.mkcl.els.domain.SectionAmendment;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("billamendmentmotion")
public class BillAmendmentMotionController extends GenericController<BillAmendmentMotion>{
	
	@Autowired
	private IProcessService processService;

	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {		
//		Bill bill = Bill.findById(Bill.class, new Long("22300"));	
		/**** Selected Motion Type ****/
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Available Motion Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try {
				deviceTypes = DeviceType.findDeviceTypesStartingWith("motions_billamendment", locale);

				model.addAttribute("motionTypes", deviceTypes);
				/**** Default Value ****/
				model.addAttribute("motionType", deviceType.getId());
				/**** Access Control Based on Motion Type ****/
				model.addAttribute("motionTypeType", deviceType.getType());
				
				Session lastSessionCreated=null;
				
				String houseType = request.getParameter("houseType");
				String sessionType = request.getParameter("sessionType");
				String sessionYear = request.getParameter("sessionYear");
				
				if(houseType!=null&&!houseType.isEmpty()&&sessionType!=null
						&&!sessionType.isEmpty()&&sessionYear!=null&&!sessionYear.isEmpty()) {
					/****
					 * House Types .If housetype=bothhouse then lowerhouse will be
					 * selected by default
					 ****/
					List<HouseType> houseTypes = new ArrayList<HouseType>();								
					if (houseType.equals("lowerhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("upperhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("bothhouse")) {
						houseTypes = HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale);
					}
					model.addAttribute("houseTypes", houseTypes);
					if (houseType.equals("bothhouse")) {
						houseType = "lowerhouse";
					}
					model.addAttribute("houseType", houseType);
					
					/**** Session Types. ****/
					List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale);
					model.addAttribute("sessionTypes", sessionTypes);
					model.addAttribute("sessionType", sessionType);
					
					/**** Years ****/
					Integer year = Integer.parseInt(sessionYear);
					CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class,"HOUSE_FORMATION_YEAR", "");
					List<Integer> years = new ArrayList<Integer>();
					if (houseFormationYear != null) {
						Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
						for (int i = year; i >= formationYear; i--) {
							years.add(i);
						}
					} else {
						model.addAttribute("errorcode", "houseformationyearnotset");
					}
					model.addAttribute("years", years);
					model.addAttribute("sessionYear", year);
				} else {
					/****
					 * House Types .If housetype=bothhouse then lowerhouse will be
					 * selected by default
					 ****/
					List<HouseType> houseTypes = new ArrayList<HouseType>();				
					if(houseType==null) {
						houseType = this.getCurrentUser().getHouseType();
					}				
					if (houseType.equals("lowerhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("upperhouse")) {
						houseTypes = HouseType.findAllByFieldName(HouseType.class,"type", houseType, "name",ApplicationConstants.ASC, locale);
					} else if (houseType.equals("bothhouse")) {
						houseTypes = HouseType.findAll(HouseType.class, "type",ApplicationConstants.ASC, locale);
					}
					model.addAttribute("houseTypes", houseTypes);
					if (houseType.equals("bothhouse")) {
						houseType = "lowerhouse";
					}
					model.addAttribute("houseType", houseType);

					/**** Session Types. ****/
					List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType",ApplicationConstants.ASC, locale);
					/**** Latest Session of a House Type ****/
					HouseType authUserHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, locale);
					
					lastSessionCreated = Session.findLatestSession(authUserHouseType);

					/***
					 * Session Year and Session Type.Default is the type and year of
					 * last created session in a particular housetype
					 ****/
					Integer year = new GregorianCalendar().get(Calendar.YEAR);
					if (lastSessionCreated.getId() != null) {
						year = lastSessionCreated.getYear();
						model.addAttribute("sessionType", lastSessionCreated.getType().getId());
					} else {
						model.addAttribute("errorcode", "nosessionentriesfound");
					}
					model.addAttribute("sessionTypes", sessionTypes);
					/**** Years ****/
					CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class,"HOUSE_FORMATION_YEAR", "");
					List<Integer> years = new ArrayList<Integer>();
					if (houseFormationYear != null) {
						Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
						for (int i = year; i >= formationYear; i--) {
							years.add(i);
						}
					} else {
						model.addAttribute("errorcode", "houseformationyearnotset");
					}
					model.addAttribute("years", years);
					model.addAttribute("sessionYear", year);				
				}	
				
				/**** Bill House Types ****/
				List<HouseType> billHouseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
				model.addAttribute("billHouseTypes", billHouseTypes);
				
				/**** added by sandeep singh(jan 27 2013) ****/
				/****
				 * Custom Parameter To Determine The Usergroup and usergrouptype
				 * of mois users . here we are determining what status will be
				 * shown to a particular user.
				 ****/
				List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
				String userGroupType = null;
				if (userGroups != null) {
					if (!userGroups.isEmpty()) {
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"BAMOIS_ALLOWED_USERGROUPTYPES", "");
						if (customParameter != null) {
							String allowedUserGroups = customParameter.getValue();
							for (UserGroup i : userGroups) {
								if (allowedUserGroups.contains(i.getUserGroupType().getType())) {
									/****
									 * Authenticated User's usergroup and
									 * usergroupType
									 ****/
									model.addAttribute("usergroup", i.getId());
									userGroupType = i.getUserGroupType().getType();
									model.addAttribute("usergroupType",userGroupType);
									/**** Motion Status Allowed ****/
									CustomParameter allowedStatus = CustomParameter.findByName(CustomParameter.class,
													"BILLAMENDMENTMOTION_GRID_STATUS_ALLOWED_"+ userGroupType.toUpperCase(),"");
									List<Status> status = new ArrayList<Status>();
									if (allowedStatus != null) {
										status = Status.findStatusContainedIn(allowedStatus.getValue(),locale);
									} else {
										CustomParameter defaultAllowedStatus = CustomParameter.findByName(CustomParameter.class,
														"BILLAMENDMENTMOTION_GRID_STATUS_ALLOWED_BY_DEFAULT","");
										if (defaultAllowedStatus != null) {
											status = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
										} else {
											model.addAttribute("errorcode","billamendmentmotion_status_allowed_by_default_not_set");
										}
									}
									model.addAttribute("status", status);
									if(request.getParameter("status")!=null
											&&!request.getParameter("status").isEmpty()) {
										model.addAttribute("selectedStatusId", request.getParameter("status"));
									}
									break;
								}
							}
						} else {
							model.addAttribute("errorcode","mois_allowed_usergroups_notset");
						}
					} else {
						model.addAttribute("errorcode","current_user_has_no_usergroups");
					}
				} else {
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
				/****
				 * Roles and ugparam.Role will be used to decide who can create
				 * new motions(member and typist).for member and typist only those
				 * motions will be visible which are created by them.For other
				 * mois users all motions will be visible.
				 ****/
				Set<Role> roles = this.getCurrentUser().getRoles();
				for (Role i : roles) {
					if (i.getType().startsWith("MEMBER_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().equals("BAMOIS_TYPIST")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					} else if (i.getType().startsWith("BAMOIS_")) {
						model.addAttribute("role", i.getType());
						model.addAttribute("ugparam", this.getCurrentUser().getActualUsername());
						break;
					}
				}
				/**** File Options(Obtain Dynamically) ****/
				if (userGroupType != null && !userGroupType.isEmpty()
						&& userGroupType.equals("assistant")) {
					int highestFileNo = BillAmendmentMotion.findHighestFileNo(lastSessionCreated, deviceType, locale);
					model.addAttribute("highestFileNo", highestFileNo);
				}
			} catch (ELSException e) {
				model.addAttribute("MotionController", e.getParameter());
			}
		}
	}
	
	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** For BAMOIS roles other than member and typist assistant grid is visible ****/
		String role=request.getParameter("role");
		String newUrlPattern=urlPattern;
		if(role.contains("BAMOIS_")&& (role.contains("TYPIST"))){
			newUrlPattern=urlPattern+"?usergroup=typist";
		}else if(role.contains("BAMOIS_")&& (!role.contains("TYPIST"))){
			newUrlPattern=urlPattern+"?usergroup=assistant";
		}
		return newUrlPattern;
	}
	
	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** Member and typist can only create new motions ****/
		String role=request.getParameter("role");		
		if(role!=null){
			if(!role.isEmpty()){
				if(role.startsWith("MEMBER_")||role.equals("BAMOIS_TYPIST")){
					return servletPath;
				}
			}
		}			
		/**** For others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
	}
	
	@Override
	protected String modifyEditUrlPattern(final String newUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		/**** if request parameter contains edit=false then editreadonly page is displayed ****/
		String edit=request.getParameter("edit");
		String readonly=request.getParameter("readonly");
		if(readonly!=null){
			if(!readonly.isEmpty()){
				if(readonly.equals("yes")){
					return newUrlPattern.replace("edit","editreadonly");
				}
			}
			
		}		
		if(edit!=null){
			if(!Boolean.parseBoolean(edit)){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}
		/**** for Member and typist edit page is displayed ****/
		/**** for assistant assistant page ****/
		/**** for other bamois usergroupTypes editreadonly page ****/
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(i.getType().startsWith("MEMBER_")||i.getType().contains("TYPIST")){
				return newUrlPattern;
			}else if(i.getType().contains("ASSISTANT")||i.getType().contains("SECTION_OFFICER")){
				return newUrlPattern.replace("edit","assistant");
			}else if(i.getType().startsWith("BAMOIS_")){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}	
		/**** for others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return "billamendmentmotion/error";
	}

	@Override
	protected void populateNew(final ModelMap model, BillAmendmentMotion domain, final String locale,
			final HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);
		/**** Amended Bill ****/
		String amendedBillId = request.getParameter("amendedBillId");
		if(amendedBillId==null || amendedBillId.isEmpty()) {
			logger.error("**** Check request parameter 'amendedBillId' for no value ****");
			model.addAttribute("errorcode","amendedBillId_isempty");
			return;
		}
		Bill amendedBill = Bill.findById(Bill.class, Long.parseLong(amendedBillId));
		if(amendedBill==null) {
			logger.error("**** Check request parameter 'amendedBillId' for invalid value ****");
			model.addAttribute("errorcode","amendedBillId_isInvalid");
			return;
		} 
		domain.setAmendedBill(amendedBill);
		model.addAttribute("amendedBill", amendedBill.getId());
		String amendedBillInfo = domain.getAmendedBillInfo();
		if(amendedBillInfo!=null && !amendedBillInfo.isEmpty()) {
			amendedBillInfo = amendedBillInfo.replace("#", "~");
		} else {
			model.addAttribute("errorcode","amendedBillInfo_notfound");
			return;
		}
		model.addAttribute("amendedBillInfo", amendedBillInfo);
		/**** Amended Bill Languages ****/		
		domain.setAmendedBillLanguages(amendedBill.findLanguagesOfContentDrafts());
		model.addAttribute("amendedBillLanguages", domain.getAmendedBillLanguages());		
		/**** Device Type ****/
		String selectedDeviceType=request.getParameter("motionType");
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
			}else{
				logger.error("**** Check request parameter 'DeviceType' for no value ****");
				model.addAttribute("errorcode","deviceType_isempty");
				return;
			}
		}else{
			logger.error("**** Check request parameter 'deviceType' for null value ****");
			model.addAttribute("errorcode","deviceType_isnull");
		}
		/**** House Type ****/
		String selectedHouseType=request.getParameter("houseType");
		HouseType houseType=null;
		if(selectedHouseType!=null){
			if(!selectedHouseType.isEmpty()){
				//**** Modified By Sandeep Singh ****/
				/*In case of a validation exception selectedHouseType is id ****/
				try {
					Long houseTypeId=Long.parseLong(selectedHouseType);
					houseType=HouseType.findById(HouseType.class,houseTypeId);
				} catch (NumberFormatException e) {
					houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
				}
				model.addAttribute("formattedHouseType",houseType.getName());
				model.addAttribute("houseType",houseType.getId());
			}else{
				logger.error("**** Check request parameter 'houseType' for no value ****");
				model.addAttribute("errorcode","houseType_isempty");	
			}
		}else{
			logger.error("**** Check request parameter 'houseType' for null value ****");
			model.addAttribute("errorcode","houseType_isnull");
		}
		/**** Session Year ****/
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
			model.addAttribute("errorcode","sessionYear_isnull");
		}        
		/**** Session Type ****/
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
		/**** Session ****/
		Session selectedSession=null;
		if(houseType!=null&&selectedYear!=null&&sessionType!=null){
			try {
				selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		/**** section amendments ****/
		String defaultBillLanguage = selectedSession.getParameter(amendedBill.getType().getType()+"_defaultTitleLanguage");
		model.addAttribute("defaultBillLanguage", defaultBillLanguage);
		boolean isSuccessful = populateSectionAmendments(model, domain, selectedSession, deviceType);
		if(!isSuccessful) {
			return;
		}
		/**** role ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		/****UserGroupType****/
		String usergroupType=request.getParameter("usergroupType");
		if(usergroupType!=null){
			model.addAttribute("usergroupType",usergroupType);
		}else{
			usergroupType=(String) request.getSession().getAttribute("usergroupType");
			model.addAttribute("usergroupType",usergroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		/*****UserGroup*******/
		String usergroup=request.getParameter("usergroup");
		if(usergroup!=null){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",usergroup);
			request.getSession().removeAttribute("usergroup");
		}
		/**** Member related things ****/
		Member member = null;
		if(role.startsWith("MEMBER")) {
			/**** Primary Member****/	
			String memberNames=null;
			String primaryMemberName=null;
			member=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale);
			if(member!=null){
				model.addAttribute("primaryMember",member.getId());
				primaryMemberName=member.getFullname();	
				memberNames=primaryMemberName;
				model.addAttribute("formattedPrimaryMember",primaryMemberName);
			}else{
				logger.error("**** Authenticated user is not a member ****");
				model.addAttribute("errorcode","member_isnull");
			}
			/**** Supporting Members ****/
			List<SupportingMember> selectedSupportingMembers=domain.getSupportingMembers();
			List<Member> supportingMembers=new ArrayList<Member>();		
			if(selectedSupportingMembers!=null){
				if(!selectedSupportingMembers.isEmpty()){
					StringBuffer bufferFirstNamesFirst=new StringBuffer();
					for(SupportingMember i:selectedSupportingMembers){
						Member m=i.getMember();
						bufferFirstNamesFirst.append(m.getFullname()+",");
						supportingMembers.add(m);
					}
					bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
					model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
					model.addAttribute("supportingMembers",supportingMembers);
					memberNames=primaryMemberName+","+bufferFirstNamesFirst.toString();				
				}
			}
			model.addAttribute("memberNames",memberNames);
			/**** Constituency ****/
			Long houseId=selectedSession.getHouse().getId();
			MasterVO constituency=null;
			if(houseType.getType().equals("lowerhouse")){
				if(member != null){
					constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
					model.addAttribute("constituency",constituency.getName());
				}
			}else if(houseType.getType().equals("upperhouse")){
				Date currentDate=new Date();
				String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
				if(member != null){
					constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
					model.addAttribute("constituency",constituency.getName());
				}
			}			
		}
	}
	
	@Override
	protected void populateEdit(final ModelMap model, BillAmendmentMotion domain, final HttpServletRequest request) {
		/**** Locale ****/
		String locale=domain.getLocale();
		/**** Amended Bill ****/
		Bill amendedBill = domain.getAmendedBill();
		if(amendedBill==null) {
			logger.error("amendedBill is not set for this billamendmentmotion having id="+domain.getId()+".");
			model.addAttribute("errorcode", "amendedBill_null");			
			return;
		}
		model.addAttribute("amendedBill", amendedBill.getId());
		String amendedBillInfo = domain.getAmendedBillInfo();
		if(amendedBillInfo!=null && !amendedBillInfo.isEmpty()) {
			amendedBillInfo = amendedBillInfo.replace("#", "~");
		} else {
			model.addAttribute("errorcode","amendedBillInfo_notfound");
			return;
		}
		model.addAttribute("amendedBillInfo", amendedBillInfo);
		/**** Amended Bill Languages ****/		
		domain.setAmendedBillLanguages(amendedBill.findLanguagesOfContentDrafts());
		model.addAttribute("amendedBillLanguages", domain.getAmendedBillLanguages());
		/**** Device Type ****/
		DeviceType deviceType = domain.getType();
		if(deviceType==null) {
			logger.error("devicetype is not set for this billamendmentmotion having id="+domain.getId()+".");
			model.addAttribute("errorcode", "devicetype_null");			
			return;
		}
		model.addAttribute("formattedDeviceType", deviceType.getName());
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("selectedDeviceType", deviceType.getType());
		/**** House Type ****/
		HouseType houseType=domain.getHouseType();
		if(houseType==null) {
			logger.error("housetype is not set for this billamendmentmotion having id="+domain.getId()+".");
			model.addAttribute("errorcode", "housetype_null");
			return;
		}
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseType",houseType.getId());
		model.addAttribute("houseTypeType",houseType.getType());
		/**** Session ****/
		Session selectedSession=domain.getSession();
		if(selectedSession==null) {
			logger.error("session is not set for this bill.");
			model.addAttribute("errorcode", "session_null");
			return;
		}
		model.addAttribute("session",selectedSession.getId());
		/**** Session Year ****/
		Integer sessionYear=selectedSession.getYear();
		if(sessionYear==null) {
			logger.error("session year is not set for session of this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "sessionYear_null");
			return;
		}
		model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear",sessionYear);
		/**** Session Type ****/
		SessionType  sessionType=selectedSession.getType();
		if(sessionType==null) {
			logger.error("session type is not set for session of this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "sessionType_null");
			return;
		}
		model.addAttribute("formattedSessionType",sessionType.getSessionType());
		model.addAttribute("sessionType",sessionType.getId());
		/**** section amendments ****/
		String defaultBillLanguage = selectedSession.getParameter(amendedBill.getType().getType()+"_defaultTitleLanguage");
		model.addAttribute("defaultBillLanguage", defaultBillLanguage);
		boolean isSuccessful = populateSectionAmendments(model, domain, selectedSession, deviceType);
		if(!isSuccessful) {
			return;
		}
		/**** role ****/
		String role=request.getParameter("role");
		if(role!=null){
			model.addAttribute("role",role);
		}else{
			role=(String) request.getSession().getAttribute("role");
			model.addAttribute("role",role);
			request.getSession().removeAttribute("role");
		}
		/**** UserGroupType ****/
		UserGroupType userGroupType=null;
		try {
			userGroupType = this.populateObjectExtendingBaseDomainByStringFieldName(request, "usergroupType", UserGroupType.class, "type", locale);
		} catch (ELSException e1) {
			model.addAttribute("errorcode", "usergrouptype_populateError");	
			return;		
		}
		if(userGroupType!=null){
			model.addAttribute("usergroupType",userGroupType.getType());
		}else{
			String strUserGroupType=(String) request.getSession().getAttribute("usergroupType");
			userGroupType = UserGroupType.findByType(strUserGroupType, locale);
			model.addAttribute("usergroupType",strUserGroupType);
			request.getSession().removeAttribute("usergroupType");
		}
		/*****UserGroup*******/
		String usergroup=request.getParameter("usergroup");
		if(usergroup!=null){
			model.addAttribute("usergroup",usergroup);
		}else{
			usergroup=(String) request.getSession().getAttribute("usergroup");
			model.addAttribute("usergroup",usergroup);
			request.getSession().removeAttribute("usergroup");
		}
		//=================== Member related things ==================/
		String memberNames=null;
		/**** Primary Member ****/		
		String primaryMemberName=null;
		Member member=domain.getPrimaryMember();
		if(member==null) {
			logger.error("member is not set for this billamendmentmotion having id="+domain.getId()+".");
			model.addAttribute("errorcode", "member_null");
			return;
		}
		model.addAttribute("primaryMember",member.getId());
		primaryMemberName=member.getFullname();
		memberNames=primaryMemberName;
		model.addAttribute("formattedPrimaryMember",primaryMemberName);
		/**** Supporting Members ****/
		List<SupportingMember> selectedSupportingMembers=domain.getSupportingMembers();
		List<Member> supportingMembers=new ArrayList<Member>();
		if(selectedSupportingMembers!=null){
			if(!selectedSupportingMembers.isEmpty()){
//				Collections.sort(selectedSupportingMembers, SupportingMember.COMPARE_BY_POSITION);
				StringBuffer bufferFirstNamesFirst=new StringBuffer();
				for(SupportingMember i:selectedSupportingMembers){
					Member m=i.getMember();
					bufferFirstNamesFirst.append(m.getFullname()+",");
					supportingMembers.add(m);
				}
				/**** Dhananjay Borkar ****/
				//bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
				model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
				model.addAttribute("supportingMembers",supportingMembers);
				memberNames=primaryMemberName+","+bufferFirstNamesFirst.toString();
				model.addAttribute("memberNames",memberNames);
			}else{
				model.addAttribute("memberNames",memberNames);
			}
		}else{
			model.addAttribute("memberNames",memberNames);
		}		
		/**** Constituency ****/
		Long houseId=selectedSession.getHouse().getId();
		MasterVO constituency=null;
		if(houseType.getType().equals("lowerhouse")){
			if(member != null){
				constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
				model.addAttribute("constituency",constituency.getName());
			}
		}else if(houseType.getType().equals("upperhouse")){
			Date currentDate=new Date();
			String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
			if(member != null){
				constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
				model.addAttribute("constituency",constituency.getName());
			}
		}
		/**** Submission Date and Creation date****/ 
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat==null) {
			logger.error("custom parameter 'SERVER_DATETIMEFORMAT' is not set properly");
			model.addAttribute("errorcode", "server_datetimeformat_notset");
			return;
		} 
		if(dateTimeFormat.getValue()==null || dateTimeFormat.getValue()=="") {
			logger.error("custom parameter 'SERVER_DATETIMEFORMAT' is not set properly");
			model.addAttribute("errorcode", "server_datetimeformat_notset");
			return;
		}		    
		if(domain.getSubmissionDate()!=null){
			model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
			model.addAttribute("formattedSubmissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
		}
		if(domain.getCreationDate()!=null){
			model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
		}
		boolean isMotionRaisedByMinister = false;
		Date currentDate = new Date();
		if(domain.getSession().getEndDate().before(currentDate)) {
			isMotionRaisedByMinister = domain.getPrimaryMember().isActiveMinisterOn(domain.getSession().getEndDate(), locale);
		} else {
			isMotionRaisedByMinister = domain.getPrimaryMember().isActiveMinisterOn(domain.getSession().getEndDate(), locale);
		}
		if(!isMotionRaisedByMinister) {	
			model.addAttribute("isMotionRaisedByMinister", "no");
			if(domain.getDateOfOpinionSoughtFromLawAndJD()!=null){
				model.addAttribute("dateOfOpinionSoughtFromLawAndJD",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getDateOfOpinionSoughtFromLawAndJD()));
				model.addAttribute("formattedDateOfOpinionSoughtFromLawAndJD",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getDateOfOpinionSoughtFromLawAndJD()));
			}
		} else {
			model.addAttribute("isMotionRaisedByMinister", "yes");
		}
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		/**** Referenced Motions Starts ****/
		CustomParameter clubbedReferencedEntitiesVisibleUserGroups = CustomParameter.
				findByName(CustomParameter.class, "BAMOIS_ALLOWED_USERGROUP_TO_DO_VIEW_CLUBBING_REFERENCING", "");   
		if(clubbedReferencedEntitiesVisibleUserGroups != null){
			List<UserGroupType> userGroupTypes = 
					this.populateListOfObjectExtendingBaseDomainByDelimitedTypes(UserGroupType.class, clubbedReferencedEntitiesVisibleUserGroups.getValue(), ",", locale);
			Boolean isUserGroupAllowed = this.isObjectExtendingBaseDomainAvailableInList(userGroupTypes, userGroupType);
			if(isUserGroupAllowed){
				//populate parent
				if(domain.getParent()!=null){
					model.addAttribute("formattedParentNumber",FormaterUtil.formatNumberNoGrouping(domain.getParent().getNumber(), locale));
					model.addAttribute("parent",domain.getParent().getId());
				}
				//populate referenced entity
//				if(domain.getReferencedAdjournmentMotion()!=null){
//					Reference referencedEntityReference = BillAmendmentMotionController.populateReferencedEntityAsReference(domain, locale);
//					model.addAttribute("referencedMotion",referencedEntityReference);
//				}
				// Populate clubbed entities
				List<Reference> clubEntityReferences = BillAmendmentMotionController.populateClubbedEntityReferences(domain, locale);
				model.addAttribute("clubbedMotionsToShow",clubEntityReferences);
			}
		}
		/**** Status,Internal Status and recommendation Status ****/
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status==null) {
			logger.error("status is not set for this billamendmentmotion having id="+domain.getId()+".");
			model.addAttribute("errorcode", "status_null");
			return;
		}
		model.addAttribute("status",status.getId());
		model.addAttribute("memberStatusType",status.getType());
		if(internalStatus==null) {
			logger.error("internal status is not set for this billamendmentmotion having id="+domain.getId()+".");
			model.addAttribute("errorcode", "internalStatus_null");
			return;
		}
		model.addAttribute("internalStatus",internalStatus.getId());
		model.addAttribute("internalStatusType", internalStatus.getType());
		model.addAttribute("internalStatusPriority", internalStatus.getPriority());
		model.addAttribute("formattedInternalStatus", internalStatus.getName());
		/**** in case of assistant and other approving BAMOIS actors ****/
		if(userGroupType!=null &&
				(userGroupType.getType().equals(ApplicationConstants.ASSISTANT)||userGroupType.getType().equals(ApplicationConstants.SECTION_OFFICER))){
			model.addAttribute("level",1);
			/**** list of put up options available ****/
			if(recommendationStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION)
					|| recommendationStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_UNCLUBBING)
					|| recommendationStatus.getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				populateInternalStatus(model,recommendationStatus.getType(),userGroupType.getType(),locale,deviceType.getType());
			} else {
				populateInternalStatus(model,internalStatus.getType(),userGroupType.getType(),locale,deviceType.getType());
			}
		}
		if(recommendationStatus==null) {
			logger.error("recommendation status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "recommendationStatus_null");
			return;
		}
		model.addAttribute("recommendationStatus",recommendationStatus.getId());
		model.addAttribute("recommendationStatusType",recommendationStatus.getType());
		model.addAttribute("recommendationStatusPriority", recommendationStatus.getPriority());
		model.addAttribute("formattedRecommendationStatus", recommendationStatus.getName());
		/**** Auxiliary workflow statuses ****/
		Status translationStatus = null;
		Status opinionFromLawAndJDStatus = null;
		try {
			translationStatus = domain.findAuxiliaryWorkflowStatus(ApplicationConstants.TRANSLATION_WORKFLOW);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			return;
		}
		if(translationStatus==null) {
			translationStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_TRANSLATION_NOTSEND, domain.getLocale());
		}
		model.addAttribute("translationStatusType", translationStatus.getType());
		model.addAttribute("formattedTranslationStatus", translationStatus.getName());
		if(!isMotionRaisedByMinister) {
			try {
				opinionFromLawAndJDStatus = domain.findAuxiliaryWorkflowStatus(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW);
				if(opinionFromLawAndJDStatus==null) {
					opinionFromLawAndJDStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_NOTSEND, domain.getLocale());
				}
				model.addAttribute("opinionFromLawAndJDStatusType", opinionFromLawAndJDStatus.getType());
				model.addAttribute("formattedOpinionFromLawAndJDStatus", opinionFromLawAndJDStatus.getName());
			} catch(ELSException e) {
				model.addAttribute("error", e.getParameter());
				return;
			}
		}
		/**** Added by dhananjayb ****/
		/**** Remove unwanted actions for relevant workflows ****/
		if(userGroupType!=null
				&&(userGroupType.getType().equals(ApplicationConstants.ASSISTANT)||userGroupType.getType().equals(ApplicationConstants.SECTION_OFFICER))){
			@SuppressWarnings("unchecked")
			List<Status> internalStatuses = (List<Status>) model.get("internalStatuses");
			List<Status> statusesToRemove = new ArrayList<Status>();
			for(Status i: internalStatuses) {
				boolean isCandidateToRemove = false;
				if(i.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_TRANSLATION)
						&& !(translationStatus.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_TRANSLATION_NOTSEND))) {
					isCandidateToRemove = true;
				} 
				else if(i.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_OPINION_FROM_LAWANDJD)
						&& (isMotionRaisedByMinister || !(opinionFromLawAndJDStatus.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_NOTSEND)))) {
					isCandidateToRemove = true;
				}
				if(isCandidateToRemove) {
					statusesToRemove.add(i);
				}
			}
			if(!statusesToRemove.isEmpty()) {
				internalStatuses.removeAll(statusesToRemove);
			}
		}
		/**** Populating Put up options and Actors ****/
		if(domain.getInternalStatus()!=null){
			String internalStatusType=domain.getInternalStatus().getType();					
			if(userGroupType!=null && userGroupType.getType().equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_ADMISSION)							
							||internalStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_REJECTION)							
					)){				
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(usergroup));
				List<Reference> actors=WorkflowConfig.findBillAmendmentMotionActorsVO(domain, internalStatus, userGroup, 1, locale);
				model.addAttribute("internalStatusSelected",internalStatus.getId());
				model.addAttribute("recommendationStatusSelected",recommendationStatus.getId());
				model.addAttribute("actors",actors);
				if(actors!=null&&!actors.isEmpty()){
					String nextActor=actors.get(0).getId();
					String[] actorArr=nextActor.split("#");
					model.addAttribute("level", actorArr[2]);			
					model.addAttribute("localizedActorName", actorArr[3]+"("+actorArr[4]+")");					
				}
			}			
		}
	}
	
	private void populateInternalStatus(ModelMap model, String type, String userGroupType,String locale, String deviceType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
		CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+deviceType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+deviceType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		if(specificDeviceStatusUserGroupStatuses!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				logger.debug("populateNew", e);
				model.addAttribute("error",e.getParameter());
				e.printStackTrace();
			}
		}else if(specificDeviceUserGroupStatuses!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				logger.debug("populateNew", e);
				model.addAttribute("error",e.getParameter());
				e.printStackTrace();
			}
		}else if(specificStatuses!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				logger.debug("populateNew", e);
				model.addAttribute("error",e.getParameter());
				e.printStackTrace();
			}
		}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
				||userGroupType.equals(ApplicationConstants.SPEAKER)){
			CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_FINAL","");
			if(finalStatus!=null){
				try {
					internalStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					logger.debug("populateNew", e);
					model.addAttribute("error",e.getParameter());
					e.printStackTrace();
				}
			}else{
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						logger.debug("populateNew", e);
						model.addAttribute("error",e.getParameter());
						e.printStackTrace();
					}
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							logger.debug("populateNew", e);
							model.addAttribute("error",e.getParameter());
							e.printStackTrace();
						}
					}else{
						model.addAttribute("errorcode","billamendmentmotion_putup_options_final_notset");
					}		
				}
			}
		}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
				&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
			CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_RECOMMEND","");
			if(recommendStatus!=null){
				try {
					internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					logger.debug("populateNew", e);
					model.addAttribute("error",e.getParameter());
					e.printStackTrace();
				}
			}else{
				CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
				if(defaultCustomParameter!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						logger.debug("populateNew", e);
						model.addAttribute("error",e.getParameter());
						e.printStackTrace();
					}
				}else{
					model.addAttribute("errorcode","billamendmentmotion_putup_options_final_notset");
				}		
			}
		}
		/**** Internal Status****/
		model.addAttribute("internalStatuses",internalStatuses);		
	}
	
	@Override
	protected void customValidateCreate(final BillAmendmentMotion domain, final BindingResult result,
			final HttpServletRequest request) {
		try {
			String role = request.getParameter("role");
			/**** Supporting Members and various Validations ****/
			populateSupportingMembers(domain,role,request);
			/**** Version Mismatch ****/
			if (domain.isVersionMismatch()) {
				result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
			}
			/**** fields validation ****/
			if(domain.getHouseType()==null){
				result.rejectValue("houseType","HousetypeEmpty","Housetype is not set.");
			}
			if(domain.getType()==null){
				result.rejectValue("type","DeviceTypeEmpty","Devicetype is not set.");
			}
			if(domain.getSession()==null){
				result.rejectValue("session","SessionEmpty","Session is not set.");
			}
			if(domain.getPrimaryMember()==null){
				result.rejectValue("primaryMember","PrimaryMemberEmpty", "primary member is not set.");
			}	
			if(role.equals("BAMOIS_TYPIST")){
				//Empty check for number
				if(domain.getNumber()==null){
					result.rejectValue("number","NumberEmpty");
				}
				// Check duplicate entry for bill Number
				Boolean flag = BillAmendmentMotion.isDuplicateNumberExist(domain);
				if(flag){
					result.rejectValue("number", "NonUnique","Duplicate Number");
				}
			}
			/**** section amendments validation ****/
			boolean isSectionAmendmentInAtleastOneLanguage = false;		
			for(String amendingBillLanguage: domain.getAmendedBillLanguages().split("#")) {
				String amendingContentInThisLanguage = request.getParameter("sectionAmendment_amendingContent_"+amendingBillLanguage);
				if(amendingContentInThisLanguage!=null && !amendingContentInThisLanguage.isEmpty()) {
					isSectionAmendmentInAtleastOneLanguage = true;
//					String sectionNumberInThisLanguage = request.getParameter("sectionAmendment_sectionNumber_"+amendingBillLanguage);
//					if(sectionNumberInThisLanguage==null || sectionNumberInThisLanguage.isEmpty()) {
//						result.rejectValue("version","SectionNumberEmpty","Please fill section numbers for all languages.");
//					}
				}
			}
			if(isSectionAmendmentInAtleastOneLanguage==false) {
				result.rejectValue("version","ContentEmpty","Please fill amendment content in atleast one language.");
			}		
			String operation=request.getParameter("operation");
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.equals("approval")){
						/**** Approval ****/					
						if(domain.getSupportingMembers()==null){
							result.rejectValue("supportingMembers","SupportingMembersEmpty","there are no supporting members for approval.");
						} else if(domain.getSupportingMembers().isEmpty()){
							result.rejectValue("supportingMembers","there are no supporting members for approval.");						
						} else {
//							if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
//								validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
//							}else if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
//								validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
//							}

							//check if request is already sent for approval
							int count=0;
							for(SupportingMember i:domain.getSupportingMembers()){
								if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
									count++;
								}
							}
							if(count==0){
								result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent","request already sent to selected supporting members.");
							}
						}
					}else if(operation.equals("submit")){
						/**** Submission ****/						
											
					}
				}
			}
		} catch(ELSException e) {
			// TODO Auto-generated catch block
			logger.debug("customValidateCreate", e);
			request.setAttribute("error",e.getParameter());
			e.printStackTrace();
		} catch(Exception e) {
			// TODO Auto-generated catch block
			logger.debug("customValidateCreate", e);
			request.setAttribute("error","Some Error..");
			e.printStackTrace();
		} 		
	}
	
	@Override
	protected void customValidateUpdate(final BillAmendmentMotion domain, final BindingResult result,
			final HttpServletRequest request) {
		String role = request.getParameter("role");
		/**** Supporting Members and various Validations ****/
		try {
			populateSupportingMembers(domain,role,request);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			logger.debug("customValidateCreate", e);
			request.setAttribute("error",e.getParameter());
			e.printStackTrace();
		}
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		/**** fields validation ****/
		if(domain.getHouseType()==null){
			result.rejectValue("houseType","HousetypeEmpty","Housetype is not set.");
		}
		if(domain.getType()==null){
			result.rejectValue("type","DeviceTypeEmpty","Devicetype is not set.");
		}
		if(domain.getSession()==null){
			result.rejectValue("session","SessionEmpty","Session is not set.");
		}
		if(domain.getPrimaryMember()==null){
			result.rejectValue("primaryMember","PrimaryMemberEmpty", "primary member is not set.");
		}
		if(role.equals("BAMOIS_TYPIST")){
			//Empty check for number
			if(domain.getNumber()==null){
				result.rejectValue("number","NumberEmpty");
			}
			// Check duplicate entry for bill Number
			Boolean flag = BillAmendmentMotion.isDuplicateNumberExist(domain);
			if(flag){
				result.rejectValue("number", "NonUnique","Duplicate Number");
			}
		}
		String usergroupType=request.getParameter("usergroupType");
		/**** section amendments validation ****/
		boolean isSectionAmendmentInAtleastOneLanguage = false;		
		for(String amendingBillLanguage: domain.getAmendedBillLanguages().split("#")) {
			String amendingContentInThisLanguage = request.getParameter("sectionAmendment_amendingContent_"+amendingBillLanguage);
			if(amendingContentInThisLanguage!=null && !amendingContentInThisLanguage.isEmpty()) {
				isSectionAmendmentInAtleastOneLanguage = true;
				if(usergroupType!=null && usergroupType.equals(ApplicationConstants.ASSISTANT)) {
					String sectionNumberInThisLanguage = request.getParameter("sectionAmendment_sectionNumber_"+amendingBillLanguage);
					String revisedSectionNumberInThisLanguage = request.getParameter("revised_sectionAmendment_sectionNumber_"+amendingBillLanguage);
					if((revisedSectionNumberInThisLanguage==null || revisedSectionNumberInThisLanguage.isEmpty())
							&& (sectionNumberInThisLanguage==null || sectionNumberInThisLanguage.isEmpty())) {
						result.rejectValue("version","SectionNumberEmpty","Please fill section numbers for all languages.");
					}
				}				
			}
		}
		if(isSectionAmendmentInAtleastOneLanguage==false) {
			result.rejectValue("version","ContentEmpty","Please fill amendment content in atleast one language.");
		}
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Approval ****/					
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty","there are no supporting members for approval.");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","there are no supporting members for approval.");						
					} else {
//						if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
//							validateNumberOfSupportingMembersForHalfHourDiscussionFromQuestion(domain, result, request);
//						}else if(domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
//							validateNumberOfSupportingMembersForHalfHourDiscussionStandalone(domain, result, request);
//						}

						//check if request is already sent for approval
						int count=0;
						for(SupportingMember i:domain.getSupportingMembers()){
							if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
								count++;
							}
						}
						if(count==0){
							result.rejectValue("supportingMembers","SupportingMembersRequestAlreadySent","request already sent to selected supporting members.");
						}
					}
				}else if(operation.equals("submit")){
					/**** Submission ****/						
										
				}else if(operation.equals("startworkflow")){
//					String isMotionRaisedByMinister = request.getParameter("isMotionRaisedByMinister");
//					if(isMotionRaisedByMinister!=null && isMotionRaisedByMinister.equals("no")
//							&& domain.getOpinionSoughtFromLawAndJD()==null || domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
//						result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from law and judiciary department is not received");
//					}
					String languagesCompulsoryInSession = domain.getAmendedBill().getSession().getParameter(domain.getAmendedBill().getType().getType() + "_languagesCompulsory");
					/**** section amendment content validation for compulsory languages ****/
					boolean isSectionAmendmentContentInAllCompulsoryLanguages = true;			
					for(String languageCompulsoryInSession: languagesCompulsoryInSession.split("#")) {
						String sectionAmendmentContentInThisLanguage = request.getParameter("sectionAmendment_amendingContent_"+languageCompulsoryInSession);
						if(sectionAmendmentContentInThisLanguage==null) {
							String revisedSectionAmendmentContentInThisLanguage = request.getParameter("revised_sectionAmendment_amendingContent_"+languageCompulsoryInSession);
							if(revisedSectionAmendmentContentInThisLanguage==null) {
								isSectionAmendmentContentInAllCompulsoryLanguages = false;
								break;
							} else if(revisedSectionAmendmentContentInThisLanguage.isEmpty()) {
								isSectionAmendmentContentInAllCompulsoryLanguages = false;
								break;
							}							
						} else if(sectionAmendmentContentInThisLanguage.isEmpty()) {
							String revisedContentDraftTextInThisLanguage = request.getParameter("revised_sectionAmendment_amendingContent_"+languageCompulsoryInSession);
							if(revisedContentDraftTextInThisLanguage==null) {
								isSectionAmendmentContentInAllCompulsoryLanguages = false;
								break;
							} else if(revisedContentDraftTextInThisLanguage.isEmpty()) {
								isSectionAmendmentContentInAllCompulsoryLanguages = false;
								break;
							}
						}
					}
					if(isSectionAmendmentContentInAllCompulsoryLanguages==false) {
						result.rejectValue("version","ContentEmptyInRequiredLanguages","Please fill amending content in required languages.");
					}
				}
			}
		}
	}
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, BillAmendmentMotion domain,
			HttpServletRequest request) {		
		String selectedSupportingMembersIfErrors = "";
		if(domain.getSupportingMembers() != null){
			for(SupportingMember supportingMember : domain.getSupportingMembers() ){
				if(selectedSupportingMembersIfErrors.trim().length() > 0){
					selectedSupportingMembersIfErrors += "," + supportingMember.getMember().getId().toString();
				}else{
					selectedSupportingMembersIfErrors += supportingMember.getMember().getId().toString();
				}
			}
		}
		model.addAttribute("selectedSupportingMembersIfErrors", selectedSupportingMembersIfErrors);		
		/**** add/update section amendments in domain ****/
		List<SectionAmendment> sectionAmendments = new ArrayList<SectionAmendment>();
		try {
			sectionAmendments = this.updateSectionAmendments(domain, request);
		} catch (ELSException e) {
			e.printStackTrace();			
		}
		domain.setSectionAmendments(sectionAmendments);
		
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, BillAmendmentMotion domain,
			HttpServletRequest request) {	
		BillAmendmentMotion billAmendmentMotion = null;
		/**** updating various dates including submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strDateOfOpinionSoughtFromLawAndJD=request.getParameter("setDateOfOpinionSoughtFromLawAndJD");
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate!=null&&!strSubmissionDate.isEmpty()){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate!=null&&!strCreationDate.isEmpty()){
					domain.setCreationDate(format.parse(strCreationDate));
				}	
				if(strDateOfOpinionSoughtFromLawAndJD!=null&&!strDateOfOpinionSoughtFromLawAndJD.isEmpty()) {
					domain.setDateOfOpinionSoughtFromLawAndJD(format.parse(strDateOfOpinionSoughtFromLawAndJD));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}		
		/**** add/update section amendments in domain ****/
		List<SectionAmendment> sectionAmendments = new ArrayList<SectionAmendment>();
		try {
			sectionAmendments = this.updateSectionAmendments(domain, request);
		} catch (ELSException e) {
			e.printStackTrace();			
		}
		domain.setSectionAmendments(sectionAmendments);
		/**** add/update revised section amendments in domain ****/
		List<SectionAmendment> revisedSectionAmendments = new ArrayList<SectionAmendment>();
		try {
			revisedSectionAmendments = this.updateRevisedSectionAmendments(domain, request);
		} catch (ELSException e) {
			e.printStackTrace();			
		}
		domain.setRevisedSectionAmendments(revisedSectionAmendments);
		/**** added by dhananjayb ****/
		/**** resetting statuses in case this was workflow request ****/
		String operation = request.getParameter("operation");
		if(operation!=null) {
			if(!operation.isEmpty()) {				
				if(operation.equals("startworkflow")) {
					Status oldInternalStatus = null;
					String oldInternalStatusId = request.getParameter("oldInternalStatus");
					if(oldInternalStatusId==null || oldInternalStatusId.isEmpty()) {						
						billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, domain.getId());
						domain.setInternalStatus(billAmendmentMotion.getInternalStatus());
					} else {
						oldInternalStatus = Status.findById(Status.class, Long.parseLong(oldInternalStatusId));
						if(oldInternalStatus!=null) {
							domain.setInternalStatus(oldInternalStatus);
						} else {
							billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, domain.getId());
							domain.setInternalStatus(billAmendmentMotion.getInternalStatus());
						}						
					}
					Status oldRecommendationStatus = null;
					String oldRecommendationStatusId = request.getParameter("oldRecommendationStatus");
					if(oldRecommendationStatusId==null || oldRecommendationStatusId.isEmpty()) {						
						billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, domain.getId());
						domain.setRecommendationStatus(billAmendmentMotion.getRecommendationStatus());
					} else {
						oldRecommendationStatus = Status.findById(Status.class, Long.parseLong(oldRecommendationStatusId));
						if(oldRecommendationStatus!=null) {
							domain.setRecommendationStatus(oldRecommendationStatus);
						} else {
							billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, domain.getId());
							domain.setRecommendationStatus(billAmendmentMotion.getRecommendationStatus());
						}						
					}
				} 
			}
		}
		super.populateUpdateIfErrors(model, domain, request);
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final BillAmendmentMotion domain,
			final HttpServletRequest request) {
		/**** add/update section amendments in domain ****/
		List<SectionAmendment> sectionAmendments = new ArrayList<SectionAmendment>();
		try {
			sectionAmendments = this.updateSectionAmendments(domain, request);
		} catch (ELSException e) {
			e.printStackTrace();			
		}
		domain.setSectionAmendments(sectionAmendments);		
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String strUserGroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null && (domain.getSectionAmendments()!=null && !domain.getSectionAmendments().isEmpty())
				){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&(strUserGroupType.equals("member")||strUserGroupType.equals("typist"))){
							/****  submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/**** only those supporting memebrs will be included who have approved the requests ****/
							List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
							if(domain.getSupportingMembers()!=null){
								if(!domain.getSupportingMembers().isEmpty()){
									for(SupportingMember i:domain.getSupportingMembers()){
										if(strUserGroupType.equals("typist")){
											supportingMembers.add(i);
										}else{
											String decisionStatusType =i.getDecisionStatus().getType().trim();
											if(decisionStatusType.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
												supportingMembers.add(i);
											}
										}
									}
									domain.setSupportingMembers(supportingMembers);
								}
							}								
							/**** Status,Internal status and recommendation status is set to submit ****/
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);							 
						}
					} else{
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				} else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			} else{
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())
				&& (strUserGroupType.equals("member") || strUserGroupType.equals("typist"))){
			Member member=domain.getPrimaryMember();
			User user = null;
			try {
				user = User.findbyNameBirthDate(member.getFirstName(),member.getMiddleName(),member.getLastName(),member.getBirthDate());
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				logger.debug("populateNew", e);
				model.addAttribute("error",e.getParameter());
				e.printStackTrace();
			}
			domain.setCreatedBy(user.getCredential().getUsername());
		}else{
			domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		}
		domain.setDataEnteredBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
	}
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final BillAmendmentMotion domain,
			final HttpServletRequest request) {
		BillAmendmentMotion billAmendmentMotion = null;		
		/**** add/update section amendments in domain ****/
		List<SectionAmendment> sectionAmendments = new ArrayList<SectionAmendment>();
		try {
			sectionAmendments = this.updateSectionAmendments(domain, request);
		} catch (ELSException e) {
			e.printStackTrace();			
		}
		domain.setSectionAmendments(sectionAmendments);
		/**** add/update revised section amendments in domain ****/
		List<SectionAmendment> revisedSectionAmendments = new ArrayList<SectionAmendment>();
		try {
			revisedSectionAmendments = this.updateRevisedSectionAmendments(domain, request);
		} catch (ELSException e) {
			e.printStackTrace();			
		}
		domain.setRevisedSectionAmendments(revisedSectionAmendments);		
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String strUserGroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null && (domain.getSectionAmendments()!=null && !domain.getSectionAmendments().isEmpty())
				){
			if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&(strUserGroupType.equals("member")||strUserGroupType.equals("typist"))){
				if(operation!=null && !operation.isEmpty() && operation.trim().equals("submit")){
					/****  submission date is set ****/
					if(domain.getSubmissionDate()==null){
						domain.setSubmissionDate(new Date());
					}
					/**** only those supporting memebrs will be included who have approved the requests ****/
					List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
					if(domain.getSupportingMembers()!=null){
						if(!domain.getSupportingMembers().isEmpty()){
							for(SupportingMember i:domain.getSupportingMembers()){
								if(i.getDecisionStatus().getType().trim().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
									supportingMembers.add(i);
								}
							}
							domain.setSupportingMembers(supportingMembers);
						}
					}								
					/**** Status,Internal status and recommendation status is set to submit ****/
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);				
				} else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}			
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILLAMENDMENTMOTION_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** Edited On,Edited By and Edited As is set ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());		
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** In case of assistant if internal status=submit, 
		 * then change its internal and recommendation status to assistant processed ****/
		if(strUserGroupType!=null){
			if(strUserGroupType.equals("assistant")){
				Long id = domain.getId();
				billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, id);
				String internalStatus = billAmendmentMotion.getInternalStatus().getType();
				if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT)) {
					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}				
			}
		}
		/**** updating various dates including submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strDateOfOpinionSoughtFromLawAndJD=request.getParameter("setDateOfOpinionSoughtFromLawAndJD");
		CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
		if(dateTimeFormat!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US");
			try {
				if(strSubmissionDate!=null){
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if(strCreationDate!=null){
					domain.setCreationDate(format.parse(strCreationDate));
				}	
				if(strDateOfOpinionSoughtFromLawAndJD!=null&&!strDateOfOpinionSoughtFromLawAndJD.isEmpty()) {
					domain.setDateOfOpinionSoughtFromLawAndJD(format.parse(strDateOfOpinionSoughtFromLawAndJD));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final BillAmendmentMotion domain,
			final HttpServletRequest request) {		
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Supporting Member Workflow ****/
		String operation=request.getParameter("operation");
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("userrole",request.getParameter("userrole"));
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Added By Sandeep Singh ****/
					/**** process Started ****/
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,null, "0");	
					/**** Supporting members status changed to pending ****/
					BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
					List<SupportingMember> supportingMembers=billAmendmentMotion.getSupportingMembers();
					Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
					for(SupportingMember i:supportingMembers){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							i.setDecisionStatus(status);
							i.setRequestReceivedOn(new Date());
							i.setApprovalType("ONLINE");
							User user = null;
							try {
								user = User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								logger.debug("populateNew", e);
								model.addAttribute("error",e.getParameter());
								e.printStackTrace();
							}
							Credential credential=user.getCredential();							
							/**** Updating WorkflowDetails ****/
							for(WorkflowDetails j:workflowDetails){
								if(j.getAssignee().equals(credential.getUsername())){
									i.setWorkflowDetailsId(String.valueOf(j.getId()));
									break;
								}
							}							
							i.merge();
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void populateAfterUpdate(final ModelMap model, final BillAmendmentMotion domain,
			final HttpServletRequest request) {		
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Supporting Member Workflow ****/
		String operation=request.getParameter("operation");
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("userrole",request.getParameter("userrole"));
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Added By Sandeep Singh ****/
					/**** process Started ****/
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					List<Task> tasks=processService.getCurrentTasks(processInstance);
					List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,null, "0");	
					/**** Supporting members status changed to pending ****/
					BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
					List<SupportingMember> supportingMembers=billAmendmentMotion.getSupportingMembers();
					Status status=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_PENDING,domain.getLocale());
					for(SupportingMember i:supportingMembers){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)){
							i.setDecisionStatus(status);
							i.setRequestReceivedOn(new Date());
							i.setApprovalType("ONLINE");
							User user = null;
							try {
								user = User.findbyNameBirthDate(i.getMember().getFirstName(),i.getMember().getMiddleName(),i.getMember().getLastName(),i.getMember().getBirthDate());
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								logger.debug("populateNew", e);
								model.addAttribute("error",e.getParameter());
								e.printStackTrace();
							}
							Credential credential=user.getCredential();							
							/**** Updating WorkflowDetails ****/
							for(WorkflowDetails j:workflowDetails){
								if(j.getAssignee().equals(credential.getUsername())){
									i.setWorkflowDetailsId(String.valueOf(j.getId()));
									break;
								}
							}							
							i.merge();
						}
					}
				} else if(operation.equals("sendForTranslation")){					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actor");
					String nextUserGroupType="";
					String level="";
					if(nextuser!=null){						
						if(!nextuser.isEmpty()){
							String[] temp=nextuser.split("#");
							properties.put("pv_user",temp[0]);
							nextUserGroupType=temp[1];
							level=temp[2];
						}
					}
					String endFlagForAuxillaryWorkflow=request.getParameter("endFlagForAuxillaryWorkflow");
					properties.put("pv_endflag",endFlagForAuxillaryWorkflow);	
					properties.put("pv_timerflag", "off");
					properties.put("pv_mailflag", "off");					
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endFlagForAuxillaryWorkflow!=null){
						if(!endFlagForAuxillaryWorkflow.isEmpty()){
							if(endFlagForAuxillaryWorkflow.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails.create(domain,task,ApplicationConstants.TRANSLATION_WORKFLOW,ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_TRANSLATION,nextUserGroupType, level);
							}
						}
					}
					/**** Workflow Started ****/
					billAmendmentMotion.setRemarksForTranslation(domain.getRemarks());
					billAmendmentMotion.simpleMerge();					
				} else if(operation.equals("sendForOpinionFromLawAndJD")){					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actor");
					String nextUserGroupType="";
					String level="";
					if(nextuser!=null){						
						if(!nextuser.isEmpty()){
							String[] temp=nextuser.split("#");
							properties.put("pv_user",temp[0]);
							nextUserGroupType=temp[1];
							level=temp[2];
						}
					}
					String endFlagForAuxillaryWorkflow=request.getParameter("endFlagForAuxillaryWorkflow");
					properties.put("pv_endflag",endFlagForAuxillaryWorkflow);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endFlagForAuxillaryWorkflow!=null){
						if(!endFlagForAuxillaryWorkflow.isEmpty()){
							if(endFlagForAuxillaryWorkflow.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails.create(domain,task,ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW,ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_OPINION_FROM_LAWANDJD,nextUserGroupType, level);
							}
						}
					}
				} else if(operation.equals("startworkflow")){
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actor");
					String nextUserGroupType="";
					String level="";
					if(nextuser!=null){
						if(!nextuser.isEmpty()){
							String[] temp=nextuser.split("#");
							properties.put("pv_user",temp[0]);
							nextUserGroupType=temp[1];
							level=temp[2];
						}
					}
					String endFlag=request.getParameter("endFlag");
					properties.put("pv_endflag",endFlag);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);	
					/**** Stale State Exception ****/
					BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endFlag!=null){
						if(!endFlag.isEmpty()){
							if(endFlag.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								Workflow workflow = null;
								try {
									workflow = billAmendmentMotion.findWorkflowFromStatus();	
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
								}
								WorkflowDetails.create(domain,task,workflow.getType(),null,nextUserGroupType, level);
							}
						}
					}																	
				}
			}
		}
	}
	
	/**** BULK SUBMISSION (MEMBER) ****/

	@RequestMapping(value="/bulksubmission", method=RequestMethod.GET)
	public String getBulkSubmissionView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("motionType");
		String strLocale = locale.toString();
		String strItemsCount = request.getParameter("itemscount");

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strItemsCount != null && !(strItemsCount.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Integer itemsCount = Integer.parseInt(strItemsCount);
				Member primaryMember = Member.findMember(this.getCurrentUser().getFirstName(),
						this.getCurrentUser().getMiddleName(),
						this.getCurrentUser().getLastName(),
						this.getCurrentUser().getBirthDate(),
						strLocale);
				List<BillAmendmentMotion> billAmendmentMotions = new ArrayList<BillAmendmentMotion>();
				if(primaryMember != null){
					billAmendmentMotions = BillAmendmentMotion.findAllReadyForSubmissionByMember(session, primaryMember,deviceType, itemsCount, strLocale);	
				}	
				model.addAttribute("billAmendmentMotions", billAmendmentMotions);
				model.addAttribute("size", billAmendmentMotions.size());

				String userGroupType = request.getParameter("usergroupType");
				model.addAttribute("usergroupType", userGroupType);
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "billamendmentmotion/bulksubmission";
	}
	
	/**
	 * We want to provide a guarantee that all the motions submitted by a 
	 * particular member will get numbers assigned sequentially. Hence, the
	 * use of synchronized method.
	 */
	@Transactional
	@RequestMapping(value="bulksubmission", method=RequestMethod.POST)
	public synchronized String bulkSubmission(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String selectedItems = request.getParameter("items");

		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");

			List<BillAmendmentMotion> billAmendmentMotions = new ArrayList<BillAmendmentMotion>();
			for(String i : items) {
				Long id = Long.parseLong(i);
				BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, id);

				/**** Update Supporting Member ****/
				List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
				Status timeoutStatus = Status.findByType(
						ApplicationConstants.SUPPORTING_MEMBER_TIMEOUT, locale.toString());
				if(billAmendmentMotion.getSupportingMembers() != null
						&& ! billAmendmentMotion.getSupportingMembers().isEmpty()) {
					for(SupportingMember sm : billAmendmentMotion.getSupportingMembers()) {
						if(sm.getDecisionStatus().getType().equals(
								ApplicationConstants.SUPPORTING_MEMBER_NOTSEND) ||
								sm.getDecisionStatus().getType().equals(
										ApplicationConstants.SUPPORTING_MEMBER_PENDING)) {
							/**** Update Supporting Member ****/
							sm.setDecisionStatus(timeoutStatus);
							sm.setApprovalDate(new Date());	
							if(billAmendmentMotion.getSectionAmendments()!=null) {
					    		List<SectionAmendment> approvedSectionAmendments = new ArrayList<SectionAmendment>();
					        	for(SectionAmendment sectionAmendment : billAmendmentMotion.getSectionAmendments()) {
					        		SectionAmendment approvedSectionAmendment = new SectionAmendment();
					        		approvedSectionAmendment.setLanguage(sectionAmendment.getLanguage());
					        		approvedSectionAmendment.setSectionNumber(sectionAmendment.getSectionNumber());
					        		approvedSectionAmendment.setAmendedSection(sectionAmendment.getAmendedSection());
					        		approvedSectionAmendment.setAmendingContent(sectionAmendment.getAmendingContent());        		
					        		approvedSectionAmendment.setLocale(sectionAmendment.getLocale());
					        		approvedSectionAmendments.add(approvedSectionAmendment);
					        	}
					        	if(sm.getApprovedSectionAmendments()!=null) {
					        		sm.getApprovedSectionAmendments().addAll(approvedSectionAmendments);
					        	} else {
					        		sm.setApprovedSectionAmendments(approvedSectionAmendments);
					        	}
					        }
							sm.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_ONLINE);

							/**** Update Workflow Details ****/
							String strWorkflowdetails = sm.getWorkflowDetailsId();
							if(strWorkflowdetails != null && ! strWorkflowdetails.isEmpty()) {
								WorkflowDetails workflowDetails = WorkflowDetails.findById(
										WorkflowDetails.class, Long.parseLong(strWorkflowdetails));
								workflowDetails.setStatus("TIMEOUT");
								workflowDetails.setCompletionTime(new Date());
								workflowDetails.merge();

								/**** Complete Task ****/
								String strTaskId = workflowDetails.getTaskId();
								Task task = processService.findTaskById(strTaskId);
								processService.completeTask(task);
							}
						}

						if(! sm.getDecisionStatus().getType().equals(
								ApplicationConstants.SUPPORTING_MEMBER_NOTSEND)) {
							supportingMembers.add(sm);
						}
					}

					billAmendmentMotion.setSupportingMembers(supportingMembers);
				}

				/**** Update Status(es) ****/
				Status newstatus = Status.findByFieldName(Status.class, "type", 
						ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT, billAmendmentMotion.getLocale());
				billAmendmentMotion.setStatus(newstatus);
				billAmendmentMotion.setInternalStatus(newstatus);
				billAmendmentMotion.setRecommendationStatus(newstatus);

				/**** Edited On, Edited By and Edited As is set ****/
				billAmendmentMotion.setSubmissionDate(new Date());
				billAmendmentMotion.setEditedOn(new Date());
				billAmendmentMotion.setEditedBy(this.getCurrentUser().getActualUsername());

				String strUserGroupType = request.getParameter("usergroupType");
				if(strUserGroupType != null) {
					UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class,
							"type", strUserGroupType, billAmendmentMotion.getLocale());
					billAmendmentMotion.setEditedAs(userGroupType.getName());
				}

				/**** Bulk Submitted ****/
				billAmendmentMotion.setBulkSubmitted(true);

				/**** Update the Bill Amendment Motion object ****/
				billAmendmentMotion = billAmendmentMotion.merge();
				billAmendmentMotions.add(billAmendmentMotion);
			}

			model.addAttribute("billAmendmentMotions", billAmendmentMotions);
		}

		return "billamendmentmotion/bulksubmissionack";
	}
	
	@RequestMapping(value="/revisions/{billAmendmentMotionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("billAmendmentMotionId")  final Long billAmendmentMotionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=BillAmendmentMotion.findRevisions(billAmendmentMotionId,locale.toString());
		BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, billAmendmentMotionId);
		if(billAmendmentMotion != null){
			if(billAmendmentMotion.getAmendedBill()!=null){
				String amendedBillLanguages = billAmendmentMotion.getAmendedBill().findLanguagesOfContentDrafts();
				List<Language> orderedLanguages = new ArrayList<Language>();
				for(String language: amendedBillLanguages.split("#")) {
					Language languageObj = Language.findByFieldName(Language.class, "type", language, locale.toString());
					orderedLanguages.add(languageObj);
				}				
				orderedLanguages = Language.sort(orderedLanguages);
				model.addAttribute("languages", orderedLanguages);
			}
		} else {
			logger.error("bill amendment motion with id=" + billAmendmentMotionId + " does not exist.");
			model.addAttribute("errorcode","some_error");
			return "bill/error";
		}	
		model.addAttribute("drafts",drafts);		
		return "billamendmentmotion/revisions";
	}
	
	/*
	 * This method is used to view the approval status of a bill amendment motion from the supporting members
	 */
	@RequestMapping(value="/status/{billamendmentmotion}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("billamendmentmotion") final String billAmendmentMotion){
		BillAmendmentMotion billAmendmentMotionTemp=BillAmendmentMotion.findById(BillAmendmentMotion.class,Long.parseLong(billAmendmentMotion));
		List<SupportingMember> supportingMembers=billAmendmentMotionTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "billamendmentmotion/supportingmember";
	}

	private void populateSupportingMembers(final BillAmendmentMotion domain,final String role,final HttpServletRequest request) throws ELSException{
		/**** Supporting Members selected by Member in new/edit ****/
		String[] selectedSupportingMembers=request.getParameterValues("selectedSupportingMembers");
		try{
			if(selectedSupportingMembers == null){
				String supportingMembersIfErrors = request.getParameter("selectedSupportingMembersIfErrors");
				
				if(supportingMembersIfErrors != null){
					if(supportingMembersIfErrors.trim().length() > 0){
						selectedSupportingMembers = request.getParameter("selectedSupportingMembersIfErrors").split(",");
					}
				}
			}
		}catch(NullPointerException npe){
			logger.error("Request Parameter missing: selectedSupportingMembersIfErrors");
		}
		/**** Supporting Members which are already present in domain ****/
		List<SupportingMember> members=new ArrayList<SupportingMember>();
		if(domain.getId()!=null){
			BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
			members=billAmendmentMotion.getSupportingMembers();
		}		
		/**** New Status ****/
		Status notsendStatus=Status.findByFieldName(Status.class, "type",ApplicationConstants.SUPPORTING_MEMBER_NOTSEND, domain.getLocale());
		/**** New Supporting Members+Already present Supporting Members ****/
		List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
		if(selectedSupportingMembers!=null){
			if(selectedSupportingMembers.length>0){				
				for(String i:selectedSupportingMembers){
					SupportingMember supportingMember=null;
					Member member=Member.findById(Member.class, Long.parseLong(i));
					/**** If supporting member is already present then do nothing ****/
					for(SupportingMember j:members){
						if(j.getMember().getId()==member.getId()){
							supportingMember=j;
							break;
						}
					}
					/**** New Supporting Member ****/
					if(supportingMember==null){
						supportingMember=new SupportingMember();
						supportingMember.setMember(member);
						supportingMember.setLocale(domain.getLocale());
						supportingMember.setDecisionStatus(notsendStatus);
						/** Auto approval for submission by typist etc. roles **/
						CustomParameter supportingMemberAutoApprovalAllowedTo = 
								CustomParameter.findByName(CustomParameter.class, 
										"BAMOIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
						if(supportingMemberAutoApprovalAllowedTo != null) {
							if(supportingMemberAutoApprovalAllowedTo.getValue().contains(role)) {
								Status APPROVED = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
								supportingMember.setDecisionStatus(APPROVED);								
								supportingMember.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
								supportingMember.setApprovalDate(new Date());
								//-------------------set approved section amendments----------------//
								String billAmendmentMotionId = request.getParameter("billAmendmentMotion");
								if(billAmendmentMotionId!=null && !billAmendmentMotionId.isEmpty()) {
									BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(billAmendmentMotionId));	
									String amendedBillLanguages = request.getParameter("amendedBillLanguages");
									String languagesAllowedInSession = amendedBillLanguages;
									List<SectionAmendment> approvedSectionAmendments = new ArrayList<SectionAmendment>();
									for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
										String approvedSectionNumberInThisLanguage = request.getParameter("sectionAmendment_sectionNumber_"+languageAllowedInSession);
										String approvedSectionAmendmentContentInThisLanguage = request.getParameter("sectionAmendment_amendingContent_"+languageAllowedInSession);
										if(approvedSectionAmendmentContentInThisLanguage!=null && !approvedSectionAmendmentContentInThisLanguage.isEmpty()) {
											SectionAmendment approvedSectionAmendment = new SectionAmendment();		
											approvedSectionAmendment.setSectionNumber(approvedSectionNumberInThisLanguage);
											approvedSectionAmendment.setAmendingContent(approvedSectionAmendmentContentInThisLanguage);	
											if(approvedSectionAmendment.getLanguage()==null) {
												Language thisLanguage;
												String sectionAmendmentLanguageId = request.getParameter("sectionAmendment_language_id_"+languageAllowedInSession);
												if(sectionAmendmentLanguageId!=null && !sectionAmendmentLanguageId.isEmpty()) {
													thisLanguage = Language.findById(Language.class, Long.parseLong(sectionAmendmentLanguageId));
												} else {
													thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
												}					
												approvedSectionAmendment.setLanguage(thisLanguage);
											}
											if(approvedSectionAmendment.getAmendedSection()==null) {
												Section thisSection;
												String amendedSectionId = request.getParameter("sectionAmendment_amendedSection_id_"+languageAllowedInSession);
												if(amendedSectionId!=null && !amendedSectionId.isEmpty()) {
													thisSection = Section.findById(Section.class, Long.parseLong(amendedSectionId));
												} else {
													thisSection = Bill.findSection(billAmendmentMotion.getAmendedBill().getId(), languageAllowedInSession, approvedSectionNumberInThisLanguage);
												}
												approvedSectionAmendment.setAmendedSection(thisSection);
											}														
											approvedSectionAmendment.setLocale(domain.getLocale());
											approvedSectionAmendments.add(approvedSectionAmendment);
										}
									}
									supportingMember.setApprovedSectionAmendments(approvedSectionAmendments);			
//									/**** Set Position ****/
//									if(domain.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
//										if(bill.getSupportingMembers()!=null) {
//											if(!bill.getSupportingMembers().isEmpty()) {
//												synchronized (domain) {
//													Collections.sort(bill.getSupportingMembers(), SupportingMember.COMPARE_BY_POSITION);
//													Integer currentHighestPosition = bill.getSupportingMembers().get(bill.getSupportingMembers().size()-1).getPosition();
//													if(currentHighestPosition==null) {
//														domain.setPosition(1);
//													} else {
//														domain.setPosition(currentHighestPosition + 1);
//													}
//												}						
//											}
//										}
//									}			
								}								
							}
						}
					}
					/*** List is updated ****/
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
	}
	
	private boolean populateSectionAmendments(ModelMap model,
			BillAmendmentMotion domain, Session selectedSession,
			DeviceType deviceType) {
		Bill amendedBill = domain.getAmendedBill();
		//TODO: languages allowed should be only those in which bill draft is mentioned
		String languagesAllowedInSession = domain.getAmendedBillLanguages();
		if(languagesAllowedInSession != null && !languagesAllowedInSession.isEmpty()) {
			List<Language> languagesAllowedForSectionAmendment = new ArrayList<Language>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
				Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
				languagesAllowedForSectionAmendment.add(languageAllowed);
			}
			List<SectionAmendment> sectionAmendments = new ArrayList<SectionAmendment>();			
			if(domain.getSectionAmendments()!=null && !domain.getSectionAmendments().isEmpty()) {				
				sectionAmendments.addAll(domain.getSectionAmendments());
				for(SectionAmendment sectionAmendment: domain.getSectionAmendments()) {
					languagesAllowedForSectionAmendment.remove(sectionAmendment.getLanguage());		
					//find referred section text if the amended section exists in system
					Section amendedSectionInGivenLanguage = sectionAmendment.getAmendedSection();
					if(amendedSectionInGivenLanguage!=null) {
						model.addAttribute("referredSectionText_"+sectionAmendment.getLanguage().getType(), amendedSectionInGivenLanguage.getText());
					}
				}				
			}
			if(!languagesAllowedForSectionAmendment.isEmpty()) {								
				for(Language languageAllowedForSectionAmendment: languagesAllowedForSectionAmendment) {
					SectionAmendment sectionAmendment = new SectionAmendment();
					sectionAmendment.setLanguage(languageAllowedForSectionAmendment);
					sectionAmendment.setSectionNumber("");					
					sectionAmendment.setAmendingContent("");
					sectionAmendments.add(sectionAmendment);
				}
			}
			model.addAttribute("sectionAmendments",sectionAmendments);
			if(domain.getRevisedSectionAmendments()!=null && !domain.getRevisedSectionAmendments().isEmpty()) {
				for(SectionAmendment revisedSectionAmendment: domain.getRevisedSectionAmendments()) {					
					model.addAttribute("revisedSectionAmendment_sectionNumber_"+revisedSectionAmendment.getLanguage().getType(), revisedSectionAmendment.getSectionNumber());
					model.addAttribute("revisedSectionAmendment_amendingContent_"+revisedSectionAmendment.getLanguage().getType(), revisedSectionAmendment.getAmendingContent());
					if(revisedSectionAmendment.getAmendedSection()!=null) {
						model.addAttribute("revisedSectionAmendment_amendedSection_id_"+revisedSectionAmendment.getLanguage().getType(), revisedSectionAmendment.getAmendedSection().getId());
					}					
					model.addAttribute("revisedSectionAmendment_id_"+revisedSectionAmendment.getLanguage().getType(), revisedSectionAmendment.getId());
				}
			}
			return true;
		} else {
			logger.error("**** Session Parameter '" + amendedBill.getType().getType() + "_languagesAllowed' is not set. ****");
			model.addAttribute("errorcode",amendedBill.getType().getType() + "__languagesAllowed_notset");
			return false;
		}		
	}
	
	private List<SectionAmendment> updateSectionAmendments(BillAmendmentMotion domain, HttpServletRequest request) throws ELSException {
		List<SectionAmendment> sectionAmendments = new ArrayList<SectionAmendment>();
		String languagesAllowedInSession = domain.getAmendedBillLanguages();
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String amendedSectionNumberInThisLanguage = request.getParameter("sectionAmendment_sectionNumber_"+languageAllowedInSession);
			String amendingContentInThisLanguage = request.getParameter("sectionAmendment_amendingContent_"+languageAllowedInSession);
			if(amendingContentInThisLanguage!=null && !amendingContentInThisLanguage.isEmpty()) {
				SectionAmendment sectionAmendment = null;				
				String sectionAmendmentIdInThisLanguage = request.getParameter("sectionAmendment_id_"+languageAllowedInSession);
				if(sectionAmendmentIdInThisLanguage!=null && !sectionAmendmentIdInThisLanguage.isEmpty()) {
					sectionAmendment = SectionAmendment.findById(SectionAmendment.class, Long.parseLong(sectionAmendmentIdInThisLanguage));					
				} else {
					sectionAmendment = new SectionAmendment();
				}
				sectionAmendment.setSectionNumber(amendedSectionNumberInThisLanguage);
				sectionAmendment.setAmendingContent(amendingContentInThisLanguage);
				if(sectionAmendment.getLanguage()==null) {
					Language thisLanguage;
					String sectionAmendmentLanguageId = request.getParameter("sectionAmendment_language_id_"+languageAllowedInSession);
					if(sectionAmendmentLanguageId!=null && !sectionAmendmentLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(sectionAmendmentLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
					}					
					sectionAmendment.setLanguage(thisLanguage);
				}
//				if(sectionAmendment.getAmendedSection()==null) {
//					Section thisSection;
//					String amendedSectionId = request.getParameter("sectionAmendment_amendedSection_id_"+languageAllowedInSession);
//					if(amendedSectionId!=null && !amendedSectionId.isEmpty()) {
//						thisSection = Section.findById(Section.class, Long.parseLong(amendedSectionId));
//					} else {
//						thisSection = Bill.findSection(domain.getAmendedBill().getId(), languageAllowedInSession, amendedSectionNumberInThisLanguage);
//					}
//					sectionAmendment.setAmendedSection(thisSection);
//				}
				Section thisSection = sectionAmendment.getAmendedSection();
				if(thisSection!=null && !thisSection.getNumber().equals(amendedSectionNumberInThisLanguage)) {
					thisSection = Bill.findSection(domain.getAmendedBill().getId(), languageAllowedInSession, amendedSectionNumberInThisLanguage);
					sectionAmendment.setAmendedSection(thisSection);
				} else {	
					thisSection = Bill.findSection(domain.getAmendedBill().getId(), languageAllowedInSession, amendedSectionNumberInThisLanguage);	
					sectionAmendment.setAmendedSection(thisSection);
				}				
				sectionAmendment.setLocale(domain.getLocale());
				sectionAmendments.add(sectionAmendment);
			}
		}
		return sectionAmendments;
	}
	
	private List<SectionAmendment> updateRevisedSectionAmendments(BillAmendmentMotion domain, HttpServletRequest request) throws ELSException {
		List<SectionAmendment> revisedSectionAmendments = new ArrayList<SectionAmendment>();
		String languagesAllowedInSession = domain.getAmendedBillLanguages();
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String originalAmendedSectionNumberInThisLanguage = request.getParameter("sectionAmendment_sectionNumber_"+languageAllowedInSession);
			String revisedAmendedSectionNumberInThisLanguage = request.getParameter("revised_sectionAmendment_sectionNumber_"+languageAllowedInSession);
			String originalAmendingContentInThisLanguage = request.getParameter("sectionAmendment_amendingContent_"+languageAllowedInSession);
			String revisedAmendingContentInThisLanguage = request.getParameter("revised_sectionAmendment_amendingContent_"+languageAllowedInSession);
			if((revisedAmendedSectionNumberInThisLanguage!=null && !revisedAmendedSectionNumberInThisLanguage.isEmpty())
					|| (revisedAmendingContentInThisLanguage!=null && !revisedAmendingContentInThisLanguage.isEmpty())) {
				SectionAmendment revisedSectionAmendment = null;	
				String revisedSectionAmendmentIdInThisLanguage = request.getParameter("revised_sectionAmendment_id_"+languageAllowedInSession);
				if(revisedSectionAmendmentIdInThisLanguage!=null && !revisedSectionAmendmentIdInThisLanguage.isEmpty()) {
					revisedSectionAmendment = SectionAmendment.findById(SectionAmendment.class, Long.parseLong(revisedSectionAmendmentIdInThisLanguage));					
				} else {
					revisedSectionAmendment = new SectionAmendment();
				}
				if(revisedAmendedSectionNumberInThisLanguage==null || revisedAmendedSectionNumberInThisLanguage.isEmpty()) {
					revisedAmendedSectionNumberInThisLanguage = originalAmendedSectionNumberInThisLanguage;
				}
				revisedSectionAmendment.setSectionNumber(revisedAmendedSectionNumberInThisLanguage);
				if(revisedAmendingContentInThisLanguage==null || revisedAmendingContentInThisLanguage.isEmpty()) {
					revisedAmendingContentInThisLanguage = originalAmendingContentInThisLanguage;
				}
				revisedSectionAmendment.setAmendingContent(revisedAmendingContentInThisLanguage);
				if(revisedSectionAmendment.getLanguage()==null) {
					Language thisLanguage;
					String revisedSectionAmendmentLanguageId = request.getParameter("revised_sectionAmendment_language_id_"+languageAllowedInSession);
					if(revisedSectionAmendmentLanguageId!=null && !revisedSectionAmendmentLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(revisedSectionAmendmentLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
					}					
					revisedSectionAmendment.setLanguage(thisLanguage);
				}
				Section thisSection = null;
				String revisedAmendedSectionId = request.getParameter("revised_sectionAmendment_amendedSection_id_"+languageAllowedInSession);
				if(revisedAmendedSectionId!=null && !revisedAmendedSectionId.isEmpty()) {
					thisSection = Section.findById(Section.class, Long.parseLong(revisedAmendedSectionId));
					if(thisSection!=null && !thisSection.getNumber().equals(revisedAmendedSectionNumberInThisLanguage)) {
						thisSection = Bill.findSection(domain.getAmendedBill().getId(), languageAllowedInSession, revisedAmendedSectionNumberInThisLanguage);
					}
				} else {	
					thisSection = Bill.findSection(domain.getAmendedBill().getId(), languageAllowedInSession, revisedAmendedSectionNumberInThisLanguage);			
				}
				revisedSectionAmendment.setAmendedSection(thisSection);
				revisedSectionAmendment.setLocale(domain.getLocale());
				revisedSectionAmendments.add(revisedSectionAmendment);
			}
		}
		return revisedSectionAmendments;
	}
	
	public static List<Reference> populateClubbedEntityReferences(BillAmendmentMotion domain, String locale) {
		List<Reference> references = new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities=BillAmendmentMotion.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null){
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getBillAmendmentMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getBillAmendmentMotion().getId()));
				references.add(reference);
			}
		}
		return references;
	}	
	
	public static Reference populateReferencedEntityAsReference(BillAmendmentMotion domain, String locale) {
		Reference reference=new Reference();
//		reference.setId(String.valueOf(domain.getReferencedBillAmendmentMotion().getId()));
//		Integer number = ((BillAmendmentMotion)domain.getReferencedBillAmendmentMotion().getDevice()).getNumber();
//		reference.setName(FormaterUtil.
//				getNumberFormatterNoGrouping(domain.getLocale()).format(number));
//		reference.setNumber(String.valueOf(((BillAmendmentMotion)domain.getReferencedBillAmendmentMotion().getDevice()).getId()));
		return reference;
	}
	
	
	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {		
		BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, id);
		if(billAmendmentMotion!=null){
			Status status=billAmendmentMotion.getStatus();
			if(status.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_INCOMPLETE)
					||status.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
}
