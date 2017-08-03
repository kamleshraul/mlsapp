package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.PrintRequisition;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDetailsRepository extends BaseRepository<WorkflowDetails, Serializable>{
	
	/** The process service. */
	@Autowired
	private IProcessService processService;

	/**************Question*********************/
	public WorkflowDetails findCurrentWorkflowDetail(final Question question) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			Workflow workflow = question.findWorkflowFromStatus();
			if(workflow!=null) {
				workflowDetails = findCurrentWorkflowDetail(question, workflow.getType());
			}			
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_question", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final Question question, final String workflowType) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", question.getId().toString());
			query.setParameter("workflowType",workflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_question", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDetails> findPendingWorkflowDetails(final Question question, final String workflowType) throws ELSException{
		List<WorkflowDetails> details = new ArrayList<WorkflowDetails>();
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", question.getId().toString());
			query.setParameter("workflowType",workflowType);
			details = (List<WorkflowDetails>)query.getResultList();
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_question", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return details;
	}
	
	public WorkflowDetails create(final Question question,final Task task,
			final String workflowType,final String assigneeLevel) throws ELSException{
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup=UserGroup.findActive(credential, new Date(), "");
							//findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
					userGroupId=String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(question!=null){
						if(question.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(question.getId()));
						}
						if(question.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
							workflowDetails.setNumericalDevice(question.getNumber().toString());
						}
						if(question.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(question.getPrimaryMember().getFullname());
						}
						if(question.getType()!=null){
							workflowDetails.setDeviceType(question.getType().getName());
						}
						if(question.getHouseType()!=null){
							workflowDetails.setHouseType(question.getHouseType().getName());
						}
						if(question.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(question.getInternalStatus().getName());
						}
						workflowDetails.setLocale(question.getLocale());
						if(question.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(question.getRecommendationStatus().getName());
						}
						if(question.getGroup()!=null){
							workflowDetails.setGroupNumber(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getGroup().getNumber()));
						}

						workflowDetails.setRemarks(question.getRemarks());
						if(question.getSession()!=null){
							if(question.getSession().getType()!=null){
								workflowDetails.setSessionType(question.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getSession().getYear()));
						}
						if(question.getChartAnsweringDate()!=null){
							workflowDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
						}
						if(question.getRevisedSubject() != null && !question.getRevisedSubject().isEmpty()){
							workflowDetails.setSubject(question.getRevisedSubject());
						}else{
							workflowDetails.setSubject(question.getSubject());
						}
						if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
							workflowDetails.setText(question.getRevisedQuestionText());
						}else{
							workflowDetails.setText(question.getQuestionText());
						}
						if(question.getFactualPosition() != null && !question.getFactualPosition().isEmpty()){
							workflowDetails.setReply(question.getFactualPosition());
						}
						if(question.getFactualPositionFromMember() != null && !question.getFactualPositionFromMember().isEmpty()){
							workflowDetails.setReply(question.getFactualPositionFromMember());
						}
						if(question.getAnswer() != null && !question.getAnswer().isEmpty()){
							workflowDetails.setReply(question.getAnswer());
						}
						if(question.getMinistry() != null){
							workflowDetails.setMinistry(question.getMinistry().getName());
						}
						if(question.getSubDepartment() != null){
							workflowDetails.setSubdepartment(question.getSubDepartment().getName());
						}
						
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, question.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else {
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
								|| workflowType.equals(ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW)
								|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
								|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
							workflowDetails.setWorkflowSubType(question.getRecommendationStatus().getType());
						} else {
							workflowDetails.setWorkflowSubType(question.getInternalStatus().getType());
						}
						workflowDetails.persist();
					}	
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

	public WorkflowDetails create(final Question question,
			final Task task,
			final UserGroupType usergroupType,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
					UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), question.getLocale());
					if(userGroup != null){
						userGroupId=String.valueOf(userGroup.getId());
						userGroupType=userGroup.getUserGroupType().getType();
						userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(question!=null){
							if(question.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(question.getId()));
							}
							if(question.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
								workflowDetails.setNumericalDevice(question.getNumber().toString());
							}
							if(question.getPrimaryMember()!=null){
								workflowDetails.setDeviceOwner(question.getPrimaryMember().getFullname());
							}
							if(question.getType()!=null){
								workflowDetails.setDeviceType(question.getType().getName());
							}
							if(question.getHouseType()!=null){
								workflowDetails.setHouseType(question.getHouseType().getName());
							}
							if(question.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(question.getInternalStatus().getName());
							}
							workflowDetails.setLocale(question.getLocale());
							if(question.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(question.getRecommendationStatus().getName());
							}
							if(question.getGroup()!=null){
								workflowDetails.setGroupNumber(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getGroup().getNumber()));
							}

							workflowDetails.setRemarks(question.getRemarks());
							if(question.getSession()!=null){
								if(question.getSession().getType()!=null){
									workflowDetails.setSessionType(question.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getSession().getYear()));
							}
							if(question.getChartAnsweringDate()!=null){
								workflowDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
							}
							if(question.getRevisedSubject() != null && !question.getRevisedSubject().isEmpty()){
								workflowDetails.setSubject(question.getRevisedSubject());
							}else{
								workflowDetails.setSubject(question.getSubject());
							}
							if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
								workflowDetails.setText(question.getRevisedQuestionText());
							}else{
								workflowDetails.setText(question.getQuestionText());
							}
							if(question.getFactualPosition() != null && !question.getFactualPosition().isEmpty()){
								workflowDetails.setReply(question.getFactualPosition());
							}
							if(question.getFactualPositionFromMember() != null && !question.getFactualPositionFromMember().isEmpty()){
								workflowDetails.setReply(question.getFactualPositionFromMember());
							}
							if(question.getAnswer() != null && !question.getAnswer().isEmpty()){
								workflowDetails.setReply(question.getAnswer());
							}
							if(question.getMinistry() != null){
								workflowDetails.setMinistry(question.getMinistry().getName());
							}
							if(question.getSubDepartment() != null){
								workflowDetails.setSubdepartment(question.getSubDepartment().getName());
							}
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, question.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(question.getRecommendationStatus().getType());
							}else if( workflowType.equals(ApplicationConstants.QUESTION_SUPPLEMENTARY_WORKFLOW)){
								workflowDetails.setWorkflowSubType(ApplicationConstants.QUESTION_PROCESSED_SUPPLEMENTARYCLUBBING);
							} else {
								workflowDetails.setWorkflowSubType(question.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
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
	
	
	public List<WorkflowDetails> create(final Question question,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) throws ELSException {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, question.getLocale());
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i : tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){
							Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroup userGroup=UserGroup.findActive(credential, new Date(), question.getLocale());
							if(userGroup != null){
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
								if(question!=null){
									if(question.getId()!=null){
										workflowDetails.setDeviceId(String.valueOf(question.getId()));
									}
									if(question.getNumber()!=null){
										workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));
										workflowDetails.setNumericalDevice(question.getNumber().toString());
									}
									if(question.getPrimaryMember()!=null){
										workflowDetails.setDeviceOwner(question.getPrimaryMember().getFullname());
									}
									if(question.getType()!=null){
										workflowDetails.setDeviceType(question.getType().getName());
									}
									if(question.getHouseType()!=null){
										workflowDetails.setHouseType(question.getHouseType().getName());
									}
									if(question.getInternalStatus()!=null){
										workflowDetails.setInternalStatus(question.getInternalStatus().getName());
									}
									workflowDetails.setLocale(question.getLocale());
									if(question.getRecommendationStatus()!=null){
										workflowDetails.setRecommendationStatus(question.getRecommendationStatus().getName());
									}
									if(question.getGroup()!=null){
										workflowDetails.setGroupNumber(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getGroup().getNumber()));
									}
									if(question.getChartAnsweringDate()!=null){
										workflowDetails.setAnsweringDate(question.getChartAnsweringDate().getAnsweringDate());
									}
									workflowDetails.setRemarks(question.getRemarks());
									if(question.getSession()!=null){
										if(question.getSession().getType()!=null){
											workflowDetails.setSessionType(question.getSession().getType().getSessionType());
										}
										workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getSession().getYear()));
									}
									if(question.getRevisedSubject() != null && !question.getRevisedSubject().isEmpty()){
										workflowDetails.setSubject(question.getRevisedSubject());
									}else{
										workflowDetails.setSubject(question.getSubject());
									}
									if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
										workflowDetails.setText(question.getRevisedQuestionText());
									}else{
										workflowDetails.setText(question.getQuestionText());
									}
									if(question.getFactualPosition() != null && !question.getFactualPosition().isEmpty()){
										workflowDetails.setReply(question.getFactualPosition());
									}
									if(question.getFactualPositionFromMember() != null && !question.getFactualPositionFromMember().isEmpty()){
										workflowDetails.setReply(question.getFactualPositionFromMember());
									}
									if(question.getAnswer() != null && !question.getAnswer().isEmpty()){
										workflowDetails.setReply(question.getAnswer());
									}
									if(question.getMinistry() != null){
										workflowDetails.setMinistry(question.getMinistry().getName());
									}
									if(question.getSubDepartment() != null){
										workflowDetails.setSubdepartment(question.getSubDepartment().getName());
									}
								}
								workflowDetails.setProcessId(i.getProcessInstanceId());
								workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
								workflowDetails.setTaskId(i.getId());
								workflowDetails.setWorkflowType(workflowType);
								/**** To make the HDS to take the workflow with mailer task ****/
								String currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
								if(workflowType.equals(currentDeviceTypeWorkflowType)){
									workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN);
									workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
									workflowDetails.setWorkflowSubType(question.getInternalStatus().getType());					
									/**** Different types of workflow sub types ****/
								}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
									workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN);
									workflowDetails.setForm(workflowDetails.getUrlPattern());
									if(requestStatus!=null){
									workflowDetails.setWorkflowSubType(requestStatus.getType());
									}
								}
								workflowDetailsList.add((WorkflowDetails) workflowDetails.persist());
							}else{
								throw new Exception();
							}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_question", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}	
public WorkflowDetails findCurrentWorkflowDetail(final Device device, final DeviceType deviceType, final String workflowType) throws ELSException {
		
		try{
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.deviceType=:deviceType" +
					" AND m.workflowType=:workflowType" +					
					" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", device.getId().toString());
			query.setParameter("deviceType", deviceType.getName());
			query.setParameter("workflowType",workflowType);			
			WorkflowDetails workflowDetails=(WorkflowDetails) query.setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(NoResultException nre) {
			return null;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetails_findCurrentWorkflowDetail_device#deviceType#workflowType", "Workflow Details for Recommendation From President Not Found");
			throw elsException;
		}
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final Question question, final String processDefinitionId, final Status status, final String usergroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionId);
		Map<String,String> properties = new HashMap<String, String>();
		Workflow workflow = Workflow.findByStatus(status, locale);
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(question, workflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(question.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
		if(processDefinitionId.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflow.getType(),Integer.toString(level));
		question.setEndFlag("continue");
		question.setTaskReceivedOn(new Date());
		question.setWorkflowDetailsId(workflowDetails.getId());
		question.setWorkflowStarted("YES");
		question.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		question.setActor(actor);
		
		String[] actorArr = actor.split("#");
		question.setLevel(actorArr[2]);
		question.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		question.simpleMerge();
		return workflowDetails;
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final Question question, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(question, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(question.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], question.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(question, task, usergroupType, workflowType, Integer.toString(level));
		question.setEndFlag("continue");
		question.setTaskReceivedOn(new Date());
		question.setWorkflowDetailsId(workflowDetails.getId());
		question.setWorkflowStarted("YES");
		question.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		question.setActor(actor);
		
		String[] actorArr = actor.split("#");
		question.setLevel(actorArr[2]);
		question.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		question.simpleMerge();
		return workflowDetails;		
	}
	
	public WorkflowDetails startProcess(final Question question, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(question, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(question.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], question.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(question, task, usergroupType, workflowType, "1");
		question.setEndFlag("continue");
		question.setTaskReceivedOn(new Date());
		question.setWorkflowDetailsId(workflowDetails.getId());
		question.setWorkflowStarted("YES");
		question.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		question.setActor(actor);
		
		String[] actorArr = actor.split("#");
		question.setLevel(actorArr[2]);
		question.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		question.simpleMerge();
		return workflowDetails;	
	}
	/******************Question************************/

	/******************Resolution************************/
	public WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution) throws ELSException {
		try{
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" ORDER BY m.assignmentTime "+ApplicationConstants.DESC +" LIMIT 0,1";
			Query  query=this.em().createQuery(strQuery);
			query.setParameter("workflowType",ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
			query.setParameter("deviceId", resolution.getId().toString());
			WorkflowDetails workflowDetails=(WorkflowDetails) query.getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_resolution", "WorkflowDetails Not Found");
			throw elsException;
		}		
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution, final String workflowHouseType) throws ELSException {
		try{
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.houseType=:houseType" +
					" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", resolution.getId().toString());
			query.setParameter("workflowType",ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
			query.setParameter("houseType", workflowHouseType);
			WorkflowDetails workflowDetails=(WorkflowDetails) query.setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_resolution", "WorkflowDetails Not Found");
			throw elsException;
		}			
	}
	
	public WorkflowDetails create(final Resolution resolution,final Task task,
			final String workflowType,final String assigneeLevel, final HouseType houseTypeForWorkflow) throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();			
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential = Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup = UserGroup.findActive(credential, new Date(), resolution.getLocale());
					userGroupId=String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(resolution!=null){
						if(resolution.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(resolution.getId()));
						}
						if(resolution.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(resolution.getLocale()).format(resolution.getNumber()));
							workflowDetails.setNumericalDevice(resolution.getNumber().toString());
						}
						if(resolution.getMember()!=null){
							workflowDetails.setDeviceOwner(resolution.getMember().getFullname());
						}
						if(resolution.getType()!=null){
							workflowDetails.setDeviceType(resolution.getType().getName());
						}
						if(houseTypeForWorkflow!=null){
							workflowDetails.setHouseType(houseTypeForWorkflow.getName());
							if(houseTypeForWorkflow.getType().equals(ApplicationConstants.LOWER_HOUSE)){
								if(resolution.getInternalStatusLowerHouse()!=null){
									workflowDetails.setInternalStatus(resolution.getInternalStatusLowerHouse().getName());
								}
								if(resolution.getFileLowerHouse()!=null){
									workflowDetails.setFile(String.valueOf(resolution.getFileLowerHouse()));
								}
								if(resolution.getRecommendationStatusLowerHouse()!=null){
									workflowDetails.setRecommendationStatus(resolution.getRecommendationStatusLowerHouse().getName());
								}
							}else if(houseTypeForWorkflow.getType().equals(ApplicationConstants.UPPER_HOUSE)){
								if(resolution.getInternalStatusUpperHouse()!=null){
									workflowDetails.setInternalStatus(resolution.getInternalStatusUpperHouse().getName());
								}
								if(resolution.getRecommendationStatusUpperHouse()!=null){
									workflowDetails.setRecommendationStatus(resolution.getRecommendationStatusUpperHouse().getName());
								}
								if(resolution.getFileUpperHouse()!=null){
									workflowDetails.setFile(String.valueOf(resolution.getFileUpperHouse()));
								}
							}
						
						}
						
						
						workflowDetails.setLocale(resolution.getLocale());
						
						workflowDetails.setRemarks(resolution.getRemarks());
						if(resolution.getSession()!=null){
							if(resolution.getSession().getType()!=null){
								workflowDetails.setSessionType(resolution.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(resolution.getLocale()).format(resolution.getSession().getYear()));
						}
						
						if(resolution.getFile() != null){
							workflowDetails.setFile(resolution.getFile().toString());
						}
						if(resolution.getRevisedSubject()!=null && !resolution.getRevisedSubject().isEmpty()) {
							workflowDetails.setSubject(resolution.getRevisedSubject());
						} else {
							workflowDetails.setSubject(resolution.getSubject());
						}
						if(resolution.getRevisedNoticeContent()!=null && !resolution.getRevisedNoticeContent().isEmpty()) {
							workflowDetails.setText(resolution.getRevisedNoticeContent());
						} else {
							workflowDetails.setText(resolution.getNoticeContent());
						}	
						
						if(resolution.getFactualPosition() != null && !resolution.getFactualPosition().isEmpty()){
							workflowDetails.setReply(resolution.getFactualPosition());
						}
						if(resolution.getMinistry() != null){
							workflowDetails.setMinistry(resolution.getMinistry().getName());
						}
						if(resolution.getSubDepartment() != null){
							workflowDetails.setSubdepartment(resolution.getSubDepartment().getName());
						}
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					if(workflowType.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.RIS_APPROVAL_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						if(houseTypeForWorkflow.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							workflowDetails.setWorkflowSubType(resolution.getInternalStatusLowerHouse().getType());	
						}else if(houseTypeForWorkflow.getType().equals(ApplicationConstants.UPPER_HOUSE)){
							workflowDetails.setWorkflowSubType(resolution.getInternalStatusUpperHouse().getType());	
						}
										
						/**** Different types of workflow sub types ****/
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_resolution", "WorkflowDetails cannot be created");
			throw elsException;
		}	
		return workflowDetails;		
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final Resolution resolution, final String workflowHouseType, final String processDefinitionId, final Status status, final String usergroupType, final int level, final String locale) throws ELSException {
		String houseTypeType = workflowHouseType;
		if(!resolution.getType().getType().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseTypeType = resolution.getHouseType().getType();
    	}
		HouseType houseType = HouseType.findByType(houseTypeType, locale);
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionId);
		Map<String,String> properties = new HashMap<String, String>();
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(resolution, houseType, status, usergroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(resolution.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(resolution.getType().getId()));
		if(processDefinitionId.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		WorkflowDetails workflowDetails = WorkflowDetails.create(resolution,task,ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW,Integer.toString(level),houseType);
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			resolution.setEndFlagLowerHouse("continue");
			resolution.setTaskReceivedOnLowerHouse(new Date());
			resolution.setWorkflowDetailsIdLowerHouse(workflowDetails.getId());
			resolution.setWorkflowStartedLowerHouse("YES");
			resolution.setWorkflowStartedOnLowerHouse(new Date());
			
			String actor = actorAtGivenLevel.getId();
			resolution.setActorLowerHouse(actor);
			
			String[] actorArr = actor.split("#");
			resolution.setLevelLowerHouse(actorArr[2]);
			resolution.setLocalizedActorNameLowerHouse(actorArr[3] + "(" + actorArr[4] + ")");
		} else if(workflowHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			resolution.setEndFlagUpperHouse("continue");
			resolution.setTaskReceivedOnUpperHouse(new Date());
			resolution.setWorkflowDetailsIdUpperHouse(workflowDetails.getId());
			resolution.setWorkflowStartedUpperHouse("YES");
			resolution.setWorkflowStartedOnUpperHouse(new Date());
			
			String actor = actorAtGivenLevel.getId();
			resolution.setActorUpperHouse(actor);
			
			String[] actorArr = actor.split("#");
			resolution.setLevelUpperHouse(actorArr[2]);
			resolution.setLocalizedActorNameUpperHouse(actorArr[3] + "(" + actorArr[4] + ")");
		}	
		
		resolution.simpleMerge();
		return workflowDetails;
	}
	/******************Resolution************************/
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,final String strSessionYear,final String strDeviceType,
			final String strStatus,final String strWorkflowSubType,final String assignee,
			final String strItemsCount,final String strLocale,final String file) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT wd FROM WorkflowDetails wd" +
				" WHERE houseType=:houseType"+
				" AND sessionType=:sessionType" +
				" AND sessionYear=:sessionYear"+
				" AND assignee=:assignee" +
				" AND deviceType=:deviceType"+
				" AND locale=:locale");
		if(file!=null&&!file.isEmpty()&&!file.equals("-")){
			buffer.append(" AND file=:file");
		}else{
			buffer.append(" AND status=:status");
			buffer.append(" AND workflowSubType=:workflowSubType");
		}
		buffer.append(" ORDER BY assignmentTime");
		List<WorkflowDetails> workflowDetails=new ArrayList<WorkflowDetails>();
		try{
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("houseType",strHouseType);
			query.setParameter("sessionType",strSessionType);
			query.setParameter("sessionYear",strSessionYear);
			query.setParameter("assignee",assignee);
			query.setParameter("deviceType",strDeviceType);
			query.setParameter("locale",strLocale);
			if(file!=null&&!file.isEmpty()&&!file.equals("-")){
				query.setParameter("file",file);
			}else{
				query.setParameter("status",strStatus);
				query.setParameter("workflowSubType",strWorkflowSubType);
			}
			query.setMaxResults(Integer.parseInt(strItemsCount));
			workflowDetails=query.getResultList();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findAll", "WorkflowDetails Not found");
			throw elsException;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,final String strSessionYear,final String strDeviceType,
			final String strStatus,final String strWorkflowSubType,final String strSubDepartment,final String assignee,
			final String strItemsCount,final String strLocale,final String file) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT wd FROM WorkflowDetails wd" +
				" WHERE houseType=:houseType"+
				" AND sessionType=:sessionType" +
				" AND sessionYear=:sessionYear"+
				" AND assignee=:assignee" +
				" AND deviceType=:deviceType"+
				" AND (:subdepartment='' OR :subdepartment='0' OR subdepartment=:subdepartment)"+
				" AND locale=:locale");
		if(file!=null&&!file.isEmpty()&&!file.equals("-")){
			buffer.append(" AND file=:file");
		}else{
			buffer.append(" AND status=:status");
			buffer.append(" AND workflowSubType=:workflowSubType");
		}
		buffer.append(" ORDER BY assignmentTime");
		List<WorkflowDetails> workflowDetails=new ArrayList<WorkflowDetails>();
		try{
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("houseType",strHouseType);
			query.setParameter("sessionType",strSessionType);
			query.setParameter("sessionYear",strSessionYear);
			query.setParameter("assignee",assignee);
			query.setParameter("deviceType",strDeviceType);
			query.setParameter("subdepartment",strSubDepartment);
			query.setParameter("locale",strLocale);
			if(file!=null&&!file.isEmpty()&&!file.equals("-")){
				query.setParameter("file",file);
			}else{
				query.setParameter("status",strStatus);
				query.setParameter("workflowSubType",strWorkflowSubType);
			}
			query.setMaxResults(Integer.parseInt(strItemsCount));
			workflowDetails=query.getResultList();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findAll", "WorkflowDetails Not found");
			throw elsException;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDetails> findAll(final String strHouseType,
			final String strSessionType,final String strSessionYear,final String strDeviceType,
			final String strStatus,final String strWorkflowSubType,final String assignee,
			final String strItemsCount,final String strLocale) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT wd FROM WorkflowDetails wd" +
				" WHERE houseType=:houseType"+
				" AND sessionType=:sessionType" +
				" AND sessionYear=:sessionYear"+
				" AND assignee=:assignee" +
				" AND deviceType=:deviceType"+
				" AND locale=:locale");
		
		buffer.append(" AND status=:status");
		buffer.append(" AND workflowSubType=:workflowSubType");
		buffer.append(" ORDER BY group_number");
		
		List<WorkflowDetails> workflowDetails=new ArrayList<WorkflowDetails>();
		try{
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("houseType",strHouseType);
			query.setParameter("sessionType",strSessionType);
			query.setParameter("sessionYear",strSessionYear);
			query.setParameter("assignee",assignee);
			query.setParameter("deviceType",strDeviceType);
			query.setParameter("locale",strLocale);
			query.setParameter("status",strStatus);
			query.setParameter("workflowSubType",strWorkflowSubType);
			query.setMaxResults(Integer.parseInt(strItemsCount));
			workflowDetails=query.getResultList();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findAll", "WorkflowDetails Not found");
			throw elsException;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDetails> findAll(String strHouseType,
			String strSessionType, String strSessionYear, String strDeviceType,
			String strStatus, String strWorkflowSubType, String assignee,
			String strItemsCount, String strLocale, String file, String group,
			Date answeringDate) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT wd FROM WorkflowDetails wd" +
				" WHERE houseType=:houseType"+
				" AND sessionType=:sessionType" +
				" AND sessionYear=:sessionYear"+
				" AND assignee=:assignee" +
				" AND deviceType=:deviceType"+
				" AND locale=:locale");
		if(file!=null&&!file.isEmpty()&&!file.equals("-")){
			buffer.append(" AND file=:file");
		}else{
			buffer.append(" AND status=:status");
			buffer.append(" AND workflowSubType=:workflowSubType");
		}
		
		if(group!=null && !group.isEmpty()){
			buffer.append(" AND groupNumber=:group");
		}
		if(answeringDate!=null){
			buffer.append(" AND answeringDate=:answeringDate");
		}
		buffer.append(" ORDER BY assignmentTime");
		List<WorkflowDetails> workflowDetails=new ArrayList<WorkflowDetails>();
		try{
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("houseType",strHouseType);
			query.setParameter("sessionType",strSessionType);
			query.setParameter("sessionYear",strSessionYear);
			query.setParameter("assignee",assignee);
			query.setParameter("deviceType",strDeviceType);
			query.setParameter("locale",strLocale);
			if(file!=null&&!file.isEmpty()&&!file.equals("-")){
				query.setParameter("file",file);
			}else{
				query.setParameter("status",strStatus);
				query.setParameter("workflowSubType",strWorkflowSubType);
			}
			if(group!=null &&!group.isEmpty()){
				query.setParameter("group", group);
			}
			if(answeringDate!=null){
				query.setParameter("answeringDate", answeringDate);
			}
			query.setMaxResults(Integer.parseInt(strItemsCount));
			workflowDetails=query.getResultList();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findAll", "WorkflowDetails Not found");
			throw elsException;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDetails> findAll(String strHouseType,
			String strSessionType, String strSessionYear, String strDeviceType,
			String strStatus, String strWorkflowSubType, String assignee,
			String strItemsCount, String strLocale, String file, String group, String subdepartment,
			Date answeringDate) throws ELSException {
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT wd FROM WorkflowDetails wd" +
				" WHERE houseType=:houseType"+
				" AND sessionType=:sessionType" +
				" AND sessionYear=:sessionYear"+
				" AND assignee=:assignee" +
				" AND deviceType=:deviceType"+
				" AND locale=:locale");
		if(file!=null&&!file.isEmpty()&&!file.equals("-")){
			buffer.append(" AND file=:file");
		}else{
			buffer.append(" AND status=:status");
			buffer.append(" AND workflowSubType=:workflowSubType");
		}
		
		if(group!=null && !group.isEmpty()){
			buffer.append(" AND groupNumber=:group");
		}
		if(answeringDate!=null){
			buffer.append(" AND answeringDate=:answeringDate");
		}
		if(subdepartment != null && !subdepartment.isEmpty() && !subdepartment.equals("-")){
			buffer.append(" AND subdepartment=:subdepartment");
		}
		buffer.append(" ORDER BY device_number");
		List<WorkflowDetails> workflowDetails=new ArrayList<WorkflowDetails>();
		try{
			Query query=this.em().createQuery(buffer.toString());
			query.setParameter("houseType",strHouseType);
			query.setParameter("sessionType",strSessionType);
			query.setParameter("sessionYear",strSessionYear);
			query.setParameter("assignee",assignee);
			query.setParameter("deviceType",strDeviceType);
			query.setParameter("locale",strLocale);
			if(file!=null&&!file.isEmpty()&&!file.equals("-")){
				query.setParameter("file",file);
			}else{
				query.setParameter("status",strStatus);
				query.setParameter("workflowSubType",strWorkflowSubType);
			}
			if(group!=null &&!group.isEmpty()){
				query.setParameter("group", group);
			}
			if(answeringDate!=null){
				query.setParameter("answeringDate", answeringDate);
			}
			if(subdepartment != null && !subdepartment.isEmpty() && !subdepartment.equals("-")){
				query.setParameter("subdepartment", subdepartment);
			}
			query.setMaxResults(Integer.parseInt(strItemsCount));
			workflowDetails=query.getResultList();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findAll", "WorkflowDetails Not found");
			throw elsException;
		}
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final UserGroup userGroup,
			final String domainIds,
			final String workflowType,
			final String status, 
			final String locale) {
		String strUserGroupId = String.valueOf(userGroup.getId());
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT wfd" +
				" FROM WorkflowDetails wfd" +
				" WHERE wfd.assigneeUserGroupId = '" + strUserGroupId + "'" +
				" AND wfd.domainIds = '" + domainIds + "'" +
				" AND wfd.workflowType = '" + workflowType + "'" +
				" AND wfd.status = '" + status + "'" +
				" AND wfd.locale = '" + locale + "'");
		
		TypedQuery<WorkflowDetails> tQuery = 
			this.em().createQuery(query.toString(), WorkflowDetails.class);
		WorkflowDetails workflowDetails = tQuery.getSingleResult();
		return workflowDetails;
	}
	
	public List<WorkflowDetails> findPendingWorkflowOfCurrentUser(final java.util.Map<String, String> parameters, 
			final String orderBy,
			final String sortOrder){
		
		StringBuffer strQuery = new StringBuffer("SELECT t FROM WorkflowDetails t WHERE");
		int index = 0; 
    	for (Entry<String, String> i : parameters.entrySet()) {
            strQuery.append(" t." + i.getKey() + "=:" + i.getKey());
            if(index < (parameters.entrySet().size() - 1)){
            	strQuery.append(" AND");	            	
            }
            index++;
        }
    	
    	strQuery.append(" ORDER BY t."+orderBy +" " + sortOrder);
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	
    	for (Entry<String, String> i : parameters.entrySet()) {
            jpQuery.setParameter(i.getKey(), i.getValue());
        }
    	@SuppressWarnings("unchecked")
		List<WorkflowDetails> list = jpQuery.getResultList();
    	return list;
	}

	public List<WorkflowDetails> findPendingWorkflowOfCurrentUserByAssignmentTimeRange(final java.util.Map<String, String> parameters,
			final Date toDate,
			final Date fromDate,
			final String orderBy, 
			final String sortOrder){
		
		StringBuffer strQuery = new StringBuffer("SELECT t FROM WorkflowDetails t WHERE ");
		int index = 0; 
    	for (Entry<String, String> i : parameters.entrySet()) {
            strQuery.append(" t." + i.getKey() + "=:" + i.getKey());
            if(index < (parameters.entrySet().size() - 1)){
            	strQuery.append(" AND");	            	
            }
            index++;
        }
    	strQuery.append(" AND t.assignmentTime BETWEEN :fromDate AND :toDate");
    	
    	strQuery.append(" ORDER BY t."+orderBy +" " + sortOrder );
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	
    	for (Entry<String, String> i : parameters.entrySet()) {
            jpQuery.setParameter(i.getKey(), i.getValue());
        }
    	jpQuery.setParameter("fromDate", fromDate);
    	jpQuery.setParameter("toDate", toDate);
    	
    	@SuppressWarnings("unchecked")
		List<WorkflowDetails> list = jpQuery.getResultList();
    	return list;
	}
	
	/************** Bill Related Domain Methods 
	 * @param customStatus status for auxillary workflow type*********************/
	public WorkflowDetails create(final Bill bill,final Task task,
			final String workflowType,String customStatus,final String userGroupType, final String assigneeLevel) {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;		
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class,"credential",credential, "credential", ApplicationConstants.ASC, bill.getLocale());
					UserGroup userGroup=null;
					for(UserGroup i: userGroups) {
						if(i.getUserGroupType().getType().equals(userGroupType)) {
							userGroup = i;
							break;
						}
					}					
					userGroupId=String.valueOf(userGroup.getId());					
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(bill!=null){
						if(bill.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(bill.getId()));
						}
						if(bill.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getNumber()));
							workflowDetails.setNumericalDevice(bill.getNumber().toString());
						}
						if(bill.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(bill.getPrimaryMember().getFullname());
						}
						if(bill.getType()!=null){
							workflowDetails.setDeviceType(bill.getType().getName());
						}
						if(Bill.findHouseTypeForWorkflow(bill)!=null){
							workflowDetails.setHouseType(Bill.findHouseTypeForWorkflow(bill).getName());
						}
						if(bill.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(bill.getInternalStatus().getName());							
						}
						workflowDetails.setLocale(bill.getLocale());						
						if(bill.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(bill.getRecommendationStatus().getName());
						}
						if(bill.getFile()!=null){
							workflowDetails.setFile(String.valueOf(bill.getFile()));
						}
						workflowDetails.setRemarks(bill.getRemarks());
						if(bill.getSession()!=null){
							if(bill.getSession().getType()!=null){
								workflowDetails.setSessionType(bill.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getSession().getYear()));
						}
						workflowDetails.setSubject(bill.getDefaultTitle());
						
						if(bill.getMinistry() != null){
							workflowDetails.setMinistry(bill.getMinistry().getName());
						}
						if(bill.getSubDepartment() != null){
							workflowDetails.setSubdepartment(bill.getSubDepartment().getName());
						}
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, bill.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else {
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						if(workflowType.equals(ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW)) {
							Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS, bill.getLocale());
							if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else if(customStatus!=null && !customStatus.isEmpty()) {
							workflowDetails.setWorkflowSubType(customStatus);
							workflowDetails.setCustomStatus(customStatus);
						} else {
							workflowDetails.setWorkflowSubType(bill.getInternalStatus().getType());
						}
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}	
		return workflowDetails;		
	}
	
	public WorkflowDetails create(final Bill bill,HouseType houseType,final Boolean isActorAcrossHouse,final PrintRequisition printRequisition,final Task task,
			final String workflowType,final String userGroupType,final String assigneeLevel) {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;		
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class,"credential",credential, "credential", ApplicationConstants.ASC, bill.getLocale());
					UserGroup userGroup=null;
					for(UserGroup i: userGroups) {
						if(i.getUserGroupType().getType().equals(userGroupType)) {
							userGroup = i;
							break;
						}
					}					
					userGroupId=String.valueOf(userGroup.getId());					
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(bill!=null){
						if(bill.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(bill.getId()));
						}
						if(bill.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getNumber()));
							workflowDetails.setNumericalDevice(bill.getNumber().toString());
						}
						if(bill.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(bill.getPrimaryMember().getFullname());
						}
						if(bill.getType()!=null){
							workflowDetails.setDeviceType(bill.getType().getName());
						}
						if(houseType!=null){
							if(isActorAcrossHouse!=null) {
								if(isActorAcrossHouse.equals(true)) {
									if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
										houseType = HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE,bill.getLocale());
									} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
										houseType = HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.LOWER_HOUSE,bill.getLocale());
									}
								}
							}
							workflowDetails.setHouseType(houseType.getName());
						}
						if(bill.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(bill.getInternalStatus().getName());							
						}
						workflowDetails.setLocale(bill.getLocale());						
						if(bill.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(bill.getRecommendationStatus().getName());
						}
						if(bill.getFile()!=null){
							workflowDetails.setFile(String.valueOf(bill.getFile()));
						}
						workflowDetails.setRemarks(bill.getRemarks());
						if(bill.getSession()!=null){
							if(bill.getSession().getType()!=null){
								workflowDetails.setSessionType(bill.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getSession().getYear()));
						}
						workflowDetails.setSubject(bill.getDefaultTitle());
						if(bill.getMinistry() != null){
							workflowDetails.setMinistry(bill.getMinistry().getName());
						}
						if(bill.getSubDepartment() != null){
							workflowDetails.setSubdepartment(bill.getSubDepartment().getName());
						}
					}					
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					if(workflowType.equals(ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						if(printRequisition!=null) {
							workflowDetails.setPrintRequisitionId(String.valueOf(printRequisition.getId()));
						}
						Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_SENDFORENDORSEMENT, bill.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else if(workflowType.equals(ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						if(printRequisition!=null) {
							workflowDetails.setPrintRequisitionId(String.valueOf(printRequisition.getId()));
						}
						Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_TRANSMITENDORSEMENTCOPIES, bill.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else if(workflowType.equals(ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						if(printRequisition!=null) {
							workflowDetails.setPrintRequisitionId(String.valueOf(printRequisition.getId()));
						}
						Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_TRANSMITPRESSCOPIES, bill.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else if(workflowType.equals(ApplicationConstants.LAY_LETTER_WORKFLOW)) {
						workflowDetails.setUrlPattern(ApplicationConstants.LAY_LETTER_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_LAYLETTER, bill.getLocale());
						if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else {
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_BILL);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						if(workflowType.equals(ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW)) {
							if(printRequisition!=null) {
								workflowDetails.setPrintRequisitionId(String.valueOf(printRequisition.getId()));
							}
							Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS, bill.getLocale());
							if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setWorkflowSubType(bill.getInternalStatus().getType());
						}
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}	
		return workflowDetails;
	}
	
	public List<WorkflowDetails> create(final Bill bill,final List<Task> tasks,
			final String workflowType,String customStatus, final String assigneeLevel) {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, bill.getLocale());
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){
							Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroup userGroup = UserGroup.findActive(credential, new Date(), bill.getLocale());
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
							if(bill!=null){
								if(bill.getId()!=null){
									workflowDetails.setDeviceId(String.valueOf(bill.getId()));
								}
								if(bill.getNumber()!=null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getNumber()));
									workflowDetails.setNumericalDevice(bill.getNumber().toString());
								}
								if(bill.getPrimaryMember()!=null){
									workflowDetails.setDeviceOwner(bill.getPrimaryMember().getFullname());
								}
								if(bill.getType()!=null){
									workflowDetails.setDeviceType(bill.getType().getName());
								}
								if(bill.getHouseType()!=null){
									workflowDetails.setHouseType(bill.getHouseType().getName());
								}
								if(bill.getInternalStatus()!=null){
									workflowDetails.setInternalStatus(bill.getInternalStatus().getName());
								}
								workflowDetails.setLocale(bill.getLocale());
								if(bill.getRecommendationStatus()!=null){
									workflowDetails.setRecommendationStatus(bill.getRecommendationStatus().getName());
								}
								if(bill.getFile()!=null){
									workflowDetails.setFile(String.valueOf(bill.getFile()));
								}								
								workflowDetails.setRemarks(bill.getRemarks());
								if(bill.getSession()!=null){
									if(bill.getSession().getType()!=null){
										workflowDetails.setSessionType(bill.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getSession().getYear()));
								}
								workflowDetails.setSubject(bill.getDefaultTitle());	
								if(bill.getMinistry() != null){
									workflowDetails.setMinistry(bill.getMinistry().getName());
								}
								if(bill.getSubDepartment() != null){
									workflowDetails.setSubdepartment(bill.getSubDepartment().getName());
								}
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_BILL);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
							} else {
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_BILL);
								workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
								if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)) {
									workflowDetails.setWorkflowSubType(bill.getInternalStatus().getType());
								} else if(customStatus!=null && !customStatus.isEmpty()) {
									workflowDetails.setWorkflowSubType(customStatus);
									workflowDetails.setCustomStatus(customStatus);
								}
							}
							workflowDetailsList.add((WorkflowDetails) workflowDetails.persist());
						}				
					}
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return workflowDetailsList;
		}
		return workflowDetailsList;
	}
	
	/************** BillAmendmentMotion Related Domain Methods 
	 * @param customStatus status for auxillary workflow type*********************/
	public WorkflowDetails create(final BillAmendmentMotion billAmendmentMotion,final Task task,
			final String workflowType,String customStatus,final String userGroupType, final String assigneeLevel) {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;		
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class,"credential",credential, "credential", ApplicationConstants.ASC, billAmendmentMotion.getLocale());
					UserGroup userGroup=null;
					for(UserGroup i: userGroups) {
						if(i.getUserGroupType().getType().equals(userGroupType)) {
							userGroup = i;
							break;
						}
					}					
					userGroupId=String.valueOf(userGroup.getId());					
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(billAmendmentMotion!=null){
						if(billAmendmentMotion.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(billAmendmentMotion.getId()));
						}
						if(billAmendmentMotion.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getNumber()));
						}
						if(billAmendmentMotion.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(billAmendmentMotion.getPrimaryMember().getFullname());
						}
						if(billAmendmentMotion.getType()!=null){
							workflowDetails.setDeviceType(billAmendmentMotion.getType().getName());
						}
						if(billAmendmentMotion.getHouseType()!=null){
							workflowDetails.setHouseType(billAmendmentMotion.getHouseType().getName());
						}
						if(billAmendmentMotion.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(billAmendmentMotion.getInternalStatus().getName());							
						}
						workflowDetails.setLocale(billAmendmentMotion.getLocale());						
						if(billAmendmentMotion.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(billAmendmentMotion.getRecommendationStatus().getName());
						}
						if(billAmendmentMotion.getFile()!=null){
							workflowDetails.setFile(String.valueOf(billAmendmentMotion.getFile()));
						}
						workflowDetails.setRemarks(billAmendmentMotion.getRemarks());
						if(billAmendmentMotion.getSession()!=null){
							if(billAmendmentMotion.getSession().getType()!=null){
								workflowDetails.setSessionType(billAmendmentMotion.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getSession().getYear()));
						}
						workflowDetails.setAmendedBillInfo(billAmendmentMotion.getAmendedBillInfo());
						workflowDetails.setDefaultAmendedSectionNumberInfo(billAmendmentMotion.getDefaultAmendedSectionNumberInfo());
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_BILLAMENDMENTMOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, billAmendmentMotion.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					} else {
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_BILLAMENDMENTMOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						/*if(workflowType.equals(ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW)) {
							Status requestStatus=Status.findByType(ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS, billAmendmentMotion.getLocale());
							if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else */if(customStatus!=null && !customStatus.isEmpty()) {
							workflowDetails.setWorkflowSubType(customStatus);
							workflowDetails.setCustomStatus(customStatus);
						} else {
							workflowDetails.setWorkflowSubType(billAmendmentMotion.getInternalStatus().getType());
						}
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}	
		return workflowDetails;		
	}
	
	public WorkflowDetails create(final BillAmendmentMotion billAmendmentMotion,
			  final Task task,
			  final UserGroupType usergroupType,
			  final String workflowType,
			  final String assigneeLevel) 
			  throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		try {
			String userGroupId=null;
			String userGroupType=null;
			String userGroupName=null;				
			String username=task.getAssignee();
			if(username==null || username.isEmpty()){
				throw new ELSException("WorkflowDetailsRepository_WorkflowDetail_create_adjournmentmotion_Task", "assignee is not set for the motion task");					
			}
			Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
			//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
			UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), billAmendmentMotion.getLocale());
			if(userGroup == null){
				throw new ELSException("WorkflowDetailsRepository_WorkflowDetail_create_adjournmentmotion_Task", "there is no active usergroup for the assignee");
			}
			userGroupId=String.valueOf(userGroup.getId());
			userGroupType=userGroup.getUserGroupType().getType();
			userGroupName=userGroup.getUserGroupType().getName();
			workflowDetails.setAssignee(task.getAssignee());
			workflowDetails.setAssigneeUserGroupId(userGroupId);
			workflowDetails.setAssigneeUserGroupType(userGroupType);
			workflowDetails.setAssigneeUserGroupName(userGroupName);
			workflowDetails.setAssigneeLevel(assigneeLevel);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				if(task.getCreateTime()!=null){
					if(!task.getCreateTime().isEmpty()){
						workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
					}
				}
			}	
			if(billAmendmentMotion!=null){
				if(billAmendmentMotion.getId()!=null){
					workflowDetails.setDeviceId(String.valueOf(billAmendmentMotion.getId()));
				}
				if(billAmendmentMotion.getNumber()!=null){
					workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getNumber()));
				}
				if(billAmendmentMotion.getPrimaryMember()!=null){
					workflowDetails.setDeviceOwner(billAmendmentMotion.getPrimaryMember().getFullname());
				}
				if(billAmendmentMotion.getType()!=null){
					workflowDetails.setDeviceType(billAmendmentMotion.getType().getName());
				}
				if(billAmendmentMotion.getHouseType()!=null){
					workflowDetails.setHouseType(billAmendmentMotion.getHouseType().getName());
				}
				if(billAmendmentMotion.getInternalStatus()!=null){
					workflowDetails.setInternalStatus(billAmendmentMotion.getInternalStatus().getName());							
				}
				workflowDetails.setLocale(billAmendmentMotion.getLocale());						
				if(billAmendmentMotion.getRecommendationStatus()!=null){
					workflowDetails.setRecommendationStatus(billAmendmentMotion.getRecommendationStatus().getName());
				}
				if(billAmendmentMotion.getFile()!=null){
					workflowDetails.setFile(String.valueOf(billAmendmentMotion.getFile()));
				}
				workflowDetails.setRemarks(billAmendmentMotion.getRemarks());
				if(billAmendmentMotion.getSession()!=null){
					if(billAmendmentMotion.getSession().getType()!=null){
						workflowDetails.setSessionType(billAmendmentMotion.getSession().getType().getSessionType());
					}
					workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getSession().getYear()));
				}
				workflowDetails.setAmendedBillInfo(billAmendmentMotion.getAmendedBillInfo());
				workflowDetails.setDefaultAmendedSectionNumberInfo(billAmendmentMotion.getDefaultAmendedSectionNumberInfo());
			}
			workflowDetails.setProcessId(task.getProcessInstanceId());
			workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
			workflowDetails.setTaskId(task.getId());
			workflowDetails.setWorkflowType(workflowType);
			
			if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
				workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION);
				workflowDetails.setForm(workflowDetails.getUrlPattern());
				Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, billAmendmentMotion.getLocale());
				if(requestStatus!=null){
					workflowDetails.setWorkflowSubType(requestStatus.getType());
				}
			} else {
				workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION);
				workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
				if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
					|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
					|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
					workflowDetails.setWorkflowSubType(billAmendmentMotion.getRecommendationStatus().getType());
				} else {
					workflowDetails.setWorkflowSubType(billAmendmentMotion.getInternalStatus().getType());
				}
				workflowDetails.persist();
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_eventmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;
	}
	
	public List<WorkflowDetails> create(final BillAmendmentMotion billAmendmentMotion,final List<Task> tasks,
			final String workflowType,String customStatus, final String assigneeLevel) {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, billAmendmentMotion.getLocale());
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){
							Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, billAmendmentMotion.getLocale());
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
							if(billAmendmentMotion!=null){
								if(billAmendmentMotion.getId()!=null){
									workflowDetails.setDeviceId(String.valueOf(billAmendmentMotion.getId()));
								}
								if(billAmendmentMotion.getNumber()!=null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getNumber()));
								}
								if(billAmendmentMotion.getPrimaryMember()!=null){
									workflowDetails.setDeviceOwner(billAmendmentMotion.getPrimaryMember().getFullname());
								}
								if(billAmendmentMotion.getType()!=null){
									workflowDetails.setDeviceType(billAmendmentMotion.getType().getName());
								}
								if(billAmendmentMotion.getHouseType()!=null){
									workflowDetails.setHouseType(billAmendmentMotion.getHouseType().getName());
								}
								if(billAmendmentMotion.getInternalStatus()!=null){
									workflowDetails.setInternalStatus(billAmendmentMotion.getInternalStatus().getName());
								}
								workflowDetails.setLocale(billAmendmentMotion.getLocale());
								if(billAmendmentMotion.getRecommendationStatus()!=null){
									workflowDetails.setRecommendationStatus(billAmendmentMotion.getRecommendationStatus().getName());
								}
								if(billAmendmentMotion.getFile()!=null){
									workflowDetails.setFile(String.valueOf(billAmendmentMotion.getFile()));
								}								
								workflowDetails.setRemarks(billAmendmentMotion.getRemarks());
								if(billAmendmentMotion.getSession()!=null){
									if(billAmendmentMotion.getSession().getType()!=null){
										workflowDetails.setSessionType(billAmendmentMotion.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(billAmendmentMotion.getLocale()).format(billAmendmentMotion.getSession().getYear()));
								}
								workflowDetails.setAmendedBillInfo(billAmendmentMotion.getAmendedBillInfo());
								workflowDetails.setDefaultAmendedSectionNumberInfo(billAmendmentMotion.getDefaultAmendedSectionNumberInfo());								
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_BILLAMENDMENTMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
							} else {
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_BILLAMENDMENTMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
								if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)) {
									workflowDetails.setWorkflowSubType(billAmendmentMotion.getInternalStatus().getType());
								} else if(customStatus!=null && !customStatus.isEmpty()) {
									workflowDetails.setWorkflowSubType(customStatus);
									workflowDetails.setCustomStatus(customStatus);
								}
							}
							workflowDetailsList.add((WorkflowDetails) workflowDetails.persist());
						}				
					}
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return workflowDetailsList;
		}
		return workflowDetailsList;
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final Bill bill, String workflowType) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+bill.getId()
			+" AND m.workflowType='"+workflowType+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			return new WorkflowDetails();
		}		
	}
	
//	public WorkflowDetails findCurrentWorkflowDetail(final BillAmendmentMotion billAmendmentMotion, String workflowType) {
//		try{
//			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+billAmendmentMotion.getId()
//			+" AND m.workflowType='"+workflowType+"' "
//			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
//			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
//			return workflowDetails;
//		}catch(Exception e){
//			e.printStackTrace();
//			return new WorkflowDetails();
//		}		
//	}
	
	public WorkflowDetails find(final Map<String, Object[]> fieldValuePair, final String locale){
		StringBuffer query = new StringBuffer();
		query.append("SELECT wf FROM WorkflowDetails wf");
		query.append(" WHERE wf.locale='" + locale + "'");
		for(Entry<String, Object[]> entry: fieldValuePair.entrySet()){
			String key = entry.getKey();
			Object[] value = (Object[])entry.getValue();
			if(value.length > 1){
				query.append(" AND (");
				for(int i = 0; i < value.length; i++){
					query.append(" wf."+key+"='"+value[i].toString()+"'");
					if(i < (value.length - 1)){
						query.append(" OR");
					}
				}
				query.append(")");
			}else{
				for(int i = 0; i < value.length; i++){
					query.append(" AND wf." + key+"='"+value[i].toString()+"'");	
				}				
			}
		}
		
		TypedQuery<WorkflowDetails> tQuery = this.em().createQuery(query.toString(), WorkflowDetails.class);
		
		List<WorkflowDetails> wfs = tQuery.getResultList();
		if(wfs != null && !wfs.isEmpty()){
			return wfs.get(0); 
		}
			
		return null;
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final UserGroup userGroup,
			final String deviceId,
			final String domainIds,
			final String workflowType,
			final String status, 
			final String locale) {
		String strUserGroupId = String.valueOf(userGroup.getId());
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT wfd" +
				" FROM WorkflowDetails wfd" +
				" WHERE wfd.assigneeUserGroupId = '" + strUserGroupId + "'" +
				" AND wfd.deviceId = '" + deviceId + "'" +
				" AND wfd.domainIds = '" + domainIds + "'" +
				" AND wfd.workflowType = '" + workflowType + "'" +
				" AND wfd.status = '" + status + "'" +
				" AND wfd.locale = '" + locale + "'");
		
		TypedQuery<WorkflowDetails> tQuery = 
			this.em().createQuery(query.toString(), WorkflowDetails.class);
		WorkflowDetails workflowDetails = tQuery.getSingleResult();
		return workflowDetails;
	}
	
	public Integer findIfWorkflowExists(final Session session,
			final HouseType houseType,
			final String deviceId,
			final String workflowSubTypeInitial,
			final String locale){
		
		Integer retVal = null;

		StringBuffer query = new StringBuffer("SELECT COUNT(wf) FROM WorkflowDetails wf"+
												" WHERE wf.deviceId=:deviceID"+
												" AND wf.houseType=:houseType"+
												" AND wf.sessionType=:sessionType"+
												" AND wf.sessionYear=:sessionYear"+
												" AND wf.workflowSubType LIKE :subType"+
												" AND wf.locale=:locale");
		TypedQuery<Long> tQuery = this.em().createQuery(query.toString(), Long.class);
		tQuery.setParameter("deviceID", deviceId);
		tQuery.setParameter("houseType", houseType.getName());
		tQuery.setParameter("sessionType", session.getType().getSessionType());
		tQuery.setParameter("sessionYear", FormaterUtil.formatNumberNoGrouping(session.getYear(), locale));
		tQuery.setParameter("subType", workflowSubTypeInitial+"%");
		tQuery.setParameter("locale", locale);
		
		retVal = ((Long)tQuery.getSingleResult()).intValue();
		
		return retVal;
	}	
	
	@SuppressWarnings("rawtypes")
	public List findCompleteness(final Session session,
			final HouseType houseType,
			final String deviceId,
			final String locale){
		StringBuffer query = new StringBuffer("SELECT wf.id,(SELECT"+ 
				" COUNT(twd.id) AS totaltasks"+
				" FROM workflow_details AS twd"+ 
				" WHERE twd.device_id='"+deviceId+"'"+
				" AND twd.workflow_sub_type LIKE 'editing_%'"+
				" AND twd.house_type='"+houseType.getName()+"'"+
				" AND twd.session_year='"+FormaterUtil.formatNumberNoGrouping(session.getYear(), locale)+"'"+
				" AND twd.session_type='"+ session.getType().getSessionType() +"') AS rs,"+
				" (SELECT "+
				" COUNT(pwd.id) AS donetasks"+	
				" FROM workflow_details AS pwd"+
				" WHERE pwd.device_id='"+deviceId+"'"+
				" AND pwd.status='COMPLETED'"+
				" AND pwd.house_type='"+houseType.getName()+"'"+
				" AND pwd.session_year='"+FormaterUtil.formatNumberNoGrouping(session.getYear(), locale)+"'"+
				" AND pwd.session_type='"+ session.getType().getSessionType() +"'"+
				" AND pwd.workflow_sub_type LIKE 'editing_%') AS rs1"+
				" FROM workflow_details wf LIMIT 1");
		Query pQuery = this.em().createNativeQuery(query.toString());
		
		return pQuery.getResultList();
	}
	
	public void endProcess(WorkflowDetails wfDetails) {
		// Complete task & end process
		String taskId = wfDetails.getTaskId();
		Task task = processService.findTaskById(taskId);
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("pv_endflag", "end");
		processService.completeTask(task, properties);
		
		// Update WorkflowDetails
		wfDetails.setStatus("COMPLETED");
		wfDetails.setCompletionTime(new Date());
		wfDetails.merge();
	}		
	
	public WorkflowDetails completeTask(final Question question) throws ELSException {
		WorkflowDetails workflowDetails = 
				WorkflowDetails.findCurrentWorkflowDetail(question);
		WorkflowDetails newWFDetails = null;
		if(workflowDetails != null){
			/**** Complete Task ****/
			String endflag = question.getEndFlag();
			Map<String,String> properties=new HashMap<String, String>();
			String user = question.getActor();
			properties.put("pv_deviceId", String.valueOf(question.getId()));
			properties.put("pv_deviceTypeId", String.valueOf(question.getType().getId()));
			if(user != null){
				if(!user.isEmpty()){
					String[] temp = user.split("#");
					properties.put("pv_user", temp[0]);
				}
			}
			properties.put("pv_endflag", endflag);
			String strTaskId = workflowDetails.getTaskId();
			Task task = processService.findTaskById(strTaskId);
			ProcessInstance processInstance = processService.findProcessInstanceById(
					task.getProcessInstanceId());
			processService.completeTask(task,properties);
			workflowDetails.setDecisionInternalStatus(question.getInternalStatus().getName());
			workflowDetails.setDecisionRecommendStatus(question.getRecommendationStatus().getName());
			workflowDetails.setStatus("COMPLETED");
			workflowDetails.setCompletionTime(new Date());
			workflowDetails.merge();
		
			/**** Get next task****/
			if(endflag!=null && !endflag.isEmpty() && !endflag.equals("end")){
				Task newtask = processService.getCurrentTask(processInstance);
				String nextuser  = question.getActor();
				if(nextuser != null){
					if(!nextuser.isEmpty()){
						String[] temp = nextuser.split("#");
						String level = temp[2];
						UserGroupType usergroupType = UserGroupType.findByType(temp[1], question.getLocale());
						Workflow workflowFromUpdatedStatus = Workflow.findByStatus(question.getInternalStatus(), question.getLocale());
						String currentDeviceTypeWorkflowType = workflowFromUpdatedStatus.getType();
						newWFDetails = 
								WorkflowDetails.create(question, newtask, usergroupType, currentDeviceTypeWorkflowType,level);
						
					}
				}
			}
		}
		return newWFDetails;
	}
	
	/**************Motion****************************/
	public WorkflowDetails findCurrentWorkflowDetail(final Motion motion, String workflowType) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+motion.getId()
			+" AND m.workflowType='"+workflowType+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			return new WorkflowDetails();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final Motion motion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			Workflow workflow = motion.findWorkflowFromStatus();
			if(workflow!=null) {
				workflowDetails = findCurrentWorkflowDetail(motion, workflow.getType());
			}			
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_motion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;		
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final CutMotionDate cutMotionDate, String workflowType) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+cutMotionDate.getId()
			+" AND m.workflowType='"+workflowType+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			return new WorkflowDetails();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final CutMotionDate cutMotionDate) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			Workflow workflow = Workflow.findByStatus(cutMotionDate.getInternalStatus(), cutMotionDate.getLocale());
			if(workflow!=null) {
				//workflowDetails = findCurrentWorkflowDetail(cutMotionDate, workflow.getType());
				workflowDetails = findCurrentWorkflowDetail(cutMotionDate, ApplicationConstants.APPROVAL_WORKFLOW);
			}			
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_motion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;		
	}
	
	public WorkflowDetails startProcess(final Motion motion, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(motion, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, "1");
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;	
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final Motion motion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, Integer.toString(level));
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;		
	}
	
	public WorkflowDetails create(final Motion motion,
			final Task task,
			final UserGroupType usergroupType,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
					UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), motion.getLocale());
					if(userGroup != null){
						userGroupId=String.valueOf(userGroup.getId());
						userGroupType=userGroup.getUserGroupType().getType();
						userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(motion!=null){
							if(motion.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(motion.getId()));
							}
							if(motion.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
								workflowDetails.setNumericalDevice(motion.getNumber().toString());
							}
							if(motion.getPrimaryMember()!=null){
								workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
							}
							if(motion.getType()!=null){
								workflowDetails.setDeviceType(motion.getType().getName());
							}
							if(motion.getHouseType()!=null){
								workflowDetails.setHouseType(motion.getHouseType().getName());
							}
							if(motion.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
							}
							workflowDetails.setLocale(motion.getLocale());
							if(motion.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							}
							
							workflowDetails.setRemarks(motion.getRemarks());
							if(motion.getSession()!=null){
								if(motion.getSession().getType()!=null){
									workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
							}
							if(motion.getAnsweringDate()!=null){
								workflowDetails.setAnsweringDate(motion.getAnsweringDate());
							}
							workflowDetails.setSubject(motion.getSubject());
							workflowDetails.setText(motion.getDetails());
							
							if(motion.getMinistry() != null){
								workflowDetails.setMinistry(motion.getMinistry().getName());
							}
							if(motion.getSubDepartment() != null){
								workflowDetails.setSubdepartment(motion.getSubDepartment().getName());
							}
							if(motion.getReply() != null && !motion.getReply().isEmpty()){
								workflowDetails.setReply(motion.getReply());
							}
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_MOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_MOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(motion.getRecommendationStatus().getType());
							} else {
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
				}			
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_motion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;	
	}
	
	public List<WorkflowDetails> create(final Motion domain,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) throws ELSException {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){
							Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroupType domainActorUserGroupType = null;
							if(domain.getActor() != null && !domain.getActor().isEmpty()){
								
								String[] actorValues = domain.getActor().split("#");						
								if(actorValues != null){
									domainActorUserGroupType = UserGroupType.findByType(actorValues[1], domain.getLocale());
								}
							}
							UserGroup userGroup = null;
							if(domainActorUserGroupType != null){
								userGroup = UserGroup.findActive(credential, domainActorUserGroupType, new Date(), domain.getLocale());
							}else{
								userGroup = UserGroup.findActive(credential, new Date(), "");
							}
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
							if(domain!=null){
								if(domain.getId()!=null){
									workflowDetails.setDeviceId(String.valueOf(domain.getId()));
								}
								if(domain.getNumber()!=null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
									workflowDetails.setNumericalDevice(domain.getNumber().toString());
								}
								if(domain.getPrimaryMember()!=null){
									workflowDetails.setDeviceOwner(domain.getPrimaryMember().getFullname());
								}
								if(domain.getType()!=null){
									workflowDetails.setDeviceType(domain.getType().getName());
								}
								if(domain.getHouseType()!=null){
									workflowDetails.setHouseType(domain.getHouseType().getName());
								}
								if(domain.getInternalStatus()!=null){
									workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
								}
								workflowDetails.setLocale(domain.getLocale());
								if(domain.getRecommendationStatus()!=null){
									workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
								}								
								workflowDetails.setRemarks(domain.getRemarks());
								if(domain.getSession()!=null){
									if(domain.getSession().getType()!=null){
										workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
								}
								if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
									workflowDetails.setSubject(domain.getRevisedSubject());
								}else{
									workflowDetails.setSubject(domain.getSubject());
								}
								if(domain.getRevisedDetails() != null && !domain.getRevisedDetails().isEmpty()){
									workflowDetails.setText(domain.getRevisedDetails());
								}else{
									workflowDetails.setText(domain.getDetails());
								}
								
								if(domain.getFile()!=null){
									workflowDetails.setFile(String.valueOf(domain.getFile()));
								}
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_MOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
								workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
								/**** Different types of workflow sub types ****/
							}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_MOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
								workflowDetails.setInternalStatus(null);
								workflowDetails.setRecommendationStatus(null);
							}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_motion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}
	public WorkflowDetails create(final Motion domain,final Task task,final String workflowType,
			final String level) throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup=UserGroup.findActive(credential, new Date(), "");
					userGroupId=String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(level);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(domain!=null){
						if(domain.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(domain.getId()));
						}
						if(domain.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
							workflowDetails.setNumericalDevice(domain.getNumber().toString());
						}
						if(domain.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(domain.getPrimaryMember().getFullname());
						}
						if(domain.getType()!=null){
							workflowDetails.setDeviceType(domain.getType().getName());
						}
						if(domain.getHouseType()!=null){
							workflowDetails.setHouseType(domain.getHouseType().getName());
						}
						if(domain.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
						}
						workflowDetails.setLocale(domain.getLocale());
						if(domain.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
						}						
						workflowDetails.setRemarks(domain.getRemarks());
						if(domain.getSession()!=null){
							if(domain.getSession().getType()!=null){
								workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
						}
						
						if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
							workflowDetails.setSubject(domain.getRevisedSubject());
						}else{
							workflowDetails.setSubject(domain.getSubject());
						}
						if((domain.getRevisedDetails() != null && !domain.getRevisedDetails().isEmpty())){
							workflowDetails.setText(domain.getRevisedDetails());
						}else{
							workflowDetails.setText(domain.getDetails());
						}
						
						if(domain.getFile()!=null){
							workflowDetails.setFile(String.valueOf(domain.getFile()));
						}
						if(domain.getMinistry() != null){
							workflowDetails.setMinistry(domain.getMinistry().getName());
						}
						if(domain.getSubDepartment() != null){
							workflowDetails.setSubdepartment(domain.getSubDepartment().getName());
						}
						if(domain.getReply() != null && !domain.getReply().isEmpty()){
							workflowDetails.setReply(domain.getReply());
						}
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_MOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
						/**** Different types of workflow sub types ****/
					}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_MOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
						workflowDetails.setInternalStatus(null);
						workflowDetails.setRecommendationStatus(null);
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_motion", "WorkflowDetails cannot be created");
			throw elsException;
		}	
		return workflowDetails;		
	}
	/**************Motion****************************/
	
	/******************StandaloneMotion**************/
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final StandaloneMotion motion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String currentDeviceTypeWorkflowType = Workflow.findByStatus(motion.getInternalStatus(), motion.getLocale()).getType();			
			
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", motion.getId().toString());
			query.setParameter("workflowType", currentDeviceTypeWorkflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_standalonemotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final StandaloneMotion motion, final String workflowType) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", motion.getId().toString());
			query.setParameter("workflowType",workflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_standalonemotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}

	public WorkflowDetails startProcess(final StandaloneMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(motion, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		Credential cr = Credential.findByFieldName(Credential.class, "username", temp[0], "");
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, "1");
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;	
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final StandaloneMotion motion, final String processDefinitionId, final Status status, final String usergroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionId);
		Map<String,String> properties = new HashMap<String, String>();
		Workflow workflow = Workflow.findByStatus(status, locale);
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(motion, workflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		if(processDefinitionId.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion,task,workflow.getType(),Integer.toString(level));
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final StandaloneMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, Integer.toString(level));
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;		
	}
	
	public WorkflowDetails create(final StandaloneMotion motion,
			final Task task,
			final UserGroupType usergroupType,
			final String workflowType,
			final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");					
					UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), motion.getLocale());
					if(userGroup != null){
						userGroupId=String.valueOf(userGroup.getId());
						userGroupType=userGroup.getUserGroupType().getType();
						userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(motion!=null){
							if(motion.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(motion.getId()));
							}
							if(motion.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
								workflowDetails.setNumericalDevice(motion.getNumber().toString());
							}
							if(motion.getPrimaryMember()!=null){
								workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
							}
							if(motion.getType()!=null){
								workflowDetails.setDeviceType(motion.getType().getName());
							}
							if(motion.getHouseType()!=null){
								workflowDetails.setHouseType(motion.getHouseType().getName());
							}
							if(motion.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
							}
							workflowDetails.setLocale(motion.getLocale());
							if(motion.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							}
							if(motion.getGroup()!=null){
								workflowDetails.setGroupNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getGroup().getNumber()));
							}

							if(motion.getFile()!=null){
								workflowDetails.setFile(String.valueOf(motion.getFile()));
							}

							workflowDetails.setRemarks(motion.getRemarks());
							if(motion.getSession()!=null){
								if(motion.getSession().getType()!=null){
									workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
							}	
							if(motion.getRevisedSubject() != null && !motion.getRevisedSubject().isEmpty()){
								workflowDetails.setSubject(motion.getRevisedSubject());
							}else{
								workflowDetails.setSubject(motion.getSubject());
							}
							if(motion.getReason() != null && !motion.getRevisedReason().isEmpty()){
								workflowDetails.setText(motion.getRevisedReason());
							}else{
								workflowDetails.setText(motion.getReason());
							}
							if(motion.getSubDepartment() != null){
								workflowDetails.setSubdepartment(motion.getSubDepartment().getName());
							}
							if(motion.getMinistry() != null){
								workflowDetails.setMinistry(motion.getMinistry().getName());
							}
							if(motion.getFactualPosition() != null && !motion.getFactualPosition().isEmpty()){
								workflowDetails.setReply(motion.getFactualPosition());
							}
							
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_STANDALONE);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_STANDALONE);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(motion.getRecommendationStatus().getType());
							} else {
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
				}			
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_standalone", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;	
	}
	
	public WorkflowDetails create(final StandaloneMotion motion,final Task task,
			final String workflowType,final String assigneeLevel) throws ELSException{
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					
					Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
					UserGroupType domainActorUserGroupType = null;
					if(motion.getActor() != null && !motion.getActor().isEmpty()){
						
						String[] actorValues = motion.getActor().split("#");						
						if(actorValues != null){
							domainActorUserGroupType = UserGroupType.findByType(actorValues[1], motion.getLocale());
						}
					}
					UserGroup userGroup = null;
					if(domainActorUserGroupType != null){
						userGroup = UserGroup.findActive(credential, domainActorUserGroupType, new Date(), motion.getLocale());
					}else{
						userGroup = UserGroup.findActive(credential, new Date(), "");
					}
					
					userGroupId = String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(assigneeLevel);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(motion!=null){
						if(motion.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(motion.getId()));
						}
						if(motion.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
							workflowDetails.setNumericalDevice(motion.getNumber().toString());
						}
						if(motion.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
						}
						if(motion.getType()!=null){
							workflowDetails.setDeviceType(motion.getType().getName());
						}
						if(motion.getHouseType()!=null){
							workflowDetails.setHouseType(motion.getHouseType().getName());
						}
						if(motion.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
						}
						workflowDetails.setLocale(motion.getLocale());
						if(motion.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
						}
						if(motion.getGroup()!=null){
							workflowDetails.setGroupNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getGroup().getNumber()));
						}
						
						workflowDetails.setRemarks(motion.getRemarks());
						if(motion.getSession()!=null){
							if(motion.getSession().getType()!=null){
								workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
						}
						
						if(motion.getFile() != null){
							workflowDetails.setFile(motion.getFile().toString());
						}
						if(motion.getRevisedSubject() != null && !motion.getRevisedSubject().isEmpty()){
							workflowDetails.setSubject(motion.getRevisedSubject());
						}else{
							workflowDetails.setSubject(motion.getSubject());
						}
						if(motion.getRevisedReason() != null && !motion.getRevisedReason().isEmpty()){
							workflowDetails.setText(motion.getRevisedReason());
						}else{
							workflowDetails.setText(motion.getReason());
						}
						if(motion.getSubDepartment() != null){
							workflowDetails.setSubdepartment(motion.getSubDepartment().getName());
						}
						if(motion.getMinistry() != null){
							workflowDetails.setMinistry(motion.getMinistry().getName());
						}
						if(motion.getFactualPosition() != null && !motion.getFactualPosition().isEmpty()){
							workflowDetails.setReply(motion.getFactualPosition());
						}
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					/**** To make the HDS to take the workflow with mailer task ****/
					String currentDeviceTypeWorkflowType = null;
					if(motion.getType()  != null){
						if(motion.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE) 
								&& motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
						}else{
							currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
						}
					}
					
					if(workflowType.equals(currentDeviceTypeWorkflowType)){
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_STANDALONE);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());					
						/**** Different types of workflow sub types ****/
					}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_STANDALONE);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
					}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_standalonemotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;		
	}
	
	public List<WorkflowDetails> create(final StandaloneMotion motion,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) throws ELSException {
		List<WorkflowDetails> workflowDetailsList = new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus = Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter != null){
				SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){

							String domainActorUserGroupType = null;
							UserGroupType ugT = null;
							if(motion.getActor() != null && !motion.getActor().isEmpty()){
								
								String[] actorValues = motion.getActor().split("#");						
								if(actorValues != null){
									domainActorUserGroupType = actorValues[1];
									ugT = UserGroupType.findByType(domainActorUserGroupType, motion.getLocale());
								}
							}
							Credential credential = Credential.findByFieldName(Credential.class, "username", username, null);
							UserGroup userGroup = null;
							if(ugT != null){
								userGroup = UserGroup.findActive(credential, ugT, new Date(), "");
							}else{
								userGroup = UserGroup.findActive(credential, new Date(), "");
							}

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
							if(motion!=null){
								if(motion.getId()!=null){
									workflowDetails.setDeviceId(String.valueOf(motion.getId()));
								}
								if(motion.getNumber()!=null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
									workflowDetails.setNumericalDevice(motion.getNumber().toString());
								}
								if(motion.getPrimaryMember()!=null){
									workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
								}
								if(motion.getType()!=null){
									workflowDetails.setDeviceType(motion.getType().getName());
								}
								if(motion.getHouseType()!=null){
									workflowDetails.setHouseType(motion.getHouseType().getName());
								}
								if(motion.getInternalStatus()!=null){
									workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
								}
								workflowDetails.setLocale(motion.getLocale());
								if(motion.getRecommendationStatus()!=null){
									workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
								}
								if(motion.getGroup()!=null){
									workflowDetails.setGroupNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getGroup().getNumber()));
								}
								workflowDetails.setRemarks(motion.getRemarks());
								if(motion.getSession()!=null){
									if(motion.getSession().getType()!=null){
										workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
								}
								
								if(motion.getFile() != null){
									workflowDetails.setFile(motion.getFile().toString());
								}
								if(motion.getRevisedSubject() != null && !motion.getRevisedSubject().isEmpty()){
									workflowDetails.setSubject(motion.getRevisedSubject());
								}else{
									workflowDetails.setSubject(motion.getSubject());
								}
								
								if(motion.getRevisedReason() != null && !motion.getRevisedReason().isEmpty()){
									workflowDetails.setText(motion.getRevisedReason());
								}else{
									workflowDetails.setText(motion.getReason());
								}
								
								if(motion.getSubDepartment() != null){
									workflowDetails.setSubdepartment(motion.getSubDepartment().getName());
								}
								if(motion.getMinistry() != null){
									workflowDetails.setMinistry(motion.getMinistry().getName());
								}
								if(motion.getFactualPosition() != null && !motion.getFactualPosition().isEmpty()){
									workflowDetails.setReply(motion.getFactualPosition());
								}
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							/**** To make the HDS to take the workflow with mailer task ****/
							String currentDeviceTypeWorkflowType = null;
							if(motion.getType()  != null){
								if(motion.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)
										&& motion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
								}else{
									currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
								}
							}
							
							if(workflowType.equals(currentDeviceTypeWorkflowType)){
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_STANDALONE);
								workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());					
								/**** Different types of workflow sub types ****/
							}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_STANDALONE);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
							}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_standalonemotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}	
	/******************StandaloneMotion**************/
	
	/**************CutMotion************************/
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final CutMotion motion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
			
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", motion.getId().toString());
			query.setParameter("workflowType",currentDeviceTypeWorkflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_cutmotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	public WorkflowDetails findCurrentWorkflowDetail(final CutMotion motion, String workflowType) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+motion.getId()
			+" AND m.workflowType='"+workflowType+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			return new WorkflowDetails();
		}		
	}
	
	public WorkflowDetails startProcess(final CutMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(motion, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getDeviceType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, "1");
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;	
	}
	public WorkflowDetails startProcessAtGivenLevel(final CutMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getDeviceType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, Integer.toString(level));
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;		
	}
	
	public WorkflowDetails create(final CutMotion motion,final Task task, final UserGroupType usergroupType,
			final String workflowType,final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails = new WorkflowDetails();
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
					UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), motion.getLocale());
					if(userGroup != null){
						String userGroupId=String.valueOf(userGroup.getId());
						String userGroupType=userGroup.getUserGroupType().getType();
						String userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(motion!=null){
							if(motion.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(motion.getId()));
							}
							if(motion.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
								workflowDetails.setNumericalDevice(motion.getNumber().toString());
							}
							if(motion.getPrimaryMember()!=null){
								workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
							}
							if(motion.getDeviceType()!=null){
								workflowDetails.setDeviceType(motion.getDeviceType().getName());
							}
							if(motion.getHouseType()!=null){
								workflowDetails.setHouseType(motion.getHouseType().getName());
							}
							if(motion.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
							}
							workflowDetails.setLocale(motion.getLocale());
							if(motion.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							}
							
							workflowDetails.setRemarks(motion.getRemarks());
							if(motion.getSession()!=null){
								if(motion.getSession().getType()!=null){
									workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
							}
							if(motion.getAnsweringDate()!=null){
								workflowDetails.setAnsweringDate(motion.getAnsweringDate());
							}
							
							if(motion.getRevisedMainTitle() != null && !motion.getRevisedMainTitle().isEmpty()){
								workflowDetails.setSubject(motion.getRevisedMainTitle());
							}else{
								workflowDetails.setSubject(motion.getMainTitle());
							}
							if(motion.getRevisedNoticeContent() != null && !motion.getRevisedNoticeContent().isEmpty()){
								workflowDetails.setText(motion.getRevisedNoticeContent());
							}else{
								workflowDetails.setText(motion.getNoticeContent());
							}
							if(motion.getSubDepartment() != null){
								workflowDetails.setSubdepartment(motion.getSubDepartment().getName());
							}
							if(motion.getMinistry() != null){
								workflowDetails.setMinistry(motion.getMinistry().getName());
							}
							if(motion.getReply() != null && !motion.getReply().isEmpty()){
								workflowDetails.setReply(motion.getReply());
							}
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_CUTMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_CUTMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(motion.getRecommendationStatus().getType());
							} else {
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
				}			
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_cutmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;	
	}
	
	public WorkflowDetails create(final CutMotion motion,final Task task, 
			final String workflowType,final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails = new WorkflowDetails();
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup=UserGroup.findActive(credential, new Date(), motion.getLocale());
					if(userGroup != null){
						String userGroupId=String.valueOf(userGroup.getId());
						String userGroupType=userGroup.getUserGroupType().getType();
						String userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(motion!=null){
							if(motion.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(motion.getId()));
							}
							if(motion.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
								workflowDetails.setNumericalDevice(motion.getNumber().toString());
							}
							if(motion.getPrimaryMember()!=null){
								workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
							}
							if(motion.getDeviceType()!=null){
								workflowDetails.setDeviceType(motion.getDeviceType().getName());
							}
							if(motion.getHouseType()!=null){
								workflowDetails.setHouseType(motion.getHouseType().getName());
							}
							if(motion.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
							}
							workflowDetails.setLocale(motion.getLocale());
							if(motion.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							}
							
							workflowDetails.setRemarks(motion.getRemarks());
							if(motion.getSession()!=null){
								if(motion.getSession().getType()!=null){
									workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
							}
							if(motion.getAnsweringDate()!=null){
								workflowDetails.setAnsweringDate(motion.getAnsweringDate());
							}
							if(motion.getRevisedMainTitle() != null && !motion.getRevisedMainTitle().isEmpty()){
								workflowDetails.setSubject(motion.getRevisedMainTitle());
							}else{
								workflowDetails.setSubject(motion.getMainTitle());
							}
							if(motion.getRevisedNoticeContent() != null && !motion.getRevisedNoticeContent().isEmpty()){
								workflowDetails.setText(motion.getRevisedNoticeContent());
							}else{
								workflowDetails.setText(motion.getNoticeContent());
							}
							if(motion.getSubDepartment() != null){
								workflowDetails.setSubdepartment(motion.getSubDepartment().getName());
							}
							if(motion.getMinistry() != null){
								workflowDetails.setMinistry(motion.getMinistry().getName());
							}
							if(motion.getReply() != null && !motion.getReply().isEmpty()){
								workflowDetails.setReply(motion.getReply());
							}
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_CUTMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_CUTMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(motion.getRecommendationStatus().getType());
							} else {
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
				}			
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_cutmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;	
	}
	
	public List<WorkflowDetails> create(final CutMotion domain,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) throws ELSException {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus = Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter != null){
				SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i : tasks){
					WorkflowDetails workflowDetails = new WorkflowDetails();
					String userGroupId = null;
					String userGroupType = null;
					String userGroupName = null;				
					String username = i.getAssignee();
					if(username != null){
						if(!username.isEmpty()){
							Credential credential = Credential.findByFieldName(Credential.class,"username",username,"");
							//UserGroup userGroup = UserGroup.findByFieldName(UserGroup.class,"credential",credential, domain.getLocale());
							UserGroup userGroup = UserGroup.findActive(credential, new Date(), domain.getLocale());
							userGroupId = String.valueOf(userGroup.getId());
							userGroupType = userGroup.getUserGroupType().getType();
							userGroupName = userGroup.getUserGroupType().getName();
							workflowDetails.setAssignee(i.getAssignee());
							workflowDetails.setAssigneeUserGroupId(userGroupId);
							workflowDetails.setAssigneeUserGroupType(userGroupType);
							workflowDetails.setAssigneeUserGroupName(userGroupName);
							workflowDetails.setAssigneeLevel(assigneeLevel);
							if(i.getCreateTime() != null){
								if(!i.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(i.getCreateTime()));
								}
							}
							if(domain != null){
								if(domain.getId() != null){
									workflowDetails.setDeviceId(String.valueOf(domain.getId()));
								}
								if(domain.getNumber() != null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
									workflowDetails.setNumericalDevice(domain.getNumber().toString());
								}
								if(domain.getPrimaryMember() != null){
									workflowDetails.setDeviceOwner(domain.getPrimaryMember().getFullname());
								}
								if(domain.getDeviceType() != null){
									workflowDetails.setDeviceType(domain.getDeviceType().getName());
								}
								if(domain.getHouseType() != null){
									workflowDetails.setHouseType(domain.getHouseType().getName());
								}
								if(domain.getInternalStatus() != null){
									workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
								}
								workflowDetails.setLocale(domain.getLocale());
								if(domain.getRecommendationStatus() != null){
									workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
								}								
								workflowDetails.setRemarks(domain.getRemarks());
								if(domain.getSession() != null){
									if(domain.getSession().getType() != null){
										workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
								}
								if(domain.getRevisedMainTitle() != null && !domain.getRevisedMainTitle().isEmpty()){
									workflowDetails.setSubject(domain.getRevisedMainTitle());
								}else{
									workflowDetails.setSubject(domain.getMainTitle());
								}
								if(domain.getRevisedNoticeContent() != null && !domain.getRevisedNoticeContent().isEmpty()){
									workflowDetails.setText(domain.getRevisedNoticeContent());
								}else{
									workflowDetails.setText(domain.getNoticeContent());
								}
								if(domain.getFile() != null){
									workflowDetails.setFile(String.valueOf(domain.getFile()));
								}
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_CUTMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern() + "/" + userGroupType);
								workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
								/**** Different types of workflow sub types ****/
							}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_CUTMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus != null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
								workflowDetails.setInternalStatus(null);
								workflowDetails.setRecommendationStatus(null);
							}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_cutmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}
	/**************CutMotion************************/
	
	/**************EventMotion************************/
	public WorkflowDetails findCurrentWorkflowDetail(final EventMotion motion, String workflowType) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+motion.getId()
			+" AND m.workflowType='"+workflowType+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			return new WorkflowDetails();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final EventMotion motion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
			
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", motion.getId().toString());
			query.setParameter("workflowType",currentDeviceTypeWorkflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_cutmotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	public WorkflowDetails startProcess(final EventMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(motion, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getDeviceType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, "1");
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;	
	}	
	
	public WorkflowDetails create(final EventMotion motion,final Task task, final UserGroupType usergroupType,
			final String workflowType,final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails = new WorkflowDetails();
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
					UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), motion.getLocale());
					if(userGroup != null){
						String userGroupId=String.valueOf(userGroup.getId());
						String userGroupType=userGroup.getUserGroupType().getType();
						String userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(motion!=null){
							if(motion.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(motion.getId()));
							}
							if(motion.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
								workflowDetails.setNumericalDevice(motion.getNumber().toString());
							}
							if(motion.getMember()!=null){
								workflowDetails.setDeviceOwner(motion.getMember().getFullname());
							}
							if(motion.getDeviceType()!=null){
								workflowDetails.setDeviceType(motion.getDeviceType().getName());
							}
							if(motion.getHouseType()!=null){
								workflowDetails.setHouseType(motion.getHouseType().getName());
							}
							if(motion.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
							}
							workflowDetails.setLocale(motion.getLocale());
							if(motion.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							}
							
							workflowDetails.setRemarks(motion.getRemarks());
							if(motion.getSession()!=null){
								if(motion.getSession().getType()!=null){
									workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
							}
							if(motion.getDiscussionDate()!=null){
								workflowDetails.setAnsweringDate(motion.getDiscussionDate());
							}
							if(motion.getRevisedEventTitle() != null && !motion.getRevisedEventTitle().isEmpty()){
								workflowDetails.setSubject(motion.getRevisedEventTitle());
							}else{
								workflowDetails.setSubject(motion.getEventTitle());
							}
							if(motion.getRevisedDescription() != null && !motion.getRevisedDescription().isEmpty()){
								workflowDetails.setText(motion.getRevisedDescription());
							}else{
								workflowDetails.setText(motion.getDescription());
							}
							
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_EVENTMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_EVENTMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(motion.getRecommendationStatus().getType());
							} else {
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
				}			
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_eventmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;	
	}
	
	public List<WorkflowDetails> create(final EventMotion domain,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) throws ELSException {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus = Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter != null){
				SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i : tasks){
					WorkflowDetails workflowDetails = new WorkflowDetails();
					String userGroupId = null;
					String userGroupType = null;
					String userGroupName = null;				
					String username = i.getAssignee();
					if(username != null){
						if(!username.isEmpty()){
							Credential credential = Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroupType domainActorUserGroupType = null;
							if(domain.getActor() != null && !domain.getActor().isEmpty()){
								
								String[] actorValues = domain.getActor().split("#");						
								if(actorValues != null){
									domainActorUserGroupType = UserGroupType.findByType(actorValues[1], domain.getLocale());
								}
							}
							UserGroup userGroup = null;
							if(domainActorUserGroupType != null){
								userGroup = UserGroup.findActive(credential, domainActorUserGroupType, new Date(), domain.getLocale());
							}else{
								userGroup = UserGroup.findActive(credential, new Date(), "");
							}
							userGroupId = String.valueOf(userGroup.getId());
							userGroupType = userGroup.getUserGroupType().getType();
							userGroupName = userGroup.getUserGroupType().getName();
							workflowDetails.setAssignee(i.getAssignee());
							workflowDetails.setAssigneeUserGroupId(userGroupId);
							workflowDetails.setAssigneeUserGroupType(userGroupType);
							workflowDetails.setAssigneeUserGroupName(userGroupName);
							workflowDetails.setAssigneeLevel(assigneeLevel);
							if(i.getCreateTime() != null){
								if(!i.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(i.getCreateTime()));
								}
							}
							if(domain != null){
								if(domain.getId() != null){
									workflowDetails.setDeviceId(String.valueOf(domain.getId()));
								}
								if(domain.getNumber() != null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
									workflowDetails.setNumericalDevice(domain.getNumber().toString());
								}
								if(domain.getMember() != null){
									workflowDetails.setDeviceOwner(domain.getMember().getFullname());
								}
								if(domain.getDeviceType() != null){
									workflowDetails.setDeviceType(domain.getDeviceType().getName());
								}
								if(domain.getHouseType() != null){
									workflowDetails.setHouseType(domain.getHouseType().getName());
								}
								if(domain.getInternalStatus() != null){
									workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
								}
								workflowDetails.setLocale(domain.getLocale());
								if(domain.getRecommendationStatus() != null){
									workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
								}								
								workflowDetails.setRemarks(domain.getRemarks());
								if(domain.getSession() != null){
									if(domain.getSession().getType() != null){
										workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
								}
								if(domain.getRevisedEventTitle() != null && !domain.getRevisedEventTitle().isEmpty()){
									workflowDetails.setSubject(domain.getRevisedEventTitle());
								}else{
									workflowDetails.setSubject(domain.getEventTitle());
								}
								if(domain.getRevisedDescription() != null && !domain.getRevisedDescription().isEmpty()){
									workflowDetails.setText(domain.getRevisedDescription());
								}else{
									workflowDetails.setText(domain.getDescription());
								}
								if(domain.getFile() != null){
									workflowDetails.setFile(String.valueOf(domain.getFile()));
								}
								
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_CUTMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern() + "/" + userGroupType);
								workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
								/**** Different types of workflow sub types ****/
							}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_CUTMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus != null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
								workflowDetails.setInternalStatus(null);
								workflowDetails.setRecommendationStatus(null);
							}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_cutmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}
	
	public WorkflowDetails create(final EventMotion domain,final Task task,final String workflowType,
			final String level) throws ELSException {
		
		WorkflowDetails workflowDetails = new WorkflowDetails();
		String userGroupId = null;
		String userGroupType = null;
		String userGroupName = null;				
		
		try {
			String username = task.getAssignee();
			if (username != null) {
				if(!username.isEmpty()){
					Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
					UserGroupType domainActorUserGroupType = null;
					if(domain.getActor() != null && !domain.getActor().isEmpty()){
						
						String[] actorValues = domain.getActor().split("#");						
						if(actorValues != null){
							domainActorUserGroupType = UserGroupType.findByType(actorValues[1], domain.getLocale());
						}
					}
					UserGroup userGroup = null;
					if(domainActorUserGroupType != null){
						userGroup = UserGroup.findActive(credential, domainActorUserGroupType, new Date(), domain.getLocale());
					}else{
						userGroup = UserGroup.findActive(credential, new Date(), "");
					}
					
					userGroupId = String.valueOf(userGroup.getId());
					userGroupType = userGroup.getUserGroupType().getType();
					userGroupName = userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(level);
					CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter != null){
						SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime() != null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if (domain != null) {
						if (domain.getId() != null) {
							workflowDetails.setDeviceId(String.valueOf(domain.getId()));
						}
						if (domain.getNumber() != null) {
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
							workflowDetails.setNumericalDevice(domain.getNumber().toString());
						}
						if (domain.getMember() != null) {
							workflowDetails.setDeviceOwner(domain.getMember().getFullname());
						}
						if (domain.getDeviceType() != null) {
							workflowDetails.setDeviceType(domain.getDeviceType().getName());
						}
						if (domain.getHouseType() != null) {
							workflowDetails.setHouseType(domain.getHouseType().getName());
						}
						if (domain.getInternalStatus() != null) {
							workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
						}
						workflowDetails.setLocale(domain.getLocale());
						if (domain.getRecommendationStatus() != null) {
							workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
						}						
						workflowDetails.setRemarks(domain.getRemarks());
						if (domain.getSession() != null) {
							if (domain.getSession().getType() != null) {
								workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
						}
						if(domain.getRevisedEventTitle() != null && !domain.getRevisedEventTitle().isEmpty()){
							workflowDetails.setSubject(domain.getRevisedEventTitle());
						}else{
							workflowDetails.setSubject(domain.getEventTitle());
						}
						if(domain.getRevisedDescription() != null && !domain.getRevisedDescription().isEmpty()){
							workflowDetails.setText(domain.getRevisedDescription());
						}else{
							workflowDetails.setText(domain.getDescription());
						}
						
						if (domain.getFile() != null) {
							workflowDetails.setFile(String.valueOf(domain.getFile()));
						}
					}
					
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
						
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_EVENTMOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
						/**** Different types of workflow sub types ****/
					}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_EVENTMOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus = Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
						if(requestStatus != null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
						workflowDetails.setInternalStatus(null);
						workflowDetails.setRecommendationStatus(null);
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_cutmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}	
		return workflowDetails;		
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final EventMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getDeviceType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, Integer.toString(level));
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;		
	}
	/*********************EventMotion*********************/
	
	/********************DiscussionMotion******************/
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final DiscussionMotion motion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
			
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", motion.getId().toString());
			query.setParameter("workflowType",currentDeviceTypeWorkflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_cutmotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
		
	public WorkflowDetails findCurrentWorkflowDetail(final DiscussionMotion motion, String workflowType) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+motion.getId()
			+" AND m.workflowType='"+workflowType+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC;
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).setMaxResults(1).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			e.printStackTrace();
			return new WorkflowDetails();
		}		
	}
	
	public List<WorkflowDetails> create(final DiscussionMotion domain,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) throws ELSException {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				for(Task i:tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){
							Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
							UserGroupType domainActorUserGroupType = null;
							if(domain.getActor() != null && !domain.getActor().isEmpty()){
								
								String[] actorValues = domain.getActor().split("#");						
								if(actorValues != null){
									domainActorUserGroupType = UserGroupType.findByType(actorValues[1], domain.getLocale());
								}
							}
							UserGroup userGroup = null;
							if(domainActorUserGroupType != null){
								userGroup = UserGroup.findActive(credential, domainActorUserGroupType, new Date(), domain.getLocale());
							}else{
								userGroup = UserGroup.findActive(credential, new Date(), "");
							}
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
							if(domain!=null){
								if(domain.getId()!=null){
									workflowDetails.setDeviceId(String.valueOf(domain.getId()));
								}
								if(domain.getNumber()!=null){
									workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
								}
								if(domain.getPrimaryMember()!=null){
									workflowDetails.setDeviceOwner(domain.getPrimaryMember().getFullname());
								}
								if(domain.getType()!=null){
									workflowDetails.setDeviceType(domain.getType().getName());
								}
								if(domain.getHouseType()!=null){
									workflowDetails.setHouseType(domain.getHouseType().getName());
								}
								if(domain.getInternalStatus()!=null){
									workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
								}
								workflowDetails.setLocale(domain.getLocale());
								if(domain.getRecommendationStatus()!=null){
									workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
								}								
								workflowDetails.setRemarks(domain.getRemarks());
								if(domain.getSession()!=null){
									if(domain.getSession().getType()!=null){
										workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
								}
								if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
									workflowDetails.setSubject(domain.getRevisedSubject());
								}else{
									workflowDetails.setSubject(domain.getSubject());
								}
								if(domain.getRevisedNoticeContent() != null && !domain.getRevisedNoticeContent().isEmpty()){
									workflowDetails.setText(domain.getRevisedNoticeContent());
								}else{
									workflowDetails.setText(domain.getNoticeContent());
								}
								
								if(domain.getFile()!=null){
									workflowDetails.setFile(String.valueOf(domain.getFile()));
								}
								
								if(domain.getClarification() != null && !domain.getClarification().isEmpty()){
									workflowDetails.setReply(domain.getClarification());
								}
								if(domain.getMinistries() !=null && !domain.getMinistries().isEmpty()){
									List<Ministry> ministries = new ArrayList<Ministry>();
									String strMinistries = "";
									for(Ministry m : ministries){
										strMinistries = strMinistries + m.getName() + "##";
									}
									workflowDetails.setMinistry(strMinistries);
								}
								if(domain.getSubDepartments() !=null && !domain.getSubDepartments().isEmpty()){
									List<SubDepartment> subdepartments = new ArrayList<SubDepartment>();
									String strSubdepartments = "";
									for(SubDepartment s : subdepartments){
										strSubdepartments = strSubdepartments + s.getName() + "##";
									}
									workflowDetails.setSubdepartment(strSubdepartments);
								}
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_DISCUSSIONMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
								workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
								/**** Different types of workflow sub types ****/
							}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_DISCUSSIONMOTION);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								if(requestStatus!=null){
								workflowDetails.setWorkflowSubType(requestStatus.getType());
								}
								workflowDetails.setInternalStatus(null);
								workflowDetails.setRecommendationStatus(null);
							}
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_motion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetailsList;	
	}
	
	public WorkflowDetails create(final DiscussionMotion domain,final Task task,final String workflowType,
			final String level) throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		String userGroupId=null;
		String userGroupType=null;
		String userGroupName=null;				
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					
					Credential credential = Credential.findByFieldName(Credential.class, "username", username, "");
					UserGroupType domainActorUserGroupType = null;
					if(domain.getActor() != null && !domain.getActor().isEmpty()){
						
						String[] actorValues = domain.getActor().split("#");						
						if(actorValues != null){
							domainActorUserGroupType = UserGroupType.findByType(actorValues[1], domain.getLocale());
						}
					}
					UserGroup userGroup = null;
					if(domainActorUserGroupType != null){
						userGroup = UserGroup.findActive(credential, domainActorUserGroupType, new Date(), domain.getLocale());
					}else{
						userGroup = UserGroup.findActive(credential, new Date(), "");
					}
					userGroupId=String.valueOf(userGroup.getId());
					userGroupType=userGroup.getUserGroupType().getType();
					userGroupName=userGroup.getUserGroupType().getName();
					workflowDetails.setAssignee(task.getAssignee());
					workflowDetails.setAssigneeUserGroupId(userGroupId);
					workflowDetails.setAssigneeUserGroupType(userGroupType);
					workflowDetails.setAssigneeUserGroupName(userGroupName);
					workflowDetails.setAssigneeLevel(level);
					CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
					if(customParameter!=null){
						SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
						if(task.getCreateTime()!=null){
							if(!task.getCreateTime().isEmpty()){
								workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
							}
						}
					}	
					if(domain!=null){
						if(domain.getId()!=null){
							workflowDetails.setDeviceId(String.valueOf(domain.getId()));
						}
						if(domain.getNumber()!=null){
							workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getNumber()));
						}
						if(domain.getPrimaryMember()!=null){
							workflowDetails.setDeviceOwner(domain.getPrimaryMember().getFullname());
						}
						if(domain.getType()!=null){
							workflowDetails.setDeviceType(domain.getType().getName());
						}
						if(domain.getHouseType()!=null){
							workflowDetails.setHouseType(domain.getHouseType().getName());
						}
						if(domain.getInternalStatus()!=null){
							workflowDetails.setInternalStatus(domain.getInternalStatus().getName());
						}
						workflowDetails.setLocale(domain.getLocale());
						if(domain.getRecommendationStatus()!=null){
							workflowDetails.setRecommendationStatus(domain.getRecommendationStatus().getName());
						}						
						workflowDetails.setRemarks(domain.getRemarks());
						if(domain.getSession()!=null){
							if(domain.getSession().getType()!=null){
								workflowDetails.setSessionType(domain.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).format(domain.getSession().getYear()));
						}
						if(domain.getRevisedSubject() != null && !domain.getRevisedSubject().isEmpty()){
							workflowDetails.setSubject(domain.getRevisedSubject());
						}else{
							workflowDetails.setSubject(domain.getSubject());
						}
						if(domain.getRevisedNoticeContent() != null && !domain.getRevisedNoticeContent().isEmpty()){
							workflowDetails.setText(domain.getRevisedNoticeContent());
						}else{
							workflowDetails.setText(domain.getNoticeContent());
						}
						if(domain.getFile()!=null){
							workflowDetails.setFile(String.valueOf(domain.getFile()));
						}
						if(domain.getClarification() != null && !domain.getClarification().isEmpty()){
							workflowDetails.setReply(domain.getClarification());
						}
						if(domain.getMinistries() !=null && !domain.getMinistries().isEmpty()){
							List<Ministry> ministries = new ArrayList<Ministry>();
							String strMinistries = "";
							for(Ministry m : ministries){
								strMinistries = strMinistries + m.getName() + "##";
							}
							workflowDetails.setMinistry(strMinistries);
						}
						if(domain.getSubDepartments() !=null && !domain.getSubDepartments().isEmpty()){
							List<SubDepartment> subdepartments = new ArrayList<SubDepartment>();
							String strSubdepartments = "";
							for(SubDepartment s : subdepartments){
								strSubdepartments = strSubdepartments + s.getName() + "##";
							}
							workflowDetails.setSubdepartment(strSubdepartments);
						}
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_DISCUSSIONMOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						workflowDetails.setWorkflowSubType(domain.getInternalStatus().getType());					
						/**** Different types of workflow sub types ****/
					}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_DISCUSSIONMOTION);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, domain.getLocale());
						if(requestStatus!=null){
						workflowDetails.setWorkflowSubType(requestStatus.getType());
						}
						workflowDetails.setInternalStatus(null);
						workflowDetails.setRecommendationStatus(null);
					}
					workflowDetails.persist();
				}				
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_motion", "WorkflowDetails cannot be created");
			throw elsException;
		}	
		return workflowDetails;		
	}
	
	public WorkflowDetails create(final DiscussionMotion motion,final Task task, final UserGroupType usergroupType,
			final String workflowType,final String assigneeLevel) throws ELSException {
		WorkflowDetails workflowDetails = new WorkflowDetails();
		try {
			String username=task.getAssignee();
			if(username!=null){
				if(!username.isEmpty()){
					
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
					UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), motion.getLocale());
					if(userGroup != null){
						String userGroupId=String.valueOf(userGroup.getId());
						String userGroupType=userGroup.getUserGroupType().getType();
						String userGroupName=userGroup.getUserGroupType().getName();
						workflowDetails.setAssignee(task.getAssignee());
						workflowDetails.setAssigneeUserGroupId(userGroupId);
						workflowDetails.setAssigneeUserGroupType(userGroupType);
						workflowDetails.setAssigneeUserGroupName(userGroupName);
						workflowDetails.setAssigneeLevel(assigneeLevel);
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
						if(customParameter!=null){
							SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
							if(task.getCreateTime()!=null){
								if(!task.getCreateTime().isEmpty()){
									workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
								}
							}
						}	
						if(motion!=null){
							if(motion.getId()!=null){
								workflowDetails.setDeviceId(String.valueOf(motion.getId()));
							}
							if(motion.getNumber()!=null){
								workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
								workflowDetails.setNumericalDevice(motion.getNumber().toString());
							}
							if(motion.getPrimaryMember()!=null){
								workflowDetails.setDeviceOwner(motion.getPrimaryMember().getFullname());
							}
							if(motion.getType()!=null){
								workflowDetails.setDeviceType(motion.getType().getName());
							}
							if(motion.getHouseType()!=null){
								workflowDetails.setHouseType(motion.getHouseType().getName());
							}
							if(motion.getInternalStatus()!=null){
								workflowDetails.setInternalStatus(motion.getInternalStatus().getName());
							}
							workflowDetails.setLocale(motion.getLocale());
							if(motion.getRecommendationStatus()!=null){
								workflowDetails.setRecommendationStatus(motion.getRecommendationStatus().getName());
							}
							
							workflowDetails.setRemarks(motion.getRemarks());
							if(motion.getSession()!=null){
								if(motion.getSession().getType()!=null){
									workflowDetails.setSessionType(motion.getSession().getType().getSessionType());
								}
								workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getSession().getYear()));
							}
							if(motion.getDiscussionDate()!=null){
								workflowDetails.setAnsweringDate(motion.getDiscussionDate());
							}
							if(motion.getRevisedSubject() != null && !motion.getRevisedSubject().isEmpty()){
								workflowDetails.setSubject(motion.getRevisedSubject());
							}else{
								workflowDetails.setSubject(motion.getSubject());
							}
							if(motion.getRevisedNoticeContent() != null && !motion.getRevisedNoticeContent().isEmpty()){
								workflowDetails.setText(motion.getRevisedNoticeContent());
							}else{
								workflowDetails.setText(motion.getNoticeContent());
							}
						}
						workflowDetails.setProcessId(task.getProcessInstanceId());
						workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
						workflowDetails.setTaskId(task.getId());
						workflowDetails.setWorkflowType(workflowType);
						
						if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
							workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_DISCUSSIONMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern());
							Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, motion.getLocale());
							if(requestStatus!=null){
							workflowDetails.setWorkflowSubType(requestStatus.getType());
							}
						} else {
							workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_DISCUSSIONMOTION);
							workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
							if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
									|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
								workflowDetails.setWorkflowSubType(motion.getRecommendationStatus().getType());
							} else {
								workflowDetails.setWorkflowSubType(motion.getInternalStatus().getType());
							}
							workflowDetails.persist();
						}
					}
				}			
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_eventmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;	
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final DiscussionMotion motion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(motion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(motion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], motion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(motion, task, usergroupType, workflowType, Integer.toString(level));
		motion.setEndFlag("continue");
		motion.setTaskReceivedOn(new Date());
		motion.setWorkflowDetailsId(workflowDetails.getId());
		motion.setWorkflowStarted("YES");
		motion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		motion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		motion.setLevel(actorArr[2]);
		motion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		motion.simpleMerge();
		return workflowDetails;		
	}
	/********************DiscussionMotion******************/
	
	//======================Adjournment Motion Methods=====================//
	public WorkflowDetails create(final AdjournmentMotion adjournmentMotion,
								  final Task task,
								  final UserGroupType usergroupType,
								  final String workflowType,
								  final String assigneeLevel) 
								  throws ELSException {
		WorkflowDetails workflowDetails=new WorkflowDetails();
		try {
			String userGroupId=null;
			String userGroupType=null;
			String userGroupName=null;				
			String username=task.getAssignee();
			if(username==null || username.isEmpty()){
				throw new ELSException("WorkflowDetailsRepository_WorkflowDetail_create_adjournmentmotion_Task", "assignee is not set for the motion task");					
			}
			Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
			//UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
			UserGroup userGroup = UserGroup.findActive(credential, usergroupType, new Date(), adjournmentMotion.getLocale());
			if(userGroup == null){
				throw new ELSException("WorkflowDetailsRepository_WorkflowDetail_create_adjournmentmotion_Task", "there is no active usergroup for the assignee");
			}
			userGroupId=String.valueOf(userGroup.getId());
			userGroupType=userGroup.getUserGroupType().getType();
			userGroupName=userGroup.getUserGroupType().getName();
			workflowDetails.setAssignee(task.getAssignee());
			workflowDetails.setAssigneeUserGroupId(userGroupId);
			workflowDetails.setAssigneeUserGroupType(userGroupType);
			workflowDetails.setAssigneeUserGroupName(userGroupName);
			workflowDetails.setAssigneeLevel(assigneeLevel);
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
				if(task.getCreateTime()!=null){
					if(!task.getCreateTime().isEmpty()){
						workflowDetails.setAssignmentTime(format.parse(task.getCreateTime()));
					}
				}
			}	
			if(adjournmentMotion!=null){
				if(adjournmentMotion.getId()!=null){
					workflowDetails.setDeviceId(String.valueOf(adjournmentMotion.getId()));
				}
				if(adjournmentMotion.getNumber()!=null){
					workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(adjournmentMotion.getLocale()).format(adjournmentMotion.getNumber()));
					workflowDetails.setNumericalDevice(adjournmentMotion.getNumber().toString());
				}
				if(adjournmentMotion.getAdjourningDate()!=null){
					workflowDetails.setAdjourningDate(adjournmentMotion.getAdjourningDate());
				}
				if(adjournmentMotion.getPrimaryMember()!=null){
					workflowDetails.setDeviceOwner(adjournmentMotion.getPrimaryMember().getFullname());
				}
				if(adjournmentMotion.getType()!=null){
					workflowDetails.setDeviceType(adjournmentMotion.getType().getName());
				}
				if(adjournmentMotion.getHouseType()!=null){
					workflowDetails.setHouseType(adjournmentMotion.getHouseType().getName());
				}
				if(adjournmentMotion.getInternalStatus()!=null){
					workflowDetails.setInternalStatus(adjournmentMotion.getInternalStatus().getName());
				}
				workflowDetails.setLocale(adjournmentMotion.getLocale());
				if(adjournmentMotion.getRecommendationStatus()!=null){
					workflowDetails.setRecommendationStatus(adjournmentMotion.getRecommendationStatus().getName());
				}
				workflowDetails.setRemarks(adjournmentMotion.getRemarks());
				if(adjournmentMotion.getSession()!=null){
					if(adjournmentMotion.getSession().getType()!=null){
						workflowDetails.setSessionType(adjournmentMotion.getSession().getType().getSessionType());
					}
					workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(adjournmentMotion.getLocale()).format(adjournmentMotion.getSession().getYear()));
				}
				if(adjournmentMotion.getRevisedSubject() != null && !adjournmentMotion.getRevisedSubject().isEmpty()){
					workflowDetails.setSubject(adjournmentMotion.getRevisedSubject());
				}else{
					workflowDetails.setSubject(adjournmentMotion.getSubject());
				}
				
				if(adjournmentMotion.getRevisedNoticeContent() != null && !adjournmentMotion.getRevisedNoticeContent().isEmpty()){
					workflowDetails.setText(adjournmentMotion.getRevisedNoticeContent());
				}else{
					workflowDetails.setText(adjournmentMotion.getNoticeContent());
				}
				if(adjournmentMotion.getMinistry() != null){
					workflowDetails.setMinistry(adjournmentMotion.getMinistry().getName());
				}
				if(adjournmentMotion.getSubDepartment() != null){
					workflowDetails.setSubdepartment(adjournmentMotion.getSubDepartment().getName());
				}
				if(adjournmentMotion.getReply() != null && !adjournmentMotion.getReply().isEmpty()){
					workflowDetails.setReply(adjournmentMotion.getReply());
				}
			}
			workflowDetails.setProcessId(task.getProcessInstanceId());
			workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
			workflowDetails.setTaskId(task.getId());
			workflowDetails.setWorkflowType(workflowType);
			
			if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
				workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION);
				workflowDetails.setForm(workflowDetails.getUrlPattern());
				Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, adjournmentMotion.getLocale());
				if(requestStatus!=null){
				workflowDetails.setWorkflowSubType(requestStatus.getType());
				}
			} else {
				workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION);
				workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
				if(workflowType.equals(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW)
						|| workflowType.equals(ApplicationConstants.UNCLUBBING_WORKFLOW)
						|| workflowType.equals(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW)) {
					workflowDetails.setWorkflowSubType(adjournmentMotion.getRecommendationStatus().getType());
				} else {
					workflowDetails.setWorkflowSubType(adjournmentMotion.getInternalStatus().getType());
				}
				workflowDetails.persist();
			}
		} catch (ParseException e) {
			logger.error("Parse Exception",e);
			return new WorkflowDetails();
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_create_eventmotion", "WorkflowDetails cannot be created");
			throw elsException;
		}
		return workflowDetails;
	}
	
	public List<WorkflowDetails> create(final AdjournmentMotion adjournmentMotion,
										final List<Task> tasks,
										final String workflowType,
										final String assigneeLevel) 
										throws ELSException, ParseException {		
		List<WorkflowDetails> workflowDetailsList = new ArrayList<WorkflowDetails>();
		Status requestStatus = Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, adjournmentMotion.getLocale());
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DB_TIMESTAMP","");
		if(customParameter == null || customParameter.getValue()==null || customParameter.getValue().isEmpty()){
			throw new ELSException("WorkflowDetailsRepository_WorkflowDetail_create_adjournmentmotion_List<Task>", "Custom Parameter 'DB_TIMESTAMP' is not set.");	
		}
		SimpleDateFormat format = FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
		for(Task i:tasks){
			WorkflowDetails workflowDetails=new WorkflowDetails();
			String userGroupId=null;
			String userGroupType=null;
			String userGroupName=null;				
			String username=i.getAssignee();
			if(username==null || username.isEmpty()) {
				throw new ELSException("WorkflowDetailsRepository_WorkflowDetail_create_adjournmentmotion_List<Task>", "assignee is not set.");
			}
			Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
			UserGroup userGroup=UserGroup.findActive(credential, new Date(), adjournmentMotion.getLocale());
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
			if(adjournmentMotion!=null){
				if(adjournmentMotion.getId()!=null){
					workflowDetails.setDeviceId(String.valueOf(adjournmentMotion.getId()));
				}
				if(adjournmentMotion.getNumber()!=null){
					workflowDetails.setDeviceNumber(FormaterUtil.getNumberFormatterNoGrouping(adjournmentMotion.getLocale()).format(adjournmentMotion.getNumber()));
					workflowDetails.setNumericalDevice(adjournmentMotion.getNumber().toString());
				}
				if(adjournmentMotion.getAdjourningDate()!=null){
					workflowDetails.setAdjourningDate(adjournmentMotion.getAdjourningDate());
				}
				if(adjournmentMotion.getPrimaryMember()!=null){
					workflowDetails.setDeviceOwner(adjournmentMotion.getPrimaryMember().getFullname());
				}
				if(adjournmentMotion.getType()!=null){
					workflowDetails.setDeviceType(adjournmentMotion.getType().getName());
				}
				if(adjournmentMotion.getHouseType()!=null){
					workflowDetails.setHouseType(adjournmentMotion.getHouseType().getName());
				}
				if(adjournmentMotion.getInternalStatus()!=null){
					workflowDetails.setInternalStatus(adjournmentMotion.getInternalStatus().getName());
				}
				workflowDetails.setLocale(adjournmentMotion.getLocale());
				if(adjournmentMotion.getRecommendationStatus()!=null){
					workflowDetails.setRecommendationStatus(adjournmentMotion.getRecommendationStatus().getName());
				}
				workflowDetails.setRemarks(adjournmentMotion.getRemarks());
				if(adjournmentMotion.getSession()!=null){
					if(adjournmentMotion.getSession().getType()!=null){
						workflowDetails.setSessionType(adjournmentMotion.getSession().getType().getSessionType());
					}
					workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(adjournmentMotion.getLocale()).format(adjournmentMotion.getSession().getYear()));
				}
				if(adjournmentMotion.getRevisedSubject() != null && !adjournmentMotion.getRevisedSubject().isEmpty()){
					workflowDetails.setSubject(adjournmentMotion.getRevisedSubject());
				}else{
					workflowDetails.setSubject(adjournmentMotion.getSubject());
				}
				
				if(adjournmentMotion.getRevisedNoticeContent() != null && !adjournmentMotion.getRevisedNoticeContent().isEmpty()){
					workflowDetails.setText(adjournmentMotion.getRevisedNoticeContent());
				}else{
					workflowDetails.setText(adjournmentMotion.getNoticeContent());
				}
				if(adjournmentMotion.getMinistry() != null){
					workflowDetails.setMinistry(adjournmentMotion.getMinistry().getName());
				}
				if(adjournmentMotion.getSubDepartment() != null){
					workflowDetails.setSubdepartment(adjournmentMotion.getSubDepartment().getName());
				}
				if(adjournmentMotion.getReply() != null && !adjournmentMotion.getReply().isEmpty()){
					workflowDetails.setReply(adjournmentMotion.getReply());
				}
			}
			workflowDetails.setProcessId(i.getProcessInstanceId());
			workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
			workflowDetails.setTaskId(i.getId());
			workflowDetails.setWorkflowType(workflowType);
			if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
				workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION);
				workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
				workflowDetails.setWorkflowSubType(adjournmentMotion.getInternalStatus().getType());					
			} else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
				workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN_ADJOURNMENTMOTION);
				workflowDetails.setForm(workflowDetails.getUrlPattern());
				if(requestStatus!=null){
				workflowDetails.setWorkflowSubType(requestStatus.getType());
				}
			}
			workflowDetailsList.add((WorkflowDetails) workflowDetails.persist());			
		}

		return workflowDetailsList;
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final AdjournmentMotion adjournmentMotion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			Workflow workflow = adjournmentMotion.findWorkflowFromStatus();
			if(workflow!=null) {
				workflowDetails = findCurrentWorkflowDetail(adjournmentMotion, workflow.getType());
			}			
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_adjournmentMotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final AdjournmentMotion adjournmentMotion, final String workflowType) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", adjournmentMotion.getId().toString());
			query.setParameter("workflowType",workflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_adjournmentMotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	public WorkflowDetails startProcess(final AdjournmentMotion adjournmentMotion, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(adjournmentMotion, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(adjournmentMotion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(adjournmentMotion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], adjournmentMotion.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(adjournmentMotion, task, usergroupType, workflowType, "1");
		adjournmentMotion.setEndFlag("continue");
		adjournmentMotion.setTaskReceivedOn(new Date());
		adjournmentMotion.setWorkflowDetailsId(workflowDetails.getId());
		adjournmentMotion.setWorkflowStarted("YES");
		adjournmentMotion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		adjournmentMotion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		adjournmentMotion.setLevel(actorArr[2]);
		adjournmentMotion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		adjournmentMotion.simpleMerge();
		return workflowDetails;	
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final AdjournmentMotion adjournmentMotion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(adjournmentMotion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(adjournmentMotion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(adjournmentMotion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(adjournmentMotion,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], adjournmentMotion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(adjournmentMotion, task, usergroupType, workflowType, Integer.toString(level));
		adjournmentMotion.setEndFlag("continue");
		adjournmentMotion.setTaskReceivedOn(new Date());
		adjournmentMotion.setWorkflowDetailsId(workflowDetails.getId());
		adjournmentMotion.setWorkflowStarted("YES");
		adjournmentMotion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		adjournmentMotion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		adjournmentMotion.setLevel(actorArr[2]);
		adjournmentMotion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		adjournmentMotion.simpleMerge();
		return workflowDetails;		
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final BillAmendmentMotion billAmendmentMotion) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			Workflow workflow = billAmendmentMotion.findWorkflowFromStatus();
			if(workflow!=null) {
				workflowDetails = findCurrentWorkflowDetail(billAmendmentMotion, workflow.getType());
			}			
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_adjournmentMotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final BillAmendmentMotion billAmendmentMotion, final String workflowType) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" AND m.status='PENDING'" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", billAmendmentMotion.getId().toString());
			query.setParameter("workflowType",workflowType);
			List<WorkflowDetails> details = (List<WorkflowDetails>)query.getResultList();
			if(details != null && !details.isEmpty()){
				workflowDetails = details.get(0);
			}
		}catch (NoResultException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch(Exception e){	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_adjournmentMotion", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	public WorkflowDetails startProcess(final BillAmendmentMotion billAmendmentMotion, final String processDefinitionKey, final Workflow processWorkflow, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtFirstLevel(billAmendmentMotion, processWorkflow, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(billAmendmentMotion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(billAmendmentMotion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], billAmendmentMotion.getLocale());
		//WorkflowDetails workflowDetails = WorkflowDetails.create(question,task,workflowType,"1");
		WorkflowDetails workflowDetails = WorkflowDetails.create(billAmendmentMotion, task, usergroupType, workflowType, "1");
		billAmendmentMotion.setEndFlag("continue");
		billAmendmentMotion.setTaskReceivedOn(new Date());
		billAmendmentMotion.setWorkflowDetailsId(workflowDetails.getId());
		billAmendmentMotion.setWorkflowStarted("YES");
		billAmendmentMotion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		billAmendmentMotion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		billAmendmentMotion.setLevel(actorArr[2]);
		billAmendmentMotion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		billAmendmentMotion.simpleMerge();
		return workflowDetails;	
	}
	
	public WorkflowDetails startProcessAtGivenLevel(final BillAmendmentMotion billAmendmentMotion, final String processDefinitionKey, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		ProcessDefinition processDefinition = processService.findProcessDefinitionByKey(processDefinitionKey);
		Map<String,String> properties = new HashMap<String, String>();
		Reference actorAtGivenLevel = WorkflowConfig.findActorVOAtGivenLevel(billAmendmentMotion, processWorkflow, userGroupType, level, locale);
		if(actorAtGivenLevel==null) {
			throw new ELSException();
		}
		String userAtGivenLevel = actorAtGivenLevel.getId();
		String[] temp = userAtGivenLevel.split("#");
		properties.put("pv_user",temp[0]);
		properties.put("pv_endflag", "continue");	
		properties.put("pv_deviceId", String.valueOf(billAmendmentMotion.getId()));
		properties.put("pv_deviceTypeId", String.valueOf(billAmendmentMotion.getType().getId()));
		if(processDefinitionKey.equals(ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW)) {
			properties.put("pv_mailflag", null);
			properties.put("pv_timerflag", null);
		}		
		ProcessInstance processInstance= processService.createProcessInstance(processDefinition, properties);
		Task task= processService.getCurrentTask(processInstance);
		String workflowType = processWorkflow.getType();
//		WorkflowDetails workflowDetails = WorkflowDetails.create(adjournmentMotion,task,workflowType,Integer.toString(level));
		UserGroupType usergroupType = UserGroupType.findByType(temp[1], billAmendmentMotion.getLocale());
		WorkflowDetails workflowDetails = WorkflowDetails.create(billAmendmentMotion, task, usergroupType, workflowType, Integer.toString(level));
		billAmendmentMotion.setEndFlag("continue");
		billAmendmentMotion.setTaskReceivedOn(new Date());
		billAmendmentMotion.setWorkflowDetailsId(workflowDetails.getId());
		billAmendmentMotion.setWorkflowStarted("YES");
		billAmendmentMotion.setWorkflowStartedOn(new Date());
		
		String actor = actorAtGivenLevel.getId();
		billAmendmentMotion.setActor(actor);
		
		String[] actorArr = actor.split("#");
		billAmendmentMotion.setLevel(actorArr[2]);
		billAmendmentMotion.setLocalizedActorName(actorArr[3] + "(" + actorArr[4] + ")");
		
		billAmendmentMotion.simpleMerge();
		return workflowDetails;		
	}

	public Long findRevisedQuestionTextWorkflowCount(Question question,Status resendRevisedQuestionTextStatus, WorkflowDetails wfDetails) {
		Long workflowDetailCount = (long) 0;
		String strQuery = "SELECT count(id) FROM workflow_details "
				+ " WHERE device_id=:questionId"
				+ " AND recommendation_status=(SELECT name FROM status WHERE id=:statusId)"
				+ " AND id<=:workflowDetailsId";
		Query query = this.em().createNativeQuery(strQuery);
		query.setParameter("questionId", question.getId());
		query.setParameter("statusId", resendRevisedQuestionTextStatus.getId());
		query.setParameter("workflowDetailsId", wfDetails.getId());
		try{
		BigInteger count = (BigInteger) query.getSingleResult();
		workflowDetailCount = Long.parseLong(count.toString());
		return workflowDetailCount;
		}catch(Exception e){
			return workflowDetailCount;
		}
	}

	public List<WorkflowDetails> findAllSupplementaryWorkflow(String strHouseType, String strSessionType,
			String strSessionYear, String strQuestionType, String strStatus, String strWorkflowSubType, String assignee,
			String strItemsCount, String strLocale) {
		List<WorkflowDetails> workflowDetails = new ArrayList<WorkflowDetails>();
		String strQuery = "SELECT wd.* FROM workflow_details wd"
				+ " WHERE wd.house_type=:houseType"
				+ " AND wd.session_type=:sessionType"
				+ " AND wd.session_year=:sessionYear"
				+ " AND wd.device_type=:deviceType"
				+ " AND wd.workflow_sub_type=:workflowSubType"
				+ " AND wd.assignee=:assignee"
				+ " AND wd.locale=:locale"
				+ " AND wd.status=:status"
				+ " ORDER BY wd.assignment_time ASC LIMIT " + strItemsCount;
		Query query = this.em().createNativeQuery(strQuery, WorkflowDetails.class);
		query.setParameter("houseType", strHouseType);
		query.setParameter("sessionType", strSessionType);
		query.setParameter("sessionYear", strSessionYear);
		query.setParameter("deviceType", strQuestionType);
		query.setParameter("workflowSubType", strWorkflowSubType);
		query.setParameter("assignee", assignee);
		query.setParameter("locale", strLocale);
		query.setParameter("status", strStatus);
		workflowDetails = query.getResultList();
		return workflowDetails;		
	}
	
}