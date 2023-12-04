/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Ballot.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain.ballot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Device;
import org.mkcl.els.domain.DeviceType;
import org.mkcl.els.domain.Group;
import org.mkcl.els.domain.Member;
import org.mkcl.els.domain.Question;
import org.mkcl.els.domain.Resolution;
import org.mkcl.els.domain.Session;
import org.mkcl.els.domain.Status;
import org.mkcl.els.repository.BallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/*
 * TODO: [IMP] Written in context of Half Hour Discussion but is applicable for 
 * Starred Questions too.
 * 
 * Following are the Configuration parameters that needs to be captured:
 * 1. Number of Ballot Entries. (Example: 2)
 * 2. Should Questions ADMITTED for previous answering date be considered as 
 * input for this ballot. (Example: YES/NO)
 * 3. If answer to point 2 is YES, then how many dates back do you want to refer. 
 * (Example: 1/2/..../ALL)
 * 4. Should Questions BALLOTED but not DISCUSSED for previous answering date  be 
 * considered as input for this ballot. (Example: YES/NO)
 * 5. If answer to point 4 is YES, then how many dates back do you want to refer. 
 * (Example: 1/2/..../ALL)
 * 
 */
@Configurable
@Entity
@Table(name="ballots")
public class Ballot extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -1201622397347500652L;

	//===============================================
	//
	//=============== ATTRIBUTES ====================
	//
	//===============================================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="devicetype_id")
	private DeviceType deviceType;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	@Temporal(TemporalType.DATE)
	private Date displayAnsweringDate;
	
	// TODO: Remove this attribute once you are sure that the Council Starred
	// Ballot does not require group attribute. Even if it does, the dependency
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
	
	
	//===============================================
	//
	//=============== CONSTRUCTORS ==================
	//
	//===============================================
	/**
	 * Not to be used. Kept here because JPA needs an 
	 * Entity to have a default public Constructor.
	 */
	public Ballot() {
		super();
	}

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

	
	//===============================================
	//
	//=============== VIEW METHODS ==================
	//
	//===============================================
	public static Ballot findByDeviceId(Long deviceId) {
		return getRepository().findByDeviceId(deviceId);
	}
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
		return StarredQuestionBallot.findStarredBallotVOs(session, answeringDate, locale);
	}
	
	public static List<QuestionSequenceVO> findStarredQuestionSequenceVOs(final Session session,
			final Date answeringDate,
			final String locale) throws ELSException {
		return StarredQuestionBallot.findStarredQuestionSequenceVOs(session, answeringDate, locale);
	}
	
	public static List<StarredBallotVO> findStarredPreBallotVOs(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return StarredQuestionBallot.findStarredPreBallotVOs(session, deviceType, answeringDate, locale);
	}
	
	public static List<BallotVO> findPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		List<BallotVO> preBallotVOs = null;		
				
		if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			preBallotVOs = HalfHourFromQuestionBallot.findPreBallotVO(session, deviceType, answeringDate, locale);
		}
		else if(deviceType.getType().equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			preBallotVOs = HalfHourStandaloneBallot.findPreBallotVO(session, deviceType, answeringDate, locale);
		}
		else if(deviceType.getType().startsWith(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			preBallotVOs =  NonOfficialResolutionBallot.findPreBallotVO(session, deviceType, answeringDate, locale);
		} 
		else if(deviceType.getType().startsWith(ApplicationConstants.NONOFFICIAL_BILL)) {
			preBallotVOs =  NonOfficialBillBallot.findPreBallotVO(session, deviceType, answeringDate, locale);
		}
		else if(deviceType.getType().equals(ApplicationConstants.PROPRIETY_POINT)) {
			preBallotVOs = ProprietyPointBallot.findPreBallotVO(session, deviceType, answeringDate, locale);
		}
		
		return preBallotVOs;
	}
	
	public static List<BallotVO> findResolutionCouncilPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return NonOfficialResolutionBallot.findResolutionCouncilPreBallotVO(session, deviceType, answeringDate, locale);
	}
	
	public static List<BallotMemberVO> findPreBallotHDQAssembly(final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException {
		return HalfHourFromQuestionBallot.findPreBallotHDQAssembly(session, deviceType, group, answeringDate, locale);
	}
	
	public static List<BallotMemberVO> findPreBallotMemberVOResolutionNonOfficial(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return NonOfficialResolutionBallot.findPreBallotMemberVOResolutionNonOfficial(session, deviceType, answeringDate, locale);
	}
	
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
	
	public static List<ResolutionBallotVO> findResolutionMemberSubjectBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return NonOfficialResolutionBallot.findResolutionMemberSubjectBallotVO(session, deviceType, answeringDate, locale);
	}
	
	public static List<BillBallotVO> findBillMemberSubjectBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		return NonOfficialBillBallot.findBillMemberSubjectBallotVO(session, deviceType, answeringDate, locale);
	}
	
	public static List<ResolutionBallotVO> createResolutionPatrakBhagTwo(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return NonOfficialResolutionBallot.createResolutionPatrakBhagTwo(session, deviceType, answeringDate, locale);
	}
	
	public static List<DeviceBallotVO> findHDSBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return HalfHourStandaloneBallot.findHDSBallotVO(session, deviceType, answeringDate, locale);
	}
	
	public static List<BallotVO> findHDSCouncilPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		return HalfHourStandaloneBallot.findHDSCouncilPreBallotVO(session, deviceType, answeringDate, locale);
	}
	
	public static List<Reference> viewBallot(final Session session,
			final DeviceType deviceType,final Boolean attendance,final String locale) {
		return getRepository().viewBallot(session,
				deviceType,attendance,locale);
	}
	
	public static List<DeviceVO> findBallotedQuestionVOs(final Session session, final DeviceType deviceType, final Group group, final Date answeringDate,
			final String locale) throws ELSException {	
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return StarredQuestionBallot.findBallotedQuestionVOs(session, deviceType, group, answeringDate, locale);
		}
		else {
			throw new ELSException("Ballot.findBallotedRoundVOsForSuchi/5", 
					"Method invoked for unsupported deviceType.");
		}		
	}
	
	public static List<RoundVO> findBallotedRoundVOsForSuchi(final Session session, final DeviceType deviceType, final String processingMode,
			final Group group, final Date answeringDate, final String locale) throws ELSException {
		String deviceTypeType = deviceType.getType();
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return StarredQuestionBallot.findBallotedRoundVOsForSuchi(session, deviceType, processingMode, group, answeringDate, locale);
		}
		else {
			throw new ELSException("Ballot.findBallotedRoundVOsForSuchi/5", 
					"Method invoked for unsupported deviceType.");
		}
	}
	
	public static List<ResolutionBallotVO> createPatrakBhagTwo(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) throws ELSException {
		return NonOfficialResolutionBallot.createPatrakBhagTwo(session, deviceType, answeringDate, locale);
	}
	
	// TODO: Incorrect implementation. Statuses have become deviceType specific. Change this accordingly.
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
		Status RESOLUTION_UNDERCONSIDERATION = null;
		
		Status[] internalStatuses = null;
		
		if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_QUESTIONS)){
			ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_ADMISSION, locale.toString());
			REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REJECTION, locale.toString());
			REPEAT_ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REPEATADMISSION, locale.toString());
			REPEAT_REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.QUESTION_FINAL_REPEATREJECTION, locale.toString());
			internalStatuses = new Status[] {ADMITTED, REJECTED, REPEAT_ADMITTED, REPEAT_REJECTED};
		}else if(deviceType.getType().startsWith(ApplicationConstants.DEVICE_RESOLUTIONS)){
			ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_ADMISSION, locale.toString());
			REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REJECTION, locale.toString());
			REPEAT_ADMITTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REPEATADMISSION, locale.toString());
			REPEAT_REJECTED = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_FINAL_REPEATREJECTION, locale.toString());
			RESOLUTION_UNDERCONSIDERATION = Status.findByFieldName(Status.class, "type", ApplicationConstants.RESOLUTION_PROCESSED_UNDERCONSIDERATION, locale.toString());
			internalStatuses = new Status[] {ADMITTED, REJECTED, REPEAT_ADMITTED, REPEAT_REJECTED, RESOLUTION_UNDERCONSIDERATION};
		}
		 
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				BallotMemberVO ballotedmemberVO = new BallotMemberVO();
				ballotedmemberVO.setMemberName(entry.getMember().getFullname());
				ballotedmemberVO.setMemberId(entry.getMember().getId());
				
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


	//===============================================
	//
	//=============== DOMAIN METHODS ================
	//
	//===============================================
	/**
	 * A router that routes the ballot creation process to 
	 * the appropriate handler. 
	 * @throws ELSException 
	 */
	public Ballot create() throws ELSException {
		Ballot ballot = null;
		
		DeviceType deviceType = this.getDeviceType();
		String deviceTypeType = deviceType.getType();
		
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			ballot = StarredQuestionBallot.create(this);
		}
		else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				ballot = HalfHourFromQuestionBallot.create(this);
		}
		else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
				ballot = HalfHourStandaloneBallot.create(this);
		}
		else if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_RESOLUTION)) {
			ballot = NonOfficialResolutionBallot.create(this);
		}
		else if(deviceTypeType.equals(ApplicationConstants.NONOFFICIAL_BILL)) {
			ballot = NonOfficialBillBallot.create(this);
		}
		else if(deviceTypeType.equals(ApplicationConstants.PROPRIETY_POINT)) {
			ballot = ProprietyPointBallot.create(this);
		}
		
		return ballot;
	}
	
	public Ballot createMemberBallotHDQAssembly() throws ELSException {
		return HalfHourFromQuestionBallot.createMemberBallotHDQAssembly(this);
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
		return HalfHourStandaloneBallot.createMemberBallotHDS(this);
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
		return HalfHourStandaloneBallot.createMemberBallotStandAlone(this);
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
	
	public static List<Ballot> find(final Session session,
			final DeviceType deviceType,
			final String locale) throws ELSException {
		return Ballot.getRepository().find(session, deviceType, locale);
	}
	
	// TODO: [IMP] The "if" condition given below tightly couples a device with
	// a ballot type (Member, Device or Subject). Read the condition from
	// configuration (inputs will be session & deviceType).
	public String update(final Member member,
			final Question question) throws IllegalAccessException, ELSException {
		DeviceType deviceType = this.getDeviceType();
		String deviceTypeType = deviceType.getType();
		
		if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			HalfHourFromQuestionBallot.updateMemberBallot(this, member, question);
		}
		else if(deviceTypeType.equals(ApplicationConstants.HALF_HOUR_DISCUSSION_STANDALONE)) {
			HalfHourStandaloneBallot.updateMemberBallot(this, member, question);
		}
		else {
			throw new IllegalAccessException("This method is not applicable for the device type: " + 
					this.getDeviceType().getType());
		}
		
		return null;
	}
	
	public static BallotEntry findBallotEntry(final List<BallotEntry> ballotEntries,
			final Member member) {
		for(BallotEntry be : ballotEntries) {
			Member m = be.getMember();
			if(m.getId().equals(member.getId())) {
				return be;
			}
		}
		return null;
	}
	
	public static List<DeviceSequence> createDeviceSequences(final Device d,
			final String locale) {
		List<DeviceSequence> sequences = new ArrayList<DeviceSequence>();
		DeviceSequence sequence = new DeviceSequence(d, locale);
		sequences.add(sequence);
		return sequences;
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
	
	/**
	 * Locates the @param device in particular Ballot. Removes the 
	 * device from the Ballot. 
	 * 
	 * As a result of @param device's removal, the sequencing of the 
	 * devices in the Ballot might change. @param isResequenceDevices
	 * controls whether re-sequencing should be performed.
	 * 
	 * Currently supported Devices: Starred Question (LowerHouse and UpperHouse)
	 * 
	 * Returns the updated Ballot if @param device is actually 
	 * balloted, else returns null.
	 */
	public static Ballot remove(final Device device,
			final boolean isResequenceDevices) throws ELSException {
		if(device instanceof Question) {
			Question question = (Question) device;
			
			DeviceType deviceType = question.getType();
			String deviceTypeType = deviceType.getType();
			if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
				return StarredQuestionBallot.removeStarredQuestion(question, isResequenceDevices);
			}
			else {
				throw new ELSException("Ballot.remove/2", 
					"Method invoked for unsupported deviceType.");
			}
		}
		else {
			throw new ELSException("Ballot.remove/2", 
				"Method invoked for unsupported device.");
		}
	}
	
	public static Ballot find(final Device device) throws ELSException {
		return Ballot.getRepository().find(device);
	}
	
	/**
	 * Only Starred UPPERHOUSE/LOWERHOUSE mode Ballot can be programmatically
	 * regenerated.
	 * 
	 * 1. Set the ballotStatus of all the Questions in @param ballot as null.
	 * 2. Delete @param ballot.
	 * 3. Delete corresponding preBallot.
	 * 4. Regenerate the preBallot.
	 * 5. Regenerate the ballot.
	 */
	public static Ballot regenerate(final Ballot ballot) throws ELSException {
		DeviceType deviceType = ballot.getDeviceType();
		String deviceTypeType = deviceType.getType();
		
		if(deviceTypeType.equals(ApplicationConstants.STARRED_QUESTION)) {
			return StarredQuestionBallot.regenerate(ballot);
		}
		else {
			throw new ELSException("Ballot.regenerate/1", 
    				"Illegal invocation. Method invoked for inappropriate" +
    				" deviceType and houseType.");
		}
	}
	
	public static String createBallot(final Session session,final DeviceType deviceType,
			final Boolean attendance,final String locale) throws ELSException {
		return getRepository().createBallot(session, deviceType, attendance, locale);
	}
	
	public static void updateBallotQuestions(final Ballot ballot, final Status ballotStatus) throws ELSException{
		getRepository().updateBallotQuestions(ballot, ballotStatus);
	}
	
	public static List<Member> findMembersOfBallotBySessionAndDeviceType(
			final Session session, final DeviceType deviceType, final String locale) {
		return getRepository().findMembersOfBallotBySessionAndDeviceType(session, deviceType, locale);
	}
	
	public static int updateByYaadi(final Ballot ballot, 
			final Status status, final String editedAs, 
			final String editedBy, final Date editedOn) {
		return getRepository().updateByYaadi(ballot, status, editedAs, editedBy, editedOn);
		
	}
	
	
	public static List<StarredBallotVO> getPreBallotVOs(final Session session,
			final DeviceType deviceType, 
			final Date answeringDate,
			final String locale) throws ELSException {
		return StarredQuestionBallot.
				getStarredPreBallotVOs(session, deviceType, answeringDate, locale);
	}
	
	public static List<BallotMemberVO> getPreBallotHDQAssembly(
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date answeringDate,
			final String locale) throws ELSException {
		// TODO Auto-generated method stub
		return HalfHourFromQuestionBallot.
				getPreBallotHDQAssembly(session, deviceType, group, answeringDate, locale);
	}
	
	public static List<BallotMemberVO> previewPreBallotHDQAssembly(
			Session session, DeviceType deviceType, Group group,
			Date answeringDate, String locale) throws ELSException {
		return HalfHourFromQuestionBallot.
						previewPreBallotHDQAssembly(session, deviceType, group, answeringDate, locale);
	}
	
	public static List<StarredBallotVO> previewPreBallotVOs(
			final Session session, 
			final DeviceType deviceType, 
			final Date answeringDate,
			final String locale) throws ELSException {
		return StarredQuestionBallot.
				previewPreBallotVOs(session, deviceType, answeringDate, locale);
	}
	
	public static List<BallotVO> previewPreBallotVOHDQCouncil(Session session,
			DeviceType deviceType, Date answeringDate, String locale) throws ELSException {
		return HalfHourFromQuestionBallot.
				previewPreBallotVOs(session, deviceType, answeringDate, locale);
	}
	
	public static List<BallotVO> findPreviewPreBallotVOHDSCouncil(
			Session session, DeviceType deviceType, Date answeringDate,
			String locale) throws ELSException {
		return HalfHourStandaloneBallot.
				previewPreBallotVOs(session, deviceType, answeringDate, locale);
	}
	public void removeBallotUH() throws ELSException {
		 getRepository().removeBallotUH(this);
		
	}
	

	//===============================================
	//
	//=============== INTERNAL METHODS ==============
	//
	//===============================================
	public static BallotRepository getRepository() {
		BallotRepository repository = new Ballot().repository;
		
		if(repository == null) {
			throw new IllegalStateException("BallotRepository has not been injected in Ballot Domain");
		}
		
		return repository;
	}
	
	//===============================================
	//
	//=============== GETTERS/SETTERS ===============
	//
	//===============================================
	public Session getSession() {
		return session;
	}

	public void setSession(final Session session) {
		this.session = session;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(final DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(final Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public Date getDisplayAnsweringDate() {
		return displayAnsweringDate;
	}

	public void setDisplayAnsweringDate(Date displayAnsweringDate) {
		this.displayAnsweringDate = displayAnsweringDate;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(final Group group) {
		this.group = group;
	}

	public List<BallotEntry> getBallotEntries() {
		return ballotEntries;
	}

	public void setBallotEntries(final List<BallotEntry> ballotEntries) {
		this.ballotEntries = ballotEntries;
	}

	public Date getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(final Date ballotDate) {
		this.ballotDate = ballotDate;
	}

	

}
