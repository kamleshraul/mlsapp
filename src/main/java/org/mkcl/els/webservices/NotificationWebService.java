package org.mkcl.els.webservices;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.NotificationVO;
import org.mkcl.els.domain.notification.Notification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ws/notification")
public class NotificationWebService {
	
	
	  /**
     * Gets the notification for current user.
     *
     * @param receiver the receiver 
     * @param locale the locale
     * @return the notification for current user
     */
	
	@RequestMapping(value = "/{receiver}/{locale}",method=RequestMethod.GET)
    public @ResponseBody List<NotificationVO> getNotification(@PathVariable("receiver") final String receiver ,
            @PathVariable("locale") final String locale,
           HttpServletRequest request, HttpServletResponse response){
    	response.setHeader("Access-Control-Allow-Origin", "*");
    	return Notification.fetchNotificationsVOForCurrentUserInRange(receiver, locale, 0, 20);
    }
	
	

}
