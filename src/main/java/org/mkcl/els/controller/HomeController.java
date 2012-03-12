package org.mkcl.els.controller;

import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mkcl.els.domain.ApplicationLocale;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


@Controller
public class HomeController  {

    private static final Logger logger = LoggerFactory
            .getLogger(HomeController.class);
    
    private static final String DEFAULT_LOCALE="mr_IN";
    
    private static final String ASC = "asc";

	private static final String DESC = "desc";        
    
    
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) final String lang,
                        final Model model,
                        final HttpServletRequest request,
                        final HttpServletResponse response,
                        Locale locale) {    	
        List<ApplicationLocale> supportedLocales = ApplicationLocale.findAll(ApplicationLocale.class,"language",ASC, "");
        if (lang != null) {
            model.addAttribute("selectedLocale", lang);
        } else {
            model.addAttribute("selectedLocale", DEFAULT_LOCALE);
        }
        model.addAttribute("locales", supportedLocales);
        return "login";
    }
   
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(final ModelMap model,
                       final HttpServletRequest request,
                       final Locale locale) {
        String menuXml = MenuItem.getMenuXml(locale.toString());
        model.addAttribute("menu_xml", menuXml);
        model.addAttribute(
                "dateFormat",
                ((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DATEPICKER_DATEFORMAT", "")).getValue());
        model.addAttribute(
                "timeFormat",
                ((CustomParameter)CustomParameter.findByName(CustomParameter.class,"DATEPICKER_TIMEFORMAT", "")).getValue());
        return "home";
    }

}
