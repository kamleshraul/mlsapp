package org.mkcl.els.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.Query;
import javax.sql.DataSource;
import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDetailsRepository extends BaseRepository<MemberDetails, Long>{

	private JdbcTemplate jdbcTemplate;

	private static final String CRITERIA_NAME="name";

	private static final String CRITERIA_PARTY="party";

	private static final String CRITERIA_CONSTITUENCY="constituency";

	private static final String CRITERIA_NO_OF_TERMS="no_of_terms";

	private static final String CRITERIA_BIRTH_DATE="birth_date";

	private static final String CRITERIA_MARITAL_STATUS="marital_status";

	private static final String CRITERIA_GENDER="gender";

	private static final String CRITERIA_ALL="all";

	private Integer PAGE_SIZE=20;

	private static final String FETCH_QUERY="SELECT first_name, middle_name,last_name,gender,marital_status,"+
	"no_of_terms,birth_date,constituencies.name as constituency," +
	"parties.name as party FROM member_details JOIN constituencies JOIN parties " +
	"WHERE member_details.constituency=constituencies.id and member_details.party_name=parties.id and "; 

	private static final String COUNT_QUERY="SELECT count(*) FROM member_details WHERE";

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

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

	public MemberSearchPage searchMemberDetails(String criteria1,String locale) {
		return searchMemberDetails(criteria1, CRITERIA_ALL,locale);
	}

	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, String locale) {
		String searchCriteria=null;
		if(criteria2.isEmpty()){
			criteria2=CRITERIA_ALL;
		}
		if(criteria1.equals(CRITERIA_NAME)){			
			searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.first_name asc";	
		}else if(criteria1.equals(CRITERIA_CONSTITUENCY)){
			if(criteria2.equals(CRITERIA_ALL)){				
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY constituencies.name asc";
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and constituencies.name='"+criteria2+"'";
				}			
		}else if(criteria1.equals(CRITERIA_PARTY)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY parties.name,member_details.first_name asc";
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and parties.name='"+criteria2+"' ORDER BY member_details.first_name asc";
			}			
		}else if(criteria1.equals(CRITERIA_GENDER)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.gender,member_details.first_name asc";
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.gender='"+criteria2+"' ORDER BY member_details.first_name asc";
			}	
		}else if(criteria1.equals(CRITERIA_MARITAL_STATUS)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.marital_status,member_details.first_name asc";
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.marital_status="+criteria2+" ORDER BY member_details.first_name asc";
			}	
		}else if(criteria1.equals(CRITERIA_NO_OF_TERMS)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.no_of_terms,member_details.first_name asc";
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.no_of_terms='"+criteria2+"' ORDER BY member_details.first_name asc";
			}	
		}else if(criteria1.equals(CRITERIA_BIRTH_DATE)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.birth_date,member_details.first_name asc";
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.birth_date='"+criteria2+"' ORDER BY member_details.first_name asc";
			}	
		}

		RowMapper<MemberInfo> mapper = new RowMapper<MemberInfo>() {
			@Override
			public MemberInfo mapRow(ResultSet rs, int arg1) throws SQLException {
				MemberInfo memberInfo = new MemberInfo();
				memberInfo.setFirstName(rs.getString("first_name"));
				memberInfo.setMiddleName(rs.getString("middle_name"));
				memberInfo.setLastName(rs.getString("last_name"));
				memberInfo.setConstituency(rs.getString("constituency"));
				memberInfo.setParty(rs.getString("party"));
				memberInfo.setGender(rs.getString("gender"));
				memberInfo.setMaritalStatus(rs.getBoolean("marital_status"));
				memberInfo.setNoOfTerms(rs.getInt("no_of_terms"));
				memberInfo.setBirthDate(rs.getDate("birth_date"));
				return memberInfo;
			}
		};		
		MemberSearchPage searchPage=new MemberSearchPage();
		searchPage.setPageItems(jdbcTemplate.query(FETCH_QUERY+searchCriteria, mapper, new Object[]{}));
		System.out.println(FETCH_QUERY+searchCriteria);
		if(searchCriteria.contains("ORDER BY")){
			searchCriteria=searchCriteria.split("ORDER BY")[0];
		}
		searchPage.setTotalRecords(jdbcTemplate.queryForInt(COUNT_QUERY+searchCriteria));
		System.out.println(COUNT_QUERY+searchCriteria);
		return 	searchPage;	
	}

	public MemberSearchPage searchMemberDetails(String criteria1, int page,
			int rows, String locale) {
		return searchMemberDetails(criteria1, CRITERIA_ALL, page, rows, locale);	
	}
	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, int page, int rows, String locale) {
		this.PAGE_SIZE=rows;
		int firstIndex=(page-1)*PAGE_SIZE;
		String searchCriteria=null;
		if(criteria2.isEmpty()){
			criteria2=CRITERIA_ALL;
		}
		if(criteria1.equals(CRITERIA_NAME)){			
			searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;		
		}else if(criteria1.equals(CRITERIA_CONSTITUENCY)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY constituencies.name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and constituencies.name='"+criteria2+"' LIMIT "+firstIndex+","+PAGE_SIZE;
			}

		}else if(criteria1.equals(CRITERIA_PARTY)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY parties.name,member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and parties.name='"+criteria2+"' ORDER BY member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}			
		}else if(criteria1.equals(CRITERIA_GENDER)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.gender,member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.gender='"+criteria2+"' ORDER BY member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}	
		}else if(criteria1.equals(CRITERIA_MARITAL_STATUS)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.marital_status,member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.marital_status="+criteria2+" ORDER BY member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}	
		}else if(criteria1.equals(CRITERIA_NO_OF_TERMS)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.no_of_terms,member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.no_of_terms='"+criteria2+"' ORDER BY member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}	
		}else if(criteria1.equals(CRITERIA_BIRTH_DATE)){
			if(criteria2.equals(CRITERIA_ALL)){
				searchCriteria=" member_details.locale ='" + locale+"' ORDER BY member_details.birth_date,member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}
			else{
				searchCriteria=" member_details.locale ='" + locale+"' and member_details.birth_date='"+criteria2+"' ORDER BY member_details.first_name asc LIMIT "+firstIndex+","+PAGE_SIZE;
			}	
		}
		RowMapper<MemberInfo> mapper = new RowMapper<MemberInfo>() {
			@Override
			public MemberInfo mapRow(ResultSet rs, int rowNo) throws SQLException {
				MemberInfo memberInfo = new MemberInfo();
				memberInfo.setFirstName(rs.getString("first_name"));
				memberInfo.setMiddleName(rs.getString("middle_name"));
				memberInfo.setLastName(rs.getString("last_name"));
				memberInfo.setConstituency(rs.getString("constituency"));
				memberInfo.setParty(rs.getString("party"));
				memberInfo.setGender(rs.getString("gender"));
				memberInfo.setMaritalStatus(rs.getBoolean("marital_status"));
				memberInfo.setNoOfTerms(rs.getInt("no_of_terms"));
				memberInfo.setBirthDate(rs.getDate("birth_date"));				
				return memberInfo;
			}
		};	
		MemberSearchPage searchPage=new MemberSearchPage();
		searchPage.setPageItems(jdbcTemplate.query(FETCH_QUERY+searchCriteria, mapper, new Object[]{}));
		System.out.println(FETCH_QUERY+searchCriteria);
		if(searchCriteria.contains("ORDER BY")){
			searchCriteria=searchCriteria.split("ORDER BY")[0];
		}
		searchPage.setTotalRecords(jdbcTemplate.queryForInt(COUNT_QUERY+searchCriteria));
		System.out.println(COUNT_QUERY+searchCriteria);
		return 	searchPage;	
	}

	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, String criteria3, String locale) {
		return null;
	}

	public MemberSearchPage searchMemberDetails(String criteria1,
			String criteria2, String criteria3, int page, int rows,
			String locale) {
		return null;
	}


}
