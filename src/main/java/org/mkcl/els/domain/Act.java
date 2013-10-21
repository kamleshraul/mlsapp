/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Act.java
 * Created On: June 20, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mkcl.els.repository.ActRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Act.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "acts")
public class Act extends Device implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The number. */    
    private Integer number;
    
    /** The year. */    
    private Integer year;
    
    /** The title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="acts_titles",
    joinColumns={@JoinColumn(name="act_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="title_id", referencedColumnName="id")})
    private List<TextDraft> titles;
    
    /** title in default language to show in grid or so **/
    @Transient
    private String defaultTitle;
    
    /** The act pdf in english. */
	@Column(length = 100)
	private String fileEnglish;   
	
	/** The act pdf in marathi. */
	@Column(length = 100)
	private String fileMarathi;
	
	/** The act pdf in hindi. */
	@Column(length = 100)
	private String fileHindi;
    
	/** The act repository. */
	@Autowired
	private transient ActRepository actRepository;
	
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new act.
     */
    public Act() {
        super();
    }

    // ----------------------------Domain Methods-------------------------//
    /**
	 * Gets the act repository.
	 *
	 * @return the act repository
	 */
	public static ActRepository getActRepository() {
		ActRepository actRepository = new Act().actRepository;
		if (actRepository == null) {
			throw new IllegalStateException(
					"ActRepository has not been injected in Act Domain");
		}
		return actRepository;
	}
	
	public String getDefaultTitle() {
    	String defaultTitle = "";
    	CustomParameter actDefaultLanguageParameter = CustomParameter.findByName(CustomParameter.class, "ACT_DEFAULT_LANGUAGE", "");
    	if(actDefaultLanguageParameter!=null) {
    		String actDefaultLanguage = actDefaultLanguageParameter.getValue();
    		if(actDefaultLanguage!=null) {
    			if(!actDefaultLanguage.isEmpty()) {
    				if(this.getTitles()!=null) {
    	        		if(!this.getTitles().isEmpty()) {
    	    				for(TextDraft td: this.getTitles()) {
    	            			if(td.getLanguage().getType().equals(actDefaultLanguage)) {
    	            				defaultTitle = td.getText();
    	            				break;
    	            			}
    	            		}
    	    			}
    				}
    			}
    		}
    	}    	    	
    	return defaultTitle;
    }

    // ----------------------------Getters/Setters------------------------//
    /**
     * Gets the number.
     * 
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the number.
     * 
     * @param name the new number
     */
    public void setNumber(final Integer number) {
        this.number = number;
    }

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<TextDraft> getTitles() {
		return titles;
	}

	public void setTitles(List<TextDraft> titles) {
		this.titles = titles;
	}

	public String getFileEnglish() {
		return fileEnglish;
	}

	public void setFileEnglish(String fileEnglish) {
		this.fileEnglish = fileEnglish;
	}

	public String getFileMarathi() {
		return fileMarathi;
	}

	public void setFileMarathi(String fileMarathi) {
		this.fileMarathi = fileMarathi;
	}

	public String getFileHindi() {
		return fileHindi;
	}

	public void setFileHindi(String fileHindi) {
		this.fileHindi = fileHindi;
	}
    
}
