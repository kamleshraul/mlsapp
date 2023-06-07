package org.mkcl.els.mobileApiServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberMobileVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Party;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mobileApiService/memberDetails")
public class MemberDetailsApi {

	
	
	   @RequestMapping(value="/allMembersByHouseType/{houseType}/{locale}")
	    public @ResponseBody List<MemberMobileVO> getAllMembersByHouseType(@PathVariable("houseType") final String houseType, 
	    			@PathVariable("locale") final String locale,
	    			HttpServletRequest request, HttpServletResponse response) throws ELSException{
	    	
	    	List<Member> members = Member.findMembersWithHousetype(houseType, locale);   	
	    	List<MemberMobileVO> mmolist = new ArrayList<MemberMobileVO>();
	    	
	    	for(Member member : members) {
	    		Long memberId = member.getId();
	    		try{
	    			MemberMobileVO mmo = Member.getMemberDataForMobileVo(memberId.longValue(), locale); 	    			
	    			mmolist.add(mmo);
	    			
				} catch(IllegalArgumentException iae){
					System.out.println(memberId);
					iae.printStackTrace();
				}
	    	}
	    	//System.out.println(mmolist.size());    	
	   		
	    	   return mmolist;
	  }
	   
	   @RequestMapping(value="/PartWiseMemberCount/{houseType}/{locale}")
	    public @ResponseBody List<MasterVO> getCountOfAllMembersByHouseType(@PathVariable("houseType") final String houseType, 
	    		@PathVariable("locale") final String locale,	
	    		HttpServletRequest request, HttpServletResponse response) throws ELSException{

			List<MasterVO> partyWiseCount = new ArrayList<MasterVO>();
			// List<House> listOfht = House.findByHouseType(houseType, locale);
			HouseType ht = HouseType.findByType(houseType, locale);
			if (ht != null && ht.getId() != null) {
				House h = House.find(ht, new Date(), locale);
				if (h != null && h.getId() != null) {
					partyWiseCount = Party.getPartyWiseCountOfMemberForMobile(h,locale);
					if (partyWiseCount != null && partyWiseCount.size() > 0) {
						response.setHeader("Access-Control-Allow-Origin", "*");
						return partyWiseCount;
					}
				}
			}
			return null;
	   }
	   
	   @RequestMapping(value="/MemberBday/{houseType}/{isBdayRange}/{locale}")
	   public @ResponseBody List<MasterVO> getMemberWithUpcomingBday(@PathVariable("houseType") final String houseType,
			   	@PathVariable("isBdayRange") final Boolean isBdayRange,@PathVariable("locale") final String locale){
		   
		   
			List<MasterVO> memberBdayDetails = new ArrayList<MasterVO>();
			
			if (isBdayRange != null) {
				HouseType ht = HouseType.findByType(houseType, locale);
				if (ht != null && ht.getId() != null) {
					House h = House.find(ht, new Date(), locale);
					if (h != null && h.getId() != null) {
						if (isBdayRange) {
							CustomParameter parameter =
									CustomParameter.findByName(CustomParameter.class, "BIRTHDAY_RANGE_FOR_MOBILE", "");
							memberBdayDetails = Member.getMemberWithUpcomingBday(h, new Date(),Integer.parseInt( parameter.getValue()), locale);
						} else {
							memberBdayDetails = Member.getMemberWithUpcomingBday(h, new Date(), 0, locale);
						}
					}
				}
			}
		   
		   return  memberBdayDetails;
		   
	   }
	   
	    
}
