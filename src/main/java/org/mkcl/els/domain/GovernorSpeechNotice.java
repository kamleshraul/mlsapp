package org.mkcl.els.domain;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.*;
import javax.persistence.*;
import org.codehaus.jackson.annotate.*;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.DateUtil;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.domain.Query;
import org.mkcl.els.repository.GovernorSpeechNoticeRepository;

import java.util.*;


@Configurable
@Entity
@Table(name="governorspeechnotice")
@JsonIgnoreProperties({"houseType", "session", "type", "drafts"})
public class GovernorSpeechNotice extends Device implements Serializable {

	/** The constant serialVersionUID **/
	private static final long serialVersionUID= 1L ;
	
	/** Basic Attribute **/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="housetype_id")
	private HouseType houseType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType type;
	
	private Integer number;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
    private Member primaryMember;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(length=100)
	private String createdBy;
	
	@Column(length=30000)
	private String subject;
	
	@Column(length=30000)
	private String revisedSubject;
	
	@Column(length=30000)
	private String noticeContent;
	
	@Column(length=30000)
	private String revisedNoticeContent;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date editedOn;

	@Column(length=1000)
	private String editedBy;
	
	@Column(length=1000)
	private String editedAs;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_id")
	private Status status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="internalstatus_id")
	private Status internalStatus;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recommendationstatus_id")
	private Status recommendationStatus; 
	
	@Column(length=30000)
	private String remarks;
	
	@Column(length=30000)
	private String rejectionReason;
	
	private String workflowStarted;
	
	private String actor;
	
	private String localizedActorName;
	
	private String endFlag;
	
	private String level;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOn;	

	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOn;
	
	private Long workflowDetailsId;
	
	// DRAFTS 
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="governorspeechnotices_drafts_association",
	           joinColumns= {@JoinColumn(name="governorspeechnotice_id", referencedColumnName="id")},
	           inverseJoinColumns={@JoinColumn(name="governorspeechnotice_draft_id", referencedColumnName="id")})
	private List<GovernorSpeechNoticeDraft> drafts;
	
	/**** Synch variables for notice lower house****/
	private transient volatile static Integer NOTICES_GOVERNOR_SPEECH_CUR_NUM_LOWER_HOUSE = 0;
	
	/**** Synch variables for notice upper house****/
	private transient volatile static Integer NOTICES_GOVERNOR_SPEECH_CUR_NUM_UPPER_HOUSE = 0;

	
	// Governor Speech Notice Repository
	@Autowired 
	private transient GovernorSpeechNoticeRepository governorSpeechNoticeRepository;
	
	// Constructors 
	public GovernorSpeechNotice() {
		super();
	}
	
	
	/** END **/

	private static GovernorSpeechNoticeRepository getGovernorSpeechNoticeRepository() {
		GovernorSpeechNoticeRepository governorSpeechNoticeRepository = new GovernorSpeechNotice().governorSpeechNoticeRepository;
		if(governorSpeechNoticeRepository == null) {
			throw new IllegalStateException("GovernorSpeechNoticeRepository has not been injected in GovernorSpeechNotice Domain");
		}
		return governorSpeechNoticeRepository;
	}
	
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

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
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

	public Long getWorkflowDetailsId() {
		return workflowDetailsId;
	}

	public void setWorkflowDetailsId(Long workflowDetailsId) {
		this.workflowDetailsId = workflowDetailsId;
	}

	public List<GovernorSpeechNoticeDraft> getDrafts() {
		return drafts;
	}

	public void setDrafts(List<GovernorSpeechNoticeDraft> drafts) {
		this.drafts = drafts;
	}
	
	
	public String formatNumber() {
		if(getNumber()!=null){
			return FormaterUtil.formatNumberNoGrouping(this.getNumber(), this.getLocale());			
		}else{
			return "";
		}
	}
	
	public GovernorSpeechNotice simpleMerge() {
		GovernorSpeechNotice governorSpeechNotice = (GovernorSpeechNotice) super.merge();
        return governorSpeechNotice;
    }
	

	
	public static void updateGovernorSpeechCurrentNumberLowerHouse(Integer num){
		synchronized (GovernorSpeechNotice.NOTICES_GOVERNOR_SPEECH_CUR_NUM_LOWER_HOUSE) {
			GovernorSpeechNotice.NOTICES_GOVERNOR_SPEECH_CUR_NUM_LOWER_HOUSE = num;								
		}
	}

	public static synchronized Integer getGovernorSpeechCurrentNumberLowerHouse(){
		return GovernorSpeechNotice.NOTICES_GOVERNOR_SPEECH_CUR_NUM_LOWER_HOUSE;
	}
	
	public static void updateGovernorSpeechCurrentNumberUpperHouse(Integer num){
		synchronized (GovernorSpeechNotice.NOTICES_GOVERNOR_SPEECH_CUR_NUM_UPPER_HOUSE) {
			GovernorSpeechNotice.NOTICES_GOVERNOR_SPEECH_CUR_NUM_UPPER_HOUSE = num;								
		}
	}

	public static synchronized Integer getGovernorSpeechCurrentNumberUpperHouse(){
		return GovernorSpeechNotice.NOTICES_GOVERNOR_SPEECH_CUR_NUM_UPPER_HOUSE;
	}
	
    /*** Access to the Repository*****/
	
	@Override
	public GovernorSpeechNotice persist() {
		if(this.getStatus().getType().equals(ApplicationConstants.GOVERNORSPEECHNOTICE_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (Motion.class) {
					
					Integer number = null;
					String houseType = this.getHouseType().getType();
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
						if(GovernorSpeechNotice.getGovernorSpeechCurrentNumberLowerHouse() == 0){
							number = GovernorSpeechNotice.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							GovernorSpeechNotice.updateGovernorSpeechCurrentNumberLowerHouse(number);
						}
					}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(GovernorSpeechNotice.getGovernorSpeechCurrentNumberUpperHouse() == 0){
							number = GovernorSpeechNotice.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							GovernorSpeechNotice.updateGovernorSpeechCurrentNumberUpperHouse(number);
						}
					}
					
					/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
					if(persistPostBallotNumber > 0){
						this.setPostBallotNumber(persistPostBallotNumber + 1);
					}*/
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(GovernorSpeechNotice.getGovernorSpeechCurrentNumberLowerHouse() + 1);
            			this.setSubmissionDate(new Date());
            			GovernorSpeechNotice.updateGovernorSpeechCurrentNumberLowerHouse(GovernorSpeechNotice.getGovernorSpeechCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(GovernorSpeechNotice.getGovernorSpeechCurrentNumberUpperHouse() + 1);
            			this.setSubmissionDate(new Date());
            			GovernorSpeechNotice.updateGovernorSpeechCurrentNumberUpperHouse(GovernorSpeechNotice.getGovernorSpeechCurrentNumberUpperHouse() + 1);
            		}
					addNoticeDraft();
					return (GovernorSpeechNotice)super.persist();
				}
			}else if(this.getNumber()!=null){
				addNoticeDraft();
            }
		}
		return (GovernorSpeechNotice) super.persist();
	}
	
	@Override
	public GovernorSpeechNotice merge() {
		GovernorSpeechNotice notice = null;
		if(this.getInternalStatus().getType().equals(
				ApplicationConstants.GOVERNORSPEECHNOTICE_SUBMIT)) {
			if(this.getNumber() == null) {
				synchronized (GovernorSpeechNotice.class) {
					Integer number = null;
					//TODO: may needed for maintaining postBallotNumber in other batch motions  
					/*Integer mergePostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
					if(mergePostBallotNumber > 0){
						this.setPostBallotNumber(mergePostBallotNumber + 1);
					}*/
					
					String houseType = this.getHouseType().getType();
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
						if(GovernorSpeechNotice.getGovernorSpeechCurrentNumberLowerHouse() == 0){
							number = GovernorSpeechNotice.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							GovernorSpeechNotice.updateGovernorSpeechCurrentNumberLowerHouse(number);
						}
					}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
						if(GovernorSpeechNotice.getGovernorSpeechCurrentNumberUpperHouse() == 0){
							number = GovernorSpeechNotice.assignMotionNo(this.getHouseType(),
									this.getSession(), this.getType(),this.getLocale());
							GovernorSpeechNotice.updateGovernorSpeechCurrentNumberUpperHouse(number);
						}
					}
					
					/*Integer persistPostBallotNumber = Motion.findMaxPostBallotNo(this.getHouseType(), this.getSession(), this.getType(), this.getLocale()); 
					if(persistPostBallotNumber > 0){
						this.setPostBallotNumber(persistPostBallotNumber + 1);
					}*/
					
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
            			this.setNumber(GovernorSpeechNotice.getGovernorSpeechCurrentNumberLowerHouse() + 1);
            			this.setSubmissionDate(new Date());
            			GovernorSpeechNotice.updateGovernorSpeechCurrentNumberLowerHouse(GovernorSpeechNotice.getGovernorSpeechCurrentNumberLowerHouse() + 1);
            		}else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)){
            			this.setNumber(GovernorSpeechNotice.getGovernorSpeechCurrentNumberUpperHouse() + 1);
            			this.setSubmissionDate(new Date());
            			GovernorSpeechNotice.updateGovernorSpeechCurrentNumberUpperHouse(GovernorSpeechNotice.getGovernorSpeechCurrentNumberUpperHouse() + 1);
            		}
					addNoticeDraft();
					notice = (GovernorSpeechNotice) super.merge();
				}
			}else {
				Motion oldMotion = Motion.findById(Motion.class, this.getId());
//				if(this.getClubbedEntities() == null){
//					this.setClubbedEntities(oldMotion.getClubbedEntities());
//				}
//				if(this.getReferencedUnits() == null){
//					this.setReferencedUnits(oldMotion.getReferencedUnits());
//				}
				this.addNoticeDraft();
				notice = (GovernorSpeechNotice) super.merge();
			}
		}else if(this.getInternalStatus().getType().
        		equals(ApplicationConstants.GOVERNORSPEECHNOTICE_INCOMPLETE) 
            	|| 
            	this.getInternalStatus().getType().
            	equals(ApplicationConstants.GOVERNORSPEECHNOTICE_COMPLETE)){
        	return (GovernorSpeechNotice) super.merge();
        }
		
		if(notice != null) {
			return notice;
		}else {
			if(this.getInternalStatus().getType().equals(ApplicationConstants.GOVERNORSPEECHNOTICE_INCOMPLETE) 
					|| 
					this.getInternalStatus().getType().equals(ApplicationConstants.GOVERNORSPEECHNOTICE_COMPLETE)) {
				return (GovernorSpeechNotice) super.merge();
			}else {
				Motion oldMotion = Motion.findById(Motion.class, this.getId());
//				if(this.getClubbedEntities() == null){
//					this.setClubbedEntities(oldMotion.getClubbedEntities());
//				}	
//				if(this.getReferencedUnits() == null){
//					this.setReferencedUnits(oldMotion.getReferencedUnits());
//				}
				this.addNoticeDraft();
				return (GovernorSpeechNotice) super.merge();
			}
		}
	}

	public static Integer assignMotionNo(final HouseType houseType,
			final Session session,final DeviceType type,final String locale) {
		return getGovernorSpeechNoticeRepository().assignMotionNo(houseType,
				session,type,locale);
	}
	
	public void addNoticeDraft() {
		if(! this.getStatus().getType().equals(ApplicationConstants.GOVERNORSPEECHNOTICE_INCOMPLETE) &&
				! this.getStatus().getType().equals(ApplicationConstants.GOVERNORSPEECHNOTICE_COMPLETE)) {
			GovernorSpeechNoticeDraft draft = new GovernorSpeechNoticeDraft();
			draft.setLocale(this.getLocale());
			draft.setRemarks(this.getRemarks());
//			draft.setParent(this.getParent());
//			draft.setClubbedEntities(this.getClubbedEntities());
//			draft.setReferencedUnits(referencedUnits);
			draft.setEditedAs(this.getEditedAs());
			draft.setEditedBy(this.getEditedBy());
//            draft.setEditedByActualName(this.getEditedByActualName());
			draft.setEditedOn(this.getEditedOn());	            
//			draft.setMinistry(this.getMinistry());
//			draft.setDepartment(this.getDepartment());
//			draft.setSubDepartment(this.getSubDepartment());	            
			draft.setStatus(this.getStatus());
			draft.setInternalStatus(this.getInternalStatus());
			draft.setRecommendationStatus(this.getRecommendationStatus());        	
        	if(this.getRevisedNoticeContent()!= null && this.getRevisedSubject() != null){
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setSubject(this.getRevisedSubject());                
			}
			else if(this.getRevisedNoticeContent() != null){
				draft.setNoticeContent(this.getRevisedNoticeContent());
				draft.setSubject(this.getSubject());
			}
			else if(this.getRevisedSubject()!=null){
				draft.setNoticeContent(this.getNoticeContent());
				draft.setSubject(this.getRevisedSubject());
			}
			else{
				draft.setNoticeContent(this.getNoticeContent());
				draft.setSubject(this.getSubject());
			}
//			draft.setReply(this.getReply());
//			draft.setActualEditedByUserName(this.getCurrentUser().getActualUsername());
//			draft.setMotionId(this.getId());	            
			if(this.getId() != null) {
				GovernorSpeechNotice notice = GovernorSpeechNotice.findById(GovernorSpeechNotice.class, this.getId());
				List<GovernorSpeechNoticeDraft> originalDrafts = notice.getDrafts();
				if(originalDrafts != null){
					originalDrafts.add(draft);
				}
				else{
					originalDrafts = new ArrayList<GovernorSpeechNoticeDraft>();
					originalDrafts.add(draft);
				}
				this.setDrafts(originalDrafts);
			}
			else {
				List<GovernorSpeechNoticeDraft> originalDrafts = new ArrayList<GovernorSpeechNoticeDraft>();
				originalDrafts.add(draft);
				this.setDrafts(originalDrafts);
			}
		}
	}
	
	public GovernorSpeechNoticeDraft findPreviousDraft() {
		return getGovernorSpeechNoticeRepository().findPreviousDraft(this.getId());
	}
	
	 public static Boolean isDuplicateNumberExist(Integer number, Long id, String locale) {
			return getGovernorSpeechNoticeRepository().isDuplicateNumberExist(number, id, locale);
	}
	 
	 public static Integer findCountOfNoticesBySpecificMemberAndSecificSession(final Session session, final String createdBy, final String locale) {
		return getGovernorSpeechNoticeRepository().findCountOfNoticesBySpecificMemberAndSecificSession(session, createdBy, locale);
	 }
	 
	 public Workflow findWorkflowFromStatus() throws ELSException {
		 
		 Workflow workflow = null;
		 
		 Status internalStatus = this.getInternalStatus();
		 
		 workflow = Workflow.findByStatus(internalStatus, this.getLocale());
		 
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
	
	 public void startWorkflow(final GovernorSpeechNotice governorSpeechNotice, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, final String locale) throws ELSException {
	    	//end current workflow if exists
			governorSpeechNotice.endWorkflow(governorSpeechNotice, workflowHouseType, locale);
	    	//update motion statuses as per the workflow status
			governorSpeechNotice.updateForInitFlow(status, userGroupType, isFlowOnRecomStatusAfterFinalDecision, locale);
	    	//find required workflow from the status
	    	Workflow workflow = Workflow.findByStatus(status, locale);
	    	//start required workflow
			WorkflowDetails.startProcessAtGivenLevel(governorSpeechNotice, ApplicationConstants.APPROVAL_WORKFLOW, workflow, userGroupType, level, locale);
	    }
	 
	 public void endWorkflow(final GovernorSpeechNotice governorSpeechNotice, final String workflowHouseType, final String locale) throws ELSException {
	    	WorkflowDetails wfDetails = WorkflowDetails.findCurrentWorkflowDetail(governorSpeechNotice);
			if(wfDetails != null && wfDetails.getId() != null) {
				try {
					WorkflowDetails.endProcess(wfDetails);
				} catch(Exception e) {
					wfDetails.setStatus(ApplicationConstants.MYTASK_COMPLETED);
					wfDetails.setCompletionTime(new Date());
					wfDetails.merge();
				} finally {
					governorSpeechNotice.removeExistingWorkflowAttributes();
				}
			} else {
				governorSpeechNotice.removeExistingWorkflowAttributes();
			}
		}
	 
	 public static List<RevisionHistoryVO> getRevisions(final Long governorSpeechNoticeId, final String locale) {
	        return getGovernorSpeechNoticeRepository().getRevisions(governorSpeechNoticeId,locale);
	    }
	 
	 /*****************/
	 
	/**** Vaidations *****/
	 
	public static boolean isAllowedForSubmission(final GovernorSpeechNotice motion, final Date date){
	    	
	    	Session session = motion.getSession();
	    	
	    	if(motion != null){
	    		if(session != null){
	    			Date startDate = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_submissionStartTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
	    			Date endDate = FormaterUtil.formatStringToDate(session.getParameter(motion.getType().getType() + "_submissionEndTime"), ApplicationConstants.SERVER_DATETIMEFORMAT);
	    			
	    			if(date.compareTo(startDate)>=0 && date.compareTo(endDate)<=0){
	    				return true;
	    			}
	    		}
	    	}
	    	
	    	return false;
	    }
	
	public static boolean isMaxAllowedNotices(final GovernorSpeechNotice notice) {
		
		boolean flag = false;
	
		Session session = notice.getSession();
		
	   	if(notice != null) {
		    
	   		if(session != null) {
	   			
	   			String numberOfNotices = session.getParameter(notice.getType().getType()+"_numberOfAcceptingNotices");
		    	int numberOfNoticesSubmittedByMember = GovernorSpeechNotice.findCountOfNoticesBySpecificMemberAndSecificSession(session,notice.getCreatedBy(),notice.getLocale());
		    	  
		    	  if(numberOfNotices != null) {
		    		  if(numberOfNoticesSubmittedByMember <= Integer.parseInt(numberOfNotices)) {
			    		  flag = false;
			    	  } else {
			    		  flag = true;
			    	  }  
		    	  } else {
		    		  flag = false;
		    	  }
	   			
	   		}
			
		  }
		
		return flag;
	}
	
	 
	/*********************/
}
