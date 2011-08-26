package org.mkcl.els.controller;

import javax.validation.Valid;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.MenuItem;
import org.mkcl.els.service.IGridService;
import org.mkcl.els.service.IMenuItemService;
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
 * MenuItemController provides the administrator with
 * an interface to CRUD menus.
 * 
 * @author amitd
 * Date: 2011 August 26
 */
@Controller
@RequestMapping("/menus")
public class MenuItemController extends BaseController 
{
	@Autowired
	IMenuItemService menuItemService;
	
	@Autowired
	IGridService gridService;
	
	/**
	 * Adds the grid id of the grid identified by 
	 * MENU_ITEM_GRID to the model.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(ModelMap model)
	{
		Grid grid = gridService.findByName("MENU_ITEM_GRID");
		model.addAttribute("gridId", grid.getId());
		return "menus/list";
	}
	
	/**
	 * Creates a new MenuItem instance and adds it to the
	 * model attribute
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model)
	{
		MenuItem menuItem = new MenuItem();
		model.addAttribute(menuItem);
		return "menus/new";
	}
	
	/**
	 * Find a MenuItem instance identified by id and add
	 * it to the model attribute
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, ModelMap model)
	{
		MenuItem menuItem = menuItemService.findById(id);
		model.addAttribute(menuItem);
		return "menus/edit";
	}
	
	/**
	 * Each MenuItem's textKey attribute should be UNIQUE.
	 * Enforce the constraint while creating a new MenuItem.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("menuItem")MenuItem menuItem, 
			BindingResult result, ModelMap model)
	{
		// Enforce the UNIQUEness constraint
		this.validate(menuItem, result);
		
		// UNIQUEness constraint violated
		if(result.hasErrors())
		{
			model.addAttribute("isvalid", false);
			return "redirect:menus/new?type=error&msg=create_failed";
		}
		
		// No violation of the UNIQUEness constraint
		menuItemService.create(menuItem);
		StringBuffer sb = new StringBuffer("redirect:menus/");
		sb.append(menuItem.getId());
		sb.append("?type=success&msg=create_success");
		return sb.toString();
	}
	
	/**
	 * Each MenuItem's textKey attribute should be UNIQUE.
	 * Enforce the constraint while updating a MenuItem.
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("menuItem")MenuItem menuItem, 
			BindingResult result, ModelMap model)
	{
		// Enforce the UNIQUEness constraint
		this.validate(menuItem, result);
		
		// UNIQUEness constraint violated
		if(result.hasErrors())
		{
			model.addAttribute("isvalid", false);
			return "redirect:menus/edit?type=error&msg=update_failed";
		}
		
		// No violation of the UNIQUEness constraint
		menuItemService.update(menuItem);
		StringBuffer sb = new StringBuffer("redirect:menus/");
		sb.append(menuItem.getId());
		sb.append("?type=success&msg=update_success");
		return sb.toString();
	}
	
	/**
	 * Delete a MenuItem instance identified by the id.
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable Long id, ModelMap model)
	{
		menuItemService.removeById(id);
		return "info?type=success&msg=delete_success";
	}	
	
	private void validate(MenuItem menuItem, Errors errors)
	{
		MenuItem duplicateMenuItem = 
			menuItemService.findByTextKey(menuItem.getTextKey());
		
		if(duplicateMenuItem != null)
		{
			if(!duplicateMenuItem.getId().equals(menuItem.getId()))
			{
				errors.rejectValue("code", "NonUnique");
			}
		}
	}
}
