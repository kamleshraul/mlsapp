package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.domain.AdjournmentMotion;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.SpecialMentionNotice;
import org.mkcl.els.domain.StandaloneMotion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/devicesearch")
public class DeviceSearchController extends BaseController{
	
	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String searchInit(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "search/error";
		/**** Advanced Search Filters****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("deviceType");
		try{
			HouseType houseType = HouseType.findByType(strHouseType, locale.toString());
			Integer sessionYear = Integer.parseInt(strSessionYear);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			//Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			model.addAttribute("houseType", houseType.getType());
			model.addAttribute("sessionType", sessionType.getId());
			model.addAttribute("sessionYear", sessionYear);
			model.addAttribute("deviceType", deviceType.getId());
			model.addAttribute("deviceTypeType", deviceType.getType());
			
			int year = sessionYear;
			CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Reference> years = new ArrayList<Reference>();
			if(houseFormationYear != null){
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i = year; i >= formationYear; i--){
					Reference reference = new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
					years.add(reference);
				}
			}else{
				model.addAttribute("flag", "houseformationyearnotset");
				return "search/error";
			}
			model.addAttribute("years", years);
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale.toString());
			model.addAttribute("sessionTypes", sessionTypes);
			
			CustomParameter csptSearchByFacility = CustomParameter.findByName(CustomParameter.class, "SEARCHFACILITY_SEARCH_BY", "");
			if(csptSearchByFacility != null && csptSearchByFacility.getValue() != null && ! csptSearchByFacility.getValue().isEmpty()){
				List<MasterVO> searchByData = new ArrayList<MasterVO>();
				for(String sf : csptSearchByFacility.getValue().split(";")){
					String[] data = sf.split(":");
					MasterVO newVO = new MasterVO();
					newVO.setValue(data[0]);
					newVO.setName(data[1]);
					searchByData.add(newVO);
				}
				model.addAttribute("searchBy", searchByData);
			}		
			
			List<DeviceType> deviceTypes = DeviceType.findAll(DeviceType.class, "name", "asc", locale.toString());
			model.addAttribute("deviceTypes",deviceTypes);
			String device = deviceType.getDevice();
			if(device.equals("Question")){
				List<DeviceType> questionTypes = DeviceType.findDeviceTypesStartingWith("questions_", locale.toString());
				model.addAttribute("dTypes", questionTypes);
				List<Group> allgroups = Group.
						findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				for(Group i:allgroups){
					MasterVO masterVO = new MasterVO(i.getId(),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i.getNumber()));
					masterVOs.add(masterVO);
				}
				model.addAttribute("groups",masterVOs);
				
			} else if(device.equals("SpecialMentionNotice") || device.equals("proprietypoint")) {
				List<DeviceType> combinedDeviceTypes = new ArrayList<DeviceType>();
				DeviceType smisDeviceType = DeviceType.findByType(ApplicationConstants.SPECIAL_MENTION_NOTICE, locale.toString());
				combinedDeviceTypes.add(smisDeviceType);
				DeviceType proisDeviceType = DeviceType.findByType(ApplicationConstants.PROPRIETY_POINT, locale.toString());
				combinedDeviceTypes.add(proisDeviceType);
				model.addAttribute("dTypes", combinedDeviceTypes);
				
				List<Ministry> ministries= Ministry.
						findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale.toString()) ;
				model.addAttribute("ministries", ministries);
				
			} else{
				List<DeviceType> dTypes = DeviceType.
						findAllByFieldName(DeviceType.class, "device", deviceType.getDevice(), "name", "asc", locale.toString());
				model.addAttribute("dTypes", dTypes);
				List<Ministry> ministries= Ministry.
						findMinistriesAssignedToGroups(houseType, sessionYear, sessionType, locale.toString()) ;
				model.addAttribute("ministries", ministries);
			}
			retVal = "devicesearch/init";
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return retVal;
		
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchfacility",method=RequestMethod.POST)
	public @ResponseBody List<SearchVO> searchDevice(final HttpServletRequest request,
			final Locale locale){
		List<SearchVO> searchVOs = new ArrayList<SearchVO>();
		try{
			String param = request.getParameter("param").trim();
			String strDeviceType = request.getParameter("deviceType");
			String strSession = request.getParameter("session");
			String start = request.getParameter("start");
			String noOfRecords = request.getParameter("record");
			
			String strHouseType = request.getParameter("houseType");
 			String strSessionYear = request.getParameter("sessionYear");
			String strSessionType = request.getParameter("sessionType");
			
			DeviceType deviceType = null;
			Session session = null;
			HouseType houseType = null;
			Integer sessionYear = null;
			SessionType sessionType = null;
			
			if(strHouseType != null && !strHouseType.isEmpty() && !strHouseType.equals("-")){
				houseType = HouseType.findByType(strHouseType, locale.toString());
			}
			
			if(strSessionYear != null && !strSessionYear.isEmpty() && !strSessionYear.equals("-")){
				sessionYear = new Integer(strSessionYear);
			}
			
			if(strSessionType != null && !strSessionType.isEmpty() && !strSessionType.equals("-")){
				sessionType = SessionType.findById(SessionType.class, new Long(strSessionType));
			}
			
			if(houseType != null && sessionYear != null && sessionType != null){
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
			}
			
			if(strDeviceType != null && !strDeviceType.isEmpty()){
				deviceType = DeviceType.findById(DeviceType.class, new Long(strDeviceType));
			}
			
			if(session == null){
				if(strSession != null && !strSession.isEmpty()){
					session = Session.findById(Session.class, new Long(strSession));
				}
			}
			
			//if any device is to be added then check the device of that devicetype 
			// And add the code for search
			Map<String,String[]> requestMap = request.getParameterMap();
			if(start != null && noOfRecords != null){
				if((!start.isEmpty()) && (!noOfRecords.isEmpty())){
					if(deviceType.getDevice().equals("Motion")){
						searchVOs = Motion.
								fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
					}else if(deviceType.getDevice().equals("Resolution")){
						searchVOs = Resolution.
								fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
					}else if(deviceType.getDevice().equals("standalonemotion")){
						searchVOs = StandaloneMotion.
								fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
					}else if(deviceType.getDevice().equalsIgnoreCase("AdjournmentMotion")){
						searchVOs = AdjournmentMotion.
								fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
					}else if(deviceType.getDevice().equals("SpecialMentionNotice")){
						searchVOs = SpecialMentionNotice.
								fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
					}else if(deviceType.getDevice().equals("proprietypoint")){
						searchVOs = ProprietyPoint.
								fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
					}
					
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return searchVOs;
	}

}
