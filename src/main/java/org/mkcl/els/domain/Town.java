package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.MemberMinisterRepository;
import org.mkcl.els.repository.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="towns")
public class Town extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 4393916246964316389L;

	//=============== ATTRIBUTES ===============
	@Column(length=900)
	private String name;
	
	@ManyToOne
	@JoinColumn(name="district_id")
	private District district;
	
	@Autowired
	private transient TownRepository repository;
	
	   public static TownRepository getTownRepository() {
		   TownRepository repository = new Town().repository;
	    	if (repository == null) {
	    		throw new IllegalStateException(
	                  "TownRepository has not been injected in Town Domain");
	    	}
	    	return repository;
	    }
	
	//=============== CONSTRUCTORS =============
	public Town() {
		super();
	}
	
	public Town(final String name,
			final District district,
			final String locale) {
		super(locale);
		
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	public static Town find(final String name, 
			final District district, 
			final String locale) {
		return getTownRepository().find(name, district, locale);
	}
	
	public static List<Town> find(final District district, 
			final String locale) {
		return getTownRepository().find(district, locale);
	}
	
	public static List<Town> findTownsbyDistricts(
			final String[] districts, final String locale) {
		return getTownRepository().findTownsbyDistricts(districts, locale);
	}
	
	public static List<Town> findTownsByDistrictId(final Long districtId,
			final String sortBy, final String sortOrder, final String locale) throws ELSException {
		return getTownRepository().findTownsByDistrictId(districtId, sortBy,
				sortOrder, locale);
	}
	//=============== INTERNAL METHODS =========
	private static TownRepository getRepository() {
		TownRepository repository = new Town().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"TownRepository has not been injected in" +
				" Town Domain");
		}
		
		return repository;
	}
	
	//=============== GETTERS/SETTERS ==========
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(final District district) {
		this.district = district;
	}
	
}