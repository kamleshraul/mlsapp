package org.mkcl.els.common.vo;

import java.util.List;

public class MemberBallotMemberWiseReportVO {

	private String member;
	
	private List<MemberBallotMemberWiseCountVO> memberBallotMemberWiseCountVOs;
	
	private List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs;

	public void setMember(String member) {
		this.member = member;
	}

	public String getMember() {
		return member;
	}	

	public void setMemberBallotMemberWiseCountVOs(
			List<MemberBallotMemberWiseCountVO> memberBallotMemberWiseCountVOs) {
		this.memberBallotMemberWiseCountVOs = memberBallotMemberWiseCountVOs;
	}

	public List<MemberBallotMemberWiseCountVO> getMemberBallotMemberWiseCountVOs() {
		return memberBallotMemberWiseCountVOs;
	}

	public void setMemberBallotMemberWiseQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs) {
		this.memberBallotMemberWiseQuestionVOs = memberBallotMemberWiseQuestionVOs;
	}

	public List<MemberBallotMemberWiseQuestionVO> getMemberBallotMemberWiseQuestionVOs() {
		return memberBallotMemberWiseQuestionVOs;
	}	
}
