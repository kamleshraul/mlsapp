package org.mkcl.els.controller.bis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.BaseDomain;
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
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.LapsedEntity;
import org.mkcl.els.domain.LayingLetter;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Ordinance;
import org.mkcl.els.domain.PrintRequisition;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Section;
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
			/**** Roles ****/
			Set<Role> roles=this.getCurrentUser().getRoles();
			Boolean isMinister = false;
			Boolean isMember = false;
			for(Role i:roles){
				if(i.getType().equals("MINISTER")){
					isMinister = true;
					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {						
						model.addAttribute("errorcode","permissiondenied");
						return;
					}					
				}else if(i.getType().startsWith("MEMBER_")){
					isMember = true;
					model.addAttribute("role",i.getType());
					//break;
				}else if(i.getType().contains("BIS_CLERK")){
					model.addAttribute("role",i.getType());
					break;
				}else if(i.getType().startsWith("BIS_")){
					model.addAttribute("role",i.getType());
					break;
				}
			}
			/**** Device Types ****/
			List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
			List<DeviceType> allowedDeviceTypes = new ArrayList<DeviceType>();
			try {
				deviceTypes = DeviceType.findDeviceTypesStartingWith("bills", locale);
				if(deviceTypes!=null) {
					for(DeviceType i: deviceTypes) {
						if(isMinister && i.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
							continue;
						} else if(isMember && !isMinister && i.getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
							continue;
						} else {
							allowedDeviceTypes.add(i);
						}
					}
				}
			} catch (ELSException e) {
				e.printStackTrace();
			}
			model.addAttribute("deviceTypes", allowedDeviceTypes);			
			model.addAttribute("deviceType",deviceType.getId());
			//Access Control Based on device Type
			model.addAttribute("deviceTypeType",deviceType.getType());
			/**** House Types ****/
			List<HouseType> houseTypes = new ArrayList<HouseType>();
			String houseType = null;
			if(isMinister) {
				houseType = ApplicationConstants.BOTH_HOUSE;
			} else {
				houseType = this.getCurrentUser().getHouseType();
			}
			if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
				houseTypes = HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
			}else if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}
			model.addAttribute("houseTypes", houseTypes);
			if(houseType.equals(ApplicationConstants.BOTH_HOUSE)) {
				if(isMinister){
					houseType = this.getCurrentUser().getHouseType();
				} else {
					houseType = ApplicationConstants.LOWER_HOUSE;
				}
			}			
			model.addAttribute("houseType",houseType);			
			/**** Latest Session of a House Type ****/
			HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
			Session lastSessionCreated=null;
			try {
				lastSessionCreated = Session.findLatestSession(authUserHouseType);
			} catch (ELSException e) {
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
											model.addAttribute("error",e.getParameter());
											e.printStackTrace();
										}
									}else{
										CustomParameter defaultAllowedStatus=CustomParameter.findByName(CustomParameter.class,"BILL_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
										if(defaultAllowedStatus!=null){
											try {
												status=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale);
											} catch (ELSException e) {
												model.addAttribute("error",e.getParameter());
												e.printStackTrace();
											}
										}else{
											model.addAttribute("errorcode","bill_status_allowed_by_default_not_set");
										}
									}
									model.addAttribute("status",status);
//									/**** translation status filter in case of assistant processing ****/
//									if(userGroupType.equals(ApplicationConstants.ASSISTANT)) {
//										CustomParameter translationStatusesParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSLATION_FILTER_STATUSES", "");
//										if(translationStatusesParameter!=null) {
//											List<Status> translationStatuses = new ArrayList<Status>();
//											try {
//												translationStatuses = Status.findStatusContainedIn(translationStatusesParameter.getValue(),locale);
//											} catch (ELSException e) {
//												logger.debug("populateNew", e);
//												model.addAttribute("error",e.getParameter());
//												e.printStackTrace();
//											}
//											model.addAttribute("translationStatuses", translationStatuses);
//										}										
//									}
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
				model.addAttribute("ugparam",this.getCurrentUser().getActualUsername());	
				if(lastSessionCreated.getParameter("bills_nonofficial_isBallotingRequired")!=null) {
					model.addAttribute("isBallotingRequired", lastSessionCreated.getParameter("bills_nonofficial_isBallotingRequired"));
				}
			} else{
				model.addAttribute("errorcode","nosessionentriesfound");
			}			
		} else {
			model.addAttribute("errorcode","workunderprogress");
		}
	}
	
	@Override
	protected String modifyURLPattern(final String urlPattern,final HttpServletRequest request,final ModelMap model,final String locale) {
		/**** For Typist and other BIS roles assistant grid is visible ****/
		String role=request.getParameter("role");
		String newUrlPattern=urlPattern;
		if(role.contains("BIS_")&& (!role.contains("TYPIST"))){	
			newUrlPattern=newUrlPattern+"?usergroup=assistant";
//			Status underConsiderationStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, locale);
//			String currentSelectedStatus = request.getParameter("status");
//			if(currentSelectedStatus!=null&&underConsiderationStatus!=null) {
//				if(currentSelectedStatus.equals(underConsiderationStatus.getId().toString())) {
//					newUrlPattern=newUrlPattern+"&internalStatus="+underConsiderationStatus.getType();
//				}
//			}			
		}else if(role.contains("BIS_")&& (role.contains("TYPIST"))){
			newUrlPattern=newUrlPattern+"?usergroup=typist";
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
				if(role.startsWith("MEMBER_")||role.contains("BIS_TYPIST")){
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
			if(i.getType().startsWith("MEMBER_")||i.getType().contains("TYPIST")){
				return newUrlPattern;
			}else if(i.getType().contains("ASSISTANT")||i.getType().contains("SECTION_OFFICER")){
				return newUrlPattern.replace("edit","assistant");
			}else if(i.getType().startsWith("BIS_")){
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
			if(!deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)){
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
		/**** Ministries ****/
		Ministry ministry=domain.getMinistry();
		if(ministry!=null){
			model.addAttribute("ministrySelected",ministry.getId());
			model.addAttribute("formattedMinistry",ministry.getName());
			List<SubDepartment> assignedSubDepartments = MemberMinister.
					findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
			model.addAttribute("subDepartments", assignedSubDepartments);
			SubDepartment subDepartment=domain.getSubDepartment();
			if(subDepartment!=null){
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
			}									
		} else {
			Session ministrySession = selectedSession;
			if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)) {
				if(usergroupType!=null && !usergroupType.isEmpty()
						&& usergroupType.equals(ApplicationConstants.MEMBER)){
					List<MemberMinister> memberMinisters = MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
					if(memberMinisters==null || memberMinisters.isEmpty()) {
						if(selectedSession.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
							try {
								ministrySession = Session.find(selectedSession.getYear(), selectedSession.getType().getType(), ApplicationConstants.UPPER_HOUSE);
								if(ministrySession!=null) {
									memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, ministrySession, locale);
								}
							} catch (ELSException e) {
								e.printStackTrace();
							}
						} else if(selectedSession.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
							try {
								ministrySession = Session.find(selectedSession.getYear(), selectedSession.getType().getType(), ApplicationConstants.LOWER_HOUSE);
								if(ministrySession!=null) {
									memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, ministrySession, locale);
								}
							} catch (ELSException e) {
								e.printStackTrace();
							}
						}
					}
					if(memberMinisters!=null && !memberMinisters.isEmpty()) {
						ministry = memberMinisters.get(0).getMinistry();
						model.addAttribute("ministrySelected",ministry.getId());
						model.addAttribute("formattedMinistry",ministry.getName());
						List<SubDepartment> assignedSubDepartments = MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getStartDate(), locale);
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
			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strRotationOrderPubDate = ministrySession.getParameter("questions_starred_rotationOrderPublishingDate");
			if(strRotationOrderPubDate==null){
				logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
				model.addAttribute("errorcode", "rotationorderpubdate_notset");
			} else {
				try {
					rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
				} catch (ParseException e) {
					logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
					model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
				}
				Date currentDate=new Date();
				if(currentDate.before(rotationOrderPubDate)){
					logger.error("Rotation order not set in session with Id:"+selectedSession.getId());
					model.addAttribute("errorcode", "rotationorderpubdate_notreached");
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
					model.addAttribute("referredActNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredAct.getNumber()));
					model.addAttribute("referredActYear", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredAct.getYear()));
				}
			}
		}		
		/**** Referred Ordinance for Amendment Bill ****/
		if(domain.getBillType()!=null) {
			if(domain.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)) {
				Ordinance referredOrdinance = domain.getReferredOrdinance();
				if(referredOrdinance!=null) {
					model.addAttribute("referredOrdinance", referredOrdinance.getId());
					model.addAttribute("referredOrdinanceNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredOrdinance.getNumber()));
					model.addAttribute("referredOrdinanceYear", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredOrdinance.getYear()));
				}
			}
		}		
		/**** titles, content drafts, 'statement of object and reason' drafts, memorandum drafts, annexures ****/			
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
		if(!deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)){
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
		}		
		/**** Ministries ****/
		Ministry ministry=domain.getMinistry();
		if(ministry!=null){
			model.addAttribute("ministrySelected",ministry.getId());
			model.addAttribute("formattedMinistry",ministry.getName());
			List<SubDepartment> assignedSubDepartments = MemberMinister.
					findAssignedSubDepartments(ministry,selectedSession.getEndDate(), locale);
			model.addAttribute("subDepartments", assignedSubDepartments);
			SubDepartment subDepartment=domain.getSubDepartment();
			if(subDepartment!=null){
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
			}									
		} else {
			Session ministrySession = selectedSession;
			if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)) {
				if(usergroupType!=null && !usergroupType.isEmpty()
						&& usergroupType.equals(ApplicationConstants.MEMBER)){
					List<MemberMinister> memberMinisters = MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, selectedSession, locale);
					if(memberMinisters==null || memberMinisters.isEmpty()) {
						if(selectedSession.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
							try {
								ministrySession = Session.find(selectedSession.getYear(), selectedSession.getType().getType(), ApplicationConstants.UPPER_HOUSE);
								if(ministrySession!=null) {
									memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, ministrySession, locale);
								}
							} catch (ELSException e) {
								e.printStackTrace();
							}
						} else if(selectedSession.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
							try {
								ministrySession = Session.find(selectedSession.getYear(), selectedSession.getType().getType(), ApplicationConstants.LOWER_HOUSE);
								if(ministrySession!=null) {
									memberMinisters=MemberMinister.findAssignedMemberMinisterOfMemberInSession(member, ministrySession, locale);
								}
							} catch (ELSException e) {
								e.printStackTrace();
							}
						}
					}
					if(memberMinisters!=null && !memberMinisters.isEmpty()) {
						ministry = memberMinisters.get(0).getMinistry();
						model.addAttribute("ministrySelected",ministry.getId());
						model.addAttribute("formattedMinistry",ministry.getName());
						List<SubDepartment> assignedSubDepartments = MemberMinister.findAssignedSubDepartments(ministry,selectedSession.getStartDate(), locale);
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
			Date rotationOrderPubDate=null;
			CustomParameter serverDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String strRotationOrderPubDate = ministrySession.getParameter("questions_starred_rotationOrderPublishingDate");
			if(strRotationOrderPubDate==null){
				logger.error("Parameter 'questions_starred_rotationOrderPublishingDate' not set in session with Id:"+selectedSession.getId());
				model.addAttribute("errorcode", "rotationorderpubdate_notset");
			} else {
				try {
					rotationOrderPubDate = FormaterUtil.getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
				} catch (ParseException e) {
					logger.error("Failed to parse rotation order publish date:'"+strRotationOrderPubDate+"' in "+serverDateFormat.getValue()+" format");
					model.addAttribute("errorcode", "rotationorderpubdate_cannotbeparsed");
				}
				Date currentDate=new Date();
				if(currentDate.before(rotationOrderPubDate)){
					logger.error("Rotation order not set in session with Id:"+selectedSession.getId());
					model.addAttribute("errorcode", "rotationorderpubdate_notreached");
				}
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
		if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
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
			/**** level of current usergroup ****/
			model.addAttribute("level",1);
			/**** list of put up options available ****/
			populateInternalStatus(model,internalStatus.getType(),usergroupType,locale,deviceType.getType());
			/**** Referencing & Clubbing For Non-Official Bill ****/
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				/**** Referenced Bill ****/
				List<Reference> refentities=new ArrayList<Reference>();
				List<String> refentitiesSessionDevice = new ArrayList<String>();
				if(domain.getReferencedBill() != null){					
					ReferencedEntity refEntity = domain.getReferencedBill();				
					Reference reference=new Reference();
					reference.setId(String.valueOf(refEntity.getId()));
					if(refEntity.getDeviceType()!=null) {
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
					} else {
						Act refAct = (Act)refEntity.getDevice();
						if(refAct.getNumber()!=null) {
							reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(refAct.getNumber()));
						}
						reference.setNumber(String.valueOf(refAct.getId()));
						refentities.add(reference);
						
						MessageResource msg = MessageResource.findByFieldName(MessageResource.class, "code", "bill.referredAct", locale);
						if(msg!=null) {
							refentitiesSessionDevice.add(FormaterUtil.formatNumberNoGrouping(refAct.getYear(), locale) + ", " + 
									msg.getValue());
						}	
						model.addAttribute("isActReferenced", true);
					}							
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
		/**** Auxiliary workflow statuses ****/
		Status translationStatus = null;
		Status opinionFromLawAndJDStatus = null;
		Status recommendationFromGovernorStatus = null;
		Status recommendationFromPresidentStatus = null;
		try {
			translationStatus = domain.findAuxiliaryWorkflowStatus(ApplicationConstants.TRANSLATION_WORKFLOW);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			return;
		}
		if(translationStatus==null) {
			translationStatus = Status.findByType(ApplicationConstants.BILL_TRANSLATION_NOTSEND, domain.getLocale());
		}
		model.addAttribute("translationStatusType", translationStatus.getType());
		model.addAttribute("formattedTranslationStatus", translationStatus.getName());
		if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
			try {
				opinionFromLawAndJDStatus = domain.findAuxiliaryWorkflowStatus(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW);
				if(opinionFromLawAndJDStatus==null) {
					opinionFromLawAndJDStatus = Status.findByType(ApplicationConstants.BILL_OPINION_FROM_LAWANDJD_NOTSEND, domain.getLocale());
				}
				model.addAttribute("opinionFromLawAndJDStatusType", opinionFromLawAndJDStatus.getType());
				model.addAttribute("formattedOpinionFromLawAndJDStatus", opinionFromLawAndJDStatus.getName());
				
				recommendationFromGovernorStatus = domain.findAuxiliaryWorkflowStatus(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW);
				if(recommendationFromGovernorStatus==null) {
					recommendationFromGovernorStatus = Status.findByType(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND, domain.getLocale());
				}
				model.addAttribute("recommendationFromGovernorStatusType", recommendationFromGovernorStatus.getType());
				model.addAttribute("formattedRecommendationFromGovernorStatus", recommendationFromGovernorStatus.getName());
				
				recommendationFromPresidentStatus = domain.findAuxiliaryWorkflowStatus(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW);
				if(recommendationFromPresidentStatus==null) {
					recommendationFromPresidentStatus = Status.findByType(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND, domain.getLocale());
				}
				model.addAttribute("recommendationFromPresidentStatusType", recommendationFromPresidentStatus.getType());
				model.addAttribute("formattedRecommendationFromPresidentStatus", recommendationFromPresidentStatus.getName());
			} catch(ELSException e) {
				model.addAttribute("error", e.getParameter());
				return;
			}			
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
			@SuppressWarnings("unchecked")
			List<Status> internalStatuses = (List<Status>) model.get("internalStatuses");
			List<Status> statusesToRemove = new ArrayList<Status>();
			for(Status i: internalStatuses) {
				boolean isCandidateToRemove = false;
				if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_TRANSLATION)
						&& !(translationStatus.getType().equals(ApplicationConstants.BILL_TRANSLATION_NOTSEND))) {
					isCandidateToRemove = true;
				} 
				else {
					if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_OPINION_FROM_LAWANDJD)
								|| i.getType().equals(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_GOVERNOR)								
								|| i.getType().equals(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_PRESIDENT)) {
							isCandidateToRemove = true;
						}
					} else {
						if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_OPINION_FROM_LAWANDJD)
								&& !(opinionFromLawAndJDStatus.getType().equals(ApplicationConstants.BILL_OPINION_FROM_LAWANDJD_NOTSEND))) {
							isCandidateToRemove = true;
						} else if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_GOVERNOR)
								&& !(recommendationFromGovernorStatus.getType().equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND))) {
							isCandidateToRemove = true;
						} else if(i.getType().equals(ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_PRESIDENT)
								&& !(recommendationFromPresidentStatus.getType().equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND))) {
							isCandidateToRemove = true;
						}
					}
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
					model.addAttribute("level", actorArr[2]);			
					model.addAttribute("localizedActorName", actorArr[3]+"("+actorArr[4]+")");					
				}
			}			
		}
		/**** checklist ****/
		if(domain.getChecklist()!=null && !domain.getChecklist().isEmpty()) {
			model.addAttribute("isChecklistFilled", true);
		}
		CustomParameter checklistCountParameter = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.BILL_CHECKLIST_COUNT, "");
		if(checklistCountParameter!=null) {
			if(checklistCountParameter.getValue()!=null) {
				try {
					int checklistCount = Integer.parseInt(checklistCountParameter.getValue());
					List<String> checklistSerialNumbers = new ArrayList<String>();
					for(int i=0; i<=checklistCount; i++) {
						checklistSerialNumbers.add(FormaterUtil.formatNumberNoGrouping(i, locale));
					}
					model.addAttribute("checklistSerialNumbers", checklistSerialNumbers);
				} catch (NumberFormatException e) {
					logger.error("custom parameter '"+ApplicationConstants.BILL_CHECKLIST_COUNT+"' is not set.");
					model.addAttribute("errorcode", "BILL_CHECKLIST_COUNT_NOTSET");
					return;
				}
			} else {
				logger.error("custom parameter '"+ApplicationConstants.BILL_CHECKLIST_COUNT+"' is not set.");
				model.addAttribute("errorcode", "BILL_CHECKLIST_COUNT_NOTSET");
				return;
			}
		} else {
			logger.error("custom parameter '"+ApplicationConstants.BILL_CHECKLIST_COUNT+"' is not set.");
			model.addAttribute("errorcode", "BILL_CHECKLIST_COUNT_NOTSET");
			return;
		}
		/**** remarks ****/	
		UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", usergroupType, locale);
		if(userGroupType!=null) {
			String currentRemarks = Bill.findLatestRemarksOfActor(domain.getId(), userGroupType.getName(), this.getCurrentUser().getActualUsername(), locale);
			model.addAttribute("currentRemarks", currentRemarks);
		}			
		
		/**** check for already submitted bills pending for to be putup ****/
		Boolean isAnyBillSubmittedEarierThanCurrentBillToBePutup = Bill.isAnyBillSubmittedEarierThanCurrentBillToBePutup(domain);
		model.addAttribute("isAnyBillSubmittedEarierThanCurrentBillToBePutup", isAnyBillSubmittedEarierThanCurrentBillToBePutup);
	
		/**** schedule 7 of constitution ****/
		String languagesAllowedForBill = domain.getSession().getParameter(deviceType.getType().trim()+"_languagesAllowed");
		if(languagesAllowedForBill!=null && !languagesAllowedForBill.isEmpty()) {
			for(String language: languagesAllowedForBill.split("#")) {
				String schedule7OfConstitutionForGivenLanguage = domain.getSession().getParameter(deviceType.getType().trim()+"_schedule7OfConstitution_"+language);
				if(schedule7OfConstitutionForGivenLanguage!=null) {
					model.addAttribute("schedule7OfConstitution_"+language, schedule7OfConstitutionForGivenLanguage);
				}
			}
		}
		
		/**** instructional order ****/
		String instructionalOrder = domain.getSession().getParameter(deviceType.getType().trim()+"_instructionalOrder");
		if(instructionalOrder!=null) {
			model.addAttribute("instructionalOrder", instructionalOrder);
		}
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
			Integer currentHouseRound = currentDraft.getHouseRound();
			if(currentHouseRound!=null) {
				model.addAttribute("currentHouseRound", currentDraft.getHouseRound());
				model.addAttribute("formattedCurrentHouseRound", FormaterUtil.formatNumberNoGrouping(currentHouseRound, domain.getLocale()));
			}			
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
			CustomParameter finalStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_FINAL","");
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
				CustomParameter recommendStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_RECOMMEND","");
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
					CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_BY_DEFAULT","");
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
					logger.debug("populateNew", e);
					model.addAttribute("error",e.getParameter());
					e.printStackTrace();
				}
			}else{
				CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_BY_DEFAULT","");
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
		if(role.equals("BIS_TYPIST") && domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)){
			//Empty check for number
			if(domain.getNumber()==null){
				result.rejectValue("number","NumberEmpty");
			}
			// Check duplicate entry for bill Number
			Boolean flag = Bill.isExist(domain);
			if(flag){
				result.rejectValue("number", "NonUnique","Duplicate Number");
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
			String contentDraftFileInThisLanguage = request.getParameter("contentDraft-file-"+languageAllowedInSession);
			if((contentDraftTextInThisLanguage!=null && !contentDraftTextInThisLanguage.isEmpty())
					|| (contentDraftFileInThisLanguage!=null && !contentDraftFileInThisLanguage.isEmpty())) {
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
					if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(domain.getIntroducingHouseType()==null) {
							result.rejectValue("introducingHouseType", "IntroducingHouseTypeEmpty", "Please select the preferred housetype for passing the bill.");
						}
						if(domain.getOpinionSoughtFromLawAndJD()==null || domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
							result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from law and judiciary department is not mentioned");
						}
						if(domain.getOpinionSoughtFromLawAndJDFile()==null || domain.getOpinionSoughtFromLawAndJDFile().isEmpty()) {
							result.rejectValue("version", "opinionFromLawAndJDFileNotAttached", "File for Opinion from law and judiciary department is not attached");
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
							} else if(domain.getBillType().getType().equals(ApplicationConstants.ORIGINAL_BILL)) {
								if(domain.getReferredOrdinance()!=null || domain.getReferredAct()!=null) {
									result.rejectValue("version", "referActOrOrdinanceNotAllowed", "Please de-refer act or ordinance referred. It's not allowed for original bill");
								}
							} else {
								if(domain.getReferredOrdinance()==null && domain.getReferredAct()==null) {
									result.rejectValue("version", "referActOrOrdinanceCompulsory", "Please refer act or ordinance.");
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
		List<TextDraft> titles = BillController.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = BillController.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
		
		/**** Referred Act for Amendment Bill ****/
		if(domain.getBillType()!=null) {
			if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
				Act referredAct = domain.getReferredAct();
				if(referredAct!=null) {
					model.addAttribute("referredAct", referredAct.getId());
					model.addAttribute("referredActNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredAct.getNumber()));
					model.addAttribute("referredActYear", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredAct.getYear()));
				}
			}
		}
		
		/**** Referred Ordinance for Amendment Bill ****/
		if(domain.getBillType()!=null) {
			if(domain.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)) {
				Ordinance referredOrdinance = domain.getReferredOrdinance();
				if(referredOrdinance!=null) {
					model.addAttribute("referredOrdinance", referredOrdinance.getId());
					model.addAttribute("referredOrdinanceNumber", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredOrdinance.getNumber()));
					model.addAttribute("referredOrdinanceYear", FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(referredOrdinance.getYear()));
				}
			}
		}
		
		populateNew(model, domain, domain.getLocale(), request);
		model.addAttribute("type", "error");
		model.addAttribute("msg", "create_failed");
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model, final Bill domain,
			final HttpServletRequest request) {
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = BillController.updateDraftsOfGivenType(domain, "title", request);
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
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = BillController.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
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
					List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,null, "0");	
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
	protected void customValidateUpdate(final Bill domain, final BindingResult result,
			final HttpServletRequest request) {		
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
		if(role.equals("BIS_TYPIST") && domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)){
			//Empty check for number
			if(domain.getNumber()==null){
				result.rejectValue("number","NumberEmpty");
			}
			// Check duplicate entry for bill Number
			Boolean flag = Bill.isExist(domain);
			if(flag){
				result.rejectValue("number", "NonUnique","Duplicate Number");
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
			String contentDraftFileInThisLanguage = request.getParameter("contentDraft-file-"+languageAllowedInSession);
			if((contentDraftTextInThisLanguage!=null && !contentDraftTextInThisLanguage.isEmpty())
					|| (contentDraftFileInThisLanguage!=null && !contentDraftFileInThisLanguage.isEmpty())) {
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
					if(domain.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
						if(domain.getIntroducingHouseType()==null) {
							result.rejectValue("introducingHouseType", "IntroducingHouseTypeEmpty", "Please select the preferred housetype for passing the bill.");
						}
						if(domain.getOpinionSoughtFromLawAndJD()==null || domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
							result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from law and judiciary department is not mentioned");
						}
						if(domain.getOpinionSoughtFromLawAndJDFile()==null || domain.getOpinionSoughtFromLawAndJDFile().isEmpty()) {
							result.rejectValue("version", "opinionFromLawAndJDFileNotAttached", "File for Opinion from law and judiciary department is not attached");
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
							} else if(domain.getBillType().getType().equals(ApplicationConstants.ORIGINAL_BILL)) {
								if(domain.getReferredOrdinance()!=null || domain.getReferredAct()!=null) {
									result.rejectValue("version", "referActOrOrdinanceNotAllowed", "Please de-refer act or ordinance referred. It's not allowed for original bill");
								}
							} else {
								if(domain.getReferredOrdinance()==null && domain.getReferredAct()==null) {
									result.rejectValue("version", "referActOrOrdinanceCompulsory", "Please refer act or ordinance.");
								}
							}
						}
					}
				} else if(operation.equals("startworkflow")){					
					if(domain.getOpinionSoughtFromLawAndJD()==null) {
						result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from law and judiciary department is not received");
					} else if(domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
						result.rejectValue("opinionSoughtFromLawAndJD", "opinionFromLawAndJDNotReceived", "Opinion from law and judiciary department is not received");
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
						String contentDraftFileInThisLanguage = request.getParameter("contentDraft-file-"+languageCompulsoryInSession);
						if((contentDraftTextInThisLanguage==null || contentDraftTextInThisLanguage.isEmpty())
								&& (contentDraftFileInThisLanguage==null || contentDraftFileInThisLanguage.isEmpty())) {
							String revisedContentDraftTextInThisLanguage = request.getParameter("revised_contentDraft_text_"+languageCompulsoryInSession);
							String revisedContentDraftFileInThisLanguage = request.getParameter("revised-contentDraft-file-"+languageCompulsoryInSession);
							if((revisedContentDraftTextInThisLanguage==null || revisedContentDraftTextInThisLanguage.isEmpty())
									&& (revisedContentDraftFileInThisLanguage==null || revisedContentDraftFileInThisLanguage.isEmpty())) {
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
							String annexureForAmendingBillFileInThisLanguage = request.getParameter("annexureForAmendingBill-file-"+languageAllowedInSession);
							if((annexureForAmendingBillTextInThisLanguage!=null && !annexureForAmendingBillTextInThisLanguage.isEmpty())
									|| (annexureForAmendingBillFileInThisLanguage!=null && !annexureForAmendingBillFileInThisLanguage.isEmpty())) {
								isAnnexureForAmendingBillInAtleastOneLanguage = true;
								break;
							} 
							else {
								String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
								String revisedAnnexureForAmendingBillFileInThisLanguage = request.getParameter("revised_annexureForAmendingBill-file-"+languageAllowedInSession);
								if((revisedAnnexureForAmendingBillTextInThisLanguage!=null && !revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty())
										|| (revisedAnnexureForAmendingBillFileInThisLanguage!=null && !revisedAnnexureForAmendingBillFileInThisLanguage.isEmpty())) {
									isAnnexureForAmendingBillInAtleastOneLanguage = true;
									break;
								}
							}
						}
						if(isAnnexureForAmendingBillInAtleastOneLanguage==true) {
							boolean isAnnexureForAmendingBillInAllContentDraftLanguages = true;						
							for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
								String contentDraftInThisLanguage = request.getParameter("contentDraft_text_"+languageAllowedInSession);
								String revisedContentDraftInThisLanguage = request.getParameter("revised_contentDraft_text_"+languageAllowedInSession);
								if(contentDraftInThisLanguage!=null	&& !contentDraftInThisLanguage.isEmpty()
										|| (revisedContentDraftInThisLanguage!=null	&& !revisedContentDraftInThisLanguage.isEmpty())) {
									String annexureForAmendingBillTextInThisLanguage = request.getParameter("annexureForAmendingBill_text_"+languageAllowedInSession);
									String annexureForAmendingBillFileInThisLanguage = request.getParameter("annexureForAmendingBill-file-"+languageAllowedInSession);
									
									if((annexureForAmendingBillTextInThisLanguage==null || annexureForAmendingBillTextInThisLanguage.isEmpty())
											&& (annexureForAmendingBillFileInThisLanguage==null || annexureForAmendingBillFileInThisLanguage.isEmpty())) {
										
										String revisedAnnexureForAmendingBillTextInThisLanguage = request.getParameter("revised_annexureForAmendingBill_text_"+languageAllowedInSession);
										String revisedAnnexureForAmendingBillFileInThisLanguage = request.getParameter("revised_annexureForAmendingBill-file-"+languageAllowedInSession);
										if((revisedAnnexureForAmendingBillTextInThisLanguage==null || revisedAnnexureForAmendingBillTextInThisLanguage.isEmpty())
												&& (revisedAnnexureForAmendingBillFileInThisLanguage==null || revisedAnnexureForAmendingBillFileInThisLanguage.isEmpty())) {
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
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				String isBallotingRequired = domain.getSession().getParameter(ApplicationConstants.NONOFFICIAL_BILL+"_isBallotingRequired");
				if(isBallotingRequired!=null) {
					if(isBallotingRequired.equals("true")) {
						if(oldRecommendationStatus.getType().startsWith(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED)) {
							String currentPosition = oldRecommendationStatus.getType().replaceFirst(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED, "");
							if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_CONSIDERED + currentPosition)) {
								result.rejectValue("version", "billNotUpdatedUnderConsideration", "Bill has not been considered.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}			
						}
					} else if(isBallotingRequired.equals("false")) {
						if(oldRecommendationStatus.getType().startsWith(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
							if(!domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_CONSIDERED)) {
								result.rejectValue("version", "billNotUpdatedUnderConsideration", "Bill has not been considered.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}			
						}
					}
				}
			} else {
				if(oldRecommendationStatus.getType().startsWith(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED)) {
					String currentPosition = oldRecommendationStatus.getType().replaceFirst(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED, "");
					if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_CONSIDERED + currentPosition)) {
						result.rejectValue("version", "billNotUpdatedUnderConsideration", "Bill has not been considered.");
						domain.setRecommendationStatus(oldRecommendationStatus);
					}			
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
			//validation for laying letter compulsory before updating in 2nd house
			if(oldRecommendationStatus.getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSED) && oldRecommendationStatus.getType().endsWith(ApplicationConstants.BILL_FIRST_HOUSE)) {
				String secondHouseForBill = domain.findSecondHouseType();
				if(domain.getCurrentHouseType().getType().equals(secondHouseForBill)) {
					String currentHouseRound = request.getParameter("currentHouseRound");
					if(currentHouseRound!=null) {
						Map<String, String> layingLetterIdentifiers = new HashMap<String, String>();
						layingLetterIdentifiers.put("deviceId", domain.getId().toString());
						layingLetterIdentifiers.put("layingFor", ApplicationConstants.LAYING_IN_SECONDHOUSE_POST_PASSED_BY_FIRST_HOUSE);				
						layingLetterIdentifiers.put("houseRound", currentHouseRound);
						LayingLetter layingLetter = LayingLetter.findLatestByFieldNames(layingLetterIdentifiers, domain.getLocale());
						if(layingLetter==null) {
							result.rejectValue("version", "layingLetterNotSent", "Letter of bill received in second house is not laid yet.");
							domain.setRecommendationStatus(oldRecommendationStatus);
						} else {							
//							CustomParameter finalAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "BILL_LAYLETTER_FINAL_AUTHORITY"+"_"+secondHouseForBill.toUpperCase(), "");
//							if(finalAuthorityParameter!=null) {
//								Map<String, String> finalLayingLetterDraftIdentifiers =  new HashMap<String, String>();
//								finalLayingLetterDraftIdentifiers.put("layingLetterId", layingLetter.getId().toString());
//								finalLayingLetterDraftIdentifiers.put("editedAs", finalAuthorityParameter.getValue());
//								LayingLetterDraft finalLayingLetterDraft = LayingLetterDraft.findByFieldNames(LayingLetterDraft.class, finalLayingLetterDraftIdentifiers, layingLetter.getLocale());
//								if(finalLayingLetterDraft==null) {
//									result.rejectValue("version", "layingLetterApprovalPending", "Letter of bill received in second house is not approved yet.");
//									domain.setRecommendationStatus(oldRecommendationStatus);
//								}
//							}
							if(!layingLetter.isApproved()) {
								result.rejectValue("version", "layingLetterApprovalPending", "Letter of bill received in second house is not approved yet.");
								domain.setRecommendationStatus(oldRecommendationStatus);
							}
						}
					}
				}
			}
		}				
	}	
	
	private void populateSupportingMembers(final Bill domain,final String role,final HttpServletRequest request){
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
						/** Auto approval for submission by typist etc. roles **/
						CustomParameter supportingMemberAutoApprovalAllowedTo = 
								CustomParameter.findByName(CustomParameter.class, 
										"BIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
						if(supportingMemberAutoApprovalAllowedTo != null) {
							if(supportingMemberAutoApprovalAllowedTo.getValue().contains(role)) {
								Status APPROVED = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, domain.getLocale());
								supportingMember.setDecisionStatus(APPROVED);								
								supportingMember.setApprovalType(ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
								supportingMember.setApprovalDate(new Date());
								supportingMember.setApprovedTitles(domain.getTitles());
								supportingMember.setApprovedContentDrafts(domain.getContentDrafts());
								supportingMember.setApprovedStatementOfObjectAndReasonDrafts(domain.getStatementOfObjectAndReasonDrafts());
								supportingMember.setApprovedFinancialMemorandumDrafts(domain.getFinancialMemorandumDrafts());
								supportingMember.setApprovedStatutoryMemorandumDrafts(domain.getStatutoryMemorandumDrafts());								
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
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = BillController.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);	
		/**** add/update revised titles in domain ****/
		List<TextDraft> revisedTitles = BillController.updateDraftsOfGivenType(domain, "revised_title", request);
		domain.setRevisedTitles(revisedTitles);
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = BillController.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		/**** add/update revised content drafts in domain ****/
		List<TextDraft> revisedContentDrafts = BillController.updateDraftsOfGivenType(domain, "revised_contentDraft", request);
		domain.setRevisedContentDrafts(revisedContentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		/**** add/update revised 'statement of object and reason drafts' in domain ****/
		List<TextDraft> revisedStatementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(domain, "revised_statementOfObjectAndReasonDraft", request);		
		domain.setRevisedStatementOfObjectAndReasonDrafts(revisedStatementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		/**** add/update revised financial memorandum drafts in domain ****/
		List<TextDraft> revisedFinancialMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "revised_financialMemorandumDraft", request);		
		domain.setRevisedFinancialMemorandumDrafts(revisedFinancialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
		/**** add/update revised statutory memorandum drafts in domain ****/
		List<TextDraft> revisedStatutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "revised_statutoryMemorandumDraft", request);
		domain.setRevisedStatutoryMemorandumDrafts(revisedStatutoryMemorandumDrafts);
		
		/**** add/update annexures for amending bill in domain ****/
		List<TextDraft> annexuresForAmendingBill = BillController.updateDraftsOfGivenType(domain, "annexureForAmendingBill", request);
		domain.setAnnexuresForAmendingBill(annexuresForAmendingBill);	
		/**** add/update revised annexures for amending bill in domain ****/
		List<TextDraft> revisedAnnexuresForAmendingBill = BillController.updateDraftsOfGivenType(domain, "revised_annexureForAmendingBill", request);
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
				} 
			}
		}				
		if(bill==null) {
			bill = Bill.findById(Bill.class, domain.getId());
		}
		domain.setVotingDetails(bill.getVotingDetails());
		//version is updated from db as some other internal operation such as adding voting details may update bill
		domain.setVersion(bill.getVersion());
		super.populateUpdateIfErrors(model, domain, request);
	}
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Bill domain,
			final HttpServletRequest request) {		
		Bill bill = null;
		
		/**** add/update titles in domain ****/
		List<TextDraft> titles = BillController.updateDraftsOfGivenType(domain, "title", request);
		domain.setTitles(titles);	
		/**** add/update revised titles in domain ****/
		List<TextDraft> revisedTitles = BillController.updateDraftsOfGivenType(domain, "revised_title", request);
		domain.setRevisedTitles(revisedTitles);
		
		/**** Status ,Internal Status,Recommendation Status,submission date,creation date,created by,created as *****/		
		/**** In case of submission ****/
		String operation=request.getParameter("operation");
		String strUserGroupType=request.getParameter("usergroupType");
		if(domain.getHouseType()!=null && domain.getSession()!=null
				&&  domain.getType()!=null && domain.getPrimaryMember()!=null && (domain.getTitles()!=null && !domain.getTitles().isEmpty())
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
					Status newstatus=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_SUBMIT, domain.getLocale());
					domain.setStatus(newstatus);
					domain.setInternalStatus(newstatus);
					domain.setRecommendationStatus(newstatus);				
				} else{
					Status status=Status.findByFieldName(Status.class, "type", ApplicationConstants.BILL_COMPLETE, domain.getLocale());
					domain.setStatus(status);
					domain.setInternalStatus(status);
					domain.setRecommendationStatus(status);
				}
			}			
		} else{
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
			}
		}
		/**** updating various dates including submission date and creation date ****/
		String strCreationDate=request.getParameter("setCreationDate");
		String strSubmissionDate=request.getParameter("setSubmissionDate");
		String strDateOfOpinionSoughtFromLawAndJD=request.getParameter("setDateOfOpinionSoughtFromLawAndJD");
		String strDateOfRecommendationFromGovernor=request.getParameter("setDateOfRecommendationFromGovernor");
		String strDateOfRecommendationFromPresident=request.getParameter("setDateOfRecommendationFromPresident");
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
				if(strDateOfRecommendationFromGovernor!=null&&!strDateOfRecommendationFromGovernor.isEmpty()) {
					domain.setDateOfRecommendationFromGovernor(format.parse(strDateOfRecommendationFromGovernor));
				} 
				if(strDateOfRecommendationFromPresident!=null&&!strDateOfRecommendationFromPresident.isEmpty()) {
					domain.setDateOfRecommendationFromPresident(format.parse(strDateOfRecommendationFromPresident));
				} 								
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/**** add/update content drafts in domain ****/
		List<TextDraft> contentDrafts = BillController.updateDraftsOfGivenType(domain, "contentDraft", request);
		domain.setContentDrafts(contentDrafts);	
		/**** add/update revised content drafts in domain ****/
		List<TextDraft> revisedContentDrafts = BillController.updateDraftsOfGivenType(domain, "revised_contentDraft", request);
		domain.setRevisedContentDrafts(revisedContentDrafts);	
		
		/**** add/update 'statement of object and reason drafts' in domain ****/
		List<TextDraft> statementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		domain.setStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
		/**** add/update revised 'statement of object and reason drafts' in domain ****/
		List<TextDraft> revisedStatementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(domain, "revised_statementOfObjectAndReasonDraft", request);		
		domain.setRevisedStatementOfObjectAndReasonDrafts(revisedStatementOfObjectAndReasonDrafts);
		
		/**** add/update financial memorandum drafts in domain ****/
		List<TextDraft> financialMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		domain.setFinancialMemorandumDrafts(financialMemorandumDrafts);
		/**** add/update revised financial memorandum drafts in domain ****/
		List<TextDraft> revisedFinancialMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "revised_financialMemorandumDraft", request);		
		domain.setRevisedFinancialMemorandumDrafts(revisedFinancialMemorandumDrafts);
		
		/**** add/update statutory memorandum drafts in domain ****/
		List<TextDraft> statutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		domain.setStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
		/**** add/update revised statutory memorandum drafts in domain ****/
		List<TextDraft> revisedStatutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(domain, "revised_statutoryMemorandumDraft", request);
		domain.setRevisedStatutoryMemorandumDrafts(revisedStatutoryMemorandumDrafts);	
		
		/**** add/update annexures for amending bill in domain ****/
		List<TextDraft> annexuresForAmendingBill = BillController.updateDraftsOfGivenType(domain, "annexureForAmendingBill", request);
		domain.setAnnexuresForAmendingBill(annexuresForAmendingBill);	
		/**** add/update revised annexures for amending bill in domain ****/
		List<TextDraft> revisedAnnexuresForAmendingBill = BillController.updateDraftsOfGivenType(domain, "revised_annexureForAmendingBill", request);
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
        domain.setSections(bill.getSections());
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
				&&domain.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_WITHDRAWN)) {
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
					List<WorkflowDetails> workflowDetails=WorkflowDetails.create(domain,tasks,ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW,null, "");
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
					String endflag=request.getParameter("endflag");
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
								WorkflowDetails.create(domain,task,ApplicationConstants.TRANSLATION_WORKFLOW,ApplicationConstants.BILL_RECOMMEND_TRANSLATION,nextUserGroupType, level);
							}
						}
					}
					/**** Workflow Started ****/
					bill.setRemarksForTranslation(domain.getRemarks());
					bill.simpleMerge();					
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
					String endflag=request.getParameter("endflag");
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
								WorkflowDetails.create(domain,task,ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW,ApplicationConstants.BILL_RECOMMEND_OPINION_FROM_LAWANDJD,nextUserGroupType, level);
							}
						}
					}
				} else if(operation.equals("sendForRecommendationFromGovernor")){					
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
					String endflag=request.getParameter("endflag");
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
								WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW,ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_GOVERNOR,nextUserGroupType, level);
							}
						}
					}
				} else if(operation.equals("sendForRecommendationFromPresident")){					
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
					String endflag=request.getParameter("endflag");
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
								WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW,ApplicationConstants.BILL_RECOMMEND_RECOMMENDATION_FROM_PRESIDENT,nextUserGroupType, level);
							}
						}
					}
				} else if(operation.equals("sendForNameclubbing")) {					
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
						String endflag=request.getParameter("endflag");
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
									WorkflowDetails.create(domain,task,workflowDetailsType,null,nextUserGroupType, level);
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
					String endflag=request.getParameter("endflag");
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
								WorkflowDetails.create(domain,task,workflowDetailsType,null,nextUserGroupType, level);
							}
						}
					}
					/**** Workflow Started ****/
					if(domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_CHECKLIST_PARAMETER).equals("yes") 
							&& domain.getOriginalType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)
							&& request.getParameter("recommendationFromGovernorStatus").equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_NOTSEND)) {						
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
								endflag=request.getParameter("endflag");
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
											WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW,expectedStatus.getType(),nextUserGroupType, level);
										}
									}
								}															
							}							
						}						
					}
					
					if(domain.findChecklistValue(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_CHECKLIST_PARAMETER).equals("yes") 
							&& domain.getOriginalType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)
							&& request.getParameter("recommendationFromPresidentStatus").equals(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_NOTSEND)) {						
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
								endflag=request.getParameter("endflag");
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
											WorkflowDetails.create(domain,task,ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW,expectedStatus.getType(),nextUserGroupType, level);
										}
									}
								}								
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
			if(drafts!=null && !drafts.isEmpty()) {
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
				model.addAttribute("errorcode","notrevisedyet");
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
					title.setShortText("");
					titles.add(title);
				}
			}
			model.addAttribute("titles",titles);
			if(domain.getRevisedTitles()!=null && !domain.getRevisedTitles().isEmpty()) {
				for(TextDraft revisedTitle: domain.getRevisedTitles()) {
					model.addAttribute("revisedTitle_"+revisedTitle.getLanguage().getType(), revisedTitle.getText());
					model.addAttribute("revisedTitle_shortText_"+revisedTitle.getLanguage().getType(), revisedTitle.getShortText());
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
					contentDraft.setFile("");
					contentDrafts.add(contentDraft);
				}
			}
			model.addAttribute("contentDrafts",contentDrafts);
			if(domain.getRevisedContentDrafts()!=null && !domain.getRevisedContentDrafts().isEmpty()) {
				for(TextDraft revisedContentDraft: domain.getRevisedContentDrafts()) {
					model.addAttribute("revisedContentDraft_"+revisedContentDraft.getLanguage().getType(), revisedContentDraft.getText());
					model.addAttribute("revisedContentDraft-file-"+revisedContentDraft.getLanguage().getType(), revisedContentDraft.getFile());
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
					annexureForAmendingBill.setFile("");
					annexuresForAmendingBill.add(annexureForAmendingBill);
				}
			}
			model.addAttribute("annexuresForAmendingBill",annexuresForAmendingBill);	
			if(domain.getRevisedAnnexuresForAmendingBill()!=null && !domain.getRevisedAnnexuresForAmendingBill().isEmpty()) {
				for(TextDraft revisedAnnexureForAmendingBill: domain.getRevisedAnnexuresForAmendingBill()) {
					model.addAttribute("revisedAnnexureForAmendingBill_"+revisedAnnexureForAmendingBill.getLanguage().getType(), revisedAnnexureForAmendingBill.getText());
					model.addAttribute("revisedAnnexureForAmendingBill-file-"+revisedAnnexureForAmendingBill.getLanguage().getType(), revisedAnnexureForAmendingBill.getFile());
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
	
	public static List<TextDraft> updateDraftsOfGivenType(Bill domain, String typeOfDraft, HttpServletRequest request) {
		List<TextDraft> draftsOfGivenType = new ArrayList<TextDraft>();
		String languagesAllowedInSession = domain.getSession().getParameter(domain.getType().getType() + "_languagesAllowed");
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String draftTextInThisLanguage = request.getParameter(typeOfDraft+"_text_"+languageAllowedInSession);
			String draftShortTextInThisLanguage = request.getParameter(typeOfDraft+"_shortText_"+languageAllowedInSession);
			String typeOfDraftForFileField = typeOfDraft.replaceAll("_", "-");
			String draftFileInThisLanguage = request.getParameter(typeOfDraftForFileField+"-file-"+languageAllowedInSession);
			if((draftTextInThisLanguage!=null && !draftTextInThisLanguage.isEmpty())
					|| (draftFileInThisLanguage!=null && !draftFileInThisLanguage.isEmpty())) {
				TextDraft draftOfGivenType = null;				
				String draftIdInThisLanguage = request.getParameter(typeOfDraft+"_id_"+languageAllowedInSession);
				if(draftIdInThisLanguage!=null && !draftIdInThisLanguage.isEmpty()) {
					draftOfGivenType = TextDraft.findById(TextDraft.class, Long.parseLong(draftIdInThisLanguage));					
				} else {
					draftOfGivenType = new TextDraft();
				}
				draftOfGivenType.setText(draftTextInThisLanguage);
				draftOfGivenType.setShortText(draftShortTextInThisLanguage);
				draftOfGivenType.setFile(draftFileInThisLanguage);
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
			logger.debug("doDiscussBills", e);
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
	
	@RequestMapping(value="/getSchedule7OfConstitution",method=RequestMethod.GET)
	public String getSchedule7OfConstitution(final ModelMap model, final HttpServletRequest request, final Locale locale){	
		String language = request.getParameter("language");
		model.addAttribute("language", language);
		String sessionId = request.getParameter("sessionId");
		model.addAttribute("sessionId", sessionId);
		String deviceTypeId = request.getParameter("deviceTypeId");
		model.addAttribute("deviceTypeId", deviceTypeId);
		return "bill/schedule7OfConstitution";
	}
	
	@RequestMapping(value="/viewSchedule7OfConstitution", method=RequestMethod.GET)
	public void viewSchedule7OfConstitution(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		boolean isError = false;
		String errorCode = null;
		String language = request.getParameter("language");
		String sessionId = request.getParameter("sessionId");
		String deviceTypeId = request.getParameter("deviceTypeId");
		if(language!=null && sessionId!=null && deviceTypeId!=null && !language.isEmpty() && !sessionId.isEmpty() && !deviceTypeId.isEmpty()) {
			Session session = Session.findById(Session.class, Long.parseLong(sessionId));
			if(session!=null) {
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
				if(deviceType!=null) {
					String schedule7OfConstitutionForGivenLanguage = session.getParameter(deviceType.getType().trim()+"_schedule7OfConstitution_"+language);
					if(schedule7OfConstitutionForGivenLanguage!=null) {
						Document document = null;
						try {
							document = Document.findByTag(schedule7OfConstitutionForGivenLanguage);
						} catch (ELSException e1) {
							e1.printStackTrace();
							isError = true;
							errorCode = "schedule7OfConstitutionNotFoundFor"+language;
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
				            isError = true;
							errorCode = "schedule7OfConstitutionDamagedFor"+language;
				        }
					} else {
						isError = true;
						errorCode = "schedule7OfConstitutionNotSetFor"+language;
					}
				} else {
					isError = true;
					errorCode = "devicetypenotfound";
				}				
			} else {
				isError = true;
				errorCode = "sessionnotfound";
			}
		} else {
			isError = true;
			errorCode = "incorrectRequestParam";
		}
		if(isError) {
			MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill."+errorCode, locale.toString());
    		try {
				if(message != null) {
	    			if(!message.getValue().isEmpty()) {
	    				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	    			} else {
	    				response.getWriter().println("<h3>Sorry..Seems Some Error with Schedule 7 Of Constitution File. Please contact administrator.</h3>");
	    			}
	    		} else {
	    			response.getWriter().println("<h3>Sorry..Seems Some Error with Schedule 7 Of Constitution File. Please contact administrator.</h3>");
	    		}
    		} catch(IOException ex) {
    			logger.error("Error in writing to response");
    		}
		}		
	}
	
	@RequestMapping(value="/getInstructionalOrder",method=RequestMethod.GET)
	public String getInstructionalOrder(final ModelMap model, final HttpServletRequest request, final Locale locale){	
		String sessionId = request.getParameter("sessionId");
		model.addAttribute("sessionId", sessionId);
		String deviceTypeId = request.getParameter("deviceTypeId");
		model.addAttribute("deviceTypeId", deviceTypeId);
		return "bill/instructionalOrder";
	}
	
	@RequestMapping(value="/viewInstructionalOrder", method=RequestMethod.GET)
	public void viewInstructionalOrder(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		boolean isError = false;
		String errorCode = null;
		String sessionId = request.getParameter("sessionId");
		String deviceTypeId = request.getParameter("deviceTypeId");
		if(sessionId!=null && deviceTypeId!=null && !sessionId.isEmpty() && !deviceTypeId.isEmpty()) {
			Session session = Session.findById(Session.class, Long.parseLong(sessionId));
			if(session!=null) {
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));
				if(deviceType!=null) {
					String instructionalOrder = session.getParameter(deviceType.getType().trim()+"_instructionalOrder");
					if(instructionalOrder!=null) {
						Document document = null;
						try {
							document = Document.findByTag(instructionalOrder);
						} catch (ELSException e1) {
							e1.printStackTrace();
							isError = true;
							errorCode = "instructionalOrderFileNotFound";
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
				            isError = true;
							errorCode = "instructionalOrderFileDamaged";
				        }
					} else {
						isError = true;
						errorCode = "instructionalOrderParameterNotSet";
					}
				} else {
					isError = true;
					errorCode = "devicetypenotfound";
				}				
			} else {
				isError = true;
				errorCode = "sessionnotfound";
			}
		} else {
			isError = true;
			errorCode = "incorrectRequestParam";
		}
		if(isError) {
			MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill."+errorCode, locale.toString());
    		try {
				if(message != null) {
	    			if(!message.getValue().isEmpty()) {
	    				response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
	    			} else {
	    				response.getWriter().println("<h3>Sorry..Seems Some Error with Instructional Order File. Please contact administrator.</h3>");
	    			}
	    		} else {
	    			response.getWriter().println("<h3>Sorry..Seems Some Error with Instructional Order File. Please contact administrator.</h3>");
	    		}
    		} catch(IOException ex) {
    			logger.error("Error in writing to response");
    		}
		}		
	}
	
	@RequestMapping(value="/referAct/init",method=RequestMethod.GET)
	public String initReferActOrOrdinance(final HttpServletRequest request,final ModelMap model, final Locale locale){
		String returnPath = null;
		String action = request.getParameter("action");
		if(action != null){
			if(!action.isEmpty()){				
				model.addAttribute("action", action);
				if(action.equals("act")){
					CustomParameter defaultTitleLanguage = CustomParameter.findByName(CustomParameter.class, "ACT_DEFAULT_LANGUAGE", "");
					if(defaultTitleLanguage!=null) {
						if(defaultTitleLanguage.getValue()!=null && !defaultTitleLanguage.getValue().isEmpty()) {
							model.addAttribute("defaultTitleLanguage", defaultTitleLanguage.getValue());
						} else {
							logger.error("**** Custom Parameter 'ACT_DEFAULT_LANGUAGE' is not set. ****");
							model.addAttribute("errorcode", "actdefaultlanguagenotset");
							return "bill/error";
						}
					} else {
						logger.error("**** Custom Parameter 'ACT_DEFAULT_LANGUAGE' is not set. ****");
						model.addAttribute("errorcode", "actdefaultlanguagenotset");
						return "bill/error";
					}
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
								returnPath = "bill/referactinit";
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
					CustomParameter defaultTitleLanguage = CustomParameter.findByName(CustomParameter.class, "ORDINANCE_DEFAULT_LANGUAGE", "");
					if(defaultTitleLanguage!=null) {
						if(defaultTitleLanguage.getValue()!=null && !defaultTitleLanguage.getValue().isEmpty()) {
							model.addAttribute("defaultTitleLanguage", defaultTitleLanguage.getValue());
						} else {
							logger.error("**** Custom Parameter 'ORDINANCE_DEFAULT_LANGUAGE' is not set. ****");
							model.addAttribute("errorcode", "ordinancedefaultlanguagenotset");
							return "bill/error";
						}
					} else {
						logger.error("**** Custom Parameter 'ORDINANCE_DEFAULT_LANGUAGE' is not set. ****");
						model.addAttribute("errorcode", "ordinancedefaultlanguagenotset");
						return "bill/error";
					}
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
								returnPath = "bill/referordinanceinit";
							} else{
								logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set with empty value. ****");
								model.addAttribute("errorcode", "firstactyearnotset");					
							}
						} else{
							logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' is set to null. ****");
							model.addAttribute("errorcode", "firstactyearnotset");				
						}		
					} else{
						logger.error("**** Custom Parameter 'FIRST_ACT_YEAR' or 'ORDINANCE_DEFAULT_LANGUAGE' is not set. ****");
						model.addAttribute("errorcode", "firstactyearnotset");						
					}	
				}
					
			}
		}
		if(returnPath!=null) {
			return returnPath;
		} else {
			return "bill/error";
		}		
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
			String languageLocale = ApplicationLocale.findLocaleFromLanguageType(languageForDocketReport);
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
				StringBuffer supportingMemberNames=new StringBuffer();
				List<SupportingMember> supportingMembers = bill.getSupportingMembers();
				if(supportingMembers!=null) {					
					for(SupportingMember sm: supportingMembers) {
						supportingMemberNames.append(sm.getMember().getFullname()+",");
					}					
				}
				List<ClubbedEntity> clubbedEntities = bill.findClubbedEntitiesByBillSubmissionDate(ApplicationConstants.ASC, bill.getLocale());
				if(clubbedEntities!=null){
					for(ClubbedEntity ce:clubbedEntities){
						/** show only those clubbed bills which are not in state of
						 * (processed to be putup for nameclubbing, putup for nameclubbing, pending for nameclubbing approval) 
						 **/
						if(ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_CLUBBED)
								|| ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
							String tempPrimary=ce.getBill().getPrimaryMember().getFullname();
							if(!supportingMemberNames.toString().contains(tempPrimary)){
								supportingMemberNames.append(ce.getBill().getPrimaryMember().getFullname()+",");
							}
							List<SupportingMember> clubbedSupportingMember=ce.getBill().getSupportingMembers();
							if(clubbedSupportingMember!=null){
								if(!clubbedSupportingMember.isEmpty()){
									for(SupportingMember l:clubbedSupportingMember){
										String tempSupporting=l.getMember().getFullname();
										if(!supportingMemberNames.toString().contains(tempSupporting)){
											supportingMemberNames.append(tempSupporting+",");
										}
									}
								}
							}							
						}						
					}
				}
				if(!supportingMemberNames.toString().isEmpty()){
					supportingMemberNames.deleteCharAt(supportingMemberNames.length()-1);
				}
				reportDataAsList.add(supportingMemberNames.toString());
			}
			//generate report
			java.io.File reportFile = null;
			if(requisitionFor.equals(ApplicationConstants.BILL_PRESS_COPY)) {
				try {
					reportFile = generateReportUsingFOP(reportDataAsList.toArray(), "template_bill_docket_report", "WORD", "bill_docket_report", locale.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.debug("generateDocket",e);					
					e.printStackTrace();
				}
			} else if(requisitionFor.equals(ApplicationConstants.BILL_GAZETTE_COPY)) {
				try {
					reportFile = generateReportUsingFOP(reportDataAsList.toArray(), "template_bill_gazette_report", "WORD", "bill_gazette_report", locale.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.debug("generateDocket",e);
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
//			String languageLocale = ApplicationLocale.findLocaleFromLanguageType(languageForGazetteReport);
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
				CustomParameter cstpDeploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(cstpDeploymentServer != null){
					if(cstpDeploymentServer.getValue() != null && !cstpDeploymentServer.getValue().isEmpty()){
						try {
							if(cstpDeploymentServer.getValue().equals("TOMCAT")){
								strHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
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
					houseType = HouseType.findByName(HouseType.class, strHouseType, locale.toString());
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
					logger.debug("generateDocket",e1);
					model.addAttribute("error", e1.getParameter());
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
							logger.debug("generateDocket",e);
							model.addAttribute("error", e.getParameter());
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
							boolean validForSelectedHouseType = false;
							String recommendationStatusType = b.getRecommendationStatus().getType();
							if(recommendationStatusType.equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
								if(b.findFirstHouseType()!=null) {
									if(b.findFirstHouseType().equals(houseType.getType())) {
										validForSelectedHouseType = true;										
									}
								}
							} else if(recommendationStatusType.startsWith(ApplicationConstants.BILL_PROCESSED_PASSED)
									&& recommendationStatusType.endsWith(ApplicationConstants.BILL_FIRST_HOUSE)) {
								if(b.findSecondHouseType()!=null) {
									if(b.findSecondHouseType().equals(houseType.getType())) {
										validForSelectedHouseType = true;										
									}
								}
							} else if(recommendationStatusType.startsWith(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED)
									&& recommendationStatusType.endsWith(ApplicationConstants.BILL_FIRST_HOUSE)) {
								if(b.findFirstHouseType()!=null) {
									if(b.findFirstHouseType().equals(houseType.getType())) {
										validForSelectedHouseType = true;										
									}
								}
							} else if(recommendationStatusType.startsWith(ApplicationConstants.BILL_PROCESSED_TOBEDISCUSSED)
									&& recommendationStatusType.endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
								if(b.findSecondHouseType()!=null) {
									if(b.findSecondHouseType().equals(houseType.getType())) {
										validForSelectedHouseType = true;										
									}
								}
							}
							if(validForSelectedHouseType) {
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
					}
					model.addAttribute("introBills", billSVOToReceiveIntroductionDate);
					model.addAttribute("discussBills", billSVOToReceiveDiscussionDate);
					
				} catch (ELSException e) {
					logger.debug("generateDocket",e);
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
			logger.debug("updateIntroductionDate",e);
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
		formattedPassedByFirstHouseForGivenRoundDate = FormaterUtil.formatMonthInLocaleLanguageDate(formattedPassedByFirstHouseForGivenRoundDate, locale.toString());
		reportFields.add(formattedPassedByFirstHouseForGivenRoundDate);
		
		String formattedLayingDate = FormaterUtil.formatDateToString(layingDate, dateFormatForReport.getValue(), locale.toString());
		formattedLayingDate = FormaterUtil.formatMonthInLocaleLanguageDate(formattedLayingDate, locale.toString());
		reportFields.add(formattedLayingDate);
		
		String formattedLayingDay = FormaterUtil.formatDateToString(layingDate, dayFormat.getValue(), locale.toString());
		formattedLayingDay = FormaterUtil.getDayInLocaleLanguage(formattedLayingDay, locale.toString());
		reportFields.add(formattedLayingDay);
		
		String formattedCurrentDate = FormaterUtil.formatDateToString(new Date(), dateFormatForReport.getValue(), locale.toString());
		formattedCurrentDate = FormaterUtil.formatMonthInLocaleLanguageDate(formattedCurrentDate, locale.toString());
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
					logger.debug("generateDocket",e);
					model.addAttribute("error", e.getParameter());
					e.printStackTrace();
				}
				Status toBeIntroduced = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED, locale.toString());
				Status departmentIntimated = Status.findByType(ApplicationConstants.BILL_PROCESSED_DEPARTMENTINTIMATED, locale.toString());
				Status admitted = Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, locale.toString());
				
				model.addAttribute("houseType", houseType.getType());
				model.addAttribute("sessionYear", FormaterUtil.formatNumberNoGrouping(session.getYear(), locale.toString()));
				model.addAttribute("house", houseType.getName());
				model.addAttribute("sessionPlace", session.getPlace().getPlace());
				
				Role role = Role.findByFieldName(Role.class, "type", "BIS_PRINCIPALSECRETARY", locale.toString());
				if(role!=null) {
					model.addAttribute("whopost", role.getLocalizedName());
				}
				List<User> users = User.findByRole(true, role.getName(), locale.toString());
				model.addAttribute("who", users.get(0).findFirstLastName());				
						
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("internalStatusId", new String[]{toBeIntroduced.getId().toString()});
				parameters.put("recommendationStatusId", new String[]{departmentIntimated.getId().toString()});
				parameters.put("statusId", new String[]{admitted.getId().toString()});
				
				Language lang = Language.findByFieldName(Language.class, "type", ApplicationLocale.findLanguageTypeFromLocale(locale.toString()), locale.toString());
				parameters.put("languageId", new String[]{lang.getId().toString()});
				@SuppressWarnings("rawtypes")
				List report = Query.findReport("BILL_PATRAKBHAG_DON", parameters);
				
				model.addAttribute("report", report);
				model.addAttribute("san", "सन");
				CustomParameter footer = null;
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					model.addAttribute("houseShort", "वि. स. वि.");
					footer = CustomParameter.findByName(CustomParameter.class, "BILL_PATRAKBHAG2_FOOTR_LOWERHOUSE", "");
				}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					model.addAttribute("houseShort", "वि. प. वि.");
					footer = CustomParameter.findByName(CustomParameter.class, "BILL_PATRAKBHAG2_FOOTR_UPPERHOUSE", "");
				}
				
				model.addAttribute("footer", footer.getValue());
				
				if(strDistributionDate != null && !strDistributionDate.isEmpty()){
					
					patrakBhagTwoDateFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "PATRAK_BHAG_TWO_DATE_FORMAT", "");
					dayFormat = CustomParameter.findByFieldName(CustomParameter.class, "name", "DAY_OF_WEEK_FORMAT", "");
					/*** To form the patrakbhag don formatted date ***/
					com.ibm.icu.util.Calendar calendar = com.ibm.icu.util.Calendar.getInstance();
					calendar.setTime(date);
					Integer intDay = calendar.get(Calendar.DAY_OF_MONTH);
					Integer intMonth = calendar.get(Calendar.MONTH);
					Integer intYear = calendar.get(Calendar.YEAR);
					String formattedCurrentDate = FormaterUtil.formatNumberNoGrouping(intDay, locale.toString()) + 
								" " + FormaterUtil.getMonthInLocaleLanguage(intMonth, locale.toString()) + 
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
	
	@RequestMapping(value = "/citationReport", method = RequestMethod.GET)
	public String getCitationReportInit(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String currentHouseTypeType = request.getParameter("houseType");
		String selectedYearStr = request.getParameter("sessionYear");
		String selectedBillIdStr = request.getParameter("billId");
		/**** House Types ****/
		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale.toString());
		if(houseTypes==null) {
			logger.error("**** house types are not available. ****");
			model.addAttribute("errorcode", "HOUSETYPES_NOT_AVAILABLE");		
			return "bill/error";
		}
		if(houseTypes.isEmpty()) {
			logger.error("**** house types are not available. ****");
			model.addAttribute("errorcode", "HOUSETYPES_NOT_AVAILABLE");		
			return "bill/error";
		}
		model.addAttribute("houseTypes", houseTypes);
		/**** Current HouseType ****/
		HouseType currentHouseType = null;
		if(currentHouseTypeType!=null) {
			if(!currentHouseTypeType.isEmpty()) {
				if(!currentHouseTypeType.equals(ApplicationConstants.BOTH_HOUSE)) {
					currentHouseType = HouseType.findByFieldName(HouseType.class, "type", currentHouseTypeType, locale.toString());
					if(currentHouseType!=null) {
						model.addAttribute("currentHouseType", currentHouseType);					
					} else {
						logger.error("**** Check request parameter 'houseType' for incorrect value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");		
						return "bill/error";
					}
				}								
			} else {
				logger.error("**** Check request parameter 'houseType' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "bill/error";
			}
		} else {
			logger.error("**** Check request parameter 'houseType' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "bill/error";
		}
		/**** Selected Bill ****/
		Bill selectedBill = null;
		if(selectedBillIdStr!=null) {
			if(!selectedBillIdStr.isEmpty()) {
				try {
					long selectedBillId = Long.parseLong(selectedBillIdStr);
					selectedBill = Bill.findById(Bill.class, selectedBillId);
					if(selectedBill!=null) {
						if(selectedBill.getNumber()!=null) {
							model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
							model.addAttribute("selectedBillId", selectedBill.getId());
							/**** Citation Statuses Allowed For Selected Bill In Selected House ****/
							String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(selectedBill, currentHouseType.getType());
							CustomParameter citationStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_CITATION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase(), "");
							if(citationStatusParameter!=null) {
								if(citationStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = citationStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseType.getType() + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> citationStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									model.addAttribute("citationStatuses", citationStatuses);
								} else {
									logger.error("Custom Parameter 'BILL_CITATION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase()+"' is not set properly");
									model.addAttribute("errorcode", "bill_citation_statusoptions_setincorrect");
									return "bill/error";
								}
							} else {
								logger.error("Custom Parameter 'BILL_CITATION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase()+"' is not set");
								model.addAttribute("errorcode", "bill_citation_statusoptions_notset");
								return "bill/error";
							}
						}						
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'billId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "bill/error";
				}								
			}
		}
		/**** Bill Year ****/
		Integer selectedYear = null;
		if(selectedBill!=null) {
			selectedYear = Bill.findYear(selectedBill);
		}
		if(selectedYear==null) {
			if(selectedYearStr!=null) {
				if(!selectedYearStr.isEmpty()) {
					try {
						selectedYear = Integer.parseInt(selectedYearStr);					
					} catch(NumberFormatException ne) {
						logger.error("**** Check request parameter 'year' for non-numeric value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");		
						return "bill/error";
					}
				} else {
					logger.error("**** Check request parameter 'year' for empty value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
					return "bill/error";
				}
			} else {
				logger.error("**** Check request parameter 'year' for null value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
				return "bill/error";
			}
		}
		if(selectedYear!=null) {
			model.addAttribute("formattedSelectedYear", FormaterUtil.formatNumberNoGrouping(selectedYear, locale.toString()));
			model.addAttribute("selectedYear", selectedYear);
		}	
		/**** Bill HouseType ****/
		HouseType selectedHouseType = currentHouseType;
		if(selectedBill!=null) {
			if(selectedBill.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				selectedHouseType = selectedBill.getHouseType();
			} else if(selectedBill.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
				selectedHouseType = selectedBill.getIntroducingHouseType();
			}
		}
		if(selectedHouseType!=null) {
			model.addAttribute("selectedHouseType", selectedHouseType);
		}
		/**** Default Date For Status (Current Date) ****/
		model.addAttribute("currentDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
		return "bill/citationreport/init";
	}
	
	@RequestMapping(value = "/generateCitationReport", method = RequestMethod.GET)
	public String generateCitationReport(final ModelMap model, final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		String reportPage = "";
		String selectedBillId = request.getParameter("deviceId");
		String status = request.getParameter("status");
		String statusDate = request.getParameter("statusDate");
		if(selectedBillId!=null&&status!=null&&statusDate!=null) {
			if(!selectedBillId.isEmpty()&&!statusDate.isEmpty()&&!status.isEmpty()) {
				try {
					Bill bill = Bill.findById(Bill.class, Long.parseLong(selectedBillId));
					if(bill!=null) {
						if(bill.getNumber()!=null) {
							model.addAttribute("billNumber", FormaterUtil.formatNumberNoGrouping(bill.getNumber(), locale.toString()));
						} else {
							model.addAttribute("billNumber", "");
						}
						model.addAttribute("billDeviceType",bill.getType().getType());
						String billTitle = bill.getDefaultTitle();
						model.addAttribute("billTitle", billTitle);
						Integer billYear = Bill.findYear(bill);
						if(billYear!=null) {
							model.addAttribute("billYear", FormaterUtil.formatNumberNoGrouping(billYear, locale.toString()));
						} else {
							model.addAttribute("billYear", "");
						}
						model.addAttribute("statusDate", statusDate);
						if(status.startsWith(ApplicationConstants.BILL_PROCESSED_CONSIDERED)) {
							reportPage = ApplicationConstants.BILL_PROCESSED_CONSIDERED + "_citation";
						} else if(status.startsWith(ApplicationConstants.BILL_PROCESSED_WITHDRAWN)) {
							reportPage = ApplicationConstants.BILL_PROCESSED_WITHDRAWN + "_citation";
						} else if(status.startsWith(ApplicationConstants.BILL_PROCESSED_DISCUSSEDCLAUSEBYCLAUSE)) {
							reportPage = ApplicationConstants.BILL_PROCESSED_DISCUSSEDCLAUSEBYCLAUSE + "_citation";
						} else if(status.startsWith(ApplicationConstants.BILL_PROCESSED_PASSED)) {
							reportPage = ApplicationConstants.BILL_PROCESSED_PASSED + "_citation";
						} else {
							reportPage = status + "_citation";
						}						
					} else {
						logger.error("**** Check request parameter 'deviceId' for invalid value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return "bill/error";
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'deviceId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
				}
			} else {
				logger.error("**** Check request parameter 'deviceId', 'status', and 'statusDate' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");
				return "bill/error";
			}
		} else {
			logger.error("**** Check request parameter 'deviceId', 'status', and 'statusDate' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");
			return "bill/error";
		}
		response.setContentType("text/html; charset=utf-8");
		return "bill/citationreport/"+reportPage;
	}
	
	@RequestMapping(value="/registerReport", method=RequestMethod.GET)
	public @ResponseBody void generateRegisterReport(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		
		java.io.File reportFile = null;		
		Map<String,String> reportData = new LinkedHashMap<String, String>();
		String firstHouse = "";
		String secondHouse = "";
		
		Boolean isError = false;		
		String billId = request.getParameter("billId");
		String outputFormat = request.getParameter("outputFormat");
		if(billId!=null && outputFormat!=null && !billId.isEmpty() && !outputFormat.isEmpty()) {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
			if(bill==null) {
				logger.error("Bill not found with Id = " + billId);
				isError = true;
			} else {
				MessageResource keyLabel = null;
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.number", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), FormaterUtil.formatNumberNoGrouping(bill.getNumber(), bill.getLocale()));
				} else {
					logger.error("Message resource not found with code = " + "bill.number");
					//isError = true;
					reportData.put("1", FormaterUtil.formatNumberNoGrouping(bill.getNumber(), bill.getLocale()));
				}
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.title", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), bill.getDefaultTitle());
				} else {
					logger.error("Message resource not found with code = " + "bill.title");
					//isError = true;
					reportData.put("2", bill.getDefaultTitle());
				}
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.deviceType", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), bill.getType().getName());
				} else {
					logger.error("Message resource not found with code = " + "bill.deviceType");
					//isError = true;
					reportData.put("3", bill.getType().getName());
				}	
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.submissionDate", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(bill.getSubmissionDate(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
				} else {
					logger.error("Message resource not found with code = " + "bill.submissionDate");
					//isError = true;
					reportData.put("4", FormaterUtil.formatDateToString(bill.getSubmissionDate(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
				}				
				
				StringBuffer billMemberNames=new StringBuffer();
				billMemberNames.append(bill.getPrimaryMember().getFullname()+",");
				List<SupportingMember> supportingMembers = bill.getSupportingMembers();
				if(supportingMembers!=null) {					
					for(SupportingMember sm: supportingMembers) {
						billMemberNames.append(sm.getMember().getFullname()+",");
					}					
				}
				List<ClubbedEntity> clubbedEntities = bill.findClubbedEntitiesByBillSubmissionDate(ApplicationConstants.ASC, bill.getLocale());
				if(clubbedEntities!=null){
					for(ClubbedEntity ce:clubbedEntities){
						/** show only those clubbed bills which are not in state of
						 * (processed to be putup for nameclubbing, putup for nameclubbing, pending for nameclubbing approval) 
						 **/
						if(ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_CLUBBED)
								|| ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
							String tempPrimary=ce.getBill().getPrimaryMember().getFullname();
							if(!billMemberNames.toString().contains(tempPrimary)){
								billMemberNames.append(ce.getBill().getPrimaryMember().getFullname()+",");
							}
							List<SupportingMember> clubbedSupportingMember=ce.getBill().getSupportingMembers();
							if(clubbedSupportingMember!=null){
								if(!clubbedSupportingMember.isEmpty()){
									for(SupportingMember l:clubbedSupportingMember){
										String tempSupporting=l.getMember().getFullname();
										if(!billMemberNames.toString().contains(tempSupporting)){
											billMemberNames.append(tempSupporting+",");
										}
									}
								}
							}							
						}						
					}
				}
				if(!billMemberNames.toString().isEmpty()){
					billMemberNames.deleteCharAt(billMemberNames.length()-1);
				}
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.members", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), billMemberNames.toString());
				} else {
					logger.error("Message resource not found with code = " + "bill.members");
					//isError = true;
					reportData.put("5", billMemberNames.toString());
				}				
								
				firstHouse = bill.findFirstHouseType();
				secondHouse = bill.findSecondHouseType();
				WorkflowDetails workflowDetails = null;
				Map<String, String> workflowDetailsIdentifiers = new HashMap<String, String>();
				Date transmissionDate = null;
				PrintRequisition printRequisition = null;
				Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
				String houseRound = "1";
				
				if(firstHouse!=null && secondHouse!=null) {			
					List<Object[]> billDrafts = Bill.findStatusDatesForBill(bill);					
					for(Object[] billDraft: billDrafts) {
						houseRound = "1";
						if(billDraft[1]!=null && !billDraft[1].equals("") && !billDraft[4].equals(ApplicationConstants.BILL_FINAL_PASSED)) {
							houseRound = billDraft[1].toString();							
						}
						keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill." + billDraft[0] + "_" + houseRound + ".expectedStatusDate", bill.getLocale());
						if(keyLabel!=null) {
							reportData.put(keyLabel.getValue(), billDraft[2].toString());
						} else {
							logger.error("Message resource not found with code = bill." + billDraft[0] + "_" + houseRound + ".expectedStatusDate");
							isError = true;							
						}
						keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill." + billDraft[0] + "_" + houseRound + ".statusDate", bill.getLocale());
						if(keyLabel!=null) {
							reportData.put(keyLabel.getValue(), billDraft[3].toString());
						} else {
							logger.error("Message resource not found with code = bill." + billDraft[0] + "_" + houseRound + ".statusDate");
							isError = true;							
						}
						if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_GAZETTE_COPY);				
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_INTRODUCED);
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {
								if(printRequisition.getPublishDateMarathi()!=null) {
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDate", bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(printRequisition.getPublishDateMarathi(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.gazettePublishingDate");
										isError = true;										
									}										
								}
								if(printRequisition.getPublishDateEnglish()!=null) {keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDateForEnglishTranslation", bill.getLocale());
								keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDateForEnglishTranslation", bill.getLocale());	
								if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(printRequisition.getPublishDateEnglish(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.gazettePublishingDateForEnglishTranslation");
										isError = true;										
									}										
								}
								if(printRequisition.getPublishDateHindi()!=null) {
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDateForHindiTranslation", bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(printRequisition.getPublishDateHindi(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.gazettePublishingDateForHindiTranslation");
										isError = true;										
									}										
								}
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse") && billDraft[1]!=null && billDraft[1].toString().equals("1")) {
							//first house first round
							printRequisitionIdentifiers.clear();												
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);				
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse");
							printRequisitionIdentifiers.put("houseRound", new String("1"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetailsIdentifiers.put("workflowType", ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW);
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {						
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromFirstHouseFirstRoundDate."+firstHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromFirstHouseFirstRoundDate."+firstHouse);
										isError = true;										
									}							
								}					
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse") && billDraft[1]!=null && billDraft[1].toString().equals("1")) {
							//second house first round
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse");
							printRequisitionIdentifiers.put("houseRound", new String("1"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {					
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromSecondHouseFirstRoundDate."+secondHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromSecondHouseFirstRoundDate."+secondHouse);
										isError = true;										
									}
								}
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse") && billDraft[1]!=null && billDraft[1].toString().equals("2")) {
							//first house second round
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse");
							printRequisitionIdentifiers.put("houseRound", new String("2"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {					
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromFirstHouseSecondRoundDate."+firstHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromFirstHouseSecondRoundDate."+firstHouse);
										isError = true;										
									}							
								}					
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse") && billDraft[1]!=null && billDraft[1].toString().equals("2")) {
							//second house second round
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse");
							printRequisitionIdentifiers.put("houseRound", new String("2"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {					
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromSecondHouseSecondRoundDate."+secondHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromSecondHouseSecondRoundDate."+secondHouse);
										isError = true;										
									}						
								}					
							}
						}								
					}					
					//generate report
					try {
						reportFile = generateReportUsingFOP(new Object[]{firstHouse,secondHouse,reportData}, "template_billregister_report", outputFormat, "registerForBill", bill.getLocale());
					} catch (Exception e) {						
						e.printStackTrace();
						logger.error("Error In Report Generation");
						isError = true;	
					}
				} else {
					logger.error("First OR Second House Not Found for Bill with Id = " + billId);
					isError = true;									
				}				
			}
		} else {
			logger.error("Check Request Parameters 'billId' & 'outputFormat' for null or empty values");
			isError = true;
		}		
		if(isError) {
			MessageResource message = MessageResource.findByFieldName(MessageResource.class, "code", "bill.register.errorInGeneration", locale.toString());
    		if(message != null) {
    			try {
					response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + message.getValue() + "</h3></body></html>");
				} catch (IOException e) {
					e.printStackTrace();
				}    			
    		} else {
    			try {
					response.getWriter().println("<h3>Error in register generation. Please contact administrator.</h3>");
				} catch (IOException e) {						
					e.printStackTrace();
				}
    		}
		} else {
			openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
		}
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String generateRegister(ModelMap model, HttpServletRequest request) {
		String resultPath = "bill/error";
		Map<String,String> reportData = new LinkedHashMap<String, String>();
		String firstHouse = "";
		String secondHouse = "";
		
		String billId = request.getParameter("billId");
		if(billId!=null && !billId.isEmpty()) {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
			if(bill==null) {
				logger.error("Bill not found with Id = " + billId);
				model.addAttribute("errorcode", "messageresourcenotfound");
				return "bill/error";
			} else {
				MessageResource keyLabel = null;
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.number", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), FormaterUtil.formatNumberNoGrouping(bill.getNumber(), bill.getLocale()));
				} else {
					logger.error("Message resource not found with code = " + "bill.number");
					model.addAttribute("errorcode", "messageresourcenotfound");
					return "bill/error";					
				}
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.title", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), bill.getDefaultTitle());
				} else {
					logger.error("Message resource not found with code = " + "bill.title");
					model.addAttribute("errorcode", "messageresourcenotfound");
					return "bill/error";					
				}
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.deviceType", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), bill.getType().getName());
				} else {
					logger.error("Message resource not found with code = " + "bill.deviceType");
					model.addAttribute("errorcode", "messageresourcenotfound");
					return "bill/error";					
				}	
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.submissionDate", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(bill.getSubmissionDate(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
				} else {
					logger.error("Message resource not found with code = " + "bill.submissionDate");
					model.addAttribute("errorcode", "messageresourcenotfound");
					return "bill/error";					
				}				
				
				StringBuffer billMemberNames=new StringBuffer();
				billMemberNames.append(bill.getPrimaryMember().getFullname()+",");
				List<SupportingMember> supportingMembers = bill.getSupportingMembers();
				if(supportingMembers!=null) {					
					for(SupportingMember sm: supportingMembers) {
						billMemberNames.append(sm.getMember().getFullname()+",");
					}					
				}
				List<ClubbedEntity> clubbedEntities = bill.findClubbedEntitiesByBillSubmissionDate(ApplicationConstants.ASC, bill.getLocale());
				if(clubbedEntities!=null){
					for(ClubbedEntity ce:clubbedEntities){
						/** show only those clubbed bills which are not in state of
						 * (processed to be putup for nameclubbing, putup for nameclubbing, pending for nameclubbing approval) 
						 **/
						if(ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_CLUBBED)
								|| ce.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
							String tempPrimary=ce.getBill().getPrimaryMember().getFullname();
							if(!billMemberNames.toString().contains(tempPrimary)){
								billMemberNames.append(ce.getBill().getPrimaryMember().getFullname()+",");
							}
							List<SupportingMember> clubbedSupportingMember=ce.getBill().getSupportingMembers();
							if(clubbedSupportingMember!=null){
								if(!clubbedSupportingMember.isEmpty()){
									for(SupportingMember l:clubbedSupportingMember){
										String tempSupporting=l.getMember().getFullname();
										if(!billMemberNames.toString().contains(tempSupporting)){
											billMemberNames.append(tempSupporting+",");
										}
									}
								}
							}							
						}						
					}
				}
				if(!billMemberNames.toString().isEmpty()){
					billMemberNames.deleteCharAt(billMemberNames.length()-1);
				}
				keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.members", bill.getLocale());
				if(keyLabel!=null) {
					reportData.put(keyLabel.getValue(), billMemberNames.toString());
				} else {
					logger.error("Message resource not found with code = " + "bill.members");
					model.addAttribute("errorcode", "messageresourcenotfound");
					return "bill/error";					
				}				
								
				firstHouse = bill.findFirstHouseType();
				secondHouse = bill.findSecondHouseType();
				WorkflowDetails workflowDetails = null;
				Map<String, String> workflowDetailsIdentifiers = new HashMap<String, String>();
				Date transmissionDate = null;
				PrintRequisition printRequisition = null;
				Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
				String houseRound = "1";
				
				if(firstHouse!=null && secondHouse!=null) {			
					List<Object[]> billDrafts = Bill.findStatusDatesForBill(bill);					
					for(Object[] billDraft: billDrafts) {
						houseRound = "1";
						if(billDraft[1]!=null && !billDraft[1].equals("") && !billDraft[4].equals(ApplicationConstants.BILL_FINAL_PASSED)) {
							houseRound = billDraft[1].toString();							
						}
						keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill." + billDraft[0] + "_" + houseRound + ".expectedStatusDate", bill.getLocale());
						if(keyLabel!=null) {
							reportData.put(keyLabel.getValue(), billDraft[2].toString());
						} else {
							logger.error("Message resource not found with code = bill." + billDraft[0] + "_" + houseRound + ".expectedStatusDate");
							model.addAttribute("errorcode", "messageresourcenotfound");
							return "bill/error";							
						}
						keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill." + billDraft[0] + "_" + houseRound + ".statusDate", bill.getLocale());
						if(keyLabel!=null) {
							reportData.put(keyLabel.getValue(), billDraft[3].toString());
						} else {
							logger.error("Message resource not found with code = bill." + billDraft[0] + "_" + houseRound + ".statusDate");
							model.addAttribute("errorcode", "messageresourcenotfound");
							return "bill/error";							
						}
						if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_INTRODUCED)) {
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_GAZETTE_COPY);				
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_INTRODUCED);
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {
								if(printRequisition.getPublishDateMarathi()!=null) {
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDate", bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(printRequisition.getPublishDateMarathi(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.gazettePublishingDate");
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}										
								}
								if(printRequisition.getPublishDateEnglish()!=null) {keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDateForEnglishTranslation", bill.getLocale());
								keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDateForEnglishTranslation", bill.getLocale());	
								if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(printRequisition.getPublishDateEnglish(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.gazettePublishingDateForEnglishTranslation");
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}										
								}
								if(printRequisition.getPublishDateHindi()!=null) {
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.gazettePublishingDateForHindiTranslation", bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(printRequisition.getPublishDateHindi(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.gazettePublishingDateForHindiTranslation");
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}										
								}
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse") && billDraft[1]!=null && billDraft[1].toString().equals("1")) {
							//first house first round
							printRequisitionIdentifiers.clear();												
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);				
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse");
							printRequisitionIdentifiers.put("houseRound", new String("1"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetailsIdentifiers.put("workflowType", ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW);
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {						
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromFirstHouseFirstRoundDate."+firstHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromFirstHouseFirstRoundDate."+firstHouse);
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}							
								}					
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse") && billDraft[1]!=null && billDraft[1].toString().equals("1")) {
							//second house first round
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse");
							printRequisitionIdentifiers.put("houseRound", new String("1"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {					
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromSecondHouseFirstRoundDate."+secondHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromSecondHouseFirstRoundDate."+secondHouse);
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}
								}
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse") && billDraft[1]!=null && billDraft[1].toString().equals("2")) {
							//first house second round
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+firstHouse+"_firsthouse");
							printRequisitionIdentifiers.put("houseRound", new String("2"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {					
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromFirstHouseSecondRoundDate."+firstHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromFirstHouseSecondRoundDate."+firstHouse);
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}							
								}					
							}
						} else if(billDraft[0].equals(ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse") && billDraft[1]!=null && billDraft[1].toString().equals("2")) {
							//second house second round
							printRequisitionIdentifiers.put("deviceId", bill.getId().toString());
							printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
							printRequisitionIdentifiers.put("status", ApplicationConstants.BILL_PROCESSED_PASSED+"_"+secondHouse+"_secondhouse");
							printRequisitionIdentifiers.put("houseRound", new String("2"));
							printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, bill.getLocale());
							if(printRequisition!=null) {					
								workflowDetailsIdentifiers.put("printRequisitionId", printRequisition.getId().toString());
								workflowDetails = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowDetailsIdentifiers, bill.getLocale());
								if(workflowDetails!=null) {
									transmissionDate = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale());
									keyLabel = MessageResource.findByFieldName(MessageResource.class, "code", "bill.transmissionFromSecondHouseSecondRoundDate."+secondHouse, bill.getLocale());
									if(keyLabel!=null) {
										reportData.put(keyLabel.getValue(), FormaterUtil.formatDateToString(transmissionDate, ApplicationConstants.SERVER_DATEFORMAT, bill.getLocale()));
									} else {
										logger.error("Message resource not found with code = bill.transmissionFromSecondHouseSecondRoundDate."+secondHouse);
										model.addAttribute("errorcode", "messageresourcenotfound");
										return "bill/error";										
									}						
								}					
							}
						}								
					}					
					model.addAttribute("registerEntries", reportData);
					resultPath = "bill/registerForSingleBill";
				} else {
					logger.error("First OR Second House Not Found for Bill with Id = " + billId);
					model.addAttribute("errorcode", "firstorsecondhousenotfound");														
				}				
			}
		} else {
			logger.error("Check Request Parameters 'billId' for null or empty values");
			model.addAttribute("errorcode", "billnotfound");			
		}
		return resultPath;
	}	
	
	@RequestMapping(value="/addSection",method=RequestMethod.GET)
	public String newSection(final HttpServletRequest request, final ModelMap model) {
		String language = request.getParameter("language");
		String selectedText = request.getParameter("selectedText");
		if(language!=null && !language.isEmpty()) {
			model.addAttribute("language", language);		
			if(selectedText!=null && !selectedText.isEmpty()) {
				model.addAttribute("selectedText", selectedText);
			}						
			return "bill/addsection";
		} else {
			logger.error("/**** Check Request Parameters for null or empty values ****/");				
			return "bill/error";
		}				
	}
	
	@RequestMapping(value="/addSection",method=RequestMethod.POST)
	public String addSection(final HttpServletRequest request, final ModelMap model) {
		String billId = request.getParameter("billId");
		String language = request.getParameter("language");
		String sectionNumber = request.getParameter("sectionNumber");
		String sectionOrder = request.getParameter("sectionOrder");
		String sectionText = request.getParameter("sectionText");
		String strUserGroupType = request.getParameter("usergroupType");
		String returnValue = "bill/error";
		if(billId!=null && !billId.isEmpty() && language!=null && !language.isEmpty() 
				&& sectionNumber!=null && !sectionNumber.isEmpty() && sectionOrder!=null && !sectionOrder.isEmpty()
				&& sectionText!=null && !sectionText.isEmpty() && strUserGroupType!=null && !strUserGroupType.isEmpty()) {
			/**** Server encoding request parameter ****/
			CustomParameter deploymentServerParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "DEPLOYMENT_SERVER", "");
			if(deploymentServerParameter!=null) {
				if(deploymentServerParameter.getValue()!=null) {
					if(deploymentServerParameter.getValue().equals("TOMCAT")) {
						try {
							sectionNumber = new String(sectionNumber.getBytes("ISO-8859-1"),"UTF-8");	
							sectionOrder = new String(sectionOrder.getBytes("ISO-8859-1"),"UTF-8");
							sectionText = new String(sectionText.getBytes("ISO-8859-1"),"UTF-8");
							Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));							
							if(bill!=null) {
								Section section = Bill.findSection(bill.getId(), language, sectionNumber);
								if(section==null) {
									section = new Section();
									section.setLocale(bill.getLocale());
									section.setLanguage(language);
									section.setNumber(sectionNumber);
									String[] sectionNumberArr = sectionNumber.split("\\.");
									if(sectionNumberArr.length==1) {
										Section sectionWithSameOrder = Bill.findSectionByHierarchyOrder(bill.getId(), language, sectionOrder);
										if(sectionWithSameOrder!=null) {
											List<Section> sections = Bill.findAllSectionsInGivenLanguage(bill.getId(), language);
											for(Section s: sections) {		
												
												if(Integer.parseInt(s.getHierarchyOrder().split("\\.")[0])>=Integer.parseInt(sectionOrder)) {
													String hOrder = s.getHierarchyOrder();													
													int hOrderFirstElement = Integer.parseInt(hOrder.split("\\.")[0]);
													hOrderFirstElement++;
													if(hOrder.split("\\.").length>1) {
														String finalHOrder = hOrder.substring(hOrder.indexOf("."), hOrder.length());
														s.setHierarchyOrder(hOrderFirstElement+""+finalHOrder);
													} else {
														s.setHierarchyOrder(hOrderFirstElement+"");
													}
													
													System.out.println(s.getHierarchyOrder());
													s.merge();
												}
											}
											section.setHierarchyOrder(sectionOrder);
										} else {
											section.setHierarchyOrder(sectionOrder);
										}
									} else {
										String parentSectionNumber = "";
										for(int i=0; i<=sectionNumberArr.length-2;i++) {											
											parentSectionNumber += sectionNumberArr[i];
											if(i!=sectionNumberArr.length-2) {
												parentSectionNumber += ".";
											}
										}
										Section parentSection = Bill.findSection(bill.getId(), language, parentSectionNumber);
										if(parentSection!=null) {
											Section sectionWithSameOrder = Bill.findSectionByHierarchyOrder(bill.getId(), language, parentSection.getHierarchyOrder()+"."+sectionOrder);
											if(sectionWithSameOrder!=null) {
												List<Section> sections = Bill.findAllInternalSections(bill.getId(), language, parentSection.getHierarchyOrder());
												for(Section s: sections) {
													if(Integer.parseInt(s.getHierarchyOrder().split("\\.")[sectionNumberArr.length-1])>=Integer.parseInt(sectionOrder)) {
														String hOrder = s.getHierarchyOrder();
														String[] hOrderArr = hOrder.split("\\.");
														int hOrderIncrementElement = Integer.parseInt(hOrder.split("\\.")[sectionNumberArr.length-1]);
														hOrderIncrementElement++;
														String preHOrder = "";//hOrder.substring(0, sectionNumberArr.length-2);
														for(int i=0; i<=sectionNumberArr.length-2;i++) {
															preHOrder += hOrderArr[i];
															if(i!=sectionNumberArr.length-2) {
																preHOrder += ".";
															}
														}
														if(hOrder.split("\\.").length>sectionNumberArr.length) {
															String postHOrder = "";//hOrder.substring(sectionNumberArr.length, hOrder.length());
															for(int i=sectionNumberArr.length; i<hOrderArr.length;i++) {
																postHOrder += hOrderArr[i];
																if(i!=hOrderArr.length-1) {
																	postHOrder += ".";
																}
															}
															s.setHierarchyOrder(preHOrder+"."+hOrderIncrementElement+"."+postHOrder);
														} else {
															s.setHierarchyOrder(preHOrder+"."+hOrderIncrementElement);
														}
														
														System.out.println(s.getHierarchyOrder());
														s.merge();
													}
												}
												section.setHierarchyOrder(parentSection.getHierarchyOrder()+"."+sectionOrder);
											} else {
												section.setHierarchyOrder(parentSection.getHierarchyOrder()+"."+sectionOrder);
											}
										} else {
											model.addAttribute("errorcode", "parentSection_undefined");
											return returnValue;
										}
									}
									section.setText(sectionText);
									section.setEditedOn(new Date());
									section.setEditedBy(this.getCurrentUser().getActualUsername());
									if(strUserGroupType!=null){
										UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, section.getLocale());
										section.setEditedAs(userGroupType.getName());
									}
									section.persist();
									if(bill.getSections()!=null) {
										bill.getSections().add(section);
										
									} else {
										List<Section> sections = new ArrayList<Section>();
										sections.add(section);
										bill.setSections(sections);
									}
									bill.simpleMerge();
									model.addAttribute("selectedSectionNumber", section.getNumber());
									model.addAttribute("selectedSectionOrder", section.findOrder());
									model.addAttribute("selectedSectionText", section.getText());
									model.addAttribute("language", section.getLanguage());
									model.addAttribute("ack_message", "success_addition");
									returnValue = "bill/editsection";
								} else {
									section.setText(sectionText);
									section.setEditedOn(new Date());
									section.setEditedBy(this.getCurrentUser().getActualUsername());
									if(strUserGroupType!=null){
										UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, section.getLocale());
										section.setEditedAs(userGroupType.getName());
									}
									section.merge();
									
									returnValue = "bill/updatesection";
								}											
							}							
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();									
						} catch (ELSException e) {
							e.printStackTrace();															
						}
					}
				}
			}
		}
		return returnValue;
	}
	
	@RequestMapping(value="/editSection",method=RequestMethod.GET)
	public String editSection(final HttpServletRequest request, final ModelMap model) {
		String language = request.getParameter("language");
		String selectedText = request.getParameter("selectedText");
		if(language!=null && !language.isEmpty()) {
			model.addAttribute("language", language);		
			if(selectedText!=null && !selectedText.isEmpty()) {
				model.addAttribute("selectedText", selectedText);
			}						
			return "bill/editsection";
		} else {
			logger.error("/**** Check Request Parameters for null or empty values ****/");				
			return "bill/error";
		}				
	}
	
	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		Bill bill=Bill.findById(Bill.class, id);
		if(bill!=null){
			Status status=bill.getStatus();
			if(status.getType().equals(ApplicationConstants.BILL_INCOMPLETE)||status.getType().equals(ApplicationConstants.BILL_COMPLETE)){
				Bill.supportingMemberWorkflowDeletion(bill);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
