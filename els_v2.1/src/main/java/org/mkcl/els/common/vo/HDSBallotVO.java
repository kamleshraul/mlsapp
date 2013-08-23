package org.mkcl.els.common.vo;

public class HDSBallotVO {

	// =============== ATTRIBUTES ====================
	private Long id;
	private String checked;
	private String questionText;
	private String memberName;
	private String number;
	private String subject;

	// =============== CONSTRUCTORS ==================
	public HDSBallotVO() {
		super();
	}

	public HDSBallotVO(final String memberName, final String number, final String subject) {
		super();
		this.memberName = memberName;
		this.number = number;
		this.subject = subject;
	}

	// =============== GETTERS/SETTERS ===============
	
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(final String checked) {
		this.checked = checked;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(final String questionText) {
		this.questionText = questionText;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}	
}
