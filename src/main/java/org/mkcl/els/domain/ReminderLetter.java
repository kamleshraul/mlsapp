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
    
    private String deviceType;
	
	private String deviceId;
	
	private String reminderFor; //type or purpose of reminder
    
    private String reminderNumber;
	
	/** The date of reminder letter sent. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date reminderDate;
    
    private String receivers;
    
    /** The reminder letter report (optional). */
	@Column(length = 100)
	private String letter;
    
    /** The status (pending/received/timeout) **/
    private String status;
    
    /**** workflow detail ****/
	private String workflowDetailsId;
    
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new laying letter.
     */
	public ReminderLetter() {
	   super();
	}
	
	// ---------------------------------Domain Methods----------------------//
	public static ReminderLetter findLatestByFieldNames(final Map<String, String> reminderLetterIdentifiers, final String locale) {
		ReminderLetter latestLayingLetter = null;
		List<ReminderLetter> reminderLetters = ReminderLetter.findAllByFieldNames(ReminderLetter.class, reminderLetterIdentifiers, "id", ApplicationConstants.DESC, locale);
		if(reminderLetters!=null) {
			if(!reminderLetters.isEmpty()) {
				latestLayingLetter = reminderLetters.get(0);
			}
		}
		return latestLayingLetter;
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
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getReminderFor() {
		return reminderFor;
	}

	public void setReminderFor(String reminderFor) {
		this.reminderFor = reminderFor;
	}

	public String getReminderNumber() {
		return reminderNumber;
	}

	public void setReminderNumber(String reminderNumber) {
		this.reminderNumber = reminderNumber;
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
    
}
