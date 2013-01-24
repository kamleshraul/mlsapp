package org.mkcl.els.common.vo;

public class BallotMemberVO {

	//=============== ATTRIBUTES ====================
	private String memberName;


	//=============== CONSTRUCTORS ==================
	public BallotMemberVO() {
		super();
	}
	
	public BallotMemberVO(String memberName) {
		super();
		this.memberName = memberName;
	}

	
	//=============== GETTERS/SETTERS ===============
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
}
