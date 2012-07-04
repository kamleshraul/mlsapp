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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.NoResultException;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ElectionResultVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberAgeWiseReportVO;
import org.mkcl.els.common.vo.MemberAgeWiseVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberChildrenWiseReportVO;
import org.mkcl.els.common.vo.MemberChildrenWiseVO;
import org.mkcl.els.common.vo.MemberCompleteDetailVO;
import org.mkcl.els.common.vo.MemberGeneralVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.MemberPartyDistrictWiseVO;
import org.mkcl.els.common.vo.MemberPartyWiseReportVO;
import org.mkcl.els.common.vo.MemberPartyWiseVO;
import org.mkcl.els.common.vo.MemberProfessionWiseReportVO;
import org.mkcl.els.common.vo.MemberProfessionWiseVO;
import org.mkcl.els.common.vo.MemberQualificationWiseReportVO;
import org.mkcl.els.common.vo.MemberQualificationWiseVO;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.common.vo.PositionHeldVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RivalMemberVO;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.FamilyMember;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.PositionHeld;
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.RivalMember;
import org.mkcl.els.domain.associations.MemberPartyAssociation;
import org.springframework.stereotype.Repository;

/**
 * The Class MemberRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MemberRepository extends BaseRepository<Member, Long>{

    /**
     * Search.
     *
     * @param housetype the housetype
     * @param house
     * @param criteria1 the criteria1
     * @param criteria2 the criteria2
     * @param locale the locale
     * @return the member search page
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MemberSearchPage search(final String housetype, final Long house, final String criteria1,
            final Long criteria2, final String locale,final String[] councilCriteria) {
        //selectClause denotes what items we want to read from the db for each member.
    	//in case of lowerhouse we want to read district name but in case of upperhouse we don't want
        String selectClause=null;
        //similarly in case of lower house we will have joins to get districts but not in case of upperhouse
        String fromClause=null;
        //in both houses we select members belonging to a particular house,having role as member and locale as 
        //locale.
        String whereClause=null;
        if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
        	selectClause="SELECT rs.title,rs.id,rs.firstname,rs.middlename,rs.lastname,rs.constituency,rs.partyname,rs.recordindex,rs.fromdate,rs.todate,rs.gender,rs.maritalstatus,rs.birthdate,rs.district FROM(" +
			"SELECT t.name as title,m.id as id,m.first_name as firstname,m.middle_name as middlename,m.last_name as lastname,c.display_name as constituency,p.name as partyname,mhr.record_index as recordindex,mp.from_date as fromdate,mp.to_date as todate,g.name as gender,ms.name as maritalstatus,m.birth_date as birthdate,d.name as district ";
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
        	whereClause=" WHERE m.locale='"+locale+"' and mr.priority=0 and mhr.house_id="+house+ " ";            
        }else{
        	selectClause="SELECT rs.title,rs.id,rs.firstname,rs.middlename,rs.lastname,rs.constituency,rs.partyname,rs.recordindex,rs.fromdate,rs.todate,rs.gender,rs.maritalstatus,rs.birthdate FROM(" +
        			"SELECT t.name as title,m.id as id,m.first_name as firstname,m.middle_name as middlename,m.last_name as lastname,c.display_name as constituency,p.name as partyname,mhr.record_index as recordindex,mp.from_date as fromdate,mp.to_date as todate,g.name as gender,ms.name as maritalstatus,m.birth_date as birthdate ";
        	fromClause="FROM members AS m "+
    		"LEFT JOIN  members_houses_roles AS mhr ON (mhr.member=m.id) "+
    		"LEFT JOIN members_parties AS mp ON(mp.member=m.id) "+
    		"LEFT JOIN constituencies AS c ON(c.id=mhr.constituency_id) "+
    		"LEFT JOIN parties AS p ON(p.id=mp.party) "+
    		"LEFT JOIN titles AS t ON(t.id=m.title_id) "+
    		"LEFT JOIN memberroles AS mr ON (mr.id=mhr.role) "+
    		"LEFT JOIN genders AS g ON(g.id=m.gender_id) "+
    		"LEFT JOIN maritalstatus AS ms ON(ms.id=m.maritalstatus_id) ";
        	whereClause=" WHERE m.locale='"+locale+"' and mr.priority=0 and mhr.house_id="+house+ " ";
        }      
        //search criterias as selected by user.
        String queryCriteriaClause=null;
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
        //oder by lastname
        String queryOrderByClause=null;
        if(criteria1.equals("birth_date")){
        queryOrderByClause=" ORDER BY day(m.birth_date) asc";
        }else{
        queryOrderByClause=" ORDER BY m.last_name asc";
        }
        String query=null;
        if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
        	query=selectClause+fromClause+whereClause+queryCriteriaClause+queryOrderByClause+") as rs";	
        }else{
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
            String upperHousePartyQuery=null;
        	if(criteria.equals("RANGE")){
        		upperHousePartyQuery=" AND ((mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"') "+
				 " OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"') "+
				 " OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.from_date<='"+toDateDBFormat+"') "+
				 " OR (mp.to_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"')) ";
            }else if(criteria.equals("YEAR")){
            	upperHousePartyQuery=" AND ((mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"') "+
            						 " OR (mp.from_date>='"+fromDateDBFormat+"' AND mp.from_date<='"+toDateDBFormat+"') "+
            						 " OR (mp.to_date>='"+fromDateDBFormat+"' AND mp.to_date<='"+toDateDBFormat+"')) ";
            							
            }else if(criteria.equals("DATE")){
            	upperHousePartyQuery=" AND mp.from_date<='"+fromDateDBFormat+"' AND mp.to_date>='"+toDateDBFormat+"' ";
            }else{
            	upperHousePartyQuery="";
            }
        	String upperHouseConstituencyQuery=null;
        	if(criteria.equals("RANGE")){
        		upperHouseConstituencyQuery=" AND ((mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"') "+
				 " OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"') "+
				 " OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.from_date<='"+toDateDBFormat+"') "+
				 " OR (mhr.to_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"')) ";
            }else if(criteria.equals("YEAR")){
            	upperHouseConstituencyQuery=" AND ((mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"') "+
				 " OR (mhr.from_date>='"+fromDateDBFormat+"' AND mhr.from_date<='"+toDateDBFormat+"') "+
				 " OR (mhr.to_date>='"+fromDateDBFormat+"' AND mhr.to_date<='"+toDateDBFormat+"')) ";
            }else if(criteria.equals("DATE")){
            	upperHouseConstituencyQuery=" AND mhr.from_date<='"+fromDateDBFormat+"' AND mhr.to_date>='"+toDateDBFormat+"'";
            }else{
            	upperHouseConstituencyQuery="";
            }
        	query=selectClause+fromClause+whereClause+queryCriteriaClause+upperHousePartyQuery+upperHouseConstituencyQuery+queryOrderByClause+") as rs";
        }        
        List records=this.em().createNativeQuery(query).getResultList();
        Long currentId=new Long(0);
        int size=0;
        MemberSearchPage memberSearchPage=new MemberSearchPage();
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
            if(locale.equals("mr_IN")){
            memberInfo.setBirthDate(FormaterUtil.formatMonthsMarathi(serverFormat, locale));
            }else{
            memberInfo.setBirthDate(serverFormat);
            }
            }
        	if(housetype.equals(ApplicationConstants.LOWER_HOUSE)){
        		memberInfo.setDistrict(o[13]!=null?o[13].toString():"-");
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
                memberInfos.add(memberInfo); 
            }       
            size=memberInfos.size();
        }
        memberSearchPage.setPageItems(memberInfos);
        memberSearchPage.setTotalRecords(records.size());
        return memberSearchPage;
    }

    /**
     * Find biography.
     *
     * @param id the id
     * @param locale the locale
     * @param data 
     * @return the member biography vo
     */
    public MemberBiographyVO findBiography(final long id, final String locale,final String[] data) {
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
        memberBiographyVO.setConstituency(data[0]);
        memberBiographyVO.setPartyName(data[1]);
        memberBiographyVO.setGender(data[2]);
        memberBiographyVO.setMaritalStatus(data[3]);        
        //the member biography fields in the order it appears in use case.
        //family details
        memberBiographyVO.setFatherName("-");
        memberBiographyVO.setMotherName("-");
        memberBiographyVO.setNoOfDaughter("-");
        memberBiographyVO.setNoOfSons("-");
        memberBiographyVO.setNoOfChildren("-");
        memberBiographyVO.setSpouseName("-");
        memberBiographyVO.setSonCount(0);
        memberBiographyVO.setDaughterCount(0);
        if(m.getFamilyMembers().isEmpty()){
        }else{
            //right now we are doing just for marathi.and so we are comparing directly with the ids.
            int noOfSons=0;
            int noOfDaughters=0;
            int noOfChildren=0;

            for(FamilyMember i:m.getFamilyMembers()){
                if(i.getRelation().getName().equals(ApplicationConstants.en_US_WIFE)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_WIFE)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_HUSBAND)||i.getRelation().getName().equals(ApplicationConstants.en_US_HUSBAND)){
                    memberBiographyVO.setSpouseName(i.getName());
                    memberBiographyVO.setSpouseRelation(i.getRelation().getName());
                }else if(i.getRelation().getName().equals(ApplicationConstants.en_US_SON)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_SON)){
                    noOfSons++;
                }else if(i.getRelation().getName().equals(ApplicationConstants.en_US_DAUGHTER)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_DAUGHTER)){
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
            memberBiographyVO.setBirthDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getBirthDate()), locale));
        }
        memberBiographyVO.setPlaceOfBirth(m.getBirthPlace().trim());

        //death date,condolence date and obituary
        if(m.getDeathDate()==null){
            memberBiographyVO.setDeathDate("-");
        }else{
            memberBiographyVO.setDeathDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getDeathDate()),locale));
        }
        if(m.getCondolenceDate()==null){
            memberBiographyVO.setCondolenceDate("-");
        }else{
            memberBiographyVO.setCondolenceDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getCondolenceDate()),locale));
        }
        if(m.getObituary()==null){
            memberBiographyVO.setObituary("-");
        }else{
            memberBiographyVO.setObituary(m.getObituary().trim());
        }        
        if(m.getMarriageDate()==null){
            memberBiographyVO.setMarriageDate("-");
        }else{
            memberBiographyVO.setMarriageDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getMarriageDate()),locale));
        }
        //qualifications.this will be separated by line
        List<Qualification> qualifications=m.getQualifications();
        if(qualifications.isEmpty()){
            memberBiographyVO.setEducationalQualification("-");
        }else{
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
        }
        //languages.This will be comma separated values
        List<Language> languages=m.getLanguages();
        if(languages.isEmpty()){
            memberBiographyVO.setLanguagesKnown("-");
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
                    buffer.append(i.getName()+" "+ApplicationConstants.AND_mr_IN+" ");
                }else if(count==size){
                    buffer.append(i.getName());
                }else{
                    buffer.append(i.getName()+", ");
                }
            }
            //buffer.deleteCharAt(buffer.length()-1);
            memberBiographyVO.setLanguagesKnown(buffer.toString());
        }
        //profession.this will also be comma separated values
        List<Profession> professions=m.getProfessions();
        if(m.getProfessions().isEmpty()){
            memberBiographyVO.setProfession("-");
        }else{
            StringBuffer buffer=new StringBuffer();
            int size=professions.size();
            int count=0;
            for(Profession i:professions){
                count++;
                if(count==size-1){
                    buffer.append(i.getName()+" "+ApplicationConstants.AND_mr_IN+" ");
                }else if(count==size){
                    buffer.append(i.getName());
                }else{
                    buffer.append(i.getName()+", ");
                }
            }
            // buffer.deleteCharAt(buffer.length()-1);
            memberBiographyVO.setProfession(buffer.toString());
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
            memberBiographyVO.setEmail(contact.getEmail1()+"<br>"+contact.getEmail2());
            memberBiographyVO.setWebsite(contact.getWebsite1()+"<br>"+contact.getWebsite2());
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
            if(contact.getFax12()!=null){
                if(!contact.getFax12().isEmpty()){
                    memberBiographyVO.setFax12(contact.getFax12());
                }
            }

            memberBiographyVO.setMobile(contact.getMobile1()+"<br>"+contact.getMobile2());
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
                        memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getTehsil().getName()+","+presentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
                    }else{
                        memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
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
                        memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+presentAddress1.getTehsil().getName()+","+presentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
                    }else{
                        memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+presentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
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
                        memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+presentAddress2.getTehsil().getName()+","+presentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
                    }else{
                        memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+presentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
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
                        memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getTehsil().getName()+","+permanentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
                    }else{
                        memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
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
                        memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+permanentAddress1.getTehsil().getName()+","+permanentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
                    }else{
                        memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+permanentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
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
                        memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+permanentAddress2.getTehsil().getName()+","+permanentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
                    }else{
                        memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+permanentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
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
                        memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
                    }else{
                        memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
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
                        memberBiographyVO.setOfficeAddress1(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
                    }else{
                        memberBiographyVO.setOfficeAddress1(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
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
                    memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+officeAddress2.getTehsil().getName()+","+officeAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
                }else{
                    memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+officeAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
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
                        memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tempAddress1.getTehsil().getName()+","+tempAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
                    }else{
                        memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tempAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
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
                            memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tempAddress2.getTehsil().getName()+","+tempAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
                        }else{
                            memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tempAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
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
        if(m.getCountriesVisited()!=null){
            if(m.getCountriesVisited().trim().isEmpty()){
                memberBiographyVO.setCountriesVisited("-");
            }else{
                memberBiographyVO.setCountriesVisited(m.getCountriesVisited().trim());
            }
        }else{
            memberBiographyVO.setCountriesVisited("-");
        }

        if(m.getEducationalCulturalActivities()!=null){
            if(m.getEducationalCulturalActivities().trim().isEmpty()){
                memberBiographyVO.setEducationalCulAct("-");
            }else{
                memberBiographyVO.setEducationalCulAct(m.getEducationalCulturalActivities().trim());
            }
        }else{
            memberBiographyVO.setEducationalCulAct("-");
        }

        if(m.getFavoritePastimeRecreation()!=null){
            if(m.getFavoritePastimeRecreation().trim().isEmpty()){
                memberBiographyVO.setPastimeRecreation("-");
            }else{
                memberBiographyVO.setPastimeRecreation(m.getFavoritePastimeRecreation().trim());
            }
        }else{
            memberBiographyVO.setPastimeRecreation("-");
        }

        if(m.getHobbySpecialInterests()!=null){
            if(m.getHobbySpecialInterests().trim().isEmpty()){
                memberBiographyVO.setSpecialInterests("-");
            }else{
                memberBiographyVO.setSpecialInterests(m.getHobbySpecialInterests().trim());
            }
        }else{
            memberBiographyVO.setSpecialInterests("-");
        }

        if(m.getLiteraryArtisticScientificAccomplishments()!=null){
            if(m.getLiteraryArtisticScientificAccomplishments().trim().isEmpty()){
                memberBiographyVO.setLiteraryArtisticScAccomplishment("-");
            }else{
                memberBiographyVO.setLiteraryArtisticScAccomplishment(m.getLiteraryArtisticScientificAccomplishments().trim());
            }
        }else{
            memberBiographyVO.setLiteraryArtisticScAccomplishment("-");
        }

        if(m.getPublications()!=null){
            if(m.getPublications().trim().isEmpty()){
                memberBiographyVO.setPublications("-");
            }else{
                memberBiographyVO.setPublications(m.getPublications().trim());
            }
        }else{
            memberBiographyVO.setPublications("-");
        }

        if(m.getOtherInformation()!=null){
            if(m.getOtherInformation().trim().isEmpty()){
                memberBiographyVO.setOtherInfo("-");
            }else{
                memberBiographyVO.setOtherInfo(m.getOtherInformation().trim());
            }
        }else{
            memberBiographyVO.setOtherInfo("-");
        }

        if(m.getSocialCulturalActivities()!=null){
            if(m.getSocialCulturalActivities().trim().isEmpty()){
                memberBiographyVO.setSocioCulturalActivities("-");
            }else{
                memberBiographyVO.setSocioCulturalActivities(m.getSocialCulturalActivities().trim());
            }
        }else{
            memberBiographyVO.setSocioCulturalActivities("-");
        }

        if(m.getSportsClubs()!=null){
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
            memberBiographyVO.setBirthDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getBirthDate()), locale));
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
                if(i.getRelation().getName().equals(ApplicationConstants.en_US_WIFE)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_WIFE)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_HUSBAND)||i.getRelation().getName().equals(ApplicationConstants.en_US_HUSBAND)){
                    memberBiographyVO.setSpouse(i.getName());
                    memberBiographyVO.setSpouseRelation(i.getRelation().getName());
                }else if(i.getRelation().getName().equals(ApplicationConstants.en_US_SON)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_SON)){
                    noOfSons++;
                }else if(i.getRelation().getName().equals(ApplicationConstants.en_US_DAUGHTER)||i.getRelation().getName().equals(ApplicationConstants.mr_IN_DAUGHTER)){
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
                    buffer.append(i.getName()+" "+ApplicationConstants.AND_mr_IN+" ");
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
                    buffer.append(i.getName()+" "+ApplicationConstants.AND_mr_IN+" ");
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
            memberBiographyVO.setDeathDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getDeathDate()),locale));
        }
        if(m.getCondolenceDate()==null){
            memberBiographyVO.setCondolenceDate("-");
        }else{
            memberBiographyVO.setCondolenceDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(m.getCondolenceDate()),locale));
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
                    positionHeldVO.setToDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(i.getToDate()),locale));
                }
                if(i.getFromDate()==null){
                    positionHeldVO.setFromDate("-");
                }else{
                    positionHeldVO.setFromDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(i.getFromDate()),locale));
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
        //present address
        Address presentAddress=m.getPresentAddress();
        if(presentAddress==null){
            memberBiographyVO.setPresentAddress("-");
        }else{
            if(!presentAddress.getDetails().trim().isEmpty()){
                if(presentAddress.getTehsil()!=null){
                    memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getTehsil().getName()+","+presentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
                }else{
                    memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress.getState().getName()+" "+presentAddress.getPincode());
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
                    memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+presentAddress1.getTehsil().getName()+","+presentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
                }else{
                    memberBiographyVO.setPresentAddress1(presentAddress1.getDetails()+"<br>"+presentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress1.getState().getName()+" "+presentAddress1.getPincode());
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
                    memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+presentAddress2.getTehsil().getName()+","+presentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
                }else{
                    memberBiographyVO.setPresentAddress2(presentAddress2.getDetails()+"<br>"+presentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+presentAddress2.getState().getName()+" "+presentAddress2.getPincode());
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
                    memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getTehsil().getName()+","+permanentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
                }else{
                    memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress.getState().getName()+" "+permanentAddress.getPincode());
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
                    memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+permanentAddress1.getTehsil().getName()+","+permanentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
                }else{
                    memberBiographyVO.setPermanentAddress1(permanentAddress1.getDetails()+"<br>"+permanentAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress1.getState().getName()+" "+permanentAddress1.getPincode());
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
                    memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+permanentAddress2.getTehsil().getName()+","+permanentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
                }else{
                    memberBiographyVO.setPermanentAddress2(permanentAddress2.getDetails()+"<br>"+permanentAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+permanentAddress2.getState().getName()+" "+permanentAddress2.getPincode());
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
                    memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
                }else{
                    memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
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
                    memberBiographyVO.setOfficeAddress1(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
                }else{
                    memberBiographyVO.setOfficeAddress1(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress.getState().getName()+" "+officeAddress.getPincode());
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
                    memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+officeAddress2.getTehsil().getName()+","+officeAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
                }else{
                    memberBiographyVO.setOfficeAddress2(officeAddress2.getDetails()+"<br>"+officeAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+officeAddress2.getState().getName()+" "+officeAddress2.getPincode());
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
                    memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tempAddress1.getTehsil().getName()+","+tempAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
                }else{
                    memberBiographyVO.setTempAddress1(tempAddress1.getDetails()+"<br>"+tempAddress1.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress1.getState().getName()+" "+tempAddress1.getPincode());
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
                    memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tempAddress2.getTehsil().getName()+","+tempAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
                }else{
                    memberBiographyVO.setTempAddress2(tempAddress2.getDetails()+"<br>"+tempAddress2.getDistrict().getName()+","+ApplicationConstants.STATE_mr_IN+"-"+tempAddress2.getState().getName()+" "+tempAddress2.getPincode());
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
                    electionResultVO.setElectionResultDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(i.getElectionResultDate()),locale));
                }
                if(i.getVotingDate()!=null){
                    electionResultVO.setVotingDate(FormaterUtil.formatMonthsMarathi(dateFormat.format(i.getVotingDate()),locale));
                }
                Constituency constituency=i.getConstituency();
                if(i.getConstituency()!=null){
                    if(constituency!=null){
                        if(!constituency.getDistricts().isEmpty()){
                            if(constituency.getNumber()!=null){
                                if(!constituency.getNumber().isEmpty()){
                                    if(constituency.getIsReserved()){
                                        memberBiographyVO.setConstituency(formatWithoutGrouping.format(Long.parseLong(constituency.getNumber().trim()))+"-"+constituency.getName()+"("+constituency.getReservedFor().getName()+"), "+ApplicationConstants.DISTRICT_mr_IN+"-"+constituency.getDistricts().get(0).getName()+" ");
                                    }else{
                                        memberBiographyVO.setConstituency(formatWithoutGrouping.format(Long.parseLong(constituency.getNumber().trim()))+"-"+constituency.getName()+", "+ApplicationConstants.DISTRICT_mr_IN+"-"+constituency.getDistricts().get(0).getName()+" ");
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

    @SuppressWarnings("rawtypes")
    public MemberAgeWiseReportVO findMembersByAge(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_AGE_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberAgeWiseVO> memberAgeWiseVOs=new ArrayList<MemberAgeWiseVO>();

        int totalMale=0;
        int totalFemale=0;
        int birthdateNotFoundMale=0;
        int birthdateNotFoundFemale=0;

        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberAgeWiseVO memberAgeWiseVO=new MemberAgeWiseVO();
            memberAgeWiseVO.setAgeGroup(o[0].toString());
            memberAgeWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
            memberAgeWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
            memberAgeWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
            memberAgeWiseVOs.add(memberAgeWiseVO);
            totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
            totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
            //here the string to compare is locale based and as such it is stored as staic final constants
            //in application locale class to avoid hard coding it in source.
            if(o[0].toString().equals(ApplicationConstants.mr_IN_INFONOTFOUND)){
                birthdateNotFoundMale=Integer.parseInt(o[2].toString().trim());
                birthdateNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }else if(o[0].toString().equals(ApplicationConstants.en_US_INFONOTFOUND)){
                birthdateNotFoundMale=Integer.parseInt(o[2].toString().trim());
                birthdateNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }
        }
        MemberAgeWiseReportVO memberAgeWiseReportVO=new MemberAgeWiseReportVO();
        memberAgeWiseReportVO.setMemberAgeWiseVOs(memberAgeWiseVOs);
        memberAgeWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
        memberAgeWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-birthdateNotFoundFemale)));
        memberAgeWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
        memberAgeWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-birthdateNotFoundMale)));
        memberAgeWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(birthdateNotFoundMale)));
        memberAgeWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(birthdateNotFoundFemale)));
        memberAgeWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-birthdateNotFoundFemale+totalMale-birthdateNotFoundMale)));
        memberAgeWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(birthdateNotFoundMale+birthdateNotFoundFemale)));
        memberAgeWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
        return memberAgeWiseReportVO;
    }


    @SuppressWarnings("rawtypes")
    public MemberQualificationWiseReportVO findMembersByQualification(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_QUALIFICATION_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberQualificationWiseVO> memberQualificationWiseVOs=new ArrayList<MemberQualificationWiseVO>();

        int totalMale=0;
        int totalFemale=0;
        int qualificationNotFoundMale=0;
        int qualificationNotFoundFemale=0;

        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberQualificationWiseVO memberQualificationWiseVO=new MemberQualificationWiseVO();
            memberQualificationWiseVO.setQualification(o[0].toString());
            memberQualificationWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
            memberQualificationWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
            memberQualificationWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
            memberQualificationWiseVOs.add(memberQualificationWiseVO);
            totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
            totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
            //here the string to compare is locale based and as such it is stored as staic final constants
            //in application locale class to avoid hard coding it in source.
            if(o[0].toString().equals(ApplicationConstants.mr_IN_INFONOTFOUND)){
                qualificationNotFoundMale=Integer.parseInt(o[2].toString().trim());
                qualificationNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }else if(o[0].toString().equals(ApplicationConstants.en_US_INFONOTFOUND)){
                qualificationNotFoundMale=Integer.parseInt(o[2].toString().trim());
                qualificationNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }
        }
        MemberQualificationWiseReportVO memberQualificationWiseReportVO = new MemberQualificationWiseReportVO();
        memberQualificationWiseReportVO.setMemberQualificationWiseVOs(memberQualificationWiseVOs);
        memberQualificationWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
        memberQualificationWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-qualificationNotFoundFemale)));
        memberQualificationWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
        memberQualificationWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-qualificationNotFoundMale)));
        memberQualificationWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(qualificationNotFoundMale)));
        memberQualificationWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(qualificationNotFoundFemale)));
        memberQualificationWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-qualificationNotFoundFemale+totalMale-qualificationNotFoundMale)));
        memberQualificationWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(qualificationNotFoundMale+qualificationNotFoundFemale)));
        memberQualificationWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
        return memberQualificationWiseReportVO;
    }

    @SuppressWarnings("rawtypes")
    public MemberProfessionWiseReportVO findMembersByProfession(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_PROFESSION_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberProfessionWiseVO> memberProfessionWiseVOs=new ArrayList<MemberProfessionWiseVO>();

        int totalAvMale=0;
        int totalAvFemale=0;
        int professionNotFoundMale=0;
        int professionNotFoundFemale=0;


        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberProfessionWiseVO memberProfessionWiseVO=new MemberProfessionWiseVO();
            memberProfessionWiseVO.setProfession(o[0].toString());
            memberProfessionWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
            memberProfessionWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
            memberProfessionWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
            memberProfessionWiseVOs.add(memberProfessionWiseVO);
            //totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
            //totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
            //here the string to compare is locale based and as such it is stored as staic final constants
            //in application locale class to avoid hard coding it in source.
            if(o[0].toString().equals(ApplicationConstants.mr_IN_INFONOTFOUND)){
                professionNotFoundMale=Integer.parseInt(o[2].toString().trim());
                professionNotFoundFemale=Integer.parseInt(o[3].toString().trim());
                //this is the case when records is info not found as in db query it is specified as 0
                memberProfessionWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())+Long.parseLong(o[3].toString().trim())));
            }else if(o[0].toString().equals(ApplicationConstants.en_US_INFONOTFOUND)){
                professionNotFoundMale=Integer.parseInt(o[2].toString().trim());
                professionNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }
            if(o[0].toString().equals(ApplicationConstants.mr_IN_INFOFOUND)){
                totalAvMale=Integer.parseInt(o[2].toString().trim());
                totalAvFemale=Integer.parseInt(o[3].toString().trim());
                //this is the case when records is info not found as in db query it is specified as 0
                memberProfessionWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())+Long.parseLong(o[3].toString().trim())));
            }else if(o[0].toString().equals(ApplicationConstants.en_US_INFOFOUND)){
                totalAvMale=Integer.parseInt(o[2].toString().trim());
                totalAvFemale=Integer.parseInt(o[3].toString().trim());
            }
        }
        MemberProfessionWiseReportVO memberProfessionWiseReportVO = new MemberProfessionWiseReportVO();
        memberProfessionWiseReportVO.setMemberProfessionWiseVOs(memberProfessionWiseVOs);
        memberProfessionWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalAvFemale+professionNotFoundFemale)));
        memberProfessionWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalAvMale+professionNotFoundMale)));
        memberProfessionWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalAvFemale+professionNotFoundFemale+totalAvMale+professionNotFoundMale)));
        return memberProfessionWiseReportVO;
    }

    @SuppressWarnings("rawtypes")
    public MemberChildrenWiseReportVO findMembersByChildren(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_CHILDREN_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberChildrenWiseVO> memberChildrenWiseVOs=new ArrayList<MemberChildrenWiseVO>();

        int totalMale=0;
        int totalFemale=0;
        int childrenNotFoundMale=0;
        int childrenNotFoundFemale=0;

        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberChildrenWiseVO memberChildrenWiseVO=new MemberChildrenWiseVO();
            memberChildrenWiseVO.setChildren(o[0].toString());
            memberChildrenWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
            memberChildrenWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
            memberChildrenWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
            memberChildrenWiseVOs.add(memberChildrenWiseVO);
            totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
            totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
            //here the string to compare is locale based and as such it is stored as staic final constants
            //in application locale class to avoid hard coding it in source.
            if(o[0].toString().equals(ApplicationConstants.mr_IN_INFONOTFOUND)){
                childrenNotFoundMale=Integer.parseInt(o[2].toString().trim());
                childrenNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }else if(o[0].toString().equals(ApplicationConstants.en_US_INFONOTFOUND)){
                childrenNotFoundMale=Integer.parseInt(o[2].toString().trim());
                childrenNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }
        }
        MemberChildrenWiseReportVO memberChildrenWiseReportVO = new MemberChildrenWiseReportVO();
        memberChildrenWiseReportVO.setMemberChildrenWiseVOs(memberChildrenWiseVOs);
        memberChildrenWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
        memberChildrenWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-childrenNotFoundFemale)));
        memberChildrenWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
        memberChildrenWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-childrenNotFoundMale)));
        memberChildrenWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(childrenNotFoundMale)));
        memberChildrenWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(childrenNotFoundFemale)));
        memberChildrenWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-childrenNotFoundFemale+totalMale-childrenNotFoundMale)));
        memberChildrenWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(childrenNotFoundMale+childrenNotFoundFemale)));
        memberChildrenWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
        return memberChildrenWiseReportVO;
    }

    public MemberPartyWiseReportVO findMembersByParty(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query = Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_PARTY_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberPartyWiseVO> memberPartyWiseVOs=new ArrayList<MemberPartyWiseVO>();

        int totalMale=0;
        int totalFemale=0;
        int partyNotFoundMale=0;
        int partyNotFoundFemale=0;

        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberPartyWiseVO memberPartyWiseVO=new MemberPartyWiseVO();
            memberPartyWiseVO.setParty(o[0].toString());
            memberPartyWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
            memberPartyWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
            memberPartyWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
            memberPartyWiseVOs.add(memberPartyWiseVO);
            totalMale=totalMale+Integer.parseInt(o[2].toString().trim());
            totalFemale=totalFemale+Integer.parseInt(o[3].toString().trim());
            //here the string to compare is locale based and as such it is stored as staic final constants
            //in application locale class to avoid hard coding it in source.
            if(o[0].toString().equals(ApplicationConstants.mr_IN_INFONOTFOUND)){
                partyNotFoundMale=Integer.parseInt(o[2].toString().trim());
                partyNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }else if(o[0].toString().equals(ApplicationConstants.en_US_INFONOTFOUND)){
                partyNotFoundMale=Integer.parseInt(o[2].toString().trim());
                partyNotFoundFemale=Integer.parseInt(o[3].toString().trim());
            }
        }
        MemberPartyWiseReportVO memberPartyWiseReportVO = new MemberPartyWiseReportVO();
        memberPartyWiseReportVO.setMemberPartyWiseVOs(memberPartyWiseVOs);
        memberPartyWiseReportVO.setTotalFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale)));
        memberPartyWiseReportVO.setTotalAvFemaleMemberCount(formatWithoutGrouping.format(new Long(totalFemale-partyNotFoundFemale)));
        memberPartyWiseReportVO.setTotalMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale)));
        memberPartyWiseReportVO.setTotalAvMaleMemberCount(formatWithoutGrouping.format(new Long(totalMale-partyNotFoundMale)));
        memberPartyWiseReportVO.setMaleRecNotFound(formatWithoutGrouping.format(new Long(partyNotFoundMale)));
        memberPartyWiseReportVO.setFemaleRecNotFound(formatWithoutGrouping.format(new Long(partyNotFoundFemale)));
        memberPartyWiseReportVO.setInfoFoundFor(formatWithoutGrouping.format(new Long(totalFemale-partyNotFoundFemale+totalMale-partyNotFoundMale)));
        memberPartyWiseReportVO.setInfoNotFoundFor(formatWithoutGrouping.format(new Long(partyNotFoundMale+partyNotFoundFemale)));
        memberPartyWiseReportVO.setGrossTotal(formatWithoutGrouping.format(new Long(totalFemale+totalMale)));
        return memberPartyWiseReportVO;
    }

    public List<MemberPartyDistrictWiseVO> findMembersByPartyDistrict(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        //creating query to execute
        List<District> districts=District.findAll(District.class,"name", ApplicationConstants.ASC,"mr_IN");
        StringBuffer buffer=new StringBuffer();
        buffer.append("SELECT "+
                "p.name AS group_party_wise,"+
        "COUNT(p.name) AS totalmembers_in_each_group,");
        /**********************LOCALE_BASED_CODE************************************************
         * here code will be locale dependent and hence needs to be changed on adding new locale.
         ***************************************************************************************/
        if(locale.equals("mr_IN")){
            buffer.append("SUM(CASE WHEN g.name='पुरुष' THEN 1 ELSE 0 END) AS totalmalecount,");
            buffer.append("SUM(CASE WHEN g.name='स्त्री' THEN 1 ELSE 0 END) AS totalfemalecount, ");
        }else{
            buffer.append("SUM(CASE WHEN g.name='Male' THEN 1 ELSE 0 END) AS totalmalecount,");
            buffer.append("SUM(CASE WHEN g.name='Female' THEN 1 ELSE 0 END) AS totalfemalecount, ");
        }
        /***************************************************************************************/
        for(District i:districts){
            buffer.append("SUM(CASE WHEN d.name='"+i.getName()+"' THEN 1 ELSE 0 END) AS '"+i.getName().trim().replaceAll(" ","_")+"',");
        }
        buffer.deleteCharAt(buffer.length()-1);
        buffer.append("FROM members AS m JOIN members_parties AS mp JOIN parties AS p ");
        buffer.append("JOIN  genders AS g ");
        buffer.append("JOIN election_results AS er JOIN  constituencies AS c JOIN constituencies_districts AS cd JOIN districts AS d JOIN divisions AS divi ");
        buffer.append("WHERE mp.member=m.id AND mp.party=p.id AND ");
        buffer.append("g.id=m.gender_id AND er.member_id=m.id AND er.constituency_id=c.id AND c.id=cd.constituency_id AND cd.district_id=d.id AND ");
        buffer.append("d.division_id=divi.id AND m.locale='mr_IN'  GROUP BY group_party_wise ");
        String firstSelect=buffer.toString();
        buffer.append("UNION ");
        buffer.append("SELECT ");
        buffer.append("'एकूण', ");
        buffer.append("SUM(dt.totalmembers_in_each_group), ");
        buffer.append("SUM(dt.totalmalecount), ");
        buffer.append("SUM(dt.totalfemalecount), ");
        for(District i:districts){
            buffer.append("SUM(dt."+i.getName().trim().replaceAll(" ", "_")+"),");
        }
        buffer.deleteCharAt(buffer.length()-1);
        buffer.append("FROM (");
        buffer.append(firstSelect);
        buffer.append(") AS dt");
        String query=buffer.toString();
        //query ends
        List results=this.em().createNativeQuery(query).getResultList();
        List<MemberPartyDistrictWiseVO> memberPartyDistrictWiseVOs=new ArrayList<MemberPartyDistrictWiseVO>();
        int noOfDistricts=districts.size();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberPartyDistrictWiseVO memberPartyDistrictWiseVO=new MemberPartyDistrictWiseVO();
            memberPartyDistrictWiseVO.setParty(o[0].toString());
            memberPartyDistrictWiseVO.setTotalMember(formatWithoutGrouping.format(Long.parseLong(o[1].toString().trim())));
            memberPartyDistrictWiseVO.setTotalMale(formatWithoutGrouping.format(Long.parseLong(o[2].toString().trim())));
            memberPartyDistrictWiseVO.setTotalFemale(formatWithoutGrouping.format(Long.parseLong(o[3].toString().trim())));
            //Map<String,String> districtWiseDistribution=new TreeMap<String, String>();
            List<Reference> districtWiseDistribution=new ArrayList<Reference>();
            for(int j=0;j<noOfDistricts;j++){
                districtWiseDistribution.add(new Reference(districts.get(j).getName(),formatWithoutGrouping.format(Long.parseLong(o[j+4].toString().trim()))));
            }
            memberPartyDistrictWiseVO.setDistrictsWiseCount(districtWiseDistribution);
            memberPartyDistrictWiseVOs.add(memberPartyDistrictWiseVO);
        }
        return memberPartyDistrictWiseVOs;
    }

    public List<MemberGeneralVO> findfemaleMembers(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query=Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_FEMALE_MEMBERS", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberGeneralVO> memberGeneralVOs=new ArrayList<MemberGeneralVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberGeneralVO memberGeneralVO=new MemberGeneralVO();
            StringBuffer fullName = new StringBuffer();
            if(o[0] != null){
                fullName.append(o[0].toString()+", ");
            }
            if(o[1] != null){
                fullName.append(o[1].toString()+" ");
            }
            if(o[2] != null){
                fullName.append(o[2].toString()+" ");
            }
            if(o[3] != null){
                fullName.append(o[3].toString());
            }
            memberGeneralVO.setFullName(fullName.toString());
            if(o[4] != null){
                memberGeneralVO.setConstituencyNo(formatWithoutGrouping.format(Long.parseLong(o[4].toString().trim())));
            }else{
                memberGeneralVO.setConstituencyNo("-");
            }
            if(o[5] != null){
                memberGeneralVO.setConstituencyName(o[5].toString().trim());
            }else{
                memberGeneralVO.setConstituencyName("-");
            }
            if(o[6] != null){
                memberGeneralVO.setConstituencyDistrict(o[6].toString().trim());
            }else{
                memberGeneralVO.setConstituencyDistrict("-");
            }
            if(o[7] != null){
                memberGeneralVO.setConstituencyReservation(o[7].toString().trim());
            }else{
                memberGeneralVO.setConstituencyReservation("-");
            }
            if(o[8] != null){
                memberGeneralVO.setPartyName(o[8].toString().trim());
            }else{
                memberGeneralVO.setPartyName("-");
            }
            memberGeneralVOs.add(memberGeneralVO);
        }
        return memberGeneralVOs;
    }

    @SuppressWarnings("rawtypes")
    public List<MemberGeneralVO> findMembersByLastName(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query=Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_MEMBERS_LIST_LASTNAME_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberGeneralVO> memberGeneralVOs=new ArrayList<MemberGeneralVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberGeneralVO memberGeneralVO=new MemberGeneralVO();
            StringBuffer fullName = new StringBuffer();
            if(o[0] != null){
                fullName.append(o[0].toString()+", ");
            }
            if(o[1] != null){
                fullName.append(o[1].toString()+" ");
            }
            if(o[2] != null){
                fullName.append(o[2].toString()+" ");
            }
            if(o[3] != null){
                fullName.append(o[3].toString());
            }
            memberGeneralVO.setFullName(fullName.toString());
            if(o[4] != null){
                memberGeneralVO.setConstituencyNo(formatWithoutGrouping.format(Long.parseLong(o[4].toString().trim())));
            }else{
                memberGeneralVO.setConstituencyNo("-");
            }
            if(o[5] != null){
                memberGeneralVO.setConstituencyName(o[5].toString().trim());
            }else{
                memberGeneralVO.setConstituencyName("-");
            }
            if(o[6] != null){
                memberGeneralVO.setConstituencyDistrict(o[6].toString().trim());
            }else{
                memberGeneralVO.setConstituencyDistrict("-");
            }
            if(o[7] != null){
                memberGeneralVO.setConstituencyReservation(o[7].toString().trim());
            }else{
                memberGeneralVO.setConstituencyReservation("-");
            }
            if(o[8] != null){
                memberGeneralVO.setPartyName(o[8].toString().trim());
            }else{
                memberGeneralVO.setPartyName("-");
            }
            memberGeneralVOs.add(memberGeneralVO);
        }
        return memberGeneralVOs;
    }

    public List<MemberGeneralVO> findMembersByDistrict(final String locale) {
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Query query=Query.findByFieldName(Query.class, "keyField", "MIS_REPORT_MEMBERS_LIST_DISTRICT_WISE", locale);
        List results=this.em().createNativeQuery(query.getQuery()).getResultList();
        List<MemberGeneralVO> memberGeneralVOs=new ArrayList<MemberGeneralVO>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            MemberGeneralVO memberGeneralVO=new MemberGeneralVO();
            StringBuffer fullName = new StringBuffer();
            if(o[0] != null){
                fullName.append(o[0].toString()+", ");
            }
            if(o[1] != null){
                fullName.append(o[1].toString()+" ");
            }
            if(o[2] != null){
                fullName.append(o[2].toString()+" ");
            }
            if(o[3] != null){
                fullName.append(o[3].toString());
            }
            memberGeneralVO.setFullName(fullName.toString());
            if(o[4] != null){
                memberGeneralVO.setConstituencyNo(formatWithoutGrouping.format(Long.parseLong(o[4].toString().trim())));
            }else{
                memberGeneralVO.setConstituencyNo("-");
            }
            if(o[5] != null){
                memberGeneralVO.setConstituencyName(o[5].toString().trim());
            }else{
                memberGeneralVO.setConstituencyName("-");
            }
            if(o[6] != null){
                memberGeneralVO.setConstituencyDistrict(o[6].toString().trim());
            }else{
                memberGeneralVO.setConstituencyDistrict("-");
            }
            if(o[7] != null){
                memberGeneralVO.setConstituencyReservation(o[7].toString().trim());
            }else{
                memberGeneralVO.setConstituencyReservation("-");
            }
            if(o[8] != null){
                memberGeneralVO.setPartyName(o[8].toString().trim());
            }else{
                memberGeneralVO.setPartyName("-");
            }
            memberGeneralVOs.add(memberGeneralVO);
        }
        return memberGeneralVOs;
    }

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
}
