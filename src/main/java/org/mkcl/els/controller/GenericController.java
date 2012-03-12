/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.GenericController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.editors.BaseEditor;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Grid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author sandeeps
 * @since v1.0.0
 */
public class GenericController<T extends BaseDomain> extends BaseController {

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** The domain class. */
    @SuppressWarnings("unchecked")
    private Class<T> domainClass = (Class<T>) DAOUtil.getTypeArguments(
            GenericController.class, this.getClass()).get(0);

    // ========================= API =========================
    /**
     * Index.
     *
     * @param model the model
     * @param request the request
     * @param formtype the formtype
     * @return the string
     * @author sandeeps
     * @since v1.0.0
     */
    @RequestMapping(value = "/module", method = RequestMethod.GET)
    public String index(final ModelMap model,
                        final HttpServletRequest request,
                        final @RequestParam(required = false) String formtype) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        model.addAttribute("urlPattern", urlPattern);
        if (formtype != null) {
            if (formtype.equals("g")) {
                model.addAttribute("urlPattern", urlPattern);
                return "generics/module";
            }
        }
        return getURL(urlPattern) + "module";
    }

    /**
     * List.
     *
     * @param model the model
     * @param locale the locale
     * @param request the request
     * @param formtype the formtype
     * @return the string
     * @author sandeeps
     * @since v1.0.0
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(final ModelMap model,
                       final Locale locale,
                       final HttpServletRequest request,
                       final @RequestParam(required = false) String formtype) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
        model.addAttribute("gridId", grid.getId());
        if (formtype != null) {
            if (formtype.equals("g")) {
                model.addAttribute("urlPattern", urlPattern);
                return "generics/list";
            }
        }
        return getURL(urlPattern) + "list";
    }

    /**
     * New form.
     *
     * @param model the model
     * @param locale the locale
     * @param request the request
     * @return the string
     * @author sandeeps
     * @since v1.0.0
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final ModelMap model,
                          final Locale locale,
                          final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        T domain = null;
        try {
            domain = domainClass.newInstance();
        }
        catch (InstantiationException e) {
            logger.error(e.getMessage());
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        // ***** Method to be overridden to provide custom implementation *****
        populateNew(model, domain, locale, request);
        // ********************************************************************
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return getURL(urlPattern) + "new";
    }

    /**
     * Edits the.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String edit(final @PathVariable("id") Long id,
                       final ModelMap model,
                       final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        T domain = (T) BaseDomain.findById(domainClass, id);
        // ***** Method to be overridden to provide custom implementation *****
        populateEdit(model, domain, request);
        // ********************************************************************
        model.addAttribute("domain", domain);
        model.addAttribute("urlPattern", urlPattern);
        return getURL(urlPattern) + "edit";
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
     * @author sandeeps
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.POST)
    public String create(final ModelMap model,
                         final HttpServletRequest request,
                         final RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) final String locale,
                         @Valid @ModelAttribute("domain") final T domain,
                         final BindingResult result) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        // ***** Method to be overridden to provide custom implementation *****
        preValidateCreate(domain, result, request);
        // ********************************************************************
        validateCreate(domain, result, request);
        // ***** Method to be overridden to provide custom implementation *****
        postValidateCreate(domain, result, request);
        // ********************************************************************
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            // ***** Method to be overridden to provide custom implementation
            // *****
            poulateCreateIfErrors(model, domain, request);
            // ********************************************************************
            return getURL(urlPattern) + "new";
        }
        // ***** Method to be overridden to provide custom implementation *****
        populateCreateIfNoErrors(model, domain, request);
        // ********************************************************************
        ((BaseDomain) domain).persist();
        request.getSession().setAttribute("refresh", "");
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("msg", "create_success");
        String returnUrl = "redirect:/" + urlPattern + "/"
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
     * @author sandeeps
     * @since v1.0.0
     */
    @RequestMapping(method = RequestMethod.PUT)
    public String update(final @Valid @ModelAttribute("domain") T domain,
                         final BindingResult result,
                         final ModelMap model,
                         final RedirectAttributes redirectAttributes,
                         final HttpServletRequest request) {
        final String urlPattern = request.getServletPath().split("\\/")[1];
        // ***** Method to be overridden to provide custom implementation *****
        preValidateUpdate(domain, result, request);
        // ********************************************************************
        validateUpdate(domain, result, request);
        // ***** Method to be overridden to provide custom implementation *****
        postValidateUpdate(domain, result, request);
        // ********************************************************************
        model.addAttribute("domain", domain);
        if (result.hasErrors()) {
            // ***** Method to be overridden to provide custom implementation
            // *****
            poulateUpdateIfErrors(model, domain, request);
            // ********************************************************************
            return getURL(urlPattern) + "edit";
        }
        // ***** Method to be overridden to provide custom implementation *****
        populateUpdateIfNoErrors(model, domain, request);
        // ********************************************************************
        ((BaseDomain) domain).merge();
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("msg", "update_success");
        String returnUrl = "redirect:/" + urlPattern + "/"
                + ((BaseDomain) domain).getId() + "/edit";
        return returnUrl;
    }

    /**
     * Delete.
     *
     * @param id the id
     * @param model the model
     * @param request the request
     * @return the string
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings("static-access")
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
        // ***** Method to be overridden to provide custom implementation *****
        preDelete(model, domain, request);
        // ********************************************************************
        domain.findById(domainClass, id).remove();
        // ***** Method to be overridden to provide custom implementation *****
        postDelete(model, domain, request);
        // ********************************************************************
        return "info";
    }

    /**
     * Inits the binder.
     *
     * @param binder the binder
     * @author sandeeps
     * @since v1.0.0 Inits the binder.
     */
    @SuppressWarnings("unused")
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
     * @author sandeeps
     * @since v1.0.0 Validate create.
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
     * @author sandeeps
     * @since v1.0.0 Validate update.
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
     * @author sandeeps
     * @since v1.0.0 Custom init binder.
     */
    @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
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
                    || strClassType.equals("Character")
                    || strClassType.equals("Integer")
                    || strClassType.equals("Float")
                    || strClassType.equals("Byte")
                    || strClassType.equals("Boolean")
                    || strClassType.equals("Number")
                    || strClassType.equals("Long")
                    || strClassType.equals("Double")
                    || strClassType.equals("java.util.List")
                    || strClassType.equals("JoinPoint$StaticPart")
                    || strClassType.equals("StaticPart")
                    || strClassType.equals("int")
                    || strClassType.equals("short")
                    || strClassType.equals("long")
                    || strClassType.equals("boolean")
                    || strClassType.equals("float")
                    || strClassType.equals("double")) {

            }
            else if (strClassType.equals("List") || strClassType.equals("Set")) {
                try {
                    Class<?> clazzField = Class.forName(i.getGenericType()
                            .toString().split("<")[1].split(">")[0]);
                    binder.registerCustomEditor(clazzField, new BaseEditor(
                            (BaseDomain) clazzField.newInstance()));
                }
                catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            else {
                try {
                    Class<?> clazzField = Class.forName(i.getType().getName());
                    try {
                        binder.registerCustomEditor(clazzField, new BaseEditor(
                                (BaseDomain) clazzField.newInstance()));
                    }
                    catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Logic for adding custom attributes to "model" while creating a new form.
     * This method will be called from newForm().
     *
     * @param model the model
     * @param domain the domain
     * @param locale the locale
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Populate new.
     */
    protected void populateNew(final ModelMap model,
                               final T domain,
                               final Locale locale,
                               final HttpServletRequest request) {
        ((BaseDomain) domain).setLocale(locale.toString());
    }

    /**
     * Populate edit.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Populate edit.
     */
    protected void populateEdit(final ModelMap model,
                                final T domain,
                                final HttpServletRequest request) {

    }

    /**
     * Populate create if no errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Populate create if no errors.
     */
    protected void populateCreateIfNoErrors(final ModelMap model,
                                            final T domain,
                                            final HttpServletRequest request) {
        populateIfNoErrors(model, domain, request);
    }

    /**
     * Poulate create if errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Poulate create if errors.
     */
    protected void poulateCreateIfErrors(final ModelMap model,
                                         final T domain,
                                         final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "create_failed");
    }

    /**
     * Populate update if no errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Populate update if no errors.
     */
    protected void populateUpdateIfNoErrors(final ModelMap model,
                                            final T domain,
                                            final HttpServletRequest request) {
        populateIfNoErrors(model, domain, request);
    }

    /**
     * Poulate update if errors.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Poulate update if errors.
     */
    protected void poulateUpdateIfErrors(final ModelMap model,
                                         final T domain,
                                         final HttpServletRequest request) {
        populateEdit(model, domain, request);
        model.addAttribute("type", "error");
        model.addAttribute("msg", "update_failed");
    }

    /**
     * Custom validate create.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Custom validate create.
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
     * @author sandeeps
     * @since v1.0.0 Custom validate update.
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
     * @author sandeeps
     * @since v1.0.0 Pre validate create.
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
     * @author sandeeps
     * @since v1.0.0 Post validate create.
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
     * @author sandeeps
     * @since v1.0.0 Pre validate update.
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
     * @author sandeeps
     * @since v1.0.0 Post validate update.
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
     * @author sandeeps
     * @since v1.0.0 Pre delete.
     */
    protected void preDelete(final ModelMap model,
                             final BaseDomain domain,
                             final HttpServletRequest request) {

    }

    /**
     * Post delete.
     *
     * @param model the model
     * @param domain the domain
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Post delete.
     */
    protected void postDelete(final ModelMap model,
                              final BaseDomain domain,
                              final HttpServletRequest request) {

    }

    // =================== Private Utility Methods ====================

    // Generic URL Construction.
    /**
     * Gets the uRL.
     *
     * @param urlPattern the url pattern
     * @return the uRL
     */
    private String getURL(final String urlPattern) {
        String[] urlParts = urlPattern.split("_");
        StringBuffer buffer = new StringBuffer();
        for (String i : urlParts) {
            buffer.append(i + "/");
        }
        return buffer.toString();

    }

    /**
     * Custom validate.
     *
     * @param domain the domain
     * @param result the result
     * @param request the request
     * @author sandeeps
     * @since v1.0.0 Custom validate.
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
                        .getMethod("getName", null).invoke(domain, null);
            }
            catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            boolean duplicateParameter = domain.isDuplicate("name", nameValue);
            Object[] params = new Object[1];
            params[0] = nameValue;
            if (duplicateParameter) {
                result.rejectValue(
                        "name", "NonUnique", params, "Duplicate Parameter");
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
     * @author sandeeps
     * @since v1.0.0 Populate if no errors.
     */
    private void populateIfNoErrors(final ModelMap model,
                                    final T domain,
                                    final HttpServletRequest request) {
        Field[] fields = domain.getClass().getDeclaredFields();
        for (Field i : fields) {
            String strClassType = i.getType().getSimpleName();
            if (strClassType.equals("String")) {
                try {
                    // String
                    // value=(String)domain.getClass().getMethod("get"+i.getName(),null).invoke(domain,null);
                    if ((String) i.get(domain) != null) {
                        i.set(domain, ((String) i.get(domain)).trim());
                    }
                }
                catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                }
                catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

}