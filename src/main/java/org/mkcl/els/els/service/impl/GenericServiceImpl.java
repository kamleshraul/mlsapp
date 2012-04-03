/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.GenericServiceImpl.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service.impl;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.common.exception.RecordNotFoundException;
import org.mkcl.els.service.IGenericService;

import com.trg.dao.jpa.GenericDAO;

/**
 * The Class GenericServiceImpl.
 *
 * @param <T> the generic type
 * @param <PK> the generic type
 * @author vishals
 * @version v1.0.0
 */
public class GenericServiceImpl<T, PK extends Serializable> implements
        IGenericService<T, PK> {

    /** The dao. */
    protected GenericDAO<T, PK> dao;

    /**
     * Instantiates a new generic service impl.
     */
    protected GenericServiceImpl() {
    }

    /**
     * Instantiates a new generic service impl.
     *
     * @param genericDao the generic dao
     */
    protected GenericServiceImpl(final GenericDAO<T, PK> genericDao) {
        this.dao = genericDao;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mkcl.els.service.IGenericService#findAll()
     */
    @Override
    public List<T> findAll() {
        return dao.findAll();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mkcl.els.service.IGenericService#findById(java.io.Serializable)
     */
    @Override
    public T findById(final PK id) {
        T entity = dao.find(id);
        if (entity == null) {
            throw new RecordNotFoundException(
                    "Error: Record was not found for Entity with Id:" + id);
        }
        return entity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mkcl.els.service.IGenericService#exists(java.io.Serializable)
     */
    @Override
    public boolean exists(final PK id) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mkcl.els.service.IGenericService#create(java.lang.Object)
     */
    @Override
    public T create(final T object) {
        // T saved_object = dao.save(object);
        // dao.flush();
        return dao.save(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mkcl.els.service.IGenericService#update(java.lang.Object)
     */
    @Override
    public T update(final T object) {
        return dao.merge(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mkcl.els.service.IGenericService#remove(java.lang.Object)
     */
    @Override
    public void remove(final T object) {
        dao.remove(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mkcl.els.service.IGenericService#removeById(java.io.Serializable)
     */
    @Override
    public void removeById(final PK id) {
        dao.removeById(id);
    }

}
