package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.MemberRole;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/masters_memberroles")
public class MemberRoleController extends GenericController<MemberRole>{
	 @Override
	    protected void populateNew(final ModelMap model,
	                               final MemberRole domain,
	                               final Locale locale,
	                               final HttpServletRequest request) {
	        
	    	 String assemblycounciltype = ((CustomParameter) CustomParameter.findByName(
	                 CustomParameter.class, "DEFAULT_HOUSETYPE", locale.toString())).getValue();
	         populate(model, domain, locale.toString(), request, assemblycounciltype);
	    }
	 
	 protected void populate(final ModelMap model, final MemberRole domain, final String locale,
				final HttpServletRequest request, final String assemblycounciltype) {
			// TODO Auto-generated method stub
		   domain.setLocale(locale);
	       List<HouseType> assemblycounciltypelist = HouseType.findAll(
	    		   HouseType.class, "type", "asc", locale.toString());
	       HouseType selectedAssemblycounciltype = HouseType.findByFieldName(
	    		   HouseType.class, "type", assemblycounciltype, locale.toString());
	       List<HouseType> newassemblycounciltype = new ArrayList<HouseType>();
	       newassemblycounciltype.add(selectedAssemblycounciltype);
	       assemblycounciltypelist.remove(selectedAssemblycounciltype);
	       newassemblycounciltype.addAll(assemblycounciltypelist);
	       model.addAttribute("assemblycounciltype", newassemblycounciltype);
	       domain.setHouseType(selectedAssemblycounciltype);
	     }
	 
	 @Override
	 protected void populateEdit(final ModelMap model,
	                                 final MemberRole domain,
	                                 final HttpServletRequest request) {
	 	domain.setLocale(domain.getLocale());
	 	String assemblycounciltype = domain.getHouseType().getType();
	     populate(
	             model, domain,domain.getLocale(), request,
	             assemblycounciltype);
	     model.addAttribute("MemberRole", domain);
	     }
}
