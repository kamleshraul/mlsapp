/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.wf.WorkflowController.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.controller.wf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.controller.BaseController;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkflowController.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Controller
@RequestMapping("/workflow")
public class WorkflowController extends BaseController {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** The process service. */
	@Autowired
	private IProcessService processService;

	//==================== Deployment Methods ====================

	/**
	 * Deploy module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/module", method=RequestMethod.GET)
	public String deployModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.module(model, request, locale);
	}

	/**
	 * Deploy list.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/list", method=RequestMethod.GET)
	public String deployList(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.list(model, request, locale);
	}

	/**
	 * Deploy new.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/new", method=RequestMethod.GET)
	public String deployNew(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		model.addAttribute("type", "");
		return this.getResourcePath(request);
	}

	/**
	 * Deploy create.
	 *
	 * @param model the model
	 * @param request the request
	 * @param docTag the doc tag
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/{docTag}/create", method=RequestMethod.GET)
	public @ResponseBody String deployCreate(final ModelMap model,
			final HttpServletRequest request,
			final @PathVariable("docTag") String docTag,
			final Locale locale) {
		Document document = Document.findByTag(docTag);
		if(document != null) {
			InputStream is = new ByteArrayInputStream(document.getFileData());
			this.processService.deploy(document.getOriginalFileName(), is);
			try {
				is.close();
			}
			catch (IOException e) {
				this.logger.error(e.getMessage());
			}
			// The process has been successfully deployed. The document object
			// is no longer necessary, so remove it from the datastore.
			document.remove();
		}
		return "";
	}

	// Deletes the process definition, cascades deletion to process instances,
	// history process instances and jobs.
	/**
	 * Deploy delete.
	 *
	 * @param model the model
	 * @param request the request
	 * @param procDefId the proc def id
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="deploy/{procDefId}/delete", method=RequestMethod.DELETE)
	public String deployDelete(final ModelMap model,
			final HttpServletRequest request,
			final @PathVariable("procDefId") String procDefId,
			final Locale locale) {
		ProcessDefinition processDefinition = processService.findProcessDefinitionById(procDefId);
		processService.undeploy(processDefinition, true);
		return "info";
	}

	//==================== My Task Methods ====================

	/**
	 * My tasks module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="myTasks/module", method=RequestMethod.GET)
	public String myTasksModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.getResourcePath(request);
	}

	/**
	 * My tasks list.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="myTasks/list", method=RequestMethod.GET)
	public String myTasksList(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.list(model, request, locale);
	}

	/**
	 * Follow a convention where URL path corresponds to JSP folder structure.
	 * If a JSP exists with following folder structure
	 * question
	 * |- starred
	 * |- process.jsp
	 * then the URL path must be question/starred/process.
	 *
	 * @param model the model
	 * @param request the request
	 * @param response the response
	 * @param taskId the task id
	 * @param locale the locale
	 */
	@RequestMapping(value="myTasks/{taskId}/process", method=RequestMethod.GET)
	public void myTasksProcess(final ModelMap model,
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @PathVariable("taskId") String taskId,
			final Locale locale) {
		Task task = this.processService.findTaskById(taskId);
		String formKey = this.processService.getFormKey(task);
		//add taskId to the request
		request.setAttribute("taskId",taskId);
		if(formKey != null) {
			try {
				request.getRequestDispatcher("/" + formKey).forward(request, response);
			}
			catch (ServletException e) {
				this.logger.error(e.getMessage());
			}
			catch (IOException e) {
				this.logger.error(e.getMessage());
			}
		}
	}

	//==================== Group Task Methods ===================

	/**
	 * Group tasks module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="groupTasks/module", method=RequestMethod.GET)
	public String groupTasksModule(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.getResourcePath(request);
	}

	/**
	 * Group tasks list.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="groupTasks/list", method=RequestMethod.GET)
	public String groupTasksList(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.list(model, request, locale);
	}

	// TODO
	/**
	 * Group tasks claim.
	 *
	 * @param model the model
	 * @param request the request
	 * @param response the response
	 * @param taskId the task id
	 * @param locale the locale
	 * @return the string
	 */
	@RequestMapping(value="groupTasks/{taskId}/claim", method=RequestMethod.GET)
	public String groupTasksClaim(final ModelMap model,
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @PathVariable("taskId") String taskId,
			final Locale locale) {
		return null;
	}

	//==================== Internal Methods ===================

	/**
	 * Module.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	private String module(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return this.getResourcePath(request);
	}

	/**
	 * List.
	 *
	 * @param model the model
	 * @param request the request
	 * @param locale the locale
	 * @return the string
	 */
	private String list(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		String resourcePath = this.getResourcePath(request);
		String urlPattern = resourcePath.split("\\/list")[0];
		Grid grid = Grid.findByDetailView(urlPattern, locale.toString());
		model.addAttribute("gridId", grid.getId());
		return resourcePath;
	}

	/**
	 * Gets the resource path.
	 *
	 * @param request the request
	 * @return the resource path
	 */
	private String getResourcePath(final HttpServletRequest request) {
		String resourcePath = request.getServletPath().replaceFirst("\\/", "");
		return resourcePath;
	}

}
