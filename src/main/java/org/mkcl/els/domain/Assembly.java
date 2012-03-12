/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Assembly.java
 * Created On: Mar 8, 2012
 */

package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.AssemblyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class Assembly.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Configurable
@Entity
@Table(name = "assemblies")
public class Assembly extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The assembly structure. */
    @ManyToOne(fetch = FetchType.EAGER)
    private AssemblyStructure assemblyStructure;

    /** The assembly. */
    @NotEmpty
    private String assembly;

    /** The strength. */
    @NotNull
    private Integer strength;

    /** The term. */
    @Column(length = 20)
    @NotEmpty
    private String term;

    /** The budget session. */
    private Boolean budgetSession = false;

    /** The monsoonsession. */
    private Boolean monsoonSession = false;

    /** The winter session. */
    private Boolean winterSession = false;

    /** The special session. */
    private Boolean specialSession = false;

    /** The assembly number. */
    @Column(length = 50)
    @NotEmpty
    private String assemblyNumber;

    /** The assembly start date. */

    @Column(length = 50)
    @NotEmpty
    private String assemblyStartDate;

    /** The assembly end date. */
    @Column(length = 50)
    private String assemblyEndDate;

    /** The assembly dissolved on. */
    @Column(length = 50)
    private String assemblyDissolvedOn;

    /** The current assembly. */
    private Boolean currentAssembly = false;

    /** The oath date. */
    @Column(length = 50)
    private String oathDate;

    /** The governers address date. */
    @Column(length = 50)
    private String governersAddressDate;

    /** The election dateof honble speaker. */
    @Column(length = 50)
    private String electionDateofHonbleSpeaker;

    /** The remarks. */
    @Column(length = 1000)
    private String remarks;

    /** The assembly repository. */
    @Autowired
    private transient AssemblyRepository assemblyRepository;

    /**
     * Instantiates a new assembly.
     */
    public Assembly() {
        super();
    }

    /**
     * Instantiates a new assembly.
     *
     * @param assemblyStructure the assembly structure
     * @param assembly the assembly
     * @param strength the strength
     * @param term the term
     * @param budgetSession the budget session
     * @param monsoonSession the monsoon session
     * @param winterSession the winter session
     * @param specialSession the special session
     * @param assemblyStartDate the assembly start date
     * @param assemblyEndDate the assembly end date
     * @param assemblyDissolvedOn the assembly dissolved on
     * @param currentAssembly the current assembly
     * @param version the version
     * @param locale the locale
     * @param assemblyNumber the assembly number
     * @param oathDate the oath date
     * @param governersAddressDate the governers address date
     * @param electionDateofHonbleSpeaker the election dateof honble speaker
     * @param remarks the remarks
     */
    public Assembly(final AssemblyStructure assemblyStructure,
            final String assembly,
            final Integer strength,
            final String term,
            final boolean budgetSession,
            final boolean monsoonSession,
            final boolean winterSession,
            final boolean specialSession,
            final String assemblyStartDate,
            final String assemblyEndDate,
            final String assemblyDissolvedOn,
            final boolean currentAssembly,
            final Long version,
            final String locale,
            final String assemblyNumber,
            final String oathDate,
            final String governersAddressDate,
            final String electionDateofHonbleSpeaker,
            final String remarks) {
        super();
        this.assemblyStructure = assemblyStructure;
        this.assembly = assembly;
        this.strength = strength;
        this.term = term;
        this.budgetSession = budgetSession;
        this.monsoonSession = monsoonSession;
        this.winterSession = winterSession;
        this.specialSession = specialSession;
        this.assemblyStartDate = assemblyStartDate;
        this.assemblyEndDate = assemblyEndDate;
        this.assemblyDissolvedOn = assemblyDissolvedOn;
        this.currentAssembly = currentAssembly;
        this.assemblyNumber = assemblyNumber;
        this.oathDate = oathDate;
        this.governersAddressDate = governersAddressDate;
        this.electionDateofHonbleSpeaker = electionDateofHonbleSpeaker;
        this.remarks = remarks;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    /**
     * Gets the assembly repository.
     *
     * @return the assembly repository
     */
    public static AssemblyRepository getAssemblyRepository() {
        AssemblyRepository assemblyRepository = new Assembly().assemblyRepository;
        if (assemblyRepository == null) {
            throw new IllegalStateException(
                    "AssemblyRepository has not been injected in Assembly Domain");
        }
        return assemblyRepository;
    }

    /**
     * Find by assembly.
     *
     * @param assembly the assembly
     * @return the assembly
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static Assembly findByAssembly(final String assembly) {
        return getAssemblyRepository().findByAssembly(assembly);
    }

    /**
     * Find current assembly.
     *
     * @param locale the locale
     * @return the assembly
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static Assembly findCurrentAssembly(final String locale) {
        return getAssemblyRepository().findCurrentAssembly(locale);
    }

    /**
     * Update previous current assembly.
     *
     * @param assembly the assembly
     * @param locale the locale
     * @author sandeeps
     * @since v1.0.0 Update previous current assembly.
     */
    @Transactional
    public void updatePreviousCurrentAssembly(final Assembly assembly,
                                              final String locale) {
        getAssemblyRepository().updatePreviousCurrentAssembly(assembly, locale);
    }

    // --Getters and Setters-----------------------------------------

    /**
     * Gets the assembly structure.
     *
     * @return the assembly structure
     */
    public AssemblyStructure getAssemblyStructure() {
        return assemblyStructure;
    }

    /**
     * Sets the assembly structure.
     *
     * @param assemblyStructure the new assembly structure
     */
    public void setAssemblyStructure(final AssemblyStructure assemblyStructure) {
        this.assemblyStructure = assemblyStructure;
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
     * Gets the strength.
     *
     * @return the strength
     */
    public Integer getStrength() {
        return strength;
    }

    /**
     * Sets the strength.
     *
     * @param strength the new strength
     */
    public void setStrength(final Integer strength) {
        this.strength = strength;
    }

    /**
     * Gets the term.
     *
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * Sets the term.
     *
     * @param term the new term
     */
    public void setTerm(final String term) {
        this.term = term;
    }

    /**
     * Checks if is budget session.
     *
     * @return true, if is budget session
     */
    public boolean isBudgetSession() {
        return budgetSession;
    }

    /**
     * Sets the budget session.
     *
     * @param budgetSession the new budget session
     */
    public void setBudgetSession(final boolean budgetSession) {
        this.budgetSession = budgetSession;
    }

    /**
     * Checks if is monsoon session.
     *
     * @return true, if is monsoon session
     */
    public boolean isMonsoonSession() {
        return monsoonSession;
    }

    /**
     * Sets the monsoon session.
     *
     * @param monsoonSession the new monsoon session
     */
    public void setMonsoonSession(final boolean monsoonSession) {
        this.monsoonSession = monsoonSession;
    }

    /**
     * Checks if is winter session.
     *
     * @return true, if is winter session
     */
    public boolean isWinterSession() {
        return winterSession;
    }

    /**
     * Sets the winter session.
     *
     * @param winterSession the new winter session
     */
    public void setWinterSession(final boolean winterSession) {
        this.winterSession = winterSession;
    }

    /**
     * Gets the assembly start date.
     *
     * @return the assembly start date
     */
    public String getAssemblyStartDate() {
        return assemblyStartDate;
    }

    /**
     * Sets the assembly start date.
     *
     * @param assemblyStartDate the new assembly start date
     */
    public void setAssemblyStartDate(final String assemblyStartDate) {
        this.assemblyStartDate = assemblyStartDate;
    }

    /**
     * Gets the assembly end date.
     *
     * @return the assembly end date
     */
    public String getAssemblyEndDate() {
        return assemblyEndDate;
    }

    /**
     * Sets the assembly end date.
     *
     * @param assemblyEndDate the new assembly end date
     */
    public void setAssemblyEndDate(final String assemblyEndDate) {
        this.assemblyEndDate = assemblyEndDate;
    }

    /**
     * Gets the assembly dissolved on.
     *
     * @return the assembly dissolved on
     */
    public String getAssemblyDissolvedOn() {
        return assemblyDissolvedOn;
    }

    /**
     * Sets the assembly dissolved on.
     *
     * @param assemblyDissolvedOn the new assembly dissolved on
     */
    public void setAssemblyDissolvedOn(final String assemblyDissolvedOn) {
        this.assemblyDissolvedOn = assemblyDissolvedOn;
    }

    /**
     * Checks if is current assembly.
     *
     * @return true, if is current assembly
     */
    public boolean isCurrentAssembly() {
        return currentAssembly;
    }

    /**
     * Sets the current assembly.
     *
     * @param currentAssembly the new current assembly
     */
    public void setCurrentAssembly(final boolean currentAssembly) {
        this.currentAssembly = currentAssembly;
    }

    /**
     * Gets the serialversionuid.
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * Gets the assembly number.
     *
     * @return the assembly number
     */
    public String getAssemblyNumber() {
        return assemblyNumber;
    }

    /**
     * Sets the assembly number.
     *
     * @param assemblyNumber the new assembly number
     */
    public void setAssemblyNumber(final String assemblyNumber) {
        this.assemblyNumber = assemblyNumber;
    }

    /**
     * Checks if is special session.
     *
     * @return true, if is special session
     */
    public boolean isSpecialSession() {
        return specialSession;
    }

    /**
     * Sets the special session.
     *
     * @param specialSession the new special session
     */
    public void setSpecialSession(final boolean specialSession) {
        this.specialSession = specialSession;
    }

    /**
     * Gets the oath date.
     *
     * @return the oath date
     */
    public String getOathDate() {
        return oathDate;
    }

    /**
     * Sets the oath date.
     *
     * @param oathDate the new oath date
     */
    public void setOathDate(final String oathDate) {
        this.oathDate = oathDate;
    }

    /**
     * Gets the governers address date.
     *
     * @return the governers address date
     */
    public String getGovernersAddressDate() {
        return governersAddressDate;
    }

    /**
     * Sets the governers address date.
     *
     * @param governersAddressDate the new governers address date
     */
    public void setGovernersAddressDate(final String governersAddressDate) {
        this.governersAddressDate = governersAddressDate;
    }

    /**
     * Gets the election dateof honble speaker.
     *
     * @return the election dateof honble speaker
     */
    public String getElectionDateofHonbleSpeaker() {
        return electionDateofHonbleSpeaker;
    }

    /**
     * Sets the election dateof honble speaker.
     *
     * @param electionDateofHonbleSpeaker the new election dateof honble speaker
     */
    public void setElectionDateofHonbleSpeaker(final String electionDateofHonbleSpeaker) {
        this.electionDateofHonbleSpeaker = electionDateofHonbleSpeaker;
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
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

    /**
     * Sets the assembly repository.
     *
     * @param assemblyRepository the new assembly repository
     */
    public void setAssemblyRepository(final AssemblyRepository assemblyRepository) {
        this.assemblyRepository = assemblyRepository;
    }
}
