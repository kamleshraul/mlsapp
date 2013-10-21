package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "laying_letter_draft")
public class LayingLetterDraft extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The laying letter. */
	@Column(length = 100)
	private String layingLetterId;
	
	/** The laying letter report. */
	@Column(length = 100)
	private String letter;
	
	/** The date of laying letter. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date layingDate;
    
    /** The status (pending/approved/rejected) **/
    private String status;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)    
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;
    
    /**** workflow detail ****/
	private String workflowDetailsId;
    
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new laying letter.
     */
	public LayingLetterDraft() {
	   super();
	}

	//-----------------------------Getters And Setters--------------------------------
	public String getLayingLetterId() {
		return layingLetterId;
	}

	public void setLayingLetterId(String layingLetterId) {
		this.layingLetterId = layingLetterId;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public Date getLayingDate() {
		return layingDate;
	}

	public void setLayingDate(Date layingDate) {
		this.layingDate = layingDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getWorkflowDetailsId() {
		return workflowDetailsId;
	}

	public void setWorkflowDetailsId(String workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}		
    
}
