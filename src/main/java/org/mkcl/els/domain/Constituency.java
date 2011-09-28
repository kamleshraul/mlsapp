/*
******************************************************************
File: org.mkcl.els.domain.Constituency.java
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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;

// TODO: Auto-generated Javadoc
/**
 * The Class Constituency.
 *
 * @author sandeeps
 * @version v1.0.0
 */

@Entity
@Table(name="constituencies")
public class Constituency {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The name. */
	@Column(length=100, nullable=false)
	@NotEmpty
	private String name;
	
	   /** The constituency number. */
    @Column(length=100)
    private String number;
	
	
	/** The districts. */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="constituency_district",
            joinColumns=
            @JoinColumn(name="constituency_id", referencedColumnName="id"),
      inverseJoinColumns=
            @JoinColumn(name="district_id", referencedColumnName="id")
    )
    @NotEmpty
    private Set<District> districts; 
    
    /** The reserved. */
    private boolean reserved=false;    
	
	/** The version. */
	@Version
	private Long version;
	
	/** The locale. */
	@Column(length=5)
	private String locale;    

	

	/**
	 * Instantiates a new constituency.
	 */
	public Constituency() {
		super();
	}

	

	/**
	 * Instantiates a new constituency.
	 *
	 * @param name the name
	 * @param number the number
	 * @param state the state
	 * @param districts the districts
	 * @param reserved the reserved
	 * @param version the version
	 * @param locale the locale
	 */
	public Constituency(String name, String number, State state,
			Set<District> districts, boolean reserved, Long version,
			String locale) {
		super();
		this.name = name;
		this.number = number;
		this.districts = districts;
		this.reserved = reserved;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Gets the districts.
	 *
	 * @return the districts
	 */
	public Set<District> getDistricts() {
		return districts;
	}

	/**
	 * Sets the districts.
	 *
	 * @param districts the new districts
	 */
	public void setDistricts(Set<District> districts) {
		this.districts = districts;
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

	/**
	 * Gets the constituency number.
	 *
	 * @return the constituency number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the constituency number.
	 *
	 * @param number the new number
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Checks if is reserved.
	 *
	 * @return true, if is reserved
	 */
	public boolean isReserved() {
		return reserved;
	}

	/**
	 * Sets the reserved.
	 *
	 * @param reserved the new reserved
	 */
	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}
	
}

