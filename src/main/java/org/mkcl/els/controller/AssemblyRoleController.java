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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestParam;

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
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("ASSEMBLY_ROLE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/assembly_roles/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model,HttpServletRequest request){
		AssemblyRole assemblyRole = new AssemblyRole();
		model.addAttribute(assemblyRole);		
		return "masters/assembly_roles/new";
	}
	
	/**
	 * Edits the.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(HttpServletRequest request,@PathVariable Long id, ModelMap model){
		AssemblyRole assemblyRole = assemblyRoleService.findById(id);
		model.addAttribute("assemblyRole",assemblyRole);		
		return "masters/assembly_roles/edit";
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
			model.addAttribute("assemblyRole",assemblyRole);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/assembly_roles/new";

		}
		
		assemblyRoleService.create(assemblyRole);
		return "redirect:assembly_roles/"+assemblyRole.getId()+"/edit?type=success&msg=create_success";

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
			model.addAttribute("assemblyRole",assemblyRole);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/assembly_roles/edit";
		}		 
		assemblyRoleService.update(assemblyRole);	
		return "redirect:assembly_roles/"+assemblyRole.getId()+"/edit?type=success&msg=update_success";
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
		assemblyRoleService.removeById(id);	
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";
		}
	
	/**
	 * Validate.
	 *
	 * @param role the role
	 * @param errors the errors
	 */
	private void validate(AssemblyRole assemblyRole, Errors errors){
		if(assemblyRole.getName()!=null){
			if(assemblyRole.getLocale().equals("en")){
				String name=assemblyRole.getName();
				Pattern pattern=Pattern.compile("[A-Za-z ]{1,100}");
				Matcher matcher=pattern.matcher(name);
				if(!matcher.matches()){
					errors.rejectValue("name","Pattern");
				}
				if(name.length()>100 || name.length()<1){
					errors.rejectValue("name","Size");

				}
			}		
		AssemblyRole duplicateRole = assemblyRoleService.findByName(assemblyRole.getName());
		if(duplicateRole!=null){
			if(!duplicateRole.getId().equals(assemblyRole.getId())){
				errors.rejectValue("name","NonUnique");
			}	
		}
		if(assemblyRole.getId()!=null){
			if(!assemblyRole.getVersion().equals(assemblyRoleService.findById(assemblyRole.getId()).getVersion())){
				errors.rejectValue("name","Version_Mismatch");
			}
		}
	}
	}
}

