/*
******************************************************************
File: org.mkcl.els.domain.Field.java
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

// TODO: Auto-generated Javadoc
/**
 * The Class Field.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name="fields")
public class Field {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** The name. */
	@Column(length=50)
	private String name;
	
	/** The detail. */
	@Column(length=100)
	private String detail;
	
	/** The mandatory. */
	@Column(length=50)
	private String mandatory="OPTIONAL";
	
	/** The visible. */
	@Column(length=50)
	private String visible="HIDDEN";
	
	/** The position. */
	private Integer position;
	
	/** The hint. */
	@Column(length=100)
	private String hint;
	
	/** The form. */
	@Column(length=50)
	private String form;
	
	/** The version. */
	@Version
	private Long version;

	/** The locale. */
	@Column(length=5)
	private String locale;	

	/**
	 * Instantiates a new field.
	 */
	public Field() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new field.
	 *
	 * @param name the name
	 * @param detail the detail
	 * @param mandatory the mandatory
	 * @param visible the visible
	 * @param position the position
	 * @param hint the hint
	 * @param form the form
	 * @param version the version
	 * @param locale the locale
	 */
	public Field(String name, String detail, String mandatory, String visible,
			Integer position, String hint, String form, Long version,
			String locale) {
		super();
		this.name = name;
		this.detail = detail;
		this.mandatory = mandatory;
		this.visible = visible;
		this.position = position;
		this.hint = hint;
		this.form = form;
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
	 * Gets the detail.
	 *
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * Sets the detail.
	 *
	 * @param detail the new detail
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * Gets the mandatory.
	 *
	 * @return the mandatory
	 */
	public String getMandatory() {
		return mandatory;
	}

	/**
	 * Sets the mandatory.
	 *
	 * @param mandatory the new mandatory
	 */
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * Gets the visible.
	 *
	 * @return the visible
	 */
	public String getVisible() {
		return visible;
	}

	/**
	 * Sets the visible.
	 *
	 * @param visible the new visible
	 */
	public void setVisible(String visible) {
		this.visible = visible;
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}

	/**
	 * Gets the hint.
	 *
	 * @return the hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * Sets the hint.
	 *
	 * @param hint the new hint
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	/**
	 * Gets the form.
	 *
	 * @return the form
	 */
	public String getForm() {
		return form;
	}

	/**
	 * Sets the form.
	 *
	 * @param form the new form
	 */
	public void setForm(String form) {
		this.form = form;
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
