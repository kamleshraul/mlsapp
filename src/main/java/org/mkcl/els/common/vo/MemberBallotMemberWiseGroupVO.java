package org.mkcl.els.common.vo;

import java.util.List;

public class MemberBallotMemberWiseGroupVO {

	private String groupNo;
	
	private String ministries;
	
	private String answeringDates;
	
	private List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs;

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}

	public String getGroupNo() {
		return groupNo;
	}

	public void setMinistries(String ministries) {
		this.ministries = ministries;
	}

	public String getMinistries() {
		return ministries;
	}

	public void setAnsweringDates(String answeringDates) {
		this.answeringDates = answeringDates;
	}

	public String getAnsweringDates() {
		return answeringDates;
	}

	public void setMemberBallotMemberWiseQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs) {
		this.memberBallotMemberWiseQuestionVOs = memberBallotMemberWiseQuestionVOs;
	}

	public List<MemberBallotMemberWiseQuestionVO> getMemberBallotMemberWiseQuestionVOs() {
		return memberBallotMemberWiseQuestionVOs;
	}	
}
