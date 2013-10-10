package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="abbreviations")
public class Abbreviation extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String name;
    
    @Column(length=10000)
    private String abbreviation;
	
    private String identificationkey;
	/****Constructors****/
    
	public Abbreviation() {
		super();
		// TODO Auto-generated constructor stub
	}
 
	public Abbreviation(String name, String abbreviation,String identificationkey) {
		super();
		this.name = name;
		this.abbreviation = abbreviation;
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

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getIdentificationkey() {
		return identificationkey;
	}

	public void setIdentificationkey(String identificationkey) {
		this.identificationkey = identificationkey;
	}

	

	
}
