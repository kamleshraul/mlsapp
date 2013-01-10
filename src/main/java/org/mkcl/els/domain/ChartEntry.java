/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ChartEntry.java
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
 * The Class ChartEntry.
 *
 * @author amitd
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="chart_entries")
public class ChartEntry extends BaseDomain implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2144161593499538742L;


	//=============== ATTRIBUTES ===============
	/** The member. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;

	/** The questions. */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="chart_entries_questions",
			joinColumns={ @JoinColumn(name="chart_entry_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="question_id", referencedColumnName="id") })
	private List<Question> questions;


	//=============== CONSTRUCTORS ===============
	/**
	 * Instantiates a new chart entry.
	 */
	public ChartEntry() {
		super();
	}

	/**
	 * Instantiates a new chart entry.
	 *
	 * @param member the member
	 * @param questions the questions
	 * @param locale the locale
	 */
	public ChartEntry(final Member member,
			final List<Question> questions,
			final String locale) {
		super(locale);
		this.member = member;
		this.questions = questions;
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
	 * Gets the questions.
	 *
	 * @return the questions
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * Sets the questions.
	 *
	 * @param questions the new questions
	 */
	public void setQuestions(final List<Question> questions) {
		this.questions = questions;
	}

}
