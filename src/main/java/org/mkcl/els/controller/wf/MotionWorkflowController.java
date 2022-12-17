package org.mkcl.els.controller.wf;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
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
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BulkApprovalVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Holiday;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.ReferenceLetter;
import org.mkcl.els.domain.ReferenceUnit;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.User;
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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workflow/motion")
public class MotionWorkflowController extends BaseController {

	/** The process service. */
	@Autowired
	private IProcessService processService;

	@InitBinder(value = "domain")
	private void initBinder(final WebDataBinder binder) {
		/**** Date ****/

		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
		if (this.getUserLocale().equals(new Locale("mr", "IN"))) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(), new Locale("hi", "IN"));
			dateFormat.setLenient(true);
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue(), this.getUserLocale());
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
		/**** Ministry ****/
		binder.registerCustomEditor(Ministry.class, new BaseEditor(new Ministry()));
		/**** Department ****/
		binder.registerCustomEditor(Department.class, new BaseEditor(new Department()));
		/**** Sub Department ****/
		binder.registerCustomEditor(SubDepartment.class, new BaseEditor(new SubDepartment()));
	}

	@RequestMapping(value = "supportingmember", method = RequestMethod.GET)
	public String initSupportingMember(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		/**** Workflowdetails ****/
		Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, longWorkflowdetails);
		/**** Motion ****/
		String motionId = workflowDetails.getDeviceId();
		model.addAttribute("motion", motionId);
		Motion motion = Motion.findById(Motion.class, Long.parseLong(motionId));
		/**** Current Supporting Member ****/
		List<SupportingMember> supportingMembers = motion.getSupportingMembers();

		Member member = Member.findMember(this.getCurrentUser().getFirstName(), this.getCurrentUser().getMiddleName(),
				this.getCurrentUser().getLastName(), this.getCurrentUser().getBirthDate(), locale.toString());
		if (member != null) {
			for (SupportingMember i : supportingMembers) {
				if (i.getMember().getId() == member.getId()) {
					i.setApprovedText(motion.getDetails());
					i.setApprovedSubject(motion.getSubject());
					model.addAttribute("currentSupportingMember", i.getMember().getId());
					model.addAttribute("domain", i);
					if (i.getDecisionStatus() != null) {
						model.addAttribute("decisionStatus", i.getDecisionStatus().getId());
						model.addAttribute("formattedDecisionStatus", i.getDecisionStatus().getName());
					}
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,
							"SERVER_DATETIMEFORMAT", "");
					if (customParameter != null) {
						SimpleDateFormat format = new SimpleDateFormat(customParameter.getValue());
						model.addAttribute("requestReceivedOnDate", format.format(i.getRequestReceivedOn()));
					}
					break;
				}
			}
		}
		/**** Populate Model ****/
		populateSupportingMember(model, motion, supportingMembers, locale.toString());
		/**** Add task and workflowdetails to model ****/
		model.addAttribute("task", workflowDetails.getTaskId());
		model.addAttribute("workflowDetailsId", workflowDetails.getId());
		model.addAttribute("status", workflowDetails.getStatus());

		return workflowDetails.getForm();
	}

	private void populateSupportingMember(final ModelMap model, final Motion motion,
			final List<SupportingMember> supportingMembers, final String locale) {
		/**** motion Type ****/
		DeviceType motionType = motion.getType();
		if (motionType != null) {
			model.addAttribute("motionType", motionType.getName());
		}
		/**** Session Year and Session Type ****/
		Session session = motion.getSession();
		if (session != null) {
			model.addAttribute("year", session.getYear());
			model.addAttribute("sessionType", session.getType().getSessionType());
		}
		/**** House Type ****/
		model.addAttribute("houseTypeName", motion.getHouseType().getName());
		model.addAttribute("houseType", motion.getHouseType().getType());
		/**** Supporting Members ****/
		List<Member> members = new ArrayList<Member>();
		if (supportingMembers != null) {
			for (SupportingMember i : supportingMembers) {
				Member selectedMember = i.getMember();
				members.add(selectedMember);
			}
			if (!members.isEmpty()) {
				StringBuffer buffer = new StringBuffer();
				for (Member i : members) {
					buffer.append(i.getFullnameLastNameFirst() + ",");
				}
				buffer.deleteCharAt(buffer.length() - 1);
				model.addAttribute("supportingMembersName", buffer.toString());
			}
		}
		/**** Decision Status ****/
		Status approveStatus = Status.findByFieldName(Status.class, "type",
				ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
		Status rejectStatus = Status.findByFieldName(Status.class, "type",
				ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale);
		List<Status> decisionStatus = new ArrayList<Status>();
		decisionStatus.add(approveStatus);
		decisionStatus.add(rejectStatus);
		model.addAttribute("decisionStatus", decisionStatus);
		/**** Primary Member ****/
		model.addAttribute("primaryMemberName", motion.getPrimaryMember().getFullnameLastNameFirst());

	}

	@Transactional
	@RequestMapping(value = "supportingmember", method = RequestMethod.PUT)
	public String updateSupportingMember(final ModelMap model, final HttpServletRequest request, final Locale locale,
			@Valid @ModelAttribute("domain") final SupportingMember domain) {
		/**** update supporting member */
		String strMember = request.getParameter("currentSupportingMember");
		String requestReceivedOn = request.getParameter("requestReceivedOnDate");
		if (requestReceivedOn != null && !(requestReceivedOn.isEmpty())) {
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT",
					"");
			if (customParameter != null) {
				SimpleDateFormat format = new SimpleDateFormat(customParameter.getValue());
				try {
					domain.setRequestReceivedOn(format.parse(requestReceivedOn));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		Member member = Member.findById(Member.class, Long.parseLong(strMember));
		domain.setMember(member);
		domain.setApprovalDate(new Date());
		domain.merge();
		/**** update workflow details ****/
		String strWorkflowdetails = domain.getWorkflowDetailsId();
		if (strWorkflowdetails != null && !strWorkflowdetails.isEmpty()) {
			WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,
					Long.parseLong(strWorkflowdetails));
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.setInternalStatus(domain.getDecisionStatus().getName());
			workflowDetails.setRecommendationStatus(domain.getDecisionStatus().getName());
			workflowDetails.merge();
			/**** complete the task ****/
			String strTaskId = workflowDetails.getTaskId();
			Task task = processService.findTaskById(strTaskId);
			processService.completeTask(task);
			model.addAttribute("task", strTaskId);
		}
		/**** display message ****/
		model.addAttribute("type", "taskcompleted");
		return "workflow/info";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String initMyTask(final ModelMap model, final HttpServletRequest request, final Locale locale) {

		/**** Add if assistant acts as section officer ****/
		CustomParameter csptAssisatntAsSO = CustomParameter.findByName(CustomParameter.class,
				"MOTION_ASSISTANT_AS_SECTION_OFFICER", "");
		if (csptAssisatntAsSO != null) {
			if (csptAssisatntAsSO.getValue() != null && !csptAssisatntAsSO.getValue().isEmpty()) {
				model.addAttribute("actAsSO", csptAssisatntAsSO.getValue());
			}
		}

		/**** Workflowdetails ****/
		Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
		if (longWorkflowdetails == null) {
			longWorkflowdetails = Long.parseLong(request.getParameter("workflowdetails"));
		}
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, longWorkflowdetails);
		/**** Adding workflowdetails and task to model ****/
		model.addAttribute("workflowdetails", workflowDetails.getId());
		model.addAttribute("workflowstatus", workflowDetails.getStatus());
		Motion domain = Motion.findById(Motion.class, Long.parseLong(workflowDetails.getDeviceId()));
		/**** Populate Model ****/
		populateModel(domain, model, request, workflowDetails);
		/**** Find Latest Remarks ****/
		try {
			findLatestRemarksByUserGroup(domain, model, request, workflowDetails);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workflowDetails.getForm();
	}

	private void populateModel(final Motion domain, final ModelMap model, final HttpServletRequest request,
			final WorkflowDetails workflowDetails) {

		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("locale", new String[] { domain.getLocale() });
		params.put("motionId", new String[] { domain.getId().toString() });
		model.addAttribute("revisions", Query.findReport("MOTION_DRAFT_FROM_VIEW", params));

		List<DeviceType> allDevices = DeviceType.findAll(DeviceType.class, "priority", ApplicationConstants.ASC,
				domain.getLocale());
		model.addAttribute("allDevices", allDevices);

		/**** In case of bulk edit we can update only few parameters ****/
		model.addAttribute("bulkedit", request.getParameter("bulkedit"));
		/**** clear remarks ****/
		domain.setRemarks("");

		/**** Locale ****/
		String locale = domain.getLocale();

		/**** House Type ****/
		HouseType houseType = domain.getHouseType();
		model.addAttribute("formattedHouseType", houseType.getName());
		model.addAttribute("houseType", houseType.getId());

		/**** Session ****/
		Session selectedSession = domain.getSession();
		model.addAttribute("session", selectedSession.getId());

		/**** Session Year ****/
		Integer sessionYear = 0;
		sessionYear = selectedSession.getYear();
		model.addAttribute("formattedSessionYear",
				FormaterUtil.getNumberFormatterNoGrouping(locale).format(sessionYear));
		model.addAttribute("sessionYear", sessionYear);

		/**** Session Type ****/
		SessionType sessionType = selectedSession.getType();
		model.addAttribute("formattedSessionType", sessionType.getSessionType());
		model.addAttribute("sessionType", sessionType.getId());

		/**** Motion Type ****/
		DeviceType motionType = domain.getType();
		model.addAttribute("formattedMotionType", motionType.getName());
		model.addAttribute("motionType", motionType.getId());
		model.addAttribute("selectedMotionType", motionType.getType());

		/**** Primary Member ****/
		String memberNames = null;
		String primaryMemberName = null;
		Member member = domain.getPrimaryMember();
		if (member != null) {
			model.addAttribute("primaryMember", member.getId());
			primaryMemberName = member.getFullname();
			memberNames = primaryMemberName;
			model.addAttribute("formattedPrimaryMember", primaryMemberName);
		}
		/**** Constituency ****/
		Long houseId = selectedSession.getHouse().getId();
		MasterVO constituency = null;
		if (houseType.getType().equals("lowerhouse")) {
			constituency = Member.findConstituencyByAssemblyId(member.getId(), houseId);
			model.addAttribute("constituency", constituency.getName());
		} else if (houseType.getType().equals("upperhouse")) {
			Date currentDate = new Date();
			String date = FormaterUtil.getDateFormatter("en_US").format(currentDate);
			constituency = Member.findConstituencyByCouncilDates(member.getId(), houseId, "DATE", date, date);
			model.addAttribute("constituency", constituency.getName());
		}
		/**** Supporting Members ****/
		List<SupportingMember> selectedSupportingMembers = domain.getSupportingMembers();
		// List<Member> supportingMembers=new ArrayList<Member>();
		if (selectedSupportingMembers != null) {
			if (!selectedSupportingMembers.isEmpty()) {
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for (SupportingMember i : selectedSupportingMembers) {
					/****
					 * All Supporting Members Are Preserved.But the names that
					 * appear in supporting members list will vary.
					 ****/
					Member m = i.getMember();
					if (i.getDecisionStatus() != null && i.getDecisionStatus().getType()
								.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
							if (m.isActiveMemberOn(new Date(), locale)) {
								bufferFirstNamesFirst.append(m.getFullname() + ",");
						}
					}
		 
					if (bufferFirstNamesFirst.length() > 0) {
						bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length() - 1);
					}							 
					model.addAttribute("supportingMembersName", bufferFirstNamesFirst.toString());
					model.addAttribute("supportingMembers", selectedSupportingMembers);
					memberNames = primaryMemberName + "," + bufferFirstNamesFirst.toString();
					model.addAttribute("memberNames", memberNames);
				}
			} else {
				model.addAttribute("memberNames", memberNames);
			}
		} else {
			model.addAttribute("memberNames", memberNames);
		}
		/**** Ministries And Sub Departments ****/
		List<Ministry> ministries = new ArrayList<Ministry>();
		try {
			ministries = Ministry.findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
		model.addAttribute("ministries", ministries);
		Ministry ministry = domain.getMinistry();
		if (ministry != null) {
			model.addAttribute("ministrySelected", ministry.getId());
			/**** Sub Departments ****/
			List<SubDepartment> subDepartments = MemberMinister.findAssignedSubDepartments(ministry,
					selectedSession.getEndDate(), locale);
			model.addAttribute("subDepartments", subDepartments);
			SubDepartment subDepartment = domain.getSubDepartment();
			if (subDepartment != null) {
				model.addAttribute("subDepartmentSelected", subDepartment.getId());
			}
		}
		/****
		 * Submission Date,Creation date,WorkflowStartedOn date,TaskReceivedOn
		 * date
		 ****/
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
		if (dateTimeFormat != null) {
			if (domain.getSubmissionDate() != null) {
				model.addAttribute("submissionDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US")
						.format(domain.getSubmissionDate()));
				model.addAttribute("formattedSubmissionDate", FormaterUtil
						.getDateFormatter(dateTimeFormat.getValue(), locale).format(domain.getSubmissionDate()));
			}
			if (domain.getCreationDate() != null) {
				model.addAttribute("creationDate", FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US")
						.format(domain.getCreationDate()));
			}
			if (domain.getWorkflowStartedOn() != null) {
				model.addAttribute("workflowStartedOnDate", FormaterUtil
						.getDateFormatter(dateTimeFormat.getValue(), "en_US").format(domain.getWorkflowStartedOn()));
			}
			if (domain.getTaskReceivedOn() != null) {
				model.addAttribute("taskReceivedOnDate", FormaterUtil
						.getDateFormatter(dateTimeFormat.getValue(), "en_US").format(domain.getTaskReceivedOn()));
			}
		}
		/**** Number ****/
		if (domain.getNumber() != null) {
			model.addAttribute("formattedNumber",
					FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getNumber()));
		}
		if (domain.getPostBallotNumber() != null) {
			model.addAttribute("formattedPostBallotNumber",
					FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getPostBallotNumber()));
		}
		/**** Created By ****/
		model.addAttribute("createdBy", domain.getCreatedBy());
		model.addAttribute("dataEnteredBy", domain.getDataEnteredBy());

		/**** UserGroup and UserGroup Type ****/
		String usergroupType = workflowDetails.getAssigneeUserGroupType();
		String userGroupId = workflowDetails.getAssigneeUserGroupId();
		model.addAttribute("usergroup", workflowDetails.getAssigneeUserGroupId());
		model.addAttribute("usergroupType", workflowDetails.getAssigneeUserGroupType());

		/******** Set resendRevisedMotionText **********/
		boolean boolResendRevisedMotionText = false;
		if ((workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.ASSISTANT)
				|| workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER))
				&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)
				&& (workflowDetails.getWorkflowSubType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION))) {
			boolResendRevisedMotionText = true;
			if (domain.getAnsweringDate() == null) {
				Date defaultAnsweringDate = Holiday.getNextWorkingDateFrom(new Date(), 1, locale);
				domain.setAnsweringDate(defaultAnsweringDate);
			}
		}

		if (domain.getAnsweringDate() != null) {
			model.addAttribute("answeringDate", domain.getAnsweringDate());
			model.addAttribute("formattedAnsweringDate", FormaterUtil.formatDateToString(domain.getAnsweringDate(),
					ApplicationConstants.SERVER_DATEFORMAT, locale));
		}

		/****** Set Clarification Not Received *********/
		boolean boolClarificationNotReceived = false;
		if (workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.SECTION_OFFICER)
				&& workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)
				&& (workflowDetails.getWorkflowSubType()
						.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						|| workflowDetails.getWorkflowSubType()
								.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
						|| workflowDetails.getWorkflowSubType()
								.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER))) {
			boolClarificationNotReceived = true;
		}

		/**** Status,Internal Status and recommendation Status ****/
		Status status = domain.getStatus();
		Status internalStatus = domain.getInternalStatus();

		if (status != null) {
			model.addAttribute("status", status.getId());
			model.addAttribute("memberStatusType", status.getType());
		}
		if (internalStatus != null) {
			model.addAttribute("internalStatus", internalStatus.getId());
			model.addAttribute("internalStatusType", internalStatus.getType());
			model.addAttribute("formattedInternalStatus", internalStatus.getName());
		}

		if (boolResendRevisedMotionText) {
			Status resendMotionTextStatus = Status
					.findByType(ApplicationConstants.MOTION_PROCESSED_RESENDREVISEDMOTIONTEXT, locale);
			model.addAttribute("resendMotionTextStatus", resendMotionTextStatus.getType());
			domain.setRecommendationStatus(resendMotionTextStatus);
			populateInternalStatus(model, domain.getRecommendationStatus().getType(),
					workflowDetails.getAssigneeUserGroupType(), locale);
		} else if (boolClarificationNotReceived) {
			Status clarificationStatus = Status.findByType(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_REPUTUP,
					locale);
			model.addAttribute("clarificationStatus", clarificationStatus.getType());
			domain.setRecommendationStatus(clarificationStatus);
			populateInternalStatus(model, clarificationStatus.getType(), workflowDetails.getAssigneeUserGroupType(),
					locale);
		} else {
			/**** list of put up options available ****/
			populateInternalStatus(model, domain, usergroupType, locale);

		}

		Status recommendationStatus = domain.getRecommendationStatus();
		if (recommendationStatus != null) {
			model.addAttribute("recommendationStatus", recommendationStatus.getId());
			model.addAttribute("recommendationStatusType", recommendationStatus.getType());
		}

		/**** Referenced Entities are collected in refentities ****/
		List<ReferenceUnit> referencedEntities = domain.getReferencedUnits();
		if (referencedEntities != null && !referencedEntities.isEmpty()) {
			List<ReferenceUnit> refmotionentities = new ArrayList<ReferenceUnit>();
			List<ReferenceUnit> refquestionentities = new ArrayList<ReferenceUnit>();
			List<ReferenceUnit> refresolutionentities = new ArrayList<ReferenceUnit>();
			for (ReferenceUnit re : referencedEntities) {
				if (re.getDeviceType() != null) {
					if (re.getDeviceType().startsWith(ApplicationConstants.DEVICE_MOTIONS)) {
						refmotionentities.add(re);
					} else if (re.getDeviceType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)) {
						refresolutionentities.add(re);
					} else if (re.getDeviceType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)) {
						refquestionentities.add(re);
					}
				}
			}
			model.addAttribute("referencedMotions", refmotionentities);
			model.addAttribute("referencedQuestions", refquestionentities);
			model.addAttribute("referencedResolutions", refresolutionentities);
			model.addAttribute("referencedEntities", referencedEntities);
		}
		/**** Clubbed motions are collected in references ****/
		List<ClubbedEntity> clubbedEntities = Motion.findClubbedEntitiesByPosition(domain);
		if (clubbedEntities != null) {
			List<Reference> references = new ArrayList<Reference>();
			StringBuffer buffer1 = new StringBuffer();
			buffer1.append(memberNames + ",");
			for (ClubbedEntity ce : clubbedEntities) {
				Reference reference = new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getMotion().getNumber()));
				reference.setNumber(String.valueOf(ce.getMotion().getId()));
				references.add(reference);
				String tempPrimary = ce.getMotion().getPrimaryMember().getFullname();
				if (!buffer1.toString().contains(tempPrimary)) {
					buffer1.append(ce.getMotion().getPrimaryMember().getFullname() + ",");
				}
				List<SupportingMember> clubbedSupportingMember = ce.getMotion().getSupportingMembers();
				if (clubbedSupportingMember != null) {
					if (!clubbedSupportingMember.isEmpty()) {
						for (SupportingMember l : clubbedSupportingMember) {
							if (l.getDecisionStatus().getType()
									.equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								Member supportingMember = l.getMember();
								if (supportingMember.isActiveMemberOn(new Date(), locale)) {
									String tempSupporting = supportingMember.getFullname();
									if (!buffer1.toString().contains(tempSupporting)) {
										buffer1.append(tempSupporting + ",");
									}
								}
							}
						}
					}
				}
			}
			if (!buffer1.toString().isEmpty()) {
				buffer1.deleteCharAt(buffer1.length() - 1);
			}
			String allMembersNames = buffer1.toString();
			model.addAttribute("memberNames", allMembersNames);
			if (!references.isEmpty()) {
				model.addAttribute("clubbedEntities", references);
			} else {
				if (domain.getParent() != null) {
					model.addAttribute("formattedParentNumber",
							FormaterUtil.getNumberFormatterNoGrouping(locale).format(domain.getParent().getNumber()));
					model.addAttribute("parent", domain.getParent().getId());
				}
			}
		}

		/**** Populating Put up options and Actors ****/
		if (userGroupId != null && !userGroupId.isEmpty()) {
			UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(userGroupId));
			List<Reference> actors = new ArrayList<Reference>();
			if (userGroup.getUserGroupType().getType().equals("department")
					&& internalStatus.getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
				Status sendback = Status.findByType(ApplicationConstants.MOTION_RECOMMEND_SENDBACK, locale);
				actors = WorkflowConfig.findMotionActorsVO(domain, sendback, userGroup,
						Integer.parseInt(domain.getLevel()), locale);
			} else {
				actors = WorkflowConfig.findMotionActorsVO(domain, internalStatus, userGroup,
						Integer.parseInt(domain.getLevel()), locale);
			}
			model.addAttribute("internalStatusSelected", internalStatus.getId());
			model.addAttribute("actors", actors);
		}
		
		if(usergroupType.startsWith(ApplicationConstants.DEPARTMENT)
				&& internalStatus.getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
			CustomParameter csptDepartmentChangeTimeLimitForDepartmentUsers = CustomParameter.findByName(CustomParameter.class, domain.getType().getType().toUpperCase()+"_"+domain.getHouseType().getType().toUpperCase()+"_DEPARTMENT_CHANGE_TIME_LIMIT_FOR_"+usergroupType.toUpperCase(), "");
			if(csptDepartmentChangeTimeLimitForDepartmentUsers!=null
					&& csptDepartmentChangeTimeLimitForDepartmentUsers.getValue()!=null
					&& !csptDepartmentChangeTimeLimitForDepartmentUsers.getValue().isEmpty()) {
				int timeLimitForDepartmentChangeInHours = Integer.parseInt(csptDepartmentChangeTimeLimitForDepartmentUsers.getValue());
				Calendar assignmentTimeCalendar = Calendar.getInstance();
				ReferenceLetter referenceLetter = ReferenceLetter.findByFieldName(ReferenceLetter.class, "referenceNumber", workflowDetails.getReferenceNumber(), locale.toString());
				if(referenceLetter!=null) {
					assignmentTimeCalendar.setTime(referenceLetter.getDispatchDate());
				} else {
					assignmentTimeCalendar.setTime(workflowDetails.getAssignmentTime());
				}				
				assignmentTimeCalendar.set(Calendar.HOUR_OF_DAY, assignmentTimeCalendar.get(Calendar.HOUR_OF_DAY)+timeLimitForDepartmentChangeInHours);
				if(new Date().after(assignmentTimeCalendar.getTime())) {
					model.addAttribute("departmentChangeTimeLimitCrossed", "YES");
				}
			}
		}
		
		/**** add domain to model ****/
		model.addAttribute("domain", domain);
	}

	private void populateInternalStatus(final ModelMap model, final Motion domain, final String usergroupType,
			final String locale) {
		try {
			List<Status> internalStatuses = new ArrayList<Status>();
			DeviceType deviceType = domain.getType();
			Status internaStatus = domain.getInternalStatus();
			HouseType houseType = domain.getHouseType();
			/**** Final Approving Authority(Final Status) ****/
			CustomParameter finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,
					deviceType.getType().toUpperCase() + "_FINAL_AUTHORITY", "");
			CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(CustomParameter.class,
					"MOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_"
							+ internaStatus.getType().toUpperCase() + "_" + usergroupType.toUpperCase(),
					"");
			CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class,
					"MOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_"
							+ houseType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(),
					"");
			CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class,
					"MOTION_PUT_UP_OPTIONS_" + deviceType.getType().toUpperCase() + "_" + usergroupType.toUpperCase(),
					"");
			if (finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(usergroupType)) {
				CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,
						"MOTION_PUT_UP_OPTIONS_" + usergroupType.toUpperCase(), "");
				if (finalApprovingAuthorityStatus != null) {
					internalStatuses = Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(), locale);
				}
			} /****
				 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+INTERNALSTATUS_TYPE+
				 * USERGROUP(Post Final Status)
				 ****/
			else if (deviceTypeInternalStatusUsergroup != null) {
				internalStatuses = Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(), locale);
			} /****
				 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(
				 * Pre Final Status-House Type Basis)
				 ****/
			else if (deviceTypeHouseTypeUsergroup != null) {
				internalStatuses = Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), locale);
			}
			/****
			 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final
			 * Status)
			 ****/
			else if (deviceTypeUsergroup != null) {
				internalStatuses = Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), locale);
			}
			/**** Internal Status ****/
			model.addAttribute("internalStatuses", internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}
	}

	private void populateInternalStatus(final ModelMap model, final String statusType, final String userGroupType,
			final String locale) {
		List<Status> internalStatuses = new ArrayList<Status>();
		try {
			/****
			 * First we will check if custom parameter for device type,internal
			 * status and usergroupType has been set
			 ****/
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,
					"MOTION_PUT_UP_OPTIONS_MOTIONS_CALLING_ATTENTION_" + statusType.toUpperCase() + "_"
							+ userGroupType.toUpperCase(),
					"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.findByName(CustomParameter.class,
					"MOTION_PUT_UP_OPTIONS_MOTION_PUT_UP_OPTIONS_MOTIONS_CALLING_ATTENTION_"
							+ userGroupType.toUpperCase(),
					"");
			CustomParameter specificStatuses = CustomParameter.findByName(CustomParameter.class,
					"MOTION_PUT_UP_OPTIONS_" + statusType.toUpperCase() + "_" + userGroupType.toUpperCase(), "");
			if (specificDeviceStatusUserGroupStatuses != null) {
				internalStatuses = Status.findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(),
						locale);
			} else if (specificDeviceUserGroupStatuses != null) {
				internalStatuses = Status.findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			} else if (specificStatuses != null) {
				internalStatuses = Status.findStatusContainedIn(specificStatuses.getValue(), locale);
			}
			/**** Internal Status ****/
			model.addAttribute("internalStatuses", internalStatuses);
		} catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	@RequestMapping(method = RequestMethod.PUT)
	public String updateMyTask(final ModelMap model, final HttpServletRequest request, final Locale locale,
			@Valid @ModelAttribute("domain") final Motion domain, final BindingResult result) {
		String referenceNumber = null;
		String referredNumber = null;
		/*** Is Resubmission of Revised Motion Text ***/
		boolean boolResendRevisedMotionText = false;
		String resendRevisedMotionTextStatus = request.getParameter("resendMotionTextStatus");
		if (resendRevisedMotionTextStatus != null && !resendRevisedMotionTextStatus.isEmpty()) {
			boolResendRevisedMotionText = true;
		}

		/**** Is Clarification of Question Received or not *************/
		boolean boolClarificationStatus = false;
		String clarificationStatus = request.getParameter("clarificationStatus");
		if (clarificationStatus != null && !clarificationStatus.isEmpty()) {
			boolClarificationStatus = true;
		}

		/**** Binding Supporting Members ****/
		String[] strSupportingMembers = request.getParameterValues("selectedSupportingMembers");
		if (strSupportingMembers != null) {
			if (strSupportingMembers.length > 0) {
				List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
				for (String i : strSupportingMembers) {
					SupportingMember supportingMember = SupportingMember.findById(SupportingMember.class,
							Long.parseLong(i));
					supportingMembers.add(supportingMember);
				}
				domain.setSupportingMembers(supportingMembers);
			}
		}
		/**** Binding Clubbed Entities ****/
		String[] strClubbedEntities = request.getParameterValues("clubbedEntities");
		if (strClubbedEntities != null) {
			if (strClubbedEntities.length > 0) {
				List<ClubbedEntity> clubbedEntities = new ArrayList<ClubbedEntity>();
				for (String i : strClubbedEntities) {
					ClubbedEntity clubbedEntity = ClubbedEntity.findById(ClubbedEntity.class, Long.parseLong(i));
					clubbedEntities.add(clubbedEntity);
				}
				domain.setClubbedEntities(clubbedEntities);
			}
		}
		/**** Binding Referenced Entities ****/
		String[] strReferencedEntities = request.getParameterValues("referencedEntities");
		if (strReferencedEntities != null) {
			if (strReferencedEntities.length > 0) {
				List<ReferenceUnit> referencedEntities = new ArrayList<ReferenceUnit>();
				for (String i : strReferencedEntities) {
					ReferenceUnit referencedEntity = ReferenceUnit.findById(ReferenceUnit.class, Long.parseLong(i));
					referencedEntities.add(referencedEntity);
				}
				domain.setReferencedUnits(referencedEntities);
			}
		}
		/**** Workflowdetails ****/
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,
				domain.getWorkflowDetailsId());
		String userGroupType = workflowDetails.getAssigneeUserGroupType();
		/**** Updating domain ****/
		domain.setEditedOn(new Date());
		domain.setEditedBy(this.getCurrentUser().getActualUsername());
		domain.setEditedAs(workflowDetails.getAssigneeUserGroupName());
		String strCreationDate = request.getParameter("setCreationDate");
		String strSubmissionDate = request.getParameter("setSubmissionDate");
		String strWorkflowStartedOnDate = request.getParameter("workflowStartedOnDate");
		String strTaskReceivedOnDate = request.getParameter("taskReceivedOnDate");
		CustomParameter dateTimeFormat = CustomParameter.findByName(CustomParameter.class, "SERVER_DATETIMEFORMAT", "");
		if (dateTimeFormat != null) {
			SimpleDateFormat format = FormaterUtil.getDateFormatter(dateTimeFormat.getValue(), "en_US");
			try {
				if (strSubmissionDate != null && !strSubmissionDate.isEmpty()) {
					domain.setSubmissionDate(format.parse(strSubmissionDate));
				}
				if (strCreationDate != null && !strCreationDate.isEmpty()) {
					domain.setCreationDate(format.parse(strCreationDate));
				}
				if (strWorkflowStartedOnDate != null && !strWorkflowStartedOnDate.isEmpty()) {
					domain.setWorkflowStartedOn(format.parse(strWorkflowStartedOnDate));
				}
				if (strTaskReceivedOnDate != null && !strTaskReceivedOnDate.isEmpty()) {
					domain.setTaskReceivedOn(format.parse(strTaskReceivedOnDate));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		/**** answer related dates ****/
		if (domain.getAnsweringDate() == null) {
			String strAnsweringDate = request.getParameter("setAnsweringDate");
			if (strAnsweringDate != null && !strAnsweringDate.isEmpty()) {
				Date answeringDate = null;
				try {
					answeringDate = FormaterUtil.getDateFormatter("en_US").parse(strAnsweringDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Added the above code as the following code was giving
				// exception of unparseble date
				// Date answeringDate =
				// FormaterUtil.formatStringToDate(strAnsweringDate,
				// ApplicationConstants.DB_DATEFORMAT, locale.toString());
				domain.setAnsweringDate(answeringDate);
			}
		}

		// ---new code
		String currentDeviceTypeWorkflowType = null;
		Workflow workflowFromUpdatedStatus = null;
		try {
			if (domain.getRecommendationStatus().getType()
					.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
					|| domain.getRecommendationStatus().getType().equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
					|| domain.getRecommendationStatus().getType()
							.equals(ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {

				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getRecommendationStatus(), domain.getLocale());

			} else if (domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLUBBING)
					|| domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)
					|| (domain.getInternalStatus().getType()
							.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							&& domain.getRecommendationStatus().getType()
									.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					|| (domain.getInternalStatus().getType()
							.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
							&& domain.getRecommendationStatus().getType()
									.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
					|| (domain.getInternalStatus().getType()
							.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
							&& domain.getRecommendationStatus().getType()
									.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					|| (domain.getInternalStatus().getType()
							.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
							&& domain.getRecommendationStatus().getType()
									.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))) {

				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
			} else {
				workflowFromUpdatedStatus = null;
			}

			// String sendbackactor=request.getParameter("sendbackactor");
			if (workflowFromUpdatedStatus != null) {
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}

			performAction(domain);

			// domain.merge();

			if (currentDeviceTypeWorkflowType == null) {
				workflowFromUpdatedStatus = Workflow.findByStatus(domain.getInternalStatus(), domain.getLocale());
				currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
			}
			// ---new code

			Motion motion = Motion.findById(Motion.class, domain.getId());
			boolean isMinistryChanged = false;
			boolean isSubDepartmentChanged = false;
			Ministry previousMinistry = motion.getMinistry();
			SubDepartment previousSubDepartment = motion.getSubDepartment();
			CustomParameter subDepartmentFilterAllowedFor = CustomParameter.findByName(CustomParameter.class,
					"MOIS_SUBDEPARTMENT_FILTER_ALLOWED_FOR", "");
			if (subDepartmentFilterAllowedFor != null) {
				if (subDepartmentFilterAllowedFor.getValue().contains(userGroupType)) {
					if (!domain.getMinistry().equals(previousMinistry)) {
						isMinistryChanged = true;
					} else if (domain.getSubDepartment() != null
							&& !domain.getSubDepartment().equals(previousSubDepartment)) {
						isSubDepartmentChanged = true;
					}
				}
			}

			if (isMinistryChanged || isSubDepartmentChanged) {
				//SEND NOTIFICATION FOR DEPARTMENT CHANGE
				String usergroupTypesForDeptChangeNotification = "";
				if (userGroupType.equals(ApplicationConstants.DEPARTMENT)
						|| userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
					Status sendToDepartmentStatus = Status.findByType(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT, locale.toString());
					domain.setRecommendationStatus(sendToDepartmentStatus);
					usergroupTypesForDeptChangeNotification = "assistant,section_officer,department";
				} 
				else {
					domain.setRecommendationStatus(motion.getInternalStatus());
					usergroupTypesForDeptChangeNotification = "assistant,clerk";
				}				
				domain.merge();
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion);
				motion.removeExistingWorkflowAttributes();
				motion.setRecommendationStatus(domain.getRecommendationStatus());

				if (wfDetails != null) {
					// Before ending wfDetails process collect information
					// which will be useful for creating a new process later.
					int assigneeLevel = Integer.parseInt(wfDetails.getAssigneeLevel());
					UserGroupType ugt = UserGroupType.findByType(userGroupType, locale.toString());
					WorkflowDetails.endProcess(wfDetails);
					if (userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
						ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale.toString());
						assigneeLevel = assigneeLevel - 1;
					}
					
					if(domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
						ReferenceLetter latestIntimationReferenceLetterHavingMotion = ReferenceLetter
								.findLatestHavingGivenDevice(domain.getId().toString(),
										ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT,
										locale.toString());
			
						Map<String, String> referenceLetterIdentifiers = new LinkedHashMap<String, String>();
						referenceLetterIdentifiers.put("parentDeviceId", domain.getId().toString());
						referenceLetterIdentifiers.put("referenceFor",
								ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT);
						ReferenceLetter latestIntimationReferenceLetterForMotion = ReferenceLetter
								.findLatestByFieldNames(referenceLetterIdentifiers, locale.toString());
			
						if (latestIntimationReferenceLetterHavingMotion != null
								&& latestIntimationReferenceLetterForMotion != null
								&& latestIntimationReferenceLetterHavingMotion.getParentDeviceId()
										.equals(domain.getId().toString())) {
							String strReferenceNumber = latestIntimationReferenceLetterForMotion
									.getReferenceNumber();
							if (strReferenceNumber != null && !strReferenceNumber.isEmpty()) {
								String[] referenceNumberSplits = strReferenceNumber
										.split(domain.getId().toString());
								Integer referenceNo = Integer.parseInt(referenceNumberSplits[1]) + 1;
								referenceNumber = domain.getId().toString() + referenceNo.toString();
								referredNumber = strReferenceNumber;
							}
						} else {
							if (latestIntimationReferenceLetterHavingMotion != null
									&& !latestIntimationReferenceLetterHavingMotion.getParentDeviceId()
											.equals(domain.getId())) {
								Motion previousParentMotion = Motion.findById(Motion.class, Long.parseLong(
										latestIntimationReferenceLetterHavingMotion.getParentDeviceId()));
								if (previousParentMotion != null && previousParentMotion.getParent().getId()
										.equals(domain.getId())) {
									referredNumber = latestIntimationReferenceLetterHavingMotion
											.getReferenceNumber();
								}
							}
							if (latestIntimationReferenceLetterForMotion != null) {
								String strReferenceNumber = latestIntimationReferenceLetterForMotion
										.getReferenceNumber();
								if (strReferenceNumber != null && !strReferenceNumber.isEmpty()) {
									String[] referenceNumberSplits = strReferenceNumber
											.split(domain.getId().toString());
									Integer referenceNo = Integer.parseInt(referenceNumberSplits[1]) + 1;
									referenceNumber = domain.getId().toString() + referenceNo.toString();
								}
							} else {
								referenceNumber = domain.getId().toString() + "1";
							}
						}
					}					
					
					Workflow workflow = Workflow.findByStatus(motion.getInternalStatus(), locale.toString());
					// Motion in Post final status and pre ballot state can be
					// group changed by Department
					// as well as assistant of Secretariat
					WorkflowDetails resendRevisedMotionTextWorkflowDetails = WorkflowDetails.startProcessAtGivenLevel(motion, ApplicationConstants.APPROVAL_WORKFLOW, workflow,
							ugt, assigneeLevel, referenceNumber, referredNumber, locale.toString());
					
					String copyType = null;
					if (referredNumber != null && !referredNumber.isEmpty()) {
						copyType = "revisedCopy";
					} 
					else {
						if(domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
							copyType = "factual_position";
						}
						else {
							copyType = "tentativeCopy";
						}						
					}

					/**** SEND NOTIFICATION TO DEPARTMENT USER ****/
					if (resendRevisedMotionTextWorkflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT) 
							|| userGroupType.equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
						NotificationController.sendDepartmentProcessNotificationForMotion(domain,
								resendRevisedMotionTextWorkflowDetails.getAssignee(), copyType,
								domain.getLocale());
					}

					/****
					 * CREATE REFERENCE LETTER FOR DEPARTMENT USER
					 ****/
					if (resendRevisedMotionTextWorkflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT)
							&& domain.getInternalStatus().getType()
									.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
						ReferenceLetter referenceLetter = Motion.generateReferenceLetter(domain, copyType,
								this.getCurrentUser().getActualUsername(),
								resendRevisedMotionTextWorkflowDetails.getAssignee(), referenceNumber,
								referredNumber, locale.toString());

						if (referenceLetter == null || referenceLetter.getId() == null) {
							logger.error(
									"Error in generation of reference letter while sending for reply to department");
						}
					}
					
					NotificationController.sendDepartmentChangeNotification(domain.getNumber().toString(), domain.getType(), domain.getHouseType(), previousSubDepartment.getName(), domain.getSubDepartment().getName(), usergroupTypesForDeptChangeNotification, domain.getLocale());
					
					/**** display message ****/
					model.addAttribute("type", "taskcompleted");
					return "workflow/info";
				}
			}

			String bulkEdit = request.getParameter("bulkedit");
			if (bulkEdit == null || !bulkEdit.equals("yes")) {
				/**** Complete Task ****/
				String nextuser = domain.getActor();
				String level = domain.getLevel();
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("pv_deviceId", String.valueOf(domain.getId()));
				properties.put("pv_deviceTypeId", String.valueOf(domain.getType().getId()));
				UserGroupType usergroupType = null;
				if (nextuser != null && !nextuser.isEmpty()) {
					String[] temp = nextuser.split("#");
					properties.put("pv_user", temp[0]);
					usergroupType = UserGroupType.findByType(temp[1], locale.toString());
				}
				String endflag = domain.getEndFlag();
				properties.put("pv_endflag", request.getParameter("endflag"));
				if (endflag != null && !endflag.isEmpty()) {
					if (endflag.equals("continue")) {
						if (boolResendRevisedMotionText) {
							ProcessDefinition processDefinition = processService
									.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
							ProcessInstance processInstance = processService.createProcessInstance(processDefinition,
									properties);
							/**** Process Started and task created ****/
							Task resendRevisedMotionTextTask = processService.getCurrentTask(processInstance);
							WorkflowDetails pendingWorkflow = WorkflowDetails.findCurrentWorkflowDetail(domain);
							WorkflowDetails resendRevisedMotionTextWorkflowDetails;
							try {
								if (pendingWorkflow != null) {
									if (pendingWorkflow.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
										Task prevTask = processService.findTaskById(pendingWorkflow.getTaskId());
										processService.completeTask(prevTask, properties);
										pendingWorkflow.setStatus("COMPLETED");
										pendingWorkflow.setCompletionTime(new Date());
										pendingWorkflow.merge();
									}

									// Old Logic for Reference Number and
									// Referred Number (Remove below commented
									// block once New Logic works fine)
									// WorkflowDetails wfDetail =
									// WorkflowDetails.findByDeviceAssignee(domain,
									// null, ApplicationConstants.DEPARTMENT,
									// locale.toString());
									// if(wfDetail!=null) {
									// String strReferenceNumber =
									// wfDetail.getReferenceNumber();
									// if(strReferenceNumber != null &&
									// !strReferenceNumber.isEmpty()){
									// String[] referenceNumberSplits =
									// strReferenceNumber.split(domain.getId().toString());
									// Integer referenceNo =
									// Integer.parseInt(referenceNumberSplits[1])
									// + 1;
									// referenceNumber =
									// domain.getId().toString() +
									// referenceNo.toString();
									// referredNumber = strReferenceNumber;
									// }else{
									// referenceNumber=
									// domain.getId().toString() + "1";
									// }
									// }else{
									// referenceNumber=
									// domain.getId().toString() + "1";
									// }
									// ==================================Old
									// Logic
									// End================================//

									// New Logic for Reference Number and
									// Referred Number
									ReferenceLetter latestIntimationReferenceLetterHavingMotion = ReferenceLetter
											.findLatestHavingGivenDevice(domain.getId().toString(),
													ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT,
													locale.toString());

									Map<String, String> referenceLetterIdentifiers = new LinkedHashMap<String, String>();
									referenceLetterIdentifiers.put("parentDeviceId", domain.getId().toString());
									referenceLetterIdentifiers.put("referenceFor",
											ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT);
									ReferenceLetter latestIntimationReferenceLetterForMotion = ReferenceLetter
											.findLatestByFieldNames(referenceLetterIdentifiers, locale.toString());

									if (latestIntimationReferenceLetterHavingMotion != null
											&& latestIntimationReferenceLetterForMotion != null
											&& latestIntimationReferenceLetterHavingMotion.getParentDeviceId()
													.equals(domain.getId().toString())) {
										String strReferenceNumber = latestIntimationReferenceLetterForMotion
												.getReferenceNumber();
										if (strReferenceNumber != null && !strReferenceNumber.isEmpty()) {
											String[] referenceNumberSplits = strReferenceNumber
													.split(domain.getId().toString());
											Integer referenceNo = Integer.parseInt(referenceNumberSplits[1]) + 1;
											referenceNumber = domain.getId().toString() + referenceNo.toString();
											referredNumber = strReferenceNumber;
										}
									} else {
										if (latestIntimationReferenceLetterHavingMotion != null
												&& !latestIntimationReferenceLetterHavingMotion.getParentDeviceId()
														.equals(domain.getId())) {
											Motion previousParentMotion = Motion.findById(Motion.class, Long.parseLong(
													latestIntimationReferenceLetterHavingMotion.getParentDeviceId()));
											if (previousParentMotion != null && previousParentMotion.getParent().getId()
													.equals(domain.getId())) {
												referredNumber = latestIntimationReferenceLetterHavingMotion
														.getReferenceNumber();
											}
										}
										if (latestIntimationReferenceLetterForMotion != null) {
											String strReferenceNumber = latestIntimationReferenceLetterForMotion
													.getReferenceNumber();
											if (strReferenceNumber != null && !strReferenceNumber.isEmpty()) {
												String[] referenceNumberSplits = strReferenceNumber
														.split(domain.getId().toString());
												Integer referenceNo = Integer.parseInt(referenceNumberSplits[1]) + 1;
												referenceNumber = domain.getId().toString() + referenceNo.toString();
											}
										} else {
											referenceNumber = domain.getId().toString() + "1";
										}
									}
									// ==================================New
									// Logic
									// End================================//
								}
								resendRevisedMotionTextWorkflowDetails = WorkflowDetails.create(domain,
										resendRevisedMotionTextTask, usergroupType, currentDeviceTypeWorkflowType,
										level, referenceNumber, referredNumber);

								String copyType = null;
								if (referredNumber != null && !referredNumber.isEmpty()) {
									copyType = "revisedCopy";
								} else {
									if(domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
										copyType = "factual_position";
									}
									else {
										copyType = "tentativeCopy";
									}
								}

								/**** SEND NOTIFICATION TO DEPARTMENT USER ****/
								if (usergroupType.getType().equals(ApplicationConstants.DEPARTMENT) || usergroupType
										.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
									NotificationController.sendDepartmentProcessNotificationForMotion(domain,
											resendRevisedMotionTextWorkflowDetails.getAssignee(), copyType,
											domain.getLocale());
								}

								/****
								 * CREATE REFERENCE LETTER FOR DEPARTMENT USER
								 ****/
								if (usergroupType.getType().equals(ApplicationConstants.DEPARTMENT)
										&& domain.getInternalStatus().getType()
												.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
										&& (domain.getRecommendationStatus().getType()
												.equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT)
												|| domain.getRecommendationStatus().getType()
														.equals(ApplicationConstants.MOTION_PROCESSED_RESENDREVISEDMOTIONTEXTTODEPARTMENT))
										&& workflowDetails.getAssigneeUserGroupType()
												.equals(ApplicationConstants.SECTION_OFFICER)) {
									ReferenceLetter referenceLetter = Motion.generateReferenceLetter(domain, copyType,
											this.getCurrentUser().getActualUsername(),
											resendRevisedMotionTextWorkflowDetails.getAssignee(), referenceNumber,
											referredNumber, locale.toString());

									if (referenceLetter == null || referenceLetter.getId() == null) {
										logger.error(
												"Error in generation of reference letter while sending for reply to department");
									}
								}

								domain.setWorkflowDetailsId(resendRevisedMotionTextWorkflowDetails.getId());
								resendRevisedMotionTextWorkflowDetails
										.setPreviousWorkflowDetail(workflowDetails.getId());
								resendRevisedMotionTextWorkflowDetails.merge();
								domain.setTaskReceivedOn(new Date());
							} catch (ELSException e) {
								model.addAttribute("error", e.getParameter());
								e.printStackTrace();
							}
						} else if (boolClarificationStatus) {
							/**** Process Started and task created ****/
							List<WorkflowDetails> pendingWorkflows = WorkflowDetails.findPendingWorkflowDetails(domain,
									workflowDetails.getWorkflowType());
							for (WorkflowDetails wd : pendingWorkflows) {
								Task prevTask = processService.findTaskById(wd.getTaskId());
								processService.completeTask(prevTask, properties);
								wd.setStatus("TIMEOUT");
								wd.setCompletionTime(new Date());
								wd.merge();
							}
						} else {
							String strTaskId = workflowDetails.getTaskId();
							Task task = processService.findTaskById(strTaskId);
							processService.completeTask(task, properties);
							ProcessInstance processInstance = processService
									.findProcessInstanceById(task.getProcessInstanceId());
							Task newtask = processService.getCurrentTask(processInstance);
							referenceNumber = workflowDetails.getReferenceNumber();
							referredNumber = workflowDetails.getReferredNumber();
							// Status recommendStatus =
							// Status.findByType(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT,
							// locale.toString());
							// Status revisedRecommendStatus =
							// Status.findByType(ApplicationConstants.MOTION_PROCESSED_RESENDREVISEDMOTIONTEXTTODEPARTMENT,
							// locale.toString());
							if (usergroupType.getType().equals(ApplicationConstants.DEPARTMENT)
									&& domain.getInternalStatus().getType()
											.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
									&& (domain.getRecommendationStatus().getType()
											.equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT)
											|| domain.getRecommendationStatus().getType()
													.equals(ApplicationConstants.MOTION_PROCESSED_RESENDREVISEDMOTIONTEXTTODEPARTMENT))
									&& workflowDetails.getAssigneeUserGroupType()
											.equals(ApplicationConstants.SECTION_OFFICER)) {

								// Old Logic for Reference Number and Referred
								// Number (Remove below commented block once New
								// Logic works fine)
								// WorkflowDetails wfDetail =
								// WorkflowDetails.findByDeviceAssignee(domain,
								// null, ApplicationConstants.DEPARTMENT,
								// locale.toString());
								// if(wfDetail != null){
								// if(wfDetail.getReferenceNumber() != null &&
								// !wfDetail.getReferenceNumber().isEmpty()){
								// String strReferenceNumber =
								// wfDetail.getReferenceNumber();
								// if(strReferenceNumber != null &&
								// !strReferenceNumber.isEmpty()){
								// String[] referenceNumberSplits =
								// strReferenceNumber.split(domain.getId().toString());
								// Integer referenceNo =
								// Integer.parseInt(referenceNumberSplits[1]) +
								// 1;
								// referenceNumber= domain.getId().toString() +
								// referenceNo.toString();
								// referredNumber = strReferenceNumber;
								// }
								// }else{
								// referenceNumber= domain.getId().toString() +
								// "1";
								// }
								// }else{
								// referenceNumber= domain.getId().toString() +
								// "1";
								// }
								// ==================================Old Logic
								// End================================//

								// New Logic for Reference Number and Referred
								// Number
								ReferenceLetter latestIntimationReferenceLetterHavingMotion = ReferenceLetter
										.findLatestHavingGivenDevice(domain.getId().toString(),
												ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT,
												locale.toString());

								Map<String, String> referenceLetterIdentifiers = new LinkedHashMap<String, String>();
								referenceLetterIdentifiers.put("parentDeviceId", domain.getId().toString());
								referenceLetterIdentifiers.put("referenceFor",
										ApplicationConstants.INTIMATION_FOR_REPLY_FROM_DEPARTMENT);
								ReferenceLetter latestIntimationReferenceLetterForMotion = ReferenceLetter
										.findLatestByFieldNames(referenceLetterIdentifiers, locale.toString());

								if (latestIntimationReferenceLetterHavingMotion != null
										&& latestIntimationReferenceLetterForMotion != null
										&& latestIntimationReferenceLetterHavingMotion.getParentDeviceId()
												.equals(domain.getId().toString())) {
									String strReferenceNumber = latestIntimationReferenceLetterForMotion
											.getReferenceNumber();
									if (strReferenceNumber != null && !strReferenceNumber.isEmpty()) {
										String[] referenceNumberSplits = strReferenceNumber
												.split(domain.getId().toString());
										Integer referenceNo = Integer.parseInt(referenceNumberSplits[1]) + 1;
										referenceNumber = domain.getId().toString() + referenceNo.toString();
										referredNumber = strReferenceNumber;
									}
								} else {
									if (latestIntimationReferenceLetterHavingMotion != null
											&& !latestIntimationReferenceLetterHavingMotion.getParentDeviceId()
													.equals(domain.getId())) {
										Motion previousParentMotion = Motion.findById(Motion.class, Long.parseLong(
												latestIntimationReferenceLetterHavingMotion.getParentDeviceId()));
										if (previousParentMotion != null
												&& previousParentMotion.getParent().getId().equals(domain.getId())) {
											referredNumber = latestIntimationReferenceLetterHavingMotion
													.getReferenceNumber();
										}
									}
									if (latestIntimationReferenceLetterForMotion != null) {
										String strReferenceNumber = latestIntimationReferenceLetterForMotion
												.getReferenceNumber();
										if (strReferenceNumber != null && !strReferenceNumber.isEmpty()) {
											String[] referenceNumberSplits = strReferenceNumber
													.split(domain.getId().toString());
											Integer referenceNo = Integer.parseInt(referenceNumberSplits[1]) + 1;
											referenceNumber = domain.getId().toString() + referenceNo.toString();
										}
									} else {
										referenceNumber = domain.getId().toString() + "1";
									}
								}
								// ==================================New Logic
								// End================================//
							}
							/****
							 * Workflow Detail entry made only if its not the
							 * end of workflow
							 ****/
							WorkflowDetails workflowDetails2 = WorkflowDetails.create(domain, newtask, usergroupType,
									currentDeviceTypeWorkflowType, level, referenceNumber, referredNumber);

							String copyType = null;
							if (referredNumber != null && !referredNumber.isEmpty()) {
								copyType = "revisedCopy";
							} else {
								if(domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
									copyType = "factual_position";
								}
								else {
									copyType = "tentativeCopy";
								}
							}

							/**** SEND NOTIFICATION TO DEPARTMENT USER ****/
							if ((usergroupType.getType().equals(ApplicationConstants.DEPARTMENT)
									|| usergroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))
									&& domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
								NotificationController.sendDepartmentProcessNotificationForMotion(domain,
										workflowDetails2.getAssignee(), copyType, domain.getLocale());
							}
							else if ((usergroupType.getType().equals(ApplicationConstants.DEPARTMENT)
									|| usergroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))
									&& domain.getInternalStatus().getType().equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
								NotificationController.sendDepartmentProcessNotificationForMotion(domain,
										workflowDetails2.getAssignee(), copyType, domain.getLocale());
							}

							/****
							 * CREATE REFERENCE LETTER FOR DEPARTMENT USER
							 ****/
							if (usergroupType.getType().equals(ApplicationConstants.DEPARTMENT)
									&& domain.getInternalStatus().getType()
											.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
									&& (domain.getRecommendationStatus().getType()
											.equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT)
											|| domain.getRecommendationStatus().getType()
													.equals(ApplicationConstants.MOTION_PROCESSED_RESENDREVISEDMOTIONTEXTTODEPARTMENT))
									&& workflowDetails.getAssigneeUserGroupType()
											.equals(ApplicationConstants.SECTION_OFFICER)) {
								ReferenceLetter referenceLetter = Motion.generateReferenceLetter(domain, copyType,
										this.getCurrentUser().getActualUsername(), workflowDetails2.getAssignee(),
										referenceNumber, referredNumber, locale.toString());

								if (referenceLetter == null || referenceLetter.getId() == null) {
									logger.error(
											"Error in generation of reference letter while sending for reply to department");
								}
							}
							/****
							 * FOr CLarificationFromMember and Department
							 ****/
							if (domain.getInternalStatus().getType()
									.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
									&& domain.getRecommendationStatus().getType()
											.equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT)) {

								Map<String, String> parameters = new HashMap<String, String>();
								User user = User.find(domain.getPrimaryMember());
								Credential credential = user.getCredential();
								parameters.put("pv_endflag", endflag);
								parameters.put("pv_user", credential.getUsername());
								parameters.put("pv_deviceId", String.valueOf(domain.getId()));
								parameters.put("pv_deviceTypeId", String.valueOf(domain.getType().getId()));

								ProcessDefinition processDefinition1 = processService
										.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
								ProcessInstance processInstance1 = processService
										.createProcessInstance(processDefinition1, parameters);
								Task newMembertask = processService.getCurrentTask(processInstance1);
								WorkflowDetails.create(domain, newMembertask, currentDeviceTypeWorkflowType, level);

							}
							domain.setWorkflowDetailsId(workflowDetails2.getId());
							domain.setTaskReceivedOn(new Date());
						}
						/**** SEND NOTIFICATION OF ANSWER RECEIVED ONLINE TO BRANCH USERS ****/
						if(workflowDetails.getAssigneeUserGroupType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)
								&& domain.getRecommendationStatus().getType().equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_SECTIONOFFICER)
								&& usergroupType.getType().equals(ApplicationConstants.SECTION_OFFICER)
								&& domain.getReply()!=null && !domain.getReply().isEmpty())
						{
							String usergroupTypes = "clerk,assistant,section_officer";
							NotificationController.sendAnswerReceivedOnlineNotification(domain.getNumber().toString(), domain.getType(), domain.getHouseType(), domain.getSubDepartment().getName(), usergroupTypes, domain.getLocale());
						}
					}
				}
				workflowDetails.setStatus("COMPLETED");
				workflowDetails.setCompletionTime(new Date());
				// workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
				// workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
				workflowDetails.setDecisionInternalStatus(domain.getInternalStatus().getName());
				workflowDetails.setDecisionRecommendStatus(domain.getRecommendationStatus().getName());
				workflowDetails.merge();
				domain.merge();
				/**** display message ****/
				model.addAttribute("type", "taskcompleted");
				return "workflow/info";
			}

			domain.merge();
			model.addAttribute("type", "success");
			populateModel(domain, model, request, workflowDetails);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "workflow/motion/" + userGroupType;
	}

	private void performAction(final Motion domain) {
		try {
			String internalStatus = domain.getInternalStatus().getType();
			String recommendationStatus = domain.getRecommendationStatus().getType();
			if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
				performActionOnAdmission(domain);
			} else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_REJECTION)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_REJECTION)) {
				performActionOnRejection(domain);
			}
			/*** Clarification Asked From Department ***/
			else if (internalStatus.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					&& recommendationStatus
							.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)) {
				performActionOnClarificationNeededFromDepartment(domain);
			}
			/*** Clarification Asked From Member ***/
			else if (internalStatus.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					&& recommendationStatus
							.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)) {
				performActionOnClarificationNeededFromMember(domain);
			} else if (internalStatus
					.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					&& recommendationStatus.startsWith(
							ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)) {
				performActionOnClarificationNeededFromMemberAndDepartment(domain);
			}

			/**** Clarification not received From Department ****/
			else if (internalStatus.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					&& recommendationStatus
							.startsWith(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)) {
				performActionOnClarificationNotReceived(domain);
			}
			/**** Clarification not received From Member ****/
			else if (internalStatus.startsWith(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					&& recommendationStatus
							.startsWith(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)) {
				performActionOnClarificationNotReceived(domain);
			}
			/**** Clarification received FROM Member ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED)) {
				performActionOnClarificationReceived(domain);
			}
			/**** Clarification received From Department ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED)) {
				performActionOnClarificationReceived(domain);
			}
			/**** Clarification received FROM Member & Department ****/
			else if (internalStatus
					.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER_DEPARTMENT)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED)) {
				performActionOnClarificationReceived(domain);
			}
			/**** Clubbing is approved ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)) {
				performActionOnClubbing(domain);
			}
			/**** Clubbing is rejected ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_CLUBBING)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_CLUBBING)) {
				performActionOnClubbingRejection(domain);
			}
			/**** Name clubbing is approved ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)) {
				performActionOnNameClubbing(domain);
			}
			/**** Name clubbing is rejected ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_NAME_CLUBBING)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_NAME_CLUBBING)) {
				performActionOnNameClubbingRejection(domain);
			}
			/**** Clubbing Post Admission is approved ****/
			else if (recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				performActionOnClubbingPostAdmission(domain);
			}
			/**** Clubbing Post Admission is rejected ****/
			else if (recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)) {
				performActionOnClubbingRejectionPostAdmission(domain);
			}
			/**** Unclubbing is approved ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)) {
				performActionOnUnclubbing(domain);
			}
			/**** Unclubbing is rejected ****/
			else if (internalStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_UNCLUBBING)
					&& recommendationStatus.equals(ApplicationConstants.MOTION_FINAL_REJECT_UNCLUBBING)) {
				performActionOnUnclubbingRejection(domain);
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	private void performActionOnAdmission(final Motion domain) {
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.MOTION_CLUBBING_MODE, "");

		Status finalStatus = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, domain.getLocale());
		domain.setStatus(finalStatus);
		/****
		 * Setting revised subject,question text,revised reason,revised brief
		 * explaination if not already set
		 ****/
		if (domain.getRevisedSubject() == null) {
			domain.setRevisedSubject(domain.getSubject());
		} else if (domain.getRevisedSubject().isEmpty()) {
			domain.setRevisedSubject(domain.getSubject());
		}
		if (domain.getRevisedDetails() == null) {
			domain.setRevisedDetails(domain.getDetails());
		} else if (domain.getRevisedDetails().isEmpty()) {
			domain.setRevisedDetails(domain.getDetails());
		}
		List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
		if (clubbedEntities != null) {
			String subject = null;
			String details = null;
			if (domain.getRevisedSubject() != null) {
				if (!domain.getRevisedSubject().isEmpty()) {
					subject = domain.getRevisedSubject();
				} else {
					subject = domain.getSubject();
				}
			} else {
				subject = domain.getSubject();
			}
			if (domain.getRevisedDetails() != null) {
				if (!domain.getRevisedDetails().isEmpty()) {
					details = domain.getRevisedDetails();
				} else {
					details = domain.getDetails();
				}
			} else {
				details = domain.getDetails();
			}

			if (csptClubbingMode != null && csptClubbingMode.getValue() != null
					&& !csptClubbingMode.getValue().isEmpty()) {
				if (csptClubbingMode.getValue().equals("normal")) {

					for (ClubbedEntity i : clubbedEntities) {
						Motion motion = i.getMotion();

						// motion.setRevisedSubject(subject);
						// motion.setRevisedDetails(details);
						motion.setStatus(finalStatus);
						motion.setInternalStatus(finalStatus);
						motion.setRecommendationStatus(finalStatus);

						motion.simpleMerge();
					}
				} else if (csptClubbingMode.getValue().equals("workflow")) {
					Status status = Status.findByType(ApplicationConstants.MOTION_PUTUP_NAME_CLUBBING,
							domain.getLocale());
					for (ClubbedEntity i : clubbedEntities) {
						Motion motion = i.getMotion();

						if (motion.getInternalStatus().getType().equals(ApplicationConstants.MOTION_SYSTEM_CLUBBED)) {
							// motion.setRevisedSubject(subject);
							// motion.setRevisedDetails(details);
							motion.setStatus(finalStatus);
							motion.setInternalStatus(finalStatus);
							motion.setRecommendationStatus(finalStatus);
						} else {
							motion.setInternalStatus(status);
							motion.setRecommendationStatus(status);
						}
						motion.simpleMerge();
					}
				}
			}
		}
	}

	private void performActionOnRejection(Motion domain) throws ELSException {
		domain.setStatus(domain.getInternalStatus());
		if (domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()) {
			domain.setRevisedSubject(domain.getSubject());
		}

		if (domain.getRevisedDetails() == null || domain.getRevisedDetails().isEmpty()) {
			domain.setRevisedDetails(domain.getDetails());
		}

		domain.merge(); // Added so as to avoid OptimisticLockException
		// Hack (11Nov2014): Commenting the following line results in
		// OptimisticLockException.
		domain.setVersion(domain.getVersion() + 1);
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class,
				ApplicationConstants.MOTION_CLUBBING_MODE, "");
		if (csptClubbingMode != null && csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()) {
			if (csptClubbingMode.getValue().equals("workflow")) {
				Motion.updateClubbing(domain);
			} else if (csptClubbingMode.getValue().equals("normal")) {
				List<ClubbedEntity> clubbedEntities = domain.getClubbedEntities();
				for (ClubbedEntity i : clubbedEntities) {
					Motion motion = i.getMotion();

					// motion.setRevisedSubject(domain.getRevisedSubject());
					// motion.setRevisedDetails(domain.getRevisedDetails());
					motion.setStatus(domain.getStatus());
					motion.setInternalStatus(domain.getInternalStatus());
					motion.setRecommendationStatus(domain.getRecommendationStatus());

					motion.simpleMerge();
				}
			}
		}

	}

	private void performActionOnClarificationNeededFromDepartment(Motion domain) {

		if (domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()) {
			domain.setRevisedSubject(domain.getSubject());
		}
		if (domain.getRevisedDetails() == null || domain.getRevisedDetails().isEmpty()) {
			domain.setRevisedDetails(domain.getDetails());
		}
	}

	private void performActionOnClarificationNeededFromMember(Motion domain) {
		if (domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()) {
			domain.setRevisedSubject(domain.getSubject());
		}

		if (domain.getRevisedDetails() == null || domain.getRevisedDetails().isEmpty()) {
			domain.setRevisedDetails(domain.getDetails());
		}
	}

	private void performActionOnClarificationNeededFromMemberAndDepartment(Motion domain) {
		if (domain.getRevisedSubject() == null || domain.getRevisedSubject().isEmpty()) {
			domain.setRevisedSubject(domain.getSubject());
		}

		if (domain.getRevisedDetails() == null || domain.getRevisedDetails().isEmpty()) {
			domain.setRevisedDetails(domain.getDetails());
		}

	}

	private void performActionOnClarificationReceived(Motion domain) {
		Status newStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
	}

	private void performActionOnClubbing(Motion domain) throws ELSException {

		Motion.updateClubbing(domain);

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

	private void performActionOnClubbingRejection(Motion domain) throws ELSException {
		/****
		 * remove clubbing (status is changed accordingly in unclub method
		 * itself)
		 ****/
		Motion.unclub(domain, domain.getLocale());

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

	private void performActionOnNameClubbing(Motion domain) throws ELSException {

		Motion.updateClubbing(domain);

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

	private void performActionOnNameClubbingRejection(Motion domain) throws ELSException {
		/****
		 * remove clubbing (status is changed accordingly in unclub method
		 * itself)
		 ****/
		Motion.unclub(domain, domain.getLocale());

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

	private void performActionOnClubbingPostAdmission(Motion domain) throws ELSException {

		Motion.updateClubbing(domain);

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

	private void performActionOnClubbingRejectionPostAdmission(Motion domain) throws ELSException {
		/****
		 * remove clubbing (status is changed accordingly in unclub method
		 * itself)
		 ****/
		Motion.unclub(domain, domain.getLocale());

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

	private void performActionOnUnclubbing(Motion domain) throws ELSException {
		/****
		 * remove clubbing (status is changed accordingly in unclub method
		 * itself)
		 ****/
		Motion.unclub(domain, domain.getLocale());

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

	private void performActionOnUnclubbingRejection(Motion domain) throws ELSException {
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

	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value = "/bulkapproval/init", method = RequestMethod.POST)
	public String getBulkApprovalInit(final HttpServletRequest request, final Locale locale, final ModelMap model) {

		try {
			/**** Request Params ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strMotionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strItemsCount = request.getParameter("itemsCount");
			String strFile = request.getParameter("file");
			String strLocale = locale.toString();
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			String strUserGroupType = null;
			String strUsergroup = null;
			if (userGroups != null) {
				if (!userGroups.isEmpty()) {

					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,
							"MOIS_ALLOWED_USERGROUPTYPES", "");
					if (customParameter != null) {
						String allowedUserGroups = customParameter.getValue();
						for (UserGroup i : userGroups) {
							if (i.getActiveFrom().before(new Date()) && i.getActiveTo().after(new Date())) {
								if (allowedUserGroups.contains(i.getUserGroupType().getType())) {
									strUsergroup = String.valueOf(i.getId());
									strUserGroupType = i.getUserGroupType().getType();
									break;
								}
							}

						}
					}
				}
			}
			Set<Role> roles = this.getCurrentUser().getRoles();
			String strRole = null;
			for (Role i : roles) {
				if (i.getType().startsWith("MEMBER_")) {
					strRole = i.getType();
					break;
				} else if (i.getType().contains("MOIS_CLERK")) {
					strRole = i.getType();
					break;
				} else if (i.getType().startsWith("MOIS_")) {
					strRole = i.getType();
					break;
				}
			}
			if (strHouseType != null && !(strHouseType.isEmpty()) && strSessionType != null
					&& !(strSessionType.isEmpty()) && strSessionYear != null && !(strSessionYear.isEmpty())
					&& strMotionType != null && !(strMotionType.isEmpty()) && strStatus != null
					&& !(strStatus.isEmpty()) && strRole != null && !(strRole.isEmpty()) && strUsergroup != null
					&& !(strUsergroup.isEmpty()) && strUserGroupType != null && !(strUserGroupType.isEmpty())
					&& strItemsCount != null && !(strItemsCount.isEmpty()) && strFile != null && !(strFile.isEmpty())
					&& strWorkflowSubType != null && !(strWorkflowSubType.isEmpty())) {
				/**** List of Statuses ****/
				if (strWorkflowSubType.equals("request_to_supporting_member")) {
					Status approveStatus = Status.findByFieldName(Status.class, "type",
							ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale.toString());
					Status rejectStatus = Status.findByFieldName(Status.class, "type",
							ApplicationConstants.SUPPORTING_MEMBER_REJECTED, locale.toString());
					List<Status> decisionStatus = new ArrayList<Status>();
					decisionStatus.add(approveStatus);
					decisionStatus.add(rejectStatus);
					model.addAttribute("internalStatuses", decisionStatus);
				} else {
					List<Status> internalStatuses = new ArrayList<Status>();
					HouseType houseType = HouseType.findByFieldName(HouseType.class, "name", strHouseType, strLocale);
					DeviceType motionType = DeviceType.findByFieldName(DeviceType.class, "name", strMotionType,
							strLocale);
					Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
					CustomParameter finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,
							motionType.getType().toUpperCase() + "_FINAL_AUTHORITY", "");
					CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter.findByName(
							CustomParameter.class,
							"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
									+ internalStatus.getType().toUpperCase() + "_" + strUserGroupType.toUpperCase(),
							"");
					CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter
							.findByName(CustomParameter.class,
									"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
											+ houseType.getType().toUpperCase() + "_" + strUserGroupType.toUpperCase(),
									"");
					CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class,
							"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
									+ strUserGroupType.toUpperCase(),
							"");
					if (finalApprovingAuthority != null
							&& finalApprovingAuthority.getValue().contains(strUserGroupType)) {
						CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(
								CustomParameter.class, "MOTION_PUT_UP_OPTIONS_" + strUserGroupType.toUpperCase(), "");
						if (finalApprovingAuthorityStatus != null) {
							internalStatuses = Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(),
									strLocale);
						}
					} /****
						 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+
						 * INTERNALSTATUS_TYPE+USERGROUP(Post Final Status)
						 ****/
					else if (deviceTypeInternalStatusUsergroup != null) {
						internalStatuses = Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(),
								strLocale);
					} /****
						 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+
						 * USERGROUP(Pre Final Status-House Type Basis)
						 ****/
					else if (deviceTypeHouseTypeUsergroup != null) {
						internalStatuses = Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(),
								strLocale);
					}
					/****
					 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre
					 * Final Status)
					 ****/
					else if (deviceTypeUsergroup != null) {
						internalStatuses = Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
					}
					model.addAttribute("internalStatuses", internalStatuses);
				}
				/**** Request Params To Model Attribute ****/
				model.addAttribute("houseType", strHouseType);
				model.addAttribute("sessionType", strSessionType);
				model.addAttribute("sessionYear", strSessionYear);
				model.addAttribute("motionType", strMotionType);
				model.addAttribute("status", strStatus);
				model.addAttribute("role", strRole);
				model.addAttribute("usergroup", strUsergroup);
				model.addAttribute("usergroupType", strUserGroupType);
				model.addAttribute("itemscount", strItemsCount);
				model.addAttribute("file", strFile);
				model.addAttribute("workflowSubType", strWorkflowSubType);
			}
			return "workflow/motion/bulkapprovalinit";
		} catch (ELSException ee) {
			logger.error(ee.getMessage());
			model.addAttribute("error", ee.getParameter());
			model.addAttribute("type", "error");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("type", "error");
		}
		return "workflow/info";
	}

	private void performActionOnClarificationNotReceived(Motion domain) {
		Status newStatus = Status.findByType(ApplicationConstants.MOTION_SYSTEM_ASSISTANT_PROCESSED,
				domain.getLocale());
		domain.setInternalStatus(newStatus);
		domain.setActor(null);
		domain.setLocalizedActorName("");
		domain.setWorkflowDetailsId(null);
		domain.setLevel("1");
		domain.setWorkflowStarted("NO");
		domain.setEndFlag(null);

	}

	// perform actions
	@RequestMapping(value = "/bulkapproval/view", method = RequestMethod.POST)
	public String getBulkApprovalView(final HttpServletRequest request, final Locale locale, final Model model) {
		populateBulkApprovalView(model, request, locale.toString());
		return "workflow/motion/bulkapprovalview";
	}

	@Transactional
	@RequestMapping(value = "/bulkapproval/update", method = RequestMethod.POST)
	public String bulkApproval(final HttpServletRequest request, final Locale locale, final Model model) {
		String[] selectedItems = request.getParameterValues("items[]");
		String strStatus = request.getParameter("aprstatus");
		String strWorkflowSubType = request.getParameter("workflowSubType");
		String strFile = request.getParameter("file");
		String remarks = request.getParameter("remarks");
		String refText = request.getParameter("refertext");

		StringBuffer recommendAdmissionMsg = new StringBuffer();
		StringBuffer recommendRejectionMsg = new StringBuffer();
		StringBuffer admittedMsg = new StringBuffer();
		StringBuffer rejectedMsg = new StringBuffer();
		if (selectedItems != null && (selectedItems.length > 0) && strStatus != null && !strStatus.isEmpty()
				&& strWorkflowSubType != null && !strWorkflowSubType.isEmpty()) {
			Status status = null;
			if (!strStatus.equals("-")) {
				status = Status.findById(Status.class, Long.parseLong(strStatus));
			}

			List<ReferenceUnit> refs = null;
			if (!strWorkflowSubType.equals("request_to_supporting_member")) {
				for (String i : selectedItems) {

					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, new Long(i));
					Motion question = Motion.findById(Motion.class, Long.parseLong(wfDetails.getDeviceId()));

					if (question != null) {
						if (question.getReferencedUnits() != null && !question.getReferencedUnits().isEmpty()) {
							refs = question.getReferencedUnits();
							break;
						}
					}
				}
			}

			for (String i : selectedItems) {
				if (strWorkflowSubType.equals("request_to_supporting_member")) {
					String[] temp = i.split("#");
					Long id = Long.parseLong(temp[0]);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, id);
					/**** Updating Supporting Member ****/
					SupportingMember supportingMember = SupportingMember.findById(SupportingMember.class,
							Long.parseLong(temp[1]));
					supportingMember.setApprovalDate(new Date());
					supportingMember.setApprovedSubject(wfDetails.getSubject());
					supportingMember.setApprovedText(wfDetails.getText());
					supportingMember.setDecisionStatus(status);
					/**** Remarks Need To Be Added ****/
					supportingMember.merge();
					/**** complete the task ****/
					String strTaskId = wfDetails.getTaskId();
					Task task = processService.findTaskById(strTaskId);
					processService.completeTask(task);
					/**** Update Workflow Details ****/
					wfDetails.setStatus("COMPLETED");
					wfDetails.setCompletionTime(new Date());
					/****
					 * In case of Supporting Member Approval Status should
					 * reflect member's actions
					 ****/
					wfDetails.setInternalStatus(status.getName());
					wfDetails.setRecommendationStatus(status.getName());
					wfDetails.merge();
				} else {
					Long id = Long.parseLong(i);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, id);
					Motion motion = Motion.findById(Motion.class, Long.parseLong(wfDetails.getDeviceId()));

					String actor = request.getParameter("actor");
					if (actor == null || actor.isEmpty()) {
						actor = motion.getActor();
						String[] temp = actor.split("#");
						actor = temp[1];
					}
					String level = request.getParameter("level");
					if (level == null || level.isEmpty()) {
						level = motion.getLevel();

					}

					if (actor != null && !actor.isEmpty() && level != null && !level.isEmpty()) {
						Reference reference = null;
						try {
							reference = UserGroup.findMotionActor(motion, actor, level, locale.toString());
						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
						if (reference != null && reference.getId() != null && !reference.getId().isEmpty()
								&& reference.getName() != null && !reference.getName().isEmpty()) {
							/**** Update Actor ****/
							String[] temp = reference.getId().split("#");
							motion.setActor(reference.getId());
							motion.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
							motion.setLevel(temp[2]);
							/****
							 * Update Internal Status and Recommendation Status
							 ****/
							// for handling send back in bulk approval
							/*
							 * if(status!=null){
							 * motion.setInternalStatus(status);
							 * motion.setRecommendationStatus(status); }
							 */
							if (status != null) {
								if (!status.getType().equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)
										&& !status.getType().equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
										&& !status.getType()
												.equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_DEPARTMENT)
										&& !status.getType()
												.equals(ApplicationConstants.MOTION_PROCESSED_SEND_TO_SECTIONOFFICER)) {
									motion.setInternalStatus(status);
								}
								motion.setRecommendationStatus(status);
								motion.setEndFlag("continue");
							}
							/**** Complete Task ****/
							Map<String, String> properties = new HashMap<String, String>();
							properties.put("pv_deviceId", String.valueOf(motion.getId()));
							properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
							properties.put("pv_user", temp[0]);
							properties.put("pv_endflag", motion.getEndFlag());
							UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
							String strTaskId = wfDetails.getTaskId();
							Task task = processService.findTaskById(strTaskId);
							processService.completeTask(task, properties);
							if (motion.getEndFlag() != null && !motion.getEndFlag().isEmpty()
									&& motion.getEndFlag().equals("continue")) {
								/**** Create New Workflow Details ****/
								ProcessInstance processInstance = processService
										.findProcessInstanceById(task.getProcessInstanceId());
								Workflow workflowFromUpdatedStatus = null;
								try {

									/*
									 * Added by Amit Desai 2 Dec 2014 START...
									 */
									/*
									 * if(question.getRecommendationStatus().
									 * getType().equals(ApplicationConstants.
									 * QUESTION_RECOMMEND_CLUBBING_POST_ADMISSION)
									 * || question.getRecommendationStatus().
									 * getType().equals(ApplicationConstants.
									 * QUESTION_RECOMMEND_UNCLUBBING) ||
									 * question.getRecommendationStatus().
									 * getType().equals(ApplicationConstants.
									 * QUESTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING
									 * )) { workflowFromUpdatedStatus =
									 * Workflow.findByStatus(question.
									 * getRecommendationStatus(),
									 * question.getLocale()); } else {
									 * workflowFromUpdatedStatus =
									 * Workflow.findByStatus(question.
									 * getInternalStatus(),
									 * question.getLocale()); }
									 */
									Status internalStatus = motion.getInternalStatus();
									String internalStatusType = internalStatus.getType();
									Status recommendationStatus = motion.getRecommendationStatus();
									String recommendationStatusType = recommendationStatus.getType();

									if (recommendationStatusType
											.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
											|| recommendationStatusType
													.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
											|| recommendationStatusType.equals(
													ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
										workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus,
												locale.toString());
									} else if (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)
											|| internalStatusType
													.equals(ApplicationConstants.MOTION_FINAL_NAME_CLUBBING)
											|| (internalStatusType
													.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType
															.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType
													.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
													&& recommendationStatusType
															.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
											|| (internalStatusType
													.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType
															.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
											|| (internalStatusType
													.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
													&& recommendationStatusType.equals(
															ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
										workflowFromUpdatedStatus = Workflow.findByStatus(internalStatus,
												locale.toString());
									} else {
										workflowFromUpdatedStatus = Workflow.findByStatus(internalStatus,
												locale.toString());
									}
									/*
									 * Added by Amit Desai 2 Dec 2014 ... END
									 */

								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error",
											"Bulk approval is unavailable please try after some time.");
									model.addAttribute("type", "error");
									return "workflow/info";
								}
								Task newtask = processService.getCurrentTask(processInstance);
								WorkflowDetails workflowDetails2 = null;
								try {
									workflowDetails2 = WorkflowDetails.create(motion, newtask, usergroupType,
											workflowFromUpdatedStatus.getType(), level);
								} catch (ELSException e) {
									e.printStackTrace();
									model.addAttribute("error", e.getParameter());
								}
								motion.setWorkflowDetailsId(workflowDetails2.getId());
								motion.setTaskReceivedOn(new Date());
							}
							/**** Update Old Workflow Details ****/
							wfDetails.setStatus("COMPLETED");
							wfDetails.setDecisionInternalStatus(motion.getInternalStatus().getName());
							wfDetails.setDecisionRecommendStatus(motion.getRecommendationStatus().getName());
							wfDetails.setCompletionTime(new Date());
							wfDetails.merge();
							/**** Update Motion ****/
							if (refs != null && !refs.isEmpty()) {
								List<ReferenceUnit> refActual = new ArrayList<ReferenceUnit>();
								for (ReferenceUnit ref : refs) {
									ref.setId(null);
									ref.setVersion(null);
									refActual.add(ref);
								}
								motion.setReferencedUnits(refActual);
							}
							if (remarks != null) {
								motion.setRemarks(remarks);
							}
							if (refText != null) {
								motion.setRefText(refText);
							}

							if (strFile != null && !strFile.isEmpty() && !strFile.equals("-")) {
								if (motion.getFile() == null) {
									motion.setFile(new Integer(strFile));
								}
							}

							motion.setEditedOn(new Date());
							motion.setEditedBy(this.getCurrentUser().getActualUsername());
							motion.setEditedAs(wfDetails.getAssigneeUserGroupName());
							performAction(motion);
							motion.merge();
							if (motion.getInternalStatus().getType()
									.equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION)) {
								recommendAdmissionMsg.append(motion.formatNumber() + ",");
							} else if (motion.getInternalStatus().getType()
									.equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION)) {
								recommendRejectionMsg.append(motion.formatNumber() + ",");
							} else if (motion.getInternalStatus().getType()
									.equals(ApplicationConstants.MOTION_FINAL_ADMISSION)) {
								admittedMsg.append(motion.formatNumber() + ",");
							} else if (motion.getInternalStatus().getType()
									.equals(ApplicationConstants.MOTION_FINAL_REJECTION)) {
								rejectedMsg.append(motion.formatNumber() + ",");
							}
						}
					}
				}
			}
		}
		model.addAttribute("recommendAdmission", recommendAdmissionMsg.toString());
		model.addAttribute("recommendRejection", recommendRejectionMsg.toString());
		model.addAttribute("admitted", admittedMsg.toString());
		model.addAttribute("rejected", rejectedMsg.toString());
		populateBulkApprovalView(model, request, locale.toString());
		return "workflow/motion/bulkapprovalview";
	}

	private void populateBulkApprovalView(final Model model, final HttpServletRequest request, final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strMotionType = request.getParameter("motionType");
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strItemsCount = request.getParameter("itemscount");
		String strFile = request.getParameter("file");
		String strWorkflowSubType = request.getParameter("workflowSubType");
		String strLocale = locale.toString();
		String assignee = this.getCurrentUser().getActualUsername();
		if (strHouseType != null && !(strHouseType.isEmpty()) && strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty()) && strMotionType != null
				&& !(strMotionType.isEmpty()) && strStatus != null && !(strStatus.isEmpty()) && strRole != null
				&& !(strRole.isEmpty()) && strUsergroup != null && !(strUsergroup.isEmpty()) && strUsergroupType != null
				&& !(strUsergroupType.isEmpty()) && strItemsCount != null && !(strItemsCount.isEmpty())
				&& strFile != null && !(strFile.isEmpty()) && strWorkflowSubType != null
				&& !(strWorkflowSubType.isEmpty())) {
			model.addAttribute("workflowSubType", strWorkflowSubType);
			/**** Workflow Details ****/
			List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();

			try {
				workflowDetails = WorkflowDetails.findAll(strHouseType, strSessionType, strSessionYear, strMotionType,
						strStatus, strWorkflowSubType, assignee, strItemsCount, strLocale, strFile);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Populating Bulk Approval VOs ****/
			List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
			NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
			int counter = 0;
			for (WorkflowDetails i : workflowDetails) {
				BulkApprovalVO bulkApprovalVO = new BulkApprovalVO();
				Motion motion = Motion.findById(Motion.class, Long.parseLong(i.getDeviceId()));
				/**** Bulk Submission for Supporting Members ****/
				if (i.getWorkflowSubType().equals("request_to_supporting_member")) {
					bulkApprovalVO.setId(String.valueOf(i.getId()));
					bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));
					bulkApprovalVO.setDeviceNumber("-");
					bulkApprovalVO.setDeviceType(motion.getType().getName());
					bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
					bulkApprovalVO.setSubject(motion.getSubject());
					if (motion.getRemarks() != null && !motion.getRemarks().isEmpty()) {
						bulkApprovalVO.setRemarks(motion.getRemarks());
					} else {
						bulkApprovalVO.setRemarks("-");
					}
					List<SupportingMember> supportingMembers = motion.getSupportingMembers();
					for (SupportingMember j : supportingMembers) {
						User user;
						try {
							user = User.findByUserName(i.getAssignee(), locale.toString());
							Member member = Member.findMember(user.getFirstName(), user.getMiddleName(),
									user.getLastName(), user.getBirthDate(), locale.toString());
							if (member != null && member.getId() == j.getMember().getId()) {
								bulkApprovalVO.setSupportingMemberId(String.valueOf(j.getId()));
							}

						} catch (ELSException e) {
							e.printStackTrace();
							model.addAttribute("error", e.getParameter());
						}
					}
					bulkApprovalVO.setCurrentStatus(i.getStatus());
					if (i.getInternalStatus() != null) {
						bulkApprovalVO.setLastDecision(i.getInternalStatus());
					} else {
						bulkApprovalVO.setLastDecision("-");
					}
					bulkapprovals.add(bulkApprovalVO);
				} else/**** Bulk Submission For Workflows ****/
				{
					/**** File Bulk Submission ****/
					if (strFile != null && !strFile.isEmpty() && !strFile.equals("-") && motion.getFile() != null
							&& motion.getFile() == Integer.parseInt(strFile)) {
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));
						if (motion.getNumber() != null) {
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						} else {
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						bulkApprovalVO.setSubject(motion.getSubject());
						if (motion.getRemarks() != null && !motion.getRemarks().isEmpty()) {
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						} else {
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					} /**** Status Wise Bulk Submission ****/
					else if (strFile != null && !strFile.isEmpty() && strFile.equals("-")) {
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));
						if (motion.getNumber() != null) {
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						} else {
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						if (motion.getRevisedDetails() != null && !motion.getRevisedDetails().isEmpty()) {
							bulkApprovalVO.setSubject(motion.getRevisedDetails());
						} else {
							bulkApprovalVO.setSubject(motion.getDetails());
						}

						if (motion.getRemarks() != null && !motion.getRemarks().isEmpty()) {
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						} else {
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);
					}
				}

				if (counter == 0) {
					model.addAttribute("apprLevel", motion.getLevel());
					counter++;
				}
			}
			model.addAttribute("bulkapprovals", bulkapprovals);
			if (bulkapprovals != null && !bulkapprovals.isEmpty()) {
				model.addAttribute("motionId", bulkapprovals.get(0).getDeviceId());
			}
		}
	}

	/**** Bulk Approval(By Any Authority) ****/
	@RequestMapping(value = "/advancedbulkapproval", method = RequestMethod.GET)
	public String getAdvancedBulkApproval(final HttpServletRequest request, final Locale locale, final ModelMap model) {
		try {
			/**** Request Params ****/
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strMotionType = request.getParameter("deviceType");
			String strStatus = request.getParameter("status");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			String strLocale = locale.toString();
			String strAnsweringDate = request.getParameter("answeringDate");
			String strGroup = request.getParameter("group");
			String assignee = this.getCurrentUser().getActualUsername();
			String strItemsCount = null;
			CustomParameter itemsCountParameter = CustomParameter.findByName(CustomParameter.class,
					"ADVANCED_BULKAPPROVAL_ITEM_COUNT", "");
			if (itemsCountParameter != null) {
				strItemsCount = itemsCountParameter.getValue();
			}
			/**** usergroup,usergroupType,role *****/
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			Credential credential = Credential.findByFieldName(Credential.class, "username",
					this.getCurrentUser().getActualUsername(), null);
			String strUserGroupType = null;
			String strUsergroup = null;
			UserGroup usergroup = null;
			if (userGroups != null && !userGroups.isEmpty()) {
				CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,
						"MOIS_ALLOWED_USERGROUPTYPES", "");
				if (customParameter != null) {
					String allowedUserGroups = customParameter.getValue();
					for (UserGroup i : userGroups) {
						UserGroup ug = UserGroup.findActive(credential, i.getUserGroupType(), new Date(),
								locale.toString());
						if (ug != null) {
							if (allowedUserGroups.contains(i.getUserGroupType().getType())) {
								strUsergroup = String.valueOf(i.getId());
								strUserGroupType = i.getUserGroupType().getType();
								usergroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUsergroup));
								break;
							}
						}
					}
				}
			}

			if (request.getSession().getAttribute("type") == null) {
				model.addAttribute("type", "");
			} else {
				model.addAttribute("type", request.getSession().getAttribute("type"));
				request.getSession().removeAttribute("type");
			}

			if (strHouseType != null && !(strHouseType.isEmpty()) && strSessionType != null
					&& !(strSessionType.isEmpty()) && strSessionYear != null && !(strSessionYear.isEmpty())
					&& strMotionType != null && !(strMotionType.isEmpty())) {
				CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER",
						"");
				if (customParameter != null) {
					String server = customParameter.getValue();
					if (server.equals("TOMCAT")) {
						try {
							strHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
							strSessionType = new String(strSessionType.getBytes("ISO-8859-1"), "UTF-8");
							strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"), "UTF-8");
							strMotionType = new String(strMotionType.getBytes("ISO-8859-1"), "UTF-8");
							strGroup = new String(strGroup.getBytes("ISO-8859-1"), "UTF-8");

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				strHouseType = request.getSession().getAttribute("houseType").toString();
				strSessionType = request.getSession().getAttribute("sessionType").toString();
				strSessionYear = request.getSession().getAttribute("sessionYear").toString();
				strMotionType = request.getSession().getAttribute("deviceType").toString();
				strWorkflowSubType = request.getSession().getAttribute("workflowSubType").toString();
				strStatus = request.getSession().getAttribute("status").toString();
				if (request.getSession().getAttribute("answeringDate") != null) {
					strAnsweringDate = request.getSession().getAttribute("answeringDate").toString();
				}
			}

			if (strHouseType != null && !(strHouseType.isEmpty()) && strSessionType != null
					&& !(strSessionType.isEmpty()) && strSessionYear != null && !(strSessionYear.isEmpty())
					&& strMotionType != null && !(strMotionType.isEmpty()) && strStatus != null
					&& !(strStatus.isEmpty()) && strUsergroup != null && !(strUsergroup.isEmpty())
					&& strUserGroupType != null && !(strUserGroupType.isEmpty()) && strWorkflowSubType != null
					&& !(strWorkflowSubType.isEmpty())) {

				model.addAttribute("status", strStatus);
				model.addAttribute("usergroup", usergroup.getId());
				// Populate Roles
				/**
				 * Rules: a. QIS roles starts with QIS_, MEMBER_ b. Any user
				 * will have single role per device type c. Any user can have
				 * multiple roles limited to one role per device type
				 */
				Set<Role> roles = this.getCurrentUser().getRoles();
				for (Role i : roles) {
					if (i.getType().startsWith("MEMBER_")) {
						model.addAttribute("role", i.getType());
						break;
					} else if (i.getType().startsWith("MOIS_")) {
						model.addAttribute("role", i.getType());
						break;
					}
				}
				/**** List of Statuses ****/
				List<Status> internalStatuses = new ArrayList<Status>();
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "name", strHouseType, strLocale);
				DeviceType motionType = DeviceType.findByFieldName(DeviceType.class, "name", strMotionType, strLocale);
				Status internalStatus = Status.findByType(strWorkflowSubType, strLocale);
				CustomParameter finalApprovingAuthority = CustomParameter.findByName(CustomParameter.class,
						motionType.getType().toUpperCase() + "_FINAL_AUTHORITY", "");
				CustomParameter deviceTypeInternalStatusUsergroup = CustomParameter
						.findByName(CustomParameter.class,
								"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
										+ internalStatus.getType().toUpperCase() + "_" + strUserGroupType.toUpperCase(),
								"");
				CustomParameter deviceTypeHouseTypeInternalStatusUsergroup = CustomParameter.findByName(
						CustomParameter.class,
						"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
								+ houseType.getType().toUpperCase() + "_" + internalStatus.getType().toUpperCase() + "_"
								+ strUserGroupType.toUpperCase(),
						"");
				CustomParameter deviceTypeHouseTypeUsergroup = CustomParameter.findByName(CustomParameter.class,
						"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
								+ houseType.getType().toUpperCase() + "_" + strUserGroupType.toUpperCase(),
						"");
				CustomParameter deviceTypeUsergroup = CustomParameter.findByName(CustomParameter.class,
						"MOTION_PUT_UP_OPTIONS_" + motionType.getType().toUpperCase() + "_"
								+ strUserGroupType.toUpperCase(),
						"");
				if (finalApprovingAuthority != null && finalApprovingAuthority.getValue().contains(strUserGroupType)) {
					CustomParameter finalApprovingAuthorityStatus = CustomParameter.findByName(CustomParameter.class,
							"MOTION_PUT_UP_OPTIONS_" + strUserGroupType.toUpperCase(), "");
					if (finalApprovingAuthorityStatus != null) {
						internalStatuses = Status.findStatusContainedIn(finalApprovingAuthorityStatus.getValue(),
								strLocale);
					}
				} /****
					 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+
					 * INTERNALSTATUS_TYPE+ USERGROUP(Post Final Status)
					 ****/
				else if (deviceTypeInternalStatusUsergroup != null) {
					internalStatuses = Status.findStatusContainedIn(deviceTypeInternalStatusUsergroup.getValue(),
							strLocale);
				} /****
					 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE + HOUSETYPE +
					 * INTERNALSTATUS_TYPE+USERGROUP(PRE Final Status)
					 ****/
				else if (deviceTypeHouseTypeInternalStatusUsergroup != null) {
					internalStatuses = Status
							.findStatusContainedIn(deviceTypeHouseTypeInternalStatusUsergroup.getValue(), strLocale);
				}
				/****
				 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+HOUSETYPE+USERGROUP(
				 * Pre Final Status-House Type Basis)
				 ****/
				else if (deviceTypeHouseTypeUsergroup != null) {
					internalStatuses = Status.findStatusContainedIn(deviceTypeHouseTypeUsergroup.getValue(), strLocale);
				}
				/****
				 * MOTION_PUT_UP_OPTIONS_+DEVICETYPE_TYPE+USERGROUP(Pre Final
				 * Status)
				 ****/
				else if (deviceTypeUsergroup != null) {
					internalStatuses = Status.findStatusContainedIn(deviceTypeUsergroup.getValue(), strLocale);
				}
				model.addAttribute("internalStatuses", internalStatuses);
				model.addAttribute("workflowSubType", strWorkflowSubType);
				Date answeringDate = null;
				if (strAnsweringDate != null && !strAnsweringDate.isEmpty()) {
					answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate,
							ApplicationConstants.DB_DATEFORMAT);
					model.addAttribute("answeringDate", strAnsweringDate);
				}
				/**** Workflow Details ****/
				List<WorkflowDetails> workflowDetails = WorkflowDetails.findAll(strHouseType, strSessionType,
						strSessionYear, strMotionType, strStatus, strWorkflowSubType, assignee, strItemsCount,
						strLocale, null, strGroup, answeringDate);
				/**** Populating Bulk Approval VOs ****/
				List<BulkApprovalVO> bulkapprovals = new ArrayList<BulkApprovalVO>();
				NumberFormat format = FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
				int counter = 0;
				for (WorkflowDetails i : workflowDetails) {
					BulkApprovalVO bulkApprovalVO = new BulkApprovalVO();
					Motion motion = Motion.findById(Motion.class, Long.parseLong(i.getDeviceId()));
					{
						bulkApprovalVO.setId(String.valueOf(i.getId()));
						bulkApprovalVO.setDeviceId(String.valueOf(motion.getId()));

						Map<String, String[]> parameters = new HashMap<String, String[]>();
						parameters.put("locale", new String[] { locale.toString() });
						parameters.put("motionId", new String[] { motion.getId().toString() });
						List clubbedNumbers = org.mkcl.els.domain.Query.findReport("MOIS_GET_CLUBBEDNUMBERS",
								parameters);
						if (clubbedNumbers != null && !clubbedNumbers.isEmpty() && clubbedNumbers.get(0) != null) {
							bulkApprovalVO.setFormattedClubbedNumbers(clubbedNumbers.get(0).toString());
						}

						List referencedNumbers = org.mkcl.els.domain.Query.findReport("MOIS_GET_REFERENCEDNUMBERS",
								parameters);
						if (referencedNumbers != null && !referencedNumbers.isEmpty()
								&& referencedNumbers.get(0) != null) {
							bulkApprovalVO.setFormattedReferencedNumbers(referencedNumbers.get(0).toString());
						}

						if (motion.getNumber() != null) {
							bulkApprovalVO.setDeviceNumber(format.format(motion.getNumber()));
						} else {
							bulkApprovalVO.setDeviceNumber("-");
						}
						bulkApprovalVO.setDeviceType(motion.getType().getName());
						bulkApprovalVO.setMember(motion.getPrimaryMember().getFullname());
						if (motion.getRevisedSubject() != null && !motion.getRevisedSubject().equals("")) {
							bulkApprovalVO.setSubject(motion.getRevisedSubject());
						} else {
							bulkApprovalVO.setSubject(motion.getSubject());
						}
						if (motion.getRevisedDetails() != null && !motion.getRevisedDetails().isEmpty()) {
							bulkApprovalVO.setBriefExpanation(motion.getRevisedDetails());
						} else {
							bulkApprovalVO.setBriefExpanation(motion.getDetails());
						}

						if (motion.getRemarks() != null && !motion.getRemarks().isEmpty()) {
							bulkApprovalVO.setLastRemark(motion.getRemarks());
						} else {
							bulkApprovalVO.setLastRemark("-");
						}
						bulkApprovalVO.setLastDecision(motion.getInternalStatus().getName());
						bulkApprovalVO.setLastRemarkBy(motion.getEditedAs());
						bulkApprovalVO.setCurrentStatus(i.getStatus());
						bulkapprovals.add(bulkApprovalVO);

						if (counter == 0) {
							model.addAttribute("level", motion.getLevel());
							counter++;
						}
					}

					model.addAttribute("bulkapprovals", bulkapprovals);
					if (bulkapprovals != null && !bulkapprovals.isEmpty()) {
						model.addAttribute("motionId", bulkapprovals.get(0).getDeviceId());
					}
				}
				model.addAttribute("deviceType", motionType.getId());
			}
			return "workflow/motion/advancedbulkapproval";
		} catch (ELSException ee) {
			model.addAttribute("error", ee.getParameter());
			model.addAttribute("type", "error");
			return "workflow/info";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
			model.addAttribute("type", "error");
			return "workflow/info";
		}
	}

	@RequestMapping(value = "/advancedbulkapproval", method = RequestMethod.POST)
	public String advancedBulkApproval(final HttpServletRequest request, final Locale locale,
			final RedirectAttributes redirectAttributes, final ModelMap model) throws ELSException {
		String listSize = request.getParameter("motionlistSize");
		Motion tempMotion = null;
		StringBuffer recommendAdmissionMsg = new StringBuffer();
		StringBuffer recommendRejectionMsg = new StringBuffer();
		StringBuffer recommendClarificationMsg = new StringBuffer();
		StringBuffer admittedMsg = new StringBuffer();
		StringBuffer rejectedMsg = new StringBuffer();
		StringBuffer clarificationMsg = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		if (listSize != null && !listSize.isEmpty()) {
			for (int i = 0; i < Integer.parseInt(listSize); i++) {
				try {
					String id = request.getParameter("motionId" + i);
					String subject = request.getParameter("subject" + i);
					String motionText = request.getParameter("motionText" + i);
					String actor = request.getParameter("actor" + i);
					String internalStatus = request.getParameter("internalStatus" + i);
					String remark = request.getParameter("remark" + i);
					String workflowDetailsId = request.getParameter("workflowDetailsId" + i);
					Long wrkflowId = Long.parseLong(workflowDetailsId);
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, wrkflowId);
					String strChecked = request.getParameter("chk" + workflowDetailsId);
					if (strChecked != null && !strChecked.isEmpty() && Boolean.parseBoolean(strChecked)) {
						Motion motion = Motion.findById(Motion.class, Long.parseLong(wfDetails.getDeviceId()));
						tempMotion = motion;
						if (motionText != null && !motionText.isEmpty()) {
							motion.setRevisedDetails(motionText);
						}
						if (remark != null && !remark.isEmpty()) {
							motion.setRemarks(remark);
						}
						if (subject != null && !subject.isEmpty()) {
							motion.setRevisedSubject(subject);
						}
						if (actor == null || actor.isEmpty()) {
							actor = motion.getActor();
							String[] temp = actor.split("#");
							actor = temp[1];
						}
						String level = request.getParameter("motionLevel");
						if (level == null || level.isEmpty()) {
							level = motion.getLevel();
						}

						/**** Update Actor ****/
						/*
						 * if(actor.equals("-")) continue;
						 */
						String[] temp = actor.split("#");
						motion.setActor(actor);
						motion.setLocalizedActorName(temp[3] + "(" + temp[4] + ")");
						motion.setLevel(temp[2]);
						/****
						 * Update Internal Status and Recommendation Status
						 ****/
						Status intStatus = Status.findById(Status.class, Long.parseLong(internalStatus));
						if (internalStatus != null) {
							if (!intStatus.getType().equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)
									&& !intStatus.getType().equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
		  
										  
								
																											
									   
								
																																																															
									   
								
																											 
									   
								
																												
									   
								
																																																		 
										  
								
																																																												
		   
																																																														  
																																																															
		  
										  
								
																											
									   
								
																																																												
		   
									&& !intStatus.getType().equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)
																																																															
		  
										  
								
																											
									   
								
																																																												
		   
									&& !intStatus.getType()
											.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)
									&& !intStatus.getType()
											.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED)) {
								motion.setInternalStatus(intStatus);
								motion.setRecommendationStatus(intStatus);
								motion.setEndFlag("continue");
							} else if (intStatus.getType()
									.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED)
									|| intStatus.getType()
											.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED)) {
								Status toBePutUp = Status.findByType(ApplicationConstants.MOTION_SYSTEM_TO_BE_PUTUP,
										locale.toString());
								motion.setInternalStatus(toBePutUp);
								motion.setRecommendationStatus(toBePutUp);
								motion.setEndFlag(null);
								motion.setActor(null);
								motion.setLevel(null);
								motion.setLocalizedActorName(null);
							} else {
								motion.setRecommendationStatus(intStatus);
								motion.setEndFlag("continue");
							}
						}

						/**** Complete Task ****/
						Map<String, String> properties = new HashMap<String, String>();
						properties.put("pv_deviceId", String.valueOf(motion.getId()));
						properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
						properties.put("pv_user", temp[0]);
						properties.put("pv_endflag", motion.getEndFlag());
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], locale.toString());
						String strTaskId = wfDetails.getTaskId();
						Task task = processService.findTaskById(strTaskId);
						processService.completeTask(task, properties);
						if (motion.getEndFlag() != null && !motion.getEndFlag().isEmpty()
								&& motion.getEndFlag().equals("continue")) {
							/**** Create New Workflow Details ****/
							ProcessInstance processInstance = processService
									.findProcessInstanceById(task.getProcessInstanceId());
							Workflow workflowFromUpdatedStatus = null;
							try {
								Status iStatus = motion.getInternalStatus();
								String internalStatusType = iStatus.getType();
								Status recommendationStatus = motion.getRecommendationStatus();
								String recommendationStatusType = recommendationStatus.getType();

								if (recommendationStatusType
										.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
										|| recommendationStatusType
												.equals(ApplicationConstants.MOTION_FINAL_CLUBBING_POST_ADMISSION)
										|| recommendationStatusType.equals(ApplicationConstants.MOTION_FINAL_UNCLUBBING)
										|| recommendationStatusType.equals(
												ApplicationConstants.MOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
									workflowFromUpdatedStatus = Workflow.findByStatus(recommendationStatus,
											locale.toString());
								} else if (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_CLUBBING)

										|| (internalStatusType
												.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType
														.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
										|| (internalStatusType
												.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
												&& recommendationStatusType
														.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))
										|| (internalStatusType
												.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType
														.equals(ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
										|| (internalStatusType
												.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
												&& recommendationStatusType.equals(
														ApplicationConstants.MOTION_PROCESSED_CLARIFICATION_RECEIVED))) {
									workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
								} else {
									workflowFromUpdatedStatus = Workflow.findByStatus(iStatus, locale.toString());
								}

							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", "Bulk approval is unavailable please try after some time.");
								model.addAttribute("type", "error");

							}
							Task newtask = processService.getCurrentTask(processInstance);
							WorkflowDetails workflowDetails2 = null;
							try {
								workflowDetails2 = WorkflowDetails.create(motion, newtask, usergroupType,
										workflowFromUpdatedStatus.getType(), level);
							} catch (ELSException e) {
								e.printStackTrace();
								model.addAttribute("error", e.getParameter());
							}
							motion.setWorkflowDetailsId(workflowDetails2.getId());
							motion.setTaskReceivedOn(new Date());
						}
						/**** Update Old Workflow Details ****/
						wfDetails.setStatus("COMPLETED");
						wfDetails.setDecisionInternalStatus(motion.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(motion.getRecommendationStatus().getName());
						wfDetails.setCompletionTime(new Date());
						if (!motion.getType().getType().startsWith("motions_halfhourdiscussion_")) {
							wfDetails.setAnsweringDate(motion.getAnsweringDate());
						}
						wfDetails.setDecisionInternalStatus(motion.getInternalStatus().getName());
						wfDetails.setDecisionRecommendStatus(motion.getRecommendationStatus().getName());
						wfDetails.merge();
						/**** Update Motion ****/
						motion.setEditedOn(new Date());
						motion.setEditedBy(this.getCurrentUser().getActualUsername());
						motion.setEditedAs(wfDetails.getAssigneeUserGroupName());
						performAction(motion);
						motion.merge();
						String internalStatusType = motion.getInternalStatus().getType();
						if (internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_ADMISSION))
							recommendAdmissionMsg.append(motion.formatNumber() + ",");
						else if (internalStatusType.equals(ApplicationConstants.MOTION_RECOMMEND_REJECTION))
							recommendRejectionMsg.append(motion.formatNumber() + ",");
						else if (internalStatusType
								.equals(ApplicationConstants.MOTION_RECOMMEND_CLARIFICATION_FROM_DEPARTMENT))
							recommendClarificationMsg.append(motion.formatNumber() + ",");
						else if (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_ADMISSION))
							admittedMsg.append(motion.formatNumber() + ",");
						else if (internalStatusType.equals(ApplicationConstants.MOTION_FINAL_REJECTION))
							rejectedMsg.append(motion.formatNumber() + ",");
						else if (internalStatusType
								.equals(ApplicationConstants.MOTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT))
							clarificationMsg.append(motion.formatNumber() + ",");

						
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorMsg.append(tempMotion.formatNumber() + ",");
					continue;
				}
			}

			if (tempMotion != null) {
				request.getSession().setAttribute("houseType", tempMotion.getHouseType().getName());
				request.getSession().setAttribute("sessionType", tempMotion.getSession().getType().getSessionType());
				request.getSession().setAttribute("sessionYear",
						FormaterUtil.formatNumberNoGrouping(tempMotion.getSession().getYear(), locale.toString()));
				request.getSession().setAttribute("deviceType", tempMotion.getType().getName());
				request.getSession().setAttribute("workflowSubType", tempMotion.getInternalStatus().getType());

			}
			String group = request.getParameter("group");
			if (group != null && !group.isEmpty()) {
				request.getSession().setAttribute("strGroup", group);
			}
			String answeringDate = request.getParameter("answeringDate");
			if (answeringDate != null && !answeringDate.isEmpty()) {
				request.getSession().setAttribute("strAnsweringDate", answeringDate);
			}
			String status = request.getParameter("status");
			if (status != null && !status.isEmpty()) {
				request.getSession().setAttribute("status", status);
			}
		}
		request.getSession().setAttribute("recommendAdmission", recommendAdmissionMsg.toString());
		request.getSession().setAttribute("recommendClarification", recommendClarificationMsg.toString());
		request.getSession().setAttribute("recommendRejection", recommendRejectionMsg.toString());
		request.getSession().setAttribute("admitted", admittedMsg.toString());
		request.getSession().setAttribute("rejected", rejectedMsg.toString());
		request.getSession().setAttribute("clarification", clarificationMsg.toString());
		request.getSession().setAttribute("errorMsg", errorMsg.toString());
		
		redirectAttributes.addFlashAttribute("type", "success");
						// this is done so as to remove the bug due to which
						// update
						// message appears even though there
						// is a fresh new/edit request i.e after
						// creating/updating
						// records if we click on
						// new /edit then success message appears
						request.getSession().setAttribute("type", "success");
						redirectAttributes.addFlashAttribute("msg", "create_success");
		String returnUrl = "redirect:/workflow/motion/advancedbulkapproval";
		return returnUrl;
		// getAdvancedBulkApproval(request, locale, model);
	}

	@SuppressWarnings("rawtypes")
	private void findLatestRemarksByUserGroup(final Motion domain, final ModelMap model,
			final HttpServletRequest request, final WorkflowDetails workflowDetails) throws ELSException {
		UserGroupType userGroupType = null;
		String username = this.getCurrentUser().getUsername();
		Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
		List<UserGroup> ugroups = this.getCurrentUser().getUserGroups();
		for (UserGroup ug : ugroups) {
			UserGroup usergroup = UserGroup.findActive(credential, ug.getUserGroupType(), domain.getSubmissionDate(),
					domain.getLocale());
			if (usergroup != null) {
				userGroupType = usergroup.getUserGroupType();
				break;
			}
		}
		if (userGroupType == null || (!userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				&& !userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))) {
			userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.ASSISTANT,
					domain.getLocale());
		} else {
			userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", ApplicationConstants.DEPARTMENT,
					domain.getLocale());
		}
		Map<String, String[]> requestMap = new HashMap<String, String[]>();
		requestMap.put("motionId", new String[] { String.valueOf(domain.getId()) });
		requestMap.put("locale", new String[] { domain.getLocale() });
		if (userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)
				|| userGroupType.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)) {
			List result = Query.findReport("MOIS_LATEST_REVISION_FOR_DESKOFFICER", requestMap);
			model.addAttribute("latestRevisions", result);
		} else {
			List result = Query.findReport("MOTION_GET_REVISION", requestMap);
			model.addAttribute("latestRevisions", result);
		}
		model.addAttribute("startingActor", userGroupType.getName());
	}

}
