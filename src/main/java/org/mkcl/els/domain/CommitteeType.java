package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.ApplicationConstants;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_types")
@JsonIgnoreProperties({"houseType"})
public class CommitteeType extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 8967854029689454985L;

	//=============== ATTRIBUTES ===============
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="house_type_id")
	private HouseType houseType;
	
	@Column(length=600)
	private String name;
	
	@Column(length=200)
	private String type;

	//=============== CONSTRUCTORS =============
	public CommitteeType() {
		super();
	}
	
	public CommitteeType(final HouseType houseType,
			final String name,
			final String type, 
			final String locale) {
		super(locale);
		this.setHouseType(houseType);
		this.setName(name);
		this.setType(type);
	}
	
	//=============== VIEW METHODS =============
	
	
	//=============== DOMAIN METHODS ===========
	public static List<CommitteeType> find(final HouseType houseType,
			final String locale) {
		List<CommitteeType> committeeTypes = 
			CommitteeType.findAllByFieldName(CommitteeType.class, "houseType", 
			houseType, "name", ApplicationConstants.ASC, locale);
		return committeeTypes;
	}
	
	//=============== INTERNAL METHODS =========
	
	
	//=============== GETTERS/SETTERS ==========
	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(final HouseType houseType) {
		this.houseType = houseType;
	}
	
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