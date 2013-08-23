/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.DocumentRepository.java
 * Created On: Jan 5, 2012
 */
package org.mkcl.els.repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.Document;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class DocumentRepository.
 * @author amitd
 * @author sandeeps
 * @version v1.0.0
 */
@Repository
public class DocumentRepository extends BaseRepository<Document, Long> {

    /**
     * Find by tag.
     *
     * @param tag the tag
     * @return the document
     * @author sujitas
     * @since v1.0.0
     */
    public Document findByTag(final String tag) throws ELSException{
    	String strquery="SELECT d FROM Document d WHERE d.tag=:tag";
    	Document document = null;
    	try{
    		Query query=this.em().createQuery(strquery,Document.class);
    		query.setParameter("tag", tag);
    		query.getSingleResult();
    		document = (Document)query.getSingleResult();
    	}catch(Exception e){
    		e.printStackTrace();
    		logger.error(e.getMessage());
    		ELSException elsException = new ELSException();
    		elsException.setParameter("DocumentRepository_Document_findByTag", "No document found.");
    		throw elsException;
    	}
    			
    	return document;
    }

}
