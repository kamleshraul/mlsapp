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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AutoCompleteVO;
import org.mkcl.els.common.vo.ConstituencyCompleteVO;
import org.mkcl.els.common.vo.DynamicSelectVO;
import org.mkcl.els.common.vo.GroupVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Department;
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
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.repository.DistrictRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
                    @PathVariable("stateId") final Long stateId, final ModelMap map,
                    final Locale locale) {
        return District.findDistrictsRefByStateId(
                stateId , "name" , ApplicationConstants.ASC ,
                locale.toString());
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
                    @PathVariable("districtId") final Long districtId, final ModelMap map,
                    final Locale locale) {
        return Tehsil.findTehsilsRefByDistrictId(
                districtId , "name" , ApplicationConstants.ASC ,
                locale.toString());
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
                    @PathVariable("state_id") final Long stateId, final ModelMap map,
                    final Locale locale) {
        return District.getDistrictRepository().findDistrictsByStateId(stateId,
                "name", "ASC", locale.toString());
    }

    /**
     * Gets the divisions by state id.
     *
     * @param stateId the state id
     * @param map the map
     * @param locale the locale
     * @return the divisions by state id
     */
    /**
     * @param stateId
     * @param map
     * @param locale
     * @return
     */
    @RequestMapping(value = "/{state_id}/divisions", method = RequestMethod.GET)
    public @ResponseBody
    List<Division> getDivisionsByStateId(
            @PathVariable("state_id") final Long stateId, final ModelMap map,
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
                    final ModelMap map, final Locale locale) {
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
                    final ModelMap map, final Locale locale) {
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
                    final ModelMap map, final Locale locale) {
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

    /*
     * @RequestMapping(value = "/{division_id}/name", method =
     * RequestMethod.GET) public @ResponseBody String getNameByDivisionId(
     *
     * @PathVariable("division_id") final Long divisionId, final ModelMap map,
     * final Locale locale) { String name = ""; Division division =
     * Division.findById(Division.class, divisionId); if (division != null) {
     * name = division.getName(); } return name; }
     */

    // /**
    // * Gets the constituencies by district id.
    // *
    // * @param districtId
    // * the district id
    // * @param map
    // * the map
    // * @return the constituencies by district id
    // */
    // @RequestMapping(value = "/{district_id}/constituencies", method =
    // RequestMethod.GET)
    // public @ResponseBody
    // List<Constituency> getConstituenciesByDistrictId(
    // @PathVariable("district_id") final Long districtId,
    // final ModelMap map) {
    // return Constituency.findConstituenciesByDistrictId(districtId);
    // }

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
                    final ModelMap map, final HttpServletRequest request,
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
                    final ModelMap map, final HttpServletRequest request,
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
            DistrictRepository dr = new DistrictRepository();
            districts = dr.findDistrictsByConstituencyId(constituency.getId(),
                    "name", "ASC");
            return districts;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return districts;
        }

    }

    @RequestMapping(value="/{house}/firstdate")
    public Reference getLastDate(@PathVariable("house") final Long houseid){
        Reference reference=new Reference();
        House house=House.findById(House.class, houseid);
        reference.setId(house.getFirstDate().toString());
        reference.setName(house.getFirstDate().toString());
        return reference;
    }

    @RequestMapping(value="department/{department}/subDepartments",method=RequestMethod.GET)
    public @ResponseBody List<SubDepartment> getSubDepartmentsByDepartment(
            @PathVariable("department") final Long department,final ModelMap map,final Locale locale){
        Department dept=Department.findById(Department.class, department);
        List<SubDepartment> subDepartments=SubDepartment.findAllByFieldName(SubDepartment.class, "department", dept, "name", ApplicationConstants.ASC, locale.toString());
        return subDepartments;
    }

    @RequestMapping(value="member/{memberId}/fullName", method=RequestMethod.GET)
    public @ResponseBody Reference getMemberFullName(@PathVariable("memberId") final Long memberId){
        Member member = Member.findById(Member.class, memberId);
        Reference reference = new Reference();
        reference.setId(member.getId().toString());
        reference.setName(member.getFullname());
        return reference;
    }

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

    @RequestMapping(value="elections/{houseType}", method=RequestMethod.GET)
    public @ResponseBody List<MasterVO> getElectionsByType(@PathVariable("houseType") final String houseType,
            final Locale locale){
        List<Election> elections=Election.findByHouseType(houseType, locale.toString());
        List<MasterVO> rolesVOs=new ArrayList<MasterVO>();
        for(Election i:elections){
            rolesVOs.add(new MasterVO(i.getId(), i.getName()));
        }
        return rolesVOs;
    }

    @RequestMapping(value="constituencies/{houseType}", method=RequestMethod.GET)
    public @ResponseBody List<MasterVO> getConstituenciesByType(@PathVariable("houseType") final String houseType,
            final Locale locale){
        List<MasterVO> constituenciesVOs=Constituency.findAllByHouseType(houseType, locale.toString());
        return constituenciesVOs;
    }

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

    @RequestMapping(value="district/{tehsilId}", method=RequestMethod.GET)
    public @ResponseBody Reference getDistrict(@PathVariable("tehsilId") final Long tehsilId,
            final Locale locale) {
        Tehsil tehsil=Tehsil.findById(Tehsil.class,tehsilId);
        Reference reference = new Reference();
        reference.setId(String.valueOf(tehsil.getId()));
        reference.setName(tehsil.getName());
        return reference;
    }

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

    @RequestMapping(value="/departments/{ministry}", method=RequestMethod.GET)
    public @ResponseBody List<MasterVO> getDepartments(
            @PathVariable("ministry") final Long ministry,
            final Locale locale
    ) {
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

    @RequestMapping(value="/subdepartments/{ministry}/{department}", method=RequestMethod.GET)
    public @ResponseBody List<MasterVO> getSubDepartments(
            @PathVariable("ministry") final Long ministry,
            @PathVariable("department") final Long department,
            final Locale locale
    ) {
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

    @RequestMapping(value="/session/{houseType}/{sessionYear}/{sessionType}", method=RequestMethod.GET)
    public @ResponseBody MasterVO getSession(
            final Locale locale,
            @PathVariable("houseType")final Long houseType,
            @PathVariable("sessionYear")final Integer sessionYear,
            @PathVariable("sessionType") final Long sessionType
    ) {
        //populating departments
        HouseType selectedHouseType=HouseType.findById(HouseType.class, houseType);
        SessionType selectedSessionType=SessionType.findById(SessionType.class, sessionType);
        Session selectedSession=Session.findSessionByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, sessionYear);
        if(selectedSession!=null){
            MasterVO masterVO=new MasterVO(selectedSession.getId(),"");
            return masterVO;
        }else{
            return new MasterVO();
        }
    }

    @RequestMapping(value="/members",method=RequestMethod.GET)
    public @ResponseBody List<AutoCompleteVO> getMembers(final HttpServletRequest request,final Locale locale,@RequestParam("session")final Long session
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

    @RequestMapping(value="/supportingmembers",method=RequestMethod.GET)
    public @ResponseBody List<DynamicSelectVO> getSupportingMembers(final HttpServletRequest request,final Locale locale,@RequestParam("session")final Long session
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

    @RequestMapping(value = "titles", method = RequestMethod.GET)
	public @ResponseBody String showTitles(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		Grid grid = Grid.findByDetailView("house", locale.toString());
		model.addAttribute("gridId", grid.getId());
		model.addAttribute("houseType", this.getCurrentUser().getHouseType());
		model.addAttribute("messagePattern", "house");
		model.addAttribute("urlPattern", "house");
		return "house/list";
	}

    @RequestMapping(value = "/{houseType}/house",
            method = RequestMethod.GET)
            public @ResponseBody
            List<House> getHousesByHouseType(
                    @PathVariable("houseType") final Long houseTypeId,
                    final ModelMap map, final Locale locale) {
        List<House> houses = new ArrayList<House>();
        HouseType houseType = HouseType.findById(HouseType.class, houseTypeId);
        if (houseType != null) {
            houses = House.findAllByFieldName(House.class, "type",
            		houseType, "name", "asc", locale.toString());
        }

        return houses;
    }

/*
 * loadGroups,loadDepartments and loadSubDepartments are used in user group jsp
 */
    @RequestMapping(value="/groups")
    public @ResponseBody List<MasterVO> loadGroups(final HttpServletRequest request,final Locale locale){
      CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
      List<MasterVO> masterVOs=new ArrayList<MasterVO>();
      List<Group> groups=new ArrayList<Group>();
      if(customParameter!=null){
          String server=customParameter.getValue();
          if(server.equals("TOMCAT")){
              String strhouseType=request.getParameter("housetype");
              String stryear=request.getParameter("year");
              String strsessionType=request.getParameter("sessiontype");
              String strlocale=locale.toString();
              try {
                  String houseType=new String(strhouseType.getBytes("ISO-8859-1"),"UTF-8");
                  HouseType selectedHouseType=HouseType.findByName(HouseType.class, houseType, strlocale);
                  String sessionType=new String(strsessionType.getBytes("ISO-8859-1"),"UTF-8");
                  SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", sessionType, strlocale);
                  Integer year=Integer.parseInt(stryear);
                  groups=Group.findByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);

              }
              catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
          }else{
              String strhouseType=request.getParameter("housetype");
              String strlocale=locale.toString();
              HouseType selectedHouseType=HouseType.findByName(HouseType.class, strhouseType, strlocale);
              String stryear=request.getParameter("year");
              Integer year=Integer.parseInt(stryear);
              String strsessionType=request.getParameter("sessiontype");
              SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", strsessionType, strlocale);
              groups=Group.findByHouseTypeSessionTypeYear(selectedHouseType, selectedSessionType, year);
          }
      }
      for(Group i:groups){
          MasterVO masterVO=new MasterVO(i.getId(),String.valueOf(i.getNumber()));
          masterVOs.add(masterVO);
      }
      return masterVOs;
    }

    @RequestMapping(value="/departments")
    public @ResponseBody List<MasterVO> loadDepartments(final HttpServletRequest request,final Locale locale){
      CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
      List<MasterVO> masterVOs=new ArrayList<MasterVO>();
      List<Group> groups=new ArrayList<Group>();
      if(customParameter!=null){
          String server=customParameter.getValue();
          if(server.equals("TOMCAT")){
              String strhouseType=request.getParameter("housetype");
              String stryear=request.getParameter("year");
              String strsessionType=request.getParameter("sessiontype");
              String strlocale=locale.toString();
              String strGroup=request.getParameter("group");
              try {
                  String houseType=new String(strhouseType.getBytes("ISO-8859-1"),"UTF-8");
                  HouseType selectedHouseType=HouseType.findByName(HouseType.class, houseType, strlocale);
                  String sessionType=new String(strsessionType.getBytes("ISO-8859-1"),"UTF-8");
                  SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", sessionType, strlocale);
                  Integer year=Integer.parseInt(stryear);
                  String[] delimitedgroups=strGroup.split(",");
                  Integer[] newgroups=new Integer[delimitedgroups.length];
                  for(int i=0;i<delimitedgroups.length;i++){
                      newgroups[i]=Integer.parseInt(delimitedgroups[i]);
                  }
                  masterVOs=MemberMinister.findfindAssignedDepartmentsVO(newgroups,selectedHouseType,selectedSessionType,year, strlocale);
              }
              catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
          }else{
              String strhouseType=request.getParameter("housetype");
              String stryear=request.getParameter("year");
              String strsessionType=request.getParameter("sessiontype");
              String strlocale=locale.toString();
              String strGroup=request.getParameter("group");
              HouseType selectedHouseType=HouseType.findByName(HouseType.class, strhouseType, strlocale);
              SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", strsessionType, strlocale);
              Integer year=Integer.parseInt(stryear);
              String[] delimitedgroups=strGroup.split(",");
              Integer[] newgroups=new Integer[delimitedgroups.length];
              for(int i=0;i<delimitedgroups.length;i++){
                  newgroups[i]=Integer.parseInt(delimitedgroups[i]);
              }
              masterVOs=MemberMinister.findfindAssignedDepartmentsVO(newgroups,selectedHouseType,selectedSessionType,year, strlocale);
          }
      }
      return masterVOs;
    }

    @RequestMapping(value="/subdepartments")
    public @ResponseBody List<MasterVO> loadSubDepartments(final HttpServletRequest request,final Locale locale){
      CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class, "DEPLOYMENT_SERVER", "");
      List<MasterVO> masterVOs=new ArrayList<MasterVO>();
      List<Group> groups=new ArrayList<Group>();
      if(customParameter!=null){
          String server=customParameter.getValue();
          if(server.equals("TOMCAT")){
              String strhouseType=request.getParameter("housetype");
              String stryear=request.getParameter("year");
              String strsessionType=request.getParameter("sessiontype");
              String strlocale=locale.toString();
              String strGroup=request.getParameter("group");
              try {
                  String houseType=new String(strhouseType.getBytes("ISO-8859-1"),"UTF-8");
                  HouseType selectedHouseType=HouseType.findByName(HouseType.class, houseType, strlocale);
                  String sessionType=new String(strsessionType.getBytes("ISO-8859-1"),"UTF-8");
                  SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", sessionType, strlocale);
                  Integer year=Integer.parseInt(stryear);
                  String[] delimitedgroups=strGroup.split(",");
                  Integer[] newgroups=new Integer[delimitedgroups.length];
                  for(int i=0;i<delimitedgroups.length;i++){
                      newgroups[i]=Integer.parseInt(delimitedgroups[i]);
                  }
                  masterVOs=MemberMinister.findfindAssignedSubDepartmentsVO(newgroups,selectedHouseType,selectedSessionType,year, strlocale);
              }
              catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
          }else{
              String strhouseType=request.getParameter("housetype");
              String stryear=request.getParameter("year");
              String strsessionType=request.getParameter("sessiontype");
              String strlocale=locale.toString();
              String strGroup=request.getParameter("group");
              HouseType selectedHouseType=HouseType.findByName(HouseType.class, strhouseType, strlocale);
              SessionType selectedSessionType=SessionType.findByFieldName(SessionType.class, "sessionType", strsessionType, strlocale);
              Integer year=Integer.parseInt(stryear);
              String[] delimitedgroups=strGroup.split(",");
              Integer[] newgroups=new Integer[delimitedgroups.length];
              for(int i=0;i<delimitedgroups.length;i++){
                  newgroups[i]=Integer.parseInt(delimitedgroups[i]);
              }
              masterVOs=MemberMinister.findfindAssignedSubDepartmentsVO(newgroups,selectedHouseType,selectedSessionType,year, strlocale);
          }
      }
      return masterVOs;
    }
}
