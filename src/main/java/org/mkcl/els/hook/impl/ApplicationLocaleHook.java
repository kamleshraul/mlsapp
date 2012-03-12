package org.mkcl.els.hook.impl;

import java.util.HashMap;
import java.util.Map;

import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.hook.adapter.ControllerHookAdapter;
import org.springframework.validation.Errors;

public class ApplicationLocaleHook extends ControllerHookAdapter{
//	public  void validateInsert(ApplicationLocale applicationLocale, Errors errors) {
//		//Check for duplicate instance if the instance has a field "name"
//		Map<String, String> names = new HashMap<String, String>();
//		names.put("language", applicationLocale.getLanguage());
//		names.put("country", applicationLocale.getCountry());
//		names.put("variant", applicationLocale.getVariant());		
//		// Check for duplicate instances
//		Boolean duplicateParameter = applicationLocale.isDuplicate(names);
//		Object[] params = new Object[1];
//		params[0] = applicationLocale.getLocaleString();
//		if(duplicateParameter) {
//			errors.rejectValue("language", "NonUnique", params, "Duplicate Parameter");
//		}
//		
//	}
	@Override
	public <T extends BaseDomain> void validateInsert(T domain, Errors errors) {
		//Check for duplicate instance if the instance has a field "name"
		ApplicationLocale applicationLocale=(ApplicationLocale) domain;
		Map<String, String> names = new HashMap<String, String>();
		names.put("language", applicationLocale.getLanguage());
		names.put("country", applicationLocale.getCountry());
		names.put("variant", applicationLocale.getVariant());		
		// Check for duplicate instances
		Boolean duplicateParameter = domain.isDuplicate(names);
		Object[] params = new Object[1];
		params[0] = applicationLocale.getLocaleString();
		if(duplicateParameter) {
			errors.rejectValue("language", "NonUnique", params, "Duplicate Parameter");
		}
	}
	
}
