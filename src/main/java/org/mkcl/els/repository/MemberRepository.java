/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MemberRepository.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.repository;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ElectionResultVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberCompleteDetailVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.MemberDetailsForAccountingVO;
import org.mkcl.els.common.vo.MemberIdentityVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.PositionHeldVO;
import org.mkcl.els.common.vo.RivalMemberVO;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.FamilyMember;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PartyType;
import org.mkcl.els.domain.PositionHeld;
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.RivalMember;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.User;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MemberRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberRepository extends BaseRepository<Member, Long>{

	@SuppressWarnings({"rawtypes" })
	public List<MemberInfo> search(final String housetype, final Long house, final String criteria1,
			final Long criteria2, final String locale,final String[] councilCriteria) {        
		String selectClause="";
		String fromClause="";
		String whereClause="";
		if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
			selectClause="SELECT rs.title,rs.id,rs.firstname,rs.middlename,rs.lastname,rs.constituency," +
			"rs.partyname,rs.recordindex,rs.fromdate,rs.todate,rs.gender,rs.maritalstatus," +
			"rs.birthdate,rs.district,rs.constituencyname FROM(" +
			"SELECT t.name as title,m.id as id,m.first_name as firstname,m.middle_name as middlename,m.last_name as lastname,c.display_name as constituency,p.name as partyname,mhr.record_index as recordindex,mp.from_date as fromdate,mp.to_date as todate,g.name as gender,ms.name as maritalstatus,m.birth_date as birthdate,d.name as district,c.name as constituencyname ";
			fromClause="FROM members AS m "+
			"LEFT JOIN  members_houses_roles AS mhr ON (mhr.member=m.id) "+
			"LEFT JOIN members_parties AS mp ON(mp.member=m.id) "+
			"LEFT JOIN constituencies AS c ON(c.id=mhr.constituency_id) "+
			"LEFT JOIN parties AS p ON(p.id=mp.party) "+
			"LEFT JOIN titles AS t ON(t.id=m.title_id) "+
			"LEFT JOIN memberroles AS mr ON (mr.id=mhr.role) "+
			"LEFT JOIN genders AS g ON(g.id=m.gender_id) "+
			"LEFT JOIN maritalstatus AS ms ON(ms.id=m.maritalstatus_id) "+
			"LEFT JOIN constituencies_districts as cd ON(cd.constituency_id=c.id) "+
			"LEFT JOIN districts as d ON(d.id=cd.district_id) ";
			whereClause=" WHERE m.locale='"+locale+"' and mr.priority=0 and mhr.house_id="+house+ " and m.death_date is null and c.is_retired=false";
		}else{
			selectClause="SELECT rs.title,rs.id,rs.firstname,rs.middlename,rs.lastname,rs.constituency," +
			"rs.partyname,rs.recordindex,rs.fromdate,rs.todate,rs.gender,rs.maritalstatus," +
			"rs.birthdate,rs.constituencyname FROM(" +
			"SELECT t.name as title,m.id as id,m.first_name as firstname,m.middle_name as middlename,m.last_name as lastname,c.display_name as constituency,p.name as partyname,mhr.record_index as recordindex,mp.from_date as fromdate,mp.to_date as todate,g.name as gender,ms.name as maritalstatus,m.birth_date as birthdate,c.name as constituencyname ";
			fromClause="FROM members AS m "+
			"LEFT JOIN  members_houses_roles AS mhr ON (mhr.member=m.id) "+
			"LEFT JOIN members_parties AS mp ON(mp.member=m.id) "+
			"LEFT JOIN constituencies AS c ON(c.id=mhr.constituency_id) "+
			"LEFT JOIN parties AS p ON(p.id=mp.party) "+
			"LEFT JOIN titles AS t ON(t.id=m.title_id) "+
			"LEFT JOIN memberroles AS mr ON (mr.id=mhr.role) "+
			"LEFT JOIN genders AS g ON(g.id=m.gender_id) "+
			"LEFT JOIN maritalstatus AS ms ON(ms.id=m.maritalstatus_id) ";
			whereClause=" WHERE m.locale='"+locale+"' and mr.priority=0 and mhr.house_id="+house+ " and m.death_date is null and c.is_retired=false";
		}
		String queryCriteriaClause="";
		if(criteria1.equals("constituency")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND (c.id="+criteria2+") ";
			}
		}else  if(criteria1.equals("district")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND (d.id="+criteria2+") ";
			}
		}else if(criteria1.equals("party")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND p.id="+criteria2;
			}
		}else if(criteria1.equals("gender")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND g.id="+criteria2;
			}
		}else if(criteria1.equals("marital_status")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND ms.id="+criteria2;
			}
		}else if(criteria1.equals("birth_date")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND month(m.birth_date)="+criteria2;
			}
		}else if(criteria1.equals("all")){
			queryCriteriaClause=" ";
		}
		String queryOrderByClause="";
		if(criteria1.equals("birth_date")){
			queryOrderByClause=" ORDER BY month(m.birth_date),day(m.birth_date),m.last_name asc";
		}else if(criteria1.equals("constituency")){
			queryOrderByClause=" ORDER BY constituencyname,m.last_name asc";
		}else if(criteria1.equals("party")){
			queryOrderByClause=" ORDER BY partyname,m.last_name asc";
		}else if(criteria1.equals("gender")){
			queryOrderByClause=" ORDER BY gender,m.last_name asc";
		}else if(criteria1.equals("marital_status")){
			queryOrderByClause=" ORDER BY maritalstatus,m.last_name asc";
		}else if(criteria1.equals("district")){
			queryOrderByClause=" ORDER BY district,m.last_name asc";
		}else{
			queryOrderByClause=" ORDER BY m.last_name asc";
		}
		String query="";	
		String partyQuery="";
		String constituencyQuery="";
		
		if(housetype.equals(ApplicationConstants.UPPER_HOUSE)){
			String criteria=councilCriteria[0];
			String fromDate=councilCriteria[1];
			String toDate=councilCriteria[2];
			Date fromDateServerFormat = null;
			Date toDateServerFormat = null;
			try {
				fromDateServerFormat = FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(fromDate);
				toDateServerFormat = FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(toDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String fromDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(fromDateServerFormat);
			String toDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(toDateServerFormat);
			if(criteria.equals("RANGE")){
				partyQuery=" AND ((mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"') "+
				" OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"') "+
				" OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.from_date<='"+toDateDBFormat+"') "+
				" OR (mp.to_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"')) ";
			}else if(criteria.equals("YEAR")){
				partyQuery=" AND ((mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"') "+
				" OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.from_date<='"+toDateDBFormat+"') "+
				" OR (mp.to_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"')) ";

			}else if(criteria.equals("DATE")){
				partyQuery=" AND mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"' ";
			}		
			if(criteria.equals("RANGE")){
				constituencyQuery=" AND ((mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"') "+
				" OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"') "+
				" OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.from_date<='"+toDateDBFormat+"') "+
				" OR (mhr.to_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"')) ";
			}else if(criteria.equals("YEAR")){
				constituencyQuery=" AND ((mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"') "+
				" OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.from_date<='"+toDateDBFormat+"') "+
				" OR (mhr.to_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"')) ";
			}else if(criteria.equals("DATE")){
				constituencyQuery=" AND mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"'";
			}
		}else if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
			// String currentDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(new Date());
			// partyQuery=" AND mp.from_date<='"+currentDateDBFormat+"' AND (mp.to_date>='"+currentDateDBFormat+"' or mp.to_date is null)";
			// constituencyQuery=" AND mhr.from_date<='"+currentDateDBFormat+"' AND (mhr.to_date>='"+currentDateDBFormat+"' or mhr.to_date is null)";
			
			// Instead of pointing to currentDateDBFormat, it should point to to_date in mhr
			partyQuery = " AND mp.from_date<=mhr.to_date AND (mp.to_date>=mhr.to_date or mp.to_date is null)";
		}
		query=selectClause+fromClause+whereClause+queryCriteriaClause+partyQuery+constituencyQuery+queryOrderByClause+") as rs";
		List records=this.em().createNativeQuery(query).getResultList();
		Long currentId=new Long(0);
		int size=0;
		List<MemberInfo> memberInfos=new ArrayList<MemberInfo>();
		for(Object i:records){
			Object[] o=(Object[]) i;
			MemberInfo memberInfo=new MemberInfo();
			memberInfo.setTitle(o[0]!=null?o[0].toString().trim():"-");
			memberInfo.setId(o[1]!=null?Long.parseLong(o[1].toString().trim()):0);
			memberInfo.setFirstName(o[2]!=null?o[2].toString().trim():"-");
			memberInfo.setMiddleName(o[3]!=null?o[3].toString().trim():"-");
			memberInfo.setLastName(o[4]!=null?o[4].toString().trim():"-");			
			memberInfo.setConstituency(o[5]!=null?o[5].toString():"-");
			memberInfo.setParty(o[6]!=null?o[6].toString():"");
			memberInfo.setRecordIndex(o[7]!=null?Integer.parseInt(o[7].toString()):0);
			memberInfo.setPartyFD(o[8]!=null?o[8].toString():"-");
			memberInfo.setPartyFD(o[9]!=null?o[9].toString():"-");
			memberInfo.setGender(o[10]!=null?o[10].toString():"-");
			memberInfo.setMaritalStatus(o[11]!=null?o[11].toString():"-");
			if(o[12]!=null){
				Date dbFormat = null;
				try {
					dbFormat = FormaterUtil.getDateFormatter("yyyy-MM-dd", locale).parse(o[12].toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String serverFormat=FormaterUtil.getDateFormatter("dd MMM yyyy",locale).format(dbFormat);
				if(!locale.equals(ApplicationConstants.STANDARD_LOCALE)
						&& !locale.equals(ApplicationConstants.STANDARD_LOCALE_INDIA)){
					memberInfo.setBirthDate(FormaterUtil.formatMonthsForLocaleLanguage(serverFormat, locale));
				}else{
					memberInfo.setBirthDate(serverFormat);
				}
			}
			if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
				memberInfo.setDistrict(o[13]!=null?o[13].toString():"-");
			}
			if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
				memberInfo.setConstituencyname(o[14]!=null?o[14].toString():"-");			
			}else if(housetype.equals(ApplicationConstants.UPPER_HOUSE)){
				memberInfo.setConstituencyname(o[13]!=null?o[13].toString():"-");		
			}
			if(memberInfo.getId()==currentId){
				if(memberInfos.get(size-1).getRecordIndex()>memberInfo.getRecordIndex()){

				}else{
					memberInfos.get(size-1).setConstituency(memberInfo.getConstituency());
					if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
						memberInfos.get(size-1).setDistrict(memberInfo.getDistrict());
					}
				}
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				try {
					if(format.parse(memberInfos.get(size-1).getPartyFD()).after(format.parse(memberInfo.getPartyFD()))){

					}else{
						memberInfos.get(size-1).setParty(memberInfo.getParty());
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				currentId=memberInfo.getId();
				if(criteria1.equals("constituency")){
					if(memberInfo.getConstituency()!=null&&!memberInfo.getConstituency().isEmpty()){
						if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
							memberInfo.setFirstChar(String.valueOf(memberInfo.getConstituencyname().charAt(0)));
						}else{
							memberInfo.setFirstChar(String.valueOf(memberInfo.getConstituencyname()));
						}
					}
				}else if(criteria1.equals("district")){
					if(memberInfo.getDistrict()!=null&&!memberInfo.getDistrict().isEmpty()){
						memberInfo.setFirstChar(memberInfo.getDistrict());
					}
				}else if(criteria1.equals("party")){
					if(memberInfo.getParty()!=null&&!memberInfo.getParty().isEmpty()){
						memberInfo.setFirstChar(memberInfo.getParty());
					}
				}else if(criteria1.equals("gender")){
					if(memberInfo.getGender()!=null&&!memberInfo.getGender().isEmpty()){
						memberInfo.setFirstChar(memberInfo.getGender());
					}
				}else if(criteria1.equals("marital_status")){
					if(memberInfo.getMaritalStatus()!=null&&!memberInfo.getMaritalStatus().isEmpty()){
						memberInfo.setFirstChar(memberInfo.getMaritalStatus());
					}
				}else if(criteria1.equals("birth_date")){
					if(memberInfo.getBirthDate()!=null&&!memberInfo.getBirthDate().isEmpty()){
						memberInfo.setFirstChar(memberInfo.getBirthDate().split(" ")[1]);
					}
				}else if(criteria1.equals("all")){
					if(memberInfo.getLastName()!=null&&!memberInfo.getLastName().isEmpty()){
						memberInfo.setFirstChar(String.valueOf(memberInfo.getLastName().charAt(0)));
					}
				}
				memberInfos.add(memberInfo);
			}
			size=memberInfos.size();
		}
		return memberInfos;
	}
	
	@SuppressWarnings({"rawtypes" })
	public List<MemberIdentityVO> searchForAccounting(final String housetype, final Long house, final String criteria1,
			final Long criteria2, final String locale,final String[] councilCriteria) {        
		List<MemberIdentityVO> memberIdentityVOs=new ArrayList<MemberIdentityVO>();
		String selectClause="";
		String fromClause="";
		String whereClause="";
		if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
			selectClause="SELECT DISTINCT rs.member_id, rs.title, rs.first_name, rs.middle_name, rs.last_name, rs.full_display_name, rs.constituency_name, rs.constituency_display_name FROM(" +
				"SELECT m.id as member_id, t.name as title, m.first_name AS first_name, m.middle_name AS middle_name, m.last_name AS last_name, " +
				"CONCAT((CASE WHEN m.title_id IS NOT NULL THEN CONCAT(t.name, ' ') ELSE '' END), m.first_name, ' ', (CASE WHEN m.middle_name IS NOT NULL AND m.middle_name<>'-' THEN CONCAT(m.middle_name, ' ') ELSE '' END), m.last_name) AS full_display_name, c.name as constituency_name, c.display_name as constituency_display_name ";
			fromClause="FROM members AS m "+
				"LEFT JOIN  members_houses_roles AS mhr ON (mhr.member=m.id) "+
				"LEFT JOIN members_parties AS mp ON(mp.member=m.id) "+
				"LEFT JOIN constituencies AS c ON(c.id=mhr.constituency_id) "+
				"LEFT JOIN parties AS p ON(p.id=mp.party) "+
				"LEFT JOIN titles AS t ON(t.id=m.title_id) "+
				"LEFT JOIN memberroles AS mr ON (mr.id=mhr.role) "+
				"LEFT JOIN genders AS g ON(g.id=m.gender_id) "+
				"LEFT JOIN maritalstatus AS ms ON(ms.id=m.maritalstatus_id) "+
				"LEFT JOIN constituencies_districts as cd ON(cd.constituency_id=c.id) "+
				"LEFT JOIN districts as d ON(d.id=cd.district_id) ";
			whereClause=" WHERE m.locale='"+locale+"' and mr.priority=0 and mhr.house_id="+house+ " and m.death_date is null and c.is_retired=false";
		}else{
			selectClause="SELECT DISTINCT rs.member_id, rs.title, rs.first_name, rs.middle_name, rs.last_name, rs.full_display_name, rs.constituency_name, rs.constituency_display_name FROM(" +
				"SELECT m.id as member_id, t.name as title, m.first_name AS first_name, m.middle_name AS middle_name, m.last_name AS last_name, " +
				"CONCAT((CASE WHEN m.title_id IS NOT NULL THEN CONCAT(t.name, ' ') ELSE '' END), m.first_name, ' ', (CASE WHEN m.middle_name IS NOT NULL AND m.middle_name<>'-' THEN CONCAT(m.middle_name, ' ') ELSE '' END), m.last_name) AS full_display_name, c.name as constituency_name, c.display_name as constituency_display_name ";
			fromClause="FROM members AS m "+
				"LEFT JOIN  members_houses_roles AS mhr ON (mhr.member=m.id) "+
				"LEFT JOIN members_parties AS mp ON(mp.member=m.id) "+
				"LEFT JOIN constituencies AS c ON(c.id=mhr.constituency_id) "+
				"LEFT JOIN parties AS p ON(p.id=mp.party) "+
				"LEFT JOIN titles AS t ON(t.id=m.title_id) "+
				"LEFT JOIN memberroles AS mr ON (mr.id=mhr.role) "+
				"LEFT JOIN genders AS g ON(g.id=m.gender_id) "+
				"LEFT JOIN maritalstatus AS ms ON(ms.id=m.maritalstatus_id) ";
			whereClause=" WHERE m.locale='"+locale+"' and mr.priority=0 and mhr.house_id="+house+ " and m.death_date is null and c.is_retired=false";
		}
		String queryCriteriaClause="";
		if(criteria1.equals("constituency")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND (c.id="+criteria2+") ";
			}
		}else  if(criteria1.equals("district")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND (d.id="+criteria2+") ";
			}
		}else if(criteria1.equals("party")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND p.id="+criteria2;
			}
		}else if(criteria1.equals("gender")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND g.id="+criteria2;
			}
		}else if(criteria1.equals("marital_status")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND ms.id="+criteria2;
			}
		}else if(criteria1.equals("birth_date")){
			if(criteria2==0){
				queryCriteriaClause=" ";
			}else{
				queryCriteriaClause=" AND month(m.birth_date)="+criteria2;
			}
		}else if(criteria1.equals("all")){
			queryCriteriaClause=" ";
		}
		String queryOrderByClause="";
		if(criteria1.equals("birth_date")){
			queryOrderByClause=" ORDER BY month(m.birth_date),day(m.birth_date),m.last_name asc";
		}else if(criteria1.equals("constituency")){
			queryOrderByClause=" ORDER BY c.name,m.last_name asc";
		}else if(criteria1.equals("party")){
			queryOrderByClause=" ORDER BY p.name,m.last_name asc";
		}else if(criteria1.equals("gender")){
			queryOrderByClause=" ORDER BY g.name,m.last_name asc";
		}else if(criteria1.equals("marital_status")){
			queryOrderByClause=" ORDER BY ms.name,m.last_name asc";
		}else if(criteria1.equals("district")){
			queryOrderByClause=" ORDER BY d.name,m.last_name asc";
		}else{
			queryOrderByClause=" ORDER BY m.last_name asc";
		}
		String query="";	
		String partyQuery="";
		String constituencyQuery="";
		
		if(housetype.equals(ApplicationConstants.UPPER_HOUSE)){
			String criteria=councilCriteria[0];
			String fromDate=councilCriteria[1];
			String toDate=councilCriteria[2];
			Date fromDateServerFormat = null;
			Date toDateServerFormat = null;
			try {
				fromDateServerFormat = FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(fromDate);
				toDateServerFormat = FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(toDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String fromDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(fromDateServerFormat);
			String toDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(toDateServerFormat);
			if(criteria.equals("RANGE")){
				partyQuery=" AND ((mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"') "+
				" OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"') "+
				" OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.from_date<='"+toDateDBFormat+"') "+
				" OR (mp.to_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"')) ";
			}else if(criteria.equals("YEAR")){
				partyQuery=" AND ((mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"') "+
				" OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.from_date<='"+toDateDBFormat+"') "+
				" OR (mp.to_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"')) ";

			}else if(criteria.equals("DATE")){
				partyQuery=" AND mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"' ";
			}		
			if(criteria.equals("RANGE")){
				constituencyQuery=" AND ((mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"') "+
				" OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"') "+
				" OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.from_date<='"+toDateDBFormat+"') "+
				" OR (mhr.to_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"')) ";
			}else if(criteria.equals("YEAR")){
				constituencyQuery=" AND ((mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"') "+
				" OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.from_date<='"+toDateDBFormat+"') "+
				" OR (mhr.to_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"')) ";
			}else if(criteria.equals("DATE")){
				constituencyQuery=" AND mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"'";
			}			
		}else if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
			// String currentDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(new Date());
			// partyQuery=" AND mp.from_date<='"+currentDateDBFormat+"' AND (mp.to_date>='"+currentDateDBFormat+"' or mp.to_date is null)";
			// constituencyQuery=" AND mhr.from_date<='"+currentDateDBFormat+"' AND (mhr.to_date>='"+currentDateDBFormat+"' or mhr.to_date is null)";
			
			// Instead of pointing to currentDateDBFormat, it should point to to_date in mhr
			//partyQuery = " AND mp.from_date<=mhr.to_date AND (mp.to_date>=mhr.to_date or mp.to_date is null)";
		}
		query=selectClause+fromClause+whereClause+queryCriteriaClause+partyQuery+constituencyQuery+queryOrderByClause+") as rs";		
		List records=this.em().createNativeQuery(query).getResultList();
		if(records!=null) {
			System.out.println("Count of Members: " + records.size());		
			for(Object i:records){
				Object[] o=(Object[]) i;
				MemberIdentityVO memberIdentityVO=new MemberIdentityVO();
				memberIdentityVO.setTitle(o[1]!=null?o[1].toString().trim():"-");
				memberIdentityVO.setFirstName(o[2]!=null?o[2].toString().trim():"-");
				memberIdentityVO.setMiddleName(o[3]!=null?o[3].toString().trim():"-");
				memberIdentityVO.setLastName(o[4]!=null?o[4].toString().trim():"-");			
				memberIdentityVO.setFullDisplayName(o[5]!=null?o[5].toString():"-");			
				memberIdentityVO.setConstituencyName(o[6]!=null?o[6].toString():"-");
				memberIdentityVO.setConstituencyDisplayName(o[7]!=null?o[7].toString():"-");
				
				if(o[0]!=null) {
					Member member = Member.findById(Member.class, Long.parseLong(o[0].toString()));
					if(member!=null) {
						try {
							User memberUser = User.findbyNameBirthDate(member.getFirstName(),
									member.getMiddleName(),member.getLastName(),
									member.getBirthDate());
							if(memberUser!=null) {
								memberIdentityVO.setUsername(memberUser.getCredential().getUsername());
							}
						} catch (ELSException e) {
							memberIdentityVO.setUsername("");
						}
					}
				}
				memberIdentityVOs.add(memberIdentityVO);
			}
		}		
		return memberIdentityVOs;
	}

	/**
	 * Find biography.
	 *
	 * @param id the id
	 * @param locale the locale
	 * @param data
	 * @return the member biography vo
	 */
	public MemberBiographyVO findBiography(final long id, final String locale) {
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT_DDMMMYYYY", "");
		SimpleDateFormat dateFormat=FormaterUtil.getDateFormatter(parameter.getValue(), locale);
		NumberFormat formatWithGrouping=FormaterUtil.getNumberFormatterGrouping(locale);
		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
		Member m=Member.findById(Member.class, id);
		MemberBiographyVO memberBiographyVO=new MemberBiographyVO();
		memberBiographyVO.setId(m.getId());
		//the header in the biography page.
		//for the time being setting party flag to "-"
		memberBiographyVO.setPartyFlag("-");
		if(m.getTitle()==null){
			memberBiographyVO.setTitle("-");
		}else{
			memberBiographyVO.setTitle(m.getTitle().getName());
		}
		if(m.getAlias().isEmpty()){
			memberBiographyVO.setAlias("-");
		}else{
			memberBiographyVO.setAlias(m.getAlias());
		}
		memberBiographyVO.setEnableAliasing(m.getAliasEnabled());
		memberBiographyVO.setFirstName(m.getFirstName());
		memberBiographyVO.setMiddleName(m.getMiddleName());
		memberBiographyVO.setLastName(m.getLastName());
		if(m.getPhoto().isEmpty()){
			memberBiographyVO.setPhoto("-");
		}else{
			memberBiographyVO.setPhoto(m.getPhoto());
		}
		Constituency constituency=findConstituency(m.getId());
		if(constituency!=null){
			memberBiographyVO.setConstituency(constituency.getDisplayName());
		}else{
			memberBiographyVO.setConstituency("-");
		}
		Party party=findParty(m.getId());
		if(party!=null){
		memberBiographyVO.setPartyName(party.getName());
		}else{
			memberBiographyVO.setPartyName("-");
		}
		if(m.getGender()!=null){
		memberBiographyVO.setGender(m.getGender().getName());
		}else{
			memberBiographyVO.setGender("-");
		}
		if(m.getMaritalStatus()!=null){
			memberBiographyVO.setMaritalStatus(m.getMaritalStatus().getName());		
		}else{
			memberBiographyVO.setMaritalStatus("-");
		}
		memberBiographyVO.setFatherName("-");
		memberBiographyVO.setMotherName("-");
		memberBiographyVO.setNoOfDaughter("-");
		memberBiographyVO.setNoOfSons("-");
		memberBiographyVO.setNoOfChildren("-");
		memberBiographyVO.setSpouseName("-");
		memberBiographyVO.setSonCount(0);
		memberBiographyVO.setDaughterCount(0);
		if(m.getFamilyMembers()!=null&&!m.getFamilyMembers().isEmpty()){
		//right now we are doing just for marathi.and so we are comparing directly with the ids.
			int noOfSons=0;
			int noOfDaughters=0;
			int noOfChildren=0;            

			for(FamilyMember i:m.getFamilyMembers()){
				if(i.getRelation().getType().equals(ApplicationConstants.WIFE)||i.getRelation().getType().equals(ApplicationConstants.HUSBAND)){
					memberBiographyVO.setSpouseName(i.getName());
					memberBiographyVO.setSpouseRelation(i.getRelation().getName());
				}else if(i.getRelation().getType().equals(ApplicationConstants.SON)){
					noOfSons++;
				}else if(i.getRelation().getType().equals(ApplicationConstants.DAUGHTER)){
					noOfDaughters++;
				}
			}
			if(noOfSons==0){
				memberBiographyVO.setNoOfSons("-");
			}else{
				memberBiographyVO.setNoOfSons(formatWithoutGrouping.format(noOfSons));
				memberBiographyVO.setSonCount(noOfSons);
			}
			if(noOfDaughters==0){
				memberBiographyVO.setNoOfDaughter("-");
			}else{
				memberBiographyVO.setNoOfDaughter(formatWithoutGrouping.format(noOfDaughters));
				memberBiographyVO.setDaughterCount(noOfDaughters);
			}
			noOfChildren=noOfDaughters+noOfSons;
			if(noOfChildren==0){
				memberBiographyVO.setNoOfChildren("-");
			}else{
				memberBiographyVO.setNoOfChildren(formatWithoutGrouping.format(noOfChildren));
			}
		}
		//birth date and birth place
		if(m.getBirthDate()==null){
			memberBiographyVO.setBirthDate("-");
		}else{
			memberBiographyVO.setBirthDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getBirthDate()), locale));
		}
		memberBiographyVO.setPlaceOfBirth(m.getBirthPlace().trim());

		//death date,condolence date and obituary
		if(m.getDeathDate()==null){
			memberBiographyVO.setDeathDate("-");
		}else{
			memberBiographyVO.setDeathDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getDeathDate()),locale));
		}
		if(m.getCondolenceDate()==null){
			memberBiographyVO.setCondolenceDate("-");
		}else{
			memberBiographyVO.setCondolenceDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getCondolenceDate()),locale));
		}
		if(m.getObituary()==null){
			memberBiographyVO.setObituary("-");
		}else{
			memberBiographyVO.setObituary(m.getObituary().trim());
		}
		if(m.getMarriageDate()==null){
			memberBiographyVO.setMarriageDate("-");
		}else{
			memberBiographyVO.setMarriageDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getMarriageDate()),locale));
		}
		//qualifications.this will be separated by line
		List<Qualification> qualifications=m.getQualifications();
		if(qualifications!=null&&!qualifications.isEmpty()){
			StringBuffer buffer=new StringBuffer();
			int count=0;
			for(Qualification i:qualifications){
				if(count>0){
					buffer.append("<br>"+i.getDetails());
				}else{
					if(i.getDetails().isEmpty()){
						if(i.getDegree()!=null){
							buffer.append(i.getDegree().getName());
						}else{
							buffer.append("-");
						}
					}else{
						buffer.append(i.getDetails());
					}
				}
			}
			memberBiographyVO.setEducationalQualification(buffer.toString());
		}else if(qualifications==null){
			memberBiographyVO.setEducationalQualification("-");
		}else if(qualifications.isEmpty()){
			memberBiographyVO.setEducationalQualification("-");
		}
		//languages.This will be comma separated values
		CustomParameter andLocalizedName=CustomParameter.findByName(CustomParameter.class,"AND", locale);
		List<Language> languages=m.getLanguages();
		if(languages!=null&&!languages.isEmpty()){
			Map<Integer,Language> languageMap=new HashMap<Integer, Language>();
			Set<Integer> keys=new TreeSet<Integer>();
			for(Language i:m.getLanguages()){
				languageMap.put(i.getPriority(),i);
				keys.add(i.getPriority());
			}
			List<Language> sortedLanguage=new ArrayList<Language>();
			for(Integer i:keys){
				sortedLanguage.add(languageMap.get(i));
			}
			StringBuffer buffer=new StringBuffer();
			int size=sortedLanguage.size();
			int count=0;
			for(Language i:sortedLanguage){
				count++;
				if(count==size-1){
					buffer.append(i.getName()+" "+andLocalizedName.getValue()+" ");
				}else if(count==size){
					buffer.append(i.getName());
				}else{
					buffer.append(i.getName()+", ");
				}
			}
			//buffer.deleteCharAt(buffer.length()-1);
			memberBiographyVO.setLanguagesKnown(buffer.toString());
		}else if(languages==null){
			memberBiographyVO.setLanguagesKnown("-");
		}else if(languages.isEmpty()){
			memberBiographyVO.setLanguagesKnown("-");
		}
		//profession.this will also be comma separated values
		List<Profession> professions=m.getProfessions();
		if(professions!=null&&!professions.isEmpty()){
			StringBuffer buffer=new StringBuffer();
			int size=professions.size();
			int count=0;
			for(Profession i:professions){
				count++;
				if(count==size-1){
					buffer.append(i.getName()+" "+andLocalizedName.getValue()+" ");
				}else if(count==size){
					buffer.append(i.getName());
				}else{
					buffer.append(i.getName()+", ");
				}
			}
			// buffer.deleteCharAt(buffer.length()-1);
			memberBiographyVO.setProfession(buffer.toString());
		}if(professions==null){
			memberBiographyVO.setProfession("-");
		}else if(professions.isEmpty()){
			memberBiographyVO.setProfession("-");
		}
		//contact info
		Contact contact=m.getContact();
		if(contact==null){
			memberBiographyVO.setEmail("-");
			memberBiographyVO.setWebsite("-");
			memberBiographyVO.setFax1("-");
			memberBiographyVO.setFax2("-");
			memberBiographyVO.setFax3("-");
			memberBiographyVO.setFax4("-");
			memberBiographyVO.setFax5("-");
			memberBiographyVO.setFax6("-");
			memberBiographyVO.setFax7("-");
			memberBiographyVO.setFax8("-");
			memberBiographyVO.setFax9("-");
			memberBiographyVO.setFax10("-");
			memberBiographyVO.setFax11("-");
			memberBiographyVO.setFax12("-");
			memberBiographyVO.setMobile("-");
			memberBiographyVO.setTelephone1("-");
			memberBiographyVO.setTelephone2("-");
			memberBiographyVO.setTelephone3("-");
			memberBiographyVO.setTelephone4("-");
			memberBiographyVO.setTelephone5("-");
			memberBiographyVO.setTelephone6("-");
			memberBiographyVO.setTelephone7("-");
			memberBiographyVO.setTelephone8("-");
			memberBiographyVO.setTelephone9("-");
			memberBiographyVO.setTelephone10("-");
			memberBiographyVO.setTelephone11("-");
			memberBiographyVO.setTelephone12("-");

		}else{
			memberBiographyVO.setEmail("-");
			memberBiographyVO.setWebsite("-");
			memberBiographyVO.setFax1("-");
			memberBiographyVO.setFax2("-");
			memberBiographyVO.setFax3("-");
			memberBiographyVO.setFax4("-");
			memberBiographyVO.setFax5("-");
			memberBiographyVO.setFax6("-");
			memberBiographyVO.setFax7("-");
			memberBiographyVO.setFax8("-");
			memberBiographyVO.setFax9("-");
			memberBiographyVO.setFax10("-");
			memberBiographyVO.setFax11("-");
			memberBiographyVO.setFax12("-");
			memberBiographyVO.setMobile("-");
			memberBiographyVO.setTelephone1("-");
			memberBiographyVO.setTelephone2("-");
			memberBiographyVO.setTelephone3("-");
			memberBiographyVO.setTelephone4("-");
			memberBiographyVO.setTelephone5("-");
			memberBiographyVO.setTelephone6("-");
			memberBiographyVO.setTelephone7("-");
			memberBiographyVO.setTelephone8("-");
			memberBiographyVO.setTelephone9("-");
			memberBiographyVO.setTelephone10("-");
			memberBiographyVO.setTelephone11("-");
			memberBiographyVO.setTelephone12("-");
			if(contact.getEmail1()!=null&&!contact.getEmail1().isEmpty()
					&&contact.getEmail2()!=null&&!contact.getEmail2().isEmpty()){
				memberBiographyVO.setEmail(contact.getEmail1()+"<br>"+contact.getEmail2());
			}else if(contact.getEmail1()!=null&&!contact.getEmail1().isEmpty()){
				memberBiographyVO.setEmail(contact.getEmail1());
			}else if(contact.getEmail2()!=null&&!contact.getEmail2().isEmpty()){
				memberBiographyVO.setEmail(contact.getEmail2());
			}
			if(contact.getWebsite1()!=null&&!contact.getWebsite1().isEmpty()
					&&contact.getWebsite2()!=null&&!contact.getWebsite2().isEmpty()){
				memberBiographyVO.setWebsite(contact.getWebsite1()+"<br>"+contact.getWebsite2());
			}else if(contact.getWebsite1()!=null&&!contact.getWebsite1().isEmpty()){
				memberBiographyVO.setWebsite(contact.getWebsite1());
			}else if(contact.getWebsite2()!=null&&!contact.getWebsite2().isEmpty()){
				memberBiographyVO.setWebsite(contact.getWebsite2());
			}
			if(contact.getMobile1()!=null&&!contact.getMobile1().isEmpty()
					&&contact.getMobile2()!=null&&!contact.getMobile2().isEmpty()){
				memberBiographyVO.setMobile(contact.getMobile1()+"<br>"+contact.getMobile2());
			}else if(contact.getMobile1()!=null&&!contact.getMobile1().isEmpty()){
				memberBiographyVO.setMobile(contact.getMobile1());
			}else if(contact.getMobile2()!=null&&!contact.getMobile2().isEmpty()){
				memberBiographyVO.setMobile(contact.getMobile2());
			}
			if(contact.getFax1()!=null){
				if(!contact.getFax1().trim().isEmpty()){
					memberBiographyVO.setFax1(contact.getFax1().trim());
				}
			}
			if(contact.getFax2()!=null){
				if(!contact.getFax2().trim().isEmpty()){
					memberBiographyVO.setFax2(contact.getFax2().trim());
				}
			}
			if(contact.getFax3()!=null){
				if(!contact.getFax3().trim().isEmpty()){
					memberBiographyVO.setFax3(contact.getFax3().trim());
				}
			}
			if(contact.getFax4()!=null){
				if(!contact.getFax4().trim().isEmpty()){
					memberBiographyVO.setFax4(contact.getFax4().trim());
				}
			}
			if(contact.getFax5()!=null){
				if(!contact.getFax5().trim().isEmpty()){
					memberBiographyVO.setFax5(contact.getFax5().trim());
				}
			}

			if(contact.getFax6()!=null){
				if(!contact.getFax6().trim().isEmpty()){
					memberBiographyVO.setFax6(contact.getFax6().trim());
				}
			}
			if(contact.getFax7()!=null){
				if(!contact.getFax7().trim().isEmpty()){
					memberBiographyVO.setFax7(contact.getFax7().trim());
				}
			}
			if(contact.getFax8()!=null){
				if(!contact.getFax8().trim().isEmpty()){
					memberBiographyVO.setFax8(contact.getFax8().trim());
				}
			}
			if(contact.getFax9()!=null){
				if(!contact.getFax9().trim().isEmpty()){
					memberBiographyVO.setFax9(contact.getFax9().trim());
				}
			}
			if(contact.getFax10()!=null){
				if(!contact.getFax10().trim().isEmpty()){
					memberBiographyVO.setFax10(contact.getFax10().trim());
				}
			}

			if(contact.getFax11()!=null){
				if(!contact.getFax11().trim().isEmpty()){
					memberBiographyVO.setFax11(contact.getFax11().trim());
				}
			}
			if(contact.getFax12()!=null){
				if(!contact.getFax12().trim().isEmpty()){
					memberBiographyVO.setFax12(contact.getFax12().trim());
				}
			}			
			if(contact.getTelephone1()!=null){
				if(!contact.getTelephone1().isEmpty()){
					memberBiographyVO.setTelephone1(contact.getTelephone1());
				}
			}
			if(contact.getTelephone2()!=null){
				if(!contact.getTelephone2().isEmpty()){
					memberBiographyVO.setTelephone2(contact.getTelephone2());
				}
			}
			if(contact.getTelephone3()!=null){
				if(!contact.getTelephone3().isEmpty()){
					memberBiographyVO.setTelephone3(contact.getTelephone3());
				}
			}
			if(contact.getTelephone4()!=null){
				if(!contact.getTelephone4().isEmpty()){
					memberBiographyVO.setTelephone4(contact.getTelephone4());
				}
			}
			if(contact.getTelephone5()!=null){
				if(!contact.getTelephone5().isEmpty()){
					memberBiographyVO.setTelephone5(contact.getTelephone5());
				}
			}

			if(contact.getTelephone6()!=null){
				if(!contact.getTelephone6().isEmpty()){
					memberBiographyVO.setTelephone6(contact.getTelephone6());
				}
			}
			if(contact.getTelephone7()!=null){
				if(!contact.getTelephone7().isEmpty()){
					memberBiographyVO.setTelephone7(contact.getTelephone7());
				}
			}
			if(contact.getTelephone8()!=null){
				if(!contact.getTelephone8().isEmpty()){
					memberBiographyVO.setTelephone8(contact.getTelephone8());
				}
			}
			if(contact.getTelephone9()!=null){
				if(!contact.getTelephone9().isEmpty()){
					memberBiographyVO.setTelephone9(contact.getTelephone9());
				}
			}
			if(contact.getTelephone10()!=null){
				if(!contact.getTelephone10().isEmpty()){
					memberBiographyVO.setTelephone10(contact.getTelephone10());
				}
			}
			if(contact.getTelephone11()!=null){
				if(!contact.getTelephone11().isEmpty()){
					memberBiographyVO.setTelephone11(contact.getTelephone11());
				}
			}
			if(contact.getTelephone12()!=null){
				if(!contact.getTelephone12().isEmpty()){
					memberBiographyVO.setTelephone12(contact.getTelephone12());
				}
			}

		}
		//initialize addresses
		CustomParameter tehsilLocalized=CustomParameter.findByName(CustomParameter.class,"TEHSIL", locale);
		CustomParameter districtLocalized=CustomParameter.findByName(CustomParameter.class,"DISTRICT", locale);
		CustomParameter stateLocalized=CustomParameter.findByName(CustomParameter.class,"STATE", locale);

		memberBiographyVO.setPermanentAddress("-");
		memberBiographyVO.setPermanentAddress1("-");
		memberBiographyVO.setPermanentAddress2("-");
		memberBiographyVO.setPresentAddress("-");
		memberBiographyVO.setPresentAddress1("-");
		memberBiographyVO.setPresentAddress2("-");
		memberBiographyVO.setCorrespondenceAddress("-");
		memberBiographyVO.setOfficeAddress("-");
		memberBiographyVO.setOfficeAddress1("-");
		memberBiographyVO.setOfficeAddress1("-");
		memberBiographyVO.setOfficeAddress2("-");
		memberBiographyVO.setTempAddress1("-");
		memberBiographyVO.setTempAddress2("-");
		//present address
		Address presentAddress=m.getPresentAddress();
		if(presentAddress!=null){
			if(presentAddress.getDetails()!=null){
				if(!presentAddress.getDetails().trim().isEmpty()){
					if(presentAddress.getTehsil()!=null){
						memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+presentAddress.getTehsil().getName()+","+districtLocalized.getValue()+"-"+presentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
					}else{
						memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+districtLocalized.getValue()+"-"+presentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
					}
				}
			}
		}

		//present address
		Address presentAddress1=m.getPresentAddress1();
		if(presentAddress1!=null){
			if(presentAddress1.getDetails()!=null){
				if(!presentAddress1.getDetails().trim().isEmpty()){
					if(presentAddress1.getTehsil()!=null){
						memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+presentAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+presentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
					}else{
						memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+presentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
					}
				}
			}
		}
		//present address
		Address presentAddress2=m.getPresentAddress2();
		if(presentAddress2!=null){
			if(presentAddress2.getDetails()!=null){
				if(!presentAddress2.getDetails().trim().isEmpty()){
					if(presentAddress2.getTehsil()!=null){
						memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+presentAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+presentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
					}else{
						memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+presentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
					}
				}
			}
		}
		//permanent address
		Address permanentAddress=m.getPermanentAddress();
		if(permanentAddress!=null){
			if(permanentAddress.getDetails()!=null){
				if(!permanentAddress.getDetails().trim().isEmpty()) {
					if(permanentAddress.getTehsil()!=null){
						memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+permanentAddress.getTehsil().getName()+","+districtLocalized.getValue()+"-"+permanentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
					}else{
						memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+districtLocalized.getValue()+"-"+permanentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
					}
				}
			}
		}
		//permanent address
		Address permanentAddress1=m.getPermanentAddress1();
		if(permanentAddress1!=null){
			if(permanentAddress1.getDetails()!=null){
				if(!permanentAddress1.getDetails().trim().isEmpty()) {
					if(permanentAddress1.getTehsil()!=null){
						memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+permanentAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+permanentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
					}else{
						memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+permanentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
					}
				}
			}
		}
		//permanent address
		Address permanentAddress2=m.getPermanentAddress2();
		if(permanentAddress2!=null){
			if(permanentAddress2.getDetails()!=null){
				if(!permanentAddress2.getDetails().trim().isEmpty()) {
					if(permanentAddress2.getTehsil()!=null){
						memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+permanentAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+permanentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
					}else{
						memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+permanentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
					}
				}
			}
		}
		//office address
		Address officeAddress=m.getOfficeAddress();
		if(officeAddress!=null){
			if(officeAddress.getDetails()!=null){
				if(!officeAddress.getDetails().trim().isEmpty()){
					if(officeAddress.getTehsil()!=null){
						memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+officeAddress.getTehsil().getName()+","+districtLocalized.getValue()+"-"+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
					}else{
						memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+districtLocalized.getValue()+"-"+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
					}
				}
			}
		}
		//office address
		Address officeAddress1=m.getOfficeAddress1();
		if(officeAddress1!=null){
			if(officeAddress1.getDetails()!=null){
				if(!officeAddress1.getDetails().trim().isEmpty()){
					if(officeAddress1.getTehsil()!=null){
						memberBiographyVO.setOfficeAddress1(officeAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+officeAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+officeAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress1.getState().getName()+" "+officeAddress1.getPincode());
					}else{
						memberBiographyVO.setOfficeAddress1(officeAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+officeAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress1.getState().getName()+" "+officeAddress1.getPincode());
					}
				}
			}
		}
		//office address
		Address officeAddress2=m.getOfficeAddress2();
		if(officeAddress2==null){
			memberBiographyVO.setOfficeAddress2("-");
		}else{
			if(!officeAddress2.getDetails().trim().isEmpty()){
				if(officeAddress2.getTehsil()!=null){
					memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+officeAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+officeAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
				}else{
					memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+officeAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
				}
			}else{
				memberBiographyVO.setOfficeAddress2("-");
			}
		}
		//temp1 address
		Address tempAddress1=m.getTempAddress1();
		if(tempAddress1!=null){
			if(tempAddress1.getDetails()!=null){
				if(!tempAddress1.getDetails().trim().isEmpty()){
					if(tempAddress1.getTehsil()!=null){
						memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+tempAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+tempAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
					}else{
						memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+tempAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
					}
				}
			}
		}
		//temp2 address
		Address tempAddress2=m.getTempAddress2();
		if(tempAddress2!=null){
			if(tempAddress2.getDetails()!=null){
				if(tempAddress2.getDetails()!=null){
					if(!tempAddress2.getDetails().trim().isEmpty()){
						if(tempAddress2.getTehsil()!=null){
							memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+tempAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+tempAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
						}else{
							memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+tempAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
						}
					}else{
						memberBiographyVO.setTempAddress2("-");
					}
				}
			}
		}
		//correspondence address
		Address correspondenceAddress=m.getCorrespondenceAddress();
		if(correspondenceAddress!=null){
			if(correspondenceAddress.getDetails()!=null){
				if(correspondenceAddress.getDetails()!=null){
					if(!correspondenceAddress.getDetails().trim().isEmpty()){
						memberBiographyVO.setCorrespondenceAddress(tempAddress2.getDetails()+"<br>"+tempAddress2.getPincode());
					}else{
						memberBiographyVO.setCorrespondenceAddress("-");
					}
				}
			}
		}
		//other info.
		if(m.getCountriesVisited()!=null&&!m.getCountriesVisited().equals("<p></p>")){
			if(m.getCountriesVisited().trim().isEmpty()){
				memberBiographyVO.setCountriesVisited("-");
			}else{
				memberBiographyVO.setCountriesVisited(m.getCountriesVisited().trim());
			}
		}else{
			memberBiographyVO.setCountriesVisited("-");
		}

		if(m.getEducationalCulturalActivities()!=null&&!m.getEducationalCulturalActivities().equals("<p></p>")){
			if(m.getEducationalCulturalActivities().trim().isEmpty()){
				memberBiographyVO.setEducationalCulAct("-");
			}else{
				memberBiographyVO.setEducationalCulAct(m.getEducationalCulturalActivities().trim());
			}
		}else{
			memberBiographyVO.setEducationalCulAct("-");
		}

		if(m.getFavoritePastimeRecreation()!=null&&!m.getFavoritePastimeRecreation().equals("<p></p>")){
			if(m.getFavoritePastimeRecreation().trim().isEmpty()){
				memberBiographyVO.setPastimeRecreation("-");
			}else{
				memberBiographyVO.setPastimeRecreation(m.getFavoritePastimeRecreation().trim());
			}
		}else{
			memberBiographyVO.setPastimeRecreation("-");
		}

		if(m.getHobbySpecialInterests()!=null&&!m.getHobbySpecialInterests().equals("<p></p>")){
			if(m.getHobbySpecialInterests().trim().isEmpty()){
				memberBiographyVO.setSpecialInterests("-");
			}else{
				memberBiographyVO.setSpecialInterests(m.getHobbySpecialInterests().trim());
			}
		}else{
			memberBiographyVO.setSpecialInterests("-");
		}

		if(m.getLiteraryArtisticScientificAccomplishments()!=null&&!m.getLiteraryArtisticScientificAccomplishments().equals("<p></p>")){
			if(m.getLiteraryArtisticScientificAccomplishments().trim().isEmpty()){
				memberBiographyVO.setLiteraryArtisticScAccomplishment("-");
			}else{
				memberBiographyVO.setLiteraryArtisticScAccomplishment(m.getLiteraryArtisticScientificAccomplishments().trim());
			}
		}else{
			memberBiographyVO.setLiteraryArtisticScAccomplishment("-");
		}

		if(m.getPublications()!=null&&!m.getPublications().equals("<p></p>")){
			if(m.getPublications().trim().isEmpty()){
				memberBiographyVO.setPublications("-");
			}else{
				memberBiographyVO.setPublications(m.getPublications().trim());
			}
		}else{
			memberBiographyVO.setPublications("-");
		}
		if(m.getOtherInformation()!=null&&!m.getOtherInformation().equals("<p></p>")){
			if(m.getOtherInformation().trim().isEmpty()){
				memberBiographyVO.setOtherInfo("-");
			}else{
				memberBiographyVO.setOtherInfo(m.getOtherInformation().trim());
			}
		}else{
			memberBiographyVO.setOtherInfo("-");
		}

		if(m.getSocialCulturalActivities()!=null&&!m.getSocialCulturalActivities().equals("<p></p>")){
			if(m.getSocialCulturalActivities().trim().isEmpty()){
				memberBiographyVO.setSocioCulturalActivities("-");
			}else{
				memberBiographyVO.setSocioCulturalActivities(m.getSocialCulturalActivities().trim());
			}
		}else{
			memberBiographyVO.setSocioCulturalActivities("-");
		}

		if(m.getSportsClubs()!=null&&!m.getSportsClubs().equals("<p></p>")){
			if(m.getSportsClubs().trim().isEmpty()){
				memberBiographyVO.setSportsClubs("-");
			}else{
				memberBiographyVO.setSportsClubs(m.getSportsClubs().trim());
			}
		}else{
			memberBiographyVO.setSportsClubs("-");
		}
		//elelction results
		//this will be according to the from and to date of elections.but right now we are taking the
		//first entry
		List<ElectionResult> electionResults=m.getElectionResults();
		if(electionResults.isEmpty()){
			memberBiographyVO.setValidVotes("-");
			memberBiographyVO.setVotesReceived("-");
			memberBiographyVO.setNoOfVoters("-");
			memberBiographyVO.setRivalMembers(new ArrayList<RivalMemberVO>());
		}else{
			if(electionResults.get(0).getTotalValidVotes()!=null){
				//memberBiographyVO.setValidVotes(formatWithGrouping.format(electionResults.get(0).getTotalValidVotes()));
				memberBiographyVO.setValidVotes(FormaterUtil.formatToINS(formatWithGrouping.format(electionResults.get(0).getTotalValidVotes())));;
			}else{
				memberBiographyVO.setValidVotes("-");
			}
			if(electionResults.get(0).getVotesReceived()!=null){
				memberBiographyVO.setVotesReceived(FormaterUtil.formatToINS(formatWithGrouping.format(electionResults.get(0).getVotesReceived())));
			}else{
				memberBiographyVO.setVotesReceived("-");
			}
			if(electionResults.get(0).getNoOfVoters()!=null){
				memberBiographyVO.setNoOfVoters(FormaterUtil.formatToINS(formatWithGrouping.format(electionResults.get(0).getNoOfVoters())));
			}else{
				memberBiographyVO.setNoOfVoters("-");
			}
			if(electionResults.get(0).getElectionResultDate()!=null){
				memberBiographyVO.setElectionResultDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(electionResults.get(0).getElectionResultDate()),locale));
			}else{
				memberBiographyVO.setElectionResultDate("-");
			}
			if(electionResults.get(0).getVotingDate()!=null){
				memberBiographyVO.setVotingDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(electionResults.get(0).getVotingDate()),locale));
			}else{
				memberBiographyVO.setVotingDate("-");
			}
			List<RivalMember> rivals=electionResults.get(0).getRivalMembers();
			List<RivalMemberVO> rivalMemberVOs=new ArrayList<RivalMemberVO>();
			if(!rivals.isEmpty()){
				for(RivalMember i:rivals){
					RivalMemberVO rivalMemberVO=new RivalMemberVO();
					rivalMemberVO.setName(i.getName());
					rivalMemberVO.setParty(i.getParty().getName());
					if(i.getVotesReceived()!=null){
						rivalMemberVO.setVotesReceived(FormaterUtil.formatToINS(formatWithGrouping.format(i.getVotesReceived())));
					}else{
						rivalMemberVO.setVotesReceived("-");
					}
					rivalMemberVOs.add(rivalMemberVO);
				}
				memberBiographyVO.setRivalMembers(rivalMemberVOs);
			}else{
				memberBiographyVO.setRivalMembers(rivalMemberVOs);
			}
		}
		//position held is left out right now.
		return memberBiographyVO;
	}

	public MemberCompleteDetailVO getCompleteDetail(final Long id, final String locale) {
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT_DDMMMYYYY", "");
		SimpleDateFormat dateFormat=FormaterUtil.getDateFormatter(parameter.getValue(), locale);
		NumberFormat formatWithGrouping=FormaterUtil.getNumberFormatterGrouping(locale);
		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
		Member m=Member.findById(Member.class, id);
		MemberCompleteDetailVO memberBiographyVO=new MemberCompleteDetailVO();
		//**************************Personal Details**********************************
		//photo
		memberBiographyVO.setPhoto("-");
		if(m.getPhoto()!=null){
			if(!m.getPhoto().isEmpty()){
				memberBiographyVO.setPhoto(m.getPhoto());
			}
		}
		//specimen signature
		memberBiographyVO.setSpecimenSignature("-");
		if(m.getSpecimenSignature()!=null){
			if(!m.getSpecimenSignature().isEmpty()){
				memberBiographyVO.setSpecimenSignature(m.getSpecimenSignature());
			}
		}
		//title.
		if(m.getTitle()==null){
			memberBiographyVO.setTitle("-");
		}else{
			memberBiographyVO.setTitle(m.getTitle().getName());
		}
		//alias
		memberBiographyVO.setAlias("-");
		if(m.getAlias()!=null){
			if(!m.getAlias().isEmpty()){
				memberBiographyVO.setAlias(m.getAlias());
			}
		}
		//firstname
		memberBiographyVO.setFirstName(m.getFirstName());
		//middlename
		memberBiographyVO.setMiddleName(m.getMiddleName());
		//lastname
		memberBiographyVO.setLastName(m.getLastName());
		//birth date and birth place
		if(m.getBirthDate()==null){
			memberBiographyVO.setBirthDate("-");
		}else{
			memberBiographyVO.setBirthDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getBirthDate()), locale));
		}
		memberBiographyVO.setBirthPlace("-");
		if(m.getBirthPlace()!=null){
			if(!m.getBirthPlace().isEmpty()){
				memberBiographyVO.setBirthPlace(m.getBirthPlace().trim());
			}
		}
		//nationality
		if(m.getNationality()==null){
			memberBiographyVO.setNationality("-");
		}else{
			memberBiographyVO.setNationality(m.getNationality().getName());
		}
		//gender
		if(m.getGender()==null){
			memberBiographyVO.setGender("-");
		}else{
			memberBiographyVO.setGender(m.getGender().getName());
		}
		//qualifications.this will be separated by line
		List<Qualification> qualifications=m.getQualifications();
		if(qualifications.isEmpty()){
			memberBiographyVO.setQualification("-");
		}else{
			StringBuffer buffer=new StringBuffer();
			int count=0;
			for(Qualification i:qualifications){
				if(count>0){
					buffer.append("<br>"+i.getDegree().getName()+":"+i.getDetails());
				}else{
					buffer.append(i.getDegree().getName()+":"+i.getDetails());
				}
			}
			memberBiographyVO.setQualification(buffer.toString());
		}
		//religion
		if(m.getReligion()==null){
			memberBiographyVO.setReligion("-");
		}else{
			memberBiographyVO.setReligion(m.getReligion().getName());
		}
		//category
		if(m.getReservation()==null){
			memberBiographyVO.setCategory("-");
		}else{
			memberBiographyVO.setCategory(m.getReservation().getName());
		}
		//caste
		memberBiographyVO.setCaste("-");
		if(m.getCaste()!=null){
			if(!m.getCaste().isEmpty()){
				memberBiographyVO.setCaste(m.getCaste());
			}
		}
		//marital status
		if(m.getMaritalStatus()==null){
			memberBiographyVO.setMaritalStatus("-");
		}else{
			memberBiographyVO.setMaritalStatus(m.getMaritalStatus().getName());
		}
		//spouse name,spouse relation,noofsons,noofdaughters,noofchildren
		memberBiographyVO.setNoOfDaughter("-");
		memberBiographyVO.setNoOfSons("-");
		memberBiographyVO.setNoOfChildren("-");
		memberBiographyVO.setSpouse("-");
		if(m.getFamilyMembers().isEmpty()){
		}else{
			//right now we are doing just for marathi.and so we are comparing directly with the ids.
			int noOfSons=0;
			int noOfDaughters=0;
			int noOfChildren=0;

			for(FamilyMember i:m.getFamilyMembers()){
				if(i.getRelation().getType().equals(ApplicationConstants.WIFE)||i.getRelation().getType().equals(ApplicationConstants.HUSBAND)){
					memberBiographyVO.setSpouse(i.getName());
					memberBiographyVO.setSpouseRelation(i.getRelation().getName());
				}else if(i.getRelation().getType().equals(ApplicationConstants.SON)){
					noOfSons++;
				}else if(i.getRelation().getType().equals(ApplicationConstants.DAUGHTER)){
					noOfDaughters++;
				}
			}
			if(noOfSons==0){
				memberBiographyVO.setNoOfSons("-");
			}else{
				memberBiographyVO.setNoOfSons(formatWithoutGrouping.format(noOfSons));
			}
			if(noOfDaughters==0){
				memberBiographyVO.setNoOfDaughter("-");
			}else{
				memberBiographyVO.setNoOfDaughter(formatWithoutGrouping.format(noOfDaughters));
			}
			noOfChildren=noOfDaughters+noOfSons;
			if(noOfChildren==0){
				memberBiographyVO.setNoOfChildren("-");
			}else{
				memberBiographyVO.setNoOfChildren(formatWithoutGrouping.format(noOfChildren));
			}
		}

		//languages.This will be comma separated values
		CustomParameter andLocalizedName=CustomParameter.findByName(CustomParameter.class,"AND", locale);
		List<Language> languages=m.getLanguages();
		if(languages.isEmpty()){
			memberBiographyVO.setLanguages("-");
		}else{
			Map<Integer,Language> languageMap=new HashMap<Integer, Language>();
			for(Language i:m.getLanguages()){
				languageMap.put(i.getPriority(),i);
			}
			List<Language> sortedLanguage=new ArrayList<Language>();
			for(Entry<Integer, Language> j:languageMap.entrySet()){
				sortedLanguage.add(j.getValue());
			}
			StringBuffer buffer=new StringBuffer();
			int size=sortedLanguage.size();
			int count=0;
			for(Language i:sortedLanguage){
				count++;
				if(count==size-1){
					buffer.append(i.getName()+" "+andLocalizedName.getValue()+" ");
				}else if(count==size){
					buffer.append(i.getName());
				}else{
					buffer.append(i.getName()+", ");
				}
			}
			//buffer.deleteCharAt(buffer.length()-1);
			memberBiographyVO.setLanguages(buffer.toString());
		}
		//profession.this will also be comma separated values
		List<Profession> professions=m.getProfessions();
		if(m.getProfessions().isEmpty()){
			memberBiographyVO.setProfessions("-");
		}else{
			StringBuffer buffer=new StringBuffer();
			int size=professions.size();
			int count=0;
			for(Profession i:professions){
				count++;
				if(count==size-1){
					buffer.append(i.getName()+" "+andLocalizedName.getValue()+" ");
				}else if(count==size){
					buffer.append(i.getName());
				}else{
					buffer.append(i.getName()+", ");
				}
			}
			// buffer.deleteCharAt(buffer.length()-1);
			memberBiographyVO.setProfessions(buffer.toString());
		}
		//death date,condolence date and obituary
		if(m.getDeathDate()==null){
			memberBiographyVO.setDeathDate("-");
		}else{
			memberBiographyVO.setDeathDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getDeathDate()),locale));
		}
		if(m.getCondolenceDate()==null){
			memberBiographyVO.setCondolenceDate("-");
		}else{
			memberBiographyVO.setCondolenceDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getCondolenceDate()),locale));
		}
		//paname,contact no and address
		memberBiographyVO.setPaAddress("-");
		memberBiographyVO.setPaContactNo("-");
		memberBiographyVO.setPaName("-");
		if(m.getPaName()!=null){
			if(!m.getPaName().isEmpty()){
				memberBiographyVO.setPaName(m.getPaName());
			}
		}
		if(m.getPaContactNo()!=null){
			if(!m.getPaContactNo().isEmpty()){
				memberBiographyVO.setPaContactNo(m.getPaContactNo());
			}
		}
		if(m.getPaAddress()!=null){
			if(!m.getPaAddress().isEmpty()){
				memberBiographyVO.setPaAddress(m.getPaAddress());
			}
		}
		//positions held
		List<PositionHeld> positionHelds=m.getPositionsHeld();
		List<PositionHeldVO> positionHeldVOs=new ArrayList<PositionHeldVO>();
		if(m.getPositionsHeld()!=null){
			for(PositionHeld i:positionHelds){
				PositionHeldVO positionHeldVO=new PositionHeldVO();
				if(i.getToDate()==null){
					positionHeldVO.setToDate("-");
				}else{
					positionHeldVO.setToDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(i.getToDate()),locale));
				}
				if(i.getFromDate()==null){
					positionHeldVO.setFromDate("-");
				}else{
					positionHeldVO.setFromDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(i.getFromDate()),locale));
				}
				if(i.getPosition().isEmpty()){
					positionHeldVO.setPosition("-");
				}else{
					positionHeldVO.setPosition(i.getPosition().trim());
				}
				positionHeldVOs.add(positionHeldVO);
			}
			memberBiographyVO.setPositionsHeld(positionHeldVOs);
		}
		//other info
		memberBiographyVO.setOtherInformation("-");
		memberBiographyVO.setCountriesVisited("-");
		memberBiographyVO.setPublications("-");
		memberBiographyVO.setSpecialInterest("-");
		if(m.getOtherInformation()!=null){
			if(!m.getOtherInformation().isEmpty()){
				memberBiographyVO.setOtherInformation(m.getOtherInformation());
			}
		}
		//countries visited
		if(m.getCountriesVisited()!=null){
			if(!m.getCountriesVisited().isEmpty()){
				memberBiographyVO.setCountriesVisited(m.getCountriesVisited());
			}
		}
		//publications
		if(m.getPublications()!=null){
			if(!m.getPublications().isEmpty()){
				memberBiographyVO.setPublications(m.getPublications());
			}
		}
		//special interest
		if(m.getHobbySpecialInterests()!=null){
			if(!m.getHobbySpecialInterests().isEmpty()){
				memberBiographyVO.setSpecialInterest(m.getHobbySpecialInterests());
			}
		}

		//*********************************************************************

		//******************Contact Details*************************************
		Contact contact=m.getContact();
		if(contact==null){
			memberBiographyVO.setEmail1("-");
			memberBiographyVO.setEmail2("-");
			memberBiographyVO.setWebsite1("-");
			memberBiographyVO.setWebsite2("-");
			memberBiographyVO.setMobile1("-");
			memberBiographyVO.setMobile2("-");
			memberBiographyVO.setFax1("-");
			memberBiographyVO.setFax2("-");
			memberBiographyVO.setFax3("-");
			memberBiographyVO.setFax4("-");
			memberBiographyVO.setFax5("-");
			memberBiographyVO.setFax6("-");
			memberBiographyVO.setFax7("-");
			memberBiographyVO.setFax8("-");
			memberBiographyVO.setFax9("-");
			memberBiographyVO.setFax10("-");
			memberBiographyVO.setFax11("-");
			memberBiographyVO.setTelephone1("-");
			memberBiographyVO.setTelephone2("-");
			memberBiographyVO.setTelephone3("-");
			memberBiographyVO.setTelephone4("-");
			memberBiographyVO.setTelephone5("-");
			memberBiographyVO.setTelephone6("-");
			memberBiographyVO.setTelephone7("-");
			memberBiographyVO.setTelephone8("-");
			memberBiographyVO.setTelephone9("-");
			memberBiographyVO.setTelephone10("-");
			memberBiographyVO.setTelephone11("-");
		}else{
			memberBiographyVO.setEmail1("-");
			memberBiographyVO.setEmail2("-");
			memberBiographyVO.setWebsite1("-");
			memberBiographyVO.setWebsite2("-");
			memberBiographyVO.setMobile1("-");
			memberBiographyVO.setMobile2("-");
			memberBiographyVO.setFax1("-");
			memberBiographyVO.setFax2("-");
			memberBiographyVO.setFax3("-");
			memberBiographyVO.setFax4("-");
			memberBiographyVO.setFax5("-");
			memberBiographyVO.setFax6("-");
			memberBiographyVO.setFax7("-");
			memberBiographyVO.setFax8("-");
			memberBiographyVO.setFax9("-");
			memberBiographyVO.setFax10("-");
			memberBiographyVO.setFax11("-");
			memberBiographyVO.setTelephone1("-");
			memberBiographyVO.setTelephone2("-");
			memberBiographyVO.setTelephone3("-");
			memberBiographyVO.setTelephone4("-");
			memberBiographyVO.setTelephone5("-");
			memberBiographyVO.setTelephone6("-");
			memberBiographyVO.setTelephone7("-");
			memberBiographyVO.setTelephone8("-");
			memberBiographyVO.setTelephone9("-");
			memberBiographyVO.setTelephone10("-");
			memberBiographyVO.setTelephone11("-");
			if(contact.getEmail1()!=null){
				if(!contact.getEmail1().isEmpty()){
					memberBiographyVO.setEmail1(contact.getEmail1());
				}
			}
			if(contact.getEmail2()!=null){
				if(!contact.getEmail2().isEmpty()){
					memberBiographyVO.setEmail2(contact.getEmail2());
				}
			}
			if(contact.getWebsite1()!=null){
				if(!contact.getWebsite1().isEmpty()){
					memberBiographyVO.setWebsite1(contact.getWebsite1());
				}
			}
			if(contact.getWebsite2()!=null){
				if(!contact.getWebsite2().isEmpty()){
					memberBiographyVO.setWebsite2(contact.getWebsite2());
				}
			}
			if(contact.getMobile1()!=null){
				if(!contact.getMobile1().isEmpty()){
					memberBiographyVO.setMobile1(contact.getMobile1());
				}
			}
			if(contact.getMobile2()!=null){
				if(!contact.getMobile2().isEmpty()){
					memberBiographyVO.setMobile2(contact.getMobile2());
				}
			}
			if(contact.getFax1()!=null){
				if(!contact.getFax1().isEmpty()){
					memberBiographyVO.setFax1(contact.getFax1());
				}
			}
			if(contact.getFax2()!=null){
				if(!contact.getFax2().isEmpty()){
					memberBiographyVO.setFax2(contact.getFax2());
				}
			}
			if(contact.getFax3()!=null){
				if(!contact.getFax3().isEmpty()){
					memberBiographyVO.setFax3(contact.getFax3());
				}
			}
			if(contact.getFax4()!=null){
				if(!contact.getFax4().isEmpty()){
					memberBiographyVO.setFax4(contact.getFax4());
				}
			}
			if(contact.getFax5()!=null){
				if(!contact.getFax5().isEmpty()){
					memberBiographyVO.setFax5(contact.getFax5());
				}
			}

			if(contact.getFax6()!=null){
				if(!contact.getFax6().isEmpty()){
					memberBiographyVO.setFax6(contact.getFax6());
				}
			}
			if(contact.getFax7()!=null){
				if(!contact.getFax7().isEmpty()){
					memberBiographyVO.setFax7(contact.getFax7());
				}
			}
			if(contact.getFax8()!=null){
				if(!contact.getFax8().isEmpty()){
					memberBiographyVO.setFax8(contact.getFax8());
				}
			}
			if(contact.getFax9()!=null){
				if(!contact.getFax9().isEmpty()){
					memberBiographyVO.setFax9(contact.getFax9());
				}
			}
			if(contact.getFax10()!=null){
				if(!contact.getFax10().isEmpty()){
					memberBiographyVO.setFax10(contact.getFax10());
				}
			}

			if(contact.getFax11()!=null){
				if(!contact.getFax11().isEmpty()){
					memberBiographyVO.setFax11(contact.getFax11());
				}
			}

			if(contact.getTelephone1()!=null){
				if(!contact.getTelephone1().isEmpty()){
					memberBiographyVO.setTelephone1(contact.getTelephone1());
				}
			}
			if(contact.getTelephone2()!=null){
				if(!contact.getTelephone2().isEmpty()){
					memberBiographyVO.setTelephone2(contact.getTelephone2());
				}
			}
			if(contact.getTelephone3()!=null){
				if(!contact.getTelephone3().isEmpty()){
					memberBiographyVO.setTelephone3(contact.getTelephone3());
				}
			}
			if(contact.getTelephone4()!=null){
				if(!contact.getTelephone4().isEmpty()){
					memberBiographyVO.setTelephone4(contact.getTelephone4());
				}
			}
			if(contact.getTelephone5()!=null){
				if(!contact.getTelephone5().isEmpty()){
					memberBiographyVO.setTelephone5(contact.getTelephone5());
				}
			}

			if(contact.getTelephone6()!=null){
				if(!contact.getTelephone6().isEmpty()){
					memberBiographyVO.setTelephone6(contact.getTelephone6());
				}
			}
			if(contact.getTelephone7()!=null){
				if(!contact.getTelephone7().isEmpty()){
					memberBiographyVO.setTelephone7(contact.getTelephone7());
				}
			}
			if(contact.getTelephone8()!=null){
				if(!contact.getTelephone8().isEmpty()){
					memberBiographyVO.setTelephone8(contact.getTelephone8());
				}
			}
			if(contact.getTelephone9()!=null){
				if(!contact.getTelephone9().isEmpty()){
					memberBiographyVO.setTelephone9(contact.getTelephone9());
				}
			}
			if(contact.getTelephone10()!=null){
				if(!contact.getTelephone10().isEmpty()){
					memberBiographyVO.setTelephone10(contact.getTelephone10());
				}
			}
			if(contact.getTelephone11()!=null){
				if(!contact.getTelephone11().isEmpty()){
					memberBiographyVO.setTelephone11(contact.getTelephone11());
				}
			}

		}
		// CustomParameter tehsilLocalized=CustomParameter.findByName(CustomParameter.class,"TEHSIL", locale);
		CustomParameter districtLocalized=CustomParameter.findByName(CustomParameter.class,"DISTRICT", locale);
		CustomParameter stateLocalized=CustomParameter.findByName(CustomParameter.class,"STATE", locale);
		//present address
		Address presentAddress=m.getPresentAddress();
		if(presentAddress==null){
			memberBiographyVO.setPresentAddress("-");
		}else{
			if(!presentAddress.getDetails().trim().isEmpty()){
				if(presentAddress.getTehsil()!=null){
					memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getTehsil().getName()+","+presentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
				}else{
					memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
				}
			}else{
				memberBiographyVO.setPresentAddress("-");
			}
		}
		//present address
		Address presentAddress1=m.getPresentAddress1();
		if(presentAddress1==null){
			memberBiographyVO.setPresentAddress1("-");
		}else{
			if(!presentAddress1.getDetails().trim().isEmpty()){
				if(presentAddress1.getTehsil()!=null){
					memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+presentAddress1.getTehsil().getName()+","+presentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
				}else{
					memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+presentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
				}
			}else{
				memberBiographyVO.setPresentAddress1("-");
			}
		}
		//present address
		Address presentAddress2=m.getPresentAddress2();
		if(presentAddress2==null){
			memberBiographyVO.setPresentAddress2("-");
		}else{
			if(!presentAddress2.getDetails().trim().isEmpty()){
				if(presentAddress2.getTehsil()!=null){
					memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+presentAddress2.getTehsil().getName()+","+presentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
				}else{
					memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+presentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
				}
			}else{
				memberBiographyVO.setPresentAddress2("-");
			}
		}

		//permanent address
		Address permanentAddress=m.getPermanentAddress();
		if(permanentAddress==null){
			memberBiographyVO.setPermanentAddress("-");
		}else{
			if(!permanentAddress.getDetails().trim().isEmpty()) {
				if(permanentAddress.getTehsil()!=null){
					memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getTehsil().getName()+","+permanentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
				}else{
					memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
				}
			}else{
				memberBiographyVO.setPermanentAddress("-");
			}
		}
		//permanent address
		Address permanentAddress1=m.getPermanentAddress1();
		if(permanentAddress1==null){
			memberBiographyVO.setPermanentAddress1("-");
		}else{
			if(!permanentAddress1.getDetails().trim().isEmpty()) {
				if(permanentAddress1.getTehsil()!=null){
					memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+permanentAddress1.getTehsil().getName()+","+permanentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
				}else{
					memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+permanentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
				}
			}else{
				memberBiographyVO.setPermanentAddress1("-");
			}
		}
		//permanent address
		Address permanentAddress2=m.getPermanentAddress2();
		if(permanentAddress2==null){
			memberBiographyVO.setPermanentAddress2("-");
		}else{
			if(!permanentAddress2.getDetails().trim().isEmpty()) {
				if(permanentAddress2.getTehsil()!=null){
					memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+permanentAddress2.getTehsil().getName()+","+permanentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
				}else{
					memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+permanentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
				}
			}else{
				memberBiographyVO.setPermanentAddress2("-");
			}
		}
		//office address
		Address officeAddress=m.getOfficeAddress();
		if(officeAddress==null){
			memberBiographyVO.setOfficeAddress("-");
		}else{
			if(!officeAddress.getDetails().trim().isEmpty()){
				if(officeAddress.getTehsil()!=null){
					memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
				}else{
					memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
				}
			}else{
				memberBiographyVO.setOfficeAddress("-");
			}
		}
		//office address
		Address officeAddress1=m.getOfficeAddress1();
		if(officeAddress1==null){
			memberBiographyVO.setOfficeAddress1("-");
		}else{
			if(!officeAddress1.getDetails().trim().isEmpty()){
				if(officeAddress1.getTehsil()!=null){
					memberBiographyVO.setOfficeAddress1(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
				}else{
					memberBiographyVO.setOfficeAddress1(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
				}
			}else{
				memberBiographyVO.setOfficeAddress1("-");
			}
		}
		//office address
		Address officeAddress2=m.getOfficeAddress2();
		if(officeAddress2==null){
			memberBiographyVO.setOfficeAddress2("-");
		}else{
			if(!officeAddress2.getDetails().trim().isEmpty()){
				if(officeAddress2.getTehsil()!=null){
					memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+officeAddress2.getTehsil().getName()+","+officeAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
				}else{
					memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+officeAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
				}
			}else{
				memberBiographyVO.setOfficeAddress2("-");
			}
		}
		//temp1 address
		Address tempAddress1=m.getTempAddress1();
		if(tempAddress1==null){
			memberBiographyVO.setTempAddress1("-");
		}else{
			if(!tempAddress1.getDetails().trim().isEmpty()){
				if(tempAddress1.getTehsil()!=null){
					memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tempAddress1.getTehsil().getName()+","+tempAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
				}else{
					memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tempAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
				}
			}else{
				memberBiographyVO.setTempAddress1("-");
			}
		}
		//temp2 address
		Address tempAddress2=m.getTempAddress2();
		if(tempAddress2==null){
			memberBiographyVO.setTempAddress2("-");
		}else{
			if(!tempAddress2.getDetails().trim().isEmpty()){
				if(tempAddress2.getTehsil()!=null){
					memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tempAddress2.getTehsil().getName()+","+tempAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
				}else{
					memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tempAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
				}
			}else{
				memberBiographyVO.setTempAddress2("-");
			}
		}

		//elelction results
		List<ElectionResult> electionResults=m.getElectionResults();
		List<ElectionResultVO> electionResultVOs=new ArrayList<ElectionResultVO>();
		if(!electionResults.isEmpty()){
			for(ElectionResult i:electionResults){
				ElectionResultVO electionResultVO=new ElectionResultVO();
				electionResultVO.setValidVotes("-");
				electionResultVO.setVotesReceived("-");
				electionResultVO.setNoOfVoters("-");
				electionResultVO.setConstituency("-");
				electionResultVO.setElection("-");
				electionResultVO.setElectionResultDate("-");
				electionResultVO.setElectionType("-");
				electionResultVO.setVotingDate("-");
				electionResultVO.setRivalMembers(new ArrayList<RivalMemberVO>());
				if(i.getTotalValidVotes()!=null){
					electionResultVO.setValidVotes(formatWithGrouping.format(electionResults.get(0).getTotalValidVotes()));
				}
				if(i.getVotesReceived()!=null){
					electionResultVO.setVotesReceived(formatWithGrouping.format(electionResults.get(0).getVotesReceived()));
				}
				if(i.getNoOfVoters()!=null){
					electionResultVO.setNoOfVoters(formatWithGrouping.format(electionResults.get(0).getNoOfVoters()));
				}
				if(i.getElection()!=null){
					electionResultVO.setElection(i.getElection().getName());
					electionResultVO.setElectionType(i.getElection().getElectionType().getName());
				}
				if(i.getElectionResultDate()!=null){
					electionResultVO.setElectionResultDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(i.getElectionResultDate()),locale));
				}
				if(i.getVotingDate()!=null){
					electionResultVO.setVotingDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(i.getVotingDate()),locale));
				}
				Constituency constituency=i.getConstituency();
				if(i.getConstituency()!=null){
					if(constituency!=null){
						if(!constituency.getDistricts().isEmpty()){
							if(constituency.getNumber()!=null){
								if(!constituency.getNumber().isEmpty()){
									if(constituency.getIsReserved()){
										memberBiographyVO.setConstituency(formatWithoutGrouping.format(Long.parseLong(constituency.getNumber().trim()))+"-"+constituency.getName()+"("+constituency.getReservedFor().getName()+"), "+districtLocalized.getValue()+"-"+constituency.getDistricts().get(0).getName()+" ");
									}else{
										memberBiographyVO.setConstituency(formatWithoutGrouping.format(Long.parseLong(constituency.getNumber().trim()))+"-"+constituency.getName()+", "+districtLocalized.getValue()+"-"+constituency.getDistricts().get(0).getName()+" ");
									}
								}
							}
						}else{
							if(constituency.getNumber()!=null){
								memberBiographyVO.setConstituency(formatWithoutGrouping.format(Long.parseLong(constituency.getNumber().trim()))+"-"+constituency.getName());
							}else{
								if(constituency.getDisplayName()!=null){
									memberBiographyVO.setConstituency(constituency.getDisplayName());
								}else{
									memberBiographyVO.setConstituency(constituency.getName());
								}
							}
						}
					}
				}
				List<RivalMember> rivals=i.getRivalMembers();
				List<RivalMemberVO> rivalMemberVOs=new ArrayList<RivalMemberVO>();
				if(!rivals.isEmpty()){
					for(RivalMember j:rivals){
						RivalMemberVO rivalMemberVO=new RivalMemberVO();
						rivalMemberVO.setName(j.getName());
						rivalMemberVO.setParty(j.getParty().getName());
						if(j.getVotesReceived()!=null){
							rivalMemberVO.setVotesReceived(formatWithGrouping.format(j.getVotesReceived()));
						}else{
							rivalMemberVO.setVotesReceived("-");
						}
						rivalMemberVOs.add(rivalMemberVO);
					}
					electionResultVO.setRivalMembers(rivalMemberVOs);
				}else{
					electionResultVO.setRivalMembers(rivalMemberVOs);
				}
				electionResultVOs.add(electionResultVO);
			}
			memberBiographyVO.setElectionResults(electionResultVOs);
		}
		//parties associations.
		List<MemberPartyAssociation> memberPartyAssociations=m.getMemberPartyAssociations();
		memberBiographyVO.setParty("-");
		if(memberPartyAssociations!=null){
			if(!memberPartyAssociations.isEmpty()){
				memberBiographyVO.setMemberPartyAssociations(m.getMemberPartyAssociations());
				//this will be date based
				if(memberPartyAssociations.get(0).getParty()!=null){
					memberBiographyVO.setParty(memberPartyAssociations.get(0).getParty().getName());
				}
				//this will be date based...add code for party symbols
			}
		}
		//house member role associations
		memberBiographyVO.setHouseMemberRoleAssociations(m.getHouseMemberRoleAssociations());
		return memberBiographyVO;
	}

	//	@SuppressWarnings("rawtypes")
	//	public MemberAgeWiseReportVO findMembersByAge(final Long house,final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_AGE_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberAgeWiseVO> memberAgeWiseVOs=new ArrayList<MemberAgeWiseVO>();
	//		int totalMale=0;
	//		int totalFemale=0;
	//		int birthdateNotFoundMale=0;
	//		int birthdateNotFoundFemale=0;
	//		CustomParameter infoNotFound=CustomParameter.findByName(CustomParameter.class,"INFO_NOT_FOUND", "");
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;			
	//			MemberAgeWiseVO memberAgeWiseVO=new MemberAgeWiseVO();
	//			memberAgeWiseVO.setAgeGroup(o[0].toString());
	//			memberAgeWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
	//			memberAgeWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
	//			memberAgeWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
	//			memberAgeWiseVOs.add(memberAgeWiseVO);
	//			totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
	//			totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
	//			//here the string to compare is locale based and as such it is stored as staic final constants
	//			//in application locale class to avoid hard coding it in source.
	//			if(infoNotFound.getValue().contains(o[0].toString())){
	//				birthdateNotFoundMale=Integer.parseInt(o[2].toString().trim());
	//				birthdateNotFoundFemale=Integer.parseInt(o[3].toString().trim());
	//			}
	//		}
	//		MemberAgeWiseReportVO memberAgeWiseReportVO=new MemberAgeWiseReportVO();
	//		memberAgeWiseReportVO.setMemberAgeWiseVOs(memberAgeWiseVOs);
	//		memberAgeWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
	//		memberAgeWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-birthdateNotFoundFemale)));
	//		memberAgeWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
	//		memberAgeWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-birthdateNotFoundMale)));
	//		memberAgeWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(birthdateNotFoundMale)));
	//		memberAgeWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(birthdateNotFoundFemale)));
	//		memberAgeWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-birthdateNotFoundFemale+totalMale-birthdateNotFoundMale)));
	//		memberAgeWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(birthdateNotFoundMale+birthdateNotFoundFemale)));
	//		memberAgeWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
	//		return memberAgeWiseReportVO;
	//	}
	//
	//
	//	@SuppressWarnings("rawtypes")
	//	public MemberQualificationWiseReportVO findMembersByQualification(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_QUALIFICATION_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberQualificationWiseVO> memberQualificationWiseVOs=new ArrayList<MemberQualificationWiseVO>();
	//		int totalMale=0;
	//		int totalFemale=0;
	//		int qualificationNotFoundMale=0;
	//		int qualificationNotFoundFemale=0;
	//		CustomParameter infoNotFound=CustomParameter.findByName(CustomParameter.class,"INFO_NOT_FOUND", "");
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberQualificationWiseVO memberQualificationWiseVO=new MemberQualificationWiseVO();
	//			memberQualificationWiseVO.setQualification(o[0].toString());
	//			memberQualificationWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
	//			memberQualificationWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
	//			memberQualificationWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
	//			memberQualificationWiseVOs.add(memberQualificationWiseVO);
	//			totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
	//			totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
	//			//here the string to compare is locale based and as such it is stored as staic final constants
	//			//in application locale class to avoid hard coding it in source.
	//			if(infoNotFound.getValue().contains(o[0].toString())){
	//				qualificationNotFoundMale=Integer.parseInt(o[2].toString().trim());
	//				qualificationNotFoundFemale=Integer.parseInt(o[3].toString().trim());
	//			}
	//		}
	//		MemberQualificationWiseReportVO memberQualificationWiseReportVO = new MemberQualificationWiseReportVO();
	//		memberQualificationWiseReportVO.setMemberQualificationWiseVOs(memberQualificationWiseVOs);
	//		memberQualificationWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
	//		memberQualificationWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-qualificationNotFoundFemale)));
	//		memberQualificationWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
	//		memberQualificationWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-qualificationNotFoundMale)));
	//		memberQualificationWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(qualificationNotFoundMale)));
	//		memberQualificationWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(qualificationNotFoundFemale)));
	//		memberQualificationWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-qualificationNotFoundFemale+totalMale-qualificationNotFoundMale)));
	//		memberQualificationWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(qualificationNotFoundMale+qualificationNotFoundFemale)));
	//		memberQualificationWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
	//		return memberQualificationWiseReportVO;
	//	}
	//
	//	@SuppressWarnings("rawtypes")
	//	public MemberProfessionWiseReportVO findMembersByProfession(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_PROFESSION_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberProfessionWiseVO> memberProfessionWiseVOs=new ArrayList<MemberProfessionWiseVO>();
	//
	//		int totalAvMale=0;
	//		int totalAvFemale=0;
	//		int professionNotFoundMale=0;
	//		int professionNotFoundFemale=0;
	//		CustomParameter infoNotFound=CustomParameter.findByName(CustomParameter.class,"INFO_NOT_FOUND", "");
	//		CustomParameter infoFound=CustomParameter.findByName(CustomParameter.class,"INFO_FOUND", "");
	//
	//
	//
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberProfessionWiseVO memberProfessionWiseVO=new MemberProfessionWiseVO();
	//			memberProfessionWiseVO.setProfession(o[0].toString());
	//			memberProfessionWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
	//			memberProfessionWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
	//			memberProfessionWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
	//			memberProfessionWiseVOs.add(memberProfessionWiseVO);
	//			//totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
	//			//totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
	//			//here the string to compare is locale based and as such it is stored as staic final constants
	//			//in application locale class to avoid hard coding it in source.
	//			if(infoNotFound.getValue().contains(o[0].toString())){
	//				professionNotFoundMale=Integer.parseInt(o[2].toString().trim());
	//				professionNotFoundFemale=Integer.parseInt(o[3].toString().trim());
	//				//this is the case when records is info not found as in db query it is specified as 0
	//				memberProfessionWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())+Long.parseLong(o[3].toString().trim())));
	//			}
	//			if(infoFound.getValue().contains(o[0].toString())){
	//				totalAvMale=Integer.parseInt(o[2].toString().trim());
	//				totalAvFemale=Integer.parseInt(o[3].toString().trim());
	//				//this is the case when records is info not found as in db query it is specified as 0
	//				memberProfessionWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())+Long.parseLong(o[3].toString().trim())));
	//			}
	//		}
	//		MemberProfessionWiseReportVO memberProfessionWiseReportVO = new MemberProfessionWiseReportVO();
	//		memberProfessionWiseReportVO.setMemberProfessionWiseVOs(memberProfessionWiseVOs);
	//		memberProfessionWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalAvFemale+professionNotFoundFemale)));
	//		memberProfessionWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalAvMale+professionNotFoundMale)));
	//		memberProfessionWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalAvFemale+professionNotFoundFemale+totalAvMale+professionNotFoundMale)));
	//		return memberProfessionWiseReportVO;
	//	}
	//
	//	@SuppressWarnings("rawtypes")
	//	public MemberChildrenWiseReportVO findMembersByChildren(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_CHILDREN_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberChildrenWiseVO> memberChildrenWiseVOs=new ArrayList<MemberChildrenWiseVO>();
	//
	//		int totalMale=0;
	//		int totalFemale=0;
	//		int childrenNotFoundMale=0;
	//		int childrenNotFoundFemale=0;
	//		CustomParameter infoNotFound=CustomParameter.findByName(CustomParameter.class,"INFO_NOT_FOUND", "");
	//
	//
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberChildrenWiseVO memberChildrenWiseVO=new MemberChildrenWiseVO();
	//			memberChildrenWiseVO.setChildren(o[0].toString());
	//			memberChildrenWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
	//			memberChildrenWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
	//			memberChildrenWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
	//			memberChildrenWiseVOs.add(memberChildrenWiseVO);
	//			totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
	//			totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
	//			//here the string to compare is locale based and as such it is stored as staic final constants
	//			//in application locale class to avoid hard coding it in source.
	//			if(infoNotFound.getValue().contains(o[0].toString())){
	//				childrenNotFoundMale=Integer.parseInt(o[2].toString().trim());
	//				childrenNotFoundFemale=Integer.parseInt(o[3].toString().trim());
	//			}
	//		}
	//		MemberChildrenWiseReportVO memberChildrenWiseReportVO = new MemberChildrenWiseReportVO();
	//		memberChildrenWiseReportVO.setMemberChildrenWiseVOs(memberChildrenWiseVOs);
	//		memberChildrenWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
	//		memberChildrenWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-childrenNotFoundFemale)));
	//		memberChildrenWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
	//		memberChildrenWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-childrenNotFoundMale)));
	//		memberChildrenWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(childrenNotFoundMale)));
	//		memberChildrenWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(childrenNotFoundFemale)));
	//		memberChildrenWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-childrenNotFoundFemale+totalMale-childrenNotFoundMale)));
	//		memberChildrenWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(childrenNotFoundMale+childrenNotFoundFemale)));
	//		memberChildrenWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
	//		return memberChildrenWiseReportVO;
	//	}
	//
	//	@SuppressWarnings("rawtypes")
	//	public MemberPartyWiseReportVO findMembersByParty(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_PARTY_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberPartyWiseVO> memberPartyWiseVOs=new ArrayList<MemberPartyWiseVO>();
	//
	//		int totalMale=0;
	//		int totalFemale=0;
	//		int partyNotFoundMale=0;
	//		int partyNotFoundFemale=0;
	//		CustomParameter infoNotFound=CustomParameter.findByName(CustomParameter.class,"INFO_NOT_FOUND", "");
	//
	//
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberPartyWiseVO memberPartyWiseVO=new MemberPartyWiseVO();
	//			memberPartyWiseVO.setParty(o[0].toString());
	//			memberPartyWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
	//			memberPartyWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
	//			memberPartyWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
	//			memberPartyWiseVOs.add(memberPartyWiseVO);
	//			totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
	//			totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
	//			//here the string to compare is locale based and as such it is stored as staic final constants
	//			//in application locale class to avoid hard coding it in source.
	//			if(infoNotFound.getValue().contains(o[0].toString())){
	//				partyNotFoundMale=Integer.parseInt(o[2].toString().trim());
	//				partyNotFoundFemale=Integer.parseInt(o[3].toString().trim());
	//			}
	//		}
	//		MemberPartyWiseReportVO memberPartyWiseReportVO = new MemberPartyWiseReportVO();
	//		memberPartyWiseReportVO.setMemberPartyWiseVOs(memberPartyWiseVOs);
	//		memberPartyWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
	//		memberPartyWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-partyNotFoundFemale)));
	//		memberPartyWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
	//		memberPartyWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-partyNotFoundMale)));
	//		memberPartyWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(partyNotFoundMale)));
	//		memberPartyWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(partyNotFoundFemale)));
	//		memberPartyWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-partyNotFoundFemale+totalMale-partyNotFoundMale)));
	//		memberPartyWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(partyNotFoundMale+partyNotFoundFemale)));
	//		memberPartyWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
	//		return memberPartyWiseReportVO;
	//	}
	//
	//	@SuppressWarnings("rawtypes")
	//	public List<MemberPartyDistrictWiseVO> findMembersByPartyDistrict(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		//creating query to execute
	//		List<District> districts=District.findAll(District.class,"name", ApplicationConstants.ASC,locale);
	//		StringBuffer buffer=new StringBuffer();
	//		buffer.append("SELECT "+
	//				"p.name AS group_party_wise,"+
	//		"COUNT(p.name) AS totalmembers_in_each_group,");
	//		/**********************LOCALE_BASED_CODE************************************************
	//		 * here code will be locale dependent and hence needs to be changed on adding new locale.
	//		 ***************************************************************************************/
	//		if(locale.equals(ApplicationLocale.findDefaultLocale())){
	//			buffer.append("SUM(CASE WHEN g.name='à¤ªà¥�à¤°à¥�à¤·' THEN 1 ELSE 0 END) AS totalmalecount,");
	//			buffer.append("SUM(CASE WHEN g.name='à¤¸à¥�à¤¤à¥�à¤°à¥€' THEN 1 ELSE 0 END) AS totalfemalecount, ");
	//		}else{
	//			buffer.append("SUM(CASE WHEN g.name='Male' THEN 1 ELSE 0 END) AS totalmalecount,");
	//			buffer.append("SUM(CASE WHEN g.name='Female' THEN 1 ELSE 0 END) AS totalfemalecount, ");
	//		}
	//		/***************************************************************************************/
	//		for(District i:districts){
	//			buffer.append("SUM(CASE WHEN d.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS '"+i.getName().trim().replaceAll(" ","_")+"',");
	//		}
	//		buffer.deleteCharAt(buffer.length()-1);
	//		buffer.append("FROM members AS m JOIN members_parties AS mp JOIN parties AS p ");
	//		buffer.append("JOIN  genders AS g ");
	//		buffer.append("JOIN election_results AS er JOIN  constituencies AS c JOIN constituencies_districts AS cd JOIN districts AS d JOIN divisions AS divi ");
	//		buffer.append("WHERE mp.member=m.id AND mp.party=p.id AND ");
	//		buffer.append("g.id=m.gender_id AND er.member_id=m.id AND er.constituency_id=c.id AND c.id=cd.constituency_id AND cd.district_id=d.id AND ");
	//		buffer.append("d.division_id=divi.id AND m.locale="+ApplicationLocale.findDefaultLocale()+" GROUP BY group_party_wise ");
	//		String firstSelect=buffer.toString();
	//		buffer.append("UNION ");
	//		buffer.append("SELECT ");
	//		buffer.append("'à¤�à¤•à¥‚à¤£', ");
	//		buffer.append("SUM(dt.totalmembers_in_each_group), ");
	//		buffer.append("SUM(dt.totalmalecount), ");
	//		buffer.append("SUM(dt.totalfemalecount), ");
	//		for(District i:districts){
	//			buffer.append("SUM(dt."+i.getName().trim().replaceAll(" ", "_")+"),");
	//		}
	//		buffer.deleteCharAt(buffer.length()-1);
	//		buffer.append("FROM (");
	//		buffer.append(firstSelect);
	//		buffer.append(") AS dt");
	//		String query=buffer.toString();
	//		//query ends
	//		List results=this.em().createNativeQuery(query).getResultList();
	//		List<MemberPartyDistrictWiseVO> memberPartyDistrictWiseVOs=new ArrayList<MemberPartyDistrictWiseVO>();
	//		int noOfDistricts=districts.size();
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberPartyDistrictWiseVO memberPartyDistrictWiseVO=new MemberPartyDistrictWiseVO();
	//			memberPartyDistrictWiseVO.setParty(o[0].toString());
	//			memberPartyDistrictWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
	//			memberPartyDistrictWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
	//			memberPartyDistrictWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
	//			//Map<String,String> districtWiseDistribution=new TreeMap<String, String>();
	//			List<Reference> districtWiseDistribution=new ArrayList<Reference>();
	//			for(int j=0;j<noOfDistricts;j++){
	//				districtWiseDistribution.add(new Reference(districts.get(j).getName(),formatWithoutGrouping.format(Long.parseLong(o[j+4].toString().trim()))));
	//			}
	//			memberPartyDistrictWiseVO.setDistrictsWiseCount(districtWiseDistribution);
	//			memberPartyDistrictWiseVOs.add(memberPartyDistrictWiseVO);
	//		}
	//		return memberPartyDistrictWiseVOs;
	//	}
	//
	//	@SuppressWarnings("rawtypes")
	//	public List<MemberGeneralVO> findfemaleMembers(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query=Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_FEMALE_MEMBERS", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberGeneralVO> memberGeneralVOs=new ArrayList<MemberGeneralVO>();
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberGeneralVO memberGeneralVO=new MemberGeneralVO();
	//			StringBuffer fullName = new StringBuffer();
	//			if(o[0] != null){
	//				fullName.append(o[0].toString()+", ");
	//			}
	//			if(o[1] != null){
	//				fullName.append(o[1].toString()+" ");
	//			}
	//			if(o[2] != null){
	//				fullName.append(o[2].toString()+" ");
	//			}
	//			if(o[3] != null){
	//				fullName.append(o[3].toString());
	//			}
	//			memberGeneralVO.setFullName(fullName.toString());
	//			if(o[4] != null){
	//				memberGeneralVO.setConstituencyNo(formatWithoutGrouping.format(Long.parseLong(o[4].toString().trim())));
	//			}else{
	//				memberGeneralVO.setConstituencyNo("-");
	//			}
	//			if(o[5] != null){
	//				memberGeneralVO.setConstituencyName(o[5].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyName("-");
	//			}
	//			if(o[6] != null){
	//				memberGeneralVO.setConstituencyDistrict(o[6].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyDistrict("-");
	//			}
	//			if(o[7] != null){
	//				memberGeneralVO.setConstituencyReservation(o[7].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyReservation("-");
	//			}
	//			if(o[8] != null){
	//				memberGeneralVO.setPartyName(o[8].toString().trim());
	//			}else{
	//				memberGeneralVO.setPartyName("-");
	//			}
	//			memberGeneralVOs.add(memberGeneralVO);
	//		}
	//		return memberGeneralVOs;
	//	}
	//
	//	@SuppressWarnings("rawtypes")
	//	public List<MemberGeneralVO> findMembersByLastName(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query=Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_MEMBERS_LIST_LASTNAME_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberGeneralVO> memberGeneralVOs=new ArrayList<MemberGeneralVO>();
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberGeneralVO memberGeneralVO=new MemberGeneralVO();
	//			StringBuffer fullName = new StringBuffer();
	//			if(o[0] != null){
	//				fullName.append(o[0].toString()+", ");
	//			}
	//			if(o[1] != null){
	//				fullName.append(o[1].toString()+" ");
	//			}
	//			if(o[2] != null){
	//				fullName.append(o[2].toString()+" ");
	//			}
	//			if(o[3] != null){
	//				fullName.append(o[3].toString());
	//			}
	//			memberGeneralVO.setFullName(fullName.toString());
	//			if(o[4] != null){
	//				memberGeneralVO.setConstituencyNo(formatWithoutGrouping.format(Long.parseLong(o[4].toString().trim())));
	//			}else{
	//				memberGeneralVO.setConstituencyNo("-");
	//			}
	//			if(o[5] != null){
	//				memberGeneralVO.setConstituencyName(o[5].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyName("-");
	//			}
	//			if(o[6] != null){
	//				memberGeneralVO.setConstituencyDistrict(o[6].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyDistrict("-");
	//			}
	//			if(o[7] != null){
	//				memberGeneralVO.setConstituencyReservation(o[7].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyReservation("-");
	//			}
	//			if(o[8] != null){
	//				memberGeneralVO.setPartyName(o[8].toString().trim());
	//			}else{
	//				memberGeneralVO.setPartyName("-");
	//			}
	//			memberGeneralVOs.add(memberGeneralVO);
	//		}
	//		return memberGeneralVOs;
	//	}
	//
	//	public List<MemberGeneralVO> findMembersByDistrict(final Long house, final String locale) {
	//		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
	//		Query query=Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_MEMBERS_LIST_DISTRICT_WISE", locale);
	//		javax.persistence.Query persistenceQuery=this.em().createNativeQuery(query.getQuery());
	//		persistenceQuery.setParameter("house", house);		
	//		List results=persistenceQuery.getResultList();
	//		List<MemberGeneralVO> memberGeneralVOs=new ArrayList<MemberGeneralVO>();
	//		for(Object i:results){
	//			Object[] o=(Object[]) i;
	//			MemberGeneralVO memberGeneralVO=new MemberGeneralVO();
	//			StringBuffer fullName = new StringBuffer();
	//			if(o[0] != null){
	//				fullName.append(o[0].toString()+", ");
	//			}
	//			if(o[1] != null){
	//				fullName.append(o[1].toString()+" ");
	//			}
	//			if(o[2] != null){
	//				fullName.append(o[2].toString()+" ");
	//			}
	//			if(o[3] != null){
	//				fullName.append(o[3].toString());
	//			}
	//			memberGeneralVO.setFullName(fullName.toString());
	//			if(o[4] != null){
	//				memberGeneralVO.setConstituencyNo(formatWithoutGrouping.format(Long.parseLong(o[4].toString().trim())));
	//			}else{
	//				memberGeneralVO.setConstituencyNo("-");
	//			}
	//			if(o[5] != null){
	//				memberGeneralVO.setConstituencyName(o[5].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyName("-");
	//			}
	//			if(o[6] != null){
	//				memberGeneralVO.setConstituencyDistrict(o[6].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyDistrict("-");
	//			}
	//			if(o[7] != null){
	//				memberGeneralVO.setConstituencyReservation(o[7].toString().trim());
	//			}else{
	//				memberGeneralVO.setConstituencyReservation("-");
	//			}
	//			if(o[8] != null){
	//				memberGeneralVO.setPartyName(o[8].toString().trim());
	//			}else{
	//				memberGeneralVO.setPartyName("-");
	//			}
	//			memberGeneralVOs.add(memberGeneralVO);
	//		}
	//		return memberGeneralVOs;
	//	}

	public MasterVO findConstituencyByAssemblyId(final Long memberId,final Long house) {
		String query="SELECT c.id,c.display_name FROM members AS m JOIN  members_houses_roles AS mhr JOIN memberroles AS mr"+
		" JOIN houses AS h JOIN constituencies AS c "+
		" WHERE m.id=mhr.member AND mr.id=mhr.role AND mhr.house_id=h.id AND c.id=mhr.constituency_id"+
		" AND  mr.priority=0 AND  "+
		"h.id="+house +" AND m.id="+memberId+" ORDER BY  mhr.record_index DESC LIMIT 0,1";
		try {
			Object o=this.em().createNativeQuery(query).getSingleResult();
			Object[] i=(Object[]) o;
			if(o!=null){
				return new MasterVO(Long.parseLong(i[0].toString()),i[1].toString());
			}else{
				return new MasterVO();
			}
		} catch (NoResultException e) {
			e.printStackTrace();
			return new MasterVO();
		}
	}


	public MasterVO findConstituencyByCouncilDates(final Long member, final Long house,
			final String criteria, final String fromDate, final String toDate) {
		try {
			Date fromDateServerFormat=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(fromDate);
			Date toDateServerFormat=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(toDate);
			String fromDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(fromDateServerFormat);
			String toDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(toDateServerFormat);
			String query1="SELECT c.id,c.display_name FROM members AS m JOIN  members_houses_roles AS mhr JOIN memberroles AS mr"+
			" JOIN houses AS h JOIN constituencies AS c "+
			" WHERE m.id=mhr.member AND mr.id=mhr.role AND mhr.house_id=h.id AND c.id=mhr.constituency_id"+
			" AND  mr.priority=0 AND  "+
			"h.id="+house +" AND m.id="+member;
			String query2=null;
			if(criteria.equals("RANGE")){
				query2=" AND mhr.from_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"' ORDER BY  mhr.record_index DESC LIMIT 0,1";
			}else if(criteria.equals("YEAR")){
				query2=" AND mhr.from_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"' ORDER BY  mhr.record_index DESC LIMIT 0,1";
			}else if(criteria.equals("DATE")){
				query2=" AND mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"' ORDER BY  mhr.record_index DESC LIMIT 0,1";
			}

			try {
				Object o=this.em().createNativeQuery(query1+query2).getSingleResult();
				Object[] i=(Object[]) o;
				if(o!=null){
					return new MasterVO(Long.parseLong(i[0].toString()),i[1].toString());
				}else{
					return new MasterVO();
				}
			} catch (NoResultException e) {
				e.printStackTrace();
				return new MasterVO();
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
			return new MasterVO();
		}

	}

	public MasterVO findPartyByAssemblyId(final Long member, final Long house) {
		String query="SELECT p.id,p.name FROM members AS m JOIN  members_parties AS mp JOIN parties AS p"+
		" WHERE m.id=mp.member AND mp.party=p.id AND "+
		" mp.house_id="+house+" ORDER BY  mhr.record_index DESC LIMIT 0,1";
		try {
			Object o=this.em().createNativeQuery(query).getSingleResult();
			Object[] i=(Object[]) o;
			if(o!=null){
				return new MasterVO(Long.parseLong(i[0].toString()),i[1].toString());
			}else{
				return new MasterVO();
			}
		} catch (NoResultException e) {
			e.printStackTrace();
			return new MasterVO();
		}
	}

	public MasterVO findPartyByCouncilDates(final Long member, final Long house,
			final String criteria, final String fromDate, final String toDate) {
		try {
			Date fromDateServerFormat=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(fromDate);
			Date toDateServerFormat=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").parse(toDate);
			String fromDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(fromDateServerFormat);
			String toDateDBFormat=FormaterUtil.getDateFormatter("yyyy-MM-dd", "en_US").format(toDateServerFormat);
			String query1="SELECT p.id,p.name FROM members AS m JOIN  members_parties AS mp JOIN parties AS p"+
			" WHERE m.id=mp.member AND mp.party=p.id AND "+
			" mp.house_id="+house;
			String query2=null;
			if(criteria.equals("RANGE")){
				query2=" AND mp.from_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"' ORDER BY  mp.record_index DESC LIMIT 0,1";
			}else if(criteria.equals("YEAR")){
				query2=" AND mp.from_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"' ORDER BY  mp.record_index DESC LIMIT 0,1";
			}else if(criteria.equals("DATE")){
				query2=" AND mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"' ORDER BY  mp.record_index DESC LIMIT 0,1";
			}
			try {
				Object o=this.em().createNativeQuery(query1+query2).getSingleResult();
				Object[] i=(Object[]) o;
				if(o!=null){
					return new MasterVO(Long.parseLong(i[0].toString()),i[1].toString());
				}else{
					return new MasterVO();
				}
			} catch (NoResultException e) {
				e.printStackTrace();
				return new MasterVO();
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
			return new MasterVO();
		}
	}

	public Member findMember(final String firstName, final String middleName,
			final String lastName, final Date birthDate, final String locale) {
		
		/**** Previous Code ****/
//		Search search=new Search();
//		if(!firstName.isEmpty()){
//			search.addFilterEqual("firstName",firstName);
//		}
//		if(!middleName.isEmpty()){
//			search.addFilterEqual("middleName",middleName);
//		}
//		if(!lastName.isEmpty()){
//			search.addFilterEqual("lastName",lastName);
//		}
//		if(birthDate!=null){
//			search.addFilterEqual("birthDate", birthDate);
//		}
//		search.addSort("lastName",false);
//		List<Member> members=this.search(search);
//		if(!members.isEmpty()){
//			return members.get(0);
//		}else{
//			return new Member();
//		}
		
		/**** Updated Code ****/
		Member member = null;
		List<Member> possibleMembers = null;
		Map<String, String> memberNameParameters = new HashMap<String, String>();
		
		//Combo 1: firstName + middleName + lastName + birthDate
		try {
			memberNameParameters.put("firstName", firstName);
			memberNameParameters.put("middleName", middleName);
			memberNameParameters.put("lastName", lastName);
//			System.out.println("combo 1:");
//			for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//			    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//			}
			possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
			if(possibleMembers!=null && !possibleMembers.isEmpty()) {
				for(Member m: possibleMembers) {
					if(m.getBirthDate().equals(birthDate)) {
						member = m;
						possibleMembers = null;
						memberNameParameters = null;
						return member;
					} else {
						throw new ELSException("member_not_found", "member_not_found");
					}
				}
			} else {
				throw new ELSException("member_not_found", "member_not_found");
			}
		} catch(ELSException eCombo1) {
			if(eCombo1.getParameter()!=null && eCombo1.getParameter().equals("member_not_found")) {
				//Combo 2: firstName + lastName + birthDate
				try {
					memberNameParameters.remove("middleName");	
//					System.out.println("combo 2:");
//					for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//					    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//					}
					possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
					if(possibleMembers!=null && !possibleMembers.isEmpty()) {
						for(Member m: possibleMembers) {
							if(m.getBirthDate().equals(birthDate)) {
								member = m;
								possibleMembers = null;
								memberNameParameters = null;
								return member;
							} else {
								throw new ELSException("member_not_found", "member_not_found");
							}
						}
					} else {
						throw new ELSException("member_not_found", "member_not_found");
					}
				} catch(ELSException eCombo2) {
					if(eCombo2.getParameter()!=null && eCombo2.getParameter().equals("member_not_found")) {
						//Combo 3: middleName + lastName + birthDate
						try {
							memberNameParameters.remove("firstName");
							memberNameParameters.put("middleName", middleName);			
//							System.out.println("combo 3:");
//							for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//							    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//							}
							possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
							if(possibleMembers!=null && !possibleMembers.isEmpty()) {
								for(Member m: possibleMembers) {
									if(m.getBirthDate().equals(birthDate)) {
										member = m;
										possibleMembers = null;
										memberNameParameters = null;
										return member;
									} else {
										throw new ELSException("member_not_found", "member_not_found");
									}
								}
							} else {
								throw new ELSException("member_not_found", "member_not_found");
							}
						} catch(ELSException eCombo3) {
							if(eCombo3.getParameter()!=null && eCombo3.getParameter().equals("member_not_found")) {
								//Combo 4: firstName + middleName + birthDate
								memberNameParameters.remove("lastName");
								memberNameParameters.put("firstName", firstName);
//								System.out.println("combo 4:");
//								for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//								    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//								}
								possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
								if(possibleMembers!=null && !possibleMembers.isEmpty()) {
									for(Member m: possibleMembers) {
										if(m.getBirthDate().equals(birthDate)) {
											member = m;
											possibleMembers = null;
											memberNameParameters = null;
											return member;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//if member is not found, return blank instance
		possibleMembers = null;
		memberNameParameters = null;
		return new Member();
	}
	
	public Member findMember(final String firstName,
			final String lastName, 
			final Date birthDate, 
			final String locale) {
		Search search=new Search();
		if(!firstName.isEmpty()){
			search.addFilterEqual("firstName",firstName);
		}
		
		if(!lastName.isEmpty()){
			search.addFilterEqual("lastName",lastName);
		}
		if(birthDate!=null){
			search.addFilterEqual("birthDate", birthDate);
		}
		search.addSort("lastName",false);
		List<Member> members=this.search(search);
		if(!members.isEmpty()){
			return members.get(0);
		}else{
			return new Member();
		}

	}
	
	public Member findDuplicateMember(final Long existingMemberId, final String firstName, final String middleName,
			final String lastName, final Date birthDate, final String locale) {
		
		Member member = null;
		List<Member> possibleMembers = null;
		Map<String, String> memberNameParameters = new HashMap<String, String>();
		
		//Combo 1: firstName + middleName + lastName + birthDate
		try {
			memberNameParameters.put("firstName", firstName);
			memberNameParameters.put("middleName", middleName);
			memberNameParameters.put("lastName", lastName);
			possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
			if(possibleMembers!=null && !possibleMembers.isEmpty()) {
				for(Member m: possibleMembers) {
					if(m.getBirthDate().equals(birthDate) && !m.getId().equals(existingMemberId)) {
						member = m;
						possibleMembers = null;
						memberNameParameters = null;
						return member;
					} else {
						throw new ELSException("member_not_found", "member_not_found");
					}
				}
			} else {
				throw new ELSException("member_not_found", "member_not_found");
			}
		} catch(ELSException eCombo1) {
			if(eCombo1.getParameter()!=null && eCombo1.getParameter().equals("member_not_found")) {
				//Combo 2: firstName + lastName + birthDate
				memberNameParameters.remove("middleName");
				possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
				if(possibleMembers!=null && !possibleMembers.isEmpty()) {
					for(Member m: possibleMembers) {
						if(m.getBirthDate().equals(birthDate) && !m.getId().equals(existingMemberId)) {
							member = m;
							possibleMembers = null;
							memberNameParameters = null;
							return member;
						}
					}
				}
			}
		}
		possibleMembers = null;
		memberNameParameters = null;
		return member;
	}
	
	public Member findDuplicateMember(final Long existingMemberId, 
			final String firstName,
			final String lastName, 
			final Date birthDate, 
			final String locale) {
		Search search=new Search();
		if(!firstName.isEmpty()){
			search.addFilterEqual("firstName",firstName);
		}
		
		if(!lastName.isEmpty()){
			search.addFilterEqual("lastName",lastName);
		}
		if(birthDate!=null){
			search.addFilterEqual("birthDate", birthDate);
		}
		search.addSort("lastName",false);
		List<Member> possibleMembers=this.search(search);
		Member member = null;
		if(possibleMembers!=null && !possibleMembers.isEmpty()) {
			for(Member m: possibleMembers) {
				if(m.getBirthDate().equals(birthDate) && !m.getId().equals(existingMemberId)) {
					member = m;
					possibleMembers = null;
					return member;
				}
			}
		}
		possibleMembers = null;
		return member;
	}

	@SuppressWarnings("rawtypes")
	public List<MemberContactVO> getContactDetails(final String[] members) {
		/*
		 * This method is used in assistant screen of qis to fetch the contact details of
		 * primary member and supporting members of a question.
		 */
		String query="SELECT t.name,m.first_name,m.middle_name,m.last_name,c.mobile1,c.email1"+
		",c.telephone1,c.telephone2,c.telephone3,c.telephone4,c.telephone5 FROM members as m JOIN "+
		" titles as t JOIN contacts as c WHERE m.title_id=t.id AND m.contactdetails_id=c.id AND ( ";
		StringBuffer buffer=new StringBuffer();
		for(String i:members){
			buffer.append("m.id="+i+" OR ");
		}
		buffer.delete(buffer.length()-3, buffer.length()-1);
		List results=this.em().createNativeQuery(query+buffer.toString()+" )").getResultList();
		List<MemberContactVO> memberContactVOs=new ArrayList<MemberContactVO>();
		for(Object i:results){
			Object[] o=(Object[]) i;
			MemberContactVO memberContactVO=new MemberContactVO();
			/*
			 * setting full name of members
			 */
			if(o[0]!=null){
				if(o[2]!=null){
					memberContactVO.setFullName(o[0].toString()+" "+o[1].toString()+" "+o[2].toString()+" "+o[3].toString());
				}else{
					memberContactVO.setFullName(o[0].toString()+" "+o[1].toString()+" "+o[3].toString());
				}
			}else{
				if(o[2]!=null){
					memberContactVO.setFullName(o[1].toString()+" "+o[2].toString()+" "+o[3].toString());
				}else{
					memberContactVO.setFullName(o[1].toString()+" "+o[3].toString());
				}
			}
			/*
			 * setting mobile and email
			 */
			if(o[4]!=null){
				memberContactVO.setMobile(o[4].toString());
			}
			if(o[5]!=null){
				memberContactVO.setEmail(o[5].toString());
			}
			/*
			 * setting permanent,present,office,mumbai and nagpur telephone
			 */
			if(o[6]!=null){
				memberContactVO.setPermanentTelephone(o[6].toString());
			}
			if(o[7]!=null){
				memberContactVO.setPresentTelephone(o[7].toString());
			}
			if(o[8]!=null){
				memberContactVO.setOfficeTelephone(o[8].toString());
			}
			if(o[9]!=null){
				memberContactVO.setMumbaiTelephone(o[9].toString());
			}
			if(o[10]!=null){
				memberContactVO.setNagpurTelephone(o[10].toString());
			}
			/*
			 * adding to list
			 */
			memberContactVOs.add(memberContactVO);
		}
		return memberContactVOs;
	}

	public List<Member> findActiveMembers(final House house,
			final MemberRole role,
			final Date date,
			final String sortOrder,
			final String locale) {
		CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String strDate = FormaterUtil.formatDateToString(date, parameter.getValue());

		String strQuery = "SELECT m" +
		" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
		" WHERE hmra.fromDate <= '" + strDate + "'" +
		" AND hmra.toDate >= '" + strDate + "'" +
		" AND hmra.role.id = " + role.getId() +
		" AND hmra.house.id = " + house.getId() +
		" AND hmra.locale = '" + locale + "'" +
		" ORDER BY m.lastName " + sortOrder;

		TypedQuery<Member> query = this.em().createQuery(strQuery, Member.class);
		List<Member> members = query.getResultList();
		return members;
	}

	public Constituency findConstituency(final Long id) {
		try {
			String query="SELECT c FROM HouseMemberRoleAssociation mhr JOIN mhr.constituency c WHERE mhr.member.id="+id+" ORDER BY mhr.fromDate "+ApplicationConstants.DESC;
			List constituencies = this.em().createQuery(query).getResultList();
			if(constituencies != null && !constituencies.isEmpty()){
				return (Constituency) constituencies.get(0);
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error("Entity Not Found",e);
			return null;
		}
	}

	public Party findParty(final Long id) {
		try {
			String query="SELECT p FROM MemberPartyAssociation mp JOIN  mp.party p WHERE mp.member.id="+id+" ORDER BY mp.fromDate "+ApplicationConstants.DESC;
			return (Party) this.em().createQuery(query).getSingleResult();
		} catch (Exception e) {
			logger.error("Entity Not Found",e);
			return null;
		}
	}
	
	public PartyType findPartyType(final Long id,Long house,String locale) {
		try {
			String query="SELECT pt FROM MemberPartyAssociation mp JOIN  mp.party p JOIN p.partyType pt WHERE mp.member.id="+id+" AND mp.house.id="+house+" ";
			return (PartyType) this.em().createQuery(query).getSingleResult();
		} catch (Exception e) {
			logger.error("Entity Not Found",e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Member> findByMemberRole(Long house, Long memberrole, String locale) {
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DB_DATEFORMAT", "");
		SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), "en_US");
		String currentDate=format.format(new Date());
		String query="SELECT m FROM HouseMemberRoleAssociation mhr JOIN mhr.member m "+
		"WHERE mhr.fromDate<='"+currentDate+"' AND (mhr.toDate>='"+currentDate+"' OR mhr.toDate IS NULL) AND mhr.house.id="+house+" AND mhr.role.id="+memberrole+
		" AND (mhr.resignationDate IS NULL OR mhr.resignationDate >'"+currentDate+"') AND m.deathDate IS NULL ORDER BY mhr.fromDate "+ApplicationConstants.DESC;
		return this.em().createQuery(query).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Member> findActiveMembersByParty(Party party,House house,String locale) {
		String strQuery="SELECT m FROM Member m JOIN m.memberPartyAssociations mpa" +
				" JOIN mpa.party p WHERE mpa.fromDate<=:currentDate AND mpa.house.id=:houseId" +
				" AND p.id=:partyId AND (mpa.toDate>=:currentDate OR mpa.toDate IS NULL) AND p.locale=:locale";
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("currentDate", new Date());
		query.setParameter("houseId", house.getId());
		query.setParameter("partyId", party.getId());
		query.setParameter("locale",locale);
		List<Member> members=query.getResultList();
		return members;
	}
	
	@SuppressWarnings("unchecked")
	public List<MasterVO> findActiveMembersByPartyType(final House house,
			final Session session, final String locale,final PartyType partytype, final Long primaryMemberId) {
	
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
					query="(SELECT DISTINCT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr JOIN members_parties as mpa JOIN parties as p JOIN party_types as pt"+
					" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and mpa.member=m.id and p.id=mpa.party and pt.id=p.party_type_id and m.id<>'"+ primaryMemberId+"' and m.locale='"+locale+"' "+
					" and mpa.house_id="+house.getId()+" AND pt.id='"+partytype.getId()+"' AND p.locale='"+locale+"' "+		
					" and (mhr.to_date>='"+strSessionStartDate+"' or mhr.to_date>='"+strSessionEndDate+"') and mr.priority=0 and mhr.house_id="+house.getId()+" ) ORDER BY m.first_name asc";
				}else{
					query="(SELECT DISTINCT m.id,t.name,m.first_name,m.middle_name,m.last_name FROM members_houses_roles as mhr JOIN members as m JOIN memberroles as mr "+
					" JOIN titles as t WHERE t.id=m.title_id and mr.id=mhr.role and mhr.member=m.id and  mpa.member=m.id and p.id=mpa.party and pt.id=p.party_type_id and m.locale='"+locale+"' "+
					" and mpa.house_id="+house.getId()+" AND pt.id='"+partytype.getId()+"' AND p.locale='"+locale+"' "+								
					" and (mhr.to_date>='"+strSessionStartDate+"' or mhr.to_date>='"+strSessionEndDate+"') and mr.priority=0 and mhr.house_id="+house.getId()+" ) ORDER BY m.first_name asc";
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
	
	
	public List<Member> findActiveMembers(final House house, 
			final MemberRole role,
			final Date date, 
			final String nameBeginningWith, 
			final String sortOrder, 
			final String locale) {
		String name = nameBeginningWith;
		
		CustomParameter parameter =
            CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
        String strDate = FormaterUtil.formatDateToString(date, parameter.getValue());

        StringBuffer query = new StringBuffer();
        query.append("SELECT m" +
        	" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
            " WHERE hmra.fromDate <= '" + strDate + "'" +
            " AND hmra.toDate >= '" + strDate + "'" +
            " AND hmra.role.id = " + role.getId() +
            " AND hmra.house.id = " + house.getId() +
            " AND hmra.locale = '" + locale + "'");
        
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
	
	@SuppressWarnings("rawtypes")
	public List<MasterVO> findAllMembersVOsWithGivenIdsAndWithNameContainingParam(final String memberIds, final String param) {
		List<MasterVO> memberVOs = new ArrayList<MasterVO>();
		String query = "SELECT m.id,t.name,m.firstName,m.middleName,m.lastName FROM Member m JOIN m.title t" +
				" WHERE m.id IN ("+memberIds+")"+
				" AND (m.firstName LIKE '%"+param+"%' OR m.middleName LIKE '%"+param+"%' OR m.lastName LIKE '%"+param+"%' OR CONCAT(m.lastName,' ',m.firstName) LIKE '%"+param+"%' OR CONCAT(m.firstName,' ',m.lastName) LIKE '%"+param+"%' OR CONCAT(m.lastName,' ',m.firstName,' ',m.middleName) LIKE '%"+param+"%' OR CONCAT(m.lastName,', ',t.name,' ',m.firstName,' ',m.middleName) LIKE '%"+param+"%' OR CONCAT(m.firstName,' ',m.middleName,' ',m.lastName) LIKE '%"+param+"%')  ORDER BY m.firstName asc";
		List members=this.em().createQuery(query).getResultList();
		for(Object i:members){
			Object[] o=(Object[]) i;
			MasterVO masterVO=new MasterVO();
			masterVO.setId(Long.parseLong(o[0].toString()));
			if(o[3]!=null){
				if(o[1]!=null) {
					masterVO.setName(o[1].toString()+", "+o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
				} else {
					masterVO.setName(o[2].toString()+" "+o[3].toString()+" "+o[4].toString());
				}
			}else{
				if(o[1]!=null) {
					masterVO.setName(o[1].toString()+", "+o[2].toString()+" "+o[4].toString());
				} else {
					masterVO.setName(o[2].toString()+" "+o[4].toString());
				}
			}
			memberVOs.add(masterVO);
		}
		return memberVOs;
	}

	@SuppressWarnings("rawtypes")
	public boolean isActiveMemberOn(final Member member,final Date activeOn,final String locale) {
		String strQuery="Select m FROM Member m JOIN m.houseMemberRoleAssociations hmra "
				+ "WHERE m.id=:member AND hmra.fromDate <=:activeOn AND "
				+ "(hmra.toDate >=:activeOn OR hmra.toDate IS NULL )";
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("member",member.getId());
		query.setParameter("activeOn", activeOn);
		List result=query.getResultList();
		if(result!=null && !result.isEmpty()){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean isActiveMemberInAnyOfGivenRolesOn(final Member member,final String[] memberRoles, final Date activeOn,final String locale) {
		String strQuery="Select m FROM Member m JOIN m.houseMemberRoleAssociations hmra "
				+ "WHERE m.id=:member "
				+ "AND hmra.fromDate <=:activeOn AND hmra.role.type IN (:memberRoles) "
				+ "AND (hmra.toDate >=:activeOn OR hmra.toDate IS NULL )";
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("member",member.getId());
		query.setParameter("memberRoles",Arrays.asList(memberRoles));
		query.setParameter("activeOn", activeOn);
		List result=query.getResultList();
		if(result!=null && !result.isEmpty()){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean isActiveMinisterOn(final Member member, final Date activeOn, final String locale) {
		String strQuery="Select m FROM Member m JOIN m.memberMinisters mm "
				+ "WHERE m.id=:member "
				+ "AND mm.ministryFromDate <=:activeOn "
				+ "AND (mm.ministryToDate >=:activeOn OR mm.ministryToDate IS NULL )";
		javax.persistence.Query query=this.em().createQuery(strQuery);
		query.setParameter("member",member.getId());		
		query.setParameter("activeOn", activeOn);
		List result=query.getResultList();
		if(result!=null && !result.isEmpty()){
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public boolean isPresentInMemberBallotAttendanceUH(final Session session,final DeviceType deviceType,
			final Member member,
			final String locale) {
			String strQuery="SELECT m FROM MemberBallotAttendance mba JOIN mba.member m "
					+ "JOIN mba.session s JOIN mba.deviceType dt "
					+ "WHERE m.id=:member AND s.id=:session AND dt.id=:deviceType";
			javax.persistence.Query query=this.em().createQuery(strQuery);
			query.setParameter("member",member.getId());
			query.setParameter("session", session.getId());
			query.setParameter("deviceType", deviceType.getId());
			List result=query.getResultList();
			if(result!=null && !result.isEmpty()){
				return true;
			}else{
				return false;
			}		
	}

	public List<Member> findInactiveMembers(final House house, 
			final MemberRole role,
			final Date fromDate, 
			final Date toDate, 
			final String locale) {
		CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String strFromDate = 
			FormaterUtil.formatDateToString(fromDate, parameter.getValue());
		String strToDate = 
			FormaterUtil.formatDateToString(toDate, parameter.getValue());

		String strQuery = "SELECT m" +
			" FROM HouseMemberRoleAssociation hmra JOIN hmra.member m" +
			" WHERE hmra.toDate >= '" + strFromDate + "'" +
			" AND hmra.toDate <= '" + strToDate + "'" +			
			" AND hmra.role.id = " + role.getId() +
			" AND hmra.house.id = " + house.getId() +
			" AND hmra.locale = '" + locale + "'";

		TypedQuery<Member> query = this.em().createQuery(strQuery, Member.class);
		List<Member> members = query.getResultList();
		return members;
	}

	public HouseMemberRoleAssociation find(Member member,
			MemberRole memberRole, Date onDate, String locale) {
		String strQuery = "SELECT hmra " +
				"FROM HouseMemberRoleAssociation  hmra " +
				" WHERE hmra.fromDate<=:onDate " +
				" AND hmra.role.id=:memberRoleId " +
				" AND hmra.member.id=:memberId " +
				" AND (hmra.toDate IS NULL " +
				" OR hmra.toDate>=:onDate) " +
				" AND hmra.locale=:locale";
		javax.persistence.Query query= this.em().createQuery(strQuery);
		query.setParameter("onDate", onDate);
		query.setParameter("memberRoleId", memberRole.getId());
		query.setParameter("memberId", member.getId());
		query.setParameter("locale", locale);
		try{
			HouseMemberRoleAssociation hmra=(HouseMemberRoleAssociation) query.getSingleResult();
			return hmra;
		}catch(Exception e){
			return null;
		}
		
	}

	public Constituency findConstituency(Member member, Date onDate) {
		try {
			String strQuery="SELECT c FROM HouseMemberRoleAssociation mhr " +
					"JOIN mhr.constituency c " +
					"WHERE mhr.member.id=:memberId" +
					" AND mhr.fromDate<=:onDate" +
					" AND mhr.toDate>=:onDate " +
					"ORDER BY mhr.fromDate "+ApplicationConstants.DESC;
			javax.persistence.Query query = this.em().createQuery(strQuery);
			query.setParameter("memberId", member.getId());
			query.setParameter("onDate", onDate);
			return (Constituency) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Entity Not Found",e);
			return null;
		}
	}
	
	public List<Member> findActiveMinisters(final Date activeOn, final String locale) {
		String query = "Select m" +
				" FROM Member m JOIN m.memberMinisters mm" +
				" WHERE mm.ministryFromDate <=:activeOn" +
				" AND (mm.ministryToDate >=:activeOn OR mm.ministryToDate IS NULL)" +
				" AND mm.locale=:locale";
		
		TypedQuery<Member> tQuery = this.em().createQuery(query, Member.class);		
		tQuery.setParameter("activeOn", activeOn);
		tQuery.setParameter("locale", locale);
		List<Member> members = tQuery.getResultList();
		
		return members;
	}
	
	@SuppressWarnings("unchecked")
	public String findHouseTypeByDate(Long member,Date onDate) {
		String strQuery="SELECT distinct ht.type FROM Member m JOIN m.houseMemberRoleAssociations mhr "+
						"join mhr.house h join h.type ht " +
				" where mhr.fromDate <=:onDate and mhr.toDate >=:onDate and m.id=:memberid";
				javax.persistence.Query query=this.em().createQuery(strQuery);
		
		query.setParameter("onDate",onDate );
		query.setParameter("memberid",member );
		List result=query.getResultList();
		
		return result.toString();
	}
	
	public List<Member> findMembersWithHousetype(final String houseType, 
					final String locale) {
		CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String currentDate = 
			FormaterUtil.formatDateToString(new Date(), parameter.getValue());


		String strQuery = "SELECT m from " +
			" Member m JOIN m.houseMemberRoleAssociations mhr " +
			" LEFT JOIN mhr.role mr " +
			" LEFT JOIN mr.houseType ht " +
			"LEFT JOIN mhr.constituency c"+
			" WHERE mhr.fromDate <= '" + currentDate  + "'" +
			" AND mhr.toDate >= '" + currentDate  + "'" +			
			" AND ht.type = '" + houseType  + "'" +	
			" AND mr.type = 'MEMBER'"+
			" AND ht.locale = '" + locale + "'";

		TypedQuery<Member> query = this.em().createQuery(strQuery, Member.class);
		List<Member> members = query.getResultList();
		return members;
	}
	
	public List<Member> findMembersWithConstituency(final String houseType,final Long constituency) {
		CustomParameter parameter =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String currentDate = 
			FormaterUtil.formatDateToString(new Date(), parameter.getValue());
		
		
		String strQuery = "SELECT m from " +
			" Member m JOIN m.houseMemberRoleAssociations mhr " +
			" LEFT JOIN mhr.role mr " +
			" LEFT JOIN mr.houseType ht " +
			"LEFT JOIN mhr.constituency c"+
			" WHERE mhr.fromDate <= '" + currentDate  + "'" +
			" AND mhr.toDate >= '" + currentDate  + "'" +			
			" AND ht.type = '" + houseType  + "'" +
			" AND c.id = " + constituency  + "" +	
			" AND mr.type = 'MEMBER'";
		
		TypedQuery<Member> query = this.em().createQuery(strQuery, Member.class);
		List<Member> members = query.getResultList();
		return members;
	}
	
	public Member findByNameBirthDate(final String firstName,final String middleName,final String lastName,
			final Date birthDate, final String locale) throws ELSException {
		
		Member member = null;
		List<Member> possibleMembers = null;
		Map<String, String> memberNameParameters = new HashMap<String, String>();
		
		//Combo 1: firstName + middleName + lastName + birthDate
		try {
			memberNameParameters.put("firstName", firstName);
			memberNameParameters.put("middleName", middleName);
			memberNameParameters.put("lastName", lastName);
//			System.out.println("combo 1:");
//			for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//			    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//			}
			possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
			if(possibleMembers!=null && !possibleMembers.isEmpty()) {
				for(Member m: possibleMembers) {
					if(m.getBirthDate().equals(birthDate)) {
						member = m;
						possibleMembers = null;
						memberNameParameters = null;
						return member;
					} else {
						throw new ELSException("member_not_found", "member_not_found");
					}
				}
			} else {
				throw new ELSException("member_not_found", "member_not_found");
			}
		} catch(ELSException eCombo1) {
			if(eCombo1.getParameter()!=null && eCombo1.getParameter().equals("member_not_found")) {
				//Combo 2: firstName + lastName + birthDate
				try {
					memberNameParameters.remove("middleName");	
//					System.out.println("combo 2:");
//					for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//					    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//					}
					possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
					if(possibleMembers!=null && !possibleMembers.isEmpty()) {
						for(Member m: possibleMembers) {
							if(m.getBirthDate().equals(birthDate)) {
								member = m;
								possibleMembers = null;
								memberNameParameters = null;
								return member;
							} else {
								throw new ELSException("member_not_found", "member_not_found");
							}
						}
					} else {
						throw new ELSException("member_not_found", "member_not_found");
					}
				} catch(ELSException eCombo2) {
					if(eCombo2.getParameter()!=null && eCombo2.getParameter().equals("member_not_found")) {
						//Combo 3: middleName + lastName + birthDate
						try {
							memberNameParameters.remove("firstName");
							memberNameParameters.put("middleName", middleName);			
//							System.out.println("combo 3:");
//							for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//							    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//							}
							possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
							if(possibleMembers!=null && !possibleMembers.isEmpty()) {
								for(Member m: possibleMembers) {
									if(m.getBirthDate().equals(birthDate)) {
										member = m;
										possibleMembers = null;
										memberNameParameters = null;
										return member;
									} else {
										throw new ELSException("member_not_found", "member_not_found");
									}
								}
							} else {
								throw new ELSException("member_not_found", "member_not_found");
							}
						} catch(ELSException eCombo3) {
							if(eCombo3.getParameter()!=null && eCombo3.getParameter().equals("member_not_found")) {
								//Combo 4: firstName + middleName + birthDate
								memberNameParameters.remove("lastName");
								memberNameParameters.put("firstName", firstName);
//								System.out.println("combo 4:");
//								for (Map.Entry<String, String> entry : memberNameParameters.entrySet()) {
//								    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
//								}
								possibleMembers = Member.findAllByFieldNames(Member.class, memberNameParameters, "id", ApplicationConstants.ASC, locale);
								if(possibleMembers!=null && !possibleMembers.isEmpty()) {
									for(Member m: possibleMembers) {
										if(m.getBirthDate().equals(birthDate)) {
											member = m;
											possibleMembers = null;
											memberNameParameters = null;
											return member;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//if member is not found, return blank instance
		possibleMembers = null;
		memberNameParameters = null;
		return new Member();
	}
	
	public MemberDetailsForAccountingVO findDetailsForAccounting(final String username, final String locale) {
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT_DDMMMYYYY", "");
		MemberDetailsForAccountingVO memberDetailsForAccountingVO=new MemberDetailsForAccountingVO();
		
		SimpleDateFormat dateFormat=FormaterUtil.getDateFormatter(parameter.getValue(), locale);
		FormaterUtil.getNumberFormatterGrouping(locale);
		FormaterUtil.getNumberFormatterNoGrouping(locale);
		
		Credential credential=Credential.findByFieldName(Credential.class, "username", username, "");
		User memberUser=User.findByFieldName(User.class,"credential",credential, locale.toString());
		Member member;
		try {
			member = Member.findByNameBirthDate(memberUser.getFirstName(), memberUser.getMiddleName(), memberUser.getLastName(), memberUser.getBirthDate(), locale);
		} catch (ELSException e) {
			return memberDetailsForAccountingVO;
		}
		
		if(member.getTitle()==null){
			memberDetailsForAccountingVO.setTitle("-");
		}else{
			memberDetailsForAccountingVO.setTitle(member.getTitle().getName());
		}		
		memberDetailsForAccountingVO.setFirstName(member.getFirstName());
		memberDetailsForAccountingVO.setMiddleName(member.getMiddleName());
		memberDetailsForAccountingVO.setLastName(member.getLastName());
		if(member.getBirthDate()==null){
			memberDetailsForAccountingVO.setBirthDate("-");
		}else{
			memberDetailsForAccountingVO.setBirthDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(member.getBirthDate()), locale));
		}
		if(member.getAlias()==null || member.getAlias().isEmpty()){
			memberDetailsForAccountingVO.setAlias("-");
		}else{
			if(member.getAliasEnabled()) {
				memberDetailsForAccountingVO.setAlias(member.getAlias());
			} else {
				memberDetailsForAccountingVO.setAlias("-");
			}			
		}
		if(member.getGender()!=null){
			memberDetailsForAccountingVO.setGender(member.getGender().getName());
		}else{
			memberDetailsForAccountingVO.setGender("-");
		}
		Constituency constituency=findConstituency(member.getId());
		if(constituency!=null){
			memberDetailsForAccountingVO.setHouseType(constituency.getHouseType().getName());
			memberDetailsForAccountingVO.setConstituency(constituency.getName());
			memberDetailsForAccountingVO.setConstituencyDisplayName(constituency.getDisplayName());
		}else{
			memberDetailsForAccountingVO.setHouseType("-");
			memberDetailsForAccountingVO.setConstituency("-");
			memberDetailsForAccountingVO.setConstituencyDisplayName("-");
		}		
		if(member.getMaritalStatus()!=null){
			memberDetailsForAccountingVO.setMaritalStatus(member.getMaritalStatus().getName());		
		}else{
			memberDetailsForAccountingVO.setMaritalStatus("-");
		}
		//contact information
		Contact contact=member.getContact();
		if(contact==null){
			memberDetailsForAccountingVO.setEmail("-");
			memberDetailsForAccountingVO.setMobile("-");
		}else{
			memberDetailsForAccountingVO.setEmail("-");
			if(contact.getEmail1()!=null&&!contact.getEmail1().isEmpty()){
				memberDetailsForAccountingVO.setEmail(contact.getEmail1());
			}else if(contact.getEmail2()!=null&&!contact.getEmail2().isEmpty()){
				memberDetailsForAccountingVO.setEmail(contact.getEmail2());
			}	
			memberDetailsForAccountingVO.setMobile("-");
			if(contact.getMobile1()!=null&&!contact.getMobile1().isEmpty()){
				memberDetailsForAccountingVO.setMobile(contact.getMobile1());
			}else if(contact.getMobile2()!=null&&!contact.getMobile2().isEmpty()){
				memberDetailsForAccountingVO.setMobile(contact.getMobile2());
			}
		}
		//addresses information
		Address permanentAddress=member.getPermanentAddress();
		if(permanentAddress!=null){
			memberDetailsForAccountingVO.setPermanentAddress(permanentAddress.generateAddressVO());			
		}
		//permanent address
		Address permanentAddress1=member.getPermanentAddress1();
		if(permanentAddress1!=null){
			memberDetailsForAccountingVO.setPermanentAddress1(permanentAddress1.generateAddressVO());			
		}
		//permanent address
		Address permanentAddress2=member.getPermanentAddress2();
		if(permanentAddress2!=null){
			memberDetailsForAccountingVO.setPermanentAddress2(permanentAddress2.generateAddressVO());			
		}
		Address presentAddress=member.getPresentAddress();
		if(presentAddress!=null){
			memberDetailsForAccountingVO.setPresentAddress(presentAddress.generateAddressVO());			
		}
		//present address
		Address presentAddress1=member.getPresentAddress1();
		if(presentAddress1!=null){
			memberDetailsForAccountingVO.setPresentAddress1(presentAddress1.generateAddressVO());			
		}
		//present address
		Address presentAddress2=member.getPresentAddress2();
		if(presentAddress2!=null){
			memberDetailsForAccountingVO.setPresentAddress2(presentAddress2.generateAddressVO());			
		}
		//death date
		if(member.getDeathDate()==null){
			memberDetailsForAccountingVO.setDeathDate("-");
		}else{
			memberDetailsForAccountingVO.setDeathDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(member.getDeathDate()),locale));
		}
		
		return memberDetailsForAccountingVO;
	}
	
	//For grav website
	
	public MemberBiographyVO findBiographyForGrav(final long id, final String strHouseType, final String locale) {
		CustomParameter parameter = CustomParameter.findByName(
				CustomParameter.class, "SERVER_DATEFORMAT_DDMMMYYYY", "");
		SimpleDateFormat dateFormat=FormaterUtil.getDateFormatter(parameter.getValue(), locale);
		NumberFormat formatWithGrouping=FormaterUtil.getNumberFormatterGrouping(locale);
		NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
		Member m=Member.findById(Member.class, id);
		MemberBiographyVO memberBiographyVO=new MemberBiographyVO();
		memberBiographyVO.setId(m.getId());
		
		HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
		
		//the header in the biography page.
		//for the time being setting party flag to "-"
		memberBiographyVO.setPartyFlag("-");
		if(m.getTitle()==null){
			memberBiographyVO.setTitle("-");
		}else{
			memberBiographyVO.setTitle(m.getTitle().getName());
		}
		if(m.getAlias().isEmpty()){
			memberBiographyVO.setAlias("-");
		}else{
			memberBiographyVO.setAlias(m.getAlias());
		}
		memberBiographyVO.setEnableAliasing(m.getAliasEnabled());
		memberBiographyVO.setFirstName(m.getFirstName());
		memberBiographyVO.setMiddleName(m.getMiddleName());
		memberBiographyVO.setLastName(m.getLastName());
		if(m.getPhoto().isEmpty()){
			memberBiographyVO.setPhoto("-");
		}else{
			memberBiographyVO.setPhoto(m.getPhoto());
		}
		Constituency constituency=findConstituency(m.getId());
		if(constituency!=null){
			memberBiographyVO.setConstituency(constituency.getDisplayName());
		}else{
			memberBiographyVO.setConstituency("-");
		}
		Party party=findParty(m.getId());
		if(party!=null){
		memberBiographyVO.setPartyName(party.getName());
		}else{
			memberBiographyVO.setPartyName("-");
		}
		if(m.getGender()!=null){
		memberBiographyVO.setGender(m.getGender().getName());
		}else{
			memberBiographyVO.setGender("-");
		}
		if(m.getMaritalStatus()!=null){
			memberBiographyVO.setMaritalStatus(m.getMaritalStatus().getName());		
		}else{
			memberBiographyVO.setMaritalStatus("-");
		}
		memberBiographyVO.setFatherName("-");
		memberBiographyVO.setMotherName("-");
		memberBiographyVO.setNoOfDaughter("-");
		memberBiographyVO.setNoOfSons("-");
		memberBiographyVO.setNoOfChildren("-");
		memberBiographyVO.setSpouseName("-");
		memberBiographyVO.setSonCount(0);
		memberBiographyVO.setDaughterCount(0);
		if(m.getFamilyMembers()!=null&&!m.getFamilyMembers().isEmpty()){
		//right now we are doing just for marathi.and so we are comparing directly with the ids.
			int noOfSons=0;
			int noOfDaughters=0;
			int noOfChildren=0;            

			for(FamilyMember i:m.getFamilyMembers()){
				if(i.getRelation().getType().equals(ApplicationConstants.WIFE)||i.getRelation().getType().equals(ApplicationConstants.HUSBAND)){
					memberBiographyVO.setSpouseName(i.getName());
					memberBiographyVO.setSpouseRelation(i.getRelation().getName());
				}else if(i.getRelation().getType().equals(ApplicationConstants.SON)){
					noOfSons++;
				}else if(i.getRelation().getType().equals(ApplicationConstants.DAUGHTER)){
					noOfDaughters++;
				}
			}
			if(noOfSons==0){
				memberBiographyVO.setNoOfSons("-");
			}else{
				memberBiographyVO.setNoOfSons(formatWithoutGrouping.format(noOfSons));
				memberBiographyVO.setSonCount(noOfSons);
			}
			if(noOfDaughters==0){
				memberBiographyVO.setNoOfDaughter("-");
			}else{
				memberBiographyVO.setNoOfDaughter(formatWithoutGrouping.format(noOfDaughters));
				memberBiographyVO.setDaughterCount(noOfDaughters);
			}
			noOfChildren=noOfDaughters+noOfSons;
			if(noOfChildren==0){
				memberBiographyVO.setNoOfChildren("-");
			}else{
				memberBiographyVO.setNoOfChildren(formatWithoutGrouping.format(noOfChildren));
			}
		}
		//birth date and birth place
		if(m.getBirthDate()==null){
			memberBiographyVO.setBirthDate("-");
		}else{
			memberBiographyVO.setBirthDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getBirthDate()), locale));
		}
		memberBiographyVO.setPlaceOfBirth(m.getBirthPlace().trim());

		//death date,condolence date and obituary
		if(m.getDeathDate()==null){
			memberBiographyVO.setDeathDate("-");
		}else{
			memberBiographyVO.setDeathDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getDeathDate()),locale));
		}
		if(m.getCondolenceDate()==null){
			memberBiographyVO.setCondolenceDate("-");
		}else{
			memberBiographyVO.setCondolenceDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getCondolenceDate()),locale));
		}
		if(m.getObituary()==null){
			memberBiographyVO.setObituary("-");
		}else{
			memberBiographyVO.setObituary(m.getObituary().trim());
		}
		if(m.getMarriageDate()==null){
			memberBiographyVO.setMarriageDate("-");
		}else{
			memberBiographyVO.setMarriageDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(m.getMarriageDate()),locale));
		}
		//qualifications.this will be separated by line
		List<Qualification> qualifications=m.getQualifications();
		if(qualifications!=null&&!qualifications.isEmpty()){
			StringBuffer buffer=new StringBuffer();
			int count=0;
			for(Qualification i:qualifications){
				if(count>0){
					buffer.append("<br>"+i.getDetails());
				}else{
					if(i.getDetails().isEmpty()){
						if(i.getDegree()!=null){
							buffer.append(i.getDegree().getName());
						}else{
							buffer.append("-");
						}
					}else{
						buffer.append(i.getDetails());
					}
				}
			}
			memberBiographyVO.setEducationalQualification(buffer.toString());
		}else if(qualifications==null){
			memberBiographyVO.setEducationalQualification("-");
		}else if(qualifications.isEmpty()){
			memberBiographyVO.setEducationalQualification("-");
		}
		//languages.This will be comma separated values
		CustomParameter andLocalizedName=CustomParameter.findByName(CustomParameter.class,"AND", locale);
		List<Language> languages=m.getLanguages();
		if(languages!=null&&!languages.isEmpty()){
			Map<Integer,Language> languageMap=new HashMap<Integer, Language>();
			Set<Integer> keys=new TreeSet<Integer>();
			for(Language i:m.getLanguages()){
				languageMap.put(i.getPriority(),i);
				keys.add(i.getPriority());
			}
			List<Language> sortedLanguage=new ArrayList<Language>();
			for(Integer i:keys){
				sortedLanguage.add(languageMap.get(i));
			}
			StringBuffer buffer=new StringBuffer();
			int size=sortedLanguage.size();
			int count=0;
			for(Language i:sortedLanguage){
				count++;
				if(count==size-1){
					buffer.append(i.getName()+" "+andLocalizedName.getValue()+" ");
				}else if(count==size){
					buffer.append(i.getName());
				}else{
					buffer.append(i.getName()+", ");
				}
			}
			//buffer.deleteCharAt(buffer.length()-1);
			memberBiographyVO.setLanguagesKnown(buffer.toString());
		}else if(languages==null){
			memberBiographyVO.setLanguagesKnown("-");
		}else if(languages.isEmpty()){
			memberBiographyVO.setLanguagesKnown("-");
		}
		//profession.this will also be comma separated values
		List<Profession> professions=m.getProfessions();
		if(professions!=null&&!professions.isEmpty()){
			StringBuffer buffer=new StringBuffer();
			int size=professions.size();
			int count=0;
			for(Profession i:professions){
				count++;
				if(count==size-1){
					buffer.append(i.getName()+" "+andLocalizedName.getValue()+" ");
				}else if(count==size){
					buffer.append(i.getName());
				}else{
					buffer.append(i.getName()+", ");
				}
			}
			// buffer.deleteCharAt(buffer.length()-1);
			memberBiographyVO.setProfession(buffer.toString());
		}if(professions==null){
			memberBiographyVO.setProfession("-");
		}else if(professions.isEmpty()){
			memberBiographyVO.setProfession("-");
		}
		//contact info
		Contact contact=m.getContact();
		if(contact==null){
			memberBiographyVO.setEmail("-");
			memberBiographyVO.setWebsite("-");
			memberBiographyVO.setFax1("-");
			memberBiographyVO.setFax2("-");
			memberBiographyVO.setFax3("-");
			memberBiographyVO.setFax4("-");
			memberBiographyVO.setFax5("-");
			memberBiographyVO.setFax6("-");
			memberBiographyVO.setFax7("-");
			memberBiographyVO.setFax8("-");
			memberBiographyVO.setFax9("-");
			memberBiographyVO.setFax10("-");
			memberBiographyVO.setFax11("-");
			memberBiographyVO.setFax12("-");
			memberBiographyVO.setMobile("-");
			memberBiographyVO.setTelephone1("-");
			memberBiographyVO.setTelephone2("-");
			memberBiographyVO.setTelephone3("-");
			memberBiographyVO.setTelephone4("-");
			memberBiographyVO.setTelephone5("-");
			memberBiographyVO.setTelephone6("-");
			memberBiographyVO.setTelephone7("-");
			memberBiographyVO.setTelephone8("-");
			memberBiographyVO.setTelephone9("-");
			memberBiographyVO.setTelephone10("-");
			memberBiographyVO.setTelephone11("-");
			memberBiographyVO.setTelephone12("-");

		}else{
			memberBiographyVO.setEmail("-");
			memberBiographyVO.setWebsite("-");
			memberBiographyVO.setFax1("-");
			memberBiographyVO.setFax2("-");
			memberBiographyVO.setFax3("-");
			memberBiographyVO.setFax4("-");
			memberBiographyVO.setFax5("-");
			memberBiographyVO.setFax6("-");
			memberBiographyVO.setFax7("-");
			memberBiographyVO.setFax8("-");
			memberBiographyVO.setFax9("-");
			memberBiographyVO.setFax10("-");
			memberBiographyVO.setFax11("-");
			memberBiographyVO.setFax12("-");
			memberBiographyVO.setMobile("-");
			memberBiographyVO.setTelephone1("-");
			memberBiographyVO.setTelephone2("-");
			memberBiographyVO.setTelephone3("-");
			memberBiographyVO.setTelephone4("-");
			memberBiographyVO.setTelephone5("-");
			memberBiographyVO.setTelephone6("-");
			memberBiographyVO.setTelephone7("-");
			memberBiographyVO.setTelephone8("-");
			memberBiographyVO.setTelephone9("-");
			memberBiographyVO.setTelephone10("-");
			memberBiographyVO.setTelephone11("-");
			memberBiographyVO.setTelephone12("-");
			if(contact.getEmail1()!=null&&!contact.getEmail1().isEmpty()
					&&contact.getEmail2()!=null&&!contact.getEmail2().isEmpty()){
				memberBiographyVO.setEmail(contact.getEmail1()+"<br>"+contact.getEmail2());
			}else if(contact.getEmail1()!=null&&!contact.getEmail1().isEmpty()){
				memberBiographyVO.setEmail(contact.getEmail1());
			}else if(contact.getEmail2()!=null&&!contact.getEmail2().isEmpty()){
				memberBiographyVO.setEmail(contact.getEmail2());
			}
			if(contact.getWebsite1()!=null&&!contact.getWebsite1().isEmpty()
					&&contact.getWebsite2()!=null&&!contact.getWebsite2().isEmpty()){
				memberBiographyVO.setWebsite(contact.getWebsite1()+"<br>"+contact.getWebsite2());
			}else if(contact.getWebsite1()!=null&&!contact.getWebsite1().isEmpty()){
				memberBiographyVO.setWebsite(contact.getWebsite1());
			}else if(contact.getWebsite2()!=null&&!contact.getWebsite2().isEmpty()){
				memberBiographyVO.setWebsite(contact.getWebsite2());
			}
			if(contact.getMobile1()!=null&&!contact.getMobile1().isEmpty()
					&&contact.getMobile2()!=null&&!contact.getMobile2().isEmpty()){
				memberBiographyVO.setMobile(contact.getMobile1()+"<br>"+contact.getMobile2());
			}else if(contact.getMobile1()!=null&&!contact.getMobile1().isEmpty()){
				memberBiographyVO.setMobile(contact.getMobile1());
			}else if(contact.getMobile2()!=null&&!contact.getMobile2().isEmpty()){
				memberBiographyVO.setMobile(contact.getMobile2());
			}
			if(contact.getFax1()!=null){
				if(!contact.getFax1().trim().isEmpty()){
					memberBiographyVO.setFax1(contact.getFax1().trim());
				}
			}
			if(contact.getFax2()!=null){
				if(!contact.getFax2().trim().isEmpty()){
					memberBiographyVO.setFax2(contact.getFax2().trim());
				}
			}
			if(contact.getFax3()!=null){
				if(!contact.getFax3().trim().isEmpty()){
					memberBiographyVO.setFax3(contact.getFax3().trim());
				}
			}
			if(contact.getFax4()!=null){
				if(!contact.getFax4().trim().isEmpty()){
					memberBiographyVO.setFax4(contact.getFax4().trim());
				}
			}
			if(contact.getFax5()!=null){
				if(!contact.getFax5().trim().isEmpty()){
					memberBiographyVO.setFax5(contact.getFax5().trim());
				}
			}

			if(contact.getFax6()!=null){
				if(!contact.getFax6().trim().isEmpty()){
					memberBiographyVO.setFax6(contact.getFax6().trim());
				}
			}
			if(contact.getFax7()!=null){
				if(!contact.getFax7().trim().isEmpty()){
					memberBiographyVO.setFax7(contact.getFax7().trim());
				}
			}
			if(contact.getFax8()!=null){
				if(!contact.getFax8().trim().isEmpty()){
					memberBiographyVO.setFax8(contact.getFax8().trim());
				}
			}
			if(contact.getFax9()!=null){
				if(!contact.getFax9().trim().isEmpty()){
					memberBiographyVO.setFax9(contact.getFax9().trim());
				}
			}
			if(contact.getFax10()!=null){
				if(!contact.getFax10().trim().isEmpty()){
					memberBiographyVO.setFax10(contact.getFax10().trim());
				}
			}

			if(contact.getFax11()!=null){
				if(!contact.getFax11().trim().isEmpty()){
					memberBiographyVO.setFax11(contact.getFax11().trim());
				}
			}
			if(contact.getFax12()!=null){
				if(!contact.getFax12().trim().isEmpty()){
					memberBiographyVO.setFax12(contact.getFax12().trim());
				}
			}			
			if(contact.getTelephone1()!=null){
				if(!contact.getTelephone1().isEmpty()){
					memberBiographyVO.setTelephone1(contact.getTelephone1());
				}
			}
			if(contact.getTelephone2()!=null){
				if(!contact.getTelephone2().isEmpty()){
					memberBiographyVO.setTelephone2(contact.getTelephone2());
				}
			}
			if(contact.getTelephone3()!=null){
				if(!contact.getTelephone3().isEmpty()){
					memberBiographyVO.setTelephone3(contact.getTelephone3());
				}
			}
			if(contact.getTelephone4()!=null){
				if(!contact.getTelephone4().isEmpty()){
					memberBiographyVO.setTelephone4(contact.getTelephone4());
				}
			}
			if(contact.getTelephone5()!=null){
				if(!contact.getTelephone5().isEmpty()){
					memberBiographyVO.setTelephone5(contact.getTelephone5());
				}
			}

			if(contact.getTelephone6()!=null){
				if(!contact.getTelephone6().isEmpty()){
					memberBiographyVO.setTelephone6(contact.getTelephone6());
				}
			}
			if(contact.getTelephone7()!=null){
				if(!contact.getTelephone7().isEmpty()){
					memberBiographyVO.setTelephone7(contact.getTelephone7());
				}
			}
			if(contact.getTelephone8()!=null){
				if(!contact.getTelephone8().isEmpty()){
					memberBiographyVO.setTelephone8(contact.getTelephone8());
				}
			}
			if(contact.getTelephone9()!=null){
				if(!contact.getTelephone9().isEmpty()){
					memberBiographyVO.setTelephone9(contact.getTelephone9());
				}
			}
			if(contact.getTelephone10()!=null){
				if(!contact.getTelephone10().isEmpty()){
					memberBiographyVO.setTelephone10(contact.getTelephone10());
				}
			}
			if(contact.getTelephone11()!=null){
				if(!contact.getTelephone11().isEmpty()){
					memberBiographyVO.setTelephone11(contact.getTelephone11());
				}
			}
			if(contact.getTelephone12()!=null){
				if(!contact.getTelephone12().isEmpty()){
					memberBiographyVO.setTelephone12(contact.getTelephone12());
				}
			}

		}
		//initialize addresses
		CustomParameter tehsilLocalized=CustomParameter.findByName(CustomParameter.class,"TEHSIL", locale);
		CustomParameter districtLocalized=CustomParameter.findByName(CustomParameter.class,"DISTRICT", locale);
		CustomParameter stateLocalized=CustomParameter.findByName(CustomParameter.class,"STATE", locale);

		memberBiographyVO.setPermanentAddress("-");
		memberBiographyVO.setPermanentAddress1("-");
		memberBiographyVO.setPermanentAddress2("-");
		memberBiographyVO.setPresentAddress("-");
		memberBiographyVO.setPresentAddress1("-");
		memberBiographyVO.setPresentAddress2("-");
		memberBiographyVO.setCorrespondenceAddress("-");
		memberBiographyVO.setOfficeAddress("-");
		memberBiographyVO.setOfficeAddress1("-");
		memberBiographyVO.setOfficeAddress1("-");
		memberBiographyVO.setOfficeAddress2("-");
		memberBiographyVO.setTempAddress1("-");
		memberBiographyVO.setTempAddress2("-");
		//present address
		Address presentAddress=m.getPresentAddress();
		if(presentAddress!=null){
			if(presentAddress.getDetails()!=null){
				if(!presentAddress.getDetails().trim().isEmpty()){
					if(presentAddress.getTehsil()!=null){
						memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+presentAddress.getTehsil().getName()+","+districtLocalized.getValue()+"-"+presentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
					}else{
						memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+districtLocalized.getValue()+"-"+presentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
					}
				}
			}
		}

		//present address
		Address presentAddress1=m.getPresentAddress1();
		if(presentAddress1!=null){
			if(presentAddress1.getDetails()!=null){
				if(!presentAddress1.getDetails().trim().isEmpty()){
					if(presentAddress1.getTehsil()!=null){
						memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+presentAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+presentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
					}else{
						memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+presentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
					}
				}
			}
		}
		//present address
		Address presentAddress2=m.getPresentAddress2();
		if(presentAddress2!=null){
			if(presentAddress2.getDetails()!=null){
				if(!presentAddress2.getDetails().trim().isEmpty()){
					if(presentAddress2.getTehsil()!=null){
						memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+presentAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+presentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
					}else{
						memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+presentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
					}
				}
			}
		}
		//permanent address
		Address permanentAddress=m.getPermanentAddress();
		if(permanentAddress!=null){
			if(permanentAddress.getDetails()!=null){
				if(!permanentAddress.getDetails().trim().isEmpty()) {
					if(permanentAddress.getTehsil()!=null){
						memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+permanentAddress.getTehsil().getName()+","+districtLocalized.getValue()+"-"+permanentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
					}else{
						memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+districtLocalized.getValue()+"-"+permanentAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
					}
				}
			}
		}
		//permanent address
		Address permanentAddress1=m.getPermanentAddress1();
		if(permanentAddress1!=null){
			if(permanentAddress1.getDetails()!=null){
				if(!permanentAddress1.getDetails().trim().isEmpty()) {
					if(permanentAddress1.getTehsil()!=null){
						memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+permanentAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+permanentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
					}else{
						memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+permanentAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
					}
				}
			}
		}
		//permanent address
		Address permanentAddress2=m.getPermanentAddress2();
		if(permanentAddress2!=null){
			if(permanentAddress2.getDetails()!=null){
				if(!permanentAddress2.getDetails().trim().isEmpty()) {
					if(permanentAddress2.getTehsil()!=null){
						memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+permanentAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+permanentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
					}else{
						memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+permanentAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
					}
				}
			}
		}
		//office address
		Address officeAddress=m.getOfficeAddress();
		if(officeAddress!=null){
			if(officeAddress.getDetails()!=null){
				if(!officeAddress.getDetails().trim().isEmpty()){
					if(officeAddress.getTehsil()!=null){
						memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+officeAddress.getTehsil().getName()+","+districtLocalized.getValue()+"-"+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
					}else{
						memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+districtLocalized.getValue()+"-"+officeAddress.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
					}
				}
			}
		}
		//office address
		Address officeAddress1=m.getOfficeAddress1();
		if(officeAddress1!=null){
			if(officeAddress1.getDetails()!=null){
				if(!officeAddress1.getDetails().trim().isEmpty()){
					if(officeAddress1.getTehsil()!=null){
						memberBiographyVO.setOfficeAddress1(officeAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+officeAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+officeAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress1.getState().getName()+" "+officeAddress1.getPincode());
					}else{
						memberBiographyVO.setOfficeAddress1(officeAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+officeAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress1.getState().getName()+" "+officeAddress1.getPincode());
					}
				}
			}
		}
		//office address
		Address officeAddress2=m.getOfficeAddress2();
		if(officeAddress2==null){
			memberBiographyVO.setOfficeAddress2("-");
		}else{
			if(!officeAddress2.getDetails().trim().isEmpty()){
				if(officeAddress2.getTehsil()!=null){
					memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+officeAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+officeAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
				}else{
					memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+officeAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
				}
			}else{
				memberBiographyVO.setOfficeAddress2("-");
			}
		}
		//temp1 address
		Address tempAddress1=m.getTempAddress1();
		if(tempAddress1!=null){
			if(tempAddress1.getDetails()!=null){
				if(!tempAddress1.getDetails().trim().isEmpty()){
					if(tempAddress1.getTehsil()!=null){
						memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+tempAddress1.getTehsil().getName()+","+districtLocalized.getValue()+"-"+tempAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
					}else{
						memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+districtLocalized.getValue()+"-"+tempAddress1.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
					}
				}
			}
		}
		//temp2 address
		Address tempAddress2=m.getTempAddress2();
		if(tempAddress2!=null){
			if(tempAddress2.getDetails()!=null){
				if(tempAddress2.getDetails()!=null){
					if(!tempAddress2.getDetails().trim().isEmpty()){
						if(tempAddress2.getTehsil()!=null){
							memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tehsilLocalized.getValue()+"-"+tempAddress2.getTehsil().getName()+","+districtLocalized.getValue()+"-"+tempAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
						}else{
							memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+districtLocalized.getValue()+"-"+tempAddress2.getDistrict().getName()+","+stateLocalized.getValue()+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
						}
					}else{
						memberBiographyVO.setTempAddress2("-");
					}
				}
			}
		}
		//correspondence address
		Address correspondenceAddress=m.getCorrespondenceAddress();
		if(correspondenceAddress!=null){
			if(correspondenceAddress.getDetails()!=null){
				if(correspondenceAddress.getDetails()!=null){
					if(!correspondenceAddress.getDetails().trim().isEmpty()){
						memberBiographyVO.setCorrespondenceAddress(tempAddress2.getDetails()+"<br>"+tempAddress2.getPincode());
					}else{
						memberBiographyVO.setCorrespondenceAddress("-");
					}
				}
			}
		}
		//other info.
		if(m.getCountriesVisited()!=null&&!m.getCountriesVisited().equals("<p></p>")){
			if(m.getCountriesVisited().trim().isEmpty()){
				memberBiographyVO.setCountriesVisited("-");
			}else{
				memberBiographyVO.setCountriesVisited(m.getCountriesVisited().trim());
			}
		}else{
			memberBiographyVO.setCountriesVisited("-");
		}

		if(m.getEducationalCulturalActivities()!=null&&!m.getEducationalCulturalActivities().equals("<p></p>")){
			if(m.getEducationalCulturalActivities().trim().isEmpty()){
				memberBiographyVO.setEducationalCulAct("-");
			}else{
				memberBiographyVO.setEducationalCulAct(m.getEducationalCulturalActivities().trim());
			}
		}else{
			memberBiographyVO.setEducationalCulAct("-");
		}

		if(m.getFavoritePastimeRecreation()!=null&&!m.getFavoritePastimeRecreation().equals("<p></p>")){
			if(m.getFavoritePastimeRecreation().trim().isEmpty()){
				memberBiographyVO.setPastimeRecreation("-");
			}else{
				memberBiographyVO.setPastimeRecreation(m.getFavoritePastimeRecreation().trim());
			}
		}else{
			memberBiographyVO.setPastimeRecreation("-");
		}

		if(m.getHobbySpecialInterests()!=null&&!m.getHobbySpecialInterests().equals("<p></p>")){
			if(m.getHobbySpecialInterests().trim().isEmpty()){
				memberBiographyVO.setSpecialInterests("-");
			}else{
				memberBiographyVO.setSpecialInterests(m.getHobbySpecialInterests().trim());
			}
		}else{
			memberBiographyVO.setSpecialInterests("-");
		}

		if(m.getLiteraryArtisticScientificAccomplishments()!=null&&!m.getLiteraryArtisticScientificAccomplishments().equals("<p></p>")){
			if(m.getLiteraryArtisticScientificAccomplishments().trim().isEmpty()){
				memberBiographyVO.setLiteraryArtisticScAccomplishment("-");
			}else{
				memberBiographyVO.setLiteraryArtisticScAccomplishment(m.getLiteraryArtisticScientificAccomplishments().trim());
			}
		}else{
			memberBiographyVO.setLiteraryArtisticScAccomplishment("-");
		}

		if(m.getPublications()!=null&&!m.getPublications().equals("<p></p>")){
			if(m.getPublications().trim().isEmpty()){
				memberBiographyVO.setPublications("-");
			}else{
				memberBiographyVO.setPublications(m.getPublications().trim());
			}
		}else{
			memberBiographyVO.setPublications("-");
		}
		if(m.getOtherInformation()!=null&&!m.getOtherInformation().equals("<p></p>")){
			if(m.getOtherInformation().trim().isEmpty()){
				memberBiographyVO.setOtherInfo("-");
			}else{
				memberBiographyVO.setOtherInfo(m.getOtherInformation().trim());
			}
		}else{
			memberBiographyVO.setOtherInfo("-");
		}

		if(m.getSocialCulturalActivities()!=null&&!m.getSocialCulturalActivities().equals("<p></p>")){
			if(m.getSocialCulturalActivities().trim().isEmpty()){
				memberBiographyVO.setSocioCulturalActivities("-");
			}else{
				memberBiographyVO.setSocioCulturalActivities(m.getSocialCulturalActivities().trim());
			}
		}else{
			memberBiographyVO.setSocioCulturalActivities("-");
		}

		if(m.getSportsClubs()!=null&&!m.getSportsClubs().equals("<p></p>")){
			if(m.getSportsClubs().trim().isEmpty()){
				memberBiographyVO.setSportsClubs("-");
			}else{
				memberBiographyVO.setSportsClubs(m.getSportsClubs().trim());
			}
		}else{
			memberBiographyVO.setSportsClubs("-");
		}
		
		//elelction results
		//this will be according to the from and to date of elections.but right now we are taking the
		//first entry
		List<ElectionResult> electionResults=m.getElectionResults();
		if(electionResults.isEmpty()){
			memberBiographyVO.setValidVotes("-");
			memberBiographyVO.setVotesReceived("-");
			memberBiographyVO.setNoOfVoters("-");
			memberBiographyVO.setRivalMembers(new ArrayList<RivalMemberVO>());
		}else{
			if(electionResults.get(0).getTotalValidVotes()!=null){
				//memberBiographyVO.setValidVotes(formatWithGrouping.format(electionResults.get(0).getTotalValidVotes()));
				memberBiographyVO.setValidVotes(FormaterUtil.formatToINS(formatWithGrouping.format(electionResults.get(0).getTotalValidVotes())));;
			}else{
				memberBiographyVO.setValidVotes("-");
			}
			if(electionResults.get(0).getVotesReceived()!=null){
				memberBiographyVO.setVotesReceived(FormaterUtil.formatToINS(formatWithGrouping.format(electionResults.get(0).getVotesReceived())));
			}else{
				memberBiographyVO.setVotesReceived("-");
			}
			if(electionResults.get(0).getNoOfVoters()!=null){
				memberBiographyVO.setNoOfVoters(FormaterUtil.formatToINS(formatWithGrouping.format(electionResults.get(0).getNoOfVoters())));
			}else{
				memberBiographyVO.setNoOfVoters("-");
			}
			if(electionResults.get(0).getElectionResultDate()!=null){
				memberBiographyVO.setElectionResultDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(electionResults.get(0).getElectionResultDate()),locale));
			}else{
				memberBiographyVO.setElectionResultDate("-");
			}
			if(electionResults.get(0).getVotingDate()!=null){
				memberBiographyVO.setVotingDate(FormaterUtil.formatMonthsForLocaleLanguage(dateFormat.format(electionResults.get(0).getVotingDate()),locale));
			}else{
				memberBiographyVO.setVotingDate("-");
			}
			List<RivalMember> rivals=electionResults.get(0).getRivalMembers();
			List<RivalMemberVO> rivalMemberVOs=new ArrayList<RivalMemberVO>();
			if(!rivals.isEmpty()){
				for(RivalMember i:rivals){
					RivalMemberVO rivalMemberVO=new RivalMemberVO();
					rivalMemberVO.setName(i.getName());
					rivalMemberVO.setParty(i.getParty().getName());
					if(i.getVotesReceived()!=null){
						rivalMemberVO.setVotesReceived(FormaterUtil.formatToINS(formatWithGrouping.format(i.getVotesReceived())));
					}else{
						rivalMemberVO.setVotesReceived("-");
					}
					rivalMemberVOs.add(rivalMemberVO);
				}
				memberBiographyVO.setRivalMembers(rivalMemberVOs);
			}else{
				memberBiographyVO.setRivalMembers(rivalMemberVOs);
			}
		}
		
		Session session = null;
		try {
			session = Session.findLatestSession(houseType);
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("memberId", new String[]{m.getId().toString()});
		params.put("locale", new String[]{locale.toString()});
		List report = Query.findReport("LOAD_MINISTER_OF_MEMBER", params);
		if(report != null && !report.isEmpty()){
			Object[] obj = (Object[])report.get(0);
			if(obj[0] != null){
				memberBiographyVO.setMinistries("ministries");
				//model.addAttribute("memberDesignation");
			}else{
				List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(m, session, locale.toString());
				for(MemberRole mr : memberRoles){
					if(mr.getType().equalsIgnoreCase(ApplicationConstants.STATE_MINISTER)
						|| mr.getType().equalsIgnoreCase(ApplicationConstants.SPEAKER)
						|| mr.getType().equalsIgnoreCase(ApplicationConstants.CHAIRMAN)
						|| mr.getType().equalsIgnoreCase(ApplicationConstants.DEPUTY_CHAIRMAN)
						|| mr.getType().equalsIgnoreCase(ApplicationConstants.DEPUTY_SPEAKER)
						|| mr.getType().equalsIgnoreCase(ApplicationConstants.LEADER_OF_OPPOSITION)){
						//model.addAttribute("memberRole",mr.getName());
						memberBiographyVO.setMemberRole(mr.getName());
						break;
					}else{
						//model.addAttribute("memberRole",mr.getName());
						memberBiographyVO.setMemberRole(mr.getName());
					}
				}
			}
		}
	
		//position held is left out right now.
		return memberBiographyVO;
	}

	@SuppressWarnings("unchecked")
	public List<String> findMembersByHouseDates(Long houseTypeId, String fromDate, String toDate, String locale) {
		CustomParameter parameter =
				CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT_HYPHEN", "");
		
		Date frmDate = FormaterUtil.formatStringToDate(fromDate, parameter.getValue());
		Date endDate = FormaterUtil.formatStringToDate(toDate, parameter.getValue());
		
		CustomParameter parameterDB =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		String fromDateSTR = FormaterUtil.formatDateToString(frmDate, parameterDB.getValue());
		String toDateSTR=FormaterUtil.formatDateToString(endDate, parameterDB.getValue());
						
		Map<String, String[]> requestMap=new HashMap<String, String[]>();
		requestMap.put("fromDate", new String[] {fromDateSTR});
		requestMap.put("toDate", new String[] {toDateSTR});
		requestMap.put("houseTypeId", new String[] {houseTypeId.toString()});
		requestMap.put("locale",new String[] {locale});
		requestMap.put("field_select_query", null);
		requestMap.put("field_header_select_query", null);
		
		return Query.findReport("MEMBER_LIST_DATEWISE", requestMap);
		
	}

	public boolean isMemberSuspendedOnDate(Long memberId, Date onDate) {
		String strQuery="SELECT M FROM Member m , MemberSuspension ms WHERE "
				+ " m.id=:memberId AND m.id=ms.member.id "
				+ " AND ms.startDateOfSuspension <= :suspensionStartDate "
				+ " AND ( ms.actualEndDateOfSuspension >= :suspensionEndDate OR ms.actualEndDateOfSuspension IS NULL ) ";
		javax.persistence.Query namedQuery = this.em().createQuery(strQuery);
		namedQuery.setParameter("memberId",memberId);
		namedQuery.setParameter("suspensionStartDate",onDate,TemporalType.DATE);
		namedQuery.setParameter("suspensionEndDate",onDate,TemporalType.DATE);
		
		List<Member> resultList = namedQuery.getResultList();
	    return resultList!=null && resultList.size()>0?true:false;
	}
	
	public List<Long> supspendedMembersIdsList(Date onDate){
		String strQuery="SELECT ms.member.id FROM MemberSuspension ms WHERE "
				+ " ms.startDateOfSuspension <= :suspensionStartDate "
				+ " AND ( ms.actualEndDateOfSuspension >= :suspensionEndDate OR ms.actualEndDateOfSuspension IS NULL ) ";
		javax.persistence.Query namedQuery = this.em().createQuery(strQuery);
		
		namedQuery.setParameter("suspensionStartDate",onDate,TemporalType.DATE);
		namedQuery.setParameter("suspensionEndDate",onDate,TemporalType.DATE);
		
		List<Long> resultList = namedQuery.getResultList();
		return resultList;
	}
}