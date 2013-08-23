package org.mkcl.els.common.vo;

import java.util.List;

import org.mkcl.els.common.util.FormaterUtil;

public class MemberBallotQuestionDistributionVO {

	private String sNo;
	
	private String member;
	
	private String memberId;
	
	private String totalCount;
	
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
	
	public String formatNumber(final Integer number, final String locale){
		return FormaterUtil.getNumberFormatterNoGrouping(locale).format(number);
	}
	
	public String formatDecimalNumber(final Double number, final String locale){
		return FormaterUtil.getDeciamlFormatterWithNoGrouping(2, locale).format(number);
	}
	
}
