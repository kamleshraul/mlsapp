package org.mkcl.els.common.vo;

/**
 * Captures the attributes of a running process. This class is 
 * different from org.mkcl.els.common.vo.ProcessDefinition in the sense 
 * that this class captures the runtime (execution) information 
 * of process while org.mkcl.els.common.vo.ProcessDefinition captures the 
 * meta information of a deployed process.
 */
public class ProcessInstance {

	private String id;
	
	private String processDefinitionId;

	public ProcessInstance(String id, String processDefinitionId) {
		super();
		this.setId(id);
		this.setProcessDefinitionId(processDefinitionId);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

}
