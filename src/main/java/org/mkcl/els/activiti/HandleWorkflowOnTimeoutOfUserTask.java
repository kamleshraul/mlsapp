package org.mkcl.els.activiti;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.impl.ActivitiServiceImpl;

public class HandleWorkflowOnTimeoutOfUserTask extends ActivitiServiceImpl {
	
	@Override
	public void execute(ActivityExecution execution) throws Exception {
		super.execute(execution);
		
		//device type
		String deviceTypeId = String.valueOf(execution.getVariable("pv_deviceTypeId"));		
		DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeId));	
		
		//device
		Device domain = null;			
		String deviceId = String.valueOf(execution.getVariable("pv_deviceId"));		
		if(deviceType.getType().startsWith("resolutions")) {
			domain = Resolution.findById(Resolution.class, Long.parseLong(deviceId));
		} else if(deviceType.getType().startsWith("questions")) {
			domain = Question.findById(Question.class, Long.parseLong(deviceId));
		} else if(deviceType.getType().startsWith("bills")) {
			domain = Bill.findById(Bill.class, Long.parseLong(deviceId));
		} else if(deviceType.getType().equals(ApplicationConstants.BILLAMENDMENT_MOTION)) {
			domain = BillAmendmentMotion.findById(BillAmendmentMotion.class, Long.parseLong(deviceId));
		}
		
		//houseType
		HouseType houseTypeForWorkflow = null;
		if(deviceType.getType().startsWith("resolutions")) {
			if(deviceType.getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", String.valueOf(execution.getVariable("pv_houseType")), domain.getLocale());
			} else {
				houseTypeForWorkflow = ((Resolution) domain).getHouseType();
			}
		}		
		
		//current workflow details
		WorkflowDetails currentWorkflowDetails = null;
		if(deviceType.getType().startsWith("resolutions")) {	
			currentWorkflowDetails = WorkflowDetails.findCurrentWorkflowDetail((Resolution) domain, houseTypeForWorkflow.getName());
		} else if(deviceType.getType().startsWith("questions")) {			
			currentWorkflowDetails = WorkflowDetails.findCurrentWorkflowDetail((Question) domain);
		} else if(deviceType.getType().startsWith("bills")) {	
			Bill bill = (Bill) domain;
			currentWorkflowDetails = WorkflowDetails.findCurrentWorkflowDetail(bill, String.valueOf(execution.getVariable("pv_workflowtype")));
		} else if(deviceType.getType().equals(ApplicationConstants.BILLAMENDMENT_MOTION)) {	
			BillAmendmentMotion billAmendmentMotion = (BillAmendmentMotion) domain;
			currentWorkflowDetails = WorkflowDetails.findCurrentWorkflowDetail(billAmendmentMotion, String.valueOf(execution.getVariable("pv_workflowtype")));
		}		
		
		//current level
		String currentLevel = currentWorkflowDetails.getAssigneeLevel();	
		
		//usergroup
		String strUserGroup=currentWorkflowDetails.getAssigneeUserGroupId();
		UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
		
		//current internal status
		Status internalStatus = null;
		if(deviceType.getType().startsWith("resolutions")) {
			if(houseTypeForWorkflow.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				Resolution resolution = (Resolution) domain;
				internalStatus = resolution.getInternalStatusLowerHouse();
			} else if(houseTypeForWorkflow.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
				Resolution resolution = (Resolution) domain;
				internalStatus = resolution.getInternalStatusUpperHouse();
			}
		} else if(deviceType.getType().startsWith("questions")) {
			Question question = (Question) domain;
			internalStatus = question.getInternalStatus();
		}
		
		//keep workflow in current stage as it is for following cases:
		if((userGroup.getUserGroupType().getType().equals(ApplicationConstants.MEMBER)) && (internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMMEMBER))) {
			return;
		}
		
		//end workflow in following cases:		
		if(deviceType.getType().startsWith("bills")) {
			Bill bill = (Bill) domain;
			if(currentWorkflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
					&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.TRANSLATOR)
					&& currentWorkflowDetails.getCustomStatus().equals(ApplicationConstants.BILL_FINAL_TRANSLATION)) {
				//----------------properties updated before task completion----------------//
				Map<String,String> properties=new HashMap<String, String>();
				properties.put("pv_endflag","end");
				properties.put("pv_timerflag", "off");	
				properties.put("pv_mailflag", "off");
				//find current task to be completed
				org.mkcl.els.common.vo.ProcessInstance processInstance = processService.findProcessInstanceById(execution.getProcessInstanceId());
				org.mkcl.els.common.vo.Task task = processService.getCurrentTask(processInstance);				
				//complete task	
				processService.completeTask(task, properties);
				//update workflowdetails
				currentWorkflowDetails.setStatus("TIMEOUT");
				Status translationTimeoutStatus = Status.findByType(ApplicationConstants.BILL_TRANSLATION_TIMEOUT, bill.getLocale());
				currentWorkflowDetails.setCustomStatus(translationTimeoutStatus.getType());
				currentWorkflowDetails.setCompletionTime(new Date());
				currentWorkflowDetails.merge();							
				return;	
			}					
		} else if(deviceType.getType().equals(ApplicationConstants.BILLAMENDMENT_MOTION)) {
			BillAmendmentMotion billAmendmentMotion = (BillAmendmentMotion) domain;
			if(currentWorkflowDetails.getWorkflowType().equals(ApplicationConstants.TRANSLATION_WORKFLOW)
					&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.TRANSLATOR)
					&& currentWorkflowDetails.getCustomStatus().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_TRANSLATION)) {
				//----------------properties updated before task completion----------------//
				Map<String,String> properties=new HashMap<String, String>();
				properties.put("pv_endflag","end");
				properties.put("pv_timerflag", "off");	
				properties.put("pv_mailflag", "off");
				//find current task to be completed
				org.mkcl.els.common.vo.ProcessInstance processInstance = processService.findProcessInstanceById(execution.getProcessInstanceId());
				org.mkcl.els.common.vo.Task task = processService.getCurrentTask(processInstance);				
				//complete task	
				processService.completeTask(task, properties);
				//update workflowdetails
				currentWorkflowDetails.setStatus("TIMEOUT");
				Status translationTimeoutStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_TRANSLATION_TIMEOUT, billAmendmentMotion.getLocale());
				currentWorkflowDetails.setCustomStatus(translationTimeoutStatus.getType());
				currentWorkflowDetails.setCompletionTime(new Date());
				currentWorkflowDetails.merge();							
				return;	
			}					
		}		
		
		//set desired internal status for the skipped actor based on current internal status
		Status internalStatusSelected = null;		
		if((userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT)) && (internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT))) {
			internalStatusSelected = Status.findByType(ApplicationConstants.RESOLUTION_RECOMMEND_SENDBACK, domain.getLocale());				
		} else {
			internalStatusSelected = internalStatus;
		}		
		
		//------------------------------next actor---------------------------------//		
		String user = "";	
		
		List<Reference> actors = null;
		if(deviceType.getType().startsWith("resolutions")) {
			actors=WorkflowConfig.findResolutionActorsVO((Resolution) domain,internalStatusSelected,userGroup,Integer.parseInt(currentLevel),houseTypeForWorkflow.getName(),domain.getLocale());
		} else if(deviceType.getType().startsWith("questions")) {
			actors=WorkflowConfig.findQuestionActorsVO((Question) domain,internalStatusSelected,userGroup,Integer.parseInt(currentLevel),domain.getLocale());
		}
		
		String nextactor=actors.get(0).getId();
		String level="";
		if(nextactor!=null){
			if(!nextactor.isEmpty()){
				String[] temp=nextactor.split("#");
				user = temp[0];				
				level=temp[2];
			}
		}		
		//-------------------------------------------------------------------------//
		
		//----------------properties updated before task completion----------------//
		Map<String,String> properties=new HashMap<String, String>();
		
		properties.put("pv_user",user);
		
		//set following conditionally.. these are default
//				properties.put("pv_timerflag", "off");		
//				properties.put("pv_reminderflag", "off");
//				properties.put("pv_mailflag", "off");
//				properties.put("pv_timerround", "0");		
//				properties.put("pv_endflag", "continue");
		
		properties.put("pv_deviceId", String.valueOf(execution.getVariable("pv_deviceId")));
		properties.put("pv_deviceTypeId", String.valueOf(execution.getVariable("pv_deviceTypeId")));
		
		String endflag="continue";
		properties.put("pv_endflag",endflag);	
		
		String mailflag="off";				
		properties.put("pv_mailflag", mailflag);
		
		String timerflag="off";
		properties.put("pv_timerflag", timerflag);	
		//-------------------------------------------------------------------------//
		
		//find current task to be completed
		org.mkcl.els.common.vo.ProcessInstance processInstance = processService.findProcessInstanceById(execution.getProcessInstanceId());
		org.mkcl.els.common.vo.Task task = processService.getCurrentTask(processInstance);
		System.out.println(task.getId());		
		
		//complete task with updated properties			
		processService.completeTask(task, properties);			
		
		//get next task
		org.mkcl.els.common.vo.Task newtask=processService.getCurrentTask(processInstance);		
	
		//create workflow detail for next level		
		if(deviceType.getType().startsWith("resolutions")) {			
			WorkflowDetails.create((Resolution) domain,newtask,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,level,houseTypeForWorkflow);			
		} else if(deviceType.getType().startsWith("questions")) {			

			/*
			 * Added by Amit Desai 2 Dec 2014
			 * START...
			 */
			// WorkflowDetails.create((Question) domain,newtask,ApplicationConstants.APPROVAL_WORKFLOW,level);
			Question question = (Question) domain;
			Workflow workflow = null;

			String internalStatusType = internalStatus.getType();
			Status recommendationStatus = question.getRecommendationStatus();
			String recommendationStatusType = recommendationStatus.getType();

			if(recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING_POST_ADMISSION)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_UNCLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_UNCLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_UNCLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_UNCLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)
					|| recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				workflow = Workflow.findByStatus(recommendationStatus, question.getLocale());
			} 
			else if(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_NAMECLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_NAMECLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_NAMECLUBBING)
					|| internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_NAMECLUBBING)
					|| (internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_PROCESSED_CLARIFICATIONRECEIVED))
					|| (internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_CLARIFICATIONRECEIVED)
					|| (internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_SHORTNOTICE_PROCESSED_CLARIFICATIONRECEIVED))
					|| (internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_DEPARTMENT)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATION_NOT_RECEIVED))
					||(internalStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_CLARIFICATION_NEEDED_FROM_MEMBER)
						&& recommendationStatusType.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_CLARIFICATIONRECEIVED)))) {
				workflow = Workflow.findByStatus(internalStatus, question.getLocale());
			}
			else {
				workflow = Workflow.findByStatus(internalStatus, question.getLocale());
			}

			String workflowType = workflow.getType();
			WorkflowDetails.create(question, task, workflowType, level);
			/*
			 * Added by Amit Desai 2 Dec 2014
			 * ... END
			 */			
		}
		
		WorkflowDetails.endProcess(currentWorkflowDetails);
		
		//update domain status, internal status & recommendation status if needed
		if((userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT)) && (internalStatus.getType().equals(ApplicationConstants.RESOLUTION_FINAL_CLARIFICATIONNEEDEDFROMDEPARTMENT))) {
			Status newStatus=Status.findByType(ApplicationConstants.RESOLUTION_SYSTEM_TO_BE_PUTUP, domain.getLocale());
			if(houseTypeForWorkflow.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				((Resolution) domain).setRecommendationStatusLowerHouse(newStatus);
			}else if(houseTypeForWorkflow.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				((Resolution) domain).setRecommendationStatusUpperHouse(newStatus);
			}
			((Resolution) domain).simpleMerge();
		}		
	}
	
}
