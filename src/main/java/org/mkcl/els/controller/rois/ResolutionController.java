package org.mkcl.els.controller.rois;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.common.xmlvo.ResolutionXmlVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.question.QuestionController;
import org.mkcl.els.domain.chart.Chart;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.ResolutionDraft;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.ResolutionDraft;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Rule;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("resolution")
public class ResolutionController extends GenericController<Resolution> {
	
	@Autowired
	private IProcessService processService;

	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		model.addAttribute("moduleLocale", locale);
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try{
				deviceTypes = DeviceType.findDeviceTypesStartingWith("resolutions", locale);
			
			}catch (ELSException e) {
				model.addAttribute(this.getClass().getName(), e.getParameter());
			}
			model.addAttribute("deviceTypes", deviceTypes);
			/**** Default Value ****/
			model.addAttribute("deviceType",deviceType.getId());
			/**** Access Control Based on device Type ****/
			model.addAttribute("deviceTypeType",deviceType.getType());
			/**** House Types ****/
			// Populate House types configured for the current user
			List<HouseType> houseTypes=null;
			try {
				houseTypes = QuestionController.getHouseTypes(currentUser, deviceType, locale);
			} catch (ELSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			model.addAttribute("houseTypes", houseTypes);			
			// Populate default House type
			HouseType authUserHouseType = null;
			if(houseTypes!=null && houseTypes.size()==1) {
				authUserHouseType = houseTypes.get(0);
				model.addAttribute("houseType", authUserHouseType.getType());
			} else {
				try {
					authUserHouseType = QuestionController.getHouseType(currentUser, locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("houseType", authUserHouseType.getType());
			}	
			String houseType = authUserHouseType.getType();
			/**** Session Types ****/
			List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
			/**** Latest Session of a House Type ****/
			Session lastSessionCreated = null;
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
				
				/*** Session Year and Session Type ****/
				Integer year=new GregorianCalendar().get(Calendar.YEAR);
				if(lastSessionCreated.getId()!=null){
					year=lastSessionCreated.getYear();
					model.addAttribute("sessionType",lastSessionCreated.getType().getId());
				}else{
					model.addAttribute("errorcode","nosessionentriesfound");
				}
				model.addAttribute("sessionTypes",sessionTypes);
				
				/**** Years ****/
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
				
			} catch (ELSException e) {				
				e.printStackTrace();
				model.addAttribute("error", e.getParameter());
			}
			
			/**** Custom Parameter To Determine The Usergroup and usergrouptype of rois users ****/			
			List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
			User user = User.findById(User.class,this.getCurrentUser().getUserId());
			Credential credential =user.getCredential();
			String userGroupType=null;
			if(userGroups!=null){
				if(!userGroups.isEmpty()){
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROIS_ALLOWED_USERGROUPTYPES", "");
					if(customParameter!=null){
						String allowedUserGroups=customParameter.getValue(); 
						for(UserGroup i:userGroups){
							UserGroup userGroup = UserGroup.findActive(credential, i.getUserGroupType(), new Date(), locale.toString());
							if(userGroup != null && i.getId().equals(userGroup.getId()) && allowedUserGroups.contains(i.getUserGroupType().getType())){
								/**** Authenticated User's usergroup and usergroupType ****/
								model.addAttribute("usergroup",i.getId());
								userGroupType=i.getUserGroupType().getType();
								model.addAttribute("usergroupType",userGroupType);
								/**** Resolution Status Allowed ****/
								CustomParameter allowedStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_GRID_STATUS_ALLOWED_"+userGroupType.toUpperCase(), "");
								List<Status> status=new ArrayList<Status>();
								if(allowedStatus!=null){
									try {
										status=Status.findStatusContainedIn(allowedStatus.getValue(),locale);
									} catch (ELSException e) {
										model.addAttribute("error", e.getParameter());
										e.printStackTrace();
									}
								}else{
									CustomParameter defaultAllowedStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
									if(defaultAllowedStatus!=null){
										try {
											status=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
									}else{
										model.addAttribute("errorcode","resolution_status_allowed_by_default_not_set");
									}
								}
								model.addAttribute("status",status);
								break;
							}
						}
					}else{
						model.addAttribute("errorcode","rois_allowed_usergroups_notset");
					}
				}else{
					model.addAttribute("errorcode","current_user_has_no_usergroups");
				}
			}else{
				model.addAttribute("errorcode","current_user_has_no_usergroups");
			}
			/**** Roles ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			for(Role i:roles){
				if(i.getType().startsWith("MEMBER_")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().contains("ROIS_CLERK")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().contains("ROIS_TYPIST")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("ROIS_")){
					model.addAttribute("role",i.getType());
					break;
				}
			}
				model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());
				/**** File Options(Obtain Dynamically)****/
				HouseType houseType1=HouseType.findByFieldName(HouseType.class, "type", houseType, locale);
				
				if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					if(userGroupType!=null&&!userGroupType.isEmpty()&&userGroupType.equals("assistant")){
						int highestFileNo;
						try {
							highestFileNo = Resolution.findHighestFileNo(lastSessionCreated,deviceType,locale,houseType1);
							model.addAttribute("highestFileNo",highestFileNo);
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}
					}
				}else if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
					HouseType lowerHouse=HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.LOWER_HOUSE, locale);
					HouseType upperHouse=HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE, locale);
					if(userGroupType!=null&&!userGroupType.isEmpty()&&userGroupType.equals("assistant")){
						int highestFileNoLowerHouse=0;
						int highestFileNoUpperHouse=0;
						int highestFileNo=0;
						
						try {
							if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
								Session upperHouseSession=Session.findLatestSession(upperHouse);
								highestFileNoLowerHouse=Resolution.findHighestFileNo(lastSessionCreated,deviceType,locale,houseType1);
								highestFileNoUpperHouse=Resolution.findHighestFileNo(upperHouseSession,deviceType,locale,houseType1);
								
							}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
								Session lowerHouseSession=Session.findLatestSession(lowerHouse);
								highestFileNoLowerHouse=Resolution.findHighestFileNo(lastSessionCreated,deviceType,locale,houseType1);
								highestFileNoUpperHouse=Resolution.findHighestFileNo(lowerHouseSession,deviceType,locale,houseType1);
								
							}
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}
						highestFileNo=highestFileNoLowerHouse+highestFileNoUpperHouse;
						model.addAttribute("highestFileNo",highestFileNo);
					}
				}
				
				/***SubDepartment***/
				List<Group> groups = Group.findAllByFieldName(Group.class, "session", lastSessionCreated, "number", ApplicationConstants.ASC, locale);
				List<SubDepartment> subdepartments = new ArrayList<SubDepartment>();
				for(Group g : groups){
					subdepartments.addAll(g.getSubdepartments());
				}
				model.addAttribute("subdepartments", subdepartments);
				
			}else{
			model.addAttribute("errorcode","workunderprogress");
		}		
	}

	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** For Clerk and other ROIS roles assistant grid is visible ****/
		String role=request.getParameter("role");
		String newUrlPattern=urlPattern;	
		String strDeviceTypeId = request.getParameter("deviceType");
		String deviceType = null;		
		
		if(strDeviceTypeId != null){
			if(!strDeviceTypeId.isEmpty()){
				DeviceType deviceTypeObject = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceTypeId));
				if(deviceTypeObject != null) {
					deviceType = deviceTypeObject.getType();
				}
			}
		}
		
		if(deviceType != null) {
			if(deviceType.equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				if(!request.getParameter("houseType").isEmpty()){
					if(request.getParameter("houseType").equals(ApplicationConstants.LOWER_HOUSE)){
						newUrlPattern=ApplicationConstants.GOVERNMENT_RESOLUTION_LOWERHOUSEGRID;
					}else if(request.getParameter("houseType").equals(ApplicationConstants.UPPER_HOUSE)){
						newUrlPattern=ApplicationConstants.GOVERNMENT_RESOLUTION_UPPERHOUSEGRID;
					}
					
				}
				if(role.contains("ROIS_")&& (!role.contains("TYPIST"))){					
					newUrlPattern=newUrlPattern+"?usergroup=assistant";
				}				
			} else if(deviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
				if(!request.getParameter("houseType").isEmpty()){
					if(request.getParameter("houseType").equals(ApplicationConstants.LOWER_HOUSE)){
						newUrlPattern=ApplicationConstants.RESOLUTION_LOWERHOUSEGRID;
					}else if(request.getParameter("houseType").equals(ApplicationConstants.UPPER_HOUSE)){
						newUrlPattern=ApplicationConstants.RESOLUTION_UPPERHOUSEGRID;
					}
					
				}
				if(role.contains("ROIS_")&& (!role.contains("TYPIST"))){					
					newUrlPattern=newUrlPattern+"?usergroup=assistant";
				}else if(role.contains("ROIS_")&& (role.contains("TYPIST"))){
					newUrlPattern=newUrlPattern+"?usergroup=typist";
				}
			}
		}
		
		return newUrlPattern;
	}
	
	@Override
	protected void populateList(final ModelMap model, final HttpServletRequest request,
	            final String locale, final AuthUser currentUser) {
		/** populateList in superclass **/
	    super.populateList(model, request, locale, currentUser);
	    
	    /** selected device type **/
	    String selectedResolutionType=request.getParameter("deviceType");
		if(selectedResolutionType==null){
			selectedResolutionType=request.getParameter("type");
		}
		DeviceType resolutionType=null;
		if(selectedResolutionType!=null){
			if(!selectedResolutionType.isEmpty()){
				resolutionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedResolutionType));
			}else{
				logger.error("**** Check request parameter 'ResolutionType' for no value ****");
				model.addAttribute("errorcode","resolutionType_isempty");		
			}
		}else{
			logger.error("**** Check request parameter 'resolutionType' for null value ****");
			model.addAttribute("errorcode","resolutionType_isnull");
		}
		
		/** report formats for karyavali report if selected device type requires the report **/
		if(resolutionType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);
			MasterVO rtfFormat = new MasterVO();
			rtfFormat.setName("RTF");
			rtfFormat.setValue("RTF");
			outputFormats.add(rtfFormat);						
			model.addAttribute("outputFormats", outputFormats);			
		}	    
	}
	
	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** Member and Clerk can only create new questions ****/
		String role=request.getParameter("role");		
		if(role!=null){
			if(!role.isEmpty()){
				if(role.startsWith("MEMBER_")||role.contains("ROIS_TYPIST")){
					return servletPath;
				}
			}
		}			
		/**** For others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return servletPath.replace("new","error");
	}
	
	@Override
	protected void populateNew(final ModelMap model, final Resolution domain,
            final String locale, final HttpServletRequest request) {
	    try{
	    	domain.setLocale(locale);
	    	String selectedHouseType=this.getCurrentUser().getHouseType();
	    	HouseType houseType=null;
	 		if(selectedHouseType!=null){
	 			if(!selectedHouseType.isEmpty()){
	  				try {
	 					Long houseTypeId=Long.parseLong(selectedHouseType);
	 					houseType=HouseType.findById(HouseType.class,houseTypeId);
	 				} catch (NumberFormatException e) {
	 					houseType=HouseType.findByFieldName(HouseType.class,"type",selectedHouseType, locale);
	 				}
	 				model.addAttribute("formattedHouseType",houseType.getName());
	 				model.addAttribute("houseTypeType",houseType.getType());
	 				model.addAttribute("houseType",houseType.getId());
	 			}else{
	 				logger.error("**** Check request parameter 'houseType' for no value ****");
	 				model.addAttribute("errorcode","houseType_isempty");	
	 			}
	 		}else{
	 			logger.error("**** Check request parameter 'houseType' for null value ****");
	 			model.addAttribute("errorcode","houseType_isnull");
	 		}
	 		 
	 		
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
			
			String selectedResolutionType=request.getParameter("deviceType");
			if(selectedResolutionType==null){
				selectedResolutionType=request.getParameter("type");
			}
			DeviceType resolutionType=null;
			if(selectedResolutionType!=null){
				if(!selectedResolutionType.isEmpty()){
					resolutionType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedResolutionType));
					model.addAttribute("formattedResolutionType", resolutionType.getName());
					model.addAttribute("resolutionType", resolutionType.getId());
					model.addAttribute("deviceType",resolutionType.getId());
					model.addAttribute("selectedResolutionType", resolutionType.getType());
				}else{
					logger.error("**** Check request parameter 'ResolutionType' for no value ****");
					model.addAttribute("errorcode","resolutionType_isempty");		
				}
			}else{
				logger.error("**** Check request parameter 'resolutionType' for null value ****");
				model.addAttribute("errorcode","resolutionType_isnull");
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
			Session selectedSession=null;
			String memberName=null;
			Member member=null;
			if(houseType!=null&&selectedYear!=null&&sessionType!=null){
				try {
					selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
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
			if(role.startsWith("MEMBER")){
				member=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale);
				if(member!=null){
					model.addAttribute("member",member.getId());
					memberName=member.getFullname();
					model.addAttribute("formattedMember",memberName);
				}else{
					logger.error("**** Authenticated user is not a member ****");
					model.addAttribute("errorcode","member_isnull");
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
				
				if(member!=null){
					Integer count;
					try {
						count = Resolution.findResolutionCount(member, selectedSession, resolutionType,locale);
						model.addAttribute("resolutionCount", count);
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
						e.printStackTrace();
					}
					
				}else{
					logger.error("**** Authenticated user is not a member ****");
					model.addAttribute("errorcode","member_isnull");
				}
			}else {
				if(domain.getMember() != null){
					model.addAttribute("member",domain.getMember().getId());
					memberName = domain.getMember().getFullname();
					model.addAttribute("formattedMember",memberName);
				}
			}
					
			/**** Ministries ****/
			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
			if(strRotationOrderPubDate!=null){
				try {
					rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
					model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
					Date currentDate=new Date();
						if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
							if(resolutionType.getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){					
								List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
								model.addAttribute("ministries",ministries);
								Ministry ministry=domain.getMinistry();
								if(ministry!=null){
									model.addAttribute("ministrySelected",ministry.getId());						
									/**** Sub Departments ****/
									List<SubDepartment> subDepartments=
											MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
									model.addAttribute("subDepartments",subDepartments);
									SubDepartment subDepartment=domain.getSubDepartment();
									if(subDepartment!=null){
										model.addAttribute("subDepartmentSelected",subDepartment.getId());
									}
								}							
							}else if(resolutionType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
								if(role.startsWith("MEMBER")){
									List<MemberMinister> memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
									/**** To check whether to populate other ministries also for this minister ****/
									Boolean isAllowedToAccessOtherMinistries = false;
									CustomParameter rolesAllowedForAccessingOtherMinistriesParameter = CustomParameter.findByName(CustomParameter.class, "MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION", "");
									if(rolesAllowedForAccessingOtherMinistriesParameter != null) {
										if(rolesAllowedForAccessingOtherMinistriesParameter.getValue() != null && !rolesAllowedForAccessingOtherMinistriesParameter.getValue().isEmpty()) {
											List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(member, selectedSession, locale);
											for(MemberRole memberRole: memberRoles) {
												for(String allowedRole: rolesAllowedForAccessingOtherMinistriesParameter.getValue().split(",")) {
													if(memberRole.getType().trim().equals(allowedRole)) {
														isAllowedToAccessOtherMinistries = true;
														break;
													}
												}
												if(isAllowedToAccessOtherMinistries == true) {
													break;
												}
											}
										} else {
											logger.error("custom parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION' is not set properly");
											model.addAttribute("errorcode", "memberroles_submissionforanyministry_in_government_resolution_notset");
										}
									} else {
										logger.error("custom parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION' is not set");
										model.addAttribute("errorcode", "memberroles_submissionforanyministry_in_government_resolution_notset");
									}
									List<Ministry> assignedMinistries=new ArrayList<Ministry>();
									if(isAllowedToAccessOtherMinistries == true) {
										List<Ministry> memberMinistries = new ArrayList<Ministry>();
										List<Ministry> otherMinistries = new ArrayList<Ministry>();
										for(MemberMinister i:memberMinisters){
											memberMinistries.add(i.getMinistry());						
										}
										//setting first member ministry as selected ministry by default
										if(!memberMinistries.isEmpty()) {
											domain.setMinistry(memberMinistries.get(0));
										}									
										//adding ministries of minister adding this resolution
										assignedMinistries.addAll(memberMinistries);
										//also adding ministries that do not belong to minister adding this resolution						
										otherMinistries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale); //Ministry.findAssignedMinistries(locale);
										otherMinistries.removeAll(memberMinistries);					
										assignedMinistries.addAll(otherMinistries);
									} else {
										for(MemberMinister i:memberMinisters){
											assignedMinistries.add(i.getMinistry());						
										}
										//setting first member ministry as selected ministry by default
										if(!assignedMinistries.isEmpty()) {
											domain.setMinistry(assignedMinistries.get(0));
										}									
									}	
									model.addAttribute("ministries",assignedMinistries);
							}else{
								List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);	
								model.addAttribute("ministries",ministries);
							}
								
								Ministry ministry=domain.getMinistry();
								if(ministry!=null){
									model.addAttribute("ministrySelected",ministry.getId());
									List<SubDepartment> assignedSubDepartments = 
											MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
									model.addAttribute("subDepartments", assignedSubDepartments);
									if(!assignedSubDepartments.isEmpty()) {
										SubDepartment subDepartment=assignedSubDepartments.get(0);
										domain.setSubDepartment(subDepartment);			
										if(subDepartment!=null){
											model.addAttribute("subDepartmentSelected",subDepartment.getId());
										}
									}									
								}
							}
						}
					} catch (ParseException e) {
						logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
						model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
					} 
				}else{
					logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
					model.addAttribute("errorcode", "rotationorderpubdate_notset");
				}				
					
					
				
						
			/**** discussion date related things in GR ****/
			if(resolutionType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				/**** expected working discussion date in GR ****/
				Date currentDate = new Date();
//				if(DateUtil.compareDatePartOnly(currentDate, selectedSession.getEndDate())>0) {
//					logger.error("session has been expired.");
//					model.addAttribute("errorcode", "session_expired");
//					return;
//				}
				String daysForDiscussionDateStr = selectedSession.getParameter("resolutions_government_daysForDiscussionDateToBeDecided");
				if(daysForDiscussionDateStr!=null){
					Date expectedDiscussionDate = Holiday.getNextWorkingDateFrom(currentDate, Integer.parseInt(daysForDiscussionDateStr), locale);
					if(DateUtil.compareDatePartOnly(expectedDiscussionDate, selectedSession.getStartDate())<0) {
						expectedDiscussionDate = selectedSession.getStartDate();
					} else if(DateUtil.compareDatePartOnly(expectedDiscussionDate, selectedSession.getEndDate())>0) {
						expectedDiscussionDate = selectedSession.getEndDate();
					}
					model.addAttribute("expectedDiscussionDate", FormaterUtil.formatDateToString(expectedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, locale));
				} else {
					logger.error("session parameter 'resolutions_government_daysForDiscussionDateToBeDecided' is not set.");
					model.addAttribute("errorcode", "resolutions_government_daysForDiscussionDateToBeDecided_notset");
				}
				
				/**** rules to be applied for discussion date in GR ****/
				List<Rule> rulesForDiscussionDate = Rule.findAllByFieldName(Rule.class, "houseType", houseType, "number", ApplicationConstants.ASC, locale);
				List<MasterVO> rulesForDiscussionDateVO = new ArrayList<MasterVO>();
				if(rulesForDiscussionDate != null) {
					for(Rule i : rulesForDiscussionDate) {
						MasterVO ruleForDiscussionDateVO = new MasterVO();
						ruleForDiscussionDateVO.setId(i.getId());					
						ruleForDiscussionDateVO.setValue(i.getNumber());
						rulesForDiscussionDateVO.add(ruleForDiscussionDateVO);
					}
				}			
				model.addAttribute("rulesForDiscussionDate", rulesForDiscussionDateVO);
				Rule ruleForDiscussionDateSelected = domain.getRuleForDiscussionDate();
				if(ruleForDiscussionDateSelected!=null) {
					model.addAttribute("ruleForDiscussionDateSelected", ruleForDiscussionDateSelected.getId());
				}
				
				/**** remarks to be put up in case of early discussion date ****/
				CustomParameter remarksCitation = CustomParameter.findByName(CustomParameter.class, "MEMBER_REMARKS_FOR_EARLY_DISCUSSION_DATE_IN_GOVERNMENT_RESOLUTION", "");
				if(remarksCitation != null) {
					model.addAttribute("remarksCitation", remarksCitation.getValue());
				} else {				
					logger.error("custom parameter 'MEMBER_REMARKS_FOR_EARLY_DISCUSSION_DATE_IN_GOVERNMENT_RESOLUTION' is not set properly");
					model.addAttribute("errorcode", "member_remarks_for_early_discussion_date_in_government_resolution_notset");
				}
			}
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "** There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
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
		/**** for Member and Clerk edit page is displayed ****/
		/**** for assistant assistant page ****/
		/**** for other ris usergroupTypes editreadonly page ****/
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(i.getType().startsWith("MEMBER_")||i.getType().contains("TYPIST")){
				return newUrlPattern;
			}else if(i.getType().contains("ASSISTANT")||i.getType().contains("SECTION_OFFICER")||i.getType().contains("CLERK")){
				return newUrlPattern.replace("edit","assistant");
			}else if(i.getType().startsWith("ROIS_")){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}	
		/**** for others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return "resolution/error";
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {
		try{
			/**** In case of bulk edit we can update only few parameters ****/
			model.addAttribute("bulkedit",request.getParameter("bulkedit"));
			String locale=domain.getLocale();
	
			/**** House Type ****/
			HouseType houseType=domain.getHouseType();
			model.addAttribute("formattedHouseType",houseType.getName());
			model.addAttribute("houseType",houseType.getId());
			model.addAttribute("houseTypeType",houseType.getType());		
	
			/**** Session ****/
			Session selectedSession=domain.getSession();
			model.addAttribute("session",selectedSession.getId());
	
			/**** Session Year ****/
			Integer sessionYear=0;
			sessionYear=selectedSession.getYear();
			model.addAttribute("formattedSessionYear",FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
			model.addAttribute("sessionYear",sessionYear);
	
			/**** Session Type ****/
			SessionType  sessionType=selectedSession.getType();
			model.addAttribute("formattedSessionType",sessionType.getSessionType());
			model.addAttribute("sessionType",sessionType.getId());        
	
			/**** Device Type ****/
			DeviceType resolutionType=domain.getType();
			model.addAttribute("formattedDeviceType",resolutionType.getName());			
			model.addAttribute("resolutionType", resolutionType.getId());
			model.addAttribute("deviceType",resolutionType.getId());
			model.addAttribute("selectedDeviceType",resolutionType.getType());
	
			/**** Member ****/
			String memberName=null;
			Member member=domain.getMember();
			if(member!=null){
				model.addAttribute("member",member.getId());
				memberName=member.getFullname();
				model.addAttribute("formattedMember",memberName);
				Integer count = Resolution.findResolutionCount(member, selectedSession, resolutionType,locale);
				model.addAttribute("resolutionCount", count);
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
			
			
			/**** Ministries ****/
			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
			if(strRotationOrderPubDate!=null){
				try {
					rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
					model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
					Date currentDate=new Date();
					if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
						if(resolutionType.getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){			
							
							List<Ministry> ministries=Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
							model.addAttribute("ministries",ministries);
							Ministry ministry=domain.getMinistry();
							if(ministry!=null){
								model.addAttribute("ministrySelected",ministry.getId());						
								/**** Sub Departments ****/
								List<SubDepartment> subDepartments=MemberMinister.
										findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
								model.addAttribute("subDepartments",subDepartments);
								SubDepartment subDepartment=domain.getSubDepartment();
								if(subDepartment!=null){
									model.addAttribute("subDepartmentSelected",subDepartment.getId());
								}
							}
						} 
						else if(resolutionType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
							List<MemberMinister> memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
							/**** To check whether to populate other ministries also for this minister ****/
							Boolean isAllowedToAccessOtherMinistries = false;					
							CustomParameter rolesAllowedForAccessingOtherMinistriesParameter = CustomParameter.findByName(CustomParameter.class, "MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION", "");
							if(rolesAllowedForAccessingOtherMinistriesParameter != null) {
								if(rolesAllowedForAccessingOtherMinistriesParameter.getValue() != null && !rolesAllowedForAccessingOtherMinistriesParameter.getValue().isEmpty()) {
									List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(member, selectedSession, locale);
									for(MemberRole memberRole: memberRoles) {
										for(String allowedRole: rolesAllowedForAccessingOtherMinistriesParameter.getValue().split(",")) {
											if(memberRole.getType().trim().equals(allowedRole)) {
												isAllowedToAccessOtherMinistries = true;
												break;
											}
										}
										if(isAllowedToAccessOtherMinistries == true) {
											break;
										}
									}
								} else {
									logger.error("custom parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION' is not set properly");
									model.addAttribute("errorcode", "memberroles_submissionforanyministry_in_government_resolution_notset");
								}
							} else {
								logger.error("custom parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_RESOLUTION' is not set");
								model.addAttribute("errorcode", "memberroles_submissionforanyministry_in_government_resolution_notset");
							}
							List<Ministry> assignedMinistries=new ArrayList<Ministry>();
							if(isAllowedToAccessOtherMinistries == true) {
								List<Ministry> memberMinistries = new ArrayList<Ministry>();
								List<Ministry> otherMinistries = new ArrayList<Ministry>();
								for(MemberMinister i:memberMinisters){
									memberMinistries.add(i.getMinistry());						
								}				
								//adding ministries of minister adding this resolution
								assignedMinistries.addAll(memberMinistries);
								//also adding ministries that do not belong to minister adding this resolution
								otherMinistries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale); //Ministry.findAssignedMinistries(locale);
								otherMinistries.removeAll(memberMinistries);					
								assignedMinistries.addAll(otherMinistries);
							} else {
								for(MemberMinister i:memberMinisters){
									assignedMinistries.add(i.getMinistry());						
								}				
							}
							model.addAttribute("ministries",assignedMinistries);
							Ministry ministry=domain.getMinistry();
							if(ministry!=null){
								model.addAttribute("ministrySelected",ministry.getId());							
								List<SubDepartment> assignedSubDepartments = MemberMinister.
										findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
								model.addAttribute("subDepartments", assignedSubDepartments);
								SubDepartment subDepartment=domain.getSubDepartment();
								if(subDepartment!=null){
									model.addAttribute("subDepartmentSelected",subDepartment.getId());
								}							
							}
						}
					}
				} catch (ParseException e) {
					logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
					model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
				} 
			}else{
				logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
				model.addAttribute("errorcode", "rotationorderpubdate_notset");
			}
													
					
			 
			/**** Submission Date and Creation date****/ 
			String houseTypeForStatus = null;
			if(resolutionType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
				houseTypeForStatus=this.getCurrentUser().getHouseType();
			}else if(resolutionType.getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				houseTypeForStatus=domain.getHouseType().getType();
			}
			model.addAttribute("houseTypeForStatus", houseTypeForStatus);
			CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
			if(dateTimeFormat!=null){            
				if(domain.getSubmissionDate()!=null){
					model.addAttribute("submissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getSubmissionDate()));
					model.addAttribute("formattedSubmissionDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getSubmissionDate()));
				}
				if(domain.getCreationDate()!=null){
					model.addAttribute("creationDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getCreationDate()));
				}
				if(resolutionType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
					
					
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						Date workflowStartedOnDateUpperHouse=domain.getWorkflowStartedOnUpperHouse();
						Date taskReceivedOnUpperHouse=domain.getTaskReceivedOnUpperHouse();
						if(domain.getWorkflowStartedOnLowerHouse()!=null){
							model.addAttribute("workflowStartedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnLowerHouse()));
						}
						if(domain.getTaskReceivedOnLowerHouse()!=null){
							model.addAttribute("taskReceivedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnLowerHouse()));
						}
						if(workflowStartedOnDateUpperHouse != null) {
							model.addAttribute("workflowStartedOnDateUpperHouse",workflowStartedOnDateUpperHouse);
						} else {
							model.addAttribute("workflowStartedOnDateUpperHouse", null);
						}
						if(taskReceivedOnUpperHouse != null) {
							model.addAttribute("taskReceivedOnUpperHouse", taskReceivedOnUpperHouse);
						} else {
							model.addAttribute("taskReceivedOnUpperHouse", null);
						}
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						Date workflowStartedOnDateLowerHouse=domain.getWorkflowStartedOnLowerHouse();
						Date taskReceivedOnLowerHouse=domain.getTaskReceivedOnLowerHouse();
						if(domain.getWorkflowStartedOnUpperHouse()!=null){
							model.addAttribute("workflowStartedOnDateUpperHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnUpperHouse()));
						}
						if(domain.getTaskReceivedOnUpperHouse()!=null){
							model.addAttribute("taskReceivedOnDateUpperHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnUpperHouse()));
						}
						if(workflowStartedOnDateLowerHouse != null) {
							model.addAttribute("workflowStartedOnDateLowerHouse", workflowStartedOnDateLowerHouse);
						} else {
							model.addAttribute("workflowStartedOnDateLowerHouse", null);
						}
						if(taskReceivedOnLowerHouse != null) {
							model.addAttribute("taskReceivedOnLowerHouse",taskReceivedOnLowerHouse);
						} else {
							model.addAttribute("taskReceivedOnLowerHouse", null);
						}
					}			
				} else {
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						if(domain.getWorkflowStartedOnLowerHouse()!=null){
							model.addAttribute("workflowStartedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnLowerHouse()));
						}
						if(domain.getTaskReceivedOnLowerHouse()!=null){
							model.addAttribute("taskReceivedOnDateLowerHouse",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnLowerHouse()));
						}
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						if(domain.getWorkflowStartedOnUpperHouse()!=null){
							model.addAttribute("workflowStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOnUpperHouse()));
						}
						if(domain.getTaskReceivedOnUpperHouse()!=null){
							model.addAttribute("taskReceivedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnUpperHouse()));
						}
					}
				}
							
			}
			/**** Number ****/
			if(domain.getNumber()!=null){
				model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
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
			
			/**** Bulk Edit ****/
			String bulkedit=request.getParameter("bulkedit");
			if(bulkedit!=null){
				model.addAttribute("bulkedit",bulkedit);
			}else{
				bulkedit=(String) request.getSession().getAttribute("bulkedit");
				if(bulkedit!=null&&!bulkedit.isEmpty()){
					model.addAttribute("bulkedit",bulkedit);
					request.getSession().removeAttribute("bulkedit");
				}
			}
			//added by sandeep singh(Jan 27 2013)
			/**** usergroup and usergroupType ****/
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
				request.getSession().removeAttribute("userGroup");
			}
			/**** Created By ****/
			model.addAttribute("createdBy",domain.getCreatedBy());
			/**** Status,Internal Status and recommendation Status ****/
			Status status=null;
			Status internalStatus=null;
			Status recommendationStatus=null;
			if(resolutionType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
				
				model.addAttribute("houseTypeForStatus", houseTypeForStatus);
				if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
					status=domain.getStatusLowerHouse();
					internalStatus=domain.getInternalStatusLowerHouse();
					recommendationStatus=domain.getRecommendationStatusLowerHouse();
					Status statusUpperHouse = domain.getStatusUpperHouse();
					if(statusUpperHouse != null) {
						model.addAttribute("statusUpperHouse", statusUpperHouse.getId());
					} else {
						model.addAttribute("statusUpperHouse", null);
					}
					Status internalStatusUpperHouse = domain.getInternalStatusUpperHouse();
					if(internalStatusUpperHouse != null) {
						model.addAttribute("internalStatusUpperHouse", internalStatusUpperHouse.getId());
					} else {
						model.addAttribute("internalStatusUpperHouse", null);
					}
					Status recommendationStatusUpperHouse = domain.getRecommendationStatusUpperHouse();
					if(recommendationStatusUpperHouse != null) {
						model.addAttribute("recommendationStatusUpperHouse", recommendationStatusUpperHouse.getId());
					} else {
						model.addAttribute("recommendationStatusUpperHouse", null);
					}
				}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
					status=domain.getStatusUpperHouse();
					internalStatus=domain.getInternalStatusUpperHouse();
					recommendationStatus=domain.getRecommendationStatusUpperHouse();
					Status statusLowerHouse = domain.getStatusLowerHouse();
					if(statusLowerHouse != null) {
						model.addAttribute("statusLowerHouse", statusLowerHouse.getId());
					} else {
						model.addAttribute("statusLowerHouse", null);
					}
					Status internalStatusLowerHouse = domain.getInternalStatusLowerHouse();
					if(internalStatusLowerHouse != null) {
						model.addAttribute("internalStatusLowerHouse", internalStatusLowerHouse.getId());
					} else {
						model.addAttribute("internalStatusLowerHouse", null);
					}
					Status recommendationStatusLowerHouse = domain.getRecommendationStatusLowerHouse();
					if(recommendationStatusLowerHouse != null) {
						model.addAttribute("recommendationStatusLowerHouse", recommendationStatusLowerHouse.getId());
					} else {
						model.addAttribute("recommendationStatusLowerHouse", null);
					}
				}			
			} else {
				if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					status=domain.getStatusLowerHouse();
					internalStatus=domain.getInternalStatusLowerHouse();
					recommendationStatus=domain.getRecommendationStatusLowerHouse();
				}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
					status=domain.getStatusUpperHouse();
					internalStatus=domain.getInternalStatusUpperHouse();
					recommendationStatus=domain.getRecommendationStatusUpperHouse();
				}
			}		
			if(status!=null){
				model.addAttribute("status",status.getId());
				model.addAttribute("memberStatusType",status.getType());
			}
			if(internalStatus!=null){
				model.addAttribute("internalStatus",internalStatus.getId());
				model.addAttribute("internalStatusType", internalStatus.getType());
				model.addAttribute("formattedInternalStatus", internalStatus.getName());
				/***********EndFlag,Level and Workflowstarted**********************/
				if(usergroupType!=null&&!(usergroupType.isEmpty())
						&&(usergroupType.equals("assistant")||usergroupType.equals("section_officer"))
						&& !(recommendationStatus.getType().equals(ApplicationConstants.RESOLUTION_PROCESSED_UNDERCONSIDERATION))){
					populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,resolutionType.getType());
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						if(domain.getWorkflowStartedLowerHouse()==null){
							domain.setWorkflowStartedLowerHouse("NO");
						}else if(domain.getWorkflowStartedLowerHouse().isEmpty()){
							domain.setWorkflowStartedLowerHouse("NO");
						}
						if(domain.getEndFlagLowerHouse()==null){
							domain.setEndFlagLowerHouse("continue");
						}else if(domain.getEndFlagLowerHouse().isEmpty()){
							domain.setEndFlagLowerHouse("continue");
						}
						if(domain.getLevelLowerHouse()==null){
							domain.setLevelLowerHouse("1");
						}else if(domain.getLevelLowerHouse().isEmpty()){
							domain.setLevelLowerHouse("1");
						}
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						if(domain.getWorkflowStartedUpperHouse()==null){
							domain.setWorkflowStartedUpperHouse("NO");
						}else if(domain.getWorkflowStartedUpperHouse().isEmpty()){
							domain.setWorkflowStartedUpperHouse("NO");
						}
						if(domain.getEndFlagUpperHouse()==null){
							domain.setEndFlagUpperHouse("continue");
						}else if(domain.getEndFlagUpperHouse().isEmpty()){
							domain.setEndFlagUpperHouse("continue");
						}
						if(domain.getLevelUpperHouse()==null){
							domain.setLevelUpperHouse("1");
						}else if(domain.getLevelUpperHouse().isEmpty()){
							domain.setLevelUpperHouse("1");
						}
					}
				}else if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("clerk")){
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						if(domain.getWorkflowStartedLowerHouse()==null){
							domain.setWorkflowStartedLowerHouse("NO");
						}else if(domain.getWorkflowStartedLowerHouse().isEmpty()){
							domain.setWorkflowStartedLowerHouse("NO");
						}
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						if(domain.getWorkflowStartedUpperHouse()==null){
							domain.setWorkflowStartedUpperHouse("NO");
						}else if(domain.getWorkflowStartedUpperHouse().isEmpty()){
							domain.setWorkflowStartedUpperHouse("NO");
						}
					}
				}
			}
			if(domain.getVotingDetail()!=null){
				model.addAttribute("votingDetailId", domain.getVotingDetail().getId());
			}
			if(recommendationStatus!=null){
				model.addAttribute("recommendationStatus",recommendationStatus.getId());
				model.addAttribute("recommendationStatusType",recommendationStatus.getType());
				model.addAttribute("formattedRecommendationStatus", recommendationStatus.getName());
				model.addAttribute("votingFor", ApplicationConstants.VOTING_FOR_PASSING_OF_RESOLUTION);
				if(usergroupType!=null&&!(usergroupType.isEmpty())
						&&(usergroupType.equals("assistant")||usergroupType.equals("section_officer"))
						&&(recommendationStatus.getType().equals(ApplicationConstants.RESOLUTION_PROCESSED_UNDERCONSIDERATION) ||
						   recommendationStatus.getType().equals(ApplicationConstants.RESOLUTION_PROCESSED_SELECTEDANDNOTDISCUSSED))){
					/**** voting for ****/
					
					populateRecommendationStatus(model,recommendationStatus.getType(),usergroupType,locale,resolutionType.getType());
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						if(domain.getWorkflowStartedLowerHouse()==null){
							domain.setWorkflowStartedLowerHouse("NO");
						}else if(domain.getWorkflowStartedLowerHouse().isEmpty()){
							domain.setWorkflowStartedLowerHouse("NO");
						}
						if(domain.getEndFlagLowerHouse()==null){
							domain.setEndFlagLowerHouse("continue");
						}else if(domain.getEndFlagLowerHouse().isEmpty()){
							domain.setEndFlagLowerHouse("continue");
						}
						if(domain.getLevelLowerHouse()==null){
							domain.setLevelLowerHouse("1");
						}else if(domain.getLevelLowerHouse().isEmpty()){
							domain.setLevelLowerHouse("1");
						}
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						if(domain.getWorkflowStartedUpperHouse()==null){
							domain.setWorkflowStartedUpperHouse("NO");
						}else if(domain.getWorkflowStartedUpperHouse().isEmpty()){
							domain.setWorkflowStartedUpperHouse("NO");
						}
						if(domain.getEndFlagUpperHouse()==null){
							domain.setEndFlagUpperHouse("continue");
						}else if(domain.getEndFlagUpperHouse().isEmpty()){
							domain.setEndFlagUpperHouse("continue");
						}
						if(domain.getLevelUpperHouse()==null){
							domain.setLevelUpperHouse("1");
						}else if(domain.getLevelUpperHouse().isEmpty()){
							domain.setLevelUpperHouse("1");
						}
					}
				}
				
			}
			HouseType houseTypeForWorkflow=null;
			/**** if its a non official resolution ****/
			if(resolutionType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				/**** in case of assistant and other approving ROIS actors ****/
				if(usergroupType.equals(ApplicationConstants.CLERK)
						|| usergroupType.equals(ApplicationConstants.ASSISTANT)){
				
					/**** Referenced Resolution are collected in refentities****/
					List<Reference> refentities=new ArrayList<Reference>();
					List<String> refentitiesSessionDevice = new ArrayList<String>();
					
					Long referencedEntityId = Resolution.findReferencedEntity(domain);//domain.getReferencedResolution();
					if(referencedEntityId!=null){
						model.addAttribute("referencedEntityId",referencedEntityId);
						Reference reference=new Reference();
						reference.setId(String.valueOf(referencedEntityId));
						Resolution refResolution = Resolution.findReferencedResolution(domain);
						if(refResolution != null){
							reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(refResolution.getNumber()));
							reference.setNumber(String.valueOf(refResolution.getId()));
							refentities.add(reference);
							Session referencedResolutionSession = refResolution.getSession();
							StringBuffer strReferenceResolution = new StringBuffer("[" + referencedResolutionSession.getType().getSessionType()+", "+FormaterUtil.formatNumberNoGrouping(referencedResolutionSession.getYear(), locale) + "], "
									+ refResolution.getType().getName());
							if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
								strReferenceResolution.append(", " + refResolution.getInternalStatusLowerHouse().getName());
							}else{
								strReferenceResolution.append("," + refResolution.getInternalStatusUpperHouse().getName());
							}
							refentitiesSessionDevice.add(strReferenceResolution.toString());
							
							
						}
						model.addAttribute("isRepeatWorkFlow", "yes");
					}			
					model.addAttribute("referencedResolutions",refentities);
					model.addAttribute("referencedResolutionsSessionAndDevice", refentitiesSessionDevice);
				}	
				/**** house type for workflow created by current assistant ****/
				 houseTypeForWorkflow = domain.getHouseType();
				if(houseTypeForWorkflow != null) {
					model.addAttribute("workflowHouseType", houseTypeForWorkflow.getName());
				} else {
					logger.error("housetype for user set incorrectly.");
				}
				
			}
			
			if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {			
				if(usergroupType.equals(ApplicationConstants.MEMBER)){
					/**** discussion date editing allowed or not  ****/
					model.addAttribute("isDiscussionDateReadOnly", false);
					
					/**** expected working discussion date in GR ****/
					Date currentDate = new Date();
//					if(DateUtil.compareDatePartOnly(currentDate, selectedSession.getEndDate())>0) {
//						logger.error("session has been expired.");
//						model.addAttribute("errorcode", "session_expired");
//					}
					String daysForDiscussionDateStr = selectedSession.getParameter("resolutions_government_daysForDiscussionDateToBeDecided");
					if(daysForDiscussionDateStr != null) {
						Date expectedDiscussionDate = Holiday.getNextWorkingDateFrom(currentDate, Integer.parseInt(daysForDiscussionDateStr), locale);
						if(DateUtil.compareDatePartOnly(expectedDiscussionDate, selectedSession.getStartDate())<0) {
							expectedDiscussionDate = selectedSession.getStartDate();
						} else if(DateUtil.compareDatePartOnly(expectedDiscussionDate, selectedSession.getEndDate())>0) {
							expectedDiscussionDate = selectedSession.getEndDate();
						}
						model.addAttribute("expectedDiscussionDate", FormaterUtil.formatDateToString(expectedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, locale));
					} else {
						logger.error("session parameter 'resolutions_government_daysForDiscussionDateToBeDecided' is not set.");
						model.addAttribute("errorcode", "resolutions_government_daysForDiscussionDateToBeDecided_notset");
					}
					
					/**** rules to be applied for discussion date in GR ****/
					List<Rule> rulesForDiscussionDate = Rule.findAllByFieldName(Rule.class, "houseType", houseType, "number", ApplicationConstants.ASC, locale);
					List<MasterVO> rulesForDiscussionDateVO = new ArrayList<MasterVO>();
					if(rulesForDiscussionDate != null) {
						for(Rule i : rulesForDiscussionDate) {
							MasterVO ruleForDiscussionDateVO = new MasterVO();
							ruleForDiscussionDateVO.setId(i.getId());					
							ruleForDiscussionDateVO.setValue(i.getNumber());
							rulesForDiscussionDateVO.add(ruleForDiscussionDateVO);
						}
					}			
					model.addAttribute("rulesForDiscussionDate", rulesForDiscussionDateVO);
					Rule ruleForDiscussionDateSelected = domain.getRuleForDiscussionDate();
					if(ruleForDiscussionDateSelected!=null) {
						model.addAttribute("ruleForDiscussionDateSelected", ruleForDiscussionDateSelected.getId());
					}				
					
					/**** remarks to be put up in case of early discussion date ****/
					CustomParameter remarksCitation = CustomParameter.findByName(CustomParameter.class, "MEMBER_REMARKS_FOR_EARLY_DISCUSSION_DATE_IN_GOVERNMENT_RESOLUTION", "");
					if(remarksCitation != null) {
						model.addAttribute("remarksCitation", remarksCitation.getValue());
					} else {				
						logger.error("custom parameter 'MEMBER_REMARKS_FOR_EARLY_DISCUSSION_DATE_IN_GOVERNMENT_RESOLUTION' is not set properly");
						model.addAttribute("errorcode", "member_remarks_for_early_discussion_date_in_government_resolution_notset");
					}
				} else {
					/**** discussion date editing allowed or not  ****/
					model.addAttribute("isDiscussionDateReadOnly", true);
				}
				
				/**** remarks in case of government resolution as there are two assistants for each housetype ****/
				String currentRemarksOfUser = null;
				if(!usergroupType.equals(ApplicationConstants.MEMBER)){
					ResolutionDraft latestDraft = Resolution.getLatestResolutionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
						
					if(latestDraft != null) {
						currentRemarksOfUser = latestDraft.getRemarks();
					}
					domain.setRemarks(currentRemarksOfUser);
				}
				/**** level of current usergroup ****/
				if(usergroupType.equals(ApplicationConstants.ASSISTANT)){				
					model.addAttribute("level",1);				
				}
				/**** house type for workflow created by current assistant ****/
				 houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", this.getCurrentUser().getHouseType(), locale);
				if(houseTypeForWorkflow != null) {
					model.addAttribute("workflowHouseType", houseTypeForWorkflow.getName());
				} else {
					logger.error("housetype for user set incorrectly.");
				}			
			}
			
			/**** Populating Put up options and Actors ****/
			String internalStatusType=null;
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					internalStatusType=domain.getInternalStatusLowerHouse().getType();
				}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
					internalStatusType=domain.getInternalStatusUpperHouse().getType();
				}
			}else if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
				String strHouseType=this.getCurrentUser().getHouseType();
				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
					internalStatusType=domain.getInternalStatusLowerHouse().getType();
				}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
					internalStatusType=domain.getInternalStatusUpperHouse().getType();
				}
			}
			
			if(domain.getBallotStatus()!=null){
				model.addAttribute("ballotStatusId", domain.getBallotStatus().getId());
			}
			
			if(domain.getDiscussionStatus()!=null){
				model.addAttribute("discussionStatusId",domain.getDiscussionStatus().getId());
			}
			
			if(usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_ADMISSION)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_GOVT)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER_AND_DEPARTMENT)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_REJECTION)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION)
							||internalStatusType.equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION))
					){
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(usergroup));
				List<Reference> actors=null;
				if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
					actors=WorkflowConfig.findResolutionActorsVO(domain, internalStatus, userGroup, 1,domain.getHouseType().getType(), locale);
				}else if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
					actors=WorkflowConfig.findResolutionActorsVO(domain, internalStatus, userGroup, 1,houseTypeForWorkflow.getName(), locale);
				}
				
				model.addAttribute("internalStatusSelected",internalStatus.getId());
				model.addAttribute("actors",actors);
				if(actors!=null&&!actors.isEmpty()){
					String nextActor=actors.get(0).getId();
					String[] actorArr=nextActor.split("#");
					if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
						domain.setLevelLowerHouse(actorArr[2]);
						domain.setLocalizedActorNameLowerHouse(actorArr[3]+"("+actorArr[4]+")");
					}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
						domain.setLevelUpperHouse(actorArr[2]);
						domain.setLocalizedActorNameUpperHouse(actorArr[3]+"("+actorArr[4]+")");
					}
					
				}
			}
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "** There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();					
		}
	}

	private void populateInternalStatus(final ModelMap model, final String type, final String userGroupType, final String locale, final String resolutionType) {
		try {
			List<Status> internalStatuses=new ArrayList<Status>();
			/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
			CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses!=null){
				internalStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus!=null){
					internalStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus!=null){
						internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter!=null){
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","resolution_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode","resolution_putup_options_final_notset");
					}		
				}
			}	
			/**** Internal Status****/
			model.addAttribute("internalStatuses",internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
		}
	}
	
	private void populateRecommendationStatus(final ModelMap model, final String type, final String userGroupType, final String locale, final String resolutionType) {
		try {
			List<Status> recommendationStatuses=new ArrayList<Status>();
			/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
			CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+resolutionType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			if(specificDeviceStatusUserGroupStatuses!=null){
				recommendationStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			}else if(specificDeviceUserGroupStatuses!=null){
				recommendationStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			}else if(specificStatuses!=null){
				recommendationStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					||userGroupType.equals(ApplicationConstants.SPEAKER)){
				CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus!=null){
					recommendationStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus!=null){
						recommendationStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter!=null){
							recommendationStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode","resolution_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					recommendationStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						recommendationStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode","resolution_putup_options_final_notset");
					}		
				}
			}	
			/**** Internal Status****/
			model.addAttribute("recommendationStatuses",recommendationStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void customValidateCreate(final Resolution domain, final BindingResult result,
			final HttpServletRequest request) {
		request.getParameter("member");
		
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Approval ****/
					if(domain.getHouseType()==null){
						result.rejectValue("houseType","HousetypeEmpty");
					}
					if(domain.getType()==null){
						result.rejectValue("type","DeviceTypeEmpty");
					}
					if(domain.getSession()==null){
						result.rejectValue("session","SessionEmpty");
					}
					if(domain.getMember()==null){
						result.rejectValue("member","MemberEmpty");
					}
					/*if(domain.getSubject().isEmpty()){
						result.rejectValue("subject","SubjectEmpty");
					}*/
					if(domain.getNoticeContent().isEmpty()){
						result.rejectValue("noticeContent","NoticeContentEmpty");
					}
				}else
					if(operation.equals("submit")){
						/**** Submission ****/
						if(domain.getHouseType()==null){
							result.rejectValue("houseType","HousetypeEmpty");
						}
						if(domain.getType()==null){
							result.rejectValue("type","DeviceTypeEmpty");
						}
						if(domain.getSession()==null){
							result.rejectValue("session","SessionEmpty");
						}
						if(domain.getMember()==null){
							result.rejectValue("member","MemberEmpty");
						}
						/*if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}*/
						if(domain.getNoticeContent().isEmpty()){
							result.rejectValue("noticeContent","NoticeContentEmpty");
						}
						
						//submission date limit validations
						if(domain.getSession()!=null && domain.getType()!=null) {
							CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
							if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
								String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
								if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
									String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
									
									for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
										if(dt.trim().equals(domain.getType().getType().trim())) {
											if(!Resolution.isAllowedForSubmission(domain, new Date())) {
												String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_submissionStartDate");
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Resolution cannot be submitted before " + submissionStartLimitDateStr);
											}
											break;
										}
									}
								}
							}
						}
					}
				}
		}/**** Drafts ****/
		else{
			if(domain.getHouseType()==null){
				result.rejectValue("houseType","HousetypeEmpty");
			}
			if(domain.getType()==null){
				result.rejectValue("type","DeviceTypeEmpty");
			}
			if(domain.getSession()==null){
				result.rejectValue("session","SessionEmpty");
			}
			if(domain.getMember()==null){
				result.rejectValue("member","MemberEmpty");
			}
			/*if(domain.getSubject().isEmpty()){
				result.rejectValue("subject","SubjectEmpty");
			}*/
			if(domain.getNoticeContent().isEmpty()){
				result.rejectValue("noticeContent","NoticeContentEmpty");
			}
		}
		/**** common validations  for all operations ****/
		//validation of discussion date in Government Resolution
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {			
			Date requestedDiscussionDate = domain.getDiscussionDate();
			String expectedDiscussionDateStr = request.getParameter("expectedDiscussionDate");			
			Date expectedDiscussionDate = FormaterUtil.formatStringToDate(expectedDiscussionDateStr, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale());
			if(DateUtil.compareDatePartOnly(requestedDiscussionDate, expectedDiscussionDate)<0) {
				if(DateUtil.compareDatePartOnly(requestedDiscussionDate, domain.getSession().getStartDate())<0 ) {
					result.rejectValue("discussionDate","DiscussionDateInvalid");
				}
				if(domain.getRuleForDiscussionDate() == null) {
					result.rejectValue("ruleForDiscussionDate","RuleForDiscussionDateEmpty");
				}
			} else if(DateUtil.compareDatePartOnly(requestedDiscussionDate, expectedDiscussionDate)>0) {
				result.rejectValue("discussionDate","DiscussionDateInvalid");
			}
		}
		
		if(domain.getType().getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {	
			if(domain.getNumber() != null){
				try {
					Integer count= Resolution.getMemberResolutionCountByNumber(domain.getMember().getId(),domain.getSession().getId(),domain.getLocale());
					if(count>=5){
						result.rejectValue("number","NumberExceeded","Extra Question Should Not Have Number");
					}
					
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		Boolean flag=Resolution.isExist(domain.getNumber(),domain.getType(),domain.getSession(),domain.getLocale());
		if(flag){
			result.rejectValue("number", "NonUnique","Duplicate Parameter");
		}
	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateUpdateIfErrors(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {
		/**** updating submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		String houseTypeForStatus=this.getCurrentUser().getHouseType();
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
				if(houseTypeForStatus.equals(ApplicationConstants.LOWER_HOUSE)){
					if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
						domain.setWorkflowStartedOnLowerHouse(format.parse(strWorkflowStartedOnDate));
					}
					if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
						domain.setTaskReceivedOnLowerHouse(format.parse(strTaskReceivedOnDate));
					}
				}else if(houseTypeForStatus.equals(ApplicationConstants.UPPER_HOUSE)){
					if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
						domain.setWorkflowStartedOnUpperHouse(format.parse(strWorkflowStartedOnDate));
					}
					if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
						domain.setTaskReceivedOnUpperHouse(format.parse(strTaskReceivedOnDate));
					}
				}
				
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}	
		super.populateUpdateIfErrors(model, domain, request);
	}

	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String strUserGroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getMember()!=null 
				&&(!domain.getNoticeContent().isEmpty())){			

			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&(strUserGroupType.equals("member")||strUserGroupType.equals("typist"))){
							/****  submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/****  discussion date is set ****/
							if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
								if(domain.getDiscussionDate()==null){
									String expectedDiscussionDateStr = request.getParameter("expectedDiscussionDate");			
									Date expectedDiscussionDate = FormaterUtil.formatStringToDate(expectedDiscussionDateStr, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale());							
									domain.setDiscussionDate(expectedDiscussionDate);
								}
							}
							/**** Status,Internal Status and recommendation Status is set ****/
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_SUBMIT, domain.getLocale());
							if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
								domain.setStatusLowerHouse(newstatus);
								domain.setInternalStatusLowerHouse(newstatus);
								domain.setRecommendationStatusLowerHouse(newstatus);
								domain.setStatusUpperHouse(newstatus);
								domain.setInternalStatusUpperHouse(newstatus);
								domain.setRecommendationStatusUpperHouse(newstatus);
							}else if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
								if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									domain.setStatusLowerHouse(newstatus);
									domain.setInternalStatusLowerHouse(newstatus);
									domain.setRecommendationStatusLowerHouse(newstatus);
								}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									domain.setStatusUpperHouse(newstatus);
									domain.setInternalStatusUpperHouse(newstatus);
									domain.setRecommendationStatusUpperHouse(newstatus);
								}
							}
						}
					}else{
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_COMPLETE, domain.getLocale());
						if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&(strUserGroupType.equals("member")||strUserGroupType.equals("typist"))){
							if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
								domain.setStatusLowerHouse(status);
								domain.setInternalStatusLowerHouse(status);
								domain.setRecommendationStatusLowerHouse(status);
								domain.setStatusUpperHouse(status);
								domain.setInternalStatusUpperHouse(status);
								domain.setRecommendationStatusUpperHouse(status);
							}else if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
								if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									domain.setStatusLowerHouse(status);
									domain.setInternalStatusLowerHouse(status);
									domain.setRecommendationStatusLowerHouse(status);
								}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									domain.setStatusUpperHouse(status);
									domain.setInternalStatusUpperHouse(status);
									domain.setRecommendationStatusUpperHouse(status);
								}
							}	
						}
					}
				}else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_COMPLETE, domain.getLocale());
					if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&strUserGroupType.equals("member")||strUserGroupType.equals("typist")){
						if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
							domain.setStatusLowerHouse(status);
							domain.setInternalStatusLowerHouse(status);
							domain.setRecommendationStatusLowerHouse(status);
							domain.setStatusUpperHouse(status);
							domain.setInternalStatusUpperHouse(status);
							domain.setRecommendationStatusUpperHouse(status);
						} else {
							if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								domain.setStatusLowerHouse(status);
								domain.setInternalStatusLowerHouse(status);
								domain.setRecommendationStatusLowerHouse(status);
							}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								domain.setStatusUpperHouse(status);
								domain.setInternalStatusUpperHouse(status);
								domain.setRecommendationStatusUpperHouse(status);
							}
						}
					}
				}
			}else{
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_COMPLETE, domain.getLocale());
				if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&strUserGroupType.equals("member")||strUserGroupType.equals("typist")){
					if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
						domain.setStatusLowerHouse(status);
						domain.setInternalStatusLowerHouse(status);
						domain.setRecommendationStatusLowerHouse(status);
						domain.setStatusUpperHouse(status);
						domain.setInternalStatusUpperHouse(status);
						domain.setRecommendationStatusUpperHouse(status);
					} else {
						if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							domain.setStatusLowerHouse(status);
							domain.setInternalStatusLowerHouse(status);
							domain.setRecommendationStatusLowerHouse(status);
						}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							domain.setStatusUpperHouse(status);
							domain.setInternalStatusUpperHouse(status);
							domain.setRecommendationStatusUpperHouse(status);
						}
					}
				}
			}
		}
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_INCOMPLETE, domain.getLocale());
			if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&strUserGroupType.equals("member")||strUserGroupType.equals("typist")){
				if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
					domain.setStatusLowerHouse(status);
					domain.setInternalStatusLowerHouse(status);
					domain.setRecommendationStatusLowerHouse(status);
					domain.setStatusUpperHouse(status);
					domain.setInternalStatusUpperHouse(status);
					domain.setRecommendationStatusUpperHouse(status);
				} else {
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						domain.setStatusLowerHouse(status);
						domain.setInternalStatusLowerHouse(status);
						domain.setRecommendationStatusLowerHouse(status);
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						domain.setStatusUpperHouse(status);
						domain.setInternalStatusUpperHouse(status);
						domain.setRecommendationStatusUpperHouse(status);
					}
				}			
			}
		}
			
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		
//		if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&strUserGroupType.equals("member")){
//			Member member=domain.getMember();
//			User user;
//			try {
//				user = User.findbyNameBirthDate(member.getFirstName(),member.getMiddleName(),member.getLastName(),member.getBirthDate());
//				domain.setCreatedBy(user.getCredential().getUsername());
//			} catch (ELSException e) {
//				model.addAttribute("error", e.getParameter());
//				e.printStackTrace();
//			}
//			
//		}else{
//			domain.setCreatedBy(this.getCurrentUser().getActualUsername());
//		}		
		domain.setCreatedBy(this.getCurrentUser().getActualUsername());
		
		domain.setDataEnteredBy(this.getCurrentUser().getActualUsername());
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}		
	}

	@Override
	protected void populateAfterCreate(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {
		/**** set statuses in draft for government resolution ****/
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			ResolutionDraft latestDraft;
			try {
				latestDraft = Resolution.getLatestResolutionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
				
				String internalStatusType = domain.getInternalStatusLowerHouse().getType();
				if(!(internalStatusType.equals(ApplicationConstants.RESOLUTION_INCOMPLETE)) && !(internalStatusType.equals(ApplicationConstants.RESOLUTION_COMPLETE))) {				
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						latestDraft.setStatus(domain.getStatusLowerHouse());
						latestDraft.setInternalStatus(domain.getInternalStatusLowerHouse());
						latestDraft.setRecommendationStatus(domain.getRecommendationStatusLowerHouse());					
					} else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						latestDraft.setStatus(domain.getStatusUpperHouse());
						latestDraft.setInternalStatus(domain.getInternalStatusUpperHouse());
						latestDraft.setRecommendationStatus(domain.getRecommendationStatusUpperHouse());					
					}
					latestDraft.setDiscussionDate(domain.getDiscussionDate());
					latestDraft.merge();
				}		
				
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}	
		}
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		

	}	
	
	@Override
	protected void customValidateUpdate(final Resolution domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Version mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		/**** Checking for mandatory fields during submit,normal create,approval ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
					/**** Submission ****/
					if(operation.equals("submit")){
						if(domain.getHouseType()==null){
							result.rejectValue("houseType","HousetypeEmpty");
						}
						if(domain.getType()==null){
							result.rejectValue("type","DeviceTypeEmpty");
						}
						if(domain.getSession()==null){
							result.rejectValue("session","SessionEmpty");
						}
						if(domain.getMember()==null){
							result.rejectValue("member","MemberEmpty");
						}
						/*if(domain.getSubject().isEmpty()){
							result.rejectValue("subject","SubjectEmpty");
						}*/
						if(domain.getNoticeContent().isEmpty()){
							result.rejectValue("noticeContent","NoticeContentEmpty");
						}
						
						//submission date limit validations
						if(domain.getSession()!=null && domain.getType()!=null) {
							CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
							if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
								String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
								if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
									String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
									
									for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
										if(dt.trim().equals(domain.getType().getType().trim())) {
											if(!Resolution.isAllowedForSubmission(domain, new Date())) {
												String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_submissionStartDate");
												result.rejectValue("version","SubmissionNotAllowedBeforeConfiguredDate","Resolution cannot be submitted before " + submissionStartLimitDateStr);
											}
											break;
										}
									}
								}
							}
						}
					}
			}
		}/**** Drafts ****/
		else{
			if(domain.getHouseType()==null){
				result.rejectValue("houseType","HousetypeEmpty");
			}
			if(domain.getType()==null){
				result.rejectValue("type","QuestionTypeEmpty");
			}
			if(domain.getSession()==null){
				result.rejectValue("session","SessionEmpty");
			}
			if(domain.getMember()==null){
				result.rejectValue("member","MemberEmpty");
			}
			/*if(domain.getSubject().isEmpty()){
				result.rejectValue("subject","SubjectEmpty");
			}*/
			if(domain.getNoticeContent().isEmpty()){
				result.rejectValue("noticeContent","NoticeContentEmpty");
			}
		}
		/**** common validations for all operations ****/
		//validation of discussion date for member in Government Resolution
		String usergroupType=request.getParameter("usergroupType");
		if(usergroupType.equals(ApplicationConstants.MEMBER)) {
			if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				Date requestedDiscussionDate = domain.getDiscussionDate();				
				String expectedDiscussionDateStr = request.getParameter("expectedDiscussionDate");			
				Date expectedDiscussionDate = FormaterUtil.formatStringToDate(expectedDiscussionDateStr, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale());
				if(DateUtil.compareDatePartOnly(requestedDiscussionDate, expectedDiscussionDate)<0) {
					if(DateUtil.compareDatePartOnly(requestedDiscussionDate, domain.getSession().getStartDate())<0 ) {
						result.rejectValue("discussionDate","DiscussionDateInvalid");
					}
					if(domain.getRuleForDiscussionDate() == null) {
						result.rejectValue("ruleForDiscussionDate","RuleForDiscussionDateEmpty");
					}
				} else if(DateUtil.compareDatePartOnly(requestedDiscussionDate, expectedDiscussionDate)>0) {
					result.rejectValue("discussionDate","DiscussionDateInvalid");
				}			
			}
		}
		
		//Validation for the Extra Resolutions: No Number should be assigned to the Extra Resolutions
//		if(domain.getType().getType().trim().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {	
//			if(domain.getNumber() != null){
//				try {
//					Integer count= Resolution.getMemberResolutionCountByNumber(domain.getMember().getId(),domain.getSession().getId(),domain.getLocale());
//					if(count>=5){
//						result.rejectValue("number","NumberExceeded");
//					}
//					
//				} catch (ELSException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
	}

	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {
		/**** Checking if its submission request or normal update ****/
		String operation=request.getParameter("operation");
		String usergroupType=request.getParameter("usergroupType");
//		String strRefResolutionID = request.getParameter("refResolution");
//		String strRefEntitiesID = request.getParameter("referredEntities");
		
		/**** Resolution status will be complete if all mandatory fields have been filled ****/
		if(domain.getHouseType()!=null&&domain.getType()!=null&&domain.getSession()!=null
				&& domain.getMember()!=null &&(!domain.getNoticeContent().isEmpty())){			
			if(operation!=null){
				if(!operation.isEmpty()){
					
					/**** Submission request ****/
					if(operation.trim().equals("submit")){
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&(usergroupType.equals("member")||usergroupType.equals("typist"))){
							/**** Submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/****  discussion date is set ****/
							if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
								if(domain.getDiscussionDate()==null){
									String expectedDiscussionDateStr = request.getParameter("expectedDiscussionDate");			
									Date expectedDiscussionDate = FormaterUtil.formatStringToDate(expectedDiscussionDateStr, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale());							
									domain.setDiscussionDate(expectedDiscussionDate);
								}
							}
							/**** Status,Internal status and recommendation status is set to complete ****/
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_SUBMIT, domain.getLocale());
							if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
								domain.setStatusLowerHouse(newstatus);
								domain.setInternalStatusLowerHouse(newstatus);
								domain.setRecommendationStatusLowerHouse(newstatus);
								domain.setStatusUpperHouse(newstatus);
								domain.setInternalStatusUpperHouse(newstatus);
								domain.setRecommendationStatusUpperHouse(newstatus);
							} else {
								if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									domain.setStatusLowerHouse(newstatus);
									domain.setInternalStatusLowerHouse(newstatus);
									domain.setRecommendationStatusLowerHouse(newstatus);
								}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									domain.setStatusUpperHouse(newstatus);
									domain.setInternalStatusUpperHouse(newstatus);
									domain.setRecommendationStatusUpperHouse(newstatus);
								}
							}	
						}
					}else if(operation.trim().equals("startworkflow")){
						{
							/* Find if next actors are not active then create a draft for them if draft is 
							 * not existing for that actors.
							 */
							try{
								String strNextuser = "";
								if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									strNextuser=request.getParameter("actorLowerHouse");
								}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									strNextuser=request.getParameter("actorUpperHouse");
								}
								String[] nextuser = null;
								int nextUserLevel = 0;
								if(strNextuser != null && !strNextuser.isEmpty()){
										nextuser = strNextuser.split("#");
										nextUserLevel = Integer.parseInt(nextuser[2]);
								} 
														
								Resolution q = null;
								
								if(domain.getId() != null){
									q = Resolution.findById(Resolution.class, domain.getId());
								}else{
									q = domain.copyResolution();
								}
								
								
								Map<String, String[]> params = new HashMap<String, String[]>();
								params.put("locale", new String[]{domain.getLocale().toString()});
								params.put("houseTypeId", new String[]{domain.getHouseType().getId().toString()});
								//params.put("deviceTypeName", new String[]{domain.getType().getName()});
								params.put("ugType", new String[]{ApplicationConstants.ASSISTANT});
								List data = Query.findReport(domain.getType().getType().toUpperCase()+"_ACTIVE_USER", params);
								String strUsername = null;
								if(data != null && !data.isEmpty()){
									Object[] obj = (Object[])data.get(0);
									strUsername = obj[1].toString();
								}
							
								Credential cr = null;
								if(strUsername != null){
									cr = Credential.findByFieldName(Credential.class, "username", strUsername, null);
								}
								
								if(cr != null){
									UserGroup assistant = UserGroup.findActive(cr, new Date(), domain.getLocale().toString());
									List<Reference> refs = null;
									if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
										refs = WorkflowConfig.
												findResolutionActorsVO(q,domain.getInternalStatusLowerHouse(),
														assistant,1,domain.getHouseType().getName(),q.getLocale());
									} else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
										refs = WorkflowConfig.
												findResolutionActorsVO(q,domain.getInternalStatusUpperHouse(),
														assistant,1,domain.getHouseType().getName(),q.getLocale());
									}									
									
									List<ResolutionDraft> ogDrafts = q.getDrafts();								
								
									for(Reference ref : refs){
										
										String[] user = ref.getId().split("#");
										
										if(!user[1].equals(ApplicationConstants.MEMBER) && !user[1].equals(ApplicationConstants.DEPARTMENT) && !user[1].equals(ApplicationConstants.DEPARTMENT_DESKOFFICER) && !ref.getState().equals(ApplicationConstants.ACTOR_ACTIVE)){
											
											int refLevel = Integer.parseInt(user[2]);
											
											if(refLevel < nextUserLevel){
												boolean foundUsersDraft = false;
												if(ogDrafts != null && !ogDrafts.isEmpty()){
													for(ResolutionDraft qd : ogDrafts){
														if(qd.getEditedAs().equals(user[3]) 
																&& qd.getEditedBy().equals(user[0])){
															foundUsersDraft = true;
															break;
														}
													}
													
													if(!foundUsersDraft){
														ResolutionDraft qdn = Resolution.addDraft(q, user[0], user[3], ref.getRemark());
														ogDrafts.add(qdn);
													}
												}
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								//return "redirect:question/"+domain.getId()+"/edit";
							}
						}
						
					}else{
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_COMPLETE, domain.getLocale());
						if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")||usergroupType.equals("typist")){
							if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
								/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
								if(!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)){
									domain.setStatusLowerHouse(status);
									domain.setInternalStatusLowerHouse(status);
									domain.setRecommendationStatusLowerHouse(status);
								}							
								if(!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)){
									domain.setStatusUpperHouse(status);
									domain.setInternalStatusUpperHouse(status);
									domain.setRecommendationStatusUpperHouse(status);
								}
							} else if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
								/**** if status is not submit then status,internal status and recommendation status is set to complete ****/ 
								if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									if(!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)&&
											!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
											!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)&&
											!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNOTRECEIVEDFROMDEPARTMENT)){
										domain.setStatusLowerHouse(status);
										domain.setInternalStatusLowerHouse(status);
										domain.setRecommendationStatusLowerHouse(status);
									}
								}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									if(!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)&&
											!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
											!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)&&
											!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNOTRECEIVEDFROMDEPARTMENT)){
										domain.setStatusUpperHouse(status);
										domain.setInternalStatusUpperHouse(status);
										domain.setRecommendationStatusUpperHouse(status);
									}
								}
							}						
						}
					}
				}else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_COMPLETE, domain.getLocale());
					if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")||usergroupType.equals("typist")){
						if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
							/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
							if(!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)){
								domain.setStatusLowerHouse(status);
								domain.setInternalStatusLowerHouse(status);
								domain.setRecommendationStatusLowerHouse(status);
							}							
							if(!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)){
								domain.setStatusUpperHouse(status);
								domain.setInternalStatusUpperHouse(status);
								domain.setRecommendationStatusUpperHouse(status);
							}
						}else if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
							/**** if status is not submit then status,internal status and recommendation status is set to complete ****/ 
							if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								if(!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)&&
										!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
										!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)){
									domain.setStatusLowerHouse(status);
									domain.setInternalStatusLowerHouse(status);
									domain.setRecommendationStatusLowerHouse(status);
								}
							}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								if(!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)&&
										!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
										!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)){
									domain.setStatusUpperHouse(status);
									domain.setInternalStatusUpperHouse(status);
									domain.setRecommendationStatusUpperHouse(status);
								}
							}
						}
					}
				}
			}else{
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_COMPLETE, domain.getLocale());
				if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")||usergroupType.equals("typist")){
					if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
						if(!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)){
							domain.setStatusLowerHouse(status);
							domain.setInternalStatusLowerHouse(status);
							domain.setRecommendationStatusLowerHouse(status);
						}							
						if(!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)){
							domain.setStatusUpperHouse(status);
							domain.setInternalStatusUpperHouse(status);
							domain.setRecommendationStatusUpperHouse(status);
						}
					}else if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/ 
						if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							if(!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)&&
									!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
									!domain.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)){
									domain.setStatusLowerHouse(status);
									domain.setInternalStatusLowerHouse(status);
									domain.setRecommendationStatusLowerHouse(status);
							}
						}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							if(!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT) &&
									!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT)&&
									!domain.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER)){
									domain.setStatusUpperHouse(status);
									domain.setInternalStatusUpperHouse(status);
									domain.setRecommendationStatusUpperHouse(status);
							}
						}
					}
				}
			}
		}
		/**** If all mandatory fields have not been set then status,internal status and recommendation status is set to incomplete ****/
		else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_INCOMPLETE, domain.getLocale());
			if(usergroupType!=null&&!(usergroupType.isEmpty())&&usergroupType.equals("member")||usergroupType.equals("typist")){
				if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
					domain.setStatusLowerHouse(status);
					domain.setInternalStatusLowerHouse(status);
					domain.setRecommendationStatusLowerHouse(status);
					domain.setStatusUpperHouse(status);
					domain.setInternalStatusUpperHouse(status);
					domain.setRecommendationStatusUpperHouse(status);
				} else {
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						domain.setStatusLowerHouse(status);
						domain.setInternalStatusLowerHouse(status);
						domain.setRecommendationStatusLowerHouse(status);
				
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						domain.setStatusUpperHouse(status);
						domain.setInternalStatusUpperHouse(status);
						domain.setRecommendationStatusUpperHouse(status);
					}
				}
			}
		}
		/**** Edited On,Edited By and Edited As is set ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		String strUserGroupType=request.getParameter("usergroupType");
		if(strUserGroupType!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
			domain.setEditedAs(userGroupType.getName());
		}
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendstion status to assistant processed ****/
		if(strUserGroupType!=null){
			if(strUserGroupType.equals("assistant") ||strUserGroupType.equals("clerk")){
				Long id = domain.getId();
				Resolution resolution = Resolution.findById(Resolution.class, id);
				String internalStatus=null;
				if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
					if(this.getCurrentUser().getHouseType().equals(ApplicationConstants.LOWER_HOUSE)){
						 internalStatus = resolution.getInternalStatusLowerHouse().getType();
						 if(internalStatus.equals(ApplicationConstants.RESOLUTION_SUBMIT)&&domain.getMinistry()!=null && domain.getSubDepartment()!=null) {
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusLowerHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusLowerHouse(ASSISTANT_PROCESSED);
							}
					}else if(this.getCurrentUser().getHouseType().equals(ApplicationConstants.UPPER_HOUSE)){
						internalStatus = resolution.getInternalStatusUpperHouse().getType();
						if(internalStatus.equals(ApplicationConstants.RESOLUTION_SUBMIT)&&domain.getMinistry()!=null && domain.getSubDepartment()!=null) {
							Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
							domain.setInternalStatusUpperHouse(ASSISTANT_PROCESSED);
							domain.setRecommendationStatusUpperHouse(ASSISTANT_PROCESSED);
						}
					}
				} else {
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						 internalStatus = resolution.getInternalStatusLowerHouse().getType();
						 if(internalStatus.equals(ApplicationConstants.RESOLUTION_SUBMIT)&&domain.getMinistry()!=null && domain.getSubDepartment()!=null) {
							if(resolution.getRecommendationStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION)){
								Status RESOLUTION_RECOMMEND_REPEATADMISSION = Status.findByType(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION, domain.getLocale());
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusLowerHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusLowerHouse(RESOLUTION_RECOMMEND_REPEATADMISSION);
							}else if(resolution.getRecommendationStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION)){
								Status RESOLUTION_RECOMMEND_REPEATREJECTION = Status.findByType(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION, domain.getLocale());
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusLowerHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusLowerHouse(RESOLUTION_RECOMMEND_REPEATREJECTION);
							}else{
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusLowerHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusLowerHouse(ASSISTANT_PROCESSED);
							}
						}
					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						internalStatus = resolution.getInternalStatusUpperHouse().getType();
						if(internalStatus.equals(ApplicationConstants.RESOLUTION_SUBMIT)&&domain.getMinistry()!=null && domain.getSubDepartment()!=null) {
							if(resolution.getRecommendationStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION)){
								Status RESOLUTION_RECOMMEND_REPEATADMISSION = Status.findByType(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION, domain.getLocale());
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusUpperHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusUpperHouse(RESOLUTION_RECOMMEND_REPEATADMISSION);
							}else if(resolution.getRecommendationStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION)){
								Status RESOLUTION_RECOMMEND_REPEATREJECTION = Status.findByType(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION, domain.getLocale());
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusUpperHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusUpperHouse(RESOLUTION_RECOMMEND_REPEATREJECTION);
							}else{
								Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
								domain.setInternalStatusUpperHouse(ASSISTANT_PROCESSED);
								domain.setRecommendationStatusUpperHouse(ASSISTANT_PROCESSED);
							}
						}
					}
				}
				/**** File parameters are set when internal status is something other than 
				 * submit,complete and incomplete and file is null .Then only the resolution gets attached to a file.*/
				
//				if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
//					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
//						if(operation==null){
//							if(!(domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
//									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
//									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))							
//									&&domain.getFileLowerHouse()==null){
//								/**** Add resolution to file ****/
//								if(domain.getNumber()!=null){
//									Reference reference=Resolution.findCurrentFile(domain,domain.getHouseType());
//									domain.setFileLowerHouse(Integer.parseInt(reference.getId()));
//									domain.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
//									domain.setFileSentLowerHouse(false);
//								}
//							}
//						}else if(operation.isEmpty()){
//							if(!(domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
//									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
//									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))
//									&&domain.getFileLowerHouse()==null){
//								/**** Add resolution to file ****/
//								if(domain.getNumber()!=null){
//									Reference reference=Resolution.findCurrentFile(domain,domain.getHouseType());
//									domain.setFileLowerHouse(Integer.parseInt(reference.getId()));
//									domain.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
//									domain.setFileSentLowerHouse(false);
//								}
//								
//							}
//						}
//					}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
//						if(operation==null){
//							if(!(domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
//									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
//									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))							
//									&&domain.getFileUpperHouse()==null){
//								/**** Add resolution to file ****/
//								if(domain.getNumber()!=null){
//									Reference reference=Resolution.findCurrentFile(domain,domain.getHouseType());
//									domain.setFileUpperHouse(Integer.parseInt(reference.getId()));
//									domain.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
//									domain.setFileSentUpperHouse(false);
//								}
//							}
//						}else if(operation.isEmpty()){
//							if(!(domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
//									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
//									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))
//									&&domain.getFileUpperHouse()==null){
//								/**** Add resolution to file ****/
//								if(domain.getNumber()!=null){
//									Reference reference=Resolution.findCurrentFile(domain,domain.getHouseType());
//									domain.setFileUpperHouse(Integer.parseInt(reference.getId()));
//									domain.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
//									domain.setFileSentUpperHouse(false);
//								}
//								
//							}
//						}
//					}
//					
//				}else 
				if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
					String strHouseType=this.getCurrentUser().getHouseType();
					HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, domain.getLocale());
					if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
						if(operation==null){
							if(!(domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))							
									&&domain.getFileLowerHouse()==null){
								/**** Add resolution to file ****/
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(domain,houseType);
									domain.setFileLowerHouse(Integer.parseInt(reference.getId()));
									domain.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
									domain.setFileSentLowerHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
							}
						}else if(operation.isEmpty()){
							if(!(domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
									||domain.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))
									&&domain.getFileLowerHouse()==null){
								/**** Add resolution to file ****/
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(domain,houseType);
									domain.setFileLowerHouse(Integer.parseInt(reference.getId()));
									domain.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
									domain.setFileSentLowerHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
							}
						}
					}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(operation==null){
							if(!(domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))							
									&&domain.getFileUpperHouse()==null){
								/**** Add resolution to file ****/
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(domain,houseType);
									domain.setFileUpperHouse(Integer.parseInt(reference.getId()));
									domain.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
									domain.setFileSentUpperHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}else if(operation.isEmpty()){
							if(!(domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)
									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)
									||domain.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE))
									&&domain.getFileUpperHouse()==null){
								/**** Add resolution to file ****/
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(domain,houseType);
									domain.setFileUpperHouse(Integer.parseInt(reference.getId()));
									domain.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
									domain.setFileSentUpperHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}
					}
				}

			}
		}
		/****For Voting Details*****//*
		Resolution resolution=Resolution.findById(Resolution.class, domain.getId());
		if(resolution!=null){
			if(resolution.getVotingDetails()!=null && !resolution.getVotingDetails().isEmpty()){
				domain.setVotingDetails(resolution.getVotingDetails());
			}
		}*/
		/**** updating submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		String strHouseType=this.getCurrentUser().getHouseType();
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
				if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
					if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
						domain.setWorkflowStartedOnLowerHouse(format.parse(strWorkflowStartedOnDate));
					}
					if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
						domain.setTaskReceivedOnLowerHouse(format.parse(strTaskReceivedOnDate));
					}
				}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
					if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
						domain.setWorkflowStartedOnUpperHouse(format.parse(strWorkflowStartedOnDate));
					}
					if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
						domain.setTaskReceivedOnUpperHouse(format.parse(strTaskReceivedOnDate));
					}
				}
				
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void populateAfterUpdate(final ModelMap model, final Resolution domain,
			final HttpServletRequest request) {		
		
		HouseType houseType = null;
		if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseType = HouseType.findByFieldName(HouseType.class, "type", this.getCurrentUser().getHouseType(), domain.getLocale());
		} else {
			houseType = domain.getHouseType();
		}
		if(request.getParameter("bulkedit")!=null&&!request.getParameter("bulkedit").isEmpty()){
			request.getSession().setAttribute("bulkedit",request.getParameter("bulkedit"));
		}
		/**** set housetype & statuses in draft for government resolution ****/
		if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			ResolutionDraft latestDraft;
			try {
				latestDraft = Resolution.getLatestResolutionDraftOfUser(domain.getId(), this.getCurrentUser().getActualUsername());
				String internalStatusType = domain.getInternalStatusLowerHouse().getType();
				if(!(internalStatusType.equals(ApplicationConstants.RESOLUTION_INCOMPLETE)) && !(internalStatusType.equals(ApplicationConstants.RESOLUTION_COMPLETE))) {				
					if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){					
						latestDraft.setStatus(domain.getStatusLowerHouse());
						latestDraft.setInternalStatus(domain.getInternalStatusLowerHouse());
						latestDraft.setRecommendationStatus(domain.getRecommendationStatusLowerHouse());					
					} else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){					
						latestDraft.setStatus(domain.getStatusUpperHouse());
						latestDraft.setInternalStatus(domain.getInternalStatusUpperHouse());
						latestDraft.setRecommendationStatus(domain.getRecommendationStatusUpperHouse());					
					}
					UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"name",latestDraft.getEditedAs(), domain.getLocale());
					if(!userGroupType.getType().equals(ApplicationConstants.MEMBER)) {
						latestDraft.setHouseType(houseType);						
					}
					latestDraft.setDiscussionDate(domain.getDiscussionDate());
					latestDraft.merge();
				}
				
			} catch (ELSException e) {	
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}			
		}		
		
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Approval Workflow ****/
		String bulkedit=request.getParameter("bulkedit");
		/*********If bulkedit=yes, the individual resolution should be saved and not put up******************/
			if(bulkedit==null||!bulkedit.equals("yes")){
				String operation=request.getParameter("operation");
				if(operation!=null){
					if(!operation.isEmpty()){
						 if(operation.equals("startworkflow")){
							
							ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
							Map<String,String> properties=new HashMap<String, String>();					
							/**** Next user and usergroup ****/
							String nextuser=null;
							String endflag=null;
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								nextuser=request.getParameter("actorLowerHouse");
								endflag=request.getParameter("endFlagLowerHouse");
							}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								nextuser=request.getParameter("actorUpperHouse");
								endflag=request.getParameter("endFlagUpperHouse");
							}
							String level="";
							String username = "";
							if(nextuser!=null){
								if(!nextuser.isEmpty()){
									String[] temp=nextuser.split("#");
									username = temp[0];
									properties.put("pv_user",username);
									level=temp[2];
								}
							}
							
							properties.put("pv_deviceId",String.valueOf(domain.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
							
							
							properties.put("pv_endflag",endflag);	
							
							String mailflag=request.getParameter("mailflag");				
							properties.put("pv_mailflag", mailflag);
							
							if(mailflag!=null) {
								if(mailflag.equals("set")) {
									String mailfrom=request.getParameter("mailfrom");
									properties.put("pv_mailfrom", mailfrom);
									
									String mailto=request.getParameter("mailto");
									properties.put("pv_mailto", mailto);
									
									String mailsubject=request.getParameter("mailsubject");
									properties.put("pv_mailsubject", mailsubject);
									
									String mailcontent=request.getParameter("mailcontent");
									properties.put("pv_mailcontent", mailcontent);
								}
							}
							
							String timerflag=request.getParameter("timerflag");
							properties.put("pv_timerflag", timerflag);
							
							if(timerflag!=null) {
								if(timerflag.equals("set")) {
									String timerduration=request.getParameter("timerduration");
									properties.put("pv_timerduration", timerduration);
									
									String lasttimerduration=request.getParameter("lasttimerduration");
									properties.put("pv_lasttimerduration", lasttimerduration);
									
									String reminderflag=request.getParameter("reminderflag");
									properties.put("pv_reminderflag", reminderflag);
									
									if(reminderflag!=null) {
										if(reminderflag.equals("set")) {
											String reminderfrom=request.getParameter("reminderfrom");
											properties.put("pv_reminderfrom", reminderfrom);
											
											String reminderto=request.getParameter("reminderto");
											properties.put("pv_reminderto", reminderto);
											
											//String remindersubject=request.getParameter("remindersubject");
											String remindersubject=domain.getSubject();
											properties.put("pv_remindersubject", remindersubject);
											
											//String remindercontent=request.getParameter("remindercontent");
											String remindercontent=domain.getNoticeContent();
											properties.put("pv_remindercontent", remindercontent);
										}
									}
									
									if(domain.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
										properties.put("pv_houseType", houseType.getType());
									}
								}
							}
							ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
							/**** Stale State Exception ****/
							Resolution resolution=Resolution.findById(Resolution.class,domain.getId());
							/**** Process Started and task created ****/
							Task task=processService.getCurrentTask(processInstance);
							if(endflag!=null){
								if(!endflag.isEmpty()){
									if(endflag.equals("continue")){
										/**** Workflow Detail entry made only if its not the end of workflow ****/								
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level,houseType);
											if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
												resolution.setWorkflowDetailsIdLowerHouse(workflowDetails.getId());
											}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
												resolution.setWorkflowDetailsIdUpperHouse(workflowDetails.getId());
											}
											
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
									}
								}
							}
							
							/**** Workflow Started ****/
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								resolution.setWorkflowStartedLowerHouse("YES");
								resolution.setWorkflowStartedOnLowerHouse(new Date());
								resolution.setTaskReceivedOnLowerHouse(new Date());
								/**** If resolution is sent individually then its file's parameters is set to null i.e 
								 * it is removed from file ****/
								resolution.setFileLowerHouse(null);
								resolution.setFileIndexLowerHouse(null);
								resolution.setFileSentLowerHouse(false);
							}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								resolution.setWorkflowStartedUpperHouse("YES");
								resolution.setWorkflowStartedOnUpperHouse(new Date());
								resolution.setTaskReceivedOnUpperHouse(new Date());
								/**** If resolution is sent individually then its file's parameters is set to null i.e 
								 * it is removed from file ****/
								resolution.setFileUpperHouse(null);
								resolution.setFileIndexUpperHouse(null);
								resolution.setFileSentUpperHouse(false);
							}
							
							resolution.simpleMerge();
						}
					}
				}
			}
		
		Status internalStatus=null;
		if(domain.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
			internalStatus = domain.getInternalStatusLowerHouse();
		}else if(domain.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
			internalStatus = domain.getInternalStatusUpperHouse();
		}
		 
		String deviceType=domain.getType().getType();
		/**** Add to chart in case of non official resolution if internal and recommendation status is already
		 * assistant processed ****/
		if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED)
				&& deviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			Resolution resolution = Resolution.findById(Resolution.class, domain.getId());
			if(resolution.getNumber()!=null){
				try{
					Chart.addToChart(resolution);
				}catch (ELSException e) {
					model.addAttribute(this.getClass().getName(), e.getParameter());
				}
			}
		}
	}
	
	
	@RequestMapping(value="/citations/{deviceType}",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,@PathVariable("deviceType")  final Long type,
			final ModelMap model){
		DeviceType deviceType=DeviceType.findById(DeviceType.class,type);
		List<Citation> deviceTypeBasedcitations=Citation.findAllByFieldName(Citation.class,"deviceType",deviceType, "text",ApplicationConstants.ASC, locale.toString());
		Status status=null;
		if(request.getParameter("status")!=null){
			status=Status.findById(Status.class, Long.parseLong(request.getParameter("status")));
		}
		List<Citation> citations=new ArrayList<Citation>();
		if(status!=null){
			for(Citation i:deviceTypeBasedcitations){
				if(i.getStatus()!=null){
					if(i.getStatus().equals(status.getType())){
						citations.add(i);
					}
				}
			}
		}
		model.addAttribute("citations",citations);
		return "resolution/citation";
	}
	
	@RequestMapping(value="/revisions/{resolutionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("resolutionId")  final Long resolutionId,
			final ModelMap model, final HttpServletRequest request){
		List<RevisionHistoryVO> drafts = new ArrayList<RevisionHistoryVO>();
		
		//revisions are housetype specific for government resolution
		String workflowHouseType = request.getParameter("workflowHouseType");
		
		//in case of non government resolutions, workflowHouseType is null.
		if(workflowHouseType == null) {
			try {
				drafts=Resolution.getRevisions(resolutionId,locale.toString());
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
		} 
		//for government resolutions
		else {
			if(!workflowHouseType.isEmpty()){
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				String server=customParameter.getValue();
				if(server.equals("TOMCAT")){
					try {
						workflowHouseType = new String(workflowHouseType.getBytes("ISO-8859-1"),"UTF-8");							
					}catch (UnsupportedEncodingException e) {
						logger.error("Cannot Encode the Parameter.");
					}
				}
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "name", workflowHouseType, locale.toString());
				try {
					drafts=Resolution.getRevisions(resolutionId, houseType.getId(), locale.toString());
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
			} else {
				logger.error("request parameter workflowHouseType is empty.");
			}
		}
		
		
		Resolution r = Resolution.findById(Resolution.class, resolutionId);
		if(r != null){
			if(r.getType() != null){
				if(r.getType().getType() != null){
					model.addAttribute("selectedDeviceType", r.getType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		return "resolution/revisions";
	}
	
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMember=request.getParameter("member");
		String[] member={strMember};
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(member);
		model.addAttribute("membersContact",memberContactVOs);
		return "resolution/contacts";
	}
	
	
	@RequestMapping(value="/discussresolutions", method=RequestMethod.POST)
	public void doDiscussResolutions(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		String[] checks = request.getParameterValues("tobeDiscussed");
		String allIds = request.getParameter("allids");
		try{
			if(checks != null){
				String[] idsAll = allIds.split("#");
				for(String idx: idsAll){
					if(getMatch(idx, checks)){
						String[] idAndSelctedForDiscussion = idx.split(";");
						if(idAndSelctedForDiscussion[1].equals("unchecked")){
							Resolution res = Resolution.findById(Resolution.class, Long.parseLong(idAndSelctedForDiscussion[0]));
							Status status = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_PROCESSED_TOBEDISCUSSED, locale.toString());
							Status recommendationStatus=Status.findByType(ApplicationConstants.RESOLUTION_PROCESSED_UNDERCONSIDERATION, locale.toString());
							res.setDiscussionStatus(status);
							if(res.getHouseType()!=null){
								if(res.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									//res.setInternalStatusLowerHouse(internalStatus);
									res.setRecommendationStatusLowerHouse(recommendationStatus);
								}else if(res.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									//res.setInternalStatusUpperHouse(internalStatus);
									res.setRecommendationStatusUpperHouse(recommendationStatus);
								}
							}
							Thread.sleep(100);
							res.merge();
						}
					}
				}
				
				Thread.sleep(200);
				
				for(String idAndStatus: idsAll){
					String id = idAndStatus.split(";")[0];
					if(!isSelcted(id, checks)){
						Resolution res = Resolution.findById(Resolution.class, Long.parseLong(id));					
						res.setDiscussionStatus(null);
						res.merge();
					}
				}
			}else{
				String[] idsAll = allIds.split("#");
				for(String idx: idsAll){
					String[] idAndSelctedForDiscussion = idx.split(";");
					Resolution res = Resolution.findById(Resolution.class, Long.parseLong(idAndSelctedForDiscussion[0]));					
					res.setDiscussionStatus(null);
					Thread.sleep(100);
					res.merge();
				}
			}
			model.addAttribute("done", "done");
			
		}catch (Exception e) {
			model.addAttribute("done", "fail");
			e.printStackTrace();
		}		
	}
	
	private boolean getMatch(final String toBeMatchWith, final String[] matches){
		boolean matchFound = false;
		for(int i = 0; i < matches.length; i++){
			if(toBeMatchWith.indexOf(matches[i]) > -1){
				matchFound = true;
				break;
			}
		}
		return matchFound;
	}
	
	private boolean isSelcted(final String id, final String[] ids){
		boolean isSelected = false;
		
		for (int i = 0; i < ids.length; i++) {
			if(id.equals(ids[i])){
				isSelected = true;
				break;
			}			
		}
		
		return isSelected;
	}
	
	
	/**** Bulk Submission ****/
	@RequestMapping(value="/bulksubmission",method=RequestMethod.GET)
	public String getBulkSubmissionView(final HttpServletRequest request,final Locale locale,
			final ModelMap model){	
		Member primaryMember=Member.findMember(this.getCurrentUser().getFirstName(),this.getCurrentUser().getMiddleName(),this.getCurrentUser().getLastName(),this.getCurrentUser().getBirthDate(),locale.toString());
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strdeviceType=request.getParameter("deviceType");
		String strLocale=locale.toString();
		String strItemsCount=request.getParameter("itemscount");
		if(strHouseType!=null&&!(strHouseType.isEmpty())
			&& strdeviceType!=null && !(strdeviceType.isEmpty())
			&& strSessionType!=null && !(strSessionType.isEmpty())
			&& strSessionYear!=null && !(strSessionYear.isEmpty())
			&& strItemsCount!=null && !(strItemsCount.isEmpty())){
				HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
				SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Integer sessionYear=Integer.parseInt(strSessionYear);
				Session session;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					DeviceType	deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strdeviceType));
					Integer itemsCount=Integer.parseInt(strItemsCount);
					List<Resolution> resolutions = new ArrayList<Resolution>();
					if(primaryMember != null){
						resolutions = Resolution.findAllByMember(session,primaryMember,deviceType,itemsCount,strLocale);
					}
					model.addAttribute("houseType", houseType.getId());
					model.addAttribute("resolutionType", deviceType.getId());
					model.addAttribute("deviceType",deviceType.getId());
					model.addAttribute("resolutions",resolutions);
					model.addAttribute("size",resolutions.size());
					model.addAttribute("locale", locale.toString());
					String userGroupType = request.getParameter("usergroupType");
					model.addAttribute("usergroupType", userGroupType);
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
		}
			
		return "resolution/bulksubmission";		
	}

	/**
	 * We want to provide a guarantee that all the resolutions submitted by a 
	 * particular member will get numbers assigned sequentially. Hence, the
	 * use of synchronized method.
	 * 
	 * @param request
	 * @param locale
	 * @param model
	 * @return
	 */
	@Transactional
	@RequestMapping(value="bulksubmission", method=RequestMethod.POST)
	public synchronized String bulkSubmission(final HttpServletRequest request, final Locale locale,
			final ModelMap model) {
		String selectedItems = request.getParameter("items");
		if(selectedItems != null && ! selectedItems.isEmpty()) {
			String[] items = selectedItems.split(",");
			Resolution domain = Resolution.findById(Resolution.class, new Long(items[0]));
			Session session = domain.getSession();
			boolean validationForSubmissionDate = false;
			//submission date limit validations
			if(domain.getSession()!=null && domain.getType()!=null) {
				CustomParameter deviceTypesHavingSubmissionStartDateValidationCP = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.DEVICETYPES_HAVING_SUBMISSION_START_DATE_VALIDATION, "");
				if(deviceTypesHavingSubmissionStartDateValidationCP!=null) {
					String deviceTypesHavingSubmissionStartDateValidationValue = deviceTypesHavingSubmissionStartDateValidationCP.getValue();
					if(deviceTypesHavingSubmissionStartDateValidationValue!=null) {
						String[] deviceTypesHavingSubmissionStartDateValidation = deviceTypesHavingSubmissionStartDateValidationValue.split(",");
						
						for(String dt: deviceTypesHavingSubmissionStartDateValidation) {
							if(dt.trim().equals(domain.getType().getType().trim())) {
								
								if(session.getParameter(domain.getType().getType() + "_" + "submissionStartDate")!=null && !session.getParameter(domain.getType().getType() + "_" + "submissionStartDate").isEmpty()
										&& session.getParameter(domain.getType().getType() + "_" + "submissionEndDate")!=null && !session.getParameter(domain.getType().getType() + "_" + "submissionEndDate").isEmpty()) {
									
									if(!Resolution.isAllowedForSubmission(domain, new Date())) {
										//String submissionStartLimitDateStr = domain.getSession().getParameter(domain.getType().getType()+"_submissionStartDate");
										validationForSubmissionDate = true;
									}
								} else {
									validationForSubmissionDate = true;
								}
								
								break;
							}
						}
					}
				}
			}

			if(!validationForSubmissionDate) {
				List<Resolution> resolutions = new ArrayList<Resolution>();
				for(String i : items) {
					Long id = Long.parseLong(i);
					Resolution resolution = Resolution.findById(Resolution.class, id);

					/**** Update Status(es) ****/
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_SUBMIT, resolution.getLocale());
					if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							resolution.setStatusLowerHouse(newstatus);
							resolution.setInternalStatusLowerHouse(newstatus);
							resolution.setRecommendationStatusLowerHouse(newstatus);
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							resolution.setStatusUpperHouse(newstatus);
							resolution.setInternalStatusUpperHouse(newstatus);
							resolution.setRecommendationStatusUpperHouse(newstatus);
						}
					}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
						resolution.setStatusLowerHouse(newstatus);
						resolution.setInternalStatusLowerHouse(newstatus);
						resolution.setRecommendationStatusLowerHouse(newstatus);
						resolution.setStatusUpperHouse(newstatus);
						resolution.setInternalStatusUpperHouse(newstatus);
						resolution.setRecommendationStatusUpperHouse(newstatus);
					}
					
					

					/**** Edited On,Edited By and Edited As is set ****/
					resolution.setSubmissionDate(new Date());
					resolution.setEditedOn(new Date());
					resolution.setEditedBy(this.getCurrentUser().getActualUsername());
					String strUserGroupType=request.getParameter("usergroupType");
					if(strUserGroupType!=null){
						UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, resolution.getLocale());
						resolution.setEditedAs(userGroupType.getName());
					}				
					/**** Bulk Submitted ****/
					resolution.setBulkSubmitted(true);
					/**** Update the Motion object ****/
					resolution = resolution.merge();
					resolutions.add(resolution);
				}

				model.addAttribute("resolutions", resolutions);
			}			
		}
		return "resolution/bulksubmissionack";
	}

	/**** Bulk Submission ****/
	@RequestMapping(value="/bulksubmission/assistant/int",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantView(final HttpServletRequest request,final Locale locale,
			final ModelMap model){	
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDeviceType=request.getParameter("deviceType");
		String strLocale=locale.toString();
		String strRole=request.getParameter("role");
		String strUsergroup=request.getParameter("usergroup");
		String strItemsCount=request.getParameter("itemscount");
		String strUsergroupType=request.getParameter("usergroupType");
		String strStatus=request.getParameter("status");
		String strFile=request.getParameter("file");
		HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", this.getCurrentUser().getHouseType(), locale.toString());
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strDeviceType!=null&&!(strDeviceType.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strFile!=null&&!(strFile.isEmpty())){
			HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			/**** Decision Status Available To Assistant(At this stage) 
			 * RESOLUTION_PUT_UP_OPTIONS_+DEVICE_TYPE+HOUSE_TYPE+USERGROUP_TYPE ****/
			CustomParameter defaultStatus=CustomParameter.findByName(CustomParameter.class,"RESOLUTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+strUsergroupType.toUpperCase(), "");
			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(), locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
			
			/**** Request Params To Model Attribute ****/
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				model.addAttribute("houseType", strHouseType);
			}else if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
				model.addAttribute("houseType", houseTypeForWorkflow.getName());
			}
			
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("deviceType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("itemscount", strItemsCount);
			model.addAttribute("file", strFile);
			
		}
		return "resolution/bulksubmissionassistantint";		
	}
	
	@RequestMapping(value="/bulksubmission/assistant/view",method=RequestMethod.GET)
	public String getBulkSubmissionAssistantView(final HttpServletRequest request,final Locale locale,
			final Model model){	
		getBulkSubmissionResolutions(model,request,locale.toString());
		return "resolution/bulksubmissionassistantview";		
	}
	

	@Transactional
	@RequestMapping(value="/bulksubmission/assistant/update",method=RequestMethod.POST)
	public String bulkSubmissionAssistant(final HttpServletRequest request,final Locale locale,
			final Model model){	
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus=request.getParameter("status");
		String refText = request.getParameter("referencedText");
		String remark = request.getParameter("remarks");
		String strFile = request.getParameter("file");
		StringBuffer assistantProcessed=new StringBuffer();
		StringBuffer recommendAdmission=new StringBuffer();
		StringBuffer recommendRejection=new StringBuffer();
		StringBuffer recommendClarificationFromDepartment=new StringBuffer();
		StringBuffer recommendClarificationFromMember=new StringBuffer();
		StringBuffer recommendRepeatAdmission=new StringBuffer();
		StringBuffer recommendRepeatRejection=new StringBuffer();
		if(selectedItems != null && selectedItems.length>0
				&&strStatus!=null&&!strStatus.isEmpty()) {
			/**** As It Is Condition ****/
			if(strStatus.equals("-")){
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Resolution resolution = Resolution.findById(Resolution.class, id);
					Status internalStatus=null;
					if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						internalStatus=resolution.getInternalStatusLowerHouse();
					}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						internalStatus=resolution.getInternalStatusUpperHouse();
					}
					/**** Add resolution to file ****/
					/*****************Added By Anand********/
					if(remark != null && !remark.isEmpty()){
						resolution.setRemarks(remark);
					}
					
					if(refText != null && !refText.isEmpty()){
						resolution.setReferencedResolutionText(refText);
					}
					
					if(strFile != null && !strFile.isEmpty()){
						if(resolution.getFile() == null){
							resolution.setFile(new Integer(strFile));
						}
					}
					
					if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileLowerHouse()==null){
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(resolution,resolution.getHouseType());
									resolution.setFileLowerHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentLowerHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileUpperHouse()==null){
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(resolution,resolution.getHouseType());
									resolution.setFileUpperHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentUpperHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}
					}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
						String strHouseType=this.getCurrentUser().getHouseType();
						HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
						if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileLowerHouse()==null){
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(resolution,houseType);
									resolution.setFileLowerHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentLowerHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileUpperHouse()==null){
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(resolution,houseType);
									resolution.setFileUpperHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentUpperHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}
					}
					
					if(!internalStatus.getType().equals(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED)){
						/**** Create Process ****/
						ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						Map<String,String> properties=new HashMap<String, String>();
						String actor=null;
						String endFlag=null;
						if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
							if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
								 actor=resolution.getActorLowerHouse();
								 endFlag=resolution.getEndFlagLowerHouse();
							}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
								 actor=resolution.getActorUpperHouse();
								 endFlag=resolution.getEndFlagUpperHouse();
							}
						}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
							String strHouseType=this.getCurrentUser().getHouseType();
							// HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
							if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
								 actor=resolution.getActorLowerHouse();
								 endFlag=resolution.getEndFlagLowerHouse();
							}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
								 actor=resolution.getActorUpperHouse();
								 endFlag=resolution.getEndFlagUpperHouse();
							}
						}
						
						String[] temp=actor.split("#");
						properties.put("pv_user",temp[0]);						
						properties.put("pv_endflag",endFlag);	
						properties.put("pv_deviceId",String.valueOf(resolution.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(resolution.getType().getId()));
						ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
						/**** Create Workdetails Entry ****/
						Task task=processService.getCurrentTask(processInstance);
						if(endFlag!=null&&!endFlag.isEmpty()&&endFlag.equals("continue")){
							if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
								if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									WorkflowDetails workflowDetails;
									try {
										workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelLowerHouse(),resolution.getHouseType());
										resolution.setWorkflowDetailsIdLowerHouse(workflowDetails.getId());
									} catch (ELSException e) {
										model.addAttribute("error", e.getParameter());
										e.printStackTrace();
									}
									
									/**** Workflow Started ****/
									resolution.setWorkflowStartedLowerHouse("YES");
									resolution.setWorkflowStartedOnLowerHouse(new Date());
									resolution.setTaskReceivedOnLowerHouse(new Date());
									resolution.setFileSentLowerHouse(true);
								}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									WorkflowDetails workflowDetails;
									try {
										workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelUpperHouse(),resolution.getHouseType());
										resolution.setWorkflowDetailsIdUpperHouse(workflowDetails.getId());
									} catch (ELSException e) {
										model.addAttribute("error", e.getParameter());
										e.printStackTrace();
									}
									
									/**** Workflow Started ****/
									resolution.setWorkflowStartedUpperHouse("YES");
									resolution.setWorkflowStartedOnUpperHouse(new Date());
									resolution.setTaskReceivedOnUpperHouse(new Date());
									resolution.setFileSentUpperHouse(true);
								}
							}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
								String strHouseType=this.getCurrentUser().getHouseType();
								// HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
								if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
									WorkflowDetails workflowDetails;
									try {
										workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelLowerHouse(),resolution.getHouseType());
										resolution.setWorkflowDetailsIdLowerHouse(workflowDetails.getId());
									} catch (ELSException e) {
										model.addAttribute("error", e.getParameter());
										e.printStackTrace();
									}
									
									/**** Workflow Started ****/
									resolution.setWorkflowStartedLowerHouse("YES");
									resolution.setWorkflowStartedOnLowerHouse(new Date());
									resolution.setTaskReceivedOnLowerHouse(new Date());
									resolution.setFileSentLowerHouse(true);
									
								}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
									WorkflowDetails workflowDetails;
									try {
										workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelUpperHouse(),resolution.getHouseType());
										resolution.setWorkflowDetailsIdUpperHouse(workflowDetails.getId());
									} catch (ELSException e) {
										model.addAttribute("error", e.getParameter());
										e.printStackTrace();
									}
									
									/**** Workflow Started ****/
									resolution.setWorkflowStartedUpperHouse("YES");
									resolution.setWorkflowStartedOnUpperHouse(new Date());
									resolution.setTaskReceivedOnUpperHouse(new Date());
									resolution.setFileSentUpperHouse(true);
									
								}
							}
							
							/***Setting the edited On , Edited By and Edited As***/
							List<UserGroup> usergroups = this.getCurrentUser().getUserGroups();
							Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getUsername(), locale.toString());
							resolution.setEditedBy(credential.getUsername());
							for(UserGroup u : usergroups){
								UserGroup userGroup = UserGroup.findActive(credential, u.getUserGroupType(), new Date(), locale.toString());
								if(userGroup != null){
									UserGroupType userGroupType = userGroup.getUserGroupType();
									if(userGroupType != null){
										resolution.setEditedAs(userGroupType.getName());
									}
								}
							}
							resolution.setEditedOn(new Date());
							
							resolution.merge();
						}	
						if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_ADMISSION)){
							recommendAdmission.append(resolution.formatNumber()+",");
						}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REJECTION)){
							recommendRejection.append(resolution.formatNumber()+",");
						}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
							recommendClarificationFromDepartment.append(resolution.formatNumber()+",");
						}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
							recommendClarificationFromMember.append(resolution.formatNumber()+",");
						}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION)){
							recommendRepeatAdmission.append(resolution.formatNumber()+",");
						}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION)){
							recommendRepeatRejection.append(resolution.formatNumber()+",");
						}
					}else{
						assistantProcessed.append(resolution.formatNumber()+",");
					}
				}	
				model.addAttribute("assistantProcessed", assistantProcessed.toString());
			}else{
				Long statusId=Long.parseLong(strStatus);
				Status status=Status.findById(Status.class,statusId);
				for(String i : selectedItems) {
					Long id = Long.parseLong(i);
					Resolution resolution = Resolution.findById(Resolution.class, id);
					/*****************Added By Anand********/
					
					if(remark != null && !remark.isEmpty()){
						resolution.setRemarks(remark);
					}
					
					if(refText != null && !refText.isEmpty()){
						resolution.setReferencedResolutionText(refText);
					}
					
					if(strFile != null && !strFile.isEmpty()){
						if(resolution.getFile() == null){
							resolution.setFile(new Integer(strFile));
						}
					}
					
					/* The resolutions are assigned the default file number incase of absence of file no.*/
					if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileLowerHouse()==null){
								Reference reference;
								try {
									reference = Resolution.findCurrentFile(resolution,resolution.getHouseType());
									resolution.setFileLowerHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentLowerHouse(false);
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
								
							}
						}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileUpperHouse()==null){
								try {
									Reference reference=Resolution.findCurrentFile(resolution,resolution.getHouseType());
									resolution.setFileUpperHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentUpperHouse(false);
								} catch (NumberFormatException e) {
									e.printStackTrace();
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
							}
						}
					}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
						String strHouseType=this.getCurrentUser().getHouseType();
						HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
						if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileLowerHouse()==null){
								try {
									Reference reference=Resolution.findCurrentFile(resolution,houseType);
									resolution.setFileLowerHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexLowerHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentLowerHouse(false);
								} catch (NumberFormatException e) {
									e.printStackTrace();
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
							}
						}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
							/* The resolutions are assigned the default file number incase of absence of file no.*/
							if(resolution.getFileUpperHouse()==null){
								try {
									Reference reference=Resolution.findCurrentFile(resolution,houseType);
									resolution.setFileUpperHouse(Integer.parseInt(reference.getId()));
									resolution.setFileIndexUpperHouse(Integer.parseInt(reference.getName()));
									resolution.setFileSentUpperHouse(false);
								} catch (NumberFormatException e) {									
									e.printStackTrace();
								} catch (ELSException e) {
									model.addAttribute("error", e.getParameter());
									e.printStackTrace();
								}
							}
						}
					}
					String actor=request.getParameter("actor");
					String level=request.getParameter("level");
					Status internalStatus=null;
					if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						internalStatus=resolution.getInternalStatusLowerHouse();
					}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						internalStatus=resolution.getInternalStatusUpperHouse();
					}
					if(actor!=null&&!actor.isEmpty()
							&&level!=null&&!level.isEmpty()){
						String workflowHouseType=null;
						if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
							workflowHouseType=resolution.getHouseType().getType();
						}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
							workflowHouseType=this.getCurrentUser().getHouseType();
							HouseType houseType=HouseType.findByFieldName(HouseType.class,"type", workflowHouseType, locale.toString());
							workflowHouseType=houseType.getName();
						}
						Reference reference = null;
						try {
							reference = UserGroup.findResolutionActor(resolution,workflowHouseType,actor,level,locale.toString());
						} catch (ELSException e) {
							model.addAttribute("error", e.getParameter());
							e.printStackTrace();
						}
						if(reference!=null
								&&reference.getId()!=null&&!reference.getId().isEmpty()
								&&reference.getName()!=null&&!reference.getName().isEmpty()){
							String[] temp=reference.getId().split("#");
							/**** Update Actor ****/
							if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
								if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									
									resolution.setActorLowerHouse(reference.getId());
									resolution.setLocalizedActorNameLowerHouse(temp[3]+"("+temp[4]+")");
									resolution.setLevelLowerHouse(temp[2]);
									resolution.setEndFlagLowerHouse("continue");
									/**** Update Internal Status and Recommendation Status ****/
									resolution.setInternalStatusLowerHouse(status);
									resolution.setRecommendationStatusLowerHouse(status);
								}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									resolution.setActorUpperHouse(reference.getId());
									resolution.setLocalizedActorNameUpperHouse(temp[3]+"("+temp[4]+")");
									resolution.setLevelUpperHouse(temp[2]);
									resolution.setEndFlagUpperHouse("continue");
									/**** Update Internal Status and Recommendation Status ****/
									resolution.setInternalStatusUpperHouse(status);
									resolution.setRecommendationStatusUpperHouse(status);
								}
							}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
								String strHouseType=this.getCurrentUser().getHouseType();
								// HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
								if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
									resolution.setActorLowerHouse(reference.getId());
									resolution.setLocalizedActorNameLowerHouse(temp[3]+"("+temp[4]+")");
									resolution.setLevelLowerHouse(temp[2]);
									resolution.setEndFlagLowerHouse("continue");
									/**** Update Internal Status and Recommendation Status ****/
									resolution.setInternalStatusLowerHouse(status);
									resolution.setRecommendationStatusLowerHouse(status);
								}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
									resolution.setActorUpperHouse(reference.getId());
									resolution.setLocalizedActorNameUpperHouse(temp[3]+"("+temp[4]+")");
									resolution.setLevelUpperHouse(temp[2]);
									resolution.setEndFlagUpperHouse("continue");
									/**** Update Internal Status and Recommendation Status ****/
									resolution.setInternalStatusUpperHouse(status);
									resolution.setRecommendationStatusUpperHouse(status);
								}
							}
							
							
							
							/**** Create Process ****/
							String endFlag=null;
							if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
								if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									 endFlag=resolution.getEndFlagLowerHouse();
								}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
									 actor=resolution.getActorUpperHouse();
									 endFlag=resolution.getEndFlagUpperHouse();
								}
							}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
								String strHouseType=this.getCurrentUser().getHouseType();
								// HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
								if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
									 endFlag=resolution.getEndFlagLowerHouse();
								}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
									 endFlag=resolution.getEndFlagUpperHouse();
								}
							}
							ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
							Map<String,String> properties=new HashMap<String, String>();					
							properties.put("pv_user",temp[0]);						
							properties.put("pv_endflag",endFlag);	
							properties.put("pv_deviceId",String.valueOf(resolution.getId()));
							properties.put("pv_deviceTypeId",String.valueOf(resolution.getType().getId()));
							String mailflag=request.getParameter("mailflag");				
							properties.put("pv_mailflag", mailflag);
							
							if(mailflag!=null) {
								if(mailflag.equals("set")) {
									String mailfrom=request.getParameter("mailfrom");
									properties.put("pv_mailfrom", mailfrom);
									
									String mailto=request.getParameter("mailto");
									properties.put("pv_mailto", mailto);
									
									String mailsubject=request.getParameter("mailsubject");
									properties.put("pv_mailsubject", mailsubject);
									
									String mailcontent=request.getParameter("mailcontent");
									properties.put("pv_mailcontent", mailcontent);
								}
							}
							
							String timerflag=request.getParameter("timerflag");
							properties.put("pv_timerflag", timerflag);
							
							if(timerflag!=null) {
								if(timerflag.equals("set")) {
									String timerduration=request.getParameter("timerduration");
									properties.put("pv_timerduration", timerduration);
									
									String lasttimerduration=request.getParameter("lasttimerduration");
									properties.put("pv_lasttimerduration", lasttimerduration);
									
									String reminderflag=request.getParameter("reminderflag");
									properties.put("pv_reminderflag", reminderflag);
									
									if(reminderflag!=null) {
										if(reminderflag.equals("set")) {
											String reminderfrom=request.getParameter("reminderfrom");
											properties.put("pv_reminderfrom", reminderfrom);
											
											String reminderto=request.getParameter("reminderto");
											properties.put("pv_reminderto", reminderto);
											
											//String remindersubject=request.getParameter("remindersubject");
											String remindersubject=resolution.getSubject();
											properties.put("pv_remindersubject", remindersubject);
											
											//String remindercontent=request.getParameter("remindercontent");
											String remindercontent=resolution.getNoticeContent();
											properties.put("pv_remindercontent", remindercontent);
										}
									}
								}
							}
							ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
							/**** Create Workdetails Entry ****/
							Task task=processService.getCurrentTask(processInstance);
							if(endFlag!=null&&!endFlag.isEmpty()&&endFlag.equals("continue")){
								if(resolution.getType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
									if(resolution.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelLowerHouse(),resolution.getHouseType());
											resolution.setWorkflowDetailsIdLowerHouse(workflowDetails.getId());
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
										
										/**** Workflow Started ****/
										resolution.setWorkflowStartedLowerHouse("YES");
										resolution.setWorkflowStartedOnLowerHouse(new Date());
										resolution.setTaskReceivedOnLowerHouse(new Date());
										resolution.setFileSentLowerHouse(true);
									}else if(resolution.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelUpperHouse(),resolution.getHouseType());
											resolution.setWorkflowDetailsIdUpperHouse(workflowDetails.getId());
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
										
										/**** Workflow Started ****/
										resolution.setWorkflowStartedUpperHouse("YES");
										resolution.setWorkflowStartedOnUpperHouse(new Date());
										resolution.setTaskReceivedOnUpperHouse(new Date());
										resolution.setFileSentUpperHouse(true);
									}
								}else if(resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
									String strHouseType=this.getCurrentUser().getHouseType();
									HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
									if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelLowerHouse(),houseType);
											resolution.setWorkflowDetailsIdLowerHouse(workflowDetails.getId());
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
										/**** Workflow Started ****/
										resolution.setWorkflowStartedLowerHouse("YES");
										resolution.setWorkflowStartedOnLowerHouse(new Date());
										resolution.setTaskReceivedOnLowerHouse(new Date());
										resolution.setFileSentLowerHouse(true);
										
									}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
										WorkflowDetails workflowDetails;
										try {
											workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,resolution.getLevelUpperHouse(),houseType);
											resolution.setWorkflowDetailsIdUpperHouse(workflowDetails.getId());
										} catch (ELSException e) {
											model.addAttribute("error", e.getParameter());
											e.printStackTrace();
										}
										
										/**** Workflow Started ****/
										resolution.setWorkflowStartedUpperHouse("YES");
										resolution.setWorkflowStartedOnUpperHouse(new Date());
										resolution.setTaskReceivedOnUpperHouse(new Date());
										resolution.setFileSentUpperHouse(true);
										
									}
								}
								
								if(refText != null && !refText.isEmpty()){
									resolution.setReferencedResolutionText(refText);
								}
								if(remark != null && !remark.isEmpty()){
									resolution.setRemarks(remark);
								}
								
								/***Setting the edited On , Edited By and Edited As***/
								List<UserGroup> usergroups = this.getCurrentUser().getUserGroups();
								Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getUsername(), null);
								resolution.setEditedBy(credential.getUsername());
								for(UserGroup u : usergroups){
									UserGroup userGroup = UserGroup.findActive(credential, u.getUserGroupType(), new Date(), locale.toString());
									if(userGroup != null){
										UserGroupType userGroupType = userGroup.getUserGroupType();
										if(userGroupType != null){
											resolution.setEditedAs(userGroupType.getName());
										}
									}
								}
								resolution.setEditedOn(new Date());
								resolution.merge();
							}	
							if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_ADMISSION)){
								recommendAdmission.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REJECTION)){
								recommendRejection.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT)){
								recommendClarificationFromDepartment.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_CLARIFICATION_FROM_MEMBER)){
								recommendClarificationFromMember.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATADMISSION)){
								recommendRepeatAdmission.append(resolution.formatNumber()+",");
							}else if(internalStatus.getType().equals(ApplicationConstants.RESOLUTION_RECOMMEND_REPEATREJECTION)){
								recommendRepeatRejection.append(resolution.formatNumber()+",");
							}
						}					
					}
				}
				model.addAttribute("recommendAdmission", recommendAdmission.toString());
				model.addAttribute("recommendRejection", recommendRejection.toString());
				model.addAttribute("recommendClarificationFromDepartment",recommendClarificationFromDepartment.toString());
				model.addAttribute("recommendClarificationFromMember",recommendClarificationFromMember.toString());
				model.addAttribute("recommendRepeatRejection",recommendRepeatRejection.toString());
				model.addAttribute("recommendRepeatRejection",recommendRepeatRejection.toString());
			}
		}
		getBulkSubmissionResolutions(model,request,locale.toString());
		return "resolution/bulksubmissionassistantview";
	}
	
	public void getBulkSubmissionResolutions(final Model model,final HttpServletRequest request,final String locale){
		/**** Request Params ****/
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDeviceType=request.getParameter("deviceType");			
		String strStatus=request.getParameter("status");
		String strRole=request.getParameter("role");
		String strUsergroup=request.getParameter("usergroup");
		String strUsergroupType=request.getParameter("usergroupType");
		String strItemsCount=request.getParameter("itemscount");
		String strFile=request.getParameter("file");
		
		/**** Locale ****/
		String strLocale=locale;
		if(strHouseType==null|| strHouseType.isEmpty()){
			strHouseType=this.getCurrentUser().getHouseType();
		}
		/**** Null and Empty Check ****/
		if(strHouseType!=null&&!(strHouseType.isEmpty())
				&&strSessionType!=null&&!(strSessionType.isEmpty())
				&&strSessionYear!=null&&!(strSessionYear.isEmpty())
				&&strDeviceType!=null&&!(strDeviceType.isEmpty())
				&&strStatus!=null&&!(strStatus.isEmpty())
				&&strRole!=null&&!(strRole.isEmpty())
				&&strUsergroup!=null&&!(strUsergroup.isEmpty())
				&&strUsergroupType!=null&&!(strUsergroupType.isEmpty())
				&&strItemsCount!=null&&!(strItemsCount.isEmpty())
				&&strFile!=null&&!(strFile.isEmpty())){
			List<Resolution> resolutions=new ArrayList<Resolution>();
			DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			HouseType houseType=null;
			if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, strLocale);
			}else if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
				houseType=HouseType.findByFieldName(HouseType.class,"name",strHouseType, strLocale);
				if(houseType==null){
					String server=null;
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					String workflowHouseType=null;
					if(customParameter!=null){
						server=customParameter.getValue();
						if(server.equals("TOMCAT")){
							try {
								workflowHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");							
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot Encode the Parameter.");
							}
						}
					}
					
					houseType=HouseType.findByFieldName(HouseType.class,"name",workflowHouseType, strLocale);
				}
			}
			
			try{
				SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Integer sessionYear=Integer.parseInt(strSessionYear);
				Session session=Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				if(strFile!=null&&!strFile.isEmpty()&&!strFile.equals("-")){
					Integer file=Integer.parseInt(strFile);
					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						resolutions=Resolution.findAllByFile(session,deviceType,file,strLocale,houseType);
					}else if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
						if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							HouseType upperHouse=HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE, strLocale);
							Session upperHouseSession=Session.findSessionByHouseTypeSessionTypeYear(upperHouse, sessionType, sessionYear);
							List<Resolution> lowerHouseResolutions=Resolution.findAllByFile(session,deviceType,file,strLocale,houseType);
							List<Resolution> upperHouseResolutions=Resolution.findAllByFile(upperHouseSession,deviceType,file,strLocale,upperHouse);
							resolutions.addAll(lowerHouseResolutions);
							resolutions.addAll(upperHouseResolutions);
						}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
							HouseType lowerHouse=HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.LOWER_HOUSE, strLocale);
							Session lowerHouseSession=Session.findSessionByHouseTypeSessionTypeYear(lowerHouse, sessionType, sessionYear);
							List<Resolution> upperHouseResolutions=Resolution.findAllByFile(session,deviceType,file,strLocale,houseType);
							List<Resolution> lowerHouseResolutions=Resolution.findAllByFile(lowerHouseSession,deviceType,file,strLocale,lowerHouse);
							resolutions.addAll(lowerHouseResolutions);
							resolutions.addAll(upperHouseResolutions);
						}
					}
					
				}else if(strItemsCount!=null&&!strItemsCount.isEmpty()){
					Integer itemsCount=Integer.parseInt(strItemsCount);
					Status internalStatus=Status.findById(Status.class,Long.parseLong(strStatus));
					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						resolutions=Resolution.findAllByStatus(session,deviceType,internalStatus,itemsCount,strLocale);
					}else if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)){
						if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							HouseType upperHouse=HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE, strLocale);
							Session upperHouseSession=Session.findSessionByHouseTypeSessionTypeYear(upperHouse, sessionType, sessionYear);
							List<Resolution> lowerHouseResolutions=Resolution.findAllByStatus(session, deviceType, internalStatus, itemsCount, strLocale);
							List<Resolution> upperHouseResolutions=Resolution.findAllByStatus(upperHouseSession, deviceType, internalStatus, itemsCount, strLocale);
							resolutions.addAll(lowerHouseResolutions);
							resolutions.addAll(upperHouseResolutions);
						}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
							HouseType lowerHouse=HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE, strLocale);
							Session lowerHouseSession=Session.findSessionByHouseTypeSessionTypeYear(lowerHouse, sessionType, sessionYear);
							List<Resolution> upperHouseResolutions=Resolution.findAllByStatus(session, deviceType, internalStatus, itemsCount, strLocale);
							List<Resolution> lowerHouseResolutions=Resolution.findAllByStatus(lowerHouseSession, deviceType, internalStatus, itemsCount, strLocale);
							resolutions.addAll(upperHouseResolutions);
							resolutions.addAll(lowerHouseResolutions);						
						}
					}
				}
				model.addAttribute("hType", houseType.getType());
				model.addAttribute("resolutions",resolutions);
				if(resolutions!=null&&!resolutions.isEmpty()){
					model.addAttribute("resolutionId",resolutions.get(0).getId());
				}
			}catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
		}
	}
	
	@RequestMapping(value="/generatekaryavalireport" ,method=RequestMethod.GET)
	public @ResponseBody void generateKaryavaliReport(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model){
		File reportFile = null; 
		
		String strHouseType=request.getParameter("houseType");
	    String strSessionType=request.getParameter("sessionType");
	    String strSessionYear=request.getParameter("sessionYear");	    
	    String strDeviceType=request.getParameter("deviceType");
	    String reportFormat=request.getParameter("outputFormat");
	    if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null && reportFormat!=null){
	    	if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty() && !reportFormat.isEmpty()) {
	    		HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
	            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
	            Integer sessionYear=Integer.parseInt(strSessionYear);
	            Session session = null;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e1) {
					model.addAttribute("error", e1.getParameter());
					e1.printStackTrace();
				}
	            
	            DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
	            
	            List<ChartVO> chartVOs= new ArrayList<ChartVO>();
	            try{
	            	/*Chart chart = new Chart(session, deviceType, locale.toString());	*/
	            	chartVOs = Resolution.getAdmittedResolutions(session,deviceType,locale.toString());
	            }catch (ELSException e) {
					model.addAttribute(this.getClass().getName(), e.getParameter());
				}
	            if(chartVOs == null) {
	            	try {
						//response.sendError(404, "Report cannot be generated at this stage.");
	            		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
	            		if(message != null) {
	            			if(!message.getValue().isEmpty()) {
	            				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	            			} else {
	            				response.getWriter().println("<h3>No Resolution is admitted yet.<br/>So Karyavali Report cannot be generated.</h3>");
	            			}
	            		} else {
	            			response.getWriter().println("<h3>No Resolution is admitted yet.<br/>So Karyavali Report cannot be generated.</h3>");
	            		}
	            		
						return;
					} catch (IOException e) {						
						e.printStackTrace();
					}
	            }
	            if(chartVOs.isEmpty()) {
	            	try {
						//response.sendError(404, "Report cannot be generated at this stage.");
	            		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "resolution.karyavaliReport.noDataFound", locale.toString());
	            		if(message != null) {
	            			if(!message.getValue().isEmpty()) {	            				
	            				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	            			} else {
	            				response.getWriter().println("<h3>No Resolution is admitted yet.<br/>So Karyavali Report cannot be generated.</h3>");
	            			}
	            		} else {
	            			response.getWriter().println("<h3>No Resolution is admitted yet.<br/>So Karyavali Report cannot be generated.</h3>");
	            		}
	            		
						return;
					} catch (IOException e) {						
						e.printStackTrace();
					}
	            }	            
	            for(ChartVO i : chartVOs) {
	            	System.out.println("resolutions of member: " + i.getMemberName());	            	
	            	for(DeviceVO j : i.getDeviceVOs()) {
	            		System.out.println("number: " + j.getNumber());
	            		System.out.println("content: " + j.getContent());
	            		System.out.println();
	            	}
	            }
	            
	            ResolutionXmlVO data = new ResolutionXmlVO();	  
	            data.setHouseType(houseType.getType());
	            data.setResolutionList(chartVOs);
	            
	            //generate report
        		try {
					reportFile = generateReportUsingFOP(data, "template_karyavalireport", reportFormat, "resolution_karyavali", locale.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
        		System.out.println("Resolution Karyavali Report generated successfully in " + reportFormat + " format!");
        		
        		openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
	    	} else{
				logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
	    } else{
			logger.error("**** Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'houseType,sessionType,sessionYear,deviceType,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}	    
	}	
	
	@RequestMapping(value = "filing/{id}/{file}/enter", method = RequestMethod.GET)
	public @ResponseBody String filing(@PathVariable("id") Long id, @PathVariable("file") Integer file, HttpServletRequest request, Locale locale){
		
		String retVal = "FAILURE";
		
		try{
			Resolution sm = Resolution.findById(Resolution.class, id);
			
			if(sm != null){
				
				sm.setFile(file);
				sm.simpleMerge();
				retVal = "SUCCESS";
			}			
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
}
