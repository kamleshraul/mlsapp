package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "status")
public class Status extends BaseDomain implements Serializable{
	// ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;
    
    /** The type. */
    @Column(length = 150)
    private String type;

    @Column(length=600)
    private String name;
 // ---------------------------------Constructors----------------------------------------------

	public Status() {
		super();
	}

	public Status(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}
	// -------------------------------Domain_Methods----------------------------------------------

	

    // ------------------------------------------Getters/Setters-----------------------------------
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
