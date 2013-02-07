/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberBallot.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.vo.MemberBallotFinalBallotVO;
import org.mkcl.els.common.vo.MemberBallotMemberWiseReportVO;
import org.mkcl.els.common.vo.MemberBallotQuestionDistributionVO;
import org.mkcl.els.common.vo.MemberBallotVO;
import org.mkcl.els.repository.MemberBallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberBallot.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="memberballot")
public class MemberBallot extends BaseDomain implements Serializable {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The session. */
	@ManyToOne
	private Session session;

	/** The device type. */
	@ManyToOne
	private DeviceType deviceType;

	/** The member. */
	@ManyToOne
	private Member member;

	/** The ballot date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date ballotDate;

	/** The round. */
	private Integer round;

	/** The question choices. */
	@ManyToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY)
	@JoinTable(name = "memberballot_choice_association",
			joinColumns = { @JoinColumn(name = "memberballot_id",
					referencedColumnName = "id") },
					inverseJoinColumns = { @JoinColumn(name = "memberballot_choice_id",
							referencedColumnName = "id") })
							private List<MemberBallotChoice> questionChoices;

	/** The position. */
	private Integer position;

	/** The attendance. */
	private Boolean attendance;	
	

	/** The member ballot repository. */
	@Autowired
	private transient MemberBallotRepository memberBallotRepository;

	/**
	 * Gets the member ballot repository.
	 *
	 * @return the member ballot repository
	 */
	public static MemberBallotRepository getMemberBallotRepository() {
		MemberBallotRepository memberBallotRepository = new MemberBallot().memberBallotRepository;
		if (memberBallotRepository == null) {
			throw new IllegalStateException(
			"MemberBallotRepository has not been injected in MemberBallot Domain");
		}
		return memberBallotRepository;
	}


	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}


	/**
	 * Sets the session.
	 *
	 * @param session the new session
	 */
	public void setSession(final Session session) {
		this.session = session;
	}


	/**
	 * Gets the device type.
	 *
	 * @return the device type
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}


	/**
	 * Sets the device type.
	 *
	 * @param deviceType the new device type
	 */
	public void setDeviceType(final DeviceType deviceType) {
		this.deviceType = deviceType;
	}


	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}


	/**
	 * Sets the member.
	 *
	 * @param member the new member
	 */
	public void setMember(final Member member) {
		this.member = member;
	}



	/**
	 * Gets the ballot date.
	 *
	 * @return the ballot date
	 */
	public Date getBallotDate() {
		return ballotDate;
	}



	/**
	 * Sets the ballot date.
	 *
	 * @param ballotDate the new ballot date
	 */
	public void setBallotDate(final Date ballotDate) {
		this.ballotDate = ballotDate;
	}



	/**
	 * Gets the round.
	 *
	 * @return the round
	 */
	public Integer getRound() {
		return round;
	}



	/**
	 * Sets the round.
	 *
	 * @param round the new round
	 */
	public void setRound(final Integer round) {
		this.round = round;
	}

	/**
	 * Gets the question choices.
	 *
	 * @return the question choices
	 */
	public List<MemberBallotChoice> getQuestionChoices() {
		return questionChoices;
	}



	/**
	 * Sets the question choices.
	 *
	 * @param questionChoices the new question choices
	 */
	public void setQuestionChoices(final List<MemberBallotChoice> questionChoices) {
		this.questionChoices = questionChoices;
	}


	/**
	 * Gets the attendance.
	 *
	 * @return the attendance
	 */
	public Boolean getAttendance() {
		return attendance;
	}



	/**
	 * Sets the attendance.
	 *
	 * @param attendance the new attendance
	 */
	public void setAttendance(final Boolean attendance) {
		this.attendance = attendance;
	}



	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}



	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(final Integer position) {
		this.position = position;
	}


	/**
	 * Creates the member ballot.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param attendance the attendance
	 * @param round the round
	 * @param locale the locale
	 * @return the boolean
	 */
	public static String createMemberBallot(final Session session,
			final DeviceType deviceType, final boolean attendance, final int round,
			final String locale) {
		return getMemberBallotRepository().createMemberBallot(session,
				deviceType, attendance, round,
				locale);
	}


	/**
	 * Find by member.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param member the member
	 * @param locale the locale
	 * @return the list
	 */
	public static List<MemberBallot> findByMember(final Session session,
			final DeviceType deviceType, final Member member, final String locale) {
		return getMemberBallotRepository().findByMember(session,
				deviceType,member,locale);
	}


	/**
	 * Find by member round.
	 *
	 * @param session the session
	 * @param questionType the question type
	 * @param member the member
	 * @param round the round
	 * @param locale the locale
	 * @return the member ballot
	 */
	public static MemberBallot findByMemberRound(final Session session,
			final DeviceType questionType,final Member member,final int round, final String locale) {
		return getMemberBallotRepository().findByMemberRound(session,
				questionType,member,round,locale);
	}   


	/**
	 * Find primary count.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param locale the locale
	 * @return the integer
	 */
	public static Integer findPrimaryCount(final Session session,
			final DeviceType deviceType, final String locale) {
		return getMemberBallotRepository().findPrimaryCount(session,
				deviceType,locale);
	}


	/**
	 * Update clubbing.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param start the start
	 * @param size the size
	 * @param locale the locale
	 * @return the boolean
	 */
	public static Boolean updateClubbing(final Session session,
			final DeviceType deviceType, final int start, final int size, final String locale) {
		return getMemberBallotRepository().updateClubbing(session,
				deviceType,start,size,locale);
	}


	/**
	 * Creates the final ballot.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param group the group
	 * @param questionDates the question dates
	 * @param locale the locale
	 * @param totalRounds 
	 */
	public static Boolean createFinalBallot(final Session session,
			final DeviceType deviceType, final Group group, final String answeringDate,
			final String locale,final String firstBatchSubmissionDate,final int totalRounds) {
		return getMemberBallotRepository().createFinalBallot(session,
				deviceType,group,answeringDate,
				locale,firstBatchSubmissionDate,totalRounds);
	}

	public static List<MemberBallotFinalBallotVO> viewFinalBallot(final Session session,
			final DeviceType deviceType,final String answeringDate,
			final String locale){
		return getMemberBallotRepository().viewBallot(session, deviceType,answeringDate, locale);
	}

	public static Boolean deleteTempEntries() {
		return getMemberBallotRepository().deleteTempEntries();
	}

	public static List<MemberBallotVO> viewMemberBallotVO(final Session session,
			final DeviceType questionType,final Boolean attendance,final Integer round,
			final String locale) {
		return getMemberBallotRepository().viewMemberBallotVO(session,
				questionType,attendance,round,locale);
	}	

	public static List<MemberBallotVO> viewMemberBallotVO(final Session session,
			final DeviceType questionType,final Boolean attendance,final Integer round,
			final Group group,final String locale) {
		return getMemberBallotRepository().viewMemberBallotVO(session,
				questionType,attendance,round,group,locale);
	}

	public static List<MemberBallotVO> viewMemberBallotVO(final Session session,
			final DeviceType questionType,final Boolean attendance,final Integer round,
			final Group group,final QuestionDates answeringDate,final String locale) {
		return getMemberBallotRepository().viewMemberBallotVO(session,
				questionType,attendance,round,group,answeringDate,locale);
	}


	public static Boolean createMemberBallotChoices(
			List<MemberBallot> memberBallots, final List<Question> questions, int rounds, Map<String, Integer> noofQuestionsInEachRound, String locale) {
		return getMemberBallotRepository().createMemberBallotChoices(memberBallots,
				questions,rounds,noofQuestionsInEachRound,locale);
	}
	
	public static MemberBallotMemberWiseReportVO findMemberWiseReportVO(
			final Session session,final DeviceType questionType,final Member member,
			final String locale){
		return getMemberBallotRepository().findMemberWiseReportVO(
				session,questionType,member,
				locale);		
	}


	public static List<MemberBallotQuestionDistributionVO> viewQuestionDistribution(
			final Session session,final DeviceType questionType,final String locale) {
		return getMemberBallotRepository().viewQuestionDistribution(
				session,questionType,locale);
	}
}
