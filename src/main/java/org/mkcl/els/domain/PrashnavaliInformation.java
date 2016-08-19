package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="prashnavali_informations")
public class PrashnavaliInformation extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -5990941940288588067L;
	
	@Column(length=3000)
	private String question;
	
	@Column(length=3000)
	private String answer;
	
	public PrashnavaliInformation() {
		super();
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
