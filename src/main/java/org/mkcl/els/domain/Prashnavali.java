package org.mkcl.els.domain;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.repository.PrashnavaliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="prashnavalis")
@JsonIgnoreProperties({"questionAnswers","credential", "internalStatus", "recommendationStatus", "status", "houseType", "committeeMember","committee"})
public class Prashnavali extends BaseDomain implements Serializable{
	
	private static final long serialVersionUID = -717179637452424318L;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;
	
	@Column(length=10000)
	private String prashnavaliName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committee_member_id")
	private CommitteeMember committeeMember;
	
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="credential_id")
	private Credential credential;
	
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="prashnavali_prashnavali_information",
    joinColumns={@JoinColumn(name="prashnavali_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="prashnavali_information_id", referencedColumnName="id")})
	private List<PrashnavaliInformation> questionAnswers;
	
	/* Work flow Attributes */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	// "Request for Tour" as raised in LowerHouse 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internal_status_id")
	private Status internalStatus;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendation_status_id")
	private Status recommendationStatus;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committee_id")
	private Committee committee;
	
	@Autowired
	private transient PrashnavaliRepository repository;
	
	
	@Override
	public BaseDomain persist() {
				
		if(this.getQuestionAnswers() != null 
				&& !this.getQuestionAnswers().isEmpty() 
				&& this.getQuestionAnswers().size() > 0){
			Status CREATED = Status.findByType(
					ApplicationConstants.PRASHNAVALI_CREATED, 
					this.getLocale());
			this.setInternalStatus(CREATED);
			this.setRecommendationStatus(CREATED);
			this.setStatus(CREATED);
		}else{
			Status INCOMPLETE = Status.findByType(
					ApplicationConstants.PRASHNAVALI_INCOMPLETE, 
					this.getLocale());
			this.setInternalStatus(INCOMPLETE);
			this.setRecommendationStatus(INCOMPLETE);
			this.setStatus(INCOMPLETE);
		}
		
		return super.persist();
	}
	
	@Override
	public BaseDomain merge() {
		Status status = this.getStatus();		
		
		if(status == null || 
				(status != null 
					&& status.getType().equals(
							ApplicationConstants.PRASHNAVALI_INCOMPLETE))) {
			
			HouseType houseType = this.getHouseType();
			if(this.getQuestionAnswers() != null && !this.getQuestionAnswers().isEmpty()
					&& this.getQuestionAnswers().size() > 0) {
				Status CREATED = Status.findByType(
						ApplicationConstants.PRASHNAVALI_CREATED, 
						this.getLocale());
				
					this.setInternalStatus(CREATED);
					this.setStatus(CREATED);
			}
			else {
				Status INCOMPLETE = Status.findByType(
						ApplicationConstants.PRASHNAVALI_INCOMPLETE, 
						this.getLocale());
				
				this.setInternalStatus(INCOMPLETE);
				this.setStatus(INCOMPLETE);
			}
		}
		
		return super.merge();
	}
	
	//=============== INTERNAL METHODS =========
	private static PrashnavaliRepository getRepository() {
		PrashnavaliRepository repository = new Prashnavali().repository;
		
		if(repository == null) {
			throw new IllegalStateException("PrashnavaliRepository has not been injected in Prashnavali Domain");
		}
		
		return repository;
	}
	public static List<Prashnavali> findActivePrashnavalis(
			final HouseType houseType,
			final Boolean isIncludeBothHouseType,
			final Date onDate, 
			final String locale) {
		Status status = Status.findByType(
				ApplicationConstants.PRASHNAVALI_FINAL_ADMISSION, locale);
		return Prashnavali.getRepository().findActivePrashnavalis(houseType, 
				status, locale);
	}
	
	public Prashnavali() {
		super();
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public List<PrashnavaliInformation> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(List<PrashnavaliInformation> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPrashnavaliName() {
		return prashnavaliName;
	}

	public void setPrashnavaliName(String prashnavaliName) {
		this.prashnavaliName = prashnavaliName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}
	
	public String formatCreateDate(final String format, final String locale){
		return FormaterUtil.formatDateToString(this.getCreateDate(), format, locale);
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public CommitteeMember getCommitteeMember() {
		return committeeMember;
	}

	public void setCommitteeMember(CommitteeMember committeeMember) {
		this.committeeMember = committeeMember;
	}

	public Committee getCommittee() {
		return committee;
	}

	public void setCommittee(Committee committee) {
		this.committee = committee;
	}
	
	
}
