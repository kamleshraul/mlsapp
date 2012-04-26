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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.domain.associations.MemberDepartmentAssociation;
import org.springframework.beans.factory.annotation.Configurable;
// TODO: Auto-generated Javadoc

/**
 * The Class Department.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "departments")
@JsonIgnoreProperties({"memberDepartmentAssociations"})
public class Department extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 1000)
    @NotEmpty
    private String name;

  
    /** The parent id. */
    @ManyToOne
	private Department parentId;
    
    /** The member party associations. */
    @OneToMany(mappedBy = "department")    
    private List<MemberDepartmentAssociation> memberDepartmentAssociations;	

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
	 * @param parentId the parent id
	 */
	public Department(final String name,final Department parentId) {
		super();
		this.name = name;
		this.parentId = parentId;
	}
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
	 * Gets the parent id.
	 *
	 * @return the parent id
	 */
	public Department getParentId() {
		return parentId;
	}
	
	/**
	 * Sets the parent id.
	 *
	 * @param parentId the new parent id
	 */
	public void setParentId(Department parentId) {
		this.parentId = parentId;
	}
	
	public List<MemberDepartmentAssociation> getMemberDepartmentAssociations() {
		return memberDepartmentAssociations;
	}

	public void setMemberDepartmentAssociations(
			List<MemberDepartmentAssociation> memberDepartmentAssociations) {
		this.memberDepartmentAssociations = memberDepartmentAssociations;
	}
	
}
