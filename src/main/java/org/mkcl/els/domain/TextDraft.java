package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "text_drafts")
@JsonIgnoreProperties({"language"})
public class TextDraft extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The text. */
    @Column(length=30000)
    private String text;
    
    /** The text in short. */
    @Column(length=10000)
    private String shortText;
    
    /** The file having the text. */
	@Column(length = 100)
	private String file;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;
    
    public TextDraft() {
    	
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getShortText() {
		return shortText;
	}

	public void setShortText(String shortText) {
		this.shortText = shortText;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}    

}
