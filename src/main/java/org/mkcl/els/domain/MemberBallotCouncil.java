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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.MemberBallotCouncilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="member_ballot_council")
@JsonIgnoreProperties({"member","questionChoices","session","deviceType"})
public class MemberBallotCouncil extends BaseDomain implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
	private Member member;

	private Integer round;

	@ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinTable(name = "member_ballot_choice_association_council",
			joinColumns = { @JoinColumn(name = "member_ballot_id",
					referencedColumnName = "id") },
					inverseJoinColumns = { @JoinColumn(name = "mamber_ballot_choice_id",
							referencedColumnName = "id") })
							private List<MemberBallotChoiceCouncil> questionChoices;

	@Temporal(TemporalType.TIMESTAMP)
	private Date ballotDate;

	@ManyToOne(fetch=FetchType.LAZY)
	private Session session;

	@ManyToOne(fetch=FetchType.LAZY)
	private DeviceType deviceType;

	private Integer position;

	@Autowired
    private transient MemberBallotCouncilRepository memberBallotCouncilRepository;

	public MemberBallotCouncil() {
		super();
	}

	public static MemberBallotCouncilRepository getMemberBallotCouncilRepository() {
        MemberBallotCouncilRepository memberBallotCouncilRepository = new MemberBallotCouncil().memberBallotCouncilRepository;
        if (memberBallotCouncilRepository == null) {
            throw new IllegalStateException(
                    "MemberBallotCouncilRepository has not been injected in MemberBallotCouncil Domain");
        }
        return memberBallotCouncilRepository;
    }

	public static Boolean createMemberBallot(final Session session,final DeviceType deviceType,final Integer round,final String locale){
		return getMemberBallotCouncilRepository().createMemberBallot(session, deviceType,round, locale);
	}

	public static List<Member> viewMemberBallot(final Session session,
			final DeviceType deviceType, final int round, final String locale) {
		return getMemberBallotCouncilRepository().viewMemberBallot(session,
				deviceType, round, locale);
	}

	public Member getMember() {
		return member;
	}

	public void setMember(final Member member) {
		this.member = member;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(final Integer round) {
		this.round = round;
	}

	public List<MemberBallotChoiceCouncil> getQuestionChoices() {
		return questionChoices;
	}

	public void setQuestionChoices(final List<MemberBallotChoiceCouncil> questionChoices) {
		this.questionChoices = questionChoices;
	}

	public Date getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(final Date ballotDate) {
		this.ballotDate = ballotDate;
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(final Integer position) {
		this.position = position;
	}
}
