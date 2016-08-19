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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.CommitteeMemberAttendanceRepository;
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
@Table(name="committee_members_attendance")
public class CommitteeMemberAttendance extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private CommitteeMember committeeMember;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committeeMeeting_id")
	private CommitteeMeeting committeeMeeting;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tourItinerary_id")
	private TourItinerary tourItinerary;
    /** The attendance. */
    private Boolean attendance=false;

	private String startTime;
	
	private String endTime;
    


    /** The  Committee Member attendance repository. */
    @Autowired
    private transient CommitteeMemberAttendanceRepository committeeMemberAttendanceRepository;

    
    /**** Constructors ****/    
    /**
     * Instantiates a new member ballot attendance.
     */
    public CommitteeMemberAttendance() {
        super();
    }

 
	/**
     * Gets the member ballot attendance repository.
     *
     * @return the member ballot attendance repository
     */
    public static CommitteeMemberAttendanceRepository getCommitteeMemberAttendanceRepository() {
    	CommitteeMemberAttendanceRepository committeeMemberAttendanceRepository = new CommitteeMemberAttendance().committeeMemberAttendanceRepository;
        if (committeeMemberAttendanceRepository == null) {
            throw new IllegalStateException(
                    "CommitteeMemberAttendanceRepository has not been injected in CommitteeMemberAttendance Domain");
        }
        return committeeMemberAttendanceRepository;
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
    public static List<CommitteeMemberAttendance> findAll(
            final CommitteeMeeting committeeMeeting,final TourItinerary tourItinerary,
            final Boolean attendance,final String locale) throws ELSException {
        return getCommitteeMemberAttendanceRepository().findAll(committeeMeeting,tourItinerary,attendance,locale);
    }
    public static List<CommitteeMemberAttendance> findAll (
           final TourItinerary tourItinerary,final String locale) throws ELSException {
        return getCommitteeMemberAttendanceRepository().findAll(tourItinerary,locale);
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
        return getCommitteeMemberAttendanceRepository().findMembersByAttendance(session,
                deviceType,attendance,round,locale);
    }
    
    public static List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendance,
            final String locale) throws ELSException {
        return getCommitteeMemberAttendanceRepository().findMembersByAttendance(session,
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
        return getCommitteeMemberAttendanceRepository().findEligibleMembers(session,
                deviceType,locale);
    }

	public static String createAllMemberAttendance(final CommitteeMeeting committeeMeeting,final String locale) throws ELSException {
		return getCommitteeMemberAttendanceRepository().attendanceWithAllMembers(
				committeeMeeting,locale);
	}	
	
	public TourItinerary getTourItinerary() {
		return tourItinerary;
	}


	public void setTourItinerary(TourItinerary tourItinerary) {
		this.tourItinerary = tourItinerary;
	}


	public static Boolean memberAttendanceCreated(final CommitteeMeeting committeeMeeting,
			final String locale) throws ELSException{
		return getCommitteeMemberAttendanceRepository().memberAttendanceCreated(committeeMeeting, locale);
	}

//	public static CommitteeMemberAttendance find(final Session session,
//			final DeviceType questionType,final Member member,final int round,final String locale) throws ELSException {
//		return getCommitteeMemberAttendanceRepository().findEntry(session,
//				questionType,member,round,locale);
//	}

	public static Boolean areMembersLocked(final Session session,
			final DeviceType questionType,final Integer round,final Boolean attendance,final  String locale) throws ELSException {
		return getCommitteeMemberAttendanceRepository().areMembersLocked(session,
				questionType,round,attendance,locale);
	}

	public static Integer findMembersByAttendanceCount(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) throws ELSException {
		return getCommitteeMemberAttendanceRepository().findMembersByAttendanceCount(session,
				deviceType,attendance,round,
				locale);
	}

	public static List<Member> findMembersByAttendance(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale,final int startingRecordToFetch,final int noOfRecordsToFetch) throws ELSException {
		return getCommitteeMemberAttendanceRepository().findMembersByAttendance(session,
				deviceType,attendance,round,
				locale,startingRecordToFetch,noOfRecordsToFetch);
	}

	public static List<Member> findNewMembers(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) throws ELSException {
		return getCommitteeMemberAttendanceRepository().findNewMembers(session,
				deviceType,attendance,round,
				locale);
	}
	
	public static List<Member> findOldMembers(final Session session,
			final DeviceType deviceType,final Boolean attendance,final int round,
			final String locale) throws ELSException{
		return getCommitteeMemberAttendanceRepository().findOldMembers(session,
				deviceType,attendance,round,
				locale);
	}

	/**** Attendance ****/
	public static String createAttendance(final Session session,
			final DeviceType deviceType,final String locale) {
		return getCommitteeMemberAttendanceRepository().createAttendance(session,deviceType,locale);
	}

//	public static List<CommitteeMemberAttendance> findAll(final Session session,
//			final DeviceType deviceType,final String attendance,final String sortOrder,
//			final String locale) throws ELSException {
//		return getCommitteeMemberAttendanceRepository().findAll(session,
//				deviceType,attendance,sortOrder,locale);
//	}

	public static int checkPositionForNullValues(final Session session,
			final DeviceType questionType,final String attendance,final Integer round,
			final String sortOrder,final String locale) throws ELSException {
		return getCommitteeMemberAttendanceRepository().checkPositionForNullValues(session,
				questionType,attendance,round,sortOrder,locale);
	}
	
	public static String updatePositionAbsentMembers(final Session session,
			final DeviceType deviceType,final Integer round,final boolean attendance,final String locale) {
		return getCommitteeMemberAttendanceRepository().updatePositionAbsentMembers(session,
				deviceType,round,attendance,locale);
	}
	
	public static Boolean checkPositionDiscontinous(final Session session,
			final DeviceType deviceType,final boolean attendance,final Integer round,final String locale) throws ELSException {
		return getCommitteeMemberAttendanceRepository().checkPositionDiscontinous(session,
				deviceType,attendance,round,locale);
	}


	public CommitteeMember getCommitteeMember() {
		return committeeMember;
	}


	public void setCommitteeMember(CommitteeMember committeeMember) {
		this.committeeMember = committeeMember;
	}


	public CommitteeMeeting getCommitteeMeeting() {
		return committeeMeeting;
	}


	public void setCommitteeMeeting(CommitteeMeeting committeeMeeting) {
		this.committeeMeeting = committeeMeeting;
	}


	public Boolean getAttendance() {
		return attendance;
	}


	public void setAttendance(Boolean attendance) {
		this.attendance = attendance;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
	/**** Getter Setters of the CommitteeMemberAttendance ****/
	
}
