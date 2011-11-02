package org.mkcl.els.webservices;

import javax.servlet.http.HttpServletResponse;
import org.mkcl.els.common.vo.MemberBiographyVO;
import org.mkcl.els.domain.Document;
import org.mkcl.els.service.IMemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ws/biography")
public class MemberBiographyWebService {

	@Autowired
	IMemberDetailsService memberDetailsService;
	
	@RequestMapping(value="/{id}/{locale}")
	public @ResponseBody MemberBiographyVO getBiography(@PathVariable("id") long id,@PathVariable("locale") String locale){
		return memberDetailsService.findBiography(id,locale);
	}
	
	@RequestMapping(value="/photo/{tag}")
	public @ResponseBody byte[] getPhoto(@PathVariable("tag") String tag,HttpServletResponse response){		
		Document document=memberDetailsService.getPhoto(tag);
		return document.getFileData();			
	}
}
