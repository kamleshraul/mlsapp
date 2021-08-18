/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.GenericController.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.PartialUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trg.dao.DAOUtil;

/**
 * The Class GenericController.
 *
 * @param <T> the generic type
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public class GenericController<T extends BaseDomain> extends BaseController {

    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** The domain class. */
    @SuppressWarnings("unchecked")
    private final Class<T> domainClass = (Class<T>) DAOUtil.getTypeArguments(
            GenericController.class, this.getClass()).get(0);

    // ========================= API =========================

    /**
     * Index.
     *
     * @param model the model
     * @param request the request
     * @param formtype the formtype
     * @return the string
     */
    @RequestMapping(value = "/module", method = RequestMethod.GET)
    public String index(final ModelMap model, 
    		final HttpServletRequest request,
            final @RequestParam(required = false) String formtype,
            final Locale locale) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        /******* Hook **********/
        populateModule(model, request, locale.toString(), this.getCurrentUser());
        /***********************/
        if (formtype != null) {
            if (formtype.equals("g")) {
                String urlPattern=servletPath.split("\\/module")[0];
                String messagePattern=urlPattern.replaceAll("\\/",".");
                model.addAttribute("messagePattern", messagePattern);
                model.addAttribute("urlPattern", urlPattern);
                return "generic/module";
            }
        }
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("module","error");
        }else{
            return servletPath;
        }
    }

    /**
     * List.
     *
     * @param formtype the formtype
     * @param model the model
     * @param locale the locale
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) final String formtype,
            final ModelMap model,
            final Locale locale,
            final HttpServletRequest request) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/list")[0];
        String messagePattern=urlPattern.replaceAll("\\/",".");
        String newurlPattern=modifyURLPattern(urlPattern,request,model,locale.toString());
        Grid grid = null;
        try{
        	grid = Grid.findByDetailView(newurlPattern, locale.toString());
        	model.addAttribute("gridId", grid.getId());
        }catch (ELSException e) {
        	logger.error(e.getMessage());
			model.addAttribute("error", e.getParameter());			
		}        
        /******* Hook **********/
        populateList(model, request, locale.toString(), this.getCurrentUser());
        /***********************/
        if (formtype != null) {
            if (formtype.equals("g")) {
                model.addAttribute("messagePattern", messagePattern);
                model.addAttribute("urlPattern", urlPattern);
                return "generic/list";
            }
        }
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("list","error");
        }else{
            return servletPath;
        }
    }

    /*
     * This gives chance to client to modify urlpattern so as to
     * select grid based on user group.for this attach usergroup=? as a
     * parameter in urlpattern
     */
    protected String modifyURLPattern(final String urlPattern, 
    		final HttpServletRequest request, 
    		final ModelMap model, 
    		final String string) {
        return urlPattern;
    }

    /**
     * New form.
     *
     * @param model the model
     * @param locale the locale
     * @param request the request
     * @return the string
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final ModelMap model, 
    		final Locale locale,
            final HttpServletRequest request) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/new")[0];
        String messagePattern=urlPattern.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", urlPattern);
        //THIS IS USED TO REMOVE THE BUG WHERE IN RECORD UPDATED MESSAGE
        //APPEARS WHEN CLICKED ON NEW REOCRD
        model.addAttribute("type", "");
        T domain = null;
        try {
            domain = domainClass.newInstance();
        }
        catch (InstantiationException e) {
            logger.error(e.getMessage());
            String message = null; 
			message = "There is some problem, request may not complete successfully.";						
			model.addAttribute("error", message);
        }
        catch (IllegalAccessException e) {
        	logger.error(e.getMessage());
            String message = null; 
			message = "There is some problem, request may not complete successfully.";						
			model.addAttribute("error", message);
        }
        try{
	        /******* Hook **********/
	        populateNew(model, domain, locale.toString(), request);
	        /***********************/
        }catch (Exception e) {
        	logger.error(e.getMessage());
        	String message = null; 
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
			}else{
				message = e.getMessage();
				e.printStackTrace();
			}			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}			
			model.addAttribute("error", message);
		}
        model.addAttribute("domain", domain);
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("new","error");
        }else{
            String modifiedNewUrlPattern=modifyNewUrlPattern(servletPath,request,model,locale.toString());
            return modifiedNewUrlPattern;
        }
    }

    protected String modifyNewUrlPattern(final String servletPath,
            final HttpServletRequest request, 
            final ModelMap model, 
            final String string) {
        return servletPath;
    }

    /**
     * Edits the.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String edit(final @PathVariable("id") Long id, 
    		final ModelMap model,
            final HttpServletRequest request) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String urlPattern=servletPath.split("\\/edit")[0].replace("/"+id,"");
        String messagePattern=urlPattern.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", urlPattern);
        T domain = (T) BaseDomain.findById(domainClass, id);
        if(domain!=null && domain.getId()>0) {
        	T nextDomain=BaseDomain.findByIdNext(domainClass, id, "id", domain.getLocale());
        	T prevDomain=BaseDomain.findByIdPrev(domainClass, id, "id", domain.getLocale());
        	
        	model.addAttribute("nextDomain",nextDomain);
        	model.addAttribute("prevDomain",prevDomain);
        }
        try{
	        /******* Hook **********/
	        populateEdit(model, domain, request);	        
        }catch (Exception e) {
        	logger.error(e.getMessage());
        	String message = null; 
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
			}else{
				message = e.getMessage();
				e.printStackTrace();
			}			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}			
			model.addAttribute("error", message);
		}        
        /***********************/
        model.addAttribute("domain", domain);
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
        }
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return urlPattern+"/"+"error";
        }else{
            String newUrlPattern=urlPattern+"/edit";
            String modifiedEditUrlPattern=modifyEditUrlPattern(newUrlPattern,request,model,domain.getLocale());
            return modifiedEditUrlPattern;
        }
    }

    protected String modifyEditUrlPattern(final String newUrlPattern, 
    		final HttpServletRequest request, 
    		final ModelMap model, 
    		final String locale) {
        return newUrlPattern;
    }

    /**
     * Creates the.
     *
     * @param model the model
     * @param request the request
     * @param redirectAttributes the redirect attributes
     * @param locale the locale
     * @param domain the domain
     * @param result the result
     * @return the string
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String create(final ModelMap model,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes,
            final Locale locale,
            @Valid @ModelAttribute("domain") final T domain,
            final BindingResult result
    ) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String messagePattern=servletPath.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", servletPath);        
        try{
	        /*****Hook*************/
	        preValidateCreate(domain, result, request);
	        /*****Hook*************/
	        validateCreate(domain, result, request);
	        /*****Hook*************/
	        postValidateCreate(domain, result, request);
	        /**********************/
	        model.addAttribute("domain", domain);
	        if (result.hasErrors()) {
	            /*****Hook*************/
	            populateCreateIfErrors(model,domain, request);
	            /**********************/	           
	            String newUrlPattern=servletPath+"/new";
	            String modifiedNewUrlPattern=modifyNewUrlPattern(newUrlPattern,request,model,domain.getLocale());
	            return modifiedNewUrlPattern;    
	        }
	        /*****Hook*************/
	        populateCreateIfNoErrors(model, domain, request);
	        /**********************/
	        trimString(model, domain, request);
	        ((BaseDomain) domain).persist();
	        populateAfterCreate(model, domain, request);
        }catch (Exception e) {
        	logger.error(e.getMessage());
        	String message = null; 
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
			}else{
				message = e.getMessage();
				e.printStackTrace();
			}			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}			
			model.addAttribute("error", message);	
	        populateCreateIfErrors(model,domain, request);
	        String newUrlPattern=servletPath+"/new";
            String modifiedNewUrlPattern=modifyNewUrlPattern(newUrlPattern,request,model,domain.getLocale());
            return modifiedNewUrlPattern;		
		}
        redirectAttributes.addFlashAttribute("type", "success");
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/" + servletPath + "/"
        + ((BaseDomain) domain).getId() + "/edit";
        return returnUrl;
    }

    /**
     * Update.
     *
     * @param domain the domain
     * @param result the result
     * @param model the model
     * @param redirectAttributes the redirect attributes
     * @param request the request
     * @return the string
     */
    @Transactional
    @RequestMapping(method = RequestMethod.PUT)
    public String update(final @Valid @ModelAttribute("domain") T domain,
            final BindingResult result, 
            final ModelMap model,
            final RedirectAttributes redirectAttributes,
            final HttpServletRequest request) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        String messagePattern=servletPath.replaceAll("\\/",".");
        model.addAttribute("messagePattern", messagePattern);
        model.addAttribute("urlPattern", servletPath);	       
        try{
        	/*****Hook*************/
	        preValidateUpdate(domain, result, request);
	        /*****Hook*************/
	        validateUpdate(domain, result, request);
	        /*****Hook*************/
	        postValidateUpdate(domain, result, request);
	        /**********************/
	        model.addAttribute("domain", domain);
	        if (result.hasErrors()) {
	            /*****Hook*************/
	            populateUpdateIfErrors(model, domain, request);
	            String newUrlPattern=servletPath+"/edit";
	            String modifiedEditUrlPattern=modifyEditUrlPattern(newUrlPattern,request,model,domain.getLocale());
	            return modifiedEditUrlPattern;            
	        }
	        /*****Hook*************/
	        populateUpdateIfNoErrors(model, domain, request);
	        /**********************/
	        /****Partial Update ***************/
	        PartialUpdate partialUp=null;
	        try{
	        	partialUp = PartialUpdate.findByFieldName(
		                PartialUpdate.class, "urlPattern", servletPath, "");
	        }catch(EntityNotFoundException e){
	        	//logger.error(e.getMessage());
	        }catch(NoResultException e){
	        	//logger.error(e.getMessage());
	        }	        
	        if (partialUp != null) {
	            partialUpdate(domain, partialUp.getFieldsNotToBeOverwritten(),
	                    domain.getId());	
	        }
	        /**********************************/
	        trimString(model, domain, request);
	        ((BaseDomain) domain).merge();
	        populateAfterUpdate(model, domain, request);
        }catch (Exception e) {
        	logger.error(e.getMessage());
        	String message = null; 
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
			}else{
				message = e.getMessage();
				e.printStackTrace();
			}			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}			
			model.addAttribute("error", message);
	        populateUpdateIfErrors(model,domain, request);
	        String newUrlPattern=servletPath+"/edit";
            String modifiedEditUrlPattern=modifyEditUrlPattern(newUrlPattern,request,model,domain.getLocale());
            return modifiedEditUrlPattern; 			
		}        
        redirectAttributes.addFlashAttribute("type", "success");
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh new/edit request i.e after creating/updating records if we click on
        //new /edit then success message appears
        request.getSession().setAttribute("type","success");
        redirectAttributes.addFlashAttribute("msg", "update_success");
        String returnUrl = "redirect:/" + servletPath + "/"
        + ((BaseDomain) domain).getId() + "/edit";
        return returnUrl;
    }



    @SuppressWarnings("unchecked")
    private void partialUpdate(final T domain, 
    		final String partialUpdate, 
    		final Long id) {
        T tempDomain = (T)T.findById(domain.getClass(), id);
        Field[] declaredFields = domain.getClass().getDeclaredFields();
        Field[] superClassFields = domain.getClass().getSuperclass().getDeclaredFields();
        Field[] fieldsToUpdate = new Field[declaredFields.length + superClassFields.length];
        if (!domain.getClass().getSuperclass().getSimpleName().equals("BaseDomain")) {
            for (int i = 0; i < superClassFields.length; i++) {
                fieldsToUpdate[i] = superClassFields[i];
            }
            int j = 0;
            for (int i = superClassFields.length; i < (declaredFields.length + superClassFields.length); i++) {
                fieldsToUpdate[i] = declaredFields[j];
                j++;
            }
        }else {
            for (int i = 0; i < declaredFields.length; i++) {
                fieldsToUpdate[i] = declaredFields[i];
            }
        }

        for (Field i : fieldsToUpdate) {
            if(i!=null){
                if ((partialUpdate.contains(i.getName()))
                        || (i.getType().getSimpleName()
                                .contains("JoinPoint$StaticPart"))
                                || (i.getType().getSimpleName().contains("StaticPart"))
                                || (Modifier.isTransient(i.getModifiers()))) {
                }
                else {
                    try {
                        String fieldUppercaseName = new StringBuffer()
                        .append(String.valueOf(i.getName().charAt(0))
                                .toUpperCase())
                                .append(i.getName().substring(1)).toString();
                        Method domainMethod = domain.getClass()
                        .getMethod(
                                "set" + fieldUppercaseName,
                                new Class[] { Class.forName(i.getType()
                                        .getName()) });
                        Method tempDomainMethod = tempDomain.getClass().getMethod(
                                "get" + fieldUppercaseName, new Class[] {});
                        domainMethod.invoke(domain, tempDomainMethod.invoke(
                                tempDomain, new Object[] {}));
                    }
                    catch (IllegalArgumentException e) {
                        logger.error(e.getMessage());
                    }
                    catch (SecurityException e) {
                        logger.error(e.getMessage());
                    }
                    catch (ClassNotFoundException e) {
                        logger.error(e.getMessage());
                    }
                    catch (IllegalAccessException e) {
                        logger.error(e.getMessage());
                    }
                    catch (InvocationTargetException e) {
                        logger.error(e.getMessage());
                    }
                    catch (NoSuchMethodException e) {
                        logger.error(e.getMessage());
                    }

                }
            }
        }
    }

    /**
     * Delete.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     */
    @SuppressWarnings("static-access")
    @Transactional
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public String delete(final @PathVariable("id") Long id,
            final ModelMap model, 
            final HttpServletRequest request) {
        T object = null;
        try {
            object = domainClass.newInstance();
        }
        catch (InstantiationException e) {
            logger.error(e.getMessage());
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        BaseDomain domain = object;
        /**********Hook**************/
        Boolean status=preDelete(model, domain, request,id);
        /****************************/
        if(status){
        	try{
        		domain.findById(domainClass, id).remove();
        	}catch(Exception ex){        		
        		status=false;
        	}        
        }
        
        try {
			/**********Hook**************/
			postDelete(model, domain, request);
		} catch (Exception e) {
			logger.error(e.getMessage());
        	String message = null; 
			if(e instanceof ELSException){
				message = ((ELSException) e).getParameter();
			}else{
				message = e.getMessage();
				e.printStackTrace();
			}
			
			if(message == null){
				message = "There is some problem, request may not complete successfully.";
			}
			
			model.addAttribute("error", message);
		}
        /****************************/
        model.addAttribute("status",status);
        return "info";
    }

    /**
     * Inits the binder.
     *
     * @param binder the binder
     */
    @InitBinder(value = "domain")
    private void initBinder(final WebDataBinder binder) {
        // ***** Method to be overridden to provide custom implementation *****
        customInitBinder(domainClass, binder);
        // ********************************************************************
    }

    /**
     * Validate create.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    private void validateCreate(final T domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
        // ***** Method to be overridden to provide custom implementation *****
        customValidateCreate(domain, result, request);
        // ********************************************************************
    }

    /**
     * Validate update.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    private void validateUpdate(final T domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
        // ***** Method to be overridden to provide custom implementation *****
        customValidateUpdate(domain, result, request);
        // ********************************************************************
    }

    // ========== Custom Hooks for extending the default behaviors specified
    // below ==========

    /**
     * Custom init binder.
     *
     * @param <E> the element type
     * @param clazz the clazz
     * @param binder the binder
     */
    @SuppressWarnings({ "rawtypes" })
    protected <E extends BaseDomain> void customInitBinder(final Class clazz,
            final WebDataBinder binder) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field i : fields) {
            String strClassType = i.getType().getSimpleName();
            boolean isTransient = Modifier.isTransient(i.getModifiers());
            if (isTransient) {

            }
            // --JoinPoint$StaticPart is the field types injected by aspectJ
            else if (strClassType.equals("String")
                    || strClassType.equals("Date")
                    || strClassType.equals("Character")
                    || strClassType.equals("Integer")
                    || strClassType.equals("Float")
                    || strClassType.equals("Byte")
                    || strClassType.equals("Boolean")
                    || strClassType.equals("Number")
                    || strClassType.equals("Long")
                    || strClassType.equals("Double")
                    || strClassType.equals("BigDecimal")
                    || strClassType.equals("java.util.List")
                    || strClassType.equals("JoinPoint$StaticPart")
                    || strClassType.equals("StaticPart")
                    || strClassType.equals("int")
                    || strClassType.equals("short")
                    || strClassType.equals("long")
                    || strClassType.equals("boolean")
                    || strClassType.equals("float")
                    || strClassType.equals("double")
                    || strClassType.endsWith("Association")
                    || strClassType.endsWith("Map")) {

            }
            else if (strClassType.equals("List")) {
                if (!i.getGenericType().toString().contains("Association")) {
                    try {
                        final Class<?> clazzField = Class.forName(i
                                .getGenericType().toString().split("<")[1]
                                                                        .split(">")[0]);
                        binder.registerCustomEditor(List.class, i.getName(),
                                new CustomCollectionEditor(List.class) {

                            @Override
                            protected Object convertElement(
                                    final Object element) {
                                String id = null;

                                if (element instanceof String) {
                                    id = (String) element;
                                }

                                return id != null ? BaseDomain
                                        .findById(clazzField,
                                                Long.valueOf(id))
                                                : null;
                            }
                        });
                    }
                    catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
            else if (strClassType.equals("Set")) {
                if (!i.getGenericType().toString().contains("Association")) {
                    try {
                        String itemClass = i.getGenericType().toString()
                        .split("<")[1].split(">")[0];
                        final Class<?> clazzField = Class.forName(itemClass);
                        binder.registerCustomEditor(Set.class, i.getName(),
                                new CustomCollectionEditor(List.class) {

                            @Override
                            protected Object convertElement(
                                    final Object element) {
                                String id = null;
                                if (element instanceof String) {
                                    id = (String) element;
                                }
                                return id != null ? BaseDomain.findById(clazzField,Long.valueOf(id)): null;
                            }
                        });

                    }
                    catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }

            else {
                try {
                    Class<?> clazzField = Class.forName(i.getType().getName());
                    try {
                        binder.registerCustomEditor(clazzField, new BaseEditor((BaseDomain) clazzField.newInstance()));
                    }
                    catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        // Added by Anand
        // Set Date Editor and Number formatter to support marathi.
        NumberFormat nf = FormaterUtil.getNumberFormatterGrouping(this.getUserLocale().toString());
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, nf, true));

        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        SimpleDateFormat dateFormat = FormaterUtil.getDateFormatter(parameter.getValue(), this.getUserLocale().toString());
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));

        // This is to register the object references from the super classes.
        customInitBinderSuperClass(clazz, binder);
    }

    @SuppressWarnings("rawtypes")
    protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {

    }

    protected void populateModule(final ModelMap model,
            final HttpServletRequest request, final String locale,
            final AuthUser currentUser) {
    }

    protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
        model.addAttribute("houseType", this.getCurrentUser().getHouseType());
    }

    /**
     * Populate new.
     *
     * @param model the model
     * @param domain the domain
     * @param locale
     * @param request the request
     */
    protected void populateNew(final ModelMap model, 
    		final T domain,
            final String locale, 
            final HttpServletRequest request) {
        ((BaseDomain) domain).setLocale(locale);
    }

    /**
     * Populate edit.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void populateEdit(final ModelMap model, final T domain,
            final HttpServletRequest request) {

    }

    /**
     * Populate create if no errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void populateCreateIfNoErrors(final ModelMap model,
            final T domain, 
            final HttpServletRequest request) throws Exception {
        populateIfNoErrors(model, domain, request);
    }

    /**
     * Poulate create if errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void populateCreateIfErrors(final ModelMap model,
            final T domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "create_failed");
    }

    protected void populateAfterCreate(final ModelMap model, final T domain,
            final HttpServletRequest request) throws Exception {
    }

    /**
     * Populate update if no errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void populateUpdateIfNoErrors(final ModelMap model,
            final T domain, 
            final HttpServletRequest request) throws Exception {
        populateIfNoErrors(model, domain, request);
    }

    /**
     * Poulate update if errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void populateUpdateIfErrors(final ModelMap model, final T domain,
            final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "update_failed");
    }

    protected void populateAfterUpdate(final ModelMap model, 
    		final T domain,
            final HttpServletRequest request) throws Exception {
    }

    /**
     * Custom validate create.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    protected void customValidateCreate(final T domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Custom validate update.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    protected void customValidateUpdate(final T domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        customValidate(domain, result, request);
    }

    /**
     * Pre validate create.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    protected void preValidateCreate(final T domain,
            final BindingResult result, 
            final HttpServletRequest request) {

    }

    /**
     * Post validate create.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    protected void postValidateCreate(final T domain,
            final BindingResult result, 
            final HttpServletRequest request) {

    }

    /**
     * Pre validate update.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    protected void preValidateUpdate(final T domain,
            final BindingResult result, 
            final HttpServletRequest request) {

    }

    /**
     * Post validate update.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    protected void postValidateUpdate(final T domain,
            final BindingResult result, 
            final HttpServletRequest request) {

    }

    /**
     * Pre delete.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @param id 
     * @return 
     */
    protected Boolean preDelete(final ModelMap model, 
    		final BaseDomain domain,
            final HttpServletRequest request, Long id) {
    	return true;
    }

    /**
     * Post delete.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void postDelete(final ModelMap model, 
    		final BaseDomain domain,
            final HttpServletRequest request) {

    }

    // =================== Private Utility Methods ====================

    // Generic URL Construction.
    /**
     * Custom validate.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     */
    private void customValidate(final T domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
        // Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        // Check for duplicate instance if the instance has a field "name"
        try {
            String nameValue = null;
            try {
                nameValue = (String) domain.getClass()
                .getMethod("getName", new Class[]{}).invoke(domain, new Object[]{});
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            boolean duplicateParameter = domain.isDuplicate("name", nameValue);
            Object[] params = new Object[1];
            params[0] = nameValue;
            if (duplicateParameter) {
                result.rejectValue("name", "NonUnique", params,
                "Duplicate Parameter");
            }
        }
        catch (SecurityException e) {
            logger.error(e.getMessage());
            return;
        }
        catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return;
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage());
            return;

        }

    }

    /**
     * Populate if no errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     */
    protected void populateIfNoErrors(final ModelMap model, 
    		final T domain,
            final HttpServletRequest request) {
    }

    private void trimString(final ModelMap model, 
    		final T domain,
            final HttpServletRequest request) {
    	
        Field[] fields = domain.getClass().getDeclaredFields();
        for (Field i : fields) {
            String strClassType = i.getType().getSimpleName();            
            boolean isTransient = i.isAnnotationPresent(javax.persistence.Transient.class);
            if (strClassType.equals("String") && !isTransient) {
                try {
                    //if ((String) i.get(domain) != null) {
                    //  i.set(domain, ((String) i.get(domain)).trim());
                    //}
                    String attrName = i.getName();
                    String attrGetter = this.createGetter(attrName);
                    Method getter = domain.getClass().getMethod(attrGetter, new Class[] {});
                    String attrValue = (String)getter.invoke(domain, new Object[] {});
                    if(attrValue != null) {
                        String attrSetter = this.createSetter(attrName);
                        Method setter = domain.getClass().
                            getMethod(attrSetter, new Class[] {Class.forName(i.getType().getName())});
                        setter.invoke(domain, new Object[] {attrValue.trim()});
                    }
                }
                catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                } catch (SecurityException e) {
                    logger.error(e.getMessage());
                } catch (NoSuchMethodException e) {
                    logger.error(e.getMessage());
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                } catch (InvocationTargetException e) {
                    logger.error(e.getMessage());
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private String createGetter(final String fieldName){
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        sb.append(String.valueOf(fieldName.charAt(0)).toUpperCase());
        sb.append(fieldName.substring(1));
        return sb.toString();
    }

    private String createSetter(final String fieldName){
        StringBuffer sb = new StringBuffer();
        sb.append("set");
        sb.append(String.valueOf(fieldName.charAt(0)).toUpperCase());
        sb.append(fieldName.substring(1));
        return sb.toString();
    }

}
