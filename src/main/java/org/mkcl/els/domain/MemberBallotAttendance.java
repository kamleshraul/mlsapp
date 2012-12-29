package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.repository.MemberBallotAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name="memberballot_attendance")
public class MemberBallotAttendance extends BaseDomain implements Serializable{

    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Session session;

    @ManyToOne
    private DeviceType deviceType;

    @ManyToOne
    private Member member;

    private Boolean attendance;

    private Integer position;

    @Autowired
    private transient MemberBallotAttendanceRepository memberBallotAttendanceRepository;

    public MemberBallotAttendance() {
        super();
    }

    public MemberBallotAttendance(final Session session, final DeviceType deviceType,
            final Member member, final Boolean attendance,final String locale) {
        super(locale);
        this.session = session;
        this.deviceType = deviceType;
        this.member = member;
        this.attendance = attendance;
    }

    public static MemberBallotAttendanceRepository getMemberBallotAttendanceRepository() {
        MemberBallotAttendanceRepository memberBallotAttendanceRepository = new MemberBallotAttendance().memberBallotAttendanceRepository;
        if (memberBallotAttendanceRepository == null) {
            throw new IllegalStateException(
                    "MemberBallotAttendanceRepository has not been injected in MemberBallotAttendance Domain");
        }
        return memberBallotAttendanceRepository;
    }

    public Session getSession() {
        return session;
    }


    public void setSession(final Session session) {
        this.session = session;
    }


    public DeviceType getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }


    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }


    public Boolean getAttendance() {
        return attendance;
    }


    public void setAttendance(final Boolean attendance) {
        this.attendance = attendance;
    }

    public Integer getPosition() {
        return position;
    }


    public void setPosition(final Integer position) {
        this.position = position;
    }

    public static List<MemberBallotAttendance> findAll(
            final Session session, final DeviceType questionType,
            final String attendance,final String sortBy,
            final String locale) {
        return getMemberBallotAttendanceRepository().findAll(
                session, questionType,attendance,sortBy, locale);
    }

    public static List<Member> findMembersByAttendance(final Session session,
            final DeviceType deviceType, final Boolean attendanceType, final String locale) {
        return getMemberBallotAttendanceRepository().findMembersByAttendance(session,
                deviceType,attendanceType,locale);
    }

    public static List<Member> findEligibleMembers(final Session session,
            final DeviceType deviceType, final String locale) {
        return getMemberBallotAttendanceRepository().findEligibleMembers(session,
                deviceType,locale);
    }



}
