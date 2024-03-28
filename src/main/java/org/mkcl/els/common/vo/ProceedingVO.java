package org.mkcl.els.common.vo;

import java.util.Date;

public class ProceedingVO {
	private Long id;

    /** The name. */
    private String mlsUrl;
    
    private Long partid;
    
    private String slotName;
    
    private String currentSlotStartDate;
    
    private String previousReporter;
    
    private String currentSlotStartTime;
    
    private String languageReporter;
    
    private String generalNotice; 
    
    
    // TODO --> Chane Term to Turn
    private Boolean isEditorTerm;
    
    private Long version;
    
    
	//=============== GETTERS/SETTERS ===============

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMlsUrl() {
		return mlsUrl;
	}

	public void setMlsUrl(String mlsUrl) {
		this.mlsUrl = mlsUrl;
	}


	public Long getPartid() {
		return partid;
	}

	public void setPartid(Long partid) {
		this.partid = partid;
	}

	public String getSlotName() {
		return slotName;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public String getCurrentSlotStartDate() {
		return currentSlotStartDate;
	}

	public void setCurrentSlotStartDate(String currentSlotStartDate) {
		this.currentSlotStartDate = currentSlotStartDate;
	}

	public String getPreviousReporter() {
		return previousReporter;
	}

	public void setPreviousReporter(String previousReporter) {
		this.previousReporter = previousReporter;
	}

	public String getCurrentSlotStartTime() {
		return currentSlotStartTime;
	}

	public void setCurrentSlotStartTime(String currenSlotStartTime) {
		this.currentSlotStartTime = currenSlotStartTime;
	}

	public String getLanguageReporter() {
		return languageReporter;
	}

	public void setLanguageReporter(String languageReporter) {
		this.languageReporter = languageReporter;
	}

	public String getGeneralNotice() {
		return generalNotice;
	}

	public void setGeneralNotice(String generalNotice) {
		this.generalNotice = generalNotice;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Boolean getIsEditorTerm() {
		return isEditorTerm;
	}

	public void IsEditorTerm(Boolean isEditorTerm) {
		this.isEditorTerm = isEditorTerm;
	}

	
    

    
 
}
