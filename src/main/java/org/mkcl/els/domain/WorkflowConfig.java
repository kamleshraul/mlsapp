package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
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
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Even though the class extends BaseDomain, all the objects of the class
 * (and thus the corresponding entries in the database table) must be locale 
 * insensitive. 
 */
@Configurable
@Entity
@Table(name="wf_config")
@JsonIgnoreProperties({"session", "workflowLevels"})
public class WorkflowConfig extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -527613490139348330L;

	/**
	 * If a Device class is added in the future, change the type to
	 * Device.
	 */
	@Column(length=1000)
	private String device;
	
	@Column(length=1000)
	private String workflowName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="wf_config_levels",
			joinColumns={@JoinColumn(name="wf_config_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="wf_level_id", referencedColumnName="id")})
	private List<WorkflowLevel> workflowLevels;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	//=============== Constructor(s) ===============
	public WorkflowConfig() {
		super();
	}
	
	//=============== Getter & Setters ===============
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<WorkflowLevel> getWorkflowLevels() {
		return workflowLevels;
	}

	public void setWorkflowLevels(List<WorkflowLevel> workflowLevels) {
		this.workflowLevels = workflowLevels;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
}
