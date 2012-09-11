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
@Table(name="user_group")
@JsonIgnoreProperties({"credentials","parameters"})
public class UserGroup extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2415572645448037836L;

	/** The name. */
	@Column(length=1000)
	private String name;

	/** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value")
    @CollectionTable(name="usergroup_parameters")
	private Map<String,String> parameters;

    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "credential_id", referencedColumnName = "id")
    private Credential credential;


    public UserGroup() {
        super();
    }


    public UserGroup(final String name, final Map<String, String> parameters,
            final Credential credential) {
        super();
        this.name = name;
        this.parameters = parameters;
        this.credential = credential;
    }


    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
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


}
