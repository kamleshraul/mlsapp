package org.mkcl.els.controller.bis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.common.vo.SectionVO;
import org.mkcl.els.controller.GenericController;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.domain.Section;
import org.mkcl.els.domain.SectionOrderSeries;
import org.mkcl.els.domain.UserGroupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("bill/section")
public class BillSectionController extends GenericController<Section> {
	
	@Override
	protected void populateList(final ModelMap model, final HttpServletRequest request,
            final String locale, final AuthUser currentUser) {
		super.populateList(model, request, locale, currentUser);
		/****populating allowed languages for the bill****/
		List<Language> languagesAllowed = new ArrayList<Language>();
		String billId = request.getParameter("billId");
		if(billId!=null && !billId.isEmpty()) {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
			if(bill!=null) {
				String languagesAllowedParameter = bill.getSession().getParameter(bill.getOriginalType().getType()+"_languagesAllowed");
				if(languagesAllowedParameter!=null) {
					for(String languageAllowed: languagesAllowedParameter.split("#")) {
						Language language = Language.findByFieldName(Language.class, "type", languageAllowed, locale);
						languagesAllowed.add(language);
					}
				}
				/****populating language for the bill****/
				String language = request.getParameter("language");
				if(language==null) {
					String defaultLanguageParameter = bill.getSession().getParameter(bill.getOriginalType().getType()+"_defaultTitleLanguage");
					if(defaultLanguageParameter!=null) {
						Language defaultLanguage = Language.findByFieldName(Language.class, "type", defaultLanguageParameter, locale);
						if(defaultLanguage!=null) {
							language = defaultLanguage.getType();							
						}
					}
				}
				model.addAttribute("selectedLanguage", language);
			}
		}		
		model.addAttribute("languages", languagesAllowed);
	}
	
	@Override
	protected void populateNew(final ModelMap model, 
    		final Section domain,
            final String locale, 
            final HttpServletRequest request) {
        domain.setLocale(locale);
        domain.setLanguage(request.getParameter("language"));        
        populateSectionOrderSeries(model, domain, request);        
    }
	
	@Override
	protected void populateEdit(final ModelMap model, final Section domain,
            final HttpServletRequest request) {
		String billId = request.getParameter("billId");
		if(billId==null || billId.isEmpty()) {
			if(request.getSession().getAttribute("billId")!=null) {
				billId = (String) request.getSession().getAttribute("billId");
				request.getSession().removeAttribute("billId");
			}			
		}
		populateSectionOrderSeries(model, domain, request);
		model.addAttribute("currentNumber", domain.getNumber());
		String currentOrder = domain.findOrder();
		if(currentOrder == null) {
			model.addAttribute("errorcode", "sectionOrderSeries_notSet");
        	return;
		}
		currentOrder = FormaterUtil.formatNumberNoGrouping(Integer.parseInt(currentOrder), domain.getLocale());
		model.addAttribute("currentOrder", currentOrder);
		model.addAttribute("currentLanguage", domain.getLanguage());
		//find whether this is only section at its hierarchy level & check whether level has custom order
		try {			
			List<Section> sections = Bill.findAllSiblingSectionsForGivenSection(Long.parseLong(billId), domain.getLanguage(), domain.getNumber());
			if(sections==null || sections.isEmpty()) {
				model.addAttribute("isFirstForHierarchyLevel", "yes");											
			} else {
				model.addAttribute("isFirstForHierarchyLevel", "no");
				Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
				boolean isHierarchyCustomOrdered = Section.isHierarchyLevelWithCustomOrder(bill, domain.getLanguage(), domain.getNumber());
				if(isHierarchyCustomOrdered) {
					model.addAttribute("isHierarchyLevelWithCustomOrder", "yes");
				} else {
					model.addAttribute("isHierarchyLevelWithCustomOrder", "no");
				}
			}
		} catch(ELSException e) {
			model.addAttribute("errorcode", e.getParameter());
		}		
    }
	
	private void populateSectionOrderSeries(final ModelMap model, 
    		final Section domain,            
            final HttpServletRequest request) {
		String languageType = domain.getLanguage();
        if(languageType==null || languageType.isEmpty()) {
        	model.addAttribute("errorcode", "RequestParam_language_notfound");
        	return;
        }
        Language language = Language.findByFieldName(Language.class, "type", languageType, domain.getLocale());
        if(language==null) {
        	model.addAttribute("errorcode", "language_withgiventype_notfound");
        	return;
        }
        List<SectionOrderSeries> sectionOrderSeries = SectionOrderSeries.findAllByFieldName(SectionOrderSeries.class, "language", language, "name", ApplicationConstants.ASC, domain.getLocale());
        if(sectionOrderSeries==null || sectionOrderSeries.isEmpty()) {
        	model.addAttribute("errorcode", "sectionOrderSeries_notSet");
        	return;
        }
        model.addAttribute("sectionOrderSeries", sectionOrderSeries);
        if(domain.getOrderingSeries()!=null) {
        	model.addAttribute("selectedOrderingSeries", domain.getOrderingSeries().getId());
        }
	}
	
	@Override
	protected void preValidateCreate(final Section domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        preValidate(domain, result, request);
    }
	
	@Override
	protected void preValidateUpdate(final Section domain,
            final BindingResult result, 
            final HttpServletRequest request) {
        preValidate(domain, result, request);
    }
	
	private void preValidate(final Section domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
		/** set bill id as session attribute to be read even after redirection **/
		request.getSession().setAttribute("billId", request.getParameter("billId"));
	}
	
	@Override
	protected void customValidateCreate(final Section domain,
            final BindingResult result, 
            final HttpServletRequest request) {	
		
        customValidate(domain, result, request);
        
    }
	
	@Override
	protected void customValidateUpdate(final Section domain,
            final BindingResult result, 
            final HttpServletRequest request) {		
		
        customValidate(domain, result, request); 
        
        String currentLanguage = request.getParameter("currentLanguage");
		if(currentLanguage==null || currentLanguage.isEmpty()) {
			result.reject("section_currentLanguage_notset", "Section has no existing language");
		}
        String currentNumber = request.getParameter("currentNumber");
		if(currentNumber==null || currentNumber.isEmpty()) {
			result.reject("section_currentNumber_notset", "Section has no existing number");
		}
		String currentOrder = request.getParameter("currentOrder");
		if(currentOrder==null || currentOrder.isEmpty()) {
			result.reject("section_currentOrder_notset", "Section has no existing order");
		}
		if(domain.getHierarchyOrder()==null || domain.getHierarchyOrder().isEmpty()) {
			result.reject("section_hierarchyOrder_notset", "Section has no existing hierarchy order");
		}
    }
	
	private void customValidate(final Section domain, 
    		final BindingResult result,
            final HttpServletRequest request) {
		// Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
        String language = request.getParameter("language");
		if(language==null || language.isEmpty()) {
			result.reject("section_language_notset", "Please select language for the section");
		}
		if(domain.getNumber()==null || domain.getNumber().isEmpty()) {
			result.reject("section_number_notset", "Please select number for the section");
		}
		String billId = request.getParameter("billId");
		if(billId==null || billId.isEmpty()) {
			result.reject("section_bill_notset", "Please select bill for the section");
		} else { 
			try {
				Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
				if(bill==null) {
					result.reject("section_bill_notfound", "Bill with selected Id not found");
				} 
				if(domain.getId()==null) {
					Section section = Bill.findSection(bill.getId(), language, domain.getNumber());
					if(section!=null) {
						result.reject("section_duplicate_number", "Section with selected number already exists");
					}
				} else {
					Section section = Bill.findSection(bill.getId(), language, domain.getNumber());
					if(section!=null && !section.getId().equals(domain.getId())) {
						result.reject("section_duplicate_number", "Section with selected number already exists");
					}
				}
			} catch(NumberFormatException e) {
				result.reject("section_bill_incorrectId", "Please select proper bill id for the section");
			} catch(Exception e) {
				result.reject("section_some_error", "There is some error.");
			}
		}		
		String sectionOrder = request.getParameter("order");
		if(sectionOrder==null || sectionOrder.isEmpty()) {
			result.reject("section_order_notset", "Please select order for the section");
		}
		if(domain.getText()==null || domain.getText().isEmpty()) {
			result.reject("section_text_notset", "Please select text for the section");
		}
		String usergroupType = request.getParameter("usergroupType");
		if(usergroupType==null || usergroupType.isEmpty()) {
			result.reject("section_usergroupType_notset", "Please select usergroupType for the section");
		}
	}
	
	@Override
	protected void populateCreateIfNoErrors(final ModelMap model,
            final Section domain, 
            final HttpServletRequest request) {
		
		String billId = request.getParameter("billId");
		String language = request.getParameter("language");		
		String sectionOrder = request.getParameter("order");
		String strUserGroupType = request.getParameter("usergroupType");
		
		try {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));							
			domain.setLanguage(language);			
			domain.setBillDraft(bill.findLatestDraft());
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			if(strUserGroupType!=null){
				UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
				domain.setEditedAs(userGroupType.getName());
			}	
			sectionOrder = FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).parse(sectionOrder).toString();
			domain.setHierarchyOrder(bill, domain.getNumber(), sectionOrder, "", "");
		} catch (ELSException e) {
			e.printStackTrace();			
		} catch (ParseException e) {			
			e.printStackTrace();
		}
//      populateIfNoErrors(model, domain, request); 
    }
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final Section domain,
            final HttpServletRequest request) {
		String billId = request.getParameter("billId");
		Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
		if(bill.getSections()!=null) {
			bill.getSections().add(domain);
			
		} else {
			List<Section> sections = new ArrayList<Section>();
			sections.add(domain);
			bill.setSections(sections);
		}
		bill.simpleMerge();
    }
	
	@Override
	protected void populateUpdateIfNoErrors(final ModelMap model,
            final Section domain, 
            final HttpServletRequest request) {
		String billId = request.getParameter("billId");
		String currentLanguage = request.getParameter("currentLanguage");
		String currentNumber = request.getParameter("currentNumber");
		String currentOrder = request.getParameter("currentOrder");
		String language = request.getParameter("language");		
		String sectionOrder = request.getParameter("order");
		String strUserGroupType = request.getParameter("usergroupType");
		
		try {
			Bill bill = Bill.findById(Bill.class, Long.parseLong(billId));
			domain.setLanguage(language);
			domain.setBillDraft(bill.findLatestDraft());
			domain.setEditedOn(new Date());
			domain.setEditedBy(this.getCurrentUser().getActualUsername());
			if(strUserGroupType!=null){
				UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",strUserGroupType, domain.getLocale());
				domain.setEditedAs(userGroupType.getName());
			}
			currentOrder = FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).parse(currentOrder).toString();
			sectionOrder = FormaterUtil.getNumberFormatterNoGrouping(domain.getLocale()).parse(sectionOrder).toString();
			domain.updateHierarchyOrder(bill, currentLanguage, currentNumber, currentOrder, sectionOrder);
		} catch (Exception e) {
			e.printStackTrace();			
		}
//        populateIfNoErrors(model, domain, request);
    }
	
	@RequestMapping(value="/reportURL",method=RequestMethod.GET)
	public @ResponseBody void generateReportUsingFOP(final HttpServletRequest request,final HttpServletResponse response, final ModelMap model,final Locale locale) throws ELSException {
		File reportFile = null; 
		Boolean isError = false;
		MessageResource errorMessage = MessageResource.findByFieldName(MessageResource.class, "code", "generic.errorMessage", locale.toString());
		
		String outputFormat = request.getParameter("outputFormat");
		
		Language language = Language.findByFieldName(Language.class, "type", "english", locale.toString());
		String localeOfLanguage = Language.findLocaleForLanguage(language);
		if(localeOfLanguage!=null && !localeOfLanguage.isEmpty()) {
			
		}
		
		if(outputFormat!=null){
			if(!outputFormat.isEmpty()){
				try {
					List<Section> sectionList = Section.findAll(Section.class, "number", ApplicationConstants.ASC, locale.toString());
					
					List<SectionVO> sectionVOs = new ArrayList<SectionVO>();
					for(Section section : sectionList) {
						SectionVO sectionVO = new SectionVO();
						sectionVO.setId(section.getId());
						sectionVO.setLanguage(section.getLanguage());
						sectionVO.setNumber(section.getNumber());
						sectionVO.setContent(section.getText());
						sectionVO.setEditedAs(section.getEditedAs());
						if(section.getEditedOn()!=null) {
							String editedOn = FormaterUtil.formatDateToString(section.getEditedOn(), ApplicationConstants.SERVER_DATEFORMAT, locale.toString());
							sectionVO.setEditedOn(editedOn);
						} else {
							sectionVO.setEditedOn("");
						}		
						sectionVOs.add(sectionVO);
					}
					
					Field[] fields = SectionVO.class.getDeclaredFields();
					List<Object[]> values = new ArrayList<Object[]>();
					List<String> headers = new ArrayList<String>();
					int secCount = 0;
					for(SectionVO sectionVO : sectionVOs) {
						Object[] value = new Object[fields.length];
						int i = 0;
						for (Field field : fields) {
							field.setAccessible(true);
							if(field.get(sectionVO)!=null) {
								value[i] = field.get(sectionVO).toString();
							} else {
								value[i] = "";
							}
							
							if(secCount==0) {
								String header = "column "+(i+1);
								headers.add(header);								
							}	
							
							i++;
						}
						values.add(value);	
						secCount++;
					}
					reportFile = generateReportUsingFOP(new Object[]{"Sections For Bills", headers, values}, "table_template", outputFormat, "Section List", locale.toString());
					if(reportFile!=null) {
						System.out.println("Report generated successfully in " + outputFormat +  " format!");
						openOrSaveReportFileFromBrowser(response, reportFile, outputFormat);
					}
				} catch(Exception e) {
					logger.error("**** Some Runtime Exception Occurred ****");
					e.printStackTrace();
					isError = true;
				}
			} else {
				logger.error("**** Check request parameter 'strRequestParameter,outputFormat' for empty values ****");
				isError = true;
			}
		} else{
			logger.error("**** Check request parameter 'strRequestParameter,outputFormat' for null values ****");
			isError = true;				
		}
		
		if(isError) {
			try {
				//response.sendError(404, "Report cannot be generated at this stage.");
				if(errorMessage != null) {
					if(!errorMessage.getValue().isEmpty()) {
						response.getWriter().println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/></head><body><h3>" + errorMessage.getValue() + "</h3></body></html>");
					} else {
						response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
					}
				} else {
					response.getWriter().println("<h3>Some Error In Report Generation. Please Contact Administrator.</h3>");
				}

				return;
			} catch (IOException e) {						
				e.printStackTrace();
			}
		}
	}
	
}
