/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.WorkflowActor.java
 * Created On: Sep 17, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowActor.
 *
 * @author Sandeep
 * @since v1.0.0
 */
@Entity
@Table(name="workflowactors")
public class WorkflowActor extends BaseDomain{

    /** The user group. */
    @ManyToOne(fetch=FetchType.LAZY)
    private UserGroup userGroup;

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
     * @param userGroup the user group
     * @param level the level
     */
    public WorkflowActor(final UserGroup userGroup, final Integer level) {
        super();
        this.userGroup = userGroup;
        this.level = level;
    }


    /**
     * Gets the user group.
     *
     * @return the user group
     */
    public UserGroup getUserGroup() {
        return userGroup;
    }


    /**
     * Sets the user group.
     *
     * @param userGroup the new user group
     */
    public void setUserGroup(final UserGroup userGroup) {
        this.userGroup = userGroup;
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


}
