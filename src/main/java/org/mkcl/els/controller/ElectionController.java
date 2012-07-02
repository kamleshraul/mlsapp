package org.mkcl.els.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Election;
import org.mkcl.els.domain.ElectionType;
import org.mkcl.els.domain.House;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/election")
public class ElectionController extends GenericController<Election> {

	@Override
	protected void populateNew(final ModelMap model, final Election domain, final String locale,
			final HttpServletRequest request) {
		domain.setLocale(locale);
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}

	@Override
	protected void populateCreateIfErrors(final ModelMap model, final Election domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}

	@Override
	protected void populateEdit(final ModelMap model, final Election domain,
			final HttpServletRequest request) {
		String locale = domain.getLocale();
		String houseType = this.getCurrentUser().getHouseType();
		this.populate(model, locale, houseType);
	}

	private void populate(final ModelMap model, final String locale, final String houseType){
		List<ElectionType> electionTypes = ElectionType.findByHouseType(houseType, locale);
		model.addAttribute("electionTypes", electionTypes);
		List<House> houses=House.findByHouseType(houseType, locale);
		model.addAttribute("houses",houses);
	}
}
