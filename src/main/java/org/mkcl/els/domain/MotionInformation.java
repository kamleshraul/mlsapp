/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MotionInformation.java
 * Created On: Jan 11, 2012
 */
package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MotionInformation.
 *
 * @author nileshp
 */
@Configurable
@Entity
@Table(name = "motion_information")
public class MotionInformation {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The year. */
    private String year;

    /** The motion type. */
    private String motionType;

    /** The assembly. */
    private String assembly;

    /** The assembly date. */
    private String assemblyDate;

    /** The locale. */
    @Column(length = 50)
    @NotEmpty
    private String locale;


    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }


    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    /**
     * Gets the assembly.
     *
     * @return the assembly
     */
    public String getAssembly() {
        return assembly;
    }

    /**
     * Sets the assembly.
     *
     * @param assembly the new assembly
     */
    public void setAssembly(final String assembly) {
        this.assembly = assembly;
    }

    /**
     * Gets the assembly date.
     *
     * @return the assembly date
     */
    public String getAssemblyDate() {
        return assemblyDate;
    }

    /**
     * Sets the assembly date.
     *
     * @param assemblyDate the new assembly date
     */
    public void setAssemblyDate(final String assemblyDate) {
        this.assemblyDate = assemblyDate;
    }

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
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the new year
     */
    public void setYear(final String year) {
        this.year = year;
    }

    /**
     * Gets the motion type.
     *
     * @return the motion type
     */
    public String getMotionType() {
        return motionType;
    }

    /**
     * Sets the motion type.
     *
     * @param motionType the new motion type
     */
    public void setMotionType(final String motionType) {
        this.motionType = motionType;
    }

}
