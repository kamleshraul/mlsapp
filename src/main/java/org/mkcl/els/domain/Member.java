/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Member.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberMinistryAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Member.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@DiscriminatorValue("M")
@JsonIgnoreProperties({ "qualifications", "religion", "languages",
        "familyMembers", "positionsHeld", "reservation", "electionResults",
        "memberPartyAssociations", "memberMinistryAssociations", "books",
        "credential", "title", "maritalStatus", "gender", "professions",
        "nationality", "permanentAddress", "presentAddress", "contact",
        "officeAddress", "houseMemberRoleAssociations" })
public class Member extends Person implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    // ----------------------------------Personal_Informations----------------------------------

    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The alias enabled. */
    private Boolean aliasEnabled;

    /** The alias. */
    @Column(length = 300)
    private String alias;

    /** The religion. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "religion_id")
    private Religion religion;

    /** The languages. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "associations_member_language",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "language_id",
                    referencedColumnName = "id") })
    private List<Language> languages;

    /** The degrees. */
    @ManyToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name = "associations_member_qualification",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "qualification_id",
                    referencedColumnName = "id") })
    private List<Qualification> qualifications;

    /** The family members. */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "associations_member_familymember",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "familymember_id",
                    referencedColumnName = "id") })
    private List<FamilyMember> familyMembers;

    /** The positions held. */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "associations_member_positionheld",
            joinColumns = { @JoinColumn(name = "member_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "positionheld_id",
                    referencedColumnName = "id") })
    private List<PositionHeld> positionsHeld;

    /** The reservation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    /** The caste. */
    @Column(length = 600)
    private String caste;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "officeaddress_id")
    private Address officeAddress;

    // ----------------------------------Other_Informations----------------------------------

    /** The social cultural activities. */
    @Column(length = 30000)
    private String socialCulturalActivities;

    /** The literary artistic scientific accomplishments. */
    @Column(length = 30000)
    private String literaryArtisticScientificAccomplishments;

    /** The hobby special interests. */
    @Column(length = 30000)
    private String hobbySpecialInterests;

    /** The favorite pastime recreation. */
    @Column(length = 30000)
    private String favoritePastimeRecreation;

    /** The sports clubs. */
    @Column(length = 30000)
    private String sportsClubs;

    /** The countries visited. */
    @Column(length = 30000)
    private String countriesVisited;

    /** The other information. */
    @Column(length = 30000)
    private String otherInformation;

    @Column(length = 30000)
    private String educationalCulturalActivities;

    // ----------------------------------Election_Results_Informations----------------------------------
    /** The election results. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<ElectionResult> electionResults;

    // ----------------------------------Party_Informations----------------------------------
    /** The member party associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<MemberPartyAssociation> memberPartyAssociations;

    // ----------------------------------Ministry_Informations----------------------------------
    /** The member ministry associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<MemberMinistryAssociation> memberMinistryAssociations;

    // ----------------------------------Ministry_Informations----------------------------------
    /** The house member role associations. */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    private List<HouseMemberRoleAssociation> houseMemberRoleAssociations;

    // ----------------------------------Book_Informations----------------------------------
    /** The books. */
    @ManyToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name = "associations_member_book", joinColumns = @JoinColumn(
            name = "member_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id",
                    referencedColumnName = "id"))
    private List<Book> books;

    // ----------------------------------Credential_Informations----------------------------------
    /** The credential. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credentialId")
    private Credential credential;
    
    @Column(length=100)
    private String status;

    // ---------------------------------Constructors----------------------------------------------

    /**
     * Instantiates a new member.
     */
    public Member() {
        super();
        this.aliasEnabled = false;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    // ------------------------------------------Getters/Setters-----------------------------------

    /**
     * Gets the religion.
     *
     * @return the religion
     */
    public Religion getReligion() {
        return religion;
    }

    /**
     * Sets the religion.
     *
     * @param religion the new religion
     */
    public void setReligion(final Religion religion) {
        this.religion = religion;
    }

    /**
     * Gets the languages.
     *
     * @return the languages
     */
    public List<Language> getLanguages() {
        return languages;
    }

    /**
     * Sets the languages.
     *
     * @param languages the new languages
     */
    public void setLanguages(final List<Language> languages) {
        this.languages = languages;
    }

    public List<Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(final List<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    public void setAliasEnabled(final Boolean aliasEnabled) {
        this.aliasEnabled = aliasEnabled;
    }

    /**
     * Gets the family members.
     *
     * @return the family members
     */
    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    /**
     * Sets the family members.
     *
     * @param familyMembers the new family members
     */
    public void setFamilyMembers(final List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    /**
     * Gets the positions held.
     *
     * @return the positions held
     */
    public List<PositionHeld> getPositionsHeld() {
        return positionsHeld;
    }

    /**
     * Sets the positions held.
     *
     * @param positionsHeld the new positions held
     */
    public void setPositionsHeld(final List<PositionHeld> positionsHeld) {
        this.positionsHeld = positionsHeld;
    }

    /**
     * Gets the reservation.
     *
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Sets the reservation.
     *
     * @param reservation the new reservation
     */
    public void setReservation(final Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * Gets the alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     *
     * @param alias the new alias
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Gets the social cultural activities.
     *
     * @return the social cultural activities
     */
    public String getSocialCulturalActivities() {
        return socialCulturalActivities;
    }

    /**
     * Sets the social cultural activities.
     *
     * @param socialCulturalActivities the new social cultural activities
     */
    public void setSocialCulturalActivities(final String socialCulturalActivities) {
        this.socialCulturalActivities = socialCulturalActivities;
    }

    /**
     * Gets the literary artistic scientific accomplishments.
     *
     * @return the literary artistic scientific accomplishments
     */
    public String getLiteraryArtisticScientificAccomplishments() {
        return literaryArtisticScientificAccomplishments;
    }

    /**
     * Sets the literary artistic scientific accomplishments.
     *
     * @param literaryArtisticScientificAccomplishments the new literary
     *        artistic scientific accomplishments
     */
    public void setLiteraryArtisticScientificAccomplishments(
            final String literaryArtisticScientificAccomplishments) {
        this.literaryArtisticScientificAccomplishments = literaryArtisticScientificAccomplishments;
    }

    /**
     * Gets the hobby special interests.
     *
     * @return the hobby special interests
     */
    public String getHobbySpecialInterests() {
        return hobbySpecialInterests;
    }

    /**
     * Sets the hobby special interests.
     *
     * @param hobbySpecialInterests the new hobby special interests
     */
    public void setHobbySpecialInterests(final String hobbySpecialInterests) {
        this.hobbySpecialInterests = hobbySpecialInterests;
    }

    /**
     * Gets the favorite pastime recreation.
     *
     * @return the favorite pastime recreation
     */
    public String getFavoritePastimeRecreation() {
        return favoritePastimeRecreation;
    }

    /**
     * Sets the favorite pastime recreation.
     *
     * @param favoritePastimeRecreation the new favorite pastime recreation
     */
    public void setFavoritePastimeRecreation(final String favoritePastimeRecreation) {
        this.favoritePastimeRecreation = favoritePastimeRecreation;
    }

    /**
     * Gets the sports clubs.
     *
     * @return the sports clubs
     */
    public String getSportsClubs() {
        return sportsClubs;
    }

    /**
     * Sets the sports clubs.
     *
     * @param sportsClubs the new sports clubs
     */
    public void setSportsClubs(final String sportsClubs) {
        this.sportsClubs = sportsClubs;
    }

    /**
     * Gets the countries visited.
     *
     * @return the countries visited
     */
    public String getCountriesVisited() {
        return countriesVisited;
    }

    /**
     * Sets the countries visited.
     *
     * @param countriesVisited the new countries visited
     */
    public void setCountriesVisited(final String countriesVisited) {
        this.countriesVisited = countriesVisited;
    }

    /**
     * Gets the other information.
     *
     * @return the other information
     */
    public String getOtherInformation() {
        return otherInformation;
    }

    /**
     * Sets the other information.
     *
     * @param otherInformation the new other information
     */
    public void setOtherInformation(final String otherInformation) {
        this.otherInformation = otherInformation;
    }

    /**
     * Gets the election results.
     *
     * @return the election results
     */
    public List<ElectionResult> getElectionResults() {
        return electionResults;
    }

    /**
     * Sets the election results.
     *
     * @param electionResults the new election results
     */
    public void setElectionResults(final List<ElectionResult> electionResults) {
        this.electionResults = electionResults;
    }

    /**
     * Gets the member party associations.
     *
     * @return the member party associations
     */
    public List<MemberPartyAssociation> getMemberPartyAssociations() {
        return memberPartyAssociations;
    }

    /**
     * Sets the member party associations.
     *
     * @param memberPartyAssociations the new member party associations
     */
    public void setMemberPartyAssociations(
            final List<MemberPartyAssociation> memberPartyAssociations) {
        this.memberPartyAssociations = memberPartyAssociations;
    }

    /**
     * Gets the member ministry associations.
     *
     * @return the member ministry associations
     */
    public List<MemberMinistryAssociation> getMemberMinistryAssociations() {
        return memberMinistryAssociations;
    }

    /**
     * Sets the member ministry associations.
     *
     * @param memberMinistryAssociations the new member ministry associations
     */
    public void setMemberMinistryAssociations(
            final List<MemberMinistryAssociation> memberMinistryAssociations) {
        this.memberMinistryAssociations = memberMinistryAssociations;
    }

    /**
     * Gets the house member role associations.
     *
     * @return the house member role associations
     */
    public List<HouseMemberRoleAssociation> getHouseMemberRoleAssociations() {
        return houseMemberRoleAssociations;
    }

    /**
     * Sets the house member role associations.
     *
     * @param houseMemberRoleAssociations the new house member role associations
     */
    public void setHouseMemberRoleAssociations(
            final List<HouseMemberRoleAssociation> houseMemberRoleAssociations) {
        this.houseMemberRoleAssociations = houseMemberRoleAssociations;
    }

    /**
     * Gets the books.
     *
     * @return the books
     */
    public List<Book> getBooks() {
        return books;
    }

    /**
     * Sets the books.
     *
     * @param books the new books
     */
    public void setBooks(final List<Book> books) {
        this.books = books;
    }

    /**
     * Gets the alias enabled.
     *
     * @return the alias enabled
     */
    public Boolean getAliasEnabled() {
        return aliasEnabled;
    }

    /**
     * Gets the credential.
     *
     * @return the credential
     */
    public Credential getCredential() {
        return credential;
    }

    /**
     * Sets the credential.
     *
     * @param credential the new credential
     */
    public void setCredential(final Credential credential) {
        this.credential = credential;
    }

    /**
     * Gets the caste.
     *
     * @return the caste
     */
    public String getCaste() {
        return caste;
    }

    /**
     * Sets the caste.
     *
     * @param caste the new caste
     */
    public void setCaste(final String caste) {
        this.caste = caste;
    }

    public Address getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(final Address officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getEducationalCulturalActivities() {
        return educationalCulturalActivities;
    }

    public void setEducationalCulturalActivities(
            final String educationalCulturalActivities) {
        this.educationalCulturalActivities = educationalCulturalActivities;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
