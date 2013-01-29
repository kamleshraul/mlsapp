package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowDetails;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDetailsRepository extends BaseRepository<WorkflowDetails, Serializable>{

	public WorkflowDetails create(final Question question,final Task task,
			final String workflowType,final String assigneeLevel) {
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
						workflowDetails.setRemarks(question.getRemarks());
						if(question.getSession()!=null){
							if(question.getSession().getType()!=null){
								workflowDetails.setSessionType(question.getSession().getType().getSessionType());
							}
							workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getSession().getYear()));
						}
						workflowDetails.setSubject(question.getSubject());
					}
					workflowDetails.setProcessId(task.getProcessInstanceId());
					workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
					workflowDetails.setTaskId(task.getId());
					workflowDetails.setWorkflowType(workflowType);
					if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
						workflowDetails.setWorkflowSubType(workflowDetails.getInternalStatus());					
						/**** Different types of workflow sub types ****/
					}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
						workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN);
						workflowDetails.setForm(workflowDetails.getUrlPattern());
						MessageResource messageResource=MessageResource.findByFieldName(MessageResource.class,"code","workflowtype.supportingmember",question.getLocale());
						if(messageResource!=null){
							workflowDetails.setWorkflowSubType(messageResource.getValue());
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

	public List<WorkflowDetails> create(final Question question,final List<Task> tasks,
			final String workflowType,final String assigneeLevel) {
		List<WorkflowDetails> workflowDetailsList=new ArrayList<WorkflowDetails>();
		try {
			MessageResource messageResource=MessageResource.findByFieldName(MessageResource.class,"code","workflowtype.supportingmember",question.getLocale());
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
								workflowDetails.setRemarks(question.getRemarks());
								if(question.getSession()!=null){
									if(question.getSession().getType()!=null){
										workflowDetails.setSessionType(question.getSession().getType().getSessionType());
									}
									workflowDetails.setSessionYear(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getSession().getYear()));
								}
								workflowDetails.setSubject(question.getSubject());
							}
							workflowDetails.setProcessId(i.getProcessInstanceId());
							workflowDetails.setStatus(ApplicationConstants.MYTASK_PENDING);
							workflowDetails.setTaskId(i.getId());
							workflowDetails.setWorkflowType(workflowType);
							if(workflowType.equals(ApplicationConstants.APPROVAL_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.APPROVAL_WORKFLOW_URLPATTERN);
								workflowDetails.setForm(workflowDetails.getUrlPattern()+"/"+userGroupType);
								workflowDetails.setWorkflowSubType(workflowDetails.getInternalStatus());					
								/**** Different types of workflow sub types ****/
							}else if(workflowType.equals(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW)){
								workflowDetails.setUrlPattern(ApplicationConstants.SUPPORTING_MEMBER_WORKFLOW_URLPATTERN);
								workflowDetails.setForm(workflowDetails.getUrlPattern());
								messageResource=MessageResource.findByFieldName(MessageResource.class,"code","workflowtype.supportingmember",question.getLocale());
								if(messageResource!=null){
									workflowDetails.setWorkflowSubType(messageResource.getValue());
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


	public WorkflowDetails findCurrentWorkflowDetail(final Question question) {
		try{
			String query="SELECT m FROM WorkflowDetails m WHERE m.deviceId="+question.getId()
			+" AND m.workflowType='"+ApplicationConstants.APPROVAL_WORKFLOW+"' "
			+" ORDER BY m.assignmentTime "+ApplicationConstants.DESC +" LIMIT 0,1";
			WorkflowDetails workflowDetails=(WorkflowDetails) this.em().createQuery(query).getSingleResult();
			return workflowDetails;
		}catch(Exception e){
			logger.error("Entity Not Found",e);
			return new WorkflowDetails();
		}		
	}
}
