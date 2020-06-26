package org.mkcl.els.common.vo;

import java.util.Date;
import java.util.List;

import org.mkcl.els.common.util.FormaterUtil;

public class MemberBallotQuestionDistributionVO {

	private String sNo;
	
	private String member;
	
	private String memberId;
	
	private String totalCount;
	
	private String houseType;
	
	private String houseTypeName;
	
	private String sessionTypeName;
	
	private String sessionYear;	
	
	private String sessionCountName;
	
	private Date questionSubmissionStartTime;
	
	private Date questionSubmissionEndTime;
	
	private List<MemberBallotMemberWiseCountVO> distributions;

	public String getsNo() {
		return sNo;
	}

	public void setsNo(final String sNo) {
		this.sNo = sNo;
	}

	public String getMember() {
		return member;
	}

	public void setMember(final String member) {
		this.member = member;
	}

	public List<MemberBallotMemberWiseCountVO> getDistributions() {
		return distributions;
	}

	public void setDistributions(final List<MemberBallotMemberWiseCountVO> distributions) {
		this.distributions = distributions;
	}

	public void setMemberId(final String memberId) {
		this.memberId = memberId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setTotalCount(final String totalCount) {
		this.totalCount = totalCount;
	}

	public String getTotalCount() {
		return totalCount;
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

	public Date getQuestionSubmissionStartTime() {
		return questionSubmissionStartTime;
	}

	public void setQuestionSubmissionStartTime(Date questionSubmissionStartTime) {
		this.questionSubmissionStartTime = questionSubmissionStartTime;
	}

	public Date getQuestionSubmissionEndTime() {
		return questionSubmissionEndTime;
	}

	public void setQuestionSubmissionEndTime(Date questionSubmissionEndTime) {
		this.questionSubmissionEndTime = questionSubmissionEndTime;
	}

	public String formatNumber(final Integer number, final String locale){
		return FormaterUtil.getNumberFormatterNoGrouping(locale).format(number);
	}
	
	public String formatDecimalNumber(final Double number, final String locale){
		return FormaterUtil.getDecimalFormatterWithNoGrouping(2, locale).format(number);
	}
	
}
