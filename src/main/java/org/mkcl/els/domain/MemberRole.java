/*
******************************************************************
File: org.mkcl.els.domain.MemberRole.java
Copyright (c) 2011, sandeeps, ${company}
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberRole.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="member_roles")
@JsonIgnoreProperties({"member","assembly","role"})
public class MemberRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The member. */
	@ManyToOne
	private MemberDetails member;
	
	/** The assembly. */
	@ManyToOne
	private Assembly assembly;
	
	/** The role. */
	@ManyToOne
	private AssemblyRole role;
	
	/** The fromdate. */
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	
	/** The to date. */
	@Temporal(TemporalType.DATE)
	private Date toDate;
	
	/** The remarks. */
	@Column(length=1000)
	private String remarks;
	
	/** The locale. */

	@Column(length=10)
	private String locale;
	
	/** The version. */
	@Version
	private Long version;

	/**
	 * Instantiates a new member role.
	 */
	public MemberRole() {
		super();
	}

	/**
	 * Instantiates a new member role.
	 *
	 * @param member the member
	 * @param assembly the assembly
	 * @param role the role
	 * @param fromdate the fromdate
	 * @param toDate the to date
	 * @param remarks the remarks
	 * @param locale the locale
	 * @param version the version
	 */
	public MemberRole(MemberDetails member, Assembly assembly,
			AssemblyRole role, Date fromDate, Date toDate, String remarks,
			String locale, Long version) {
		super();
		this.member = member;
		this.assembly = assembly;
		this.role = role;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.remarks = remarks;
		this.locale = locale;
		this.version = version;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	public MemberDetails getMember() {
		return member;
	}

	/**
	 * Sets the member.
	 *
	 * @param member the new member
	 */
	public void setMember(MemberDetails member) {
		this.member = member;
	}

	/**
	 * Gets the assembly.
	 *
	 * @return the assembly
	 */
	public Assembly getAssembly() {
		return assembly;
	}

	/**
	 * Sets the assembly.
	 *
	 * @param assembly the new assembly
	 */
	public void setAssembly(Assembly assembly) {
		this.assembly = assembly;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public AssemblyRole getRole() {
		return role;
	}

	/**
	 * Sets the role.
	 *
	 * @param role the new role
	 */
	public void setRole(AssemblyRole role) {
		this.role = role;
	}

	/**
	 * Gets the fromdate.
	 *
	 * @return the fromdate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the fromdate.
	 *
	 * @param fromdate the new fromdate
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 *
	 * @return the to date
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 *
	 * @param toDate the new to date
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 *
	 * @param remarks the new remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(Long version) {
		this.version = version;
	}	
}
