package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.soap.SOAPBinding.Use;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.repository.RosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "rosters")
@JsonIgnoreProperties({"session","language","users","slots"})
public class Roster extends BaseDomain implements Serializable{

	/*********Fields **************/

	private static final long serialVersionUID = 1L;	

	@ManyToOne
	private Session session;

	private Integer registerNo;	

	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	private Integer slotDuration;
	
	private String action;
	
	@ManyToOne
	private Language language;
	
	@ManyToMany
    @JoinTable(name = "rosters_users", joinColumns =
    @JoinColumn(name = "roster_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id",
                    referencedColumnName = "id"))
    private List<User> users = new ArrayList<User>();		 

	@Autowired
	private transient RosterRepository rosterRepository;

	/*********** Constructors ****************/	

	public Roster() {
		super();
	}

	/********** Domain Methods ****************/
	public static RosterRepository getRosterRepository() {
		RosterRepository rosterRepository = new Roster().rosterRepository;
		if (rosterRepository == null) {
			throw new IllegalStateException(
			"RosterRepository has not been injected in Roster Domain");
		}
		return rosterRepository;
	}
	
	public static Roster findLastCreated(final Session session,final String locale) throws ELSException {
		return getRosterRepository().findLastCreated(session,locale);
	}

	/*********** Setters and Getters ************/
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Integer getRegisterNo() {
		return registerNo;
	}

	public void setRegisterNo(Integer registerNo) {
		this.registerNo = registerNo;
	}	

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
 
	public void setSlotDuration(Integer slotDuration) {
		this.slotDuration = slotDuration;
	}

	public Integer getSlotDuration() {
		return slotDuration;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}	
}
