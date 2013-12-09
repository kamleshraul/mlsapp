package org.mkcl.els.common.vo;

import java.util.List;

public class CatchwordHeadingVO {
	
	/**** Attributes ****/
	private String catchWord;
	
	private String heading;
	
	
	/**** Constructor ****/
	public CatchwordHeadingVO(){
		super();
	}


	public String getCatchWord() {
		return catchWord;
	}


	public void setCatchWord(String catchWord) {
		this.catchWord = catchWord;
	}


	public String getHeading() {
		return heading;
	}


	public void setHeading(String headings) {
		this.heading = headings;
	}
}
