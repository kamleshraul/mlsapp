package org.mkcl.els.common.vo;

public class QuestionVO {

	//=============== ATTRIBUTES ====================
	private Long id;
	private Integer number;
	private String status;

	
	//=============== CONSTRUCTORS ==================
	public QuestionVO() {
		super();
	}
	
	public QuestionVO(Long id, Integer number, String status) {
		super();
		this.setId(id);
		this.setNumber(number);
		this.setStatus(status);
	}
	
	
	//=============== GETTERS/SETTERS ===============
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

