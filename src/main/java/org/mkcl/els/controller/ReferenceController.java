/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.ReferenceController.java
 * Created On: May 4, 2012
 */

package org.mkcl.els.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AutoCompleteVO;
import org.mkcl.els.common.vo.ConstituencyCompleteVO;
import org.mkcl.els.common.vo.DynamicSelectVO;
import org.mkcl.els.common.vo.GroupVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CommitteeType;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MemberMinister;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.PartyType;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowActor;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// TODO: Auto-generated Javadoc
/**
 * The Class ReferenceController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/ref")
public class ReferenceController extends BaseController {

	/**
	 * Gets the districts by state id.
	 *
	 * @param stateId the state id
	 * @param map the map
	 * @param locale the locale
	 * @return the districts by state id
	 */
	@RequestMapping(value = "state/{stateId}/districts",
			method = RequestMethod.GET)
			public @ResponseBody
			List<Reference> getDistrictsByState(
					@PathVariable("stateId") final Long stateId,
					final ModelMap map,
					final Locale locale) {
		List<Reference> districts = new ArrayList<Reference>();
		try {
			districts = District.findDistrictsRefByStateId(
					stateId , "name" , ApplicationConstants.ASC ,
					locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
		}
		
		return districts;
	}

	/**
	 * Gets the tehsils by district.
	 *
	 * @param districtId the district id
	 * @param map the map
	 * @param locale the locale
	 * @return the tehsils by district
	 */
	@RequestMapping(value = "district/{districtId}/tehsils",
			method = RequestMethod.GET)
			public @ResponseBody
			List<Reference> getTehsilsByDistrict(
					@PathVariable("districtId") final Long districtId,
					final ModelMap map,
					final Locale locale) {
		
		try {
			return Tehsil.findTehsilsRefByDistrictId(districtId , "name" , ApplicationConstants.ASC ,locale.toString());
		} catch (ELSException e) {
			map.addAttribute("error", e.getParameter());
		}
		return new ArrayList<Reference>();
	}


	/**
	 * Gets the districts by state id.
	 *
	 * @param stateId the state id
	 * @param map the map
	 * @param locale the locale
	 * @return the districts by state id
	 */
	@RequestMapping(value = "/state{state_id}/districts",
			method = RequestMethod.GET)
			public @ResponseBody
			List<District> getDistrictsByStateId(
					@PathVariable("state_id") final Long stateId,
					final ModelMap map,
					final Locale locale) {
		List<District> districts = new ArrayList<District>();
		try{
			districts = District.findDistrictsByStateId(stateId,"name", "ASC", locale.toString());
		}catch (ELSException e) {
			map.addAttribute("REFERENCE_CONTROLLER", "Request can not be completed at the moment.");
		}
		return districts;
	}

	/**
	 * Gets the divisions by state id.
	 *
	 * @param stateId the state id
	 * @param map the map
	 * @param locale the locale
	 * @return the divisions by state id
	 */
	@RequestMapping(value = "/{state_id}/divisions", method = RequestMethod.GET)
	public @ResponseBody
	List<Division> getDivisionsByStateId(
			@PathVariable("state_id") final Long stateId, 
			final ModelMap map,
			final Locale locale) {
		
		return Division.findAllByFieldName(Division.class, "state",
				State.findById(State.class, stateId), "name", "asc",
				locale.toString());
	}

	/**
	 * Gets the districts by division id.
	 *
	 * @param divisionId the division id
	 * @param map the map
	 * @param locale the locale
	 * @return the districts by division id
	 */
	@RequestMapping(value = "/{division_id}/districts",
			method = RequestMethod.GET)
			public @ResponseBody
			List<District> getDistrictsByDivisionId(
					@PathVariable("division_id") final Long divisionId,
					final ModelMap map, 
					final Locale locale) {
		List<District> districts = new LinkedList<District>();
		Division division = Division.findById(Division.class, divisionId);
		if (division != null) {
			districts = District.findAllByFieldName(District.class, "division",
					division, "name", "asc", locale.toString());
		}

		return districts;
	}

	/**
	 * Gets the railway stations by selected districts.
	 *
	 * @param districtsStr the districts str
	 * @param map the map
	 * @param locale the locale
	 * @return the railway stations by selected districts
	 */
	@RequestMapping(value = "/districts{selectedDistricts}/railwayStations",
			method = RequestMethod.GET)
			public @ResponseBody
			List<RailwayStation> getRailwayStationsBySelectedDistricts(
					@PathVariable("selectedDistricts") final String districtsStr,
					final ModelMap map,
					final Locale locale) {
		List<RailwayStation> railwayStationsForSelectedDistricts = new LinkedList<RailwayStation>();
		List<RailwayStation> railwayStationsForDistrict = new LinkedList<RailwayStation>();
		String districts[] = districtsStr.split(",");
		for (int i = 0; i < districts.length; i++) {
			railwayStationsForDistrict = RailwayStation.findAllByFieldName(
					RailwayStation.class,
					"district",
					District.findById(District.class,
							Long.parseLong(districts[i])), "name", "asc",
							locale.toString());
			railwayStationsForSelectedDistricts
			.addAll(railwayStationsForDistrict);
			railwayStationsForDistrict.clear();
		}
		return railwayStationsForSelectedDistricts;
	}

	/**
	 * Gets the airports by selected districts.
	 *
	 * @param districtsStr the districts str
	 * @param map the map
	 * @param locale the locale
	 * @return the airports by selected districts
	 */
	@RequestMapping(value = "/districts{selectedDistricts}/airports",
			method = RequestMethod.GET)
			public @ResponseBody
			List<Airport> getAirportsBySelectedDistricts(
					@PathVariable("selectedDistricts") final String districtsStr,
					final ModelMap map, 
					final Locale locale) {
		List<Airport> airportsForSelectedDistricts = new LinkedList<Airport>();
		List<Airport> airportsForDistrict = new LinkedList<Airport>();
		String districts[] = districtsStr.split(",");
		for (int i = 0; i < districts.length; i++) {
			airportsForDistrict = Airport.findAllByFieldName(
					Airport.class,
					"district",
					District.findById(District.class,
							Long.parseLong(districts[i])), "name", "asc",
							locale.toString());
			airportsForSelectedDistricts.addAll(airportsForDistrict);
			airportsForDistrict.clear();
		}
		return airportsForSelectedDistricts;
	}

	/**
	 * Gets the tehsils by district id.
	 *
	 * @param districtId the district id
	 * @param map the map
	 * @param request the request
	 * @param locale the locale
	 * @return the tehsils by district id
	 */
	@RequestMapping(value = "/{district_id}/tehsils",
			method = RequestMethod.GET)
			public @ResponseBody
			List<Tehsil> getTehsilsByDistrictId(
					@PathVariable("district_id") final Long districtId,
					final ModelMap map, 
					final HttpServletRequest request,
					final Locale locale) {
		District district = District.findById(District.class, districtId);
		return Tehsil.findAllByFieldName(Tehsil.class, "district", district,
				"name", "asc", locale.toString());
	}

	/**
	 * Gets the constituencies starting with.
	 *
	 * @param constituencyName the constituency name
	 * @param map the map
	 * @param request the request
	 * @param locale the locale
	 * @return the constituencies starting with Explaination for
	 * Internationalization request.getParameter(param) doesn't seem to
	 * respect the request.setCharacterEncoding(encoding) as set in the
	 * SetCharacterEncodingFilter in case of a GET request.Meaning the
	 * parameter is returned in the default encoding scheme as specified
	 * in servlet 2.0 spec. i.e ISO 8859-1.This is acceptable in case of
	 * english language but is not acceptable in case of multi languages
	 * support. Reason:There exists no standard that defines how query
	 * string must be encoded before transmitting and hence app-server
	 * developers left the encoding of the parameters in the default
	 * scheme suggested.But character encoding is set properly in case
	 * of POST requests as there exists
	 * standard(application/x-www-form-urlencoded) that defines the
	 * encoding of parameters transmitted as part of request body.
	 *
	 * Solution:Obtain the parameters in the default encoding
	 * scheme(ISO-8859-1) Then get the bytes from the string by using
	 * string.getBytes(encoding) method.Here string.getBytes() method is
	 * avoided as it returns the bytes using java's default encoding of
	 * String i.e UTF16(Big Endian-MSB first) resulting in data
	 * corruption.The encoding pass to getBytes(encoding) is ISO-8859-1
	 * Then a new decoded string in required format(utf-8)is constructed
	 * by using String decodedString=new
	 * String(q.getBytes("ISO-8859-1"),"utf-8") which gives us the
	 * unicode string using utf-8 encoding. Also before passing this
	 * string for further processing the trim() method must be called to
	 * remove leading and trailing whitespaces. For better customization
	 * default character encoding can be set as a custom parameter.
	 * Similar changes should be made to all the methods. Issue:Need to
	 * think of a better approach in case of
	 * exception(UnsupportedEncodingException).At present just returning
	 * an empty collection.
	 */
	// @RequestMapping(value = "/constituencies", method = RequestMethod.GET)
	// public String getConstituenciesStartingWith(final ModelMap map,
	// final HttpServletRequest request, final Locale locale) {
	// String param = request.getParameter("term");
	// String decodedString = null;
	// List<Reference> constituencies = new ArrayList<Reference>();
	// try {
	// decodedString = new String(param.getBytes("ISO-8859-1"), "UTF-8");
	// ConstituencyRepository cr = new ConstituencyRepository();
	// constituencies = cr.findConstituenciesRefStartingWith(
	// decodedString.trim(), locale.toString());
	// map.addAttribute("results", constituencies);
	// return "constituencies";
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// // this is done so as to have a graceful degradation in case of
	// // exceptions
	// map.addAttribute("results", constituencies);
	// return "constituencies";
	// }
	// }

	/**
	 * Gets the districts by constituency id.
	 *
	 * @param constituencyName the constituency name
	 * @param map the map
	 * @param request the request
	 * @param locale the locale
	 * @return the districts by constituency id Issue:The method findByName in
	 *         constituency should contain another parameter locale. Alternate
	 *         Explaination(Need Discussion):Since in the constituency
	 *         controller there is a check to prevent constituency with same
	 *         names and it is locale based so the names will be unique in all
	 *         locale and hence need for second parameter seems meaningless.But
	 *         still need discussions.
	 */
	@RequestMapping(value = "/data/{constituency_name}/districts",
			method = RequestMethod.GET)
			public @ResponseBody
			List<District> getDistrictsByConstituencyId(
					@PathVariable("constituency_name") final String constituencyName,
					final ModelMap map, 
					final HttpServletRequest request,
					final Locale locale) {
		String decodedString = null;
		List<District> districts = new ArrayList<District>();
		try {
			CustomParameter customParameter = CustomParameter.findByName(
					CustomParameter.class, "DEFAULT_URI_ENCODING",
					locale.toString());
			CustomParameter customParameter1 = CustomParameter.findByName(
					CustomParameter.class, "DEFAULT_ENCODING",
					locale.toString());
			decodedString = new String(
					constituencyName.getBytes(customParameter.getValue()),
					customParameter1.getValue());
			Constituency constituency = Constituency
			.findByName(Constituency.class, decodedString.trim(),
					locale.toString());
			districts = District.findDistrictsByConstituencyId(constituency.getId(),
					"name", "ASC");
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (ELSException e) {
			map.addAttribute("REFERENCE_CONTROLLER", "Request can not be completed at the moment.");
		}
		return districts; 
	}

	/**
	 * Gets the last date.
	 *
	 * @param houseid the houseid
	 * @return the last date
	 */
	@RequestMapping(value="/{house}/firstdate")
	public Reference getLastDate(@PathVariable("house") final Long houseid){
		Reference reference=new Reference();
		House house=House.findById(House.class, houseid);
		reference.setId(house.getFirstDate().toString());
		reference.setName(house.getFirstDate().toString());
		return reference;
	}

	/**
	 * Gets the sub departments by department.
	 *
	 * @param department the department
	 * @param map the map
	 * @param locale the locale
	 * @return the sub departments by department
	 */
	@RequestMapping(value="department/{department}/subDepartments",method=RequestMethod.GET)
	public @ResponseBody List<SubDepartment> getSubDepartmentsByDepartment(
			@PathVariable("department") final Long department,
			final ModelMap map,
			final Locale locale){
		Department dept=Department.findById(Department.class, department);
		List<SubDepartment> subDepartments=SubDepartment.findAllByFieldName(SubDepartment.class, "department", dept, "name", ApplicationConstants.ASC, locale.toString());
		return subDepartments;
	}

	/**
	 * Gets the member full name.
	 *
	 * @param memberId the member id
	 * @return the member full name
	 */
	@RequestMapping(value="member/{memberId}/fullName", method=RequestMethod.GET)
	public @ResponseBody Reference getMemberFullName(@PathVariable("memberId") final Long memberId){
		Member member = Member.findById(Member.class, memberId);
		Reference reference = new Reference();
		reference.setId(member.getId().toString());
		reference.setName(member.getFullname());
		return reference;
	}

	/**
	 * Gets the member death date.
	 *
	 * @param memberId the member id
	 * @param locale the locale
	 * @return the member death date
	 */
	@RequestMapping(value="member/{memberId}/deathDate", method=RequestMethod.GET)
	public @ResponseBody Reference getMemberDeathDate(@PathVariable("memberId") final Long memberId,
			final Locale locale){
		Member member = Member.findById(Member.class, memberId);
		Reference reference = new Reference();
		reference.setId(member.getId().toString());
		reference.setName("");
		Date deathDate = member.getDeathDate();
		if(deathDate != null){
			// Convert the date(in en_US) to the present locale
			SimpleDateFormat sdf = FormaterUtil.getDateFormatter(locale.toString());
			reference.setName(sdf.format(deathDate));
		}
		return reference;
	}

	/**
	 * Gets the division districts by constituency.
	 *
	 * @param constituency the constituency
	 * @param locale the locale
	 * @return the division districts by constituency
	 */
	@RequestMapping(value="divdis/{constituency}", method=RequestMethod.GET)
	public @ResponseBody ConstituencyCompleteVO getDivisionDistrictsByConstituency(	@PathVariable("constituency") final Long constituency,
			final Locale locale){
		Constituency selectedConstituency=Constituency.findById(Constituency.class,constituency);
		ConstituencyCompleteVO constituencyCompleteVO=new ConstituencyCompleteVO();
		List<MasterVO> districtsToPopulate=new ArrayList<MasterVO>();
		if(!selectedConstituency.getDistricts().isEmpty()){
			if(selectedConstituency.getDistricts().get(0).getDivision()!=null){
				constituencyCompleteVO.setDivision(selectedConstituency.getDistricts().get(0).getDivision().getName());
			}
		}
		List<District> districts=selectedConstituency.getDistricts();
		for(District i:districts){
			districtsToPopulate.add(new MasterVO(i.getId(), i.getName()));
		}
		constituencyCompleteVO.setDistricts(districtsToPopulate);
		return constituencyCompleteVO;
	}

	/**
	 * Gets the houses by type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the houses by type
	 */
	@RequestMapping(value="houses/{houseType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getHousesByType(@PathVariable("houseType") final String houseType,
			final Locale locale){
		HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
		List<House> houses=House.findAllByFieldName(House.class, "type",selectedHouseType, "firstDate",ApplicationConstants.DESC, locale.toString());
		List<MasterVO> housesVOs=new ArrayList<MasterVO>();
		for(House i:houses){
			housesVOs.add(new MasterVO(i.getId(), i.getDisplayName()));
		}
		return housesVOs;
	}

	/**
	 * Gets the member roles by type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the member roles by type
	 */
	@RequestMapping(value="memberroles/{houseType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMemberRolesByType(@PathVariable("houseType") final String houseType,
			final Locale locale){
		HouseType selectedRole=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
		List<MemberRole> roles=MemberRole.findAllByFieldName(MemberRole.class, "houseType",selectedRole, "name",ApplicationConstants.ASC, locale.toString());
		List<MasterVO> rolesVOs=new ArrayList<MasterVO>();
		for(MemberRole i:roles){
			rolesVOs.add(new MasterVO(i.getId(), i.getName()));
		}
		return rolesVOs;
	}

	/**
	 * Gets the elections by type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the elections by type
	 */
	@RequestMapping(value="elections/{houseType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getElectionsByType(@PathVariable("houseType") final String houseType,
			final Locale locale){
		List<MasterVO> rolesVOs=new ArrayList<MasterVO>();
		
		try{
			List<Election> elections=Election.findByHouseType(houseType, locale.toString());
			for(Election i:elections){
				rolesVOs.add(new MasterVO(i.getId(), i.getName()));
			}
		}catch (ELSException e) {
			logger.error(e.getMessage());
		}
		return rolesVOs;
	}

	/**
	 * Gets the constituencies by type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the constituencies by type
	 */
	@RequestMapping(value="constituencies/{houseType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getConstituenciesByType(@PathVariable("houseType") final String houseType,
			final Locale locale){
		List<MasterVO> constituenciesVOs = new ArrayList<MasterVO>();
		try{
			constituenciesVOs = Constituency.findAllByHouseType(houseType, locale.toString());
		}catch (ELSException e) {
			e.printStackTrace();
		}
		return constituenciesVOs;
	}

	/**
	 * Gets the election type.
	 *
	 * @param electionId the election id
	 * @param locale the locale
	 * @return the election type
	 */
	@RequestMapping(value="election/{electionId}/electionType", method=RequestMethod.GET)
	public @ResponseBody Reference getElectionType(@PathVariable("electionId") final Long electionId,
			final Locale locale) {
		Election election = Election.findById(Election.class, electionId);
		ElectionType electionType = election.getElectionType();
		Reference reference = new Reference();
		reference.setId(String.valueOf(electionType.getId()));
		reference.setName(electionType.getName());
		return reference;
	}

	/**
	 * Gets the district.
	 *
	 * @param tehsilId the tehsil id
	 * @param locale the locale
	 * @return the district
	 */
	@RequestMapping(value="district/{tehsilId}", method=RequestMethod.GET)
	public @ResponseBody Reference getDistrict(@PathVariable("tehsilId") final Long tehsilId,
			final Locale locale) {
		Tehsil tehsil=Tehsil.findById(Tehsil.class,tehsilId);
		Reference reference = new Reference();
		reference.setId(String.valueOf(tehsil.getId()));
		reference.setName(tehsil.getName());
		return reference;
	}

	/**
	 * Gets the group vo.
	 *
	 * @param group the group
	 * @param locale the locale
	 * @return the group vo
	 */
	@RequestMapping(value="/ministrydeptsubdeptdates/{group}", method=RequestMethod.GET)
	public @ResponseBody GroupVO getGroupVO(
			@PathVariable("group") final Long group,
			final Locale locale
	) {
		Group selectedGroup=Group.findById(Group.class, group);
		GroupVO groupVO=new GroupVO();
		//populating ministries
		List<Ministry> ministries=selectedGroup.getMinistries();
		List<MasterVO> ministriesVOs=new ArrayList<MasterVO>();
		for(Ministry i:ministries){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			ministriesVOs.add(masterVO);
		}
		groupVO.setMinistries(ministriesVOs);
		//populating departments
		List<Department> departments=MemberMinister.findAssignedDepartments(ministries.get(0),locale.toString());
		List<MasterVO> departmentVOs=new ArrayList<MasterVO>();
		for(Department i:departments){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			departmentVOs.add(masterVO);
		}
		groupVO.setDepartments(departmentVOs);
		//populating subdepartments
		if(departments.size()>0){
			List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministries.get(0), departments.get(0), locale.toString());
			List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
			for(SubDepartment i:subDepartments){
				MasterVO masterVO=new MasterVO();
				masterVO.setId(i.getId());
				masterVO.setName(i.getName());
				subDepartmentVOs.add(masterVO);
			}
			groupVO.setSubDepartments(subDepartmentVOs);
		}
		//populating answering dates
		List<QuestionDates> dates=selectedGroup.getQuestionDates();
		List<Reference> answeringDates=new ArrayList<Reference>();
		for(QuestionDates i:dates){
			Date ansDate=i.getAnsweringDate();
			if(ansDate!=null){
				String strAnsweringDate=FormaterUtil.getDateFormatter(ApplicationConstants.SERVER_DATEFORMAT,locale.toString()).format(ansDate);
				Reference answeringDateVO=new Reference(strAnsweringDate,strAnsweringDate);
				answeringDates.add(answeringDateVO);
			}
		}
		groupVO.setAnsweringDates(answeringDates);
		return groupVO;
	}

	/**
	 * Gets the departments.
	 *
	 * @param ministry the ministry
	 * @param locale the locale
	 * @return the departments
	 */
	@RequestMapping(value="/departments/{ministry}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getDepartments(
			@PathVariable("ministry") final Long ministry,
			final Locale locale){
		Ministry selectedMinistry=Ministry.findById(Ministry.class, ministry);
		//populating departments
		List<Department> departments=MemberMinister.findAssignedDepartments(selectedMinistry, locale.toString());
		List<MasterVO> departmentVOs=new ArrayList<MasterVO>();
		for(Department i:departments){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			departmentVOs.add(masterVO);
		}
		return departmentVOs;
	}

	/**
	 * Gets the departments by ministry name.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the departments by ministry name
	 */
	@RequestMapping(value="/departments/byministriesname", method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> getDepartmentsByMinistryName(
			final HttpServletRequest request,
			final Locale locale){
		String[] strMinistries=request.getParameterValues("ministries[]");
		List<MasterVO> departmentVOs=new ArrayList<MasterVO>();
		List<Department> departments=MemberMinister.findAssignedDepartments(strMinistries,locale.toString());
		for(Department i:departments){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			departmentVOs.add(masterVO);
		}
		return departmentVOs;
	}

	/**
	 * Gets the sub departments.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the sub departments
	 */
	@RequestMapping(value="/subdepartments/byministriesdepartmentsname", method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> getSubDepartments(
			final HttpServletRequest request,
			final Locale locale){
		String[] ministries=request.getParameterValues("ministries[]");
		String[] departments=request.getParameterValues("departments[]");
		//populating sub departments
		List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministries,departments, locale.toString());
		List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
		for(SubDepartment i:subDepartments){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			subDepartmentVOs.add(masterVO);
		}
		return subDepartmentVOs;
	}

	/**
	 * Gets the sub departments by ministry department names.
	 *
	 * @param ministry the ministry
	 * @param department the department
	 * @param locale the locale
	 * @return the sub departments by ministry department names
	 */
	@RequestMapping(value="/subdepartments/{ministry}/{department}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getSubDepartmentsByMinistryDepartmentNames(
			@PathVariable("ministry") final Long ministry,
			@PathVariable("department") final Long department,
			final Locale locale){
		Ministry selectedMinistry=Ministry.findById(Ministry.class, ministry);
		Department selectedDepartment=Department.findById(Department.class,department);
		//populating sub departments
		List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(selectedMinistry,selectedDepartment, locale.toString());
		List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
		for(SubDepartment i:subDepartments){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			subDepartmentVOs.add(masterVO);
		}
		return subDepartmentVOs;
	}

	/**
	 * Gets the session.
	 *
	 * @param locale the locale
	 * @param houseType the house type
	 * @param sessionYear the session year
	 * @param sessionType the session type
	 * @return the session
	 */
	@RequestMapping(value="/session/{houseType}/{sessionYear}/{sessionType}", method=RequestMethod.GET)
	public @ResponseBody MasterVO getSession(
			final Locale locale,
			@PathVariable("houseType")final Long houseType,
			@PathVariable("sessionYear")final Integer sessionYear,
			@PathVariable("sessionType") final Long sessionType
	) {
		try {
			//populating departments
			HouseType selectedHouseType=HouseType.findById(HouseType.class, houseType);
			SessionType selectedSessionType=SessionType.findById(SessionType.class, sessionType);
			Session selectedSession=Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, sessionYear);
			
			MasterVO masterVO=new MasterVO(selectedSession.getId(),"");
			return masterVO;
			
		} catch (ELSException e) {
			e.printStackTrace();
		}
		return new MasterVO();
	}

	/**
	 * Gets the members.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @param session the session
	 * @param model the model
	 * @return the members
	 */
	@RequestMapping(value="/members",method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getMembers(final HttpServletRequest request,
			final Locale locale,
			@RequestParam("session")final Long session
			,final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> memberVOs=new ArrayList<MasterVO>();
		List<AutoCompleteVO> autoCompleteVOs=new ArrayList<AutoCompleteVO>();
		Session selectedSession=Session.findById(Session.class,session);
		House house=selectedSession.getHouse();
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				String strParam=request.getParameter("term");
				try {
					String param=new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
					memberVOs=HouseMemberRoleAssociation.findAllActiveMemberVOSInSession(house, selectedSession, locale.toString(), param);
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				String param=request.getParameter("term");
				memberVOs=HouseMemberRoleAssociation.findAllActiveMemberVOSInSession(house, selectedSession, locale.toString(), param);
			}
		}
		for(MasterVO i:memberVOs){
			AutoCompleteVO autoCompleteVO=new  AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}

		return autoCompleteVOs;
	}

	/**
	 * Gets the supporting members.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @param session the session
	 * @param model the model
	 * @return the supporting members
	 */
	@RequestMapping(value="/supportingmembers",method=RequestMethod.GET)
	public @ResponseBody List<DynamicSelectVO> getSupportingMembers(final HttpServletRequest request,
			final Locale locale,
			@RequestParam("session")final Long session
			,final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> memberVOs=new ArrayList<MasterVO>();
		List<DynamicSelectVO> dynamicSelectVOs=new ArrayList<DynamicSelectVO>();
		Session selectedSession=Session.findById(Session.class,session);
		House house=selectedSession.getHouse();
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				String strParam=request.getParameter("tag");
				try {
					String param=new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
					memberVOs=HouseMemberRoleAssociation.findAllActiveMemberVOSInSession(house, selectedSession, locale.toString(), param);
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				String param=request.getParameter("tag");
				memberVOs=HouseMemberRoleAssociation.findAllActiveMemberVOSInSession(house, selectedSession, locale.toString(), param);
			}
		}
		for(MasterVO i:memberVOs){
			DynamicSelectVO dynamicSelectVO=new  DynamicSelectVO();
			dynamicSelectVO.setKey(i.getId());
			dynamicSelectVO.setValue(i.getName());
			dynamicSelectVOs.add(dynamicSelectVO);
		}
		return dynamicSelectVOs;
	}

	/**
	 * Gets the member constituency.
	 *
	 * @param memberid the memberid
	 * @param locale the locale
	 * @param session the session
	 * @return the member constituency
	 */
	@RequestMapping(value="/member/{memberid}/{constituency}", method=RequestMethod.GET)
	public @ResponseBody MasterVO getMemberConstituency(
			@PathVariable("memberid") final Long memberid,
			final Locale locale,
			@RequestParam("session") final Long session){
		Session selectedSession=Session.findById(Session.class,session);
		House selectedHouse=selectedSession.getHouse();
		Long house=selectedHouse.getId();
		HouseType houseType=selectedHouse.getType();
		String sessionStartDate="";
		String sessionEndDate="";
		MasterVO masterVO=new MasterVO();
		if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
			if(selectedSession.getStartDate()!=null&&selectedSession.getEndDate()!=null){
				sessionStartDate=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").format(selectedSession.getStartDate());
				sessionEndDate=FormaterUtil.getDateFormatter("dd/MM/yyyy", "en_US").format(selectedSession.getEndDate());
				masterVO=Member.findConstituencyByCouncilDates(memberid, house,"RANGE",sessionStartDate,sessionEndDate);
			}
		}
		else if(houseType.equals(ApplicationConstants.LOWER_HOUSE)){
			masterVO=Member.findConstituencyByAssemblyId(memberid, house);
		}
		return masterVO;
	}

	/**
	 * Show titles.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value = "titles", method = RequestMethod.GET)
	public @ResponseBody String showTitles(final ModelMap model, 
			final HttpServletRequest request, 
			final Locale locale) {
		Grid grid = null;
		try{
			grid = Grid.findByDetailView("house", locale.toString());
		
			model.addAttribute("gridId", grid.getId());
			model.addAttribute("houseType", this.getCurrentUser().getHouseType());
			model.addAttribute("messagePattern", "house");
			model.addAttribute("urlPattern", "house");
			return "house/list";
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
			return "home";
		}
	}

	/**
	 * Gets the houses by house type.
	 *
	 * @param houseTypeId the house type id
	 * @param map the map
	 * @param locale the locale
	 * @return the houses by house type
	 */
	@RequestMapping(value = "/{houseType}/house",
			method = RequestMethod.GET)
			public @ResponseBody
			List<House> getHousesByHouseType(
					@PathVariable("houseType") final Long houseTypeId,
					final ModelMap map, 
					final Locale locale) {
		List<House> houses = new ArrayList<House>();
		HouseType houseType = HouseType.findById(HouseType.class, houseTypeId);
		if (houseType != null) {
			houses = House.findAllByFieldName(House.class, "type",
					houseType, "firstDate", ApplicationConstants.DESC, locale.toString());
		}

		return houses;
	}

	/*
	 * loadGroups,loadDepartments and loadSubDepartments are used in user group jsp
	 */
	/**
	 * Load groups.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list< master v o>
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value="/groups")
	public @ResponseBody List<MasterVO> loadGroups(final HttpServletRequest request,
			final Locale locale){
		//CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		List<Group> groups=new ArrayList<Group>();
		String strhouseType=request.getParameter("houseType");
		String stryear=request.getParameter("year");
		String strsessionType=request.getParameter("sessionType");
		if(strhouseType!=null&&stryear!=null&&strsessionType!=null){
			HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class, "type",strhouseType, locale.toString());
			SessionType selectedSessionType=SessionType.findById(SessionType.class, Long.parseLong(strsessionType));
			Integer year=Integer.parseInt(stryear);
			try {
				groups=Group.findByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
			} catch (ELSException e) {
				e.printStackTrace();
				return masterVOs;
			}
			for(Group i:groups){
				MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
				masterVOs.add(masterVO);
			}
		}
		return masterVOs;
	}

	/**
	 * Load allowed groups.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list
	 */
	@RequestMapping(value="/allowedgroups")
	public @ResponseBody List<MasterVO> loadAllowedGroups(final HttpServletRequest request,
			final Locale locale){
		//CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		List<Group> groups=new ArrayList<Group>();
		String strhouseType=request.getParameter("houseType");
		String stryear=request.getParameter("sessionYear");
		String strsessionType=request.getParameter("sessionType");
		String strAllowedGroups=request.getParameter("allowedgroups");
		if(strhouseType!=null&&stryear!=null&&strsessionType!=null&&strAllowedGroups!=null){
			HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",strhouseType,locale.toString());
			SessionType selectedSessionType=SessionType.findById(SessionType.class, Long.parseLong(strsessionType));
			Integer year=Integer.parseInt(stryear);
			try {
				groups=Group.findByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
			
				for(Group i:groups){
					if(strAllowedGroups.contains(String.valueOf(i.getNumber()))){
						MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
						masterVOs.add(masterVO);
					}
				}
			} catch (ELSException e) {
				e.printStackTrace();
				return masterVOs;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return masterVOs;
	}


	/**
	 * Load departments.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list< master v o>
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value="/departments")
	public @ResponseBody List<MasterVO> loadDepartments(final HttpServletRequest request,
			final Locale locale){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		if(customParameter!=null){
			String server=customParameter.getValue();
			String strhouseType=request.getParameter("housetype");
			String stryear=request.getParameter("year");
			String strsessionType=request.getParameter("sessiontype");
			String strlocale=locale.toString();
			String strGroup=request.getParameter("group");
			String houseType=strhouseType;
			String sessionType=strsessionType;
			if(server.equals("TOMCAT")){
				try {
					houseType=new String(strhouseType.getBytes("ISO-8859-1"),"UTF-8");
					sessionType=new String(strsessionType.getBytes("ISO-8859-1"),"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
			}
			HouseType selectedHouseType=HouseType.findByName(HouseType.class, houseType, strlocale);
			SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", sessionType, strlocale);
			Integer year=Integer.parseInt(stryear);
			String[] delimitedgroups=strGroup.split(",");
			Integer[] newgroups=new Integer[delimitedgroups.length];
			for(int i=0;i<delimitedgroups.length;i++){
				newgroups[i]=Integer.parseInt(delimitedgroups[i]);
			}
			masterVOs=MemberMinister.findAssignedDepartmentsVO(newgroups,selectedHouseType,selectedSessionType,year, strlocale);
		}
		return masterVOs;
	}

	/**
	 * Load sub departments.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list< master v o>
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value="/subdepartments")
	public @ResponseBody List<MasterVO> loadSubDepartments(final HttpServletRequest request,
			final Locale locale){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		if(customParameter!=null){
			String server=customParameter.getValue();
			String strhouseType=request.getParameter("housetype");
			String stryear=request.getParameter("year");
			String strsessionType=request.getParameter("sessiontype");
			String strlocale=locale.toString();
			String strGroup=request.getParameter("group");
			String houseType=strhouseType;
			String sessionType=strsessionType;
			if(server.equals("TOMCAT")){

				try {
					houseType=new String(strhouseType.getBytes("ISO-8859-1"),"UTF-8");
					sessionType=new String(strsessionType.getBytes("ISO-8859-1"),"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
			}
			HouseType selectedHouseType=HouseType.findByName(HouseType.class, houseType, strlocale);
			SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", sessionType, strlocale);
			Integer year=Integer.parseInt(stryear);
			String[] delimitedgroups=strGroup.split(",");
			Integer[] newgroups=new Integer[delimitedgroups.length];
			for(int i=0;i<delimitedgroups.length;i++){
				newgroups[i]=Integer.parseInt(delimitedgroups[i]);
			}
			masterVOs=MemberMinister.findAssignedSubDepartmentsVO(newgroups,selectedHouseType,selectedSessionType,year, strlocale);
		}
		return masterVOs;
	}

	/**
	 * Load sub departments by dept names.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the list< master v o>
	 * @author compaq
	 * @since v1.0.0
	 */
	@RequestMapping(value="/departments/subdepartments")
	public @ResponseBody List<MasterVO> loadSubDepartmentsByDeptNames(final HttpServletRequest request,
			final Locale locale){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		if(customParameter!=null){
			String server=customParameter.getValue();
			String strhouseType=request.getParameter("housetype");
			String stryear=request.getParameter("year");
			String strsessionType=request.getParameter("sessiontype");
			String strlocale=locale.toString();
			String strGroup=request.getParameter("group");
			String houseType=strhouseType;
			String sessionType=strsessionType;
			String strDepartment=request.getParameter("department");
			String department=strDepartment;
			if(server.equals("TOMCAT")){
				try {
					houseType=new String(strhouseType.getBytes("ISO-8859-1"),"UTF-8");
					sessionType=new String(strsessionType.getBytes("ISO-8859-1"),"UTF-8");
					department=new String(strDepartment.getBytes("ISO-8859-1"),"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
			}
			String[] departments=department.split(",");
			HouseType selectedHouseType=HouseType.findByName(HouseType.class, houseType, strlocale);
			SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", sessionType, strlocale);
			Integer year=Integer.parseInt(stryear);
			String[] delimitedgroups=strGroup.split(",");
			Integer[] newgroups=new Integer[delimitedgroups.length];
			for(int i=0;i<delimitedgroups.length;i++){
				newgroups[i]=Integer.parseInt(delimitedgroups[i]);
			}
			masterVOs=MemberMinister.findAssignedSubDepartmentsVO(newgroups,departments,selectedHouseType,selectedSessionType,year, strlocale);
		}
		return masterVOs;
	}

	/**
	 * Gets the group.
	 *
	 * @param request the request
	 * @param ministryId the ministry id
	 * @param locale the locale
	 * @return the group
	 */
	@RequestMapping(value="/ministry/{ministryId}/group")
	public @ResponseBody MasterVO getGroup(final HttpServletRequest request,
			@PathVariable("ministryId") final Long ministryId,
			final Locale locale){
		Ministry ministry=Ministry.findById(Ministry.class, ministryId);
		HouseType houseType=HouseType.findById(HouseType.class,Long.parseLong(request.getParameter("houseType")));
		Integer sessionYear=Integer.parseInt(request.getParameter("sessionYear"));
		SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(request.getParameter("sessionType")));
		Group group;
		MasterVO masterVO=new MasterVO();
		try {
			group = Group.find(ministry, houseType, sessionYear, sessionType, locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
			return masterVO;
					
		}
		if(group!=null){
			masterVO.setId(group.getId());
			masterVO.setName(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(group.getNumber()));
		}
		return masterVO;
	}

	/**
	 * Gets the answering dates.
	 *
	 * @param request the request
	 * @param groupId the group id
	 * @param locale the locale
	 * @return the answering dates
	 */
	@RequestMapping(value="/group/{groupId}/answeringdates")
	public @ResponseBody List<MasterVO> getAnsweringDates(final HttpServletRequest request,
			@PathVariable("groupId") final Long groupId,
			final Locale locale){
		Group group=Group.findById(Group.class,groupId);
		List<QuestionDates> answeringDates=group.getQuestionDates();
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		for(QuestionDates i:answeringDates){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(FormaterUtil.getDateFormatter(locale.toString()).format(i.getAnsweringDate()));
			masterVOs.add(masterVO);
		}
		return masterVOs;
	}

	/**
	 * Gets the ministries.
	 *
	 * @param request the request
	 * @param sessionId the session id
	 * @param locale the locale
	 * @return the ministries
	 */
	@RequestMapping(value="/session/{sessionId}/ministries")
	public @ResponseBody List<MasterVO> getMinistries(final HttpServletRequest request,
			@PathVariable("sessionId") final Long sessionId,
			final Locale locale){
		Session session=Session.findById(Session.class,sessionId);
		List<Ministry> ministries;
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		
		try {
			ministries = Ministry.findMinistriesAssignedToGroups(session.getHouse().getType(), session.getYear(), session.getType(), locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
			return masterVOs;
			
		}
		for(Ministry i:ministries){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			masterVOs.add(masterVO);
		}
		return masterVOs;
	}

	/**
	 * Gets the question search.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the question search
	 */



	/**
	 * Find actors.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the list< reference>
	 * @since v1.0.0
	 */
	@RequestMapping(value="/question/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		List<Reference> actors=new ArrayList<Reference>();
		String strQuestion=request.getParameter("question");
		String strInternalStatus=request.getParameter("status");
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		if(strQuestion!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null){
			if((!strQuestion.isEmpty())&&(!strInternalStatus.isEmpty())&&
					(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())){
				Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
				Question question=Question.findById(Question.class,Long.parseLong(strQuestion));
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
				actors=WorkflowConfig.findQuestionActorsVO(question,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
			}
		}
		return actors;
	}

	/**
	 * Find latest workflow config.
	 *
	 * @param deviceTypesEnabled the device types enabled
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the reference
	 * @author compaq
	 * @since v1.0.0
	 */

	@RequestMapping(value = "/{deviceTypesEnabled}/deviceTypesNeedBallot", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getDeviceTypesNeedBallot(@PathVariable("deviceTypesEnabled") final String deviceTypesEnabled,
			final HttpServletRequest request, 
			final ModelMap model, 
			final Locale locale) {
		List<Reference> deviceTypesRef = new ArrayList<Reference>();
		for(String deviceTypeEnabled : deviceTypesEnabled.split(",")) {
			DeviceType deviceType = DeviceType.findByType(deviceTypeEnabled, locale.toString());
			Reference reference = new Reference(deviceType.getType(), deviceType.getName());
			deviceTypesRef.add(reference);
		}
		return deviceTypesRef;
	}

	//    @RequestMapping(value = "/admittedstarreduh", method = RequestMethod.GET)
	//    public @ResponseBody List<MasterVO> getAdmittedStarredUH(final HttpServletRequest request, final ModelMap model, final Locale locale) {
	//        String strDeviceType=request.getParameter("deviceType");
	//        String strSession=request.getParameter("session");
	//        String strMember=request.getParameter("member");
	//        List<MasterVO> masterVOs=new ArrayList<MasterVO>();
	//        if(strDeviceType!=null&&strSession!=null&&strMember!=null){
	//            Session session=Session.findById(Session.class, Long.parseLong(strSession));
	//            DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
	//            Member member=Member.findById(Member.class, Long.parseLong(strMember));
	//            masterVOs=Question.findAdmittedStarredQuestionsUH(session,deviceType,member,locale.toString());
	//        }
	//        return masterVOs;
	//    }

	/**
	 * Gets the answering dates.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the answering dates
	 */
	@RequestMapping(value = "/answeringDates", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getAnsweringDates(final HttpServletRequest request, 
			final ModelMap model, 
			final Locale locale) {
		String strQuestion=request.getParameter("question");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		if(strQuestion!=null){
			Question question=Question.findById(Question.class,Long.parseLong(strQuestion));
			List<QuestionDates> dates=question.getGroup().getQuestionDates();
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"SERVER_DATEFORMAT", "");
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(), locale.toString());
				for(QuestionDates i:dates){
					MasterVO masterVO=new MasterVO(i.getId(),format.format(i.getAnsweringDate()));
					masterVOs.add(masterVO);
				}
			}else{
				logger.error("Custom Parameter 'SERVER_DATEFORMAT' not set");
			}
		}
		return masterVOs;
	}

	/**
	 * Gets the house by type.
	 *
	 * @param houseType the house type
	 * @param locale the locale
	 * @return the house by type
	 */
	@RequestMapping(value="{houseType}/houses", method=RequestMethod.GET)
	public @ResponseBody List<House> getHouseByType(@PathVariable("houseType") final String houseType,
			final Locale locale){
		HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
		List<House> houses=House.findAllByFieldName(House.class, "type",selectedHouseType, "firstDate",ApplicationConstants.DESC, locale.toString());
		return houses;
	}

	//---------------------------Added by vikas & dhananjay-------------------------------------
	/**
	 * Gets the discussion dates.
	 *
	 * @param id to find the session
	 * @param discussionDays days submitted by user
	 * @return List<Reference> of Dates on which submitted days come
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value="/session/{id}/devicetypeconfig/{discussionDays}/discussiondates", method=RequestMethod.GET)
	public @ResponseBody List<Reference> getDiscussionDates(@PathVariable("id") final Long id, 
			@PathVariable("discussionDays") final String discussionDays){

		String[] days = discussionDays.split(",");

		Session domain = Session.findById(Session.class, id);

		//------------------find dates---------------------------


		Date sessionStartDate= domain.getStartDate();
		Date sessionEndDate=domain.getEndDate();
		List<Reference> references = new ArrayList<Reference>();

		if((sessionStartDate != null) && (sessionStartDate != null)){
			Calendar start = Calendar.getInstance();

			Calendar end = Calendar.getInstance();

			List<Date> dates = new ArrayList<Date>();

			SimpleDateFormat sf=new SimpleDateFormat("EEEE");
			CustomParameter parameter = CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
			SimpleDateFormat dateFormat = null;

			SimpleDateFormat dateFormatEn_US = null;
			if(parameter != null){
				dateFormatEn_US = FormaterUtil.getDateFormatter(parameter.getValue(), domain.getLocale());
			}

			dateFormat = FormaterUtil.getDateFormatter(domain.getLocale());

			for(String day: days){			

				start.setTime(sessionStartDate);
				end.setTime(sessionEndDate);


				for (; !start.after(end); start.add(Calendar.DATE, 1)) {
					Date current = start.getTime();
					String select="false";

					if(sf.format(current).equals(day)){

						dates.add(current);
					}
				}
			}
			//--------------------------------------------------------

			Collections.sort(dates);

			for(Date date: dates){

				Reference reference = new Reference();

				reference.setId(dateFormat.format(date));
				reference.setName(dateFormat.format(date));

				references.add(reference);    
			}
		}
		return references;
	}

	//---------------------------Added by vikas & dhananjay-------------------------------------
	/**
	 * Gets the session config answering dates.
	 *
	 * @param id to find the session
	 * @return List<Reference> of Dates on which submitted days come
	 */
	@RequestMapping(value="/session/{id}/devicetypeconfig/discussiondates", method=RequestMethod.GET)
	public @ResponseBody
	List<MasterVO> getSessionConfigAnsweringDates(@PathVariable("id") final Long id) {

		Session session = Session.findById(Session.class, id);
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();

		if (session != null) {

			String[] dates = session.getParameter("questions_halfhourdiscussion_from_question_discussionDates").split("#");

			try {
				for (int i = 0; i < dates.length; i++) {

					Date date = FormaterUtil.getDateFormatter("en_US").parse(dates[i]);

					MasterVO masterVO = new MasterVO();
					masterVO.setId(new Long(i));
					masterVO.setName(FormaterUtil.getDateFormatter(session.getLocale().toString()).format(date));
					masterVOs.add(masterVO);
				}
			} catch (ParseException e) {

				logger.error("session does not exist.");
			}

		}

		return masterVOs;
	} 

	//---------------------------Added by vikas & dhananjay 20012013-------------------------------------
	/**
	 * Gets the question id.
	 *
	 * @param model the model
	 * @param request the request
	 * @return the question id
	 */
	@RequestMapping(value="/questionid",method=RequestMethod.GET)
	public @ResponseBody MasterVO getQuestionId(final ModelMap model, final HttpServletRequest request){

		MasterVO masterVO = new MasterVO();

		String strNumber=request.getParameter("strQuestionNumber");
		String strSessionId = request.getParameter("strSessionId");
		String strDeviceTypeId= request.getParameter("deviceTypeId");
		String locale = request.getParameter("locale");

		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");

		Integer qNumber=null;
		Long deviceTypeId=null;

		if(strNumber!=null && strSessionId!=null && locale!=null && strDeviceTypeId!=null){
			if(strNumber.trim().length() > 0 && locale.trim().length() > 0 && strSessionId.trim().length() > 0 && strDeviceTypeId.length()>0){
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							strNumber=new String(strNumber.getBytes("ISO-8859-1"),"UTF-8");						
						}
						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					try {
						qNumber=new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale).parse(strNumber).intValue());
						deviceTypeId = new Long(strDeviceTypeId);
					} catch (ParseException e) {
						logger.error("Number parse exception.");
						masterVO.setId(new Long(-1));
						masterVO.setName("undefined");

						return masterVO;
					}
				}	

				Session currentSession = Session.findById(Session.class, new Long(strSessionId));
				DeviceType excludeDeviceType = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE, locale);
				Long exclusiveDeviceTypeId = null;
				Session prevSession = null;
				Question question = null;

				if(excludeDeviceType != null){
					if(excludeDeviceType.getId()!= null){
						exclusiveDeviceTypeId = excludeDeviceType.getId();
					}
				}

				if(currentSession != null){						
					try {
						prevSession = Session.findPreviousSession(currentSession);
					} catch (ELSException e) {
						model.addAttribute("error", e.getParameter());
					}
					question = Question.findQuestionExcludingGivenDeviceTypes(currentSession, qNumber, locale, deviceTypeId, exclusiveDeviceTypeId);
				}

				if(question == null){
					if(prevSession != null){
						question = Question.findQuestionExcludingGivenDeviceTypes(prevSession, qNumber, locale, deviceTypeId, exclusiveDeviceTypeId);
					}
				}
				
				
				if(question != null){
					masterVO.setId(question.getId());
					masterVO.setName(question.getId().toString());
				}else{
					masterVO.setId(new Long(0));
					masterVO.setName("undefined");				
				}
				
			}
		}

		return masterVO;
	}

	/**
	 * Gets the menu items by parent.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the menu items by parent
	 */
	@RequestMapping(value="/menusbyparents",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMenuItemsByParent(final HttpServletRequest request,
			final Locale locale){
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		String parents=request.getParameter("parents");
		if(parents!=null){
			List<MenuItem> menus=MenuItem.findByParents(parents, locale.toString());
			for(MenuItem i:menus){
				MasterVO masterVO=new MasterVO(i.getId(),i.getText());
				masterVOs.add(masterVO);    			
			}
		}
		return masterVOs;
	}

	/**
	 * Gets the clarifications.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the clarifications
	 */
	@RequestMapping(value="/clarifications",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClarifications(
			final HttpServletRequest request,
			final Locale locale){
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		List<Status> status;
		try {
			status = Status.findStartingWith("question_clarifications","name",ApplicationConstants.ASC, locale.toString());
			for(Status i:status){
				MasterVO masterVO=new MasterVO(i.getId(),i.getName());
				masterVOs.add(masterVO);
			}
		} catch (ELSException e) {
		}
		return masterVOs;
	}	

	/**
	 * Gets the ministries by group.
	 *
	 * @param groupid the groupid
	 * @param request the request
	 * @param locale the locale
	 * @return the ministries by group
	 */
	@RequestMapping(value="/group/{groupid}/ministries",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMinistriesByGroup(
			@PathVariable("groupid")final Long groupid,
			final HttpServletRequest request,
			final Locale locale){
		List<Ministry> ministries;
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		try {
			ministries = Group.findMinistriesByName(groupid);
		} catch (ELSException e) {
			e.printStackTrace();
			return masterVOs;
		}
		
		for(Ministry i:ministries){
			MasterVO masterVO=new MasterVO(i.getId(),i.getName());
			masterVOs.add(masterVO);
		}
		return masterVOs;
	}

	/**
	 * ** Anand Kulkarni ***.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @param session the session
	 * @param model the model
	 * @return the s members
	 */
	@RequestMapping(value="/member/supportingmembers",method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getSMembers(final HttpServletRequest request,
			final Locale locale,
			@RequestParam("session")final Long session,
			final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> memberVOs=new ArrayList<MasterVO>();
		List<AutoCompleteVO> autoCompleteVOs=new ArrayList<AutoCompleteVO>();
		Session selectedSession=Session.findById(Session.class,session);
		House house=selectedSession.getHouse();
		Long primaryMemberId=null;
		if(request.getParameter("primaryMemberId")!=null){
			if(!request.getParameter("primaryMemberId").isEmpty()){
				primaryMemberId = Long.parseLong(request.getParameter("primaryMemberId"));
			}
		}
		/**** Removed this portion so as to use same code for getting primary members in case of
		 * clerk login
		 */
		//		if(primaryMemberId==null){
		//			return autoCompleteVOs;
		//		}
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				String strParam=request.getParameter("term");
				try {
					String param=new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
					memberVOs=HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house, selectedSession, locale.toString(), param,primaryMemberId);
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}else{
				String param=request.getParameter("term");
				memberVOs=HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house,selectedSession, locale.toString(), param, primaryMemberId);
			}
		}
		for(MasterVO i:memberVOs){
			AutoCompleteVO autoCompleteVO=new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}

		return autoCompleteVOs;
	}

	/**
	 * Gets the last reciving date.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the last reciving date
	 */
	@RequestMapping(value="/getformatteddate",method=RequestMethod.GET)
	public @ResponseBody String getLastRecivingDate(final HttpServletRequest request,
			final Locale locale){

		String strReceivingDate = request.getParameter("crdt");
		String formattedDate = null;
		Date receivingDate = null;

		if(strReceivingDate != null){
			if(!strReceivingDate.isEmpty()){
				try {
					receivingDate = FormaterUtil.getDateFormatter("yyyy-MM-dd hh:mm:ss", "en_US").parse(strReceivingDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				formattedDate = FormaterUtil.getDateFormatter("dd/MM/yyyy hh:mm:ss", "mr_IN").format(receivingDate);
			}
		}

		return formattedDate;
	}

	/**
	 * Gets the session for groups.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the session for groups
	 */
	@RequestMapping(value="/sessionforgroups")
	public @ResponseBody String getSessionForGroups(final HttpServletRequest request, 
			final Locale locale){	
		String result = null;		

		String hType = request.getParameter("houseType");
		String sType = request.getParameter("sessionType");	
		String sYear = request.getParameter("year");			

		if(hType != null && sType !=null && sYear != null) {

			if(!hType.isEmpty() && !sType.isEmpty() && !sYear.isEmpty()) {

				HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", hType, locale.toString());
				SessionType sessionType = SessionType.findByFieldName(SessionType.class, "type", sType, locale.toString());		
				Integer year = Integer.parseInt(sYear);

				Session session = null;				
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
					if(session != null) {
						if(session.getId() != null) {
							result = "success";
						}			
					} else {
						result = "error_nosessionfound";						
					}
				} catch(Exception e) {
					result = "error_duplicatesessionfound";					
				}				

			}
		}		
		return result;
	}

	/**
	 * Gets the ministries for group.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the ministries for group
	 */
	@RequestMapping(value="/group/ministries",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMinistriesForGroup(final HttpServletRequest request,	
			final Locale locale){
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();	

		String hTypeId = request.getParameter("houseType");
		String sTypeId = request.getParameter("sessionType");	
		String sYear = request.getParameter("year");
		String gNumber = request.getParameter("groupNumber");

		if(hTypeId != null && sTypeId !=null && sYear != null && gNumber != null) {

			if(!hTypeId.isEmpty() && !sTypeId.isEmpty() && !sYear.isEmpty() && !gNumber.isEmpty()) {

				HouseType houseType = HouseType.findById(HouseType.class, Long.parseLong(hTypeId));
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(sTypeId));		
				Integer year = Integer.parseInt(sYear);
				Integer groupNumber = Integer.parseInt(gNumber);

				List<Ministry> ministries = Ministry.findAll(Ministry.class, "name", ApplicationConstants.ASC, locale.toString());
				List<Ministry> ministriesOfOtherGroupsInSameSession;
				try {
					ministriesOfOtherGroupsInSameSession = Group.findMinistriesInGroupsForSessionExcludingGivenGroup(houseType, sessionType, year, groupNumber, locale.toString());
				} catch (ELSException e) {
					e.printStackTrace();
					return masterVOs;
				}
				if(!ministriesOfOtherGroupsInSameSession.isEmpty()) {
					ministries.removeAll(ministriesOfOtherGroupsInSameSession);
				}
				for(Ministry i : ministries){
					MasterVO masterVO=new MasterVO(i.getId(),i.getName());
					masterVOs.add(masterVO);
				}
			}
		}

		return masterVOs;
	}	


	/**
	 * Find resolution actors.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the list
	 */
	@RequestMapping(value="/resolution/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findResolutionActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		List<Reference> actors=new ArrayList<Reference>();
		String strResolution=request.getParameter("resolution");
		String strInternalStatus=request.getParameter("status");
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		String workflowHouseType = request.getParameter("workflowHouseType");
		if(strResolution!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null&&workflowHouseType!=null){
			if((!strResolution.isEmpty())&&(!strInternalStatus.isEmpty())&&
					(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())&&(!workflowHouseType.isEmpty())){
				Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
				Resolution resolution=Resolution.findById(Resolution.class,Long.parseLong(strResolution));
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));

				String server=null;
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					server=customParameter.getValue();
					if(server.equals("TOMCAT")){
						try {
							workflowHouseType = new String(workflowHouseType.getBytes("ISO-8859-1"),"UTF-8");							
						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}
				}

				actors=WorkflowConfig.findResolutionActorsVO(resolution,internalStatus,userGroup,Integer.parseInt(strLevel),workflowHouseType,locale.toString());
			}
		}
		return actors;
	}

	// Added 
	/**
	 * Load sub workflow by device type.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the list
	 */
	@RequestMapping(value="/status",method=RequestMethod.GET)
	public @ResponseBody List<Status> loadSubWorkflowByDeviceType(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		List<Status> workflowTypes= new ArrayList<Status>();
		try{
			DeviceType deviceType=null;
			String server=null;
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(customParameter!=null){
				server=customParameter.getValue();
				String strDeviceType=request.getParameter("deviceType");
				if(!strDeviceType.isEmpty()){
					if(server.equals("TOMCAT")){
						try {
							String param = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
							deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}else{
						deviceType=DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
					}
				}
				if(deviceType!=null){
					List<UserGroup> userGroups=this.getCurrentUser().getUserGroups();
					if(userGroups!=null){
						for(UserGroup i:userGroups){
							UserGroup userGroup=UserGroup.findById(UserGroup.class,i.getId());
							String userGroupDeviceType=userGroup.getParameterValue(ApplicationConstants.DEVICETYPE_KEY+"_"+locale);
							if(userGroupDeviceType.contains(deviceType.getName())){
								/**** Authenticated User's usergroup and usergroupType ****/
								String userGroupType=i.getUserGroupType().getType();
								/**** Status Allowed ****/
								CustomParameter allowedWorkflowTypes=CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_"+deviceType
										.getType().toUpperCase()+"_"+userGroupType.toUpperCase(), "");
								if(allowedWorkflowTypes!=null){
									workflowTypes=Status.findStatusContainedIn(allowedWorkflowTypes.getValue(), locale.toString());
								}else{
									CustomParameter defaultAllowedWorkflowTypes=CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_BY_DEFAULT", "");
									if(defaultAllowedWorkflowTypes!=null){
										workflowTypes=Status.findStatusContainedIn(defaultAllowedWorkflowTypes.getValue(), locale.toString());
									}else{
										logger.error("Custom Parameter 'MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_BY_DEFAULT' not set");
									}
								}
								break;
							}						
						}
						return workflowTypes;
					}else{
						logger.error("User Group type is not set");
					}
				}else{
					logger.error("No device Type found");
				}
			}else{
				logger.error("Custom Parameter 'DEPLOYMENT_SERVER' not set");
			}
		}catch (Exception e) {
			
		}
		return null;
	}

	//Added by Dhananjay
	/**
	 * Gets the last submission date from last discussion date.
	 *
	 * @param lastDiscussionDateStr the last discussion date str
	 * @param daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession the days between submission end date and last discussion date of session
	 * @param locale the locale
	 * @return the last submission date from last discussion date
	 */
	@RequestMapping(value="/getLastSubmissionDateFromLastDiscussionDate", method=RequestMethod.GET)
	public @ResponseBody Reference getLastSubmissionDateFromLastDiscussionDate(@RequestParam String lastDiscussionDateStr, 
			@RequestParam String daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession, 
			final Locale locale){
		Reference reference = new Reference();
		Date lastDiscussionDate = null;
		Date submissionEndDate = null;

		SimpleDateFormat sf=null;
		if(locale.toString().equals("mr_IN")){
			sf=new SimpleDateFormat("dd/MM/yyyy",new Locale("hi","IN"));
		}
		else{
			sf=new SimpleDateFormat("dd/MM/yyyy",new Locale(locale.toString()));
		}

		if(lastDiscussionDateStr != null && daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession != null) {
			if(!lastDiscussionDateStr.isEmpty() && !daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession.isEmpty()) {
				CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
				if(customParameter!=null){
					String server=customParameter.getValue();
					if(server.equals("TOMCAT")){						
						try {
							lastDiscussionDateStr=new String(lastDiscussionDateStr.getBytes("ISO-8859-1"),"UTF-8");			
							daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession = new String(daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession.getBytes("ISO-8859-1"),"UTF-8");			
						}
						catch (UnsupportedEncodingException e){
							e.printStackTrace();
						}
					}
				}
				try {
					lastDiscussionDate = sf.parse(lastDiscussionDateStr);
				} catch (ParseException e) {					
					e.printStackTrace();
				}
			} else {
				throw new RuntimeException("one or more request parameters are empty");				
			}
		} else {			
			throw new RuntimeException("one or more request parameters are null");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(lastDiscussionDate);		
		calendar.add(Calendar.DATE, -(Integer.parseInt(daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession)));
		submissionEndDate = calendar.getTime();

		SimpleDateFormat dateFormat = FormaterUtil.getDateFormatter(locale.toString());        
		reference.setId(dateFormat.format(submissionEndDate));
		reference.setName(dateFormat.format(submissionEndDate));		

		return reference;
	}

	/**
	 * Gets the type of selected device type.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the type of selected device type
	 */
	@RequestMapping(value="/getTypeOfSelectedDeviceType",method=RequestMethod.GET)
	public @ResponseBody String getTypeOfSelectedDeviceType(final HttpServletRequest request, 
			final Locale locale){

		String strDeviceTypeId = request.getParameter("deviceTypeId");
		String deviceType = null;		

		if(strDeviceTypeId != null){
			if(!strDeviceTypeId.isEmpty()){
				DeviceType deviceTypeObject = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceTypeId));
				if(deviceTypeObject != null) {
					deviceType = deviceTypeObject.getType();
				}
			}
		}

		return deviceType;
	}
	
	/**
	 * Gets the type of selected device type from name.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the type of selected device type from name
	 */
	@RequestMapping(value="/getTypeOfSelectedDeviceTypeFromName",method=RequestMethod.GET)
	public @ResponseBody String getTypeOfSelectedDeviceTypeFromName(final HttpServletRequest request, 
			final Locale locale){
		String server=null;
		String strDeviceType=request.getParameter("deviceType");
		DeviceType deviceType=null;
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
			server=customParameter.getValue();
		if(!strDeviceType.isEmpty()){
			if(server.equals("TOMCAT")){
				try {
					String param = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
					deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
				}catch (UnsupportedEncodingException e) {
					logger.error("Cannot Encode the Parameter.");
				}
			}else{
				deviceType=DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
			}
		}
		}
		return deviceType.getType();
	}

	/**
	 * Gets the sub departments from ministry.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the sub departments from ministry
	 */
	@RequestMapping(value="/ministry/subdepartments",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getSubDepartmentsFromMinistry(final HttpServletRequest request, 
			final Locale locale
	){
		String strMinistry=request.getParameter("ministry");
		Long ministryId=Long.parseLong(strMinistry);
		Ministry ministry=Ministry.findById(Ministry.class,ministryId);
		List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,locale.toString());
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		for(SubDepartment i:subDepartments){
			MasterVO masterVO=new MasterVO(i.getId(),i.getName());
			masterVOs.add(masterVO);
		}
		return masterVOs;
	}

	/**
	 * Find motion actors.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the list
	 */
	@RequestMapping(value="/motion/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findMotionActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		List<Reference> actors=new ArrayList<Reference>();
		String strMotion=request.getParameter("motion");
		String strInternalStatus=request.getParameter("status");
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		if(strMotion!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null){
			if((!strMotion.isEmpty())&&(!strInternalStatus.isEmpty())&&
					(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())){
				Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
				Motion motion=Motion.findById(Motion.class,Long.parseLong(strMotion));
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
				actors=WorkflowConfig.findMotionActorsVO(motion,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
			}
		}
		return actors;
	}

	/**
	 * Gets the member choices.
	 *
	 * @param request the request
	 * @param locale the locale
	 * @return the member choices
	 */
	@RequestMapping(value="/member_choice_resolutions",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMemberChoices(final HttpServletRequest request, 
			final Locale locale){
		
		List<MasterVO> resolutions = new ArrayList<MasterVO>();
		try{
			String strMemberId = request.getParameter("member");
			String strHouseType = request.getParameter("houseType");
			String strSessionTypeId = request.getParameter("sessionType");
			String strYear = request.getParameter("sessionYear");
			String strAnsweringDate = request.getParameter("answeringDate");
			String strDeviceType = request.getParameter("deviceType");	
			
			if(strDeviceType!=null && strMemberId!=null && strHouseType!=null && strSessionTypeId!=null && strYear!=null && strAnsweringDate!=null){
				if(!strDeviceType.isEmpty() && !strMemberId.isEmpty() && !strHouseType.isEmpty() && !strSessionTypeId.isEmpty() && !strYear.isEmpty() && !strAnsweringDate.isEmpty()){
					Long memberId = new Long(strMemberId);
					
					/** Create HouseType */
					HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
					
					/** Create SessionType */
					SessionType sessionType =
						SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
					
					/** Create year */
					Integer year = Integer.valueOf(strYear);
					
					/** Create Session */
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
					
					/** Create DeviceType */	
					DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
					
					/** Create answeringDate */
					Date answeringDate = null;
					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
						CustomParameter dbDateFormat = CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
						answeringDate = FormaterUtil.formatStringToDate(strAnsweringDate, dbDateFormat.getValue());
					}
					
					CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
				
					Date startTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionStartDate"),datePattern.getValue(), locale.toString());
					Date endTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionEndDate"),datePattern.getValue(), locale.toString());
					
					Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale.toString());
					Status[] internalStatuses = new Status[] { ADMITTED };
								
					List<Resolution> resolutionsList = Resolution.find(session, deviceType, memberId, answeringDate, internalStatuses, startTime, endTime,ApplicationConstants.ASC, locale.toString());
					for(Resolution r: resolutionsList){
						MasterVO masterVO = new MasterVO();
						masterVO.setId(r.getId());
						masterVO.setNumber(r.getNumber());
						masterVO.setValue(r.getNoticeContent());
						resolutions.add(masterVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		return resolutions;
	}

	@RequestMapping(value="/resolutions_government/isDiscussionDateEarly",method=RequestMethod.GET)
	public @ResponseBody String isDiscussionDateEarlyInGovernmentResolution(final HttpServletRequest request, 
			final Locale locale){
		String isDiscussionDateEarly = "";		
		String strExpectedDiscussionDate=request.getParameter("expectedDiscussionDate");
		String strRequestedDiscussionDate=request.getParameter("requestedDiscussionDate");	
		if(strExpectedDiscussionDate != null && strRequestedDiscussionDate != null) {
			if(!strExpectedDiscussionDate.isEmpty() && !strRequestedDiscussionDate.isEmpty()) {				
				CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(server != null) {
					if(server.getValue().equals("TOMCAT")) {
						try {
							strExpectedDiscussionDate = new String(strExpectedDiscussionDate.getBytes("ISO-8859-1"),"UTF-8");
							strRequestedDiscussionDate = new String(strRequestedDiscussionDate.getBytes("ISO-8859-1"),"UTF-8");							
						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}
				}
				Date expectedDiscussionDate = FormaterUtil.formatStringToDate(strExpectedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				Date requestedDiscussionDate = FormaterUtil.formatStringToDate(strRequestedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				if(expectedDiscussionDate != null && requestedDiscussionDate != null) {					
					if(requestedDiscussionDate.before(expectedDiscussionDate)) {
						isDiscussionDateEarly = "true";
					} else {
						isDiscussionDateEarly = "false";
					}
				}
			}
		}	
		return isDiscussionDateEarly;
	}
	
	@RequestMapping(value="/resolutions_government/isDiscussionDateChanged",method=RequestMethod.GET)
	public @ResponseBody String isDiscussionDateChangedInGovernmentResolution(final HttpServletRequest request, 
			final Locale locale){
		String isDiscussionDateChanged = "";		
		String strExpectedDiscussionDate=request.getParameter("expectedDiscussionDate");
		String strRequestedDiscussionDate=request.getParameter("requestedDiscussionDate");	
		if(strExpectedDiscussionDate != null && strRequestedDiscussionDate != null) {
			if(!strExpectedDiscussionDate.isEmpty() && !strRequestedDiscussionDate.isEmpty()) {				
				CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(server != null) {
					if(server.getValue().equals("TOMCAT")) {
						try {
							strExpectedDiscussionDate = new String(strExpectedDiscussionDate.getBytes("ISO-8859-1"),"UTF-8");
							strRequestedDiscussionDate = new String(strRequestedDiscussionDate.getBytes("ISO-8859-1"),"UTF-8");							
						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}
				}
				Date expectedDiscussionDate = FormaterUtil.formatStringToDate(strExpectedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				Date requestedDiscussionDate = FormaterUtil.formatStringToDate(strRequestedDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				if(expectedDiscussionDate != null && requestedDiscussionDate != null) {					
					if(requestedDiscussionDate.before(expectedDiscussionDate) || requestedDiscussionDate.after(expectedDiscussionDate)) {
						isDiscussionDateChanged = "true";
					} else {
						isDiscussionDateChanged = "false";
					}
				}
			}
		}	
		return isDiscussionDateChanged;
	}
	
	//========== CIS related AJAX calls ==========
	@RequestMapping(value="committeeTypes/houseType/{houseType}", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findCommitteeTypes(
			@PathVariable("houseType") final Long houseTypeId,
			final HttpServletRequest request,
			final Locale locale) {
		List<Reference> references = new ArrayList<Reference>();
		
		HouseType houseType = HouseType.findById(HouseType.class, houseTypeId);
		List<CommitteeType> committeeTypes = CommitteeType.find(houseType, locale.toString());
		for(CommitteeType ct : committeeTypes) {
			String id = String.valueOf(ct.getId());
			Reference reference = new Reference(id, ct.getName());
			references.add(reference);
		}
		
		return references;
	}
	
	@RequestMapping(value="committeeNames/committeeType/{committeeType}", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findCommitteeNamesByCommitteeType(
			@PathVariable("committeeType") final Long committeTypeId,
			final HttpServletRequest request,
			final Locale locale) {
		List<Reference> references = new ArrayList<Reference>();
		 
		CommitteeType committeeType = CommitteeType.findById(CommitteeType.class, committeTypeId);
		List<CommitteeName> committeeNames = CommitteeName.find(committeeType, locale.toString());
		for(CommitteeName cn : committeeNames) {
			String id = String.valueOf(cn.getId());
			Reference reference = new Reference(id, cn.getDisplayName());
			references.add(reference);
		}
		
		return references;
	}
	
	@RequestMapping(value="committeeName/{committeeName}/foundationDate", method=RequestMethod.GET)
	public @ResponseBody Reference findFoundationDate(
			@PathVariable("committeeName") final Long committeeNameId,
			final Locale locale) {
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, committeeNameId);
		Date foundationDate = committeeName.getFoundationDate();
		
		CustomParameter serverDateFormat = 
			CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
		String dateFormat = serverDateFormat.getValue();
		String strFoundationDate = 
			FormaterUtil.formatDateToString(foundationDate, dateFormat, locale.toString());
		
		Reference reference = new Reference();
		reference.setId(String.valueOf(committeeName.getId()));
		reference.setName(strFoundationDate);
		
		return reference;
	}
	
	@RequestMapping(value="committee/dissolutionDate", method=RequestMethod.GET)
	public @ResponseBody Reference computeDissolutionDate(
			@RequestParam("committeeName") final Long committeeNameId,
			@RequestParam("formationDate") final String strDate,
			final Locale locale) {
		String strFormationDate = strDate;
		
		CustomParameter deploymentServer = 
			CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		String server = deploymentServer.getValue();
		if(server.equals("TOMCAT")) {
			try {
				strFormationDate = new String(strDate.getBytes("ISO-8859-1"),"UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				logger.error("Cannot Encode the Parameter.");
			}
		}
		
		CustomParameter serverDateFormat = 
			CustomParameter.findByName(CustomParameter.class, "SERVER_DATEFORMAT", "");
		String dateFormat = serverDateFormat.getValue();
		Date formationDate = FormaterUtil.formatStringToDate(strFormationDate, 
				dateFormat, locale.toString());
		
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, committeeNameId);
		Date dissolutionDate = 
			Committee.dissolutionDate(committeeName, formationDate, locale.toString());
		String strDissolutionDate = 
			FormaterUtil.formatDateToString(dissolutionDate, dateFormat, locale.toString());
		
		Reference reference = new Reference();
		reference.setId(String.valueOf(committeeName.getId()));
		reference.setName(strDissolutionDate);
		
		return reference;
	}
	
	@RequestMapping(value="committeeNames/houseType/{houseType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> findCommitteeNamesByHouseType(
			@PathVariable("houseType") final Long houseTypeId,
			final HttpServletRequest request,
			final Locale locale) {
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		
		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		
		HouseType houseType = HouseType.findById(HouseType.class, houseTypeId);
		HouseType bothHouse = HouseType.findByType(
				ApplicationConstants.BOTH_HOUSE, locale.toString());
		String houseTypeType = houseType.getType();
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			List<CommitteeName> lowerHouseCommittees = 
				CommitteeName.find(houseType, locale.toString());
			committeeNames.addAll(lowerHouseCommittees);
			
			List<CommitteeName> bothHouseCommittees = 
				CommitteeName.find(bothHouse, locale.toString());
			committeeNames.addAll(bothHouseCommittees);
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			List<CommitteeName> upperHouseCommittees = 
				CommitteeName.find(houseType, locale.toString());
			committeeNames.addAll(upperHouseCommittees);
			
			List<CommitteeName> bothHouseCommittees = 
				CommitteeName.find(bothHouse, locale.toString());
			committeeNames.addAll(bothHouseCommittees);
		}
		else if(houseTypeType.equals(ApplicationConstants.BOTH_HOUSE)) {
			List<CommitteeName> allCommittees =
				CommitteeName.findAll(locale.toString());
			committeeNames.addAll(allCommittees);
		}
		
		for(CommitteeName cn : committeeNames) {
			Long id = cn.getId();
			String name = cn.getName();
			String displayName = cn.getDisplayName();
			
			MasterVO masterVO = new MasterVO();
			masterVO.setId(id);
			masterVO.setName(name);
			masterVO.setValue(displayName);
			
			masterVOs.add(masterVO);
		}
		
		return masterVOs;
	}
	
	@RequestMapping(value="/workflowTypes",method=RequestMethod.GET)
	public @ResponseBody List<Status> loadSubWorkflowByModule(
			final HttpServletRequest request,
			@RequestParam("module") final String module,
			final Locale localeObj) {
		List<Status> workflowTypes = new ArrayList<Status>();
		
		try {
			String locale = localeObj.toString();
			String encodedModule = this.encode(module);
			String moduleUC = encodedModule.toUpperCase();
			
			// Allowed usergrouptypes for this module
			String name1 = moduleUC + "_ALLOWED_USERGROUPTYPES";
			CustomParameter configuredUGTs = 
				CustomParameter.findByName(CustomParameter.class, name1, locale);
			String[] allowedUserGroupTypes = 
				configuredUGTs.getValue().split(",");
			
			// Retrieve the appropriate userGroup for this user
			List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
			UserGroup userGroup = 
				this.getUserGroup(userGroups, allowedUserGroupTypes);
			String ugtType = userGroup.getUserGroupType().getType();
			String ugtTypeUC = ugtType.toUpperCase(); 
			
			// Get the statuses configured for this ugtType for the given 
			// module
			String name2 = "MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_" + moduleUC +
				"_" + ugtTypeUC;
			CustomParameter configuredStatuses = 
				CustomParameter.findByName(CustomParameter.class, name2, "");
			if(configuredStatuses == null) {
				name2 = "MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_" + moduleUC +
					"_BY_DEFAULT";
				configuredStatuses = 
					CustomParameter.findByName(CustomParameter.class, 
							name2, "");
			}
			workflowTypes = Status.findStatusContainedIn(
					configuredStatuses.getValue(), locale);
		}
		catch (Exception e) {

		}
		
		return workflowTypes;
	}
	
	@RequestMapping(value="partyType/{partyType}/members",
			method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getPartyTypewiseMembers(
			final HttpServletRequest request,
			@PathVariable("partyType") final Long partyTypeId,
			@RequestParam("houseType") final Long houseTypeId,
			@RequestParam("term") final String term,
			final Locale localeObj){
		List<AutoCompleteVO> vos = new ArrayList<AutoCompleteVO>();
		
		try {
			String locale = localeObj.toString();			
			String nameBeginningWith = this.encode(term);			
			PartyType partyType = 
				PartyType.findById(PartyType.class, partyTypeId);
			
			HouseType houseType = 
				HouseType.findById(HouseType.class, houseTypeId);
			
			Date currentDate = new Date();
			
			List<Member> members = 
				House.findActiveMembers(houseType, partyType, currentDate, 
						nameBeginningWith, ApplicationConstants.ASC, locale);
			for(Member m : members) {
				Long id = m.getId();
				String name = m.getFullname();
				
				AutoCompleteVO vo = new AutoCompleteVO();
				vo.setId(id);
				vo.setValue(name);
				vos.add(vo);
			}
		}
		catch (Exception e) {

		}
		
		return vos;
	}

	@RequestMapping(value="houseType/{houseType}/members",
			method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getHouseTypeWiseActiveMembers(
			final HttpServletRequest request,
			@PathVariable("houseType") final Long houseTypeId,
			@RequestParam("term") final String term,
			final Locale localeObj){
		List<AutoCompleteVO> vos = new ArrayList<AutoCompleteVO>();
		
		try {
			String locale = localeObj.toString();			
			String nameBeginningWith = this.encode(term);			
			
			HouseType houseType = 
				HouseType.findById(HouseType.class, houseTypeId);
			
			Date currentDate = new Date();
			
			List<Member> members = 
				House.findActiveMembers(houseType, currentDate, 
						nameBeginningWith, ApplicationConstants.ASC, locale);
			for(Member m : members) {
				Long id = m.getId();
				String name = m.getFullname();
				
				AutoCompleteVO vo = new AutoCompleteVO();
				vo.setId(id);
				vo.setValue(name);
				vos.add(vo);
			}
		}
		catch (Exception e) {

		}
		
		return vos;
	}
	
	@RequestMapping(value="committee/actors/workflow/{workflowName}",
			method=RequestMethod.GET)
	public @ResponseBody List<Reference> getCommitteeActors(
			@PathVariable("workflowName") final String workflowName,
			@RequestParam("status") final Long statusId,
			@RequestParam("houseType") final Long houseTypeId,
			@RequestParam("userGroup") final Long userGroupId,
			@RequestParam("assigneeLevel") final int assigneeLevel,
			final Locale localeObj) {
		List<Reference> actors = new ArrayList<Reference>();
		
		try {
			HouseType houseType = 
				HouseType.findById(HouseType.class, houseTypeId);
			UserGroup userGroup = 
				UserGroup.findById(UserGroup.class, userGroupId);
			Status status = Status.findById(Status.class, statusId);
			String locale = localeObj.toString();
			
			List<WorkflowActor> wfActors = WorkflowConfig.findCommitteeActors(
					houseType, userGroup, status, workflowName, 
					assigneeLevel, locale);
			for(WorkflowActor wfa : wfActors) {
				String id = String.valueOf(wfa.getId());
				String name = wfa.getUserGroupType().getName();
				Reference actor = new Reference(id, name);
				actors.add(actor);
			}
		}
		catch (Exception e) {

		}
		
		return actors;
	}

	private String encode(final String str) {
		String retVal = str;
		
		CustomParameter customParameter = 
			CustomParameter.findByName(CustomParameter.class, 
					"DEPLOYMENT_SERVER", "");
		String server = customParameter.getValue();
		if(server.equals("TOMCAT")) {
			try {
				retVal = new String(retVal.getBytes("ISO-8859-1"), "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				logger.error("Cannot Encode the Parameter.");
			}
		}
		
		return retVal;
	}
	
	private UserGroup getUserGroup(final List<UserGroup> userGroups,
			final String[] userGroupTypeTypes) {
		for(UserGroup ug : userGroups) {
			String ugtType = ug.getUserGroupType().getType();
			for(String ugtt : userGroupTypeTypes) {
				if(ugtType.equals(ugtt)) {
					// Returning ug is the right thing to do. 
					// But it throws the following exception:
					// org.hibernate.LazyInitializationException: 
					// failed to lazily initialize a collection of role: 
					// org.mkcl.els.domain.UserGroup.parameters, no session or 
					// session was closed
					
					// return ug;
					
					// As a way around following piece of code is added
					UserGroup userGroup = 
						UserGroup.findById(UserGroup.class, ug.getId());
					return userGroup;
				}
			}
		}
		
		return null;
	}
	
}