package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "masters_relations")
public class Relation extends BaseDomain implements Serializable{
	
	// ---------------------------------Attributes------------------------------------------
		/** The Constant serialVersionUID. */
		private static final transient long serialVersionUID = 1L;

		/** The reservation_type. */
		@Column(length = 150, nullable = false)
		@NotEmpty
		private String name;

		// ---------------------------------Constructors----------------------------------------------

		public Relation() {
			super();
		}
		public Relation(final String name) {
			super();
			this.name = name;
		}
		// -------------------------------Domain_Methods----------------------------------------------
		// ------------------------------------------Getters/Setters-----------------------------------
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
}
