/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.UserGroup.java
 * Created On: Aug 28, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


// TODO: Auto-generated Javadoc
/**
 * The Class UserGroup.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="usergroups")
@JsonIgnoreProperties({"credential","parameters"})
public class UserGroup extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------------------
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2415572645448037836L;

	/** The name. */
	@ManyToOne(fetch=FetchType.EAGER)
	private UserGroupType userGroupType;

	/** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="usergroups_parameters")
	private Map<String,String> parameters;

    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "credential_id", referencedColumnName = "id")
    private Credential credential;

    @Temporal(TemporalType.DATE)
    private Date activeFrom;
    
    @Temporal(TemporalType.DATE)
    private Date activeTo;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
    /** The drafts. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="usergroup_id", referencedColumnName="id")
    private List<UserGroupDraft> drafts;
    
    @Autowired
    private transient UserGroupRepository userGroupRepository;
    
    @Column(length=300)
	private String firstName="";
    
    @Column(length=300)
	private String middleName="";
    
    @Column(length=300)
	private String lastName="";
    

    // --------------------------------Constructors----------------------------------------------
    public UserGroup() {
        super();
    }
    
    public UserGroup(final UserGroupType userGroupType,
            final Map<String, String> parameters, final Credential credential,
            final Date activeFrom) {
        super();
        this.userGroupType = userGroupType;
        this.parameters = parameters;
        this.credential = credential;
        this.activeFrom = activeFrom;
    }
    
    
    // -------------------------------Domain_Methods----------------------------------------------    
    public static UserGroupRepository getUserGroupRepository() {
    	UserGroupRepository userGroupRepository = new UserGroup().userGroupRepository;
        if (userGroupRepository == null) {
            throw new IllegalStateException(
                    "UserGroupRepository has not been injected in UserGroup Domain");
        }
        return userGroupRepository;
    }
    
    public static Reference findMotionActor(final Motion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findMotionActor(motion,userGroupType,level,locale);
	}
	
	public static Reference findCutMotionDateActor(final CutMotionDate cutMotionDate,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findCutMotionDateActor(cutMotionDate, userGroupType, level, locale);
	}
	
	public static Reference findCutMotionActor(final CutMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findCutMotionActor(motion, userGroupType, level, locale);
	} 
	
	public static Reference findAdjournmentMotionActor(final AdjournmentMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findAdjournmentMotionActor(motion, userGroupType, level, locale);
	}
	
	public static Reference findSpecialMentionNoticeActor(final SpecialMentionNotice notice,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findSpecialMentionNoticeActor(notice, userGroupType, level, locale);
	}
	
	public static Reference findEventMotionActor(final EventMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findEventMotionActor(motion, userGroupType, level, locale);
	} 
	
	public static Reference findDiscussionMotionActor(final DiscussionMotion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findDiscussionMotionActor(motion, userGroupType, level, locale);
	}
	
	public static UserGroup findUserGroup(String houseType, String userGroupType, String deviceType, String ministry, String subDepartment, String locale) throws ELSException {
		return getUserGroupRepository().findUserGroup(houseType, userGroupType, deviceType, ministry, subDepartment, locale);
	}
	
	public static List<UserGroup> findActiveUserGroupsOfGivenUser(final String userName,final String houseType,final String deviceType,final String locale) throws ELSException {
		return getUserGroupRepository().findActiveUserGroupsOfGivenUser(userName, houseType, deviceType, locale);
	}
	
	public static Reference findResolutionActor(Resolution resolution,String workflowHouseType,
			String userGroupType, String level, String locale) throws ELSException {
		return getUserGroupRepository().findResolutionActor(resolution,workflowHouseType,userGroupType,level,locale);
	}
	
	public static Reference findQuestionActor(final Question question, 
			final String actor,
			final String level, 
			final String locale) throws ELSException {
		return getUserGroupRepository().findQuestionActor(question, actor, level, locale);
	}
	
	public static Reference findStandaloneMotionActor(final StandaloneMotion question, 
			final String actor,
			final String level, 
			final String locale) throws ELSException {
		return getUserGroupRepository().findStandaloneMotionActor(question, actor, level, locale);
	}
	
	public static Reference findProprietyPointActor(final ProprietyPoint motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findProprietyPointActor(motion, userGroupType, level, locale);
	}
	
	public static Reference findRulesSuspensionMotionActor(RulesSuspensionMotion motion, String userGroupType, String level,
			String locale) throws ELSException {
		return getUserGroupRepository().findRulesSuspensionMotionActor(motion, userGroupType, level, locale);
	}
	
	public static Map<String, String> findParametersByUserGroup(UserGroup userGroup) {
		return getUserGroupRepository().findParametersByUserGroup(userGroup);
	}
	
	public static UserGroup findActive(Credential credential,
			UserGroupType usergroupType, Date onDate, String locale) {
		return getUserGroupRepository().findActive(credential, usergroupType, onDate, locale);
	}
	
	public static UserGroup findActive(final String usergroupType, final Date onDate, final String locale) {
		return getUserGroupRepository().findActive(usergroupType, onDate, locale);
	}
	
	public static UserGroup findActive(Credential credential, Date onDate, String locale) {
		return getUserGroupRepository().findActive(credential, onDate, locale);
	}
	
	public static boolean isActiveInSession(final Session session,
			final UserGroup userGroup,
			final String locale) throws ELSException {
		boolean retVal = false;		
		
//		if(userGroup.getActiveFrom().before(session.getEndDate())
//				&& userGroup.getActiveTo().after(session.getEndDate())){
		if(userGroup.getActiveFrom().compareTo(session.getEndDate())<=0
				&& userGroup.getActiveTo().compareTo(session.getEndDate())>=0) {
			
			retVal = true;
			
		}else{
			
			retVal = false;
		}
		return retVal;
	}	
	
	@Override
    public UserGroup merge() {		
		if(this.getId()!=null && (this.getDrafts()==null || this.getDrafts().isEmpty())) {
			UserGroup dbUserGroup = UserGroup.findById(UserGroup.class, this.getId());
			this.setDrafts(dbUserGroup.getDrafts());
		}
		this.addUserGroupDraft();
		UserGroup userGroup = (UserGroup) super.merge();
        return userGroup;
	}
	
	/**
     * Adds the usergroup draft.
     */
    private void addUserGroupDraft() {
    	UserGroupDraft draft = new UserGroupDraft();
    	draft.setLocale(this.getLocale());
    	
    	draft.setEditedAs(this.getEditedAs());
        draft.setEditedBy(this.getEditedBy());
        draft.setEditedOn(this.getEditedOn());
        
        draft.setActiveFrom(this.getActiveFrom());
        draft.setActiveTo(this.getActiveTo());
        draft.setUserGroupType(this.getUserGroupType());
        draft.setParameters(this.getParameters());
        
        if(this.getId() != null) {
            UserGroup userGroup = UserGroup.findById(UserGroup.class, this.getId());
            List<UserGroupDraft> originalDrafts = userGroup.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<UserGroupDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<UserGroupDraft> originalDrafts = new ArrayList<UserGroupDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
	

	// -------------------------------Getters & Setters-------------------------------
    public UserGroupType getUserGroupType() {
        return userGroupType;
    }

    public void setUserGroupType(final UserGroupType userGroupType) {
        this.userGroupType = userGroupType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(final Credential credential) {
        this.credential = credential;
    }

    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(final Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public String getParameterValue(final String key){
        Map<String,String> params=this.getParameters();
        if(params!=null){
        if(params.containsKey(key)){
            return params.get(key);
        }else{
            return "";
        }
        }else{
            return "";
        }
    }
    
	public Date getActiveTo() {
		return activeTo;
	}
	
	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}	
	
	public Date getEditedOn() {
		return editedOn;
	}
	
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}
	
	public String getEditedBy() {
		return editedBy;
	}
	
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}
	
	public String getEditedAs() {
		return editedAs;
	}
	
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public List<UserGroupDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<UserGroupDraft> drafts) {
		this.drafts = drafts;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}	
	
}