package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.CommitteeNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="committee_names")
@JsonIgnoreProperties({"committeeType", "rule", "durationInYears", 
	"durationInMonths","durationInDays", "noOfLowerHouseMembers", 
	"noOfUpperHouseMembers", "isExpired"})
public class CommitteeName extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1058629326391054430L;

	//=============== ATTRIBUTES ===============
	@Column(length=900)
	private String name;
	
	@Column(length=1500)
	private String displayName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committee_type_id")
	private CommitteeType committeeType;
	
	@Temporal(TemporalType.DATE)
	private Date foundationDate;
	
	@Column(length=6000)
	private String rule;

	private Integer durationInYears;
	
	private Integer durationInMonths;
	
	private Integer durationInDays;
	
	private Integer noOfLowerHouseMembers;
	
	private Integer noOfUpperHouseMembers;
	
	private Boolean isExpired;
	
	@Autowired
	private transient CommitteeNameRepository repository;

	//=============== CONSTRUCTORS =============
	public CommitteeName() {
		super();
		this.setIsExpired(false);
	}
	
	public CommitteeName(final String name,
			final String displayName,
			final CommitteeType committeeType,
			final Date foundationDate,
			final String locale) {
		super(locale);
		this.setName(name);
		this.setDisplayName(displayName);
		this.setCommitteeType(committeeType);
		this.setFoundationDate(foundationDate);
		this.setIsExpired(false);
	}
	
	//=============== VIEW METHODS =============
		
	//=============== DOMAIN METHODS ===========
	public static CommitteeName find(final String name,
			final CommitteeType committeeType,
			final String locale) {
		return CommitteeName.getRepository().find(name, committeeType, locale);
	}
	
	public static List<CommitteeName> find(final CommitteeType committeeType,
			final String locale) {
		return CommitteeName.getRepository().find(committeeType, locale);
	}
	
	public static List<CommitteeName> find(final HouseType houseType,
			final String locale) {
		return CommitteeName.getRepository().find(houseType, locale);
	}
	
	public static List<CommitteeName> findAll(final String locale) {
		return CommitteeName.getRepository().findAll(locale);
	}
		
	//=============== INTERNAL METHODS =========
	private static CommitteeNameRepository getRepository() {
		CommitteeNameRepository repository = new CommitteeName().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"CommitteeNameRepository has not been injected in" +
				" CommitteeName Domain");
		}
		
		return repository;
	}
		
	//=============== GETTERS/SETTERS ==========
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public CommitteeType getCommitteeType() {
		return committeeType;
	}

	public void setCommitteeType(final CommitteeType committeeType) {
		this.committeeType = committeeType;
	}

	public Date getFoundationDate() {
		return foundationDate;
	}

	public void setFoundationDate(final Date foundationDate) {
		this.foundationDate = foundationDate;
	}
	
	public String getRule() {
		return rule;
	}

	public void setRule(final String rule) {
		this.rule = rule;
	}

	public Integer getDurationInYears() {
		return durationInYears;
	}

	public void setDurationInYears(final Integer durationInYears) {
		this.durationInYears = durationInYears;
	}

	public Integer getDurationInMonths() {
		return durationInMonths;
	}

	public void setDurationInMonths(final Integer durationInMonths) {
		this.durationInMonths = durationInMonths;
	}

	public Integer getDurationInDays() {
		return durationInDays;
	}

	public void setDurationInDays(final Integer durationInDays) {
		this.durationInDays = durationInDays;
	}

	public Integer getNoOfLowerHouseMembers() {
		return noOfLowerHouseMembers;
	}

	public void setNoOfLowerHouseMembers(final Integer noOfLowerHouseMembers) {
		this.noOfLowerHouseMembers = noOfLowerHouseMembers;
	}

	public Integer getNoOfUpperHouseMembers() {
		return noOfUpperHouseMembers;
	}

	public void setNoOfUpperHouseMembers(final Integer noOfUpperHouseMembers) {
		this.noOfUpperHouseMembers = noOfUpperHouseMembers;
	}

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(final Boolean isExpired) {
		this.isExpired = isExpired;
	}

}
