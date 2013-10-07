/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Language.java
 * Created On: Mar 13, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Language.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "languages")
public class Language extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The language. */
    @Column(length = 300)
    private String name;
    
    /** The type. */
    @Column(length = 300)
    private String type;
    
    private Integer priority;
    
    @Autowired
    private transient LanguageRepository languageRepository;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new language.
     */
    public Language() {
        super();
    }

    /**
     * Instantiates a new language.
     *
     * @param language the language
     */
    public Language(final String name) {
        super();
        this.name = name;
    }

    // -------------------------------Domain_Methods----------------------------------------------
    public static LanguageRepository getLanguageRepository() {
    	LanguageRepository languageRepository = new Language().languageRepository;
        if (languageRepository == null) {
            throw new IllegalStateException(
                    "LanguageRepository has not been injected in Language Domain");
        }
        return languageRepository;
    }
    
	public static List<Language> findAllSortedByPriorityAndName(String locale) throws ELSException{
		return getLanguageRepository().findAllSortedByPriorityAndName(locale);
	}
	
	public static List<Language> findAllLanguagesByModule(final String module,final String locale) throws ELSException {
		return getLanguageRepository().findAllLanguagesByModule(module,locale);
	} 
    // ------------------------------------------Getters/Setters-----------------------------------
    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setName(final String name) {
        this.name = name;
    }

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}   
}
