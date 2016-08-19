package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.MemberwiseQuestionsVO;
import org.mkcl.els.common.vo.RotationOrderReportVO;

@XmlRootElement(name="CumulativeMemberwiseQuestionData")
//@XmlAccessorType(XmlAccessType.FIELD)
public class CumulativeMemberwiseQuestionsXmlVO extends XmlVO {		
	
	private List<MemberwiseQuestionsVO> memberwiseQuestionDataList;
		
	public CumulativeMemberwiseQuestionsXmlVO() {
		
	}		
	
	/**
	 * @return the memberwiseQuestionDataList
	 */
	@XmlElementWrapper(name = "memberwiseQuestionDataList")
	@XmlElement(name = "MemberwiseQuestionData")
	public List<MemberwiseQuestionsVO> getMemberwiseQuestionDataList() {
		return memberwiseQuestionDataList;
	}

	/**
	 * @param memberwiseQuestionDataList the memberwiseQuestionDataList to set
	 */
	public void setMemberwiseQuestionDataList(
			List<MemberwiseQuestionsVO> memberwiseQuestionDataList) {
		this.memberwiseQuestionDataList = memberwiseQuestionDataList;
	}	
	
}
