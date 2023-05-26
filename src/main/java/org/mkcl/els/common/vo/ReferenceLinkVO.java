package org.mkcl.els.common.vo;

import javax.persistence.Column;

/**
 * @author sagars
 *
 */
public class ReferenceLinkVO {
	
	// ---------------------------------Attributes-------------------------------------------------
	private String name;
	
	private String date;
	
	private String link;
	
	private String localizedname;

	private String englishFormatDate;

	private String locale;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getLocalizedname() {
		return localizedname;
	}

	public void setLocalizedname(String localizedname) {
		this.localizedname = localizedname;
	}

	public String getEnglishFormatDate() {
		return englishFormatDate;
	}

	public void setEnglishFormatDate(String englishFormatDate) {
		this.englishFormatDate = englishFormatDate;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
    		
}
