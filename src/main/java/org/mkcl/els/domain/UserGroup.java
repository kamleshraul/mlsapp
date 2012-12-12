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
}
