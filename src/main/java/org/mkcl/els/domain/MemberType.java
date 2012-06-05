/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberType.java
 * Created On: May 11, 2012
 */
package org.mkcl.els.domain;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.springframework.beans.factory.annotation.Configurable;
// TODO: Auto-generated Javadoc
/**
 * The Class MemberType.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "memberTypes")
public class MemberType extends BaseDomain implements Serializable{

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;
    
    @Column(length = 150)
    private String type;
    
   // ---------------------------------Constructors----------------------------------------------
	/**
    * Instantiates a new member type.
    */
   public MemberType() {
		super();
	}
	
	/**
	 * Instantiates a new member type.
	 *
	 * @param name the name
	 */
	public MemberType(final String name) {
		super();
		this.name = name;
	}
	// ---------------------------------Getters and setters----------------------------------------------
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
	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
