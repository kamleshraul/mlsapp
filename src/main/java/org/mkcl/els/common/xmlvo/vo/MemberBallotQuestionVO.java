package org.mkcl.els.common.vo;

public class MemberBallotQuestionVO {

	private String id;
	
	private String number;
	
	private String group;	
	
	private String answeringDate;
	
	private String choice;
	
	private String parentId;
	
	private String parentNumber;


	public void setId(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setNumber(final String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setGroup(final String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setAnsweringDate(final String answeringDate) {
		this.answeringDate = answeringDate;
	}

	public String getAnsweringDate() {
		return answeringDate;
	}

	public void setChoice(final String choice) {
		this.choice = choice;
	}

	public String getChoice() {
		return choice;
	}

	public void setParentId(final String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentNumber(final String parentNumber) {
		this.parentNumber = parentNumber;
	}

	public String getParentNumber() {
		return parentNumber;
	}	
}
