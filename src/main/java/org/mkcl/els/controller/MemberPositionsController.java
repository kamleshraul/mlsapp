package org.mkcl.els.controller;

import org.mkcl.els.service.IMemberPositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/member_position_details")
public class MemberPositionsController {
	@Autowired
	IMemberPositionsService memberPositionsService;	
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		memberPositionsService.removeById(id);
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";		
	}

}
