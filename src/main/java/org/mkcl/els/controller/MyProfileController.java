package org.mkcl.els.controller;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.util.PasswordValidator;
import org.mkcl.els.domain.Credential;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/myprofile")
public class MyProfileController extends BaseController {
	
	@Autowired 
	private PasswordValidator passwordValidator;
	
	@Autowired 
	private ISecurityService securityService;
	
	@RequestMapping(value = "/password", method = RequestMethod.GET)
    public String changePasswordInit(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        /** username **/
        model.addAttribute("username", this.getCurrentUser().getActualUsername());    
        /** existing password **/
        if(request.getSession().getAttribute("existingPassword")==null){
            model.addAttribute("existingPassword","");
        }else{
        	model.addAttribute("existingPassword",request.getSession().getAttribute("existingPassword"));
            request.getSession().removeAttribute("existingPassword");
        }
        /** new password **/
        if(request.getSession().getAttribute("newPassword")==null){
            model.addAttribute("newPassword","");
        }else{
        	model.addAttribute("newPassword",request.getSession().getAttribute("newPassword"));
            request.getSession().removeAttribute("newPassword");
        }
        /** confirmed password **/
        if(request.getSession().getAttribute("confirmedPassword")==null){
            model.addAttribute("confirmedPassword","");
        }else{
        	model.addAttribute("confirmedPassword",request.getSession().getAttribute("confirmedPassword"));
            request.getSession().removeAttribute("confirmedPassword");
        }
        
        /** birth date **/
        if(request.getSession().getAttribute("birthDate")==null){
            model.addAttribute("birthDate","");
        }else{
        	model.addAttribute("birthDate",request.getSession().getAttribute("birthDate"));
            request.getSession().removeAttribute("birthDate");
        }
        CustomParameter cpSecretKey = CustomParameter.findByName(CustomParameter.class, "SECRET_KEY_FOR_ENCRYPTION", "");
        if(cpSecretKey != null){
        	model.addAttribute("secret_key", cpSecretKey.getValue());
        }
        CustomParameter isPasswordValidationRequired = CustomParameter.findByName(CustomParameter.class, "PASSWORD_STRING_VALIDATION_REQUIREMENT", "");
        if(isPasswordValidationRequired!=null && isPasswordValidationRequired.getValue()!=null) {
        	model.addAttribute("isPasswordValidationRequired", isPasswordValidationRequired.getValue());
        }
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh request
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
        }
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("password","error");
        }else{
            return servletPath;
        }
    }
	
	@RequestMapping(value = "/password", method = RequestMethod.POST)
    public String changePasswordUpdate(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) {
		boolean isError = false;
		String username = request.getParameter("username");
		String existingPassword = request.getParameter("existingPassword");
		String newPassword = request.getParameter("newPassword");
		String confirmedPassword = request.getParameter("confirmedPassword");
		String birthDate = request.getParameter("birthDate");
		String birthDateFormat = ApplicationConstants.SERVER_DATEFORMAT;
		User user = null;
		
		if(username!=null && !username.isEmpty() && existingPassword!=null && !existingPassword.isEmpty()
				&& newPassword!=null && !newPassword.isEmpty() && confirmedPassword!=null
				&& !confirmedPassword.isEmpty() && newPassword.equals(confirmedPassword)) {					
			try {
				user = User.findByUserName(username, locale.toString());
			} catch (ELSException e) {
				e.printStackTrace();
				//error (user not found)
				isError = true;	
			}
			if(!isError && user!=null && user.getId()!=null) {
				Credential credential = user.getCredential();
				if(credential!=null) {
					if(securityService.isAuthenticated(existingPassword, credential.getPassword())) {
						String isPasswordValidationRequired = request.getParameter("isPasswordValidationRequired");
						if(isPasswordValidationRequired!=null && isPasswordValidationRequired.equals("yes")) {
							if(passwordValidator.validate(newPassword)) {
								Date currentDate = new Date();
								String encodedPassword = securityService.getEncodedPassword(newPassword);
								credential.setPassword(encodedPassword);
								credential.setPasswordChangeCount(credential.getPasswordChangeCount()+1);
								credential.setPasswordChangeDateTime(currentDate);
								credential.merge();
								
							} else {
								//error (password not set in valid format)
								isError = true;
							}
						} else {
							Date currentDate = new Date();
							String encodedPassword = securityService.getEncodedPassword(newPassword);
							credential.setPassword(encodedPassword);
							credential.setPasswordChangeCount(credential.getPasswordChangeCount()+1);
							credential.setPasswordChangeDateTime(currentDate);
							credential.merge();
						}	
						
						Date bDate = null;
						if(birthDate!=null && !birthDate.isEmpty()) {
							try {
								bDate = FormaterUtil.formatStringToDate(birthDate, birthDateFormat);
							} catch(Exception e) {
								e.printStackTrace();
								bDate = user.getBirthDate();
							}									
						} else {
							bDate = user.getBirthDate();
						}
						if(bDate==null) { //this was needed as parsing invalid birthDate does not throw exception from FormaterUtil.formatStringToDate()
							bDate = user.getBirthDate();
						}
						
						Set<org.mkcl.els.domain.Role> roles = credential.getRoles();								
						for(org.mkcl.els.domain.Role r : roles){
							if(r != null){
								if(r.getType().startsWith("MEMBER")){
									Member member=Member.findMember(this.getCurrentUser().getFirstName(),
											this.getCurrentUser().getMiddleName(), this.getCurrentUser().getLastName(),
											this.getCurrentUser().getBirthDate(), locale.toString());
									
									member.setBirthDate(bDate);
									member.merge();
								}
							}
						}
						
						user.setBirthDate(bDate);
						user.merge();
						this.getCurrentUser().setBirthDate(bDate);							
												
					} else {
						//error (user not authenticated.. wrong password attempt)
						isError = true;
					}
				} else {
					//error (credential not found)
					isError = true;
				}
			} else {
				//error (user not found)
				isError = true;
			}
		} else {
			//error (parameter/s invalid)
			isError = true;
		}
		
		if(isError) {
			redirectAttributes.addFlashAttribute("type", "error");
			request.getSession().setAttribute("type","error");
	        redirectAttributes.addFlashAttribute("msg", "update_error");
	        request.getSession().setAttribute("existingPassword", existingPassword);
	        
		} else {
			redirectAttributes.addFlashAttribute("type", "success");
	        //this is done so as to remove the bug due to which update message appears even though there
	        //is a fresh request
	        request.getSession().setAttribute("type","success");
	        redirectAttributes.addFlashAttribute("msg", "update_success");
	        request.getSession().setAttribute("existingPassword", newPassword); //as password is updated to new value in this case
		}
		
		request.getSession().setAttribute("newPassword", newPassword);	
		request.getSession().setAttribute("confirmedPassword", confirmedPassword);
		if(birthDate!=null && !birthDate.isEmpty()) {
			request.getSession().setAttribute("birthDate", birthDate);
		} else {
			if(user!=null && user.getId()!=null) {
				request.getSession().setAttribute("birthDate", FormaterUtil.formatDateToString(user.getBirthDate(), birthDateFormat));
			} else {
				request.getSession().setAttribute("birthDate", "");
			}
		}		
        String returnUrl = "redirect:/" + request.getServletPath().replaceFirst("\\/","");
        return returnUrl;
    }
	
	@RequestMapping(value = "/high_security_password", method = RequestMethod.GET)
    public String changeHighSecurityPasswordInit(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) throws ELSException {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        /** username **/
        String username = this.getCurrentUser().getActualUsername();
        model.addAttribute("username", username);    
        /** existing high security password **/
        if(request.getSession().getAttribute("existingHighSecurityPassword")==null){
            model.addAttribute("existingHighSecurityPassword","");
        }else{
        	model.addAttribute("existingHighSecurityPassword",request.getSession().getAttribute("existingHighSecurityPassword"));
            request.getSession().removeAttribute("existingHighSecurityPassword");
        }
        /** new high security password **/
        if(request.getSession().getAttribute("newHighSecurityPassword")==null){
            model.addAttribute("newHighSecurityPassword","");
        }else{
        	model.addAttribute("newHighSecurityPassword",request.getSession().getAttribute("newHighSecurityPassword"));
            request.getSession().removeAttribute("newHighSecurityPassword");
        }
        /** confirmed high security password **/
        if(request.getSession().getAttribute("confirmedHighSecurityPassword")==null){
            model.addAttribute("confirmedHighSecurityPassword","");
        }else{
        	model.addAttribute("confirmedHighSecurityPassword",request.getSession().getAttribute("confirmedHighSecurityPassword"));
            request.getSession().removeAttribute("confirmedHighSecurityPassword");
        }
        CustomParameter cpSecretKey = CustomParameter.findByName(CustomParameter.class, "SECRET_KEY_FOR_ENCRYPTION", "");
        if(cpSecretKey != null){
        	model.addAttribute("secret_key", cpSecretKey.getValue());
        }
        CustomParameter isHighSecurityPasswordValidationRequired = CustomParameter.findByName(CustomParameter.class, "HIGH_SECURITY_PASSWORD_STRING_VALIDATION_REQUIREMENT", "");
        if(isHighSecurityPasswordValidationRequired!=null && isHighSecurityPasswordValidationRequired.getValue()!=null) {
        	model.addAttribute("isHighSecurityPasswordValidationRequired", isHighSecurityPasswordValidationRequired.getValue());
        }
        User user = User.findByUserName(username, locale.toString());
		if(user!=null && user.getId()!=null) {
			if(user.getCredential()!=null) {
				model.addAttribute("existingPassword", user.getCredential().getPassword());
			}
		}
        //this is done so as to remove the bug due to which update message appears even though there
        //is a fresh request
        if(request.getSession().getAttribute("type")==null){
            model.addAttribute("type","");
        }else{
        	model.addAttribute("type",request.getSession().getAttribute("type"));
            request.getSession().removeAttribute("type");
        }
        //here making provisions for displaying error pages
        if(model.containsAttribute("errorcode")){
            return servletPath.replace("high_security_password","error");
        }else{
            return servletPath;
        }
    }
	
	@RequestMapping(value = "/high_security_password", method = RequestMethod.POST)
    public String changeHighSecurityPasswordUpdate(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) {
		String username = request.getParameter("username");
		String existingHighSecurityPassword = request.getParameter("existingHighSecurityPassword");
		String newHighSecurityPassword = request.getParameter("newHighSecurityPassword");
		String confirmedHighSecurityPassword = request.getParameter("confirmedHighSecurityPassword");
		
		if(username!=null && !username.isEmpty() && existingHighSecurityPassword!=null && !existingHighSecurityPassword.isEmpty()
				&& newHighSecurityPassword!=null && !newHighSecurityPassword.isEmpty() && confirmedHighSecurityPassword!=null
				&& !confirmedHighSecurityPassword.isEmpty() && newHighSecurityPassword.equals(confirmedHighSecurityPassword)) {
			User user = null;			
			try {
				user = User.findByUserName(username, locale.toString());
			} catch (ELSException e) {
				e.printStackTrace();
				//error
				redirectAttributes.addFlashAttribute("type", "error");
				request.getSession().setAttribute("type","error");
		        redirectAttributes.addFlashAttribute("msg", "update_error");
			}
			if(user!=null && user.getId()!=null) {
				Credential credential = user.getCredential();
				if(credential!=null) {
					if(securityService.isAuthenticated(existingHighSecurityPassword, credential.getHighSecurityPassword())) {
						String isHighSecurityPasswordValidationRequired = request.getParameter("isHighSecurityPasswordValidationRequired");
						if(isHighSecurityPasswordValidationRequired!=null && isHighSecurityPasswordValidationRequired.equals("yes")) {
							if(passwordValidator.validate(newHighSecurityPassword)) {
								//Date currentDate = new Date();
								String encodedHighSecurityPassword = securityService.getEncodedPassword(newHighSecurityPassword);
								credential.setHighSecurityPassword(encodedHighSecurityPassword);
								//credential.setHighSecurityPasswordChangeCount(credential.getHighSecurityPasswordChangeCount()+1);
								//credential.setHighSecurityPasswordChangeDateTime(currentDate);
								credential.merge();	
								user.merge();								
								
								redirectAttributes.addFlashAttribute("type", "success");
						        //this is done so as to remove the bug due to which update message appears even though there
						        //is a fresh request
						        request.getSession().setAttribute("type","success");
						        redirectAttributes.addFlashAttribute("msg", "update_success");
							} else {
								//error
								redirectAttributes.addFlashAttribute("type", "error");
								request.getSession().setAttribute("type","error");
						        redirectAttributes.addFlashAttribute("msg", "update_error");
							}
						} else {
							//Date currentDate = new Date();
							String encodedHighSecurityPassword = securityService.getEncodedPassword(newHighSecurityPassword);
							credential.setHighSecurityPassword(encodedHighSecurityPassword);
							//credential.setHighSecurityPasswordChangeCount(credential.getHighSecurityPasswordChangeCount()+1);
							//credential.setHighSecurityPasswordChangeDateTime(currentDate);
							credential.merge();						
							redirectAttributes.addFlashAttribute("type", "success");
					        //this is done so as to remove the bug due to which update message appears even though there
					        //is a fresh request
					        request.getSession().setAttribute("type","success");
					        redirectAttributes.addFlashAttribute("msg", "update_success");
						}												
					} else {
						//error
						redirectAttributes.addFlashAttribute("type", "error");
						request.getSession().setAttribute("type","error");
				        redirectAttributes.addFlashAttribute("msg", "update_error");
					}
				}
			}
		} else {
			//error
			redirectAttributes.addFlashAttribute("type", "error");
			request.getSession().setAttribute("type","error");
	        redirectAttributes.addFlashAttribute("msg", "update_error");
		} 
		request.getSession().setAttribute("existingHighSecurityPassword", existingHighSecurityPassword);
		request.getSession().setAttribute("newHighSecurityPassword", newHighSecurityPassword);	
		request.getSession().setAttribute("confirmedHighSecurityPassword", confirmedHighSecurityPassword);
		String returnUrl = "redirect:/" + request.getServletPath().replaceFirst("\\/","");
        return returnUrl;
    }

}
