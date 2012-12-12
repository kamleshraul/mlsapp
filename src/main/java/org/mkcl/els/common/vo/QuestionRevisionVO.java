package org.mkcl.els.common.vo;

public class QuestionRevisionVO {
/*
 * This vo is used to contain the various changes made in a question by various actors
 * during the workflow.
 */
	private String editedAs;
	
	private String editedBY;
	
	private String editedOn;
	
	private String status;
	
	private String subject;
	
	private String question;
	
	private String remarks;

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedBY() {
		return editedBY;
	}

	public void setEditedBY(String editedBY) {
		this.editedBY = editedBY;
	}

	public String getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(String editedOn) {
		this.editedOn = editedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
