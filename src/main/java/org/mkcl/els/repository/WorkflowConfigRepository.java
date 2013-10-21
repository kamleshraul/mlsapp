package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowConfigRepository extends BaseRepository<WorkflowConfig, Serializable>{

	public Boolean removeActor(final Long workflowconfigId, final Long workflowactorId) {
		try{
			Query query1=Query.findByFieldName(Query.class, "keyField", ApplicationConstants.WORKFLOWCONFIG_REMOVEACTOR_WFCONFIG_WFACTORS_QUERY,"");
			String strquery1=query1.getQuery();
			javax.persistence.Query q=this.em().createNativeQuery(strquery1);
			q.setParameter("workflowconfigId", workflowconfigId);
			q.setParameter("workflowactorId", workflowactorId);
			q.executeUpdate();
			Query query2=Query.findByFieldName(Query.class, "keyField", ApplicationConstants.WORKFLOWCONFIG_REMOVEACTOR_WORKFLOWACTORS_QUERY,"");
			String strquery2=query2.getQuery();
			javax.persistence.Query q1=this.em().createNativeQuery(strquery2);
			q1.setParameter("workflowactorId", workflowactorId);
			q1.executeUpdate();
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
				List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
						userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
				for(UserGroup j:userGroups){
					int noOfComparisons=0;
					int noOfSuccess=0;
					Map<String,String> params=j.getParameters();
					if(houseType!=null){
						HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
							if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(houseType.getName())){
								noOfComparisons++;
								noOfSuccess++;
							}else{
								noOfComparisons++;
							}
						}
					}
					if(deviceType!=null){
						if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(deviceType.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(ministry!=null){
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(ministry.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(department!=null){
						if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(subDepartment!=null){
						if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(subDepartment.getName())){
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
						User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
						reference.setId(j.getCredential().getUsername()
								+"#"+j.getUserGroupType().getType()
								+"#"+i.getLevel()
								+"#"+userGroupTypeTemp.getName()
								+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
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
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
							if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(houseType.getName())){
								noOfComparisons++;
								noOfSuccess++;
							}else{
								noOfComparisons++;
							}
						}
					}
					if(deviceType!=null){
						if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(deviceType.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(ministry!=null){
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(ministry.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(department!=null){
						if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(subDepartment!=null){
						if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(subDepartment.getName())){
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

	//	private WorkflowActor getWorkflowActor(final WorkflowConfig workflowConfig,
	//			final UserGroupType userGroupType,final int level) {
	//		String query="SELECT wfa FROM WorkflowConfig wc JOIN wc.workflowactors wfa  "+
	//		" JOIN wfa.userGroupType ugt WHERE wc.id="+workflowConfig.getId()+
	//		" AND ugt.id="+userGroupType.getId()+" AND wfa.level="+level;
	//		try{
	//			return (WorkflowActor) this.em().createQuery(query).getSingleResult();
	//		}catch (Exception e) {
	//			logger.error(e.getMessage());
	//			return new WorkflowActor();
	//		}
	//
	//	}

	@SuppressWarnings("unchecked")
	private WorkflowActor getWorkflowActor(final WorkflowConfig workflowConfig,
			final UserGroupType userGroupType,final int level) {
		/**** Get the closest level of particular user group ****/
		String userGrouplevelQuery="SELECT wfa FROM WorkflowConfig wc" +
				" JOIN wc.workflowactors wfa  "+
				" JOIN wfa.userGroupType ugt" +
				" WHERE wc.id=:workflowConfigId" +
				" AND ugt.id=:userGroupTypeId";
		javax.persistence.Query query=this.em().createQuery(userGrouplevelQuery);
		query.setParameter("workflowConfigId", workflowConfig.getId());
		query.setParameter("userGroupTypeId", userGroupType.getId());
		List<WorkflowActor> workflowActors=query.getResultList();
		/**** We need to find the closest level to level parameter ****/
		Map<String,WorkflowActor> actorsMap=new HashMap<String, WorkflowActor>();
		Set<Integer> sortedSet=new TreeSet<Integer>();
		for(WorkflowActor i:workflowActors){
			int currentLevel=i.getLevel();
			if(currentLevel-level==0){
				return i;
			}else {
				int absDifference=Math.abs(currentLevel-level);
				actorsMap.put(String.valueOf(absDifference), i);
				sortedSet.add(absDifference);				
			}
		}
		actorsMap.get(sortedSet.iterator().next());
		Iterator<Integer> iterator=sortedSet.iterator();
		int lowestAbs=0;
		while(iterator.hasNext()){
			lowestAbs=iterator.next();
			break;
		}
		return actorsMap.get(String.valueOf(lowestAbs));
	}

	@SuppressWarnings("unchecked")
	private List<WorkflowActor> getWorkflowActorsExcludingCurrent(final WorkflowConfig workflowConfig,
			final WorkflowActor currentWorkflowActor,final String sortorder) {
		String strQuery=null;
		if(sortorder.equals(ApplicationConstants.ASC)){
			strQuery="SELECT wfa FROM WorkflowConfig wc" +
					" JOIN wc.workflowactors wfa  "+
					" WHERE wc.id=:workflowConfigId" +
					" AND wfa.id >:currentWorkflowActorId" +
					" ORDER BY wfa.id "+sortorder;	
		}else{
			strQuery="SELECT wfa FROM WorkflowConfig wc" +
					" JOIN wc.workflowactors wfa  "+
					" WHERE wc.id=:workflowConfigId" +
					" AND wfa.id <:currentWorkflowActorId ORDER BY wfa.id "+sortorder;	
		}	
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("workflowConfigId", workflowConfig.getId());
		query.setParameter("currentWorkflowActorId", currentWorkflowActor.getId());
		return query.getResultList();	
	}	

	private WorkflowConfig getLatest(final Question question,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", question.getType().getId());
		query.setParameter("workflowName",workflowName);
		query.setParameter("houseTypeId", question.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	

	//maybe removed as generic method is added below this method
	private WorkflowConfig getLatest(final Resolution resolution,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", resolution.getType().getId());
		query.setParameter("workflowName",workflowName);
		query.setParameter("houseTypeId", resolution.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}

	//added for government resolution when housetype depends on user's housetype
	private WorkflowConfig getLatest(final Resolution resolution,final HouseType houseType,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", resolution.getType().getId());
		query.setParameter("workflowName",workflowName);
		query.setParameter("houseTypeId", houseType.getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}


	public List<Reference> findResolutionActorsVO(final Resolution resolution,
			final Status internalStatus,final UserGroup userGroup,final int level,final String workflowHouseType,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		HouseType houseType = null;
		if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
			houseType = HouseType.findByFieldName(HouseType.class, "name", workflowHouseType, locale);
		} else {
			houseType=resolution.getHouseType();
		}
		/**** Note :Here this can be configured so that list of workflows which
		 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.RESOLUTION_RECOMMEND_SENDBACK)
				||status.equals(ApplicationConstants.RESOLUTION_RECOMMEND_DISCUSS)
		){

			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				workflowConfig=getLatest(resolution,houseType,resolution.getInternalStatusLowerHouse().getType(),locale.toString());
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				workflowConfig=getLatest(resolution,houseType,resolution.getInternalStatusUpperHouse().getType(),locale.toString());
			}

			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		}else{
			workflowConfig=getLatest(resolution,houseType,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}

		DeviceType deviceType=resolution.getType();
		Ministry ministry=resolution.getMinistry();
		Department department=resolution.getDepartment();
		SubDepartment subDepartment=resolution.getSubDepartment();		
		for(WorkflowActor i:allEligibleActors){
			UserGroupType userGroupTypeTemp=i.getUserGroupType();
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				/**The Parameters doesnot need to check in case of MEMBER***/
				if(!j.getUserGroupType().getType().equals(ApplicationConstants.MEMBER)){
					Map<String,String> params=j.getParameters();
					if(houseType!=null){
						HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
							if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(houseType.getName())){
								noOfComparisons++;
								noOfSuccess++;
							}else{
								noOfComparisons++;
							}
						}
					}
					if(deviceType!=null){
						if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(deviceType.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(ministry!=null){
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(ministry.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(department!=null){
						if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
					if(subDepartment!=null){
						if( params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null){
							if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(subDepartment.getName())){
								noOfComparisons++;
								noOfSuccess++;
							}else{
								noOfComparisons++;
							}
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
				}else{
					Credential credential= j.getCredential();
					if(credential!=null){
						if(resolution.getCreatedBy().equals(credential.getUsername())){
							noOfSuccess++;
							noOfComparisons++;
						}
						else{
							noOfComparisons++;
						}
					}

				}
				if(noOfComparisons==noOfSuccess){
					Reference reference=new Reference();
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+i.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}				
			}
		}		
		return references;
	}

	public List<Reference> findMotionActorsVO(final Motion motion,
			final Status internalStatus,final UserGroup userGroup,
			final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
		 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
				||status.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)){
			workflowConfig=getLatest(motion,motion.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		}else{
			workflowConfig=getLatest(motion,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getType();
		Ministry ministry=motion.getMinistry();
		SubDepartment subDepartment=motion.getSubDepartment();		
		for(WorkflowActor i:allEligibleActors){
			UserGroupType userGroupTypeTemp=i.getUserGroupType();
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(houseType!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(houseType.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(deviceType!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(deviceType.getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
				if(ministry!=null){
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(ministry.getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}				
				if(subDepartment!=null){
					//System.out.println(j.getUserGroupType().getType()+":"+params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale));
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(subDepartment.getName())){
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+i.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}				
			}
		}	
			
	return references;
}
	
	private WorkflowConfig getLatest(final Motion motion,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflow"+
				" AND ht.id=:houseTypeId" +
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getType().getId());
		query.setParameter("workflow",workflowName);
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	public List<WorkflowActor> findCommitteeActors(final HouseType houseType,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int level,
			final String locale) {
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = 
			this.getWorkflowActor(wfConfig, userGroupType, level);
		
		if(status.getType().equals(
				ApplicationConstants.COMMITTEE_RECOMMEND_SENDBACK)
				|| status.getType().equals(
						ApplicationConstants.COMMITTEE_PROCESSED_SENDBACK)) {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, 
					currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, 
					currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActors;
	}
	
	public WorkflowActor findNextCommitteeActor(final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = 
			this.getWorkflowActor(wfConfig, userGroupType, level);
		
		if(status.getType().equals(
				ApplicationConstants.COMMITTEE_RECOMMEND_SENDBACK)) {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}	
	
	private WorkflowConfig getLatest(final HouseType houseType,
			final String workflowName,
			final String locale) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT wc" +
				" FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.houseType ht" +
				" WHERE ht.id = " + houseType.getId() +
				" AND wf.type = '" + workflowName + "'" +
				" AND wc.isLocked = true" +
				" AND wc.locale = '" + locale + "'" +
				" ORDER BY wc.id " + ApplicationConstants.DESC);
		
		TypedQuery<WorkflowConfig> tQuery = 
			this.em().createQuery(query.toString(), 
					WorkflowConfig.class).setMaxResults(1);
		
		List<WorkflowConfig> workflowConfigs = tQuery.getResultList();
		if(! workflowConfigs.isEmpty()) {
			return workflowConfigs.get(0);
		}
		
		return null;
	}
	
	private WorkflowActor getNextWorkflowActor(
			final WorkflowConfig workflowConfig,
			final WorkflowActor currentWorkflowActor, 
			final String sortOrder) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT wfa" +
				" FROM WorkflowConfig wc JOIN wc.workflowactors wfa" +
				" WHERE wc.id = " + workflowConfig.getId() +
				" AND wfa.id > " + currentWorkflowActor.getId() +
				" ORDER BY wfa.id");
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(" " + ApplicationConstants.ASC);
		}
		else {
			query.append(" " + ApplicationConstants.DESC);
		}
		
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query.toString(), 
					WorkflowActor.class).setMaxResults(1);
		
		List<WorkflowActor> wfActors = tQuery.getResultList();
		if(! wfActors.isEmpty()) {
			return wfActors.get(0);
		}
			
		return null;
	}
	
	/**
	 * 
	 * @param bill
	 * @param internalStatus (though this is normally internal status, but for some cases this can be recommendation status, 
	 * translation status or even opinionFromLawAndJD status)
	 * @param userGroup
	 * @param level
	 * @param locale
	 * @return
	 */
	public List<Reference> findBillActorsVO(final Bill bill,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.BILL_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.BILL_RECOMMEND_DISCUSS)								
		){
			workflowConfig=getLatest(bill,bill.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else{
			workflowConfig=getLatest(bill,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}
		HouseType houseType=Bill.findHouseTypeForWorkflow(bill);
		DeviceType deviceType=bill.getType();
		Ministry ministry=bill.getMinistry();
		SubDepartment subDepartment=bill.getSubDepartment();		
		for(WorkflowActor i:allEligibleActors){
			UserGroupType userGroupTypeTemp=i.getUserGroupType();
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+i.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}				
			}
		}				
		return references;
	}
	
	public List<Reference> findBillActorsVO(final Bill bill, HouseType houseType, final Boolean isActorAcrossHouse,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.BILL_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.BILL_RECOMMEND_DISCUSS)								
		){
			workflowConfig=getLatest(bill,houseType,bill.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else{
			workflowConfig=getLatest(bill,houseType,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}
		DeviceType deviceType=bill.getType();
		Ministry ministry=bill.getMinistry();
		SubDepartment subDepartment=bill.getSubDepartment();	
		if(isActorAcrossHouse!=null) {
			if(isActorAcrossHouse.equals(true)) {
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
					houseType = HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.UPPER_HOUSE,bill.getLocale());
				} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
					houseType = HouseType.findByFieldName(HouseType.class, "type", ApplicationConstants.LOWER_HOUSE,bill.getLocale());
				}
			}
		}
		for(WorkflowActor i:allEligibleActors){
			UserGroupType userGroupTypeTemp=i.getUserGroupType();
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+i.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}				
			}
		}				
		return references;
	}
	
	private WorkflowConfig getLatest(final Bill bill,final String internalStatus,final String locale) {
		HouseType houseTypeForWorkflow = Bill.findHouseTypeForWorkflow(bill);
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";				
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+bill.getType().getId()+
		" AND wf.type='"+workflowName+"' "+
		" AND ht.id="+houseTypeForWorkflow.getId()+
		" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		try{
			return (WorkflowConfig) this.em().createQuery(query).getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	private WorkflowConfig getLatest(final Bill bill,final HouseType houseType,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";				
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+bill.getType().getId()+
		" AND wf.type='"+workflowName+"' "+
		" AND ht.id="+houseType.getId()+
		" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		try{
			return (WorkflowConfig) this.em().createQuery(query).getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
}
