package org.mkcl.els.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.repository.AppropriationBillMotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="appropriation_bill_motions")
@JsonIgnoreProperties({"houseType", "session", "deviceType", "discussionDate", "recommendationStatus",
		"supportingMembers", "department", "drafts", "noticeContent", "subTitle", "creationDate"})
public class AppropriationBillMotion extends Device implements Serializable {
	
	/** The Constant serialVersionUID. */
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

	/** Amount Demanded **/
	@Column(precision=20, scale=2)
	private BigDecimal amountDemanded;

	/** The submission date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;

	/** The creation date. */
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
	@Column(length = 1000)
	private String mainTitle;

	/** The revisedMainTitle */
	@Column(length = 1000)
	private String revisedMainTitle;

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
	@JoinTable(name = "appropriationbillmotions_supportingmembers", 
	joinColumns = { @JoinColumn(name = "appropriationbillmotion_id", referencedColumnName = "id") }, 
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

	/**** DRAFTS ****/
	/** The drafts. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "appropriationbillmotions_drafts_association", 
	joinColumns = { @JoinColumn(name = "appropriationbillmotion_id", referencedColumnName = "id") }, 
	inverseJoinColumns = { @JoinColumn(name = "appropriationbillmotion_draft_id", referencedColumnName = "id") })
	private List<AppropriationBillMotionDraft> drafts;

	/** The reply */
	@Column(length = 30000)
	private String reply;

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
	
	/**** Synch variables for budgetary appropriation bill motion lower house****/
	private transient volatile static Integer APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE = 0;
	
	/**** Synch variables for budgetary appropriation bill motion upper house****/
	private transient volatile static Integer APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE = 0;
	
	/**** Synch variables for supplementary appropriation bill motion lower house****/
	private transient volatile static Integer APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE = 0;
	
	/**** Synch variables for supplementary appropriation bill motion upper house****/
	private transient volatile static Integer APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE = 0;
	
	@Autowired
	private transient AppropriationBillMotionRepository appropriationBillMotionRepository;
	
	
	/**** Constructors ****/
	/**
	 * Instantiates a new Appropriation Bill Motion.
	 */
	public AppropriationBillMotion() {
		super();
	}
	
	
	/**** Domain Methods ****/
	private static AppropriationBillMotionRepository getAppropriationBillMotionRepository() {
		AppropriationBillMotionRepository appropriationBillMotionRepository = new AppropriationBillMotion().appropriationBillMotionRepository;
		if (appropriationBillMotionRepository == null) {
			throw new IllegalStateException(
			"AppropriationBillMotionRepository has not been injected in AppropriationBillMotion Domain");
		}
		return appropriationBillMotionRepository;
	}

//	public static List<ClubbedEntity> findClubbedEntitiesByPosition(final AppropriationBillMotion motion) {
//		return getAppropriationBillMotionRepository().findClubbedEntitiesByPosition(motion);
//	}
	


	@Override
	public AppropriationBillMotion persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
//				synchronized (this) {
//					Integer number = AppropriationBillMotion.assignAppropriationBillMotionNo(this.getHouseType(),
//							this.getSession(), this.getDeviceType(),this.getLocale());
//					this.setNumber(number + 1);
//					addAppropriationBillMotionDraft();
//					return (AppropriationBillMotion)super.persist();
//				}
				synchronized (AppropriationBillMotion.class) {
                	
                	Integer number = null;
					try {
						String houseType = this.getHouseType().getType();
						String cutMotionType = this.getDeviceType().getType();
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {					
							if (AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(number);
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {					
							if (AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(number);
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(number);
							}
						} else if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {					
							if (AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(number);
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {					
							if (AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(number);
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {
	            			this.setNumber(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {
	            			this.setNumber(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            		}
	            		
	            		addAppropriationBillMotionDraft();
						return (AppropriationBillMotion)super.persist();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
			}else if(this.getNumber() != null){
				addAppropriationBillMotionDraft();
			}
		}
		return (AppropriationBillMotion) super.persist();
	}
	
	public static Boolean isExist(final Integer number, final DeviceType deviceType, final Session session, final String locale) {
		return getAppropriationBillMotionRepository().isExist(number, null, deviceType, session, locale);
	}

	public static Boolean isExist(final Integer number, final Long id, final DeviceType deviceType, final Session session, final String locale) {
		return getAppropriationBillMotionRepository().isExist(number, id, deviceType, session, locale);
	}
	
	public static Integer assignAppropriationBillMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) throws ELSException {
		return getAppropriationBillMotionRepository().assignAppropriationBillMotionNo(houseType,session,type,locale);
	}

	private void addAppropriationBillMotionDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE)) {
			AppropriationBillMotionDraft draft = new AppropriationBillMotionDraft();
			draft.setLocale(this.getLocale());
			if(this.getId()!=null) {
				draft.setDeviceId(this.getId().toString());
			}			
			draft.setRemarks(this.getRemarks());
			//draft.setClubbedEntities(this.getClubbedEntities());
			//draft.setReferencedEntities(this.getReferencedEntities());
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
			
			if(this.getRevisedSubTitle() != null){
				draft.setSubTitle(this.getRevisedSubTitle());
			}else{
				draft.setSubTitle(this.getSubTitle());
			}
			
			draft.setAmountDemanded(this.getAmountDemanded());
			draft.setDemandNumber(this.getDemandNumber());
			draft.setItemNumber(this.getItemNumber());
			draft.setPageNumber(this.getPageNumber());
			
			if(this.getId() != null) {
				AppropriationBillMotion motion = AppropriationBillMotion.findById(AppropriationBillMotion.class, this.getId());
				List<AppropriationBillMotionDraft> originalDrafts = motion.getDrafts();
				if(originalDrafts != null){
					originalDrafts.add(draft);
				}
				else{
					originalDrafts = new ArrayList<AppropriationBillMotionDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			}
			else {
				List<AppropriationBillMotionDraft> originalDrafts = new ArrayList<AppropriationBillMotionDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}

	@Override
	public AppropriationBillMotion merge() {
		AppropriationBillMotion motion = null;
		if(this.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_SUBMIT)) {
			if(this.getNumber() == null) {
//				synchronized (this) {
//					Integer number = AppropriationBillMotion.assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(),this.getLocale());
//					this.setNumber(number + 1);
//					addAppropriationBillMotionDraft();
//					motion = (AppropriationBillMotion) super.merge();
//				}
				synchronized (AppropriationBillMotion.class) {
                	
                	Integer number = null;
					try {
						String houseType = this.getHouseType().getType();
						String cutMotionType = this.getDeviceType().getType();
						if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {					
							if (AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(number);
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {					
							if (AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(number);
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(number);
							}
						} else if (houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {					
							if (AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(number);
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(number);
							}
						} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {					
							if (AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() == 0) {
								number = AppropriationBillMotion.
										assignAppropriationBillMotionNo(this.getHouseType(), this.getSession(), this.getDeviceType(), this.getLocale());
								AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(number);
								AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(number);
							}
						}
						
	            		if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {
	            			this.setNumber(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {
	            			this.setNumber(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.LOWER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse() + 1);
	            		} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE) 
								&& cutMotionType.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)) {
	            			this.setNumber(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse() + 1);
	            		}
	            		
						addAppropriationBillMotionDraft();
						motion = (AppropriationBillMotion) super.merge();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
					}
                }
			}else {
//				AppropriationBillMotion oldMotion = AppropriationBillMotion.findById(AppropriationBillMotion.class, this.getId());
//				if(this.getClubbedEntities() == null){
//					this.setClubbedEntities(oldMotion.getClubbedEntities());
//				}
//				if(this.getReferencedEntities() == null){
//					this.setReferencedEntities(oldMotion.getReferencedEntities());
//				}
				this.addAppropriationBillMotionDraft();
				motion = (AppropriationBillMotion) super.merge();
			}
		}
		
		if(motion != null) {
			return motion;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_INCOMPLETE) 
					|| this.getInternalStatus().getType().equals(ApplicationConstants.APPROPRIATIONBILLMOTION_COMPLETE)) {
				return (AppropriationBillMotion) super.merge();
			}else {
//				AppropriationBillMotion oldMotion = AppropriationBillMotion.findById(AppropriationBillMotion.class, this.getId());
//				if(this.getClubbedEntities() == null){
//					this.setClubbedEntities(oldMotion.getClubbedEntities());
//				}	
//				if(this.getReferencedEntities() == null){
//					this.setReferencedEntities(oldMotion.getReferencedEntities());
//				}
				this.addAppropriationBillMotionDraft();
				return (AppropriationBillMotion) super.merge();
			}
		}
	}

	public AppropriationBillMotion simpleMerge() {
		AppropriationBillMotion m = (AppropriationBillMotion) super.merge();
		return m;
	}
	
	/**** budgetary appropriation bill motion atomic value ****/
	public static void updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(Integer num){
		synchronized (AppropriationBillMotion.APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE) {
			AppropriationBillMotion.APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(){
		return AppropriationBillMotion.APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(Integer num){
		synchronized (AppropriationBillMotion.APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE) {
			AppropriationBillMotion.APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(){
		return AppropriationBillMotion.APPROPRIATIONBILLMOTION_BUDGETARY_CUR_NUM_UPPER_HOUSE;
	}
	
	/**** supplementary appropriation bill motion atomic value ****/
	public static void updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(Integer num){
		synchronized (AppropriationBillMotion.APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE) {
			AppropriationBillMotion.APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(){
		return AppropriationBillMotion.APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(Integer num){
		synchronized (AppropriationBillMotion.APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE) {
			AppropriationBillMotion.APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(){
		return AppropriationBillMotion.APPROPRIATIONBILLMOTION_SUPPLEMENTARY_CUR_NUM_UPPER_HOUSE;
	}
	
	public static org.mkcl.els.common.vo.Reference getCurNumber(final Session session, final DeviceType deviceType){
    	
    	org.mkcl.els.common.vo.Reference ref = new org.mkcl.els.common.vo.Reference();
    	String strHouseType = session.getHouse().getType().getType();
    	
    	if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	} else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(AppropriationBillMotion.getBudgetaryAppropriationBillMotionCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    		
    	} else if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberLowerHouse().toString());
    		ref.setId(ApplicationConstants.LOWER_HOUSE);
    		
    	} else if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)
    			&& deviceType.getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)){
    		
    		ref.setName(deviceType.getType());
			ref.setNumber(AppropriationBillMotion.getSupplementaryAppropriationBillMotionCurrentNumberUpperHouse().toString());
    		ref.setId(ApplicationConstants.UPPER_HOUSE);
    		
    	}
    	
    	return ref;
    }
    
    public static void updateCurNumber(final Integer num, final String houseType, final String device){
    	
    	if(device.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(num);
    			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(num);
    			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(num);
    		}  	
	    	
    	} else if(device.equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_SUPPLEMENTARY)){
    		if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
    			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberLowerHouse(num);
    			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberLowerHouse(num);
    		}
    		
    		if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
    			AppropriationBillMotion.updateSupplementaryAppropriationBillMotionCurrentNumberUpperHouse(num);
    			AppropriationBillMotion.updateBudgetaryAppropriationBillMotionCurrentNumberUpperHouse(num);
    		}	    	
	    	
    	}
    }

	public static List<RevisionHistoryVO> getRevisions(final Long appropriationBillMotionId, final String locale) {
		return getAppropriationBillMotionRepository().getRevisions(appropriationBillMotionId, locale);
	}

	public static List<AppropriationBillMotion> findAllByMember(final Session session,
			final Member primaryMember,
			final DeviceType appropriationBillMotionType,
			final Integer itemsCount,
			final String locale) {
		return getAppropriationBillMotionRepository().findAllByMember(session, primaryMember, appropriationBillMotionType,itemsCount, locale);
	}

	public String formatNumber() {
		if(getNumber()!=null){
			NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
			return format.format(this.getNumber());
		}else{
			return "";
		}
	}	

	public static List<AppropriationBillMotion> findAllByStatus(final Session session,
			final DeviceType cutMotionType,
			final Status internalStatus,
			final Integer itemsCount,
			final String locale) {
		return getAppropriationBillMotionRepository().findAllByStatus(session, cutMotionType, internalStatus, itemsCount, locale);
	}
    
    public List<MasterVO> findInternalMinistriesForDepartment() {
    	List<MasterVO> internalMinistries = new ArrayList<MasterVO>();
    	if(this.getNumber()!=null && this.getDepartment()!=null) {
    		Date onDate = new Date();
    		if(this.getSession().getEndDate().after(new Date())) {
    			onDate = this.getSession().getEndDate();
    		}
    		internalMinistries = getAppropriationBillMotionRepository().findInternalMinistriesForDepartment(this.getSession(), this.getDeviceType(), this.getSubDepartment(), onDate, this.getLocale());
    	}
    	return internalMinistries;
    }
    
    public void startWorkflow(final AppropriationBillMotion appropriationBillMotion, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
    	//end current workflow if exists
		appropriationBillMotion.endWorkflow(appropriationBillMotion, workflowHouseType, locale);
    	//update motion statuses as per the workflow status
		appropriationBillMotion.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
    	//find required workflow from the status
    	Workflow workflow = Workflow.findByStatus(status, locale);
    	//start required workflow
		WorkflowDetails.startProcessAtGivenLevel(appropriationBillMotion, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
    }
	
	public void endWorkflow(final AppropriationBillMotion appropriationBillMotion, final String workflowHouseType, final String locale) throws ELSException {
    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(appropriationBillMotion);
		if(wfDetails != null && wfDetails.getId() != null) {
			try {
				WorkflowDetails.endProcess(wfDetails);
			} catch(Exception e) {
				wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
				wfDetails.setCompletionTime(new Date());
				wfDetails.merge();
			} finally {
				appropriationBillMotion.removeExistingWorkflowAttributes();
			}
		} else {
			appropriationBillMotion.removeExistingWorkflowAttributes();
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

	public BigDecimal getAmountDemanded() {
		return amountDemanded;
	}

	public void setAmountDemanded(BigDecimal amountDemanded) {
		this.amountDemanded = amountDemanded;
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

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public List<AppropriationBillMotionDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<AppropriationBillMotionDraft> drafts) {
		this.drafts = drafts;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
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
	
	public static void supportingMemberWorkflowDeletion(final AppropriationBillMotion appropriationBillMotion) {
    	if(appropriationBillMotion!=null && appropriationBillMotion.getId()>0) {
    		if(anySupportingMembersWorkflows(appropriationBillMotion)) {
    			deleteSupportingMembersWorkflows(appropriationBillMotion);
    		}
    	}
    }
    
    public static boolean anySupportingMembersWorkflows(final AppropriationBillMotion appropriationBillMotion) {
		List<SupportingMember> supportingMembers = appropriationBillMotion.getSupportingMembers();
		if(supportingMembers!=null && supportingMembers.size()>0) {
			for(SupportingMember sm :supportingMembers) {
				if(sm.getWorkflowDetailsId()!=null && sm.getWorkflowDetailsId().trim().length()>0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean deleteSupportingMembersWorkflows(final AppropriationBillMotion appropriationBillMotion) {
		List<Long> workflowDetailsList=new ArrayList<Long>();
		if(appropriationBillMotion!=null && appropriationBillMotion.getId()>0 && appropriationBillMotion.getSupportingMembers()!=null 
				&& appropriationBillMotion.getSupportingMembers().size()>0) {
			List<SupportingMember> supportingMembers = appropriationBillMotion.getSupportingMembers();
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