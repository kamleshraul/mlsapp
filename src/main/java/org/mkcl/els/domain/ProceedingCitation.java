package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="proceeding_citation")
public class ProceedingCitation extends BaseDomain implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String text;

	public ProceedingCitation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ProceedingCitation(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
