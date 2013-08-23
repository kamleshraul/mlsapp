package org.mkcl.els.common.vo;

public class DeviceBallotVO {
	private Long id;
	private String number;
	private String memberName;
	private String subject;
	private String body;
	private String selected;
	
	public Long getId() {
		return id;
	}
	public void setId(final Long id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(final String number) {
		this.number = number;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(final String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(final String body) {
		this.body = body;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(final String selected) {
		this.selected = selected;
	}	
}
