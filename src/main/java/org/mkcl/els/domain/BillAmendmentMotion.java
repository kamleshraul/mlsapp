package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import javax.persistence.OptimisticLockException;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.repository.BillAmendmentMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(name="billamendmentmotions")
@JsonIgnoreProperties(value={"houseType", "session", "type", "supportingMembers",
		"sectionAmendments", "revisedSectionAmendments", "recommendationStatus", 
		"parent", "clubbedEntities", "referencedEntities", "drafts", "amendedBill"})
public class BillAmendmentMotion extends Device implements Serializable {
	
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
	
	/** The primary member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="billamendmentmotions_supportingmembers",
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
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
	
	/** The amended bill. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="amendedbill_id")
	private Bill amendedBill;
	
	@Transient
	private String amendedBillInfo;
	
	@Transient
	private String amendedBillLanguages;
	
	/** The amendment. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billamendmentmotions_sectionamendments",
    joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="sectionamendment_id", referencedColumnName="id")})
    private List<SectionAmendment> sectionAmendments;
    
    /** The revised amendment. */
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name="billamendmentmotions_revisedsectionamendments",
    joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="revised_sectionamendment_id", referencedColumnName="id")})
    private List<SectionAmendment> revisedSectionAmendments;
    
    @Transient
    private String defaultAmendedSectionNumberInfo;
    
    /** The date of opinion sought from law and judiciary department. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfOpinionSoughtFromLawAndJD;
    
    /** The opinion sought from law and judiciary department. */
    @Column(length=30000)
    private String opinionSoughtFromLawAndJD;
    
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
	
	/** The remarks for translation. */
    @Column(length=30000)
    private String remarksForTranslation;
	
	/** The parent. */
	@ManyToOne(fetch=FetchType.LAZY)
	private BillAmendmentMotion parent;

	/** The clubbed entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="billamendmentmotions_clubbingentities", 
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="clubbed_entity_id", referencedColumnName="id")})
	private List<ClubbedEntity> clubbedEntities;

	/** The referenced entities. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@JoinTable(name="billamendmentmotions_referencedentities", 
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")}, 
	inverseJoinColumns={@JoinColumn(name="referenced_entity_id", referencedColumnName="id")})
	private List<ReferencedEntity> referencedEntities;
	
	/**** To be used in case of bulk submission and workflows****/
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

	private Integer file;

	private Integer fileIndex;

	private Boolean fileSent;
	
	/** The drafts. */
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="billamendmentmotions_drafts_association", 
	joinColumns={@JoinColumn(name="billamendmentmotion_id", referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="billamendmentmotion_draft_id", referencedColumnName="id")})
	private List<BillAmendmentMotionDraft> drafts;
	
	/** The bill amendment motion repository. */
	@Autowired
	private transient BillAmendmentMotionRepository billAmendmentMotionRepository;

	/**** Constructors ****/
	public BillAmendmentMotion(HouseType houseType, Session session,
			DeviceType type, Integer number, Member primaryMember,
			List<SupportingMember> supportingMembers, Date submissionDate,
			Date creationDate, String createdBy, String dataEnteredBy,
			Date editedOn, String editedBy, String editedAs, Bill amendedBill,
			List<SectionAmendment> sectionAmendments, Status status,
			Status internalStatus, Status recommendationStatus, String remarks, String remarksForTranslation,
			BillAmendmentMotion parent, List<ClubbedEntity> clubbedEntities,
			List<ReferencedEntity> referencedEntities,
			List<BillAmendmentMotionDraft> drafts) {
		super();
		this.houseType = houseType;
		this.session = session;
		this.type = type;
		this.number = number;
		this.primaryMember = primaryMember;
		this.supportingMembers = supportingMembers;
		this.submissionDate = submissionDate;
		this.creationDate = creationDate;
		this.createdBy = createdBy;
		this.dataEnteredBy = dataEnteredBy;
		this.editedOn = editedOn;
		this.editedBy = editedBy;
		this.editedAs = editedAs;
		this.amendedBill = amendedBill;
		this.sectionAmendments = sectionAmendments;
		this.status = status;
		this.internalStatus = internalStatus;
		this.recommendationStatus = recommendationStatus;
		this.remarks = remarks;
		this.remarksForTranslation = remarksForTranslation;
		this.parent = parent;
		this.clubbedEntities = clubbedEntities;
		this.referencedEntities = referencedEntities;
		this.drafts = drafts;
	}

	public BillAmendmentMotion() {
		super();		
	}
	
	/**** Domain Methods ****/
	private static BillAmendmentMotionRepository getBillAmendmentMotionRepository() {
		BillAmendmentMotionRepository billAmendmentMotionRepository = new BillAmendmentMotion().billAmendmentMotionRepository;
		if (billAmendmentMotionRepository == null) {
			throw new IllegalStateException(
			"BillAmendmentMotionRepository has not been injected in BillAmendmentMotion Domain");
		}
		return billAmendmentMotionRepository;
	}
	
	@Override
    public BillAmendmentMotion persist() {
    	if(this.getStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT)) {
    		if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = BillAmendmentMotion.assignBillAmendmentMotionNo(this.getAmendedBill());
					this.setNumber(number + 1);
				}
			}
    		addBillAmendmentMotionDraft();
    	}
    	return (BillAmendmentMotion)super.persist();
    }	

	@Override
    public BillAmendmentMotion merge() {
		BillAmendmentMotion billAmendmentMotion = null;
    	if(this.getStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_SUBMIT)) {
    		if(this.getNumber() == null) {
				synchronized (this) {
					Integer number = BillAmendmentMotion.assignBillAmendmentMotionNo(this.getAmendedBill());
					this.setNumber(number + 1);
				}
			}
    		addBillAmendmentMotionDraft();
    		billAmendmentMotion = (BillAmendmentMotion)super.merge();
    	} else if(this.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
    		if(this.getClubbedEntities() == null || this.getReferencedEntities()==null) {
				BillAmendmentMotion oldBillAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, this.getId());   
				if(this.getClubbedEntities() == null) {
					this.setClubbedEntities(oldBillAmendmentMotion.getClubbedEntities());
				}
				if(this.getReferencedEntities()==null) {
					this.setReferencedEntities(oldBillAmendmentMotion.getReferencedEntities());
				}    				
			}    		
    		addBillAmendmentMotionDraft();
    		billAmendmentMotion = (BillAmendmentMotion)super.merge();
    	}
    	if(billAmendmentMotion != null) {
    		return billAmendmentMotion;
    	} else {
    		if(this.getInternalStatus().getType().equals(ApplicationConstants.BILL_INCOMPLETE) 
                	|| 
                	this.getInternalStatus().getType().equals(ApplicationConstants.BILL_COMPLETE)) {
                    return (BillAmendmentMotion) super.merge();
                }
                else {
                	if(this.getClubbedEntities() == null || this.getReferencedEntities()==null) {
                		BillAmendmentMotion oldBillAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, this.getId());   
        				if(this.getClubbedEntities() == null) {
        					this.setClubbedEntities(oldBillAmendmentMotion.getClubbedEntities());
        				}
        				if(this.getReferencedEntities()==null) {
        					this.setReferencedEntities(oldBillAmendmentMotion.getReferencedEntities());
        				}    				
        			}
                	addBillAmendmentMotionDraft();
                	return (BillAmendmentMotion)super.merge();
                }
    	}    	
    }
	
	/**
     * The merge function, besides updating BillAmendmentMotion, performs various actions
     * based on BillAmendmentMotion's status. What if we need just the simple functionality
     * of updation? Use this method.
     *
     * @return the billAmendmentMotion
     */
	public BillAmendmentMotion simpleMerge() {
		BillAmendmentMotion m = (BillAmendmentMotion) super.merge();
		return m;
	}	
	
	public static Boolean isDuplicateNumberExist(final BillAmendmentMotion billAmendmentMotion) {
    	return getBillAmendmentMotionRepository().isDuplicateNumberExist(billAmendmentMotion);
    }
	
	private static Integer assignBillAmendmentMotionNo(Bill amendedBill) {	
		return getBillAmendmentMotionRepository().assignBillAmendmentMotionNo(amendedBill);
	}
	
	public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}
	
	/**
     * Adds the billamendmentmotion draft.
     */
    private void addBillAmendmentMotionDraft() {
        if(! this.getStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_INCOMPLETE) 
        		&& ! this.getStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_COMPLETE)) {
            BillAmendmentMotionDraft draft = new BillAmendmentMotionDraft();
            draft.setRemarks(this.getRemarks());           
            
            draft.setEditedAs(this.getEditedAs());
            draft.setEditedBy(this.getEditedBy());
            draft.setEditedOn(this.getEditedOn());
            
            draft.setStatus(this.getStatus());
            draft.setInternalStatus(this.getInternalStatus());
            draft.setRecommendationStatus(this.getRecommendationStatus());  
            
            if(this.getRevisedSectionAmendments()!= null && !this.getRevisedSectionAmendments().isEmpty()) {            	
            	draft.setSectionAmendments(this.addRevisedSectionAmendmentsForDraft(this.getRevisedSectionAmendments()));
            } else {
            	draft.setSectionAmendments(this.addSectionAmendmentsForDraft(this.getSectionAmendments()));
            }
            
            draft.setParent(this.getParent());
            draft.setClubbedEntities(this.getClubbedEntities());
            
            if(this.getId() != null) {
                BillAmendmentMotion billAmendmentMotion = BillAmendmentMotion.findById(BillAmendmentMotion.class, this.getId());
                List<BillAmendmentMotionDraft> originalDrafts = billAmendmentMotion.getDrafts();
                if(originalDrafts != null){
                    originalDrafts.add(draft);
                }
                else{
                    originalDrafts = new ArrayList<BillAmendmentMotionDraft>();
                    originalDrafts.add(draft);
                }
                this.setDrafts(originalDrafts);
            }
            else {
                List<BillAmendmentMotionDraft> originalDrafts = new ArrayList<BillAmendmentMotionDraft>();
                originalDrafts.add(draft);
                this.setDrafts(originalDrafts);
            }
        }
    }

	private List<SectionAmendment> addSectionAmendmentsForDraft(List<SectionAmendment> sectionAmendments) {
		if(sectionAmendments!=null) {
    		List<SectionAmendment> sectionAmendmentsForDraft = new ArrayList<SectionAmendment>();
        	for(SectionAmendment sectionAmendment : sectionAmendments) {
        		SectionAmendment sectionAmendmentForDraft = new SectionAmendment();
        		sectionAmendmentForDraft.setLanguage(sectionAmendment.getLanguage());
        		sectionAmendmentForDraft.setSectionNumber(sectionAmendment.getSectionNumber());
        		sectionAmendmentForDraft.setAmendedSection(sectionAmendment.getAmendedSection());
        		sectionAmendmentForDraft.setAmendingContent(sectionAmendment.getAmendingContent());        		
        		sectionAmendmentForDraft.setLocale(sectionAmendment.getLocale());
        		sectionAmendmentsForDraft.add(sectionAmendmentForDraft);
        	}
        	return sectionAmendmentsForDraft;
    	} else {
    		return null;
    	}		
	}
	
	private List<SectionAmendment> addRevisedSectionAmendmentsForDraft(List<SectionAmendment> revisedSectionAmendments) {
		List<SectionAmendment> revisedSectionAmendmentsForDraft = new ArrayList<SectionAmendment>();    	
    	String[] languagesForDraft = this.getAmendedBillLanguages().split("#");
    	for(String languageForDraft : languagesForDraft) {
    		SectionAmendment existingSectionAmendment = null;//findDraftOfGivenTypeInGivenLanguage(revisedTypeOfDraft, languageForDraft);
    		if(revisedSectionAmendments != null) {
        		for(SectionAmendment revisedSectionAmendment: revisedSectionAmendments) {
        			if(revisedSectionAmendment.getLanguage().getType().equals(languageForDraft)) {
        				existingSectionAmendment = revisedSectionAmendment;
        				break;
        			}
        		}
        	}
    		if(existingSectionAmendment==null) {    			
    			List<SectionAmendment> sectionAmendments = this.getSectionAmendments();
    			if(sectionAmendments != null) {
            		for(SectionAmendment sectionAmendment: sectionAmendments) {
            			if(sectionAmendment.getLanguage().getType().equals(languageForDraft)) {
            				existingSectionAmendment = sectionAmendment;
            				break;
            			}
            		}
            	}
    		}
    		if(existingSectionAmendment!=null) {
    			SectionAmendment revisedSectionAmendmentForDraft = new SectionAmendment();
        		revisedSectionAmendmentForDraft.setLanguage(existingSectionAmendment.getLanguage());
        		revisedSectionAmendmentForDraft.setSectionNumber(existingSectionAmendment.getSectionNumber());
        		revisedSectionAmendmentForDraft.setAmendedSection(existingSectionAmendment.getAmendedSection());
        		revisedSectionAmendmentForDraft.setAmendingContent(existingSectionAmendment.getAmendingContent());
        		revisedSectionAmendmentForDraft.setLocale(existingSectionAmendment.getLocale());
        		revisedSectionAmendmentsForDraft.add(revisedSectionAmendmentForDraft);
    		}
    	}    	
    	return revisedSectionAmendmentsForDraft;		
	}	
	
	public static List<BillAmendmentMotion> findAllReadyForSubmissionByMember(final Session session,
			final Member primaryMember,
			final DeviceType motionType,
			final Integer itemsCount,
			final String locale) throws ELSException{
		return getBillAmendmentMotionRepository().findAllReadyForSubmissionByMember(session, primaryMember, motionType, itemsCount, locale);
	}
	
	public static int findHighestFileNo(final Session session,final DeviceType motionType,final String locale) {
		return getBillAmendmentMotionRepository().findHighestFileNo(session,motionType,locale);
	}
	
	public Status findAuxiliaryWorkflowStatus(String workflowType) throws ELSException {
		if(workflowType==null) {
			ELSException elsException=new ELSException();
			elsException.setParameter("BillAmendmentMotion_findAuxillaryWorkflowStatus", "workflow type is null");
			throw elsException;
		}
		Status auxiliaryWorkflowStatus = null;
		WorkflowDetails workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(this, this.getType(), workflowType);
		if(workflowDetails!=null) {
			
			if(workflowDetails.getCustomStatus()==null) {
				ELSException elsException=new ELSException();
				elsException.setParameter("BillAmendmentMotion_findAuxillaryWorkflowStatus", "custom status not set for workflow.");
				throw elsException;
			} else {
				auxiliaryWorkflowStatus = Status.findByType(workflowDetails.getCustomStatus(), this.getLocale());
				if(auxiliaryWorkflowStatus==null) {
					ELSException elsException=new ELSException();
					elsException.setParameter("BillAmendmentMotion_findAuxillaryWorkflowStatus", "status with type '" + workflowDetails.getCustomStatus() + "' not found");
					throw elsException;
				}
			}
		}				
		return auxiliaryWorkflowStatus;
	}
	
	public SectionAmendment findSectionAmendmentInGivenLanguage(String language) {
		SectionAmendment sectionAmendmentInGivenLanguage = null;
		if(this.getSectionAmendments()!=null) {
			for(SectionAmendment s: this.getSectionAmendments()) {
				if(s.getLanguage().getType().equals(language)) {
					sectionAmendmentInGivenLanguage = s;
				}
			}
		}
		return sectionAmendmentInGivenLanguage;
	}
	
	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final BillAmendmentMotion billAmendmentMotion) {
    	return getBillAmendmentMotionRepository().findClubbedEntitiesByPosition(billAmendmentMotion);
    }
	
	public List<ClubbedEntity> findClubbedEntitiesByMotionNumber(final String sortOrder) {
    	return getBillAmendmentMotionRepository().findClubbedEntitiesByMotionNumber(this, sortOrder);
    }
	
	/**
     * Gets the revisions.
     *
     * @param questionId the bill amendment motion id
     * @param locale the locale
     * @return the revisions
     */
    public static List<RevisionHistoryVO> findRevisions(final Long billAmendmentMotionId, final String locale) {
        return getBillAmendmentMotionRepository().findRevisions(billAmendmentMotionId,locale);
    }
    
    /**** Clubbing ****/
    public static boolean club(final BillAmendmentMotion m1,final BillAmendmentMotion m2,final String locale) throws ELSException{
    	boolean clubbingStatus = false;
    	try {  
    		if(m1.getParent()!=null || m2.getParent()!=null) {
    			throw new ELSException("error", "BILLAMENDMENTMOTION_ALREADY_CLUBBED");    			
    		} else {
    			clubbingStatus = clubbingRules(m1, m2, locale);
    			if(clubbingStatus) {
    				Status putupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
    				Status approvalStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION, locale);
    				
    				//Case 1: Both motions are just ready to be put up
    				if(m1.getInternalStatus().equals(putupStatus) && m2.getInternalStatus().equals(putupStatus)) {
    					Status clubbedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_CLUBBED, locale);
    					if(m1.getNumber().compareTo(m2.getNumber())<0) {
    	    				actualClubbing(m1, m2, clubbedStatus, clubbedStatus, locale);
    	    				return true;
    	    			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	    				actualClubbing(m2, m1, clubbedStatus, clubbedStatus, locale);
    	    				return true;
    	    			} else {
    	    				return false;
    	    			}
    				}
    				//Case 2A: One motion is pending in approval workflow while other is ready to be put up
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    	    				&& m2.getInternalStatus().equals(putupStatus)) {
	    	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING, locale);
	    	    		actualClubbing(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    	    		return true;
	    	    	}
    				//Case 2B: One motion is pending in approval workflow while other is ready to be put up
    		    	else if(m1.getInternalStatus().equals(putupStatus)
    	    				&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING, locale);
	    	    		actualClubbing(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    	    		return true;
	    	    	}
    				//Case 3: Both motions are pending in approval workflow
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    	    				&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    		    		int m1_approvalLevel = Integer.parseInt(m1_workflowDetails.getAssigneeLevel());
    		    		int m2_approvalLevel = Integer.parseInt(m2_workflowDetails.getAssigneeLevel());
    		    		if(m1_approvalLevel==m2_approvalLevel) {
    		    			if(m1.getNumber().compareTo(m2.getNumber())<0) {        
    	        				WorkflowDetails.endProcess(m2_workflowDetails);
    	        				m2.removeExistingWorkflowAttributes();
    	        				actualClubbing(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
    	        				return true;
    	        			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	        				WorkflowDetails.endProcess(m1_workflowDetails);
    	        				m1.removeExistingWorkflowAttributes();
    	        				actualClubbing(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    	        				return true;
    	        			} else {
    	        				return false;
    	        			}
    		    		} else if(m1_approvalLevel>m2_approvalLevel) {
    		    			WorkflowDetails.endProcess(m2_workflowDetails);;
    		    			m2.removeExistingWorkflowAttributes();
    		    			actualClubbing(m1, m2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		    			return true;
    		    		} else if(m1_approvalLevel<m2_approvalLevel) {
    		    			WorkflowDetails.endProcess(m1_workflowDetails);
    		    			m1.removeExistingWorkflowAttributes();
    		    			actualClubbing(m2, m1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
    		    			return true;
    		    		} else {
    		    			return false;
    		    		}
    		    	}
    				//Case 4A: One motion is admitted while other motion is ready to be put up (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(approvalStatus) && m2.getInternalStatus().equals(putupStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		actualClubbing(m1, m2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 4B: One motion is admitted while other motion is ready to be put up (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(putupStatus) && m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		actualClubbing(m2, m1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 5A: One motion is admitted while other question is pending in approval workflow (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().equals(approvalStatus)
    						&& m2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    						&& m2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    		    		WorkflowDetails.endProcess(m2_workflowDetails);
    		    		m2.removeExistingWorkflowAttributes();
    		    		actualClubbing(m1, m2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 5B: One motion is admitted while other question is pending in approval workflow (Nameclubbing Case)
    		    	else if(m1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
    						&& m1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
    						&& m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails.endProcess(m1_workflowDetails);
    		    		m1.removeExistingWorkflowAttributes();
    		    		actualClubbing(m2, m1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
    		    		return true;
    		    	}
    				//Case 6: Both motions are admitted
    		    	else if(m1.getInternalStatus().equals(approvalStatus) && m2.getInternalStatus().equals(approvalStatus)) {
    		    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
    		    		WorkflowDetails m1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m1);
    		    		WorkflowDetails m2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(m2);
    		    		if(m1_workflowDetails==null && m2_workflowDetails==null) {
    		    			if(m1.getNumber().compareTo(m2.getNumber())<0) {        
    	        				actualClubbing(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    	        				return true;
    	        			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	        				actualClubbing(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
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
    	            				actualClubbing(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    	            				return true;
    	            			} else if(m1.getNumber().compareTo(m2.getNumber())>0) {
    	            				WorkflowDetails.endProcess(m1_workflowDetails);
    	            				m1.removeExistingWorkflowAttributes();
    	            				actualClubbing(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    	            				return true;
    	            			} else {
    	            				return false;
    	            			}
    		        		} else if(m1_approvalLevel>m2_approvalLevel) {
    		        			WorkflowDetails.endProcess(m2_workflowDetails);
    		        			m2.removeExistingWorkflowAttributes();
    		        			actualClubbing(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		        			return true;
    		        		} else if(m1_approvalLevel<m2_approvalLevel) {
    		        			WorkflowDetails.endProcess(m1_workflowDetails);
    		        			m1.removeExistingWorkflowAttributes();
    		        			actualClubbing(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
    		        			return true;
    		        		} else {
    		        			return false;
    		        		}
    		    		} else if(m1_workflowDetails==null && m2_workflowDetails!=null) {
    		    			WorkflowDetails.endProcess(m2_workflowDetails);
    		    			m2.removeExistingWorkflowAttributes();
    						actualClubbing(m1, m2, m2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
    						return true;
    		    		} else if(m1_workflowDetails!=null && m2_workflowDetails==null) {
    		    			WorkflowDetails.endProcess(m1_workflowDetails);
    		    			m1.removeExistingWorkflowAttributes();
    		    			actualClubbing(m2, m1, m1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
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
    
    private static boolean clubbingRules(BillAmendmentMotion m1, BillAmendmentMotion m2, String locale) throws ELSException {
    	if(!m1.getSession().equals(m2.getSession())) {
    		throw new ELSException("error", "BILLAMENDMENTMOTIONS_FROM_DIFFERENT_SESSIONS");		
    	} else {
			//clubbing rules succeeded
    		return true;
		}   	
    }
    
    @Transactional(noRollbackFor={OptimisticLockException.class})
    private static void actualClubbing(BillAmendmentMotion parent,BillAmendmentMotion child,
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
				BillAmendmentMotion clubbedMotion = i.getBillAmendmentMotion();
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
				BillAmendmentMotion clubbedMotion = i.getBillAmendmentMotion();
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
		clubbedEntity.setBillAmendmentMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);
		
		if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
			for(ClubbedEntity k:childClubbedEntities){
				BillAmendmentMotion billAmendmentMotion = k.getBillAmendmentMotion();
				/** find current clubbing workflow if pending **/
				String pendingWorkflowTypeForMotion = "";
				if(billAmendmentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_CLUBBING)
						|| billAmendmentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(billAmendmentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_NAMECLUBBING)
						|| billAmendmentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_NAMECLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(billAmendmentMotion.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| billAmendmentMotion.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					/** end current clubbing workflow **/
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(billAmendmentMotion, pendingWorkflowTypeForMotion);
					WorkflowDetails.endProcess(wfDetails);
					billAmendmentMotion.removeExistingWorkflowAttributes();
					/** put up for proper clubbing workflow as per updated parent **/
					Status finalAdmitStatus = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION , locale);
					if(parent.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
						Status putupForClubbingStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING , locale);
						billAmendmentMotion.setInternalStatus(putupForClubbingStatus);
						billAmendmentMotion.setRecommendationStatus(putupForClubbingStatus);
					} else {
						if(billAmendmentMotion.getStatus().getPriority().compareTo(finalAdmitStatus.getPriority())<0) {
							Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_NAMECLUBBING , locale);
							billAmendmentMotion.setInternalStatus(putupForNameClubbingStatus);
							billAmendmentMotion.setRecommendationStatus(putupForNameClubbingStatus);
						} else {
							Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
							billAmendmentMotion.setInternalStatus(putupForClubbingPostAdmissionStatus);
							billAmendmentMotion.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
						}
					}
				}
				billAmendmentMotion.setEditedAs(child.getEditedAs());
				billAmendmentMotion.setEditedBy(child.getEditedBy());
				billAmendmentMotion.setEditedOn(child.getEditedOn());
				billAmendmentMotion.setParent(parent);
				billAmendmentMotion.merge();
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
			if(parent.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
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
    
    /**** Update Clubbing ****/
    public static void updateClubbing(BillAmendmentMotion billAmendmentMotion) throws ELSException {
		//case 1: motion is child
		if(billAmendmentMotion.getParent()!=null) {
			BillAmendmentMotion.updateClubbingForChild(billAmendmentMotion);
		} 
		//case 2: motion is parent
		else if(billAmendmentMotion.getParent()==null && billAmendmentMotion.getClubbedEntities()!=null && !billAmendmentMotion.getClubbedEntities().isEmpty()) {
			BillAmendmentMotion.updateClubbingForParent(billAmendmentMotion);
		}
	}
    
    private static void updateClubbingForParent(BillAmendmentMotion billAmendmentMotion) {
    	for(ClubbedEntity ce: billAmendmentMotion.getClubbedEntities()) {
    		BillAmendmentMotion clubbedMotion = ce.getBillAmendmentMotion();
			if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(billAmendmentMotion, clubbedMotion);
				
				clubbedMotion.setStatus(billAmendmentMotion.getInternalStatus());
				clubbedMotion.setInternalStatus(billAmendmentMotion.getInternalStatus());
				clubbedMotion.setRecommendationStatus(billAmendmentMotion.getInternalStatus());
				
				clubbedMotion.merge();
			}
		}
    }
    
    private static void updateDomainFieldsOnClubbingFinalisation(BillAmendmentMotion parent, BillAmendmentMotion child) {    	
    	
    	/** copy latest section amending content of parent to revised section amending content of child **/
    	List<SectionAmendment> childRevisedSectionAmendments = null;
    	
    	if(child.getRevisedSectionAmendments()!=null && !child.getRevisedSectionAmendments().isEmpty()) {
    		childRevisedSectionAmendments = child.getRevisedSectionAmendments();    		
    	} else {
    		childRevisedSectionAmendments = new ArrayList<SectionAmendment>();		
    	}
    	
    	String languageTypesAllowedForSectionAmendment = child.getAmendedBill().findLanguagesOfContentDrafts();
    	List<Language> languagesAllowedForSectionAmendment = new ArrayList<Language>();
		for(String languageTypeAllowedForSectionAmendment: languageTypesAllowedForSectionAmendment.split("#")) {
			Language languageAllowedForSectionAmendment = Language.findByFieldName(Language.class, "type", languageTypeAllowedForSectionAmendment, child.getLocale());
			languagesAllowedForSectionAmendment.add(languageAllowedForSectionAmendment);
		}
		
		for(Language language: languagesAllowedForSectionAmendment) {			
			SectionAmendment parentSectionAmendmentInGivenLanguage = null;
			boolean isParentSectionAmendmentInGivenLanguageFound = false;
			if(parent.getRevisedSectionAmendments()!=null && !parent.getRevisedSectionAmendments().isEmpty()) {
				for(SectionAmendment parentSA: parent.getRevisedSectionAmendments()) {
					if(parentSA.getLanguage().getType().equals(language.getType())) {
						parentSectionAmendmentInGivenLanguage = parentSA;
						isParentSectionAmendmentInGivenLanguageFound = true;
					}
				}
			} 
			if(!isParentSectionAmendmentInGivenLanguageFound) {
				if(parent.getSectionAmendments()!=null && !parent.getSectionAmendments().isEmpty()) {
					for(SectionAmendment parentSA: parent.getSectionAmendments()) {
						if(parentSA.getLanguage().getType().equals(language.getType())) {
							parentSectionAmendmentInGivenLanguage = parentSA;
						}
					}
				}
			}
			if(parentSectionAmendmentInGivenLanguage!=null) {
				SectionAmendment childSectionAmendmentInGivenLanguage = null;
				if(!childRevisedSectionAmendments.isEmpty()) {
					boolean isChildSectionAmendmentInGivenLanguageFound = false;
					for(SectionAmendment childSA: child.getRevisedSectionAmendments()) {
						if(childSA.getLanguage().getType().equals(parentSectionAmendmentInGivenLanguage.getLanguage().getType())) {
							childSectionAmendmentInGivenLanguage = childSA;
							childSectionAmendmentInGivenLanguage.setAmendingContent(parentSectionAmendmentInGivenLanguage.getAmendingContent());
							isChildSectionAmendmentInGivenLanguageFound = true;
						}
					}
					if(!isChildSectionAmendmentInGivenLanguageFound) {
						childSectionAmendmentInGivenLanguage = SectionAmendment.createCopyOfSectionAmendment(parentSectionAmendmentInGivenLanguage);
					}
				} else {
					childSectionAmendmentInGivenLanguage = SectionAmendment.createCopyOfSectionAmendment(parentSectionAmendmentInGivenLanguage);
				}
				childRevisedSectionAmendments.add(childSectionAmendmentInGivenLanguage);
			}
		}
		child.setRevisedSectionAmendments(childRevisedSectionAmendments);
    }
    
    private static void updateClubbingForChild(BillAmendmentMotion billAmendmentMotion) throws ELSException {
    	String locale = billAmendmentMotion.getLocale();
		BillAmendmentMotion parentMotion = billAmendmentMotion.getParent();
		
		Status putupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, billAmendmentMotion.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION, billAmendmentMotion.getLocale());
	
		if(parentMotion.getNumber().compareTo(billAmendmentMotion.getNumber())<0) {
			updateDomainFieldsOnClubbingFinalisation(parentMotion, billAmendmentMotion);
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_CLUBBED, billAmendmentMotion.getLocale());
				billAmendmentMotion.setInternalStatus(clubbedStatus);
				billAmendmentMotion.setRecommendationStatus(clubbedStatus);
			} else {
				billAmendmentMotion.setStatus(parentMotion.getInternalStatus());
				billAmendmentMotion.setInternalStatus(parentMotion.getInternalStatus());
				billAmendmentMotion.setRecommendationStatus(parentMotion.getInternalStatus());				
			}				
			
			billAmendmentMotion.simpleMerge();
			
		} else if(parentMotion.getNumber().compareTo(billAmendmentMotion.getNumber())>0) {				
			
			WorkflowDetails parentMotion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
			if(parentMotion_workflowDetails!=null) {
				WorkflowDetails.endProcess(parentMotion_workflowDetails);
				parentMotion.removeExistingWorkflowAttributes();
			}
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				billAmendmentMotion.setInternalStatus(putupStatus);
				billAmendmentMotion.setRecommendationStatus(putupStatus);
				
				Status clubbedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_CLUBBED, billAmendmentMotion.getLocale());
				actualClubbing(billAmendmentMotion, parentMotion, clubbedStatus, clubbedStatus, locale);
			} else {
				billAmendmentMotion.setStatus(parentMotion.getInternalStatus());
				billAmendmentMotion.setInternalStatus(parentMotion.getInternalStatus());
				if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION)) {
					Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING, billAmendmentMotion.getLocale());
					billAmendmentMotion.setRecommendationStatus(admitDueToReverseClubbingStatus);
					Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
					WorkflowDetails.startProcess(billAmendmentMotion, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
				} else {
					//TODO:handle case when parent is already rejected.. below is temporary fix
					//clarification from ketkip remaining
					billAmendmentMotion.setRecommendationStatus(parentMotion.getInternalStatus());					
				}					
//				if(parentMotion.getReply()!=null && (billAmendmentMotion.getReply()==null || billAmendmentMotion.getReply().isEmpty())) {
//					billAmendmentMotion.setReply(parentMotion.getReply());
//				}
//				if(parentMotion.getRejectionReason()!=null && (billAmendmentMotion.getRejectionReason()==null || billAmendmentMotion.getRejectionReason().isEmpty())) {
//					billAmendmentMotion.setRejectionReason(parentMotion.getRejectionReason());
//				}
				updateDomainFieldsOnClubbingFinalisation(billAmendmentMotion, parentMotion);
				actualClubbing(billAmendmentMotion, parentMotion, parentMotion.getInternalStatus(), parentMotion.getInternalStatus(), locale);
			}
		}
    }
    
    /**** Unclubbing ****/
    public static boolean unclub(final BillAmendmentMotion m1, final BillAmendmentMotion m2, String locale) throws ELSException {
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
    
    public static boolean unclub(final BillAmendmentMotion billAmendmentMotion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(billAmendmentMotion.getParent()==null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(billAmendmentMotion.getParent(), billAmendmentMotion, locale);
		return clubbingStatus;
	}
    
    public static boolean actualUnclubbing(final BillAmendmentMotion parent, final BillAmendmentMotion child, String locale) throws ELSException {
    	/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
		//TODO: write condition for above case & initiate code to send for unclubbing workflow
		Status approvedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION, locale);		
		boolean isOptimisticLockExceptionPossible = false;
		if(child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_UNCLUBBING)) {
			isOptimisticLockExceptionPossible = true;
		}
		if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				&& !child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_UNCLUBBING)) {
			Status putupUnclubStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PUTUP_UNCLUBBING, locale);
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
				if(! i.getBillAmendmentMotion().getId().equals(child.getId())){
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
			if(child.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_CLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
			} else if(child.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_NAMECLUBBING)
					|| child.getInternalStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_NAMECLUBBING)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
			} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					|| child.getRecommendationStatus().getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
			}
			if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
				WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);	
				WorkflowDetails.endProcess(wfDetails);
				child.removeExistingWorkflowAttributes();
			}
			/** update child status **/
			Status putupStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status admitStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMISSION, locale);
			if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
				child.setInternalStatus(putupStatus);
				child.setRecommendationStatus(putupStatus);
			} else {
//				if(child.getReply()==null || child.getReply().isEmpty()
//						|| child.getReply().equals(parent.getReply())) {
//					child.setInternalStatus(admitStatus);
//					child.setRecommendationStatus(admitStatus);
//					if(child.getReply().equals(parent.getReply())) {
//						child.setReply(null);
//					}
//					Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
//					UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
//					WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
//				} else {
//					child.setInternalStatus(admitStatus);
//					Status answerReceivedStatus = Status.findByType(ApplicationConstants.BILLAMENDMENTMOTION_PROCESSED_REPLY_RECEIVED, locale);
//					child.setRecommendationStatus(answerReceivedStatus);
//				}
				child.setInternalStatus(admitStatus);
				child.setRecommendationStatus(admitStatus);
				Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
				UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
				WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 6, locale);
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

		if(recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_CLUBBING_POST_ADMISSION)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_UNCLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_RECOMMEND_ADMIT_DUE_TO_REVERSE_CLUBBING)
				|| recommendationStatusType.equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_ADMIT_DUE_TO_REVERSE_CLUBBING)) {
			
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

	public Bill getAmendedBill() {
		return amendedBill;
	}

	public void setAmendedBill(Bill amendedBill) {
		this.amendedBill = amendedBill;
	}

	public String getAmendedBillInfo() {
		this.amendedBillInfo = "";
		if(this.amendedBill!=null) {
			Integer billYear = this.amendedBill.findYear();
			if(billYear!=null) {
				this.amendedBillInfo += this.amendedBill.getLocale();
				this.amendedBillInfo += "#";
				this.amendedBillInfo += FormaterUtil.formatNumberNoGrouping(billYear, this.amendedBill.getLocale());		
			} else {
				return this.amendedBillInfo;
			}
			this.amendedBillInfo += "#";
			String numberingHouseType = this.amendedBill.findNumberingHouseType();
			if(numberingHouseType!=null) {
				this.amendedBillInfo += numberingHouseType;
			} else {
				return this.amendedBillInfo;
			}
			this.amendedBillInfo += "#";
			Integer billNumber = this.amendedBill.getNumber();
			if(billNumber!=null) {
				this.amendedBillInfo += FormaterUtil.formatNumberNoGrouping(billNumber, this.amendedBill.getLocale());		
			} else {
				return this.amendedBillInfo;
			}
		}
		return this.amendedBillInfo;
	}

	public String getAmendedBillLanguages() {
//		if(amendedBillLanguages==null) {
//			amendedBillLanguages = amendedBill.findLanguagesOfContentDrafts();
//		}
		return amendedBillLanguages;
	}

	public void setAmendedBillLanguages(String amendedBillLanguages) {
		this.amendedBillLanguages = amendedBillLanguages;
	}

	public List<SectionAmendment> getSectionAmendments() {
		return sectionAmendments;
	}

	public void setSectionAmendments(List<SectionAmendment> sectionAmendments) {
		this.sectionAmendments = sectionAmendments;
	}

	public List<SectionAmendment> getRevisedSectionAmendments() {
		return revisedSectionAmendments;
	}

	public void setRevisedSectionAmendments(
			List<SectionAmendment> revisedSectionAmendments) {
		this.revisedSectionAmendments = revisedSectionAmendments;
	}

	public String getDefaultAmendedSectionNumberInfo() {
		String defaultAmendedSectionNumber = this.findDefaultAmendedSectionNumber();
		if(defaultAmendedSectionNumber!=null) {
			this.defaultAmendedSectionNumberInfo = this.getLocale();
			this.defaultAmendedSectionNumberInfo += "#";
			this.defaultAmendedSectionNumberInfo += defaultAmendedSectionNumber;
		}
		return this.defaultAmendedSectionNumberInfo;
	}

	public String findDefaultAmendedSectionNumber() {		
		String defaultAmendedSectionNumber = "";
		if(this.getSectionAmendments()!=null && !this.getSectionAmendments().isEmpty()) {		
			String defaultAmendedSectionLanguage = this.getSession().getParameter(this.amendedBill.getType().getType()+"_defaultTitleLanguage");
	    	if(defaultAmendedSectionLanguage!=null&&!defaultAmendedSectionLanguage.isEmpty()) {
	    		if(this.getRevisedSectionAmendments()!=null && !this.getRevisedSectionAmendments().isEmpty()) {
	    			for(SectionAmendment sa: this.getRevisedSectionAmendments()) {
            			if(sa.getLanguage().getType().equals(defaultAmendedSectionLanguage)) {
            				defaultAmendedSectionNumber = sa.getSectionNumber();
            				break;
            			}
            		}
        			if(defaultAmendedSectionNumber==null || defaultAmendedSectionNumber.isEmpty()) {
        				if(this.getSectionAmendments()!=null && !this.getSectionAmendments().isEmpty()) {
        					for(SectionAmendment sa: this.getSectionAmendments()) {
    	            			if(sa.getLanguage().getType().equals(defaultAmendedSectionLanguage)) {
    	            				defaultAmendedSectionNumber = sa.getSectionNumber();
    	            				break;
    	            			}
    	            		}
        	        	}
        			}
	        	} else if(this.getSectionAmendments()!=null && !this.getSectionAmendments().isEmpty()) {
	        		for(SectionAmendment sa: this.getSectionAmendments()) {
            			if(sa.getLanguage().getType().equals(defaultAmendedSectionLanguage)) {
            				defaultAmendedSectionNumber = sa.getSectionNumber();
            				break;
            			}
            		}
	        	}
	    	}
		}
		return defaultAmendedSectionNumber;
	}
	
	public String findDefaultSectionAmendmentContent() {
    	String defaultSectionAmendmentContent = "";
    	String defaultTitleLanguage = this.getAmendedBill().getSession().getParameter(this.getAmendedBill().getType().getType()+"_defaultTitleLanguage");
    	if(defaultTitleLanguage!=null&&!defaultTitleLanguage.isEmpty()) {
    		if(this.getRevisedSectionAmendments()!=null) {
        		if(!this.getRevisedSectionAmendments().isEmpty()) {
        			for(SectionAmendment sa: this.getRevisedSectionAmendments()) {
            			if(sa.getLanguage().getType().equals(defaultTitleLanguage)) {
            				defaultSectionAmendmentContent = sa.getAmendingContent();
            				break;
            			}
            		}
        			if(defaultSectionAmendmentContent.isEmpty()) {
        				if(this.getSectionAmendments()!=null) {
        	        		if(!this.getSectionAmendments().isEmpty()) {
        	    				for(SectionAmendment sa: this.getSectionAmendments()) {
        	            			if(sa.getLanguage().getType().equals(defaultTitleLanguage)) {
        	            				defaultSectionAmendmentContent = sa.getAmendingContent();
        	            				break;
        	            			}
        	            		}
        	    			}
        	        	}
        			}
        		} else if(this.getSectionAmendments()!=null) {
        			if(!this.getSectionAmendments().isEmpty()) {
        				for(SectionAmendment sa: this.getSectionAmendments()) {
                			if(sa.getLanguage().getType().equals(defaultTitleLanguage)) {
                				defaultSectionAmendmentContent = sa.getAmendingContent();
                				break;
                			}
                		}
        			}        		
            	}
        	} else if(this.getSectionAmendments()!=null) {
        		if(!this.getSectionAmendments().isEmpty()) {
    				for(SectionAmendment sa: this.getSectionAmendments()) {
            			if(sa.getLanguage().getType().equals(defaultTitleLanguage)) {
            				defaultSectionAmendmentContent = sa.getAmendingContent();
            				break;
            			}
            		}
    			}
        	}
    	}    	    	
    	return defaultSectionAmendmentContent;
    }

	public Date getDateOfOpinionSoughtFromLawAndJD() {
		return dateOfOpinionSoughtFromLawAndJD;
	}

	public void setDateOfOpinionSoughtFromLawAndJD(
			Date dateOfOpinionSoughtFromLawAndJD) {
		this.dateOfOpinionSoughtFromLawAndJD = dateOfOpinionSoughtFromLawAndJD;
	}

	public String getOpinionSoughtFromLawAndJD() {
		return opinionSoughtFromLawAndJD;
	}

	public void setOpinionSoughtFromLawAndJD(String opinionSoughtFromLawAndJD) {
		this.opinionSoughtFromLawAndJD = opinionSoughtFromLawAndJD;
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

	public String getRemarksForTranslation() {
		return remarksForTranslation;
	}

	public void setRemarksForTranslation(String remarksForTranslation) {
		this.remarksForTranslation = remarksForTranslation;
	}

	public BillAmendmentMotion getParent() {
		return parent;
	}

	public void setParent(BillAmendmentMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}

	public List<ReferencedEntity> getReferencedEntities() {
		return referencedEntities;
	}

	public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
		this.referencedEntities = referencedEntities;
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

	public Integer getFile() {
		return file;
	}

	public void setFile(Integer file) {
		this.file = file;
	}

	public Integer getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}

	public Boolean getFileSent() {
		return fileSent;
	}

	public void setFileSent(Boolean fileSent) {
		this.fileSent = fileSent;
	}

	public List<BillAmendmentMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<BillAmendmentMotionDraft> drafts) {
		this.drafts = drafts;
	}
	
	public static void supportingMemberWorkflowDeletion(final BillAmendmentMotion billAmendmentMotion) {
    	if(billAmendmentMotion!=null && billAmendmentMotion.getId()>0) {
    		if(anySupportingMembersWorkflows(billAmendmentMotion)) {
    			deleteSupportingMembersWorkflows(billAmendmentMotion);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final BillAmendmentMotion billAmendmentMotion) {
		List<SupportingMember> supportingMembers = billAmendmentMotion.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final BillAmendmentMotion billAmendmentMotion) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(billAmendmentMotion!=null && billAmendmentMotion.getId()>0 && billAmendmentMotion.getSupportingMembers()!=null 
				&& billAmendmentMotion.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = billAmendmentMotion.getSupportingMembers();
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
