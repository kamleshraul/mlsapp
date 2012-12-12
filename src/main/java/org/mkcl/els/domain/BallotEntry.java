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

@Configurable
@Entity
@Table(name="ballot_entries")
public class BallotEntry extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 4999119120588598051L;

	//=============== ATTRIBUTES ====================
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="ballot_entries_question_sequences",
			joinColumns={ @JoinColumn(name="ballot_entry_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="question_sequence_id", referencedColumnName="id") })
	private List<QuestionSequence> questionSequences;
	
	
	//=============== CONSTRUCTORS ===============
	public BallotEntry() {
		super();
	}


	public BallotEntry(final Member member, 
			final List<QuestionSequence> questionSequences,
			final String locale) {
		super(locale);
		this.member = member;
		this.questionSequences = questionSequences;
	}
	
	
	//=============== GETTERS/SETTERS ===============
	public Member getMember() {
		return member;
	}

	public void setMember(final Member member) {
		this.member = member;
	}

	public List<QuestionSequence> getQuestionSequences() {
		return questionSequences;
	}

	public void setQuestionSequences(final List<QuestionSequence> questionSequences) {
		this.questionSequences = questionSequences;
	}

}
