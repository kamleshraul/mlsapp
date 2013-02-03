package org.mkcl.els.common.vo;

import java.util.List;

public class MemberBallotFinalBallotVO {

	private String ballotSno;
	
	private String member;
	
	private String ballotEntryId;
	
	private List<MemberBallotFinalBallotQuestionVO> questions;

	
	public String getBallotSno() {
		return ballotSno;
	}

	public void setBallotSno(String ballotSno) {
		this.ballotSno = ballotSno;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getBallotEntryId() {
		return ballotEntryId;
	}

	public void setBallotEntryId(String ballotEntryId) {
		this.ballotEntryId = ballotEntryId;
	}

	public List<MemberBallotFinalBallotQuestionVO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<MemberBallotFinalBallotQuestionVO> questions) {
		this.questions = questions;
	}
	
	
}
