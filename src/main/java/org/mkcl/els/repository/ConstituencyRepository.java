/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.ConstituencyRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.springframework.stereotype.Repository;


/**
 * The Class ConstituencyRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class ConstituencyRepository extends BaseRepository<Constituency, Long>{

	/**
	 * Find vo by default state and house type.
	 *
	 * @param defaultState the default state
	 * @param houseType the house type
	 * @param locale the locale
	 * @param sortBy the sort by
	 * @param sortOrder the sort order
	 * @return the list
	 */
	public List<MasterVO> findVOByDefaultStateAndHouseType(final String defaultState,
			final String houseType, final String locale, final String sortBy, final String sortOrder) {
		 /***********************Both queries can be used************************************************
		 /************** query1:SELECT c.display_name,c.id FROM masters_constituencies AS c WHERE c.id IN(
			SELECT constituency_id FROM associations_constituency_district AS a WHERE district_id IN(
			SELECT d.id FROM masters_districts AS d JOIN masters_divisions AS di JOIN masters_states AS s WHERE s.locale='en_US' AND s.name='Maharashtra'
			AND d.division_id=di.id AND di.state_id=s.id));
		 *************************************************************************************************/
		/***************query2:SELECT c.display_name,c.id FROM masters_constituencies AS c JOIN masters_housetype AS h WHERE h.type='lowerhouse' AND c.id IN(
			SELECT constituency_id FROM associations_constituency_district AS a WHERE district_id IN(
			SELECT d.id FROM masters_districts AS d JOIN masters_divisions AS di JOIN masters_states AS s WHERE s.locale='en_US' AND s.name='Maharashtra'
			AND d.division_id=di.id AND di.state_id=s.id));
		 *************************************************************************************************/
		String query=null;
		if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
			query="SELECT distinct(c.name),c.number,c.id FROM constituencies AS c "+
			  " WHERE c.id IN("+
			  "SELECT constituency_id FROM constituencies_districts AS a WHERE district_id IN("+
				"SELECT d.id FROM districts AS d JOIN divisions AS di JOIN states AS s "+
				"WHERE s.locale='"+locale+"' AND s.name='"+defaultState+"' AND d.division_id=di.id AND di.state_id=s.id))"+
				" ORDER BY c.name";
		}else{
			query="SELECT distinct(c.name),c.number,c.id FROM constituencies AS c JOIN housetypes "+
			  "AS h WHERE h.type='"+houseType+"' AND c.id IN("+
			  "SELECT constituency_id FROM constituencies_districts AS a WHERE district_id IN("+
				"SELECT d.id FROM districts AS d JOIN divisions AS di JOIN states AS s "+
				"WHERE s.locale='"+locale+"' AND s.name='"+defaultState+"' AND d.division_id=di.id AND di.state_id=s.id))"+
				" ORDER BY c.name";
		}
		List constituencies= this.em().createNativeQuery(query).getResultList();
		List<MasterVO> constituencyVOs=new ArrayList<MasterVO>();
		NumberFormat numFormat=NumberFormat.getInstance(new Locale("hi","IN"));
		for(Object i:constituencies){
			Object[] o=(Object[]) i;
			Long numb=Long.parseLong(o[1].toString().trim());
			String number=numFormat.format(numb);
			MasterVO constituencyVO=new MasterVO(Long.parseLong(o[2].toString()),o[0].toString().trim()+","+number);
			constituencyVOs.add(constituencyVO);
		}
		return constituencyVOs;
	}

}
