package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "reminder_letter_tasks")
public class ReminderLetterTask extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The reminder letter referred. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "reminder_letter_id")
    private ReminderLetter reminderLetter;
    
    /* assignee to acknowledge reminder letter */
    @Column(length = 100)
    private String assignee;
    
    /** The time of reminder letter dispatched. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date reminderDispatchedOn;
	
	/** The time of reminder letter acknowledged. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date reminderAcknowledgedOn;
    
    /** The status (dispatched/acknowledged/timeout) **/
	@Column(length = 100)
    private String status;
    
    /**** workflow detail id of reminder letter task (in case workflow task is created) ****/
	@Column(length = 20)
	private String workflowDetailsId;
    
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new laying letter.
     */
	public ReminderLetterTask() {
	   super();
	}
	
	// ---------------------------------Domain Methods----------------------//
//	public static ReminderLetterTask findLatestByFieldNames(final Map<String, String> reminderLetterIdentifiers, final String locale) {
//		ReminderLetterTask latestReminderLetter = null;
//		List<ReminderLetterTask> reminderLetters = ReminderLetterTask.findAllByFieldNames(ReminderLetterTask.class, reminderLetterIdentifiers, "id", ApplicationConstants.DESC, locale);
//		if(reminderLetters!=null) {
//			if(!reminderLetters.isEmpty()) {
//				latestReminderLetter = reminderLetters.get(0);
//			}
//		}
//		return latestReminderLetter;
//	}
	
//	public Boolean isApproved() {
//		Boolean isApproved = false;
//		CustomParameter finalAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "BILL_LAYLETTER_FINAL_AUTHORITY"+"_"+this.getType().toUpperCase(), "");
//		if(finalAuthorityParameter!=null) {
//			Map<String, String> finalLayingLetterDraftIdentifiers =  new HashMap<String, String>();
//			finalLayingLetterDraftIdentifiers.put("reminderLetterId", this.getId().toString());
//			for(String finalAuthority: finalAuthorityParameter.getValue().split(",")) {
//				finalLayingLetterDraftIdentifiers.put("editedAs", finalAuthority);
//				LayingLetterDraft finalLayingLetterDraft = LayingLetterDraft.findByFieldNames(LayingLetterDraft.class, finalLayingLetterDraftIdentifiers, this.getLocale());
//				if(finalLayingLetterDraft!=null) {
//					isApproved = true;
//					break;
//				}
//			}			
//		}
//		return isApproved;
//	}
	

	// ---------------------------------Getters and Setters----------------------//
	public ReminderLetter getReminderLetter() {
		return reminderLetter;
	}

	public void setReminderLetter(ReminderLetter reminderLetter) {
		this.reminderLetter = reminderLetter;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getReminderDispatchedOn() {
		return reminderDispatchedOn;
	}

	public void setReminderDispatchedOn(Date reminderDispatchedOn) {
		this.reminderDispatchedOn = reminderDispatchedOn;
	}

	public Date getReminderAcknowledgedOn() {
		return reminderAcknowledgedOn;
	}

	public void setReminderAcknowledgedOn(Date reminderAcknowledgedOn) {
		this.reminderAcknowledgedOn = reminderAcknowledgedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWorkflowDetailsId() {
		return workflowDetailsId;
	}

	public void setWorkflowDetailsId(String workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}
    
}
