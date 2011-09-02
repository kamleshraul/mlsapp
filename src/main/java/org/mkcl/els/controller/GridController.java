/*
******************************************************************
File: org.mkcl.els.controller.GridController.java
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
import org.mkcl.els.common.vo.Filter;
import org.mkcl.els.common.vo.GridConfig;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/grid")
public class GridController extends BaseController{
	
	@Autowired
	IGridService gridService;
	
	/**
	 * Gets the metadata configuration.
	 *
	 * @param gridId the grid id
	 * @param meta the meta
	 * @param model the model
	 * @param request the request
	 * @return the meta
	 * @throws ClassNotFoundException the class not found exception
	 */
	@RequestMapping(value = "/{gridId}/meta", method = RequestMethod.GET)
    public @ResponseBody GridConfig getConfig(
            @PathVariable Long gridId,
            ModelMap model, HttpServletRequest request) throws ClassNotFoundException {
        //Get the entity class based on the grid id
        return gridService.getConfig(gridId);
    }
	
	/**
	 * Gets the.
	 *
	 * @param gridId the grid id
	 * @param page the page
	 * @param rows the rows
	 * @param sidx the sidx
	 * @param order the order
	 * @param search the search
	 * @param searchField the search field
	 * @param searchString the search string
	 * @param searchOper the search oper
	 * @param filtersData the filters data
	 * @param baseFilters the base filters
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the grid data
	 * @throws ClassNotFoundException the class not found exception
	 */
	@RequestMapping(value = "/data/{gridId}", method = RequestMethod.GET)
	public  @ResponseBody GridData get(
			@PathVariable Long gridId,
			@RequestParam(value = "page", required = false) Integer page ,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String order,
			@RequestParam(value = "_search", required = false) Boolean search,
			@RequestParam(value = "searchField", required = false) String searchField,
			@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "searchOper", required = false) String searchOper,
			@RequestParam(value = "filters", required = false) String filtersData,
			@RequestParam(value = "baseFilters", required = false) String baseFilters,
			ModelMap model, HttpServletRequest request, Locale locale) throws ClassNotFoundException {
		
		Filter filter = Filter.create(filtersData);
		if(search){
			return gridService.getData(gridId, rows, page, sidx, order, filter.toSQl(), locale);
		}
		else{
			return gridService.getData(gridId, rows, page, sidx, order, locale);
		}
	}
	
	/**
	 * Lists all grids.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(ModelMap model) {
		Grid grid = gridService.findByName("GRID");
		model.addAttribute("gridId", grid.getId());
		return "grid/list";
	}
	
	/**
	 * _new.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value = "new", method = RequestMethod.GET)
	public String _new(ModelMap model){
		Grid grid = new Grid();
		model.addAttribute(grid);
		return "grid/new";
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
		Grid grid = gridService.findById(id);
		model.addAttribute(grid);
		return "grid/edit";
	}
	
	
	/**
	 * Creates a new grid.
	 *
	 * @param grid the grid
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid 
			@ModelAttribute("grid") Grid grid, 
			BindingResult result, ModelMap model){
		this.validate(grid, result);
		
		if(result.hasErrors()){
			model.addAttribute("isvalid", false);
			return "redirect:grid/new?type=error&msg=create_failed";
		}
		
		gridService.create(grid);		
		return "redirect:grid/"+grid.getId()+"?type=success&msg=create_success";
	}
	
	
	/**
	 * Updates the grid.
	 *
	 * @param grid the grid
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid 
			@ModelAttribute("grid")Grid grid, 
			BindingResult result, ModelMap model){
		this.validate(grid, result);
		
		if(result.hasErrors()){
			model.addAttribute("isvalid", false);
			return "redirect:grid/edit?type=error&msg=update_failed";
		}
		 
		gridService.update(grid);		
		return "redirect:grid/"+grid.getId()+"?type=success&msg=update_success";
	}
	
	/**
	 * Deletes an existing grid.
	 *
	 * @param id the id
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="{id}", method=RequestMethod.DELETE)
    public String delete(@PathVariable Long id, ModelMap model){
		gridService.removeById(id);	
		return "info?type=success&msg=delete_success";
	}
	
	/**
	 * Custom Validation.
	 *
	 * @param grid the grid
	 * @param errors the errors
	 */
	private void validate(Grid grid, Errors errors){
		Grid duplicateGrid = gridService.findByName(grid.getName());
		if(duplicateGrid!=null){
			if(!duplicateGrid.getId().equals(grid.getId())){
				errors.rejectValue("code","NonUnique");
			}
		}
		//Check if the version matches
		if(grid.getId()!=null){
			if(!grid.getVersion().equals(gridService.findById(grid.getId()).getVersion())){
				errors.reject("Version_Mismatch");
			}
		}
	}
}
