package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.MotionDraft;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/poster_activities")
public class PosterActivitiesController extends BaseController {
	
	@RequestMapping(value = "/update_decision/init", method = RequestMethod.GET)
    public String posterActivitiesInitForUpdateDecision(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale)
	{
		/**** Request Params ****/
		String retVal = "motion/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
//		String strStatus = request.getParameter("status");
//		String strRole = request.getParameter("role");
//		String strUsergroup = request.getParameter("usergroup");
//		String strUsergroupType = request.getParameter("usergroupType");

		/**** Locale ****/
		String strLocale = locale.toString();

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())) 
		{
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale); //can be used later if required in custom parameter name below
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));			
			Session session = null;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, Integer.parseInt(strSessionYear));
				model.addAttribute("session", session.getId());
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				model.addAttribute("errorCode", "request_parameters_invalid");
				return retVal;
			} catch (ELSException e) {
				e.printStackTrace();
				model.addAttribute("errorCode", "request_parameters_invalid");
				return retVal;
			}
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			
			CustomParameter csptStatusesForUpdatingDecisions = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_STATUSES_FOR_DECISION_UPDATE", "");

			List<Status> internalStatuses = new ArrayList<Status>();
			try {
				if(csptStatusesForUpdatingDecisions!=null && csptStatusesForUpdatingDecisions.getValue()!=null) {
					internalStatuses = Status.findStatusContainedIn(csptStatusesForUpdatingDecisions.getValue(),locale.toString());
					model.addAttribute("internalStatuses", internalStatuses);
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", houseType.getType());
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("deviceType", deviceType.getId());
			model.addAttribute("device", deviceType.getDeviceName());
//			model.addAttribute("status", strStatus);
//			model.addAttribute("role", strRole);
//			model.addAttribute("usergroup", strUsergroup);
//			model.addAttribute("usergroupType", strUsergroupType);

			retVal = "poster_activities/decisionupdate_init";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	@RequestMapping(value="/update_decision/view_devices", method=RequestMethod.GET)
	public String posterActivitiesViewDevicesForUpdateDecision(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.viewDevicesForUpdateDecision(model, request, locale.toString());
		return "poster_activities/decisionupdate_devicesview";
	}
	
	@Transactional
	@RequestMapping(value="/update_decision/process", method=RequestMethod.POST)
	public String statusUpdateAssistant(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		boolean updated = false;
		String page = "motion/error";
		StringBuffer success = new StringBuffer();
		
		try{
			String strDeviceType = request.getParameter("deviceType");
			String[] selectedItems = request.getParameterValues("items[]");
			String strDecisionStatus = request.getParameter("decisionStatus");
			String remarks = request.getParameter("remarks");
			
			if(strDeviceType!=null && !strDeviceType.isEmpty() 
					&& selectedItems != null && selectedItems.length > 0
					&& strDecisionStatus != null && !strDecisionStatus.isEmpty()) {
				DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
				String finalAuthorityUsername = "";
				for(String i : selectedItems) {
					try {
						Long id = Long.parseLong(i);
						Motion motion = Motion.findById(Motion.class, id);
						if(motion.getParent()==null) {
							Status status = Status.findById(Status.class, new Long(strDecisionStatus));
							UserGroupType userGroupType = UserGroupType.findByType(ApplicationConstants.ASSISTANT, motion.getLocale());
								
							Device.startDeviceWorkflow(motion.getType().getDeviceName(), motion.getId(), status, userGroupType, 7, "", false, motion.getLocale());
							
							//motion.simpleMerge();
							Motion updatedMotion = Motion.findById(Motion.class, motion.getId());
							if(remarks!=null) {
								updatedMotion.setRemarks(remarks);						
							}
							UserGroupType finalAuthorityUGT = null;
							if(updatedMotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
								finalAuthorityUGT = UserGroupType.findByType(ApplicationConstants.SPEAKER, updatedMotion.getLocale());
							}
							else if(updatedMotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
								finalAuthorityUGT = UserGroupType.findByType(ApplicationConstants.CHAIRMAN, updatedMotion.getLocale());
							}
							if(finalAuthorityUGT!=null) {
								updatedMotion.setEditedAs(finalAuthorityUGT.getDisplayName());
								updatedMotion.setEditedOn(new Date());
								//later add new UserGroup.findActive for current devicetype as allowed devicetype parameter
								UserGroup finalAuthorityUserGroup = UserGroup.findActive(finalAuthorityUGT.getType(), new Date(), updatedMotion.getLocale());
								if(finalAuthorityUserGroup!=null) {
									finalAuthorityUsername = finalAuthorityUserGroup.getCredential().getUsername();
									updatedMotion.setEditedBy(finalAuthorityUsername);
								}							
								//remove below code later once above code is proper
								if(updatedMotion.getHouseType().getType().equals(ApplicationConstants.LOWER_HOUSE)) {
									updatedMotion.setEditedBy("mois_speaker");
									finalAuthorityUsername = updatedMotion.getEditedBy();
								}
								else if(updatedMotion.getHouseType().getType().equals(ApplicationConstants.UPPER_HOUSE)) {
									updatedMotion.setEditedBy("mois_chairman");
									finalAuthorityUsername = updatedMotion.getEditedBy();
								}
							}
							
							updatedMotion.addMotionDraft();
							updatedMotion.simpleMerge();
							updated = true;
							success.append(FormaterUtil.formatNumberNoGrouping(updatedMotion.getNumber(), updatedMotion.getLocale())+",");
						}
					} catch(ELSException e) {
						e.printStackTrace();
						logger.error(e.getParameter());
						logger.error("Problem in bulk update of workflow details task with " + deviceType.getDeviceName() + " ID = "+i);
						continue;
					} catch(Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
						logger.error("Problem in bulk update of workflow details task with " + deviceType.getDeviceName() + " ID = "+i);
						continue;
					}										
				}
				//Add Notification to finalAuthorityUsername
			}
		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			this.viewDevicesForUpdateDecision(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "poster_activities/decisionupdate_viewresult";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	private void viewDevicesForUpdateDecision(final ModelMap model,
										final HttpServletRequest request, 
										final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSession = request.getParameter("session");
		String strDeviceType = request.getParameter("deviceType");
		String deviceNumbers = request.getParameter("deviceNumbers");
		
		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSession != null && !(strSession.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& deviceNumbers !=null && !(deviceNumbers.isEmpty()))
		{
			List<Motion> motions = new ArrayList<Motion>(); //later to be replaced with DeviceVOs for generic approach
		
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale);
			Session session = Session.findById(Session.class, Long.parseLong(strSession));
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));				
			
			motions = Motion.findAllByNumbersInSession(session, deviceType, deviceNumbers, locale);
	
			model.addAttribute("motions", motions);
			if(motions != null && ! motions.isEmpty()) {
				model.addAttribute("motionId", motions.get(0).getId());
			}
		}
	}

}