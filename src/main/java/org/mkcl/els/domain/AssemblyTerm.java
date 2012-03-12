/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.AssemblyTerm.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class AssemblyTerm.
 *
 * @author samiksham
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "assembly_terms")
public class AssemblyTerm extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    // ---------------------------------Attributes------------------------//
    /** The term. */
    @NotNull
    private Integer term;

    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new assembly term.
     */
    public AssemblyTerm() {
        super();
    }

    /**
     * Instantiates a new assembly term.
     *
     * @param term the term
     */
    public AssemblyTerm(final Integer term) {
        super();
        this.term = term;
    }

    // ----------------------------Domain Methods-------------------------//

    // ----------------------------Getters/Setters------------------------//

    /**
     * Gets the term.
     *
     * @return the term
     */
    public Integer getTerm() {
        return term;
    }

    /**
     * Sets the term.
     *
     * @param term the new term
     */
    public void setTerm(final Integer term) {
        this.term = term;
    }

}
