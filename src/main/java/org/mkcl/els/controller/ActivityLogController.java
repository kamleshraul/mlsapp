package org.mkcl.els.controller;

import org.mkcl.els.domain.ActivityLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/activitylog")
public class ActivityLogController extends GenericController<ActivityLog>{
	
}
