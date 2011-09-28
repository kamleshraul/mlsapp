package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.Title;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class TitleRepository extends BaseRepository<Title, Long>{

	public Title findByName(String name) {
		Search search = new Search();
		search.addFilterEqual("name", name);
		Title title = this.searchUnique(search);
		return title;
	}
	
	public List<Title> findAllSorted(){
		Search search=new Search();
		search.addSort("name",false);
		return this.search(search);
	}

}
