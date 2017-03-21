package org.mkcl.els.common.vo;

public class CommitteeMemberVO {

	//=============== ATTRIBUTES ====================
	private Long memberId;
	
	private String memberName;
	
	//=============== CONSTRUCTORS ==================
	public CommitteeMemberVO() {
		super();
	}

	public CommitteeMemberVO(final Long memberId, 
			final String memberName) {
		super();
		this.setMemberId(memberId);
		this.setMemberName(memberName);
	}

	//=============== GETTERS SETTERS ===============
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(final Long memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}
	
}