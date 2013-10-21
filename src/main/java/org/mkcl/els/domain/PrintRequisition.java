/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Title.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class PrintRequisition.
 * 
 * @author dhananjayb * 
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "print_requisitions")
public class PrintRequisition extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    @Column
    private String requisitionFor;
    
    //------------unique identifier fields for given print requisition-------------//
    @Column
    private String deviceId;
    
    @Column
    private String deviceType;
    
    @Column
    private String houseType;
    
    @Column
    private String houseRound;
    
    @Column
    private String sessionId;
    
    @Column
    private String year;
    
    @Column
    private String date;
    
    @Column
    private String status;
    
    //------------form parameters for given print requisition-------------//
    @ElementCollection
	@JoinColumn(name="print_requisition_id")
    @MapKeyColumn(name="field_key")
    @Column(name="field_value",length=10000)
    @CollectionTable(name="print_requisition_fields")
	private Map<String,String> fields;
    
  //--------------bill specific fields-------------//
    @Column
    private String optionalFieldsForDocket;
    
    /** The docket report in english. */
	@Column(length = 100)
	private String docketReportEnglish;   
	
	/** The docket report in marathi. */
	@Column(length = 100)
	private String docketReportMarathi;
	
	/** The docket report in hindi. */
	@Column(length = 100)
	private String docketReportHindi;
	
	/** The press copy in english. */
	@Column(length = 100)
	private String pressCopyEnglish;   
	
	/** The press copy in marathi. */
	@Column(length = 100)
	private String pressCopyMarathi;
	
	/** The press copy in hindi. */
	@Column(length = 100)
	private String pressCopyHindi;
	
	/** The endorsement copy in english. */
	@Column(length = 100)
	private String endorsementCopyEnglish;   
	
	/** The endorsement copy in marathi. */
	@Column(length = 100)
	private String endorsementCopyMarathi;
	
	/** The endorsement copy in hindi. */
	@Column(length = 100)
	private String endorsementCopyHindi;
    
//    /** The date on which requisition is sent */
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateOfRequisition;
//    
//    /** The docket. */
////	@Column(length = 100)
////	private String docket;
//
//    /** The description of work of printing. */
//    @Column(length = 30000)
//    private String workOfPrinting;    
//    
//    /** The government authority for printing. */
//    @Column(length = 30000)
//    private String governmentAuthorityForPrinting;
//    
//    /** The government orders of the authority allocating the work to the press. */
//    @Column(length = 30000)
//    private String governmentOrdersForPrinting;
//    
//    /** The budget head of accounts whose cost is debitable for the purpose of proforma account. */
//    @Column(length = 30000)
//    private String budgetHeadOfAccounts;
//    
//    /** The size in which to be printed. */
//    private String printSize;
//    
//    /** The style of binding. */
//    private String bindingStyle;
//    
//    /** The number of proofs required.
//     * N.B. datatype is taken as String here as it may be details instead of just number. */
//    @Column(length = 30000)
//    private String numberOfProofsRequired;
//    
//    /** The date on which proofs are required.(In case of urgent work) */
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateForProofsRequired;
//    
//    /** The date on which final copies are required.(In case of urgent work) */
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateForFinalCopiesRequired;
//    
//    /** The details of number of final copies required. */
//    @Column(length = 30000)
//    private String numberOfFinalCopiesRequired;
//    
//    /** The edition of the publication . */
//    @Column(length = 30000)
//    private String editionOfPublication;
//    
//    /** The special instructions for press, if any. */
//    @Column(length = 30000)
//    private String specialInstructionsForPress;
//    
//    /** The type to be kept after final copies supplied. */
//    private String typeToBeKeptAfterFinalCopiesSupplied;
//    
//    /** is prior reference necessary for finalising price **/
//    private boolean isPriorReferenceNecessaryForFinalisingPrice;
//    
//    /** The notes to mention, if any. */
//    @Column(length = 30000)
//    private String notes;

    // ---------------------------------Constructors----------------------//

    /**
     * Instantiates a new title.
     */
    public PrintRequisition() {
        super();
    }

    // ----------------------------Domain Methods-------------------------//    

	// ----------------------------Getters/Setters------------------------//
    public String getRequisitionFor() {
		return requisitionFor;
	}

	public void setRequisitionFor(String requisitionFor) {
		this.requisitionFor = requisitionFor;
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public String getOptionalFieldsForDocket() {
		return optionalFieldsForDocket;
	}

	public void setOptionalFieldsForDocket(String optionalFieldsForDocket) {
		this.optionalFieldsForDocket = optionalFieldsForDocket;
	}

	public String getDocketReportEnglish() {
		return docketReportEnglish;
	}

	public void setDocketReportEnglish(String docketReportEnglish) {
		this.docketReportEnglish = docketReportEnglish;
	}

	public String getDocketReportMarathi() {
		return docketReportMarathi;
	}

	public void setDocketReportMarathi(String docketReportMarathi) {
		this.docketReportMarathi = docketReportMarathi;
	}

	public String getDocketReportHindi() {
		return docketReportHindi;
	}

	public void setDocketReportHindi(String docketReportHindi) {
		this.docketReportHindi = docketReportHindi;
	}

	public String getPressCopyEnglish() {
		return pressCopyEnglish;
	}

	public void setPressCopyEnglish(String pressCopyEnglish) {
		this.pressCopyEnglish = pressCopyEnglish;
	}

	public String getPressCopyMarathi() {
		return pressCopyMarathi;
	}

	public void setPressCopyMarathi(String pressCopyMarathi) {
		this.pressCopyMarathi = pressCopyMarathi;
	}

	public String getPressCopyHindi() {
		return pressCopyHindi;
	}

	public void setPressCopyHindi(String pressCopyHindi) {
		this.pressCopyHindi = pressCopyHindi;
	}

	public String getEndorsementCopyEnglish() {
		return endorsementCopyEnglish;
	}

	public void setEndorsementCopyEnglish(String endorsementCopyEnglish) {
		this.endorsementCopyEnglish = endorsementCopyEnglish;
	}

	public String getEndorsementCopyMarathi() {
		return endorsementCopyMarathi;
	}

	public void setEndorsementCopyMarathi(String endorsementCopyMarathi) {
		this.endorsementCopyMarathi = endorsementCopyMarathi;
	}

	public String getEndorsementCopyHindi() {
		return endorsementCopyHindi;
	}

	public void setEndorsementCopyHindi(String endorsementCopyHindi) {
		this.endorsementCopyHindi = endorsementCopyHindi;
	}
	
//    public Date getDateOfRequisition() {
//		return dateOfRequisition;
//	}
//
//	public void setDateOfRequisition(Date dateOfRequisition) {
//		this.dateOfRequisition = dateOfRequisition;
//	}
//
//	public String getWorkOfPrinting() {
//		return workOfPrinting;
//	}
//
//	public void setWorkOfPrinting(String workOfPrinting) {
//		this.workOfPrinting = workOfPrinting;
//	}
//
//	public String getGovernmentAuthorityForPrinting() {
//		return governmentAuthorityForPrinting;
//	}
//
//	public void setGovernmentAuthorityForPrinting(
//			String governmentAuthorityForPrinting) {
//		this.governmentAuthorityForPrinting = governmentAuthorityForPrinting;
//	}
//
//	public String getGovernmentOrdersForPrinting() {
//		return governmentOrdersForPrinting;
//	}
//
//	public void setGovernmentOrdersForPrinting(String governmentOrdersForPrinting) {
//		this.governmentOrdersForPrinting = governmentOrdersForPrinting;
//	}
//
//	public String getBudgetHeadOfAccounts() {
//		return budgetHeadOfAccounts;
//	}
//
//	public void setBudgetHeadOfAccounts(String budgetHeadOfAccounts) {
//		this.budgetHeadOfAccounts = budgetHeadOfAccounts;
//	}
//
//	public String getPrintSize() {
//		return printSize;
//	}
//
//	public void setPrintSize(String printSize) {
//		this.printSize = printSize;
//	}
//
//	public String getBindingStyle() {
//		return bindingStyle;
//	}
//
//	public void setBindingStyle(String bindingStyle) {
//		this.bindingStyle = bindingStyle;
//	}
//
//	public String getNumberOfProofsRequired() {
//		return numberOfProofsRequired;
//	}
//
//	public void setNumberOfProofsRequired(String numberOfProofsRequired) {
//		this.numberOfProofsRequired = numberOfProofsRequired;
//	}
//
//	public Date getDateForProofsRequired() {
//		return dateForProofsRequired;
//	}
//
//	public void setDateForProofsRequired(Date dateForProofsRequired) {
//		this.dateForProofsRequired = dateForProofsRequired;
//	}
//
//	public Date getDateForFinalCopiesRequired() {
//		return dateForFinalCopiesRequired;
//	}
//
//	public void setDateForFinalCopiesRequired(Date dateForFinalCopiesRequired) {
//		this.dateForFinalCopiesRequired = dateForFinalCopiesRequired;
//	}
//
//	public String getNumberOfFinalCopiesRequired() {
//		return numberOfFinalCopiesRequired;
//	}
//
//	public void setNumberOfFinalCopiesRequired(String numberOfFinalCopiesRequired) {
//		this.numberOfFinalCopiesRequired = numberOfFinalCopiesRequired;
//	}
//
//	public String getEditionOfPublication() {
//		return editionOfPublication;
//	}
//
//	public void setEditionOfPublication(String editionOfPublication) {
//		this.editionOfPublication = editionOfPublication;
//	}
//
//	public String getSpecialInstructionsForPress() {
//		return specialInstructionsForPress;
//	}
//
//	public void setSpecialInstructionsForPress(String specialInstructionsForPress) {
//		this.specialInstructionsForPress = specialInstructionsForPress;
//	}
//
//	public String getTypeToBeKeptAfterFinalCopiesSupplied() {
//		return typeToBeKeptAfterFinalCopiesSupplied;
//	}
//
//	public void setTypeToBeKeptAfterFinalCopiesSupplied(
//			String typeToBeKeptAfterFinalCopiesSupplied) {
//		this.typeToBeKeptAfterFinalCopiesSupplied = typeToBeKeptAfterFinalCopiesSupplied;
//	}
//
//	public boolean isPriorReferenceNecessaryForFinalisingPrice() {
//		return isPriorReferenceNecessaryForFinalisingPrice;
//	}
//
//	public void setPriorReferenceNecessaryForFinalisingPrice(
//			boolean isPriorReferenceNecessaryForFinalisingPrice) {
//		this.isPriorReferenceNecessaryForFinalisingPrice = isPriorReferenceNecessaryForFinalisingPrice;
//	}
//
//	public String getNotes() {
//		return notes;
//	}
//
//	public void setNotes(String notes) {
//		this.notes = notes;
//	}	
}
