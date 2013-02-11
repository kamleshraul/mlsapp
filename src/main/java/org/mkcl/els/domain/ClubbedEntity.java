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
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    /** The position. */
    private Integer position;

    /** The device type. */
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;

    /** The question. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Question question;

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


    /**
     * Gets the device type.
     *
     * @return the device type
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }
    
    public static String club(final Long questionBeingProcessed,final Long questionBeingClubbed,final String locale){
        return getClubbedEntityRepository().club(questionBeingProcessed,questionBeingClubbed,locale);
    }
    
    public static String unclub(final Long questionBeingProcessed,final Long questionBeingClubbed,final String locale){
        return getClubbedEntityRepository().unclub(questionBeingProcessed,questionBeingClubbed,locale);
    }
    
    public static List<QuestionSearchVO> fullTextSearchClubbing(final String param,
			final Question question,final int start,final int noOfRecords,final String locale,final Map<String, String[]> requestMap) {
		return getClubbedEntityRepository().fullTextSearchClubbing(param, question, start, noOfRecords, locale,requestMap);
	}

    /**
     * If @param question has clubbings then remove @param question 
     * as the parent of it's clubbings. Elect a new parent from among
     * the clubbings.
     * 
     * Return @param question if it has no clubbings, else
     * Return the new elected parent. 
     */
    public static Question removeParent(final Question question) {
    	Question newParent = question;
    	
    	List<ClubbedEntity> entities = Question.findClubbedEntitiesByPosition(question);
    	if(entities != null) {
    		if(entities.size() > 0) {
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
    	}
    	
    	return newParent;
    }
}
