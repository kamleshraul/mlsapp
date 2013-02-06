package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.ChartVO;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Chart;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/chart")
public class ChartController extends BaseController{

	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String getChartPage(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String strGroup = request.getParameter("group");
		/**** Added By Sandeep Singh ****/
		String strUserGroup = request.getParameter("usergroup");
		String strUserGroupType = request.getParameter("usergroupType");
		if(strGroup != null && strUserGroup != null && strUserGroupType != null) {
			if((!strGroup.isEmpty()) && (!strUserGroup.isEmpty()) && (!strUserGroupType.isEmpty())) {
				Group group = Group.findById(Group.class, Long.parseLong(strGroup));
				List<MasterVO> masterVOs = new ArrayList<MasterVO>();
				List<QuestionDates> questionDates = group.getQuestionDates();
				for(QuestionDates i:questionDates) {
					MasterVO masterVO = new MasterVO(i.getId(), 
							FormaterUtil.getDateFormatter(
									locale.toString()).format(i.getAnsweringDate()));
					masterVOs.add(masterVO);
				}
				model.addAttribute("answeringDates", masterVOs);
				model.addAttribute("usergroup", strUserGroup);
				model.addAttribute("usergroupType", strUserGroupType);
			}
		}
		return "chart/chartinit";
	}

	/**
	 * Return "CREATED" if Chart is created
	 * OR
	 * Return "ALREADY_EXISTS" if Chart already exists
	 * OR
	 * Return "PREVIOUS_CHART_IS_NOT_PROCESSED" if previous Chart is not processed
	 */
	@Transactional
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public @ResponseBody String createChart(final HttpServletRequest request,
			final Locale locale) {
		String retVal = "ERROR";
		try {
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType =
				HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType =
				SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			
			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			
			/** Create Session */
			Session session = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);
			
			/** Create Group*/
			String strGroup = request.getParameter("group");
			Group group = Group.findById(Group.class, Long.parseLong(strGroup));
			
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			QuestionDates questionDates = 
				QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
			Date answeringDate = questionDates.getAnsweringDate();
			
			/** Create Chart */
			Chart foundChart = Chart.find(session, group, answeringDate, locale.toString());
			if(foundChart == null) {
				Chart chart = new Chart(session, group, answeringDate, locale.toString());
				Chart createdChart = chart.create();
				if(createdChart == null) {
					retVal = "PREVIOUS_CHART_IS_NOT_PROCESSED";
				}
				else {
					retVal = "CREATED";
				}
			}
			else {
				retVal = "ALREADY_EXISTS";
			}
			
		}
		catch(Exception e) {
			logger.error("error", e);
			retVal = "ERROR";
		}
		return retVal;
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewChart(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String retVal = "chart/error";
		try {
			/** Create HouseType */
			String strHouseType = request.getParameter("houseType");
			HouseType houseType =
				HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale.toString());
			
			/** Create SessionType */
			String strSessionTypeId = request.getParameter("sessionType");
			SessionType sessionType =
				SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
			
			/** Create year */
			String strYear = request.getParameter("sessionYear");
			Integer year = Integer.valueOf(strYear);
			
			/** Create Session */
			Session session = 
				Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

			/** Create Group*/
			String strGroup = request.getParameter("group");
			Group group = Group.findById(Group.class, Long.parseLong(strGroup));
			
			/** Create answeringDate */
			String strAnsweringDate = request.getParameter("answeringDate");
			QuestionDates questionDates = 
				QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
			Date answeringDate = questionDates.getAnsweringDate();
			
			/** Add localized answeringDate to model */
			CustomParameter parameter =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String localizedAnsweringDate = FormaterUtil.formatDateToString(answeringDate, 
					parameter.getValue(), locale.toString());
			model.addAttribute("answeringDate", localizedAnsweringDate);
			
			/** View Chart */
			List<ChartVO> chartVOs = 
				Chart.getChartVOs(session, group, answeringDate, locale.toString());
			model.addAttribute("chartVOs", chartVOs);
			
			/** Set max Questions on Chart against any member*/
			Integer maxQuestionsOnChart = 
				Chart.maxChartedQuestions(session, group, answeringDate, locale.toString());
			model.addAttribute("maxQns", maxQuestionsOnChart);
			
			retVal = "chart/chart";
		}
		catch(Exception e) {
			logger.error("error", e);
			model.addAttribute("type", "INSUFFICIENT_PARAMETERS_FOR_VIEWING_CHART");
			retVal = "chart/error";
		}
		return retVal;
	}
	
}
