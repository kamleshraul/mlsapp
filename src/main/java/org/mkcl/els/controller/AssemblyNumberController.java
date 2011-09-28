/*
******************************************************************
File: org.mkcl.els.controller.AssemblyNumberController.java
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IAssemblyNumberService;
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

// TODO: Auto-generated Javadoc
/**
 * The Class AssemblyNumberController.
 *
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/assembly_number")
public class AssemblyNumberController extends BaseController {

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The assembly number service. */
	@Autowired
	IAssemblyNumberService assemblyNumberService;
	
	/**
	 * List.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(HttpServletRequest request,ModelMap model) {
		Grid grid = gridService.findByName("ASSEMBLY_NUMBER_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/assembly_number/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(HttpServletRequest request,ModelMap model){
		AssemblyNumber assemblyNumber = new AssemblyNumber();
		model.addAttribute(assemblyNumber);		
		return "masters/assembly_number/new";
	}
	
	/**
	 * Edits the.
	 *
	 * @param id the id
	 * @param model the model
	 * @param type the type
	 * @param msg the msg
	 * @return the string
	 */
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(HttpServletRequest request,@PathVariable Long id, ModelMap model){
		AssemblyNumber assemblyNumber = assemblyNumberService.findById(id);
		model.addAttribute(assemblyNumber);		
		return "masters/assembly_number/edit";
	}
	
	/**
	 * Creates the.
	 *
	 * @param assemblyNumber the assembly number
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("assemblyNumber") AssemblyNumber assemblyNumber, 
			BindingResult result, ModelMap model){
		this.validate(assemblyNumber, result);		
		if(result.hasErrors()){
			model.addAttribute("assemblyNumber",assemblyNumber);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/assembly_number/new";
		}		
		assemblyNumberService.create(assemblyNumber);
		return "redirect:assembly_number/"+assemblyNumber.getId()+"/edit?type=success&msg=create_success";
	
	}
	
	/**
	 * Update.
	 *
	 * @param assemblyNumber the assembly number
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("assemblyNumber")AssemblyNumber assemblyNumber, 
			BindingResult result, ModelMap model){
		this.validate(assemblyNumber, result);		
		if(result.hasErrors()){
			model.addAttribute("assemblyNumber",assemblyNumber);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/assembly_number/edit";
		}
		 
		assemblyNumberService.update(assemblyNumber);
		return "redirect:assembly_number/"+assemblyNumber.getId()+"/edit?type=success&msg=update_success";
	}
	
	/**
	 * Delete.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model,HttpServletRequest request){
		assemblyNumberService.removeById(id);	
		return "info";	
		}
	
	/**
	 * Validate.
	 *
	 * @param assemblyNumber the assembly number
	 * @param errors the errors
	 */
	private void validate(AssemblyNumber assemblyNumber, Errors errors){
		if(assemblyNumber.getAssemblyNo()!=null){
			if(assemblyNumber.getLocale().equals("en")){
				String number=assemblyNumber.getAssemblyNo();
				if(number.length()>100 || number.length()<1){
					errors.rejectValue("assemblyNo","Size");

				}
			}		
		}

		AssemblyNumber duplicateNumber = 
			assemblyNumberService.findByAssemblyNo(assemblyNumber.getAssemblyNo());
		
		if(duplicateNumber!=null){
			if(!duplicateNumber.getId().equals(assemblyNumber.getId())){
				// assemblyNo attribute of AssemblyNumber object must be unique
				errors.rejectValue("assemblyNo","NonUnique");
			}	
		}
		//Check if the version matches
		if(assemblyNumber.getId()!=null){
			if(!assemblyNumber.getVersion().equals(assemblyNumberService.findById(assemblyNumber.getId()).getVersion())){
				errors.rejectValue("assemblyNo","Version_Mismatch");

			}
		}
	}
}
