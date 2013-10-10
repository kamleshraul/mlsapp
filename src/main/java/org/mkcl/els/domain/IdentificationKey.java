package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="identificationKeys")
public class IdentificationKey extends BaseDomain implements Serializable{
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /****Attributes****/
    @Column(length=10000)
	private String master;
	
    @Column(length=10000)
    private String mastertable;
    
    @Column
    private String searchField;
    
    private String identificationkey;

    /****Constructors****/
	public IdentificationKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IdentificationKey(String master, String identificationkey,String mastertable, String searchField) {
		super();
		this.master = master;
		this.identificationkey = identificationkey;
		this.mastertable=mastertable;
		this.searchField=searchField;
	}

	/****Domain Method****/
	
	
	/****Getters and Setters****/
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getIdentificationkey() {
		return identificationkey;
	}

	public void setIdentificationkey(String identificationkey) {
		this.identificationkey = identificationkey;
	}

	public String getMastertable() {
		return mastertable;
	}

	public void setMastertable(String mastertable) {
		this.mastertable = mastertable;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	
	
	
}
