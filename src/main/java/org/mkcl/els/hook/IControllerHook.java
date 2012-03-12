package org.mkcl.els.hook;

import java.util.Locale;

import org.mkcl.els.domain.BaseDomain;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

public interface IControllerHook extends IHook {
	
	public <T extends BaseDomain> void populateNew(final ModelMap model, final T domain, final Locale locale);
	
	public <T extends BaseDomain> void populateInsert(final ModelMap model, final T domain, final Locale locale);
	
	public <T extends BaseDomain> void populateEdit(final ModelMap model, final T domain, final Locale locale);
	
	public <T extends BaseDomain> void populateUpdate(final ModelMap model, final T domain, final Locale locale);	
	
	public <T extends BaseDomain> void validateInsert(final T domain, final Errors errors);
	
	public <T extends BaseDomain> void validateUpdate(final T domain, final Errors errors);
	
    public <T extends BaseDomain> void initBinder(final T domain,final WebDataBinder binder);
}
