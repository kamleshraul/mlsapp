package org.mkcl.els.controller;

import javax.validation.Valid;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Title;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.ITitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/titles")
public class TitleController {

	@Autowired
	IGridService gridService;
	
	@Autowired
	ITitleService titleService;
	
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("TITLE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/titles/list";
	}
	
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model){
		Title title = new Title();
		model.addAttribute("title",title);
		return "masters/titles/new";
	}
	
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		Title title = titleService.findById(id);
		model.addAttribute(title);
		return "masters/titles/edit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("title") Title title, 
			BindingResult result, ModelMap model){
		this.validate(title, result);		
		if(result.hasErrors()){
			model.addAttribute("title",title);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/titles/new";
		}
		
		titleService.create(title);
		return "redirect:titles/"+title.getId()+"/edit?type=success&msg=create_success";
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("title") Title title, 
			BindingResult result, ModelMap model){
		this.validate(title, result);		
		if(result.hasErrors()){
			model.addAttribute("title",title);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/titles/edit";
		}
		
		titleService.create(title);
		return "redirect:titles/"+title.getId()+"/edit?type=success&msg=create_success";
	}
	
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		titleService.removeById(id);	
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";	
		}

	private void validate(Title title, Errors errors) {
		Title duplicateNumber = 
			titleService.findByName(title.getName());
		
		if(duplicateNumber!=null){
			if(!duplicateNumber.getId().equals(title.getId())){
				// assemblyNo attribute of assemblyTerm object must be unique
				errors.rejectValue("name","NonUnique");
			}	
		}
		//Check if the version matches
		if(title.getId()!=null){
			if(!title.getVersion().equals(titleService.findById(title.getId()).getVersion())){
				errors.rejectValue("name","Version_Mismatch");

			}
		}
		
	}
}
