/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.WorkflowActor.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowActor.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="workflowactors")
@JsonIgnoreProperties({"userGroup"})
public class WorkflowActor extends BaseDomain implements Serializable,Comparable<WorkflowActor>{

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The user group type. */
	@ManyToOne(fetch=FetchType.LAZY)
    private UserGroupType userGroupType;

    /** The level. */
    private Integer level;
    
    /**
     * Instantiates a new workflow actor.
     */
    public WorkflowActor() {
        super();
    }

    /**
     * Instantiates a new workflow actor.
     *
     * @param locale the locale
     */
    public WorkflowActor(final String locale) {
        super(locale);
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
     * Gets the level.
     *
     * @return the level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(final Integer level) {
        this.level = level;
    }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final WorkflowActor o) {
		int result=this.level-o.level;
		return result;
	}

}
