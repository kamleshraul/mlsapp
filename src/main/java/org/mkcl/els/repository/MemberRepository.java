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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MemberAgeWiseReportVO;
import org.mkcl.els.common.vo.MemberAgeWiseVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberChildrenWiseReportVO;
import org.mkcl.els.common.vo.MemberChildrenWiseVO;
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
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.RivalMember;
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
     * @param criteria1 the criteria1
     * @param criteria2 the criteria2
     * @param locale the locale
     * @return the member search page
     */
    @SuppressWarnings("unchecked")
    public MemberSearchPage search(final String housetype, final String criteria1,
            final Long criteria2, final String locale) {

        String querySelectClause="SELECT m FROM Member m LEFT JOIN m.houseMemberRoleAssociations hmra " +
        "LEFT JOIN m.memberPartyAssociations mpa LEFT JOIN m.electionResults er WHERE m.locale='"+locale+"'";
        String queryCriteriaClause=null;
        if(criteria1.equals("constituency")){
            if(criteria2==0){
                queryCriteriaClause=" ";
            }else{
                queryCriteriaClause=" AND (hmra.constituency.id="+criteria2+" OR er.constituency.id="+criteria2+") ";
            }
        }else if(criteria1.equals("party")){
            if(criteria2==0){
                queryCriteriaClause=" ";
            }else{
                queryCriteriaClause=" AND mpa.party.id="+criteria2;
            }
        }else if(criteria1.equals("gender")){
            if(criteria2==0){
                queryCriteriaClause=" ";
            }else{
                queryCriteriaClause=" AND m.gender.id="+criteria2;
            }
        }else if(criteria1.equals("marital_status")){
            if(criteria2==0){
                queryCriteriaClause=" ";
            }else{
                queryCriteriaClause=" AND m.maritalStatus.id="+criteria2;
            }
        }else if(criteria1.equals("all")){
            queryCriteriaClause=" ";
        }
        String queryOrderByClause=" ORDER BY m.lastName asc";
        String query=querySelectClause+queryCriteriaClause+queryOrderByClause;
        List<Member> records=this.em().createQuery(query).getResultList();
        MemberSearchPage memberSearchPage=new MemberSearchPage();
        List<MemberInfo> memberInfos=new ArrayList<MemberInfo>();
        for(Member i:records){
            MemberInfo memberInfo=new MemberInfo();
            memberInfo.setTitle(i.getTitle()!=null?i.getTitle().getName():"-");
            memberInfo.setFirstName(i.getFirstName().trim());
            memberInfo.setMiddleName(i.getMiddleName().trim());
            memberInfo.setLastName(i.getLastName().trim());
            if(i.getHouseMemberRoleAssociations().isEmpty()){
                memberInfo.setConstituency("-");
            }else {
                if(i.getHouseMemberRoleAssociations().get(0).getConstituency()!=null){
                    if(criteria1.equals("constituency")){
                        memberInfo.setConstituency(i.getHouseMemberRoleAssociations().get(0).getConstituency().getName().trim()+"-"+i.getHouseMemberRoleAssociations().get(0).getConstituency().getNumber()+", "+i.getHouseMemberRoleAssociations().get(0).getConstituency().getDistricts().get(0).getName());
                    }else{
                        memberInfo.setConstituency(i.getHouseMemberRoleAssociations().get(0).getConstituency().getNumber()+"-"+i.getHouseMemberRoleAssociations().get(0).getConstituency().getName().trim()+", "+i.getHouseMemberRoleAssociations().get(0).getConstituency().getDistricts().get(0).getName());
                    }
                }else{
                    memberInfo.setConstituency("-");
                }
            }
            //in case constituency is not found in the house member role association table it will be read from the
            //election results table.
            if(memberInfo.getConstituency().equals("-")){
                if(i.getElectionResults().isEmpty()){
                    memberInfo.setConstituency("-");
                }else {
                    if(i.getElectionResults().get(0).getConstituency()!=null){
                        if(criteria1.equals("constituency")){
                            //here we need to also take into account those constituencies which are nominated ones and
                            //donot have any number.here we are assuming that for nominated members constituency
                            //will have a name but no number and districts
                            if(i.getElectionResults().get(0).getConstituency().getNumber()!=null){
                                memberInfo.setConstituency(i.getElectionResults().get(0).getConstituency().getName().trim()+"-"+i.getElectionResults().get(0).getConstituency().getNumber()+", "+i.getElectionResults().get(0).getConstituency().getDistricts().get(0).getName());
                            }else{
                                memberInfo.setConstituency(i.getElectionResults().get(0).getConstituency().getName().trim());
                            }
                        }else{
                            if(i.getElectionResults().get(0).getConstituency().getNumber()!=null){
                            memberInfo.setConstituency(i.getElectionResults().get(0).getConstituency().getNumber()+"-"+i.getElectionResults().get(0).getConstituency().getName().trim()+", "+i.getElectionResults().get(0).getConstituency().getDistricts().get(0).getName());
                            }else{
                                memberInfo.setConstituency(i.getElectionResults().get(0).getConstituency().getName().trim());
                            }
                        }
                    }else{
                        memberInfo.setConstituency("-");
                    }
                }
            }
            if(i.getMemberPartyAssociations().isEmpty()){
                memberInfo.setParty("-");
            }else {
                if(i.getMemberPartyAssociations().get(0).getParty()!=null){
                    // memberInfo.setParty(i.getMemberPartyAssociations().get(0).getParty().getName().trim()+"("+i.getMemberPartyAssociations().get(0).getParty().getShortName().trim()+")");
                    memberInfo.setParty(i.getMemberPartyAssociations().get(0).getParty().getName().trim());
                }else{
                    memberInfo.setParty("-");
                }
            }
            memberInfo.setGender(i.getGender().getName().trim());
            memberInfo.setMaritalStatus(i.getMaritalStatus().getName().trim());
            memberInfo.setId(i.getId());
            memberInfos.add(memberInfo);
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
     * @return the member biography vo
     */
    public MemberBiographyVO findBiography(final long id, final String locale) {
        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        SimpleDateFormat dateFormat=FormaterUtil.getDateFormatter(parameter.getValue(), locale);
        NumberFormat formatWithGrouping=FormaterUtil.getNumberFormatterGrouping(locale);
        NumberFormat formatWithoutGrouping=FormaterUtil.getNumberFormatterNoGrouping(locale);
        Member m=Member.findById(Member.class, id);
        MemberBiographyVO memberBiographyVO=new MemberBiographyVO();
        //the header in the biography page.
        if(m.getTitle()==null){
            memberBiographyVO.setTitle("-");
        }else{
            memberBiographyVO.setTitle(m.getTitle().getName());
        }
        memberBiographyVO.setAlias(m.getAlias());
        memberBiographyVO.setEnableAliasing(m.getAliasEnabled());
        memberBiographyVO.setFirstName(m.getFirstName());
        memberBiographyVO.setMiddleName(m.getMiddleName());
        memberBiographyVO.setLastName(m.getLastName());
        if(m.getPhoto().isEmpty()){
            memberBiographyVO.setPhoto("-");
        }else{
            memberBiographyVO.setPhoto(m.getPhoto());
        }
        //here the value that should be taken must be the one where the role has the maximum priority
        //but for the time being we are taking the first one as only one entry is going to be taken.
        //in all the cases below selection has to be made depending upon the from and to date fields.
        //constituency
        if(!m.getHouseMemberRoleAssociations().isEmpty()){
            Constituency constituency=m.getHouseMemberRoleAssociations().get(0).getConstituency();
            memberBiographyVO.setConstituency(constituency.getNumber()+"-"+constituency.getName()+", "+constituency.getDistricts().get(0).getName());
        }else if(!m.getElectionResults().isEmpty()){
            Constituency constituency=m.getElectionResults().get(0).getConstituency();
            memberBiographyVO.setConstituency(constituency.getNumber()+"-"+constituency.getName()+", "+constituency.getDistricts().get(0).getName());
        }else{
            memberBiographyVO.setConstituency("-");
        }
        //party
        if(!m.getMemberPartyAssociations().isEmpty()){
            Party party=m.getMemberPartyAssociations().get(0).getParty();
            //memberBiographyVO.setPartyName(party.getName()+"("+party.getShortName()+")");
            memberBiographyVO.setPartyName(party.getName());
            if(!party.getPartySymbols().isEmpty()){
                memberBiographyVO.setPartyFlag(party.getPartySymbols().get(0).getSymbol());
            }else{
                memberBiographyVO.setPartyFlag("-");
            }
        }else{
            memberBiographyVO.setPartyName("-");
            memberBiographyVO.setPartyFlag("-");
        }
        //the member biography fields in the order it appears in use case.
        //family details
        memberBiographyVO.setFatherName("-");
        memberBiographyVO.setMotherName("-");
        memberBiographyVO.setNoOfDaughter("-");
        memberBiographyVO.setNoOfSons("-");
        memberBiographyVO.setSpouseName("-");
        if(m.getFamilyMembers().isEmpty()){
        }else{
            //right now we are doing just for marathi.and so we are comparing directly with the ids.
            int noOfSons=0;
            int noOfDaughters=0;
            for(FamilyMember i:m.getFamilyMembers()){
                if(i.getRelation().getId()==7){
                    memberBiographyVO.setFatherName(i.getName());
                }else if(i.getRelation().getId()==8){
                    memberBiographyVO.setMotherName(i.getName());
                }else if(i.getRelation().getId()==3){
                    memberBiographyVO.setSpouseName(i.getName());
                }else if(i.getRelation().getId()==4){
                    memberBiographyVO.setSpouseName(i.getName());
                }else if(i.getRelation().getId()==5){
                    noOfSons++;
                }else if(i.getRelation().getId()==6){
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
        }
        //birth date and birth place
        if(m.getBirthDate()==null){
            memberBiographyVO.setBirthDate("-");
        }else{
            memberBiographyVO.setBirthDate(dateFormat.format(m.getBirthDate()));
        }
        memberBiographyVO.setPlaceOfBirth(m.getBirthPlace().trim());

        //death date,condolence date and obituary
        if(m.getDeathDate()==null){
            memberBiographyVO.setDeathDate("-");
        }else{
            memberBiographyVO.setDeathDate(dateFormat.format(m.getDeathDate()));
        }
        if(m.getCondolenceDate()==null){
            memberBiographyVO.setCondolenceDate("-");
        }else{
            memberBiographyVO.setCondolenceDate(dateFormat.format(m.getCondolenceDate()));
        }
        if(m.getObituary()==null){
            memberBiographyVO.setObituary("-");
        }else{
            memberBiographyVO.setObituary(m.getObituary().trim());
        }
        //marital status and marriage date
        if(m.getMaritalStatus()==null){
            memberBiographyVO.setMaritalStatus("-");
        }else{
            memberBiographyVO.setMaritalStatus(m.getMaritalStatus().getName());
        }
        if(m.getMarriageDate()==null){
            memberBiographyVO.setMarriageDate("-");
        }else{
            memberBiographyVO.setMarriageDate(dateFormat.format(m.getMarriageDate()));
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
                    buffer.append("<br>"+i.getDegree().getName()+":"+i.getDetails());
                }else{
                    buffer.append(i.getDegree().getName()+":"+i.getDetails());
                }
            }
            memberBiographyVO.setEducationalQualification(buffer.toString());
        }
        //languages.This will be comma separated values
        List<Language> languages=m.getLanguages();
        if(languages.isEmpty()){
            memberBiographyVO.setLanguagesKnown("-");
        }else{
            StringBuffer buffer=new StringBuffer();
            for(Language i:languages){
                buffer.append(i.getName()+",");
            }
            buffer.deleteCharAt(buffer.length()-1);
            memberBiographyVO.setLanguagesKnown(buffer.toString());
        }
        //profession.this will also be comma separated values
        List<Profession> professions=m.getProfessions();
        if(m.getProfessions().isEmpty()){
            memberBiographyVO.setProfession("-");
        }else{
            StringBuffer buffer=new StringBuffer();
            for(Profession i:professions){
                buffer.append(i.getName()+",");
            }
            buffer.deleteCharAt(buffer.length()-1);
            memberBiographyVO.setProfession(buffer.toString());
        }
        //contact info
        Contact contact=m.getContact();
        if(contact==null){
            memberBiographyVO.setEmail("");
            memberBiographyVO.setWebsite("");
            memberBiographyVO.setFax("");
            memberBiographyVO.setMobile("");
            memberBiographyVO.setTelephone("");
        }else{
            memberBiographyVO.setEmail(contact.getEmail1()+"<br>"+contact.getEmail2());
            memberBiographyVO.setWebsite(contact.getWebsite1()+"<br>"+contact.getWebsite2());
            memberBiographyVO.setFax(contact.getFax1()+"<br>"+contact.getFax2());
            memberBiographyVO.setMobile(contact.getMobile1()+"<br>"+contact.getMobile2());
            memberBiographyVO.setTelephone(contact.getTelephone1()+"<br>"+contact.getTelephone2());
        }
        //present address
        Address presentAddress=m.getPresentAddress();
        if(presentAddress==null){
            memberBiographyVO.setPresentAddress("-");
        }else{
            if(!presentAddress.getDetails().trim().isEmpty()){
                if(presentAddress.getTehsil()!=null){
                    memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getTehsil().getName()+","+presentAddress.getDistrict().getName()+"("+presentAddress.getState().getName()+") "+presentAddress.getPincode());
                }else{
                    memberBiographyVO.setPresentAddress(presentAddress.getDetails()+"<br>"+presentAddress.getDistrict().getName()+"("+presentAddress.getState().getName()+") "+presentAddress.getPincode());
                }
            }else{
                memberBiographyVO.setPresentAddress("-");
            }
        }
        //permanent address
        Address permanentAddress=m.getPermanentAddress();
        if(permanentAddress==null){
            memberBiographyVO.setPermanentAddress("-");
        }else{
            if(!permanentAddress.getDetails().trim().isEmpty()) {
                if(permanentAddress.getTehsil()!=null){
                    memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getTehsil().getName()+","+permanentAddress.getDistrict().getName()+"("+permanentAddress.getState().getName()+") "+permanentAddress.getPincode());
                }else{
                    memberBiographyVO.setPermanentAddress(permanentAddress.getDetails()+"<br>"+permanentAddress.getDistrict().getName()+"("+permanentAddress.getState().getName()+") "+permanentAddress.getPincode());
                }
            }else{
                memberBiographyVO.setPermanentAddress("-");
            }
        }
        //office address
        Address officeAddress=m.getOfficeAddress();
        if(officeAddress==null){
            memberBiographyVO.setOfficeAddress("-");
        }else{
            if(!officeAddress.getDetails().trim().isEmpty()){
                if(officeAddress.getTehsil()!=null){
                    memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getTehsil().getName()+","+officeAddress.getDistrict().getName()+"("+officeAddress.getState().getName()+") "+officeAddress.getPincode());
                }else{
                    memberBiographyVO.setOfficeAddress(officeAddress.getDetails()+"<br>"+officeAddress.getDistrict().getName()+"("+officeAddress.getState().getName()+") "+officeAddress.getPincode());
                }
            }else{
                memberBiographyVO.setOfficeAddress("-");
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
            memberBiographyVO.setRivalMembers(new ArrayList<RivalMemberVO>());
        }else{
            memberBiographyVO.setValidVotes(formatWithGrouping.format(electionResults.get(0).getTotalValidVotes()));
            memberBiographyVO.setVotesReceived(formatWithGrouping.format(electionResults.get(0).getVotesReceived()));
            List<RivalMember> rivals=electionResults.get(0).getRivalMembers();
            List<RivalMemberVO> rivalMemberVOs=new ArrayList<RivalMemberVO>();
            if(!rivals.isEmpty()){
                for(RivalMember i:rivals){
                    RivalMemberVO rivalMemberVO=new RivalMemberVO();
                    rivalMemberVO.setName(i.getName());
                    rivalMemberVO.setParty(i.getParty().getName());
                    rivalMemberVO.setVotesReceived(formatWithGrouping.format(i.getVotesReceived()));
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
}
