package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.LayingLetter;
import org.mkcl.els.domain.PrintRequisition;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.UserGroupType;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/layingletter")
public class LayingLetterController extends BaseController {
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
	@RequestMapping(value = "/bill/layLetterWhenPassedByFirstHouse", method = RequestMethod.GET)
	public String layLetterWhenPassedByFirstHouse(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String currentHouseTypeType = request.getParameter("houseType");
		String selectedYearStr = request.getParameter("sessionYear");
		String selectedBillIdStr = request.getParameter("billId");
		/**** House Types ****/
		List<HouseType> houseTypes = HouseType.findAll(HouseType.class, "type", ApplicationConstants.ASC, locale.toString());
		if(houseTypes==null) {
			logger.error("**** house types are not available. ****");
			model.addAttribute("errorcode", "HOUSETYPES_NOT_AVAILABLE");		
			return "printrequisition/error";
		}
		if(houseTypes.isEmpty()) {
			logger.error("**** house types are not available. ****");
			model.addAttribute("errorcode", "HOUSETYPES_NOT_AVAILABLE");		
			return "printrequisition/error";
		}
		model.addAttribute("houseTypes", houseTypes);
		/**** Current HouseType ****/
		HouseType currentHouseType = null;
		if(currentHouseTypeType!=null) {
			if(!currentHouseTypeType.isEmpty()) {
				if(!currentHouseTypeType.equals(ApplicationConstants.BOTH_HOUSE)) {
					currentHouseType = HouseType.findByFieldName(HouseType.class, "type", currentHouseTypeType, locale.toString());
					if(currentHouseType!=null) {
						model.addAttribute("currentHouseType", currentHouseType);					
					} else {
						logger.error("**** Check request parameter 'houseType' for incorrect value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");		
						return "layingletter/error";
					}
				}								
			} else {
				logger.error("**** Check request parameter 'houseType' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "layingletter/error";
			}
		} else {
			logger.error("**** Check request parameter 'houseType' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "layingletter/error";
		}		
		/**** Selected Bill ****/
		Bill selectedBill = null;
		if(selectedBillIdStr!=null) {
			if(!selectedBillIdStr.isEmpty()) {
				try {
					long selectedBillId = Long.parseLong(selectedBillIdStr);
					selectedBill = Bill.findById(Bill.class, selectedBillId);
					if(selectedBill!=null) {
						if(selectedBill.getRecommendationStatus().getType().startsWith(ApplicationConstants.BILL_PROCESSED_PASSED)
								&& selectedBill.getRecommendationStatus().getType().endsWith(ApplicationConstants.BILL_FIRST_HOUSE)) {
							if(Bill.findHouseOrderOfGivenHouseForBill(selectedBill, currentHouseType.getType()).equals(ApplicationConstants.BILL_SECOND_HOUSE)) {
								if(selectedBill.getNumber()!=null) {
									model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
									model.addAttribute("selectedBillId", selectedBill.getId());									
								}
							} else {
								logger.error("**** selected housetype is not second house of selected bill. so it is not eligible for laying letter. ****");
								model.addAttribute("errorcode", "BILL_LAYINGERROR_HOUSETYPENOTSECOND");
								return "layingletter/error";
							}
						} else {
							logger.error("**** selected bill is not currently passed from first house. so it is not eligible for laying letter. ****");
							model.addAttribute("errorcode", "BILL_LAYINGERROR_NOTPASSEDFROMFIRSTHOUSE");
							return "layingletter/error";
						}						
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'billId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "layingletter/error";
				}								
			}
		}
		/**** Bill Year ****/
		Integer selectedYear = null;
		if(selectedBill!=null) {
			selectedYear = Bill.findYear(selectedBill);
		}
		if(selectedYear==null) {
			if(selectedYearStr!=null) {
				if(!selectedYearStr.isEmpty()) {
					try {
						selectedYear = Integer.parseInt(selectedYearStr);					
					} catch(NumberFormatException ne) {
						logger.error("**** Check request parameter 'year' for non-numeric value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");		
						return "layingletter/error";
					}
				} else {
					logger.error("**** Check request parameter 'year' for empty value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
					return "layingletter/error";
				}
			} else {
				logger.error("**** Check request parameter 'year' for null value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
				return "layingletter/error";
			}
		}
		if(selectedYear!=null) {
			model.addAttribute("formattedSelectedYear", FormaterUtil.formatNumberNoGrouping(selectedYear, locale.toString()));
			model.addAttribute("selectedYear", selectedYear);
		}	
		/**** Bill HouseType ****/
		HouseType selectedHouseType = currentHouseType;
		if(selectedBill!=null) {
			if(selectedBill.getType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)) {
				selectedHouseType = selectedBill.getHouseType();
			} else if(selectedBill.getType().getType().equals(ApplicationConstants.GOVERNMENT_BILL)) {
				selectedHouseType = selectedBill.getIntroducingHouseType();
			}
		}
		if(selectedHouseType!=null) {
			model.addAttribute("selectedHouseType", selectedHouseType);
		}
		/**** House Rounds Available For Bill ****/
		CustomParameter billHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
		if(billHouseRoundsParameter!=null) {
			if(billHouseRoundsParameter.getValue()!=null) {
				try {
					Integer houseRoundsAvailable = Integer.parseInt(billHouseRoundsParameter.getValue());							
					/**** Populate House Rounds ****/
					List<MasterVO> houseRoundVOs = new ArrayList<MasterVO>();
					for(int i=1; i<=houseRoundsAvailable; i++) {
						MasterVO houseRoundVO = new MasterVO();
						houseRoundVO.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
						houseRoundVO.setValue(String.valueOf(i));												
						houseRoundVOs.add(houseRoundVO);
					}
					model.addAttribute("houseRoundVOs", houseRoundVOs);
				} catch(NumberFormatException ne) {
					logger.error("custom parameter 'BILL_HOUSEORDERS' is not set properly.");
					model.addAttribute("errorcode", "bill_houseorders_setincorrect");
					return "layingletter/error";
				}				
			} else {
				logger.error("custom parameter 'BILL_HOUSEORDERS' is not set properly.");
				model.addAttribute("errorcode", "bill_houseorders_setincorrect");
				return "layingletter/error";
			}	
		} else {
			logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
			model.addAttribute("errorcode", "bill_houseorders_notset");
			return "layingletter/error";
		}
		model.addAttribute("domain", new LayingLetter());
		return "layingletter/layletterforbill";
	}
	
	private void populateLayingLetter(final ModelMap model, final HttpServletRequest request, final LayingLetter layingLetter) {
		if(layingLetter!=null) {
			model.addAttribute("layingLetter", layingLetter);
			Status status = Status.findByType(layingLetter.getStatus(), layingLetter.getLocale());						
			model.addAttribute("status", status);
			if(layingLetter.getLayingDate()!=null) {
				model.addAttribute("layingDate", FormaterUtil.formatDateToString(layingLetter.getLayingDate(), ApplicationConstants.SERVER_DATEFORMAT, layingLetter.getLocale()));
			} else {
				model.addAttribute("layingDate", FormaterUtil.formatDateToString(new Date(), ApplicationConstants.SERVER_DATEFORMAT, layingLetter.getLocale()));
			}						
			if(layingLetter.getLetter()!=null) {
				model.addAttribute("letter", layingLetter.getLetter());							
			}
			if(!layingLetter.getStatus().equals(ApplicationConstants.LAYINGLETTER_NOTSEND)) {
				model.addAttribute("isLetterLaid", true);
				model.addAttribute("isLetterRemovable", false);
			} else {
				model.addAttribute("isLetterLaid", false);
				model.addAttribute("isLetterRemovable", true);
			}
		}		
	}
	
	@RequestMapping(value = "/bill/getlayingletter", method = RequestMethod.GET)
	public String getLayingLetterForBill(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String selectedBillId = request.getParameter("deviceId");
		String houseRound = request.getParameter("houseRound");
		String layingFor = ApplicationConstants.LAYING_IN_SECONDHOUSE_POST_PASSED_BY_FIRST_HOUSE;
		if(selectedBillId!=null&&houseRound!=null) {
			if(!selectedBillId.isEmpty()&&!houseRound.isEmpty()) {
				try {
					Bill bill = Bill.findById(Bill.class, Long.parseLong(selectedBillId));
					if(bill!=null) {
						Map<String, String> layingLetterIdentifiers = new HashMap<String, String>();
						layingLetterIdentifiers.put("deviceId", selectedBillId);
						layingLetterIdentifiers.put("layingFor", layingFor);				
						layingLetterIdentifiers.put("houseRound", houseRound);
						LayingLetter layingLetter = LayingLetter.findLatestByFieldNames(layingLetterIdentifiers, locale.toString());
						if(layingLetter==null) {
							layingLetter = new LayingLetter();
							layingLetter.setLocale(locale.toString());
							layingLetter.setDeviceId(selectedBillId);
							layingLetter.setHouseRound(houseRound);
							layingLetter.setLayingFor(ApplicationConstants.LAYING_IN_SECONDHOUSE_POST_PASSED_BY_FIRST_HOUSE);
							layingLetter.setStatus(ApplicationConstants.LAYINGLETTER_NOTSEND);							
						}
						populateLayingLetter(model, request, layingLetter);
					} else {
						logger.error("**** Check request parameter 'deviceId' for invalid value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return "layingletter/error";
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'deviceId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "layingletter/error";
				}				
			} else {
				logger.error("**** Check request parameter 'deviceId' and 'houseRound' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");
				return "layingletter/error";
			}
		} else {
			logger.error("**** Check request parameter 'deviceId' and 'houseRound' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");
			return "layingletter/error";
		}
		return "layingletter/fieldsform";
	}
	
	@Transactional
	@RequestMapping(value = "/bill/layLetterWhenPassedByFirstHouse", method = RequestMethod.POST)
	public String updateLetterWhenPassedByFirstHouse(final ModelMap model, final HttpServletRequest request, 
			final Locale locale, @Valid @ModelAttribute("domain") LayingLetter layingLetter) {
		String isLetterLaid = request.getParameter("isLetterLaid");
		if(Boolean.parseBoolean(isLetterLaid) == true) {
			model.addAttribute("errorcode", "LETTER_LAID_ALREADY");
			return "layingletter/error";
		}
		String setlayingDate = request.getParameter("setlayingDate");
		if(setlayingDate!=null) {
			layingLetter.setLayingDate(FormaterUtil.formatStringToDate(setlayingDate, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
		}
		String operation = request.getParameter("operation");
		if(operation!=null) {
			if(operation.equals("send")) {
				Bill bill = Bill.findById(Bill.class, Long.parseLong(layingLetter.getDeviceId()));
				String endflag="";	
				String level="";
				Map<String,String> properties=new HashMap<String, String>();															
				ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
				properties=new HashMap<String, String>();					
				/**** Next user and usergroup ****/
				Status expectedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_LAYLETTER, bill.getLocale());
				HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", layingLetter.getHouseType(),layingLetter.getLocale());
				int startingLevel = 1;						
				String strStartingUserGroup=request.getParameter("usergroupForLayingLetter");
				if(expectedStatus!=null && strStartingUserGroup!=null) {
					UserGroup startingUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strStartingUserGroup));
					List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(bill,houseTypeForWorkflow,false,expectedStatus,startingUserGroup,startingLevel,bill.getLocale());
					if(eligibleActors!=null && !eligibleActors.isEmpty()) {
						String nextuser=eligibleActors.get(0).getId();	
						String nextUserGroupType="";
						if(nextuser!=null){						
							if(!nextuser.isEmpty()){
								String[] temp=nextuser.split("#");
								properties.put("pv_user",temp[0]);
								nextUserGroupType=temp[1];
								level=temp[2];
								String localizedActorName=temp[3]+"("+temp[4]+")";
							}
						}
						endflag="continue";
						properties.put("pv_endflag",endflag);	
						properties.put("pv_deviceId",String.valueOf(bill.getId()));
						properties.put("pv_deviceTypeId",String.valueOf(bill.getType().getId()));
						ProcessInstance processInstance=processService.createProcessInstance(processDefinition, properties);
						/**** Process Started and task created ****/
						Task task=processService.getCurrentTask(processInstance);
						if(endflag!=null){
							if(!endflag.isEmpty()){
								if(endflag.equals("continue")){
									/**** Workflow Detail entry made only if it's not the end of workflow ****/
//									//set status here if it is going to be needed..
//									bill.setPrintRequisitionToPressPostAdmissionStatus(expectedStatus);
									WorkflowDetails workflowDetails = WorkflowDetails.create(bill,houseTypeForWorkflow,false,null,task,ApplicationConstants.LAY_LETTER_WORKFLOW,nextUserGroupType,level);
									layingLetter.setWorkflowDetailsId(String.valueOf(workflowDetails.getId()));
									layingLetter.setStatus(ApplicationConstants.LAYINGLETTER_PENDING);
									model.addAttribute("type", "laid");																						
								}
							}
						}					
					}							
				}
			} else if(operation.equals("save")) {
				model.addAttribute("type", "saved");
			}
		}	
		layingLetter.setEditedOn(new Date());
		layingLetter.setEditedBy(this.getCurrentUser().getActualUsername());	
		String usergroupTypeForLayingLetter = request.getParameter("usergroupTypeForLayingLetter");
		if(usergroupTypeForLayingLetter!=null){
			UserGroupType userGroupType=UserGroupType.findByFieldName(UserGroupType.class,"type",usergroupTypeForLayingLetter, layingLetter.getLocale());
			layingLetter.setEditedAs(userGroupType.getType());
		}
		if(layingLetter.getId()!=null) {
			layingLetter.merge();
			//handle stale state exception
			layingLetter = LayingLetter.findById(LayingLetter.class, layingLetter.getId());
		} else {					
			layingLetter.persist();
		}
		populateLayingLetter(model, request, layingLetter);		
		return "layingletter/fieldsform";
	}

}
