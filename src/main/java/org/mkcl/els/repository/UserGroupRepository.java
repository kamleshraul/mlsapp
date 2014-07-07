package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Repository;

@Repository
public class UserGroupRepository extends BaseRepository<UserGroup, Serializable>{

	public Reference findMotionActor(final Motion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(motion.getHouseType()!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(motion.getHouseType().getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(motion.getType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(motion.getType().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
				if(motion.getMinistry()!=null){
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(motion.getMinistry().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}			
				if(motion.getSubDepartment()!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(motion.getSubDepartment().getName())){
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
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+level
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
				}				
			}
			return reference;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserGroupRepository_Reference_findMotionActor", "No Actor Found");
			throw elsException;
		}
	}
	
	public Reference findCutMotionActor(final CutMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(motion.getHouseType()!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(motion.getHouseType().getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(motion.getDeviceType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(motion.getDeviceType().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
				if(motion.getMinistry()!=null){
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(motion.getMinistry().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}			
				if(motion.getSubDepartment()!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(motion.getSubDepartment().getName())){
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
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+level
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
				}				
			}
			return reference;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserGroupRepository_Reference_findMotionActor", "No Actor Found");
			throw elsException;
		}
	}

	public UserGroup findUserGroup(final String houseType,final String userGroupType,final String deviceType,final String ministry,final String subDepartment) throws ELSException {
		Date currentDate=new Date();  
		String queryString = "SELECT u FROM UserGroup u JOIN u.userGroupType ugt WHERE " +
				"ugt.type=:userGroupType AND u.parameters['HOUSETYPE_mr_IN'] LIKE:houseType"+
				" AND u.parameters['DEVICETYPE_mr_IN'] LIKE:deviceType"+
				" AND u.parameters['MINISTRY_mr_IN'] LIKE:ministry"+
				" AND u.parameters['SUBDEPARTMENT_mr_IN'] LIKE:subDepartment"+
				" AND (u.activeTo>=:currentDate OR u.activeTo IS NULL)";

		Query query = this.em().createQuery(queryString);
		query.setParameter("userGroupType",userGroupType);
		query.setParameter("houseType","%"+houseType+"%");
		query.setParameter("deviceType", "%"+deviceType+"%");
		query.setParameter("ministry", "%"+ministry+"%");
		query.setParameter("subDepartment", "%"+subDepartment+"%");
		query.setParameter("currentDate",currentDate);
		try {
			UserGroup userGroup= (UserGroup) query.getSingleResult();
			return userGroup;
		}catch(EntityNotFoundException ex){
			logger.error(ex.getMessage());
			return null;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserGroupRepository_UserGroup_findUserGroup", "No UserGroup Found");
			throw elsException;
		}
	}

	public Reference findResolutionActor(final Resolution resolution,final String workflowHouseType,
			final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			HouseType houseType = null;
			if(resolution.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
				houseType = HouseType.findByFieldName(HouseType.class, "name", workflowHouseType, locale);
			} else {
				houseType=resolution.getHouseType();
			}
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(resolution.getHouseType()!=null){
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
				if(resolution.getType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(resolution.getType().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
				if(resolution.getMinistry()!=null){
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(resolution.getMinistry().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}			
				if(resolution.getSubDepartment()!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(resolution.getSubDepartment().getName())){
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
					reference.setId(j.getCredential().getUsername()
							+"#"+j.getUserGroupType().getType()
							+"#"+level
							+"#"+userGroupTypeTemp.getName()
							+"#"+user.getTitle()+" "+user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
					reference.setName(userGroupTypeTemp.getName());
				}				
			}
			return reference;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserGroupRepository_Reference_findMotionActor", "No Actor Found");
			throw elsException;
		}
	}

	public Reference findQuestionActor(final Question question, 
			final String actor,
			final String level, 
			final String locale) throws ELSException {
		try{
			Reference reference = new Reference();

			UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, 
					"type", actor, locale);
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class, "userGroupType",
					userGroupType, "activeFrom", ApplicationConstants.DESC, locale);
			for(UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();

				if(question.getHouseType() != null) {
					HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type", 
							"bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null &&! params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(
							bothHouse.getName())) {
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(
								question.getHouseType().getName())) {
							noOfComparisons++;
							noOfSuccess++;
						}
						else {
							noOfComparisons++;
						}
					}
				}

				if(question.getType() != null) {
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale).contains(
							question.getType().getName())) {
						noOfComparisons++;
						noOfSuccess++;
					}
					else {
						noOfComparisons++;
					}
				}

				if(question.getMinistry() != null) {
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale).contains(
							question.getMinistry().getName())) {
						noOfComparisons++;
						noOfSuccess++;
					}
					else {
						noOfComparisons++;
					}
				}

				if(question.getSubDepartment() != null) {
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale).contains(
							question.getSubDepartment().getName())) {
						noOfComparisons++;
						noOfSuccess++;
					}
					else {
						noOfComparisons++;
					}
				}

				Date fromDate = j.getActiveFrom();
				Date toDate = j.getActiveTo();
				Date currentDate = new Date();

				noOfComparisons++;
				if(((fromDate == null || currentDate.after(fromDate) || currentDate.equals(fromDate)) &&
						(toDate == null || currentDate.before(toDate) || currentDate.equals(toDate)))) {
					noOfSuccess++;
				}

				/**** Include Leave Module ****/
				if(noOfComparisons == noOfSuccess) {
					User user = User.findByFieldName(User.class, "credential", j.getCredential(), locale);
					reference.setId(j.getCredential().getUsername()
							+ "#" + j.getUserGroupType().getType()
							+ "#" + level
							+ "#" + userGroupType.getName()
							+ "#" + user.getTitle() + " " + user.getFirstName() + " "
							+ user.getMiddleName() + " " +user.getLastName());
					reference.setName(userGroupType.getName());
				}
			}
			return reference;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserGroupRepository_Reference_findMotionActor", "No Actor Found");
			throw elsException;
		}

	}

	public Map<String, String> findParametersByUserGroup(UserGroup userGroup) {
		String strQuery="SELECT u FROM UserGroup u WHERE u.id=:userGroupId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("userGroupId", userGroup.getId());
		UserGroup ug1=  (UserGroup) query.getSingleResult();
		return ug1.getParameters();
	}
}
