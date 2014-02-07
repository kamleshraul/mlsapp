package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.RosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "rosters")
@JsonIgnoreProperties({"session","language","reporters"})
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

	private Integer day;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date slotDurationChangedFrom;

	@ManyToMany()
	@JoinTable(name = "rosters_reporters", joinColumns =
		@JoinColumn(name = "roster_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "reporter_id",
				referencedColumnName = "id"))
				private List<Reporter> reporters = new ArrayList<Reporter>();		 

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

	public static Roster findLastCreated(final Session session,final String locale) {
		return getRosterRepository().findLastCreated(session,locale);
	}

	public Boolean generateSlot() {		
		return getRosterRepository().generateSlot(this);
	}
	
	public static Boolean generateSlot(final Adjournment adjournment) {		
		return getRosterRepository().generateSlot(adjournment);
	}
	
	public static Boolean toggleSlots(final Long rosterId,final Date startTime,final Date endTime,final Boolean toggle) {		
		return getRosterRepository().toggleSlots(rosterId,startTime,endTime,toggle);
	}
	
	public static Boolean generateNewSlots(final Roster roster,final Date startTime,final Date endTime,
			final String reportersToBeTakenFrom){
		return getRosterRepository().generateNewSlots(roster,startTime,endTime,
				reportersToBeTakenFrom);
	}
	
	public static Boolean deleteExistingSlots(final Long rosterId,final Date startTime,final Date endTime) {
		return getRosterRepository().deleteExistingSlots(rosterId,startTime,endTime);
	}
	
	@Override
	public boolean remove() {
		return getRosterRepository().removeRoster(this);
	}
	
	public static Reporter findFirstReporterAtPosX(final Roster domain,final int position,final String activeStatus) {
		return getRosterRepository().findFirstReporterAtPosX(domain,position,activeStatus);
	}
	
	public static Reporter findByUser(final Roster roster,final User user) {
		return getRosterRepository().findByUser(roster,user);
	}
	
	public static List<Reporter> findReportersOtherThan(final Roster domain,
			final List<Long> originalReporters) {
		return getRosterRepository().findReportersOtherThan(domain,
				originalReporters);
	}	
	public static List<Reporter> findReportersByActiveStatus(final Roster roster,final Boolean isActive) {
		return getRosterRepository().findReportersByActiveStatus(roster,isActive);
	}	
	
	public static Boolean slotsAlreadyCreated(final Roster roster){
		return getRosterRepository().slotsAlreadyCreated(roster);
	}
	
	public static List<Roster> findAllRosterBySessionAndLanguage(
			final Session session,final Language language,final String locale) {
		return getRosterRepository().findAllRosterBySessionAndLanguage(session,language,locale);
	}	
	
	public static Roster findRosterBySessionLanguageAndDay(final Session session,
			final int day,final Language language,final String locale) {
		return getRosterRepository().findRosterBySessionLanguageAndDay(session,day,language,locale);
	}
	
	public static Roster findRosterByDate(Date sDate,Language language, String locale) {
		return getRosterRepository().findRosterByDate(sDate,language,locale);
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

	public List<Reporter> getReporters() {
		return reporters;
	}

	public void setReporters(List<Reporter> reporters) {
		this.reporters = reporters;
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

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getDay() {
		return day;
	}	

	public Date getSlotDurationChangedFrom() {
		return slotDurationChangedFrom;
	}

	public void setSlotDurationChangedFrom(Date slotDurationChangedFrom) {
		this.slotDurationChangedFrom = slotDurationChangedFrom;
	}

		
}
