/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ApplicationLocale.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class ApplicationLocale.
 * 
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "locales")
public class ApplicationLocale extends BaseDomain implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The language. */
    @Column(length = 3)
    private String language;

    /** The country. */
    @Column(length = 3)
    private String country;

    /** The variant. */
    @Column(length = 3)
    private String variant;

    /** The display name. */
    @Column(length = 90)
    private String displayName;
    
    /** The language type. */
    @Column(length = 90)
    private String languageType;

    // ==================== Constructors ====================
    /**
     * Instantiates a new application locale.
     */
    public ApplicationLocale() {
        super();
        this.variant = "";
    }

    /**
     * Instantiates a new application locale.
     * 
     * @param language the language
     * @param country the country
     * @param displayName the display name
     */
    public ApplicationLocale(final String language, final String country,
            final String displayName) {
        super();
        this.language = language;
        this.country = country;
        this.variant = "";
        this.displayName = displayName;
    }

    /**
     * Instantiates a new application locale.
     * 
     * @param language the language
     * @param country the country
     * @param variant the variant
     * @param displayName the display name
     */
    public ApplicationLocale(final String language, final String country,
            final String variant, final String displayName) {
        super();
        this.language = language;
        this.country = country;
        this.variant = variant;
        this.displayName = displayName;
    }
    
    /**
     * Instantiates a new application locale.
     * 
     * @param language the language
     * @param country the country
     * @param variant the variant
     * @param displayName the display name
     * @param displayName the language type
     */
    public ApplicationLocale(final String language, final String country,
            final String variant, final String displayName, final String languageType) {
        super();
        this.language = language;
        this.country = country;
        this.variant = variant;
        this.displayName = displayName;
        this.languageType = languageType;
    }

    // ==================== Domain Methods ====================
    /**
     * Gets the locale string.
     * 
     * @return the locale string
     */
    public String getLocaleString() {
        if (!variant.isEmpty()) {
            return this.language + "_" + this.country + "_" + this.variant;
        }
        else {
            return this.language + "_" + this.country;
        }
    }

    // ==================== Getters & Setters ====================
    /**
     * Gets the language.
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     * 
     * @param language the new language
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Gets the country.
     * 
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     * 
     * @param country the new country
     */
    public void setCountry(final String country) {
        this.country = country.toUpperCase();
    }

    /**
     * Gets the variant.
     * 
     * @return the variant
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the variant.
     * 
     * @param variant the new variant
     */
    public void setVariant(final String variant) {
        this.variant = variant;
    }

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     * 
     * @param displayName the new display name
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the language type.
     * 
     * @return the language type
     */
    public String getLanguageType() {
		return languageType;
	}

    /**
     * Sets the language type.
     * 
     * @param languageType the new language type
     */
	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}
}
