package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.TestVO;

@XmlRootElement(name="TestReportData")
public class TestXmlVO extends XmlVO {
	
	private String testReportName;
	
	private String testData;
	
	private List<TestVO> testList;
		
	public TestXmlVO() {
		
	}

	@XmlElement(name = "testReportName")
	public String getTestReportName() {
		return testReportName;
	}

	public void setTestReportName(String testReportName) {
		this.testReportName = testReportName;
	}

	@XmlElement(name = "testData")
	public String getTestData() {
		return testData;
	}

	public void setTestData(final String testData) {
		this.testData = testData;
	}
	
	@XmlElementWrapper(name = "testList")
	@XmlElement(name = "testListUnit")
	public List<TestVO> getTestList() {
		return testList;
	}

	public void setTestList(final List<TestVO> testList) {
		this.testList = testList;
	}
	
}
