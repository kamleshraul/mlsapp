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
		String strGroup=request.getParameter("group");
		/**** Added By Sandeep Singh ****/
		String strUserGroup=request.getParameter("usergroup");
		String strUserGroupType=request.getParameter("usergroupType");
		if(strGroup!=null&&strUserGroup!=null&&strUserGroupType!=null){
			if((!strGroup.isEmpty())&&(!strUserGroup.isEmpty())&&(!strUserGroupType.isEmpty())){
				Group group=Group.findById(Group.class,Long.parseLong(strGroup));
				List<MasterVO> masterVOs=new ArrayList<MasterVO>();
				List<QuestionDates> questionDates=group.getQuestionDates();
				for(QuestionDates i:questionDates){
					MasterVO masterVO=new MasterVO(i.getId(),FormaterUtil.getDateFormatter(locale.toString()).format(i.getAnsweringDate()));
					masterVOs.add(masterVO);
				}
				model.addAttribute("answeringDates",masterVOs);
				model.addAttribute("usergroup",strUserGroup);
				model.addAttribute("usergroupType",strUserGroupType);
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
		String retVal = "ALREADY_EXISTS";

		String strLocale = locale.toString();
		String strHouseType = request.getParameter("houseType");
		String strYear = request.getParameter("sessionYear");
		String strSessionTypeId = request.getParameter("sessionType");
		String strTempDate=request.getParameter("answeringDate");
		QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));

		HouseType houseType =
			HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
		SessionType sessionType =
			SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
		Integer year = Integer.valueOf(strYear);

		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

		String ugparam=request.getParameter("group");
		Group group = Group.findById(Group.class, Long.parseLong(ugparam));

		Date answeringDate = questionDates.getAnsweringDate();
		if(answeringDate != null) {
			Chart foundChart = Chart.find(session, group, answeringDate, strLocale);
			if(foundChart == null) {
				Chart chart = new Chart(session, group, answeringDate, strLocale);
				Chart createdChart = chart.create();
				if(createdChart == null) {
					retVal = "PREVIOUS_CHART_IS_NOT_PROCESSED";
				}
				else {
					retVal = "CREATED";
				}
			}
		}

		return retVal;
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewChart(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String strLocale = locale.toString();
		String strHouseType = request.getParameter("houseType");
		String strYear = request.getParameter("sessionYear");
		String strSessionTypeId = request.getParameter("sessionType");
		String strTempDate=request.getParameter("answeringDate");
		QuestionDates questionDates=QuestionDates.findById(QuestionDates.class,Long.parseLong(strTempDate));

		HouseType houseType =
			HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
		SessionType sessionType =
			SessionType.findById(SessionType.class, Long.valueOf(strSessionTypeId));
		Integer year = Integer.valueOf(strYear);

		Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, year);

		String ugparam=request.getParameter("group");
		Group group = Group.findById(Group.class, Long.parseLong(ugparam));

		Date answeringDate = questionDates.getAnsweringDate();

		if(answeringDate != null) {
			List<ChartVO> chartVOs = Chart.getChartVOs(session, group, answeringDate, strLocale);
			model.addAttribute("chartVOs", chartVOs);

			CustomParameter parameter =
				CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			String localizedAnsweringDate =
				FormaterUtil.formatDateToString(answeringDate, parameter.getValue(), strLocale);
			model.addAttribute("answeringDate", localizedAnsweringDate);
		}
		else {
			model.addAttribute("errorcode", "answeringDateNotSelected");
		}

		return "chart/chart";
	}
}
