/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ReferencedEntity.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class ReferencedEntity.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="refernced_entities")
public class ReferencedEntity extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The position. */
    private Integer position;

    /** The question. */
    private Question question;

    /**
     * Instantiates a new referenced entity.
     */
    public ReferencedEntity() {
        super();
    }


    /**
     * Gets the position.
     *
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }


    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(final Integer position) {
        this.position = position;
    }


    /**
     * Gets the question.
     *
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }


    /**
     * Sets the question.
     *
     * @param question the new question
     */
    public void setQuestion(final Question question) {
        this.question = question;
    }
}
