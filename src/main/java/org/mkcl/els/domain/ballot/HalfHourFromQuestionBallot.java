package org.mkcl.els.domain.ballot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

public class HalfHourFromQuestionBallot {

	//=================================================
	//
	//=============== VIEW METHODS ====================
	//
	//=================================================
	public static List<BallotVO> findPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		
		PreBallot preBallotHDAssembly = PreBallot.find(session, deviceType, answeringDate, locale);
		List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
		
		if(preBallotHDAssembly == null){
		
			PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
			List<Question> questions = 
					HalfHourFromQuestionBallot.computeQuestionsForHalfHour(session, deviceType, answeringDate, false, true, locale);
			
			List<Question> newQuestionList = new ArrayList<Question>();
			for(Question q : questions){
				if(q.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
					newQuestionList.add(q);
				}
			}
			for(Question q : newQuestionList) {
				
				{
					BallotEntry ballotEntry = new BallotEntry();
					ballotEntry.setMember(q.getPrimaryMember());
					ballotEntry.setLocale(q.getLocale());
					
					List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
					deviceSequence.add(new DeviceSequence(q, q.getLocale()));
					ballotEntry.setDeviceSequences(deviceSequence);
					preBallotEntries.add(ballotEntry);
					
					deviceSequence = null;
					ballotEntry = null;
				}
				
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
				preBallotVO.setQuestionNumber(q.getNumber());
				preBallotVO.setQuestionSubject(q.getSubject());
				
				preBallotVOs.add(preBallotVO);
			}
			
			Collections.sort(preBallotEntries, new Comparator<BallotEntry>() {
				@Override
				public int compare(BallotEntry b1, BallotEntry b2){
					if(!b1.getDeviceSequences().isEmpty() && !b2.getDeviceSequences().isEmpty()){
						return ((Question)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((Question)b2.getDeviceSequences().get(0).getDevice()).getNumber());
					}
					return 0;
				}
			});
			
			Collections.sort(preBallotVOs, new Comparator<BallotVO>(){
				@Override
				public int compare(BallotVO b1, BallotVO b2){
					return b1.getQuestionNumber().compareTo(b2.getQuestionNumber());
				}
			});			
			
			//persist the preballot list
			newPreBallot.setBallotEntries(preBallotEntries);
			newPreBallot.persist();
		}else{
			CustomParameter cspt = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() + "_PREBALLOT_RECREATE_IF_EXISTS", "");
			if(cspt == null || cspt.getValue().equals("YES")){
				Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
				if(ballot == null){
					preBallotHDAssembly.remove();
					
					PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
					List<Question> questions = 
							HalfHourFromQuestionBallot.computeQuestionsForHalfHour(session, deviceType, answeringDate, false, true, locale);
					
					List<Question> newQuestionList = new ArrayList<Question>();
					for(Question q : questions){
						if(q.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
							newQuestionList.add(q);
						}
					}
					for(Question q : newQuestionList) {
						
						{
							BallotEntry ballotEntry = new BallotEntry();
							ballotEntry.setMember(q.getPrimaryMember());
							ballotEntry.setLocale(q.getLocale());
							
							List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
							deviceSequence.add(new DeviceSequence(q, q.getLocale()));
							ballotEntry.setDeviceSequences(deviceSequence);
							preBallotEntries.add(ballotEntry);
							
							deviceSequence = null;
							ballotEntry = null;
						}
						
						BallotVO preBallotVO = new BallotVO();
						preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
						preBallotVO.setQuestionNumber(q.getNumber());
						preBallotVO.setQuestionSubject(q.getSubject());
						
						preBallotVOs.add(preBallotVO);
					}
					
					Collections.sort(preBallotEntries, new Comparator<BallotEntry>() {
						@Override
						public int compare(BallotEntry b1, BallotEntry b2){
							if(!b1.getDeviceSequences().isEmpty() && !b2.getDeviceSequences().isEmpty()){
								return ((Question)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((Question)b2.getDeviceSequences().get(0).getDevice()).getNumber());
							}
							return 0;
						}
					});
					
					Collections.sort(preBallotVOs, new Comparator<BallotVO>(){
						@Override
						public int compare(BallotVO b1, BallotVO b2){
							return b1.getQuestionNumber().compareTo(b2.getQuestionNumber());
						}
					});			
					
					//persist the preballot list
					newPreBallot.setBallotEntries(preBallotEntries);
					newPreBallot.persist();
					
				}else{
					if(!preBallotHDAssembly.getBallotEntries().isEmpty()){
						for(BallotEntry be : preBallotHDAssembly.getBallotEntries()){
							for(DeviceSequence ds : be.getDeviceSequences()){
								if(ds.getDevice() != null){
									if(ds.getDevice() instanceof Question) {
										Question q = (Question) ds.getDevice();
										BallotVO preBallotVO = new BallotVO();
										preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
										preBallotVO.setQuestionNumber(q.getNumber());
										preBallotVO.setQuestionSubject(q.getSubject());
										
										preBallotVOs.add(preBallotVO);
									}
								}
							}
						}
					}
				}
			}else{
				if(!preBallotHDAssembly.getBallotEntries().isEmpty()){
					for(BallotEntry be : preBallotHDAssembly.getBallotEntries()){
						for(DeviceSequence ds : be.getDeviceSequences()){
							if(ds.getDevice() != null){
								if(ds.getDevice() instanceof Question) {
									Question q = (Question) ds.getDevice();
									BallotVO preBallotVO = new BallotVO();
									preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
									preBallotVO.setQuestionNumber(q.getNumber());
									preBallotVO.setQuestionSubject(q.getSubject());
									
									preBallotVOs.add(preBallotVO);
								}
							}
						}
					}
				}
			}
		}
		
		return preBallotVOs;
	}
	
	public static List<BallotMemberVO> previewPreBallotHDQAssembly(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot == null){
				List<Member> members = HalfHourFromQuestionBallot.computeMembers(session,deviceType, answeringDate, false, locale);
				for(Member m: members) {
					BallotMemberVO preBallotMemberVO = new BallotMemberVO();
					preBallotMemberVO.setMemberName(m.getFullname());
					preBallotMemberVOs.add(preBallotMemberVO);
				}
			}else{
				preBallotMemberVOs.addAll(HalfHourFromQuestionBallot.getBallotMembers(preBallot.getBallotEntries()));
			}
		}
		
		return preBallotMemberVOs;
	}
	
	
	
	public static List<BallotMemberVO> findPreBallotHDQAssembly(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			
			PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot == null){
				List<Member> members = HalfHourFromQuestionBallot.computeMembers(session,deviceType, answeringDate, false, locale);
				
				preBallot = new PreBallot(session, deviceType, group, answeringDate, new Date(), locale);
				List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
								
				for(Member m: members) {
					
					BallotMemberVO preBallotMemberVO = new BallotMemberVO();
					preBallotMemberVO.setMemberName(m.getFullname());
					preBallotMemberVOs.add(preBallotMemberVO);
					
					BallotEntry preBallotEntry = new BallotEntry(m, locale);
					preBallotEntries.add(preBallotEntry);
				}
				
				preBallot.setBallotEntries(preBallotEntries);
				
				preBallot.persist();
			}else{
				CustomParameter cspt = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() +"_PREBALLOT_RECREATE_IF_EXISTS","");
				if(cspt == null || cspt.getValue().equals("YES")){
					Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
					if(ballot == null){
						preBallot.remove();
						
						
						PreBallot newPreBallot = new PreBallot(session, deviceType, group, answeringDate, new Date(), locale);
						List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
						List<Member> members = HalfHourFromQuestionBallot.computeMembers(session, deviceType, answeringDate, false, locale);
						
						for(Member m: members) {
							
							BallotMemberVO preBallotMemberVO = new BallotMemberVO();
							preBallotMemberVO.setMemberName(m.getFullname());
							preBallotMemberVOs.add(preBallotMemberVO);
							
							BallotEntry preBallotEntry = new BallotEntry(m, locale);
							preBallotEntries.add(preBallotEntry);
						}
						
						newPreBallot.setBallotEntries(preBallotEntries);
						
						newPreBallot.persist();
						
					}else{
						preBallotMemberVOs.addAll(HalfHourFromQuestionBallot.getBallotMembers(preBallot.getBallotEntries()));
					}
				}else{
					preBallotMemberVOs.addAll(HalfHourFromQuestionBallot.getBallotMembers(preBallot.getBallotEntries()));
				}
			}
		}
		
		return preBallotMemberVOs;
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
			return HalfHourFromQuestionBallot.createHDQAssemblyBallot(ballot);
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			return HalfHourFromQuestionBallot.createHDQCouncilBallot(ballot);
		}
		else {
			throw new ELSException("StarredQuestionBallot.create/1", "Inappropriate houseType set in Session.");
		}
	}
	
	public static Ballot createMemberBallotHDQAssembly(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = HalfHourFromQuestionBallot.computeMembers(b.getSession(), b.getDeviceType(), 
					b.getAnsweringDate(), true, b.getLocale());
					
			List<Member> randomizedList = HalfHourFromQuestionBallot.randomizeMembers(computedList);
			
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCP = CustomParameter.findByFieldName(CustomParameter.class, "name", 
					ApplicationConstants.HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL, null);
			Integer ballotOutput = null;
			if(ballotOutputCountCP == null){
				ballotOutput = new Integer(3);
			}else{
				ballotOutput = new Integer(ballotOutputCountCP.getValue());
			}
			
			List<Member> selectedList = HalfHourFromQuestionBallot.selectMembersForBallot(randomizedList, ballotOutput);
			
			List<BallotEntry> ballotEntries = HalfHourFromQuestionBallot.createMemberBallotEntries(selectedList, b.getLocale());
			b.setBallotEntries(ballotEntries);
			b.persist();
			return b;
		}
		else {
			return ballot;
		}
	}
	
	public static void updateMemberBallot(final Ballot ballot,
			final Member member,
			final Question question) throws IllegalAccessException, ELSException {
		BallotEntry ballotEntry = HalfHourFromQuestionBallot.findBallotEntry(member, ballot.getSession(),
				ballot.getDeviceType(), ballot.getAnsweringDate(), ballot.getLocale());
		List<DeviceSequence> questionSequences = 
			Ballot.createDeviceSequences(question, ballot.getLocale());
		ballotEntry.setDeviceSequences(questionSequences);
		ballotEntry.merge();
	}
	

	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	private static List<Question> computeQuestionsForHalfHour(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final String locale) throws ELSException {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
	
		Date startTime = FormaterUtil.formatStringToDate(session.
				getParameter(deviceType.getType() + "_submissionStartDate"), 
				datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.
				getParameter(deviceType.getType() + "_submissionEndDate"), 
				datePattern.getValue(), locale);
		
		Status ADMITTED = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED };
		
		// TODO: internal Status will only refer to the lifecycle of a Question in the Workflow
		// i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Questions
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<Question> questions = null;
		questions = Question.findByBallot(session, deviceType, answeringDate, internalStatuses, false, false, isMandatoryUnique, isPreBallot, startTime, endTime, ApplicationConstants.ASC, locale);

		return questions;
	}
	
	private static List<Member> computeMembers(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Boolean isUnique,
			final String locale) throws ELSException {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
	
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(
				deviceType.getType() + "_submissionStartDate"), 
				datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(
				deviceType.getType() + "_submissionEndDate"), 
				datePattern.getValue(), locale);
		
		// TODO: [FATAL] internal Status will only refer to the lifecycle of a Question in the 
		// Workflow i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Members
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<Member> members = null;		
		Status ADMITTED = null;

		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
			ADMITTED = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_FINAL_ADMISSION, locale);
		}
		
		Status[] internalStatuses = new Status[] { ADMITTED };
		
		if(isUnique.booleanValue()){
			members = Question.findPrimaryMembersByBallot(session, deviceType, answeringDate, internalStatuses, false, false, startTime, endTime, ApplicationConstants.ASC, locale);
		}
		else {
			members = Question.findPrimaryMembersForBallot(session, deviceType,answeringDate, internalStatuses, false, startTime, endTime,ApplicationConstants.ASC, locale);
		}
		
		return members;
	}
	
	private static List<BallotMemberVO> getBallotMembers(List<BallotEntry> ballotEntries){
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		for(BallotEntry be : ballotEntries){
			Member member = be.getMember();
			BallotMemberVO preBallotMemberVO = new BallotMemberVO();
			preBallotMemberVO.setMemberName(member.getFullname());
			preBallotMemberVOs.add(preBallotMemberVO);
		}
		
		return preBallotMemberVOs;
	}
	
	private static Ballot createHDQAssemblyBallot(final Ballot ballot) throws ELSException {
		return HalfHourFromQuestionBallot.createHDQAssemblyMemberBallot(ballot);
	}
	
	/**
	 * Algorithm:
	 * 1> Compute Members: Find all the Members who have submitted 
	 * Questions between start time & end time, with device type = 
	 * "half hour discussion from question" and internal status = 
	 * "ADMITTED"  & question.parent = null (don't consider clubbed 
	 * questions)
	 * 
	 * 2> Randomize the list of Members obtained in step 1.
	 * 
	 * 3> Pick 2 (configurable) Members from the randomized list in step 2.
	 * @throws ELSException 
	 */
	private static Ballot createHDQAssemblyMemberBallot(final Ballot b) throws ELSException {

		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, b.getDeviceType().getType().toUpperCase() + "_" + b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			
			if(csptUniqueFlag != null && csptUniqueFlag.getValue() != null && !csptUniqueFlag.getValue().isEmpty()){
				if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
					computedList = HalfHourFromQuestionBallot.computeMembers(b.getSession(),
								b.getDeviceType(),
								b.getAnsweringDate(),
								true,
								b.getLocale());
				}
			}
			
			// Read the constant 3 as a configurable parameter
			CustomParameter hdqAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(hdqAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCount = Integer.parseInt(hdqAssemblyBallotOutPutCount.getValue());
			
			List<Member> finalComputedList = getUniqueMembers(b.getSession(), b.getDeviceType(), computedList, "member");
			List<Member> newMemberList = new ArrayList<Member>();
						
			if(finalComputedList.size() < outPutCount){
				
				if(!finalComputedList.isEmpty()){
					Member m = finalComputedList.get(0);
					if(m.isActiveMemberOn(new Date(), b.getLocale())){
						newMemberList.add(m);
					}
				}
				
				finalComputedList = HalfHourFromQuestionBallot.computeMembers(b.getSession(),
						b.getDeviceType(),
						b.getAnsweringDate(),
						false,
						b.getLocale());
			}
			for(Member m : finalComputedList){
				if(m.isActiveMemberOn(new Date(), b.getLocale())){
					newMemberList.add(m);
				}
			}
			
			List<Member> randomizedList = HalfHourFromQuestionBallot.randomizeMembers(newMemberList);
			
			List<Member> selectedList = HalfHourFromQuestionBallot.selectMembersForBallot(randomizedList, outPutCount);
			
			List<BallotEntry> ballotEntries = HalfHourFromQuestionBallot.createMemberBallotEntries(selectedList, b.getLocale());
			b.setBallotEntries(ballotEntries);
			ballot = (Ballot) b.persist();	
		}
		
		return ballot;
	}
	
	private static List<Member> getUniqueMembers(final Session session, final DeviceType deviceType, final List<Member> members, final String memberNotice) {
		StringBuffer memberList = new StringBuffer("");
		String returnData = Question.findBallotedMembers(session, memberNotice, deviceType);
		memberList.append(( returnData == null)? "":returnData);
		List<Member> newMs = new ArrayList<Member>();
		if(!memberList.toString().isEmpty()){
			for(Member m : members){
				if(! HalfHourFromQuestionBallot.isExistingInList(memberList.toString(), m.getId().toString())){
					newMs.add(m);
				}
			}
		}else{
			newMs.addAll(members);
		}
		
		return newMs;
	}
	
	private static boolean isExistingInList(final String list, final String data){
		boolean retVal = false;
		if(list != null){
			if(data != null){
				retVal = list.contains(data);
			}
		}
		return retVal;
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
	
	private static List<BallotEntry> createMemberBallotEntries(final List<Member> members,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		for(Member m : members) {
			BallotEntry ballotEntry = new BallotEntry(m, locale);
			ballotEntries.add(ballotEntry);
		}
		return ballotEntries;
	}
	
	private static Ballot createHDQCouncilBallot(final Ballot ballot) throws ELSException {
		return HalfHourFromQuestionBallot.createHDQNoticeBallot(ballot);
	}
	
	/**
	 * Assumption: 
	 * internalStatus of Question will increment in the following manner:
	 * ADMITTED -> BALLOTED -> DISCUSSED
	 * 
	 * Algorithm:
	 * 1> Compute Questions: Find all the Questions submitted between start 
	 * time & end time, with device type = "half hour discussion from question", 
	 * internal status = "ADMITTED" & parent = null (don't consider clubbed 
	 * questions)
	 * 
	 * 2> Randomize the list of Questions obtained in step 1.
	 * 
	 * 3> Pick 2 (configurable) questions from the randomized list in step 2.
	 * @throws ELSException 
	 */
	private static Ballot createHDQNoticeBallot(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Question> computedList = HalfHourFromQuestionBallot.computeQuestionsForHalfHour(b.getSession(),
					b.getDeviceType(),
					b.getAnsweringDate(),
					true,
					false,
					b.getLocale());			
			
			CustomParameter csptUniqueFlagForNoticeBallot = CustomParameter.findByName(CustomParameter.class, b.getDeviceType().getType().toUpperCase() + "_" + b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_NOTICE_BALLOT", "");
			
			if(csptUniqueFlagForNoticeBallot != null && csptUniqueFlagForNoticeBallot.getValue() != null && !csptUniqueFlagForNoticeBallot.getValue().isEmpty()){
				if(csptUniqueFlagForNoticeBallot.getValue().equalsIgnoreCase("YES")){
					computedList = getUniqueMemberSubjectQuestion(b.getSession(), b.getDeviceType(), computedList, "notice");
				}
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter councilBallotCount = CustomParameter.findByFieldName(CustomParameter.class, "name", b.getDeviceType().getType().toUpperCase()+"_"+b.getSession().getHouse().getType().getType().toUpperCase()+"_BALLOT_OUTPUT_COUNT", "");
			
			if(councilBallotCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter(b.getDeviceType().getType().toUpperCase() + "_" +b.getSession().getHouse().getType().getType().toUpperCase()+"_BALLOT_OUTPUT_COUNT","Custom Parameter for output count is not found.");
				throw elsException;
			}
			
			int outputCount = Integer.parseInt(councilBallotCount.getValue());
			int ATTEMPT_COUNT = 0;
			
			CustomParameter csptMaxAttempt = CustomParameter.findByName(CustomParameter.class, "HDQ_COUNCIL_BALLOT_CALCULATION_MAX_ATTEMPT", "");
			int maxAttempt;
			if(csptMaxAttempt != null){
				maxAttempt = Integer.parseInt(csptMaxAttempt.getValue());
			}else{
				maxAttempt = 2;
			}
			
			List<Question> newComputedList = new ArrayList<Question>();
						
			newComputedList = HalfHourFromQuestionBallot.getComputedQuestion(b, outputCount, computedList);
			
			while(outputCount > newComputedList.size() && ATTEMPT_COUNT < maxAttempt){
				newComputedList = HalfHourFromQuestionBallot.getComputedQuestion(b, outputCount, newComputedList);
				ATTEMPT_COUNT++;
			}
			
			List<Question> randomizedList = HalfHourFromQuestionBallot.randomizeQuestions(newComputedList);
			List<Question> selectedList = HalfHourFromQuestionBallot.selectQuestionsForBallot(randomizedList, Integer.valueOf(councilBallotCount.getValue()));
			List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
			if(csptUniqueFlagForNoticeBallot!= null && csptUniqueFlagForNoticeBallot.getValue().equalsIgnoreCase("YES")){
				boolean isUniqueDataInSelectedList = false;
				long prevMember = 0L;
				for(Question qq : selectedList){
					if(prevMember != 0){
						if(prevMember != qq.getPrimaryMember().getId().longValue()){
							isUniqueDataInSelectedList = true;
						}else{
							selectedList.remove(qq);
							isUniqueDataInSelectedList = false;
						}
					}
					prevMember = qq.getPrimaryMember().getId().longValue();
				}
				
				List<Question> newSelectedList = new ArrayList<Question>();
				for(Question sm : selectedList){
					if(sm != null){
						newSelectedList.add(sm);
					}
				}
				
				if(!isUniqueDataInSelectedList && (newSelectedList.size() < outputCount)){
					for(Question qq : newComputedList){
						if(isUniqueQuestion(qq, newSelectedList)){
							newSelectedList.add(qq);
							break;
						}
					}
				}
				
				ballotEntries = HalfHourFromQuestionBallot.createQuestionNoticeBallotEntries(newSelectedList, b.getLocale());
			}else{
				List<Question> newSelectedList = new ArrayList<Question>();
				for(Question sm : selectedList){
					if(sm != null){
						newSelectedList.add(sm);
					}
				}
				ballotEntries = HalfHourFromQuestionBallot.createQuestionNoticeBallotEntries(newSelectedList, b.getLocale());
			}

			b.setBallotEntries(ballotEntries);
			ballot = (Ballot) b.persist();	
		}
		
		Status BALLOTED = Status.findByType(ApplicationConstants.QUESTION_HALFHOURDISCUSSION_FROMQUESTION_PROCESSED_BALLOTED, b.getLocale());
		Ballot.getRepository().updateBallotQuestions(ballot, BALLOTED);
		
		return ballot;
	}
	
	private static List<Question> getUniqueMemberSubjectQuestion(final Session session, 
			final DeviceType deviceType, 
			final List<Question> questions, 
			final String memberNotice){
		StringBuffer memberList = new StringBuffer(Question.findBallotedMembers(session, memberNotice, deviceType));
		StringBuffer subjectList = new StringBuffer(Question.findBallotedSubjects(session, deviceType));
		List<Question> newQuestionList = new ArrayList<Question>();
		memberList.append(",");
		subjectList.append(",");
		if(questions != null && !questions.isEmpty()){
			for(Question q : questions){
				if(!isExistingInList(memberList.toString(), q.getPrimaryMember().getId().toString())){
					if(!isExistingInList(subjectList.toString(), q.getRevisedSubject())){
						memberList.append(q.getPrimaryMember().getId().toString()+",");
						subjectList.append(q.getRevisedSubject()+",");
						newQuestionList.add(q);
					}
				}
			}
		}
		
		return newQuestionList;
	}
	
	/**
	 * Does not shuffle in place, returns a new list.
	 */
	private static List<Question> randomizeQuestions(final List<Question> questions) {
		List<Question> newQuestions = new ArrayList<Question>();
		newQuestions.addAll(questions);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newQuestions, rnd);
		return newQuestions;
	}
	
	/**
	 * A subset of eligible Questions of size @param maxQuestions are taken in Ballot.
	 */
	private static List<Question> selectQuestionsForBallot(final List<Question> questions,
			final Integer maxQuestions) {
		List<Question> selectedQList = new ArrayList<Question>();
		selectedQList.addAll(questions);
		if(selectedQList.size() >= maxQuestions) {
			selectedQList = selectedQList.subList(0, maxQuestions); 
		}
		return selectedQList;
	}
	
	private static List<BallotEntry> createQuestionNoticeBallotEntries(final List<Question> questions,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		for(Question q : questions) {
			BallotEntry ballotEntry = new BallotEntry();
			ballotEntry.setMember(q.getPrimaryMember());
			ballotEntry.setDeviceSequences(Ballot.createDeviceSequences(q, locale));
			ballotEntry.setLocale(locale);
			
			ballotEntries.add(ballotEntry);
		}
		return ballotEntries;
	}
	
	private static BallotEntry findBallotEntry(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return Ballot.getRepository().find(member, session, deviceType, answeringDate, locale);
	}	
	
	private static List<Question> getComputedQuestion(Ballot b, int outputCount, List<Question> computedList) throws ELSException{
		List<Question> newComputedList = new ArrayList<Question>();
		if( outputCount > computedList.size()){
			if(!computedList.isEmpty()){
				randomizeQuestions(computedList);
				Question m = computedList.get(0);
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), b.getLocale())){
					newComputedList.add(m);
				}
			}
			computedList = HalfHourFromQuestionBallot.computeQuestionsForHalfHour(b.getSession(),
					b.getDeviceType(),
					b.getAnsweringDate(),
					false,
					false,
					b.getLocale());
		}
		
		for(Question m : computedList){
			if(m.getPrimaryMember().isActiveMemberOn(new Date(), b.getLocale())){
				newComputedList.add(m);
			}
		}
		
		return newComputedList;
	}
	
	private static boolean isUniqueQuestion(Question q, List<Question> list){
		boolean retVal = false;
		for(Question s : list){
			if(s.getPrimaryMember().getId().longValue() != q.getPrimaryMember().getId().longValue()){
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}

	public static List<BallotMemberVO> getPreBallotHDQAssembly(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
		if(preBallot != null){
			preBallotMemberVOs.
			addAll(HalfHourFromQuestionBallot.getBallotMembers(preBallot.getBallotEntries()));
		}
		return preBallotMemberVOs;
	}

	public static List<BallotVO> previewPreBallotVOs(Session session,
			DeviceType deviceType, Date answeringDate, String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		List<Question> questions = HalfHourFromQuestionBallot.
				computeQuestionsForHalfHour(session, deviceType, answeringDate, false, true, locale);
			
			List<Question> newQuestionList = new ArrayList<Question>();
			for(Question q : questions){
				if(q.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
					newQuestionList.add(q);
				}
			}
			for(Question q : newQuestionList) {
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
				preBallotVO.setQuestionNumber(q.getNumber());
				preBallotVO.setQuestionSubject(q.getSubject());
				
				preBallotVOs.add(preBallotVO);
			}
		
			Collections.sort(preBallotVOs, new Comparator<BallotVO>(){
				@Override
				public int compare(BallotVO b1, BallotVO b2){
					return b1.getQuestionNumber().compareTo(b2.getQuestionNumber());
				}
			});			
			
		return preBallotVOs;
	}
	
}
