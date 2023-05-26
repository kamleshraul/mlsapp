package org.mkcl.els.common.vo;

import java.util.List;

public class GeneralReportVO {
	

	private List<Object[]> obj;
	
	private String[] topHeader;
	
	private List<String> serialNumbers;

	public GeneralReportVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<Object[]> getObj() {
		return obj;
	}

	public void setObj(List<Object[]> obj) {
		this.obj = obj;
	}

	public String[] getTopHeader() {
		return topHeader;
	}

	public void setTopHeader(String[] topHeader) {
		this.topHeader = topHeader;
	}

	public List<String> getSerialNumbers() {
		return serialNumbers;
	}

	public void setSerialNumbers(List<String> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}
			
}
