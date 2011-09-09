/*
******************************************************************
File: org.mkcl.els.domain.AssemblyNumber.java
Copyright (c) 2011, amitd, ${company}
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * The Class AssemblyNumber.
 *
 * @author amitd
 * @version v1.0.0
 */
@Entity
@Table(name="assembly_number")
public class AssemblyNumber implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//===========Attributes==========
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The assembly number. */
	@Column(length=100, nullable=false)
	@NotEmpty
	private String assemblyNo;

	/** The version. */
	@Version
	private Long version;

	/** The locale. */
	@Column(length=50)
	private String locale;

	//==========Constructors==========
	/**
	 * Instantiates a new assembly number.
	 */
	public AssemblyNumber() {
		super();
	}

	/**
	 * Instantiates a new assembly number.
	 *
	 * @param assemblyNo the assembly no
	 * @param locale the locale
	 */
	public AssemblyNumber(String assemblyNo, String locale) {
		super();
		this.assemblyNo = assemblyNo;
		this.locale = locale;
	}

	//==========Getters & Setters==========
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
	 * Gets the assembly no.
	 *
	 * @return the assembly no
	 */
	public String getAssemblyNo() {
		return assemblyNo;
	}

	/**
	 * Sets the assembly no.
	 *
	 * @param assemblyNo the new assembly no
	 */
	public void setAssemblyNo(String assemblyNo) {
		this.assemblyNo = assemblyNo;
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
