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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberRole.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="member_roles")
//@JsonIgnoreProperties({"member","assembly","role"})
public class MemberRole implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The member. */
	@ManyToOne(fetch=FetchType.EAGER)
	private MemberDetails member;
	
	/** The assembly. */
	@ManyToOne(fetch=FetchType.EAGER)
	private Assembly assembly;
	
	/** The role. */
	@ManyToOne(fetch=FetchType.EAGER)
	private AssemblyRole role;
	
	/** The fromdate. */
	@Column(length=20)
	@NotEmpty
	private String fromDate;
	
	/** The to date. */
	@Column(length=20)
	@NotEmpty
	private String toDate;
	
	/** The remarks. */
	@Column(length=1000)
	private String remarks;
	
	/** The status. */
	@Column(length=20)
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
			AssemblyRole role, String fromDate, String toDate, String remarks,
			String status, String locale, Long version) {
		super();
		this.member = member;
		this.assembly = assembly;
		this.role = role;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.remarks = remarks;
		this.status = status;
		this.locale = locale;
		this.version = version;
	}
	
	public static MemberRole newInstance(MemberRole memberRole){
		return new MemberRole(memberRole.getMember(),memberRole.getAssembly(),memberRole.getRole(),
				memberRole.getFromDate(),memberRole.getToDate(),memberRole.getRemarks(),
				memberRole.getStatus(),memberRole.getLocale(),memberRole.getVersion());
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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
