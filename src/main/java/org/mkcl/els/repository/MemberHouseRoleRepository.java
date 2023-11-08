/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberHouseRoleRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.stereotype.Repository;


import com.trg.search.Search;

/**
 * The Class MemberHouseRoleRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberHouseRoleRepository extends
BaseRepository<HouseMemberRoleAssociation, Serializable> {

	/**
	 * Find by member id and id.
	 *
	 * @param memberId the member id
	 * @param recordIndex the record index
	 * @return the house member role association
	 */
	public HouseMemberRoleAssociation findByMemberIdAndId(final Long memberId,
			final int recordIndex) {
		String strquery = "SELECT m FROM HouseMemberRoleAssociation m" +
				" WHERE m.member.id=:memberId" + 
				" AND m.recordIndex=:recordIndex";
		
		try{
			Query query=this.em().createQuery(strquery);
			query.setParameter("memberId", memberId);
			query.setParameter("recordIndex", recordIndex);

			return (HouseMemberRoleAssociation) query.getSingleResult();
		}
		catch (NoResultException e) {
			e.printStackTrace();
			return new HouseMemberRoleAssociation();
		}
	}
	
	
//	 public String findByMemberIdAndIdOrderByRecordIndex(final
//	  Long memberId) {
//		 String strquery =
//	  "SELECT m FROM HouseMemberRoleAssociation m" + " WHERE m.member.id=:memberId"
//	  + " AND  whereorder by m.recordIndex desc LIMIT 1";
//	  
//	  try{ 
//		  Query query=this.em().createQuery(strquery);
//	 query.setParameter("memberId", memberId);  
//	 return (HouseMemberRoleAssociation)query.getSingleResult(); 
//	 } 
//	  catch (NoResultException e) { e.printStackTrace();
//	 return new HouseMemberRoleAssociation(); 
//	 } 
//	  }
	 

	/**
	 * Find highest record index.
	 *
	 * @param member the member
	 * @return the int
	 * @throws ELSException 
	 */

	public int findHighestRecordIndex(final Long member) throws ELSException {
		try {
			String strquery = "SELECT m FROM HouseMemberRoleAssociation m WHERE m.member.id=:memberId"
				+" ORDER BY m.recordIndex desc LIMIT 1";
			TypedQuery<HouseMemberRoleAssociation> query=this.em().createQuery(strquery, HouseMemberRoleAssociation.class);
			query.setParameter("memberId", member);
			List<HouseMemberRoleAssociation> associations = query.getResultList();
			if(associations.isEmpty()){
				return 0;
			}else{
				return associations.get(0).getRecordIndex();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberHouseRoleRepository_int_findHighestRecordIndex", "No record count found.");
			throw elsException;
		}
	}

	/**
	 * Find by pk.
	 *
	 * @param association the association
	 * @return the house member role association
	 * @throws ELSException 
	 */
	public HouseMemberRoleAssociation findByPK(
			final HouseMemberRoleAssociation association) throws ELSException {
		try {
			String strQuery="SELECT hmra FROM HouseMemberRoleAssociation hmra" +
					" WHERE hmra.member=:member AND hmra.role=:role" +
					" AND hmra.house=:house AND hmra.recordIndex=:recordIndex" +
					" AND hmra.fromDate=:fromDate AND hmra.toDate=:toDate";
			Query query=this.em().createQuery(strQuery);
			query.setParameter("member", association.getMember());
			query.setParameter("role", association.getRole());
			query.setParameter("house", association.getHouse());
			query.setParameter("recordIndex", association.getRecordIndex());
			query.setParameter("fromDate", association.getFromDate());
			query.setParameter("toDate", association.getToDate());
			return (HouseMemberRoleAssociation) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberHouseRoleRepository_HouseMemberRoleAssociation_findByPK", "No role found.");
			throw elsException;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HouseMemberRoleAssociation> findByMemberIdRolePriorityHouseId(
			final Long member, final int rolepriority, final Long house, final String locale) throws ELSException {
		try {
			String strquery="SELECT hmra FROM HouseMemberRoleAssociation hmra JOIN hmra.role r JOIN hmra.member m JOIN hmra.house h" +
			" WHERE hmra.locale=:locale AND m.id=:memberId AND r.priority=:priority AND h.id=:houseId";
			Query query=this.em().createQuery(strquery);
			query.setParameter("locale", locale);
			query.setParameter("memberId", member);
			query.setParameter("priority", rolepriority);
			query.setParameter("houseId", house);
			List<HouseMemberRoleAssociation> houseMemberRoleAssociations=query.getResultList();
			return houseMemberRoleAssociations;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberHouseRoleRepository_List<HouseMemberRoleAssociation>_findByMemberIdRolePriorityHouseId", "No role association found.");
			throw elsException;
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MasterVO> findAllActiveMemberVOSInSession(final House house,
			final Session session, final String locale) {
		List<MasterVO> memberVOS=new ArrayList<MasterVO>();
		try {
			Date sessionStartDate=session.getStartDate();
			Date sessionEndDate=session.getEndDate();
			
			if(sessionStartDate!=null && sessionEndDate!=null){
				SimpleDateFormat format=new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT);
				String strSessionStartDate=format.format(sessionStartDate);
				String strSessionEndDate=format.format(sessionEndDate);
				org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MEMBERHOUSEROLE_FIND_ALL_ACTIVE_MEMBERVOS_IN_SESSION, "");
				String strquery = qQuery.getQuery();
				Query query=this.em().createNativeQuery(strquery);
				query.setParameter("locale", locale);
				query.setParameter("strSessionStartDate", strSessionStartDate);
				query.setParameter("strSessionEndDate", strSessionEndDate);
				query.setParameter("houseId", house.getId());
				List members=query.getResultList();
				for(Object i:members){
					Object[] o=(Object[]) i;
					MasterVO masterVO=new MasterVO();
					masterVO.setId(Long.parseLong(o[0].toString()));
					if(o[3]!=null){
						masterVO.setName(o[4].toString()+", "+o[1].toString()+" "+o[2].toString()+" "+o[3].toString());
					}else{
						masterVO.setName(o[4].toString()+", "+o[1].toString()+" "+o[2].toString());
					}
					memberVOS.add(masterVO);
				}
			}
			return memberVOS;
		} catch (Exception e) {
			e.printStackTrace();
			return memberVOS;
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MasterVO> findAllActiveMemberVOSInSession(final House house,
			final Session session, final String locale,final String param) {
		List<MasterVO> memberVOS=new ArrayList<MasterVO>();
		try {
			Date sessionStartDate=session.getStartDate();
			Date sessionEndDate=session.getEndDate();
			if(sessionStartDate!=null && sessionEndDate!=null){
				SimpleDateFormat format=new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT);
				String strSessionStartDate=format.format(sessionStartDate);
				String strSessionEndDate=format.format(sessionEndDate);
				org.mkcl.els.domain.Query qQuery=org.mkcl.els.domain.Query.findByFieldName(org.mkcl.els.domain.Query.class, "keyField", ApplicationConstants.MEMBERHOUSEROLE_FIND_ALL_ACTIVE_MEMBERVOS_IN_SESSION_WITH_PARAM, "");
				String strquery = qQuery.getQuery();
				Query query=this.em().createNativeQuery(strquery);
				query.setParameter("locale", locale);
				query.setParameter("strSessionStartDate", strSessionStartDate);
				query.setParameter("strSessionEndDate", strSessionEndDate);
				query.setParameter("houseId", house.getId());
				query.setParameter("param", "%"+param+"%");
				List members=query.getResultList();
				for(Object i:members){
					Object[] o=(Object[]) i;
					MasterVO masterVO=new MasterVO();
					masterVO.setId(Long.parseLong(o[0].toString()));
					if(o[3]!=null){
						masterVO.setName(o[1].toString()+", "+o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
					}else{
						masterVO.setName(o[1].toString()+", "+o[2].toString()+" "+o[3].toString());
					}
					memberVOS.add(masterVO);
				}
			}
			return memberVOS;
		} catch (Exception e) {
			e.printStackTrace();
			return memberVOS;
		}
	}

	/**
	 * Returns null if there are no active house member roles.
	 * @throws ELSException 
	 */
	public List<HouseMemberRoleAssociation> findActiveHouseMemberRoles(final House house,
			final MemberRole role, 
			final Date date,
			final String locale) throws ELSException {
		try {
			String strQuery="SELECT hmra FROM HouseMemberRoleAssociation hmra " +
					"WHERE hmra.fromDate<=:fromDate AND hmra.toDate>=:toDate" +
					" AND hmra.role=:role AND hmra.locale=:locale";
			if(role.getType().equals("MEMBER") 
				|| role.getType().equals("LEADER_OF_OPPOSITION")){
				strQuery = strQuery + " AND hmra.house=:house";
			}
			Query query=this.em().createQuery(strQuery);
			query.setParameter("fromDate", date);
			query.setParameter("toDate", date);
			query.setParameter("role", role);
			if(role.getType().equals("MEMBER") 
					|| role.getType().equals("LEADER_OF_OPPOSITION")){
				query.setParameter("house", house);
			}	
			
			query.setParameter("locale", locale);
			List<HouseMemberRoleAssociation> associations = query.getResultList();
			return associations;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberHouseRoleRepository_List<HouseMemberRoleAssociation>_findActiveHouseMemberRoles", "No active member found.");
			throw elsException;
		}
	}

	public List<MasterVO> findAllActiveMemberVOSInSession(Session session,
			String locale) {
		House house=session.getHouse();
		return findAllActiveMemberVOSInSession(house, session, locale);
	}

	@SuppressWarnings("unchecked")
	public List<Member> findAllActiveMembersInSession(Session session,
			String locale) throws ELSException {
		try {
			String strquery="SELECT m FROM HouseMemberRoleAssociation hmra JOIN hmra.member m JOIN hmra.role r"+
			" WHERE m.locale=:locale AND hmra.fromDate<=:fromDate AND hmra.toDate>=:toDate "+
			" AND hmra.house.id=:houseId AND r.priority=0 ORDER BY m.lastName asc";
			Query query=this.em().createQuery(strquery);
			query.setParameter("locale", locale);
			query.setParameter("fromDate", session.getStartDate());
			query.setParameter("toDate", session.getEndDate());
			query.setParameter("houseId", session.getHouse().getId());
			List<Member> members=query.getResultList();
			return members;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ELSException elsException = new ELSException();
			elsException.setParameter("MemberHouseRoleRepository_List<Member>_findAllActiveMembersInSession", "No member found.");
			throw elsException;
		}
	}

	/**** Anand Kulkarni ****/
	@SuppressWarnings("rawtypes")
	public List<MasterVO> findAllActiveSupportingMemberVOSInSession(final House house,
			final Session session, final String locale,final String param, final Long primaryMemberId) {
		List<MasterVO> memberVOS=new ArrayList<MasterVO>();
		try {
			Date sessionStartDate=session.getStartDate();
			Date sessionEndDate=session.getEndDate();
			String query=null;
			if(sessionStartDate!=null && sessionEndDate!=null){
				SimpleDateFormat format=new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT);
				String strSessionStartDate=format.format(sessionStartDate);
				String strSessionEndDate=format.format(sessionEndDate);
				if(primaryMemberId!=null){
					query="SELECT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr "+
					" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and m.id<>'"+ primaryMemberId+"' and m.locale='"+locale+"' "+
					" and (mhr.to_date>='"+strSessionStartDate+"' or mhr.to_date>='"+strSessionEndDate+"') and mr.priority=0 and mhr.house_id="+house.getId()+" and (m.first_name LIKE '%"+param+"%' OR m.middle_name LIKE '%"+param+"%' OR m.last_name LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.last_name) LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+param+"%') ORDER BY m.first_name asc";
				}else{
					query="SELECT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr "+
					" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and  m.locale='"+locale+"' "+
					" and (mhr.to_date>='"+strSessionStartDate+"' or mhr.to_date>='"+strSessionEndDate+"') and mr.priority=0 and mhr.house_id="+house.getId()+" and (m.first_name LIKE '%"+param+"%' OR m.middle_name LIKE '%"+param+"%' OR m.last_name LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.last_name) LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+param+"%') ORDER BY m.first_name asc";
				}				
				List members=this.em().createNativeQuery(query).getResultList();
				List<Member> activeMinistersList = Member.findActiveMinisters(new Date(), locale);
				String[] memberAsPresidingOfficerRoles = new String[] {"SPEAKER", "DEPUTY_SPEAKER", "CHAIRMAN", "DEPUTY_CHAIRMAN"};
				for(Object i:members){
					Object[] o=(Object[]) i;
					Member member = Member.findById(Member.class, Long.parseLong(o[0].toString()));
					boolean isMemberActiveMinister = false;
					if(activeMinistersList!=null && member!=null) {
						for(Member m: activeMinistersList) {
							if(member.getId().equals(m.getId())) {
								isMemberActiveMinister = true;
								break;
							}
						}
					}
					//if(member.isActiveOnlyAsMember(new Date(), locale)) {
					if(!isMemberActiveMinister && !member.isActiveMemberInAnyOfGivenRolesOn(memberAsPresidingOfficerRoles, new Date(), locale)) {
						MasterVO masterVO=new MasterVO();
						masterVO.setId(Long.parseLong(o[0].toString()));
						if(o[3]!=null){
							masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
						}else{
							masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString());
						}
						memberVOS.add(masterVO);
					}
				}
			}
			
			//suspended members should not allowed for supporting memebers
			// filtering suspended members
			if(memberVOS!=null && memberVOS.size()>0) { 
				List<Long> suspendedMembersIds = Member.getMemberRepository().supspendedMembersIdsList(new Date());
				if(suspendedMembersIds !=null && suspendedMembersIds.size()>0) {
					for(MasterVO m: memberVOS) {
						if(suspendedMembersIds.contains(m.getId()))
							memberVOS.remove(m);						
					}
				}				
			}
			
			return memberVOS;
		} catch (Exception e) {
			e.printStackTrace();
			return memberVOS;
		}
	}
	
	
	public List<MasterVO> findAllActiveSupportingMemberVOSInSessionUpdated(final House house,
			final Session session, final String locale,final String param, final Long primaryMemberId) {
		List<MasterVO> memberVOS=new ArrayList<MasterVO>();
		try {
			
			Member mem = Member.findById(Member.class, primaryMemberId);
			
			MemberPartyAssociation mpa = null;
			for(MemberPartyAssociation MPA : mem.getMemberPartyAssociations())
			{
				if(MPA.getHouse().getId().equals(house.getId())) {
					mpa = MPA;
				}
			}
			mem = null;
			Date sessionStartDate=session.getStartDate();
			Date sessionEndDate=session.getEndDate();
			String query=null;
			if(sessionStartDate!=null && sessionEndDate!=null){
				SimpleDateFormat format=new SimpleDateFormat(ApplicationConstants.DB_DATEFORMAT);
				String strSessionStartDate=format.format(sessionStartDate);
				String strSessionEndDate=format.format(sessionEndDate);
				if(primaryMemberId!=null){
					query="SELECT Distinct m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN members_parties as mpa JOIN memberroles as mr "+
					" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and m.id=mpa.member and mhr.member=m.id and m.id<>'"+ primaryMemberId+"' and m.locale='"+locale+"' "+
					" and (mhr.to_date>='"+strSessionStartDate+"' or mhr.to_date>='"+strSessionEndDate+"') and mr.priority=0 and mpa.house_id="+house.getId()+" and mpa.is_member_of_ruling_party IS "+mpa.getIsMemberOfRulingParty() +"   and (m.first_name LIKE '%"+param+"%' OR m.middle_name LIKE '%"+param+"%' OR m.last_name LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.last_name) LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+param+"%') ORDER BY m.first_name asc";
				}else{
					query="SELECT Distinct m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr  JOIN members_parties as mpa  "+
					" JOIN titles as t WHERE t.id=m.title_id and m.id=mpa.member  and mr.id=mhr.role and mhr.member=m.id and  m.locale='"+locale+"' "+
					" and (mhr.to_date>='"+strSessionStartDate+"' or mhr.to_date>='"+strSessionEndDate+"') and mr.priority=0 and mhr.house_id="+house.getId()+" and mpa.is_member_of_ruling_party IS "+mpa.getIsMemberOfRulingParty() +"   and (m.first_name LIKE '%"+param+"%' OR m.middle_name LIKE '%"+param+"%' OR m.last_name LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.last_name) LIKE '%"+param+"%' OR concat(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+param+"%' OR concat(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+param+"%') ORDER BY m.first_name asc";
				}				
				List members=this.em().createNativeQuery(query).getResultList();
				List<Member> activeMinistersList = Member.findActiveMinisters(new Date(), locale);
				String[] memberAsPresidingOfficerRoles = new String[] {"SPEAKER", "DEPUTY_SPEAKER", "CHAIRMAN", "DEPUTY_CHAIRMAN"};
				for(Object i:members){
					Object[] o=(Object[]) i;
					Member member = Member.findById(Member.class, Long.parseLong(o[0].toString()));
					boolean isMemberActiveMinister = false;
					if(activeMinistersList!=null && member!=null) {
						for(Member m: activeMinistersList) {
							if(member.getId().equals(m.getId())) {
								isMemberActiveMinister = true;
								break;
							}
						}
					}
					//if(member.isActiveOnlyAsMember(new Date(), locale)) {
					if(!isMemberActiveMinister && !member.isActiveMemberInAnyOfGivenRolesOn(memberAsPresidingOfficerRoles, new Date(), locale)) {
						MasterVO masterVO=new MasterVO();
						masterVO.setId(Long.parseLong(o[0].toString()));
						if(o[3]!=null){
							masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
						}else{
							masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString());
						}
						memberVOS.add(masterVO);
					}
				}
			}
			
			//suspended members should not allowed for supporting memebers
			// filtering suspended members
			if(memberVOS!=null && memberVOS.size()>0) { 
				List<Long> suspendedMembersIds = Member.getMemberRepository().supspendedMembersIdsList(new Date());
				if(suspendedMembersIds !=null && suspendedMembersIds.size()>0) {
					for(MasterVO m: memberVOS) {
						if(suspendedMembersIds.contains(m.getId()))
							memberVOS.remove(m);						
					}
				}				
			}
			
			return memberVOS;
		} catch (Exception e) {
			e.printStackTrace();
			return memberVOS;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<MemberRole> findAllActiveRolesOfMemberInSession(Member member, Session session, String locale) {
		List<MemberRole> memberRoles = new ArrayList<MemberRole>();
		try{
			if(member != null && session != null) {
				String strquery="SELECT distinct r FROM HouseMemberRoleAssociation hmra JOIN hmra.member m JOIN hmra.role r"+
						" WHERE m.locale=:locale AND hmra.member.id=:memberId AND (hmra.toDate>=:toDate OR hmra.toDate is NULL)"+
						" AND hmra.house.id=:houseId ORDER BY r.type asc";
				Query query=this.em().createQuery(strquery);
				query.setParameter("locale", locale);
				query.setParameter("memberId", member.getId());
				query.setParameter("toDate", session.getEndDate());
				query.setParameter("houseId", session.getHouse().getId());
				memberRoles=query.getResultList();
			}
			
			return memberRoles;
		}catch (Exception e) {
			logger.error(e.getMessage());
			return memberRoles;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Member> findAllActiveMembersInHouse(final House house, final String locale) {
		String query="SELECT m FROM HouseMemberRoleAssociation hmra JOIN hmra.member m JOIN hmra.role r"+
		" WHERE m.locale='"+locale+"' " +
		" AND (hmra.toDate>='"+FormaterUtil.formatDateToString(new Date(), "yyyy-MM-dd")+"' OR hmra.toDate IS NULL)" +
		" AND hmra.house.id="+house.getId()+" AND r.priority=0 ORDER BY m.lastName asc";
		List<Member> members=this.em().createQuery(query).getResultList();
		return members;
	}

	public List<MasterVO> findActiveMembersInHouseByTerm(House house, String strParam, String locale) {
		List<MasterVO> memberVOs = new ArrayList<MasterVO>();
		try {
				String query="SELECT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr "+
					" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and  m.locale='"+locale+"' "+
					" and mhr.house_id="+house.getId()+" and (m.first_name LIKE '%"+strParam+"%' OR m.middle_name LIKE '%"+strParam+"%' OR m.last_name LIKE '%"+strParam+"%' OR concat(m.last_name,' ',m.first_name) LIKE '%"+strParam+"%' OR concat(m.first_name,' ',m.last_name) LIKE '%"+strParam+"%' OR concat(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+strParam+"%' OR concat(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+strParam+"%' OR concat(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+strParam+"%') ORDER BY m.first_name asc";
				List members=this.em().createNativeQuery(query).getResultList();
				for(Object i:members){
					Object[] o=(Object[]) i;
					MasterVO masterVO=new MasterVO();
					masterVO.setId(Long.parseLong(o[0].toString()));
					if(o[3]!=null){
						masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
					}else{
						masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString());
					}
					memberVOs.add(masterVO);
				}
			return memberVOs;
		} catch (Exception e) {
			e.printStackTrace();
			return memberVOs;
		}
	}
	
	public List<MasterVO> findActiveMembersByTerm( String strParam, String locale) {
		List<MasterVO> memberVOs = new ArrayList<MasterVO>();
		try {
			
			
			List<House> allHouses = House.findAll(House.class, "id",ApplicationConstants.DESC, locale.toString());
			Long currentLowerHouseId = allHouses.get(0).getId();
			

			String q = "SELECT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles AS mhr JOIN members AS m JOIN memberroles AS mr "
					+ " JOIN titles AS t WHERE t.id=m.title_id AND mr.id=mhr.role AND mhr.member=m.id AND  m.locale= '" + locale+"'"
					+ " AND mhr.house_id IN (2,"+currentLowerHouseId+")  AND"
					+ " (m.first_name LIKE '%"+ strParam+"%' OR m.middle_name LIKE '%"+ strParam+"%' OR m.last_name LIKE '%"+ strParam+"%' OR CONCAT(m.last_name,' ',m.first_name) "
					+ " LIKE '%"+ strParam+"%' OR CONCAT(m.first_name,' ',m.last_name) LIKE '%"+ strParam+"%' OR CONCAT(m.last_name,' ',m.first_name,' ',m.middle_name) LIKE '%"+ strParam+"%' "
					+ " OR CONCAT(m.last_name,', ',t.name,' ',m.first_name,' ',m.middle_name) LIKE '%"+ strParam+"%' OR CONCAT(m.first_name,' ',m.middle_name,' ',m.last_name) LIKE '%"+ strParam+"%' )"
					+ " ORDER BY m.first_name ASC";
			
			Query Nquery = this.em().createNativeQuery(q);
			 
			 List members = Nquery.getResultList();
			for(Object i:members){
					Object[] o=(Object[]) i;
					MasterVO masterVO=new MasterVO();
					masterVO.setId(Long.parseLong(o[0].toString()));
					if(o[3]!=null){
						masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
					}else{
						masterVO.setName(o[1].toString()+o[2].toString()+" "+o[3].toString());
						}
						memberVOs.add(masterVO);
					}
						
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return memberVOs;
	}
}
