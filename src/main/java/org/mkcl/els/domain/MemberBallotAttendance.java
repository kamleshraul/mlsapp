package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

    private Boolean present;

    @Temporal(TemporalType.TIMESTAMP)
    private Date attendanceTime;

    @Autowired
    private transient MemberBallotAttendanceRepository memberBallotAttendanceRepository;

    public MemberBallotAttendance() {
        super();
    }

    public MemberBallotAttendance(final Session session, final DeviceType deviceType,
            final Member member, final Boolean present,final String locale) {
        super(locale);
        this.session = session;
        this.deviceType = deviceType;
        this.member = member;
        this.present = present;
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


    public Boolean getPresent() {
        return present;
    }

    public void setPresent(final Boolean present) {
        this.present = present;
    }


    public Date getAttendanceTime() {
        return attendanceTime;
    }


    public void setAttendanceTime(final Date attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public static List<MemberBallotAttendance> findAll(
            final Session session,
            final DeviceType questionType, final String locale) {
        return getMemberBallotAttendanceRepository().findAll(
                session,
                questionType,locale);
    }


}
