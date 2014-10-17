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
	
}
