package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.DistrictEditor;
import org.mkcl.els.common.editors.StateEditor;
import org.mkcl.els.common.editors.TehsilEditor;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.State;
import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IDistrictService;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMemberDetailsService;
import org.mkcl.els.service.IStateService;
import org.mkcl.els.service.ITehsilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/member_contact_details")
public class MemberContactDetailsController {
	
	@Autowired
	IGridService gridService;
	
	@Autowired
	IMemberDetailsService memberDetailsService;
	
	@Autowired
	IStateService stateService;
	
	@Autowired
	IDistrictService districtService;
	
	@Autowired
	ITehsilService tehsilService;
	
	@Autowired
	ICustomParameterService customParameterService;
	
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(HttpServletRequest request,@PathVariable Long id, ModelMap model){
		MemberDetails memberContactDetails = memberDetailsService.findById(id);
		populateModel(model, memberContactDetails);	
		request.getSession().setAttribute("refresh","");
		return "member_details/contact/edit";
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String edit(@Valid @ModelAttribute("memberContactDetails") MemberDetails memberContactDetails,BindingResult result, ModelMap model,@RequestParam(required=false) String state){
		this.validate(memberContactDetails,result);
		if(result.hasErrors()){
			populateModel(model, memberContactDetails);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "member_details/contact/edit";
		}	
		memberDetailsService.updateMemberContactDetails(memberContactDetails);
		if(customParameterService.findByName("MIS_PROGRESSIVE_DISPLAY").getValue().toLowerCase().equals("progressive")){
			return "redirect:/member_other_details/"+memberContactDetails.getId()+"/edit?type=success&msg=update_success";
		}
		else{
			return "redirect:member_contact_details/"+memberContactDetails.getId()+"/edit?type=success&msg=create_success";
		}

	}

	private void validate(MemberDetails memberContactDetails,
			BindingResult result) {
		// TODO Auto-generated method stub
		
	}
	
	@InitBinder 
	public void initBinder(WebDataBinder binder) { 
		binder.registerCustomEditor(State.class, new StateEditor(stateService));
		binder.registerCustomEditor(District.class, new DistrictEditor(districtService)); 
		binder.registerCustomEditor(Tehsil.class, new TehsilEditor(tehsilService)); 

	}

	private void populateModel(ModelMap model,
			MemberDetails memberContactDetails) {
		List<State> states=stateService.findAllSorted();
		model.addAttribute("presentStates",states);		
		model.addAttribute("permanentStates",states);
		model.addAttribute("memberContactDetails", memberContactDetails);
		
	}
}
