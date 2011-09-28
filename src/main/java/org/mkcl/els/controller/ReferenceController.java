package org.mkcl.els.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Reference;
import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.service.IConstituencyService;
import org.mkcl.els.service.IDistrictService;
import org.mkcl.els.service.ITehsilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ref")
public class ReferenceController extends BaseController{

	@Autowired
	IDistrictService districtService;
	
	@Autowired
	IConstituencyService constituencyService;
	
	@Autowired
	ITehsilService tehsilService;
	
	
	@RequestMapping(value = "/{state_id}/districts", method=RequestMethod.GET)
	public @ResponseBody List<District> getDistrictsByStateId(@PathVariable("state_id") Long stateId, ModelMap map){
		return districtService.findDistrictsByStateId(stateId);
	}
	
	@RequestMapping(value = "/{district_id}/constituencies", method=RequestMethod.GET)
	public @ResponseBody List<Constituency> getConstituenciesByDistrictId(@PathVariable("district_id") Long districtId, ModelMap map){
		return constituencyService.findConstituenciesByDistrictId(districtId);
	}
	
	@RequestMapping(value = "/{district_id}/tehsils", method=RequestMethod.GET)
	public @ResponseBody List<Tehsil> getTehsilsByDistrictId(@PathVariable("district_id") Long districtId, ModelMap map, HttpServletRequest request){
		return tehsilService.findTehsilsByDistrictId(districtId);
	}
	
	@RequestMapping(value = "/constituencies", method=RequestMethod.GET)
	public String getConstituenciesStartingWith(ModelMap map, HttpServletRequest request){
		List<Reference> constituencies=constituencyService.findConstituenciesStartingWith(request.getParameter("q"));
		map.addAttribute("results",constituencies);
		return "constituencies";
	}
		
	@RequestMapping(value = "/data/{constituency_name}/districts", method=RequestMethod.GET)
	public @ResponseBody Set<District> getDistrictsByConstituencyId(@PathVariable("constituency_name") String constituencyName,ModelMap map, HttpServletRequest request){
		Set<District> districts=districtService.findDistrictsByConstituencyId(constituencyService.findByName(constituencyName).getId());
		return districts;
	}
}
