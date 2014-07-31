package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "section_drafts")
public class SectionDraft extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//=============== BASIC ATTRIBUTES ====================	
	/** The number. */
    @Column(length = 300)
	private String number;
    
    /** The key. */
    @Column(length = 300)
	private String hierarchyOrder;
	
	/** The language. */
    @Column(length = 300)
	private String language;
	
	/** The text. */
    @Column(length=30000)
	private String text;
    
    /** The bill draft. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bill_draft_id")
	private BillDraft billDraft;
    
    /** The edited on. */
    @Temporal(TemporalType.TIMESTAMP)    
    private Date editedOn; 
    
    /** The edited by. */
    @Column(length=1000)
    private String editedBy;

    /** The edited as. */
    @Column(length=1000)
    private String editedAs;

    //=============== Getters & Setters ====================
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
    public String getHierarchyOrder() {
		return hierarchyOrder;
	}

	public void setHierarchyOrder(String hierarchyOrder) {
		this.hierarchyOrder = hierarchyOrder;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BillDraft getBillDraft() {
		return billDraft;
	}

	public void setBillDraft(BillDraft billDraft) {
		this.billDraft = billDraft;
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
