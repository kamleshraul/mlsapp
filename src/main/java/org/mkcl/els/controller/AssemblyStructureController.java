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
	@RequestMapping(method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("ASSEMBLY_STRUCTURE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "assembly_struct/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model){
		AssemblyStructure structure = new AssemblyStructure();
		model.addAttribute(structure);
		return "assembly_struct/new";
	}
	
	/**
	 * Edits the.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		AssemblyStructure structure = assemblyStructureService.findById(id);
		model.addAttribute(structure);
		return "assembly_struct/edit";
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
			model.addAttribute("isvalid", false);
			return "redirect:assembly_struct/new?type=error&msg=create_failed";
		}
		
		assemblyStructureService.create(assemblyStructure);		
		return "redirect:assembly_struct/"+assemblyStructure.getId()+"?type=success&msg=create_success";
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
			model.addAttribute("isvalid", false);
			return "redirect:assembly_struct/"+assemblyStructure.getId()+"?type=error&msg=update_failed";
		}
		 
		assemblyStructureService.update(assemblyStructure);		
		return "redirect:assembly_struct/"+assemblyStructure.getId()+"?type=success&msg=update_success";
	}
	
	/**
	 * Delete.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		assemblyStructureService.removeById(id);	
		return "info";
	}
	
	/**
	 * Validate.
	 *
	 * @param structure the structure
	 * @param errors the errors
	 */
	private void validate(AssemblyStructure structure, Errors errors){
		AssemblyStructure duplicateStructure = 
			assemblyStructureService.findByName(structure.getName());
		
		if(duplicateStructure!=null){
			if(!duplicateStructure.getId().equals(structure.getId())){
				// name attribute of AssemblyStructure object must be unique
				errors.rejectValue("name","NonUnique");
			}	
		}
		//Check if the version matches
		if(structure.getId()!=null){
			if(!structure.getVersion().equals(assemblyStructureService.findById(structure.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}
	}
}
