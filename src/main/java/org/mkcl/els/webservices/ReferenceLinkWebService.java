package org.mkcl.els.webservices;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.ReferenceLinkVO;
import org.mkcl.els.domain.ReferenceLinks;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/ws/yaadi")
public class ReferenceLinkWebService {

	@RequestMapping(value = "/{housetype}/{devicetype}/{documenttype}",method=RequestMethod.GET)
    public @ResponseBody List<ReferenceLinkVO> getReference(@PathVariable("devicetype") final Integer devicetype ,
            @PathVariable("housetype") final Integer housetype, @PathVariable("documenttype") final Integer documenttype,
           HttpServletRequest request, HttpServletResponse response){
    	response.setHeader("Access-Control-Allow-Origin", "*");
    	return ReferenceLinks.findReferences(housetype,devicetype,documenttype);
    }
	
}
