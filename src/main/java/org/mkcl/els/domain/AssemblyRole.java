/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.AssemblyRole.java
 * Created On: Mar 8, 2012
 */

package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.mkcl.els.repository.AssemblyRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class AssemblyRole.
 *
 * @author nileshp
 * @since v1.0.0
 */
@Configurable
@Entity
@Table(name = "assembly_roles")
public class AssemblyRole extends BaseDomain implements Serializable {

    // ---------------------------------Attributes------------------------------------------
    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /** The name. */
    @Column(length = 100, nullable = false)
    @NotEmpty
    private String name;

    /** The assembly role repository. */
    @Autowired
    private transient AssemblyRoleRepository assemblyRoleRepository;

    // ---------------------------------Constructors----------------------------------------------
    /**
     * Instantiates a new assembly role.
     */
    public AssemblyRole() {
        super();
    }

    /**
     * Instantiates a new assembly role.
     *
     * @param name the name
     */
    public AssemblyRole(final String name) {
        super();
        this.name = name;
    }

    // -------------------------------Domain_Methods----------------------------------------------

    /**
     * Gets the assembly role repository.
     *
     * @return the assembly role repository
     */
    public static AssemblyRoleRepository getAssemblyRoleRepository() {
        AssemblyRoleRepository assemblyRoleRepository = new AssemblyRole().assemblyRoleRepository;
        if (assemblyRoleRepository == null) {
            throw new IllegalStateException(
                    "AssemblyRole Repository has not been injected "
                            + "in AssemblyRole domain");
        }
        return assemblyRoleRepository;
    }

    /**
     * Find unassigned roles.
     *
     * @param locale the locale
     * @param memberId the member id
     * @return the list
     * @author nileshp
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    public static List<AssemblyRole> findUnassignedRoles(final String locale,
                                                         final Long memberId) {
        return getAssemblyRoleRepository()
                .findUnassignedRoles(locale, memberId);
    }

    // ------------------------------------------Getters/Setters-----------------------------------
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
    public void setName(final String name) {
        this.name = name;
    }
}
