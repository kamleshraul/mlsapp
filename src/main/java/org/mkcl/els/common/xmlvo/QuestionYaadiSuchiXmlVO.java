package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.MinistryVO;
import org.mkcl.els.common.vo.RoundVO;

@XmlRootElement(name="DeviceData")
public class QuestionYaadiSuchiXmlVO extends XmlVO {
	
	private String houseType;
	
	private String sessionNumber;
	
	private String sessionType;
	
	private String sessionYear;
	
	private String sessionPlace;
	
	private String userName;
	
	private String userRole;
	
	private List<MinistryVO> ministryVOs;
	
	private String answeringDay;
	
	private String displayAnsweringDay;
	
	private String answeringDate;
	
	private String displayAnsweringDate;
	
	private String answeringDateInIndianCalendar;
	
	private String displayAnsweringDateInIndianCalendar;
	
	/**** used for only yaadi ****/
	private List<DeviceVO> deviceVOs;
	
	private String totalNumberOfDevices;
	
	/**** used for only suchi ****/
	private List<RoundVO> roundVOs;	
	
	private String yaadiNumber;
	
	private String yaadiNumberInText;
		
	public QuestionYaadiSuchiXmlVO() {
		
	}
	
	@XmlElement(name = "houseType")
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	@XmlElement(name = "sessionNumber")
	public String getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	@XmlElement(name = "sessionType")
	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	@XmlElement(name = "sessionYear")
	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	@XmlElement(name = "sessionPlace")
	public String getSessionPlace() {
		return sessionPlace;
	}

	public void setSessionPlace(String sessionPlace) {
		this.sessionPlace = sessionPlace;
	}

	@XmlElement(name = "userName")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name = "userRole")
	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	@XmlElementWrapper(name = "ministryVOs")
	@XmlElement(name = "ministryVO")
	public List<MinistryVO> getMinistryVOs() {
		return ministryVOs;
	}

	public void setMinistryVOs(List<MinistryVO> ministryVOs) {
		this.ministryVOs = ministryVOs;
	}

	@XmlElement(name = "answeringDay")
	public String getAnsweringDay() {
		return answeringDay;
	}

	public void setAnsweringDay(String answeringDay) {
		this.answeringDay = answeringDay;
	}

	@XmlElement(name = "displayAnsweringDay")
	public String getDisplayAnsweringDay() {
		return displayAnsweringDay;
	}

	public void setDisplayAnsweringDay(String displayAnsweringDay) {
		this.displayAnsweringDay = displayAnsweringDay;
	}

	@XmlElement(name = "answeringDate")
	public String getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(String answeringDate) {
		this.answeringDate = answeringDate;
	}

	@XmlElement(name = "displayAnsweringDate")
	public String getDisplayAnsweringDate() {
		return displayAnsweringDate;
	}

	public void setDisplayAnsweringDate(String displayAnsweringDate) {
		this.displayAnsweringDate = displayAnsweringDate;
	}
	
	@XmlElement(name = "answeringDateInIndianCalendar")
	public String getAnsweringDateInIndianCalendar() {
		return answeringDateInIndianCalendar;
	}

	public void setAnsweringDateInIndianCalendar(String answeringDateInIndianCalendar) {
		this.answeringDateInIndianCalendar = answeringDateInIndianCalendar;
	}
	
	@XmlElement(name = "displayAnsweringDateInIndianCalendar")
	public String getDisplayAnsweringDateInIndianCalendar() {
		return displayAnsweringDateInIndianCalendar;
	}

	public void setDisplayAnsweringDateInIndianCalendar(String displayAnsweringDateInIndianCalendar) {
		this.displayAnsweringDateInIndianCalendar = displayAnsweringDateInIndianCalendar;
	}

	@XmlElementWrapper(name = "deviceVOs")
	@XmlElement(name = "deviceVO")
	public List<DeviceVO> getDeviceVOs() {
		return deviceVOs;
	}

	public void setDeviceVOs(List<DeviceVO> deviceVOs) {
		this.deviceVOs = deviceVOs;
	}

	@XmlElement(name = "totalNumberOfDevices")
	public String getTotalNumberOfDevices() {
		return totalNumberOfDevices;
	}

	public void setTotalNumberOfDevices(String totalNumberOfDevices) {
		this.totalNumberOfDevices = totalNumberOfDevices;
	}

	@XmlElementWrapper(name = "roundVOs")
	@XmlElement(name = "roundVO")
	public List<RoundVO> getRoundVOs() {
		return roundVOs;
	}

	public void setRoundVOs(List<RoundVO> roundVOs) {
		this.roundVOs = roundVOs;
	}

	@XmlElement(name = "yaadiNumber")
	public String getYaadiNumber() {
		return yaadiNumber;
	}

	public void setYaadiNumber(String yaadiNumber) {
		this.yaadiNumber = yaadiNumber;
	}

	@XmlElement(name = "yaadiNumberInText")
	public String getYaadiNumberInText() {
		return yaadiNumberInText;
	}

	public void setYaadiNumberInText(String yaadiNumberInText) {
		this.yaadiNumberInText = yaadiNumberInText;
	}	
	
}
