package org.mkcl.els.common.vo;

import java.util.Date;
import javax.persistence.Version;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.Party;

public class MemberPersonalDetails {
	
	private Long id;
		    
	private String photo;
	
	private String title;

	private String firstName;

	private String middleName;	

	private String lastName;	

	private Constituency constituency;	

	private Party partyName;	
	
	private String fatherTitle;

	private String fatherName;
	
	private String motherTitle;

	private String motherName;	

	private Date birthDate;	
	
	private boolean maritalStatus;	

	private Date marriageDate;	

	private String spouseName;	

	private Integer noOfSons;	

	private Integer noOfDaughter;	

	private String educationalQualification;	

	private String profession;
	
	private String locale;
	
	@Version
	private String version;

	public MemberPersonalDetails() {
		super();
	}

	
	public MemberPersonalDetails(String photo, String title, String firstName,
			String middleName, String lastName, Constituency constituency,
			Party partyName, String fatherTitle, String fatherName,
			String motherTitle, String motherName, Date birthDate,
			boolean maritalStatus, Date marriageDate, String spouseName,
			Integer noOfSons, Integer noOfDaughter,
			String educationalQualification, String profession, String locale,
			String version) {
		super();
		this.photo = photo;
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.constituency = constituency;
		this.partyName = partyName;
		this.fatherTitle = fatherTitle;
		this.fatherName = fatherName;
		this.motherTitle = motherTitle;
		this.motherName = motherName;
		this.birthDate = birthDate;
		this.maritalStatus = maritalStatus;
		this.marriageDate = marriageDate;
		this.spouseName = spouseName;
		this.noOfSons = noOfSons;
		this.noOfDaughter = noOfDaughter;
		this.educationalQualification = educationalQualification;
		this.profession = profession;
		this.locale = locale;
		this.version = version;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Constituency getConstituency() {
		return constituency;
	}

	public void setConstituency(Constituency constituency) {
		this.constituency = constituency;
	}

	public Party getPartyName() {
		return partyName;
	}

	public void setPartyName(Party partyName) {
		this.partyName = partyName;
	}

	public String getFatherTitle() {
		return fatherTitle;
	}


	public void setFatherTitle(String fatherTitle) {
		this.fatherTitle = fatherTitle;
	}


	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherTitle() {
		return motherTitle;
	}


	public void setMotherTitle(String motherTitle) {
		this.motherTitle = motherTitle;
	}


	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public boolean isMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(boolean maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Date getMarriageDate() {
		return marriageDate;
	}

	public void setMarriageDate(Date marriageDate) {
		this.marriageDate = marriageDate;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public Integer getNoOfSons() {
		return noOfSons;
	}

	public void setNoOfSons(Integer noOfSons) {
		this.noOfSons = noOfSons;
	}

	public Integer getNoOfDaughter() {
		return noOfDaughter;
	}

	public void setNoOfDaughter(Integer noOfDaughter) {
		this.noOfDaughter = noOfDaughter;
	}

	public String getEducationalQualification() {
		return educationalQualification;
	}

	public void setEducationalQualification(String educationalQualification) {
		this.educationalQualification = educationalQualification;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	

}
