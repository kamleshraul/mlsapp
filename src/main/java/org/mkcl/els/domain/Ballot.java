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
import java.util.List;
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

import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.vo.BallotMemberVO;
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.QuestionSequenceVO;
import org.mkcl.els.common.vo.StarredBallotVO;
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
	 *
	 */
	public static List<StarredBallotVO> findStarredBallotVOs(final Session session,
			final Date answeringDate,
			final String locale) {
		List<StarredBallotVO> ballotVOs = new ArrayList<StarredBallotVO>();
		
		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				Long memberId = be.getMember().getId();
				String memberName = be.getMember().getFullnameLastNameFirst();
				List<QuestionSequenceVO> questionSequenceVOs =
					Ballot.getQuestionSequenceVOs(be.getQuestionSequences());

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

	private static List<QuestionSequenceVO> getQuestionSequenceVOs(
			final List<QuestionSequence> sequences) {
		List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
		for(QuestionSequence qs : sequences) {
			QuestionSequenceVO seqVO = new QuestionSequenceVO(qs.getQuestion().getId(),
					qs.getQuestion().getNumber(),
					qs.getSequenceNo());

			questionSequenceVOs.add(seqVO);
		}
		return questionSequenceVOs;
	}

	public static List<BallotVO> findPreBallotVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> preBallotVOs = new ArrayList<BallotVO>();
		
		if(deviceType.getType().equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			List<Question> questions = Ballot.computeQuestions(session, answeringDate, locale);
			for(Question q : questions) {
				BallotVO preBallotVO = new BallotVO();
				preBallotVO.setMemberName(q.getPrimaryMember().getFullname());
				preBallotVO.setQuestionNumber(q.getNumber());
				if(q.getRevisedSubject() != null) {
					preBallotVO.setQuestionSubject(q.getRevisedSubject());
				}
				else {
					preBallotVO.setQuestionSubject(q.getSubject());
				}
				preBallotVOs.add(preBallotVO);
			}
		}
		
		return preBallotVOs;
	}
	
	public static List<BallotMemberVO> findPreBallotMemberVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotMemberVO> preBallotMemberVOs = new ArrayList<BallotMemberVO>();
		
		if(deviceType.getType().equals(
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
			List<Member> members = Ballot.computeMembers(session, answeringDate, locale);
			for(Member m: members) {
				BallotMemberVO preBallotMemberVO = new BallotMemberVO();
				preBallotMemberVO.setMemberName(m.getFullname());
				
				preBallotMemberVOs.add(preBallotMemberVO);
			}
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
	 * Returns a list of HalfHourBallotVO.
	 *
	 */
	public static List<BallotVO> findBallotedVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotedVOs = new ArrayList<BallotVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				BallotVO ballotedVO = new BallotVO();
				ballotedVO.setMemberName(entry.getMember().getFullname());
				
				// Hard coding for now. In case of notice ballot, a member 
				// could appear twice.
				QuestionSequence qs = entry.getQuestionSequences().get(0);
				Question q = qs.getQuestion();
				ballotedVO.setQuestionNumber(q.getNumber());
				
				if(q.getRevisedSubject() != null) {
					ballotedVO.setQuestionSubject(q.getRevisedSubject());
				}
				else {
					ballotedVO.setQuestionSubject(q.getSubject());
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
	 *
	 */
	public static List<BallotMemberVO> findBallotedMemberVO(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		List<BallotMemberVO> ballotedMemberVOs = new ArrayList<BallotMemberVO>();
		
		Ballot ballot = Ballot.find(session, deviceType, answeringDate, locale);
		if(ballot != null) {
			List<BallotEntry> entries = ballot.getBallotEntries();
			for(BallotEntry entry : entries) {
				BallotMemberVO ballotedmemberVO = new BallotMemberVO();
				ballotedmemberVO.setMemberName(entry.getMember().getFullname());
				
				ballotedMemberVOs.add(ballotedmemberVO);
			}
		}
		else {
			ballotedMemberVOs = null;
		}
		
		return ballotedMemberVOs;
	}
	
	
	//=============== DOMAIN METHODS ================
	/**
	 * A router that routes the ballot creation process to 
	 * the appropriate handler. 
	 */
	public Ballot create() {
		Ballot ballot = null;
		
		HouseType houseType = this.getSession().getHouse().getType();
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE)) {
			if(this.getDeviceType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				ballot = this.createStarredAssemblyBallot();
			}
			else if(this.getDeviceType().getType().
						equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				ballot = this.createHalfHourAssemblyBallot();
			}
		}
		else if(houseType.getType().equals(ApplicationConstants.UPPER_HOUSE)) {
			if(this.getDeviceType().getType().equals(ApplicationConstants.STARRED_QUESTION)) {
				ballot = this.createStarredCouncilBallot();
			}
			else if(this.getDeviceType().getType().
						equals(ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
				ballot = this.createHalfHourCouncilBallot();
			}
		}
		
		return ballot;
	}
	
	// TODO: [IMP] The "if" condition given below tightly couples a device with
	// a ballot type (Member, Device or Subject). Read the condition from
	// configuration (inputs will be session & deviceType).
	public String update(final Member member,
			final Question question) throws IllegalAccessException {
		HouseType houseType = this.getSession().getHouse().getType();
		if(houseType.getType().equals(ApplicationConstants.LOWER_HOUSE) && 
				this.getDeviceType().getType().equals(
						ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION)) {
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
	 */
	public static Ballot find(final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
		return Ballot.getRepository().find(session, deviceType, answeringDate, locale);
	}
	
	/**
	 * Returns the list of Questions of @param member taken in a Ballot
	 * for the particular @param answeringDate.
	 *
	 * Returns an empty list if there are no Questions for member.
	 */
	public static List<Question> findBallotedQuestions(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Date answeringDate,
			final String locale) {
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
	 */
	private Ballot createStarredAssemblyBallot() {
		Group group = Group.find(this.getSession(), this.getAnsweringDate(), this.getLocale());
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			Integer noOfRounds = Ballot.getNoOfRounds();
			
			List<BallotEntry> computedList = Ballot.compute(this.getSession(),
					group, this.getAnsweringDate(), noOfRounds, this.getLocale());
			List<BallotEntry> randomizedList = Ballot.randomize(computedList);
			List<BallotEntry> sequencedList = Ballot.addSequenceNumbers(randomizedList, noOfRounds);

			this.setBallotEntries(sequencedList);
			ballot = (Ballot) this.persist();
		}
		
		return ballot;
	}
	
	private static Integer getNoOfRounds() {
		CustomParameter parameter = CustomParameter.findByName(CustomParameter.class,
				"QUESTION_BALLOT_NO_OF_ROUNDS", "");
		return Integer.valueOf(parameter.getValue());
	}
	
	/**
	 * Only members having any Question eligible for this ballot will
	 * appear in the Ballot.
	 */
	private static List<BallotEntry> compute(final Session session,
			final Group group,
			final Date answeringDate,
			final Integer noOfRounds,
			final String locale) {
		List<BallotEntry> entries = new ArrayList<BallotEntry>();

		List<Date> groupAnsweringDates = group.getAnsweringDates(ApplicationConstants.ASC);
		List<Date> previousAnsweringDates =
			Ballot.getPreviousDates(groupAnsweringDates, answeringDate);

		DeviceType deviceType = DeviceType.findByType(ApplicationConstants.STARRED_QUESTION, locale);
		List<Member> members = Chart.findMembers(session, group, answeringDate, locale);
		for(Member m : members) {
			BallotEntry ballotEntry = Ballot.compute(m, session, deviceType, group,
					answeringDate, previousAnsweringDates, noOfRounds, locale);

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
	 * Returns null if at the end of Step 4 the @param member do not have any Questions
	 * in the list.
	 */
	private static BallotEntry compute(final Member member,
			final Session session,
			final DeviceType deviceType,
			final Group group,
			final Date currentAnsweringDate,
			final List<Date> previousAnsweringDates,
			final Integer noOfRounds,
			final String locale) {
		BallotEntry ballotEntry = null;

		List<Question> questionsQueue = Ballot.createQuestionQueue(member, session, group,
				currentAnsweringDate, previousAnsweringDates, noOfRounds, locale);

		List<Question> ballotedQList = Ballot.ballotedQuestions(member, session, deviceType,
				previousAnsweringDates, locale);

		List<Question> eligibleQList = Ballot.listDifference(questionsQueue, ballotedQList);
		if(! eligibleQList.isEmpty()) {
			List<Question> selectedQList = Ballot.selectForBallot(eligibleQList, noOfRounds);

			List<QuestionSequence> questionSequences =
				Ballot.createQuestionSequences(selectedQList, locale);

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
				List<QuestionSequence> qsList = be.getQuestionSequences();
				if(qsList.size() > i) {
					QuestionSequence qs = qsList.get(i);
					qs.setSequenceNo(++sequenceNo);
				}
			}
		}
		return newBallotEntryList;
	}
	
	/**
	 * Returns a subset of @param dates where each date in @param dates is
	 * less than @param date. Returns an empty list if no such dates
	 * could be found
	 */
	private static List<Date> getPreviousDates(final List<Date> dates, final Date date) {
		List<Date> dateList = new ArrayList<Date>();
		for(Date d : dates) {
			if(d.compareTo(date) < 0) {
				dateList.add(d);
			}
		}
		return dateList;
	}
	
	/**
	 * Creates the question queue.
	 */
	private static List<Question> createQuestionQueue(final Member member,
			final Session session,
			final Group group,
			final Date currentAnsweringDate,
			final List<Date> previousAnsweringDates,
			final Integer noOfRounds,
			final String locale) {
		List<Question> questionQueue = new ArrayList<Question>();

		List<Date> dates = new ArrayList<Date>();
		dates.addAll(previousAnsweringDates);
		dates.add(currentAnsweringDate);
		for(Date d : dates) {
			List<Question> qList = Chart.findQuestions(member, session, group, d, locale);
			List<Question> eligibleQList = Ballot.eligibleForBallot(qList, locale);
			questionQueue.addAll(eligibleQList);
		}

		return questionQueue;
	}
	
	/**
	 * Balloted questions.
	 */
	private static List<Question> ballotedQuestions(final Member member,
			final Session session,
			final DeviceType deviceType,
			final List<Date> answeringDates,
			final String locale) {
		List<Question> ballotedQList = new ArrayList<Question>();
		for(Date d : answeringDates) {
			List<Question> qList = Ballot.findBallotedQuestions(member, session, deviceType,
					d, locale);
			ballotedQList.addAll(qList);
		}
		return ballotedQList;
	}
	
	/**
	 * List difference.
	 */
	private static List<Question> listDifference(final List<Question> list1,
			final List<Question> list2) {
		List<Question> questions = new ArrayList<Question>();
		for(Question q1 : list1) {
			int list2Size = list2.size();
			int iterations = 0;
			for(Question q2 : list2) {
				if(q1.getId().equals(q2.getId())) {
					break;
				}
				++iterations;
			}
			if(iterations == list2Size) {
				questions.add(q1);
			}
		}
		return questions;
	}
	
	/**
	 * Creates the question sequences.
	 */
	private static List<QuestionSequence> createQuestionSequences(final List<Question> questions,
			final String locale) {
		List<QuestionSequence> questionSequences = new ArrayList<QuestionSequence>();
		for(Question q : questions) {
			QuestionSequence qs = new QuestionSequence(q, locale);
			questionSequences.add(qs);
		}
		return questionSequences;
	}
	
	/**
	 * A Question is eligible for ballot only if its internal status = "ADMITTED" and
	 * it has no parent Question. If a Question has a parent, then it's parent
	 * may be considered for the Ballot. The kid will never be considered for the
	 * Ballot.
	 *
	 * Returns a subset of @param questions sorted by priority. If there are no
	 * questions eligible for the ballot, returns an empty list.
	 */
	// TODO: [FATAL] internalStatus will be set till ADMITTED only. Further processing
	// if any will be stored in recommendation_status.
	// As it is the following logic will fail because further processed 
	// questions will also be seen.
	// Modify the code to consider only admitted questions which are not
	// balloted.
	private static List<Question> eligibleForBallot(final List<Question> questions,
			final String locale) {
		String ADMITTED = ApplicationConstants.QUESTION_FINAL_ADMISSION;
		List<Question> eligibleQList = new ArrayList<Question>();
		for(Question q : questions) {
			if(q.getInternalStatus().getType().equals(ADMITTED) && q.getParent() == null) {
				eligibleQList.add(q);
			}
		}
		return Question.sortByPriority(eligibleQList, ApplicationConstants.ASC);
	}

	/**
	 * A subset of eligible Questions of size @param noOfRounds are taken in Ballot.
	 */
	private static List<Question> selectForBallot(final List<Question> questions,
			final Integer noOfRounds) {
		List<Question> selectedQList = new ArrayList<Question>();
		selectedQList.addAll(questions);
		if(selectedQList.size() >= noOfRounds) {
			selectedQList = selectedQList.subList(0, noOfRounds);
		}
		return selectedQList;
	}
	
	
	//=============== COUNCIL: STARRED QUESTION BALLOT ===============
	public Ballot createStarredCouncilBallot() {
		return null;
	}
	
	
	//=============== ASSEMBLY: HALF HOUR DISCUSSION BALLOT ==========
	public Ballot createHalfHourAssemblyBallot() {
		return this.createMemberBallot();
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
	 */
	public Ballot createMemberBallot() {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Member> computedList = Ballot.computeMembers(this.getSession(),
					this.getAnsweringDate(),
					this.getLocale());
			List<Member> randomizedList = Ballot.randomizeMembers(computedList);
			// Read the constant 2 as a configurable parameter
			List<Member> selectedList = Ballot.selectMembersForBallot(randomizedList, 2);
			
			List<BallotEntry> ballotEntries = Ballot.createMemberBallotEntries(selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
	}
	
	private void updateMemberBallot(final Member member,
			final Question question) {
		BallotEntry ballotEntry = Ballot.findBallotEntry(member, this.getSession(),
				this.getDeviceType(), this.getAnsweringDate(), this.getLocale());
		List<QuestionSequence> questionSequences = 
			Ballot.createQuestionSequences(question, this.getLocale());
		ballotEntry.setQuestionSequences(questionSequences);
		ballotEntry.merge();
	}
	
	private static List<Member> computeMembers(final Session session,
			final Date answeringDate,
			final String locale) {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION, locale);
	
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
		
		List<Member> members = Question.findPrimaryMembers(session, deviceType, 
				 answeringDate, internalStatuses, false, startTime, endTime, 
				 ApplicationConstants.ASC, locale);
		
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
			final String locale) {
		return Ballot.getRepository().find(member, session, deviceType, answeringDate, locale);
	}
	
	
	//=============== COUNCIL: HALF HOUR DISCUSSION BALLOT ===========
	public Ballot createHalfHourCouncilBallot() {
		return this.createNoticeBallot();
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
	 */
	public Ballot createNoticeBallot() {
		Ballot ballot = Ballot.find(this.getSession(), this.getDeviceType(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			List<Question> computedList = Ballot.computeQuestions(this.getSession(),
					this.getAnsweringDate(),
					this.getLocale());
			List<Question> randomizedList = Ballot.randomizeQuestions(computedList);
			// Read the constant 2 as a configurable parameter
			List<Question> selectedList = Ballot.selectQuestionsForBallot(randomizedList, 2);
			
			List<BallotEntry> ballotEntries = Ballot.createNoticeBallotEntries(selectedList,
					this.getLocale());
			this.setBallotEntries(ballotEntries);
			ballot = (Ballot) this.persist();	
		}
		
		return ballot;
		
	}
	
	private static List<Question> computeQuestions(final Session session,
			final Date answeringDate,
			final String locale) {
		CustomParameter datePattern = CustomParameter.findByName(CustomParameter.class, 
				"DB_TIMESTAMP", "");
		DeviceType deviceType = DeviceType.findByType(
				ApplicationConstants.HALF_HOUR_DISCUSSION_QUESTION_FROM_QUESTION, locale);
	
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
		
		List<Question> questions = Question.find(session, deviceType, 
				answeringDate, internalStatuses, false, startTime, endTime, 
				ApplicationConstants.ASC, locale);
		
		return questions;
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
	
	private static List<BallotEntry> createNoticeBallotEntries(final List<Question> questions,
			final String locale) {
		List<BallotEntry> ballotEntries = new ArrayList<BallotEntry>();
		for(Question q : questions) {
			BallotEntry ballotEntry = new BallotEntry();
			ballotEntry.setMember(q.getPrimaryMember());
			ballotEntry.setQuestionSequences(Ballot.createQuestionSequences(q, locale));
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

	private static List<QuestionSequence> createQuestionSequences(final Question q,
			final String locale) {
		List<QuestionSequence> sequences = new ArrayList<QuestionSequence>();
		QuestionSequence sequence = new QuestionSequence(q, locale);
		sequences.add(sequence);
		return sequences;
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
}
