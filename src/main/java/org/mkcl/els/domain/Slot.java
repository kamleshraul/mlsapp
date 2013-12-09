package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "slots")
@JsonIgnoreProperties({"roster"})
public class Slot extends BaseDomain implements Serializable{

	/*********Fields **************/

	private static final long serialVersionUID = 1L;
	
	
	private String name;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	private Reporter reporter;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	@Column(length=30000)
	private String remarks;
	
	private Boolean turnedoff;
	
	@ManyToOne
	private Roster roster;
	
	@Autowired
	private transient SlotRepository slotRepository;

	
	/*********** Constructors ****************/	
	public Slot() {
		super();
	}
	/********** Domain Methods ****************/
	public static SlotRepository getSlotRepository(){
		SlotRepository slotRepository=new Slot().slotRepository;
		if (slotRepository == null) {
			throw new IllegalStateException(
			"SlotRepository has not been injected in Slot Domain");
		}
		return slotRepository;
	}
	
	public static Slot lastGeneratedSlot(final Roster roster){
		return getSlotRepository().lastGeneratedSlot(roster);
	}
	
	public HouseType findHouseType() {
		return getSlotRepository().getHouseType(this);
	}
	
	public Language findLanguage() {
		return getSlotRepository().getLanguage(this);
	}
	
	public User findUser() {
		return getSlotRepository().getUser(this);
	}	
	
	public static Slot findByEndTime(final Roster roster,final Date endTime) {
		return getSlotRepository().findByEndTime(roster,endTime);
	}	
	
	public static Slot findByStartTime(final Roster roster,final Date startTime) {
		return getSlotRepository().findByStartTime(roster,startTime);
	}	

	public static List<Slot> findSlotsByLanguageContainingSlotTime(Language language, Slot slot) {
		return getSlotRepository().findSlotsByLanguageContainingSlotTime(language,slot);
	}
	
	public static List<User> findDifferentLanguageUsersBySlot(Slot s) {
		return getSlotRepository().findDifferentLanguageUsersBySlot(s);
	}
	
	public static List<Slot> findSlotsBySessionAndLanguage(Session session,
			Language language) {
		return getSlotRepository().findSlotsBySessionAndLanguage(session,language);
	}
	
	public static List<Slot> findSlotsByReporterAndRoster(Roster roster,
			Reporter reporter) {
		return getSlotRepository().findSlotsByReporterAndRoster(roster,reporter);
	}
	
	public static List<Slot> findSlotsByMemberAndRoster(Roster roster,
			Member member) {
		return getSlotRepository().findSlotsByMemberAndRoster(roster,member);
	}
	/*********** Setters and Getters ************/	
	
	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setRoster(Roster roster) {
		this.roster = roster;
	}

	public Roster getRoster() {
		return roster;
	}

	public void setTurnedoff(Boolean turnedoff) {
		this.turnedoff = turnedoff;
	}

	public Boolean getTurnedoff() {
		return turnedoff;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	
	
	
	
}
