package org.mkcl.els.webservices;

import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.service.IMemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ws/membersearch")
public class MemberSearchWebService {
	
	@Autowired
	IMemberDetailsService memberDetailsService;
	
	@RequestMapping(value="/{criteria1}/{locale}")
	public @ResponseBody MemberSearchPage searchSingle(@PathVariable String criteria1,@PathVariable String locale){
		return memberDetailsService.searchMemberDetails(criteria1, locale);		
	}
	
	@RequestMapping(value="/{criteria1}/{page}/{rows}/{locale}")
	public @ResponseBody MemberSearchPage searchSinglePagination(@PathVariable String criteria1,@PathVariable Integer page,@PathVariable Integer rows,@PathVariable String locale){
		return memberDetailsService.searchMemberDetails(criteria1, page, rows, locale);		
	}
	
	@RequestMapping(value="/{criteria1}/{criteria2}/{locale}")
	public @ResponseBody MemberSearchPage searchDouble(@PathVariable String criteria1,@PathVariable String criteria2,@PathVariable String locale){
		return memberDetailsService.searchMemberDetails(criteria1, criteria2, locale);		
	}
	
	@RequestMapping(value="/{criteria1}/{criteria2}/{page}/{rows}/{locale}")
	public @ResponseBody MemberSearchPage searchDoublePagination(@PathVariable String criteria1,@PathVariable String criteria2,@PathVariable Integer page,@PathVariable Integer rows,@PathVariable String locale){
		return memberDetailsService.searchMemberDetails(criteria1, criteria2, page, rows, locale);		
	}
	


}
