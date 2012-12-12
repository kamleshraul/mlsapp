package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mkcl.els.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
@Table(name = "status")
public class Status extends BaseDomain implements Serializable{
	// ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private transient static final long serialVersionUID = 1L;

    /** The type. */
    @Column(length = 150)
    private String type;

    @Column(length=600)
    private String name;

    @Autowired
    private transient StatusRepository statusRepository;
 // ---------------------------------Constructors----------------------------------------------

	public Status() {
		super();
	}

	public Status(final String type, final String name) {
		super();
		this.type = type;
		this.name = name;
	}
	// -------------------------------Domain_Methods----------------------------------------------
	public static StatusRepository getStatusRepository() {
	    StatusRepository statusRepository = new Status().statusRepository;
        if (statusRepository == null) {
            throw new IllegalStateException(
                    "StatusRepository has not been injected in Status Domain");
        }
        return statusRepository;
    }

    public static List<Status> findStartingWith(final String pattern,final String sortBy,final String sortOrder,final String locale){
        return getStatusRepository().findStartingWith(pattern,sortBy,sortOrder,locale);
    }

    public static Status findByType(String typeName, String locale) {
		return Status.findByFieldName(Status.class, "type", typeName, locale);
	}
    // ------------------------------------------Getters/Setters-----------------------------------
	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
