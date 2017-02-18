package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.VishaysuchiVO;


@XmlRootElement(name="VishaysuchiData")
public class VishaysuchiXMLVO extends XmlVO{

	private List<VishaysuchiVO> vishaysuchi;
	
	public VishaysuchiXMLVO(){
		
	}
	
	@XmlElementWrapper(name = "vishaysuchiList")
	@XmlElement(name="vishaysuchi")
	public List<VishaysuchiVO> getVishaysuchi() {
		return vishaysuchi;
	}

	public void setVishaysuchi(List<VishaysuchiVO> vishaysuchi) {
		this.vishaysuchi = vishaysuchi;
	}
}