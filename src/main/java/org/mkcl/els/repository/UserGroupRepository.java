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
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.AppropriationBillMotion;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CutMotion;
import org.mkcl.els.domain.CutMotionDate;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.DiscussionMotion;
import org.mkcl.els.domain.EventMotion;
import org.mkcl.els.domain.GovernorSpeechNotice;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.RulesSuspensionMotion;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
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
	
	public Reference findCutMotionDateActor(final CutMotionDate cutMotionDate,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(cutMotionDate.getHouseType()!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(cutMotionDate.getHouseType().getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(cutMotionDate.getDeviceType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(cutMotionDate.getDeviceType().getName())){
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
	
	public Reference findAdjournmentMotionActor(final AdjournmentMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
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
	
	public Reference findSpecialMentionNoticeActor(final SpecialMentionNotice notice,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(notice.getHouseType()!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(notice.getHouseType().getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(notice.getType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(notice.getType().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
				if(notice.getMinistry()!=null){
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(notice.getMinistry().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}			
				if(notice.getSubDepartment()!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(notice.getSubDepartment().getName())){
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
	
	public Reference findGovernorSpeechNoticeActor(final GovernorSpeechNotice notice,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(notice.getHouseType()!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(notice.getHouseType().getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(notice.getType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(notice.getType().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
//				if(notice.getMinistry()!=null){
//					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(notice.getMinistry().getName())){
//						noOfComparisons++;
//						noOfSuccess++;
//					}else{
//						noOfComparisons++;
//					}
//				}			
//				if(notice.getSubDepartment()!=null){
//					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(notice.getSubDepartment().getName())){
//						noOfComparisons++;
//						noOfSuccess++;
//					}else{
//						noOfComparisons++;
//					}
//				}	
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

	public Reference findEventMotionActor(final EventMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
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
	
	public UserGroup findUserGroup(final String houseType,final String userGroupType,final String deviceType,final String ministry,final String subDepartment,final String locale) throws ELSException {
		Date currentDate=new Date();
		String queryString = "SELECT u FROM UserGroup u JOIN u.userGroupType ugt WHERE " +
				"ugt.type=:userGroupType AND u.parameters['HOUSETYPE_"+locale+"'] LIKE:houseType"+
				" AND u.parameters['DEVICETYPE_"+locale+"'] LIKE:deviceType"+
				" AND u.parameters['MINISTRY_"+locale+"'] LIKE:ministry"+
				" AND u.parameters['SUBDEPARTMENT_"+locale+"'] LIKE:subDepartment"+
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
	
	public List<UserGroup> findActiveUserGroupsOfGivenUser(final String userName,final String houseType,final String deviceType,final String locale) throws ELSException {
		Date currentDate=new Date();  
		String queryString = "SELECT u FROM UserGroup u JOIN u.credential cr" +
				" WHERE cr.username=:userName "+
				" AND (u.parameters['HOUSETYPE_"+locale+"'] LIKE:houseType OR u.parameters['HOUSETYPE_"+locale+"'] LIKE '%दोन्ही सभागृह%')"+
				" AND u.parameters['DEVICETYPE_"+locale+"'] LIKE:deviceType"+
				" AND (u.activeTo>=:currentDate OR u.activeTo IS NULL)";

		Query query = this.em().createQuery(queryString);
		query.setParameter("userName",userName);
		query.setParameter("houseType","%"+houseType+"%");
		query.setParameter("deviceType", "%"+deviceType+"%");
		query.setParameter("currentDate",currentDate);
		try {
			@SuppressWarnings("unchecked")
			List<UserGroup> userGroups= query.getResultList();
			return userGroups;
		}catch(EntityNotFoundException ex){
			logger.error(ex.getMessage());
			return null;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException=new ELSException();
			elsException.setParameter("UserGroupRepository_UserGroup_findActiveUserGroupsForDeviceType", "No UserGroup Found");
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
							+"#"+userGroupTypeTemp.getDisplayName()
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

				if(question.getMinistry()!=null){							
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).isEmpty()){
						String[] allowedMinistries = params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedMinistries.length; k++) {
							if(allowedMinistries[k].equals(question.getMinistry().getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
						noOfComparisons++;
					}
				}
				
				if(question.getSubDepartment()!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).isEmpty()){
						String[] allowedSubdepartments = params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).split("##");
						for(int k=0; k<allowedSubdepartments.length; k++) {
							if(allowedSubdepartments[k].equals(question.getSubDepartment().getName())) {										
								noOfSuccess++;
								break;
							}
						}
						noOfComparisons++;
					}else{
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
							+ "#" + userGroupType.getDisplayName()
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
	
	public Reference findStandaloneMotionActor(final StandaloneMotion question, 
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
	
	public Reference findDiscussionMotionActor(final DiscussionMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			
			List<Ministry> domainMinistries = motion.getMinistries();
			List<SubDepartment> domainSubDepartments = motion.getSubDepartments();
			
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
				
				
				if(domainMinistries != null) {
					if (params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale) != null
							&& WorkflowConfig.containsGivenData(domainMinistries, 
									params.get(ApplicationConstants.MINISTRY_KEY + "_" + locale))) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
						noOfComparisons++;
					}
				}
				if(domainSubDepartments != null) {
					
					if (params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale) != null
							&& WorkflowConfig.containsGivenData(domainSubDepartments, 
									params.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_" + locale))) {
						noOfComparisons++;
						noOfSuccess++;
					} else {
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
	
	public Reference findProprietyPointActor(final ProprietyPoint proprietyPoint,final String userGroupType,final String level,final String locale) throws ELSException {
		try{
			Reference reference=new Reference();
			UserGroupType userGroupTypeTemp=UserGroupType.findByFieldName(UserGroupType.class,"type",userGroupType, locale);
			List<UserGroup> userGroups=UserGroup.findAllByFieldName(UserGroup.class,"userGroupType",
					userGroupTypeTemp, "activeFrom",ApplicationConstants.DESC, locale);
			for(UserGroup j:userGroups){
				int noOfComparisons=0;
				int noOfSuccess=0;
				Map<String,String> params=j.getParameters();
				if(proprietyPoint.getHouseType()!=null){
					HouseType bothHouse=HouseType.findByFieldName(HouseType.class, "type","bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && !params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(bothHouse.getName())){
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(proprietyPoint.getHouseType().getName())){
							noOfComparisons++;
							noOfSuccess++;
						}else{
							noOfComparisons++;
						}
					}
				}
				if(proprietyPoint.getDeviceType()!=null){
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale).contains(proprietyPoint.getDeviceType().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}
				if(proprietyPoint.getMinistry()!=null){
					if(params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale)!=null && params.get(ApplicationConstants.MINISTRY_KEY+"_"+locale).contains(proprietyPoint.getMinistry().getName())){
						noOfComparisons++;
						noOfSuccess++;
					}else{
						noOfComparisons++;
					}
				}			
				if(proprietyPoint.getSubDepartment()!=null){
					if(params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale)!=null && params.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale).contains(proprietyPoint.getSubDepartment().getName())){
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
			elsException.setParameter("UserGroupRepository_Reference_findProprietyPointActor", "No Actor Found");
			throw elsException;
		}
	}
	
	
	public Reference findRulesSuspensionMotionActor(RulesSuspensionMotion motion, String actor, String level,
			String locale) throws ELSException {
		try{
			Reference reference = new Reference();

			UserGroupType userGroupType = UserGroupType.findByFieldName(UserGroupType.class, 
					"type", actor, locale);
			List<UserGroup> userGroups = UserGroup.findAllByFieldName(UserGroup.class, "userGroupType",
					userGroupType, "activeFrom", ApplicationConstants.DESC, locale);
			Date rulesSuspensionDate = motion.getRuleSuspensionDate();
			Group group = Group.findByAnsweringDateInHouseType(rulesSuspensionDate, motion.getHouseType());
			Ministry ministry = group.getMinistries().get(0);
			SubDepartment subdepartment = group.getSubdepartments().get(0);
			for(UserGroup j : userGroups) {
				int noOfComparisons = 0;
				int noOfSuccess = 0;
				Map<String, String> params = j.getParameters();

				if(motion.getHouseType() != null) {
					HouseType bothHouse = HouseType.findByFieldName(HouseType.class, "type", 
							"bothhouse", locale);
					if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null &&! params.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale).contains(
							bothHouse.getName())) {
						if(params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.HOUSETYPE_KEY+"_"+locale).contains(
								motion.getHouseType().getName())) {
							noOfComparisons++;
							noOfSuccess++;
						}
						else {
							noOfComparisons++;
						}
					}
				}

				if(motion.getType() != null) {
					if(params.get(ApplicationConstants.DEVICETYPE_KEY+"_"+locale)!=null && params.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale).contains(
							motion.getType().getName())) {
						noOfComparisons++;
						noOfSuccess++;
					}
					else {
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
							+ "#" + userGroupType.getDisplayName()
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
	
	public Reference findAppropriationBillMotionActor(final AppropriationBillMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
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

	public Map<String, String> findParametersByUserGroup(UserGroup userGroup) {
		String strQuery="SELECT u FROM UserGroup u WHERE u.id=:userGroupId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("userGroupId", userGroup.getId());
		UserGroup ug1=  (UserGroup) query.getSingleResult();
		return ug1.getParameters();
	}

	public UserGroup findActive(final Credential credential,
			final UserGroupType usergroupType,
			final Date onDate, String locale) {
		String strQuery = "SELECT ug FROM UserGroup ug " +
				" WHERE ug.userGroupType.id=:usergroupTypeId "+
				" AND ug.activeFrom<=:onDate"+
				" AND ug.activeTo>=:onDate"+
				" AND ug.credential.id=:credentialId"+ 
				" ORDER BY ug.id DESC";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("usergroupTypeId", usergroupType.getId());
		query.setParameter("onDate", onDate);
		query.setParameter("credentialId", credential.getId());
		List<UserGroup> userGroups = query.getResultList();
		if(userGroups.size()>0){
			return userGroups.get(0);
		}else{
			return null;
		}
	}

	public List<UserGroup> findAllActive(final Credential credential,
			final UserGroupType usergroupType,
			final Date onDate, String locale) {
		String strQuery = "SELECT ug FROM UserGroup ug " +
				" WHERE ug.userGroupType.id=:usergroupTypeId "+
				" AND ug.activeFrom<=:onDate"+
				" AND ug.activeTo>=:onDate"+
				" AND ug.credential.id=:credentialId"+ 
				" ORDER BY ug.id DESC";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("usergroupTypeId", usergroupType.getId());
		query.setParameter("onDate", onDate);
		query.setParameter("credentialId", credential.getId());
		List<UserGroup> userGroups = query.getResultList();
		return userGroups;
	}
	
	public UserGroup findActive(final String userGroupType,
			final Date onDate, String locale) {
		String strQuery = "SELECT ug FROM UserGroup ug" +
				" WHERE ug.userGroupType.type=:userGroupType"+
				" AND ug.activeFrom<=:onDate"+
				" AND ug.activeTo>=:onDate";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("userGroupType", userGroupType);
		query.setParameter("onDate", onDate);
		List<UserGroup> userGroups = query.getResultList();
		if(userGroups.size()>0){
			return userGroups.get(0);
		}else{
			return null;
		}
	}
	
	public UserGroup findActive(final String userGroupType, final DeviceType deviceTypeEnabledForUserGroup,
			final Date onDate, String locale) {
		String strQuery = "SELECT ug FROM UserGroup ug" +
				" WHERE ug.userGroupType.type=:userGroupType"+
				" AND ug.activeFrom<=:onDate"+
				" AND ug.activeTo>=:onDate"+
				" AND ug.parameters[:deviceTypeParameterKey] LIKE :deviceTypeEnabledForUserGroup";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("userGroupType", userGroupType);
		query.setParameter("onDate", onDate);
		query.setParameter("deviceTypeParameterKey", "DEVICETYPE_"+locale);
		query.setParameter("deviceTypeEnabledForUserGroup", "%"+deviceTypeEnabledForUserGroup.getName()+"%");
		List<UserGroup> userGroups = query.getResultList();
		if(userGroups.size()>0){
			return userGroups.get(0);
		}else{
			return null;
		}
	}

	public UserGroup findActive(final Credential credential,
			final Date onDate, String locale) {
		String strQuery = "SELECT ug FROM UserGroup ug " +
				" WHERE ug.activeFrom<=:onDate"+
				" AND ug.activeTo>=:onDate"+
				" AND ug.credential.id=:credentialId";
		Query query = this.em().createQuery(strQuery);
		query.setParameter("onDate", onDate);
		query.setParameter("credentialId", credential.getId());
		List<UserGroup> userGroups = query.getResultList();
		if(userGroups.size()>0){
			return userGroups.get(0);
		}else{
			return null;
		}
	}
}
