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
@Table(name="dams")
public class Dam extends BaseDomain implements Serializable{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String name;
	
    @ManyToOne(fetch=FetchType.LAZY)
    private District district;
    
    private String identificationkey;
	/****Constructors****/
	
    public Dam() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    public Dam(String name,String identificationkey,District district) {
		super();
		this.name = name;
		this.identificationkey=identificationkey;
		this.district=district;
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

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}
	
}
