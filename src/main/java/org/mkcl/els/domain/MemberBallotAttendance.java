/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberBallotAttendance.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.repository.MemberBallotAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberBallotAttendance.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="memberballot_attendance")
public class MemberBallotAttendance extends BaseDomain implements Serializable{

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

    /** The attendance. */
    private Boolean attendance=false;

    /** The position. */
    private Integer position;
    
    private Integer round;
    
    private Boolean locked=false;
    

    /** The member ballot attendance repository. */
    @Autowired
    private transient MemberBallotAttendanceRepository memberBallotAttendanceRepository;

    /**
     * Instantiates a new member ballot attendance.
     */
    public MemberBallotAttendance() {
        super();
    }

    /**
     * Instantiates a new member ballot attendance.
     *
     * @param session the session
     * @param deviceType the device type
     * @param member the member
     * @param attendance the attendance
     * @param locale the locale
     */
    public MemberBallotAttendance(final Session session, final DeviceType deviceType,
            final Member member, final Boolean attendance,final Integer round,final Boolean locked,final String locale) {
        super(locale);
        this.session = session;
        this.deviceType = deviceType;
        this.member = member;
        this.attendance = attendance;
        this.round=round;
        this.locked=locked;
    }

    /**
     * Gets the member ballot attendance repository.
     *
     * @return the member ballot attendance repository
     */
    public static MemberBallotAttendanceRepository getMemberBallotAttendanceRepository() {
        MemberBallotAttendanceRepository memberBallotAttendanceRepository = new MemberBallotAttendance().memberBallotAttendanceRepository;
        if (memberBallotAttendanceRepository == null) {
            throw new IllegalStateException(
                    "MemberBallotAttendanceRepository has not been injected in MemberBallotAttendance Domain");
        }
        return memberBallotAttendanceRepository;
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
     * Find all.
     *
     * @param session the session
     * @param questionType the question type
     * @param attendance the attendance
     * @param round 
     * @param sortBy the sort by
     * @param locale the locale
     * @return the list
     */
    public static List<MemberBallotAttendance> findAll(
            final Session session, final DeviceType questionType,
            final String attendance,final Integer round, final String sortBy,
            final String locale) {
        return getMemberBallotAttendanceRepository().findAll(
                session, questionType,attendance,round,sortBy, locale);
    }

    /**
     * Find members by attendance.
     *
     * @param session the session
     * @param deviceType the device type
     * @param attendanceType the attendance type
     * @param locale the locale
     * @return the list
     */
    public static List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendance,
            final Integer round,final String locale) {
        return getMemberBallotAttendanceRepository().findMembersByAttendance(session,
                deviceType,attendance,round,locale);
    }

    /**
     * Find eligible members.
     *
     * @param session the session
     * @param deviceType the device type
     * @param locale the locale
     * @return the list
     */
    public static List<Member> findEligibleMembers(final Session session,
            final DeviceType deviceType, final String locale) {
        return getMemberBallotAttendanceRepository().findEligibleMembers(session,
                deviceType,locale);
    }

	public static String createMemberBallotAttendance(final Session session,
			final DeviceType questionType,final Integer round, final String locale) {
		return getMemberBallotAttendanceRepository().createMemberBallotAttendance(
				session,questionType,round,locale);
	}
	
	public static Boolean memberBallotCreated(final Session session, final DeviceType questionType,
			final Integer round, final String locale){
		return getMemberBallotAttendanceRepository().memberBallotCreated(session, questionType,round, locale);
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public Integer getRound() {
		return round;
	}

	public static MemberBallotAttendance find(final Session session,
			final DeviceType questionType,final Member member,final int round,final String locale) {
		return getMemberBallotAttendanceRepository().findEntry(session,
				questionType,member,round,locale);
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getLocked() {
		return locked;
	}	

	public static Boolean areMembersLocked(final Session session,
			final DeviceType questionType,final Integer round,final Boolean attendance,final  String locale) {
		return getMemberBallotAttendanceRepository().areMembersLocked(session,
				questionType,round,attendance,locale);
	}

	public static Integer findMembersByAttendanceCount(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) {
		return getMemberBallotAttendanceRepository().findMembersByAttendanceCount(session,
				deviceType,attendance,round,
				locale);
	}

	public static List<Member> findMembersByAttendance(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale,final int startingRecordToFetch,final int noOfRecordsToFetch) {
		return getMemberBallotAttendanceRepository().findMembersByAttendance(session,
				deviceType,attendance,round,
				locale,startingRecordToFetch,noOfRecordsToFetch);
	}

	public static List<Member> findNewMembers(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) {
		return getMemberBallotAttendanceRepository().findNewMembers(session,
				deviceType,attendance,round,
				locale);
	}
	
	public static List<Member> findOldMembers(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale){
		return getMemberBallotAttendanceRepository().findOldMembers(session,
				deviceType,attendance,round,
				locale);
	}
	
	public String formatPosition(){
		try {
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(getLocale());
			return format.format(getPosition());
		} catch (Exception e) {			
			e.printStackTrace();
			return String.valueOf(0);
		}
	}

}
