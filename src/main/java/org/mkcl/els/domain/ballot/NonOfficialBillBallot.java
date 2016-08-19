package org.mkcl.els.domain.ballot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.BillBallotVO;
import org.mkcl.els.domain.Bill;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.House;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;

public class NonOfficialBillBallot {

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
		
		List<Bill> bills = 
				NonOfficialBillBallot.computeBillNonOfficial(session, answeringDate, true, locale);
		PreBallot preBallotBill = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
		List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
		
		for(Bill b : bills) {
			
			{
				BallotEntry ballotEntry = new BallotEntry();
				ballotEntry.setMember(b.getPrimaryMember());
				ballotEntry.setLocale(b.getLocale());
				
				List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
				deviceSequence.add(new DeviceSequence(b, b.getLocale()));
				ballotEntry.setDeviceSequences(deviceSequence);
				preBallotEntries.add(ballotEntry);
				
				deviceSequence = null;
				ballotEntry = null;
			}
			
			BallotVO preBallotVO = new BallotVO();
			preBallotVO.setMemberName(b.getPrimaryMember().getFullname());
			preBallotVO.setQuestionNumber(b.getNumber());
			preBallotVO.setQuestionSubject(b.getDefaultTitle());

			preBallotVOs.add(preBallotVO);
		}
		
		//persist the preballot list
		preBallotBill.setBallotEntries(preBallotEntries);
		preBallotBill.persist();
		
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
	 *
	 */
	public static List<BillBallotVO> findBillMemberSubjectBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BillBallotVO> ballotedVOs = new ArrayList<BillBallotVO>();
		
		Ballot ballot = null;
		try {
			ballot = Ballot.find(session, deviceType, answeringDate, locale);
		} catch (ELSException e) {
			e.printStackTrace();
		}
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				BillBallotVO ballotedVO = new BillBallotVO();
				ballotedVO.setMemberName(entry.getMember().getFullname());
				for(DeviceSequence ds : entry.getDeviceSequences()){
					Device device = ds.getDevice();
					Long id = device.getId();
					Bill bill = Bill.findById(Bill.class, id);
					ballotedVO.setId(bill.getId());
					if(bill.getDiscussionStatus() != null){
						ballotedVO.setChecked("checked");
					}else{
						ballotedVO.setChecked("unchecked");
					}
					ballotedVO.setBillNumber(FormaterUtil.formatNumberNoGrouping(bill.getNumber(), locale));
					ballotedVO.setBillTitle(bill.getDefaultTitle());
					ballotedVO.setContentDraft(bill.getDefaultContentDraft());
				}
				ballotedVOs.add(ballotedVO);
			}
		}
		else {
			ballotedVOs = null;
		}
		
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
			return NonOfficialBillBallot.createBillNonOfficialBallot(ballot);
		}
		else if(houseTypeType.equals(ApplicationConstants.UPPER_HOUSE)) {
			return NonOfficialBillBallot.createBillNonOfficialBallot(ballot);
		}
		else {
			throw new ELSException("StarredQuestionBallot.create/1", "Inappropriate houseType set in Session.");
		}
	}
	
	public static Ballot createBallotBillNonOfficial_UniqueMember(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(),
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = NonOfficialBillBallot.computeMembersBillNonOfficial(b.getSession(),
					false,
					b.getAnsweringDate(),
					b.getLocale());
			List<Member> randomizedList = NonOfficialBillBallot.randomizeMembers(computedList);
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, 
					"name", ApplicationConstants.BILL_NONOFFICIAL_BALLOT_OUTPUT_COUNT, "");
			Integer ballotOutputCount = null;
			if(ballotOutputCountCustomParameter != null){
				ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
			}else{
				ballotOutputCount = 6;
			}
			
			List<Member> selectedList = NonOfficialBillBallot.selectMembersForBallot(randomizedList, ballotOutputCount);
			
			List<BallotEntry> ballotEntries = NonOfficialBillBallot.createBillNonOfficialBallotEntries(b.getSession(), b.getDeviceType(), 
					b.getAnsweringDate(), selectedList, b.getLocale());
			b.setBallotEntries(ballotEntries);
			b.persist();
			
			return b;
		}
		else {
			return ballot;
		}
	}
	
	
	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	private static List<Bill> computeBillNonOfficial(final Session session,
			final Date answeringDate, final Boolean isPreballot,
			final String locale) throws ELSException {
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.NONOFFICIAL_BILL, locale);
		
		Status UNDER_CONSIDERATION = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, locale);
		Status[] internalStatuses = new Status[] { UNDER_CONSIDERATION };
	
		Status INTRODUCED = Status.findByType(ApplicationConstants.BILL_PROCESSED_INTRODUCED, locale);		
		Status[] recommendationStatuses = new Status[] { INTRODUCED };
		
		List<Bill> bills = Bill.findForBallot(session, deviceType, 
				answeringDate, internalStatuses, recommendationStatuses, isPreballot, false, ApplicationConstants.ASC, locale);
		
		return bills;
	}
	
	private static Ballot createBillNonOfficialBallot(final Ballot ballot) throws ELSException {
		return NonOfficialBillBallot.createBallotBillNonOfficial_UniqueSubject(ballot);
	}
	
	private static Ballot createBallotBillNonOfficial_UniqueSubject(final Ballot b) throws ELSException {
		Ballot ballot = Ballot.find(b.getSession(), b.getDeviceType(), 
				b.getAnsweringDate(), b.getLocale());
		
		if(ballot == null) {
			List<Bill> bills = NonOfficialBillBallot.computeBillNonOfficial(b.getSession(), b.getAnsweringDate(), false, b.getLocale());
			List<Bill> randomizedList = NonOfficialBillBallot.randomizeBills(bills);
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, 
					"name", ApplicationConstants.BILL_NONOFFICIAL_BALLOT_OUTPUT_COUNT, "");
			Integer ballotOutputCount = null;
			if(ballotOutputCountCustomParameter != null){
				ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
			}else{
				ballotOutputCount = 6;
			}			
			List<Bill> selectedList = NonOfficialBillBallot.selectBillsForBallot(randomizedList, ballotOutputCount);
			List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();				
			for(Bill bill : selectedList) {				
				BallotEntry ballotEntry = new BallotEntry();
				ballotEntry.setMember(bill.getPrimaryMember());
				ballotEntry.setLocale(bill.getLocale());					
				List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
				deviceSequence.add(new DeviceSequence(bill, bill.getLocale()));
				ballotEntry.setDeviceSequences(deviceSequence);
				ballotEntries.add(ballotEntry);					
				deviceSequence = null;
				ballotEntry = null;								
			}
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
	private static List<Bill> randomizeBills(final List<Bill> bills) {
		List<Bill> newBills = new ArrayList<Bill>();
		newBills.addAll(bills);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newBills, rnd);
		return newBills;
	}
	
	/**
	 * A subset of eligible Bills of size @param maxBills are taken in Ballot.
	 */
	private static List<Bill> selectBillsForBallot(final List<Bill> bills,
			final Integer maxBills) {
		List<Bill> selectedBList = new ArrayList<Bill>();
		selectedBList.addAll(bills);
		if(selectedBList.size() >= maxBills) {
			selectedBList = selectedBList.subList(0, maxBills); 
		}
		return selectedBList;
	}
	
	private static List<Member> computeMembersBillNonOfficial(final Session session,
			final Boolean isPreBallot,
			final Date answeringDate,
			final String locale) throws ELSException {
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.NONOFFICIAL_BILL, locale);
	
		Status UNDER_CONSIDERATION = Status.findByType(ApplicationConstants.BILL_PROCESSED_UNDERCONSIDERATION, locale);
		Status[] internalStatuses = new Status[] { UNDER_CONSIDERATION };
	
		Status INTRODUCED = Status.findByType(ApplicationConstants.BILL_PROCESSED_INTRODUCED, locale);		
		Status[] recommendationStatuses = new Status[] { INTRODUCED };
		
		List<Member> members = Bill.findMembersAllForBallot(session, deviceType, 
				 answeringDate, internalStatuses, recommendationStatuses, isPreBallot, ApplicationConstants.ASC, locale);
		
		return members;
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
	
	private static List<BallotEntry> createBillNonOfficialBallotEntries(final Session session, final DeviceType deviceType, final Date answeringDate, final List<Member> members,
			final String locale) throws ELSException {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		List<String> subjectList = new ArrayList<String>();
		
		for(Member m : members) {
			
			Bill bill = Bill.getBillForMemberOfUniqueSubject(session, deviceType, answeringDate, m.getId(), subjectList, locale);
			/**** Update the bill's discussionDate ****/
			if(bill != null){
				bill.setExpectedDiscussionDate(answeringDate);
				Status ballotedStatus = Status.findByType(ApplicationConstants.BILL_PROCESSED_BALLOTED, locale);
				bill.setBallotStatus(ballotedStatus);
				/**** Here the intimation to the member should be sent ****/
				bill.merge();
							
				subjectList.add(bill.getDefaultTitle());
				
				BallotEntry ballotEntry = new BallotEntry();
				ballotEntry.setMember(m);
				List<DeviceSequence> deviceSequences = Ballot.createDeviceSequences(bill, locale);
				ballotEntry.setDeviceSequences(deviceSequences);
				ballotEntries.add(ballotEntry);
			}
		}
		return ballotEntries;
	}
	
}
