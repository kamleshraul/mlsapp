package org.mkcl.els.controller.wf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.mois.BillAmendmentMotionController;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Section;
import org.mkcl.els.domain.SectionAmendment;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/workflow/billamendmentmotion")
public class BillAmendmentMotionWorkflowController extends BaseController {
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
	@SuppressWarnings("unused")
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/**** Date ****/
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
		if(this.getUserLocale().equals(new Locale("mr","IN")))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),this.getUserLocale());
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		}
		/**** Member ****/
		binder.registerCustomEditor(Member.class, new BaseEditor(new Member()));		
		/**** Status ****/
		binder.registerCustomEditor(Status.class, new BaseEditor(new Status()));
		/**** House Type ****/
		binder.registerCustomEditor(HouseType.class, new BaseEditor(new HouseType()));
		/**** Session ****/
		binder.registerCustomEditor(Session.class, new BaseEditor(new Session()));
		/**** Device Type ****/
		binder.registerCustomEditor(DeviceType.class, new BaseEditor(new DeviceType()));
		/**** Device Type ****/
		binder.registerCustomEditor(Bill.class, new BaseEditor(new Bill()));
	}

	@RequestMapping(value="supportingmember",method=RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Bill Amendment Motion ****/
		String billAmendmentMotionId=workflowDetails.getDeviceId();
		model.addAttribute("billAmendmentMotion",billAmendmentMotionId);
		BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,Long.parseLong(billAmendmentMotionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers=billAmendmentMotion.getSupportingMembers();
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					//i.setApprovedTitles(billAmendmentMotion.getTitles());
					model.addAttribute("currentSupportingMember", i.getMember().getId());
					model.addAttribute("domain", i);
					if (i.getDecisionStatus() != null) {
						model.addAttribute("decisionStatus", i.getDecisionStatus().getId());
						model.addAttribute("formattedDecisionStatus", i.getDecisionStatus().getName());
					}
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
					if (customParameter != null) {
						SimpleDateFormat format = new SimpleDateFormat(customParameter.getValue());
						model.addAttribute("requestReceivedOnDate", format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, billAmendmentMotion,supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}
	
	private void populateSupportingMember(final ModelMap model,final BillAmendmentMotion billAmendmentMotion, 
			final List<SupportingMember> supportingMembers,final String locale){
		/**** Device Type ****/
		DeviceType deviceType=billAmendmentMotion.getType();
		if(deviceType!=null){
			model.addAttribute("deviceType", deviceType.getName());
			model.addAttribute("deviceTypeId", deviceType.getId());
		}
		/**** Session Year and Session Type ****/
		Session session=billAmendmentMotion.getSession();
		if(session!=null){
			model.addAttribute("sessionId", session.getId());
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName",billAmendmentMotion.getHouseType().getName());
		model.addAttribute("houseType",billAmendmentMotion.getHouseType().getType());
		/**** Amended Bill ****/
		Bill amendedBill = billAmendmentMotion.getAmendedBill();
		if(amendedBill==null) {
			logger.error("amendedBill is not set for this billamendmentmotion having id="+billAmendmentMotion.getId()+".");
			model.addAttribute("errorcode", "amendedBill_null");			
			return;
		}
		model.addAttribute("amendedBill", amendedBill.getId());
		String amendedBillInfo = billAmendmentMotion.getAmendedBillInfo();
		if(amendedBillInfo!=null && !amendedBillInfo.isEmpty()) {
			amendedBillInfo = amendedBillInfo.replace("#", "~");
		} else {
			model.addAttribute("errorcode","amendedBillInfo_notfound");
			return;
		}
		model.addAttribute("amendedBillInfo", amendedBillInfo);
		/**** Amended Bill Languages ****/		
		billAmendmentMotion.setAmendedBillLanguages(amendedBill.findLanguagesOfContentDrafts());
		model.addAttribute("amendedBillLanguages", billAmendmentMotion.getAmendedBillLanguages());
		/**** section amendments ****/
		String defaultBillLanguage = billAmendmentMotion.getSession().getParameter(billAmendmentMotion.getAmendedBill().getType().getType()+"_defaultTitleLanguage");
		model.addAttribute("defaultBillLanguage", defaultBillLanguage);
		boolean isSuccessful = populateSectionAmendments(model, billAmendmentMotion, billAmendmentMotion.getSession(), deviceType);
		if(!isSuccessful) {
			return;
		}
		/**** Supporting Members ****/
		List<Member> members=new ArrayList<Member>();
		if(supportingMembers!=null){
			for(SupportingMember i:supportingMembers){
				Member selectedMember=i.getMember();
				members.add(selectedMember);
			}
			if(!members.isEmpty()){
				StringBuffer buffer=new StringBuffer();
				for(Member i:members){
					buffer.append(i.getFullnameLastNameFirst()+",");
				}
				buffer.deleteCharAt(buffer.length()-1);
				model.addAttribute("supportingMembersName", buffer.toString());
			}
		}
		/**** Decision Status ****/
		Status approveStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
		Status rejectStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale);
		List<Status> decisionStatus=new ArrayList<Status>();
		decisionStatus.add(approveStatus);
		decisionStatus.add(rejectStatus);
		model.addAttribute("decisionStatus",decisionStatus);
		/**** Primary Member ****/
		model.addAttribute("primaryMemberName",billAmendmentMotion.getPrimaryMember().getFullnameLastNameFirst());		
	}
	
	@RequestMapping(value="supportingmember",method=RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final SupportingMember domain) throws ELSException {
		/**** update supporting member */
		String strMember=request.getParameter("currentSupportingMember");
		String requestReceivedOn=request.getParameter("requestReceivedOnDate");
		if(requestReceivedOn!=null&& !(requestReceivedOn.isEmpty())){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT","");
			if(customParameter!=null){
				SimpleDateFormat format=new SimpleDateFormat(customParameter.getValue());
				try {
					domain.setRequestReceivedOn(format.parse(requestReceivedOn));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		Member member=Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());		
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
			domain.setApprovedSectionAmendments(approvedSectionAmendments);			
//			/**** Set Position ****/
//			if(domain.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
//				if(bill.getSupportingMembers()!=null) {
//					if(!bill.getSupportingMembers().isEmpty()) {
//						synchronized (domain) {
//							Collections.sort(bill.getSupportingMembers(), SupportingMember.COMPARE_BY_POSITION);
//							Integer currentHighestPosition = bill.getSupportingMembers().get(bill.getSupportingMembers().size()-1).getPosition();
//							if(currentHighestPosition==null) {
//								domain.setPosition(1);
//							} else {
//								domain.setPosition(currentHighestPosition + 1);
//							}
//						}						
//					}
//				}
//			}			
		}		
		domain.merge();
		/**** update workflow details ****/
		String strWorkflowdetails=domain.getWorkflowDetailsId();
		if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();
			/**** complete the task ****/		 
			String strTaskId=workflowDetails.getTaskId();
			Task task=processService.findTaskById(strTaskId);
			processService.completeTask(task);
			model.addAttribute("task",strTaskId);		
		}
		/**** display message ****/
		model.addAttribute("type","taskcompleted");
		return "workflow/info";
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		if(longWorkflowdetails==null){
			longWorkflowdetails=Long.parseLong(request.getParameter("workflowdetails"));
		}
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Adding workflowdetails and task to model ****/
		model.addAttribute("workflowdetails",workflowDetails.getId());
		model.addAttribute("workflowstatus",workflowDetails.getStatus());
		model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
		model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
		BillAmendmentMotion domain=BillAmendmentMotion.findById(BillAmendmentMotion.class,Long.parseLong(workflowDetails.getDeviceId()));
		/**** Populate Model ****/
		populateModel(domain,model,request,workflowDetails);			
		return workflowDetails.getForm();
	}
	
	private void populateModel(final BillAmendmentMotion domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) {
		/**** clear remarks ****/
		domain.setRemarks("");
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
			logger.error("devicetype is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "devicetype_null");			
			return;
		}
		model.addAttribute("formattedDeviceType", deviceType.getName());
		model.addAttribute("deviceType", deviceType.getId());
		model.addAttribute("selectedDeviceType", deviceType.getType());
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
		/**** UserGroup and UserGroup Type ****/
		String usergroup = workflowDetails.getAssigneeUserGroupId();
		model.addAttribute("usergroup",usergroup);
		UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(usergroup));
		String usergroupType = workflowDetails.getAssigneeUserGroupType();
		model.addAttribute("usergroupType",usergroupType);
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
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
		/**** list of put up options available ****/			
		if(!workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				&& !workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {
//			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
//					|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
//					|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
//				populateInternalStatus(model,domain,domain.getRecommendationStatus(),domain.getLocale());
//			} else {
//				populateInternalStatus(model,domain,domain.getInternalStatus(),domain.getLocale());
//			}
			populateInternalStatus(model,domain,workflowDetails.getWorkflowType(),locale);
		} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				&& !workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.TRANSLATOR)) {
			List<Status> translationStatuses=new ArrayList<Status>();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseTypeForWorkflow = domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_TRANSLATION_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					try {
						translationStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}/**** BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				try {
					translationStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/**** BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				try {
					translationStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			/**** BILLAMENDMENTMOTION_TRANSLATION_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				try {
					translationStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses",translationStatuses);
		} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)
				&& !workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.OPINION_ABOUT_BILLAMENDMENTMOTION_DEPARTMENT)) {
			List<Status> opinionFromLawAndJDStatuses=new ArrayList<Status>();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseTypeForWorkflow = domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_OPINION_FROM_LAWANDJD_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					try {
						opinionFromLawAndJDStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}/**** BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				try {
					opinionFromLawAndJDStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/**** BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				try {
					opinionFromLawAndJDStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			/**** BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				try {
					opinionFromLawAndJDStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses",opinionFromLawAndJDStatuses);
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
		/**** Populating Put up options and Actors ****/							
		if(usergroupType!=null&&!usergroupType.isEmpty()){				
			String currentStatusType=null;	
//			List<Reference> actors=new ArrayList<Reference>();
			CustomParameter finalApprovingAuthority=null;
			String recommendedAction = null;
			Status statusRecommended = null;
//			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.APPROVAL_WORKFLOW)) {
//				if(domain.getInternalStatus()!=null) {					
//					actors = WorkflowConfig.findBillAmendmentMotionActorsVO(domain, domain.getInternalStatus(), userGroup, 1, locale);
//					statusRecommended = domain.getInternalStatus();
//					currentStatusType = domain.getInternalStatus().getType();
//					if(currentStatusType!=null) {
//						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
//						recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
//						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
//							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
//								statusRecommended = Status.findByType("billamendmentmotion_final_"+recommendedAction, locale);
//								model.addAttribute("hideActorsFlag",true);
//								if(statusRecommended!=null) {
//									domain.setInternalStatus(statusRecommended);
//									domain.setRecommendationStatus(statusRecommended);
//								}
//							}
//						}						
//					}
//				}								
//			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
//				if(domain.getInternalStatus()!=null) {
//					actors = WorkflowConfig.findBillAmendmentMotionActorsVO(domain, domain.getInternalStatus(), userGroup, 1, locale);
//					statusRecommended = domain.getInternalStatus();
//					currentStatusType = domain.getInternalStatus().getType();
//					if(currentStatusType!=null) {
//						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
//						if(currentStatusType.contains("reject")) {
//							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
//									+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
//						} else {
//							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
//						}
//						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
//							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
//								statusRecommended = Status.findByType("billamendmentmotion_final_"+recommendedAction, locale);
//								model.addAttribute("hideActorsFlag",true);
//								if(statusRecommended!=null) {
//									domain.setInternalStatus(statusRecommended);
//									domain.setRecommendationStatus(statusRecommended);
//								}
//							}
//						}						
//					}
//				}				
//			} else 
			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)) {				
				if(translationStatus!=null) {
//					if(!(usergroupType.equals(ApplicationConstants.TRANSLATOR) && workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED))) {
//						actors = WorkflowConfig.findBillAmendmentMotionActorsVO(domain, translationStatus, userGroup, 1, locale);
//					}					
					statusRecommended = translationStatus;
					currentStatusType = translationStatus.getType();
					if(currentStatusType!=null) {
						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_TRANSLATION_FINAL_AUTHORITY", "");
						if(currentStatusType.contains("reject")) {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
									+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
						} else {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
						}
						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
								statusRecommended = Status.findByType("billamendmentmotion_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);								
							}
						}						
					}
				}					
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {				
				if(opinionFromLawAndJDStatus!=null) {
//					if(!(usergroupType.equals(ApplicationConstants.OPINION_ABOUT_BILLAMENDMENTMOTION_DEPARTMENT) && workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED))) {
//						actors = WorkflowConfig.findBillAmendmentMotionActorsVO(domain, opinionFromLawAndJDStatus, userGroup, 1, locale);
//					}					
					statusRecommended = opinionFromLawAndJDStatus;
					currentStatusType = opinionFromLawAndJDStatus.getType();
					if(currentStatusType!=null) {
						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_OPINION_FROM_LAWANDJD_FINAL_AUTHORITY", "");
						if(currentStatusType.contains("reject")) {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
									+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
						} else {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
						}
						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
								statusRecommended = Status.findByType("billamendmentmotion_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);								
							}
						}						
					}
				}
			} else {
				if(workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
						|| workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
//					actors = WorkflowConfig.findBillAmendmentMotionActorsVO(domain, recommendationStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
					statusRecommended = domain.getRecommendationStatus();
					currentStatusType = domain.getRecommendationStatus().getType();
				}else{
//					actors = WorkflowConfig.findBillAmendmentMotionActorsVO(domain, internalStatus, userGroup, Integer.parseInt(domain.getLevel()), locale);
					statusRecommended = domain.getInternalStatus();
					currentStatusType = domain.getInternalStatus().getType();
				}
				if(currentStatusType!=null) {
					if(currentStatusType.contains("reject") && !currentStatusType.endsWith("rejection")) {
						recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
								+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
					} else {
						recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
					}
					finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+houseType.getType().toUpperCase()+"_"+workflowDetails.getWorkflowType().toUpperCase()+"_FINAL_AUTHORITY", "");
					if(finalApprovingAuthority==null) {
						finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_"+workflowDetails.getWorkflowType().toUpperCase()+"_FINAL_AUTHORITY", "");
					}					
					if(finalApprovingAuthority==null) {
						finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
					}
					if(finalApprovingAuthority!=null&&recommendedAction!=null) {
						if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
							statusRecommended = Status.findByType("billamendmentmotion_final_"+recommendedAction, locale);
							model.addAttribute("hideActorsFlag",true);
							if(statusRecommended!=null) {
								if(!workflowDetails.getWorkflowType().equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
										&& !workflowDetails.getWorkflowType().equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
										&& !workflowDetails.getWorkflowType().equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
									domain.setInternalStatus(statusRecommended);
								} 								
								domain.setRecommendationStatus(statusRecommended);
							}
						}
					}						
				}
			}
			if(statusRecommended!=null) {
				model.addAttribute("internalStatusSelected",statusRecommended.getId());
			}				
//			model.addAttribute("actors",actors);
//			if(actors!=null&&!actors.isEmpty()){
//				String nextActor=actors.get(0).getId();
//				String[] actorArr=nextActor.split("#");
//				domain.setLevel(actorArr[2]);
//				domain.setLocalizedActorName(actorArr[3]+"("+actorArr[4]+")");
//			}
		}
		/**** level ****/
		model.addAttribute("level",workflowDetails.getAssigneeLevel());			
		/**** add domain to model ****/
		model.addAttribute("domain",domain);
		/**** mail & timer process variables ****/
		//default values for process variables. can set conditionally for given actor here.
		model.addAttribute("pv_mailflag", "off");
		model.addAttribute("pv_reminderflag", "off");
		model.addAttribute("pv_timerflag", "off");
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final BillAmendmentMotion domain,final BindingResult result) {
		/**** Workflowdetails ****/
		String strWorkflowdetails = (String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		try {
			BillAmendmentMotion billAmendmentMotion = null;
			if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
				/**** display message ****/
				model.addAttribute("type","taskalreadycompleted");
				return "workflow/info";
			}
			/**** amended bill ****/
			//String amendedBillId = request.getP
			/** Custom Status for Auxillary Workflows **/
			Status customStatus = null;		
			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
					|| workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) { 
				if(request.getParameter("customStatus")!=null && !request.getParameter("customStatus").isEmpty()) {
					customStatus = Status.findById(Status.class, Long.parseLong(request.getParameter("customStatus")));
					workflowDetails.setCustomStatus(customStatus.getType());
				}			
			}		
			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
					&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.TRANSLATOR)) {
				if(billAmendmentMotion==null) {
					billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, domain.getId());
				}		
				billAmendmentMotion.setAmendedBillLanguages(domain.getAmendedBillLanguages());
				/**** add/update revised section amendments in domain ****/
				billAmendmentMotion.getRevisedSectionAmendments().clear();
				List<SectionAmendment> revisedSectionAmendments = new ArrayList<SectionAmendment>();
				try {
					revisedSectionAmendments = this.updateRevisedSectionAmendments(domain, request);
				} catch (ELSException e) {
					e.printStackTrace();			
				}
				if(revisedSectionAmendments!=null) {
					for(SectionAmendment rsa: revisedSectionAmendments) {
						billAmendmentMotion.getRevisedSectionAmendments().add(rsa);
					}
				}
				billAmendmentMotion.setEditedOn(new Date());
				billAmendmentMotion.setEditedBy(this.getCurrentUser().getActualUsername());
				billAmendmentMotion.setEditedAs(workflowDetails.getAssigneeUserGroupName());
				billAmendmentMotion.merge();
				if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
					if(request.getParameter("operation").equals("saveTranslation")) {
						model.addAttribute("type","success");
						model.addAttribute("workflowdetails",workflowDetails.getId());
						model.addAttribute("workflowstatus",workflowDetails.getStatus());
						model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
						model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
						populateModel(billAmendmentMotion, model, request, workflowDetails);
						return "workflow/billamendmentmotion/"+userGroupType;
					} else if(request.getParameter("operation").equals("sendTranslation")) {
						String endFlag=request.getParameter("endFlagForAuxillaryWorkflow");
						Map<String,String> properties=new HashMap<String, String>();
						String level="";
						String nextUserGroupType="";
						String nextuser = request.getParameter("actor");
						if(nextuser!=null){
							if(!nextuser.isEmpty()){
								String[] temp=nextuser.split("#");
								nextUserGroupType=temp[1];
								properties.put("pv_user",temp[0]);
								level=temp[2];
							}
						}
						properties.put("pv_deviceId",String.valueOf(billAmendmentMotion.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(billAmendmentMotion.getType().getId()));		
						properties.put("pv_endflag", endFlag);
						properties.put("pv_timerflag", "off");
						properties.put("pv_mailflag", "off");		
						String strTaskId=workflowDetails.getTaskId();
						Task task=processService.findTaskById(strTaskId);
						processService.completeTask(task,properties);		
						if(endFlag!=null){
							if(!endFlag.isEmpty()){
								if(endFlag.equals("continue")){					
									ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
									Task newtask=processService.getCurrentTask(processInstance);
									/**** Workflow Detail entry made only if its not the end of workflow ****/
									if(customStatus!=null) {
										WorkflowDetails.create(domain,newtask,workflowDetails.getWorkflowType(),customStatus.getType(),nextUserGroupType, level);
									} else {
										WorkflowDetails.create(domain,newtask,workflowDetails.getWorkflowType(),null,nextUserGroupType, level);
									}
									
								}
							}
						}
						workflowDetails.setStatus("COMPLETED");
						workflowDetails.setCompletionTime(new Date());			
						Status translationCompletedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_TRANSLATION_COMPLETED, billAmendmentMotion.getLocale());
						workflowDetails.setCustomStatus(translationCompletedStatus.getType());
						workflowDetails.merge();
						/**** display message ****/
						model.addAttribute("type","taskcompleted");
						return "workflow/info";					
					}
				}
			}
			/**** Binding Supporting Members ****/
			String[] strSupportingMembers=request.getParameterValues("selectedSupportingMembers");
			List<SupportingMember> members=new ArrayList<SupportingMember>();
			if(domain.getId()!=null){
				if(billAmendmentMotion==null) {
					billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
				}			
				members=billAmendmentMotion.getSupportingMembers();
			}
			if(strSupportingMembers!=null){
				if(strSupportingMembers.length>0){
					List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
					for(String i:strSupportingMembers){
						SupportingMember supportingMember=null;
						Member member=Member.findById(Member.class, Long.parseLong(i));
						/**** If supporting member is already present then do nothing ****/
						for(SupportingMember j:members){
							if(j.getMember().getId().equals(member.getId())){
								supportingMember=j;
								supportingMembers.add(supportingMember);
								break;
							}
						}					
					}
					domain.setSupportingMembers(supportingMembers);
				}
			}
//			/**** Binding Supporting Members ****/
//			String[] strSupportingMembers=request.getParameterValues("supportingMembers");
//			if(strSupportingMembers!=null){
//				if(strSupportingMembers.length>0){
//					List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
//					for(String i:strSupportingMembers){
//						SupportingMember supportingMember=SupportingMember.findById(SupportingMember.class, Long.parseLong(i));
//						supportingMembers.add(supportingMember);
//					}
//					domain.setSupportingMembers(supportingMembers);
//				}
//			}
			/***** To retain the clubbed bill amendment motions when moving through workflow ****/
			String[] strClubbedEntities= request.getParameterValues("clubbedEntities");
			if(strClubbedEntities!=null){
				if(strClubbedEntities.length>0){
					List<ClubbedEntity> clubbedEntities=new ArrayList<ClubbedEntity>();
					for(String i:strClubbedEntities){
						ClubbedEntity clubbedEntity=ClubbedEntity.findById(ClubbedEntity.class, Long.parseLong(i));
						clubbedEntities.add(clubbedEntity);
					}
					domain.setClubbedEntities(clubbedEntities);
				}
			}
			/***** To retain the parent billamendmentmotion when moving through workflow ****/
			String strParentBillAmendmentMotion = request.getParameter("parent");
			if(strParentBillAmendmentMotion!=null) {
				if(!strParentBillAmendmentMotion.isEmpty()) {
					BillAmendmentMotion parentBillAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(strParentBillAmendmentMotion));
					domain.setParent(parentBillAmendmentMotion);
				}
			}
			/**** Updating domain ****/
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
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
			
			/**** added by dhananjayb.. required in case when domain is updated with start of new workflow before completion of current workflow ****/
			String endFlagForCurrentWorkflow = domain.getEndFlag();				
			
			if(domain.getDrafts()==null) {
				if(billAmendmentMotion==null) {
					billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
				}
				domain.setDrafts(billAmendmentMotion.getDrafts());
			}
			
			String currentDeviceTypeWorkflowType = workflowDetails.getWorkflowType();
			if(!currentDeviceTypeWorkflowType.equals(ApplicationConstants.TRANSLATION_WORKFLOW) 
					&& !currentDeviceTypeWorkflowType.equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {
				Workflow workflowFromUpdatedStatus = domain.findWorkflowFromStatus();
				
				//String sendbackactor=request.getParameter("sendbackactor");
				if(workflowFromUpdatedStatus!=null) {
					currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
				}
			}
				
			performAction(domain, request);	
			domain.merge();
			
			/**** Complete Task ****/			
			if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
				if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW) 
						&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.OPINION_ABOUT_BILLAMENDMENTMOTION_DEPARTMENT)
						&& request.getParameter("operation").equals("saveOpinionFromLawAndJD")) {
					model.addAttribute("type","success");
					model.addAttribute("workflowdetails",workflowDetails.getId());
					model.addAttribute("workflowstatus",workflowDetails.getStatus());
					model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
					model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
					/** Stale State Exception **/
					billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
					populateModel(billAmendmentMotion, model, request, workflowDetails);
					return "workflow/billamendmentmotion/"+userGroupType;
				}
			}		
			String endFlag="";	
			Map<String,String> properties=new HashMap<String, String>();
			String level="";
			String nextUserGroupType="";
			String nextuser = request.getParameter("actor");
			if(nextuser!=null){
				if(!nextuser.isEmpty()){
					String[] temp=nextuser.split("#");
					nextUserGroupType=temp[1];
					properties.put("pv_user",temp[0]);
					level=temp[2];
				}
			}
			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
					|| workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {
				endFlag=request.getParameter("endFlagForAuxillaryWorkflow");
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
				endFlag = endFlagForCurrentWorkflow;
			} else {
				endFlag=request.getParameter("endFlag");
			}					
			properties.put("pv_deviceId",String.valueOf(domain.getId()));
			properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));		
			properties.put("pv_endflag", endFlag);
			/**** set timer for translation ****/
			if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.TRANSLATION_WORKFLOW)
					&& customStatus.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_TRANSLATION)
					&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)) {
				properties.put("pv_workflowtype", ApplicationConstants.TRANSLATION_WORKFLOW);
				properties.put("pv_timerflag", "set");
				String lastTimerDuration;
				String translationTimeoutDays = domain.getSession().getParameter(domain.getType().getType()+"_translationTimeoutDays");
				//CustomParameter translationTimeoutDays = CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_TRANSLATION_TIMEOUT_DAYS", "");
				if(translationTimeoutDays!=null) {
					lastTimerDuration = "PT"+translationTimeoutDays+"M";
				} else {
					lastTimerDuration = "PT10M";
				}
				properties.put("pv_timerduration", "PT1M");
				properties.put("pv_reminderflag", "off");
				properties.put("pv_lasttimerduration", lastTimerDuration);
			} else {
				properties.put("pv_timerflag", "off");
			}
			properties.put("pv_mailflag", "off");		
			String strTaskId=workflowDetails.getTaskId();
			Task task=processService.findTaskById(strTaskId);
			processService.completeTask(task,properties);
			if(endFlag!=null){
				if(!endFlag.isEmpty()){
					if(endFlag.equals("continue")){					
						ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
						Task newtask=processService.getCurrentTask(processInstance);
						/**** Workflow Detail entry made only if its not the end of workflow ****/
						if(customStatus!=null) {
							WorkflowDetails.create(domain,newtask,currentDeviceTypeWorkflowType,customStatus.getType(),nextUserGroupType, level);
						} else {
							WorkflowDetails.create(domain,newtask,currentDeviceTypeWorkflowType,null,nextUserGroupType, level);
						}					
					}
				}
			}
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			/**** Stale State Exception ****/
			billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,domain.getId());
			if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.TRANSLATION_WORKFLOW)) {
				if(customStatus.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_TRANSLATION)) {
					if(domain.getRemarks()!=null && !domain.getRemarks().isEmpty()) {
						billAmendmentMotion.setRemarksForTranslation(domain.getRemarks());
					}				
					billAmendmentMotion.simpleMerge();
				}
				if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
					if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.TRANSLATOR)
						&& request.getParameter("operation").equals("sendTranslation")) {
						Status translationCompletedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_TRANSLATION_COMPLETED, billAmendmentMotion.getLocale());
						workflowDetails.setCustomStatus(translationCompletedStatus.getType());					
					}
				}
			} else if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {
				if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
					if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.OPINION_ABOUT_BILLAMENDMENTMOTION_DEPARTMENT)
						&& request.getParameter("operation").equals("sendOpinionFromLawAndJD")) {
						Status opinionFromLawAndJDReceivedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_OPINION_FROM_LAWANDJD_RECEIVED, billAmendmentMotion.getLocale());
						workflowDetails.setCustomStatus(opinionFromLawAndJDReceivedStatus.getType());
						if(domain.getOpinionSoughtFromLawAndJD()!=null) {
							if(!domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
								billAmendmentMotion.setDateOfOpinionSoughtFromLawAndJD(new Date());
								billAmendmentMotion.simpleMerge();
							}
						}
					}
				}
			}
			workflowDetails.merge();		
			/**** display message ****/
			model.addAttribute("type","taskcompleted");
			return "workflow/info";
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "** There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
		return "workflow/adjournmentmotion/"+userGroupType;		
	}
	
	private void performAction(final BillAmendmentMotion domain, HttpServletRequest request) throws ELSException {
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();
		/**** Admission ****/
		if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION)){
			performActionOnAdmission(domain, request);
		} 
		/**** Rejection ****/
		else if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECTION)){
			performActionOnRejection(domain, request);
		} 
		/**** Clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING)){
			performActionOnClubbing(domain);
		}
		/**** Clubbing is rejected ****/
		else if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING)){
			performActionOnClubbingRejection(domain);
		}
		/**** Name clubbing is approved ****/
		else if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain);
		}		
		/**** Name clubbing is rejected ****/		
		else if(internalStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_NAMECLUBBING)){
			performActionOnNameClubbingRejection(domain);
		}	
		/**** Clubbing Post Admission is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingPostAdmission(domain);
		}
		/**** Clubbing Post Admission is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)){
			performActionOnClubbingRejectionPostAdmission(domain);
		}
		/**** Unclubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_UNCLUBBING)){
			performActionOnUnclubbing(domain);
		}
		/**** Unclubbing is rejected ****/
		else if(recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_UNCLUBBING)){
			performActionOnUnclubbingRejection(domain);
		}
		/**** Admission Due To Reverse Clubbing is approved ****/
		else if(recommendationStatus.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)){
			performActionOnAdmissionDueToReverseClubbing(domain);
		}
	}
	
	private void performActionOnAdmission(BillAmendmentMotion domain, HttpServletRequest request) {
		Status finalStatus=Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		this.copyOriginalToEmptyRevisedSectionAmendments(domain, request);		
		/**** update clubbed bills accordingly ****/
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			Status status=Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				BillAmendmentMotion billAmendmentMotion=i.getBillAmendmentMotion();
				if(billAmendmentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_CLUBBED)){
					billAmendmentMotion.setStatus(finalStatus);
					billAmendmentMotion.setInternalStatus(finalStatus);
					billAmendmentMotion.setRecommendationStatus(finalStatus);
				}else { //if(!bill.getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {					
					billAmendmentMotion.setInternalStatus(status);
					billAmendmentMotion.setRecommendationStatus(status);
				}			
				billAmendmentMotion.simpleMerge();
			}
		}
	}
	
	private void performActionOnRejection(BillAmendmentMotion domain, HttpServletRequest request) {
		Status finalStatus=Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECTION, domain.getLocale());
		domain.setStatus(finalStatus);		
		this.copyOriginalToEmptyRevisedSectionAmendments(domain, request);
		/**** update clubbed bills accordingly ****/
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			Status status=Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_REJECTION, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				BillAmendmentMotion billAmendmentMotion=i.getBillAmendmentMotion();
				if(billAmendmentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_CLUBBED)){
					billAmendmentMotion.setStatus(finalStatus);
					billAmendmentMotion.setInternalStatus(finalStatus);
					billAmendmentMotion.setRecommendationStatus(finalStatus);
				}else{					
					billAmendmentMotion.setInternalStatus(status);
					billAmendmentMotion.setRecommendationStatus(status);
				}			
				billAmendmentMotion.simpleMerge();
			}
		}
	}
	
private void performActionOnClubbing(BillAmendmentMotion domain) throws ELSException {
		
		BillAmendmentMotion.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnClubbingRejection(BillAmendmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		BillAmendmentMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}

	private void performActionOnNameClubbing(BillAmendmentMotion domain) throws ELSException {
		
		BillAmendmentMotion.updateClubbing(domain);

		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		if(!domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			domain.setActor(null);
			domain.setLocalizedActorName("");
			domain.setWorkflowDetailsId(null);
			domain.setLevel("1");
			domain.setWorkflowStarted("NO");
			domain.setEndFlag(null);		
		}		
	}
	
	private void performActionOnNameClubbingRejection(BillAmendmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
//		domain = ClubbedEntity.unclub(domain);
		BillAmendmentMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);	
	}

	private void performActionOnClubbingPostAdmission(BillAmendmentMotion domain) throws ELSException {
		
		BillAmendmentMotion.updateClubbing(domain);
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnClubbingRejectionPostAdmission(BillAmendmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		BillAmendmentMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);	
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnclubbing(BillAmendmentMotion domain) throws ELSException {
		/**** remove clubbing (status is changed accordingly in unclub method itself) ****/
		BillAmendmentMotion.unclub(domain, domain.getLocale());
		
		// Hack (07May2014): Commenting the following line results in 
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnUnclubbingRejection(BillAmendmentMotion domain) throws ELSException {
		/** Back to clubbed state as it was before sending for unclubbing **/
		domain.setInternalStatus(domain.getParent().getInternalStatus());
		domain.setRecommendationStatus(domain.getParent().getInternalStatus());
		domain.simpleMerge();
		
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
	}
	
	private void performActionOnAdmissionDueToReverseClubbing(BillAmendmentMotion domain) throws ELSException {
		Status admitStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION, domain.getLocale());
		Workflow processWorkflow = Workflow.findByStatus(admitStatus, domain.getLocale());
		UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, domain.getLocale());
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);
		WorkflowDetails.startProcessAtGivenLevel(domain, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, domain.getLocale());
	}
	
	private void populateInternalStatus(ModelMap model,BillAmendmentMotion domain,String workflowType,String locale) {
		List<Status> internalStatuses=new ArrayList<Status>();
		DeviceType deviceType=domain.getType();
		Status internaStatus=domain.getInternalStatus();
		HouseType houseTypeForWorkflow = domain.getHouseType();
		String usergroupType=(String) model.get("usergroupType");
		/**** Final Approving Authority(Final Status) ****/
		CustomParameter finalApprovingAuthority=null;
		CustomParameter finalApprovingAuthorityStatus=null;
		if(workflowType.equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
			finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_NAMECLUBBING_FINAL_AUTHORITY", "");
			finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_NAMECLUBBING_FINAL_"+usergroupType.toUpperCase(),"");
		} else {
			finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
			finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
		}			
		CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter internalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILLAMENDMENTMOTION_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILLAMENDMENTMOTION_PUT_UP_OPTIONS_BY_DEFAULT","");
		if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
			if(finalApprovingAuthorityStatus!=null){
				try {
					internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}/**** BILLAMENDMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
		else if(deviceTypeInternalStatusUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}/**** BILLAMENDMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
		else if(deviceTypeHouseTypeUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		/**** BILLAMENDMENTMOTION_PUT_UP_OPTIONS_+INTERNALSTATUS_TYPE+USERGROUP(Pre Final Status)****/
		else if(internalStatusUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(internalStatusUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**** BILLAMENDMENTMOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
		else if(deviceTypeUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(defaultCustomParameter!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(defaultCustomParameter.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		/**** Internal Status****/
		model.addAttribute("internalStatuses",internalStatuses);		
	}
	
	private boolean populateSectionAmendments(ModelMap model,
			BillAmendmentMotion domain, Session selectedSession,
			DeviceType deviceType) {
		Bill amendedBill = domain.getAmendedBill();		
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
	
	private void copyOriginalToEmptyRevisedSectionAmendments(BillAmendmentMotion domain, HttpServletRequest request) {
		
		if(domain.getRevisedSectionAmendments()==null || domain.getRevisedSectionAmendments().isEmpty()) {
			domain.setRevisedSectionAmendments(domain.getSectionAmendments());
		} else {
			String languagesAllowedByAmendingBill = domain.getAmendedBillLanguages();
			for(String languageAllowedInSession: languagesAllowedByAmendingBill.split("#")) {
				String revisedSectionAmendmentIdInThisLanguage = request.getParameter("revised_sectionAmendment_id_"+languageAllowedInSession);
				if(revisedSectionAmendmentIdInThisLanguage==null || revisedSectionAmendmentIdInThisLanguage.isEmpty()) {
					String originalSectionAmendmentIdInThisLanguage = request.getParameter("sectionAmendment_id_"+languageAllowedInSession);
					if(originalSectionAmendmentIdInThisLanguage!=null && !originalSectionAmendmentIdInThisLanguage.isEmpty()) {
						SectionAmendment sectionAmendment = SectionAmendment.findById(SectionAmendment.class, Long.parseLong(originalSectionAmendmentIdInThisLanguage));
						if(sectionAmendment!=null) {
							domain.getRevisedSectionAmendments().add(sectionAmendment);
						}
					}				
				}
			}
		}
	}
}
