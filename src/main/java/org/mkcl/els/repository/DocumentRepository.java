

package org.mkcl.els.repository;


import org.mkcl.els.domain.Document;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;


@Repository
public class DocumentRepository extends BaseRepository<Document, Long>{
	
	
	public Document findByTag(String tag) {
		return this.searchUnique(new Search().addFilterEqual("tag",tag));
	}

	public void removeByTag(String tag) {
		Document document=findByTag(tag);
		remove(document);
	}

}
