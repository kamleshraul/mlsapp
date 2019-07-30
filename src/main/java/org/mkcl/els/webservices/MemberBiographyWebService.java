/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MemberBiographyWebService.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.webservices;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.CalenderVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBiographiesVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberBiographyWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ws/biography")
public class MemberBiographyWebService {

    /**
     * Gets the biography.
     *
     * @param id the id
     * @param locale the locale
     * @return the biography
     */
    @RequestMapping(value = "/{id}/{locale}",method=RequestMethod.GET)
    public @ResponseBody MemberBiographyVO getBiography(@PathVariable("id") final long id ,
            @PathVariable("locale") final String locale,
            final HttpServletRequest request){
    	return Member.findBiography(id , locale);
    }

    /**
     * Gets the photo.
     *
     * @param tag the tag
     * @param response the response
     * @return the photo
     */
    @RequestMapping(value="/photo/{tag}")
    public @ResponseBody byte[] getPhoto(@PathVariable("tag")
            final String tag ,
            final HttpServletResponse response){
        Document document;
		try {
			document = Document.findByTag(tag);
			return document.getFileData();
		} catch (ELSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
    }
/*    
    //All members districtwise for website
    @RequestMapping(value="membersforGrav/allMembersDistrictwise/{houseType}/{locale}")
	public @ResponseBody List<MemberBiographyVO> findActiveMembersByDistricts(@PathVariable("houseType") final String houseType,
		@PathVariable("locale")  final Locale locale,
		final HttpServletRequest request,final HttpServletResponse response)throws ELSException{
    	Member.findMembersWithHousetype(houseType, locale.toString());
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MemberBiographyVO> members=new ArrayList<MemberBiographyVO>();
		String districtId = request.getParameter("districtId");
		
		if(customParameter!=null){
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("locale", new String[]{locale.toString()});
			parameters.put("districtId", new String[]{districtId});
			List result = Query.findReport("ACTIVE_MEMBERS_DISTRICTWISE_FORWEBSITE", parameters);
			for(int i=0;i<result.size();i++){
		       	 Object[] row = (Object[])result.get(i);
		       	MemberBiographyVO membersDistrictwise = new MemberBiographyVO();
		       	membersDistrictwise.setFirstName(row[0].toString());
		       	membersDistrictwise.setLastName(row[1].toString());
		       	membersDistrictwise.setConstituency(row[2].toString());
		       	membersDistrictwise.setCountriesVisited(row[3].toString());//District
		       	membersDistrictwise.setEmail(row[4].toString());//Houses
		       	membersDistrictwise.setPhoto(row[5].toString());
		       	membersDistrictwise.setBirthDate(row[6].toString());
		       	membersDistrictwise.setDeathDate(row[7].toString());
				members.add(membersDistrictwise);
			}
		}
		response.setHeader("Access-Control-Allow-Origin", "*");
		return members;
	}*/

    /**
     * This method is added to facilitate Kartik's requirement of
     * being able to print all the Member's Bioprofile in one go.
     * Since this utility is temporary, no effort is made to
     * optimize the query by adding methods to the domain/vo package.
     */
//    @RequestMapping(value="/allMembers/{locale}")
//    public @ResponseBody MemberBiographiesVO getAllMemberBiographies(
//            @PathVariable final String locale){
//        List<Member> members =
//            Member.findAll(Member.class, "lastName", ApplicationConstants.ASC, locale);
//        MemberBiographiesVO biographiesVOs = new MemberBiographiesVO();
//        for(Member member : members){
//            Long memberId = member.getId();
//            try{
//                MemberBiographyVO biographyVO = Member.findBiography(memberId.longValue(), locale);
//                biographiesVOs.getMemberBiographyVOs().add(biographyVO);
//            } catch(IllegalArgumentException iae){
//                System.out.println(memberId);
//                iae.printStackTrace();
//            }
//        }
//        return biographiesVOs;
//    }
    
    @RequestMapping(value="/allMembersByHouseType/{houseType}/{locale}")
    public @ResponseBody MemberBiographiesVO getAllMembersByHouseType(@PathVariable("houseType") final String houseType, 
    			@PathVariable("locale") final String locale,
    			HttpServletRequest request, HttpServletResponse response) throws ELSException{
    		List<Member> members =
    		Member.findMembersWithHousetype(houseType, locale);
    		MemberBiographiesVO biographiesVOs = new MemberBiographiesVO();
    		for(Member member : members){
    			Long memberId = member.getId();
    			try{
    				MemberBiographyVO biographyVO = Member.findBiography(memberId.longValue(), locale);
    				biographiesVOs.getMemberBiographyVOs().add(biographyVO);
    			} catch(IllegalArgumentException iae){
    				System.out.println(memberId);
    				iae.printStackTrace();
    			}
    		}
    	
    		response.setHeader("Access-Control-Allow-Origin", "*");
    	   return biographiesVOs;
  }
    
    //For Grav Website
    //List of members by housetype 
    @RequestMapping(value="biographyforGrav/allMembersByHouseType/{houseType}/{locale}")
    public @ResponseBody MemberBiographiesVO getAllMembersByHouseTypeForGrav(@PathVariable("houseType") final String houseType, 
    			@PathVariable("locale") final String locale,
    			HttpServletRequest request, HttpServletResponse response) throws ELSException{
    		List<Member> members =
    		Member.findMembersWithHousetype(houseType, locale);
    		MemberBiographiesVO biographiesVOs = new MemberBiographiesVO();
    		
    		for(Member member : members){
    			
    			Long memberId = member.getId();
	    			try{
	    				MemberBiographyVO biographyVO = Member.findBiographyForGrav(memberId.longValue(), houseType, locale);
	    				biographiesVOs.getMemberBiographyVOs().add(biographyVO);
	    				
	    			} catch(IllegalArgumentException iae){
	    				System.out.println(memberId);
	    				iae.printStackTrace();
	    			}
    		}
    		response.setHeader("Access-Control-Allow-Origin", "*");
    	   return biographiesVOs;
  }
    
    //All members districtwise for website
    @RequestMapping(value="membersforGrav/allMembersDistrictwise/{houseType}/{locale}")
	public @ResponseBody List<MemberBiographyVO> findActiveMembersByDistricts(@PathVariable("houseType") final String houseType,
		@PathVariable("locale")  final Locale locale,
		final HttpServletRequest request,final HttpServletResponse response)throws ELSException{
    	Member.findMembersWithHousetype(houseType, locale.toString());
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MemberBiographyVO> members=new ArrayList<MemberBiographyVO>();
		String districtId = request.getParameter("districtId");
		
		if(customParameter!=null){
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("locale", new String[]{locale.toString()});
			parameters.put("districtId", new String[]{districtId});
			List result = Query.findReport("ACTIVE_MEMBERS_DISTRICTWISE_FORWEBSITE", parameters);
			for(int i=0;i<result.size();i++){
		       	 Object[] row = (Object[])result.get(i);
		       	MemberBiographyVO membersDistrictwise = new MemberBiographyVO();
		       	membersDistrictwise.setFirstName(row[0].toString());
		       	membersDistrictwise.setLastName(row[1].toString());
		       	membersDistrictwise.setConstituency(row[2].toString());
		       	membersDistrictwise.setCountriesVisited(row[3].toString());//District
		       	membersDistrictwise.setEmail(row[4].toString());//Houses
		       	membersDistrictwise.setPhoto(row[5].toString());
		       	membersDistrictwise.setBirthDate(row[6].toString());
		       	membersDistrictwise.setDeathDate(row[7].toString());
				members.add(membersDistrictwise);
			}
		}
		response.setHeader("Access-Control-Allow-Origin", "*");
		return members;
	}
    
    //List of parties for particular housetype for website
    @RequestMapping(value="membersforGrav/partyListHousewise/{houseType}/{locale}")
   	public @ResponseBody List<MasterVO> findPartiesByHouses(@PathVariable("houseType") final String strHouseType,
   			@PathVariable("locale")  final Locale locale,
   			final HttpServletRequest request,final HttpServletResponse response)throws ELSException{
    	HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
    	House house = House.find(houseType, new Date(), locale.toString());
   		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
   		List<MasterVO> partyList=new ArrayList<MasterVO>();
   		
   		if(customParameter!=null){
   			Map<String, String[]> parameters = new HashMap<String, String[]>();
   			parameters.put("locale", new String[]{locale.toString()});
   			parameters.put("houseid", new String[]{house.getId().toString()});
   			List result = Query.findReport("PARTYLIST_HOUSEWISE_FORWEBSITE", parameters);
   			for(int i=0;i<result.size();i++){
   				Object[] row = (Object[])result.get(i);
   				MasterVO partiesByHouses = new MasterVO();
   				partiesByHouses.setId(Long.parseLong(row[0].toString()));
   		       	partiesByHouses.setName(row[1].toString());
   		      // partiesByHouses.setType(row[2].toString());
   		       	partyList.add(partiesByHouses);
   			}
   		}
   		response.setHeader("Access-Control-Allow-Origin", "*");
   		return partyList;
   	}
    
	//All members partywise for website
    @RequestMapping(value="membersforGrav/allMembersPartytwise/{houseType}/{locale}")
   	public @ResponseBody List<MemberBiographyVO> findActiveMembersByParty(@PathVariable("houseType") final String strHouseType,
   			@PathVariable("locale")  final Locale locale,
   			final HttpServletRequest request,final HttpServletResponse response)throws ELSException{
    	//Member.findMembersWithHousetype(houseType, locale.toString());
    	HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
    	House house = House.find(houseType, new Date(), locale.toString());
   		String partyId = request.getParameter("partyId");
   
   		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
   		List<MemberBiographyVO> members=new ArrayList<MemberBiographyVO>();
   		
   		if(customParameter!=null){
   			Map<String, String[]> parameters = new HashMap<String, String[]>();
   			parameters.put("locale", new String[]{locale.toString()});
   			parameters.put("houseid", new String[]{house.getId().toString()});
   			parameters.put("partyId", new String[]{partyId});
   			List result = Query.findReport("ACTIVE_MEMBERS_PARTYWISE_FORWEBSITE", parameters);
   			for(int i=0;i<result.size();i++){
   		       	 Object[] row = (Object[])result.get(i);
   		       	MemberBiographyVO membersPartywise = new MemberBiographyVO();
   		       	membersPartywise.setId(Long.parseLong(row[0].toString()));
   		       	membersPartywise.setPartyName(row[1].toString());
   		       	membersPartywise.setFirstName(row[2].toString());
   		       	membersPartywise.setLastName(row[3].toString());
   		       	membersPartywise.setPhoto(row[4].toString());
   		       	membersPartywise.setMemberRole(row[5].toString());
   				members.add(membersPartywise);
   			}
   		}
   		response.setHeader("Access-Control-Allow-Origin", "*");
   		return members;
   	}
    
	//All members CalenderEvent for website
    @RequestMapping(value="eventsforGrav/allEvents/{houseType}/{locale}")
   	public @ResponseBody List<CalenderVO> calenderEvents(@PathVariable("houseType") final String strHouseType,
   			@PathVariable("locale")  final Locale locale,
   			final HttpServletRequest request,final HttpServletResponse response)throws ELSException{
   		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
   		List<CalenderVO> events=new ArrayList<CalenderVO>();
   		
   		if(customParameter!=null){
   			Map<String, String[]> parameters = new HashMap<String, String[]>();
   			parameters.put("locale", new String[]{locale.toString()});
   			
   			List result = Query.findReport("CALENDER_EVENTS_COMMITTEE_FORWEBSITE", parameters);
   			for(int i=0;i<result.size();i++){
   		       	 Object[] row = (Object[])result.get(i);
   		       	 CalenderVO calenderEvents = new CalenderVO();
   		       	 calenderEvents.setId(Long.parseLong(row[0].toString()));
   		       	 calenderEvents.setStart(row[1].toString());
   		       	 calenderEvents.setTitle(row[2].toString());
   		       	 calenderEvents.setTime(row[3].toString());
   				events.add(calenderEvents);
   			}
   		}
   		response.setHeader("Access-Control-Allow-Origin", "*");
   		return events;
   	}

}
