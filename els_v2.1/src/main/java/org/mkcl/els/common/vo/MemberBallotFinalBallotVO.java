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

	public void setBallotSno(final String ballotSno) {
		this.ballotSno = ballotSno;
	}

	public String getMember() {
		return member;
	}

	public void setMember(final String member) {
		this.member = member;
	}

	public String getBallotEntryId() {
		return ballotEntryId;
	}

	public void setBallotEntryId(final String ballotEntryId) {
		this.ballotEntryId = ballotEntryId;
	}

	public List<MemberBallotFinalBallotQuestionVO> getQuestions() {
		return questions;
	}

	public void setQuestions(final List<MemberBallotFinalBallotQuestionVO> questions) {
		this.questions = questions;
	}
	
	
}
