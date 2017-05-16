package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.DocumentLinkRepository;
import org.mkcl.els.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="document_links")
@JsonIgnoreProperties({"session"})
public class DocumentLink extends BaseDomain implements Serializable{

	private static final long serialVersionUID = 3538042466401998198L;
	
	/** houseType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;
	
	@Column(name = "session_date")
	private Date sessionDate;
	

	@Column(length = 30000)
	private String url;

	@Column(length = 30000)
	private String title;
	
	@Column(length = 30000)
	private String localizedTitle;


	@Autowired
	private transient DocumentLinkRepository documentLinkRepository;
	/********* Constructors ***********/
	public DocumentLink() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DocumentLink(Session session, Date sessionDate, String links, String title) {
		super();
		this.session = session;
		this.sessionDate = sessionDate;
		this.url = links;
		this.title = title;
	}
	
	public static DocumentLinkRepository getDocumentLinkRepository() {
		DocumentLinkRepository documentLinkRepository = new DocumentLink().documentLinkRepository;
        if (documentLinkRepository == null) {
            throw new IllegalStateException(
                    "DocumentLinkRepository has not been injected in DocumentLink Domain");
        }
        return documentLinkRepository;
    }
	/**** Methods 
	 * @throws ELSException *****/
	public static DocumentLink findRotationOrderLinkBySession(Session session2) throws ELSException {
		return getDocumentLinkRepository().findRotationOrderLinkBySession(session2);
	}
	
	
	/********* Getters and Setters ***********/
	
	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}


	public Date getSessionDate() {
		return sessionDate;
	}


	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getLocalizedTitle() {
		return localizedTitle;
	}


	public void setLocalizedTitle(String localizedTitle) {
		this.localizedTitle = localizedTitle;
	}


	
	
	
}
