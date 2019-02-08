package org.mkcl.els.domain;

import java.io.Serializable;
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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.repository.ProprietyPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="propriety_points")
@JsonIgnoreProperties({"houseType", "session", "deviceType", "supportingMembers", "ballotStatus", "drafts"})
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
    
    /** The rejection reason. */
    @Column(length=30000)
    private String rejectionReason;
    
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
    
    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
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
					try {
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {					
							if (ProprietyPoint.getCurrentNumberLowerHouse() == 0) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								ProprietyPoint.updateCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {					
							if (ProprietyPoint.getCurrentNumberUpperHouse() == 0) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								ProprietyPoint.updateCurrentNumberUpperHouse(number);
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
					try {
						String houseType = this.getHouseType().getType();
						
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {					
							if (ProprietyPoint.getCurrentNumberLowerHouse() == 0) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								ProprietyPoint.updateCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {					
							if (ProprietyPoint.getCurrentNumberUpperHouse() == 0) {
								number = ProprietyPoint.assignNumber(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								ProprietyPoint.updateCurrentNumberUpperHouse(number);
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
    
    public ProprietyPointDraft findPreviousDraft() {
		return getProprietyPointRepository().findPreviousDraft(this.getId());
	}
    
    public static List<RevisionHistoryVO> getRevisions(final Long proprietyPointId, final String locale) {
        return getProprietyPointRepository().getRevisions(proprietyPointId, locale);
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

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	
}