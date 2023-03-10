package org.mkcl.els.common.vo;

import java.util.List;

import org.mkcl.els.domain.associations.HouseMemberRoleAssociation;
import org.mkcl.els.domain.associations.MemberPartyAssociation;


/**
 * The Class MemberMobileVO.
 *
 * @author shubhama @author sagars
 * @since v1.0.0
 */


public class MemberMobileVO {
	
	//Personal Details
	
		/** The photo. */
		private Long id;
	
		/** The photo. */
		private String photo;
		
		/** The party. */
		private String party;
		
		/** The constituency. */
		private String constituency;
		
		/** The title. */
		private String title;

		/** The first name. */
		private String firstName;

		/** The middle name. */
		private String middleName;

		/** The last name. */
		private String lastName;

		/** The alias. */
		private String alias;

		/** The birth date. */
		private String birthDate;
		
		/** The gender. */
		private String gender;
		
		/** The caste. */
		private String caste;
		
		/** The pa contact no. */
		private String paContactNo;
		
		/** The mobile1. */
	    private String mobile1;
	    
	    /** The email1. */
	    private String email1;
	    
	    
	    public MemberMobileVO() {
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * Gets the Member Id.
		 *
		 * @return the Member Id
		 */

		public Long getId() {
			return id;
		}
		
		/**
		 * Sets the Member Id.
		 *
		 * @param specimenSignature the new specimen signature
		 */
		public void setId(Long id) {
			this.id = id;
		}

		/**
		 * Gets the Member Photo.
		 *
		 * @return the Member Photo
		 */
		public String getPhoto() {
			return photo;
		}
		
		/**
		 * Sets the Member Photo.
		 *
		 * @param Member Photo. the new Member Photo.
		 */
		
		public void setPhoto(String photo) {
			this.photo = photo;
		}
		
		
		/**
		 * Gets the Member Party.
		 *
		 * @return the Member Party
		 */
		public String getParty() {
			return party;
		}
		
		/**
		 * Sets the Member Party.
		 *
		 * @param Member Party. the new Member Party.
		 */
		public void setParty(String party) {
			this.party = party;
		}
		
		/**
		 * Gets the Member Constituency
		 *
		 * @return the Member Constituency
		 */
		public String getConstituency() {
			return constituency;
		}
		
		/**
		 * Sets the Member Constituency.
		 *
		 * @param Member Constituency. the new Member Constituency.
		 */
		public void setConstituency(String constituency) {
			this.constituency = constituency;
		}
		
		/**
		 * Gets the Member Title.
		 *
		 * @return the Member Title
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * Sets the Member Title.
		 *
		 * @param Member Title. the new Member Title.
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		
		/**
		 * Gets the Member FirstName.
		 *
		 * @return the Member FirstName
		 */
		public String getFirstName() {
			return firstName;
		}
		
		/**
		 * Sets the Member FirstName.
		 *
		 * @param Member FirstName. the new Member FirstName.
		 */
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		
		/**
		 * Gets the Member MiddleName.
		 *
		 * @return the Member MiddleName
		 */
		public String getMiddleName() {
			return middleName;
		}
		
		/**
		 * Sets the Member MiddleName.
		 *
		 * @param Member MiddleName. the new Member MiddleName.
		 */
		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}
		
		/**
		 * Gets the Member LastName.
		 *
		 * @return the Member LastName
		 */
		public String getLastName() {
			return lastName;
		}
		
		/**
		 * Sets the Member LastName.
		 *
		 * @param Member LastName. the new Member LastName.
		 */
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		
		/**
		 * Gets the Member Alias.
		 *
		 * @return the Member Alias
		 */
		public String getAlias() {
			return alias;
		}
		
		/**
		 * Sets the Member Alias.
		 *
		 * @param Member Alias. the new Member Alias.
		 */
		public void setAlias(String alias) {
			this.alias = alias;
		}
		
		/**
		 * Gets the Member BirthDate
		 *
		 * @return the Member BirthDate
		 */
		public String getBirthDate() {
			return birthDate;
		}
		
		/**
		 * Sets the Member BirthDate.
		 *
		 * @param Member BirthDate. the new Member BirthDate.
		 */
		public void setBirthDate(String birthDate) {
			this.birthDate = birthDate;
		}
		
		/**
		 * Gets the Member Gender.
		 *
		 * @return the Member Gender
		 */
		public String getGender() {
			return gender;
		}
		
		/**
		 * Sets the Member Gender.
		 *
		 * @param Member Gender. the new Member Gender.
		 */
		public void setGender(String gender) {
			this.gender = gender;
		}
		
		/**
		 * Gets the Member Caste.
		 *
		 * @return the Member Caste
		 */
		public String getCaste() {
			return caste;
		}
		
		/**
		 * Sets the Member Caste.
		 *
		 * @param Member Caste. the new Member Caste.
		 */
		public void setCaste(String caste) {
			this.caste = caste;
		}
		
		/**
		 * Gets the Member ContactNo.
		 *
		 * @return the Member ContactNo
		 */
		public String getPaContactNo() {
			return paContactNo;
		}
		
		/**
		 * Sets the Member ContactNo.
		 *
		 * @param Member ContactNo. the new Member ContactNo.
		 */
		public void setPaContactNo(String paContactNo) {
			this.paContactNo = paContactNo;
		}
		
		/**
		 * Gets the Member Mobile.
		 *
		 * @return the Member Mobile
		 */
		public String getMobile1() {
			return mobile1;
		}
		
		/**
		 * Sets the Member Mobile.
		 *
		 * @param Member Mobile. the new Member Mobile.
		 */
		public void setMobile1(String mobile1) {
			this.mobile1 = mobile1;
		}
		
		/**
		 * Gets the Member Email.
		 *
		 * @return the Member Email
		 */
		public String getEmail1() {
			return email1;
		}
		
		/**
		 * Sets the Member Email.
		 *
		 * @param Member Email. the new Member Email.
		 */
		public void setEmail1(String email1) {
			this.email1 = email1;
		}
	    
	    
	    
}