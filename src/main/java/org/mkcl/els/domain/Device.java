package org.mkcl.els.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.vo.DeviceBallotVO;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Device extends BaseDomain {
	
	private static transient volatile Boolean isCurrentNumberForDevicesUpdateRequired = true;
	
	/** 
     * update static current numbers for all devices
     */
    public static void updateCurrentNumberForDevices() {
    	if(Device.isCurrentNumberForDevicesUpdateRequired) {
    		CustomParameter applicationRunningMode = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.APPLICATION_RUNNING_MODE, "");
    		if(applicationRunningMode!=null && applicationRunningMode.getValue()!=null && applicationRunningMode.getValue().equals(ApplicationConstants.APPLICATION_RUNNING_MODE_LOCAL)) {
    			return;
    		}
    		try {
        		HouseType lowerHouseType = HouseType.findByType(ApplicationConstants.LOWER_HOUSE, ApplicationConstants.DEFAULT_LOCALE);
            	HouseType upperHouseType = HouseType.findByType(ApplicationConstants.UPPER_HOUSE, ApplicationConstants.DEFAULT_LOCALE);
            	
            	Session latestLowerHouseSession = null;
            	Session latestUpperHouseSession = null;
            	
            	DeviceType starredQuestionDeviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType hdqQuestionDeviceType = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType nongovResolutionDeviceType = DeviceType.findByType(ApplicationConstants.NONOFFICIAL_RESOLUTION, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType govResolutionDeviceType = DeviceType.findByType(ApplicationConstants.GOVERNMENT_RESOLUTION, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType callingAttentionMotionDeviceType = DeviceType.findByType(ApplicationConstants.MOTION_CALLING_ATTENTION, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType standaloneMotionDeviceType = DeviceType.findByType(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType budgetaryCutMotionDeviceType = DeviceType.findByType(ApplicationConstants.MOTIONS_CUTMOTION_BUDGETARY, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType supplementaryCutMotionDeviceType = DeviceType.findByType(ApplicationConstants.MOTIONS_CUTMOTION_SUPPLEMENTARY, ApplicationConstants.DEFAULT_LOCALE);
            	DeviceType adjournmentMotionDeviceType = DeviceType.findByType(ApplicationConstants.ADJOURNMENT_MOTION, ApplicationConstants.DEFAULT_LOCALE);
            	
            	Integer number = null;
            	
            	/** update lowerhouse static current number for starred, unstarred and short notice questions **/
            	
            	if (Question.getStarredCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, starredQuestionDeviceType);
            		number = Question.assignQuestionNo(lowerHouseType, latestLowerHouseSession, starredQuestionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
        			Question.updateStarredCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for starred, unstarred and short notice questions **/
            	if (Question.getStarredCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, starredQuestionDeviceType);
            		number = Question.assignQuestionNo(upperHouseType, latestUpperHouseSession, starredQuestionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
        			Question.updateStarredCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for half hour discussion from questions **/
            	if (Question.getHDQCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, hdqQuestionDeviceType);
            		number = Question.assignQuestionNo(lowerHouseType, latestLowerHouseSession, hdqQuestionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
        			Question.updateHDQCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for half hour discussion from questions **/
            	if (Question.getHDQCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, hdqQuestionDeviceType);
            		number = Question.assignQuestionNo(upperHouseType, latestUpperHouseSession, hdqQuestionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
        			Question.updateHDQCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for non government resolutions **/
            	
            	if (Resolution.getResolutionNonGovCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, nongovResolutionDeviceType);
            		number = Resolution.assignResolutionNo(lowerHouseType, latestLowerHouseSession, nongovResolutionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		Resolution.updateResolutionNonGovCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for non government resolutions **/
            	if (Resolution.getResolutionNonGovCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, nongovResolutionDeviceType);
            		number = Resolution.assignResolutionNo(upperHouseType, latestUpperHouseSession, nongovResolutionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		Resolution.updateResolutionNonGovCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for government resolutions **/
            	if (Resolution.getResolutionGovCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, govResolutionDeviceType);
            		number = Resolution.assignResolutionNo(lowerHouseType, latestLowerHouseSession, govResolutionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		Resolution.updateResolutionGovCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for government resolutions **/
            	if (Resolution.getResolutionGovCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, govResolutionDeviceType);
            		number = Resolution.assignResolutionNo(upperHouseType, latestUpperHouseSession, govResolutionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		Resolution.updateResolutionGovCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for calling attention motions **/
            	if (Motion.getCallingAttentionCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, callingAttentionMotionDeviceType);
            		number = Motion.assignMotionNo(lowerHouseType, latestLowerHouseSession, callingAttentionMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		Motion.updateCallingAttentionCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for calling attention motions **/
            	if (Motion.getCallingAttentionCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, callingAttentionMotionDeviceType);
            		number = Motion.assignMotionNo(upperHouseType, latestUpperHouseSession, callingAttentionMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		Motion.updateCallingAttentionCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for standalone motions **/
            	if (StandaloneMotion.getHDSCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, standaloneMotionDeviceType);
            		number = StandaloneMotion.assignStandaloneMotionNo(lowerHouseType, latestLowerHouseSession, standaloneMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		StandaloneMotion.updateHDSCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for standalone motions **/
            	if (StandaloneMotion.getHDSCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, standaloneMotionDeviceType);
            		number = StandaloneMotion.assignStandaloneMotionNo(upperHouseType, latestUpperHouseSession, standaloneMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		StandaloneMotion.updateHDSCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for budgetary cutmotions **/
            	if (CutMotion.getBudgetaryCutMotionCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, budgetaryCutMotionDeviceType);
            		number = CutMotion.assignCutMotionNo(lowerHouseType, latestLowerHouseSession, budgetaryCutMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		CutMotion.updateBudgetaryCutMotionCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for budgetary cutmotions **/
            	if (CutMotion.getBudgetaryCutMotionCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, budgetaryCutMotionDeviceType);
            		number = CutMotion.assignCutMotionNo(upperHouseType, latestUpperHouseSession, budgetaryCutMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		CutMotion.updateBudgetaryCutMotionCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for supplementary cutmotions **/
            	if (CutMotion.getSupplementaryCutMotionCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, supplementaryCutMotionDeviceType);
            		number = CutMotion.assignCutMotionNo(lowerHouseType, latestLowerHouseSession, supplementaryCutMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		CutMotion.updateSupplementaryCutMotionCurrentNumberLowerHouse(number);
        		}
            	
            	/** update upperhouse static current number for supplementary cutmotions **/
            	if (CutMotion.getSupplementaryCutMotionCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, supplementaryCutMotionDeviceType);
            		number = CutMotion.assignCutMotionNo(upperHouseType, latestUpperHouseSession, supplementaryCutMotionDeviceType, ApplicationConstants.DEFAULT_LOCALE);
            		CutMotion.updateSupplementaryCutMotionCurrentNumberUpperHouse(number);
        		}
            	
            	/** update lowerhouse static current number for adjournment motions **/
            	if (AdjournmentMotion.getCurrentNumberLowerHouse() == 0) {
            		latestLowerHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(lowerHouseType, adjournmentMotionDeviceType);
            		Date defaultAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(latestLowerHouseSession);
            		number = AdjournmentMotion.assignMotionNo(lowerHouseType, defaultAdjourningDate, ApplicationConstants.DEFAULT_LOCALE);
					AdjournmentMotion.updateCurrentNumberLowerHouse(number);
					AdjournmentMotion.updateCurrentAdjourningDateLowerHouse(defaultAdjourningDate);
        		}
            	
            	/** update upperhouse static current number for adjournment motions **/
            	if (AdjournmentMotion.getCurrentNumberUpperHouse() == 0) {
            		latestUpperHouseSession = Session.findLatestSessionHavingGivenDeviceTypeEnabled(upperHouseType, adjournmentMotionDeviceType);
            		Date defaultAdjourningDate = AdjournmentMotion.findDefaultAdjourningDateForSession(latestUpperHouseSession);
            		number = AdjournmentMotion.assignMotionNo(upperHouseType, defaultAdjourningDate, ApplicationConstants.DEFAULT_LOCALE);
					AdjournmentMotion.updateCurrentNumberUpperHouse(number);
					AdjournmentMotion.updateCurrentAdjourningDateUpperHouse(defaultAdjourningDate);
        		}
            	
            	Device.isCurrentNumberForDevicesUpdateRequired(false);
        	} catch (ELSException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }    	
    }
	
	public static void startDeviceWorkflow(final String deviceName, final Long deviceId, final Status status, final UserGroupType userGroupType, final Integer level, final String workflowHouseType, final Boolean isFlowOnRecomStatusAfterFinalDecision, String locale) throws ELSException {
		
		if(deviceName.split("_")[0].toUpperCase().equals("QUESTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			Question question = Question.findById(Question.class, deviceId);
			question.startWorkflow(question, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("MOTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			Motion motion = Motion.findById(Motion.class, deviceId);
			motion.startWorkflow(motion, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("STANDALONEMOTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			StandaloneMotion standaloneMotion = StandaloneMotion.findById(StandaloneMotion.class, deviceId);
			standaloneMotion.startWorkflow(standaloneMotion, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("RESOLUTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			Resolution resolution = Resolution.findById(Resolution.class, deviceId);
			resolution.startWorkflow(resolution, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("CUTMOTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			CutMotion cutMotion = CutMotion.findById(CutMotion.class, deviceId);
			cutMotion.startWorkflow(cutMotion, status, userGroupType, level, workflowHouseType, isFlowOnRecomStatusAfterFinalDecision, locale);
		
		}
		
	}
	
	public static void endDeviceWorkflow(String deviceName, Long deviceId, String workflowHouseType, String locale) throws ELSException {
		
		if(deviceName.split("_")[0].toUpperCase().equals("QUESTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			Question question = Question.findById(Question.class, deviceId);
			question.endWorkflow(question, workflowHouseType, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("MOTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			Motion motion = Motion.findById(Motion.class, deviceId);
			motion.endWorkflow(motion, workflowHouseType, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("STANDALONEMOTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			StandaloneMotion standaloneMotion = StandaloneMotion.findById(StandaloneMotion.class, deviceId);
			standaloneMotion.endWorkflow(standaloneMotion, workflowHouseType, locale);
		
		} else if(deviceName.split("_")[0].toUpperCase().equals("RESOLUTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			Resolution resolution = Resolution.findById(Resolution.class, deviceId);
			resolution.endWorkflow(resolution, workflowHouseType, locale);
			
		} else if(deviceName.split("_")[0].toUpperCase().equals("CUTMOTION")) { //conventionally it is same as 'device field value till first underscore in uppercase' in corresponding devicetype of given device
			CutMotion cutMotion = CutMotion.findById(CutMotion.class, deviceId);
			cutMotion.endWorkflow(cutMotion, workflowHouseType, locale);
		
		}
		
	}
	
	/**** update flag for 'current number for devices' update is required or not ****/
	public static void isCurrentNumberForDevicesUpdateRequired(Boolean flag){
		synchronized (Device.isCurrentNumberForDevicesUpdateRequired) {
			Device.isCurrentNumberForDevicesUpdateRequired = flag;			
		}
	}
	
	public String findYaadiLayingStatus() {
    	return YaadiDetails.findYaadiLayingStatus(this);
    }
	
	public static DeviceBallotVO findBallotRelatedInformation(String deviceName, Long deviceId, String locale) throws ELSException {
		DeviceBallotVO deviceBallotVO = null;
		
		Map<String, String[]> queryParameters = new HashMap<String, String[]>();
		queryParameters.put("locale", new String[] {locale});
		queryParameters.put("deviceName", new String[] {deviceName});
		queryParameters.put("deviceId", new String[] {deviceId.toString()});
		
		@SuppressWarnings("unchecked")
		List<DeviceBallotVO> resultList = Query.findResultListOfGivenClass("DEVICE_BALLOT_RELATED_INFORMATION", queryParameters, DeviceBallotVO.class);
		
		if(resultList!=null && resultList.isEmpty()) {
			deviceBallotVO = resultList.get(0);
		}
		
		return deviceBallotVO;
	}
	
	public static String findBallotInformationText(String deviceType, Long deviceId, String locale) throws ELSException {
		String ballotInformation = "";
		
		Map<String, String[]> queryParameters = new HashMap<String, String[]>();
		queryParameters.put("locale", new String[] {locale});
		queryParameters.put("deviceId", new String[] {deviceId.toString()});
		
		@SuppressWarnings("rawtypes")
		List resultList = Query.findReport(deviceType.toUpperCase()+"_BALLOT_INFORMATION_TEXT", queryParameters);
		
		if(resultList!=null && !resultList.isEmpty()) {
			ballotInformation = (String) resultList.get(0);
		}
		
		return ballotInformation;
	}

}
