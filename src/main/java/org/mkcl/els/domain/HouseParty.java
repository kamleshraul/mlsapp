package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.repository.HousePartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="house_parties")
@JsonIgnoreProperties({"parties", "remarks"})
public class HouseParty extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -4952720075279080034L;

	//=============== ATTRIBUTES ===============
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="house_id")
	private House house;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="party_type_id")
	private PartyType partyType;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="house_parties_parties",
			joinColumns={@JoinColumn(name="house_party_id", 
					referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="party_id", 
					referencedColumnName="id")})
	private List<Party> parties;
	
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	
	@Temporal(TemporalType.DATE)
	private Date toDate;
	
	@Column(length=30000)
	private String remarks;

	@Autowired
	private transient HousePartyRepository repository;
		
	//=============== CONSTRUCTORS =============
	public HouseParty() {
		super();
	}
	
	public HouseParty(final House house,
			final PartyType partyType,
			final List<Party> parties,
			final Date fromDate,
			final String locale) {
		super(locale);
		this.setHouse(house);
		this.setPartyType(partyType);
		this.setParties(parties);
		this.setFromDate(fromDate);
	}
	
	//=============== VIEW METHODS =============
	
	//=============== DOMAIN METHODS ===========
	public static HouseParty find(final House house, 
			final PartyType partyType,
			final Date fromDate, 
			final String locale) {
		return HouseParty.getRepository().find(house, 
				partyType, fromDate, locale);
	}
	
	public static HouseParty findInBetween(final House house, 
			final PartyType partyType,
			final Date date, 
			final String locale) {
		return HouseParty.getRepository().findInBetween(house, 
				partyType, date, locale);
	}
	
	//=============== INTERNAL METHODS =========
	private static HousePartyRepository getRepository() {
		HousePartyRepository repository = new HouseParty().repository;
		
		if(repository == null) {
			throw new IllegalStateException(
				"HousePartyRepository has not been injected in" +
				" HouseParty Domain");
		}
		
		return repository;
	}
	
	//=============== GETTERS/SETTERS ==========
	public House getHouse() {
		return house;
	}

	public void setHouse(final House house) {
		this.house = house;
	}

	public PartyType getPartyType() {
		return partyType;
	}

	public void setPartyType(final PartyType partyType) {
		this.partyType = partyType;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(final List<Party> parties) {
		this.parties = parties;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

}
