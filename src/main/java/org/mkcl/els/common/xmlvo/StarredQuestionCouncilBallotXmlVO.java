package org.mkcl.els.common.xmlvo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.domain.MemberBallotAttendance;

@XmlRootElement(name="MemberBallotData")
public class StarredQuestionCouncilBallotXmlVO extends XmlVO {
	
	private String round;
	
	private List<MemberBallotVO> memberBallots;
	
	private String attendance;
	
	private String date;
	
	private String sessionPlace;
	
	public StarredQuestionCouncilBallotXmlVO() {
		
	}
	
	@XmlElement(name = "round")
	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	@XmlElementWrapper(name = "memberBallotList")
	@XmlElement(name = "memberBallot")
	public List<MemberBallotVO> getMemberBallots() {
		return memberBallots;
	}

	public void setMemberBallots(final List<MemberBallotVO> memberBallots) {
		this.memberBallots = memberBallots;
	}

	@XmlElement(name = "attendance")
	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(final String attendance) {
		this.attendance = attendance;
	}

	@XmlElement(name = "sessionPlace")
	public String getSessionPlace() {
		return sessionPlace;
	}

	public void setSessionPlace(final String sessionPlace) {
		this.sessionPlace = sessionPlace;
	}

	@XmlElement(name = "date")
	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}
	
}
