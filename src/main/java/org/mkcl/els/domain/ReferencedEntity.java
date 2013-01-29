/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ReferencedEntity.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.repository.ReferencedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class ReferencedEntity.
 *
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="refernced_entities")
public class ReferencedEntity extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The position. */
    private Integer position;

    /** The question. */
    @ManyToOne(fetch=FetchType.LAZY)
    private Question question;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;
    
    @Autowired
    private transient ReferencedEntityRepository referencedEntityRepository;

    /**
     * Instantiates a new referenced entity.
     */
    public ReferencedEntity() {
        super();
    }
    
    public static ReferencedEntityRepository getReferencedEntityRepository() {
    	ReferencedEntityRepository referencedEntityRepository = new ReferencedEntity().referencedEntityRepository;
        if (referencedEntityRepository == null) {
            throw new IllegalStateException(
                    "ReferencedEntityRepository has not been injected in Refrenced Entity Domain");
        }
        return referencedEntityRepository;
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


	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}


	public DeviceType getDeviceType() {
		return deviceType;
	}
	
	public static Boolean referencing(final Long primaryId,final Long referencingId,
			final String locale) {
		return getReferencedEntityRepository().referencing(primaryId,referencingId,
				locale);
	}
	public static Boolean deReferencing(final Long primaryId,final Long referencedId,final String locale) {
		return getReferencedEntityRepository().deReferencing(primaryId,referencedId,locale);
	}
	
	public static List<QuestionSearchVO> fullTextSearchReferencing(
			final String param,
			final Question question,final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencing(param, question, start, noOfRecords, locale);
	}
}
