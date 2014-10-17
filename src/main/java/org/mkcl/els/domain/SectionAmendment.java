package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "sectionamendments")
public class SectionAmendment extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//=============== BASIC ATTRIBUTES ====================
	/** The section number. */
    @Column(length = 300)
	private String sectionNumber; //needed if sections are not saved explicitly
    
    /** The amended section. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="amendedsection_id")
	private Section amendedSection;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;
	
	/** The content. */
    @Column(length=30000)
	private String amendingContent;    

    //=============== Getters & Setters ====================
    public String getSectionNumber() {
		return sectionNumber;
	}

	public void setSectionNumber(String sectionNumber) {
		this.sectionNumber = sectionNumber;
	}

	public Section getAmendedSection() {
		return amendedSection;
	}

	public void setAmendedSection(Section amendedSection) {
		this.amendedSection = amendedSection;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getAmendingContent() {
		return amendingContent;
	}

	public void setAmendingContent(String amendingContent) {
		this.amendingContent = amendingContent;
	}

}
