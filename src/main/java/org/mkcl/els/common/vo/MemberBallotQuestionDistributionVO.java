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

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getTotalCount() {
		return totalCount;
	}	
	
	public String formatNumber(Integer number,String locale){
		return FormaterUtil.getNumberFormatterNoGrouping(locale).format(number);
	}
	
	public String formatDecimalNumber(Double number,String locale){
		return FormaterUtil.getDeciamlFormatterWithNoGrouping(2, locale).format(number);
	}
	
}
