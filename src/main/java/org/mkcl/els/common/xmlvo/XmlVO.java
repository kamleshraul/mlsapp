package org.mkcl.els.common.xmlvo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XmlVO {
	
	private String locale;
	
	private String outputFormat;
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(final String locale) {
		this.locale = locale;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(final String outputFormat) {
		this.outputFormat = outputFormat;
	}	
	
}
