package org.mkcl.els.controller.ris;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.ChildVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ParentVO;
import org.mkcl.els.common.vo.PartDraftVO;
import org.mkcl.els.common.vo.ProceedingVO;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.xmlvo.ProceedingXMLVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Designation;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.ProceedingAutofill;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proceeding")
public class ProceedingController extends GenericController<Proceeding>{
	
	@Autowired 
	Environment env;


	/*****************Proceeding Related**************/
	@Override
	protected void populateModule(final ModelMap model,final HttpServletRequest request,
			final String locale,final AuthUser currentUser) {	
		/**** House Types ****/
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		String houseType=this.getCurrentUser().getHouseType(); 
		if(houseType.equals("lowerhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("upperhouse")){
			houseTypes=HouseType.findAllByFieldName(HouseType.class, "type",houseType,"name",ApplicationConstants.ASC, locale);
		}else if(houseType.equals("bothhouse")){
			houseTypes=HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
		}
		model.addAttribute("houseTypes", houseTypes);
		if(houseType.equals("bothhouse")){
			houseType="lowerhouse";
		}
		model.addAttribute("houseType",houseType);

		/**** Session Types ****/
		List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType", ApplicationConstants.ASC, locale);
		HouseType authUserHouseType=HouseType.findByFieldName(HouseType.class, "type",houseType, locale);
		Session lastSessionCreated = null;
		try {
			lastSessionCreated = Session.findLatestSession(authUserHouseType);
		} catch (ELSException e) {
			model.addAttribute("errorcode","nosessionentriesfound");
			e.printStackTrace();
		}
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

		/**** Language ****/
		List<Language> languages = new ArrayList<Language>();
		try {
			languages = Language.findAllLanguagesByModule("RIS",locale);
			model.addAttribute("languages",languages);
		} catch (ELSException e) {
			model.addAttribute("errorcode", "languagesnotset");
			e.printStackTrace();
		}
		

		/******Reporter*********/
		User user = null;
		try {
			user = User.findByUserName(this.getCurrentUser().getUsername(),locale);
			model.addAttribute("ugparam",user.getId());
		} catch (ELSException e) {
			model.addAttribute("errorcode", "userdoesnotexist");
			e.printStackTrace();
		}
		
		Set<Role> roles = this.getCurrentUser().getRoles();
		for(Role r : roles){
			String roleType = r.getType();
			if(roleType.equals(ApplicationConstants.RIS_CHIEF_REPORTER) 
					|| roleType.equals(ApplicationConstants.RIS_REPORTER)){
				model.addAttribute("roleType",roleType);
				break;
			}
		}
			
		/*** CommitteeType ***/
		List<CommitteeType> committeeTypes = CommitteeType.findAll(CommitteeType.class, "name", "ASC", locale);
		model.addAttribute("committeeTypes", committeeTypes);
		
		/*** Committee Meetings***/
//		List<CommitteeMeeting> committeeMeetings = Roster.findCommitteeMeetingByUserId(user.getId(), locale);
//		model.addAttribute("committeeMeetings", committeeMeetings);
	}

	@Override
	protected void populateEdit(final ModelMap model, final Proceeding domain,
			final HttpServletRequest request) {
		HouseType houseType = null;
		if(domain.getSlot()!=null){
			Slot slot=domain.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			CommitteeMeeting committeeMeeting = roster.getCommitteeMeeting();
			/****slot****/
			model.addAttribute("slotId", domain.getSlot().getId());
			model.addAttribute("slotName",domain.getSlot().getName());
			String startTime = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "dd-MM-yyyy HH:mm", domain.getLocale());
			String endTime = FormaterUtil.formatDateToString(domain.getSlot().getEndTime(), "dd-MM-yyyy HH:mm", domain.getLocale());
			request.setAttribute("slotDate", slot.getStartTime());
			model.addAttribute("slotStartTime", startTime);
			model.addAttribute("slotEndTime", endTime);
			String currentSlotStartDate = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "dd-MM-yyyy", domain.getLocale());
			String currentSlotStartTime = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "HH:mm", domain.getLocale());
			model.addAttribute("currentSlotStartDate", currentSlotStartDate);
			model.addAttribute("currentSlotStartTime", currentSlotStartTime);
			
			List<User> users=Slot.findDifferentLanguageUsersBySlot(slot);
			String languageReporter="";
			for(int i=0;i<users.size();i++){
				languageReporter=languageReporter+users.get(i).getFirstName();
				if(i+1<users.size()){
					languageReporter=languageReporter+"/";
				}
			}
			
			model.addAttribute("languageReporter", languageReporter);

				
				/**** Previous Slot ****/
				Slot previousSlot = Slot.findPreviousSlot(slot);
				List<Slot> slots = Slot.findSlotsByReporterAndRoster(slot.getRoster(), slot.getReporter());
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				for(Slot s : slots){
					if(s.getStartTime().after(slot.getStartTime())){
						MasterVO masterVO = new MasterVO();
						masterVO.setName(s.getName());
						masterVO.setType(FormaterUtil.formatDateToString(s.getStartTime(), "HH:mm", domain.getLocale()));
						masterVO.setValue(FormaterUtil.formatDateToString(s.getEndTime(), "HH:mm", domain.getLocale()));
						masterVOs.add(masterVO);
					}
				}
				
				model.addAttribute("nextSlots", masterVOs);
				
				if(previousSlot!=null){
					Reporter previousReporter = previousSlot.getReporter();
					User previousReporterUser = previousReporter.getUser();
					model.addAttribute("previousReporter", previousReporterUser.getTitle() + " " +previousReporterUser.getLastName());
					Proceeding previousProceeding = Proceeding.findByFieldName(Proceeding.class, "slot", previousSlot, domain.getLocale());
					if(previousProceeding != null){
						List<Part> previousParts = previousProceeding.getParts();
						if(!previousParts.isEmpty()){
							/**** Last Part of previous part ****/
							Part previousPart = previousParts.get(previousParts.size()-1);
							model.addAttribute("previousPartMainHeading", previousPart.getMainHeading());
							model.addAttribute("previousPartPageHeading", previousPart.getPageHeading());
							model.addAttribute("previousPartSpecialHeading", previousPart.getSpecialHeading());
							if(previousPart.getChairPersonRole()!=null){
								model.addAttribute("previousPartChairPersonRole",previousPart.getChairPersonRole().getId());
								//model.addAttribute("previousPartChairPerson", previousPart.getChairPerson());
							}
							if(previousPart.getDeviceType()!=null){
								model.addAttribute("previousPartDeviceType",previousPart.getDeviceType().getId());
								if(previousPart.getDeviceType().getDevice().equals(ApplicationConstants.QUESTION)){
									Question question = Question.findById(Question.class, previousPart.getDeviceId());
									model.addAttribute("previousPartDeviceId",question.getId());
									model.addAttribute("previousPartDeviceNumber",question.getNumber());
								}else if(previousPart.getDeviceType().getDevice().equals(ApplicationConstants.RESOLUTION)){
									Resolution resolution = Resolution.findById(Resolution.class, previousPart.getDeviceId());
									model.addAttribute("previousPartDeviceId",resolution.getId());
									model.addAttribute("previousPartDeviceNumber",resolution.getNumber());
								}
							}
						}
					}
				}
				
				Slot nextSlot = Slot.findNextSlot(slot);
				if(nextSlot != null){
					Reporter nextReporter = nextSlot.getReporter();
					User nextReporterUser = nextReporter.getUser();
					model.addAttribute("nextReporter", nextReporterUser.getTitle() + " " + nextReporterUser.getLastName());
				}
				if(session!=null){
					houseType = session.getHouse().getType();
					model.addAttribute("session",session.getId());
				/****Party****/
				List<Party> parties=MemberPartyAssociation.findActivePartiesHavingMemberInHouse(session.getHouse(),domain.getLocale());
				model.addAttribute("parties", parties);
				
				/****Ministries****/
				List<Ministry> ministries;
				try {
					ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
					model.addAttribute("ministries", ministries);
				} catch (ELSException e) {
					logger.error("Ministries not assigned to Group");
					e.printStackTrace();
				}
				
				
				
				
				/****Members****/
				List<Member> members=Member.findAll(Member.class, "firstName", "asc", domain.getLocale());
				model.addAttribute("members",members);
				
				/****MemberRoles****/
				List<MemberRole> roles= new ArrayList<MemberRole>();
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_MEMBERROLES_FOR_RIS_LOWERHOUSE", "");
					if(customParameter!=null){
						String allowedMemberRoles = customParameter.getValue();
						String strMemberRoles[] = allowedMemberRoles.split(",");
						for(int i=0; i< strMemberRoles.length;i++){
							MemberRole memberRole = MemberRole.findByFieldName(MemberRole.class, "type", strMemberRoles[i], domain.getLocale());
							if(memberRole!=null){
								roles.add(memberRole);
							}
						}
					}else{
						model.addAttribute("errorcode","allowedmemberrolesforrislowerhousenotset");
						logger.error("Custom Parameter ALLOWED_MEMBERROLES_FOR_RIS_LOWERHOUSE not set");
					}
				}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_MEMBERROLES_FOR_RIS_UPPERHOUSE", "");
					if(customParameter!=null){
						String allowedMemberRoles = customParameter.getValue();
						String strMemberRoles[] = allowedMemberRoles.split(",");
						for(int i=0; i< strMemberRoles.length;i++){
							MemberRole memberRole = MemberRole.findByFieldName(MemberRole.class, "type", strMemberRoles[i], domain.getLocale());
							if(memberRole!=null){
								roles.add(memberRole);
							}
						}
					}else{
						model.addAttribute("errorcode","allowedmemberrolesforrisupperhousenotset");
						logger.error("Custom Parameter ALLOWED_MEMBERROLES_FOR_RIS_UPPERHOUSE not set");
					}
				}
				
				model.addAttribute("roles", roles);
				
				/****Designation****/
				List<Designation> designations=Designation.findAll(Designation.class, "name", "asc", domain.getLocale());
				model.addAttribute("designations",designations);
				
				
				/****SubDepartments****/
				List<SubDepartment> subDepartments=SubDepartment.findAll(SubDepartment.class, "name", "asc", domain.getLocale());
				model.addAttribute("subDepartments",subDepartments);
				
				/****DeviceType****/
				CustomParameter deviceTypesGoneLive = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_GONE_LIVE", "");
				String strDevices = deviceTypesGoneLive.getValue();
				String[] devTypes = strDevices.split(",");
				List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
				for(String d : devTypes){
					DeviceType deviceType = DeviceType.findByType(d, domain.getLocale());
					deviceTypes.add(deviceType);
				}
				model.addAttribute("deviceTypes", deviceTypes);
				
			}else if(committeeMeeting!=null){
				model.addAttribute("committeeMeeting",committeeMeeting.getId());
				Committee committee = committeeMeeting.getCommittee();
				CommitteeName committeeName = committee.getCommitteeName();
				model.addAttribute("committeeName", committeeName.getDisplayName());
				
			}
			
		/****Parts****/
		List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", domain, "orderNo", "asc", domain.getLocale());
		model.addAttribute("parts", parts);
		if(parts.isEmpty()){
			model.addAttribute("partCount", 1);
		}else{
			model.addAttribute("partCount", domain.getParts().size());
		}

		/****Proceeding Id****/
		model.addAttribute("proceeding",domain.getId());
				
		/****Locale****/
		model.addAttribute("locale",domain.getLocale());
		
		/***Reporter***/
		model.addAttribute("reporter",domain.getSlot().getReporter().getId());
		model.addAttribute("userName", this.getCurrentUser().getUsername());
		
		/****Undo Counts and RedoCount for Editing Functionality****/
		model.addAttribute("undoCount", 0);
		model.addAttribute("redoCount", 0);
		
		model.addAttribute("documentId",domain.getDocumentId());
		
		String username = this.getCurrentUser().getActualUsername();
		List<ProceedingAutofill> proceedingAutofills = ProceedingAutofill.
				findAllByFieldName(ProceedingAutofill.class, "username", username, "id", "asc", domain.getLocale());
		model.addAttribute("proceedingAutofills", proceedingAutofills);
		}

	}

	@Override
	protected void preValidateUpdate(final Proceeding domain,
			final BindingResult result, final HttpServletRequest request) {
		String strPartCount = request.getParameter("partCount");
		if(strPartCount !=null && !strPartCount.isEmpty() && !strPartCount.equals("undefined") ){
			populateParts(domain, request, result);
		}else{
			result.rejectValue("version", "partCountEmpty");
		}
		

	}
	
	@Override
	protected String modifyEditUrlPattern(final String editUrlPattern, 
    		final HttpServletRequest request, 
    		final ModelMap model, 
    		final String locale) {
		Date previousSystemDate = FormaterUtil.formatStringToDate("30/07/2016", "dd/MM/yyyy");
		Date slotStartTime = (Date) request.getAttribute("slotDate");
		if(slotStartTime.before(previousSystemDate)){
			
				return editUrlPattern.replace("edit", "edit2");
			
		}
        return editUrlPattern;
    }

	private void populateParts(Proceeding domain, HttpServletRequest request,
			BindingResult result) {
		List<Part> parts = new ArrayList<Part>();
		Integer partCount = Integer.parseInt(request.getParameter("partCount"));
		for (int i = 1; i <= partCount; i++) {
			
			Part part=new Part();
			
			/****Part Id****/
			String id=request.getParameter("partId"+ i);
			if(id!=null){
				if(!id.isEmpty()){
					part = Part.findById(Part.class, Long.parseLong(id));
					if(part.getPartDrafts() != null && part.getPartDrafts().isEmpty()){
						part.setPartDrafts(part.getPartDrafts());
					}
				}
			}
			/****PrimaryMember****/
			String strMember=request.getParameter("primaryMember"+i);
			if(strMember!=null && !strMember.equals("")){
				Member member=Member.findById(Member.class, Long.parseLong(strMember));
				part.setPrimaryMember(member);
			}
			

			/****OrderNo****/
			String order=request.getParameter("partOrder"+ i);
			if(order!=null){
				if(!order.isEmpty()){
					part.setOrderNo(Integer.parseInt(order));
				}
			}

			/****Part Version****/
			String version=request.getParameter("partVersion"+ i);
			if(version!=null){
				if(!version.isEmpty()){
					part.setVersion(Long.parseLong(version));
				}
			}

			/****Part locale****/
			String locale=request.getParameter("partLocale"+ i);
			if(locale!=null){
				if(!locale.isEmpty()){
					part.setLocale(locale);
				}
			}
			
			/****Main Heading****/
			String mainHeading=request.getParameter("mainHeading"+i);
			if(mainHeading!=null&&!mainHeading.isEmpty()){
				part.setMainHeading(mainHeading);
			}
			
			/****Page Heading****/
			String pageHeading=request.getParameter("pageHeading"+i);
			if(pageHeading!=null && !pageHeading.isEmpty()){
				part.setPageHeading(pageHeading);
			}
			
			/****Special Heading****/
			String specialHeading=request.getParameter("specialHeading"+i);
			if(specialHeading!=null && !specialHeading.isEmpty()){
				part.setSpecialHeading(specialHeading);
			}
			
			/****Member role and Chairperson****/
			String strRole=request.getParameter("chairPersonRole"+i);
			String strChairPerson = request.getParameter("chairPerson"+i);
			if(strRole!=null && !strRole.isEmpty()){
				MemberRole mr=MemberRole.findById(MemberRole.class, Long.parseLong(strRole));
				Member chairPersonMember=null;
				if(mr!=null){
					if(strChairPerson != null && !strChairPerson.isEmpty()){
						part.setChairPerson(strChairPerson);
					}else{
						Slot slot=domain.getSlot();
						Roster roster=slot.getRoster();
						Session session=roster.getSession();
						House house=session.getHouse();
						List<HouseMemberRoleAssociation> hmras;
						try {
							hmras = HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, mr, new Date(), domain.getLocale());
							for(HouseMemberRoleAssociation h:hmras){
								if(h.getRole().equals(mr)){
									chairPersonMember=h.getMember();
									break;
								}
							}
							part.setChairPerson(chairPersonMember.getFullname());
						} catch (ELSException e) {
							result.rejectValue("version", "noactivememberrole");
							e.printStackTrace();
						}
					}
					
					part.setChairPersonRole(mr);
				}
			}
			
			/****Proceeding Content****/
			String content=request.getParameter("partContent"+i);
			if(content!=null && !content.isEmpty()){
				part.setProceedingContent(content);
				
			}
			
			String revisedContent = request.getParameter("partRevisedContent"+i);
			if(revisedContent != null && !revisedContent.isEmpty()){
				part.setRevisedContent(revisedContent);
			}else if(content != null && !content.isEmpty()){
				part.setRevisedContent(content);
			}
			
			/****Proceeding Id****/
			String strProceeding=request.getParameter("partProceeding"+i);
			if(strProceeding!=null && !strProceeding.isEmpty()){
				Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
				part.setProceeding(proceeding);
			}
			
			/****PrimaryMember Ministry****/
			String strPrimaryMemberMinistry=request.getParameter("primaryMemberMinistry"+i);
			if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
				part.setPrimaryMemberMinistry(ministry);
			}
			
			/****PrimaryMember Designation****/
			String strPrimaryMemberDesignation=request.getParameter("primaryMemberDesignation"+i);
			if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
				part.setPrimaryMemberDesignation(designation);
			}
			
			/****Primary Member SubDepartment****/
			String strPrimaryMemberSubDepartment=request.getParameter("primaryMemberSubDepartment"+i);
			if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty()){
				SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
				part.setPrimaryMemberSubDepartment(subDepartment);
			}
			
			/****Substitute Member****/
			String strSubstituteMember=request.getParameter("substituteMember"+i);
			if(strSubstituteMember!=null && !strSubstituteMember.isEmpty()){
				Member member=Member.findById(Member.class, Long.parseLong(strSubstituteMember));
				part.setSubstituteMember(member);
			}
			
			/****Substitute Member Ministry****/
			String strSubstituteMemberMinistry=request.getParameter("substituteMemberMinistry"+i);
			if(strSubstituteMemberMinistry!=null && !strSubstituteMemberMinistry.isEmpty()){
				Ministry ministry=Ministry.findById(Ministry.class, Long.parseLong(strSubstituteMemberMinistry));
				part.setSubstituteMemberMinistry(ministry);
			}
			
			/****Substitute Member Designation****/
			String strSubstituteMemberDesignation=request.getParameter("substituteMemberDesignation"+i);
			if(strSubstituteMemberDesignation!=null && !strSubstituteMemberDesignation.isEmpty()){
				Designation designation=Designation.findById(Designation.class, Long.parseLong(strSubstituteMemberDesignation));
				part.setSubstituteMemberDesignation(designation);
			}
			
			/****Substitute Member SubDepartment ****/
			String strSubstituteMemberSubDepartment=request.getParameter("substituteMemberSubDepartment"+i);
			if(strSubstituteMemberSubDepartment!=null && !strSubstituteMemberSubDepartment.isEmpty()){
				SubDepartment subDepartment=SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubstituteMemberSubDepartment));
				part.setSubstituteMemberSubDepartment(subDepartment);
			}
			
			/****Public Representative****/
			String strPublicRepresentative=request.getParameter("publicRepresentative"+i);
			if(strPublicRepresentative!=null && !strPublicRepresentative.isEmpty()){
				part.setPublicRepresentative(strPublicRepresentative);
			}

			/****Public Representative Detail****/
			String strPublicRepresentativeDetail=request.getParameter("publicRepresentativeDetail"+i);
			if(strPublicRepresentativeDetail!=null && !strPublicRepresentativeDetail.isEmpty()){
				part.setPublicRepresentativeDetail(strPublicRepresentativeDetail);
			}
			
			/****Device Type****/
			String strDeviceType=request.getParameter("deviceType"+i);
			if(strDeviceType!=null && !strDeviceType.isEmpty()){
				DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				part.setDeviceType(deviceType);
			}
			
			/****Device Id****/
			String strDeviceId=request.getParameter("deviceId"+i);
			if(strDeviceId!=null && !strDeviceId.isEmpty()){
				part.setDeviceId(Long.parseLong(strDeviceId));
			}
			
			/****The Part Entry Date****/
			part.setEntryDate(new Date());
			
			/**** Is Constituency Required ****/
			String strIsConstituencyRequired=request.getParameter("isConstituencyRequired"+i);
			if(strIsConstituencyRequired!=null && !strIsConstituencyRequired.isEmpty()){
				part.setIsConstituencyRequired(true);
			}else{
				part.setIsConstituencyRequired(false);
			}
			
			/**** Is Interrupted ****/
			String strIsInterrupted=request.getParameter("isInterrupted"+i);
			if(strIsInterrupted!=null && !strIsInterrupted.isEmpty()){
				part.setIsInterrupted(true);
			}else{
				part.setIsInterrupted(false);
			}
			
			/**** Is Interrupted ****/
			String strIsSubstitutionRequired=request.getParameter("isSubstitutionRequired"+i);
			if(strIsSubstitutionRequired!=null && !strIsSubstitutionRequired.isEmpty()){
				part.setIsSubstitutionRequired(true);
			}else{
				part.setIsSubstitutionRequired(false);
			}
					
			/****Reporter****/
			part.setReporter(domain.getSlot().getReporter());
			parts.add(part);
		}
		domain.setParts(parts);
	}
	
	@Override
	protected void customValidateUpdate(final Proceeding domain,
			final BindingResult result, final HttpServletRequest request) {
		
	}

	// Not Used Currently... If Bookmarking is implemented uncomment following code
	/****View the Details of the Part in which the Bookmark is added****/
//	@RequestMapping(value="/part/viewbookmark",method=RequestMethod.GET)
//	public String viewBookmarkDetail(final HttpServletRequest request, final Locale locale,
//			final ModelMap model){
//		String strBookmarkId=request.getParameter("id");
//		
//		if(strBookmarkId!=null && !strBookmarkId.isEmpty()){
//			/****Bookmark****/
//			Bookmark bookmark=Bookmark.findById(Bookmark.class, Long.parseLong(strBookmarkId));
//			if(bookmark!=null){
//				Part part=bookmark.getMasterPart();
//				Proceeding proceeding=part.getProceeding();
//				Language language=proceeding.getSlot().findLanguage();
//				List result=Part.findAllPartsOfProceeding(proceeding,language,bookmark.getBookmarkKey(),locale.toString());
//				model.addAttribute("report", result);
//				model.addAttribute("bookmarkKey",bookmark.getBookmarkKey());
//				/****Reporter****/
//				Reporter reporter=part.getReporter();
//				if(reporter!=null){
//					User user=reporter.getUser();
//					if(user!=null){
//						model.addAttribute("reporter",user.findFullName());
//					}
//				}
//			}
//		}
//		return "proceeding/bookmarkreadonly";
//	}

	
	@Transactional
	@RequestMapping(value = "/{proceedingId}/{partId}/delete", method = RequestMethod.DELETE)
	public @ResponseBody String deletePart(final @PathVariable("proceedingId") Long proceedingId,
			final @PathVariable("partId") Long partId,
			final ModelMap model, final HttpServletRequest request) {
		Proceeding proceeding =Proceeding.findById(Proceeding.class,proceedingId);
		Boolean status=false;
		status=Proceeding.removePart(proceeding,partId);
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}
	}

	
	@RequestMapping(value="/part/save",method=RequestMethod.POST)
	public @ResponseBody ChildVO savePart(final HttpServletRequest request, final Locale locale,final ModelMap model){
		String strPartCount = request.getParameter("partCount");
		ChildVO childVO=new ChildVO();
		if(strPartCount!=null && !strPartCount.isEmpty()){
			String strContent = request.getParameter("partContent"+strPartCount);
			String strPrimaryMember = request.getParameter("primaryMember"+strPartCount);
			String strPrimaryMemberMinistry = request.getParameter("primaryMemberMinistry"+strPartCount);
			String strPrimaryMemberDesignation = request.getParameter("primaryMemberDesignation"+strPartCount);
			String strPrimaryMemberSubDepartment = request.getParameter("primaryMemberSubDepartment"+strPartCount);
			String strProceeding = request.getParameter("partProceeding"+strPartCount);
			String strPublicRepresentative = request.getParameter("publicRepresentative"+strPartCount);
			String strPublicRepresentativeDetail = request.getParameter("publicRepresentativeDetail"+strPartCount);
			String strChairPersonRole = request.getParameter("chairPersonRole"+strPartCount);
			String strChairPerson = request.getParameter("chairPerson"+strPartCount);
			String strDeviceType = request.getParameter("deviceType"+strPartCount);
			String strIsInterrupted = request.getParameter("isInterrupted"+strPartCount);
			String strMainHeading = request.getParameter("mainHeading"+strPartCount);
			String strPageHeading = request.getParameter("pageHeading"+strPartCount);
			String strSpecialHeading = request.getParameter("specialHeading"+strPartCount);
			String strIsConstituencyRequired = request.getParameter("isConstituencyRequired"+strPartCount);
			String strIsSubstitutionRequired = request.getParameter("isSubstitutionRequired"+strPartCount);
			String strOrderNo = request.getParameter("partOrder"+strPartCount);
			String strDeviceId = request.getParameter("deviceId"+strPartCount);
			String strReporter = request.getParameter("partReporter"+strPartCount);
			String strPartId = request.getParameter("partId"+strPartCount);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			String server=null;
			Part part=null;
			Session session=null;
			
			
			/**** Part ****/
			if(strPartId!=null && !strPartId.isEmpty()){
				part=Part.findById(Part.class, Long.parseLong(strPartId));
			}else{
				part=new Part();
			}
			
			Proceeding proceeding=null;
			/***`Proceeding Content***/
			if(strContent!=null && !strContent.isEmpty()){
				part.setProceedingContent(strContent);
				part.setRevisedContent(strContent);
			}
			
			/**** Proceeding ****/
			if(strProceeding!=null && !strProceeding.isEmpty()){
				 proceeding = Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
				part.setProceeding(proceeding);
			}
			
			/**** ChairPerson and Member Role ****/
			if(strChairPersonRole!=null && !strChairPersonRole.isEmpty()){
				MemberRole memberRole = MemberRole.findById(MemberRole.class, Long.parseLong(strChairPersonRole));
				part.setChairPersonRole(memberRole);
				if(strChairPerson!=null && !strChairPerson.isEmpty()){
					part.setChairPerson(strChairPerson);
				}else{
					Member member=null;
					if(memberRole!=null){
						Slot slot=proceeding.getSlot();
						Roster roster=slot.getRoster();
						session=roster.getSession();
						House house=session.getHouse();
						List<HouseMemberRoleAssociation> hmras;
						try {
							hmras = HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, memberRole, new Date(), proceeding.getLocale());
							for(HouseMemberRoleAssociation h:hmras){
								if(h.getRole().equals(memberRole)){
									member=h.getMember();
									break;
								}
							}
							if(member!=null){
								part.setChairPerson(member.getFullname());
							}
						} catch (ELSException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			/**** Primary member ****/
			if(strPrimaryMember!=null && !strPrimaryMember.isEmpty()){
				Member member = Member.findById(Member.class, Long.parseLong(strPrimaryMember));
				part.setPrimaryMember(member);
			}
			
			/**** Primary Member Designation****/
			if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
				Designation designation = Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
				part.setPrimaryMemberDesignation(designation);
				if(designation.getType().equals(ApplicationConstants.STATE_MINISTER)){
					if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty()){
						if(strIsSubstitutionRequired !=null && !strIsSubstitutionRequired.isEmpty()){
							SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
							Ministry ministry = Ministry.find(subDepartment, new Date(), locale.toString());
							Member member = MemberMinister.find(ministry,locale);
							if(member!=null){
								/**** Substitute Member , Ministry, Designation, SubDepartment****/
								part.setSubstituteMember(member);
								part.setSubstituteMemberMinistry(ministry);
								part.setSubstituteMemberSubDepartment(subDepartment);
								List<MemberMinister> memberMinisters=member.getMemberMinisters();
								for(MemberMinister mm:memberMinisters){
									if((mm.getMinistryFromDate()==null || mm.getMinistryFromDate().before(new Date()))
										&& (mm.getMinistryToDate()==null || mm.getMinistryToDate().after(new Date()))){
										part.setSubstituteMemberDesignation(mm.getDesignation());
										break;
									}
								}
							}
						}
					}
				}else{
					if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty() ){
						SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
						Ministry ministry = Ministry.find(subDepartment, new Date(), locale.toString());
						part.setPrimaryMemberMinistry(ministry);
						part.setPrimaryMemberSubDepartment(subDepartment);
					}
				}
				
			}
			
			/**** Primary member Ministry ****/
			if(strPrimaryMemberMinistry!=null && !strPrimaryMemberMinistry.isEmpty()){
				Ministry ministry = Ministry.findById(Ministry.class, Long.parseLong(strPrimaryMemberMinistry));
				part.setPrimaryMemberMinistry(ministry);
			}
			
			/**** Primary member SubDepartment****/
			if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty() ){
				SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
				part.setPrimaryMemberSubDepartment(subDepartment);
			}
			
			/**** Public Representative ****/
			if(strPublicRepresentative!=null && !strPublicRepresentative.isEmpty()){
				part.setPublicRepresentative(strPublicRepresentative);
				/**** Public Representative Details****/
				if(strPublicRepresentativeDetail!=null && !strPublicRepresentativeDetail.isEmpty()){
					part.setPublicRepresentativeDetail(strPublicRepresentativeDetail);
				}
			}
					
			/**** DeviceType****/
			if(strDeviceType!=null && !strDeviceType.isEmpty()){
				DeviceType deviceType =  DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				part.setDeviceType(deviceType);
				/**** Device Id ****/
				if(strDeviceId!=null && !strDeviceId.isEmpty()){
					part.setDeviceId(Long.parseLong(strDeviceId));
				}
			}
						
			/**** Is Interrupted****/
			if(strIsInterrupted!=null && !strIsInterrupted.isEmpty()){
				part.setIsInterrupted(true);
			}else{
				part.setIsInterrupted(false);
			}
			
			/**** Main Heading ****/
			if(strMainHeading!=null && !strMainHeading.isEmpty()){
				part.setMainHeading(strMainHeading);
			}
			
			/**** Page Heading ****/
			if(strPageHeading!=null && !strPageHeading.isEmpty()){
				part.setPageHeading(strPageHeading);
			}
			
			/**** Special Heading ****/
			if(strSpecialHeading!=null && !strSpecialHeading.isEmpty()){
				part.setSpecialHeading(strSpecialHeading);
			}
			
			/**** Is Constituency Required****/
			if(strIsConstituencyRequired!=null && !strIsConstituencyRequired.isEmpty()){
				part.setIsConstituencyRequired(true);
			}else{
				part.setIsConstituencyRequired(false);
			}
			
			/**** Is Constituency Required****/
			if(strIsSubstitutionRequired!=null && !strIsSubstitutionRequired.isEmpty()){
				part.setIsSubstitutionRequired(true);
			}else{
				part.setIsSubstitutionRequired(false);
			}
			
			/**** Order No ****/
			if(strOrderNo!=null && !strOrderNo.isEmpty()){
				part.setOrderNo(Integer.parseInt(strOrderNo));
			}
			
			/**** Reporter ****/
			if(strReporter!=null && !strReporter.isEmpty()){
				Reporter reporter= Reporter.findById(Reporter.class, Long.parseLong(strReporter));
				part.setReporter(reporter);
			}
			/**** Locale****/
			part.setLocale(locale.toString());
			
			if(part.getId()!=null){
				if(part!=null){
					PartDraft partDraft=new PartDraft();
					partDraft.setPageHeading(part.getPageHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setOriginalText(part.getProceedingContent());
					String editedBy = request.getParameter("editingUser");
					partDraft.setEditedBy(editedBy);
					partDraft.setLocale(locale.toString());
					partDraft.setEditedOn(new Date());
					partDraft.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
					partDraft.setWorkflowCopy(false);
					part.getPartDrafts().add(partDraft);
			}
				((BaseDomain)part).merge();
			}else{
				((BaseDomain)part).persist();
			}
			
			/**** ChildVO****/
			childVO.setId(part.getId());
			childVO.setMainHeading(part.getMainHeading());
			childVO.setVersion(part.getVersion());
			childVO.setPageHeading(part.getPageHeading());
			childVO.setSpecialHeading(part.getSpecialHeading());
			
			if(part.getChairPersonRole()!=null){
				childVO.setMemberrole(part.getChairPersonRole().getId().toString());
				childVO.setChairperson(part.getChairPerson());
			}
			
			childVO.setOrderNo(part.getOrderNo());
			if(part.getPrimaryMember()!=null){
				childVO.setPrimaryMember(part.getPrimaryMember().getId().toString());
				childVO.setPrimaryMemberName(part.getPrimaryMember().getFullname());
			}else{
				childVO.setPrimaryMember("");
			}
			
			
			if(part.getPrimaryMemberDesignation()!=null){
				childVO.setPrimaryMemberDesignation(part.getPrimaryMemberDesignation().getId().toString());
				childVO.setPrimaryMemberDesignationName(part.getPrimaryMemberDesignation().getName());
			}else{
				childVO.setPrimaryMemberDesignation("");
			}
			
			if(part.getPrimaryMemberMinistry()!=null){
				childVO.setPrimaryMemberMinistry(part.getPrimaryMemberMinistry().getId().toString());
				childVO.setPrimaryMemberMinistryName(part.getPrimaryMemberMinistry().getName());
			}else{
				childVO.setPrimaryMemberMinistry("");
			}
			
			if(part.getPrimaryMemberSubDepartment()!=null){
				childVO.setPrimaryMemberSubDepartment(part.getPrimaryMemberSubDepartment().getId().toString());
				childVO.setPrimaryMemberSubDepartmentName(part.getPrimaryMemberSubDepartment().getName());
			}else{
				childVO.setPrimaryMemberSubDepartment("");
			}
			
			
			if(part.getSubstituteMember()!=null){
				childVO.setSubstituteMember(part.getSubstituteMember().getId().toString());
				childVO.setSubstituteMemberName(part.getSubstituteMember().getFullname());
			}else{
				childVO.setSubstituteMember("");
			}
			
			
			if(part.getSubstituteMemberDesignation()!=null){
				childVO.setSubstituteMemberDesignation(part.getSubstituteMemberDesignation().getId().toString());
				childVO.setSubstituteMemberDesignationName(part.getSubstituteMemberDesignation().getName());
			}else{
				childVO.setSubstituteMemberDesignation("");
			}
			
			if(part.getSubstituteMemberMinistry()!=null){
				childVO.setSubstituteMemberMinistry(part.getSubstituteMemberMinistry().getId().toString());
				childVO.setSubstituteMemberMinistry(part.getSubstituteMemberMinistry().getName());
			}else{
				childVO.setSubstituteMemberMinistry("");
			}
			
			if(part.getSubstituteMemberSubDepartment()!=null){
				childVO.setSubstituteMemberSubDepartment(part.getSubstituteMemberSubDepartment().getId().toString());
				childVO.setSubstituteMemberSubDepartmentName(part.getSubstituteMemberSubDepartment().getName());
			}else{
				childVO.setSubstituteMemberSubDepartment("");
			}
			
			
			if(part.getIsConstituencyRequired()!=null){
				if(part.getIsConstituencyRequired()){
					if(part.getPrimaryMember()!=null){
						Constituency constituency = part.getPrimaryMember().findConstituency();
						childVO.setConstituency(constituency.getName());
					}
					
				}
				childVO.setConstituencyRequired(part.getIsConstituencyRequired());
			}
			
			if(part.getIsInterrupted()!=null){
				childVO.setInterrupted(part.getIsInterrupted());
			}
			
			if(part.getIsSubstitutionRequired()!=null){
				childVO.setSubstitutionRequired(part.getIsSubstitutionRequired());
			}
			
			if(part.getDeviceType()!=null){
				childVO.setDeviceType(part.getDeviceType().getId().toString());
			}else{
				childVO.setDeviceType("");
				childVO.setDeviceId("");
			}
						
			if(part.getDeviceId()!=null){
				childVO.setDeviceId(part.getDeviceId().toString());
			}
			
			if(part.getPublicRepresentative()!=null && !part.getPublicRepresentative().isEmpty()){
				childVO.setPublicRepresentative(part.getPublicRepresentative());
				if(part.getPublicRepresentativeDetail()!=null && !part.getPublicRepresentativeDetail().isEmpty()){
					childVO.setPublicRepresentativeDetails(part.getPublicRepresentativeDetail());
				}
			}else{
				childVO.setPublicRepresentative("");
				childVO.setPublicRepresentativeDetails("");
			}
			
			childVO.setProceedingContent(part.getProceedingContent());
			childVO.setProceeding(part.getProceeding().getId());
			childVO.setReporter(part.getReporter().getId());
		}
		return childVO;
		//return null;
		
	}
	
	/****Bookmark Related****/
	@RequestMapping(value="/part/bookmark",method=RequestMethod.GET)
	public String getBookmark(final HttpServletRequest request, final Locale locale,final ModelMap model){
		List<Language> mainlanguages=new ArrayList<Language>();
		String strLanguage=request.getParameter("language");
		String strSlot=request.getParameter("currentSlot");
		String strPart=request.getParameter("currentPart");
		String strCount=request.getParameter("count");
		String strProceeding = request.getParameter("currentProceeding");
		if(strProceeding != null && !strProceeding.isEmpty()){
			Proceeding proceeding = Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			Slot slot = proceeding.getSlot();
			Part part = Part.findByFieldName(Part.class, "proceeding", proceeding, locale.toString());
			if(part != null){
				strSlot = slot.getId().toString();
				strPart = part.getId().toString();
			}
			if(strSlot == null || strSlot.equals("")){
				strSlot = slot.getId().toString();
			}
		}

		/****Language****/
		Language language=null;
		if(strLanguage!=null && !strLanguage.equals("")){
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
		}
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_LANGUAGES_FOR_BOOKMARKING", "");
		if(customParameter!=null){
			String strValue = customParameter.getValue();
			String strLanguages[] = strValue.split(",");
			for(int i=0;i<strLanguages.length;i++){
				Language otherLanguage = Language.findByFieldName(Language.class, "type", strLanguages[i], locale.toString());
				if(otherLanguage!=null){
					if(otherLanguage.getId()!=language.getId()){
						mainlanguages.add(otherLanguage);
					}
				}
			}
		}else{
			logger.error("Custom Parameter ALLOWED_LANGUAGES_FOR_BOOKMARKING is not set");
		}
		model.addAttribute("count", strCount);
		model.addAttribute("languages", mainlanguages);
		model.addAttribute("currentSlot", strSlot);
		model.addAttribute("currentPart",strPart);
		model.addAttribute("proceedingId",strProceeding);
		
		return "proceeding/bookmark";
	}

	
	@RequestMapping(value="/part/bookmark",method=RequestMethod.POST)
	public  @ResponseBody MasterVO addBookmark(final HttpServletRequest request, final Locale locale,final ModelMap model){
			
		String strLanguage = request.getParameter("language");
		String strPart = request.getParameter("masterPart");
		String strSlavePart = request.getParameter("slavePart");
		String strPreviousContent = request.getParameter("previousContent");
		String strReplacedContent = request.getParameter("replacedContent");
		String isPart = request.getParameter("isBookmarkPart");
		String strSlot = request.getParameter("currentSlot");
		String strPartArray = request.getParameter("partIdArray");
		String strOrderCount =  request.getParameter("orderCount");
		String strMasterSlot = request.getParameter("masterSlot");
		MasterVO masterVO= new MasterVO();
		if(isPart != null && !isPart.isEmpty()){
			if(!Boolean.parseBoolean(isPart)){
				if( strPart != null && !strPart.isEmpty()
						&& strPreviousContent != null && !strPreviousContent.isEmpty()
						&& strReplacedContent != null && !strReplacedContent.isEmpty()){
						Part masterPart = Part.findById(Part.class, Long.parseLong(strPart));
						masterPart.setRevisedContent(strReplacedContent);
			
						PartDraft partDraft = new PartDraft();
						partDraft.setEditedBy(this.getCurrentUser().getUsername());
						partDraft.setEditedOn(new Date());
						partDraft.setLocale(locale.toString());
						partDraft.setMainHeading(masterPart.getMainHeading());
						partDraft.setPageHeading(masterPart.getPageHeading());
						partDraft.setOriginalMainHeading(masterPart.getMainHeading());
						partDraft.setOriginalPageHeading(masterPart.getPageHeading());
						partDraft.setOriginalText(strPreviousContent);
						partDraft.setReplacedText(strReplacedContent);
						partDraft.setRevisedContent(strReplacedContent);
						partDraft.persist();
						
						masterPart.getPartDrafts().add(partDraft);
						masterPart.merge();
						
					}
			}else{
				if(strLanguage != null && !strLanguage.isEmpty()
						&& strSlavePart != null && !strSlavePart.isEmpty()
						&& strSlot != null && !strSlot.isEmpty()
						&& strOrderCount!=null && !strOrderCount.isEmpty()
						&& strMasterSlot!=null && !strMasterSlot.isEmpty()){
						//Language language = Language.findByFieldName(Language.class, "type", strLanguage, locale.toString());
						Part slavePart = Part.findById(Part.class, Long.parseLong(strSlavePart));
						//Slot currentSlot = Slot.findById(Slot.class, Long.parseLong(strSlot));
						Slot masterSlot= Slot.findById(Slot.class, Long.parseLong(strMasterSlot));		
						Part part=new Part();
						part.setChairPerson(slavePart.getChairPerson());
						part.setChairPersonRole(slavePart.getChairPersonRole());
						part.setDeviceId(slavePart.getDeviceId());
						part.setDeviceType(slavePart.getDeviceType());
						part.setEntryDate(new Date());
						part.setIsConstituencyRequired(slavePart.getIsConstituencyRequired());
						part.setIsInterrupted(slavePart.getIsInterrupted());
						part.setLocale(locale.toString());
						part.setMainHeading(slavePart.getMainHeading());
						part.setPageHeading(slavePart.getPageHeading());
						part.setOrderNo(Integer.parseInt(strOrderCount));
						part.setPrimaryMember(slavePart.getPrimaryMember());
						part.setPrimaryMemberDesignation(slavePart.getPrimaryMemberDesignation());
						part.setPrimaryMemberMinistry(slavePart.getPrimaryMemberMinistry());
						part.setPrimaryMemberSubDepartment(slavePart.getPrimaryMemberSubDepartment());
						part.setProceedingContent(slavePart.getProceedingContent());
						part.setPublicRepresentative(slavePart.getPublicRepresentative());
						part.setPublicRepresentativeDetail(slavePart.getPublicRepresentativeDetail());
						part.setRevisedContent(slavePart.getRevisedContent());
						part.setSubstituteMember(slavePart.getSubstituteMember());
						part.setSubstituteMemberDesignation(slavePart.getSubstituteMemberDesignation());
						part.setSubstituteMemberMinistry(slavePart.getSubstituteMemberMinistry());
						part.setSubstituteMemberSubDepartment(slavePart.getSubstituteMemberSubDepartment());
						Proceeding proceeding =  Proceeding.findByFieldName(Proceeding.class, "slot", masterSlot, locale.toString());
						part.setProceeding(proceeding);
						part.setReporter(masterSlot.getReporter());
						part.persist();
						masterVO.setId(part.getId());
						
//						Bookmark bookmark=new Bookmark();
//						bookmark.setLanguage(language);
//						bookmark.setLocale(locale.toString());
//						bookmark.setMasterPart(part);
//						bookmark.setSlavePart(slavePart);
//						bookmark.setSlot(currentSlot);
//						bookmark.setPreviousText("");
//						bookmark.setTextToBeReplaced(part.getRevisedContent());
//						bookmark.persist();
						
						if(strPartArray!=null && !strPartArray.isEmpty()){
							String partArray[] = strPartArray.split(",");
							int orderCount = Integer.parseInt(strOrderCount)+1;
							for(int i=0;i<partArray.length;i++){
								Part otherPart = Part.findById(Part.class, Long.parseLong(partArray[i]));
								otherPart.setOrderNo(orderCount);
								orderCount=orderCount+1;
								otherPart.merge();
							}
						}
						
						
				}
			}
		}
		return masterVO;
		
	}
	
	
	
	@RequestMapping(value="/part/updateMemberDetail",method=RequestMethod.POST)
	public @ResponseBody ChildVO savePartMemberDetail(final HttpServletRequest request, final Locale locale,final ModelMap model){
		ChildVO childVO=new ChildVO();
			
			String strPrimaryMember = request.getParameter("editPrimaryMember");
			String strPrimaryMemberDesignation = request.getParameter("editPrimaryMemberDesignation");
			String strPrimaryMemberSubDepartment = request.getParameter("editPrimaryMemberSubDepartment");
			String strPublicRepresentative = request.getParameter("editPublicRepresentative");
			String strPublicRepresentativeDetail = request.getParameter("editPublicRepresentativeDetail");
			String strChairPersonRole = request.getParameter("editChairPersonRole");
			String strChairPerson = request.getParameter("editChairPerson");
			String strIsConstituencyRequired = request.getParameter("editIsConstituencyRequired");
			String strIsSubstitutionRequired = request.getParameter("editIsSubstitutionRequired");
			String strPartId = request.getParameter("editPartId");
			if(strPartId!=null && !strPartId.isEmpty()){
				Part part = Part.findById(Part.class, Long.parseLong(strPartId));
				/**** ChairPerson and Member Role ****/
				if(strChairPersonRole!=null && !strChairPersonRole.isEmpty()){
					MemberRole memberRole = MemberRole.findById(MemberRole.class, Long.parseLong(strChairPersonRole));
					if(!part.getChairPersonRole().equals(memberRole)){
						part.setChairPersonRole(memberRole);
						if(strChairPerson!=null && !strChairPerson.isEmpty()){
							part.setChairPerson(strChairPerson);
						}
					}
				}
				
				/**** Primary member ****/
				if(strPrimaryMember!=null && !strPrimaryMember.isEmpty()){
					Member member = Member.findById(Member.class, Long.parseLong(strPrimaryMember));
					if(part.getPrimaryMember()!=null ){
						if(!part.getPrimaryMember().equals(member)){
							part.setPrimaryMember(member);
						}
					}else{
						part.setPrimaryMember(member);
					}
				}
				
				/**** Primary Member Designation****/
				if(strPrimaryMemberDesignation!=null && !strPrimaryMemberDesignation.isEmpty()){
					Designation designation = Designation.findById(Designation.class, Long.parseLong(strPrimaryMemberDesignation));
					part.setPrimaryMemberDesignation(designation);
					if(designation.getType().equals(ApplicationConstants.STATE_MINISTER)){
						if(strPrimaryMemberSubDepartment!=null && !strPrimaryMemberSubDepartment.isEmpty() ){
							SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strPrimaryMemberSubDepartment));
							Ministry ministry = Ministry.find(subDepartment, new Date(), locale.toString());
							Member member = MemberMinister.find(ministry,locale);
							if(member!=null){
								/**** Substitute Member , Ministry, Designation, SubDepartment****/
								part.setSubstituteMember(member);
								part.setSubstituteMemberMinistry(ministry);
								part.setSubstituteMemberSubDepartment(subDepartment);
								List<MemberMinister> memberMinisters=member.getMemberMinisters();
								for(MemberMinister mm:memberMinisters){
									if((mm.getMinistryFromDate()==null || mm.getMinistryFromDate().before(new Date()))
										&& (mm.getMinistryToDate()==null || mm.getMinistryToDate().after(new Date()))){
										part.setSubstituteMemberDesignation(mm.getDesignation());
										break;
									}
								}
							}
						}
					}
				}
				
				/**** Public Representative ****/
				if(strPublicRepresentative!=null && !strPublicRepresentative.isEmpty()){
					part.setPublicRepresentative(strPublicRepresentative);
					/**** Public Representative Details****/
					if(strPublicRepresentativeDetail!=null && !strPublicRepresentativeDetail.isEmpty()){
						part.setPublicRepresentativeDetail(strPublicRepresentativeDetail);
					}
				}
						
				
				/**** Is Constituency Required****/
				if(strIsConstituencyRequired!=null && !strIsConstituencyRequired.isEmpty()){
					part.setIsConstituencyRequired(true);
				}else{
					part.setIsConstituencyRequired(false);
				}
				
				/**** Is Constituency Required****/
				if(strIsSubstitutionRequired!=null && !strIsSubstitutionRequired.isEmpty()){
					part.setIsSubstitutionRequired(true);
				}else{
					part.setIsSubstitutionRequired(false);
				}
				
				part.merge();
				
				/**** ChildVO****/
				
				childVO.setVersion(part.getVersion());
					
				if(part.getPrimaryMember()!=null){
					childVO.setPrimaryMember(part.getPrimaryMember().getId().toString());
					childVO.setPrimaryMemberName(part.getPrimaryMember().getFullname());
				}
				
				if(part.getChairPersonRole()!=null){
					childVO.setMemberrole(part.getChairPersonRole().getId().toString());
				}
				
							
				if(part.getPrimaryMemberDesignation()!=null){
					childVO.setPrimaryMemberDesignation(part.getPrimaryMemberDesignation().getId().toString());
					childVO.setPrimaryMemberDesignationName(part.getPrimaryMemberDesignation().getName());
				}
				
				
				if(part.getPrimaryMemberSubDepartment()!=null){
					childVO.setPrimaryMemberSubDepartment(part.getPrimaryMemberSubDepartment().getId().toString());
					childVO.setPrimaryMemberSubDepartmentName(part.getPrimaryMemberSubDepartment().getName());
				}
				
				
				if(part.getSubstituteMember()!=null){
					childVO.setSubstituteMember(part.getSubstituteMember().getId().toString());
					childVO.setSubstituteMemberName(part.getSubstituteMember().getFullname());
				}
				
				
				if(part.getSubstituteMemberDesignation()!=null){
					childVO.setSubstituteMemberDesignation(part.getSubstituteMemberDesignation().getId().toString());
					childVO.setSubstituteMemberDesignationName(part.getSubstituteMemberDesignation().getName());
				}
				
				if(part.getSubstituteMemberMinistry()!=null){
					childVO.setSubstituteMemberMinistry(part.getSubstituteMemberMinistry().getId().toString());
					childVO.setSubstituteMemberMinistryName(part.getSubstituteMemberMinistry().getName());
				}
				
				
				if(part.getSubstituteMemberSubDepartment()!=null){
					childVO.setSubstituteMemberSubDepartment(part.getSubstituteMemberSubDepartment().getId().toString());
					childVO.setSubstituteMemberSubDepartmentName(part.getSubstituteMemberSubDepartment().getName());
				}
				
				
				if(part.getIsConstituencyRequired()!=null){
					if(part.getIsConstituencyRequired()){
						if(part.getPrimaryMember()!=null){
							Constituency constituency = part.getPrimaryMember().findConstituency();
							childVO.setConstituency(constituency.getName());
						}
						
					}
					childVO.setConstituencyRequired(part.getIsConstituencyRequired());
				}
				
						
				if(part.getPublicRepresentative()!=null && !part.getPublicRepresentative().isEmpty()){
					childVO.setPublicRepresentative(part.getPublicRepresentative());
					if(part.getPublicRepresentativeDetail()!=null && !part.getPublicRepresentativeDetail().isEmpty()){
						childVO.setPublicRepresentativeDetails(part.getPublicRepresentativeDetail());
					}
				}
			}
				
			return childVO;
		//return null;
		
	}
	
	
	
	/****Citations****/
	@RequestMapping(value="part/citations",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strCounter=request.getParameter("counter");
		List<Citation> citations=Citation.findAllByFieldName(Citation.class, "type", ApplicationConstants.REPORTING, "text", "asc", locale.toString());//ProceedingCitation.findAll(ProceedingCitation.class, "id", "asc", locale.toString());
		if(strCounter!=null && !strCounter.isEmpty()){
			model.addAttribute("counter", Integer.parseInt(strCounter));
		}
		model.addAttribute("citations",citations);
		return "proceeding/proceedingcitation";
		
	}

	@RequestMapping(value="/part/getMemberByParty",method=RequestMethod.GET)
	public @ResponseBody List<Member> getMemberByParty(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strPartyId=request.getParameter("partyId");
		String type=this.getCurrentUser().getHouseType();
		HouseType houseType=null;
		if(type!=null && !type.isEmpty()){
			 houseType=HouseType.findByType(type, locale.toString());
		}
		House house=House.find(houseType, new Date(), locale.toString());
		if(strPartyId!=null && !strPartyId.isEmpty()){
			Party party=Party.findById(Party.class, Long.parseLong(strPartyId));
			List<Member> members=Member.findActiveMembersByParty(party,house,locale.toString());
			model.addAttribute("members", members);		
		
		return members;
		}
		return null;
	}
	
	@RequestMapping(value="/part/getMemberByPartyPage",method=RequestMethod.GET)
	public String getMemberByPartyPage(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strPartyId=request.getParameter("partyId");
		String strCount=request.getParameter("partCount");
		String type=request.getParameter("housetype");
		HouseType houseType=null;
		if(type!=null && !type.isEmpty()){
			 houseType=HouseType.findByType(type, locale.toString());
		}
		House house=House.find(houseType, new Date(), locale.toString());
		if(strPartyId!=null && !strPartyId.isEmpty()){
			Party party=Party.findById(Party.class, Long.parseLong(strPartyId));
			List<Member> members=Member.findActiveMembersByParty(party,house,locale.toString());
			model.addAttribute("members", members);		
		    model.addAttribute("partCount", strCount);
		return "proceeding/memberphoto";
		}
		return "";
	}

	/****Reports Related****/
	
	@RequestMapping(value="/part/proceedingwiseReport",method=RequestMethod.GET)
	public String getProceedingWiseReport(final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String reporterString="प्रथम";
		String inplaceOf="यांच्या करिता";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		Language language=null;
		String strProceeding=request.getParameter("proceeding");
		String strLanguage=request.getParameter("language");
		if(strProceeding!=null && !strProceeding.isEmpty()){
			Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			Slot slot=proceeding.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			if(strLanguage!=null&& !strLanguage.isEmpty()){
				language=Language.findById(Language.class, Long.parseLong(strLanguage));
			}
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("proceedingId", new String[]{proceeding.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_SLOT_WISE_REPORT, parametersMap);		
			
			model.addAttribute("report", result);
			for(int a=0;a<result.size();a++){
				Object[] row = (Object[]) result.get(a);
				/****If the Member who is speaking is Speaker/Chairman/Depy Speaker/Chief Minister The Membername is replaced by 
				 their memberrole*****/
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().equals(ApplicationConstants.SPEAKER.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_SPEAKER.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.CHAIRMAN.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.CHIEF_MINISTER.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHAIRMAN.toUpperCase())
									||memberRole.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER.toUpperCase())){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);	
			model.addAttribute("language",language.getId());
			model.addAttribute("proceeding",proceeding.getId());
			model.addAttribute("outputFormats", outputFormats);
			model.addAttribute("chairpersonText", "अध्याक्षथानी");
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("reporterString",reporterString);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("userName", this.getCurrentUser().getUsername());
			model.addAttribute("undoCount", 0);
			model.addAttribute("redoCount", 0);
			
			
			return "proceeding/part/proceedingwisereport";
		}
		return null;
	}


	@RequestMapping(value="/proceedingwisereport",method=RequestMethod.GET)
	public @ResponseBody void viewProceedingWiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strProceeding=request.getParameter("proceeding");
		String reportFormat=request.getParameter("outputFormat");
		String strLanguage=request.getParameter("language");
		File reportFile = null;
		if(strProceeding!=null && !strProceeding.isEmpty()
				&& strLanguage!=null && !strLanguage.isEmpty()){
			Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			Slot slot=proceeding.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			ParentVO parentVO=new ParentVO();
			parentVO.setId(slot.getId());
			parentVO.setName(slot.getName());
			if(session != null){
				House house = session.getHouse();
				parentVO.setHouseType(house.getType().getType());
			}
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
			String startDate=dateFormat.format(slot.getStartTime());
			String startTime=dateFormat1.format(slot.getStartTime());
			parentVO.setStartDate(startDate);
			parentVO.setStartTime(startTime);
			List<ChildVO> childVOs=new ArrayList<ChildVO>();
			List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proceeding, "orderNo", "asc", locale.toString());
			for(Part p:parts){
				ChildVO childVO=new ChildVO();
				childVO.setId(p.getId());
				if(p.getRevisedContent() != null){
					
					String revisedContent = p.getRevisedContent().replaceAll("<!-- pagebreak -->", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
					revisedContent = revisedContent.replaceAll("<br><p></p>", "");
					revisedContent = revisedContent.replaceAll("<p></p>", "");
					revisedContent = revisedContent.replaceAll("<div></div>", "");
					revisedContent = revisedContent.replaceAll("<span></span>", "");
					//revisedContent = revisedContent.replaceAll("<table.*?headerTable.*?>.*?</table>", "");
					Pattern blockRegex = Pattern.compile( "<table class.*?headerTable.*?>.*?</table>",
                            Pattern.CASE_INSENSITIVE |
                            Pattern.DOTALL);  
					Matcher m = blockRegex.matcher(revisedContent);
					StringBuffer sb = new StringBuffer();
					boolean result = m.find();
					while(result) {
					m.appendReplacement(sb, "");
					result = m.find();
					}
					m.appendTail(sb);
					revisedContent = sb.toString();
					revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
					childVO.setProceedingContent(revisedContent);
				}
				
				childVO.setChairperson(p.getChairPerson());
				if(p.getPageHeading()!=null){
					childVO.setPageHeading(p.getPageHeading());
				}else{
					childVO.setPageHeading("");
				}
				if(p.getMainHeading()!=null){
					childVO.setMainHeading(p.getMainHeading());
				}else{
					childVO.setMainHeading("");
				}
				if(p.getSpecialHeading()!=null){
					childVO.setSpecialHeading(p.getSpecialHeading());
				}else{
					childVO.setSpecialHeading("");
				}
				if(p.getChairPersonRole()!=null){
					childVO.setMemberrole(p.getChairPersonRole().getName());
				}
				
				childVO.setOrderNo(p.getOrderNo());
				Member primaryMember=p.getPrimaryMember();
				if(primaryMember!=null){
					List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
					
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getFromDate().before(session.getEndDate()) && h.getToDate().after(session.getEndDate())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								childVO.setPrimaryMember(memberRole.getName());
							}else{
								childVO.setPrimaryMember(primaryMember.findFirstLastName());
							}
						}
					}
					
					
									
					if(p.getIsConstituencyRequired()){
//						House house=roster.getSession().getHouse();
//						MasterVO masterVo=null;
//						if(house.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
//							 masterVo=Member.findConstituencyByAssemblyId(primaryMember.getId(), house.getId());
//							
//						}else if(house.getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
//							Date currentDate=new Date();
//							String date=FormaterUtil.getDateFormatter("en_US").format(currentDate);
//							masterVo=Member.findConstituencyByCouncilDates(primaryMember.getId(), house.getId(), "DATE", date, date);
//						}
						if(p.getIsConstituencyRequired()){
							Constituency constituency=p.getPrimaryMember().findConstituency();
							if(constituency!=null){
								String cn = constituency.getName();
								if(cn.contains("(")){
									String[] constituencies = cn.split("\\(");
									childVO.setConstituency(constituencies[0]);
								}else if(cn.contains("-")){
									String[] constituencies = cn.split("-");
									childVO.setConstituency(constituencies[1]);
								}else{
									childVO.setConstituency(cn);
								}
								
							}
						}
						
					}
					if(p.getPrimaryMemberDesignation()!=null){
					childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
					}
					if(p.getPrimaryMemberMinistry()!=null){
						childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
					}
					if(p.getPrimaryMemberSubDepartment()!=null){
						childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
					}
				}
				Member substituteMember=p.getSubstituteMember();
				if(substituteMember!=null){
					childVO.setSubstituteMember(substituteMember.getFullname());
					if(p.getSubstituteMemberDesignation()!=null){
						childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
					}
					if(p.getSubstituteMemberMinistry()!=null){
						childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
					}
					if(p.getSubstituteMemberSubDepartment()!=null){
						childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
					}
				}
				if(p.getPublicRepresentative()!=null){
					childVO.setPublicRepresentative(p.getPublicRepresentative());
					if(p.getPublicRepresentativeDetail()!=null){
						childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
					}
				}
				childVOs.add(childVO); 
			}
			parentVO.setChildVOs(childVOs);
			Slot previousSlot = Roster.findPreviousSlot(slot);
			if(previousSlot != null){
				parentVO.setReporter(previousSlot.getReporter().getUser().getTitle() + " " + previousSlot.getReporter().getUser().getLastName());
			}
			Slot nextSlot = Roster.findNextSlot(slot);
			if(nextSlot != null){
				parentVO.setNextReporter(nextSlot.getReporter().getUser().getTitle() + " " + nextSlot.getReporter().getUser().getLastName());
			}
			//parentVO.setReporter(slot.getReporter().getUser().getFirstName());
			List<User> users=Slot.findDifferentLanguageUsersBySlot(slot);
			String languageReporter="";
			for(int i=0;i<users.size();i++){
				languageReporter=languageReporter+users.get(i).getFirstName();
				if(i+1<users.size()){
					languageReporter=languageReporter+"/";
				}
			}
			parentVO.setLanguageReporter(languageReporter);
			parentVOs.add(parentVO);
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat,slot.getName(), locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_slotwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}

	/**********Report Related******************/
	@RequestMapping(value="/rosterwisereport",method=RequestMethod.GET)
	public String mergeProceedingContentReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		Roster roster = null;
		Language language = null;
		Session session = null;
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){
			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
		}else if(strLanguage != null && !strLanguage.equals("")
				&& strDay != null && !strDay.equals("") 
				&& strCommitteeMeeting!=null && !strCommitteeMeeting.equals("")){
			String[] strLanguages = strLanguage.split(",");
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
			for(String lang : strLanguages){
				language = Language.findById(Language.class, Long.parseLong(lang));
				try{
					roster = Roster.findRosterByCommitteeMeetingLanguageAndDay(committeeMeeting, language, Integer.parseInt(strDay), locale.toString());
					if(roster != null){
						break;
					}
				}catch(Exception ex){
					
				}
			}
			model.addAttribute("committeeMeeting", committeeMeeting.getId());
		}
		if(roster!=null){
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			List result=Query.findReport(ApplicationConstants.PROCEEDING_CONTENT_MERGE_REPORT, parametersMap);		
			List<Object> tempList2=new ArrayList<Object>();
			List<Object> objects=new ArrayList<Object>();
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			
			for(int i=0;i<result.size();i++){
				Object[] row1=(Object[]) result.get(i);
				Object[] row=new Object[row1.length+2];
				for(int j=0;j<row1.length;j++){
					row[j]=row1[j];
				}
				tempList2.add(row);
			}
			/****Adding the next reporter name to the array****/
			for(int i=0;i<tempList2.size();i++){
				Object[] row=(Object[]) tempList2.get(i);
				for(int j=i+1;j<tempList2.size() && j<i+2;j++){
					Object[] row1=(Object[]) tempList2.get(j);
					if(!row1[6].toString().equals(row[6].toString())){
						row[25]=row1[18];
						i=j+1;
						break;
					}
				}
			}

				/****Adding the previous reporter name ****/
			for(int i=0;i<tempList2.size();i++){
				Object[] row=(Object[]) tempList2.get(i);
				for(int k=i-1;k>=0;k--){
					Object[] row2=(Object[]) tempList2.get(k);
					if(!row2[6].toString().equals(row[6].toString())){
					//	if(row2[10]!=null){
							row[26]=row2[18];
							break;
					//	}
					}
				}
				//objects.add(row);
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("report", tempList2);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailyproceedingreport";
		}

		return retVal;
	}

	@RequestMapping(value="/viewreport",method=RequestMethod.GET)
	public @ResponseBody void viewProceedingReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String reportFormat=request.getParameter("outputFormat");
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		File reportFile = null;
		List<ParentVO> parentVOs=new ArrayList<ParentVO>();
		Roster roster = null;
		Language language = null;
		Session session = null;
		String committeeShortName = null;
		HouseType houseType = null;
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){

			houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(strLanguage!=null&&!strLanguage.equals("")
				&& strDay!=null&&!strDay.equals("")
				&& strCommitteeMeeting != null && !strCommitteeMeeting.equals("")){
//			language = Language.findById(Language.class, Long.parseLong(strLanguage));
//			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
//			roster = Roster.findRosterByCommitteeMeetingLanguageAndDay(committeeMeeting, language, Integer.parseInt(strDay), locale.toString());
			String[] strLanguages = strLanguage.split(",");
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
			for(String lang : strLanguages){
				language = Language.findById(Language.class, Long.parseLong(lang));
				try{
					roster = Roster.findRosterByCommitteeMeetingLanguageAndDay(committeeMeeting, language, Integer.parseInt(strDay), locale.toString());
					List<Part> parts = Part.findPartsByRoster(roster);
					if(!parts.isEmpty()){
						break;
					}
				}catch(Exception ex){
					
				}
			}
			Committee committee = committeeMeeting.getCommittee();
			CommitteeName committeeName = committee.getCommitteeName();
			committeeShortName = committeeName.getShortName();
			
		}
			
		if(roster != null){
			List<Slot> slots =Slot.findActiveSlots(roster);
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				if(houseType != null){
					parentVO.setHouseType(houseType.getType());
				}
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
//						String content = p.getRevisedContent();
//						content = content.replaceAll("align=\"justify\" style=\"line-height: 200%; font-size: 16px;", "");
						String revisedContent = p.getRevisedContent().replaceAll("<p><!-- pagebreak --></p>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
						revisedContent = revisedContent.replaceAll("<br><p></p>", "");
						revisedContent = revisedContent.replaceAll("<p></p>", "");
						revisedContent = revisedContent.replaceAll("<div></div>", "");
						revisedContent = revisedContent.replaceAll("<span></span>", "");
						Pattern blockRegex = Pattern.compile( "<table .*?headerTable.*?/table>",
                                Pattern.CASE_INSENSITIVE |
                                Pattern.DOTALL);  
						Matcher m = blockRegex.matcher(revisedContent);
						StringBuffer sb = new StringBuffer();
						boolean result = m.find();
						while(result) {
						m.appendReplacement(sb, "");
						result = m.find();
						}
						m.appendTail(sb);
						revisedContent = sb.toString();
						revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
						childVO.setProceedingContent(revisedContent);
						if(p.getPageHeading()!=null){
							childVO.setPageHeading(p.getPageHeading());
						}else{
							childVO.setPageHeading("");
						}
						if(p.getMainHeading()!=null){
							childVO.setMainHeading(p.getMainHeading());
						}else{
							childVO.setMainHeading("");
						}
						if(p.getSpecialHeading()!=null){
							childVO.setSpecialHeading(p.getSpecialHeading());
						}else{
							childVO.setSpecialHeading("");
						}
											
						if(p.getChairPersonRole()!=null){
							childVO.setMemberrole(p.getChairPersonRole().getName());
							if(p.getChairPersonRole().getType().equalsIgnoreCase(ApplicationConstants.PANEL_CHAIRMAN)
								|| p.getChairPersonRole().getType().equalsIgnoreCase(ApplicationConstants.PANEL_SPEAKER)
								|| p.getChairPersonRole().getType().equalsIgnoreCase(ApplicationConstants.SPEAKER)
								|| p.getChairPersonRole().getType().equalsIgnoreCase(ApplicationConstants.CHAIRMAN)){
								childVO.setChairperson(p.getChairPerson());
							}else{
								childVO.setChairperson("");
							}
						}else{
							childVO.setMemberrole("");
						}
						childVO.setOrderNo(p.getOrderNo());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getFromDate().before(session.getEndDate()) && h.getToDate().after(session.getEndDate())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.findFirstLastName());
									}
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								Constituency constituency=p.getPrimaryMember().findConstituency();
								if(constituency!=null){
									String cn = constituency.getName();
									if(cn.contains("(")){
										String[] constituencies = cn.split("\\(");
										childVO.setConstituency(constituencies[0]);
									}else if(cn.contains("-")){
										String[] constituencies = cn.split("-");
										childVO.setConstituency(constituencies[1]);
									}else{
										childVO.setConstituency(cn);
									}
								}
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}else{
							childVO.setPrimaryMember("");
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							if(p.getIsSubstitutionRequired()){
								childVO.setSubstituteMember(substituteMember.getFullname());
								if(p.getSubstituteMemberDesignation()!=null){
									childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
								}
								if(p.getSubstituteMemberMinistry()!=null){
									childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
								}
								if(p.getSubstituteMemberSubDepartment()!=null){
									childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
								}
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					if(committeeShortName != null && !committeeShortName.isEmpty()){
						parentVO.setStartDate(committeeShortName +"/"+startDate);
					}else{
						parentVO.setStartDate(startDate);
					}
					
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getTitle() + " " + s.getReporter().getUser().getLastName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat, "karyavrutt_rosterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_rosterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}

	@RequestMapping(value="/sessionwisereport",method=RequestMethod.GET)
	public @ResponseBody void viewProceedingSessionwiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;

		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			
			List<Slot> slots=Slot.findSlotsBySessionAndLanguage(session,language);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						String revisedContent = p.getRevisedContent();
						Pattern blockRegex = Pattern.compile( "<table .*?headerTable.*?/table>",
                                Pattern.CASE_INSENSITIVE |
                                Pattern.DOTALL);  
						Matcher m = blockRegex.matcher(revisedContent);
						StringBuffer sb = new StringBuffer();
						boolean result = m.find();
						while(result) {
						m.appendReplacement(sb, "");
						result = m.find();
						}
						m.appendTail(sb);
						revisedContent = sb.toString();
						revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
						childVO.setProceedingContent(revisedContent);
						if(p.getPageHeading()!=null){
							childVO.setPageHeading(p.getPageHeading());
						}else{
							childVO.setPageHeading("");
						}
						if(p.getMainHeading()!=null){
							childVO.setMainHeading(p.getMainHeading());
						}else{
							childVO.setMainHeading("");
						}
						if(p.getSpecialHeading()!=null){
							childVO.setSpecialHeading(p.getSpecialHeading());
						}else{
							childVO.setSpecialHeading("");
						}
						if(p.getChairPersonRole()!=null){
							childVO.setMemberrole(p.getChairPersonRole().getName());
							if(p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_CHAIRMAN)
									|| p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_SPEAKER)){
									childVO.setChairperson(p.getChairPerson());
							}else{
									childVO.setChairperson("");
							}
						}
						childVO.setOrderNo(p.getOrderNo());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								Constituency constituency=p.getPrimaryMember().findConstituency();
								if(constituency!=null){
									childVO.setConstituency(constituency.getName());
								}
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							if(p.getIsSubstitutionRequired()){
								childVO.setSubstituteMember(substituteMember.getFullname());
								if(p.getSubstituteMemberDesignation()!=null){
									childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
								}
								if(p.getSubstituteMemberMinistry()!=null){
									childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
								}
								if(p.getSubstituteMemberSubDepartment()!=null){
									childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
								}
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat, "karyavrutt_sessionwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_sessionwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}


	@RequestMapping(value="/reporterwisereport",method=RequestMethod.GET)
	public String reporterwiseProceedingContent(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strUser=request.getParameter("user");
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		Roster roster = null;
		User user = null; 
		Language language = null;
		Session session = null;
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")&&
				strUser!=null && !strUser.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
			user=User.findById(User.class, Long.parseLong(strUser));
			
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
			roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
		}else if(strLanguage!=null && !strLanguage.equals("")
				&& strDay!=null && !strDay.equals("")
				&& strUser!=null && !strUser.equals("")
				&& strCommitteeMeeting != null && !strCommitteeMeeting.equals("")){
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
			user=User.findById(User.class, Long.parseLong(strUser));
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
			roster=Roster.findRosterByCommitteeMeetingLanguageAndDay(committeeMeeting, language, Integer.parseInt(strDay), locale.toString());
		}
		if(roster != null && user != null){
			
			Reporter reporter= Roster.findByUser(roster, user);
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("reporterId",new String[]{reporter.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_REPORTER_WISE_REPORT, parametersMap);	
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[14]!=null){
					Member member=Member.findById(Member.class, Long.parseLong(row[14].toString()));
					List<HouseMemberRoleAssociation> hrma=member.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[15]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailyreporterwiseproceeding";
		}

		return retVal;
	}

	@RequestMapping(value="/reporterwiseproceeding",method=RequestMethod.GET)
	public @ResponseBody void getReporterwiseProceedingReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strLanguage = request.getParameter("language");
		String strDay = request.getParameter("day");
		String reportFormat = request.getParameter("outputFormat");
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		File reportFile = null;
		Roster roster = null;
		Language language = null;
		Session session = null;
		if(strHouseType != null && !strHouseType.equals("")
				&& strSessionType != null && !strSessionType.equals("")
				&& strSessionYear != null && !strSessionYear.equals("")
				&& strLanguage != null && !strLanguage.equals("")
				&& strDay != null && !strDay.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			language=Language.findById(Language.class, Long.parseLong(strLanguage));
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
			roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
		}else if(strLanguage!=null && !strLanguage.equals("")
				&& strDay!=null && !strDay.equals("")
				&& strCommitteeMeeting != null && !strCommitteeMeeting.equals("")){
			language = Language.findById(Language.class, Long.parseLong(strLanguage));
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
			roster = Roster.findRosterByCommitteeMeetingLanguageAndDay(committeeMeeting, language, Integer.parseInt(strDay), locale.toString());
		}
		if(roster != null){
			User user = User.findById(User.class, this.getCurrentUser().getUserId());
			Reporter reporter = Roster.findByUser(roster, user);
			List<Slot> slots = Slot.findSlotsByReporterAndRoster(roster,reporter);
			List<ParentVO> parentVOs = new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						String revisedContent = p.getRevisedContent();
						Pattern blockRegex = Pattern.compile( "<table .*?headerTable.*?/table>",
                                Pattern.CASE_INSENSITIVE |
                                Pattern.DOTALL);  
						Matcher m = blockRegex.matcher(revisedContent);
						StringBuffer sb = new StringBuffer();
						boolean result = m.find();
						while(result) {
						m.appendReplacement(sb, "");
						result = m.find();
						}
						m.appendTail(sb);
						revisedContent = sb.toString();
						revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
						childVO.setProceedingContent(revisedContent);
						if(p.getPageHeading()!=null){
							childVO.setPageHeading(p.getPageHeading());
						}else{
							childVO.setPageHeading("");
						}
						if(p.getMainHeading()!=null){
							childVO.setMainHeading(p.getMainHeading());
						}else{
							childVO.setMainHeading("");
						}
						if(p.getSpecialHeading()!=null){
							childVO.setSpecialHeading(p.getSpecialHeading());
						}else{
							childVO.setSpecialHeading("");
						}
						if(p.getChairPersonRole()!=null){
							childVO.setMemberrole(p.getChairPersonRole().getName());
							if(p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_CHAIRMAN)
									|| p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_SPEAKER)){
									childVO.setChairperson(p.getChairPerson());
							}else{
									childVO.setChairperson("");
							}
						}
						childVO.setOrderNo(p.getOrderNo());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								Constituency constituency=p.getPrimaryMember().findConstituency();
								if(constituency!=null){
									childVO.setConstituency(constituency.getName());
								}
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							if(p.getIsSubstitutionRequired()){
								childVO.setSubstituteMember(substituteMember.getFullname());
								if(p.getSubstituteMemberDesignation()!=null){
									childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
								}
								if(p.getSubstituteMemberMinistry()!=null){
									childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
								}
								if(p.getSubstituteMemberSubDepartment()!=null){
									childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
								}
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat, "karyavrutt_reporterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_reporterwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}


	


	@RequestMapping(value="/memberwisereport",method=RequestMethod.GET)
	public String getMemberWiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("memberId", new String[]{member.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_MEMBER_WISE_REPORT, parametersMap);	
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					Member member1=Member.findById(Member.class, Long.parseLong(row[9].toString()));
					List<HouseMemberRoleAssociation> hrma=member1.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[10]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("member",member.getId());
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailymemberwiseproceeding";
		}

		return retVal;
	}
	
	@RequestMapping(value="/memberwisereport1",method=RequestMethod.GET)
	public String getMemberWiseReport1(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("memberId", new String[]{member.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_MEMBER_WISE_REPORT, parametersMap);		
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					Member member1=Member.findById(Member.class, Long.parseLong(row[9].toString()));
					List<HouseMemberRoleAssociation> hrma=member1.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[10]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("member",member.getId());
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailymemberwiseproceeding1";
		}

		return retVal;
	}


	@RequestMapping(value="/memberwiseproceeding",method=RequestMethod.GET)
	public @ResponseBody void viewMemberWiseReport(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			List<Slot> slots=Slot.findSlotsByMemberAndRoster(roster,member);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						String revisedContent = p.getRevisedContent();
						Pattern blockRegex = Pattern.compile( "<table .*?headerTable.*?/table>",
                                Pattern.CASE_INSENSITIVE |
                                Pattern.DOTALL);  
						Matcher m = blockRegex.matcher(revisedContent);
						StringBuffer sb = new StringBuffer();
						boolean result = m.find();
						while(result) {
						m.appendReplacement(sb, "");
						result = m.find();
						}
						m.appendTail(sb);
						revisedContent = sb.toString();
						revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
						childVO.setProceedingContent(revisedContent);
						if(p.getPageHeading()!=null){
							childVO.setPageHeading(p.getPageHeading());
						}else{
							childVO.setPageHeading("");
						}
						if(p.getMainHeading()!=null){
							childVO.setMainHeading(p.getMainHeading());
						}else{
							childVO.setMainHeading("");
						}
						if(p.getSpecialHeading()!=null){
							childVO.setSpecialHeading(p.getSpecialHeading());
						}else{
							childVO.setSpecialHeading("");
						}
						if(p.getChairPersonRole()!=null){
							childVO.setMemberrole(p.getChairPersonRole().getName());
							if(p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_CHAIRMAN)
									|| p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_SPEAKER)){
									childVO.setChairperson(p.getChairPerson());
							}else{
									childVO.setChairperson("");
							}
						}
						childVO.setOrderNo(p.getOrderNo());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								Constituency constituency=p.getPrimaryMember().findConstituency();
								if(constituency!=null){
									childVO.setConstituency(constituency.getName());
								}
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
				
				
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat, "karyavrutt_memberwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_memberwise", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
	
	
	
	@RequestMapping(value="/memberwiseproceeding1",method=RequestMethod.GET)
	public @ResponseBody void viewMemberWiseReports(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){
			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			List<Slot> slots=Slot.findSlotsByMemberAndRoster(roster,member);
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findAllByFieldName(Part.class, "proceeding", proc, "orderNo", "asc", locale.toString());
					for(int i=0;i<parts.size();i++){
						Part part=parts.get(i);
						Member primaryMember=part.getPrimaryMember();
						Member substituteMember=part.getSubstituteMember();
						String mainHeading="";
						String pageHeading="";
						if((primaryMember!=null && primaryMember.getId().equals(member.getId()))||(substituteMember!=null && substituteMember.getId().equals(member.getId()))){
							ParentVO parentVO=new ParentVO();
							List<ChildVO> childVOs=new ArrayList<ChildVO>();
							for(int j=i;j<parts.size();j++){
								
								Part part1=parts.get(j);
								if(j==i || (part1.getMainHeading()!=null && part1.getMainHeading().equals(mainHeading))||(part1.getPageHeading()!=null && part1.getPageHeading().equals(pageHeading))){
									ChildVO childVO=new ChildVO();
									childVO.setId(part1.getId());
									String revisedContent = part1.getRevisedContent();
									Pattern blockRegex = Pattern.compile( "<table .*?headerTable.*?/table>",
			                                Pattern.CASE_INSENSITIVE |
			                                Pattern.DOTALL);  
									Matcher m = blockRegex.matcher(revisedContent);
									StringBuffer sb = new StringBuffer();
									boolean result = m.find();
									while(result) {
									m.appendReplacement(sb, "");
									result = m.find();
									}
									m.appendTail(sb);
									revisedContent = sb.toString();
									revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
									childVO.setProceedingContent(revisedContent);
									if(part1.getChairPersonRole()!=null){
										childVO.setMemberrole(part1.getChairPersonRole().getName());
										if(part1.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_CHAIRMAN)
												|| part1.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_SPEAKER)){
												childVO.setChairperson(part1.getChairPerson());
										}else{
												childVO.setChairperson("");
										}
									}
									childVO.setOrderNo(part1.getOrderNo());
									if(part1.getPageHeading()!=null){
										childVO.setPageHeading(part1.getPageHeading());
									}else{
										childVO.setPageHeading("");
									}
									if(part1.getMainHeading()!=null){
										childVO.setMainHeading(part1.getMainHeading());
									}else{
										childVO.setMainHeading("");
									}
									if(part1.getSpecialHeading()!=null){
										childVO.setSpecialHeading(part1.getSpecialHeading());
									}else{
										childVO.setSpecialHeading("");
									}
									Member pMember=part1.getPrimaryMember();
									Member sMember=part1.getSubstituteMember();
									if(pMember!=null){
										List<HouseMemberRoleAssociation> hrma=pMember.getHouseMemberRoleAssociations();
										for(HouseMemberRoleAssociation h:hrma){
											if(h.getHouse().equals(session.getHouse())){
												MemberRole memberRole=h.getRole();
												if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
														||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
													childVO.setPrimaryMember(memberRole.getName());
												}else{
													childVO.setPrimaryMember(pMember.getFullname());
												}
					
											}
										}
									
										
										if(part1.getIsConstituencyRequired()){
											Constituency constituency=part1.getPrimaryMember().findConstituency();
											if(constituency!=null){
												childVO.setConstituency(constituency.getName());
											}
										}
										if(part1.getPrimaryMemberDesignation()!=null){
										childVO.setPrimaryMemberDesignation(part1.getPrimaryMemberDesignation().getName());
										}
										if(part1.getPrimaryMemberMinistry()!=null){
											childVO.setPrimaryMemberMinistry(part1.getPrimaryMemberMinistry().getName());
										}
										if(part1.getPrimaryMemberSubDepartment()!=null){
											childVO.setPrimaryMemberSubDepartment(part1.getPrimaryMemberSubDepartment().getName());
										}
									}
									if(sMember!=null){
										childVO.setSubstituteMember(sMember.getFullname());
										if(part1.getSubstituteMemberDesignation()!=null){
											childVO.setSubstituteMemberDesignation(part1.getSubstituteMemberDesignation().getName());
										}
										if(part1.getSubstituteMemberMinistry()!=null){
											childVO.setSubstituteMemberMinistry(part1.getSubstituteMemberMinistry().getName());
										}
										if(part1.getSubstituteMemberSubDepartment()!=null){
											childVO.setSubstituteMemberSubDepartment(part1.getSubstituteMemberSubDepartment().getName());
										}
									}
									if(part1.getPublicRepresentative()!=null){
										childVO.setPublicRepresentative(part1.getPublicRepresentative());
										if(part1.getPublicRepresentativeDetail()!=null){
											childVO.setPublicRepresentativeDetails(part1.getPublicRepresentativeDetail());
										}
									}
									childVOs.add(childVO);
									i=j+1;;
								}
							}
							parentVO.setChildVOs(childVOs);
							parentVO.setId(s.getId());
							parentVO.setName(s.getName());
							SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
							SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
							String startDate=dateFormat.format(s.getStartTime());
							String startTime=dateFormat1.format(s.getStartTime());
							parentVO.setStartDate(startDate);
							parentVO.setStartTime(startTime);
							parentVO.setReporter(s.getReporter().getUser().getFirstName());
							List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
							String languageReporter="";
							for(int a=0;a<users.size();a++){
								languageReporter=languageReporter+users.get(a).getFirstName();
								if(a+1<users.size()){
									languageReporter=languageReporter+"/";
								}
							}
							parentVO.setLanguageReporter(languageReporter);
							parentVOs.add(parentVO);
							
						}
					}
				}
			}
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat, "karyavrutt_memberwise1", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_memberwise1", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
	
	@RequestMapping(value="/memberwisereport2",method=RequestMethod.GET)
	public String getMemberWiseReport2(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){

		String retVal= "proceeding/error";
		String generalNotice = "(असुधारित प्रत/ प्रसिद्धीसाठी नाही)";
		String pageHeading="पृ शी";
		String mainHeading="मु शी";
		String inplaceOf="यांच्या करिता";
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("memberId", new String[]{member.getId().toString()});
			List result=Query.findReport(ApplicationConstants.RIS_MEMBER_WISE_REPORT2, parametersMap);		
			for(int i=0;i<result.size();i++){
				Object[] row = (Object[]) result.get(i);
				if(row[10]!=null){
					Member member1=Member.findById(Member.class, Long.parseLong(row[9].toString()));
					List<HouseMemberRoleAssociation> hrma=member1.getHouseMemberRoleAssociations();
					for(HouseMemberRoleAssociation h:hrma){
						if(h.getHouse().equals(session.getHouse())){
							MemberRole memberRole=h.getRole();
							if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
									||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
								row[10]="<b>"+memberRole.getName()+"</b>";
							}

						}
					}
				}
			}
			model.addAttribute("generalNotice", generalNotice);
			model.addAttribute("mainHeading", mainHeading);
			model.addAttribute("pageHeading",pageHeading);
			model.addAttribute("inplaceOf", inplaceOf);
			model.addAttribute("member",member.getId());
			model.addAttribute("report", result);
			List<MasterVO> outputFormats = new ArrayList<MasterVO>();
			MasterVO pdfFormat = new MasterVO();
			pdfFormat.setName("PDF");
			pdfFormat.setValue("PDF");
			outputFormats.add(pdfFormat);
			MasterVO wordFormat = new MasterVO();
			wordFormat.setName("WORD");
			wordFormat.setValue("WORD");
			outputFormats.add(wordFormat);									
			model.addAttribute("outputFormats", outputFormats);

			retVal = "proceeding/dailymemberwiseproceeding2";
		}

		return retVal;
	}
	
	@RequestMapping(value="/memberwiseproceeding2",method=RequestMethod.GET)
	public @ResponseBody void viewMemberWiseReport2(final HttpServletRequest request,final HttpServletResponse response,final ModelMap model,final Locale locale){
		String reportFormat=request.getParameter("outputFormat");
		File reportFile = null;
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strMember=request.getParameter("member");
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("") &&
				strMember!=null && !strMember.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
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
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
			Member member=Member.findById(Member.class, Long.parseLong(strMember));
			List<Slot> slots=Slot.findSlotsByMemberAndRoster(roster,member);
			
			List<ParentVO> parentVOs=new ArrayList<ParentVO>();
			for(Slot s:slots){
				ParentVO parentVO=new ParentVO();
				List<ChildVO> childVOs=new ArrayList<ChildVO>();
				List<Proceeding> proceedings=Proceeding.findAllFilledProceedingBySlot(s);
				for(Proceeding proc:proceedings){
					List<Part> parts=Part.findPartsByProceedingAndMember(proc,member);
					for(Part p:parts){
						ChildVO childVO=new ChildVO();
						childVO.setId(p.getId());
						String revisedContent = p.getRevisedContent();
						Pattern blockRegex = Pattern.compile( "<table .*?headerTable.*?/table>",
                                Pattern.CASE_INSENSITIVE |
                                Pattern.DOTALL);  
						Matcher m = blockRegex.matcher(revisedContent);
						StringBuffer sb = new StringBuffer();
						boolean result = m.find();
						while(result) {
						m.appendReplacement(sb, "");
						result = m.find();
						}
						m.appendTail(sb);
						revisedContent = sb.toString();
						revisedContent = revisedContent.replaceAll("<div .*?pageBreakDiv.*?>.*?</div>", "<p style='page-break-after: always;'><!-- pagebreak --></p>");
						childVO.setProceedingContent(revisedContent);
						if(p.getPageHeading()!=null){
							childVO.setPageHeading(p.getPageHeading());
						}else{
							childVO.setPageHeading("");
						}
						if(p.getMainHeading()!=null){
							childVO.setMainHeading(p.getMainHeading());
						}else{
							childVO.setMainHeading("");
						}
						if(p.getSpecialHeading()!=null){
							childVO.setSpecialHeading(p.getSpecialHeading());
						}else{
							childVO.setSpecialHeading("");
						}
						if(p.getChairPersonRole()!=null){
							childVO.setMemberrole(p.getChairPersonRole().getName());
							if(p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_CHAIRMAN)
									|| p.getChairPersonRole().getType().equals(ApplicationConstants.PANEL_SPEAKER)){
									childVO.setChairperson(p.getChairPerson());
							}else{
									childVO.setChairperson("");
							}
						}
						childVO.setOrderNo(p.getOrderNo());
						Member primaryMember=p.getPrimaryMember();
						if(primaryMember!=null){
							List<HouseMemberRoleAssociation> hrma=primaryMember.getHouseMemberRoleAssociations();
							for(HouseMemberRoleAssociation h:hrma){
								if(h.getHouse().equals(session.getHouse())){
									MemberRole memberRole=h.getRole();
									if(memberRole.getType().toLowerCase().equals(ApplicationConstants.SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_SPEAKER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.CHIEF_MINISTER)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHAIRMAN)
											||memberRole.getType().toLowerCase().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)){
										childVO.setPrimaryMember(memberRole.getName());
									}else{
										childVO.setPrimaryMember(primaryMember.getFullname());
									}
		
								}
							}
						
							
							if(p.getIsConstituencyRequired()){
								Constituency constituency = p.getPrimaryMember().findConstituency();
								childVO.setConstituency(constituency.getName());
							}
							if(p.getPrimaryMemberDesignation()!=null){
							childVO.setPrimaryMemberDesignation(p.getPrimaryMemberDesignation().getName());
							}
							if(p.getPrimaryMemberMinistry()!=null){
								childVO.setPrimaryMemberMinistry(p.getPrimaryMemberMinistry().getName());
							}
							if(p.getPrimaryMemberSubDepartment()!=null){
								childVO.setPrimaryMemberSubDepartment(p.getPrimaryMemberSubDepartment().getName());
							}
						}
						Member substituteMember=p.getSubstituteMember();
						if(substituteMember!=null){
							childVO.setSubstituteMember(substituteMember.getFullname());
							if(p.getSubstituteMemberDesignation()!=null){
								childVO.setSubstituteMemberDesignation(p.getSubstituteMemberDesignation().getName());
							}
							if(p.getSubstituteMemberMinistry()!=null){
								childVO.setSubstituteMemberMinistry(p.getSubstituteMemberMinistry().getName());
							}
							if(p.getSubstituteMemberSubDepartment()!=null){
								childVO.setSubstituteMemberSubDepartment(p.getSubstituteMemberSubDepartment().getName());
							}
						}
						if(p.getPublicRepresentative()!=null){
							childVO.setPublicRepresentative(p.getPublicRepresentative());
							if(p.getPublicRepresentativeDetail()!=null){
								childVO.setPublicRepresentativeDetails(p.getPublicRepresentativeDetail());
							}
						}
						childVOs.add(childVO); 
					}
					parentVO.setChildVOs(childVOs);
					parentVO.setId(s.getId());
					parentVO.setName(s.getName());
					SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat dateFormat1=new SimpleDateFormat("HH:mm");
					String startDate=dateFormat.format(s.getStartTime());
					String startTime=dateFormat1.format(s.getStartTime());
					parentVO.setStartDate(startDate);
					parentVO.setStartTime(startTime);
					parentVO.setReporter(s.getReporter().getUser().getFirstName());
					List<User> users=Slot.findDifferentLanguageUsersBySlot(s);
					String languageReporter="";
					for(int i=0;i<users.size();i++){
						languageReporter=languageReporter+users.get(i).getFirstName();
						if(i+1<users.size()){
							languageReporter=languageReporter+"/";
						}
					}
					parentVO.setLanguageReporter(languageReporter);
					parentVOs.add(parentVO);
				}
			}
			
			ProceedingXMLVO proceedingXMLVO=new ProceedingXMLVO();
			proceedingXMLVO.setParentVOs(parentVOs);
			if(!parentVOs.isEmpty()){
				if(reportFormat.equals("WORD")) {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_word2", reportFormat, "karyavrutt_memberwise2", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						reportFile = generateReportUsingFOP(proceedingXMLVO, "template_ris_proceeding_content_merge_report_pdf", reportFormat, "karyavrutt_memberwise2", locale.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}        		
				System.out.println("ProceedingContent generated successfully in " + reportFormat + " format!");
				openOrSaveReportFileFromBrowser(response, reportFile, reportFormat);
			}
		}
	}
	
	@Transactional
	@RequestMapping(value="/part/replace",method=RequestMethod.POST)
	public @ResponseBody List doReplace(HttpServletRequest request, HttpServletResponse response, ModelMap model, Locale locale){
		List matchedParts = null;
		try{			
			String strProceeding=request.getParameter("proceedingId");
			String strSearchTerm = request.getParameter("searchTerm");
			String strReplaceTerm = request.getParameter("replaceTerm");
			String strUndoCount = request.getParameter("undoCount");
			String strRedoCount = request.getParameter("redoCount");
			Integer undoCount = Integer.valueOf(strUndoCount);
			Integer redoCount = Integer.valueOf(strRedoCount);
			
			if (strProceeding != null && !strProceeding.equals("")){
				Proceeding proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
				matchedParts = Part.findAllEligibleForReplacement(proceeding, strSearchTerm, strReplaceTerm, locale.toString());
				if(matchedParts != null && !matchedParts.isEmpty()){
					for(int i = 0; i < matchedParts.size(); i++){
						
						Object[] objArr = (Object[])matchedParts.get(i);
						if(!objArr[1].toString().equals(objArr[3].toString())){
							Part partToBeReplaced = Part.findById(Part.class, Long.valueOf(objArr[0].toString()));
							/****Create draft****/
							PartDraft pd = new PartDraft();
							pd.setEditedBy(this.getCurrentUser().getActualUsername());
							pd.setEditedOn(new Date());
							pd.setLocale(locale.toString());
							if(objArr[4]!=null){
								pd.setMainHeading(objArr[4].toString());
							}
							if(objArr[5]!=null ){
								pd.setOriginalMainHeading(objArr[5].toString());
							}
							if(objArr[6]!=null){
								pd.setReplacedMainHeading(objArr[6].toString());
							}
							if(objArr[7]!=null){
								pd.setPageHeading(objArr[7].toString());
							}
							if(objArr[8]!=null){
								pd.setOriginalPageHeading(objArr[8].toString());
							}
							if(objArr[9]!=null){
								pd.setReplacedPageHeading(objArr[9].toString());
							}
							pd.setOriginalText(objArr[2].toString());
							pd.setReplacedText(objArr[3].toString());
							pd.setRevisedContent(objArr[3].toString());
							pd.setUndoCount(undoCount);
							pd.setRedoCount(redoCount);
							pd.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
							pd.setUniqueIdentifierForRedo(UUID.randomUUID().toString());
							pd.setWorkflowCopy(false);
							
							/****Attach undoCount and undoUID in the result list****/
							((Object[])matchedParts.get(i))[10] = partToBeReplaced.getId().toString()+":"+pd.getUndoCount()+":"+pd.getUniqueIdentifierForUndo();
							((Object[])matchedParts.get(i))[11] = partToBeReplaced.getId().toString()+":"+pd.getRedoCount()+":"+pd.getUniqueIdentifierForRedo();
							((Object[])matchedParts.get(i))[12] = "include";
							
							partToBeReplaced.getPartDrafts().add(pd);
							partToBeReplaced.setRevisedContent(objArr[3].toString());
							partToBeReplaced.setMainHeading(objArr[6].toString());
							partToBeReplaced.setPageHeading(objArr[9].toString());
							partToBeReplaced.merge();
						}else{
							((Object[])matchedParts.get(i))[12] = "exclude";
						}
					}
				}
			}		
						
		}catch (Exception e) {
			e.printStackTrace();
		}
		return matchedParts;
	}
	
	/****Replace Functionality in Proceeding wise Report****/
	@Transactional
	@RequestMapping(value="/part/undolastchange/{partid}",method=RequestMethod.POST)
	public @ResponseBody List<PartDraftVO> doUndo(@PathVariable(value="partid") Long id, HttpServletRequest request, HttpServletResponse response, Locale locale){
		PartDraftVO partDraftVO = null;
		try{
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			String strUniqueUndoId = request.getParameter("uniqueIdentifierForUndo");
			String strUndoCount = request.getParameter("undoCount");
			
			if(strUniqueUndoId != null && !strUniqueUndoId.isEmpty()
					&& strUndoCount != null && !strUndoCount.isEmpty()){
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("uniqueIdentifierForUndo", new String[]{strUniqueUndoId});
				parameters.put("undoCount", new String[]{strUndoCount});
				parameters.put("editedOn", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("editedBy", new String[]{this.getCurrentUser().getActualUsername()});
				List pds = Query.findReport("RIS_FIND_PART_DRAFTS", parameters);
				List<PartDraftVO> undoData = new ArrayList<PartDraftVO>();
				if(pds != null){
					for(Object o : pds){
						Object[] pd = (Object[])o;
						partDraftVO = new PartDraftVO();
						partDraftVO.setId(id);
						if(pd[1]!=null){
							partDraftVO.setContent(pd[1].toString());
						}
						if(pd[3]!=null){
							partDraftVO.setPageHeading(pd[3].toString());
						}
						if(pd[5]!=null){
							partDraftVO.setMainHeading(pd[5].toString());
						}
						undoData.add(partDraftVO);
						Part pp = Part.findById(Part.class, id);
						pp.setRevisedContent(partDraftVO.getContent());
						pp.setMainHeading(partDraftVO.getMainHeading());
						pp.setPageHeading(partDraftVO.getPageHeading());
						pp.merge();
					}
					
				}
				
				return undoData;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/***Redo Functionality****/
	@Transactional
	@RequestMapping(value="/part/redolastchange/{partid}",method=RequestMethod.POST)
	public @ResponseBody List<PartDraftVO> doRedo(@PathVariable(value="partid") Long id, HttpServletRequest request, HttpServletResponse response, Locale locale){
		PartDraftVO partDraftVO = null;
		try{
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			String strUrData = request.getParameter("urData");
			if(strUrData != null && !strUrData.isEmpty()){
				String[] data = strUrData.split(":"); 
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("uniqueIdentifierForUndo", new String[]{data[2]});
				parameters.put("undoCount", new String[]{data[1]});
				parameters.put("editedOn", new String[]{FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT)});
				parameters.put("editedBy", new String[]{this.getCurrentUser().getActualUsername()});
				List pds = Query.findReport("RIS_FIND_PART_DRAFTS", parameters);
				List<PartDraftVO> undoData = new ArrayList<PartDraftVO>();
				if(pds != null){
					for(Object o : pds){
						Object[] pd = (Object[])o;
						partDraftVO = new PartDraftVO();
						partDraftVO.setId(id);
						Part pp = Part.findById(Part.class, id);
						if(pd[2]!=null){
							partDraftVO.setContent(pd[2].toString());
						}
						if(pd[4]!=null){
							partDraftVO.setPageHeading(pd[4].toString());
						}else{
							partDraftVO.setPageHeading(pp.getPageHeading());
						}
						if(pd[6]!=null){
							partDraftVO.setMainHeading(pd[6].toString());
						}else{
							partDraftVO.setMainHeading(pp.getMainHeading());
						}
						undoData.add(partDraftVO);
						
						pp.setRevisedContent(partDraftVO.getContent());
						if(partDraftVO.getMainHeading()!=null){
							pp.setMainHeading(partDraftVO.getMainHeading());
						}
						if(partDraftVO.getPageHeading()!=null){
							pp.setPageHeading(partDraftVO.getPageHeading());
						}
						pp.merge();
					}
					
				}
				return undoData;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/part/updatePart",method=RequestMethod.POST)
	public @ResponseBody MasterVO updatePart(HttpServletRequest request,ModelMap model,Locale locale){
		String partId=request.getParameter("partId");
		String partField=request.getParameter("partField");
		String partText=request.getParameter("editedContent");
		String editedBy=request.getParameter("editedBy");
		String strUndoCount = request.getParameter("undoCount");
		MasterVO masterVO = null;
		if(partId!=null && !partId.equals("") 
			&& partField!=null && !partId.equals("")
			&& partText!=null && !partText.equals("")){
			Part part=Part.findById(Part.class, Long.parseLong(partId));
			if(part!=null){
				PartDraft partDraft=new PartDraft();
				if(partField.equals("mainHeading")){
					partDraft.setOriginalMainHeading(part.getMainHeading());
					part.setMainHeading(partText);
					partDraft.setMainHeading(partText);
					partDraft.setPageHeading(part.getPageHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setReplacedMainHeading(partText);
				}else if(partField.equals("pageHeading")){
					partDraft.setOriginalPageHeading(part.getPageHeading());
					part.setPageHeading(partText);
					partDraft.setPageHeading(partText);
					partDraft.setReplacedPageHeading(partText);
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
				}else if(partField.equals("specialHeading")){
					partDraft.setOriginalText(part.getRevisedContent());
					part.setSpecialHeading(partText);
					partDraft.setOriginalSpecialHeading(part.getSpecialHeading());
					partDraft.setReplacedSpecialHeading(partText);
					partDraft.setSpecialHeading(partText);
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setReplacedText(part.getRevisedContent());
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setOriginalMainHeading(part.getMainHeading());
					partDraft.setPageHeading(part.getPageHeading());
					partDraft.setOriginalPageHeading(part.getPageHeading());
				}else{
					partDraft.setOriginalText(part.getRevisedContent());
					part.setRevisedContent(partText);
					partDraft.setRevisedContent(partText);
					partDraft.setReplacedText(partText);
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setOriginalMainHeading(part.getMainHeading());
					partDraft.setPageHeading(part.getPageHeading());
					partDraft.setOriginalPageHeading(part.getPageHeading());
				}
				partDraft.setEditedBy(editedBy);
				partDraft.setLocale(locale.toString());
				partDraft.setEditedOn(new Date());
				partDraft.setUndoCount(Integer.valueOf(strUndoCount));
				partDraft.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
				partDraft.setWorkflowCopy(false);
				part.getPartDrafts().add(partDraft);
				part.merge();
				masterVO = new MasterVO();
				masterVO.setId(part.getId());
				masterVO.setValue(part.getId()+":"+partDraft.getUndoCount()+":"+partDraft.getUniqueIdentifierForUndo());
			}
		}
		return masterVO;
	}
	
	
	/*** Facility to Upload proceedings for a given Slot***/
	@RequestMapping(value="/{id}/uploadproceeding",method=RequestMethod.GET)
    public String populateDocument(final @PathVariable("id") Long id, 
    		final ModelMap model,
            final HttpServletRequest request){
		Proceeding domain =  Proceeding.findById(Proceeding.class, id);
		model.addAttribute("domain", domain);
		HouseType houseType = null;
		if(domain.getSlot()!=null){
			Slot slot=domain.getSlot();
			Roster roster=slot.getRoster();
			Session session=roster.getSession();
			CommitteeMeeting committeeMeeting = roster.getCommitteeMeeting();
			if(session!=null){
				houseType = session.getHouse().getType();
				model.addAttribute("session",session.getId());
				
				/**** Previous Slot ****/
//				Slot previousSlot = Slot.findPreviousSlot(slot);
//				
//				if(previousSlot!=null){
//					Proceeding previousProceeding = Proceeding.findByFieldName(Proceeding.class, "slot", previousSlot, domain.getLocale());
//					if(previousProceeding != null){
//						
//					}
//				}
				
				/****slot****/
				model.addAttribute("slot", domain.getSlot().getId());
				model.addAttribute("slotName",domain.getSlot().getName());
							
			}else if(committeeMeeting!=null){
				model.addAttribute("committeeMeeting",committeeMeeting.getId());
			}
			
			/****Proceeding Id****/
			model.addAttribute("proceeding",domain.getId());
			
			/****Locale****/
			model.addAttribute("locale",domain.getLocale());
			
			/***Reporter***/
			model.addAttribute("reporter",domain.getSlot().getReporter().getId());
			model.addAttribute("userName", this.getCurrentUser().getUsername());
			
			/***Document uploaded***/
			model.addAttribute("documentId",domain.getDocumentId());
			if(domain.getDocumentId() != null){
				Document document = null;
				try {
					document = Document.findByTag(domain.getDocumentId());
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("documentName", document.getOriginalFileName());
			}
		}
		 if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
         }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
         }
		
		return "proceeding/uploadproceeding";
		
	}
	
	
	@RequestMapping(value="/uploadproceeding",method=RequestMethod.POST)
    public  @ResponseBody String saveDocument(
    		@RequestParam(required = false) final MultipartFile file,
            final ModelMap model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request){
		String returnUrl = null;
		try{
			if(file != null){
				String proceedingId = request.getParameter("proceedingId");
				Proceeding domain = Proceeding.findById(Proceeding.class, Long.parseLong(proceedingId));
				CustomParameter storageCustomParameter = CustomParameter
		                    .findByName(CustomParameter.class, "DOCUMENT_STORAGE_USED","");
				if(storageCustomParameter != null){
					/***Saving the document url in Document domain only***/
					Document document = new Document();
		            document.setCreatedOn(new Date());
		            document.setOriginalFileName(file.getOriginalFilename());
		            document.setFileSize(file.getSize());
		            document.setType(file.getContentType());
		            CustomParameter customParameter = CustomParameter
		                    .findByName(
		                            CustomParameter.class, "FILE_PREFIX",
		                            "");
		            document.setTag(customParameter.getValue()
		                    + String.valueOf(UUID.randomUUID().hashCode()));
					if(storageCustomParameter.getValue().equals("drive")){
						//Getting the current active profile				
						String[] profiles = env.getActiveProfiles();
						Properties prop = new Properties();
						if(profiles.length > 0){
							String propertyFile = "props/"+profiles[0]+"/app.properties";
							InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFile);
							if (inputStream != null) {
								prop.load(inputStream);
							} 
						}
						
						/***Path to save the document***/
						String filePath = prop.getProperty("drive.path");
						String fileNameToCreate = filePath + file.getOriginalFilename();
						File newFile = new File(fileNameToCreate);
						FileUtils.writeByteArrayToFile(newFile, file.getBytes());
						document.setPath(fileNameToCreate);
					}else{
						 document.setFileData(file.getBytes());
					}
				    document = document.persist();
		            domain.setDocumentId(document.getTag());
		            domain.persist();
				}
				 redirectAttributes.addFlashAttribute("type", "success");
			     //this is done so as to remove the bug due to which update message appears even though there
			     //is a fresh new/edit request i.e after creating/updating records if we click on
			     //new /edit then success message appears
			     request.getSession().setAttribute("type","success");
			     redirectAttributes.addFlashAttribute("msg", "create_success");
			    /* returnUrl = "redirect:proceeding/"+ domain.getId() +"/uploadproceeding";*/
	            returnUrl = "success";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	   
        return returnUrl;
	}
	
	
	@RequestMapping(value = "/{tag}", method = RequestMethod.GET)
    public void get(@PathVariable final String tag,
                    final HttpServletRequest request,
                    final HttpServletResponse response) {
        Document document = null;
        try {
        	document = Document.findByTag(tag);
            response.setContentType(document.getType());
            response.setContentLength((int) document.getFileSize());
            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + document.getOriginalFileName() + "\"");
            CustomParameter storageCustomParameter = CustomParameter
                    .findByName(CustomParameter.class, "DOCUMENT_STORAGE_USED","");
            if(storageCustomParameter != null){
            	 if(storageCustomParameter.getValue().equals("drive")){
                 	String filePath = document.getPath();
                 	java.io.File file = new java.io.File(filePath);
                 	FileInputStream inputStream = new FileInputStream(file);
                 	FileCopyUtils.copy(
                 			inputStream, response.getOutputStream());
                 }else{
                 	FileCopyUtils.copy(
                             document.getFileData(), response.getOutputStream());
                 }
            }
        } catch (IOException e) {
            logger.error("Error occured while downloading file:" + e.toString());
        }catch (ELSException e) {
			logger.error(e.getMessage());			
		}
    }
	
	@Transactional
    @RequestMapping(value = "/remove/{tag}", method = RequestMethod.DELETE)
    public @ResponseBody Boolean remove(@PathVariable("tag") final String tag,
                   final HttpServletRequest request) {
        Document document =null;
        Boolean success = false;
        try{
        	 String proceedingId = request.getParameter("proceedingId");
        	 Proceeding domain = Proceeding.findById(Proceeding.class, Long.parseLong(proceedingId));
        	 domain.setDocumentId(null);
        	 domain.merge();
        	 document = Document.findByTag(tag);
        	 CustomParameter storageCustomParameter = CustomParameter
                     .findByName(CustomParameter.class, "DOCUMENT_STORAGE_USED","");
             if(storageCustomParameter != null){
             	 if(storageCustomParameter.getValue().equals("drive")){
               	String filePath = document.getPath();
             	java.io.File file = new java.io.File(filePath);
             	file.delete();
             	//Files.deleteIfExists(file.toPath());
             	 }
        	 document.remove();
        	 success = true;
             }
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
         return success;
    }
	
	@RequestMapping(value="/{id}/complete", method=RequestMethod.POST)
	public @ResponseBody Boolean completeSlot(final @PathVariable("id") Long id, final HttpServletRequest request, final ModelMap model, final Locale locale){
		Boolean returnValue = false;
		try{
			Proceeding proceeding = Proceeding.findById(Proceeding.class, id);
			Part part=Part.findByFieldName(Part.class, "proceeding", proceeding, locale.toString());
			if(part.getId()!=null){
				if(part!=null){
					PartDraft partDraft=new PartDraft();
					partDraft.setPageHeading(part.getPageHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setMainHeading(part.getMainHeading());
					partDraft.setRevisedContent(part.getRevisedContent());
					partDraft.setOriginalText(part.getProceedingContent());
					String editedBy = request.getParameter("editingUser");
					partDraft.setEditedBy(editedBy);
					partDraft.setLocale(locale.toString());
					partDraft.setEditedOn(new Date());
					partDraft.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
					partDraft.setWorkflowCopy(false);
					part.getPartDrafts().add(partDraft);
			}
				((BaseDomain)part).merge();
			}else{
				((BaseDomain)part).persist();
			}
			
			
			Slot slot = proceeding.getSlot();
			slot.setCompleted(true);
			slot.setCompletedDate(new Date());
			slot.merge();
			returnValue = true;
					
		}catch (Exception e) {
			returnValue = false;
			e.printStackTrace();
		}
		return returnValue;
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/generalreport", method=RequestMethod.GET)
	public String getReport(HttpServletRequest request, Model model, Locale locale){
		
		Map<String, String[]> requestMap = request.getParameterMap();
		List report = Query.findReport(request.getParameter("report"), requestMap);
		if(report != null && !report.isEmpty()){
			Object[] obj = (Object[])report.get(0);
			if(obj != null){
				
				model.addAttribute("topHeader", obj[0].toString().split(";"));
			}
		}
		model.addAttribute("formater", new FormaterUtil());
		model.addAttribute("locale", locale.toString());
		model.addAttribute("report", report);
		
		return "proceeding/reports/"+request.getParameter("reportout");
	}
	
	
	@RequestMapping(value="/revisions/{partId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("partId")  final Long partId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=Proceeding.getRevisions(partId,locale.toString());
		model.addAttribute("drafts",drafts);		
		return "proceeding/revisions";
	}
	

		
	
		@RequestMapping(value="/getProceedingris", method=RequestMethod.GET)
		public  @ResponseBody ProceedingVO getProceedingris(final ModelMap model,
				final HttpServletRequest request) {
			HouseType houseType = null;
			ProceedingVO proceedingVO = new ProceedingVO();
			String strProceeding=request.getParameter("proceeding");
		
			if(strProceeding!=null && !strProceeding.isEmpty()){
				Proceeding domain=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			
		//	if(domain.getSlot()!=null){
				Slot slot=domain.getSlot();
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				CommitteeMeeting committeeMeeting = roster.getCommitteeMeeting();
				/****slot****/
				model.addAttribute("slotId", domain.getSlot().getId());
				model.addAttribute("slotName",domain.getSlot().getName());
				proceedingVO.setSlotName(domain.getSlot().getName());
	
				String startTime = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "dd-MM-yyyy HH:mm", domain.getLocale());
				String endTime = FormaterUtil.formatDateToString(domain.getSlot().getEndTime(), "dd-MM-yyyy HH:mm", domain.getLocale());
				request.setAttribute("slotDate", slot.getStartTime());
				
				model.addAttribute("slotStartTime", startTime);
				model.addAttribute("slotEndTime", endTime);
				String currentSlotStartDate = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "dd-MM-yyyy");
				String currentSlotStartTime = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "HH:mm");
				model.addAttribute("currentSlotStartDate", currentSlotStartDate);
				model.addAttribute("currentSlotStartTime", currentSlotStartTime);
				proceedingVO.setCurrentSlotStartDate(currentSlotStartDate);
				proceedingVO.setCurrentSlotStartTime(currentSlotStartTime);
				List<User> users=Slot.findDifferentLanguageUsersBySlot(slot);
				String languageReporter="";
				for(int i=0;i<users.size();i++){
					languageReporter=languageReporter+users.get(i).getFirstName();
					if(i+1<users.size()){
						languageReporter=languageReporter+"/";
					}
				}
				
				model.addAttribute("languageReporter", languageReporter);
				proceedingVO.setLanguageReporter(languageReporter);
				if(session!=null){
					houseType = session.getHouse().getType();
					model.addAttribute("session",session.getId());
					
					/**** Previous Slot ****/
					Slot previousSlot = Slot.findPreviousSlot(slot);
					List<Slot> slots = Slot.findSlotsByReporterAndRoster(slot.getRoster(), slot.getReporter());
					List<MasterVO> masterVOs = new ArrayList<MasterVO>();
					for(Slot s : slots){
						if(s.getStartTime().after(slot.getStartTime())){
							MasterVO masterVO = new MasterVO();
							masterVO.setName(s.getName());
							masterVO.setType(FormaterUtil.formatDateToString(s.getStartTime(), "HH:mm", domain.getLocale()));
							masterVO.setValue(FormaterUtil.formatDateToString(s.getEndTime(), "HH:mm", domain.getLocale()));
							masterVOs.add(masterVO);
						}
					}
					
					model.addAttribute("nextSlots", masterVOs);
					
					if(previousSlot!=null){
						Reporter previousReporter = previousSlot.getReporter();
						User previousReporterUser = previousReporter.getUser();
						model.addAttribute("previousReporter", previousReporterUser.getTitle() + " " +previousReporterUser.getLastName());
						MessageResource previousReporterMessage = MessageResource.findByFieldName(MessageResource.class, "code", "part.previousReporterMessage", domain.getLocale());
						
						proceedingVO.setPreviousReporter(previousReporterMessage.getValue()+previousReporterUser.getTitle() + previousReporterUser.getLastName());
						Proceeding previousProceeding = Proceeding.findByFieldName(Proceeding.class, "slot", previousSlot, domain.getLocale());
						if(previousProceeding != null){
							List<Part> previousParts = previousProceeding.getParts();
							if(!previousParts.isEmpty()){
								/**** Last Part of previous part ****/
								Part previousPart = previousParts.get(previousParts.size()-1);
								model.addAttribute("previousPartMainHeading", previousPart.getMainHeading());
								model.addAttribute("previousPartPageHeading", previousPart.getPageHeading());
								model.addAttribute("previousPartSpecialHeading", previousPart.getSpecialHeading());
								if(previousPart.getChairPersonRole()!=null){
									model.addAttribute("previousPartChairPersonRole",previousPart.getChairPersonRole().getId());
									//model.addAttribute("previousPartChairPerson", previousPart.getChairPerson());
								}
								if(previousPart.getDeviceType()!=null){
									model.addAttribute("previousPartDeviceType",previousPart.getDeviceType().getId());
									if(previousPart.getDeviceType().getDevice().equals(ApplicationConstants.QUESTION)){
										Question question = Question.findById(Question.class, previousPart.getDeviceId());
										model.addAttribute("previousPartDeviceId",question.getId());
										model.addAttribute("previousPartDeviceNumber",question.getNumber());
									}else if(previousPart.getDeviceType().getDevice().equals(ApplicationConstants.RESOLUTION)){
										Resolution resolution = Resolution.findById(Resolution.class, previousPart.getDeviceId());
										model.addAttribute("previousPartDeviceId",resolution.getId());
										model.addAttribute("previousPartDeviceNumber",resolution.getNumber());
									}
								}
							}
						}
					}
					
					Slot nextSlot = Slot.findNextSlot(slot);
					if(nextSlot != null){
						Reporter nextReporter = nextSlot.getReporter();
						User nextReporterUser = nextReporter.getUser();
						model.addAttribute("nextReporter", nextReporterUser.getTitle() + " " + nextReporterUser.getLastName());
					}
					/****Party****/
					List<Party> parties=MemberPartyAssociation.findActivePartiesHavingMemberInHouse(session.getHouse(),domain.getLocale());
					model.addAttribute("parties", parties);
					
					/****Ministries****/
					List<Ministry> ministries;
					try {
						ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
						model.addAttribute("ministries", ministries);
					} catch (ELSException e) {
						logger.error("Ministries not assigned to Group");
						e.printStackTrace();
					}
					
					
					
					
					/****Members****/
					List<Member> members=Member.findAll(Member.class, "firstName", "asc", domain.getLocale());
					model.addAttribute("members",members);
					
					/****MemberRoles****/
					List<MemberRole> roles= new ArrayList<MemberRole>();
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_MEMBERROLES_FOR_RIS_LOWERHOUSE", "");
						if(customParameter!=null){
							String allowedMemberRoles = customParameter.getValue();
							String strMemberRoles[] = allowedMemberRoles.split(",");
							for(int i=0; i< strMemberRoles.length;i++){
								MemberRole memberRole = MemberRole.findByFieldName(MemberRole.class, "type", strMemberRoles[i], domain.getLocale());
								if(memberRole!=null){
									roles.add(memberRole);
								}
							}
						}else{
							model.addAttribute("errorcode","allowedmemberrolesforrislowerhousenotset");
							logger.error("Custom Parameter ALLOWED_MEMBERROLES_FOR_RIS_LOWERHOUSE not set");
						}
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_MEMBERROLES_FOR_RIS_UPPERHOUSE", "");
						if(customParameter!=null){
							String allowedMemberRoles = customParameter.getValue();
							String strMemberRoles[] = allowedMemberRoles.split(",");
							for(int i=0; i< strMemberRoles.length;i++){
								MemberRole memberRole = MemberRole.findByFieldName(MemberRole.class, "type", strMemberRoles[i], domain.getLocale());
								if(memberRole!=null){
									roles.add(memberRole);
								}
							}
						}else{
							model.addAttribute("errorcode","allowedmemberrolesforrisupperhousenotset");
							logger.error("Custom Parameter ALLOWED_MEMBERROLES_FOR_RIS_UPPERHOUSE not set");
						}
					}
					
					model.addAttribute("roles", roles);
					
					/****Designation****/
					List<Designation> designations=Designation.findAll(Designation.class, "name", "asc", domain.getLocale());
					model.addAttribute("designations",designations);
					
					
					/****SubDepartments****/
					List<SubDepartment> subDepartments=SubDepartment.findAll(SubDepartment.class, "name", "asc", domain.getLocale());
					model.addAttribute("subDepartments",subDepartments);
					
					/****DeviceType****/
					CustomParameter deviceTypesGoneLive = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_GONE_LIVE", "");
					String strDevices = deviceTypesGoneLive.getValue();
					String[] devTypes = strDevices.split(",");
					List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
					for(String d : devTypes){
						DeviceType deviceType = DeviceType.findByType(d, domain.getLocale());
						deviceTypes.add(deviceType);
					}
					model.addAttribute("deviceTypes", deviceTypes);
					
				}else if(committeeMeeting!=null){
					model.addAttribute("committeeMeeting",committeeMeeting.getId());
					Committee committee = committeeMeeting.getCommittee();
					CommitteeName committeeName = committee.getCommitteeName();
					model.addAttribute("committeeName", committeeName.getDisplayName());
					
				}
				
			/****Parts****/
				
			Part part=Part.findByFieldName(Part.class,"proceeding",domain,domain.getLocale());
			
				
			if(part==null){
				part=new Part();
				part.setProceeding(domain);
				part.setLocale(domain.getLocale());
				part.setReporter(domain.getSlot().getReporter());
				part.persist();
				proceedingVO.setPartid(part.getId());
				proceedingVO.setVersion(part.getVersion());
			}else{
				proceedingVO.setPartid(part.getId());
				proceedingVO.setVersion(part.getVersion());
			}
			

			/****Proceeding Id****/
			model.addAttribute("proceeding",domain.getId());
					
			/****Locale****/
			model.addAttribute("locale",domain.getLocale());
			
			/***Reporter***/
			model.addAttribute("reporter",domain.getSlot().getReporter().getId());
			model.addAttribute("userName", this.getCurrentUser().getUsername());
			
			/****Undo Counts and RedoCount for Editing Functionality****/
			model.addAttribute("undoCount", 0);
			model.addAttribute("redoCount", 0);
			
			model.addAttribute("documentId",domain.getDocumentId());
			MessageResource generalNotice = MessageResource.findByFieldName(MessageResource.class, "code", "part.generalNotice", domain.getLocale());
			proceedingVO.setGeneralNotice(generalNotice.getValue());
			MessageResource mlsURL = MessageResource.findByFieldName(MessageResource.class, "code", "part.mlsUrl", domain.getLocale());
			proceedingVO.setMlsUrl(mlsURL.getValue());
			
			String username = this.getCurrentUser().getActualUsername();
			List<ProceedingAutofill> proceedingAutofills = ProceedingAutofill.
					findAllByFieldName(ProceedingAutofill.class, "username", username, "id", "asc", domain.getLocale());
			model.addAttribute("proceedingAutofills", proceedingAutofills);
			
			}
			return proceedingVO;

		}
		
		@RequestMapping(value="/getBookmarkProceedingris", method=RequestMethod.GET)
		public  @ResponseBody ProceedingVO getBookmarkProceedingris(final ModelMap model,
				final HttpServletRequest request) {
			HouseType houseType = null;	
			ProceedingVO proceedingVO = new ProceedingVO();
			
			String strPart=request.getParameter("proceeding");
			Part part1=Part.findById(Part.class,Long.parseLong(strPart));
			String strProceeding=part1.getProceeding().getId().toString();
			if(strProceeding!=null && !strProceeding.isEmpty()){
				Proceeding domain=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			
		//	if(domain.getSlot()!=null){
				Slot slot=domain.getSlot();
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				CommitteeMeeting committeeMeeting = roster.getCommitteeMeeting();
				/****slot****/
				model.addAttribute("slotId", domain.getSlot().getId());
				model.addAttribute("slotName",domain.getSlot().getName());
				proceedingVO.setSlotName(domain.getSlot().getName());
				String startTime = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "dd-MM-yyyy HH:mm", domain.getLocale());
				String endTime = FormaterUtil.formatDateToString(domain.getSlot().getEndTime(), "dd-MM-yyyy HH:mm", domain.getLocale());
				request.setAttribute("slotDate", slot.getStartTime());
				
				model.addAttribute("slotStartTime", startTime);
				model.addAttribute("slotEndTime", endTime);
				String currentSlotStartDate = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "dd-MM-yyyy");
				String currentSlotStartTime = FormaterUtil.formatDateToString(domain.getSlot().getStartTime(), "HH:mm");
				model.addAttribute("currentSlotStartDate", currentSlotStartDate);
				model.addAttribute("currentSlotStartTime", currentSlotStartTime);
				proceedingVO.setCurrentSlotStartDate(currentSlotStartDate);
				proceedingVO.setCurrentSlotStartTime(currentSlotStartTime);
				List<User> users=Slot.findDifferentLanguageUsersBySlot(slot);
				String languageReporter="";
				for(int i=0;i<users.size();i++){
					languageReporter=languageReporter+users.get(i).getFirstName();
					if(i+1<users.size()){
						languageReporter=languageReporter+"/";
					}
				}
				
				model.addAttribute("languageReporter", languageReporter);
				proceedingVO.setLanguageReporter(languageReporter);
				if(session!=null){
					houseType = session.getHouse().getType();
					model.addAttribute("session",session.getId());
					
					/**** Previous Slot ****/
					Slot previousSlot = Slot.findPreviousSlot(slot);
					List<Slot> slots = Slot.findSlotsByReporterAndRoster(slot.getRoster(), slot.getReporter());
					List<MasterVO> masterVOs = new ArrayList<MasterVO>();
					for(Slot s : slots){
						if(s.getStartTime().after(slot.getStartTime())){
							MasterVO masterVO = new MasterVO();
							masterVO.setName(s.getName());
							masterVO.setType(FormaterUtil.formatDateToString(s.getStartTime(), "HH:mm", domain.getLocale()));
							masterVO.setValue(FormaterUtil.formatDateToString(s.getEndTime(), "HH:mm", domain.getLocale()));
							masterVOs.add(masterVO);
						}
					}
					
					model.addAttribute("nextSlots", masterVOs);
					
					if(previousSlot!=null){
						Reporter previousReporter = previousSlot.getReporter();
						User previousReporterUser = previousReporter.getUser();
						model.addAttribute("previousReporter", previousReporterUser.getTitle() + " " +previousReporterUser.getLastName());
						MessageResource previousReporterMessage = MessageResource.findByFieldName(MessageResource.class, "code", "part.previousReporterMessage", domain.getLocale());
						
						proceedingVO.setPreviousReporter(previousReporterMessage.getValue()+previousReporterUser.getTitle() + previousReporterUser.getLastName());
						Proceeding previousProceeding = Proceeding.findByFieldName(Proceeding.class, "slot", previousSlot, domain.getLocale());
						if(previousProceeding != null){
							List<Part> previousParts = previousProceeding.getParts();
							if(!previousParts.isEmpty()){
								/**** Last Part of previous part ****/
								Part previousPart = previousParts.get(previousParts.size()-1);
								model.addAttribute("previousPartMainHeading", previousPart.getMainHeading());
								model.addAttribute("previousPartPageHeading", previousPart.getPageHeading());
								model.addAttribute("previousPartSpecialHeading", previousPart.getSpecialHeading());
								if(previousPart.getChairPersonRole()!=null){
									model.addAttribute("previousPartChairPersonRole",previousPart.getChairPersonRole().getId());
									//model.addAttribute("previousPartChairPerson", previousPart.getChairPerson());
								}
								if(previousPart.getDeviceType()!=null){
									model.addAttribute("previousPartDeviceType",previousPart.getDeviceType().getId());
									if(previousPart.getDeviceType().getDevice().equals(ApplicationConstants.QUESTION)){
										Question question = Question.findById(Question.class, previousPart.getDeviceId());
										model.addAttribute("previousPartDeviceId",question.getId());
										model.addAttribute("previousPartDeviceNumber",question.getNumber());
									}else if(previousPart.getDeviceType().getDevice().equals(ApplicationConstants.RESOLUTION)){
										Resolution resolution = Resolution.findById(Resolution.class, previousPart.getDeviceId());
										model.addAttribute("previousPartDeviceId",resolution.getId());
										model.addAttribute("previousPartDeviceNumber",resolution.getNumber());
									}
								}
							}
						}
					}
					
					Slot nextSlot = Slot.findNextSlot(slot);
					if(nextSlot != null){
						Reporter nextReporter = nextSlot.getReporter();
						User nextReporterUser = nextReporter.getUser();
						model.addAttribute("nextReporter", nextReporterUser.getTitle() + " " + nextReporterUser.getLastName());
					}
					/****Party****/
					List<Party> parties=MemberPartyAssociation.findActivePartiesHavingMemberInHouse(session.getHouse(),domain.getLocale());
					model.addAttribute("parties", parties);
					
					/****Ministries****/
					List<Ministry> ministries;
					try {
						ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), session.getLocale());
						model.addAttribute("ministries", ministries);
					} catch (ELSException e) {
						logger.error("Ministries not assigned to Group");
						e.printStackTrace();
					}
					
					
					
					
					/****Members****/
					List<Member> members=Member.findAll(Member.class, "firstName", "asc", domain.getLocale());
					model.addAttribute("members",members);
					
					/****MemberRoles****/
					List<MemberRole> roles= new ArrayList<MemberRole>();
					if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_MEMBERROLES_FOR_RIS_LOWERHOUSE", "");
						if(customParameter!=null){
							String allowedMemberRoles = customParameter.getValue();
							String strMemberRoles[] = allowedMemberRoles.split(",");
							for(int i=0; i< strMemberRoles.length;i++){
								MemberRole memberRole = MemberRole.findByFieldName(MemberRole.class, "type", strMemberRoles[i], domain.getLocale());
								if(memberRole!=null){
									roles.add(memberRole);
								}
							}
						}else{
							model.addAttribute("errorcode","allowedmemberrolesforrislowerhousenotset");
							logger.error("Custom Parameter ALLOWED_MEMBERROLES_FOR_RIS_LOWERHOUSE not set");
						}
					}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
						CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "ALLOWED_MEMBERROLES_FOR_RIS_UPPERHOUSE", "");
						if(customParameter!=null){
							String allowedMemberRoles = customParameter.getValue();
							String strMemberRoles[] = allowedMemberRoles.split(",");
							for(int i=0; i< strMemberRoles.length;i++){
								MemberRole memberRole = MemberRole.findByFieldName(MemberRole.class, "type", strMemberRoles[i], domain.getLocale());
								if(memberRole!=null){
									roles.add(memberRole);
								}
							}
						}else{
							model.addAttribute("errorcode","allowedmemberrolesforrisupperhousenotset");
							logger.error("Custom Parameter ALLOWED_MEMBERROLES_FOR_RIS_UPPERHOUSE not set");
						}
					}
					
					model.addAttribute("roles", roles);
					
					/****Designation****/
					List<Designation> designations=Designation.findAll(Designation.class, "name", "asc", domain.getLocale());
					model.addAttribute("designations",designations);
					
					
					/****SubDepartments****/
					List<SubDepartment> subDepartments=SubDepartment.findAll(SubDepartment.class, "name", "asc", domain.getLocale());
					model.addAttribute("subDepartments",subDepartments);
					
					/****DeviceType****/
					CustomParameter deviceTypesGoneLive = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_GONE_LIVE", "");
					String strDevices = deviceTypesGoneLive.getValue();
					String[] devTypes = strDevices.split(",");
					List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
					for(String d : devTypes){
						DeviceType deviceType = DeviceType.findByType(d, domain.getLocale());
						deviceTypes.add(deviceType);
					}
					model.addAttribute("deviceTypes", deviceTypes);
					
				}else if(committeeMeeting!=null){
					model.addAttribute("committeeMeeting",committeeMeeting.getId());
					Committee committee = committeeMeeting.getCommittee();
					CommitteeName committeeName = committee.getCommitteeName();
					model.addAttribute("committeeName", committeeName.getDisplayName());
					
				}
				
			/****Parts****/
				
			Part part=Part.findByFieldName(Part.class,"proceeding",domain,domain.getLocale());
			
				
			if(part==null){
				part=new Part();
				part.setProceeding(domain);
				part.setLocale(domain.getLocale());
				part.setReporter(domain.getSlot().getReporter());
				part.persist();
				proceedingVO.setPartid(part.getId());
				proceedingVO.setVersion(part.getVersion());
			}else{
				proceedingVO.setPartid(part.getId());	
				proceedingVO.setVersion(part.getVersion());
			}
			

			/****Proceeding Id****/
			model.addAttribute("proceeding",domain.getId());
					
			/****Locale****/
			model.addAttribute("locale",domain.getLocale());
			
			/***Reporter***/
			model.addAttribute("reporter",domain.getSlot().getReporter().getId());
			model.addAttribute("userName", this.getCurrentUser().getUsername());
			
			/****Undo Counts and RedoCount for Editing Functionality****/
			model.addAttribute("undoCount", 0);
			model.addAttribute("redoCount", 0);
			
			model.addAttribute("documentId",domain.getDocumentId());
			MessageResource generalNotice = MessageResource.findByFieldName(MessageResource.class, "code", "part.generalNotice", domain.getLocale());
			proceedingVO.setGeneralNotice(generalNotice.getValue());	
			MessageResource mlsURL = MessageResource.findByFieldName(MessageResource.class, "code", "part.mlsUrl", domain.getLocale());	
			proceedingVO.setMlsUrl(mlsURL.getValue());
		
			String username = this.getCurrentUser().getActualUsername();
			List<ProceedingAutofill> proceedingAutofills = ProceedingAutofill.
					findAllByFieldName(ProceedingAutofill.class, "username", username, "id", "asc", domain.getLocale());
			model.addAttribute("proceedingAutofills", proceedingAutofills);
			
			}
			return proceedingVO;

		}
		
		@RequestMapping(value = "/notifyPendingTurn", method = RequestMethod.GET)
	    public String notifyPendingTurnInit(final ModelMap model, 
	    		final HttpServletRequest request,
	            final Locale locale) {
	        final String servletPath = request.getServletPath().replaceFirst("\\/","");
	        
	        /** rosterHandledBy **/
	        String rosterHandledBy = request.getParameter("rosterHandledBy");
	        if(rosterHandledBy!=null && !rosterHandledBy.isEmpty()) {
	        	model.addAttribute("rosterHandledBy", rosterHandledBy);
	        } else if(request.getSession().getAttribute("rosterHandledBy")!=null){
	        	model.addAttribute("rosterHandledBy",request.getSession().getAttribute("rosterHandledBy"));
	            request.getSession().removeAttribute("rosterHandledBy");
	        } else {
	        	model.addAttribute("rosterHandledBy", "");
	        }        
	        
	        /** is volatile notification **/
	        if(request.getSession().getAttribute("isVolatile")!=null){
	        	model.addAttribute("isVolatile",request.getSession().getAttribute("isVolatile"));
	            request.getSession().removeAttribute("isVolatile");
	        }else{        	
	            if(rosterHandledBy!=null && !rosterHandledBy.isEmpty()) {
	            	model.addAttribute("isVolatile", false);
	            } else {
	            	model.addAttribute("isVolatile", true);
	            }
	        }
	        
	        /** notification title **/
	        if(request.getSession().getAttribute("notificationTitle")!=null){
	        	model.addAttribute("notificationTitle",request.getSession().getAttribute("notificationTitle"));
	            request.getSession().removeAttribute("notificationTitle");
	        } else {
	        	model.addAttribute("notificationTitle","Notify Pending Turn");
	        }
	        
	        /** notification message **/
	        if(request.getSession().getAttribute("notificationMessage")!=null){
	        	model.addAttribute("notificationMessage",request.getSession().getAttribute("notificationMessage"));
	            request.getSession().removeAttribute("notificationMessage");
	        }
	        
	        //this is done so as to remove the bug due to which update message appears even though there
	        //is a fresh request
	        if(request.getSession().getAttribute("type")==null){
	            model.addAttribute("type","");
	        }else{
	        	model.addAttribute("type",request.getSession().getAttribute("type"));
	            request.getSession().removeAttribute("type");
	        }
	        
	        //here making provisions for displaying error pages
	        if(model.containsAttribute("errorcode")){
	            return servletPath.replace("notifyPendingTurn","error");
	        }else{
	            return servletPath;
	        }
	    }
		
		@RequestMapping(value = "/notifyPendingTurn", method = RequestMethod.POST)
	    public String notifyPendingTurn(final ModelMap model, 
	    		final HttpServletRequest request,
	    		final RedirectAttributes redirectAttributes,
	            final Locale locale) {
			final String servletPath = request.getServletPath().replaceFirst("\\/","");
			String returnUrl = "";
			String rosterHandledBy = request.getParameter("rosterHandledBy");
			String notificationTitle = request.getParameter("notificationTitle");
			String notificationMessage = request.getParameter("notificationMessage");
			String isVolatile = request.getParameter("isVolatile");
			if(rosterHandledBy!=null && !rosterHandledBy.isEmpty()) {
				if(notificationTitle!=null && !notificationTitle.isEmpty()
						&& isVolatile!=null && !isVolatile.isEmpty()) {
					try {
//						StringBuffer senderName = new StringBuffer("");
//						if(this.getCurrentUser().getTitle()!=null && !this.getCurrentUser().getTitle().isEmpty()) {
//							senderName.append(this.getCurrentUser().getTitle());
//							senderName.append(" ");
//						}
//						if(this.getCurrentUser().getFirstName()!=null && !this.getCurrentUser().getFirstName().isEmpty()) {
//							senderName.append(this.getCurrentUser().getFirstName());
//							senderName.append(" ");
//						}
//						if(this.getCurrentUser().getLastName()!=null && !this.getCurrentUser().getLastName().isEmpty()) {
//							senderName.append(this.getCurrentUser().getLastName());
//						}
						NotificationController.sendNotificationFromUserPage(this.getCurrentUser().getActualUsername(), notificationTitle, notificationMessage, Boolean.parseBoolean(isVolatile), rosterHandledBy, locale.toString());
											 
						request.getSession().setAttribute("rosterHandledBy", rosterHandledBy);
						request.getSession().setAttribute("isVolatile", isVolatile);
						request.getSession().setAttribute("notificationTitle", notificationTitle);	
						request.getSession().setAttribute("notificationMessage", notificationMessage);
						
				        //this is done so as to remove the bug due to which update message appears even though there
				        //is a fresh request
				        request.getSession().setAttribute("type","success");
				        redirectAttributes.addFlashAttribute("msg", "update_success");
				        returnUrl = "redirect:/" + servletPath;
					} catch (Exception e) {
						e.printStackTrace();
						//error
						model.addAttribute("errorcode", "EXCEPTION_OCCURRED");
						returnUrl = servletPath.replace("notifyPendingTurn","error");
					}
				} else {
					//error
					model.addAttribute("errorcode", "TITLE_EMPTY");
			        returnUrl = servletPath.replace("notifyPendingTurn","error");
				}	        	        
			} else {
				//error
				model.addAttribute("errorcode", "CHIEF_REPORTER_NAME_EMPTY");
				returnUrl = servletPath.replace("notifyPendingTurn","error");
			}
			return returnUrl;
	    }
}
