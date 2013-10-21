package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BillSearchVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.Language;
import org.mkcl.els.domain.LapsedEntity;
import org.mkcl.els.domain.ReferencedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/lapsedentity")
public class LapsedEntityController {
	
	@RequestMapping(value="/init",method=RequestMethod.GET)
	public String getReferLapsedInit(final HttpServletRequest request,final ModelMap model){
		String strDeviceId=request.getParameter("id");
		String strDeviceType = request.getParameter("deviceType");
		String strUsergroupType = request.getParameter("usergroupType");
		
		try{
			if(strDeviceId!=null && strUsergroupType!=null && strDeviceType!=null){
				if(!strDeviceId.isEmpty()){
					model.addAttribute("deviceType", strDeviceType);
					if(strDeviceType.startsWith(ApplicationConstants.DEVICE_BILLS)){
						model.addAttribute("whichDevice", ApplicationConstants.DEVICE_BILLS);
					}
					if(strUsergroupType != null){
						if(!strUsergroupType.isEmpty()){
							model.addAttribute("usergroupType", strUsergroupType);
						}
					}
					if(strDeviceType != null){
						if(!strDeviceType.isEmpty()){
							if(strDeviceType.startsWith(ApplicationConstants.DEVICE_BILLS)){								
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
								List<LapsedEntity> lapsedEntities= new ArrayList<LapsedEntity>();
								if(bill.getLapsedBill() != null){
									lapsedEntities.add(bill.getLapsedBill());
								}
								List<Reference> references=new ArrayList<Reference>();
								StringBuffer buffer=new StringBuffer();
								if(!lapsedEntities.isEmpty()){
									for(LapsedEntity i:lapsedEntities){
										
										Reference reference=new Reference();
										reference.setId(String.valueOf(i.getId()));
										reference.setName(String.valueOf(FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(((Bill)i.getDevice()).getNumber())));
										buffer.append(i.getId()+",");
										references.add(reference);
										
										model.addAttribute("lapsedEntities",references);
										model.addAttribute("lapsedEntitiesIds",buffer.toString());
									}
								}
								model.addAttribute("id",Long.parseLong(strDeviceId));
								if(bill.getNumber()!=null) {
									model.addAttribute("number",FormaterUtil.getNumberFormatterNoGrouping(bill.getLocale()).format(bill.getNumber()));
								}							
							}							
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "referlapsed/init";
	}
	
	@RequestMapping(value="/searchbill",method=RequestMethod.POST)
    public @ResponseBody List<BillSearchVO> searchBillForReferLapsed(final HttpServletRequest request,final ModelMap model,final Locale locale){
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
                		billSearchVOs=LapsedEntity.fullTextSearchReferLapsedBill(param, bill, language, Integer.parseInt(start),Integer.parseInt(noOfRecords),locale.toString());
                	}catch(Exception e){
                		e.printStackTrace();
                	}                	
                }
        }       
        return billSearchVOs;
    }
	
	@Transactional
	@RequestMapping(value="/referlapsed",method=RequestMethod.POST)
	public @ResponseBody String referLapsed(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strrId=request.getParameter("rId");
		String device=request.getParameter("device");
		Boolean status=false;
		if(strpId!=null&&strrId!=null&&device!=null){
			if(!strpId.isEmpty()&&!strrId.isEmpty()&&!device.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long referencingId=Long.parseLong(strrId);
				status=LapsedEntity.referLapsed(device, primaryId, referencingId, locale.toString());				
			}
		}
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}				
	}

	@Transactional
	@RequestMapping(value="/dereferlapsed",method=RequestMethod.POST)
	public  @ResponseBody String deReferLapsed(final HttpServletRequest request,final ModelMap model,final Locale locale){
		String strpId=request.getParameter("pId");
		String strrId=request.getParameter("rId");
		String device=request.getParameter("device");
		Boolean status=false;
		if(strpId!=null&&strrId!=null&&device!=null){
			if(!strpId.isEmpty()&&!strrId.isEmpty()&&!device.isEmpty()){
				Long primaryId=Long.parseLong(strpId);
				Long referencingId=Long.parseLong(strrId);
				status=LapsedEntity.deReferLapsed(device, primaryId, referencingId, locale.toString());				
			}
		}
		if(status){
			return "SUCCESS";
		}else{
			return "FAILED";
		}		
	}

}
