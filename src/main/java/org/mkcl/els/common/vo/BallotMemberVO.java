package org.mkcl.els.common.vo;

public class BallotMemberVO {

	//=============== ATTRIBUTES ====================
	private long memberId;
	
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

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}	
	
	
}
