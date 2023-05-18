package org.mkcl.els.mobileApiServices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;

import org.mkcl.els.common.vo.MemberMobileVO;
import org.mkcl.els.domain.Member;
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
	   		response.setHeader("Access-Control-Allow-Origin", "*");
	    	   return mmolist;
	  }
	    
}
