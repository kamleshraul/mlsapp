package org.mkcl.els.domain;

/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.CutMotion.java
 * Created On: Mar 27, 2012
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.repository.CutMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="cutmotions")
@JsonIgnoreProperties({"houseType", "session", "deviceType", "answeringDate",
	"recommendationStatus", "supportingMembers", "department",
	"drafts", "parent", "clubbedEntities","referencedEntities","discussionDate",
	"noticeContent", "secondaryTitle", "subTitle", "creationDate", "yaadiMinistry", "yaadiDepartment", "yaadiSubDepartment"})
public class CutMotion extends Device implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/** houseType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "housetype_id")
	private HouseType houseType;

	/** session **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;

	/** deviceType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;

	/** Number ***/
	private Integer number;
	
	/** Internal Number ***/
	private Integer internalNumber;

	/**amount to be deducted **/
	@Column(precision=20, scale=2)
	private BigDecimal amountToBeDeducted;

	/** Total Amount **/
	@Column(precision=20, scale=2)
	private BigDecimal totalAmoutDemanded;

	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	/**demandNumber **/
	private String demandNumber;

	/** itemNumber **/
	private Integer itemNumber; // ---- for supplementary

	/** pageNumber **/
	private String pageNumber;

	/** The created by. */
	@Column(length = 1000)
	private String createdBy;
	
	/** The dataEnteredBy by. */
	@Column(length = 1000)
	private String dataEnteredBy;

	/** The edited on. */
	@Temporal(TemporalType.TIMESTAMP)
	@JoinColumn(name = "editedon")
	private Date editedOn;

	/** The edited by. */
	@Column(length = 1000)
	private String editedBy;

	/** The edited as. */
	@Column(length = 1000)
	private String editedAs;

	/** The mainTitle */
	@Column(length = 2000)
	private String mainTitle;

	/** The revisedMainTitle */
	@Column(length = 1000)
	private String revisedMainTitle;

	/** The secondaryTitle */
	@Column(length = 1000)
	private String secondaryTitle; // ----------for budgetary only

	/** The revisedSecondaryTitle */
	@Column(length = 1000)
	private String revisedSecondaryTitle; // --------for budgetary only

	/** The subTitle */
	@Column(length = 1000)
	private String subTitle;

	/** The revisedSubTitle */
	@Column(length = 1000)
	private String revisedSubTitle;

	/** The noticeContent */
	@Column(length = 30000)
	private String noticeContent;

	/** The revisedNoticeContent */
	@Column(length = 30000)
	private String revisedNoticeContent;

	/** The rejectionReason */
	@Column(length = 10000)
	private String rejectionReason; //----will come from citation

	/** status **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private Status status;

	/** internalStatus **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "internalstatus_id")
	private Status internalStatus;

	/** recommendationStatus **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recommendationstatus_id")
	private Status recommendationStatus;

	/** The remarks. */
	@Column(length = 30000)
	private String remarks;

	/** primaryMember **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member primaryMember;

	/** The supporting members. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "cutmotions_supportingmembers", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "supportingmember_id", referencedColumnName = "id") })
	private List<SupportingMember> supportingMembers;

	/** The ministry. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ministry_id")
	private Ministry ministry;

	/** The department. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	/** The sub department. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subdepartment_id")
	private SubDepartment subDepartment;
	
	/** The ministry. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "yaadi_ministry_id")
	private Ministry yaadiMinistry;

	/** The department. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "yaadi_department_id")
	private Department yaadiDepartment;

	/** The sub department. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "yaadi_subdepartment_id")
	private SubDepartment yaadiSubDepartment;
	
	/** The yaadi laying date. */
	@Temporal(TemporalType.DATE)
    private Date yaadiLayingDate;

	/**** DRAFTS ****/
	/** The drafts. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "cutmotions_drafts_association", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "cutmotion_draft_id", referencedColumnName = "id") })
	private List<CutMotionDraft> drafts;

	/**** Clubbing ****/
	/** The parent. */
	@ManyToOne(fetch = FetchType.LAZY)
	private CutMotion parent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "cutmotions_clubbingentities", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") },
	inverseJoinColumns = { @JoinColumn(name = "clubbed_entity_id", referencedColumnName = "id") })
	private List<ClubbedEntity> clubbedEntities;
	
	// =============== Referencing ====================//
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "cutmotions_referencedunits", 
	joinColumns = { @JoinColumn(name = "cutmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "referenced_unit_id", referencedColumnName = "id") })
	private List<ReferenceUnit> referencedEntities;

	/** The reply */
	@Column(length = 30000)
	private String reply;

	/** answering date **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date answeringDate;

	@Temporal(TemporalType.DATE)
	private Date discussionDate;
	
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
	
	private String workflowStarted;

	private String actor;

	private String localizedActorName;

	private String endFlag;

	private String level;

	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;

	private boolean bulkSubmitted = false;

	private Long workflowDetailsId;

	private Integer file;

	private Integer fileIndex;

	private Boolean fileSent;
	
	/**** Synch variables for budgetary cutmotion lower house****/
	private transient volatile static Integer CUTMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE = 0;
	
	/**** Synch variables for budgetary cutmotion upper house****/
	private transient volatile static Integer CUTMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE = 0;
	
	/**** Synch variables for supplementary cutmotion lower house****/
	private transient volatile static Integer CUTMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE = 0;
	
	/**** Synch variables for supplementary cutmotion upper house****/
	private transient volatile static Integer CUTMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE = 0;

	@Autowired
	private transient CutMotionRepository cutMotionRepository;
	
	/**** Constructor ****/
	public CutMotion() {
		super();
	}

	/**** Domain Methods ****/

	private static CutMotionRepository getCutMotionRepository() {
		CutMotionRepository cutMotionRepository = new CutMotion().cutMotionRepository;
		if (cutMotionRepository == null) {
			throw new IllegalStateException(
			"CutMotionRepository has not been injected in CutMotion Domain");
		}
		return cutMotionRepository;
	}

	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final CutMotion motion) {
		return getCutMotionRepository().findClubbedEntitiesByPosition(motion);
	}

	@Override
	public CutMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
//				synchronized (this) {
//					Integer number = CutMotion.assignCutMotionNo(this.getHouseType(),
//							this.getSession(), this.getDeviceType(),this.getLocale());
//					this.setNumber(number + 1);
//					addCutMotionDraft();
//					return (CutMotion)super.persist();
//				}
				synchronized (CutMotion.class) {
                	
                	Integer number = null;
					try {
						String houseType = this.getHouseType().getType();
						String cutMotionType = this.getDeviceType().getType();
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {					
							if (CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(number);
								CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {					
							if (CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(number);
								CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(number);
							}
						} else if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {					
							if (CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(number);
								CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {					
							if (CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(number);
								CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {
	            			this.setNumber(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {
	            			this.setNumber(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		addCutMotionDraft();
						return (CutMotion)super.persist();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
			}else if(this.getNumber() != null){
				addCutMotionDraft();
			}
		}
		return (CutMotion) super.persist();
	}
	
	public static Boolean isExist(final Integer number, final DeviceType deviceType, final Session session, final String locale) {
		 return getCutMotionRepository().isExist(number, null, deviceType, session, locale);
	 }

	 public static Boolean isExist(final Integer number, final Long id, final DeviceType deviceType, final Session session, final String locale) {
		 return getCutMotionRepository().isExist(number, id, deviceType, session, locale);
	 }
	
	public static Integer assignCutMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) throws ELSException {
		return getCutMotionRepository().assignCutMotionNo(houseType,session,type,locale);
	}

	private void addCutMotionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.CUTMOTION_COMPLETE)) {
			CutMotionDraft draft = new CutMotionDraft();
			draft.setLocale(this.getLocale());
			if(this.getId()!=null) {
				draft.setDeviceId(this.getId().toString());
			}			
			draft.setRemarks(this.getRemarks());
			draft.setClubbedEntities(this.getClubbedEntities());
			draft.setReferencedEntities(this.getReferencedEntities());
			draft.setEditedAs(this.getEditedAs());
			draft.setEditedBy(this.getEditedBy());
			draft.setEditedOn(this.getEditedOn());	            
			draft.setMinistry(this.getMinistry());
			draft.setDepartment(this.getDepartment());
			draft.setSubDepartment(this.getSubDepartment());	            
			draft.setStatus(this.getStatus());
			draft.setInternalStatus(this.getInternalStatus());
			draft.setRecommendationStatus(this.getRecommendationStatus());
			
			if(this.getRevisedNoticeContent()!= null 
					&& this.getRevisedMainTitle() != null){
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setMainTitle(this.getRevisedMainTitle());
			}else if(this.getRevisedNoticeContent() != null){
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setMainTitle(this.getMainTitle());
			}else if(this.getRevisedMainTitle() != null){
				draft.setNoticeContent(this.getNoticeContent());
				draft.setMainTitle(this.getRevisedMainTitle());
			}else{
				draft.setNoticeContent(this.getNoticeContent());
				draft.setMainTitle(this.getMainTitle());
			}	  
			
			if(this.getDeviceType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)){
				if(this.getRevisedSecondaryTitle() != null){
					draft.setSecondaryTitle(this.getRevisedSecondaryTitle());
				}else{
					draft.setSecondaryTitle(this.getSecondaryTitle());
				}
			}
			
			if(this.getRevisedSubTitle() != null){
				draft.setSubTitle(this.getRevisedSubTitle());
			}else{
				draft.setSubTitle(this.getSubTitle());
			}
			
			draft.setTotalAmoutDemanded(this.getTotalAmoutDemanded());
			draft.setAmountToBeDeducted(this.getAmountToBeDeducted());
			draft.setDemandNumber(this.getDemandNumber());
			draft.setItemNumber(this.getItemNumber());
			draft.setPageNumber(this.getPageNumber());
			
			if(this.getId() != null) {
				CutMotion motion = CutMotion.findById(CutMotion.class, this.getId());
				List<CutMotionDraft> originalDrafts = motion.getDrafts();
				if(originalDrafts != null){
					originalDrafts.add(draft);
				}
				else{
					originalDrafts = new ArrayList<CutMotionDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			}
			else {
				List<CutMotionDraft> originalDrafts = new ArrayList<CutMotionDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}

	@Override
	public CutMotion merge() {
		CutMotion motion = null;
		if(this.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
//				synchronized (this) {
//					Integer number = CutMotion.assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(),this.getLocale());
//					this.setNumber(number + 1);
//					addCutMotionDraft();
//					motion = (CutMotion) super.merge();
//				}
				synchronized (CutMotion.class) {
                	
                	Integer number = null;
					try {
						String houseType = this.getHouseType().getType();
						String cutMotionType = this.getDeviceType().getType();
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {					
							if (CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(number);
								CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {					
							if (CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(number);
								CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(number);
							}
						} else if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {					
							if (CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(number);
								CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {					
							if (CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() == 0) {
								number = CutMotion.
										assignCutMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(number);
								CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {
	            			this.setNumber(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)) {
	            			this.setNumber(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() + 1);
	            			CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() + 1);
	            		}
	            		
						addCutMotionDraft();
						motion = (CutMotion) super.merge();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
			}else {
				CutMotion oldMotion = CutMotion.findById(CutMotion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}
				if(this.getReferencedEntities() == null){
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addCutMotionDraft();
				motion = (CutMotion) super.merge();
			}
		}
		
		if(motion != null) {
			return motion;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_INCOMPLETE) 
					|| this.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_COMPLETE)) {
				return (CutMotion) super.merge();
			}else {
				CutMotion oldMotion = CutMotion.findById(CutMotion.class, this.getId());
				if(this.getClubbedEntities() == null){
					this.setClubbedEntities(oldMotion.getClubbedEntities());
				}	
				if(this.getReferencedEntities() == null){
					this.setReferencedEntities(oldMotion.getReferencedEntities());
				}
				this.addCutMotionDraft();
				return (CutMotion) super.merge();
			}
		}
	}

	public CutMotion simpleMerge() {
		CutMotion m = (CutMotion) super.merge();
		return m;
	}
	
	/**** budgetary cutmotion atomic value ****/
	public static void updateBudgetaryCutMotionCurrentNumberLowerHouse(Integer num){
		synchronized (CutMotion.CUTMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE) {
			CutMotion.CUTMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getBudgetaryCutMotionCurrentNumberLowerHouse(){
		return CutMotion.CUTMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateBudgetaryCutMotionCurrentNumberUpperHouse(Integer num){
		synchronized (CutMotion.CUTMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE) {
			CutMotion.CUTMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getBudgetaryCutMotionCurrentNumberUpperHouse(){
		return CutMotion.CUTMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE;
	}
	
	/**** supplementary cutmotion atomic value ****/
	public static void updateSupplementaryCutMotionCurrentNumberLowerHouse(Integer num){
		synchronized (CutMotion.CUTMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE) {
			CutMotion.CUTMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getSupplementaryCutMotionCurrentNumberLowerHouse(){
		return CutMotion.CUTMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateSupplementaryCutMotionCurrentNumberUpperHouse(Integer num){
		synchronized (CutMotion.CUTMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE) {
			CutMotion.CUTMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getSupplementaryCutMotionCurrentNumberUpperHouse(){
		return CutMotion.CUTMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE;
	}
	
	public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	} else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    		
    	} else if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	} else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    		
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(final Integer num, final String houseType, final String device){
    	
    	if(device.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(num);
    			CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(num);
    			CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(num);
    		}  	
	    	
    	} else if(device.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(num);
    			CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(num);
    			CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(num);
    		}	    	
	    	
    	}
    }

	public static List<RevisionHistoryVO> getRevisions(final Long cutMotionId, final String locale) {
		return getCutMotionRepository().getRevisions(cutMotionId, locale);
	}

	public static List<CutMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType cutMotionType,
			final Integer itemsCount,
			final String locale) {
		return getCutMotionRepository().findAllByMember(session, primaryMember, cutMotionType,itemsCount, locale);
	}

	public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	

	public static List<CutMotion> findAllByStatus(final Session session,
			final DeviceType cutMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		return getCutMotionRepository().findAllByStatus(session, cutMotionType, internalStatus, itemsCount, locale);
	}	
	
	public static boolean isDepartmentwiseMaximumLimitForMemberReached(final DeviceType deviceType, final Session session, final Member member, final Department department, final String locale) {
		boolean isDepartmentwiseMaximumLimitForMemberReached = false;
		
		CustomParameter csptDepartmentwiseMaximumLimitForMember = CustomParameter.findByName(CustomParameter.class, "CMOIS_DEPARTMENTWISE_MAXIMUM_LIMIT_FOR_MEMBER", "");
		if(csptDepartmentwiseMaximumLimitForMember!=null 
				&& csptDepartmentwiseMaximumLimitForMember.getValue()!=null
				&& !csptDepartmentwiseMaximumLimitForMember.getValue().isEmpty()) {
			
			int maximumLimitCount = Integer.parseInt(csptDepartmentwiseMaximumLimitForMember.getValue());
			
			Map<String, String[]> queryParameters = new HashMap<String, String[]>();
			queryParameters.put("locale", new String[] {locale});
			queryParameters.put("deviceTypeId", new String[] {deviceType.getId().toString()});
			queryParameters.put("sessionId", new String[] {session.getId().toString()});
			queryParameters.put("memberId", new String[] {member.getId().toString()});
			queryParameters.put("departmentId", new String[] {department.getId().toString()});
			@SuppressWarnings("rawtypes")
			List cutMotionsOfMemberForGivenDepartment = Query.findResultListOfGivenClass("CUTMOTIONS_OF_MEMBER_FOR_DEPARTMENT_IN_GIVEN_SESSION", queryParameters, CutMotion.class);
			
			if(cutMotionsOfMemberForGivenDepartment!=null 
					&& !cutMotionsOfMemberForGivenDepartment.isEmpty()
					&& cutMotionsOfMemberForGivenDepartment.size() >= maximumLimitCount) {
				
				isDepartmentwiseMaximumLimitForMemberReached = true;
			}
		}		
		
		return isDepartmentwiseMaximumLimitForMemberReached;
	}

	public CutMotionDraft findLatestDraft() {
		List<CutMotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			return drafts.get(size);            
		}
		return null;
	}

	public CutMotionDraft findPreviousDraft() {
		List<CutMotionDraft> drafts = this.getDrafts();
		if(drafts != null) {
			int size = drafts.size();
			if(size > 1) {
				return drafts.get(size-1);
			}
		}
		return null;
	}

	public static Reference findCurrentFile(final CutMotion domain) {
		return getCutMotionRepository().findCurrentFile(domain);
	}
	
	public static List<CutMotion> findAllByFile(final Session session,
			final DeviceType cutMotionType,
			final Integer file,
			final String locale) {
		return getCutMotionRepository().findAllByFile(session, cutMotionType, file, locale);
	}

	public static List<CutMotion> findBySessionDeviceTypeSubdepartment(final Session session,
			final DeviceType cutMotionType,
			final SubDepartment subDepartment,
			final String locale) {
		return getCutMotionRepository().findBySessionDeviceTypeSubdepartment(session, cutMotionType, subDepartment, locale);
	}
	
	public static Integer findMaxNumberBySubdepartment(final Session session,
			final DeviceType deviceType, final SubDepartment subDepartment, final String locale) {
		
		return getCutMotionRepository().findMaxNumberBySubdepartment(session, deviceType, subDepartment, locale);
	}	
	
	public static int findHighestFileNo(final Session session,
			final DeviceType cutMotionType,
			final String locale) {
		return getCutMotionRepository().findHighestFileNo(session, cutMotionType, locale);
	}
	
	public static CutMotion getMotion(final Long sessionId, final Long deviceTypeId, final Integer dNumber, final String locale) {
		return getCutMotionRepository().getMotion(sessionId,deviceTypeId,dNumber,locale);
	}
	
	public static List<CutMotion> findFinalizedCutMotions(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status,
			final String sortOrder,
			final String locale) {
		return getCutMotionRepository().findFinalizedCutMotionsByDepartment(session, deviceType, subDepartment, status, sortOrder, locale);
	}
	
	public static Date findDiscussionDateForDepartment(final Session session,
			final DeviceType deviceType,
			final SubDepartment subDepartment,
			final String locale) {
		Date discussionDateForDepartment = null;
		
		Status dateAdmitted = Status.findByType(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION, locale);
		CutMotionDate cutMotionDate=null;
		try {
			cutMotionDate = CutMotionDate.findCutMotionDateSessionDeviceType(session, deviceType, locale);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(cutMotionDate != null && cutMotionDate.getStatus().getType().equals(dateAdmitted.getType())){
			for(CutMotionDepartmentDatePriority p : cutMotionDate.getDepartmentDates()){
				if(p.getSubDepartment().getName().equals(subDepartment.getName())) {
					discussionDateForDepartment = p.getDiscussionDate();
					break;
				}
			}
		}
		if(discussionDateForDepartment!=null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(discussionDateForDepartment);
			calendar.set(Calendar.HOUR_OF_DAY, 6);
			discussionDateForDepartment = calendar.getTime();
		}
		
		return discussionDateForDepartment;
	}
	
	public static Boolean assignCutMotionNumberByDepartment(final Session session,
			final DeviceType deviceType,
			final SubDepartment subDepartment,
			final String locale) {
		
		//disallow assign number if discussion date processing time has reached
		Date discussionDateForDepartment = CutMotion.findDiscussionDateForDepartment(session, deviceType, subDepartment, locale);
		if(new Date().after(discussionDateForDepartment)) {
			return false;
		}
		
		/**** Assign number to admitted cutmotions ****/
		boolean admittedMotionUpdated = false;
		Status admitted = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION, locale);
		int currentAdmissionCount = 0;
		Integer intCurrentAdmissionCount = CutMotion.findHighestNumberByStatusDepartment(session, deviceType, subDepartment, admitted, locale);
		if(intCurrentAdmissionCount != null){
			currentAdmissionCount = intCurrentAdmissionCount.intValue();
		}		
		List<CutMotion> admittedCutMotions = CutMotion.findFinalizedCutMotions(session, deviceType, subDepartment, admitted, ApplicationConstants.ASC, locale);
		int admissionCounter = 0;
		String reassignAdmission = null;
		CustomParameter csptReassignAdmissionNumbers = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_REASSIGN_ADMISSION_NUMBER, "");
		if(csptReassignAdmissionNumbers != null && csptReassignAdmissionNumbers.getValue() != null && !csptReassignAdmissionNumbers.getValue().isEmpty()){
			reassignAdmission = csptReassignAdmissionNumbers.getValue();
		}
		Date yaadiLayingDateForDepartment = Holiday.getLastWorkingDateFrom(discussionDateForDepartment, 1, ApplicationConstants.DAY_WORKING_SCOPE_SECRETARIAT_STAFF, locale);
		for(CutMotion cm : admittedCutMotions){
			if(reassignAdmission != null && !reassignAdmission.isEmpty() && reassignAdmission.equals("yes")){
				++admissionCounter;
				cm.setInternalNumber(/*currentAdmissionCount + */admissionCounter);
				cm.setYaadiLayingDate(yaadiLayingDateForDepartment);
				cm.simpleMerge();
				admittedMotionUpdated = true;
			}else{
				if(cm.getInternalNumber() == null){
					++admissionCounter;
					cm.setInternalNumber(currentAdmissionCount + admissionCounter);
					cm.setYaadiLayingDate(yaadiLayingDateForDepartment);
					cm.simpleMerge();					
				}					
				admittedMotionUpdated = true;
			}
		}
		
		/**** Assign number to rejected cutmotions ****/
		boolean rejectedMotionUpdated = false;
		Status rejected = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_REJECTION, locale);
		int currentRejectionCount = 0;
		Integer intCurrentRejectionCount = CutMotion.findHighestNumberByStatusDepartment(session, deviceType, subDepartment, rejected, locale);
		if(intCurrentRejectionCount != null){
			currentRejectionCount = intCurrentRejectionCount.intValue();
		}		
		List<CutMotion> rejectedCutMotions = CutMotion.findFinalizedCutMotions(session, deviceType, subDepartment, rejected, ApplicationConstants.ASC, locale);
		int rejectionCounter = 0;
		String reassignRejection = null;		
		CustomParameter csptReassignRejectionNumbers = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_REASSIGN_REJECTION_NUMBER, "");
		if(csptReassignRejectionNumbers != null && csptReassignRejectionNumbers.getValue() != null && !csptReassignRejectionNumbers.getValue().isEmpty()){
			reassignRejection = csptReassignRejectionNumbers.getValue();
		}
		for(CutMotion cm : rejectedCutMotions){
			if(reassignRejection != null && !reassignRejection.isEmpty() && reassignRejection.equals("yes")){
				++rejectionCounter;
				cm.setInternalNumber(/*currentRejectionCount + */rejectionCounter);
				cm.setYaadiLayingDate(null);
				cm.simpleMerge();
				rejectedMotionUpdated = true;
			}else{
				if(cm.getInternalNumber() == null){
					++rejectionCounter;
					cm.setInternalNumber(currentRejectionCount + rejectionCounter);
					cm.setYaadiLayingDate(null);
					cm.simpleMerge();					
				}					
				rejectedMotionUpdated = true;
			}
		}
			
		if(admittedMotionUpdated){		
			if(rejectedCutMotions == null || rejectedCutMotions.isEmpty()){
				rejectedMotionUpdated = true;
			}
		}
		if(admittedMotionUpdated && rejectedMotionUpdated){
			return true;
		}
		
		return false;
	}
	
	private static Integer findHighestNumberByStatusDepartment(final Session session,
			final DeviceType deviceType, 
			final SubDepartment subDepartment,
			final Status status, final 
			String locale) {
		return getCutMotionRepository().findHighestNumberByStatusDepartment(session, deviceType, subDepartment, status, locale);
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
		List<ClubbedEntity> clubbedEntities = CutMotion.findClubbedEntitiesByPosition(this);
		if (clubbedEntities != null) {
			for (ClubbedEntity ce : clubbedEntities) {
				/**
				 * show only those clubbed questions which are not in state of
				 * (processed to be putup for nameclubbing, putup for
				 * nameclubbing, pending for nameclubbing approval)
				 **/
				if (ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED)
						|| ce.getStandaloneMotion().getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)) {
					member = ce.getStandaloneMotion().getPrimaryMember();
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
					List<SupportingMember> clubbedSupportingMembers = ce.getStandaloneMotion().getSupportingMembers();
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
	
	public static List<CutMotionDraft> findDraftsForGivenDevice(final Long deviceId) {
		return getCutMotionRepository().findDraftsForGivenDevice(deviceId);
	}
	
	public static List<MasterVO> findAllYaadiDepartmentDetails(final Session session, final DeviceType cutMotionType, final String locale) {
		return getCutMotionRepository().findAllYaadiDepartmentDetails(session, cutMotionType, locale);
	}
	
	//************************Clubbing**********************
	public static boolean club(final Long primary, final Long clubbing, final String locale) throws ELSException{
		
		CutMotion m1 = CutMotion.findById(CutMotion.class, primary);
		CutMotion m2 = CutMotion.findById(CutMotion.class, clubbing);
		
		return club(m1, m2, locale); 
		
	}

	public static boolean club(final CutMotion q1,final CutMotion q2,final String locale) throws ELSException{    	
    	boolean clubbingStatus = false;
    	try {    		
    		if(q1.getParent()!=null || q2.getParent()!=null) {
    			throw new ELSException("error", "MOTION_ALREADY_CLUBBED");
    		} else {
    			if((q1.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)
        				&& q2.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY))
        				|| (q1.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)
                				&& q2.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY))) {
    				
    					clubbingStatus = clubMotions(q1, q2, locale);    				
        		} else {
        			return false;
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
	
	private static boolean clubMotions(CutMotion q1, CutMotion q2, String locale) throws ELSException {
    	boolean clubbingStatus = false;
    	clubbingStatus = clubbingRulesForMotion(q1, q2, locale);
    	if(clubbingStatus) {
    		
    		clubbingStatus = clubMotion(q1, q2, locale);

    	}    	 
    	return clubbingStatus;
    }
	
	private static boolean clubbingRulesForMotion(CutMotion q1, CutMotion q2, String locale) throws ELSException {
    	boolean clubbingStatus = clubbingRulesCommon(q1, q2, locale);
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){ 
	    	if(clubbingStatus) {
	    		if(q1.getReply()!=null && !q1.getReply().isEmpty()) {
	    			WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    			if(q1_workflowDetails!=null && q1_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
	    				throw new ELSException("error", "MOTION_ANSWERED_BUT_FLOW_PENDING");
	    			}
	    		}
	    		if(q2.getReply()!=null && !q2.getReply().isEmpty()) {
	    			WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    			if(q2_workflowDetails!=null && q2_workflowDetails.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
	    				throw new ELSException("error", "MOTION_ANSWERED_BUT_FLOW_PENDING");
	    			}
	    		}    		
	    	}
    	}
    	return clubbingStatus;
    	
    }
    
    private static boolean clubMotion(CutMotion q1, CutMotion q2, String locale) throws ELSException {
    	//=============cases common to both houses============//
    	boolean clubbingStatus = clubMotionsBH(q1, q2, locale);
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_CLUBBING_MODE, "");

    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("workflow")){
	    	if(!clubbingStatus) {
	    		//=============cases specific to lowerhouse============//
	        	/** get chart answering dates for questions **/
	        	Date q1_AnsweringDate = q1.getAnsweringDate();
	        	Date q2_AnsweringDate = q2.getAnsweringDate();
	        	
	        	Status yaadiLaidStatus = Status.findByType(ApplicationConstants.MOTION_PROCESSED_YAADILAID, locale);
	        	
	        	//Case 7: Both questions are admitted and balloted
	        	if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)
	    				&& q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)
	    				&& (q1.getRecommendationStatus().getPriority().compareTo(yaadiLaidStatus.getPriority())<0)) {
	        		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
	        		if(q1_AnsweringDate.compareTo(q2_AnsweringDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	        				clubbingStatus = true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        				clubbingStatus = true;
	        			} else {
	        				clubbingStatus = true;
	        			}
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)<0) {
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			clubbingStatus = true;
	        		} else if(q1_AnsweringDate.compareTo(q2_AnsweringDate)>0) {
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			clubbingStatus = true;
	        		}
	        	}
	    	}    	
    	}
    	
    	return clubbingStatus;
    }
    
    private static boolean clubbingRulesCommon(CutMotion q1, CutMotion q2, String locale) throws ELSException {
    	if(q1.getSession().equals(q2.getSession()) && !q1.getDeviceType().getType().equals(q2.getDeviceType().getType())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEVICETYPE");    		
    	} else if(!q1.getMinistry().getName().equals(q2.getMinistry().getName())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_MINISTRY");    		
    	} else if(!q1.getSubDepartment().getName().equals(q2.getSubDepartment().getName())) {
    		throw new ELSException("error", "MOTIONS_FROM_DIFFERENT_DEPARTMENT");    		
    	} else {
    		//clubbing rules succeeded
    		return true;
    	}  	
    }
    
    private static boolean clubMotionsBH(CutMotion q1, CutMotion q2, String locale) throws ELSException {
    	
    	CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
    		if(q1.getNumber().compareTo(q2.getNumber())<0) {
				actualClubbingMotions(q1, q2, q1.getInternalStatus(), q1.getRecommendationStatus(), locale);
				WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(q2);
				if(wfOfChild != null){
					WorkflowDetails.endProcess(wfOfChild);
				}
				q2.removeExistingWorkflowAttributes();
				return true;
			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
				actualClubbingMotions(q2, q1, q2.getInternalStatus(), q2.getRecommendationStatus(), locale);
				
				WorkflowDetails wfOfChild = WorkflowDetails.findCurrentWorkflowDetail(q1);
				if(wfOfChild != null){
					WorkflowDetails.endProcess(wfOfChild);
				}
				q1.removeExistingWorkflowAttributes();
				
				return true;
			} else {
				return false;
			}
    	}else{
	    	/** get answering dates for motions **/
	    	Date q1_DiscussionDate = q1.getDiscussionDate();
	    	Date q2_DiscussionDate = q2.getDiscussionDate();
	    	
	    	Status putupStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
			Status approvalStatus = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION, locale);
	    	
	    	//Case 1: Both motions are just ready to be put up
	    	if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)
	    			&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		
	    		Status clubbedStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED, locale);
	    		if(q1_DiscussionDate != null && q2_DiscussionDate != null){
	    			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {
	        				actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	        			actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	        			return true;
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
	        			actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		}else{
	    			
	    			if(q1.getNumber().compareTo(q2.getNumber())<0) {
	    				actualClubbingMotions(q1, q2, clubbedStatus, clubbedStatus, locale);
	    				return true;
	    			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	    				actualClubbingMotions(q2, q1, clubbedStatus, clubbedStatus, locale);
	    				return true;
	    			} else {
	    				return false;
	    			}
	    		}
	    	} 
	    	//Case 2A: One motion is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING, locale);
	    		actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 2B: One motion is pending in approval workflow while other is ready to be put up
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)
	    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING, locale);
	    		actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 3: Both motions are pending in approval workflow
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
	    				&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
	    				&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status clubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	    		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	    		if(q1_approvalLevel==q2_approvalLevel) {
	    			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				WorkflowDetails.endProcess(q2_workflowDetails);
	        				q2.removeExistingWorkflowAttributes();
	        				actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);          				
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				WorkflowDetails.endProcess(q1_workflowDetails);
	        				q1.removeExistingWorkflowAttributes();
	        				actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	        			WorkflowDetails.endProcess(q2_workflowDetails);
	        			q2.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        			return true;
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
	        			WorkflowDetails.endProcess(q1_workflowDetails);
	        			q1.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_approvalLevel>q2_approvalLevel) {
	    			WorkflowDetails.endProcess(q2_workflowDetails);
	    			q2.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q1, q2, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    			return true;
	    		} else if(q1_approvalLevel<q2_approvalLevel) {
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    			q1.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q2, q1, clubbbingPutupStatus, clubbbingPutupStatus, locale);
	    			return true;
	    		} else {
	    			return false;
	    		}    		
	    	}
	    	//Case 4A: One motion is admitted but not balloted yet while other motion is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_NAMECLUBBING, locale);
	    		actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 4B: One motion is admitted but not balloted yet while other motion is ready to be put up (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED)
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_NAMECLUBBING, locale);
	    		actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 5A: One motion is admitted but not balloted yet while other motion is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)
					&& q2.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q2.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_NAMECLUBBING, locale);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		WorkflowDetails.endProcess(q2_workflowDetails);
	    		q2.removeExistingWorkflowAttributes();
	    		actualClubbingMotions(q1, q2, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 5B: One motion is admitted but not balloted yet while other motion is pending in approval workflow (Nameclubbing Case)
	    	else if(q1.getInternalStatus().getPriority().compareTo(putupStatus.getPriority()) > 0
					&& q1.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority()) < 0
					&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)) {
	    		Status nameclubbbingPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_NAMECLUBBING, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		if(q1_workflowDetails != null){
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    		}
	    		q1.removeExistingWorkflowAttributes();
	    		actualClubbingMotions(q2, q1, nameclubbbingPutupStatus, nameclubbbingPutupStatus, locale);
	    		return true;
	    	}
	    	//Case 6: Both motions are admitted but not balloted
	    	else if(q1.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)
	    				&& q2.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)) {
	    		Status clubbbingPostAdmissionPutupStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING_POST_ADMISSION, locale);
	    		WorkflowDetails q1_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q1);
	    		WorkflowDetails q2_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(q2);
	    		if(q1_workflowDetails==null && q2_workflowDetails==null) {
	    			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0 || q1.isFromDifferentBatch(q2)) {
	        			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	        				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	        				return true;
	        			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	        				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        				return true;
	        			} else {
	        				return false;
	        			}
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails!=null) {
	    			int q1_approvalLevel = Integer.parseInt(q1_workflowDetails.getAssigneeLevel());
	        		int q2_approvalLevel = Integer.parseInt(q2_workflowDetails.getAssigneeLevel());
	        		if(q1_approvalLevel==q2_approvalLevel) {
	        			if(q1_DiscussionDate.compareTo(q2_DiscussionDate)==0 || q1.isFromDifferentBatch(q2)) {
	            			if(q1.getNumber().compareTo(q2.getNumber())<0) {        
	            				WorkflowDetails.endProcess(q2_workflowDetails);
	            				q2.removeExistingWorkflowAttributes();
	            				actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
	            				return true;
	            			} else if(q1.getNumber().compareTo(q2.getNumber())>0) {
	            				WorkflowDetails.endProcess(q1_workflowDetails);
	            				q1.removeExistingWorkflowAttributes();
	            				actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            				return true;
	            			} else {
	            				return false;
	            			}
	            		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)<0) {
	            			WorkflowDetails.endProcess(q2_workflowDetails);
	            			q2.removeExistingWorkflowAttributes();
	            			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            			return true;
	            		} else if(q1_DiscussionDate.compareTo(q2_DiscussionDate)>0) {
	            			WorkflowDetails.endProcess(q1_workflowDetails);
	            			q1.removeExistingWorkflowAttributes();
	            			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	            			return true;
	            		} else {
	            			return false;
	            		}
	        		} else if(q1_approvalLevel>q2_approvalLevel) {
	        			WorkflowDetails.endProcess(q2_workflowDetails);
	        			q2.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else if(q1_approvalLevel<q2_approvalLevel) {
	        			WorkflowDetails.endProcess(q1_workflowDetails);
	        			q1.removeExistingWorkflowAttributes();
	        			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	        			return true;
	        		} else {
	        			return false;
	        		}
	    		} else if(q1_workflowDetails==null && q2_workflowDetails!=null) {
	    			WorkflowDetails.endProcess(q2_workflowDetails);
	    			q2.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q1, q2, q2.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);          				
					return true;
	    		} else if(q1_workflowDetails!=null && q2_workflowDetails==null) {
	    			WorkflowDetails.endProcess(q1_workflowDetails);
	    			q1.removeExistingWorkflowAttributes();
	    			actualClubbingMotions(q2, q1, q1.getInternalStatus(), clubbbingPostAdmissionPutupStatus, locale);
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}    	
	    	else {
	    		return false;
	    	}
    	}
    }  
    
    public Boolean isFromDifferentBatch(CutMotion q) {
		Boolean isFromDifferentBatch = false;
		if(q!=null && this.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
				&& q.getSession().findHouseType().equals(ApplicationConstants.UPPER_HOUSE)
				&& this.getSession().getId().equals(q.getSession().getId())) {
			
			String firstBatchStartDateParameter = null;
			String firstBatchEndDateParameter = null;
			
			if(q.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)){
				firstBatchStartDateParameter = this.getSession().getParameter(ApplicationConstants.CUTMOTION_BUDGETARY_FIRST_BATCH_START_TIME);
				firstBatchEndDateParameter = this.getSession().getParameter(ApplicationConstants.CUTMOTION_BUDGETARY_FIRST_BATCH_END_TIME);
			}else if(q.getDeviceType().getType().equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)){
				firstBatchStartDateParameter = this.getSession().getParameter(ApplicationConstants.CUTMOTION_SUPPLEMENTARY_FIRST_BATCH_START_TIME);
				firstBatchEndDateParameter = this.getSession().getParameter(ApplicationConstants.CUTMOTION_SUPPLEMENTARY_FIRST_BATCH_END_TIME);
			}
			
			if(firstBatchStartDateParameter!=null&&firstBatchEndDateParameter!=null){
				if((!firstBatchStartDateParameter.isEmpty())&&(!firstBatchEndDateParameter.isEmpty())){
					Date firstBatchStartDate = FormaterUtil.formatStringToDate(firstBatchStartDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					Date firstBatchEndDate = FormaterUtil.formatStringToDate(firstBatchEndDateParameter, ApplicationConstants.DB_DATETIME_FORMAT);
					String this_batch = "";
					if(this.getSubmissionDate().compareTo(firstBatchStartDate)>=0
							&& this.getSubmissionDate().compareTo(firstBatchEndDate)<=0) {
						this_batch = "FIRST_BATCH";
					} else if(this.getSubmissionDate().compareTo(firstBatchEndDate)>0) {
						this_batch = "SECOND_BATCH";
					}
					String q_batch = "";
					if(q.getSubmissionDate().compareTo(firstBatchStartDate)>=0
							&& q.getSubmissionDate().compareTo(firstBatchEndDate)<=0) {
						q_batch = "FIRST_BATCH";
					} else if(this.getSubmissionDate().compareTo(firstBatchEndDate)>0) {
						q_batch = "SECOND_BATCH";
					}
					if(!this_batch.isEmpty() && !q_batch.isEmpty() && !this_batch.equals(q_batch)) {
						isFromDifferentBatch = true;
					}
				}
			}
		}
		return isFromDifferentBatch;
	}
    
    private static void actualClubbingMotions(CutMotion parent, CutMotion child,
			Status newInternalStatus, Status newRecommendationStatus,String locale) throws ELSException {
		/**** a.Clubbed entities of parent motion are obtained 
		 * b.Clubbed entities of child motion are obtained
		 * c.Child motion is updated(parent,internal status,recommendation status) 
		 * d.Child Motion entry is made in Clubbed Entity and child motion clubbed entity is added to parent clubbed entity 
		 * e.Clubbed entities of child motions are updated(parent,internal status,recommendation status)
		 * f.Clubbed entities of parent(child motion clubbed entities,other clubbed entities of child question and 
		 * clubbed entities of parent motion is updated)
		 * g.Position of all clubbed entities of parent are updated in order of their answering date and number ****/
		List<ClubbedEntity> parentClubbedEntities=new ArrayList<ClubbedEntity>();
		if(parent.getClubbedEntities()!=null && !parent.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:parent.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long childMnId = child.getId();
				CutMotion clubbedMn = i.getCutMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(! childMnId.equals(clubbedMnId)) {
					parentClubbedEntities.add(i);
				}
			}			
		}

		List<ClubbedEntity> childClubbedEntities=new ArrayList<ClubbedEntity>();
		if(child.getClubbedEntities()!=null && !child.getClubbedEntities().isEmpty()){
			for(ClubbedEntity i:child.getClubbedEntities()){
				// parent & child need not be disjoint. They could
				// be present in each other's hierarchy.
				Long parentMnId = parent.getId();
				CutMotion clubbedMn = i.getCutMotion();
				Long clubbedMnId = clubbedMn.getId();
				if(! parentMnId.equals(clubbedMnId)) {
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
		clubbedEntity.setDeviceType(child.getDeviceType());
		clubbedEntity.setLocale(child.getLocale());
		clubbedEntity.setCutMotion(child);
		clubbedEntity.persist();
		parentClubbedEntities.add(clubbedEntity);		

		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_CLUBBING_MODE, "");
		if(csptClubbingMode != null){
			if(csptClubbingMode.getValue() != null && !csptClubbingMode.getValue().isEmpty()){
			
				if(csptClubbingMode.getValue().equals("normal")){
					Status submitted = Status.findByType(ApplicationConstants.CUTMOTION_SUBMIT, locale);
					
					if(childClubbedEntities != null && !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k : childClubbedEntities){
							CutMotion motion = k.getCutMotion();					
							
							WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(motion);
							if(wd != null){
								WorkflowDetails.endProcess(wd);
								motion.removeExistingWorkflowAttributes();
							}
							
							motion.setInternalStatus(newInternalStatus);
							motion.setRecommendationStatus(newRecommendationStatus);
							motion.setStatus(submitted);
							motion.setParent(parent);
							motion.merge();
							parentClubbedEntities.add(k);
						}			
					}
					
				}else if(csptClubbingMode.getValue().equals("workflow")){
					if(childClubbedEntities!=null&& !childClubbedEntities.isEmpty()){
						for(ClubbedEntity k:childClubbedEntities){
							CutMotion motion=k.getCutMotion();					
							/** find current clubbing workflow if pending **/
							String pendingWorkflowTypeForMotion = "";
							
							if(motion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_CLUBBING)
									|| motion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
							} else if(motion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_NAME_CLUBBING)
									|| motion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_NAME_CLUBBING)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
							} else if(motion.getRecommendationStatus().getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
									|| motion.getRecommendationStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
								pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
							}
							
							
							if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
								/** end current clubbing workflow **/
								WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(motion, pendingWorkflowTypeForMotion);
								if(wfDetails != null){
									WorkflowDetails.endProcess(wfDetails);
								}
								motion.removeExistingWorkflowAttributes();
								
								/** put up for proper clubbing workflow as per updated parent **/
								Status finalAdmitStatus = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION , locale);
								Integer parent_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();
								Integer motion_finalAdmissionStatusPriority = finalAdmitStatus.getPriority();					
								
								if(parent.getStatus().getPriority().compareTo(parent_finalAdmissionStatusPriority)<0) {
									Status putupForClubbingStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING , locale);
									motion.setInternalStatus(putupForClubbingStatus);
									motion.setRecommendationStatus(putupForClubbingStatus);
								} else {
									if(motion.getStatus().getPriority().compareTo(motion_finalAdmissionStatusPriority)<0) {
										Status putupForNameClubbingStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_NAMECLUBBING , locale);
										motion.setInternalStatus(putupForNameClubbingStatus);
										motion.setRecommendationStatus(putupForNameClubbingStatus);
									} else {
										Status putupForClubbingPostAdmissionStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING_POST_ADMISSION , locale);
										motion.setInternalStatus(putupForClubbingPostAdmissionStatus);
										motion.setRecommendationStatus(putupForClubbingPostAdmissionStatus);
									}
								}
							}
							motion.setParent(parent);
							motion.merge();
							parentClubbedEntities.add(k);
						}			
					}
				}
			}
			
			boolean isChildBecomingParentCase = false;
			if(parent.getParent()!=null) {
				isChildBecomingParentCase = true;
				parent.setParent(null);
			}		
			parent.setClubbedEntities(parentClubbedEntities);
			if(isChildBecomingParentCase) {
				Long parent_currentVersion = parent.getVersion();
				parent_currentVersion++;
				parent.setVersion(parent_currentVersion);
				parent.simpleMerge();
			} else {
				parent.merge();
			}		

			List<ClubbedEntity> clubbedEntities=parent.findClubbedEntitiesByDiscussionDateMotionNumber(ApplicationConstants.ASC,locale);
			Integer position=1;
			for(ClubbedEntity i:clubbedEntities){
				i.setPosition(position);
				position++;
				i.merge();
			}
		}
	}
    
    public void removeExistingWorkflowAttributes() {
		// Update question so as to remove existing workflow
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
    
    public List<ClubbedEntity> findClubbedEntitiesByDiscussionDateMotionNumber(final String sortOrder, final String locale) {
    	return getCutMotionRepository().findClubbedEntitiesByDiscussionDateMotionNumber(this,sortOrder, locale);
    }
    
	//************************Clubbing**********************
    
    //************************Unclubbing********************
    public static boolean unclub(final Long m1, final Long m2, String locale) throws ELSException {
		CutMotion motion1 = CutMotion.findById(CutMotion.class, m1);
		CutMotion motion2 = CutMotion.findById(CutMotion.class, m2);
		return unclub(motion1, motion2, locale);
	}
    
    public static boolean unclub(final CutMotion q1, final CutMotion q2, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(q1.getParent()==null && q2.getParent()==null) {
			throw new ELSException("error", "CLUBBED_MOTION_NOT_FOUND");
		}
		if(q2.getParent()!=null && q2.getParent().equals(q1)) {
			clubbingStatus = actualUnclubbing(q1, q2, locale);
		} else if(q1.getParent()!=null && q1.getParent().equals(q2)) {
			clubbingStatus = actualUnclubbing(q2, q1, locale);
		} else {
			throw new ELSException("error", "NO_CLUBBING_BETWEEN_GIVEN_MOTIONS");
		}
		return clubbingStatus;
	}
	
	public static boolean unclub(final CutMotion motion, String locale) throws ELSException {
		boolean clubbingStatus = false;
		if(motion.getParent()==null) {
			throw new ELSException("error", "MOTION_NOT_CLUBBED");
		}
		clubbingStatus = actualUnclubbing(motion.getParent(), motion, locale);
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbing(final CutMotion parent, final CutMotion child, String locale) throws ELSException {
		boolean clubbingStatus = false;
		clubbingStatus = actualUnclubbingMotions(parent, child, locale);		
		return clubbingStatus;
	}
	
	public static boolean actualUnclubbingMotions(final CutMotion parent, final CutMotion child, String locale) throws ELSException {
		
		boolean retVal = false;
		CustomParameter csptClubbingMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CUTMOTION_CLUBBING_MODE, "");
    	
    	if(csptClubbingMode != null && csptClubbingMode.getValue() != null 
    			&& !csptClubbingMode.getValue().isEmpty() && csptClubbingMode.getValue().equals("normal")){
    		Status putupStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
    		Status submitStatus = Status.findByType(ApplicationConstants.CUTMOTION_SUBMIT, locale);
    		
    		/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
			List<ClubbedEntity> oldClubbedMotions=parent.getClubbedEntities();
			List<ClubbedEntity> newClubbedMotions=new ArrayList<ClubbedEntity>();
			Integer position=0;
			boolean found=false;
			
			for(ClubbedEntity i:oldClubbedMotions){
				if(!i.getCutMotion().getId().equals(child.getId())){
					if(found){
						i.setPosition(position);
						position++;
						i.merge();
						newClubbedMotions.add(i);
					}else{
						newClubbedMotions.add(i);                		
					}
				}else{
					found = true;
					position = i.getPosition();
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
			child.setInternalStatus(putupStatus);
			child.setRecommendationStatus(putupStatus);
			child.setStatus(submitStatus);
			child.merge();
			retVal = true;
		}else{	
			/** if child was clubbed with speaker/chairman approval then put up for unclubbing workflow **/
			//TODO: write condition for above case & initiate code to send for unclubbing workflow
			Status approvedStatus = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION, locale);		
			if(child.getInternalStatus().getPriority().compareTo(approvedStatus.getPriority())>=0
					&& !child.getRecommendationStatus().equals(ApplicationConstants.CUTMOTION_PUTUP_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus().equals(ApplicationConstants.CUTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
					&& !child.getRecommendationStatus().equals(ApplicationConstants.CUTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
				Status putupUnclubStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_UNCLUBBING, locale);
				child.setRecommendationStatus(putupUnclubStatus);
				child.merge();
				retVal = true;
			} else {
				/** remove child's clubbing entitiy from parent & update parent's clubbing entities **/
				List<ClubbedEntity> oldClubbedMotions=parent.getClubbedEntities();
				List<ClubbedEntity> newClubbedMotions=new ArrayList<ClubbedEntity>();
				Integer position=0;
				boolean found=false;
				for(ClubbedEntity i:oldClubbedMotions){
					if(! i.getCutMotion().getId().equals(child.getId())){
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
				if(child.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_WORKFLOW;
				} else if(child.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_NAME_CLUBBING)
						|| child.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_NAME_CLUBBING)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.NAMECLUBBING_WORKFLOW;
				} else if(child.getRecommendationStatus().getType().equals(ApplicationConstants.CUTMOTION_RECOMMEND_CLUBBING_POST_ADMISSION)
						|| child.getRecommendationStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_CLUBBING_POST_ADMISSION)) {
					pendingWorkflowTypeForMotion = ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW;
				}
				if(pendingWorkflowTypeForMotion!=null && !pendingWorkflowTypeForMotion.isEmpty()) {
					WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(child, pendingWorkflowTypeForMotion);	
					WorkflowDetails.endProcess(wfDetails);
					child.removeExistingWorkflowAttributes();
				}
				/** update child status **/
				Status putupStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED, locale);
				Status admitStatus = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION, locale);
				if(child.getInternalStatus().getPriority().compareTo(admitStatus.getPriority())<0) {
					child.setInternalStatus(putupStatus);
					child.setRecommendationStatus(putupStatus);
				} else {
					if(child.getReply()==null || child.getReply().isEmpty()) {
						child.setInternalStatus(admitStatus);
						child.setRecommendationStatus(admitStatus);
						Workflow processWorkflow = Workflow.findByStatus(admitStatus, locale);
						UserGroupType assistantUGT = UserGroupType.findByType(ApplicationConstants.ASSISTANT, locale);
						WorkflowDetails.startProcessAtGivenLevel(child, ApplicationConstants.APPROVAL_WORKFLOW, processWorkflow, assistantUGT, 1, locale);
					} else {
						child.setInternalStatus(admitStatus);
						Status replyReceivedStatus = Status.findByType(ApplicationConstants.CUTMOTION_PROCESSED_REPLY_RECEIVED, locale);
						child.setRecommendationStatus(replyReceivedStatus);
					}
				}
			}	
			child.merge();
			retVal = true;
		}
		return retVal;
	}
    //************************Unclubbing********************
	//************************Clubbing unclubbing update*********************
	/**** Motion Update Clubbing Starts ****/
    public static void updateClubbing(CutMotion motion) throws ELSException {
		//case 1: motion is child
		if(motion.getParent()!=null) {
			CutMotion.updateClubbingForChild(motion);
		} 
		//case 2: motion is parent
		else if(motion.getParent()==null && motion.getClubbedEntities()!=null && !motion.getClubbedEntities().isEmpty()) {
			CutMotion.updateClubbingForParent(motion);
		}
	}
    
    private static void updateClubbingForChild(CutMotion motion) throws ELSException {
		updateClubbingForChildMotion(motion);
	}
    
    private static void updateClubbingForChildMotion(CutMotion motion) throws ELSException {
		String locale = motion.getLocale();
		CutMotion parentMotion = motion.getParent();
		    	
    	Status putupStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_ASSISTANT_PROCESSED, motion.getLocale());
		Status approvalStatus = Status.findByType(ApplicationConstants.CUTMOTION_FINAL_ADMISSION, motion.getLocale());
		
		if(motion.isFromDifferentBatch(parentMotion)) {
			
			if(parentMotion.getNumber().compareTo(motion.getNumber())<0) {
				
				updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
				
				if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					Status clubbedStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED, motion.getLocale());
					motion.setInternalStatus(clubbedStatus);
					motion.setRecommendationStatus(clubbedStatus);
				} else {
					motion.setStatus(parentMotion.getInternalStatus());
					motion.setInternalStatus(parentMotion.getInternalStatus());
					motion.setRecommendationStatus(parentMotion.getInternalStatus());
				}
				motion.simpleMerge();
				
			} else if(parentMotion.getNumber().compareTo(motion.getNumber())>0) {				
				
				WorkflowDetails parentMoion_workflowDetails = WorkflowDetails.findCurrentWorkflowDetail(parentMotion);
				if(parentMoion_workflowDetails!=null) {
					WorkflowDetails.endProcess(parentMoion_workflowDetails);					
					parentMotion.removeExistingWorkflowAttributes();
				}
				if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
					motion.setInternalStatus(putupStatus);
					motion.setRecommendationStatus(putupStatus);
					
					//updateDomainFieldsOnClubbingFinalisation(question, parentQuestion);
					
					Status clubbedStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED, motion.getLocale());
					actualClubbingMotions(motion, parentMotion, clubbedStatus, clubbedStatus, locale);
				} else {
					motion.setStatus(parentMotion.getInternalStatus());
					motion.setInternalStatus(parentMotion.getInternalStatus());
					if(parentMotion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_FINAL_ADMISSION)) {
						Status admitDueToReverseClubbingStatus = Status.findByType(ApplicationConstants.CUTMOTION_PUTUP_ADMIT_DUE_TO_REVERSE_CLUBBING, motion.getLocale());
						motion.setRecommendationStatus(admitDueToReverseClubbingStatus);
						Workflow admitDueToReverseClubbingWorkflow = Workflow.findByStatus(admitDueToReverseClubbingStatus, locale);
						WorkflowDetails.startProcess(motion, ApplicationConstants.APPROVAL_WORKFLOW, admitDueToReverseClubbingWorkflow, locale);
					} else {
						//TODO:handle case when parent is already rejected.. below is temporary fix
						//clarification from ketkip remaining
						motion.setRecommendationStatus(parentMotion.getInternalStatus());	
						
					}					
					if(parentMotion.getReply()!=null && (motion.getReply()==null || motion.getReply().isEmpty())) {
						motion.setReply(parentMotion.getReply());
					}
					updateDomainFieldsOnClubbingFinalisation(motion, parentMotion);
									
					actualClubbingMotions(motion, parentMotion, parentMotion.getInternalStatus(), parentMotion.getInternalStatus(), locale);
				}
			}
		} else {
			
				
			updateDomainFieldsOnClubbingFinalisation(parentMotion, motion);
			
			if(parentMotion.getInternalStatus().getPriority().compareTo(approvalStatus.getPriority())<0) {
				Status clubbedStatus = Status.findByType(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED, motion.getLocale());
				motion.setInternalStatus(clubbedStatus);
				motion.setRecommendationStatus(clubbedStatus);
			} else {
				motion.setStatus(parentMotion.getInternalStatus());
				motion.setInternalStatus(parentMotion.getInternalStatus());
				motion.setRecommendationStatus(parentMotion.getInternalStatus());
			}
			motion.simpleMerge();
		}
	}
    
    public static void updateDomainFieldsOnClubbingFinalisation(CutMotion parent, CutMotion child) {
    	updateDomainFieldsOnClubbingFinalisationForMotion(parent, child);		
    }
    
    private static void updateDomainFieldsOnClubbingFinalisationForMotion(CutMotion parent, CutMotion child) {
    	updateDomainFieldsOnClubbingFinalisationCommon(parent, child);
    }
    
    private static void updateDomainFieldsOnClubbingFinalisationCommon(CutMotion parent, CutMotion child) {
		/** copy latest subject of parent to revised subject of child **/
		if(parent.getRevisedMainTitle()!=null && !parent.getRevisedMainTitle().isEmpty()) {
			child.setRevisedMainTitle(parent.getRevisedMainTitle());
		} else {
			child.setRevisedMainTitle(parent.getMainTitle());
		}
		if(parent.getRevisedSubTitle()!=null && !parent.getRevisedSubTitle().isEmpty()) {
			child.setRevisedSubTitle(parent.getRevisedSubTitle());
		} else {
			child.setRevisedSubTitle(parent.getSubTitle());
		}
		if(parent.getRevisedSecondaryTitle()!=null && !parent.getRevisedSecondaryTitle().isEmpty()) {
			child.setRevisedSecondaryTitle(parent.getRevisedSecondaryTitle());
		} else {
			child.setRevisedSecondaryTitle(parent.getSecondaryTitle());
		}
		/** copy latest details text of parent to revised details text of child **/
		if(parent.getRevisedNoticeContent()!=null && !parent.getRevisedNoticeContent().isEmpty()) {
			child.setRevisedNoticeContent(parent.getRevisedNoticeContent());
		} else {
			child.setRevisedNoticeContent(parent.getNoticeContent());
		}
		/** copy latest answer of parent to revised answer of child **/
		child.setReply(parent.getReply());
	}
    
    private static void updateClubbingForParent(CutMotion motion) {
    	updateClubbingForParentMotion(motion);		
	}

	private static void updateClubbingForParentMotion(CutMotion motion) {
		for(ClubbedEntity ce: motion.getClubbedEntities()) {
			CutMotion clubbedMotion = ce.getCutMotion();
			if(clubbedMotion.getInternalStatus().getType().equals(ApplicationConstants.CUTMOTION_SYSTEM_CLUBBED)) {
				
				updateDomainFieldsOnClubbingFinalisation(motion, clubbedMotion);
				
				clubbedMotion.setStatus(motion.getInternalStatus());
				clubbedMotion.setInternalStatus(motion.getInternalStatus());
				clubbedMotion.setRecommendationStatus(motion.getInternalStatus());				
				clubbedMotion.merge();
			}
		}
	}
	//************************Clubbing unclubbing update**********************
	
	public void startWorkflow(final CutMotion cutMotion, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		cutMotion.endWorkflow(cutMotion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
		cutMotion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(cutMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
	
	public void endWorkflow(final CutMotion cutMotion, final String workflowHouseType, final String locale) throws ELSException {
    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(cutMotion);
		if(wfDetails != null && wfDetails.getId() != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch(Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				cutMotion.removeExistingWorkflowAttributes();
			}
		} else {
			cutMotion.removeExistingWorkflowAttributes();
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
	
    public String findYaadiDetailsText() {
		String yaadiDetailsText = "";
		if(this.getId()!=null && this.getYaadiLayingDate()!=null) {
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{this.getLocale()});
			parametersMap.put("cutMotionId", new String[]{this.getId().toString()});
			@SuppressWarnings("rawtypes")
			List yaadiDetailsTextResult = org.mkcl.els.domain.Query.findReport("CUTMOTION_YADI_DETAILS_TEXT", parametersMap);
			if(yaadiDetailsTextResult!=null && !yaadiDetailsTextResult.isEmpty()) {
				if(yaadiDetailsTextResult.get(0)!=null) {
					yaadiDetailsText = yaadiDetailsTextResult.get(0).toString();
				}
			}
		}		
		return yaadiDetailsText;
	}
    
    public List<MasterVO> findInternalMinistriesForDepartment() {
    	List<MasterVO> internalMinistries = new ArrayList<MasterVO>();
    	if(this.getNumber()!=null && this.getDepartment()!=null) {
    		Date onDate = new Date();
    		if(this.getSession().getEndDate().after(new Date())) {
    			onDate = this.getSession().getEndDate();
    		}
    		internalMinistries = getCutMotionRepository().findInternalMinistriesForDepartment(this.getSession(), this.getDeviceType(), this.getSubDepartment(), onDate, this.getLocale());
    	}
    	return internalMinistries;
    }
    
    public static List<Long> findCutMotionIDsHavingPendingReplyPostLastDateOfReplyReceiving(final HouseType houseType, final DeviceType deviceType, final SubDepartment subDepartment, final String locale) throws ELSException {
    	return getCutMotionRepository().findCutMotionIDsHavingPendingReplyPostLastDateOfReplyReceiving(houseType, deviceType, subDepartment, locale);
    }
	
	/**** Getter Setters ****/
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

	public Integer getInternalNumber() {
		return internalNumber;
	}

	public void setInternalNumber(Integer internalNumber) {
		this.internalNumber = internalNumber;
	}

	public BigDecimal getAmountToBeDeducted() {
		return amountToBeDeducted;
	}

	public void setAmountToBeDeducted(BigDecimal amountToBeDeducted) {
		this.amountToBeDeducted = amountToBeDeducted;
	}

	public BigDecimal getTotalAmoutDemanded() {
		return totalAmoutDemanded;
	}

	public void setTotalAmoutDemanded(BigDecimal totalAmoutDemanded) {
		this.totalAmoutDemanded = totalAmoutDemanded;
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

	public String getDemandNumber() {
		return demandNumber;
	}

	public void setDemandNumber(String demandNumber) {
		this.demandNumber = demandNumber;
	}

	public Integer getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	
	public String findCreatedBy() throws ELSException {
		if(this.getCreatedBy()!=null 
				&& !this.getCreatedBy().isEmpty()
				&& !this.getCreatedBy().contains("typist")) {
			return this.getCreatedBy();
		} else {
			User mUser = User.findbyNameBirthDate(
								this.getPrimaryMember().getFirstName(),
								this.getPrimaryMember().getMiddleName(),this.getPrimaryMember().getLastName(),
								this.getPrimaryMember().getBirthDate()
						 );
			Credential mCredential=mUser.getCredential();
			return mCredential.getUsername();
		}
	}
	
	public String findSupportedBy() throws ELSException {
		String supportedBy = "";
		List<SupportingMember> selectedSupportingMembers = this.getSupportingMembers();
		if(selectedSupportingMembers != null && !selectedSupportingMembers.isEmpty()){
			User mUser = null;
			Credential mCredential = null;
			StringBuffer supportingMembersUserNames = new StringBuffer("");
			for(SupportingMember i : selectedSupportingMembers){
				if(i.getDecisionStatus()!=null
						&& i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
					mUser = User.
							findbyNameBirthDate(i.getMember().getFirstName(),
									i.getMember().getMiddleName(),i.getMember().getLastName(),
									i.getMember().getBirthDate());
					mCredential=mUser.getCredential();
					supportingMembersUserNames.append(mCredential.getUsername() + ",");
				}
			}
			if(supportingMembersUserNames.length()>1) {
				supportingMembersUserNames.deleteCharAt(supportingMembersUserNames.length()-1);
			}
			supportedBy = supportingMembersUserNames.toString();
		}
		return supportedBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}

	public String getRevisedMainTitle() {
		return revisedMainTitle;
	}

	public void setRevisedMainTitle(String revisedMainTitle) {
		this.revisedMainTitle = revisedMainTitle;
	}

	public String getSecondaryTitle() {
		return secondaryTitle;
	}

	public void setSecondaryTitle(String secondaryTitle) {
		this.secondaryTitle = secondaryTitle;
	}

	public String getRevisedSecondaryTitle() {
		return revisedSecondaryTitle;
	}

	public void setRevisedSecondaryTitle(String revisedSecondaryTitle) {
		this.revisedSecondaryTitle = revisedSecondaryTitle;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getRevisedSubTitle() {
		return revisedSubTitle;
	}

	public void setRevisedSubTitle(String revisedSubTitle) {
		this.revisedSubTitle = revisedSubTitle;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Ministry getMinistry() {
		return ministry;
	}

	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	public Ministry getYaadiMinistry() {
		return yaadiMinistry;
	}

	public void setYaadiMinistry(Ministry yaadiMinistry) {
		this.yaadiMinistry = yaadiMinistry;
	}

	public Department getYaadiDepartment() {
		return yaadiDepartment;
	}

	public void setYaadiDepartment(Department yaadiDepartment) {
		this.yaadiDepartment = yaadiDepartment;
	}

	public SubDepartment getYaadiSubDepartment() {
		return yaadiSubDepartment;
	}

	public void setYaadiSubDepartment(SubDepartment yaadiSubDepartment) {
		this.yaadiSubDepartment = yaadiSubDepartment;
	}

	public Date getYaadiLayingDate() {
		return yaadiLayingDate;
	}

	public void setYaadiLayingDate(Date yaadiLayingDate) {
		this.yaadiLayingDate = yaadiLayingDate;
	}

	public List<CutMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<CutMotionDraft> drafts) {
		this.drafts = drafts;
	}

	public CutMotion getParent() {
		return parent;
	}

	public void setParent(CutMotion parent) {
		this.parent = parent;
	}

	public List<ClubbedEntity> getClubbedEntities() {
		return clubbedEntities;
	}

	public void setClubbedEntities(List<ClubbedEntity> clubbedEntities) {
		this.clubbedEntities = clubbedEntities;
	}
		
	public List<ReferenceUnit> getReferencedEntities() {
		return referencedEntities;
	}

	public void setReferencedEntities(List<ReferenceUnit> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
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
}