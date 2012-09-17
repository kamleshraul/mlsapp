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
	@ManyToOne(fetch=FetchType.LAZY)
	private UserGroupType userGroupType;

	/** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="usergroups_parameters")
	private Map<String,String> parameters;

    /** The credential. */
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "credential_id", referencedColumnName = "id")
    private Credential credential;

    /** The active from. */
    @Temporal(TemporalType.DATE)
    private Date activeFrom;

    /**
     * Instantiates a new user group.
     */
    public UserGroup() {
        super();
    }
    
    /**
     * Instantiates a new user group.
     *
     * @param userGroupType the user group type
     * @param parameters the parameters
     * @param credential the credential
     * @param activeFrom the active from
     */
    public UserGroup(final UserGroupType userGroupType,
            final Map<String, String> parameters, final Credential credential,
            final Date activeFrom) {
        super();
        this.userGroupType = userGroupType;
        this.parameters = parameters;
        this.credential = credential;
        this.activeFrom = activeFrom;
    }

    /**
     * Gets the user group type.
     *
     * @return the user group type
     */
    public UserGroupType getUserGroupType() {
        return userGroupType;
    }

    /**
     * Sets the user group type.
     *
     * @param userGroupType the new user group type
     */
    public void setUserGroupType(final UserGroupType userGroupType) {
        this.userGroupType = userGroupType;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the parameters
     */
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the credential.
     *
     * @return the credential
     */
    public Credential getCredential() {
        return credential;
    }

    /**
     * Sets the credential.
     *
     * @param credential the new credential
     */
    public void setCredential(final Credential credential) {
        this.credential = credential;
    }

    /**
     * Gets the active from.
     *
     * @return the active from
     */
    public Date getActiveFrom() {
        return activeFrom;
    }

    /**
     * Sets the active from.
     *
     * @param activeFrom the new active from
     */
    public void setActiveFrom(final Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    /**
     * Gets the parameter value.
     *
     * @param key the key
     * @return the parameter value
     */
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
}
