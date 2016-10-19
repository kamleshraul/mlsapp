package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="proceeding_citation")
public class ProceedingCitation extends BaseDomain implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Column(length=30000)
	private String title;
	
	@Column(length=30000)
	private String content;

	public ProceedingCitation() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}
