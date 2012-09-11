package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * Even though the class extends BaseDomain, all the objects of the class
 * (and thus the corresponding entries in the database table) must be locale 
 * insensitive. 
 */
@Configurable
@Entity
@Table(name="wf_level")
public class WorkflowLevel extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1328929069597368635L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	private Role role;
	
	/**
	 * Between org.mkcl.els.domain.Credential and org.mkcl.els.domain.User,
	 * Credential is non locale aware while User is locale aware. Hence, the
	 * decision to use Credential object instead of User object.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="credential_id")
	private Credential credential;
	
	private Integer level;

	//=============== Constructor(s) ===============
	public WorkflowLevel() {
		super();
	}

	public WorkflowLevel(Role role, Credential credential, Integer level) {
		super();
		this.role = role;
		this.credential = credential;
		this.level = level;
	}

	//=============== Getter & Setters ===============
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
}
