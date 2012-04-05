/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.BaseRepository.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.mkcl.els.common.util.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.dao.jpa.GenericDAOImpl;
import com.trg.search.Search;
import com.trg.search.jpa.JPASearchProcessor;

/**
 * The Class BaseRepository.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class BaseRepository<T, ID extends Serializable> extends
GenericDAOImpl<T, ID> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.trg.dao.jpa.JPABaseDAO#setEntityManager(javax.persistence.EntityManager
     * )
     */
    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        super.setEntityManager(entityManager);
        entityManager.setFlushMode(FlushModeType.AUTO);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.trg.dao.jpa.JPABaseDAO#setSearchProcessor(com.trg.search.jpa.
     * JPASearchProcessor)
     */
    @Override
    @Autowired
    public void setSearchProcessor(final JPASearchProcessor searchProcessor) {
        super.setSearchProcessor(searchProcessor);
    }

    // =======================towards_generalization----------------------------------//

    /**
     * Find by id.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param id the id
     * @return the u
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> U findById(final Class persistenceClass, final ID id) {
        return (U) _find(persistenceClass, id);
    }

    /**
     * Find by name.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param fieldValue the field value
     * @param locale the locale
     * @return the u
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> U findByName(final Class persistenceClass,
            final String fieldValue, final String locale) {
        final Search search = new Search();
        search.addFilterEqual("name", fieldValue);
        if (locale == null) {
            search.addFilterNull("locale");
        } else if (locale.isEmpty()) {

        } else {
            search.addFilterEqual("locale", locale);
        }
        return (U) this._searchUnique(persistenceClass, search);
    }

    /**
     * Find by field name.
     *
     * @param <U> the generic type
     * @param <V> the value type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param locale the locale
     * @return the u
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T, V> U findByFieldName(
            final Class persistenceClass,
            final String fieldName,
            final V fieldValue,
            final String locale) {
        final Search search = new Search();
        search.addFilterEqual(fieldName, fieldValue);
        if (locale == null) {
            search.addFilterNull("locale");
        } else if (locale.isEmpty()) {

        } else {
            search.addFilterEqual("locale", locale);
        }
        return (U) this._searchUnique(persistenceClass, search);
    }

    /**
     * Find by field names.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param names the names
     * @param locale the locale
     * @return the u
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> U findByFieldNames(final Class persistenceClass,
            final Map<String, String> names, final String locale) {
        final Search search = new Search();
        for (Entry<String, String> i : names.entrySet()) {
            search.addFilterEqual(i.getKey(), i.getValue());
        }
        if (locale == null) {
            search.addFilterNull("locale");
        } else if (locale.isEmpty()) {

        } else {
            search.addFilterEqual("locale", locale);
        }
        return (U) this._searchUnique(persistenceClass, search);
    }

    /**
     * Find all.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> List<U> findAll(final Class persistenceClass,
            final String sortBy, final String sortOrder, final String locale) {
        final Search search = new Search();
        if (sortOrder.toLowerCase().equals(ApplicationConstants.ASC)) {
            search.addSortAsc(sortBy);
        } else {
            search.addSortDesc(sortBy);
        }
        if (locale == null) {
            search.addFilterNull("locale");
        } else if (locale.isEmpty()) {

        } else {
            search.addFilterEqual("locale", locale);
        }
        final List<U> records = this._search(persistenceClass, search);
        return records;
    }


    /**
     * Find all by field name.
     *
     * @param <U> the generic type
     * @param <V> the value type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <U extends T, V> List<U> findAllByFieldName(
            final Class persistenceClass,
            final String fieldName,
            final V fieldValue,
            final String sortBy,
            final String sortOrder,
            final String locale) {
        final Search search = new Search();
        if (sortOrder.toLowerCase().equals(ApplicationConstants.ASC)) {
            search.addSortAsc(sortBy);
        } else {
            search.addSortDesc(sortBy);
        }
        if (locale == null) {
            search.addFilterNull("locale");
        } else if (locale.isEmpty()) {

        } else {
            search.addFilterEqual("locale", locale);
        }
        search.addFilterEqual(fieldName, fieldValue);
        final List<U> records = this._search(persistenceClass, search);
        return records;
    }

    /**
     * Find all by starting with.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param startingWith the starting with
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <U extends T> List<U> findAllByStartingWith(
            final Class persistenceClass,
            final String fieldName,
            final String startingWith,
            final String sortBy,
            final String sortOrder,
            final String locale) {
        final Search search = new Search();
        if (sortOrder.toLowerCase().equals(ApplicationConstants.ASC)) {
            search.addSortAsc(sortBy);
        } else {
            search.addSortDesc(sortBy);
        }
        if (locale == null) {
            search.addFilterNull("locale");
        } else if (locale.isEmpty()) {

        } else {
            search.addFilterEqual("locale", locale);
        }
        search.addFilterILike(fieldName, startingWith);
        final List<U> records = this._search(persistenceClass, search);
        return records;
    }

}
