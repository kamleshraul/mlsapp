package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.QuestionDatesVO;

@XmlRootElement(name="AadwaChartData")
public class AadwaChartXmlVO extends XmlVO {
	
	private List<QuestionDatesVO> rotationOrderDatesList;
		
	public AadwaChartXmlVO() {
		
	}
	
	@XmlElementWrapper(name = "rotationOrderDatesList")
	@XmlElement(name = "rotationOrderDate")
	public List<QuestionDatesVO> getRotationOrderDatesList() {
		return rotationOrderDatesList;
	}

	public void setRotationOrderDatesList(final List<QuestionDatesVO> rotationOrderDatesList) {
		this.rotationOrderDatesList = rotationOrderDatesList;
	}
		
}
