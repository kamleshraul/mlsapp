/*
******************************************************************
File: org.mkcl.els.controller.HomeController.java
Copyright (c) 2011, vishals, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IMenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// TODO: Auto-generated Javadoc
/**
 * Handles requests for the application home page.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
public class HomeController extends BaseController{

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/** The menu service. */
	@Autowired
	private IMenuItemService menuService;
	
	@Autowired
	private ICustomParameterService customParameterService;
	/**
	 * Gets the Login page.
	 *
	 * @return the string
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam(required=false) String lang,Model model) {
		List<String> locales=new ArrayList<String>();
		
		if(lang!=null){
		if(lang.equals("en")||lang.isEmpty()){
			locales.add("en#English");
			locales.add("hi_IN#Hindi");
			locales.add("mr_IN#Marathi");
		}else if(lang.equals("hi_IN")){
			locales.add("hi_IN#Hindi");
			locales.add("en#English");
			locales.add("mr_IN#Marathi");
		}else if(lang.equals("mr_IN")){
			locales.add("mr_IN#Marathi");
			locales.add("hi_IN#Hindi");
			locales.add("en#English");
		}
		}else{
			locales.add("en#English");
			locales.add("hi_IN#Hindi");
			locales.add("mr_IN#Marathi");
		}
		model.addAttribute("locales",locales);		
		return "login";
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String home(ModelMap model, HttpServletRequest request, Locale locale) {
		String menuXml = menuService.getMenuXml(locale);
		model.addAttribute("menu_xml", menuXml);
		//used by datepicker to read the date,time format
		model.addAttribute("dateFormat",customParameterService.findByName("SERVER_DATEFORMAT").getValue());
		model.addAttribute("timeFormat",customParameterService.findByName("SERVER_TIMEFORMAT").getValue());			
		return "home2";
	}
	
}

