package org.mkcl.els.controller.ris;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.SearchVO;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Committee;
import org.mkcl.els.domain.CommitteeMeeting;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Proceeding;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.StandaloneMotion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/proceedingsearch")
public class ProceedingSearchController extends BaseController{
	
	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String searchInit(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String retVal = "search/error";
		/**** Advanced Search Filters****/
		try{
			List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "name", "ASC", locale.toString());
			model.addAttribute("houseTypes", houseTypes);
			
			List<SessionType> sessionTypes = SessionType.findAll(SessionType.class, "type", "ASC", locale.toString());
			model.addAttribute("sessionTypes", sessionTypes);

			Integer latestYear = new GregorianCalendar().get(Calendar.YEAR);

			CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
			List<Reference> years = new ArrayList<Reference>();
			if(houseFormationYear != null){
				Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
				for(int i = latestYear; i >= formationYear; i--){
					Reference reference = new Reference(String.valueOf(i),FormaterUtil.getNumberFormatterNoGrouping(locale.toString()).format(i));
					years.add(reference);
				}
			}else{
				model.addAttribute("flag", "houseformationyearnotset");
				return "search/error";
			}
			model.addAttribute("years", years);

			
			List<Committee> committees = Committee.findActiveCommittees(new Date(), locale.toString());
			List<MasterVO> committeeDetails = new ArrayList<MasterVO>();
			for(Committee c : committees){
				MasterVO committeeDetail = new MasterVO();
				committeeDetail.setId(c.getCommitteeName().getId());
				committeeDetail.setName(c.getCommitteeName().getDisplayName());
				committeeDetails.add(committeeDetail);
			}
			model.addAttribute("committeeDetails", committeeDetails);
			retVal = "proceeding/search/init";
			
		}catch(Exception e){
			logger.error("error", e);
		}
		return retVal;
		
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchfacility",method=RequestMethod.POST)
	public @ResponseBody List<SearchVO> searchProceeding(final HttpServletRequest request,
			final Locale locale){
		List<SearchVO> searchVOs = new ArrayList<SearchVO>();
		try{
			String param = request.getParameter("param").trim();
			String start = request.getParameter("start");
			String noOfRecords = request.getParameter("record");
			
			Map<String,String[]> requestMap = request.getParameterMap();
			if(start != null && noOfRecords != null){
				if((!start.isEmpty()) && (!noOfRecords.isEmpty())){
					searchVOs = Proceeding.
							fullTextSearchForSearching(param, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString(), requestMap);
				}
			}
		}catch(Exception e){
			logger.error("error", e);
		}
		return searchVOs;
	}

}
