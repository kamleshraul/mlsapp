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
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.ballot.Ballot;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.ballot.PreBallot;
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
	
	public PreBallot find(final Device device) throws ELSException{
		PreBallot preBallot = null;
		
		StringBuffer strQuery = new StringBuffer();
		strQuery.append(
			"SELECT b" +
			" FROM PreBallot b JOIN b.ballotEntries be" +
			" JOIN be.deviceSequences ds" +
			" JOIN ds.device d" +
			" WHERE d.id = :deviceId");
		
		TypedQuery<PreBallot> jpQuery = this.em().createQuery(strQuery.toString(), PreBallot.class);
		jpQuery.setParameter("deviceId", device.getId());
		try {
			preBallot = jpQuery.getSingleResult();
		}
		catch(EntityNotFoundException enfe){
			logger.error(enfe.getMessage());
		}
		catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("PreBallotRepository_PreBallot_find(DT)", "PreBallot not found.");
			throw elsException;
		}
		return preBallot;		
	}

	// If this method is executed in non transactional block, it throws the following exception:
	// javax.persistence.TransactionRequiredException: Executing an update/delete query
	@SuppressWarnings({ "rawtypes" })
	public boolean optimizedRemove(final PreBallot preBallot) {
		try {
			Long id = preBallot.getId();
			
			// Get ballot_entry_id's
			String getBallotEntryIds = "SELECT pbe.ballot_entry_id" +
					" FROM preballots_ballot_entries AS pbe" + 
					" WHERE pbe.preballot_id=:preBallotId";
			Query q1 = this.em().createNativeQuery(getBallotEntryIds);
			q1.setParameter("preBallotId", id);
			List ballotEntryIds = q1.getResultList();
			
			// Get device_sequence_id's
			String getDeviceSequenceIds = "SELECT ds.id" +
					" FROM device_sequences AS ds" +
					" WHERE ds.id IN (" +
						" SELECT beds.device_sequence_id" + 
						" FROM ballot_entries_device_sequences AS beds" + 
						" WHERE beds.ballot_entry_id IN (" +
							" SELECT pbe.ballot_entry_id" + 
							" FROM preballots_ballot_entries AS pbe" + 
							" WHERE pbe.preballot_id=:preBallotId))";
			Query q2 = this.em().createNativeQuery(getDeviceSequenceIds);
			q2.setParameter("preBallotId", id);
			List deviceSequenceIds = q2.getResultList();
			
			// Delete from ballot_entries_device_sequences table
			String deleteBallotEntryDeviceSequences = "DELETE FROM ballot_entries_device_sequences" +
					" WHERE ballot_entry_id IN (" +
						" SELECT pbe.ballot_entry_id" + 
						" FROM preballots_ballot_entries AS pbe" + 
						" WHERE pbe.preballot_id=:preBallotId)";
			Query q3 = this.em().createNativeQuery(deleteBallotEntryDeviceSequences);
			q3.setParameter("preBallotId", id);
			q3.executeUpdate();
			
			// Delete from preballots_ballot_entries table
			String deletePreBallotBallotEntry = "DELETE FROM preballots_ballot_entries WHERE preballot_id=:preBallotId";
			Query q4 = this.em().createNativeQuery(deletePreBallotBallotEntry);
			q4.setParameter("preBallotId", id);
			q4.executeUpdate();
			
			// Delete from ballot_entries table
			if(ballotEntryIds != null && ! ballotEntryIds.isEmpty()) {
				StringBuffer commaSeparatedBallotEntryIds = new StringBuffer("(");
				int length = ballotEntryIds.size();
				int counter = 0;
				for(Object l : ballotEntryIds) {
					++counter;
					commaSeparatedBallotEntryIds.append(l);
					if(counter < length) {
						commaSeparatedBallotEntryIds.append(", ");
					}
				}
				commaSeparatedBallotEntryIds.append(")");
				
				String str = commaSeparatedBallotEntryIds.toString();
				String deleteBallotEntries = "DELETE FROM ballot_entries WHERE id IN " + str;
				Query q5 = this.em().createNativeQuery(deleteBallotEntries);
				q5.executeUpdate();
			}
			
			// Delete from device_sequences table
			if(deviceSequenceIds != null && ! deviceSequenceIds.isEmpty()) {
				StringBuffer commaSeparatedDeviceSequenceIds = new StringBuffer("(");
				int length = deviceSequenceIds.size();
				int counter = 0;
				for(Object l : deviceSequenceIds) {
					++counter;
					commaSeparatedDeviceSequenceIds.append(l);
					if(counter < length) {
						commaSeparatedDeviceSequenceIds.append(", ");
					}
				}
				commaSeparatedDeviceSequenceIds.append(")");
				
				String str = commaSeparatedDeviceSequenceIds.toString();
				String deleteDeviceSequences = "DELETE FROM device_sequences WHERE id IN " + str;
				Query q6 = this.em().createNativeQuery(deleteDeviceSequences);
				q6.executeUpdate();
			}
			
			// Delete from preBallots table
			String deletePreBallot = "DELETE FROM preballots WHERE id=:preBallotId";
			Query q7 = this.em().createNativeQuery(deletePreBallot);
			q7.setParameter("preBallotId", id);
			q7.executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public boolean optimizedRemoveHDS(final PreBallot preBallot) {
		try {
			Long id = preBallot.getId();
			
			// Get ballot_entry_id's
			String getBallotEntryIds = "SELECT pbe.ballot_entry_id" +
								" FROM preballots_ballot_entries AS pbe" + 
								" WHERE pbe.preballot_id IN (:preBallotId)";
			Query q1 = this.em().createNativeQuery(getBallotEntryIds);
			q1.setParameter("preBallotId", id);
			List ballotEntryIds = q1.getResultList();
			
			
			if(ballotEntryIds != null && ! ballotEntryIds.isEmpty()) {
				StringBuffer commaSeparatedBallotEntryIds = new StringBuffer("(");
				int length = ballotEntryIds.size();
				int counter = 0;
				for(Object l : ballotEntryIds) {
					++counter;
					commaSeparatedBallotEntryIds.append(l);
					if(counter < length) {
						if(counter == length) {
							commaSeparatedBallotEntryIds.append(" ");
						}
						else {
							commaSeparatedBallotEntryIds.append(", ");
						}
					}
				}
			   	commaSeparatedBallotEntryIds.append(")");
			
			   	String str1 = commaSeparatedBallotEntryIds.toString();   
			 
			 		// Get device sequences
			 	String getDeviceSequenceIds = "SELECT beds.device_sequence_id" +
											" FROM ballot_entries_device_sequences AS beds" + 
											" WHERE beds.ballot_entry_id IN "+str1;
		     		Query q2 = this.em().createNativeQuery(getDeviceSequenceIds);
		     	    List deviceSequenceIds = q2.getResultList();
		     	    
		     		// Delete from ballot_entries_device_sequences table
					String deleteBallotEntryDeviceSequences = "DELETE FROM ballot_entries_device_sequences" +
										" WHERE ballot_entry_id IN" + str1;
					Query q3 = this.em().createNativeQuery(deleteBallotEntryDeviceSequences);
					q3.executeUpdate(); 
					
					
					if(deviceSequenceIds != null && !deviceSequenceIds.isEmpty()) {
						StringBuffer commaSeparatedDeviceEntryIds = new StringBuffer("(");
						int deviceLength = deviceSequenceIds.size();
						int deviceCounter = 0;
						for(Object l : deviceSequenceIds) {
							++deviceCounter;
							commaSeparatedDeviceEntryIds.append(l);
							if(deviceCounter < deviceLength) {
								if(deviceCounter == deviceLength) {
									commaSeparatedDeviceEntryIds.append(" ");
								}
								else {
									commaSeparatedDeviceEntryIds.append(", ");
								}
							}
						}
						commaSeparatedDeviceEntryIds.append(")");
					
					   	String str2 = commaSeparatedDeviceEntryIds.toString(); 
					   	
					 // Delete from device_sequences table
						String deleteDeviceSequences = "DELETE FROM device_sequences" +
											" WHERE id IN" + str2;
						Query q4 = this.em().createNativeQuery(deleteDeviceSequences);
						q4.executeUpdate(); 
					}
					
					
					 // Delete from preballots_ballot_entries table
					String deletePreballotEntriesSequences = "DELETE FROM preballots_ballot_entries" +
										" WHERE preballot_id=:preBallotId";
					Query q5 = this.em().createNativeQuery(deletePreballotEntriesSequences);
					q5.setParameter("preBallotId", id);
					q5.executeUpdate(); 
					
					 // Delete from preballots_ballot_entries table
					String deleteballotEntries = "DELETE FROM ballot_entries" +
										" WHERE id IN "+str1;
					Query q7 = this.em().createNativeQuery(deleteballotEntries);
					q7.executeUpdate(); 					
			}		
			// Delete from preballots table
			String deletePreballot = "DELETE FROM preballots" +
					" WHERE id IN (:preBallotId)";
			Query q6 = this.em().createNativeQuery(deletePreballot);
			q6.setParameter("preBallotId", id);
			q6.executeUpdate(); 						
			
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
