package org.mkcl.els.common.vo;

/**
 * Captures the structure of a Process Definition. This class
 * is different from org.mkcl.els.common.vo.ProcessInstance in 
 * the sense that this class captures the meta information of a 
 * deployed process while org.mkcl.els.common.vo.ProcessInstance
 * captures the runtime (execution) information of process.
 */
public class ProcessDefinition {

	private String id;

	private String key;

	private String name;

	private String category;
	
	private String processImage;
	
	private String deploymentId;
	
	private String deploymentTime;

	private Integer version;

	public ProcessDefinition(final String id, final String key) {
		super();
		this.setId(id);
		this.setKey(key);
	}
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProcessImage() {
		return processImage;
	}

	public void setProcessImage(String processImage) {
		this.processImage = processImage;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getDeploymentTime() {
		return deploymentTime;
	}

	public void setDeploymentTime(String deploymentTime) {
		this.deploymentTime = deploymentTime;
	}

}
