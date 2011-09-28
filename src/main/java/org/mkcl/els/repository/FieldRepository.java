package org.mkcl.els.repository;

import org.mkcl.els.domain.Field;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class FieldRepository extends BaseRepository<Field, Long>{

	public Field findByName(String name){
		Search search = new Search();
		search.addFilterEqual("name", name);
		Field field = this.searchUnique(search);
		return field;
	}
}
