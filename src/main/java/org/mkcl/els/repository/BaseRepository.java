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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /** The Constant logger. */
    protected static final Logger logger = LoggerFactory
            .getLogger(BaseRepository.class);
   
    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        super.setEntityManager(entityManager);
        entityManager.setFlushMode(FlushModeType.AUTO);
    }

    @Override
    @Autowired
    public void setSearchProcessor(final JPASearchProcessor searchProcessor) {
        super.setSearchProcessor(searchProcessor);
    }

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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <U extends T> List<U> findAllHavingIdIn(final Class persistenceClass, final String[] domainIds) {
        List<U> domainObjects = new ArrayList<U>();
        for(String i: domainIds) {
        	U domainObj = (U) _find(persistenceClass, Long.parseLong(i));
        	domainObjects.add(domainObj);
        }
    	return domainObjects;
    }

    /**
     * Find by name.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param fieldValue the field value
     * @param locale the locale
     * @return the u
     * @throws ELSException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> U findByName(final Class persistenceClass,
            final String fieldValue, 
            final String locale)  {
    	StringBuffer strQuery = new StringBuffer("SELECT t FROM " + 
    			persistenceClass.getSimpleName() + " t" +
    			" WHERE t.name=:name");
    	
    	if (locale == null) {
    		strQuery.append(" AND locale is NULL");
        }
    	else if (! locale.isEmpty()) {
    		strQuery.append(" AND locale=:locale");
        }
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	jpQuery.setParameter("name", fieldValue);
    	
    	if (locale != null && ! locale.isEmpty()) {
    		jpQuery.setParameter("locale", locale);
        }
    	
    	U result = null;
    	try {
    		result = (U) jpQuery.getSingleResult();
    	}
    	catch (Exception e) {
    		logger.warn("BaseRepository: findByName(): " +
    			"Common exception thrown when getSingleResult() is invoked on javax.persistence.Query");
		}
    	
    	return result;  	
    }

    /**
     * Find by field name.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param locale the locale
     * @return the u
         * @throws ELSException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T,V> U findByFieldName(final Class persistenceClass,
            final String fieldName, final V fieldValue, final String locale) {
    	StringBuffer strQuery = new StringBuffer("SELECT t FROM " + 
    			persistenceClass.getSimpleName() + " t" +
    			" WHERE t." + fieldName + "=:fieldValue");
    	
    	if (locale == null) {
    		strQuery.append(" AND locale=" + null);
        }
    	else if (! locale.isEmpty()) {
    		strQuery.append(" AND locale=:locale");
        }
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	jpQuery.setParameter("fieldValue", fieldValue);
    	
    	if (locale != null && !locale.isEmpty()) {
    		jpQuery.setParameter("locale", locale);
        }
    	
    	U result = null;
    	try {
    		result = (U) jpQuery.getSingleResult();
    	}
    	catch (Exception e) {
    		logger.warn("BaseRepository: findByFieldName(): " +
				"Common exception thrown when getSingleResult() is invoked on javax.persistence.Query");
		}
    	
    	return result;
    }

    /**
     * Find by field names.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param names the names
     * @param locale the locale
     * @return the u
     * @throws ELSException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> U findByFieldNames(final Class persistenceClass,
            final Map<String, String> names, final String locale) {
    	StringBuffer strQuery = new StringBuffer("SELECT DISTINCT t FROM " + 
    			persistenceClass.getSimpleName() + " t WHERE");
    	
    	int index = 0; 
    	for (Entry<String, String> i : names.entrySet()) {
    		if(i.getValue() != null) {
    			strQuery.append(" t." + i.getKey() + "=:" + i.getKey());
    		} 
    		else {
    			strQuery.append(" t." + i.getKey() + " IS NULL");
    		}
    		
            if(index < (names.entrySet().size() - 1)){
            	strQuery.append(" AND");	            	
            }
            index++;
        }
    	
    	if (locale == null) {
    		strQuery.append(" AND locale IS NULL");
        }
    	else if (! locale.isEmpty()) {
        	strQuery.append(" AND locale=:locale");
        }
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	for (Entry<String, String> i : names.entrySet()) {
    		if(i.getValue()!=null) {
    			jpQuery.setParameter(i.getKey(), i.getValue());
    		}          
        }    	
    	if (locale != null && ! locale.isEmpty()) {
    		jpQuery.setParameter("locale", locale);
        }
    	
    	U result = null;
    	try {
    		result = (U) jpQuery.getSingleResult();
    	}
    	catch (Exception e) {
    		logger.warn("BaseRepository: findByFieldNames(): " +
				"Common exception thrown when getSingleResult() is invoked on javax.persistence.Query");
		}
    	
    	return result;
    }
    
    // TODO
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> List<U> findAllByFieldNames(final Class persistenceClass,
            final Map<String, String> names, 
            final String sortBy, 
            final String sortOrder, 
            final String locale) {
        
    	final Search search = new Search();
        for (Entry<String, String> i : names.entrySet()) {
            search.addFilterEqual(i.getKey(), i.getValue());
        }
        if (locale == null) {
            search.addFilterNull("locale");
        }
        else if (! locale.isEmpty()) {
        	search.addFilterEqual("locale", locale);
        }
        
        if (sortBy!=null && !sortBy.isEmpty() 
        		&& sortOrder != null && !sortOrder.isEmpty()) {
        	if(sortOrder.equalsIgnoreCase(ApplicationConstants.DESC)) {
        		search.addSortDesc(sortBy);
        	} else {
        		search.addSortAsc(sortBy);
        	}
        }
        
        final List<U> records = this._search(persistenceClass, search);
        return records;
    	
        // Test the following commented code and if it works as desired
        // then replace the above uncommented code with the following
        // commented code.
    	/*
    	StringBuffer strQuery = new StringBuffer("SELECT DISTINCT t FROM " + 
    			persistenceClass.getSimpleName() + " t WHERE");
    	
    	int index = 0; 
    	for (Entry<String, String> i : names.entrySet()) {
    		if(i.getValue() != null) {
    			strQuery.append(" t." + i.getKey() + "=:" + i.getKey());
    		} 
    		else {
    			strQuery.append(" t." + i.getKey() + " IS NULL");
    		}
    		
            if(index < (names.entrySet().size() - 1)){
            	strQuery.append(" AND");	            	
            }
            index++;
        }
    	
    	if (locale == null) {
    		strQuery.append(" AND locale IS NULL");
        }
    	else if (! locale.isEmpty()) {
        	strQuery.append(" AND locale=:locale");
        }
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	for (Entry<String, String> i : names.entrySet()) {
    		if(i.getValue()!=null) {
    			jpQuery.setParameter(i.getKey(), i.getValue());
    		}          
        }    	
    	if (locale != null && ! locale.isEmpty()) {
    		jpQuery.setParameter("locale", locale);
        }
    	
    	List<U> result = jpQuery.getResultList();
		return result;
		*/
    }

     /**
     * Find all by field name.
     *
     * @param <U> the generic type
     * @param persistenceClass the persistence class
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param locale the locale
     * @return the list
     * @throws ELSException 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public <U extends T,V> List<U> findAllByFieldName(
            final Class persistenceClass, 
            final String fieldName, 
            final V fieldValue,
            final String sortBy, 
            final String sortOrder, 
            final String locale){
    	StringBuffer strQuery = new StringBuffer("SELECT t FROM " + 
    			persistenceClass.getSimpleName() + 
				" t WHERE t." + fieldName + "=:" + fieldName);
		
    	if (locale == null) {
			strQuery.append(" AND locale=:locale");
		}
    	else if (! locale.isEmpty()) {
    		strQuery.append(" AND locale=:locale");
		}
		strQuery.append(" ORDER BY t." + sortBy + " " + sortOrder);

		Query jpQuery = this.em().createQuery(strQuery.toString());
		jpQuery.setParameter(fieldName, fieldValue);
		if (locale == null) {
			jpQuery.setParameter("locale", null);
		}
		else if (! locale.isEmpty()) {
			jpQuery.setParameter("locale", locale);
		}
		
		List<U> result = jpQuery.getResultList();
		return result;
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
     * @throws ELSException 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public <U extends T> List<U> findAllByStartingWith(
            final Class persistenceClass, final String fieldName,
            final String startingWith, final String sortBy, 
            final String sortOrder, final String locale) {
		StringBuffer strQuery = new StringBuffer("SELECT t FROM " + 
				persistenceClass.getSimpleName() + 
				" t WHERE t." + fieldName + " LIKE :pattern");
		
		if (locale == null) {
			strQuery.append(" AND locale=:locale");
		}
		else if (! locale.isEmpty()) {
			strQuery.append(" AND locale=:locale");
		}
		
		strQuery.append(" ORDER BY t." + sortBy + " " + sortOrder);

		Query jpQuery = this.em().createQuery(strQuery.toString());
		jpQuery.setParameter("pattern", startingWith + "%");
		if (locale == null) {
			jpQuery.setParameter("locale", null);
		}
		else if (! locale.isEmpty()) {
			jpQuery.setParameter("locale", locale);
		}
		
		List<U> result = jpQuery.getResultList();
		return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <U extends T> List<U> findAllByLikeParameter(
            final Class persistenceClass, final String[] fields,
            final String term,final String locale) {
		StringBuffer strQuery = new StringBuffer("SELECT t FROM " + 
				persistenceClass.getSimpleName() + 
				" t WHERE ");
		
		int fieldsLength = fields.length;
		for(int i = 0; i < fieldsLength; i++){
			strQuery.append(fields[i] + " LIKE :pattern");
			if(i + 1 < fieldsLength){
				strQuery.append(" OR ");
			}
		}
		
		if (locale == null) {
			strQuery.append(" AND locale=:locale");
		}
		else if (! locale.isEmpty()) {
			strQuery.append(" AND locale=:locale");
		}
		
		Query jpQuery = this.em().createQuery(strQuery.toString());
		jpQuery.setParameter("pattern", term + "%");
		jpQuery.setParameter("locale",locale);
		
		List<U> result = jpQuery.getResultList();
		return result;
			
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
     * @throws ELSException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <U extends T> List<U> findAll(final Class persistenceClass,
            final String sortBy, final String sortOrder, final String locale) {
    	StringBuffer strQuery = new StringBuffer("SELECT DISTINCT t FROM " + 
    			persistenceClass.getSimpleName() + " t");
    	
    	if (locale == null) {
    		strQuery.append(" WHERE locale=:locale");
        }
    	else if (! locale.isEmpty()) {
    		strQuery.append(" WHERE locale=:locale");
        }
    	strQuery.append(" ORDER BY t." + sortBy + " " + sortOrder);
    	
    	Query jpQuery = this.em().createQuery(strQuery.toString());
    	if (locale == null) {
    		jpQuery.setParameter("locale", null);
        }
    	else if (! locale.isEmpty()) {
    		jpQuery.setParameter("locale", locale);
        }
    	
    	List<U> result = jpQuery.getResultList();
    	return result;
    }

}
