package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="party_types")
public class PartyType extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 723771137435176518L;

	//=============== ATTRIBUTES ===============
	@Column(length=600)
	private String name;
	
	@Column(length=200)
	private String type;
	
	//=============== CONSTRUCTORS =============
	public PartyType() {
		super();
	}
	
	public PartyType(final String name,
			final String type, 
			final String locale) {
		super(locale);
		this.setName(name);
		this.setType(type);
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	public static PartyType findByType(final String type,
			final String locale) {
		PartyType partyType = 
			PartyType.findByFieldName(PartyType.class, "type", type, locale);
		return partyType;
	}
	
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