package org.mkcl.els.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "day_working_scope")
public class DayWorkingScope extends BaseDomain implements Serializable {

	// ---------------------------------Attributes------------------------//
	/** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150)
    private String type;

    /** The name. */
    @Column(length=600)
    private String name;
    
    private Integer priority;

    // ---------------------------------Constructors-----------------------------------------------
	public DayWorkingScope() {
		super();
	}

	// --------------------------------Getters & Setters--------------------------------------------
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}	

}