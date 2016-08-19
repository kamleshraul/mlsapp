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
import org.mkcl.els.common.exception.ELSException;
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
	
	private String module;

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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public static Boolean removeActor(Long workflowconfigId, Long workflowactorId) {
		return getWorkflowConfigRepository().removeActor(workflowconfigId, workflowactorId);
	}
	
	/*************************Question*****************************/
	public static Reference findActorVOAtGivenLevel(final Question question, final Status status, final String usergroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(question, status, usergroupType, level, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final Question question, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(question, processWorkflow, userGroupType, level, locale);
	}

	public static Reference findActorVOAtFirstLevel(final Question question, final Workflow processWorkflow, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(question, processWorkflow, locale);
	}
	
	public static List<Reference> findQuestionActorsVO(final Question question,
			final Status internalStatus,final UserGroup userGroup,final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findQuestionActorsVO(question,
				internalStatus, 
				userGroup,level,locale);
	}
	
	public static List<WorkflowActor> findQuestionActors(final Question question,
			final Status internalStatus,
			final UserGroup userGroup,final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findQuestionActors(question,
				internalStatus,
				userGroup,level,locale);
	}
	/***********************Question***********************/
		
	/*********************************Resolution******************************/
	public static Reference findActorVOAtGivenLevel(final Resolution resolution, final HouseType workflowHouseType, final Status status, final String usergroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(resolution, workflowHouseType, status, usergroupType, level, locale);
	}
	
	public static List<Reference> findResolutionActorsVO(final Resolution resolution,
			final Status internalStatus,final UserGroup userGroup,final int level,final String workflowHouseType, final String locale) {
		return getWorkflowConfigRepository().findResolutionActorsVO(resolution,
				internalStatus, 
				userGroup,level,workflowHouseType,locale);
	}
	/*********************************Resolution******************************/
	
	/*********************************Bills******************************/
	public static List<Reference> findBillActorsVO(final Bill bill,
			final Status internalStatus,final UserGroup userGroup,final int level, final String locale) {
		return getWorkflowConfigRepository().findBillActorsVO(bill,
				internalStatus, 
				userGroup,level,locale);
	}
	
	public static List<Reference> findBillActorsVO(final Bill bill, HouseType houseType, final Boolean isActorAcrossHouse,
			final Status internalStatus,final UserGroup userGroup,final int level, final String locale) {
		return getWorkflowConfigRepository().findBillActorsVO(bill, houseType, isActorAcrossHouse,
				internalStatus, 
				userGroup,level,locale);
	}
	
	public static Reference findActorVOAtFirstLevel(final Bill bill, final Workflow processWorkflow, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(bill, processWorkflow, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final Bill bill, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(bill, processWorkflow, userGroupType, level, locale);
	}
	
	public static List<Reference> findBillAmendmentMotionActorsVO(final BillAmendmentMotion billAmendmentMotion,
			final Status internalStatus,final UserGroup userGroup,final int level, final String locale) {
		return getWorkflowConfigRepository().findBillAmendmentMotionActorsVO(billAmendmentMotion,
				internalStatus, userGroup,level,locale);
	}
	
	public static Reference findActorVOAtFirstLevel(final BillAmendmentMotion billAmendmentMotion, final Workflow processWorkflow, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(billAmendmentMotion, processWorkflow, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final BillAmendmentMotion billAmendmentMotion, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(billAmendmentMotion, processWorkflow, userGroupType, level, locale);
	}
	/*********************************Bills******************************/
	
	/*******************************Committee****************************/
	public static List<WorkflowActor> findCommitteeTourActors(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName,
			final Integer assigneeLevel, 
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().
			findCommitteeTourActors(houseType, userGroup, status, workflowName, 
					assigneeLevel, locale);
	}

	/**
	 * Returns null if there is no next actor
	 */
	public static WorkflowActor findNextCommitteeTourActor(
			final HouseType houseType, 
			final UserGroup userGroup, 
			final Status status,
			final String workflowName, 
			final Integer assigneeLevel, 
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().
			findNextCommitteeTourActor(houseType, userGroup, status, 
				workflowName, assigneeLevel, locale);
	}
	
	public static List<WorkflowActor> findCommitteeActors(
			final HouseType houseType,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int level,
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().findCommitteeActors(
				houseType, userGroup, status, workflowName, level, locale);
	}

	/**
	 * Returns null if there is no next actor
	 */
	public static WorkflowActor findNextCommitteeActor(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().
			findNextCommitteeActor(houseType, userGroup, status, 
					workflowName, level, locale);
	}
	/*******************************Committee****************************/

	
	/******************************Editing******************************/
	public static WorkflowActor findNextEditingActor(final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findNextEditingActor(houseType, userGroup, status, workflowName, level, locale);
	}
	
	public static List<WorkflowActor> findEditingActors(final HouseType houseType,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findEditingActors(houseType, userGroup, status, workflowName, level, locale);
	}
	/******************************Editing******************************/
		
	/****************************Motion*******************************/
	public static Reference findActorVOAtFirstLevel(final Motion motion, final Workflow processWorkflow, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(motion, processWorkflow, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final Motion motion, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
	}
	
	public static List<Reference> findMotionActorsVO(final Motion motion,
			final Status internalStatus,final UserGroup userGroup,final int level,
			final String locale) {
		return getWorkflowConfigRepository().findMotionActorsVO(motion,
				internalStatus,userGroup,level,
				locale);
	}
	/****************************Motion*******************************/
	
	/****************************StandaloneMotion********************/
	public static List<Reference> findStandaloneMotionActorsVO(final StandaloneMotion motion,
			final Status internalStatus,final UserGroup userGroup,final int level, final String locale) {
		return getWorkflowConfigRepository().findStandaloneMotionActorsVO(motion, internalStatus, userGroup, level, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final StandaloneMotion motion, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
	}
	
	public static Reference findActorVOAtFirstLevel(final StandaloneMotion motion, final Workflow processWorkflow, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(motion, processWorkflow, locale);
	}
	/****************************StandaloneMotion********************/
	
	/****************************EventMotion*******************************/
	public static List<Reference> findEventMotionActorsVO(final EventMotion motion,
			final Status internalStatus,
			final UserGroup userGroup,
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findEventMotionActorsVO(motion, internalStatus, userGroup, level, locale);
	}
	
	public static WorkflowActor findNextEventMotionActor(final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findNextEventMotionActor(houseType, userGroup, status, workflowName, level, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final EventMotion motion, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
	}
	
	public static Reference findActorVOAtFirstLevel(final EventMotion motion, final Workflow processWorkflow, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(motion, processWorkflow, locale);
	}
	
	/***************************EventMotion*******************************/
	
	/***************************CutMotion*********************************/
	public static Reference findActorVOAtFirstLevel(final CutMotion motion, final Workflow processWorkflow, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(motion, processWorkflow, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final CutMotion motion, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(motion, processWorkflow, userGroupType, level, locale);
	}
	
	public static List<Reference> findCutMotionActorsVO(final CutMotion motion,
			final Status internalStatus,
			final UserGroup userGroup,
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findCutMotionActorsVO(motion, internalStatus, userGroup, level, locale);
	}
	
	public static WorkflowActor findNextCutMotionActor(final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findNextCutMotionActor(houseType, userGroup, status, workflowName, level, locale);
	}
	
	/**
	 * Returns null if there is no next actor
	 */
	public static WorkflowActor findNextCutMotionDateActor(
			final DeviceType deviceType,
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().findNextCutMotionDateActor(deviceType, houseType, userGroup, status, workflowName, level, locale);
	}
	
	public static List<WorkflowActor> findCutMotionDateActors(final HouseType houseType,
			final UserGroup userGroup,
			final Status status,
			final String workflowName,
			final int level,
			final String locale) {
		return getWorkflowConfigRepository().findCutMotionDateActors(houseType, userGroup, status, workflowName, level, locale);
	}
	/***************************CutMotion********************************/
	
	/***************************DiscussionMotion*************************/
	public static List<Reference> findDiscussionMotionActorsVO(
			DiscussionMotion motion, Status internalStatus,
			UserGroup userGroup, int level, String locale) {
		return getWorkflowConfigRepository().findDiscussionMotionActors(motion, internalStatus, userGroup, level, locale);
	}
	
	public static  boolean containsGivenData(final List<? extends BaseDomain> dataList, final String data){
		
		return getWorkflowConfigRepository().containsGivenData(dataList, data);
	}
	
	public static Reference findActorVOAtGivenLevel(final DiscussionMotion question, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(question, processWorkflow, userGroupType, level, locale);
	}
	/**************************DiscussionMotion*************************/
	
	/****************************** Adjournment Motion *********************/
	public static List<Reference> findAdjournmentMotionActorsVO(
			AdjournmentMotion motion, Status internalStatus,
			UserGroup userGroup, int level, String locale) {
		return getWorkflowConfigRepository().findAdjournmentMotionActors(motion, internalStatus, userGroup, level, locale);
	}
	
	public static Reference findActorVOAtFirstLevel(final AdjournmentMotion adjournmentMotion, final Workflow processWorkflow, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtFirstLevel(adjournmentMotion, processWorkflow, locale);
	}
	
	public static Reference findActorVOAtGivenLevel(final AdjournmentMotion adjournmentMotion, final Workflow processWorkflow, final UserGroupType userGroupType, final int level, final String locale) throws ELSException {
		return getWorkflowConfigRepository().findActorVOAtGivenLevel(adjournmentMotion, processWorkflow, userGroupType, level, locale);
	}
	/****************************** Adjournment Motion *********************/
	
	/****************************** Prashnavali ***************************/
	public static List<WorkflowActor> findPrashnavaliActors(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName,
			final Integer assigneeLevel, 
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().
			findPrashnavaliActors(houseType, userGroup, status, workflowName, 
					assigneeLevel, locale);
	}
	
	public static WorkflowActor findNextPrashnavaliActor(
			final HouseType houseType,
			final UserGroup userGroup, 
			final Status status, 
			final String workflowName, 
			final int level,
			final String locale) {
		return WorkflowConfig.getWorkflowConfigRepository().
			findNextPrashnavaliActor(houseType, userGroup, status, 
					workflowName, level, locale);
	}
	/****************************** Prashnavali ***************************/
}