package org.mkcl.els.controller.ris;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Roster;
import org.mkcl.els.domain.Slot;
import org.mkcl.els.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roster/slot")
public class SlotController extends GenericController<Slot>{
	
	@Override
	protected void populateNew(final ModelMap model, final Slot domain, final String locale,
			final HttpServletRequest request) {
		
		/**** Locale ****/
		domain.setLocale(locale.toString());
		
		/**** User ***/
		String strRoster=request.getParameter("roster");
		if(strRoster!=null&&!strRoster.isEmpty()){
			Roster roster=Roster.findById(Roster.class,Long.parseLong(strRoster));
			List<User> users=roster.getUsers();
			model.addAttribute("users",users);
		}
	}

}
