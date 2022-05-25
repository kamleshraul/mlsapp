package org.mkcl.els.domain;

import java.io.Serializable;
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
import org.mkcl.els.repository.RulesSuspensionMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="rules_suspension_motions")
@JsonIgnoreProperties({"houseType", "session", "type", "supportingMembers", 
	"parent", "clubbedEntities","drafts"})
public class RulesSuspensionMotion extends Device implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**** Attributes ****/
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
	
	/** The primary member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="rules_suspension_motions_supportingmembers",
	joinColumns={@JoinColumn(name="rules_suspension_motion_id", referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="supportingmember_id", referencedColumnName="id")})
	private List<SupportingMember> supportingMembers;
	
	
    /*** The ministry. ***/
  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
  @JoinTable(name="rulessuspensionmotion_ministries",
          joinColumns={@JoinColumn(name="rulessuspensionmotion_id", referencedColumnName="id")},
          inverseJoinColumns={@JoinColumn(name="ministry_id", referencedColumnName="id")})
  private List<Ministry> ministries;

  /*** The department. ***/
  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
  @JoinTable(name="rulessuspensionmotion_departments",
          joinColumns={@JoinColumn(name="rulessuspensionmotion_id", referencedColumnName="id")},
          inverseJoinColumns={@JoinColumn(name="department_id", referencedColumnName="id")})
  private List<Department> departments;

  /*** The sub department. ***/
  @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
  @JoinTable(name="rulessuspensionmotion_subdepartments",
          joinColumns={@JoinColumn(name="rulessuspensionmotion_id", referencedColumnName="id")},
          inverseJoinColumns={@JoinColumn(name="subdepartment_id", referencedColumnName="id")})
  private List<SubDepartment> subDepartments;
	
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
	private Date ruleSuspensionDate;
	
	/** The adjourning date. */
	@Transient
	private String formattedRuleSuspensionDate;
	
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
    
    /** The revised notice content. */
    @Column(length=30000)
    private String authorityDraft;
    
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
	private RulesSuspensionMotion parent;
	
	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="rules_suspension_motions_clubbingentities", 
	joinColumns={@JoinColumn(name="rules_suspension_motion_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;

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
    
    private static transient volatile Date CUR_RULE_SUSPENSION_DATE_LOWER_HOUSE = new Date();
	
    private static transient volatile Date CUR_RULE_SUSPENSION_DATE_UPPER_HOUSE = new Date();
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="rulessuspensionmotions_drafts_association", 
    		joinColumns={@JoinColumn(name="rules_suspension_motion_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="rules_suspension_motion_draft_id", referencedColumnName="id")})
    private List<RulesSuspensionMotionDraft> drafts;
    
    
    /** The rules suspension motion repository. */
    @Autowired
    private transient RulesSuspensionMotionRepository rulesSuspensionMotionRepository;
	
    /**** Constructor and Methods ****/
    
    public RulesSuspensionMotion() {
		super();
	}
    
	public static Date findDefaultRuleSuspensionDateForSession(Session session, boolean isForMemberLogin) throws ELSException {
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		if(Session.isCurrentDateInSession(session)) {
			if(!isForMemberLogin || RulesSuspensionMotion.validateSubmissionEndTime(session, new Date())) {
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
    
	
	/**
	 * @return the rulesSuspensionMotionRepository
	 */
	private static RulesSuspensionMotionRepository getRulesSuspensionMotionRepository() {
		RulesSuspensionMotionRepository rulesSuspensionMotionRepository = new RulesSuspensionMotion().rulesSuspensionMotionRepository;
        if (rulesSuspensionMotionRepository == null) {
            throw new IllegalStateException(
            	"RulesSuspensionMotionRepository has not been injected in Rules Suspension Motion Domain");
        }
        return rulesSuspensionMotionRepository;
	}
	
	@Override
	public RulesSuspensionMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (RulesSuspensionMotion.class) {
                	Integer number = null;
                	Boolean isRulesSuspensionDateDifferent = false;                	
                	String houseType = this.getHouseType().getType();
                	if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {	
                		if(RulesSuspensionMotion.getCurrentRulesSuspensionDateLowerHouse() == null) {
                			isRulesSuspensionDateDifferent = true;
                    	} else if(RulesSuspensionMotion.getCurrentRulesSuspensionDateLowerHouse().compareTo(this.getRuleSuspensionDate())!=0) {
                    		isRulesSuspensionDateDifferent = true;
                    	}
						if (RulesSuspensionMotion.getCurrentNumberLowerHouse()==0 || isRulesSuspensionDateDifferent) {
							number = RulesSuspensionMotion.assignMotionNo(this.getHouseType(), this.getRuleSuspensionDate(), this.getLocale());
							RulesSuspensionMotion.updateCurrentNumberLowerHouse(number);
							RulesSuspensionMotion.updateCurrentRulesSuspensionDateLowerHouse(this.getRuleSuspensionDate());
						}
					} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(RulesSuspensionMotion.getCurrentRulesSuspensionDateUpperHouse()==null) {
							isRulesSuspensionDateDifferent = true;
                    	} else if(RulesSuspensionMotion.getCurrentRulesSuspensionDateUpperHouse().compareTo(this.getRuleSuspensionDate())!=0) {
                    		isRulesSuspensionDateDifferent = true;
                    	}
						if (RulesSuspensionMotion.getCurrentNumberUpperHouse()==0 || isRulesSuspensionDateDifferent) {
							number = RulesSuspensionMotion.assignMotionNo(this.getHouseType(), this.getRuleSuspensionDate(), this.getLocale());
							RulesSuspensionMotion.updateCurrentNumberUpperHouse(number);
							RulesSuspensionMotion.updateCurrentRulesSuspensionDateUpperHouse(this.getRuleSuspensionDate());
						}
					}
					
            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(RulesSuspensionMotion.getCurrentNumberLowerHouse() + 1);
            			RulesSuspensionMotion.updateCurrentNumberLowerHouse(RulesSuspensionMotion.getCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(RulesSuspensionMotion.getCurrentNumberUpperHouse() + 1);
            			RulesSuspensionMotion.updateCurrentNumberUpperHouse(RulesSuspensionMotion.getCurrentNumberUpperHouse() + 1);
            		}                	
            		addRulesSuspensionMotionDraft();
                    return (RulesSuspensionMotion)super.persist();
                }
            }
            else if(this.getNumber()!=null){
            	addRulesSuspensionMotionDraft();
            }
		}
		return (RulesSuspensionMotion) super.persist();
	}
	
	@Override
	public RulesSuspensionMotion merge() {
		RulesSuspensionMotion rulesSuspensionMotion = null;
		if(this.getStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT)) {
            if(this.getNumber() == null) {
                synchronized (this) {
                	Integer number = null;                	
                	Boolean isRulesSuspensionDateDifferent = false;                	
                	String houseType = this.getHouseType().getType();
                	if (houseType.equals(ApplicationConstants.LOWER_HOUSE)) {	
                		if(RulesSuspensionMotion.getCurrentRulesSuspensionDateLowerHouse()==null) {
                			isRulesSuspensionDateDifferent = true;
                    	} else if(RulesSuspensionMotion.getCurrentRulesSuspensionDateLowerHouse().compareTo(this.getRuleSuspensionDate())!=0) {
                    		isRulesSuspensionDateDifferent = true;
                    	}
						if (RulesSuspensionMotion.getCurrentNumberLowerHouse()==0 || isRulesSuspensionDateDifferent) {
							number = RulesSuspensionMotion.assignMotionNo(this.getHouseType(), this.getRuleSuspensionDate(), this.getLocale());
							RulesSuspensionMotion.updateCurrentNumberLowerHouse(number);
							RulesSuspensionMotion.updateCurrentRulesSuspensionDateLowerHouse(this.getRuleSuspensionDate());
						}
					} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(RulesSuspensionMotion.getCurrentRulesSuspensionDateUpperHouse()==null) {
							isRulesSuspensionDateDifferent = true;
                    	} else if(RulesSuspensionMotion.getCurrentRulesSuspensionDateUpperHouse().compareTo(this.getRuleSuspensionDate())!=0) {
                    		isRulesSuspensionDateDifferent = true;
                    	}
						if (RulesSuspensionMotion.getCurrentNumberUpperHouse()==0 || isRulesSuspensionDateDifferent) {
							number = RulesSuspensionMotion.assignMotionNo(this.getHouseType(), this.getRuleSuspensionDate(), this.getLocale());
							RulesSuspensionMotion.updateCurrentNumberUpperHouse(number);
							RulesSuspensionMotion.updateCurrentRulesSuspensionDateUpperHouse(this.getRuleSuspensionDate());
						}
					}					
            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(RulesSuspensionMotion.getCurrentNumberLowerHouse() + 1);
            			RulesSuspensionMotion.updateCurrentNumberLowerHouse(RulesSuspensionMotion.getCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(RulesSuspensionMotion.getCurrentNumberUpperHouse() + 1);
            			RulesSuspensionMotion.updateCurrentNumberUpperHouse(RulesSuspensionMotion.getCurrentNumberUpperHouse() + 1);
            		}
            		addRulesSuspensionMotionDraft();
            		rulesSuspensionMotion = (RulesSuspensionMotion) super.merge();
                }
            }
            else {
            	RulesSuspensionMotion oldRulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldRulesSuspensionMotion.getClubbedEntities());
            	}
            	
            	addRulesSuspensionMotionDraft();
            	rulesSuspensionMotion = (RulesSuspensionMotion) super.merge();
            }
		} else if(this.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_COMPLETE)){
			RulesSuspensionMotion oldRulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, this.getId());
			List<RulesSuspensionMotionDraft> originalDrafts = oldRulesSuspensionMotion.getDrafts();
			this.setDrafts(originalDrafts);
			//-----------------------------------------------------------------------------
			rulesSuspensionMotion = (RulesSuspensionMotion) super.merge();
		}
		if(rulesSuspensionMotion != null) {
			return rulesSuspensionMotion;
		}
		else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_INCOMPLETE) 
	            	|| 
	            	this.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_COMPLETE)){
				//added by dhananjayb to retain drafts in case of motion getting this status as result of updation error in workflow
				RulesSuspensionMotion oldRulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, this.getId());
				List<RulesSuspensionMotionDraft> originalDrafts = oldRulesSuspensionMotion.getDrafts();
				this.setDrafts(originalDrafts);
				//-----------------------------------------------------------------------------
				return (RulesSuspensionMotion) super.merge();
			} 
			else {
				RulesSuspensionMotion oldRulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, this.getId());
            	if(this.getClubbedEntities() == null){
            		this.setClubbedEntities(oldRulesSuspensionMotion.getClubbedEntities());
            	}
            	addRulesSuspensionMotionDraft();
            	return (RulesSuspensionMotion) super.merge();
			}
		}		
	}
	
	/**
     * Adds the adjournment motion draft.
     */
    private void addRulesSuspensionMotionDraft() {
    	RulesSuspensionMotionDraft draft = new RulesSuspensionMotionDraft();
        draft.setRemarks(this.getRemarks());
        
        draft.setParent(this.getParent());
        draft.setClubbedEntities(this.getClubbedEntities());

        
        draft.setEditedAs(this.getEditedAs());
        draft.setEditedBy(this.getEditedBy());
        draft.setEditedOn(this.getEditedOn());
        
        draft.setMinistries(this.getMinistries());
        draft.setSubDepartments(this.getSubDepartments());
        draft.setDepartments(this.getDepartments());
        
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
        
    	draft.setAuthorityDraft(this.getAuthorityDraft());
        if(this.getId() != null) {
            RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, this.getId());
            List<RulesSuspensionMotionDraft> originalDrafts = rulesSuspensionMotion.getDrafts();
            if(originalDrafts != null){
                originalDrafts.add(draft);
            }
            else{
                originalDrafts = new ArrayList<RulesSuspensionMotionDraft>();
                originalDrafts.add(draft);
            }
            this.setDrafts(originalDrafts);
        }
        else {
            List<RulesSuspensionMotionDraft> originalDrafts = new ArrayList<RulesSuspensionMotionDraft>();
            originalDrafts.add(draft);
            this.setDrafts(originalDrafts);
        }
    }
    
    /**
     * The merge function, besides updating  Rules Suspension Motion, performs various actions
     * based on Rules Suspension Motion's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the RulesSuspensionMotion
     */
    public RulesSuspensionMotion simpleMerge() {
    	RulesSuspensionMotion rulesSuspensionMotion = (RulesSuspensionMotion) super.merge();
        return rulesSuspensionMotion;
    }
	
	public static Integer assignMotionNo(final HouseType houseType, final Date ruleSuspensionDate, final String locale) {
		return getRulesSuspensionMotionRepository().assignMotionNo(houseType, ruleSuspensionDate, locale);		
	}
	
	public String formatNumber() {
		if(getNumber()!=null){
			return FormaterUtil.formatNumberNoGrouping(this.getNumber(), this.getLocale());			
		}else{
			return "";
		}
	}

	public static Date findDefaultRuleSuspensionDateForSession(final Session session, final Boolean isForMemberLogin) throws ELSException {
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		if(Session.isCurrentDateInSession(session)) {
			if(!isForMemberLogin || RulesSuspensionMotion.validateSubmissionEndTime(session, new Date())) {
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

	public static List<RulesSuspensionMotion> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		return getRulesSuspensionMotionRepository().findAllReadyForSubmissionByMember(session, primaryMember, motionType, itemsCount, locale);
	}
	
	public static Boolean isDuplicateNumberExist(Date ruleSuspensionDate, Integer number, Long id, String locale) {
		return getRulesSuspensionMotionRepository().isDuplicateNumberExist(ruleSuspensionDate, number, id, locale);
	}
	
	public RulesSuspensionMotionDraft findPreviousDraft() {
		return getRulesSuspensionMotionRepository().findPreviousDraft(this.getId());
	}
	
	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final RulesSuspensionMotion rulesSuspensionMotion) {
    	return getRulesSuspensionMotionRepository().findClubbedEntitiesByPosition(rulesSuspensionMotion);
    }
	
    public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final String sortOrder) {
    	return getRulesSuspensionMotionRepository().findClubbedEntitiesByMotionNumber(this, sortOrder);
    }
	
	/**
     * Gets the revisions.
     *
     * @param adjournmentMotionId the question id
     * @param locale the locale
     * @return the revisions
     */
    public static List<RevisionHistoryVO> getRevisions(final Long rulesSuspensionMotionId, final String locale) {
        return getRulesSuspensionMotionRepository().getRevisions(rulesSuspensionMotionId,locale);
    }
    
    
    public Workflow findWorkflowFromStatus() throws ELSException {
    	Workflow workflow = null;
		
		Status internalStatus = this.getInternalStatus();
		Status recommendationStatus = this.getRecommendationStatus();
		String recommendationStatusType = recommendationStatus.getType();

		if(recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
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
    	if(DateUtil.compareDatePartOnly(this.getRuleSuspensionDate(), new Date())==0
    			&& !(Holiday.isHolidayOnDate(this.getRuleSuspensionDate(), this.getLocale()))) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public static Boolean validateSubmissionTime(final Session motionSession, Date ruleSuspensionDate) {
    	CustomParameter csptSubmissionStartTimeValidationRequired = CustomParameter.findByName(CustomParameter.class, "RSMOIS_SUBMISSION_START_TIME_VALIDATION_REQUIRED", "");
    	if(csptSubmissionStartTimeValidationRequired!=null && csptSubmissionStartTimeValidationRequired.getValue().equals("YES")) {
    		Date currentSubmissionTime = new Date();    	
        	Date submissionStartTime = RulesSuspensionMotion.findSubmissionStartTime(motionSession, ruleSuspensionDate);
        	Date submissionEndTime = RulesSuspensionMotion.findSubmissionEndTime(motionSession, ruleSuspensionDate);    	
        	if(currentSubmissionTime.compareTo(submissionStartTime)>=0 && currentSubmissionTime.compareTo(submissionEndTime)<=0) {
        		return true;
        	} else {
        		return false;
        	}
    	} else {
    		return RulesSuspensionMotion.validateSubmissionEndTime(motionSession, ruleSuspensionDate);
    	}    	    	
    }
    
    public static Boolean validateSubmissionEndTime(final Session motionSession, Date ruleSuspensionDate) {
    	Date currentSubmissionTime = new Date();    	
    	Date submissionEndTime = RulesSuspensionMotion.findSubmissionEndTime(motionSession, ruleSuspensionDate);    	
    	if(currentSubmissionTime.compareTo(submissionEndTime)<=0) {
    		return true;
    	} else {
    		return false;
    	}    	
    }
    
    public static Date findSubmissionStartTime(final Session motionSession, Date ruleSuspensionDate) {
    	//find submission start date part
    	String strRulesSuspensionDate = FormaterUtil.formatDateToString(ruleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT);
    	String submissionStartDatePart = strRulesSuspensionDate;
    	//find submission start time part
    	String submissionStartTimePart = "00:00:00";
    	if(motionSession!=null) {
    		String submissionStartTimeParameter = motionSession.getParameter(ApplicationConstants.RULESSUSPENSION_MOTION+"_submissionStartTime_"+strRulesSuspensionDate);
    		if(submissionStartTimeParameter!=null && !submissionStartTimeParameter.isEmpty()) {
    			submissionStartTimePart = submissionStartTimeParameter + ":00";
    		} else {
    			String submissionStartTimeDefaultSessionParameter = motionSession.getParameter(ApplicationConstants.RULESSUSPENSION_MOTION+"_submissionStartTime");
    			if(submissionStartTimeDefaultSessionParameter!=null && !submissionStartTimeDefaultSessionParameter.isEmpty()) {
    				String[] submissionStartTimeDefaultSessionParameters =  submissionStartTimeDefaultSessionParameter.split(" ");
    				submissionStartTimePart = submissionStartTimeDefaultSessionParameters[1];
        		} else {
        			CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.RULESSUSPENSION_MOTION.toUpperCase()+"_SUBMISSIONSTARTTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
            		if(csptsubmissionStartTime!=null && csptsubmissionStartTime.getValue()!=null && !csptsubmissionStartTime.getValue().isEmpty()) {
            			submissionStartTimePart = csptsubmissionStartTime.getValue() + ":00";
            		}
        		}
    		}
    	} else {
    		CustomParameter csptsubmissionStartTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.RULESSUSPENSION_MOTION.toUpperCase()+"_SUBMISSIONSTARTTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
    		if(csptsubmissionStartTime!=null && csptsubmissionStartTime.getValue()!=null && !csptsubmissionStartTime.getValue().isEmpty()) {
    			submissionStartTimePart = csptsubmissionStartTime.getValue() + ":00";
    		}
    	}
    	//find submission start time
    	String submissionStartTime = submissionStartDatePart + " " + submissionStartTimePart;
    	return FormaterUtil.formatStringToDate(submissionStartTime, ApplicationConstants.SERVER_DATETIMEFORMAT);
    }
    
    public static Date findSubmissionEndTime(final Session motionSession, Date ruleSuspensionDate) {
    	//find submission end date part
    	String strRulesSuspensionDate = FormaterUtil.formatDateToString(ruleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT);
    	String submissionEndDatePart = strRulesSuspensionDate;
    	//find submission end time part
    	String submissionEndTimePart = "00:00:00";
    	if(motionSession!=null) {
    		String submissionEndTimeParameter = motionSession.getParameter(ApplicationConstants.RULESSUSPENSION_MOTION+"_submissionEndTime_"+submissionEndDatePart);
    		if(submissionEndTimeParameter!=null && !submissionEndTimeParameter.isEmpty()) {
    			submissionEndTimePart = submissionEndTimeParameter + ":00";
    		} else {
    			String submissionEndTimeDefaultSessionParameter = motionSession.getParameter(ApplicationConstants.RULESSUSPENSION_MOTION+"_submissionEndTime");
        		if(submissionEndTimeDefaultSessionParameter!=null && !submissionEndTimeDefaultSessionParameter.isEmpty()) {
        			String[] submissionEndTimeDefaultSessionParameters =  submissionEndTimeDefaultSessionParameter.split(" ");
        			submissionEndTimePart = submissionEndTimeDefaultSessionParameters[1];
        		} else {
        			CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.RULESSUSPENSION_MOTION.toUpperCase()+"_SUBMISSIONENDTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
            		if(csptsubmissionEndTime!=null && csptsubmissionEndTime.getValue()!=null && !csptsubmissionEndTime.getValue().isEmpty()) {
            			submissionEndTimePart = csptsubmissionEndTime.getValue() + ":00";
            		}
        		}
    		}
    	} else {
    		CustomParameter csptsubmissionEndTime = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.RULESSUSPENSION_MOTION.toUpperCase()+"_SUBMISSIONENDTIME_"+motionSession.getHouse().getType().getType().toUpperCase(), "");
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
		List<ClubbedEntity> clubbedEntities = RulesSuspensionMotion.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getRulesSuspensionMotion().getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED)
						|| ce.getRulesSuspensionMotion().getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION)) {
					member = ce.getRulesSuspensionMotion().getPrimaryMember();
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
					List<SupportingMember> clubbedSupportingMembers = ce.getRulesSuspensionMotion().getSupportingMembers();
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
			Status submitStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT, this.getLocale());
			if(this.getStatus().getPriority()>=submitStatus.getPriority()) {
				memberStatus = submitStatus;
			} else {
				memberStatus = this.getStatus();
			}
		}		
		return memberStatus;
	}
    
    public void startWorkflow(final RulesSuspensionMotion rulesSuspensionMotion, 
    							final Status status, 
    							final UserGroupType userGroupType, 
    							final Integer level, 
    							final String workflowHouseType, 
    							final Boolean isFlowOnRecomStatusAfterFinalDecision, 
    							final String locale) throws ELSException {
    	//end current workflow if exists
    	rulesSuspensionMotion.endWorkflow(rulesSuspensionMotion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
    	rulesSuspensionMotion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(rulesSuspensionMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
	
	public void endWorkflow(final RulesSuspensionMotion rulesSuspensionMotion, final String workflowHouseType, final String locale) throws ELSException {
    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(rulesSuspensionMotion);
		if(wfDetails != null && wfDetails.getId() != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch(Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				rulesSuspensionMotion.removeExistingWorkflowAttributes();
			}
		} else {
			rulesSuspensionMotion.removeExistingWorkflowAttributes();
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
    /**** Getters and Setters ****/

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

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}

	

	public Date getRuleSuspensionDate() {
		return ruleSuspensionDate;
	}

	public void setRuleSuspensionDate(Date ruleSuspensionDate) {
		this.ruleSuspensionDate = ruleSuspensionDate;
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

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getRevisedNoticeContent() {
		return revisedNoticeContent;
	}

	public void setRevisedNoticeContent(String revisedNoticeContent) {
		this.revisedNoticeContent = revisedNoticeContent;
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

	public RulesSuspensionMotion getParent() {
		return parent;
	}

	public void setParent(RulesSuspensionMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
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

	public List<RulesSuspensionMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<RulesSuspensionMotionDraft> drafts) {
		this.drafts = drafts;
	}

	public List<Ministry> getMinistries() {
		return ministries;
	}

	public void setMinistries(List<Ministry> ministries) {
		this.ministries = ministries;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<SubDepartment> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(List<SubDepartment> subDepartments) {
		this.subDepartments = subDepartments;
	}
	
	public String getAuthorityDraft() {
		return authorityDraft;
	}

	public void setAuthorityDraft(String authorityDraft) {
		this.authorityDraft = authorityDraft;
	}

	public String getFormattedRuleSuspensionDate() {
		if(this.ruleSuspensionDate!=null) {
			try {
				formattedRuleSuspensionDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(this.ruleSuspensionDate, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", this.getLocale());
			} catch (ELSException e) {
				formattedRuleSuspensionDate = "";
			}
		} else {
			formattedRuleSuspensionDate = "";
		}
		return formattedRuleSuspensionDate;
	}

	public void setFormattedRuleSuspensionDate(String formattedRuleSuspensionDate) {
		this.formattedRuleSuspensionDate = formattedRuleSuspensionDate;
	}

	/****number atomic value ****/
	public static void updateCurrentNumberLowerHouse(Integer num){
		synchronized (RulesSuspensionMotion.CUR_NUM_LOWER_HOUSE) {
			RulesSuspensionMotion.CUR_NUM_LOWER_HOUSE = num;
		}
	}

	public static synchronized Integer getCurrentNumberLowerHouse(){
		return RulesSuspensionMotion.CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateCurrentNumberUpperHouse(Integer num){
		synchronized (RulesSuspensionMotion.CUR_NUM_UPPER_HOUSE) {
			RulesSuspensionMotion.CUR_NUM_UPPER_HOUSE = num;
		}
	}

	public static synchronized Integer getCurrentNumberUpperHouse(){
		return RulesSuspensionMotion.CUR_NUM_UPPER_HOUSE;
	}
	
	public static void updateCurrentRulesSuspensionDateLowerHouse(Date ruleSuspensionDate){
		synchronized (RulesSuspensionMotion.CUR_RULE_SUSPENSION_DATE_LOWER_HOUSE) {
			RulesSuspensionMotion.CUR_RULE_SUSPENSION_DATE_LOWER_HOUSE = ruleSuspensionDate;
		}
	}

	public static synchronized Date getCurrentRulesSuspensionDateLowerHouse(){
		return RulesSuspensionMotion.CUR_RULE_SUSPENSION_DATE_LOWER_HOUSE;
	}
	
	public static void updateCurrentRulesSuspensionDateUpperHouse(Date ruleSuspensionDate){
		synchronized (RulesSuspensionMotion.CUR_RULE_SUSPENSION_DATE_UPPER_HOUSE) {
			RulesSuspensionMotion.CUR_RULE_SUSPENSION_DATE_UPPER_HOUSE = ruleSuspensionDate;
		}
	}

	public static synchronized Date getCurrentRulesSuspensionDateUpperHouse(){
		return RulesSuspensionMotion.CUR_RULE_SUSPENSION_DATE_UPPER_HOUSE;
	}

	public static boolean unclub(RulesSuspensionMotion domain, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(domain.getParent()==null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(domain.getParent(), domain, locale);
		return clubbingStatus;
	}

	private static boolean actualUnclubbing(RulesSuspensionMotion parent, RulesSuspensionMotion child,
			String locale) throws ELSException {
		/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_UNCLUBBING, locale);
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
				if(! i.getRulesSuspensionMotion().getId().equals(child.getId())){
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
			if(child.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status putupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, locale);
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

	public static void updateClubbing(RulesSuspensionMotion domain) throws ELSException {
		//case 1: motion is child
		if(domain.getParent()!=null) {
			RulesSuspensionMotion.updateClubbingForChild(domain);
		} 
		//case 2: motion is parent
		else if(domain.getParent()==null && domain.getClubbedEntities()!=null && !domain.getClubbedEntities().isEmpty()) {
			RulesSuspensionMotion.updateClubbingForParent(domain);
		}
	}

	private static void updateClubbingForParent(RulesSuspensionMotion domain) {
		for(ClubbedEntity ce: domain.getClubbedEntities()) {
			RulesSuspensionMotion clubbedMotion = ce.getRulesSuspensionMotion();
			if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED)) {
				updateDomainFieldsOnClubbingFinalisation(domain, clubbedMotion);
				clubbedMotion.setStatus(domain.getInternalStatus());
				clubbedMotion.setInternalStatus(domain.getInternalStatus());
				clubbedMotion.setRecommendationStatus(domain.getInternalStatus());
				
				clubbedMotion.merge();
			}
		}
	}

	private static void updateClubbingForChild(RulesSuspensionMotion domain) throws ELSException {
		String locale = domain.getLocale();
		RulesSuspensionMotion parentMotion = domain.getParent();
		
		Status putupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED, domain.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, domain.getLocale());
	
		if(parentMotion.getNumber().compareTo(domain.getNumber())<0) {
			updateDomainFieldsOnClubbingFinalisation(parentMotion, domain);
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED, domain.getLocale());
				domain.setInternalStatus(clubbedStatus);
				domain.setRecommendationStatus(clubbedStatus);
			} else {
				domain.setStatus(parentMotion.getInternalStatus());
				domain.setInternalStatus(parentMotion.getInternalStatus());
				domain.setRecommendationStatus(parentMotion.getInternalStatus());				
			}				
			
			domain.simpleMerge();
			
		} else if(parentMotion.getNumber().compareTo(domain.getNumber())>0) {				
			
			WorkflowDetails parentMotion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
			if(parentMotion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentMotion_workflowDetails);
				parentMotion.removeExistingWorkflowAttributes();
			}
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				domain.setInternalStatus(putupStatus);
				domain.setRecommendationStatus(putupStatus);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED, domain.getLocale());
				actualClubbingWithApprovalWorkflow(domain, parentMotion, clubbedStatus, clubbedStatus, locale);
			} else {
				domain.setStatus(parentMotion.getInternalStatus());
				domain.setInternalStatus(parentMotion.getInternalStatus());
				if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, domain.getLocale());
					domain.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(domain, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					domain.setRecommendationStatus(parentMotion.getInternalStatus());					
				}					
				if(parentMotion.getReply()!=null && (domain.getReply()==null || domain.getReply().isEmpty())) {
					domain.setReply(parentMotion.getReply());
				}
				if(parentMotion.getRejectionReason()!=null && (domain.getRejectionReason()==null || domain.getRejectionReason().isEmpty())) {
					domain.setRejectionReason(parentMotion.getRejectionReason());
				}
				updateDomainFieldsOnClubbingFinalisation(domain, parentMotion);
				actualClubbingWithApprovalWorkflow(domain, parentMotion, parentMotion.getInternalStatus(), parentMotion.getInternalStatus(), locale);
			}
		}
		
	}

	private static void updateDomainFieldsOnClubbingFinalisation(RulesSuspensionMotion parent,
			RulesSuspensionMotion child) {
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
		if(parent.getMinistries()!=null && child.getMinistries()==null) {
			child.setMinistries(parent.getMinistries());
		} else if(child.getMinistries()!=null && parent.getMinistries()==null) {
			parent.setMinistries(child.getMinistries());
		}
		/** update sub departments in sync **/
		if(parent.getSubDepartments()!=null && child.getSubDepartments()==null) {
			child.setSubDepartments(parent.getSubDepartments());
		} else if(child.getSubDepartments()!=null && parent.getSubDepartments()==null) {
			parent.setSubDepartments(child.getSubDepartments());
		}
		
	}

	private static void actualClubbingWithApprovalWorkflow(RulesSuspensionMotion parent,
			RulesSuspensionMotion child, Status newInternalStatus, Status newRecommendationStatus, String locale) {
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
				RulesSuspensionMotion clubbedMotion = i.getRulesSuspensionMotion();
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
				RulesSuspensionMotion clubbedMotion = i.getRulesSuspensionMotion();
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
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setRulesSuspensionMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);
		
		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				RulesSuspensionMotion rulesSuspensionMotion = k.getRulesSuspensionMotion();
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForMotion = "";
				if(rulesSuspensionMotion.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING)
						|| rulesSuspensionMotion.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(rulesSuspensionMotion.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_NAMECLUBBING)
						|| rulesSuspensionMotion.getInternalStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(rulesSuspensionMotion.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| rulesSuspensionMotion.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(rulesSuspensionMotion, pendingWorkflowTypeForMotion);
					WorkflowDetails.endProcess(wfDetails);
					rulesSuspensionMotion.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION , locale);
					if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING , locale);
						rulesSuspensionMotion.setInternalStatus(putupForClubbingStatus);
						rulesSuspensionMotion.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(rulesSuspensionMotion.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_NAMECLUBBING , locale);
							rulesSuspensionMotion.setInternalStatus(putupForNameClubbingStatus);
							rulesSuspensionMotion.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
							rulesSuspensionMotion.setInternalStatus(putupForClubbingPostAdmissionStatus);
							rulesSuspensionMotion.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				rulesSuspensionMotion.setEditedAs(child.getEditedAs());
				rulesSuspensionMotion.setEditedBy(child.getEditedBy());
				rulesSuspensionMotion.setEditedOn(child.getEditedOn());
				rulesSuspensionMotion.setParent(parent);
				rulesSuspensionMotion.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.RULESSUSPENSIONMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
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

	public static boolean club(RulesSuspensionMotion primaryMotion, RulesSuspensionMotion clubbingMotion,
			String locale) throws ELSException {
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.RULES_SUSPENSION_MOTION_CLUBBING_MODE, "");
    	if(csptClubbingMode!=null && csptClubbingMode.getValue()!=null && csptClubbingMode.getValue().equalsIgnoreCase(ApplicationConstants.CLUBBING_MODE_APPROVAL_WORKFLOW)) {
    		//clubbing with approval workflow
    		return clubWithApprovalWorkflow(primaryMotion, clubbingMotion, locale);
    	} else {
    		//normal clubbing
    		return clubWithoutApprovalWorkflow(primaryMotion, clubbingMotion, locale);
    	}  
	}

	private static boolean clubWithoutApprovalWorkflow(RulesSuspensionMotion m1,
			RulesSuspensionMotion m2, String locale) throws ELSException {
		boolean clubbingStatus = false;
    	try {
    		if(m1.getParent()!=null || m2.getParent()!=null) {
    			throw new ELSException("error", "RULESSUSPENSIONMOTION_ALREADY_CLUBBED");			
    		} else {
    			clubbingStatus = clubbingRulesWithoutApprovalWorkflow(m1, m2, locale);
    			if(clubbingStatus) {
    				Status approvalStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, locale);
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
							Status submitStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT, locale);
							Status clubbedStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED, locale);
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
							Status submitStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SUBMIT, locale);
    						Status clubbedStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED, locale);
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

	private static void actualClubbingWithoutApprovalWorkflow(RulesSuspensionMotion parent, RulesSuspensionMotion child,
			Status newStatus, Status newInternalStatus, Status newRecommendationStatus, String locale) throws ELSException {
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
				RulesSuspensionMotion clubbedMotion = i.getRulesSuspensionMotion();
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
				RulesSuspensionMotion clubbedMotion = i.getRulesSuspensionMotion();
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
		Status approvalStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, locale);
		updateDomainFieldsOnClubbingFinalisation(parent, child);
		child.merge();

		ClubbedEntity clubbedEntity=new ClubbedEntity();
		clubbedEntity.setDeviceType(child.getType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setRulesSuspensionMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);
		
		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				RulesSuspensionMotion rulesSuspensionMotion = k.getRulesSuspensionMotion();
				WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(rulesSuspensionMotion);
				if(wd != null){
					WorkflowDetails.endProcess(wd);
					rulesSuspensionMotion.removeExistingWorkflowAttributes();
				}
				rulesSuspensionMotion.setStatus(newStatus);
				rulesSuspensionMotion.setInternalStatus(newInternalStatus);
				rulesSuspensionMotion.setRecommendationStatus(newRecommendationStatus);
				updateDomainFieldsOnClubbingFinalisation(parent, rulesSuspensionMotion);
				rulesSuspensionMotion.setEditedAs(child.getEditedAs());
				rulesSuspensionMotion.setEditedBy(child.getEditedBy());
				rulesSuspensionMotion.setEditedOn(child.getEditedOn());
				rulesSuspensionMotion.setParent(parent);
				rulesSuspensionMotion.merge();
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

	private static boolean clubbingRulesWithoutApprovalWorkflow(RulesSuspensionMotion m1, RulesSuspensionMotion m2,
			String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(m1, m2, locale);
    	return clubbingStatus;
	}

	private static boolean clubWithApprovalWorkflow(RulesSuspensionMotion m1,
			RulesSuspensionMotion m2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	try {  
    		if(m1.getParent()!=null || m2.getParent()!=null) {
    			throw new ELSException("error", "RULESSUSPENSIONMOTION_ALREADY_CLUBBED");    			
    		} else {
    			clubbingStatus = clubbingRulesWithApprovalWorkflow(m1, m2, locale);
    			if(clubbingStatus) {
    				Status putupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
    				Status approvalStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_FINAL_ADMISSION, locale);
    				
    				//Case 1: Both motions are just ready to be put up
    				if(m1.getInternalStatus().equals(putupStatus) && m2.getInternalStatus().equals(putupStatus)) {
    					Status clubbedStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_SYSTEM_CLUBBED, locale);
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
	    	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING, locale);
	    	    		actualClubbingWithApprovalWorkflow(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    	    		return true;
	    	    	}
    				//Case 2B: One motion is pending in approval workflow while other is ready to be put up
    		    	else if(m1.getInternalStatus().equals(putupStatus)
    	    				&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING, locale);
	    	    		actualClubbingWithApprovalWorkflow(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    	    		return true;
	    	    	}
    				//Case 3: Both motions are pending in approval workflow
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING, locale);
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
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_NAMECLUBBING, locale);
    		    		actualClubbingWithApprovalWorkflow(m1, m2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 4B: One motion is admitted while other motion is ready to be put up (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(putupStatus) && m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_NAMECLUBBING, locale);
    		    		actualClubbingWithApprovalWorkflow(m2, m1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 5A: One motion is admitted while other question is pending in approval workflow (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(approvalStatus)
    						&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    						&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_NAMECLUBBING, locale);
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
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_NAMECLUBBING, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails.endProcess(m1_workflowDetails);
    		    		m1.removeExistingWorkflowAttributes();
    		    		actualClubbingWithApprovalWorkflow(m2, m1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 6: Both motions are admitted
    		    	else if(m1.getInternalStatus().equals(approvalStatus) && m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.RULESSUSPENSIONMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
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

	private static boolean clubbingRulesWithApprovalWorkflow(RulesSuspensionMotion m1, RulesSuspensionMotion m2,
			String locale) throws ELSException {
		boolean clubbingStatus = clubbingRulesCommon(m1, m2, locale);
    	if(clubbingStatus) {
    		if(m1.getReply()!=null && !m1.getReply().isEmpty()) {
    			WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    			if(m1_workflowDetails!=null && m1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "RULESSUSPENSIONMOTION_REPLIED_BUT_FLOW_PENDING");
    			}
    		} else if(m2.getReply()!=null && !m2.getReply().isEmpty()) {
    			WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    			if(m2_workflowDetails!=null && m2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
    				throw new ELSException("error", "RULESSUSPENSIONMOTION_REPLIED_BUT_FLOW_PENDING");
    			}
    		}
    	}
    	return clubbingStatus;
	}

	private static boolean clubbingRulesCommon(RulesSuspensionMotion m1, RulesSuspensionMotion m2, String locale) throws ELSException {
		if(!m1.getSession().equals(m2.getSession())) {
    		//different sessions not allowed
    		throw new ELSException("error", "RULESSUSPENSIONMOTIONS_FROM_DIFFERENT_SESSIONS");	
    		
    	} else if(!m1.getRuleSuspensionDate().equals(m2.getRuleSuspensionDate())) {
    		//different adjourning dates not allowed
    		throw new ELSException("error", "RULESSUSPENSIONMOTIONS_FROM_DIFFERENT_RULE_SUSPENSION_DATES");
    		
    	}  else {
			//clubbing rules succeeded
    		return true;
		}
	}
	
	public static void supportingMemberWorkflowDeletion(final RulesSuspensionMotion rulesSuspensionMotion) {
    	if(rulesSuspensionMotion!=null && rulesSuspensionMotion.getId()>0) {
    		if(anySupportingMembersWorkflows(rulesSuspensionMotion)) {
    			deleteSupportingMembersWorkflows(rulesSuspensionMotion);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final RulesSuspensionMotion rulesSuspensionMotion) {
		List<SupportingMember> supportingMembers = rulesSuspensionMotion.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final RulesSuspensionMotion rulesSuspensionMotion) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(rulesSuspensionMotion!=null && rulesSuspensionMotion.getId()>0 && rulesSuspensionMotion.getSupportingMembers()!=null 
				&& rulesSuspensionMotion.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = rulesSuspensionMotion.getSupportingMembers();
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
