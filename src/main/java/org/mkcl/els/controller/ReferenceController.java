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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.util.RomanNumeral;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.AutoCompleteVO;
import org.mkcl.els.common.vo.ConstituencyCompleteVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.DynamicSelectVO;
import org.mkcl.els.common.vo.GroupVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.OrdinanceSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.SectionVO;
import org.mkcl.els.common.vo.TemplateVO;
import org.mkcl.els.controller.mois.CutMotionDateControllerUtility;
import org.mkcl.els.controller.wf.EditingWorkflowController;
import org.mkcl.els.domain.*;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.service.ISecurityService;
import org.mkcl.els.service.impl.JwtServiceImpl;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class ReferenceController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/ref")
public class ReferenceController extends BaseController {
	
	@Autowired 
	private ISecurityService securityService;

	@Autowired
	SessionRegistry sessionRegistry;
	
	@Autowired
	JwtServiceImpl jwtService;
	/**
	 * Gets the districts by state id.
	 *
	 * @param stateId the state id
	 * @param map the map
	 * @param locale the locale
	 * @return the districts by state id
	 */
	@RequestMapping(value = "state/{stateId}/districts", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getDistrictsByState(@PathVariable("stateId") final Long stateId, final ModelMap map, final Locale locale) {
		List<Reference> districts = new ArrayList<Reference>();
		try {
			districts = District.findDistrictsRefByStateId(stateId , "name" , ApplicationConstants.ASC ,locale.toString());
		} catch (ELSException e) {
			e.printStackTrace();
		}
		
		return districts;
	}
	
	@RequestMapping(value = "/{username}/isMemberActiveInSession", method = RequestMethod.GET)
	public @ResponseBody boolean isMemberActiveInLoginSession(@PathVariable("username") final String username, final ModelMap map,HttpServletRequest request,HttpSession session ,final Locale locale) {
		boolean isMemberActiveInLoginSession = false;
		try {
			
			List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
			List<SessionInformation> allSessions=new ArrayList<SessionInformation>();
			
			for(Object obj:allPrincipals) {
				AuthUser authUser=(AuthUser)obj;
				if(authUser!=null && authUser.getActualUsername()!=null 
						&& authUser.getActualUsername().trim().equalsIgnoreCase(username) ) {
					isMemberActiveInLoginSession=true;
					allSessions = sessionRegistry.getAllSessions(obj,false);
					break;
				}
			}
			
			
			String token = request.getHeader("authorization");
			if(token!=null && token.trim().length()>0) {
				boolean verifyJwtToken = jwtService.verifyJwtToken(token,username,allSessions);
				
				if(verifyJwtToken==true) {
					if(username!=null && username.trim().length()>0) {
						User memberUser = User.findByUserName(username, locale.toString());
						logger.debug("memberUser found with ID: " + memberUser.getId());
						Member member = Member.findByNameBirthDate(memberUser.getFirstName(), memberUser.getMiddleName(), memberUser.getLastName(), memberUser.getBirthDate(), locale.toString());
						logger.debug("member found with ID: " + member.getId());
						if(member!=null && member.getId()!=null &&member.getId()>0)
							isMemberActiveInLoginSession = true;					
						else
							isMemberActiveInLoginSession=false;
					}
				}else {
					//as jwt token is invalid
					isMemberActiveInLoginSession=false;
				}
			}
			//String loggedInUsername = this.getCurrentUser().getActualUsername();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Invalid username: " + username);
		}		
		return isMemberActiveInLoginSession;
	}

	/**
	 * Gets the tehsils by district.
	 *
	 * @param districtId the district id
	 * @param map the map
	 * @param locale the locale
	 * @return the tehsils by district
	 */
	@RequestMapping(value = "district/{districtId}/tehsils", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getTehsilsByDistrict(@PathVariable("districtId") final Long districtId, final ModelMap map, final Locale locale) {
		
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
	@RequestMapping(value = "/state{state_id}/districts", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getDistrictsByStateId(@PathVariable("state_id") final Long stateId, final ModelMap map, final Locale locale) {
		List<Reference> districts = new ArrayList<Reference>();
		try{
			for(District district : District.findDistrictsByStateId(stateId,"name", "ASC", locale.toString())){
				Reference ref = new Reference();
				ref.setId(district.getId().toString());
				ref.setName(district.getName());
			}
		}catch (ELSException e) {
			
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
	public @ResponseBody List<MasterVO> getDivisionsByStateId(@PathVariable("state_id") final Long stateId, final ModelMap map, final Locale locale) {
		
		List<Division> divisions = Division.findAllByFieldName(Division.class, "state", State.findById(State.class, stateId), "name", "asc", locale.toString());
		List<MasterVO> divisionsVO = new ArrayList<MasterVO>();
		
		for(Division division : divisions){
			MasterVO vo = new MasterVO();
			vo.setId(division.getId());
			vo.setName(division.getName());
			divisionsVO.add(vo);
		}
		
		return divisionsVO; 
	}

	/**
	 * Gets the districts by division id.
	 *
	 * @param divisionId the division id
	 * @param map the map
	 * @param locale the locale
	 * @return the districts by division id
	 */
	@RequestMapping(value = "/{division_id}/districts", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getDistrictsByDivisionId(@PathVariable("division_id") final Long divisionId, final ModelMap map, final Locale locale) {
		List<District> districts = null;
		List<MasterVO> districtsVO = new ArrayList<MasterVO>();
		
		Division division = Division.findById(Division.class, divisionId);
		if (division != null) {
			districts = District.findAllByFieldName(District.class, "division", division, "name", "asc", locale.toString());
			
			for(District district : districts){
				MasterVO vo = new MasterVO();
				vo.setId(district.getId());
				vo.setName(district.getName());
				districtsVO.add(vo);
			}
		}

		return districtsVO;
	}

	/**
	 * Gets the railway stations by selected districts.
	 *
	 * @param districtsStr the districts str
	 * @param map the map
	 * @param locale the locale
	 * @return the railway stations by selected districts
	 */
	@RequestMapping(value = "/districts{selectedDistricts}/railwayStations", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getRailwayStationsBySelectedDistricts(@PathVariable("selectedDistricts") final String districtsStr, final ModelMap map, final Locale locale) {
				
		List<Reference> railwayStationsForSelectedDistrictsVO = new ArrayList<Reference>();
		
		String districts[] = districtsStr.split(",");
		for (int i = 0; i < districts.length; i++) {
			
			List<RailwayStation> railwayStationsForDistrict = RailwayStation.findAllByFieldName(RailwayStation.class, "district", District.findById(District.class, Long.parseLong(districts[i])), "name", "asc", locale.toString());
			
			for(RailwayStation railwayStation : railwayStationsForDistrict){
				Reference ref = new Reference();
				ref.setId(railwayStation.getId().toString());
				ref.setName(railwayStation.getName());
				railwayStationsForSelectedDistrictsVO.add(ref);
			}
			
			railwayStationsForDistrict = null;
		}
		
		return railwayStationsForSelectedDistrictsVO;
	}

	/**
	 * Gets the airports by selected districts.
	 *
	 * @param districtsStr the districts str
	 * @param map the map
	 * @param locale the locale
	 * @return the airports by selected districts
	 */
	@RequestMapping(value = "/districts{selectedDistricts}/airports", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getAirportsBySelectedDistricts(@PathVariable("selectedDistricts") final String districtsStr, final ModelMap map, final Locale locale) {
		
		List<Reference> airportsForSelectedDistricts = new ArrayList<Reference>();
		
		String districts[] = districtsStr.split(",");
		for (int i = 0; i < districts.length; i++) {
			List<Airport> airportsForDistrict = Airport.findAllByFieldName( Airport.class, "district", District.findById(District.class, Long.parseLong(districts[i])), "name", "asc", locale.toString());
			
			for(Airport airport : airportsForDistrict){
				Reference ref = new Reference();
				ref.setId(airport.getId().toString());
				ref.setName(airport.getName());
				airportsForSelectedDistricts.add(ref);
			}
			
			airportsForDistrict = null;
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
	@RequestMapping(value = "/{district_id}/tehsils", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getTehsilsByDistrictId(@PathVariable("district_id") final Long districtId, final ModelMap map, final HttpServletRequest request, final Locale locale) {
		District district = District.findById(District.class, districtId);
		List<Reference> tehsils = new ArrayList<Reference>();
		
		if(district != null){
			List<Tehsil> tehs = Tehsil.findAllByFieldName(Tehsil.class, "district", district, "name", "asc", locale.toString());
			for(Tehsil tehsil : tehs){
				Reference ref = new Reference();
				ref.setId(tehsil.getId().toString());
				ref.setName(tehsil.getName());
				tehsils.add(ref);
			}
		}
		return tehsils; 
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
	@RequestMapping(value = "/data/{constituency_name}/districts",method = RequestMethod.GET)
	public @ResponseBody List<Reference> getDistrictsByConstituencyId(@PathVariable("constituency_name") final String constituencyName, final ModelMap map, final HttpServletRequest request, final Locale locale) {
		
		String decodedString = null;
		
		List<Reference> districtsVO = new ArrayList<Reference>();
		
		try {
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "DEFAULT_URI_ENCODING", locale.toString());
			CustomParameter customParameter1 = CustomParameter.findByName(CustomParameter.class, "DEFAULT_ENCODING", locale.toString());
			decodedString = new String(constituencyName.getBytes(customParameter.getValue()), customParameter1.getValue());
			Constituency constituency = Constituency.findByName(Constituency.class, decodedString.trim(), locale.toString());
			List<District> districts = District.findDistrictsByConstituencyId(constituency.getId(), "name", "ASC");
			for(District district : districts){
				Reference ref = new Reference();
				ref.setId(district.getId().toString());
				ref.setName(district.getName());
				districtsVO.add(ref);
			}
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (ELSException e) {
			map.addAttribute("REFERENCE_CONTROLLER", "Request can not be completed at the moment.");
		}
		return districtsVO; 
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
	public @ResponseBody List<Reference> getSubDepartmentsByDepartment(@PathVariable("department") final Long department, final ModelMap map, final Locale locale){
		List<Reference> subDepts = new ArrayList<Reference>();
		
		Department dept = Department.findById(Department.class, department);
		if(dept != null){
			List<SubDepartment> subDepartments = SubDepartment.findAllByFieldName(SubDepartment.class, "department", dept, "name", ApplicationConstants.ASC, locale.toString());
			for(SubDepartment subDept : subDepartments){
				Reference ref = new Reference();
				ref.setId(subDept.getId().toString());
				ref.setName(subDept.getName());
				subDepts.add(ref);
			}
		}
		return subDepts;
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
	public @ResponseBody Reference getMemberDeathDate(@PathVariable("memberId") final Long memberId, final Locale locale){
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
	public @ResponseBody ConstituencyCompleteVO getDivisionDistrictsByConstituency(	@PathVariable("constituency") final Long constituency, final Locale locale){
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
	public @ResponseBody Reference getElectionType(@PathVariable("electionId") final Long electionId, final Locale locale) {
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
	public @ResponseBody GroupVO getGroupVO(@PathVariable("group") final Long group, final Locale locale) {
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
		List<Department> departments=MemberMinister.findAssignedDepartments(ministries.get(0),
				selectedGroup.getSession().getEndDate(), locale.toString());
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
			List<SubDepartment> subDepartments=
					MemberMinister.findAssignedSubDepartments(ministries.get(0), departments.get(0), 
							selectedGroup.getSession().getEndDate(), locale.toString());
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
	public @ResponseBody List<MasterVO> getDepartments(@PathVariable("ministry") final Long ministry,
			final HttpServletRequest request,
			final Locale locale){
		Ministry selectedMinistry=Ministry.findById(Ministry.class, ministry);
		List<MasterVO> departmentVOs=new ArrayList<MasterVO>();
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		if(strHouseType!= null && !strHouseType.isEmpty()
			&& strSessionType != null && !strSessionType.isEmpty()
			&& strSessionYear != null && !strSessionYear.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
		//populating departments
		List<Department> departments=MemberMinister.findAssignedDepartments(selectedMinistry,session.getEndDate(), locale.toString());
		for(Department i:departments){
			MasterVO masterVO=new MasterVO();
			masterVO.setId(i.getId());
			masterVO.setName(i.getName());
			departmentVOs.add(masterVO);
		}
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
	public @ResponseBody List<MasterVO> getDepartmentsByMinistryName(final HttpServletRequest request, final Locale locale){
		String[] strMinistries=request.getParameterValues("ministries[]");
		List<MasterVO> departmentVOs=new ArrayList<MasterVO>();
		if(strMinistries != null){
			if(strMinistries.length > 0){
				List<Department> departments=MemberMinister.findAssignedDepartments(strMinistries,locale.toString());
				for(Department i:departments){
					MasterVO masterVO=new MasterVO();
					masterVO.setId(i.getId());
					masterVO.setName(i.getName());
					departmentVOs.add(masterVO);
				}
			}
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
//	@RequestMapping(value="/subdepartments/byministriesdepartmentsname", method=RequestMethod.POST)
//	public @ResponseBody List<MasterVO> getSubDepartments(
//			final HttpServletRequest request,
//			final Locale locale){
//		String[] ministries=request.getParameterValues("ministries[]");
//		String[] departments=request.getParameterValues("departments[]");
//		//populating sub departments
//		List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
//		if(ministries != null && departments != null){
//			
//			List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministries,departments, locale.toString());
//			
//			for(SubDepartment i:subDepartments){
//				MasterVO masterVO=new MasterVO();
//				masterVO.setId(i.getId());
//				masterVO.setName(i.getName());
//				subDepartmentVOs.add(masterVO);
//			}
//		}
//		return subDepartmentVOs;
//	}
	
	@RequestMapping(value="/subdepartments/byministriesname", method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> getSubDepartments(
			final HttpServletRequest request,
			final Locale locale){
		String[] ministries=request.getParameterValues("ministries[]");
		//populating sub departments
		List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
		if(ministries != null){
			List<SubDepartment> subDepartments = new ArrayList<SubDepartment>();
			String fromDateStr = request.getParameter("activeFrom");
			String toDateStr = request.getParameter("activeTo");
			
			if(fromDateStr!=null && !fromDateStr.isEmpty()
					&& toDateStr!=null && !toDateStr.isEmpty()) {
				CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptDeployment!=null && csptDeployment.getValue().equals("TOMCAT")){
					try {
						fromDateStr = new String(fromDateStr.getBytes("ISO-8859-1"),"UTF-8");
						toDateStr = new String(toDateStr.getBytes("ISO-8859-1"),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return subDepartmentVOs;
					}										
				}
				Date fromDate = FormaterUtil.formatStringToDate(fromDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				Date toDate = FormaterUtil.formatStringToDate(toDateStr, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				subDepartments=MemberMinister.findAssignedSubDepartments(ministries, fromDate, toDate, locale.toString());
			} else {
				subDepartments=MemberMinister.findAssignedSubDepartments(ministries, locale.toString());
			}
			
			for(SubDepartment i:subDepartments){
				MasterVO masterVO=new MasterVO();
				masterVO.setId(i.getId());
				masterVO.setName(i.getName());
				subDepartmentVOs.add(masterVO);
			}
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
			@PathVariable("ministry") final Long ministry, @PathVariable("department") final Long department, 
			final HttpServletRequest request,
			final Locale locale){
		Ministry selectedMinistry=Ministry.findById(Ministry.class, ministry);
		List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
		Department selectedDepartment=Department.findById(Department.class,department);
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		if(strHouseType!= null && !strHouseType.isEmpty()
			&& strSessionType != null && !strSessionType.isEmpty()
			&& strSessionYear != null && !strSessionYear.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
			//populating sub departments
			List<SubDepartment> subDepartments=MemberMinister.
					findAssignedSubDepartments(selectedMinistry,selectedDepartment, session.getEndDate(), locale.toString());
			
			for(SubDepartment i:subDepartments){
				MasterVO masterVO=new MasterVO();
				masterVO.setId(i.getId());
				masterVO.setName(i.getName());
				subDepartmentVOs.add(masterVO);
			}
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

	@RequestMapping(value="/sessionbyhousetype/{houseType}/{sessionYear}/{sessionType}", method=RequestMethod.GET)
	public @ResponseBody MasterVO getSessionByHouseType(
			final Locale locale,
			@PathVariable("houseType")final String houseType,
			@PathVariable("sessionYear")final Integer sessionYear,
			@PathVariable("sessionType") final Long sessionType) {
		try {
			//populating departments
			HouseType selectedHouseType=HouseType.findByType(houseType, locale.toString());
			SessionType selectedSessionType=SessionType.findById(SessionType.class, sessionType);
			Session selectedSession=Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, sessionYear);
			
			MasterVO masterVO=new MasterVO(selectedSession.getId(),"");
			return masterVO;
			
		} catch (ELSException e) {
			e.printStackTrace();
		}
		return new MasterVO();
	}
	
	@RequestMapping(value="/sessionbyparametername", method=RequestMethod.GET)
	public @ResponseBody MasterVO getSessionByHouseType(HttpServletRequest request, final Locale locale) {
		
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strYear = request.getParameter("year");
		String piggyBackNumber = request.getParameter("piggyBackNumber");
		
		MasterVO retVal = getSession(strHouseType, strSessionType, strYear, locale.toString());

		if(piggyBackNumber != null && !piggyBackNumber.isEmpty()){
			piggyBackNumber = getDecodedString(new String[]{piggyBackNumber})[0];
			
			retVal.setNumber(new Long(piggyBackNumber).intValue());
		}
		
		return retVal;
	}
	
	private MasterVO getSession(String houseTypeName, String sessionTypeName, String sessionYear, String locale){
		MasterVO retVal = new MasterVO();
		
		String[] decodedString = getDecodedString(new String[]{houseTypeName, sessionTypeName, sessionYear});
		houseTypeName = decodedString[0];
		sessionTypeName = decodedString[1];
		sessionYear = decodedString[2];
		
		HouseType selectedHouseType = null;
		SessionType selectedSessionType = null;
		Integer year = null;
		
		try{
			selectedHouseType = HouseType.findById(HouseType.class, new Long(houseTypeName));
		}catch(Exception e){
			
		}
		
		if(selectedHouseType == null){
			try{
				selectedHouseType = HouseType.findByName(houseTypeName, locale);
			}catch(Exception e){
				
			}
		}
		
		if(selectedHouseType == null){
			try{
				selectedHouseType = HouseType.findByType(houseTypeName, locale);
			}catch(Exception e){
				
			}
		}

		try{
			selectedSessionType = SessionType.findById(SessionType.class, new Long(sessionTypeName));
		}catch(Exception e){
			
		}
		
		if(selectedSessionType == null){
			try{
				selectedSessionType = SessionType.findByFieldName(SessionType.class, "sessionType", sessionTypeName, locale);
			}catch(Exception e){
				
			}
		}
		
		if(selectedSessionType == null){
			try{
				selectedSessionType = SessionType.findByType(sessionTypeName, locale);
			}catch(Exception e){
				
			}
		}
		
		year = new Integer(sessionYear);
		try{
			Session session = Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
			
			if(session != null){
				retVal.setId(session.getId());
			}
			
		}catch(Exception e){
			
		}
		
		return retVal;
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
			final Locale locale,@RequestParam("session")final Long session
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
	@RequestMapping(value = "/{houseType}/house", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getHousesByHouseType(@PathVariable("houseType") final Long houseTypeId, final ModelMap map, final Locale locale) {
		List<Reference> houses = new ArrayList<Reference>();
		HouseType houseType = HouseType.findById(HouseType.class, houseTypeId);
		if (houseType != null) {
			List<House> houseList = House.findAllByFieldName(House.class, "type", houseType, "firstDate", ApplicationConstants.DESC, locale.toString());
			for(House house : houseList){
				Reference ref = new Reference();
				ref.setId(house.getId().toString());
				ref.setName(house.getName());
				houses.add(ref);
			}
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
	public @ResponseBody List<MasterVO> loadGroups(final HttpServletRequest request, final Locale locale){
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
//	@RequestMapping(value="/allowedgroups")
//	public @ResponseBody List<MasterVO> loadAllowedGroups(final HttpServletRequest request,
//			final Locale locale){
//		//CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
//		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
//		List<Group> groups=new ArrayList<Group>();
//		String strhouseType=request.getParameter("houseType");
//		String stryear=request.getParameter("sessionYear");
//		String strsessionType=request.getParameter("sessionType");
//		//String strAllowedGroups=request.getParameter("allowedgroups");
//		
//		String strAllowedGroups = this.getCurrentUser().getGroupsAllowed();
//		
//		if(strhouseType!=null&&stryear!=null&&strsessionType!=null&&strAllowedGroups!=null){
//			HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",strhouseType,locale.toString());
//			SessionType selectedSessionType=SessionType.findById(SessionType.class, Long.parseLong(strsessionType));
//			Integer year=Integer.parseInt(stryear);
//			try {
//				groups=Group.findByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
//			
//				for(Group i:groups){
//					if(strAllowedGroups.contains(String.valueOf(i.getNumber()))){
//						MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
//						masterVOs.add(masterVO);
//					}
//				}
//			} catch (ELSException e) {
//				e.printStackTrace();
//				return masterVOs;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return masterVOs;
//	}


//	@RequestMapping(value="/allowedgroups")
//	public @ResponseBody List<MasterVO> loadAllowedGroups(final HttpServletRequest request,
//			final Locale locale){
//		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
//		String strHouseType = request.getParameter("houseType");
//		String strYear = request.getParameter("sessionYear");
//		String strSessionType = request.getParameter("sessionType");
//		String strlocale = locale.toString();
//		if(strHouseType != null && !strHouseType.isEmpty()
//			&& strSessionType != null && !strSessionType.isEmpty()
//			&& strYear != null && !strYear.isEmpty()){
//			HouseType houseType = HouseType.findByType(strHouseType, strlocale);
//			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
//			String groupsAllowed = null;
//			try {
//				Session session = Session.
//						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strYear));
//				if(session != null){
//					List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
//					for(UserGroup ug : userGroups){
//						if(UserGroup.isActiveInSession(session, ug, strlocale)){
//							Map<String,String> userGroupParameters = UserGroup.findParametersByUserGroup(ug);
//							groupsAllowed = userGroupParameters.get(ApplicationConstants.GROUPSALLOWED_KEY+"_"+strlocale);
//						}
//					}
//					List<Group> groups = Group.
//							findAllByFieldName(Group.class, "session", session, "number", ApplicationConstants.ASC, strlocale);
//					for(Group i:groups){
//						if(groupsAllowed.contains(String.valueOf(i.getNumber()))){
//							MasterVO masterVO=new MasterVO();
//							masterVO.setId(i.getId());
//							masterVO.setName(FormaterUtil.getNumberFormatterNoGrouping(strlocale).format(i.getNumber()));
//							masterVOs.add(masterVO);
//						}
//					}
//				}
//			} catch (NumberFormatException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ELSException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return masterVOs;
//	}
	
	@RequestMapping(value="/allowedgroups")
	public @ResponseBody List<MasterVO> loadAllowedGroups(final HttpServletRequest request,
			final Locale locale){
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		String strHouseType = request.getParameter("houseType");
		String strYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strlocale = locale.toString();
		if(strHouseType != null && !strHouseType.isEmpty()
			&& strSessionType != null && !strSessionType.isEmpty()
			&& strYear != null && !strYear.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, strlocale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			String groupsAllowed = null;
			try {
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strYear));
				Session latestSession = Session.findLatestSession(houseType);
				if(session != null && latestSession != null){
					List<Group> groups = Group.
							findAllByFieldName(Group.class, "session", session, "number", ApplicationConstants.ASC, strlocale);
					if(latestSession.getId().equals(session.getId())){
						groupsAllowed = this.getCurrentUser().getGroupsAllowed();
						for(Group i:groups){
							if(groupsAllowed.contains(String.valueOf(i.getNumber()))){
								MasterVO masterVO=new MasterVO();
								masterVO.setId(i.getId());
								masterVO.setName(FormaterUtil.getNumberFormatterNoGrouping(strlocale).format(i.getNumber()));
								masterVOs.add(masterVO);
							}
						}
					}else{
						for(Group i:groups){
							MasterVO masterVO=new MasterVO();
							masterVO.setId(i.getId());
							masterVO.setName(FormaterUtil.getNumberFormatterNoGrouping(strlocale).format(i.getNumber()));
							masterVOs.add(masterVO);
						}
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
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
	 * Gets the group.
	 *
	 * @param request the request
	 * @param ministryId the ministry id
	 * @param locale the locale
	 * @return the group
	 */
	@RequestMapping(value="/subdepartment/{subdepartmentId}/group")
	public @ResponseBody MasterVO getGroupBySubDepartment(final HttpServletRequest request,
			@PathVariable("subdepartmentId") final Long subdepartmentId,
			final Locale locale){
		SubDepartment subdepartment=SubDepartment.findById(SubDepartment.class, subdepartmentId);
		Session session = Session.findById(Session.class, Long.parseLong(request.getParameter("session")));
		Group group;
		MasterVO masterVO=new MasterVO();
		try {
			group = Group.find(subdepartment, session, locale);
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
				try {
					actors=WorkflowConfig.findQuestionActorsVO(question,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	public @ResponseBody List<Reference> getHouseByType(@PathVariable("houseType") final String houseType, final Locale locale){
		HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",houseType, locale.toString());
		List<Reference> houses = new ArrayList<Reference>();
		
		if(selectedHouseType != null){
			List<House> houseList = House.findAllByFieldName(House.class, "type",selectedHouseType, "firstDate",ApplicationConstants.DESC, locale.toString());
			for(House house : houseList){
				Reference ref = new Reference();
				ref.setId(house.getId().toString());
				ref.setName(house.getName());
				houses.add(ref);
			}
		}
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
	public @ResponseBody List<MasterVO> getSessionConfigAnsweringDates(@PathVariable("id") final Long id) {

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
	public @ResponseBody MasterVO getQuestionId(final ModelMap model, final HttpServletRequest request, final Locale locale){

		MasterVO masterVO = new MasterVO();
		String strNumber = request.getParameter("strQuestionNumber");
		String strSessionId = request.getParameter("strSessionId");
		String strDeviceTypeId = request.getParameter("deviceTypeId");
		String strLocale = request.getParameter("locale");
		CustomParameter customParameter = CustomParameter.
				findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		Integer qNumber = null;
		if(strNumber != null && !strNumber.isEmpty() 
			&& strSessionId != null && !strSessionId.isEmpty()
			&& strLocale != null && !strLocale.isEmpty()
			&& strDeviceTypeId != null && !strDeviceTypeId.isEmpty()){
			
			if(customParameter != null){
				String server = customParameter.getValue();
				if(server.equals("TOMCAT")){
					try {
						strNumber = new String(strNumber.getBytes("ISO-8859-1"), "UTF-8");						
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					qNumber = new Integer(FormaterUtil.
							getNumberFormatterNoGrouping(locale.toString()).parse(strNumber).intValue());
				} catch (ParseException e) {
					logger.error("Number parse exception.");
					masterVO.setId(new Long(-1));
					masterVO.setName("undefined");
				}	

				Session currentSession = Session.findById(Session.class, new Long(strSessionId));
				Question question = null;
			
				Map<String, String[]> params = new HashMap<String, String[]>();
				params.put("locale", new String[]{locale.toString()});
				params.put("sessionId", new String[]{currentSession.getId().toString()});
				params.put("qNumber", new String[]{qNumber.toString()});
				List data = Query.findReport("HDQ_REFER_QUESTION", params);
				
				if(data != null && !data.isEmpty()){
					String strId = null;
					try{
						strId = ((Object[])data.get(0))[0].toString();
						
						if(strId != null){
							question = Question.findById(Question.class, new Long(strId));
						}
					}catch(Exception e){
						logger.error("error", e);
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
		return getAutoCompleteData(request, session, locale);
	}
	
	private List<AutoCompleteVO> getAutoCompleteData(HttpServletRequest request, Long session,Locale locale){
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> memberVOs = new ArrayList<MasterVO>();
		List<AutoCompleteVO> autoCompleteVOs = new ArrayList<AutoCompleteVO>();
		Session selectedSession = Session.findById(Session.class, session);
		House house = selectedSession.getHouse();
		Long primaryMemberId = null;
		if(request.getParameter("primaryMemberId") != null){
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
		if(customParameter != null){
			String server = customParameter.getValue();
			if(server.equals("TOMCAT")){
				String strParam = request.getParameter("term");
				try {
					String param = new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
					memberVOs = HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house, selectedSession, locale.toString(), param,primaryMemberId);
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}else{
				String param = request.getParameter("term");
				memberVOs = HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house,selectedSession, locale.toString(), param, primaryMemberId);
			}
		}
		for(MasterVO i : memberVOs){
			AutoCompleteVO autoCompleteVO = new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}
		
		return autoCompleteVOs;
	}
	
	@RequestMapping(value = "/member/supportingmembers/fromsession", method = RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getMemberAutosuggest(final HttpServletRequest request,
			final Locale locale){
		HouseType houseType = null;
		SessionType sessionType = null;
		Integer sessionYear = null;
		Session session = null;
		
		try{
			String strHouseType1 = request.getParameter("houseType1");
			String strSessionYear1 = request.getParameter("sessionYear1");
			String strSessionType1 = request.getParameter("sessionType1");
			String strHouseType2 = request.getParameter("houseType2");
			String strSessionYear2 = request.getParameter("sessionYear2");
			String strSessionType2 = request.getParameter("sessionType2");
			
			if(strHouseType1 != null && !strHouseType1.isEmpty()){
				try{
					houseType = HouseType.findByType(strHouseType1, locale.toString());
				}catch(Exception e){
					logger.error("error", e);
					if(strHouseType2 != null && !strHouseType2.isEmpty()){
						houseType = HouseType.findByType(strHouseType2, locale.toString());
					}
				}
			}
			
			if(strSessionType1 != null && !strSessionType1.isEmpty()){
				try{
					sessionType = SessionType.findById(SessionType.class, new Long(strSessionType1));
				}catch(Exception e){
					logger.error("error", e);
					if(strSessionType2 != null && !strSessionType2.isEmpty()){
						sessionType = SessionType.findById(SessionType.class, new Long(strSessionType2));
					}
				}
			}
			
			if(strSessionYear1 != null && !strSessionYear1.isEmpty()){
				try{
					sessionYear = Integer.parseInt(strSessionYear1);
				}catch(Exception e){
					logger.error("error", e);
					if(strSessionYear2 != null && !strSessionYear2.isEmpty()){
						sessionYear = Integer.parseInt(strSessionYear2);;
					}
				}
			}
			
			try{
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			}catch(Exception e){
				logger.error("error", e);
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return getAutoCompleteData(request, session.getId(), locale);
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
					receivingDate = FormaterUtil.getDateFormatter("yyyy-MM-dd hh:mm:ss", ApplicationConstants.STANDARD_LOCALE).parse(strReceivingDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				formattedDate = FormaterUtil.formatDateToString(receivingDate, "dd/MM/yyyy hh:mm:ss", locale.toString());
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
	public @ResponseBody List<Reference> findResolutionActors(final HttpServletRequest request, final ModelMap model, final Locale locale){
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
	public @ResponseBody List<MasterVO> loadSubWorkflowByDeviceType(final HttpServletRequest request, final ModelMap model, final Locale locale){
		List<MasterVO> workflowTypes = new ArrayList<MasterVO>();
		
		try{
			DeviceType deviceType = null;
			String server = null;
			CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(customParameter != null){
				server = customParameter.getValue();
				String strDeviceType = request.getParameter("deviceType");
				if(!strDeviceType.isEmpty()){
					if(server.equals("TOMCAT")){
						try {
							String param = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
							deviceType = DeviceType.findByName(DeviceType.class, param, locale.toString());
						}catch (UnsupportedEncodingException e) {
							logger.error("Cannot Encode the Parameter.");
						}
					}else{
						deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
					}
				}
				
				
				if(deviceType != null){
					List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
					StringBuffer statuses = new StringBuffer();
					if(userGroups != null){
						for(UserGroup i : userGroups){
							UserGroup userGroup = UserGroup.findById(UserGroup.class,i.getId());
							String userGroupDeviceType = userGroup.getParameterValue(ApplicationConstants.DEVICETYPE_KEY+"_"+locale);
							if(userGroupDeviceType.contains(deviceType.getName())){
								/**** Authenticated User's usergroup and usergroupType ****/
								String userGroupType = i.getUserGroupType().getType();
								/**** Status Allowed ****/
								CustomParameter allowedWorkflowTypes = CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_"+deviceType.getType().toUpperCase()+"_"+userGroupType.toUpperCase(), "");
								if(allowedWorkflowTypes != null){
									List<Status> workflowTypesForUsergroup = new ArrayList<Status>();
									workflowTypesForUsergroup = Status.findStatusContainedIn(allowedWorkflowTypes.getValue(), locale.toString());
									
									for(Status status : workflowTypesForUsergroup){
										
										MasterVO statusVO = new MasterVO();
										statusVO.setName(status.getName());
										statusVO.setValue(status.getType());
										if(!workflowTypes.contains(statusVO)){
											workflowTypes.add(statusVO);
										}
									}
								}else{
									allowedWorkflowTypes = CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_"+deviceType.getType().toUpperCase(), ""); 
									if(allowedWorkflowTypes != null){
										List<Status> workflowTypesForUsergroup = new ArrayList<Status>();
										workflowTypesForUsergroup = Status.findStatusContainedIn(allowedWorkflowTypes.getValue(), locale.toString());
										for(Status status : workflowTypesForUsergroup){
											
											MasterVO statusVO = new MasterVO();
											statusVO.setName(status.getName());
											statusVO.setValue(status.getType());
											if(!workflowTypes.contains(statusVO)){
												workflowTypes.add(statusVO);
											}
										}
									}
								}
							}						
						}
						
						if(workflowTypes.isEmpty()) {
							CustomParameter defaultAllowedWorkflowTypes=CustomParameter.findByName(CustomParameter.class,"MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_BY_DEFAULT", "");
							if(defaultAllowedWorkflowTypes != null){
								List<Status> workflowTypeList = new ArrayList<Status>();
								workflowTypeList = Status.findStatusContainedIn(defaultAllowedWorkflowTypes.getValue(), locale.toString());
								for(Status status : workflowTypeList){
									
									MasterVO statusVO = new MasterVO();
									statusVO.setName(status.getName());
									statusVO.setValue(status.getType());
									if(!workflowTypes.contains(statusVO)){
										workflowTypes.add(statusVO);
									}
								}
							}else{
								logger.error("Custom Parameter 'MYTASK_GRID_WORKFLOW_TYPES_ALLOWED_BY_DEFAULT' not set");
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
			@RequestParam String daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession, final Locale locale){
		Reference reference = new Reference();
		
		Date lastDiscussionDate = null;
		Date submissionEndDate = null;

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
				lastDiscussionDate = FormaterUtil.formatStringToDate(lastDiscussionDateStr, "dd/MM/yyyy", locale.toString());
				
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
		String strHouseType=request.getParameter("houseType");
		DeviceType deviceType=null;
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
			server=customParameter.getValue();
		if(!strDeviceType.isEmpty()){
			if(server.equals("TOMCAT")){
				try {
					String param = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
					String htName = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
					if(htName!=null && !htName.isEmpty()) {
						HouseType houseType = HouseType.findByName(htName, locale.toString());
						if(houseType==null) {
							houseType = HouseType.findByType(htName, locale.toString());
						}
						if(houseType!=null) {
							if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
								deviceType=DeviceType.findByFieldName(DeviceType.class, "name_"+ApplicationConstants.LOWER_HOUSE, param, locale.toString());
								if(deviceType==null) {
									deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
								}
							} else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
								deviceType=DeviceType.findByFieldName(DeviceType.class, "name_"+ApplicationConstants.UPPER_HOUSE, param, locale.toString());
								if(deviceType==null) {
									deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
								}
							} else {
								deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
							}
						} else {
							deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
						}
					} else {
						deviceType=DeviceType.findByName(DeviceType.class, param, locale.toString());
					}					
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
			final Locale locale){
		String strMinistry=request.getParameter("ministry");
		Long ministryId=Long.parseLong(strMinistry);
		Ministry ministry=Ministry.findById(Ministry.class,ministryId);
		
		String strSessionId=request.getParameter("session");
		Long sessionId = Long.parseLong(strSessionId);
		Session session = Session.findById(Session.class, sessionId);
		
		Date onDate = session.getEndDate();
		if(onDate.before(new Date())) {
			CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
			if(csptNewHouseFormationInProcess==null) {
				onDate = new Date();
			} else if(csptNewHouseFormationInProcess.getValue()==null) {
				onDate = new Date();
			} else if(!csptNewHouseFormationInProcess.getValue().equals("YES")) {
				onDate = new Date();
			}	
		}
		List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry,
				onDate,
				locale.toString());
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		for(SubDepartment i:subDepartments){
			MasterVO masterVO=new MasterVO(i.getId(),i.getName());
			masterVO.setValue(String.valueOf(i.getDepartment().getId()));
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
	public @ResponseBody String isDiscussionDateEarlyInGovernmentResolution(final HttpServletRequest request, final Locale locale){
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
		List<CommitteeName> committeeNames = new ArrayList<CommitteeName>();
		List<UserGroup> usergroups = this.getCurrentUser().getUserGroups();
		for(UserGroup ug : usergroups){
			if(ug.getActiveFrom().before(new Date()) && ug.getActiveTo().after(new Date())){
				Map<String, String> parameters = UserGroup.findParametersByUserGroup(ug);
				String committeeNameParam = parameters.get(ApplicationConstants.COMMITTEENAME_KEY + "_" + locale);
				if(committeeNameParam != null && ! committeeNameParam.equals("")) {
					String cNames[] = committeeNameParam.split("##");
					for(String cName : cNames){
						List<CommitteeName> comNames = 
								CommitteeName.findAllByFieldName(CommitteeName.class, "displayName", cName, "displayName", "asc", locale.toString());
						if(comNames != null && !comNames.isEmpty()){
							committeeNames.addAll(comNames);
						}
					}
				}
			}
		}
		for(CommitteeName cn : committeeNames) {
			if(cn.getCommitteeType().equals(committeeType)){
				String id = String.valueOf(cn.getId());
				Reference reference = new Reference(id, cn.getDisplayName());
				references.add(reference);
			}
		}
		
		return references;
	}
	
	@RequestMapping(value="CommitteeMemberAttendance/{touritinerary}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> findCommitteeMemberAttendance(
			@PathVariable("touritinerary") final Long touritineraryId,
			final HttpServletRequest request,
			final Locale locale) throws ELSException {

		TourItinerary tourItinerary=TourItinerary.findById(TourItinerary.class, touritineraryId);
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		List<CommitteeMemberAttendance> committeeMemberAttendance=new ArrayList<CommitteeMemberAttendance>();
		
			committeeMemberAttendance = CommitteeMemberAttendance.findAll(tourItinerary,locale.toString());
			for(CommitteeMemberAttendance r: committeeMemberAttendance){
				MasterVO masterVO = new MasterVO();
				masterVO.setId(r.getId());
				masterVO.setName(r.getCommitteeMember().getMember().findFirstLastName());
				masterVO.setIsSelected(r.getAttendance());
				masterVOs.add(masterVO);
			}
			return masterVOs;
	
	}
	
	@RequestMapping(value="committeeName/{committeeName}/foundationDate", method=RequestMethod.GET)
	public @ResponseBody Reference findFoundationDate(@PathVariable("committeeName") final Long committeeNameId, final Locale locale) {
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
	
	@RequestMapping(value="committeeName/{committeeName}/committeeType", method=RequestMethod.GET)
	public @ResponseBody Reference findcommitteeType(@PathVariable("committeeName") final Long committeeNameId, final Locale locale) {
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, committeeNameId);
				
		CommitteeType committeeType = CommitteeType.findById(CommitteeType.class, committeeName.getCommitteeType().getId());
		
		
		Reference reference = new Reference();
		reference.setId(String.valueOf(committeeName.getId()));
		reference.setName(committeeType.getName());
		
		return reference;
	}
	@RequestMapping(value="committeeName/{committeeName}/committeeTours", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findcommitteeTours(@PathVariable("committeeName") final Long committeeNameId, final Locale locale) {
		
		List<Reference> references = new ArrayList<Reference>();
		CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, committeeNameId);
		//Committee committee = Committee.findByFieldName(Committee.class,"committeeName",committeeName,locale.toString());
		
		Committee committee = Committee.findActiveCommittee(committeeName, new Date(), locale.toString());
			
			List<CommitteeTour> committeetours = 
				CommitteeTour.findCommitteeTours(committee, locale.toString());
		
			for(CommitteeTour cn : committeetours) {
			String id = String.valueOf(cn.getId());
			Reference reference = new Reference(id, cn.getSubject());
			references.add(reference);
		}
		
		return references;
	}
	
	@RequestMapping(value="committee/dissolutionDate", method=RequestMethod.GET)
	public @ResponseBody Reference computeDissolutionDate(@RequestParam("committeeName") final Long committeeNameId,
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
	public @ResponseBody List<MasterVO> findCommitteeNamesByHouseType(@PathVariable("houseType") final Long houseTypeId,
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
	public @ResponseBody List<Status> loadSubWorkflowByModule(final HttpServletRequest request,
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
			e.printStackTrace();
		}
		
		return workflowTypes;
	}
	
	@RequestMapping(value="partyType/{partyType}/members", method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getPartyTypewiseMembers(final HttpServletRequest request,
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

	@RequestMapping(value="houseType/{houseType}/members", method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getHouseTypeWiseActiveMembers(final HttpServletRequest request,
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
	
	@RequestMapping(value="committee/actors/workflow/{workflowName}", method=RequestMethod.GET)
	public @ResponseBody List<Reference> getCommitteeActors(@PathVariable("workflowName") final String workflowName,
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
	
	@RequestMapping(value="/towns/bydistricts", method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> getTowns(
			final HttpServletRequest request,
			final Locale locale){
		String[] districts=request.getParameterValues("districts[]");
		//populating sub departments
		List<MasterVO> townVOs=new ArrayList<MasterVO>();
		if(districts != null){
			
			List<Town> towns=Town.findTownsbyDistricts(districts, locale.toString());
			
			for(Town i:towns){
				MasterVO masterVO=new MasterVO();
				masterVO.setId(i.getId());
				masterVO.setName(i.getName());
				townVOs.add(masterVO);
			}
		}
		return townVOs;
	}
	
	
	@RequestMapping(value="/zillaparishads/bydistricts", method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> getzillaparishads(
			final HttpServletRequest request,
			final Locale locale){
		String[] districts=request.getParameterValues("districts[]");
		//populating sub departments
		List<MasterVO> zillaparishadVOs=new ArrayList<MasterVO>();
		if(districts != null){
			
			List<Zillaparishad> zillaparishads=Zillaparishad.findZillaparishadsbyDistricts(districts, locale.toString());
			
			for(Zillaparishad i:zillaparishads){
				MasterVO masterVO=new MasterVO();
				masterVO.setId(i.getId());
				masterVO.setName(i.getName());
				zillaparishadVOs.add(masterVO);
			}
		}
		return zillaparishadVOs;
	}
	
	@RequestMapping(value="district/{districtId}/towns", method=RequestMethod.GET)
	public @ResponseBody List<Reference> getTownsByDistrict(@PathVariable("districtId") final Long districtId, final Locale localeObj) {
		List<Reference> refs = new ArrayList<Reference>();
		
		District district = District.findById(District.class, districtId);
		String locale = localeObj.toString();
		
		List<Town> towns = Town.find(district, locale);
		for(Town town : towns) {
			Reference ref = new Reference();
			ref.setId(String.valueOf(town.getId()));
			ref.setName(town.getName());
			refs.add(ref);
		}
		
		
		return refs;
	}
	
	@RequestMapping(value="district/{districtId}/zp", method=RequestMethod.GET)
	public @ResponseBody List<Reference> getZPByDistrict(@PathVariable("districtId") final Long districtId, final Locale localeObj) {
		List<Reference> refs = new ArrayList<Reference>();
		
		District district = District.findById(District.class, districtId);
		String locale = localeObj.toString();
		
//		List<Zillaparishad> zillaparishads = Zillaparishad.find(district, locale);
//		for(Zillaparishad zillaparishad : zillaparishads) {
//			Reference ref = new Reference();
//			ref.setId(String.valueOf(zillaparishad.getId()));
//			ref.setName(zillaparishad.getName());
//			refs.add(ref);
//		}
		
		
		return refs;
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
	
	/****RIS Related Ajax Calls****/
	@RequestMapping(value="/roster/actions",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findRosterActions(final HttpServletRequest request, final Locale locale){
		String strStartTime=request.getParameter("startTime");
		String strEndTime=request.getParameter("endTime");
		String strSlotDuration=request.getParameter("slotDuration");
		String strRoster=request.getParameter("roster");
		String strReporterSize=request.getParameter("reporterSize");
		List<Reference> references=new ArrayList<Reference>();
		if(strStartTime!=null&&!strStartTime.isEmpty()
				&&strEndTime!=null&&!strEndTime.isEmpty()
				&&strSlotDuration!=null&&!strSlotDuration.isEmpty()
				&&strRoster!=null&&!strRoster.isEmpty()
				&&strReporterSize!=null && !strReporterSize.isEmpty()){
			CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
			Date startTime=null;
			Date endTime=null;
			Integer slotDuration=Integer.parseInt(strSlotDuration);
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),locale.toString());
				try {
					startTime=format.parse(strStartTime);
					endTime=format.parse(strEndTime);
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						startTime=defaultFormat.parse(strStartTime);
						endTime=defaultFormat.parse(strEndTime);
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
			/**** Conditions ****/
			Roster roster=Roster.findById(Roster.class,Long.parseLong(strRoster));
			Date storedStartTime=roster.getStartTime();
			Date storedEndTime=roster.getEndTime();
			Integer storedSlotDuration=roster.getSlotDuration();
			List<Reporter> reporters=Roster.findReportersByActiveStatus(roster, true);
			Integer storedReporterSize=reporters.size();
			CustomParameter actionParameter=null;
			String event="";
			/**** StartTime/EndTime/Slot Duration are changed ****/
			if(storedStartTime!=null&&!(startTime.equals(storedStartTime))
					&&storedEndTime!=null&&!(endTime.equals(storedEndTime))
					&&storedSlotDuration!=null&&slotDuration!=storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_STARTTIME_ENDTIME_SLOTDURATION_CHANGED","");
				event="STARTTIME_ENDTIME_SLOTDURATION_CHANGED";
			}
			/**** StartTime/Slot Duration are changed ****/
			else if(storedStartTime!=null&&!(startTime.equals(storedStartTime))
					&&storedEndTime!=null&&(endTime.equals(storedEndTime))
					&&storedSlotDuration!=null&&slotDuration!=storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_STARTTIME_SLOTDURATION_CHANGED","");
				event="STARTTIME_SLOTDURATION_CHANGED";
			}
			/**** EndTime Preponded/Slot Duration are changed ****/
			else if(storedStartTime!=null&&(startTime.equals(storedStartTime))
					&&storedEndTime!=null&&(endTime.before(storedEndTime))
					&&storedSlotDuration!=null&&slotDuration!=storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_ENDTIME_PREPONDED_SLOTDURATION_CHANGED","");
				event="ENDTIME_PREPONDED_SLOTDURATION_CHANGED";
			}
			/**** EndTime Postponded/Slot Duration are changed ****/
			else if(storedStartTime!=null&&(startTime.equals(storedStartTime))
					&&storedEndTime!=null&&(endTime.after(storedEndTime))
					&&storedSlotDuration!=null&&slotDuration!=storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_ENDTIME_POSTPONDED_SLOTDURATION_CHANGED","");
				event="ENDTIME_POSTPONDED_SLOTDURATION_CHANGED";
			}
			/**** StartTime/EndTime are changed ****/
			else if(storedStartTime!=null&&!(startTime.equals(storedStartTime))
					&&storedEndTime!=null&&!(endTime.equals(storedEndTime))
					&&storedSlotDuration!=null&&slotDuration==storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_STARTTIME_ENDTIME_CHANGED","");
				event="STARTTIME_ENDTIME_CHANGED";
			}
			/**** StartTime is preponded/postponded ****/
			else if(storedStartTime!=null&&(startTime.before(storedStartTime)||startTime.after(storedStartTime))
					&&storedEndTime!=null&&endTime.equals(storedEndTime)
					&&storedSlotDuration!=null&&slotDuration==storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_STARTTIME_CHANGED","");
				event="STARTTIME_CHANGED";
			}/**** EndTime is preponded ****/
			else if(storedStartTime!=null&&startTime.equals(storedStartTime)
					&&storedEndTime!=null&&endTime.before(storedEndTime)
					&&storedSlotDuration!=null&&slotDuration==storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_ENDTIME_PREPONDED","");
				event="ENDTIME_PREPONDED";
			}/**** EndTime is postponded ****/
			else if(storedStartTime!=null&&startTime.equals(storedStartTime)
					&&storedEndTime!=null&&endTime.after(storedEndTime)
					&&storedSlotDuration!=null&&slotDuration==storedSlotDuration){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_ENDTIME_POSTPONDED","");
				event="ENDTIME_POSTPONDED";
			}/**** Slot Duration is changed ****/
			else if(storedStartTime!=null&&startTime.equals(storedStartTime)
					&&storedEndTime!=null&&endTime.equals(storedEndTime)
					&&storedSlotDuration!=null&&slotDuration!=storedSlotDuration&&slotDuration>=0){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_SLOTDURATION_CHANGED","");
				event="SLOTDURATION_CHANGED";
			}else if(storedStartTime!=null && startTime.equals(storedStartTime)
					&&storedEndTime!=null && endTime.equals(storedEndTime)
					&&storedReporterSize!=null /*&& storedReporterSize!=Integer.parseInt(strReporterSize)*/){
				actionParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_REPORTER_CHANGED","");
				event="REPORTER_CHANGED";
			}
			if(actionParameter!=null){
				String[] actions=actionParameter.getValue().split(",");
				for(String i:actions){
					Reference reference=new Reference(i,event);
					references.add(reference);
				}				
			}
		}	
		return references;
	}
	
	@RequestMapping(value="/findLatestListNumberForGivenDeviceType",method=RequestMethod.GET)
	public @ResponseBody int findLatestListNumberForGivenDeviceType(final HttpServletRequest request, final Locale locale) {
		int latestListNumber=-1;
		String strHouseType=request.getParameter("houseType");
	    String strSessionType=request.getParameter("sessionType");
	    String strSessionYear=request.getParameter("sessionYear");	    
	    String strDeviceType=request.getParameter("questionType");
	    if(strDeviceType == null){
			strDeviceType = request.getParameter("deviceType");
		}
	    if(strHouseType!=null && strSessionType!=null && strSessionYear!=null && strDeviceType!=null) {
	    	if(!strHouseType.isEmpty() && !strSessionType.isEmpty() && !strSessionYear.isEmpty() && !strDeviceType.isEmpty()) {
	    		HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
	            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
	            Integer sessionYear=Integer.parseInt(strSessionYear);
	            Session session = null;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				} catch (ELSException e) {
					e.printStackTrace();
				}
	            DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
	            if(deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
	            	List<Question> questionsForCurrentList = Question.findAdmittedQuestionsOfGivenTypeWithoutListNumberInSession(session.getId(), deviceType.getId());
	            	if(questionsForCurrentList!=null && !questionsForCurrentList.isEmpty()) {
	            		Integer highestListNumberForCurrentSession = Question.findHighestListNumberForAdmittedQuestionsOfGivenTypeInSession(session.getId(), deviceType.getId());
	            		if(highestListNumberForCurrentSession == null) {
	            			boolean isListDoneForAnyNextSession = Question.isAdmittedQuestionOfGivenTypeWithListNumberInNextSessions(session.getId(), houseType.getType(), deviceType.getId());
	            			if(isListDoneForAnyNextSession==false) {
	            				Session previousSession = null;
								try {
									previousSession = Session.findPreviousSession(session);
								} catch (ELSException e) {
									e.printStackTrace();
								}
	            				if(previousSession != null) {
	            					List<Question> questionsForListInPreviousSession = Question.findAdmittedQuestionsOfGivenTypeWithoutListNumberInSession(previousSession.getId(), deviceType.getId());
	            					if(questionsForListInPreviousSession!=null && questionsForListInPreviousSession.isEmpty()) {
	            						Integer highestListNumberForPreviousSession = Question.findHighestListNumberForAdmittedQuestionsOfGivenTypeInSession(previousSession.getId(), deviceType.getId());
	            						latestListNumber = highestListNumberForPreviousSession;
	            						return latestListNumber;
	            					} else {
	            						latestListNumber = 0;
			            				return latestListNumber;
	            					}
	            				} else {
	            					latestListNumber = 0;
		            				return latestListNumber;
	            				}
	            			} else {
	            				latestListNumber = 0;
	            				return latestListNumber;
	            			}
	            		} else {
	            			latestListNumber = highestListNumberForCurrentSession;
	            			return latestListNumber;
	            		}
	            	} else {
	            		return latestListNumber;
	            	}
	            }
	    	}
	    }
		return 0;
	}
	
	@RequestMapping(value="/rosterdays",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getRosterDaysFromSession(final HttpServletRequest request, final Locale locale){
		String strhouseType=request.getParameter("houseType");
		String stryear=request.getParameter("sessionYear");
		String strsessionType=request.getParameter("sessionType");
		String strlanguage=request.getParameter("language");
		//List<Integer> rosterDays=new ArrayList<Integer>();
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		if(strhouseType!=null&&stryear!=null&&strsessionType!=null){
			HouseType selectedHouseType=HouseType.findByFieldName(HouseType.class,"type",strhouseType,locale.toString());
			SessionType selectedSessionType=SessionType.findById(SessionType.class, Long.parseLong(strsessionType));
			Integer year=Integer.parseInt(stryear);
			Language language=Language.findById(Language.class, Long.parseLong(strlanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
			} catch (ELSException e) {
				e.printStackTrace();
			}
			List<Roster> rosters=Roster.findAllRosterBySessionAndLanguage(session,language, locale.toString());
			for(Roster r:rosters){
				MasterVO masterVO = new MasterVO();
				masterVO.setNumber(r.getDay());
				masterVO.setName(FormaterUtil.formatDateToString(r.getStartTime(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
				masterVOs.add(masterVO);
				//rosterDays.add(r.getDay());
			}
		}
		return masterVOs;
	}
	
	
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getData(final HttpServletRequest request,
			final Locale locale){
		String key=request.getParameter("key");
		String keys[]=key.split(",");
		String param=null;
		String searchKey="";
		String server=null;
		for(int i=0;i<keys.length;i++){
			if(Integer.parseInt(keys[i])==17){
				searchKey=searchKey+"ctrl"+"+";
			}else if(Integer.parseInt(keys[i])==18){
				searchKey=searchKey+"alt"+"+";
			}else if(Integer.parseInt(keys[i])>=65 && Integer.parseInt(keys[i])<=90){
				searchKey=searchKey+((char)Integer.parseInt(keys[i]));
			}
		}
		IdentificationKey identificationKey=IdentificationKey.findByFieldName(IdentificationKey.class, "identificationkey", searchKey, locale.toString());
		String masterName=identificationKey.getMaster();
		String searchfield=identificationKey.getSearchField();
		String term=request.getParameter("term");
		String[] searchFields=searchfield.split(",");
		List<MasterVO> results=new ArrayList<MasterVO>();
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(customParameter!=null){
			server=customParameter.getValue();
			if(!term.isEmpty()){
				if(server.equals("TOMCAT")){
					try {
						 param = new String(term.getBytes("ISO-8859-1"),"UTF-8");
						
					}catch (UnsupportedEncodingException e) {
						logger.error("Cannot Encode the Parameter.");
					}
				}
			}
		}
		if(masterName!=null && !masterName.equals("")){
			if(masterName.equals("Department")){
				List<Department> departments=Department.findAllByLikeParameter(Department.class, searchFields, param, locale.toString());
				for(Department d:departments){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(d.getName());
					masterVO.setValue(d.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Constituency")){
				List<Constituency> constituencies=Constituency.findAllByLikeParameter(Constituency.class, searchFields, param, locale.toString());
				for(Constituency c:constituencies){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(c.getDisplayName());
					masterVO.setValue(c.getDisplayName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Member")){
				List<Member> members=Member.findAllByLikeParameter(Member.class, searchFields, param, locale.toString());
				for(Member m:members){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(m.getFullname());
					masterVO.setValue(m.getFullname());
					results.add(masterVO);
				}
			}else if(masterName.equals("Resolution")){
				List<Resolution> resolutions=Resolution.findAllByFieldName(Resolution.class, "number", param,"number" ,"asc",locale.toString());
				for(Resolution r:resolutions){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(r.getNumber()+" "+r.getHouseType().getName()+" "+r.getSession().getType().getSessionType()+"  "+r.getSession().getYear());
					masterVO.setValue(r.getSubject()+"\n"+r.getNoticeContent());
					results.add(masterVO);
				}
			}else if(masterName.equals("Question")){
				List<Question> questions=Question.findAllByFieldName(Question.class, "number", param,"number" ,"asc",locale.toString());
				for(Question q:questions){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(q.getNumber()+" "+q.getHouseType().getType()+" "+q.getSession().getType().getSessionType()+" "+q.getSession().getYear());
					masterVO.setValue(q.getSubject()+"\n"+q.getQuestionText());
					results.add(masterVO);
				}
			}else if(masterName.equals("GovernmentScheme")){
				List<GovernmentScheme> schemes=GovernmentScheme.findAllByLikeParameter(GovernmentScheme.class, searchFields, param, locale.toString());
				for(GovernmentScheme gs:schemes){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(gs.getName());
					masterVO.setValue(gs.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("GovernmentProject")){
				List<GovernmentProject> projects=GovernmentProject.findAllByLikeParameter(GovernmentProject.class, searchFields, param, locale.toString());
				for(GovernmentProject gp:projects){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(gp.getName());
					masterVO.setValue(gp.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("GovernmentProgram")){
				List<GovernmentProgram> programs=GovernmentProgram.findAllByLikeParameter(GovernmentProgram.class, searchFields, param, locale.toString());
				for(GovernmentProgram gp:programs){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(gp.getName());
					masterVO.setValue(gp.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("GovernmentCorporation")){
				List<GovernmentCorporation> corporations=GovernmentCorporation.findAllByLikeParameter(GovernmentCorporation.class, searchFields, param, locale.toString());
				for(GovernmentCorporation gc:corporations){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(gc.getName());
					masterVO.setValue(gc.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Dam")){
				List<Dam> dams=Dam.findAllByLikeParameter(Dam.class, searchFields, param, locale.toString());
				for(Dam d:dams){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(d.getName());
					masterVO.setValue(d.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("River")){
				List<River> rivers=River.findAllByLikeParameter(River.class, searchFields, param, locale.toString());
				for(River r:rivers){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(r.getName());
					masterVO.setValue(r.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("University")){
				List<University> universities=University.findAllByLikeParameter(University.class, searchFields, param, locale.toString());
				for(University u:universities){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(u.getName());
					masterVO.setValue(u.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Sanctuary")){
				List<Sanctuary> sanctuaries=Sanctuary.findAllByLikeParameter(Sanctuary.class, searchFields, param, locale.toString());
				for(Sanctuary s:sanctuaries){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(s.getName());
					masterVO.setValue(s.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Fort")){
				List<Fort> forts=Fort.findAllByLikeParameter(Fort.class, searchFields, param, locale.toString());
				for(Fort f:forts){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(f.getName());
					masterVO.setValue(f.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Creek")){
				List<Creek> creeks=Creek.findAllByLikeParameter(Creek.class, searchFields, param, locale.toString());
				for(Creek c:creeks){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(c.getName());
					masterVO.setValue(c.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Abbreviation")){
				List<Abbreviation> abbreviations=Abbreviation.findAllByLikeParameter(Abbreviation.class, searchFields, param, locale.toString());
				for(Abbreviation a:abbreviations){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(a.getName());
					masterVO.setValue(a.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Ghat")){
				List<Ghat> ghats=Ghat.findAllByLikeParameter(Ghat.class, searchFields, param, locale.toString());
				for(Ghat g:ghats){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(g.getName());
					masterVO.setValue(g.getName());
					results.add(masterVO);
				}
			}else if(masterName.equals("Highway")){
				List<Highway> highways=Highway.findAllByLikeParameter(Highway.class, searchFields, param, locale.toString());
				for(Highway h:highways){
					MasterVO masterVO=new MasterVO();
					masterVO.setName(h.getName());
					masterVO.setValue(h.getName());
					results.add(masterVO);
				}
			}
		}
		return results;
	}
	
	@RequestMapping(value="/slot",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getSlot(final HttpServletRequest request, final Locale locale,final ModelMap model){
		Language language=null;
		List<Slot> slots=new ArrayList<Slot>();
		List<MasterVO> masterVos=new ArrayList<MasterVO>();
		String strLanguage=request.getParameter("language");
		String strSlot=request.getParameter("currentSlot");
		if(strLanguage!=null && !strLanguage.equals("")){
			 language=Language.findByFieldName(Language.class, "type", strLanguage, locale.toString());
		}
		Slot slot=null;
		if(strSlot!=null && !strSlot.equals("")){
			slot=Slot.findById(Slot.class, Long.parseLong(strSlot));
		}
		if(strLanguage!=null && !strLanguage.equals("")){
			slots=Slot.findSlotsByLanguageContainingSlotTime(language,slot); 
		}
		for(Slot s:slots){
			MasterVO masterVo=new MasterVO();
			masterVo.setId(s.getId());
			masterVo.setName(s.getName());
			masterVo.setValue(FormaterUtil.formatDateToString(s.getStartTime(), "HH:mm", locale.toString()));
			masterVo.setType(FormaterUtil.formatDateToString(s.getEndTime(), "HH:mm", locale.toString()));
			Reporter r = s.getReporter();
			User u = r.getUser();
			Credential credential = u.getCredential();
			masterVo.setDisplayName(credential.getUsername());
			masterVos.add(masterVo);
		}
		return masterVos;
		
	}
	
	@RequestMapping(value="/bookmarktext",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getBookmarkTextToBeReplaced(final HttpServletRequest request, final Locale locale,final ModelMap model){
		String strSlot=request.getParameter("slot");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		if(strSlot!=null && !strSlot.isEmpty()){
			String slots[]=strSlot.split(",");
			for(int i=0;i<slots.length;i++){
				Slot slot=Slot.findById(Slot.class, Long.parseLong(slots[i]));
					if(slot!=null){
					Proceeding proceeding=Proceeding.findByFieldName(Proceeding.class, "slot", slot, locale.toString());
					List<Part> parts = Part.findAllByFieldName(Part.class, "proceeding", proceeding, "orderNo", "asc", locale.toString());
					if(!parts.isEmpty()){
						for(Part p:parts){
							MasterVO masterVo=new MasterVO();
							if(p.getPrimaryMember()!=null){
								masterVo.setValue(p.getPrimaryMember().getFullname());
							}
							masterVo.setId(p.getId());
							//masterVo.setValue(proceeding.getSlot().getName());
							masterVo.setName(p.getRevisedContent());
							masterVo.setType(p.getPageHeading());
							masterVo.setDisplayName(p.getMainHeading());
							masterVo.setValue(p.getProceeding().getId().toString());
							masterVOs.add(masterVo);
						}
						
					}
				}
			}
			
		}

		return masterVOs;
	}
		
	
	@RequestMapping(value="/session", method=RequestMethod.GET)
	public @ResponseBody MasterVO getSession(final HttpServletRequest request,ModelMap model,
			final Locale locale) {
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		Session selectedSession=null;
		//populating departments
		if(strHouseType!=null && !strHouseType.equals("") &&
			strSessionType!=null && !strSessionType.equals("")&&
			strSessionYear!=null && !strSessionYear.equals("")){
			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType selectedSessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			try {
				selectedSession=Session.findSessionByHouseTypeSessionTypeYear(houseType, selectedSessionType,Integer.parseInt(strSessionYear) );
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}
		if(selectedSession!=null){
			MasterVO masterVO=new MasterVO(selectedSession.getId(),"");
			return masterVO;
		}else{
			return new MasterVO();
		}
	}
	
	
	@RequestMapping(value="/device",method=RequestMethod.GET)
	public String getDeviceContent(ModelMap model, HttpServletRequest request,HttpServletResponse response,Locale locale){

		MasterVO masterVO = new MasterVO();

		String strNumber=request.getParameter("number");
		String strSessionId = request.getParameter("session");
		String strDeviceTypeId= request.getParameter("deviceType");
		String viewName = request.getParameter("viewName");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");

		Integer dNumber=null;
		Long deviceTypeId=null;

		if(strNumber!=null && strSessionId!=null && locale!=null && strDeviceTypeId!=null){
			if(strNumber.trim().length() > 0 && locale.toString().trim().length() > 0 && strSessionId.trim().length() > 0 && strDeviceTypeId.length()>0){
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
						dNumber=new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strNumber).intValue());
						deviceTypeId = new Long(strDeviceTypeId);
					} catch (ParseException e) {
						logger.error("Number parse exception.");
						masterVO.setId(new Long(-1));
						masterVO.setName("undefined");

						//return masterVO;
					}
				}	
				DeviceType deviceType=DeviceType.findById(DeviceType.class, deviceTypeId);
				String device=deviceType.getDevice();
				Session currentSession = Session.findById(Session.class, new Long(strSessionId));
				if(device.equals("Question")){
					String content="";
					Question question=Question.getQuestion(currentSession.getId(), deviceTypeId, dNumber, locale.toString());
					if(question!=null){
						if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
							Question referencedQuestion=question.getHalfHourDiscusionFromQuestionReference();
							if(referencedQuestion!=null){
								model.addAttribute("questionSubject",referencedQuestion.getSubject());
								model.addAttribute("number", referencedQuestion.getNumber());
								model.addAttribute("deviceType",referencedQuestion.getType().getName());
							
								String answeringDate=FormaterUtil.formatDateToString(referencedQuestion.getAnsweringDate().getAnsweringDate(), "dd MMM yyyy", locale.toString());
								model.addAttribute("answeringDate",FormaterUtil.formatMonthsForLocaleLanguage(answeringDate,locale.toString()));
								model.addAttribute("member",question.getPrimaryMember().findFirstLastName());
								model.addAttribute("questionId", question.getId());
								if(viewName==null || viewName.isEmpty()) {
									viewName = "proceeding/contentimports/questions_halfanhourfromquestion_content";
								}
							}
						}else{
							model.addAttribute("questionId", question.getId());
							model.addAttribute("questionSubject",question.getRevisedSubject());
							model.addAttribute("questionNumber",question.getNumber());
							model.addAttribute("formattedQuestionNumber",FormaterUtil.formatNumberNoGrouping(question.getNumber(), locale.toString()));
							if(question.getRevisedQuestionText() != null && !question.getRevisedQuestionText().isEmpty()){
								String questionText = question.getRevisedQuestionText();
								questionText = questionText.replaceAll("<p", "<div");
								questionText = questionText.replaceAll("</p", "</div");
								model.addAttribute("questionText",questionText);
							}
							
							model.addAttribute("answer",question.getAnswer());
							model.addAttribute("questionPrimaryMember",question.getPrimaryMember().findFirstLastName());
							String supportingMembers = question.findAllMemberNamesWithConstituencies(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
							model.addAttribute("supportingMember",supportingMembers);
							if(question.getMinistry()!=null){
								Ministry ministry=question.getMinistry();
								Member member=MemberMinister.findMemberHavingMinistryInSession(question.getSession(), ministry);
								model.addAttribute("minister",member.findFirstLastName());
								model.addAttribute("ministryName", question.getSubDepartment().getName());
							}
						}
					}
					if(viewName==null || viewName.isEmpty()) {
						viewName = "proceeding/contentimports/questions_starred_content";
					}
				}else if(device.equals("Resolution")){
					Resolution resolution=Resolution.getResolution(currentSession.getId(), deviceTypeId, dNumber, locale.toString());
					if(resolution!=null){
						if(resolution.getRevisedNoticeContent()!= null && !resolution.getRevisedNoticeContent().isEmpty()){
							String noticeContent  = resolution.getRevisedNoticeContent();
							noticeContent = noticeContent.replaceAll("<p", "<div");
							noticeContent = noticeContent.replaceAll("</p", "</div");
						}
						model.addAttribute("noticeContent", resolution.getNoticeContent());
						model.addAttribute("resolutionId",resolution.getId());
					}
					if(viewName==null || viewName.isEmpty()) {
						viewName = "proceeding/contentimports/resolutions_content";
					}
				}else if(device.equals("Motion")){
					String content="";
					Motion motion=Motion.getMotion(currentSession.getId(), deviceTypeId, dNumber, locale.toString());
					String isReply=request.getParameter("isReplyRequired");
					Boolean isReplyRequired=Boolean.parseBoolean(isReply);
					if(motion!=null){
						model.addAttribute("motionNumber",motion.getNumber());
						model.addAttribute("motionSubject",motion.getSubject());
						model.addAttribute("motionDetails",motion.getDetails());
						model.addAttribute("motionReply",motion.getReply());
						model.addAttribute("motionId",motion.getId());
						model.addAttribute("isReplyRequired",isReplyRequired);
						model.addAttribute("motionPrimaryMember",motion.getPrimaryMember().getFullname());
						if(!motion.getSupportingMembers().isEmpty()){
							for(SupportingMember m:motion.getSupportingMembers()){
								content=content+","+m.getMember().findFirstLastName();
							}
						}
						model.addAttribute("supportingMember",content);
						if(motion.getMinistry()!=null){
							Ministry ministry=motion.getMinistry();
							Member member=MemberMinister.findMemberHavingMinistryInSession(motion.getSession(), ministry);
							model.addAttribute("minister",member.findFirstLastName());
						}
						model.addAttribute("houseType", motion.getHouseType().getType());
					}
					if(viewName==null || viewName.isEmpty()) {
						viewName = "proceeding/contentimports/motions_callingattention_content";
					}
				}else if(device.equals("Bill")){
					String billyear=request.getParameter("billYear");
					String billHouseType=request.getParameter("billHouseType");
					Bill bill=null;
					if(billyear!=null && !billyear.isEmpty() && 
						billHouseType!=null && !billHouseType.isEmpty()){
						Integer billYear=Integer.parseInt(billyear);
						Long houseTypeId=Long.parseLong(billHouseType);
						bill=Bill.findByNumberYearAndHouseType(dNumber, billYear, houseTypeId, locale.toString());
						model.addAttribute("billYear",billyear);
					}
					CustomParameter languageParameter=CustomParameter.findByName(CustomParameter.class, ApplicationConstants.REPORTING_BILL_LANGUAGES, "");
					List<TextDraft> titles=bill.getRevisedTitles();
					if(languageParameter!=null){
						String[] languages=languageParameter.getValue().split(",");
						for(int i=0;i<languages.length;i++){
							for(TextDraft t:titles){
								if(t.getLanguage().getType().equals(languages[i])){
									if(languages[i].equals(ApplicationConstants.MARATHI)){
										model.addAttribute("marathiTitle",t.getText());
									}else{
										model.addAttribute("otherTitle",t.getText());
									}
								}
							}
						}
					}
					if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
						model.addAttribute("billHouseType",bill.getHouseType().getType());
					}else{
						model.addAttribute("billHouseType",bill.getIntroducingHouseType().getType());
					}
					model.addAttribute("billNumber", RomanNumeral.getRomanEquivalent(bill.getNumber()));
					
					model.addAttribute("billId", bill.getId());
					if(viewName==null || viewName.isEmpty()) {
						viewName = "proceeding/contentimports/bills_content";
					}
				}
			}
		}

		return viewName;
	}
	
	@RequestMapping(value="/gethalfhourdiscussionfromquestion",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getHalfHourDiscussionFromQuestion(final HttpServletRequest request, final Locale locale,final ModelMap model){
		
		String strStarredQuestionNo = request.getParameter("starredQuestionNo");
		String strSession = request.getParameter("session");
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		
		if(strSession != null && !strSession.isEmpty() &&
			strStarredQuestionNo != null && !strStarredQuestionNo.isEmpty()){
			Session session = Session.findById(Session.class, Long.parseLong(strSession));
			Integer number = Integer.parseInt(strStarredQuestionNo);
			DeviceType deviceType = DeviceType.findByType("questions_starred", locale.toString());
			Question question = Question.getQuestion(session.getId(), deviceType.getId(), number, locale.toString());
			if(question != null){
				List<Question> halfHourDiscussionsFromQuestions = Question.findAllByFieldName(Question.class, "halfHourDiscusionFromQuestionReference", question, "number", "desc", locale.toString());
				for(Question q : halfHourDiscussionsFromQuestions){
					MasterVO masterVo = new MasterVO();
					masterVo.setId(q.getId());
					masterVo.setName(q.getNumber().toString());
					masterVOs.add(masterVo);
				}
			}
		}
		return masterVOs;
		
	}
	
	@RequestMapping(value="/getphoto",method=RequestMethod.GET)
	public @ResponseBody void getPhotoOfMember(final HttpServletRequest request,final HttpServletResponse response, final Locale locale,final ModelMap model){
		String strMemberId = request.getParameter("memberId");
		if(strMemberId != null && !strMemberId.isEmpty()){
			Member member = Member.findById(Member.class, Long.parseLong(strMemberId));
			Document doc = null;
			try {
				doc = Document.findByTag(member.getPhoto());
			} catch (ELSException e1) {
				e1.printStackTrace();
			}
				
			//open as dialog box having options 'open with' & 'save file'
			//response.setHeader("Content-Disposition", "inline; filename=" + doc.getOriginalFileName());
			
			//response.setHeader("Cache-Control", "cache, must-revalidate");
			response.addHeader("Cache-Control", "cache, must-revalidate"); 
			response.setHeader("Pragma", "public");
			
			response.setContentType("image/jpeg");
			//or
			//open directly in browser
			//response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
			
			try {
				FileCopyUtils.copy(doc.getFileData(), response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="/member/getmembers",method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getActiveMembersAndMinisters(final HttpServletRequest request, final Locale locale, @RequestParam("session")final Long session, final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> memberVOs=new ArrayList<MasterVO>();
		List<MasterVO> ministerVOs=new ArrayList<MasterVO>();
		List<MasterVO> mainVO=new ArrayList<MasterVO>();
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
					House secondHouse=null;
					if(house.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
						HouseType houseType=HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale.toString());
						secondHouse=House.find(houseType, new Date(), locale.toString());
					}else if(house.getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
						HouseType houseType=HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
						secondHouse=House.find(houseType, new Date(), locale.toString());
					}
					ministerVOs=MemberMinister.findMinistersInSecondHouse(secondHouse,param,locale.toString());
					if(!ministerVOs.isEmpty()){
						mainVO.addAll(ministerVOs);
					}
					memberVOs=HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house, selectedSession, locale.toString(), param,primaryMemberId);
					mainVO.addAll(memberVOs);
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}else{
				String param=request.getParameter("term");
				
				House secondHouse=null;
				if(house.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
					HouseType houseType=HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale.toString());
					secondHouse=House.find(houseType, new Date(), locale.toString());
				}else if(house.getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
					HouseType houseType=HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
					secondHouse=House.find(houseType, new Date(), locale.toString());
				}
				ministerVOs=MemberMinister.findMinistersInSecondHouse(secondHouse,param,locale.toString());
				if(!ministerVOs.isEmpty()){
					mainVO.addAll(ministerVOs);
				}
				memberVOs=HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house,selectedSession, locale.toString(), param, primaryMemberId);
				mainVO.addAll(memberVOs);
			}
		}
		for(MasterVO i:mainVO){
			AutoCompleteVO autoCompleteVO=new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}

		return autoCompleteVOs;
	}
	
	@RequestMapping(value="/bill/member/getmembers",method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getActiveMembersAndMinistersForBill(final HttpServletRequest request, final Locale locale, @RequestParam("session")final Long session, final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> memberVOs=new ArrayList<MasterVO>();
		List<MasterVO> ministerVOs=new ArrayList<MasterVO>();
		List<MasterVO> mainVO=new ArrayList<MasterVO>();
		List<AutoCompleteVO> autoCompleteVOs=new ArrayList<AutoCompleteVO>();
		Session selectedSession=Session.findById(Session.class,session);
		DeviceType deviceType=DeviceType.findById(DeviceType.class,Long.parseLong(request.getParameter("deviceTypeId")));
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
		String param=request.getParameter("term");
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){				
				try {
					param=new String(param.getBytes("ISO-8859-1"),"UTF-8");										
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}
			House secondHouse=null;
			if(house.getType().getType().equals(ApplicationConstants.LOWER_HOUSE)){
				HouseType houseType=HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale.toString());
				secondHouse=House.find(houseType, new Date(), locale.toString());
			}else if(house.getType().getType().equals(ApplicationConstants.UPPER_HOUSE)){
				HouseType houseType=HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
				secondHouse=House.find(houseType, new Date(), locale.toString());
			}
			if(deviceType.getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
				//add first house ministers
				ministerVOs=MemberMinister.findMinistersInGivenHouse(house,param,locale.toString());
				if(!ministerVOs.isEmpty()){
					mainVO.addAll(ministerVOs);
				}
				//add second house ministers
				ministerVOs.clear();
				ministerVOs=MemberMinister.findMinistersInGivenHouse(secondHouse,param,locale.toString());
				if(!ministerVOs.isEmpty()){
					mainVO.addAll(ministerVOs);
				}
			} else {
				//add all given house members
				memberVOs=HouseMemberRoleAssociation.findAllActiveSupportingMemberVOSInSession(house, selectedSession, locale.toString(), param,primaryMemberId);
				mainVO.addAll(memberVOs);
				ministerVOs=MemberMinister.findMinistersInGivenHouse(selectedSession.getHouse(),param,locale.toString());
				if(!ministerVOs.isEmpty()){
					//remove if member is minister
					List<MasterVO> removableVOs = new ArrayList<MasterVO>();
					for(MasterVO mvo: mainVO) {
						for(MasterVO mivo: ministerVOs) {
							if(mvo.getId().equals(mivo.getId())) {
								removableVOs.add(mvo);								
								break;
							}
						}
					}
					mainVO.removeAll(removableVOs);
				}
			}
		}
		for(MasterVO i:mainVO){
			AutoCompleteVO autoCompleteVO=new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}

		return autoCompleteVOs;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/partmembers", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPartMembers(HttpServletRequest request, HttpServletResponse response, Locale locale){
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		
		List<MasterVO> members = new ArrayList<MasterVO>();
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
						
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("sessionId", new String[]{roster.getSession().getId().toString()});
			parametersMap.put("day", new String[]{roster.getDay().toString()});
			List result=Query.findReport("EDITING_PRIMARY_MEMBERS_OF_PART", parametersMap);
			
			for(Object ob : result){
				Object[] objArr = (Object[]) ob;
				MasterVO mVO = new MasterVO();
				mVO.setId(Long.valueOf(objArr[0].toString()));
				mVO.setName(objArr[1].toString());
				members.add(mVO);
				mVO = null;				
			}
		}
		return members;
	}
	
	@RequestMapping(value="/memberreporttype", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMemberReportType(HttpServletRequest request, HttpServletResponse response, Locale locale){
		CustomParameter csptMemberReportTypes = CustomParameter.findByName(CustomParameter.class, "EDITING_MEMBER_REPORT_TYPES", "");
		List<MasterVO> memberReportTypes = new ArrayList<MasterVO>();
		if(csptMemberReportTypes != null){
			if(csptMemberReportTypes.getValue() != null && !csptMemberReportTypes.getValue().isEmpty()){
				for(String value : csptMemberReportTypes.getValue().split(",")){
					String[] splitValue = value.split(":");
					MasterVO mvo = new MasterVO();
					mvo.setValue(splitValue[0]);
					mvo.setName(splitValue[1]);
					memberReportTypes.add(mvo);
				}				
			}
		}
		
		return memberReportTypes;
	}
	
	//TODO: Members page headings
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/memberreportpageheading", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMemberReportPageheading(HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		List<MasterVO> memberReportPageHeading = new ArrayList<MasterVO>();
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strLanguage=request.getParameter("language");
		String strMemberId = request.getParameter("member");
		String strDay=request.getParameter("day");
		
		
		if(strHouseType!=null&&!strHouseType.equals("")&&
				strSessionType!=null&&!strSessionType.equals("")&&
				strSessionYear!=null&&!strSessionYear.equals("")&&
				strLanguage!=null&&!strLanguage.equals("")&&
				strDay!=null&&!strDay.equals("")){

			HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
						
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("rosterId", new String[]{roster.getId().toString()});
			parametersMap.put("languageId", new String[]{language.getId().toString()});
			parametersMap.put("sessionId", new String[]{roster.getSession().getId().toString()});
			parametersMap.put("day", new String[]{roster.getDay().toString()});
			parametersMap.put("memberId", new String[]{strMemberId});
			List result=Query.findReport("RIS_PROCEEDING_CONTENT_MERGE_REPORT3", parametersMap);
			
			String memberPageHeading = "";
			
			for(Object ob : result){
				Object[] objArr = (Object[]) ob;
				if(objArr[1] != null){
					if(!memberPageHeading.equals(objArr[1].toString())){
						MasterVO mVO = new MasterVO();
						mVO.setId(Long.valueOf(objArr[20].toString()));
						mVO.setName(objArr[1].toString());
						memberReportPageHeading.add(mVO);
						mVO = null;
					}
				}
				
			}
		}
		return memberReportPageHeading;
	}
		
	//TODO: Page headings
		@SuppressWarnings("rawtypes")
		@RequestMapping(value="/reportpageheading", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getReportPageheading(HttpServletRequest request, HttpServletResponse response, Locale locale){
			
			List<MasterVO> reportPageHeading = new ArrayList<MasterVO>();
			String strHouseType=request.getParameter("houseType");
			String strSessionType=request.getParameter("sessionType");
			String strSessionYear=request.getParameter("sessionYear");
			String strLanguage=request.getParameter("language");
			String strDay=request.getParameter("day");
			
			
			if(strHouseType!=null&&!strHouseType.equals("")&&
					strSessionType!=null&&!strSessionType.equals("")&&
					strSessionYear!=null&&!strSessionYear.equals("")&&
					strLanguage!=null&&!strLanguage.equals("")&&
					strDay!=null&&!strDay.equals("")){

				HouseType houseType=HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
				SessionType sessionType=SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
				Session session = null;
				try {
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ELSException e) {
					e.printStackTrace();
				}
				Roster roster=Roster.findRosterBySessionLanguageAndDay(session,Integer.parseInt(strDay),language,locale.toString());
							
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[]{locale.toString()});
				parametersMap.put("rosterId", new String[]{roster.getId().toString()});
				parametersMap.put("languageId", new String[]{language.getId().toString()});
				parametersMap.put("sessionId", new String[]{roster.getSession().getId().toString()});
				parametersMap.put("day", new String[]{roster.getDay().toString()});
				List result=Query.findReport("RIS_PROCEEDING_CONTENT_MERGE_REPORT2", parametersMap);
				String pageHeading = "";
				
				for(Object ob : result){
					Object[] objArr = (Object[]) ob;
					if(objArr[1] != null){
						if(!pageHeading.equals(objArr[1].toString())){
							pageHeading = objArr[1].toString(); 
							MasterVO mVO = new MasterVO();
							mVO.setId(Long.valueOf(objArr[20].toString()));
							mVO.setName(objArr[1].toString());
							reportPageHeading.add(mVO);
							mVO = null;
						}
					}
				}
			}
			return reportPageHeading;
		}
		
		@RequestMapping(value="/getInterruptedProceedings",method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getInterruptedProceeding(final HttpServletRequest request, final Locale locale,final ModelMap model){
			//String strSlot=request.getParameter("currentSlot");
			String strDate=request.getParameter("selectedDate");
			String strSearchBy=request.getParameter("searchBy");
			String strLanguage=request.getParameter("language");
			String strSession = request.getParameter("session");
			List<MasterVO> masterVOs=new ArrayList<MasterVO>();
			if(strDate!=null &&	!strDate.isEmpty() 
					&& strSearchBy!=null &&!strSearchBy.isEmpty()
					&& strLanguage!=null && !strLanguage.isEmpty()
					&& strSession!=null && !strSession.isEmpty()){
				//Slot slot=Slot.findById(Slot.class, Long.parseLong(strSlot));
				Session session =Session.findById(Session.class, Long.parseLong(strSession));
				Language language=Language.findById(Language.class, Long.parseLong(strLanguage));
				Date sDate = FormaterUtil.formatStringToDate(strDate, ApplicationConstants.SERVER_DATEFORMAT);
				Roster roster=Roster.findRosterByDate(sDate, language, session, locale.toString());
				if(roster!=null){
								
					List<Part> parts=Part.findInterruptedProceedingInRoster(roster,locale);
					if(!parts.isEmpty()){
						for(Part p:parts){
							if(p.getMainHeading()!=null && !p.getMainHeading().isEmpty() &&
							   p.getPageHeading()!=null && !p.getPageHeading().isEmpty()){
								MasterVO masterVo=new MasterVO();
								if(strSearchBy.equals(ApplicationConstants.PAGE_HEADING)){
									masterVo.setName(p.getPageHeading());
								}else if(strSearchBy.equals(ApplicationConstants.MAIN_HEADING)){
									masterVo.setName( p.getMainHeading());
								}
								masterVo.setValue(p.getMainHeading()+"#"+p.getPageHeading());
								masterVOs.add(masterVo);
							}
						}
					}
				}
			}
			return masterVOs;
		}

		
		@RequestMapping(value="/bill/actors",method=RequestMethod.POST)
		public @ResponseBody List<Reference> findActorsForBill(final HttpServletRequest request,final ModelMap model,
				final Locale locale){
			List<Reference> actors=new ArrayList<Reference>();
			String strBill=request.getParameter("bill");
			String strInternalStatus=request.getParameter("status");		
			String strUserGroup=request.getParameter("usergroup");
			String strLevel=request.getParameter("level");
			if(strBill!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null){
				if((!strBill.isEmpty())&&(!strInternalStatus.isEmpty())&&
						(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())){
					Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
					if(internalStatus.getType().equals(ApplicationConstants.BILL_FINAL_REJECT_TRANSLATION)) {
						return actors;
					}
					Bill bill=Bill.findById(Bill.class,Long.parseLong(strBill));
					UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
					actors=WorkflowConfig.findBillActorsVO(bill,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
				}
			}
			return actors;
		}
		
		@RequestMapping(value="/getTypeOfSelectedBillType",method=RequestMethod.GET) 
		public @ResponseBody String findTypeOfSelectedBillType(final HttpServletRequest request) {
			String typeOfSelectedBillType = "";
			String selectedBillTypeId = request.getParameter("selectedBillTypeId");
			if(selectedBillTypeId!=null) {
				if(!selectedBillTypeId.isEmpty()) {
					BillType selectedBillType = BillType.findById(BillType.class, Long.parseLong(selectedBillTypeId));
					if(selectedBillType!=null) {
						typeOfSelectedBillType = selectedBillType.getType();
					}								
				}
			}
			return typeOfSelectedBillType;
		}
		
		@RequestMapping(value="/getTypeOfSelectedBillKind",method=RequestMethod.GET) 
		public @ResponseBody String findTypeOfSelectedBillKind(final HttpServletRequest request) {
			String kindOfSelectedBillKind = "";
			String selectedBillKindId = request.getParameter("selectedBillKindId");
			if(selectedBillKindId!=null) {
				if(!selectedBillKindId.isEmpty()) {
					BillKind selectedBillKind = BillKind.findById(BillKind.class, Long.parseLong(selectedBillKindId));
					if(selectedBillKind!=null) {
						kindOfSelectedBillKind = selectedBillKind.getType();
					}								
				}
			}
			return kindOfSelectedBillKind;
		}
		
		
		@RequestMapping(value="/ordinance/{year}/getOrdinancesForYear", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getOrdinancesForYear(@PathVariable(value="year") Integer year, final HttpServletRequest request, final Locale locale){
			
			List<MasterVO> ordinancesVO = new ArrayList<MasterVO>();
			
			if(year != null) {
				List<Ordinance> ordinances = Ordinance.findAllByFieldName(Ordinance.class, "year", year.toString(),"number", ApplicationConstants.ASC, locale.toString());
				
				for(Ordinance o : ordinances){
					MasterVO mv = new MasterVO();
					mv.setId(o.getId());
					mv.setNumber(o.getNumber());
					mv.setValue(FormaterUtil.formatNumberNoGrouping(o.getNumber(), locale.toString()));
					
					ordinancesVO.add(mv);
				}
			}
			
			return ordinancesVO;
		}
		
		@RequestMapping(value="/bill/membersforintroduction",method=RequestMethod.GET)
		public @ResponseBody List<AutoCompleteVO> getMembersForIntroductionOfBill(final HttpServletRequest request,final Locale locale,final ModelMap model){
			List<AutoCompleteVO> autoCompleteVOs=new ArrayList<AutoCompleteVO>();
			String strParam=request.getParameter("term");
			String strBillId=request.getParameter("billId");
			if(strParam!=null && strBillId!=null) {
				if(!strParam.isEmpty() && !strBillId.isEmpty()) {
					Bill bill = Bill.findById(Bill.class, Long.parseLong(strBillId));
					if(bill!=null) {
						StringBuffer memberIds = new StringBuffer();
						memberIds.append(bill.getPrimaryMember().getId());
						if(bill.getSupportingMembers()!=null) {
							if(!bill.getSupportingMembers().isEmpty()) {
								memberIds.append(",");
								for(SupportingMember i: bill.getSupportingMembers()) {
									memberIds.append(i.getMember().getId());
									memberIds.append(",");
								}
								memberIds.deleteCharAt(memberIds.length()-1);
							}
						}
						if(bill.getClubbedEntities()!=null) {
							if(!bill.getClubbedEntities().isEmpty()) {
								memberIds.append(",");
								for(ClubbedEntity i: bill.getClubbedEntities()) {
									if(i.getBill().getInternalStatus().getType().equals(ApplicationConstants.BILL_FINAL_ADMISSION)) {
										String clubbedBillMemberId = i.getBill().getPrimaryMember().getId().toString();
										if(!memberIds.toString().contains(clubbedBillMemberId)){
											memberIds.append(clubbedBillMemberId);
											memberIds.append(",");
										}
										if(i.getBill().getSupportingMembers()!=null) {
											if(!i.getBill().getSupportingMembers().isEmpty()) {
												for(SupportingMember j: i.getBill().getSupportingMembers()) {
													String clubbedBillSupportingMemberId = j.getMember().getId().toString();
													if(!memberIds.toString().contains(clubbedBillSupportingMemberId)){
														memberIds.append(clubbedBillSupportingMemberId);
														memberIds.append(",");
													}
												}
												if(memberIds.charAt(memberIds.length()-1)==',') {
													memberIds.deleteCharAt(memberIds.length()-1);
												}
											}
										}
									}							
								}
								if(memberIds.charAt(memberIds.length()-1)==',') {
									memberIds.deleteCharAt(memberIds.length()-1);
								}						
							}
						}				
						CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
						List<MasterVO> memberVOs=new ArrayList<MasterVO>();		
						if(customParameter!=null){
							String server=customParameter.getValue();
							if(server.equals("TOMCAT")){
								try {
									strParam=new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
									memberVOs = Member.findAllMembersVOsWithGivenIdsAndWithNameContainingParam(memberIds.toString(), strParam);
								}
								catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							} else {
								memberVOs = Member.findAllMembersVOsWithGivenIdsAndWithNameContainingParam(memberIds.toString(), strParam);
							}						
						}
						for(MasterVO i:memberVOs){
							AutoCompleteVO autoCompleteVO=new  AutoCompleteVO();
							autoCompleteVO.setId(i.getId());
							autoCompleteVO.setValue(i.getName());
							autoCompleteVOs.add(autoCompleteVO);
						}
					}				
				}
			}
			return autoCompleteVOs;
		}
		
		@RequestMapping(value="/bill/printrequisition_statuses", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getPrintRequisitionStatusesForBillInGivenHouse(final HttpServletRequest request, final Locale locale){
			List<MasterVO> printRequisitionStatusVOs = new ArrayList<MasterVO>();
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			String currentHouseTypeType = request.getParameter("currentHouseTypeType");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null&&currentHouseTypeType!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()&&!currentHouseTypeType.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								printRequisitionStatusVOs = null;
								return printRequisitionStatusVOs;
							}catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								printRequisitionStatusVOs = null;
								return printRequisitionStatusVOs;
							}
						}
					}
					try {
						Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill==null) {
							logger.error("Check Request Parameter 'billNumber' and 'billYear' for invalid values");
							printRequisitionStatusVOs = null;
							return printRequisitionStatusVOs;
						}
						String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(bill, currentHouseTypeType);
						if(currentHouseOrder!=null) {
							CustomParameter printRequisitionStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_PRINTREQUISITION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase(), "");
							if(printRequisitionStatusParameter!=null) {
								if(printRequisitionStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = printRequisitionStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(currentHouseOrder)) {
												if(i.trim().contains(currentHouseTypeType)) {
													filteredStatusTypes.append(i.trim()+",");
												}																						
											} else {
												filteredStatusTypes.append(i.trim()+",");						
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> printRequisitionStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									if(printRequisitionStatuses!=null) {
										for(Status i: printRequisitionStatuses) {
											MasterVO printRequisitionStatusVO = new MasterVO();
											printRequisitionStatusVO.setName(i.getName());
											printRequisitionStatusVO.setValue(i.getType());
											printRequisitionStatusVO.setId(bill.getId());
											printRequisitionStatusVOs.add(printRequisitionStatusVO);
										}
									}
								} else {
									logger.error("Custom Parameter 'BILL_PRINTREQUISITION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase() +"' is not set properly");				
									printRequisitionStatusVOs = null;
									return printRequisitionStatusVOs;
								}
							} else {
								logger.error("Custom Parameter 'BILL_PRINTREQUISITION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase() +"' is not set");			
								printRequisitionStatusVOs = null;
								return printRequisitionStatusVOs;
							} 
						} else {
							logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
							printRequisitionStatusVOs = null;
							return printRequisitionStatusVOs;
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
						printRequisitionStatusVOs = null;
						return printRequisitionStatusVOs;
					}				
				} else {
					logger.error("Check Request Parameter 'billNumber', 'billYear' for empty Values");
					printRequisitionStatusVOs = null;
					return printRequisitionStatusVOs;
				}			
			} else {
				logger.error("Check Request Parameter 'billNumber', 'billYear' for null Values");
				printRequisitionStatusVOs = null;
				return printRequisitionStatusVOs;
			}
			return printRequisitionStatusVOs;
		}
		
		@RequestMapping(value="/bill/sendGreenCopyForEndorsement_statuses", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getSendGreenCopyForEndorsementStatusesForBillInGivenHouse(final HttpServletRequest request, final Locale locale){
			List<MasterVO> sendGreenCopyForEndorsementStatusVOs = new ArrayList<MasterVO>();
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			String currentHouseTypeType = request.getParameter("currentHouseTypeType");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null&&currentHouseTypeType!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()&&!currentHouseTypeType.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");		
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								sendGreenCopyForEndorsementStatusVOs = null;
								return sendGreenCopyForEndorsementStatusVOs;
							}catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								sendGreenCopyForEndorsementStatusVOs = null;
								return sendGreenCopyForEndorsementStatusVOs;
							}
						}
					}
					try {
						Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill==null) {
							logger.error("Check Request Parameter 'billNumber' and 'billYear' for invalid values");
							sendGreenCopyForEndorsementStatusVOs = null;
							return sendGreenCopyForEndorsementStatusVOs;
						}
						String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(bill, currentHouseTypeType);
						if(currentHouseOrder!=null) {
							CustomParameter sendGreenCopyForEndorsementStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_SENDGREENCOPYFORENDORSEMENT_STATUSOPTIONS", "");
							if(sendGreenCopyForEndorsementStatusParameter!=null) {
								if(sendGreenCopyForEndorsementStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = sendGreenCopyForEndorsementStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseTypeType + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> sendGreenCopyForEndorsementStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									if(sendGreenCopyForEndorsementStatuses!=null) {
										for(Status i: sendGreenCopyForEndorsementStatuses) {
											MasterVO sendGreenCopyForEndorsementStatusVO = new MasterVO();
											sendGreenCopyForEndorsementStatusVO.setName(i.getName());
											sendGreenCopyForEndorsementStatusVO.setValue(i.getType());
											sendGreenCopyForEndorsementStatusVO.setId(bill.getId());
											sendGreenCopyForEndorsementStatusVOs.add(sendGreenCopyForEndorsementStatusVO);
										}
									}
								} else {
									logger.error("Custom Parameter 'BILL_SENDGREENCOPYFORENDORSEMENT_STATUSOPTIONS' is not set properly");
									sendGreenCopyForEndorsementStatusVOs = null;
									return sendGreenCopyForEndorsementStatusVOs;
								}
							} else {
								logger.error("Custom Parameter 'BILL_SENDGREENCOPYFORENDORSEMENT_STATUSOPTIONS' is not set");
								sendGreenCopyForEndorsementStatusVOs = null;
								return sendGreenCopyForEndorsementStatusVOs;
							} 
						} else {
							logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
							sendGreenCopyForEndorsementStatusVOs = null;
							return sendGreenCopyForEndorsementStatusVOs;
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
						sendGreenCopyForEndorsementStatusVOs = null;
						return sendGreenCopyForEndorsementStatusVOs;
					}				
				} else {
					logger.error("Check Request Parameter 'billNumber', 'billYear' for empty Values");
					sendGreenCopyForEndorsementStatusVOs = null;
					return sendGreenCopyForEndorsementStatusVOs;
				}			
			} else {
				logger.error("Check Request Parameter 'billNumber', 'billYear' for null Values");
				sendGreenCopyForEndorsementStatusVOs = null;
				return sendGreenCopyForEndorsementStatusVOs;
			}
			return sendGreenCopyForEndorsementStatusVOs;
		}
		
		@RequestMapping(value="/bill/transmitEndorsementCopies_statuses", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getTransmitEndorsementCopiesStatusesForBillInGivenHouse(final HttpServletRequest request, final Locale locale){
			List<MasterVO> transmitEndorsementCopiesStatusVOs = new ArrayList<MasterVO>();
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			String currentHouseTypeType = request.getParameter("currentHouseTypeType");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null&&currentHouseTypeType!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()&&!currentHouseTypeType.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");														
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								transmitEndorsementCopiesStatusVOs = null;
								return transmitEndorsementCopiesStatusVOs;
							}catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								transmitEndorsementCopiesStatusVOs = null;
								return transmitEndorsementCopiesStatusVOs;
							}
						}
					}
					try {
						Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill==null) {
							logger.error("Check Request Parameter 'billNumber' and 'billYear' for invalid values");
							transmitEndorsementCopiesStatusVOs = null;
							return transmitEndorsementCopiesStatusVOs;
						}
						String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(bill, currentHouseTypeType);
						if(currentHouseOrder!=null) {
							CustomParameter transmitEndorsementCopiesStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSMITENDORSEMENTCOPIES_STATUSOPTIONS", "");
							if(transmitEndorsementCopiesStatusParameter!=null) {
								if(transmitEndorsementCopiesStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = transmitEndorsementCopiesStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseTypeType + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> transmitEndorsementCopiesStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									if(transmitEndorsementCopiesStatuses!=null) {
										for(Status i: transmitEndorsementCopiesStatuses) {
											MasterVO transmitEndorsementCopiesStatusVO = new MasterVO();
											transmitEndorsementCopiesStatusVO.setName(i.getName());
											transmitEndorsementCopiesStatusVO.setValue(i.getType());
											transmitEndorsementCopiesStatusVO.setId(bill.getId());
											transmitEndorsementCopiesStatusVOs.add(transmitEndorsementCopiesStatusVO);
										}
									}
								} else {
									logger.error("Custom Parameter 'BILL_TRANSMITENDORSEMENTCOPIES_STATUSOPTIONS' is not set properly");
									transmitEndorsementCopiesStatusVOs = null;
									return transmitEndorsementCopiesStatusVOs;
								}
							} else {
								logger.error("Custom Parameter 'BILL_TRANSMITENDORSEMENTCOPIES_STATUSOPTIONS' is not set");
								transmitEndorsementCopiesStatusVOs = null;
								return transmitEndorsementCopiesStatusVOs;
							} 
						} else {
							logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
							transmitEndorsementCopiesStatusVOs = null;
							return transmitEndorsementCopiesStatusVOs;
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
						transmitEndorsementCopiesStatusVOs = null;
						return transmitEndorsementCopiesStatusVOs;
					}				
				} else {
					logger.error("Check Request Parameter 'billNumber', 'billYear' for empty Values");
					transmitEndorsementCopiesStatusVOs = null;
					return transmitEndorsementCopiesStatusVOs;
				}			
			} else {
				logger.error("Check Request Parameter 'billNumber', 'billYear' for null Values");
				transmitEndorsementCopiesStatusVOs = null;
				return transmitEndorsementCopiesStatusVOs;
			}
			return transmitEndorsementCopiesStatusVOs;
		}
		
		@RequestMapping(value="/bill/checkeligibilityforlayingletter", method=RequestMethod.GET)
		public @ResponseBody Long checkEligibilityOfSelectedBillForLayingLetter(final HttpServletRequest request, final Locale locale){
			Long validBillId = null;
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			String currentHouseTypeType = request.getParameter("currentHouseTypeType");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null&&currentHouseTypeType!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()&&!currentHouseTypeType.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								validBillId = null;
								return validBillId;
							}catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								validBillId = null;
								return validBillId;
							}
						}
					}
					try {
						Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill==null) {
							logger.error("Check Request Parameter 'billNumber' and 'billYear' for invalid values");
							validBillId = null;
							return validBillId;
						}	
						if(bill.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSED)
								&& bill.getRecommendationStatus().getType().endsWith(ApplicationConstants.BILL_FIRST_HOUSE)) {
							String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(bill, currentHouseTypeType);
							if(currentHouseOrder!=null) {
								if(currentHouseOrder.equals(ApplicationConstants.BILL_SECOND_HOUSE)) {
									validBillId = bill.getId();
								} else {
									logger.error("**** selected housetype is not second house of selected bill. so it is not eligible for laying letter. ****");
									validBillId = new Long(-2);
									return validBillId;
								}
							} else {
								logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
								validBillId = null;
								return validBillId;
							}
						} else {					
							logger.error("**** selected bill is not currently passed from first house. so it is not eligible for laying letter. ****");
							validBillId = new Long(-1);
							return validBillId;
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
						validBillId = null;
						return validBillId;
					}
				}
			}
			return validBillId;
		}
		
		@RequestMapping(value="/bill/transmitPressCopies_statuses", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getTransmitPressCopiesStatusesForBillInGivenHouse(final HttpServletRequest request, final Locale locale){
			List<MasterVO> transmitPressCopiesStatusVOs = new ArrayList<MasterVO>();
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			String currentHouseTypeType = request.getParameter("currentHouseTypeType");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null&&currentHouseTypeType!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()&&!currentHouseTypeType.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {								
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								transmitPressCopiesStatusVOs = null;
								return transmitPressCopiesStatusVOs;
							}catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								transmitPressCopiesStatusVOs = null;
								return transmitPressCopiesStatusVOs;
							}
						}
					}
					try {
						NumberFormat nf = NumberFormat.getInstance(locale);
				        DecimalFormat df = (DecimalFormat) nf;	
				        billNumberStr = df.parse(billNumberStr).toString();
				        billYearStr = df.parse(billYearStr).toString();   
				        Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill==null) {
							logger.error("Check Request Parameter 'billNumber' and 'billYear' for invalid values");
							transmitPressCopiesStatusVOs = null;
							return transmitPressCopiesStatusVOs;
						}
						String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(bill, currentHouseTypeType);
						if(currentHouseOrder!=null) {
							CustomParameter transmitPressCopiesStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSMITPRESSCOPIES_STATUSOPTIONS", "");
							if(transmitPressCopiesStatusParameter!=null) {
								if(transmitPressCopiesStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = transmitPressCopiesStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseTypeType + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> transmitPressCopiesStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									if(transmitPressCopiesStatuses!=null) {
										for(Status i: transmitPressCopiesStatuses) {
											MasterVO transmitPressCopiesStatusVO = new MasterVO();
											transmitPressCopiesStatusVO.setName(i.getName());
											transmitPressCopiesStatusVO.setValue(i.getType());
											transmitPressCopiesStatusVO.setId(bill.getId());
											transmitPressCopiesStatusVOs.add(transmitPressCopiesStatusVO);
										}
									}
								} else {
									logger.error("Custom Parameter 'BILL_TRANSMITPRESSCOPIES_STATUSOPTIONS' is not set properly");
									transmitPressCopiesStatusVOs = null;
									return transmitPressCopiesStatusVOs;
								}
							} else {
								logger.error("Custom Parameter 'BILL_TRANSMITPRESSCOPIES_STATUSOPTIONS' is not set");
								transmitPressCopiesStatusVOs = null;
								return transmitPressCopiesStatusVOs;
							} 
						} else {
							logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
							transmitPressCopiesStatusVOs = null;
							return transmitPressCopiesStatusVOs;
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
						transmitPressCopiesStatusVOs = null;
						return transmitPressCopiesStatusVOs;
					} catch(ParseException pe) {
						transmitPressCopiesStatusVOs = null;
						return transmitPressCopiesStatusVOs;
					}
				} else {
					logger.error("Check Request Parameter 'billNumber', 'billYear' for empty Values");
					transmitPressCopiesStatusVOs = null;
					return transmitPressCopiesStatusVOs;
				}			
			} else {
				logger.error("Check Request Parameter 'billNumber', 'billYear' for null Values");
				transmitPressCopiesStatusVOs = null;
				return transmitPressCopiesStatusVOs;
			}
			return transmitPressCopiesStatusVOs;
		}
		
		@RequestMapping(value="/bill/citation_statuses", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getCitationStatusesForBillInGivenHouse(final HttpServletRequest request, final Locale locale){
			List<MasterVO> citationStatusVOs = new ArrayList<MasterVO>();
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			String currentHouseTypeType = request.getParameter("currentHouseTypeType");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null&&currentHouseTypeType!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()&&!currentHouseTypeType.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");	
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								citationStatusVOs = null;
								return citationStatusVOs;
							} catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								citationStatusVOs = null;
								return citationStatusVOs;
							}
						}
					}
					try {
						Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill==null) {
							logger.error("Check Request Parameter 'billNumber' and 'billYear' for invalid values");
							citationStatusVOs = null;
							return citationStatusVOs;
						}
						String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(bill, currentHouseTypeType);
						if(currentHouseOrder!=null) {
							/**** Citation Statuses Allowed For Selected Bill In Selected House ****/							
							CustomParameter citationStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_CITATION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase(), "");
							if(citationStatusParameter!=null) {
								if(citationStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = citationStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseTypeType + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> citationStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									if(citationStatuses!=null) {
										for(Status i: citationStatuses) {
											MasterVO citationStatusVO = new MasterVO();
											citationStatusVO.setName(i.getName());
											citationStatusVO.setValue(i.getType());
											citationStatusVO.setId(bill.getId());
											citationStatusVOs.add(citationStatusVO);
										}
									}
								} else {
									logger.error("Custom Parameter 'BILL_CITATION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase()+"' is not set properly");
									citationStatusVOs = null;
									return citationStatusVOs;
								}
							} else {
								logger.error("Custom Parameter 'BILL_CITATION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase()+"' is not set");
								citationStatusVOs = null;
								return citationStatusVOs;
							}							 
						} else {
							logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
							citationStatusVOs = null;
							return citationStatusVOs;
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
						citationStatusVOs = null;
						return citationStatusVOs;
					}				
				} else {
					logger.error("Check Request Parameter 'billNumber', 'billYear' for empty Values");
					citationStatusVOs = null;
					return citationStatusVOs;
				}			
			} else {
				logger.error("Check Request Parameter 'billNumber', 'billYear' for null Values");
				citationStatusVOs = null;
				return citationStatusVOs;
			}
			return citationStatusVOs;
		}
		
		@RequestMapping(value="/findIdOfBillWithGivenNumberYearAndHouseType", method=RequestMethod.GET)
		public @ResponseBody String findIdOfBillWithGivenNumberYearAndHouseType(final HttpServletRequest request, final Locale locale){
			String billId = null;
			String billNumberStr = request.getParameter("billNumber");
			String billYearStr = request.getParameter("billYear");
			String houseTypeId = request.getParameter("houseTypeId");
			if(billNumberStr!=null&&billYearStr!=null&&houseTypeId!=null) {
				if(!billNumberStr.isEmpty()&&!billYearStr.isEmpty()&&!houseTypeId.isEmpty()) {
					CustomParameter server=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
					if(server != null) {
						if(server.getValue().equals("TOMCAT")) {
							try {
								billNumberStr = new String(billNumberStr.getBytes("ISO-8859-1"),"UTF-8");
								billYearStr = new String(billYearStr.getBytes("ISO-8859-1"),"UTF-8");
								NumberFormat nf = NumberFormat.getInstance(locale);
						        DecimalFormat df = (DecimalFormat) nf;	
						        billNumberStr = df.parse(billNumberStr).toString();
						        billYearStr = df.parse(billYearStr).toString();
							}catch (UnsupportedEncodingException e) {
								logger.error("Cannot encode one of the request parameters.");
								return billId;
							}catch (ParseException e) {
								logger.error("Cannot parse one of the request parameters.");
								return billId;
							}
						}
					}
					try {
						Bill bill = Bill.findByNumberYearAndHouseType(Integer.parseInt(billNumberStr), Integer.parseInt(billYearStr), Long.parseLong(houseTypeId), locale.toString());
						if(bill!=null) {
							billId = bill.getId().toString();
						} else {
							logger.error("Check Request Parameter 'billNumber', 'billYear' and 'currentHouseTypeType' for invalid values");
						}
					}catch(NumberFormatException ne) {
						logger.error("Check Request Parameter 'billNumber', 'billYear' for Non-Numeric Values");
					}				
				} else {
					logger.error("Check Request Parameter 'billNumber', 'billYear' for empty Values");
				}			
			} else {
				logger.error("Check Request Parameter 'billNumber', 'billYear' for null Values");
			}
			return billId;
		}
		
		@RequestMapping(value="/geteditingactors",method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getEditingActors(HttpServletRequest request, Locale locale){
			return EditingWorkflowController.getEditingActors(request, locale);
		}
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value="/devicesofrosterproceeding", method=RequestMethod.GET)
		public @ResponseBody List getRosterDevicesForProceeding(final HttpServletRequest request, final Locale locale){
			List retVal = null;
			boolean flag = false;
			
			try{
				String strHouseType = request.getParameter("houseType");
				String strSessionYear = request.getParameter("sessionYear");
				String strSessionType = request.getParameter("sessionType");
				String strLanguage = request.getParameter("language");
				String strDay = request.getParameter("day");
				
				if((strHouseType != null && !strHouseType.isEmpty())
						&& (strSessionType != null && !strSessionType.isEmpty())
						&& (strSessionYear != null && !strSessionYear.isEmpty())
						&& (strLanguage != null && !strLanguage.isEmpty())
						&& (strDay != null && !strDay.isEmpty())) {
					
					HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
					SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
					Language language = Language.findById(Language.class, Long.parseLong(strLanguage));
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
					
					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("locale", new String[]{locale.toString()});
					parameters.put("day", new String[]{strDay});
					parameters.put("languageId", new String[]{language.getId().toString()});
					parameters.put("sessionId", new String[]{session.getId().toString()});
					retVal = Query.findReport("EDIS_ROSTER_DEVICES_OF_PROCEEDING", parameters);
					flag = true;
				}else{
					flag = false;
				}
				
			}catch (Exception e) {
				flag = false;
				logger.debug("ref/devicesofrosterproceeding", e);
				e.printStackTrace();
			}
			
			if(!flag){
				retVal = new ArrayList();
				Object[] data = new Object[2];
				MessageResource msr = MessageResource.findByFieldName(MessageResource.class, "code", "generic.error", locale.toString());
				
				data[0] = "error:"+((msr != null)? msr.getValue():"Can not complete the last operation.");
			}
			return retVal; 
		}
		
		/**
		 * @param id
		 * @param request
		 * @param locale
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		@RequestMapping(value="/getpartDraftsInWorkflow/{id}",method=RequestMethod.GET)
		public @ResponseBody List getDraftsOfPartInWorkflow(@PathVariable(value="id") Long id, HttpServletRequest request, Locale locale){
			try{
				String strWfId = request.getParameter("wfdetailsId");
				if(strWfId != null && !strWfId.isEmpty()){
					WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, Long.valueOf(strWfId));
					
					String strUserGroup = request.getParameter("userGroup");
					String strUserGroupType = request.getParameter("userGroupType");
					
					if(strUserGroup != null && !strUserGroup.isEmpty()
							&& strUserGroupType != null && !strUserGroupType.isEmpty()){
						
						Status status = Status.findByType(wfDetails.getWorkflowSubType(), locale.toString());
						
						Map<String, String[]> parameters = new HashMap<String, String[]>();
						
						if(strUserGroupType.equals(ApplicationConstants.EDITOR)){
							if(status.getType().equals(ApplicationConstants.EDITING_FINAL_MEMBERAPPROVAL)){
								parameters.put("locale", new String[]{locale.toString()});
								parameters.put("rosterId", new String[]{wfDetails.getDeviceId()});
								UserGroup assignerUserGroup = UserGroup.findById(UserGroup.class, Long.valueOf(wfDetails.getAssignerUserGroupId()));
								
								User user = EditingWorkflowController.getUser(assignerUserGroup, locale.toString());
								Member member = Member.findMember(user.getFirstName(),user.getMiddleName(), user.getLastName(), user.getBirthDate(), locale.toString());
								parameters.put("primaryMemberId", new String[]{member.getId().toString()});
								parameters.put("editedby", new String[]{wfDetails.getAssigner()});
								parameters.put("partId", new String[]{id.toString()});
								return Query.findReport("EDIS_WORKFLOW_MEMBER_SENT_DRAFTS_DESC_OF_PART", parameters);
							}else if(status.getType().equals(ApplicationConstants.EDITING_FINAL_SPEAKERAPPROVAL)){
								parameters.put("locale", new String[]{locale.toString()});
								parameters.put("rosterId", new String[]{wfDetails.getDeviceId()});
								parameters.put("editedby", new String[]{wfDetails.getAssigner()});
								parameters.put("partId", new String[]{id.toString()});
								return Query.findReport("EDIS_WORKFLOW_SPEAKER_SENT_DRAFTS_DESC_OF_PART", parameters);
							}
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@RequestMapping(value="committeetour/actors/workflow/{workflowName}", method=RequestMethod.GET)
		public @ResponseBody List<Reference> getCommitteeTourActors(
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
				
				List<WorkflowActor> wfActors = 
					WorkflowConfig.findCommitteeTourActors(
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
		
		@RequestMapping(value="/isBallotingAllowedForSession",method=RequestMethod.GET)
		public @ResponseBody String isBallotingAllowedForSession(HttpServletRequest request, Locale locale) throws ELSException {
			
			String result = "error";
			
			String houseTypeStr = request.getParameter("houseType");
			String sessionTypeStr = request.getParameter("sessionType");
			String sessionYear = request.getParameter("sessionYear");	
			String deviceTypeStr = request.getParameter("deviceType");
			
			if(houseTypeStr!=null&&sessionTypeStr!=null&&sessionYear!=null&&deviceTypeStr!=null) {
				if(!houseTypeStr.isEmpty()&&!sessionTypeStr.isEmpty()&&!sessionYear.isEmpty()&&!deviceTypeStr.isEmpty()) {
					CustomParameter deploymentServerParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "DEPLOYMENT_SERVER", "");
					if(deploymentServerParameter!=null) {
						if(deploymentServerParameter.getValue()!=null) {
							if(deploymentServerParameter.getValue().equals("TOMCAT")) {
								try {
									houseTypeStr = new String(houseTypeStr.getBytes("ISO-8859-1"),"UTF-8");
									sessionTypeStr = new String(sessionTypeStr.getBytes("ISO-8859-1"),"UTF-8");
									sessionYear = new String(sessionYear.getBytes("ISO-8859-1"),"UTF-8");
									deviceTypeStr = new String(deviceTypeStr.getBytes("ISO-8859-1"),"UTF-8");
									HouseType houseType = HouseType.findByType(houseTypeStr, locale.toString());
									if(houseType==null) {
										houseType = HouseType.findById(HouseType.class, Long.parseLong(houseTypeStr));
									}
									SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(sessionTypeStr));
									if(sessionType==null) {
										sessionType = SessionType.findByFieldName(SessionType.class, "type", sessionTypeStr, locale.toString());
									}
									Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(sessionYear));
									if(session!=null) {
										DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceTypeStr));
										if(deviceType==null) {
											deviceType = DeviceType.findByFieldName(SessionType.class, "type", deviceTypeStr, locale.toString());
										}
										if(deviceType!=null) {
											if(deviceType.getType().equals(ApplicationConstants.NONOFFICIAL_BILL) && session.getParameter(deviceType.getType()+"_isBallotingRequired")!=null) {
												result = session.getParameter(deviceType.getType()+"_isBallotingRequired");
											} else {
												result = "";
											}
										}																													
									}
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();									
								}
							}
						}
					}
				} 
			} 	
			
			return result;
		}

		
		@RequestMapping(value="/referOrdinance/searchByNumber",method=RequestMethod.GET)
	    public @ResponseBody OrdinanceSearchVO searchOrdinanceForReferring(final HttpServletRequest request,final ModelMap model,final Locale locale){
			OrdinanceSearchVO ordSearchVO = new OrdinanceSearchVO();
			String ordYearStr=request.getParameter("ordYear");
			String ordNumberStr=request.getParameter("ordNumber");
			if(ordYearStr!=null&&ordNumberStr!=null){
	        	if((!ordYearStr.isEmpty())&&(!ordNumberStr.isEmpty())){
	        		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");      
	        		if(deploymentServer!=null) {
	        			String server=deploymentServer.getValue();
        				if(server!=null) {
        					if(server.equals("TOMCAT")){
	        					try {
	        						ordYearStr=new String(ordYearStr.getBytes("ISO-8859-1"),"UTF-8");
	        						ordNumberStr=new String(ordNumberStr.getBytes("ISO-8859-1"),"UTF-8");
	        					}
	        					catch (UnsupportedEncodingException e) {
	        						logger.error("**** encoding of request parameters failed ****");
	        						ordSearchVO.setId(new Long(-1));
	            					return ordSearchVO;
	        					}
	        				}
        				} else {
        					logger.error("**** Custom Parameter 'DEPLOYMENT_SERVER' value is not set ****");
        					ordSearchVO.setId(new Long(-1));
        					return ordSearchVO;
        				}
        				try { 
        					Integer ordYear = Integer.parseInt(ordYearStr);
        					Integer ordNumber = Integer.parseInt(ordNumberStr);
        					Ordinance ordinance = Ordinance.findByYearAndNumber(ordYear, ordNumber);
        					if(ordinance!=null) {
        						ordSearchVO.setId(ordinance.getId());
        						ordSearchVO.setNumber(ordinance.getNumber().toString());
        						ordSearchVO.setYear(ordinance.getYear().toString());
        						ordSearchVO.setTitle(ordinance.getDefaultTitle());        						
        					} else {
        						ordSearchVO.setId(new Long(0));
            					return ordSearchVO;
        					}
        				} catch(NumberFormatException nfe) {
        					logger.error("**** Some of numeric request parameters are invalid or not encoded due to incorrect 'DEPLOYMENT_SERVER' custom parameter value ****");
        					ordSearchVO.setId(new Long(-1));
        					return ordSearchVO;
        				}        				
	        		} else {
	        			logger.error("**** Custom Parameter 'DEPLOYMENT_SERVER' is not set ****");
	        			ordSearchVO.setId(new Long(-1));
    					return ordSearchVO;
	        		}	        		
	        	}
	        }
			return ordSearchVO;
		}

	
		@RequestMapping(value="/getchairperson",method=RequestMethod.GET)
		public @ResponseBody List<String> getChairPerson(final HttpServletRequest request, final Locale locale,final ModelMap model){
			List<String> chairPersons=new ArrayList<String>();
			String strMemberRole=request.getParameter("chairPersonRole");
			String strProceeding=request.getParameter("proceeding");
			Proceeding proceeding=null;
			MemberRole mr=null;
			if(strProceeding!=null && !strProceeding.isEmpty()){
				proceeding=Proceeding.findById(Proceeding.class, Long.parseLong(strProceeding));
			}
			if(strMemberRole!=null && !strMemberRole.isEmpty()){
				 mr=MemberRole.findById(MemberRole.class, Long.parseLong(strMemberRole));
			}
			
			Member member=null;
			if(mr!=null && proceeding!=null){
				Slot slot=proceeding.getSlot();
				Roster roster=slot.getRoster();
				Session session=roster.getSession();
				House house=session.getHouse();
				List<HouseMemberRoleAssociation> hmras;
				try {
					hmras = HouseMemberRoleAssociation.findActiveHouseMemberRoles(house, mr, new Date(), locale.toString());
					for(HouseMemberRoleAssociation h:hmras){
						if(h.getRole().equals(mr)){
							member=h.getMember();
							chairPersons.add(member.getFullname());
						}
					}
				}catch (ELSException e) {
					e.printStackTrace();
				}
								
			}
			return chairPersons;
		}
		
		@RequestMapping(value="/bill/checkSectionDetails", method=RequestMethod.GET)
		public @ResponseBody SectionVO checkBillSectionDetails(final HttpServletRequest request, final Locale locale) {
			String billId = request.getParameter("billId");
			String language = request.getParameter("language");
			String sectionNumber = request.getParameter("sectionNumber");
			String sectionOrder = request.getParameter("sectionOrder");
			SectionVO sectionVO = new SectionVO();
			if(billId!=null && !billId.isEmpty() && language!=null && !language.isEmpty() 
					&& sectionNumber!=null && !sectionNumber.isEmpty()) {
				CustomParameter deploymentServerParameter = CustomParameter.findByFieldName(CustomParameter.class, "name", "DEPLOYMENT_SERVER", "");
				if(deploymentServerParameter!=null) {
					if(deploymentServerParameter.getValue()!=null) {
						if(deploymentServerParameter.getValue().equals("TOMCAT")) {
							try {
								sectionNumber = new String(sectionNumber.getBytes("ISO-8859-1"),"UTF-8");
								sectionOrder = new String(sectionOrder.getBytes("ISO-8859-1"),"UTF-8");
								Section section = Bill.findSection(Long.parseLong(billId), language, sectionNumber);
								if(section!=null && request.getParameter("isCurrent")==null) {
									sectionVO.setInfo("section_exists_already");									
								} else {
									if(sectionOrder!=null && !sectionOrder.isEmpty()) {
										sectionOrder = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(sectionOrder).toString();
										String[] sectionNumberArr = sectionNumber.split("\\.");
										if(sectionNumberArr.length==1) {
											Section sectionWithSameOrder = Bill.findSectionByHierarchyOrder(Long.parseLong(billId), language, sectionOrder);
											if(sectionWithSameOrder!=null) {
												sectionVO.setInfo("section_with_same_order");												
											}
										} else {											
											String parentSectionNumber = "";
											for(int i=0; i<=sectionNumberArr.length-2;i++) {											
												parentSectionNumber += sectionNumberArr[i];
												if(i!=sectionNumberArr.length-2) {
													parentSectionNumber += ".";
												}
											}
											Section parentSection = Bill.findSection(Long.parseLong(billId), language, parentSectionNumber);
											if(parentSection!=null) {
												Section sectionWithSameOrder = Bill.findSectionByHierarchyOrder(Long.parseLong(billId), language, parentSection.getHierarchyOrder()+"."+sectionOrder);
												if(sectionWithSameOrder!=null) {
													sectionVO.setInfo("section_with_same_order");
												}
											} else {
												sectionVO.setInfo("error");												
											}
										}
									} else {
										//find whether this is only section at its hierarchy level.. send this to field 'isFirstForHierarchy'
										List<Section> sections = Bill.findAllSiblingSectionsForGivenSection(Long.parseLong(billId), language, sectionNumber);
										if(sections==null || sections.isEmpty()) {
											sectionVO.setIsFirstForHierarchyLevel("yes");								
										} else { //populate series for its hierarchy level & check whether level has custom order
											SectionOrderSeries hierarchyOrderSeries = sections.get(0).getOrderingSeries();
											if(hierarchyOrderSeries!=null) {
												sectionVO.setOrderingSeries(hierarchyOrderSeries.getId().toString());
											}										
											Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
											boolean isHierarchyCustomOrdered = Section.isHierarchyLevelWithCustomOrder(bill, language, sectionNumber);
											if(isHierarchyCustomOrdered) {
												sectionVO.setIsHierarchyLevelWithCustomOrder("yes");
											}
										}
									}									
								}																
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
								sectionVO.setInfo("invalidSectionNumber");								
							} catch (ELSException e) {
								e.printStackTrace();
								if(e.getParameter("bill_section_notfound")!=null) {
									sectionVO.setInfo("error");									
								} else {
									sectionVO.setInfo("error");	
								}								
							} catch (Exception e) {
								e.printStackTrace();
								sectionVO.setInfo("error");	
							}
						}
					}
				}
			} else {
				sectionVO.setInfo("error");	
			}	
			if(sectionVO.getIsFirstForHierarchyLevel()==null) {
				sectionVO.setIsFirstForHierarchyLevel("no");
			}
			if(sectionVO.getIsHierarchyLevelWithCustomOrder()==null) {
				sectionVO.setIsHierarchyLevelWithCustomOrder("no");
			}
			if(sectionVO.getOrderingSeries()==null) {
				sectionVO.setOrderingSeries("");
			}
			return sectionVO;
		}
		
		@RequestMapping(value="/getDepartment", method=RequestMethod.GET)
		public @ResponseBody List<MasterVO> getDepartmentFromGroup(final HttpServletRequest request, final Locale locale){
			List<MasterVO> masterVos=new ArrayList<MasterVO>();
			
			try{
				String strGroup = request.getParameter("group");
				String strDeviceType = request.getParameter("deviceType");
				String strHouseType = request.getParameter("houseType");
				String strUserGroupType = request.getParameter("usergroupType");
				if(strDeviceType != null && !strDeviceType.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()){
					Group group = null;
					List<Ministry> ministries = null;
					DeviceType device = null;
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					try{
						device = DeviceType.findByType(strDeviceType, locale.toString());
					}catch(Exception e){
						device = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
					}
					
					if(device == null){
						device = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
					}
					
					if(strGroup != null && !strGroup.isEmpty()){
						group = Group.findById(Group.class, new Long(strGroup));
					}
					
					
					if(group == null){
						
						Session session = null; 
						String strSessionId = request.getParameter("session");
						
						if(strSessionId != null && !strSessionId.isEmpty()){
							session = Session.findById(Session.class, new Long(strSessionId));
						}
					
						if(session != null){
							
							User user = User.findByUserName(this.getCurrentUser().getUsername(), locale.toString());
							UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType, locale.toString());
							UserGroup userGroup = UserGroup.findActive(user.getCredential(), userGroupType,session.getEndDate(), locale.toString());
			
							Map<String,String> parameters = UserGroup.findParametersByUserGroup(userGroup);
							
							if(parameters.get(ApplicationConstants.MINISTRY_KEY+"_"+locale.toString()) != null 
									&& !parameters.get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale.toString()).equals(" ")){
								
								String strministries=parameters.get(ApplicationConstants.MINISTRY_KEY+"_"+locale.toString());
								
								ministries = Ministry.findAssignedMinistriesInSession(session.getEndDate(), locale.toString());
									
								for(Ministry m : ministries){
									if(strministries.contains(m.getName())){
										Date onDate = session.getEndDate();
										if(onDate.before(new Date())) {
											CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
											if(csptNewHouseFormationInProcess==null) {
												onDate = new Date();
											} else if(csptNewHouseFormationInProcess.getValue()==null) {
												onDate = new Date();
											} else if(!csptNewHouseFormationInProcess.getValue().equals("YES")) {
												onDate = new Date();
											}
										}
										List<SubDepartment> subDepartments = 
												MemberMinister.findAssignedSubDepartments(m, onDate, locale.toString());
										for(SubDepartment s : subDepartments){
											MasterVO masterVo = new MasterVO();
											masterVo.setId(s.getId());
											masterVo.setName(s.getName());
											masterVos.add(masterVo);
										}
									}
								}
							}
						}
					}else if(group != null){
						Session session = group.getSession();
						Session currentSession = Session.findLatestSession(houseType);
						Boolean isCurrentSession = false;
						if(currentSession != null && session != null){
							if(currentSession.equals(session)){
								isCurrentSession = true;
							}
						}
						ministries = group.getMinistries();
						if(isCurrentSession){
							User user = User.findByUserName(this.getCurrentUser().getUsername(), locale.toString());
							UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType, locale.toString());
							UserGroup userGroup = UserGroup.
									findActive(user.getCredential(), userGroupType, group.getSession().getEndDate(), locale.toString());
							Map<String,String> parameters=UserGroup.findParametersByUserGroup(userGroup);
							if(parameters.
									get(ApplicationConstants.MINISTRY_KEY+"_"+locale.toString())!=null 
									&& !parameters.
									get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale.toString()).equals(" ")){
								String strministries = parameters.
										get(ApplicationConstants.MINISTRY_KEY+"_"+locale.toString());
								for(Ministry m:ministries){
									if(strministries.contains(m.getName())){
										Date onDate = group.getSession().getStartDate();
										if(onDate.before(new Date())) {
											CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
											if(csptNewHouseFormationInProcess==null) {
												onDate = new Date();
											} else if(csptNewHouseFormationInProcess.getValue()==null) {
												onDate = new Date();
											} else if(!csptNewHouseFormationInProcess.getValue().equals("YES")) {
												onDate = new Date();
											}
										}
										List<SubDepartment> subDepartments = MemberMinister.
												findAssignedSubDepartments(m, onDate, locale.toString());
										String subDepartmentParameters = parameters.
												get(ApplicationConstants.SUBDEPARTMENT_KEY+"_"+locale.toString());
										List<SubDepartment> subDepartmentList = 
												delimitedStringToSubDepartmentList(subDepartmentParameters,"##",locale.toString());
										for(SubDepartment s:subDepartments){
											if(isSubDepartmentExists(subDepartmentList,s)){
												MasterVO masterVo=new MasterVO();
												masterVo.setId(s.getId());
												masterVo.setName(s.getName());
												masterVos.add(masterVo);
											}
										}
									}
								}
								
							}
						}else{
							for(Ministry m : ministries){
								Date onDate = session.getEndDate();
								if(onDate.before(new Date())) {
									CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
									if(csptNewHouseFormationInProcess==null) {
										onDate = new Date();
									} else if(csptNewHouseFormationInProcess.getValue()==null) {
										onDate = new Date();
									} else if(!csptNewHouseFormationInProcess.getValue().equals("YES")) {
										onDate = new Date();
									}
								}
								List<SubDepartment> subDepartments = 
										MemberMinister.findAssignedSubDepartments(m, onDate, locale.toString());
								for(SubDepartment s : subDepartments){
									MasterVO masterVo = new MasterVO();
									masterVo.setId(s.getId());
									masterVo.setName(s.getName());
									masterVos.add(masterVo);
								}
							}
						}
					}				
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return masterVos;
	}		
		
	public  List<SubDepartment> delimitedStringToSubDepartmentList(final String delimitedSubDepartments,
			final String delimiter,
			final String locale) {
		List<SubDepartment> subDepartments = new ArrayList<SubDepartment>();
		
		String[] strSubDepartments = delimitedSubDepartments.split(delimiter);
		for(String strSubDepartment : strSubDepartments) {
			SubDepartment subDepartment = SubDepartment.
					findByName(SubDepartment.class, strSubDepartment, locale);
			subDepartments.add(subDepartment);
		}
		
		return subDepartments;
	}
	
	private boolean isSubDepartmentExists(final List<SubDepartment> subDepartments,
			final SubDepartment subDepartment) {
		for(SubDepartment sd : subDepartments) {
			if(sd != null && subDepartment != null){
				if(subDepartment.getId().equals(sd.getId())) {
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	@RequestMapping(value="/getministries",method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getMinistries(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> ministerVOs=new ArrayList<MasterVO>();
		List<AutoCompleteVO> autoCompleteVOs=new ArrayList<AutoCompleteVO>();
		String strSession=request.getParameter("session");
		Session session=null;
		if(strSession!=null && !strSession.isEmpty()){
			session=Session.findById(Session.class, Long.parseLong(strSession));
			
		}
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				String strParam=request.getParameter("term");
				try {
					String param=new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
					ministerVOs = Ministry.findMinistriesAssignedToGroupsByTerm(session.getHouse().getType(), session.getYear(), session.getType(),param, locale.toString());
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}else{
				String param=request.getParameter("term");
				ministerVOs=Ministry.findMinistriesAssignedToGroupsByTerm(session.getHouse().getType(), session.getYear(), session.getType(),param, locale.toString());
			}
		}
		for(MasterVO i:ministerVOs){
			AutoCompleteVO autoCompleteVO=new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}

		return autoCompleteVOs;
	}
	
	@RequestMapping(value="/getministries_withoutgroup",method=RequestMethod.GET)
	public @ResponseBody List<AutoCompleteVO> getMinistriesWithoutGroup(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> ministerVOs=new ArrayList<MasterVO>();
		List<AutoCompleteVO> autoCompleteVOs=new ArrayList<AutoCompleteVO>();
		String strSession=request.getParameter("session");
		Session session=null;
		if(strSession!=null && !strSession.isEmpty()){
			session=Session.findById(Session.class, Long.parseLong(strSession));
			
		}
		if(customParameter!=null){
			String server=customParameter.getValue();
			if(server.equals("TOMCAT")){
				String strParam=request.getParameter("term");
				try {
					String param=new String(strParam.getBytes("ISO-8859-1"),"UTF-8");					
					ministerVOs = Ministry.findAssignedMinistriesInSessionByTerm(session.getStartDate(), param, locale.toString());
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}else{
				String param=request.getParameter("term");
				ministerVOs = Ministry.findAssignedMinistriesInSessionByTerm(session.getStartDate(), param, locale.toString());
			}
		}
		for(MasterVO i:ministerVOs){
			AutoCompleteVO autoCompleteVO=new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}

		return autoCompleteVOs;
	}
	
	/**** To get the clubbed questions text ****/
	@RequestMapping(value="/{id}/clubbedquestiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClubbedQuestionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> clubbedQuestionsVO = new ArrayList<MasterVO>();
		
		try{
			
			Question parent = Question.findById(Question.class, id);
			String permission = "yes";
			CustomParameter partiallyClubbedParameter = CustomParameter.findByName(CustomParameter.class, "PERMISSION_TO_DISPLAY_PARTIAL_CLUBBED_QUESTIONS", "");
			if(partiallyClubbedParameter != null){
				permission = partiallyClubbedParameter.getValue();
			}
			if(parent != null){
				MasterVO mVO = new MasterVO();
				mVO.setId(parent.getId());
				mVO.setName(FormaterUtil.formatNumberNoGrouping(parent.getNumber(), locale.toString()));
				mVO.setDisplayName(parent.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
				if(parent.getQuestionText()!= null && !parent.getQuestionText().isEmpty()){
					mVO.setValue(parent.getQuestionText());
				}else{
					mVO.setValue(parent.getRevisedQuestionText());
				}
				clubbedQuestionsVO.add(mVO);
				List<ClubbedEntity> clubbedQuestions = parent.getClubbedEntities();
				Boolean isAllowedForDisplay = false;
				for(ClubbedEntity ce : clubbedQuestions){
					Question cQuestion = ce.getQuestion();
					if(cQuestion != null){
						if(permission.equals("no")){
							if(cQuestion.getInternalStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_CLUBBED)
								|| cQuestion.getInternalStatus().getType().contains("FINAL")
								|| cQuestion.getInternalStatus().getType().contains(ApplicationConstants.QUESTION_UNSTARRED_SYSTEM_CLUBBED)){
								isAllowedForDisplay = true;
							}
						}else{
							isAllowedForDisplay = true;
						}
						if(isAllowedForDisplay){
							mVO = new MasterVO();
							mVO.setId(cQuestion.getId());
							mVO.setName(FormaterUtil.formatNumberNoGrouping(cQuestion.getNumber(), locale.toString()));
							mVO.setDisplayName(cQuestion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
							if(cQuestion.getQuestionText()!= null && !cQuestion.getQuestionText().isEmpty()){
								mVO.setValue(cQuestion.getQuestionText());
							}else{
								mVO.setValue(cQuestion.getRevisedQuestionText());
							}
							clubbedQuestionsVO.add(mVO);
						}
							
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		return clubbedQuestionsVO;
	}
	
	/**** To get the clubbed questions text ****/
	@RequestMapping(value="/{id}/referencedquestiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getReferencedQuestionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> referencedQuestionsVO = new ArrayList<MasterVO>();
		
		try{
			
			Question question = Question.findById(Question.class, id);
			
			if(question != null){
				List<ReferenceUnit> referencedQuestions = question.getReferencedEntities();
				
				for(ReferenceUnit ru : referencedQuestions){
					Question rQuestion = Question.findById(Question.class, ru.getDevice());
					if(rQuestion != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(rQuestion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(rQuestion.getNumber(), locale.toString()));
						if(rQuestion.getQuestionText()!= null && !rQuestion.getQuestionText().isEmpty()){
							mVO.setValue(rQuestion.getQuestionText());
						}else{
							mVO.setValue(rQuestion.getRevisedQuestionText());
						}
						
						referencedQuestionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return referencedQuestionsVO;
	}
	
	/**** To get the clubbed motions text ****/
	@RequestMapping(value="/{id}/clubbedmotiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClubbedMotionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> clubbedMotionsVO = new ArrayList<MasterVO>();
		
		try{
			
			Motion parent = Motion.findById(Motion.class, id);
			
			if(parent != null){
				List<ClubbedEntity> clubbedMotions = parent.getClubbedEntities();
				
				for(ClubbedEntity ce : clubbedMotions){
					Motion cMotion = ce.getMotion();
					if(cMotion != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(cMotion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(cMotion.getNumber(), locale.toString()));
						mVO.setDisplayName(cMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						if(cMotion.getDetails()!= null && !cMotion.getDetails().isEmpty()){
							mVO.setValue(cMotion.getDetails());
						}else{
							mVO.setValue(cMotion.getRevisedDetails());
						}
						
						clubbedMotionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return clubbedMotionsVO;
	}
	
	/**** To get the clubbed motions text ****/
	@RequestMapping(value="/{id}/referencedmotiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getReferencedMotionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> referencedMotionsVO = new ArrayList<MasterVO>();
		
		try{
			
			Motion motion = Motion.findById(Motion.class, id);
			
			if(motion != null){
				List<ReferenceUnit> referencedMotions = motion.getReferencedUnits();
				
				for(ReferenceUnit ru : referencedMotions){
					Motion rMotion = Motion.findById(Motion.class, ru.getDevice());
					if(rMotion != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(rMotion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(rMotion.getNumber(), locale.toString()));
						if(rMotion.getDetails()!= null && !rMotion.getDetails().isEmpty()){
							mVO.setValue(rMotion.getDetails());
						}else{
							mVO.setValue(rMotion.getRevisedDetails());
						}
						
						referencedMotionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return referencedMotionsVO;
	}
	
		
	/**** To get the clubbed questions text ****/
	@RequestMapping(value="/{id}/referencedstandalonetext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getReferencedStandaloneTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> referencedQuestionsVO = new ArrayList<MasterVO>();
		
		try{
			
			StandaloneMotion standalonemotion = StandaloneMotion.findById(StandaloneMotion.class, id);
			
			if(standalonemotion != null){
				List<ReferenceUnit> referencedQuestions = standalonemotion.getReferencedEntities();
				
				for(ReferenceUnit ru : referencedQuestions){
					StandaloneMotion rstandalonemotion = StandaloneMotion.findById(StandaloneMotion.class, ru.getDevice());
					if(rstandalonemotion != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(rstandalonemotion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(rstandalonemotion.getNumber(), locale.toString()));
						if(rstandalonemotion.getRevisedReason()!= null && !rstandalonemotion.getRevisedReason().isEmpty()){
							mVO.setValue(rstandalonemotion.getRevisedReason());
						}else{
							mVO.setValue(rstandalonemotion.getReason());
						}
						
						referencedQuestionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return referencedQuestionsVO;
	}
	
	@RequestMapping(value="/newpendingtasks", method=RequestMethod.GET)
	public @ResponseBody MasterVO getNewPendingTasks(HttpServletRequest request, Locale locale){
		MasterVO data = new MasterVO();
		try{
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strStatus = request.getParameter("status");
			String strHouseType = request.getParameter("houseType");
			String strWorkflowType = request.getParameter("workflowType");
			String strWorkflowSubType = request.getParameter("workflowSubType");
			
			if(strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strStatus != null && !strStatus.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()){
				
				CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
				
					if(csptServer.getValue().equals("TOMCAT")){
						
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"), "UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"), "UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
					}
				}
				
				
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("locale", locale.toString());
				parameters.put("assignee", this.getCurrentUser().getActualUsername());
				parameters.put("sessionYear", strSessionYear);
				parameters.put("sessionType", strSessionType);
				parameters.put("houseType", strHouseType);
				parameters.put("status", strStatus);
				if(strWorkflowType != null && !strWorkflowType.isEmpty()){
					parameters.put("workflowType", strWorkflowType);
				}
				if(strWorkflowSubType != null && !strWorkflowSubType.isEmpty()){
					parameters.put("workflowSubType", strWorkflowSubType);
				}
				
				List<WorkflowDetails> workflows = WorkflowDetails.findPendingWorkflowOfCurrentUser(parameters, "assignmentTime", ApplicationConstants.ASC);
				
				if(workflows != null){
					
						data.setValue(String.valueOf(workflows.size()));
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return data;
	}
	
	@RequestMapping(value="/getStatusByDeviceType", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getStatusByDeviceType(HttpServletRequest request, Locale locale){
		String strDeviceType=request.getParameter("deviceType");
		List<MasterVO> masterVOs=new ArrayList<MasterVO>();
		if(strDeviceType!=null && !strDeviceType.isEmpty()){
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			if(deviceType!=null){
				try {
					List<Status> statuses=Status.findStartingWith(deviceType.getDevice()+"_", "name", "desc", locale.toString());
					for(Status s:statuses){
						MasterVO masterVO=new MasterVO();
						masterVO.setValue(s.getType());
						masterVO.setName(s.getName());
						masterVOs.add(masterVO);
					}
					
				} catch (ELSException e) {
					e.printStackTrace();
				}
				
			}
		}
		return masterVOs;
	}
	
	@RequestMapping(value="/getQuestionByNumberAndSession", method=RequestMethod.GET)
	public @ResponseBody Boolean getQuestionByNumberAndSession(HttpServletRequest request, Locale locale){
		Boolean flag=false;
		String strNumber=request.getParameter("number");
		String strSession=request.getParameter("session");
		String strDeviceType=request.getParameter("deviceType");
		if(strNumber!=null && !strNumber.isEmpty() 
			&& strSession!=null && !strSession.isEmpty()){
			Session session=Session.findById(Session.class, Long.parseLong(strSession));
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			Integer questionNumber=null;
			if(csptDeployment!=null){
				String server=csptDeployment.getValue();
				if(server.equals("TOMCAT")){
					try {
						strNumber = new String(strNumber.getBytes("ISO-8859-1"),"UTF-8");
						questionNumber=  FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
								
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					try {
						questionNumber=FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			flag=Question.isExist(questionNumber,deviceType, session, locale.toString());
		}
		return flag;
	}
	
	@RequestMapping(value="/adjournmentmotion/duplicatenumber", method=RequestMethod.GET)
	public @ResponseBody Boolean isDuplicateNumberedAdjournmentMotion(HttpServletRequest request, Locale locale) throws ParseException, UnsupportedEncodingException{
		Boolean flag=false;
		String strAdjourningDate=request.getParameter("adjourningDate");
		String strNumber=request.getParameter("number");		
		if(strNumber!=null && !strNumber.isEmpty() 
			&& strAdjourningDate!=null && !strAdjourningDate.isEmpty()){
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(csptDeployment!=null){
				String server=csptDeployment.getValue();
				if(server.equals("TOMCAT")){
					strNumber = new String(strNumber.getBytes("ISO-8859-1"),"UTF-8");
				}
			}
			Date adjourningDate = FormaterUtil.formatStringToDate(strAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT);
			Integer adjournmentMotionNumber=FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
			flag=AdjournmentMotion.isDuplicateNumberExist(adjourningDate, adjournmentMotionNumber, null, locale.toString());
		}
		return flag;
	}
	
	@RequestMapping(value = "/eventmotionbynumberandsession", method = RequestMethod.GET)
	public @ResponseBody Boolean eventMotionByNumberAndSession(HttpServletRequest request, Locale locale){
		Boolean flag = false;
		String strNumber = request.getParameter("number");
		String strSession = request.getParameter("session");
		String strDeviceType = request.getParameter("deviceType");
		if(strNumber != null && !strNumber.isEmpty() 
			&& strSession != null && !strSession.isEmpty()){
			Session session = Session.findById(Session.class, Long.parseLong(strSession));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			Integer motionNumber = null;
			if(csptDeployment != null){
				String server = csptDeployment.getValue();
				if(server.equals("TOMCAT")){
					try {
						strNumber = new String(strNumber.getBytes("ISO-8859-1"),"UTF-8");
						motionNumber = FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
								
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					try {
						motionNumber = FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			flag = EventMotion.isExist(motionNumber, deviceType, session, locale.toString());
		}
		return flag;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/pendingtasksdevices", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTasksDeviceId(HttpServletRequest request, Locale locale){
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strStatus = request.getParameter("status");
		String strWfSubType = request.getParameter("wfSubType");
		String strGrid = request.getParameter("grid");
		String strGroup = request.getParameter("group");
		String strSubdepartment = request.getParameter("subdepartment");
		String strAnsweringDate = request.getParameter("answeringDate");
		
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> vos = new ArrayList<MasterVO>();
				
		try {
			String server=csptDeployment.getValue();
			SessionType sessionType = null;
			HouseType houseType = null;
			Integer year = null;
			Session session = null;					
			DeviceType deviceType = null;
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			List data = null;
			
			if(strGrid.equals("workflow")){
				if(csptDeployment!=null){
					if(server.equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strStatus = new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
						strWfSubType = new String(strWfSubType.getBytes("ISO-8859-1"),"UTF-8");
						if(strGroup != null && !strGroup.isEmpty()){
							strGroup = new String(strGroup.getBytes("ISO-8859-1"), "UTF-8");
						}
					}
				}
				
				sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				houseType = HouseType.findByName(strHouseType, locale.toString());
				year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("workflowSubType", new String[]{strWfSubType});
				parameters.put("assignee", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("locale", new String[]{locale.toString()});
				if(deviceType.getType().contains(ApplicationConstants.DEVICE_RESOLUTIONS)){
					data = Query.findReport("ROIS_STATUS_REPORT_DEVICES_WF", parameters);
				}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_MOTIONS_CALLING)){
					data = Query.findReport("MOIS_STATUS_REPORT_DEVICES_WF", parameters);
				}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
					Integer iGroupNumber = new Integer(strGroup);
					parameters.put("group", new String[]{iGroupNumber.toString()});
					parameters.put("answeringDate", new String[]{strAnsweringDate});
					data = Query.findReport("QIS_STATUS_REPORT_DEVICES_WF", parameters);
				}
			}else if(strGrid.equals("device")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				houseType = HouseType.findByType(strHouseType, locale.toString());
				year = new Integer(Integer.parseInt(strSessionYear));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("subdepartment", new String[]{strSubdepartment});
				if(deviceType.getType().contains(ApplicationConstants.DEVICE_RESOLUTIONS)){
					data = Query.findReport("ROIS_STATUS_REPORT_DEVICES_DV", parameters);
				}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
					parameters.put("group", new String[]{strGroup});
					data = Query.findReport("QIS_STATUS_REPORT_DEVICES_DV", parameters);
				}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_MOTIONS_CALLING)){
					data = Query.findReport("MOIS_STATUS_REPORT_DEVICES_DV", parameters);
				}
				
			}
			
			if(data != null){
				for(Object o : data){
					Object[] objx = (Object[]) o;
					MasterVO vo = new MasterVO();
					if(objx[0] != null){
						vo.setValue(objx[0].toString());
						vos.add(vo);
					}
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vos;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/pendingtasksdevicessmois", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTasksDevicesSMOIS(HttpServletRequest request, Locale locale){
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strStatus = request.getParameter("status");
		String strWfSubType = request.getParameter("wfSubType");
		String strGrid = request.getParameter("grid");
		String strGroup = request.getParameter("group");
		String strSubdepartment = request.getParameter("subdepartment");
		String strAnsweringDate = request.getParameter("answeringDate");
		
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> vos = new ArrayList<MasterVO>();
				
		try {
			String server=csptDeployment.getValue();
			SessionType sessionType = null;
			HouseType houseType = null;
			Integer year = null;
			Session session = null;					
			DeviceType deviceType = null;
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			List data = null;
			
			if(strGrid.equals("workflow")){
				if(csptDeployment!=null){
					if(server.equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strStatus = new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
						strWfSubType = new String(strWfSubType.getBytes("ISO-8859-1"),"UTF-8");
						strGroup = new String(strGroup.getBytes("ISO-8859-1"), "UTF-8");
					}
				}
				
				sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				houseType = HouseType.findByName(strHouseType, locale.toString());
				year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				Integer iGroupNumber = new Integer(strGroup);
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("workflowSubType", new String[]{strWfSubType});
				parameters.put("assignee", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("group", new String[]{iGroupNumber.toString()});
				parameters.put("answeringDate", new String[]{strAnsweringDate});
				
				data = Query.findReport("SMOIS_STATUS_REPORT_DEVICES_WF", parameters);
				
			}else if(strGrid.equals("device")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				houseType = HouseType.findByType(strHouseType, locale.toString());
				year = new Integer(Integer.parseInt(strSessionYear));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("group", new String[]{strGroup});
				parameters.put("subdepartment", new String[]{strSubdepartment});
				parameters.put("locale", new String[]{locale.toString()});
				data = Query.findReport("SMOIS_STATUS_REPORT_DEVICES_DV", parameters);
			}
			
			if(data != null){
				for(Object o : data){
					Object[] objx = (Object[]) o;
					MasterVO vo = new MasterVO();
					if(objx[0] != null){
						vo.setValue(objx[0].toString());
						vos.add(vo);
					}
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vos;
	}
	@RequestMapping(value="dummypage", method=RequestMethod.GET)
	public String getDummyPage(ModelMap model, Locale locale){
		model.addAttribute("data","data");
		return "question/dummy";
	}
	
	@RequestMapping(value="/getChartAnsweringDateByGroup", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getChartAnsweringDateFromGroup(HttpServletRequest request, ModelMap model,Locale locale){
		String strGroup=request.getParameter("group");
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		List<MasterVO> masterVOs= new ArrayList<MasterVO>();
		if(strGroup!=null && !strGroup.isEmpty()
			&& strHouseType!=null && !strHouseType.isEmpty()
			&& strSessionType!=null && !strSessionType.isEmpty()
			&& strSessionYear!=null && !strSessionYear.isEmpty()){
			try {
				CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptDeployment!=null){
					String server=csptDeployment.getValue();
					if(server.equals("TOMCAT")){
						strGroup = new String(strGroup.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
					}
				}
				Integer groupNumber=new Integer(FormaterUtil.getDecimalFormatterWithNoGrouping(0, locale.toString()).parse(strGroup).toString());
				HouseType houseType=HouseType.findByName(strHouseType, locale.toString());
				SessionType sessionType=SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				Integer sessionYear=new Integer(FormaterUtil.getDecimalFormatterWithNoGrouping(0,locale.toString()).parse(strSessionYear).toString());
				Group group=Group.findByNumberHouseTypeSessionTypeYear(groupNumber, houseType, sessionType, sessionYear);
				if(group!=null){
					List<QuestionDates> questionDates=group.getQuestionDates();
					for(QuestionDates qd:questionDates){
						MasterVO masterVO=new MasterVO();
						masterVO.setValue(qd.getAnsweringDate().toString());
						masterVO.setName(FormaterUtil.formatDateToString(qd.getAnsweringDate(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
						masterVOs.add(masterVO);
					}
				}
						
				
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return masterVOs;
			
		
	}
	@RequestMapping(value="/device/actors",method=RequestMethod.GET)
	public @ResponseBody List<Reference> findActorsByQuestionNumber(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		List<Reference> actors = new ArrayList<Reference>();
		
		try{
			String strDevice = request.getParameter("device");
			String strUserGroup = request.getParameter("usergroup");
			String strLevel = request.getParameter("level");
			String strDeviceNumber = request.getParameter("deviceNumber");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strHouseType = request.getParameter("houseType");
			
			
			if(strDevice != null && strUserGroup != null
					&& strDeviceNumber != null && strSessionType != null
					&& strSessionYear != null && strHouseType != null){
				if(!strDevice.isEmpty() && !strUserGroup.isEmpty() 
						&& !strDeviceNumber.isEmpty() && !strSessionType.isEmpty()
						&& !strSessionYear.isEmpty() && !strHouseType.isEmpty()){
					
					SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, new Integer(strSessionYear));
					UserGroup userGroup = UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
					
					if(strDevice.equals(Question.class.getSimpleName())){
												
						Question question = Question.getQuestion(session.getId(), new Integer(strDeviceNumber), locale.toString());
						if(strLevel == null){
							if(question.getLevel() != null && !question.getLevel().isEmpty()){
								strLevel = question.getLevel();
							}else{
								strLevel = "1";
							}
						}
						
						Status internalStatus = (question != null)? question.getInternalStatus() : null;	
						
						actors = WorkflowConfig.findQuestionActorsVO(question,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return actors;
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/newpendingmessages", method=RequestMethod.GET)
	public @ResponseBody List getNewMessage(HttpServletRequest request, Locale locale){
		List pendingMessage = null;
		try{
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strHouseType = request.getParameter("houseType");
			
			if(strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()){
				
				CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
				
					if(csptServer.getValue().equals("TOMCAT")){
						
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"), "UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"), "UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"), "UTF-8");
					}
				}
				
				SessionType sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
				Integer year = new Integer(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear)));
				
				Map<String, String[]> parameters = new HashMap<String, String[]>();
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("recepientUserName", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("sessionYear", new String[]{year.toString()});
				parameters.put("sessionType", new String[]{sessionType.getId().toString()});
				parameters.put("houseType", new String[]{houseType.getType()});
				
				pendingMessage = Query.findReport("USER_MESSAGES", parameters);
				
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return pendingMessage;
	}
	
//	@RequestMapping(value="/test/session/{sessionId}", method=RequestMethod.GET)
//	public @ResponseBody String test(@PathVariable("sessionId") final Long sessionId) {
////		long[] questionNumbers = new long[] {
////				48996, 49093, 49334, 49343, 49359, 49632, 49633,
////				49638, 49687, 49689, 49693, 49694, 49695, 49617,
////				49719, 49720, 49721, 49785, 49788, 49787, 49867,
////				49877, 50295, 50304, 50341, 50360, 50491, 50519,
////				50521, 50524, 51040, 51103, 51104, 51130, 51192,
////				51193, 51277, 51391, 51393, 51422, 51638, 51797
////		};
//		String locale = ApplicationConstants.DEFAULT_LOCALE;
//		
//		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
//		long deviceTypeId = deviceType.getId();
//		try{
//		CustomParameter cp = CustomParameter.findByName(CustomParameter.class, "SQ_CONVERT_TO_UNSTARRED_AND_ADMIT", "");
//		if(cp != null) {
//			String qnNumbers = cp.getValue();
//			String[] qnNumbersArr = qnNumbers.split(",");
//			for(String qnNumber : qnNumbersArr) {
//				Integer questionNumber = Integer.parseInt(qnNumber.trim());
//				Question q = Question.getQuestion(sessionId, deviceTypeId, questionNumber, locale);
//				ManualCouncilUtil.performActionOnConvertToUnstarredAndAdmit(q);
//			}
//		}
//			return "SUCCESS";
//		}catch(Exception e){
//			return "FAIL";
//		}
//	}
	
	
	@RequestMapping(value="/findYaadiLayingDateForYaadi" ,method=RequestMethod.GET)
	public @ResponseBody Reference findYaadiLayingDateForYaadi(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model) throws Exception {
		String formattedYaadiLayingDate = null;
		String sessionId = request.getParameter("sessionId");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		if(sessionId!=null && strYaadiNumber!=null){
			if(!sessionId.isEmpty() && !strYaadiNumber.isEmpty()){
				CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
					if(csptServer.getValue().equals("TOMCAT")){
						strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");							
					}
				}
				Session session = Session.findById(Session.class, Long.parseLong(sessionId));
				if(session==null) {
					logger.error("**** Session not found with request parameter sessionId ****");
					throw new ELSException();
				}	
				Integer yaadiNumber = (Integer) (FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strYaadiNumber).intValue());
				Date yaadiLayingDate = Question.findYaadiLayingDateForYaadi(null, session, yaadiNumber, locale.toString());
				if(yaadiLayingDate!=null) {
					formattedYaadiLayingDate = FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
				} else {
					formattedYaadiLayingDate = "-";
				}
			}
		}		
		return new Reference(formattedYaadiLayingDate, formattedYaadiLayingDate);
	}
	
	@RequestMapping(value="/checkfinalizationofnumberedyaadi" ,method=RequestMethod.GET)
	public @ResponseBody Boolean checkFinalizationOfNumberedYaadi(final HttpServletRequest request, HttpServletResponse response, final Locale locale, final ModelMap model) throws Exception {
		Boolean isNumberedYaadiFinalized = false;
		String sessionId = request.getParameter("sessionId");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		if(sessionId!=null && strYaadiNumber!=null){
			if(!sessionId.isEmpty() && !strYaadiNumber.isEmpty()){
				CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptServer != null && csptServer.getValue() != null && !csptServer.getValue().isEmpty()){
					if(csptServer.getValue().equals("TOMCAT")){
						strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
					}
				}
				Session session = Session.findById(Session.class, Long.parseLong(sessionId));
				if(session==null) {
					logger.error("**** Session not found with request parameter sessionId ****");
					throw new ELSException();
				}	
				Integer yaadiNumber = (Integer) (FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strYaadiNumber).intValue());
				Date yaadiLayingDate = Question.findYaadiLayingDateForYaadi(null, session, yaadiNumber, locale.toString());
				if(yaadiLayingDate!=null) {
					isNumberedYaadiFinalized = Question.isNumberedYaadiFinalized(null, session, yaadiNumber, yaadiLayingDate, locale.toString());
				}				
			}
		}		
		return isNumberedYaadiFinalized;
	}

	
	@RequestMapping(value="/cutmotion/actors", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findCutMotionActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		List<Reference> actors = new ArrayList<Reference>();
		String strCutmotion = request.getParameter("cutmotion");
		String strInternalStatus = request.getParameter("status");
		String strUserGroup = request.getParameter("usergroup");
		String strLevel = request.getParameter("level");
		if (strCutmotion != null && strInternalStatus != null
				&& strUserGroup != null && strLevel != null) {
			if ((!strCutmotion.isEmpty()) && (!strInternalStatus.isEmpty())
					&& (!strUserGroup.isEmpty()) && (!strLevel.isEmpty())) {
				Status internalStatus = Status.findById(Status.class, Long.parseLong(strInternalStatus));
				CutMotion motion = CutMotion.findById(CutMotion.class, Long.parseLong(strCutmotion));
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
				actors = WorkflowConfig.findCutMotionActorsVO(motion, internalStatus, userGroup, Integer.parseInt(strLevel), locale.toString());
			}
		}
		return actors;
	}

	@RequestMapping(value="/eventmotion/actors", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findEventMotionActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		List<Reference> actors = new ArrayList<Reference>();
		String strCutmotion = request.getParameter("eventmotion");
		String strInternalStatus = request.getParameter("status");
		String strUserGroup = request.getParameter("usergroup");
		String strLevel = request.getParameter("level");
		if (strCutmotion != null && strInternalStatus != null
				&& strUserGroup != null && strLevel != null) {
			if ((!strCutmotion.isEmpty()) && (!strInternalStatus.isEmpty())
					&& (!strUserGroup.isEmpty()) && (!strLevel.isEmpty())) {
				Status internalStatus = Status.findById(Status.class, Long.parseLong(strInternalStatus));
				EventMotion motion = EventMotion.findById(EventMotion.class, Long.parseLong(strCutmotion));
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
				actors = WorkflowConfig.findEventMotionActorsVO(motion, internalStatus, userGroup, Integer.parseInt(strLevel), locale.toString());
			}
		}
		return actors;
	}
	
	@RequestMapping(value="/bill/findsectionbyhierarchyorder", method=RequestMethod.GET)
	public @ResponseBody String findBillSectionByHierarchyOrder(final HttpServletRequest request, final Locale locale) throws ELSException {
		String returnValue = "";
		String billId = request.getParameter("billId");
		String language = request.getParameter("language");
		String hierarchyOrder = request.getParameter("hierarchyOrder");
		if(billId!=null && !billId.isEmpty() && language!=null && !language.isEmpty() 
				&& hierarchyOrder!=null && !hierarchyOrder.isEmpty()) {
			Section section = Bill.findSectionByHierarchyOrder(Long.parseLong(billId), language, hierarchyOrder);
			if(section!=null) {
				returnValue = section.getId().toString();
			}
		}
		return returnValue;
	}

	@RequestMapping(value="/cutmotiondate/actors",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getCutMotionDateActors(HttpServletRequest request, Locale locale){
		CutMotionDate cutMotionDate = CutMotionDate.findById(CutMotionDate.class, new Long(request.getParameter("cutMotionDate")));
		return CutMotionDateControllerUtility.getActors(request, cutMotionDate, locale.toString());
	}
	
	@RequestMapping(value="/findParty", method=RequestMethod.GET)
	public @ResponseBody Long findPartyByMemberId(final HttpServletRequest request,
			final Locale locale){
		String strMemberId = request.getParameter("memberId");
		if(strMemberId != null && !strMemberId.isEmpty()){
			Member member = Member.findById(Member.class, Long.parseLong(strMemberId));
			Party party = member.findParty();
			return party.getId();
		}
		return null;
	}

	@RequestMapping(value="/getDeviceNumber",method=RequestMethod.GET)
	public @ResponseBody MasterVO getDeviceNumber(HttpServletRequest request, Locale locale){
		String strDeviceId = request.getParameter("deviceId");
		String strDeviceType = request.getParameter("deviceType");
		MasterVO masterVO=new MasterVO();
		if(strDeviceType!=null && !strDeviceType.isEmpty()
			&& strDeviceId!=null && !strDeviceId.isEmpty()){
			DeviceType deviceType=DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			String device=deviceType.getDevice();
			if(device.equals(ApplicationConstants.QUESTION)){
				Question question=Question.findById(Question.class, Long.parseLong(strDeviceId));
				masterVO.setName(question.getNumber().toString());
			}else if(device.equals(ApplicationConstants.RESOLUTION)){
				Resolution resolution = Resolution.findById(Resolution.class, Long.parseLong(strDeviceId));
				masterVO.setName(resolution.getNumber().toString());
			}
		}
		return masterVO;
	}
	
	@RequestMapping(value="/section/findSeriesByLanguage", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> findSectionOrderSeriesForGivenLanguage(HttpServletRequest request, Locale locale) {
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		String languageType = request.getParameter("language");
		if(languageType!=null && !languageType.isEmpty()) {
			Language language = Language.findByFieldName(Language.class, "type", languageType, locale.toString());
	        if(language!=null) {
	        	List<SectionOrderSeries> sectionOrderSeries = SectionOrderSeries.findAllByFieldName(SectionOrderSeries.class, "language", language, "name", ApplicationConstants.ASC, locale.toString());
		        if(sectionOrderSeries!=null && !sectionOrderSeries.isEmpty()) {
		        	for(SectionOrderSeries series: sectionOrderSeries) {
		        		MasterVO masterVO = new MasterVO();
		        		masterVO.setId(series.getId());
		        		masterVO.setName(series.getName());
		        		masterVOs.add(masterVO);
		        	}
		        }
	        }	        
		}
		return masterVOs;
	}
	
	@RequestMapping(value="/section/findOrderSequenceByNumberAndSeries", method=RequestMethod.GET)
	public @ResponseBody MasterVO findSectionOrderSequenceByNumberAndSeries(HttpServletRequest request, Locale locale) throws ELSException, UnsupportedEncodingException {
		MasterVO jsonData = new MasterVO();
		String orderSequence = "";
		String number = request.getParameter("number");
		String orderSeriesId = request.getParameter("seriesId");
		if(number==null || number.isEmpty() || orderSeriesId==null || orderSeriesId.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
			throw new ELSException();	
		}
		if(deploymentServer.getValue().equals("TOMCAT")){
			number = new String(number.getBytes("ISO-8859-1"), "UTF-8");		
		}
		SectionOrderSeries orderingSeries = SectionOrderSeries.findById(SectionOrderSeries.class, Long.parseLong(orderSeriesId));
		orderSequence = Section.findOrderInSeries(number, orderingSeries, locale.toString());
		if(orderSequence!=null && !orderSequence.isEmpty()) {
			orderSequence = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(orderSequence), locale.toString());
		}
		jsonData.setName(orderSequence);		
		return jsonData;
	}
	
	@RequestMapping(value="/bill/section/getReferenceText", method=RequestMethod.GET)
	public @ResponseBody MasterVO getReferenceTextForSection(HttpServletRequest request, Locale locale) throws ELSException, UnsupportedEncodingException {
		MasterVO jsonData = new MasterVO();
		String referenceText = "";
		String billId = request.getParameter("billId");
		String number = request.getParameter("number");
		String language = request.getParameter("language");
		if(billId==null || billId.isEmpty() || number==null || number.isEmpty() || language==null || language.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
			throw new ELSException();	
		}
		if(deploymentServer.getValue().equals("TOMCAT")){		
			number = new String(number.getBytes("ISO-8859-1"), "UTF-8");		
		}
		String[] numberArr = number.split("\\.");
		
		if(numberArr.length==1) {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
			if(bill == null) {
				throw new ELSException();
			}
			referenceText = bill.findTextOfGivenDraftTypeInGivenLanguage("revised_contentDraft", language);
			if(referenceText==null || referenceText.isEmpty()) {
				referenceText = bill.findTextOfGivenDraftTypeInGivenLanguage("contentDraft", language);
			}
			if(referenceText==null) {
				referenceText = "";
			}
		} else {
			String parentNumber = "";
			for(int i=0; i<numberArr.length-1;i++) {
				parentNumber += numberArr[i];
				if(i!=numberArr.length-2) {
					parentNumber += ".";
				}
			}
			Section parentSection = Bill.findSection(Long.parseLong(billId), language, parentNumber);
			if(parentSection==null) {
				throw new ELSException();
			}
			referenceText = parentSection.getText();
		}
		jsonData.setName(referenceText);		
		return jsonData;
	}
	
	@RequestMapping(value="/bill/section/referOrders", method=RequestMethod.GET)
	public String referOrdersForSection(HttpServletRequest request, ModelMap model, Locale locale) throws ELSException, UnsupportedEncodingException {
		List<MasterVO> sectionOrders = new ArrayList<MasterVO>();
		String returnPath = "bill/section/referOrders";
		String billId = request.getParameter("billId");
		String sectionNumber = request.getParameter("sectionNumber");
		String language = request.getParameter("language");
		if(billId==null || billId.isEmpty() || sectionNumber==null || sectionNumber.isEmpty() || language==null || language.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
			throw new ELSException();	
		}
		if(deploymentServer.getValue().equals("TOMCAT")){		
			sectionNumber = new String(sectionNumber.getBytes("ISO-8859-1"), "UTF-8");		
		}
		Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
		if(bill == null) {
			throw new ELSException();
		}
		List<Section> sections = Bill.findAllSectionsAtHierarchyLevelOfGivenSection(bill.getId(), language, sectionNumber);
		if(sections!=null && !sections.isEmpty()) {
			for(Section s: sections) {
				MasterVO sectionOrder = new MasterVO();
				sectionOrder.setName(s.getNumber());
				String sOrder = s.findOrder();
				if(sOrder!=null) {
					sectionOrder.setValue(FormaterUtil.formatNumberNoGrouping(Integer.parseInt(sOrder), locale.toString()));
				} else {
					sectionOrder.setValue("");
				}
				sectionOrders.add(sectionOrder);
			}			
		}	
		model.addAttribute("sectionOrders", sectionOrders);
		return returnPath;
	}
	
	@RequestMapping(value="/ministry/{houseType}/{sessionYear}/{sessionType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMinistryOfSession(
			final Locale locale,
			@PathVariable("houseType")final String houseType,
			@PathVariable("sessionYear")final Integer sessionYear,
			@PathVariable("sessionType") final Long sessionType) {
		List<MasterVO> ministries = new ArrayList<MasterVO>();
		try {
			//populating ministry
			HouseType selectedHouseType = HouseType.findByType(houseType, locale.toString());
			SessionType selectedSessionType = SessionType.findById(SessionType.class, sessionType);
			Session selectedSession = Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, sessionYear);
			List<Ministry> mins = Ministry.findMinistriesAssignedToGroups(selectedHouseType, sessionYear, selectedSessionType, locale.toString());			
			
			for(Ministry i : mins){
				MasterVO masterVO = new MasterVO(i.getId(),i.getName());
				ministries.add(masterVO);
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		}
		return ministries;
	}
	
	
	@RequestMapping(value="/getSubDeparmentsByMinistries", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getSubDepartmentsByMinistries(
			final HttpServletRequest request,
			final Locale locale){
		List<MasterVO> subDepartmentVOs=new ArrayList<MasterVO>();
		String strMinistries = request.getParameter("ministries");
		String strSession = request.getParameter("session");
		if(strMinistries != null && !strMinistries.isEmpty()
			&& strSession != null && !strSession.isEmpty()){
			String[] ministries = strMinistries.split(",");
			Session session = Session.findById(Session.class, Long.parseLong(strSession));
			for(int i = 0;i < ministries.length;i++){
				Ministry ministry = Ministry.findById(Ministry.class, Long.parseLong(ministries[i]));
				Date onDate = session.getEndDate();
				if(onDate.before(new Date())) {
					CustomParameter csptNewHouseFormationInProcess = CustomParameter.findByName(CustomParameter.class, "NEW_HOUSE_FORMATION_IN_PROCESS", "");
					if(csptNewHouseFormationInProcess==null) {
						onDate = new Date();
					} else if(csptNewHouseFormationInProcess.getValue()==null) {
						onDate = new Date();
					} else if(!csptNewHouseFormationInProcess.getValue().equals("YES")) {
						onDate = new Date();
					}
				}
				List<SubDepartment> subDepartments=MemberMinister.findAssignedSubDepartments(ministry, onDate, locale.toString());
				for(SubDepartment sd:subDepartments){
					MasterVO masterVO=new MasterVO();
					masterVO.setId(sd.getId());
					masterVO.setName(sd.getName());
					subDepartmentVOs.add(masterVO);
				}
			}
		}
		return subDepartmentVOs;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/getGroupChangedQuestion",method=RequestMethod.GET)
	public @ResponseBody List getGroupChangedQuestion(final HttpServletRequest request,Locale locale){
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strAnsweringDate = request.getParameter("answeringDate");
		String strDeviceType = request.getParameter("deviceType");
		Session session =findSession(strHouseType, strSessionType, strSessionYear, locale);
		if(session!=null &&
			strAnsweringDate!=null && !strAnsweringDate.isEmpty() &&
			strDeviceType!=null && !strDeviceType.isEmpty()){
			Map<String, String[]> parametersMap = new HashMap<String, String[]>();
			parametersMap.put("locale", new String[]{locale.toString()});
			parametersMap.put("sessionId", new String[]{session.getId().toString()});
			parametersMap.put("deviceTypeId", new String[]{strDeviceType});
			parametersMap.put("answeringDate",new String[]{strAnsweringDate});
			List result=Query.findReport("STARRED_CHART_GROUP_CHANGED_DETAILS", parametersMap);
			return result;
		}
		
		return new ArrayList();
		
	}
	
	private Session findSession(String strHouseType,String strSessionType,String strSessionYear,Locale locale){
		if(strHouseType!=null && !strHouseType.isEmpty()
			&& strSessionType!=null && !strSessionType.isEmpty()
			&& strSessionYear!=null && !strSessionYear.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			if(houseType!=null && sessionType!=null){
				try {
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
					return session;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ELSException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	
	@RequestMapping(value="/findBillsForGivenCombinationOfYearAndHouseType", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> findBillsForGivenCombinationOfYearAndHouseType(final HttpServletRequest request, final Locale locale) {
		List<MasterVO> billMasterVOs = new ArrayList<MasterVO>();
		String billYear = request.getParameter("billYear");
		String billHouseType = request.getParameter("billHouseType");
		List<Bill> bills = null;
		if((billYear==null || billYear.isEmpty() || billYear.equals("0"))
				&& (billHouseType==null || billHouseType.isEmpty())) {
			bills = Bill.findAll(Bill.class, "number", ApplicationConstants.ASC, locale.toString());								
		} else if((billYear!=null && !billYear.isEmpty() && !billYear.equals("0"))
				&& (billHouseType==null || billHouseType.isEmpty())) {
			bills = Bill.findAllByYear(Integer.parseInt(billYear), locale.toString());						
		} else if((billYear==null || billYear.isEmpty() || billYear.equals("0"))
				&& (billHouseType!=null && !billHouseType.isEmpty())) {
			bills = Bill.findAllByIntroducingHouseType(billHouseType, locale.toString());							
		} else {
			bills = Bill.findAllInYearByIntroducingHouseType(Integer.parseInt(billYear), billHouseType, locale.toString());
		}
		if(bills!=null && !bills.isEmpty()) {
			for(Bill bill: bills) {
				MasterVO billMasterVO = new MasterVO();
				billMasterVO.setId(bill.getId());
				if(bill.getNumber()!=null) {
					billMasterVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(bill.getNumber(), locale.toString()));
				} else {
					billMasterVO.setFormattedNumber("-");
				}				
				billMasterVOs.add(billMasterVO);				
			}
		}
		return billMasterVOs;
		
	}
	
	@RequestMapping(value="/billamendmentmotion/getReferredSectionText", method=RequestMethod.GET)
	public @ResponseBody MasterVO getReferredSectionTextForBillAmendmentMotion(HttpServletRequest request, Locale locale) throws ELSException, UnsupportedEncodingException {
		MasterVO jsonData = new MasterVO();
		String referredSectionText = "";
		String billId = request.getParameter("billId");
		String sectionNumber = request.getParameter("sectionNumber");
		String language = request.getParameter("language");
		if(billId==null || billId.isEmpty() || sectionNumber==null || sectionNumber.isEmpty() || language==null || language.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
			throw new ELSException();	
		}
		if(deploymentServer.getValue().equals("TOMCAT")){		
			sectionNumber = new String(sectionNumber.getBytes("ISO-8859-1"), "UTF-8");		
		}
		Section referredSection = Bill.findSection(Long.parseLong(billId), language, sectionNumber);
		if(referredSection!=null) {
			jsonData.setId(referredSection.getId());
			referredSectionText = referredSection.getText();			
		} else {
			jsonData.setId(new Long("0"));
			referredSectionText = "";			
		}		
		jsonData.setName(referredSectionText);
		return jsonData;
	}
	
	@RequestMapping(value="/billamendmentmotion/getReferredBillDraft", method=RequestMethod.GET)
	public @ResponseBody MasterVO getReferredBillDraftForBillAmendmentMotion(HttpServletRequest request, Locale locale) throws ELSException, UnsupportedEncodingException {
		MasterVO jsonData = new MasterVO();
		String referredSectionText = "";
		String billId = request.getParameter("billId");
		String language = request.getParameter("language");
		if(billId==null || billId.isEmpty() || language==null || language.isEmpty()) {
			throw new ELSException();
		}
		Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
		if(bill == null) {
			throw new ELSException();
		}
		referredSectionText = bill.findTextOfGivenDraftTypeInGivenLanguage("revised_contentDraft", language);
		if(referredSectionText==null || referredSectionText.isEmpty()) {
			referredSectionText = bill.findTextOfGivenDraftTypeInGivenLanguage("contentDraft", language);
		}	
		jsonData.setName(referredSectionText==null?"":referredSectionText);
		return jsonData;
	}
	
	@RequestMapping(value="/billamendmentmotion/amendedBillInfo", method=RequestMethod.GET)
	public String getAmendedBillInfoForBillAmendmentMotion(HttpServletRequest request, ModelMap model, Locale locale) throws ELSException, UnsupportedEncodingException {
		String returnPath = "error";
		String amendedBillInfo = request.getParameter("amendedBillInfo");
		if(amendedBillInfo!=null && !amendedBillInfo.isEmpty()) {			
			CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
				throw new ELSException();	
			}
			if(deploymentServer.getValue().equals("TOMCAT")){		
				amendedBillInfo = new String(amendedBillInfo.getBytes("ISO-8859-1"), "UTF-8");
			}
			String[] amendedBillInfoParts = amendedBillInfo.split("~");
			model.addAttribute("amendedBillInfoParts", amendedBillInfoParts);
			returnPath = "billamendmentmotion/templates/amendedBillInfo";
		}		
		return returnPath;
	}
	
	@RequestMapping(value="/parseNumbersSeparatedByGivenDelimiter", method=RequestMethod.GET)
	public @ResponseBody String parseNumbersSeparatedByGivenDelimiter(HttpServletRequest request, Locale locale) throws ELSException, UnsupportedEncodingException, ParseException {
		StringBuffer parsedNumbers = new StringBuffer("");
		String requestedNumbers = request.getParameter("numbers");
		String delimiter = request.getParameter("delimiter");
		if(requestedNumbers==null || requestedNumbers.isEmpty() 
				|| delimiter==null || delimiter.isEmpty()) {
			throw new ELSException();
		} else {
			CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
				throw new ELSException();	
			}
			if(deploymentServer.getValue().equals("TOMCAT")){
				requestedNumbers = new String(requestedNumbers.getBytes("ISO-8859-1"), "UTF-8");
			}
			for(String i: requestedNumbers.split(delimiter)) {
				parsedNumbers.append(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(i.trim()));
				parsedNumbers.append(",");
			}
			if(parsedNumbers.length()>1) {
				parsedNumbers.deleteCharAt(parsedNumbers.length()-1);
			}
		}
		return parsedNumbers.toString().isEmpty()?"0":parsedNumbers.toString();
	}

	/**
	 * @param request
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/party/memberbyparty", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getMemberByParty(
			final HttpServletRequest request, final Locale locale,
			final ModelMap model){
		String strPartyId = request.getParameter("partyId");
		String type = this.getCurrentUser().getHouseType();
		HouseType houseType = null;
		if(type != null && !type.isEmpty()){
			 houseType = HouseType.findByType(type, locale.toString());
		}
		House house = House.find(houseType, new Date(), locale.toString());
		if(strPartyId != null && !strPartyId.isEmpty()){
			Party party = Party.findById(Party.class, Long.parseLong(strPartyId));
			List<Member> members = Member.findActiveMembersByParty(party,house,locale.toString());
			List<MasterVO> memberVOs = new ArrayList<MasterVO>();
			for(Member m : members){
				if(m != null){
					MasterVO vo = new MasterVO();
					vo.setId(m.getId());
					if(m.getAliasEnabled() && m.getAlias() != null && !m.getAlias().isEmpty()){
						vo.setName(m.getAlias());
					}else{
						vo.setName(m.getTitle().getName() + " " + m.getFirstName() + " " + m.getLastName());
					}
					memberVOs.add(vo);
				}
			}
			return memberVOs;
		}
		return null;
	}
	
	@RequestMapping(value = "/member/motions", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getMotions(HttpServletRequest request, Locale locale){
		List<Reference> motions = new ArrayList<Reference>();
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strDeviceType = request.getParameter("deviceType");
			
			Session session = null;
			if(strHouseType != null && !strHouseType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()){
				
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Integer sessionYear = new Integer(strSessionYear);
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
								
				if(houseType != null && sessionType != null){
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				}
			}
			
			DeviceType deviceType = null;
			if(strDeviceType != null && !strDeviceType.isEmpty()){
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
						
			}
			
			Member member = null;
			String strMember = request.getParameter("member");
			if(strMember != null && !strMember.isEmpty()){
				member = Member.findById(Member.class, new Long(strMember));
			}
			Status admitted = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale.toString());
			if(member != null && deviceType != null && admitted != null){
				List<Motion> mots = Motion.findAllByMember(session, deviceType, admitted, member, locale.toString());
				
				for(Motion m : mots){
					Reference ref = new Reference();
					ref.setId(m.getId().toString());
					if(m.getNumber() != null){
						ref.setName(m.getNumber().toString());
						ref.setName(FormaterUtil.formatNumberNoGrouping(m.getNumber(), locale.toString()));
					}else if(m.getPostBallotNumber() != null){
						ref.setName(m.getPostBallotNumber().toString());
						ref.setName(FormaterUtil.formatNumberNoGrouping(m.getPostBallotNumber(), locale.toString()));
					}
					motions.add(ref);
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return motions; 
	}
	
	@RequestMapping(value = "/alladmitted/motions", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getAllMotions(HttpServletRequest request, Locale locale){
		List<Reference> motions = new ArrayList<Reference>();
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strDeviceType = request.getParameter("deviceType");
			
			Session session = null;
			if(strHouseType != null && !strHouseType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()){
				
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				Integer sessionYear = new Integer(strSessionYear);
				SessionType sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
								
				if(houseType != null && sessionType != null){
					session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				}
			}
			
			DeviceType deviceType = null;
			if(strDeviceType != null && !strDeviceType.isEmpty()){
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
						
			}
						
			Status admitted = Status.findByType(ApplicationConstants.MOTION_FINAL_ADMISSION, locale.toString());
			if(deviceType != null && admitted != null){
				List<Motion> mots = Motion.findAllByStatus(session, deviceType, admitted, locale.toString());
				
				for(Motion m : mots){
					Reference ref = new Reference();
					ref.setId(m.getId().toString());
					if(m.getNumber() != null){
						ref.setName(m.getNumber().toString());
						ref.setName(FormaterUtil.formatNumberNoGrouping(m.getNumber(), locale.toString()));
					}else if(m.getPostBallotNumber() != null){
						ref.setName(m.getPostBallotNumber().toString());
						ref.setName(FormaterUtil.formatNumberNoGrouping(m.getPostBallotNumber(), locale.toString()));
					}
					motions.add(ref);
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return motions; 
	}
	
	@RequestMapping(value = "/decodestring", method = RequestMethod.GET)
	public @ResponseBody Reference getDecodedString(HttpServletRequest request, Locale locale){
		Reference ref = new Reference();
		try{
			CustomParameter csptServeCustomParameter = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			String strData = request.getParameter("data");
			if(csptServeCustomParameter != null){
				if(csptServeCustomParameter.getValue().equals("TOMCAT")){
					String strDS = new String(strData.getBytes("ISO-8859-1"), "UTF-8");
					ref.setName(strDS);
				}						
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return ref;
	}

	@RequestMapping(value="/discussionmotion/actors", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findDiscussionMotionActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		List<Reference> actors = new ArrayList<Reference>();
		String strDiscussionmotion = request.getParameter("discussionmotion");
		String strInternalStatus = request.getParameter("status");
		String strUserGroup = request.getParameter("usergroup");
		String strLevel = request.getParameter("level");
		if (strDiscussionmotion != null && !strDiscussionmotion.isEmpty()
			&& strInternalStatus != null && !strInternalStatus.isEmpty()
			&& strUserGroup != null && (!strUserGroup.isEmpty())
			&& strLevel != null && !strLevel.isEmpty()) {
			Status internalStatus = Status.findById(Status.class, Long.parseLong(strInternalStatus));
			DiscussionMotion motion = DiscussionMotion.findById(DiscussionMotion.class, Long.parseLong(strDiscussionmotion));
			UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
			actors = WorkflowConfig.findDiscussionMotionActorsVO(motion, internalStatus, userGroup, Integer.parseInt(strLevel), locale.toString());
		}
		return actors;
	}
	
	@RequestMapping(value="/loadStatusByDeviceType", method= RequestMethod.GET)
	public @ResponseBody List<MasterVO> loadStatusesByDeviceType(final HttpServletRequest request, final Locale locale) throws ELSException{
		String strDeviceType = request.getParameter("deviceType");
		String strUserGroupType = request.getParameter("currentusergroupType");
		List<MasterVO> statusVOs = new ArrayList<MasterVO>();
		if(strDeviceType!=null && !strDeviceType.isEmpty()
			&& strUserGroupType!=null && !strUserGroupType.isEmpty()){
			DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			CustomParameter allowedStatus = null;
			if(deviceType != null){
				allowedStatus = CustomParameter.
					findByName(CustomParameter.class,"QUESTION_GRID_STATUS_ALLOWED_" + deviceType.getType().toUpperCase()+"_"
								+ strUserGroupType.toUpperCase(), "");
			}
			List<Status> statuses=new ArrayList<Status>();
			if(allowedStatus!=null){
					statuses=Status.findStatusContainedIn(allowedStatus.getValue(),locale.toString());
				}else{
					CustomParameter defaultAllowedStatus=CustomParameter.findByName(CustomParameter.class,"QUESTION_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
					if(defaultAllowedStatus!=null){
						statuses=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale.toString());
					}
				}
			for(Status s:statuses){
				MasterVO masterVo = new MasterVO();
				masterVo.setId(s.getId());
				masterVo.setName(s.getName());
				masterVo.setType(s.getType());
				statusVOs.add(masterVo);
			}
		}
		return statusVOs;
	}

	@RequestMapping(value="/loadOriginalDeviceTypesForGivenDeviceType", method= RequestMethod.GET)
	public @ResponseBody List<DeviceType> loadOriginalDeviceTypesForGivenDeviceType(final HttpServletRequest request, final Locale locale) throws ELSException{
		
		String strDeviceType = request.getParameter("deviceType");		
		List<DeviceType> originalDeviceTypes = new ArrayList<DeviceType>();
		
		if(strDeviceType!=null && !strDeviceType.isEmpty()){
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(strDeviceType));
			originalDeviceTypes.addAll(DeviceType.findOriginalDeviceTypesForGivenDeviceType(deviceType));			
		}
		
		return originalDeviceTypes;
	}
	
	@RequestMapping(value="/billamendmentmotion/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findActorsForBillAmendmentMotion(final HttpServletRequest request,final ModelMap model,
			final Locale locale) throws ELSException {
		List<Reference> actors=new ArrayList<Reference>();
		String strBillAmendmentMotion=request.getParameter("billamendmentmotion");
		String strInternalStatus=request.getParameter("status");		
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		if(strBillAmendmentMotion!=null && !strBillAmendmentMotion.isEmpty() 
				&& strInternalStatus!=null && !strInternalStatus.isEmpty() 
				&& strUserGroup!=null && !strUserGroup.isEmpty() 
				&& strLevel!=null && !strLevel.isEmpty()) {
			Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
			if(internalStatus.getType().equals(ApplicationConstants.BILLAMENDMENTMOTION_FINAL_REJECT_TRANSLATION)) {
				return actors;
			}
			BillAmendmentMotion billAmendmentMotion=BillAmendmentMotion.findById(BillAmendmentMotion.class,Long.parseLong(strBillAmendmentMotion));
			UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
			actors=WorkflowConfig.findBillAmendmentMotionActorsVO(billAmendmentMotion,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
		} else {
			throw new ELSException();
		}
		return actors;
	}
	
	@RequestMapping(value="/standalonemotion/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findStandaloneMotionActors(final HttpServletRequest request,
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
				Status internalStatus = Status.findById(Status.class,Long.parseLong(strInternalStatus));
				StandaloneMotion standaloneMotion = StandaloneMotion.findById(StandaloneMotion.class,Long.parseLong(strQuestion));
				UserGroup userGroup = UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
				actors = WorkflowConfig.findStandaloneMotionActorsVO(standaloneMotion, internalStatus, userGroup, Integer.parseInt(strLevel), locale.toString());
			}
		}
		return actors;
	}
	
	@RequestMapping(value = "/sessiondates/{id}", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getSessionDates(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> dates = new ArrayList<MasterVO>();
		
		try{
			Session s = Session.findById(Session.class, id);
			
			Calendar startDate = Calendar.getInstance(); 
			startDate.setTime(s.getStartDate());
			
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(s.getEndDate());			
			
			for(; startDate.before(endDate); startDate.add(Calendar.DATE, 1)){
				if(!org.mkcl.els.domain.Holiday.isHolidayOnDate(startDate.getTime(), locale.toString())){
					MasterVO vo = new MasterVO();
					vo.setValue(FormaterUtil.formatDateToString(startDate.getTime(), ApplicationConstants.DB_DATEFORMAT));
					vo.setName(FormaterUtil.formatDateToString(startDate.getTime(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
					dates.add(vo);
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return dates;
	}
	
	@RequestMapping(value = "/user/isAuthenticatedWithEnteredPassword", method = RequestMethod.GET)
	public @ResponseBody boolean isUserAuthenticatedWithEnteredPassword(final HttpServletRequest request, final Locale locale) throws ELSException {
		boolean isAuthenticatedWithEnteredPassword  = false;
		String username = request.getParameter("username");
		String enteredPassword = request.getParameter("enteredPassword");
		if(username!=null && !username.isEmpty() && enteredPassword!=null && !enteredPassword.isEmpty()) {
			User user = User.findByUserName(username, locale.toString());
			if(user!=null && user.getId()!=null) {
				if(user.getCredential()!=null) {
					isAuthenticatedWithEnteredPassword = securityService.isAuthenticated(enteredPassword, user.getCredential().getPassword());
				}
			}
		} else {
			throw new ELSException();
		}
		return isAuthenticatedWithEnteredPassword;
	}
	
	@RequestMapping(value = "/user/isAuthenticatedWithEnteredHighSecurityPassword", method = RequestMethod.GET)
	public @ResponseBody boolean isUserAuthenticatedWithEnteredHighSecurityPassword(final HttpServletRequest request, final Locale locale) throws ELSException {
		boolean isAuthenticatedWithEnteredHighSecurityPassword  = false;
		String username = request.getParameter("username");
		String enteredHighSecurityPassword = request.getParameter("enteredHighSecurityPassword");
		if(username!=null && !username.isEmpty() && enteredHighSecurityPassword!=null && !enteredHighSecurityPassword.isEmpty()) {
			User user = User.findByUserName(username, locale.toString());
			if(user!=null && user.getId()!=null) {
				if(user.getCredential()!=null) {
					isAuthenticatedWithEnteredHighSecurityPassword = securityService.isAuthenticated(enteredHighSecurityPassword, user.getCredential().getHighSecurityPassword());
				}
			}
		} else {
			throw new ELSException();
		}
		return isAuthenticatedWithEnteredHighSecurityPassword;
	}
	
	@RequestMapping(value="/motionnumberinsession", method=RequestMethod.GET)
	public @ResponseBody Boolean getMotionByNumberAndSession(HttpServletRequest request, Locale locale){
		Boolean flag = false;
		String strNumber = request.getParameter("number");
		String strSession = request.getParameter("session");
		String strDeviceType = request.getParameter("deviceType");
		
		if (strNumber != null && !strNumber.isEmpty() 
				&& strSession != null && !strSession.isEmpty()) {
			
			Session session = Session.findById(Session.class,
					Long.parseLong(strSession));
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class,
					Long.parseLong(strDeviceType));
			
			CustomParameter csptDeployment = CustomParameter.findByName(
					CustomParameter.class, "DEPLOYMENT_SERVER", "");
			
			Integer motionNumber = null;
			
			if(csptDeployment != null){
				String server = csptDeployment.getValue();
				if(server.equals("TOMCAT")){
					try {
						strNumber = new String(strNumber.getBytes("ISO-8859-1"),"UTF-8");
						motionNumber =  FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
								
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					try {
						motionNumber = FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			flag = Motion.isExist(motionNumber, deviceType, session, locale.toString());
		}
		return flag;
	}
	
	@RequestMapping(value="/assignedGroupsInSession", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> assignedGroupsInSession(final HttpServletRequest request, final Locale locale){
		List<MasterVO> vos = new ArrayList<MasterVO>();
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strUserGroup = request.getParameter("userGroup");
			
			if(strHouseType != null && !strHouseType.isEmpty()
				&& strSessionType != null && !strSessionType.isEmpty()
				&& strSessionYear != null && !strSessionYear.isEmpty()
				&& strUserGroup != null && !strUserGroup.isEmpty()){
				
				String[] values = getDecodedString(new String[]{strHouseType,strSessionType, strSessionYear});
				
				HouseType houseType = HouseType.findByName(values[0], locale.toString());
				
				SessionType sessionType = SessionType.
						findByFieldName(SessionType.class, "sessionType", values[1], locale.toString());
				
				Integer year = new Integer(FormaterUtil.
						getNumberFormatterNoGrouping(locale.toString()).parse(values[2]).intValue());
				
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
				
				if(session != null){
					
					List<UserGroup> uGroups = this.getCurrentUser().getUserGroups();
					for(UserGroup userGroup : uGroups){
					
						if(UserGroup.isActiveInSession(session, userGroup, locale.toString())){

							Credential credential = userGroup.getCredential();
							User user = User.findByUserName(credential.getUsername(), locale.toString());
							Map<String,String> userGroupParam = UserGroup.findParametersByUserGroup(userGroup);
							
							String groupsAllowed = userGroupParam.get(ApplicationConstants.GROUPSALLOWED_KEY + "_" + locale.toString());
							
							for(String s: groupsAllowed.split(",")){
								MasterVO masterVo = new MasterVO();
								Group group = Group.findByNumberHouseTypeSessionTypeYear(new Integer(s), houseType, sessionType, year);
								masterVo.setName(FormaterUtil.formatNumberNoGrouping(new Integer(s), locale.toString()));
								masterVo.setId(group.getId());
								vos.add(masterVo);
							}
							break;
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return vos;
	}
	
	private String[] getDecodedString(String[] values){
		CustomParameter deploymentServer = CustomParameter.
				findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(deploymentServer != null && deploymentServer.getValue() != null && !deploymentServer.getValue().isEmpty()){
			if(deploymentServer.getValue().equals("TOMCAT")){

				for(int i = 0; i < values.length; i++){
					try {
						if(values[i] != null){
							values[i] = new String(values[i].getBytes("ISO-8859-1"), "UTF-8");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return values;
	}
	
	public String[] decodeString(String[] values){
		return getDecodedString(values);
	}
	
	public static String[] decodedValues(String[] values){
		return (new ReferenceController()).decodeString(values);
	}
	
	@RequestMapping(value = "/currentandprevioussession", method = RequestMethod.GET)	
	public @ResponseBody List<Reference> getCurrentAndPreviousSession(HttpServletRequest request, Locale locale){
		List<Reference> masters = new ArrayList<Reference>();
		try{
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strHouseType = request.getParameter("houseType");
			
			if(strSessionYear != null && !strSessionYear.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()){
				
				String[] decodedValues = getDecodedString(new String[]{strSessionYear, strSessionType, strHouseType});
				strSessionYear = decodedValues[0];
				strSessionType = decodedValues[1];
				strHouseType = decodedValues[2];
				
				HouseType hsT = HouseType.findByType(strHouseType, locale.toString());
				SessionType sT = SessionType.findById(SessionType.class, new Long(strSessionType));
				Integer year = new Integer(strSessionYear);
				
				Session currentSession = Session.findSessionByHouseTypeSessionTypeYear(hsT, sT, year);
				Session previousSession = Session.findPreviousSession(currentSession);
				
				if(currentSession != null){
					masters.add(new Reference(currentSession.getId().toString(), currentSession.getType().getSessionType()));
				}
				
				if(previousSession != null){
					masters.add(new Reference(previousSession.getId().toString(), previousSession.getType().getSessionType()));
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return masters;
	}
	
	@RequestMapping(value = "/adjournmentmotion/adjourningdatesforsession", method = RequestMethod.GET)	
	public @ResponseBody List<Object[]> findAdjourningDatesForSession(HttpServletRequest request, Locale locale) throws Exception{
		String houseTypeStr = request.getParameter("houseType");
		String sessionTypeStr= request.getParameter("sessionType");
		String sessionYearStr= request.getParameter("sessionYear");
		String usergroupType = request.getParameter("usergroupType");
		if(houseTypeStr==null||houseTypeStr.isEmpty()||sessionTypeStr==null||sessionTypeStr.isEmpty()||sessionYearStr==null||sessionYearStr.isEmpty()||usergroupType==null||usergroupType.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(csptDeployment!=null && csptDeployment.getValue()!=null){
			if(csptDeployment.getValue().equals("TOMCAT")){
				houseTypeStr = new String(houseTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionTypeStr = new String(sessionTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionYearStr = new String(sessionYearStr.getBytes("ISO-8859-1"),"UTF-8");
			}
		}
		HouseType houseType = HouseType.findByType(houseTypeStr, locale.toString());		
		if(houseType==null) {
			houseType = HouseType.findByName(HouseType.class, houseTypeStr, locale.toString());
		}
		SessionType sessionType = null;
		try {
			sessionType = SessionType.findById(SessionType.class, Long.parseLong(sessionTypeStr));
		} catch(NumberFormatException ne) {
			sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", sessionTypeStr, locale.toString());
		}
		Integer sessionYear = Integer.parseInt(sessionYearStr);
		Session session = Session.find(sessionYear, sessionType.getType(), houseType.getType());
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		/** populate session dates as possible adjourning dates **/
		List<Date> sessionDates = session.findAllSessionDates();
		List<Object[]> adjourningDates = this.populateDateListUsingCustomParameterFormat(sessionDates, "ADJOURNMENTMOTION_ADJOURNINGDATEFORMAT", locale.toString());
		
		/** populate default adjourning session date for the session **/
		Date defaultAdjourningDate = null;
		if(usergroupType.equals(ApplicationConstants.MEMBER)) {
			defaultAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(session, true);
		} else {
			defaultAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(session, false);
		}		
		adjourningDates.add(new Object[]{FormaterUtil.formatDateToString(defaultAdjourningDate, ApplicationConstants.SERVER_DATEFORMAT)});
		
		return adjourningDates;
	}

	@RequestMapping(value = "/rulessuspensionmotion/rulesuspensiondatesforsession", method = RequestMethod.GET)	
	public @ResponseBody List<Object[]> findRuleSuspensionDatesForSession(HttpServletRequest request, Locale locale) throws Exception{
		String houseTypeStr = request.getParameter("houseType");
		String sessionTypeStr= request.getParameter("sessionType");
		String sessionYearStr= request.getParameter("sessionYear");
		String usergroupType = request.getParameter("usergroupType");
		if(houseTypeStr==null||houseTypeStr.isEmpty()||sessionTypeStr==null||sessionTypeStr.isEmpty()||sessionYearStr==null||sessionYearStr.isEmpty()||usergroupType==null||usergroupType.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(csptDeployment!=null && csptDeployment.getValue()!=null){
			if(csptDeployment.getValue().equals("TOMCAT")){
				houseTypeStr = new String(houseTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionTypeStr = new String(sessionTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionYearStr = new String(sessionYearStr.getBytes("ISO-8859-1"),"UTF-8");
			}
		}
		HouseType houseType = HouseType.findByType(houseTypeStr, locale.toString());		
		if(houseType==null) {
			houseType = HouseType.findByName(HouseType.class, houseTypeStr, locale.toString());
		}
		SessionType sessionType = null;
		try {
			sessionType = SessionType.findById(SessionType.class, Long.parseLong(sessionTypeStr));
		} catch(NumberFormatException ne) {
			sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", sessionTypeStr, locale.toString());
		}
		Integer sessionYear = Integer.parseInt(sessionYearStr);
		Session session = Session.find(sessionYear, sessionType.getType(), houseType.getType());
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		/** populate session dates as possible adjourning dates **/
		List<Date> sessionDates = session.findAllSessionDates();
		List<Object[]> ruleSuspensionDates = this.populateDateListUsingCustomParameterFormat(sessionDates, "RULESSUSPENSIONMOTION_RULESUSPENSIONDATEFORMAT", locale.toString());
		
		/** populate default rules suspension session date for the session **/
		Date defaultRuleSuspensionDate = null;
		if(usergroupType.equals(ApplicationConstants.MEMBER)) {
			defaultRuleSuspensionDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(session, true);
		} else {
			defaultRuleSuspensionDate = RulesSuspensionMotion.findDefaultRuleSuspensionDateForSession(session, false);
		}		
		ruleSuspensionDates.add(new Object[]{FormaterUtil.formatDateToString(defaultRuleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT)});
		
		return ruleSuspensionDates;
	}
	
	@RequestMapping(value = "/proprietypoint/proprietypointdatesforsession", method = RequestMethod.GET)
	public @ResponseBody List<Object[]> findProprietyPointDatesForSession(HttpServletRequest request, Locale locale) throws Exception{
		String houseTypeStr = request.getParameter("houseType");
		String sessionTypeStr= request.getParameter("sessionType");
		String sessionYearStr= request.getParameter("sessionYear");
		String usergroupType = request.getParameter("usergroupType");
		if(houseTypeStr==null||houseTypeStr.isEmpty()||sessionTypeStr==null||sessionTypeStr.isEmpty()||sessionYearStr==null||sessionYearStr.isEmpty()||usergroupType==null||usergroupType.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(csptDeployment!=null && csptDeployment.getValue()!=null){
			if(csptDeployment.getValue().equals("TOMCAT")){
				houseTypeStr = new String(houseTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionTypeStr = new String(sessionTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionYearStr = new String(sessionYearStr.getBytes("ISO-8859-1"),"UTF-8");
			}
		}
		HouseType houseType = HouseType.findByType(houseTypeStr, locale.toString());		
		if(houseType==null) {
			houseType = HouseType.findByName(HouseType.class, houseTypeStr, locale.toString());
		}
		SessionType sessionType = null;
		try {
			sessionType = SessionType.findById(SessionType.class, Long.parseLong(sessionTypeStr));
		} catch(NumberFormatException ne) {
			sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", sessionTypeStr, locale.toString());
		}
		Integer sessionYear = Integer.parseInt(sessionYearStr);
		Session session = Session.find(sessionYear, sessionType.getType(), houseType.getType());
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		/** populate session dates as possible proprietypoint dates **/
		List<Date> sessionDates = session.findAllSessionDates();
		List<Object[]> proprietypointDates = this.populateDateListUsingCustomParameterFormat(sessionDates, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", locale.toString());
		
		/** populate default proprietypoint date for the session **/
		Date defaultProprietyPointDate = null;
		if(usergroupType.equals(ApplicationConstants.MEMBER)) {
			defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(session, true);
		} else {
			defaultProprietyPointDate = ProprietyPoint.findDefaultProprietyPointDateForSession(session, false);
		}
		String formattedDefaultProprietyPointDate = FormaterUtil.formatDateToStringUsingCustomParameterFormat(defaultProprietyPointDate, "PROPRIETYPOINT_PROPRIETYPOINTDATEFORMAT", locale.toString());
		proprietypointDates.add(new Object[]{FormaterUtil.formatDateToString(defaultProprietyPointDate, ApplicationConstants.SERVER_DATEFORMAT), formattedDefaultProprietyPointDate});
		
		return proprietypointDates;
	}
	
	@RequestMapping(value = "/selectedStatusType", method = RequestMethod.GET)
	public @ResponseBody String findStatusTypeById(HttpServletRequest request, Locale locale) {
		String statusType = "";
		String strStatusId = request.getParameter("statusId");
		if(strStatusId != null && !strStatusId.isEmpty()){
			Status status = Status.findById(Status.class, Long.parseLong(strStatusId));
			if(status != null){
				statusType = status.getType();
			}
		}
		return statusType;
		
	}
	
	@RequestMapping(value="/deviceexistsinsession", method=RequestMethod.GET)
	public @ResponseBody Boolean getDeviceInSession(HttpServletRequest request, Locale locale){
		
		Boolean flag = false;
		
		try{
			String strNumber = request.getParameter("number");
			String strSession = request.getParameter("session");
			String strDeviceType = request.getParameter("deviceType");
			
			if(strNumber != null && !strNumber.isEmpty() 
				&& strSession != null && !strSession.isEmpty()){
				
				Session session = Session.findById(Session.class, Long.parseLong(strSession));
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				Integer deviceNumber = null;
				
				String[] decodedStrings = getDecodedString(new String[]{strNumber});
				if(decodedStrings != null && decodedStrings.length > 0){
					strNumber = decodedStrings[0];
					
					deviceNumber = FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
				}
								
				if(deviceType != null && session != null){
					
					String device = deviceType.getType();
					
					if(device.indexOf(ApplicationConstants.DEVICE_CUTMOTIONS) == 0){
						
						flag = CutMotion.isExist(deviceNumber, deviceType, session, locale.toString());
						
					}else if(device.indexOf(ApplicationConstants.DEVICE_DISCUSSIONMOTIONS) == 0){
						
						flag = DiscussionMotion.isExist(deviceNumber, deviceType, session, locale.toString());						
						
					}else if(device.indexOf(ApplicationConstants.DEVICE_STANDALONE) == 0){
						
						flag = StandaloneMotion.isExist(deviceNumber, deviceType, session, locale.toString());
						
					}
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return flag;
	}
	
	@RequestMapping(value = "/specialmentionnotice/specialmentionnoticedatesforsession", method = RequestMethod.GET)	
	public @ResponseBody List<Object[]> findSpecialMentionNoticeDatesForSession(HttpServletRequest request, Locale locale) throws Exception{
		String houseTypeStr = request.getParameter("houseType");
		String sessionTypeStr= request.getParameter("sessionType");
		String sessionYearStr= request.getParameter("sessionYear");
		String usergroupType = request.getParameter("usergroupType");
		if(houseTypeStr==null||houseTypeStr.isEmpty()||sessionTypeStr==null||sessionTypeStr.isEmpty()||sessionYearStr==null||sessionYearStr.isEmpty()||usergroupType==null||usergroupType.isEmpty()) {
			throw new ELSException();
		}
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		if(csptDeployment!=null && csptDeployment.getValue()!=null){
			if(csptDeployment.getValue().equals("TOMCAT")){
				houseTypeStr = new String(houseTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionTypeStr = new String(sessionTypeStr.getBytes("ISO-8859-1"),"UTF-8");
				sessionYearStr = new String(sessionYearStr.getBytes("ISO-8859-1"),"UTF-8");
			}
		}
		HouseType houseType = HouseType.findByType(houseTypeStr, locale.toString());		
		if(houseType==null) {
			houseType = HouseType.findByName(HouseType.class, houseTypeStr, locale.toString());
		}
		SessionType sessionType = null;
		try {
			sessionType = SessionType.findById(SessionType.class, Long.parseLong(sessionTypeStr));
		} catch(NumberFormatException ne) {
			sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", sessionTypeStr, locale.toString());
		}
		Integer sessionYear = Integer.parseInt(sessionYearStr);
		Session session = Session.find(sessionYear, sessionType.getType(), houseType.getType());
		if(session==null || session.getId()==null) {
			throw new ELSException();
		}
		/** populate session dates as possible adjourning dates **/
		List<Date> sessionDates = session.findAllSessionDates();
		List<Object[]> specialmentionnoticeDates = this.populateDateListUsingCustomParameterFormat(sessionDates, "SPECIALMENTIONNOTICE_SPECIALMENTIONNOTICEDATEFORMAT", locale.toString());
		
		/** populate default specialmentionnotice session date for the session **/
		Date defaultSpecialMentionNoticeDate = null;
		if(usergroupType.equals(ApplicationConstants.MEMBER)) {
			defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(session, true);
		} else {
			defaultSpecialMentionNoticeDate = SpecialMentionNotice.findDefaultSpecialMentionNoticeDateForSession(session, false);
		}		
		specialmentionnoticeDates.add(new Object[]{FormaterUtil.formatDateToString(defaultSpecialMentionNoticeDate, ApplicationConstants.SERVER_DATEFORMAT)});
		
		return specialmentionnoticeDates;
	}
	
	/**
	 * Find actors.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the list< reference>
	 * @since v1.0.0
	 */
	@RequestMapping(value="/specialmentionnotice/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findSpecialMentionNoticeActors(final HttpServletRequest request,
			final ModelMap model, final Locale locale){
		List<Reference> actors=new ArrayList<Reference>();
		String strMotion=request.getParameter("motion");
		String strInternalStatus=request.getParameter("status");
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		if(strMotion!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null){
			if((!strMotion.isEmpty())&&(!strInternalStatus.isEmpty())&&
					(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())){
				Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
				SpecialMentionNotice specialMentionNotice=SpecialMentionNotice.findById(SpecialMentionNotice.class,Long.parseLong(strMotion));
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
				try {
					actors=WorkflowConfig.findSpecialMentionNoticeActorsVO(specialMentionNotice,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return actors;
	}
	
	/**** To get the special mention notice's notice content text ****/
	@RequestMapping(value="/specialmentionnotice/{id}/notice_content_text", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String getspecialMentionNoticeContentText(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		String noticeContentText = "";
		
		SpecialMentionNotice specialMentionNotice = SpecialMentionNotice.findById(SpecialMentionNotice.class, id);
		if(specialMentionNotice!=null) {
			if(specialMentionNotice.getNoticeContent()!=null) {
				if(specialMentionNotice.getRevisedNoticeContent()!=null && specialMentionNotice.getRevisedNoticeContent().length()>specialMentionNotice.getNoticeContent().length()) {
					noticeContentText = specialMentionNotice.getRevisedNoticeContent();
				} else {
					noticeContentText = specialMentionNotice.getNoticeContent();
				}
			} else {
				if(specialMentionNotice.getRevisedNoticeContent()!=null) {
					noticeContentText = specialMentionNotice.getRevisedNoticeContent();
				}
			}
		}
		
		return noticeContentText;
	}
	
	/**** To get the clubbed special mention notice's text ****/
	@RequestMapping(value="/specialmentionnotice/{id}/clubbedmotiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClubbedSpecialMentionNoticeTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> clubbedSpecialMentionNoticesVO = new ArrayList<MasterVO>();
		
		try{
			
			SpecialMentionNotice parent = SpecialMentionNotice.findById(SpecialMentionNotice.class, id);
			
			if(parent != null){
				List<ClubbedEntity> clubbedSpecialMentionNotices = parent.getClubbedEntities();
				
				for(ClubbedEntity ce : clubbedSpecialMentionNotices){
					SpecialMentionNotice cSpecialMentionNotice = ce.getSpecialMentionNotice();
					if(cSpecialMentionNotice != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(cSpecialMentionNotice.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(cSpecialMentionNotice.getNumber(), locale.toString()));
						mVO.setDisplayName(cSpecialMentionNotice.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						if(cSpecialMentionNotice.getRevisedNoticeContent() != null && !cSpecialMentionNotice.getRevisedNoticeContent().isEmpty()){
							mVO.setValue(cSpecialMentionNotice.getRevisedNoticeContent());
						}else{
							mVO.setValue(cSpecialMentionNotice.getNoticeContent());
						}
						
						clubbedSpecialMentionNoticesVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return clubbedSpecialMentionNoticesVO;
	}
	
	/**
	 * Find actors.
	 *
	 * @param request the request
	 * @param model the model
	 * @param locale the locale
	 * @return the list< reference>
	 * @since v1.0.0
	 */
	@RequestMapping(value="/adjournmentmotion/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findAdjournmentMotionActors(final HttpServletRequest request,
			final ModelMap model, final Locale locale){
		List<Reference> actors=new ArrayList<Reference>();
		String strMotion=request.getParameter("motion");
		String strInternalStatus=request.getParameter("status");
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		if(strMotion!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null){
			if((!strMotion.isEmpty())&&(!strInternalStatus.isEmpty())&&
					(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())){
				Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
				AdjournmentMotion adjournmentMotion=AdjournmentMotion.findById(AdjournmentMotion.class,Long.parseLong(strMotion));
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
				try {
					actors=WorkflowConfig.findAdjournmentMotionActorsVO(adjournmentMotion,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return actors;
	}
	
	/**** To get the adjournment motion's notice content text ****/
	@RequestMapping(value="/adjournmentmotion/{id}/notice_content_text", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String getAdjournmentMotionNoticeContentText(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		String noticeContentText = "";
		
		AdjournmentMotion adjournmentMotion = AdjournmentMotion.findById(AdjournmentMotion.class, id);
		if(adjournmentMotion!=null) {
			if(adjournmentMotion.getNoticeContent()!=null) {
				if(adjournmentMotion.getRevisedNoticeContent()!=null && adjournmentMotion.getRevisedNoticeContent().length()>adjournmentMotion.getNoticeContent().length()) {
					noticeContentText = adjournmentMotion.getRevisedNoticeContent();
				} else {
					noticeContentText = adjournmentMotion.getNoticeContent();
				}
			} else {
				if(adjournmentMotion.getRevisedNoticeContent()!=null) {
					noticeContentText = adjournmentMotion.getRevisedNoticeContent();
				}
			}
		}
		
		return noticeContentText;
	}
	
	/**** To get the clubbed adjournment motion's text ****/
	@RequestMapping(value="/adjournmentmotion/{id}/clubbedmotiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClubbedAdjournmentMotionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> clubbedAdjournmentMotionsVO = new ArrayList<MasterVO>();
		
		try{
			
			AdjournmentMotion parent = AdjournmentMotion.findById(AdjournmentMotion.class, id);
			
			if(parent != null){
				List<ClubbedEntity> clubbedAdjournmentMotions = parent.getClubbedEntities();
				
				for(ClubbedEntity ce : clubbedAdjournmentMotions){
					AdjournmentMotion cAdjournmentMotion = ce.getAdjournmentMotion();
					if(cAdjournmentMotion != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(cAdjournmentMotion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(cAdjournmentMotion.getNumber(), locale.toString()));
						mVO.setDisplayName(cAdjournmentMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						if(cAdjournmentMotion.getRevisedNoticeContent() != null && !cAdjournmentMotion.getRevisedNoticeContent().isEmpty()){
							mVO.setValue(cAdjournmentMotion.getRevisedNoticeContent());
						}else{
							mVO.setValue(cAdjournmentMotion.getNoticeContent());
						}
						
						clubbedAdjournmentMotionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return clubbedAdjournmentMotionsVO;
	}
	
	@RequestMapping(value="/cumulativememberwisequestionsreport/memberorder", method=RequestMethod.POST)
	public @ResponseBody List<MasterVO> orderMembersForCumulativeMemberwiseQuestionsReport(final HttpServletRequest request, Locale locale) throws ELSException, UnsupportedEncodingException {
		List<MasterVO> memberOrderVOs = new ArrayList<MasterVO>();
		String items = request.getParameter("items");
		if(items!=null && !items.isEmpty()) {			
			String[] unOrderedMembers = items.split(",");
			for(int index=0; index < unOrderedMembers.length; index++) {
				MasterVO memberOrderVO = new MasterVO();
				String[] unOrderedMemberData = unOrderedMembers[index].split("_");
				String strMemberId = unOrderedMemberData[0];
				memberOrderVO.setId(Long.parseLong(strMemberId));
				String memberName = unOrderedMemberData[1];
				memberOrderVO.setName(memberName);
				if(unOrderedMemberData.length>2) {
					Integer memberOrder = Integer.parseInt(unOrderedMemberData[2]);
					memberOrderVO.setOrder(memberOrder);
					memberOrderVO.setFormattedOrder(FormaterUtil.formatNumberNoGrouping(memberOrder, locale.toString()));
				} else {
					Integer unspecfiedMemberOrder = unOrderedMembers.length+1;
					memberOrderVO.setOrder(unspecfiedMemberOrder);
					memberOrderVO.setFormattedOrder(FormaterUtil.formatNumberNoGrouping(unspecfiedMemberOrder, locale.toString()));
				}				
				memberOrderVOs.add(memberOrderVO);
			}
			if(!memberOrderVOs.isEmpty()) {
				memberOrderVOs = MasterVO.sortByOrder(memberOrderVOs, ApplicationConstants.ASC);
				for(MasterVO m: memberOrderVOs) {
					if(m.getOrder().equals(memberOrderVOs.size()+1)) {
						m.setFormattedOrder("-");
					}						
				}
			}
		}
		return memberOrderVOs;
	}

	@RequestMapping(value="/processingMode",method=RequestMethod.GET)
	public @ResponseBody String getProcessingMode(HttpServletRequest request, Locale locale){
		String strHouseType = request.getParameter("houseType");
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String processingMode = "";
		if(strHouseType != null && !strHouseType.isEmpty()
			&& strSessionYear != null && !strSessionYear.isEmpty()
			&& strSessionType != null && !strSessionType.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			try {
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				processingMode = session.getParameter(ApplicationConstants.QUESTION_STARRED_PROCESSINGMODE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return processingMode;
	}
	
	@RequestMapping(value = "/checkforduplicacy", method = RequestMethod.GET)
	public @ResponseBody Boolean doClassTesting(HttpServletRequest request, Locale locale){
		Object retVal = new Boolean(false);
		
		String strClassName = request.getParameter("device");
		String strDeviceType = request.getParameter("deviceType");
		String strSession = request.getParameter("session");
		String strNumber = request.getParameter("number");

		try{
			
			String[] decodedData = getDecodedString(new String[]{strNumber});
			Integer deviceNumber = null;
			if(decodedData != null && decodedData.length > 0){
				deviceNumber = new Integer(decodedData[0]);
			}
			
			
			Class<?> cls = Class.forName("org.mkcl.els.domain." + strClassName);
			Object obj = cls.newInstance();
			Method method = null;
			
			Object[] parameters = new Object[4];
			
			parameters[0] = deviceNumber;
			parameters[1] = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			parameters[2] = Session.findById(Session.class, new Long(strSession));
			parameters[3] = locale.toString();
			
			
			for(Method m : cls.getDeclaredMethods()){
				if(m.getName().equals("isExist")){
					method = m;
					break;
				}
			}

			
			retVal = method.invoke(obj, parameters);
		}catch(Exception e){
			logger.error("error", e);
		}
		return (Boolean)retVal;
	}
	
	
	@RequestMapping(value = "/committeename", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getCommitteeName(HttpServletRequest request, Locale locale){
		List<MasterVO> committeeNameVOs = new ArrayList<MasterVO>();
		String strCommitteeTypeId = request.getParameter("committeeTypeId");
		if(strCommitteeTypeId != null && !strCommitteeTypeId.isEmpty()){
			CommitteeType committeeType = CommitteeType.findById(CommitteeType.class, Long.parseLong(strCommitteeTypeId));
			if(committeeType != null){
				List<CommitteeName> committeeNames = CommitteeName.find(committeeType, locale.toString());
				for(CommitteeName c: committeeNames){
					MasterVO masterVO = new MasterVO();
					masterVO.setId(c.getId());
					masterVO.setName(c.getDisplayName());
					committeeNameVOs.add(masterVO);
				}
			}
			
		}
		return committeeNameVOs;
		
	}
	
	@RequestMapping(value = "/committeemeeting", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getCommitteeMeeting(HttpServletRequest request, Locale locale){
		List<MasterVO> committeeMeetingVOs = new ArrayList<MasterVO>();
		String strCommitteeNameId = request.getParameter("committeeNameId");
		if(strCommitteeNameId != null && !strCommitteeNameId.isEmpty()){
			CommitteeName committeeName = CommitteeName.findById(CommitteeName.class, Long.parseLong(strCommitteeNameId));
			if(committeeName != null){
				List<CommitteeMeeting> committeeMeetings = CommitteeMeeting.find(committeeName, locale.toString(),ApplicationConstants.DESC);
				//Collections.sort(committeeMeetings, Collections.reverseOrder());
				for(CommitteeMeeting c: committeeMeetings){
					String titile="";
					MasterVO masterVO = new MasterVO();
					masterVO.setId(c.getId());
					masterVO.setName(FormaterUtil.formatDateToString(c.getMeetingDate(), ApplicationConstants.SERVER_DATEFORMAT)+' '+c.getStartTime());
				
						if(c.getCreatedBy() != null && !c.getCreatedBy().isEmpty()){
							titile="Created By:"+c.getCreatedBy();
							
						}
						
						if(c.getCommitteeSubject() != null){
							titile=titile+' '+"Subject:"+c.getCommitteeSubject().getName();
						}					
						masterVO.setValue(titile);

					committeeMeetingVOs.add(masterVO);
				}
			}
			
		}
		return committeeMeetingVOs;
		
	}
	
	@RequestMapping(value="/rosterdaysfromcommitteemeeting",method=RequestMethod.GET)
	public @ResponseBody List<Integer> getRosterDaysFromCommitteeMeeting(final HttpServletRequest request, final Locale locale){
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		String strlanguage=request.getParameter("language");
		List<Integer> rosterDays=new ArrayList<Integer>();
		if(strlanguage!=null && !strlanguage.isEmpty()
			&& strCommitteeMeeting!=null && !strCommitteeMeeting.isEmpty()){
			CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
			Language language = Language.findById(Language.class, Long.parseLong(strlanguage));
			List<Roster> rosters = Roster.findAllRosterByCommitteeMeeting(committeeMeeting,language, locale.toString());
			for(Roster r:rosters){
				rosterDays.add(r.getDay());
			}
		}
		return rosterDays;
	}
	
	@RequestMapping(value="prashnavali/actors/workflow/{workflowName}", method=RequestMethod.GET)
	public @ResponseBody List<Reference> getPrashnavaliActors(
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
			/*** As the committees flow may contain cross housetype users like undersecretary of assembly
			 *  can be under secretary of council committee ***/
			List<WorkflowActor> wfActors = new ArrayList<WorkflowActor>();
			String strHouseType = userGroup.getParameterValue(ApplicationConstants.HOUSETYPE_KEY+"_"+locale);
			HouseType userHouseType = HouseType.findByName(strHouseType, locale);
			if(userHouseType != null && userHouseType.getType().equals(ApplicationConstants.BOTH_HOUSE)){
				 wfActors = WorkflowConfig.findPrashnavaliActors(
						userHouseType, userGroup, status, workflowName, 
						assigneeLevel, locale);
			}else{
				 wfActors = WorkflowConfig.findPrashnavaliActors(
						houseType, userGroup, status, workflowName, 
						assigneeLevel, locale);
			}
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
	
	@RequestMapping(value = "getnextlotofassignedpendingtasks", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getNextLotOfPendingTasks(HttpServletRequest request, Locale locale){
		List<Reference> retVal = new ArrayList<Reference>();	
		
		try{
			String strHouseType = request.getParameter("houseType");
			String strSessionYear = request.getParameter("sessionYear");
			String strSessioneType = request.getParameter("sessionType");
			String strDeviceType = request.getParameter("deviceType");
			String strModule = request.getParameter("module");
			String strStatus = request.getParameter("status");
			String strWFSubType = request.getParameter("workflowSubType");
			String strAssignee = request.getParameter("assignee");
			String strGroup = request.getParameter("group");
			String strAnsDate = request.getParameter("answeringDate");
			String strStart = request.getParameter("start");
			String strEnd = request.getParameter("end");
			
			/*String[] decodedString = getDecodedString(new String[]{strHouseType, strSessionYear, strSessioneType, 
					strDeviceType, strModule, strStatus, strWFSubType, strAssignee, strGroup, strAnsDate});*/
			
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("locale", new String[]{locale.toString()});
			params.put("houseType", new String[]{strHouseType});
			params.put("sessionYear", new String[]{strSessionYear});
			params.put("sessionType",new String[]{ strSessioneType});
			params.put("deviceType",new String[]{ strDeviceType});
			params.put("module",new String[]{ strModule});
			params.put("status", new String[]{strStatus});
			params.put("workflowSubType", new String[]{strWFSubType});
			params.put("assignee", new String[]{strAssignee});
			params.put("group", new String[]{strGroup});
			params.put("answeringDate", new String[]{strAnsDate});
			
			@SuppressWarnings("rawtypes")
			List data = Query.findReport("NEXT_LOT_OF_PENDING_TASKS", params, new Integer(strStart), new Integer(strEnd));
			if(data != null && !data.isEmpty()){
				for(Object d : data){
					Object[] o = (Object[]) d;
					if(o[1] != null){
						Reference ref = new Reference();
						ref.setId(o[1].toString());
						retVal.add(ref);
					}
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	@RequestMapping(value="/groupchartansweringdate", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getChartAnsweringByGroup(HttpServletRequest request, ModelMap model, Locale locale){
		
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		
		try{
			String strGroup = request.getParameter("group");

			if (strGroup != null && !strGroup.isEmpty()) {

				CustomParameter csptDeployment = CustomParameter.findByName(
						CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if (csptDeployment != null) {
					String server = csptDeployment.getValue();
					if (server.equals("TOMCAT")) {
						strGroup = getDecodedString(new String[] { strGroup })[0];
					}
				}

				Group group = Group.findById(Group.class, new Long(strGroup));

				if (group != null) {
					List<QuestionDates> questionDates = group
							.getQuestionDates();
					for (QuestionDates qd : questionDates) {
						MasterVO masterVO = new MasterVO();
						masterVO.setId(qd.getId());
						masterVO.setValue(qd.getAnsweringDate().toString());
						masterVO.setName(FormaterUtil.formatDateToString(qd.getAnsweringDate(),
								ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
						masterVOs.add(masterVO);
					}
				}

			}
		} catch (Exception e){
			logger.error("error", e);
		}
		return masterVOs;			
	}
	
	@RequestMapping(value="/billamendmentmotion/subjectline", method=RequestMethod.GET)
	public String getSubjectLineForBillAmendmentMotion(HttpServletRequest request, ModelMap model, Locale locale) throws ELSException, UnsupportedEncodingException {
		String returnPath = "error";
		String amendedBillInfo = request.getParameter("amendedBillInfo");
		if(amendedBillInfo!=null && !amendedBillInfo.isEmpty()) {			
			CustomParameter deploymentServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServer == null || deploymentServer.getValue() == null || deploymentServer.getValue().isEmpty()){
				throw new ELSException();	
			}
			if(deploymentServer.getValue().equals("TOMCAT")){		
				amendedBillInfo = new String(amendedBillInfo.getBytes("ISO-8859-1"), "UTF-8");
			}
			String[] amendedBillInfoParts = amendedBillInfo.split("~");
			model.addAttribute("amendedBillInfoParts", amendedBillInfoParts);
			returnPath = "billamendmentmotion/templates/subjectline";
		}		
		return returnPath;
	}
	
	/**** To get the clubbed billamendment motion's text ****/
	@RequestMapping(value="/billamendmentmotion/{id}/clubbedmotiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClubbedBillAmendmentMotionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> clubbedBillAmendmentMotionsVO = new ArrayList<MasterVO>();
		
		try{
			
			BillAmendmentMotion parent = BillAmendmentMotion.findById(BillAmendmentMotion.class, id);
			
			if(parent != null){
				List<ClubbedEntity> clubbedBillAmendmentMotions = parent.getClubbedEntities();
				
				for(ClubbedEntity ce : clubbedBillAmendmentMotions){
					BillAmendmentMotion cBillAmendmentMotion = ce.getBillAmendmentMotion();
					if(cBillAmendmentMotion != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(cBillAmendmentMotion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(cBillAmendmentMotion.getNumber(), locale.toString()));
						mVO.setDisplayName(cBillAmendmentMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
						mVO.setValue(cBillAmendmentMotion.findDefaultSectionAmendmentContent());						
						
						clubbedBillAmendmentMotionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return clubbedBillAmendmentMotionsVO;
	}

	@RequestMapping(value="/chart/chart_entries")
	public @ResponseBody List<MasterVO> findChartEntriesForGivenChart(HttpServletRequest request, Locale locale) {
		List<MasterVO> chartEntryMasterVOs = new ArrayList<MasterVO>();
		String chartId = request.getParameter("chartId");
		if(chartId!=null && !chartId.isEmpty()) {
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("chartId", new String[]{chartId});
			parameters.put("locale", new String[]{locale.toString()});
			@SuppressWarnings("unchecked")
			List<Object[]> result = Query.findReport("CHART_ENTRIES_FOR_GIVEN_CHART", parameters);
			if(result!=null && !result.isEmpty()) {
				for(Object[] o: result) {
					MasterVO chartEntryMasterVO = new MasterVO();
					chartEntryMasterVO.setId(Long.parseLong(o[0].toString()));
					chartEntryMasterVO.setName(o[1].toString());
					chartEntryMasterVO.setValue(o[2].toString());
					chartEntryMasterVOs.add(chartEntryMasterVO);
				}
			}
		}
		return chartEntryMasterVOs;
	}

	@RequestMapping(value = "/allministries", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getAllMinistries(HttpServletRequest request, Locale locale){
		
		
		List<MasterVO> retVal = new ArrayList<MasterVO>();
		try{
			String strSessionId = request.getParameter("session");
			Long sessionId = null;
			if(strSessionId != null && !strSessionId.isEmpty()){
				sessionId = new Long(strSessionId);
			}
			CustomParameter csptUseMinistryFromCurrentSession = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.QUESTION_REFERENCING_USE_MINISTRIES_FROM_CURRENT_SESSION, "");
			List<Ministry> ministries = null;
			
			if(csptUseMinistryFromCurrentSession != null && csptUseMinistryFromCurrentSession.getValue() != null
					&& !csptUseMinistryFromCurrentSession.getValue().isEmpty() 
					&& csptUseMinistryFromCurrentSession.getValue().equalsIgnoreCase("yes")){
				Session session = null;
				if(sessionId != null){
					session = Session.findById(Session.class, sessionId);
				}
				if(session != null){
					ministries = Ministry.findAssignedMinistriesInSession(session.getStartDate(), locale.toString());
				}else{
					ministries = Ministry.findAssignedMinistriesInSession(new Date(), locale.toString());
				}
				
			}else{
				ministries = Ministry.findAll(Ministry.class, "name", ApplicationConstants.ASC, locale.toString());
			}
			
			if(ministries != null && !ministries.isEmpty()){
				retVal = createMatsreVOs(ministries);
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}		
		return retVal;
	}
	
	private List<MasterVO> createMatsreVOs(List<? extends BaseDomain> data){
		List<MasterVO> retVal = new ArrayList<MasterVO>();
		try{			
			for(BaseDomain b : data){
				
				Long id = null;
				String name = null;
				if(b instanceof Ministry){		
					id = ((Ministry)b).getId();
					name = ((Ministry)b).getName();
				}else if(b instanceof SubDepartment){					
					id = ((SubDepartment)b).getId();
					name = ((SubDepartment)b).getName();
				}
				retVal.add(new MasterVO(id, name));
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
		
	}
	
	@RequestMapping(value = "/subdepartments/ministry", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getAllSubDepartmentsByMinistry(HttpServletRequest request, Locale locale){
	
		List<MasterVO> retVal = new ArrayList<MasterVO>();
		try{
			
			String strMinistryId = request.getParameter("ministryId");
			Long ministryId = null;
			if(strMinistryId != null && !strMinistryId.isEmpty()){
				ministryId = new Long(strMinistryId);
			}
			 
			List<SubDepartment> subds = new ArrayList<SubDepartment>();
			if(ministryId != null){
				Ministry mins = Ministry.findById(Ministry.class, ministryId);
				subds = MemberMinister.findAssignedSubDepartments(new String[]{mins.getName()}, locale.toString());
			}
			
			if(subds != null && !subds.isEmpty()){
				retVal = createMatsreVOs(subds);
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}		
		return retVal;
	}
	
	@RequestMapping(value = "/alleligiblemembers", method = RequestMethod.GET)
	public @ResponseBody List<Reference> getAllEligibleMembers(HttpServletRequest request, @RequestParam("session") long sessionId, Locale locale){
		List<Reference> retVal = new ArrayList<Reference>();
		
		try{
			
			Session session = Session.findById(Session.class, sessionId);
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("houseId", new String[]{session.getHouse().getId().toString()});
			Date limitingDateForSession = null;
			
			if(session.getEndDate().compareTo(new Date())<=0) {
				limitingDateForSession = session.getEndDate();
			} else if(session.getStartDate().compareTo(new Date())>=0) {
				limitingDateForSession = session.getStartDate();
			} else {
				limitingDateForSession = new Date();
			}
			
			params.put("limitingDateForSession", new String[]{FormaterUtil.formatDateToString(limitingDateForSession, ApplicationConstants.DB_DATEFORMAT)});
			params.put("locale", new String[]{locale.toString()});
			
			List resultList = Query.findReport("MEMBERS_ELIGIBLE_FOR_QUESTION_SUBMISSION_IN_GIVEN_HOUSE", params);
			if(resultList!=null && !resultList.isEmpty()) {
				for(Object o: resultList) {			
					
					Object[] result = (Object[])o;
					Reference member = new Reference();
					
					if(result[0]!=null) {
						member.setId(result[0].toString());
					}
					if(result[1]!=null) {
						member.setName(result[1].toString());
					}
					
					retVal.add(member);
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	@RequestMapping(value = "/question/flowStatusType", method = RequestMethod.GET)
	public @ResponseBody String getFlowStatusTypeForQuestion(final HttpServletRequest request, final Locale locale) {
		String flowStatusType = "";
		String questionId = request.getParameter("questionId");
		if(questionId!=null && !questionId.isEmpty()) {
			Question question = Question.findById(Question.class, Long.parseLong(questionId));
			if(question!=null) {
				QuestionDraft secondPreviousDraft = question.findSecondPreviousDraft();
				if(secondPreviousDraft!=null && secondPreviousDraft.getInternalStatus()!=null) {					
					String internalStatusType = secondPreviousDraft.getInternalStatus().getType();
					String recommendationStatusType = secondPreviousDraft.getRecommendationStatus().getType();
					if(recommendationStatusType.endsWith(ApplicationConstants.CLUBBING_POST_ADMISSION_WORKFLOW.split("_workflow")[0])
							|| recommendationStatusType.endsWith(ApplicationConstants.ADMIT_DUE_TO_REVERSE_CLUBBING_WORKFLOW.split("_workflow")[0])
							|| recommendationStatusType.endsWith(ApplicationConstants.UNCLUBBING_WORKFLOW.split("_workflow")[0])
							|| recommendationStatusType.endsWith(ApplicationConstants.CLUBBING_WITH_UNSTARRED_FROM_PREVIOUS_SESSION_WORKFLOW.split("_workflow")[0])) {
						flowStatusType = recommendationStatusType;
					} else {
						flowStatusType = internalStatusType;
					}
				}
			}
		}
		return flowStatusType;
	}
	@RequestMapping(value= "/allparties/{session}", method = RequestMethod.GET)
	public @ResponseBody List<Reference>  getAllPartiesByHouse(@PathVariable("session") Long sessionId, HttpServletRequest request, Locale locale){
		
		List<Reference> retVal = new ArrayList<Reference>();
		try{
			Session session = Session.findById(Session.class, sessionId);
			if(session != null){
				List<Party> parties = Party.findActiveParties(session.getHouse(), locale.toString());
				for(Party p : parties){
					Reference ref = new Reference(p.getId().toString(), p.getName());
					retVal.add(ref);
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}	
	
	@RequestMapping(value= "/yaadidetails", method = RequestMethod.GET)
	public String loadYaadiDetails(HttpServletRequest request, ModelMap model, Locale locale) {
		String retVal = "yaadi_details/error";
		
		String strHouseType = request.getParameter("houseType");
		String strSession = request.getParameter("sessionId");
		String strDeviceType = request.getParameter("deviceType");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		
		if(strHouseType!=null && strSession!=null && strDeviceType!=null && strYaadiNumber!=null){
			if(!strHouseType.isEmpty() && !strSession.isEmpty() && !strDeviceType.isEmpty() && !strYaadiNumber.isEmpty()){
				try {
					HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
					if(houseType==null) {
						houseType = HouseType.findByName(strHouseType, locale.toString());
					}
					if(houseType==null) {
						houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					}										
					if(houseType==null) {
						logger.error("**** HouseType Not Found ****");
						model.addAttribute("errorcode", "HOUSETYPE_NOTFOUND");											
					} else {
						model.addAttribute("houseTypeId", houseType.getId());
						Session session = Session.findById(Session.class, Long.parseLong(strSession));
						if(session==null) {								
							logger.error("**** Session Not Found ****");
							model.addAttribute("errorcode", "SESSION_NOTFOUND");
						} else {
							model.addAttribute("sessionId", session.getId());
							DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
							if(deviceType==null) {								
								logger.error("**** Device Type Not Found ****");
								model.addAttribute("errorcode", "DEVICETYPE_NOTFOUND");
							} else {
								model.addAttribute("deviceTypeId", deviceType.getId());		
								CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
								if(csptServer != null && csptServer.getValue() != null && csptServer.getValue().equals("TOMCAT")){
									strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
								}
								Integer yaadiNumber = Integer.parseInt(strYaadiNumber);
								if(yaadiNumber!=null) {
									YaadiDetails yaadiDetails = null;
									List<Device> totalDevicesInYaadi = new ArrayList<Device>();
									if(yaadiNumber.intValue()>0) {
										yaadiDetails = YaadiDetails.find(deviceType, session, yaadiNumber, locale.toString());
									}		
									if(yaadiDetails==null) {
										/** populate Data for New Yaadi which is either first or latest **/
										model.addAttribute("yaadiDetailsId", "");
										model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(yaadiNumber, locale.toString()));
										model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										totalDevicesInYaadi = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, 0, locale.toString());
									} else {
										/** populate Data for Given Yaadi which is not yet filled **/
										model.addAttribute("yaadiDetailsId", yaadiDetails.getId());
										model.addAttribute("yaadiNumber", FormaterUtil.formatNumberNoGrouping(yaadiDetails.getNumber(), locale.toString()));
										Date yaadiLayingDate = yaadiDetails.getLayingDate();
										if(yaadiLayingDate!=null) {
											model.addAttribute("yaadiLayingDate", FormaterUtil.formatDateToString(yaadiLayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
											model.addAttribute("isYaadiLayingDateSet", "yes");
										}
										if(yaadiDetails.getLayingStatus()!=null 
												&& (yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_READY)
													|| yaadiDetails.getLayingStatus().getType().equals(ApplicationConstants.YAADISTATUS_LAID))
										) {
											totalDevicesInYaadi = yaadiDetails.getDevices();
										} else {
											List<Device> existingDevicesInYaadi = yaadiDetails.getDevices();
											totalDevicesInYaadi.addAll(existingDevicesInYaadi);
											if(!yaadiDetails.isNumberedYaadiFilled()) {
												List<Device> newlyAddedDevices = YaadiDetails.findDevicesEligibleForNumberedYaadi(deviceType, session, existingDevicesInYaadi.size(), locale.toString());
												if(newlyAddedDevices!=null && !newlyAddedDevices.isEmpty()) {
													totalDevicesInYaadi.addAll(newlyAddedDevices);
												}
											}
										}																														
									}
									/** populate device vo **/									
									if(totalDevicesInYaadi!=null && !totalDevicesInYaadi.isEmpty()) {
										String yaadiDevicesCount = FormaterUtil.formatNumberNoGrouping(totalDevicesInYaadi.size(), locale.toString());
										model.addAttribute("yaadiDevicesCount", yaadiDevicesCount);
										List<DeviceVO> totalDevicesInYaadiVOs = populateDevicesForNumberedYaadi(totalDevicesInYaadi, locale.toString());
										totalDevicesInYaadiVOs = DeviceVO.sort(totalDevicesInYaadiVOs, "number", ApplicationConstants.ASC);
										model.addAttribute("totalDevicesInYaadiVOs", totalDevicesInYaadiVOs);
									}
									List<Date> availableYaadiLayingDates = Question.findAvailableYaadiLayingDatesForSession(null, session, locale.toString());
									if(availableYaadiLayingDates!=null && !availableYaadiLayingDates.isEmpty()) {
										List<String> yaadiLayingDates = new ArrayList<String>();
										for(Date eligibleDate: availableYaadiLayingDates) {
											yaadiLayingDates.add(FormaterUtil.formatDateToString(eligibleDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
										}
										model.addAttribute("yaadiLayingDates", yaadiLayingDates);
									}
									/** populate group numbers **/
									CustomParameter deviceTypesHavingGroupCP = CustomParameter.findByName(CustomParameter.class, "DEVICETYPES_HAVING_GROUPS", "");
									if(deviceTypesHavingGroupCP!=null && deviceTypesHavingGroupCP.getValue()!=null && !deviceTypesHavingGroupCP.getValue().isEmpty()) {
										if(deviceTypesHavingGroupCP.getValue().contains(deviceType.getType())) {
											CustomParameter groupNumberLimitCP = CustomParameter.findByName(CustomParameter.class, "NO_OF_GROUPS", "");
											if(groupNumberLimitCP!=null && groupNumberLimitCP.getValue()!=null && !groupNumberLimitCP.getValue().isEmpty()) {
												Integer groupNumberLimit = Integer.parseInt(groupNumberLimitCP.getValue()); 
												List<Reference> groupNumbers = new ArrayList<Reference>();
												for(Integer i=1; i<=groupNumberLimit; i++) {
													Reference groupNumber = new Reference();
													groupNumber.setNumber(i.toString());
													groupNumber.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
													groupNumbers.add(groupNumber);
												}
												model.addAttribute("groupNumbers", groupNumbers);
											}
										}
									}
									/** populate yaadi statuses **/
									CustomParameter yaadiLayingStatusesCP = CustomParameter.findByName(CustomParameter.class, "YAADI_LAYING_STATUSES", "");		
									if(yaadiLayingStatusesCP!=null && yaadiLayingStatusesCP.getValue()!=null && !yaadiLayingStatusesCP.getValue().isEmpty()) {
										List<Status> yaadiLayingStatuses = Status.findStatusContainedIn(yaadiLayingStatusesCP.getValue(), locale.toString());
										model.addAttribute("yaadiLayingStatuses", yaadiLayingStatuses);
										if(yaadiDetails!=null && yaadiDetails.getLayingStatus()!=null && yaadiDetails.getLayingStatus().getId()!=null) {
											model.addAttribute("yaadiLayingStatus", yaadiDetails.getLayingStatus());
										}
									}		
									/** populate whether to allow manually entering questions **/
									CustomParameter manuallyEnteringAllowedCP = CustomParameter.findByName(CustomParameter.class, "QIS_UNSTARRED_YAADI_MANUALLY_ENTERING_ALLOWED", "");
									if(manuallyEnteringAllowedCP!=null) {
										model.addAttribute("manuallyEnteringAllowed", manuallyEnteringAllowedCP.getValue());
									}
									retVal = "yaadi_details/"+ deviceType.getType().trim().toLowerCase() + "_yaadi";
								} else {
									logger.error("**** Error in Query of finding Yaadi Number ****");
									model.addAttribute("errorcode", "QUERY_ERROR");
								}								
							}										
						}
					}
				} catch(ELSException e) {
					if(e.getParameter("error")!=null && e.getParameter("error").equalsIgnoreCase("device.yaadiNumberingParameterNotSet")) {
						model.addAttribute("errorcode", "UNSTARRED_YAADI_NUMBERING_SESSION_PARAMETER_MISSING");						
					} else {
						model.addAttribute("error", e.getParameter("error"));	
					}						
				} catch(Exception e) {
					e.printStackTrace();
					model.addAttribute("error", "SOME_EXCEPTION_OCCURED");
				}
			}
		}
		
		return retVal;
	}
	
	@RequestMapping(value= "/checkduplicateyaadidetails", method = RequestMethod.GET)
	public @ResponseBody Boolean checkDuplicateYaadiNumber(HttpServletRequest request, ModelMap model, Locale locale) {
		Boolean isDuplicateYaadiNumber = false;
		
		String strHouseType = request.getParameter("houseType");
		String strSession = request.getParameter("sessionId");
		String strDeviceType = request.getParameter("deviceType");
		String strYaadiNumber = request.getParameter("yaadiNumber");
		
		if(strHouseType!=null && strSession!=null && strDeviceType!=null && strYaadiNumber!=null){
			if(!strHouseType.isEmpty() && !strSession.isEmpty() && !strDeviceType.isEmpty() && !strYaadiNumber.isEmpty()){
				try {
					HouseType houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
					if(houseType==null) {
						logger.error("**** HouseType Not Found ****");
						isDuplicateYaadiNumber = null;							
					} else {
						DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
						if(deviceType==null) {
							logger.error("**** DeviceType Not Found ****");
							isDuplicateYaadiNumber = null;							
						} else {
							Session session = Session.findById(Session.class, Long.parseLong(strSession));
							if(session==null) {
								logger.error("**** Session Not Found ****");
								isDuplicateYaadiNumber = null;
							} else {
								CustomParameter csptServer = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
								if(csptServer != null && csptServer.getValue() != null && csptServer.getValue().equals("TOMCAT")){
									strYaadiNumber = new String(strYaadiNumber.getBytes("ISO-8859-1"), "UTF-8");
									Integer yaadiNumber = Integer.parseInt(strYaadiNumber);
									YaadiDetails yd = YaadiDetails.find(deviceType, session, yaadiNumber, locale.toString());
									if(yd!=null && yd.getId()!=null) {
										isDuplicateYaadiNumber = true;
									}
								} else {
									isDuplicateYaadiNumber = null;
								}								
							}
						}
					}
				} catch(Exception e) {
					isDuplicateYaadiNumber = null;
				}
			}
		}
		
		return isDuplicateYaadiNumber;
	}		
	
	@RequestMapping("/custp/{cname}")
	public @ResponseBody MasterVO getCustomParameter(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value = "cname") String cname,Locale locale){
		MasterVO retVal = new MasterVO();
		try{
			CustomParameter csptParameter = CustomParameter.findByName(CustomParameter.class, cname, "");
			if(csptParameter != null){
				retVal.setId(csptParameter.getId());
				retVal.setValue(csptParameter.getValue());
				retVal.setName(csptParameter.getName());
			}
		}catch(Exception er){
			logger.error("error", er);
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/resetmemberpassword")
	public @ResponseBody String resetMemberPassword(HttpServletRequest request, Locale locale){
		String retVal = "FAILURE";
		try{
			String strMember = request.getParameter("memrole");
			List<Credential> creds = Credential.findAllCredentialsByRole(strMember);
			for(Credential cr : creds){
				String strPassword = Credential.generatePassword(Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
				String encodedPassword = securityService.getEncodedPassword(strPassword);
				cr.setPassword(encodedPassword);
				cr.merge();
			}
					 
			retVal = "SUCCESS";
		}catch(Exception e){
			logger.error("error", e);
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/decrypt")
	public @ResponseBody String decryptPassword(HttpServletRequest request, Locale locale){
		String strPassword = request.getParameter("upass");
		StringBuffer str = new StringBuffer();
		
		if(strPassword != null && !strPassword.isEmpty()){
			char[] chars = strPassword.toCharArray();
			for(char c : chars){
				char cc = (char)(((int)c) - Integer.parseInt(ApplicationConstants.DEFAULT_PASSWORD_LENGTH));
				str.append(cc);
			}
		}
		
		return str.toString();
	}
	
	@RequestMapping(value= "/devicetypesforhousetype", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> getDeviceTypesForHouseType(HttpServletRequest request, ModelMap model, Locale locale) throws ELSException {
		
		List<MasterVO> deviceTypeVOs = new ArrayList<MasterVO>();
		
		String houseType = request.getParameter("houseType");
		String deviceTypeType = request.getParameter("deviceType");
		
		if(houseType!=null && !houseType.isEmpty()) {
			HouseType selectedHouseType = HouseType.findByType(houseType, locale.toString());
			if(selectedHouseType==null) {
				selectedHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale.toString());
			}
			MessageResource bothHouseName = MessageResource.findByFieldName(MessageResource.class, "code", "generic.both_house_label", locale.toString());
			
			List<DeviceType> deviceTypes = null;
			if(deviceTypeType!=null && !deviceTypeType.isEmpty()) {
				deviceTypes = DeviceType.findDeviceTypesStartingWith(deviceTypeType, locale.toString());
			} else {
				//deviceTypes = DeviceType.findAll(DeviceType.class,"priority",ApplicationConstants.ASC, locale.toString());
				
				Credential cr = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), "");				
				List<UserGroup> userGroups = this.getCurrentUser().getUserGroups();
				String alldeviceTypeNameParam="";
				List<String> ugTypes = new ArrayList<String>();
				for(UserGroup ug : userGroups){
					String ugType = ug.getUserGroupType().getType();
					boolean isUGTAlreadyConsidered = false;
					if(!ugTypes.isEmpty()) {
						for(String ugt: ugTypes) {
							if(ugt.equals(ugType)) {
								isUGTAlreadyConsidered = true;
								break;
							}
						}
					}
					if(ugTypes.isEmpty() || !isUGTAlreadyConsidered) {
						List<UserGroup> activeUserGroupsForGivenUGType = UserGroup.findAllActive(cr, ug.getUserGroupType(), new Date(), locale.toString());
						if(activeUserGroupsForGivenUGType!=null && !activeUserGroupsForGivenUGType.isEmpty()) {
							ugTypes.add(ugType);
							for(UserGroup userGroup: activeUserGroupsForGivenUGType) {
								if(userGroup != null){
									/**** Authenticated User's usergroup and usergroupType ****/
									String userGroupType = userGroup.getUserGroupType().getType();			
									model.addAttribute("usergroup", userGroup.getId());
									model.addAttribute("usergroupType", userGroupType);
									
									Map<String, String> parameters = UserGroup.findParametersByUserGroup(userGroup);
									
									String houseTypeNameParam = parameters.get(ApplicationConstants.HOUSETYPE_KEY + "_" + locale.toString());
									if(houseTypeNameParam!=null && bothHouseName!=null && houseTypeNameParam.equals(bothHouseName.getValue())) {
										houseTypeNameParam = selectedHouseType.getName();
									}
									
									if(houseTypeNameParam!=null && selectedHouseType.getName().equals(houseTypeNameParam)) {
										String deviceTypeNameParam= parameters.get(ApplicationConstants.DEVICETYPE_KEY + "_" + locale.toString());
										if(deviceTypeNameParam != null && ! deviceTypeNameParam.equals("")) {
											//alldeviceTypeNameParam.concat(deviceTypeNameParam);
											if(!alldeviceTypeNameParam.isEmpty()
													&& !alldeviceTypeNameParam.endsWith("##")) {
												alldeviceTypeNameParam=alldeviceTypeNameParam+"##";
											}
											alldeviceTypeNameParam=alldeviceTypeNameParam+deviceTypeNameParam;
											//deviceTypes=DeviceType.findAllowedTypesForUser(deviceTypeNameParam, "##", locale);
										}
									}						
								}
							}
						}
						
					} else {
						continue;
					}
				}
				deviceTypes=DeviceType.findAllowedTypesForUser(alldeviceTypeNameParam, "##", locale.toString());
			}
			if(deviceTypes==null || deviceTypes.isEmpty()) {
				logger.error("/**** no devicetypes found ****/");
				throw new ELSException("ReferenceController/getDeviceTypesForHouseType", "no devicetypes found");
			}
			for(DeviceType deviceType: deviceTypes) {
				MasterVO deviceTypeVO = new MasterVO();
				deviceTypeVO.setId(deviceType.getId());
				deviceTypeVO.setType(deviceType.getType());
				deviceTypeVO.setName(deviceType.getName());
				if(houseType!=null && houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
					deviceTypeVO.setDisplayName(deviceType.getName_lowerhouse());
				} else if(houseType!=null && houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
					deviceTypeVO.setDisplayName(deviceType.getName_upperhouse());
				} else {
					deviceTypeVO.setDisplayName(deviceType.getName());
				}					
				deviceTypeVOs.add(deviceTypeVO);
			}
		} else {
			logger.error("/**** request parameter housetype is not set ****/");
			throw new ELSException("ReferenceController/getDeviceTypesForHouseType", "request parameter housetype is not set");
		}
		
		return deviceTypeVOs;
	}
	
	@RequestMapping(value = "/setactual", method = RequestMethod.GET)
	public @ResponseBody String setActual(HttpServletRequest request, Locale locale){
		String retVal = "FAILURE";
		
		try{
			
			Map<String, String[]> p = new HashMap<String, String[]>();
			p.put("locale", new String[]{locale.toString()});
			List report = Query.findReport("DUP_RECS", p);
			List<Reference> ids = new ArrayList<Reference>();
			
			int highestNumber = Integer.parseInt(request.getParameter("hNum"));
			for(int i = 0; i < report.size(); i++){
				if(i > 0){
					Object[] obj2 = (Object[])report.get(i);
					Object[] obj1 = (Object[])report.get(i - 1);
					
					long id2 = Long.parseLong(obj2[0].toString());
					long id1 = Long.parseLong(obj1[0].toString());
					
					int num2 = Integer.parseInt(obj2[1].toString());
					int num1 = Integer.parseInt(obj1[1].toString());
					
					if(num2 == num1){
						if(id2 > id1){
							Reference ref = new Reference(String.valueOf(id2), String.valueOf(num2));
							ids.add(ref);
						}
					}
				}
			}
			
			for(Reference r : ids){
				highestNumber++;
				Question q = Question.findById(Question.class, new Long(r.getId()));
				q.setNumber(highestNumber);
			}
			
			retVal = "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/logoutuser", method = RequestMethod.GET)
	public @ResponseBody String logoutUser(HttpServletRequest request, HttpServletResponse response, Locale locale){

		String retVal = "FAILURE";
		try {

			SessionInformation activeSession = null;
			String strUsername = request.getParameter("username");

			boolean found = false;
			
			for (Object principal : sessionRegistry.getAllPrincipals()) {
				
				for (SessionInformation session : sessionRegistry
						.getAllSessions(principal, false)) {
					if(principal instanceof AuthUser){
						String userName = ((AuthUser)principal).getUsername();
						if(strUsername != null && strUsername.equals(userName)){
							activeSession = session;
							break;
						}						
					}
					
				}
				
				if(found){
					break;
				}
			}
			if(activeSession != null){
				activeSession.expireNow();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retVal;
	}
	
	@RequestMapping(value = "/viewcurnum", method = RequestMethod.GET)
	public @ResponseBody Reference getCurNum(HttpServletRequest request, HttpServletResponse response, Locale locale){
		
		try{				
			
			String strDevId = request.getParameter("devid");
			String strSession = request.getParameter("session");
			String strClassName = request.getParameter("clsname");
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class, new Long(strDevId));
			Session session = Session.findById(Session.class, new Long(strSession));
			
			Class<?> cls = Class.forName("org.mkcl.els.domain." + strClassName);
			Object obj = cls.newInstance();
			Method method = null;
			
			
			Object[] parameters = new Object[2];
			
			parameters[0] = session;
			parameters[1] = deviceType;
			
			
			for(Method m : cls.getDeclaredMethods()){
				if(m.getName().equals("getCurNumber")){
					method = m;
					break;
				}
			}

			
			Object retVal = method.invoke(obj, parameters);
			
			return (org.mkcl.els.common.vo.Reference) retVal;
			
		}catch(Exception e){
			logger.error("error", e);
		}
			
		
		Reference ref = new Reference();
		ref.setName("NO_DATA");
		ref.setNumber("NO_DATA");
		return ref;
	}
	
	@RequestMapping(value = "/refreshcurnum/{num}/{housetype}/{devicetype}", method = RequestMethod.GET)
	public @ResponseBody String updateCurNum(@PathVariable("num") Integer num, 
			@PathVariable("housetype") String houseType, @PathVariable("devicetype") String strDevice,
			HttpServletRequest request, HttpServletResponse response, Locale locale){
		try{
			String forDate = request.getParameter("forDate");
			if(strDevice.equals(ApplicationConstants.STARRED_QUESTION) 
					|| strDevice.equals(ApplicationConstants.UNSTARRED_QUESTION)
					|| strDevice.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)
					|| strDevice.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				Question.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.MOTION_CALLING_ATTENTION)){
				Motion.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				Resolution.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)){
				StandaloneMotion.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY)
					|| strDevice.equals(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY)){
				CutMotion.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.ADJOURNMENT_MOTION)){
				AdjournmentMotion.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.PROPRIETY_POINT)){
				ProprietyPoint.updateCurNumber(num, houseType, strDevice);
			}else if(strDevice.equals(ApplicationConstants.SPECIAL_MENTION_NOTICE)){
				if(forDate!=null && !forDate.isEmpty()) {
					try {
						Date specialMentionNoticeDate = FormaterUtil.formatStringToDate(forDate, ApplicationConstants.DB_DATEFORMAT);
						SpecialMentionNotice.updateCurNumber(num, specialMentionNoticeDate, houseType, strDevice);					
					} catch(Exception e) {
						logger.error("Invalid Date Format for Special Mention Notice Date Parameter in ReferenceController.updateCurNum()");
						return "ERROR";
					}
				} else {
					logger.error("Invalid Date for Special Mention Notice Date Parameter in ReferenceController.updateCurNum()");
					return "ERROR";
				}
			}
			
			return "SUCCESS";
		}catch(Exception e){
			logger.error("error", e);
		}
		return "FAILURE";//Question.getDevicedCurrentNumber(deviceType);
	}
	
	@RequestMapping(value = "user/checkIfAllowedForMultiLogin", method = RequestMethod.GET)
	public @ResponseBody Boolean checkUserIfAllowedForMultiLogin(final HttpServletRequest request, final ModelMap map, final Locale locale) {
		Boolean isAllowedForMultiLogin = null;
		String credentialId = request.getParameter("credentialId");
		if(credentialId!=null && !credentialId.isEmpty() && !credentialId.equals("false")) {
			try {
				Credential credential = Credential.findById(Credential.class, Long.parseLong(credentialId));
				if(credential!=null) {
					isAllowedForMultiLogin = credential.isAllowedForMultiLogin();
				}
			} catch(Exception e) {
				e.printStackTrace();
				isAllowedForMultiLogin = null;				
			}			
		}
		return isAllowedForMultiLogin;
	}	
	
	@RequestMapping(value = "/requiredStatus", method = RequestMethod.GET)
	public @ResponseBody List<Status> loadStatus(HttpServletRequest request,
			HttpServletResponse response, 
			Locale locale){
		List<Status> statuses=new ArrayList<Status>();
		try{
			String strDeviceType = request.getParameter("deviceType");
			String strHouseType = request.getParameter("houseType");
			String strSessionType = request.getParameter("sessionType");
			String strSessionYear = request.getParameter("sessionYear");
			String strUsergroupType = request.getParameter("usergroupType");
			if(strDeviceType != null && !strDeviceType.isEmpty()
					&& strHouseType != null && !strHouseType.isEmpty()
					&& strSessionType != null && !strSessionType.isEmpty()
					&& strUsergroupType != null && !strUsergroupType.isEmpty()
					&& strSessionYear != null && !strSessionYear.isEmpty()){
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				Session currentSession = Session.findLatestSession(houseType);
				
				if(!session.equals(currentSession)){
					CustomParameter allowedStatus = CustomParameter.
							findByName(CustomParameter.class,
									deviceType.getDevice().toUpperCase()+"_GRID_STATUS_ALLOWED_"+ strUsergroupType.toUpperCase()+"_PREVIOUS_SESSION", "");
					if(allowedStatus != null){
						try {
							statuses = Status.findStatusContainedIn(allowedStatus.getValue(),locale.toString());
						} catch (ELSException e) {
							e.printStackTrace();
						}
					}else{
						CustomParameter defaultAllowedStatus = CustomParameter.
								findByName(CustomParameter.class,
										deviceType.getDevice().toUpperCase()+"_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
						if(defaultAllowedStatus != null){
							try {
								statuses = Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale.toString());
							} catch (ELSException e) {
								
								e.printStackTrace();
							}
						}
					}
				}else{
					CustomParameter allowedStatus=CustomParameter.
							findByName(CustomParameter.class,
									deviceType.getDevice().toUpperCase()+"_GRID_STATUS_ALLOWED_" + strUsergroupType.toUpperCase(), "");
					if(allowedStatus!=null){
						try {
							statuses=Status.findStatusContainedIn(allowedStatus.getValue(),locale.toString());
						} catch (ELSException e) {
							
							e.printStackTrace();
						}
					}else{
						CustomParameter defaultAllowedStatus=CustomParameter.
								findByName(CustomParameter.class,
										deviceType.getDevice().toUpperCase()+"_GRID_STATUS_ALLOWED_BY_DEFAULT", "");
						if(defaultAllowedStatus!=null){
							try {
								statuses=Status.findStatusContainedIn(defaultAllowedStatus.getValue(),locale.toString());
							} catch (ELSException e) {
									e.printStackTrace();
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return statuses;
	}
	
	@RequestMapping(value = "/yaadidetails/validateAndLoadQuestionDetailsForUnstarredYaadi", method = RequestMethod.GET)
	public @ResponseBody String[] validateAndLoadQuestionDetailsForUnstarredYaadi(HttpServletRequest request,
			HttpServletResponse response, 
			Locale locale){
		String[] questionDetails = new String[]{"", "number", "subject", "short_details", "content", "answer", "group_number", "yaadi_number", "number_english"};
		
		String questionNumber = request.getParameter("questionNumber");
		String sessionId = request.getParameter("sessionId");
		String deviceTypeId = request.getParameter("deviceTypeId");
		String houseDurationCategory = request.getParameter("houseDurationCategory");
		
		try {
			CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServerCP.getValue().equals("TOMCAT")){
				questionNumber = new String(questionNumber.getBytes("ISO-8859-1"),"UTF-8");			
			}
			questionNumber = FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(questionNumber).toString();
			
			Map<String, String[]> parameterMap = new HashMap<String, String[]>();
	    	parameterMap.put("locale", new String[]{locale.toString()});
	    	parameterMap.put("questionNumber", new String[]{questionNumber});
	    	parameterMap.put("sessionId", new String[]{sessionId});
	    	parameterMap.put("deviceTypeId", new String[]{deviceTypeId});
	    	parameterMap.put("houseDurationCategory", new String[]{houseDurationCategory});
	    	List resultList = Query.findReport("QIS_YAADI_MANUAL_NUMBER_VALIDATION_DETAILS", parameterMap);
	    	
	    	if(resultList==null || resultList.size()!=1) {
	    		questionDetails[0]="-1";
	    		return questionDetails;
	    	}
	    	
	    	Object[] questionDetailsData = (Object[]) resultList.get(0);
	    	
    		Boolean isQuestionParent = Boolean.valueOf(questionDetailsData[0].toString());
    		if(!isQuestionParent) {
    			questionDetails[0]="-2";
	    		return questionDetails;
    		}
    		
    		Boolean isQuestionAdmitted = Boolean.valueOf(questionDetailsData[1].toString());
    		if(!isQuestionAdmitted) {
    			questionDetails[0]="-3";
	    		return questionDetails;
    		}

    		Boolean isQuestionAnswered = Boolean.valueOf(questionDetailsData[2].toString());
    		if(!isQuestionAnswered) {
    			questionDetails[0]="-4";
	    		return questionDetails;
    		}
    		
    		Boolean isQuestionNotInExistingYaadi = Boolean.valueOf(questionDetailsData[3].toString());
    		if(!isQuestionNotInExistingYaadi) {
    			questionDetails[0]="-5";
    			questionDetails[7]=questionDetailsData[14].toString();
	    		return questionDetails;
    		}
    		
    		Boolean isQuestionMemberAlive = Boolean.valueOf(questionDetailsData[4].toString());
    		if(!isQuestionMemberAlive) {
    			questionDetails[0]="-6";
	    		return questionDetails;
    		}
    		
    		Boolean isQuestionMemberNotSuspended = Boolean.valueOf(questionDetailsData[5].toString());
    		if(!isQuestionMemberNotSuspended) {
    			questionDetails[0]="-7";
	    		return questionDetails;
    		}
    		
//    		Boolean isQuestionMemberActiveInHouse = Boolean.valueOf(questionDetailsData[6].toString());
//    		if(!isQuestionMemberActiveInHouse) {
//    			questionDetails[0]="-8";
//	    		return questionDetails;
//    		}
    		
    		Boolean isQuestionNotRemovedFromYaadi = Boolean.valueOf(questionDetailsData[7].toString());
    		if(!isQuestionNotRemovedFromYaadi) {
    			questionDetails[0]="-9";
	    		return questionDetails;
    		}
    		
    		questionDetails[0]=questionDetailsData[8].toString();
    		questionDetails[1]=questionDetailsData[9].toString();
    		questionDetails[2]=questionDetailsData[10].toString();
    		
    		Question question = Question.findById(Question.class, Long.parseLong(questionDetailsData[8].toString()));
    		questionDetails[3]=question.findShortDetailsTextForYaadi(false);
    		
    		questionDetails[4]=questionDetailsData[11].toString();
    		questionDetails[5]=questionDetailsData[12].toString();
    		questionDetails[6]=questionDetailsData[13].toString();
    		questionDetails[8]=questionDetailsData[15].toString();
		} catch(Exception e) {
			questionDetails[0]="0";
			e.printStackTrace();
		}
		
		return questionDetails;
	}
	
	@RequestMapping(value = "/formatNumbersInGivenText", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String formatNumbersInGivenText(HttpServletRequest request, Locale locale){
		
		String numberedText = request.getParameter("numberedText");
		
		try {
			if(numberedText==null || numberedText.isEmpty()) {
				return "";
			}
			
			CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(deploymentServerCP.getValue().equals("TOMCAT")){
				numberedText = new String(numberedText.getBytes("ISO-8859-1"),"UTF-8");		
			}
			
			return FormaterUtil.formatNumbersInGivenText(numberedText, locale.toString());
		} catch(Exception e) {
			e.printStackTrace();
			return numberedText;
		}
		
	}
	
	@RequestMapping(value="/{committeeMeetingId}/committeeMemberNames", method=RequestMethod.GET)
	public @ResponseBody List<String> completeProceeding(final @PathVariable("committeeMeetingId") Long committeeMeetingId, 
			final HttpServletRequest request, final ModelMap model, final Locale locale){
		List<String> memberNames = new ArrayList<String>(); 
		CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, committeeMeetingId);
		Committee committee = committeeMeeting.getCommittee();
		List<CommitteeMember> committeeMembers = committee.getMembers();
		for(CommitteeMember cm : committeeMembers){
			Member m = cm.getMember();
			memberNames.add(m.getFullname());
		}
		return memberNames;
	}
	
	@RequestMapping(value="/rosterFromCommitteeMeeting", method=RequestMethod.GET)
	public @ResponseBody Boolean getRosterByCommitteeMeetingAndLanguage(final HttpServletRequest request, final ModelMap model, final Locale locale){
		String strLanguage=request.getParameter("language");
		String strDay=request.getParameter("day");
		String strCommitteeMeeting = request.getParameter("committeeMeeting");
		try{
			if(strLanguage != null && !strLanguage.equals("")
					&& strDay != null && !strDay.equals("") 
					&& strCommitteeMeeting!=null && !strCommitteeMeeting.equals("")){
				String[] strLanguages = strLanguage.split(",");
				Roster roster = null;
				CommitteeMeeting committeeMeeting = CommitteeMeeting.findById(CommitteeMeeting.class, Long.parseLong(strCommitteeMeeting));
				for(String lang : strLanguages){
					Language language = Language.findById(Language.class, Long.parseLong(lang));
					try{
						roster = Roster.findRosterByCommitteeMeetingLanguageAndDay(committeeMeeting, language, Integer.parseInt(strDay), locale.toString());
						if(roster != null){
							List<Part> parts = Part.findPartsByRoster(roster);
							if(!parts.isEmpty()){
								break;
							}
						}
					}catch(Exception e){
						
					}
				}
				if(roster != null && roster.getPublish() != null && roster.getPublish().equals(true)){
					return true;
				}
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}
	
	@RequestMapping(value = "/yaadidetails/validateQuestionsFormattingForUnstarredYaadi", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String validateQuestionsFormattingForUnstarredYaadi(HttpServletRequest request,
			HttpServletResponse response,
			Locale locale){
		StringBuffer questionFormattingDetails = new StringBuffer();
		
		String selectedDeviceIds = request.getParameter("selectedDeviceIds");
		
		if(selectedDeviceIds!=null && !selectedDeviceIds.isEmpty()) {
			try {
				selectedDeviceIds = selectedDeviceIds.substring(0, selectedDeviceIds.length()-1);
				
				Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		    	parameterMap.put("locale", new String[]{locale.toString()});
		    	parameterMap.put("selectedDeviceIds", new String[]{selectedDeviceIds});
		    	@SuppressWarnings("unchecked")
				List<String> resultList = Query.findReport("QIS_YAADI_QUESTIONS_FORMATTING_VALIDATION_DETAILS", parameterMap, true);
		    	
		    	if(resultList==null || resultList.size()==0) {
		    		questionFormattingDetails.append("error_occurred");
		    	}
		    	
		    	for(String result: resultList) {
		    		if(result!=null && !result.equals("formatting_is_valid")) {
		    			questionFormattingDetails.append(result);
		    			questionFormattingDetails.append("<br/>");
		    		}
		    	}	    		    		
			} catch(Exception e) {
				questionFormattingDetails.append("error_occurred");
				e.printStackTrace();
			}
		}	
		
		if(questionFormattingDetails.toString().isEmpty()) {
			questionFormattingDetails.append("formatting_is_valid");
		}
		
		return questionFormattingDetails.toString();
	}

	@RequestMapping(value = "/yaadi_details/bulk_yaadi_selection", method = RequestMethod.GET)
	public @ResponseBody List<Object[]> loadBulkYaadiDetails(HttpServletRequest request,
			HttpServletResponse response, 
			Locale locale){
		List<Object[]> yaadiList=new ArrayList<Object[]>();
		
		String strYaadiNumbers = request.getParameter("yaadiNumbers");
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
		
		if(strYaadiNumbers!=null && !strYaadiNumbers.isEmpty()
				&& strHouseType!=null && !strHouseType.isEmpty()
				&& strSessionType!=null && !strSessionType.isEmpty()
				&& strSessionYear!=null && !strSessionYear.isEmpty()
				&& strDeviceType!=null && !strDeviceType.isEmpty()) {
			try {
				CustomParameter deploymentServerCP = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(deploymentServerCP.getValue().equals("TOMCAT")){
					strYaadiNumbers = new String(strYaadiNumbers.getBytes("ISO-8859-1"),"UTF-8");		
				}
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				if(houseType==null) {
					houseType = HouseType.findByName(strHouseType, locale.toString());
				}
				if(houseType==null) {
					houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseType));
				}										
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Integer sessionYear = Integer.parseInt(strSessionYear);
				if(houseType==null || sessionType==null) {
					logger.error("**** HouseType or SessionType Not Found ****");
				} else {
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
					if(session==null) {								
						logger.error("**** Session Not Found ****");
					} else {
						StringBuffer yaadiNumbers = new StringBuffer("");
						for(String yn: strYaadiNumbers.split(",")) {
							if(!yn.isEmpty() && yn.split("-").length>1) {
								int yaadiRangeFirstElement = Integer.parseInt(yn.split("-")[0].trim());
								int yaadiRangeLastElement = Integer.parseInt(yn.split("-")[1].trim());
								for(Integer i=yaadiRangeFirstElement; i<=yaadiRangeLastElement; i++) {
									yaadiNumbers.append(i.toString());
									yaadiNumbers.append(",");
								}
							} else {
								if(!yn.isEmpty()) {									
//									yaadiNumbers.append(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(yn.trim()));
									yaadiNumbers.append(Integer.parseInt(yn.trim()));
									yaadiNumbers.append(",");
								}								
							}
						}
						yaadiNumbers.deleteCharAt(yaadiNumbers.length()-1);
						Map<String, String[]> qparams = new HashMap<String, String[]>();
						qparams.put("locale", new String[]{locale.toString()});
						qparams.put("deviceTypeId", new String[]{strDeviceType});
						qparams.put("sessionId", new String[]{session.getId().toString()});
						qparams.put("yaadiNumbers", new String[]{yaadiNumbers.toString()});
						List<Object[]> result = Query.findReport("YAADI_DETAILS_FOR_BULK_UPDATE", qparams, true);
						System.out.println(result.size());
						//TODO:
					}					
				}				
				
			} catch(Exception e) {
				logger.error("Exception occured in fetching yaadi details");
			}
		}
		
		return yaadiList;
	}
	
	@RequestMapping(value="/proceedingCitation", method=RequestMethod.GET)
	public @ResponseBody List<TemplateVO> getProceedingAutofillData(final HttpServletRequest request, 
			final ModelMap model, 
			final Locale locale){
		List<TemplateVO> templateVOs = new ArrayList<TemplateVO>();
		List<ProceedingCitation> proceedingCitations = ProceedingCitation.
				findAll(ProceedingCitation.class, "title", "asc", locale.toString());
		for(ProceedingCitation proceedingCitation : proceedingCitations){
			TemplateVO templateVO = new TemplateVO();
			templateVO.setTitle(proceedingCitation.getTitle());
			templateVO.setContent(proceedingCitation.getContent());
			templateVOs.add(templateVO);
		}
		return templateVOs;
	
	}
	
	@RequestMapping(value="/rosterPublished", method=RequestMethod.GET)
	public @ResponseBody Boolean getPublishedRoster(final HttpServletRequest request, 
			final ModelMap model, 
			final Locale locale){
		Boolean isRosterPublished = false;
		String strPartId = request.getParameter("partId");
		if(strPartId != null && !strPartId.isEmpty()){
			Part part = Part.findById(Part.class, Long.parseLong(strPartId));
			Roster roster = Roster.findByPart(part,locale);
			if(roster != null){
				if(roster.getPublish()){
					isRosterPublished = true;
				}
			}
		}
		return isRosterPublished;
	
	}
	
	@RequestMapping(value="/proceedingHeader", method=RequestMethod.GET)
	public @ResponseBody MasterVO getProceedingHeaderDetails(final HttpServletRequest request, 
			final ModelMap model, 
			final Locale locale){
		String strPart = request.getParameter("partId");
		MasterVO masterVO = new MasterVO();
		if(strPart != null && !strPart.isEmpty()){
			Part part = Part.findById(Part.class, Long.parseLong(strPart));
			Proceeding proceeding  = part.getProceeding();
			Slot slot = proceeding.getSlot();
			masterVO.setName(slot.getName());
			String currentSlotStartDate = FormaterUtil.formatDateToString(slot.getStartTime(), "dd-MM-yyyy", locale.toString());
			String currentSlotStartTime = FormaterUtil.formatDateToString(slot.getStartTime(), "HH:mm", locale.toString());
			masterVO.setType(currentSlotStartTime);
			masterVO.setValue(currentSlotStartDate);
			List<User> users=Slot.findDifferentLanguageUsersBySlot(slot);
			String languageReporter="";
			for(int i=0;i<users.size();i++){
				languageReporter=languageReporter+users.get(i).getFirstName();
				if(i+1<users.size()){
					languageReporter=languageReporter+"/";
				}
			}
			masterVO.setDisplayName(languageReporter);
			Slot previousSlot = Slot.findPreviousSlot(slot);
			if(previousSlot != null){
				Reporter reporter = previousSlot.getReporter();
				User user = reporter.getUser();
				masterVO.setFormattedOrder(user.getTitle()+" "+user.getLastName());
			}
		}
		return masterVO;
		
	}
	
//	@RequestMapping(value = "/field_select_query_for_report", method = RequestMethod.GET)
//	public @ResponseBody String loadFieldsSelectQueryForReport(HttpServletRequest request,
//			HttpServletResponse response, 
//			Locale locale){
//		StringBuffer fieldsSelectQueryBuffer = new StringBuffer("");
//		
//		try {			
//			@SuppressWarnings("unchecked")
//			Map<String, String[]> requestMap = request.getParameterMap();			
//			String reportFieldsCount = request.getParameter("reportFieldsCount");
//			for(int i=1; i<=Integer.parseInt(reportFieldsCount); i++) {
//				Map<String, String[]> parameterMap = new HashMap<String, String[]>();
//				parameterMap.putAll(requestMap);
//				parameterMap.put("reportField", new String[] {request.getParameter("reportField_"+i)});
//				@SuppressWarnings("rawtypes")
//				List reportSelect = Query.findReport(request.getParameter("reportSelectQuery"), parameterMap);
//				fieldsSelectQueryBuffer.append(reportSelect.get(0).toString().replaceAll("_colon_", ":").trim());
//				fieldsSelectQueryBuffer.append(",\n");				
//				parameterMap = null;
//			}			
//		} catch(Exception e) {
//			logger.error("Exception occured in loading field_select_query_for_report");
//		}
//		if(fieldsSelectQueryBuffer.length()>0) {
//			fieldsSelectQueryBuffer.deleteCharAt(fieldsSelectQueryBuffer.length()-1);
//			fieldsSelectQueryBuffer.deleteCharAt(fieldsSelectQueryBuffer.length()-1);
//		}
//		return fieldsSelectQueryBuffer.toString();
//	}
//	
//	@RequestMapping(value = "/field_header_select_query_for_report", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
//	public @ResponseBody String loadFieldHeadersSelectQueryForReport(HttpServletRequest request,
//			HttpServletResponse response, 
//			Locale locale){
//		StringBuffer fieldHeadersSelectQueryBuffer = new StringBuffer("");
//		
//		try {			
//			@SuppressWarnings("unchecked")
//			Map<String, String[]> requestMap = request.getParameterMap();			
//			String reportFieldsCount = request.getParameter("reportFieldsCount");
//			for(int i=1; i<=Integer.parseInt(reportFieldsCount); i++) {
//				Map<String, String[]> parameterMap = new HashMap<String, String[]>();
//				parameterMap.putAll(requestMap);
//				parameterMap.put("reportField", new String[] {request.getParameter("reportField_"+i)});
//				@SuppressWarnings("rawtypes")
//				List reportHeaderSelect = Query.findReport(request.getParameter("reportHeaderSelectQuery"), parameterMap);
//				fieldHeadersSelectQueryBuffer.append(reportHeaderSelect.get(0).toString().replaceAll("_colon_", ":").trim());
//				fieldHeadersSelectQueryBuffer.append(",\n");				
//				parameterMap = null;
//			}			
//		} catch(Exception e) {
//			logger.error("Exception occured in loading field_headers_select_query_for_report");
//		}
//		if(fieldHeadersSelectQueryBuffer.length()>0) {
//			fieldHeadersSelectQueryBuffer.deleteCharAt(fieldHeadersSelectQueryBuffer.length()-1);
//			fieldHeadersSelectQueryBuffer.deleteCharAt(fieldHeadersSelectQueryBuffer.length()-1);
//		}
//		return fieldHeadersSelectQueryBuffer.toString();
//	}
	
	
	
	@RequestMapping(value="/{id}/referencedresolutiontext", method=RequestMethod.GET)
	public @ResponseBody MasterVO getReferencedResolutionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		MasterVO referencedQuestionsVO = new MasterVO();
		try{
			Resolution resolution = Resolution.findById(Resolution.class, id);
			if(resolution != null){
				ReferencedEntity referencedEntity = resolution.getReferencedResolution();
				Resolution refResolution = (Resolution) referencedEntity.getDevice();
				if(refResolution != null){
					referencedQuestionsVO.setId(refResolution.getId());
					referencedQuestionsVO.setName(FormaterUtil.formatNumberNoGrouping(refResolution.getNumber(), locale.toString()));
					if(refResolution.getRevisedNoticeContent()!= null && !refResolution.getRevisedNoticeContent().isEmpty()){
						referencedQuestionsVO.setValue(refResolution.getRevisedNoticeContent());
					}else{
						referencedQuestionsVO.setValue(refResolution.getNoticeContent());
					}
				}	
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		return referencedQuestionsVO;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/pendingtasksdevicescmois", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTasksDevicesCMOIS(HttpServletRequest request, Locale locale){
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strStatus = request.getParameter("status");
		String strWfSubType = request.getParameter("wfSubType");
		String strGrid = request.getParameter("grid");
		String strSubdepartment = request.getParameter("subdepartment");
		String strAnsweringDate = request.getParameter("answeringDate");
		
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> vos = new ArrayList<MasterVO>();
				
		try {
			String server=csptDeployment.getValue();
			SessionType sessionType = null;
			HouseType houseType = null;
			Integer year = null;
			Session session = null;					
			DeviceType deviceType = null;
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			List data = null;
			
			if(strGrid.equals("workflow")){
				if(csptDeployment!=null){
					if(server.equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strStatus = new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
						strWfSubType = new String(strWfSubType.getBytes("ISO-8859-1"),"UTF-8");
					}
				}
				
				sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				houseType = HouseType.findByName(strHouseType, locale.toString());
				year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("workflowSubType", new String[]{strWfSubType});
				parameters.put("assignee", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("answeringDate", new String[]{strAnsweringDate});
				
				data = Query.findReport("CMOIS_STATUS_REPORT_DEVICES_WF", parameters);
				
			}else if(strGrid.equals("device")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				houseType = HouseType.findByType(strHouseType, locale.toString());
				year = new Integer(Integer.parseInt(strSessionYear));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("subdepartment", new String[]{strSubdepartment});
				parameters.put("locale", new String[]{locale.toString()});
				data = Query.findReport("CMOIS_STATUS_REPORT_DEVICES_DV", parameters);
			}
			
			if(data != null){
				for(Object o : data){
					Object[] objx = (Object[]) o;
					MasterVO vo = new MasterVO();
					if(objx[0] != null){
						vo.setValue(objx[0].toString());
						vos.add(vo);
					}
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vos;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/pendingtasksdevicesamois", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTasksDevicesAMOIS(HttpServletRequest request, Locale locale){
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strStatus = request.getParameter("status");
		String strWfSubType = request.getParameter("wfSubType");
		String strGrid = request.getParameter("grid");
		String strSubdepartment = request.getParameter("subdepartment");
		String strAdjourningDate = request.getParameter("adjourningDate");
		
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> vos = new ArrayList<MasterVO>();
				
		try {
			String server=csptDeployment.getValue();
			SessionType sessionType = null;
			HouseType houseType = null;
			Integer year = null;
			Session session = null;					
			DeviceType deviceType = null;
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			List data = null;
			
			if(strGrid.equals("workflow")){
				if(csptDeployment!=null){
					if(server.equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strStatus = new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
						strWfSubType = new String(strWfSubType.getBytes("ISO-8859-1"),"UTF-8");
					}
				}
				
				sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				houseType = HouseType.findByName(strHouseType, locale.toString());
				year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("workflowSubType", new String[]{strWfSubType});
				parameters.put("assignee", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("adjourningDate", new String[]{strAdjourningDate});
				
				data = Query.findReport("AMOIS_STATUS_REPORT_DEVICES_WF", parameters);
				
			}else if(strGrid.equals("device")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				houseType = HouseType.findByType(strHouseType, locale.toString());
				year = new Integer(Integer.parseInt(strSessionYear));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("subdepartment", new String[]{strSubdepartment});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("adjourningDate", new String[]{strAdjourningDate});
				
				data = Query.findReport("AMOIS_STATUS_REPORT_DEVICES_DV", parameters);
			}
			
			if(data != null){
				for(Object o : data){
					Object[] objx = (Object[]) o;
					MasterVO vo = new MasterVO();
					if(objx[0] != null){
						vo.setValue(objx[0].toString());
						vo.setNumber(Integer.parseInt(objx[2].toString()));
						vos.add(vo);
					}
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(vos!=null && !vos.isEmpty() && vos.size()>1) {
			vos = MasterVO.sort(vos, "number", ApplicationConstants.ASC); //order by number
		}
		
		return vos;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/pendingtasksdevicessmis", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTasksDevicesSMIS(HttpServletRequest request, Locale locale){
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strStatus = request.getParameter("status");
		String strWfSubType = request.getParameter("wfSubType");
		String strGrid = request.getParameter("grid");
		String strSubdepartment = request.getParameter("subdepartment");
		String strSpecialMentionNoticeDate = request.getParameter("specialMentionNoticeDate");
		
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> vos = new ArrayList<MasterVO>();
				
		try {
			String server=csptDeployment.getValue();
			SessionType sessionType = null;
			HouseType houseType = null;
			Integer year = null;
			Session session = null;					
			DeviceType deviceType = null;
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			List data = null;
			
			if(strGrid.equals("workflow")){
				if(csptDeployment!=null){
					if(server.equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strStatus = new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
						strWfSubType = new String(strWfSubType.getBytes("ISO-8859-1"),"UTF-8");
					}
				}
				
				sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				houseType = HouseType.findByName(strHouseType, locale.toString());
				year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("workflowSubType", new String[]{strWfSubType});
				parameters.put("assignee", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("specialMentionNoticeDate", new String[]{strSpecialMentionNoticeDate});
				
				data = Query.findReport("SMIS_STATUS_REPORT_DEVICES_WF", parameters);
				
			}else if(strGrid.equals("device")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				houseType = HouseType.findByType(strHouseType, locale.toString());
				year = new Integer(Integer.parseInt(strSessionYear));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("subdepartment", new String[]{strSubdepartment});
				parameters.put("locale", new String[]{locale.toString()});
				parameters.put("specialMentionNoticeDate", new String[]{strSpecialMentionNoticeDate});
				
				data = Query.findReport("SMIS_STATUS_REPORT_DEVICES_DV", parameters);
			}
			
			if(data != null){
				for(Object o : data){
					Object[] objx = (Object[]) o;
					MasterVO vo = new MasterVO();
					if(objx[0] != null){
						vo.setValue(objx[0].toString());
						vo.setNumber(Integer.parseInt(objx[2].toString()));
						vos.add(vo);
					}
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(vos!=null && !vos.isEmpty() && vos.size()>1) {
			vos = MasterVO.sort(vos, "number", ApplicationConstants.ASC); //order by number
		}
		
		return vos;
	}
	
	@RequestMapping(value="/answering_dates_for_member_suchi_view")
	public @ResponseBody List<MasterVO> loadAnsweringDatesForMemberSuchiView(final HttpServletRequest request,
			final Locale locale){
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		String strHouseType = request.getParameter("houseType");
		String strYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strlocale = locale.toString();
		if(strHouseType != null && !strHouseType.isEmpty()
			&& strSessionType != null && !strSessionType.isEmpty()
			&& strYear != null && !strYear.isEmpty()){
			HouseType houseType = HouseType.findByType(strHouseType, strlocale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			try {
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strYear));				
				if(session != null){
					Map<String, String[]> queryParameters = new HashMap<String, String[]>();
					queryParameters.put("locale", new String[]{locale.toString()});
					queryParameters.put("sessionId", new String[]{session.getId().toString()});					
					@SuppressWarnings("unchecked")
					List<QuestionDates> answeringDates = Query.findResultListOfGivenClass("SESSION_ANSWERING_DATES_FOR_MEMBER_SUCHI_VIEW", queryParameters, QuestionDates.class);
					if(answeringDates!=null && !answeringDates.isEmpty()) {
						for (QuestionDates qd : answeringDates) {
							MasterVO masterVO = new MasterVO();
							masterVO.setId(qd.getId());
							masterVO.setValue(qd.getAnsweringDate().toString());
							masterVO.setName(FormaterUtil.formatDateToString(qd.getAnsweringDate(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
							masterVOs.add(masterVO);							
						}
					}					
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return masterVOs;
	}
	
	@RequestMapping(value = "check_if_suchi_published_on_selected_answering_date", method = RequestMethod.GET)
	public @ResponseBody Boolean checkIfSuchiPublishedOnSelectedAnsweringDate(final HttpServletRequest request, final ModelMap map, final Locale locale) {
		Boolean isSuchiPublishedOnSelectedAnsweringDate = null;
		String answeringDateId = request.getParameter("answeringDate");
		if(answeringDateId!=null && !answeringDateId.isEmpty()) {
			QuestionDates answeringDateForSuchi = QuestionDates.findById(QuestionDates.class, Long.parseLong(answeringDateId));
			if(answeringDateForSuchi!=null) {
				isSuchiPublishedOnSelectedAnsweringDate = answeringDateForSuchi.getSuchiPublished();
				if(isSuchiPublishedOnSelectedAnsweringDate==null) {
					isSuchiPublishedOnSelectedAnsweringDate = false;
				}
			}			
		}
		return isSuchiPublishedOnSelectedAnsweringDate;
	}
	
	
	@RequestMapping(value="/documentlinks", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getSessionDocumentLinks(final HttpServletRequest request, final Locale locale){
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		try{
			String strSessionType = request.getParameter("sessiontype");
			String strHouseType = request.getParameter("housetype");
			String strSessionYear = request.getParameter("sessionyear");
			Integer latestYear = new GregorianCalendar().get(Calendar.YEAR);
			if(strSessionType != null && !strSessionType.isEmpty()
				&& strHouseType != null && !strHouseType.isEmpty()
				&& strSessionYear != null && !strSessionYear.isEmpty()){
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				HouseType houseType =  HouseType.findByType(strHouseType, locale.toString());
				Integer sessionYear = Integer.parseInt(strSessionYear);
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				if(session != null){
					List<DocumentLink> documentLinks = DocumentLink.findAllByFieldName(DocumentLink.class, "session", session, "sessionDate", ApplicationConstants.ASC, locale.toString());
					for(DocumentLink dl : documentLinks){
						if(!dl.getTitle().equalsIgnoreCase("Rotation Order")){
							MasterVO masterVO = new MasterVO();
							masterVO.setId(dl.getId());
							masterVO.setName(dl.getLocalizedTitle());
							masterVO.setType(dl.getTitle());
							masterVO.setDisplayName(dl.getUrl());
							masterVO.setSessionDate(
									FormaterUtil.formatStringToDate(FormaterUtil.formatDateToString(dl.getSessionDate(), ApplicationConstants.DB_DATEFORMAT), ApplicationConstants.DB_DATEFORMAT));
							masterVOs.add(masterVO);
						}
					}
				}
				
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		return masterVOs;
	}
	
	@RequestMapping(value="/question/{id}/is_clubbedquestion_pendingwith_updatedquestiontext", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String getPendingClubbedQuestions(final HttpServletRequest request, final @PathVariable("id") Long questionId, final Locale locale){
		StringBuffer pendingClubbedQuestionNumbers = new StringBuffer("");
		
		Question question = Question.findById(Question.class, questionId);
		if(question!=null 
				&& question.getClubbedEntities()!=null 
				&& !question.getClubbedEntities().isEmpty()) {
			
			Integer finalStatusPriority = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale.toString()).getPriority();
			
			int clubbedCount = 1;
			for(ClubbedEntity ce: question.getClubbedEntities()) {
				Question clubbedQuestion = ce.getQuestion();
				
				if(!clubbedQuestion.getInternalStatus().getType().endsWith(ApplicationConstants.STATUS_SYSTEM_CLUBBED)
						&& clubbedQuestion.getRecommendationStatus().getPriority()<finalStatusPriority) {
					
					/** fetch parent's latest question text **/
					String latestParentQuestionText = question.getRevisedQuestionText();
					if(latestParentQuestionText==null || latestParentQuestionText.isEmpty()) {
						latestParentQuestionText = question.getQuestionText();
					}
					/** fetch child's latest question text **/
					String latestClubbedQuestionText = clubbedQuestion.getRevisedQuestionText();
					if(latestClubbedQuestionText==null || latestClubbedQuestionText.isEmpty()) {
						latestClubbedQuestionText = clubbedQuestion.getQuestionText();
					}
					/** add the pending clubbed question only if there is difference between the question texts **/
					if(!latestClubbedQuestionText.equals(latestParentQuestionText)) {
						pendingClubbedQuestionNumbers.append(FormaterUtil.formatNumberNoGrouping(clubbedQuestion.getNumber(), locale.toString()));
						
						if(clubbedCount < question.getClubbedEntities().size()) {
							pendingClubbedQuestionNumbers.append(", ");
						}
						clubbedCount++;
					}													
				}
			}
		}
		
		return pendingClubbedQuestionNumbers.toString();
	}
	
	@RequestMapping(value="/servertime", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String getServerTime(final HttpServletRequest request, final Locale locale){		
		return FormaterUtil.formatDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
	}
	
	@RequestMapping(value = "/newpendingmotionadvancecopy", method = RequestMethod.GET)
	public @ResponseBody Long getPendingMotionAdvanceCopy(HttpServletRequest request, Locale locale){
		Long retVal = (long) 0;
		try{
			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strHouseType = request.getParameter("houseType");
			String strAssignee = request.getParameter("assignee");
			if(strSessionYear != null && !strSessionYear.isEmpty()
				&& strSessionType != null && !strSessionType.isEmpty()
				&& strHouseType != null && !strHouseType.isEmpty()
				&& strAssignee != null && !strAssignee.isEmpty()){
				CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptDeployment!=null){
					if(csptDeployment.getValue().equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
					}
					SessionType sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
					HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
					Integer year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
					Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
					Credential credential = Credential.findByFieldName(Credential.class, "username", strAssignee, null);
					UserGroup userGroup = UserGroup.findActive(credential, new Date(), locale.toString());
					UserGroupType userGroupType = userGroup.getUserGroupType();
					Map<String, String[]> parameterMap = new HashMap<String, String[]>();
					parameterMap.put("locale", new String[]{locale.toString()});
					parameterMap.put("sessionId", new String[]{session.getId().toString()});
					if(userGroupType.getType().equals(ApplicationConstants.DEPARTMENT)){
						parameterMap.put("actor", new String[]{""});
					}else{
						parameterMap.put("actor", new String[]{credential.getUsername()});
					}
					if(userGroup != null){
						Map<String, String> usergroupParameters = userGroup.getParameters();
						String strSubdepartment = usergroupParameters.get(ApplicationConstants.SUBDEPARTMENT_KEY + "_"+locale.toString());
						String subdepartmentIds = new String();
						if(strSubdepartment != null && !strSubdepartment.isEmpty()){
							String subdepartments[] = strSubdepartment.split("##");
							for(int i = 0;i<subdepartments.length;i++){
								SubDepartment subdepartment = SubDepartment.findByName(SubDepartment.class, subdepartments[i], locale.toString());
								if(subdepartment != null){
									subdepartmentIds+=subdepartment.getId();
									if(i+1<subdepartments.length){
										subdepartmentIds+=",";
									}
								}
							}
							parameterMap.put("subdepartments", new String[]{subdepartmentIds});
						}
					}
					List<BigInteger> report = Query.findReport("MOIS_PENDING_ADVANCE_COPY", parameterMap, true);
					if(report != null && !report.isEmpty()){
						return (long) report.size();
					}
				}
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@RequestMapping(value = "/actorformotionadvancecopy", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> findActorsForMotionAdvanceCopy(HttpServletRequest request, Locale locale){
		List<MasterVO> actors = new ArrayList<MasterVO>();
		try{
			String strMotionId = request.getParameter("motionId");
			if(strMotionId != null && !strMotionId.isEmpty()){
				Motion motion = Motion.findById(Motion.class, Long.parseLong(strMotionId));
				Map<String, String[]> parameterMap = new HashMap<String, String[]>();
				parameterMap.put("subdepartment", new String[]{"%"+motion.getSubDepartment().getName()+"%"});
				parameterMap.put("ministry", new String[]{"%"+motion.getMinistry().getName()+"%"});
				parameterMap.put("usergrouptype", new String[]{ApplicationConstants.DEPARTMENT_DESKOFFICER});
				parameterMap.put("locale", new String[]{locale.toString()});
				List report = Query.findReport("MOIS_FIND_ELIGIBLE_ACTORS", parameterMap);
				if(report != null){
					for(Object o : report){
						MasterVO masterVO = new MasterVO();
						Object[] objx = (Object[]) o;
						masterVO.setValue(objx[0].toString());
						masterVO.setName(objx[1].toString());
						actors.add(masterVO);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return actors;
	}
	
	
	@RequestMapping(value = "/isValidForNewRis", method = RequestMethod.GET)
	public @ResponseBody Boolean isValidSlotForNewRIS(HttpServletRequest request, Locale locale){
		Boolean valid = false;
		String strProceedingId = request.getParameter("proceedingId");
		if(strProceedingId != null && !strProceedingId.isEmpty()){
			Proceeding proceeding = Proceeding.findById(Proceeding.class, Long.parseLong(strProceedingId));
			Slot slot = proceeding.getSlot();
			CustomParameter newRISStartDateParameter = CustomParameter.findByName(CustomParameter.class, "NEW_RIS_START_DATE", "");
			if(newRISStartDateParameter != null){
				String strNewRisStartDate = newRISStartDateParameter.getValue();
				Date newRisStartDate = FormaterUtil.formatStringToDate(strNewRisStartDate, ApplicationConstants.SERVER_DATEFORMAT);
				if(slot.getStartTime().after(newRisStartDate)){
					valid = true;
				}
			}
			Roster roster=slot.getRoster();
			if(roster.getCommitteeMeeting()!= null){
				valid = false;
			}
		}

		return valid;
	}
	
	
	@RequestMapping(value = "/isValidToOpen", method = RequestMethod.GET)
	public @ResponseBody Boolean isisValidToOpen(HttpServletRequest request, Locale locale){
		Boolean valid = true;
		String strProceedingId = request.getParameter("proceedingId");
		if(strProceedingId != null && !strProceedingId.isEmpty()){
			Proceeding proceeding = Proceeding.findById(Proceeding.class, Long.parseLong(strProceedingId));
			Slot slot = proceeding.getSlot();
			
				Date currentDate = FormaterUtil.getCurrentDate();
				if(slot.getStartTime().after(currentDate)){
					valid = false;
			
			}
		}

		return valid;
	}
	
	@RequestMapping(value = "/loadVisibilityFlagsForMemberQuestionsView", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> populateVisibilityFlagsForMemberQuestionsView(HttpServletRequest request, Locale locale){
		List<MasterVO> visibilityFlags = new ArrayList<MasterVO>();
		try {
			String strHouseType = request.getParameter("houseType");
			String strYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strlocale = locale.toString();
			
			if(strHouseType != null && !strHouseType.isEmpty()
				&& strSessionType != null && !strSessionType.isEmpty()
				&& strYear != null && !strYear.isEmpty()){
				
				HouseType houseType = HouseType.findByType(strHouseType, strlocale);
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strYear));
				
				/****Member's Questions Views Visibility Parameters****/
				Boolean sessionEndDateFlag = false;
				Date sessionEndDate = session.getEndDate();
				if(sessionEndDate!=null) {
					String sessionEndDateTimeStr = FormaterUtil.formatDateToString(sessionEndDate, ApplicationConstants.DB_DATEFORMAT);
					CustomParameter visibilityStartTimeCP = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_START_TIME_FOR_MEMBER_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
					if(visibilityStartTimeCP!=null && visibilityStartTimeCP.getValue()!=null) {
						sessionEndDateTimeStr = sessionEndDateTimeStr + " " + visibilityStartTimeCP.getValue();
						Date sessionEndDateTime = FormaterUtil.formatStringToDate(sessionEndDateTimeStr, ApplicationConstants.DB_DATETIME_FORMAT);
						if(new Date().compareTo(sessionEndDateTime)>=0) {
							sessionEndDateFlag = true;
						}
					}
				}
				
				Boolean statusFlag = false;		
				CustomParameter statusFlagForMemberQuestionsView = CustomParameter.findByName(CustomParameter.class, "STATUS_FLAG_FOR_MEMBER_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(statusFlagForMemberQuestionsView!=null && statusFlagForMemberQuestionsView.getValue()!=null
						&& statusFlagForMemberQuestionsView.getValue().equals("visible")) {
					statusFlag = true; 
				}
				if(statusFlag.equals(true) && sessionEndDateFlag.equals(true)) {
					MasterVO visibilityFlagForStatus = new MasterVO();
					visibilityFlagForStatus.setName("member_questions_view_status_flag");
					visibilityFlagForStatus.setValue("status_visible");
					visibilityFlags.add(visibilityFlagForStatus);
				}
				
				Boolean visibilityFlagForAdmitted = false;
				CustomParameter visibilityFlagForMemberAdmittedQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_ADMITTED_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(visibilityFlagForMemberAdmittedQuestionsView!=null && visibilityFlagForMemberAdmittedQuestionsView.getValue()!=null
						&& visibilityFlagForMemberAdmittedQuestionsView.getValue().equals("visible")) {
					visibilityFlagForAdmitted = true; 
				}
				if(visibilityFlagForAdmitted.equals(true)/* && sessionEndDateFlag.equals(true)*/) {
					MasterVO visibilityFlagForAdmittedQuestions = new MasterVO();
					visibilityFlagForAdmittedQuestions.setName("member_admitted_questions_view_flag");
					visibilityFlagForAdmittedQuestions.setValue("admitted_visible");
					visibilityFlags.add(visibilityFlagForAdmittedQuestions);
				}
				
				Boolean visibilityFlagForRejected = false;
				CustomParameter visibilityFlagForMemberRejectedQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_REJECTED_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(visibilityFlagForMemberRejectedQuestionsView!=null && visibilityFlagForMemberRejectedQuestionsView.getValue()!=null
						&& visibilityFlagForMemberRejectedQuestionsView.getValue().equals("visible")) {
					visibilityFlagForRejected = true; 
				}
				if(visibilityFlagForRejected.equals(true) && sessionEndDateFlag.equals(true)) {
					MasterVO visibilityFlagForRejectedQuestions = new MasterVO();
					visibilityFlagForRejectedQuestions.setName("member_rejected_questions_view_flag");
					visibilityFlagForRejectedQuestions.setValue("rejected_visible");
					visibilityFlags.add(visibilityFlagForRejectedQuestions);
				}
				
				Boolean visibilityFlagForUnstarred = false;
				CustomParameter visibilityFlagForMemberUnstarredQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_UNSTARRED_QUESTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(visibilityFlagForMemberUnstarredQuestionsView!=null && visibilityFlagForMemberUnstarredQuestionsView.getValue()!=null
						&& visibilityFlagForMemberUnstarredQuestionsView.getValue().equals("visible")) {
					visibilityFlagForUnstarred = true; 
				}
				if(visibilityFlagForUnstarred.equals(true) && sessionEndDateFlag.equals(true)) {
					MasterVO visibilityFlagForUnstarredQuestions = new MasterVO();
					visibilityFlagForUnstarredQuestions.setName("member_unstarred_questions_view_flag");
					visibilityFlagForUnstarredQuestions.setValue("unstarred_visible");
					visibilityFlags.add(visibilityFlagForUnstarredQuestions);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<MasterVO>();
		}
		return visibilityFlags;
	}
	
	@RequestMapping(value = "/loadVisibilityFlagsForMemberStandaloneMotionsView", method = RequestMethod.GET)
	public @ResponseBody List<MasterVO> populateVisibilityFlagsForMemberStandaloneMotionsView(HttpServletRequest request, Locale locale){
		List<MasterVO> visibilityFlags = new ArrayList<MasterVO>();
		try {
			String strHouseType = request.getParameter("houseType");
			String strYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			String strlocale = locale.toString();
			
			if(strHouseType != null && !strHouseType.isEmpty()
				&& strSessionType != null && !strSessionType.isEmpty()
				&& strYear != null && !strYear.isEmpty()){
				
				HouseType houseType = HouseType.findByType(strHouseType, strlocale);
				SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				Session session = Session.
						findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strYear));
				
				/****Member's Questions Views Visibility Parameters****/
				Boolean sessionEndDateFlag = false;
				Date sessionEndDate = session.getEndDate();
				if(sessionEndDate!=null) {
					String sessionEndDateTimeStr = FormaterUtil.formatDateToString(sessionEndDate, ApplicationConstants.DB_DATEFORMAT);
					CustomParameter visibilityStartTimeCP = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_START_TIME_FOR_MEMBER_STANDALONEMOTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
					if(visibilityStartTimeCP!=null && visibilityStartTimeCP.getValue()!=null) {
						sessionEndDateTimeStr = sessionEndDateTimeStr + " " + visibilityStartTimeCP.getValue();
						Date sessionEndDateTime = FormaterUtil.formatStringToDate(sessionEndDateTimeStr, ApplicationConstants.DB_DATETIME_FORMAT);
						if(new Date().compareTo(sessionEndDateTime)>=0) {
							sessionEndDateFlag = true;
						}
					}
				}
				
				Boolean statusFlag = false;		
				CustomParameter statusFlagForMemberQuestionsView = CustomParameter.findByName(CustomParameter.class, "STATUS_FLAG_FOR_MEMBER_STANDALONEMOTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(statusFlagForMemberQuestionsView!=null && statusFlagForMemberQuestionsView.getValue()!=null
						&& statusFlagForMemberQuestionsView.getValue().equals("visible")) {
					statusFlag = true; 
				}
				if(statusFlag.equals(true) && sessionEndDateFlag.equals(true)) {
					MasterVO visibilityFlagForStatus = new MasterVO();
					visibilityFlagForStatus.setName("member_standalonemotions_view_status_flag");
					visibilityFlagForStatus.setValue("status_visible");
					visibilityFlags.add(visibilityFlagForStatus);
				}
				
				Boolean visibilityFlagForAdmitted = false;
				CustomParameter visibilityFlagForMemberAdmittedQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_ADMITTED_STANDALONEMOTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(visibilityFlagForMemberAdmittedQuestionsView!=null && visibilityFlagForMemberAdmittedQuestionsView.getValue()!=null
						&& visibilityFlagForMemberAdmittedQuestionsView.getValue().equals("visible")) {
					visibilityFlagForAdmitted = true; 
				}
				if(visibilityFlagForAdmitted.equals(true) && sessionEndDateFlag.equals(true)) {
					MasterVO visibilityFlagForAdmittedQuestions = new MasterVO();
					visibilityFlagForAdmittedQuestions.setName("member_admitted_standalonemotions_view_flag");
					visibilityFlagForAdmittedQuestions.setValue("admitted_visible");
					visibilityFlags.add(visibilityFlagForAdmittedQuestions);
				}
				
				Boolean visibilityFlagForRejected = false;
				CustomParameter visibilityFlagForMemberRejectedQuestionsView = CustomParameter.findByName(CustomParameter.class, "VISIBILITY_FLAG_FOR_MEMBER_REJECTED_STANDALONEMOTIONS_VIEW_"+houseType.getType().toUpperCase(), "");
				if(visibilityFlagForMemberRejectedQuestionsView!=null && visibilityFlagForMemberRejectedQuestionsView.getValue()!=null
						&& visibilityFlagForMemberRejectedQuestionsView.getValue().equals("visible")) {
					visibilityFlagForRejected = true; 
				}
				if(visibilityFlagForRejected.equals(true) && sessionEndDateFlag.equals(true)) {
					MasterVO visibilityFlagForRejectedQuestions = new MasterVO();
					visibilityFlagForRejectedQuestions.setName("member_rejected_standalonemotions_view_flag");
					visibilityFlagForRejectedQuestions.setValue("rejected_visible");
					visibilityFlags.add(visibilityFlagForRejectedQuestions);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<MasterVO>();
		}
		return visibilityFlags;
	}
	
	@RequestMapping(value = "/isDepartmentChangeRestricted", method = RequestMethod.GET)
	public @ResponseBody String isDepartmentChangeRestricted(final ModelMap model, 
			final HttpServletRequest request, 
			final Locale locale) {
		String isDepartmentChangeRestricted = "NO";
		String deviceId = request.getParameter("deviceId");
		String usergroupType = request.getParameter("usergroupType");
		if(deviceId!=null && !deviceId.isEmpty()
				&& usergroupType!=null && !usergroupType.isEmpty()) {
			Question domain = Question.findById(Question.class, Long.parseLong(deviceId));
			if(domain!=null) {
				String processingMode = "";
				String sessionProcessingMode = domain.getSession().getParameter(domain.getType().getType()+"_processingMode");
				if(sessionProcessingMode!=null && !sessionProcessingMode.isEmpty()) {
					processingMode = sessionProcessingMode;
				} else {
					processingMode = domain.getHouseType().getType();
				}
				CustomParameter csptDepartmentChangeRestricted = CustomParameter.findByName(CustomParameter.class, domain.getOriginalType().getType().toUpperCase()+"_"+processingMode.toUpperCase()+"_"+usergroupType.toUpperCase()+"_DEPARTMENT_CHANGE_RESTRICTED", locale.toString());
				if(csptDepartmentChangeRestricted!=null && csptDepartmentChangeRestricted.getValue()!=null && csptDepartmentChangeRestricted.getValue().equals("YES")) {
					
					if(domain.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION) //allowed for questions converted to unstarred in previous sessions
							&& new Date().after(domain.getSession().getEndDate())) {
						//allow department change in this case
						return "NO";
					} else {
						//restrict department change in this case
						isDepartmentChangeRestricted = "YES";
					}			
				}
			}
		}	
		return isDepartmentChangeRestricted;
	}
	
	@RequestMapping(value="/proprietypoint/actors", method=RequestMethod.GET)
	public @ResponseBody List<Reference> findProprietyPointActors(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		
		List<Reference> actors = new ArrayList<Reference>();
		String strProprietyPoint = request.getParameter("proprietypoint");
		String strInternalStatus = request.getParameter("status");
		String strUserGroup = request.getParameter("usergroup");
		String strLevel = request.getParameter("level");
		if (strProprietyPoint != null && strInternalStatus != null
				&& strUserGroup != null && strLevel != null) {
			if ((!strProprietyPoint.isEmpty()) && (!strInternalStatus.isEmpty())
					&& (!strUserGroup.isEmpty()) && (!strLevel.isEmpty())) {
				Status internalStatus = Status.findById(Status.class, Long.parseLong(strInternalStatus));
				ProprietyPoint proprietyPoint = ProprietyPoint.findById(ProprietyPoint.class, Long.parseLong(strProprietyPoint));
				UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
				actors = WorkflowConfig.findProprietyPointActorsVO(proprietyPoint, internalStatus, userGroup, Integer.parseInt(strLevel), locale.toString());
			}
		}
		return actors;
	}
	
	@RequestMapping(value = "/isUserSessionActive", method = RequestMethod.GET)
	public @ResponseBody boolean isUserSessionActive(HttpServletRequest request, Locale locale){
		boolean isUserSessionActive = false;
		Object loggedInUser = request.getSession().getAttribute("logged_in_active_user");
        if(loggedInUser!=null && !loggedInUser.toString().isEmpty()) {
        	isUserSessionActive = true;
        }
        ApplicationConstants.isUserSessionActive_URL_HIT_COUNT++; //temporary code
		return isUserSessionActive;
	}
	
	//temporary method
	@RequestMapping(value = "/viewUserSessionActiveHitCount", method = RequestMethod.GET)
	public @ResponseBody long viewUserSessionActiveHitCount(HttpServletRequest request, Locale locale){
		long userSessionActiveHitCount = ApplicationConstants.isUserSessionActive_URL_HIT_COUNT;
		ApplicationConstants.isUserSessionActive_URL_HIT_COUNT = 0;
		return userSessionActiveHitCount;
	}
	//====================temporary method ends========================
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/pendingtasksdevicesprois", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getPendingTasksDevicesPROIS(HttpServletRequest request, Locale locale){
		String strSessionYear = request.getParameter("sessionYear");
		String strSessionType = request.getParameter("sessionType");
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strStatus = request.getParameter("status");
		String strWfSubType = request.getParameter("wfSubType");
		String strGrid = request.getParameter("grid");
		String strSubdepartment = request.getParameter("subdepartment");
		
		CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
		List<MasterVO> vos = new ArrayList<MasterVO>();
				
		try {
			String server=csptDeployment.getValue();
			SessionType sessionType = null;
			HouseType houseType = null;
			Integer year = null;
			Session session = null;					
			DeviceType deviceType = null;
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			List data = null;
			
			if(strGrid.equals("workflow")){
				if(csptDeployment!=null){
					if(server.equals("TOMCAT")){
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strStatus = new String(strStatus.getBytes("ISO-8859-1"),"UTF-8");
						strWfSubType = new String(strWfSubType.getBytes("ISO-8859-1"),"UTF-8");
					}
				}
				
				sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				houseType = HouseType.findByName(strHouseType, locale.toString());
				year = new Integer(FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).parse(strSessionYear).intValue());
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("workflowSubType", new String[]{strWfSubType});
				parameters.put("assignee", new String[]{this.getCurrentUser().getActualUsername()});
				parameters.put("locale", new String[]{locale.toString()});
				
				data = Query.findReport("PROIS_STATUS_REPORT_DEVICES_WF", parameters);
				
			}else if(strGrid.equals("device")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
				houseType = HouseType.findByType(strHouseType, locale.toString());
				year = new Integer(Integer.parseInt(strSessionYear));
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);					
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
				
				parameters.put("sessionId", new String[]{session.getId().toString()});
				parameters.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				parameters.put("status", new String[]{strStatus});
				parameters.put("subdepartment", new String[]{strSubdepartment});
				parameters.put("locale", new String[]{locale.toString()});
				
				data = Query.findReport("PROIS_STATUS_REPORT_DEVICES_DV", parameters);
			}
			
			if(data != null){
				for(Object o : data){
					Object[] objx = (Object[]) o;
					MasterVO vo = new MasterVO();
					if(objx[0] != null){
						vo.setValue(objx[0].toString());
						vo.setNumber(Integer.parseInt(objx[2].toString()));
						vos.add(vo);
					}
				}
			}
			
		} catch (ELSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(vos!=null && !vos.isEmpty() && vos.size()>1) {
			vos = MasterVO.sort(vos, "number", ApplicationConstants.ASC); //order by number
		}
		
		return vos;
	}
	
	@RequestMapping(value="/membersbyhouse", method=RequestMethod.GET)
	public List<AutoCompleteVO> getMembersAutoCompleteDataByHouse(HttpServletRequest request,Locale locale){
		CustomParameter customParameter = CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<AutoCompleteVO> autoCompleteVOs = new ArrayList<AutoCompleteVO>();
		String strHouseId = request.getParameter("house");
		House house = House.findById(House.class, Long.parseLong(strHouseId));
		String strParam = null;
		if(customParameter != null){
			String server = customParameter.getValue();
			if(server.equals("TOMCAT")){
				strParam = request.getParameter("term");
				try {
					strParam = new String(strParam.getBytes("ISO-8859-1"),"UTF-8");
				}
				catch (UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}else{
				strParam = request.getParameter("term");
			}
		}
		List<MasterVO> memberVOs = HouseMemberRoleAssociation.findActiveMembersInHouseByTerm(house, strParam, locale.toString());
		for(MasterVO i : memberVOs){
			AutoCompleteVO autoCompleteVO = new AutoCompleteVO();
			autoCompleteVO.setId(i.getId());
			autoCompleteVO.setValue(i.getName());
			autoCompleteVOs.add(autoCompleteVO);
		}
		
		return autoCompleteVOs;
	}
	
	
	@RequestMapping(value="/rulessuspensionmotion/actors",method=RequestMethod.POST)
	public @ResponseBody List<Reference> findRulesSuspensionMotionActors(final HttpServletRequest request,
			final ModelMap model, final Locale locale){
		List<Reference> actors=new ArrayList<Reference>();
		String strMotion=request.getParameter("motion");
		String strInternalStatus=request.getParameter("status");
		String strUserGroup=request.getParameter("usergroup");
		String strLevel=request.getParameter("level");
		if(strMotion!=null&&strInternalStatus!=null&&strUserGroup!=null&&strLevel!=null){
			if((!strMotion.isEmpty())&&(!strInternalStatus.isEmpty())&&
					(!strUserGroup.isEmpty())&&(!strLevel.isEmpty())){
				Status internalStatus=Status.findById(Status.class,Long.parseLong(strInternalStatus));
				RulesSuspensionMotion rulesSuspensionMotion = RulesSuspensionMotion.findById(RulesSuspensionMotion.class,Long.parseLong(strMotion));
				UserGroup userGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strUserGroup));
				try {
					actors=WorkflowConfig.findRulesSuspensionMotionActorsVO(rulesSuspensionMotion,internalStatus,userGroup,Integer.parseInt(strLevel),locale.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return actors;
	}
	
	@RequestMapping(value="/loadsessionsbyhousetype", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> loadSessionsByHouseType(HttpServletRequest request, HttpServletResponse response,
			final Locale locale) {
		List<MasterVO> sessionList = new ArrayList<MasterVO>();
		try {String strHouseTypeId = request.getParameter("houseTypeId");
			if(strHouseTypeId != null && !strHouseTypeId.isEmpty()){
				HouseType houseType = HouseType.findById(HouseType.class, Long.parseLong(strHouseTypeId));
				List<House> houses = House.findByHouseType(houseType.getType(), locale.toString());
				for(House h : houses){
					List<Session> sessions = Session.findAllByFieldName(Session.class, "house", h, "year", "desc", locale.toString());
					for(Session s : sessions){
						MasterVO masterVO = new MasterVO();
						masterVO.setName(s.getType().getSessionType() + " " + s.getYear());
						masterVO.setId(s.getId());
						sessionList.add(masterVO);
					}
				}
				
			}
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sessionList;
		
	}
	
	
	/**** To get the clubbed adjournment motion's text ****/
	@RequestMapping(value="/rulessuspensionmotion/{id}/clubbedmotiontext", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> getClubbedRulesSuspensionMotionTexts(@PathVariable("id") Long id, final HttpServletRequest request, final Locale locale){
		
		List<MasterVO> clubbedRulesSuspensionMotionsVO = new ArrayList<MasterVO>();
		
		try{
			
			RulesSuspensionMotion parent = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, id);
			if(parent == null){
				WorkflowDetails wfDetails = WorkflowDetails.findById(WorkflowDetails.class, id);
				if(wfDetails != null){
					parent = RulesSuspensionMotion.findById(RulesSuspensionMotion.class, Long.parseLong(wfDetails.getDeviceId()));
				}
			}
			
			if(parent != null){
				List<ClubbedEntity> clubbedRulesSuspensionMotions = parent.getClubbedEntities();
				
				for(ClubbedEntity ce : clubbedRulesSuspensionMotions){
					RulesSuspensionMotion cRulesSuspensionMotion = ce.getRulesSuspensionMotion();
					if(clubbedRulesSuspensionMotions != null){
						MasterVO mVO = new MasterVO();
						mVO.setId(cRulesSuspensionMotion.getId());
						mVO.setName(FormaterUtil.formatNumberNoGrouping(cRulesSuspensionMotion.getNumber(), locale.toString()));
						mVO.setDisplayName(cRulesSuspensionMotion.getPrimaryMember().findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
//						if(cRulesSuspensionMotion.getRevisedNoticeContent() != null && !cRulesSuspensionMotion.getRevisedNoticeContent().isEmpty()){
//							mVO.setValue(cRulesSuspensionMotion.getRevisedNoticeContent());
//						}else{
							mVO.setValue(cRulesSuspensionMotion.getNoticeContent());
//						}
						
						clubbedRulesSuspensionMotionsVO.add(mVO);
					}
				}
			}
		}catch(Exception e){
			logger.error(e.toString());
		}
		
		
		return clubbedRulesSuspensionMotionsVO;
	}
	
	
	@RequestMapping(value="/rulessuspensionmotion/duplicatenumber", method=RequestMethod.GET)
	public @ResponseBody Boolean isDuplicateNumberedRulesSuspensionMotion(HttpServletRequest request, Locale locale) throws ParseException, UnsupportedEncodingException{
		Boolean flag=false;
		String strRuleSuspensionDate=request.getParameter("ruleSuspensionDate");
		String strNumber=request.getParameter("number");		
		if(strNumber!=null && !strNumber.isEmpty() 
			&& strRuleSuspensionDate!=null && !strRuleSuspensionDate.isEmpty()){
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(csptDeployment!=null){
				String server=csptDeployment.getValue();
				if(server.equals("TOMCAT")){
					strNumber = new String(strNumber.getBytes("ISO-8859-1"),"UTF-8");
				}
			}
			Date ruleSuspensionDate = FormaterUtil.formatStringToDate(strRuleSuspensionDate, ApplicationConstants.SERVER_DATEFORMAT);
			Integer ruleSuspensionMotionNumber=FormaterUtil.getDecimalFormatterWithGrouping(0, locale.toString()).parse(strNumber).intValue();
			flag=RulesSuspensionMotion.isDuplicateNumberExist(ruleSuspensionDate, ruleSuspensionMotionNumber, null, locale.toString());
		}
		return flag;
	}
	
	
	@RequestMapping(value="/cutmotion/updatedTotalAmoutDemanded", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String updateTotalAmoutDemandedForCutMotion(final HttpServletRequest request, final Locale locale) throws UnsupportedEncodingException {
		String revisedTotalAmountDemanded = "";
		String strRevisedTotalAmoutDemanded=request.getParameter("value");
		if(strRevisedTotalAmoutDemanded!=null && !strRevisedTotalAmoutDemanded.isEmpty()) {
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(csptDeployment!=null){
				String server=csptDeployment.getValue();
				if(server!=null && server.equals("TOMCAT")){
					strRevisedTotalAmoutDemanded = new String(strRevisedTotalAmoutDemanded.getBytes("ISO-8859-1"),"UTF-8");
				}
			}
			revisedTotalAmountDemanded = FormaterUtil.formatNumberForIndianCurrencyWithSymbol(Long.parseLong(strRevisedTotalAmoutDemanded), locale.toString());
		}		
		return revisedTotalAmountDemanded;
	}
	
	@RequestMapping(value = "/format_numeric_text", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String formatNumericTextInRequiredLocale(HttpServletRequest request,
			HttpServletResponse response,
			Locale locale) throws UnsupportedEncodingException{
		
		String formattedNumericText = "";
		
		String numericText = request.getParameter("numericText");
		if(numericText!=null && !numericText.isEmpty()) {
			CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
			if(csptDeployment!=null){
				String server=csptDeployment.getValue();
				if(server!=null && server.equals("TOMCAT")){
					numericText = new String(numericText.getBytes("ISO-8859-1"),"UTF-8");
				}
			}
			
			String requiredLocale = request.getParameter("locale");
			if(requiredLocale==null || requiredLocale.isEmpty()) {
				requiredLocale = locale.toString();
			}
			
			formattedNumericText = FormaterUtil.formatNumbersInGivenText(numericText, requiredLocale);
		}
		
		return formattedNumericText;
	}
	
	@RequestMapping(value="/workflow/findDevicesForReminderOfReply", method=RequestMethod.GET)
	public @ResponseBody List<Long> findDevicesForReminderOfReply_WorkflowPage(HttpServletRequest request, ModelMap model,Locale locale){
		//String[] devicesForReminderOfReply = new String[]();
		List<Long> devicesForReminderOfReply = new ArrayList<Long>();
		
		String strDeviceType=request.getParameter("deviceType");
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDepartment=request.getParameter("department");
		
		if(strDeviceType!=null && !strDeviceType.isEmpty()
			&& strHouseType!=null && !strHouseType.isEmpty()
			&& strSessionType!=null && !strSessionType.isEmpty()
			&& strSessionYear!=null && !strSessionYear.isEmpty()){
			try {
				CustomParameter csptDeployment = CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
				if(csptDeployment!=null){
					String server=csptDeployment.getValue();
					if(server.equals("TOMCAT")){
						strDeviceType = new String(strDeviceType.getBytes("ISO-8859-1"),"UTF-8");
						strSessionType = new String(strSessionType.getBytes("ISO-8859-1"),"UTF-8");
						strHouseType = new String(strHouseType.getBytes("ISO-8859-1"),"UTF-8");
						strSessionYear = new String(strSessionYear.getBytes("ISO-8859-1"),"UTF-8");
						if(strDepartment!=null && !strDepartment.isEmpty()) {
							strDepartment = new String(strDepartment.getBytes("ISO-8859-1"),"UTF-8");
						}						
					}
				}
				
				HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
				SessionType sessionType = SessionType.findByFieldName(SessionType.class, "sessionType", strSessionType, locale.toString());
				Integer sessionYear = new Integer(FormaterUtil.getDecimalFormatterWithNoGrouping(0,locale.toString()).parse(strSessionYear).toString());
				Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				
				DeviceType deviceType = DeviceType.findByName(DeviceType.class, strDeviceType, locale.toString());
				if(deviceType!=null && deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					
					SubDepartment subDepartment = SubDepartment.findByName(SubDepartment.class, strDepartment, locale.toString());
					List<Long> questionIds = new ArrayList<Long>();
					String userGroupType = request.getParameter("userGroupType");
					if(userGroupType!=null 
							&& (userGroupType.equalsIgnoreCase(ApplicationConstants.DEPARTMENT) || userGroupType.equalsIgnoreCase(ApplicationConstants.DEPARTMENT_DESKOFFICER))) {
						
			    		String reminderNumberStartLimitingDate = "";
			    		String reminderNumberEndLimitingDate = "";
						if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
		    				House correspondingAssemblyHouse = Session.findCorrespondingAssemblyHouseForCouncilSession(session);
		    				Date houseStartDate = correspondingAssemblyHouse.getFirstDate();
		    				reminderNumberStartLimitingDate = FormaterUtil.formatDateToString(houseStartDate, ApplicationConstants.DB_DATEFORMAT);
		    				Date houseEndDate = correspondingAssemblyHouse.getLastDate();
		    				reminderNumberEndLimitingDate = FormaterUtil.formatDateToString(houseEndDate, ApplicationConstants.DB_DATEFORMAT);
		    			} else {
		    				Date houseStartDate = session.getHouse().getFirstDate();
		    				reminderNumberStartLimitingDate = FormaterUtil.formatDateToString(houseStartDate, ApplicationConstants.DB_DATEFORMAT);
		    				Date houseEndDate = session.getHouse().getLastDate();
		    				reminderNumberEndLimitingDate = FormaterUtil.formatDateToString(houseEndDate, ApplicationConstants.DB_DATEFORMAT);
		    			}
						Map<String, String> reminderLetterIdentifiers = new HashMap<String, String>();
			    		reminderLetterIdentifiers.put("houseType", houseType.getType());
			    		reminderLetterIdentifiers.put("deviceType", deviceType.getType());
			    		reminderLetterIdentifiers.put("reminderFor", ApplicationConstants.REMINDER_FOR_REPLY_FROM_DEPARTMENT);
			    		reminderLetterIdentifiers.put("reminderTo", subDepartment.getId().toString());
			    		reminderLetterIdentifiers.put("reminderNumberStartLimitingDate", reminderNumberStartLimitingDate);
			    		reminderLetterIdentifiers.put("reminderNumberEndLimitingDate", reminderNumberEndLimitingDate);
			    		reminderLetterIdentifiers.put("locale", locale.toString());
			    		ReminderLetter latestReminderLetter = ReminderLetter.findLatestByFieldNames(reminderLetterIdentifiers, locale.toString());
			    		
			    		if(latestReminderLetter!=null) {
			    			String devices = latestReminderLetter.getDeviceIds();
			    			if(devices!=null) {
			    				for(String qid: devices.split(",")) {
			    					questionIds.add(Long.parseLong(qid));
			    				}
			    			}
			    		}
					} else {
						questionIds = Question.findQuestionIDsHavingPendingAnswersPostLastDateOfAnswerReceiving(houseType, deviceType, subDepartment, locale.toString());
					}
					if(questionIds!=null && !questionIds.isEmpty()) {
						devicesForReminderOfReply = null;
						devicesForReminderOfReply = questionIds;
					}
					
				} else if(deviceType!=null && deviceType.getType().startsWith(ApplicationConstants.DEVICE_CUTMOTIONS)) {
					
					SubDepartment subDepartment = SubDepartment.findByName(SubDepartment.class, strDepartment, locale.toString());
					List<Long> cutmotionIds = new ArrayList<Long>();
					String userGroupType = request.getParameter("userGroupType");
					if(userGroupType!=null 
							&& (userGroupType.equalsIgnoreCase(ApplicationConstants.DEPARTMENT) || userGroupType.equalsIgnoreCase(ApplicationConstants.DEPARTMENT_DESKOFFICER))) {
						
			    		String reminderNumberStartLimitingDate = "";
			    		String reminderNumberEndLimitingDate = "";
						if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
		    				House correspondingAssemblyHouse = Session.findCorrespondingAssemblyHouseForCouncilSession(session);
		    				Date houseStartDate = correspondingAssemblyHouse.getFirstDate();
		    				reminderNumberStartLimitingDate = FormaterUtil.formatDateToString(houseStartDate, ApplicationConstants.DB_DATEFORMAT);
		    				Date houseEndDate = correspondingAssemblyHouse.getLastDate();
		    				reminderNumberEndLimitingDate = FormaterUtil.formatDateToString(houseEndDate, ApplicationConstants.DB_DATEFORMAT);
		    			} else {
		    				Date houseStartDate = session.getHouse().getFirstDate();
		    				reminderNumberStartLimitingDate = FormaterUtil.formatDateToString(houseStartDate, ApplicationConstants.DB_DATEFORMAT);
		    				Date houseEndDate = session.getHouse().getLastDate();
		    				reminderNumberEndLimitingDate = FormaterUtil.formatDateToString(houseEndDate, ApplicationConstants.DB_DATEFORMAT);
		    			}
						Map<String, String> reminderLetterIdentifiers = new HashMap<String, String>();
			    		reminderLetterIdentifiers.put("houseType", houseType.getType());
			    		reminderLetterIdentifiers.put("deviceType", deviceType.getType());
			    		reminderLetterIdentifiers.put("reminderFor", ApplicationConstants.REMINDER_FOR_REPLY_FROM_DEPARTMENT);
			    		reminderLetterIdentifiers.put("reminderTo", subDepartment.getId().toString());
			    		reminderLetterIdentifiers.put("reminderNumberStartLimitingDate", reminderNumberStartLimitingDate);
			    		reminderLetterIdentifiers.put("reminderNumberEndLimitingDate", reminderNumberEndLimitingDate);
			    		reminderLetterIdentifiers.put("locale", locale.toString());
			    		ReminderLetter latestReminderLetter = ReminderLetter.findLatestByFieldNames(reminderLetterIdentifiers, locale.toString());
			    		
			    		if(latestReminderLetter!=null) {
			    			String devices = latestReminderLetter.getDeviceIds();
			    			if(devices!=null) {
			    				for(String qid: devices.split(",")) {
			    					cutmotionIds.add(Long.parseLong(qid));
			    				}
			    			}
			    		}
					} else {
						cutmotionIds = CutMotion.findCutMotionIDsHavingPendingReplyPostLastDateOfReplyReceiving(houseType, deviceType, subDepartment, locale.toString());
					}
					if(cutmotionIds!=null && !cutmotionIds.isEmpty()) {
						devicesForReminderOfReply = null;
						devicesForReminderOfReply = cutmotionIds;
					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ELSException e) {
				e.printStackTrace();
			}
		}
		
		return devicesForReminderOfReply;
	}
	
	@RequestMapping(value="/device/findDevicesForReminderOfReply", method=RequestMethod.GET)
	public @ResponseBody List<Long> findDevicesForReminderOfReply_DevicePage(HttpServletRequest request, ModelMap model,Locale locale){
		//String[] devicesForReminderOfReply = new String[]();
		List<Long> devicesForReminderOfReply = new ArrayList<Long>();
		
		String strDeviceType=request.getParameter("deviceType");
		String strHouseType=request.getParameter("houseType");
		String strSessionType=request.getParameter("sessionType");
		String strSessionYear=request.getParameter("sessionYear");
		String strDepartment=request.getParameter("department");
		
		if(strDeviceType!=null && !strDeviceType.isEmpty()
			&& strHouseType!=null && !strHouseType.isEmpty()
			&& strSessionType!=null && !strSessionType.isEmpty()
			&& strSessionYear!=null && !strSessionYear.isEmpty()){
			try {				
				HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
				//SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
				//Integer sessionYear = new Integer(strSessionYear);
				//Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				
				if(deviceType!=null && deviceType.getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strDepartment));
					List<Long> questionIds = Question.findQuestionIDsHavingPendingAnswersPostLastDateOfAnswerReceiving(houseType, deviceType, subDepartment, locale.toString());
					if(questionIds!=null && !questionIds.isEmpty()) {
						devicesForReminderOfReply = null;
						devicesForReminderOfReply = questionIds;
					}
					
				} else if(deviceType!=null && deviceType.getType().startsWith(ApplicationConstants.DEVICE_CUTMOTIONS)) {
					SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strDepartment));
					List<Long> cutmotionIds = CutMotion.findCutMotionIDsHavingPendingReplyPostLastDateOfReplyReceiving(houseType, deviceType, subDepartment, locale.toString());
					if(cutmotionIds!=null && !cutmotionIds.isEmpty()) {
						devicesForReminderOfReply = cutmotionIds;
					}
				}
			}
			catch (ELSException e) {
				e.printStackTrace();
			}
		}
		
		return devicesForReminderOfReply;
	}
	
	@RequestMapping(value="/cutmotion/validate_department",method=RequestMethod.GET)
	public @ResponseBody String validateDepartmentForCutMotion(HttpServletRequest request, Locale locale){
		String validationResponse = "";
		String strSubDepartment = request.getParameter("subDepartment");
		String strSession = request.getParameter("session");
		String strDeviceType = request.getParameter("deviceType");
		if(strSubDepartment != null && !strSubDepartment.isEmpty()
			&& strSession != null && !strSession.isEmpty()
			&& strDeviceType != null && !strDeviceType.isEmpty()){
			SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubDepartment));
			Session session = Session.findById(Session.class, Long.parseLong(strSession));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			try {
				if(subDepartment!=null && session!=null && deviceType!=null) {
					Status dateAdmitted = Status.findByType(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION, locale.toString());
					//Status dateAdmissionProcessed = Status.findByType(ApplicationConstants.CUTMOTIONDATE_PROCESSED_DATE_ADMISSION, locale.toString());
					CutMotionDate cutMotionDate = CutMotionDate.findCutMotionDateSessionDeviceType(session, deviceType, locale.toString());
					if(cutMotionDate != null){
						if(cutMotionDate.getStatus().getType().equals(dateAdmitted.getType())){
							for(CutMotionDepartmentDatePriority p : cutMotionDate.getDepartmentDates()){
								if(subDepartment != null){
									if(p.getSubDepartment().getName().equals(subDepartment.getName())/* && p.getDepartment().getName().equals(cutMotion.getSubDepartment().getDepartment().getName())*/){
										if((new Date()).after(p.getSubmissionEndDate())){
											validationResponse = "expired";
											break;
										} else {
											validationResponse = "available";
											break;
										}
									}
								}
							}
							if(validationResponse.isEmpty()) {
								validationResponse = "unavailable";
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return validationResponse;
	}
	
	@RequestMapping(value="/appropriationbillmotion/validate_department",method=RequestMethod.GET)
	public @ResponseBody String validateDepartmentForAppropriationBillMotion(HttpServletRequest request, Locale locale){
		String validationResponse = "unavailable";
		String strSubDepartment = request.getParameter("subDepartment");
		String strSession = request.getParameter("session");
		String strDeviceType = request.getParameter("deviceType");
		if(strSubDepartment != null && !strSubDepartment.isEmpty()
			&& strSession != null && !strSession.isEmpty()
			&& strDeviceType != null && !strDeviceType.isEmpty()){
			SubDepartment subDepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strSubDepartment));
			Session session = Session.findById(Session.class, Long.parseLong(strSession));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			try {
				if(subDepartment!=null && session!=null && deviceType!=null) {
					DeviceType cutMotionDeviceType = null;
					if(deviceType.getType().equals(ApplicationConstants.MOTIONS_APPROPRIATIONBILLMOTION_BUDGETARY)) {
						cutMotionDeviceType = DeviceType.findByType(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY, locale.toString());
					} else {
						cutMotionDeviceType = DeviceType.findByType(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY, locale.toString());
					}
					Status dateAdmitted = Status.findByType(ApplicationConstants.CUTMOTIONDATE_FINAL_DATE_ADMISSION, locale.toString());
					//Status dateAdmissionProcessed = Status.findByType(ApplicationConstants.CUTMOTIONDATE_PROCESSED_DATE_ADMISSION, locale.toString());
					CutMotionDate cutMotionDate = CutMotionDate.findCutMotionDateSessionDeviceType(session, cutMotionDeviceType, locale.toString());
					if(cutMotionDate != null){
						if(cutMotionDate.getStatus().getType().equals(dateAdmitted.getType())){
							boolean isPresentInCutMotionDepartmentDatePriority = false;
							for(CutMotionDepartmentDatePriority p : cutMotionDate.getDepartmentDates()){
								if(subDepartment != null){
									if(p.getSubDepartment().getName().equals(subDepartment.getName())/* && p.getDepartment().getName().equals(cutMotion.getSubDepartment().getDepartment().getName())*/){
										isPresentInCutMotionDepartmentDatePriority = true;
										break;
									}
								}
							}
							if(!isPresentInCutMotionDepartmentDatePriority) {
								validationResponse = "available";
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return validationResponse;
	}
	
	@RequestMapping(value="findLatestSessionYear/{selectedHouseType}", method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> findLatestSessionYear(
			@PathVariable("selectedHouseType") final String selectedHouseType,
			final HttpServletRequest request,
			final Locale locale) {
		
		Session lastSessionCreated;
		List<MasterVO> years = new ArrayList<MasterVO>();
		try {
			HouseType houseType = HouseType.findByType(selectedHouseType, locale.toString());
			lastSessionCreated = Session.findLatestSession(houseType);
			
			if(lastSessionCreated.getId()!=null){
				
				
				
				/**** Years ****/
			
				
				//set upper limit for years available
				Integer latestYear = lastSessionCreated.getYear();
				if(latestYear == null) {
					latestYear = new GregorianCalendar().get(Calendar.YEAR); //set as current year in case latest session has no year set.
				}				
				
				//starting year must be set as custom parameter 'HOUSE_FORMATION_YEAR'
				CustomParameter houseFormationYearParameter = CustomParameter.
						findByFieldName(CustomParameter.class, "name", "HOUSE_FORMATION_YEAR", "");
				
				if(houseFormationYearParameter != null) {
					if(!houseFormationYearParameter.getValue().isEmpty()) {
						Integer houseFormationYear;
						try {
							houseFormationYear = Integer.parseInt(houseFormationYearParameter.getValue());
							for(Integer i = latestYear; i >= houseFormationYear; i--) {
								MasterVO year = new MasterVO();
								year.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
								year.setValue(i.toString());							
								years.add(year);
							}
							
					
						
						}
						catch(NumberFormatException ne) {
												
						}				
					}
				}	
						
			}
			
		} catch (ELSException e) {
			
		} catch (Exception e) {
			String message = e.getMessage();
			
			if(message == null){
				message = "There is some problem, rquest may not complete successfully.";
			}
			
			e.printStackTrace();
		}						
		return years;
	}
	
}