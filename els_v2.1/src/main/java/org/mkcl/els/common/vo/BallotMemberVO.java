package org.mkcl.els.common.vo;

public class BallotMemberVO {

	//=============== ATTRIBUTES ====================
	private String memberName;
	
	private String memberChoiceNumber;


	//=============== CONSTRUCTORS ==================
	public BallotMemberVO() {
		super();
	}
	
	public BallotMemberVO(final String memberName) {
		super();
		this.memberName = memberName;
	}

	
	//=============== GETTERS/SETTERS ===============
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}

	public String getMemberChoiceNumber() {
		return memberChoiceNumber;
	}

	public void setMemberChoiceNumber(final String memberChoiceNumber) {
		this.memberChoiceNumber = memberChoiceNumber;
	}	
}
