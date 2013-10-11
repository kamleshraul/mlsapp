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
import org.mkcl.els.repository.CommitteeTourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_tours")
@JsonIgnoreProperties({"town", "reporters", "itineraries"})
public class CommitteeTour extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 3676106504296627183L;

	//=============== ATTRIBUTES ===============
	@Column(length=900)
	private String venueName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="town_id")
	private Town town;
	
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
	
	@Autowired
	private transient CommitteeTourRepository repository;
	
	//=============== CONSTRUCTORS =============
	public CommitteeTour() {
		super();
		this.setReporters(new ArrayList<CommitteeReporter>());
		this.setItineraries(new ArrayList<TourItinerary>());
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	public static CommitteeTour find(final Town town, 
			final String venueName,
			final Date fromDate, 
			final Date toDate, 
			final String subject, 
			final String locale) {
		return CommitteeTour.getRepository().find(town, venueName, 
				fromDate, toDate, subject, locale);
	}
	
	//=============== INTERNAL METHODS =========
	private static CommitteeTourRepository getRepository() {
		CommitteeTourRepository repository = new CommitteeTour().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"CommitteeTourRepository has not been injected in" +
				" CommitteeTour Domain");
		}
		
		return repository;
	}
	
	//=============== GETTERS/SETTERS ==========
	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(final String venueName) {
		this.venueName = venueName;
	}

	public Town getTown() {
		return town;
	}

	public void setTown(final Town town) {
		this.town = town;
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