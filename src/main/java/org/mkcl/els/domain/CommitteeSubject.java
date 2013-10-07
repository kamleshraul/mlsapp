package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="committee_subjects")
public class CommitteeSubject extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 5083894194317454342L;
	
	//=============== ATTRIBUTES ===============
	@Column(length=600)
	private String name;
	
	//=============== CONSTRUCTORS =============
	public CommitteeSubject() {
		super();
	}
	
	public CommitteeSubject(final String name,  
			final String locale) {
		super(locale);
		this.setName(name);
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	
	//=============== INTERNAL METHODS =========
	
	//=============== GETTERS/SETTERS ==========
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}