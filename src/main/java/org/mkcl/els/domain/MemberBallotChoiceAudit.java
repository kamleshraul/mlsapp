package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.repository.MemberBallotChoiceAuditRepository;
import org.mkcl.els.repository.MemberBallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="memberballot_choice_audit")
public class MemberBallotChoiceAudit extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

	/** The session. */
	@ManyToOne
	private Session session;

	/** The device type. */
	@ManyToOne
	private DeviceType deviceType;

	/** The member. */
	@ManyToOne
	private Member member;
	
	/** The choices entries (drafts of member ballot choices). */
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name="memberballot_choice_audit_id", referencedColumnName="id")
	private List<MemberBallotChoiceDraft> choiceEntries;
	
	/** The flag if filled by member. */
	@Column(name="is_filled_by_member")
	boolean filledByMember;
	
	/** Reason for updating question choices. */
	@Column(length=3000)
	private String reasonForChoicesUpdate;
	
	/** The ballot date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date editedOn;
	
	/** The edited by. */
    @Column(length=1000)
    private String editedBy;
	
	/** The edited as (usergroup_type). */
	@ManyToOne
	private UserGroupType editedAs;
	
	/** The member ballot choice audit repository. */
	@Autowired
	private transient MemberBallotChoiceAuditRepository memberBallotChoiceAuditRepository;
	

	// ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new member ballot choice audit.
     */
    public MemberBallotChoiceAudit() {
        super();
    }
    

    // ----------------------------Domain Methods-------------------------//
	/**
	 * Gets the member ballot choice audit repository.
	 *
	 * @return the member ballot choice audit repository
	 */
	public static MemberBallotChoiceAuditRepository getMemberBallotRepository() {
		MemberBallotChoiceAuditRepository memberBallotChoiceAuditRepository = new MemberBallotChoiceAudit().memberBallotChoiceAuditRepository;
		if (memberBallotChoiceAuditRepository == null) {
			throw new IllegalStateException(
			"MemberBallotChoiceAuditRepository has not been injected in MemberBallotChoiceAudit Domain");
		}
		return memberBallotChoiceAuditRepository;
	}

	
	// ----------------------------Getters/Setters------------------------//
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<MemberBallotChoiceDraft> getChoiceEntries() {
		return choiceEntries;
	}

	public void setChoiceEntries(List<MemberBallotChoiceDraft> choiceEntries) {
		this.choiceEntries = choiceEntries;
	}

	public boolean getFilledByMember() {
		return filledByMember;
	}

	public boolean isFilledByMember() {
		return filledByMember;
	}

	public void setIsFilledByMember(boolean filledByMember) {
		this.filledByMember = filledByMember;
	}

	public String getReasonForChoicesUpdate() {
		return reasonForChoicesUpdate;
	}

	public void setReasonForChoicesUpdate(String reasonForChoicesUpdate) {
		this.reasonForChoicesUpdate = reasonForChoicesUpdate;
	}

	public Date getEditedOn() {
		return editedOn;
	}

	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public UserGroupType getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(UserGroupType editedAs) {
		this.editedAs = editedAs;
	}

}