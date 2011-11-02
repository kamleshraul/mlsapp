package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.Field;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class FieldRepository extends BaseRepository<Field, Long>{

	public Field findByName(String name){
		Search search = new Search();
		search.addFilterEqual("form", name);
		Field field = this.searchUnique(search);
		return field;
	}

	public List<Field> findByFormNameSorted(String formName) {
		Search search = new Search();
		search.addFilterEqual("form", formName);
		search.addSort("position",true);
		return this.search(search);
	}
}
