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

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.HouseType;
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
	 * @throws ELSException 
	 */
	public List<MasterVO> findVOByDefaultStateAndHouseType(final String defaultState,
			final String houseType, final String locale, final String sortBy, final String sortOrder) throws ELSException {
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
		List<MasterVO> constituencyVOs=new ArrayList<MasterVO>();
		
		try{String strQuery=null;
			Query jpQuery = null;
			if(houseType.equals(ApplicationConstants.BOTH_HOUSE)){
				org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CONSTITUENCY_BY_DEFAULT_STATE_HOUSETYPE_BOTH", null);
				/*query="SELECT rs.cname,rs.cnumber,rs.cid FROM " +
						"(" +
						"(SELECT distinct(c.name) as cname,c.number as cnumber,c.id as cid FROM constituencies AS c "+
				  " WHERE c.id IN("+
				  "SELECT constituency_id FROM constituencies_districts AS a WHERE district_id IN("+
					"SELECT d.id FROM districts AS d JOIN divisions AS di JOIN states AS s "+
					"WHERE s.locale='"+locale+"' AND s.name='"+defaultState+"' AND d.division_id=di.id AND di.state_id=s.id)))"+
					" UNION " +
					"(SELECT distinct(c.name) as cname,c.number as cnumber,c.id as cid FROM constituencies AS c " +
					" WHERE c.id NOT IN (SELECT constituency_id FROM constituencies_districts))" +
					") as rs ORDER BY rs.cname";*/
				if(query != null){
					strQuery = query.getQuery();
					jpQuery = this.em().createNativeQuery(strQuery);
					jpQuery.setParameter("locale", locale );
					jpQuery.setParameter("defaultState", defaultState);				
				}
				
			}else{
				org.mkcl.els.domain.Query query = org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", "CONSTITUENCY_BY_DEFAULT_STATE_HOUSETYPE_INDIVIDUAL", null);
				
				/*strQuery="SELECT rs.cname,rs.cnumber,rs.cid FROM " +
						"(" +
						"SELECT distinct(c.name) as cname,c.number as cnumber,c.id as cid FROM constituencies AS c JOIN housetypes "+
				  "AS h WHERE h.type='"+houseType+"' AND c.id IN("+
				  "SELECT constituency_id FROM constituencies_districts AS a WHERE district_id IN("+
					"SELECT d.id FROM districts AS d JOIN divisions AS di JOIN states AS s "+
					"WHERE s.locale='"+locale+"' AND s.name='"+defaultState+"' AND d.division_id=di.id AND di.state_id=s.id))"+
					" UNION " +
	                " (SELECT distinct(c.name) as cname,c.number as cnumber,c.id as cid FROM constituencies AS c JOIN housetypes as ht " +
	                " WHERE c.id NOT IN (SELECT constituency_id FROM constituencies_districts) AND ht.type='"+houseType+"' "+
	                " )) as rs ORDER BY rs.cname";*/
				
				if(query != null){
					strQuery = query.getQuery();
					jpQuery = this.em().createNativeQuery(strQuery);
					jpQuery.setParameter("houseTypeA", houseType);	
					jpQuery.setParameter("locale", locale);
					jpQuery.setParameter("defaultState", defaultState);	
					jpQuery.setParameter("houseTypeB",houseType);	
				}
			}
			
			if(jpQuery != null){
				List constituencies= jpQuery.getResultList();
				NumberFormat numFormat=NumberFormat.getInstance(new Locale("hi","IN"));
				for (Object i : constituencies) {
					Object[] o = (Object[]) i;
					Long numb = null;
					String number = "";
					MasterVO constituencyVO = null;
					if (o[1] != null) {
						numb = Long.parseLong(o[1].toString().trim());
						number = numFormat.format(numb);
						constituencyVO = new MasterVO(Long.parseLong(o[2].toString()), number + "-" + o[0].toString().trim());
					} else {
						constituencyVO = new MasterVO(Long.parseLong(o[2].toString()), o[0].toString().trim());
					}
					constituencyVOs.add(constituencyVO);
				}
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ConstituencyRepository_List<MasterVO>_findVOByDefaultStateAndHouseType", "No constituency found.");
			throw elsException;
		}
		
		return constituencyVOs;
	}

	public List<MasterVO> findAllByHouseType(final String houseType, final String locale) throws ELSException {
		/*String query="SELECT c.id,c.display_name FROM constituencies as c JOIN housetypes as ht WHERE ht.type='"+houseType+"' and ht.id=c.housetype_id ORDER BY c.name ASC";*/
		
		String strQuery = "SELECT c.id, c.display_name FROM constituencies c"+
							" INNER JOIN housetypes ht ON (ht.id=c.housetype_id) WHERE ht.type=:houseType"+
							" AND c.is_retired IS false "+
							" ORDER BY (CASE"+
											" WHEN c.number IS NOT NULL AND c.number<>'' THEN 1"+
											" ELSE 2"+
									  " END),"+
									  " (CASE"+
											" WHEN ht.type='upperhouse' THEN c.display_name"+
											" ELSE 1"+
									  " END),"+
									  " CONVERT(c.number, SIGNED)";
		
		List<MasterVO> constituencyVOs=new ArrayList<MasterVO>();
		try{
			Query query = this.em().createNativeQuery(strQuery);
			query.setParameter("houseType", houseType);
			List constituencies = new ArrayList();					
			constituencies = query.getResultList();		
			for(Object i:constituencies){
				Object[] o=(Object[]) i;
				MasterVO masterVO=new MasterVO(Long.parseLong(o[0].toString()),(String)o[1]);
				constituencyVOs.add(masterVO);
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ConstituencyRepository_List<MasterVO>_findAllByHouseType", "No constituency found.");
			throw elsException;
		}
		
		return constituencyVOs;
	}

	@SuppressWarnings("unchecked")
	public List<Constituency> findAllByDisplayName(final HouseType houseType,
			final String displayName, final String locale) throws ELSException {
		
		String strQuery = "SELECT c FROM Constituency c" +
						" WHERE c.displayName = :displayName" +
						" AND c.houseType.id=:houseTypeId" + 
						" AND c.locale=:locale"; 
				
		List<Constituency> constituencies = new ArrayList<Constituency>();
		try{
			TypedQuery<Constituency> jpQuery = this.em().createQuery(strQuery,Constituency.class);
			jpQuery.setParameter("displayName", displayName );
			jpQuery.setParameter("houseTypeId", houseType.getId());
			jpQuery.setParameter("locale", locale);
			
		
			List<Constituency> cX = jpQuery.getResultList();
			if(cX != null){
				constituencies = cX;
			}
		}catch(Exception e) {	
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ConstituencyRepository_List<Constituency>_findAllByDisplayName", "No contituency found.");
			throw elsException;
		}
		return constituencies;
	}
	
	public List<Constituency> findConstituenciesByDistrictId(final Long districtId,
			final String houseType,final String sortBy, final String sortOrder, final String locale) throws ELSException {
		String strquery = "SELECT c FROM Constituency c" +
						" JOIN c.houseType AS ht"+
						" JOIN c.districts AS d"+
					" WHERE d.id=:districtId" +
					" AND ht.type=:houseType "+
					" AND c.locale=:locale" +
					" ORDER BY d."+ sortBy + " " + sortOrder;
		
		List<Constituency> constituencies=new ArrayList<Constituency>();
		
		try{
			Query query = this.em().createQuery(strquery);
			query.setParameter("districtId", districtId);
			query.setParameter("houseType", houseType);
			query.setParameter("locale", locale);
			 constituencies= query.getResultList();
			
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("ConstituencyRepository_List<Reference>_findConstituenciesByDistrictId", "No Constituency found.");
			throw elsException;
		}
		
		return constituencies;
	}

}
