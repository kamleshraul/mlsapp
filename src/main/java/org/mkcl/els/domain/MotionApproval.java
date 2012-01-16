/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MotionApproval.java
 * Created On: Jan 13, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class MotionApproval.
 *
 * @author meenalw
 * @since v1.0.0
 */
@Entity
@Table(name = "motion_approval")
public class MotionApproval {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The motion text. */
    private String motionText;

    /** The revised text. */
    private String revisedText;

    /** The criteria. */
    private char criteria;

    /** The remarks. */
    private String remarks;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * Gets the motion text.
     *
     * @return the motion text
     */
    public String getMotionText() {
        return motionText;
    }


    /**
     * Sets the motion text.
     *
     * @param motionText the new motion text
     */
    public void setMotionText(String motionText) {
        this.motionText = motionText;
    }


    /**
     * Gets the revised text.
     *
     * @return the revised text
     */
    public String getRevisedText() {
        return revisedText;
    }


    /**
     * Sets the revised text.
     *
     * @param revisedText the new revised text
     */
    public void setRevisedText(String revisedText) {
        this.revisedText = revisedText;
    }


    /**
     * Gets the criteria.
     *
     * @return the criteria
     */
    public char getCriteria() {
        return criteria;
    }


    /**
     * Sets the criteria.
     *
     * @param criteria the new criteria
     */
    public void setCriteria(char criteria) {
        this.criteria = criteria;
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
