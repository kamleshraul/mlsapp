/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2013 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IQuestionService.java
 * Created On: Jan 10, 2013
 */
package org.mkcl.els.service;

import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Interface IQuestionService.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
public interface IQuestionService {

    /**
     * Find supporting members.
     *
     * @param strQuestionId the str question id
     * @return the list
     */
    public List<String> findSupportingMembers(final String strQuestionId);

    /**
     * Find email by username.
     *
     * @param username the username
     * @return the string
     */
    public String findEmailByUsername(final String username);

    /**
     * Find by locale and code.
     *
     * @param locale the locale
     * @param code the code
     * @return the string
     */
    public String findByLocaleAndCode(final String locale,final String code);


}
