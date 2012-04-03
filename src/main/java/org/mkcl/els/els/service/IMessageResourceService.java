/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.IMessageResourceService.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service;

import org.mkcl.els.domain.MessageResource;

/**
 * The Interface IMessageResourceService.
 *
 * @author vishals
 * @version v1.0.0
 */
public interface IMessageResourceService extends
        IGenericService<MessageResource, Long> {

    // /**
    // * Find by locale and code.
    // *
    // * @param locale the locale
    // * @param code the code
    // * @return the message resource
    // */
    // public MessageResource findByLocaleAndCode(String locale, String code);
}
