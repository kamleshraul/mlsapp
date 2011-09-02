/*
******************************************************************
File: org.mkcl.els.controller.AccountController.java
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

import org.mkcl.els.common.vo.Password;
import org.mkcl.els.domain.User;
import org.mkcl.els.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Class AccountController.
 *
 * @author vishals
 * @version v1.0.0
 */
@Controller
@RequestMapping("/acct")
public class AccountController extends BaseController{
	
	/** The user service. */
	@Autowired
	IUserService userService;
	
	/**
	 * Gets the aggregated account details page.
	 *
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String get(ModelMap model){
		return "account/acct";
	}
	
	/**
	 * Gets the password form.
	 *
	 * @param model the model
	 * @return the password form
	 */
	@RequestMapping(value="changepwd", method=RequestMethod.GET)
    public String getPasswordForm(ModelMap model){
		Password password = new Password();
		model.addAttribute("password", password);
		return "account/change_pwd";
	}
	
	/**
	 * Gets the about details page.
	 *
	 * @param model the model
	 * @return the about details
	 */
	@RequestMapping(value="about", method=RequestMethod.GET)
    public String getAboutDetails(ModelMap model){
		return "account/about_me";
	}
	
	/**
	 * Change password.
	 *
	 * @param password the password
	 * @param result the result
	 * @param model the model
	 * @return the string
	 */
	@RequestMapping(value="changepwd", method=RequestMethod.POST)
    public String changePassword(@ModelAttribute Password password, BindingResult result, ModelMap model){
		this.validatePassword(password, result);
		if(result.hasErrors()){
			model.addAttribute("isvalid",false);
			return "redirect:changepwd??type=error&msg=update_failed";
		}
		User user = userService.findByUsername(this.getCurrentUser().getUsername());
		user.setPassword(password.getNewPassword());
		userService.update(user);
		model.addAttribute("password", password);
		model.addAttribute("updateflag", true);
		return "account/change_pwd";
	}
	
	/**
	 * Validate password.
	 *
	 * @param password the password
	 * @param errors the errors
	 */
	private void validatePassword(Password password, Errors errors){
		if(!password.getOldPassword().equals(this.getCurrentUser().getPassword())){
			errors.rejectValue("oldPassword","Mismatch");
		}
	}

}
