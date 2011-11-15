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
import javax.validation.constraints.NotNull;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
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
@JsonIgnoreProperties("assemblyStructure")
public class Assembly implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The assembly structure. */
	@ManyToOne(fetch=FetchType.LAZY)
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



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public AssemblyStructure getAssemblyStructure() {
		return assemblyStructure;
	}



	public void setAssemblyStructure(AssemblyStructure assemblyStructure) {
		this.assemblyStructure = assemblyStructure;
	}



	public String getAssembly() {
		return assembly;
	}



	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}



	public Integer getStrength() {
		return strength;
	}



	public void setStrength(Integer strength) {
		this.strength = strength;
	}



	public String getTerm() {
		return term;
	}



	public void setTerm(String term) {
		this.term = term;
	}



	public boolean isBudgetSession() {
		return budgetSession;
	}



	public void setBudgetSession(boolean budgetSession) {
		this.budgetSession = budgetSession;
	}



	public boolean isMonsoonSession() {
		return monsoonSession;
	}



	public void setMonsoonSession(boolean monsoonSession) {
		this.monsoonSession = monsoonSession;
	}



	public boolean isWinterSession() {
		return winterSession;
	}



	public void setWinterSession(boolean winterSession) {
		this.winterSession = winterSession;
	}



	public Date getAssemblyStartDate() {
		return assemblyStartDate;
	}



	public void setAssemblyStartDate(Date assemblyStartDate) {
		this.assemblyStartDate = assemblyStartDate;
	}



	public Date getAssemblyEndDate() {
		return assemblyEndDate;
	}



	public void setAssemblyEndDate(Date assemblyEndDate) {
		this.assemblyEndDate = assemblyEndDate;
	}



	public Date getAssemblyDissolvedOn() {
		return assemblyDissolvedOn;
	}



	public void setAssemblyDissolvedOn(Date assemblyDissolvedOn) {
		this.assemblyDissolvedOn = assemblyDissolvedOn;
	}



	public Long getVersion() {
		return version;
	}



	public void setVersion(Long version) {
		this.version = version;
	}



	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}	
	
}
