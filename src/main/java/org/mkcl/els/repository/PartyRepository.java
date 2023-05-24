package org.mkcl.els.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Party;
import org.springframework.stereotype.Repository;

@Repository
public class PartyRepository extends BaseRepository<Party, Serializable> {
	
	@SuppressWarnings("unchecked")
	public List<Party> findActiveParties(final House house, final String locale){
		try{
			StringBuffer strQuery = new StringBuffer("SELECT DISTINCT p FROM MemberPartyAssociation mpa" +
					" JOIN mpa.party p" +
					" WHERE p.isDissolved=:isDissolved" + 
					" AND mpa.house.id=:house" +
					" AND p.locale=:locale");
			
			Query query = this.em().createQuery(strQuery.toString(), Party.class);
			query.setParameter("isDissolved", false);
			query.setParameter("house", house.getId());
			query.setParameter("locale", locale);
			
			return query.getResultList();
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return null; 		
	}
	
	public String findCurrentPartySymbolPhoto(final Party party) {
		try{
			StringBuffer strQuery = new StringBuffer("SELECT DISTINCT ps.symbol FROM Party p" +
					" JOIN p.partySymbols ps" +
					" WHERE p.id=:partyId" + 
					" ORDER BY ps.changeDate DESC LIMIT 1");
			
			Query query = this.em().createQuery(strQuery.toString(), String.class);
			query.setParameter("partyId", party.getId());
			
			String currentPartySymbolPhoto = query.getSingleResult().toString();
			return currentPartySymbolPhoto;
			
		}catch(NoResultException e){
			return "";
		}catch(Exception e){
			logger.error("error", e);
			return "";
		}
	}
	
	public List<MasterVO> getPartyWiseCountOfMemberForMobile(final House house){
		
		List<MasterVO> partyWiseCount = new ArrayList<MasterVO>();
		Date CurrDate = new Date();
		 CustomParameter parameter =
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		 String strDate = FormaterUtil.formatDateToString(CurrDate, parameter.getValue());
			
		try{
			List<Object> count  = new ArrayList<Object>();
			String strQuery =
					 "SELECT p.id AS 'Id' , p.`name` AS 'Party Name',COUNT(mhr.`member`) AS 'Member Count' FROM `members_houses_roles` mhr "
					+ " INNER JOIN `members_parties` mp ON (mp.`member`  = mhr.`member` AND mp.`house_id` = mhr.`house_id`) "
					+ " INNER JOIN `parties` p ON (p.`id` = mp.`party`)"
					+ " WHERE mhr.`from_date` <= '" + strDate + "'" 
					+ " AND mhr.`to_date` >= '" + strDate + "'" 
					+ " AND mp.`from_date` <= '" + strDate + "'" 
					+ " AND mp.`to_date` >= '" + strDate + "'" 
					+ " AND mhr.`house_id` =:houseId "
					+ " AND mhr.`is_sitting` = TRUE"
					+ " GROUP BY p.`name`";
					
			
		 
			Query query = this.em().createNativeQuery(strQuery);
			query.setParameter("houseId", house.getId());
			
			//System.out.println(query.toString());
			 count = query.getResultList();	
			 MasterVO arr = null;
			 if(count != null && count.size()>0) {				
				 for(Object i : count) {
					 arr =new MasterVO();
					 Object[] o=(Object[]) i;
					 arr.setId(Long.parseLong(o[0].toString()));
					 arr.setDisplayName(o[1].toString());
					 arr.setNumber(Integer.parseInt(o[2].toString()));
					partyWiseCount.add(arr);
				 }
			 }
			 
			
			
		}catch(NoResultException e){
			e.printStackTrace();
		}catch(Exception e){
			logger.error("error", e);
			e.printStackTrace();
		}
		
		return partyWiseCount;
	}
	

}
