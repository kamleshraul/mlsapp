/*
******************************************************************
File: org.mkcl.els.domain.Assembly.java
Copyright (c) 2011, sandeeps, MKCL
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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


// TODO: Auto-generated Javadoc
/**
 * The Class Assembly.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="assemblies")
public class Assembly {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The assembly structure. */
	@ManyToOne
	private AssemblyStructure assemblyStructure;
	
	/** The assembly. */
	@NotEmpty
	private String assembly;
		
	/** The strength. */
    @NotNull
	private Integer strength;

	/** The term. */
	@Column(length=20)
	@NotEmpty
	private String term;
	
	/** The budget session. */
	private boolean budgetSession=false;
	
	/** The monsoonsession. */
	private boolean monsoonSession=false;
	
	/** The winter session. */
	private boolean winterSession=false;
	
	/** The assembly start date. */
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date assemblyStartDate;
	
	/** The assembly end date. */
	@Temporal(TemporalType.DATE)
	private Date assemblyEndDate;
	
	/** The assembly dissolved on. */
	@Temporal(TemporalType.DATE)
	private Date assemblyDissolvedOn;
	
	/** The version. */
	@Version
	private Long version;

	/** The locale. */
	@Column(length=50)
	@NotEmpty
	private String locale;

	/**
	 * Instantiates a new assembly.
	 */
	public Assembly() {
		super();
	}

	/**
	 * Instantiates a new assembly.
	 *
	 * @param assemblyStructure the assembly structure
	 * @param assembly the assembly
	 * @param strength the strength
	 * @param term the term
	 * @param budgetSession the budget session
	 * @param monsoonSession the monsoon session
	 * @param winterSession the winter session
	 * @param assemblyStartDate the assembly start date
	 * @param assemblyEndDate the assembly end date
	 * @param assemblyDissolvedOn the assembly dissolved on
	 * @param version the version
	 * @param locale the locale
	 */
	public Assembly(AssemblyStructure assemblyStructure, String assembly,
			Integer strength, String term, boolean budgetSession,
			boolean monsoonSession, boolean winterSession,
			Date assemblyStartDate, Date assemblyEndDate,
			Date assemblyDissolvedOn, Long version, String locale) {
		super();
		this.assemblyStructure = assemblyStructure;
		this.assembly = assembly;
		this.strength = strength;
		this.term = term;
		this.budgetSession = budgetSession;
		this.monsoonSession = monsoonSession;
		this.winterSession = winterSession;
		this.assemblyStartDate = assemblyStartDate;
		this.assemblyEndDate = assemblyEndDate;
		this.assemblyDissolvedOn = assemblyDissolvedOn;
		this.version = version;
		this.locale = locale;
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
	 * Gets the assembly structure.
	 *
	 * @return the assembly structure
	 */
	public AssemblyStructure getAssemblyStructure() {
		return assemblyStructure;
	}

	/**
	 * Sets the assembly structure.
	 *
	 * @param assemblyStructure the new assembly structure
	 */
	public void setAssemblyStructure(AssemblyStructure assemblyStructure) {
		this.assemblyStructure = assemblyStructure;
	}

	/**
	 * Gets the assembly.
	 *
	 * @return the assembly
	 */
	public String getAssembly() {
		return assembly;
	}

	/**
	 * Sets the assembly.
	 *
	 * @param assembly the new assembly
	 */
	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}

	/**
	 * Gets the strength.
	 *
	 * @return the strength
	 */
	public Integer getStrength() {
		return strength;
	}

	/**
	 * Sets the strength.
	 *
	 * @param strength the new strength
	 */
	public void setStrength(Integer strength) {
		this.strength = strength;
	}

	/**
	 * Gets the term.
	 *
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * Sets the term.
	 *
	 * @param term the new term
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Checks if is budget session.
	 *
	 * @return true, if is budget session
	 */
	public boolean isBudgetSession() {
		return budgetSession;
	}

	/**
	 * Sets the budget session.
	 *
	 * @param budgetSession the new budget session
	 */
	public void setBudgetSession(boolean budgetSession) {
		this.budgetSession = budgetSession;
	}

	/**
	 * Checks if is monsoon session.
	 *
	 * @return true, if is monsoon session
	 */
	public boolean isMonsoonSession() {
		return monsoonSession;
	}

	/**
	 * Sets the monsoon session.
	 *
	 * @param monsoonSession the new monsoon session
	 */
	public void setMonsoonSession(boolean monsoonSession) {
		this.monsoonSession = monsoonSession;
	}

	/**
	 * Checks if is winter session.
	 *
	 * @return true, if is winter session
	 */
	public boolean isWinterSession() {
		return winterSession;
	}

	/**
	 * Sets the winter session.
	 *
	 * @param winterSession the new winter session
	 */
	public void setWinterSession(boolean winterSession) {
		this.winterSession = winterSession;
	}

	/**
	 * Gets the assembly start date.
	 *
	 * @return the assembly start date
	 */
	public Date getAssemblyStartDate() {
		return assemblyStartDate;
	}

	/**
	 * Sets the assembly start date.
	 *
	 * @param assemblyStartDate the new assembly start date
	 */
	public void setAssemblyStartDate(Date assemblyStartDate) {
		this.assemblyStartDate = assemblyStartDate;
	}

	/**
	 * Gets the assembly end date.
	 *
	 * @return the assembly end date
	 */
	public Date getAssemblyEndDate() {
		return assemblyEndDate;
	}

	/**
	 * Sets the assembly end date.
	 *
	 * @param assemblyEndDate the new assembly end date
	 */
	public void setAssemblyEndDate(Date assemblyEndDate) {
		this.assemblyEndDate = assemblyEndDate;
	}

	/**
	 * Gets the assembly dissolved on.
	 *
	 * @return the assembly dissolved on
	 */
	public Date getAssemblyDissolvedOn() {
		return assemblyDissolvedOn;
	}

	/**
	 * Sets the assembly dissolved on.
	 *
	 * @param assemblyDissolvedOn the new assembly dissolved on
	 */
	public void setAssemblyDissolvedOn(Date assemblyDissolvedOn) {
		this.assemblyDissolvedOn = assemblyDissolvedOn;
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
	
	
}
