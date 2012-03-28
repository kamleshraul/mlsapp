package org.mkcl.els.controller;

import java.util.ArrayList;
import org.mkcl.els.domain.CustomParameter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.domain.Grid;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionPlace;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/masters_sessions")
public class SessionController extends GenericController<Session> {
	/** The Constant ASC. */
	private static final String ASC = "asc";

	@RequestMapping(value = "{id}/list", method = RequestMethod.GET)
	    public String list(@PathVariable final Long id,
	    					final ModelMap model,
	                       final Locale locale,
	                       final HttpServletRequest request,
	                       final @RequestParam(required = false) String formtype) {
	        final String urlPattern = request.getServletPath().split("\\/")[1];
	        Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
	        model.addAttribute("gridId", grid.getId());
	        model.addAttribute("urlPattern", urlPattern);
	        model.addAttribute("houseId",id);
	        return getURL(urlPattern) + "list";
	    }
	
	private String getURL(final String urlPattern) {
       String[] urlParts = urlPattern.split("_");
       StringBuffer buffer = new StringBuffer();
       for (String i : urlParts) {
           buffer.append(i + "/");
       }
       return buffer.toString();

   }
	
	 @RequestMapping(value = "{houseId}/new", method = RequestMethod.GET)
	    public String newForm(final @PathVariable Long houseId,
	    						final ModelMap model,
	                          final Locale locale,
	                          final HttpServletRequest request) {
	        final String urlPattern = request.getServletPath().split("\\/")[1];
	        Session domain = null;
	        try {
	        	domain = (Session.class).newInstance();
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
	        model.addAttribute("houseId",houseId);
	        return getURL(urlPattern) + "new";
	    }

	 
	 	
	   @Override
	protected void customValidateCreate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	@Override
	protected void customValidateUpdate(final Session domain,
			final BindingResult result, final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	private void customValidate(final Session sessiondetails,
			final BindingResult result, final HttpServletRequest request) {
		new HashMap<String, String>();
		if (sessiondetails.isVersionMismatch()) {
			result.rejectValue("VersionMismatch", "version");
		}
		if (result.hasErrors()) {
			System.out.println("error");
		}
	}

	
    protected void populateNew(	final ModelMap model,
                               final Session domain,
                               final Locale locale,
                               final HttpServletRequest request) {
		 String sessiontype = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_SESSIONTYPE", locale.toString())).getValue();
    	 String sessionplace = ((CustomParameter) CustomParameter.findByName(
                 CustomParameter.class, "DEFAULT_SESSIONPLACE", locale.toString())).getValue();
    	 populate(model, domain, locale, request, sessiontype,sessionplace);
    	 
    }
	
	
    protected void populateEdit(final ModelMap model,
                                final Session domain,
                                final HttpServletRequest request) {
    	String sessiontype = domain.getType().getSessionType();
    	String sessionplace=domain.getPlace().getPlace();
    	House house=domain.getHouse();
    	populate(model, domain, new Locale(domain.getLocale()),request,
    	         sessiontype,sessionplace);
    	model.addAttribute("houseId", house.getId());
    	model.addAttribute("session", domain);
    	 	
    }

	
	 protected void populate(final ModelMap model, final Session domain, final Locale locale,
				final HttpServletRequest request, final String sessiontype,final String sessionplace) {
			// TODO Auto-generated method stub
		   domain.setLocale(locale.toString());
	       List<SessionType> sessionType = SessionType.findAll(
	    		   SessionType.class, "sessionType", "asc", locale.toString());
	       SessionType selectedsessions = SessionType.findByFieldName(
	    		   SessionType.class, "sessionType", sessiontype, locale.toString());
	       List<SessionType> newsessiontype = new ArrayList<SessionType>();
	       newsessiontype.add(selectedsessions);
	       sessionType.remove(selectedsessions);
	       newsessiontype.addAll(sessionType);
	       model.addAttribute("sessionType", newsessiontype);
	       	       
	       List<SessionPlace> sessionPlace = SessionPlace.findAll(
	    		   SessionPlace.class, "place", "asc", locale.toString());
	       SessionPlace selectedplace = SessionPlace.findByFieldName(
	    		   SessionPlace.class, "place", sessionplace, locale.toString());
	       List<SessionPlace> newsessionplace = new ArrayList<SessionPlace>();
	       newsessionplace.add(selectedplace);
	       sessionPlace.remove(selectedplace);
	       newsessionplace.addAll(sessionPlace);
	       model.addAttribute("place",newsessionplace);
	       
	     }


}

