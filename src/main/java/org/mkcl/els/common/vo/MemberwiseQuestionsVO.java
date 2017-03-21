package org.mkcl.els.common.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.mkcl.els.common.vo.GroupVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseCountVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseQuestionVO;

public class MemberwiseQuestionsVO {
	
	private String houseType;
	
	private String submissionDate;
	
	private List<GroupVO> groupVOs;
	
	private String member;
	
	private List<MemberBallotMemberWiseCountVO> memberBallotMemberWiseCountVOs;
	
	private List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs;
	
	private String admittedQuestionCount;
	
	private String convertedToUnstarredAndAdmittedQuestionCount;
	
	private String rejectedQuestionCount;
	
	private String clarificationQuestionCount;
	
	public MemberwiseQuestionsVO() {
		super();
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	@XmlElementWrapper(name = "groupList")
	@XmlElement(name = "group")
	public List<GroupVO> getGroupVOs() {
		return groupVOs;
	}

	public void setGroupVOs(List<GroupVO> groupVOs) {
		this.groupVOs = groupVOs;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	@XmlElementWrapper(name = "countVOs")
	@XmlElement(name = "countVO")
	public List<MemberBallotMemberWiseCountVO> getMemberBallotMemberWiseCountVOs() {
		return memberBallotMemberWiseCountVOs;
	}

	public void setMemberBallotMemberWiseCountVOs(
			List<MemberBallotMemberWiseCountVO> memberBallotMemberWiseCountVOs) {
		this.memberBallotMemberWiseCountVOs = memberBallotMemberWiseCountVOs;
	}

	@XmlElementWrapper(name = "memberBallotMemberWiseQuestionVOs")
	@XmlElement(name = "memberBallotMemberWiseQuestionVO")
	public List<MemberBallotMemberWiseQuestionVO> getMemberBallotMemberWiseQuestionVOs() {
		return memberBallotMemberWiseQuestionVOs;
	}

	public void setMemberBallotMemberWiseQuestionVOs(
			List<MemberBallotMemberWiseQuestionVO> memberBallotMemberWiseQuestionVOs) {
		this.memberBallotMemberWiseQuestionVOs = memberBallotMemberWiseQuestionVOs;
	}

	@XmlElement(name = "admittedQuestionCount")
	public String getAdmittedQuestionCount() {
		return admittedQuestionCount;
	}

	public void setAdmittedQuestionCount(String admittedQuestionCount) {
		this.admittedQuestionCount = admittedQuestionCount;
	}

	@XmlElement(name = "convertedToUnstarredAndAdmittedQuestionCount")
	public String getConvertedToUnstarredAndAdmittedQuestionCount() {
		return convertedToUnstarredAndAdmittedQuestionCount;
	}

	public void setConvertedToUnstarredAndAdmittedQuestionCount(
			String convertedToUnstarredAndAdmittedQuestionCount) {
		this.convertedToUnstarredAndAdmittedQuestionCount = convertedToUnstarredAndAdmittedQuestionCount;
	}

	@XmlElement(name = "rejectedQuestionCount")
	public String getRejectedQuestionCount() {
		return rejectedQuestionCount;
	}

	public void setRejectedQuestionCount(String rejectedQuestionCount) {
		this.rejectedQuestionCount = rejectedQuestionCount;
	}

	@XmlElement(name = "clarificationQuestionCount")
	public String getClarificationQuestionCount() {
		return clarificationQuestionCount;
	}

	public void setClarificationQuestionCount(String clarificationQuestionCount) {
		this.clarificationQuestionCount = clarificationQuestionCount;
	}	

}
