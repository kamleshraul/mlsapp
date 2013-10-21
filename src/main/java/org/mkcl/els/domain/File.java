/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.Act.java
 * Created On: June 20, 2013
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.mkcl.els.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class File.
 * 
 * @author dhananjayb
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "files")
public class File extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------//
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
	@Column(length = 100)
	private String name;   
	
	/** The value. */
	@Column(length = 100)
	private String value; 
	
	/** The file repository. */
	@Autowired
	private transient FileRepository fileRepository;
	
    // ---------------------------------Constructors----------------------//
    /**
     * Instantiates a new act.
     */
    public File() {
        super();
    }

    // ----------------------------Domain Methods-------------------------//
    /**
	 * Gets the file repository.
	 *
	 * @return the file repository
	 */
	public static FileRepository getFileRepository() {
		FileRepository fileRepository = new File().fileRepository;
		if (fileRepository == null) {
			throw new IllegalStateException(
					"FileRepository has not been injected in Act Domain");
		}
		return fileRepository;
	}
	
	// ----------------------------Getters/Setters------------------------//
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
    
}
