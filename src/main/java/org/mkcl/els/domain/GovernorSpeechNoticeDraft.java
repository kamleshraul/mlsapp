package org.mkcl.els.domain;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.*;
import javax.persistence.*;
import java.util.*;

@Configurable
@Entity
@Table(name="governorspeechnotice_drafts")
public class GovernorSpeechNoticeDraft extends BaseDomain implements Serializable{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID= 1L ;
	
	/** The subject. */
    @Column(length=30000)
    private String subject;

    /** The notice content. */
    @Column(length=30000)
    private String noticeContent;
    
    /** The status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** The internal status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
    
    /** The remarks. */
    @Column(length=30000)
    private String remarks;

    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
    
    // Constructor 
    public GovernorSpeechNoticeDraft() {
    	
    }


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getNoticeContent() {
		return noticeContent;
	}


	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
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


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
