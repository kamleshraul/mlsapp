package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mkcl.els.controller.GenericController;
import org.mkcl.els.repository.AdjournmentMotionRepository;
import org.mkcl.els.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="feedback")
public class Feedback extends BaseDomain implements Serializable {

	//Attributes 
//	private static final long serialVersionUID = 1L;
	
	private Integer ratings;
	
	private Integer totalRatings;
	
	@Column(length = 500)
	private String feedback_content;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="session_id")
	private Session session;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="credential_id")
	private Credential credential;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	 /** The adjournment motion repository. */
    @Autowired
    private transient FeedbackRepository feedbackRepository;
    

	public Feedback() {
		
	}
	
	private static FeedbackRepository getFeedbackRepository() {
		FeedbackRepository feedbackRepository = new Feedback().feedbackRepository;
        if (feedbackRepository == null) {
            throw new IllegalStateException(
            	"FeedbackRepository has not been injected in Feedback Domain");
        }
        return feedbackRepository;
	}
	
	public Feedback(final Integer ratings, final Integer totalRatings, final String feedback_content, final Session session, final Credential credential) {
		super();
		this.ratings = ratings;
		this.totalRatings = totalRatings;
		this.feedback_content = feedback_content;
		this.session = session;
		this.credential = credential;
	}
	
	public Integer getRatings() {
		return ratings;
	}

	public void setRatings(Integer ratings) {
		this.ratings = ratings;
	}

	public Integer getTotalRatings() {
		return totalRatings;
	}

	public void setTotalRatings(Integer totalRatings) {
		this.totalRatings = totalRatings;
	}

	public String getFeedback_content() {
		return feedback_content;
	}

	public void setFeedback_content(String feedback_content) {
		this.feedback_content = feedback_content;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public static Boolean findFeedbackSubmitted(final Credential credentialId, final Session sessionId) {
		return getFeedbackRepository().findFeedbackSubmitted(credentialId, sessionId);
	}
		
}
