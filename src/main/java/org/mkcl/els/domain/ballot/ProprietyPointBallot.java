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
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Query;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.ProprietyPoint;
import org.mkcl.els.domain.Status;

public class ProprietyPointBallot {
	
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
		
		PreBallot preBallotPROISAssembly = PreBallot.find(session, deviceType, answeringDate, locale);
		List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
		
		if(preBallotPROISAssembly == null){
			
			PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);				
			//TODO change to propriety points
			List<ProprietyPoint> proprietyPoints = 
					ProprietyPointBallot.computeProprietyPointsForBallot(session, deviceType, answeringDate, false, true, locale);
			
			List<ProprietyPoint> newProprietyPointList = new ArrayList<ProprietyPoint>();
			for(ProprietyPoint m : proprietyPoints){
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
					newProprietyPointList.add(m);
				}
			}
			for(ProprietyPoint m : newProprietyPointList) {
				
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
						return ((ProprietyPoint)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((ProprietyPoint)b2.getDeviceSequences().get(0).getDevice()).getNumber());
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
					
					for(BallotEntry be : preBallotPROISAssembly.getBallotEntries()){					
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
					
					preBallotPROISAssembly.setBallotEntries(null);*/
					preBallotPROISAssembly.optimizedRemoveHDS();
					
					PreBallot newPreBallot = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
					List<ProprietyPoint> proprietyPoints = 
							ProprietyPointBallot.computeProprietyPointsForBallot(session, deviceType, answeringDate, false, true, locale);
					
					List<ProprietyPoint> newProprietyPointList = new ArrayList<ProprietyPoint>();
					for(ProprietyPoint m : proprietyPoints){
						if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
							newProprietyPointList.add(m);
						}
					}
					for(ProprietyPoint m : newProprietyPointList) {
						
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
								return ((ProprietyPoint)b1.getDeviceSequences().get(0).getDevice()).getNumber().compareTo(((ProprietyPoint)b2.getDeviceSequences().get(0).getDevice()).getNumber());
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
					if(!preBallotPROISAssembly.getBallotEntries().isEmpty()){
						/*for(BallotEntry be : preBallotPROISAssembly.getBallotEntries()){
							for(DeviceSequence ds : be.getDeviceSequences()){
								if(ds.getDevice() != null){
									if(ds.getDevice() instanceof ProprietyPoint) {
										ProprietyPoint q = (ProprietyPoint) ds.getDevice();
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
						params.put("preBallotId", new String[]{preBallotPROISAssembly.getId().toString()});
						List devices = Query.findReport("PROPRIETYPOINT_PREBALLOT_DEVICES", params);
						
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
				if(!preBallotPROISAssembly.getBallotEntries().isEmpty()){
					/*for(BallotEntry be : preBallotPROISAssembly.getBallotEntries()){
						for(DeviceSequence ds : be.getDeviceSequences()){
							if(ds.getDevice() != null){
								if(ds.getDevice() instanceof ProprietyPoint) {
									ProprietyPoint q = (ProprietyPoint) ds.getDevice();
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
					params.put("preBallotId", new String[]{preBallotPROISAssembly.getId().toString()});
					List devices = Query.findReport("PROPRIETYPOINT_PREBALLOT_DEVICES", params);
					
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
			return ProprietyPointBallot.createPROISAssemblyBallot(ballot);
		}
//		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
//			return ProprietyPointBallot.createPROISCouncilBallot(ballot);
//		}
		else {
			throw new ELSException("ProprietyPointBallot.create/1", "Inappropriate houseType set in Session.");
		}
	}
	
	public static List<BallotVO> findUnBallotedVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> unBallotedVOs = new ArrayList<BallotVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null){
			List<ProprietyPoint> proprietyPoints = 
					ProprietyPointBallot.computeProprietyPointsForBallot(session, deviceType, answeringDate, false, false, locale);
			
			List<ProprietyPoint> newProprietyPointList = new ArrayList<ProprietyPoint>();
			for(ProprietyPoint m : proprietyPoints){
				if(m.getPrimaryMember().isActiveMemberOn(new Date(), locale)){
					newProprietyPointList.add(m);
				}
			}
			for(ProprietyPoint m : newProprietyPointList) {				
				BallotVO unBallotedVO = new BallotVO();
				unBallotedVO.setMemberName(m.getPrimaryMember().getFullname());
				unBallotedVO.setQuestionNumber(m.getNumber());
				if(m.getRevisedSubject() != null && !m.getRevisedSubject().isEmpty()){
					unBallotedVO.setQuestionSubject(m.getRevisedSubject());
				}else{
					unBallotedVO.setQuestionSubject(m.getSubject());
				}
				
				unBallotedVOs.add(unBallotedVO);
			}			
		}
		
		return unBallotedVOs;
	}
	
	
	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	private static List<ProprietyPoint> computeProprietyPointsForBallot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Boolean isMandatoryUnique,
			final Boolean isPreBallot,
			final String locale) throws ELSException {
		//CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
	
		Date startTime = ProprietyPoint.findSubmissionStartTime(session, answeringDate); //for council 2nd argument should be 1 working day before answeringDate
		Date endTime = ProprietyPoint.findSubmissionEndTime(session, answeringDate); //for council 2nd argument should be 1 working day before answeringDate or session end date if current date is session date
		
		Status ADMITTED = Status.findByType(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION, locale);
		Status RECOMMENDADMITTED = Status.findByType(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_ADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED,RECOMMENDADMITTED };
		
		// TODO: internal Status will only refer to the lifecycle of a Question in the Workflow
		// i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Questions
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<ProprietyPoint> proprietyPoints = ProprietyPoint.findByBallot(session, deviceType, answeringDate, internalStatuses, false, false, isMandatoryUnique, isPreBallot, startTime, endTime, ApplicationConstants.ASC, locale);
			
		return proprietyPoints;
	}
	
	private static Ballot createPROISAssemblyBallot(final Ballot ballot) throws ELSException {
		return ProprietyPointBallot.createBallotPROISAssembly(ballot);
	}
	
	private static Ballot createBallotPROISAssembly(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, b.getDeviceType().getType().toUpperCase() + "_" + b.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			if(csptUniqueFlag == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("PROIS_LOWERHOUSE_UNIQUE_FLAG_MEMBER_BALLOT", "Custom Parameters for PROIS_LOWERHOUSE_UNIQUE_FLAG_MEMBER_BALLOT is not set.");
				throw elsException;
			}
			
			if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
				computedList = ProprietyPointBallot.computeMembersProprietyPoint(b.getSession(),
							b.getDeviceType(),
							b.getAnsweringDate(),
							true,
							b.getLocale());
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter proisAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "PROIS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(proisAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("PROIS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for PROIS_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCount = Integer.parseInt(proisAssemblyBallotOutPutCount.getValue());
			List<Member> finalComputedList = ProprietyPointBallot.getUniqueMembers(b.getSession(), b.getDeviceType(), computedList, "member");
			List<Member> newMemberList = new ArrayList<Member>();
			
			if(finalComputedList.size() < outPutCount){
				if(!finalComputedList.isEmpty()){
					Member m = finalComputedList.get(0);
					if(m.isActiveMemberOn(new Date(), b.getLocale())){
						newMemberList.add(m);
					}
				}
				finalComputedList = ProprietyPointBallot.computeMembersProprietyPoint(b.getSession(),
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
			
			List<Member> randomizedList = ProprietyPointBallot.randomizeMembers(newMemberList);
			List<Member> selectedList = ProprietyPointBallot.selectMembersForBallot(randomizedList, outPutCount);
			
			List<BallotEntry> ballotEntries = ProprietyPointBallot.createPROISBallotEntries(b.getSession(), b.getDeviceType(), b.getAnsweringDate(), selectedList,
					b.getLocale());
			b.setBallotEntries(ballotEntries);
			ballot = (Ballot) b.persist();	
		}
		
		return ballot;
	}
	
	private static List<Member> computeMembersProprietyPoint(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Boolean isUnique,
			final String locale) throws ELSException {
		//CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		
		Date startTime = ProprietyPoint.findSubmissionStartTime(session, answeringDate); //for council 2nd argument should be 1 working day before answeringDate
		Date endTime = ProprietyPoint.findSubmissionEndTime(session, answeringDate); //for council 2nd argument should be 1 working day before answeringDate or session end date if current date is session date
		
		List<Member> members = null;		
		Status ADMITTED = null;
		Status RECOMMENDADMITTED = null;

		if(deviceType.getType().equals(ApplicationConstants.PROPRIETY_POINT)){
			
			ADMITTED = Status.findByType(ApplicationConstants.PROPRIETYPOINT_FINAL_ADMISSION, locale);
			RECOMMENDADMITTED = Status.findByType(ApplicationConstants.PROPRIETYPOINT_RECOMMEND_ADMISSION, locale);
			Status[] internalStatuses = new Status[] { ADMITTED, RECOMMENDADMITTED };
			
			if(isUnique.booleanValue()){
				members = ProprietyPoint.findPrimaryMembersByBallot(session, deviceType, answeringDate, internalStatuses, false, false, startTime, endTime, ApplicationConstants.ASC, locale);
			}else{
				members = ProprietyPoint.findPrimaryMembersForBallot(session, deviceType,answeringDate, internalStatuses, false, startTime, endTime,ApplicationConstants.ASC, locale);
			}
		}	
		
		return members;
	}
	
	private static List<Member> getUniqueMembers(final Session session, final DeviceType deviceType, final List<Member> members, final String memberNotice) {
		StringBuffer memberList = new StringBuffer("");
		String returnData = Question.findBallotedMembers(session, memberNotice, deviceType); //review if to be replicated with propriety point method 
		memberList.append(( returnData == null)? "":returnData);
		List<Member> newMs = new ArrayList<Member>();
		if(!memberList.toString().isEmpty()){
			for(Member m : members){
				if(!ProprietyPointBallot.isExistingInList(memberList.toString(), m.getId().toString())){
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
	
	private static List<BallotEntry> createPROISBallotEntries(final Session session, final DeviceType deviceType, final Date answeringDate, final List<Member> members,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		
		for(Member m : members) {
			
			BallotEntry ballotEntry = new BallotEntry();
			ballotEntry.setMember(m);
			ballotEntries.add(ballotEntry);
		}

		return ballotEntries;
	}

}