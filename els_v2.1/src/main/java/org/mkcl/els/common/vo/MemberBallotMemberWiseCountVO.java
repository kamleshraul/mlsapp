package org.mkcl.els.common.vo;

import java.text.NumberFormat;

import org.mkcl.els.common.util.FormaterUtil;

public class MemberBallotMemberWiseCountVO {

	private String statusType;
	
	private String count;
	
	private String statusTypeType;

	public void setStatusType(final String statusType) {
		this.statusType = statusType;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setCount(final String count) {
		this.count = count;
	}

	public String getCount() {
		return count;
	}

	public void setStatusTypeType(final String statusTypeType) {
		this.statusTypeType = statusTypeType;
	}

	public String getStatusTypeType() {
		return statusTypeType;
	}
	
	public String formatNumber(final Integer number, final String locale){
		NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale);
		return format.format(number);
	}
	
	
}
