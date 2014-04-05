/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.ElectionRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Election;
import org.springframework.stereotype.Repository;

/**
 * The Class ElectionRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class ElectionRepository extends BaseRepository<Election,Long> {

	/**
	 * Find by house type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the list
	 * @throws ELSException 
	 */
    public List<Election> findByHouseType(final String houseType,final String locale) throws ELSException {
    	
    	String query=null;
        TypedQuery<Election> jpQuery = null;
        try{
	        if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
	            query="SELECT e FROM Election e WHERE e.locale=:locale";
	            jpQuery = this.em().createQuery(query, Election.class);
	            jpQuery.setParameter("locale", locale);
	            
	        }else{
	            query="SELECT e FROM Election e" +
	            		" WHERE e.locale=:locale" +
	            		" AND e.electionType.houseType.type=:houseType";
	            
	            jpQuery = this.em().createQuery(query, Election.class);
	            jpQuery.setParameter("locale", locale);
	            jpQuery.setParameter("houseType", houseType);
	
	        }
        }catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ElectionRepository_List<Election>_findByHouseType", "Election list is unavailable.");
			throw elsException;
		}
        return jpQuery.getResultList();
    }

}
