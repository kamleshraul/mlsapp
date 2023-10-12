package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.PosterLog;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/posterUtility")
public class PosterUtilityController extends GenericController<PosterLog> {

	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		
		//returned False which doesnt allow to delete record
		return false;
	}
	
	
	  @Override
	    protected void populateNew(final ModelMap model, final PosterLog domain,
	            final String locale, final HttpServletRequest request) {
	      
	     
	      populate(model, domain, locale, request);
	       
	    }

	
		private void populate(ModelMap model, PosterLog domain, String locale, HttpServletRequest request
				) {
		
			CustomParameter cp1 = CustomParameter.
					findByName(CustomParameter.class, "WORKFLOW_STATUS_TYPE", "");

			CustomParameter cp2 = CustomParameter.
					findByName(CustomParameter.class, "POSTER_USER_GROUP_TYPES", "");
			
			CustomParameter cp3 = CustomParameter.
					findByName(CustomParameter.class, "DEVICE_TYPES_FOR_POSTER_ACTIVITIES", "");
			
			if (cp1 != null && cp2 != null && cp3 != null && cp1.getValue() != null && cp2.getValue() != null && cp3.getValue() != null ) {
				
				String[] workflowtypes = cp1.getValue().split(",");
				Map<String,String> KVworkflowtypes = new  HashMap<String,String>();
				for(String s : workflowtypes) {
					String[] splittingIntoKeyValue = s.split(":");
					KVworkflowtypes.put(splittingIntoKeyValue[0], splittingIntoKeyValue[1]);
				}				
				model.addAttribute("WORKFLOW_STATUS_TYPE", KVworkflowtypes);

				List<UserGroupType> ugt = new ArrayList<UserGroupType>();
				for (String s : cp2.getValue().split(",")) {
						UserGroupType ut = UserGroupType.findByType(s, locale.toString());
						ugt.add(ut);
				}

				model.addAttribute("POSTER_USER_GROUP_TYPES", ugt);
				
				List<DeviceType> dt = new ArrayList<DeviceType>();
				
				for(String s :cp3.getValue().split(",")) {
					DeviceType deviceType = DeviceType.findByType(s, locale.toString());
					dt.add(deviceType);
				}

				model.addAttribute("dt", dt);
			}
			
			
		}
		
		
		@RequestMapping(value = "/getStatusForParticularDeviceType/{deviceType}", method = RequestMethod.GET)
		public @ResponseBody List<Status> getStatusForParticularDeviceType(
				@PathVariable("deviceType") final String deviceType, HttpServletRequest request, final Locale locale) {

			List<Status> statusesForPosterActivitiesOfDeviceType = new ArrayList<Status>();

			if (deviceType != null) {
				DeviceType dt = DeviceType.findByType(deviceType, locale.toString());
				if (dt != null) {
					CustomParameter csptStatusesForDefaultDeviceType = CustomParameter.findByName(CustomParameter.class,
							"STATUS_TYPES_FOR_POSTER_ACTIVITIES_OF_" + dt.getType().toUpperCase(), "");
					if (csptStatusesForDefaultDeviceType != null
							&& csptStatusesForDefaultDeviceType.getValue() != null) {
						try {
							statusesForPosterActivitiesOfDeviceType = Status.findStatusWithSupportOrderContainedIn(
									csptStatusesForDefaultDeviceType.getValue(), locale.toString());
						} catch (ELSException e) {

							e.printStackTrace();
						}
						return statusesForPosterActivitiesOfDeviceType;
					}
				}
			}

			return null;
		}
		
		
		
		@RequestMapping(value="/posterActivityForSupport/id/{id}/deviceType/{deviceType}/status/{status}/houseType/{houseType}",method=RequestMethod.GET)
		public String posterActivityForSupport(
				@PathVariable("id") final Integer deviceId,
					@PathVariable("deviceType") final String deviceType,
					@PathVariable("status") final String status,
					@PathVariable("houseType") final String houseType,
						HttpServletRequest request,
							final Locale locale,	
								final ModelMap model
				) {
			
			CustomParameter cp1 = CustomParameter.
					findByName(CustomParameter.class, "WORKFLOW_STATUS_TYPE", "");

			CustomParameter cp2 = CustomParameter.
					findByName(CustomParameter.class, "POSTER_USER_GROUP_TYPES", "");
			
			if (cp1 != null && cp2 != null  && cp1.getValue() != null && cp2.getValue() != null  ) {
				String[] workflowtypes = cp1.getValue().split(",");
				Map<String,String> KVworkflowtypes = new  HashMap<String,String>();
				for(String s : workflowtypes) {
					String[] splittingIntoKeyValue = s.split(":");
					KVworkflowtypes.put(splittingIntoKeyValue[0], splittingIntoKeyValue[1]);
				}				
				model.addAttribute("WORKFLOW_STATUS_TYPE", KVworkflowtypes);

				List<UserGroupType> ugt = new ArrayList<UserGroupType>();
				for (String s : cp2.getValue().split(",")) {
						UserGroupType ut = UserGroupType.findByType(s, locale.toString());
						ugt.add(ut);
				}

				model.addAttribute("POSTER_USER_GROUP_TYPES", ugt);
			}
			
			model.addAttribute("selectedStatusType", status);
			
		
			DeviceType dt = DeviceType.findByType(deviceType, locale.toString());
			model.addAttribute("dt", dt);
			
			
			HouseType ht = HouseType.findByType(houseType, locale.toString());
			model.addAttribute("houseTypeId", ht.getId());
			
			model.addAttribute("id", deviceId);
			
			return "support_activities/posterSupportActivity";
		}
	
}
