package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class UserGroupDraft.
 */
@Configurable
@Entity
@Table(name = "usergroup_drafts")
public class UserGroupDraft extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------------------
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.DATE)
    private Date activeFrom;
    
    @Temporal(TemporalType.DATE)
    private Date activeTo;
    
    /** The name. */
	@ManyToOne(fetch=FetchType.EAGER)
	private UserGroupType userGroupType;

	/** The parameters. */
	@ElementCollection
    @MapKeyColumn(name="parameter_key")
    @Column(name="parameter_value",length=10000)
    @CollectionTable(name="usergroup_drafts_parameters", joinColumns = @JoinColumn(name = "usergroup_draft"))
	private Map<String,String> parameters;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name="editedon")
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;

    
	// -------------------------------Getters & Setters-------------------------------
    public Date getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

	public UserGroupType getUserGroupType() {
		return userGroupType;
	}

	public void setUserGroupType(UserGroupType userGroupType) {
		this.userGroupType = userGroupType;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}    

}