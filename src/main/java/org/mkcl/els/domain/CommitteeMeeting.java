package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.CommitteeMeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_meetings")
@JsonIgnoreProperties({"committeeMeetingType","prashnavali","committeeTour","committee"})
public class CommitteeMeeting extends BaseDomain implements Serializable{

	private static final long serialVersionUID = -1754534001115968245L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CommitteeMeetingType_id")
	private CommitteeMeetingType committeeMeetingType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committee_id")
	private Committee committee;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prashnavali_id")
	private Prashnavali prashnavali;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committeesubject_id")
	private CommitteeSubject committeeSubject;
	
	
	@Temporal(TemporalType.DATE)
	private Date meetingDate;
	

	private String startTime;
	

	private String endTime;
	
	@OneToOne
	private CommitteeTour committeeTour;
	
	private String meetingLocation;
	
	@Column(name="conciseMinutes", length=30000)
	private String conciseMinutes;
	
	@Column(name="speech", length=30000)
	private String speech;
	
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
	
	@Autowired
	private transient CommitteeMeetingRepository committeeMeetingRepository;

	public CommitteeMeeting() {
		super();
	}
	
	//=============== DOMAIN METHODS ===========	
    private static CommitteeMeetingRepository getCommitteeMeetingRepository() {
    	CommitteeMeetingRepository committeeMeetingRepository = new CommitteeMeeting().committeeMeetingRepository;
        if (committeeMeetingRepository == null) {
            throw new IllegalStateException(
                    "committeeMeetingRepository has not been injected in CommitteeMeeting Domain");
        }
        return committeeMeetingRepository;
    }
	public static List<CommitteeMeeting> find(CommitteeName committeeName,
			String locale) {
		return getCommitteeMeetingRepository().find(committeeName,locale);
	}
	
	public static List<CommitteeMeeting> find(CommitteeName committeeName, String locale, String sortOrder) {
		return getCommitteeMeetingRepository().find(committeeName, locale, sortOrder);
	}

	public CommitteeMeetingType getCommitteeMeetingType() {
		return committeeMeetingType;
	}

	public void setCommitteeMeetingType(CommitteeMeetingType committeeMeetingType) {
		this.committeeMeetingType = committeeMeetingType;
	}

	public Prashnavali getPrashnavali() {
		return prashnavali;
	}

	public void setPrashnavali(Prashnavali prashnavali) {
		this.prashnavali = prashnavali;
	}

	public Committee getCommittee() {
		return committee;
	}

	public void setCommittee(Committee committee) {
		this.committee = committee;
	}

	

	public CommitteeSubject getCommitteeSubject() {
		return committeeSubject;
	}

	public void setCommitteeSubject(CommitteeSubject committeeSubject) {
		this.committeeSubject = committeeSubject;
	}

	public Date getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(Date meetingDate) {
		this.meetingDate = meetingDate;
	}

	

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public CommitteeTour getCommitteeTour() {
		return committeeTour;
	}

	public void setCommitteeTour(CommitteeTour committeeTour) {
		this.committeeTour = committeeTour;
	}

	public String getMeetingLocation() {
		return meetingLocation;
	}

	public void setMeetingLocation(String meetingLocation) {
		this.meetingLocation = meetingLocation;
	}

	public String getConciseMinutes() {
		return conciseMinutes;
	}

	public void setConciseMinutes(String conciseMinutes) {
		this.conciseMinutes = conciseMinutes;
	}
	public String getSpeech() {
		return speech;
	}

	public void setSpeech(String speech) {
		this.speech = speech;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
		
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
		
	public String getCreatedBy() {
		return createdBy;
	}
		
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}	
	
	public Date getEditedOn() {
		return editedOn;
	}
	
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}	
	
	public String getEditedBy() {
		return editedBy;
	}
		
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}
		
	public String getEditedAs() {
		return editedAs;
	}	
	
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

}
