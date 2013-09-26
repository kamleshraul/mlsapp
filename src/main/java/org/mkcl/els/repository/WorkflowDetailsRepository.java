package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowDetails;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDetailsRepository extends BaseRepository<WorkflowDetails, Serializable>{

	/************** Question Related Domain Methods *********************/
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
					UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
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
						if(question.getFile()!=null){
							workflowDetails.setFile(String.valueOf(question.getFile()));
						}
						workflowDetails.setRemarks(question.getRemarks());
						if(question.getSession()!=null){
							if(question.getSession().getType()!=null){
								workflowDetails.setSessionType(question.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getSession().getYear()));
						}
						workflowDetails.setSubject(question.getSubject());
						workflowDetails.setText(question.getQuestionText());
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					
					/**** To make the HDS to take the workflow with mailer task ****/
					String currentDeviceTypeWorkflowType = null;
					if(question.getType()  != null){
						if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE) 
								&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
							currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
						}else{
							currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
						}
					}
					
					if(workflowType.equals(currentDeviceTypeWorkflowType)){
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						workflowDetails.setWorkflowSubType(question.getInternalStatus().getType());					
						/**** Different types of workflow sub types ****/
					}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						Status requestStatus=Status.findByType(ApplicationConstants.REQUEST_TO_SUPPORTING_MEMBER, question.getLocale());
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
				for(Task i:tasks){
					WorkflowDetails workflowDetails=new WorkflowDetails();
					String userGroupId=null;
					String userGroupType=null;
					String userGroupName=null;				
					String username=i.getAssignee();
					if(username!=null){
						if(!username.isEmpty()){
							Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
							UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, question.getLocale());
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
								workflowDetails.setSubject(question.getSubject());
								workflowDetails.setText(question.getQuestionText());
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							/**** To make the HDS to take the workflow with mailer task ****/
							String currentDeviceTypeWorkflowType = null;
							if(question.getType()  != null){
								if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
										&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
									currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
								}else{
									currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
								}
							}
							
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


	@SuppressWarnings("unchecked")
	public WorkflowDetails findCurrentWorkflowDetail(final Question question) throws ELSException{
		WorkflowDetails workflowDetails = null;
		try{
			/**** To make the HDS to take the workflow with mailer task ****/
			String currentDeviceTypeWorkflowType = null;
			if(question.getType()  != null){
				if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
						&& question.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					currentDeviceTypeWorkflowType = ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW;
				}else{
					currentDeviceTypeWorkflowType = ApplicationConstants.APPROVAL_WORKFLOW;
				}
			}
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" ORDER BY m.assignmentTime " + ApplicationConstants.DESC;
			Query query=this.em().createQuery(strQuery);
			query.setParameter("deviceId", question.getId().toString());
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
			elsException.setParameter("WorkflowDetailsRepository_WorkflowDetail_findCurrentWorkflowDetail_question", "WorkflowDetails Not Found");
			throw elsException;
		}		
		
		return workflowDetails;
	}
	
	public WorkflowDetails findCurrentWorkflowDetail(final Resolution resolution) throws ELSException {
		try{
			String strQuery="SELECT m FROM WorkflowDetails m" +
					" WHERE m.deviceId=:deviceId"+
					" AND m.workflowType=:workflowType" +
					" ORDER BY m.assignmentTime "+ApplicationConstants.DESC +" LIMIT 0,1";
			Query  query=this.em().createQuery(strQuery);
			query.setParameter("workflowType",ApplicationConstants.RESOLUTION_APPROVAL_WORKFLOW);
			query.setParameter("deviceId", resolution.getId());
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
			query.setParameter("deviceId", resolution.getId());
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
					Credential credential=Credential.findByFieldName(Credential.class,"username",username,"");
					UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, resolution.getLocale());
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
						workflowDetails.setSubject(resolution.getSubject());
						workflowDetails.setText(resolution.getNoticeContent());
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
	/************** Motion Related Domain Methods 
	 * @throws ELSException *********************/
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
							UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, domain.getLocale());
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
								workflowDetails.setSubject(domain.getSubject());
								workflowDetails.setText(domain.getDetails());
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
					UserGroup userGroup=UserGroup.findByFieldName(UserGroup.class,"credential",credential, domain.getLocale());
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
						workflowDetails.setSubject(domain.getSubject());
						workflowDetails.setText(domain.getDetails());
						if(domain.getFile()!=null){
							workflowDetails.setFile(String.valueOf(domain.getFile()));
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
}
