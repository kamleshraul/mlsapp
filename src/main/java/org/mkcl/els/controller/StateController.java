/*
******************************************************************
File: org.mkcl.els.controller.StateController.java
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.State;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IStateService;
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
 * The Class StateController.
 *
 * @author sandeeps
 * @version v1.0.0
 */

@Controller
@RequestMapping("/states")
public class StateController extends BaseController{

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The state service. */
	@Autowired
	IStateService stateService;
	
	/**
	 * Index.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model){
		Grid grid = gridService.findByName("STATE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "masters/states/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="new",method=RequestMethod.GET)
	public String _new(ModelMap model){	
		State state=new State();
		model.addAttribute("state",state);
		return "masters/states/new";
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
		State state = stateService.findById(id);
		model.addAttribute("state",state);
		return "masters/states/edit";
	}
	
	/**
	 * Creates the.
	 *
	 * @param state the state
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("state") State state,BindingResult result, ModelMap model){
		this.validate(state,result);
		if(result.hasErrors()){
			model.addAttribute("state",state);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/states/new";
		}	
		stateService.create(state);
		return "redirect:states/"+state.getId()+"/edit?type=success&msg=create_success";

		
	}
	
	/**
	 * Edits the.
	 *
	 * @param state the state
	 * @param result the result
	 * @param model the model
	 * @param session the session
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String edit(@Valid @ModelAttribute("state") State state,BindingResult result, ModelMap model){
		this.validate(state,result);
		if(result.hasErrors()){
			model.addAttribute("state",state);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
		    return "masters/states/edit";
		}	
		stateService.update(state);
		return "redirect:states/"+state.getId()+"/edit?type=success&msg=update_success";

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
		stateService.removeById(id);		
		model.addAttribute("type","success");
		model.addAttribute("msg","delete_success");
		return "info";
		}	
	
	
	
	/**
	 * Validate.
	 *
	 * @param state the state
	 * @param errors the errors
	 */
	private void validate(State state, Errors errors){
		if(state.getName()!=null){
			if(state.getLocale().equals("en")){
				String name=state.getName();
				Pattern pattern=Pattern.compile("[A-Za-z ]{1,50}");
				Matcher matcher=pattern.matcher(name);
				if(!matcher.matches()){
					errors.rejectValue("name","Pattern");
				}
				if(name.length()>50 || name.length()<1){
					errors.rejectValue("name","Size");

				}
			}		
		}
		State duplicateParameter = stateService.findByName(state.getName());
		if(duplicateParameter!=null){
			if(!duplicateParameter.getId().equals(state.getId())){
				errors.rejectValue("name","NonUnique");
			}	
		}
		if(state.getId()!=null){
			if(!state.getVersion().equals(stateService.findById(state.getId()).getVersion())){
				errors.reject("name","Version_Mismatch");
			}
		}
	}
	
}
