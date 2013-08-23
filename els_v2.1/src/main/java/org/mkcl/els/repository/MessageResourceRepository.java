/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2011 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MessageResourceRepository.java
 * Created On: Dec 20, 2011
 */

package org.mkcl.els.repository;

import java.util.Locale;

import javax.persistence.Query;

import org.mkcl.els.domain.MessageResource;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MessageResourceRepository.
 *
 * @author vishals
 * @since v1.0.0
 */
@Repository
public class MessageResourceRepository extends
        BaseRepository<MessageResource, Long> {

    /**
     * Find by code and locale.
     *
     * @param locale the locale
     * @return the message resource
     */
    public MessageResource findByLocale(final Locale locale) {
    	MessageResource mR = null;
    	
    	try{
	    	
	    	String strQuery="SELECT mr FROM MessageResource mr WHERE mr.locale=:locale";
	    	Query query=this.em().createQuery(strQuery);
	    	query.setParameter("locale", locale.getCountry());
	    	mR = (MessageResource) query.getSingleResult();
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return mR;
    }

    /**
     * Find by locale and code.
     *
     * @param locale the locale
     * @param code the code
     * @return the message resource
     */
    public MessageResource findByLocaleAndCode(final String locale,
                                               final String code) {
    	
    	MessageResource mR = null;
    	try{
	    	String strQuery="SELECT mr FROM MessageResource mr WHERE mr.locale=:locale AND mr.code=:code";
	    	Query query=this.em().createQuery(strQuery);
	    	query.setParameter("locale", locale);
	    	query.setParameter("code", code);
	    	mR = (MessageResource) query.getSingleResult();
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return mR;
    }
}

