package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.ParentVO;

@XmlRootElement(name="ProceedingData")
public class ProceedingXMLVO extends XmlVO{

	private List<ParentVO> parentVOs;

	public ProceedingXMLVO() {
		super();
	}

	@XmlElementWrapper(name = "slotList")
	@XmlElement(name = "slot")
	public List<ParentVO> getParentVOs() {
		return parentVOs;
	}

	public void setParentVOs(List<ParentVO> parentVOs) {
		this.parentVOs = parentVOs;
	}
	
	
	
	
	
}
