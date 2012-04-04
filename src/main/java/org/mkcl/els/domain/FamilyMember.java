/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Family.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

import com.sun.istack.NotNull;

/**
 * The Class Family.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "families")
public class FamilyMember extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 600)
    private String name;

    /** The relation. */
    @OneToOne
    @JoinColumn(name = "relation_id")
    @NotNull
    private Relation relation;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new family.
     */
    public FamilyMember() {
        super();
        //this.relation=new Relation();
    }

    /**
     * Instantiates a new family.
     *
     * @param name the name
     * @param relation the relation
     */
    public FamilyMember(final String name, final Relation relation) {
        super();
        this.name = name;
        this.relation = relation;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------
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

    /**
     * Gets the relation.
     *
     * @return the relation
     */
    public Relation getRelation() {
        return relation;
    }

    /**
     * Sets the relation.
     *
     * @param relation the new relation
     */
    public void setRelation(final Relation relation) {
        this.relation = relation;
    }

}
