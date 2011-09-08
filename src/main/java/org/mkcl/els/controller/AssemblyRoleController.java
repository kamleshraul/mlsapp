/*
******************************************************************
File: org.mkcl.els.controller.AssemblyRoleController.java
Copyright (c) 2011, amitd, MKCL
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

import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IAssemblyRoleService;
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
 * The Class AssemblyRoleController.
 *
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/assembly_roles")
public class AssemblyRoleController extends BaseController{

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The assembly role service. */
	@Autowired
	IAssemblyRoleService assemblyRoleService;
	
	/**
	 * List.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("ASSEMBLY_ROLE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "assembly_roles/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model){
		AssemblyRole assemblyRole = new AssemblyRole();
		model.addAttribute(assemblyRole);
		return "assembly_roles/new";
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
		AssemblyRole assemblyRole = assemblyRoleService.findById(id);
		model.addAttribute(assemblyRole);
		return "assembly_roles/edit";
	}
	
	/**
	 * Creates the.
	 *
	 * @param assemblyRole the assembly role
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("assemblyRole") AssemblyRole assemblyRole, 
			BindingResult result, ModelMap model){
		this.validate(assemblyRole, result);
		
		if(result.hasErrors()){
			model.addAttribute("isvalid", false);
			return "redirect:assembly_roles/new?type=error&msg=create_failed";
		}
		
		assemblyRoleService.create(assemblyRole);		
		return "redirect:assembly_roles/"+assemblyRole.getId()+"?type=success&msg=create_success";
	}
	
	/**
	 * Update.
	 *
	 * @param assemblyRole the assembly role
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("assemblyRole")AssemblyRole assemblyRole, 
			BindingResult result, ModelMap model){
		this.validate(assemblyRole, result);
		
		if(result.hasErrors()){
			model.addAttribute("isvalid", false);
			return "redirect:assembly_roles/"+assemblyRole.getId()+"?type=error&msg=update_failed";
		}
		 
		assemblyRoleService.update(assemblyRole);		
		return "redirect:assembly_roles/"+assemblyRole.getId()+"?type=success&msg=update_success";
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
		assemblyRoleService.removeById(id);	
		return "info";
	}
	
	/**
	 * Validate.
	 *
	 * @param role the role
	 * @param errors the errors
	 */
	private void validate(AssemblyRole role, Errors errors){
		AssemblyRole duplicateRole = assemblyRoleService.findByName(role.getName());
		if(duplicateRole!=null){
			if(!duplicateRole.getId().equals(role.getId())){
				// name attribute of AssemblyRole object must be unique
				errors.rejectValue("name","NonUnique");
			}	
		}
		//Check if the version matches
		if(role.getId()!=null){
			if(!role.getVersion().equals(assemblyRoleService.findById(role.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}
	}
}
