package org.mkcl.els.common.vo;

public class HalfHourBallotMemberVO {

	//=============== ATTRIBUTES ====================
	private String memberName;


	//=============== CONSTRUCTORS ==================
	public HalfHourBallotMemberVO() {
		super();
	}
	
	public HalfHourBallotMemberVO(final String memberName) {
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
	
}
