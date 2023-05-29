package org.mkcl.els.mobileApiServices;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import groovy.json.StringEscapeUtils;


@Controller
@RequestMapping("/mobileApiService/CustomParameterWebService")
public class CustomParameterWebService {
	
	@RequestMapping(value="/getscp/{cpname}")
	public @ResponseBody String getSpecificCustomParameterwithoutLocale(@PathVariable("cpname") final String customParameterName, HttpServletRequest request, HttpServletResponse response) {
		
		CustomParameter customParameter = null;
		System.out.println(customParameterName);
		try {
			customParameter = CustomParameter.findByName(CustomParameter.class, customParameterName, "");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return customParameter.getValue().toString();
    		
	}
	
	@RequestMapping(value="/getscp/{cpname}/{locale}",produces="text/plain;charset=UTF-8")
	public @ResponseBody String getSpecificCustomParameter(@PathVariable("cpname") final String customParameterName,@PathVariable("locale") final String locale, HttpServletRequest request, HttpServletResponse response) {
		
		CustomParameter customParameter = null;
		 
		try {
			customParameter = CustomParameter.findByName(CustomParameter.class, customParameterName, locale);
			//System.out.println(customParameter);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return customParameter.getValue();
    		
	}


}
