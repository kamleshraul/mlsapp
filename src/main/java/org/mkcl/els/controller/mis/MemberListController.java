package org.mkcl.els.controller.mis;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("member")
public class MemberListController extends GenericController<Member> {
	 @Override
	    protected void populateModule(final ModelMap model,
	            final HttpServletRequest request, final String locale,
	            final AuthUser currentUser) {
	        model.addAttribute("housetype", currentUser.getHouseType());
	    }
}
