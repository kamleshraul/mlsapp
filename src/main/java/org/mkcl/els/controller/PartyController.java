/*
******************************************************************
File: org.mkcl.els.controller.PartyController.java
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

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.Party;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IPartyService;
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
 * The Class PartyController.
 *
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/party")
public class PartyController extends BaseController{

	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The party service. */
	@Autowired
	IPartyService partyService;
	
	/**
	 * List.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("PARTY_GRID");
		model.addAttribute("gridId", grid.getId());
		return "party/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model){
		Party party = new Party();
		model.addAttribute(party);
		return "party/new";
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
		Party party = partyService.findById(id);
		model.addAttribute(party);
		return "party/edit";
	}
	
	/**
	 * Creates the.
	 *
	 * @param party the party
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("party") Party party, 
			BindingResult result, ModelMap model){
		this.validate(party, result);
		
		if(result.hasErrors()){
			model.addAttribute("isvalid", false);
			return "redirect:party/new?type=error&msg=create_failed";
		}
		
		partyService.create(party);		
		return "redirect:party/"+party.getId()+"?type=success&msg=create_success";
	}
	
	/**
	 * Update.
	 *
	 * @param party the party
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("party")Party party, 
			BindingResult result, ModelMap model){
		this.validate(party, result);
		
		if(result.hasErrors()){
			model.addAttribute("isvalid", false);
			return "redirect:party/"+party.getId()+"?type=error&msg=update_failed";
		}
		 
		partyService.update(party);		
		return "redirect:party/"+party.getId()+"?type=success&msg=update_success";
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
		partyService.removeById(id);	
		return "info";
	}
	
	/**
	 * Validate.
	 *
	 * @param party the party
	 * @param errors the errors
	 */
	private void validate(Party party, Errors errors){
		Party duplicateParty = partyService.findByName(party.getName());
		if(duplicateParty!=null){
			if(!duplicateParty.getId().equals(party.getId())){
				// name attribute of Party object must be unique
				errors.rejectValue("name","NonUnique");
			}	
		}
		//Check if the version matches
		if(party.getId()!=null){
			if(!party.getVersion().equals(partyService.findById(party.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}
	}
}
