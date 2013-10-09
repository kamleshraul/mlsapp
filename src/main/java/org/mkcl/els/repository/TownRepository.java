package org.mkcl.els.repository;

import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Town;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class TownRepository extends BaseRepository<Town, Long> {

	public Town find(final String name, 
			final District district, 
			final String locale) {
		Search search = new Search();
		search.addFilterEqual("name", name);
		search.addFilterEqual("district", district);
		search.addFilterEqual("locale", locale);
		Town town = this.searchUnique(search);
		return town;
	}
	
}