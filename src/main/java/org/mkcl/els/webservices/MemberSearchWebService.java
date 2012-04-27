/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.webservices.MemberSearchWebService.java
 * Created On: Apr 17, 2012
 */
package org.mkcl.els.webservices;

import org.mkcl.els.common.vo.MemberSearchPage;
import org.mkcl.els.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class MemberSearchWebService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/ws/membersearch")
public class MemberSearchWebService {

	/**
	 * Search.
	 *
	 * @param housetype the housetype
	 * @param criteria1 the criteria1
	 * @param criteria2 the criteria2
	 * @param locale the locale
	 * @return the member search page
	 */
	@RequestMapping(value="/{housetype}/{criteria1}/{criteria2}/{locale}")
	public @ResponseBody MemberSearchPage search(@PathVariable final String housetype,@PathVariable final String criteria1,@PathVariable final Long criteria2,@PathVariable final String locale){
		return Member.search(housetype,criteria1, criteria2,locale);
	}
}
