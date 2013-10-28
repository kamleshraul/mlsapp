/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Rule.java
 * Created On: May 8, 2013
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Rule.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "rules")
//@JsonIgnoreProperties({"houseType"})
public class Rule extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;

    /** The number. */
    @Column(length = 600)
    private Integer number;
    
    private Integer editionYear;
    
    private Integer editionNumber;
    
    /** The title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="rules_titles",
    joinColumns={@JoinColumn(name="rule_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="title_id", referencedColumnName="id")})
    private List<TextDraft> titles;
    
    /** title in default language to show in grid or so **/
    @Transient
    private String defaultTitle;
    
    /** The rule pdf in english. */
	@Column(length = 100)
	private String fileEnglish;   
	
	/** The rule pdf in marathi. */
	@Column(length = 100)
	private String fileMarathi;

    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new rule.
     */
    public Rule() {
        super();
    }

    /**
     * Instantiates a new rule.
     * 
     * @param number the number
     * @param locale the locale
     * @param version the version
     */
    public Rule(final Integer number, final String locale, final Long version) {
        super();
        this.number = number;

    }

    // ----------------------------Domain Methods-------------------------//
    public String getDefaultTitle() {
    	String defaultTitle = "";
    	CustomParameter ruleDefaultLanguageParameter = CustomParameter.findByName(CustomParameter.class, "RULE_DEFAULT_LANGUAGE", "");
    	if(ruleDefaultLanguageParameter!=null) {
    		String actDefaultLanguage = ruleDefaultLanguageParameter.getValue();
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
    public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}
	
	public Integer getNumber() {
        return number;
    }

    public void setNumber(final Integer number) {
        this.number = number;
    }

	public Integer getEditionYear() {
		return editionYear;
	}

	public void setEditionYear(Integer editionYear) {
		this.editionYear = editionYear;
	}

	public Integer getEditionNumber() {
		return editionNumber;
	}

	public void setEditionNumber(Integer editionNumber) {
		this.editionNumber = editionNumber;
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
}
