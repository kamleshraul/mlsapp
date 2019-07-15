package org.mkcl.els.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.DepartmentDashboardVo;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.domain.CommitteeName;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("statisticaldashboard")
public class StatisticalDashboardController extends BaseController{
	
	
	
	@RequestMapping(value = "/device", method = RequestMethod.GET)
    public String guestHouseBooking(final ModelMap model, 
    		final HttpServletRequest request,
            final Locale locale) throws ELSException, ParseException {
		   final String servletPath = request.getServletPath().replaceFirst("\\/","");
			String deviceType = request.getParameter("deviceType");
			String admitStatus = request.getParameter("admitStatus");
			String rejectStatus = request.getParameter("rejectStatus");
			
			model.addAttribute("deviceType",deviceType);
			model.addAttribute("admitStatus",admitStatus);
			model.addAttribute("rejectStatus",rejectStatus);
			
		
		   return servletPath;
	
	}
	
	@RequestMapping(value="/loadhousetypes",method=RequestMethod.GET)
	public @ResponseBody List<HouseType> loadHouseType(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		List<HouseType> housetypes = HouseType.findAll(HouseType.class, "name", "asc", locale.toString());
		HouseType bothHouseType = HouseType.findByType(ApplicationConstants.BOTH_HOUSE, locale.toString());
		housetypes.remove(bothHouseType);
		return housetypes;
	
	}
	
	@RequestMapping(value="/loadDeviceDetails",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> loadDeviceDetails(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){

		List<MasterVO> deviceDetails = new ArrayList<MasterVO>();
		String session = request.getParameter("session");
		String deviceType = request.getParameter("deviceType");
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale", new String[]{locale.toString()});
		parameters.put("session", new String[]{session.toString()});
		parameters.put("deviceType", new String[]{deviceType.toString()});
		DeviceType objDeviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
		String deviceTypeName=objDeviceType.getDevice().toUpperCase();
		deviceTypeName=deviceTypeName.substring(0, (deviceTypeName.indexOf("_")>= 0) ? deviceTypeName.indexOf("_") : deviceTypeName.length());
		/*int len=(deviceTypeName.indexOf("_")>= 0) ? deviceTypeName.indexOf("_") : deviceTypeName.length();
		
		if (deviceTypeName.indexOf("_")>= 0) {
			deviceTypeName.indexOf("_");
		}
		else
		{
			deviceTypeName.length();
		}*/
		List result = Query.findReport("STATISTICAL_DEVICE_COUNT_SESSIONWISE_"+deviceTypeName, parameters);
		for(int i=0;i<result.size();i++){
	       	 Object[] row = (Object[])result.get(i);
	       	MasterVO masterVO = new MasterVO();
	       	masterVO.setName(row[0].toString());
	       	masterVO.setFormattedNumber(row[1].toString());
	       	masterVO.setFormattedOrder(row[2].toString());
	       	masterVO.setValue(row[3].toString());
	       	deviceDetails.add(masterVO);
		}
		return deviceDetails;
	
	}
	
	@RequestMapping(value="/loadPartyDetails",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> loadPartyDetails(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		List<MasterVO> partyDetails = new ArrayList<MasterVO>();
		String session = request.getParameter("session");
		String deviceType = request.getParameter("deviceType");
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale", new String[]{locale.toString()});
		parameters.put("session", new String[]{session.toString()});
		parameters.put("deviceType", new String[]{deviceType.toString()});
		DeviceType objDeviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
		String deviceTypeName=objDeviceType.getDevice().toUpperCase();
		deviceTypeName=deviceTypeName.substring(0, (deviceTypeName.indexOf("_")>= 0) ? deviceTypeName.indexOf("_") : deviceTypeName.length());
		List result = Query.findReport("STATISTICAL_PARTY_LIST_SESSIONWISE_"+deviceTypeName, parameters);
		for(int i=0;i<result.size();i++){
	       	 Object[] row = (Object[])result.get(i);
	       	MasterVO masterVO = new MasterVO();
	       	masterVO.setName(row[0].toString());
	       	masterVO.setId(Long.parseLong(row[1].toString()));
	       	partyDetails.add(masterVO);
		}
		return partyDetails;
	
	}
	
	@RequestMapping(value="/loadministriesdetails",method=RequestMethod.GET)
	public @ResponseBody List<MasterVO> loadministriesdetails(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model){
		List<MasterVO> ministryDetails = new ArrayList<MasterVO>();
		String session = request.getParameter("session");
		String deviceType = request.getParameter("deviceType");
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("locale", new String[]{locale.toString()});
		parameters.put("session", new String[]{session.toString()});
		parameters.put("deviceType", new String[]{deviceType.toString()});
		DeviceType objDeviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
		String deviceTypeName=objDeviceType.getDevice().toUpperCase();
		deviceTypeName=deviceTypeName.substring(0, (deviceTypeName.indexOf("_")>= 0) ? deviceTypeName.indexOf("_") : deviceTypeName.length());
		List result = Query.findReport("STATISTICAL_MINISTRIES_LIST_SESSIONWISE_"+deviceTypeName, parameters);
		for(int i=0;i<result.size();i++){
	       	 Object[] row = (Object[])result.get(i);
	       	MasterVO masterVO = new MasterVO();
	       	masterVO.setName(row[0].toString());
	       	masterVO.setId(Long.parseLong(row[3].toString()));
	       	ministryDetails.add(masterVO);
		}
		return ministryDetails;
	
	}
	
	@RequestMapping(value="/getSortedData")
	public @ResponseBody List<MasterVO> findActiveMembersByDistricts(final HttpServletRequest request,
			final Locale locale,
			final ModelMap model)throws ELSException{
		
		CustomParameter customParameter=CustomParameter.findByName(CustomParameter.class,"DEPLOYMENT_SERVER", "");
		List<MasterVO> members=new ArrayList<MasterVO>();
		String house = request.getParameter("house");
		String session = request.getParameter("session");
		String deviceType = request.getParameter("deviceType");
		String admitStatus = request.getParameter("admitStatus");
		String rejectStatus = request.getParameter("rejectStatus");
		
		if(customParameter!=null){
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			parameters.put("locale", new String[]{locale.toString()});
			parameters.put("house", new String[]{house});
			parameters.put("session", new String[]{session});
			parameters.put("deviceType", new String[]{deviceType});
			parameters.put("admitStatus", new String[]{admitStatus});
			parameters.put("rejectStatus", new String[]{rejectStatus});
			DeviceType objDeviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
			String deviceTypeName=objDeviceType.getDevice().toUpperCase();
			deviceTypeName=deviceTypeName.substring(0, (deviceTypeName.indexOf("_")>= 0) ? deviceTypeName.indexOf("_") : deviceTypeName.length());
			List result = Query.findReport("STATISTICAL_MEMBER_LIST_SESSIONWISE_"+deviceTypeName, parameters);
			for(int i=0;i<result.size();i++){
		       	 Object[] row = (Object[])result.get(i);
		       	MasterVO membersSessionwise = new MasterVO();
		    	membersSessionwise.setType(row[0].toString());
		       	membersSessionwise.setName(row[1].toString());
		       	membersSessionwise.setFormattedNumber(row[2].toString());
		       	membersSessionwise.setFormattedOrder(row[3].toString());
		       	membersSessionwise.setValue(row[4].toString());
				members.add(membersSessionwise);
			}
		}
		
		return members;
	}

}
