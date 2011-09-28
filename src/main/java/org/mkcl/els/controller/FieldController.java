package org.mkcl.els.controller;

import javax.validation.Valid;

import org.mkcl.els.domain.Field;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IFieldService;
import org.mkcl.els.service.IGridService;
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
@RequestMapping("/fields")
public class FieldController {

	@Autowired
	IGridService gridService;
	
	@Autowired
	IFieldService fieldService;
	
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String index(ModelMap model){
		Grid grid = gridService.findByName("FIELD_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/fields/list";
	}
	
	@RequestMapping(value="new",method=RequestMethod.GET)
	public String _new(ModelMap model,Error errors){
		Field field=new Field();
		model.addAttribute("field",field);
		return "masters/fields/new";
	}
	
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		Field field=fieldService.findById(id);
		model.addAttribute("field",field);
		return "masters/fields/edit";		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("field") Field field,BindingResult result, ModelMap model){
		this.validate(field,result);
		if(result.hasErrors()){
			model.addAttribute("field",field);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/fields/new";
		}	
		fieldService.create(field);
		return "redirect:fields/"+field.getId()+"/edit?type=success&msg=create_success";
		
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid @ModelAttribute("field") Field field,BindingResult result, ModelMap model){
		this.validate(field,result);
		if(result.hasErrors()){
			model.addAttribute("field",field);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/fields/edit";
		}	
		fieldService.create(field);
		return "redirect:fields/"+field.getId()+"/edit?type=success&msg=create_success";
		
	}
	
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		fieldService.removeById(id);
		model.addAttribute("msg","delete_success");
		model.addAttribute("type","success");
		return "info";			
	}	

	private void validate(Field field, Errors errors) {
		if((field.getMandatory().equals("MANDATORY"))&&(field.getVisible().equals("HIDDEN"))){
			errors.rejectValue("visible","NonVisible");
		}
	}
}
