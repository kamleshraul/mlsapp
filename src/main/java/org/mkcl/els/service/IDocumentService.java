
package org.mkcl.els.service;

import java.io.IOException;

import org.mkcl.els.domain.Document;


public interface IDocumentService extends IGenericService<Document ,Long>{
	

	public Document save (Document document) throws IOException;
	
	public Document findByTag(String tag);
	
	public void removeByTag(String tag);

}
