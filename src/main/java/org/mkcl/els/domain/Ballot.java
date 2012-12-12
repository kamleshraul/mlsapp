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
import org.mkcl.els.common.vo.BallotVO;
import org.mkcl.els.common.vo.QuestionSequenceVO;
import org.mkcl.els.repository.BallotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
	@JoinColumn(name="group_id")
	private Group group;
	
	@Temporal(TemporalType.DATE)
	private Date answeringDate;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="ballots_ballot_entries",
			joinColumns={ @JoinColumn(name="ballot_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") })
	private List<BallotEntry> ballotEntries;
	
	@Autowired
	private transient BallotRepository repository;
	
	
	//=============== CONSTRUCTORS ==================
	public Ballot() {
		super();
	}
	
	public Ballot(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		super(locale);
		this.setSession(session);
		this.setGroup(group);
		this.setAnsweringDate(answeringDate);
		this.setBallotEntries(new ArrayList<BallotEntry>());
	}
	
	
	//=============== VIEW METHODS ==================
	/**
	 * Returns null if Ballot does not exist for the specified parameters
	 * OR
	 * Returns an empty list if there are no entries for the Ballot
	 * OR
	 * Returns a list of BallotVO
	 */
	public static List<BallotVO> getBallotVOs(final Session session,
			final Group group,
			final Date answeringDate,
			final String locale) {
		List<BallotVO> ballotVOs = new ArrayList<BallotVO>();
		Ballot ballot = Ballot.find(session, group, answeringDate, locale);
		
		if(ballot != null) {
			List<BallotEntry> ballotEntries = ballot.getBallotEntries();
			for(BallotEntry be : ballotEntries) {
				Long memberId = be.getMember().getId();
				String memberName = be.getMember().getFullnameLastNameFirst();
				
				List<QuestionSequenceVO> questionSequenceVOs = 
					Ballot.getQuestionSequenceVOs(be.getQuestionSequences());
				
				BallotVO ballotVO = new BallotVO(memberId, memberName, questionSequenceVOs);
				ballotVOs.add(ballotVO);
			}
		}
		else {
			ballotVOs = null;
		}
		return ballotVOs;
	}
	
	private static List<QuestionSequenceVO> getQuestionSequenceVOs(List<QuestionSequence> sequences) {
		List<QuestionSequenceVO> questionSequenceVOs = new ArrayList<QuestionSequenceVO>();
		for(QuestionSequence qs : sequences) {
			QuestionSequenceVO seqVO = new QuestionSequenceVO(qs.getQuestion().getId(),
					qs.getQuestion().getNumber(), 
					qs.getSequenceNo());
			
			questionSequenceVOs.add(seqVO);
		}
		return questionSequenceVOs;
	}
	
	
	//=============== DOMAIN METHODS ================
	/**
	 * 3 stepped process:
	 * 1> Compute Ballot entries.
	 * 2> Randomize Ballot entries.
	 * 3> Add sequence numbers. 
	 * 
	 * Creates a new Ballot. If a ballot already exists then return the
	 * existing Ballot.
	 */
	public Ballot create() {
		Ballot ballot = Ballot.find(this.getSession(), this.getGroup(), 
				this.getAnsweringDate(), this.getLocale());
		
		if(ballot == null) {
			Integer noOfRounds = Ballot.getNoOfRounds();
			
			List<BallotEntry> computedList = Ballot.compute(this.getSession(), 
					this.getGroup(), this.getAnsweringDate(), noOfRounds, this.getLocale());
			List<BallotEntry> randomizedList = Ballot.randomize(computedList);
			List<BallotEntry> sequencedList = Ballot.addSequenceNumbers(randomizedList, noOfRounds);
			
			this.setBallotEntries(sequencedList);
			ballot = (Ballot) this.persist();
		}
		
		return ballot;
	}
	
	public Boolean isExists() {
		Ballot ballot = Ballot.find(this.getSession(), this.getGroup(), 
				this.getAnsweringDate(), this.getLocale());
		if(ballot == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns null if there is no Ballot for the specified parameters.
	 */
	public static Ballot find(final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		return Ballot.getBallotRepository().find(session, group, answeringDate, locale);
	}
	
	/**
	 * Returns the list of Questions of @param member taken in a Ballot
	 * for the particular @param answeringDate.
	 * 
	 * Returns an empty list if there are no Questions for member.
	 */
	public static List<Question> findQuestions(final Member member,
			final Session session, 
			final Group group, 
			final Date answeringDate,
			final String locale) {
		return Ballot.getBallotRepository().find(member, session, group, answeringDate, locale);
	}
	
	
	//=============== INTERNAL METHODS ==============
	private static BallotRepository getBallotRepository() {
		BallotRepository repository = new Ballot().repository;
		if(repository == null) {
			throw new IllegalStateException(
				"BallotRepository has not been injected in Ballot Domain");
		}
		return repository;
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
		List<BallotEntry> ballotEntryList = new ArrayList<BallotEntry>();
		
		List<Date> groupAnsweringDates = group.getAnsweringDates(ApplicationConstants.ASC);
		List<Date> previousAnsweringDates = 
			Ballot.getPreviousDates(groupAnsweringDates, answeringDate);
		
		List<Member> members = Chart.findMembers(session, group, answeringDate, locale);
		for(Member m : members) {
			BallotEntry ballotEntry = Ballot.compute(m, session, group, 
					answeringDate, previousAnsweringDates, noOfRounds, locale);
			
			if(ballotEntry != null) {
				ballotEntryList.add(ballotEntry);
			}
		}
		
		return ballotEntryList;
	}
	
	/**
	 * Algorithm:
	 * 1> Create a list of Questions eligible for ballot for all the answeringDates
	 * (including currentAnsweringDate and previousAnsweringDates)
	 * 2> Create a list of Balloted Questions for the previousAnsweringDates.
	 * 3> The difference between Step 1 list and Step 2 list is the eligible list of
	 * Questions for the current Ballot.
	 * 4> Choose as many as @param noOfRounds Questions from Step 3 list. These are the
	 * Questions to be taken on the current ballot.
	 * 
	 * Returns null if at the end of Step 4 the @param member do not have any Questions
	 * in the list.
	 */
	// TODO: Started with a crude implementation. I will optimize it later if required.
	private static BallotEntry compute(final Member member,
			final Session session,
			final Group group,
			final Date currentAnsweringDate,
			final List<Date> previousAnsweringDates,
			final Integer noOfRounds,
			final String locale) {
		BallotEntry ballotEntry = null;
		
		List<Question> questionsQueue = Ballot.createQuestionQueue(member, session, group, 
				currentAnsweringDate, previousAnsweringDates, noOfRounds, locale);
		
		List<Question> ballotedQList = Ballot.ballotedQuestions(member, session, group, 
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
	
	private static List<Question> ballotedQuestions(final Member member,
			final Session session,
			final Group group,
			final List<Date> answeringDates,
			final String locale) {
		List<Question> ballotedQList = new ArrayList<Question>();
		for(Date d : answeringDates) {
			List<Question> qList = Ballot.findQuestions(member, session, group, d, locale);
			ballotedQList.addAll(qList);
		}
		return ballotedQList;
	}
	
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
	
	private static List<QuestionSequence> createQuestionSequences(final List<Question> questions,
			final String locale) {
		List<QuestionSequence> questionSequences = new ArrayList<QuestionSequence>();
		for(Question q : questions) {
			QuestionSequence qs = new QuestionSequence(locale);
			qs.setQuestion(q);
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
	private static List<Question> eligibleForBallot(final List<Question> questions,
			final String locale) {
		String ADMITTED = "question_workflow_approving_admission";
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
	
	
	//=============== GETTERS/SETTERS ===============
	public Session getSession() {
		return session;
	}

	public void setSession(final Session session) {
		this.session = session;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(final Group group) {
		this.group = group;
	}

	public Date getAnsweringDate() {
		return answeringDate;
	}

	public void setAnsweringDate(final Date answeringDate) {
		this.answeringDate = answeringDate;
	}

	public List<BallotEntry> getBallotEntries() {
		return ballotEntries;
	}

	public void setBallotEntries(final List<BallotEntry> ballotEntries) {
		this.ballotEntries = ballotEntries;
	}
}
