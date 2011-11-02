package org.mkcl.els.webservices;

import java.util.List;


import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.service.IConstituencyService;
import org.mkcl.els.service.IMemberDetailsService;
import org.mkcl.els.service.IPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ws/masters")
public class MasterWebService {
	
	@Autowired
	IConstituencyService constituencyService;
	
	@Autowired
	IPartyService partyService;
	
	@Autowired
	IMemberDetailsService memberDetailsService;

	@RequestMapping(value="/constituencies/{locale}")
	public @ResponseBody List<MasterVO> getConstituencies(@PathVariable String locale){
		return constituencyService.findAllSortedVO(locale);
	}
	
	@RequestMapping(value="/parties/{locale}")
	public @ResponseBody List<MasterVO> getParties(@PathVariable String locale){
		return partyService.findAllSortedVO(locale);
	}
	
	@RequestMapping(value="/terms/{locale}")
	public @ResponseBody Integer getNoOfTerms(@PathVariable String locale){
		return memberDetailsService.maxNoOfTerms(locale);
	}
}
