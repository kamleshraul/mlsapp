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
import java.util.Locale;

import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.common.vo.RivalMemberVO;
import org.mkcl.els.domain.Address;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Contact;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.ElectionResult;
import org.mkcl.els.domain.FamilyMember;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.mkcl.els.domain.Profession;
import org.mkcl.els.domain.Qualification;
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
                queryCriteriaClause=" AND hmra.constituency.id="+criteria2;
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
		            memberInfo.setConstituency(i.getHouseMemberRoleAssociations().get(0).getConstituency().getNumber()+"-"+i.getHouseMemberRoleAssociations().get(0).getConstituency().getName().trim()+", "+i.getHouseMemberRoleAssociations().get(0).getConstituency().getDistricts().get(0).getName());
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
	                    memberInfo.setConstituency(i.getElectionResults().get(0).getConstituency().getNumber()+"-"+i.getElectionResults().get(0).getConstituency().getName().trim()+", "+i.getElectionResults().get(0).getConstituency().getDistricts().get(0).getName());
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
        SimpleDateFormat dateFormat=null;
        NumberFormat formatWithGrouping=null;
        NumberFormat formatWithoutGrouping=null;
		if(locale.equals("mr_IN")){
			dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("hi","IN"));
			formatWithGrouping=NumberFormat.getInstance(new Locale("hi","IN"));
			formatWithGrouping.setGroupingUsed(true);
			formatWithoutGrouping=NumberFormat.getInstance(new Locale("hi","IN"));
			formatWithoutGrouping.setGroupingUsed(false);
		}else{
			dateFormat = new SimpleDateFormat(parameter.getValue(),new Locale("en","US"));
			formatWithGrouping=NumberFormat.getInstance(new Locale("en","US"));
			formatWithGrouping.setGroupingUsed(true);
			formatWithoutGrouping=NumberFormat.getInstance(new Locale("en","US"));
			formatWithoutGrouping.setGroupingUsed(false);
		}
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

}
