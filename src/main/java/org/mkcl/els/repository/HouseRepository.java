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
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PartyType;
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
	
	public House find(final HouseType houseType, 
			final Date date, 
			final String locale) {
		Search search = new Search();
		
		search.addFilterEqual("type", houseType);
		search.addFilterLessOrEqual("firstDate", date);
		search.addFilterGreaterOrEqual("lastDate", date);
		search.addFilterEqual("locale",locale);
		
		House house = this.searchUnique(search); 
		return house;
	}
	
	public Long findActiveMembersCount(final House house,
			final MemberRole role,
    		final Date date,
    		final String locale) {
		CustomParameter dbDateFormat = CustomParameter.findByName(
				CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = 
			FormaterUtil.formatDateToString(date, dbDateFormat.getValue());
		
    	StringBuffer query = new StringBuffer();
    	query.append("SELECT COUNT(m)" +
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
			" WHERE hmra.fromDate <= '" + strDate + "'" +
			" AND hmra.toDate >= '" + strDate + "'" +
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.locale = '" + locale + "'");
    	
    	TypedQuery<Long> tQuery = 
    		this.em().createQuery(query.toString(), Long.class);
    	Long count = tQuery.getSingleResult();
    	return count;
	}

	public Long findActiveMembersCount(final House house, 
			final PartyType partyType,
			final MemberRole role, 
			final Date date, 
			final String locale) {
		CustomParameter dbDateFormat = CustomParameter.findByName(
				CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = 
			FormaterUtil.formatDateToString(date, dbDateFormat.getValue());
		
		Boolean ofRulingParty = false;
		if(partyType.getType().equals(ApplicationConstants.RULING_PARTY)) {
			ofRulingParty = true;
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT COUNT(m)" +
			" FROM MemberPartyAssociation mpa JOIN mpa.party p" +
			" JOIN mpa.member m" +
			" WHERE mpa.fromDate <= '" + strDate + "'" +
			" AND (mpa.toDate >= '" + strDate + "'" + " OR mpa.toDate IS NULL)" +
			" AND mpa.house.id = " + house.getId() +
			" AND m.locale = '" + locale + "'");
		
		query.append(" AND m.id IN (" +
			" SELECT m1.id"	+
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m1" +
			" WHERE hmra.fromDate <= '" + strDate + "'" +
			" AND hmra.toDate >= '" + strDate + "'" +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.locale = '" + locale + "'" +
			" )");
		
		query.append(" AND (");
		query.append(" p.id IN (" +
			" SELECT p1.id" +
		    " FROM HouseParty hp JOIN hp.parties p1" +
		    " WHERE hp.fromDate <= '" + strDate + "'" +
			" AND hp.toDate >= '" + strDate + "'" +
			" AND hp.house.id = " + house.getId() +
			" AND hp.partyType.id = " + partyType.getId() +
			" AND hp.locale = '" + locale + "'" +
			" )");
		query.append(" OR ");
		query.append(" mpa.isMemberOfRulingParty = " + ofRulingParty);		
		query.append(" )");
		
    	TypedQuery<Long> tQuery = 
    		this.em().createQuery(query.toString(), Long.class);
    	Long count = tQuery.getSingleResult();
    	return count;
	}

	public Long findActiveMembersCount(final House house, 
			final Party party,
			final MemberRole role,
			final Date date, 
			final String locale) {
		CustomParameter dbDateFormat = CustomParameter.findByName(
				CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = 
			FormaterUtil.formatDateToString(date, dbDateFormat.getValue());
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT COUNT(m)" +
			" FROM MemberPartyAssociation mpa JOIN mpa.party p" +
			" JOIN mpa.member m" +
			" WHERE mpa.fromDate <= '" + strDate + "'" +
			" AND (mpa.toDate >= '" + strDate + "'" + " OR mpa.toDate IS NULL)" +
			" AND mpa.house.id = " + house.getId() +
			" AND mpa.party.id = " + party.getId() +
			" AND m.locale = '" + locale + "'");
		
		query.append(" AND m.id IN (" +
			" SELECT m1.id"	+
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m1" +
			" WHERE hmra.fromDate <= '" + strDate + "'" +
			" AND hmra.toDate >= '" + strDate + "'" +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.locale = '" + locale + "'" +
			" )");
		
    	TypedQuery<Long> tQuery = 
    		this.em().createQuery(query.toString(), Long.class);
    	Long count = tQuery.getSingleResult();
    	return count;
	}

	public Long findIndependentMembersCount(final House house, 
			final PartyType partyType,
			final Party party,
			final MemberRole role, 
			final Date date, 
			final String locale) {
		CustomParameter dbDateFormat = CustomParameter.findByName(
				CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = 
			FormaterUtil.formatDateToString(date, dbDateFormat.getValue());
		
		Boolean ofRulingParty = false;
		if(partyType.getType().equals(ApplicationConstants.RULING_PARTY)) {
			ofRulingParty = true;
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT COUNT(m)" +
			" FROM MemberPartyAssociation mpa JOIN mpa.party p" +
			" JOIN mpa.member m" +
			" WHERE mpa.fromDate <= '" + strDate + "'" +
			" AND (mpa.toDate >= '" + strDate + "'" + " OR mpa.toDate IS NULL)" +
			" AND mpa.house.id = " + house.getId() +
			" AND mpa.party.id = " + party.getId() +
			" AND mpa.isMemberOfRulingParty = " + ofRulingParty +			
			" AND m.locale = '" + locale + "'");
		
		query.append(" AND m.id IN (" +
			" SELECT m1.id"	+
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m1" +
			" WHERE hmra.fromDate <= '" + strDate + "'" +
			" AND hmra.toDate >= '" + strDate + "'" +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.locale = '" + locale + "'" +
			" )");
		
    	TypedQuery<Long> tQuery = 
    		this.em().createQuery(query.toString(), Long.class);
    	Long count = tQuery.getSingleResult();
    	return count;
	}

	public List<Member> findActiveMembers(final House house, 
			final PartyType partyType,
			final MemberRole role, 
			final Date date,
			final String sortOrder,
			final String locale) {
		CustomParameter dbDateFormat = CustomParameter.findByName(
				CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = 
			FormaterUtil.formatDateToString(date, dbDateFormat.getValue());
		
		Boolean ofRulingParty = false;
		if(partyType.getType().equals(ApplicationConstants.RULING_PARTY)) {
			ofRulingParty = true;
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT m" +
			" FROM MemberPartyAssociation mpa JOIN mpa.party p" +
			" JOIN mpa.member m" +
			" WHERE mpa.fromDate <= '" + strDate + "'" +
			" AND (mpa.toDate >= '" + strDate + "'" + " OR mpa.toDate IS NULL)" +
			" AND mpa.house.id = " + house.getId() +
			" AND m.locale = '" + locale + "'");
		
		query.append(" AND m.id IN (" +
			" SELECT m1.id"	+
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m1" +
			" WHERE hmra.fromDate <= '" + strDate + "'" +
			" AND hmra.toDate >= '" + strDate + "'" +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.locale = '" + locale + "'" +
			" )");
		
		query.append(" AND (");
		query.append(" p.id IN (" +
			" SELECT p1.id" +
		    " FROM HouseParty hp JOIN hp.parties p1" +
		    " WHERE hp.fromDate <= '" + strDate + "'" +
			" AND hp.toDate >= '" + strDate + "'" +
			" AND hp.house.id = " + house.getId() +
			" AND hp.partyType.id = " + partyType.getId() +
			" AND hp.locale = '" + locale + "'" +
			" )");
		query.append(" OR ");
		query.append(" mpa.isMemberOfRulingParty = " + ofRulingParty);		
		query.append(" )");
		
		query.append(" ORDER BY m.lastName ");
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(ApplicationConstants.ASC);
		}
		else {
			query.append(ApplicationConstants.DESC);
		}
		
    	TypedQuery<Member> tQuery = 
    		this.em().createQuery(query.toString(), Member.class);
    	List<Member> members = tQuery.getResultList();
    	return members;
	}

	public List<Member> findActiveMembers(final House house, 
			final PartyType partyType,
			final MemberRole role, 
			final Date date, 
			final String nameBeginningWith,
			final String sortOrder, 
			final String locale) {
		String name = nameBeginningWith;
		
		CustomParameter dbDateFormat = CustomParameter.findByName(
				CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = 
			FormaterUtil.formatDateToString(date, dbDateFormat.getValue());
		
		Boolean ofRulingParty = false;
		if(partyType.getType().equals(ApplicationConstants.RULING_PARTY)) {
			ofRulingParty = true;
		}
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT m" +
			" FROM MemberPartyAssociation mpa JOIN mpa.party p" +
			" JOIN mpa.member m" +
			" WHERE mpa.fromDate <= '" + strDate + "'" +
			" AND (mpa.toDate >= '" + strDate + "'" + " OR mpa.toDate IS NULL)" +
			" AND mpa.house.id = " + house.getId() +
			" AND m.locale = '" + locale + "'");
		
		query.append(" AND m.id IN (" +
			" SELECT m1.id"	+
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m1" +
			" WHERE hmra.fromDate <= '" + strDate + "'" +
			" AND hmra.toDate >= '" + strDate + "'" +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.locale = '" + locale + "'" +
			" )");
		
		query.append(" AND (");
		query.append(" p.id IN (" +
			" SELECT p1.id" +
		    " FROM HouseParty hp JOIN hp.parties p1" +
		    " WHERE hp.fromDate <= '" + strDate + "'" +
			" AND hp.toDate >= '" + strDate + "'" +
			" AND hp.house.id = " + house.getId() +
			" AND hp.partyType.id = " + partyType.getId() +
			" AND hp.locale = '" + locale + "'" +
			" )");
		query.append(" OR ");
		query.append(" mpa.isMemberOfRulingParty = " + ofRulingParty);		
		query.append(" )");
		
		query.append(" AND (");
		query.append(" m.firstName LIKE '%" + name + "%'" +
			" OR m.middleName LIKE '%" + name + "%'" +
			" OR m.lastName LIKE '%" + name + "%'" +
			" OR CONCAT(m.lastName, ' ', m.firstName) LIKE '%" + name + "%'" +
			" OR CONCAT(m.firstName, ' ', m.lastName) LIKE '%" + name + "%'" +
			" OR CONCAT(m.lastName, ' ', m.firstName, ' ', m.middleName)" +
				" LIKE '%" + name + "%'" +
			" OR CONCAT(m.firstName, ' ', m.middleName, ' ', m.lastName)" +
				" LIKE '%" + name + "%'");
		query.append(" )");
		
		query.append(" ORDER BY m.lastName ");
		if(sortOrder.equals(ApplicationConstants.ASC)) {
			query.append(ApplicationConstants.ASC);
		}
		else {
			query.append(ApplicationConstants.DESC);
		}
		
    	TypedQuery<Member> tQuery = 
    		this.em().createQuery(query.toString(), Member.class);
    	List<Member> members = tQuery.getResultList();
    	return members;
	}
	
}