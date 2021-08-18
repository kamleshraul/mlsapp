/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.domain.BaseDomain.java
 * Created On: Mar 20, 2012
 */
package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.mkcl.els.common.vo.AuthUser;
import org.mkcl.els.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class BaseDomain.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Configurable
@MappedSuperclass
public class BaseDomain {

	// ==================== Attributes ====================
    /** The id. */
	@Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="TABLE_GEN")
    @TableGenerator(name="TABLE_GEN", allocationSize=50)
    private Long id;

    /** The version. */
    @Version
    private Long version;

    /** The locale. */
    @Column(length = 10)
    private String locale;

    /** The base repository. */
    @Autowired
    private transient BaseRepository<BaseDomain, Serializable> baseRepository;
    
    /**
     * Gets the current user.
     *
     * @return the current user
     */
    protected AuthUser getCurrentUser() {
        return (AuthUser) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    }

    // ==================== Constructors ====================
    /**
     * Instantiates a new base domain.
     */
    public BaseDomain() {
        super();
    }

    /**
     * Instantiates a new base domain.
     *
     * @param locale the locale
     */
    public BaseDomain(final String locale) {
        super();
        this.locale = locale;
    }

    /**
     * Gets the base repository.
     *
     * @return the base repository
     */
    public static BaseRepository<BaseDomain, Serializable> getBaseRepository() {
        BaseRepository<BaseDomain, Serializable> baseRepository = new BaseDomain().baseRepository;
        if (baseRepository == null) {
            throw new IllegalStateException(
                    "AssemblyRepository has not been injected in Assembly Domain");
        }
        return baseRepository;
    }

    // ==================== Domain Methods ====================
    /**
     * Persist.
     *
     * @return the base domain
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional
    public BaseDomain persist() {
        baseRepository.save(this);
        baseRepository.flush();
        return this;
    }

    /**
     * Merge.
     *
     * @return the base domain
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional
    public BaseDomain merge() {
        baseRepository.merge(this);
        baseRepository.flush();
        return this;
    }

    /**
     * Removes the.
     *
     * @return true, if successful
     * @author sandeeps
     * @since v1.0.0
     */
    @Transactional
    public boolean remove() {
        return baseRepository.remove(this);
    }

    /**
     * Checks if is duplicate.
     *
     * @param fieldName the field name
     * @param fieldValue the field value
     * @return true, if is duplicate
     */
    @Transactional(readOnly = true)
    public boolean isDuplicate(final String fieldName, final String fieldValue){
        BaseDomain duplicateParameter = null;
        if (this.getLocale().isEmpty()) {
            duplicateParameter = getBaseRepository().findByFieldName(
                    this.getClass(), fieldName, fieldValue, "");
        }
        else {
            duplicateParameter = getBaseRepository().findByFieldName(
                    this.getClass(), fieldName, fieldValue, this.getLocale());
        }
        if (duplicateParameter != null) {
            if (!duplicateParameter.getId().equals(this.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is duplicate.
     *
     * @param names the names
     * @return true, if is duplicate
     */
    @Transactional(readOnly = true)
    public boolean isDuplicate(final Map<String, String> names) {
        BaseDomain duplicateParameter = getBaseRepository().findByFieldNames(
                this.getClass(), names, this.getLocale());
        if (duplicateParameter != null) {
            if (!duplicateParameter.getId().equals(this.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is version mismatch.
     *
     * @return true, if is version mismatch
     */
    @Transactional(readOnly = true)
    public boolean isVersionMismatch() {
        Boolean retVal = false;
        if (this.getId() != null) {
            BaseDomain baseDomain = getBaseRepository().findById(
                    this.getClass(), id);
            retVal = (!baseDomain.getVersion().equals(this.version));
        }
        return retVal;
    }
    
    /**
     * Find by id.
     *
     * @param <T> the generic type
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param id the id
     * @return the t
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
    public static <T extends BaseDomain> T findById(final Class persistenceClass, 
    		final Long id) {
        return (T) getBaseRepository().findById(persistenceClass, id);
    }
    
    @SuppressWarnings({ "rawtypes" })
	@Transactional(readOnly = true)
    public static <T extends BaseDomain> List<T> findAllHavingIdIn(final Class persistenceClass, 
    		final String[] domainIds ) {    	
        return getBaseRepository().findAllHavingIdIn(persistenceClass, domainIds);
    }

    /**
     * Find by name.
     *
     * @param <T> the generic type
     * @param persistenceClass the persistence class
     * @param fieldValue the field value
     * @param locale the locale
     * @return the t
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain> T findByName(final Class persistenceClass, 
    		final String fieldValue,
            final String locale) {
        return (T) getBaseRepository().findByName(persistenceClass, fieldValue,
                locale);
    }

    /**
     * Find by field name.
     *
     * @param <T> the generic type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param locale the locale
     * @return the t
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain> T findByFieldName(
            final Class persistenceClass, 
            final String fieldName,
            final String fieldValue, 
            final String locale){
        return (T) getBaseRepository().findByFieldName(persistenceClass,
                fieldName, fieldValue, locale);
    }

    /**
     * Find by field name.
     *
     * @param <T> the generic type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param locale the locale
     * @return the t
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain,U> T findByFieldName(
            final Class persistenceClass, 
            final String fieldName,
            final U fieldValue, 
            final String locale){
        return (T) getBaseRepository().findByFieldName(persistenceClass,
                fieldName, fieldValue, locale);
    }

    /**
     * Find by field names.
     *
     * @param <T> the generic type
     * @param persistenceClass the persistence class
     * @param names the names
     * @param locale the locale
     * @return the t
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain> T findByFieldNames(
            final Class persistenceClass, 
            final Map<String, String> names,
            final String locale) {
        return (T) getBaseRepository().findByFieldNames(persistenceClass,
                names, locale);
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain> List<T> findAllByFieldNames(
            final Class persistenceClass, 
            final Map<String, String> names, 
            final String sortBy,
            final String sortOrder, 
            final String locale) {
        return getBaseRepository().findAllByFieldNames(persistenceClass, 
        		names, sortBy, sortOrder, locale);
    }

    /**
     * Find all.
     *
     * @param <T> the generic type
     * @param persistenceClass the persistence class
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     * @author sandeeps
     * @since v1.0.0
     */
    @SuppressWarnings({ "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain> List<T> findAll(
            final Class persistenceClass, 
            final String sortBy,
            final String sortOrder, 
            final String locale) {
        return getBaseRepository().findAll(persistenceClass, 
        		sortBy, sortOrder, locale);
    }

    /**
     * Find all by field name.
     *
     * @param <T> the generic type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     */
    @SuppressWarnings({ "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain> List<T> findAllByFieldName(
            final Class persistenceClass, 
            final String fieldName,
            final String fieldValue, 
            final String sortBy,
            final String sortOrder, 
            final String locale){
        return getBaseRepository().findAllByFieldName(persistenceClass,
                fieldName, fieldValue, sortBy, sortOrder, locale);
    }

    @SuppressWarnings({ "rawtypes" })
    @Transactional(readOnly = true)
    public static <T extends BaseDomain,U> List<T> findAllByFieldName(
            final Class persistenceClass, 
            final String fieldName,
            final U fieldValue, 
            final String sortBy,
            final String sortOrder, 
            final String locale) {
        return getBaseRepository().findAllByFieldName(persistenceClass,
                fieldName, fieldValue, sortBy, sortOrder, locale);
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Transactional(readOnly = true)
    public static <U extends BaseDomain> List<U> findAllByStartingWith(
            final Class persistenceClass, 
            final String fieldName,
            final String startingWith, 
            final String sortBy, 
            final String sortOrder, 
            final String locale){
        return getBaseRepository().findAllByStartingWith(persistenceClass, 
        		fieldName, startingWith, sortBy, sortOrder, locale);
    }

	public static <U extends BaseDomain> List<U> findAllByLikeParameter(
            final Class<?> persistenceClass, 
            final String[] fields,
            final String term,
            final String locale) {
		return  getBaseRepository().findAllByLikeParameter(persistenceClass,
				fields, term, locale);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
    public static <T extends BaseDomain> T findByIdNext(final Class persistenceClass, 
    		final Long id,final String searchBy,final String locale) {
		List<T> result = getBaseRepository().findByPagination(persistenceClass, searchBy, id, searchBy, "asc", locale, 1,"next");
        return  result!=null && result.size()>0?(T)(result.get(0)):null;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
    public static <T extends BaseDomain> T findByIdPrev(final Class persistenceClass, 
    		final Long id,final String searchBy,final String locale) {
		List<T> result = getBaseRepository().findByPagination(persistenceClass, searchBy, id, searchBy, "asc", locale, 1,"prev");
        return  result!=null && result.size()>0?(T)(result.get(0)):null;
    }
	
    // ==================== Getters & Setters ====================
    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(final Long version) {
        this.version = version;
    }

    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }
    
}