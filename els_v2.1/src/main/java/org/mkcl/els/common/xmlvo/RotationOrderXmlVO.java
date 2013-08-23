package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.RotationOrderReportVO;

@XmlRootElement(name="RotationOrderData")
//@XmlAccessorType(XmlAccessType.FIELD)
public class RotationOrderXmlVO extends XmlVO {		
	
	private String rotationOrderMainCover;
	
	private String rotationOrderMainHeader;
	
	private String rotationOrderMainFooter;
	
	private List<RotationOrderReportVO> rotationOrderForGroupList;
		
	public RotationOrderXmlVO() {
		
	}	
	
	@XmlElementWrapper(name = "rotationOrderForGroupList")
	@XmlElement(name = "rotationOrderForGroup")
	public List<RotationOrderReportVO> getRotationOrderForGroupList() {
		return rotationOrderForGroupList;
	}

	public void setRotationOrderForGroupList(final List<RotationOrderReportVO> rotationOrderForGroupList) {
		this.rotationOrderForGroupList = rotationOrderForGroupList;
	}

	public String getRotationOrderMainCover() {
		return rotationOrderMainCover;
	}

	public void setRotationOrderMainCover(final String rotationOrderMainCover) {
		this.rotationOrderMainCover = rotationOrderMainCover;
	}

	public String getRotationOrderMainHeader() {
		return rotationOrderMainHeader;
	}

	public void setRotationOrderMainHeader(final String rotationOrderMainHeader) {
		this.rotationOrderMainHeader = rotationOrderMainHeader;
	}

	public String getRotationOrderMainFooter() {
		return rotationOrderMainFooter;
	}

	public void setRotationOrderMainFooter(final String rotationOrderMainFooter) {
		this.rotationOrderMainFooter = rotationOrderMainFooter;
	}
	
}
