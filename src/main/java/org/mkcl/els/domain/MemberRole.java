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
	
	private Date unassignedOn;
	
	private String unassignedBy;
	
	private Date assignedOn;
	
	private String assignedBy;
	
	private String status;
	
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

	public MemberRole(MemberDetails member, Assembly assembly,
			AssemblyRole role, Date fromDate, Date toDate, String remarks,
			Date unassignedOn, String unassignedBy, Date assignedOn,
			String assignedBy, String status, String locale, Long version) {
		super();
		this.member = member;
		this.assembly = assembly;
		this.role = role;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.remarks = remarks;
		this.unassignedOn = unassignedOn;
		this.unassignedBy = unassignedBy;
		this.assignedOn = assignedOn;
		this.assignedBy = assignedBy;
		this.status = status;
		this.locale = locale;
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MemberDetails getMember() {
		return member;
	}

	public void setMember(MemberDetails member) {
		this.member = member;
	}

	public Assembly getAssembly() {
		return assembly;
	}

	public void setAssembly(Assembly assembly) {
		this.assembly = assembly;
	}

	public AssemblyRole getRole() {
		return role;
	}

	public void setRole(AssemblyRole role) {
		this.role = role;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getUnassignedOn() {
		return unassignedOn;
	}

	public void setUnassignedOn(Date unassignedOn) {
		this.unassignedOn = unassignedOn;
	}

	public String getUnassignedBy() {
		return unassignedBy;
	}

	public void setUnassignedBy(String unassignedBy) {
		this.unassignedBy = unassignedBy;
	}

	public Date getAssignedOn() {
		return assignedOn;
	}

	public void setAssignedOn(Date assignedOn) {
		this.assignedOn = assignedOn;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
