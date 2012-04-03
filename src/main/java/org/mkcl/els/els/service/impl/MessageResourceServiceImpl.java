/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.MessageResourceServiceImpl.java
 * Created On: Mar 8, 2012
 */
package org.mkcl.els.service.impl;

import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.service.IMessageResourceService;
import org.springframework.stereotype.Service;

/**
 * The Class MessageResourceServiceImpl.
 *
 * @author vishals
 * @version v1.0.0
 */
@Service
public class MessageResourceServiceImpl extends
        GenericServiceImpl<MessageResource, Long> implements
        IMessageResourceService {

    // /** The repository. */
    // private MessageResourceRepository messageResourceRepository;
    //
    // /**
    // * Sets the message resource repository.
    // *
    // * @param messageResourceRepository the new message resource repository
    // */
    // @Autowired
    // public void setMessageResourceRepository(final MessageResourceRepository
    // messageResourceRepository) {
    // this.dao = messageResourceRepository;
    // this.messageResourceRepository = messageResourceRepository;
    // }
    //
    //
    // /**
    // * Find a messageResource based on locale & code
    // *
    // * @param locale the locale
    // * @param code the code
    // */
    // @Override
    // public MessageResource findByLocaleAndCode(final String locale, final
    // String code) {
    // return messageResourceRepository.findByLocaleAndCode(locale, code);
    // }

}
