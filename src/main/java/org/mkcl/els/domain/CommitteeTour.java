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
@Table(name="committee_tours")
@JsonIgnoreProperties({"venueTown", "reporters", "itineraries"})
public class CommitteeTour extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 3676106504296627183L;

	//=============== ATTRIBUTES ===============
	@Column(length=900)
	private String venueName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="venue_town_id")
	private Town venueTown;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;
	
	@Column(length=3000)
	private String subject;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committee_tours_committee_reporters",
			joinColumns={@JoinColumn(name="committee_tour_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="committee_reporter_id",
					referencedColumnName="id")})
	private List<CommitteeReporter> reporters;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="committee_tours_tour_itineraries",
			joinColumns={@JoinColumn(name="committee_tour_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="tour_itinerary_id",
					referencedColumnName="id")})
	private List<TourItinerary> itineraries;
	
	//=============== CONSTRUCTORS =============
	public CommitteeTour() {
		super();
		this.setReporters(new ArrayList<CommitteeReporter>());
		this.setItineraries(new ArrayList<TourItinerary>());
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	
	//=============== INTERNAL METHODS =========
	
	//=============== GETTERS/SETTERS ==========
	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(final String venueName) {
		this.venueName = venueName;
	}

	public Town getVenueTown() {
		return venueTown;
	}

	public void setVenueTown(final Town venueTown) {
		this.venueTown = venueTown;
	}

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
	
}