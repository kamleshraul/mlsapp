package org.mkcl.els.controller.feedback;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.BaseController;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.Feedback;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/feedback")
public class FeedbackController extends BaseController {
	

	@RequestMapping(value="/createfeedback",method=RequestMethod.POST)
	public @ResponseBody boolean CreateFeedback(ModelMap model, Feedback domain,HttpServletRequest request) {
		 boolean result = false;
		try {
			Locale locale = this.getUserLocale();
			HouseType houseType;
			Session session;
			domain.setRatings(Integer.parseInt(request.getParameter("ratingSystem")));
			domain.setTotalRatings(5);
			Credential credential = Credential.findByFieldName(Credential.class, "username", this.getCurrentUser().getActualUsername(), "");
			domain.setCredential(credential);
			User authenticatedUser = User.findByFieldName(User.class, "credential", credential, locale.toString());			
			if(authenticatedUser.getHouseType().getType().equals("bothhouse")) {
				houseType = HouseType.findByType("lowerhouse", locale.toString());
				session = Session.findLatestSession(houseType);
			}else {
				houseType = HouseType.findByType(authenticatedUser.getHouseType().getType(), locale.toString());
				session = Session.findLatestSession(houseType);
			}
			domain.setLocale(locale.toString());
			domain.setSession(session);
			domain.setFeedback_content(request.getParameter("feedback_content"));
			domain.setCreationDate(new Date());	
			domain.persist();
			result = true;
			return result;
		} catch(Exception e) {
			model.addAttribute("error",e.getMessage());
		}
		return result;
	}
	
	   @RequestMapping(value = "/success", method = RequestMethod.POST)
	    public String redirectToHomeSuccess(final ModelMap model, final HttpServletRequest request, final Locale locale) {
	      	return "feedback/success";
	    }
	   
	   @RequestMapping(value = "/failure", method = RequestMethod.POST)
	    public String redirectToHomeFailure(final ModelMap model, final HttpServletRequest request, final Locale locale) {
	      	return "feedback/failure";
	    }
}
