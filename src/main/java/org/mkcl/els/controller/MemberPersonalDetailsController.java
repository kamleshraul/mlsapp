package org.mkcl.els.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.mkcl.els.common.editors.ConstituencyEditor;
import org.mkcl.els.common.editors.PartyEditor;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Field;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.Party;
import org.mkcl.els.service.IConstituencyService;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IDistrictService;
import org.mkcl.els.service.IDocumentService;
import org.mkcl.els.service.IFieldService;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMemberDetailsService;
import org.mkcl.els.service.IPartyService;
import org.mkcl.els.service.IStateService;
import org.mkcl.els.service.ITehsilService;
import org.mkcl.els.service.ITitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/member_personal_details")
public class MemberPersonalDetailsController {
	
		
	@Autowired
	IGridService gridService;
	
	@Autowired
	ICustomParameterService customParameterService;
	
	@Autowired
	IStateService stateService;
	
	@Autowired
	IDistrictService districtService;
	
	@Autowired
	ITehsilService tehsilService;
	
	@Autowired
	IConstituencyService constituencyService;
	
	@Autowired
	IPartyService partyService;
	
	@Autowired
	IMemberDetailsService memberDetailsService;
	
	@Autowired
	ITitleService titleService;
	
	@Autowired
	IFieldService fieldService;
	
	@Autowired
	IDocumentService documentService;

	@RequestMapping(value="list",method = RequestMethod.GET)
	public String index(ModelMap model){
		Grid grid = gridService.findByName("MEMBER_DETAIL_GRID");		
		model.addAttribute("gridId", grid.getId());
		return "member_details/personal/list";
	}
	
	@RequestMapping(value="new",method=RequestMethod.GET)
	public String _new(ModelMap model,Error errors,Locale locale){
		MemberDetails memberPersonalDetails=new MemberDetails();
		memberPersonalDetails.setLocale(locale.toString());
		populateModel(model,memberPersonalDetails);	
		model.addAttribute("photoExt", customParameterService.findByName("PHOTO_EXTENSION").getValue());
		model.addAttribute("photoSize", Long.parseLong(customParameterService.findByName("PHOTO_SIZE").getValue())*1024*1024);
		return "member_details/personal/new";		
	}
	
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(HttpServletRequest request,@PathVariable Long id, ModelMap model){
		MemberDetails memberPersonalDetails = memberDetailsService.findById(id);
		if(memberPersonalDetails.getConstituency()!=null){
		Set<District> districts=memberPersonalDetails.getConstituency().getDistricts();
		StringBuffer buffer=new StringBuffer();
		String state=districts.iterator().next().getState().getName();
		for(District i:districts){
			if(i!=null){
				buffer.append(i.getName()+",");
			}
		}
		buffer.deleteCharAt(buffer.length()-1);
		model.addAttribute("constituency",memberPersonalDetails.getConstituency());
		model.addAttribute("district",buffer.toString());
		model.addAttribute("state",state);
		}
		populateModel(model, memberPersonalDetails);
		model.addAttribute("photoExt", customParameterService.findByName("PHOTO_EXTENSION").getValue());
		model.addAttribute("photoSize", Long.parseLong(customParameterService.findByName("PHOTO_SIZE").getValue())*1024*1024);
		Document document=documentService.findByTag(memberPersonalDetails.getPhoto());
		if(document!=null){
			model.addAttribute("photoOriginalName", document.getOriginalFileName());

		}
		return "member_details/personal/edit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("memberPersonalDetails") MemberDetails memberPersonalDetails,BindingResult result, ModelMap model,@RequestParam(required=false) String constituencies,@RequestParam(required=false) String district,@RequestParam(required=false) String state,HttpServletRequest request){
		if(constituencies!=null||!constituencies.isEmpty()){
		memberPersonalDetails.setConstituency(constituencyService.findByName(constituencies));
		}
		this.validate(memberPersonalDetails,result);
		if(result.hasErrors()){
			populateModel(model,memberPersonalDetails);
			model.addAttribute("district",district);
			model.addAttribute("state",state);
			model.addAttribute("constituency",memberPersonalDetails.getConstituency());
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "member_details/personal/new";
		}
		memberDetailsService.create(memberPersonalDetails);
		request.getSession().setAttribute("refresh","");
		if(customParameterService.findByName("MIS_PROGRESSIVE_DISPLAY").getValue().equals("PROGRESSIVE")){
			return "redirect:/member_contact_details/"+memberPersonalDetails.getId()+"/edit?type=success&msg=create_success";
		}
		else{
			return "redirect:member_personal_details/"+memberPersonalDetails.getId()+"/edit?type=success&msg=create_success";
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String edit(HttpServletRequest request,@Valid @ModelAttribute("memberPersonalDetails") MemberDetails memberPersonalDetails,BindingResult result, ModelMap model,@RequestParam(required=false) String constituencies,@RequestParam(required=false) String district,@RequestParam(required=false) String state ){
		if(constituencies!=null||!constituencies.isEmpty()){
			memberPersonalDetails.setConstituency(constituencyService.findByName(constituencies));
		}
		this.validate(memberPersonalDetails,result);
		if(result.hasErrors()){
			populateModel(model,memberPersonalDetails);
			model.addAttribute("district",district);
			model.addAttribute("state",state);
			model.addAttribute("constituency",memberPersonalDetails.getConstituency());
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "member_details/personal/edit";
		}	
			memberDetailsService.updateMemberPersonalDetails(memberPersonalDetails);
			request.getSession().setAttribute("refresh","");
			if(customParameterService.findByName("MIS_PROGRESSIVE_DISPLAY").getValue().equals("PROGRESSIVE")){
				return "redirect:/member_contact_details/"+memberPersonalDetails.getId()+"/edit?type=success&msg=update_success";
			}
			else{
				return "redirect:member_personal_details/"+memberPersonalDetails.getId()+"/edit?type=success&msg=update_success";
			}		
	}	
	
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model,HttpServletRequest request){
		memberDetailsService.removeById(id);
		return "info";		
	}
	
	@InitBinder 
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(customParameterService.findByName("SERVER_DATEFORMAT").getValue()); 
		dateFormat.setLenient(true); 
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Constituency.class, new ConstituencyEditor(constituencyService)); 
		binder.registerCustomEditor(Party.class, new PartyEditor(partyService)); 

	}
	
	private void validate(MemberDetails memberPersonalDetails,
			Errors errors) {
		
	}
	
	private void populateModel(ModelMap model,
			MemberDetails memberPersonalDetails) {
		List<Field> fieldsCollection=fieldService.findAll();
		Map<String,Field> fields=new HashMap<String, Field>();
		for(Field i:fieldsCollection){
			fields.put(i.getName(),i);
		}
		model.addAttribute("fields",fields);
		model.addAttribute("titles", titleService.findAllSorted());
		model.addAttribute("parties", partyService.findAllSorted());
		model.addAttribute("memberPersonalDetails",memberPersonalDetails);		
	}
	
	
}
