package org.mkcl.els.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="fields")
public class Field {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length=50)
	private String name;
	
	@Column(length=100)
	private String detail;
	
	@Column(length=50)
	private String mandatory="OPTIONAL";
	
	@Column(length=50)
	private String visible="HIDDEN";
	
	private Integer position;
	
	@Column(length=50)
	private String form;
	
	@Version
	private Long version;

	/** The locale. */
	@Column(length=5)
	private String locale;	

	public Field() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Field(Long id, String name, String detail, String mandatory,
			String visible, Integer position, String form, Long version,
			String locale) {
		super();
		this.id = id;
		this.name = name;
		this.detail = detail;
		this.mandatory = mandatory;
		this.visible = visible;
		this.position = position;
		this.form = form;
		this.version = version;
		this.locale = locale;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}


	public Integer getPosition() {
		return position;
	}


	public void setPosition(Integer position) {
		this.position = position;
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

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}
	
	

}
