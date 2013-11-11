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
@JsonIgnoreProperties({"reporters", "itineraries", "status",
	"internalStatusLH", "recommendationStatusLH",
	"internalStatusUH", "recommendationStatusUH"})
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
	
	// "Request for Tour" as raised in LowerHouse 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_lh_id")
	private Status internalStatusLH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_lh_id")
	private Status recommendationStatusLH;
	
	@Column(length=30000)
	private String remarksLH;
	
	// "Request for Tour" as raised in UpperHouse
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_uh_id")
	private Status internalStatusUH;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_uh_id")
	private Status recommendationStatusUH;
	
	@Column(length=30000)
	private String remarksUH;
	
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

	public Status getInternalStatusLH() {
		return internalStatusLH;
	}

	public void setInternalStatusLH(final Status internalStatusLH) {
		this.internalStatusLH = internalStatusLH;
	}

	public Status getRecommendationStatusLH() {
		return recommendationStatusLH;
	}

	public void setRecommendationStatusLH(final Status recommendationStatusLH) {
		this.recommendationStatusLH = recommendationStatusLH;
	}

	public String getRemarksLH() {
		return remarksLH;
	}

	public void setRemarksLH(final String remarksLH) {
		this.remarksLH = remarksLH;
	}

	public Status getInternalStatusUH() {
		return internalStatusUH;
	}

	public void setInternalStatusUH(final Status internalStatusUH) {
		this.internalStatusUH = internalStatusUH;
	}

	public Status getRecommendationStatusUH() {
		return recommendationStatusUH;
	}

	public void setRecommendationStatusUH(final Status recommendationStatusUH) {
		this.recommendationStatusUH = recommendationStatusUH;
	}

	public String getRemarksUH() {
		return remarksUH;
	}

	public void setRemarksUH(final String remarksUH) {
		this.remarksUH = remarksUH;
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