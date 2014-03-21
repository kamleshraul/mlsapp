package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.common.vo.ProcessDefinition;
import org.mkcl.els.common.vo.ProcessInstance;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.Task;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.PrintRequisition;
import org.mkcl.els.domain.PrintRequisitionParameter;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.UserGroup;
import org.mkcl.els.domain.WorkflowConfig;
import org.mkcl.els.domain.WorkflowDetails;
import org.mkcl.els.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/printrequisition")
public class PrintRequisitionController extends BaseController {
	
	/** The process service. */
	@Autowired
	private IProcessService processService;
	
	@RequestMapping(value = "/bill", method = RequestMethod.GET)
	public String getRequisitionInitForBill(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String currentHouseTypeType = request.getParameter("houseTypeType");		
		String selectedYearStr = request.getParameter("billYear");
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
						return "printrequisition/error";
					}
				}								
			} else {
				logger.error("**** Check request parameter 'houseType' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'houseType' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "printrequisition/error";
		}
		/**** Selected Bill ****/
		Bill selectedBill = null;
		if(selectedBillIdStr!=null) {
			if(!selectedBillIdStr.isEmpty()) {
				try {
					long selectedBillId = Long.parseLong(selectedBillIdStr);
					selectedBill = Bill.findById(Bill.class, selectedBillId);
					if(selectedBill!=null) {
						if(selectedBill.getNumber()!=null) {
							model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
							model.addAttribute("selectedBillId", selectedBill.getId());
							/**** Requisition Statuses Allowed For Selected Bill In Selected House ****/
							String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(selectedBill, currentHouseType.getType());
							CustomParameter printRequisitionStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_PRINTREQUISITION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase(), "");
							if(printRequisitionStatusParameter!=null) {
								if(printRequisitionStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = printRequisitionStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(currentHouseOrder)) {
												if(i.trim().contains(currentHouseType.getType())) {
													filteredStatusTypes.append(i.trim()+",");
												}																						
											} else {
												filteredStatusTypes.append(i.trim()+",");						
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> printRequisitionStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									model.addAttribute("printRequisitionStatuses", printRequisitionStatuses);
								} else {
									logger.error("Custom Parameter 'BILL_PRINTREQUISITION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase() +"' is not set properly");
									model.addAttribute("errorcode", "bill_printrequisition_statusoptions_"+currentHouseOrder+"_setincorrect");
									return "printrequisition/error";
								}
							} else {
								logger.error("Custom Parameter 'BILL_PRINTREQUISITION_STATUSOPTIONS_"+currentHouseOrder.toUpperCase() +"' is not set");
								model.addAttribute("errorcode", "bill_printrequisition_statusoptions_"+currentHouseOrder+"_notset");
								return "printrequisition/error";
							}
						}						
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'billId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
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
						return "printrequisition/error";
					}
				} else {
					logger.error("**** Check request parameter 'year' for empty value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
					return "printrequisition/error";
				}
			} else {
				logger.error("**** Check request parameter 'year' for null value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
				return "printrequisition/error";
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
		/**** Requisition For ****/
		CustomParameter printRequisitionForParameter = CustomParameter.findByName(CustomParameter.class, "BILL_PRINTREQUISITION_REQUISITIONFOROPTIONS", "");
		if(printRequisitionForParameter!=null) {
			if(printRequisitionForParameter.getValue()!=null) {	
				List<String> requisitionForOptions = new ArrayList<String>();				
				String[] requisitionForOptionsArr = printRequisitionForParameter.getValue().split(",");
				for(String i: requisitionForOptionsArr) {
					requisitionForOptions.add(i.trim());									
				}			
				model.addAttribute("requisitionForOptions", requisitionForOptions);
			} else {
				logger.error("Custom Parameter 'BILL_PRINTREQUISITION_REQUISITIONFOROPTIONS' is not set properly");
				model.addAttribute("errorcode", "bill_printrequisition_statusoptions_setincorrect");
				return "printrequisition/error";
			}
		} else {
			logger.error("Custom Parameter 'BILL_PRINTREQUISITION_REQUISITIONFOROPTIONS' is not set");
			model.addAttribute("errorcode", "bill_printrequisition_statusoptions__notset");
			return "printrequisition/error";
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
					return "printrequisition/error";
				}				
			} else {
				logger.error("custom parameter 'BILL_HOUSEORDERS' is not set properly.");
				model.addAttribute("errorcode", "bill_houseorders_setincorrect");
				return "printrequisition/error";
			}	
		} else {
			logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
			model.addAttribute("errorcode", "bill_houseorders_notset");
			return "printrequisition/error";
		}
		model.addAttribute("domain", new PrintRequisition());
		return "printrequisition/requisitionforbill";
	}
	
	private void populatePrintRequisition(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		Map<String, String> fields = new HashMap<String, String>();
		if(printRequisition!=null) {
			model.addAttribute("printRequisition", printRequisition);							
			fields = printRequisition.getFields();
			if(fields==null) {
				fields = new HashMap<String, String>();
			}
		}
		Bill bill = Bill.findById(Bill.class, Long.parseLong(printRequisition.getDeviceId()));
		if(bill==null) {
			logger.error("bill is not found for device id.");
			model.addAttribute("errorcode", "billnotfound");
			return;			
		}
		List<MasterVO> printRequisitionParameterVOs = new ArrayList<MasterVO>();
		List<PrintRequisitionParameter> printRequisitionParameters = PrintRequisitionParameter.findAllByFieldName(PrintRequisitionParameter.class, "requisitionFor", printRequisition.getRequisitionFor(), "parameterOrder", ApplicationConstants.ASC, "");
		for(PrintRequisitionParameter printRequisitionParameter: printRequisitionParameters) {
			MasterVO printRequisitionParameterVO = new MasterVO();
			printRequisitionParameterVO.setName(printRequisitionParameter.getParameterName());
			if(fields.containsKey(printRequisitionParameter.getParameterName())) {
				printRequisitionParameterVO.setValue(fields.get(printRequisitionParameter.getParameterName()));
			} else {
				if(printRequisitionParameter.getParameterName().equals("departmentsToReceiveGazette")) {
					CustomParameter departmentsToReceiveGazetteParameter = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.BILL_GAZETTE_RECEIVING_DEPARTMENTS, printRequisition.getLocale());
					if(departmentsToReceiveGazetteParameter!=null) {
						String departmentsToReceiveGazette = departmentsToReceiveGazetteParameter.getValue();
						if(departmentsToReceiveGazette!=null) {
							if(!departmentsToReceiveGazette.isEmpty()) {
								departmentsToReceiveGazette = departmentsToReceiveGazette.replace("bill", bill.getSubDepartment().getName());
								departmentsToReceiveGazette = departmentsToReceiveGazette.replace("#", ", ");
								printRequisitionParameterVO.setValue(departmentsToReceiveGazette);
							}
						}
					} else {
						logger.error("custom parameter '"+ApplicationConstants.BILL_GAZETTE_RECEIVING_DEPARTMENTS+"' is not set properly.");
						model.addAttribute("errorcode", "bill_gazette_receiving_departments_setincorrect");
						return;
					}
				} else {
					if(printRequisitionParameter.getParameterValue()!=null && !printRequisitionParameter.getParameterValue().isEmpty()) {
						printRequisitionParameterVO.setValue(printRequisitionParameter.getParameterValue());
					}
				}
			}
			printRequisitionParameterVO.setOrder(printRequisitionParameter.getParameterOrder());
			printRequisitionParameterVOs.add(printRequisitionParameterVO);
		}
		model.addAttribute("printRequisitionParameterVOs", printRequisitionParameterVOs);
		if(printRequisition!=null) {
			if(printRequisition.getDocketReportEnglish()!=null) {
				if(!printRequisition.getDocketReportEnglish().isEmpty()) {
					model.addAttribute("docketReportEnglish", printRequisition.getDocketReportEnglish());
				}
			}
			if(printRequisition.getDocketReportMarathi()!=null) {
				if(!printRequisition.getDocketReportMarathi().isEmpty()) {
					model.addAttribute("docketReportMarathi", printRequisition.getDocketReportMarathi());
				}
			}
			if(printRequisition.getDocketReportHindi()!=null) {
				if(!printRequisition.getDocketReportHindi().isEmpty()) {
					model.addAttribute("docketReportHindi", printRequisition.getDocketReportHindi());
				}
			}
			if(printRequisition.getPressCopyEnglish()!=null) {
				if(!printRequisition.getPressCopyEnglish().isEmpty()) {
					model.addAttribute("pressCopyEnglish", printRequisition.getPressCopyEnglish());
					model.addAttribute("pressCopiesReceived", "yes");
				}
			}
			if(printRequisition.getPressCopyMarathi()!=null) {
				if(!printRequisition.getPressCopyMarathi().isEmpty()) {
					model.addAttribute("pressCopyMarathi", printRequisition.getPressCopyMarathi());
					if(!model.containsAttribute("pressCopiesReceived")) {
						model.addAttribute("pressCopiesReceived", "yes");
					}
				}
			}
			if(printRequisition.getPressCopyHindi()!=null) {
				if(!printRequisition.getPressCopyHindi().isEmpty()) {
					model.addAttribute("pressCopyHindi", printRequisition.getPressCopyHindi());
					if(!model.containsAttribute("pressCopiesReceived")) {
						model.addAttribute("pressCopiesReceived", "yes");
					}
				}
			}	
			if(printRequisition.getEndorsementCopyEnglish()!=null) {
				if(!printRequisition.getEndorsementCopyEnglish().isEmpty()) {
					model.addAttribute("endorsementCopyEnglish", printRequisition.getEndorsementCopyEnglish());
					model.addAttribute("endorsementCopiesReceived", "yes");
				}
			}
			if(printRequisition.getEndorsementCopyMarathi()!=null) {
				if(!printRequisition.getEndorsementCopyMarathi().isEmpty()) {
					model.addAttribute("endorsementCopyMarathi", printRequisition.getEndorsementCopyMarathi());
					if(!model.containsAttribute("endorsementCopiesReceived")) {
						model.addAttribute("endorsementCopiesReceived", "yes");
					}
				}
			}
			if(printRequisition.getEndorsementCopyHindi()!=null) {
				if(!printRequisition.getEndorsementCopyHindi().isEmpty()) {
					model.addAttribute("endorsementCopyHindi", printRequisition.getEndorsementCopyHindi());
					if(!model.containsAttribute("endorsementCopiesReceived")) {
						model.addAttribute("endorsementCopiesReceived", "yes");
					}
				}
			}
		}
		if(printRequisition.getPublishDateMarathi()!=null) {
			model.addAttribute("publishDateMarathi", FormaterUtil.formatDateToString(printRequisition.getPublishDateMarathi(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
		}
		if(printRequisition.getPublishDateEnglish()!=null) {
			model.addAttribute("publishDateEnglish", FormaterUtil.formatDateToString(printRequisition.getPublishDateEnglish(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
		}
		if(printRequisition.getPublishDateHindi()!=null) {
			model.addAttribute("publishDateHindi", FormaterUtil.formatDateToString(printRequisition.getPublishDateHindi(), ApplicationConstants.SERVER_DATEFORMAT, printRequisition.getLocale()));
		}
		CustomParameter optionalFieldsForDocketParameter = CustomParameter.findByName(CustomParameter.class, "BILL_OPTIONAL_FIELDS_FOR_PRINTREQUISITION", "");
		CustomParameter defaultOptionalFieldsForDocketParameter = CustomParameter.findByName(CustomParameter.class, "BILL_DEFAULT_OPTIONAL_FIELDS_FOR_"+printRequisition.getRequisitionFor().toUpperCase(), "");
		if(optionalFieldsForDocketParameter!=null) {
			if(optionalFieldsForDocketParameter.getValue()!=null) {
				if(!optionalFieldsForDocketParameter.getValue().isEmpty()) {
					List<MasterVO> optionalFieldsForDocket = new ArrayList<MasterVO>();
					List<String> existingOptionalFieldsForDocket = new ArrayList<String>();
					if(printRequisition.getOptionalFieldsForDocket()!=null) {
						if(!printRequisition.getOptionalFieldsForDocket().isEmpty()) {								
							existingOptionalFieldsForDocket = Arrays.asList(printRequisition.getOptionalFieldsForDocket().split("#"));								
						}
					} else {
						if(defaultOptionalFieldsForDocketParameter!=null) {
							if(defaultOptionalFieldsForDocketParameter.getValue()!=null) {
								existingOptionalFieldsForDocket = Arrays.asList(defaultOptionalFieldsForDocketParameter.getValue().split("#"));
							}
						}
					}					
					for(String optionalFieldForDocketParameter: optionalFieldsForDocketParameter.getValue().split("#")) {
						MasterVO optionalFieldForDocket = new MasterVO();
						optionalFieldForDocket.setName(optionalFieldForDocketParameter);
						optionalFieldForDocket.setIsSelected(false);
						for(String existingOptionalFieldForDocket: existingOptionalFieldsForDocket) {
							if(optionalFieldForDocket.getName().equals(existingOptionalFieldForDocket)) {
								optionalFieldForDocket.setIsSelected(true);
								break;
							}
						}
						optionalFieldsForDocket.add(optionalFieldForDocket);
					}
					model.addAttribute("optionalFieldsForDocket", optionalFieldsForDocket);
				}
			}
		}
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		WorkflowDetails workflowForPrintRequisition = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowIdentifiers, printRequisition.getLocale());
		if(workflowForPrintRequisition!=null) {
			model.addAttribute("isPrintRequisitionSent", true);	
			model.addAttribute("isDocketReportRemovable", false);
		} else {
			model.addAttribute("isDocketReportRemovable", true);
		}
	}
	
	private void populateSendForEndorsement(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		populatePrintRequisitionForEndorsementCopies(model, request, printRequisition);
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_SENDFORENDORSEMENT);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		List<WorkflowDetails> workflowsOfSendForEndorsement = WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, workflowIdentifiers, "id", ApplicationConstants.ASC, printRequisition.getLocale());
		if(workflowsOfSendForEndorsement!=null) {
			if(!workflowsOfSendForEndorsement.isEmpty()) {
				model.addAttribute("isAlreadySentForEndorsement", true);
			}
		}
	}
	
	private void populateTransmitEndorsementCopies(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		populatePrintRequisitionForEndorsementCopies(model, request, printRequisition);
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_TRANSMITENDORSEMENTCOPIES);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		List<WorkflowDetails> workflowsOfTransmitEndorsement = WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, workflowIdentifiers, "id", ApplicationConstants.ASC, printRequisition.getLocale());
		if(workflowsOfTransmitEndorsement!=null) {
			if(!workflowsOfTransmitEndorsement.isEmpty()) {
				model.addAttribute("isAlreadyTransmitted", true);
			}
		}
	}
	
	private void populateEndorsementCopies(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		populatePrintRequisitionForEndorsementCopies(model, request, printRequisition);
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_SENDFORENDORSEMENT);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		List<WorkflowDetails> workflowsOfSendForEndorsement = WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, workflowIdentifiers, "id", ApplicationConstants.ASC, printRequisition.getLocale());
		if(workflowsOfSendForEndorsement!=null) {
			if(!workflowsOfSendForEndorsement.isEmpty()) {
				model.addAttribute("isAlreadySentForEndorsement", true);
			}
		}
		workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_TRANSMITENDORSEMENTCOPIES);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		List<WorkflowDetails> workflowsOfTransmitEndorsement = WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, workflowIdentifiers, "id", ApplicationConstants.ASC, printRequisition.getLocale());
		if(workflowsOfTransmitEndorsement!=null) {
			if(!workflowsOfTransmitEndorsement.isEmpty()) {
				model.addAttribute("isAlreadyTransmitted", true);
			}
		}
	}
	
	private void populatePrintRequisitionForEndorsementCopies(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		if(printRequisition!=null) {
			model.addAttribute("printRequisition", printRequisition);
			if(printRequisition.getEndorsementCopyEnglish()!=null) {
				if(!printRequisition.getEndorsementCopyEnglish().isEmpty()) {
					model.addAttribute("endorsementCopyEnglish", printRequisition.getEndorsementCopyEnglish());
					model.addAttribute("endorsementCopiesReceived", "yes");
				}
			}
			if(printRequisition.getEndorsementCopyMarathi()!=null) {
				if(!printRequisition.getEndorsementCopyMarathi().isEmpty()) {
					model.addAttribute("endorsementCopyMarathi", printRequisition.getEndorsementCopyMarathi());
					if(!model.containsAttribute("endorsementCopiesReceived")) {
						model.addAttribute("endorsementCopiesReceived", "yes");
					}
				}
			}
			if(printRequisition.getEndorsementCopyHindi()!=null) {
				if(!printRequisition.getEndorsementCopyHindi().isEmpty()) {
					model.addAttribute("endorsementCopyHindi", printRequisition.getEndorsementCopyHindi());
					if(!model.containsAttribute("endorsementCopiesReceived")) {
						model.addAttribute("endorsementCopiesReceived", "yes");
					}
				}
			}
		}
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		WorkflowDetails workflowForPrintRequisition = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowIdentifiers, printRequisition.getLocale());
		if(workflowForPrintRequisition!=null) {
			model.addAttribute("isPrintRequisitionSent", true);				
		}		
	}
	
	@RequestMapping(value = "/bill/getrequisition", method = RequestMethod.GET)
	public String getRequisitionForBill(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String selectedBillId = request.getParameter("deviceId");
		String requisitionFor = request.getParameter("requisitionFor");
		String status = request.getParameter("status");
		String houseRound = request.getParameter("houseRound");
		if(selectedBillId!=null&&requisitionFor!=null&&status!=null&&houseRound!=null) {
			if(!selectedBillId.isEmpty()&&!requisitionFor.isEmpty()&&!status.isEmpty()) {
				try {
					Bill bill = Bill.findById(Bill.class, Long.parseLong(selectedBillId));
					if(bill!=null) {
						model.addAttribute("requisitionFor", requisitionFor);
						Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
						printRequisitionIdentifiers.put("deviceId", selectedBillId);
						printRequisitionIdentifiers.put("requisitionFor", requisitionFor);				
						printRequisitionIdentifiers.put("status", status);
//						if(houseRound.isEmpty()) {
//							houseRound=null;
//						}
						printRequisitionIdentifiers.put("houseRound", houseRound);
						PrintRequisition printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, locale.toString());
						if(printRequisition==null) {
							printRequisition = new PrintRequisition();
							printRequisition.setLocale(locale.toString());
							printRequisition.setDeviceId(selectedBillId);
							printRequisition.setRequisitionFor(requisitionFor);
							printRequisition.setStatus(status);
							printRequisition.setHouseRound(houseRound);
						}
						populatePrintRequisition(model, request, printRequisition);
					} else {
						logger.error("**** Check request parameter 'deviceId' for invalid value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return "printrequisition/error";
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'deviceId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
				}				
			} else {
				logger.error("**** Check request parameter 'deviceId', 'requisitionFor', and 'status' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'deviceId', 'requisitionFor', 'status', and 'houseRound' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");
			return "printrequisition/error";
		}
		return "printrequisition/requisitionform";
	}

	@RequestMapping(value = "/bill", method = RequestMethod.POST)
	public String updateRequisitionForBill(final ModelMap model, final HttpServletRequest request, 
			final Locale locale, @Valid @ModelAttribute("domain") PrintRequisition printRequisition) {
		String isPrintRequisitionSent = request.getParameter("isPrintRequisitionSent");
		if(isPrintRequisitionSent!=null) {
			if(Boolean.parseBoolean(isPrintRequisitionSent) != true) {
				if(request.getParameterValues("optionalFieldsForDocket")!=null) {
					String optionalFieldsForDocket = "";
					for(int i=0; i<request.getParameterValues("optionalFieldsForDocket").length;i++) {
						if(i==request.getParameterValues("optionalFieldsForDocket").length-1) {
							optionalFieldsForDocket += request.getParameterValues("optionalFieldsForDocket")[i];
						} else {
							optionalFieldsForDocket += request.getParameterValues("optionalFieldsForDocket")[i] + "#";
						}
					}
					if(!optionalFieldsForDocket.isEmpty()) {
						printRequisition.setOptionalFieldsForDocket(optionalFieldsForDocket);
					} else {
						printRequisition.setOptionalFieldsForDocket(null);
					}
				}
				if(printRequisition.getId()!=null) {
					printRequisition.merge();
					//handle stale state exception
					printRequisition = PrintRequisition.findById(PrintRequisition.class, printRequisition.getId());
				} else {					
					printRequisition.persist();
				}
				String operation = request.getParameter("operation");
				if(operation!=null) {
					if(operation.equals("send")) {
						Bill bill = Bill.findById(Bill.class, Long.parseLong(printRequisition.getDeviceId()));
						String endflag="";	
						String level="";
						Map<String,String> properties=new HashMap<String, String>();															
						ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
						properties=new HashMap<String, String>();					
						/**** Next user and usergroup ****/
						Status expectedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS, bill.getLocale());
						HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", printRequisition.getHouseType(),printRequisition.getLocale());
						int startingLevel = 1;						
						String strStartingUserGroup=request.getParameter("usergroup");
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
//											//set status here if it is going to be needed..
//											bill.setPrintRequisitionToPressPostAdmissionStatus(expectedStatus);
											WorkflowDetails.create(bill,houseTypeForWorkflow,false,printRequisition,task,ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW,nextUserGroupType,level);
											model.addAttribute("type", "sent");											
										}
									}
								}					
							}							
						}
					} else if(operation.equals("save")) {
						model.addAttribute("type", "saved");
					}
				}
			} else {
				String strPublishDateMarathi = request.getParameter("setPublishDateMarathi");
				if(strPublishDateMarathi!=null) {
					printRequisition.setPublishDateMarathi(FormaterUtil.formatStringToDate(strPublishDateMarathi, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
				}
				String strPublishDateEnglish = request.getParameter("setPublishDateEnglish");
				if(strPublishDateEnglish!=null) {
					printRequisition.setPublishDateEnglish(FormaterUtil.formatStringToDate(strPublishDateEnglish, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
				}
				String strPublishDateHindi = request.getParameter("setPublishDateHindi");
				if(strPublishDateHindi!=null) {
					printRequisition.setPublishDateHindi(FormaterUtil.formatStringToDate(strPublishDateHindi, ApplicationConstants.SERVER_DATEFORMAT, locale.toString()));
				}
				printRequisition.merge();
				model.addAttribute("type", "saved");
			}
		}		
		populatePrintRequisition(model, request, printRequisition);		
		return "printrequisition/requisitionform";
	}
	
	@RequestMapping(value = "/bill/getEndorsementCopies", method = RequestMethod.GET)
	public String getEndorsementCopiesForBill(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String selectedBillId = request.getParameter("deviceId");
		String status = request.getParameter("status");
		String houseRound = request.getParameter("houseRound");
		if(selectedBillId!=null&&status!=null&&houseRound!=null) {
			if(!selectedBillId.isEmpty()&&!houseRound.isEmpty()&&!status.isEmpty()) {
				try {
					Bill bill = Bill.findById(Bill.class, Long.parseLong(selectedBillId));
					if(bill!=null) {
						model.addAttribute("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
						Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
						printRequisitionIdentifiers.put("deviceId", selectedBillId);
						printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);				
						printRequisitionIdentifiers.put("status", status);
						if(houseRound.isEmpty()) {
							houseRound=null;
						}
						printRequisitionIdentifiers.put("houseRound", houseRound);
						PrintRequisition printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, locale.toString());
						if(printRequisition!=null) {
							populateEndorsementCopies(model, request, printRequisition);
						} else {
							logger.error("**** There is no print requisition for this endorsement copies request. ****");
							model.addAttribute("errorcode", "PRINT_REQUISITION_NOTSET");
							return "printrequisition/error";
						}						
					} else {
						logger.error("**** Check request parameter 'deviceId' for invalid value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return "printrequisition/error";
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'deviceId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
				}				
			} else {
				logger.error("**** Check request parameter 'deviceId', 'requisitionFor', and 'status' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'deviceId', 'requisitionFor', 'status', and 'houseRound' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");
			return "printrequisition/error";
		}
		return "printrequisition/endorsementCopiesForm";
	}
	
	@RequestMapping(value = "/bill/sendForEndorsement", method = RequestMethod.GET)
	public String sendGreenCopyForEndorsement(final ModelMap model, final HttpServletRequest request, final Locale locale) {
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
						return "printrequisition/error";
					}
				}								
			} else {
				logger.error("**** Check request parameter 'houseType' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'houseType' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "printrequisition/error";
		}		
		/**** Selected Bill ****/
		Bill selectedBill = null;
		if(selectedBillIdStr!=null) {
			if(!selectedBillIdStr.isEmpty()) {
				try {
					long selectedBillId = Long.parseLong(selectedBillIdStr);
					selectedBill = Bill.findById(Bill.class, selectedBillId);
					if(selectedBill!=null) {
						if(selectedBill.getNumber()!=null) {
							model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
							model.addAttribute("selectedBillId", selectedBill.getId());
							/**** Requisition Statuses Allowed For Selected Bill In Selected House ****/
							String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(selectedBill, currentHouseType.getType());
							CustomParameter sendGreenCopyForEndorsementStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_SENDGREENCOPYFORENDORSEMENT_STATUSOPTIONS", "");
							if(sendGreenCopyForEndorsementStatusParameter!=null) {
								if(sendGreenCopyForEndorsementStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = sendGreenCopyForEndorsementStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseType.getType() + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> sendGreenCopyForEndorsementStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									model.addAttribute("sendGreenCopyForEndorsementStatuses", sendGreenCopyForEndorsementStatuses);
								} else {
									logger.error("Custom Parameter 'BILL_SENDGREENCOPYFORENDORSEMENT_STATUSOPTIONS' is not set properly");
									model.addAttribute("errorcode", "bill_sendgreencopyforendorsement_statusoptions_setincorrect");
									return "printrequisition/error";
								}
							} else {
								logger.error("Custom Parameter 'BILL_SENDGREENCOPYFORENDORSEMENT_STATUSOPTIONS' is not set");
								model.addAttribute("errorcode", "bill_sendgreencopyforendorsement_statusoptions_notset");
								return "printrequisition/error";
							}
						}						
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'billId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
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
						return "printrequisition/error";
					}
				} else {
					logger.error("**** Check request parameter 'year' for empty value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
					return "printrequisition/error";
				}
			} else {
				logger.error("**** Check request parameter 'year' for null value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
				return "printrequisition/error";
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
					return "printrequisition/error";
				}				
			} else {
				logger.error("custom parameter 'BILL_HOUSEORDERS' is not set properly.");
				model.addAttribute("errorcode", "bill_houseorders_setincorrect");
				return "printrequisition/error";
			}	
		} else {
			logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
			model.addAttribute("errorcode", "bill_houseorders_notset");
			return "printrequisition/error";
		}
		model.addAttribute("domain", new PrintRequisition());
		return "printrequisition/requestforendorsementbill";
	}
	
	@RequestMapping(value = "/bill/transmitEndorsementCopies", method = RequestMethod.GET)
	public String transmitEndorsementCopies(final ModelMap model, final HttpServletRequest request, final Locale locale) {
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
						return "printrequisition/error";
					}
				}								
			} else {
				logger.error("**** Check request parameter 'houseType' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'houseType' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "printrequisition/error";
		}
		/**** Selected Bill ****/
		Bill selectedBill = null;
		if(selectedBillIdStr!=null) {
			if(!selectedBillIdStr.isEmpty()) {
				try {
					long selectedBillId = Long.parseLong(selectedBillIdStr);
					selectedBill = Bill.findById(Bill.class, selectedBillId);
					if(selectedBill!=null) {
						if(selectedBill.getNumber()!=null) {
							model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
							model.addAttribute("selectedBillId", selectedBill.getId());
							/**** Requisition Statuses Allowed For Selected Bill In Selected House ****/
							String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(selectedBill, currentHouseType.getType());
							CustomParameter transmitEndorsementCopiesStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSMITENDORSEMENTCOPIES_STATUSOPTIONS", "");
							if(transmitEndorsementCopiesStatusParameter!=null) {
								if(transmitEndorsementCopiesStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = transmitEndorsementCopiesStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseType.getType() + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> transmitEndorsementCopiesStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									model.addAttribute("transmitEndorsementCopiesStatuses", transmitEndorsementCopiesStatuses);
								} else {
									logger.error("Custom Parameter 'BILL_TRANSMITENDORSEMENTCOPIES_STATUSOPTIONS' is not set properly");
									model.addAttribute("errorcode", "bill_transmitendorsementcopies_statusoptions_setincorrect");
									return "printrequisition/error";
								}
							} else {
								logger.error("Custom Parameter 'BILL_TRANSMITENDORSEMENTCOPIES_STATUSOPTIONS' is not set");
								model.addAttribute("errorcode", "bill_transmitendorsementcopies_statusoptions_notset");
								return "printrequisition/error";
							}
						}						
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'billId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
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
						return "printrequisition/error";
					}
				} else {
					logger.error("**** Check request parameter 'year' for empty value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
					return "printrequisition/error";
				}
			} else {
				logger.error("**** Check request parameter 'year' for null value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
				return "printrequisition/error";
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
					return "printrequisition/error";
				}				
			} else {
				logger.error("custom parameter 'BILL_HOUSEORDERS' is not set properly.");
				model.addAttribute("errorcode", "bill_houseorders_setincorrect");
				return "printrequisition/error";
			}	
		} else {
			logger.error("custom parameter 'BILL_HOUSEORDERS' is not set."); 
			model.addAttribute("errorcode", "bill_houseorders_notset");
			return "printrequisition/error";
		}
		model.addAttribute("domain", new PrintRequisition());
		return "printrequisition/transmitendorsementcopiesbill";
	}
	
	@RequestMapping(value = "/bill/transmitPressCopies", method = RequestMethod.GET)
	public String transmitPressCopies(final ModelMap model, final HttpServletRequest request, final Locale locale) {
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
						return "printrequisition/error";
					}
				}								
			} else {
				logger.error("**** Check request parameter 'houseType' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'houseType' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "printrequisition/error";
		}
		/**** Selected Bill ****/
		Bill selectedBill = null;
		if(selectedBillIdStr!=null) {
			if(!selectedBillIdStr.isEmpty()) {
				try {
					long selectedBillId = Long.parseLong(selectedBillIdStr);
					selectedBill = Bill.findById(Bill.class, selectedBillId);
					if(selectedBill!=null) {
						if(selectedBill.getNumber()!=null) {
							model.addAttribute("selectedBillNumber", FormaterUtil.formatNumberNoGrouping(selectedBill.getNumber(), locale.toString()));
							model.addAttribute("selectedBillId", selectedBill.getId());
							/**** Requisition Statuses Allowed For Selected Bill In Selected House ****/
							String currentHouseOrder = Bill.findHouseOrderOfGivenHouseForBill(selectedBill, currentHouseType.getType());
							CustomParameter transmitPressCopiesStatusParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSMITPRESSCOPIES_STATUSOPTIONS", "");
							if(transmitPressCopiesStatusParameter!=null) {
								if(transmitPressCopiesStatusParameter.getValue()!=null) {									
									StringBuffer filteredStatusTypes = new StringBuffer("");
									String[] statusTypesArr = transmitPressCopiesStatusParameter.getValue().split(",");
									for(String i: statusTypesArr) {
										System.out.println(filteredStatusTypes.toString());
										if(!i.trim().isEmpty()) {
											if(i.trim().endsWith(ApplicationConstants.BILL_FIRST_HOUSE) || i.trim().endsWith(ApplicationConstants.BILL_SECOND_HOUSE)) {
												if(i.trim().endsWith(currentHouseType.getType() + "_" + currentHouseOrder)) {
													filteredStatusTypes.append(i.trim()+",");							
												}
											} else {
												filteredStatusTypes.append(i.trim()+",");
											}					
										}				
									}
									filteredStatusTypes.deleteCharAt(filteredStatusTypes.length()-1);	
									List<Status> transmitPressCopiesStatuses = Status.findStatusContainedIn(filteredStatusTypes.toString(), locale.toString(), ApplicationConstants.ASC);
									model.addAttribute("transmitPressCopiesStatuses", transmitPressCopiesStatuses);
								} else {
									logger.error("Custom Parameter 'BILL_TRANSMITPRESSCOPIES_STATUSOPTIONS' is not set properly");
									model.addAttribute("errorcode", "bill_transmitpresscopies_statusoptions_setincorrect");
									return "printrequisition/error";
								}
							} else {
								logger.error("Custom Parameter 'BILL_TRANSMITPRESSCOPIES_STATUSOPTIONS' is not set");
								model.addAttribute("errorcode", "bill_transmitpresscopies_statusoptions_notset");
								return "printrequisition/error";
							}
						}						
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'billId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
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
						return "printrequisition/error";
					}
				} else {
					logger.error("**** Check request parameter 'year' for empty value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
					return "printrequisition/error";
				}
			} else {
				logger.error("**** Check request parameter 'year' for null value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
				return "printrequisition/error";
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
					return "printrequisition/error";
				}				
			} else {
				logger.error("custom parameter 'BILL_HOUSEORDERS' is not set properly.");
				model.addAttribute("errorcode", "bill_houseorders_setincorrect");
				return "printrequisition/error";
			}	
		} else {
			logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
			model.addAttribute("errorcode", "bill_houseorders_notset");
			return "printrequisition/error";
		}
		model.addAttribute("domain", new PrintRequisition());
		return "printrequisition/transmitpresscopiesbill";
	}
	
	@RequestMapping(value = "/bill/getPressCopies", method = RequestMethod.GET)
	public String getPressCopiesForBill(final ModelMap model, final HttpServletRequest request, final Locale locale) {
		String selectedBillId = request.getParameter("deviceId");
		String status = request.getParameter("status");
		String houseRound = request.getParameter("houseRound");
		if(selectedBillId!=null&&status!=null&&houseRound!=null) {
			if(!selectedBillId.isEmpty()&&!houseRound.isEmpty()&&!status.isEmpty()) {
				try {
					Bill bill = Bill.findById(Bill.class, Long.parseLong(selectedBillId));
					if(bill!=null) {
						model.addAttribute("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);
						Map<String, String> printRequisitionIdentifiers = new HashMap<String, String>();
						printRequisitionIdentifiers.put("deviceId", selectedBillId);
						printRequisitionIdentifiers.put("requisitionFor", ApplicationConstants.BILL_PRESS_COPY);				
						printRequisitionIdentifiers.put("status", status);
						if(houseRound.isEmpty()) {
							houseRound=null;
						}
						printRequisitionIdentifiers.put("houseRound", houseRound);
						PrintRequisition printRequisition = PrintRequisition.findByFieldNames(PrintRequisition.class, printRequisitionIdentifiers, locale.toString());
						if(printRequisition!=null) {
							populateTransmitPressCopies(model, request, printRequisition);
						} else {
							logger.error("**** There is no print requisition for this press copies request. ****");
							model.addAttribute("errorcode", "PRINT_REQUISITION_NOTSET");
							return "printrequisition/error";
						}						
					} else {
						logger.error("**** Check request parameter 'deviceId' for invalid value. ****");
						model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
						return "printrequisition/error";
					}
				} catch(NumberFormatException ne) {
					logger.error("**** Check request parameter 'deviceId' for non-numeric value. ****");
					model.addAttribute("errorcode", "REQUEST_PARAMETER_INVALID");
					return "printrequisition/error";
				}				
			} else {
				logger.error("**** Check request parameter 'deviceId', 'requisitionFor', and 'status' for empty value. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");
				return "printrequisition/error";
			}
		} else {
			logger.error("**** Check request parameter 'deviceId', 'requisitionFor', 'status', and 'houseRound' for null value. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");
			return "printrequisition/error";
		}
		return "printrequisition/pressCopiesForm";
	}
	
	private void populateTransmitPressCopies(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		populatePrintRequisitionForPressCopies(model, request, printRequisition);
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_TRANSMITPRESSCOPIES);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		List<WorkflowDetails> workflowsOfTransmitPress = WorkflowDetails.findAllByFieldNames(WorkflowDetails.class, workflowIdentifiers, "id", ApplicationConstants.ASC, printRequisition.getLocale());
		if(workflowsOfTransmitPress!=null) {
			if(!workflowsOfTransmitPress.isEmpty()) {
				model.addAttribute("isAlreadyTransmitted", true);
				CustomParameter finalAuthorityParameter = CustomParameter.findByName(CustomParameter.class, "BILL_TRANSMITPRESSCOPIES_FINAL_AUTHORITY", "");
				if(finalAuthorityParameter!=null) {
					WorkflowDetails finalActorWorkflowDetails = null;
					for(WorkflowDetails workflowDetails: workflowsOfTransmitPress) {
						if(finalAuthorityParameter.getValue().contains(workflowDetails.getAssigneeUserGroupType())) {
							finalActorWorkflowDetails = workflowDetails;
						}
					}
					if(finalActorWorkflowDetails!=null) {
						if(finalActorWorkflowDetails.getStatus().equals(ApplicationConstants.MYTASK_COMPLETED)) {
							model.addAttribute("isTransmissionAcknowledged",true);
							model.addAttribute("isHardCopyReceived",finalActorWorkflowDetails.getIsHardCopyReceived());
							if(finalActorWorkflowDetails.getDateOfHardCopyReceived()!=null) {
								Date dateOfHardCopyReceived = FormaterUtil.formatStringToDate(finalActorWorkflowDetails.getDateOfHardCopyReceived(), ApplicationConstants.SERVER_DATEFORMAT, finalActorWorkflowDetails.getLocale());
								model.addAttribute("dateOfHardCopyReceived", FormaterUtil.formatDateToString(dateOfHardCopyReceived, ApplicationConstants.SERVER_DATEFORMAT, finalActorWorkflowDetails.getLocale()));
							}	
							if(finalActorWorkflowDetails.getAcknowledgementDecision()!=null) {
								Status acknowledgementDecisionStatus = Status.findByType(finalActorWorkflowDetails.getAcknowledgementDecision(), finalActorWorkflowDetails.getLocale());
								model.addAttribute("formattedAcknowledgementDecision",acknowledgementDecisionStatus.getName());
							}
						} else {
							model.addAttribute("isTransmissionAcknowledged",false);
						}
					}
				} else {
					logger.error("Custom parameter 'BILL_TRANSMITPRESSCOPIES_FINAL_AUTHORITY' is not set.");
					model.addAttribute("errorcode", "BILL_TRANSMITPRESSCOPIES_FINAL_AUTHORITY_NOTSET");					
				}
			}
		}
	}
	
	private void populatePrintRequisitionForPressCopies(final ModelMap model, final HttpServletRequest request, final PrintRequisition printRequisition) {
		if(printRequisition!=null) {
			model.addAttribute("printRequisition", printRequisition);
			if(printRequisition.getPressCopyEnglish()!=null) {
				if(!printRequisition.getPressCopyEnglish().isEmpty()) {
					model.addAttribute("pressCopyEnglish", printRequisition.getPressCopyEnglish());
					model.addAttribute("pressCopiesReceived", "yes");
				}
			}
			if(printRequisition.getPressCopyMarathi()!=null) {
				if(!printRequisition.getPressCopyMarathi().isEmpty()) {
					model.addAttribute("pressCopyMarathi", printRequisition.getPressCopyMarathi());
					if(!model.containsAttribute("pressCopiesReceived")) {
						model.addAttribute("pressCopiesReceived", "yes");
					}
				}
			}
			if(printRequisition.getPressCopyHindi()!=null) {
				if(!printRequisition.getPressCopyHindi().isEmpty()) {
					model.addAttribute("pressCopyHindi", printRequisition.getPressCopyHindi());
					if(!model.containsAttribute("pressCopiesReceived")) {
						model.addAttribute("pressCopiesReceived", "yes");
					}
				}
			}
		}
		Map<String, String> workflowIdentifiers = new HashMap<String, String>();
		workflowIdentifiers.put("deviceId", printRequisition.getDeviceId());
		workflowIdentifiers.put("workflowType", ApplicationConstants.REQUISITION_TO_PRESS_WORKFLOW);
		workflowIdentifiers.put("workflowSubType", ApplicationConstants.BILL_FINAL_PRINT_REQUISITION_TO_PRESS);
		workflowIdentifiers.put("printRequisitionId", String.valueOf(printRequisition.getId()));
		WorkflowDetails workflowForPrintRequisition = WorkflowDetails.findByFieldNames(WorkflowDetails.class, workflowIdentifiers, printRequisition.getLocale());
		if(workflowForPrintRequisition!=null) {
			model.addAttribute("isPrintRequisitionSent", true);				
		}		
	}
	
	@RequestMapping(value = "/sendForEndorsement", method = RequestMethod.POST)
	public String sendForEndorsement(final ModelMap model, final HttpServletRequest request, 
			final Locale locale, @Valid @ModelAttribute("domain") PrintRequisition printRequisition) {
		String isAlreadySentForEndorsement = request.getParameter("isAlreadySentForEndorsement");		
		if(Boolean.parseBoolean(isAlreadySentForEndorsement) != true) {			
			Bill bill = Bill.findById(Bill.class, Long.parseLong(printRequisition.getDeviceId()));
			String endflag="";	
			String level="";
			Map<String,String> properties=new HashMap<String, String>();															
			ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
			properties=new HashMap<String, String>();					
			/**** Next user and usergroup ****/
			Status expectedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_SENDFORENDORSEMENT, bill.getLocale());
			HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", printRequisition.getHouseType(),printRequisition.getLocale());
			int startingLevel = 1;						
			String strStartingUserGroup=request.getParameter("usergroup");
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
//											//set status here if it is going to be needed..
//											bill.setPrintRequisitionToPressPostAdmissionStatus(expectedStatus);
								WorkflowDetails.create(bill,houseTypeForWorkflow,false,printRequisition,task,ApplicationConstants.SEND_FOR_ENDORSEMENT_WORKFLOW,nextUserGroupType,level);
								model.addAttribute("type", "sent");											
							}
						}
					}					
				}				
			}
		}				
		populateSendForEndorsement(model, request, printRequisition);	
		return "printrequisition/endorsementCopiesForm";
	}
	
	@RequestMapping(value = "/transmitEndorsementCopies", method = RequestMethod.POST)
	public String transmitEndorsementCopies(final ModelMap model, final HttpServletRequest request, 
			final Locale locale, @Valid @ModelAttribute("domain") PrintRequisition printRequisition) {
		String isAlreadySentForEndorsement = request.getParameter("isAlreadyTransmitted");		
		if(Boolean.parseBoolean(isAlreadySentForEndorsement) != true) {			
			Bill bill = Bill.findById(Bill.class, Long.parseLong(printRequisition.getDeviceId()));
			String endflag="";	
			String level="";
			Map<String,String> properties=new HashMap<String, String>();															
			ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
			properties=new HashMap<String, String>();					
			/**** Next user and usergroup ****/
			Status expectedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_TRANSMITENDORSEMENTCOPIES, bill.getLocale());
			HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", printRequisition.getHouseType(),printRequisition.getLocale());
			int startingLevel = 1;						
			String strStartingUserGroup=request.getParameter("usergroup");
			if(expectedStatus!=null && strStartingUserGroup!=null) {
				UserGroup startingUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strStartingUserGroup));
				List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(bill,houseTypeForWorkflow,true,expectedStatus,startingUserGroup,startingLevel,bill.getLocale());
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
//											//set status here if it is going to be needed..
//											bill.setPrintRequisitionToPressPostAdmissionStatus(expectedStatus);
								WorkflowDetails.create(bill,houseTypeForWorkflow,true,printRequisition,task,ApplicationConstants.TRANSMIT_ENDORSEMENT_COPIES_WORKFLOW,nextUserGroupType,level);
								model.addAttribute("type", "transmitted");											
							}
						}
					}					
				}				
			}
		}				
		populateTransmitEndorsementCopies(model, request, printRequisition);	
		return "printrequisition/endorsementCopiesForm";
	}
	
	@RequestMapping(value = "/transmitPressCopies", method = RequestMethod.POST)
	public String transmitPressCopies(final ModelMap model, final HttpServletRequest request, 
			final Locale locale, @Valid @ModelAttribute("domain") PrintRequisition printRequisition) {
		String isAlreadySentForPress = request.getParameter("isAlreadyTransmitted");		
		if(Boolean.parseBoolean(isAlreadySentForPress) != true) {			
			Bill bill = Bill.findById(Bill.class, Long.parseLong(printRequisition.getDeviceId()));
			String endflag="";	
			String level="";
			Map<String,String> properties=new HashMap<String, String>();															
			ProcessDefinition processDefinition=processService.findProcessDefinitionByKey(ApplicationConstants.APPROVAL_WORKFLOW);
			properties=new HashMap<String, String>();					
			/**** Next user and usergroup ****/
			Status expectedStatus = Status.findByType(ApplicationConstants.BILL_FINAL_TRANSMITPRESSCOPIES, bill.getLocale());
			HouseType houseTypeForWorkflow = HouseType.findByFieldName(HouseType.class, "type", printRequisition.getHouseType(),printRequisition.getLocale());
			int startingLevel = 1;						
			String strStartingUserGroup=request.getParameter("usergroup");
			if(expectedStatus!=null && strStartingUserGroup!=null) {
				UserGroup startingUserGroup=UserGroup.findById(UserGroup.class,Long.parseLong(strStartingUserGroup));
				List<Reference> eligibleActors = WorkflowConfig.findBillActorsVO(bill,houseTypeForWorkflow,true,expectedStatus,startingUserGroup,startingLevel,bill.getLocale());
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
//											//set status here if it is going to be needed..
//											bill.setPrintRequisitionToPressPostAdmissionStatus(expectedStatus);
								WorkflowDetails.create(bill,houseTypeForWorkflow,true,printRequisition,task,ApplicationConstants.TRANSMIT_PRESS_COPIES_WORKFLOW,nextUserGroupType,level);
								model.addAttribute("type", "transmitted");											
							}
						}
					}					
				}				
			}
		}				
		populateTransmitPressCopies(model, request, printRequisition);	
		return "printrequisition/pressCopiesForm";
	}
	
}
