package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.ParentVO;
import org.mkcl.els.common.vo.RevisionHistoryVO;

@XmlRootElement(name="CurrentStatusData")
public class CurrentStatusXMLVO extends XmlVO{
	
	private String number;
	
	private String deviceType;
	
	private String deviceTypeName;
	
	private String primaryMemberName;	

	private List<RevisionHistoryVO> revisionHistoryVOs;

	public CurrentStatusXMLVO() {
		super();
	}	

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceTypeName() {
		return deviceTypeName;
	}

	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}

	public String getPrimaryMemberName() {
		return primaryMemberName;
	}

	public void setPrimaryMemberName(String primaryMemberName) {
		this.primaryMemberName = primaryMemberName;
	}

	@XmlElementWrapper(name = "revisionHistoryVOs")
	@XmlElement(name = "revisionHistoryVO")
	public List<RevisionHistoryVO> getRevisionHistoryVOs() {
		return revisionHistoryVOs;
	}

	public void setRevisionHistoryVOs(List<RevisionHistoryVO> revisionHistoryVOs) {
		this.revisionHistoryVOs = revisionHistoryVOs;
	}	
	
}
