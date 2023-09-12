package org.mkcl.els.controller.question;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.MemberContactVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.RevisionHistoryVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Citation;
import org.mkcl.els.domain.ClubbedEntity;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.QuestionDates;
import org.mkcl.els.domain.ReferenceUnit;
import org.mkcl.els.domain.Role;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.SubDepartment;
import org.mkcl.els.domain.SupportingMember;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequestMapping("question")
public class QuestionController extends GenericController<Question> {

	@Autowired
	private IProcessService processService;
	
	
	public IProcessService getProcessService(){
		return processService;
	}
	
	@Override
	protected void populateModule(final ModelMap model, 
			final HttpServletRequest request,
			final String locale, 
			final AuthUser currentUser) {
		try {
			DeviceType deviceType = QuestionController.getDeviceType(request, locale);
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateModule(model, request, locale, currentUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateModule(model, request, locale, currentUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateModule(model, request, locale, currentUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateModule(model, request, locale, currentUser);
			}
			else {
				throw new ELSException("QuestionController.populateModule/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected String modifyURLPattern(final String urlPattern,
			final HttpServletRequest request,
			final ModelMap model,
			final String locale) {
		try {
			AuthUser authUser = this.getCurrentUser();
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale);
			String deviceTypeType = deviceType.getType();
			String newUrlPattern = null;
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				newUrlPattern = StarredQuestionController.modifyURLPattern(urlPattern, request, model, authUser ,locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				newUrlPattern = UnstarredQuestionController.modifyURLPattern(urlPattern, request, model, authUser ,locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				newUrlPattern = ShortNoticeController.modifyURLPattern(urlPattern, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				newUrlPattern = HalfHourDiscussionFromQuestionController.modifyURLPattern(urlPattern, request, model, authUser, locale);
			}
			else {
				throw new ELSException("QuestionController.modifyURLPattern/4", 
						"Method invoked for inappropriate device type");
			}
			return newUrlPattern;
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected String modifyNewUrlPattern(final String servletPath,
			final HttpServletRequest request, 
			final ModelMap model, 
			final String locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale);
			String deviceTypeType = deviceType.getType();
			String newServletPath = servletPath;
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				newServletPath = StarredQuestionController.modifyNewUrlPattern(servletPath, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				newServletPath = UnstarredQuestionController.modifyNewUrlPattern(servletPath, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				newServletPath = ShortNoticeController.modifyNewUrlPattern(servletPath, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				newServletPath = HalfHourDiscussionFromQuestionController.modifyNewUrlPattern(servletPath, request, model, authUser, locale);
			}
			else {
				throw new ELSException("QuestionController.modifyNewUrlPattern/4", 
						"Method invoked for inappropriate device type");
			}
			return newServletPath;
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void populateNew(final ModelMap model, 
			final Question domain, 
			final String locale,
			final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale);
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateNew(model, domain, locale, authUser, request);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateNew(model, domain, locale, authUser, request);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateNew(model, domain, locale, authUser, request);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateNew(model, domain, locale, authUser, request);
			}
			else {
				throw new ELSException("QuestionController.populateNew/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	 protected void preValidateCreate(final Question domain,
	            final BindingResult result, 
	            final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.preValidateCreate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.preValidateCreate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.preValidateCreate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.preValidateCreate(domain, result, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	 }
	
	@Override
	protected void customValidateCreate(final Question domain, final BindingResult result,
			final HttpServletRequest request) {	
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.customValidateCreate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.customValidateCreate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.customValidateCreate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.customValidateCreate(domain, result, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	 protected void populateCreateIfErrors(final ModelMap model,
	            final Question domain,
	            final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateCreateIfErrors(model, domain, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateCreateIfErrors(model, domain, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateCreateIfErrors(model, domain, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateCreateIfErrors(model, domain, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void populateCreateIfNoErrors(final ModelMap model,
	            final Question domain, 
	            final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateCreateIfNoErrors(domain, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateCreateIfNoErrors(domain, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateCreateIfNoErrors(domain, model,request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateCreateIfNoErrors(domain, model, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}  
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final Question domain,
            final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateAfterCreate(domain, model, request, authUser, processService);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateAfterCreate(domain, model, request, authUser, processService);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateAfterCreate(domain, model,request, authUser, processService);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateAfterCreate(domain, model, request, authUser, processService);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}  
    }
	
	@Override
	protected String modifyEditUrlPattern(final String editUrlPattern,
			final HttpServletRequest request, final ModelMap model, final String locale) {
		AuthUser authUser = this.getCurrentUser();
		String editServletPath = editUrlPattern;
		try {
			DeviceType deviceType = null;
			try{
				deviceType = QuestionController.getDeviceTypeById(request, locale);
			}catch(NumberFormatException nfe){
				logger.error("error", nfe);
			}
			
			if(deviceType == null){
				deviceType = QuestionController.getDeviceType(request, locale);
			}
			String deviceTypeType = deviceType.getType();
			
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				editServletPath =  StarredQuestionController.
						modifyEditUrlPattern(editUrlPattern, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				editServletPath =  UnstarredQuestionController.
						modifyEditUrlPattern(editUrlPattern, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				editServletPath =  ShortNoticeController.
						modifyEditUrlPattern(editUrlPattern, request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				editServletPath =  HalfHourDiscussionFromQuestionController.
						modifyEditUrlPattern(editUrlPattern, request, model, authUser, locale);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return editServletPath;
	}
	
	@Override
	protected void populateEdit(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateEdit(domain, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateEdit(domain, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateEdit(domain, model,request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateEdit(domain, model, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
    protected void preValidateUpdate(final Question domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.preValidateUpdate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.preValidateUpdate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.preValidateUpdate(domain, result,request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.preValidateUpdate(domain, result, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
    }
	
	@Override
    protected void customValidateUpdate(final Question domain,
            final BindingResult result, 
            final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.customValidateUpdate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.customValidateUpdate(domain, result, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.customValidateUpdate(domain, result,request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.customValidateUpdate(domain, result, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
    }
	
	 @Override
	 protected void populateUpdateIfErrors(final ModelMap model, final Question domain,
	            final HttpServletRequest request) {
		 AuthUser authUser = this.getCurrentUser();
			try {
				DeviceType deviceType = domain.getType();
				String deviceTypeType = deviceType.getType();
				if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
					StarredQuestionController.populateUpdateIfErrors(domain, model, request, authUser);
				}
				else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					UnstarredQuestionController.populateUpdateIfErrors(domain, model, request, authUser);
				}
				else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					ShortNoticeController.populateUpdateIfErrors(domain, model,request, authUser);
				}
				else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					HalfHourDiscussionFromQuestionController.populateUpdateIfErrors(domain, model, request, authUser);
				}
				else {
					throw new ELSException("QuestionController.customValidateCreate/4", 
							"Method invoked for inappropriate device type");
				}
			}
			catch(ELSException elsx) {
				elsx.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
	 }
	 
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateUpdateIfNoErrors(domain, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateUpdateIfNoErrors(domain, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateUpdateIfNoErrors(domain, model,request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateUpdateIfNoErrors(domain, model, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void populateAfterUpdate(final ModelMap model, final Question domain,
			final HttpServletRequest request) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = domain.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				StarredQuestionController.populateAfterUpdate(domain, model, request, authUser, processService);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				UnstarredQuestionController.populateAfterUpdate(domain, model, request, authUser, processService);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				ShortNoticeController.populateAfterUpdate(domain, model,request, authUser, processService);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				HalfHourDiscussionFromQuestionController.populateAfterUpdate(domain, model, request, authUser, processService);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Transactional
	@Override
	protected Boolean preDelete(final ModelMap model, final BaseDomain domain,
			final HttpServletRequest request,final Long id) {
		
		Question question=Question.findById(Question.class, id);
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = question.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionController.preDelete(question, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				return UnstarredQuestionController.preDelete(question, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				return ShortNoticeController.preDelete(question, model, request, authUser);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				return HalfHourDiscussionFromQuestionController.preDelete(question, model, request, authUser);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
		
	}
	
	@RequestMapping(value="/determine_ordering_for_submission", method=RequestMethod.GET)
	public String determineOrderingForSubmissionInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
			String deviceTypeType = deviceType.getType();
			
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionController.
						determineOrderingForSubmissionInit(request, model, authUser, locale);
			}
			/*else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				return  UnstarredQuestionController.
						getBulkSubmissionView( request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				return  ShortNoticeController.
						getBulkSubmissionView( request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				return  HalfHourDiscussionFromQuestionController.
						getBulkSubmissionView(request, model, authUser, locale);
			}*/
			else {
				throw new ELSException("QuestionController.determineOrderingForSubmissionInit/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}	
	
	@Transactional
	@RequestMapping(value="determine_ordering_for_submission", method=RequestMethod.POST)
	public String determineOrderingForSubmission(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
			String deviceTypeType = deviceType.getType();
			
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionController.
						determineOrderingForSubmission(request, model, authUser, processService, locale);
			}
//			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
//				return  UnstarredQuestionController.
//						bulkSubmission( request, model, authUser, processService, locale);
//			}
//			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
//				return  ShortNoticeController.
//						bulkSubmission( request, model, authUser, processService, locale);
//			}
//			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
//				return  HalfHourDiscussionFromQuestionController.
//						bulkSubmission(request, model, authUser, processService, locale);
//			}
			else {
				throw new ELSException("QuestionController.determineOrderingForSubmission/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/bulksubmission", method=RequestMethod.GET)
	public String getBulkSubmissionView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
			String deviceTypeType = deviceType.getType();
			
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionController.
						getBulkSubmissionView(request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				return  UnstarredQuestionController.
						getBulkSubmissionView( request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				return  ShortNoticeController.
						getBulkSubmissionView( request, model, authUser, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				return  HalfHourDiscussionFromQuestionController.
						getBulkSubmissionView(request, model, authUser, locale);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	@Transactional
	@RequestMapping(value="bulksubmission", method=RequestMethod.POST)
	public synchronized String bulkSubmission(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		AuthUser authUser = this.getCurrentUser();
		try {
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
			String deviceTypeType = deviceType.getType();
			
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionController.
						bulkSubmission(request, model, authUser, processService, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
				return  UnstarredQuestionController.
						bulkSubmission( request, model, authUser, processService, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
				return  ShortNoticeController.
						bulkSubmission( request, model, authUser, processService, locale);
			}
			else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				return  HalfHourDiscussionFromQuestionController.
						bulkSubmission(request, model, authUser, processService, locale);
			}
			else {
				throw new ELSException("QuestionController.customValidateCreate/4", 
						"Method invoked for inappropriate device type");
			}
		}
		catch(ELSException elsx) {
			elsx.printStackTrace();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	/**** BULK SUBMISSION (ASSISTANT) ****/
	//
		@RequestMapping(value="/bulksubmission/assistant/int", method=RequestMethod.GET)
		public String getBulkSubmissionAssistantInt(final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {
			AuthUser authUser = this.getCurrentUser();
			try {
				DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
				String deviceTypeType = deviceType.getType();
				
				if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
					return StarredQuestionController.
							getBulkSubmissionAssistantInt(request, model, authUser, locale);
				}
				else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					return  UnstarredQuestionController.
							getBulkSubmissionAssistantInt( request, model, authUser, locale);
				}
				else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					return  ShortNoticeController.
							getBulkSubmissionAssistantInt( request, model, authUser, locale);
				}
				else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					return  HalfHourDiscussionFromQuestionController.
							getBulkSubmissionAssistantInt(request, model, authUser, locale);
				}
				else {
					throw new ELSException("QuestionController.getBulkSubmissionAssistantInt/3", 
							"Method invoked for inappropriate device type");
				}
			}
			catch(ELSException elsx) {
				elsx.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
		
		@RequestMapping(value="/bulksubmission/assistant/view", method=RequestMethod.GET)
		public String getBulkSubmissionAssistantView(final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {
			try {
				QuestionController.getBulkSubmissionQuestions(model, request, locale.toString());
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "question/bulksubmissionassistantview";
		}
		
		public static void getBulkSubmissionQuestions(final ModelMap model,
		final HttpServletRequest request, 
		final String locale) throws ELSException {
			/**** Request Params ****/
			HouseType houseType = QuestionController.getHouseTypebyType(request, locale);
			SessionType sessionType = QuestionController.getSessionType(request, locale);
			DeviceType deviceType = QuestionController.getDeviceType(request, locale);
			Integer sessionYear = QuestionController.stringToIntegerYear(request, locale);
			Integer itemCount = QuestionController.stringToIntegerItemCount(request, locale);
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
			String strStatus = request.getParameter("status");
			String strGroup = request.getParameter("group");
			String strDepartment = request.getParameter("department");
			if(strStatus != null && !(strStatus.isEmpty())) {
				List<Question> questions = new ArrayList<Question>();
				Group group=null;
				if(strGroup!=null && strGroup !=""){
					group=Group.findById(Group.class, Long.parseLong(strGroup));
				}
				SubDepartment subdepartment = null;
				if(strDepartment != null && !strDepartment.equals("-")){
					subdepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strDepartment));
				}
	
				Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
				questions = Question.findAllByStatus(session, deviceType, internalStatus,group , subdepartment,
						itemCount, locale);
				
				model.addAttribute("questions", questions);
				if(questions != null && ! questions.isEmpty()) {
					model.addAttribute("questionId", questions.get(0).getId());
				}
			}
		}	
		
		

		@Transactional
		@RequestMapping(value="/bulksubmission/assistant/update", method=RequestMethod.POST)
		public String bulkSubmissionAssistant(final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {	
			AuthUser authUser = this.getCurrentUser();
			try {
				DeviceType deviceType = QuestionController.getDeviceType(request, locale.toString());
				String deviceTypeType = deviceType.getType();
				
				if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
					return StarredQuestionController.
							bulkSubmissionAssistant(request, model, authUser, processService, locale);
				}
				else if (deviceTypeType.equals(ApplicationConstants.UNSTARRED_QUESTION)) {
					return  UnstarredQuestionController.
							bulkSubmissionAssistant( request, model, authUser, processService, locale);
				}
				else if (deviceTypeType.equals(ApplicationConstants.SHORT_NOTICE_QUESTION)) {
					return  ShortNoticeController.
							bulkSubmissionAssistant( request, model, authUser, processService, locale);
				}
				else if (deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
					return  HalfHourDiscussionFromQuestionController.
							bulkSubmissionAssistant(request, model, authUser, processService, locale);
				}
				else {
					throw new ELSException("QuestionController.getBulkSubmissionAssistantInt/3", 
							"Method invoked for inappropriate device type");
				}
			}
			catch(ELSException elsx) {
				elsx.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
		
		@RequestMapping(value="/bulktimeout/init", method=RequestMethod.GET)
		public String getBulkTimeoutInit(final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {
			AuthUser authUser = this.getCurrentUser();
			try {
				DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
				String deviceTypeType = deviceType.getType();
				
				if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
					return StarredQuestionController.
							getBulkTimeoutInit(request, model, authUser, locale);
				}
				else {
					throw new ELSException("QuestionController.getBulkTimeoutInit/3", 
							"Method invoked for inappropriate device type");
				}
			}
			catch(ELSException elsx) {
				elsx.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
		
		@RequestMapping(value="/bulktimeout/view", method=RequestMethod.GET)
		public String getBulkTimeoutView(final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {
			try {
				QuestionController.getBulkTimeoutQuestions(model, request, locale.toString());
			} catch (ELSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "question/bulk_timeout_view";
		}
		
		public static void getBulkTimeoutQuestions(final ModelMap model,
		final HttpServletRequest request, 
		final String locale) throws ELSException {
			/**** Request Params ****/
			HouseType houseType = QuestionController.getHouseTypebyType(request, locale);
			SessionType sessionType = QuestionController.getSessionType(request, locale);
			DeviceType deviceType = QuestionController.getDeviceType(request, locale);
			Integer sessionYear = QuestionController.stringToIntegerYear(request, locale);
			Integer itemCount = QuestionController.stringToIntegerItemCount(request, locale);
			Session session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);
			String strStatus = request.getParameter("status");
			String strGroup = request.getParameter("group");
			String strDepartment = request.getParameter("department");
			String selectedQuestionIds = request.getParameter("questionIds");
			
			if(selectedQuestionIds!=null && !selectedQuestionIds.isEmpty()) {
				List<Question> questions = new ArrayList<Question>();
				Group group=null;
				if(strGroup!=null && strGroup !=""){
					group=Group.findById(Group.class, Long.parseLong(strGroup));
				}
				SubDepartment subdepartment = null;
				if(strDepartment != null && !strDepartment.equals("-")){
					subdepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strDepartment));
				}
	
				Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));				
				for(String questionId: selectedQuestionIds.split(",")) {
					Question question = Question.findById(Question.class, Long.parseLong(questionId));
					if(question!=null && internalStatus!=null
							&& question.getInternalStatus().getId().equals(internalStatus.getId())) 
					{
						questions.add(question);
					}			
				}
				
				model.addAttribute("questions", questions);
				if(questions != null && ! questions.isEmpty()) {
					model.addAttribute("questionId", questions.get(0).getId());
				}
			}
			else {
				if(strStatus != null && !(strStatus.isEmpty())) {
					List<Question> questions = new ArrayList<Question>();
					Group group=null;
					if(strGroup!=null && strGroup !=""){
						group=Group.findById(Group.class, Long.parseLong(strGroup));
					}
					SubDepartment subdepartment = null;
					if(strDepartment != null && !strDepartment.equals("-")){
						subdepartment = SubDepartment.findById(SubDepartment.class, Long.parseLong(strDepartment));
					}
		
					Status internalStatus = Status.findById(Status.class,Long.parseLong(strStatus));
					questions = Question.findAllForTimeoutByStatus(session, deviceType, internalStatus,group , subdepartment,
							itemCount, locale);
					
					model.addAttribute("questions", questions);
					if(questions != null && ! questions.isEmpty()) {
						model.addAttribute("questionId", questions.get(0).getId());
					}
				}
			}
		}	
		
		

		@Transactional
		@RequestMapping(value="/bulktimeout/update", method=RequestMethod.POST)
		public String bulkTimeout(final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {	
			AuthUser authUser = this.getCurrentUser();
			try {
				DeviceType deviceType = QuestionController.getDeviceType(request, locale.toString());
				String deviceTypeType = deviceType.getType();
				
				if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
					return StarredQuestionController.
							bulkTimeout(request, model, authUser, processService, logger, locale);
				}
				else {
					throw new ELSException("QuestionController.bulkTimeout/3", 
							"Method invoked for inappropriate device type");
				}
			}
			catch(ELSException elsx) {
				elsx.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	
		@RequestMapping(value="/viewquestion",method=RequestMethod.GET)
		public String viewQuestion(final HttpServletRequest request,
				final ModelMap model,
				final Locale locale) throws ELSException{
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
			String deviceTypeType = deviceType.getType();
			//To solve the multiple request error while approving referencing / clubbing 
			// following page will be used to display the referenced / clubbed question
			if(deviceTypeType.startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
			
				 HalfHourDiscussionFromQuestionController.viewQuestion(request,model,locale);
			}
			return "question/viewquestion";
		}
		
		@RequestMapping(value="/getsubject",method=RequestMethod.GET)
		public @ResponseBody List getSubjectAndQuestion(final HttpServletRequest request,
				final ModelMap model,
				final Locale locale) throws ELSException{
			List data = new ArrayList();
			DeviceType deviceType = QuestionController.getDeviceTypeById(request, locale.toString());
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				data = HalfHourDiscussionFromQuestionController.getSubjectAndQuestion(request,model,locale);
			}
			return data;
		}
	//=================UTILITY METHODS==============================
	public static DeviceType getDeviceType(final HttpServletRequest request,
			final String locale) throws ELSException {
		String deviceTypeType = request.getParameter("questionType");
		
		if(deviceTypeType == null || deviceTypeType.isEmpty()) {
			throw new ELSException("QuestionController.getDeviceType/2", "Device type is not set in the Request");
		}
		
		DeviceType deviceType = DeviceType.findByType(deviceTypeType, locale);
		return deviceType;
	}
	
	public static DeviceType getDeviceTypeById(HttpServletRequest request,
			String locale) throws ELSException {
		String deviceTypeId = request.getParameter("questionType");
		
		if(deviceTypeId == null){
			deviceTypeId = (String)request.getSession().getAttribute("questionType");
		}
		
		if(deviceTypeId == null || deviceTypeId.isEmpty()) {
			throw new ELSException("QuestionController.getDeviceType/2", 
					"Device type is not set in the Request");
		}
		
		DeviceType deviceType = DeviceType.findById(DeviceType.class,Long.parseLong(deviceTypeId));
		return deviceType;
	}
	
	public static List<DeviceType> getQuestionDeviceTypes(final String locale) throws ELSException {
		List<DeviceType> deviceTypes = 
				DeviceType.findDeviceTypesStartingWith(ApplicationConstants.DEVICE_QUESTIONS, locale);
		return deviceTypes;
	}
	
	public static List<HouseType> getHouseTypes(final AuthUser user, final DeviceType deviceType,
			final String locale) throws ELSException {
		List<HouseType> houseTypes = new ArrayList<HouseType>();
		
		String strHouseType = user.getHouseType();
		if(strHouseType.equals(ApplicationConstants.LOWER_HOUSE)
				|| strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			houseTypes = HouseType.findAllByFieldName(HouseType.class, 
					"type", strHouseType, "name", ApplicationConstants.ASC, locale);
		}
		else if(strHouseType.equals(ApplicationConstants.BOTH_HOUSE)) {
			//check for lower house in the active usergroup having selected device type
			HouseType lowerHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
			List<UserGroup> currentUserGroupsWithDeviceTypeForLowerHouse = UserGroup.findActiveUserGroupsOfGivenUser(user.getActualUsername(), lowerHouseType.getName(), deviceType.getName(), locale);
			if(currentUserGroupsWithDeviceTypeForLowerHouse!=null && !currentUserGroupsWithDeviceTypeForLowerHouse.isEmpty()) {
				houseTypes.add(lowerHouseType);
			}
			//check for upper house in the active usergroup having selected device type
			HouseType upperHouseType = HouseType.findByType(ApplicationConstants.UPPER_HOUSE, locale);
			List<UserGroup> currentUserGroupsWithDeviceTypeForUpperHouse = UserGroup.findActiveUserGroupsOfGivenUser(user.getActualUsername(), upperHouseType.getName(), deviceType.getName(), locale);
			if(currentUserGroupsWithDeviceTypeForUpperHouse!=null && !currentUserGroupsWithDeviceTypeForUpperHouse.isEmpty()) {
				houseTypes.add(upperHouseType);
			}
			if(houseTypes.isEmpty()) { //no active usergroup for the user or no need for having usergroup for the user
				houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale);
			}			
		}
		else {
			throw new ELSException("QuestionController.getHouseTypes/2", 
					"Inappropriate house type is set in AuthUser.");
		}
		
		return houseTypes;
	}
	
	public static HouseType getHouseType(final AuthUser user,
			final String locale) throws ELSException {
		// Assumption: LOWER_HOUSE is the default house type
		HouseType houseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, locale);
		
		String strHouseType = user.getHouseType();
		if(strHouseType.equals(ApplicationConstants.UPPER_HOUSE)) {
			houseType = HouseType.findByType(strHouseType, locale);
		}
		
		// In case strHouseType = "BOTH_HOUSE", return the default houseType i.e LOWER_HOUSE
		return houseType;
	}
	
	private static HouseType getHouseTypebyType(HttpServletRequest request,
			String locale) throws ELSException {
		String houseTypeType = request.getParameter("houseType");
		
		if(houseTypeType == null || houseTypeType.isEmpty()) {
			throw new ELSException("QuestionController.getDeviceType/2", "Device type is not set in the Request");
		}
		
		HouseType houseType = HouseType.findByType(houseTypeType, locale);
		return houseType;
	}
	
	public static List<SessionType> getSessionTypes(final String locale) throws ELSException {
		List<SessionType> sessionTypes = 
				SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale);
		return sessionTypes;
	}
	
	public static List<Integer> getSessionYears(final Integer latestYear) throws ELSException {
		List<Integer> years = new ArrayList<Integer>();
		
		CustomParameter houseFormationYear = 
				CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
		if(houseFormationYear != null) {
			Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
			for(int i = latestYear; i >= formationYear; i--) {
				years.add(i);
			}
		}
		else {
			throw new ELSException("QuestionController.getSessionYears/1", 
					"HOUSE_FORMATION_YEAR key is not set as CustomParameter");
		}
		
		return years;
	}
	
	public static List<UserGroupType> delimitedStringToUGTList(final String delimitedUserGroups,
			final String delimiter,
			final String locale) {
		List<UserGroupType> userGroupTypes = new ArrayList<UserGroupType>();
		
		String[] strUserGroupTypes = delimitedUserGroups.split(delimiter);
		for(String strUserGroupType : strUserGroupTypes) {
			strUserGroupType = strUserGroupType.trim();
			UserGroupType ugt = UserGroupType.findByType(strUserGroupType, locale);
			userGroupTypes.add(ugt);
		}
		
		return userGroupTypes;
	}
	
	/**
	 * Return a userGroup from @param userGroups whose userGroupType is 
	 * same as one of the @param userGroupTypes.
	 * 
	 * Return null if no match is found.
	 * @throws ELSException 
	 */
	public static UserGroup getUserGroup(final List<UserGroup> userGroups,
			final List<UserGroupType> userGroupTypes, 
			final Session session,
			final String locale) throws ELSException {		
		for(UserGroup ug : userGroups) {
			if(UserGroup.isActiveInSession(session,ug,locale)){
				for(UserGroupType ugt : userGroupTypes) {
					UserGroupType userGroupType = ug.getUserGroupType();
					if(ugt.getId().equals(userGroupType.getId())) {
						return ug;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Return true if @param userGroupType is present in the collection
	 * @param userGroupTypes 
	 */
	public static boolean isUserGroupTypeExists(final List<UserGroupType> userGroupTypes,
			final UserGroupType userGroupType) {
		if(userGroupType != null){
			for(UserGroupType ugt : userGroupTypes) {
				if(ugt != null){
					if(ugt.getId().equals(userGroupType.getId())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static List<SubDepartment> getSubDepartments(final String delimitedSubDepartmentNames,
			final String delimiter,
			final String locale) {
		List<SubDepartment> subDepartments = new ArrayList<SubDepartment>();
		
		String subDepartmentNames[] = delimitedSubDepartmentNames.split(delimiter);
		for(String subDepartmentName : subDepartmentNames){
			SubDepartment subDepartment = 
					SubDepartment.findByName(SubDepartment.class, subDepartmentName, locale);
			subDepartments.add(subDepartment);
		}
		
		return subDepartments;
	}
	
	public static List<Integer> delimitedStringToIntegerList(final String delimitedInts,
			final String delimiter) {
		List<Integer> ints = new ArrayList<Integer>();
		
		String[] strInts = delimitedInts.split(delimiter);
		for(String strInt : strInts) {
			Integer i = Integer.parseInt(strInt);
			ints.add(i);
		}
		
		return ints;
	}
	
	public static Role getRole(final HttpServletRequest request,
			final String locale) throws ELSException{
		Role role = null;
		String strRole = request.getParameter("role");
		if(strRole != null && !strRole.isEmpty()) {
			role = Role.findByType(strRole, locale); 
		}
		return role;
	}
	
	public static HouseType getHouseType(final HttpServletRequest request,
			final String locale) throws ELSException{
		String strHouseType = request.getParameter("houseType");
		
		if(strHouseType == null || strHouseType.isEmpty()) {
			throw new ELSException("QuestionController.getHouseType/2", "HouseType is not set in the Request");
		}
		HouseType houseType = HouseType.findByType(strHouseType, locale);
		
		return houseType;
	}
	
	
	public static List<Role> delimitedStringToRoleList(final String delimitedRoles,
			final String delimiter,
			final String locale) {
		List<Role> roles = new ArrayList<Role>();
		
		String[] strRoles = delimitedRoles.split(delimiter);
		for(String strRole : strRoles) {
			Role role = Role.findByType(strRole, locale);
			roles.add(role);
		}
		
		return roles;
	}
	
	/**
	 * Return true if @param role is present in the collection
	 * @param roles 
	 */
	public static boolean isRoleExists(final List<Role> roles,
			final Role role) {
		for(Role r : roles) {
			if(r != null && role != null){
				if(role.getId().equals(r.getId())) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static SessionType getSessionType(final HttpServletRequest request,
			final String locale) throws ELSException {
		String strSessionType = request.getParameter("sessionType");
		
		if(strSessionType == null || strSessionType.isEmpty()) {
			throw new ELSException("QuestionController.getSessionType/2", "sessionType is not set in the Request");
		}
		SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
		
		return sessionType;
	}

	public static Date getRotationOrderPublishingDate(final Session session) throws ParseException, ELSException{
		Date rotationOrderPubDate = null;
		String strRotationOrderPubDate = session.getParameter("questions_starred_rotationOrderPublishingDate");
		if(strRotationOrderPubDate != null && !strRotationOrderPubDate.isEmpty()){
			CustomParameter serverDateFormat =
					CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
			if(serverDateFormat == null){
				throw new ELSException("QuestionController.getRotationOrderPublishingDate/1", 
						"Custom Parameter serverDateFormat is not set "); 
			}
			rotationOrderPubDate = FormaterUtil.
					getDateFormatter(serverDateFormat.getValue(), "en_US").parse(strRotationOrderPubDate);
		}
		return rotationOrderPubDate;
	}
	
	public static Constituency getConstituency(final Member member,
			final Date onDate){
		Constituency constituency = Member.findConstituency(member, onDate);
		return constituency;
	}
	
	public static Integer stringToIntegerYear(final HttpServletRequest request,
			final String locale) throws ELSException{
		Integer sessionYear = null;
		String selectedYear = request.getParameter("sessionYear");
		if(selectedYear != null && !selectedYear.isEmpty()) {
			sessionYear = Integer.parseInt(selectedYear);
			return sessionYear;
		}else{
			throw new ELSException("QuestionController.stringToIntegerYear/2", 
					"Session Year is not set in request "); 
		}
		
	}
		
	
	/****Populating Methods****/	
	public static Member populateMember(final ModelMap model,
			final AuthUser authUser,
			final String locale){
		Member member = Member.findMember(authUser.getFirstName(), authUser.getMiddleName(),
							authUser.getLastName(), authUser.getBirthDate(), locale);
		if(member != null) {
			model.addAttribute("primaryMember", member.getId());
			model.addAttribute("formattedPrimaryMember", member.getFullname());
		}
		return member;
	}
	
	public static String getDelimitedSupportingMembers(final ModelMap model,
			final Question domain, String usergroupType) {
		String memberNames = "";
		List<SupportingMember> selectedSupportingMembers = domain.getSupportingMembers();
		if(selectedSupportingMembers != null){
			if(!selectedSupportingMembers.isEmpty()){
				StringBuffer bufferFirstNamesFirst = new StringBuffer();
				for(SupportingMember i:selectedSupportingMembers){
					//if(usergroupType != null && !usergroupType.isEmpty() && (usergroupType.equals("member") || usergroupType.equals("typist"))){
					if(domain.getStatus()!=null 
							&& !domain.getStatus().getType().endsWith(ApplicationConstants.STATUS_COMPLETE)
							&& !domain.getStatus().getType().endsWith(ApplicationConstants.STATUS_INCOMPLETE)){
						if(i.getDecisionStatus().getType().equals(ApplicationConstants.SUPPORTING_MEMBER_APPROVED)){
							Member m = i.getMember();
							if(m.isActiveMemberOn(new Date(), domain.getLocale())){
								bufferFirstNamesFirst.append(m.getFullname() + ",");
							}
						}												
					}else{
						Member m = i.getMember();
						if(m.isActiveMemberOn(new Date(), domain.getLocale())){
							bufferFirstNamesFirst.append(m.getFullname() + ",");
						}
					}
				}
				if(bufferFirstNamesFirst.length()>0){
					bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
					memberNames = bufferFirstNamesFirst.toString();
				}
			}
		}
		return memberNames;
	}

	public static List<SupportingMember> getSupportingMembers(
			final HttpServletRequest request,
			final Question domain,
			final Role role,
			final String locale) {
		List<SupportingMember> supportingMembers = new ArrayList<SupportingMember>();
		List<SupportingMember> members=new ArrayList<SupportingMember>();
		if(domain.getId()!=null){
			Question question=Question.findById(Question.class,domain.getId());
			members=question.getSupportingMembers();
		}
		String[] strSupportingMemberIds = request.getParameterValues("selectedSupportingMembers");
		if(strSupportingMemberIds != null && strSupportingMemberIds.length > 0) {
			for(String strSupportingMemberId : strSupportingMemberIds) {
				Long supportingMemberId = Long.parseLong(strSupportingMemberId);
				Member member = Member.findById(Member.class, supportingMemberId);
				SupportingMember supportingMember=null;
				for(SupportingMember j : members){
					if(j.getMember().getId() == member.getId()){
						supportingMember = j;
						break;
					}
				}
				Status NOT_SEND = Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_NOTSEND, locale);
				if(supportingMember == null){
					supportingMember = new SupportingMember();
					supportingMember.setMember(member);
					supportingMember.setLocale(locale);
					supportingMember.setDecisionStatus(NOT_SEND);
					
					CustomParameter supportingMemberAutoApprovalAllowedTo = 
							CustomParameter.findByName(CustomParameter.class, 
									"QIS_SUPPORTINGMEMBER_AUTO_APPROVAL_ALLOWED_TO", "");
					if(supportingMemberAutoApprovalAllowedTo != null) {
						List<Role> roles = QuestionController.delimitedStringToRoleList(
								supportingMemberAutoApprovalAllowedTo.getValue(), ",", locale);
						Boolean isRoleExists = QuestionController.isRoleExists(roles, role);
						if(isRoleExists) {
							Status APPROVED = 
									Status.findByType(ApplicationConstants.SUPPORTING_MEMBER_APPROVED, locale);
							supportingMember.setDecisionStatus(APPROVED);
							
							supportingMember.setApprovalType(
									ApplicationConstants.SUPPORTING_MEMBER_APPROVALTYPE_AUTOAPPROVED);
							
							String subject = domain.getSubject();
							String questionText = domain.getQuestionText();
							supportingMember.setApprovedSubject(subject);
							supportingMember.setApprovedText(questionText);
							
						}
					}
				}
				supportingMembers.add(supportingMember);
			}
		}
		
		return supportingMembers;
	}

	public static UserGroupType getUserGroupType(HttpServletRequest request,
			String locale) {
		String strUserGroupType = request.getParameter("usergroupType");
		if(strUserGroupType != null && !strUserGroupType.isEmpty()){
			UserGroupType userGroupType = UserGroupType.findByType(strUserGroupType,locale);
			return userGroupType;
		}
		return null;
	}

	public static UserGroup getUserGroup(HttpServletRequest request,
			String locale) {
		String strUserGroup  = request.getParameter("usergroup");
		if(strUserGroup != null && !strUserGroup.isEmpty()){
			UserGroup userGroup = UserGroup.findById(UserGroup.class, Long.parseLong(strUserGroup));
			return userGroup;
		}
		return null;
	}
	
	public static void populateInternalStatus(final ModelMap model, final String type,final String userGroupType,final String locale, final String questionType) {
		List<Status> internalStatuses=new ArrayList<Status>();
		try{
			CustomParameter specificDeviceStatusUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()
							+"_"+type.toUpperCase()+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificDeviceUserGroupStatuses = CustomParameter.
					findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+questionType.toUpperCase()
							+"_"+userGroupType.toUpperCase(),"");
			CustomParameter specificStatuses = CustomParameter.
					findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_"+type.toUpperCase()
							+"_"+userGroupType.toUpperCase(),"");
			
			if(specificDeviceStatusUserGroupStatuses != null) {
				internalStatuses = Status.
						findStatusContainedIn(specificDeviceStatusUserGroupStatuses.getValue(), locale);
			} else if(specificDeviceUserGroupStatuses != null) {
				internalStatuses = Status.
						findStatusContainedIn(specificDeviceUserGroupStatuses.getValue(), locale);
			} else if(specificStatuses != null) {
				internalStatuses = Status.
						findStatusContainedIn(specificStatuses.getValue(), locale);
			} else if(userGroupType.equals(ApplicationConstants.CHAIRMAN)
					|| userGroupType.equals(ApplicationConstants.SPEAKER)) {
				CustomParameter finalStatus = CustomParameter.
						findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_FINAL","");
				if(finalStatus != null) {
					internalStatuses = Status.
							findStatusContainedIn(finalStatus.getValue(), locale);
				}else{
					CustomParameter recommendStatus = CustomParameter.
							findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
					if(recommendStatus != null){
						internalStatuses = Status.
								findStatusContainedIn(recommendStatus.getValue(), locale);
					}else{
						CustomParameter defaultCustomParameter = CustomParameter.
								findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
						if(defaultCustomParameter != null){
							internalStatuses = Status.
									findStatusContainedIn(defaultCustomParameter.getValue(), locale);
						}else{
							model.addAttribute("errorcode", "question_putup_options_final_notset");
						}		
					}
				}
			}else if((!userGroupType.equals(ApplicationConstants.CHAIRMAN))
					&&(!userGroupType.equals(ApplicationConstants.SPEAKER))){
				CustomParameter recommendStatus = CustomParameter.
						findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_RECOMMEND","");
				if(recommendStatus != null) {
					internalStatuses = Status.findStatusContainedIn(recommendStatus.getValue(), locale);
				}else{
					CustomParameter defaultCustomParameter = CustomParameter.
							findByName(CustomParameter.class,"QUESTION_PUT_UP_OPTIONS_BY_DEFAULT","");
					if(defaultCustomParameter != null) {
						internalStatuses = Status.
								findStatusContainedIn(defaultCustomParameter.getValue(), locale);
					}else{
						model.addAttribute("errorcode", "question_putup_options_final_notset");
					}		
				}
			}	
			model.addAttribute("internalStatuses", internalStatuses);
		}catch (ELSException e) {
			model.addAttribute("error", e.getParameter());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Reference> getReferencedEntityReferences(Question domain,
			String locale) throws ELSException {
		List<Reference> refentities = new ArrayList<Reference>();
		List<ReferenceUnit> referencedEntities = domain.getReferencedEntities();
		if(referencedEntities != null && !referencedEntities.isEmpty()){
			for(ReferenceUnit re : referencedEntities){
				if(re.getDeviceType() != null){
					if(re.getDeviceType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
						Question q = Question.findById(Question.class, re.getDevice());
						if(q != null){
								Reference reference=new Reference();
								/** reference unit id **/
								reference.setId(String.valueOf(re.getId()));
								/** id of referred device **/
								reference.setNumber(String.valueOf(q.getId()));
								/** detailed information of referred device **/
								StringBuffer detail = new StringBuffer();			
								/** session information **/
								detail.append(" (" 
										+ FormaterUtil.formatNumberNoGrouping(q.getSession().getYear(), locale) + ", "
										+ re.getSessionTypeName());
								/** ballot, yaadi and discussion information as per devicetype **/
								if(re.getDeviceType().equals(ApplicationConstants.STARRED_QUESTION)){									
									if(q.getBallotStatus()!=null && q.getBallotStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_BALLOTED)) {
										/** ballot information **/
										detail.append(", ");
										String ballotInformation = Device.findBallotInformationText(q.getType().getType(), q.getId(), locale);
										if(ballotInformation != null && !ballotInformation.isEmpty()){
											detail.append(ballotInformation);
											/** discussion/lapsed information **/
											detail.append(", ");
											if(q.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_SYSTEM_LAPSED)
													|| q.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_LAPSED)) {
												detail.append(q.getRecommendationStatus().getName());
											} else if(q.getRecommendationStatus().getType().equals(ApplicationConstants.QUESTION_PROCESSED_DISCUSSED)) {
												detail.append(q.getRecommendationStatus().getName());
											} else {
												MessageResource notDiscussedMsg = MessageResource.findByFieldName(MessageResource.class, "code", "question.not_discussed", locale);
												if(notDiscussedMsg!=null) {
													detail.append(notDiscussedMsg.getValue());
												} else {
													detail.append("-");
												}
											}
											detail.append(")");
										}else{
											detail.append(", -)");
										}										
									} else {
										detail.append(", -)");
									}									
									
								}else if(q.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)){
									/** yaadi information **/
									detail.append(", ");
									String yaadiLayingStatus = q.findYaadiLayingStatus();
									detail.append(((q.getYaadiNumber() != null) ? FormaterUtil.formatNumberNoGrouping(q.getYaadiNumber(), locale):"-"));
									if(yaadiLayingStatus!=null && (yaadiLayingStatus.equals(ApplicationConstants.YAADISTATUS_READY) || yaadiLayingStatus.equals(ApplicationConstants.YAADISTATUS_LAID))) {
										detail.append(", " + ((q.getYaadiLayingDate() != null)? FormaterUtil.formatDateToString(q.getYaadiLayingDate(), ApplicationConstants.SERVER_DATEFORMAT, locale):"-")
										+ ")");					
									} else {
										detail.append(")");
									}
								}								
								reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(re.getNumber()) + detail.toString());
								/** devicetype of referred device **/
								reference.setState(q.getType().getType());
								/** main status of referred device **/
								reference.setRemark(q.getStatus().getName());
								refentities.add(reference);
						}
					}
				}
			}
		}
		return refentities;
	}

	public static List<Reference> getClubbedEntityReferences(Question domain,
			String locale) {
		List<Reference> references = new ArrayList<Reference>();
		List<ClubbedEntity> clubbedEntities=Question.findClubbedEntitiesByPosition(domain);
		if(clubbedEntities!=null){
			for(ClubbedEntity ce:clubbedEntities){
				Reference reference=new Reference();
				reference.setId(String.valueOf(ce.getId()));
				reference.setName(FormaterUtil.getNumberFormatterNoGrouping(locale).format(ce.getQuestion().getNumber()));
				reference.setNumber(String.valueOf(ce.getQuestion().getId()));
				references.add(reference);
			}
		}
		return references;
	}
	
	
	/*
	 * This method is used to view the approval status of a question from the supporting members
	 */
	@RequestMapping(value="/status/{question}",method=RequestMethod.GET)
	public String getSupportingMemberStatus(final HttpServletRequest request,final ModelMap model,@PathVariable("question") final String question){
		Question questionTemp=Question.findById(Question.class,Long.parseLong(question));
		List<SupportingMember> supportingMembers=questionTemp.getSupportingMembers();
		model.addAttribute("supportingMembers",supportingMembers);
		return "question/supportingmember";
	}
	
	

	@RequestMapping(value="/citations/{deviceType}",method=RequestMethod.GET)
	public String getCitations(final HttpServletRequest request, final Locale locale,@PathVariable("deviceType")  final Long type,
			final ModelMap model){
		DeviceType deviceType=DeviceType.findById(DeviceType.class,type);
		List<Citation> deviceTypeBasedcitations=Citation.findAllByFieldName(Citation.class,"deviceType",deviceType, "text",ApplicationConstants.ASC, locale.toString());
		Status status=null;
		if(request.getParameter("status")!=null){
			status=Status.findById(Status.class, Long.parseLong(request.getParameter("status")));
		}
		List<Citation> citations=new ArrayList<Citation>();
		if(status!=null){
			for(Citation i:deviceTypeBasedcitations){
				if(i.getStatus()!=null){
					if(i.getStatus().equals(status.getType())){
						citations.add(i);
					}
				}
			}
		}
		model.addAttribute("citations",citations);
		return "question/citation";
	}

	@RequestMapping(value="/revisions/{questionId}",method=RequestMethod.GET)
	public String getDrafts(final Locale locale,@PathVariable("questionId")  final Long questionId,
			final ModelMap model){
		List<RevisionHistoryVO> drafts=Question.getRevisions(questionId,locale.toString());
		Question q = Question.findById(Question.class, questionId);
		if(q != null){
			if(q.getType() != null){
				if(q.getType().getType() != null){
					model.addAttribute("selectedDeviceType", q.getType().getType());
				}
			}
		}		
		model.addAttribute("drafts",drafts);
		model.addAttribute("questions_halfhourdiscussion_from_question", ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION);
		return "question/revisions";
	}

	@RequestMapping(value="/members/contacts",method=RequestMethod.GET)
	public String getMemberContacts(final Locale locale,
			final ModelMap model,final HttpServletRequest request){
		String strMembers=request.getParameter("members");
		String[] members=strMembers.split(",");
		List<MemberContactVO> memberContactVOs=Member.getContactDetails(members);
		model.addAttribute("membersContact",memberContactVOs);
		return "question/contacts";
	}

	@RequestMapping(value="/subject/{id}",method=RequestMethod.GET)
	public @ResponseBody MasterVO getSubject(final HttpServletRequest request,final ModelMap model,
			final @PathVariable("id")Long id){
		Question question=Question.findById(Question.class, id);
		MasterVO masterVO=new MasterVO();
		masterVO.setId(question.getId());
		if(question.getRevisedSubject()!=null){
			masterVO.setName(question.getRevisedSubject());
		}else{
			masterVO.setName(question.getSubject());
		}
		return masterVO;
	}

	public static Integer stringToIntegerItemCount(
			HttpServletRequest request, String string) throws ELSException {
		Integer itemCount = null;
		String selectedItemCount = request.getParameter("itemscount");
		if(selectedItemCount != null && !selectedItemCount.isEmpty()) {
			itemCount = Integer.parseInt(selectedItemCount);
			return itemCount;
		}else{
			throw new ELSException("QuestionController.stringToIntegerItemCount/2", 
					"items Count  is not set in request "); 
		}
	}
	
	/**** Yaadi to discuss update ****/
	@RequestMapping(value="/yaaditodiscussupdate/assistant/init", method=RequestMethod.GET)
	public String getYaadiToDiscussInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "question/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("questionType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strGroup = request.getParameter("group");
		String strAnsweringDate = request.getParameter("answeringDate");

		/**** Locale ****/
		String strLocale = locale.toString();

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strGroup != null && !(strGroup.isEmpty())
				&& strAnsweringDate != null && !(strAnsweringDate.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));

			/**** Decision Status Available To Assistant(At this stage) 
			 * QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + HOUSE_TYPE + USERGROUP_TYPE ****/
			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "QUESTION_YAADI_UPDATE_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + strUsergroupType.toUpperCase(), "");

			List<Status> internalStatuses;
			try {
				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
				model.addAttribute("internalStatuses", internalStatuses);
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("questionType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("group", strGroup);
			model.addAttribute("answeringDate", strAnsweringDate);

			retVal = "question/yaaditoduscussupdateinit";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	
	/**** Yaadi to discuss update ****/
	@RequestMapping(value="/yaadiQuestionContentUpdate/assistant/init", method=RequestMethod.GET)
	public String getYaadiQuestionContentUpdateInit(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		/**** Request Params ****/
		String retVal = "question/error";
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("questionType");			
		String strStatus = request.getParameter("status");
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strGroup = request.getParameter("group");
		String strAnsweringDate = request.getParameter("answeringDate");

		/**** Locale ****/
		String strLocale = locale.toString();

		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strGroup != null && !(strGroup.isEmpty())
				&& strAnsweringDate != null && !(strAnsweringDate.isEmpty())
				&& strStatus != null && !(strStatus.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, strLocale);
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));

			/**** Decision Status Available To Assistant(At this stage) 
			 * QUESTION_PUT_UP_OPTIONS_ + QUESTION_TYPE + HOUSE_TYPE + USERGROUP_TYPE ****/
//			CustomParameter defaultStatus = CustomParameter.findByName(CustomParameter.class, "QUESTION_YAADI_UPDATE_" + deviceType.getType().toUpperCase() + "_" + houseType.getType().toUpperCase() + "_" + strUsergroupType.toUpperCase(), "");
//
//			List<Status> internalStatuses;
//			try {
//				internalStatuses = Status.findStatusContainedIn(defaultStatus.getValue(),locale.toString());
//				model.addAttribute("internalStatuses", internalStatuses);
//			} catch (ELSException e) {
//				model.addAttribute("error", e.getParameter());
//			}
			/**** Request Params To Model Attribute ****/
			model.addAttribute("houseType", strHouseType);
			model.addAttribute("sessionType", strSessionType);
			model.addAttribute("sessionYear", strSessionYear);
			model.addAttribute("questionType", strDeviceType);
			model.addAttribute("status", strStatus);
			model.addAttribute("role", strRole);
			model.addAttribute("usergroup", strUsergroup);
			model.addAttribute("usergroupType", strUsergroupType);
			model.addAttribute("group", strGroup);
			model.addAttribute("answeringDate", strAnsweringDate);

			retVal = "question/yaadiQuestionContentUpdateinit";
		}else{
			model.addAttribute("errorcode","CAN_NOT_INITIATE");
		}

		return retVal;
	}
	
	@RequestMapping(value="/yaaditodiscussupdate/assistant/view", method=RequestMethod.GET)
	public String getYaadiToDiscussUpdateAssistantView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.getYaadiToDiscussUpdateQuestions(model, request, locale.toString());
		return "question/yaaditodiscussupdateview";
	}
	
	@RequestMapping(value="/yaaditoupdateContent/assistant/view", method=RequestMethod.GET)
	public String getYaadiToUpdateContentAssistantView(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		this.getYaadiToDiscussUpdateQuestions(model, request, locale.toString());
		return "question/yaaditoupdateContentview";
	}
	
	@Transactional
	@RequestMapping(value="/yaaditodiscussupdate/assistant/update", method=RequestMethod.POST)
	public String yaadiToDiscussUpdateAssistant(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		boolean updated = false;
		String page = "question/error";
		StringBuffer success = new StringBuffer();
		
		try{
			String[] selectedItems = request.getParameterValues("items[]");
			String strDecisionStatus = request.getParameter("decisionStatus");
			String strStatus = request.getParameter("status");
			String strDiscussionDate = request.getParameter("discussionDate");
			String strRemark = request.getParameter("remark");
			
			if(selectedItems != null && selectedItems.length > 0
					&& strDecisionStatus != null && !strDecisionStatus.isEmpty()
					&& strStatus != null && !strStatus.isEmpty()) {
				/**** As It Is Condition ****/
				if(!strStatus.equals("-")) {
					for(String i : selectedItems) {
						Long id = Long.parseLong(i);
						Question question = Question.findById(Question.class, id);
						Status discussed = Status.findById(Status.class, new Long(strDecisionStatus));
						question.setRecommendationStatus(discussed);
						if(strDiscussionDate != null && !strDiscussionDate.isEmpty()){
							question.setDiscussionDate(FormaterUtil.formatStringToDate(strDiscussionDate, ApplicationConstants.SERVER_DATEFORMAT));
						}
						if(strRemark != null && !strRemark.isEmpty()){
							question.setRemarks(strRemark);
						}
						question.merge();
						updated = true;
						success.append(FormaterUtil.formatNumberNoGrouping(question.getNumber(), question.getLocale())+",");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			this.getYaadiToDiscussUpdateQuestions(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "question/yaaditodiscussupdateview";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	
	@Transactional
	@RequestMapping(value="/yaaditoUpdateQsnContent/assistant/update", method=RequestMethod.POST) 	
	public String yaaditoUpdateQsnContent(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		
		boolean updated = false;
		String page = "question/error";
		StringBuffer success = new StringBuffer();
		
		try{
			
			String selectedItemsLength = (String)request.getParameter("itemsLength");
			int number  = Integer.parseInt(selectedItemsLength);
			
			ArrayList<HashMap<String,String>>  questionContent= new ArrayList<HashMap<String,String>>();
			
			for (int i=0;i<number;i++)
			{
				HashMap<String,String> ymap = new HashMap<String,String>();
				String  questionId = request.getParameter("items["+i+"][questionId]");
				ymap.put("questionId", questionId);
				String subject  = request.getParameter("items["+i+"][subject]");
				ymap.put("subject", subject);
				String RevisedText  = request.getParameter("items["+i+"][revisedQuestionText]");
				ymap.put("revisedQuestionText", RevisedText);
				String answer = request.getParameter("items["+i+"][answer]");
				ymap.put("answer", answer);
				questionContent.add(ymap);
			}
			
			if(questionContent != null )
			{
				for(HashMap<String,String>  i : questionContent)
				{
					Long id = Long.parseLong(i.get("questionId"));
					Question question = Question.findById(Question.class, id);
					
					question.setRevisedSubject(i.get("subject"));					
					question.setRevisedQuestionText(i.get("revisedQuestionText"));
					question.setAnswer(i.get("answer"));
					question.merge();
					updated = true;
					success.append(FormaterUtil.formatNumberNoGrouping(question.getNumber(), question.getLocale())+",");
					
				}
			}
			
			

		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			this.getYaadiToDiscussUpdateQuestions(model, request, locale.toString());
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "question/yaaditoupdateContentview";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	private void getYaadiToDiscussUpdateQuestions(final ModelMap model,
	final HttpServletRequest request, 
	final String locale) {
		/**** Request Params ****/
		String strHouseType = request.getParameter("houseType");
		String strSessionType = request.getParameter("sessionType");
		String strSessionYear = request.getParameter("sessionYear");
		String strDeviceType = request.getParameter("questionType");			
		String strRole = request.getParameter("role");
		String strUsergroup = request.getParameter("usergroup");
		String strUsergroupType = request.getParameter("usergroupType");
		String strGroup = request.getParameter("group");
		String strAnsweringDate = request.getParameter("answeringDate");
		
		
		if(strHouseType != null && !(strHouseType.isEmpty())
				&& strSessionType != null && !(strSessionType.isEmpty())
				&& strSessionYear != null && !(strSessionYear.isEmpty())
				&& strDeviceType != null && !(strDeviceType.isEmpty())
				&& strGroup != null && !(strGroup.isEmpty())
				&& strAnsweringDate != null && !(strAnsweringDate.isEmpty())
				&& strRole != null && !(strRole.isEmpty())
				&& strUsergroup != null && !(strUsergroup.isEmpty())
				&& strUsergroupType != null && !(strUsergroupType.isEmpty())) {
			List<Question> questions = new ArrayList<Question>();
		
			HouseType houseType = HouseType.findByFieldName(HouseType.class, "type", strHouseType, locale);
			SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(strSessionType));
			Integer sessionYear = Integer.parseInt(strSessionYear);
			Session session;
			try {
				session = Session.findSessionByHouseTypeSessionTypeYear(houseType,sessionType, sessionYear);				
				DeviceType deviceType = DeviceType.findById(DeviceType.class, 
						Long.parseLong(strDeviceType));
				Group group=null;
				if(strGroup!=null && strGroup !=""){
					group=Group.findById(Group.class, Long.parseLong(strGroup));
				}				
				
				QuestionDates qd = QuestionDates.findById(QuestionDates.class, Long.parseLong(strAnsweringDate));
				Date answeringDate = qd.getAnsweringDate();				
				Map<String, String[]> parametersMap = new HashMap<String, String[]>();
				parametersMap.put("locale", new String[]{locale.toString()});
				parametersMap.put("sessionId", new String[]{session.getId().toString()});
				parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
				if(group!=null && group.getId()!=null) {
					parametersMap.put("groupId", new String[]{group.getId().toString()});
				} else {
					parametersMap.put("groupId", new String[]{"0"});
				}				
				parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
				List ballotVOs = org.mkcl.els.domain.Query.findReport("YADI_BALLOT_VIEW", parametersMap);
				if(ballotVOs!=null && !ballotVOs.isEmpty()) {
					for(Object o: ballotVOs) {
						Object[] device = (Object[]) o;
						if(device!=null) {
							Question q = Question.findById(Question.class, Long.parseLong(device[1].toString()));
							if(q!=null) {
								questions.add(q);
							}
						}
					}
				}
				
				//Uncomment for enabling shuffling for  questions in yaadi
				//questions= this.shuffleArrayWithoutSamePosition(questions);
				
				StringBuilder str = new StringBuilder();		
				if(questions != null && ! questions.isEmpty()) {
					for(Question q : questions) {
						str.append(q.getId()+",");
					}
					model.addAttribute("questions", questions);
					model.addAttribute("questionIds", str);
				}
			} catch (ELSException e) {
				model.addAttribute("error", e.getParameter());
				e.printStackTrace();
			}
		}
	}
	
	
    private  List<Question> shuffleArrayWithoutSamePosition(List<Question> arr) {
        Random rand = new Random();
        List<Question> shuffledQuestion = new ArrayList<Question>();
        for(int i=0;i<arr.size();i++)
        {
        	shuffledQuestion.add(i,arr.get(i));
        }

        // Shuffle the array using the Fisher-Yates algorithm
        for (int i = shuffledQuestion.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Question temp = shuffledQuestion.get(i);
            shuffledQuestion.set(i, shuffledQuestion.get(j));       
            shuffledQuestion.set(j, temp);          
        }

        // Check if any element is in the same position as the original array
        for (int i = 0; i < arr.size(); i++) {
            if (shuffledQuestion.get(i).equals(arr.get(i))) {
                // Reshuffle the array again if any element is in the same position
                return shuffleArrayWithoutSamePosition(arr);
            }
        }

        return shuffledQuestion;
    }

	
	@RequestMapping(value="/yaadidiscussiondate", method=RequestMethod.GET)
	public String getYaadiToDiscussino(final ModelMap model,
			final HttpServletRequest request,
			final Locale locale) {
		return "question/yaadidiscussiondate";
	}
	
	@RequestMapping(value="/questionFormatEdit" , method=RequestMethod.GET)
	public String getFormatEdit(final ModelMap model,final HttpServletRequest request, final Locale locale)
	{
		try {
			List<HouseType> housetypes = HouseType.findAll(HouseType.class, "name", ApplicationConstants.ASC, locale.toString());
			List<SessionType> sessionTypes = 
					SessionType.findAll(SessionType.class, "sessionType", ApplicationConstants.ASC, locale.toString());
			List<DeviceType> devices = DeviceType.findAll(DeviceType.class, "name", ApplicationConstants.ASC, locale.toString());
			model.addAttribute("housetypes", housetypes);
			model.addAttribute("sessionTypes", sessionTypes);
			model.addAttribute("devices", devices);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "questionformat/formatEdit";
	}
	
	
	@RequestMapping(value="/questionFormatView" , method=RequestMethod.GET)
	public String getQsnViewPage(final ModelMap model,final HttpServletRequest request, final Locale locale)
	{
		this.getQsnView(model, request, locale);
		return "questionformat/viewQuestion";
	}
	
	
	public void getQsnView(final ModelMap model,final HttpServletRequest request, final Locale locale)
	{
		String strHouseType = request.getParameter("houseType");
		String strDeviceType = request.getParameter("deviceType");
		String strqsnId = request.getParameter("qsnId");
		Integer qsnId =  Integer.parseInt(strqsnId.trim());
		
		
		if(strHouseType != null && !(strHouseType.isEmpty())
			&& 	strDeviceType!= null && !(strDeviceType.isEmpty())
			&&  qsnId != null 
				) {
			HouseType houseType = HouseType.findByName(strHouseType, locale.toString());
			
			DeviceType deviceType = DeviceType.findById(DeviceType.class, Long.parseLong(strDeviceType));
			
			try {
				
				Session latestSession = Session.findLatestSession(houseType);
				House currentHouse = House.findCurrentHouse(locale.toString());
				List<Question> qsnDetails = new ArrayList<Question>();
				
				qsnDetails = Question.getQuestionDetail(latestSession,qsnId,deviceType.getId(),latestSession.getHouse().getId(),currentHouse.getFirstDate());
				if (qsnDetails.size() == 0) {
					throw new ELSException("Unable to Find Question Please Check the Number ", strqsnId);
				}
				String[] Pactor = new String[(qsnDetails.size())];
				boolean parentSet=false;
				
				if (qsnDetails.get(0).getParent() == null) {
					List<Question> childQsnDetails = Question.getChildQuestionDetail(latestSession,
							qsnDetails.get(0).getId(), deviceType.getId(), latestSession.getHouse().getId(),
							currentHouse.getFirstDate());
					model.addAttribute("childQuestions", childQsnDetails);

				}
				else if (qsnDetails.get(0).getParent() != null) {
					qsnDetails = Question.getQuestionDetail(latestSession, qsnDetails.get(0).getParent().getNumber(),
							deviceType.getId(), latestSession.getHouse().getId(), currentHouse.getFirstDate());
					model.addAttribute("questions", qsnDetails);
					List<Question> childQsnDetails = Question.getChildQuestionDetail(latestSession,
							qsnDetails.get(0).getId(), deviceType.getId(), latestSession.getHouse().getId(),
							currentHouse.getFirstDate());
					model.addAttribute("childQuestions", childQsnDetails);
					for (int i = 0; i < qsnDetails.size(); i++) {
						if (qsnDetails.get(i).getActor() != null) {
							Pactor = qsnDetails.get(i).getActor().split("#");
							model.addAttribute("actor", Pactor[0]);
						}
					}
					parentSet = true;
				}
				
				if (parentSet == false) {
					for (int i = 0; i < qsnDetails.size(); i++) {
						if (qsnDetails.get(i).getActor() != null) {
							Pactor = qsnDetails.get(i).getActor().split("#");
							model.addAttribute("actor", Pactor[0]);
						}
					}
					model.addAttribute("questions", qsnDetails);
				}
			
			} catch(ELSException elsx) {
				elsx.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	@RequestMapping(value="/questionFormatUpdate" , method=RequestMethod.POST)
	public String updateQsnAdmin(final ModelMap model,final HttpServletRequest request, final Locale locale)
	{

		boolean updated = false;
		String page = "question/error";
		StringBuffer success = new StringBuffer();
		
		try{
			
			String selectedItemsLength = (String)request.getParameter("itemsLength");
			int number  = Integer.parseInt(selectedItemsLength);
			
			ArrayList<HashMap<String,String>>  questionContent= new ArrayList<HashMap<String,String>>();
			
			for (int i=0;i<number;i++)
			{
				HashMap<String,String> ymap = new HashMap<String,String>();
				String  questionId = request.getParameter("items["+i+"][questionId]");
				ymap.put("questionId", questionId);
				String questionText  = request.getParameter("items["+i+"][questionText]");
				ymap.put("questionText", questionText);
				String RevisedText  = request.getParameter("items["+i+"][revisedQuestionText]");
				ymap.put("revisedQuestionText", RevisedText);
				String answer = request.getParameter("items["+i+"][answer]");
				ymap.put("answer", answer);
				questionContent.add(ymap);
			}
			
			if(questionContent != null )
			{
				for(HashMap<String,String>  i : questionContent)
				{
					Long id = Long.parseLong(i.get("questionId"));
					Question question = Question.findById(Question.class, id);
					
					question.setQuestionText(HtmlUtils.htmlUnescape(i.get("questionText")));					
					question.setRevisedQuestionText(HtmlUtils.htmlUnescape(i.get("revisedQuestionText")));
					question.setAnswer(HtmlUtils.htmlUnescape(i.get("answer")));
					question.simpleMerge();
					updated = true;
					success.append(FormaterUtil.formatNumberNoGrouping(question.getNumber(), question.getLocale())+",");
					WorkflowDetails workflowDetails = 
							WorkflowDetails.findCurrentWorkflowDetail(question);
					if(workflowDetails != null) {
					workflowDetails.setText(i.get("revisedQuestionText"));
					workflowDetails.merge();
					}
				}
			}
			
			

		}catch(Exception e){
			e.printStackTrace();
			updated = false;
		}
		
		if(updated){
			this.getQsnView(model, request, locale);
			success.append(" updated successfully...");
			model.addAttribute("success", success.toString());
			page = "questionformat/viewQuestion";
		}else{
			model.addAttribute("failure", "update failed.");
		}
		
		return page;
	}
	
	@RequestMapping(value="/questionStatusUpdate" , method=RequestMethod.POST)
	@ResponseBody
	public String updateStarredQsnStatus(final ModelMap model,final HttpServletRequest request, final Locale locale)
	{
		boolean updated = false;
		//String page = "question/error";
		StringBuffer success = new StringBuffer();
		
		try {
			String[] selectedQsn = request.getParameterValues("selectedQuestionId[]");
			String deviceType = (String)request.getParameter("deviceType");
			Integer arrLength = selectedQsn.length;
			DeviceType d = DeviceType.findById(DeviceType.class,Long.parseLong(deviceType));
			if(arrLength != null)
			{
				for(String q: selectedQsn)
				{
					Long qId = Long.parseLong(q);
					Question questiond = Question.findById(Question.class, qId);
					if(questiond != null )
					{
						
							List<Question> childQsns = Question.getChildQuestionDetail(questiond.getSession(), questiond.getId(),questiond.getType().getId(),questiond.getHouseType().getId(), questiond.getSession().getStartDate());
							if(childQsns.size() > 0) {
							for(Question qd : childQsns)
							{
								qd.setInternalStatus(Question.findCorrespondingStatusForGivenQuestionType(questiond.getInternalStatus(), d));			
								qd.setStatus(Question.findCorrespondingStatusForGivenQuestionType(questiond.getStatus(), d));
								qd.setRecommendationStatus(Question.findCorrespondingStatusForGivenQuestionType(questiond.getRecommendationStatus(), d));
								qd.setType(d);
							}}
							questiond.setInternalStatus(Question.findCorrespondingStatusForGivenQuestionType(questiond.getInternalStatus(), d));			
							questiond.setStatus(Question.findCorrespondingStatusForGivenQuestionType(questiond.getStatus(), d));
							questiond.setRecommendationStatus(Question.findCorrespondingStatusForGivenQuestionType(questiond.getRecommendationStatus(), d));
							questiond.setType(d);
						
						questiond.merge();
						
						success = success.append(questiond.getNumber().toString());
						updated = true;
						
					}
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		if(updated)
		{
			return success.toString() ;
		}
		
		return "Failed" ;
	}
	
	/**** Used in bulk approval of supporting members to fetch question details ****/
	@RequestMapping(value="/{id}/details", method=RequestMethod.GET)
	public String getDetails(@PathVariable("id")final Long id,
			final ModelMap model){
		Question question = Question.findById(Question.class, id);
		model.addAttribute("details", question.getQuestionText());
		return "question/details";
	}	
	
	@Transactional
	@RequestMapping(value = "/getNumberToLapse", method = RequestMethod.GET)
	public @ResponseBody String getNumberToLapse(final HttpServletRequest request, final Locale locale) throws ELSException {
		String strdeviceType = request.getParameter("deviceType");
		String[]  strMemberIds = request.getParameterValues("memberIds");
		String strhouseType = request.getParameter("houseType");
		String strHouseDate = request.getParameter("latestAssemblyHouseFormationDate");
		
		List<Long> qsnToBeLapsed = new ArrayList<Long>();
		StringBuilder questionNumbers = new StringBuilder();
		
		if(strdeviceType != null && !strdeviceType.isEmpty() &&
				strMemberIds !=null  &&
				strhouseType !=null && !strhouseType.isEmpty()) {
			
			HouseType ht = HouseType.findByName(strhouseType, locale.toString());
			Session s  = Session.findLatestSession(ht);
			DeviceType dt = DeviceType.findById(DeviceType.class, Long.parseLong(strdeviceType));
			
			String qMNames = null;
			
			List<Question> QsnNumbers = null;
			
			if(ht != null && s != null && dt != null) {
				 QsnNumbers = 
						Question.getAllQuestionToBeLapsed(s.getHouse().getId(),strHouseDate,dt.getId(),strMemberIds); 
			}
			
			if (QsnNumbers != null) {
				for (Question q : QsnNumbers) {
					qMNames = q.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
					if (qMNames.isEmpty()) {
						qsnToBeLapsed.add(q.getId());
						questionNumbers.append(q.getId().toString() + ",");
					if (q.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
							if ( q.getBallotStatus() == null) {
								
								Status status = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_LAPSED, locale.toString());
								
								WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(q);
								if (wd != null) {
									if (wd.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
										wd.setStatus(ApplicationConstants.MYTASK_LAPSED);
										wd.merge();
									}
								}
								
								q.setLocalizedActorName("");
								q.setInternalStatus(status);
								q.setStatus(status);
								q.setRecommendationStatus(status);


								q.simpleMerge();
							}
						} else if (q.getType().getType().equals(ApplicationConstants.UNSTARRED_QUESTION)) {
							if (!q.getRecommendationStatus().getType().equals(ApplicationConstants.YAADISTATUS_LAID)) {
								
								Status status = Status.findByType(ApplicationConstants.QUESTION_UNSTARRED_PROCESSED_LAPSED, locale.toString());
								
								WorkflowDetails wd = WorkflowDetails.findCurrentWorkflowDetail(q);
								if (wd != null) {
									if (wd.getStatus().equals(ApplicationConstants.MYTASK_PENDING)) {
										wd.setStatus(ApplicationConstants.MYTASK_LAPSED);
										wd.merge();
									}
								}
								
								q.setLocalizedActorName("");
								q.setInternalStatus(status);
								q.setStatus(status);
								q.setRecommendationStatus(status);

								
								q.simpleMerge();
							}
						}
					}
				}				
			}			
		}		
		return questionNumbers.toString();
		
	}
	
	/**** Utility for shifting chart answering date ****/
	@Transactional
	@RequestMapping(value="questionId/{id}", method=RequestMethod.GET)
	public String getChartAnsweringDate(@PathVariable("id")final Long id, 
			final ModelMap model, final HttpServletRequest request){
		Question question = Question.findById(Question.class, id);
		model.addAttribute("formattedDeviceType",question.getType().getName());
		model.addAttribute("questionId",question.getId());
		model.addAttribute("questionNumber",question.getNumber());
		model.addAttribute("formattedQsNumber", FormaterUtil.formatNumberNoGrouping(question.getNumber(), "mr_IN"));
		model.addAttribute("subject",question.getSubject());
        model.addAttribute("groupNumber", FormaterUtil.getNumberFormatterNoGrouping("mr_IN").
				format(question.getGroup().getNumber()));   
        CustomParameter dateTimeFormat=CustomParameter.findByName(CustomParameter.class,"SERVER_DATETIMEFORMAT", "");
        model.addAttribute("formattedChartAnsweringDate",FormaterUtil.getDateFormatter(dateTimeFormat.getValue(),"mr_IN").format(question.getChartAnsweringDate().getAnsweringDate()));
		model.addAttribute("chartAnsweringDate",question.getChartAnsweringDate().getAnsweringDate());
		model.addAttribute("strusergroupType",request.getParameter("strusergroupType"));
		Group group = Group.findById(Group.class, question.getGroup().getId());
		List<QuestionDates> questionDates = group.getQuestionDates();
		List<MasterVO> masterVOs = new ArrayList<MasterVO>();
		int count = 1;
		for(QuestionDates questionDate : questionDates) {
			String strAnsweringDate = 
					FormaterUtil.getDateFormatter("mr_IN").format(questionDate.getAnsweringDate());
			MasterVO masterVO = new MasterVO(questionDate.getId(), strAnsweringDate);
			masterVO.setValue(FormaterUtil.formatDateToString(questionDate.getAnsweringDate(), ApplicationConstants.DB_DATEFORMAT));
			masterVO.setSessionDate(questionDate.getAnsweringDate());
			masterVOs.add(masterVO);
		}		
		for(MasterVO i : masterVOs) {
			if(i.getSessionDate().equals(question.getChartAnsweringDate().getAnsweringDate())) {
			    masterVOs.subList(0, count).clear();
			    break;
			}
			count++;
		}
		
		model.addAttribute("questionDates",masterVOs);
				
		return "question/charts/chartdetails";
	}

	   @Transactional
	   @RequestMapping(value = "/updatedChart/{chartId}/questionId/{qId}", method = RequestMethod.POST)
	    public @ResponseBody Boolean changeChartAnsweringDate(@PathVariable("qId") final String qId, @PathVariable("chartId") final String chartId,final ModelMap model, final HttpServletRequest request, final Locale locale) {
		 
		   boolean chartUpdated = false;
		   AuthUser authUser = this.getCurrentUser();
		   Question question = Question.findById(Question.class, Long.parseLong(qId));
		   
		   if(question.getType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
			   chartUpdated = StarredQuestionController.changeChartAnsweringDate(qId, chartId, model, request, authUser, locale);
		   }
		   
		  return chartUpdated;
	    }
	   
	   	@RequestMapping(value="/getSubdepartmentChangeCount", method=RequestMethod.GET)
		public String getSubdepartmentChangeCountPrompt (final ModelMap model,
				final HttpServletRequest request,
				final Locale locale) {
			
			String sessionId = request.getParameter("sessionId");
			model.addAttribute("sessionId",sessionId);
			
			String deviceType = request.getParameter("deviceType");
			model.addAttribute("deviceType",deviceType);
			
//			String isEqualToGroup = request.getParameter("isEqualToGroup"); 
//			model.addAttribute("isEqualToGroup",isEqualToGroup);
			
			
			return "question/changeSubdepartmentReportPrompt";
		}
	   	
	   	
}