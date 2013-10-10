package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Dam;
import org.mkcl.els.domain.District;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(value="/dam")
public class DamController extends GenericController<Dam>{
	
	@Override
	 protected void populateNew(final ModelMap model, final Dam domain,
	            final String locale, final HttpServletRequest request) {
	 
		List<District> districts=District.findAll(District.class, "name", "desc", locale);
		model.addAttribute("districts",districts);
	 }

	
	@Override
	protected void populateEdit(final ModelMap model, final Dam domain,
	            final HttpServletRequest request) {

		List<District> districts=District.findAll(District.class, "name", "desc", domain.getLocale());
		model.addAttribute("districts",districts);
	}
}
