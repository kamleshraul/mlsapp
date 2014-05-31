/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Ballot.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.poi.ss.formula.ptg.MemErrPtg;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.BillBallotVO;
import org.mkcl.els.common.vo.DeviceBallotVO;
import org.mkcl.els.common.vo.DeviceVO;
import org.mkcl.els.common.vo.QuestionSequenceVO;
import org.mkcl.els.common.vo.Reference;
import org.mkcl.els.common.vo.ResolutionBallotVO;
import org.mkcl.els.common.vo.RoundVO;
import org.mkcl.els.common.vo.StarredBallotVO;
import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.repository.BallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class Ballot.
 *
 * @author amitd
 * @since v1.0.0
 */
//	TODO: [IMP] Written in context of Half Hour Discussion but
//	is applicable for Starred Questions too.
//	Following are the Configuration parameters that needs to be captured:
//	1. Number of Ballot Entries. (Example: 2)
//	2. Should Questions ADMITTED for previous answering date be considered as
//	input for this ballot. (Example: YES/NO)
//	3. If answer to point 2 is YES, then how many dates back do you want to
//	refer. (Example: 1/2/..../ALL)
//	4. Should Questions BALLOTED but not DISCUSSED for previous answering date 
//	be considered as input for this ballot. (Example: YES/NO)
//	5. If answer to point 4 is YES, then how many dates back do you want to
//	refer. (Example: 1/2/..../ALL)
@Configurable
@Entity
@Table(name="ballots")
public class Ballot extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 8638638668842050930L;

	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	// TODO: Remove this attribute once you are sure that the Council Starred
	// Ballot does not require group attribute. Even if it is, the dependency
	// can be removed because given a session & answeringDate, group can be determined.
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private Group group;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="ballots_ballot_entries",
			joinColumns={ @JoinColumn(name="ballot_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") })
	private List<BallotEntry> ballotEntries;

	@Temporal(TemporalType.TIMESTAMP)
	private Date ballotDate;
	
	@Autowired
	private transient BallotRepository repository;

	
	//=============== CONSTRUCTORS ==================
	/**
	 * Not to be used. Kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public Ballot() {
		super();
	}

	/**
	 * To be used for Half Hour Discussion device.
	 */
	public Ballot(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Date ballotDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setDeviceType(deviceType);
		this.setAnsweringDate(answeringDate);
		this.setBallotDate(ballotDate);
	}

	
	//=============== VIEW METHODS ==================
	/**
	 * Returns null if Ballot does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no entries for the Ballot
	 * OR
	 * Returns a list of StarredBallotVO.
	 * @throws ELSException 
	 *
	 */
	public static List<StarredBallotVO> findStarredBallotVOs(final Session session,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = new ArrayList<StarredBallotVO>();
		
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				Long memberId = be.getMember().getId();
				String memberName = be.getMember().getFullnameLastNameFirst();
				List<QuestionSequenceVO> questionSequenceVOs =
					Ballot.getQuestionSequenceVOs(be.getDeviceSequences());

				StarredBallotVO ballotVO = new StarredBallotVO(memberId, 
						memberName, questionSequenceVOs);
				ballotVOs.add(ballotVO);
			}
		}
		else {
			ballotVOs = null;
		}
		
		return ballotVOs;
	}
	
	public static List<QuestionSequenceVO> findStarredQuestionSequenceVOs(final Session session,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<QuestionSequenceVO> ballotVOs = new ArrayList<QuestionSequenceVO>();
		
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				List<QuestionSequenceVO> questionSequenceVOs = Ballot.getQuestionSequenceVOs(be.getDeviceSequences());
				for(QuestionSequenceVO i: questionSequenceVOs) {
					i.setMemberId(be.getMember().getId());
				}
				ballotVOs.addAll(questionSequenceVOs);
			}
		}
		else {
			ballotVOs = null;
		}
		
		return ballotVOs;
	}

	private static List<QuestionSequenceVO> getQuestionSequenceVOs(
			final List<DeviceSequence> sequences) {
		List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
		for(DeviceSequence ds : sequences) {
			QuestionSequenceVO seqVO = new QuestionSequenceVO(((Question)ds.getDevice()).getId(),
					((Question)ds.getDevice()).getNumber(),
					ds.getSequenceNo());

			questionSequenceVOs.add(seqVO);
		}
		return questionSequenceVOs;
	}

	@SuppressWarnings("rawtypes")
	public static List<StarredBallotVO> findStarredPreBallotVOs(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<StarredBallotVO> ballotVOs = new ArrayList<StarredBallotVO>();
		
		Integer noOfRounds = Ballot.getNoOfRounds();
		Group group = Group.find(session, answeringDate, locale);
		 
		//find the preballot 
		PreBallot preBallot = PreBallot.find(session, deviceType, answeringDate, locale);
		List<BallotEntry> ballotEntries = null;
		
		if(preBallot == null){
			//if does not exists create a pre ballot and save it
			ballotEntries = Ballot.compute(session, group, answeringDate, noOfRounds, locale);
			if(!ballotEntries.isEmpty()){
				preBallot = new PreBallot(session, deviceType, answeringDate, new Date(System.currentTimeMillis()), locale);
				preBallot.setBallotEntries(ballotEntries);
				preBallot.persist();
			}
		}else{
			ballotEntries = preBallot.getBallotEntries();
		}
		
		Map<String, String[]> queryParameters = new HashMap<String, String[]>();
		queryParameters.put("preballotId", new String[]{preBallot.getId().toString()});
		queryParameters.put("locale", new String[]{preBallot.getLocale()});
		
		for(BallotEntry be : ballotEntries) {
			StarredBallotVO ballotVO = new StarredBallotVO();
			Member currentMember = be.getMember();
			ballotVO.setMemberId(currentMember.getId());
			CustomParameter memberNameFormatParameter = null;
			if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
				memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_PREBALLOT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
			} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
				memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_PREBALLOT_MEMBERNAMEFORMAT_LOWERHOUSE", "");
			}
			if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
				ballotVO.setMemberName(currentMember.findNameInGivenFormat(memberNameFormatParameter.getValue()));
			} else {
				ballotVO.setMemberName(currentMember.findNameInGivenFormat(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME));
			}			
			queryParameters.put("memberId", new String[]{currentMember.getId().toString()});
			List deviceSequences = Query.findReport("QIS_LOWERHOUSE_PREBALLOT_MEMBER_DEVICESEQUENCES", queryParameters);
			if(deviceSequences!=null && !deviceSequences.isEmpty()) {
				List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
				for(Object obj: deviceSequences) {
					if(obj!=null) {
						Object[] deviceSequence = (Object[]) obj;
						if(deviceSequence!=null && deviceSequence.length>=3) {
							QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
							if(deviceSequence[1]!=null && deviceSequence[2]!=null) {
								questionSequenceVO.setQuestionId(Long.parseLong(deviceSequence[1].toString()));						
								questionSequenceVO.setNumber(Integer.parseInt(deviceSequence[2].toString()));
								questionSequenceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
								questionSequenceVOs.add(questionSequenceVO);
							}
						}													
					}
				}
				ballotVO.setQuestionSequenceVOs(questionSequenceVOs);
			}			
			ballotVOs.add(ballotVO);
		}
		
		return ballotVOs;
	}
	
	public static List<BallotVO> findPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
				deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
			List<Question> questions = null;
			
			questions = Ballot.computeQuestionsForHalfHour(session, deviceType, answeringDate, false, locale);
			
			PreBallot preBallotHDQAssembly = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
			List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
			
			for(Question q : questions) {
				
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
			
			//persist the preballot list
			preBallotHDQAssembly.setBallotEntries(preBallotEntries);
			preBallotHDQAssembly.persist();
			
		}else if(deviceType.getType().startsWith(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
			List<Resolution> resolutions = Ballot.computeResolutionNonOfficial(session, answeringDate, locale);
			PreBallot preBallotResolution = new PreBallot(session, deviceType, answeringDate, new Date(), locale);
			List<BallotEntry> preBallotEntries = new ArrayList<BallotEntry>();
			
			for(Resolution r : resolutions) {
				
				{
					BallotEntry ballotEntry = new BallotEntry();
					ballotEntry.setMember(r.getMember());
					ballotEntry.setLocale(r.getLocale());
					
					List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
					deviceSequence.add(new DeviceSequence(r, r.getLocale()));
					ballotEntry.setDeviceSequences(deviceSequence);
					preBallotEntries.add(ballotEntry);
					
					deviceSequence = null;
					ballotEntry = null;
				}
				
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(r.getMember().getFullname());
				preBallotVO.setQuestionNumber(r.getNumber());
				preBallotVO.setQuestionSubject(r.getSubject());

				preBallotVOs.add(preBallotVO);
			}
			
			//persist the preballot list
			preBallotResolution.setBallotEntries(preBallotEntries);
			preBallotResolution.persist();
		} else if(deviceType.getType().startsWith(ApplicationConstants.NONOFFICIAL_BILL)){
			List<Bill> bills = Ballot.computeBillNonOfficial(session, answeringDate, true, locale);
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
		}		
		return preBallotVOs;
	}
	
	
	public static List<BallotVO> findResolutionCouncilPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
	
		List<Member> members = Ballot.computeMembersResolutionNonOfficial(session, true, answeringDate, locale);
			
		for(Member m : members) {
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(m.getFullname());
				preBallotVOs.add(preBallotVO);
		}	
		
		return preBallotVOs;
	}
	
	public static List<BallotMemberVO> findPreBallotMemberVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
				deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
			
			List<Member> members = null;
			if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				members = Ballot.computeMembers(session, deviceType, answeringDate, false, locale);
			}else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				members = Ballot.computeMembers(session,deviceType, answeringDate, false, locale);
			}
			for(Member m: members) {
				BallotMemberVO preBallotMemberVO = new BallotMemberVO();
				preBallotMemberVO.setMemberName(m.getFullname());
				
				preBallotMemberVOs.add(preBallotMemberVO);
			}
		}
		
		return preBallotMemberVOs;
	}
	
	
	//===================
	
	public static List<BallotMemberVO> findPreBallotMemberVOResolutionNonOfficial(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		
		List<Member> members = null;
		
		members = Ballot.computeMembers(session, deviceType, answeringDate, false, locale);
		
		for(Member m: members) {
			BallotMemberVO preBallotMemberVO = new BallotMemberVO();
			preBallotMemberVO.setMemberName(m.getFullname());
			
			preBallotMemberVOs.add(preBallotMemberVO);
		}
		
		return preBallotMemberVOs;
	}
	//======================
	/**
	 * Use it post Ballot.
	 * 
	 * Returns null if Ballot does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no entries for the Ballot
	 * OR
	 * Returns a list of HalfHourBallotVO.
	 * @throws ELSException 
	 *
	 */
	public static List<BallotVO> findBallotedVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> ballotedVOs = new ArrayList<BallotVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				BallotVO ballotedVO = new BallotVO();
				ballotedVO.setMemberName(entry.getMember().getFullname());
				
				// Hard coding for now. In case of notice ballot, a member 
				// could appear twice.
				List<DeviceSequence> qs = entry.getDeviceSequences();
				if(qs != null){
					if(qs.size() > 0){
						Device device = qs.get(0).getDevice();
						Question q = ((Question)device);
						ballotedVO.setQuestionNumber(q.getNumber());
						ballotedVO.setQuestionSubject(q.getSubject());
					}
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
	 * Returns a list of HalfHourBallotMemberVO.
	 * @throws ELSException 
	 *
	 */
	public static List<BallotMemberVO> findBallotedMemberVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotMemberVO> ballotedMemberVOs = new ArrayList<BallotMemberVO>();
		
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, "DB_TIMESTAMP", "");
		
		Date startTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionStartDate"),datePattern.getValue(), locale.toString());
		Date endTime = FormaterUtil.formatStringToDate(session.getParameter(deviceType.getType() + "_submissionEndDate"),datePattern.getValue(), locale.toString());
		
		Status ADMITTED = null;
		Status REJECTED = null;
		Status REPEAT_ADMITTED = null;
		Status REPEAT_REJECTED = null;
		if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
			ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_ADMISSION, locale.toString());
			REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REJECTION, locale.toString());
			REPEAT_ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REPEATADMISSION, locale.toString());
			REPEAT_REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REPEATREJECTION, locale.toString());
		}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
			ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale.toString());
			REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale.toString());
			REPEAT_ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale.toString());
			REPEAT_REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION, locale.toString());
		}
		Status[] internalStatuses = {ADMITTED, REJECTED, REPEAT_ADMITTED, REPEAT_REJECTED};
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				BallotMemberVO ballotedmemberVO = new BallotMemberVO();
				ballotedmemberVO.setMemberName(entry.getMember().getFullname());
				
				if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
					List<Question> questions = Question.findQuestionsByDiscussionDateAndMember(session, deviceType, entry.getMember().getId(), answeringDate, internalStatuses, startTime, endTime, ApplicationConstants.DESC, locale);
										
					int i = 0;
					for(Question q : questions){
						if(i == 0){
							ballotedmemberVO.setMemberChoiceNumber(FormaterUtil.formatNumberNoGrouping(q.getNumber(), locale));
							break;
						}
					}
					ballotedMemberVOs.add(ballotedmemberVO);
				}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
					
					List<Resolution> resolutions = Resolution.findResolutionsByDiscussionDateAndMember(session, deviceType, entry.getMember().getId(), answeringDate, internalStatuses, startTime, endTime, ApplicationConstants.DESC, locale);
								
					int i = 0;
					for(Resolution r : resolutions){
						if(i == 0){
							ballotedmemberVO.setMemberChoiceNumber(FormaterUtil.formatNumberNoGrouping(r.getNumber(), locale));
							break;
						}
					}
					ballotedMemberVOs.add(ballotedmemberVO);
				}
			}
		}
		else {
			ballotedMemberVOs = null;
		}
		
		return ballotedMemberVOs;
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
	
	
	//=============== DOMAIN METHODS ================
	/**
	 * A router that routes the ballot creation process to 
	 * the appropriate handler. 
	 * @throws ELSException 
	 */
	public Ballot create() throws ELSException {
		Ballot ballot = null;
		
		HouseType houseType = this.getSession().getHouse().getType();
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			if(this.getDeviceType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				ballot = this.createStarredAssemblyBallot();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
					ballot = this.createHalfHourAssemblyBallot();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
					ballot = this.createHalfHourAssemblyBallotStandAlone();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				ballot = this.createResolutionNonOfficialAssemblyBallot();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				ballot = this.createBillNonOfficialBallot();
			}
		}else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			if(this.getDeviceType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				ballot = this.createStarredCouncilBallot();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
				ballot = this.createHalfHourCouncilBallot();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
				ballot = this.createHDSCouncilBallot();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)){
				ballot = this.createCouncilBallotResolutionNonOfficial();
			}else if(this.getDeviceType().getType().equals(ApplicationConstants.NONOFFICIAL_BILL)){
				ballot = this.createBillNonOfficialBallot();
			}
		}
		
		return ballot;
	}
	
	// TODO: [IMP] The "if" condition given below tightly couples a device with
	// a ballot type (Member, Device or Subject). Read the condition from
	// configuration (inputs will be session & deviceType).
	public String update(final Member member,
			final Question question) throws IllegalAccessException, ELSException {
		HouseType houseType = this.getSession().getHouse().getType();
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE) && 
				this.getDeviceType().getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION) ||
				deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)) {
			this.updateMemberBallot(member, question);
		}
		else {
			throw new IllegalAccessException("This method is not applicable for the device type: " + 
					this.getDeviceType().getType());
		}
		return null;
	}
	
	/**
	 * Returns null if there is no Ballot for the specified parameters.
	 * @throws ELSException 
	 */
	public static Ballot find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return Ballot.getRepository().find(session, deviceType, answeringDate, locale);
	}
	
	/**
	 * Returns the list of Questions of @param member taken in a Ballot
	 * for the particular @param answeringDate.
	 *
	 * Returns an empty list if there are no Questions for member.
	 * @throws ELSException 
	 */
	public static List<Question> findBallotedQuestions(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return Ballot.getRepository().findBallotedQuestions(member, 
				session, deviceType, answeringDate, locale);
	}
	
	
	//=============== ASSEMBLY: STARRED QUESTION BALLOT ==============
	
	/**
	 * 3 stepped process:
	 * 1> Compute Ballot entries.
	 * 2> Randomize Ballot entries.
	 * 3> Add sequence numbers.
	 *
	 * Creates a new Ballot. If a ballot already exists then return the
	 * existing Ballot.
	 * @throws ELSException 
	 */
//	private Ballot createStarredAssemblyBallot() throws ELSException {
//		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
//				this.getAnsweringDate(), this.getLocale());
//		
//		if(ballot == null) {
//			Integer noOfRounds = Ballot.getNoOfRounds();
//			Group group = Group.find(session, answeringDate, this.getLocale());
//			
//			List<BallotEntry> computedList = Ballot.compute(this.getSession(),
//					group, this.getAnsweringDate(), noOfRounds, this.getLocale());
//			List<BallotEntry> randomizedList = Ballot.randomize(computedList);
//			List<BallotEntry> sequencedList = Ballot.addSequenceNumbers(randomizedList, noOfRounds);
//
//			this.setBallotEntries(sequencedList);
//			this.setGroup(group);
//			ballot = (Ballot) this.persist();
//			
//			Status BALLOTED = 
//				Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, this.getLocale());
//			Ballot.getRepository().updateBallotQuestions(ballot, BALLOTED);
//		}
//		
//		return ballot;
//	}
	private Ballot createStarredAssemblyBallot() throws ELSException {
		Session session = this.getSession();
		DeviceType deviceType = this.getDeviceType();
		Date answeringDate = this.getAnsweringDate();
		String locale = this.getLocale();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		
		if(ballot == null) {
			Integer noOfRounds = Ballot.getNoOfRounds();			
			PreBallot preBallot = 
				PreBallot.find(session, deviceType, answeringDate, locale);
			if(preBallot == null) {
				throw new ELSException("Ballot_createStarredAssemblyBallot", "PRE_BALLOT_NOT_CREATED");
			}
			else {
				List<BallotEntry> preBallotList = preBallot.getBallotEntries(); 
				List<BallotEntry> randomizedList = Ballot.randomize(preBallotList);
				List<BallotEntry> sequencedList = Ballot.addSequenceNumbers(randomizedList, noOfRounds);

				this.setBallotEntries(sequencedList);
				this.setGroup(group);
				ballot = (Ballot) this.persist();
				
				Status BALLOTED = 
					Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, this.getLocale());
				Ballot.getRepository().updateBallotQuestions(ballot, BALLOTED);
			}
		}
		
		return ballot;
	}
	
	private static Integer getNoOfRounds() throws ELSException {
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,"QUESTION_BALLOT_NO_OF_ROUNDS", "");
		return Integer.valueOf(parameter.getValue());
	}
	
	/**
	 * Only members having any Question eligible for this ballot will
	 * appear in the Ballot.
	 * @throws ELSException 
	 */
	private static List<BallotEntry> compute(final Session session,
			final Group group,
			final Date answeringDate,
			final Integer noOfRounds,
			final String locale) throws ELSException {
		List<BallotEntry> entries = new ArrayList<BallotEntry>();

		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		
		Status internalStatus = 
			Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status ballotStatus =
			Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
		
		List<Member> eligibleMembers = Ballot.getRepository().findMembersEligibleForBallot(session, 
				deviceType, group, answeringDate, internalStatus, ballotStatus, locale);
		for(Member m : eligibleMembers) {
			BallotEntry ballotEntry = Ballot.compute(m, session, deviceType, group,
					answeringDate, internalStatus, ballotStatus, noOfRounds, locale);

			if(ballotEntry != null) {
				entries.add(ballotEntry);
			}
		}

		return entries;
	}

	/**
	 * Algorithm:
	 * 1> Create a list of Questions eligible for ballot for all the answeringDates
	 * (including currentAnsweringDate and previousAnsweringDates)
	 * 
	 * 2> Create a list of Balloted Questions for the previousAnsweringDates.
	 * 
	 * 3> The difference between Step 1 list and Step 2 list is the eligible list of
	 * Questions for the current Ballot.
	 * 
	 * 4> Choose as many as @param noOfRounds Questions from Step 3 list. These are the
	 * Questions to be taken on the current ballot.
	 *
	 *
	 * Eligibility Algorithm:
	 * A Question is eligible for ballot only if its internalStatus = "ADMITTED",
	 * ballotStatus != "BALLOTED" and it has no parent Question. If a Question has a 
	 * parent, then it's parent may be considered for the Ballot. The kid will never be 
	 * considered for the Ballot.
	 *
	 *
	 * Returns a subset of @param questions sorted by priority. If there are no
	 * questions eligible for the ballot, returns an empty list.
	 * Returns null if at the end of Step 4 the @param member do not have any Questions
	 * in the list.
	 * @throws ELSException 
	 */
	private static BallotEntry compute(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final Status internalStatus,
			final Status ballotStatus,
			final Integer noOfRounds,
			final String locale) throws ELSException {
		BallotEntry ballotEntry = null;
		
		List<Question> eligibleQuestions = 
			Ballot.getRepository().findQuestionsEligibleForBallot(member, session, deviceType, 
					group, answeringDate, internalStatus, ballotStatus, noOfRounds, locale);
		if(! eligibleQuestions.isEmpty()) {
			List<DeviceSequence> questionSequences = 
				Ballot.createQuestionSequences(eligibleQuestions, locale);
			ballotEntry = new BallotEntry(member, questionSequences, locale);
		}
		
		return ballotEntry;
	}

	/**
	 * Does not shuffle in place, returns a new list.
	 */
	private static List<BallotEntry> randomize(final List<BallotEntry> ballotEntryList) {
		List<BallotEntry> newBallotEntryList = new ArrayList<BallotEntry>();
		newBallotEntryList.addAll(ballotEntryList);
		Long seed = System.nanoTime();
		Random rnd = new Random(seed);
		Collections.shuffle(newBallotEntryList, rnd);
		return newBallotEntryList;
	}
	
	/**
	 * Returns a new list.
	 */
	private static List<BallotEntry> addSequenceNumbers(final List<BallotEntry> ballotEntryList,
			final Integer noOfRounds) {
		List<BallotEntry> newBallotEntryList = new ArrayList<BallotEntry>();
		newBallotEntryList.addAll(ballotEntryList);

		Integer sequenceNo = new Integer(0);
		for(int i = 0; i < noOfRounds; i++) {
			for(BallotEntry be : newBallotEntryList) {
				List<DeviceSequence> qsList = be.getDeviceSequences();
				if(qsList.size() > i) {
					DeviceSequence qs = qsList.get(i);
					qs.setSequenceNo(++sequenceNo);
				}
			}
		}
		return newBallotEntryList;
	}

	/**
	 * Creates the question sequences.
	 */
	private static List<DeviceSequence> createQuestionSequences(final List<Question> questions,
			final String locale) {
		List<DeviceSequence> questionSequences = new ArrayList<DeviceSequence>();
		for(Question q : questions) {
			DeviceSequence qs = new DeviceSequence(q, locale);
			questionSequences.add(qs);
		}
		return questionSequences;
	}
	
	
	//=============== COUNCIL: STARRED QUESTION BALLOT ===============
	public Ballot createStarredCouncilBallot() {
		return null;
	}
	
	
	//=============== ASSEMBLY: HALF HOUR DISCUSSION BALLOT ==========
	public Ballot createHalfHourAssemblyBallot() throws ELSException {
		return this.createMemberBallot();
	}
	
	//=============== ASSEMBLY: HALF HOUR DISCUSSION STAND ALONE BALLOT ==========
	public Ballot createHalfHourAssemblyBallotStandAlone() throws ELSException {
		return this.createBallotHDS();
	}
	
	//=============== ASSEMBLY: NONOFFICIAL RESOLUTION BALLOT ==========
	public Ballot createResolutionNonOfficialAssemblyBallot() throws ELSException {
		return this.createBallotResolutionNonOfficial();
	}
		
	//=============== ASSEMBLY: NONOFFICIAL BILL BALLOT ==========
	public Ballot createBillNonOfficialBallot() throws ELSException {
		return this.createBallotBillNonOfficial_UniqueSubject();
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
	public Ballot createMemberBallot() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, this.getDeviceType().getType().toUpperCase() + "_" + this.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			
			if(csptUniqueFlag != null && csptUniqueFlag.getValue() != null && !csptUniqueFlag.getValue().isEmpty()){
				if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
					computedList = Ballot.computeMembers(this.getSession(),
								this.getDeviceType(),
								this.getAnsweringDate(),
								true,
								this.getLocale());
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
			
			List<Member> finalComputedList = getUniqueMembers(session, deviceType, computedList, "member");
			if(finalComputedList.size() < outPutCount){
				finalComputedList = Ballot.computeMembers(this.getSession(),
						this.getDeviceType(),
						this.getAnsweringDate(),
						false,
						this.getLocale());
			}
			
			List<Member> randomizedList = Ballot.randomizeMembers(finalComputedList);
			
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, outPutCount);
			
			List<BallotEntry> ballotEntries = Ballot.createMemberBallotEntries(selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
	}
	
	private List<Member> getUniqueMembers(Session session, DeviceType deviceType, List<Member> members, String memberNotice){
		StringBuffer memberList = new StringBuffer("");
		String returnData = Question.findBallotedMembers(session, memberNotice, deviceType);
		memberList.append(( returnData == null)? "":returnData);
		List<Member> newMs = new ArrayList<Member>();
		if(!memberList.toString().isEmpty()){
			for(Member m : members){
				if(!isExistingInList(memberList.toString(), m.getId().toString())){
					newMs.add(m);
				}
			}
		}else{
			newMs.addAll(members);
		}
		
		return newMs;
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
	public Ballot createMemberBallotStandAlone() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, this.getDeviceType().getType().toUpperCase() + "_" + this.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			
			if(csptUniqueFlag != null && csptUniqueFlag.getValue() != null && !csptUniqueFlag.getValue().isEmpty()){
				if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
					computedList = Ballot.computeMembers(this.getSession(),
								this.getDeviceType(),
								this.getAnsweringDate(),
								true,
								this.getLocale());
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
			List<Member> finalComputedList = getUniqueMembers(session, deviceType, computedList, "member");
			if(finalComputedList.size() < outPutCOunt){
				finalComputedList = Ballot.computeMembers(this.getSession(),
						this.getDeviceType(),
						this.getAnsweringDate(),
						false,
						this.getLocale());
			}
			
			List<Member> randomizedList = Ballot.randomizeMembers(finalComputedList);
			// Read the constant 2 as a configurable parameter
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, outPutCOunt);
			
			List<BallotEntry> ballotEntries = Ballot.createMemberBallotEntries(selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
	}
	
	public Ballot createBallotResolutionNonOfficial() throws ELSException{
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = Ballot.computeMembersResolutionNonOfficial(this.getSession(),
					false,
					this.getAnsweringDate(),
					this.getLocale());
			List<Member> randomizedList = Ballot.randomizeMembers(computedList);
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_ASSEMBLY, "");
			Integer ballotOutputCount = null;
			if(ballotOutputCountCustomParameter != null){
				ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
			}else{
				ballotOutputCount = 5;
			}
			
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, ballotOutputCount);
			
			List<BallotEntry> ballotEntries = Ballot.createResolutionNonOfficialBallotEntries(this.getSession(), this.getDeviceType(), this.getAnsweringDate(), selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
	}
	
	public Ballot createBallotBillNonOfficial_UniqueMember() throws ELSException{
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(),
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = Ballot.computeMembersBillNonOfficial(this.getSession(),
					false,
					this.getAnsweringDate(),
					this.getLocale());
			List<Member> randomizedList = Ballot.randomizeMembers(computedList);
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.BILL_NONOFFICIAL_BALLOT_OUTPUT_COUNT, "");
			Integer ballotOutputCount = null;
			if(ballotOutputCountCustomParameter != null){
				ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
			}else{
				ballotOutputCount = 6;
			}
			
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, ballotOutputCount);
			
			List<BallotEntry> ballotEntries = Ballot.createBillNonOfficialBallotEntries(this.getSession(), this.getDeviceType(), this.getAnsweringDate(), selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
	}
	
	public Ballot createBallotBillNonOfficial_UniqueSubject() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Bill> bills = Ballot.computeBillNonOfficial(this.getSession(), this.getAnsweringDate(), false, this.getLocale());
			List<Bill> randomizedList = Ballot.randomizeBills(bills);
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCustomParameter =  CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.BILL_NONOFFICIAL_BALLOT_OUTPUT_COUNT, "");
			Integer ballotOutputCount = null;
			if(ballotOutputCountCustomParameter != null){
				ballotOutputCount = new Integer(ballotOutputCountCustomParameter.getValue());
			}else{
				ballotOutputCount = 6;
			}			
			List<Bill> selectedList = Ballot.selectBillsForBallot(randomizedList, ballotOutputCount);
			List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();				
			for(Bill b : selectedList) {				
				BallotEntry ballotEntry = new BallotEntry();
				ballotEntry.setMember(b.getPrimaryMember());
				ballotEntry.setLocale(b.getLocale());					
				List<DeviceSequence> deviceSequence = new ArrayList<DeviceSequence>(1);
				deviceSequence.add(new DeviceSequence(b, b.getLocale()));
				ballotEntry.setDeviceSequences(deviceSequence);
				ballotEntries.add(ballotEntry);					
				deviceSequence = null;
				ballotEntry = null;								
			}
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();
		}
		
		return ballot;
	}
	
	//======added for HDS balloting
	public Ballot createBallotHDS() throws ELSException{
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, this.getDeviceType().getType().toUpperCase() + "_" + this.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			if(csptUniqueFlag == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("QUESTIONS_HALFHOURDISCUSSION_STANDALONE_UNIQUE_FLAG_MEMBER_BALLOT", "Custom Parameters for QUESTIONS_HALFHOURDISCUSSION_STANDALONE_UNIQUE_FLAG_MEMBER_BALLOT is not set.");
				throw elsException;
			}
			
			if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
				computedList = Ballot.computeMembers(this.getSession(),
							this.getDeviceType(),
							this.getAnsweringDate(),
							true,
							this.getLocale());
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter hdsAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(hdsAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCount = Integer.parseInt(hdsAssemblyBallotOutPutCount.getValue());
			List<Member> finalComputedList = getUniqueMembers(session, deviceType, computedList, "member");
			if(finalComputedList.size() < outPutCount){
				finalComputedList = Ballot.computeMembers(this.getSession(),
						this.getDeviceType(),
						this.getAnsweringDate(),
						false,
						this.getLocale());
			}
			
			List<Member> randomizedList = Ballot.randomizeMembers(finalComputedList);
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, outPutCount);
			
			List<BallotEntry> ballotEntries = Ballot.createHDSBallotEntries(this.getSession(), this.getDeviceType(), this.getAnsweringDate(), selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
	}
	
	private static List<BallotEntry> createHDSBallotEntries(final Session session, final DeviceType deviceType, final Date answeringDate, final List<Member> members,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		
		for(Member m : members) {
			
			//Question question = Question.getQuestionForMemberOfUniqueSubject(session, deviceType, answeringDate, m.getId(), subjectList, locale);
			//**** Update the questions discussionDate ****//*
			
			BallotEntry ballotEntry = new BallotEntry();
			ballotEntry.setMember(m);
			ballotEntries.add(ballotEntry);
		}
		
		/*for(Member m : members) {
			
			Question question = Question.getQuestionForMemberOfUniqueSubject(session, deviceType, answeringDate, m.getId(), subjectList, locale);
			*//**** Update the questions discussionDate ****//*
			if(question != null){
				question.setDiscussionDate(answeringDate);
				Status ballotedStatus = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, locale);
				question.setBallotStatus(ballotedStatus);
				*//**** Here the intimation to the member should be sent ****//*
				question.merge();
							
				subjectList.add(question.getSubject());
				
				BallotEntry ballotEntry = new BallotEntry();
				ballotEntry.setMember(m);
				List<DeviceSequence> deviceSequences = Ballot.createDeviceSequences(question, locale);
				ballotEntry.setDeviceSequences(deviceSequences);
				ballotEntries.add(ballotEntry);
			}
		}*/
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
	
		List<Member> members = Ballot.computeMembersHDSPreBallot(session, answeringDate, locale);
			
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
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE, locale);
	
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
	public Ballot createMemberBallotHDS() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			
			List<Member> computedList = null;
			
			CustomParameter csptUniqueFlag = CustomParameter.findByName(CustomParameter.class, this.getDeviceType().getType().toUpperCase() + "_" + this.getSession().getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_MEMBER_BALLOT", "");
			if(csptUniqueFlag == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("QUESTIONS_HALFHOURDISCUSSION_STANDALONE_UNIQUE_FLAG_MEMBER_BALLOT", "Custom Parameters for QUESTIONS_HALFHOURDISCUSSION_STANDALONE_UNIQUE_FLAG_MEMBER_BALLOT is not set.");
				throw elsException;
			}
			
			if(csptUniqueFlag.getValue().equalsIgnoreCase("YES")){
				computedList = Ballot.computeMembers(this.getSession(),
							this.getDeviceType(),
							this.getAnsweringDate(),
							true,
							this.getLocale());
			}
			
			// Read the constant 2 as a configurable parameter
			CustomParameter hdsAssemblyBallotOutPutCount = CustomParameter.findByName(CustomParameter.class, "HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "");
			if(hdsAssemblyBallotOutPutCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter("HDS_ASSEMBLY_BALLOT_OUTPUT_COUNT", "Custom Parameters for HDQ_ASSEMBLY_BALLOT_OUTPUT_COUNT is not set.");
				throw elsException;
			}
			
			int outPutCount = Integer.parseInt(hdsAssemblyBallotOutPutCount.getValue());
			List<Member> finalComputedList = getUniqueMembers(session, deviceType, computedList, "member");
			if(finalComputedList.size() < outPutCount){
				finalComputedList = Ballot.computeMembers(this.getSession(),
						this.getDeviceType(),
						this.getAnsweringDate(),
						false,
						this.getLocale());
			}
			
			List<Member> randomizedList = Ballot.randomizeMembers(finalComputedList);
			
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCP = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.QUESTIONS_HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL, null);
			Integer ballotOutput = null;
			if(ballotOutputCountCP == null){
				ballotOutput = new Integer(3);
			}else{
				ballotOutput = new Integer(ballotOutputCountCP.getValue());
			}
			
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, ballotOutput);
			
			List<BallotEntry> ballotEntries = Ballot.createMemberBallotEntries(selectedList, this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
		
	}
	
	public Ballot createMemberBallotHDQAssembly() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			
			List<Member> computedList = Ballot.computeMembers(this.getSession(), this.getDeviceType(), 
					this.getAnsweringDate(), true, this.getLocale());
					
			List<Member> randomizedList = Ballot.randomizeMembers(computedList);
			
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCP = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.QUESTIONS_HALFHOURDISCUSSION_STANDALONE_BALLOT_OUTPUT_COUNT_COUNCIL, null);
			Integer ballotOutput = null;
			if(ballotOutputCountCP == null){
				ballotOutput = new Integer(3);
			}else{
				ballotOutput = new Integer(ballotOutputCountCP.getValue());
			}
			
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, ballotOutput);
			
			List<BallotEntry> ballotEntries = Ballot.createMemberBallotEntries(selectedList, this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
		
	}
	//===============hds balloting======================================end==========
	
	private void updateMemberBallot(final Member member,
			final Question question) throws ELSException {
		BallotEntry ballotEntry = Ballot.findBallotEntry(member, this.getSession(),
				this.getDeviceType(), this.getAnsweringDate(), this.getLocale());
		List<DeviceSequence> questionSequences = 
			Ballot.createDeviceSequences(question, this.getLocale());
		ballotEntry.setDeviceSequences(questionSequences);
		ballotEntry.merge();
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
		
		Status ADMITTED = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
		Status[] internalStatuses = new Status[] { ADMITTED };
		
		// TODO: [FATAL] internal Status will only refer to the lifecycle of a Question in the 
		// Workflow i.e till ADMITTED. The further statuses of the Question viz Sent to department,
		// Balloted, Discussed will be captured in recommendationStatus. So, in compute Members
		// the condition should be: For all the active members who have submitted "half hour 
		// discussion from question" between the specified time window (start time & end time) &
		// whose questions have been admitted (internal status = "ADMITTED") and not balloted
		// (recommendation status = "BALLOTED" or "DISCUSSED" or any further status) are to be
		// picked up for this Ballot.
		
		List<Member> members = null;
		//if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)){
		if(isUnique.booleanValue()){
			members = Question.findPrimaryMembersByBallot(session, deviceType, answeringDate, internalStatuses, false, false, startTime, endTime, ApplicationConstants.ASC, locale);
		}else{
			members = Question.findPrimaryMembers(session, deviceType,answeringDate, internalStatuses, false, startTime, endTime,ApplicationConstants.ASC, locale);
		}
		/*}else{
			members = Question.findPrimaryMembers(session, deviceType,answeringDate, internalStatuses, false, startTime, endTime,ApplicationConstants.ASC, locale);
		}*/
		
		return members;
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
		Status[] internalStatuses = new Status[] { ADMITTED };
				
		List<Member> members = Resolution.findMembersEligibleForTheBallot(session, deviceType, answeringDate, internalStatuses, startTime, endTime, sortOrder, locale); 
		
		return members;
	}
	
	/**
	 * For resolution nonofficial
	 * @param session
	 * @param answeringDate
	 * @param locale
	 * @return
	 * @throws ELSException 
	 */
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
		Status[] internalStatuses = new Status[] { ADMITTED };
		
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
	
	/**
	 * For resolution nonofficial
	 * @param session
	 * @param answeringDate
	 * @param locale
	 * @return
	 * @throws ELSException 
	 */
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
	
	private static List<BallotEntry> createMemberBallotEntries(final List<Member> members,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		for(Member m : members) {
			BallotEntry ballotEntry = new BallotEntry(m, locale);
			ballotEntries.add(ballotEntry);
		}
		return ballotEntries;
	}
	
	//resolution ballot
	private static List<BallotEntry> createResolutionNonOfficialBallotEntries(final Session session, final DeviceType deviceType, final Date answeringDate, final List<Member> members,
			final String locale) throws ELSException {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		List<String> subjectList = new ArrayList<String>();
		
		for(Member m : members) {
			
			Resolution resolution = Resolution.getResolutionForMemberOfUniqueSubject(session, deviceType, answeringDate, m.getId(), subjectList, locale);
			/**** Update the resolution's discussionDate ****/
			if(resolution != null){
				resolution.setDiscussionDate(answeringDate);
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
	
	//bill ballot
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
	
	private static BallotEntry findBallotEntry(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return Ballot.getRepository().find(member, session, deviceType, answeringDate, locale);
	}
	
	
	//=============== COUNCIL: HALF HOUR DISCUSSION BALLOT ===========
	public Ballot createHalfHourCouncilBallot() throws ELSException {
		return this.createNoticeBallot();
	}
	
	//=============== COUNCIL: HALF HOUR DISCUSSION STAND ALONE BALLOT ===========
	public Ballot createHDSCouncilBallot() throws ELSException {
		return this.createNoticeBallot();
	}
	
	//============COUNCIL: RESOLUTION NONOFFICIAL=======================
	public Ballot createCouncilBallotResolutionNonOfficial() throws ELSException {
		return this.createMemberBallotResolutionNonOfficial();
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
	public Ballot createNoticeBallot() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Question> computedList = Ballot.computeQuestionsForHalfHour(this.getSession(),
					this.getDeviceType(),
					this.getAnsweringDate(),
					true,
					this.getLocale());			
			
			// Read the constant 2 as a configurable parameter
			CustomParameter councilBallotCount = CustomParameter.findByFieldName(CustomParameter.class, "name", this.getDeviceType().getType().toUpperCase()+"_"+this.getSession().getHouse().getType().getType().toUpperCase()+"_BALLOT_OUTPUT_COUNT", "");
			
			if(councilBallotCount == null){
				ELSException elsException = new ELSException();
				elsException.setParameter(this.getDeviceType().getType().toUpperCase()+"_"+this.getSession().getHouse().getType().getType().toUpperCase()+"_BALLOT_OUTPUT_COUNT","Custom Parameter for output count is not found.");
				throw elsException;
			}
			
			int outputCount = Integer.parseInt(councilBallotCount.getValue());
			if( outputCount > computedList.size()){
				computedList = Ballot.computeQuestionsForHalfHour(this.getSession(),
						this.getDeviceType(),
						this.getAnsweringDate(),
						false,
						this.getLocale());
			}
			CustomParameter csptUniqueFlagForNoticeBallot = CustomParameter.findByName(CustomParameter.class, deviceType.getType().toUpperCase() + "_" + session.getHouse().getType().getType().toUpperCase() + "_UNIQUE_FLAG_NOTICE_BALLOT", "");
			
			if(csptUniqueFlagForNoticeBallot != null && csptUniqueFlagForNoticeBallot.getValue() != null && !csptUniqueFlagForNoticeBallot.getValue().isEmpty()){
				if(csptUniqueFlagForNoticeBallot.getValue().equalsIgnoreCase("YES")){
					computedList = getUniqueMemberSubjectQuestion(this.getSession(), this.getDeviceType(), computedList, "notice");
				}
			}
			List<Question> randomizedList = Ballot.randomizeQuestions(computedList);
			List<Question> selectedList = Ballot.selectQuestionsForBallot(randomizedList, Integer.valueOf(councilBallotCount.getValue()));
			List<BallotEntry> ballotEntries = Ballot.createNoticeBallotEntries(selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		Status BALLOTED = Status.findByType(ApplicationConstants.QUESTION_PROCESSED_BALLOTED, this.getLocale());
		Ballot.getRepository().updateBallotQuestions(ballot, BALLOTED);
		
		return ballot;
		
	}	
	
	private List<Question> getUniqueMemberSubjectQuestion(Session session, DeviceType deviceType, List<Question> questions, String memberNotice){
		StringBuffer memberList = new StringBuffer(Question.findBallotedMembers(session, memberNotice, deviceType));
		StringBuffer subjectList = new StringBuffer(Question.findBallotedMembers(session, memberNotice, deviceType));
		List<Question> newQuestionList = new ArrayList<Question>();
		if(questions != null && !questions.isEmpty()){
			for(Question q : questions){
				if(!isExistingInList(memberList.toString(), q.getPrimaryMember().getId().toString())){
					if(!isExistingInList(subjectList.toString(), q.getRevisedSubject())){
						memberList.append(q.getPrimaryMember().getId().toString()+"##");
						subjectList.append(q.getRevisedSubject()+"##");
						newQuestionList.add(q);
					}
				}
			}
		}
		
		return newQuestionList;
	}
	
	private boolean isExistingInList(String list, String data){
		return list.contains(data);
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
	public Ballot createMemberBallotResolutionNonOfficial() throws ELSException {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			
			List<Member> computedList = Ballot.computeMembersEligibleForTheBallot(session, deviceType, answeringDate, ApplicationConstants.ASC, this.getLocale());
					
			List<Member> randomizedList = Ballot.randomizeMembers(computedList);
			
			// Read the constant 5 as a configurable parameter
			CustomParameter ballotOutputCountCP = CustomParameter.findByFieldName(CustomParameter.class, "name", ApplicationConstants.RESOLUTION_NONOFFICIAL_BALLOT_OUTPUT_COUNT_COUNCIL, null);
			Integer ballotOutput = null;
			if(ballotOutputCountCP == null){
				ballotOutput = new Integer(3);
			}else{
				ballotOutput = new Integer(ballotOutputCountCP.getValue());
			}
			
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, ballotOutput);
			
			List<BallotEntry> ballotEntries = Ballot.createMemberBallotEntries(selectedList, this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
		
	}	
	
	private static List<Question> computeQuestionsForHalfHour(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final Boolean isMandatoryUnique,
			final String locale) throws ELSException {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
	
		Date startTime = FormaterUtil.formatStringToDate(session.
				getParameter(deviceType.getType() + "_submissionStartDate"), 
				datePattern.getValue(), locale);
		Date endTime = FormaterUtil.formatStringToDate(session.
				getParameter(deviceType.getType() + "_submissionEndDate"), 
				datePattern.getValue(), locale);
		
		Status ADMITTED = Status.findByType(ApplicationConstants.QUESTION_FINAL_ADMISSION, locale);
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
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_STANDALONE)
				|| deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)){
			questions = Question.findByBallot(session, deviceType, answeringDate, internalStatuses, false, false, isMandatoryUnique, startTime, endTime, ApplicationConstants.ASC, locale);
		}
		
		return questions;
	}

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
		Status[] internalStatuses = new Status[] { ADMITTED };
		
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
	
	/**
	 * A subset of eligible Resolution of size @param maxQuestions are taken in Ballot.
	 */
//	private static List<Resolution> selectResolutionsForBallot(final List<Resolution> resolutions,
//			final Integer maxResolutions) {
//		List<Resolution> selectedQList = new ArrayList<Resolution>();
//		selectedQList.addAll(resolutions);
//		if(selectedQList.size() >= maxResolutions) {
//			selectedQList = selectedQList.subList(0, maxResolutions); 
//		}
//		return selectedQList;
//	}
	
	private static List<BallotEntry> createNoticeBallotEntries(final List<Question> questions,
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
	
	
	//=============== INTERNAL METHODS ==============
	private static BallotRepository getRepository() {
		BallotRepository repository = new Ballot().repository;
		if(repository == null) {
			throw new IllegalStateException(
				"BallotRepository has not been injected in Ballot Domain");
		}
		return repository;
	}

	private static List<DeviceSequence> createDeviceSequences(final Device q,
			final String locale) {
		List<DeviceSequence> sequences = new ArrayList<DeviceSequence>();
		DeviceSequence sequence = new DeviceSequence(q, locale);
		sequences.add(sequence);
		return sequences;
	}
	
//	private static List<Resolution> randomizeResolutions(final List<Resolution> resolutions) {
//		List<Resolution> newResolution = new ArrayList<Resolution>();
//		newResolution.addAll(resolutions);
//		Long seed = System.nanoTime();
//		Random rnd = new Random(seed);
//		Collections.shuffle(newResolution, rnd);
//		return newResolution;
//	}
	
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
	public static List<ResolutionBallotVO> findMemberSubjectBallotVO(final Session session,
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
	
	public static List<ResolutionBallotVO> createPatrakBhagTwo(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<ResolutionBallotVO> ballotedVOs = findMemberSubjectBallotVO(session, deviceType, answeringDate, locale);
		
		return ballotedVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<DeviceVO> findBallotedQuestionVOs(final Session session, final DeviceType deviceType, final Group group, final Date answeringDate,
			final String locale) throws ELSException {			
		List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();		
		
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("sessionId", new String[]{session.getId().toString()});
		parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		if(deviceType.getDevice().equals("Question")) {
			parametersMap.put("groupId", new String[]{group.getId().toString()});
		}		
		parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
		List ballotVOs = org.mkcl.els.domain.Query.findReport("YADI_BALLOT_VIEW", parametersMap);
		parametersMap = null;
		
		if(ballotVOs!=null && !ballotVOs.isEmpty()) {
			List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
			for(Object i: ballotVOs) {
				Object[] ballotVO = (Object[])i;
				QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
				if(ballotVO[0]!=null && !ballotVO[0].toString().isEmpty()) {
					questionSequenceVO.setMemberId(Long.parseLong(ballotVO[0].toString()));
				}
				if(ballotVO[1]!=null && !ballotVO[1].toString().isEmpty()) {
					questionSequenceVO.setQuestionId(Long.parseLong(ballotVO[1].toString()));
				}
				if(ballotVO[2]!=null && !ballotVO[2].toString().isEmpty()) {
					questionSequenceVO.setNumber(Integer.parseInt(ballotVO[2].toString()));
				}
				if(ballotVO[3]!=null && !ballotVO[3].toString().isEmpty()) {
					questionSequenceVO.setSequenceNo(Integer.parseInt(ballotVO[3].toString()));
				}
				questionSequenceVOs.add(questionSequenceVO);
			}
			QuestionSequenceVO.sortBySequenceNumber(questionSequenceVOs);
			int count=0;
			for(QuestionSequenceVO questionSequenceVO: questionSequenceVOs) {
				System.out.println(questionSequenceVO.getNumber() + ": " + questionSequenceVO.getSequenceNo());
				DeviceVO deviceVO = new DeviceVO();
				count++;
				deviceVO.setSerialNumber(FormaterUtil.formatNumberNoGrouping(count, locale));
				deviceVO.setId(questionSequenceVO.getQuestionId());
				deviceVO.setNumber(questionSequenceVO.getNumber());
				deviceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVO.getNumber(), locale));
				Question q = Question.findById(Question.class, questionSequenceVO.getQuestionId());
//				String memberNames="";
//				Member member=q.getPrimaryMember();
//				if(member!=null){
//					memberNames+=member.findFirstLastName();
//				}
//				List<SupportingMember> selectedSupportingMembers=q.getSupportingMembers();					
//				if(selectedSupportingMembers!=null){
//					if(!selectedSupportingMembers.isEmpty()){
//						StringBuffer bufferFirstNamesFirst=new StringBuffer();
//						for(SupportingMember sm:selectedSupportingMembers){
//							Member m=sm.getMember();
//							bufferFirstNamesFirst.append(m.findFirstLastName()+",");								
//						}
//						bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
//						memberNames+=","+bufferFirstNamesFirst.toString();
//					}
//				}
				String houseType = session.findHouseType();
				String allMemberNames = "";
				CustomParameter memberNameFormatParameter = null;
				String memberNameFormat = null;
				if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_LOWERHOUSE", "");
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						memberNameFormat = memberNameFormatParameter.getValue();						
					} else {
						memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;						
					}
					allMemberNames = q.findAllMemberNamesWithConstituencies(memberNameFormat);
				} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
					Member pmember = Member.findById(Member.class, questionSequenceVO.getMemberId());
					memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_YADI_MEMBERNAMEFORMAT_UPPERHOUSE", "");
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						memberNameFormat = memberNameFormatParameter.getValue();						
					} else {
						memberNameFormat = ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME;						
					}
					allMemberNames = q.findAllMemberNames(memberNameFormat);
					if(pmember!=null) {
						String pmemberName = pmember.findNameInGivenFormat(memberNameFormat);
						String[] allMemberNamesArr = allMemberNames.split(",");
						if(allMemberNames!=null && !(allMemberNamesArr[0].equals(pmemberName))) {
							StringBuffer revisedAllMemberNames = new StringBuffer();
							for(int i=1; i<allMemberNamesArr.length; i++) {
								if(i==1) {
									revisedAllMemberNames.append(allMemberNamesArr[i].substring(1, allMemberNamesArr[i].length()));
								} else {
									revisedAllMemberNames.append("," + allMemberNamesArr[i]);
								}
							}
							allMemberNames = revisedAllMemberNames.toString();
						}
					}					
				}
				List<Title> titles = Title.findAll(Title.class, "name", ApplicationConstants.ASC, locale);
				if(titles!=null && !titles.isEmpty()) {
					for(Title t: titles) {
						if(t.getName().trim().endsWith(".")) {
							allMemberNames = allMemberNames.replace(t.getName().trim()+" ", t.getName().trim());
						}
					}
				}
				deviceVO.setMemberNames(allMemberNames);
				if(q.getRevisedSubject()!=null && !q.getRevisedSubject().isEmpty()) {
					deviceVO.setSubject(FormaterUtil.formatNumbersInGivenText(q.getRevisedSubject(), locale));
				} else if(q.getSubject()!=null && !q.getSubject().isEmpty()) {
					deviceVO.setSubject(FormaterUtil.formatNumbersInGivenText(q.getSubject(), locale));
				}
				String content = q.getRevisedQuestionText();
				if(content!=null && !content.isEmpty()) {
					if(content.endsWith("<br><p></p>")) {
						content = content.substring(0, content.length()-11);						
					} else if(content.endsWith("<p></p>")) {
						content = content.substring(0, content.length()-7);					
					}
					content = FormaterUtil.formatNumbersInGivenText(content, locale);
					deviceVO.setContent(content);
				} else {
					content = q.getQuestionText();
					if(content!=null && !content.isEmpty()) {
						if(content.endsWith("<br><p></p>")) {
							content = content.substring(0, content.length()-11);							
						} else if(content.endsWith("<p></p>")) {
							content = content.substring(0, content.length()-7);					
						}
						content = FormaterUtil.formatNumbersInGivenText(content, locale);
						deviceVO.setContent(content);
					}
				}						
				String answer = q.getAnswer();
				if(answer != null) {
					if(answer.endsWith("<br><p></p>")) {
						answer = answer.substring(0, answer.length()-11);						
					} else if(answer.endsWith("<p></p>")) {
						answer = answer.substring(0, answer.length()-7);					
					}
					answer = FormaterUtil.formatNumbersInGivenText(answer, locale);
				}				
				deviceVO.setAnswer(answer);				
				Member answeringMember = MemberMinister.findMemberHavingMinistryInSession(session, q.getMinistry());
				if(answeringMember != null){
					deviceVO.setAnsweredBy(answeringMember.findFirstLastName());
				}
				deviceVO.setMinistryName(q.getSubDepartment().getName());
				try {
					MemberMinister memberMinister = Question.findMemberMinisterIfExists(q);
					if(memberMinister!=null) {
						deviceVO.setPrimaryMemberDesignation(memberMinister.getDesignation().getName());
					} else {
						deviceVO.setPrimaryMemberDesignation("");
					}
				} catch(ELSException ex) {
					deviceVO.setPrimaryMemberDesignation("");
				}
				/** referenced question details (later should come through referenced entities) **/
				String questionReferenceText = q.getQuestionreferenceText();
				if(questionReferenceText!=null) {
					questionReferenceText = FormaterUtil.formatNumbersInGivenText(questionReferenceText, locale);
					deviceVO.setQuestionReferenceText(questionReferenceText);
				} else {
					deviceVO.setQuestionReferenceText("");
				}
				deviceVOs.add(deviceVO);
			}
		} else {
			deviceVOs = null;
		}
				
		return deviceVOs;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<RoundVO> findBallotedRoundVOsForSuchi(final Session session, final DeviceType deviceType, Group group, Date answeringDate, final String locale) throws ELSException {
		//first we find balloted questions in sequence order
		List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
		
		Map<String, String[]> parametersMap = new HashMap<String, String[]>();
		parametersMap.put("locale", new String[]{locale.toString()});
		parametersMap.put("sessionId", new String[]{session.getId().toString()});
		parametersMap.put("deviceTypeId", new String[]{deviceType.getId().toString()});
		if(deviceType.getDevice().equals("Question")) {
			parametersMap.put("groupId", new String[]{group.getId().toString()});
		}		
		parametersMap.put("answeringDate", new String[]{FormaterUtil.formatDateToString(answeringDate, ApplicationConstants.DB_DATEFORMAT)});
		List ballotVOs = org.mkcl.els.domain.Query.findReport("YADI_BALLOT_VIEW", parametersMap);
		parametersMap = null;
		
		if(ballotVOs!=null && !ballotVOs.isEmpty()) {
			for(Object i: ballotVOs) {
				Object[] ballotVO = (Object[])i;
				QuestionSequenceVO questionSequenceVO = new QuestionSequenceVO();
				if(ballotVO[0]!=null && !ballotVO[0].toString().isEmpty()) {
					questionSequenceVO.setMemberId(Long.parseLong(ballotVO[0].toString()));
				}
				if(ballotVO[1]!=null && !ballotVO[1].toString().isEmpty()) {
					questionSequenceVO.setQuestionId(Long.parseLong(ballotVO[1].toString()));
				}
				if(ballotVO[2]!=null && !ballotVO[2].toString().isEmpty()) {
					questionSequenceVO.setNumber(Integer.parseInt(ballotVO[2].toString()));
				}
				if(ballotVO[3]!=null && !ballotVO[3].toString().isEmpty()) {
					questionSequenceVO.setSequenceNo(Integer.parseInt(ballotVO[3].toString()));
				}
				questionSequenceVOs.add(questionSequenceVO);
			}
			QuestionSequenceVO.sortBySequenceNumber(questionSequenceVOs);
		}
		for(QuestionSequenceVO questionSequenceVO: questionSequenceVOs) {
			System.out.println(questionSequenceVO.getMemberId()+": "+questionSequenceVO.getQuestionId()
					+": "+questionSequenceVO.getNumber()+": "+questionSequenceVO.getSequenceNo());
		}
		//now we arrange them in roundwise order.
		List<RoundVO> roundVOs = new ArrayList<RoundVO>();		
		String numberOfRoundsStr="0";
		if(session.findHouseType().equals(ApplicationConstants.LOWER_HOUSE)) {
			numberOfRoundsStr = session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_BALLOT_LH);				
		} else if(session.findHouseType().equals(ApplicationConstants.UPPER_HOUSE)) {
			numberOfRoundsStr = session.getParameter(ApplicationConstants.QUESTION_STARRED_NO_OF_ROUNDS_BALLOT_UH);				
		}		
		//crucial time.. find number of questions in each round
		int numberOfRounds=Integer.parseInt(numberOfRoundsStr);
		int memberIndex=0;			
		List<Integer> questionsInRounds = new ArrayList<Integer>();
		for(int i=1; i<numberOfRounds;i++) {				
			while(questionsInRounds.size()<i) {
				Long memberId = questionSequenceVOs.get(memberIndex).getMemberId();
				boolean toNextMember = true;
				int currentIndex=0;
				for(QuestionSequenceVO qs: questionSequenceVOs) {					
					if(currentIndex<=memberIndex) {
						currentIndex++;
						continue;
					}
					else if(qs.getMemberId().equals(memberId)) {
						int questionsExcludingLastRound = 0;
						for(int k : questionsInRounds) {				
							questionsExcludingLastRound += k;
						}
						questionsInRounds.add(currentIndex - questionsExcludingLastRound);
						//questionsInRounds.add(currentIndex-memberIndex);
						toNextMember = false;
						break;
					}
					currentIndex++;
				}
				if(toNextMember) {					
					memberIndex++;
					if(memberIndex<questionSequenceVOs.size()) {
						currentIndex=0;
					} else {
						int questionsExcludingLastRound = 0;
						for(int l : questionsInRounds) {				
							questionsExcludingLastRound += l;
						}
						questionsInRounds.add(questionSequenceVOs.size() - questionsExcludingLastRound);
					}					
				}
			}
			memberIndex = 0;
			for(int j=0; j<i; j++) {
				memberIndex += questionsInRounds.get(j);
			}					
		}			
		int questionsExcludingLastRound = 0;
		for(int i : questionsInRounds) {				
			questionsExcludingLastRound += i;
		}
		questionsInRounds.add(questionSequenceVOs.size() - questionsExcludingLastRound);		
		//now gather all details of each round		
		int questionsTillGivenRound = 0;
		int count=0;
		for(int i : questionsInRounds) {
			if(i>0) {
				String formattedNumberOfQuestionsInGivenRound = FormaterUtil.formatNumberNoGrouping(i, locale);
				String firstElementInGivenRound = FormaterUtil.formatNumberNoGrouping(questionsTillGivenRound+1, locale);
				String lastElementInGivenRound = FormaterUtil.formatNumberNoGrouping(questionsTillGivenRound+i, locale);
				RoundVO roundVO = new RoundVO();
				roundVO.setNumberOfQuestionsInGivenRound(i);
				roundVO.setFormattedNumberOfQuestionsInGivenRound(formattedNumberOfQuestionsInGivenRound);
				roundVO.setFirstElementInGivenRound(firstElementInGivenRound);
				roundVO.setFirstElementInGivenRoundInt(questionsTillGivenRound+1);				
				roundVO.setLastElementInGivenRound(lastElementInGivenRound);
				roundVO.setLastElementInGivenRoundInt(questionsTillGivenRound+i);
				List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
				for(int j=questionsTillGivenRound; j<(questionsTillGivenRound + i); j++) {
					DeviceVO deviceVO = new DeviceVO();
					count++;
					deviceVO.setSerialNumber(FormaterUtil.formatNumberNoGrouping(count, locale));
					deviceVO.setId(questionSequenceVOs.get(j).getQuestionId());
					deviceVO.setNumber(questionSequenceVOs.get(j).getNumber());
					deviceVO.setFormattedNumber(FormaterUtil.formatNumberNoGrouping(questionSequenceVOs.get(j).getNumber(), locale));
					Question q = Question.findById(Question.class, questionSequenceVOs.get(j).getQuestionId());
//					String memberNames="";
//					Member member=q.getPrimaryMember();
//					if(member!=null){
//						memberNames+=member.findFirstLastName();
//					}
//					List<SupportingMember> selectedSupportingMembers=q.getSupportingMembers();					
//					if(selectedSupportingMembers!=null){
//						if(!selectedSupportingMembers.isEmpty()){
//							StringBuffer bufferFirstNamesFirst=new StringBuffer();
//							for(SupportingMember k:selectedSupportingMembers){
//								Member m=k.getMember();
//								bufferFirstNamesFirst.append(m.findFirstLastName()+",");								
//							}
//							bufferFirstNamesFirst.deleteCharAt(bufferFirstNamesFirst.length()-1);
//							memberNames+=","+bufferFirstNamesFirst.toString();
//						}
//					}
					String houseType = q.getHouseType().getType();
					String allMemberNames = "";
					CustomParameter memberNameFormatParameter = null;
					if(houseType.equals(ApplicationConstants.LOWER_HOUSE)) {
						memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_SUCHI_MEMBERNAMEFORMAT_LOWERHOUSE", "");
					} else if(houseType.equals(ApplicationConstants.UPPER_HOUSE)) {
						memberNameFormatParameter = CustomParameter.findByName(CustomParameter.class, "QIS_SUCHI_MEMBERNAMEFORMAT_UPPERHOUSE", "");
					}
					if(memberNameFormatParameter!=null && memberNameFormatParameter.getValue()!=null && !memberNameFormatParameter.getValue().isEmpty()) {
						allMemberNames = q.findAllMemberNames(memberNameFormatParameter.getValue());
					} else {
						allMemberNames = q.findAllMemberNames(ApplicationConstants.FORMAT_MEMBERNAME_FIRSTNAMELASTNAME);
					}
					List<Title> titles = Title.findAll(Title.class, "name", ApplicationConstants.ASC, locale);
					if(titles!=null && !titles.isEmpty()) {
						for(Title t: titles) {
							if(t.getName().trim().endsWith(".")) {
								allMemberNames = allMemberNames.replace(t.getName().trim()+" ", t.getName().trim());
							}
						}
					}
					deviceVO.setMemberNames(allMemberNames);	
					if(q.getRevisedSubject()!=null && !q.getRevisedSubject().isEmpty()) {
						deviceVO.setSubject(FormaterUtil.formatNumbersInGivenText(q.getRevisedSubject(), locale));
					} else if(q.getSubject()!=null && !q.getSubject().isEmpty()) {
						deviceVO.setSubject(FormaterUtil.formatNumbersInGivenText(q.getSubject(), locale));
					}
					String content = q.getRevisedQuestionText();
					if(content!=null && !content.isEmpty()) {
						if(content.endsWith("<br><p></p>")) {
							content = content.substring(0, content.length()-11);							
						} else if(content.endsWith("<p></p>")) {
							content = content.substring(0, content.length()-7);							
						}
						content = FormaterUtil.formatNumbersInGivenText(content, locale);
						deviceVO.setContent(content);
					} else {
						content = q.getQuestionText();
						if(content!=null && !content.isEmpty()) {
							if(content.endsWith("<br><p></p>")) {
								content = content.substring(0, content.length()-11);								
							} else if(content.endsWith("<p></p>")) {
								content = content.substring(0, content.length()-7);							
							}
							content = FormaterUtil.formatNumbersInGivenText(content, locale);
							deviceVO.setContent(content);
						}
					}							
					String answer = q.getAnswer();
					if(answer != null) {
						if(answer.endsWith("<br><p></p>")) {
							answer = answer.substring(0, answer.length()-11);							
						} else if(answer.endsWith("<p></p>")) {
							answer = answer.substring(0, answer.length()-7);							
						}
						answer = FormaterUtil.formatNumbersInGivenText(answer, locale);
					}				
					deviceVO.setAnswer(answer);				
					Member answeringMember = MemberMinister.findMemberHavingMinistryInSession(session, q.getMinistry());
					List<MemberRole> memberRoles = HouseMemberRoleAssociation.findAllActiveRolesOfMemberInSession(answeringMember, session, locale);
					for(MemberRole l : memberRoles) {
						if(l.getType().equals(ApplicationConstants.CHIEF_MINISTER) || l.getType().equals(ApplicationConstants.DEPUTY_CHIEF_MINISTER)) {
							deviceVO.setMinistryName(q.getMinistry().getName());
							break;
						}
					}
					if(deviceVO.getMinistryName()==null) {
						Role ministerRole = Role.findByFieldName(Role.class, "type", ApplicationConstants.MINISTER, locale);
						String localizedMinisterRoleName = ministerRole.getLocalizedName();				
						deviceVO.setMinistryName(q.getSubDepartment().getName() + " " + localizedMinisterRoleName);
					}				
					if(answeringMember != null){
						deviceVO.setAnsweredBy(answeringMember.findFirstLastName());
					}
					/** referenced question details (later should come through referenced entities) **/
					if(q.getQuestionreferenceText()!=null) {
						deviceVO.setQuestionReferenceText(q.getQuestionreferenceText());
					} else {
						deviceVO.setQuestionReferenceText("");
					}
					deviceVOs.add(deviceVO);
				}
				roundVO.setDeviceVOs(deviceVOs);
				roundVOs.add(roundVO);
				questionsTillGivenRound += i;
			}			
		}
		return roundVOs;
	}
	//=============== GETTERS/SETTERS ===============
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(Date answeringDate) {
		this.answeringDate = answeringDate;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<BallotEntry> getBallotEntries() {
		return ballotEntries;
	}

	public void setBallotEntries(List<BallotEntry> ballotEntries) {
		this.ballotEntries = ballotEntries;
	}

	public Date getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(Date ballotDate) {
		this.ballotDate = ballotDate;
	}

	/**** Ballot Create And View 
	 * @throws ELSException ****/
	public static String createBallot(final Session session,final DeviceType deviceType,
			final Boolean attendance,final String locale) throws ELSException {
		return getRepository().createBallot(session,deviceType,
				attendance,locale);
	}

	public static List<Reference> viewBallot(final Session session,
			final DeviceType deviceType,final Boolean attendance,final String locale) {
		return getRepository().viewBallot(session,
				deviceType,attendance,locale);
	}
	
	public static void updateBallotQuestions(final Ballot ballot, final Status ballotStatus) throws ELSException{
		getRepository().updateBallotQuestions(ballot, ballotStatus);
	}

	public static List<Member> findMembersOfBallotBySessionAndDeviceType(
			final Session session, final DeviceType deviceType, final String locale) {
		
		return getRepository().findMembersOfBallotBySessionAndDeviceType(session, deviceType, locale);
	}
}
