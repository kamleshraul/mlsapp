package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="committee_meeting_types")
public class CommitteeMeetingType extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1267235341787596599L;
	
	//=============== ATTRIBUTES ===============
	@Column(length=600)
	private String name;
	
	@Column(length=200)
	private String type;
	
	//=============== CONSTRUCTORS =============
	public CommitteeMeetingType() {
		super();
	}
	
	public CommitteeMeetingType(final String name, 
			final String type, 
			final String locale) {
		super(locale);
		this.setName(name);
		this.setType(type);
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

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

}