package org.mkcl.els.controller.ris;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.controller.NotificationController;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Reporter;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roster/slot")
public class SlotController extends GenericController<Slot>{

	@Override
	protected void populateEdit(ModelMap model, Slot domain,
			HttpServletRequest request) {
		/**** Start/End Time ****/
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(customParameter!=null){
			SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
			if(domain.getStartTime()!=null){
				model.addAttribute("startTime",format.format(domain.getStartTime()));
			}
			if(domain.getEndTime()!=null){
				model.addAttribute("endTime",format.format(domain.getEndTime()));
			}
		}
		/**** Reporter ****/
		HouseType houseType=domain.findHouseType();
		Language language=domain.findLanguage();
		String locale=domain.getLocale();
		CustomParameter customParameter1=CustomParameter.findByName(CustomParameter.class,"RIS_ROLES_ALLOTED_TO_SLOT","");
		if(customParameter1!=null){
			List<User> allRISUsers = new ArrayList<User>();
			if(houseType!=null&&language!=null){
				if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)){
					allRISUsers=User.findByRole(false,customParameter1.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,"");
				}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)){
					allRISUsers=User.findByRole(false,customParameter1.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.DESC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,"");
				}
			}
			if(allRISUsers.isEmpty()){
				allRISUsers = User.findByRole(false,customParameter1.getValue(),language.getName(),"houseType,joiningDate,lastName",ApplicationConstants.ASC+","+ApplicationConstants.ASC+","+ApplicationConstants.ASC,locale,"");
			}
			model.addAttribute("users",allRISUsers);
		}
		if(domain.findUser()!=null){
			model.addAttribute("user",domain.findUser().getId());			
		}
		if(domain.getReporter()!=null){
			model.addAttribute("reporter",domain.getReporter().getId());
			model.addAttribute("reporterPosition",domain.getReporter().getPosition());
		}
		/**** Roster ****/
		model.addAttribute("roster", domain.getRoster().getId());
		model.addAttribute("isCompleted",domain.getCompleted());
	}

	@Override
	protected void customValidateUpdate(Slot domain, BindingResult result,
			HttpServletRequest request) {
		String strUser=request.getParameter("user");
		if(strUser!=null&&!strUser.isEmpty()){
			User user=User.findById(User.class,Long.parseLong(strUser));
			if(user!=null){
				String strPosition = request.getParameter("position");
				Reporter reporter=new Reporter();
				reporter.setLocale(domain.getLocale());
				if(strPosition != null && !strPosition.isEmpty()){
					reporter.setPosition(Integer.parseInt(strPosition));
				}
				reporter.setUser(user);
				domain.setReporter(reporter);
			}
		}else{
			result.rejectValue("reporter","ReporterEmpty");
		}
		if(domain.getReporter()==null){
			result.rejectValue("reporter","ReporterEmpty");
		}
		/**** Start Time and End Time****/
		String strStartTime=request.getParameter("selectedStartTime");
		String strEndTime=request.getParameter("selectedEndTime");
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"ROSTER_DATETIMEFORMAT","");
		if(strStartTime!=null&&!strStartTime.isEmpty()){			
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setStartTime(format.parse(strStartTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setStartTime(defaultFormat.parse(strStartTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(strEndTime!=null&&!strEndTime.isEmpty()){
			if(customParameter!=null){
				SimpleDateFormat format=FormaterUtil.getDateFormatter(customParameter.getValue(),domain.getLocale());
				try {
					domain.setEndTime(format.parse(strEndTime));
				} catch (ParseException e) {
					SimpleDateFormat defaultFormat=FormaterUtil.getDateFormatter(customParameter.getValue(),"en_US");
					try {
						domain.setEndTime(defaultFormat.parse(strEndTime));
					} catch (ParseException e1) {
						logger.error("Unparseable Timestamp:"+strStartTime+","+strEndTime,e1);;
					}
				}
			}
		}
		if(domain.getStartTime()==null){
			result.rejectValue("startTime","StartTimeEmpty");
		}
		if(domain.getEndTime()==null){
			result.rejectValue("endTime","EndTimeEmpty");
		}		
	}
	
	@Override
	protected void populateAfterUpdate(ModelMap model, Slot domain,
			HttpServletRequest request) {
		/**** Update Roster with newly added reporter ****/
		Roster roster=domain.getRoster();
		List<Reporter> reporters=roster.getReporters();
		if(reporters!=null&&!reporters.isEmpty()){
			domain.getReporter().setIsActive(true);
			domain.getReporter().persist();
			reporters.add(domain.getReporter());			
			roster.setReporters(reporters);
			roster.merge();			
		}else if(reporters==null){
			reporters=new ArrayList<Reporter>();
			domain.getReporter().setIsActive(true);
			domain.getReporter().persist();
			reporters.add(domain.getReporter());
			roster.setReporters(reporters);
			roster.merge();			
		}else if(reporters.isEmpty()){
			domain.getReporter().setIsActive(true);
			domain.getReporter().persist();
			reporters.add(domain.getReporter());
			roster.setReporters(reporters);
			roster.merge();			
		}
	}
	
	@RequestMapping(value = "/sendReminderNotification", method = RequestMethod.GET)
    public String sendReminderNotificationInit(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) {
        final String servletPath = request.getServletPath().replaceFirst("\\/","");
        
        /** reporterUsername **/
        String reporterUsername = request.getParameter("reporterUsername");
        if(reporterUsername!=null && !reporterUsername.isEmpty()) {
        	model.addAttribute("reporterUsername", reporterUsername);
        } else if(request.getSession().getAttribute("reporterUsername")!=null){
        	model.addAttribute("reporterUsername",request.getSession().getAttribute("reporterUsername"));
            request.getSession().removeAttribute("reporterUsername");
        } else {
        	model.addAttribute("reporterUsername", "");
        }        
        
        /** is volatile notification **/
        if(request.getSession().getAttribute("isVolatile")!=null){
        	model.addAttribute("isVolatile",request.getSession().getAttribute("isVolatile"));
            request.getSession().removeAttribute("isVolatile");
        }else{        	
            if(reporterUsername!=null && !reporterUsername.isEmpty()) {
            	model.addAttribute("isVolatile", false);
            } else {
            	model.addAttribute("isVolatile", true);
            }
        }
        
        /** notification title **/
        if(request.getSession().getAttribute("notificationTitle")!=null){
        	model.addAttribute("notificationTitle",request.getSession().getAttribute("notificationTitle"));
            request.getSession().removeAttribute("notificationTitle");
        }
        
        /** notification message **/
        if(request.getSession().getAttribute("notificationMessage")!=null){
        	model.addAttribute("notificationMessage",request.getSession().getAttribute("notificationMessage"));
            request.getSession().removeAttribute("notificationMessage");
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
            return servletPath.replace("sendReminderNotification","error");
        }else{
            return servletPath;
        }
    }
	
	@RequestMapping(value = "/sendReminderNotification", method = RequestMethod.POST)
    public String sendReminderNotification(final ModelMap model, 
    		final HttpServletRequest request,
    		final RedirectAttributes redirectAttributes,
            final Locale locale) {
		final String servletPath = request.getServletPath().replaceFirst("\\/","");
		String returnUrl = "";
		String reporterUsername = request.getParameter("reporterUsername");
		String notificationTitle = request.getParameter("notificationTitle");
		String notificationMessage = request.getParameter("notificationMessage");
		String isVolatile = request.getParameter("isVolatile");
		if(reporterUsername!=null && !reporterUsername.isEmpty()) {
			if(notificationTitle!=null && !notificationTitle.isEmpty()
					&& isVolatile!=null && !isVolatile.isEmpty()) {
				try {
//					StringBuffer senderName = new StringBuffer("");
//					if(this.getCurrentUser().getTitle()!=null && !this.getCurrentUser().getTitle().isEmpty()) {
//						senderName.append(this.getCurrentUser().getTitle());
//						senderName.append(" ");
//					}
//					if(this.getCurrentUser().getFirstName()!=null && !this.getCurrentUser().getFirstName().isEmpty()) {
//						senderName.append(this.getCurrentUser().getFirstName());
//						senderName.append(" ");
//					}
//					if(this.getCurrentUser().getLastName()!=null && !this.getCurrentUser().getLastName().isEmpty()) {
//						senderName.append(this.getCurrentUser().getLastName());
//					}
					NotificationController.sendNotificationFromUserPage(this.getCurrentUser().getActualUsername(), notificationTitle, notificationMessage, Boolean.parseBoolean(isVolatile), reporterUsername, locale.toString());
										 
					request.getSession().setAttribute("reporterUsername", reporterUsername);
					request.getSession().setAttribute("isVolatile", isVolatile);
					request.getSession().setAttribute("notificationTitle", notificationTitle);	
					request.getSession().setAttribute("notificationMessage", notificationMessage);
					
			        //this is done so as to remove the bug due to which update message appears even though there
			        //is a fresh request
			        request.getSession().setAttribute("type","success");
			        redirectAttributes.addFlashAttribute("msg", "update_success");
			        returnUrl = "redirect:/" + servletPath;
				} catch (Exception e) {
					e.printStackTrace();
					//error
					model.addAttribute("errorcode", "EXCEPTION_OCCURRED");
					returnUrl = servletPath.replace("sendReminderNotification","error");
				}
			} else {
				//error
				model.addAttribute("errorcode", "TITLE_EMPTY");
		        returnUrl = servletPath.replace("sendReminderNotification","error");
			}	        	        
		} else {
			//error
			model.addAttribute("errorcode", "REPORTER_NAME_EMPTY");
			returnUrl = servletPath.replace("sendReminderNotification","error");
		}
		return returnUrl;
    }
	
}