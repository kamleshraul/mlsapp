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


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
	
	/** The assembly number. */
    @OneToOne()
	private AssemblyNumber assemblyNumber;
	
	/** The strength. */
    @NotNull
	private Integer strength;
	
	/** The term. */
	@NotEmpty
	@Column(length=20)
	private String term;
	
	/** The budget session. */
	private boolean budgetSession=false;
	
	/** The monsoonsession. */
	private boolean monsoonSession=false;
	
	/** The winter session. */
	private boolean winterSession=false;
	
	/** The special session. */
	private boolean specialSession=false;
	
	@Version
	private Long version;

	/** The locale. */
	@Column(length=50)
	private String locale;

	/**
	 * Instantiates a new assembly.
	 */
	public Assembly() {
		super();
	}
	public Assembly(AssemblyStructure assemblyStructure,
			AssemblyNumber assemblyNumber, Integer strength, String term,
			boolean budgetSession, boolean monsoonSession,
			boolean winterSession, boolean specialSession, Long version,
			String locale) {
		super();
		this.assemblyStructure = assemblyStructure;
		this.assemblyNumber = assemblyNumber;
		this.strength = strength;
		this.term = term;
		this.budgetSession = budgetSession;
		this.monsoonSession = monsoonSession;
		this.winterSession = winterSession;
		this.specialSession = specialSession;
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
	 * Gets the assembly number.
	 *
	 * @return the assembly number
	 */
	public AssemblyNumber getAssemblyNumber() {
		return assemblyNumber;
	}

	/**
	 * Sets the assembly number.
	 *
	 * @param assemblyNumber the new assembly number
	 */
	public void setAssemblyNumber(AssemblyNumber assemblyNumber) {
		this.assemblyNumber = assemblyNumber;
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
	 * Checks if is monsoonsession.
	 *
	 * @return true, if is monsoonsession
	 */
	public boolean isMonsoonSession() {
		return monsoonSession;
	}

	/**
	 * Sets the monsoonsession.
	 *
	 * @param monsoonsession the new monsoonsession
	 */
	public void setMonsoonsession(boolean monsoonSession) {
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
	 * Checks if is special session.
	 *
	 * @return true, if is special session
	 */
	public boolean isSpecialSession() {
		return specialSession;
	}

	/**
	 * Sets the special session.
	 *
	 * @param specialSession the new special session
	 */
	public void setSpecialSession(boolean specialSession) {
		this.specialSession = specialSession;
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

	public void setMonsoonSession(boolean monsoonSession) {
		this.monsoonSession = monsoonSession;
	}		
	
}
