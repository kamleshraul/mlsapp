/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Person.java
 * Created On: Mar 19, 2012
 */

package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class Person.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "members")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "member_type")
@DiscriminatorValue("P")
@JsonIgnoreProperties({"title","maritalStatus","gender","professions","nationality","permanentAddress","presentAddress","contact"})
public class Person extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The title. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="title_id")
    private Title title;

    /** The first name. */
    @Column(length = 300)
    private String firstName;

    /** The middle name. */
    @Column(length = 300)
    private String middleName;

    /** The last name. */
    @Column(length = 300)
    private String lastName;

    /** The birth date. */
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    /** The birth place. */
    @Column(length = 300)
    private String birthPlace;

    /** The marital status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "maritalstatus_id")
    private MaritalStatus maritalStatus;

    /** The marriage date. */
    @Temporal(TemporalType.DATE)
    private Date marriageDate;

    /** The gender. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private Gender gender;

    /** The profession. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "associations_member_profession",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "profession_id",
                    referencedColumnName = "id") })
    private List<Profession> professions;

    /** The nationality. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;

    /** The photo. */
    @Column(length = 200)
    private String photo;

    /** The specimen signature. */
    @Column(length = 200)
    private String specimenSignature;

    /** The permanent address. */
    @OneToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "permanentaddress_id")
    protected Address permanentAddress;

    /** The present address. */
    @OneToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "presentaddress_id")
    protected Address presentAddress;

    /** The contact. */
    @OneToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "contactdetails_id")
    protected Contact contact;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new person.
     */
    public Person() {
        super();
    }



    /**
     * Instantiates a new person.
     *
     * @param title the title
     * @param firstName the first name
     * @param middleName the middle name
     * @param lastName the last name
     * @param birthDate the birth date
     * @param gender the gender
     */
    public Person(final Title title, final String firstName, final String middleName,
            final String lastName, final Date birthDate, final Gender gender) {
        super();
        this.title = title;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------


    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public Title getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(final Title title) {
		this.title = title;
	}

	/**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name.
     *
     * @param middleName the new middle name
     */
    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the birth date.
     *
     * @return the birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date.
     *
     * @param birthDate the new birth date
     */
    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the birth place.
     *
     * @return the birth place
     */
    public String getBirthPlace() {
        return birthPlace;
    }

    /**
     * Sets the birth place.
     *
     * @param birthPlace the new birth place
     */
    public void setBirthPlace(final String birthPlace) {
        this.birthPlace = birthPlace;
    }

    /**
     * Gets the marital status.
     *
     * @return the marital status
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the marital status.
     *
     * @param maritalStatus the new marital status
     */
    public void setMaritalStatus(final MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the marriage date.
     *
     * @return the marriage date
     */
    public Date getMarriageDate() {
        return marriageDate;
    }

    /**
     * Sets the marriage date.
     *
     * @param marriageDate the new marriage date
     */
    public void setMarriageDate(final Date marriageDate) {
        this.marriageDate = marriageDate;
    }

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender the new gender
     */
    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    /**
     * Gets the professions.
     *
     * @return the professions
     */
    public List<Profession> getProfessions() {
        return professions;
    }

    /**
     * Sets the professions.
     *
     * @param professions the new professions
     */
    public void setProfessions(final List<Profession> professions) {
        this.professions = professions;
    }

    /**
     * Gets the nationality.
     *
     * @return the nationality
     */
    public Nationality getNationality() {
        return nationality;
    }

    /**
     * Sets the nationality.
     *
     * @param nationality the new nationality
     */
    public void setNationality(final Nationality nationality) {
        this.nationality = nationality;
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

    /**
     * Gets the specimen signature.
     *
     * @return the specimen signature
     */
    public String getSpecimenSignature() {
        return specimenSignature;
    }

    /**
     * Sets the specimen signature.
     *
     * @param specimenSignature the new specimen signature
     */
    public void setSpecimenSignature(final String specimenSignature) {
        this.specimenSignature = specimenSignature;
    }

    /**
     * Gets the permanent address.
     *
     * @return the permanent address
     */
    public Address getPermanentAddress() {
        return permanentAddress;
    }

    /**
     * Sets the permanent address.
     *
     * @param permanentAddress the new permanent address
     */
    public void setPermanentAddress(final Address permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    /**
     * Gets the present address.
     *
     * @return the present address
     */
    public Address getPresentAddress() {
        return presentAddress;
    }

    /**
     * Sets the present address.
     *
     * @param presentAddress the new present address
     */
    public void setPresentAddress(final Address presentAddress) {
        this.presentAddress = presentAddress;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     *
     * @param contact the new contact
     */
    public void setContact(final Contact contact) {
        this.contact = contact;
    }

}
