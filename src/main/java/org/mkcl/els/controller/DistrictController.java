/*
******************************************************************
File: org.mkcl.els.controller.DistrictController.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.mkcl.els.common.editors.StateEditor;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.State;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IDistrictService;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IStateService;
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
 * The Class DistrictController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/districts")
public class DistrictController extends BaseController{

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The state service. */
	@Autowired
	IStateService stateService;
	
	@Autowired
	IDistrictService districtService;
	
	@Autowired
	ICustomParameterService customParameterService;
	/**
	 * Index.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model){
		Grid grid = gridService.findByName("DISTRICT_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/districts/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="new",method=RequestMethod.GET)
	public String _new(ModelMap model){	
		District district=new District();
		populateModel(model, district, customParameterService.findByName("DEFAULT_STATE").getValue());		
		return "masters/districts/new";
	}
	
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		District district = districtService.findById(id);
		populateModel(model, district,district.getState().getName());
		return "masters/districts/edit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("district") District district,BindingResult result, ModelMap model){
		this.validate(district,result);
		if(result.hasErrors()){
			populateModel(model, district,district.getState().getName());
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/districts/new";
		}	
		districtService.create(district);
		return "redirect:districts/"+district.getId()+"/edit?type=success&msg=create_success";

		
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String edit(@Valid @ModelAttribute("district") District district,BindingResult result, ModelMap model){
		this.validate(district,result);
		if(result.hasErrors()){
			populateModel(model, district,district.getState().getName());
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/districts/edit";
		}	
		districtService.update(district);
		return "redirect:districts/"+district.getId()+"/edit?type=success&msg=update_success";

	}	
	
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		districtService.removeById(id);
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";		
	}
	
	private void validate(District district, Errors errors){
		if(district.getName()!=null){
			if(district.getLocale().equals("en")){
				String name=district.getName();
				Pattern pattern=Pattern.compile("[A-Za-z ]{1,50}");
				Matcher matcher=pattern.matcher(name);
				if(!matcher.matches()){
					errors.rejectValue("name","Pattern");
				}
				if(name.length()>100 || name.length()<1){
					errors.rejectValue("name","Size");

				}
			}		
		}
		District duplicateParameter = districtService.findByName(district.getName());
		if(duplicateParameter!=null){
			if(!duplicateParameter.getId().equals(district.getId())){
				errors.rejectValue("name","NonUnique");
			}	
		}
		if(district.getId()!=null){
			if(!district.getVersion().equals(districtService.findById(district.getId()).getVersion())){
				errors.reject("name","Version_Mismatch");
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
		binder.registerCustomEditor(State.class, new StateEditor(stateService)); 
	}
	
	private void populateModel(ModelMap model,District district,String stateName){
		List<State> states=stateService.findAllSorted();
		State selectedState=stateService.findByName(stateName);
		List<State> newStates=new ArrayList<State>();
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("district",district);		
		model.addAttribute("states",newStates);
	}

}
