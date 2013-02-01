package org.mkcl.els.common.vo;

public class MemberBallotQuestionVO {

	private String id;
	
	private String number;
	
	private String group;	
	
	private String answeringDate;
	
	private String choice;
	
	private String parentId;
	
	private String parentNumber;


	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setAnsweringDate(String answeringDate) {
		this.answeringDate = answeringDate;
	}

	public String getAnsweringDate() {
		return answeringDate;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getChoice() {
		return choice;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	public String getParentNumber() {
		return parentNumber;
	}	
}
