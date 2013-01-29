package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowConfigRepository extends BaseRepository<WorkflowConfig, Serializable>{

	public Boolean removeActor(final Long workflowconfigId, final Long workflowactorId) {
		try{
			String query1="DELETE from wfconfig_wfactors where wfconfig_id="+workflowconfigId+" and wfactors_id="+workflowactorId;
			this.em().createNativeQuery(query1).executeUpdate();
			String query2="DELETE from workflowactors WHERE id="+workflowactorId;
			this.em().createNativeQuery(query2).executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}	

	public List<Reference> findQuestionActorsVO(final Question question,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		if(status.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)){

		}else{			
			/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
			if(status.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
					){
				workflowConfig=getLatest(question,question.getInternalStatus().getType(),locale.toString());
				userGroupType=userGroup.getUserGroupType();
				currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
				allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
			}else{
				workflowConfig=getLatest(question,status,locale.toString());
				userGroupType=userGroup.getUserGroupType();
				currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
				allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
			HouseType houseType=question.getHouseType();
			DeviceType deviceType=question.getType();
			Ministry ministry=question.getMinistry();
			Department department=question.getDepartment();
			SubDepartment subDepartment=question.getSubDepartment();		
			for(WorkflowActor i:allEligibleActors){
				UserGroupType userGroupTypeTemp=i.getUserGroupType();
				System.out.println(userGroupTypeTemp.getType());
				List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
						userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
				for(UserGroup j:userGroups){
					int noOfComparisons=0;
					int noOfSuccess=0;
					Map<String,String> params=j.getParameters();
					if(houseType!=null){
						HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
						if(!params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
							if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(houseType.getName())){
								noOfComparisons++;
								noOfSuccess++;
							}else{
								noOfComparisons++;
							}
						}
					}
					if(deviceType!=null){
						if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(deviceType.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(ministry!=null){
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(ministry.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(department!=null){
						if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(subDepartment!=null){
						if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(subDepartment.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}	
					Date fromDate=j.getActiveFrom();
					Date toDate=j.getActiveTo();
					Date currentDate=new Date();
					noOfComparisons++;
					if(((fromDate==null||currentDate.after(fromDate)||currentDate.equals(fromDate))
							&&(toDate==null||currentDate.before(toDate)||currentDate.equals(toDate)))
					){
						noOfSuccess++;
					}
					/**** Include Leave Module ****/
					if(noOfComparisons==noOfSuccess){
						Reference reference=new Reference();
						reference.setId(j.getCredential().getUsername()+"#"+j.getUserGroupType().getType()+"#"+i.getLevel());
						reference.setName(userGroupTypeTemp.getName());
						references.add(reference);
						break;
					}				
				}
			}		

		}		
		return references;
	}

	public List<WorkflowActor> findQuestionActors(final Question question,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		List<WorkflowActor> actualActors=new ArrayList<WorkflowActor>();
		if(status.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)){

		}else{
			workflowConfig=getLatest(question,status,locale.toString());
			UserGroupType userGroupType=userGroup.getUserGroupType();
			WorkflowActor currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
			/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
			if(status.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)){
				allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
			}else{
				allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
			HouseType houseType=question.getHouseType();
			DeviceType deviceType=question.getType();
			Ministry ministry=question.getMinistry();
			Department department=question.getDepartment();
			SubDepartment subDepartment=question.getSubDepartment();		
			for(WorkflowActor i:allEligibleActors){
				int noOfComparisons=0;
				int noOfSuccess=0;
				UserGroupType userGroupTypeTemp=i.getUserGroupType();
				List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
						userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
				for(UserGroup j:userGroups){
					Map<String,String> params=j.getParameters();
					if(houseType!=null){
						HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
						if(!params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
							if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(houseType.getName())){
								noOfComparisons++;
								noOfSuccess++;
							}else{
								noOfComparisons++;
							}
						}
					}
					if(deviceType!=null){
						if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(deviceType.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(ministry!=null){
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(ministry.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(department!=null){
						if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(subDepartment!=null){
						if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(subDepartment.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}	
					Date fromDate=j.getActiveFrom();
					Date toDate=j.getActiveTo();
					Date currentDate=new Date();
					noOfComparisons++;
					if(((fromDate==null||currentDate.after(fromDate)||currentDate.equals(fromDate))
							&&(toDate==null||currentDate.before(toDate)||currentDate.equals(toDate)))
					){
						noOfSuccess++;
					}
					/**** Include Leave Module ****/
					if(noOfComparisons==noOfSuccess){
						actualActors.add(i);
						break;
					}				
				}
			}		

		}		
		return actualActors;
	}

	private WorkflowActor getWorkflowActor(final WorkflowConfig workflowConfig,
			final UserGroupType userGroupType,final int level) {
		String query="SELECT wfa FROM WorkflowConfig wc JOIN wc.workflowactors wfa  "+
		" JOIN wfa.userGroupType ugt WHERE wc.id="+workflowConfig.getId()+
		" AND ugt.id="+userGroupType.getId()+" AND wfa.level="+level;
		try{
			return (WorkflowActor) this.em().createQuery(query).getSingleResult();
		}catch (Exception e) {
			logger.error(e.getMessage());
			return new WorkflowActor();
		}

	}

	@SuppressWarnings("unchecked")
	private List<WorkflowActor> getWorkflowActorsExcludingCurrent(final WorkflowConfig workflowConfig,
			final WorkflowActor currentWorkflowActor,final String sortorder) {
		String query=null;
		if(sortorder.equals(ApplicationConstants.ASC)){
			query="SELECT wfa FROM WorkflowConfig wc JOIN wc.workflowactors wfa  "+
			" WHERE wc.id="+workflowConfig.getId()+
			" AND wfa.id >"+currentWorkflowActor.getId()+" ORDER BY wfa.id "+sortorder;	
		}else{
			query="SELECT wfa FROM WorkflowConfig wc JOIN wc.workflowactors wfa  "+
			" WHERE wc.id="+workflowConfig.getId()+
			" AND wfa.id <"+currentWorkflowActor.getId()+" ORDER BY wfa.id "+sortorder;	
		}		
		return this.em().createQuery(query).getResultList();		
	}	

	private WorkflowConfig getLatest(Question question, String internalStatus,String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+question.getType().getId()+
		" AND wf.type='"+workflowName+"' "+
		" AND ht.id="+question.getHouseType().getId()+
		" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		try{
			return (WorkflowConfig) this.em().createQuery(query).getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
}
