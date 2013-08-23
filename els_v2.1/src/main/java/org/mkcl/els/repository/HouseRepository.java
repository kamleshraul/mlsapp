/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.HouseRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.House;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class HouseRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class HouseRepository extends BaseRepository<House,Long>{

	/**
	 * Find current house.
	 *
	 * @param locale the locale
	 * @return the house
	 * @throws ELSException 
	 */
	public House findCurrentHouse(final String locale) throws ELSException{
		Date currentDate=new Date();
		String strquery="SELECT DISTINCT h FROM House h WHERE h.locale=:locale"+
					 " AND h.lastDate>=:currentDate AND h.firstDate<=:currentDate";
		House house = null;
		try{
			Query query=this.em().createQuery(strquery);
			query.setParameter("locale", locale);
			query.setParameter("currentDate", currentDate);
			
			house = (House) query.getSingleResult();	
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("HouseRepository_House_findCurrentHouse", "No house found.");
			throw elsException;
		}
		
		return house;		 
	}

	/**
	 * Find house by to from date.
	 *
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param locale the locale
	 * @return the house
	 * @throws ELSException 
	 */
	public House findHouseByToFromDate(final Date fromDate,final Date toDate,final String locale) throws ELSException{
		String strquery="SELECT DISTINCT h FROM House h WHERE h.locale=:locale"+
				 " AND h.lastDate>=:toDate AND h.firstDate<=:fromDate";
		House house = null;
		try{
			Query query=this.em().createQuery(strquery);
			query.setParameter("locale", locale);
			query.setParameter("toDate", toDate);
			query.setParameter("fromDate", fromDate);
			
			house = (House) query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("HouseRepository_House_findHouseByToFromDate", "No house found.");
			throw elsException;
		}
		return house;		
	}

	/**
	 * Find by house type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the list
	 * @throws ELSException 
	 */
	@SuppressWarnings("unchecked")
	public List<House> findByHouseType(final String houseType, final String locale) throws ELSException {
		String strQuery=null;
		List<House> houses = new ArrayList<House>();
		
		if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
			strQuery="SELECT h FROM House h WHERE h.locale=:locale ORDER BY h.formationDate DESC";
		}else{
			strQuery="SELECT h FROM House h WHERE h.locale=:locale AND h.type.type=:houseType ORDER BY h.formationDate DESC";
		}
		
		try{
			Query query=this.em().createQuery(strQuery);
			query.setParameter("locale", locale);
			if(!houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				query.setParameter("houseType", houseType);
			}
			
			houses = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("HouseRepository_List<House>_findByHouseType", "No house found.");
			throw elsException;
		}
		
		return houses;
	}
}
