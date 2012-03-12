package org.mkcl.els.hook.adapter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class CustomAnnotationMethodHandlerAdapter extends AnnotationMethodHandlerAdapter{
	public ServletRequestDataBinder createCustomBinder(HttpServletRequest request,
            Object target,
            String objectName)
     throws Exception{		
		return this.createBinder(request, target, objectName);		
	}	
}
