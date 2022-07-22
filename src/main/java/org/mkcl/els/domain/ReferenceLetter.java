package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.repository.ReferenceLetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "reference_letters")
public class ReferenceLetter extends BaseDomain implements Serializable {
	
	// ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@Column(length = 100)
	private String houseType;
    
	@Column(length = 100)
    private String deviceType;
	
	/* session of letter (optional) */
	@Column(length = 20)
	private String sessionId;
	
	/* group reference (optional) */
	@Column(length = 20)
	private String groupId;
	
	/* ministry reference (optional) */
	@Column(length = 20)
	private String ministryId;
	
	/* subdepartment reference (optional) */
	@Column(name="subdepartment_id", length = 20)
	private String subDepartmentId;
	
	/* letter's copy type (advance_copy / tentative_copy / revised_copy / final_copy) */
	@Column(length = 100)
    private String copyType;
	
	/* single device id or comma separated devices ids */
    @Column(length = 10000)
	private String deviceIds;
    
    /* parent device id */
    @Column(length = 20)
	private String parentDeviceId;
    
    /* comma separated clubbed device ids */
    @Column(length = 10000)
	private String clubbedDeviceIds;
    
    /* comma separated member ids of primary member, supporting members and clubbed devices members and their supporting members at the time of letter generation */
    @Column(length = 10000)
	private String memberIds;
	
    /* type or purpose of reference (intimation / other purpose) */
    @Column(length = 300)
    private String referenceFor;
	
    /* department_id or member_id or custom names to whom reference is being given */
    @Column(length = 30000)
	private String referenceTo;
    
    @Column(length = 20)
    private String referenceNumber;
    
    @Column(length = 20)
    private String referredNumber;
	
	/** The date of reference letter sent. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dispatchDate;
    
    /** The date of reference letter received. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date acknowledgementDate;
    
    @Temporal(TemporalType.DATE)
    private Date answeringDate;
    
    /* comma separated usernames of reference receivers */
    @Column(length = 10000)
    private String receivers;
    
    /** The reference letter subject (subject / title). */
	@Column(length = 30000)
	private String subject;
    
    /** The reference letter content (question_text / details / notice_content). */
	@Column(length = 30000)
	private String noticeContent;
    
    /** The status (dispatched/acknowledged/timeout) **/
	@Column(length = 100)
    private String status;
	
	/* username of sender who generates the letter */
	@Column(length = 100)
	private String generatedBy;

    @Autowired
    private transient ReferenceLetterRepository referenceLetterRepository;
    
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new reference letter.
     */
	public ReferenceLetter() {
	   super();
	}
	
	// ---------------------------------Domain Methods----------------------//
	public static ReferenceLetterRepository getReferenceLetterRepository() {
		ReferenceLetterRepository referenceLetterRepository = new ReferenceLetter().referenceLetterRepository;
        if (referenceLetterRepository == null) {
            throw new IllegalStateException(
                    "ReferenceLetterRepository has not been injected in ReferenceLetter Domain");
        }
        return referenceLetterRepository;
    }
	
	public static ReferenceLetter findLatestByFieldNames(final Map<String, String> referenceLetterIdentifiers, final String locale) throws ELSException {
		ReferenceLetter latestReferenceLetterByGivenFieldNames = null;
		List<ReferenceLetter> referenceLettersByGivenFieldNames = ReferenceLetter.findAllByFieldNames(ReferenceLetter.class, referenceLetterIdentifiers, "dispatchDate", ApplicationConstants.DESC, locale);
		if(referenceLettersByGivenFieldNames!=null && !referenceLettersByGivenFieldNames.isEmpty()) {
			latestReferenceLetterByGivenFieldNames = referenceLettersByGivenFieldNames.get(0);
		}
		return latestReferenceLetterByGivenFieldNames;
	}
	
	public static ReferenceLetter findLatestHavingGivenDevice(final String deviceId, final String referenceFor, final String locale) {
		return getReferenceLetterRepository().findLatestHavingGivenDevice(deviceId, referenceFor, locale);
	}
	

	// ---------------------------------Getters and Setters----------------------//
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getMinistryId() {
		return ministryId;
	}

	public void setMinistryId(String ministryId) {
		this.ministryId = ministryId;
	}

	public String getSubDepartmentId() {
		return subDepartmentId;
	}

	public void setSubDepartmentId(String subDepartmentId) {
		this.subDepartmentId = subDepartmentId;
	}

	public String getCopyType() {
		return copyType;
	}

	public void setCopyType(String copyType) {
		this.copyType = copyType;
	}

	public String getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(String deviceIds) {
		this.deviceIds = deviceIds;
	}

	public String getParentDeviceId() {
		return parentDeviceId;
	}

	public void setParentDeviceId(String parentDeviceId) {
		this.parentDeviceId = parentDeviceId;
	}

	public String getClubbedDeviceIds() {
		return clubbedDeviceIds;
	}

	public void setClubbedDeviceIds(String clubbedDeviceIds) {
		this.clubbedDeviceIds = clubbedDeviceIds;
	}

	public String getMemberIds() {
		return memberIds;
	}

	public void setMemberIds(String memberIds) {
		this.memberIds = memberIds;
	}

	public String getReferenceFor() {
		return referenceFor;
	}

	public void setReferenceFor(String referenceFor) {
		this.referenceFor = referenceFor;
	}

	public String getReferenceTo() {
		return referenceTo;
	}

	public void setReferenceTo(String referenceTo) {
		this.referenceTo = referenceTo;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getReferredNumber() {
		return referredNumber;
	}

	public void setReferredNumber(String referredNumber) {
		this.referredNumber = referredNumber;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public Date getAcknowledgementDate() {
		return acknowledgementDate;
	}

	public void setAcknowledgementDate(Date acknowledgementDate) {
		this.acknowledgementDate = acknowledgementDate;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}	
    
}
