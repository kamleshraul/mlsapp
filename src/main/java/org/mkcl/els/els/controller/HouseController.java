package org.mkcl.els.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/house")
public class HouseController extends GenericController<House>{
	private static final String ASC = "asc";
	
	@Override
	 @RequestMapping(value = "/module", method = RequestMethod.GET)
	    public String index(final ModelMap model,
	                        final HttpServletRequest request,
	                        final @RequestParam(required = false) String formtype,
	                        final Locale locale) {
	        final String urlPattern = request.getServletPath().split("\\/")[1];
	        model.addAttribute("type", HouseType.findByFieldName(HouseType.class, "type", this.getCurrentUser().getHouseType(), locale.toString()).getId());
	        model.addAttribute("urlPattern", urlPattern);
	        if (formtype != null) {
	            if (formtype.equals("g")) {
	                model.addAttribute("urlPattern", urlPattern);
	                return "generics/module";
	            }
	        }
	        return getURL(urlPattern) + "module";
	    }

	    /**
	     * List.
	     *
	     * @param model the model
	     * @param locale the locale
	     * @param request the request
	     * @param formtype the formtype
	     * @return the string
	     * @author sandeeps
	     * @since v1.0.0
	     */
	@Override
	    @RequestMapping(value = "/list", method = RequestMethod.GET)
	    public String list(@RequestParam(required = false) final String formtype,
	            final ModelMap model, final Locale locale,
	            final HttpServletRequest request) {
	        final String urlPattern = request.getServletPath().split("\\/")[1];
	        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
	        String type=this.getCurrentUser().getHouseType();
	        HouseType housetype=HouseType.findByFieldName(HouseType.class, "type", type, locale.toString());
	        model.addAttribute("gridId", grid.getId());
	        model.addAttribute("urlPattern", urlPattern);
	        model.addAttribute("type", housetype.getId());
	        if (formtype != null) {
	            if (formtype.equals("g")) {
	                model.addAttribute("urlPattern", urlPattern);
	                return "generics/list";
	            }
	        }
	        return getURL(urlPattern) + "list";
	    }
	    
	    private String getURL(final String urlPattern) {
	        String[] urlParts = urlPattern.split("_");
	        StringBuffer buffer = new StringBuffer();
	        for (String i : urlParts) {
	            buffer.append(i + "/");
	        }
	        return buffer.toString();

	    }

	protected void customValidateCreate(final House domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	protected void customValidateUpdate(final House domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	private void customValidate(final House house,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();
			if(this.getCurrentUser().getHouseType().equals("lowerhouse"))
			{
			Boolean isDuplicateParameter = house.isDuplicate("name",house.getName());
			Object[] params = new Object[1];
			params[0] = house.getName();
			if (isDuplicateParameter) {
				result.rejectValue("name", "NonUnique", params,
					"Duplicate Parameter");
			}
			
			Date formationDate=house.getFormationDate();
			Date dissolveDate=house.getDissolveDate();
			if(formationDate.after(dissolveDate)){
				result.rejectValue("formationDate","FormationDateLtDissolveDate","Invalid Date");
			}
			
			
			Date firstDate=house.getFirstDate();
			Date endDate=house.getLastDate();
			if(firstDate.after(endDate)){
				result.rejectValue("firstDate","FirstDateLtLastDate","Invalid Date");
			}
			}
			if (house.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
			}
			
			
			
			if (result.hasErrors()) {
			System.out.println("error");
		}
	}
	
	@Override
	protected void populateNew(final ModelMap model, final House house,
			final String locale, final HttpServletRequest request) {
		house.setLocale(locale);
		populateModel(model, house);
	}
	@Override
	protected void populateEdit(final ModelMap model, final House house,
			final HttpServletRequest request) {
		    populateModel(model, house);
			house.setLocale(house.getLocale());
	}
	
	private void populateModel(final ModelMap model, final House house) {
		
		String type=this.getCurrentUser().getHouseType();
		HouseType housetype=HouseType.findByFieldName(HouseType.class, "type", type, house.getLocale());
		model.addAttribute("housetype",housetype.getId());
		model.addAttribute("houseType",housetype.getType());
		model.addAttribute("domain",house);
	}
	
}
