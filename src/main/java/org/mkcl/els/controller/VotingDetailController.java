/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.TitleController.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Status;
import org.mkcl.els.domain.Title;
import org.mkcl.els.domain.VotingDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class VotingDetailController.
 *
 * @author dhananjayb
 * @since v1.0.0
 */
@Controller
@RequestMapping("/votingdetail")
public class VotingDetailController extends GenericController<VotingDetail> {
	
	@Override
	protected void populateNew(final ModelMap model, final VotingDetail domain,
            final String locale, final HttpServletRequest request) {
        domain.setLocale(locale);
        String deviceId=request.getParameter("deviceId");
		String houseType = request.getParameter("houseType");
		String houseRound = request.getParameter("houseRound");
		String deviceType = request.getParameter("deviceType");		
		String votingFor = request.getParameter("votingFor");
		/**** House Type ****/
		if(houseType!=null) {
			if(!houseType.isEmpty()) {
				HouseType selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, locale.toString());
				if(selectedHouseType==null) {
					selectedHouseType = HouseType.findById(HouseType.class, Long.parseLong(houseType));
				}
				if(selectedHouseType!=null) {
					model.addAttribute("formattedHouseType", selectedHouseType.getName());
					model.addAttribute("houseType", selectedHouseType.getType());
				}
			}
		}
		Integer houseRoundsAvailable = null;
		/**** Device Type ****/
		if(deviceType!=null) {
			if(!deviceType.isEmpty()) {
				DeviceType selectedDeviceType = DeviceType.findByFieldName(DeviceType.class, "type", deviceType, locale.toString());
				if(selectedDeviceType==null) {
					selectedDeviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
				}
				if(selectedDeviceType!=null) {
					model.addAttribute("formattedDeviceType", selectedDeviceType.getName());
					model.addAttribute("deviceType", selectedDeviceType.getType());
					/**** Device ****/
					if(deviceId!=null) {
						if(!deviceId.isEmpty()) {
							model.addAttribute("deviceId", Long.parseLong(deviceId));							
						}					
					}
					if(selectedDeviceType.getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
						/**** House Rounds Available For Bill ****/
						CustomParameter billHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
						if(billHouseRoundsParameter!=null) {
							houseRoundsAvailable = Integer.parseInt(billHouseRoundsParameter.getValue());							
						} else {
							logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
							model.addAttribute("errorcode", "bill_houseorders_notset");
							return;
						}
					}
				}				
			}
		}	
		if(houseRoundsAvailable==null) {
			/**** House Rounds Available By Default ****/
			CustomParameter defaultHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "DEFAULT_HOUSEROUNDS", "");
			if(defaultHouseRoundsParameter!=null) {
				houseRoundsAvailable = Integer.parseInt(defaultHouseRoundsParameter.getValue());
			} else {
				logger.error("custom parameter 'DEFAULT_HOUSEROUNDS' is not set.");
				model.addAttribute("errorcode", "default_houseorders_notset");		
				return;
			}						
		}
		/**** Populate House Rounds ****/
		int selectedHouseRound = 0;
		if(houseRound!=null) {
			if(!houseRound.isEmpty()) {
				selectedHouseRound = Integer.parseInt(houseRound);
			}
		}
		List<MasterVO> houseRoundVOs = new ArrayList<MasterVO>();
		for(int i=1; i<=houseRoundsAvailable; i++) {
			MasterVO houseRoundVO = new MasterVO();
			houseRoundVO.setName(FormaterUtil.formatNumberNoGrouping(i, locale.toString()));
			houseRoundVO.setValue(String.valueOf(i));
			if(Integer.parseInt(houseRoundVO.getValue())==selectedHouseRound) {
				houseRoundVO.setIsSelected(true);
			}				
			houseRoundVOs.add(houseRoundVO);
		}
		model.addAttribute("houseRoundVOs", houseRoundVOs);			
		/**** Voting For ****/
		if(votingFor!=null) {
			if(!votingFor.isEmpty()) {
				model.addAttribute("votingFor", votingFor);	
			} else {
				logger.error("request parameter 'votingFor' is empty.");
				model.addAttribute("errorcode", "votingFor_empty");				
			}						
		} else {
			logger.error("request parameter 'votingFor' is null.");
			model.addAttribute("errorcode", "votingFor_null");	
			return;
		}
		/**** Voting Decision ****/
		CustomParameter votingDecisionParameter = CustomParameter.findByName(CustomParameter.class, "VOTING_DECISIONS", "");
		if(votingDecisionParameter!=null) {
			List<Status> votingDecisionStatuses=Status.findStatusContainedIn(votingDecisionParameter.getValue(), domain.getLocale(), ApplicationConstants.ASC);
			model.addAttribute("votingDecisionStatuses", votingDecisionStatuses);
		} else {
			logger.error("custom parameter 'VOTING_DECISIONS' is not set.");
			model.addAttribute("errorcode", "voting_decisions_notset");		
			return;
		}
		/**** Open through Overlay OR Master ****/
		model.addAttribute("openThroughOverlay", request.getParameter("openThroughOverlay"));		
    }
	
	@Override
	protected void populateEdit(final ModelMap model, final VotingDetail domain,
            final HttpServletRequest request) {
        String houseType = domain.getHouseType();
        String deviceId = domain.getDeviceId();
		Integer houseRound = domain.getHouseRound();
		String deviceType = domain.getDeviceType();		
		/**** House Type ****/
		if(houseType!=null) {
			if(!houseType.isEmpty()) {	
				HouseType selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, domain.getLocale());
				model.addAttribute("formattedHouseType", selectedHouseType.getName());
				model.addAttribute("houseType", selectedHouseType.getType());
			}
		}
		Integer houseRoundsAvailable = null;
		/**** Device Type ****/
		if(deviceType!=null) {
			if(!deviceType.isEmpty()) {
				DeviceType selectedDeviceType = DeviceType.findByFieldName(DeviceType.class, "type", deviceType, domain.getLocale());
				if(selectedDeviceType!=null) {
					model.addAttribute("formattedDeviceType", selectedDeviceType.getName());
					model.addAttribute("deviceType", selectedDeviceType.getType());
					/**** Device ****/
					if(deviceId!=null) {
						if(!deviceId.isEmpty()) {
							model.addAttribute("deviceId", Long.parseLong(deviceId));							
						}					
					}
					if(selectedDeviceType.getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
						/**** House Rounds Available For Bill ****/
						CustomParameter billHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
						if(billHouseRoundsParameter!=null) {
							houseRoundsAvailable = Integer.parseInt(billHouseRoundsParameter.getValue());							
						} else {
							logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
							model.addAttribute("errorcode", "bill_houseorders_notset");
							return;
						}
					}
				}				
			}
		}	
		if(houseRoundsAvailable==null) {
			/**** House Rounds Available By Default ****/
			CustomParameter defaultHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "DEFAULT_HOUSEROUNDS", "");
			if(defaultHouseRoundsParameter!=null) {
				houseRoundsAvailable = Integer.parseInt(defaultHouseRoundsParameter.getValue());
			} else {
				logger.error("custom parameter 'DEFAULT_HOUSEROUNDS' is not set.");
				model.addAttribute("errorcode", "default_houseorders_notset");		
				return;
			}						
		}
		/**** Populate House Rounds ****/
		int selectedHouseRound = 0;		
		if(houseRound!=null) {
			selectedHouseRound = houseRound;
		}
		List<MasterVO> houseRoundVOs = new ArrayList<MasterVO>();
		for(int i=1; i<=houseRoundsAvailable; i++) {
			MasterVO houseRoundVO = new MasterVO();
			houseRoundVO.setName(FormaterUtil.formatNumberNoGrouping(i, domain.getLocale()));
			houseRoundVO.setValue(String.valueOf(i));
			if(Integer.parseInt(houseRoundVO.getValue())==selectedHouseRound) {
				houseRoundVO.setIsSelected(true);
			}				
			houseRoundVOs.add(houseRoundVO);
		}
		model.addAttribute("houseRoundVOs", houseRoundVOs);	
		/**** Voting For ****/
		model.addAttribute("votingFor", domain.getVotingFor());
		/**** Voting Decision ****/
		CustomParameter votingDecisionParameter = CustomParameter.findByName(CustomParameter.class, "VOTING_DECISIONS", "");
		if(votingDecisionParameter!=null) {
			List<Status> votingDecisionStatuses=Status.findStatusContainedIn(votingDecisionParameter.getValue(), domain.getLocale(), ApplicationConstants.ASC);
			model.addAttribute("votingDecisionStatuses", votingDecisionStatuses);
			model.addAttribute("selectedDecision", domain.getDecision());
		} else {
			logger.error("custom parameter 'VOTING_DECISIONS' is not set.");
			model.addAttribute("errorcode", "voting_decisions_notset");		
			return;
		}
		/**** Open through Overlay OR Master ****/
		if(domain.getVotingFor().equals(ApplicationConstants.VOTING_FOR_PASSING_OF_BILL)) {
			model.addAttribute("openThroughOverlay", "yes");
		}		
    }
	
	@Override
	protected void customValidateCreate(final VotingDetail domain, final BindingResult result,
			final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	@Override
	protected void customValidateUpdate(final VotingDetail domain, final BindingResult result,
			final HttpServletRequest request) {
		customValidate(domain, result, request);
	}
	
	private void customValidate(final VotingDetail domain, final BindingResult result,
			final HttpServletRequest request) {
		// Check for version mismatch
        if (domain.isVersionMismatch()) {
            result.rejectValue("VersionMismatch", "version");
        }
	}
	
	@Override
	protected void populateAfterCreate(final ModelMap model, final VotingDetail domain,
			final HttpServletRequest request) {
		if(domain.getDeviceId()!=null) {
			if(domain.getDeviceType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
				Bill bill = Bill.findById(Bill.class, Long.parseLong(domain.getDeviceId()));
				if(bill!=null) {
					List<VotingDetail> votingDetailsForBill = new ArrayList<VotingDetail>();
					if(bill.getVotingDetails()!=null) {
						if(!bill.getVotingDetails().isEmpty()) {
							votingDetailsForBill.addAll(bill.getVotingDetails());
						}						
					}
					votingDetailsForBill.add(domain);
					bill.setVotingDetails(votingDetailsForBill);
					bill.simpleMerge();
				}
			}
		}
	}
	
	@RequestMapping(value="/editVotingDetailsForDevice", method=RequestMethod.GET)
	public String editVotingDetailsForDevice(final ModelMap model, final HttpServletRequest request, final Locale locale){
		String deviceId=request.getParameter("deviceId");
		String houseType = request.getParameter("houseType");
		String deviceType = request.getParameter("deviceType");		
		String votingFor = request.getParameter("votingFor");
		if(deviceId!=null&&houseType!=null&&deviceType!=null&&votingFor!=null) {
			if(!deviceId.isEmpty()&&!houseType.isEmpty()&&!deviceType.isEmpty()&&!votingFor.isEmpty()) {
				/**** House Type ****/
				HouseType selectedHouseType = HouseType.findByFieldName(HouseType.class, "type", houseType, locale.toString());
				if(selectedHouseType==null) {
					selectedHouseType = HouseType.findById(HouseType.class, Long.parseLong(houseType));
				}
				if(selectedHouseType!=null) {
					model.addAttribute("formattedHouseType", selectedHouseType.getName());
					model.addAttribute("houseType", selectedHouseType.getType());
				}
				/**** Device Type ****/
				DeviceType selectedDeviceType = DeviceType.findByFieldName(DeviceType.class, "type", deviceType, locale.toString());
				if(selectedDeviceType==null) {
					selectedDeviceType = DeviceType.findById(DeviceType.class, Long.parseLong(deviceType));
				}
				if(selectedDeviceType!=null) {
					model.addAttribute("formattedDeviceType", selectedDeviceType.getName());
					model.addAttribute("deviceType", selectedDeviceType.getType());
					/**** Device ****/					
					model.addAttribute("deviceId", Long.parseLong(deviceId));
					List<VotingDetail> votingDetailsForDevice = null; 
					if(selectedDeviceType.getType().startsWith(ApplicationConstants.DEVICE_BILLS)) {
						/**** House Rounds Available For Bill ****/
						CustomParameter billHouseRoundsParameter = CustomParameter.findByName(CustomParameter.class, "BILL_HOUSEROUNDS", "");
						if(billHouseRoundsParameter!=null) {
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
						} else {
							logger.error("custom parameter 'BILL_HOUSEORDERS' is not set.");
							model.addAttribute("errorcode", "bill_houseorders_notset");
							return "votingdetail/error";
						}
						/**** Voting For ****/
						model.addAttribute("votingFor", votingFor);
						/**** Populate Voting Decisions ****/
						CustomParameter votingDecisionParameter = CustomParameter.findByName(CustomParameter.class, "VOTING_DECISIONS", "");
						if(votingDecisionParameter!=null) {
							List<Status> votingDecisionStatuses=Status.findStatusContainedIn(votingDecisionParameter.getValue(), locale.toString(), ApplicationConstants.ASC);
							model.addAttribute("votingDecisionStatuses", votingDecisionStatuses);
						} else {
							logger.error("custom parameter 'VOTING_DECISIONS' is not set.");
							model.addAttribute("errorcode", "voting_decisions_notset");		
							return "votingdetail/error";
						}
						/**** Voting Details For Bill In Given House ****/
						Bill bill = Bill.findById(Bill.class, Long.parseLong(deviceId));
						votingDetailsForDevice = VotingDetail.findByVotingForDeviceInGivenHouse(bill, selectedDeviceType, selectedHouseType, votingFor);
						if(votingDetailsForDevice==null) {
							votingDetailsForDevice = new ArrayList<VotingDetail>();
						}						
					}
					model.addAttribute("votingDetailsForDevice", votingDetailsForDevice);
				}
			} else {
				logger.error("**** Check request parameters 'deviceId', 'houseType', 'deviceType' and 'votingFor' for empty values. ****");
				model.addAttribute("errorcode", "REQUEST_PARAMETER_EMPTY");		
				return "votingdetail/error";
			}
		} else {
			logger.error("**** Check request parameters 'deviceId', 'houseType', 'deviceType' and 'votingFor' for null values. ****");
			model.addAttribute("errorcode", "REQUEST_PARAMETER_NULL");		
			return "votingdetail/error";
		}
		return "votingdetail/editfordevice";
	}
	
	@Transactional
	@RequestMapping(value="/editVotingDetailsForDevice",method=RequestMethod.POST)
	public @ResponseBody String updateVotingDetailsForDevice(final ModelMap model, final HttpServletRequest request, final Locale locale){
		String result="";
		String deviceId=request.getParameter("deviceId");
		String houseType = request.getParameter("houseType");				
		String deviceType = request.getParameter("deviceType");		
		String votingFor = request.getParameter("votingFor");
		String numberOfVotingDetailsForDeviceStr = request.getParameter("numberOfVotingDetailsForDevice");
		if(numberOfVotingDetailsForDeviceStr!=null) {
			if(!numberOfVotingDetailsForDeviceStr.isEmpty()) {
				int numberOfVotingDetailsForDevice = Integer.parseInt(numberOfVotingDetailsForDeviceStr);
				if(numberOfVotingDetailsForDevice>0) {					
					if(deviceId!=null&&houseType!=null&&deviceType!=null&&votingFor!=null) {
						if(!deviceId.isEmpty()&&!houseType.isEmpty()&&!deviceType.isEmpty()&&!votingFor.isEmpty()) {							
							if(deviceType.startsWith(ApplicationConstants.DEVICE_BILLS)) {
								Bill bill = Bill.findById(Bill.class, Long.parseLong(deviceId));
								if(bill!=null) {		
									List<VotingDetail> votingDetails = new ArrayList<VotingDetail>();
									for(int i=1; i<=numberOfVotingDetailsForDevice; i++) {
										String votingDetailId = request.getParameter("id_"+i);
										if(votingDetailId!=null) {
											if(!votingDetailId.isEmpty()) {
												VotingDetail votingDetail = VotingDetail.findById(VotingDetail.class, Long.parseLong(votingDetailId));
												if(votingDetail!=null) {
													String houseRoundStr = request.getParameter("houseRound_"+i);
													if(houseRoundStr!=null) {
														if(!houseRoundStr.isEmpty()) {															
															votingDetail.setHouseRound(Integer.parseInt(houseRoundStr));
														}
													}
													String totalNumberOfVotersStr = request.getParameter("totalNumberOfVoters_"+i);
													if(totalNumberOfVotersStr!=null) {
														if(!totalNumberOfVotersStr.isEmpty()) {															
															votingDetail.setTotalNumberOfVoters(Integer.parseInt(totalNumberOfVotersStr));
														}
													}
													String actualNumberOfVotersStr = request.getParameter("actualNumberOfVoters_"+i);
													if(actualNumberOfVotersStr!=null) {
														if(!actualNumberOfVotersStr.isEmpty()) {															
															votingDetail.setActualNumberOfVoters(Integer.parseInt(actualNumberOfVotersStr));
														}
													}
													String votesInFavorStr = request.getParameter("votesInFavor_"+i);
													if(votesInFavorStr!=null) {
														if(!votesInFavorStr.isEmpty()) {															
															votingDetail.setVotesInFavor(Integer.parseInt(votesInFavorStr));
														}
													}
													String votesAgainstStr = request.getParameter("votesAgainst_"+i);
													if(votesAgainstStr!=null) {
														if(!votesAgainstStr.isEmpty()) {															
															votingDetail.setVotesAgainst(Integer.parseInt(votesAgainstStr));
														}
													}
													String decision = request.getParameter("decision_"+i);
													if(decision!=null) {
														if(!decision.isEmpty()) {															
															votingDetail.setDecision(decision);
														}
													}
													String isInDecorumStr = request.getParameter("isInDecorum_"+i);
//													if(isInDecorumStr!=null) {
//														if(!isInDecorumStr.isEmpty()) {															
//															votingDetail.setIsInDecorum(Boolean.parseBoolean(isInDecorumStr));
//														}
//													}
													votingDetail.setIsInDecorum(Boolean.parseBoolean(isInDecorumStr));
													votingDetails.add(votingDetail);
												}
											}
										}
									}
									if(!votingDetails.isEmpty()) {
										bill.getVotingDetails().removeAll(votingDetails);
										if(!bill.getVotingDetails().isEmpty()) {
											votingDetails.addAll(bill.getVotingDetails());
										}
										bill.setVotingDetails(votingDetails);
										bill.simpleMerge();
										result = "success";
									}
								}
							}
						}
					}
				}			
			}
		}		
		return result;				
	}
	
}
