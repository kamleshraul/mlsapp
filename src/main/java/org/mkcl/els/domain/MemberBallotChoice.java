/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.MemberBallotChoice.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mkcl.els.repository.MemberBallotChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberBallotChoice.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name="memberballot_choice")
public class MemberBallotChoice extends BaseDomain implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The question. */
	@ManyToOne(fetch=FetchType.LAZY)
	private Question question;

	/** The new answering date. */
	@ManyToOne(fetch=FetchType.LAZY)
	private QuestionDates newAnsweringDate;

	/** The choice. */
	private Integer choice;

    /** The clubbing updated. */
    private Boolean clubbingUpdated;


    /** The member ballot choice repository. */
    @Autowired
    private transient MemberBallotChoiceRepository memberBallotChoiceRepository;

	/**
	 * Instantiates a new member ballot choice.
	 */
	public MemberBallotChoice() {
		super();
	}

	/**
	 * Gets the question.
	 *
	 * @return the question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the question.
	 *
	 * @param question the new question
	 */
	public void setQuestion(final Question question) {
		this.question = question;
	}

	/**
	 * Gets the choice.
	 *
	 * @return the choice
	 */
	public Integer getChoice() {
		return choice;
	}

	/**
	 * Sets the choice.
	 *
	 * @param choice the new choice
	 */
	public void setChoice(final Integer choice) {
		this.choice = choice;
	}


    /**
     * Gets the new answering date.
     *
     * @return the new answering date
     */
    public QuestionDates getNewAnsweringDate() {
        return newAnsweringDate;
    }


    /**
     * Sets the new answering date.
     *
     * @param newAnsweringDate the new new answering date
     */
    public void setNewAnsweringDate(final QuestionDates newAnsweringDate) {
        this.newAnsweringDate = newAnsweringDate;
    }

    /**
     * Find by member.
     *
     * @param session the session
     * @param deviceType the device type
     * @param member the member
     * @param locale the locale
     * @return the list
     */
    public static List<MemberBallotChoice> findByMember(final Session session,
            final DeviceType deviceType, final Member member, final String locale) {
        return getMemberBallotChoiceRepository().findByMember(session,deviceType,member,locale);
    }

    /**
     * Gets the member ballot choice repository.
     *
     * @return the member ballot choice repository
     */
    public static MemberBallotChoiceRepository getMemberBallotChoiceRepository() {
        MemberBallotChoiceRepository memberBallotChoiceRepository = new MemberBallotChoice().memberBallotChoiceRepository;
        if (memberBallotChoiceRepository == null) {
            throw new IllegalStateException(
                    "MemberBallotChoiceRepository has not been injected in MemberBallotChoice Domain");
        }
        return memberBallotChoiceRepository;
    }

    /**
     * Sets the clubbing updated.
     *
     * @param clubbingUpdated the new clubbing updated
     */
    public void setClubbingUpdated(final Boolean clubbingUpdated) {
        this.clubbingUpdated = clubbingUpdated;
    }

    /**
     * Gets the clubbing updated.
     *
     * @return the clubbing updated
     */
    public Boolean getClubbingUpdated() {
        return clubbingUpdated;
    }


}
