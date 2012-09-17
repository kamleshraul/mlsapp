package org.mkcl.els.controller.wf;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Question;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/workflow/question")
public class QuestionWorkflowController extends BaseController{


    @RequestMapping(value="assistant",method=RequestMethod.GET)
    public String initAssistant(final ModelMap model,
            final HttpServletRequest request,
            final Locale locale) {
        //Assistant will start the workflow and the first task we will create
        //is of assistant to make him part of the workflow.Assistant will
        //be part of the workflow when the control is returned from any
        //task
        //first we will add taskId to the request
        model.addAttribute("taskId",request.getParameter("taskId"));
        //then we add usergroup to the request
        model.addAttribute("usergroup",request.getParameter("usergroup"));
        //we also add deviceId and deviceType to the request
        model.addAttribute("deviceId", request.getParameter("deviceId"));
        model.addAttribute("deviceType", request.getParameter("deviceType"));
        //we create an empty question domain
        Question question=new Question();
        question.setLocale(locale.toString());
        return "workflow/starred/secretary";
    }

	@RequestMapping(value="secretary",method=RequestMethod.GET)
	public String initSecretary(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
	    //We are assuming that all the secretaries will have more or less same
	    //view and hence in case of secretaries we will have workflow/starred
	    ///secretary
	    //first we will add taskId to the request
        model.addAttribute("taskId",request.getParameter("taskId"));
        //then we add usergroup to the request
        model.addAttribute("usergroup",request.getParameter("usergroup"));
		return "workflow/starred/secretary";
	}

	@RequestMapping(value="speaker",method=RequestMethod.GET)
    public String initSpeaker(final ModelMap model,
            final HttpServletRequest request,
            final Locale locale) {
        //speaker/chairman can need customized jsp and hence we are keeping separate
	    //jsp for it.
	    //first we will add taskId to the request
        model.addAttribute("taskId",request.getParameter("taskId"));
        //then we add usergroup to the request
        model.addAttribute("usergroup",request.getParameter("usergroup"));
        return "workflow/starred/secretary";
    }

}
