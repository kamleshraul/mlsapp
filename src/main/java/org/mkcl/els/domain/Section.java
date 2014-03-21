package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "sections")
public class Section extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//=============== BASIC ATTRIBUTES ====================	
	/** The number. */
    @Column(length = 300)
	private String number;
	
	/** The language. */
    @Column(length = 300)
	private String language;
	
	/** The text. */
    @Column(length=30000)
	private String text;

    //=============== Getters & Setters ====================
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
