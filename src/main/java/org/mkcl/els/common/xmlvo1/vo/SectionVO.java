package org.mkcl.els.common.vo;

public class SectionVO {
	
	private Long id; 
	
	private String language;
	
	private String number;
	
	private String content;
	
	private String editedAs;
	
	private String editedOn;
	
	private String info;
	
	private String orderingSeries;
	
	private String isFirstForHierarchyLevel;
	
	private String isHierarchyLevelWithCustomOrder;
	
	/**** Getters & Setters ****/
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(String editedOn) {
		this.editedOn = editedOn;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getOrderingSeries() {
		return orderingSeries;
	}

	public void setOrderingSeries(String orderingSeries) {
		this.orderingSeries = orderingSeries;
	}

	public String getIsFirstForHierarchyLevel() {
		return isFirstForHierarchyLevel;
	}

	public void setIsFirstForHierarchyLevel(String isFirstForHierarchyLevel) {
		this.isFirstForHierarchyLevel = isFirstForHierarchyLevel;
	}

	public String getIsHierarchyLevelWithCustomOrder() {
		return isHierarchyLevelWithCustomOrder;
	}

	public void setIsHierarchyLevelWithCustomOrder(
			String isHierarchyLevelWithCustomOrder) {
		this.isHierarchyLevelWithCustomOrder = isHierarchyLevelWithCustomOrder;
	}

}
