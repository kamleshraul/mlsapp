package org.mkcl.els.controller.bis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.util.RomanNumeral;
import org.mkcl.els.common.vo.ActSearchVO;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.OrdinanceSearchVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Act;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillDraft;
import org.mkcl.els.domain.BillKind;
import org.mkcl.els.domain.BillType;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.File;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.LapsedEntity;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.PrintRequisition;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.TextDraft;
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
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("bill")
public class BillController extends GenericController<Bill> {
	
	@Autowired
	private IProcessService processService;
	
	@Override
	protected void populateModule(final ModelMap model, final HttpServletRequest request,
			final String locale, final AuthUser currentUser) {
		DeviceType deviceType=DeviceType.findByFieldName(DeviceType.class, "type",request.getParameter("type"), locale);
		if(deviceType!=null){
			/**** Device Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			try {
				deviceTypes = DeviceType.findDeviceTypesStartingWith("bills", locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addAttribute("deviceTypes", deviceTypes);			
			model.addAttribute("deviceType",deviceType.getId());
			//Access Control Based on device Type
			model.addAttribute("deviceTypeType",deviceType.getType());
			/**** House Types ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType = this.getCurrentUser().getHouseType();
			if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				houseType = ApplicationConstants.LOWER_HOUSE;
			}
			model.addAttribute("houseType",houseType);			
			/**** Latest Session of a House Type ****/
			HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
			Session lastSessionCreated=null;
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(lastSessionCreated.getId()!=null){
				/**** Session Types ****/
				List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
				model.addAttribute("sessionTypes",sessionTypes);	
				model.addAttribute("sessionType",lastSessionCreated.getType().getId());			
				/**** Years ****/
				CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
				List<Integer> years=new ArrayList<Integer>();
				if(houseFormationYear!=null){
					Integer year=lastSessionCreated.getYear();
					Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
					for(int i=year;i>=formationYear;i--){
						years.add(i);
					}
					model.addAttribute("years",years);
					model.addAttribute("sessionYear",year);	
				} else{
					model.addAttribute("errorcode", "houseformationyearnotset");
				}
				/**** Custom Parameter To Determine The Usergroup and usergrouptype of bill users ****/			
				List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
				String userGroupType=null;
				if(userGroups!=null){
					if(!userGroups.isEmpty()){
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"BIS_ALLOWED_USERGROUPTYPES", "");
						if(customParameter!=null){
							String allowedUserGroups=customParameter.getValue(); 
							for(UserGroup i:userGroups){
								if(allowedUserGroups.contains(i.getUserGroupType().getType())){
									/**** Authenticated User's usergroup and usergroupType ****/
									model.addAttribute("usergroup",i.getId());
									userGroupType=i.getUserGroupType().getType();
									model.addAttribute("usergroupType",userGroupType);
									/**** Bill Status Allowed ****/
									CustomParameter allowedStatus=CustomParameter.findByName(CustomParameter.class,"BILL_GRID_STATUS_ALLOWED_"+userGroupType.toUpperCase(), "");
									List<Status> status=new ArrayList<Status>();
									if(allowedStatus!=null){
										try {
											status=Status.findStatusContainedIn(allowedStatus.getValue(),locale);
										} catch (ELSException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}else{
										CustomParameter defaultAllowedStatus=CustomParameter.findByName(CustomParameter.class,"BILL_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
										if(defaultAllowedStatus!=null){
											try {
												status=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
											} catch (ELSException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}else{
											model.addAttribute("errorcode","bill_status_allowed_by_default_not_set");
										}
									}
									model.addAttribute("status",status);
									/**** translation status filter in case of assistant processing ****/
									if(userGroupType.equals(ApplicationConstants.ASSISTANT)) {
										CustomParameter translationStatusesParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSLATION_FILTER_STATUSES", "");
										if(translationStatusesParameter!=null) {
											List<Status> translationStatuses = new ArrayList<Status>();
											try {
												translationStatuses = Status.findStatusContainedIn(translationStatusesParameter.getValue(),locale);
											} catch (ELSException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											model.addAttribute("translationStatuses", translationStatuses);
										}										
									}
									break;
								}
							}
						}else{
							model.addAttribute("errorcode","bis_allowed_usergroups_notset");
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
					}else if(i.getType().contains("BIS_CLERK")){
						model.addAttribute("role",i.getType());
						break;
					}else if(i.getType().startsWith("BIS_")){
						model.addAttribute("role",i.getType());
						break;
					}
				}
				model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());				
			} else{
				model.addAttribute("errorcode","nosessionentriesfound");
			}			
		} else {
			model.addAttribute("errorcode","workunderprogress");
		}
	}
	
	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** For Clerk and other ROIS roles assistant grid is visible ****/
		String role=request.getParameter("role");
		String newUrlPattern=urlPattern;
		if(role.contains("BIS_")&& (!role.contains("CLERK"))){	
			newUrlPattern=newUrlPattern+"?usergroup=assistant";
//			Status underConsiderationStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, locale);
//			String currentSelectedStatus = request.getParameter("status");
//			if(currentSelectedStatus!=null&&underConsiderationStatus!=null) {
//				if(currentSelectedStatus.equals(underConsiderationStatus.getId().toString())) {
//					newUrlPattern=newUrlPattern+"&internalStatus="+underConsiderationStatus.getType();
//				}
//			}			
		}else if(role.contains("BIS_")&& (role.contains("CLERK"))){
			newUrlPattern=newUrlPattern+"?usergroup=clerk";
		}
		return newUrlPattern;
	}
	
	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, final ModelMap model, final String string) {
		/**** Member and Clerk can only create new bills ****/
		String role=request.getParameter("role");		
		if(role!=null){
			if(!role.isEmpty()){
				if(role.startsWith("MEMBER_")||role.contains("BIS_CLERK")){
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
		/**** for Member and Clerk edit page is displayed ****/
		/**** for assistant assistant page ****/
		/**** for other ris usergroupTypes editreadonly page ****/
		Set<Role> roles=this.getCurrentUser().getRoles();
		for(Role i:roles){
			if(i.getType().startsWith("MEMBER_")||i.getType().contains("CLERK")){
				return newUrlPattern;
			}else if(i.getType().contains("ASSISTANT")||i.getType().contains("SECTION_OFFICER")){
				return newUrlPattern.replace("edit","assistant");
			}else if(i.getType().startsWith("ROIS_")){
				return newUrlPattern.replace("edit","editreadonly");
			}
		}	
		/**** for others permission denied ****/
		model.addAttribute("errorcode","permissiondenied");
		return "bill/error";
	}
	
	@Override
	protected void populateNew(final ModelMap model, final Bill domain,
            final String locale, final HttpServletRequest request) {
		/**** Locale ****/
		domain.setLocale(locale);
		/**** Device Type ****/
		String selectedDeviceType=request.getParameter("deviceType");
		if(selectedDeviceType==null){
			selectedDeviceType=request.getParameter("type");
		}
		DeviceType deviceType=null;
		if(selectedDeviceType!=null){
			if(!selectedDeviceType.isEmpty()){
				deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(selectedDeviceType));
				model.addAttribute("formattedDeviceTypeForBill", deviceType.getName());
				model.addAttribute("deviceTypeForBill", deviceType.getId());
				model.addAttribute("selectedDeviceTypeForBill", deviceType.getType());
			}else{
				logger.error("**** Check request parameter 'DeviceType' for no value ****");
				model.addAttribute("errorcode","deviceType_isempty");		
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
		if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)) {
			/**** House Types for selecting houseType where bill will be introduced first ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			model.addAttribute("introducingHouseTypes", houseTypes);
			if(domain.getIntroducingHouseType()!=null) {
				model.addAttribute("selectedIntroducingHouseType",domain.getIntroducingHouseType().getId());
			} else {
				model.addAttribute("selectedIntroducingHouseType",houseType.getId());
			}			
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
			model.addAttribute("errorcode","sessionyear_isnull");
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
		/**** Bill Type ****/
		List<BillType> billTypes = BillType.findAll(BillType.class, "name", ApplicationConstants.ASC, locale);
		if(billTypes!=null && !billTypes.isEmpty()) {			
			List<BillType> billTypesAllowed = new ArrayList<BillType>();
			String billTypesAllowedForSession = selectedSession.getParameter(deviceType.getType() + "_billTypesAllowed");
			if(billTypesAllowedForSession != null && !billTypesAllowedForSession.isEmpty()) {
				for(BillType billType : billTypes) {
					for(String billTypeAllowedForSession : billTypesAllowedForSession.split("#")) {
						if(billType.getType().equals(billTypeAllowedForSession)) {
							billTypesAllowed.add(billType);
							break;
						}
					}
				}				
			} else {
				logger.error("**** Session Parameter '" + deviceType.getType() + "_billTypesAllowed' is not set. ****");
				model.addAttribute("errorcode",deviceType.getType() + "_billTypesAllowed_notset");
				return;
			}
			model.addAttribute("billTypes", billTypesAllowed);
			if(domain.getBillType()!=null) {
				model.addAttribute("selectedBillType", domain.getBillType().getId());
				model.addAttribute("typeOfSelectedBillType", domain.getBillType().getType());
			}			
		} else {
			logger.error("**** Bill types are not defined. ****");
			model.addAttribute("errorcode","billType_notfound");
			return;
		}
		/**** Bill Kind ****/
		List<BillKind> billKinds = BillKind.findAll(BillKind.class, "name", ApplicationConstants.ASC, locale);
		if(billKinds!=null && !billKinds.isEmpty()) {
			List<BillKind> billKindsAllowed = new ArrayList<BillKind>();
			String billKindsAllowedForSession = selectedSession.getParameter(deviceType.getType() + "_billKindsAllowed");
			if(billKindsAllowedForSession != null && !billKindsAllowedForSession.isEmpty()) {
				for(BillKind billKind : billKinds) {
					for(String billKindAllowedForSession : billKindsAllowedForSession.split("#")) {
						if(billKind.getType().equals(billKindAllowedForSession)) {
							billKindsAllowed.add(billKind);
							break;
						}
					}
				}				
			} else {
				logger.error("**** Session Parameter '" + deviceType.getType() + "_billKindsAllowed' is not set. ****");
				model.addAttribute("errorcode",deviceType.getType() + "_billKindsAllowed_notset");
				return;
			}
			model.addAttribute("billKinds", billKindsAllowed);
			if(domain.getBillKind()!=null) {
				model.addAttribute("selectedBillKind", domain.getBillKind().getId());
			}						
		} else {
			logger.error("**** Bill kinds are not defined. ****");
			model.addAttribute("errorcode","billKind_notfound");
		}
		/**** titles, content drafts, 'statement of object and reason' drafts & memorandum drafts ****/	
		String defaultBillLanguage = selectedSession.getParameter(deviceType.getType()+"_defaultTitleLanguage");
		model.addAttribute("defaultBillLanguage", defaultBillLanguage);
		boolean isSuccessful = populateAllTypesOfDrafts(model, domain, selectedSession, deviceType);
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
						if(deviceType.getType().trim().equals(ApplicationConstants.NONOFFICIAL_BILL)){					
							List<Ministry> ministries = new ArrayList<Ministry>();
							try {
								ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							model.addAttribute("ministries",ministries);
							Ministry ministry=domain.getMinistry();
							if(ministry!=null){
								model.addAttribute("ministrySelected",ministry.getId());						
								/**** Sub Departments ****/
								List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,locale);
								model.addAttribute("subDepartments",subDepartments);
								SubDepartment subDepartment=domain.getSubDepartment();
								if(subDepartment!=null){
									model.addAttribute("subDepartmentSelected",subDepartment.getId());
								}
							}							
						}else if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)){
							if(role.startsWith("MEMBER")){
								List<MemberMinister> memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
								List<Ministry> assignedMinistries=new ArrayList<Ministry>();								
								for(MemberMinister i:memberMinisters){
									assignedMinistries.add(i.getMinistry());						
								}
								//setting first member ministry as selected ministry by default
								if(!assignedMinistries.isEmpty()) {
									domain.setMinistry(assignedMinistries.get(0));
								}								
							model.addAttribute("ministries",assignedMinistries);
						}else{
							List<Ministry> ministries = new ArrayList<Ministry>();
							try {
								ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
							model.addAttribute("ministries",ministries);
						}
							
						Ministry ministry=domain.getMinistry();
						if(ministry!=null){
							model.addAttribute("ministrySelected",ministry.getId());
							List<SubDepartment> assignedSubDepartments = MemberMinister.findAssignedSubDepartments(ministry,locale);
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
		/**** Referred Act for Amendment Bill ****/
		if(domain.getBillType()!=null) {
			if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
				Act referredAct = domain.getReferredAct();
				referredAct = Act.findById(Act.class, new Long(1));
				if(referredAct!=null) {
					model.addAttribute("referredAct", referredAct.getId());
					model.addAttribute("referredActNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredAct.getNumber()));
					model.addAttribute("referredActYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredAct.getYear()));
				}
			}
		}		
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final Bill domain,
			final HttpServletRequest request) {		
		/**** Locale ****/
		String locale=domain.getLocale();
		/**** Device Type ****/
		DeviceType deviceType = domain.getType();
		if(deviceType==null) {
			logger.error("devicetype is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "devicetype_null");			
			return;
		}
		model.addAttribute("formattedDeviceTypeForBill", deviceType.getName());
		model.addAttribute("deviceTypeForBill", deviceType.getId());
		model.addAttribute("selectedDeviceTypeForBill", deviceType.getType());
		/**** Original Device Type ****/		
		if(domain.getOriginalType()!=null) {
			model.addAttribute("originalDeviceType",domain.getOriginalType().getId());
		}
		/**** House Type ****/
		HouseType houseType=domain.getHouseType();
		if(houseType==null) {
			logger.error("housetype is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "housetype_null");
			return;
		}
		model.addAttribute("formattedHouseType",houseType.getName());
		model.addAttribute("houseType",houseType.getId());
		model.addAttribute("houseTypeType",houseType.getType());
		if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)) {
			/**** House Types for selecting houseType where bill will be introduced first ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			model.addAttribute("introducingHouseTypes", houseTypes);	
			if(domain.getIntroducingHouseType()!=null) {
				model.addAttribute("selectedIntroducingHouseType",domain.getIntroducingHouseType().getId());
			}			
		}
		/**** Current House Type ****/
		System.out.println(request.getParameter("currentHouseType"));
		HouseType selectedHouseType = null;
		String selectedHouseTypeType = request.getParameter("houseType");
		if(selectedHouseTypeType!=null) {
			selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", selectedHouseTypeType, domain.getLocale());
		} else {
			selectedHouseType = domain.getCurrentHouseType();
		}
		if(selectedHouseType!=null) {
			domain.setCurrentHouseType(selectedHouseType);
			model.addAttribute("currentHouseType", selectedHouseType.getId());
		}
		
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
		/**** Bill Type ****/
		List<BillType> billTypes = BillType.findAll(BillType.class, "name", ApplicationConstants.ASC, locale);
		if(billTypes!=null && !billTypes.isEmpty()) {			
			List<BillType> billTypesAllowed = new ArrayList<BillType>();
			String billTypesAllowedForSession = selectedSession.getParameter(deviceType.getType() + "_billTypesAllowed");
			if(billTypesAllowedForSession != null && !billTypesAllowedForSession.isEmpty()) {
				for(BillType billType : billTypes) {
					for(String billTypeAllowedForSession : billTypesAllowedForSession.split("#")) {
						if(billType.getType().equals(billTypeAllowedForSession)) {
							billTypesAllowed.add(billType);
							break;
						}
					}
				}				
			} else {
				logger.error("**** Session Parameter '" + deviceType.getType() + "_billTypesAllowed' is not set. ****");
				model.addAttribute("errorcode",deviceType.getType() + "_billTypesAllowed_notset");
				return;
			}
			model.addAttribute("billTypes", billTypesAllowed);
			if(domain.getBillType()!=null) {
				model.addAttribute("selectedBillType", domain.getBillType().getId());		
				model.addAttribute("typeOfSelectedBillType", domain.getBillType().getType());
			}			
		} else {
			logger.error("**** Bill types are not defined. ****");
			model.addAttribute("errorcode","billType_notfound");
			return;
		}
		/**** Bill Kind ****/
		List<BillKind> billKinds = BillKind.findAll(BillKind.class, "name", ApplicationConstants.ASC, locale);
		if(billKinds!=null && !billKinds.isEmpty()) {
			List<BillKind> billKindsAllowed = new ArrayList<BillKind>();
			String billKindsAllowedForSession = selectedSession.getParameter(deviceType.getType() + "_billKindsAllowed");
			if(billKindsAllowedForSession != null && !billKindsAllowedForSession.isEmpty()) {
				for(BillKind billKind : billKinds) {
					for(String billKindAllowedForSession : billKindsAllowedForSession.split("#")) {
						if(billKind.getType().equals(billKindAllowedForSession)) {
							billKindsAllowed.add(billKind);
							break;
						}
					}
				}				
			} else {
				logger.error("**** Session Parameter '" + deviceType.getType() + "_billKindsAllowed' is not set. ****");
				model.addAttribute("errorcode",deviceType.getType() + "_billKindsAllowed_notset");
				return;
			}
			model.addAttribute("billKinds", billKindsAllowed);
			if(domain.getBillKind()!=null) {
				model.addAttribute("selectedBillKind", domain.getBillKind().getId());
			}						
		} else {
			logger.error("**** Bill kinds are not defined. ****");
			model.addAttribute("errorcode","billKind_notfound");
		}
		/**** Referred Act for Amendment Bill ****/
		if(domain.getBillType()!=null) {
			if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
				Act referredAct = domain.getReferredAct();
				if(referredAct!=null) {
					model.addAttribute("referredAct", referredAct.getId());
					model.addAttribute("referredActNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredAct.getNumber()));
					model.addAttribute("referredActYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredAct.getYear()));
				}
			}
		}		
		/**** titles, content drafts, 'statement of object and reason' drafts, memorandum drafts, annexures ****/			
		boolean isSuccessful = populateAllTypesOfDrafts(model, domain, selectedSession, deviceType);
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
		//=================== Member related things ==================/
		String memberNames=null;
		/**** Primary Member ****/		
		String primaryMemberName=null;
		Member member=domain.getPrimaryMember();
		if(member==null) {
			logger.error("member is not set for this bill having id="+domain.getId()+".");
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
		if(selectedSession.getHouse()==null) {
			logger.error("house is not set for session of this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "house_null");
			return;
		}
		Long houseId=selectedSession.getHouse().getId();
		MasterVO constituency=null;
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
			constituency=Member.findConstituencyByAssemblyId(member.getId(), houseId);
			
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			Date currentDate=new Date();
			String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
			constituency=Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
		}
		if(constituency==null) {
			logger.error("constituency is not set for member of this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "constituency_null");
			return;
		}
		model.addAttribute("constituency",constituency.getName());
		/**** Ministries ****/
		String strRotationOrderPubDate = selectedSession.getParameter("questions_starred_rotationOrderPublishingDate");
		if(strRotationOrderPubDate==null) {
			logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
			model.addAttribute("errorcode", "rotationorderpubdate_notset");
			return;
		}
		if(strRotationOrderPubDate.isEmpty()) {
			logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
			model.addAttribute("errorcode", "rotationorderpubdate_notset");
			return;
		}
		Date rotationOrderPubDate=null;
		CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");		
		try {
			rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
			model.addAttribute("rotationOrderPublishDate", FormaterUtil.getDateFormatter(locale).format(rotationOrderPubDate));
			Date currentDate=new Date();
			if(currentDate.equals(rotationOrderPubDate)||currentDate.after(rotationOrderPubDate)){
				if(deviceType.getType().trim().equals(ApplicationConstants.NONOFFICIAL_BILL)){					
					List<Ministry> ministries = new ArrayList<Ministry>();
					try {
						ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					model.addAttribute("ministries",ministries);
					Ministry ministry=domain.getMinistry();
					if(ministry!=null){
						model.addAttribute("ministrySelected",ministry.getId());						
						/**** Sub Departments ****/
						List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,locale);
						model.addAttribute("subDepartments",subDepartments);
						SubDepartment subDepartment=domain.getSubDepartment();
						if(subDepartment!=null){
							model.addAttribute("subDepartmentSelected",subDepartment.getId());
						}
					}							
				} else if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)){
					if(role.startsWith("MEMBER")){
						List<MemberMinister> memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
						/**** To check whether to populate other ministries also for this minister ****/
						Boolean isAllowedToAccessOtherMinistries = false;
						CustomParameter rolesAllowedForAccessingOtherMinistriesParameter = CustomParameter.findByName(CustomParameter.class, "MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_BILL", "");
						if(rolesAllowedForAccessingOtherMinistriesParameter != null) {
							if(rolesAllowedForAccessingOtherMinistriesParameter.getValue() != null && !rolesAllowedForAccessingOtherMinistriesParameter.getValue().isEmpty()) {
								List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(member, selectedSession, locale);
								for(MemberRole memberRole: memberRoles) {
									for(String allowedRole: rolesAllowedForAccessingOtherMinistriesParameter.getValue().split("#")) {
										if(memberRole.getName().trim().equals(allowedRole)) {
											isAllowedToAccessOtherMinistries = true;
											break;
										}
									}
									if(isAllowedToAccessOtherMinistries == true) {
										break;
									}
								}
							} else {
								logger.error("custom parameter 'MEMBERROLES_SUBMISSIONFORANYMINISTRY_IN_GOVERNMENT_BILL' is not set properly");
								model.addAttribute("errorcode", "memberroles_submissionforanyministry_in_government_bill_notset");
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
							try {
								otherMinistries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
							} catch (ELSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} //Ministry.findAssignedMinistries(locale);
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
						List<Ministry> ministries = new ArrayList<Ministry>();
						try {
							ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						model.addAttribute("ministries",ministries);
					}
						
					Ministry ministry=domain.getMinistry();
					if(ministry!=null){
						model.addAttribute("ministrySelected",ministry.getId());
						List<SubDepartment> assignedSubDepartments = MemberMinister.findAssignedSubDepartments(ministry,locale);
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
		if(domain.getDateOfOpinionSoughtFromLawAndJD()!=null){
			model.addAttribute("dateOfOpinionSoughtFromLawAndJD",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getDateOfOpinionSoughtFromLawAndJD()));
			model.addAttribute("formattedDateOfOpinionSoughtFromLawAndJD",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getDateOfOpinionSoughtFromLawAndJD()));
		}
		if(domain.getDateOfRecommendationFromGovernor()!=null){
			model.addAttribute("dateOfRecommendationFromGovernor",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getDateOfRecommendationFromGovernor()));
			model.addAttribute("formattedDateOfRecommendationFromGovernor",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getDateOfRecommendationFromGovernor()));
		}
		if(domain.getDateOfRecommendationFromPresident()!=null){
			model.addAttribute("dateOfRecommendationFromPresident",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getDateOfRecommendationFromPresident()));
			model.addAttribute("formattedDateOfRecommendationFromPresident",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),locale).format(domain.getDateOfRecommendationFromPresident()));
		}
		if(domain.getWorkflowStartedOn()!=null){
			model.addAttribute("workflowStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowStartedOn()));
		}
		if(domain.getWorkflowForTranslationStartedOn()!=null){
			model.addAttribute("workflowForTranslationStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowForTranslationStartedOn()));
		}
		if(domain.getWorkflowForOpinionFromLawAndJDStartedOn()!=null){
			model.addAttribute("workflowForOpinionFromLawAndJDStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowForOpinionFromLawAndJDStartedOn()));
		}
		if(domain.getWorkflowForRecommendationFromGovernorStartedOn()!=null){
			model.addAttribute("workflowForRecommendationFromGovernorStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowForRecommendationFromGovernorStartedOn()));
		}
		if(domain.getWorkflowForRecommendationFromPresidentStartedOn()!=null){
			model.addAttribute("workflowForRecommendationFromPresidentStartedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getWorkflowForRecommendationFromPresidentStartedOn()));
		}
		if(domain.getTaskReceivedOn()!=null){
			model.addAttribute("taskReceivedOnDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOn()));
		}
		if(domain.getTaskReceivedOnForTranslation()!=null){
			model.addAttribute("taskReceivedOnDateForTranslation",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnForTranslation()));
		}
		if(domain.getTaskReceivedOnForOpinionFromLawAndJD()!=null){
			model.addAttribute("taskReceivedOnDateForOpinionFromLawAndJD",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnForOpinionFromLawAndJD()));
		}
		if(domain.getTaskReceivedOnForRecommendationFromGovernor()!=null){
			model.addAttribute("taskReceivedOnDateForRecommendationFromGovernor",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnForRecommendationFromGovernor()));
		}
		if(domain.getTaskReceivedOnForRecommendationFromPresident()!=null){
			model.addAttribute("taskReceivedOnDateForRecommendationFromPresident",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"en_US").format(domain.getTaskReceivedOnForRecommendationFromPresident()));
		}
		/**** Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy",domain.getCreatedBy());
		/**** Status,Internal Status and recommendation Status ****/
		Status status=domain.getStatus();
		Status internalStatus=domain.getInternalStatus();
		Status recommendationStatus=domain.getRecommendationStatus();
		if(status==null) {
			logger.error("status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "status_null");
			return;
		}
		model.addAttribute("status",status.getId());
		model.addAttribute("memberStatusType",status.getType());
		if(internalStatus==null) {
			logger.error("internal status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "internalStatus_null");
			return;
		}
		model.addAttribute("internalStatus",internalStatus.getId());
		model.addAttribute("internalStatusType", internalStatus.getType());
		model.addAttribute("internalStatusPriority", internalStatus.getPriority());
		model.addAttribute("formattedInternalStatus", internalStatus.getName());
		/**** in case of assistant and other approving BIS actors ****/
		if(usergroupType!=null&&!(usergroupType.isEmpty())&&
				(usergroupType.equals(ApplicationConstants.ASSISTANT)||usergroupType.equals(ApplicationConstants.SECTION_OFFICER))){
			/**** list of put up options available ****/
			populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
			if(domain.getWorkflowStarted()==null){
				domain.setWorkflowStarted("NO");
			}else if(domain.getWorkflowStarted().isEmpty()){
				domain.setWorkflowStarted("NO");
			}
			if(domain.getTranslationWorkflowStarted()==null){
				domain.setTranslationWorkflowStarted("NO");
			}else if(domain.getTranslationWorkflowStarted().isEmpty()){
				domain.setTranslationWorkflowStarted("NO");
			}
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				if(domain.getOpinionFromLawAndJDWorkflowStarted()==null){
					domain.setOpinionFromLawAndJDWorkflowStarted("NO");
				}else if(domain.getOpinionFromLawAndJDWorkflowStarted().isEmpty()){
					domain.setOpinionFromLawAndJDWorkflowStarted("NO");
				}
			}
			if(domain.getRecommendationFromGovernorWorkflowStarted()==null){
				domain.setRecommendationFromGovernorWorkflowStarted("NO");
			}else if(domain.getRecommendationFromGovernorWorkflowStarted().isEmpty()){
				domain.setRecommendationFromGovernorWorkflowStarted("NO");
			}
			if(domain.getRecommendationFromPresidentWorkflowStarted()==null){
				domain.setRecommendationFromPresidentWorkflowStarted("NO");
			}else if(domain.getRecommendationFromPresidentWorkflowStarted().isEmpty()){
				domain.setRecommendationFromPresidentWorkflowStarted("NO");
			}
			if(domain.getEndFlag()==null){
				domain.setEndFlag("continue");
			}else if(domain.getEndFlag().isEmpty()){
				domain.setEndFlag("continue");
			}
			if(domain.getEndFlagForTranslation()==null){
				domain.setEndFlagForTranslation("continue");
			}else if(domain.getEndFlagForTranslation().isEmpty()){
				domain.setEndFlagForTranslation("continue");
			}
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				if(domain.getEndFlagForOpinionFromLawAndJD()==null){
					domain.setEndFlagForOpinionFromLawAndJD("continue");
				}else if(domain.getEndFlagForOpinionFromLawAndJD().isEmpty()){
					domain.setEndFlagForOpinionFromLawAndJD("continue");
				}
			}	
			if(domain.getEndFlagForRecommendationFromGovernor()==null){
				domain.setEndFlagForRecommendationFromGovernor("continue");
			}else if(domain.getEndFlagForRecommendationFromGovernor().isEmpty()){
				domain.setEndFlagForRecommendationFromGovernor("continue");
			}
			if(domain.getEndFlagForRecommendationFromPresident()==null){
				domain.setEndFlagForRecommendationFromPresident("continue");
			}else if(domain.getEndFlagForRecommendationFromPresident().isEmpty()){
				domain.setEndFlagForRecommendationFromPresident("continue");
			}
			if(domain.getLevel()==null){
				domain.setLevel("1");
			}else if(domain.getLevel().isEmpty()){
				domain.setLevel("1");
			}
			if(domain.getLevelForTranslation()==null){
				domain.setLevelForTranslation("1");
			}else if(domain.getLevelForTranslation().isEmpty()){
				domain.setLevelForTranslation("1");
			}
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				if(domain.getLevelForOpinionFromLawAndJD()==null){
					domain.setLevelForOpinionFromLawAndJD("1");
				}else if(domain.getLevelForOpinionFromLawAndJD().isEmpty()){
					domain.setLevelForOpinionFromLawAndJD("1");
				}
			}
			if(domain.getLevelForRecommendationFromGovernor()==null){
				domain.setLevelForRecommendationFromGovernor("1");
			}else if(domain.getLevelForRecommendationFromGovernor().isEmpty()){
				domain.setLevelForRecommendationFromGovernor("1");
			}
			if(domain.getLevelForRecommendationFromPresident()==null){
				domain.setLevelForRecommendationFromPresident("1");
			}else if(domain.getLevelForRecommendationFromPresident().isEmpty()){
				domain.setLevelForRecommendationFromPresident("1");
			}			
			/**** Referencing & Clubbing For Non-Official Bill ****/
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				/**** Referenced Bill ****/
				List<Reference> refentities=new ArrayList<Reference>();
				List<String> refentitiesSessionDevice = new ArrayList<String>();
				if(domain.getReferencedBill() != null){					
					ReferencedEntity refEntity = domain.getReferencedBill();				
					Reference reference=new Reference();
					reference.setId(String.valueOf(refEntity.getId()));
					Bill refBill = (Bill)refEntity.getDevice();
					if(refBill.getNumber()!=null) {
						reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(refBill.getNumber()));
					}					
					reference.setNumber(String.valueOf(refBill.getId()));
					refentities.add(reference);
					
					Session referencedBillSession = refBill.getSession();
					refentitiesSessionDevice.add("[" + referencedBillSession.getType().getSessionType()+", "+
							FormaterUtil.formatNumberNoGrouping(referencedBillSession.getYear(), locale) + "], " + 
							refBill.getType().getName());
							
					model.addAttribute("referencedBills",refentities);
					model.addAttribute("referencedBill", refEntity.getId());
					model.addAttribute("referencedBillsSessionAndDevice", refentitiesSessionDevice);
					
				}
				/**** Clubbed Bills are collected in references ****/		
				List<Reference> references=new ArrayList<Reference>();
				List<Reference> referencesToShow=new ArrayList<Reference>();
				List<ClubbedEntity> clubbedEntities=Bill.findClubbedEntitiesByPosition(domain);
				StringBuffer buffer1=new StringBuffer();
				buffer1.append(memberNames+",");	
				if(clubbedEntities!=null){
					for(ClubbedEntity ce:clubbedEntities){
						Reference reference=new Reference();
						reference.setId(String.valueOf(ce.getId()));
						if(ce.getBill().getNumber()==null) {
							reference.setName("click to see");
						} else {
							reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getBill().getNumber()));
						}						
						reference.setNumber(String.valueOf(ce.getBill().getId()));
						references.add(reference);
						/** show only those clubbed bills which are not in state of
						 * (processed to be putup for nameclubbing, putup for nameclubbing, pending for nameclubbing approval) 
						 **/
						if(ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_CLUBBED)
								|| ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
							String tempPrimary=ce.getBill().getPrimaryMember().getFullname();
							if(!buffer1.toString().contains(tempPrimary)){
								buffer1.append(ce.getBill().getPrimaryMember().getFullname()+",");
							}
							List<SupportingMember> clubbedSupportingMember=ce.getBill().getSupportingMembers();
							if(clubbedSupportingMember!=null){
								if(!clubbedSupportingMember.isEmpty()){
									for(SupportingMember l:clubbedSupportingMember){
										String tempSupporting=l.getMember().getFullname();
										if(!buffer1.toString().contains(tempSupporting)){
											buffer1.append(tempSupporting+",");
										}
									}
								}
							}
							referencesToShow.add(reference);
						}						
					}
				}
				if(!buffer1.toString().isEmpty()){
					buffer1.deleteCharAt(buffer1.length()-1);
				}
				String allMembersNames=buffer1.toString();
				model.addAttribute("memberNames",allMembersNames);
				model.addAttribute("clubbedBills",references);
				//in case of assistant, show all so that he can unclub any clubbed entity
				if(usergroupType.equals(ApplicationConstants.ASSISTANT)) {
					model.addAttribute("clubbedBillsToShow",references);
				} else {
					model.addAttribute("clubbedBillsToShow",referencesToShow);
				}				
				if(references.isEmpty()){
					if(domain.getParent()!=null){
						if(domain.getParent().getNumber()!=null) {
							model.addAttribute("formattedParentNumber",FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
						} else {
							model.addAttribute("formattedParentNumber","Click to See");
						}						
						model.addAttribute("parent",domain.getParent().getId());
					}
				}
			}
			/**** Lapsed Bill ****/
			List<Reference> lapsedentities=new ArrayList<Reference>();
			List<String> refentitiesSessionDevice = new ArrayList<String>();
			if(domain.getLapsedBill() != null){					
				LapsedEntity lapsedEntity = domain.getLapsedBill();				
				Reference reference=new Reference();
				reference.setId(String.valueOf(lapsedEntity.getId()));
				Bill refBill = (Bill)lapsedEntity.getDevice();
				if(refBill.getNumber()!=null) {
					reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(refBill.getNumber()));
				}					
				reference.setNumber(String.valueOf(refBill.getId()));
				lapsedentities.add(reference);
				
				Session lapsedBillSession = refBill.getSession();
				refentitiesSessionDevice.add("[" + lapsedBillSession.getType().getSessionType()+", "+
						FormaterUtil.formatNumberNoGrouping(lapsedBillSession.getYear(), locale) + "], " + 
						refBill.getType().getName());
						
				model.addAttribute("lapsedBills",lapsedentities);
				model.addAttribute("lapsedBill", lapsedEntity.getId());
				model.addAttribute("lapsedBillsSessionAndDevice", refentitiesSessionDevice);
				
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
		Status translationStatus = domain.getTranslationStatus();
		if(translationStatus==null) {
			translationStatus = Status.findByType(ApplicationConstants.BILL_TRANSLATION_NOTSEND, locale);	
			domain.setTranslationStatus(translationStatus);
		}
		if(translationStatus!=null) {
			model.addAttribute("translationStatus",translationStatus.getId());
			model.addAttribute("translationStatusType", translationStatus.getType());
			model.addAttribute("formattedTranslationStatus", translationStatus.getName());			
		}
		if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
			Status opinionFromLawAndJDStatus = domain.getOpinionFromLawAndJDStatus();
			if(opinionFromLawAndJDStatus==null) {
				opinionFromLawAndJDStatus = Status.findByType(ApplicationConstants.BILL_OPINION_FROM_LAWANDJD_NOTSEND, locale);
				domain.setOpinionFromLawAndJDStatus(opinionFromLawAndJDStatus);
			}
			
			if(opinionFromLawAndJDStatus!=null) {
				model.addAttribute("opinionFromLawAndJDStatus",opinionFromLawAndJDStatus.getId());
				model.addAttribute("opinionFromLawAndJDStatusType", opinionFromLawAndJDStatus.getType());
				model.addAttribute("formattedOpinionFromLawAndJDStatus", opinionFromLawAndJDStatus.getName());
			}
		}	
		Status recommendationFromGovernorStatus = domain.getRecommendationFromGovernorStatus();
		if(recommendationFromGovernorStatus==null) {
			recommendationFromGovernorStatus = Status.findByType(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND, locale);	
			domain.setRecommendationFromGovernorStatus(recommendationFromGovernorStatus);
		}
		if(recommendationFromGovernorStatus!=null) {
			model.addAttribute("recommendationFromGovernorStatus",recommendationFromGovernorStatus.getId());
			model.addAttribute("recommendationFromGovernorStatusType", recommendationFromGovernorStatus.getType());
			model.addAttribute("formattedRecommendationFromGovernorStatus", recommendationFromGovernorStatus.getName());			
		}
		Status recommendationFromPresidentStatus = domain.getRecommendationFromPresidentStatus();
		if(recommendationFromPresidentStatus==null) {
			recommendationFromPresidentStatus = Status.findByType(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND, locale);	
			domain.setRecommendationFromPresidentStatus(recommendationFromPresidentStatus);
		}
		if(recommendationFromPresidentStatus!=null) {
			model.addAttribute("recommendationFromPresidentStatus",recommendationFromPresidentStatus.getId());
			model.addAttribute("recommendationFromPresidentStatusType", recommendationFromPresidentStatus.getType());
			model.addAttribute("formattedRecommendationFromPresidentStatus", recommendationFromPresidentStatus.getName());			
		}
		/**** Populate for Status Updation Post Admission Workflow End ****/
		Status statusForBeginningStatusUpdation = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED, locale);
		model.addAttribute("statusUpdationPriority", statusForBeginningStatusUpdation.getPriority());
		if(internalStatus.getPriority()>=statusForBeginningStatusUpdation.getPriority()) {
			populateModelForStatusUpdation(model, domain, request, usergroupType);
		}		
		/**** Added by dhananjayb ****/
		/**** Remove unwanted actions for relevant workflows ****/
		if(usergroupType!=null&&!(usergroupType.isEmpty())
				&&(usergroupType.equals(ApplicationConstants.ASSISTANT)||usergroupType.equals(ApplicationConstants.SECTION_OFFICER))){
			List<Status> internalStatuses = (List<Status>) model.get("internalStatuses");
			List<Status> statusesToRemove = new ArrayList<Status>();
			for(Status i: internalStatuses) {
				boolean isCandidateToRemove = false;
				if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_TRANSLATION)
						&& !(domain.getTranslationStatus().getType().equals(ApplicationConstants.BILL_TRANSLATION_NOTSEND))) {
					isCandidateToRemove = true;
				} else if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_OPINION_FROM_LAWANDJD)
						&& domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)
						&& !(domain.getOpinionFromLawAndJDStatus().getType().equals(ApplicationConstants.BILL_OPINION_FROM_LAWANDJD_NOTSEND))) {
					isCandidateToRemove = true;
				} else if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_OPINION_FROM_LAWANDJD)
						&& domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
					isCandidateToRemove = true;
				} else if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_GOVERNOR)
						&& !(domain.getRecommendationFromGovernorStatus().getType().equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND))) {
					isCandidateToRemove = true;
				} else if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_PRESIDENT)
						&& !(domain.getRecommendationFromPresidentStatus().getType().equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND))) {
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
			if(usergroupType!=null&&!usergroupType.isEmpty()&&usergroupType.equals("assistant")
					&&(internalStatusType.equals(ApplicationConstants.BILL_RECOMMEND_ADMISSION)							
							||internalStatusType.equals(ApplicationConstants.BILL_RECOMMEND_REJECTION)							
					)){				
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(usergroup));
				List<Reference> actors=WorkflowConfig.findBillActorsVO(domain, internalStatus, userGroup, 1, locale);
				model.addAttribute("internalStatusSelected",internalStatus.getId());
				model.addAttribute("recommendationStatusSelected",recommendationStatus.getId());
				model.addAttribute("actors",actors);
				if(actors!=null&&!actors.isEmpty()){
					String nextActor=actors.get(0).getId();
					String[] actorArr=nextActor.split("#");
					domain.setLevel(actorArr[2]);
					domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
				}
			}			
		}
		/**** remarks ****/	
		UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", usergroupType, locale);
		if(userGroupType!=null) {
			String currentRemarks = Bill.getLatestRemarksOfActor(domain.getId(), userGroupType.getName(), this.getCurrentUser().getActualUsername(), locale);
			model.addAttribute("currentRemarks", currentRemarks);
		}			
		
		/**** check for already submitted bills pending for to be putup ****/
		Boolean isAnyBillSubmittedEarierThanCurrentBillToBePutup = Bill.isAnyBillSubmittedEarierThanCurrentBillToBePutup(domain);
		model.addAttribute("isAnyBillSubmittedEarierThanCurrentBillToBePutup", isAnyBillSubmittedEarierThanCurrentBillToBePutup);
	}
	
	private void populateModelForStatusUpdation(ModelMap model, Bill domain, HttpServletRequest request, String usergroupType) {
		/**** list of put up options available post under consideration part ****/
		populateRecommendationStatus(model, domain, request, usergroupType);
		/**** House Rounds Available for Status Updation ****/
		CustomParameter billHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
		if(billHouseRoundsParameter!=null) {
			int billHouseRoundsAvailable = Integer.parseInt(billHouseRoundsParameter.getValue());
			List<MasterVO> houseRoundVOs = new ArrayList<MasterVO>();
			for(int i=1; i<=billHouseRoundsAvailable; i++) {
				MasterVO houseRoundVO = new MasterVO();
				houseRoundVO.setName(FormaterUtil.formatNumberNoGrouping(i, domain.getLocale()));
				houseRoundVO.setValue(String.valueOf(i));
				houseRoundVOs.add(houseRoundVO);
			}
			model.addAttribute("houseRoundVOs", houseRoundVOs);
		} else {
			logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
			model.addAttribute("errorcode", "bill_houseorders_notset");
			return;
		}
		/**** voting for ****/
		model.addAttribute("votingForPassingOfBill", ApplicationConstants.VOTING_FOR_PASSING_OF_BILL);
		/**** introduced by ****/
		Member introducedByMember = domain.getIntroducedBy();
		if(introducedByMember==null) {
			introducedByMember = domain.getPrimaryMember();
		}
		model.addAttribute("introducedByMember", introducedByMember);
		/**** expeted status date & status date for current recommendation status ****/
		if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_DEPARTMENTINTIMATED)) {
			model.addAttribute("currentHouseRound", FormaterUtil.formatNumberNoGrouping(1, domain.getLocale()));
			model.addAttribute("currentExpectedStatusDate", FormaterUtil.formatDateToString(domain.getEditedOn(), ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale()));
			model.addAttribute("currentStatusDate", FormaterUtil.formatDateToString(domain.getEditedOn(), ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale()));
		} else {
			BillDraft currentDraft = null;
			if(domain.getHouseRound()!=null) {
				currentDraft = Bill.findDraftByRecommendationStatusAndHouseRound(domain, domain.getRecommendationStatus(), domain.getHouseRound());
			} else {
				currentDraft = Bill.findDraftByRecommendationStatus(domain, domain.getRecommendationStatus());
			}
			int currentHouseRound = currentDraft.getHouseRound();
			model.addAttribute("currentHouseRound", FormaterUtil.formatNumberNoGrouping(currentHouseRound, domain.getLocale()));
			Date currentExpectedStatusDate = currentDraft.getExpectedStatusDate();
			if(currentExpectedStatusDate!=null) {
				model.addAttribute("currentExpectedStatusDate", FormaterUtil.formatDateToString(currentExpectedStatusDate, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale()));	
			}
			Date currentStatusDate = currentDraft.getStatusDate();
			if(currentStatusDate!=null) {
				model.addAttribute("currentStatusDate", FormaterUtil.formatDateToString(currentStatusDate, ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale()));	
			}
		}
		model.addAttribute("statusDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, domain.getLocale()));
	}	

	private void populateInternalStatus(ModelMap model, String type, String userGroupType,String locale, String deviceType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
		CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+deviceType.toUpperCase()+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+deviceType.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificStatuses=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		if(specificDeviceStatusUserGroupStatuses!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(specificDeviceUserGroupStatuses!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(specificStatuses!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
				||userGroupType.equals(ApplicationConstants.SPEAKER)){
			CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_FINAL","");
			if(finalStatus!=null){
				try {
					internalStatuses=Status.findStatusContainedIn(finalStatus.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter!=null){
						try {
							internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						model.addAttribute("errorcode","bill_putup_options_final_notset");
					}		
				}
			}
		}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
				&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
			CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_RECOMMEND","");
			if(recommendStatus!=null){
				try {
					internalStatuses=Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_BY_DEFAULT","");
				if(defaultCustomParameter!=null){
					try {
						internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					model.addAttribute("errorcode","bill_putup_options_final_notset");
				}		
			}
		}
		/**** Internal Status****/
		model.addAttribute("internalStatuses",internalStatuses);		
	}
	
	private void populateRecommendationStatus(final ModelMap model, final Bill domain, final HttpServletRequest request, final String userGroupType) {
		List<Status> recommendationStatuses=new ArrayList<Status>();
		/**** First we will check if custom parameter for device type,internal status and usergroupType has been set ****/
		CustomParameter specificDeviceStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+domain.getType().getType().toUpperCase()+"_"+domain.getInternalStatus().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificDeviceUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+domain.getType().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		CustomParameter specificStatusUserGroupStatuses=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+domain.getInternalStatus().getType().toUpperCase()+"_"+userGroupType.toUpperCase(),"");
		String availableStatusTypes = "";
		if(specificDeviceStatusUserGroupStatuses!=null){
			availableStatusTypes = specificDeviceStatusUserGroupStatuses.getValue();			
		}else if(specificDeviceUserGroupStatuses!=null){
			availableStatusTypes = specificDeviceUserGroupStatuses.getValue();			
		}else if(specificStatusUserGroupStatuses!=null){
			availableStatusTypes = specificStatusUserGroupStatuses.getValue();			
		}
		if(domain.getCurrentHouseType()!=null) {			
			availableStatusTypes = filterAvailableStatusesForBillInCurrentHouse(domain, availableStatusTypes, domain.getCurrentHouseType().getType());			
		}
		recommendationStatuses=Status.findStatusContainedIn(availableStatusTypes, domain.getLocale(), ApplicationConstants.ASC);		
		/**** Recommendation Status****/
		model.addAttribute("recommendationStatuses",recommendationStatuses);		
	}
	
	private String filterAvailableStatusesForBillInCurrentHouse(final Bill domain, final String availableStatusTypes, final String currentHouseType) {
		String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(domain, currentHouseType);
		if(currentHouseOrder!=null) {
			StringBuffer filteredStatusTypes = new StringBuffer("");
			String[] statusTypesArr = availableStatusTypes.split(",");
			for(String i: statusTypesArr) {
				System.out.println(filteredStatusTypes.toString());
				if(!i.trim().isEmpty()) {
					if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
						if(i.trim().endsWith(currentHouseType + "_" + currentHouseOrder)) {
							filteredStatusTypes.append(i.trim()+",");							
						}
					} else {
						if(i.trim().equals(ApplicationConstants.BILL_PROCESSED_PASSEDBYBOTHHOUSES)) {
							if(currentHouseOrder.equals(ApplicationConstants.BILL_FIRST_HOUSE)) {
								filteredStatusTypes.append(i.trim()+",");								
							}
						} else {
							filteredStatusTypes.append(i.trim()+",");						
						}						
					}					
				}				
			}
			if(filteredStatusTypes.length()>1) {
				filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);
			}			
			if(currentHouseOrder.equals(ApplicationConstants.BILL_FIRST_HOUSE)) {
				CustomParameter lapsedStatuses = CustomParameter.findByName(CustomParameter.class, "BILL_LAPSE_OPTIONS_"+domain.getType().getType().toUpperCase(), "");
				if(lapsedStatuses!=null) {
					filteredStatusTypes.append(",");
					filteredStatusTypes.append(lapsedStatuses.getValue());
				}
			}
			return filteredStatusTypes.toString();
		} else {
			return availableStatusTypes;
		}
	}

	@Override
	protected void customValidateCreate(final Bill domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
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
		String languagesAllowedInSession = domain.getSession().getParameter(domain.getType().getType() + "_languagesAllowed");
		/**** title validation ****/
		boolean isTitleInAtleastOneLanguage = false;			
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String titleTextInThisLanguage = request.getParameter("title_text_"+languageAllowedInSession);
			if(titleTextInThisLanguage!=null && !titleTextInThisLanguage.isEmpty()) {
				isTitleInAtleastOneLanguage = true;
				break;
			}
		}
		if(isTitleInAtleastOneLanguage==false) {
			result.rejectValue("titles","TitleEmpty","Please enter the title in atleast one language.");
		}
		/**** content draft validation ****/
		boolean isContentDraftInAtleastOneLanguage = false;			
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String contentDraftTextInThisLanguage = request.getParameter("contentDraft_text_"+languageAllowedInSession);
			if(contentDraftTextInThisLanguage!=null && !contentDraftTextInThisLanguage.isEmpty()) {
				isContentDraftInAtleastOneLanguage = true;
				break;
			}
		}
		if(isContentDraftInAtleastOneLanguage==false) {
			result.rejectValue("version","ContentEmpty","Please fill content draft in atleast one language.");
		}
		/**** statement of object & reason draft validation ****/
		boolean isStatementOfObjectAndReasonInAtleastOneLanguage = false;
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String statementOfObjectAndReasonTextInThisLanguage = request.getParameter("statementOfObjectAndReasonDraft_text_"+languageAllowedInSession);
			if(statementOfObjectAndReasonTextInThisLanguage!=null && !statementOfObjectAndReasonTextInThisLanguage.isEmpty()) {
				isStatementOfObjectAndReasonInAtleastOneLanguage = true;
				break;
			}
		}
		if(isStatementOfObjectAndReasonInAtleastOneLanguage==false) {
			result.rejectValue("version","StatementOfObjectAndReasonEmpty","Please fill statement of object & reason in atleast one language.");
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
					if(domain.getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(domain.getIntroducingHouseType()==null) {
							result.rejectValue("introducingHouseType", "IntroducingHouseTypeEmpty", "Please select the preferred housetype for passing the bill.");
						}
					}	
					if(domain.getBillType()!=null) {
						if(domain.getBillType().getType()!=null) {
							if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
								if(domain.getReferredAct()==null) {
									result.rejectValue("version", "referredActNotSet", "Please refer act for the amending bill");
								}
							} else if(domain.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)) {
								if(domain.getReferredOrdinance()==null) {
									result.rejectValue("version", "referredOrdinanceNotSet", "Please refer ordinance for the ordinance replacement bill");
								}
							}
						}
					}					
				}
			}
		}				
	}
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, Bill domain,
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
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = this.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = this.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = this.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
		
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final Bill domain,
			final HttpServletRequest request) {
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String strUserGroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null && (domain.getTitles()!=null && !domain.getTitles().isEmpty())
				){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&(strUserGroupType.equals("member")||strUserGroupType.equals("clerk"))){
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
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);							 
						}
					} else{
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				} else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			} else{
				Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
				domain.setStatus(status);
				domain.setInternalStatus(status);
				domain.setRecommendationStatus(status);
			}
		} else{
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_INCOMPLETE, domain.getLocale());
			domain.setStatus(status);
			domain.setInternalStatus(status);
			domain.setRecommendationStatus(status);
		}
		/**** add creation date and created by ****/
		domain.setCreationDate(new Date());
		if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&strUserGroupType.equals("clerk")){
			Member member=domain.getPrimaryMember();
			User user = null;
			try {
				user = User.findbyNameBirthDate(member.getFirstName(),member.getMiddleName(),member.getLastName(),member.getBirthDate());
			} catch (ELSException e) {
				// TODO Auto-generated catch block
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
		
		/**** date of opinion sought from law & jd in case it is received for governement bill ****/
		if(domain.getOpinionSoughtFromLawAndJD()!=null) {
			if(!domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
				domain.setDateOfOpinionSoughtFromLawAndJD(new Date());
			}
		}
		
		/**** date of recommendation received from governor in case it is received for governement bill ****/
		if(domain.getRecommendationFromGovernor()!=null) {
			if(!domain.getRecommendationFromGovernor().isEmpty()) {
				domain.setDateOfRecommendationFromGovernor(new Date());
			}
		}
		
		/**** date of recommendation received from president in case it is received for governement bill ****/
		if(domain.getRecommendationFromPresident()!=null) {
			if(!domain.getRecommendationFromPresident().isEmpty()) {
				domain.setDateOfRecommendationFromPresident(new Date());
			}
		}
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = this.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = this.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = this.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final Bill domain,
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
					List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"0");	
					/**** Supporting members status changed to pending ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					List<SupportingMember> supportingMembers=bill.getSupportingMembers();
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
	protected void customValidateUpdate(final Bill domain, final BindingResult result,
			final HttpServletRequest request) {
		/**** Supporting Members and various Validations ****/
		populateSupportingMembers(domain,request);
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
		String usergroupType=request.getParameter("usergroupType");
		if(usergroupType!=null){
			if(usergroupType.equals(ApplicationConstants.ASSISTANT)) {
				if(domain.getMinistry()==null){
					result.rejectValue("ministry","MinistryEmpty","Please select the ministry for bill.");
				}
				if(domain.getBillType()==null){
					result.rejectValue("billType","BillTypeEmpty","Please select the bill type.");
				}
				if(domain.getBillKind()==null){
					result.rejectValue("billKind","BillKindEmpty","Please select the bill kind.");
				}
			}
		}
		
		String languagesAllowedInSession = domain.getSession().getParameter(domain.getType().getType() + "_languagesAllowed");
		/**** title validation ****/
		boolean isTitleInAtleastOneLanguage = false;			
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String titleTextInThisLanguage = request.getParameter("title_text_"+languageAllowedInSession);
			if(titleTextInThisLanguage!=null && !titleTextInThisLanguage.isEmpty()) {
				isTitleInAtleastOneLanguage = true;
				break;
			}
		}
		if(isTitleInAtleastOneLanguage==false) {
			result.rejectValue("titles","TitleEmpty","Please enter the title in atleast one language.");
		}
		/**** content draft validation ****/
		boolean isContentDraftInAtleastOneLanguage = false;			
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String contentDraftTextInThisLanguage = request.getParameter("contentDraft_text_"+languageAllowedInSession);
			if(contentDraftTextInThisLanguage!=null && !contentDraftTextInThisLanguage.isEmpty()) {
				isContentDraftInAtleastOneLanguage = true;
				break;
			}
		}
		if(isContentDraftInAtleastOneLanguage==false) {
			result.rejectValue("version","ContentEmpty","Please fill content draft in atleast one language.");
		}
		/**** statement of object & reason draft validation ****/
		boolean isStatementOfObjectAndReasonInAtleastOneLanguage = false;
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String statementOfObjectAndReasonTextInThisLanguage = request.getParameter("statementOfObjectAndReasonDraft_text_"+languageAllowedInSession);
			if(statementOfObjectAndReasonTextInThisLanguage!=null && !statementOfObjectAndReasonTextInThisLanguage.isEmpty()) {
				isStatementOfObjectAndReasonInAtleastOneLanguage = true;
				break;
			}
		}
		if(isStatementOfObjectAndReasonInAtleastOneLanguage==false) {
			result.rejectValue("version","StatementOfObjectAndReasonEmpty","Please fill statement of object & reason in atleast one language.");
		}
		
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				if(operation.equals("approval")){
					/**** Approval ****/					
					if(domain.getSupportingMembers()==null){
						result.rejectValue("supportingMembers","SupportingMembersEmpty","there are no supporting members for approval.");
					} else if(domain.getSupportingMembers().isEmpty()){
						result.rejectValue("supportingMembers","SupportingMembersEmpty","there are no supporting members for approval.");						
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
					if(domain.getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(domain.getIntroducingHouseType()==null) {
							result.rejectValue("introducingHouseType", "IntroducingHouseTypeEmpty", "Please select the preferred housetype for passing the bill.");
						}
					}
					if(domain.getBillType()!=null) {
						if(domain.getBillType().getType()!=null) {
							if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
								if(domain.getReferredAct()==null) {
									result.rejectValue("version", "referredActNotSet", "Please refer act for the amending bill");
								}
							} else if(domain.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)) {
								if(domain.getReferredOrdinance()==null) {
									result.rejectValue("version", "referredOrdinanceNotSet", "Please refer ordinance for the ordinance replacement bill");
								}
							}
						}
					}
				} else if(operation.equals("startworkflow")){
					if(domain.getOpinionSoughtFromLawAndJD()==null) {
						result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from Law and JD is not received");
					} else if(domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
						result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from Law and JD is not received");
					}
					String languagesCompulsoryInSession = domain.getSession().getParameter(domain.getType().getType() + "_languagesCompulsory");
					/**** title validation for compulsory languages ****/
					boolean isTitleInAllCompulsoryLanguages = true;			
					for(String languageCompulsoryInSession: languagesCompulsoryInSession.split("#")) {
						String titleTextInThisLanguage = request.getParameter("title_text_"+languageCompulsoryInSession);
						if(titleTextInThisLanguage==null) {
							String revisedTitleTextInThisLanguage = request.getParameter("revised_title_text_"+languageCompulsoryInSession);
							if(revisedTitleTextInThisLanguage==null) {
								isTitleInAllCompulsoryLanguages = false;
								break;
							} else if(revisedTitleTextInThisLanguage.isEmpty()) {
								isTitleInAllCompulsoryLanguages = false;
								break;
							}							
						} else if(titleTextInThisLanguage.isEmpty()) {
							String revisedTitleTextInThisLanguage = request.getParameter("revised_title_text_"+languageCompulsoryInSession);
							if(revisedTitleTextInThisLanguage==null) {
								isTitleInAllCompulsoryLanguages = false;
								break;
							} else if(revisedTitleTextInThisLanguage.isEmpty()) {
								isTitleInAllCompulsoryLanguages = false;
								break;
							}
						}
					}
					if(isTitleInAllCompulsoryLanguages==false) {
						result.rejectValue("version","TitleEmptyInRequiredLanguages","Please fill title in required languages.");
					}
					/**** content draft validation for compulsory languages ****/
					boolean isContentDraftInAllCompulsoryLanguages = true;			
					for(String languageCompulsoryInSession: languagesCompulsoryInSession.split("#")) {
						String contentDraftTextInThisLanguage = request.getParameter("contentDraft_text_"+languageCompulsoryInSession);
						if(contentDraftTextInThisLanguage==null) {
							String revisedContentDraftTextInThisLanguage = request.getParameter("revised_contentDraft_text_"+languageCompulsoryInSession);
							if(revisedContentDraftTextInThisLanguage==null) {
								isContentDraftInAllCompulsoryLanguages = false;
								break;
							} else if(revisedContentDraftTextInThisLanguage.isEmpty()) {
								isContentDraftInAllCompulsoryLanguages = false;
								break;
							}							
						} else if(contentDraftTextInThisLanguage.isEmpty()) {
							String revisedContentDraftTextInThisLanguage = request.getParameter("revised_contentDraft_text_"+languageCompulsoryInSession);
							if(revisedContentDraftTextInThisLanguage==null) {
								isContentDraftInAllCompulsoryLanguages = false;
								break;
							} else if(revisedContentDraftTextInThisLanguage.isEmpty()) {
								isContentDraftInAllCompulsoryLanguages = false;
								break;
							}
						}
					}
					if(isContentDraftInAllCompulsoryLanguages==false) {
						result.rejectValue("version","ContentEmptyInRequiredLanguages","Please fill content draft in required languages.");
					}
					/**** statement of object & reason validation for compulsory languages ****/
					boolean isStatementOfObjectAndReasonInAllCompulsoryLanguages = true;			
					for(String languageCompulsoryInSession: languagesCompulsoryInSession.split("#")) {
						String statementOfObjectAndReasonTextInThisLanguage = request.getParameter("statementOfObjectAndReasonDraft_text_"+languageCompulsoryInSession);
						if(statementOfObjectAndReasonTextInThisLanguage==null) {
							String revisedStatementOfObjectAndReasonTextInThisLanguage = request.getParameter("revised_statementOfObjectAndReasonDraft_text_"+languageCompulsoryInSession);
							if(revisedStatementOfObjectAndReasonTextInThisLanguage==null) {
								isStatementOfObjectAndReasonInAllCompulsoryLanguages = false;
								break;
							} else if(revisedStatementOfObjectAndReasonTextInThisLanguage.isEmpty()) {
								isStatementOfObjectAndReasonInAllCompulsoryLanguages = false;
								break;
							}							
						} else if(statementOfObjectAndReasonTextInThisLanguage.isEmpty()) {
							String revisedStatementOfObjectAndReasonTextInThisLanguage = request.getParameter("revised_statementOfObjectAndReasonDraft_text_"+languageCompulsoryInSession);
							if(revisedStatementOfObjectAndReasonTextInThisLanguage==null) {
								isStatementOfObjectAndReasonInAllCompulsoryLanguages = false;
								break;
							} else if(revisedStatementOfObjectAndReasonTextInThisLanguage.isEmpty()) {
								isStatementOfObjectAndReasonInAllCompulsoryLanguages = false;
								break;
							}
						}
					}
					if(isStatementOfObjectAndReasonInAllCompulsoryLanguages==false) {
						result.rejectValue("version","StatementOfObjectAndReasonEmptyInRequiredLanguages","Please fill statement of object & reason in required languages.");
					}					
					/**** financial memorandum validation for compulsory languages if it is mentioned in any language ****/
					//first check if financial memorandum is mentioned in atleast one language 
					boolean isFinancialMemorandumInAtleastOneLanguage = false;			
					for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
						String financialMemorandumTextInThisLanguage = request.getParameter("financialMemorandumDraft_text_"+languageAllowedInSession);
						if(financialMemorandumTextInThisLanguage!=null && !financialMemorandumTextInThisLanguage.isEmpty()) {
							isFinancialMemorandumInAtleastOneLanguage = true;
							break;
						} else {
							String revisedFinancialMemorandumTextInThisLanguage = request.getParameter("revised_financialMemorandumDraft_text_"+languageAllowedInSession);
							if(revisedFinancialMemorandumTextInThisLanguage!=null && !revisedFinancialMemorandumTextInThisLanguage.isEmpty()) {
								isFinancialMemorandumInAtleastOneLanguage = true;
								break;
							}
						}
					}
					if(isFinancialMemorandumInAtleastOneLanguage==true) {
						boolean isFinancialMemorandumInAllCompulsoryLanguages = true;			
						for(String languageCompulsoryInSession: languagesCompulsoryInSession.split("#")) {
							String financialMemorandumTextInThisLanguage = request.getParameter("financialMemorandumDraft_text_"+languageCompulsoryInSession);
							if(financialMemorandumTextInThisLanguage==null) {
								String revisedFinancialMemorandumTextInThisLanguage = request.getParameter("revised_financialMemorandumDraft_text_"+languageCompulsoryInSession);
								if(revisedFinancialMemorandumTextInThisLanguage==null) {
									isFinancialMemorandumInAllCompulsoryLanguages = false;
									break;
								} else if(revisedFinancialMemorandumTextInThisLanguage.isEmpty()) {
									isFinancialMemorandumInAllCompulsoryLanguages = false;
									break;
								}
							} else if(financialMemorandumTextInThisLanguage.isEmpty()) {
								String revisedFinancialMemorandumTextInThisLanguage = request.getParameter("revised_financialMemorandumDraft_text_"+languageCompulsoryInSession);
								if(revisedFinancialMemorandumTextInThisLanguage==null) {
									isFinancialMemorandumInAllCompulsoryLanguages = false;
									break;
								} else if(revisedFinancialMemorandumTextInThisLanguage.isEmpty()) {
									isFinancialMemorandumInAllCompulsoryLanguages = false;
									break;
								}
							}
						}
						if(isFinancialMemorandumInAllCompulsoryLanguages==false) {
							result.rejectValue("version","FinancialMemorandumEmptyInRequiredLanguages","Please fill financial memorandum in required languages.");
						}
					}
					/**** statutory memorandum validation for compulsory languages if it is mentioned in any language ****/
					//first check if statutory memorandum is mentioned in atleast one language 
					boolean isStatutoryMemorandumInAtleastOneLanguage = false;			
					for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
						String statutoryMemorandumTextInThisLanguage = request.getParameter("statutoryMemorandumDraft_text_"+languageAllowedInSession);
						if(statutoryMemorandumTextInThisLanguage!=null && !statutoryMemorandumTextInThisLanguage.isEmpty()) {
							isStatutoryMemorandumInAtleastOneLanguage = true;
							break;
						} else {
							String revisedStatutoryMemorandumTextInThisLanguage = request.getParameter("revised_statutoryMemorandumDraft_text_"+languageAllowedInSession);
							if(revisedStatutoryMemorandumTextInThisLanguage!=null && !revisedStatutoryMemorandumTextInThisLanguage.isEmpty()) {
								isStatutoryMemorandumInAtleastOneLanguage = true;
								break;
							}
						}
					}
					if(isStatutoryMemorandumInAtleastOneLanguage==true) {
						boolean isStatutoryMemorandumInAllCompulsoryLanguages = true;			
						for(String languageCompulsoryInSession: languagesCompulsoryInSession.split("#")) {
							String statutoryMemorandumTextInThisLanguage = request.getParameter("statutoryMemorandumDraft_text_"+languageCompulsoryInSession);
							if(statutoryMemorandumTextInThisLanguage==null) {
								String revisedStatutoryMemorandumTextInThisLanguage = request.getParameter("revised_statutoryMemorandumDraft_text_"+languageCompulsoryInSession);
								if(revisedStatutoryMemorandumTextInThisLanguage==null) {
									isStatutoryMemorandumInAllCompulsoryLanguages = false;
									break;
								} else if(revisedStatutoryMemorandumTextInThisLanguage.isEmpty()) {
									isStatutoryMemorandumInAllCompulsoryLanguages = false;
									break;
								}
							} else if(statutoryMemorandumTextInThisLanguage.isEmpty()) {
								String revisedStatutoryMemorandumTextInThisLanguage = request.getParameter("revised_statutoryMemorandumDraft_text_"+languageCompulsoryInSession);
								if(revisedStatutoryMemorandumTextInThisLanguage==null) {
									isStatutoryMemorandumInAllCompulsoryLanguages = false;
									break;
								} else if(revisedStatutoryMemorandumTextInThisLanguage.isEmpty()) {
									isStatutoryMemorandumInAllCompulsoryLanguages = false;
									break;
								}
							}
						}
						if(isStatutoryMemorandumInAllCompulsoryLanguages==false) {
							result.rejectValue("version","StatutoryMemorandumEmptyInRequiredLanguages","Please fill statutory memorandum in required languages.");
						}
					}
					/**** annexures for amending bill validation for all draft languages if it is mentioned in any language ****/
					if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
						//first check if annexure for amending bill is mentioned in atleast one language 
						boolean isAnnexureForAmendingBillInAtleastOneLanguage = false;			
						for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
							String annexureForAmendingBillTextInThisLanguage = request.getParameter("annexureForAmendingBill_text_"+languageAllowedInSession);
							if(annexureForAmendingBillTextInThisLanguage!=null && !annexureForAmendingBillTextInThisLanguage.isEmpty()) {
								isAnnexureForAmendingBillInAtleastOneLanguage = true;
								break;
							} 
							else {
								String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
								if(revisedAnnexureForAmendingBillTextInThisLanguage!=null && !revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty()) {
									isAnnexureForAmendingBillInAtleastOneLanguage = true;
									break;
								}
							}
						}
						if(isAnnexureForAmendingBillInAtleastOneLanguage==true) {
							boolean isAnnexureForAmendingBillInAllContentDraftLanguages = true;						
							for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
								if(request.getParameter("contentDraft_text_"+languageAllowedInSession)!=null
										&& !request.getParameter("contentDraft_text_"+languageAllowedInSession).isEmpty()) {
									String annexureForAmendingBillTextInThisLanguage = request.getParameter("annexureForAmendingBill_text_"+languageAllowedInSession);
									if(annexureForAmendingBillTextInThisLanguage==null) {
										String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
										if(revisedAnnexureForAmendingBillTextInThisLanguage==null) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										} else if(revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty()) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										}
									} else if(annexureForAmendingBillTextInThisLanguage.isEmpty()) {
										String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
										if(revisedAnnexureForAmendingBillTextInThisLanguage==null) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										} else if(revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty()) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										}
									}
								} else if(request.getParameter("revised_contentDraft_text_"+languageAllowedInSession)!=null
										&& !request.getParameter("revised_contentDraft_text_"+languageAllowedInSession).isEmpty()) {
									String annexureForAmendingBillTextInThisLanguage = request.getParameter("annexureForAmendingBill_text_"+languageAllowedInSession);
									if(annexureForAmendingBillTextInThisLanguage==null) {
										String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
										if(revisedAnnexureForAmendingBillTextInThisLanguage==null) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										} else if(revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty()) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										}
									} else if(annexureForAmendingBillTextInThisLanguage.isEmpty()) {
										String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
										if(revisedAnnexureForAmendingBillTextInThisLanguage==null) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										} else if(revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty()) {
											isAnnexureForAmendingBillInAllContentDraftLanguages = false;
											break;
										}
									}
								}
							}
							if(isAnnexureForAmendingBillInAllContentDraftLanguages==false) {
								result.rejectValue("version","AnnexureForAmendingBillEmptyInContentDraftLanguages","Please fill annexures for all content draft languages.");
							}
						}
					}					
				} else if(operation.equals("sendForGazettePublishing")){
					String isPrintRequisitionForGazetteSent = request.getParameter("isPrintRequisitionForGazetteSent");
					if(isPrintRequisitionForGazetteSent!=null) {
						if(isPrintRequisitionForGazetteSent.equals("true")) {
							result.rejectValue("version", "sendForGazettePublishingAlreadyDone", "request for gazette publishing is already sent.");
						}
					}
					//validation for gazette reports to be done
				}
			}
		}
		
		/**** validations for status updation ****/
		Status statusForBeginningStatusUpdation = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED, domain.getLocale());
		if(domain.getInternalStatus().getPriority()>=statusForBeginningStatusUpdation.getPriority()) {
			Bill bill = Bill.findById(Bill.class, domain.getId());
			Status oldRecommendationStatus = bill.getRecommendationStatus();
			//validations for update to introduction		
			if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED)
					&&domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
				/**** recommendation from governor/president if needed ****/
				String recommendationFromGovernorCheck = domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_CHECKLIST_PARAMETER);
				if(recommendationFromGovernorCheck!=null) {
					if(!recommendationFromGovernorCheck.isEmpty()) {
						if(domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_CHECKLIST_PARAMETER).equals("yes")) {
							if(domain.getRecommendationFromGovernor()==null) {
								result.rejectValue("version", "recommendationFromGovernorNotReceived", "recommendation from governor is needed but not received yet.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							} else if(domain.getRecommendationFromGovernor().isEmpty()) {
								result.rejectValue("version", "recommendationFromGovernorNotReceived", "recommendation from governor is needed but not received yet.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}
						}
					}
				}
				String recommendationFromPresidentCheck = domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_CHECKLIST_PARAMETER);
				if(recommendationFromPresidentCheck!=null) {
					if(!recommendationFromPresidentCheck.isEmpty()) {
						if(domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_CHECKLIST_PARAMETER).equals("yes")) {
							if(domain.getRecommendationFromPresident()==null) {
								result.rejectValue("version", "recommendationFromPresidentNotReceived", "recommendation from president is needed but not received yet.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							} else if(domain.getRecommendationFromPresident().isEmpty()) {
								result.rejectValue("version", "recommendationFromPresidentNotReceived", "recommendation from president is needed but not received yet.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}
						}
					}
				}	
				if(domain.getStatusDate()==null) {
					result.rejectValue("version", "introductionDateNotSet", "Please set introduction date.");
					domain.setRecommendationStatus(oldRecommendationStatus);
				}
			}
			//validations for updation to consideration
			if(oldRecommendationStatus.getType().startsWith(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED)) {
				String currentPosition = oldRecommendationStatus.getType().replaceFirst(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED, "");
				if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_CONSIDERED + currentPosition)) {
					result.rejectValue("version", "billNotUpdatedUnderConsideration", "Bill has not been considered.");
					domain.setRecommendationStatus(oldRecommendationStatus);
				}			
			}
			//validations for updation to refer/re-refer joint committee
			if(oldRecommendationStatus.getType().startsWith(ApplicationConstants.BILL_PROCESSED_CONSIDERED)) {
				String currentPosition = oldRecommendationStatus.getType().replaceFirst(ApplicationConstants.BILL_PROCESSED_CONSIDERED, "");
//				currentPosition = currentPosition.substring(0, currentPosition.length()-3);
				if(domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_REFERTOJOINTCOMMITTEE + currentPosition)
						|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_REREFERTOJOINTCOMMITTEE + currentPosition)) {
					if(domain.getBillKind().getType().equals(ApplicationConstants.MONEY_BILL)) {
						result.rejectValue("version", "moneyBillCantBeReferredToJointCommittee", "Money Bill Cannot Be Referred To Joint Committee.");
						domain.setRecommendationStatus(oldRecommendationStatus);
					}
				}
			}	
			//validations for house round in status updation			
			String currentHouseType = domain.getCurrentHouseType().getType();		
			BillDraft latestDraftOfStatus = null;
			if(Bill.findHouseOrderOfGivenHouseForBill(domain, currentHouseType).equals(ApplicationConstants.BILL_FIRST_HOUSE)) {
				if(domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSED+"_")) {
					latestDraftOfStatus = Bill.findDraftByRecommendationStatusAndHouseRound(domain, domain.getRecommendationStatus(), domain.getHouseRound());
					if(domain.getHouseRound()==1) {
						if(latestDraftOfStatus!=null) {
							if(latestDraftOfStatus.getHouseRound()>1) {
								result.rejectValue("version", "billAlreadyPassedInFirstHouse", "Bill is already passed in first house.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}
						}
					} else if(domain.getHouseRound()==2) {
						if(latestDraftOfStatus==null) {
							result.rejectValue("version", "billNotPassedInFirstHouseForFirstTime", "Bill is not passed in first house for first time.");
							domain.setRecommendationStatus(oldRecommendationStatus);
						} else {
							if(latestDraftOfStatus.getHouseRound()>2) {
								result.rejectValue("version", "billAlreadyPassedInFirstHouseForSecondTime", "Bill is already passed in first house for second time.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}
						}
					}
				} else if(!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_NEGATIVED)
						&&!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_WITHDRAWN)
						&&!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_LAPSED)
						&&!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSEDBYBOTHHOUSES)) {
					if(domain.getHouseRound()!=null) {
						latestDraftOfStatus = Bill.findDraftByRecommendationStatusAndHouseRound(domain, domain.getRecommendationStatus(), domain.getHouseRound());
						if(domain.getHouseRound()==1) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()!=1) {
									result.rejectValue("version", "billStatusAlreadySetInSecondHouse", "This status is already set in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}						
							}
						} else if(domain.getHouseRound()==2) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()!=2) {
									result.rejectValue("version", "billStatusAlreadySetInSecondHouse", "This status is already set in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}						
							}
						}
					}					
				}
			} else if(Bill.findHouseOrderOfGivenHouseForBill(domain, currentHouseType).equals(ApplicationConstants.BILL_SECOND_HOUSE)) {
				if(domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSED+"_")) {
					if(domain.getHouseRound()!=null) {
						latestDraftOfStatus = Bill.findDraftByRecommendationStatusAndHouseRound(domain, domain.getRecommendationStatus(), domain.getHouseRound());
						if(domain.getHouseRound()==1) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()>1) {
									result.rejectValue("version", "billAlreadyPassedInSecondHouse", "Bill is already passed in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}
							}
						} else if(domain.getHouseRound()==2) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()!=2) {
									result.rejectValue("version", "billAlreadyPassedInSecondHouse", "Bill is already passed in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}						
							}
						}
					}					
				} else if(domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_NEGATIVED)) {
					if(domain.getHouseRound()!=null) {
						latestDraftOfStatus = Bill.findDraftByRecommendationStatusAndHouseRound(domain, domain.getRecommendationStatus(), domain.getHouseRound());
						if(domain.getHouseRound()==1) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()>1) {
									result.rejectValue("version", "billAlreadyNegativedInSecondHouse", "Bill is already negatived in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}
							}
						}
					}					
				} else if(!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_WITHDRAWN)
						&&!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_LAPSED)
						&&!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSEDBYBOTHHOUSES)) {
					if(domain.getHouseRound()!=null) {
						latestDraftOfStatus = Bill.findDraftByRecommendationStatusAndHouseRound(domain, domain.getRecommendationStatus(), domain.getHouseRound());
						if(domain.getHouseRound()==1) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()!=1) {
									result.rejectValue("version", "billStatusAlreadySetInSecondHouse", "This status is already set in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}						
							}
						} else if(domain.getHouseRound()==2) {
							if(latestDraftOfStatus!=null) {
								if(latestDraftOfStatus.getHouseRound()!=2) {
									result.rejectValue("version", "billStatusAlreadySetInSecondHouse", "This status is already set in second house.");
									domain.setRecommendationStatus(oldRecommendationStatus);
								}						
							}
						}
					}					
				}
			}
		}		
	}	
	
	private void populateSupportingMembers(final Bill domain,final HttpServletRequest request){
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
			Bill bill=Bill.findById(Bill.class,domain.getId());
			members=bill.getSupportingMembers();
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
					}
					/*** List is updated ****/
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
	}
	
	@Override
	protected void populateUpdateIfErrors(ModelMap model, Bill domain, HttpServletRequest request) {
		Bill bill = null;
		
		/**** updating various dates including submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strDateOfOpinionSoughtFromLawAndJD=request.getParameter("setDateOfOpinionSoughtFromLawAndJD");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		String strWorkflowForTranslationStartedOnDate=request.getParameter("workflowForTranslationStartedOnDate");
		String strTaskReceivedOnDateForTranslation=request.getParameter("taskReceivedOnDateForTranslation");
		String strWorkflowForOpinionFromLawAndJDStartedOnDate=request.getParameter("workflowForOpinionFromLawAndJDStartedOnDate");
		String strTaskReceivedOnDateForOpinionFromLawAndJD=request.getParameter("taskReceivedOnDateForOpinionFromLawAndJD");
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
				if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
				if(strWorkflowForTranslationStartedOnDate!=null&&!strWorkflowForTranslationStartedOnDate.isEmpty()){
					domain.setWorkflowForTranslationStartedOn(format.parse(strWorkflowForTranslationStartedOnDate));
				}
				if(strTaskReceivedOnDateForTranslation!=null&&!strTaskReceivedOnDateForTranslation.isEmpty()){
					domain.setTaskReceivedOnForTranslation(format.parse(strTaskReceivedOnDateForTranslation));
				}
				if(strWorkflowForOpinionFromLawAndJDStartedOnDate!=null&&!strWorkflowForOpinionFromLawAndJDStartedOnDate.isEmpty()){
					domain.setWorkflowForOpinionFromLawAndJDStartedOn(format.parse(strWorkflowForOpinionFromLawAndJDStartedOnDate));
				}
				if(strTaskReceivedOnDateForOpinionFromLawAndJD!=null&&!strTaskReceivedOnDateForOpinionFromLawAndJD.isEmpty()){
					domain.setTaskReceivedOnForOpinionFromLawAndJD(format.parse(strTaskReceivedOnDateForOpinionFromLawAndJD));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);	
		/**** add/update revised titles in domain ****/
		List<TextDraft> revisedTitles = this.updateDraftsOfGivenType(domain, "revised_title", request);
		domain.setRevisedTitles(revisedTitles);
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		/**** add/update revised content drafts in domain ****/
		List<TextDraft> revisedContentDrafts = this.updateDraftsOfGivenType(domain, "revised_contentDraft", request);
		domain.setRevisedContentDrafts(revisedContentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = this.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		/**** add/update revised 'statement of object and reason drafts' in domain ****/
		List<TextDraft> revisedStatementOfObjectAndReasonDrafts = this.updateDraftsOfGivenType(domain, "revised_statementOfObjectAndReasonDraft", request);		
		domain.setRevisedStatementOfObjectAndReasonDrafts(revisedStatementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = this.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		/**** add/update revised financial memorandum drafts in domain ****/
		List<TextDraft> revisedFinancialMemorandumDrafts = this.updateDraftsOfGivenType(domain, "revised_financialMemorandumDraft", request);		
		domain.setRevisedFinancialMemorandumDrafts(revisedFinancialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = this.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
		/**** add/update revised statutory memorandum drafts in domain ****/
		List<TextDraft> revisedStatutoryMemorandumDrafts = this.updateDraftsOfGivenType(domain, "revised_statutoryMemorandumDraft", request);
		domain.setRevisedStatutoryMemorandumDrafts(revisedStatutoryMemorandumDrafts);
		
		/**** add/update annexures for amending bill in domain ****/
		List<TextDraft> annexuresForAmendingBill = this.updateDraftsOfGivenType(domain, "annexureForAmendingBill", request);
		domain.setAnnexuresForAmendingBill(annexuresForAmendingBill);	
		/**** add/update revised annexures for amending bill in domain ****/
		List<TextDraft> revisedAnnexuresForAmendingBill = this.updateDraftsOfGivenType(domain, "revised_annexureForAmendingBill", request);
		domain.setRevisedAnnexuresForAmendingBill(revisedAnnexuresForAmendingBill);
		
		/**** added by dhananjayb ****/
		/**** resetting statuses in case this was workflow request ****/
		String operation = request.getParameter("operation");
		if(operation!=null) {
			if(!operation.isEmpty()) {				
				if(operation.equals("startworkflow")) {
					Status oldInternalStatus = null;
					String oldInternalStatusId = request.getParameter("oldInternalStatus");
					if(oldInternalStatusId==null) {						
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setInternalStatus(bill.getInternalStatus());
					} else if(oldInternalStatusId.isEmpty()) {
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setInternalStatus(bill.getInternalStatus());
					} else {
						oldInternalStatus = Status.findById(Status.class, Long.parseLong(oldInternalStatusId));
						if(oldInternalStatus!=null) {
							domain.setInternalStatus(oldInternalStatus);
						} else {
							bill = Bill.findById(Bill.class, domain.getId());
							domain.setInternalStatus(bill.getInternalStatus());
						}						
					}
					Status oldRecommendationStatus = null;
					String oldRecommendationStatusId = request.getParameter("oldRecommendationStatus");
					if(oldRecommendationStatusId==null) {						
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setRecommendationStatus(bill.getRecommendationStatus());
					} else if(oldRecommendationStatusId.isEmpty()) {
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setRecommendationStatus(bill.getRecommendationStatus());
					} else {
						oldRecommendationStatus = Status.findById(Status.class, Long.parseLong(oldRecommendationStatusId));
						if(oldRecommendationStatus!=null) {
							domain.setRecommendationStatus(oldRecommendationStatus);
						} else {
							bill = Bill.findById(Bill.class, domain.getId());
							domain.setRecommendationStatus(bill.getRecommendationStatus());
						}						
					}
				} else if(operation.equals("sendForTranslation")) {
					Status oldTranslationStatus = null;
					String oldTranslationStatusId = request.getParameter("oldTranslationStatus");
					if(oldTranslationStatusId==null) {						
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setTranslationStatus(bill.getTranslationStatus());
					} else if(oldTranslationStatusId.isEmpty()) {
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setTranslationStatus(bill.getTranslationStatus());
					} else {
						oldTranslationStatus = Status.findById(Status.class, Long.parseLong(oldTranslationStatusId));
						if(oldTranslationStatus!=null) {
							domain.setTranslationStatus(oldTranslationStatus);
						} else {
							bill = Bill.findById(Bill.class, domain.getId());
							domain.setTranslationStatus(bill.getTranslationStatus());
						}						
					}
				} else if(operation.equals("sendForOpinionFromLawAndJD")) {
					Status oldOpinionFromLawAndJDStatus = null;
					String oldOpinionFromLawAndJDStatusId = request.getParameter("oldOpinionFromLawAndJDStatus");
					if(oldOpinionFromLawAndJDStatusId==null) {						
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setOpinionFromLawAndJDStatus(bill.getOpinionFromLawAndJDStatus());
					} else if(oldOpinionFromLawAndJDStatusId.isEmpty()) {
						bill = Bill.findById(Bill.class, domain.getId());
						domain.setOpinionFromLawAndJDStatus(bill.getOpinionFromLawAndJDStatus());
					} else {
						oldOpinionFromLawAndJDStatus = Status.findById(Status.class, Long.parseLong(oldOpinionFromLawAndJDStatusId));
						if(oldOpinionFromLawAndJDStatus!=null) {
							domain.setOpinionFromLawAndJDStatus(oldOpinionFromLawAndJDStatus);
						} else {
							bill = Bill.findById(Bill.class, domain.getId());
							domain.setOpinionFromLawAndJDStatus(bill.getOpinionFromLawAndJDStatus());
						}						
					}
				}
			}
		}				
		if(bill==null) {
			bill = Bill.findById(Bill.class, domain.getId());
		}
		domain.setVotingDetails(bill.getVotingDetails());
		super.populateUpdateIfErrors(model, domain, request);
	}
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Bill domain,
			final HttpServletRequest request) {		
		Bill bill = null;
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = this.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);	
		/**** add/update revised titles in domain ****/
		List<TextDraft> revisedTitles = this.updateDraftsOfGivenType(domain, "revised_title", request);
		domain.setRevisedTitles(revisedTitles);
		
		/**** Checking if it's submission request or normal update ****/
		String operation=request.getParameter("operation");
		String strUserGroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null && (domain.getTitles()!=null && !domain.getTitles().isEmpty())
				){
			if(operation!=null){
				if(!operation.isEmpty()){
					if(operation.trim().equals("submit")){
						if(strUserGroupType!=null&&!(strUserGroupType.isEmpty())&&(strUserGroupType.equals("member")||strUserGroupType.equals("clerk"))){
							/****  submission date is set ****/
							if(domain.getSubmissionDate()==null){
								domain.setSubmissionDate(new Date());
							}
							/**** only those supporting memebers will be included who have approved the requests ****/
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
							Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_SUBMIT, domain.getLocale());
							domain.setStatus(newstatus);
							domain.setInternalStatus(newstatus);
							domain.setRecommendationStatus(newstatus);
						}
					} else {
						/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
						if(domain.getStatus().getType().equals(ApplicationConstants.BILL_INCOMPLETE)
								|| domain.getStatus().getType().equals(ApplicationConstants.BILL_COMPLETE)){
							Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
							domain.setStatus(status);
							domain.setInternalStatus(status);
							domain.setRecommendationStatus(status);
						}						
					}
				} else {
					/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
					if(!domain.getStatus().getType().equals(ApplicationConstants.BILL_SUBMIT)){
						Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
						domain.setStatus(status);
						domain.setInternalStatus(status);
						domain.setRecommendationStatus(status);
					}
				}
			} else {
				/**** if status is not submit then status,internal status and recommendation status is set to complete ****/
				Status submit = Status.findByType(ApplicationConstants.BILL_SUBMIT, domain.getLocale()); 
				if(domain.getStatus().getPriority() < submit.getPriority()/*.getType().equals(ApplicationConstants.BILL_SUBMIT)*/){
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}
		} else {
			/**** If all mandatory fields have not been set then status,internal status and recommendation status is set to incomplete ****/
			Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_INCOMPLETE, domain.getLocale());
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
		/**** In case of assistant if internal status=submit,ministry,department,group is set 
		 * then change its internal and recommendation status to assistant processed ****/
		if(strUserGroupType!=null){
			if(strUserGroupType.equals("assistant")){
				Long id = domain.getId();
				bill = Bill.findById(Bill.class, id);
				String internalStatus = bill.getInternalStatus().getType();
				if(internalStatus.equals(ApplicationConstants.BILL_SUBMIT) && domain.getMinistry()!=null && domain.getSubDepartment() != null) {
					Status ASSISTANT_PROCESSED = Status.findByType(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
					domain.setInternalStatus(ASSISTANT_PROCESSED);
					domain.setRecommendationStatus(ASSISTANT_PROCESSED);
				}				
				/**** File parameters are set when internal status is something other than 
				 * submit,complete and incomplete and file is null .Then only the motion gets attached to a file.*/
//				String currentStatus=domain.getInternalStatus().getType();
//				if(operation==null){
//					if(!domain.getType().getType().equals(ApplicationConstants.STARRED_BILL)
//						|| !domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_BILL_STANDALONE)
//						&&!(currentStatus.equals(ApplicationConstants.BILL_SUBMIT)
//							||currentStatus.equals(ApplicationConstants.BILL_COMPLETE)
//							||currentStatus.equals(ApplicationConstants.BILL_INCOMPLETE))
//							&& (domain.getType().getType().equals(ApplicationConstants.UNSTARRED_BILL)
//							||domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_BILL_FROM_BILL)
//							||domain.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_BILL))							
//							&&domain.getFile()==null){
//						/**** Add Bill to file ****/
//						Reference reference=Bill.findCurrentFile(domain);
//						domain.setFile(Integer.parseInt(reference.getId()));
//						domain.setFileIndex(Integer.parseInt(reference.getName()));
//						domain.setFileSent(false);
//					}
//				}else if(operation.isEmpty()){
//					if(!domain.getType().getType().equals(ApplicationConstants.STARRED_BILL)
//						|| !domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_BILL_STANDALONE)
//						&&!(currentStatus.equals(ApplicationConstants.BILL_SUBMIT)
//							||currentStatus.equals(ApplicationConstants.BILL_COMPLETE)
//							||currentStatus.equals(ApplicationConstants.BILL_INCOMPLETE))
//							&& (domain.getType().getType().equals(ApplicationConstants.UNSTARRED_BILL)
//							||domain.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_BILL_FROM_BILL)
//							||domain.getType().getType().equals(ApplicationConstants.SHORT_NOTICE_BILL))							
//							&&domain.getFile()==null){
//						/**** Add Bill to file ****/
//						Reference reference=Bill.findCurrentFile(domain);
//						domain.setFile(Integer.parseInt(reference.getId()));
//						domain.setFileIndex(Integer.parseInt(reference.getName()));
//						domain.setFileSent(false);
//					}
//				}
			}
		}
		/**** updating various dates including submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strDateOfOpinionSoughtFromLawAndJD=request.getParameter("setDateOfOpinionSoughtFromLawAndJD");
		String strDateOfRecommendationFromGovernor=request.getParameter("setDateOfRecommendationFromGovernor");
		String strDateOfRecommendationFromPresident=request.getParameter("setDateOfRecommendationFromPresident");
		String strWorkflowStartedOnDate=request.getParameter("workflowStartedOnDate");
		String strWorkflowForTranslationStartedOnDate=request.getParameter("workflowForTranslationStartedOnDate");
		String strWorkflowForOpinionFromLawAndJDStartedOnDate=request.getParameter("workflowForOpinionFromLawAndJDStartedOnDate");
		String strWorkflowForRecommendationFromGovernorStartedOnDate=request.getParameter("workflowForRecommendationFromGovernorStartedOnDate");
		String strWorkflowForRecommendationFromPresidentStartedOnDate=request.getParameter("workflowForRecommendationFromPresidentStartedOnDate");
		String strTaskReceivedOnDate=request.getParameter("taskReceivedOnDate");
		String strTaskReceivedOnDateForTranslation=request.getParameter("taskReceivedOnDateForTranslation");
		String strTaskReceivedOnDateForOpinionFromLawAndJD=request.getParameter("taskReceivedOnDateForOpinionFromLawAndJD");
		String strTaskReceivedOnDateForRecommendationFromGovernor=request.getParameter("taskReceivedOnDateForRecommendationFromGovernor");
		String strTaskReceivedOnDateForRecommendationFromPresident=request.getParameter("taskReceivedOnDateForRecommendationFromPresident");
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
				} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_SUBMIT) 
						|| domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED)) {
					if(domain.getOpinionSoughtFromLawAndJD()!=null) {
						if(!domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
							domain.setDateOfOpinionSoughtFromLawAndJD(new Date());
						}
					}
				}
				if(strDateOfRecommendationFromGovernor!=null&&!strDateOfRecommendationFromGovernor.isEmpty()) {
					domain.setDateOfRecommendationFromGovernor(format.parse(strDateOfRecommendationFromGovernor));
				} 
//				else {
//					if(domain.getRecommendationFromGovernor()!=null) {
//						if(!domain.getRecommendationFromGovernor().isEmpty()) {
//							domain.setDateOfRecommendationFromGovernor(new Date());
//						}
//					}
//				}
				if(strDateOfRecommendationFromPresident!=null&&!strDateOfRecommendationFromPresident.isEmpty()) {
					domain.setDateOfRecommendationFromPresident(format.parse(strDateOfRecommendationFromPresident));
				} 
//				else {
//					if(domain.getRecommendationFromPresident()!=null) {
//						if(!domain.getRecommendationFromPresident().isEmpty()) {
//							domain.setDateOfRecommendationFromPresident(new Date());
//						}
//					}
//				}
				if(strWorkflowStartedOnDate!=null&&!strWorkflowStartedOnDate.isEmpty()){
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if(strWorkflowForTranslationStartedOnDate!=null&&!strWorkflowForTranslationStartedOnDate.isEmpty()){
					domain.setWorkflowForTranslationStartedOn(format.parse(strWorkflowForTranslationStartedOnDate));
				}
				if(strWorkflowForOpinionFromLawAndJDStartedOnDate!=null&&!strWorkflowForOpinionFromLawAndJDStartedOnDate.isEmpty()){
					domain.setWorkflowForOpinionFromLawAndJDStartedOn(format.parse(strWorkflowForOpinionFromLawAndJDStartedOnDate));
				}
				if(strWorkflowForRecommendationFromGovernorStartedOnDate!=null&&!strWorkflowForRecommendationFromGovernorStartedOnDate.isEmpty()){
					domain.setWorkflowForRecommendationFromGovernorStartedOn(format.parse(strWorkflowForRecommendationFromGovernorStartedOnDate));
				}
				if(strWorkflowForRecommendationFromPresidentStartedOnDate!=null&&!strWorkflowForRecommendationFromPresidentStartedOnDate.isEmpty()){
					domain.setWorkflowForRecommendationFromPresidentStartedOn(format.parse(strWorkflowForRecommendationFromPresidentStartedOnDate));
				}
				if(strTaskReceivedOnDate!=null&&!strTaskReceivedOnDate.isEmpty()){
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
				if(strTaskReceivedOnDateForTranslation!=null&&!strTaskReceivedOnDateForTranslation.isEmpty()){
					domain.setTaskReceivedOnForTranslation(format.parse(strTaskReceivedOnDateForTranslation));
				}
				if(strTaskReceivedOnDateForOpinionFromLawAndJD!=null&&!strTaskReceivedOnDateForOpinionFromLawAndJD.isEmpty()){
					domain.setTaskReceivedOnForOpinionFromLawAndJD(format.parse(strTaskReceivedOnDateForOpinionFromLawAndJD));
				}
				if(strTaskReceivedOnDateForRecommendationFromGovernor!=null&&!strTaskReceivedOnDateForRecommendationFromGovernor.isEmpty()){
					domain.setTaskReceivedOnForRecommendationFromGovernor(format.parse(strTaskReceivedOnDateForRecommendationFromGovernor));
				}
				if(strTaskReceivedOnDateForRecommendationFromPresident!=null&&!strTaskReceivedOnDateForRecommendationFromPresident.isEmpty()){
					domain.setTaskReceivedOnForRecommendationFromPresident(format.parse(strTaskReceivedOnDateForRecommendationFromPresident));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = this.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		/**** add/update revised content drafts in domain ****/
		List<TextDraft> revisedContentDrafts = this.updateDraftsOfGivenType(domain, "revised_contentDraft", request);
		domain.setRevisedContentDrafts(revisedContentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = this.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		/**** add/update revised 'statement of object and reason drafts' in domain ****/
		List<TextDraft> revisedStatementOfObjectAndReasonDrafts = this.updateDraftsOfGivenType(domain, "revised_statementOfObjectAndReasonDraft", request);		
		domain.setRevisedStatementOfObjectAndReasonDrafts(revisedStatementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = this.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		/**** add/update revised financial memorandum drafts in domain ****/
		List<TextDraft> revisedFinancialMemorandumDrafts = this.updateDraftsOfGivenType(domain, "revised_financialMemorandumDraft", request);		
		domain.setRevisedFinancialMemorandumDrafts(revisedFinancialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = this.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
		/**** add/update revised statutory memorandum drafts in domain ****/
		List<TextDraft> revisedStatutoryMemorandumDrafts = this.updateDraftsOfGivenType(domain, "revised_statutoryMemorandumDraft", request);
		domain.setRevisedStatutoryMemorandumDrafts(revisedStatutoryMemorandumDrafts);	
		
		/**** add/update annexures for amending bill in domain ****/
		List<TextDraft> annexuresForAmendingBill = this.updateDraftsOfGivenType(domain, "annexureForAmendingBill", request);
		domain.setAnnexuresForAmendingBill(annexuresForAmendingBill);	
		/**** add/update revised annexures for amending bill in domain ****/
		List<TextDraft> revisedAnnexuresForAmendingBill = this.updateDraftsOfGivenType(domain, "revised_annexureForAmendingBill", request);
		domain.setRevisedAnnexuresForAmendingBill(revisedAnnexuresForAmendingBill);
		
		/**** Check For Bill Completeness ****/       
        if(request.getParameter("operation")!=null) {
            if(!request.getParameter("operation").isEmpty()) {
                if(operation.equals("startworkflow")) {
                    if(domain.getReferencedBill()==null 
                    		&& domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_RECOMMEND_REJECTION)) {
                    	domain.setIsIncomplete(true);
                    } else {
                    	domain.setIsIncomplete(false);
                    }
                }
            }
        }
		
        if(bill==null) {
			bill = Bill.findById(Bill.class, domain.getId());
		}
		domain.setVotingDetails(bill.getVotingDetails());
		
		/**** status updation activities if any ****/
        Status statusForBeginningStatusUpdation = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED, domain.getLocale());
		if(domain.getInternalStatus().getPriority()>=statusForBeginningStatusUpdation.getPriority()) {
			performActionForStatusUpdation(domain, request);
		}					
	}
	
	private void performActionForStatusUpdation(Bill domain, final HttpServletRequest request) {
		if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED)
				&&domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
			/**** update internal status ****/
			Status expectedInternalStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, domain.getLocale());
			domain.setInternalStatus(expectedInternalStatus);			
		} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION)
				&&domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_PASSEDBYBOTHHOUSES)) {
			/**** update bill status ****/
			Status finalStatus = Status.findByType(ApplicationConstants.BILL_FINAL_PASSED, domain.getLocale());
			domain.setStatus(finalStatus);
			domain.setInternalStatus(finalStatus);			
		} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION)
				&&
				(domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_NEGATIVED)
				&&domain.getRecommendationStatus().getType().endsWith(ApplicationConstants.BILL_FIRST_HOUSE))
			) {
			/**** update bill status ****/
			Status finalStatus = Status.findByType(ApplicationConstants.BILL_FINAL_NEGATIVED, domain.getLocale());
			domain.setStatus(finalStatus);
			domain.setInternalStatus(finalStatus);			
		} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION)
				&&domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_WITHDRAWN)) {
			/**** update bill status ****/
			Status finalStatus = Status.findByType(ApplicationConstants.BILL_FINAL_WITHDRAWN, domain.getLocale());
			domain.setStatus(finalStatus);
			domain.setInternalStatus(finalStatus);			
		} else if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION)
				&&domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_LAPSED)) {
			/**** update bill status ****/
			Status finalStatus = Status.findByType(ApplicationConstants.BILL_FINAL_LAPSED, domain.getLocale());
			domain.setStatus(finalStatus);
			domain.setInternalStatus(finalStatus);	
		}
	}

	@Override
	protected void populateAfterUpdate(final ModelMap model, final Bill domain,
			final HttpServletRequest request) {
		request.getSession().setAttribute("role",request.getParameter("role"));
		request.getSession().setAttribute("usergroup",request.getParameter("usergroup"));
		request.getSession().setAttribute("usergroupType",request.getParameter("usergroupType"));
		/**** Approval Workflow ****/
		String operation=request.getParameter("operation");
		if(operation!=null){
			if(!operation.isEmpty()){
				/**** Supporting Member Workflow ****/
				if(operation.equals("approval")){
					/**** Added by Sandeep Singh ****/
					/**** Supporting Member Workflow is started ****/
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",domain.getType().getType());
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Workflow Details Entries are created ****/
					List<Task> tasks=processService.getCurrentTasks(processInstance);					
					List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,"");
					/**** Not Send supporting members status are changed to pending ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					List<SupportingMember> supportingMembers=bill.getSupportingMembers();
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
					String nextuser=request.getParameter("actorForWorkflow");
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
					String endflag=domain.getEndFlagForTranslation();
					properties.put("pv_endflag",endflag);	
					properties.put("pv_timerflag", "off");
					properties.put("pv_mailflag", "off");					
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.TRANSLATION_WORKFLOW,nextUserGroupType,level);
								bill.setWorkflowDetailsIdForTranslation(workflowDetails.getId());
							}
						}
					}
					/**** Workflow Started ****/
					bill.setTranslationWorkflowStarted("YES");					
					bill.setWorkflowForTranslationStartedOn(new Date());					
					bill.setTaskReceivedOnForTranslation(new Date());	
					bill.setActorForTranslation(request.getParameter("actorForWorkflow"));
					bill.setLocalizedActorNameForTranslation(request.getParameter("localizedActorNameForWorkflow"));
					bill.setLevelForTranslation(request.getParameter("levelForWorkflow"));
					bill.setRemarksForTranslation(domain.getRemarks());
					/**** If question is sent individually then its file's parameters is set to null i.e 
					 * it is removed from file ****/
//					bill.setFile(null);
//					bill.setFileIndex(null);
//					bill.setFileSent(false);
					bill.simpleMerge();					
				} else if(operation.equals("sendForOpinionFromLawAndJD")){					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actorForWorkflow");
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
					String endflag=domain.getEndFlagForOpinionFromLawAndJD();
					properties.put("pv_endflag",endflag);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW,nextUserGroupType,level);
								bill.setWorkflowDetailsIdForOpinionFromLawAndJD(workflowDetails.getId());
							}
						}
					}
					/**** Workflow Started ****/
					bill.setOpinionFromLawAndJDWorkflowStarted("YES");					
					bill.setWorkflowForOpinionFromLawAndJDStartedOn(new Date());					
					bill.setTaskReceivedOnForOpinionFromLawAndJD(new Date());	
					bill.setActorForOpinionFromLawAndJD(request.getParameter("actorForWorkflow"));
					bill.setLocalizedActorNameForOpinionFromLawAndJD(request.getParameter("localizedActorNameForWorkflow"));
					bill.setLevelForOpinionFromLawAndJD(request.getParameter("levelForWorkflow"));
					/**** If question is sent individually then its file's parameters is set to null i.e 
					 * it is removed from file ****/
//					bill.setFile(null);
//					bill.setFileIndex(null);
//					bill.setFileSent(false);
					bill.simpleMerge();					
				} else if(operation.equals("sendForRecommendationFromGovernor")){					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actorForWorkflow");
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
					String endflag=domain.getEndFlagForRecommendationFromGovernor();
					properties.put("pv_endflag",endflag);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW,nextUserGroupType,level);
								bill.setWorkflowDetailsIdForRecommendationFromGovernor(workflowDetails.getId());
							}
						}
					}
					/**** Workflow Started ****/
					bill.setRecommendationFromGovernorWorkflowStarted("YES");					
					bill.setWorkflowForRecommendationFromGovernorStartedOn(new Date());					
					bill.setTaskReceivedOnForRecommendationFromGovernor(new Date());	
					bill.setActorForRecommendationFromGovernor(request.getParameter("actorForWorkflow"));
					bill.setLocalizedActorNameForRecommendationFromGovernor(request.getParameter("localizedActorNameForWorkflow"));
					bill.setLevelForRecommendationFromGovernor(request.getParameter("levelForWorkflow"));
					/**** If question is sent individually then its file's parameters is set to null i.e 
					 * it is removed from file ****/
//					bill.setFile(null);
//					bill.setFileIndex(null);
//					bill.setFileSent(false);
					bill.simpleMerge();					
				} else if(operation.equals("sendForRecommendationFromPresident")){					
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actorForWorkflow");
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
					String endflag=domain.getEndFlagForRecommendationFromPresident();
					properties.put("pv_endflag",endflag);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW,nextUserGroupType,level);
								bill.setWorkflowDetailsIdForRecommendationFromPresident(workflowDetails.getId());
							}
						}
					}
					/**** Workflow Started ****/
					bill.setRecommendationFromPresidentWorkflowStarted("YES");					
					bill.setWorkflowForRecommendationFromPresidentStartedOn(new Date());					
					bill.setTaskReceivedOnForRecommendationFromPresident(new Date());	
					bill.setActorForRecommendationFromPresident(request.getParameter("actorForWorkflow"));
					bill.setLocalizedActorNameForRecommendationFromPresident(request.getParameter("localizedActorNameForWorkflow"));
					bill.setLevelForRecommendationFromPresident(request.getParameter("levelForWorkflow"));
					/**** If question is sent individually then its file's parameters is set to null i.e 
					 * it is removed from file ****/
//					bill.setFile(null);
//					bill.setFileIndex(null);
//					bill.setFileSent(false);
					bill.simpleMerge();					
				} else if(operation.equals("startworkflow")){
					ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
					Map<String,String> properties=new HashMap<String, String>();					
					/**** Next user and usergroup ****/
					String nextuser=request.getParameter("actorForWorkflow");
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
					String endflag=domain.getEndFlag();
					properties.put("pv_endflag",endflag);	
					properties.put("pv_deviceId",String.valueOf(domain.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
					ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
					/**** Stale State Exception ****/
					Bill bill=Bill.findById(Bill.class,domain.getId());
					/**** Process Started and task created ****/
					Task task=processService.getCurrentTask(processInstance);
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								String workflowDetailsType = "";
								if(domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_RECOMMEND_NAMECLUBBING)) {
									workflowDetailsType = ApplicationConstants.NAMECLUBBING_WORKFLOW;
								} else {
									workflowDetailsType = ApplicationConstants.APPROVAL_WORKFLOW;
								}
								WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,workflowDetailsType,nextUserGroupType,level);
								bill.setWorkflowDetailsId(workflowDetails.getId());
							}
						}
					}
					/**** Workflow Started ****/
					bill.setWorkflowStarted("YES");
					bill.setWorkflowStartedOn(new Date());
					bill.setTaskReceivedOn(new Date());
					bill.setActor(request.getParameter("actorForWorkflow"));
					bill.setLocalizedActorName(request.getParameter("localizedActorNameForWorkflow"));
					bill.setLevel(request.getParameter("levelForWorkflow"));
					/**** If question is sent individually then its file's parameters is set to null i.e 
					 * it is removed from file ****/
					bill.setFile(null);
					bill.setFileIndex(null);
					bill.setFileSent(false);
					
					if(domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_CHECKLIST_PARAMETER).equals("yes") 
							&& domain.getRecommendationFromGovernorStatus().getType().equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND)) {						
						processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						properties=new HashMap<String, String>();					
						/**** Next user and usergroup ****/
						Status expectedStatus = Status.findByType(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_GOVERNOR, domain.getLocale());
						int startingLevel = 1;
						String localizedActorName = "";
						String strStartingUserGroup=request.getParameter("usergroup");
						if(expectedStatus!=null && strStartingUserGroup!=null) {
							UserGroup startingUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strStartingUserGroup));
							List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(domain,expectedStatus,startingUserGroup,startingLevel,domain.getLocale());
							if(eligibleActors!=null && !eligibleActors.isEmpty()) {
								nextuser=eligibleActors.get(0).getId();
								nextUserGroupType="";
								level="";
								if(nextuser!=null){						
									if(!nextuser.isEmpty()){
										String[] temp=nextuser.split("#");
										properties.put("pv_user",temp[0]);
										nextUserGroupType=temp[1];
										level=temp[2];
										localizedActorName=temp[3]+"("+temp[4]+")";
									}
								}
								endflag=domain.getEndFlagForRecommendationFromGovernor();
								properties.put("pv_endflag",endflag);	
								properties.put("pv_deviceId",String.valueOf(domain.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
								processInstance=processService.createProcessInstance(processDefinition, properties);
								/**** Process Started and task created ****/
								task=processService.getCurrentTask(processInstance);
								if(endflag!=null){
									if(!endflag.isEmpty()){
										if(endflag.equals("continue")){
											/**** Workflow Detail entry made only if its not the end of workflow ****/
											domain.setRecommendationFromGovernorStatus(expectedStatus);
											WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW,nextUserGroupType,level);
											bill.setWorkflowDetailsIdForRecommendationFromGovernor(workflowDetails.getId());
										}
									}
								}
								/**** Workflow Started ****/
								bill.setRecommendationFromGovernorStatus(expectedStatus);
								bill.setRecommendationFromGovernorWorkflowStarted("YES");					
								bill.setWorkflowForRecommendationFromGovernorStartedOn(new Date());					
								bill.setTaskReceivedOnForRecommendationFromGovernor(new Date());	
								bill.setActorForRecommendationFromGovernor(nextuser);
								bill.setLocalizedActorNameForRecommendationFromGovernor(localizedActorName);
								bill.setLevelForRecommendationFromGovernor(level);
							}							
						}						
					}
					
					if(domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_CHECKLIST_PARAMETER).equals("yes") 
							&& domain.getRecommendationFromPresidentStatus().getType().equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND)) {						
						processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						properties=new HashMap<String, String>();					
						/**** Next user and usergroup ****/
						Status expectedStatus = Status.findByType(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_PRESIDENT, domain.getLocale());
						int startingLevel = 1;
						String localizedActorName = "";
						String strStartingUserGroup=request.getParameter("usergroup");
						if(expectedStatus!=null && strStartingUserGroup!=null) {
							UserGroup startingUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strStartingUserGroup));
							List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(domain,expectedStatus,startingUserGroup,startingLevel,domain.getLocale());
							if(eligibleActors!=null && !eligibleActors.isEmpty()) {
								nextuser=eligibleActors.get(0).getId();
								nextUserGroupType="";
								level="";
								if(nextuser!=null){						
									if(!nextuser.isEmpty()){
										String[] temp=nextuser.split("#");
										properties.put("pv_user",temp[0]);
										nextUserGroupType=temp[1];
										level=temp[2];
										localizedActorName=temp[3]+"("+temp[4]+")";
									}
								}
								endflag=domain.getEndFlagForRecommendationFromPresident();
								properties.put("pv_endflag",endflag);	
								properties.put("pv_deviceId",String.valueOf(domain.getId()));
								properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));
								processInstance=processService.createProcessInstance(processDefinition, properties);
								/**** Process Started and task created ****/
								task=processService.getCurrentTask(processInstance);
								if(endflag!=null){
									if(!endflag.isEmpty()){
										if(endflag.equals("continue")){
											/**** Workflow Detail entry made only if its not the end of workflow ****/
											domain.setRecommendationFromPresidentStatus(expectedStatus);
											WorkflowDetails workflowDetails = WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW,nextUserGroupType,level);
											bill.setWorkflowDetailsIdForRecommendationFromPresident(workflowDetails.getId());
										}
									}
								}
								/**** Workflow Started ****/
								bill.setRecommendationFromPresidentStatus(expectedStatus);
								bill.setRecommendationFromPresidentWorkflowStarted("YES");					
								bill.setWorkflowForRecommendationFromPresidentStartedOn(new Date());					
								bill.setTaskReceivedOnForRecommendationFromPresident(new Date());	
								bill.setActorForRecommendationFromPresident(nextuser);
								bill.setLocalizedActorNameForRecommendationFromPresident(localizedActorName);
								bill.setLevelForRecommendationFromPresident(level);
							}							
						}						
					}
					
					bill.simpleMerge();				
				}			
			}
		}
	}
	
	/*
	 * This method is used to view the approval status of a bill from the supporting members
	 */
	@RequestMapping(value="/status/{bill}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("bill") final String bill){
		Bill billTemp=Bill.findById(Bill.class,Long.parseLong(bill));
		List<SupportingMember> supportingMembers=billTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "bill/supportingmember";
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
		return "bill/citation";
	}
	
	@RequestMapping(value="/revisions/{billId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("billId")  final Long billId,
			final ModelMap model, final HttpServletRequest request){
		List<Object[]> drafts = new ArrayList<Object[]>();
		String thingToBeRevised = request.getParameter("thingToBeRevised");		
		if(thingToBeRevised!=null && !thingToBeRevised.isEmpty()) {
			drafts = Bill.getRevisions(billId, thingToBeRevised, locale.toString());
			Bill b = Bill.findById(Bill.class, billId);
			if(b != null){
				if(b.getType() != null){
					if(b.getType().getType() != null && !(b.getType().getType().isEmpty())){
						model.addAttribute("selectedDeviceType", b.getType().getType());
						String defaultTitleLanguage = b.getSession().getParameter(b.getType().getType()+"_defaultTitleLanguage");
						model.addAttribute("defaultTitleLanguage", defaultTitleLanguage);
						model.addAttribute("drafts",drafts);
						if(thingToBeRevised.equals("checklist")) {
							List<String> draftRevisedByHeaders = new ArrayList<String>();
							String editedOnForFirstDraft = drafts.get(0)[3].toString();
							for(Object[] i: drafts) {
								if((!i.equals(drafts.get(0))) && i[3].toString().equals(editedOnForFirstDraft)) {
									break;
								} else {
									String draftRevisedByHeader = i[1].toString() + 
											"<br/>(" + i[2].toString() + "-" + i[3].toString() + ")" +
											"<br/>" + i[4].toString();
									draftRevisedByHeaders.add(draftRevisedByHeader);
								}
							}
							model.addAttribute("draftRevisedByHeaders",draftRevisedByHeaders);
							return "bill/checklist_revisions";
						} else {
							return "bill/revisions";
						}						
					} else {
						logger.error("devicetype with id=" + b.getType().getId() + " has empty or null type attribute");
						model.addAttribute("errorcode","some_error");
						return "bill/error";
					}
				} else {
					logger.error("bill with id=" + b.getId() + " has no devicetype");
					model.addAttribute("errorcode","some_error");
					return "bill/error";
				}
			} else {
				logger.error("bill with id=" + billId + " does not exist.");
				model.addAttribute("errorcode","some_error");
				return "bill/error";
			}
		} else {
			logger.error("request parameter 'thing to be revised' is not set.");
			model.addAttribute("errorcode","some_error");
			return "bill/error";
		}		
	}
	
	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "bill/contacts";
	}
	
	/**** Populate Available Status Dates For Given Recommendation Status Update ****/
	@RequestMapping(value="/populateAvailableStatusDates",method=RequestMethod.GET)
	public String populateAvailableStatusDates(final HttpServletRequest request, final Locale locale, final ModelMap model){
		List<String> statusDates = new ArrayList<String>();
		String billId = request.getParameter("billId");
		String statusId = request.getParameter("statusId");
		if(billId!=null && statusId!=null) {
			if(!billId.isEmpty() && !statusId.isEmpty()) {
				Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
				Status status = Status.findById(Status.class, Long.parseLong(statusId));
				if(bill!=null && status!=null) {
					if(bill.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
						if(status.getType().equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
							String discussionDatesStr = bill.getSession().getParameter(ApplicationConstants.NONOFFICIAL_BILL+"_discussionDates");
							if(discussionDatesStr!=null) {
								for(String discussionDateStr: discussionDatesStr.split("#")) {
									Date discussionDate = FormaterUtil.formatStringToDate(discussionDateStr, ApplicationConstants.DB_DATEFORMAT, bill.getLocale());
//									if(DateUtil.compareDatePartOnly(discussionDate, new Date())>=0) {
//										statusDates.add(FormaterUtil.formatDateToString(discussionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
//									}	
									statusDates.add(FormaterUtil.formatDateToString(discussionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
								}
							}
						} else if(status.getType().startsWith(ApplicationConstants.BILL_PROCESSED_CONSIDERED)) {
							Date expectedDiscussionDate = bill.getExpectedDiscussionDate();
							if(expectedDiscussionDate!=null) {
								statusDates.add(FormaterUtil.formatDateToString(expectedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
							}							
						}
					} else if(bill.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(status.getType().equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
							Date expectedIntroductionDate = bill.getExpectedIntroductionDate();
							if(expectedIntroductionDate!=null) {
								statusDates.add(FormaterUtil.formatDateToString(expectedIntroductionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
							}
						} else if(status.getType().startsWith(ApplicationConstants.BILL_PROCESSED_CONSIDERED)) {
							Date expectedDiscussionDate = bill.getExpectedDiscussionDate();
							if(expectedDiscussionDate!=null) {
								statusDates.add(FormaterUtil.formatDateToString(expectedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
							}							
						}
					}
				}
			}
		}		
		model.addAttribute("statusDates",statusDates);
		return "bill/status_dates";
	}
	
	private boolean populateAllTypesOfDrafts(ModelMap model, Bill domain, Session selectedSession, DeviceType deviceType) {
		/**** titles, content drafts, 'statement of object and reason' drafts, memorandum drafts, annexures ****/			
		String languagesAllowedInSession = selectedSession.getParameter(deviceType.getType() + "_languagesAllowed");
		if(languagesAllowedInSession != null && !languagesAllowedInSession.isEmpty()) {
			List<Language> languagesAllowedForTitle = new ArrayList<Language>();
			List<Language> languagesAllowedForContentDraft = new ArrayList<Language>();
			List<Language> languagesAllowedForSORDraft = new ArrayList<Language>();
			List<Language> languagesAllowedForFinancialMemorandumDraft = new ArrayList<Language>();
			List<Language> languagesAllowedForStatutoryMemorandumDraft = new ArrayList<Language>();
			List<Language> languagesAllowedForAnnexureForAmendingBill = new ArrayList<Language>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {			
				Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
				languagesAllowedForTitle.add(languageAllowed);
				languagesAllowedForContentDraft.add(languageAllowed);
				languagesAllowedForSORDraft.add(languageAllowed);
				languagesAllowedForFinancialMemorandumDraft.add(languageAllowed);
				languagesAllowedForStatutoryMemorandumDraft.add(languageAllowed);					
				languagesAllowedForAnnexureForAmendingBill.add(languageAllowed);						
			}
			List<TextDraft> titles = new ArrayList<TextDraft>();			
			if(domain.getTitles()!=null && !domain.getTitles().isEmpty()) {				
				titles.addAll(domain.getTitles());
				for(TextDraft title: domain.getTitles()) {
					languagesAllowedForTitle.remove(title.getLanguage());					
				}				
			}
			if(!languagesAllowedForTitle.isEmpty()) {								
				for(Language languageAllowedForTitle: languagesAllowedForTitle) {
					TextDraft title = new TextDraft();
					title.setLanguage(languageAllowedForTitle);
					title.setText("");
					titles.add(title);
				}
			}
			model.addAttribute("titles",titles);
			if(domain.getRevisedTitles()!=null && !domain.getRevisedTitles().isEmpty()) {
				for(TextDraft revisedTitle: domain.getRevisedTitles()) {
					model.addAttribute("revisedTitle_"+revisedTitle.getLanguage().getType(), revisedTitle.getText());
					model.addAttribute("revisedTitle_id_"+revisedTitle.getLanguage().getType(), revisedTitle.getId());
				}
			}
			List<TextDraft> contentDrafts = new ArrayList<TextDraft>();			
			if(domain.getContentDrafts()!=null && !domain.getContentDrafts().isEmpty()) {				
				contentDrafts.addAll(domain.getContentDrafts());
				for(TextDraft contentDraft: domain.getContentDrafts()) {
					languagesAllowedForContentDraft.remove(contentDraft.getLanguage());					
				}				
			}
			if(!languagesAllowedForContentDraft.isEmpty()) {								
				for(Language languageAllowedForContentDraft: languagesAllowedForContentDraft) {
					TextDraft contentDraft = new TextDraft();
					contentDraft.setLanguage(languageAllowedForContentDraft);
					contentDraft.setText("");
					contentDrafts.add(contentDraft);
				}
			}
			model.addAttribute("contentDrafts",contentDrafts);
			if(domain.getRevisedContentDrafts()!=null && !domain.getRevisedContentDrafts().isEmpty()) {
				for(TextDraft revisedContentDraft: domain.getRevisedContentDrafts()) {
					model.addAttribute("revisedContentDraft_"+revisedContentDraft.getLanguage().getType(), revisedContentDraft.getText());
					model.addAttribute("revisedContentDraft_id_"+revisedContentDraft.getLanguage().getType(), revisedContentDraft.getId());
				}
			}
			List<TextDraft> statementOfObjectAndReasonDrafts = new ArrayList<TextDraft>();	
			if(domain.getStatementOfObjectAndReasonDrafts()!=null && !domain.getStatementOfObjectAndReasonDrafts().isEmpty()) {
				statementOfObjectAndReasonDrafts.addAll(domain.getStatementOfObjectAndReasonDrafts());
				for(TextDraft statementOfObjectAndReasonDraft: domain.getStatementOfObjectAndReasonDrafts()) {
					languagesAllowedForSORDraft.remove(statementOfObjectAndReasonDraft.getLanguage());
				}
			}
			if(!languagesAllowedForSORDraft.isEmpty()) {
				for(Language languageAllowedForSOR: languagesAllowedForSORDraft) {
					TextDraft statementOfObjectAndReasonDraft = new TextDraft();
					statementOfObjectAndReasonDraft.setLanguage(languageAllowedForSOR);
					statementOfObjectAndReasonDraft.setText("");
					statementOfObjectAndReasonDrafts.add(statementOfObjectAndReasonDraft);
				}
			}
			model.addAttribute("statementOfObjectAndReasonDrafts",statementOfObjectAndReasonDrafts);
			if(domain.getRevisedStatementOfObjectAndReasonDrafts()!=null && !domain.getRevisedStatementOfObjectAndReasonDrafts().isEmpty()) {
				for(TextDraft revisedStatementOfObjectAndReasonDraft: domain.getRevisedStatementOfObjectAndReasonDrafts()) {
					model.addAttribute("revisedStatementOfObjectAndReasonDraft_"+revisedStatementOfObjectAndReasonDraft.getLanguage().getType(), revisedStatementOfObjectAndReasonDraft.getText());
					model.addAttribute("revisedStatementOfObjectAndReasonDraft_id_"+revisedStatementOfObjectAndReasonDraft.getLanguage().getType(), revisedStatementOfObjectAndReasonDraft.getId());
				}
			}
			List<TextDraft> financialMemorandumDrafts = new ArrayList<TextDraft>();					
			if(domain.getFinancialMemorandumDrafts()!=null && !domain.getFinancialMemorandumDrafts().isEmpty()) {
				financialMemorandumDrafts.addAll(domain.getFinancialMemorandumDrafts());
				for(TextDraft financialMemorandumDraft: domain.getFinancialMemorandumDrafts()) {
					languagesAllowedForFinancialMemorandumDraft.remove(financialMemorandumDraft.getLanguage());
				}				
			}
			if(!languagesAllowedForFinancialMemorandumDraft.isEmpty()) {								
				for(Language languageAllowedForFinancialMemorandum: languagesAllowedForFinancialMemorandumDraft) {
					TextDraft financialMemorandumDraft = new TextDraft();
					financialMemorandumDraft.setLanguage(languageAllowedForFinancialMemorandum);
					financialMemorandumDraft.setText("");
					financialMemorandumDrafts.add(financialMemorandumDraft);
				}
			}
			model.addAttribute("financialMemorandumDrafts",financialMemorandumDrafts);
			if(domain.getRevisedFinancialMemorandumDrafts()!=null && !domain.getRevisedFinancialMemorandumDrafts().isEmpty()) {
				for(TextDraft revisedFinancialMemorandumDraft: domain.getRevisedFinancialMemorandumDrafts()) {
					model.addAttribute("revisedFinancialMemorandumDraft_"+revisedFinancialMemorandumDraft.getLanguage().getType(), revisedFinancialMemorandumDraft.getText());
					model.addAttribute("revisedFinancialMemorandumDraft_id_"+revisedFinancialMemorandumDraft.getLanguage().getType(), revisedFinancialMemorandumDraft.getId());
				}
			}
			List<TextDraft> statutoryMemorandumDrafts = new ArrayList<TextDraft>();					
			if(domain.getStatutoryMemorandumDrafts()!=null && !domain.getStatutoryMemorandumDrafts().isEmpty()) {
				statutoryMemorandumDrafts.addAll(domain.getStatutoryMemorandumDrafts());
				for(TextDraft statutoryMemorandumDraft: domain.getStatutoryMemorandumDrafts()) {
					languagesAllowedForStatutoryMemorandumDraft.remove(statutoryMemorandumDraft.getLanguage());
				}				
			}
			if(!languagesAllowedForStatutoryMemorandumDraft.isEmpty()) {								
				for(Language languageAllowedForStatutoryMemorandumDraft: languagesAllowedForStatutoryMemorandumDraft) {
					TextDraft statutoryMemorandumDraft = new TextDraft();
					statutoryMemorandumDraft.setLanguage(languageAllowedForStatutoryMemorandumDraft);
					statutoryMemorandumDraft.setText("");
					statutoryMemorandumDrafts.add(statutoryMemorandumDraft);
				}
			}
			model.addAttribute("statutoryMemorandumDrafts",statutoryMemorandumDrafts);
			if(domain.getRevisedStatutoryMemorandumDrafts()!=null && !domain.getRevisedStatutoryMemorandumDrafts().isEmpty()) {
				for(TextDraft revisedStatutoryMemorandumDraft: domain.getRevisedStatutoryMemorandumDrafts()) {
					model.addAttribute("revisedStatutoryMemorandumDraft_"+revisedStatutoryMemorandumDraft.getLanguage().getType(), revisedStatutoryMemorandumDraft.getText());
					model.addAttribute("revisedStatutoryMemorandumDraft_id_"+revisedStatutoryMemorandumDraft.getLanguage().getType(), revisedStatutoryMemorandumDraft.getId());
				}
			}
			List<TextDraft> annexuresForAmendingBill = new ArrayList<TextDraft>();			
			if(domain.getAnnexuresForAmendingBill()!=null && !domain.getAnnexuresForAmendingBill().isEmpty()) {				
				annexuresForAmendingBill.addAll(domain.getAnnexuresForAmendingBill());
				for(TextDraft annexureForAmendingBill: domain.getAnnexuresForAmendingBill()) {
					languagesAllowedForAnnexureForAmendingBill.remove(annexureForAmendingBill.getLanguage());					
				}				
			}
			if(!languagesAllowedForAnnexureForAmendingBill.isEmpty()) {								
				for(Language languageAllowedForAnnexureForAmendingBill: languagesAllowedForAnnexureForAmendingBill) {
					TextDraft annexureForAmendingBill = new TextDraft();
					annexureForAmendingBill.setLanguage(languageAllowedForAnnexureForAmendingBill);
					annexureForAmendingBill.setText("");
					annexuresForAmendingBill.add(annexureForAmendingBill);
				}
			}
			model.addAttribute("annexuresForAmendingBill",annexuresForAmendingBill);	
			if(domain.getRevisedAnnexuresForAmendingBill()!=null && !domain.getRevisedAnnexuresForAmendingBill().isEmpty()) {
				for(TextDraft revisedAnnexureForAmendingBill: domain.getRevisedAnnexuresForAmendingBill()) {
					model.addAttribute("revisedAnnexureForAmendingBill_"+revisedAnnexureForAmendingBill.getLanguage().getType(), revisedAnnexureForAmendingBill.getText());
					model.addAttribute("revisedAnnexureForAmendingBill_id_"+revisedAnnexureForAmendingBill.getLanguage().getType(), revisedAnnexureForAmendingBill.getId());
				}
			}
			return true;
		} else {
			logger.error("**** Session Parameter '" + deviceType.getType() + "_languagesAllowed' is not set. ****");
			model.addAttribute("errorcode",deviceType.getType() + "__languagesAllowed_notset");
			return false;
		}
	}
	
	private List<TextDraft> updateDraftsOfGivenType(Bill domain, String typeOfDraft, HttpServletRequest request) {
		List<TextDraft> draftsOfGivenType = new ArrayList<TextDraft>();
		String languagesAllowedInSession = domain.getSession().getParameter(domain.getType().getType() + "_languagesAllowed");
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String draftTextInThisLanguage = request.getParameter(typeOfDraft+"_text_"+languageAllowedInSession);
			if(draftTextInThisLanguage!=null && !draftTextInThisLanguage.isEmpty()) {
				TextDraft draftOfGivenType = null;				
				String draftIdInThisLanguage = request.getParameter(typeOfDraft+"_id_"+languageAllowedInSession);
				if(draftIdInThisLanguage!=null && !draftIdInThisLanguage.isEmpty()) {
					draftOfGivenType = TextDraft.findById(TextDraft.class, Long.parseLong(draftIdInThisLanguage));					
				} else {
					draftOfGivenType = new TextDraft();
				}
				draftOfGivenType.setText(draftTextInThisLanguage);
				if(draftOfGivenType.getLanguage()==null) {
					Language thisLanguage;
					String draftLanguageId = request.getParameter(typeOfDraft+"_language_id_"+languageAllowedInSession);
					if(draftLanguageId!=null && !draftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(draftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
					}					
					draftOfGivenType.setLanguage(thisLanguage);
				}
				draftOfGivenType.setLocale(domain.getLocale());
				draftsOfGivenType.add(draftOfGivenType);
			} 
//			else if(draftTextInThisLanguage!=null && draftTextInThisLanguage.isEmpty()) {
//				String draftIdInThisLanguage = request.getParameter(typeOfDraft+"_id_"+languageAllowedInSession);
//				TextDraft draftOfGivenType = null;
//				if(draftIdInThisLanguage!=null && !draftIdInThisLanguage.isEmpty()) {
//					draftOfGivenType = TextDraft.findById(TextDraft.class, Long.parseLong(draftIdInThisLanguage));
//					if(draftOfGivenType!=null) {
//						draftOfGivenType.remove();
//					}
//				}
//			}
		}
		return draftsOfGivenType;	
	}
	
	@RequestMapping(value="/discussbills", method=RequestMethod.POST)
	public void doDiscussBills(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		String[] checks = request.getParameterValues("tobeDiscussed");
		String allIds = request.getParameter("allids");
		try{
			if(checks != null){
				String[] idsAll = allIds.split("#");
				for(String idx: idsAll){
					if(getMatch(idx, checks)){
						String[] idAndSelctedForDiscussion = idx.split(";");
						if(idAndSelctedForDiscussion[1].equals("unchecked")){
							Bill bill = Bill.findById(Bill.class, Long.parseLong(idAndSelctedForDiscussion[0]));
							//set following conditionally if balloting will happen for 2nd time or in 2nd house also
							//clue: u can check on bill's current recommendation status
							Status newRecommendationStatus = null;
							if(bill.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
								newRecommendationStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED + "_" + bill.findFirstHouseType() + "_" + ApplicationConstants.BILL_FIRST_HOUSE, locale.toString());
							} else if(bill.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSED)
									&& bill.getRecommendationStatus().getType().endsWith(ApplicationConstants.BILL_FIRST_HOUSE)) {
								newRecommendationStatus = Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED + "_" + bill.findSecondHouseType() + "_" + ApplicationConstants.BILL_SECOND_HOUSE, locale.toString());
							}								
							bill.setRecommendationStatus(newRecommendationStatus);
							Thread.sleep(100);
							bill.merge();
						}
					}
				}
				
				Thread.sleep(200);
				
				for(String idAndStatus: idsAll){
					String id = idAndStatus.split(";")[0];
					if(!isSelcted(id, checks)){
						Bill bill = Bill.findById(Bill.class, Long.parseLong(id));					
						bill.setDiscussionStatus(null);
						bill.merge();
					}
				}
			}else{
				String[] idsAll = allIds.split("#");
				for(String idx: idsAll){
					String[] idAndSelctedForDiscussion = idx.split(";");
					Bill bill = Bill.findById(Bill.class, Long.parseLong(idAndSelctedForDiscussion[0]));					
					bill.setDiscussionStatus(null);
					Thread.sleep(100);
					bill.merge();
				}
			}
			model.addAttribute("done", "done");
			
		}catch (Exception e) {
			model.addAttribute("done", "fail");
			e.printStackTrace();
		}		
	}
	
	private boolean getMatch(String toBeMatchWith, String[] matches){
		boolean matchFound = false;
		for(int i = 0; i < matches.length; i++){
			if(toBeMatchWith.indexOf(matches[i]) > -1){
				matchFound = true;
				break;
			}
		}
		return matchFound;
	}
	
	private boolean isSelcted(String id, String[] ids){
		boolean isSelected = false;
		
		for (int i = 0; i < ids.length; i++) {
			if(id.equals(ids[i])){
				isSelected = true;
				break;
			}			
		}
		
		return isSelected;
	}
	
	@RequestMapping(value="/viewSchedule7OfConstitution", method=RequestMethod.GET)
	public @ResponseBody void viewSchedule7OfConstitution(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		CustomParameter billCheckListReferenceFileParameter = CustomParameter.findByName(CustomParameter.class, "BILL_CHECKLIST_REFERENCE_FILE", "");
		boolean isBillCheckListReferenceFileParameterSet;
		if(billCheckListReferenceFileParameter==null) {			
			isBillCheckListReferenceFileParameterSet = false;
		} else if(billCheckListReferenceFileParameter.getValue()==null) {
			isBillCheckListReferenceFileParameterSet = false;
		} else if(billCheckListReferenceFileParameter.getValue().isEmpty()) {
			isBillCheckListReferenceFileParameterSet = false;
		} else {
			isBillCheckListReferenceFileParameterSet = true;
		}
		if(isBillCheckListReferenceFileParameterSet==false) {
			logger.error("Custom Parameter 'BILL_CHECKLIST_REFERENCE_FILE' is not set");
			MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.schedule7OfConstitution.notfound", locale.toString());
    		try {
				if(message != null) {
	    			if(!message.getValue().isEmpty()) {
	    				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	    			} else {
	    				response.getWriter().println("<h3>Sorry..Schedule 7 Of Constitution File is not found. Please contact administrator.</h3>");
	    			}
	    		} else {
	    			response.getWriter().println("<h3>Sorry..Schedule 7 Of Constitution File is not found. Please contact administrator.</h3>");
	    		}
				return;
    		} catch(IOException ex) {
    			logger.error("Error in writing to response");
    		}
		}
		File file = File.findByName(File.class, "schedule 7 of constitution", locale.toString());
		if(file!=null) {
			Document document = null;
			try {
				document = Document.findByTag(file.getValue());
			} catch (ELSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        try {
	            response.setContentType(document.getType());
	            response.setContentLength((int) document.getFileSize());
	            response.setHeader("Content-Disposition", "inline; filename=\""
	                    + document.getOriginalFileName() + "\"");
	            FileCopyUtils.copy(
	                    document.getFileData(), response.getOutputStream());
	        } catch (IOException e) {
	            logger.error("Error occured while opening file:" + e.toString());
	        }
		} else {
			MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.schedule7OfConstitution.notfound", locale.toString());
    		try {
				if(message != null) {
	    			if(!message.getValue().isEmpty()) {
	    				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	    			} else {
	    				response.getWriter().println("<h3>Sorry..Schedule 7 Of Constitution File is not found. Please contact administrator.</h3>");
	    			}
	    		} else {
	    			response.getWriter().println("<h3>Sorry..Schedule 7 Of Constitution File is not found. Please contact administrator.</h3>");
	    		}
    		} catch(IOException ex) {
    			logger.error("Error in writing to response");
    		}
		}
	}
	
	@RequestMapping(value="/referAct/init",method=RequestMethod.GET)
	public String initReferAct(final HttpServletRequest request,final ModelMap model, final Locale locale){
		String action = request.getParameter("action");
		if(action != null){
			if(!action.isEmpty()){
				model.addAttribute("action", action);
				if(action.equals("act")){
					CustomParameter firstActYearParameter = CustomParameter.findByName(CustomParameter.class, "FIRST_ACT_YEAR", "");
					if(firstActYearParameter!=null) {
						String firstActYearStr = firstActYearParameter.getValue();
						if(firstActYearStr!=null) {
							if(!firstActYearStr.isEmpty()) {
								int firstActYear = Integer.parseInt(firstActYearStr);
								List<MasterVO> years = new ArrayList<MasterVO>();
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(new Date());
								int currentYear = calendar.get(Calendar.YEAR);
								for(int i=currentYear; i>=firstActYear; i--) {
									MasterVO year = new MasterVO();
									year.setNumber(i);
									year.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));						
									years.add(year);
								}
								model.addAttribute("years", years);										
							} else{
								logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set with empty value. ****");
								model.addAttribute("errorcode", "firstactyearnotset");					
							}
						} else{
							logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set to null. ****");
							model.addAttribute("errorcode", "firstactyearnotset");				
						}		
					} else{
						logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is not set. ****");
						model.addAttribute("errorcode", "firstactyearnotset");			
					}	
				}else if(action.equals("ordinance")){
					CustomParameter firstOrdinanceYearParameter = CustomParameter.findByName(CustomParameter.class, "FIRST_ORDINANCE_YEAR", "");
					if(firstOrdinanceYearParameter!=null) {
						String firstOrdinanceYearStr = firstOrdinanceYearParameter.getValue();
						if(firstOrdinanceYearStr!=null) {
							if(!firstOrdinanceYearStr.isEmpty()) {
								int firstOrdinanceYear = Integer.parseInt(firstOrdinanceYearStr);
								List<MasterVO> years = new ArrayList<MasterVO>();
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(new Date());
								int currentYear = calendar.get(Calendar.YEAR);
								for(int i=currentYear; i>=firstOrdinanceYear; i--) {
									MasterVO year = new MasterVO();
									year.setNumber(i);
									year.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));						
									years.add(year);
								}
								model.addAttribute("years", years);
								
							} else{
								logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set with empty value. ****");
								model.addAttribute("errorcode", "firstactyearnotset");					
							}
						} else{
							logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set to null. ****");
							model.addAttribute("errorcode", "firstactyearnotset");				
						}		
					} else{
						logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is not set. ****");
						model.addAttribute("errorcode", "firstactyearnotset");			
					}	
				}
					
			}
		}	
		return "bill/referactinit";
	}
	
	@RequestMapping(value="/referAct/search",method=RequestMethod.POST)
    public @ResponseBody List<ActSearchVO> searchActForReferring(final HttpServletRequest request,final ModelMap model,final Locale locale){
		List<ActSearchVO> actSearchVOs = new ArrayList<ActSearchVO>();
		String param=request.getParameter("param").trim();
		String actYear=request.getParameter("refYear");
		String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(param!=null&&actYear!=null&&start!=null&&noOfRecords!=null){
        	if((!param.isEmpty())&&(!actYear.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
        		CustomParameter actDefaultLanguageParameter = CustomParameter.findByName(CustomParameter.class, "ACT_DEFAULT_LANGUAGE", "");
            	if(actDefaultLanguageParameter!=null) {
            		String actDefaultLanguage = actDefaultLanguageParameter.getValue();
            		if(actDefaultLanguage!=null) {
            			if(!actDefaultLanguage.isEmpty()) {
            				model.addAttribute("actDefaultLanguage", actDefaultLanguage);
            				actSearchVOs = Bill.fullTextSearchActForReferring(param, actYear, actDefaultLanguage, start, noOfRecords);
//            				Act act = Act.findById(Act.class, new Long(50));
//            				ActSearchVO actSearchVO = new ActSearchVO();
//            				actSearchVO.setId(act.getId());
//            				actSearchVO.setNumber(String.valueOf(act.getNumber()));
//            				actSearchVO.setTitle(act.getDefaultTitle());
//            				actSearchVO.setYear(String.valueOf(act.getYear()));
//            				actSearchVO.setFileEnglish(act.getFileEnglish());
//            				actSearchVO.setFileMarathi(act.getFileMarathi());
//            				actSearchVO.setFileHindi(act.getFileHindi());
//            				actSearchVOs.add(actSearchVO);
            			}
            		}
            	}            	
        	}
        }
		return actSearchVOs;
	}
	
	@RequestMapping(value="/referOrdinance/search",method=RequestMethod.POST)
    public @ResponseBody List<OrdinanceSearchVO> searchOrdinanceForReferring(final HttpServletRequest request,final ModelMap model,final Locale locale){
		List<OrdinanceSearchVO> ordSearchVOs = new ArrayList<OrdinanceSearchVO>();
		String param=request.getParameter("param").trim();
		String ordYear=request.getParameter("refYear");
		String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(param!=null&&ordYear!=null&&start!=null&&noOfRecords!=null){
        	if((!param.isEmpty())&&(!ordYear.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
        		CustomParameter actDefaultLanguageParameter = CustomParameter.findByName(CustomParameter.class, "ACT_DEFAULT_LANGUAGE", "");
            	if(actDefaultLanguageParameter!=null) {
            		String actDefaultLanguage = actDefaultLanguageParameter.getValue();
            		if(actDefaultLanguage!=null) {
            			if(!actDefaultLanguage.isEmpty()) {
            				model.addAttribute("actDefaultLanguage", actDefaultLanguage);
            				ordSearchVOs = Bill.fullTextSearchOrdinanceForReferring(param, ordYear, actDefaultLanguage, start, noOfRecords);
            			}
            		}
            	}            	
        	}
        }
		return ordSearchVOs;
	}
	
	@RequestMapping(value="/generateDocketReport",method=RequestMethod.GET)
    public @ResponseBody void generateDocketReport(final HttpServletRequest request,final HttpServletResponse response,final Locale locale){
		String billId = request.getParameter("billId");
		String requisitionFor = request.getParameter("requisitionFor");
		String languageForDocketReport = request.getParameter("languageForDocketReport");
		String optionalFieldsForDocket = request.getParameter("optionalFieldsForDocket");
		if(billId!=null && requisitionFor!=null && languageForDocketReport!=null) {
			List<Object> reportDataAsList = new ArrayList<Object>();
			reportDataAsList.addAll(Bill.findBillDataForDocketReport(billId, languageForDocketReport));
			//reportDataAsList = Bill.findBillDataForDocketReport(billId, languageForDocketReport);
			String languageLocale = "";
			if(languageForDocketReport.equals("marathi")) {
				languageLocale = "mr_IN";
			} else if(languageForDocketReport.equals("hindi")) {
				languageLocale = "hi_IN";
			} else {
				languageLocale = "en_US";
			}
			String billNumber = reportDataAsList.get(1).toString();
			reportDataAsList.add(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(billNumber), languageLocale));
			reportDataAsList.add(RomanNumeral.getRomanEquivalent(Long.parseLong(billNumber)));	
			reportDataAsList.add(optionalFieldsForDocket);
			String billYear = reportDataAsList.get(2).toString();
			reportDataAsList.add(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(billYear), languageLocale));
			List<User> principalSecretaries = User.findByRole(false, "BIS_PRINCIPALSECRETARY", locale.toString());
			if(principalSecretaries!=null) {
				if(!principalSecretaries.isEmpty()) {
					if(principalSecretaries.size()==1) {
						reportDataAsList.add(principalSecretaries.get(0).findFirstLastName());
					} else {
						
					}
				}
			}
			//temporary way of getting bill device type by finding bill again
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
			if(bill!=null) {
				if(bill.getType()!=null) {
					reportDataAsList.add(bill.getType().getType());
				}
			}
			//generate report
			java.io.File reportFile = null;
			if(requisitionFor.equals(ApplicationConstants.BILL_PRESS_COPY)) {
				try {
					reportFile = generateReportUsingFOP(reportDataAsList.toArray(), "template_bill_docket_report", "WORD", "bill_docket_report", locale.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(requisitionFor.equals(ApplicationConstants.BILL_GAZETTE_COPY)) {
				try {
					reportFile = generateReportUsingFOP(reportDataAsList.toArray(), "template_bill_gazette_report", "WORD", "bill_gazette_report", locale.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
		}
	}
	
//	@RequestMapping(value="/generateGazetteReport",method=RequestMethod.GET)
//    public @ResponseBody void generateGazetteReport(final HttpServletRequest request,final HttpServletResponse response,final Locale locale){
//		String billId = request.getParameter("billId");
//		String languageForGazetteReport = request.getParameter("languageForGazetteReport");
//		String optionalFieldsForGazette = request.getParameter("optionalFieldsForGazette");
//		if(billId!=null && languageForGazetteReport!=null) {
//			List<Object> reportDataAsList = new ArrayList<Object>();
//			reportDataAsList.addAll(Bill.findBillDataForDocketReport(billId, languageForGazetteReport));
//			//reportDataAsList = Bill.findBillDataForDocketReport(billId, languageForDocketReport);
//			String languageLocale = "";
//			if(languageForGazetteReport.equals("marathi")) {
//				languageLocale = "mr_IN";
//			} else if(languageForGazetteReport.equals("hindi")) {
//				languageLocale = "hi_IN";
//			} else {
//				languageLocale = "en_US";
//			}
//			String billNumber = reportDataAsList.get(1).toString();
//			reportDataAsList.add(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(billNumber), languageLocale));
//			reportDataAsList.add(RomanNumeral.getRomanEquivalent(Long.parseLong(billNumber)));	
//			reportDataAsList.add(optionalFieldsForGazette);
//			String billYear = reportDataAsList.get(2).toString();
//			reportDataAsList.add(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(billYear), languageLocale));
//			List<User> principalSecretaries = User.findByRole(false, "BIS_PRINCIPALSECRETARY", locale.toString());
//			if(principalSecretaries!=null) {
//				if(!principalSecretaries.isEmpty()) {
//					if(principalSecretaries.size()==1) {
//						reportDataAsList.add(principalSecretaries.get(0).findFirstLastName());
//					} else {
//						
//					}
//				}
//			}
//			//generate report
//			java.io.File reportFile = generateReportUsingFOP(reportDataAsList.toArray(), "template_bill_gazette_report", "WORD", "bill_gazette_report", locale.toString());
//			openOrSaveReportFileFromBrowser(response, reportFile, "WORD");
//		}
//	}
	
	@RequestMapping(value="/viewPressCopiesForGivenPrintRequisition",method=RequestMethod.GET)
    public String viewPressCopiesForGivenPrintRequisition(final ModelMap model, final HttpServletRequest request, final Locale locale){
		String returnPath = "bill/error";
		String billId = request.getParameter("billId");
		String requisitionFor = request.getParameter("requisitionFor");
		String billStatus = request.getParameter("billStatus");
		if(billId!=null && requisitionFor!=null && billStatus!=null) {
			if(!billId.isEmpty() && !requisitionFor.isEmpty() && !billStatus.isEmpty()) {
				Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
				printRequisitionIdentifiers.put("deviceId", billId);
				printRequisitionIdentifiers.put("requisitionFor", requisitionFor);				
				printRequisitionIdentifiers.put("status", billStatus);
				PrintRequisition printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, locale.toString());
				model.addAttribute("requisitionFor", requisitionFor);	
				model.addAttribute("billStatusForRequisition", billStatus);
				if(printRequisition!=null) {					
					if(printRequisition.getPressCopyEnglish()!=null) {
						if(!printRequisition.getPressCopyEnglish().isEmpty()) {
							model.addAttribute("pressCopyEnglish", printRequisition.getPressCopyEnglish());
						}
					}
					if(printRequisition.getPressCopyMarathi()!=null) {
						if(!printRequisition.getPressCopyMarathi().isEmpty()) {
							model.addAttribute("pressCopyMarathi", printRequisition.getPressCopyMarathi());
						}
					}
					if(printRequisition.getPressCopyHindi()!=null) {
						if(!printRequisition.getPressCopyHindi().isEmpty()) {
							model.addAttribute("pressCopyHindi", printRequisition.getPressCopyHindi());
						}
					}	
					model.addAttribute("pressCopiesReceived", "yes");
				} else {
					model.addAttribute("pressCopiesReceived", "no");
				}
				returnPath = "bill/presscopies";
			} else {
				logger.error("request parameter cannot be empty.");
				model.addAttribute("errorcode","some_error");				
			}
		} else {
			logger.error("request parameter cannot be null.");
			model.addAttribute("errorcode","some_error");			
		}
		return returnPath;
	}
	
	@RequestMapping(value="/providedate", method=RequestMethod.GET)
	public String provideIntroductionDate(final ModelMap model, final HttpServletRequest request, final Locale locale){
		String strHouseType = request.getParameter("houseType");
		
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strUserGroupType = request.getParameter("usergrouptype");
		
		Session session = null;
		
		if(strUserGroupType == null){
			for(UserGroup ug : this.getCurrentUser().getUserGroups()){
				strUserGroupType = ug.getUserGroupType().getType();
				break;
			}
		}
		
		model.addAttribute("usergroupType", strUserGroupType);
		
		/**** To put the rounds to be selected ****/
		CustomParameter roundsForBill = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
		if(roundsForBill == null){
			roundsForBill = CustomParameter.findByName(CustomParameter.class, "DEFAULT_HOUSEROUNDS", "");
		}
		if(roundsForBill != null){
			if(roundsForBill.getValue() != null && !roundsForBill.getValue().isEmpty()){
				List<MasterVO> rounds = new ArrayList<MasterVO>();
				int maxRound = Integer.parseInt(roundsForBill.getValue());
				for(int i = 1; i <= maxRound; i++){
					MasterVO mVO = new MasterVO();
					mVO.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
					mVO.setValue(String.valueOf(i));
					
					rounds.add(mVO);
				}
				
				model.addAttribute("rounds", rounds);
			}
		}
		
		if(strHouseType!=null && strSessionType!=null && strSessionYear!=null){
			if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty()){
				
				HouseType houseType = null;
				String decodedHouseType = null;
				CustomParameter cstpDeploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(cstpDeploymentServer != null){
					if(cstpDeploymentServer.getValue() != null && !cstpDeploymentServer.getValue().isEmpty()){
						try {
							if(cstpDeploymentServer.getValue().equals("TOMCAT")){
								decodedHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
								strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"), "UTF-8");
								strSessionType = new String(strSessionType.getBytes("ISO-8859-1"), "UTF-8");
							}							
						} catch (UnsupportedEncodingException e) {
							
							e.printStackTrace();
						}
					}
				}
				
				houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
				if(houseType == null){
					houseType = HouseType.findByName(HouseType.class, decodedHouseType, locale.toString());
				}
				
				model.addAttribute("houseType", houseType.getType());
				SessionType sessionType = null;
				try {
					sessionType = SessionType.findById(SessionType.class, Long.valueOf(strSessionType));
				} catch(Exception e) {
					if(sessionType==null) {
						sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
					}
				}
				if(sessionType!=null) {
					model.addAttribute("sessionType", sessionType.getSessionType());
				}				
				
				Integer sessionYear = Integer.valueOf(strSessionYear);
				model.addAttribute("sessionYear", sessionYear);
				
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DeviceType deviceType = DeviceType.findByType(ApplicationConstants.GOVERNMENT_BILL, locale.toString());
				Status admitted = Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, locale.toString());
								
				Status[] statuses = null;
				
				CustomParameter cpStatuses = CustomParameter.findByName(CustomParameter.class, "BILL_INTRODUCTION_DATE_ELIGIBILE_STATUSES", "");
				if(cpStatuses != null){
					if(!cpStatuses.getValue().isEmpty()){
						List<Status> listStatus = new ArrayList<Status>();
						try {
							listStatus = Status.findStatusContainedIn(cpStatuses.getValue(), locale.toString());
						} catch (ELSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						statuses = new Status[listStatus.size()];
						for(int i = 0; i < listStatus.size(); i++){
							statuses[i] = listStatus.get(i);
						}
					}
				}
				
				try {
					List<BillSearchVO> billSVOToReceiveIntroductionDate = new ArrayList<BillSearchVO>();
					List<BillSearchVO> billSVOToReceiveDiscussionDate = new ArrayList<BillSearchVO>();
										
					if(strUserGroupType != null && !strUserGroupType.isEmpty()){
						List<Bill> introBills = null;
						if(strUserGroupType.equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT)){
							introBills = Bill.findBillsForItroduction(session, deviceType, statuses, admitted, true, ApplicationConstants.ASC, locale.toString());
						}else{
							introBills = Bill.findBillsForItroduction(session, deviceType, statuses, admitted, false, ApplicationConstants.ASC, locale.toString());
						}
						
						
						for(Bill b: introBills){
							BillSearchVO bsVO = new BillSearchVO();
							bsVO.setId(b.getId());
							bsVO.setTitle(b.getDefaultTitle());
							bsVO.setNumber(FormaterUtil.formatNumberNoGrouping(b.getNumber(), locale.toString()));
							if(b.getExpectedIntroductionDate() != null){
								bsVO.setFormattedExpectedIntroductionDate(FormaterUtil.formatDateToString(b.getExpectedIntroductionDate(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
							}
							if(b.getExpectedDiscussionDate() != null){
								bsVO.setFormattedExpectedDiscussionDate(FormaterUtil.formatDateToString(b.getExpectedDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
							}
							billSVOToReceiveIntroductionDate.add(bsVO);
						}
						
						Status introducedBill = Status.findByType(ApplicationConstants.BILL_PROCESSED_INTRODUCED, locale.toString());
						
						List<Bill> discussBills = null;
						if(strUserGroupType.equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT)){
							discussBills = Bill.findBillsByPriority(session, deviceType, introducedBill, true, "expectedIntroductionDate", ApplicationConstants.ASC, locale.toString());
						}else{
							discussBills = Bill.findBillsByPriority(session, deviceType, introducedBill, false, "expectedIntroductionDate", ApplicationConstants.ASC, locale.toString());
						}
						for(Bill b: discussBills){
							BillSearchVO bsVO = new BillSearchVO();
							bsVO.setId(b.getId());
							bsVO.setTitle(b.getDefaultTitle());
							if(b.getNumber() != null){
								bsVO.setNumber(FormaterUtil.formatNumberNoGrouping(b.getNumber(), locale.toString()));
							}
							if(b.getExpectedIntroductionDate() != null){
								bsVO.setFormattedExpectedIntroductionDate(FormaterUtil.formatDateToString(b.getExpectedIntroductionDate(), ApplicationConstants.SERVER_DATEFORMAT));
							}
							if(b.getExpectedDiscussionDate() != null){
								bsVO.setFormattedExpectedDiscussionDate(FormaterUtil.formatDateToString(b.getExpectedDiscussionDate(), ApplicationConstants.SERVER_DATEFORMAT));
							}
							billSVOToReceiveDiscussionDate.add(bsVO);
						}
					}
					model.addAttribute("introBills", billSVOToReceiveIntroductionDate);
					model.addAttribute("discussBills", billSVOToReceiveDiscussionDate);
					
				} catch (ELSException e) {
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
			}
		}
		
		return "bill/providedate";
	}
	
	@Transactional
	@RequestMapping(value="/providedate/update",method=RequestMethod.POST)
	public @ResponseBody String updateIntroductionDate(final ModelMap model, final HttpServletRequest request, final Locale locale){
		
		String[] items = request.getParameter("items").split(",");
		String strHouseType = request.getParameter("houseType");
		String strRound = request.getParameter("round");
		boolean success = false;
		StringBuffer successData = new StringBuffer();
		String houseType = null;
		try{
			CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServer != null){
				if(!deploymentServer.getValue().isEmpty()){
					if(deploymentServer.getValue().equals("TOMCAT")){
						String codedHouseTypeName = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
						HouseType ht = HouseType.findByFieldName(HouseType.class, "name", codedHouseTypeName, locale.toString());
						houseType = ht.getType();
					}
				}
			}
			String action = request.getParameter("action");
			if(action != null){
				if(!action.isEmpty()){
					if(items != null){
						if(items.length > 0){
							for(int i = 0; i < items.length; i++){
								String[] billData = items[i].split("ooo");
								if(billData != null){
									if(billData.length >= 3){
										Integer priority = Integer.valueOf(billData[0]);
										Long id = Long.valueOf(billData[1]);
										Date date = null;
										if(!billData[2].isEmpty()){
											date = FormaterUtil.formatStringToDate(billData[2], ApplicationConstants.SERVER_DATEFORMAT);
										}else{
											date = FormaterUtil.formatStringToDate(billData[3], ApplicationConstants.SERVER_DATEFORMAT);
										}
										
										Bill bill = Bill.findById(Bill.class, id);
										successData.append(billData[0]);
										if(i < (items.length - 1)){
											successData.append(", ");
										}
										if(bill != null){
											bill.setPriority(priority);
											if(action.equals("introduction")){
												bill.setExpectedIntroductionDate(date);
											}else if(action.equals("discussion")){
												bill.setExpectedDiscussionDate(date);												
																								
												Status statusForDiscussion = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED + "_" + houseType + "_" + Bill.findHouseOrderOfGivenHouseForBill(bill, houseType), bill.getLocale());
												bill.setRecommendationStatus(statusForDiscussion);
												bill.setHouseRound(Integer.valueOf(strRound));
																								
											}
											bill.setStatusDate(new Date());
											bill.setExpectedStatusDate(date);
											bill.merge();
										}
										
										success = true;
									}
								}
							}
						}
					}
				}
			}
			
		}catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return successData.toString();
	}
	
	@RequestMapping(value="/generateLayingLetterWhenPassedByFirstHouse",method=RequestMethod.GET)
	public @ResponseBody void generateLayingLetterWhenPassedByFirstHouse(final HttpServletRequest request,
			final HttpServletResponse response,final ModelMap model,final Locale locale) {
		java.io.File reportFile = null;
		
		String strBillId = request.getParameter("deviceId");
		String strHouseRound = request.getParameter("houseRound");
		String strLayingDate = request.getParameter("layingDate");
		
		if(strBillId==null || strHouseRound==null || strLayingDate==null) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
        		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.layingLetterReport.incorrectparameters", locale.toString());
        		if(message != null) {
        			if(!message.getValue().isEmpty()) {
        				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
        			} else {
        				response.getWriter().println("<h3>Please check 'bill number', 'houseRound' & 'layingDate'.</h3>");
        			}
        		} else {
        			response.getWriter().println("<h3>Please check 'bill number', 'houseRound' & 'layingDate'.</h3>");
        		}
        		
				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
		if(strBillId.isEmpty() || strHouseRound.isEmpty() || strLayingDate.isEmpty()) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
        		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.layingLetterReport.incorrectparameters", locale.toString());
        		if(message != null) {
        			if(!message.getValue().isEmpty()) {
        				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
        			} else {
        				response.getWriter().println("<h3>Please check 'bill number', 'houseRound' & 'layingDate'.</h3>");
        			}
        		} else {
        			response.getWriter().println("<h3>Please check 'bill number', 'houseRound' & 'layingDate'.</h3>");
        		}
        		
				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
		CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(server != null) {
			if(server.getValue().equals("TOMCAT")) {
				try {
					strHouseRound = new String(strHouseRound.getBytes("ISO-8859-1"),"UTF-8");
					strLayingDate = new String(strLayingDate.getBytes("ISO-8859-1"),"UTF-8");
				}catch (UnsupportedEncodingException e) {
					logger.error("Cannot Encode the Request Parameter 'houseRound' & 'layingDate'.");					
				}
			}
		}
		Bill bill = Bill.findById(Bill.class, Long.parseLong(strBillId));
		if(bill==null) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
        		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.layingLetterReport.incorrectparameters", locale.toString());
        		if(message != null) {
        			if(!message.getValue().isEmpty()) {
        				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
        			} else {
        				response.getWriter().println("<h3>Please check 'bill number'.</h3>");
        			}
        		} else {
        			response.getWriter().println("<h3>Please check 'bill number'.</h3>");
        		}
        		
				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
		
		List<Object> reportFields = new ArrayList<Object>();
		reportFields.add(FormaterUtil.formatNumberNoGrouping(bill.getNumber(), locale.toString()));
		reportFields.add(bill.getDefaultTitle());	
		reportFields.add(FormaterUtil.formatNumberNoGrouping(Bill.findYear(bill), locale.toString()));
		
		Status passedByFirstHouseForGivenRoundStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+bill.findFirstHouseType()+"_"+ApplicationConstants.BILL_FIRST_HOUSE, locale.toString());
		BillDraft draftOfPassedByFirstHouseForGivenRound = Bill.findDraftByRecommendationStatusAndHouseRound(bill, passedByFirstHouseForGivenRoundStatus, Integer.parseInt(strHouseRound));
		if(draftOfPassedByFirstHouseForGivenRound==null) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
        		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.layingLetterReport.invalidBill", locale.toString());
        		if(message != null) {
        			if(!message.getValue().isEmpty()) {
        				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
        			} else {
        				response.getWriter().println("<h3>Bill is not passed by first house yet.</h3>");
        			}
        		} else {
        			response.getWriter().println("<h3>Bill is not passed by first house yet.</h3>");
        		}
        		
				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}	
		CustomParameter dateFormatForReport = CustomParameter.findByFieldName(CustomParameter.class, "name", "BILL_LAYING_LETTER_DATE_FORMAT", "");
		CustomParameter dayFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "DAY_OF_WEEK_FORMAT", "");
		Date passedByFirstHouseForGivenRoundDate = draftOfPassedByFirstHouseForGivenRound.getStatusDate();
		Date layingDate = FormaterUtil.formatStringToDate(strLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
		if(passedByFirstHouseForGivenRoundDate==null) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
        		MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.layingLetterReport.invalidBill", locale.toString());
        		if(message != null) {
        			if(!message.getValue().isEmpty()) {
        				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
        			} else {
        				response.getWriter().println("<h3>Bill is not passed by first house yet.</h3>");
        			}
        		} else {
        			response.getWriter().println("<h3>Bill is not passed by first house yet.</h3>");
        		}
        		
				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
		String formattedPassedByFirstHouseForGivenRoundDate = FormaterUtil.formatDateToString(passedByFirstHouseForGivenRoundDate, dateFormatForReport.getValue(), locale.toString());
		formattedPassedByFirstHouseForGivenRoundDate = FormaterUtil.formatMonthInMarathiDate(formattedPassedByFirstHouseForGivenRoundDate, locale.toString());
		reportFields.add(formattedPassedByFirstHouseForGivenRoundDate);
		
		String formattedLayingDate = FormaterUtil.formatDateToString(layingDate, dateFormatForReport.getValue(), locale.toString());
		formattedLayingDate = FormaterUtil.formatMonthInMarathiDate(formattedLayingDate, locale.toString());
		reportFields.add(formattedLayingDate);
		
		String formattedLayingDay = FormaterUtil.formatDateToString(layingDate, dayFormat.getValue(), locale.toString());
		formattedLayingDay = FormaterUtil.getDayInMarathi(formattedLayingDay, locale.toString());
		reportFields.add(formattedLayingDay);
		
		String formattedCurrentDate = FormaterUtil.formatDateToString(new Date(), dateFormatForReport.getValue(), locale.toString());
		formattedCurrentDate = FormaterUtil.formatMonthInMarathiDate(formattedCurrentDate, locale.toString());
		reportFields.add(formattedCurrentDate);
		
		HouseType firstHouseType = HouseType.findByFieldName(HouseType.class, "type", bill.findFirstHouseType(), locale.toString());
		reportFields.add(firstHouseType.getName());
		HouseType secondHouseType = HouseType.findByFieldName(HouseType.class, "type", bill.findSecondHouseType(), locale.toString());
		reportFields.add(secondHouseType.getName());
		CustomParameter ruleForLayingLetterParameter = CustomParameter.findByName(CustomParameter.class, "BILL_"+bill.findSecondHouseType().toUpperCase()+"_RULEFORLAYINGLETTER", locale.toString());
		reportFields.add(ruleForLayingLetterParameter.getValue());	
		
		if(bill.getSubDepartment()!=null) {
			reportFields.add(bill.getSubDepartment().getName());
		}
		
		CustomParameter layingLetterSenderRole = CustomParameter.findByName(CustomParameter.class, "BILL_LAYLETTER_SENDER_ROLE", "");
		List<User> users = User.findByRole(true, layingLetterSenderRole.getValue(), locale.toString());
		User layingLetterSender = null;
		for(User u : users){
			if(u.getHouseType().getType().equals(bill.findSecondHouseType())) {
				layingLetterSender = u;
				break;
			}			
		}		
		reportFields.add(layingLetterSender.findFirstMiddleBeginLetterLastName());
		Role role = Role.findByName(Role.class, layingLetterSenderRole.getValue(), locale.toString());
		reportFields.add(role.getLocalizedName());		
		
		Object[] reportFieldsArr = new Object[reportFields.size()];
		for(int i=0; i<reportFields.size();i++) {
			reportFieldsArr[i] = reportFields.get(i);
		}
		try {
			reportFile = generateReportUsingFOP(reportFieldsArr, "bill_layingletter_template", "PDF", "layingletter_report", locale.toString());
			openOrSaveReportFileFromBrowser(response, reportFile, "PDF");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/createbillpatrakbhagdon",method=RequestMethod.GET)
	public String generatePatrakBhag2Report(final HttpServletRequest request,
			final HttpServletResponse response,final ModelMap model,final Locale locale) {
		String retVal = "bill/error";
		
		String strDeviceType = request.getParameter("deviceType");
		String strHouseType = request.getParameter("houseType");
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strOutputFormat = request.getParameter("outputFormat");
		String strDistributionDate = request.getParameter("patrakbahgdonDate");
						
		Date date = new Date();
		CustomParameter patrakBhagTwoDateFormat;
		CustomParameter dayFormat;
		
		if(strDeviceType!=null&&strHouseType!=null
				&&strSessionYear!=null&&strSessionType!=null&&strOutputFormat!=null){
			if((!strDeviceType.isEmpty())&&(!strHouseType.isEmpty())
					&&(!strSessionYear.isEmpty())&&(!strSessionType.isEmpty())&&(!strOutputFormat.isEmpty())){
				DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
				Session session = null;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Status toBeIntroduced = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED, locale.toString());
				Status departmentIntimated = Status.findByType(ApplicationConstants.BILL_PROCESSED_DEPARTMENTINTIMATED, locale.toString());
				Status admitted = Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, locale.toString());
				
				model.addAttribute("houseType", houseType.getType());
				model.addAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString()));
				model.addAttribute("house", houseType.getName());
				model.addAttribute("sessionPlace", session.getPlace().getPlace());
				
				List<User> users = User.findByRole(true, "BIS_PRINCIPALSECRETARY", locale.toString());
				User principalSecretary = null;
				for(User u : users){
					principalSecretary = u;
					break;
				}
				
				model.addAttribute("who", principalSecretary.findFirstLastName());
				for(Role r : principalSecretary.getCredential().getRoles()){
					model.addAttribute("whopost", r.getLocalizedName());
					break;
				}
						
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("internalStatusId", new String[]{toBeIntroduced.getId().toString()});
				parameters.put("recommendationStatusId", new String[]{departmentIntimated.getId().toString()});
				parameters.put("statusId", new String[]{admitted.getId().toString()});
				
				Language lang = null;
				if(locale.toString().equals(ApplicationConstants.DEFAULT_LOCALE)){
					lang = Language.findByFieldName(Language.class, "type", "marathi", locale.toString());
				}else{
					lang = Language.findByFieldName(Language.class, "type", "english", locale.toString());
				}
				parameters.put("languageId", new String[]{lang.getId().toString()});
				List report = Query.findReport("BILL_PATRAKBHAG_DON", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("san", "सन");
				CustomParameter footer = null;
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					model.addAttribute("houseShort", "वि. स. वि.");
					footer = CustomParameter.findByName(CustomParameter.class, "BILL_PATRAKBHAG2_FOOTR_LOWERHOUSE", "");
				}else if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					model.addAttribute("houseShort", "वि. प. वि.");
					footer = CustomParameter.findByName(CustomParameter.class, "BILL_PATRAKBHAG2_FOOTR_UPPERHOUSE", "");
				}
				
				model.addAttribute("footer", footer.getValue());
				
				if(strDistributionDate != null && !strDistributionDate.isEmpty()){
					
					patrakBhagTwoDateFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "PATRAK_BHAG_TWO_DATE_FORMAT", null);
					dayFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "DAY_OF_WEEK_FORMAT", null);
					/*** To form the patrakbhag don formatted date ***/
					com.ibm.icu.util.Calendar calendar = com.ibm.icu.util.Calendar.getInstance();
					calendar.setTime(date);
					Integer intDay = calendar.get(Calendar.DAY_OF_MONTH);
					Integer intMonth = calendar.get(Calendar.MONTH);
					Integer intYear = calendar.get(Calendar.YEAR);
					String formattedCurrentDate = FormaterUtil.formatNumberNoGrouping(intDay, locale.toString()) + 
								" " + FormaterUtil.getMonthInMarathi(intMonth, locale.toString()) + 
								", " + FormaterUtil.formatNumberNoGrouping(intYear, locale.toString());
					
					model.addAttribute("formattedCurrentDay", FormaterUtil.formatDateToString(date, dayFormat.getValue(), locale.toString()));
					model.addAttribute("formattedCurrentDate", FormaterUtil.formatDateToString(date, patrakBhagTwoDateFormat.getValue(), locale.toString()));
					model.addAttribute("indianDateFormatCurrentDate", FormaterUtil.getIndianDate(date, locale.toString()));
					
					Date distributionDate = FormaterUtil.formatStringToDate(strDistributionDate, ApplicationConstants.SERVER_DATEFORMAT);
					model.addAttribute("formattedDistributionDay", FormaterUtil.formatDateToString(distributionDate, dayFormat.getValue(), locale.toString()));
					model.addAttribute("formattedDistributionDate", FormaterUtil.formatDateToString(distributionDate, patrakBhagTwoDateFormat.getValue(), locale.toString()));
				}
				
				retVal = "bill/report/patrakbhagtwo";
				
			} else {
				logger.error("**** Check request parameters 'deviceType,houseType,sessionYear,sessionType,outputFormat' for empty values ****");
				try {
					response.getWriter().println("<h3>Check request parameters 'deviceType,houseType,sessionYear,sessionType,outputFormat' for empty values</h3>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.error("**** Check request parameters 'deviceType,houseType,sessionYear,sessionType,outputFormat' for null values ****");
			try {
				response.getWriter().println("<h3>Check request parameters 'deviceType,houseType,sessionYear,sessionType,outputFormat' for null values</h3>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return retVal;
	}
	
	@RequestMapping(value="/editStatusUpdation", method=RequestMethod.GET)
	public String editStatusUpdation(final ModelMap model, final HttpServletRequest request, final Locale locale){
		String billId=request.getParameter("billId");
		String houseType = request.getParameter("houseType");
		if(billId!=null&&houseType!=null) {
			if(!billId.isEmpty()&&!houseType.isEmpty()) {
				/**** House Type ****/
				HouseType selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, locale.toString());
				if(selectedHouseType==null) {
					selectedHouseType = HouseType.findById(HouseType.class, Long.parseLong(houseType));
				}
				if(selectedHouseType!=null) {
					model.addAttribute("formattedHouseType", selectedHouseType.getName());
					model.addAttribute("houseType", selectedHouseType.getType());
				}				
				/**** House Rounds Available For Bill ****/
				CustomParameter billHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
				if(billHouseRoundsParameter!=null) {
					Integer houseRoundsAvailable = Integer.parseInt(billHouseRoundsParameter.getValue());							
					/**** Populate House Rounds ****/
					List<MasterVO> houseRoundVOs = new ArrayList<MasterVO>();
					for(int i=1; i<=houseRoundsAvailable; i++) {
						MasterVO houseRoundVO = new MasterVO();
						houseRoundVO.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
						houseRoundVO.setValue(String.valueOf(i));												
						houseRoundVOs.add(houseRoundVO);
					}
					model.addAttribute("houseRoundVOs", houseRoundVOs);
				} else {
					logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
					model.addAttribute("errorcode", "bill_houseorders_notset");
					return "votingdetail/error";
				}
				model.addAttribute("billId", Long.parseLong(billId));
				/**** Voting Details For Bill In Given House ****/
				Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
				if(bill!=null) {					
					List<BillDraft> draftsOfStatusUpdationForGivenHouse = Bill.findStatusUpdationDraftsForGivenHouse(bill, selectedHouseType);
					if(draftsOfStatusUpdationForGivenHouse!=null) {
						if(!draftsOfStatusUpdationForGivenHouse.isEmpty()) {
							/**** Statuses, Status Dates And Expected Status Dates ****/
							List<String> statuses = new ArrayList<String>();
							List<String> statusDates = new ArrayList<String>();
							List<String> expectedStatusDates = new ArrayList<String>();
							for(BillDraft i: draftsOfStatusUpdationForGivenHouse) {
								statuses.add(i.getRecommendationStatus().getName());
								Date statusDate = i.getStatusDate();
								if(statusDate!=null) {
									statusDates.add(FormaterUtil.formatDateToString(statusDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								} else {
									statusDates.add("");
								}
								Date expectedStatusDate = i.getExpectedStatusDate();
								if(expectedStatusDate!=null) {
									expectedStatusDates.add(FormaterUtil.formatDateToString(expectedStatusDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
								} else {
									expectedStatusDates.add("");
								}
							}
							model.addAttribute("statuses", statuses);
							model.addAttribute("statusDates", statusDates);
							model.addAttribute("expectedStatusDates", expectedStatusDates);
						}
					} else {
						draftsOfStatusUpdationForGivenHouse = new ArrayList<BillDraft>();
					}
					model.addAttribute("draftsOfStatusUpdationForGivenHouse", draftsOfStatusUpdationForGivenHouse);
				}				
			}
		}
		return "bill/editstatusupdationforhouse";
	}
	
	@Transactional
	@RequestMapping(value="/editStatusUpdation",method=RequestMethod.POST)
	public @ResponseBody String updateStatusUpdation(final ModelMap model, final HttpServletRequest request, final Locale locale){
		String result="";
		String billId=request.getParameter("billId");
		String houseType = request.getParameter("houseType");				
		String numberOfDraftsOfStatusUpdationForGivenHouseStr = request.getParameter("numberOfDraftsOfStatusUpdationForGivenHouse");
		if(numberOfDraftsOfStatusUpdationForGivenHouseStr!=null) {
			if(!numberOfDraftsOfStatusUpdationForGivenHouseStr.isEmpty()) {
				int numberOfDraftsOfStatusUpdationForGivenHouse = Integer.parseInt(numberOfDraftsOfStatusUpdationForGivenHouseStr);
				if(numberOfDraftsOfStatusUpdationForGivenHouse>0) {					
					if(billId!=null&&houseType!=null) {
						if(!billId.isEmpty()&&!houseType.isEmpty()) {						
							Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
							if(bill!=null) {		
								List<BillDraft> billDrafts = new ArrayList<BillDraft>();
								for(int i=1; i<=numberOfDraftsOfStatusUpdationForGivenHouse; i++) {
									String billDraftId = request.getParameter("id_"+i);
									if(billDraftId!=null) {
										if(!billDraftId.isEmpty()) {
											BillDraft billDraft = BillDraft.findById(BillDraft.class, Long.parseLong(billDraftId));
											if(billDraft!=null) {
												String houseRoundStr = request.getParameter("houseRound_"+i);
												if(houseRoundStr!=null) {
													if(!houseRoundStr.isEmpty()) {															
														billDraft.setHouseRound(Integer.parseInt(houseRoundStr));
													}
												}
												String statusDateStr = request.getParameter("statusDate_"+i);
												if(statusDateStr!=null) {
													if(!statusDateStr.isEmpty()) {														
														billDraft.setStatusDate(FormaterUtil.formatStringToDate(statusDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
													}
												}
												String expectedStatusDateStr = request.getParameter("expectedStatusDate_"+i);
												if(expectedStatusDateStr!=null) {
													if(!expectedStatusDateStr.isEmpty()) {														
														billDraft.setExpectedStatusDate(FormaterUtil.formatStringToDate(expectedStatusDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
													}
												}
												billDrafts.add(billDraft);
											}
										}
									}
								}
								if(!billDrafts.isEmpty()) {
									bill.getDrafts().removeAll(billDrafts);
									if(!bill.getDrafts().isEmpty()) {
										billDrafts.addAll(bill.getDrafts());
									}
									bill.setDrafts(billDrafts);
									bill.simpleMerge();
									result = "success";
								}								
							}
						}
					}
				}			
			}
		}		
		return result;		
	}	
}
