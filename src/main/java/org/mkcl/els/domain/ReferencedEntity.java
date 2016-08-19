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

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.ResolutionSearchVO;
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
@Table(name="referenced_entities")
public class ReferencedEntity extends BaseDomain implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**** Attributes ****/
    
    /** The position. */
    private Integer position;

    /** The device. */
    @ManyToOne(fetch=FetchType.EAGER)
    private Device device;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private DeviceType deviceType;
    
    @Autowired
    private transient ReferencedEntityRepository referencedEntityRepository;

    

    /**** Constructors ****/

    /**
     * Instantiates a new referenced entity.
     */
    public ReferencedEntity() {
        super();
    }
    
    

    /**** Domain methods ****/
    
    public static ReferencedEntityRepository getReferencedEntityRepository() {
    	ReferencedEntityRepository referencedEntityRepository = new ReferencedEntity().referencedEntityRepository;
        if (referencedEntityRepository == null) {
            throw new IllegalStateException(
                    "ReferencedEntityRepository has not been injected in Refrenced Entity Domain");
        }
        return referencedEntityRepository;
    }
    
    public static Boolean referencing(final Long primaryId,final Long referencingId,
			final String locale) {
		return getReferencedEntityRepository().referencing(primaryId,referencingId,
				locale);
	}
	public static Boolean referencing(final String device, final Long primaryId,final Long referencingId,
			final String locale) {
		return getReferencedEntityRepository().referencing(device, primaryId,referencingId,
				locale);
	}
	public static Boolean referencingMotion(final DeviceType targetDeviceType, final String device,final Long primaryId,final Long referencingId,final String locale) {
		return getReferencedEntityRepository().referencingMotion(targetDeviceType, device, primaryId,referencingId,
				locale);
	}
	
	public static Boolean deReferencing(final Long primaryId,final Long referencedId,final String locale) {
		return getReferencedEntityRepository().deReferencing(primaryId,referencedId,locale);
	}	
	
	public static Boolean deReferencing(final String device, final Long primaryId,final Long referencedId,final String locale) {
		return getReferencedEntityRepository().deReferencing(device, primaryId,referencedId,locale);
	}
	
	public static Boolean deReferencingMotion(final DeviceType targetDeviceType, final String device, final Long primaryId,final Long referencedId,final String locale) {
		return getReferencedEntityRepository().deReferencingMotion(targetDeviceType, device, primaryId,referencedId,locale);
	}
	
	public static List<QuestionSearchVO> fullTextSearchReferencing(
			final String param,
			final Question question,
			final Integer sessionCount,
			final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencing(param, question, sessionCount, start, noOfRecords, locale);
	}
	
	public static List<QuestionSearchVO> fullTextSearchReferencing(
			final String param,
			final Question question,
			final Integer sessionCount,
			final Integer sessionYear, 
			final Long sessionType,
			final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencing(param, question, sessionCount, sessionYear, sessionType, start, noOfRecords, locale);
	}
	
	public static List<MotionSearchVO> fullTextSearchReferencing(
			final String param,
			final Motion motion, final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencing(param, motion, start, noOfRecords, locale);
	}
	
	public static List<ResolutionSearchVO> fullTextSearchReferencingResolution(
			final String param,
			final Resolution resolution, final boolean isAutomatic, final int start,final int noOfRecords,final String locale) throws ELSException {
		return getReferencedEntityRepository().fullTextSearchReferencingResolution(param, resolution, isAutomatic, start, noOfRecords, locale);
	}
	
	public static List<ResolutionSearchVO> fullTextSearchReferencingResolution(
			final String param,
			final Resolution resolution, final Session session,final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencingResolution(param, resolution, session, start, noOfRecords, locale);
	}
	
	public static Integer fullTextSearchReferencingId(final String param,
			final Resolution resolution, final int start, final int noOfRecords, final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencingId(param, resolution, start, noOfRecords, locale);
		
	}
	
	public static List<QuestionSearchVO> fullTextSearchReferencingHDS(String param,
			StandaloneMotion question, Session session, int start, int noOfRecords, String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencingHDS(param, question, session, start, noOfRecords, locale);
	}
	
	public static List<QuestionSearchVO> fullTextSearchReferencingHDS(
			final String param,
			final StandaloneMotion question, final boolean isAutomatic, final int start,final int noOfRecords,final String locale) throws ELSException {
		return getReferencedEntityRepository().fullTextSearchReferencingHDS(param, question, isAutomatic, start, noOfRecords, locale);
	}
	
	public static List<QuestionSearchVO> fullTextSearchReferencingHDS(
			final String param,
			final StandaloneMotion question,final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencingHDS(param, question, start, noOfRecords, locale);
	}
	
	public static List<BillSearchVO> fullTextSearchReferencingBill(final String param, final Bill bill, 
			final String language, final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().fullTextSearchReferencingBill(param, bill, language, start, noOfRecords, locale);		
	}	
	
	public static List<BillSearchVO> exactSearchReferencingBill(final Bill bill, 
			final String language, final int start,final int noOfRecords,final String locale) {
		return getReferencedEntityRepository().exactSearchReferencingBill(bill, language, start, noOfRecords, locale);		
	}

    /**** Getters and Setters ****/
    
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


	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}


	public DeviceType getDeviceType() {
		return deviceType;
	}
	
	/**
	 * @return the device
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * @param device the device to set
	 */
	public void setDevice(Device device) {
		this.device = device;
	}
}
