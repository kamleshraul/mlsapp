package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "sections")
public class Section extends BaseDomain implements Serializable {

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
    
  //=============== DRAFTS ====================
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="sections_drafts_association", 
    		joinColumns={@JoinColumn(name="section_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="section_draft_id", referencedColumnName="id")})
    private List<SectionDraft> drafts;
    
    @Override
    public Section persist() {
    	addSectionDraft();
    	return (Section)super.persist();
    }
    
    @Override
    public Section merge() {
    	addSectionDraft();
    	return (Section)super.merge();
    }
    
    private void addSectionDraft() {
    	SectionDraft draft = new SectionDraft();
    	draft.setLocale(this.getLocale());
    	draft.setLanguage(this.getLanguage());
    	draft.setNumber(this.getNumber());
    	draft.setHierarchyOrder(this.getHierarchyOrder());
    	draft.setText(this.getText());
    	draft.setBillDraft(this.getBillDraft());
    	draft.setEditedOn(this.getEditedOn());
    	draft.setEditedBy(this.getEditedBy());    	
    	draft.setEditedAs(this.getEditedAs());
    	if(this.getId() != null) {
            Section section = Section.findById(Section.class, this.getId());
            List<SectionDraft> originalDrafts = section.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<SectionDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<SectionDraft> originalDrafts = new ArrayList<SectionDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
    
    public String findOrder() {
    	if(this.getHierarchyOrder()!=null && !this.getHierarchyOrder().isEmpty()) {
    		String[] hierarchyOrder = this.getHierarchyOrder().split("\\.");
    		return hierarchyOrder[hierarchyOrder.length-1];
    	} else {
    		return null;
    	}    	
    }

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

	public List<SectionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<SectionDraft> drafts) {
		this.drafts = drafts;
	}

}
