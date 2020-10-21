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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "reminder_letters")
public class ReminderLetter extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@Column(length = 100)
	private String houseType;
    
	@Column(length = 100)
    private String deviceType;
	
	/* single device id or comma separated devices ids */
    @Column(length = 10000)
	private String deviceIds;
	
    /* type or purpose of reminder */
    @Column(length = 300)
    private String reminderFor;
	
    /* department_id or member_id or custom names to whom reminder is being given */
    @Column(length = 30000)
	private String reminderTo;
    
    @Column(length = 20)
    private String reminderNumber;
	
    @Column(length = 20)
	private String reminderNumberStartLimitingDate; //possibly assembly house start date for given house type or custom start date as applicable
	
    @Column(length = 20)
	private String reminderNumberEndLimitingDate; //possibly assembly house end date for given house type or custom end date as applicable
	
	/** The date of reminder letter sent. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date reminderDate;
    
    /** The date of reminder letter received. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date reminderAcknowledgementDate;
    
    /* usernames of reminder receivers */
    @Column(length = 10000)
    private String receivers;
    
    /** The reminder letter report (optional). */
	@Column(length = 100)
	private String letter;
    
    /** The status (dispatched/acknowledged/timeout) **/
	@Column(length = 100)
    private String status;
    
    /**** workflow detail id of reminder letter flow (optional) ****/
	@Column(length = 20)
	private String workflowDetailsId;
	
	@Column(length = 100)
	private String generatedBy;
	
	@OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "reminder_letter_id", referencedColumnName = "id")
	private List<ReminderLetterTask> reminderLetterTasks;
    
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new laying letter.
     */
	public ReminderLetter() {
	   super();
	}
	
	// ---------------------------------Domain Methods----------------------//
	public static ReminderLetter findLatestByFieldNames(final Map<String, String> reminderLetterIdentifiers, final String locale) {
		ReminderLetter latestReminderLetter = null;
		List<ReminderLetter> reminderLetters = ReminderLetter.findAllByFieldNames(ReminderLetter.class, reminderLetterIdentifiers, "id", ApplicationConstants.DESC, locale);
		if(reminderLetters!=null) {
			if(!reminderLetters.isEmpty()) {
				latestReminderLetter = reminderLetters.get(0);
			}
		}
		return latestReminderLetter;
	}
	
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
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(String deviceIds) {
		this.deviceIds = deviceIds;
	}

	public String getReminderFor() {
		return reminderFor;
	}

	public void setReminderFor(String reminderFor) {
		this.reminderFor = reminderFor;
	}

	public String getReminderTo() {
		return reminderTo;
	}

	public void setReminderTo(String reminderTo) {
		this.reminderTo = reminderTo;
	}

	public String getReminderNumber() {
		return reminderNumber;
	}

	public void setReminderNumber(String reminderNumber) {
		this.reminderNumber = reminderNumber;
	}

	public String getReminderNumberStartLimitingDate() {
		return reminderNumberStartLimitingDate;
	}

	public void setReminderNumberStartLimitingDate(
			String reminderNumberStartLimitingDate) {
		this.reminderNumberStartLimitingDate = reminderNumberStartLimitingDate;
	}

	public String getReminderNumberEndLimitingDate() {
		return reminderNumberEndLimitingDate;
	}

	public void setReminderNumberEndLimitingDate(
			String reminderNumberEndLimitingDate) {
		this.reminderNumberEndLimitingDate = reminderNumberEndLimitingDate;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public Date getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}	

	public Date getReminderAcknowledgementDate() {
		return reminderAcknowledgementDate;
	}

	public void setReminderAcknowledgementDate(Date reminderAcknowledgementDate) {
		this.reminderAcknowledgementDate = reminderAcknowledgementDate;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
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

	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public List<ReminderLetterTask> getReminderLetterTasks() {
		return reminderLetterTasks;
	}

	public void setReminderLetterTasks(List<ReminderLetterTask> reminderLetterTasks) {
		this.reminderLetterTasks = reminderLetterTasks;
	}	
    
}
