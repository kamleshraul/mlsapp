/*
******************************************************************
File: org.mkcl.els.controller.AssemblyStructureController.java
Copyright (c) 2011, amitd, ${company}
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.controller;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.AssemblyStructure;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IAssemblyStructureService;
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

/**
 * The Class AssemblyStructureController.
 *
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/assembly_struct")
public class AssemblyStructureController extends BaseController {
	
	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The assembly structure service. */
	@Autowired
	IAssemblyStructureService assemblyStructureService;
	
	/**
	 * List.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("ASSEMBLY_STRUCTURE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/assembly_struct/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model,Locale locale){
		AssemblyStructure structure = new AssemblyStructure();
		structure.setLocale(locale.toString());
		model.addAttribute(structure);		
		return "masters/assembly_struct/new";
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
		AssemblyStructure structure = assemblyStructureService.findById(id);
		model.addAttribute(structure);		
		return "masters/assembly_struct/edit";
	}
	
	/**
	 * Creates the.
	 *
	 * @param structure the structure
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("assemblyStructure") AssemblyStructure assemblyStructure, 
			BindingResult result, ModelMap model){
		this.validate(assemblyStructure, result);		
		if(result.hasErrors()){
			model.addAttribute("assemblyStructure",assemblyStructure);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/assembly_struct/new";
		}
		
		assemblyStructureService.create(assemblyStructure);			
		return "redirect:assembly_struct/"+assemblyStructure.getId()+"/edit?type=success&msg=create_success";
		}
	
	/**
	 * Update.
	 *
	 * @param structure the structure
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("assemblyStructure")AssemblyStructure assemblyStructure, 
			BindingResult result, ModelMap model){
		this.validate(assemblyStructure, result);
		
		if(result.hasErrors()){
			model.addAttribute("assemblyStructure",assemblyStructure);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/assembly_struct/edit";
		}
		 
		assemblyStructureService.update(assemblyStructure);
		return "redirect:assembly_struct/"+assemblyStructure.getId()+"/edit?type=success&msg=update_success";
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
		assemblyStructureService.removeById(id);	
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";
		}
	
	/**
	 * Validate.
	 *
	 * @param structure the structure
	 * @param errors the errors
	 */
	private void validate(AssemblyStructure assemblyStructure, Errors errors){
		if(assemblyStructure.getName()!=null){
			if(assemblyStructure.getLocale().equals("en")){
				String name=assemblyStructure.getName();
				Pattern pattern=Pattern.compile("[A-Za-z ]{1,100}");
				Matcher matcher=pattern.matcher(name);
				if(!matcher.matches()){
					errors.rejectValue("name","Pattern");
				}
				if(name.length()>100 || name.length()<1){
					errors.rejectValue("name","Size");

				}
			}		
		}

		AssemblyStructure duplicateStructure = 
			assemblyStructureService.findByName(assemblyStructure.getName());
		
		if(duplicateStructure!=null){
			if(!duplicateStructure.getId().equals(assemblyStructure.getId())){
				// name attribute of AssemblyStructure object must be unique
				errors.rejectValue("name","NonUnique");
			}	
		}
		//Check if the version matches
		if(assemblyStructure.getId()!=null){
			if(!assemblyStructure.getVersion().equals(assemblyStructureService.findById(assemblyStructure.getId()).getVersion())){
				errors.rejectValue("name","Version_Mismatch");
			}
		}
	}
}
