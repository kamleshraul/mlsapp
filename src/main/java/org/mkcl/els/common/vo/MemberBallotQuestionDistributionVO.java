package org.mkcl.els.common.vo;

import java.util.List;

public class MemberBallotQuestionDistributionVO {

	private String sNo;
	
	private String member;
	
	private String memberId;
	
	private List<MemberBallotMemberWiseCountVO> distributions;

	public String getsNo() {
		return sNo;
	}

	public void setsNo(String sNo) {
		this.sNo = sNo;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public List<MemberBallotMemberWiseCountVO> getDistributions() {
		return distributions;
	}

	public void setDistributions(List<MemberBallotMemberWiseCountVO> distributions) {
		this.distributions = distributions;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberId() {
		return memberId;
	}	
	
}
