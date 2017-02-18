package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.ChartVO;

@XmlRootElement(name="ResolutionData")
public class ResolutionXmlVO extends XmlVO {
	
	private List<ChartVO> resolutionList;
		
	private String date;
	
	private Integer karyavaliNumber;
	
	public ResolutionXmlVO() {
		
	}
	
	@XmlElementWrapper(name = "resolutionList")
	@XmlElement(name = "resolutionListForMember")
	public List<ChartVO> getResolutionList() {
		return resolutionList;
	}

	public void setResolutionList(final List<ChartVO> resolutionList) {
		this.resolutionList = resolutionList;
	}

	@XmlElement(name = "date")
	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	@XmlElement(name = "karyavaliNumber")
	public Integer getKaryavaliNumber() {
		return karyavaliNumber;
	}

	public void setKaryavaliNumber(Integer karyavaliNumber) {
		this.karyavaliNumber = karyavaliNumber;
	}
	
	
	
}
