/*
******************************************************************
File: org.mkcl.els.controller.AssemblyController.java
Copyright (c) 2011, sandeeps, MKCL
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

import org.mkcl.els.common.editors.AssemblyNumberEditor;
import org.mkcl.els.common.editors.AssemblyStructureEditor;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.domain.AssemblyStructure;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IAssemblyNumberService;
import org.mkcl.els.service.IAssemblyService;
import org.mkcl.els.service.IAssemblyStructureService;
import org.mkcl.els.service.IGridService;
import org.springframework.beans.factory.annotation.Autowired;
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

// TODO: Auto-generated Javadoc
/**
 * The Class AssemblyController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/assemblies")
public class AssemblyController extends BaseController{

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The assembly structure service. */
	@Autowired
	IAssemblyStructureService assemblyStructureService;
	
	/** The assembly number service. */
	@Autowired
	IAssemblyNumberService assemblyNumberService;
	
	/** The assembly service. */
	@Autowired
	IAssemblyService assemblyService;
	
	/**
	 * Index.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String index(ModelMap model){
		Grid grid = gridService.findByName("ASSEMBLY_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/assemblies/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @param errors the errors
	 * @return the string
	 */
	@RequestMapping(value="new",method=RequestMethod.GET)
	public String _new(ModelMap model,Error errors,HttpServletRequest request){
		Assembly assembly=new Assembly();
		populateModel(model,assembly);			
		return "masters/assemblies/new";
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
		Assembly assembly=assemblyService.findById(id);
		populateModel(model, assembly);			
		return "masters/assemblies/edit";		
	}
	
	/**
	 * Creates the.
	 *
	 * @param assembly the assembly
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("assembly") Assembly assembly,BindingResult result, ModelMap model){
		this.validate(assembly,result);
		if(result.hasErrors()){
			populateModel(model, assembly);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/assemblies/new";
		}	
		assemblyService.create(assembly);
		return "redirect:assemblies/"+assembly.getId()+"/edit?type=success&msg=create_success";
		
	}
	
	/**
	 * Update.
	 *
	 * @param assembly the assembly
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid @ModelAttribute("assembly") Assembly assembly,BindingResult result, ModelMap model){
		this.validate(assembly,result);
		if(result.hasErrors()){
			populateModel(model, assembly);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/assemblies/edit";
		}	
		assemblyService.update(assembly);
		return "redirect:assemblies/"+assembly.getId()+"/edit?type=success&msg=update_success";
	}
	
	/**
	 * Delete.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(HttpServletRequest request,@PathVariable Long id, ModelMap model){
		assemblyService.removeById(id);
		return "info";			
	}
	
	/**
	 * Validate.
	 *
	 * @param assembly the assembly
	 * @param errors the errors
	 */
	private void validate(Assembly assembly, Errors errors) {
		if(assembly.getTerm()!=null){
			if(assembly.getLocale().equals("en")){
				String term=assembly.getTerm();
				if(term.length()>20 || term.length()<1){
					errors.rejectValue("term","Size");

				}
			}		
		}
		Assembly duplicateParameter = assemblyService.findByAssemblyNumber(assembly.getAssemblyNumber());
		if(duplicateParameter!=null){
			if(!duplicateParameter.getId().equals(assembly.getId())){
				errors.rejectValue("assemblyNumber","NonUnique");
			}	
		}
		if(assembly.getId()!=null){
			if(!assembly.getVersion().equals(assemblyService.findById(assembly.getId()).getVersion())){
				errors.reject("assemblyNumber","Version_Mismatch");
			}
		}
	}
	
	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder 
	public void initBinder(WebDataBinder binder) { 
		binder.registerCustomEditor(AssemblyStructure.class, new AssemblyStructureEditor(assemblyStructureService)); 
		binder.registerCustomEditor(AssemblyNumber.class, new AssemblyNumberEditor(assemblyNumberService)); 
	}

	/**
	 * Populate model.
	 *
	 * @param model the model
	 * @param assembly the assembly
	 */
	private void populateModel(ModelMap model,Assembly assembly){
		model.addAttribute("assemblyStructures",assemblyStructureService.findAllSortedByName());		
		model.addAttribute("assemblyNumbers",assemblyNumberService.findAllSortedByNumber());
		model.addAttribute("assembly",assembly);
	}
}
