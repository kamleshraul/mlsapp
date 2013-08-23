/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.ProcessInstance.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.common.vo;

// TODO: Auto-generated Javadoc
/**
 * Captures the attributes of a running process. This class is
 * different from org.mkcl.els.common.vo.ProcessDefinition in the sense
 * that this class captures the runtime (execution) information
 * of process while org.mkcl.els.common.vo.ProcessDefinition captures the
 * meta information of a deployed process.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class ProcessInstance {

	/** The id. */
	private String id;

	/** The process definition id. */
	private String processDefinitionId;

	/**
	 * Instantiates a new process instance.
	 *
	 * @param id the id
	 * @param processDefinitionId the process definition id
	 */
	public ProcessInstance(final String id, final String processDefinitionId) {
		super();
		this.setId(id);
		this.setProcessDefinitionId(processDefinitionId);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the process definition id.
	 *
	 * @return the process definition id
	 */
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	/**
	 * Sets the process definition id.
	 *
	 * @param processDefinitionId the new process definition id
	 */
	public void setProcessDefinitionId(final String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

}
