package org.mkcl.els.controller.wf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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
import org.mkcl.els.controller.bis.BillController;
import org.mkcl.els.domain.Act;
import org.mkcl.els.domain.BillKind;
import org.mkcl.els.domain.BillType;
import org.mkcl.els.domain.ClarificationNeededFrom;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.LapsedEntity;
import org.mkcl.els.domain.LayingLetter;
import org.mkcl.els.domain.LayingLetterDraft;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Ordinance;
import org.mkcl.els.domain.PrintRequisition;
import org.mkcl.els.domain.PrintRequisitionParameter;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.TextDraft;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
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
@RequestMapping("/workflow/bill")
public class BillWorkflowController extends BaseController {
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
	@SuppressWarnings("unused")
	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/**** Date ****/

		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT", "");
		if(this.getUserLocale().equals(new Locale("mr","IN")))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
					dateFormat, true));
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(),this.getUserLocale());
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
					dateFormat, true));
		}
		/**** Member ****/
		binder.registerCustomEditor(Member.class, new BaseEditor(
				new Member()));		
		/**** Status ****/
		binder.registerCustomEditor(Status.class, new BaseEditor(
				new Status()));
		/**** House Type ****/
		binder.registerCustomEditor(HouseType.class, new BaseEditor(
				new HouseType()));
		/**** Session ****/
		binder.registerCustomEditor(Session.class, new BaseEditor(
				new Session()));
		/**** Device Type ****/
		binder.registerCustomEditor(DeviceType.class, new BaseEditor(
				new DeviceType()));
		/**** Question Dates ****/
		binder.registerCustomEditor(QuestionDates.class, new BaseEditor(
				new QuestionDates()));
		/**** Clarification Needed from ****/
		binder.registerCustomEditor(ClarificationNeededFrom.class, new BaseEditor(
				new ClarificationNeededFrom()));
		/**** Group ****/
		binder.registerCustomEditor(Group.class, new BaseEditor(
				new Group()));
		/**** Ministry ****/
		binder.registerCustomEditor(Ministry.class, new BaseEditor(
				new Ministry()));
		/**** Department ****/
		binder.registerCustomEditor(Department.class, new BaseEditor(
				new Department()));
		/**** Sub Department ****/
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(
				new SubDepartment()));
		/**** Supporting Member ****/
		//		binder.registerCustomEditor(List.class, "SupportingMember",
		//				new CustomCollectionEditor(List.class) {
		//			@Override
		//			protected Object convertElement(
		//					final Object element) {
		//				String id = null;
		//
		//				if (element instanceof String) {
		//					id = (String) element;
		//				}
		//				return id != null ? SupportingMember
		//						.findById(SupportingMember.class,
		//								Long.valueOf(id))
		//								: null;
		//			}
		//		});

		/**** Clubbed Entity ****/
		//		binder.registerCustomEditor(List.class, "ClubbedEntity",
		//				new CustomCollectionEditor(List.class) {
		//			@Override
		//			protected Object convertElement(
		//					final Object element) {
		//				String id = null;
		//
		//				if (element instanceof String) {
		//					id = (String) element;
		//				}
		//				return id != null ? ClubbedEntity
		//						.findById(ClubbedEntity.class,
		//								Long.valueOf(id))
		//								: null;
		//			}
		//		});

		/**** Referenced Entity ****/
		//		binder.registerCustomEditor(List.class, "ReferencedEntity",
		//				new CustomCollectionEditor(List.class) {
		//			@Override
		//			protected Object convertElement(
		//					final Object element) {
		//				String id = null;
		//
		//				if (element instanceof String) {
		//					id = (String) element;
		//				}
		//				return id != null ? ReferencedEntity
		//						.findById(ReferencedEntity.class,
		//								Long.valueOf(id))
		//								: null;
		//			}
		//		});
		//----------21012013--------------------------
		binder.registerCustomEditor(BillType.class, new BaseEditor(new BillType()));
		binder.registerCustomEditor(BillKind.class, new BaseEditor(new BillKind()));
	}
	
	@RequestMapping(value="supportingmember",method=RequestMethod.GET)
	public String initSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		/**** Bill ****/
		String billId=workflowDetails.getDeviceId();
		model.addAttribute("bill",billId);
		Bill bill=Bill.findById(Bill.class,Long.parseLong(billId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers=bill.getSupportingMembers();
		Member member=Member.findMember(this.getCurrentUser().getFirstName(),
				this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
				this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					String selectedStatus = request.getParameter("status");
					i.setApprovedTitles(bill.getTitles());
					model.addAttribute("currentSupportingMember", i.getMember()
							.getId());
					model.addAttribute("domain", i);
					if (i.getDecisionStatus() != null) {
						model.addAttribute("decisionStatus", i
								.getDecisionStatus().getId());
						model.addAttribute("formattedDecisionStatus", i
								.getDecisionStatus().getName());
					}
					CustomParameter customParameter = CustomParameter
							.findByName(CustomParameter.class,
									"SERVER_DATETIMEFORMAT", "");
					if (customParameter != null) {
						SimpleDateFormat format = new SimpleDateFormat(
								customParameter.getValue());
						model.addAttribute("requestReceivedOnDate",
								format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, bill,supportingMembers,locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}


	private void populateSupportingMember(final ModelMap model,final Bill bill, final List<SupportingMember> supportingMembers,final String locale){
		/**** Bill Device Type ****/
		DeviceType deviceType=bill.getType();
		if(deviceType!=null){
			model.addAttribute("deviceType", deviceType.getName());
			model.addAttribute("deviceTypeId", deviceType.getId());
			model.addAttribute("selectedDeviceTypeForBill", deviceType.getType());
		}
		/**** Bill Type ****/		
		BillType billType=bill.getBillType();
		if(billType!=null){
			model.addAttribute("billType", billType.getId());
			model.addAttribute("billTypeName", billType.getName());			
		}
		/**** Bill Kind ****/
		BillKind billKind=bill.getBillKind();
		if(billKind!=null){
			model.addAttribute("billKind", billKind.getId());
			model.addAttribute("billKindName", billKind.getName());			
		}		
		/**** Session Year and Session Type ****/
		Session session=bill.getSession();
		if(session!=null){
			model.addAttribute("sessionId", session.getId());
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName",bill.getHouseType().getName());
		model.addAttribute("houseType",bill.getHouseType().getType());
		/**** Introducing House Type in case of Government Bill ****/
		if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)) {
			model.addAttribute("introducingHouseType",bill.getIntroducingHouseType().getId());
			model.addAttribute("introducingHouseTypeName",bill.getIntroducingHouseType().getName());
		}
		/**** Referred Act for Amendment Bill ****/
		if(bill.getBillType()!=null) {
			if(bill.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)) {
				Act referredAct = bill.getReferredAct();
				//TODO: for testing
				referredAct = Act.findById(Act.class, new Long(1));
				if(referredAct!=null) {
					model.addAttribute("referredAct", referredAct.getId());
					model.addAttribute("referredActNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredAct.getNumber()));
					model.addAttribute("referredActYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredAct.getYear()));
				}
			}
		}
		/**** Referred Ordinance for Replacement Bill ****/
		if(bill.getBillType()!=null) {
			if(bill.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)) {
				Ordinance referredOrdinance = bill.getReferredOrdinance();				
				if(referredOrdinance!=null) {
					model.addAttribute("referredOrdinance", referredOrdinance.getId());
					model.addAttribute("referredOrdinanceNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredOrdinance.getNumber()));
					model.addAttribute("referredOrdinanceYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredOrdinance.getYear()));
				}
			}
		}
		/**** titles, content drafts, 'statement of object and reason' drafts, memorandum drafts ****/
		String defaultBillLanguage = bill.getSession().getParameter(deviceType.getType()+"_defaultTitleLanguage");
		model.addAttribute("defaultBillLanguage", defaultBillLanguage);
		boolean isSuccessful = populateAllTypesOfDrafts(model, bill, bill.getSession(), deviceType);
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
		model.addAttribute("primaryMemberName",bill.getPrimaryMember().getFullnameLastNameFirst());		
	}

	@RequestMapping(value="supportingmember",method=RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final SupportingMember domain) {
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
		//-------------------set approved drafts for title,content, SOR & memorandums----------------//
		String billId = request.getParameter("bill");
		if(billId!=null && !billId.isEmpty()) {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));		
			String languagesAllowedInSession = bill.getSession().getParameter(bill.getType().getType() + "_languagesAllowed");
			/**** add titles in domain ****/
			List<TextDraft> titles = new ArrayList<TextDraft>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
				String titleTextInThisLanguage = request.getParameter("title_text_"+languageAllowedInSession);
				String titleShortTextInThisLanguage = request.getParameter("title_shortText_"+languageAllowedInSession);
				if(titleTextInThisLanguage!=null && !titleTextInThisLanguage.isEmpty()) {
					TextDraft title = new TextDraft();				
					title.setText(titleTextInThisLanguage);	
					title.setShortText(titleShortTextInThisLanguage);	
					Language thisLanguage;
					String titleLanguageId = request.getParameter("title_language_id_"+languageAllowedInSession);
					if(titleLanguageId!=null && !titleLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(titleLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, locale.toString());
					}					
					title.setLanguage(thisLanguage);					
					title.setLocale(domain.getLocale());
					titles.add(title);
				}
			}
			domain.setApprovedTitles(titles);
			/**** add content drafts in domain ****/
			List<TextDraft> contentDrafts = new ArrayList<TextDraft>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
				String contentDraftTextInThisLanguage = request.getParameter("contentDraft_text_"+languageAllowedInSession);
				String contentDraftFileInThisLanguage = request.getParameter("contentDraft-file-"+languageAllowedInSession);
				if((contentDraftTextInThisLanguage!=null && !contentDraftTextInThisLanguage.isEmpty())
						|| (contentDraftFileInThisLanguage!=null && !contentDraftFileInThisLanguage.isEmpty())) {
					TextDraft contentDraft = new TextDraft();				
					contentDraft.setText(contentDraftTextInThisLanguage);	
					contentDraft.setFile(contentDraftFileInThisLanguage);	
					Language thisLanguage;
					String contentDraftLanguageId = request.getParameter("contentDraft_language_id_"+languageAllowedInSession);
					if(contentDraftLanguageId!=null && !contentDraftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(contentDraftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, locale.toString());
					}					
					contentDraft.setLanguage(thisLanguage);					
					contentDraft.setLocale(domain.getLocale());
					contentDrafts.add(contentDraft);
				}
			}
			domain.setApprovedContentDrafts(contentDrafts);
			/**** add 'statement of object and reason drafts' drafts in domain ****/
			List<TextDraft> statementOfObjectAndReasonDrafts = new ArrayList<TextDraft>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
				String statementOfObjectAndReasonDraftTextInThisLanguage = request.getParameter("statementOfObjectAndReasonDraft_text_"+languageAllowedInSession);
				if(statementOfObjectAndReasonDraftTextInThisLanguage!=null && !statementOfObjectAndReasonDraftTextInThisLanguage.isEmpty()) {
					TextDraft statementOfObjectAndReasonDraft = new TextDraft();				
					statementOfObjectAndReasonDraft.setText(statementOfObjectAndReasonDraftTextInThisLanguage);				
					Language thisLanguage;
					String statementOfObjectAndReasonDraftLanguageId = request.getParameter("statementOfObjectAndReasonDraft_language_id_"+languageAllowedInSession);
					if(statementOfObjectAndReasonDraftLanguageId!=null && !statementOfObjectAndReasonDraftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(statementOfObjectAndReasonDraftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, locale.toString());
					}					
					statementOfObjectAndReasonDraft.setLanguage(thisLanguage);					
					statementOfObjectAndReasonDraft.setLocale(domain.getLocale());
					statementOfObjectAndReasonDrafts.add(statementOfObjectAndReasonDraft);
				}
			}
			domain.setApprovedStatementOfObjectAndReasonDrafts(statementOfObjectAndReasonDrafts);
			/**** add 'financial memorandum' drafts in domain ****/
			List<TextDraft> financialMemorandumDrafts = new ArrayList<TextDraft>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
				String financialMemorandumDraftTextInThisLanguage = request.getParameter("financialMemorandumDraft_text_"+languageAllowedInSession);
				if(financialMemorandumDraftTextInThisLanguage!=null && !financialMemorandumDraftTextInThisLanguage.isEmpty()) {
					TextDraft financialMemorandumDraft = new TextDraft();				
					financialMemorandumDraft.setText(financialMemorandumDraftTextInThisLanguage);					
					Language thisLanguage;
					String financialMemorandumDraftLanguageId = request.getParameter("financialMemorandumDraft_language_id_"+languageAllowedInSession);
					if(financialMemorandumDraftLanguageId!=null && !financialMemorandumDraftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(financialMemorandumDraftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, locale.toString());
					}					
					financialMemorandumDraft.setLanguage(thisLanguage);					
					financialMemorandumDraft.setLocale(domain.getLocale());
					financialMemorandumDrafts.add(financialMemorandumDraft);
				}
			}
			domain.setApprovedFinancialMemorandumDrafts(financialMemorandumDrafts);
			/**** add 'statutory memorandum' drafts in domain ****/
			List<TextDraft> statutoryMemorandumDrafts = new ArrayList<TextDraft>();
			for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
				String statutoryMemorandumDraftTextInThisLanguage = request.getParameter("statutoryMemorandumDraft_text_"+languageAllowedInSession);
				if(statutoryMemorandumDraftTextInThisLanguage!=null && !statutoryMemorandumDraftTextInThisLanguage.isEmpty()) {
					TextDraft statutoryMemorandumDraft = new TextDraft();				
					statutoryMemorandumDraft.setText(statutoryMemorandumDraftTextInThisLanguage);					
					Language thisLanguage;
					String statutoryMemorandumDraftLanguageId = request.getParameter("statutoryMemorandumDraft_language_id_"+languageAllowedInSession);
					if(statutoryMemorandumDraftLanguageId!=null && !statutoryMemorandumDraftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(statutoryMemorandumDraftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, locale.toString());
					}					
					statutoryMemorandumDraft.setLanguage(thisLanguage);					
					statutoryMemorandumDraft.setLocale(domain.getLocale());
					statutoryMemorandumDrafts.add(statutoryMemorandumDraft);
				}
			}
			domain.setApprovedStatutoryMemorandumDrafts(statutoryMemorandumDrafts);
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
	
	@RequestMapping(value="transmitpresscopies",method=RequestMethod.GET)
	public String initTransmissionOfPressCopies(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		if(workflowDetails!=null) {
			model.addAttribute("usergroup", workflowDetails.getAssigneeUserGroupId());
			model.addAttribute("usergroupType", workflowDetails.getAssigneeUserGroupType());
			/**** Current Print Requisition for Endorsement ****/
			if(workflowDetails.getPrintRequisitionId()!=null) {
				PrintRequisition printRequisition = PrintRequisition.findById(PrintRequisition.class, Long.parseLong(workflowDetails.getPrintRequisitionId()));
				if(printRequisition!=null) {
					model.addAttribute("printRequisitionId", printRequisition.getId());
					/**** House Type ****/
					String houseType = printRequisition.getHouseType();
					if(houseType!=null) {
						HouseType selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, printRequisition.getLocale());
						model.addAttribute("selectedHouseType", selectedHouseType);
					}
					/**** Bill ****/
					String selectedBillId=printRequisition.getDeviceId();
					Bill selectedBill=Bill.findById(Bill.class,Long.parseLong(selectedBillId));
					if(selectedBill!=null) {
						/**** Bill Year ****/
						Integer selectedBillYear = Bill.findYear(selectedBill);
						if(selectedBillYear!=null) {
							model.addAttribute("formattedSelectedYear", FormaterUtil.formatNumberNoGrouping(selectedBillYear, locale.toString()));
							model.addAttribute("selectedYear", selectedBillYear);
						}
						/**** Bill Number ****/
						if(selectedBill.getNumber()!=null) {
							model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
							model.addAttribute("selectedBillId", selectedBill.getId());
						}								
					}				
					/**** Status ****/
					String selectedStatusType = printRequisition.getStatus();
					Status selectedStatus = Status.findByType(selectedStatusType, printRequisition.getLocale());
					model.addAttribute("selectedStatus", selectedStatus);
					/**** House Round ****/
					String selectedHouseRoundStr = printRequisition.getHouseRound();
					if(selectedHouseRoundStr!=null) {
						int selectedHouseRound = Integer.parseInt(selectedHouseRoundStr);
						model.addAttribute("formattedSelectedHouseRound", FormaterUtil.formatNumberNoGrouping(selectedHouseRound, locale.toString()));
						model.addAttribute("selectedHouseRound", selectedHouseRound);
					}
					/**** Endorsement Copies ****/
					if(workflowDetails.getWorkflowType().equals(ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW)
							||workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW)) {
						if(printRequisition.getEndorsementCopyEnglish()!=null) {
							if(!printRequisition.getEndorsementCopyEnglish().isEmpty()) {
								model.addAttribute("endorsementCopyEnglish", printRequisition.getEndorsementCopyEnglish());
								model.addAttribute("endorsementCopiesReceived", "yes");
							}
						}
						if(printRequisition.getEndorsementCopyMarathi()!=null) {
							if(!printRequisition.getEndorsementCopyMarathi().isEmpty()) {
								model.addAttribute("endorsementCopyMarathi", printRequisition.getEndorsementCopyMarathi());
								if(!model.containsAttribute("endorsementCopiesReceived")) {
									model.addAttribute("endorsementCopiesReceived", "yes");
								}
							}
						}
						if(printRequisition.getEndorsementCopyHindi()!=null) {
							if(!printRequisition.getEndorsementCopyHindi().isEmpty()) {
								model.addAttribute("endorsementCopyHindi", printRequisition.getEndorsementCopyHindi());
								if(!model.containsAttribute("endorsementCopiesReceived")) {
									model.addAttribute("endorsementCopiesReceived", "yes");
								}
							}
						}
					}
					/**** Press Copies ****/
					if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW)) {
						if(printRequisition.getPressCopyEnglish()!=null) {
							if(!printRequisition.getPressCopyEnglish().isEmpty()) {
								model.addAttribute("pressCopyEnglish", printRequisition.getPressCopyEnglish());
								model.addAttribute("pressCopiesReceived", "yes");
							}
						}
						if(printRequisition.getPressCopyMarathi()!=null) {
							if(!printRequisition.getPressCopyMarathi().isEmpty()) {
								model.addAttribute("pressCopyMarathi", printRequisition.getPressCopyMarathi());
								if(!model.containsAttribute("pressCopiesReceived")) {
									model.addAttribute("pressCopiesReceived", "yes");
								}
							}
						}
						if(printRequisition.getPressCopyHindi()!=null) {
							if(!printRequisition.getPressCopyHindi().isEmpty()) {
								model.addAttribute("pressCopyHindi", printRequisition.getPressCopyHindi());
								if(!model.containsAttribute("pressCopiesReceived")) {
									model.addAttribute("pressCopiesReceived", "yes");
								}
							}
						}
					}
					/**** Date Of Hard Copy Received ****/
					model.addAttribute("dateOfHardCopyReceived", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
					/**** Acknowledgement Decision Status ****/
					Status approveStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.TRANSMISSION_APPROVED, locale.toString());
					Status rejectStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.TRANSMISSION_REJECTED, locale.toString());
					List<Status> acknowledgementDecisionStatuses=new ArrayList<Status>();
					acknowledgementDecisionStatuses.add(approveStatus);
					acknowledgementDecisionStatuses.add(rejectStatus);
					model.addAttribute("acknowledgementDecisionStatuses",acknowledgementDecisionStatuses);
					/**** Existing Values for Completed Endorsement ****/
					if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
						model.addAttribute("isHardCopyReceived",workflowDetails.getIsHardCopyReceived());
						Date dateOfHardCopyReceived = FormaterUtil.formatStringToDate(workflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
						model.addAttribute("dateOfHardCopyReceived", FormaterUtil.formatDateToString(dateOfHardCopyReceived, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
						Status acknowledgementDecisionStatus = Status.findByType(workflowDetails.getAcknowledgementDecision(), locale.toString());
						model.addAttribute("formattedAcknowledgementDecision",acknowledgementDecisionStatus.getName());
					}
				}
			} else {
				
			}
		} else {
			
		}		
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
		model.addAttribute("workflowType",workflowDetails.getWorkflowType());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}
	
	@Transactional
	@RequestMapping(value="transmitpresscopies",method=RequestMethod.PUT)
	public String updateTransmissionOfPressCopies(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** update workflow details ****/		
		String strWorkflowdetails=request.getParameter("workflowDetailsId");
		if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
			workflowDetails.setIsHardCopyReceived(request.getParameter("isHardCopyReceived"));
			workflowDetails.setDateOfHardCopyReceived(request.getParameter("dateOfHardCopyReceived"));
			workflowDetails.setAcknowledgementDecision(request.getParameter("acknowledgementDecision"));
			Bill bill = Bill.findById(Bill.class, Long.parseLong(workflowDetails.getDeviceId()));
			PrintRequisition printRequisition = PrintRequisition.findById(PrintRequisition.class, Long.parseLong(workflowDetails.getPrintRequisitionId()));
				
			String endflag="";	
			String level="";
			Map<String,String> properties=new HashMap<String, String>();
			/**** Next user and usergroup ****/
			Status expectedStatus = Status.findByType(workflowDetails.getWorkflowSubType(), bill.getLocale());
			HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", printRequisition.getHouseType(),printRequisition.getLocale());
			int currentLevel = Integer.parseInt(workflowDetails.getAssigneeLevel());					
			String strCurrentUserGroup=workflowDetails.getAssigneeUserGroupId();
			if(expectedStatus!=null && strCurrentUserGroup!=null) {
				UserGroup currentUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strCurrentUserGroup));
				String finalAuthority = "";
				if(workflowDetails.getWorkflowType().equals(ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW)) {
					finalAuthority = "BILL_ENDORSEMENT_FINAL_AUTHORITY";
				} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW)) {
					finalAuthority = "BILL_TRANSMITENDORSEMENTCOPIES_FINAL_AUTHORITY";
				} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW)) {
					finalAuthority = "BILL_TRANSMITPRESSCOPIES_FINAL_AUTHORITY";
				}
				CustomParameter finalAuthorityParameter = CustomParameter.findByName(CustomParameter.class, finalAuthority, "");
				if(finalAuthorityParameter.getValue().contains(workflowDetails.getAssigneeUserGroupType())) {
					endflag="end";
				} else {
					endflag="continue";
				}
				properties.put("pv_endflag",endflag);	
				properties.put("pv_deviceId",String.valueOf(bill.getId()));
				properties.put("pv_deviceTypeId",String.valueOf(bill.getType().getId()));				
				if(endflag!=null){
					if(!endflag.isEmpty()){
						if(endflag.equals("continue")){
							List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(bill,houseTypeForWorkflow,false,expectedStatus,currentUserGroup,currentLevel,bill.getLocale());
							if(eligibleActors!=null && !eligibleActors.isEmpty()) {
								String nextuser=eligibleActors.get(0).getId();	
								String nextUserGroupType="";
								if(nextuser!=null){						
									if(!nextuser.isEmpty()){
										String[] temp=nextuser.split("#");
										properties.put("pv_user",temp[0]);
										nextUserGroupType=temp[1];
										level=temp[2];
										String localizedActorName=temp[3]+"("+temp[4]+")";
									}
								}	
								/**** complete the task ****/		 
								String strTaskId=workflowDetails.getTaskId();
								Task task=processService.findTaskById(strTaskId);
								processService.completeTask(task, properties);
								model.addAttribute("task",strTaskId);
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails.create(bill,houseTypeForWorkflow,false,printRequisition,newtask,workflowDetails.getWorkflowType(),nextUserGroupType,level);
							}																	
						} else if(endflag.equals("end")) {
							/**** complete the task ****/		 
							String strTaskId=workflowDetails.getTaskId();
							Task task=processService.findTaskById(strTaskId);
							processService.completeTask(task, properties);
							model.addAttribute("task",strTaskId);
						}
					}
				}								
			}
			
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();			
		}
		/**** display message ****/
		model.addAttribute("type","taskcompleted");
		return "workflow/info";
	}
	
	@RequestMapping(value="layletter",method=RequestMethod.GET)
	public String initLayLetter(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails=(Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
		if(workflowDetails==null) {
			logger.error("**** workflow details is not found with request attribute 'workflowdetails'. ****");
			model.addAttribute("errorcode", "REQUEST_ATTRIBUTE_WORKFLOWDETAILS_INVALID");
			return "workflow/bill/error";
		}
		model.addAttribute("usergroup", workflowDetails.getAssigneeUserGroupId());
		model.addAttribute("usergroupType", workflowDetails.getAssigneeUserGroupType());		
		/**** Current Laying Letter & Related Draft ****/
		LayingLetterDraft layingLetterDraft = LayingLetterDraft.findByFieldName(LayingLetterDraft.class, "workflowDetailsId", String.valueOf(workflowDetails.getId()), locale.toString());
		if(layingLetterDraft==null) {
			logger.error("**** no laying letter draft found for this workflow task. ****");
			model.addAttribute("errorcode", "NOLAYINGLETTERDRAFTFOUND");
			return "workflow/bill/error";
		}
		if(layingLetterDraft.getLayingLetterId()==null) {
			logger.error("**** laying letter id is set to null in draft. ****");
			model.addAttribute("errorcode", "NOLAYINGLETTERFOUND");
			return "workflow/bill/error";
		}
		if(layingLetterDraft.getLayingLetterId().isEmpty()) {
			logger.error("**** laying letter id is set to empty in draft. ****");
			model.addAttribute("errorcode", "NOLAYINGLETTERFOUND");
			return "workflow/bill/error";
		}		
		LayingLetter layingLetter = LayingLetter.findById(LayingLetter.class, Long.parseLong(layingLetterDraft.getLayingLetterId()));
		if(layingLetter==null) {
			logger.error("**** no laying letter found for this workflow task. ****");
			model.addAttribute("errorcode", "NOLAYINGLETTERFOUND");
			return "workflow/bill/error";
		}
		/**** House Type ****/
		String houseType = layingLetter.getHouseType();
		if(houseType!=null) {
			HouseType selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, layingLetter.getLocale());
			model.addAttribute("selectedHouseType", selectedHouseType);
		}
		/**** Bill ****/
		String selectedBillId=layingLetter.getDeviceId();
		Bill selectedBill=Bill.findById(Bill.class,Long.parseLong(selectedBillId));
		if(selectedBill!=null) {
			/**** Bill Year ****/
			Integer selectedBillYear = Bill.findYear(selectedBill);
			if(selectedBillYear!=null) {
				model.addAttribute("formattedSelectedYear", FormaterUtil.formatNumberNoGrouping(selectedBillYear, locale.toString()));
				model.addAttribute("selectedYear", selectedBillYear);
			}
			/**** Bill Number ****/
			if(selectedBill.getNumber()!=null) {
				model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
				model.addAttribute("selectedBillId", selectedBill.getId());
			}								
		}				
		/**** Status ****/
		String selectedStatusType = layingLetterDraft.getStatus();
		Status selectedStatus = Status.findByType(selectedStatusType, layingLetter.getLocale());
		model.addAttribute("selectedStatus", selectedStatus);
		/**** House Round ****/
		String selectedHouseRoundStr = layingLetter.getHouseRound();
		if(selectedHouseRoundStr!=null) {
			int selectedHouseRound = Integer.parseInt(selectedHouseRoundStr);
			model.addAttribute("formattedSelectedHouseRound", FormaterUtil.formatNumberNoGrouping(selectedHouseRound, locale.toString()));
			model.addAttribute("selectedHouseRound", selectedHouseRound);
		}
		model.addAttribute("domain", layingLetter);		
		Status status = Status.findByType(layingLetterDraft.getStatus(), layingLetter.getLocale());						
		model.addAttribute("status", status);
		if(layingLetterDraft.getLayingDate()!=null) {
			model.addAttribute("layingDate", FormaterUtil.formatDateToString(layingLetter.getLayingDate(), ApplicationConstants.SERVER_DATEFORMAT, layingLetter.getLocale()));
		} else {
			model.addAttribute("layingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, layingLetter.getLocale()));
		}						
		if(layingLetterDraft.getLetter()!=null) {
			model.addAttribute("letter", layingLetter.getLetter());							
		}
		/**** Acknowledgement Decision Status ****/
		Status approveStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.LAYINGLETTER_APPROVED, locale.toString());
		Status rejectStatus=Status.findByFieldName(Status.class,"type",ApplicationConstants.LAYINGLETTER_REJECTED, locale.toString());
		List<Status> acknowledgementDecisionStatuses=new ArrayList<Status>(); 
		acknowledgementDecisionStatuses.add(approveStatus);
		acknowledgementDecisionStatuses.add(rejectStatus);
		model.addAttribute("acknowledgementDecisionStatuses",acknowledgementDecisionStatuses);
		if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
			Status acknowledgementDecisionStatus = Status.findByType(workflowDetails.getAcknowledgementDecision(), workflowDetails.getLocale());
			model.addAttribute("formattedAcknowledgementDecision",acknowledgementDecisionStatus.getName());
		}
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task",workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId",workflowDetails.getId());
		model.addAttribute("workflowType",workflowDetails.getWorkflowType());
		model.addAttribute("status",workflowDetails.getStatus());

		return workflowDetails.getForm();
	}
	
	@Transactional
	@RequestMapping(value="layletter",method=RequestMethod.PUT)
	public String updateLayingOfLetter(final ModelMap model,
			final HttpServletRequest request, final Locale locale,
			@Valid @ModelAttribute("domain") LayingLetter layingLetter) {
			/**** update workflow details ****/		
		String strWorkflowdetails=request.getParameter("workflowDetailsId");
		if(strWorkflowdetails!=null&&!strWorkflowdetails.isEmpty()){
			WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
			workflowDetails.setAcknowledgementDecision(request.getParameter("acknowledgementDecision"));
			Bill bill = Bill.findById(Bill.class, Long.parseLong(workflowDetails.getDeviceId()));
			String setlayingDate = request.getParameter("setlayingDate");
				if(setlayingDate!=null) {
					layingLetter.setLayingDate(FormaterUtil.formatStringToDate(setlayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
				}
			String endflag="";	
			String level="";
			Map<String,String> properties=new HashMap<String, String>();
			/**** Next user and usergroup ****/
			Status expectedStatus = Status.findByType(workflowDetails.getWorkflowSubType(), bill.getLocale());
			HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", layingLetter.getHouseType(),layingLetter.getLocale());
			int currentLevel = Integer.parseInt(workflowDetails.getAssigneeLevel());					
			String strCurrentUserGroup=workflowDetails.getAssigneeUserGroupId();
			if(expectedStatus!=null && strCurrentUserGroup!=null) {
				UserGroup currentUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strCurrentUserGroup));
				CustomParameter finalAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "BILL_LAYLETTER_FINAL_AUTHORITY"+"_"+houseTypeForWorkflow.getType().toUpperCase(), "");
				if(finalAuthorityParameter.getValue().contains(workflowDetails.getAssigneeUserGroupType())) {
					endflag="end";
				} else {
					endflag="continue";
				}
				properties.put("pv_endflag",endflag);	
				properties.put("pv_deviceId",String.valueOf(bill.getId()));
				properties.put("pv_deviceTypeId",String.valueOf(bill.getType().getId()));				
				if(endflag!=null){
					if(!endflag.isEmpty()){
						if(endflag.equals("continue")){
							List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(bill,houseTypeForWorkflow,false,expectedStatus,currentUserGroup,currentLevel,bill.getLocale());
							if(eligibleActors!=null && !eligibleActors.isEmpty()) {
								String nextuser=eligibleActors.get(0).getId();	
								String nextUserGroupType="";
								if(nextuser!=null){						
									if(!nextuser.isEmpty()){
										String[] temp=nextuser.split("#");
										properties.put("pv_user",temp[0]);
										nextUserGroupType=temp[1];
										level=temp[2];
										String localizedActorName=temp[3]+"("+temp[4]+")";
									}
								}	
								/**** complete the task ****/		 
								String strTaskId=workflowDetails.getTaskId();
								Task task=processService.findTaskById(strTaskId);
								processService.completeTask(task, properties);
								model.addAttribute("task",strTaskId);
								ProcessInstance processInstance = processService.findProcessInstanceById(task.getProcessInstanceId());
								Task newtask=processService.getCurrentTask(processInstance);
								/**** Workflow Detail entry made only if its not the end of workflow ****/
								WorkflowDetails nextWorkflowDetails =WorkflowDetails.create(bill,houseTypeForWorkflow,false,null,newtask,workflowDetails.getWorkflowType(),nextUserGroupType,level);
								layingLetter.setWorkflowDetailsId(String.valueOf(nextWorkflowDetails.getId()));
							}																	
						} else if(endflag.equals("end")) {
							/**** complete the task ****/		 
							String strTaskId=workflowDetails.getTaskId();
							Task task=processService.findTaskById(strTaskId);
							processService.completeTask(task, properties);
							model.addAttribute("task",strTaskId);							
							layingLetter.setStatus(workflowDetails.getAcknowledgementDecision());
							layingLetter.setWorkflowDetailsId(null);
						}
					}
				}								
			}
			
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();
		}		
		layingLetter.setEditedOn(new Date());
		layingLetter.setEditedBy(this.getCurrentUser().getActualUsername());	
		String usergroupTypeForLayingLetter = request.getParameter("currentusergroupType");
		if(usergroupTypeForLayingLetter!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",usergroupTypeForLayingLetter, layingLetter.getLocale());
			layingLetter.setEditedAs(userGroupType.getType());
		}
		layingLetter.merge();
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
		Bill domain=Bill.findById(Bill.class,Long.parseLong(workflowDetails.getDeviceId()));

		/**** Populate Model ****/
		if((workflowDetails.getWorkflowType().equals(ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW))
				&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.PRESS)){
			populateModelForPress(domain,model,request,workflowDetails);
		} else {
			populateModel(domain,model,request,workflowDetails);
		}				
		return workflowDetails.getForm();
	}
	
	private void populateModelForPress(final Bill domain, final ModelMap model,
			final HttpServletRequest request, final WorkflowDetails workflowDetails) {
		/**** end flag ****/
		if(workflowDetails.getStatus().equals("PENDING")) {
			model.addAttribute("endflag", "continue");
		} else if(workflowDetails.getStatus().equals("COMPLETED")) {
			model.addAttribute("endflag", "end");
		}
		/**** Print Requisition ****/
		if(workflowDetails.getPrintRequisitionId()!=null) {
			PrintRequisition printRequisition = PrintRequisition.findById(PrintRequisition.class, Long.parseLong(workflowDetails.getPrintRequisitionId()));
			populatePrintRequisition(printRequisition, domain, model, request, workflowDetails);			
			model.addAttribute("domain",domain);
		}		
	}

	private void populatePrintRequisition(final PrintRequisition printRequisition, final Bill domain, final ModelMap model, final HttpServletRequest request, final WorkflowDetails workflowDetails) {
		/**** Bill Device Type ****/
		DeviceType deviceType = domain.getType();
		if(deviceType==null) {
			logger.error("devicetype is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "devicetype_null");			
			return;
		}
		model.addAttribute("formattedDeviceTypeForBill", deviceType.getName());

		/**** Bill Number ****/
		if(domain.getNumber()!=null){
			model.addAttribute("formattedNumber",FormaterUtil.formatNumberNoGrouping(domain.getNumber(), domain.getLocale()));
		}
		model.addAttribute("requisitionFor", printRequisition.getRequisitionFor());
		Status status = Status.findByType(printRequisition.getStatus(), printRequisition.getLocale());
		model.addAttribute("status", status);		
		if(printRequisition.getHouseRound()!=null){
			if(!printRequisition.getHouseRound().isEmpty()) {
				model.addAttribute("houseRound", printRequisition.getHouseRound());
				model.addAttribute("formattedHouseRound", FormaterUtil.formatNumberNoGrouping(Integer.parseInt(printRequisition.getHouseRound()), printRequisition.getLocale()));
			}			
		}		
		Map<String, String> fields = new HashMap<String, String>();
		if(printRequisition!=null) {
			fields = printRequisition.getFields();
			if(fields==null) {
				fields = new HashMap<String, String>();
			}
		}
		List<MasterVO> printRequisitionParameterVOs = new ArrayList<MasterVO>();
		List<PrintRequisitionParameter> printRequisitionParameters = PrintRequisitionParameter.findAllByFieldName(PrintRequisitionParameter.class, "requisitionFor", printRequisition.getRequisitionFor(), "parameterOrder", ApplicationConstants.ASC, "");
		for(PrintRequisitionParameter printRequisitionParameter: printRequisitionParameters) {
			MasterVO printRequisitionParameterVO = new MasterVO();
			printRequisitionParameterVO.setName(printRequisitionParameter.getParameterName());
			if(fields.containsKey(printRequisitionParameter.getParameterName())) {
				printRequisitionParameterVO.setValue(fields.get(printRequisitionParameter.getParameterName()));
			}				
			printRequisitionParameterVO.setOrder(printRequisitionParameter.getParameterOrder());
			printRequisitionParameterVOs.add(printRequisitionParameterVO);
		}
		model.addAttribute("printRequisitionParameterVOs", printRequisitionParameterVOs);
		CustomParameter optionalFieldsForDocketParameter = CustomParameter.findByName(CustomParameter.class, "BILL_OPTIONAL_FIELDS_FOR_DOCKET", "");
		if(optionalFieldsForDocketParameter!=null) {
			if(optionalFieldsForDocketParameter.getValue()!=null) {
				if(!optionalFieldsForDocketParameter.getValue().isEmpty()) {
					List<MasterVO> optionalFieldsForDocket = new ArrayList<MasterVO>();
					List<String> existingOptionalFieldsForDocket = new ArrayList<String>();
					if(printRequisition!=null) {
						if(printRequisition.getOptionalFieldsForDocket()!=null) {
							if(!printRequisition.getOptionalFieldsForDocket().isEmpty()) {								
								existingOptionalFieldsForDocket = Arrays.asList(printRequisition.getOptionalFieldsForDocket().split("#"));								
							}
						}
					}						
					for(String optionalFieldForDocketParameter: optionalFieldsForDocketParameter.getValue().split("#")) {
						MasterVO optionalFieldForDocket = new MasterVO();
						optionalFieldForDocket.setName(optionalFieldForDocketParameter);
						optionalFieldForDocket.setIsSelected(false);
						for(String existingOptionalFieldForDocket: existingOptionalFieldsForDocket) {
							if(optionalFieldForDocket.getName().equals(existingOptionalFieldForDocket)) {
								optionalFieldForDocket.setIsSelected(true);
								break;
							}
						}
						optionalFieldsForDocket.add(optionalFieldForDocket);
					}
					model.addAttribute("optionalFieldsForDocket", optionalFieldsForDocket);
				}
			}
		}
		if(printRequisition!=null) {
			if(printRequisition.getDocketReportEnglish()!=null) {
				if(!printRequisition.getDocketReportEnglish().isEmpty()) {
					model.addAttribute("docketReportEnglish", printRequisition.getDocketReportEnglish());
				}
			}
			if(printRequisition.getDocketReportMarathi()!=null) {
				if(!printRequisition.getDocketReportMarathi().isEmpty()) {
					model.addAttribute("docketReportMarathi", printRequisition.getDocketReportMarathi());
				}
			}
			if(printRequisition.getDocketReportHindi()!=null) {
				if(!printRequisition.getDocketReportHindi().isEmpty()) {
					model.addAttribute("docketReportHindi", printRequisition.getDocketReportHindi());
				}
			}
			if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.PRESS)) {
				model.addAttribute("isFileRemovable", false);			
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
				if(printRequisition.getEndorsementCopyEnglish()!=null) {
					if(!printRequisition.getEndorsementCopyEnglish().isEmpty()) {
						model.addAttribute("endorsementCopyEnglish", printRequisition.getEndorsementCopyEnglish());
					}
				}
				if(printRequisition.getEndorsementCopyMarathi()!=null) {
					if(!printRequisition.getEndorsementCopyMarathi().isEmpty()) {
						model.addAttribute("endorsementCopyMarathi", printRequisition.getEndorsementCopyMarathi());
					}
				}
				if(printRequisition.getEndorsementCopyHindi()!=null) {
					if(!printRequisition.getEndorsementCopyHindi().isEmpty()) {
						model.addAttribute("endorsementCopyHindi", printRequisition.getEndorsementCopyHindi());
					}
				}
				if(workflowDetails.getStatus().equals("COMPLETED")) {
					model.addAttribute("isPressCopyRemovable", false);
					model.addAttribute("isEndorsementCopyRemovable", false);
				}
			}
		}
	}

	private void populateModel(final Bill domain, final ModelMap model,
			final HttpServletRequest request,final WorkflowDetails workflowDetails) {
//		/**** clear remarks ****/
//		domain.setRemarks("");
		/**** remarks ****/	
		String currentRemarks = Bill.findLatestRemarksOfActor(domain.getId(), workflowDetails.getAssigneeUserGroupName(), this.getCurrentUser().getActualUsername(), domain.getLocale());
		model.addAttribute("currentRemarks", currentRemarks);
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
		/**** UserGroup and UserGroup Type ****/
		String usergroup = workflowDetails.getAssigneeUserGroupId();
		model.addAttribute("usergroup",usergroup);
		String usergroupType = workflowDetails.getAssigneeUserGroupType();
		model.addAttribute("usergroupType",usergroupType);		
		/**** titles, content drafts, 'statement of object and reason' drafts, memorandum drafts, annexures ****/
		String defaultBillLanguage = selectedSession.getParameter(deviceType.getType()+"_defaultTitleLanguage");
		model.addAttribute("defaultBillLanguage", defaultBillLanguage);
		boolean isSuccessful = populateAllTypesOfDrafts(model, domain, selectedSession, deviceType);
		if(!isSuccessful) {
			return;
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
			model.addAttribute("formattedMinistry",ministry.getDropdownDisplayName());
			List<SubDepartment> assignedSubDepartments = 
					MemberMinister.findAssignedSubDepartments(ministry, selectedSession.getEndDate(), locale);
			model.addAttribute("subDepartments", assignedSubDepartments);
			SubDepartment subDepartment=domain.getSubDepartment();
			if(subDepartment!=null){
				model.addAttribute("subDepartmentSelected",subDepartment.getId());
			}									
		} else {
			Session ministrySession = selectedSession;
			if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_BILL)) {
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
					model.addAttribute("formattedMinistry",ministry.getDropdownDisplayName());
					List<SubDepartment> assignedSubDepartments = 
							MemberMinister.findAssignedSubDepartments(ministry, selectedSession.getStartDate(), locale);
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
		
		/**** Referred Ordinance for Replacement Bill ****/
		if(domain.getBillType()!=null) {
			if(domain.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)) {
				Ordinance referredOrdinance = domain.getReferredOrdinance();
				if(referredOrdinance!=null) {
					model.addAttribute("referredOrdinance", referredOrdinance.getId());
					model.addAttribute("referredOrdinanceNumber", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredOrdinance.getNumber()));
					model.addAttribute("referredOrdinanceYear", FormaterUtil.getNumberFormatterNoGrouping(locale).format(referredOrdinance.getYear()));
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
		model.addAttribute("formattedInternalStatus", internalStatus.getName());
		/**** list of put up options available ****/			
		if(!workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				&& !workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)
				&& !workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW)
				&& !workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW)) {
			populateInternalStatus(model,domain,workflowDetails.getWorkflowType(),locale);
		} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				&& !workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.TRANSLATOR)) {
			List<Status> translationStatuses=new ArrayList<Status>();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseTypeForWorkflow = Bill.findHouseTypeForWorkflow(domain);
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_TRANSLATION_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_TRANSLATION_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_TRANSLATION_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_TRANSLATION_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILL_TRANSLATION_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					try {
						translationStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}/**** BILL_TRANSLATION_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				try {
					translationStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/**** BILL_TRANSLATION_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				try {
					translationStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			/**** BILL_TRANSLATION_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
				&& !workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.OPINION_ABOUT_BILL_DEPARTMENT)) {
			List<Status> opinionFromLawAndJDStatuses=new ArrayList<Status>();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseTypeForWorkflow = Bill.findHouseTypeForWorkflow(domain);
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+houseTypeForWorkflow.getType().toUpperCase()+"_OPINION_FROM_LAWANDJD_FINAL_AUTHORITY", "");
			if(finalApprovingAuthority==null) {
				finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_OPINION_FROM_LAWANDJD_FINAL_AUTHORITY", "");
			}
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_OPINION_FROM_LAWANDJD_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_OPINION_FROM_LAWANDJD_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_OPINION_FROM_LAWANDJD_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_OPINION_FROM_LAWANDJD_WORKFLOW_FINAL_AUTHORITY","");
				if(finalApprovingAuthorityStatus!=null){
					try {
						opinionFromLawAndJDStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}/**** BILL_OPINION_FROM_LAWANDJD_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				try {
					opinionFromLawAndJDStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/**** BILL_OPINION_FROM_LAWANDJD_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				try {
					opinionFromLawAndJDStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			/**** BILL_OPINION_FROM_LAWANDJD_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
		} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW)
				&& !workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT)) {
			List<Status> recommendationFromGovernorStatuses=new ArrayList<Status>();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseTypeForWorkflow = Bill.findHouseTypeForWorkflow(domain);
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_RECOMMENDATION_FROM_GOVERNOR_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					try {
						recommendationFromGovernorStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}/**** BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				try {
					recommendationFromGovernorStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/**** BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				try {
					recommendationFromGovernorStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			/**** BILL_RECOMMENDATION_FROM_GOVERNOR_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				try {
					recommendationFromGovernorStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses",recommendationFromGovernorStatuses);
		} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW)
				&& !workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT)) {
			List<Status> recommendationFromPresidentStatuses=new ArrayList<Status>();
			Status internaStatus=domain.getInternalStatus();
			HouseType houseTypeForWorkflow = Bill.findHouseTypeForWorkflow(domain);
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_RECOMMENDATION_FROM_PRESIDENT_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
			if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
				CustomParameter finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_"+usergroupType.toUpperCase(),"");
				if(finalApprovingAuthorityStatus!=null){
					try {
						recommendationFromPresidentStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}/**** BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
			else if(deviceTypeInternalStatusUsergroup!=null){
				try {
					recommendationFromPresidentStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/**** BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
			else if(deviceTypeHouseTypeUsergroup!=null){
				try {
					recommendationFromPresidentStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			/**** BILL_RECOMMENDATION_FROM_PRESIDENT_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
			else if(deviceTypeUsergroup!=null){
				try {
					recommendationFromPresidentStatuses=Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
			/**** Internal Status****/
			model.addAttribute("internalStatuses",recommendationFromPresidentStatuses);
		}								
		if(recommendationStatus==null) {
			logger.error("recommendation status is not set for this bill having id="+domain.getId()+".");
			model.addAttribute("errorcode", "recommendationStatus_null");
			return;
		}
		model.addAttribute("recommendationStatus",recommendationStatus.getId());
		model.addAttribute("recommendationStatusType",recommendationStatus.getType());
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
		/**** Populating Put up options and Actors ****/							
		if(usergroupType!=null&&!usergroupType.isEmpty()){				
			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(usergroup));
			String currentStatusType=null;	
			List<Reference> actors=new ArrayList<Reference>();
			CustomParameter finalApprovingAuthority=null;
			String recommendedAction = null;
			Status statusRecommended = null;
			if(workflowDetails.getWorkflowType().equals(ApplicationConstants.APPROVAL_WORKFLOW)) {
				if(domain.getInternalStatus()!=null) {					
					actors = WorkflowConfig.findBillActorsVO(domain, domain.getInternalStatus(), userGroup, 1, locale);
					statusRecommended = domain.getInternalStatus();
					currentStatusType = domain.getInternalStatus().getType();
					if(currentStatusType!=null) {
						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
								statusRecommended = Status.findByType("bill_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);
								if(statusRecommended!=null) {
									domain.setInternalStatus(statusRecommended);
									domain.setRecommendationStatus(statusRecommended);
								}
							}
						}						
					}
				}								
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
				if(domain.getInternalStatus()!=null) {
					actors = WorkflowConfig.findBillActorsVO(domain, domain.getInternalStatus(), userGroup, 1, locale);
					statusRecommended = domain.getInternalStatus();
					currentStatusType = domain.getInternalStatus().getType();
					if(currentStatusType!=null) {
						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
						if(currentStatusType.contains("reject")) {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
									+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
						} else {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
						}
						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
								statusRecommended = Status.findByType("bill_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);
								if(statusRecommended!=null) {
									domain.setInternalStatus(statusRecommended);
									domain.setRecommendationStatus(statusRecommended);
								}
							}
						}						
					}
				}				
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)) {				
				if(translationStatus!=null) {
					if(!(usergroupType.equals(ApplicationConstants.TRANSLATOR) && workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED))) {
						actors = WorkflowConfig.findBillActorsVO(domain, translationStatus, userGroup, 1, locale);
					}					
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
								statusRecommended = Status.findByType("bill_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);								
							}
						}						
					}
				}					
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {				
				if(opinionFromLawAndJDStatus!=null) {
					if(!(usergroupType.equals(ApplicationConstants.OPINION_ABOUT_BILL_DEPARTMENT) && workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED))) {
						actors = WorkflowConfig.findBillActorsVO(domain, opinionFromLawAndJDStatus, userGroup, 1, locale);
					}					
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
								statusRecommended = Status.findByType("bill_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);								
							}
						}						
					}
				}
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW)) {				
				if(recommendationFromGovernorStatus!=null) {
					if(!(usergroupType.equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT) && workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED))) {
						actors = WorkflowConfig.findBillActorsVO(domain, recommendationFromGovernorStatus, userGroup, 1, locale);
					}					
					statusRecommended = recommendationFromGovernorStatus;
					currentStatusType = recommendationFromGovernorStatus.getType();
					if(currentStatusType!=null) {
						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_RECOMMENDATION_FROM_GOVERNOR_FINAL_AUTHORITY", "");
						if(currentStatusType.contains("reject")) {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
									+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
						} else {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
						}
						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
								statusRecommended = Status.findByType("bill_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);								
							}
						}						
					}
				}
			} else if(workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW)) {				
				if(recommendationFromPresidentStatus!=null) {
					if(!(usergroupType.equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT) && workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED))) {
						actors = WorkflowConfig.findBillActorsVO(domain, recommendationFromPresidentStatus, userGroup, 1, locale);
					}
					statusRecommended = recommendationFromPresidentStatus;
					currentStatusType = recommendationFromPresidentStatus.getType();
					if(currentStatusType!=null) {
						finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_RECOMMENDATION_FROM_PRESIDENT_FINAL_AUTHORITY", "");
						if(currentStatusType.contains("reject")) {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-2]
									+ "_" + currentStatusType.split("_")[currentStatusType.split("_").length-1] ;
						} else {
							recommendedAction = currentStatusType.split("_")[currentStatusType.split("_").length-1];
						}
						if(finalApprovingAuthority!=null&&recommendedAction!=null) {
							if(finalApprovingAuthority.getValue().contains(usergroupType)) {							
								statusRecommended = Status.findByType("bill_final_"+recommendedAction, locale);
								model.addAttribute("hideActorsFlag",true);								
							}
						}						
					}
				}
			}			
			if(statusRecommended!=null) {
				model.addAttribute("internalStatusSelected",statusRecommended.getId());
			}				
			model.addAttribute("actors",actors);
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
		/**** Print Requisition Parameters ****/
//		if(domain.getStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION) 
//				&& domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION) 
//				//&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)
//				&& (usergroupType.equals(ApplicationConstants.ASSISTANT) || usergroupType.equals(ApplicationConstants.SECTION_OFFICER))) {
//			populatePrintRequisition(ApplicationConstants.BILL_PRESS_COPY, ApplicationConstants.BILL_FINAL_ADMISSION, domain, model, request, workflowDetails);
//		}
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
	
	@Transactional
	@RequestMapping(method=RequestMethod.PUT)
	public String updateMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale,@Valid @ModelAttribute("domain") final Bill domain,final BindingResult result) {
		Bill bill = null;
		
		/**** Workflowdetails ****/
		String strWorkflowdetails=(String) request.getParameter("workflowdetails");
		WorkflowDetails workflowDetails=WorkflowDetails.findById(WorkflowDetails.class,Long.parseLong(strWorkflowdetails));
		if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
			/**** display message ****/
			model.addAttribute("type","taskalreadycompleted");
			return "workflow/info";
		} else if(workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_TIMEOUT)) {
			/**** display message ****/
			model.addAttribute("type","taskalreadytimedout");
			return "workflow/info";
		}
		/** Custom Status for Auxillary Workflows **/
		Status customStatus = null;		
		if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW) 
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW) 
				|| workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW)) {
			if(request.getParameter("customStatus")!=null && !request.getParameter("customStatus").isEmpty()) {
				customStatus = Status.findById(Status.class, Long.parseLong(request.getParameter("customStatus")));
				workflowDetails.setCustomStatus(customStatus.getType());
			}			
		}
		/**** handle workflows for print requisitions ****/
		if((workflowDetails.getWorkflowType().equals(ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW))
				&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.PRESS)){					
			Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
			printRequisitionIdentifiers.put("deviceId", String.valueOf(domain.getId()));
			String requisitionFor = request.getParameter("requisitionFor");
			String status = request.getParameter("status");
			String houseRound = request.getParameter("houseRound");
			printRequisitionIdentifiers.put("deviceId", String.valueOf(domain.getId()));
			printRequisitionIdentifiers.put("requisitionFor", requisitionFor);		
			printRequisitionIdentifiers.put("status", status);
			printRequisitionIdentifiers.put("houseRound", houseRound);
			PrintRequisition printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, domain.getLocale());
			if(printRequisition!=null) {
				/**** Press Copies ****/
				if(request.getParameter("pressCopyEnglish")!=null) {
						printRequisition.setPressCopyEnglish(request.getParameter("pressCopyEnglish"));
				}
				if(request.getParameter("pressCopyMarathi")!=null) {
					printRequisition.setPressCopyMarathi(request.getParameter("pressCopyMarathi"));
				}
				if(request.getParameter("pressCopyHindi")!=null) {
					printRequisition.setPressCopyHindi(request.getParameter("pressCopyHindi"));
				}
				/**** Endorsement Copies ****/
				if(request.getParameter("endorsementCopyEnglish")!=null) {
						printRequisition.setEndorsementCopyEnglish(request.getParameter("endorsementCopyEnglish"));
				}
				if(request.getParameter("endorsementCopyMarathi")!=null) {
					printRequisition.setEndorsementCopyMarathi(request.getParameter("endorsementCopyMarathi"));
				}
				if(request.getParameter("endorsementCopyHindi")!=null) {
					printRequisition.setEndorsementCopyHindi(request.getParameter("endorsementCopyHindi"));
				}
				printRequisition.merge();
				String operation = request.getParameter("operation");
				if(operation!=null) {
					if(operation.equals("savePressCopy")) {
						model.addAttribute("type","success");
						model.addAttribute("workflowdetails",workflowDetails.getId());
						model.addAttribute("workflowstatus",workflowDetails.getStatus());
						model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
						model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
						/** Stale State Exception **/
						if(bill==null) {
							bill = Bill.findById(Bill.class, domain.getId());
						}
						populateModelForPress(bill, model, request, workflowDetails);
						String userGroupType = workflowDetails.getAssigneeUserGroupType();
						return "workflow/bill/"+userGroupType;
					} else if(operation.equals("sendPressCopy")) {
						String endflag = request.getParameter("endflag");
						if(endflag!=null) {
							if(endflag.equals("end")) {
								Map<String,String> properties=new HashMap<String, String>();
								properties.put("pv_deviceId",String.valueOf(domain.getId()));
//								properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));		
								properties.put("pv_endflag", endflag);
								String strTaskId=workflowDetails.getTaskId();
								Task task=processService.findTaskById(strTaskId);
								processService.completeTask(task,properties);
								workflowDetails.setStatus("COMPLETED");
								workflowDetails.setCompletionTime(new Date());
								/**** display message ****/
								model.addAttribute("type","taskcompleted");
								return "workflow/info";
							}
						}
					}
				}
			}			
		}
		if(workflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.TRANSLATOR)) {
			if(bill==null) {
				bill = Bill.findById(Bill.class, domain.getId());
			}				
			/**** add/update revised titles in domain ****/
			bill.getRevisedTitles().clear();
			List<TextDraft> revisedTitles = BillController.updateDraftsOfGivenType(bill, "revised_title", request);
			for(TextDraft rt: revisedTitles) {
				bill.getRevisedTitles().add(rt);
			}
			/**** add/update revised content drafts in domain ****/
			bill.getRevisedContentDrafts().clear();
			List<TextDraft> revisedContentDrafts = BillController.updateDraftsOfGivenType(bill, "revised_contentDraft", request);
			for(TextDraft rcd: revisedContentDrafts) {
				bill.getRevisedContentDrafts().add(rcd);
			}
			/**** add/update revised 'statement of object and reason drafts' in domain ****/
			bill.getRevisedStatementOfObjectAndReasonDrafts().clear();
			List<TextDraft> revisedStatementOfObjectAndReasonDrafts = BillController.updateDraftsOfGivenType(bill, "revised_statementOfObjectAndReasonDraft", request);		
			for(TextDraft rsor: revisedStatementOfObjectAndReasonDrafts) {
				bill.getRevisedStatementOfObjectAndReasonDrafts().add(rsor);
			}	
			/**** add/update revised financial memorandum drafts in domain ****/
			bill.getRevisedFinancialMemorandumDrafts().clear();
			List<TextDraft> revisedFinancialMemorandumDrafts = BillController.updateDraftsOfGivenType(bill, "revised_financialMemorandumDraft", request);		
			for(TextDraft rfm: revisedFinancialMemorandumDrafts) {
				bill.getRevisedFinancialMemorandumDrafts().add(rfm);
			}	
			/**** add/update revised statutory memorandum drafts in domain ****/
			bill.getRevisedStatutoryMemorandumDrafts().clear();
			List<TextDraft> revisedStatutoryMemorandumDrafts = BillController.updateDraftsOfGivenType(bill, "revised_statutoryMemorandumDraft", request);
			for(TextDraft rsm: revisedStatutoryMemorandumDrafts) {
				bill.getRevisedStatutoryMemorandumDrafts().add(rsm);
			}		
			/**** add/update revised annexures for amending bill in domain ****/
			bill.getRevisedAnnexuresForAmendingBill().clear();
			List<TextDraft> revisedAnnexuresForAmendingBill = BillController.updateDraftsOfGivenType(bill, "revised_annexureForAmendingBill", request);
			for(TextDraft ra: revisedAnnexuresForAmendingBill) {
				bill.getRevisedAnnexuresForAmendingBill().add(ra);
			}
			bill.setEditedOn(new Date());
			bill.setEditedBy(this.getCurrentUser().getActualUsername());
			bill.setEditedAs(workflowDetails.getAssigneeUserGroupName());
			bill.merge();
			if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
				if(request.getParameter("operation").equals("saveTranslation")) {
					model.addAttribute("type","success");
					model.addAttribute("workflowdetails",workflowDetails.getId());
					model.addAttribute("workflowstatus",workflowDetails.getStatus());
					model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
					model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
					populateModel(bill, model, request, workflowDetails);
					String userGroupType = workflowDetails.getAssigneeUserGroupType();
					return "workflow/bill/"+userGroupType;
				} else if(request.getParameter("operation").equals("sendTranslation")) {
					String endflag=request.getParameter("endflag");
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
					properties.put("pv_deviceId",String.valueOf(bill.getId()));
					properties.put("pv_deviceTypeId",String.valueOf(bill.getType().getId()));		
					properties.put("pv_endflag", endflag);
					properties.put("pv_timerflag", "off");
					properties.put("pv_mailflag", "off");		
					String strTaskId=workflowDetails.getTaskId();
					Task task=processService.findTaskById(strTaskId);
					processService.completeTask(task,properties);		
					if(endflag!=null){
						if(!endflag.isEmpty()){
							if(endflag.equals("continue")){					
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
					Status translationCompletedStatus = Status.findByType(ApplicationConstants.BILL_TRANSLATION_COMPLETED, bill.getLocale());
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
			if(bill==null) {
				bill=Bill.findById(Bill.class,domain.getId());
			}			
			members=bill.getSupportingMembers();
		}
		if(strSupportingMembers!=null){
			if(strSupportingMembers.length>0){
				List<SupportingMember> supportingMembers=new ArrayList<SupportingMember>();
				for(String i:strSupportingMembers){
					SupportingMember supportingMember=null;
					Member member=Member.findById(Member.class, Long.parseLong(i));
					/**** If supporting member is already present then do nothing ****/
					for(SupportingMember j:members){
						if(j.getMember().getId()==member.getId()){
							supportingMember=j;
							supportingMembers.add(supportingMember);
							break;
						}
					}					
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
		/***** To retain the referred act when moving through workflow ****/
		if(domain.getBillType() != null){
			if(domain.getBillType().getType().equals(ApplicationConstants.AMENDMENT_BILL)){
				String refAct = request.getParameter("referredAct");
				if(refAct != null && !refAct.isEmpty()){
					Act referredAct = Act.findById(Act.class, Long.parseLong(refAct));
					domain.setReferredAct(referredAct);
				}
			}			
		}
		
		/***** To retain the referred ordinance when moving through workflow ****/
		if(domain.getBillType() != null){
			if(domain.getBillType().getType().equals(ApplicationConstants.ORDINANCE_REPLACEMENT_BILL)){
				String refOrdinance = request.getParameter("referredOrdinance");
				if(refOrdinance != null && !refOrdinance.isEmpty()){
					Ordinance referredOrdinance = Ordinance.findById(Ordinance.class, Long.parseLong(refOrdinance));
					domain.setReferredOrdinance(referredOrdinance);
				}
			}			
		}
		
		/***** To retain the clubbed bills when moving through workflow ****/
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
		/***** To retain the parent bill when moving through workflow ****/
		String strParentBill = request.getParameter("parent");
		if(strParentBill!=null) {
			if(!strParentBill.isEmpty()) {
				Bill parentBill = Bill.findById(Bill.class, Long.parseLong(strParentBill));
				domain.setParent(parentBill);
			}
		}
		/***** To retain the referenced bill when moving through workflow ****/
		if(domain.getType() != null){
			if(domain.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				String refBill = request.getParameter("referencedBill");
				if(refBill != null && !refBill.isEmpty()){
					ReferencedEntity refBillEntity = ReferencedEntity.findById(ReferencedEntity.class, Long.parseLong(refBill));
					domain.setReferencedBill(refBillEntity);
				}
			}			
		}
		/***** To retain the lapsed bill when moving through workflow ****/
		if(domain.getType() != null){			
			String lapsedBill = request.getParameter("lapsedBill");
			if(lapsedBill != null && !lapsedBill.isEmpty()){
				LapsedEntity lapsedEntity = LapsedEntity.findById(LapsedEntity.class, Long.parseLong(lapsedBill));
				domain.setLapsedBill(lapsedEntity);
			}					
		}
		/**** Check For Bill Completeness ****/
		if(domain.getReferencedBill()==null 
        		&& domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_RECOMMEND_REJECTION)) {
        	domain.setIsIncomplete(true);
        } else {
        	domain.setIsIncomplete(false);
        }
		/**** Updating domain ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
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
				} else {
					if(workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW)
							&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT)) {
						domain.setDateOfRecommendationFromGovernor(new Date());
					}
				}
				if(strDateOfRecommendationFromPresident!=null&&!strDateOfRecommendationFromPresident.isEmpty()) {
					domain.setDateOfRecommendationFromPresident(format.parse(strDateOfRecommendationFromPresident));
				} else {
					if(workflowDetails.getWorkflowType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW)
							&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT)) {
						domain.setDateOfRecommendationFromPresident(new Date());
					}
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
		
		/**** retain sections ****/
		if(bill==null) {
			bill=Bill.findById(Bill.class,domain.getId());
		}
		domain.setSections(bill.getSections());
		
		performAction(domain, request);	
		domain.merge();
		
		/**** Complete Task ****/	
		String currentDeviceTypeWorkflowType = workflowDetails.getWorkflowType();
		if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
			if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW) 
					&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.OPINION_ABOUT_BILL_DEPARTMENT)
					&& request.getParameter("operation").equals("saveOpinionFromLawAndJD")) {
				model.addAttribute("type","success");
				model.addAttribute("workflowdetails",workflowDetails.getId());
				model.addAttribute("workflowstatus",workflowDetails.getStatus());
				model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
				model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
				/** Stale State Exception **/
				bill=Bill.findById(Bill.class,domain.getId());
				populateModel(bill, model, request, workflowDetails);
				String userGroupType = workflowDetails.getAssigneeUserGroupType();
				return "workflow/bill/"+userGroupType;
			} else if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW) 
					&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT)
					&& request.getParameter("operation").equals("saveRecommendationFromGovernor")) {
				model.addAttribute("type","success");
				model.addAttribute("workflowdetails",workflowDetails.getId());
				model.addAttribute("workflowstatus",workflowDetails.getStatus());
				model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
				model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
				/** Stale State Exception **/
				bill=Bill.findById(Bill.class,domain.getId());
				populateModel(bill, model, request, workflowDetails);
				String userGroupType = workflowDetails.getAssigneeUserGroupType();
				return "workflow/bill/"+userGroupType;
			} else if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW) 
					&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT)
					&& request.getParameter("operation").equals("saveRecommendationFromPresident")) {
				model.addAttribute("type","success");
				model.addAttribute("workflowdetails",workflowDetails.getId());
				model.addAttribute("workflowstatus",workflowDetails.getStatus());
				model.addAttribute("workflowtype", workflowDetails.getWorkflowType());
				model.addAttribute("workflowsubtype", workflowDetails.getWorkflowSubType());
				/** Stale State Exception **/
				bill=Bill.findById(Bill.class,domain.getId());
				populateModel(bill, model, request, workflowDetails);
				String userGroupType = workflowDetails.getAssigneeUserGroupType();
				return "workflow/bill/"+userGroupType;
			}
		}
		String endflag="";	
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
		endflag=request.getParameter("endflag");		
		properties.put("pv_deviceId",String.valueOf(domain.getId()));
		properties.put("pv_deviceTypeId",String.valueOf(domain.getType().getId()));		
		properties.put("pv_endflag", endflag);
		/**** set timer for translation ****/
		if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.TRANSLATION_WORKFLOW)
				&& customStatus.getType().equals(ApplicationConstants.BILL_FINAL_TRANSLATION)
				&& workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)) {
			properties.put("pv_workflowtype", ApplicationConstants.TRANSLATION_WORKFLOW);
			properties.put("pv_timerflag", "set");
			String lastTimerDuration;
			String translationTimeoutDays = domain.getSession().getParameter(domain.getType().getType()+"_translationTimeoutDays");
			//CustomParameter translationTimeoutDays = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSLATION_TIMEOUT_DAYS", "");
			if(translationTimeoutDays!=null) {
				lastTimerDuration = "PT"+translationTimeoutDays+"M";
			} else {
				lastTimerDuration = "PT10M";
			}
			//domain.getSession().getParameter("resolutions_nonofficial_reminderDayNumberForFactualPosition");
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
		if(endflag!=null){
			if(!endflag.isEmpty()){
				if(endflag.equals("continue")){					
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
		bill=Bill.findById(Bill.class,domain.getId());
		if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.TRANSLATION_WORKFLOW)) {
			if(customStatus.getType().equals(ApplicationConstants.BILL_FINAL_TRANSLATION)) {
				if(domain.getRemarks()!=null && !domain.getRemarks().isEmpty())
				bill.setRemarksForTranslation(domain.getRemarks());
			}
			if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.TRANSLATOR)
					&& request.getParameter("operation").equals("sendTranslation")) {
					Status translationCompletedStatus = Status.findByType(ApplicationConstants.BILL_TRANSLATION_COMPLETED, bill.getLocale());
					workflowDetails.setCustomStatus(translationCompletedStatus.getType());					
				}
			}
		} else if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.OPINION_FROM_LAWANDJD_WORKFLOW)) {
			if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.OPINION_ABOUT_BILL_DEPARTMENT)
					&& request.getParameter("operation").equals("sendOpinionFromLawAndJD")) {
					Status opinionFromLawAndJDReceivedStatus = Status.findByType(ApplicationConstants.BILL_OPINION_FROM_LAWANDJD_RECEIVED, bill.getLocale());
					workflowDetails.setCustomStatus(opinionFromLawAndJDReceivedStatus.getType());
					if(domain.getOpinionSoughtFromLawAndJD()!=null) {
						if(!domain.getOpinionSoughtFromLawAndJD().isEmpty()) {
							bill.setDateOfOpinionSoughtFromLawAndJD(new Date());
						}
					}
				}
			}
		} else if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_WORKFLOW)) {
			if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_GOVERNOR_DEPARTMENT)
					&& request.getParameter("operation").equals("sendRecommendationFromGovernor")) {
					Status recommendationFromGovernorReceivedStatus = Status.findByType(ApplicationConstants.BILL_RECOMMENDATION_FROM_GOVERNOR_RECEIVED, bill.getLocale());
					workflowDetails.setCustomStatus(recommendationFromGovernorReceivedStatus.getType());
					if(domain.getRecommendationFromGovernor()!=null) {
						if(!domain.getRecommendationFromGovernor().isEmpty()) {
							bill.setDateOfRecommendationFromGovernor(new Date());
						}
					}
				}
			}
		} else if(currentDeviceTypeWorkflowType.equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_WORKFLOW)) {
			if(request.getParameter("operation")!=null && !request.getParameter("operation").isEmpty()) {
				if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.RECOMMENDATION_FROM_PRESIDENT_DEPARTMENT)
					&& request.getParameter("operation").equals("sendRecommendationFromPresident")) {
					Status recommendationFromPresidentReceivedStatus = Status.findByType(ApplicationConstants.BILL_RECOMMENDATION_FROM_PRESIDENT_RECEIVED, bill.getLocale());
					workflowDetails.setCustomStatus(recommendationFromPresidentReceivedStatus.getType());
					if(domain.getRecommendationFromPresident()!=null) {
						if(!domain.getRecommendationFromPresident().isEmpty()) {
							bill.setDateOfRecommendationFromPresident(new Date());
						}
					}
				}
			}
		}		
		workflowDetails.merge();	
		if(domain.getStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION) 
				&& domain.getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION) 
				&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_DEPARTMENTINTIMATED)) {
			Status toBeIntroducedStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED, bill.getLocale());
			bill.setInternalStatus(toBeIntroducedStatus);
		}
		bill.simpleMerge();
		/**** display message ****/
		model.addAttribute("type","taskcompleted");
		return "workflow/info";		
	}
	
	private void performAction(Bill domain, HttpServletRequest request) {		
		String internalStatus=domain.getInternalStatus().getType();
		String recommendationStatus=domain.getRecommendationStatus().getType();		
		if(internalStatus.equals(ApplicationConstants.BILL_FINAL_ADMISSION)
				&&recommendationStatus.equals(ApplicationConstants.BILL_FINAL_ADMISSION)){
			performActionOnAdmission(domain, request);
		}
		else if(internalStatus.equals(ApplicationConstants.BILL_FINAL_REJECTION)
				&&recommendationStatus.equals(ApplicationConstants.BILL_PROCESSED_REJECTIONWITHREASON)){
			performActionOnRejection(domain, request);
		} else if(internalStatus.equals(ApplicationConstants.BILL_FINAL_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.BILL_FINAL_NAMECLUBBING)){
			performActionOnNameClubbing(domain, request);
		} else if(internalStatus.equals(ApplicationConstants.BILL_FINAL_REJECT_NAMECLUBBING)&&
				recommendationStatus.equals(ApplicationConstants.BILL_FINAL_REJECT_NAMECLUBBING)){
			performActionOnNameClubbingRejection(domain, request);
		}
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
	
	private void copyOriginalToEmptyRevisedDraftsOfGivenType(Bill domain, String typeOfDraft, HttpServletRequest request) {
		List<TextDraft> revisedDraftsOfGivenTypeInDomain = null;
		if(typeOfDraft.equals("title")) {
			revisedDraftsOfGivenTypeInDomain = domain.getRevisedTitles();							
		} else if(typeOfDraft.equals("contentDraft")) {
			revisedDraftsOfGivenTypeInDomain = domain.getRevisedContentDrafts();							
		} else if(typeOfDraft.equals("statementOfObjectAndReasonDraft")) {
			revisedDraftsOfGivenTypeInDomain = domain.getRevisedStatementOfObjectAndReasonDrafts();
		} else if(typeOfDraft.equals("financialMemorandumDraft")) {
			revisedDraftsOfGivenTypeInDomain = domain.getRevisedFinancialMemorandumDrafts();
		} else if(typeOfDraft.equals("statutoryMemorandumDraft")) {
			revisedDraftsOfGivenTypeInDomain = domain.getRevisedStatutoryMemorandumDrafts();
		} else if(typeOfDraft.equals("annexureForAmendingBill")) {
			revisedDraftsOfGivenTypeInDomain = domain.getRevisedAnnexuresForAmendingBill();
		}
		List<TextDraft> existingRevisedDraftsOfGivenType = new ArrayList<TextDraft>();
		List<TextDraft> revisedDraftsOfGivenType = new ArrayList<TextDraft>();
		String languagesAllowedInSession = domain.getSession().getParameter(domain.getType().getType() + "_languagesAllowed");
		for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
			String reviseDraftTextInThisLanguage = request.getParameter("revised_" + typeOfDraft+"_text_"+languageAllowedInSession);
			String reviseDraftShortTextInThisLanguage = request.getParameter("revised_" + typeOfDraft+"_shortText_"+languageAllowedInSession);
			String typeOfDraftForFileField = typeOfDraft.replaceAll("_", "-");
			String reviseDraftFileInThisLanguage = request.getParameter("revised-" + typeOfDraftForFileField+"-file-"+languageAllowedInSession);
			TextDraft revisedDraftOfGivenType = null;
			if((reviseDraftTextInThisLanguage==null || reviseDraftTextInThisLanguage.isEmpty())
					&& (reviseDraftShortTextInThisLanguage==null || reviseDraftShortTextInThisLanguage.isEmpty())
					&& (reviseDraftFileInThisLanguage==null || reviseDraftFileInThisLanguage.isEmpty())) {
				String originalDraftTextInThisLanguage = request.getParameter(typeOfDraft+"_text_"+languageAllowedInSession);
				String originalDraftShortTextInThisLanguage = request.getParameter(typeOfDraft+"_shortText_"+languageAllowedInSession);
				String originalDraftFileInThisLanguage = request.getParameter(typeOfDraftForFileField+"-file-"+languageAllowedInSession);
				String revisedDraftIdInThisLanguage = request.getParameter("revised_" + typeOfDraft+"_id_"+languageAllowedInSession);
				if(revisedDraftIdInThisLanguage!=null && !revisedDraftIdInThisLanguage.isEmpty()) {
					revisedDraftOfGivenType = TextDraft.findById(TextDraft.class, Long.parseLong(revisedDraftIdInThisLanguage));					
					if(revisedDraftsOfGivenTypeInDomain!=null) {
						if(!revisedDraftsOfGivenTypeInDomain.isEmpty()) {
							for(TextDraft revisedDraftOfGivenTypeInDomain: revisedDraftsOfGivenTypeInDomain) {
								if(revisedDraftOfGivenTypeInDomain.getId().equals(revisedDraftOfGivenType.getId())) {
									existingRevisedDraftsOfGivenType.add(revisedDraftOfGivenType);
									break;
								}
							}									
						}
					}
				} else {
					revisedDraftOfGivenType = new TextDraft();
				}
				revisedDraftOfGivenType.setText(originalDraftTextInThisLanguage);
				revisedDraftOfGivenType.setShortText(originalDraftShortTextInThisLanguage);
				revisedDraftOfGivenType.setFile(originalDraftFileInThisLanguage);
				if(revisedDraftOfGivenType.getLanguage()==null) {
					Language thisLanguage;
					String draftLanguageId = request.getParameter(typeOfDraft+"_language_id_"+languageAllowedInSession);
					if(draftLanguageId!=null && !draftLanguageId.isEmpty()) {
						thisLanguage = Language.findById(Language.class, Long.parseLong(draftLanguageId));
					} else {
						thisLanguage = Language.findByFieldName(Language.class, "type", languageAllowedInSession, domain.getLocale());
					}					
					revisedDraftOfGivenType.setLanguage(thisLanguage);
				}
				revisedDraftOfGivenType.setLocale(domain.getLocale());
				revisedDraftsOfGivenType.add(revisedDraftOfGivenType);								
			}
		}
		if(revisedDraftsOfGivenTypeInDomain!=null && !revisedDraftsOfGivenTypeInDomain.isEmpty()) {
			if(!existingRevisedDraftsOfGivenType.isEmpty()) {
				revisedDraftsOfGivenTypeInDomain.removeAll(existingRevisedDraftsOfGivenType);			
			}
		}		
		revisedDraftsOfGivenTypeInDomain.addAll(revisedDraftsOfGivenType);
		if(typeOfDraft.equals("title")) {			
			domain.setRevisedTitles(revisedDraftsOfGivenTypeInDomain);						
		} else if(typeOfDraft.equals("contentDraft")) {			
			domain.setRevisedContentDrafts(revisedDraftsOfGivenTypeInDomain);						
		} else if(typeOfDraft.equals("statementOfObjectAndReasonDraft")) {
			domain.setRevisedStatementOfObjectAndReasonDrafts(revisedDraftsOfGivenTypeInDomain);
		} else if(typeOfDraft.equals("financialMemorandumDraft")) {
			domain.setRevisedFinancialMemorandumDrafts(revisedDraftsOfGivenTypeInDomain);
		} else if(typeOfDraft.equals("statutoryMemorandumDraft")) {
			domain.setRevisedStatutoryMemorandumDrafts(revisedDraftsOfGivenTypeInDomain);
		} else if(typeOfDraft.equals("annexureForAmendingBill")) {
			domain.setRevisedAnnexuresForAmendingBill(revisedDraftsOfGivenTypeInDomain);
		}					
	}
	
	private void populateInternalStatus(ModelMap model,Bill domain,String workflowType,String locale) {
		List<Status> internalStatuses=new ArrayList<Status>();
		DeviceType deviceType=domain.getType();
		Status internaStatus=domain.getInternalStatus();
		HouseType houseTypeForWorkflow = Bill.findHouseTypeForWorkflow(domain);
		String usergroupType=(String) model.get("usergroupType");
		/**** Final Approving Authority(Final Status) ****/
		CustomParameter finalApprovingAuthority=null;
		CustomParameter finalApprovingAuthorityStatus=null;
		if(workflowType.equals(ApplicationConstants.NAMECLUBBING_WORKFLOW)) {
			finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_NAMECLUBBING_FINAL_AUTHORITY", "");
			finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_NAMECLUBBING_FINAL_"+usergroupType.toUpperCase(),"");
		} else {
			finalApprovingAuthority=CustomParameter.findByName(CustomParameter.class,deviceType.getType().toUpperCase()+"_FINAL_AUTHORITY", "");
			finalApprovingAuthorityStatus=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_"+usergroupType.toUpperCase(),"");
		}			
		CustomParameter deviceTypeInternalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter deviceTypeHouseTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+houseTypeForWorkflow.getType().toUpperCase()+"_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter internalStatusUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_PUT_UP_OPTIONS_"+internaStatus.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter deviceTypeUsergroup=CustomParameter.findByName(CustomParameter.class, "BILL_PUT_UP_OPTIONS_"+deviceType.getType().toUpperCase()+"_"+usergroupType.toUpperCase(), "");
		CustomParameter defaultCustomParameter=CustomParameter.findByName(CustomParameter.class,"BILL_PUT_UP_OPTIONS_BY_DEFAULT","");
		if(finalApprovingAuthority!=null&&finalApprovingAuthority.getValue().contains(usergroupType)){
			if(finalApprovingAuthorityStatus!=null){
				try {
					internalStatuses=Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				} catch (ELSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}/**** BILL_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)****/
		else if(deviceTypeInternalStatusUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}/**** BILL_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(Pre Final Status-House Type Basis)****/
		else if(deviceTypeHouseTypeUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		/**** BILL_PUT_UP_OPTIONS_+INTERNALSTATUS_TYPE+USERGROUP(Pre Final Status)****/
		else if(internalStatusUsergroup!=null){
			try {
				internalStatuses=Status.findStatusContainedIn(internalStatusUsergroup.getValue(), locale);
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**** BILL_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final Status)****/
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
	
	private void performActionOnAdmission(Bill domain, HttpServletRequest request) {
		Status finalStatus=Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setAdmissionDate(new Date());
		domain.setStatusDate(new Date());
		this.copyOriginalToEmptyRevisedDrafts(domain, request);		
		/**** update clubbed bills accordingly ****/
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			Status status=Status.findByType(ApplicationConstants.BILL_PUTUP_NAMECLUBBING, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Bill bill=i.getBill();
				if(bill.getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_CLUBBED)){
					bill.setStatus(finalStatus);
					bill.setInternalStatus(finalStatus);
					bill.setRecommendationStatus(finalStatus);
				}else { //if(!bill.getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {					
					bill.setInternalStatus(status);
					bill.setRecommendationStatus(status);
				}			
				bill.simpleMerge();
			}
		}
	}
	
	private void performActionOnRejection(Bill domain, HttpServletRequest request) {
		Status finalStatus=Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION, domain.getLocale());
		domain.setStatus(finalStatus);
		Status recommendationStatusForFinalRejection = null;
		if(domain.getReferencedBill()!=null) {
			recommendationStatusForFinalRejection = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION_DUETOREFERENCING, domain.getLocale());
    		domain.setRecommendationStatus(recommendationStatusForFinalRejection);
		} else {
			if(domain.getIsIncomplete()!=null) {
				if(domain.getIsIncomplete().equals(true)) {
					recommendationStatusForFinalRejection = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION_DUETOINCOMPLETENESS, domain.getLocale());
		    		domain.setRecommendationStatus(recommendationStatusForFinalRejection);
				} else {
					recommendationStatusForFinalRejection = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION_DUETOFINALAUTHORITYDECISION, domain.getLocale());
		    		domain.setRecommendationStatus(recommendationStatusForFinalRejection);
				}
			} else {
				recommendationStatusForFinalRejection = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION_DUETOFINALAUTHORITYDECISION, domain.getLocale());
	    		domain.setRecommendationStatus(recommendationStatusForFinalRejection);
			}			
		}		
		domain.setRejectionDate(new Date());
		domain.setStatusDate(new Date());
		this.copyOriginalToEmptyRevisedDrafts(domain, request);
		/**** update clubbed bills accordingly ****/
		List<ClubbedEntity> clubbedEntities=domain.getClubbedEntities();
		if(clubbedEntities!=null){
			Status status=Status.findByType(ApplicationConstants.BILL_PUTUP_REJECTION, domain.getLocale());
			for(ClubbedEntity i:clubbedEntities){
				Bill bill=i.getBill();
				if(bill.getInternalStatus().getType().equals(ApplicationConstants.BILL_SYSTEM_CLUBBED)){
					bill.setStatus(finalStatus);
					bill.setInternalStatus(finalStatus);
					//Status rejectionReasonStatus = Status.findByType(ApplicationConstants.BILL_FINAL_REJECTION_DUETOCLUBBING, domain.getLocale());
					bill.setRecommendationStatus(finalStatus);
				}else{					
					bill.setInternalStatus(status);
					bill.setRecommendationStatus(status);
				}			
				bill.simpleMerge();
			}
		}
	}
	
	private void performActionOnNameClubbing(Bill domain, HttpServletRequest request) {
		Status finalStatus=Status.findByType(ApplicationConstants.BILL_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		domain.setStatusDate(new Date());
		domain.setInternalStatus(finalStatus);
//		domain.setRecommendationStatus(finalStatus);
		
//		if(domain.getParent()!=null) {
//			if(domain.getParent().getInternalStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_TOBEINTRODUCED)) {
//				domain.setInternalStatus(domain.getParent().getInternalStatus());
//			} else {
//				domain.setInternalStatus(finalStatus);				
//			}
//			if(domain.getParent().getRecommendationStatus().getType().equals(ApplicationConstants.BILL_PROCESSED_DEPARTMENTINTIMATED)) {
//				domain.setRecommendationStatus(domain.getParent().getRecommendationStatus());
//				//send department intimation here
//			} else {
//				domain.setRecommendationStatus(finalStatus);
//			}
//		}
		this.copyOriginalToEmptyRevisedDrafts(domain, request);		
	}
	
	private void performActionOnNameClubbingRejection(Bill domain, HttpServletRequest request) {
		if(domain.getStatus().equals(ApplicationConstants.BILL_SUBMIT)) {
			Status internalStatusExpected = Status.findByType(ApplicationConstants.BILL_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
			domain.setInternalStatus(internalStatusExpected);
		} else if(domain.getStatus().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
			domain.setInternalStatus(domain.getStatus());
//			domain.setRecommendationStatus(domain.getStatus());
		}
		domain.setParent(null);
		this.copyOriginalToEmptyRevisedDrafts(domain, request);		
	}
	
	private void copyOriginalToEmptyRevisedDrafts(Bill domain, HttpServletRequest request) {
		this.copyOriginalToEmptyRevisedDraftsOfGivenType(domain, "title", request);
		this.copyOriginalToEmptyRevisedDraftsOfGivenType(domain, "contentDraft", request);		
		this.copyOriginalToEmptyRevisedDraftsOfGivenType(domain, "statementOfObjectAndReasonDraft", request);		
		this.copyOriginalToEmptyRevisedDraftsOfGivenType(domain, "financialMemorandumDraft", request);		
		this.copyOriginalToEmptyRevisedDraftsOfGivenType(domain, "statutoryMemorandumDraft", request);
		this.copyOriginalToEmptyRevisedDraftsOfGivenType(domain, "annexureForAmendingBill", request);
	}
}
