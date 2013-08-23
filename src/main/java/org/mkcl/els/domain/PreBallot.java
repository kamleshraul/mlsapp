/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.PreBallot.java
 * Created On: Aug 6, 2013
 * @since 1.0
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.repository.PreBallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class PreBallot.
 *
 * @author vikasg
 * @since 1.0
 */
@Configurable
@Entity
@Table(name="preballots")
public class PreBallot extends BaseDomain implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8638638668842587956L;

	/**** ATTRIBUTES ****/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	/** The device type. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;
	
	/** The answering date. */
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	/** The group. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private Group group;
	
	/** The ballot entries. */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="preballots_ballot_entries",
			joinColumns={ @JoinColumn(name="preballot_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") })
	private List<BallotEntry> ballotEntries;

	/** The pre ballot date. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date preBallotDate;
	
	/** The pre ballot repository. */
	@Autowired
	private transient PreBallotRepository preBallotRepository;
	

	
	/**** CONSTRUCTORS ****/
	
	/**
	 * Not to be used. Kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public PreBallot() {
		super();
	}

	/**
	 * Can be used with devices having no group.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param answeringDate the answering date
	 * @param preBallotDate the pre ballot date
	 * @param locale the locale
	 */
	public PreBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Date preBallotDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setPreBallotDate(preBallotDate);
	}
	
	/**
	 * Tobe used for devices having group.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param preBallotDate the pre ballot date
	 * @param locale the locale
	 */
	public PreBallot(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Date preBallotDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setPreBallotDate(preBallotDate);
	}
	
	/**** Domain Methods ****/
	
	/**
	 * @param session the session
	 * @param deviceType the device type
	 * @param answeringDate the answering date
	 * @param locale the locale
	 * @return the pre ballot
	 * @throws ELSException the eLS exception
	 * 
	 * Find.
	 */
	public static PreBallot find(final Session session,
					final DeviceType deviceType,
					final Date answeringDate,
					final String locale) throws ELSException{
		return PreBallot.getRepository().find(session, deviceType, answeringDate, locale);
	}
	
	/**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param group the group
	 * @param answeringDate the answering date
	 * @param locale the locale
	 * @return the pre ballot
	 * @throws ELSException the eLS exception
	 * 
	 * Find.
	 */
	public static PreBallot find(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException{
		return PreBallot.getRepository().find(session, deviceType, group, answeringDate, locale);
	}
	
	/**** Internal Methods ****/
	/**
	 * @return the repository
	 */
	private static PreBallotRepository getRepository() {
		PreBallotRepository repository = new PreBallot().preBallotRepository;
		if(repository == null) {
			throw new IllegalStateException(
				"PreBallotRepository has not been injected in Ballot Domain");
		}
		return repository;
	}
	
	public static List<BallotVO> getBallotVOFromBallotEntries(final List<BallotEntry> ballotEntries, final String locale){
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		
		for(BallotEntry bE : ballotEntries){
			
			for(DeviceSequence ds : bE.getDeviceSequences()){
				BallotVO preBallotVO = new BallotVO();
				if(ds.getDevice() instanceof Question){
					
					Question q = (Question) ds.getDevice();
					
					preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
					preBallotVO.setQuestionNumber(q.getNumber());
					preBallotVO.setQuestionSubject(q.getSubject());
				}else if(ds.getDevice() instanceof Resolution){
					Resolution r = (Resolution) ds.getDevice();
					
					preBallotVO.setMemberName(r.getMember().getFullname());
					preBallotVO.setQuestionNumber(r.getNumber());
					preBallotVO.setQuestionSubject(r.getSubject());
				}
				
				preBallotVOs.add(preBallotVO);				
			}
		}
		
		return preBallotVOs;
	}
	
	/**** Getters and Setters ****/
	/** 
	 *
	 * @return the session
	 */
	
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the session.
	 *
	 * @param session the new session
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Gets the device type.
	 *
	 * @return the device type
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * Sets the device type.
	 *
	 * @param deviceType the new device type
	 */
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * Gets the answering date.
	 *
	 * @return the answering date
	 */
	public Date getAnsweringDate() {
		return answeringDate;
	}

	/**
	 * Sets the answering date.
	 *
	 * @param answeringDate the new answering date
	 */
	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets the group.
	 *
	 * @param group the new group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * Gets the ballot entries.
	 *
	 * @return the ballot entries
	 */
	public List<BallotEntry> getBallotEntries() {
		return ballotEntries;
	}

	/**
	 * Sets the ballot entries.
	 *
	 * @param ballotEntries the new ballot entries
	 */
	public void setBallotEntries(List<BallotEntry> ballotEntries) {
		this.ballotEntries = ballotEntries;
	}

	/**
	 * Gets the pre ballot date.
	 *
	 * @return the pre ballot date
	 */
	public Date getPreBallotDate() {
		return preBallotDate;
	}

	/**
	 * Sets the pre ballot date.
	 *
	 * @param preBallotDate the new pre ballot date
	 */
	public void setPreBallotDate(Date preBallotDate) {
		this.preBallotDate = preBallotDate;
	}	
}
