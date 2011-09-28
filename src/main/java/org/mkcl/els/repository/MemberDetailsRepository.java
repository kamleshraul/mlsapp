package org.mkcl.els.repository;

import javax.persistence.Query;
import org.mkcl.els.domain.MemberDetails;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDetailsRepository extends BaseRepository<MemberDetails, Long>{

	public int updateMemberPersonalDetails(
			MemberDetails memberPersonalDetails) {
		String insertQuery="update member_details set photo=:photo,"+
		"title=:title,"+
		"first_name=:firstName,"+
		"middle_name=:middleName,"+
		"last_name=:lastName,"+
		"constituency=:constituency,"+
		"party_name=:partyName,"+
		"father_title=:fatherTitle,"+
		"father_name=:fatherName,"+
		"mother_title=:motherTitle,"+
		"mother_name=:motherName,"+
		"birth_date=:birthDate,"+
		"marital_status=:maritalStatus,"+
		"marriage_date=:marriageDate,"+
		"spouse_name=:spouseName,"+
		"no_of_sons=:noOfSons,"+
		"no_of_daughter=:noOfDaughter,"+
		"educational_qualification=:educationalQualification,"+
		"profession=:profession where id=:id";
		
		Query query=this.em().createNativeQuery(insertQuery);
		query.setParameter("id", memberPersonalDetails.getId());
		query.setParameter("photo", memberPersonalDetails.getPhoto());
		query.setParameter("title", memberPersonalDetails.getTitle());
		query.setParameter("firstName", memberPersonalDetails.getFirstName());
		query.setParameter("middleName", memberPersonalDetails.getMiddleName());
		query.setParameter("lastName", memberPersonalDetails.getLastName());
		query.setParameter("constituency", memberPersonalDetails.getConstituency()==null?null:memberPersonalDetails.getConstituency().getId());
		query.setParameter("partyName", memberPersonalDetails.getPartyName()==null?null: memberPersonalDetails.getPartyName().getId());
		query.setParameter("fatherTitle", memberPersonalDetails.getFatherTitle());
		query.setParameter("fatherName", memberPersonalDetails.getFatherName());
		query.setParameter("motherTitle", memberPersonalDetails.getMotherTitle());
		query.setParameter("motherName", memberPersonalDetails.getMotherName());
		query.setParameter("birthDate", memberPersonalDetails.getBirthDate());
		query.setParameter("maritalStatus", memberPersonalDetails.isMaritalStatus());
		query.setParameter("marriageDate", memberPersonalDetails.getMarriageDate());
		query.setParameter("spouseName", memberPersonalDetails.getSpouseName());
		query.setParameter("noOfSons", memberPersonalDetails.getNoOfSons());
		query.setParameter("noOfDaughter", memberPersonalDetails.getNoOfDaughter());
		query.setParameter("educationalQualification", memberPersonalDetails.getEducationalQualification());
		query.setParameter("profession", memberPersonalDetails.getProfession());		
		int noOfRows=query.executeUpdate();
		return noOfRows;
	}

	public int updateMemberContactDetails(MemberDetails memberContactDetails) {
		String insertQuery="update member_details set email=:email,"+
		"present_address=:presentAddress,"+
		"present_state=:presentState,"+
		"present_district=:presentDistrict,"+
		"present_tehsil=:presentTehsil,"+
		"present_city=:presentCity,"+
		"present_pin_code=:presentPinCode,"+
		"present_telephone=:presentTelephone,"+
		"present_fax=:presentFax,"+
		"present_mobile=:presentMobile,"+
		"permanent_address=:permanentAddress,"+
		"permanent_state=:permanentState,"+
		"permanent_district=:permanentDistrict,"+
		"permanent_tehsil=:permanentTehsil,"+
		"permanent_city=:permanentCity,"+
		"permanent_pin_code=:permanentPinCode,"+
		"permanent_telephone=:permanentTelephone,"+
		"permanent_fax=:permanentFax,"+
		"permanent_mobile=:permanentMobile where id=:id";
		
		Query query=this.em().createNativeQuery(insertQuery);
		query.setParameter("id", memberContactDetails.getId());
		query.setParameter("email", memberContactDetails.getEmail());
		query.setParameter("presentAddress", memberContactDetails.getPresentAddress());
		query.setParameter("presentState", memberContactDetails.getPresentState()==null?null:memberContactDetails.getPresentState().getId());
		query.setParameter("presentDistrict", memberContactDetails.getPresentDistrict()==null?null:memberContactDetails.getPresentDistrict().getId());
		query.setParameter("presentTehsil", memberContactDetails.getPresentTehsil()==null?null:memberContactDetails.getPresentTehsil().getId());
		query.setParameter("presentCity", memberContactDetails.getPresentCity());
		query.setParameter("presentPinCode", memberContactDetails.getPresentPinCode());
		query.setParameter("presentTelephone", memberContactDetails.getPresentTelephone());
		query.setParameter("presentFax", memberContactDetails.getPresentFax());
		query.setParameter("presentMobile", memberContactDetails.getPresentMobile());
		query.setParameter("permanentAddress", memberContactDetails.getPermanentAddress());
		query.setParameter("permanentState", memberContactDetails.getPermanentState()==null?null:memberContactDetails.getPermanentState().getId());
		query.setParameter("permanentDistrict", memberContactDetails.getPermanentDistrict()==null?null:memberContactDetails.getPermanentDistrict().getId());
		query.setParameter("permanentTehsil", memberContactDetails.getPermanentTehsil()==null?null:memberContactDetails.getPermanentTehsil().getId());
		query.setParameter("permanentCity", memberContactDetails.getPermanentCity());
		query.setParameter("permanentPinCode", memberContactDetails.getPermanentPinCode());
		query.setParameter("permanentTelephone", memberContactDetails.getPermanentTelephone());
		query.setParameter("permanentFax", memberContactDetails.getPermanentFax());
		query.setParameter("permanentMobile", memberContactDetails.getPermanentMobile());		
		int noOfRows=query.executeUpdate();
		return noOfRows;
	}

	public int updateMemberOtherDetails(MemberDetails memberOtherDetails) {
		String insertQuery="update member_details set no_of_terms=:noOfTerms,"+
		"socio_cultural_activities=:socioCulturalActivities,"+
		"literary_artistic_sc_accomplishment=:literaryArtisticScAccomplishment,"+
		"special_interests=:specialInterests,"+
		"pastime_recreation=:pastimeRecreation,"+
		"sports_clubs=:sportsClubs,"+
		"countries_visited=:countriesVisited,"+
		"experience=:experience,"+
		"other_info=:otherInfo where id=:id";		
		Query query=this.em().createNativeQuery(insertQuery);
		query.setParameter("id", memberOtherDetails.getId());
		query.setParameter("noOfTerms", memberOtherDetails.getNoOfTerms());
		query.setParameter("socioCulturalActivities", memberOtherDetails.getSocioCulturalActivities());
		query.setParameter("literaryArtisticScAccomplishment", memberOtherDetails.getLiteraryArtisticScAccomplishment());
		query.setParameter("specialInterests", memberOtherDetails.getSpecialInterests());
		query.setParameter("pastimeRecreation", memberOtherDetails.getPastimeRecreation());
		query.setParameter("sportsClubs", memberOtherDetails.getSportsClubs());
		query.setParameter("countriesVisited", memberOtherDetails.getCountriesVisited());
		query.setParameter("experience", memberOtherDetails.getExperience());
		query.setParameter("otherInfo", memberOtherDetails.getOtherInfo());
		int noOfRows=query.executeUpdate();
		return noOfRows;
	}

}
