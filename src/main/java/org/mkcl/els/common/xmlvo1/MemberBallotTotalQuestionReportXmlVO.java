package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;

@XmlRootElement(name="root")
public class MemberBallotTotalQuestionReportXmlVO extends XmlVO {
	
	private List<MemberBallotQuestionDistributionVO> questionDistributionVOs;
	
	private String totalQuestions;
	
	private String totalAdmittedQuestions;
	
	private String totalConvertToUnstarredAndAdmitQuestions;
	
	private String totalRejectedQuestions;
	
	private String totalClarificationQuestions;
	
	private String percentTotalAdmittedQuestions;
	
	private String percentTotalConvertToUnstarredAndAdmitQuestions;
	
	private String percentTotalRejectedQuestions;
	
	private String percentTotalClarificationQuestions;
	
	private String questionSubmissionDate;
	
	private String questionSubmissionStartTime;
	
	private int dayTime;
	
	private String questionSubmissionEndTime;
	
	private String houseType;
	
	private String houseTypeName;
	
	private String sessionTypeName;
	
	private String sessionYear;
	
	private String sessionCountName;
	
	private String questionTypeName;	
	
	public MemberBallotTotalQuestionReportXmlVO() {
		super();
	}

	@XmlElementWrapper(name = "questionDistributions")
	@XmlElement(name = "questionDistribution")
	public List<MemberBallotQuestionDistributionVO> getQuestionDistributionVOs() {
		return questionDistributionVOs;
	}

	public void setQuestionDistributionVOs(
			List<MemberBallotQuestionDistributionVO> questionDistributionVOs) {
		this.questionDistributionVOs = questionDistributionVOs;
	}

	public String getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(String totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public String getTotalAdmittedQuestions() {
		return totalAdmittedQuestions;
	}

	public void setTotalAdmittedQuestions(String totalAdmittedQuestions) {
		this.totalAdmittedQuestions = totalAdmittedQuestions;
	}

	public String getTotalConvertToUnstarredAndAdmitQuestions() {
		return totalConvertToUnstarredAndAdmitQuestions;
	}

	public void setTotalConvertToUnstarredAndAdmitQuestions(
			String totalConvertToUnstarredAndAdmitQuestions) {
		this.totalConvertToUnstarredAndAdmitQuestions = totalConvertToUnstarredAndAdmitQuestions;
	}

	public String getTotalRejectedQuestions() {
		return totalRejectedQuestions;
	}

	public void setTotalRejectedQuestions(String totalRejectedQuestions) {
		this.totalRejectedQuestions = totalRejectedQuestions;
	}

	public String getTotalClarificationQuestions() {
		return totalClarificationQuestions;
	}

	public void setTotalClarificationQuestions(String totalClarificationQuestions) {
		this.totalClarificationQuestions = totalClarificationQuestions;
	}

	public String getPercentTotalAdmittedQuestions() {
		return percentTotalAdmittedQuestions;
	}

	public void setPercentTotalAdmittedQuestions(
			String percentTotalAdmittedQuestions) {
		this.percentTotalAdmittedQuestions = percentTotalAdmittedQuestions;
	}

	public String getPercentTotalConvertToUnstarredAndAdmitQuestions() {
		return percentTotalConvertToUnstarredAndAdmitQuestions;
	}

	public void setPercentTotalConvertToUnstarredAndAdmitQuestions(
			String percentTotalConvertToUnstarredAndAdmitQuestions) {
		this.percentTotalConvertToUnstarredAndAdmitQuestions = percentTotalConvertToUnstarredAndAdmitQuestions;
	}

	public String getPercentTotalRejectedQuestions() {
		return percentTotalRejectedQuestions;
	}

	public void setPercentTotalRejectedQuestions(
			String percentTotalRejectedQuestions) {
		this.percentTotalRejectedQuestions = percentTotalRejectedQuestions;
	}

	public String getPercentTotalClarificationQuestions() {
		return percentTotalClarificationQuestions;
	}

	public void setPercentTotalClarificationQuestions(
			String percentTotalClarificationQuestions) {
		this.percentTotalClarificationQuestions = percentTotalClarificationQuestions;
	}

	public String getQuestionSubmissionDate() {
		return questionSubmissionDate;
	}

	public void setQuestionSubmissionDate(String questionSubmissionDate) {
		this.questionSubmissionDate = questionSubmissionDate;
	}

	public String getQuestionSubmissionStartTime() {
		return questionSubmissionStartTime;
	}

	public void setQuestionSubmissionStartTime(String questionSubmissionStartTime) {
		this.questionSubmissionStartTime = questionSubmissionStartTime;
	}

	public int getDayTime() {
		return dayTime;
	}

	public void setDayTime(int dayTime) {
		this.dayTime = dayTime;
	}

	public String getQuestionSubmissionEndTime() {
		return questionSubmissionEndTime;
	}

	public void setQuestionSubmissionEndTime(String questionSubmissionEndTime) {
		this.questionSubmissionEndTime = questionSubmissionEndTime;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getHouseTypeName() {
		return houseTypeName;
	}

	public void setHouseTypeName(String houseTypeName) {
		this.houseTypeName = houseTypeName;
	}

	public String getSessionTypeName() {
		return sessionTypeName;
	}

	public void setSessionTypeName(String sessionTypeName) {
		this.sessionTypeName = sessionTypeName;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public String getSessionCountName() {
		return sessionCountName;
	}

	public void setSessionCountName(String sessionCountName) {
		this.sessionCountName = sessionCountName;
	}

	public String getQuestionTypeName() {
		return questionTypeName;
	}

	public void setQuestionTypeName(String questionTypeName) {
		this.questionTypeName = questionTypeName;
	}		

}
