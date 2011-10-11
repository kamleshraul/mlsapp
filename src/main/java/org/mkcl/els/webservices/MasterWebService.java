package org.mkcl.els.webservices;

import java.util.List;


import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Party;
import org.mkcl.els.service.IConstituencyService;
import org.mkcl.els.service.IPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ws/masters")
public class MasterWebService {
	
	@Autowired
	IConstituencyService constituencyService;
	
	@Autowired
	IPartyService partyService;

	@RequestMapping(value="/constituencies")
	public @ResponseBody List<MasterVO> getConstituencies(){
		return constituencyService.findAllSortedVO();
	}
	
	@RequestMapping(value="/parties")
	public @ResponseBody List<MasterVO> getParties(){
		return partyService.findAllSortedVO();
	}
}
