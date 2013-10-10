package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Dam;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Fort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(value="/fort")
public class FortController extends GenericController<Fort>{
	
	@Override
	 protected void populateNew(final ModelMap model, final Fort domain,
	            final String locale, final HttpServletRequest request) {
	 
		List<District> districts=District.findAll(District.class, "name", "desc", locale);
		model.addAttribute("districts",districts);
	 }

	
	@Override
	protected void populateEdit(final ModelMap model, final Fort domain,
	            final HttpServletRequest request) {

		List<District> districts=District.findAll(District.class, "name", "desc", domain.getLocale());
		model.addAttribute("districts",districts);
	}

}
