/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.WorkflowConfig.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.repository.WorkflowConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * Even though the class extends BaseDomain, all the objects of the class
 * (and thus the corresponding entries in the database table) must be locale
 * insensitive.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="wf_config")
@JsonIgnoreProperties({"session", "workflowactors","deviceType"})
public class WorkflowConfig extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -527613490139348330L;

	/**
	 * If a Device class is added in the future, change the type to
	 * Device.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;

	/** The workflow. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="workflow_id")
	private Workflow workflow;


	/** The device type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;

	/** The workflowactors. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="wfconfig_wfactors",
			joinColumns={@JoinColumn(name="wfconfig_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="wfactors_id", referencedColumnName="id")})
	private List<WorkflowActor> workflowactors;

	/** The created on. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	/** The workflow config repository. */
	@Autowired
    private transient WorkflowConfigRepository workflowConfigRepository;

	//=============== Constructor(s) ===============
	/**
	 * Instantiates a new workflow config.
	 */
	public WorkflowConfig() {
		super();
	}

	/**
	 * Instantiates a new workflow config.
	 *
	 * @param session the session
	 * @param workflow the workflow
	 * @param workflowactors the workflowactors
	 * @param createdOn the created on
	 */
	public WorkflowConfig(final Session session, final Workflow workflow,
            final List<WorkflowActor> workflowactors, final Date createdOn) {
        super();
        this.session = session;
        this.workflow = workflow;
        this.workflowactors = workflowactors;
        this.createdOn = createdOn;
    }

	/**
	 * Gets the workflow config repository.
	 *
	 * @return the workflow config repository
	 */
	public static WorkflowConfigRepository getWorkflowConfigRepository() {
		WorkflowConfigRepository workflowConfigRepository = new WorkflowConfig().workflowConfigRepository;
        if (workflowConfigRepository == null) {
            throw new IllegalStateException(
                    "WorkflowConfigRepository has not been injected in WorkflowConfig Domain");
        }
        return workflowConfigRepository;
    }

	/**
	 * Find actors.
	 *
	 * @param sessionId the session id
	 * @param deviceTypeId the device type id
	 * @param workflowType the workflow type
	 * @param groupNumber the group number
	 * @param workflowConfigId the workflow config id
	 * @param level the level
	 * @param sortorder the sortorder
	 * @return the list
	 */
	public static List<Reference> findActors(final Long sessionId,
			final Long deviceTypeId,
			final String workflowType,
			final Integer groupNumber,
			final Long workflowConfigId,
			final Integer level,
			final String sortorder
			){
		return getWorkflowConfigRepository().findActors(sessionId,
				deviceTypeId,
				workflowType,
				groupNumber,
				workflowConfigId,
				level,
				sortorder);
	}
    //=============== Getter & Setters ===============
    /**
     * Gets the session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets the session.
     *
     * @param session the new session
     */
    public void setSession(final Session session) {
        this.session = session;
    }

    /**
     * Gets the workflow.
     *
     * @return the workflow
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * Sets the workflow.
     *
     * @param workflow the new workflow
     */
    public void setWorkflow(final Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * Gets the workflowactors.
     *
     * @return the workflowactors
     */
    public List<WorkflowActor> getWorkflowactors() {
        return workflowactors;
    }

    /**
     * Sets the workflowactors.
     *
     * @param workflowactors the new workflowactors
     */
    public void setWorkflowactors(final List<WorkflowActor> workflowactors) {
        this.workflowactors = workflowactors;
    }

    /**
     * Gets the created on.
     *
     * @return the created on
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the created on.
     *
     * @param createdOn the new created on
     */
    public void setCreatedOn(final Date createdOn) {
        this.createdOn = createdOn;
    }

	/**
	 * Gets the device type.
	 *
	 * @return the device type
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * Sets the device type.
	 *
	 * @param deviceType the new device type
	 */
	public void setDeviceType(final DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * Find latest.
	 *
	 * @param sessionId the session id
	 * @param deviceTypeId the device type id
	 * @param workflowType the workflow type
	 * @return the workflow config
	 */
	public static WorkflowConfig findLatest(final Long sessionId,
			final Long deviceTypeId,final  String workflowType) {
		return getWorkflowConfigRepository().findLatest(sessionId,
				deviceTypeId,workflowType);
	}

	/**
	 * Removes the actor.
	 *
	 * @param workflowconfigId the workflowconfig id
	 * @param workflowactorId the workflowactor id
	 */
	public static void removeActor(final Long workflowconfigId,final Long workflowactorId) {
		getWorkflowConfigRepository().removeActor(workflowconfigId,workflowactorId);
	}

	/**
	 * Gets the level.
	 *
	 * @param workflowconfigId the workflowconfig id
	 * @param actor the actor
	 * @return the level
	 */
	public static Integer getLevel(final Long workflowconfigId,final String actor){
		return getWorkflowConfigRepository().getLevel(workflowconfigId,actor);
	}
}
