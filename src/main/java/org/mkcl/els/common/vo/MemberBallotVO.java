package org.mkcl.els.common.vo;

import java.util.List;

public class MemberBallotVO {

	private String id;
	
	private String position;
	
	private String attendance;
	
	private String round;
	
	private String member;
	
	private String memberId;
	
	private List<MemberBallotQuestionVO> questions;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getMember() {
		return member;
	}

	public void setQuestions(List<MemberBallotQuestionVO> questions) {
		this.questions = questions;
	}

	public List<MemberBallotQuestionVO> getQuestions() {
		return questions;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}

	public String getAttendance() {
		return attendance;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getRound() {
		return round;
	}	
}
