/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ClubbedEntity.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.repository.ClubbedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class ClubbedEntity.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="clubbed_entities")
public class ClubbedEntity extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The created on. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_on")
    private Date createdOn;    

    /** The position. */
    private Integer position;

    /** The device type. */
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;

    /** The question. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Question question;
    
    /** The motion. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Motion motion;   
    
    @ManyToOne(fetch=FetchType.LAZY)
    private CutMotion cutMotion;  
    
    @ManyToOne(fetch=FetchType.LAZY)
    private EventMotion eventMotion;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private StandaloneMotion standaloneMotion;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private DiscussionMotion discussionMotion;
    
    /** The bill. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Bill bill;
    
    /** The bill amendment motion. */
    @ManyToOne(fetch=FetchType.LAZY)
    private BillAmendmentMotion billAmendmentMotion;
    
    /** The Special Mention Notice. */
    @ManyToOne(fetch=FetchType.LAZY)
    private SpecialMentionNotice specialMentionNotice;
    
    /** The adjournment motion. */
    @ManyToOne(fetch=FetchType.LAZY)
    private AdjournmentMotion adjournmentMotion;
    
    /** The Rule Suspension motion. */
    @ManyToOne(fetch=FetchType.LAZY)
    private RulesSuspensionMotion rulesSuspensionMotion;

	@Autowired
    private transient ClubbedEntityRepository clubbedEntityRepository;
    /**
     * Instantiates a new clubbed entity.
     */
    public ClubbedEntity() {
        super();
    }
    
    public static ClubbedEntityRepository getClubbedEntityRepository() {
    	ClubbedEntityRepository clubbedEntityRepository = new ClubbedEntity().clubbedEntityRepository;
        if (clubbedEntityRepository == null) {
            throw new IllegalStateException(
                    "ClubbedEntityRepository has not been injected in Clubbed Entity Domain");
        }
        return clubbedEntityRepository;
    }

    

    /**
     * Gets the position.
     *
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }


    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(final Integer position) {
        this.position = position;
    }


    /**
     * Gets the question.
     *
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }


    /**
     * Sets the question.
     *
     * @param question the new question
     */
    public void setQuestion(final Question question) {
        this.question = question;
    }


    /**
     * Sets the device type.
     *
     * @param deviceType the new device type
     */
    public void setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public Motion getMotion() {
		return motion;
	}

	public void setMotion(Motion motion) {
		this.motion = motion;
	}

	public CutMotion getCutMotion() {
		return cutMotion;
	}

	public void setCutMotion(CutMotion cutMotion) {
		this.cutMotion = cutMotion;
	}
	
	public EventMotion getEventMotion(){
		return eventMotion;
	}
	
	public void setEventMotion(EventMotion eventMotion){
		this.eventMotion = eventMotion;
	}
	
    public StandaloneMotion getStandaloneMotion() {
		return standaloneMotion;
	}

	public void setStandaloneMotion(StandaloneMotion standaloneMotion) {
		this.standaloneMotion = standaloneMotion;
	}
	
	public DiscussionMotion getDiscussionMotion() {
		return discussionMotion;
	}

	public void setDiscussionMotion(DiscussionMotion discussionMotion) {
		this.discussionMotion = discussionMotion;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public BillAmendmentMotion getBillAmendmentMotion() {
		return billAmendmentMotion;
	}

	public void setBillAmendmentMotion(BillAmendmentMotion billAmendmentMotion) {
		this.billAmendmentMotion = billAmendmentMotion;
	}

	/**
	 * @return the adjournmentMotion
	 */
	public AdjournmentMotion getAdjournmentMotion() {
		return adjournmentMotion;
	}

	/**
	 * @param adjournmentMotion the adjournmentMotion to set
	 */
	public void setAdjournmentMotion(AdjournmentMotion adjournmentMotion) {
		this.adjournmentMotion = adjournmentMotion;
	}
	
	/**
	 * @return the specialMentionNotice
	 */
	public SpecialMentionNotice getSpecialMentionNotice() {
		return specialMentionNotice;
	}

	/**
	 * @param specialMentionNotice the specialMentionNotice to set
	 */
	public void setSpecialMentionNotice(SpecialMentionNotice specialMentionNotice) {
		this.specialMentionNotice = specialMentionNotice;
	}

	/**
     * Gets the device type.
     *
     * @return the device type
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }    
    
	public RulesSuspensionMotion getRulesSuspensionMotion() {
		return rulesSuspensionMotion;
	}

	public void setRulesSuspensionMotion(RulesSuspensionMotion rulesSuspensionMotion) {
		this.rulesSuspensionMotion = rulesSuspensionMotion;
	}
    
    
    /**** Search questions for clubbing 
     * @throws ELSException ****/
    public static List<QuestionSearchVO> fullTextSearchClubbing(final String param,
			final Question question,final int start,final int noOfRecords,final String locale,final Map<String, String[]> requestMap) throws ELSException {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, question, start, noOfRecords, locale,requestMap);
	}
    
    /**** Search questions for seacrhfacility ****/
    public static List<QuestionSearchVO> fullTextSearchForSearching(final String param,
    		final DeviceType deviceType,
    		final Session session,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, 
			String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchForSearchFacility(param, deviceType, session, start, noOfRecords, locale,requestMap);
	}
    
    public static List<QuestionSearchVO> fullTextSearchForSearching(final String param,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, 
			String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchForSearchFacility(param, start, noOfRecords, locale,requestMap);
	}
    
    /**** Club question ****/
    public static String club(final Long questionBeingProcessed,final Long questionBeingClubbed,final String locale){
        return getClubbedEntityRepository().club(questionBeingProcessed,questionBeingClubbed,locale);
    }
    
    /**** Club motion ****/
    public static String clubMotion(final Long motionBeingProcessed,final Long motionBeingClubbed,final String locale){
        return getClubbedEntityRepository().clubMotion(motionBeingProcessed, motionBeingClubbed, locale);
    }
    
    /**** Update the clubbing of given question ****/
   	public static Question updateClubbing(final Question domain) {
   		return getClubbedEntityRepository().updateClubbing(domain);
   	}   	
    
    /**** Unclub question and its parent ****/
   	public static Question unclub(final Question domain){
   		return getClubbedEntityRepository().unclub(domain);
   	}
   	
   	/**** Unclub question and its parent ****/
   	public static Question unclubChildrenWithStatus(final Question domain,final Status status){
   		return getClubbedEntityRepository().unclubChildrenWithStatus(domain,status);
   	}
   	
	/**** Unclub question ****/
    public static String unclub(final Long questionBeingProcessed,final Long questionBeingClubbed,final String locale){
        return getClubbedEntityRepository().unclub(questionBeingProcessed,questionBeingClubbed,locale);
    }
    
    /**** Unclub question ****/
    public static Question unclubWithoutMerge(final Question questionBeingProcessed,final Question questionBeingClubbed,final String locale){
        return getClubbedEntityRepository().unclubWithoutMerge(questionBeingProcessed,questionBeingClubbed,locale);
    }
    
    /**** Remove Parent ****/
    public static Question removeParent(final Question question) {
    	Question newParent = question;
    	
    	List<ClubbedEntity> entities = Question.findClubbedEntitiesByPosition(question);
    	if(entities != null && entities.size() > 0) {
			// Remove the clubbings from the original parent
    		question.setClubbedEntities(null);
    		question.simpleMerge();
    		
    		ClubbedEntity entity = entities.get(0);
    		newParent = entity.getQuestion();
    		List<ClubbedEntity> newClubbings = entities.subList(1, entities.size());
    		// Set the new parent as the parent of the new clubbings
    		for(ClubbedEntity ce : newClubbings) {
    			Question q = ce.getQuestion();
    			q.setParent(newParent);
    			q.simpleMerge();
    		}
    		// Set the clubbings of the new Parent
    		newParent.setParent(null);
    		newParent.setClubbedEntities(newClubbings);
    		newParent = newParent.simpleMerge();
    	}
    	else {
    		newParent = null;
    	}
    	
    	return newParent;
    }
    
    public static List<BillSearchVO> fullTextSearchClubbing(final String param,
			final Bill bill,final int start,final int noOfRecords,final String locale,final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, bill, start, noOfRecords, locale,requestMap);
	}

    public static String clubBill(final Long billBeingProcessed,final Long billBeingClubbed,final String locale){
        return getClubbedEntityRepository().clubBill(billBeingProcessed,billBeingClubbed,locale);
    }   
    
    public static String unclubBill(final Long billBeingProcessed,final Long billBeingClubbed,final String locale){
        return getClubbedEntityRepository().unclubBill(billBeingProcessed,billBeingClubbed,locale);
    }

    /**** Unclub question ****/
    public static String unclubMotion(final Long motionBeingProcessed,final Long motionBeingClubbed,final String locale){
        return getClubbedEntityRepository().unclubMotion(motionBeingProcessed, motionBeingClubbed, locale);
    }
    
	public static ClubbedEntity findByQuestion(final Question question,final String locale) {
		return getClubbedEntityRepository().findByQuestion(question,locale);
	}
	
	public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final Motion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
    
	public static List<MotionSearchVO> fullTextSearchFiling(final String param,
			final Motion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchFiling(param, motion, start, noOfRecords, locale, requestMap);
	}
	
	public static List<QuestionSearchVO> fullTextSearchClubbing(final String param,
			final StandaloneMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
	
	public static List<QuestionSearchVO> fullTextSearchFiling(final String param,
			final StandaloneMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchFiling(param, motion, start, noOfRecords, locale, requestMap);
	}
	
	public static List<QuestionSearchVO> fullTextSearchFiling(final String param,
			final Resolution motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchFiling(param, motion, start, noOfRecords, locale, requestMap);
	}
	
	/**** Club motion ****/
    public static String clubStandalone(final Long motionBeingProcessed,final Long motionBeingClubbed,final String locale){
        return getClubbedEntityRepository().clubStandalone(motionBeingProcessed, motionBeingClubbed, locale);
    }
    
    public static String unclubStandalone(final Long motionBeingProcessed,
			final Long motionBeingClubbed, final String locale){
    	return getClubbedEntityRepository().unclubStandalone(motionBeingProcessed, motionBeingClubbed, locale);
    }
    
    public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final CutMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
    
    public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final EventMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
    
    public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final DiscussionMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
    
    public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final AdjournmentMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
    
    public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final BillAmendmentMotion motion,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}

	public static List<MotionSearchVO> fullTextSearchClubbing(String param, 
			RulesSuspensionMotion motion, 
			int start, int noOfRecords, String locale, Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, motion, start, noOfRecords, locale, requestMap);
	}
	
	public static List<MotionSearchVO> fullTextSearchClubbing(final String param,
			final SpecialMentionNotice notice,
			final int start,
			final int noOfRecords,
			final String locale,
			final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, notice, start, noOfRecords, locale, requestMap);
	}
}
