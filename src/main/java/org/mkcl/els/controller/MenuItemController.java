/*
******************************************************************
File: org.mkcl.els.controller.MenuItemController.java
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.MenuItemEditor;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMenuItemService;
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
 * The Class MenuItemController.
 *
 * @author amitd
 * @version v1.0.0
 */
@Controller
@RequestMapping("/menus")
public class MenuItemController extends BaseController 
{
	
	/** The menu item service. */
	@Autowired
	IMenuItemService menuItemService;
	
	/** The grid service. */
	@Autowired
	IGridService gridService;
	

	/**
	 * Lists all the menu items.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="list",method = RequestMethod.GET)
	public String list(ModelMap model,Locale locale){
		String menu_xml = menuItemService.getMenuXml(locale);
		model.addAttribute("menu_xml", menu_xml);
		return "masters/menus/list";
	}
	
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @param request the request
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model,HttpServletRequest request,Locale locale){
		MenuItem menuItem = new MenuItem();
		menuItem.setLocale(locale.toString());
		String parentId = request.getParameter("parentId");
		if(parentId!=null){
			MenuItem parent = menuItemService.findById(Long.parseLong(parentId));
			menuItem.setParent(parent);
		}
		model.addAttribute(menuItem);
		return "masters/menus/new";
	}
	
	/**
	 * Shows an existing record in edit mode.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model){
		MenuItem menuItem = menuItemService.findById(id);
		model.addAttribute(menuItem);
		return "masters/menus/edit";
	}

	
	/**
	 * Creates a new menu item.
	 *
	 * @param menuItem the menu item
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("menuItem")MenuItem menuItem, 
			BindingResult result, ModelMap model){
		// Enforce the UNIQUEness constraint
		this.validate(menuItem, result);
		
		// UNIQUEness constraint violated
		if(result.hasErrors()){
			model.addAttribute("menuItem",menuItem);
			model.addAttribute("type","error");
			model.addAttribute("msg","create_failed");
			return "masters/menus/new";
		}
		menuItemService.create(menuItem);
		return "redirect:menus/"+menuItem.getId()+"/edit?type=success&msg=create_success";
	}
	
	
	/**
	 * Updates an existing menu.
	 *
	 * @param menuItem the menu item
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("menuItem")MenuItem menuItem, 
			BindingResult result, ModelMap model){
		// Enforce the UNIQUEness constraint
		this.validate(menuItem, result);
		
		// UNIQUEness constraint violated
		if(result.hasErrors()){
			model.addAttribute("menuItem",menuItem);
			model.addAttribute("type","error");
		    model.addAttribute("msg","update_failed");
		    return "masters/menus/edit";
		}
		
		// No violation of the UNIQUEness constraint
		menuItemService.update(menuItem);
		return "redirect:menus/"+menuItem.getId() + "/edit?type=success&msg=update_success";
	}
	
	
	/**
	 * Deletes an existing record.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "{id}/delete", method = RequestMethod.DELETE)
	public String delete(@PathVariable Long id, ModelMap model){
		menuItemService.removeById(id);
		model.addAttribute("msg","delete_success");
		model.addAttribute("type","menu_success");
		return "info";	
		}	
	
	
	/**
	 * Validates the menu item object.
	 *
	 * @param menuItem the menu item
	 * @param errors the errors
	 */
	private void validate(MenuItem menuItem, Errors errors){
		/*MenuItem duplicateMenuItem = menuItemService.findByTextKey(menuItem.getTextKey());
		if(duplicateMenuItem!=null){
			if(!duplicateMenuItem.getId().equals(menuItem.getId())){
				errors.rejectValue("code","NonUnique");
			}	
		}
		//Check if the version matches
		if(menuItem.getId()!=null){
			if(!menuItem.getVersion().equals(menuItemService.findById(menuItem.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}*/
	}
	
	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder 
	public void initBinder(WebDataBinder binder) { 
		binder.registerCustomEditor(MenuItem.class, new MenuItemEditor(menuItemService)); 
	}
}
