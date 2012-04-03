package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@DiscriminatorValue("E")
public class Employee extends Person implements Serializable{
		
	private transient static final long serialVersionUID = 1L;
	
	@ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "credentialId")
    private Credential credential;

	public Employee() {
		super();
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}	
	
	}
