package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="forts")
public class Fort extends BaseDomain implements Serializable{
	

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String name;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private District district;
    
    private String identificationkey;

    /****Constructors****/
    
    public Fort() {
		super();
	}
    
    
	public Fort(String name, District district,String identificationkey) {
		super();
		this.name = name;
		this.district = district;
		this.identificationkey=identificationkey;
	}


	/****Domain Methods ****/

	/****Getters and Setters****/
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public District getDistrict() {
		return district;
	}


	public void setDistrict(District district) {
		this.district = district;
	}


	public String getIdentificationkey() {
		return identificationkey;
	}


	public void setIdentificationkey(String identificationkey) {
		this.identificationkey = identificationkey;
	}
}
