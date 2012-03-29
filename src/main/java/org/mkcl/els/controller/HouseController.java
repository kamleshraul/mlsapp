package org.mkcl.els.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.mkcl.els.common.util.DateFormater;
import org.mkcl.els.common.util.DateFormatter1;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.repository.GridRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.trg.search.Search;

@Controller
@RequestMapping("/masters_houses")
public class HouseController extends GenericController<House>{
	private static final String ASC = "asc";
	
//	@Override
//	 @RequestMapping(value = "/module", method = RequestMethod.GET)
//	    public String index(final ModelMap model,
//	                        final HttpServletRequest request,
//	                        final @RequestParam(required = false) String formtype,
//	                        final Locale locale) {
//	        final String urlPattern = request.getServletPath().split("\\/")[1];
//	        model.addAttribute("urlPattern", urlPattern);
//	        model.addAttribute("type", HouseType.findByFieldName(HouseType.class, "type", this.getCurrentUser().getHouseType(), locale.toString()).getId());
//	        return getURL(urlPattern) + "module";
//	    }
//
//	@Override
//	 @RequestMapping(value = "/list", method = RequestMethod.GET)
//	    public String list(final ModelMap model,
//	                       final Locale locale,
//	                       final HttpServletRequest request,
//	                       final @RequestParam(required = false) String formtype) {
//	        final String urlPattern = request.getServletPath().split("\\/")[1];
//	        String type=this.getCurrentUser().getHouseType();
//	        HouseType housetype=HouseType.findByFieldName(HouseType.class, "type", type, locale.toString());
//	        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
//	        model.addAttribute("gridId", grid.getId());
//	        model.addAttribute("urlPattern", urlPattern);
//	        model.addAttribute("type", housetype.getId());
//	        return getURL(urlPattern) + "list";
//	    }
//	private String getURL(final String urlPattern) {
//        String[] urlParts = urlPattern.split("_");
//        StringBuffer buffer = new StringBuffer();
//        for (String i : urlParts) {
//            buffer.append(i + "/");
//        }
//        return buffer.toString();
//
//    }
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
			if(this.getCurrentUser().getHouseType()=="lowerhouse")
			{
			Boolean isDuplicateParameter = house.isDuplicate("name",house.getName());
			Object[] params = new Object[1];
			params[0] = house.getName();
			if (isDuplicateParameter) {
				result.rejectValue("name", "NonUnique", params,
					"Duplicate Parameter");
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
			
			Date formationDate=house.getFormationDate();
			Date dissolveDate=house.getDissolveDate();
			if(formationDate.after(dissolveDate)){
				result.rejectValue("formationDate","FormationDateLtDissolveDate","Invalid Date");
			}
			
			if (result.hasErrors()) {
			System.out.println("error");
		}
	}
	
	@Override
	protected void populateNew(final ModelMap model, final House house,
			final String locale, final HttpServletRequest request) {
		house.setLocale(locale.toString());
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
		System.out.println(housetype.getId());
		model.addAttribute("housetype",housetype.getId());
		model.addAttribute("domain",house);
	}
	
}
