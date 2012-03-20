/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.ElectionType.java
 * Created On: Mar 15, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;

// TODO: Auto-generated Javadoc
/**
 * The Class ElectionType.
 *
 * @author Anand
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "masters_electionTypes")
public class ElectionType extends BaseDomain implements Serializable{
	
	// ---------------------------------Attributes-------------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;
    
    /** The type. */
    @Column(length = 150)
    @NotEmpty
    private String electionType;

   
    /** The assemblycounciltype. */
    @ManyToOne
    private HouseType houseType;

    /** The election type repository. */
//    @Autowired
//    private static transient ElectionTypeRepository electionTypeRepository;

// ---------------------------------Constructors----------------------------------------------
    /**
 * Instantiates a new election type.
 */
public ElectionType() {
		super();
	}

	
	/**
	 * Instantiates a new election type.
	 *
	 * @param electionType the election type
	 * @param assemblycounciltype the assemblycounciltype
	 */
	public ElectionType(final String electionType,
						final HouseType houseType) {
	super();
	this.electionType = electionType;
	this.houseType = houseType;
}


	// -------------------------------Domain_Methods----------------------------------------------
	
//	/**
//	 * Gets the election type repository.
//	 *
//	 * @return the election type repository
//	 */
//	public static ElectionTypeRepository getElectionTypeRepository() {
//		ElectionTypeRepository electionTypeRepository = new ElectionType().electionTypeRepository;
//        if (electionTypeRepository == null) {
//            throw new IllegalStateException(
//                    "ElectionTypeRepository has not been injected in ElectionType Domain");
//        }
//        return electionTypeRepository;
//    }
//	
//	 /**
// 	 * Find election type by assembly council type id.
// 	 *
// 	 * @param assemblyCouncilTypeid the assembly council typeid
// 	 * @param sortBy the sort by
// 	 * @param sortOrder the sort order
// 	 * @return the list
// 	 * @author compaq
// 	 * @since v1.0.0
// 	 */
// 	@Transactional(readOnly = true)
//	    public static List<ElectionType> findElectionTypeByAssemblyCouncilTypeId(
//	   final Long assemblyCouncilTypeid, final String sortBy, final String sortOrder) {
//	        return getElectionTypeRepository().findElectionTypeByAssemblyCouncilTypeId(
//	                assemblyCouncilTypeid, sortBy, sortOrder);
//	    }
//	 
//	 /**
// 	 * Find election type by assembly council type.
// 	 *
// 	 * @param type the type
// 	 * @param orderBy the order by
// 	 * @param sortOrder the sort order
// 	 * @return the list
// 	 * @author compaq
// 	 * @since v1.0.0
// 	 */
// 	@Transactional(readOnly = true)
//	    public static List<ElectionType> findElectionTypeByAssemblyCouncilType(
//	    		final String type, final String orderBy, final String sortOrder) {
//	        return getElectionTypeRepository().findElectionTypeByAssemblyCouncilType(
//	                type, orderBy, sortOrder);
//	    }
	 // ------------------------------------------Getters/Setters-------------------------------

	
	/**
	 * Gets the election type.
	 *
	 * @return the election type
	 */
	public String getElectionType() {
		return electionType;
	}


	public HouseType getHouseType() {
		return houseType;
	}


	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}


	/**
	 * Sets the election type.
	 *
	 * @param electionType the new election type
	 */
	public void setElectionType(final String electionType) {
		this.electionType = electionType;
	}


	
}
