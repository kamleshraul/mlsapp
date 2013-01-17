package org.mkcl.els.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.QuestionDatesVO;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.HouseType;
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
}
