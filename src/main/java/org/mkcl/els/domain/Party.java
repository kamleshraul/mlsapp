/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Party.java
 * Created On: Dec 20, 2011
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Party.
 *
 * @author meenalw
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "parties")
public class Party extends BaseDomain implements Serializable {

    // ---------------------------------Attributes-------------------------------------------------
    /**
     * The Constant serialVersionUID.
     */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @NotEmpty
    private String name;

    /** The abbreviation. */
    @Column(length = 30, nullable = false)
    @NotEmpty
    private String abbreviation;

    /** The photo. */
    @Column(length = 50)
    private String photo;

    /**
     * Repository.
     */
    @Autowired
    private transient PartyRepository partyRepository;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new party.
     */
    public Party() {
        super();
    }

    /**
     * Instantiates a new party.
     *
     * @param name the name
     * @param abbreviation the abbreviation
     * @param version the version
     * @param locale the locale
     * @param photo the photo
     */
    public Party(final String name,
            final String abbreviation,
            final Long version,
            final String locale,
            final String photo) {
        super();
        this.name = name;
        this.abbreviation = abbreviation;
        this.photo = photo;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    /**
     * Gets the party repository.
     *
     * @return the party repository
     */
    public static PartyRepository getPartyRepository() {
        final PartyRepository repository = new Party().partyRepository;
        if (repository == null) {
            throw new IllegalStateException(
                    "PartyRepository has not been injected");
        }
        return repository;
    }





    /**
     * Gets the name.
     *
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name new name
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the abbreviation.
     *
     * @return the abbreviation
     */
    public final String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Sets the abbreviation.
     *
     * @param abbreviation the new abbreviation
     */
    public final void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Gets the photo.
     *
     * @return the photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets the photo.
     *
     * @param photo the new photo
     */
    public void setPhoto(final String photo) {
        this.photo = photo;
    }

}
