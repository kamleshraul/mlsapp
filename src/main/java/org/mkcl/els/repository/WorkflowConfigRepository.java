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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.BillAmendmentMotion;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDraft;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.Workflow;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.ballot.Ballot;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowConfigRepository extends BaseRepository<WorkflowConfig, Serializable>{
	
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
	
	private WorkflowConfig getLatest(final HouseType houseType,
			final String workflowName,
			final String module,
			final String locale) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT wc" +
				" FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" LEFT JOIN wc.houseType ht" +
				" WHERE wf.type = '" + workflowName + "'" +
				" AND wc.module = '" + module + "'" +
				" AND wc.isLocked = true" +
				" AND wc.locale = '" + locale + "'");
		
		if(!houseType.getType().equals(ApplicationConstants.BOTH_HOUSE)){
			query.append(" AND  ht.id = " + houseType.getId());
		}
		
		query.append(" ORDER BY wc.id " + ApplicationConstants.DESC);
		
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
		if(!actorsMap.isEmpty()){
			actorsMap.get(sortedSet.iterator().next());
			Iterator<Integer> iterator=sortedSet.iterator();
			int lowestAbs=0;
			while(iterator.hasNext()){
				lowestAbs=iterator.next();
				break;
			}
			return actorsMap.get(String.valueOf(lowestAbs));
		}else{
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private List<WorkflowActor> getWorkflowActors(final WorkflowConfig workflowConfig,
			final List<Long> usergroupTypeIds,final int level) {
		/**** Get the closest level of particular user group ****/
		String userGrouplevelQuery="SELECT wfa FROM WorkflowConfig wc" +
				" JOIN wc.workflowactors wfa  "+
				" JOIN wfa.userGroupType ugt" +
				" WHERE wc.id=:workflowConfigId" +
				" AND ugt.id IN(:userGroupTypeIds)";
		javax.persistence.Query query=this.em().createQuery(userGrouplevelQuery);
		query.setParameter("workflowConfigId", workflowConfig.getId());
		query.setParameter("userGroupTypeIds", usergroupTypeIds);
		List<WorkflowActor> workflowActors=query.getResultList();
		return workflowActors;
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
	
	
	@SuppressWarnings("unchecked")
	private List<WorkflowActor> getWorkflowActorsExcludingGivenActorList(final WorkflowConfig workflowConfig,
			final List<WorkflowActor> workflowActorsToBeExcluded,
			final WorkflowActor currentWorkflowActor,
			final String sortorder) {
		String strQuery=null;
		List<Long> wfactorIds = new ArrayList<Long>();
		for(WorkflowActor wfa : workflowActorsToBeExcluded){
			wfactorIds.add(wfa.getId());
		}
		if(sortorder.equals(ApplicationConstants.ASC)){
			strQuery="SELECT wfa FROM WorkflowConfig wc" +
					" JOIN wc.workflowactors wfa  "+
					" WHERE wc.id=:workflowConfigId" +
					" AND wfa.id >:currentWorkflowActorId" ;
			if(workflowActorsToBeExcluded.size()>0){
				strQuery = strQuery + " AND wfa.id NOT IN (:wfactorIds)" ;
			}
			strQuery= strQuery	+ " ORDER BY wfa.id "+sortorder;	
		}else{
			strQuery="SELECT wfa FROM WorkflowConfig wc" +
					" JOIN wc.workflowactors wfa  "+
					" WHERE wc.id=:workflowConfigId";
			if(workflowActorsToBeExcluded.size()>0){
				strQuery = strQuery + " AND wfa.id NOT IN (:wfactorIds)" ;
			}
			strQuery= strQuery	+ " AND wfa.id <:currentWorkflowActorId " +
				    " ORDER BY wfa.id "+sortorder;	
		}	
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("workflowConfigId", workflowConfig.getId());
		query.setParameter("currentWorkflowActorId", currentWorkflowActor.getId());
		if(workflowActorsToBeExcluded.size()>0){
			query.setParameter("wfactorIds",wfactorIds);
		}
		
		return query.getResultList();	
	}
	/********************************Question****************************/
	public Reference findActorVOAtGivenLevel(final Question question, final Status status, 
			final String usergroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(question, status.getType(), locale.toString());
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(question, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;
	}
	
	public Reference findActorVOAtGivenLevel(final Question question, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(question, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(question, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}

	public Reference findActorVOAtFirstLevel(final Question question, final Workflow processWorkflow, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(question, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(question, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}

	private WorkflowActor findFirstActor(final Question question, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(question, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}

	private WorkflowConfig getLatest(final Question question, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
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
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", question.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}

	private Reference findActorDetails(final Question question,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = question.getHouseType();
		DeviceType deviceType = question.getType();
		Ministry ministry = null;
		SubDepartment subDepartment = null;
		if(question.getBallotStatus() != null){
			Ballot ballot = Ballot.find(question);
			/** Here if post ballot change is done then the actors should be populated based on 
			 * the existing group and not the changed group.
			 * for this Following activities are performed
			 * 1. find the  Original Department and subdepartment from questionDraft.
			 * 2. Check whether the group change is done pre ballot or post ballot
			 * 3. If done post ballot then find the actor based on original ministry and subdepartment.
			 * 4. If the usergroup type is department the find it based on changed ministry and subdepartment.
			 * **/
			QuestionDraft questionDraft = Question.findLatestGroupChangedDraft(question);
			QuestionDraft latestGroupChangedDraft = Question.findGroupChangedDraft(question);
			if(ballot != null && questionDraft != null 
				&& ballot.getBallotDate().before(latestGroupChangedDraft.getEditedOn())){
				ministry = questionDraft.getMinistry();
				subDepartment = questionDraft.getSubDepartment();
			}else{
				ministry = question.getMinistry();
				subDepartment = question.getSubDepartment(); 
			}
		}else{
			ministry = question.getMinistry();
			subDepartment = question.getSubDepartment();
		}
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(question.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT)){
				ministry = question.getMinistry();
				subDepartment = question.getSubDepartment();
			}
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}

	public List<Reference> findQuestionActorsVO(final Question question,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) throws ELSException {
		String status = internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		if(status.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)
			||status.equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_GROUPCHANGED)
			||status.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_GROUPCHANGED)
			||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_GROUPCHANGED)
			||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_GROUPCHANGED)
			||status.equals(ApplicationConstants.QUESTION_SYSTEM_TO_BE_PUTUP)){

		}else{
			/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
			if(status.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
				||status.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
				||status.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_SENDBACK)
				||status.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_DISCUSS)
				||status.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_SENDBACK)
				||status.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_DISCUSS)
				||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_SENDBACK)
				||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_DISCUSS)
				
			){
				workflowConfig = getLatest(question,question.getInternalStatus().getType(),locale.toString());
				userGroupType = userGroup.getUserGroupType();
				currentWorkflowActor = getWorkflowActor(workflowConfig,userGroupType,level);
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
			}else if((status.equals(ApplicationConstants.QUESTION_PROCESSED_SENDTODESKOFFICER) 
						|| status.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_SENDTODESKOFFICER)
						|| status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_SENDTODESKOFFICER))
					&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
				workflowConfig = getLatest(question,question.getInternalStatus().getType(),locale.toString());
				UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
				currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}else if(status.equals(ApplicationConstants.QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTOSECTIONOFFICER)
					||status.equals(ApplicationConstants.QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTODEPARTMENT)
					||status.equals(ApplicationConstants.QUESTION_PROCESSED_SENDSUPPLEMENTARYQUESTIONTODESKOFFICER)){
				
				Workflow processWorkflow = Workflow.findByType(ApplicationConstants.QUESTION_SUPPLEMENTARY_WORKFLOW, locale.toString());
				workflowConfig = getLatest(question, processWorkflow, locale);
				userGroupType = userGroup.getUserGroupType();
				currentWorkflowActor = getWorkflowActor(workflowConfig,userGroupType,level);
				CustomParameter userGroupTypeToBeExcluded =  CustomParameter.
					findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
				if(userGroupTypeToBeExcluded != null && 
						(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
					String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
					String[] arrUsergroupTypes = strUsergroupTypes.split(",");
					List<Long> usergroupTypeIds = new ArrayList<Long>();
					for(String s : arrUsergroupTypes){
						UserGroupType ugt = UserGroupType.findByType(s, locale);
						if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
							if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
								usergroupTypeIds.add(ugt.getId());
							}
						}else{
							usergroupTypeIds.add(ugt.getId());
						}						
					}
					List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
					allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
				}else{
					allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
				}
			}else if(status.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_SENDANSWERFORCONFIRMATION)
					||status.equals(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_RECOMMENDANSWERFORCONFIRMATION)){
				
				Workflow processWorkflow = Workflow.findByType(ApplicationConstants.ANSWER_CONFIRMATION_WORKFLOW, locale.toString());
				workflowConfig = getLatest(question, processWorkflow, locale);
				userGroupType = userGroup.getUserGroupType();
				currentWorkflowActor = getWorkflowActor(workflowConfig,userGroupType,level);
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}else{
				workflowConfig = getLatest(question,status,locale.toString());
				userGroupType = userGroup.getUserGroupType();
				currentWorkflowActor = getWorkflowActor(workflowConfig,userGroupType,(level-1));
				CustomParameter userGroupTypeToBeExcluded = null;
				if(status.toUpperCase().contains("FINAL")){
					userGroupTypeToBeExcluded = CustomParameter.
					findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
				}else{
					userGroupTypeToBeExcluded = CustomParameter.
					findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
				}
				if(userGroupTypeToBeExcluded != null && 
						(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
					String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
					String[] arrUsergroupTypes = strUsergroupTypes.split(",");
					List<Long> usergroupTypeIds = new ArrayList<Long>();
					for(String s : arrUsergroupTypes){
						UserGroupType ugt = UserGroupType.findByType(s, locale);
						if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
							if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
								usergroupTypeIds.add(ugt.getId());
							}
						}else{
							usergroupTypeIds.add(ugt.getId());
						}						
					}
					List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
					allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
				}else{
					allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
				}
			}
			HouseType houseType = question.getHouseType();
			DeviceType deviceType = question.getType();
			/** Here if post ballot change is done then the actors should be populated based on 
			 * the existing group and not the changed group.
			 * for this Following activities are performed
			 * 1. find the  Original Department and subdepartment from questionDraft.
			 * 2. Check whether the group change is done pre ballot or post ballot
			 * 3. If done post ballot then find the actor based on original ministry and subdepartment.
			 * 4. If the usergroup type is department the find it based on changed ministry and subdepartment.
			 * **/
			Ministry ministry = null;
			SubDepartment subDepartment = null;
			if(question.getBallotStatus() != null){
				Ballot ballot = Ballot.findByDeviceId(question.getId());
				QuestionDraft questionDraft = Question.findLatestGroupChangedDraft(question);
				QuestionDraft latestGroupChangedDraft = Question.findGroupChangedDraft(question);
				if(ballot != null && questionDraft != null 
						&& ballot.getBallotDate().before(latestGroupChangedDraft.getEditedOn())){
					ministry = questionDraft.getMinistry();
					subDepartment = questionDraft.getSubDepartment();
					
				}else{
					 ministry = question.getMinistry();
					 subDepartment = question.getSubDepartment();
				}
			}else{
				 ministry = question.getMinistry();
				 subDepartment = question.getSubDepartment();
			}
			
			for(WorkflowActor i : allEligibleActors){
				UserGroupType userGroupTypeTemp = i.getUserGroupType();
				if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
					try {
						User user = User.find(question.getPrimaryMember());
						Reference reference = new Reference();
						reference.setId(user.getCredential().getUsername()
								+ "#" + userGroupTypeTemp.getType()
								+ "#" + i.getLevel()
								+ "#" + userGroupTypeTemp.getName()
								+ "#" + user.getTitle() + " " 
								+ user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
						reference.setName(userGroupTypeTemp.getName());
						references.add(reference);
						break;
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT) 
							|| (userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
						ministry = question.getMinistry();
						subDepartment = question.getSubDepartment();
					}
					List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
							userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
					for(UserGroup j : userGroups){
						int noOfComparisons = 0;
						int noOfSuccess = 0;
						Map<String,String> params = j.getParameters();
						if(houseType != null){
							HouseType bothHouse = HouseType.
									findByFieldName(HouseType.class, "type","bothhouse", locale);
							if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null 
									&& !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
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
							if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
								String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
								for(int k=0; k<allowedMinistries.length; k++) {
									if(allowedMinistries[k].equals(ministry.getName())) {										
										noOfSuccess++;
										break;
									}
								}
								noOfComparisons++;
							}else{
								noOfComparisons++;
							}
						}
						if(subDepartment!=null){
							if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
								String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
								for(int k=0; k<allowedSubdepartments.length; k++) {
									if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
										noOfSuccess++;
										break;
									}
								}
								noOfComparisons++;
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
									+"#"+user.getTitle()+" "
									+user.getFirstName()+" "
									+user.getMiddleName()+" "
									+user.getLastName());
							reference.setName(userGroupTypeTemp.getName());
							reference.setState(params.get(ApplicationConstants.ACTORSTATE_KEY+"_"+locale));
							reference.setRemark(params.get(ApplicationConstants.ACTORREMARK_KEY+"_"+locale));
							if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
								if(!reference.getId().equals(question.getActor())){
									references.add(reference);
								}
							}else{
								references.add(reference);
								break;
							}
						}				
					}
				}
			}		
		}		
		return references;
	}

	public List<WorkflowActor> findQuestionActors(final Question question,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) throws ELSException {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		List<WorkflowActor> actualActors=new ArrayList<WorkflowActor>();
		if(status.equals(ApplicationConstants.QUESTION_SYSTEM_GROUPCHANGED)
				||status.equals(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_GROUPCHANGED)
				||status.equals(ApplicationConstants.QUESTION_SHORTNOTICE_SYSTEM_GROUPCHANGED)
				||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_SYSTEM_GROUPCHANGED)){

		}else{
			workflowConfig=getLatest(question,status,locale.toString());
			UserGroupType userGroupType=userGroup.getUserGroupType();
			WorkflowActor currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
			/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
			if(status.equals(ApplicationConstants.QUESTION_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.QUESTION_RECOMMEND_DISCUSS)
					||status.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.QUESTION_UNSTARRED_RECOMMEND_DISCUSS)
					||status.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.QUESTION_SHORTNOTICE_RECOMMEND_DISCUSS)
					||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_RECOMMEND_DISCUSS)){
				allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
			}else{
				allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
			HouseType houseType=question.getHouseType();
			DeviceType deviceType=question.getType();
			/** Here if post ballot change is done then the actors should be populated based on 
			 * the existing group and not the changed group.
			 * for this Following activities are performed
			 * 1. find the  Original Department and subdepartment from questionDraft.
			 * 2. Check whether the group change is done pre ballot or post ballot
			 * 3. If done post ballot then find the actor based on original ministry and subdepartment.
			 * 4. If the usergroup type is department the find it based on changed ministry and subdepartment.
			 * **/
			Ministry ministry = null;
			SubDepartment subDepartment = null;
			if(question.getBallotStatus() != null){
				Ballot ballot = Ballot.find(question);
				QuestionDraft questionDraft = Question.findLatestGroupChangedDraft(question);
				QuestionDraft latestGroupChangedDraft = Question.findGroupChangedDraft(question);
				if(ballot != null && questionDraft != null 
					&& ballot.getBallotDate().before(latestGroupChangedDraft.getEditedOn())){
					ministry = questionDraft.getMinistry();
					subDepartment = questionDraft.getSubDepartment();
				}else{
					 ministry = question.getMinistry();
					 subDepartment = question.getSubDepartment();
				}
			}else{
				 ministry = question.getMinistry();
				 subDepartment = question.getSubDepartment();
			}
			
			for(WorkflowActor i:allEligibleActors){
				int noOfComparisons=0;
				int noOfSuccess=0;
				UserGroupType userGroupTypeTemp=i.getUserGroupType();
				List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
						userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
				if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT)
						||userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
					ministry = question.getMinistry();
					subDepartment = question.getSubDepartment();
				}
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
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
							String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
							for(int k=0; k<allowedMinistries.length; k++) {
								if(allowedMinistries[k].equals(ministry.getName())) {										
									noOfSuccess++;
									break;
								}
							}
							noOfComparisons++;
						}else{
							noOfComparisons++;
						}
					}
					if(subDepartment!=null){
						if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
							String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
							for(int k=0; k<allowedSubdepartments.length; k++) {
								if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
									noOfSuccess++;
									break;
								}
							}
							noOfComparisons++;
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
						if(!userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							break;
						}
					}				
				}
			}		

		}		
		return actualActors;
	}
	
	public WorkflowConfig getLatest(final Question question,final String internalStatus,final String locale) {
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
	/******************************Question****************************/	

	/***************************Resolution******************************/
	//maybe removed as generic method is added below this method
	public WorkflowConfig getLatest(final Resolution resolution,final String internalStatus,final String locale) {
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
	
	public Reference findActorVOAtGivenLevel(final Resolution resolution, final HouseType workflowHouseType, final Status status, 
			final String usergroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(resolution, workflowHouseType, status.getType(), locale.toString());
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(resolution, workflowHouseType, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;
	}
	
	private Reference findActorDetails(final Resolution resolution,
			final HouseType houseType,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		DeviceType deviceType = resolution.getType();
		Ministry ministry = resolution.getMinistry();
		SubDepartment subDepartment = resolution.getSubDepartment();
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(resolution.getMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT)){
				ministry = resolution.getMinistry();
				subDepartment = resolution.getSubDepartment();
			}
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
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
				||status.equals(ApplicationConstants.RESOLUTION_RECOMMEND_DISCUSS)){

			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				workflowConfig=getLatest(resolution,houseType,resolution.getInternalStatusLowerHouse().getType(),locale.toString());
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				workflowConfig=getLatest(resolution,houseType,resolution.getInternalStatusUpperHouse().getType(),locale.toString());
			}

			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		}else if((status.equals(ApplicationConstants.RESOLUTION_PROCESSED_SENDTODESKOFFICER))
				&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
				workflowConfig = getLatest(resolution,houseType,resolution.getInternalStatusLowerHouse().getType(),locale.toString());
			}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
				workflowConfig = getLatest(resolution,houseType,resolution.getInternalStatusUpperHouse().getType(),locale.toString());
			}
			UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
			currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}else{
			workflowConfig = getLatest(resolution,houseType,status,locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig,userGroupType,level);
			CustomParameter userGroupTypeToBeExcluded = null;
			if(status.toUpperCase().contains("FINAL")){
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
			}else{
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
			}
			if(userGroupTypeToBeExcluded != null && 
					(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
				String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
				String[] arrUsergroupTypes = strUsergroupTypes.split(",");
				List<Long> usergroupTypeIds = new ArrayList<Long>();
				for(String s : arrUsergroupTypes){
					UserGroupType ugt = UserGroupType.findByType(s, locale);
					if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
						if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							usergroupTypeIds.add(ugt.getId());
						}
					}else{
						usergroupTypeIds.add(ugt.getId());
					}						
				}
				List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
				allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
			}else{
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
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
				Map<String,String> params=j.getParameters();
				
				/**The Parameters doesnot need to check in case of MEMBER***/
				if(!j.getUserGroupType().getType().equals(ApplicationConstants.MEMBER)){					
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
						if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
							String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
							for(int k=0; k<allowedMinistries.length; k++) {
								if(allowedMinistries[k].equals(ministry.getName())) {										
									noOfSuccess++;
									break;
								}
							}
							noOfComparisons++;
						}else{
							noOfComparisons++;
						}
					}
					/*if(department!=null){
						if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}*/
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
					reference.setState(params.get(ApplicationConstants.ACTORSTATE_KEY+"_"+locale));
					reference.setRemark(params.get(ApplicationConstants.ACTORREMARK_KEY+"_"+locale));
					if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
						if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
							if(!reference.getId().equals(resolution.getActorLowerHouse())){
									references.add(reference);
								}
						}else{
							if(!reference.getId().equals(resolution.getActorUpperHouse())){
									references.add(reference);
							}
						}
						
					}else{
						references.add(reference);
						break;
					}
				}				
			}
		}		
		return references;
	}
	/********************************Resolution*************************/
	
	/*********************************Committee*************************/
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
				ApplicationConstants.COMMITTEE_RECOMMEND_SENDBACK)
				|| status.getType().equals(
						ApplicationConstants.COMMITTEE_PROCESSED_SENDBACK)) {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}	
	public List<WorkflowActor> findCommitteeTourActors(
			final HouseType houseType,
			final UserGroup userGroup,
			final Status status, 
			final String workflowName, 
			final Integer assigneeLevel,
			final String locale) {
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType, "tour"+workflowName, "COMMITTEE", locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = 
			this.getWorkflowActor(wfConfig, userGroupType, assigneeLevel);
		
		if(status.getType().equals(
				ApplicationConstants.COMMITTEETOUR_RECOMMEND_SENDBACK)) {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActors;
	}

	public WorkflowActor findNextCommitteeTourActor(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName,
			final Integer assigneeLevel, 
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = 
			this.getWorkflowActor(wfConfig, userGroupType, assigneeLevel);
		
		if(status.getType().equals(
				ApplicationConstants.COMMITTEETOUR_RECOMMEND_SENDBACK)) {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}
	/*********************************Committee*************************/
	
	/*******************************Bill*******************************/
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
	
	/**
	 * 
	 * @param billAmendmentMotion
	 * @param internalStatus (though this is normally internal status, but for some cases this can be recommendation status, 
	 * translation status or even opinionFromLawAndJD status)
	 * @param userGroup
	 * @param level
	 * @param locale
	 * @return
	 */
	public List<Reference> findBillAmendmentMotionActorsVO(final BillAmendmentMotion billAmendmentMotion,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_DISCUSS)								
		){
			workflowConfig=getLatest(billAmendmentMotion,billAmendmentMotion.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else{
			workflowConfig=getLatest(billAmendmentMotion,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}
		HouseType houseType=billAmendmentMotion.getHouseType();
		DeviceType deviceType=billAmendmentMotion.getType();
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
	
	private WorkflowConfig getLatest(final BillAmendmentMotion billAmendmentMotion,final String internalStatus,final String locale) {
		HouseType houseTypeForWorkflow = billAmendmentMotion.getHouseType();
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";				
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+billAmendmentMotion.getType().getId()+
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
	
	public Reference findActorVOAtFirstLevel(final Bill bill, final Workflow processWorkflow, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(bill, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(bill, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final Bill bill, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(bill, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(bill, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowActor findFirstActor(final Bill bill, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(bill, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private WorkflowConfig getLatest(final Bill bill, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", bill.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", bill.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	private Reference findActorDetails(final Bill bill,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = bill.getHouseType();
		DeviceType deviceType = bill.getType();
		Ministry ministry = bill.getMinistry();
		SubDepartment subDepartment = bill.getSubDepartment();	
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(bill.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT)){
				ministry = bill.getMinistry();
				subDepartment = bill.getSubDepartment();
			}
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	/*********************************Bill*************************/
	
	/**********************************Editing*******************************/
	public WorkflowActor findNextEditingActor(final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = this.getWorkflowActor(wfConfig, userGroupType, level);
		
		wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.ASC);
		
		return wfActor;
	}	
	
	public List<WorkflowActor> findEditingActors(final HouseType houseType,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int level,
			final String locale) {
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		
		WorkflowConfig wfConfig = this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = this.getWorkflowActor(wfConfig, userGroupType, level);
		
		if(status.getType().equals(ApplicationConstants.EDITING_RECOMMEND_SENDBACK)) {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActors;
	}
	/**********************************Editing*******************************/
	
	/********************************Motion*********************/
	public List<Reference> findMotionActorsVO(final Motion motion,
			final Status internalStatus, final UserGroup userGroup,
			final int level, final String locale) {
		String status = internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		/****
		 * Note :Here this can be configured so that list of workflows which
		 * goes back is read dynamically
		 ****/
		if (status.equals(ApplicationConstants.MOTION_RECOMMEND_SENDBACK)
				|| status.equals(ApplicationConstants.MOTION_RECOMMEND_DISCUSS)) {
			workflowConfig = getLatest(motion, motion.getInternalStatus()
					.getType(), locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig,
					userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(
					workflowConfig, currentWorkflowActor,
					ApplicationConstants.DESC);
		}else if(status.equals(ApplicationConstants.MOTION_PROCESSED_SENDTODESKOFFICER)
			&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			workflowConfig = getLatest(motion, motion.getInternalStatus()
					.getType(), locale.toString());
			UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
			currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		} else {
			workflowConfig = getLatest(motion, status, locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig,
					userGroupType, level);
//			allEligibleActors = getWorkflowActorsExcludingCurrent(
//					workflowConfig, currentWorkflowActor,
//					ApplicationConstants.ASC);
			CustomParameter userGroupTypeToBeExcluded = null;
			if(status.toUpperCase().contains("FINAL")){
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
			}else{
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
			}
			if(userGroupTypeToBeExcluded != null && 
					(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
				String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
				String[] arrUsergroupTypes = strUsergroupTypes.split(",");
				List<Long> usergroupTypeIds = new ArrayList<Long>();
				for(String s : arrUsergroupTypes){
					UserGroupType ugt = UserGroupType.findByType(s, locale);
					if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
						if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							usergroupTypeIds.add(ugt.getId());
						}
					}else{
						usergroupTypeIds.add(ugt.getId());
					}						
				}
				List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
				allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
			}else{
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
		}
		HouseType houseType = motion.getHouseType();
		DeviceType deviceType = motion.getType();
		Ministry ministry = motion.getMinistry();
		SubDepartment subDepartment = motion.getSubDepartment();

		for (WorkflowActor i : allEligibleActors) {
			UserGroupType userGroupTypeTemp = i.getUserGroupType();
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(
					UserGroup.class, "userGroupType", userGroupTypeTemp,
					"activeFrom", ApplicationConstants.DESC, locale);
			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT) 
					|| (userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
				ministry = motion.getMinistry();
				subDepartment = motion.getSubDepartment();
			}
			for (UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();
				if (houseType != null) {
					HouseType bothHouse = HouseType.findByFieldName(
							HouseType.class, "type", "bothhouse", locale);
					if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_"
							+ locale) != null && !params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(
									bothHouse.getName())) {
						if (params.get(
								ApplicationConstants.HOUSETYPE_KEY + "_"
										+ locale).contains(houseType.getName())) {
							noOfComparisons++;
							noOfSuccess++;
						} else {
							noOfComparisons++;
						}
					}
				}
				if (deviceType != null) {
					if (params.get(ApplicationConstants.DEVICETYPE_KEY + "_"
							+ locale) != null
							&& params.get(
									ApplicationConstants.DEVICETYPE_KEY + "_"
											+ locale).contains(
									deviceType.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if (ministry != null) {
					if (params.get(ApplicationConstants.MINISTRY_KEY + "_"
							+ locale) != null
							&& params.get(
									ApplicationConstants.MINISTRY_KEY + "_"
											+ locale).contains(
									ministry.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if (subDepartment != null) {
					// System.out.println(j.getUserGroupType().getType()+":"+params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale));
					if (params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_"
							+ locale) != null
							&& params.get(
									ApplicationConstants.SUBDEPARTMENT_KEY
											+ "_" + locale).contains(
									subDepartment.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				Date fromDate = j.getActiveFrom();
				Date toDate = j.getActiveTo();
				Date currentDate = new Date();
				noOfComparisons++;
				if (((fromDate == null || currentDate.after(fromDate) || currentDate
						.equals(fromDate)) && (toDate == null
						|| currentDate.before(toDate) || currentDate
							.equals(toDate)))) {
					noOfSuccess++;
				}
				/**** Include Leave Module ****/
				if (noOfComparisons == noOfSuccess) {
					Reference reference = new Reference();
					User user = User.findByFieldName(User.class, "credential",
							j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername() + "#"
							+ j.getUserGroupType().getType() + "#"
							+ i.getLevel() + "#" + userGroupTypeTemp.getName()
							+ "#" + user.getTitle() + " " + user.getFirstName()
							+ " " + user.getMiddleName() + " "
							+ user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
						if(!reference.getId().equals(motion.getActor())){
							references.add(reference);
						}
					}else{
						references.add(reference);
						break;
					}
					
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
	
	public Reference findActorVOAtGivenLevel(final Motion motion, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(motion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}

	public Reference findActorVOAtFirstLevel(final Motion motion, final Workflow processWorkflow, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(motion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}

	private WorkflowActor findFirstActor(final Motion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(motion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}

	private WorkflowConfig getLatest(final Motion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}

	private Reference findActorDetails(final Motion motion, final WorkflowActor workflowActor, final String locale) {
		Reference actorAtGivenLevel = null;
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getType();
		Ministry ministry=motion.getMinistry();
		SubDepartment subDepartment=motion.getSubDepartment();
		//Department department=subDepartment.getDepartment();
		UserGroupType userGroupTypeTemp=workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user=User.find(motion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
//				if(department!=null){
//					if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
//						noOfComparisons++;
//						noOfSuccess++;
//					}else{
//						noOfComparisons++;
//					}
//				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	/********************************Motion*********************/
	
	/********************************StandaloneMotion*********************/
	public Reference findActorVOAtFirstLevel(final StandaloneMotion motion, final Workflow processWorkflow, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(motion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final StandaloneMotion motion, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(motion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}

	private WorkflowActor findFirstActor(final StandaloneMotion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(motion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private Reference findActorDetails(final StandaloneMotion motion, final WorkflowActor workflowActor, final String locale) {
		Reference actorAtGivenLevel = null;
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getType();
		Ministry ministry=motion.getMinistry();
		SubDepartment subDepartment=motion.getSubDepartment();
		Department department=subDepartment.getDepartment();
		UserGroupType userGroupTypeTemp=workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user=User.find(motion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				/*if(department!=null){
					if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}*/
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	
	private WorkflowConfig getLatest(final StandaloneMotion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	public List<Reference> findStandaloneMotionActorsVO(final StandaloneMotion motion,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		if(status.equals(ApplicationConstants.STANDALONE_SYSTEM_GROUPCHANGED)){

		}else{
			/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
			if(status.equals(ApplicationConstants.STANDALONE_RECOMMEND_SENDBACK)
					||status.equals(ApplicationConstants.STANDALONE_RECOMMEND_DISCUSS)
			){
				workflowConfig = getLatest(motion,motion.getInternalStatus().getType(),locale.toString());
				userGroupType = userGroup.getUserGroupType();
				currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig, currentWorkflowActor, ApplicationConstants.DESC);
			}else if((status.equals(ApplicationConstants.STANDALONEMOTION_PROCESSED_SENDTODESKOFFICER))
					&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
				workflowConfig = getLatest(motion,motion.getInternalStatus().getType(),locale.toString());
				UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
				currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}else{
				workflowConfig = getLatest(motion,status,locale.toString());
				userGroupType = userGroup.getUserGroupType();
				currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
				CustomParameter userGroupTypeToBeExcluded = null;
				if(status.toUpperCase().contains("FINAL")){
					userGroupTypeToBeExcluded = CustomParameter.
					findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
				}else{
					userGroupTypeToBeExcluded = CustomParameter.
					findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
				}
				if(userGroupTypeToBeExcluded != null && 
						(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
					String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
					String[] arrUsergroupTypes = strUsergroupTypes.split(",");
					List<Long> usergroupTypeIds = new ArrayList<Long>();
					for(String s : arrUsergroupTypes){
						UserGroupType ugt = UserGroupType.findByType(s, locale);
						if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
							if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
								usergroupTypeIds.add(ugt.getId());
							}
						}else{
							usergroupTypeIds.add(ugt.getId());
						}						
					}
					List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
					allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
				}else{
					allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
				}
			}
			HouseType houseType = motion.getHouseType();
			DeviceType deviceType = motion.getType();
			Ministry ministry = motion.getMinistry();
			SubDepartment subDepartment = motion.getSubDepartment();		
			for(WorkflowActor i : allEligibleActors){
				UserGroupType userGroupTypeTemp = i.getUserGroupType();
				if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
					try {
						User user = User.find(motion.getPrimaryMember());
						Reference reference = new Reference();
						reference.setId(user.getCredential().getUsername()
								+"#"+userGroupTypeTemp.getType()
								+"#"+i.getLevel()
								+"#"+userGroupTypeTemp.getName()
								+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
						reference.setName(userGroupTypeTemp.getName());
						references.add(reference);
						break;
					} catch (ELSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
							userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
					
					for(UserGroup j:userGroups){
						int noOfComparisons = 0;
						int noOfSuccess = 0;
						Map<String,String> params = j.getParameters();
						if(houseType != null){
							HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
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
							if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
								String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
								for(int k=0; k<allowedMinistries.length; k++) {
									if(allowedMinistries[k].equals(ministry.getName())) {										
										noOfSuccess++;
										break;
									}
								}
								noOfComparisons++;
							}else{
								noOfComparisons++;
							}
						}
						if(subDepartment!=null){
							if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
								String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
								for(int k=0; k<allowedSubdepartments.length; k++) {
									if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
										noOfSuccess++;
										break;
									}
								}
								noOfComparisons++;
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
							reference.setState(params.get(ApplicationConstants.ACTORSTATE_KEY+"_"+locale));
							reference.setRemark(params.get(ApplicationConstants.ACTORREMARK_KEY+"_"+locale));
							if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
								if(!reference.getId().equals(motion.getActor())){
									references.add(reference);
								}
							}else{
								references.add(reference);
								break;
							}
						}				
					}
				}
			}		

		}		
		return references;
	}
	
	public WorkflowConfig getLatest(final StandaloneMotion motion,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp = internalStatus.split("_");
		String workflowName = temp[temp.length-1]+"_workflow";
		String strQuery = "SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id " + ApplicationConstants.DESC ;				
		javax.persistence.Query query = this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getType().getId());
		query.setParameter("workflowName", workflowName);
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	/********************************StandaloneMotion*********************/
	
	/********************************CutMotion*********************/
	public Reference findActorVOAtGivenLevel(final CutMotion cutMotion, final Status status, 
			final String usergroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(cutMotion, status.getType(), locale.toString());
		UserGroupType userGroupType = UserGroupType.findByType(usergroupType, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(cutMotion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;
	}
	public Reference findActorVOAtGivenLevel(final CutMotion motion, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(motion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}

	public Reference findActorVOAtFirstLevel(final CutMotion motion, final Workflow processWorkflow, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(motion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowConfig getLatest(final CutMotion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getDeviceType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
//	public WorkflowConfig getLatest(final CutMotion motion,final String internalStatus,final String locale) {
//		/**** Latest Workflow Configurations ****/
//		String[] temp = internalStatus.split("_");
//		String workflowName = temp[temp.length-1]+"_workflow";
//		String strQuery = "SELECT wc FROM WorkflowConfig wc" +
//				" JOIN wc.workflow wf" +
//				" JOIN wc.deviceType d " +
//				" JOIN wc.houseType ht" +
//				" WHERE d.id=:deviceTypeId" +
//				" AND wf.type=:workflowName" +
//				" AND ht.id=:houseTypeId"+
//				" AND wc.isLocked=true ORDER BY wc.id " + ApplicationConstants.DESC ;				
//		javax.persistence.Query query = this.em().createQuery(strQuery);
//		query.setParameter("deviceTypeId", motion.getDeviceType().getId());
//		query.setParameter("workflowName", workflowName);
//		query.setParameter("houseTypeId", motion.getHouseType().getId());
//		try{
//			return (WorkflowConfig) query.getResultList().get(0);
//		}catch(Exception ex){
//			ex.printStackTrace();
//			return new WorkflowConfig();
//		}	
//	}

	private Reference findActorDetails(final CutMotion motion, final WorkflowActor workflowActor, final String locale) {
		Reference actorAtGivenLevel = null;
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getDeviceType();
		Ministry ministry=motion.getMinistry();
		SubDepartment subDepartment=motion.getSubDepartment();
		Department department=subDepartment.getDepartment();
		UserGroupType userGroupTypeTemp=workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user=User.find(motion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	
	private WorkflowActor findFirstActor(final CutMotion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(motion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	public WorkflowConfig getLatest(final CutMotionDate cutMotionDate,final String internalStatus,final String locale) {
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
		query.setParameter("deviceTypeId", cutMotionDate.getDeviceType().getId());
		query.setParameter("workflow",workflowName);
		query.setParameter("houseTypeId", cutMotionDate.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	public WorkflowConfig getLatest(final CutMotion motion,final String internalStatus,final String locale) {
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
		query.setParameter("deviceTypeId", motion.getDeviceType().getId());
		query.setParameter("workflow",workflowName);
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	public List<Reference> findCutMotionActorsVO(final CutMotion motion,
			final Status internalStatus, final UserGroup userGroup,
			final int level, final String locale) {
		String status = internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		/****
		 * Note :Here this can be configured so that list of workflows which
		 * goes back is read dynamically
		 ****/
		if (status.equals(ApplicationConstants.CUTMOTION_RECOMMEND_SENDBACK)
				|| status.equals(ApplicationConstants.CUTMOTION_RECOMMEND_DISCUSS)) {
			workflowConfig = getLatest(motion, motion.getInternalStatus().getType(), locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(
					workflowConfig, currentWorkflowActor,
					ApplicationConstants.DESC);
		} else if(status.equals(ApplicationConstants.CUTMOTION_PROCESSED_SENDTODESKOFFICER)
				&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			workflowConfig = getLatest(motion,motion.getInternalStatus().getType(),locale.toString());
			UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
			currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		} else {
			workflowConfig = getLatest(motion, status, locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			CustomParameter userGroupTypeToBeExcluded = null;
			if(status.toUpperCase().contains("FINAL")){
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
			}else{
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
			}
			if(userGroupTypeToBeExcluded != null && 
					(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
				String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
				String[] arrUsergroupTypes = strUsergroupTypes.split(",");
				List<Long> usergroupTypeIds = new ArrayList<Long>();
				for(String s : arrUsergroupTypes){
					UserGroupType ugt = UserGroupType.findByType(s, locale);
					if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
						if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							usergroupTypeIds.add(ugt.getId());
						}
					}else{
						usergroupTypeIds.add(ugt.getId());
					}						
				}
				List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
				allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
			}else{
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
		}
		HouseType houseType = motion.getHouseType();
		DeviceType deviceType = motion.getDeviceType();
		Ministry ministry = motion.getMinistry();
		SubDepartment subDepartment = motion.getSubDepartment();
		for (WorkflowActor i : allEligibleActors) {
			UserGroupType userGroupTypeTemp = i.getUserGroupType();
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(
					UserGroup.class, "userGroupType", userGroupTypeTemp,
					"activeFrom", ApplicationConstants.DESC, locale);
			for (UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();
				if (houseType != null) {
					HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type", "bothhouse", locale);
					if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale) != null
							&& !params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(bothHouse.getName())) {
						if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(houseType.getName())) {
							noOfComparisons++;
							noOfSuccess++;
						} else {
							noOfComparisons++;
						}
					}
				}
				if (deviceType != null) {
					if (params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale) != null
							&& params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale).contains(deviceType.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if (ministry != null) {
					if (params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale) != null
							&& params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale).contains(ministry.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if (subDepartment != null) {
					// System.out.println(j.getUserGroupType().getType()+":"+params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale));
					if (params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale) != null
							&& params.get(
									ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale).contains(subDepartment.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				Date fromDate = j.getActiveFrom();
				Date toDate = j.getActiveTo();
				Date currentDate = new Date();
				noOfComparisons++;
				if (((fromDate == null || currentDate.after(fromDate) || currentDate.equals(fromDate)) 
						&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate)))) {
					noOfSuccess++;
				}
				/**** Include Leave Module ****/
				if (noOfComparisons == noOfSuccess) {
					Reference reference = new Reference();
					User user = User.findByFieldName(User.class, "credential", j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername() + "#"
							+ j.getUserGroupType().getType() + "#"
							+ i.getLevel() + "#" + userGroupTypeTemp.getName()
							+ "#" + user.getTitle() + " " + user.getFirstName()
							+ " " + user.getMiddleName() + " "
							+ user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					reference.setState(params.get(ApplicationConstants.ACTORSTATE_KEY+"_"+locale));
					reference.setRemark(params.get(ApplicationConstants.ACTORREMARK_KEY+"_"+locale));
					if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
						if(!reference.getId().equals(motion.getActor())){
							references.add(reference);
						}
					}else{
						references.add(reference);
						break;
					}
				}
			}
		}

		return references;
	}

	public WorkflowActor findNextCutMotionActor(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName,
			final Integer assigneeLevel, 
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = this.getWorkflowActor(wfConfig, userGroupType, assigneeLevel);
		
		if(status.getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_SENDBACK)
				|| status.getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_DISCUSS)) {
			wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}
	
	public WorkflowActor findNextCutMotionDateActor(final DeviceType deviceType,
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = this.getWorkflowActor(wfConfig, userGroupType, level);
		
		if(status.getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_SENDBACK)
				|| status.getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_SENDBACK)) {
			wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}	
	
	public List<WorkflowActor> findCutMotionDateActors(final HouseType houseType,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int level,
			final String locale) {
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		
		WorkflowConfig wfConfig = this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = this.getWorkflowActor(wfConfig, userGroupType, level);
		
		if(status.getType().equals(ApplicationConstants.CUTMOTIONDATE_RECOMMEND_DATE_SENDBACK)
				|| status.getType().equals(ApplicationConstants.CUTMOTIONDATE_RECOMMEND_DATE_DISCUSS)) {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActors;
	}
	/********************************CutMotion***********************/
	
	/********************************EventMotion*********************/
	public Reference findActorVOAtFirstLevel(final EventMotion motion, final Workflow processWorkflow, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(motion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final EventMotion motion, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(motion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(motion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowActor findFirstActor(final EventMotion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(motion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private WorkflowConfig getLatest(final EventMotion motion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getDeviceType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}

	private Reference findActorDetails(final EventMotion motion, final WorkflowActor workflowActor, final String locale) {
		Reference actorAtGivenLevel = null;
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getDeviceType();
		UserGroupType userGroupTypeTemp=workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(motion.getMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	
	public List<Reference> findEventMotionActorsVO(final EventMotion motion,
			final Status internalStatus, final UserGroup userGroup,
			final int level, final String locale) {
		String status = internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		/****
		 * Note :Here this can be configured so that list of workflows which
		 * goes back is read dynamically
		 ****/
		if (status.equals(ApplicationConstants.EVENTMOTION_RECOMMEND_SENDBACK)
				|| status.equals(ApplicationConstants.EVENTMOTION_RECOMMEND_DISCUSS)) {
			workflowConfig = getLatest(motion, motion.getInternalStatus().getType(), locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig, currentWorkflowActor, ApplicationConstants.DESC);
		} else {
			workflowConfig = getLatest(motion, status, locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig, currentWorkflowActor, ApplicationConstants.ASC);
		}
		HouseType houseType = motion.getHouseType();
		DeviceType deviceType = motion.getDeviceType();
		for (WorkflowActor i : allEligibleActors) {
			UserGroupType userGroupTypeTemp = i.getUserGroupType();
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(
					UserGroup.class, "userGroupType", userGroupTypeTemp,
					"activeFrom", ApplicationConstants.DESC, locale);
			for (UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();
				if (houseType != null) {
					HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type", "bothhouse", locale);
					if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale) != null
							&& !params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(bothHouse.getName())) {
						if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(houseType.getName())) {
							noOfComparisons++;
							noOfSuccess++;
						} else {
							noOfComparisons++;
						}
					}
				}
				if (deviceType != null) {
					if (params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale) != null
							&& params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale).contains(deviceType.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				
				
				Date fromDate = j.getActiveFrom();
				Date toDate = j.getActiveTo();
				Date currentDate = new Date();
				noOfComparisons++;
				if (((fromDate == null || currentDate.after(fromDate) || currentDate.equals(fromDate)) 
						&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate)))) {
					noOfSuccess++;
				}
				/**** Include Leave Module ****/
				if (noOfComparisons == noOfSuccess) {
					Reference reference = new Reference();
					User user = User.findByFieldName(User.class, "credential", j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername() + "#"
							+ j.getUserGroupType().getType() + "#"
							+ i.getLevel() + "#" + userGroupTypeTemp.getName()
							+ "#" + user.getTitle() + " " + user.getFirstName()
							+ " " + user.getMiddleName() + " "
							+ user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}
			}
		}

		return references;
	}

	public WorkflowActor findNextEventMotionActor(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName,
			final Integer assigneeLevel, 
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = this.getWorkflowActor(wfConfig, userGroupType, assigneeLevel);
		
		if(status.getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_SENDBACK)
				|| status.getType().equals(ApplicationConstants.EVENTMOTION_RECOMMEND_DISCUSS)) {
			wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}
	
	private WorkflowConfig getLatest(final EventMotion motion,final String internalStatus,final String locale) {
		/**** Latest Workflow Configurations ****/
		String[] temp = internalStatus.split("_");
		String workflowName = temp[temp.length-1]+"_workflow";
		String strQuery = "SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflow"+
				" AND ht.id=:houseTypeId" +
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query = this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", motion.getDeviceType().getId());
		query.setParameter("workflow", workflowName);
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	/********************************EventMotion*********************/
	
	/********************************DiscussionMotion*********************/	
	
	private WorkflowConfig getLatest(final DiscussionMotion question, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
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
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", question.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	public List<Reference> findDiscussionMotionActors(
			DiscussionMotion motion, Status internalStatus,
			UserGroup userGroup, int level, String locale) {
		
		String status = internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		/****
		 * Note :Here this can be configured so that list of workflows which
		 * goes back is read dynamically
		 ****/
		if (status.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_SENDBACK)
				|| status.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_DISCUSS)) {
			workflowConfig = getLatest(motion, motion.getInternalStatus().getType(), locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(
					workflowConfig, currentWorkflowActor,
					ApplicationConstants.DESC);
		} else {
			workflowConfig = getLatest(motion, status, locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig, currentWorkflowActor, ApplicationConstants.ASC);
		}
		
		HouseType houseType = motion.getHouseType();
		DeviceType deviceType = motion.getType();
		
		List<Ministry> domainMinistries = motion.getMinistries();
		List<SubDepartment> domainSubDepartments = motion.getSubDepartments();
		
		for (WorkflowActor i : allEligibleActors) {
			UserGroupType userGroupTypeTemp = i.getUserGroupType();
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(
					UserGroup.class, "userGroupType", userGroupTypeTemp,
					"activeFrom", ApplicationConstants.DESC, locale);
			
			for (UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();
				if (houseType != null) {
					HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type", "bothhouse", locale);
					if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale) != null
							&& !params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(bothHouse.getName())) {
						if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(houseType.getName())) {
							noOfComparisons++;
							noOfSuccess++;
						} else {
							noOfComparisons++;
						}
					}
				}
				if (deviceType != null) {
					if (params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale) != null
							&& params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale).contains(deviceType.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				
				if(domainMinistries != null) {
					if (params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale) != null
							&& containsGivenData(domainMinistries, 
									params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale))) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if(domainSubDepartments != null) {
					
					if (params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale) != null
							&& containsGivenData(domainSubDepartments, 
									params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale))) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				Date fromDate = j.getActiveFrom();
				Date toDate = j.getActiveTo();
				Date currentDate = new Date();
				noOfComparisons++;
				if (((fromDate == null || currentDate.after(fromDate) || currentDate.equals(fromDate)) 
						&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate)))) {
					noOfSuccess++;
				}
				/**** Include Leave Module ****/
				if (noOfComparisons == noOfSuccess) {
					Reference reference = new Reference();
					User user = User.findByFieldName(User.class, "credential", j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername() + "#"
							+ j.getUserGroupType().getType() + "#"
							+ i.getLevel() + "#" + userGroupTypeTemp.getName()
							+ "#" + user.getTitle() + " " + user.getFirstName()
							+ " " + user.getMiddleName() + " "
							+ user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}
			}
		}

		return references;
	}
	
	public List<Reference> findDiscussionMotionActorsByGivenMinistryAndSubdepartment(final Ministry ministry,
			final SubDepartment subDepartment,
			DiscussionMotion motion, Status internalStatus,
			UserGroup userGroup, int level, String locale) {
		
		String status = internalStatus.getType();
		WorkflowConfig workflowConfig = null;
		UserGroupType userGroupType = null;
		WorkflowActor currentWorkflowActor = null;
		List<Reference> references = new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors = new ArrayList<WorkflowActor>();
		/****
		 * Note :Here this can be configured so that list of workflows which
		 * goes back is read dynamically
		 ****/
		if (status.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_SENDBACK)
				|| status.equals(ApplicationConstants.DISCUSSIONMOTION_RECOMMEND_DISCUSS)) {
			workflowConfig = getLatest(motion, motion.getInternalStatus().getType(), locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(
					workflowConfig, currentWorkflowActor,
					ApplicationConstants.DESC);
		} else {
			workflowConfig = getLatest(motion, status, locale.toString());
			userGroupType = userGroup.getUserGroupType();
			currentWorkflowActor = getWorkflowActor(workflowConfig, userGroupType, level);
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig, currentWorkflowActor, ApplicationConstants.ASC);
		}
		
		HouseType houseType = motion.getHouseType();
		DeviceType deviceType = motion.getType();
		
		for (WorkflowActor i : allEligibleActors) {
			UserGroupType userGroupTypeTemp = i.getUserGroupType();
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(
					UserGroup.class, "userGroupType", userGroupTypeTemp,
					"activeFrom", ApplicationConstants.DESC, locale);
			
			for (UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();
				if (houseType != null) {
					HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type", "bothhouse", locale);
					if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale) != null
							&& !params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(bothHouse.getName())) {
						if (params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(houseType.getName())) {
							noOfComparisons++;
							noOfSuccess++;
						} else {
							noOfComparisons++;
						}
					}
				}
				if (deviceType != null) {
					if (params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale) != null
							&& params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale).contains(deviceType.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				
				if(ministry != null) {
					if (params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale) != null
							&&  params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale).contains(ministry.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if(subDepartment != null) {
					
					if (params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale) != null
							&& params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale).contains(subDepartment.getName())) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				Date fromDate = j.getActiveFrom();
				Date toDate = j.getActiveTo();
				Date currentDate = new Date();
				noOfComparisons++;
				if (((fromDate == null || currentDate.after(fromDate) || currentDate.equals(fromDate)) 
						&& (toDate == null || currentDate.before(toDate) || currentDate.equals(toDate)))) {
					noOfSuccess++;
				}
				/**** Include Leave Module ****/
				if (noOfComparisons == noOfSuccess) {
					Reference reference = new Reference();
					User user = User.findByFieldName(User.class, "credential", j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername() + "#"
							+ j.getUserGroupType().getType() + "#"
							+ i.getLevel() + "#" + userGroupTypeTemp.getName()
							+ "#" + user.getTitle() + " " + user.getFirstName()
							+ " " + user.getMiddleName() + " "
							+ user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
					references.add(reference);
					break;
				}
			}
		}

		return references;
	}
	public WorkflowConfig getLatest(final DiscussionMotion motion,final String internalStatus,final String locale) {
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
		query.setParameter("deviceTypeId", motion.getType().getId());
		query.setParameter("workflowName",workflowName);
		query.setParameter("houseTypeId", motion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	/**** To compare Ministry and sub-departments for finding of actor in discussion motion ****/
	public boolean containsGivenData(final List<? extends BaseDomain> dataList, final String data){
		
		boolean retVal = false;
		for(BaseDomain b : dataList){
			
			String tempData = null;
			
			if(b instanceof Ministry){
				tempData = ((Ministry)b).getName();
			}
			
			if(b instanceof SubDepartment){
				tempData = ((SubDepartment)b).getName();
			}
			
			if(data.contains(tempData)){
				retVal = true;
				break;				
			}
		}
		
		return retVal;
	}
	
	public Reference findActorVOAtGivenLevel(final DiscussionMotion question, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(question, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(question, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private Reference findActorDetails(final DiscussionMotion motion, final WorkflowActor workflowActor, final String locale) {
		Reference actorAtGivenLevel = null;
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getType();
		Ministry ministry=motion.getMinistries().get(0);
		SubDepartment subDepartment=motion.getSubDepartments().get(0);
		Department department=subDepartment.getDepartment();
		UserGroupType userGroupTypeTemp=workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user=User.find(motion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				/*if(department!=null){
					if(params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEPARTMENT_KEY+"_"+locale).contains(department.getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}*/
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	/********************************DiscussionMotion*********************/
	
	/****************************** Adjournment Motion *********************/
	public List<Reference> findAdjournmentMotionActors(final AdjournmentMotion adjourn,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_DISCUSS)								
		){
			workflowConfig=getLatest(adjourn,adjourn.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else if(status.equals(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_SENDTODESKOFFICER)
				&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			workflowConfig = getLatest(adjourn,adjourn.getInternalStatus().getType(),locale.toString());
			UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
			currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		} else{
			workflowConfig=getLatest(adjourn,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			CustomParameter userGroupTypeToBeExcluded = null;
			if(status.toUpperCase().contains("FINAL")){
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
			}else{
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
			}
			if(userGroupTypeToBeExcluded != null && 
					(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
				String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
				String[] arrUsergroupTypes = strUsergroupTypes.split(",");
				List<Long> usergroupTypeIds = new ArrayList<Long>();
				for(String s : arrUsergroupTypes){
					UserGroupType ugt = UserGroupType.findByType(s, locale);
					if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
						if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							usergroupTypeIds.add(ugt.getId());
						}
					}else{
						usergroupTypeIds.add(ugt.getId());
					}						
				}
				List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
				allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
			}else{
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
		}
		HouseType houseType=adjourn.getHouseType();
		DeviceType deviceType=adjourn.getType();
		Ministry ministry = adjourn.getMinistry();
		SubDepartment subDepartment = adjourn.getSubDepartment();
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					reference.setState(params.get(ApplicationConstants.ACTORSTATE_KEY+"_"+locale));
					reference.setRemark(params.get(ApplicationConstants.ACTORREMARK_KEY+"_"+locale));
					if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
						if(!reference.getId().equals(adjourn.getActor())){
							references.add(reference);
						}
					}else{
						references.add(reference);
						break;
					}
				}				
			}
		}				
		return references;
	}
	
	public WorkflowConfig getLatest(final AdjournmentMotion adjournmentMotion,final String internalStatus,final String locale) {
		HouseType houseTypeForWorkflow = adjournmentMotion.getHouseType();
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";				
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+adjournmentMotion.getType().getId()+
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
	
	public Reference findActorVOAtFirstLevel(final AdjournmentMotion adjournmentMotion, final Workflow processWorkflow, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(adjournmentMotion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(adjournmentMotion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final AdjournmentMotion adjournmentMotion, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(adjournmentMotion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(adjournmentMotion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowActor findFirstActor(final AdjournmentMotion adjournmentMotion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(adjournmentMotion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private WorkflowConfig getLatest(final AdjournmentMotion adjournmentMotion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", adjournmentMotion.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", adjournmentMotion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	private Reference findActorDetails(final AdjournmentMotion adjournmentMotion,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = adjournmentMotion.getHouseType();
		DeviceType deviceType = adjournmentMotion.getType();
		Ministry ministry = adjournmentMotion.getMinistry();
		SubDepartment subDepartment = adjournmentMotion.getSubDepartment();	
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(adjournmentMotion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			/** uncomment below code if device has ballot **/
//			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT) 
//					|| (userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
//				ministry = adjournmentMotion.getMinistry();
//				subDepartment = adjournmentMotion.getSubDepartment();
//			}
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}	
	/****************************** Adjournment Motion *********************/
	
	/****************************** Special Mention Notice *********************/
	public List<Reference> findSpecialMentionNoticeActors(final SpecialMentionNotice specialmention,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.SPECIALMENTIONNOTICE_RECOMMEND_DISCUSS)								
		){
			workflowConfig=getLatest(specialmention,specialmention.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else if(status.equals(ApplicationConstants.SPECIALMENTIONNOTICE_PROCESSED_SENDTODESKOFFICER)
				&& userGroup.getUserGroupType().getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
			workflowConfig = getLatest(specialmention,specialmention.getInternalStatus().getType(),locale.toString());
			UserGroupType ugt = UserGroupType.findByType(ApplicationConstants.DEPARTMENT, locale);
			currentWorkflowActor = getWorkflowActor(workflowConfig,ugt,(level-1));
			allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		} else{
			workflowConfig=getLatest(specialmention,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			CustomParameter userGroupTypeToBeExcluded = null;
			if(status.toUpperCase().contains("FINAL")){
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_POSTFINAL_STATUS, "");
			}else{
				userGroupTypeToBeExcluded = CustomParameter.
				findByName(CustomParameter.class, ApplicationConstants.USERGROUPTYPE_TO_BE_EXCLUDED_FROM_WORKFLOWCONFIG_PREFINAL_STATUS, "");
			}
			if(userGroupTypeToBeExcluded != null && 
					(userGroupTypeToBeExcluded.getValue() != null  && !userGroupTypeToBeExcluded.getValue().isEmpty())){
				String strUsergroupTypes = userGroupTypeToBeExcluded.getValue();
				String[] arrUsergroupTypes = strUsergroupTypes.split(",");
				List<Long> usergroupTypeIds = new ArrayList<Long>();
				for(String s : arrUsergroupTypes){
					UserGroupType ugt = UserGroupType.findByType(s, locale);
					if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
						if(!ugt.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
							usergroupTypeIds.add(ugt.getId());
						}
					}else{
						usergroupTypeIds.add(ugt.getId());
					}						
				}
				List<WorkflowActor> workflowActorsToBeExcluded = getWorkflowActors(workflowConfig,usergroupTypeIds,level);
				allEligibleActors = getWorkflowActorsExcludingGivenActorList(workflowConfig, workflowActorsToBeExcluded, currentWorkflowActor, ApplicationConstants.ASC);
			}else{
				allEligibleActors = getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
			}
		}
		HouseType houseType=specialmention.getHouseType();
		DeviceType deviceType=specialmention.getType();
		Ministry ministry = specialmention.getMinistry();
		SubDepartment subDepartment = specialmention.getSubDepartment();
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					reference.setState(params.get(ApplicationConstants.ACTORSTATE_KEY+"_"+locale));
					reference.setRemark(params.get(ApplicationConstants.ACTORREMARK_KEY+"_"+locale));
					if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER)){
						if(!reference.getId().equals(specialmention.getActor())){
							references.add(reference);
						}
					}else{
						references.add(reference);
						break;
					}
				}				
			}
		}				
		return references;
	}
	
	public WorkflowConfig getLatest(final SpecialMentionNotice specialMentionNotice,final String internalStatus,final String locale) {
		HouseType houseTypeForWorkflow = specialMentionNotice.getHouseType();
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";				
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+specialMentionNotice.getType().getId()+
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
	
	public Reference findActorVOAtFirstLevel(final SpecialMentionNotice specialMentionNotice, final Workflow processWorkflow, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(specialMentionNotice, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(specialMentionNotice, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final SpecialMentionNotice specialMentionNotice, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(specialMentionNotice, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(specialMentionNotice, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowActor findFirstActor(final SpecialMentionNotice specialMentionNotice, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(specialMentionNotice, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private WorkflowConfig getLatest(final SpecialMentionNotice specialMentionNotice, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", specialMentionNotice.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", specialMentionNotice.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	private Reference findActorDetails(final SpecialMentionNotice specialMentionNotice,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = specialMentionNotice.getHouseType();
		DeviceType deviceType = specialMentionNotice.getType();
		Ministry ministry = specialMentionNotice.getMinistry();
		SubDepartment subDepartment = specialMentionNotice.getSubDepartment();	
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(specialMentionNotice.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			/** uncomment below code if device has ballot **/
//			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT) 
//					|| (userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT_DESKOFFICER))){
//				ministry = adjournmentMotion.getMinistry();
//				subDepartment = adjournmentMotion.getSubDepartment();
//			}
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}	
	
	/****************************** Special Mention Notice *********************/
	
	/****************************** Propriety Point *********************/
	public List<Reference> findProprietyPointActors(final ProprietyPoint proprietyPoint,
			final Status internalStatus,final UserGroup userGroup,final int level,final String locale) {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_DISCUSS)								
		){
			workflowConfig=getLatest(proprietyPoint,proprietyPoint.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else{
			workflowConfig=getLatest(proprietyPoint,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}
		HouseType houseType=proprietyPoint.getHouseType();
		DeviceType deviceType=proprietyPoint.getDeviceType();
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
	
	public WorkflowConfig getLatest(final ProprietyPoint proprietyPoint,final String internalStatus,final String locale) {
		HouseType houseTypeForWorkflow = proprietyPoint.getHouseType();
		/**** Latest Workflow Configurations ****/
		String[] temp=internalStatus.split("_");
		String workflowName=temp[temp.length-1]+"_workflow";				
		String query="SELECT wc FROM WorkflowConfig wc JOIN wc.workflow wf JOIN wc.deviceType d " +
		" JOIN wc.houseType ht "+
		" WHERE d.id="+proprietyPoint.getDeviceType().getId()+
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
	
	public Reference findActorVOAtFirstLevel(final ProprietyPoint proprietyPoint, final Workflow processWorkflow, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(proprietyPoint, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(proprietyPoint, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final ProprietyPoint proprietyPoint, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(proprietyPoint, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(proprietyPoint, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowActor findFirstActor(final ProprietyPoint proprietyPoint, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(proprietyPoint, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private WorkflowConfig getLatest(final ProprietyPoint proprietyPoint, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", proprietyPoint.getDeviceType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", proprietyPoint.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	private Reference findActorDetails(final ProprietyPoint proprietyPoint,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = proprietyPoint.getHouseType();
		DeviceType deviceType = proprietyPoint.getDeviceType();
		Ministry ministry = proprietyPoint.getMinistry();
		SubDepartment subDepartment = proprietyPoint.getSubDepartment();	
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(proprietyPoint.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			if(userGroupTypeTemp.getType().equals(ApplicationConstants.DEPARTMENT)){
				ministry = proprietyPoint.getMinistry();
				subDepartment = proprietyPoint.getSubDepartment();
			}
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subDepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subDepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}	
	/****************************** Propriety Point *********************/
	
	/****************************** BillAmendment Motion ****************************/
	public Reference findActorVOAtFirstLevel(final BillAmendmentMotion billAmendmentMotion, final Workflow processWorkflow, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(billAmendmentMotion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(billAmendmentMotion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;		
	}
	
	public Reference findActorVOAtGivenLevel(final BillAmendmentMotion billAmendmentMotion, final Workflow processWorkflow,
			final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(billAmendmentMotion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(billAmendmentMotion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;		
	}
	
	private WorkflowActor findFirstActor(final BillAmendmentMotion billAmendmentMotion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		WorkflowConfig latestWorkflowConfig = getLatest(billAmendmentMotion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;		
	}
	
	private WorkflowConfig getLatest(final BillAmendmentMotion billAmendmentMotion, final Workflow processWorkflow, final String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", billAmendmentMotion.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", billAmendmentMotion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}	
	}
	
	private Reference findActorDetails(final BillAmendmentMotion billAmendmentMotion,
			final WorkflowActor workflowActor, 
			final String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = billAmendmentMotion.getHouseType();
		DeviceType deviceType = billAmendmentMotion.getType();
		
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(billAmendmentMotion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}
	/****************************** BillAmendment Motion ****************************/
	
	/****************************** Prashnavali ****************************/
	public List<WorkflowActor> findPrashnavaliActors(
			final HouseType houseType,
			final UserGroup userGroup,
			final Status status, 
			final String workflowName, 
			final Integer assigneeLevel,
			final String locale) {
		List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType,workflowName,"COMMITTEE", locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = 
			this.getWorkflowActor(wfConfig, userGroupType, assigneeLevel);
		
		if(status.getType().equals(
				ApplicationConstants.PRASHNAVALI_RECOMMEND_SENDBACK)) {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActors = getWorkflowActorsExcludingCurrent(wfConfig, currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActors;
	}

	public WorkflowActor findNextPrashnavaliActor(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName,
			final Integer assigneeLevel, 
			final String locale) {
		WorkflowActor wfActor = null;
		
		WorkflowConfig wfConfig = 
			this.getLatest(houseType, workflowName, locale);
		UserGroupType userGroupType = userGroup.getUserGroupType();
		WorkflowActor currentWfActor = 
			this.getWorkflowActor(wfConfig, userGroupType, assigneeLevel);
		
		if(status.getType().equals(
				ApplicationConstants.PRASHNAVALI_RECOMMEND_SENDBACK)) {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.DESC);
		}
		else {
			wfActor = getNextWorkflowActor(wfConfig, 
					currentWfActor, ApplicationConstants.ASC);
		}
		
		return wfActor;
	}
	/****************************** Prashnavali 
	 * @throws ELSException ****************************/

	public List<Reference> findRulesSuspensionMotionActors(RulesSuspensionMotion motion, Status internalStatus,
			UserGroup userGroup, int level, String locale) throws ELSException {
		String status=internalStatus.getType();
		WorkflowConfig workflowConfig=null;
		UserGroupType userGroupType=null;
		WorkflowActor currentWorkflowActor=null;
		List<Reference> references=new ArrayList<Reference>();
		List<WorkflowActor> allEligibleActors=new ArrayList<WorkflowActor>();
		Date ruleSuspensionDate = motion.getRuleSuspensionDate();
		Group group = Group.findByAnsweringDateInHouseType(ruleSuspensionDate, motion.getHouseType());
		Ministry ministry = group.getMinistries().get(0);
		SubDepartment subdepartment = group.getSubdepartments().get(0);
		/**** Note :Here this can be configured so that list of workflows which
			 * goes back is read  dynamically ****/
		if(status.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_SENDBACK)				
				||status.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_DISCUSS)){
			workflowConfig=getLatest(motion,motion.getInternalStatus().getType(),locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.DESC);
		} else{
			workflowConfig=getLatest(motion,status,locale.toString());
			userGroupType=userGroup.getUserGroupType();
			currentWorkflowActor=getWorkflowActor(workflowConfig,userGroupType,level);
			allEligibleActors=getWorkflowActorsExcludingCurrent(workflowConfig,currentWorkflowActor,ApplicationConstants.ASC);
		}
		HouseType houseType=motion.getHouseType();
		DeviceType deviceType=motion.getType();
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subdepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subdepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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

	public Reference findActorVOAtFirstLevel(RulesSuspensionMotion rulesSuspensionMotion, Workflow processWorkflow,
			String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowActor workflowActorAtFirstLevel = findFirstActor(rulesSuspensionMotion, processWorkflow, locale);
		actorAtGivenLevel = findActorDetails(rulesSuspensionMotion, workflowActorAtFirstLevel, locale);
		return actorAtGivenLevel;	
	}

	private Reference findActorDetails(RulesSuspensionMotion rulesSuspensionMotion,
			WorkflowActor workflowActor, String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		HouseType houseType = rulesSuspensionMotion.getHouseType();
		DeviceType deviceType = rulesSuspensionMotion.getType();
		Date ruleSuspensionDate = rulesSuspensionMotion.getRuleSuspensionDate();
		Group group = Group.findByAnsweringDateInHouseType(ruleSuspensionDate, rulesSuspensionMotion.getHouseType());
		Ministry ministry = group.getMinistries().get(0);
		SubDepartment subdepartment = group.getSubdepartments().get(0);
		UserGroupType userGroupTypeTemp = workflowActor.getUserGroupType();
		if(userGroupTypeTemp.getType().equals(ApplicationConstants.MEMBER)){
			try {
				User user = User.find(rulesSuspensionMotion.getPrimaryMember());
				actorAtGivenLevel = new Reference();
				actorAtGivenLevel.setId(user.getCredential().getUsername()
						+"#"+userGroupTypeTemp.getType()
						+"#"+workflowActor.getLevel()
						+"#"+userGroupTypeTemp.getName()
						+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
				actorAtGivenLevel.setName(userGroupTypeTemp.getName());	
				return actorAtGivenLevel;
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}else{
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j : userGroups){
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String,String> params = j.getParameters();
				if(houseType != null){
					HouseType bothHouse = HouseType.
							findByFieldName(HouseType.class, "type","bothhouse", locale);
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
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(ministry.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				if(subdepartment!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(subdepartment.getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
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
					User user=User.findByFieldName(User.class,"credential",j.getCredential(), locale);
					actorAtGivenLevel = new Reference();
					actorAtGivenLevel.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+workflowActor.getLevel()
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					actorAtGivenLevel.setName(userGroupTypeTemp.getName());		
					return actorAtGivenLevel;
				}				
			}
		}
		return actorAtGivenLevel;
	}

	private WorkflowActor findFirstActor(RulesSuspensionMotion rulesSuspensionMotion, Workflow processWorkflow,
			String locale) {
		WorkflowConfig latestWorkflowConfig = getLatest(rulesSuspensionMotion, processWorkflow, locale);
		String query = "SELECT wa" +
				" FROM WorkflowConfig wc join wc.workflowactors wa" +
				" WHERE wc.id=:wcid" +
				" AND wa.level=1" +				
				" ORDER BY wa.id DESC";
		TypedQuery<WorkflowActor> tQuery = 
			this.em().createQuery(query, WorkflowActor.class);
		tQuery.setParameter("wcid", latestWorkflowConfig.getId());		
		tQuery.setMaxResults(1);
		WorkflowActor firstActor = tQuery.getSingleResult();
		return firstActor;	
	}
	
	
	public WorkflowConfig getLatest(RulesSuspensionMotion rulesSuspensionMotion, String internalStatus,
			String locale) {
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
		query.setParameter("deviceTypeId", rulesSuspensionMotion.getType().getId());
		query.setParameter("workflowName",workflowName);
		query.setParameter("houseTypeId", rulesSuspensionMotion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}
	}
	

	private WorkflowConfig getLatest(RulesSuspensionMotion rulesSuspensionMotion, Workflow processWorkflow,
			String locale) {
		/**** Latest Workflow Configurations ****/
		String strQuery="SELECT wc FROM WorkflowConfig wc" +
				" JOIN wc.workflow wf" +
				" JOIN wc.deviceType d " +
				" JOIN wc.houseType ht" +
				" WHERE d.id=:deviceTypeId" +
				" AND wf.type=:workflowName" +
				" AND ht.id=:houseTypeId"+
				" AND wc.isLocked=true ORDER BY wc.id "+ApplicationConstants.DESC ;				
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("deviceTypeId", rulesSuspensionMotion.getType().getId());
		query.setParameter("workflowName",processWorkflow.getType());
		query.setParameter("houseTypeId", rulesSuspensionMotion.getHouseType().getId());
		try{
			return (WorkflowConfig) query.getResultList().get(0);
		}catch(Exception ex){
			ex.printStackTrace();
			return new WorkflowConfig();
		}
	}

	public Reference findActorVOAtGivenLevel(RulesSuspensionMotion rulesSuspensionMotion, Workflow processWorkflow,
			UserGroupType userGroupType, int level, String locale) throws ELSException {
		Reference actorAtGivenLevel = null;
		WorkflowConfig workflowConfig = getLatest(rulesSuspensionMotion, processWorkflow, locale);
		WorkflowActor workflowActorAtGivenLevel = getWorkflowActor(workflowConfig,userGroupType,level);
		actorAtGivenLevel = findActorDetails(rulesSuspensionMotion, workflowActorAtGivenLevel, locale);
		return actorAtGivenLevel;	
	}
	
}
