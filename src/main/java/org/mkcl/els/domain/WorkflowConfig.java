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
@JsonIgnoreProperties({"workflowactors"})
public class WorkflowConfig extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -527613490139348330L;

	@ManyToOne(fetch=FetchType.LAZY)
	private HouseType houseType;
	
	/** The device type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;
	
	/** The workflow. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="workflow_id")
	private Workflow workflow;

	/** The workflowactors. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="wfconfig_wfactors",
			joinColumns={@JoinColumn(name="wfconfig_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="wfactors_id", referencedColumnName="id")})
	private List<WorkflowActor> workflowactors;

	/** The created on. */
	@Temporal(TemporalType.DATE)
	private Date createdOn;
	
	private Boolean isLocked=false;	

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
	
	public static WorkflowConfigRepository getWorkflowConfigRepository() {
		WorkflowConfigRepository workflowConfigRepository = new WorkflowConfig().workflowConfigRepository;
        if (workflowConfigRepository == null) {
            throw new IllegalStateException(
                    "WorkflowConfigRepository has not been injected in WorkflowConfig Domain");
        }
        return workflowConfigRepository;
    }

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public List<WorkflowActor> getWorkflowactors() {
		return workflowactors;
	}

	public void setWorkflowactors(List<WorkflowActor> workflowactors) {
		this.workflowactors = workflowactors;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public Boolean getIsLocked() {
		return isLocked;
	}
	
	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public static Boolean removeActor(Long workflowconfigId, Long workflowactorId) {
		return getWorkflowConfigRepository().removeActor(workflowconfigId, workflowactorId);
	}
	
	public static List<Reference> findQuestionActorsVO(final Question question,
			final Status internalStatus,final UserGroup userGroup,final int level, final String locale) {
		return getWorkflowConfigRepository().findQuestionActorsVO(question,
				internalStatus, 
				userGroup,level,locale);
	}
	
	public static List<WorkflowActor> findQuestionActors(final Question question,
			final Status internalStatus,
			final UserGroup userGroup,final int level, final String locale) {
		return getWorkflowConfigRepository().findQuestionActors(question,
				internalStatus,
				userGroup,level,locale);
	}

	
}
