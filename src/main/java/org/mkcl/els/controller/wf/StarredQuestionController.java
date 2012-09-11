package org.mkcl.els.controller.wf;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/question/starred")
public class StarredQuestionController {

	// TODO
	@RequestMapping(value="process", method=RequestMethod.GET)
	public String process(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return null;
	}
	
}
