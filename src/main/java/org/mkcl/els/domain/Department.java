package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;
@Configurable
@Entity
@Table(name = "masters_department")
public class Department extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 1000)
    @NotEmpty
    private String name;

    /** The house type. */
    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

	// ---------------------------------Constructors----------------------------------------------
    public Department() {
		super();
	}

	public Department(String name, Ministry ministry) {
		super();
		this.name = name;
		this.ministry = ministry;
	}
	// -------------------------------Domain_Methods----------------------------------------------

	// ------------------------------------------Getters/Setters-------------------------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}
    
}
