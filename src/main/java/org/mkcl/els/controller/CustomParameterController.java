/*
******************************************************************
File: org.mkcl.els.controller.CustomParameterController.java
Copyright (c) 2011, vishals, MKCL
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

import javax.validation.Valid;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.ICustomParameterService;
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
 * The Class CustomParameterController.
 *
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/custom_params")
public class CustomParameterController extends BaseController
{
	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The custom parameter service. */
	@Autowired
	ICustomParameterService customParameterService;
	
	/**
	 * Lists all custom parameters.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("CUSTOM_PARAMETER_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/custom_params/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model,Locale locale){
		CustomParameter customParameter = new CustomParameter();
		model.addAttribute(customParameter);
		return "masters/custom_params/new";
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
		CustomParameter customParameter = customParameterService.findById(id);
		model.addAttribute(customParameter);
		return "masters/custom_params/edit";
	}
	
	/**
	 * Creates a new custom parameter.
	 *
	 * @param customParameter the custom parameter
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("customParameter") CustomParameter customParameter, 
			BindingResult result, ModelMap model){
		this.validate(customParameter, result);
		
		if(result.hasErrors()){
			model.addAttribute(customParameter);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/custom_params/new";
		}
		
		customParameterService.create(customParameter);	
		return "redirect:custom_params/"+customParameter.getId()+"/edit?type=success&msg=create_success";

	}
	
	/**
	 * Updates the custom parameter.
	 *
	 * @param customParameter the custom parameter
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("customParameter")CustomParameter customParameter, 
			BindingResult result, ModelMap model){
		this.validate(customParameter, result);
		
		if(result.hasErrors()){
			model.addAttribute(customParameter);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
		    return "masters/custom_params/edit";
		}
		 
		customParameterService.update(customParameter);
		return "redirect:custom_params/"+customParameter.getId()+"/edit?type=success&msg=update_success";

	}
	
	/**
	 * Deletes an existing custom parameter.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		customParameterService.removeById(id);	
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";
		}
	
	/**
	 * Custom Validation.
	 *
	 * @param customParameter the custom parameter
	 * @param errors the errors
	 */
	private void validate(CustomParameter customParameter, Errors errors){
		CustomParameter duplicateParameter = customParameterService.findByName(customParameter.getName());
		if(duplicateParameter!=null){
			if(!duplicateParameter.getId().equals(customParameter.getId())){
				// name attribute of CustomParameter object must be unique
				errors.rejectValue("name","NonUnique");
			}	
		}
		//Check if the version matches
		if(customParameter.getId()!=null){
			if(!customParameter.getVersion().equals(customParameterService.findById(customParameter.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}
	}
}
