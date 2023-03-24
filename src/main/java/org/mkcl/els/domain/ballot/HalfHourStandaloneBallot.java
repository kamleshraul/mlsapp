package org.mkcl.els.domain.ballot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.DeviceBallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.StandaloneMotion;
import org.mkcl.els.domain.Status;

public class HalfHourStandaloneBallot {

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
			//TODO change to standalone
			List<StandaloneMotion> motions = 
					HalfHourStandaloneBallot.computeStandalonesForBallot(session, deviceType, answeringDate, false, true, locale);
			
			List<StandaloneMotion> newMotionList = new ArrayList<StandaloneMotion>();
			for(StandaloneMotion m : motions){
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
					newMotionList.add(m);
				}
			}
			for(StandaloneMotion m : newMotionList) {
				
				{
					BallotEntry ballotEntry = new BallotEntry();
					ballotEntry.setMember(m.getPrimaryMember());
					ballotEntry.setLocale(m.getLocale());
					
					List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
					deviceSequence.add(new DeviceSequence(m, m.getLocale()));
					ballotEntry.setDeviceSequences(deviceSequence);
					preBallotEntries.add(ballotEntry);
					
					deviceSequence = null;
					ballotEntry = null;
				}
				
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(m.getPrimaryMember().getFullname());
				preBallotVO.setQuestionNumber(m.getNumber());
				if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
					preBallotVO.setQuestionSubject(m.getRevisedSubject());
				}else{
					preBallotVO.setQuestionSubject(m.getSubject());
				}
				
				preBallotVOs.add(preBallotVO);
			}
			
			Collections.sort(preBallotEntries, new Comparator<BallotEntry>() {
				@Override
				public int compare(BallotEntry b1, BallotEntry b2){
					if(!b1.getDeviceSequences().isEmpty() && !b2.getDeviceSequences().isEmpty()){
						return ((StandaloneMotion)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((StandaloneMotion)b2.getDeviceSequences().get(0).getDevice()).getNumber());
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
			if(preBallotEntries != null && !preBallotEntries.isEmpty()){
				newPreBallot.setBallotEntries(preBallotEntries);
				newPreBallot.persist();
			}
		}else{
			CustomParameter cspt = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() + "_PREBALLOT_RECREATE_IF_EXISTS", "");
			if(cspt == null || cspt.getValue().equals("YES")){
				Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
				if(ballot == null){
					/*StringBuffer deviceSequenceToBeDeleted = new StringBuffer();
					StringBuffer ballotEntriesToBeDeleted = new StringBuffer();
					
					for(BallotEntry be : preBallotHDAssembly.getBallotEntries()){					
						for(DeviceSequence ds : be.getDeviceSequences()){
							deviceSequenceToBeDeleted.append(ds.getId()+",");
						}
						
						ballotEntriesToBeDeleted.append(be.getId()+",");
					}
					
					deviceSequenceToBeDeleted.replace(deviceSequenceToBeDeleted.toString().length(), deviceSequenceToBeDeleted.toString().length(), "");
					ballotEntriesToBeDeleted.replace(ballotEntriesToBeDeleted.toString().length(), ballotEntriesToBeDeleted.toString().length(), "");
					{
						Map<String, String[]> param = new HashMap<String, String[]>();
						param.put("locale", new String[]{locale});
						param.put("deviceS", new String[]{deviceSequenceToBeDeleted.toString()});
						param.put("ballotEntries", new String[]{ballotEntriesToBeDeleted.toString()});
						Query.findReportWithIn("DELETE_DEVICE_SEQUENCE", param);
						Query.findReportWithIn("DELETE_BALLOT_ENTRIES", param);
					}
					
					preBallotHDAssembly.setBallotEntries(null);*/
					preBallotHDAssembly.optimizedRemoveHDS();
					
					PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
					List<StandaloneMotion> motions = 
							HalfHourStandaloneBallot.computeStandalonesForBallot(session, deviceType, answeringDate, false, true, locale);
					
					List<StandaloneMotion> newMotionList = new ArrayList<StandaloneMotion>();
					for(StandaloneMotion m : motions){
						if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
							newMotionList.add(m);
						}
					}
					for(StandaloneMotion m : newMotionList) {
						
						{
							BallotEntry ballotEntry = new BallotEntry();
							ballotEntry.setMember(m.getPrimaryMember());
							ballotEntry.setLocale(m.getLocale());
							
							List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
							deviceSequence.add(new DeviceSequence(m, m.getLocale()));
							ballotEntry.setDeviceSequences(deviceSequence);
							preBallotEntries.add(ballotEntry);
							
							deviceSequence = null;
							ballotEntry = null;
						}
						
						BallotVO preBallotVO = new BallotVO();
						preBallotVO.setMemberName(m.getPrimaryMember().getFullname());
						preBallotVO.setQuestionNumber(m.getNumber());
						if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
							preBallotVO.setQuestionSubject(m.getRevisedSubject());
						}else{
							preBallotVO.setQuestionSubject(m.getSubject());
						}
						
						preBallotVOs.add(preBallotVO);
					}
					
					Collections.sort(preBallotEntries, new Comparator<BallotEntry>() {
						@Override
						public int compare(BallotEntry b1, BallotEntry b2){
							if(!b1.getDeviceSequences().isEmpty() && !b2.getDeviceSequences().isEmpty()){
								return ((StandaloneMotion)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((StandaloneMotion)b2.getDeviceSequences().get(0).getDevice()).getNumber());
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
						/*for(BallotEntry be : preBallotHDAssembly.getBallotEntries()){
							for(DeviceSequence ds : be.getDeviceSequences()){
								if(ds.getDevice() != null){
									if(ds.getDevice() instanceof StandaloneMotion) {
										StandaloneMotion q = (StandaloneMotion) ds.getDevice();
										BallotVO preBallotVO = new BallotVO();
										preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
										preBallotVO.setQuestionNumber(q.getNumber());
										if(q.getRevisedSubject() != null && !q.getRevisedSubject().isEmpty()){
											preBallotVO.setQuestionSubject(q.getRevisedSubject());
										}else{
											preBallotVO.setQuestionSubject(q.getSubject());
										}
										
										preBallotVOs.add(preBallotVO);
									}
								}
							}
						}*/
						
						Map<String, String[]> params = new HashMap<String, String[]>();
						params.put("locale", new String[]{locale});
						params.put("preBallotId", new String[]{preBallotHDAssembly.getId().toString()});
						List devices = Query.findReport("STANDALONE_PREBALLOT_DEVICES", params);
						
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
					}
				}
			}else{
				if(!preBallotHDAssembly.getBallotEntries().isEmpty()){
					/*for(BallotEntry be : preBallotHDAssembly.getBallotEntries()){
						for(DeviceSequence ds : be.getDeviceSequences()){
							if(ds.getDevice() != null){
								if(ds.getDevice() instanceof StandaloneMotion) {
									StandaloneMotion q = (StandaloneMotion) ds.getDevice();
									BallotVO preBallotVO = new BallotVO();
									preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
									preBallotVO.setQuestionNumber(q.getNumber());
									if(q.getRevisedSubject() != null && !q.getRevisedSubject().isEmpty()){
										preBallotVO.setQuestionSubject(q.getRevisedSubject());
									}else{
										preBallotVO.setQuestionSubject(q.getSubject());
									}
									
									preBallotVOs.add(preBallotVO);
								}
							}
						}
					}*/
					
					Map<String, String[]> params = new HashMap<String, String[]>();
					params.put("locale", new String[]{locale});
					params.put("preBallotId", new String[]{preBallotHDAssembly.getId().toString()});
					List devices = Query.findReport("STANDALONE_PREBALLOT_DEVICES", params);
					
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
				}
			}
		}
		
		return preBallotVOs;
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
	public static List<DeviceBallotVO> findHDSBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<DeviceBallotVO> ballotedVOs = new ArrayList<DeviceBallotVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				DeviceBallotVO ballotedVO = new DeviceBallotVO();
				ballotedVO.setMemberName(entry.getMember().getFullname());
				/*for(DeviceSequence ds : entry.getDeviceSequences()){
					Device device = ds.getDevice();
					Long id = device.getId();
					Question question = Question.findById(Question.class, id);
					ballotedVO.setId(question.getId());
					
					ballotedVO.setNumber(FormaterUtil.formatNumberNoGrouping(question.getNumber(), locale));
					ballotedVO.setSubject(question.getSubject());
					if(question.getRevisedQuestionText() != null){
						ballotedVO.setBody(question.getRevisedQuestionText());
					}else{
						ballotedVO.setBody(question.getQuestionText());
					}
				}*/
				ballotedVOs.add(ballotedVO);
			}
		}
		else {
			ballotedVOs = null;
		}
		
		return ballotedVOs;
	}
	
	public static List<BallotVO> findHDSCouncilPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
	
		List<Member> members = HalfHourStandaloneBallot.computeMembersHDSPreBallot(session, answeringDate, locale);
			
		for(Member m : members) {
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(m.getFullname());
				preBallotVOs.add(preBallotVO);
		}	
		
		return preBallotVOs;
	}
	
	/**
	 * For HDS council preballot member calculation
	 * @param session
	 * @param answeringDate
	 * @param locale
	 * @return
	 * @throws ELSException 
	 */
	private static List<Member> computeMembersHDSPreBallot(final Session session,
			final Date answeringDate,
			final String locale) {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE, locale);
	
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(
				deviceType.getType() + "_submissionStartDate"), 
				datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(
				deviceType.getType() + "_submissionEndDate"), 
				datePattern.getValue(), locale);
		
		Status ADMITTED = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED };
		
		List<Member> members = Question.findPrimaryMembers(session, deviceType, answeringDate, internalStatuses, false, startTime, endTime, ApplicationConstants.ASC, locale);
		
		return members;
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
			return HalfHourStandaloneBallot.createHDSAssemblyBallot(ballot);
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			return HalfHourStandaloneBallot.createHDSCouncilBallot(ballot);
		}
		else {
			throw new ELSException("StarredQuestionBallot.create/1", "Inappropriate houseType set in Session.");
		}
	}
	
	public static Ballot createMemberBallotHDS(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, 
					b.getDeviceType().getType().toUpperCase() + "_" + 
					b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			if(csptUniqueFlag == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("QUESTIONS_HALFHOURDISCUSSION_STANDALONE_UNIQUE_FLAG_MEMBER_BALLOT", 
						"Custom Parameters for QUESTIONS_HALFHOURDISCUSSION_STANDALONE_UNIQUE_FLAG_MEMBER_BALLOT is not set.");
				throw elsException;
			}
			
			if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
				computedList = HalfHourStandaloneBallot.computeMembersStandalone(b.getSession(),
							b.getDeviceType(),
							b.getAnsweringDate(),
							true,
							b.getLocale());
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter hdsAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(hdsAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCount = Integer.parseInt(hdsAssemblyBallotOutPutCount.getValue());
			List<Member> finalComputedList = getUniqueMembers(b.getSession(), b.getDeviceType(), computedList, "member");
			if(finalComputedList.size() < outPutCount){
				finalComputedList = HalfHourStandaloneBallot.computeMembersStandalone(b.getSession(),
						b.getDeviceType(),
						b.getAnsweringDate(),
						false,
						b.getLocale());
			}
			
			List<Member> randomizedList = HalfHourStandaloneBallot.randomizeMembers(finalComputedList);
			
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCP = CustomParameter.findByFieldName(CustomParameter.class, "name", 
					ApplicationConstants.HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL, null);
			Integer ballotOutput = null;
			if(ballotOutputCountCP == null){
				ballotOutput = new Integer(3);
			}else{
				ballotOutput = new Integer(ballotOutputCountCP.getValue());
			}
			
			List<Member> selectedList = HalfHourStandaloneBallot.selectMembersForBallot(randomizedList, ballotOutput);
			
			List<BallotEntry> ballotEntries = HalfHourStandaloneBallot.createMemberBallotEntries(selectedList, b.getLocale());
			b.setBallotEntries(ballotEntries);
			b.persist();
			
			return b;
		}
		else {
			return ballot;
		}	
	}
	
	public static Ballot createMemberBallotStandAlone(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, 
					b.getDeviceType().getType().toUpperCase() + "_" + 
					b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			if(csptUniqueFlag != null && csptUniqueFlag.getValue() != null && !csptUniqueFlag.getValue().isEmpty()){
				if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
					computedList = HalfHourStandaloneBallot.computeMembersStandalone(b.getSession(),
								b.getDeviceType(),
								b.getAnsweringDate(),
								true,
								b.getLocale());
				}
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter hdsAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(hdsAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCOunt = Integer.parseInt(hdsAssemblyBallotOutPutCount.getValue());
			List<Member> finalComputedList = getUniqueMembers(b.getSession(), b.getDeviceType(), computedList, "member");
			if(finalComputedList.size() < outPutCOunt){
				finalComputedList = HalfHourStandaloneBallot.computeMembersStandalone(b.getSession(),
						b.getDeviceType(),
						b.getAnsweringDate(),
						false,
						b.getLocale());
			}
			
			List<Member> randomizedList = HalfHourStandaloneBallot.randomizeMembers(finalComputedList);
			// Read the constant 2 as a configurable parameter
			List<Member> selectedList = HalfHourStandaloneBallot.selectMembersForBallot(randomizedList, outPutCOunt);
			
			List<BallotEntry> ballotEntries = HalfHourStandaloneBallot.createMemberBallotEntries(selectedList, b.getLocale());
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
		BallotEntry ballotEntry = HalfHourStandaloneBallot.findBallotEntry(member, ballot.getSession(),
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
	private static List<StandaloneMotion> computeStandalonesForBallot(final Session session,
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
		
		Status ADMITTED = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
		Status REPEATADMITTED = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED,REPEATADMITTED };
		
		// TODO: internal Status will only refer to the lifecycle of a Question in the Workflow
		// i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Questions
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<StandaloneMotion> motions = StandaloneMotion.findByBallot(session, deviceType, answeringDate, internalStatuses, false, false, isMandatoryUnique, isPreBallot, startTime, endTime, ApplicationConstants.ASC, locale);
			
		return motions;
	}
	
	private static Ballot createHDSAssemblyBallot(final Ballot ballot) throws ELSException {
		return HalfHourStandaloneBallot.createBallotHDSAssembly(ballot);
	}
	
	private static Ballot createBallotHDSAssembly(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, b.getDeviceType().getType().toUpperCase() + "_" + b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			if(csptUniqueFlag == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDS_LOWERHOUSE_UNIQUE_FLAG_MEMBER_BALLOT", "Custom Parameters for HDS_LOWERHOUSE_UNIQUE_FLAG_MEMBER_BALLOT is not set.");
				throw elsException;
			}
			
			if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
				computedList = HalfHourStandaloneBallot.computeMembersStandalone(b.getSession(),
							b.getDeviceType(),
							b.getAnsweringDate(),
							true,
							b.getLocale());
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter hdsAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(hdsAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCount = Integer.parseInt(hdsAssemblyBallotOutPutCount.getValue());
			List<Member> finalComputedList = HalfHourStandaloneBallot.getUniqueMembers(b.getSession(), b.getDeviceType(), computedList, "member");
			List<Member> newMemberList = new ArrayList<Member>();
			
			if(finalComputedList.size() < outPutCount){
				if(!finalComputedList.isEmpty()){
					Member m = finalComputedList.get(0);
					if(m.isActiveMemberOn(new Date(), b.getLocale())){
						newMemberList.add(m);
					}
				}
				finalComputedList = HalfHourStandaloneBallot.computeMembersStandalone(b.getSession(),
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
			
			List<Member> randomizedList = HalfHourStandaloneBallot.randomizeMembers(newMemberList);
			List<Member> selectedList = HalfHourStandaloneBallot.selectMembersForBallot(randomizedList, outPutCount);
			
			List<BallotEntry> ballotEntries = HalfHourStandaloneBallot.createHDSBallotEntries(b.getSession(), b.getDeviceType(), b.getAnsweringDate(), selectedList,
					b.getLocale());
			b.setBallotEntries(ballotEntries);
			ballot = (Ballot) b.persist();	
		}
		
		return ballot;
	}
	
	private static List<Member> computeMembersStandalone(final Session session,
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
		
		List<Member> members = null;		
		Status ADMITTED = null;
		Status REPEATADMITTED = null;

		if(getDeviceTypeStartsWith(deviceType).equals(ApplicationConstants.DEVICE_STANDALONE)){
			
			ADMITTED = Status.findByType(ApplicationConstants.STANDALONE_FINAL_ADMISSION, locale);
			REPEATADMITTED = Status.findByType(ApplicationConstants.STANDALONE_FINAL_REPEATADMISSION, locale);
			Status[] internalStatuses = new Status[] { ADMITTED, REPEATADMITTED };
			
			if(isUnique.booleanValue()){
				members = StandaloneMotion.findPrimaryMembersByBallot(session, deviceType, answeringDate, internalStatuses, false, false, startTime, endTime, ApplicationConstants.ASC, locale);
			}else{
				members = StandaloneMotion.findPrimaryMembersForBallot(session, deviceType,answeringDate, internalStatuses, false, startTime, endTime,ApplicationConstants.ASC, locale);
			}
		}	
		
		return members;
	}
	
	private static List<Member> getUniqueMembers(final Session session, final DeviceType deviceType, final List<Member> members, final String memberNotice) {
		StringBuffer memberList = new StringBuffer("");
		String returnData = Question.findBallotedMembers(session, memberNotice, deviceType);
		memberList.append(( returnData == null)? "":returnData);
		List<Member> newMs = new ArrayList<Member>();
		if(!memberList.toString().isEmpty()){
			for(Member m : members){
				if(!HalfHourStandaloneBallot.isExistingInList(memberList.toString(), m.getId().toString())){
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
	
	private static List<BallotEntry> createHDSBallotEntries(final Session session, final DeviceType deviceType, final Date answeringDate, final List<Member> members,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		
		for(Member m : members) {
			
			BallotEntry ballotEntry = new BallotEntry();
			ballotEntry.setMember(m);
			ballotEntries.add(ballotEntry);
		}

		return ballotEntries;
	}
	
	private static Ballot createHDSCouncilBallot(final Ballot ballot) throws ELSException {
		return HalfHourStandaloneBallot.createHDSNoticeBallot(ballot);
	}
	
	private static Ballot createHDSNoticeBallot(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<StandaloneMotion> computedList = HalfHourStandaloneBallot.computeStandalonesForBallot(b.getSession(), b.getDeviceType(), b.getAnsweringDate(), true, false, b.getLocale());		
			
			CustomParameter csptUniqueFlagForNoticeBallot = CustomParameter.findByName(CustomParameter.class, b.getDeviceType().getType().toUpperCase() + "_" + b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_NOTICE_BALLOT", "");
			
			if(csptUniqueFlagForNoticeBallot != null && csptUniqueFlagForNoticeBallot.getValue() != null && !csptUniqueFlagForNoticeBallot.getValue().isEmpty()){
				if(csptUniqueFlagForNoticeBallot.getValue().equalsIgnoreCase("YES")){
					computedList = getUniqueMemberSubjectStandalone(b.getSession(), b.getDeviceType(), computedList, "notice");
				}
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter councilBallotCount = CustomParameter.findByFieldName(CustomParameter.class, "name", b.getDeviceType().getType().toUpperCase()+"_"+b.getSession().getHouse().getType().getType().toUpperCase()+"_BALLOT_OUTPUT_COUNT", "");
			
			if(councilBallotCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter(b.getDeviceType().getType().toUpperCase()+"_"+b.getSession().getHouse().getType().getType().toUpperCase()+"_BALLOT_OUTPUT_COUNT","Custom Parameter for output count is not found.");
				throw elsException;
			}
			
			int outputCount = Integer.parseInt(councilBallotCount.getValue());
			int ATTEMPT_COUNT = 0;
			
			CustomParameter csptMaxAttempt = CustomParameter.findByName(CustomParameter.class, "HDS_COUNCIL_BALLOT_CALCULATION_MAX_ATTEMPT", "");
			int maxAttempt;
			if(csptMaxAttempt != null){
				maxAttempt = Integer.parseInt(csptMaxAttempt.getValue());
			}else{
				maxAttempt = 2;
			}
			
			List<StandaloneMotion> newComputedList = new ArrayList<StandaloneMotion>();
						
			newComputedList = HalfHourStandaloneBallot.getComputedStandalone(b, outputCount, computedList);
			
			while(outputCount > newComputedList.size() && ATTEMPT_COUNT < maxAttempt){
				newComputedList = HalfHourStandaloneBallot.getComputedStandalone(b, outputCount, newComputedList);
				ATTEMPT_COUNT++;
			}
			
			List<StandaloneMotion> randomizedList = HalfHourStandaloneBallot.randomizeStandalones(newComputedList);
			List<StandaloneMotion> selectedList = HalfHourStandaloneBallot.selectStandalonesForBallot(randomizedList, Integer.valueOf(councilBallotCount.getValue()));
			
			boolean isUniqueDataInSelectedList = false;
			long prevMember = 0L;
			for(StandaloneMotion qq : selectedList){
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
			
			List<StandaloneMotion> newSelectedList = new ArrayList<StandaloneMotion>();
			for(StandaloneMotion sm : selectedList){
				if(sm != null){
					newSelectedList.add(sm);
				}
			}
			
			if(!isUniqueDataInSelectedList && (newSelectedList.size() < outputCount)){
				for(StandaloneMotion qq : newComputedList){
					if(isUniqueStandalone(qq, newSelectedList)){
						newSelectedList.add(qq);
						break;
					}
				}
			}
			
			List<BallotEntry> ballotEntries = HalfHourStandaloneBallot.createStandaloneNoticeBallotEntries(newSelectedList, b.getLocale());
			b.setBallotEntries(ballotEntries);
			ballot = (Ballot) b.persist();	
		}
		
		Status BALLOTED = Status.findByType(ApplicationConstants.STANDALONE_PROCESSED_BALLOTED, b.getLocale());
		Ballot.getRepository().updateBallotStandalones(ballot, BALLOTED);
		
		return ballot;
	}
	
	private static List<StandaloneMotion> getUniqueMemberSubjectStandalone(final Session session, 
			final DeviceType deviceType, 
			final List<StandaloneMotion> motions, 
			final String memberNotice){
		StringBuffer memberList = new StringBuffer(StandaloneMotion.findBallotedMembers(session, memberNotice, deviceType));
		StringBuffer subjectList = new StringBuffer(StandaloneMotion.findBallotedSubjects(session, deviceType));
		
		List<StandaloneMotion> newMotionList = new ArrayList<StandaloneMotion>();
		
		memberList.append(",");
		subjectList.append(",");
		
		if(motions != null && !motions.isEmpty()){
			for(StandaloneMotion m : motions){
				
				if(!isExistingInList(memberList.toString(), m.getPrimaryMember().getId().toString())){
					
					if(!isExistingInList(subjectList.toString(), m.getRevisedSubject())){
					
						memberList.append(m.getPrimaryMember().getId().toString()+",");
						subjectList.append(m.getRevisedSubject()+",");
						newMotionList.add(m);
					}
				}
			}
		}
		
		return newMotionList;
	}
	
	private static List<StandaloneMotion> getComputedStandalone(final Ballot ballot, 
			int outputCount, List<StandaloneMotion> computedList) throws ELSException{
		List<StandaloneMotion> newComputedList = new ArrayList<StandaloneMotion>();
		if( outputCount > computedList.size()){
			if(!computedList.isEmpty()){
				HalfHourStandaloneBallot.randomizeStandalones(computedList);
				StandaloneMotion m = computedList.get(0);
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), ballot.getLocale())){
					newComputedList.add(m);
				}
			}
			computedList = computeStandalonesForBallot(ballot.getSession(),
					ballot.getDeviceType(),
					ballot.getAnsweringDate(),
					false,
					false,
					ballot.getLocale());
		}
		
		for(StandaloneMotion m : computedList){
			if(m.getPrimaryMember().isActiveMemberOn(new Date(), ballot.getLocale())){
				newComputedList.add(m);
			}
		}
		
		return newComputedList;
	}
	
	/**
	 * Does not shuffle in place, returns a new list.
	 */
	private static List<StandaloneMotion> randomizeStandalones(final List<StandaloneMotion> motions) {
		List<StandaloneMotion> newMotions = new ArrayList<StandaloneMotion>();
		newMotions.addAll(motions);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newMotions, rnd);
		return newMotions;
	}
	
	/**
	 * A subset of eligible Standalone of size @param maxQuestions are taken in Ballot.
	 */
	private static List<StandaloneMotion> selectStandalonesForBallot(final List<StandaloneMotion> motions, final Integer maxNos) {
		List<StandaloneMotion> selectedList = new ArrayList<StandaloneMotion>();
		selectedList.addAll(motions);
		if(selectedList.size() >= maxNos) {
			selectedList = selectedList.subList(0, maxNos); 
		}
		return selectedList;
	}
	
	private static boolean isUniqueStandalone(final StandaloneMotion motion, final List<StandaloneMotion> list){
		boolean retVal = false;
		for(StandaloneMotion s : list){
			if(s.getPrimaryMember().getId().longValue() != motion.getPrimaryMember().getId().longValue()){
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}
	
	private static List<BallotEntry> createStandaloneNoticeBallotEntries(final List<StandaloneMotion> motions,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		for(StandaloneMotion m : motions) {
			BallotEntry ballotEntry = new BallotEntry();
			ballotEntry.setMember(m.getPrimaryMember());
			ballotEntry.setDeviceSequences(Ballot.createDeviceSequences(m, locale));
			ballotEntry.setLocale(locale);
			
			ballotEntries.add(ballotEntry);
		}
		return ballotEntries;
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
	
	private static BallotEntry findBallotEntry(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return Ballot.getRepository().find(member, session, deviceType, answeringDate, locale);
	}
	
	private static String getDeviceTypeStartsWith(DeviceType deviceType){
		String retVal = null;
		if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
			retVal = ApplicationConstants.DEVICE_QUESTIONS;
		}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
			retVal = ApplicationConstants.DEVICE_RESOLUTIONS;
		}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_STANDALONE)){
			retVal = ApplicationConstants.DEVICE_STANDALONE;			
		}
		return retVal;
		
	}

	public static List<BallotVO> previewPreBallotVOs(Session session,
			DeviceType deviceType, Date answeringDate, String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		List<StandaloneMotion> motions = 
		HalfHourStandaloneBallot.computeStandalonesForBallot(session, deviceType, answeringDate, false, true, locale);
		List<StandaloneMotion> newMotionList = new ArrayList<StandaloneMotion>();
		for(StandaloneMotion m : motions){
			if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
				newMotionList.add(m);
			}
		}
		for(StandaloneMotion m : newMotionList) {
			BallotVO preBallotVO = new BallotVO();
			preBallotVO.setMemberName(m.getPrimaryMember().getFullname());
			preBallotVO.setQuestionNumber(m.getNumber());
			if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
				preBallotVO.setQuestionSubject(m.getRevisedSubject());
			}else{
				preBallotVO.setQuestionSubject(m.getSubject());
			}
			
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
