package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import org.mkcl.els.repository.MemberBallotRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name="memberballot")
public class MemberBallot extends BaseDomain implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Session session;

    @ManyToOne
    private DeviceType deviceType;

    @ManyToOne
    private Member member;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ballotDate;

    private Integer round;

    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinTable(name = "memberballot_choice_association",
            joinColumns = { @JoinColumn(name = "memberballot_id",
                    referencedColumnName = "id") },
                    inverseJoinColumns = { @JoinColumn(name = "mamberballot_choice_id",
                            referencedColumnName = "id") })
                            private List<MemberBallotChoice> questionChoices;

    private Integer position;

    private Boolean attendance;

    @Autowired
    private transient MemberBallotRepository memberBallotRepository;

    public static MemberBallotRepository getMemberBallotRepository() {
        MemberBallotRepository memberBallotRepository = new MemberBallot().memberBallotRepository;
        if (memberBallotRepository == null) {
            throw new IllegalStateException(
                    "MemberBallotRepository has not been injected in MemberBallot Domain");
        }
        return memberBallotRepository;
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



    public Date getBallotDate() {
        return ballotDate;
    }



    public void setBallotDate(final Date ballotDate) {
        this.ballotDate = ballotDate;
    }



    public Integer getRound() {
        return round;
    }



    public void setRound(final Integer round) {
        this.round = round;
    }

    public List<MemberBallotChoice> getQuestionChoices() {
        return questionChoices;
    }



    public void setQuestionChoices(final List<MemberBallotChoice> questionChoices) {
        this.questionChoices = questionChoices;
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


    public static Boolean createMemberBallot(final Session session,
            final DeviceType deviceType, final boolean attendance, final int round,
            final String locale) {
        return getMemberBallotRepository().createMemberBallot(session,
                deviceType, attendance, round,
                locale);
    }


    public static List<MemberBallot> viewMemberBallot(final Session session,
            final DeviceType deviceType,final boolean attendance, final int round,
            final String locale) {
        return getMemberBallotRepository().viewMemberBallot(session,
                deviceType,attendance,round,
                locale);
    }


    public static List<MemberBallot> findByMember(final Session session,
            final DeviceType deviceType, final Member member, final String locale) {
        return getMemberBallotRepository().findByMember(session,
                deviceType,member,locale);
    }



}
