package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OptimisticLockException;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.repository.AdjournmentMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(name="adjournmentmotions")
@JsonIgnoreProperties({"houseType", "session", "type", "supportingMembers", 
	"parent", "clubbedEntities", "referencedAdjournmentMotion", "drafts"})
public class AdjournmentMotion extends Device implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//=============== BASIC ATTRIBUTES ====================
	/** The house type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="housetype_id")
	private HouseType houseType;

	/** The session. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;

	/** The type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType type; 

	/** The number. */
	private Integer number;
	
	/** The number in sequence of admitted count for the session. */
	private Integer admissionNumber;
	
	/** The primary member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="adjournmentmotions_supportingmembers",
	joinColumns={@JoinColumn(name="adjournmentmotion_id", referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
	private List<SupportingMember> supportingMembers;
	
	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/** The creation date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	/** The created by. */
	@Column(length=1000)
	private String createdBy;

	/**** The clerk name ****/
	private String dataEnteredBy;
	
	/** The adjourning date. */
	@Temporal(TemporalType.DATE)
	private Date adjourningDate;
	
	/** The adjourning date. */
	@Transient
	private String formattedAdjourningDate;
	
	/** The subject. */
    @Column(length=30000)
    private String subject;
    
    /** The revised subject. */
    @Column(length=30000)
    private String revisedSubject;
   
    /** The notice content. */
    @Column(length=30000)
    private String noticeContent;
    
    /** The revised notice content. */
    @Column(length=30000)
    private String revisedNoticeContent;
	
	/** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;
   
    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;

	/** The edited on. */
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name="editedon")
	private Date editedOn; 

	/** The edited by. */
	@Column(length=1000)
	private String editedBy;

	/** The edited as. */
	@Column(length=1000)
	private String editedAs;
	
	/** ** The Status ***. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;

	/** ** The Internal Status ***. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internalstatus_id")
	private Status internalStatus;

	/**** The Recommendation Status. ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendationstatus_id")
	private Status recommendationStatus;
	
	/** ** Remarks ***. */
	@Column(length=30000)
	private String remarks;
	
	/** ** Decision Remarks ***. */
	@Column(length=30000)
	private String remarksAboutDecision;
	
	/** ** Reply ***. */
	@Column(length=30000)
	private String reply;
	
	/** ** Rejection Reason ***. */
	@Column(length=30000)
	private String rejectionReason;
	
	/** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private AdjournmentMotion parent;
	
	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="adjournmentmotions_clubbingentities", 
	joinColumns={@JoinColumn(name="adjournmentmotion_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;

	/** The referenced adjournment motion. */
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private ReferencedEntity referencedAdjournmentMotion;
    
    /** The date of reply requested to department. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date replyRequestedDate;
    
    /** The date of reply received from department. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date replyReceivedDate;
    
    /** The reply received mode (ONLINE/OFFLINE). */
    @Column(name="reply_received_mode", length=50)
    private String replyReceivedMode;
    
    /** The last date of reply receiving from department. */
    @Temporal(TemporalType.DATE)
    private Date lastDateOfReplyReceiving;
    
    /**** Fields for storing the confirmation of Group change ****/
    private Boolean transferToDepartmentAccepted = false;
    
    private Boolean mlsBranchNotifiedOfTransfer = false;
    
    /**** Reason for Late Reply ****/
    @Column(name="reason_for_late_reply",length=30000)
    private String reasonForLateReply;
    
    //=============== To be used in case of bulk submission and workflows ====================//
    private String workflowStarted;

	private String actor;

	private String localizedActorName;

	private String endFlag;

	private String level;

	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;	

	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;

	private boolean bulkSubmitted=false;

	private Long workflowDetailsId;
	
	private static transient volatile Integer CUR_NUM_LOWER_HOUSE = 0;
	
    private static transient volatile Integer CUR_NUM_UPPER_HOUSE = 0;
    
    private static transient volatile Date CUR_ADJOURNING_DATE_LOWER_HOUSE = new Date();
	
    private static transient volatile Date CUR_ADJOURNING_DATE_UPPER_HOUSE = new Date();
    
    //=============== DRAFTS ====================
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="adjournmentmotions_drafts_association", 
    		joinColumns={@JoinColumn(name="adjournmentmotion_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="adjournmentmotion_draft_id", referencedColumnName="id")})
    private List<AdjournmentMotionDraft> drafts;
    
    /** The adjournment motion repository. */
    @Autowired
    private transient AdjournmentMotionRepository adjournmentMotionRepository;
    
    
    
    /**** Constructors ****/
    /**
     * Instantiates a new adjournment motion.
     */
    public AdjournmentMotion() {
    	super();
	}
    
    
    
    /**** Domain methods ****/
    /**
	 * @return the adjournmentMotionRepository
	 */
	private static AdjournmentMotionRepository getAdjournmentMotionRepository() {
		AdjournmentMotionRepository adjournmentMotionRepository = new AdjournmentMotion().adjournmentMotionRepository;
        if (adjournmentMotionRepository == null) {
            throw new IllegalStateException(
            	"AdjournmentMotionRepository has not been injected in AdjournmentMotion Domain");
        }
        return adjournmentMotionRepository;
	}
	
	@Override
	public AdjournmentMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (AdjournmentMotion.class) {
                	Integer number = null;
                	Boolean isAdjourningDateDifferent = false;                	
                	String houseType = this.getHouseType().getType();
                	if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {	
                		if(AdjournmentMotion.getCurrentAdjourningDateLowerHouse()==null) {
                    		isAdjourningDateDifferent = true;
                    	} else if(AdjournmentMotion.getCurrentAdjourningDateLowerHouse().compareTo(this.getAdjourningDate())!=0) {
                    		isAdjourningDateDifferent = true;
                    	}
						if (AdjournmentMotion.getCurrentNumberLowerHouse()==0 || isAdjourningDateDifferent) {
							number = AdjournmentMotion.assignMotionNo(this.getHouseType(), this.getAdjourningDate(), this.getLocale());
							AdjournmentMotion.updateCurrentNumberLowerHouse(number);
							AdjournmentMotion.updateCurrentAdjourningDateLowerHouse(this.getAdjourningDate());
						}
					} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(AdjournmentMotion.getCurrentAdjourningDateUpperHouse()==null) {
                    		isAdjourningDateDifferent = true;
                    	} else if(AdjournmentMotion.getCurrentAdjourningDateUpperHouse().compareTo(this.getAdjourningDate())!=0) {
                    		isAdjourningDateDifferent = true;
                    	}
						if (AdjournmentMotion.getCurrentNumberUpperHouse()==0 || isAdjourningDateDifferent) {
							number = AdjournmentMotion.assignMotionNo(this.getHouseType(), this.getAdjourningDate(), this.getLocale());
							AdjournmentMotion.updateCurrentNumberUpperHouse(number);
							AdjournmentMotion.updateCurrentAdjourningDateUpperHouse(this.getAdjourningDate());
						}
					}
					
            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(AdjournmentMotion.getCurrentNumberLowerHouse() + 1);
            			AdjournmentMotion.updateCurrentNumberLowerHouse(AdjournmentMotion.getCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(AdjournmentMotion.getCurrentNumberUpperHouse() + 1);
            			AdjournmentMotion.updateCurrentNumberUpperHouse(AdjournmentMotion.getCurrentNumberUpperHouse() + 1);
            		}                	
            		addAdjournmentMotionDraft();
                    return (AdjournmentMotion)super.persist();
                }
            }
            else if(this.getNumber()!=null){
            	addAdjournmentMotionDraft();
            }
		}
		return (AdjournmentMotion) super.persist();
	}
	
	@Override
	public AdjournmentMotion merge() {
		AdjournmentMotion adjournmentMotion = null;
		if(this.getStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (this) {
                	Integer number = null;                	
                	Boolean isAdjourningDateDifferent = false;                	
                	String houseType = this.getHouseType().getType();
                	if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {	
                		if(AdjournmentMotion.getCurrentAdjourningDateLowerHouse()==null) {
                    		isAdjourningDateDifferent = true;
                    	} else if(AdjournmentMotion.getCurrentAdjourningDateLowerHouse().compareTo(this.getAdjourningDate())!=0) {
                    		isAdjourningDateDifferent = true;
                    	}
						if (AdjournmentMotion.getCurrentNumberLowerHouse()==0 || isAdjourningDateDifferent) {
							number = AdjournmentMotion.assignMotionNo(this.getHouseType(), this.getAdjourningDate(), this.getLocale());
							AdjournmentMotion.updateCurrentNumberLowerHouse(number);
							AdjournmentMotion.updateCurrentAdjourningDateLowerHouse(this.getAdjourningDate());
						}
					} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(AdjournmentMotion.getCurrentAdjourningDateUpperHouse()==null) {
                    		isAdjourningDateDifferent = true;
                    	} else if(AdjournmentMotion.getCurrentAdjourningDateUpperHouse().compareTo(this.getAdjourningDate())!=0) {
                    		isAdjourningDateDifferent = true;
                    	}
						if (AdjournmentMotion.getCurrentNumberUpperHouse()==0 || isAdjourningDateDifferent) {
							number = AdjournmentMotion.assignMotionNo(this.getHouseType(), this.getAdjourningDate(), this.getLocale());
							AdjournmentMotion.updateCurrentNumberUpperHouse(number);
							AdjournmentMotion.updateCurrentAdjourningDateUpperHouse(this.getAdjourningDate());
						}
					}					
            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(AdjournmentMotion.getCurrentNumberLowerHouse() + 1);
            			AdjournmentMotion.updateCurrentNumberLowerHouse(AdjournmentMotion.getCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(AdjournmentMotion.getCurrentNumberUpperHouse() + 1);
            			AdjournmentMotion.updateCurrentNumberUpperHouse(AdjournmentMotion.getCurrentNumberUpperHouse() + 1);
            		}
            		addAdjournmentMotionDraft();
            		adjournmentMotion = (AdjournmentMotion) super.merge();
                }
            }
            else {
            	AdjournmentMotion oldAdjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldAdjournmentMotion.getClubbedEntities());
            	}
            	if(this.getReferencedAdjournmentMotion() == null){
            		this.setReferencedAdjournmentMotion(oldAdjournmentMotion.getReferencedAdjournmentMotion());
            	}
            	addAdjournmentMotionDraft();
            	adjournmentMotion = (AdjournmentMotion) super.merge();
            }
		} else if(this.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_COMPLETE)){
			//added by dhananjayb to retain drafts in case of motion getting this status as result of updation error in workflow
			AdjournmentMotion oldAdjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, this.getId());
			List<AdjournmentMotionDraft> originalDrafts = oldAdjournmentMotion.getDrafts();
			this.setDrafts(originalDrafts);
			//-----------------------------------------------------------------------------
			adjournmentMotion = (AdjournmentMotion) super.merge();
		}
		if(adjournmentMotion != null) {
			return adjournmentMotion;
		}
		else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_INCOMPLETE) 
	            	|| 
	            	this.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_COMPLETE)){
				//added by dhananjayb to retain drafts in case of motion getting this status as result of updation error in workflow
				AdjournmentMotion oldAdjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, this.getId());
				List<AdjournmentMotionDraft> originalDrafts = oldAdjournmentMotion.getDrafts();
				this.setDrafts(originalDrafts);
				//-----------------------------------------------------------------------------
				return (AdjournmentMotion) super.merge();
			} 
			else {
				AdjournmentMotion oldAdjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldAdjournmentMotion.getClubbedEntities());
            	}
            	if(this.getReferencedAdjournmentMotion() == null){
            		this.setReferencedAdjournmentMotion(oldAdjournmentMotion.getReferencedAdjournmentMotion());
            	}
            	addAdjournmentMotionDraft();
            	return (AdjournmentMotion) super.merge();
			}
		}		
	}
	
	/**
     * Adds the adjournment motion draft.
     */
    private void addAdjournmentMotionDraft() {
    	AdjournmentMotionDraft draft = new AdjournmentMotionDraft();
        draft.setRemarks(this.getRemarks());
        
        draft.setParent(this.getParent());
        draft.setClubbedEntities(this.getClubbedEntities());
        draft.setReferencedAdjournmentMotion(this.getReferencedAdjournmentMotion());
        
        draft.setEditedAs(this.getEditedAs());
        draft.setEditedBy(this.getEditedBy());
        draft.setEditedOn(this.getEditedOn());
        
        draft.setMinistry(this.getMinistry());
        draft.setSubDepartment(this.getSubDepartment());
        
        draft.setStatus(this.getStatus());
        draft.setInternalStatus(this.getInternalStatus());
        draft.setRecommendationStatus(this.getRecommendationStatus());
       
    	if(this.getRevisedSubject()!=null && !this.getRevisedSubject().isEmpty()) {
    		draft.setSubject(this.getRevisedSubject());
    	} else {
    		draft.setSubject(this.getSubject());
    	}
    	
    	if(this.getRevisedNoticeContent()!=null && !this.getRevisedNoticeContent().isEmpty()) {
    		draft.setNoticeContent(this.getRevisedNoticeContent());
    	} else {
    		draft.setNoticeContent(this.getNoticeContent());
    	}
        
        if(this.getId() != null) {
            AdjournmentMotion adjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, this.getId());
            List<AdjournmentMotionDraft> originalDrafts = adjournmentMotion.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<AdjournmentMotionDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<AdjournmentMotionDraft> originalDrafts = new ArrayList<AdjournmentMotionDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
    
    /**
     * The merge function, besides updating  Adjournment Motion, performs various actions
     * based on Adjournment Motion's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the AdjournmentMotion
     */
    public AdjournmentMotion simpleMerge() {
    	AdjournmentMotion adjournmentMotion = (AdjournmentMotion) super.merge();
        return adjournmentMotion;
    }
	
	public static Integer assignMotionNo(final HouseType houseType, final Date adjourningDate, final String locale) {
		return getAdjournmentMotionRepository().assignMotionNo(houseType, adjourningDate, locale);		
	}
	
	public static Integer assignAdmissionNumber(final Session session, final String locale) {
		return getAdjournmentMotionRepository().assignAdmissionNumber(session, locale);
	}
	
	public String formatNumber() {
		if(getNumber()!=null){
			return FormaterUtil.formatNumberNoGrouping(this.getNumber(), this.getLocale());			
		}else{
			return "";
		}
	}

	public static Date findDefaultAdjourningDateForSession(final Session session, final Boolean isForMemberLogin) throws ELSException {
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		if(Session.isCurrentDateInSession(session)) {
			if(!isForMemberLogin || AdjournmentMotion.validateSubmissionEndTime(session, new Date())) {
				return new Date();
			} else {
				Date nextSessionWorkingDay = session.getNextSessionDate(new Date(), 1, session.getLocale());
				if(nextSessionWorkingDay!=null) {
					return nextSessionWorkingDay;
				} else {
					return session.getEndDate();
				}
			}						
		} else {
			return session.getStartDate();
		}
	}

	public static List<AdjournmentMotion> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		return getAdjournmentMotionRepository().findAllReadyForSubmissionByMember(session, primaryMember, motionType, itemsCount, locale);
	}
	
	public static Boolean isDuplicateNumberExist(Date adjourningDate, Integer number, Long id, String locale) {
		return getAdjournmentMotionRepository().isDuplicateNumberExist(adjourningDate, number, id, locale);
	}
	
	public AdjournmentMotionDraft findPreviousDraft() {
		return getAdjournmentMotionRepository().findPreviousDraft(this.getId());
	}
	
	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final AdjournmentMotion adjournmentMotion) {
    	return getAdjournmentMotionRepository().findClubbedEntitiesByPosition(adjournmentMotion);
    }
	
    public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final String sortOrder) {
    	return getAdjournmentMotionRepository().findClubbedEntitiesByMotionNumber(this, sortOrder);
    }
	
	/**
     * Gets the revisions.
     *
     * @param adjournmentMotionId the question id
     * @param locale the locale
     * @return the revisions
     */
    public static List<RevisionHistoryVO> getRevisions(final Long adjournmentMotionId, final String locale) {
        return getAdjournmentMotionRepository().getRevisions(adjournmentMotionId,locale);
    }
    
    public static List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		return getAdjournmentMotionRepository().fullTextSearchForSearching(param,start,noOfRecords, locale, requestMap);
	}
    
    /**** Clubbing ****/
    public static boolean club(final AdjournmentMotion m1,final AdjournmentMotion m2,final String locale) throws ELSException{
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ADJOURNMENT_MOTION_CLUBBING_MODE, "");
    	if(csptClubbingMode!=null && csptClubbingMode.getValue()!=null && csptClubbingMode.getValue().equalsIgnoreCase(ApplicationConstants.CLUBBING_MODE_APPROVAL_WORKFLOW)) {
    		//clubbing with approval workflow
    		return clubWithApprovalWorkflow(m1, m2, locale);
    	} else {
    		//normal clubbing
    		return clubWithoutApprovalWorkflow(m1, m2, locale);
    	}    	
    }
    
    /**** Clubbing with approval workflow ****/
    public static boolean clubWithApprovalWorkflow(final AdjournmentMotion m1,final AdjournmentMotion m2,final String locale) throws ELSException{
    	boolean clubbingStatus = false;
    	try {  
    		if(m1.getParent()!=null || m2.getParent()!=null) {
    			throw new ELSException("error", "ADJOURNMENTMOTION_ALREADY_CLUBBED");    			
    		} else {
    			clubbingStatus = clubbingRulesWithApprovalWorkflow(m1, m2, locale);
    			if(clubbingStatus) {
    				Status putupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
    				Status approvalStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, locale);
    				
    				//Case 1: Both motions are just ready to be put up
    				if(m1.getInternalStatus().equals(putupStatus) && m2.getInternalStatus().equals(putupStatus)) {
    					Status clubbedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED, locale);
    					if(m1.getNumber().compareTo(m2.getNumber())<0) {
    	    				actualClubbingWithApprovalWorkflow(m1, m2, clubbedStatus, clubbedStatus, locale);
    	    				return true;
    	    			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	    				actualClubbingWithApprovalWorkflow(m2, m1, clubbedStatus, clubbedStatus, locale);
    	    				return true;
    	    			} else {
    	    				return false;
    	    			}
    				}
    				//Case 2A: One motion is pending in approval workflow while other is ready to be put up
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    	    				&& m2.getInternalStatus().equals(putupStatus)) {
	    	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING, locale);
	    	    		actualClubbingWithApprovalWorkflow(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    	    		return true;
	    	    	}
    				//Case 2B: One motion is pending in approval workflow while other is ready to be put up
    		    	else if(m1.getInternalStatus().equals(putupStatus)
    	    				&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING, locale);
	    	    		actualClubbingWithApprovalWorkflow(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    	    		return true;
	    	    	}
    				//Case 3: Both motions are pending in approval workflow
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    		    		int m1_approvalLevel = Integer.parseInt(m1_workflowDetails.getAssigneeLevel());
    		    		int m2_approvalLevel = Integer.parseInt(m2_workflowDetails.getAssigneeLevel());
    		    		if(m1_approvalLevel==m2_approvalLevel) {
    		    			if(m1.getNumber().compareTo(m2.getNumber())<0) {        
    	        				WorkflowDetails.endProcess(m2_workflowDetails);
    	        				m2.removeExistingWorkflowAttributes();
    	        				actualClubbingWithApprovalWorkflow(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
    	        				return true;
    	        			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	        				WorkflowDetails.endProcess(m1_workflowDetails);
    	        				m1.removeExistingWorkflowAttributes();
    	        				actualClubbingWithApprovalWorkflow(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    	        				return true;
    	        			} else {
    	        				return false;
    	        			}
    		    		} else if(m1_approvalLevel>m2_approvalLevel) {
    		    			WorkflowDetails.endProcess(m2_workflowDetails);;
    		    			m2.removeExistingWorkflowAttributes();
    		    			actualClubbingWithApprovalWorkflow(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		    			return true;
    		    		} else if(m1_approvalLevel<m2_approvalLevel) {
    		    			WorkflowDetails.endProcess(m1_workflowDetails);
    		    			m1.removeExistingWorkflowAttributes();
    		    			actualClubbingWithApprovalWorkflow(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		    			return true;
    		    		} else {
    		    			return false;
    		    		}
    		    	}
    				//Case 4A: One motion is admitted while other motion is ready to be put up (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(approvalStatus) && m2.getInternalStatus().equals(putupStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		actualClubbingWithApprovalWorkflow(m1, m2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 4B: One motion is admitted while other motion is ready to be put up (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(putupStatus) && m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		actualClubbingWithApprovalWorkflow(m2, m1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 5A: One motion is admitted while other question is pending in approval workflow (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(approvalStatus)
    						&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    						&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    		    		WorkflowDetails.endProcess(m2_workflowDetails);
    		    		m2.removeExistingWorkflowAttributes();
    		    		actualClubbingWithApprovalWorkflow(m1, m2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 5B: One motion is admitted while other question is pending in approval workflow (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    						&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    						&& m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails.endProcess(m1_workflowDetails);
    		    		m1.removeExistingWorkflowAttributes();
    		    		actualClubbingWithApprovalWorkflow(m2, m1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 6: Both motions are admitted
    		    	else if(m1.getInternalStatus().equals(approvalStatus) && m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    		    		if(m1_workflowDetails==null && m2_workflowDetails==null) {
    		    			if(m1.getNumber().compareTo(m2.getNumber())<0) {        
    	        				actualClubbingWithApprovalWorkflow(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    	        				return true;
    	        			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	        				actualClubbingWithApprovalWorkflow(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    	        				return true;
    	        			} else {
    	        				return false;
    	        			}
    		    		} else if(m1_workflowDetails!=null && m2_workflowDetails!=null) {
    		    			int m1_approvalLevel = Integer.parseInt(m1_workflowDetails.getAssigneeLevel());
    		        		int m2_approvalLevel = Integer.parseInt(m2_workflowDetails.getAssigneeLevel());
    		        		if(m1_approvalLevel==m2_approvalLevel) {
    		        			if(m1.getNumber().compareTo(m2.getNumber())<0) {        
    	            				WorkflowDetails.endProcess(m2_workflowDetails);
    	            				m2.removeExistingWorkflowAttributes();
    	            				actualClubbingWithApprovalWorkflow(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    	            				return true;
    	            			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	            				WorkflowDetails.endProcess(m1_workflowDetails);
    	            				m1.removeExistingWorkflowAttributes();
    	            				actualClubbingWithApprovalWorkflow(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    	            				return true;
    	            			} else {
    	            				return false;
    	            			}
    		        		} else if(m1_approvalLevel>m2_approvalLevel) {
    		        			WorkflowDetails.endProcess(m2_workflowDetails);
    		        			m2.removeExistingWorkflowAttributes();
    		        			actualClubbingWithApprovalWorkflow(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		        			return true;
    		        		} else if(m1_approvalLevel<m2_approvalLevel) {
    		        			WorkflowDetails.endProcess(m1_workflowDetails);
    		        			m1.removeExistingWorkflowAttributes();
    		        			actualClubbingWithApprovalWorkflow(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		        			return true;
    		        		} else {
    		        			return false;
    		        		}
    		    		} else if(m1_workflowDetails==null && m2_workflowDetails!=null) {
    		    			WorkflowDetails.endProcess(m2_workflowDetails);
    		    			m2.removeExistingWorkflowAttributes();
    						actualClubbingWithApprovalWorkflow(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    						return true;
    		    		} else if(m1_workflowDetails!=null && m2_workflowDetails==null) {
    		    			WorkflowDetails.endProcess(m1_workflowDetails);
    		    			m1.removeExistingWorkflowAttributes();
    		    			actualClubbingWithApprovalWorkflow(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		    			return true;
    		    		} else {
    		    			return false;
    		    		}
    		    	}
    			}
    		}    		
    	} catch(ELSException ex){
    		throw ex;
		} catch(Exception ex){
    		//logger.error("CLUBBING_FAILED",ex);
			clubbingStatus = false;
			return clubbingStatus;
		}        
        return clubbingStatus;
    }
    
    /**** Clubbing without approval workflow ****/
    public static boolean clubWithoutApprovalWorkflow(final AdjournmentMotion m1,final AdjournmentMotion m2,final String locale) throws ELSException{
    	boolean clubbingStatus = false;
    	try {
    		if(m1.getParent()!=null || m2.getParent()!=null) {
    			throw new ELSException("error", "ADJOURNMENTMOTION_ALREADY_CLUBBED");			
    		} else {
    			clubbingStatus = clubbingRulesWithoutApprovalWorkflow(m1, m2, locale);
    			if(clubbingStatus) {
    				Status approvalStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, locale);
    				//case 1: m1 is lower number and hence parent
    				if(m1.getNumber().compareTo(m2.getNumber()) < 0) {
    					WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(m2);
    					if(wfOfChild != null){
    						WorkflowDetails.endProcess(wfOfChild);
    					}
    					m2.removeExistingWorkflowAttributes();
    					if(m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
							actualClubbingWithoutApprovalWorkflow(m1, m2, m1.getInternalStatus(), m1.getInternalStatus(), m1.getInternalStatus(), locale);						
						} else {
							//TODO: confirm the action below for parent pending while child having final decision
							Status submitStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SUBMIT, locale);
							Status clubbedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED, locale);
    						actualClubbingWithoutApprovalWorkflow(m1, m2, submitStatus, clubbedStatus, clubbedStatus, locale);
						}     					
    					return true;
    					
    				} 
    				//case 2: m1 is higher number and hence child
    				else if(m1.getNumber().compareTo(m2.getNumber()) > 0) {
    					WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(m1);
    					if(wfOfChild != null){
    						WorkflowDetails.endProcess(wfOfChild);
    					}
    					m1.removeExistingWorkflowAttributes();
    					if(m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
							actualClubbingWithoutApprovalWorkflow(m2, m1, m2.getInternalStatus(), m2.getInternalStatus(), m2.getInternalStatus(), locale);    							
						} else {
							//TODO: confirm the action below for parent pending while child having final decision
							Status submitStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SUBMIT, locale);
    						Status clubbedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED, locale);
    						actualClubbingWithoutApprovalWorkflow(m2, m1, submitStatus, clubbedStatus, clubbedStatus, locale);
						}     					
    					return true;
    					
    				} else {
    					return false;
    				}
    			}
    		}
    	} catch(ELSException ex){
    		throw ex;
		} catch(Exception ex){
    		//logger.error("CLUBBING_FAILED",ex);
			clubbingStatus = false;
			return clubbingStatus;
		}        
        return clubbingStatus;
    }
    
    private static boolean clubbingRulesWithApprovalWorkflow(AdjournmentMotion m1, AdjournmentMotion m2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(m1, m2, locale);
    	if(clubbingStatus) {
    		if(m1.getReply()!=null && !m1.getReply().isEmpty()) {
    			WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    			if(m1_workflowDetails!=null && m1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "ADJOURNMENTMOTION_REPLIED_BUT_FLOW_PENDING");
    			}
    		} else if(m2.getReply()!=null && !m2.getReply().isEmpty()) {
    			WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    			if(m2_workflowDetails!=null && m2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "ADJOURNMENTMOTION_REPLIED_BUT_FLOW_PENDING");
    			}
    		}
    	}
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesWithoutApprovalWorkflow(AdjournmentMotion m1, AdjournmentMotion m2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(m1, m2, locale);
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesCommon(AdjournmentMotion m1, AdjournmentMotion m2, String locale) throws ELSException {
    	if(!m1.getSession().equals(m2.getSession())) {
    		//different sessions not allowed
    		throw new ELSException("error", "ADJOURNMENTMOTIONS_FROM_DIFFERENT_SESSIONS");	
    		
    	} else if(!m1.getAdjourningDate().equals(m2.getAdjourningDate())) {
    		//different adjourning dates not allowed
    		throw new ELSException("error", "ADJOURNMENTMOTIONS_FROM_DIFFERENT_ADJOURNING_DATES");
    		
    	} else if(m1.getMinistry()!=null && m2.getMinistry()!=null
    				&& m1.getMinistry().getId()!=null && m2.getMinistry().getId()!=null
    				&& !m1.getMinistry().getName().equals(m2.getMinistry().getName())) {
    		//if set different ministries then not allowed
    		throw new ELSException("error", "ADJOURNMENTMOTIONS_FROM_DIFFERENT_MINISTRY");  
    		
    	} else if(m1.getSubDepartment()!=null && m2.getSubDepartment()!=null
				&& m1.getSubDepartment().getId()!=null && m2.getSubDepartment().getId()!=null
				&& !m1.getSubDepartment().getName().equals(m2.getSubDepartment().getName())) {
    		//if set different subdepartments then not allowed
    		throw new ELSException("error", "ADJOURNMENTMOTIONS_FROM_DIFFERENT_DEPARTMENT");
    		
    	} else {
			//clubbing rules succeeded
    		return true;
		}
    }
    
    @Transactional(noRollbackFor={OptimisticLockException.class})
    private static void actualClubbingWithApprovalWorkflow(AdjournmentMotion parent,AdjournmentMotion child,
			Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent motion are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child motion and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their number ****/
    	List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMotionId = child.getId();
				AdjournmentMotion clubbedMotion = i.getAdjournmentMotion();
				Long clubbedMotionId = clubbedMotion.getId();
				if(! childMotionId.equals(clubbedMotionId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMotionId = parent.getId();
				AdjournmentMotion clubbedMotion = i.getAdjournmentMotion();
				Long clubbedMotionId = clubbedMotion.getId();
				if(! parentMotionId.equals(clubbedMotionId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setAdjournmentMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);
		
		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				AdjournmentMotion adjournmentMotion = k.getAdjournmentMotion();
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForMotion = "";
				if(adjournmentMotion.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_CLUBBING)
						|| adjournmentMotion.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(adjournmentMotion.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_NAMECLUBBING)
						|| adjournmentMotion.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(adjournmentMotion.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| adjournmentMotion.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(adjournmentMotion, pendingWorkflowTypeForMotion);
					WorkflowDetails.endProcess(wfDetails);
					adjournmentMotion.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION , locale);
					if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING , locale);
						adjournmentMotion.setInternalStatus(putupForClubbingStatus);
						adjournmentMotion.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(adjournmentMotion.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_NAMECLUBBING , locale);
							adjournmentMotion.setInternalStatus(putupForNameClubbingStatus);
							adjournmentMotion.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
							adjournmentMotion.setInternalStatus(putupForClubbingPostAdmissionStatus);
							adjournmentMotion.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				adjournmentMotion.setEditedAs(child.getEditedAs());
				adjournmentMotion.setEditedBy(child.getEditedBy());
				adjournmentMotion.setEditedOn(child.getEditedOn());
				adjournmentMotion.setParent(parent);
				adjournmentMotion.merge();
				parentClubbedEntities.add(k);
			}
		}
		boolean isChildBecomingParentCase = false;
		if(parent.getParent()!=null) {
			isChildBecomingParentCase = true;
			parent.setParent(null);
		}		
		parent.setClubbedEntities(parentClubbedEntities);
		if(isChildBecomingParentCase) {		
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);			
			} 
			parent.merge();
		} else {
			parent.simpleMerge();
		}		
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByMotionNumber(ApplicationConstants.ASC);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
    }
    
    @Transactional(noRollbackFor={OptimisticLockException.class})
    private static void actualClubbingWithoutApprovalWorkflow(AdjournmentMotion parent,AdjournmentMotion child,
    		Status newStatus,Status newInternalStatus,Status newRecommendationStatus,String locale) throws ELSException {
    	/**** a.Clubbed entities of parent motion are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child motion and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their number ****/
    	List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMotionId = child.getId();
				AdjournmentMotion clubbedMotion = i.getAdjournmentMotion();
				Long clubbedMotionId = clubbedMotion.getId();
				if(! childMotionId.equals(clubbedMotionId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMotionId = parent.getId();
				AdjournmentMotion clubbedMotion = i.getAdjournmentMotion();
				Long clubbedMotionId = clubbedMotion.getId();
				if(! parentMotionId.equals(clubbedMotionId)) {
					childClubbedEntities.add(i);
				}
			}
		}	

		child.setParent(parent);
		child.setClubbedEntities(null);
		child.setStatus(newStatus);
		child.setInternalStatus(newInternalStatus);
		child.setRecommendationStatus(newRecommendationStatus);
		Status approvalStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, locale);
//		if(newStatus.getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
//			updateDomainFieldsOnClubbingFinalisation(parent, child);
//		}	
		updateDomainFieldsOnClubbingFinalisation(parent, child);
		
//			if(child.getFile()!=null){
//				child.setFile(null);
//				child.setFileIndex(null);
//				child.setFileSent(false);
//			}
		
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setAdjournmentMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);
		
		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				AdjournmentMotion adjournmentMotion = k.getAdjournmentMotion();
				WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(adjournmentMotion);
				if(wd != null){
					WorkflowDetails.endProcess(wd);
					adjournmentMotion.removeExistingWorkflowAttributes();
				}
				adjournmentMotion.setStatus(newStatus);
				adjournmentMotion.setInternalStatus(newInternalStatus);
				adjournmentMotion.setRecommendationStatus(newRecommendationStatus);
//				if(newStatus.getPriority().compareTo(approvalStatus.getPriority()) >= 0) {
//					updateDomainFieldsOnClubbingFinalisation(parent, adjournmentMotion);
//				}
				updateDomainFieldsOnClubbingFinalisation(parent, adjournmentMotion);
				adjournmentMotion.setEditedAs(child.getEditedAs());
				adjournmentMotion.setEditedBy(child.getEditedBy());
				adjournmentMotion.setEditedOn(child.getEditedOn());
				adjournmentMotion.setParent(parent);
				adjournmentMotion.merge();
				parentClubbedEntities.add(k);
			}
		}
		parent.setClubbedEntities(parentClubbedEntities);
		parent.simpleMerge();		
		List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByMotionNumber(ApplicationConstants.ASC);
		Integer position=1;
		for(ClubbedEntity i:clubbedEntities){
			i.setPosition(position);
			position++;
			i.merge();
		}
    }
    
    /**** Update Clubbing ****/
    public static void updateClubbing(AdjournmentMotion adjournmentMotion) throws ELSException {
		//case 1: motion is child
		if(adjournmentMotion.getParent()!=null) {
			AdjournmentMotion.updateClubbingForChild(adjournmentMotion);
		} 
		//case 2: motion is parent
		else if(adjournmentMotion.getParent()==null && adjournmentMotion.getClubbedEntities()!=null && !adjournmentMotion.getClubbedEntities().isEmpty()) {
			AdjournmentMotion.updateClubbingForParent(adjournmentMotion);
		}
	}
    
    private static void updateClubbingForParent(AdjournmentMotion adjournmentMotion) {
    	for(ClubbedEntity ce: adjournmentMotion.getClubbedEntities()) {
    		AdjournmentMotion clubbedMotion = ce.getAdjournmentMotion();
			if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(adjournmentMotion, clubbedMotion);
				
				clubbedMotion.setStatus(adjournmentMotion.getInternalStatus());
				clubbedMotion.setInternalStatus(adjournmentMotion.getInternalStatus());
				clubbedMotion.setRecommendationStatus(adjournmentMotion.getInternalStatus());
				
				clubbedMotion.merge();
			}
		}
    }
    
    private static void updateDomainFieldsOnClubbingFinalisation(AdjournmentMotion parent, AdjournmentMotion child) {
    	/** copy latest subject of parent to revised subject of child **/
		if(parent.getRevisedSubject()!=null && !parent.getRevisedSubject().isEmpty()) {
			child.setRevisedSubject(parent.getRevisedSubject());
		} else {
			child.setRevisedSubject(parent.getSubject());
		}
		/** copy latest notice content of parent to revised notice content of child **/
		if(parent.getRevisedNoticeContent()!=null && !parent.getRevisedNoticeContent().isEmpty()) {
			child.setRevisedNoticeContent(parent.getRevisedNoticeContent());
		} else {
			child.setRevisedNoticeContent(parent.getNoticeContent());
		}
		/** copy latest reply of parent to revised answer of child **/
		child.setReply(parent.getReply());
		/** copy latest rejection reason of parent to revised rejection reason of child **/
		child.setRejectionReason(parent.getRejectionReason());
		/** update ministries in sync **/
		if(parent.getMinistry()!=null && child.getMinistry()==null) {
			child.setMinistry(parent.getMinistry());
		} else if(child.getMinistry()!=null && parent.getMinistry()==null) {
			parent.setMinistry(child.getMinistry());
		}
		/** update sub departments in sync **/
		if(parent.getSubDepartment()!=null && child.getSubDepartment()==null) {
			child.setSubDepartment(parent.getSubDepartment());
		} else if(child.getSubDepartment()!=null && parent.getSubDepartment()==null) {
			parent.setSubDepartment(child.getSubDepartment());
		}
    }
    
    private static void updateClubbingForChild(AdjournmentMotion adjournmentMotion) throws ELSException {
    	String locale = adjournmentMotion.getLocale();
		AdjournmentMotion parentMotion = adjournmentMotion.getParent();
		
		Status putupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, adjournmentMotion.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, adjournmentMotion.getLocale());
	
		if(parentMotion.getNumber().compareTo(adjournmentMotion.getNumber())<0) {
			updateDomainFieldsOnClubbingFinalisation(parentMotion, adjournmentMotion);
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED, adjournmentMotion.getLocale());
				adjournmentMotion.setInternalStatus(clubbedStatus);
				adjournmentMotion.setRecommendationStatus(clubbedStatus);
			} else {
				adjournmentMotion.setStatus(parentMotion.getInternalStatus());
				adjournmentMotion.setInternalStatus(parentMotion.getInternalStatus());
				adjournmentMotion.setRecommendationStatus(parentMotion.getInternalStatus());				
			}				
			
			adjournmentMotion.simpleMerge();
			
		} else if(parentMotion.getNumber().compareTo(adjournmentMotion.getNumber())>0) {				
			
			WorkflowDetails parentMotion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
			if(parentMotion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentMotion_workflowDetails);
				parentMotion.removeExistingWorkflowAttributes();
			}
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				adjournmentMotion.setInternalStatus(putupStatus);
				adjournmentMotion.setRecommendationStatus(putupStatus);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED, adjournmentMotion.getLocale());
				actualClubbingWithApprovalWorkflow(adjournmentMotion, parentMotion, clubbedStatus, clubbedStatus, locale);
			} else {
				adjournmentMotion.setStatus(parentMotion.getInternalStatus());
				adjournmentMotion.setInternalStatus(parentMotion.getInternalStatus());
				if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, adjournmentMotion.getLocale());
					adjournmentMotion.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(adjournmentMotion, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					adjournmentMotion.setRecommendationStatus(parentMotion.getInternalStatus());					
				}					
				if(parentMotion.getReply()!=null && (adjournmentMotion.getReply()==null || adjournmentMotion.getReply().isEmpty())) {
					adjournmentMotion.setReply(parentMotion.getReply());
				}
				if(parentMotion.getRejectionReason()!=null && (adjournmentMotion.getRejectionReason()==null || adjournmentMotion.getRejectionReason().isEmpty())) {
					adjournmentMotion.setRejectionReason(parentMotion.getRejectionReason());
				}
				updateDomainFieldsOnClubbingFinalisation(adjournmentMotion, parentMotion);
				actualClubbingWithApprovalWorkflow(adjournmentMotion, parentMotion, parentMotion.getInternalStatus(), parentMotion.getInternalStatus(), locale);
			}
		}
    }
    
    /**** Unclubbing ****/
    public static boolean unclub(final AdjournmentMotion m1, final AdjournmentMotion m2, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(m1.getParent()==null && m2.getParent()==null) {
			throw new ELSException("error", "CLUBBED_MOTION_NOT_FOUND");
		}
		if(m2.getParent()!=null && m2.getParent().equals(m1)) {
			clubbingStatus = actualUnclubbing(m1, m2, locale);
		} else if(m1.getParent()!=null && m1.getParent().equals(m2)) {
			clubbingStatus = actualUnclubbing(m2, m1, locale);
		} else {
			throw new ELSException("error", "NO_CLUBBING_BETWEEN_GIVEN_MOTIONS");
		}
		return clubbingStatus;
	}
    
    public static boolean unclub(final AdjournmentMotion adjournmentMotion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(adjournmentMotion.getParent()==null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(adjournmentMotion.getParent(), adjournmentMotion, locale);
		return clubbingStatus;
	}
    
    public static boolean actualUnclubbing(final AdjournmentMotion parent, final AdjournmentMotion child, String locale) throws ELSException {
    	/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PUTUP_UNCLUBBING, locale);
			child.setRecommendationStatus(putupUnclubStatus);
			child.merge();
			return true;
		} else {
			/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedMotions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedMotions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			for(ClubbedEntity i:oldClubbedMotions){
				if(! i.getAdjournmentMotion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedMotions.add(i);
					}else{
						newClubbedMotions.add(i);                		
					}
				}else{
					found=true;
					position=i.getPosition();
					// clubbedEntityToRemove=i;
				}
			}
			if(!newClubbedMotions.isEmpty()){
				parent.setClubbedEntities(newClubbedMotions);
			}else{
				parent.setClubbedEntities(null);
			}            
			parent.simpleMerge();
			/**break child's clubbing **/
			child.setParent(null);
			/** find & end current clubbing workflow of child if pending **/
			String pendingWorkflowTypeForMotion = "";
			if(child.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status putupStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION, locale);
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(putupStatus);
				child.setRecommendationStatus(putupStatus);
			} else {
				if(child.getReply()==null || child.getReply().isEmpty()
						|| child.getReply().equals(parent.getReply())) {
					child.setInternalStatus(admitStatus);
					child.setRecommendationStatus(admitStatus);
					if(child.getReply().equals(parent.getReply())) {
						child.setReply(null);
					}
					Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
					UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
					WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
				} else {
					child.setInternalStatus(admitStatus);
					Status answerReceivedStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_PROCESSED_REPLY_RECEIVED, locale);
					child.setRecommendationStatus(answerReceivedStatus);
				}
			}
		}	
		if(isOptimisticLockExceptionPossible) {
			Long child_currentVersion = child.getVersion();
			child_currentVersion++;
			child.setVersion(child_currentVersion);
		}		
		child.merge();
		return true;
    }

    public Workflow findWorkflowFromStatus() throws ELSException {
    	Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
			workflow = Workflow.findByStatus(recommendationStatus, this.getLocale());
		
		} else {
			workflow = Workflow.findByStatus(internalStatus, this.getLocale());											
		}
		
		return workflow;
    }
    
    public void removeExistingWorkflowAttributes() {
		// Update motion so as to remove existing workflow
		// based attributes
		this.setEndFlag(null);
		this.setLevel("1");
		this.setTaskReceivedOn(null);
		this.setWorkflowDetailsId(null);
		this.setWorkflowStarted("NO");
		this.setWorkflowStartedOn(null);
		this.setActor(null);
		this.setLocalizedActorName("");	
		this.simpleMerge();
	}
    
    public Boolean validateSubmissionDate() {
    	if(DateUtil.compareDatePartOnly(this.getAdjourningDate(), new Date())==0
    			&& !(Holiday.isHolidayOnDate(this.getAdjourningDate(), this.getLocale()))) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public static Boolean validateSubmissionTime(final Session motionSession, Date adjourningDate) {
    	CustomParameter csptSubmissionStartTimeValidationRequired = CustomParameter.findByName(CustomParameter.class, "AMOIS_SUBMISSION_START_TIME_VALIDATION_REQUIRED", "");
    	if(csptSubmissionStartTimeValidationRequired!=null && csptSubmissionStartTimeValidationRequired.getValue().equals("YES")) {
    		Date currentSubmissionTime = new Date();    	
        	Date submissionStartTime = AdjournmentMotion.findSubmissionStartTime(motionSession, adjourningDate);
        	Date submissionEndTime = AdjournmentMotion.findSubmissionEndTime(motionSession, adjourningDate);    	
        	if(currentSubmissionTime.compareTo(submissionStartTime)>=0 && currentSubmissionTime.compareTo(submissionEndTime)<=0) {
        		return true;
        	} else {
        		return false;
        	}
    	} else {
    		return AdjournmentMotion.validateSubmissionEndTime(motionSession, adjourningDate);
    	}    	    	
    }
    
    public static Boolean validateSubmissionEndTime(final Session motionSession, Date adjourningDate) {
    	Date currentSubmissionTime = new Date();    	
    	Date submissionEndTime = AdjournmentMotion.findSubmissionEndTime(motionSession, adjourningDate);    	
    	if(currentSubmissionTime.compareTo(submissionEndTime)<=0) {
    		return true;
    	} else {
    		return false;
    	}    	
    }
    
    public static Date findSubmissionStartTime(final Session motionSession, Date adjourningDate) {
    	//find submission start date part
    	String strAdjourningDate = FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.SERVER_DATEFORMAT);
    	String submissionStartDatePart = strAdjourningDate;
    	//find submission start time part
    	String submissionStartTimePart = "00:00:00";
    	if(motionSession!=null) {
    		String submissionStartTimeParameter = motionSession.getParameter(ApplicationConstants.ADJOURNMENT_MOTION+"_submissionStartTime_"+strAdjourningDate);
    		if(submissionStartTimeParameter!=null && !submissionStartTimeParameter.isEmpty()) {
    			submissionStartTimePart = submissionStartTimeParameter + ":00";
    		} else {
    			String submissionStartTimeDefaultSessionParameter = motionSession.getParameter(ApplicationConstants.ADJOURNMENT_MOTION+"_submissionStartTime");
        		if(submissionStartTimeDefaultSessionParameter!=null && !submissionStartTimeDefaultSessionParameter.isEmpty()) {
        			submissionStartTimePart = submissionStartTimeDefaultSessionParameter + ":00";
        		} else {
        			CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ADJOURNMENT_MOTION.toUpperCase()+"_SUBMISSIONSTARTTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
            		if(csptsubmissionStartTime!=null && csptsubmissionStartTime.getValue()!=null && !csptsubmissionStartTime.getValue().isEmpty()) {
            			submissionStartTimePart = csptsubmissionStartTime.getValue() + ":00";
            		}
        		}
    		}
    	} else {
    		CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ADJOURNMENT_MOTION.toUpperCase()+"_SUBMISSIONSTARTTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
    		if(csptsubmissionStartTime!=null && csptsubmissionStartTime.getValue()!=null && !csptsubmissionStartTime.getValue().isEmpty()) {
    			submissionStartTimePart = csptsubmissionStartTime.getValue() + ":00";
    		}
    	}
    	//find submission start time
    	String submissionStartTime = submissionStartDatePart + " " + submissionStartTimePart;
    	return FormaterUtil.formatStringToDate(submissionStartTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
    }
    
    public static Date findSubmissionEndTime(final Session motionSession, Date adjourningDate) {
    	//find submission end date part
    	String strAdjourningDate = FormaterUtil.formatDateToString(adjourningDate, ApplicationConstants.SERVER_DATEFORMAT);
    	String submissionEndDatePart = strAdjourningDate;
    	//find submission end time part
    	String submissionEndTimePart = "00:00:00";
    	if(motionSession!=null) {
    		String submissionEndTimeParameter = motionSession.getParameter(ApplicationConstants.ADJOURNMENT_MOTION+"_submissionEndTime_"+strAdjourningDate);
    		if(submissionEndTimeParameter!=null && !submissionEndTimeParameter.isEmpty()) {
    			submissionEndTimePart = submissionEndTimeParameter + ":00";
    		} else {
    			String submissionEndTimeDefaultSessionParameter = motionSession.getParameter(ApplicationConstants.ADJOURNMENT_MOTION+"_submissionEndTime");
        		if(submissionEndTimeDefaultSessionParameter!=null && !submissionEndTimeDefaultSessionParameter.isEmpty()) {
        			submissionEndTimePart = submissionEndTimeDefaultSessionParameter + ":00";
        		} else {
        			CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ADJOURNMENT_MOTION.toUpperCase()+"_SUBMISSIONENDTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
            		if(csptsubmissionEndTime!=null && csptsubmissionEndTime.getValue()!=null && !csptsubmissionEndTime.getValue().isEmpty()) {
            			submissionEndTimePart = csptsubmissionEndTime.getValue() + ":00";
            		}
        		}
    		}
    	} else {
    		CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.ADJOURNMENT_MOTION.toUpperCase()+"_SUBMISSIONENDTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
    		if(csptsubmissionEndTime!=null && csptsubmissionEndTime.getValue()!=null && !csptsubmissionEndTime.getValue().isEmpty()) {
    			submissionEndTimePart = csptsubmissionEndTime.getValue() + ":00";
    		}
    	}
    	//find submission end time
    	String submissionEndTime = submissionEndDatePart + " " + submissionEndTimePart;
    	return FormaterUtil.formatStringToDate(submissionEndTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
    }
    
    public String findAllMemberNames(String nameFormat) {
		StringBuffer allMemberNamesBuffer = new StringBuffer("");
		Member member = null;
		String memberName = "";				
		/** primary member **/
		member = this.getPrimaryMember();		
		if(member==null) {
			return allMemberNamesBuffer.toString();
		}		
		memberName = member.findNameInGivenFormat(nameFormat);
		if(memberName!=null && !memberName.isEmpty()) {
			if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
				allMemberNamesBuffer.append(memberName);
			}						
		} else {
			return allMemberNamesBuffer.toString();
		}						
		/** supporting members **/
		List<SupportingMember> supportingMembers = this.getSupportingMembers();
		if (supportingMembers != null) {
			for (SupportingMember sm : supportingMembers) {
				member = sm.getMember();
				Status approvalStatus = sm.getDecisionStatus();
				if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
					memberName = member.findNameInGivenFormat(nameFormat);
					if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {				
						if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
							if(allMemberNamesBuffer.length()>0) {
								allMemberNamesBuffer.append(", " + memberName);
							} else {
								allMemberNamesBuffer.append(memberName);
							}
						}																		
					}									
				}				
			}
		}		
		/** clubbed questions members **/
		List<ClubbedEntity> clubbedEntities = AdjournmentMotion.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getAdjournmentMotion().getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_SYSTEM_CLUBBED)
						|| ce.getAdjournmentMotion().getInternalStatus().getType().equals(ApplicationConstants.ADJOURNMENTMOTION_FINAL_ADMISSION)) {
					member = ce.getAdjournmentMotion().getPrimaryMember();
					if(member!=null) {
						memberName = member.findNameInGivenFormat(nameFormat);
						if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
							if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
								if(allMemberNamesBuffer.length()>0) {
									allMemberNamesBuffer.append(", " + memberName);
								} else {
									allMemberNamesBuffer.append(memberName);
								}
							}							
						}												
					}
					List<SupportingMember> clubbedSupportingMembers = ce.getAdjournmentMotion().getSupportingMembers();
					if (clubbedSupportingMembers != null) {
						for (SupportingMember csm : clubbedSupportingMembers) {
							member = csm.getMember();
							Status approvalStatus = csm.getDecisionStatus();
							if(member!=null && approvalStatus!=null && approvalStatus.getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)) {
								memberName = member.findNameInGivenFormat(nameFormat);
								if(memberName!=null && !memberName.isEmpty() && !allMemberNamesBuffer.toString().contains(memberName)) {
									if(member.isSupportingOrClubbedMemberToBeAddedForDevice(this)) {
										if(allMemberNamesBuffer.length()>0) {
											allMemberNamesBuffer.append(", " + memberName);
										} else {
											allMemberNamesBuffer.append(memberName);
										}
									}									
								}								
							}
						}
					}
				}
			}
		}		
		return allMemberNamesBuffer.toString();
	}
    
    public Status findMemberStatus() {	
		Status memberStatus = null;
		if(this.getStatus()!=null) {
			Status submitStatus = Status.findByType(ApplicationConstants.ADJOURNMENTMOTION_SUBMIT, this.getLocale());
			if(this.getStatus().getPriority()>=submitStatus.getPriority()) {
				memberStatus = submitStatus;
			} else {
				memberStatus = this.getStatus();
			}
		}		
		return memberStatus;
	}
    
    public void startWorkflow(final AdjournmentMotion adjournmentMotion, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		adjournmentMotion.endWorkflow(adjournmentMotion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
		adjournmentMotion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(adjournmentMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
	
	public void endWorkflow(final AdjournmentMotion adjournmentMotion, final String workflowHouseType, final String locale) throws ELSException {
    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(adjournmentMotion);
		if(wfDetails != null && wfDetails.getId() != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch(Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				adjournmentMotion.removeExistingWorkflowAttributes();
			}
		} else {
			adjournmentMotion.removeExistingWorkflowAttributes();
		}
	}
    
    public void updateForInitFlow(final Status status, final UserGroupType userGroupType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) {
    	/** update statuses for the required flow **/
    	Map<String, String[]> parameterMap = new HashMap<String, String[]>();
    	parameterMap.put("locale", new String[]{locale});
    	parameterMap.put("flowStatusType", new String[]{status.getType()});
    	parameterMap.put("isAfterFinalDecision", new String[]{isFlowOnRecomStatusAfterFinalDecision.toString()});
    	parameterMap.put("userGroupType", new String[]{userGroupType.getType()});
    	List statusRecommendations = Query.findReport(ApplicationConstants.QUERYNAME_STATUS_RECOMMENDATIONS_FOR_INIT_FLOW, parameterMap);
    	if(statusRecommendations!=null && !statusRecommendations.isEmpty()) {
    		Object[] statuses = (Object[]) statusRecommendations.get(0);
    		if(statuses[0]!=null && !statuses[0].toString().isEmpty()) {
    			Status mainStatus = Status.findByType(statuses[0].toString(), locale);
    			this.setStatus(mainStatus);
    		}
    		if(statuses[1]!=null && !statuses[1].toString().isEmpty()) {
    			Status internalStatus = Status.findByType(statuses[1].toString(), locale);
    			this.setInternalStatus(internalStatus);
    		}
    		if(statuses[2]!=null && !statuses[2].toString().isEmpty()) {
    			Status recommendationStatus = Status.findByType(statuses[2].toString(), locale);
    			this.setRecommendationStatus(recommendationStatus);
    		}   
    		this.simpleMerge();
    	}		
    }

	public static Integer findContinuationNumber(final AdjournmentMotion adjournmentMotion) {
		return getAdjournmentMotionRepository().findContinuationNumber(adjournmentMotion);
	}
    
	/**** Getters and Setters ****/
	/**
	 * @return the houseType
	 */
	public HouseType getHouseType() {
		return houseType;
	}

	/**
	 * @param houseType the houseType to set
	 */
	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return the type
	 */
	public DeviceType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(DeviceType type) {
		this.type = type;
	}

	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getAdmissionNumber() {
		return admissionNumber;
	}

	public void setAdmissionNumber(Integer admissionNumber) {
		this.admissionNumber = admissionNumber;
	}

	/**
	 * @return the primaryMember
	 */
	public Member getPrimaryMember() {
		return primaryMember;
	}

	/**
	 * @param primaryMember the primaryMember to set
	 */
	public void setPrimaryMember(Member primaryMember) {
		this.primaryMember = primaryMember;
	}

	/**
	 * @return the supportingMembers
	 */
	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	/**
	 * @param supportingMembers the supportingMembers to set
	 */
	public void setSupportingMembers(List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	/**
	 * @return the submissionDate
	 */
	public Date getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * @param submissionDate the submissionDate to set
	 */
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the dataEnteredBy
	 */
	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	/**
	 * @param dataEnteredBy the dataEnteredBy to set
	 */
	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}

	/**
	 * @return the adjourningDate
	 */
	public Date getAdjourningDate() {
		return adjourningDate;
	}
	
	/**
	 * @param adjourningDate the adjourningDate to set
	 */
	public void setAdjourningDate(Date adjourningDate) {
		this.adjourningDate = adjourningDate;
	}

	/**
	 * @return the formatted adjourningDate
	 */
	public String getFormattedAdjourningDate() {
		if(this.adjourningDate!=null) {
			try {
				formattedAdjourningDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(this.adjourningDate, "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", this.getLocale());
			} catch (ELSException e) {
				formattedAdjourningDate = "";
			}
		} else {
			formattedAdjourningDate = "";
		}
		return formattedAdjourningDate;
	}
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the revisedSubject
	 */
	public String getRevisedSubject() {
		return revisedSubject;
	}

	/**
	 * @param revisedSubject the revisedSubject to set
	 */
	public void setRevisedSubject(String revisedSubject) {
		this.revisedSubject = revisedSubject;
	}

	/**
	 * @return the noticeContent
	 */
	public String getNoticeContent() {
		return noticeContent;
	}

	/**
	 * @param noticeContent the noticeContent to set
	 */
	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	/**
	 * @return the revisedNoticeContent
	 */
	public String getRevisedNoticeContent() {
		return revisedNoticeContent;
	}

	/**
	 * @param revisedNoticeContent the revisedNoticeContent to set
	 */
	public void setRevisedNoticeContent(String revisedNoticeContent) {
		this.revisedNoticeContent = revisedNoticeContent;
	}

	/**
	 * @return the ministry
	 */
	public Ministry getMinistry() {
		return ministry;
	}

	/**
	 * @param ministry the ministry to set
	 */
	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	/**
	 * @return the subDepartment
	 */
	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	/**
	 * @param subDepartment the subDepartment to set
	 */
	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	/**
	 * @return the editedOn
	 */
	public Date getEditedOn() {
		return editedOn;
	}

	/**
	 * @param editedOn the editedOn to set
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * @return the editedBy
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * @param editedBy the editedBy to set
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * @return the editedAs
	 */
	public String getEditedAs() {
		return editedAs;
	}

	/**
	 * @param editedAs the editedAs to set
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the internalStatus
	 */
	public Status getInternalStatus() {
		return internalStatus;
	}

	/**
	 * @param internalStatus the internalStatus to set
	 */
	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	/**
	 * @return the recommendationStatus
	 */
	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	/**
	 * @param recommendationStatus the recommendationStatus to set
	 */
	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	/**
	 * @return the remarks
	 */
	public String getRemarksAboutDecision() {
		return remarksAboutDecision;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarksAboutDecision(String remarksAboutDecision) {
		this.remarksAboutDecision = remarksAboutDecision;
	}

	/**
	 * @return the reply
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * @param reply the reply to set
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}
	
	public Date getReplyRequestedDate() {
		return replyRequestedDate;
	}

	public void setReplyRequestedDate(Date replyRequestedDate) {
		this.replyRequestedDate = replyRequestedDate;
	}

	public Date getReplyReceivedDate() {
		return replyReceivedDate;
	}

	public void setReplyReceivedDate(Date replyReceivedDate) {
		this.replyReceivedDate = replyReceivedDate;
	}

	public String getReplyReceivedMode() {
		return replyReceivedMode;
	}

	public void setReplyReceivedMode(String replyReceivedMode) {
		this.replyReceivedMode = replyReceivedMode;
	}

	public Date getLastDateOfReplyReceiving() {
		return lastDateOfReplyReceiving;
	}

	public void setLastDateOfReplyReceiving(Date lastDateOfReplyReceiving) {
		this.lastDateOfReplyReceiving = lastDateOfReplyReceiving;
	}
	
	public Boolean getTransferToDepartmentAccepted() {
		return transferToDepartmentAccepted;
	}

	public void setTransferToDepartmentAccepted(Boolean transferToDepartmentAccepted) {
		this.transferToDepartmentAccepted = transferToDepartmentAccepted;
	}

	public Boolean getMlsBranchNotifiedOfTransfer() {
		return mlsBranchNotifiedOfTransfer;
	}

	public void setMlsBranchNotifiedOfTransfer(Boolean mlsBranchNotifiedOfTransfer) {
		this.mlsBranchNotifiedOfTransfer = mlsBranchNotifiedOfTransfer;
	}

	public String getReasonForLateReply() {
		return reasonForLateReply;
	}

	public void setReasonForLateReply(String reasonForLateReply) {
		this.reasonForLateReply = reasonForLateReply;
	}

	/**
	 * @return the rejectionReason
	 */
	public String getRejectionReason() {
		return rejectionReason;
	}

	/**
	 * @param rejectionReason the rejectionReason to set
	 */
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	/**
	 * @return the parent
	 */
	public AdjournmentMotion getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AdjournmentMotion parent) {
		this.parent = parent;
	}

	/**
	 * @return the clubbedEntities
	 */
	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	/**
	 * @param clubbedEntities the clubbedEntities to set
	 */
	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	/**
	 * @return the referencedAdjournmentMotion
	 */
	public ReferencedEntity getReferencedAdjournmentMotion() {
		return referencedAdjournmentMotion;
	}

	/**
	 * @param referencedAdjournmentMotion the referencedAdjournmentMotion to set
	 */
	public void setReferencedAdjournmentMotion(ReferencedEntity referencedAdjournmentMotion) {
		this.referencedAdjournmentMotion = referencedAdjournmentMotion;
	}

	/**
	 * @return the workflowStarted
	 */
	public String getWorkflowStarted() {
		return workflowStarted;
	}

	/**
	 * @param workflowStarted the workflowStarted to set
	 */
	public void setWorkflowStarted(String workflowStarted) {
		this.workflowStarted = workflowStarted;
	}

	/**
	 * @return the actor
	 */
	public String getActor() {
		return actor;
	}

	/**
	 * @param actor the actor to set
	 */
	public void setActor(String actor) {
		this.actor = actor;
	}

	/**
	 * @return the localizedActorName
	 */
	public String getLocalizedActorName() {
		return localizedActorName;
	}

	/**
	 * @param localizedActorName the localizedActorName to set
	 */
	public void setLocalizedActorName(String localizedActorName) {
		this.localizedActorName = localizedActorName;
	}

	/**
	 * @return the endFlag
	 */
	public String getEndFlag() {
		return endFlag;
	}

	/**
	 * @param endFlag the endFlag to set
	 */
	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the workflowStartedOn
	 */
	public Date getWorkflowStartedOn() {
		return workflowStartedOn;
	}

	/**
	 * @param workflowStartedOn the workflowStartedOn to set
	 */
	public void setWorkflowStartedOn(Date workflowStartedOn) {
		this.workflowStartedOn = workflowStartedOn;
	}
	
	/**
	 * @return the taskReceivedOn
	 */
	public Date getTaskReceivedOn() {
		return taskReceivedOn;
	}

	/**
	 * @param taskReceivedOn the taskReceivedOn to set
	 */
	public void setTaskReceivedOn(Date taskReceivedOn) {
		this.taskReceivedOn = taskReceivedOn;
	}

	/**
	 * @return the bulkSubmitted
	 */
	public boolean isBulkSubmitted() {
		return bulkSubmitted;
	}

	/**
	 * @param bulkSubmitted the bulkSubmitted to set
	 */
	public void setBulkSubmitted(boolean bulkSubmitted) {
		this.bulkSubmitted = bulkSubmitted;
	}

	/**
	 * @return the workflowDetailsId
	 */
	public Long getWorkflowDetailsId() {
		return workflowDetailsId;
	}

	/**
	 * @param workflowDetailsId the workflowDetailsId to set
	 */
	public void setWorkflowDetailsId(Long workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}

	/**
	 * @return the drafts
	 */
	public List<AdjournmentMotionDraft> getDrafts() {
		return drafts;
	}

	/**
	 * @param drafts the drafts to set
	 */
	public void setDrafts(List<AdjournmentMotionDraft> drafts) {
		this.drafts = drafts;
	}
	
	/****number atomic value ****/
	public static void updateCurrentNumberLowerHouse(Integer num){
		synchronized (AdjournmentMotion.CUR_NUM_LOWER_HOUSE) {
			AdjournmentMotion.CUR_NUM_LOWER_HOUSE = num;
		}
	}

	public static synchronized Integer getCurrentNumberLowerHouse(){
		return AdjournmentMotion.CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateCurrentNumberUpperHouse(Integer num){
		synchronized (AdjournmentMotion.CUR_NUM_UPPER_HOUSE) {
			AdjournmentMotion.CUR_NUM_UPPER_HOUSE = num;
		}
	}

	public static synchronized Integer getCurrentNumberUpperHouse(){
		return AdjournmentMotion.CUR_NUM_UPPER_HOUSE;
	}
	
	public static void updateCurrentAdjourningDateLowerHouse(Date adjourningDate){
		synchronized (AdjournmentMotion.CUR_ADJOURNING_DATE_LOWER_HOUSE) {
			AdjournmentMotion.CUR_ADJOURNING_DATE_LOWER_HOUSE = adjourningDate;
		}
	}

	public static synchronized Date getCurrentAdjourningDateLowerHouse(){
		return AdjournmentMotion.CUR_ADJOURNING_DATE_LOWER_HOUSE;
	}
	
	public static void updateCurrentAdjourningDateUpperHouse(Date adjourningDate){
		synchronized (AdjournmentMotion.CUR_ADJOURNING_DATE_UPPER_HOUSE) {
			AdjournmentMotion.CUR_ADJOURNING_DATE_UPPER_HOUSE = adjourningDate;
		}
	}

	public static synchronized Date getCurrentAdjourningDateUpperHouse(){
		return AdjournmentMotion.CUR_ADJOURNING_DATE_UPPER_HOUSE;
	}
	
	public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
    		
			ref.setName(ApplicationConstants.ADJOURNMENT_MOTION);
			ref.setNumber(AdjournmentMotion.getCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
    		
    		ref.setName(ApplicationConstants.ADJOURNMENT_MOTION);
			ref.setNumber(AdjournmentMotion.getCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(final Integer num, final String houseType, final String device){
    	
    	if(device.equals(ApplicationConstants.ADJOURNMENT_MOTION)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			AdjournmentMotion.updateCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			AdjournmentMotion.updateCurrentNumberUpperHouse(num);
    		}
	    	
	    	
    	}
    }

}
