/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IGenericService.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * The Interface IGenericService.
 *
 * @param <T> the generic type
 * @param <PK> the generic type
 * @author vishals
 * @version v1.0.0
 */
public interface IGenericService<T, PK extends Serializable> {

    /**
     * Finds all the records.
     *
     * @return the list
     * @author vishals
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    List<T> findAll();

    /**
     * Finds the record by id.
     *
     * @param id the id
     * @return the t
     * @author vishals
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    T findById(PK id);

    /**
     * Checks if the record Exists.
     *
     * @param id the id
     * @return true, if successful
     * @author vishals
     * @since v1.0.0
     */
    @Transactional(readOnly = true)
    boolean exists(PK id);

    /**
     * Creates the new record.
     *
     * @param object the object
     * @return the t
     * @author vishals
     * @since v1.0.0
     */
    @Transactional
    T create(T object);

    /**
     * Updates the existing record.
     *
     * @param object the object
     * @return the t
     * @author vishals
     * @since v1.0.0
     */
    @Transactional
    T update(T object);

    /**
     * Removes the record.
     *
     * @param object the object
     * @author vishals
     * @since v1.0.0 Removes the.
     */
    @Transactional
    void remove(T object);

    /**
     * Removes the record based on id.
     *
     * @param id the id
     * @author vishals
     * @since v1.0.0 Removes the by id.
     */
    @Transactional
    void removeById(PK id);

}
