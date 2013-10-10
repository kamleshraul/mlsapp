package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="government_projects")
public class GovernmentProject extends BaseDomain implements Serializable{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String name;
	
    @Column(length=10000)
   	private String type;
    
    @ManyToOne
    private District district;
    
    private String identificationkey;
	/****Constructors****/
    
    public GovernmentProject() {
		super();
	}

	public GovernmentProject(String name,String identificationkey) {
		super();
		this.name = name;
		this.identificationkey=identificationkey;
	}

	public GovernmentProject(String name, String type, District district,
			String identificationkey) {
		super();
		this.name = name;
		this.type = type;
		this.district = district;
		this.identificationkey = identificationkey;
	}
 
	/****Domain Methods ****/

	

	/****Getters and Setters****/
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentificationkey() {
		return identificationkey;
	}

	public void setIdentificationkey(String identificationkey) {
		this.identificationkey = identificationkey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	
	
}
