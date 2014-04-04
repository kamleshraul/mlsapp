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
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
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
    
    @Autowired
    private transient UserGroupRepository userGroupRepository;

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
    
    public static UserGroupRepository getUserGroupRepository() {
    	UserGroupRepository userGroupRepository = new UserGroup().userGroupRepository;
        if (userGroupRepository == null) {
            throw new IllegalStateException(
                    "UserGroupRepository has not been injected in UserGroup Domain");
        }
        return userGroupRepository;
    }

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
	public static Reference findMotionActor(final Motion motion,final String userGroupType,final String level,final String locale) throws ELSException {
		return getUserGroupRepository().findMotionActor(motion,userGroupType,level,locale);
	} 
	
	public static UserGroup findUserGroup(String houseType, String userGroupType, String deviceType, String ministry, String subDepartment) throws ELSException {
		return getUserGroupRepository().findUserGroup(houseType, userGroupType, deviceType, ministry, subDepartment);
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
	public static Map<String, String> findParametersByUserGroup(UserGroup userGroup) {
		return getUserGroupRepository().findParametersByUserGroup(userGroup);
	}
}
