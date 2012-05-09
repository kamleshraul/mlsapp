package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/election")
public class ElectionController extends GenericController<Election> {

	@Override
	protected void populateNew(ModelMap model, Election domain, String locale,
			HttpServletRequest request) {
		domain.setLocale(locale);
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}
	
	@Override
	protected void populateCreateIfErrors(ModelMap model, Election domain,
			HttpServletRequest request) {
		String locale = domain.getLocale();
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}
	
	@Override
	protected void populateEdit(ModelMap model, Election domain,
			HttpServletRequest request) {
		String locale = domain.getLocale();
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}
	
	private void populate(ModelMap model, String locale, String houseType){
		List<ElectionType> electionTypes = ElectionType.findByHouseType(houseType, locale);
		model.addAttribute("electionTypes", electionTypes);
	}
}
