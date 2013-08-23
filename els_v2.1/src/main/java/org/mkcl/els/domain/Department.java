/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Department.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Department.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "departments")
//@JsonIgnoreProperties({"memberDepartmentAssociations"})
public class Department extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 1000)
    @NotEmpty
    private String name;


    /** The parent id. */
   @Column 
   private Boolean isExpired;
   
   /** The remarks. */
   @Column(length=1000)
   private String remarks;

//    @Autowired
//    private transient DepartmentRepository departmentRepository;


	// ---------------------------------Constructors----------------------------------------------
   /**
 * Instantiates a new department.
 */
public Department() {
		super();
   }
   
   /**
    * Instantiates a new department.
    *
    * @param name the name
    * @param isExpired the is expired
    * @param remarks the remarks
    */
   public Department(String name, Boolean isExpired, String remarks) {
		super();
		this.name = name;
		this.isExpired = isExpired;
		this.remarks = remarks;
	}
//
//	public static DepartmentRepository getDepartmentRepository() {
//	    DepartmentRepository departmentRepository = new Department().departmentRepository;
//        if (departmentRepository == null) {
//            throw new IllegalStateException(
//                    "DepartmentRepository has not been injected in Department Domain");
//        }
//        return departmentRepository;
//    }
	// ---------------------------------Getters and Setters----------------------------------------------
/**
 * Gets the name.
 *
 * @return the name
 */
public String getName() {
	return name;
}

/**
 * Sets the name.
 *
 * @param name the new name
 */
public void setName(String name) {
	this.name = name;
}

/**
 * Gets the checks if is expired.
 *
 * @return the checks if is expired
 */
public Boolean getIsExpired() {
	return isExpired;
}

/**
 * Sets the checks if is expired.
 *
 * @param isExpired the new checks if is expired
 */
public void setIsExpired(Boolean isExpired) {
	this.isExpired = isExpired;
}

/**
 * Gets the remarks.
 *
 * @return the remarks
 */
public String getRemarks() {
	return remarks;
}

/**
 * Sets the remarks.
 *
 * @param remarks the new remarks
 */
public void setRemarks(String remarks) {
	this.remarks = remarks;
}
	

}
