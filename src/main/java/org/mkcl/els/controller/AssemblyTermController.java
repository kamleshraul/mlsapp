package org.mkcl.els.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.AssemblyTerm;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IAssemblyTermService;
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
@RequestMapping("/assembly_terms")
public class AssemblyTermController {
	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The assembly number service. */
	@Autowired
	IAssemblyTermService assemblyTermService;
	
	/**
	 * List.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("ASSEMBLY_TERM_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/assembly_terms/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model,Locale locale){
		AssemblyTerm assemblyTerm = new AssemblyTerm();
		assemblyTerm.setLocale(locale.toString());
		model.addAttribute("assemblyTerm",assemblyTerm);		
		return "masters/assembly_terms/new";
	}
	
	/**
	 * Edits the.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		AssemblyTerm assemblyTerm = assemblyTermService.findById(id);
		model.addAttribute(assemblyTerm);
		return "masters/assembly_terms/edit";
	}
	
	/**
	 * Creates the.
	 *
	 * @param assemblyTerm the assembly number
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("assemblyTerm") AssemblyTerm assemblyTerm, 
			BindingResult result, ModelMap model){
		this.validate(assemblyTerm, result);		
		if(result.hasErrors()){
			model.addAttribute("assemblyTerm",assemblyTerm);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/assembly_terms/new";
		}
		
		assemblyTermService.create(assemblyTerm);
		return "redirect:assembly_terms/"+assemblyTerm.getId()+"/edit?type=success&msg=create_success";
	}
	
	/**
	 * Update.
	 *
	 * @param assemblyTerm the assembly number
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("assemblyTerm")AssemblyTerm assemblyTerm, 
			BindingResult result, ModelMap model){
		this.validate(assemblyTerm, result);
		
		if(result.hasErrors()){
			model.addAttribute("assemblyTerm",assemblyTerm);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/assembly_terms/edit";
		}		 
		assemblyTermService.update(assemblyTerm);
		return "redirect:assembly_terms/"+assemblyTerm.getId()+"/edit?type=success&msg=update_success";
	}
	
	/**
	 * Delete.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		assemblyTermService.removeById(id);	
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";	
		}
	
	/**
	 * Validate.
	 *
	 * @param assemblyTerm the assembly number
	 * @param errors the errors
	 */
	private void validate(AssemblyTerm assemblyTerm, Errors errors){
		AssemblyTerm duplicateNumber = 
			assemblyTermService.findByAssemblyTerm(assemblyTerm.getTerm());
		
		if(duplicateNumber!=null){
			if(!duplicateNumber.getId().equals(assemblyTerm.getId())){
				// assemblyNo attribute of assemblyTerm object must be unique
				errors.rejectValue("term","NonUnique");
			}	
		}
		//Check if the version matches
		if(assemblyTerm.getId()!=null){
			if(!assemblyTerm.getVersion().equals(assemblyTermService.findById(assemblyTerm.getId()).getVersion())){
				errors.rejectValue("term","Version_Mismatch");

			}
		}
	}
}
