package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
import org.mkcl.els.repository.ProprietyPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="propriety_points")
@JsonIgnoreProperties({"houseType", "session", "deviceType", "supportingMembers", "ballotStatus", "discussionStatus", "parent", "clubbedEntities", "drafts"})
public class ProprietyPoint extends Device implements Serializable {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
	
    /********************************************* Attributes *******************************************/
    /** The house type. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="housetype_id")
    private HouseType houseType;

    /** The session. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="session_id")
    private Session session;
    
    /** deviceType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;
	
	/** The number. */
    private Integer number;
	
	/** The number in sequence of admitted count for the session. */
	private Integer admissionNumber;
    
    /** The submission date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

    /** The creation date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /** The created by. */
    @Column(length=1000)
    private String createdBy;
    
    /**** PRIMARY & SUPPORTING MEMBERS ****/
    /** The primary member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member primaryMember;

    /** The supporting members. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="proprietypoints_supportingmembers",
            joinColumns={@JoinColumn(name="proprietypoint_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
    private List<SupportingMember> supportingMembers;
	
    /** The propriety point date
     *  Numbering & Processing of devices will be based on this date.
     */
    @Temporal(TemporalType.DATE)
	@Column(name="propriety_point_date")
	private Date proprietyPointDate;
	
	/** The formatted propriety point date. */
	@Transient
	private String formattedProprietyPointDate;
    
    /** The subject. */
    @Column(length=30000)
    private String subject;

    /** The subject. */
    @Column(length=30000)
    private String revisedSubject;
    
    /** The points of propriety. */
    @Column(length=30000)
    private String pointsOfPropriety;

    /** The question text. */
    @Column(length=30000)
    private String revisedPointsOfPropriety;
    
    @Temporal(TemporalType.DATE)
    private Date discussionDate;
    
    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;
   
    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;
    
    /** The reply. */
    @Column(length=30000)
    private String reply;
    
    /** The document tag having reply file reference. */
    @Column(length=100)
    private String replyDoc;
    
    /** The rejection reason. */
    @Column(length=30000)
    private String rejectionReason;
	
	/** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private ProprietyPoint parent;
	
	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="proprietypoints_clubbingentities",
	joinColumns={@JoinColumn(name="proprietypoint_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;
    
    /** 
     * The status. 
     * Refers to various final status viz, SUBMITTED, ADMITTED, REJECTED 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="status_id")
    private Status status;

    /** 
     * The internal status. 
     * Refers to status assigned to a Propriety Point during the Workflow
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="internalstatus_id")
    private Status internalStatus;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recommendationstatus_id")
    private Status recommendationStatus;
    
    /** 
     * If a propriety point is balloted then its balloted status is set to balloted 
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ballotstatus_id")
    private Status ballotStatus;

	/**** The Disucssion Status. ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="discussionstatus_id")
	private Status discussionStatus;
    
    /** The remarks. */
    @Column(length=30000)
    private String remarks;
	
	/** ** Decision Remarks ***. */
	@Column(length=30000)
	private String remarksAboutDecision;
    
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
    
    /**** DRAFTS ****/
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="proprietypoints_drafts_association", 
    		joinColumns={@JoinColumn(name="proprietypoint_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="proprietypoint_draft_id", referencedColumnName="id")})
    private Set<ProprietyPointDraft> drafts;
    
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
    
    private static transient volatile Date CUR_PROPRIETYPOINT_DATE_LOWER_HOUSE = new Date();
	
    private static transient volatile Date CUR_PROPRIETYPOINT_DATE_UPPER_HOUSE = new Date();
    //=========================================================================================//
    
    /** The propriety point repository. */
    @Autowired
    private transient ProprietyPointRepository proprietyPointRepository;
    
    /********************************************* Constructors *******************************************/	
    /**
     * Instantiates a new propriety point.
     */
    public ProprietyPoint() {
        super();
    }
    
    /********************************************* Domain methods *******************************************/
    /**
	 * @return the proprietyPointRepository
	 */
	private static ProprietyPointRepository getProprietyPointRepository() {
		ProprietyPointRepository proprietyPointRepository = new ProprietyPoint().proprietyPointRepository;
        if (proprietyPointRepository == null) {
            throw new IllegalStateException(
            	"ProprietyPointRepository has not been injected in ProprietyPoint Domain");
        }
        return proprietyPointRepository;
	}
	
	@Override
	public ProprietyPoint persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)) {
			if(this.getNumber() == null) {
//				synchronized (this) {
//					Integer number = ProprietyPoint.assignProprietyPointNo(this.getHouseType(),
//							this.getSession(), this.getDeviceType(),this.getLocale());
//					this.setNumber(number + 1);
//					addProprietyPointDraft();
//					return (ProprietyPoint)super.persist();
//				}
				synchronized (ProprietyPoint.class) {                	
                	Integer number = null;
                	Boolean isProprietyPointDateDifferent = false;
					try {
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
//							if (ProprietyPoint.getCurrentNumberLowerHouse() == 0) {
//								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
//								ProprietyPoint.updateCurrentNumberLowerHouse(number);
//							}
							if(ProprietyPoint.getCurrentProprietyPointDateLowerHouse()==null) {
	                    		isProprietyPointDateDifferent = true;
	                    	} else if(ProprietyPoint.getCurrentProprietyPointDateLowerHouse().compareTo(this.getProprietyPointDate())!=0) {
	                    		isProprietyPointDateDifferent = true;
	                    	}
							if (ProprietyPoint.getCurrentNumberLowerHouse()==0 || isProprietyPointDateDifferent) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getProprietyPointDate(), this.getLocale());
								ProprietyPoint.updateCurrentNumberLowerHouse(number);
								ProprietyPoint.updateCurrentProprietyPointDateLowerHouse(this.getProprietyPointDate());
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
							if(ProprietyPoint.getCurrentProprietyPointDateUpperHouse()==null) {
	                    		isProprietyPointDateDifferent = true;
	                    	} else if(DateUtil.compareDatePartOnly(new Date(), this.getSession().getEndDate())==0) {
	                    		isProprietyPointDateDifferent = true;
	                    	} else if(ProprietyPoint.getCurrentProprietyPointDateUpperHouse().compareTo(this.getProprietyPointDate())!=0) {
	                    		isProprietyPointDateDifferent = true;
	                    	}
							if (ProprietyPoint.getCurrentNumberUpperHouse()==0 || isProprietyPointDateDifferent) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getProprietyPointDate(), this.getLocale());
								ProprietyPoint.updateCurrentNumberUpperHouse(number);
								ProprietyPoint.updateCurrentProprietyPointDateUpperHouse(this.getProprietyPointDate());
							}
						}			
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
	            			this.setNumber(ProprietyPoint.getCurrentNumberLowerHouse() + 1);
	            			ProprietyPoint.updateCurrentNumberLowerHouse(ProprietyPoint.getCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
	            			this.setNumber(ProprietyPoint.getCurrentNumberUpperHouse() + 1);
	            			ProprietyPoint.updateCurrentNumberUpperHouse(ProprietyPoint.getCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		addProprietyPointDraft();
						return (ProprietyPoint)super.persist();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
			}else if(this.getNumber() != null){
				addProprietyPointDraft();
			}
		}
		return (ProprietyPoint) super.persist();
	}
	
	@Override
	public ProprietyPoint merge() {
		ProprietyPoint proprietyPoint = null;
		if(this.getInternalStatus().getType().equals(ApplicationConstants.PROPRIETYPOINT_SUBMIT)) {
			if(this.getNumber() == null) {
//				synchronized (this) {
//					Integer number = ProprietyPoint.assignProprietyPointNo(this.getHouseType(),
//							this.getSession(), this.getDeviceType(),this.getLocale());
//					this.setNumber(number + 1);
//					addProprietyPointDraft();
//					proprietyPoint = (ProprietyPoint) super.merge();
//				}
				synchronized (ProprietyPoint.class) {                	
                	Integer number = null;
                	Boolean isProprietyPointDateDifferent = false;
					try {
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {					
//							if (ProprietyPoint.getCurrentNumberLowerHouse() == 0) {
//								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
//								ProprietyPoint.updateCurrentNumberLowerHouse(number);
//							}
							if(ProprietyPoint.getCurrentProprietyPointDateLowerHouse()==null) {
	                    		isProprietyPointDateDifferent = true;
	                    	} else if(ProprietyPoint.getCurrentProprietyPointDateLowerHouse().compareTo(this.getProprietyPointDate())!=0) {
	                    		isProprietyPointDateDifferent = true;
	                    	}
							if (ProprietyPoint.getCurrentNumberLowerHouse()==0 || isProprietyPointDateDifferent) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getProprietyPointDate(), this.getLocale());
								ProprietyPoint.updateCurrentNumberLowerHouse(number);
								ProprietyPoint.updateCurrentProprietyPointDateLowerHouse(this.getProprietyPointDate());
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {					
							if(ProprietyPoint.getCurrentProprietyPointDateUpperHouse()==null) {
	                    		isProprietyPointDateDifferent = true;
	                    	} else if(DateUtil.compareDatePartOnly(new Date(), this.getSession().getEndDate())==0) {
	                    		isProprietyPointDateDifferent = true;
	                    	} else if(ProprietyPoint.getCurrentProprietyPointDateUpperHouse().compareTo(this.getProprietyPointDate())!=0) {
	                    		isProprietyPointDateDifferent = true;
	                    	}
							if (ProprietyPoint.getCurrentNumberUpperHouse()==0 || isProprietyPointDateDifferent) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getProprietyPointDate(), this.getLocale());
								ProprietyPoint.updateCurrentNumberUpperHouse(number);
								ProprietyPoint.updateCurrentProprietyPointDateUpperHouse(this.getProprietyPointDate());
							}
						}			
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
	            			this.setNumber(ProprietyPoint.getCurrentNumberLowerHouse() + 1);
	            			ProprietyPoint.updateCurrentNumberLowerHouse(ProprietyPoint.getCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
	            			this.setNumber(ProprietyPoint.getCurrentNumberUpperHouse() + 1);
	            			ProprietyPoint.updateCurrentNumberUpperHouse(ProprietyPoint.getCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		addProprietyPointDraft();
	            		proprietyPoint = (ProprietyPoint) super.merge();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
			} else {
				ProprietyPoint oldProprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldProprietyPoint.getClubbedEntities());
            	}
				this.addProprietyPointDraft();
				proprietyPoint = (ProprietyPoint) super.merge();
			}
		}
		if(proprietyPoint != null) {
			return proprietyPoint;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.PROPRIETYPOINT_INCOMPLETE) 
					|| this.getInternalStatus().getType().equals(ApplicationConstants.PROPRIETYPOINT_COMPLETE)) {
				return (ProprietyPoint) super.merge();
			}else {
				ProprietyPoint oldProprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldProprietyPoint.getClubbedEntities());
            	}
				this.addProprietyPointDraft();
				return (ProprietyPoint) super.merge();
			}
		}
	}
	
	/**
     * Adds the propriety point draft.
     */
    private void addProprietyPointDraft() {
    	ProprietyPointDraft draft = new ProprietyPointDraft();
    	draft.setProprietyPointId(this.getId());
    	draft.setLocale(this.getLocale());
        draft.setRemarks(this.getRemarks());
        
        draft.setParent(this.getParent());
        draft.setClubbedEntities(this.getClubbedEntities());
        
        draft.setEditedAs(this.getEditedAs());
        draft.setEditedBy(this.getEditedBy());
        draft.setEditedOn(this.getEditedOn());
        
        draft.setMinistry(this.getMinistry());
        draft.setSubDepartment(this.getSubDepartment());
        
        draft.setStatus(this.getStatus());
        draft.setInternalStatus(this.getInternalStatus());
        draft.setRecommendationStatus(this.getRecommendationStatus());
        draft.setBallotStatus(this.getBallotStatus());
        draft.setDiscussionDate(this.getDiscussionDate());
       
    	if(this.getRevisedSubject()!=null && !this.getRevisedSubject().isEmpty()) {
    		draft.setSubject(this.getRevisedSubject());
    	} else {
    		draft.setSubject(this.getSubject());
    	}
    	
    	if(this.getRevisedPointsOfPropriety()!=null && !this.getRevisedPointsOfPropriety().isEmpty()) {
    		draft.setPointsOfPropriety(this.getRevisedPointsOfPropriety());
    	} else {
    		draft.setPointsOfPropriety(this.getPointsOfPropriety());
    	}
    	
    	if(this.getAdmissionNumber() != null) {
    		draft.setAdmissionNumber(admissionNumber);
    	}
    	
    	if(this.getReply()!=null) {
    		draft.setReply(this.getReply());
    	}
    	if(this.getReplyDoc()!=null) {
    		draft.setReplyDoc(this.getReplyDoc());
    	}
    	        
        if(this.getId() != null) {
            ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, this.getId());
            Set<ProprietyPointDraft> originalDrafts = proprietyPoint.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new HashSet<ProprietyPointDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            Set<ProprietyPointDraft> originalDrafts = new HashSet<ProprietyPointDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
    
    public static Integer assignNumber(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) {
		return getProprietyPointRepository().assignNumber(houseType,session,type,locale);
	}
    
    public static Integer assignNumber(final HouseType houseType,
			final Session session,final Date proprietyPointDate,final String locale) {
		return getProprietyPointRepository().assignNumber(houseType,session,proprietyPointDate,locale);
	}
	
	public static Integer assignAdmissionNumber(final Session session, final String locale) {
		return getProprietyPointRepository().assignAdmissionNumber(session, locale);
	}
    
    /**
     * The merge function, besides updating  Propriety Point, performs various actions
     * based on Propriety Point's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the ProprietyPoint
     */
    public ProprietyPoint simpleMerge() {
    	ProprietyPoint proprietyPoint = (ProprietyPoint) super.merge();
        return proprietyPoint;
    }

	public static Date findDefaultProprietyPointDateForSession(final Session session, final Boolean isForMemberLogin) throws ELSException {
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		if(session.getHouse().getType().getType().equals(ApplicationConstants.UPPER_HOUSE)) { //for upperhouse and assuming submissions for next session date
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1); 
			Date currentDatePlusOne = c.getTime();
			
			if(Session.isGivenDateInSession(currentDatePlusOne,session)) {
				if((!isForMemberLogin || ProprietyPoint.validateSubmissionEndTime(session, new Date())) && !Holiday.isHolidayOnDate(currentDatePlusOne, session.getLocale())) {
					return currentDatePlusOne;
				} else {
					Date nextSessionWorkingDay = session.getNextSessionDate(currentDatePlusOne, 1, session.getLocale());
					if(nextSessionWorkingDay!=null) {
						return nextSessionWorkingDay;
					} else {
						return session.getEndDate();
					}
				}						
			} else {
//				Date nextSessionWorkingDay = session.getNextSessionDate(currentDatePlusOne, 1, session.getLocale());
//				if(nextSessionWorkingDay!=null) {
//					return nextSessionWorkingDay;
//				} else {
//					return session.getEndDate();
//				}
				return session.getEndDate();
			}
		} 
		else { //for lowerhouse and assuming submissions for current session date only as default
			if(session==null || session.getId()==null) {
				throw new ELSException();
			}
			if(Session.isCurrentDateInSession(session)) {
				if(!isForMemberLogin || ProprietyPoint.validateSubmissionEndTime(session, new Date())) {
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
	}
    
    public static List<ProprietyPoint> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		return getProprietyPointRepository().findAllReadyForSubmissionByMember(session, primaryMember, motionType, itemsCount, locale);
	}
    
    public static Boolean isDuplicateNumberExist(Integer number, Long id, String locale) {
		return getProprietyPointRepository().isDuplicateNumberExist(number, id, locale);
	}	
	
	public static boolean isDatewiseMaximumLimitForMemberReached(final HouseType houseType, final DeviceType deviceType, final Session session, final Member member, final Date proprietyPointDate, final String locale) {
		boolean isDatewiseMaximumLimitForMemberReached = false;
		
		CustomParameter csptDatewiseMaximumLimitForMember = CustomParameter.findByName(CustomParameter.class, "PROIS_DATEWISE_MAXIMUM_LIMIT_FOR_MEMBER_"+houseType.getType().toUpperCase(), "");
		if(csptDatewiseMaximumLimitForMember!=null 
				&& csptDatewiseMaximumLimitForMember.getValue()!=null
				&& !csptDatewiseMaximumLimitForMember.getValue().isEmpty()) {
			
			int maximumLimitCount = Integer.parseInt(csptDatewiseMaximumLimitForMember.getValue());
			
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[] {locale});
			queryParameters.put("deviceTypeId", new String[] {deviceType.getId().toString()});
			queryParameters.put("sessionId", new String[] {session.getId().toString()});
			queryParameters.put("memberId", new String[] {member.getId().toString()});
			queryParameters.put("proprietyPointDate", new String[] {FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.DB_DATEFORMAT)});
			@SuppressWarnings("rawtypes")
			List proprietyPointsOfMemberForGivenDate = Query.findResultListOfGivenClass("PROPRIETY_POINTS_OF_MEMBER_FOR_DATE_IN_GIVEN_SESSION", queryParameters, ProprietyPoint.class);
			
			if(proprietyPointsOfMemberForGivenDate!=null 
					&& !proprietyPointsOfMemberForGivenDate.isEmpty()
					&& proprietyPointsOfMemberForGivenDate.size() >= maximumLimitCount) {
				
				isDatewiseMaximumLimitForMemberReached = true;
			}
		}		
		
		return isDatewiseMaximumLimitForMemberReached;
	}
    
    public ProprietyPointDraft findPreviousDraft() {
		return getProprietyPointRepository().findPreviousDraft(this.getId());
	}
	
	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final ProprietyPoint proprietyPoint) {
    	return getProprietyPointRepository().findClubbedEntitiesByPosition(proprietyPoint);
    }
	
    public List<ClubbedEntity> findClubbedEntitiesByDeviceNumber(final String sortOrder) {
    	return getProprietyPointRepository().findClubbedEntitiesByDeviceNumber(this, sortOrder);
    }
    
    public static List<RevisionHistoryVO> getRevisions(final Long proprietyPointId, final String locale) {
        return getProprietyPointRepository().getRevisions(proprietyPointId, locale);
    }
    
    public static List<ProprietyPoint> findAllAdmissionNumberPointsofSpecificSession(final Session session,
			final String locale) throws ELSException{
	return getProprietyPointRepository().findAllAdmissionNumberPointsofSpecificSession(session, locale);
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
		List<ClubbedEntity> clubbedEntities = ProprietyPoint.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getProprietyPoint().getInternalStatus().getType().equals(ApplicationConstants.PROPRIETYPOINT_SYSTEM_CLUBBED)
						|| ce.getProprietyPoint().getInternalStatus().getType().equals(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION)) {
					member = ce.getProprietyPoint().getPrimaryMember();
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
					List<SupportingMember> clubbedSupportingMembers = ce.getProprietyPoint().getSupportingMembers();
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
			Status submitStatus = Status.findByType(ApplicationConstants.PROPRIETYPOINT_SUBMIT, this.getLocale());
			if(this.getStatus().getPriority()>=submitStatus.getPriority()) {
				memberStatus = submitStatus;
			} else {
				memberStatus = this.getStatus();
			}
		}		
		return memberStatus;
	}
    
    public void startWorkflow(final ProprietyPoint adjournmentMotion, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		adjournmentMotion.endWorkflow(adjournmentMotion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
		adjournmentMotion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(adjournmentMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
	
	public void endWorkflow(final ProprietyPoint adjournmentMotion, final String workflowHouseType, final String locale) throws ELSException {
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
    
    public Workflow findWorkflowFromStatus() throws ELSException {
    	Workflow workflow = Workflow.findByStatus(this.getInternalStatus(), this.getLocale());
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
    	if(this.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
        	Date nextWorkingDate = Holiday.getNextWorkingDateFrom(new Date(), 1, this.getLocale());
        	if(DateUtil.compareDatePartOnly(this.getProprietyPointDate(), nextWorkingDate) == 0
        			//&& (DateUtil.compareDatePartOnly(this.getSubmissionDate(), new Date())) == 0
        			&& !(Holiday.isHolidayOnDate(this.getProprietyPointDate(), this.getLocale()))) {
        		return true;
        	} else if(DateUtil.compareDatePartOnly(new Date(), this.getSession().getEndDate()) == 0
        			&& (DateUtil.compareDatePartOnly(this.getProprietyPointDate(), this.getSession().getEndDate())) == 0
        			&& !(Holiday.isHolidayOnDate(this.getProprietyPointDate(), this.getLocale()))) {
        		return true;
        	} else {
        		return false;
        	}    		
    	}
    	else {
    		if(DateUtil.compareDatePartOnly(this.getProprietyPointDate(), new Date()) == 0
        			&& !(Holiday.isHolidayOnDate(this.getProprietyPointDate(), this.getLocale()))) {
        		return true;
        	} else {
        		return false;
        	}
    	}
    }
    
    public static Boolean validateSubmissionTime(final Session proprietyPointSession, Date proprietyPointDate) {
    	CustomParameter csptSubmissionStartTimeValidationRequired = CustomParameter.findByName(CustomParameter.class, "PROIS_SUBMISSION_START_TIME_VALIDATION_REQUIRED", "");
    	if(csptSubmissionStartTimeValidationRequired!=null && csptSubmissionStartTimeValidationRequired.getValue().equals("YES")) {
    		Date currentSubmissionTime = new Date();    	
        	Date submissionStartTime = ProprietyPoint.findSubmissionStartTime(proprietyPointSession, proprietyPointDate);
        	Date submissionEndTime = ProprietyPoint.findSubmissionEndTime(proprietyPointSession, proprietyPointDate);    	
        	if(currentSubmissionTime.compareTo(submissionStartTime)>=0 && currentSubmissionTime.compareTo(submissionEndTime)<=0) {
        		return true;
        	} else {
        		return false;
        	}
    	} else {
    		return ProprietyPoint.validateSubmissionEndTime(proprietyPointSession, proprietyPointDate);
    	}
    }
    
    public static Boolean validateSubmissionTimeUpperHouse(final Session proprietyPointSession, Date proprietyPointDate, Date currentSubmissionTime) {
    	boolean isSubmissionDateValidated = false;
    	CustomParameter csptSubmissionStartTimeValidationRequired = CustomParameter.findByName(CustomParameter.class, "PROIS_SUBMISSION_START_TIME_VALIDATION_REQUIRED", "");
    	if(csptSubmissionStartTimeValidationRequired!=null && csptSubmissionStartTimeValidationRequired.getValue().equals("YES")) {	    		   	
        	if(DateUtil.compareDatePartOnly(new Date(), proprietyPointSession.getEndDate()) < 0
        			&& DateUtil.compareDatePartOnly(proprietyPointDate, new Date()) > 0) {
        		isSubmissionDateValidated = true;
        	} else if(DateUtil.compareDatePartOnly(new Date(), proprietyPointSession.getEndDate()) == 0
        			&& DateUtil.compareDatePartOnly(proprietyPointDate, proprietyPointSession.getEndDate()) == 0) {
        		isSubmissionDateValidated = true;
        	}
        	if(isSubmissionDateValidated) {
        		Date submissionStartTime = ProprietyPoint.findSubmissionStartTime(proprietyPointSession, currentSubmissionTime);
            	Date submissionEndTime = ProprietyPoint.findSubmissionEndTime(proprietyPointSession, currentSubmissionTime);    	
            	if(currentSubmissionTime.compareTo(submissionStartTime)>=0 && currentSubmissionTime.compareTo(submissionEndTime)<=0) {
            		return true;
            	} else {
            		return false;
            	}
        	} else {
        		return false;
        	}
    	} else {
    		return ProprietyPoint.validateSubmissionEndTime(proprietyPointSession, currentSubmissionTime);
    	}
    }
    	
	 public static Boolean validateSubmissionEndTime(final Session proprietyPointSession, Date currentSubmissionTime) {
	    	Date submissionEndTime = ProprietyPoint.findSubmissionEndTime(proprietyPointSession, currentSubmissionTime);
	    	if(currentSubmissionTime.compareTo(submissionEndTime)<=0) {
	    		return true;
	    	} else {
	    		return false;
	    	}    	
	    }
   
   public static Date findSubmissionStartTime(final Session proprietyPointSession, Date proprietyPointDate) {
    	//find submission start date part
    	String strProprietyPointDate = FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.SERVER_DATEFORMAT);
    	String submissionStartDatePart = strProprietyPointDate;
    	//find submission start time part
    	String submissionStartTimePart = "00:00:00";
    	if(proprietyPointSession!=null) {
    		String submissionStartTimeParameter = proprietyPointSession.getParameter(ApplicationConstants.PROPRIETY_POINT+"_submissionStartTime_"+strProprietyPointDate);
    		if(submissionStartTimeParameter!=null && !submissionStartTimeParameter.isEmpty()) {
    			submissionStartTimePart = submissionStartTimeParameter + ":00";
    		} else {
    			String submissionStartTimeDefaultSessionParameter = proprietyPointSession.getParameter(ApplicationConstants.PROPRIETY_POINT+"_submissionStartTime");
        		if(submissionStartTimeDefaultSessionParameter!=null && !submissionStartTimeDefaultSessionParameter.isEmpty()) {
        			String[] submissionStartTimeDefaultSessionParameters =  submissionStartTimeDefaultSessionParameter.split(" ");
    				submissionStartTimePart = submissionStartTimeDefaultSessionParameters[1];
        		} else {
        			CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROPRIETY_POINT.toUpperCase()+"_SUBMISSIONSTARTTIME_"+proprietyPointSession.getHouse().getType().getType().toUpperCase(), "");
            		if(csptsubmissionStartTime!=null && csptsubmissionStartTime.getValue()!=null && !csptsubmissionStartTime.getValue().isEmpty()) {
            			submissionStartTimePart = csptsubmissionStartTime.getValue() + ":00";
            		}
        		}
    		}
    	} else {
    		CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROPRIETY_POINT.toUpperCase()+"_SUBMISSIONSTARTTIME_"+proprietyPointSession.getHouse().getType().getType().toUpperCase(), "");
    		if(csptsubmissionStartTime!=null && csptsubmissionStartTime.getValue()!=null && !csptsubmissionStartTime.getValue().isEmpty()) {
    			submissionStartTimePart = csptsubmissionStartTime.getValue() + ":00";
    		}
    	}
    	//find submission start time
    	String submissionStartTime = submissionStartDatePart + " " + submissionStartTimePart;
    	return FormaterUtil.formatStringToDate(submissionStartTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
    }

   public static Date findSubmissionEndTime(final Session proprietyPointSession, Date proprietyPointDate) {
    	//find submission end date part
    	String strProprietyPointDate = FormaterUtil.formatDateToString(proprietyPointDate, ApplicationConstants.SERVER_DATEFORMAT);
    	String submissionEndDatePart = strProprietyPointDate;
    	//find submission end time part
    	String submissionEndTimePart = "00:00:00";
    	if(proprietyPointSession!=null) {
    		String submissionEndTimeParameter = proprietyPointSession.getParameter(ApplicationConstants.PROPRIETY_POINT+"_submissionEndTime_"+strProprietyPointDate);
    		if(submissionEndTimeParameter!=null && !submissionEndTimeParameter.isEmpty()) {
    			submissionEndTimePart = submissionEndTimeParameter + ":00";
    		} else {
    			String submissionEndTimeDefaultSessionParameter = proprietyPointSession.getParameter(ApplicationConstants.PROPRIETY_POINT+"_submissionEndTime");
        		if(submissionEndTimeDefaultSessionParameter!=null && !submissionEndTimeDefaultSessionParameter.isEmpty()) {
        			String[] submissionEndTimeDefaultSessionParameters =  submissionEndTimeDefaultSessionParameter.split(" ");
        			submissionEndTimePart = submissionEndTimeDefaultSessionParameters[1];
        		} else {
        			CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROPRIETY_POINT.toUpperCase()+"_SUBMISSIONENDTIME_"+proprietyPointSession.getHouse().getType().getType().toUpperCase(), "");
            		if(csptsubmissionEndTime!=null && csptsubmissionEndTime.getValue()!=null && !csptsubmissionEndTime.getValue().isEmpty()) {
            			submissionEndTimePart = csptsubmissionEndTime.getValue() + ":00";
            		}
        		}
    		}
    	} else {
    		CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.PROPRIETY_POINT.toUpperCase()+"_SUBMISSIONENDTIME_"+proprietyPointSession.getHouse().getType().getType().toUpperCase(), "");
    		if(csptsubmissionEndTime!=null && csptsubmissionEndTime.getValue()!=null && !csptsubmissionEndTime.getValue().isEmpty()) {
    			submissionEndTimePart = csptsubmissionEndTime.getValue() + ":00";
    		}
    	}
    	//find submission end time
    	String submissionEndTime = submissionEndDatePart + " " + submissionEndTimePart;
    	return FormaterUtil.formatStringToDate(submissionEndTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
    }
   
    /**** clubbing related code ****/
    public static void updateDomainFieldsOnClubbingFinalisation(ProprietyPoint parent, ProprietyPoint child) {
    	/** copy latest subject of parent to revised subject of child **/
		if(parent.getRevisedSubject()!=null && !parent.getRevisedSubject().isEmpty()) {
			child.setRevisedSubject(parent.getRevisedSubject());
		} else {
			child.setRevisedSubject(parent.getSubject());
		}
		/** copy latest notice content of parent to revised notice content of child **/
		if(parent.getRevisedPointsOfPropriety()!=null && !parent.getRevisedPointsOfPropriety().isEmpty()) {
			child.setRevisedPointsOfPropriety(parent.getRevisedPointsOfPropriety());
		} else {
			child.setRevisedPointsOfPropriety(parent.getPointsOfPropriety());
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

    public static List<SearchVO> fullTextSearchForSearching(String param, int start, int noOfRecords, String locale,
			Map<String, String[]> requestMap) {
		return getProprietyPointRepository().fullTextSearchForSearching(param,start,noOfRecords, locale, requestMap);
	}
    
    
    /**
     * Find the ProprietyPoints based on ballot status
     * @param session
     * @param deviceType
     * @param discussionDate
     * @param internalStatuses
     * @param hasParent
     * @param isBalloted
     * @param startTime
     * @param endTime
     * @param sortOrder
     * @param locale
     * @return
     */
    public static List<ProprietyPoint> findByBallot(final Session session,
			final DeviceType deviceType,
			final Date discussionDate,
			final Status[] internalStatuses,
			final Boolean hasParent,
			final Boolean isBalloted,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) {
    	
    	return getProprietyPointRepository().findByBallot(session, deviceType, discussionDate, 
    			internalStatuses, hasParent, isBalloted, 
    			isMandatoryUnique, isPreBallot, startTime, 
    			endTime, sortOrder, locale);
    }
    

    /********************************************* Getters & Setters *******************************************/
	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

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

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getAdmissionNumber() {
		return admissionNumber;
	}

	public void setAdmissionNumber(Integer admissionNumber) {
		this.admissionNumber = admissionNumber;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Member getPrimaryMember() {
		return primaryMember;
	}

	public void setPrimaryMember(Member primaryMember) {
		this.primaryMember = primaryMember;
	}

	public List<SupportingMember> getSupportingMembers() {
		return supportingMembers;
	}

	public void setSupportingMembers(List<SupportingMember> supportingMembers) {
		this.supportingMembers = supportingMembers;
	}

	public Date getProprietyPointDate() {
		return proprietyPointDate;
	}

	public void setProprietyPointDate(Date proprietyPointDate) {
		this.proprietyPointDate = proprietyPointDate;
	}

	/**
	 * @return the formattedProprietyPointDate
	 */
	public String getFormattedProprietyPointDate() {
		if(this.proprietyPointDate!=null) {
			try {
				formattedProprietyPointDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(this.proprietyPointDate, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", this.getLocale());
			} catch (ELSException e) {
				formattedProprietyPointDate = "";
			}
		} else {
			formattedProprietyPointDate = "";
		}
		return formattedProprietyPointDate;
	}
    
    public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRevisedSubject() {
		return revisedSubject;
	}

	public void setRevisedSubject(String revisedSubject) {
		this.revisedSubject = revisedSubject;
	}

	public String getPointsOfPropriety() {
		return pointsOfPropriety;
	}

	public void setPointsOfPropriety(String pointsOfPropriety) {
		this.pointsOfPropriety = pointsOfPropriety;
	}

	public String getRevisedPointsOfPropriety() {
		return revisedPointsOfPropriety;
	}

	public void setRevisedPointsOfPropriety(String revisedPointsOfPropriety) {
		this.revisedPointsOfPropriety = revisedPointsOfPropriety;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}
	
	public String getReplyDoc() {
		return replyDoc;
	}

	public void setReplyDoc(String replyDoc) {
		this.replyDoc = replyDoc;
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

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public ProprietyPoint getParent() {
		return parent;
	}

	public void setParent(ProprietyPoint parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getInternalStatus() {
		return internalStatus;
	}

	public void setInternalStatus(Status internalStatus) {
		this.internalStatus = internalStatus;
	}

	public Status getRecommendationStatus() {
		return recommendationStatus;
	}

	public void setRecommendationStatus(Status recommendationStatus) {
		this.recommendationStatus = recommendationStatus;
	}

	public Status getBallotStatus() {
		return ballotStatus;
	}

	public void setBallotStatus(Status ballotStatus) {
		this.ballotStatus = ballotStatus;
	}

	public Status getDiscussionStatus() {
		return discussionStatus;
	}

	public void setDiscussionStatus(Status discussionStatus) {
		this.discussionStatus = discussionStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarksAboutDecision() {
		return remarksAboutDecision;
	}

	public void setRemarksAboutDecision(String remarksAboutDecision) {
		this.remarksAboutDecision = remarksAboutDecision;
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

	public String getEditedAs() {
		return editedAs;
	}

	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	public Set<ProprietyPointDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(Set<ProprietyPointDraft> drafts) {
		this.drafts = drafts;
	}
	
	public String getWorkflowStarted() {
		return workflowStarted;
	}


	public void setWorkflowStarted(String workflowStarted) {
		this.workflowStarted = workflowStarted;
	}


	public String getActor() {
		return actor;
	}


	public void setActor(String actor) {
		this.actor = actor;
	}


	public String getLocalizedActorName() {
		return localizedActorName;
	}


	public void setLocalizedActorName(String localizedActorName) {
		this.localizedActorName = localizedActorName;
	}


	public String getEndFlag() {
		return endFlag;
	}


	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public Date getWorkflowStartedOn() {
		return workflowStartedOn;
	}


	public void setWorkflowStartedOn(Date workflowStartedOn) {
		this.workflowStartedOn = workflowStartedOn;
	}


	public Date getTaskReceivedOn() {
		return taskReceivedOn;
	}


	public void setTaskReceivedOn(Date taskReceivedOn) {
		this.taskReceivedOn = taskReceivedOn;
	}


	public boolean isBulkSubmitted() {
		return bulkSubmitted;
	}


	public void setBulkSubmitted(boolean bulkSubmitted) {
		this.bulkSubmitted = bulkSubmitted;
	}


	public Long getWorkflowDetailsId() {
		return workflowDetailsId;
	}


	public void setWorkflowDetailsId(Long workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}
	
	/****number atomic value ****/
	public static void updateCurrentNumberLowerHouse(Integer num){
		synchronized (ProprietyPoint.CUR_NUM_LOWER_HOUSE) {
			ProprietyPoint.CUR_NUM_LOWER_HOUSE = num;
		}
	}

	public static synchronized Integer getCurrentNumberLowerHouse(){
		return ProprietyPoint.CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateCurrentNumberUpperHouse(Integer num){
		synchronized (ProprietyPoint.CUR_NUM_UPPER_HOUSE) {
			ProprietyPoint.CUR_NUM_UPPER_HOUSE = num;
		}
	}

	public static synchronized Integer getCurrentNumberUpperHouse(){
		return ProprietyPoint.CUR_NUM_UPPER_HOUSE;
	}
	
	public static void updateCurrentProprietyPointDateLowerHouse(Date proprietyPointDate){
		synchronized (ProprietyPoint.CUR_PROPRIETYPOINT_DATE_LOWER_HOUSE) {
			ProprietyPoint.CUR_PROPRIETYPOINT_DATE_LOWER_HOUSE = proprietyPointDate;
		}
	}

	public static synchronized Date getCurrentProprietyPointDateLowerHouse(){
		return ProprietyPoint.CUR_PROPRIETYPOINT_DATE_LOWER_HOUSE;
	}
	
	public static void updateCurrentProprietyPointDateUpperHouse(Date proprietyPointDate){
		synchronized (ProprietyPoint.CUR_PROPRIETYPOINT_DATE_UPPER_HOUSE) {
			ProprietyPoint.CUR_PROPRIETYPOINT_DATE_UPPER_HOUSE = proprietyPointDate;
		}
	}

	public static synchronized Date getCurrentProprietyPointDateUpperHouse(){
		return ProprietyPoint.CUR_PROPRIETYPOINT_DATE_UPPER_HOUSE;
	}
	
	public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
    		
			ref.setName(ApplicationConstants.PROPRIETY_POINT);
			ref.setNumber(ProprietyPoint.getCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	}else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)){
    		
    		ref.setName(ApplicationConstants.PROPRIETY_POINT);
			ref.setNumber(ProprietyPoint.getCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(final Integer num, final String houseType, final String device){
    	
    	if(device.equals(ApplicationConstants.PROPRIETY_POINT)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			ProprietyPoint.updateCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			ProprietyPoint.updateCurrentNumberUpperHouse(num);
    		}
	    	
	    	
    	}
    }
    
    public static void supportingMemberWorkflowDeletion(final ProprietyPoint proprietyPoint) {
    	if(proprietyPoint!=null && proprietyPoint.getId()>0) {
    		if(anySupportingMembersWorkflows(proprietyPoint)) {
    			deleteSupportingMembersWorkflows(proprietyPoint);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final ProprietyPoint proprietyPoint) {
		List<SupportingMember> supportingMembers = proprietyPoint.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final ProprietyPoint proprietyPoint) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(proprietyPoint!=null && proprietyPoint.getId()>0 && proprietyPoint.getSupportingMembers()!=null 
				&& proprietyPoint.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = proprietyPoint.getSupportingMembers();
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					workflowDetailsList.add(Long.valueOf(sm.getWorkflowDetailsId()));
			}
		}
		
		int deleteCount=0;
		for(Long workFlowDetailsId : workflowDetailsList) {
			BaseDomain workFlowdetails = WorkflowDetails.findById(WorkflowDetails.class, workFlowDetailsId);
			boolean isDeleted = WorkflowDetails.getBaseRepository().remove(workFlowdetails);
			if(isDeleted)deleteCount++;
		}
		
		return workflowDetailsList!=null && deleteCount== workflowDetailsList.size();
	}
	
}