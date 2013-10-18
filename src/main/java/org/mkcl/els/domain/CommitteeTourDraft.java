package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_tour_drafts")
@JsonIgnoreProperties({"reporters", "itineraries"})
public class CommitteeTourDraft extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 8622945016413351860L;

	//=============== ATTRIBUTES ===============
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;
	
	@Column(length=3000)
	private String subject;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committee_tour_drafts_committee_reporters",
			joinColumns={@JoinColumn(name="committee_tour_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_reporter_id",
					referencedColumnName="id")})
	private List<CommitteeReporter> reporters;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committee_tour_drafts_tour_itineraries",
			joinColumns={@JoinColumn(name="committee_tour_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="tour_itinerary_id",
					referencedColumnName="id")})
	private List<TourItinerary> itineraries;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status__id")
	private Status internalStatus;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_id")
	private Status recommendationStatus;
	
	@Column(length=30000)
	private String remarks;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date editedOn;
	
	@Column(length=1000)
	private String editedAs;
	
	@Column(length=1000)
	private String editedBy;

	//=============== CONSTRUCTORS =============
	public CommitteeTourDraft() {
		super();
		this.setItineraries(new ArrayList<TourItinerary>());
		this.setReporters(new ArrayList<CommitteeReporter>());
	}

	//=============== VIEW METHODS =============
	
	
	//=============== DOMAIN METHODS ===========
	
	
	//=============== INTERNAL METHODS =========
	
	
	//=============== GETTERS/SETTERS ==========
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public List<CommitteeReporter> getReporters() {
		return reporters;
	}

	public void setReporters(final List<CommitteeReporter> reporters) {
		this.reporters = reporters;
	}

	public List<TourItinerary> getItineraries() {
		return itineraries;
	}

	public void setItineraries(final List<TourItinerary> itineraries) {
		this.itineraries = itineraries;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(final Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(final Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(final Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(final String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(final String editedBy) {
		this.editedBy = editedBy;
	}
	
}