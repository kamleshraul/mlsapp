/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BallotEntry.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class BallotEntry.
 *
 * @author amitd
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="ballot_entries")
public class BallotEntry extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4999119120588598051L;

	//=============== ATTRIBUTES ====================
	/** The member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;

	/** The question sequences. */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="ballot_entries_question_sequences",
			joinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="question_sequence_id", referencedColumnName="id") })
	private List<QuestionSequence> questionSequences;


	//=============== CONSTRUCTORS ===============
	/**
	 * Instantiates a new ballot entry.
	 */
	public BallotEntry() {
		super();
	}


	/**
	 * Instantiates a new ballot entry.
	 *
	 * @param member the member
	 * @param questionSequences the question sequences
	 * @param locale the locale
	 */
	public BallotEntry(final Member member,
			final List<QuestionSequence> questionSequences,
			final String locale) {
		super(locale);
		this.member = member;
		this.questionSequences = questionSequences;
	}


	//=============== GETTERS/SETTERS ===============
	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * Sets the member.
	 *
	 * @param member the new member
	 */
	public void setMember(final Member member) {
		this.member = member;
	}

	/**
	 * Gets the question sequences.
	 *
	 * @return the question sequences
	 */
	public List<QuestionSequence> getQuestionSequences() {
		return questionSequences;
	}

	/**
	 * Sets the question sequences.
	 *
	 * @param questionSequences the new question sequences
	 */
	public void setQuestionSequences(final List<QuestionSequence> questionSequences) {
		this.questionSequences = questionSequences;
	}

}
