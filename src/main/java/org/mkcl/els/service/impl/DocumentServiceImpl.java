
package org.mkcl.els.service.impl;

import java.io.IOException;

import org.mkcl.els.domain.Document;
import org.mkcl.els.repository.DocumentRepository;
import org.mkcl.els.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DocumentServiceImpl extends GenericServiceImpl<Document,Long> implements IDocumentService {
	@Autowired
	DocumentRepository documentRepository;
	
	@Override
	@Transactional
	public Document save(Document document) throws IOException {
		return documentRepository.save(document);
	}

	@Override
	public Document findByTag(String tag) {
		return documentRepository.findByTag(tag);
	}

	@Override
	public void removeByTag(String tag) {
		documentRepository.removeByTag(tag);
	}


}
