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
import org.mkcl.els.repository.OrdinanceRepository;
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
@Table(name = "ordinances")
public class Ordinance extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The number. */    
    private Integer number;
    
    /** The year. */    
    private Integer year;
    
    /** The title. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="ordinances_titles",
    joinColumns={@JoinColumn(name="ordinance_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="title_id", referencedColumnName="id")})
    private List<TextDraft> titles;
    
    /** title in default language to show in grid or so **/
    @Transient
    private String defaultTitle;
    
    /** The ordinance pdf in english. */
	@Column(length = 100)
	private String fileEnglish;   
	
	/** The ordinance pdf in marathi. */
	@Column(length = 100)
	private String fileMarathi;
	
	/** The ordinance pdf in hindi. */
	@Column(length = 100)
	private String fileHindi;
    
	/** The ordinance repository. */
	@Autowired
	private transient OrdinanceRepository ordinanceRepository;
	
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new ordinance.
     */
    public Ordinance() {
        super();
    }

    // ----------------------------Domain Methods-------------------------//
    /**
	 * Gets the ordinance repository.
	 *
	 * @return the ordinance repository
	 */
	private static OrdinanceRepository getOrdinanceRepository() {
		OrdinanceRepository ordinanceRepository = new Ordinance().ordinanceRepository;
		if (ordinanceRepository == null) {
			throw new IllegalStateException(
					"OrdinanceRepository has not been injected in Act Domain");
		}
		return ordinanceRepository;
	}
	
	public String getDefaultTitle() {
    	String defaultTitle = "";
    	CustomParameter ordinanceDefaultLanguageParameter = CustomParameter.findByName(CustomParameter.class, "ORDINANCE_DEFAULT_LANGUAGE", "");
    	if(ordinanceDefaultLanguageParameter!=null) {
    		String ordinanceDefaultLanguage = ordinanceDefaultLanguageParameter.getValue();
    		if(ordinanceDefaultLanguage!=null) {
    			if(!ordinanceDefaultLanguage.isEmpty()) {
    				if(this.getTitles()!=null) {
    	        		if(!this.getTitles().isEmpty()) {
    	    				for(TextDraft td: this.getTitles()) {
    	            			if(td.getLanguage().getType().equals(ordinanceDefaultLanguage)) {
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
	
	public static Ordinance findByYearAndNumber(final Integer ordYear, final Integer ordNumber) {
		return getOrdinanceRepository().findByYearAndNumber(ordYear, ordNumber);
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
