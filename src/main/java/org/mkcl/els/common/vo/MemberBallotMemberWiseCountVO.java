package org.mkcl.els.common.vo;

import java.text.NumberFormat;

import org.mkcl.els.common.util.FormaterUtil;

public class MemberBallotMemberWiseCountVO {

	private String statusType;
	
	private String count;
	
	private String statusTypeType;

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCount() {
		return count;
	}

	public void setStatusTypeType(String statusTypeType) {
		this.statusTypeType = statusTypeType;
	}

	public String getStatusTypeType() {
		return statusTypeType;
	}
	
	public String formatNumber(Integer number,String locale){
		NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(locale);
		return format.format(number);
	}
	
	
}
