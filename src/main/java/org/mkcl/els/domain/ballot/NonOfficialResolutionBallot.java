package org.mkcl.els.domain.ballot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.ResolutionBallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

public class NonOfficialResolutionBallot {

	//===============================================
	//
	//=============== VIEW METHODS ==================
	//
	//===============================================
	public static List<BallotVO> findPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
		//find if the preballot exists
		PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
		if(preBallot == null){
			List<Resolution> resolutions = 
					NonOfficialResolutionBallot.computeResolutionNonOfficial(session, answeringDate, locale);
			PreBallot preBallotResolution = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
			List<Member> uniqueMembers = new ArrayList<Member>();
			for(Resolution r : resolutions) {
				if(!uniqueMembers.contains(r.getMember())){
					BallotEntry ballotEntry = new BallotEntry();
					ballotEntry.setMember(r.getMember());
					ballotEntry.setLocale(r.getLocale());
					
					List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
					deviceSequence.add(new DeviceSequence(r, r.getLocale()));
					ballotEntry.setDeviceSequences(deviceSequence);
					preBallotEntries.add(ballotEntry);
					
					BallotVO preBallotVO = new BallotVO();
					CustomParameter customParameter = CustomParameter.
							findByName(CustomParameter.class, "RESOLUTION_PREBALLOT_MEMBERNAMEFORMAT", "");
					if(customParameter != null){
						preBallotVO.setMemberName(r.getMember().findNameInGivenFormat(customParameter.getValue()));
					}else{
						preBallotVO.setMemberName(r.getMember().findNameInGivenFormat("firstnamelastname"));
					}
					
					preBallotVO.setQuestionNumber(r.getNumber());
					preBallotVO.setQuestionSubject(r.getSubject());
					preBallotVOs.add(preBallotVO);
					uniqueMembers.add(r.getMember());
				}
			}
			//persist the preballot list
			preBallotResolution.setBallotEntries(preBallotEntries);
			preBallotResolution.persist();
		}else{
			CustomParameter customParamter = CustomParameter.
						findByName(CustomParameter.class, "ROIS_STARRED_LOWERHOUSE_PREBALLOT_RECREATE_IF_EXISTS", "");
			if(customParamter == null || customParamter.getValue().equals("YES")){
				Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
				if(ballot == null){
					//Delete the preballot and recreate the new preballot
					preBallot.optimizedRemove();
					List<Resolution> resolutions = 
							NonOfficialResolutionBallot.computeResolutionNonOfficial(session, answeringDate, locale);
					PreBallot preBallotResolution = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
					List<Member> uniqueMembers = new ArrayList<Member>();
					for(Resolution r : resolutions) {
						if(!uniqueMembers.contains(r.getMember())){
							BallotEntry ballotEntry = new BallotEntry();
							ballotEntry.setMember(r.getMember());
							ballotEntry.setLocale(r.getLocale());
							
							List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
							deviceSequence.add(new DeviceSequence(r, r.getLocale()));
							ballotEntry.setDeviceSequences(deviceSequence);
							preBallotEntries.add(ballotEntry);
							
							BallotVO preBallotVO = new BallotVO();
							CustomParameter customParameter = CustomParameter.
									findByName(CustomParameter.class, "RESOLUTION_PREBALLOT_MEMBERNAMEFORMAT", "");
							if(customParameter != null){
								preBallotVO.setMemberName(r.getMember().findNameInGivenFormat(customParameter.getValue()));
							}else{
								preBallotVO.setMemberName(r.getMember().findNameInGivenFormat("firstnamelastname"));
							}
							
							preBallotVO.setQuestionNumber(r.getNumber());
							preBallotVO.setQuestionSubject(r.getSubject());
							preBallotVOs.add(preBallotVO);
							uniqueMembers.add(r.getMember());
						}
					}
					//persist the preballot list
					preBallotResolution.setBallotEntries(preBallotEntries);
					preBallotResolution.persist();
				}else{
					Map<String, String[]> params = new HashMap<String, String[]>();
					params.put("locale", new String[]{locale});
					params.put("preBallotId", new String[]{preBallot.getId().toString()});
					List devices = Query.findReport("RESOLUTION_PREBALLOT_DEVICES", params);
					
					if(devices != null && !devices.isEmpty()){
						for(Object o : devices){
							Object[] obj = (Object[])o;
							
							BallotVO preBallotVO = new BallotVO();
							preBallotVO.setMemberName(obj[2].toString());
							preBallotVO.setQuestionNumber(new Integer(obj[1].toString()));
							preBallotVO.setQuestionSubject(obj[3].toString());
							preBallotVOs.add(preBallotVO);
						}
					}
//					preBallotEntries = preBallot.getBallotEntries();
//					preBallotVOs = PreBallot.getBallotVOFromBallotEntries(preBallotEntries, locale);
				}
			}else{
				Map<String, String[]> params = new HashMap<String, String[]>();
				params.put("locale", new String[]{locale});
				params.put("preBallotId", new String[]{preBallot.getId().toString()});
				List devices = Query.findReport("RESOLUTION_PREBALLOT_DEVICES", params);
				
				if(devices != null && !devices.isEmpty()){
					for(Object o : devices){
						Object[] obj = (Object[])o;
						
						BallotVO preBallotVO = new BallotVO();
						preBallotVO.setMemberName(obj[2].toString());
						preBallotVO.setQuestionNumber(new Integer(obj[1].toString()));
						preBallotVOs.add(preBallotVO);
					}
				}
//				preBallotEntries = preBallot.getBallotEntries();
//				preBallotVOs = PreBallot.getBallotVOFromBallotEntries(preBallotEntries, locale);
			}
		}

		
		return preBallotVOs;
	}
	
	public static List<BallotVO> findResolutionCouncilPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
	
		List<Member> members = NonOfficialResolutionBallot.computeMembersResolutionNonOfficial(session, true, answeringDate, locale);
		for(Member m : members) {
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(m.getFullname());
				preBallotVOs.add(preBallotVO);
		}	
		
		return preBallotVOs;
	}
	
	public static List<BallotMemberVO> findPreBallotMemberVOResolutionNonOfficial(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		
		List<Member> members = null;
		members = NonOfficialResolutionBallot.computeMembersResolutionNonOfficial(session, false, answeringDate, locale);
		for(Member m: members) {
			BallotMemberVO preBallotMemberVO = new BallotMemberVO();
			preBallotMemberVO.setMemberName(m.getFullname());
			
			preBallotMemberVOs.add(preBallotMemberVO);
		}
		
		return preBallotMemberVOs;
	}
	
	/**
	 * Use it post Ballot.
	 * 
	 * Returns null if Ballot does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no entries for the Ballot
	 * OR
	 * Returns a list of NonOfficialMemberSubjectCombo BallotVO.
	 * @throws ELSException 
	 *
	 */
	public static List<ResolutionBallotVO> findResolutionMemberSubjectBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<ResolutionBallotVO> ballotedVOs = new ArrayList<ResolutionBallotVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				ResolutionBallotVO ballotedVO = new ResolutionBallotVO();
				ballotedVO.setMemberName(entry.getMember().getFullname());
				for(DeviceSequence ds : entry.getDeviceSequences()){
					Device device = ds.getDevice();
					Long id = device.getId();
					Resolution resolution = Resolution.findById(Resolution.class, id);
					ballotedVO.setId(resolution.getId());
					if(resolution.getDiscussionStatus() != null){
						ballotedVO.setChecked("checked");
					}else{
						ballotedVO.setChecked("unchecked");
					}
					ballotedVO.setResolutionNumber(FormaterUtil.formatNumberNoGrouping(resolution.getNumber(), locale));
					ballotedVO.setResolutionSubject(resolution.getSubject());
					ballotedVO.setNoticeContent(resolution.getRevisedNoticeContent());
				}
				ballotedVOs.add(ballotedVO);
			}
		}
		else {
			ballotedVOs = null;
		}
		
		return ballotedVOs;
	}
	
	/**
	 * Use it post Ballot.
	 * 
	 * Returns null if Ballot does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no entries for the Ballot
	 * OR
	 * Returns a list of NonOfficialMemberSubjectCombo BallotVO.
	 * @throws ELSException 
	 *
	 */
	public static List<ResolutionBallotVO> createResolutionPatrakBhagTwo(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<ResolutionBallotVO> ballotedVOs = findResolutionMemberSubjectBallotVO(session, deviceType, answeringDate, locale);
		return ballotedVOs;
	}
	
	public static List<ResolutionBallotVO> createPatrakBhagTwo(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<ResolutionBallotVO> ballotedVOs = findMemberSubjectBallotVO(session, deviceType, answeringDate, locale);
		return ballotedVOs;
	}
	
	//===============================================
	//
	//=============== DOMAIN METHODS ================
	//
	//===============================================
	public static Ballot create(final Ballot ballot) throws ELSException {
		Session session = ballot.getSession();
		House house = session.getHouse();
		HouseType houseType = house.getType();
		
		String houseTypeType = houseType.getType();
		if(houseTypeType.equals(ApplicationConstants.LOWER_HOUSE)) {
			return NonOfficialResolutionBallot.createResolutionNonOfficialAssemblyBallot(ballot);
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			return NonOfficialResolutionBallot.createCouncilBallotResolutionNonOfficial(ballot);
		}
		else {
			throw new ELSException("StarredQuestionBallot.create/1", "Inappropriate houseType set in Session.");
		}
	}
	
	
	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	private static List<Resolution> computeResolutionNonOfficial(final Session session,
			final Date answeringDate,
			final String locale) throws ELSException {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.NONOFFICIAL_RESOLUTION, locale);
	
		Date startTime = FormaterUtil.formatStringToDate(session.
				getParameter(deviceType.getType() + "_submissionStartDate"), 
				datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.
				getParameter(deviceType.getType() + "_submissionEndDate"), 
				datePattern.getValue(), locale);
		
		Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale);
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED,REPEATADMITTED };
		
		// TODO: internal Status will only refer to the lifecycle of a Question in the Workflow
		// i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Questions
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<Resolution> resolutions = Resolution.find(session, deviceType, 
				answeringDate, internalStatuses, false, startTime, endTime, 
				ApplicationConstants.ASC, locale);
		
		return resolutions;
	}
	
	private static List<Member> computeMembersResolutionNonOfficial(final Session session,
			final Boolean isPreBallot,
			final Date answeringDate,
			final String locale) throws ELSException {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.NONOFFICIAL_RESOLUTION, locale);
	
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(
				deviceType.getType() + "_submissionStartDate"), 
				datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(
				deviceType.getType() + "_submissionEndDate"), 
				datePattern.getValue(), locale);
		
		Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale);
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED, REPEATADMITTED };
		
		// TODO: [FATAL] internal Status will only refer to the lifecycle of a Question in the 
		// Workflow i.e till ADMITTED. The further statuses of the Resolution viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Members
		// the condition should be: For all the active members who have submitted "resolution"
		// between the specified time window (start time & end time) &
		// whose resolutions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<Member> members = Resolution.findMembersAll(session, deviceType, 
				 answeringDate, internalStatuses, isPreBallot, startTime, endTime, 
				 ApplicationConstants.ASC, locale);
		
		return members;
	}
	
	private static Ballot createResolutionNonOfficialAssemblyBallot(final Ballot ballot) throws ELSException {
		return NonOfficialResolutionBallot.createBallotResolutionNonOfficial(ballot);
	}
	
	private static Ballot createBallotResolutionNonOfficial(final Ballot b) throws ELSException{
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = NonOfficialResolutionBallot.computeMembersResolutionNonOfficial(b.getSession(),
					false,
					b.getAnsweringDate(),
					b.getLocale());
			List<Member> randomizedList = NonOfficialResolutionBallot.randomizeMembers(computedList);
			
			//			// Read the constant 5 as a configurable parameter
//			CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, "name", 
//					ApplicationConstants.RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_ASSEMBLY, "");
//			Integer ballotOutputCount = null;
//			if(ballotOutputCountCustomParameter != null){
//				ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
//			}else{
//				ballotOutputCount = 5;
//			}
			
		//	List<Member> selectedList = NonOfficialResolutionBallot.selectMembersForBallot(randomizedList, ballotOutputCount);
			
			List<BallotEntry> ballotEntries = NonOfficialResolutionBallot.
						createResolutionNonOfficialBallotEntries(b.getSession(), b.getDeviceType(), b.getAnsweringDate(), randomizedList,	b.getLocale());
			b.setBallotEntries(ballotEntries);
			b.persist();
			return b;
		}
		else {
			return ballot;
		}
	}
	
	/**
	 * Does not shuffle in place, returns a new list.
	 */
	private static List<Member> randomizeMembers(final List<Member> members) {
		List<Member> newMembers = new ArrayList<Member>();
		newMembers.addAll(members);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newMembers, rnd);
		return newMembers;
	}
	
	/**
	 * A subset of eligible Members of size @param maxMembers are taken in Ballot.
	 */
	private static List<Member> selectMembersForBallot(final List<Member> members,
			final Integer maxMembers) {
		List<Member> selectedMList = new ArrayList<Member>();
		selectedMList.addAll(members);
		if(selectedMList.size() >= maxMembers) {
			selectedMList = selectedMList.subList(0, maxMembers); 
		}
		return selectedMList;
	}
	
	private static List<BallotEntry> createResolutionNonOfficialBallotEntries(final Session session, final DeviceType deviceType, final Date answeringDate, final List<Member> members,
			final String locale) throws ELSException {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		List<String> subjectList = new ArrayList<String>();
		List<Resolution> resolutions = Resolution.findDiscussedResolution(session, deviceType, locale);
		for(Resolution r : resolutions){
			subjectList.add(r.getSubject());
		}
		
		CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, "name", 
				ApplicationConstants.RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_ASSEMBLY, "");
		Integer ballotOutputCount = null;
		if(ballotOutputCountCustomParameter != null){
			ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
		}else{
			ballotOutputCount = 5;
		}
		
		for(Member m : members) {
			Resolution resolution = Resolution.getResolutionForMemberOfUniqueSubject(session, deviceType, answeringDate, m.getId(), subjectList, locale);
			// Read the constant 5 as a configurable parameter
			/**** Update the resolution's discussionDate ****/
			if(resolution != null && ballotEntries.size() < ballotOutputCount){
//				resolution.setDiscussionDate(answeringDate);
				Status ballotedStatus = Status.findByType(ApplicationConstants.RESOLUTION_PROCESSED_BALLOTED, locale);
				resolution.setBallotStatus(ballotedStatus);
				/**** Here the intimation to the member should be sent ****/
				resolution.merge();
				subjectList.add(resolution.getSubject());
			
				BallotEntry ballotEntry = new BallotEntry();
				ballotEntry.setMember(m);
				List<DeviceSequence> deviceSequences = Ballot.createDeviceSequences(resolution, locale);
				ballotEntry.setDeviceSequences(deviceSequences);
				ballotEntries.add(ballotEntry);
			}
		}
		return ballotEntries;
	}
	
	private static Ballot createCouncilBallotResolutionNonOfficial(final Ballot ballot) throws ELSException {
		return NonOfficialResolutionBallot.createMemberBallotResolutionNonOfficial(ballot);
	}
	
	/**
	 * Assumption: 
	 * internalStatus of Question will increment in the following manner:
	 * ADMITTED -> BALLOTED -> DISCUSSED
	 * 
	 * Algorithm:
	 * 1> Compute Questions: Find all the Questions submitted between start 
	 * time & end time, with device type = RESOLUTION NONOFFICIAL", 
	 * internal status = "ADMITTED" & parent = null (don't consider clubbed 
	 * questions)
	 * 
	 * 2> Randomize the list of Questions obtained in step 1.
	 * 
	 * 3> Pick 2 (configurable) questions from the randomized list in step 2.
	 * @throws ELSException 
	 */
	private static Ballot createMemberBallotResolutionNonOfficial(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = NonOfficialResolutionBallot.computeMembersEligibleForTheBallot(b.getSession(), 
					b.getDeviceType(), b.getAnsweringDate(), ApplicationConstants.ASC, b.getLocale());
					
			List<Member> randomizedList = NonOfficialResolutionBallot.randomizeMembers(computedList);
			
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCP = CustomParameter.findByFieldName(CustomParameter.class, "name", 
					ApplicationConstants.RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_COUNCIL, "");
			Integer ballotOutput = null;
			if(ballotOutputCountCP == null){
				ballotOutput = new Integer(5);
			}else{
				ballotOutput = new Integer(ballotOutputCountCP.getValue());
			}
			
			List<Member> selectedList = NonOfficialResolutionBallot.selectMembersForBallot(randomizedList, ballotOutput);

			List<BallotEntry> ballotEntries = NonOfficialResolutionBallot.createMemberBallotEntries(selectedList, b.getLocale());
			b.setBallotEntries(ballotEntries);
			b.persist();
			
			return b;
		}
		else {
			return ballot;
		}
	}
	
	private static List<Member> computeMembersEligibleForTheBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String sortOrder,
			final String locale) throws ELSException{
		
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
	
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionStartDate"), datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionEndDate"), datePattern.getValue(), locale);
		
		Status ADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale);
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale);
		
		
		Status[] internalStatuses = new Status[] { ADMITTED,REPEATADMITTED };
				
		List<Member> members = Resolution.findMembersEligibleForTheBallot(session, deviceType, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale); 
		
		return members;
	}
	
	private static List<BallotEntry> createMemberBallotEntries(final List<Member> members,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		for(Member m : members) {
			BallotEntry ballotEntry = new BallotEntry(m, locale);
			ballotEntries.add(ballotEntry);
		}
		return ballotEntries;
	}
	
	/**
	 * Use it post Ballot.
	 * 
	 * Returns null if Ballot does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no entries for the Ballot
	 * OR
	 * Returns a list of NonOfficialMemberSubjectCombo BallotVO.
	 * @throws ELSException 
	 *
	 */
	private static List<ResolutionBallotVO> findMemberSubjectBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<ResolutionBallotVO> ballotedVOs = new ArrayList<ResolutionBallotVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				ResolutionBallotVO ballotedVO = new ResolutionBallotVO();
				ballotedVO.setMemberName(entry.getMember().findFirstLastName());
				for(DeviceSequence ds : entry.getDeviceSequences()){
					Device device = ds.getDevice();
					Long id = device.getId();
					Resolution resolution = Resolution.findById(Resolution.class, id);
					ballotedVO.setId(resolution.getId());
					if(resolution.getDiscussionStatus() != null){
						ballotedVO.setChecked("checked");
					}else{
						ballotedVO.setChecked("unchecked");
					}
					ballotedVO.setResolutionNumber(FormaterUtil.formatNumberNoGrouping(resolution.getNumber(), locale));
					ballotedVO.setResolutionSubject(resolution.getSubject());
					if(resolution.getRevisedNoticeContent().isEmpty()){
						ballotedVO.setNoticeContent(resolution.getNoticeContent().replaceAll("\\<.*?>",""));
					}else{
						ballotedVO.setNoticeContent(resolution.getRevisedNoticeContent().replaceAll("\\<.*?>",""));
					}
				}
				ballotedVOs.add(ballotedVO);
			}
		}else {
			ballotedVOs = null;
		}
		
		return ballotedVOs;
	}

}
