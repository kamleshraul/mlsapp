package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "part_drafts")
public class PartDraft extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(length = 30000)
	private String mainHeading;

	@Column(length = 30000)
	private String pageHeading;

	@Column(length = 30000)
	private String revisedContent;

	@Column(length = 1000)
	private String editedBy;

	@Column(length = 1000)
	private String editedAs;

	private Date editedOn;

	@Column(length=30000)
	private String originalText;

	@Column(length=30000)
	private String replacedText;
		
	@Column(length=255)
	private String uniqueIdentifierForUndo;

	private Integer undoCount;
	
	@Column(length=255)
	private String uniqueIdentifierForRedo;

	private Integer redoCount;

	public PartDraft() {
		super();
	}

	public String getMainHeading() {
		return mainHeading;
	}

	public void setMainHeading(String mainHeading) {
		this.mainHeading = mainHeading;
	}

	public String getPageHeading() {
		return pageHeading;
	}

	public void setPageHeading(String pageHeading) {
		this.pageHeading = pageHeading;
	}

	public String getRevisedContent() {
		return revisedContent;
	}

	public void setRevisedContent(String revisedContent) {
		this.revisedContent = revisedContent;
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

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getReplacedText() {
		return replacedText;
	}

	public void setReplacedText(String replacedText) {
		this.replacedText = replacedText;
	}

	public String getUniqueIdentifierForUndo() {
		return uniqueIdentifierForUndo;
	}

	public void setUniqueIdentifierForUndo(String uniqueIdentifierForUndo) {
		this.uniqueIdentifierForUndo = uniqueIdentifierForUndo;
	}

	public Integer getUndoCount() {
		return undoCount;
	}

	public void setUndoCount(Integer undoCount) {
		this.undoCount = undoCount;
	}

	public String getUniqueIdentifierForRedo() {
		return uniqueIdentifierForRedo;
	}

	public void setUniqueIdentifierForRedo(String uniqueIdentifierForRedo) {
		this.uniqueIdentifierForRedo = uniqueIdentifierForRedo;
	}

	public Integer getRedoCount() {
		return redoCount;
	}

	public void setRedoCount(Integer redoCount) {
		this.redoCount = redoCount;
	}	
}
