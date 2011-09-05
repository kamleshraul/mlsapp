/*
******************************************************************
File: org.mkcl.els.controller.MessageResourceController.java
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

import javax.validation.Valid;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMessageResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class MessageResourceController.
 *
 * @author vishals
 * @version v1.0.0
 */
@Controller
@RequestMapping("/messages")
public class MessageResourceController extends BaseController{
	
	
	/** The grid service. */
	@Autowired
	IGridService gridService;
	
	/** The message resource service. */
	@Autowired IMessageResourceService messageResourceService;
	
	/**
	 * Lists all message resources.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("MSG_RESOURCE_GRID");
		model.addAttribute("gridId", grid.getId());
		return "messages/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="new",method = RequestMethod.GET)
	public String _new(ModelMap model){
		MessageResource resource = new MessageResource();
		model.addAttribute(resource);
		return "messages/new";
	}
	
	/**
	 * Edits the.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}",method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		MessageResource messageResource = messageResourceService.findById(id);
		model.addAttribute(messageResource);
		return "messages/edit";
	}

	
	/**
	 * Creates a new message resource.
	 *
	 * @param messageResource the message resource
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute("messageResource") MessageResource messageResource, BindingResult result, ModelMap model){
		this.validate(messageResource, result);
		if(result.hasErrors()){
			model.addAttribute("isvalid",false);
			return "redirect:messages/new?type=error&msg=create_failed";
		}
		messageResourceService.create(messageResource);		
		return "redirect:messages/"+messageResource.getId() + "?type=success&msg=create_success";
	}
	
	/**
	 * Updates the message resource.
	 *
	 * @param messageResource the message resource
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid @ModelAttribute("messageResource")MessageResource messageResource, BindingResult result, ModelMap model){
		this.validate(messageResource, result);
		if(result.hasErrors()){
			model.addAttribute("isvalid",false);
			return "redirect:messages/edit?type=error&msg=update_failed";
		}
		messageResourceService.update(messageResource);		
		return "redirect:messages/"+messageResource.getId() + "?type=success&msg=update_success";
	}

	/**
	 * Deletes an existing message resource.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		messageResourceService.removeById(id);	
		return "info?type=success&msg=delete_success";
	}
	
	/**
	 * Custom Validation.
	 *
	 * @param messageResource the message resource
	 * @param errors the errors
	 */
	private void validate(MessageResource messageResource, Errors errors){
		MessageResource duplicateResource = messageResourceService.findByLocaleAndCode(messageResource.getLocale(), messageResource.getCode());
		if(duplicateResource!=null){
			if(!duplicateResource.getId().equals(messageResource.getId())){
				errors.rejectValue("code","NonUnique");
			}	
		}
		//Check if the version matches
		if(messageResource.getId()!=null){
			if(!messageResource.getVersion().equals(messageResourceService.findById(messageResource.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}
	}
}
