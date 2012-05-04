/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 ${company_name}.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.mis.MemberListController.java
 * Created On: May 4, 2012
 */
package org.mkcl.els.controller.mis;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class MemberListController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("member")
public class MemberListController extends GenericController<Member> {

 	/* (non-Javadoc)
 	 * @see org.mkcl.els.controller.GenericController#populateModule(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest, java.lang.String, org.mkcl.els.common.vo.AuthUser)
 	 */
 	@Override
	    protected void populateModule(final ModelMap model,
	            final HttpServletRequest request, final String locale,
	            final AuthUser currentUser) {
	        model.addAttribute("housetype", currentUser.getHouseType());
	    }
}
