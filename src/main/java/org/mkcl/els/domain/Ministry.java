/*
 * 
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Ministry.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_ministry")
public class Ministry extends BaseDomain implements Serializable{
	// ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    /** The name. */
    @Column(length = 150, nullable = false)
    @NotEmpty
    private String department;
    
    /** The alias. */
    @Column(length=50)
    private String alias; // have declared the departmentshortname as alias
    // ---------------------------------Constructors----------------------------------------------

	/**
     * Instantiates a new ministry.
     */
    public Ministry() {
		super();
	}
	
	/**
	 * Instantiates a new ministry.
	 *
	 * @param department the department
	 * @param alias the alias
	 */
	public Ministry(String department, String alias) {
		super();
		this.department = department;
		this.alias = alias;
	}
	
    
    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------
	/**
     * Gets the department.
     *
     * @return the department
     */
    public String getDepartment() {
		return department;
	}
	
	/**
	 * Sets the department.
	 *
	 * @param department the new department
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	
	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}
	
	/**
	 * Sets the alias.
	 *
	 * @param alias the new alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
    
}
