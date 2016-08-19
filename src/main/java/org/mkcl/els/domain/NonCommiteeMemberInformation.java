package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name="noncommiteemember_informations")
public class NonCommiteeMemberInformation extends BaseDomain implements Serializable {

	private static final long serialVersionUID = -5990941940288588067L;
	
	@Column(length=3000)
	private String name;
	
	
	
	public NonCommiteeMemberInformation() {
		super();
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	
}
