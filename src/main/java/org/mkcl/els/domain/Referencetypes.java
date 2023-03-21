package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "referencetypes")
public class Referencetypes extends BaseDomain implements Serializable {
	
	 private static final transient long serialVersionUID = 1L;
	 
	 @Column(length = 50)
	 private String name;
	
	 @Column(length = 50)
	 private String display_name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	 
}
