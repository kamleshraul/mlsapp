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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.Airport;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Division;
import org.mkcl.els.domain.RailwayStation;
import org.mkcl.els.domain.Reference;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.repository.DistrictRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
}
