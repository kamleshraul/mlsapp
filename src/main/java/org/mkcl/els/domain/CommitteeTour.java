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
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.repository.CommitteeTourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_tours")
@JsonIgnoreProperties({"committee", "town", "reporters", "itineraries",
	"status", "internalStatusLH", "recommendationStatusLH",
	"internalStatusUH", "recommendationStatusUH", "drafts"})
public class CommitteeTour extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 3676106504296627183L;

	//=============== ATTRIBUTES ===============
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committee_id")
	private Committee committee;
	
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
	
	/* Work flow Attributes */
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
	
	/* Audit Log */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(length=1000)
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date editedOn;
	
	@Column(length=1000)
	private String editedAs;
	
	@Column(length=1000)
	private String editedBy;
	
	/* Drafts */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name="committee_tours_drafts_association", 
    		joinColumns={@JoinColumn(name="committee_tour_id", 
    				referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="committee_tour_draft_id", 
    				referencedColumnName="id")})
	private List<CommitteeTourDraft> drafts;
	
	@Autowired
	private transient CommitteeTourRepository repository;
	
	//=============== CONSTRUCTORS =============
	public CommitteeTour() {
		super();
		this.setReporters(new ArrayList<CommitteeReporter>());
		this.setItineraries(new ArrayList<TourItinerary>());
		this.setDrafts(new ArrayList<CommitteeTourDraft>());
	}
	
	//=============== VIEW METHODS =============
//	public String formatFromDate() {
//		String retVal = "";
//
//		Date fromDate = this.getFromDate();
//		if(fromDate != null){
//			CustomParameter dateTimeFormat =
//				CustomParameter.findByName(CustomParameter.class, 
//						ApplicationConstants.SERVER_DATETIMEFORMAT, 
//						"");
//
//			if(dateTimeFormat != null) {
//				String format = dateTimeFormat.getValue();
//				retVal = FormaterUtil.formatDateToString(fromDate, format);
//			}
//		}
//
//		return retVal;
//	}
//
//	public String formatToDate(){
//		String retVal = "";
//
//		Date toDate = this.getToDate();
//		if(toDate != null){
//			CustomParameter dateTimeFormat =
//				CustomParameter.findByName(CustomParameter.class, 
//						ApplicationConstants.SERVER_DATETIMEFORMAT, 
//						"");
//
//			if(dateTimeFormat != null) {
//				String format = dateTimeFormat.getValue();
//				retVal = FormaterUtil.formatDateToString(toDate, format);
//			}
//		}
//
//		return retVal;
//	}
	
	//=============== DOMAIN METHODS ===========
//	public static CommitteeTour find(final Town town, 
//			final String venueName,
//			final Date fromDate, 
//			final Date toDate, 
//			final String subject, 
//			final String locale) {
//		return CommitteeTour.getRepository().find(town, venueName, 
//				fromDate, toDate, subject, locale);
//	}
	
	public static CommitteeTour find(final Committee committee, 
			final Date fromDate,
			final String locale) {
		return CommitteeTour.getRepository().find(committee, fromDate, locale);
	}
	
	@Override
	public BaseDomain persist() {
		Committee committee = this.getCommittee();
		CommitteeName committeeName = committee.getCommitteeName();
		CommitteeType committeeType = committeeName.getCommitteeType();
		HouseType houseType = committeeType.getHouseType();
		
		if(this.getVenueName() != null
				&& this.getTown() != null
				&& this.getFromDate() != null
				&& this.getToDate() != null
				&& this.getSubject() != null
				&& ! this.getReporters().isEmpty()
				&& ! this.getItineraries().isEmpty()) {
			Status CREATED = Status.findByType(
					ApplicationConstants.COMMITTEETOUR_CREATED, 
					this.getLocale());
			
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				this.setInternalStatusLH(CREATED);
				this.setStatus(CREATED);
			}
			else if(houseType.getType().equals(
					ApplicationConstants.UPPER_HOUSE)) {
				this.setInternalStatusUH(CREATED);
				this.setStatus(CREATED);
			}
			else if(houseType.getType().equals(
					ApplicationConstants.BOTH_HOUSE)) {
				this.setInternalStatusLH(CREATED);
				this.setInternalStatusUH(CREATED);
				this.setStatus(CREATED);
			}
		}
		else {
			Status INCOMPLETE = Status.findByType(
					ApplicationConstants.COMMITTEETOUR_INCOMPLETE, 
					this.getLocale());
			
			if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
				this.setInternalStatusLH(INCOMPLETE);
				this.setStatus(INCOMPLETE);
			}
			else if(houseType.getType().equals(
					ApplicationConstants.UPPER_HOUSE)) {
				this.setInternalStatusUH(INCOMPLETE);
				this.setStatus(INCOMPLETE);
			}
			else if(houseType.getType().equals(
					ApplicationConstants.BOTH_HOUSE)) {
				this.setInternalStatusLH(INCOMPLETE);
				this.setInternalStatusUH(INCOMPLETE);
				this.setStatus(INCOMPLETE);
			}
		}
		
		return super.persist();
	}
	
	@Override
	public BaseDomain merge() {
		Status status = this.getStatus();		
		
		if(status == null || 
				(status != null 
					&& status.getType().equals(
							ApplicationConstants.COMMITTEETOUR_INCOMPLETE))) {
			Committee committee = this.getCommittee();
			CommitteeName committeeName = committee.getCommitteeName();
			CommitteeType committeeType = committeeName.getCommitteeType();
			HouseType houseType = committeeType.getHouseType();
			if(this.getVenueName() != null
					&& this.getTown() != null
					&& this.getFromDate() != null
					&& this.getToDate() != null
					&& this.getSubject() != null
					&& ! this.getReporters().isEmpty()
					&& ! this.getItineraries().isEmpty()) {
				Status CREATED = Status.findByType(
						ApplicationConstants.COMMITTEETOUR_CREATED, 
						this.getLocale());
				
				if(houseType.getType().equals(
						ApplicationConstants.LOWER_HOUSE)) {
					this.setInternalStatusLH(CREATED);
					this.setStatus(CREATED);
				}
				else if(houseType.getType().equals(
						ApplicationConstants.UPPER_HOUSE)) {
					this.setInternalStatusUH(CREATED);
					this.setStatus(CREATED);
				}
				else if(houseType.getType().equals(
						ApplicationConstants.BOTH_HOUSE)) {
					this.setInternalStatusLH(CREATED);
					this.setInternalStatusUH(CREATED);
					this.setStatus(CREATED);
				}
			}
			else {
				Status INCOMPLETE = Status.findByType(
						ApplicationConstants.COMMITTEETOUR_INCOMPLETE, 
						this.getLocale());
				
				if(houseType.getType().equals(
						ApplicationConstants.LOWER_HOUSE)) {
					this.setInternalStatusLH(INCOMPLETE);
					this.setStatus(INCOMPLETE);
				}
				else if(houseType.getType().equals(
						ApplicationConstants.UPPER_HOUSE)) {
					this.setInternalStatusUH(INCOMPLETE);
					this.setStatus(INCOMPLETE);
				}
				else if(houseType.getType().equals(
						ApplicationConstants.BOTH_HOUSE)) {
					this.setInternalStatusLH(INCOMPLETE);
					this.setInternalStatusUH(INCOMPLETE);
					this.setStatus(INCOMPLETE);
				}
			}
		}
		
		return super.merge();
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
	public Committee getCommittee() {
		return committee;
	}

	public void setCommittee(final Committee committee) {
		this.committee = committee;
	}
	
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
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

	public List<CommitteeTourDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(final List<CommitteeTourDraft> drafts) {
		this.drafts = drafts;
	}
	
}