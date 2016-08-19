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
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
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
    
    /** The party. */
    @ManyToOne
    private Party party;

    /** The attendance. */
    private Boolean attendance=false;

    /** The position. */
    private Integer position;
    
    private Integer round;
    
    private Boolean locked=false;
    
    private String createdBy;
    
    private String createdAs;
    
    private String editedBy;
    
    private String editedAs;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedOn;
    
    /**** For absent members ****/
    private Boolean positionDiscontious;

    /** The member ballot attendance repository. */
    @Autowired
    private transient MemberBallotAttendanceRepository memberBallotAttendanceRepository;

    
    /**** Constructors ****/    
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

    public MemberBallotAttendance(final Session session,final DeviceType deviceType,
			final Member member,final Boolean attendance,final String locale) {
		super(locale);
		this.session = session;
		this.deviceType = deviceType;
		this.member = member;
		this.attendance = attendance;
	}
    
    public MemberBallotAttendance(final Session session,final DeviceType deviceType,
			final Party party,final Boolean attendance,final String locale) {
		super(locale);
		this.session = session;
		this.deviceType = deviceType;
		this.party = party;
		this.attendance = attendance;
	}

    /**** Domain methods ****/
    
    public String formatPosition(){
		try {
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(getLocale());
			return format.format(getPosition().intValue());
		} catch (Exception e) {			
			e.printStackTrace();
			return String.valueOf(0);
		}
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
     * Find all.
     *
     * @param session the session
     * @param questionType the question type
     * @param attendance the attendance
     * @param round 
     * @param sortBy the sort by
     * @param locale the locale
     * @return the list
     * @throws ELSException 
     */
    public static List<MemberBallotAttendance> findAll(
            final Session session, final DeviceType questionType,
            final String attendance,final Integer round, final String sortBy,
            final String locale) throws ELSException {
        return getMemberBallotAttendanceRepository().findAll(session, questionType,attendance,round,sortBy, locale);
    }

    /**
     * Find members by attendance.
     *
     * @param session the session
     * @param deviceType the device type
     * @param attendanceType the attendance type
     * @param locale the locale
     * @return the list
     * @throws ELSException 
     */
    public static List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendance,
            final Integer round,final String locale) throws ELSException {
        return getMemberBallotAttendanceRepository().findMembersByAttendance(session,
                deviceType,attendance,round,locale);
    }
    
    public static List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendance,
            final String locale) throws ELSException {
        return getMemberBallotAttendanceRepository().findMembersByAttendance(session,
                deviceType,attendance,locale);
    }
    
    
    
    public static List<Party> findPartiesByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendance,
            final String locale) throws ELSException {
        return getMemberBallotAttendanceRepository().findPartiesByAttendance(session,
                deviceType,attendance,locale);
    }

    /**
     * Find eligible members.
     *
     * @param session the session
     * @param deviceType the device type
     * @param locale the locale
     * @return the list
     * @throws ELSException 
     */
    public static List<Member> findEligibleMembers(final Session session,
            final DeviceType deviceType, final String locale) throws ELSException {
        return getMemberBallotAttendanceRepository().findEligibleMembers(session,
                deviceType,locale);
    }

	public static String createMemberBallotAttendance(final Session session,
			final DeviceType questionType,final Integer round, final String createdBy,final String createdAs,final String locale) {
		return getMemberBallotAttendanceRepository().createMemberBallotAttendance(
				session,questionType,round,createdBy,createdAs,locale);
	}	
	
	public static Boolean memberBallotCreated(final Session session, final DeviceType questionType,
			final Integer round, final String locale) throws ELSException{
		return getMemberBallotAttendanceRepository().memberBallotCreated(session, questionType,round, locale);
	}

	public static MemberBallotAttendance find(final Session session,
			final DeviceType questionType,final Member member,final int round,final String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().findEntry(session,
				questionType,member,round,locale);
	}

	public static Boolean areMembersLocked(final Session session,
			final DeviceType questionType,final Integer round,final Boolean attendance,final  String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().areMembersLocked(session,
				questionType,round,attendance,locale);
	}

	public static Integer findMembersByAttendanceCount(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().findMembersByAttendanceCount(session,
				deviceType,attendance,round,
				locale);
	}

	public static List<Member> findMembersByAttendance(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale,final int startingRecordToFetch,final int noOfRecordsToFetch) throws ELSException {
		return getMemberBallotAttendanceRepository().findMembersByAttendance(session,
				deviceType,attendance,round,
				locale,startingRecordToFetch,noOfRecordsToFetch);
	}

	public static List<Member> findNewMembers(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().findNewMembers(session,
				deviceType,attendance,round,
				locale);
	}
	
	public static List<Member> findOldMembers(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) throws ELSException{
		return getMemberBallotAttendanceRepository().findOldMembers(session,
				deviceType,attendance,round,
				locale);
	}

	/**** Attendance ****/
	public static String createAttendance(final Session session,
			final DeviceType deviceType,final String locale) {
		return getMemberBallotAttendanceRepository().createAttendance(session,deviceType,locale);
	}

	public static List<MemberBallotAttendance> findAll(final Session session,
			final DeviceType deviceType,final String attendance,final String sortOrder,
			final String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().findAll(session,
				deviceType,attendance,sortOrder,locale);
	}

	public static int checkPositionForNullValues(final Session session,
			final DeviceType questionType,final String attendance,final Integer round,
			final String sortOrder,final String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().checkPositionForNullValues(session,
				questionType,attendance,round,sortOrder,locale);
	}
	
	public static String updatePositionAbsentMembers(final Session session,
			final DeviceType deviceType,final Integer round,final boolean attendance,final String locale) {
		return getMemberBallotAttendanceRepository().updatePositionAbsentMembers(session,
				deviceType,round,attendance,locale);
	}
	
	public static Boolean checkPositionDiscontinous(final Session session,
			final DeviceType deviceType,final boolean attendance,final Integer round,final String locale) throws ELSException {
		return getMemberBallotAttendanceRepository().checkPositionDiscontinous(session,
				deviceType,attendance,round,locale);
	}
	
	
	/**** Getter Setters of the MemberBallotAttendance ****/
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
     * Sets the party
     * @return
     */
    public Party getParty() {
		return party;
	}

	/**
	 * returns the party
	 *  
	 * @param party
	 */
	public void setParty(Party party) {
		this.party = party;
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
     * @param round
     */
    public void setRound(Integer round) {
		this.round = round;
	}

	/**
	 * @return
	 */
	public Integer getRound() {
		return round;
	}
	
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getLocked() {
		return locked;
	}	

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedAs(String createdAs) {
		this.createdAs = createdAs;
	}

	public String getCreatedAs() {
		return createdAs;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setPositionDiscontious(Boolean positionDiscontious) {
		this.positionDiscontious = positionDiscontious;
	}

	public Boolean getPositionDiscontious() {
		return positionDiscontious;
	}
}
