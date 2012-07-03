/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.QuestionLimitingAction.java
 * Created On: 19 Jun, 2012
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
 * The Class QuestionLimitingAction.
 *
 * @author Dhananjay
 * @since v1.1.0
 */
@Configurable
@Entity
@Table(name = "question_limiting_actions")
public class QuestionLimitingAction extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The name. */
    @Column(length = 150)
    @NotEmpty
    private String name;    

    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new question limiting action.
     */
    public QuestionLimitingAction() {
        super();
    }

    /**
     * Instantiates a new question limiting action.
     *
     * @param name the name
     * @param locale the locale
     * @param version the version
     */
    public QuestionLimitingAction(final String name, final String locale, final Long version) {
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
}
