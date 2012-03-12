package org.mkcl.els.hook.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.hook.IControllerHook;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

public  class ControllerHookAdapter implements IControllerHook {

	@Override
	public <T extends BaseDomain> void populateNew(final ModelMap model, final T domain,
			final Locale locale) {
		domain.setLocale(locale.toString());
	}

	@Override
	public <T extends BaseDomain> void populateInsert(final ModelMap model, final T domain,
			final Locale locale) {

	}

	@Override
	public <T extends BaseDomain> void populateEdit(final ModelMap model, final T domain,
			final Locale locale) {

	}

	@Override
	public <T extends BaseDomain> void populateUpdate(final ModelMap model, final T domain,
			final Locale locale) {

	}

	@Override
	public <T extends BaseDomain> void validateInsert(final T domain, final Errors errors) {
			//Check for duplicate instance if the instance has a field "name"
			try {
				Field nameField=domain.getClass().getDeclaredField("name");
				String nameValue=(String) nameField.get(domain);
				Boolean duplicateParameter = domain.isDuplicate("name",nameValue);
				Object[] params = new Object[1];
				params[0] =nameValue;
				if(duplicateParameter) {
					errors.rejectValue("name", "NonUnique", params, "Duplicate Parameter");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
				return;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return;
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			}

	}

	@Override
	public <T extends BaseDomain> void validateUpdate(final T domain, final Errors errors) {
		// Check for version mismatch
		if(domain.isVersionMismatch()) {
			errors.rejectValue("VersionMismatch", "version");
		}
		//Check for duplicate instance if the instance has a field "name"
		try {
			Field nameField=domain.getClass().getDeclaredField("name");
			String nameValue=(String) nameField.get(domain);
			Boolean duplicateParameter = domain.isDuplicate("name",nameValue);
			Object[] params = new Object[1];
			params[0] =nameValue;
			if(duplicateParameter) {
				errors.rejectValue("name", "NonUnique", params, "Duplicate Parameter");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public <T extends BaseDomain> void initBinder(final T domain, final WebDataBinder binder) {
		Field[] fields=domain.getClass().getDeclaredFields();
		for(Field i:fields){
			String classType=i.getType().getSimpleName();
			//--JoinPoint$StaticPart is the field types injected by aspectJ
			 if (classType.equals("String")||classType.equals("Character")||classType.equals("Boolean")||classType.equals("Number")||classType.equals("JoinPoint$StaticPart")) {

			}else{
				if(i.getModifiers()!=Modifier.TRANSIENT) {
                    binder.registerCustomEditor(domain.getClass(),new BaseEditor(domain));
                }
			}
		}
	}

}
