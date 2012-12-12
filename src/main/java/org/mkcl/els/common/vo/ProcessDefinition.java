/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.vo.ProcessDefinition.java
 * Created On: Dec 12, 2012
 */
package org.mkcl.els.common.vo;

// TODO: Auto-generated Javadoc
/**
 * Captures the structure of a Process Definition. This class
 * is different from org.mkcl.els.common.vo.ProcessInstance in
 * the sense that this class captures the meta information of a
 * deployed process while org.mkcl.els.common.vo.ProcessInstance
 * captures the runtime (execution) information of process.
 *
 * @author compaq
 * @since v1.0.0
 */
public class ProcessDefinition {

	/** The id. */
	private String id;

	/** The key. */
	private String key;

	/** The name. */
	private String name;

	/** The category. */
	private String category;
	
	/** The process image. */
	private String processImage;
	
	/** The deployment id. */
	private String deploymentId;
	
	/** The deployment time. */
	private String deploymentTime;

	/** The version. */
	private Integer version;

	/**
	 * Instantiates a new process definition.
	 *
	 * @param id the id
	 * @param key the key
	 */
	public ProcessDefinition(final String id, final String key) {
		super();
		this.setId(id);
		this.setKey(key);
	}
	
	/**
	 * Instantiates a new process definition.
	 *
	 * @param id the id
	 * @param key the key
	 * @param name the name
	 * @param category the category
	 * @param processImage the process image
	 * @param deploymentId the deployment id
	 * @param deploymentTime the deployment time
	 * @param version the version
	 */
	public ProcessDefinition(final String id, final String key, final String name, 
			final String category, final String processImage, 
			final String deploymentId,
			final String deploymentTime, final Integer version) {
		super();
		this.setId(id);
		this.setKey(key);
		this.setName(name);
		this.setCategory(category);
		this.setProcessImage(processImage);
		this.setDeploymentId(deploymentId);
		this.setDeploymentTime(deploymentTime);
		this.setVersion(version);
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
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Gets the process image.
	 *
	 * @return the process image
	 */
	public String getProcessImage() {
		return processImage;
	}

	/**
	 * Sets the process image.
	 *
	 * @param processImage the new process image
	 */
	public void setProcessImage(String processImage) {
		this.processImage = processImage;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Gets the deployment id.
	 *
	 * @return the deployment id
	 */
	public String getDeploymentId() {
		return deploymentId;
	}

	/**
	 * Sets the deployment id.
	 *
	 * @param deploymentId the new deployment id
	 */
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	/**
	 * Gets the deployment time.
	 *
	 * @return the deployment time
	 */
	public String getDeploymentTime() {
		return deploymentTime;
	}

	/**
	 * Sets the deployment time.
	 *
	 * @param deploymentTime the new deployment time
	 */
	public void setDeploymentTime(String deploymentTime) {
		this.deploymentTime = deploymentTime;
	}

}
