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

import java.util.Date;
import java.util.List;

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
	 */
	public House findCurrentHouse(final String locale){
		Search search=new Search();
		search.addFilterEqual("locale",locale);
		Date currentDate=new Date();
		search.addFilterGreaterOrEqual("lastDate", currentDate);
		search.addFilterLessOrEqual("firstDate",currentDate);
		return this.searchUnique(search);
	}

	/**
	 * Find house by to from date.
	 *
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param locale the locale
	 * @return the house
	 */
	public House findHouseByToFromDate(final Date fromDate,final Date toDate,final String locale){
		Search search=new Search();
		search.addFilterEqual("locale",locale);
		search.addFilterGreaterOrEqual("lastDate", toDate);
		search.addFilterLessOrEqual("firstDate",fromDate);
		return this.searchUnique(search);
	}

	/**
	 * Find by house type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<House> findByHouseType(final String houseType, final String locale) {
		String query=null;
		if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
			query="SELECT h FROM House h WHERE h.locale='"+locale+"' ORDER BY h.formationDate DESC";
		}else{
			query="SELECT h FROM House h WHERE h.locale='"+locale+"' AND h.type.type='"+houseType+"' ORDER BY h.formationDate DESC";
		}
		return this.em().createQuery(query).getResultList();
	}
}
