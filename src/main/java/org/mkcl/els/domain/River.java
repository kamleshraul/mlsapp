package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="rivers")
public class River extends BaseDomain implements Serializable{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String name;
    
    private String identificationkey;
	
	
	/****Constructors****/
    
    public River() {
		super();
	}

	public River(String name,String identificationkey) {
		super();
		this.name = name;
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

	public String getIdentificationkey() {
		return identificationkey;
	}

	public void setIdentificationkey(String identificationkey) {
		this.identificationkey = identificationkey;
	}

	
}
