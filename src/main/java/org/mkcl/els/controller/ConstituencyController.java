/*
******************************************************************
File: org.mkcl.els.controller.ConstituencyController.java
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
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.State;
import org.mkcl.els.service.IConstituencyService;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IDistrictService;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
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
 * The Class ConstituencyController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/constituencies")
public class ConstituencyController extends BaseController{

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	@Autowired
	IStateService stateService;
	
	@Autowired
	IDistrictService districtService;
	
	@Autowired
	IConstituencyService constituencyService;
	
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
		Grid grid = gridService.findByName("CONSTITUENCY_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/constituencies/list";
	}
	
	@RequestMapping(value="new",method=RequestMethod.GET)
	public String _new(ModelMap model,Locale locale){	
		Constituency constituency=new Constituency();
		constituency.setLocale(locale.toString());
		populateModel(model, constituency, customParameterService.findByName("DEFAULT_STATE").getValue());		
		return "masters/constituencies/new";
	}
	
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		Constituency constituency=constituencyService.findById(id);
		Set<District> districts=constituency.getDistricts();
		String stateName=null;
		if(!districts.isEmpty()){
			stateName=districts.iterator().next().getState().getName();	
		}
		populateModel(model, constituency, stateName);		
		return "masters/constituencies/edit";		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("constituency") Constituency constituency,BindingResult result, ModelMap model,@RequestParam String state){
		this.validate(constituency,result);
		if(result.hasErrors()){
			populateModel(model, constituency,state);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/constituencies/new";
		}	
		constituencyService.create(constituency);
		return "redirect:constituencies/"+constituency.getId()+"/edit?type=success&msg=create_success";

		
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid @ModelAttribute("constituency") Constituency constituency,BindingResult result, ModelMap model,@RequestParam String state){
		this.validate(constituency,result);
		if(result.hasErrors()){
			populateModel(model, constituency,state);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
			return "masters/constituencies/edit";
		}	
		constituencyService.update(constituency);
		return "redirect:constituencies/"+constituency.getId()+"/edit?type=success&msg=update_success";
		
	}
	
	@RequestMapping(value="{id}/delete", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		constituencyService.removeById(id);
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";			
	}
	
	private void validate(Constituency constituency, Errors errors){
		if(constituency.getName()!=null){
			if(constituency.getLocale().equals("en")){
				String name=constituency.getName();
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
		if(constituency.getNumber()!=null){
			if(constituency.getLocale().equals("en")){
				String number=constituency.getNumber();
				if(number.length()>100 || number.length()<1){
					errors.rejectValue("number","Size");
				}
			}		
		}
		Constituency duplicateParameter = constituencyService.findByName(constituency.getName());
		if(duplicateParameter!=null){
			if(!duplicateParameter.getId().equals(constituency.getId())){
				errors.rejectValue("name","NonUnique");
			}	
		}
		if(constituency.getId()!=null){
			if(!constituency.getVersion().equals(constituencyService.findById(constituency.getId()).getVersion())){
				errors.rejectValue("name","Version_Mismatch");
			}
		}
	}
	
	@InitBinder 
	public void initBinder(WebDataBinder binder) { 
		binder.registerCustomEditor(Set.class, "districts", new CustomCollectionEditor(Set.class) {
			protected Object convertElement(Object element) {
				String id = null;

				if (element instanceof String) {
					id = (String)element;
				}

				return id != null ? districtService.findById(Long.valueOf(id)) : null;
			}
		});
	}
	
	private void populateModel(ModelMap model,Constituency constituency,String stateName){
		List<State> states=stateService.findAllSorted();
		State selectedState=stateService.findByName(stateName);
		List<State> newStates=new ArrayList<State>();
		newStates.add(selectedState);
		states.remove(selectedState);
		newStates.addAll(states);
		model.addAttribute("constituency",constituency);		
		model.addAttribute("states",newStates);
		model.addAttribute("districts",districtService.findDistrictsByStateId(selectedState.getId()));
		
	}
}
