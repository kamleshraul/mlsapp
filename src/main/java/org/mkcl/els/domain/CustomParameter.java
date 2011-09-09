/*
******************************************************************
File: org.mkcl.els.domain.CustomParameter.java
Copyright (c) 2011, vishals, MKCL
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
 * The Class CustomParameter.
 *
 * @author vishals
 * @version v1.0.0
 */
@Entity
@Table(name="custom_parameters")
public class CustomParameter implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	// Attributes --------------------------------------------------------------------------------------------------------------------
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The name. */
	@Column(length=100)
	@NotEmpty
	private String name;
	
	/** The value. */
	@Column(length=500)
	@NotEmpty
	private String value;

	/** The updateable. */
	private Boolean updateable;
	
	/** The description. */
	@Column(length=2000)
	private String description;
	
	/** The version. */
    @Version
    private Long version;

    // Constructors -----------------------------------------
	/**
     * Instantiates a new custom parameter.
     */
    public CustomParameter(){
		
	}
	
	/**
	 * Instantiates a new custom parameter.
	 *
	 * @param name the name
	 * @param value the value
	 * @param updateable the updateable
	 * @param description the description
	 */
	public CustomParameter(String name, String value, Boolean updateable,
			String description) {
		super();
		this.name = name;
		this.value = value;
		this.updateable = updateable;
		this.description = description;
	}
	
	// Getters/Setters -------------------------------------
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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the updateable.
	 *
	 * @return the updateable
	 */
	public Boolean getUpdateable() {
		return updateable;
	}

	/**
	 * Sets the updateable.
	 *
	 * @param updateable the new updateable
	 */
	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
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
