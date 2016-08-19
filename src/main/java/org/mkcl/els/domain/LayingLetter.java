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
@Table(name = "laying_letter")
public class LayingLetter extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String houseType;
	
	private String houseRound;
	
	/** The device. */
    private String deviceId;
    
    private String deviceType;   
    
    private String layingFor;
    
    /** The laying letter report. */
	@Column(length = 100)
	private String letter;
	
	/** The date of laying letter. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date layingDate;
    
    /** The status (pending/approved/rejected) **/
    private String status;
    
    //=============== DRAFTS ====================
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="layingletters_drafts_association", 
    		joinColumns={@JoinColumn(name="laying_letter_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="laying_letter_draft_id", referencedColumnName="id")})
    private List<LayingLetterDraft> drafts;
    
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
	public LayingLetter() {
	   super();
	}
	
	// ---------------------------------Domain Methods----------------------//
	@Override
    public LayingLetter persist() {
		addLayingLetterDraft();
		LayingLetter layingLetter = (LayingLetter)super.persist();
		LayingLetterDraft firstDraft = layingLetter.getDrafts().get(0);
		firstDraft.setLayingLetterId(String.valueOf(layingLetter.getId()));
		firstDraft.merge();
    	return layingLetter;
    }
	
	@Override
    public LayingLetter merge() {
		addLayingLetterDraft();
    	return (LayingLetter)super.merge();
    }
	
	private void addLayingLetterDraft() {
		LayingLetterDraft draft = new LayingLetterDraft();
		draft.setLocale(this.getLocale());
		draft.setLayingLetterId(String.valueOf(this.getId()));		
		draft.setEditedAs(this.getEditedAs());
        draft.setEditedBy(this.getEditedBy());
        draft.setEditedOn(this.getEditedOn());
        draft.setLetter(this.getLetter());
        draft.setLayingDate(this.getLayingDate());
        draft.setStatus(this.getStatus());
        draft.setWorkflowDetailsId(this.getWorkflowDetailsId());
        if(this.getId() != null) {
            LayingLetter layingLetter = LayingLetter.findById(LayingLetter.class, this.getId());
            List<LayingLetterDraft> originalDrafts = layingLetter.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<LayingLetterDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<LayingLetterDraft> originalDrafts = new ArrayList<LayingLetterDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
	}
	
	public static LayingLetter findLatestByFieldNames(final Map<String, String> layingLetterIdentifiers, final String locale) {
		LayingLetter latestLayingLetter = null;
		List<LayingLetter> layingLetters = LayingLetter.findAllByFieldNames(LayingLetter.class, layingLetterIdentifiers, "id", ApplicationConstants.DESC, locale);
		if(layingLetters!=null) {
			if(!layingLetters.isEmpty()) {
				latestLayingLetter = layingLetters.get(0);
			}
		}
		return latestLayingLetter;
	}
	
	public Boolean isApproved() {
		Boolean isApproved = false;
		CustomParameter finalAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "BILL_LAYLETTER_FINAL_AUTHORITY"+"_"+this.getHouseType().toUpperCase(), "");
		if(finalAuthorityParameter!=null) {
			Map<String, String> finalLayingLetterDraftIdentifiers =  new HashMap<String, String>();
			finalLayingLetterDraftIdentifiers.put("layingLetterId", this.getId().toString());
			for(String finalAuthority: finalAuthorityParameter.getValue().split(",")) {
				finalLayingLetterDraftIdentifiers.put("editedAs", finalAuthority);
				LayingLetterDraft finalLayingLetterDraft = LayingLetterDraft.findByFieldNames(LayingLetterDraft.class, finalLayingLetterDraftIdentifiers, this.getLocale());
				if(finalLayingLetterDraft!=null) {
					isApproved = true;
					break;
				}
			}			
		}
		return isApproved;
	}
	

	// ---------------------------------Getters and Setters----------------------//
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getHouseRound() {
		return houseRound;
	}

	public void setHouseRound(String houseRound) {
		this.houseRound = houseRound;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getLayingFor() {
		return layingFor;
	}

	public void setLayingFor(String layingFor) {
		this.layingFor = layingFor;
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
	
	public List<LayingLetterDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<LayingLetterDraft> drafts) {
		this.drafts = drafts;
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
