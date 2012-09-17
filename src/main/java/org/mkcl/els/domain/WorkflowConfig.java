package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@JsonIgnoreProperties({"session", "workflowactors"})
public class WorkflowConfig extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -527613490139348330L;

	/**
	 * If a Device class is added in the future, change the type to
	 * Device.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="workflow_id")
	private Workflow workflow;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="wfconfig_wfactors",
			joinColumns={@JoinColumn(name="wfconfig_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="wfactors_id", referencedColumnName="id")})
	private List<WorkflowActor> workflowactors;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	//=============== Constructor(s) ===============
	public WorkflowConfig() {
		super();
	}

	public WorkflowConfig(final Session session, final Workflow workflow,
            final List<WorkflowActor> workflowactors, final Date createdOn) {
        super();
        this.session = session;
        this.workflow = workflow;
        this.workflowactors = workflowactors;
        this.createdOn = createdOn;
    }
    //=============== Getter & Setters ===============
    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final Workflow workflow) {
        this.workflow = workflow;
    }

    public List<WorkflowActor> getWorkflowactors() {
        return workflowactors;
    }

    public void setWorkflowactors(final List<WorkflowActor> workflowactors) {
        this.workflowactors = workflowactors;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final Date createdOn) {
        this.createdOn = createdOn;
    }
}
