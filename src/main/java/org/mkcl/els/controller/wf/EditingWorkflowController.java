package org.mkcl.els.controller.wf;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.edis.EditingController;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.PartDraft;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/workflow/editing")
public class EditingWorkflowController extends BaseController{

	@Autowired
	private IProcessService processService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String initMyTask(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		WorkflowDetails workflowDetails = null;
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			String strStatus = request.getParameter("selectedSubWorkflow");
			String strAction = request.getParameter("action");
			
			String[] decodedStrings = EditingWorkflowUtility.getDecodedString(new String[]{strHouseType, strSessionType, strSessionYear});
			strHouseType = decodedStrings[0];
			strSessionType = decodedStrings[1];
			strSessionYear = decodedStrings[2];
			
			/**** Workflowdetails ****/
			Long longWorkflowdetails = (Long) request.getAttribute("workflowdetails");
			if(longWorkflowdetails == null){
				longWorkflowdetails = Long.parseLong(request.getParameter("workflowdetails"));
			}
			workflowDetails = WorkflowDetails.findById(WorkflowDetails.class,longWorkflowdetails);
			
			HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
			UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
			Status status = Status.findByType(workflowDetails.getWorkflowSubType(), locale.toString());
			
			model.addAttribute("workflowdetails",workflowDetails.getId());
			model.addAttribute("workflowstatus",workflowDetails.getStatus());
			Roster roster = Roster.findById(Roster.class,Long.parseLong(workflowDetails.getDeviceId()));
			Member member = null;
			List parts = null;
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			if(workflowDetails != null ){
				if(strUserGroupType.equals(ApplicationConstants.MEMBER)){
					member = Member.findMember(this.getCurrentUser().getFirstName(), this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(), this.getCurrentUser().getBirthDate(), locale.toString());
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("rosterId", new String[]{workflowDetails.getDeviceId()});
					parameters.put("primaryMemberId", new String[]{member.getId().toString()});
					parts = Query.findReport("EDIS_WORKFLOW_MEMBER_SENT_DRAFTS_DESC", parameters);
					
				}else if(strUserGroupType.equals(ApplicationConstants.EDITOR)){
					if(status.getType().equals(ApplicationConstants.EDITING_FINAL_MEMBERAPPROVAL)){
						parameters.put("locale", new String[]{locale.toString()});
						parameters.put("rosterId", new String[]{workflowDetails.getDeviceId()});
						UserGroup assignerUserGroup = UserGroup.findById(UserGroup.class, Long.valueOf(workflowDetails.getAssignerUserGroupId()));
						User user = EditingWorkflowUtility.getUser(assignerUserGroup, locale.toString());
						member = Member.findMember(user.getFirstName(),user.getMiddleName(), user.getLastName(), user.getBirthDate(), locale.toString());
						parameters.put("primaryMemberId", new String[]{member.getId().toString()});
						parameters.put("editedby", new String[]{workflowDetails.getAssigner()});
						parts = Query.findReport("EDIS_WORKFLOW_MEMBER_SENT_DRAFTS_DESC", parameters);
					}else if(status.getType().equals(ApplicationConstants.EDITING_FINAL_SPEAKERAPPROVAL)){
						parameters.put("locale", new String[]{locale.toString()});
						parameters.put("rosterId", new String[]{workflowDetails.getDeviceId()});
						parameters.put("editedby", new String[]{workflowDetails.getAssigner()});
						parts = Query.findReport("EDIS_WORKFLOW_SPEAKER_SENT_DRAFTS_DESC", parameters);
					}
					
				}else{					
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("rosterId", new String[]{workflowDetails.getDeviceId()});
					parameters.put("editedby", new String[]{workflowDetails.getAssigner()});
					parts = Query.findReport("EDIS_WORKFLOW_SPEAKER_SENT_DRAFTS_DESC", parameters);
					
				}
			}
			model.addAttribute("statuses", EditingWorkflowUtility.getStatusesForActor(request, workflowDetails, locale));			
			model.addAttribute("actors", EditingWorkflowUtility.getActors(houseType, userGroup, status, workflowDetails, locale.toString()));
			model.addAttribute("parts", parts);
			model.addAttribute("action", strAction);
			model.addAttribute("username", this.getCurrentUser().getActualUsername());
			/**** Populate Model ****/		
			populateModel(roster,model,request,workflowDetails);
			
		}catch (ELSException e1) {
			model.addAttribute("error", e1.getParameter());
		}catch (Exception e) {
			String message = e.getMessage();
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			model.addAttribute("error", message);
			e.printStackTrace();
		}
		
		return workflowDetails.getForm();
	}
	
	@Transactional
	@RequestMapping(value="/savepart/{id}", method=RequestMethod.POST)
	public @ResponseBody String saveDraft(@PathVariable(value="id") Long id, HttpServletRequest request, Locale locale){
		String retVal = "FAILURE";
		try{
			Part part = Part.findById(Part.class, id);
			String editedContent = request.getParameter("editedContent");
			String strUserGroupType = request.getParameter("userGroupType");
			UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, "type", strUserGroupType, locale.toString());
			if(part != null){
				/****Create the part draft****/
				AuthUser user = this.getCurrentUser();
				
				PartDraft draft = new PartDraft();
				draft.setEditedBy(user.getActualUsername());
				draft.setEditedAs(userGroupType.getName());
				draft.setEditedOn(new Date());
				draft.setLocale(part.getLocale());
				draft.setMainHeading(part.getMainHeading());
				draft.setPageHeading(part.getPageHeading());
				draft.setRevisedContent(editedContent);
				draft.setWorkflowCopy(true);
				
				part.getPartDrafts().add(draft);				
				part.merge();
				
				retVal = "SUCCESS"; 
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	@Transactional
	@RequestMapping(value="/mergedraftcontent",method=RequestMethod.POST)
	public @ResponseBody String mergeDraftWithPart(HttpServletRequest request, Locale locale){
		String flag = "FAIL";
		try{
			String strAction = request.getParameter("action");
			if(strAction.equals("edit")){
				String strPart = request.getParameter("partId");
				String strEditedContent = request.getParameter("editedContent");
				
				String finalEditedContent = strEditedContent.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
				
				//strEditedContent = EditingWorkflowUtility.getDecodedString(new String[]{strEditedContent})[0];
				
				Part part = Part.findById(Part.class, Long.valueOf(strPart));				
				
				part.setEditedContent(finalEditedContent);
				part.merge();
				flag = "SUCCESS";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	@Transactional
	@RequestMapping(value="/replace",method=RequestMethod.POST)
	public @ResponseBody List doReplace(HttpServletRequest request, HttpServletResponse response, ModelMap model, Locale locale){
		List matchedParts = null;
		
		try{			
			/*model.addAttribute("searchTerm", domain.getSearchTerm());
			model.addAttribute("replaceTerm", domain.getReplaceTerm());
			model.addAttribute("undoCount", (domain.getUndoCount() + 1));
			domain.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
			domain.setUniqueIdentifierForRedo(UUID.randomUUID().toString());
			model.addAttribute("uniqueIdentifierForUndo", domain.getUniqueIdentifierForUndo());
			model.addAttribute("uniqueIdentifierForRedo", domain.getUniqueIdentifierForRedo());
			model.addAttribute("undoCount", (domain.getUndoCount() + 1));
			model.addAttribute("redoCount", (domain.getRedoCount() + 1));*/
			
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strWorkflowDetailsId = request.getParameter("workflowDetailsId");
			
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			
			String strSearchTerm = request.getParameter("searchTerm");
			String strReplaceTerm = request.getParameter("replaceTerm");
			
			String strUndoCount = request.getParameter("undoCount");
			String strRedoCount = request.getParameter("redoCount");
			Integer undoCount = Integer.valueOf(strUndoCount);
			Integer redoCount = Integer.valueOf(strRedoCount);
			
			if (strHouseType != null && !strHouseType.equals("")
					&& strSessionType != null && !strSessionType.equals("")
					&& strSessionYear != null && !strSessionYear.equals("")
					&& strWorkflowDetailsId != null && !strWorkflowDetailsId.isEmpty()) {
	
				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType,locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, Integer.parseInt(strSessionYear));
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
				UserGroupType userGroupType = userGroup.getUserGroupType();
				WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.valueOf(strWorkflowDetailsId));
				Roster roster = Roster.findById(Roster.class, Long.valueOf(wfDetails.getDeviceId()));
				
				matchedParts = Part.findAllEligibleForReplacement(roster, strSearchTerm, strReplaceTerm, locale.toString()); 
						//Part.findAllPartRosterSearchTerm(roster, domain.getSearchTerm(), locale.toString());//Query.findReport("EDIS_MATCHING_PARTS_FOR_REPLACEMENT", parametersMap);
				
				if(matchedParts != null && !matchedParts.isEmpty()){
					for(int i = 0; i < matchedParts.size(); i++){
						
						Object[] objArr = (Object[])matchedParts.get(i);
						if(!objArr[3].toString().equals(objArr[4].toString())){
							Part partToBeReplaced = Part.findById(Part.class, Long.valueOf(objArr[0].toString()));
							/****Create draft****/
							PartDraft pd = new PartDraft();
							pd.setEditedBy(this.getCurrentUser().getActualUsername());
							pd.setEditedAs(userGroupType.getName());
							pd.setEditedOn(new Date());
							pd.setLocale(locale.toString());
							pd.setMainHeading(partToBeReplaced.getMainHeading());
							pd.setPageHeading(partToBeReplaced.getPageHeading());
							pd.setOriginalText(objArr[3].toString());
							pd.setReplacedText(objArr[4].toString());
							pd.setRevisedContent(objArr[4].toString());
							pd.setUndoCount(undoCount);
							pd.setRedoCount(redoCount);
							pd.setUniqueIdentifierForUndo(UUID.randomUUID().toString());
							pd.setUniqueIdentifierForRedo(UUID.randomUUID().toString());
							pd.setWorkflowCopy(true);
							
							/****Attach undoCount and undoUID in the result list****/
							((Object[])matchedParts.get(i))[5] = partToBeReplaced.getId().toString()+":"+pd.getUndoCount()+":"+pd.getUniqueIdentifierForUndo();
							((Object[])matchedParts.get(i))[6] = partToBeReplaced.getId().toString()+":"+pd.getRedoCount()+":"+pd.getUniqueIdentifierForRedo();
							((Object[])matchedParts.get(i))[7] = "include";
							
							partToBeReplaced.getPartDrafts().add(pd);
							partToBeReplaced.merge();
						}else{
							((Object[])matchedParts.get(i))[7] = "exclude";
						}
					}
				}
			}		
						
		}catch (Exception e) {
			e.printStackTrace();
		}
		return matchedParts;
	}
	
	public static List<MasterVO> getEditingActors(HttpServletRequest request, Locale locale){
		try{
								
			return EditingWorkflowUtility.getActors(request, locale.toString());
			
		}catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static User getUser(UserGroup ug, String locale){
		return EditingWorkflowUtility.getUser(ug, locale);
	}
	
	private void populateModel(final Roster roster, 
							final ModelMap model,
							final HttpServletRequest request,
							final WorkflowDetails workflowDetails) throws ELSException{
		
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST)
	public String updateMyTask(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale){
		String form = "editing/error";
		
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			String strStatus = request.getParameter("selectedSubWorkflow"); 
			
			String[] decodedStrings = EditingWorkflowUtility.getDecodedString(new String[]{strHouseType, strSessionType, strSessionYear});
			strHouseType = decodedStrings[0];
			strSessionType = decodedStrings[1];
			strSessionYear = decodedStrings[2]; 
			
			/****Current WorkflowDetails ****/
			WorkflowDetails currentWorkflow = WorkflowDetails.findById(WorkflowDetails.class, Long.valueOf(request.getParameter("workflowDetailsId")));
			
			/****To create session ****/
			HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
			SessionType sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
			Integer sessionYear = Integer.valueOf(strSessionYear);
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			
			UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
			Status status = null;
			
			String desStrStatus = request.getParameter("decissiveStatus");
			if(desStrStatus != null){
				status = Status.findByType(desStrStatus, locale.toString());
			}
			
			if(status == null){
				status = Status.findByType(strStatus, locale.toString());
			}			
			
			Map<String, String> properties = new HashMap<String, String>();
			WorkflowActor wfActor = WorkflowConfig.findNextEditingActor(houseType, userGroup, status, EditingWorkflowUtility.getFullWorkflowName(status), Integer.parseInt(currentWorkflow.getAssigneeLevel()), locale.toString());
			if (wfActor != null) {
				User user = EditingWorkflowUtility.getUser(wfActor, houseType, locale.toString());
				Credential credential = user.getCredential();
				properties.put("pv_user", credential.getUsername());
				properties.put("pv_endflag", "continue");
			} else {
				properties.put("pv_user", "");
				properties.put("pv_endflag", "end");
			}
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("locale", locale.toString());
			parameters.put("assignee", currentWorkflow.getAssignee());
			parameters.put("status", "PENDING");
			parameters.put("processId", currentWorkflow.getProcessId());
			
			Task prevTask = processService.findTaskById(currentWorkflow.getTaskId());
			processService.completeTask(prevTask, parameters);
			
			if(wfActor != null){
				ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
				ProcessInstance processInstance = processService.createProcessInstance(processDefinition, properties);
				Task task = processService.getCurrentTask(processInstance);
				WorkflowDetails wfDetails = EditingWorkflowUtility.create(currentWorkflow, Long.valueOf(currentWorkflow.getDeviceId()), session, status, task, EditingWorkflowUtility.getFullWorkflowName(status), wfActor.getLevel().toString(), locale.toString());
			}
			
			
			currentWorkflow.setStatus("COMPLETED");
			currentWorkflow.setCompletionTime(new Date());
			currentWorkflow.merge();
			/**** display message ****/
			model.addAttribute("type","taskcompleted");
			form = "workflow/info";
		}catch(Exception e){
			e.printStackTrace();
		}
		return form;
	}
}


class EditingWorkflowUtility{
	private static Logger logger = LoggerFactory.getLogger(EditingWorkflowUtility.class);
	
	public static List<MasterVO> getActors(final HouseType houseType,
				final UserGroup userGroup,
				final Status status,
				final WorkflowDetails workflowDetails,
				final String locale){
		List<WorkflowActor> actors = WorkflowConfig.findEditingActors(houseType, userGroup, status, getFullWorkflowName(status), Integer.parseInt(workflowDetails.getAssigneeLevel()), locale);
		List<MasterVO> actorsVO = new ArrayList<MasterVO>();
		for(WorkflowActor wfa : actors){
			MasterVO mvo = new MasterVO();
			User user = getUser(wfa, houseType, locale);
			String value = wfa.getId()+";"+concat(new String[]{user.getTitle(),user.getFirstName(),user.getMiddleName(), user.getLastName()}, " ");
			mvo.setValue(value);
			mvo.setName(getUserGroup(wfa, houseType, locale).getUserGroupType().getName());
			actorsVO.add(mvo);
		}
		
		return actorsVO;
	}
	
	public static List<MasterVO> getActors(HttpServletRequest request, String locale){
		String strHouseType = request.getParameter("houseType");
		String strUserGroup = request.getParameter("userGroup");
		WorkflowDetails workflowDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.valueOf(request.getParameter("workflowDetailsId")));
		String strStatus = request.getParameter("selectedSubWorkflow");
		
		String[] values = getDecodedString(new String[]{strHouseType});
		strHouseType = values[0];
		
		HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
		UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.valueOf(strUserGroup));
		Status status = Status.findByType(strStatus, locale.toString());
				
		return EditingWorkflowUtility.getActors(houseType, userGroup, status, workflowDetails, locale.toString());
	}
	
	public static List<MasterVO> getStatusesForActor(HttpServletRequest request, WorkflowDetails currentWorkflow, Locale locale){
		List<MasterVO> statuses = new ArrayList<MasterVO>();
		
		try{
			String strUserGroup = request.getParameter("userGroup");
			String strUserGroupType = request.getParameter("userGroupType");
			
			CustomParameter actorsAllowedStatusesUserGroup = CustomParameter.findByName(CustomParameter.class, "EDITING_PUTUP_OPTIONS_"+currentWorkflow.getWorkflowSubType().toUpperCase()+"_"+strUserGroupType.toUpperCase(), "");
			CustomParameter actorsAllowedStatusesDefualt = null;
			CustomParameter actorsStatuses = null;
			
			if(actorsAllowedStatusesUserGroup != null){
				actorsStatuses = actorsAllowedStatusesUserGroup;
			}else{
				actorsAllowedStatusesDefualt = CustomParameter.findByName(CustomParameter.class, "EDITING_PUTUP_OPTIONS_"+currentWorkflow.getWorkflowSubType().toUpperCase()+"_BY_DEFAULT", "");
			}
			
			actorsStatuses = actorsAllowedStatusesDefualt;
			
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
						values[i] = new String(values[i].getBytes("ISO-8859-1"), "UTF-8");
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
	
	public static WorkflowDetails create(final WorkflowDetails currentWorkflowDetails,
			final Long rosterId, 
			final Session session,
			final Status status,
			final Task task,
			final String workflowType,
			final String assigneeLevel,
			final String locale) throws ELSException{
		
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username = task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, locale);
					userGroupId=String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					
					
					workflowDetails.setLocale(locale);
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					
					workflowDetails.setAssigner(currentWorkflowDetails.getAssignee());
					workflowDetails.setAssignerUserGroupId(currentWorkflowDetails.getAssigneeUserGroupId());
					workflowDetails.setAssignerUserGroupType(currentWorkflowDetails.getAssigneeUserGroupType());
					
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					
					workflowDetails.setSessionType(session.getType().getSessionType());
					workflowDetails.setSessionYear(FormaterUtil.formatNumberNoGrouping(session.getYear(), locale));
					workflowDetails.setHouseType(session.getHouse().getType().getName());
					
					workflowDetails.setUrlPattern("workflow/editing");
					workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
					workflowDetails.setModule("EDITING");
					workflowDetails.setDeviceType("");
					workflowDetails.setDeviceId(rosterId.toString());
					
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					workflowDetails.setWorkflowSubType(status.getType());
					
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
	
	public static List<WorkflowDetails> create(final Long rosterId,
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
							UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, locale);
							userGroupId=String.valueOf(userGroup.getId());
							userGroupType=userGroup.getUserGroupType().getType();
							userGroupName=userGroup.getUserGroupType().getName();
							workflowDetails.setAssignee(i.getAssignee());
							workflowDetails.setAssigneeUserGroupId(userGroupId);
							workflowDetails.setAssigneeUserGroupType(userGroupType);
							workflowDetails.setAssigneeUserGroupName(userGroupName);
							workflowDetails.setAssigneeLevel(assigneeLevel);
							if(i.getCreateTime()!=null){
								if(!i.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(i.getCreateTime()));
								}
							}
							
							
							workflowDetails.setDeviceId(rosterId.toString());
							
							workflowDetails.setHouseType(session.getHouse().getType().getName());								
							workflowDetails.setSessionType(session.getType().getSessionType());								
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(locale).format(session.getYear()));
								
								
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							
							workflowDetails.setUrlPattern("workflow/editing");
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							workflowDetails.setModule("EDITING");
							workflowDetails.setDeviceType("");
							workflowDetails.setDeviceId(rosterId.toString());
							
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
			elsException.setParameter("Editing create workflowdetails", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}	

	public static  User getUser(final WorkflowActor wfActor, final HouseType houseType, final String locale) {
		UserGroup userGroup = getUserGroup(wfActor, houseType, locale);
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
			final String locale) {
		List<UserGroup> userGroups = getUserGroups(wfActor, locale);		
		UserGroup userGroup = getEligibleUserGroup(userGroups, houseType, true, locale);
		if(userGroup != null) {
			return userGroup;
		}
		
		return null;
	}
	public static List<UserGroup> getUserGroups(
			final WorkflowActor workflowActor,
			final String locale) {
		UserGroupType userGroupType = workflowActor.getUserGroupType();
		
		List<UserGroup> userGroups = 
			UserGroup.findAllByFieldName(UserGroup.class, "userGroupType", 
					userGroupType, "activeFrom", ApplicationConstants.DESC, 
					locale);
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
	
	public static UserGroup getEligibleUserGroup(List<UserGroup> userGroups,
			final HouseType houseType,
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
			
			
			// ug must be active
			boolean flag2 = false;
			Date fromDate = ug.getActiveFrom();
			Date toDate = ug.getActiveTo();
			Date currentDate = new Date();
			if((fromDate == null || currentDate.after(fromDate) ||currentDate.equals(fromDate))
					&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate))) {
				flag2 = true;
			}
			
			boolean flag3 = isEditingRoleAssigned(ug);
			
			
			// if all cases are met then return user
			if(flag1 && flag2 && flag3) {
				return ug;
			}
		}
		
		return null;
	}
	
	private static boolean isEditingRoleAssigned(UserGroup userGroup){
		Credential credential = userGroup.getCredential();
		Set<org.mkcl.els.domain.Role> roles = credential.getRoles();
		
		for(org.mkcl.els.domain.Role r : roles){
			if(r != null){
				if(r.getType().startsWith("EDIS")){
					return true;
				}
			}
		}
		
		return false;
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
				
		WorkflowActor wfActor = getNextActor(request, userGroup, houseType, assigneeLevel, locale);
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
		wfDetails.setModule(ApplicationConstants.EDITING);
		wfDetails.setLocale(locale);
		
		wfDetails.persist();
		return wfDetails;
	}
	
	
	public static WorkflowActor getNextActor(final HttpServletRequest request,
			final UserGroup userGroup,
			final HouseType houseType,
			final Integer assigneeLevel,
			final String locale) {
		WorkflowActor wfActor = null;
		
		String strWFActorId = request.getParameter("actor");
		
		Status status = getStatus(request);
		String wfName = getFullWorkflowName(status);
			
		wfActor = WorkflowConfig.findNextEditingActor(houseType, userGroup, status, wfName, assigneeLevel, locale);
		
		
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
			final HouseType houseType,
			final Status status,
			final Integer currentActorLevel,
			final String urlPattern,
			final String locale) {
		WorkflowDetails wfDetails = new WorkflowDetails();
		
		// Workflow parameters
		String wfName = null;
		String wfSubType =  null;
		Date assignmentTime = new Date();
		wfDetails.setProcessId(task.getProcessInstanceId());
		wfDetails.setTaskId(task.getId());
		wfDetails.setWorkflowType(wfName);
		wfDetails.setWorkflowSubType(wfSubType);
		wfDetails.setAssignmentTime(assignmentTime);
		wfDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
		
		// User parameters
		// Not applicable parameters: nextWorkflowActorId
		WorkflowActor nextActor = null;//CommitteeWFUtility.getNextActor(request,currentActorUserGroup, houseType,currentActorLevel, locale);
		UserGroup nextUserGroup = null;//CommitteeWFUtility.getUserGroup(nextActor, houseType, locale);
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
}

