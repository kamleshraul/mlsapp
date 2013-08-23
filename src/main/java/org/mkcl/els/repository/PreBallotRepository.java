/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.PreBallotRepository.java
 * Created On: Aug 6, 2013
 * @since 1.0
 */
package org.mkcl.els.repository;

import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Ballot;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.PreBallot;
import org.mkcl.els.domain.Session;
import org.springframework.stereotype.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Class PreBallotRepository.
 *
 * @author vikasg
 * @since 1.0
 */
@Repository
public class PreBallotRepository extends BaseRepository<Ballot, Long> {

	
	/**
	 * Find.
	 *
	 * @param session the session
	 * @param deviceType the device type
	 * @param answeringDate the answering date
	 * @param locale the locale
	 * @return the pre ballot
	 * @throws ELSException the eLS exception
	 * 
	 * Find.
	 */
	public PreBallot find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException{
		
		String query = "SELECT p FROM PreBallot p" +
				" WHERE p.session.id=:sessionId" + 
				" AND p.deviceType.id=:deviceTypeId" +  
				" AND p.answeringDate=:answeringDate" + 
				" AND p.locale=:locale";
		PreBallot preBallot = null;
		try{
			TypedQuery<PreBallot> jpQuery = em().createQuery(query, PreBallot.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("locale", locale);
		
			preBallot = (PreBallot)jpQuery.getSingleResult();
		}catch(NoResultException nre){
			nre.printStackTrace();
			logger.error(nre.getMessage());			
			preBallot = null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("PreBallotRepository_PreBallot_find", "No pre ballot found.");
			throw elsException;	
		}
		
		return preBallot;
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
	public PreBallot find(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException{
		
		String query = "SELECT p FROM PreBallot p" +
				" WHERE p.session.id=:sessionId" + 
				" AND p.deviceType.id=:deviceTypeId" +  
				" AND p.answeringDate=:answeringDate" + 
				" AND p.group.id=:groupId" +
				" AND p.locale=:locale";
		PreBallot preBallot = null;
		try{
			TypedQuery<PreBallot> jpQuery = em().createQuery(query, PreBallot.class);
			jpQuery.setParameter("sessionId", session.getId());
			jpQuery.setParameter("deviceTypeId", deviceType.getId());
			jpQuery.setParameter("groupId", group.getId());
			jpQuery.setParameter("answeringDate", answeringDate);
			jpQuery.setParameter("locale", locale);
		
			preBallot = (PreBallot)jpQuery.getSingleResult();
		}catch(NoResultException nre){
			nre.printStackTrace();
			logger.error(nre.getMessage());			
			preBallot = null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("PreBallotRepository_PreBallot_find", "No pre ballot found.");
			throw elsException;	
		}
		
		return preBallot;
	}
}
