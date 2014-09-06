package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.MotionSearchVO;
import org.mkcl.els.common.vo.QuestionSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.ResolutionSearchVO;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.Act;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.Motion;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.ReferencedEntity;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/refentity")
public class ReferencedEntityController {

	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getReferencing(final HttpServletRequest request,
			final ModelMap model){
		String strDeviceId=request.getParameter("id");
		String strDeviceType = request.getParameter("deviceType");
		String strUsergroupType = request.getParameter("usergroupType");
		String strHouseType = request.getParameter("houseType");
		
		try{
			model.addAttribute("houseType", strHouseType);
			if(strDeviceId!=null && strUsergroupType!=null && strDeviceType!=null /*&& strHouseType != null*/){
				if(!strDeviceId.isEmpty()){
					model.addAttribute("deviceType", strDeviceType);

					if(strDeviceType.startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
						model.addAttribute("whichDevice", ApplicationConstants.DEVICE_QUESTIONS);
					}else if(strDeviceType.startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
						model.addAttribute("whichDevice", ApplicationConstants.DEVICE_RESOLUTIONS);
					}else if(strDeviceType.startsWith(ApplicationConstants.DEVICE_BILLS)){
						model.addAttribute("whichDevice", ApplicationConstants.DEVICE_BILLS);
					}else if(strDeviceType.startsWith(ApplicationConstants.DEVICE_MOTIONS)){
						model.addAttribute("whichDevice", ApplicationConstants.DEVICE_MOTIONS);
					}
					
					if(strUsergroupType != null){
						if(!strUsergroupType.isEmpty()){
							model.addAttribute("usergroupType", strUsergroupType);
						}
					}
					if(strDeviceType != null){
						if(!strDeviceType.isEmpty()){							
							if(strDeviceType.startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
								Question question=Question.findById(Question.class,Long.parseLong(strDeviceId));
								int year=question.getSession().getYear();
								CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
								List<Reference> years=new ArrayList<Reference>();
								if(houseFormationYear!=null){
									Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
									for(int j=year;j>=formationYear;j--){
										Reference yearReference=new Reference(String.valueOf(j),FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(j));
										years.add(yearReference);
									}
								}else{
									model.addAttribute("flag", "houseformationyearnotset");
									return "referencing/error";
								}
								model.addAttribute("years",years);
								model.addAttribute("sessionYear",year);
								List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType",ApplicationConstants.ASC, question.getLocale());
								model.addAttribute("sessionTypes",sessionTypes);
								model.addAttribute("sessionType", question.getSession().getType().getId());
								
								if(question.getRevisedSubject()!=null){
									if(!question.getRevisedSubject().isEmpty()){
										model.addAttribute("subject",question.getRevisedSubject());
									}else{
										model.addAttribute("subject",question.getSubject());
									}
								}else{
									model.addAttribute("subject",question.getSubject());
								}
								
								List<ReferencedEntity> referencedEntities= new ArrayList<ReferencedEntity>();
								if(question.getReferencedEntities() != null){
									referencedEntities = question.getReferencedEntities();
								}
								List<Reference> references=new ArrayList<Reference>();
								StringBuffer buffer=new StringBuffer();
								if(!referencedEntities.isEmpty()){
									for(ReferencedEntity i:referencedEntities){
										
										Reference reference=new Reference();
										reference.setId(String.valueOf(i.getId()));
										reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(((Question)i.getDevice()).getNumber())));
										buffer.append(i.getId()+",");
										references.add(reference);
										
										model.addAttribute("referencedEntities",references);
										model.addAttribute("referencedEntitiesIds",buffer.toString());
									}
								}
								model.addAttribute("id",Long.parseLong(strDeviceId));
								model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(question.getLocale()).format(question.getNumber()));							
							}else if(strDeviceType.startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
								
								if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
									Resolution resolution=Resolution.findById(Resolution.class,Long.parseLong(strDeviceId));
									//===================
																									
									int year=resolution.getSession().getYear();
									CustomParameter houseFormationYear=CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
									List<Reference> years=new ArrayList<Reference>();
									if(houseFormationYear!=null){
										Integer formationYear=Integer.parseInt(houseFormationYear.getValue());
										for(int j=year;j>=formationYear;j--){
											Reference yearReference=new Reference(String.valueOf(j),FormaterUtil.getNumberFormatterNoGrouping(resolution.getLocale()).format(j));
											years.add(yearReference);
										}
									}else{
										model.addAttribute("flag", "houseformationyearnotset");
										return "referencing/error";
									}
									model.addAttribute("years",years);
									model.addAttribute("sessionYear",year);
									List<SessionType> sessionTypes=SessionType.findAll(SessionType.class,"sessionType",ApplicationConstants.ASC, resolution.getLocale());
									model.addAttribute("sessionTypes",sessionTypes);
									model.addAttribute("sessionType", resolution.getSession().getType().getId());
																
									//===================
									if(resolution.getRevisedNoticeContent()!=null){
										if(!resolution.getRevisedNoticeContent().isEmpty()){
											model.addAttribute("noticeContent",resolution.getRevisedNoticeContent());
										}else{
											model.addAttribute("noticeContent",resolution.getNoticeContent());
										}
									}else{
										model.addAttribute("noticeContent",resolution.getNoticeContent());
									}
									
									List<ReferencedEntity> referencedEntities= new ArrayList<ReferencedEntity>();
									if(resolution.getReferencedResolution() != null){
										referencedEntities.add(resolution.getReferencedResolution());
									}
									List<Reference> references=new ArrayList<Reference>();
									StringBuffer buffer=new StringBuffer();
									if(!referencedEntities.isEmpty()){
										for(ReferencedEntity i:referencedEntities){
											
											Reference reference=new Reference();
											reference.setId(String.valueOf(i.getId()));
											reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(resolution.getLocale()).format(((Resolution)i.getDevice()).getNumber())));
											buffer.append(i.getId()+",");
											references.add(reference);
											
											model.addAttribute("referencedEntities",references);
											model.addAttribute("referencedEntitiesIds",buffer.toString());
										}
									}
									model.addAttribute("id",Long.parseLong(strDeviceId));
									model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(resolution.getLocale()).format(resolution.getNumber()));
								}
							}else if(strDeviceType.startsWith(ApplicationConstants.DEVICE_BILLS)){
								
								if(strDeviceType.equals(ApplicationConstants.NONOFFICIAL_BILL)){
									Bill bill=Bill.findById(Bill.class,Long.parseLong(strDeviceId));
									if(bill.getDefaultTitle()!=null){
										if(!bill.getDefaultTitle().isEmpty()){
											model.addAttribute("title",bill.getDefaultTitle());
										}else{
											model.addAttribute("title",bill.getDefaultTitle());
										}
									}else{
										model.addAttribute("title",bill.getDefaultTitle());
									}
									String languagesAllowedInSession = bill.getSession().getParameter(strDeviceType + "_languagesAllowed");
									if(languagesAllowedInSession != null && !languagesAllowedInSession.isEmpty()) {
										List<Language> languagesAllowedForBill = new ArrayList<Language>();
										for(String languageAllowedInSession: languagesAllowedInSession.split("#")) {
											Language languageAllowed = Language.findByFieldName(Language.class, "type", languageAllowedInSession, bill.getLocale());
											languagesAllowedForBill.add(languageAllowed);
										}
										model.addAttribute("languagesAllowedForBill", languagesAllowedForBill);
									}
									List<ReferencedEntity> referencedEntities= new ArrayList<ReferencedEntity>();
									if(bill.getReferencedBill() != null){
										referencedEntities.add(bill.getReferencedBill());
									}
									List<Reference> references=new ArrayList<Reference>();
									StringBuffer buffer=new StringBuffer();
									if(!referencedEntities.isEmpty()){
										for(ReferencedEntity i:referencedEntities){
											
											Reference reference=new Reference();
											reference.setId(String.valueOf(i.getId()));
											/** for act reference, device type is not set **/
											if(i.getDeviceType()!=null) {
												reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(((Bill)i.getDevice()).getNumber())));
											} else {
												reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(((Act)i.getDevice()).getNumber())));
											}											
											buffer.append(i.getId()+",");
											references.add(reference);
											
											model.addAttribute("referencedEntities",references);
											model.addAttribute("referencedEntitiesIds",buffer.toString());
										}
									}
									model.addAttribute("id",Long.parseLong(strDeviceId));
									if(bill.getNumber()!=null) {
										model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getNumber()));
									}	
									/** Exact Match For Referencing without search **/
									List<BillSearchVO> billSearchVOs=new ArrayList<BillSearchVO>();
									String language = bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
							        billSearchVOs=ReferencedEntity.exactSearchReferencingBill(bill, language, 0, 10, bill.getLocale());
							        model.addAttribute("exactReferences", billSearchVOs);
								}
							}else if(strDeviceType.startsWith(ApplicationConstants.DEVICE_MOTIONS)){
								Motion motion = Motion.findById(Motion.class, Long.parseLong(strDeviceId));
								int year = motion.getSession().getYear();
								CustomParameter houseFormationYear = CustomParameter.findByName(CustomParameter.class, "HOUSE_FORMATION_YEAR", "");
								List<Reference> years = new ArrayList<Reference>();
								if(houseFormationYear != null){
									Integer formationYear = Integer.parseInt(houseFormationYear.getValue());
									for(int j = year; j >= formationYear; j--){
										Reference yearReference = new Reference(String.valueOf(j),FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(j));
										years.add(yearReference);
									}
								}else{
									model.addAttribute("flag", "houseformationyearnotset");
									return "referencing/error";
								}
								model.addAttribute("years",years);
								model.addAttribute("sessionYear",year);
								List<SessionType> sessionTypes = SessionType.findAll(SessionType.class,"sessionType",ApplicationConstants.ASC, motion.getLocale());
								model.addAttribute("sessionTypes",sessionTypes);
								model.addAttribute("sessionType", motion.getSession().getType().getId());
								model.addAttribute("refSession", motion.getSession().getId());
								model.addAttribute("refHouseType", motion.getSession().getHouse().getType().getType());
								
								if(motion.getRevisedSubject() != null){
									if(!motion.getRevisedSubject().isEmpty()){
										model.addAttribute("subject", motion.getRevisedSubject());
									}else{
										model.addAttribute("subject", motion.getSubject());
									}
								}else{
									model.addAttribute("subject", motion.getSubject());
								}
								
								List<ReferencedEntity> referencedEntities= new ArrayList<ReferencedEntity>();
								if(motion.getReferencedEntities() != null){
									referencedEntities = motion.getReferencedEntities();
								}
								List<Reference> references = new ArrayList<Reference>();
								StringBuffer buffer = new StringBuffer();
								if(!referencedEntities.isEmpty()){
									for(ReferencedEntity i:referencedEntities){
										
										Reference reference = new Reference();
										reference.setId(String.valueOf(i.getId()));
										reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(((Motion)i.getDevice()).getNumber())));
										buffer.append(i.getId()+",");
										references.add(reference);
										
										model.addAttribute("referencedEntities",references);
										model.addAttribute("referencedEntitiesIds",buffer.toString());
									}
								}
								model.addAttribute("id", Long.parseLong(strDeviceId));
								model.addAttribute("number", FormaterUtil.getNumberFormatterNoGrouping(motion.getLocale()).format(motion.getNumber()));
							}
						}
					}
				}
			}else{
				model.addAttribute("error", "One or more request parameters are not available.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "referencing/init";
	}
	
	@RequestMapping(value="/search",method=RequestMethod.POST)
    public @ResponseBody List<QuestionSearchVO> searchQuestionForReferencing(final HttpServletRequest request,
            final Locale locale){
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
		String strHouseType = request.getParameter("houseType");
        String param=request.getParameter("param").trim();
        String questionId=request.getParameter("question");
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(questionId!=null&&start!=null&&noOfRecords!=null&&strHouseType!=null){
            if((!questionId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())&&(!strHouseType.isEmpty())){
                Question question=Question.findById(Question.class, Long.parseLong(questionId));
                if(question.getType() != null){
                	if(question.getType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
                			&& strHouseType.equals(ApplicationConstants.LOWER_HOUSE)){
                		questionSearchVOs = searchHDSLowerHouseForReferencing(question, request, locale);
                	}else{
                    	questionSearchVOs=ReferencedEntity.fullTextSearchReferencing(param,question,Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
                    }
                }
            }
        }       
        return questionSearchVOs;
    }
	
	@RequestMapping(value="/searchmotion",method=RequestMethod.POST)
    public @ResponseBody List<MotionSearchVO> searchMotionForReferencing(final HttpServletRequest request, final Locale locale){
		List<MotionSearchVO> motionSearchVOs = new ArrayList<MotionSearchVO>();
		String strHouseType = request.getParameter("houseType");
        String param = request.getParameter("param").trim();
        String motionId = request.getParameter("motion");
        String start = request.getParameter("start");
        String noOfRecords = request.getParameter("record");
        if(motionId != null && start != null && noOfRecords != null && strHouseType != null){
            if((!motionId.isEmpty()) && (!start.isEmpty()) && (!noOfRecords.isEmpty()) && (!strHouseType.isEmpty())){
                Motion motion = Motion.findById(Motion.class, Long.parseLong(motionId));
                if(motion.getType() != null){
                	motionSearchVOs = ReferencedEntity.fullTextSearchReferencing(param, motion, Integer.parseInt(start), Integer.parseInt(noOfRecords), locale.toString());
                }
            }
        }
        return motionSearchVOs;
    }
	
	private List<QuestionSearchVO> searchHDSLowerHouseForReferencing(final Question question, 
			final HttpServletRequest request, 
			final Locale locale){
		List<QuestionSearchVO> questionSearchVOs=new ArrayList<QuestionSearchVO>();
        String param=request.getParameter("param").trim();
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        String questionSessionYear = request.getParameter("questionSessionYear");
        String questionSessionTypeId = request.getParameter("questionSessionType");
        if(start!=null&&noOfRecords!=null){
                if((!start.isEmpty())&&(!noOfRecords.isEmpty())){
                	try{
	                	if(questionSessionYear!=null&&questionSessionTypeId!=null){
	                		if(!questionSessionYear.isEmpty()&&(!questionSessionTypeId.isEmpty())){
			                   if(!questionSessionYear.equals("-")&&(!questionSessionTypeId.equals("-"))){
			                    
			                	   /** Find the session to be searched in **/
				                    SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(questionSessionTypeId));
				                    Session session = null;				                    
				                    if(sessionType != null){
				                    	session = Session.findSessionByHouseTypeSessionTypeYear(question.getHouseType(), sessionType, Integer.parseInt(questionSessionYear));
				                    }
				                    if(session != null){
				                    	questionSearchVOs=ReferencedEntity.fullTextSearchReferencingQuestionHDS(param,question, session, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
				                    }
			                   }else{
			                	   questionSearchVOs=ReferencedEntity.fullTextSearchReferencingQuestionHDS(param,question, false, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
			                	}
	                		}
	                	}else{
	                		questionSearchVOs=ReferencedEntity.fullTextSearchReferencingQuestionHDS(param,question, false, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
	                	}
                	}catch(Exception e){
                		e.printStackTrace();
                	}                	
                }
        }       
        return questionSearchVOs;
    }
	
	@RequestMapping(value="/searchresolution",method=RequestMethod.POST)
    public @ResponseBody List<ResolutionSearchVO> searchResolutionForReferencing(final HttpServletRequest request,
            final Locale locale){
		List<ResolutionSearchVO> resolutionSearchVOs=new ArrayList<ResolutionSearchVO>();
        String param=request.getParameter("param").trim();
        String resolutionId=request.getParameter("resolution");
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        String resolutionSessionYear = request.getParameter("resolutionSessionYear");
        String resolutionSessionTypeId = request.getParameter("resolutionSessionType");
        if(resolutionId!=null&&start!=null&&noOfRecords!=null){
                if((!resolutionId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
                	try{
	                	if(resolutionSessionYear!=null&&resolutionSessionTypeId!=null){
	                		if(!resolutionSessionYear.isEmpty()&&(!resolutionSessionTypeId.isEmpty())){
			                   if(!resolutionSessionYear.equals("-")&&(!resolutionSessionTypeId.equals("-"))){
			                	   /** Current resolution to be excluded **/
			                	   Resolution resolution=Resolution.findById(Resolution.class, Long.parseLong(resolutionId));
			                    
			                	   /** Find the session to be searched in **/
				                    SessionType sessionType = SessionType.findById(SessionType.class, Long.parseLong(resolutionSessionTypeId));
				                    Session session = null;				                    
				                    if(sessionType != null){
				                    	session = Session.findSessionByHouseTypeSessionTypeYear(resolution.getHouseType(), sessionType, Integer.parseInt(resolutionSessionYear));
				                    }
				                    if(session != null){
				                    	resolutionSearchVOs=ReferencedEntity.fullTextSearchReferencingResolution(param,resolution, session, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
				                    }
			                   }else{
			                		Resolution resolution=Resolution.findById(Resolution.class, Long.parseLong(resolutionId));
				                    resolutionSearchVOs=ReferencedEntity.fullTextSearchReferencingResolution(param,resolution, false, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
			                	}
	                		}
	                	}else{
	                		Resolution resolution=Resolution.findById(Resolution.class, Long.parseLong(resolutionId));
		                    resolutionSearchVOs=ReferencedEntity.fullTextSearchReferencingResolution(param,resolution, false, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
	                	}
                	}catch(Exception e){
                		e.printStackTrace();
                	}                	
                }
        }       
        return resolutionSearchVOs;
    }
	
	@RequestMapping(value="/searchbill",method=RequestMethod.POST)
    public @ResponseBody List<BillSearchVO> searchBillForReferencing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		List<BillSearchVO> billSearchVOs=new ArrayList<BillSearchVO>();
        String param=request.getParameter("param").trim();
        String billId=request.getParameter("bill");
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(param!=null&&billId!=null&&start!=null&&noOfRecords!=null){
                if((!param.isEmpty()&&!billId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
                	try{
                		Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
                		String defaultTitleLanguage = bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
                		model.addAttribute("defaultTitleLanguage", defaultTitleLanguage);
                		String language = "";
                		if(request.getParameter("language")!=null){			
                			if((!request.getParameter("language").isEmpty())&&(!request.getParameter("language").equals("-"))){
                				language=request.getParameter("language");				
                			} else {
                				int firstChar=request.getParameter("param").charAt(0); //param already checked for null & empty
                				if(firstChar>=2308 && firstChar <= 2418){
                					language="marathi";
                				}else if((firstChar>=65 && firstChar <= 90) || (firstChar>=97 && firstChar <= 122)
                						||(firstChar>=48 && firstChar <= 57)){
                					language="english";
                				} else {
                					//default language for bill
                					language=bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
                				}
                			}
                		} else {
                			int firstChar=request.getParameter("param").charAt(0); //param already checked for null & empty
                			if(firstChar>=2308 && firstChar <= 2418){
                				language="marathi";
                			}else if((firstChar>=65 && firstChar <= 90) || (firstChar>=97 && firstChar <= 122)
                					||(firstChar>=48 && firstChar <= 57)){
                				language="english";
                			} else {
                				//default language for bill
                				language=bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
                			}
                		}
                		billSearchVOs=ReferencedEntity.fullTextSearchReferencingBill(param, bill, language, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
                	}catch(Exception e){
                		e.printStackTrace();
                	}                	
                }
        }       
        return billSearchVOs;
    }
	
	@RequestMapping(value="/searchexactbill",method=RequestMethod.POST)
    public @ResponseBody List<BillSearchVO> searchExactBillForReferencing(final HttpServletRequest request,final ModelMap model,final Locale locale){
		List<BillSearchVO> billSearchVOs=new ArrayList<BillSearchVO>();
        String billId=request.getParameter("bill");
        String start=request.getParameter("start");
        String noOfRecords=request.getParameter("record");
        if(billId!=null&&start!=null&&noOfRecords!=null){
                if((!billId.isEmpty())&&(!start.isEmpty())&&(!noOfRecords.isEmpty())){
                	try{
                		Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
                		String defaultTitleLanguage = bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
                		model.addAttribute("defaultTitleLanguage", defaultTitleLanguage);
                		String language = "";
                		if(request.getParameter("language")!=null){			
                			if((!request.getParameter("language").isEmpty())&&(!request.getParameter("language").equals("-"))){
                				language=request.getParameter("language");				
                			} else {
                				int firstChar=request.getParameter("param").charAt(0); //param already checked for null & empty
                				if(firstChar>=2308 && firstChar <= 2418){
                					language="marathi";
                				}else if((firstChar>=65 && firstChar <= 90) || (firstChar>=97 && firstChar <= 122)
                						||(firstChar>=48 && firstChar <= 57)){
                					language="english";
                				} else {
                					//default language for bill
                					language=bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
                				}
                			}
                		} else {
                			int firstChar=request.getParameter("param").charAt(0); //param already checked for null & empty
                			if(firstChar>=2308 && firstChar <= 2418){
                				language="marathi";
                			}else if((firstChar>=65 && firstChar <= 90) || (firstChar>=97 && firstChar <= 122)
                					||(firstChar>=48 && firstChar <= 57)){
                				language="english";
                			} else {
                				//default language for bill
                				language=bill.getSession().getParameter(bill.getType().getType()+"_defaultTitleLanguage");
                			}
                		}
                		billSearchVOs=ReferencedEntity.exactSearchReferencingBill(bill, language, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
                	}catch(Exception e){
                		e.printStackTrace();
                	}                	
                }
        }       
        return billSearchVOs;
    }
	
	@Transactional
	@RequestMapping(value="/referencing",method=RequestMethod.POST)
	public @ResponseBody String referencing(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String strpId=request.getParameter("pId");
		String strrId=request.getParameter("rId");
		String device=request.getParameter("device");
		Boolean status=false;
		if(strpId!=null&&strrId!=null&&device!=null){
			if(!strpId.isEmpty()&&!strrId.isEmpty()&&!device.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long referencingId=Long.parseLong(strrId);
				status=ReferencedEntity.referencing(device, primaryId, referencingId, locale.toString());				
			}
		}
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}				
	}

	@Transactional
	@RequestMapping(value="/dereferencing",method=RequestMethod.POST)
	public  @ResponseBody String dereferencing(final HttpServletRequest request,
			final ModelMap model,
			final Locale locale){
		String strpId=request.getParameter("pId");
		String strrId=request.getParameter("rId");
		String device=request.getParameter("device");
		Boolean status=false;
		if(strpId!=null&&strrId!=null&&device!=null){
			if(!strpId.isEmpty()&&!strrId.isEmpty()&&!device.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long referencingId=Long.parseLong(strrId);
				status=ReferencedEntity.deReferencing(device, primaryId, referencingId, locale.toString());				
			}
		}
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}		
	}
}
