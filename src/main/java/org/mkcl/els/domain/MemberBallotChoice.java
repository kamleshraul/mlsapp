package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="memberballot_choice")
public class MemberBallotChoice extends BaseDomain implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
	private Question question;

	@ManyToOne(fetch=FetchType.LAZY)
	private QuestionDates newAnsweringDate;

	private Integer choice;

	public MemberBallotChoice() {
		super();
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(final Question question) {
		this.question = question;
	}

	public Integer getChoice() {
		return choice;
	}

	public void setChoice(final Integer choice) {
		this.choice = choice;
	}


    public QuestionDates getNewAnsweringDate() {
        return newAnsweringDate;
    }


    public void setNewAnsweringDate(final QuestionDates newAnsweringDate) {
        this.newAnsweringDate = newAnsweringDate;
    }

}
