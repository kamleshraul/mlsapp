package org.mkcl.els.controller;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.common.vo.RotationOrderVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/rotationorder")
public class RotationOrderController {

	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getBallotPage(final HttpServletRequest request,final ModelMap model,
			final Locale locale){
		return "rotationorder/rotationorderinit";
	}
	
	@RequestMapping(value="/aadwachart",method=RequestMethod.GET)
    public String printReport(final HttpServletRequest request,final Locale locale,final ModelMap model){      	
        String strHouseType=request.getParameter("houseType");
        String strSessionType=request.getParameter("sessionType");
        String strSessionYear=request.getParameter("sessionYear");
        if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null){
            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
            Integer sessionYear=Integer.parseInt(strSessionYear);
            List<QuestionDatesVO> questionDates=Group.findAllGroupDatesFormatted(houseType, sessionType, sessionYear,locale.toString());
            model.addAttribute("dates",questionDates);
        }
        return "rotationorder/aadwachart";
    }
	
	@RequestMapping(value="/viewrotationorder" ,method=RequestMethod.GET)
	public String printRotationOrder(final HttpServletRequest request, final Locale locale, final ModelMap model){
		 String strHouseType=request.getParameter("houseType");
	     String strSessionType=request.getParameter("sessionType");
	     String strSessionYear=request.getParameter("sessionYear");
	     SimpleDateFormat dbFormat = null;
	     if(strHouseType!=null&&strSessionType!=null&&strSessionYear!=null){
	            HouseType houseType=HouseType.findByFieldName(HouseType.class,"type",strHouseType, locale.toString());
	            SessionType sessionType=SessionType.findById(SessionType.class,Long.parseLong(strSessionType));
	            Integer sessionYear=Integer.parseInt(strSessionYear);
	            Session session= Session.findSessionByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
	           
	            CustomParameter dbDateFormat=CustomParameter.findByName(CustomParameter.class,"ROTATION_ORDER_DATE_FORMAT", "");
	            if(dbDateFormat!=null){
	            	dbFormat=FormaterUtil.getDateFormatter(dbDateFormat.getValue(), locale.toString());
	            }
	            NumberFormat numberFormat=FormaterUtil.getNumberFormatterNoGrouping(locale.toString());
	            List<Group> groups= Group.findByHouseTypeSessionTypeYear(houseType, sessionType, sessionYear);
	            List<RotationOrderVO> rotationOrderVOs= new ArrayList<RotationOrderVO>();
	            
	            for(Group g:groups){
	            	RotationOrderVO rotationOrderVO= new RotationOrderVO();
	            	List<Ministry> ministries=Group.findMinistriesByPriority(g);
	            	List<QuestionDates> dates= g.getQuestionDates();
	            	List<String> ministriesStr= new ArrayList<String>();
	            	List<String> numberOfMinisteries= new ArrayList<String>();
	            	int i=1;
	            	for(Ministry m:ministries){
	            		ministriesStr.add(m.getName());
	            		numberOfMinisteries.add(numberFormat.format(i++));
	            	}
	            	List<String> answeringDates= new ArrayList<String>();
	            	List<String> finalSubmissionDates=new ArrayList<String>();
	            	for(QuestionDates d:dates){
	            		//Added the following code to solve the marathi month and day issue
	            		String[] strAnsweringDates=dbFormat.format(d.getAnsweringDate()).split(",");
	            		String answeringDay=FormaterUtil.getDayInMarathi(strAnsweringDates[0],locale.toString());
	            		String[] strAnsweringMonth=strAnsweringDates[1].split(" ");
	            		String answeringMonth=FormaterUtil.getMonthInMarathi(strAnsweringMonth[1], locale.toString());
	            		
	            		answeringDates.add(answeringDay+","+strAnsweringMonth[0]+" "+ answeringMonth +","+strAnsweringDates[2]);
	            		
	            		String[] strSubmissionDates=dbFormat.format(d.getFinalSubmissionDate()).split(",");
	            		String submissionDay=FormaterUtil.getDayInMarathi(strSubmissionDates[0],locale.toString());
	            		String[] strSubmissionMonth=strSubmissionDates[1].split(" ");
	            		String submissionMonth=FormaterUtil.getMonthInMarathi(strSubmissionMonth[1], locale.toString());
	            		
	            		finalSubmissionDates.add(submissionDay+","+strSubmissionMonth[0]+" "+ submissionMonth +","+strSubmissionDates[2]);
	            	}
	            	rotationOrderVO.setGroup(numberFormat.format(g.getNumber()));
	            	rotationOrderVO.setMinistries(ministriesStr);
	            	rotationOrderVO.setNumberOfMinisteries(numberOfMinisteries);
	            	rotationOrderVO.setAnsweringDates(answeringDates);
	            	rotationOrderVO.setFinalSubmissionDates(finalSubmissionDates);
	            	rotationOrderVOs.add(rotationOrderVO);
	            }
	            model.addAttribute("rotationOrderHeader", session.getParameter("questions_starred_rotationOrderHeader"));
	            model.addAttribute("rotationOrderCover", session.getParameter("questions_starred_rotationOrderCover"));
	            model.addAttribute("rotationOrderFooter", session.getParameter("questions_starred_rotationOrderFooter"));
	            model.addAttribute("dates", rotationOrderVOs);
	        }
		return "rotationorder/viewrotationorder";
	}	
}
