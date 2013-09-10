package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="committee_designations")
public class CommitteeDesignation extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -1861401573501303968L;

	//=============== ATTRIBUTES ===============
	@Column(length=600)
	private String name;
	
	@Column(length=200)
	private String type;
	
	private Integer priority;
	
	//=============== CONSTRUCTORS =============
	public CommitteeDesignation() {
		super();
	}
	
	public CommitteeDesignation(final String name, 
			final String type, 
			final String locale) {
		super(locale);
		this.setName(name);
		this.setType(type);
	}
	
	public CommitteeDesignation(final String name, 
			final String type,
			final Integer priority,
			final String locale) {
		super(locale);
		this.setName(name);
		this.setType(type);
		this.setPriority(priority);
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(final Integer priority) {
		this.priority = priority;
	}

}