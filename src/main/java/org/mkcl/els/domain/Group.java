/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Group.java
 * Created On: 19 Jun, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * The Class Group.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Configurable
@Entity
@Table(name = "groups")
public class Group extends BaseDomain implements Serializable {
    
    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The name. */
    //since group name is name 1,2 & so on therefore taking field name as 'name'
    @Column(length = 6)
    @NotNull
    private Integer name;   
    
    // ---------------------------------Constructors----------------------//    
    /**
     * Instantiates a new group.
     */
    public Group() {
	super();	
    }
    
    /**
     * Instantiates a new group.
     *
     * @param name the name
     * @param locale the locale
     * @param version the version
     */
    public Group(final Integer name, final String locale, final Long version) {
        super();
        this.name = name;
    }

    // ----------------------------Domain Methods-------------------------//
    
    // ----------------------------Getters/Setters------------------------//
    /**
     * Gets the name.
     *
     * @return the name
     */
    public Integer getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(Integer name) {
        this.name = name;
    }

}
