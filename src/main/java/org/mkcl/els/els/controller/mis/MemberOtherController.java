package org.mkcl.els.controller.mis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.PositionHeld;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("member/other")
public class MemberOtherController extends GenericController<Member>{
	
	@Override
	protected void preValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
		populate(domain, result, request);
    }
	
	@Override
    protected void populateEdit(final ModelMap model, final Member domain,
            final HttpServletRequest request) {
		if(!domain.getPositionsHeld().isEmpty()){
	        model.addAttribute("positionCount", domain.getPositionsHeld().size());
	        model.addAttribute("positions", domain.getPositionsHeld());
		}else{
	        model.addAttribute("positionCount", 0);
		}        
    }

	private void populate(final Member domain,
            final BindingResult result, final HttpServletRequest request){
		try {
			List<PositionHeld> positions = new ArrayList<PositionHeld>();
			 Integer positionCount = Integer.parseInt(request
			 .getParameter("positionCount"));
			 for (int i = 1; i <= positionCount; i++) {
			  PositionHeld positionHeld = new PositionHeld();
			 SimpleDateFormat format=new SimpleDateFormat(((CustomParameter)CustomParameter.findByName(CustomParameter.class,"SERVER_DATEFORMAT", "")).getValue());
			 
			 String fromDate=request.getParameter("positionFromDate" + i);
			 if(fromDate!=null){
			 if(!fromDate.isEmpty()){
			 positionHeld.setFromDate(format.parse(fromDate));
			 }
			 }
			 
			 String toDate=request.getParameter("positionToDate" + i);
			 if(toDate!=null){
			 positionHeld.setToDate(format.parse(toDate));
			 }
			 
			 String position=request.getParameter("positionPosition" + i);
			 if(position!=null){
			 positionHeld.setPosition(position);
			 }
			 
			String id=request.getParameter("positionId"+ i);
	        if(id!=null){
	        	if(!id.isEmpty()){
	        	positionHeld.setId(Long.parseLong(id));
	        	}
	        }
	        	
	        String version=request.getParameter("positionVersion"+ i);
	        if(version!=null){
	        	if(!version.isEmpty()){
	        	positionHeld.setId(Long.parseLong(version));
	        	}
	        }
	        
	        String locale=request.getParameter("positionLocale"+ i);
	        if(locale!=null){
	        	if(!locale.isEmpty()){
	        	positionHeld.setLocale(locale);
	        }
	        }
			 positions.add(positionHeld);	     
		 }
			 domain.setPositionsHeld(positions);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    protected void customValidateUpdate(final Member domain,
            final BindingResult result, final HttpServletRequest request) {
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
    }
	@SuppressWarnings("rawtypes")
	protected <E extends BaseDomain> void customInitBinderSuperClass(
            final Class clazz, final WebDataBinder binder) {
		// Set Date Editor
        CustomParameter parameter = CustomParameter.findByName(
                CustomParameter.class, "SERVER_DATEFORMAT", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat(parameter.getValue());
        dateFormat.setLenient(true);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
                dateFormat, true));
	}
}


