/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Reference.java
 * Created On: Jan 6, 2012
 */
package org.mkcl.els.common.vo;

/**
 * The Class SectionAmendmentVO.
 * @author dhananjayb
 * @version v1.0.0
 */
public class SectionAmendmentVO {

    // ---------------------------------Attributes-------------------------------------------------
	/** The language. */
	private String language;
	
	/** The language name. */
	private String languageName;
	
    /** The amended section number. */
    private String amendedSectionNumber;
    
    /** The amending content. */
    private String amendingContent;

    // ---------------------------------Constructors-----------------------------------------------
    /**
     * Instantiates a new sectionAmendmentVO.
     */
    public SectionAmendmentVO() {
        super();
    }

    // ------------------------------------------Getters/Setters-----------------------------------
    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
    
    public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getAmendedSectionNumber() {
		return amendedSectionNumber;
	}

	public void setAmendedSectionNumber(String amendedSectionNumber) {
		this.amendedSectionNumber = amendedSectionNumber;
	}

	public String getAmendingContent() {
		return amendingContent;
	}

	public void setAmendingContent(String amendingContent) {
		this.amendingContent = amendingContent;
	}
	
}
