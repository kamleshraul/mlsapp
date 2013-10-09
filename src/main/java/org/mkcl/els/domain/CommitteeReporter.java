package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_reporters")
@JsonIgnoreProperties({"language"})
public class CommitteeReporter extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -2858152386157007010L;

	//=============== ATTRIBUTES ===============
	private Integer noOfReporters;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="language_id")
	private Language language;
	
	//=============== CONSTRUCTORS =============
	public CommitteeReporter() {
		super();
	}
	
	public CommitteeReporter(final Integer noOfReporters,
			final Language language,
			final String locale) {
		super(locale);
		this.setNoOfReporters(noOfReporters);
		this.setLanguage(language);
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	
	//=============== INTERNAL METHODS =========
	
	//=============== GETTERS/SETTERS ==========
	public Integer getNoOfReporters() {
		return noOfReporters;
	}

	public void setNoOfReporters(final Integer noOfReporters) {
		this.noOfReporters = noOfReporters;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(final Language language) {
		this.language = language;
	}
	
}