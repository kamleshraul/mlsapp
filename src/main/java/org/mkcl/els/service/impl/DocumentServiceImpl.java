/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.service.impl.DocumentServiceImpl.java
 * Created On: Mar 8, 2012
 */

package org.mkcl.els.service.impl;

import org.mkcl.els.domain.Document;
import org.mkcl.els.service.IDocumentService;
import org.springframework.stereotype.Service;

/**
 * The Class DocumentServiceImpl.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Service
public class DocumentServiceImpl extends GenericServiceImpl<Document, Long>
        implements IDocumentService {

    // /** The document repository. */
    // @Autowired
    // DocumentRepository documentRepository;
    //
    // /* (non-Javadoc)
    // * @see
    // org.mkcl.els.service.IDocumentService#save(org.mkcl.els.domain.Document)
    // */
    // @Override
    // @Transactional
    // public Document save(final Document document) throws IOException {
    // return documentRepository.save(document);
    // }
    //
    // /* (non-Javadoc)
    // * @see org.mkcl.els.service.IDocumentService#findByTag(java.lang.String)
    // */
    // @Override
    // public Document findByTag(String tag) {
    // return documentRepository.findByTag(tag);
    // }
    //
    // /* (non-Javadoc)
    // * @see
    // org.mkcl.els.service.IDocumentService#removeByTag(java.lang.String)
    // */
    // @Override
    // public void removeByTag(String tag) {
    // documentRepository.removeByTag(tag);
    // }

}
