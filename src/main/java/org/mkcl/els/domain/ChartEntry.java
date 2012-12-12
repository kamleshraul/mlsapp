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
@Table(name="chart_entries")
public class ChartEntry extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 2144161593499538742L;

	
	//=============== ATTRIBUTES ===============
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="chart_entries_questions",
			joinColumns={ @JoinColumn(name="chart_entry_id", referencedColumnName="id") },
			inverseJoinColumns={ @JoinColumn(name="question_id", referencedColumnName="id") })
	private List<Question> questions;
	
	
	//=============== CONSTRUCTORS ===============
	public ChartEntry() {
		super();
	}
	
	public ChartEntry(final Member member, 
			final List<Question> questions,
			final String locale) {
		super(locale);
		this.member = member;
		this.questions = questions;
	}

	
	//=============== GETTERS/SETTERS ===============
	public Member getMember() {
		return member;
	}

	public void setMember(final Member member) {
		this.member = member;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(final List<Question> questions) {
		this.questions = questions;
	}
	
}
