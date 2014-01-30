/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Resolution.java
 * Created On: Mar 2, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.ResolutionRevisionVO;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.repository.ResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
// TODO: Auto-generated Javadoc

/**
 * The Class Resolution.
 *
 * @author anandk
 * @author vikasg
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="resolutions")
@JsonIgnoreProperties({"houseType", "session", "type","recommendationStatusLowerHouse","recommendationStatusUpperHouse", "ballotStatus", 
	  "drafts", "referencedResolution","ruleForDiscussionDate","discussionStatus"})
public class Resolution extends Device implements Serializable{
	
	/** The Constant serialVersionUID. */
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

    /** The discussion date. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date discussionDate;
	
   
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
    
    /** 
     * The status. Refers to various final status viz, SUBMITTED,
     * ADMITTED, REJECTED 
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="lowerhouse_status_id")
    private Status statusLowerHouse;
    
    /** The status upper house. */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="upperhouse_status_id")
    private Status statusUpperHouse;

    /** 
     * The internal status. Refers to status assigned to a resolution
     * during the Workflow
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="lowerhouse_internalstatus_id")
    private Status internalStatusLowerHouse;
    
    /** The internal status upper house. */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="upperhouse_internalstatus_id")
    private Status internalStatusUpperHouse;

    /** The recommendation status. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="lowerhouse_recommendationstatus_id")
    private Status recommendationStatusLowerHouse;
    
    /** The recommendation status upper house. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="upperhouse_recommendationstatus_id")
    private Status recommendationStatusUpperHouse;
    
    /** If a resolution is balloted then its balloted status is set to balloted. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ballotstatus_id")
    private Status ballotStatus;
    
    /** If resolution is selected for discussion *. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="discussionstatus_id")
    private Status discussionStatus;
   
   
    /** The remarks. */
    @Column(length=30000)
    private String remarks;
    
    
    /** The  member. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    
    
    /** The ministry. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ministry_id")
    private Ministry ministry;

   
    /** The department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="department_id")
    private Department department;

    
    /** The sub department. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="subdepartment_id")
    private SubDepartment subDepartment;

    
    
    /** The drafts. */
    @ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinTable(name="resolutions_drafts_association", 
    		joinColumns={@JoinColumn(name="resolution_id", referencedColumnName="id")}, 
    		inverseJoinColumns={@JoinColumn(name="resolution_draft_id", referencedColumnName="id")})
    private List<ResolutionDraft> drafts;   
    
    /** The referenced resolutions. */
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private ReferencedEntity referencedResolution;    
   
    /** The mark as accepted. */
    private Boolean markAsAccepted;
    
    /** *****************For Non Official Factual Position**************************************. */
    @Column(length=30000)
    private String factualPosition;
    
    /** The date of factual position receiving. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDateOfFactualPositionReceiving;
    
    /** The number of days for factual position receiving. */
    private Integer numberOfDaysForFactualPositionReceiving;
    
    /** The questions asked in factual position. */
    @Column(length=30000)
    private String questionsAskedInFactualPosition;
    
    /** The house type for gr. */
    @Transient
    private String houseTypeForGR;

    /** The rejection reason. */
    @Column(length=30000)
    private String rejectionReason;
   
    /** ** To be used in case of bulk submission and workflows***. */
	private String workflowStartedLowerHouse;
	
	/** The workflow started upper house. */
	private String workflowStartedUpperHouse;

	/** The actor lower house. */
	private String actorLowerHouse;
	
	/** The actor upper house. */
	private String actorUpperHouse;
	
	/** The localized actor name lower house. */
	private String localizedActorNameLowerHouse;
	
	/** The localized actor name upper house. */
	private String localizedActorNameUpperHouse;

	/** The end flag lower house. */
	private String endFlagLowerHouse;
	
	/** The end flag upper house. */
	private String endFlagUpperHouse;
	
	/** The level lower house. */
	private String levelLowerHouse;
	
	/** The level upper house. */
	private String levelUpperHouse;
	
	/** The workflow started on lower house. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOnLowerHouse;
	
	/** The workflow started on upper house. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowStartedOnUpperHouse;	
	
	/** The task received on lower house. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOnLowerHouse;
	
	/** The task received on upper house. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date taskReceivedOnUpperHouse;
	
	/** The bulk submitted. */
	private boolean bulkSubmitted=false;
	
	/** The workflow details id lower house. */
	private Long workflowDetailsIdLowerHouse;
	
	/** The workflow details id upper house. */
	private Long workflowDetailsIdUpperHouse;
	
	/** The file lower house. */
	private Integer fileLowerHouse;
	
	/** The file upper house. */
	private Integer fileUpperHouse;

	/** The file index lower house. */
	private Integer fileIndexLowerHouse;
	
	/** The file index upper house. */
	private Integer fileIndexUpperHouse;

	/** The file sent lower house. */
	private Boolean fileSentLowerHouse;
	
	/** The file sent upper house. */
	private Boolean fileSentUpperHouse;
    
    /**** For Government Resolution ****/
    /** The rule for discussion date in GR. */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="rule_id")
    private Rule ruleForDiscussionDate;
    
    @OneToOne
    private VotingDetail votingDetail;
    
    /** The resolution repository. */
    @Autowired
    private transient ResolutionRepository resolutionRepository;
    

    /**** Constructors ****/

    /**
     * Instantiates a new resolution.
     */
    public Resolution() {
		super();
	}

    /**** Domain methods ****/
    
	/**
	 * Gets the resolution repository.
	 * 
	 * @return the resolution repository
	 */
    private static ResolutionRepository getResolutionRepository() {
    	ResolutionRepository resolutionRepository = new Resolution().resolutionRepository;
        if (resolutionRepository == null) {
            throw new IllegalStateException(
            	"ResolutionRepository has not been injected in Resolution Domain");
        }
        return resolutionRepository;
    }

    /* (non-Javadoc)
     * @see org.mkcl.els.domain.BaseDomain#persist()
     */
    @Override
    public Resolution persist() {
    	try{
	    	if(this.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
	    		 if(this.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)) {
	    	            if(this.getNumber() == null) {
	    	                synchronized (this) {
	    	                	if(this.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
	    	                		Integer number = Resolution.assignResolutionNo(this.getHouseType(),
	    	                                this.getSession(), this.getType(),this.getLocale());
	    	                        this.setNumber(number + 1);
	    	                	} 
	    	                	else {
	    	                		Integer count= Resolution.getMemberResolutionCountByNumber(this.getMember().getId(),this.getSession().getId(),this.getLocale());
	        	                    if(count<5){
	        	                    	Integer number = Resolution.assignResolutionNo(this.getHouseType(),
	        	                                this.getSession(), this.getType(),this.getLocale());
	        	                        this.setNumber(number + 1);
	        	                    }
	    	                	}    	                	
	    	                	addResolutionDraft();
	    	                    return (Resolution)super.persist();
	    	                }
	    	            }
	    	        }
	    	}else if(this.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
	    		if(this.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)) {
		            if(this.getNumber() == null) {
		                synchronized (this) {
		                	if(this.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
		                		Integer number = Resolution.assignResolutionNo(this.getHouseType(),
		                                this.getSession(), this.getType(),this.getLocale());
		                        this.setNumber(number + 1);
		                	} 
		                	else {
		                		Integer count= Resolution.getMemberResolutionCountByNumber(this.getMember().getId(),this.getSession().getId(),this.getLocale());
	    	                    if(count<5){
	    	                    	Integer number = Resolution.assignResolutionNo(this.getHouseType(),
	    	                                this.getSession(), this.getType(),this.getLocale());
	    	                        this.setNumber(number + 1);
	    	                    }
		                	}
		                	addResolutionDraft();
		                    return (Resolution)super.persist();
		                }
		            }
		        }
	    	}
    	}catch (ELSException e) {
    		e.printStackTrace();
		}
       
        return (Resolution) super.persist();
    }
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.domain.BaseDomain#merge()
	 */
	@Override
    public Resolution merge() {
		
        Resolution resolution = null;
        try{
	        if(this.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
	        	if(this.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)||
	        			this.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_SYSTEM_ASSISTANT_PROCESSED)) {
	                if(this.getNumber() == null) {
	                    synchronized (this) {
	                    	if(this.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
		                		Integer number = Resolution.assignResolutionNo(this.getHouseType(),
		                                this.getSession(), this.getType(),this.getLocale());
		                        this.setNumber(number + 1);
		                	} 
		                	else {
		                		Integer count= Resolution.getMemberResolutionCountByNumber(this.getMember().getId(),this.getSession().getId(),this.getLocale());
	    	                    if(count<5){
	    	                    	Integer number = Resolution.assignResolutionNo(this.getHouseType(),
	    	                                this.getSession(), this.getType(),this.getLocale());
	    	                        this.setNumber(number + 1);
	    	                    }
		                	}
	                        addResolutionDraft();
	                        resolution = (Resolution) super.merge();
	                    }
	                }
	                else {
	                	Resolution oldResolution = Resolution.findById(Resolution.class, this.getId());
	                	if(this.getReferencedResolution() == null){
	                		this.setReferencedResolution(oldResolution.getReferencedResolution());
	                	}
	                	this.addResolutionDraft();
	                	resolution = (Resolution) super.merge();
	                }
	            }
	            if(resolution != null) {
	                return resolution;
	            }
	            else {
	                if(this.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE) 
	                	|| 
	                	this.getInternalStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)) {
	                    return (Resolution) super.merge();
	                }
	                else {
	                	Resolution oldResolution = Resolution.findById(Resolution.class, this.getId());
	                	if(this.getReferencedResolution()== null){
	                		this.setReferencedResolution(oldResolution.getReferencedResolution());
	                	}
	                    this.addResolutionDraft();
	                    return (Resolution) super.merge();
	                }
	            }
	        }else if(this.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
	        	if(this.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_SUBMIT)) {
	                if(this.getNumber() == null) {
	                    synchronized (this) {
	                    	if(this.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
		                		Integer number = Resolution.assignResolutionNo(this.getHouseType(),
		                                this.getSession(), this.getType(),this.getLocale());
		                        this.setNumber(number + 1);
		                	} 
		                	else {
		                		Integer count= Resolution.getMemberResolutionCountByNumber(this.getMember().getId(),this.getSession().getId(),this.getLocale());
	    	                    if(count<5){
	    	                    	Integer number = Resolution.assignResolutionNo(this.getHouseType(),
	    	                                this.getSession(), this.getType(),this.getLocale());
	    	                        this.setNumber(number + 1);
	    	                    }
		                	}
	                        addResolutionDraft();
	                        resolution = (Resolution) super.merge();
	                    }
	                }
	                else {
	                	Resolution oldResolution = Resolution.findById(Resolution.class, this.getId());
	                	if(this.getReferencedResolution() == null){
	                		this.setReferencedResolution(oldResolution.getReferencedResolution());
	                	}
	                	this.addResolutionDraft();
	                	resolution = (Resolution) super.merge();
	                }
	            }
	            if(resolution != null) {
	                return resolution;
	            }
	            else {
	                if(this.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE) 
	                	|| 
	                	this.getInternalStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)) {
	                    return (Resolution) super.merge();
	                }
	                else {
	                	Resolution oldResolution = Resolution.findById(Resolution.class, this.getId());
	                	if(this.getReferencedResolution()== null){
	                		this.setReferencedResolution(oldResolution.getReferencedResolution());
	                	}
	                    this.addResolutionDraft();
	                    return (Resolution) super.merge();
	                }
	            }
	        }
        }catch (ELSException e) {
        	e.printStackTrace();
		}
        return (Resolution) super.merge();
        
       }
    
    
    /**
     * Simple merge.
     *
     * @return the resolution
     * @author anandk
     * @since v1.0.0
     */
    public Resolution simpleMerge() {
        Resolution r = (Resolution) super.merge();
        return r;
    }
    
   
    /**
     * Adds the resolution draft.
     */
    private void addResolutionDraft() {
    	if(this.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
    		  if(! this.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE) &&
    	        		! this.getStatusLowerHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)) {
    	            ResolutionDraft draft = new ResolutionDraft();
    	            
    	            draft.setLocale(this.getLocale());
    	            draft.setRemarks(this.getRemarks());
    	            
    	            
    	            draft.setReferencedResolution(this.getReferencedResolution());
    	            
    	            draft.setEditedAs(this.getEditedAs());
    	            draft.setEditedBy(this.getEditedBy());
    	            draft.setEditedOn(this.getEditedOn());
    	            
    	            
    	            draft.setMinistry(this.getMinistry());
    	            draft.setDepartment(this.getDepartment());
    	            draft.setSubDepartment(this.getSubDepartment());
    	            
    	            if(!this.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
    	            	draft.setStatus(this.getStatusLowerHouse());
        	            draft.setInternalStatus(this.getInternalStatusLowerHouse());
        	            draft.setRecommendationStatus(this.getRecommendationStatusLowerHouse());
    	            }    	            
    	            
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
    		         }else{
    		            	draft.setNoticeContent(this.getNoticeContent());
    		                draft.setSubject(this.getSubject());
    		         }    	            
    	            
    	             if(this.getId() != null) {
    	                Resolution resolution = Resolution.findById(Resolution.class, this.getId());
    	                List<ResolutionDraft> originalDrafts = resolution.getDrafts();
    	                if(originalDrafts != null){
    	                    originalDrafts.add(draft);
    	                }
    	                else{
    	                    originalDrafts = new ArrayList<ResolutionDraft>();
    	                    originalDrafts.add(draft);
    	                }
    	                this.setDrafts(originalDrafts);
    	            }
    	            else {
    	            	List<ResolutionDraft> originalDrafts = new ArrayList<ResolutionDraft>();
    	                originalDrafts.add(draft);
    	                this.setDrafts(originalDrafts);
    	            }
    	        }
    	}if(this.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
    		if(! this.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_INCOMPLETE) &&
	        		! this.getStatusUpperHouse().getType().equals(ApplicationConstants.RESOLUTION_COMPLETE)) {
	            ResolutionDraft draft = new ResolutionDraft();
	            
	            draft.setLocale(this.getLocale());
	            draft.setRemarks(this.getRemarks());
	            
	            
	            draft.setReferencedResolution(this.getReferencedResolution());
	            
	            draft.setEditedAs(this.getEditedAs());
	            draft.setEditedBy(this.getEditedBy());
	            draft.setEditedOn(this.getEditedOn());
	            
	            
	            draft.setMinistry(this.getMinistry());
	            draft.setDepartment(this.getDepartment());
	            draft.setSubDepartment(this.getSubDepartment());
	            
	            if(!this.getType().getType().trim().equals(ApplicationConstants.GOVERNMENT_RESOLUTION)) {
	            	draft.setStatus(this.getStatusUpperHouse());
		            draft.setInternalStatus(this.getInternalStatusUpperHouse());
		            draft.setRecommendationStatus(this.getRecommendationStatusUpperHouse());
	            }	            
	            
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
		         }else{
		            	draft.setNoticeContent(this.getNoticeContent());
		                draft.setSubject(this.getSubject());
		         }
	             if(this.getId() != null) {
	                Resolution resolution = Resolution.findById(Resolution.class, this.getId());
	                List<ResolutionDraft> originalDrafts = resolution.getDrafts();
	                if(originalDrafts != null){
	                    originalDrafts.add(draft);
	                }
	                else{
	                    originalDrafts = new ArrayList<ResolutionDraft>();
	                    originalDrafts.add(draft);
	                }
	                this.setDrafts(originalDrafts);
	            }
	            else {	
	            	List<ResolutionDraft> originalDrafts = new ArrayList<ResolutionDraft>();
	                originalDrafts.add(draft);
	                this.setDrafts(originalDrafts);
	            }
	        }
    	}
      
    }
    
    
    
    /**
     * Assign resolution no.
     *
     * @param houseType the house type
     * @param session the session
     * @param deviceType the device type
     * @param locale the locale
     * @return the integer
     * @author anandk
     * @throws ELSException 
     * @since v1.0.0
     */
    public static Integer assignResolutionNo(final HouseType houseType, 
    		final Session session, final DeviceType deviceType, final String locale) throws ELSException {
        return getResolutionRepository().assignResolutionNo(houseType, session, deviceType, locale);
    }
    
    /**
     * Gets the revisions for other than government resolution.
     *
     * @param resolutionId the resolution id
     * @param locale the locale
     * @return the revisions
     * @throws ELSException 
     */
    public static List<RevisionHistoryVO> getRevisions(final Long resolutionId, final String locale) throws ELSException {
        return getResolutionRepository().getRevisions(resolutionId,locale);
    }
        
    /**
     * Gets the revisions for government resolution.
     *
     * @param resolutionId the resolution id
     * @param workflowHouseTypeId the workflow house type id
     * @param locale the locale
     * @return the revisions
     * @throws ELSException 
     */
    public static List<RevisionHistoryVO> getRevisions(final Long resolutionId, Long workflowHouseTypeId, final String locale) throws ELSException {
        return getResolutionRepository().getRevisions(resolutionId,workflowHouseTypeId,locale);
    }
    
    /**
     * Find members.
     *
     * @param session the session
     * @param deviceType the device type
     * @param answeringDate the answering date
     * @param internalStatuses the internal statuses
     * @param startTime the start time
     * @param endTime the end time
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list< member>
     * @author anandk
     * @throws ELSException 
     * @since v1.0.0
     */
    public static List<Member> findMembers(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
    	
    	return getResolutionRepository().findMembers(session, deviceType, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale);
    }
    
   
    /**
     * Find members all.
     *
     * @param session the session
     * @param deviceType the device type
     * @param answeringDate the answering date
     * @param internalStatuses the internal statuses
     * @param isPreBallot the is pre ballot
     * @param startTime the start time
     * @param endTime the end time
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list< member>
     * @author anandk
     * @throws ELSException 
     * @since v1.0.0
     */
    public static List<Member> findMembersAll(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Boolean isPreBallot,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException {
    	return getResolutionRepository().findMembersAll(session, deviceType, answeringDate, internalStatuses,isPreBallot, startTime, endTime, sortOrder, locale);
    }
    
    
    /**
     * @param session
     * @param deviceType
     * @param answeringDate
     * @param internalStatuses
     * @param startTime
     * @param endTime
     * @param sortOrder
     * @param locale
     * @return
     * @throws ELSException
     */
    public static List<Member> findMembersEligibleForTheBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Status[] internalStatuses,
			final Date startTime,
			final Date endTime,
			final String sortOrder,
			final String locale) throws ELSException{
    	return getResolutionRepository().findMembersEligibleForTheBallot(session, deviceType, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale);
    }
   
    public static List<Resolution> findChosenResolutionsForGivenDate(final Session session, 
			final DeviceType deviceType,
			final Status ballotStatus,
			final Status discussionStatus,
			final Date discussionDate,
			final String locale){
    	
    	return getResolutionRepository().findChosenResolutionsForGivenDate(session, deviceType, ballotStatus, discussionStatus, discussionDate, locale);
    			
    }
    
    /**
     * Gets the resolution for member of unique subject.
     *
     * @param session the session
     * @param deviceType the device type
     * @param answeringDate the answering date
     * @param memberID the member id
     * @param subjects the subjects
     * @param locale the locale
     * @return the resolution for member of unique subject
     * @throws ELSException 
     */
    public static Resolution getResolutionForMemberOfUniqueSubject(final Session session, final DeviceType deviceType, final Date answeringDate, final Long memberID, final List<String> subjects, final String locale) throws ELSException{
    	return getResolutionRepository().findResolutionForMemberOfUniqueSubject(session, deviceType, answeringDate, memberID, subjects, locale);
    }
    
    
  
    /**
     * Gets the member resolution count by number.
     *
     * @param memberId the member id
     * @param sessionId the session id
     * @param locale the locale
     * @return the member resolution count by number
     * @throws ELSException 
     */
    private static Integer getMemberResolutionCountByNumber(Long memberId,Long sessionId,String locale) throws ELSException {
    	 return getResolutionRepository().findMemberResolutionCountByNumber(memberId,sessionId,locale);
	}
    
	
	/**
	 * Gets the unique members and subjects.
	 *
	 * @param house the house
	 * @param sessionID the session id
	 * @return the unique members and subjects
	 * @throws ELSException 
	 */
	public static List<MasterVO> getUniqueMembersAndSubjects(final String house, final String sessionID) throws ELSException{
		return getResolutionRepository().findUniqueMembersAndSubjects(house, sessionID);
	}
	
	
	
 	/**
	  * Find active members with resolutions.
	  *
	  * @param session the session
	  * @param activeOn the active on
	  * @param deviceType the device type
	  * @param internalStatuses the internal statuses
	  * @param startTime the start time
	  * @param endTime the end time
	  * @param sortOrder the sort order
	  * @param locale the locale
	  * @return the list< member>
	  * @author anandk
 	 * @throws ELSException 
	  * @since v1.0.0
	  */
	 public static List<Member> findActiveMembersWithResolutions(
				Session session, Date activeOn, DeviceType deviceType,
				Status[] internalStatuses, Date startTime, Date endTime,
				String sortOrder, String locale) throws ELSException {
	    	MemberRole role = MemberRole.find(session.getHouse().getType(), "MEMBER", locale);
	    	return Resolution.getResolutionRepository().findActiveMembersWithResolutions(session, 
	    		role, activeOn, deviceType, internalStatuses,startTime, endTime, sortOrder, locale);
		}

	
		/**
		 * Sort by number.
		 *
		 * @param nonDatedQList the non dated q list
		 * @param sortOrder the sort order
		 * @return the list< device>
		 * @author anandk
		 * @since v1.0.0
		 */
		public static List<Device> sortByNumber(List<Device> nonDatedQList,
				String sortOrder) {
			List<Device> deviceList=new ArrayList<Device>();
			List<Resolution> newRList = new ArrayList<Resolution>();
			for(Device d:nonDatedQList){
				newRList.add((Resolution) d);
			}
	        if(sortOrder.equals(ApplicationConstants.ASC)) {
	            Comparator<Resolution> c = new Comparator<Resolution>() {

	                @Override
	                public int compare(final Resolution r1, final Resolution r2) {
	                    return r1.getNumber().compareTo(r2.getNumber());
	                }
	            };
	            Collections.sort(newRList, c);
	        }
	        else if(sortOrder.equals(ApplicationConstants.DESC)) {
	            Comparator<Resolution> c = new Comparator<Resolution>() {

	                @Override
	                public int compare(final Resolution r1, final Resolution r2) {
	                    return r2.getNumber().compareTo(r1.getNumber());
	                }
	            };
	            Collections.sort(newRList, c);
	        }
	        for(Resolution r:newRList){
	        	deviceList.add(r);
	        }
	        return deviceList;
		}

		
		
		/**
		 * Find non answering date.
		 *
		 * @param session the session
		 * @param member the member
		 * @param deviceType the device type
		 * @param startTime the start time
		 * @param endTime the end time
		 * @param internalStatuses the internal statuses
		 * @param maxD the max d
		 * @param asc the asc
		 * @param locale the locale
		 * @return the list< resolution>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Resolution> findNonAnsweringDate(Session session,
				Member member, DeviceType deviceType, Date startTime,
				Date endTime, Status[] internalStatuses, int maxD, String asc,
				String locale) throws ELSException {
			 return getResolutionRepository().findNonAnsweringDate(session,member,deviceType,startTime,
					 endTime,internalStatuses,maxD,asc,locale);
		}

		
		/**
		 * Find non answering date.
		 *
		 * @param session the session
		 * @param member the member
		 * @param deviceType the device type
		 * @param finalSubmissionTime the final submission time
		 * @param internalStatuses the internal statuses
		 * @param maxD the max d
		 * @param asc the asc
		 * @param locale the locale
		 * @return the list< resolution>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Resolution> findNonAnsweringDate(Session session,
				Member member, DeviceType deviceType, Date finalSubmissionTime,
				Status[] internalStatuses, int maxD, String asc, String locale) throws ELSException {
			 return getResolutionRepository().findNonAnsweringDate(session,member,deviceType,finalSubmissionTime,
					 internalStatuses,maxD,asc,locale);
		}
		
		
		/**
		 * Find active members without resolutions.
		 *
		 * @param session the session
		 * @param activeOn the active on
		 * @param deviceType the device type
		 * @param internalStatuses the internal statuses
		 * @param startTime the start time
		 * @param endTime the end time
		 * @param sortOrder the sort order
		 * @param locale the locale
		 * @return the list< member>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Member> findActiveMembersWithoutResolutions(
				Session session, Date activeOn, DeviceType deviceType,
				Status[] internalStatuses, Date startTime, Date endTime,
				String sortOrder, String locale) throws ELSException {
			MemberRole role = MemberRole.find(session.getHouse().getType(), "MEMBER", locale);
	    	return Resolution.getResolutionRepository().findActiveMembersWithoutResolutions(session, 
	    		role, activeOn, deviceType, internalStatuses,startTime, endTime, sortOrder, locale);
		}
	    
	  
    	/**
	     * Gets the latest resolution draft of user.
	     *
	     * @param resolutionId the resolution id
	     * @param username the username
	     * @return the latest resolution draft of user
    	 * @throws ELSException 
	     */
	    public static ResolutionDraft getLatestResolutionDraftOfUser(Long resolutionId, String username) throws ELSException {
	    	return getResolutionRepository().findLatestResolutionDraftOfUser(resolutionId, username);
	    }
	    
	   
    	/**
	     * Find.
	     *
	     * @param member the member
	     * @param session the session
	     * @param locale the locale
	     * @return the resolution
	     * @author anandk
    	 * @throws ELSException 
	     * @since v1.0.0
	     */
	    public static Resolution find(Member member, Session session,String locale) throws ELSException {
	    	return getResolutionRepository().find(member, session,locale);
		}
	
	    /**
    	 * Find.
    	 *
    	 * @param session the session
    	 * @param deviceType the device type
    	 * @param answeringDate the answering date
    	 * @param internalStatuses the internal statuses
    	 * @param hasParent the has parent
    	 * @param startTime the start time
    	 * @param endTime the end time
    	 * @param sortOrder the sort order
    	 * @param locale the locale
    	 * @return the list< resolution>
    	 * @author anandk
	     * @throws ELSException 
    	 * @since v1.0.0
    	 */
    	public static List<Resolution> find(final Session session,
				final DeviceType deviceType,
				final Date answeringDate,
				final Status[] internalStatuses,
				final Boolean hasParent,
				final Date startTime,
				final Date endTime,
				final String sortOrder,
				final String locale) throws ELSException {
			return getResolutionRepository().find(session, deviceType, answeringDate, internalStatuses, hasParent, startTime, endTime, sortOrder, locale);
		}
		

		
		/**
		 * Find.
		 *
		 * @param session the session
		 * @param deviceType the device type
		 * @param memberId the member id
		 * @param answeringDate the answering date
		 * @param internalStatuses the internal statuses
		 * @param startTime the start time
		 * @param endTime the end time
		 * @param sortOrder the sort order
		 * @param locale the locale
		 * @return the list< resolution>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Resolution> find(final Session session,
				final DeviceType deviceType,
				final Long memberId,
				final Date answeringDate,
				final Status[] internalStatuses,
				final Date startTime,
				final Date endTime,
				final String sortOrder,
				final String locale) throws ELSException {
			return getResolutionRepository().find(session, deviceType, memberId, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale);
		}
		
		
		
		/**
		 * Find resolutions by discussion date and member.
		 *
		 * @param session the session
		 * @param deviceType the device type
		 * @param memberId the member id
		 * @param answeringDate the answering date
		 * @param internalStatuses the internal statuses
		 * @param startTime the start time
		 * @param endTime the end time
		 * @param sortOrder the sort order
		 * @param locale the locale
		 * @return the list< resolution>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Resolution> findResolutionsByDiscussionDateAndMember(final Session session,
				final DeviceType deviceType,
				final Long memberId,
				final Date answeringDate,
				final Status[] internalStatuses,
				final Date startTime,
				final Date endTime,
				final String sortOrder,
				final String locale) throws ELSException {
			
			return getResolutionRepository().findResolutionsByDiscussionDateAndMember(session, deviceType, memberId, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale);
		}
		
		
		/**
		 * Gets the member choice count.
		 *
		 * @param session the session
		 * @param deviceType the device type
		 * @param memberId the member id
		 * @param answeringDate the answering date
		 * @param internalStatuses the internal statuses
		 * @param startTime the start time
		 * @param endTime the end time
		 * @param locale the locale
		 * @return the member choice count
		 * @throws ELSException 
		 */
		public static Integer getMemberChoiceCount(final Session session,
				final DeviceType deviceType,
				final Long memberId,
				final Date answeringDate,
				final Status[] internalStatuses,
				final Date startTime,
				final Date endTime,
				final String locale) throws ELSException{
			return getResolutionRepository().findMemberChoiceCount(session, deviceType, memberId, answeringDate, internalStatuses, startTime, endTime, locale);
		}

		
		/**
		 * Find all by member.
		 *
		 * @param session the session
		 * @param member the member
		 * @param deviceType the device type
		 * @param itemsCount the items count
		 * @param strLocale the str locale
		 * @return the list< resolution>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Resolution> findAllByMember(Session session,
				Member member, DeviceType deviceType, Integer itemsCount,
				String strLocale) throws ELSException {
			return getResolutionRepository().findAllByMember(session,member,deviceType,itemsCount,strLocale);
		}
		
		
		/**
		 * Format number.
		 *
		 * @return the string
		 * @author anandk
		 * @since v1.0.0
		 */
		public String formatNumber() {
			if(getNumber()!=null){
				NumberFormat format=FormaterUtil.getNumberFormatterNoGrouping(this.getLocale());
				return format.format(this.getNumber());
			}else{
				return "";
			}
		}

		
		/**
		 * Find all by status.
		 *
		 * @param session the session
		 * @param deviceType the device type
		 * @param internalStatus the internal status
		 * @param itemsCount the items count
		 * @param locale the locale
		 * @return the list< resolution>
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static List<Resolution> findAllByStatus(Session session,
				DeviceType deviceType, Status internalStatus, Integer itemsCount,
				String locale) throws ELSException {
			return getResolutionRepository().findAllByStatus(session, deviceType, internalStatus, itemsCount, locale);
		}

		
		/**
		 * Gets the resolution without number.
		 *
		 * @param member the member
		 * @param deviceType the device type
		 * @param session the session
		 * @param locale the locale
		 * @return the resolution without number
		 * @throws ELSException 
		 */
		public static Integer getResolutionWithoutNumber(Member member,DeviceType deviceType,
				Session session, String locale) throws ELSException {
			return getResolutionRepository().findResolutionWithoutNumberCount(member,deviceType,session,locale);
		}

		
		/**
		 * Gets the rejected resolution.
		 *
		 * @param member the member
		 * @param deviceType the device type
		 * @param session the session
		 * @param locale the locale
		 * @return the rejected resolution
		 * @throws ELSException 
		 */
		public static List<Resolution> getRejectedResolution(Member member,
				DeviceType deviceType, Session session, String locale) throws ELSException {
			return getResolutionRepository().findRejectedResolution(member,deviceType,session,locale);
		}
		
		public static String getRejectedResolutionsAsString(List<Resolution> resolutions) throws ELSException{
			return getResolutionRepository().findRejectedResolutionAsString(resolutions);
		}

		
		/**
		 * Find resolution count.
		 *
		 * @param member2 the member2
		 * @param selectedSession the selected session
		 * @param resolutionType the resolution type
		 * @param locale the locale
		 * @return the integer
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static Integer findResolutionCount(Member member2,
				Session selectedSession, DeviceType resolutionType, String locale) throws ELSException {
			return getResolutionRepository().findResolutionCount(member2,
					selectedSession,resolutionType,locale);
		}

		
		/**
		 * Find highest file no.
		 *
		 * @param session the session
		 * @param deviceType the device type
		 * @param locale the locale
		 * @param houseType the house type
		 * @return the int
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static int findHighestFileNo(Session session,
				DeviceType deviceType, String locale,HouseType houseType) throws ELSException {
			return getResolutionRepository().findHighestFileNo(session,
					deviceType,houseType,locale);
		}

		
		/**
		 * Find current file.
		 *
		 * @param domain the domain
		 * @param houseType the house type
		 * @return the reference
		 * @author anandk
		 * @throws ELSException 
		 * @since v1.0.0
		 */
		public static Reference findCurrentFile(Resolution domain,HouseType houseType) throws ELSException {
			return getResolutionRepository().findCurrentFile(domain,houseType);
					
		}

	
	/**
	 * Find all by file.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param file the file
	 * @param locale the locale
	 * @param houseType the house type
	 * @return the list< resolution>
	 * @author anandk
	 * @throws ELSException 
	 * @since v1.0.0
	 */
	public static List<Resolution> findAllByFile(Session session,
			DeviceType deviceType, Integer file, String locale,HouseType houseType) throws ELSException {
		return getResolutionRepository().findAllByFile(session,
				deviceType,file,locale,houseType);
	}	
	
	/**
	 * @param session
	 * @param deviceType
	 * @param memberId
	 * @param discussionDate
	 * @param locale
	 * @return
	 */
	public static Integer findChoiceCountForGivenDiscussionDateOfMember(final Session session, 
			final DeviceType deviceType, 
			final Member member, 
			final Date discussionDate, 
			final String locale){
		return  getResolutionRepository().findChoiceCountForGivenDiscussionDateOfMember(session, deviceType, member, discussionDate, locale);
	}

	/*public static List<MasterVO> getMemberResolutionStatistics(final Member member, final Session session, final String locale){
		return getResolutionRepository().getMemberResolutionStatistics(member, session, locale);
	}*/
	
	public static Resolution getResolution(Long sessionId, Long deviceTypeId,
			Integer dNumber, String locale) {
		return  getResolutionRepository().getResolution(sessionId, deviceTypeId, dNumber,locale);
	}
	    
	/**** Getters and Setters ****/
	/**
	 * Gets the house type.
	 * 
	 * @return the house type
	 */
	public HouseType getHouseType() {
		return houseType;
	}

	/**
	 * Sets the house type.
	 * 
	 * @param houseType
	 *            the new house type
	 */
	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the session.
	 * 
	 * @param session
	 *            the new session
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public DeviceType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(DeviceType type) {
		this.type = type;
	}

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number
	 *            the new number
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Gets the submission date.
	 * 
	 * @return the submission date
	 */
	public Date getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * Sets the submission date.
	 * 
	 * @param submissionDate
	 *            the new submission date
	 */
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	/**
	 * Gets the creation date.
	 * 
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date.
	 * 
	 * @param creationDate
	 *            the new creation date
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the created by.
	 * 
	 * @return the created by
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 * 
	 * @param createdBy
	 *            the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the edited on.
	 * 
	 * @return the edited on
	 */
	public Date getEditedOn() {
		return editedOn;
	}

	/**
	 * Sets the edited on.
	 * 
	 * @param editedOn
	 *            the new edited on
	 */
	public void setEditedOn(Date editedOn) {
		this.editedOn = editedOn;
	}

	/**
	 * Gets the edited by.
	 * 
	 * @return the edited by
	 */
	public String getEditedBy() {
		return editedBy;
	}

	/**
	 * Sets the edited by.
	 * 
	 * @param editedBy
	 *            the new edited by
	 */
	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * Gets the edited as.
	 * 
	 * @return the edited as
	 */
	public String getEditedAs() {
		return editedAs;
	}

	/**
	 * Sets the edited as.
	 * 
	 * @param editedAs
	 *            the new edited as
	 */
	public void setEditedAs(String editedAs) {
		this.editedAs = editedAs;
	}

	/**
	 * Gets the discussion date.
	 * 
	 * @return the discussion date
	 */
	public Date getDiscussionDate() {
		return discussionDate;
	}

	/**
	 * Sets the discussion date.
	 * 
	 * @param discussionDate
	 *            the new discussion date
	 */
	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the revised subject.
	 * 
	 * @return the revised subject
	 */
	public String getRevisedSubject() {
		return revisedSubject;
	}

	/**
	 * Sets the revised subject.
	 * 
	 * @param revisedSubject
	 *            the new revised subject
	 */
	public void setRevisedSubject(String revisedSubject) {
		this.revisedSubject = revisedSubject;
	}

	/**
	 * Gets the notice content.
	 * 
	 * @return the notice content
	 */
	public String getNoticeContent() {
		return noticeContent;
	}

	/**
	 * Sets the notice content.
	 * 
	 * @param noticeContent
	 *            the new notice content
	 */
	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	/**
	 * Gets the revised notice content.
	 * 
	 * @return the revised notice content
	 */
	public String getRevisedNoticeContent() {
		return revisedNoticeContent;
	}

	/**
	 * Sets the revised notice content.
	 * 
	 * @param revisedNoticeContent
	 *            the new revised notice content
	 */
	public void setRevisedNoticeContent(String revisedNoticeContent) {
		this.revisedNoticeContent = revisedNoticeContent;
	}

	/**
	 * Gets the status lower house.
	 * 
	 * @return the status lower house
	 */
	public Status getStatusLowerHouse() {
		return statusLowerHouse;
	}

	/**
	 * Sets the status lower house.
	 * 
	 * @param statusLowerHouse
	 *            the new status lower house
	 */
	public void setStatusLowerHouse(Status statusLowerHouse) {
		this.statusLowerHouse = statusLowerHouse;
	}

	/**
	 * Gets the status upper house.
	 * 
	 * @return the status upper house
	 */
	public Status getStatusUpperHouse() {
		return statusUpperHouse;
	}

	/**
	 * Sets the status upper house.
	 * 
	 * @param statusUpperHouse
	 *            the new status upper house
	 */
	public void setStatusUpperHouse(Status statusUpperHouse) {
		this.statusUpperHouse = statusUpperHouse;
	}

	/**
	 * Gets the internal status lower house.
	 * 
	 * @return the internal status lower house
	 */
	public Status getInternalStatusLowerHouse() {
		return internalStatusLowerHouse;
	}

	/**
	 * Sets the internal status lower house.
	 * 
	 * @param internalStatusLowerHouse
	 *            the new internal status lower house
	 */
	public void setInternalStatusLowerHouse(Status internalStatusLowerHouse) {
		this.internalStatusLowerHouse = internalStatusLowerHouse;
	}

	/**
	 * Gets the internal status upper house.
	 * 
	 * @return the internal status upper house
	 */
	public Status getInternalStatusUpperHouse() {
		return internalStatusUpperHouse;
	}

	/**
	 * Sets the internal status upper house.
	 * 
	 * @param internalStatusUpperHouse
	 *            the new internal status upper house
	 */
	public void setInternalStatusUpperHouse(Status internalStatusUpperHouse) {
		this.internalStatusUpperHouse = internalStatusUpperHouse;
	}

	/**
	 * Gets the recommendation status lower house.
	 * 
	 * @return the recommendation status lower house
	 */
	public Status getRecommendationStatusLowerHouse() {
		return recommendationStatusLowerHouse;
	}

	/**
	 * Sets the recommendation status lower house.
	 * 
	 * @param recommendationStatusLowerHouse
	 *            the new recommendation status lower house
	 */
	public void setRecommendationStatusLowerHouse(
			Status recommendationStatusLowerHouse) {
		this.recommendationStatusLowerHouse = recommendationStatusLowerHouse;
	}

	/**
	 * Gets the recommendation status upper house.
	 * 
	 * @return the recommendation status upper house
	 */
	public Status getRecommendationStatusUpperHouse() {
		return recommendationStatusUpperHouse;
	}

	/**
	 * Sets the recommendation status upper house.
	 * 
	 * @param recommendationStatusUpperHouse
	 *            the new recommendation status upper house
	 */
	public void setRecommendationStatusUpperHouse(
			Status recommendationStatusUpperHouse) {
		this.recommendationStatusUpperHouse = recommendationStatusUpperHouse;
	}

	/**
	 * Gets the ballot status.
	 * 
	 * @return the ballot status
	 */
	public Status getBallotStatus() {
		return ballotStatus;
	}

	/**
	 * Sets the ballot status.
	 * 
	 * @param ballotStatus
	 *            the new ballot status
	 */
	public void setBallotStatus(Status ballotStatus) {
		this.ballotStatus = ballotStatus;
	}

	/**
	 * Gets the remarks.
	 * 
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 * 
	 * @param remarks
	 *            the new remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Gets the primary member.
	 * 
	 * @return the primary member
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * Sets the primary member.
	 * 
	 * @param member
	 *            the new member
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * Gets the ministry.
	 * 
	 * @return the ministry
	 */
	public Ministry getMinistry() {
		return ministry;
	}

	/**
	 * Sets the ministry.
	 * 
	 * @param ministry
	 *            the new ministry
	 */
	public void setMinistry(Ministry ministry) {
		this.ministry = ministry;
	}

	/**
	 * Gets the department.
	 * 
	 * @return the department
	 */
	public Department getDepartment() {
		return department;
	}

	/**
	 * Sets the department.
	 * 
	 * @param department
	 *            the new department
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * Gets the sub department.
	 * 
	 * @return the sub department
	 */
	public SubDepartment getSubDepartment() {
		return subDepartment;
	}

	/**
	 * Sets the sub department.
	 * 
	 * @param subDepartment
	 *            the new sub department
	 */
	public void setSubDepartment(SubDepartment subDepartment) {
		this.subDepartment = subDepartment;
	}

	/**
	 * Gets the drafts.
	 * 
	 * @return the drafts
	 */
	public List<ResolutionDraft> getDrafts() {
		return drafts;
	}

	/**
	 * Sets the drafts.
	 * 
	 * @param drafts
	 *            the new drafts
	 */
	public void setDrafts(List<ResolutionDraft> drafts) {
		this.drafts = drafts;
	}

	/**
	 * Gets the referenced resolutions.
	 * 
	 * @return the referenced resolutions
	 */
	public ReferencedEntity getReferencedResolution() {
		return referencedResolution;
	}

	/**
	 * Sets the referenced resolutions.
	 * 
	 * @param referencedResolution
	 *            the new referenced resolution
	 */
	public void setReferencedResolution(ReferencedEntity referencedResolution) {
		this.referencedResolution = referencedResolution;
	}

	/**
	 * Gets the mark as accepted.
	 * 
	 * @return the mark as accepted
	 */
	public Boolean getMarkAsAccepted() {
		return markAsAccepted;
	}

	/**
	 * Sets the mark as accepted.
	 * 
	 * @param markAsAccepted
	 *            the new mark as accepted
	 */
	public void setMarkAsAccepted(Boolean markAsAccepted) {
		this.markAsAccepted = markAsAccepted;
	}

	/**
	 * Gets the factual position.
	 * 
	 * @return the factual position
	 */
	public String getFactualPosition() {
		return factualPosition;
	}

	/**
	 * Sets the factual position.
	 * 
	 * @param factualPosition
	 *            the new factual position
	 */
	public void setFactualPosition(String factualPosition) {
		this.factualPosition = factualPosition;
	}

	/**
	 * Gets the date of factual position receiving.
	 * 
	 * @return the date of factual position receiving
	 */
	public Date getLastDateOfFactualPositionReceiving() {
		return lastDateOfFactualPositionReceiving;
	}

	/**
	 * Sets the date of factual position receiving.
	 * 
	 * @param lastDateOfFactualPositionReceiving
	 *            the new last date of factual position receiving
	 */
	public void setLastDateOfFactualPositionReceiving(
			Date lastDateOfFactualPositionReceiving) {
		this.lastDateOfFactualPositionReceiving = lastDateOfFactualPositionReceiving;
	}

	/**
	 * Gets the number of days for factual position receiving.
	 * 
	 * @return the number of days for factual position receiving
	 */
	public Integer getNumberOfDaysForFactualPositionReceiving() {
		return numberOfDaysForFactualPositionReceiving;
	}

	/**
	 * Sets the number of days for factual position receiving.
	 * 
	 * @param numberOfDaysForFactualPositionReceiving
	 *            the new number of days for factual position receiving
	 */
	public void setNumberOfDaysForFactualPositionReceiving(
			Integer numberOfDaysForFactualPositionReceiving) {
		this.numberOfDaysForFactualPositionReceiving = numberOfDaysForFactualPositionReceiving;
	}

	/**
	 * Gets the house type for gr.
	 * 
	 * @return the house type for gr
	 */
	public String getHouseTypeForGR() {
		houseTypeForGR = "";
		if (this.getHouseType() != null) {
			houseTypeForGR = this.getHouseType().getName();
		}
		return houseTypeForGR;
	}

	/**
	 * Gets the rejection reason.
	 * 
	 * @return the rejection reason
	 */
	public String getRejectionReason() {
		return rejectionReason;
	}

	/**
	 * Sets the rejection reason.
	 * 
	 * @param rejectionReason
	 *            the new rejection reason
	 */
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	/**
	 * Gets the questions asked in factual position.
	 * 
	 * @return the questions asked in factual position
	 */
	public String getQuestionsAskedInFactualPosition() {
		return questionsAskedInFactualPosition;
	}

	/**
	 * Sets the questions asked in factual position.
	 * 
	 * @param questionsAskedInFactualPosition
	 *            the new questions asked in factual position
	 */
	public void setQuestionsAskedInFactualPosition(
			String questionsAskedInFactualPosition) {
		this.questionsAskedInFactualPosition = questionsAskedInFactualPosition;
	}

	/**
	 * Gets the discussion status.
	 * 
	 * @return the discussion status
	 */
	public Status getDiscussionStatus() {
		return discussionStatus;
	}

	/**
	 * Sets the discussion status.
	 * 
	 * @param discussionStatus
	 *            the new discussion status
	 */
	public void setDiscussionStatus(Status discussionStatus) {
		this.discussionStatus = discussionStatus;
	}

	/**
	 * Gets the rule for discussion date.
	 * 
	 * @return the rule for discussion date
	 */
	public Rule getRuleForDiscussionDate() {
		return ruleForDiscussionDate;
	}

	/**
	 * Sets the rule for discussion date.
	 * 
	 * @param ruleForDiscussionDate
	 *            the new rule for discussion date
	 */
	public void setRuleForDiscussionDate(Rule ruleForDiscussionDate) {
		this.ruleForDiscussionDate = ruleForDiscussionDate;
	}

	/**
	 * Gets the bulk submitted.
	 * 
	 * @return the bulk submitted
	 */
	public boolean getBulkSubmitted() {
		return bulkSubmitted;
	}

	/**
	 * Sets the bulk submitted.
	 * 
	 * @param bulkSubmitted
	 *            the new bulk submitted
	 */
	public void setBulkSubmitted(boolean bulkSubmitted) {
		this.bulkSubmitted = bulkSubmitted;
	}

	/**
	 * Sets the house type for gr.
	 * 
	 * @param houseTypeForGR
	 *            the new house type for gr
	 */
	public void setHouseTypeForGR(String houseTypeForGR) {
		this.houseTypeForGR = houseTypeForGR;
	}

	/**
	 * Gets the workflow started lower house.
	 * 
	 * @return the workflow started lower house
	 */
	public String getWorkflowStartedLowerHouse() {
		return workflowStartedLowerHouse;
	}

	/**
	 * Sets the workflow started lower house.
	 * 
	 * @param workflowStartedLowerHouse
	 *            the new workflow started lower house
	 */
	public void setWorkflowStartedLowerHouse(String workflowStartedLowerHouse) {
		this.workflowStartedLowerHouse = workflowStartedLowerHouse;
	}

	/**
	 * Gets the workflow started upper house.
	 * 
	 * @return the workflow started upper house
	 */
	public String getWorkflowStartedUpperHouse() {
		return workflowStartedUpperHouse;
	}

	/**
	 * Sets the workflow started upper house.
	 * 
	 * @param workflowStartedUpperHouse
	 *            the new workflow started upper house
	 */
	public void setWorkflowStartedUpperHouse(String workflowStartedUpperHouse) {
		this.workflowStartedUpperHouse = workflowStartedUpperHouse;
	}

	/**
	 * Gets the actor lower house.
	 * 
	 * @return the actor lower house
	 */
	public String getActorLowerHouse() {
		return actorLowerHouse;
	}

	/**
	 * Sets the actor lower house.
	 * 
	 * @param actorLowerHouse
	 *            the new actor lower house
	 */
	public void setActorLowerHouse(String actorLowerHouse) {
		this.actorLowerHouse = actorLowerHouse;
	}

	/**
	 * Gets the actor upper house.
	 * 
	 * @return the actor upper house
	 */
	public String getActorUpperHouse() {
		return actorUpperHouse;
	}

	/**
	 * Sets the actor upper house.
	 * 
	 * @param actorUpperHouse
	 *            the new actor upper house
	 */
	public void setActorUpperHouse(String actorUpperHouse) {
		this.actorUpperHouse = actorUpperHouse;
	}

	/**
	 * Gets the localized actor name lower house.
	 * 
	 * @return the localized actor name lower house
	 */
	public String getLocalizedActorNameLowerHouse() {
		return localizedActorNameLowerHouse;
	}

	/**
	 * Sets the localized actor name lower house.
	 * 
	 * @param localizedActorNameLowerHouse
	 *            the new localized actor name lower house
	 */
	public void setLocalizedActorNameLowerHouse(
			String localizedActorNameLowerHouse) {
		this.localizedActorNameLowerHouse = localizedActorNameLowerHouse;
	}

	/**
	 * Gets the localized actor name upper house.
	 * 
	 * @return the localized actor name upper house
	 */
	public String getLocalizedActorNameUpperHouse() {
		return localizedActorNameUpperHouse;
	}

	/**
	 * Sets the localized actor name upper house.
	 * 
	 * @param localizedActorNameUpperHouse
	 *            the new localized actor name upper house
	 */
	public void setLocalizedActorNameUpperHouse(
			String localizedActorNameUpperHouse) {
		this.localizedActorNameUpperHouse = localizedActorNameUpperHouse;
	}

	/**
	 * Gets the end flag lower house.
	 * 
	 * @return the end flag lower house
	 */
	public String getEndFlagLowerHouse() {
		return endFlagLowerHouse;
	}

	/**
	 * Sets the end flag lower house.
	 * 
	 * @param endFlagLowerHouse
	 *            the new end flag lower house
	 */
	public void setEndFlagLowerHouse(String endFlagLowerHouse) {
		this.endFlagLowerHouse = endFlagLowerHouse;
	}

	/**
	 * Gets the end flag upper house.
	 * 
	 * @return the end flag upper house
	 */
	public String getEndFlagUpperHouse() {
		return endFlagUpperHouse;
	}

	/**
	 * Sets the end flag upper house.
	 * 
	 * @param endFlagUpperHouse
	 *            the new end flag upper house
	 */
	public void setEndFlagUpperHouse(String endFlagUpperHouse) {
		this.endFlagUpperHouse = endFlagUpperHouse;
	}

	/**
	 * Gets the level lower house.
	 * 
	 * @return the level lower house
	 */
	public String getLevelLowerHouse() {
		return levelLowerHouse;
	}

	/**
	 * Sets the level lower house.
	 * 
	 * @param levelLowerHouse
	 *            the new level lower house
	 */
	public void setLevelLowerHouse(String levelLowerHouse) {
		this.levelLowerHouse = levelLowerHouse;
	}

	/**
	 * Gets the level upper house.
	 * 
	 * @return the level upper house
	 */
	public String getLevelUpperHouse() {
		return levelUpperHouse;
	}

	/**
	 * Sets the level upper house.
	 * 
	 * @param levelUpperHouse
	 *            the new level upper house
	 */
	public void setLevelUpperHouse(String levelUpperHouse) {
		this.levelUpperHouse = levelUpperHouse;
	}

	/**
	 * Gets the workflow started on lower house.
	 * 
	 * @return the workflow started on lower house
	 */
	public Date getWorkflowStartedOnLowerHouse() {
		return workflowStartedOnLowerHouse;
	}

	/**
	 * Sets the workflow started on lower house.
	 * 
	 * @param workflowStartedOnLowerHouse
	 *            the new workflow started on lower house
	 */
	public void setWorkflowStartedOnLowerHouse(Date workflowStartedOnLowerHouse) {
		this.workflowStartedOnLowerHouse = workflowStartedOnLowerHouse;
	}

	/**
	 * Gets the workflow started on upper house.
	 * 
	 * @return the workflow started on upper house
	 */
	public Date getWorkflowStartedOnUpperHouse() {
		return workflowStartedOnUpperHouse;
	}

	/**
	 * Sets the workflow started on upper house.
	 * 
	 * @param workflowStartedOnUpperHouse
	 *            the new workflow started on upper house
	 */
	public void setWorkflowStartedOnUpperHouse(Date workflowStartedOnUpperHouse) {
		this.workflowStartedOnUpperHouse = workflowStartedOnUpperHouse;
	}

	/**
	 * Gets the task received on lower house.
	 * 
	 * @return the task received on lower house
	 */
	public Date getTaskReceivedOnLowerHouse() {
		return taskReceivedOnLowerHouse;
	}

	/**
	 * Sets the task received on lower house.
	 * 
	 * @param taskReceivedOnLowerHouse
	 *            the new task received on lower house
	 */
	public void setTaskReceivedOnLowerHouse(Date taskReceivedOnLowerHouse) {
		this.taskReceivedOnLowerHouse = taskReceivedOnLowerHouse;
	}

	/**
	 * Gets the task received on upper house.
	 * 
	 * @return the task received on upper house
	 */
	public Date getTaskReceivedOnUpperHouse() {
		return taskReceivedOnUpperHouse;
	}

	/**
	 * Sets the task received on upper house.
	 * 
	 * @param taskReceivedOnUpperHouse
	 *            the new task received on upper house
	 */
	public void setTaskReceivedOnUpperHouse(Date taskReceivedOnUpperHouse) {
		this.taskReceivedOnUpperHouse = taskReceivedOnUpperHouse;
	}

	/**
	 * Gets the workflow details id lower house.
	 * 
	 * @return the workflow details id lower house
	 */
	public Long getWorkflowDetailsIdLowerHouse() {
		return workflowDetailsIdLowerHouse;
	}

	/**
	 * Sets the workflow details id lower house.
	 * 
	 * @param workflowDetailsIdLowerHouse
	 *            the new workflow details id lower house
	 */
	public void setWorkflowDetailsIdLowerHouse(Long workflowDetailsIdLowerHouse) {
		this.workflowDetailsIdLowerHouse = workflowDetailsIdLowerHouse;
	}

	/**
	 * Gets the workflow details id upper house.
	 * 
	 * @return the workflow details id upper house
	 */
	public Long getWorkflowDetailsIdUpperHouse() {
		return workflowDetailsIdUpperHouse;
	}

	/**
	 * Sets the workflow details id upper house.
	 * 
	 * @param workflowDetailsIdUpperHouse
	 *            the new workflow details id upper house
	 */
	public void setWorkflowDetailsIdUpperHouse(Long workflowDetailsIdUpperHouse) {
		this.workflowDetailsIdUpperHouse = workflowDetailsIdUpperHouse;
	}

	/**
	 * Gets the file lower house.
	 * 
	 * @return the file lower house
	 */
	public Integer getFileLowerHouse() {
		return fileLowerHouse;
	}

	/**
	 * Sets the file lower house.
	 * 
	 * @param fileLowerHouse
	 *            the new file lower house
	 */
	public void setFileLowerHouse(Integer fileLowerHouse) {
		this.fileLowerHouse = fileLowerHouse;
	}

	/**
	 * Gets the file upper house.
	 * 
	 * @return the file upper house
	 */
	public Integer getFileUpperHouse() {
		return fileUpperHouse;
	}

	/**
	 * Sets the file upper house.
	 * 
	 * @param fileUpperHouse
	 *            the new file upper house
	 */
	public void setFileUpperHouse(Integer fileUpperHouse) {
		this.fileUpperHouse = fileUpperHouse;
	}

	/**
	 * Gets the file index lower house.
	 * 
	 * @return the file index lower house
	 */
	public Integer getFileIndexLowerHouse() {
		return fileIndexLowerHouse;
	}

	/**
	 * Sets the file index lower house.
	 * 
	 * @param fileIndexLowerHouse
	 *            the new file index lower house
	 */
	public void setFileIndexLowerHouse(Integer fileIndexLowerHouse) {
		this.fileIndexLowerHouse = fileIndexLowerHouse;
	}

	/**
	 * Gets the file index upper house.
	 * 
	 * @return the file index upper house
	 */
	public Integer getFileIndexUpperHouse() {
		return fileIndexUpperHouse;
	}

	/**
	 * Sets the file index upper house.
	 * 
	 * @param fileIndexUpperHouse
	 *            the new file index upper house
	 */
	public void setFileIndexUpperHouse(Integer fileIndexUpperHouse) {
		this.fileIndexUpperHouse = fileIndexUpperHouse;
	}

	/**
	 * Gets the file sent lower house.
	 * 
	 * @return the file sent lower house
	 */
	public Boolean getFileSentLowerHouse() {
		return fileSentLowerHouse;
	}

	/**
	 * Sets the file sent lower house.
	 * 
	 * @param fileSentLowerHouse
	 *            the new file sent lower house
	 */
	public void setFileSentLowerHouse(Boolean fileSentLowerHouse) {
		this.fileSentLowerHouse = fileSentLowerHouse;
	}

	/**
	 * Gets the file sent upper house.
	 * 
	 * @return the file sent upper house
	 */
	public Boolean getFileSentUpperHouse() {
		return fileSentUpperHouse;
	}

	/**
	 * Sets the file sent upper house.
	 * 
	 * @param fileSentUpperHouse
	 *            the new file sent upper house
	 */
	public void setFileSentUpperHouse(Boolean fileSentUpperHouse) {
		this.fileSentUpperHouse = fileSentUpperHouse;
	}

	public String getDataEnteredBy() {
		return dataEnteredBy;
	}

	public void setDataEnteredBy(String dataEnteredBy) {
		this.dataEnteredBy = dataEnteredBy;
	}

	public VotingDetail getVotingDetail() {
		return votingDetail;
	}

	public void setVotingDetail(VotingDetail votingDetail) {
		this.votingDetail = votingDetail;
	}

	

}
